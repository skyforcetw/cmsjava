package auo.cms.hsv.autotune;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.cms.profile.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.math.Interpolation;
import shu.cms.devicemodel.lcd.MultiMatrixModel;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ProfileColorSpaceUtils {
//  public final static sRGBTuneTarget getsRGBTuneTargetInstance(sRGBTuneTarget.
//      Patches patches, LCDTarget test729Target) {
//    ProfileColorSpace pcs = getProfileColorSpaceFrom729Target(test729Target);
//    sRGBTuneTarget target = new sRGBTuneTarget(patches, pcs);
//    return target;
//  }

  /**
   * LCDTarget轉PreferredColorSpace
   * @param standardColorSpace ColorSpace
   * @param preferredTest729Target LCDTarget
   * @param percent double
   * @param whiteXYZ CIEXYZ
   * @return PreferredColorSpace
   * @todo percent要做負的
   */
  public final static PreferredColorSpace getPreferredColorSpacee(
      RGB.ColorSpace standardColorSpace, LCDTarget preferredTest729Target,
      double percent, CIEXYZ whiteXYZ) {
    ColorSpaceConnectedLUT clut = toPreferredColorSpaceConnectedLUT(
        standardColorSpace, preferredTest729Target, percent, whiteXYZ);
    return getPreferredColorSpacee(clut, whiteXYZ);
  }

  /**
   * ColorSpaceConnectedLUT轉PreferredColorSpace
   * (PreferredColorSpace包含PCS和clut)
   * @param clut ColorSpaceConnectedLUT
   * @param whiteXYZ CIEXYZ
   * @return PreferredColorSpace
   */
  public final static PreferredColorSpace getPreferredColorSpacee(
      ColorSpaceConnectedLUT clut, CIEXYZ whiteXYZ) {
    //3D LUT轉成四面體內插表
    TetrahedralInterpolation tetrahedral = clut.produceTetrahedralInterpolation();

    tetrahedral.registerCoordinateIF(getCoordinateInstance());
    //四面體內插表再轉成PCS
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(tetrahedral, null,
        whiteXYZ, "");
    //PreferredColorSpace即是包含PCS和clut
    PreferredColorSpace prcs = new PreferredColorSpace(pcs, clut);
    return prcs;

  }

  /**
   * 轉換為一個3D LUT (ColorSpaceConnectedLUT即為3D LUT)
   * @param standardColorSpace ColorSpace
   * @param preferredTest729Target LCDTarget
   * @param percent double
   * @param whiteXYZ CIEXYZ
   * @return ColorSpaceConnectedLUT
   */
  private final static ColorSpaceConnectedLUT toPreferredColorSpaceConnectedLUT(
      RGB.ColorSpace standardColorSpace, LCDTarget preferredTest729Target,
      double percent, CIEXYZ whiteXYZ) {
    List<Patch> patchList = preferredTest729Target.getPatchList();
    int size = patchList.size();
    //白點
    CIEXYZ preferredWhiteXYZ = preferredTest729Target.getWhitePatch().getXYZ();
    CIEXYZ standardWhiteXYZ = standardColorSpace.getReferenceWhiteXYZ();
    double[][] input = new double[size][];
    double[][] output = new double[size][];

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      input[x] = p.getRGB().getValues();
      RGB rgb = p.getRGB();
      CIEXYZ preferredXYZ = p.getXYZ();
      CIEXYZ standardXYZ = rgb.toXYZ(standardColorSpace);
      CIELCh preferredLCh = new CIELCh(new CIELab(preferredXYZ,
                                                  preferredWhiteXYZ));
      CIELCh standardLCh = new CIELCh(new CIELab(standardXYZ, standardWhiteXYZ));

      CIELCh finalLCh = new CIELCh();
      finalLCh.L = Interpolation.linear(0, 100, standardLCh.L, preferredLCh.L,
                                        percent);
      finalLCh.C = Interpolation.linear(0, 100, standardLCh.C, preferredLCh.C,
                                        percent);
      finalLCh.h = Interpolation.linear(0, 100, standardLCh.h, preferredLCh.h,
                                        percent);
      CIELab finalLab = new CIELab(finalLCh);
      CIEXYZ finalXYZ = CIELab.toXYZ(finalLab, whiteXYZ);

      output[x] = finalXYZ.getValues();
    }

    ColorSpaceConnectedLUT lut = new ColorSpaceConnectedLUT(3, 3, 9, input,
        new double[] {0, 0, 0}, new double[] {255, 255, 255}, output, null, null,
        ColorSpaceConnectedLUT.Style.AToB, ColorSpaceConnectedLUT.PCSType.XYZ);
    return lut;
  }

  public final static ColorSpaceConnectedLUT toColorSpaceConnectedLUT(
      LCDTarget test729Target) {
    List<Patch> patchList = test729Target.getPatchList();
    int size = patchList.size();
    double[][] input = new double[size][];
    double[][] output = new double[size][];
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      input[x] = p.getRGB().getValues();
      output[x] = p.getXYZ().getValues();
    }

    ColorSpaceConnectedLUT lut = new ColorSpaceConnectedLUT(3, 3, 9, input,
        new double[] {0, 0, 0}, new double[] {255, 255, 255}, output, null, null,
        ColorSpaceConnectedLUT.Style.AToB, ColorSpaceConnectedLUT.PCSType.XYZ);
    return lut;
  }

  private static TetrahedralInterpolation.CoordinateIF coordinateIF;
  private final static TetrahedralInterpolation.CoordinateIF
      getCoordinateInstance() {
    if (null == coordinateIF) {
      coordinateIF = new TetrahedralInterpolation.CoordinateIF() {

        public double[] getCoordinate(double[] key) {
          double[] gridKey = getGridKey(key);
          double[] stair = stairs;
          double x0 = gridKey[0];
          double y0 = gridKey[1];
          double z0 = gridKey[2];
          double x1 = x0 + stair[0];
          double y1 = y0 + stair[1];
          double z1 = z0 + stair[2];

          double[] keyMax = maxValues;
          if (x0 >= keyMax[0]) {
            x1 = x0;
            x0 = x1 - stair[0];
          }
          if (y0 >= keyMax[1]) {
            y1 = y0;
            y0 = y1 - stair[1];
          }
          if (z0 >= keyMax[2]) {
            z1 = z0;
            z0 = z1 - stair[2];
          }

          return new double[] {
              x0, y0, z0, x1, y1, z1};
        }

        private double[] minValues = new double[] {
            0, 0, 0};
        private double[] maxValues = new double[] {
            255, 255, 255};
        private double[] stairs = new double[] {
            32, 32, 32};
        protected final double[] getGridKey(double[] key) {
          double[] value = DoubleArray.minus(key, minValues);

          double[] remainder = DoubleArray.modulus(value, stairs);
          int size = value.length;
          for (int x = 0; x < size; x++) {
            remainder[x] = (key[x] == 255) ? 0 : remainder[x];
          }

          double[] grid = DoubleArray.minus(key, remainder);
          return grid;
        }
      };
    }

    return coordinateIF;
  }

  public final static ProfileColorSpace getProfileColorSpaceFromRampTarget(
      LCDTarget rampTarget) {
    MultiMatrixModel model = new MultiMatrixModel(rampTarget);
    model.produceFactor();
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(model, "");
    return pcs;
  }

  public final static ProfileColorSpace getProfileColorSpaceFrom729Target(
      LCDTarget test729Target) {
    ColorSpaceConnectedLUT lut = toColorSpaceConnectedLUT(test729Target);
    TetrahedralInterpolation tetrahedral = lut.produceTetrahedralInterpolation();

    //==========================================================================
    // CoordinateIF再造
    //==========================================================================
    TetrahedralInterpolation.CoordinateIF coordinateIF =
        getCoordinateInstance();
    //==========================================================================

    tetrahedral.registerCoordinateIF(coordinateIF);
    CIEXYZ whiteXYZ = test729Target.getWhitePatch().getXYZ();
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(tetrahedral, null,
        whiteXYZ, "");
    return pcs;
  }
}
