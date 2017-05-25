package shu.cms.hvs.hk;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.regress.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * HK效應的模型
 * 實作原理請見
 *  Simple Estimation Methods for the Helmholtz–Kohlrausch Effect,
 *  Yoshinobu Nayatani(1997)
 *
 * VAC(Variable-Achromatic-Color): 改變中性色的亮度, 去符合有彩色的亮度
 * VCC (Variable-Chromatic-Color): 改變有彩色的亮度, 去符合中性色的亮度
 * 一般來說VCC較為有用, 因此在此建議採用VCC
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class NayataniHKModel {

  protected CIEXYZ white;

  public NayataniHKModel(CIEXYZ white) {
    this.white = white;
  }

  public final double getVCCLuminance(CIEXYZ XYZ) {
    double gamma = getGammaVCC(XYZ);
    return XYZ.Y * gamma;
  }

//  public final double getLuminanceByRegression(CIEXYZ XYZ) {
//    if (regression == null) {
//      throw new java.lang.IllegalStateException("regression == null");
//    }
//    return regression.getPredict(new double[] {XYZ.Y})[0];
//  }

  /**
   *
   * @param XYZArray CIEXYZ[]
   * @return double[][] input是Y,output是HK之後的Y
   */
  protected double[][] getRegressionInputAndOutput(CIEXYZ[] XYZArray) {
    int size = XYZArray.length;
    double[] input = new double[size];
    double[] output = new double[size];

    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = XYZArray[x];
      double Y = getVCCLuminance(XYZ);
      input[x] = XYZ.Y;
      output[x] = Double.isNaN(Y) ? 0 : Y;
    }
    double[][] result = new double[][] {
        input, output};
    return result;
  }

  public final static double getVCCLuminance(CIEXYZ XYZ, CIEXYZ white) {
    double gamma = getGammaVCC(XYZ, white);
    return XYZ.Y * gamma;
  }

  public final static double getGammaVAC(CIEXYZ XYZ, CIEXYZ white) {
    double[] uvp = new CIExyY(XYZ).getuvPrimeValues();
    double[] whiteuvp = new CIExyY(white).getuvPrimeValues();
    double q = getQTheta(uvp, whiteuvp);
    double KBr = getKBr(white);
    double Suv = getSuv(uvp, whiteuvp);
    return 0.4462 *
        Math.pow(1 + ( -0.1340 * q + 0.0872 * KBr) * Suv + 0.3086, 3);
  }

  public final static double getGammaVCC(CIEXYZ XYZ, CIEXYZ white) {
    double[] uvp = new CIExyY(XYZ).getuvPrimeValues();
    double[] whiteuvp = new CIExyY(white).getuvPrimeValues();
    double q = getQTheta(uvp, whiteuvp);
    double KBr = getKBr(white);
    double Suv = getSuv(uvp, whiteuvp);
    return 0.4462 *
        Math.pow(1 + ( -0.8660 * q + 0.0872 * KBr) * Suv + 0.3086, 3);
  }

  public final double getGammaVCC(CIEXYZ XYZ) {
    double[] uvp = new CIExyY(XYZ).getuvPrimeValues();
    double[] whiteuvp = new CIExyY(white).getuvPrimeValues();
    double q = getQTheta(uvp, whiteuvp);
    double KBr = getKBr(white);
    double Suv = getSuv(uvp, whiteuvp);
    return 0.4462 *
        Math.pow(1 + ( -0.8660 * q + 0.0872 * KBr) * Suv + 0.3086, 3);
  }

  private final static double getTheta(double[] uvPrime, double[] whiteuvPrime) {
    double[] diff = DoubleArray.minus(uvPrime, whiteuvPrime);
    double[] d = new double[] {
        0, diff[0], diff[1]};
    double[] LCh = CIELCh.cartesian2polarCoordinatesValues(d);
    return LCh[2];
  }

  private final static double getKBr(CIEXYZ white) {
    double La04495 = Math.pow(white.Y * .2, 0.4495);
    return 0.2717 * ( (6.469 + 6.362 * La04495) / (6.469 + La04495));
  }

  private final static double getSuv(double[] uvPrime, double[] whiteuvPrime) {
    double[] deltauvp = DoubleArray.minus(uvPrime, whiteuvPrime);
    return 13 * Math.sqrt(Maths.sqr(deltauvp[0]) + Maths.sqr(deltauvp[1]));
  }

  private final static double getQTheta(double[] uvp, double[] whiteuvp) {
    double t = getTheta(uvp, whiteuvp);
    t = Math.toRadians(t);
    double q = -0.01585
        - 0.03017 * Math.cos(t) - 0.04556 * Math.cos(2 * t)
        - 0.02667 * Math.cos(3 * t) - 0.00295 * Math.cos(4 * t)
        + 0.14592 * Math.sin(t) + 0.05084 * Math.sin(2 * t)
        - 0.01900 * Math.sin(3 * t) - 0.00764 * Math.sin(4 * t);
    return q;
  }

  public static void main(String[] args) {
    LCDTarget target = LCDTarget.Instance.get("cpt_320WF01SC",
                                              LCDTarget.Source.K10,
                                              LCDTargetBase.Number.Ramp1024,
                                              LCDTarget.FileType.VastView,
                                              null, "cp");
//    CIEXYZ white = target.getWhitePatch().getXYZ();
    CIEXYZ white = target.getWhitePatch().getNormalizedXYZ();
    List<Patch> patchList = target.filter.oneValueChannel(RGBBase.Channel.B);
//    List<Patch> patchList = target.filter.grayPatch();
    for (Patch p : patchList) {
      CIEXYZ XYZ = p.getXYZ();
//      CIEXYZ XYZ = p.getNormalizedXYZ();

      double VCC = getGammaVCC(XYZ, white);
//      System.out.println(XYZ.Y + " " + VCC + " " + getKBr(XYZ));
    }

//    for (int x = 0; x < 64; x++) {
//      CIEXYZ XYZ = new CIEXYZ(0, x, 0);
//      System.out.println(getKBr(XYZ));
//    }

  }

  /**
   * 以XYZArray為來源資料, 產生VCC係數的回歸
   * @param XYZArray CIEXYZ[]
   */
  public final void produceRegression(CIEXYZ[] XYZArray) {
    double[][] inputAndOutput = getRegressionInputAndOutput(XYZArray);
    double[] input = inputAndOutput[0];
    double[] output = inputAndOutput[1];

    Polynomial.COEF_1 coef = PolynomialRegression.
        findBestPolynomialCoefficient1(input, output);
    if (coef.hasLowerOrder()) {
      coef = coef.getLowerOrder();
    }
    regression = new PolynomialRegression(input, output, coef);
    regression.regress();
  }

  public final double getHKLuminanceByRegression(CIEXYZ XYZ) {
    if (regression == null) {
      throw new java.lang.IllegalStateException("regression == null");
    }
    return regression.getPredict(new double[] {XYZ.Y})[0];
  }

  protected PolynomialRegression regression = null;
  public final void resetRegression() {
    regression = null;
  }
}
