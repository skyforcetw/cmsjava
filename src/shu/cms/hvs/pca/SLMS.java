package shu.cms.hvs.pca;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class SLMS
    extends PartialChromaticAdaptation {
  /**
   * SLMS
   *
   * @param vc ViewingConditions
   */
  public SLMS(ViewingConditions vc) {
    super(vc, CAMConst.CATType.Hunt);
  }

  protected SLMS(ViewingConditions vc, CAMConst.CATType catType) {
    super(vc, CAMConst.CATType.Hunt);
  }

  /**
   * getMixedAdaptation
   *
   * @return LMS
   */
//  protected LMS getMixedAdaptation() {
//    LMS diaplayLMSnp = incompleteChromaticAdaptation(normalDisplayWhite);
//    LMS ambientLMS = getLMS(normalAmbientWhite);
//    diaplayLMSnp.L = getDisplayFactor() * diaplayLMSnp.L +
//        getAmbientFactor() * ambientLMS.L;
//    diaplayLMSnp.M = getDisplayFactor() * diaplayLMSnp.M +
//        getAmbientFactor() * ambientLMS.M;
//    diaplayLMSnp.S = getDisplayFactor() * diaplayLMSnp.S +
//        getAmbientFactor() * ambientLMS.S;
//    return diaplayLMSnp;
//  }

  /**
   * incompleteChromaticAdaptation
   *
   * @param XYZ CIEXYZ
   * @return LMS
   */
//  protected LMS incompleteChromaticAdaptation(CIEXYZ XYZ) {
//    double[] pLMSValues = getpLMSValues();
//    LMS lms = this.getLMS(XYZ);
//    lms.L /= pLMSValues[0];
//    lms.M /= pLMSValues[1];
//    lms.S /= pLMSValues[2];
//    return lms;
//  }

  private double[] pLMSValues;

  protected final double[] getpLMSValues() {
    if (pLMSValues == null) {
      double YnDisplay = displayWhite.Y;
      pLMSValues = new double[3];
      double[] lmseValues = getLMSEValues();
      double YnDisplay3 = Maths.cubeRoot(YnDisplay);

      for (int x = 0; x < 3; x++) {
        pLMSValues[x] = (1 + YnDisplay3 + lmseValues[x]) /
            (1 + YnDisplay3 + 1. / lmseValues[x]);
      }
    }
    return pLMSValues;
  }

  protected double[] getLMSEValues() {
    double[] lmsEValues = getLMSValues(normalDisplayWhite.getValues());
    double sum = Maths.sum(lmsEValues);
    for (int x = 0; x < lmsEValues.length; x++) {
      lmsEValues[x] = 3 * lmsEValues[x] / sum;
    }
    return lmsEValues;
  }

  public static void main(String[] args) {
//    CIEXYZ display = new CIEXYZ(80, 90, 50);
//    CIEXYZ displayn = new CIEXYZ(95, 100, 85);
//    CIEXYZ ambient = new CIEXYZ(83, 93, 73);
    CIEXYZ display = new CIEXYZ(38, 40, 59);
    CIEXYZ displayn = new CIEXYZ(76.23, 80, 118.63);
    CIEXYZ ambient = new CIEXYZ(161.53, 160, 102.01);

    ViewingConditions vc = new ViewingConditions(displayn, ambient,
                                                 Surround.Average);
//    PartialChromaticAdaptation pca = new SLMS(vc);
    PartialChromaticAdaptation pca = new TC8_04(vc);

    System.out.println(pca.getAdaptation(display));
    System.out.println(pca.getAdaptedWhite());
    System.out.println(CorrelatedColorTemperature.XYZ2CCTByRobertson(pca.
        getAdaptedWhite()));

//    SLMS2001 slms2001 = new SLMS2001(vc);
//    System.out.println(slms2001.getAdaptation(display));
//    System.out.println(CorrelatedColorTemperature.XYZ2CCT(slms2001.
//        getAdaptedWhite()));
  }
}
