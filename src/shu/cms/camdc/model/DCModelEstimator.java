package shu.cms.camdc.model;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.math.regress.*;

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
public class DCModelEstimator {
  private DCTarget dcTarget;
  public DCModelEstimator(DCTarget dcTarget) {
    this.dcTarget = dcTarget;
    init(this.dcTarget);
    if (DoAnalyzeTouchBoundary) {
      analyzeTouchBoundary(dcTarget, 5);
    }
  }

  Interpolation1DLUT luminanceLUT;
  double luminanceGamma;
  private int luminancePatchSkipCount = 1;

  /**
   * 建構dg count轉亮度的曲線
   * 分別產生為gamma以及對照表型式
   * @param dcTarget DCTarget
   */
  private void initLuminanceLUT(DCTarget dcTarget) {
    List<Patch> patchList = dcTarget.filter.grayScale();
    int size = patchList.size();
    size = size - luminancePatchSkipCount;
    double[] digitCountArray = new double[size + 2];
    double[] luminanceArray = new double[size + 2];

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      digitCountArray[x + 1] = p.getRGB().G;
      luminanceArray[x + 1] = p.getNormalizedXYZ().Y;
    }
    digitCountArray[0] = 0;
    digitCountArray[size + 1] = 255;

    luminanceArray[0] = 0;
    luminanceArray[size + 1] = 1;
    luminanceLUT = new Interpolation1DLUT(digitCountArray, luminanceArray);
    luminanceGamma = GammaFinder.findingGamma(digitCountArray, luminanceArray,
                                              0, 0, 255, 1);
  }

  double[][] rgb2XYZMatrix;
  double[][] XYZ2rgbMatrix;
  private void produceRGB2XYZMatrix(DCTarget dcTarget) {
    int size = dcTarget.size();
    double[][] XYZValuesArray = new double[size][];
    double[][] rgbValuesArray = new double[size][];
    for (int x = 0; x < size; x++) {
      Patch p = dcTarget.getPatch(x);
//      RGB rgb = p.getRGB();
      RGB rgb = p.getOriginalRGB();
      CIEXYZ XYZ = p.getNormalizedXYZ();
      XYZValuesArray[x] = XYZ.getValues();
      double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
//      for (int c = 0; c < 3; c++) {
//        double v = rgbValues[c];
//        rgbValues[c] = luminanceLUT.getValue(v);
//      }
      rgbValuesArray[x] = rgbValues;
    }
    PolynomialRegression regress = new PolynomialRegression(rgbValuesArray,
        XYZValuesArray, Polynomial.COEF_3.BY_3);
    regress.regress();
//    double rmsd = regress.getRMSD();
//    double[][] predict = regress.getPredict();
    rgb2XYZMatrix = regress.getCoefs();
    XYZ2rgbMatrix = DoubleArray.inverse(rgb2XYZMatrix);
  }

  private static boolean DoAnalyzeTouchBoundary = false;

  private void analyzeTouchBoundary(DCTarget dcTarget, int tolerance) {
    Patch white = dcTarget.getBrightestPatch();
    double[] whiteRGBValues = white.getRGB().getValues();

    for (Patch p : dcTarget.getPatchList()) {
      if (white != p) {
        RGB rgb = p.getRGB();
        double[] rgbValues = rgb.getValues();
        double[] delta = DoubleArray.minus(rgbValues, whiteRGBValues);
        DoubleArray.abs(delta);
        for (int x = 0; x < 3; x++) {
          if (delta[x] < tolerance) {
            System.out.println(p.getName() + " " + p.getRGB());
            break;
          }
        }
      }
//      whiteRGB.
    }
  }

  /**
   * 將digit count的rgb轉回為與亮度線性的rgb
   * @param dcTarget DCTarget
   */
  private void initOriignalRGB(DCTarget dcTarget) {
    for (Patch p : dcTarget.getPatchList()) {
      RGB rgb = p.getRGB();
      RGB luminanceRGB = (RGB) rgb.clone();
      luminanceRGB.R = luminanceLUT.getValue(rgb.R) * 255;
      luminanceRGB.G = luminanceLUT.getValue(rgb.G) * 255;
      luminanceRGB.B = luminanceLUT.getValue(rgb.B) * 255;
      Patch.Operator.setOriginalRGB(p, luminanceRGB);
    }
  }

  private double[] getWhiteBalanceRBRatio(DCTarget dcTarget) {
    List<Patch> grayScale = dcTarget.filter.grayScale();
    double[] rbRatio = new double[2];
    int size = grayScale.size();
    for (int x = 1; x < size - 1; x++) {
//    for (Patch p : grayScale) {
      Patch p = grayScale.get(x);
      RGB luminanceRGB = p.getOriginalRGB();
      double rRatio = luminanceRGB.G / luminanceRGB.R;
      double bRatio = luminanceRGB.G / luminanceRGB.B;
      rbRatio[0] += rRatio;
      rbRatio[1] += bRatio;
    }

    rbRatio[0] /= (size - 2);
    rbRatio[1] /= (size - 2);
    return rbRatio;
  }

  private double[] wbRBRatio;
  private void init(DCTarget dcTarget) {
    initLuminanceLUT(dcTarget);
//    initOriignalRGB(dcTarget);
    produceRGB2XYZMatrix(dcTarget);

    wbRBRatio = getWhiteBalanceRBRatio(dcTarget);
  }

  public static void main(String[] args) {
//    IdealDigitalCamera camera = estimateHTCLegend(false, false);
    String iccfilename = "Measurement Files/camera/htc legend/test3/日.icc";
    IdealDigitalCamera camera = CameraSensorEstimator.estimateCamera(
        iccfilename, false, false);
    Plot2D plot = Plot2D.getInstance();

    //F8下推出的sensor對應到A下面並不準確



    /**
     * 白平衡
     *
     * 首先算出某白點下的rgb2XYZ
     * 然後將XYZ轉到lms, lms再色適應到D65(因為要存成sRGB)
     * 再轉回XYZ, 再轉成sRGB
     */
  }

}
