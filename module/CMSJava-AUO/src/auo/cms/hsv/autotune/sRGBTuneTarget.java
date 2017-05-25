package auo.cms.hsv.autotune;

import java.util.*;

import auo.cms.hsv.old.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.profile.*;
import shu.math.array.*;

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
public class sRGBTuneTarget
    extends TuneTarget implements SingleTuneTarget {
  public static enum Patches {
    StandAlone, Integrated, IEC61966_4_NonNeutral
  }

  private List<RGB> IEC61966_4Patches;
  private List<RGB> testPatches;
  private List<RGB> totalPatches;
  private Patches patches;
  private Map<Double, HSV[]> multiPatchesMap;
  private HSVLUTInterpolator singlePatchesInterpolator;

  private void initTestPatchesMap() {
    //==========================================================================
    // 單一色塊
    //==========================================================================
    int testSize = testPatches.size();
    double[][] hsvLut = new double[3][testSize];
    for (int x = 0; x < testSize; x++) {
      HSV hsv = new HSV(testPatches.get(x));
      hsvLut[0][x] = hsv.H;
      hsvLut[1][x] = hsv.S;
      hsvLut[2][x] = hsv.V;
    }
    singlePatchesInterpolator = new HSVLUTInterpolator(hsvLut[0], hsvLut[1],
        hsvLut[2]);
    //==========================================================================

    //==========================================================================
    // 多個色塊
    //==========================================================================
    int size = totalPatches.size();
    HSV[] totalHSVArray = new HSV[size];

    for (int x = 0; x < size; x++) {
      RGB rgb = totalPatches.get(x);
      HSV hsv = new HSV(rgb);
      totalHSVArray[x] = hsv;
    }
    Arrays.sort(totalHSVArray);
    multiPatchesMap = new Hashtable<Double, HSV[]> (size);
    for (int x = 0; x < 6; x++) {
      HSV[] hsvArray = new HSV[5];
      for (int y = 0; y < 5; y++) {
        hsvArray[y] = totalHSVArray[x * 5 + y];
      }
      multiPatchesMap.put(Double.valueOf(x * 60), hsvArray);
    }
    //==========================================================================
  }

  public sRGBTuneTarget(Patches patches) {
    this.patches = patches;
    List<RGB> whqlRGBList = LCDTarget.Instance.getRGBList(LCDTarget.Number.WHQL);
    testPatches = getTestPatches(this.patches, whqlRGBList);
    IEC61966_4Patches = getTestPatches(Patches.IEC61966_4_NonNeutral,
                                       whqlRGBList);
    totalPatches = new ArrayList<RGB> (testPatches.size() +
                                       IEC61966_4Patches.size());
    totalPatches.addAll(testPatches);
    totalPatches.addAll(IEC61966_4Patches);
    initTestPatchesMap();
  }

  private ProfileColorSpace pcs;
  public sRGBTuneTarget(Patches patches, ProfileColorSpace pcs) {
    this(patches);
    this.pcs = pcs;
  }

  public final static sRGBTuneTarget getInstance(Patches patches,
                                                 LCDTarget target729) {
    if (target729.getNumber() != LCDTarget.Number.Test729) {
      throw new IllegalArgumentException(
          "target729.getNumber() != LCDTarget.Number.Test729");
    }
    ProfileColorSpace pcs = ProfileColorSpaceUtils.
        getProfileColorSpaceFrom729Target(target729);
    sRGBTuneTarget tunrTarget = new sRGBTuneTarget(patches, pcs);
    return tunrTarget;
  }

  private static List<RGB> getTestPatches(Patches patches, List<RGB>
      whqlRGBList) {
    switch (patches) {
      case StandAlone:
        return whqlRGBList.subList(0, 6);
      case Integrated:
        return whqlRGBList.subList(7, 13);
      case IEC61966_4_NonNeutral:
        return whqlRGBList.subList(22, 46);
      default:
        return null;
    }
  }

  /**
   * getTarget
   *
   * @param hue double
   * @param saturation double
   * @param value double
   * @return CIEXYZ
   */
  public CIEXYZ getTarget(double hue, double saturation, double value) {
    HSV hsv = new HSV(RGB.ColorSpace.sRGB_gamma22, new double[] {hue,
                      saturation, value});
    RGB rgb = hsv.toRGB();
    if (pcs != null) {
      return getPCSCIEXYZ(rgb);
    }
    else {
      return rgb.toXYZ();
    }
  }

  private CIEXYZ getPCSCIEXYZ(RGB rgb) {
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
    try {
      double[] XYZValues = pcs.toCIEXYZValues(rgbValues);
      CIEXYZ XYZ = new CIEXYZ(XYZValues);
      return XYZ;
    }
    catch (IllegalArgumentException ex) {
      return null;
    }
  }

  public CIEXYZ getTarget(int hue) {
    HSV hsv = getTuneSpot(hue);
    return getTarget(hsv.H, hsv.S, hsv.V);

  }

  /**
   * getTuneSpot
   *
   * @param hue double
   * @return HSV
   */
  public HSV getTuneSpot(double hue) {
    //利用內插出來的值產生出調整色塊
    double saturation = singlePatchesInterpolator.getSaturationAdjustDouble(
        hue);
    double value = singlePatchesInterpolator.getValueAdjustDouble(hue);
    HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {hue, saturation, value});
    return hsv;
  }

  public HSV[] getTuneSpots(double hue) {
    throw new UnsupportedOperationException("");
//    HSV hsv = getTuneSpot(hue);
//    HSV[] hsvArray = new HSV[] {
//        hsv};
//    return hsvArray;
  }
}
