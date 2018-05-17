package shu.cms.hvs.pca;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.cms.plot.*;
import shu.math.*;
//import shu.plot.*;

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
public abstract class PartialChromaticAdaptation {

  /**
   * 計算經過部分適應和混合適應後的XYZ
   * @param XYZ CIEXYZ
   * @return LMS
   */
  public final LMS getAdaptation(CIEXYZ XYZ) {
    LMS mixedAdaptaion = getMixedAdaptation();
    XYZ = (CIEXYZ) XYZ.clone();
    XYZ.normalize(displayWhite);
//    LMS lms = incompleteChromaticAdaptation(XYZ);
    LMS lms = getLMS(XYZ);
    lms.L = lms.L / mixedAdaptaion.L;
    lms.M = lms.M / mixedAdaptaion.M;
    lms.S = lms.S / mixedAdaptaion.S;
    return lms;
  }

  private CIEXYZ adaptedWhite;
  private CIEXYZ adaptedDisplayWhite;

  /**
   * 部分適應後的螢幕白點
   * @return CIEXYZ
   */
  public CIEXYZ getAdaptedDisplayWhite() {
    if (adaptedDisplayWhite == null) {
      LMS lms = incompleteChromaticAdaptation(normalDisplayWhite);
      adaptedDisplayWhite = lms.toXYZ();
    }
    return adaptedDisplayWhite;
  }

  /**
   * 經過部分適應和混合適應後的適應白點
   * @return CIEXYZ
   */
  public CIEXYZ getAdaptedWhite() {
    if (adaptedWhite == null) {
      LMS mixedAdaptaion = getMixedAdaptation();
      adaptedWhite = mixedAdaptaion.toXYZ();
    }
    return adaptedWhite;
  }

//  protected abstract double getDFactor();

  protected void initDFactorIF() {
    if (this instanceof DFactorIF) {
      this.DFactor = (DFactorIF)this;
    }
  }

  public static void main(String[] args) {
//    plotSubjectiveNeutralPoints();
    plotMCANeutralPoints(35);
    plotMCANeutralPoints(70);
  }

  protected final static Plot2D plotMCANeutralPoints(double luminance) {
    Plot2D p = Plot2D.getInstance("MCA Neutral Points " +
                                  Double.toString(luminance) + " nits");

    int[] CCTArray = new int[] {
        9800, 6650, 5900, 5500, 4500, 4000, 3350};
    for (int displayCCT : CCTArray) {
      for (int CCT = 3000; CCT <= 17500; CCT += 100) {

        CIExyY xyY = CorrelatedColorTemperature.CCT2BlackbodyxyY(displayCCT);
        xyY.Y = luminance;
        CIEXYZ displayWhite = xyY.toXYZ();
        xyY = CorrelatedColorTemperature.CCT2BlackbodyxyY(CCT);
        xyY.Y = luminance;
        CIEXYZ illuminantXYZ = xyY.toXYZ();

        ViewingConditions vc = new ViewingConditions(displayWhite,
            illuminantXYZ,
            Surround.Average);

//        PartialChromaticAdaptation pca = new SLMS(vc);
//         PartialChromaticAdaptation pca = new SLMS2001(vc);
        PartialChromaticAdaptation pca = new TC8_04(vc);

        CIEXYZ XYZ = pca.getAdaptedWhite();
        double npCCT = CorrelatedColorTemperature.XYZ2CCTByRobertson(XYZ);
        p.addCacheScatterLinePlot(Integer.toString(displayCCT), CCT, npCCT);

      }
    }
    p.addLegend();
    p.setAxeLabel(0, "CCT of Illuminant[K]");
    p.setAxeLabel(1, "Adaptative CCT[K]");
    p.setVisible();
    return p;
  }

  protected final static Plot2D plotSubjectiveNeutralPoints() {
    Plot2D p = Plot2D.getInstance();
    int[] CCTArray = new int[] {
        9800, 6900, 5900, 4975, 4000, 2985};

    for (int CCT : CCTArray) {
      for (double LogLa = 0; LogLa <= 3; LogLa += 0.05) {
        double La = Math.pow(10, LogLa);

        CIExyY xyY = CorrelatedColorTemperature.CCT2BlackbodyxyY(CCT);
        xyY.Y = La;
        CIEXYZ adaptingWhite = xyY.toXYZ();
        ViewingConditions vc = new ViewingConditions(adaptingWhite, new CIEXYZ(),
            Surround.Dark);

//         PartialChromaticAdaptation pca = new SLMS(vc);
//         PartialChromaticAdaptation pca = new SLMS2001(vc);
        PartialChromaticAdaptation pca = new TC8_04(vc);

        CIEXYZ XYZ = pca.getAdaptedDisplayWhite();
        double npCCT = CorrelatedColorTemperature.XYZ2CCTByRobertson(XYZ);
        p.addCacheScatterLinePlot(Integer.toString(CCT), LogLa, npCCT);
      }
    }
    p.setAxeLabel(0, "Adapting luminance, log(La)[cd/m^2]");
    p.setAxeLabel(1, "CCT of the neutral point,[K]");
    p.addLegend();
    p.setVisible();
    return p;
  }

  protected final LMS incompleteChromaticAdaptation(CIEXYZ XYZ) {
    double[] dLMSValues = null;
    if (DFactor != null) {
      dLMSValues = getdLMSValues(DFactor);
    }
    else {
      dLMSValues = getpLMSValues();
    }
    LMS lms = this.getLMS(XYZ);
    lms.L /= dLMSValues[0];
    lms.M /= dLMSValues[1];
    lms.S /= dLMSValues[2];
    return lms;
  }

  /**
   * 取得部分適應的係數
   * @return double[]
   */
  protected abstract double[] getpLMSValues();

  private double[] dLMSValues;

  /**
   * 從DFactor計算部分適應的係數
   * @param DFactor DFactorIF
   * @return double[]
   */
  protected final double[] getdLMSValues(DFactorIF DFactor) {
    if (dLMSValues == null) {
      dLMSValues = getLMSValues(normalDisplayWhite.getValues());
      double D = DFactor.getDFactor();
      dLMSValues[0] = D + dLMSValues[0] * (1 - D);
      dLMSValues[1] = D + dLMSValues[1] * (1 - D);
      dLMSValues[2] = D + dLMSValues[2] * (1 - D);
    }

    return dLMSValues;
  }

  private DFactorIF DFactor;
  protected CIEXYZ displayWhite;
  protected CIEXYZ ambientWhite;
  protected CIEXYZ normalDisplayWhite;
  protected CIEXYZ normalAmbientWhite;
  protected ViewingConditions vc;
  public PartialChromaticAdaptation(ViewingConditions vc,
                                    CAMConst.CATType catType) {
    this.vc = vc;

    displayWhite = vc.displayWhite;
    ambientWhite = vc.ambientWhite;
    normalDisplayWhite = (CIEXYZ) vc.displayWhite.clone();
    normalAmbientWhite = (CIEXYZ) vc.ambientWhite.clone();
    normalDisplayWhite.normalizeY();
    normalAmbientWhite.normalizeY();

    this.catType = catType;
    initDFactorIF();
  }

  private double Ydap = -1;

  protected double getMixedWeightFactor(double luminance) {
    double Yadp = this.getYadp();
    return Maths.cubeRoot(luminance / Yadp);
  }

  /**
   * 計算適應白點時的螢幕係數
   * @return double
   */
  protected double getDisplayFactor() {
    if (displayFactor == -1) {
      displayFactor = vc.Radp * getMixedWeightFactor(displayWhite.Y);
    }
    return displayFactor;
  }

  /**
   * 計算適應白點時的環境係數
   * @return double
   */
  protected double getAmbientFactor() {
    if (ambientFactor == -1) {
      ambientFactor = (1 - vc.Radp) * getMixedWeightFactor(ambientWhite.Y);
    }
    return ambientFactor;
  }

  private double ambientFactor = -1;
  private double displayFactor = -1;

//  protected double getDisplayMixedWeightFactor() {
//    if (displayWeight == -1) {
//      return displayWeight;
//    }
//    return displayWeight;
//  }
//
//  protected double getAmbientMixedWeightFactor() {
//    if (ambientWeight == -1) {
//      return ambientWeight;
//    }
//    return ambientWeight;
//  }
//
//  private double displayWeight = -1;
//  private double ambientWeight = -1;

  protected final double getYadp() {
    if (Ydap == -1) {
      double YnDisplay = displayWhite.Y;
      double YnAmbient = ambientWhite.Y;
      double Rdap = vc.Radp;
      Ydap = Math.pow(Rdap * Maths.cubeRoot(YnDisplay) +
                      (1 - Rdap) * Maths.cubeRoot(YnAmbient), 3);
    }
    return Ydap;
  }

  protected LMS getMixedAdaptation() {
    if (mixedAdaptaion == null) {
      mixedAdaptaion = incompleteChromaticAdaptation(normalDisplayWhite);
      LMS ambientLMS = getLMS(normalAmbientWhite);
      mixedAdaptaion.L = getDisplayFactor() * mixedAdaptaion.L +
          getAmbientFactor() * ambientLMS.L;
      mixedAdaptaion.M = getDisplayFactor() * mixedAdaptaion.M +
          getAmbientFactor() * ambientLMS.M;
      mixedAdaptaion.S = getDisplayFactor() * mixedAdaptaion.S +
          getAmbientFactor() * ambientLMS.S;
    }
    return mixedAdaptaion;
  }

  private LMS mixedAdaptaion;

  private CAMConst.CATType catType = CAMConst.CATType.CAT02;
  protected CAMConst.CATType getCATType() {
    return catType;
  }

  protected final LMS getLMS(CIEXYZ XYZ) {
    LMS lms = new LMS(XYZ, catType);
    return lms;
  }

  protected final double[] getLMSValues(double[] XYZValues) {
    double[] lmsValues = LMS.fromXYZValues(XYZValues, catType);
    return lmsValues;
  }

//  protected abstract LMS incompleteChromaticAdaptation(CIEXYZ XYZ);
}
