package shu.cms.measure.calibrate;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 色度儀的四色校正
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class FourColorCalibrator
    implements Serializable {
  protected double[][] calibrateMatrix;
  protected LCDMetadata sampleLCDMetadata;
  protected CIEXYZ white;

  /**
   * 將sampleRGBW校正到refRGBW
   * @param refRGBW CIEXYZ[]
   * @param sampleRGBW CIEXYZ[]
   */
  public FourColorCalibrator(CIEXYZ[] refRGBW,
                             CIEXYZ[] sampleRGBW) {
    calibrateMatrix = getCalibrateMatrix(refRGBW, sampleRGBW);
    white = refRGBW[3];
  }

  public FourColorCalibrator(double[][] calibrateMatrix) {
    this.calibrateMatrix = calibrateMatrix;
  }

  /**
   * 將lcdTargets校正到reference
   * @param reference LCDTarget
   * @param lcdTargets LCDTarget[]
   */
  public final static void calibrate(LCDTarget reference,
                                     LCDTarget[] lcdTargets) {
    LCDTarget aveTarget = LCDTarget.Operator.average(lcdTargets);
    CIEXYZ[] ref = getRGBW(reference);
    CIEXYZ[] sample = getRGBW(aveTarget);
    FourColorCalibrator calibrator = new FourColorCalibrator(ref, sample);

    for (LCDTarget lcdTarget : lcdTargets) {
//      int size = lcdTarget.size();
//      for (int x = 0; x < size; x++) {
//        Patch p = lcdTarget.getPatch(x);
//        CIEXYZ XYZ = p.getXYZ();
//        CIEXYZ result = calibrator.calibrate(XYZ);
//        XYZ.setValues(result.getValues());
//      }
      calibrator.calibrate(lcdTarget);
    }
  }

  /**
   * 將lcdTarget校正到reference
   * @param reference LCDTarget
   * @param lcdTarget LCDTarget
   */
  public FourColorCalibrator(LCDTarget reference,
                             LCDTarget lcdTarget) {
    this(getRGBW(reference), getRGBW(lcdTarget));
    sampleLCDMetadata = lcdTarget.getLCDMetadata();
  }

  /**
   * 將patchList每一個patch裡的XYZ, 重新計算校正後的XYZ, 並且替換掉.
   * @param patchList List
   */
  public final void calibrate(List<Patch> patchList) {
    int size = patchList.size();
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      CIEXYZ XYZ = p.getXYZ();
      CIEXYZ result = calibrate(XYZ);
//      XYZ.setValues(result.getValues());
      Patch.Operator.setXYZ(p, result);
    }
//    Patch.Produce.LabPatches(patchList, white);
  }

  public final void calibrate(
      LCDTarget lcdTarget) {
    int size = lcdTarget.size();
    for (int x = 0; x < size; x++) {
      Patch p = lcdTarget.getPatch(x);
      CIEXYZ XYZ = p.getXYZ();
      CIEXYZ result = calibrate(XYZ);
//      XYZ.setValues(result.getValues());
      Patch.Operator.setXYZ(p, result);
    }

    //絕對亮度校正
    CIEXYZ calLuminance = calibrate(lcdTarget.getLuminance());
    lcdTarget.getLuminance().setValues(calLuminance.getValues());
    //所有色塊Lab重新計算
    CIEXYZ white = lcdTarget.getWhitePatch().getXYZ();
    lcdTarget.calculatePatchLab(white);
    lcdTarget.calculateNormalizedXYZ();
  }

  public final static void calibrate(LCDTarget reference,
                                     LCDTarget lcdTarget) {
    CIEXYZ[] ref = getRGBW(reference);
    CIEXYZ[] sample = getRGBW(lcdTarget);
    FourColorCalibrator calibrator = new FourColorCalibrator(ref, sample);
    calibrator.calibrate(lcdTarget);
  }

  protected final static CIEXYZ[] getRGBW(LCDTarget lcdTarget) {
    if (!lcdTarget.hasRGBWPatch()) {
      throw new IllegalArgumentException(
          "lcdTarget is not satisfy \"RGBW\" patches.");
    }
    CIEXYZ w = lcdTarget.getWhitePatch().getXYZ();
    CIEXYZ r = lcdTarget.getSaturatedChannelPatch(RGBBase.Channel.R).getXYZ();
    CIEXYZ g = lcdTarget.getSaturatedChannelPatch(RGBBase.Channel.G).getXYZ();
    CIEXYZ b = lcdTarget.getSaturatedChannelPatch(RGBBase.Channel.B).getXYZ();
    return new CIEXYZ[] {
        r, g, b, w};
  }

  protected transient double[] tmpValues = new double[3];

  public final CIEXYZ calibrate(final CIEXYZ XYZ) {
    if (tmpValues == null) {
      tmpValues = new double[3];
    }
    XYZ.getValues(tmpValues);
    double[] result = DoubleArray.times(calibrateMatrix, tmpValues);
    CIEXYZ resultXYZ = new CIEXYZ(result, this.white);
    return resultXYZ;
  }

  public final static double[][] getCalibrateMatrix(CIEXYZ[] refRGBW,
      CIEXYZ[] sampleRGBW) {
    double[][] R = getR(refRGBW, sampleRGBW);
    double[] RY = R[1];
    double[] MW = sampleRGBW[3].getValues();
    double LW = refRGBW[3].Y;
    double kY = LW / DoubleArray.times(RY, MW);
    double[][] Rp = DoubleArray.times(R, kY);
    return Rp;
  }

  protected final static double[][] getxyzMatrix(CIEXYZ[] rgbw) {
//    double[][] m = new double[][] {
//        rgbw[0].getxyzValues(), rgbw[1].getxyzValues(), rgbw[2].getxyzValues()};
    double[][] m = new double[][] {
        rgbw[0].getxyzValues(), rgbw[1].getxyzValues(), rgbw[2].getxyzValues()};
    m = DoubleArray.transpose(m);
    return m;
  }

  protected final static double[][] getR(CIEXYZ[] refRGBW,
                                         CIEXYZ[] sampleRGBW) {
    double[][] M = getM(refRGBW);
    double[][] N = getM(sampleRGBW);
//    double[][] R = DoubleArray.times(N, DoubleArray.inverse(M));
    double[][] R = DoubleArray.times(M, DoubleArray.inverse(N));
    return R;
  }

  protected final static double[][] getM(CIEXYZ[] rgbw) {
    double[][] xyzm = getxyzMatrix(rgbw);
    double[][] invm = DoubleArray.inverse(xyzm);
    double[] w = rgbw[3].getxyzValues();
    double[] k = DoubleArray.times(invm, w);
    double[][] km = DoubleArray.diagonal(new double[3][3], k);
    double[][] M = DoubleArray.times(xyzm, km);
    return M;
  }

  public static void main(String[] args) {
//    LCDTarget sample = LCDTarget.Instance.get("cpt_32inch No.2",
//                                              LCDTarget.Source.K10,
//                                              LCDTargetBase.Number.Ramp1024,
//                                              LCDTarget.FileType.VastView, null);
//
//    LCDTarget ref = LCDTarget.Instance.get("cpt_32inch No.2",
//                                           LCDTarget.Source.CA210,
//                                           LCDTargetBase.Number.Ramp1024,
//                                           LCDTarget.FileType.VastView, null);
//    LCDTarget.Calibrate.storeCalibration(ref, sample);

    CIEXYZ[] ref = new CIEXYZ[] {
        new CIEXYZ(211.85, 111.19, 5.64),
        new CIEXYZ(163.53, 341.26, 49.36),
        new CIEXYZ(119.03, 55.97, 652.22),
        new CIEXYZ(494.16, 508.45, 705.78)};
    CIEXYZ[] cal = new CIEXYZ[] {
        new CIEXYZ(217.77, 112.28, 4.41),
        new CIEXYZ(162.97, 239.79, 47.67),
        new CIEXYZ(120.33, 58.84, 660.24),
        new CIEXYZ(500.38, 510.28, 710.56)};

    FourColorCalibrator calibrator = new FourColorCalibrator(ref, cal);
//    System.out.println(DoubleArray.toString(calibrator.getCalibrateMatrix()));
    for (CIEXYZ XYZ : cal) {
      System.out.println(calibrator.calibrate(XYZ));
    }
  }

  public double[][] getCalibrateMatrix() {
    return calibrateMatrix;
  }

}
