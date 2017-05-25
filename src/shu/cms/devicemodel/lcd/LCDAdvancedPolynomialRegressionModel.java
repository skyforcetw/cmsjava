package shu.cms.devicemodel.lcd;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.*;
import shu.cms.devicemodel.lcd.LCDModelBase.*;
import shu.cms.devicemodel.lcd.util.*;
import shu.cms.lcd.*;
import shu.math.*;
import shu.math.Polynomial.*;
import shu.math.array.*;
import shu.math.regress.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 所謂的advanced就是:把前導跟反推的係數分開了,也就是兩個model可以用不同的係數個數
 * 而且也新增加了根據deltaE最佳化係數個數的功能: findBestForwardPolynomialCoefficient3
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author vastview.com.tw
 * @version 1.0
 */
public class LCDAdvancedPolynomialRegressionModel
    extends LCDModel {

  public static class Factor
      extends LCDModel.Factor {
    double[][] forwardCoefficients;
    double[][] reverseCoefficients;
    Polynomial.COEF forwardCoefficientCount;
    Polynomial.COEF reverseCoefficientCount;

    /**
     *
     * @return double[]
     */
    public double[] getVariables() {
      return null;
    }

    Factor() {

    }

    Factor(Polynomial.COEF forwardCoefficientCount,
           double[][] forwardCoefficients,
           Polynomial.COEF reverseCoefficientCount,
           double[][] reverseCoefficients) {
      this.forwardCoefficientCount = forwardCoefficientCount;
      this.forwardCoefficients = forwardCoefficients;
      this.reverseCoefficientCount = reverseCoefficientCount;
      this.reverseCoefficients = reverseCoefficients;
    }

    public String toString() {
      return "forwardCoefCount[" + forwardCoefficientCount + "]\ncoefs[" +
          DoubleArray.toString(forwardCoefficients) + "]\n\n" +
          "reverseCoefCount[" + reverseCoefficientCount + "]\ncoefs[" +
          DoubleArray.toString(reverseCoefficients) + "]";

    }
  }

  /**
   * 使用模式
   *
   * @param factor LCDModelFactor
   */
  public LCDAdvancedPolynomialRegressionModel(LCDModelFactor factor) {
    super(factor);
  }

  /**
   * 求值模式
   * @param lcdTarget LCDTarget
   */
  public LCDAdvancedPolynomialRegressionModel(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  /**
   * 求值模式
   * @param lcdTarget LCDTarget
   * @param rCorrectLCDTarget LCDTarget 建議採用code間距為0的導具
   */
  public LCDAdvancedPolynomialRegressionModel(LCDTarget lcdTarget,
                                              LCDTarget rCorrectLCDTarget) {
    this(lcdTarget, rCorrectLCDTarget, null, null, false);
  }

  public LCDAdvancedPolynomialRegressionModel(LCDTarget lcdTarget,
                                              LCDTarget rCorrectLCDTarget,
                                              boolean
                                              cooperateWithLCDTargetInterpolator) {
    this(lcdTarget, rCorrectLCDTarget, null, null,
         cooperateWithLCDTargetInterpolator);
  }

  public LCDAdvancedPolynomialRegressionModel(LCDTarget lcdTarget,
                                              LCDTarget rCorrectLCDTarget,
                                              COEF forwardCoefficientCount,
                                              COEF reverseCoefficientCount,
                                              boolean
                                              cooperateWithLCDTargetInterpolator) {
    super(lcdTarget, rCorrectLCDTarget, cooperateWithLCDTargetInterpolator);
    this.setForwardCoefficientCount(forwardCoefficientCount);
    this.setReverseCoefficientCount(reverseCoefficientCount);
  }

  /**
   * 使用模式
   * @param modelFactorFilename String
   */
  public LCDAdvancedPolynomialRegressionModel(String modelFactorFilename) {
    this( (LCDModelFactor) Load.modelFactorFile(modelFactorFilename));
  }

  public static void main(String[] args) {
    LCDTarget.setRGBNormalize(false);
//    LCDTarget.setXYZNormalize(false);
    LCDTarget lcdTarget = LCDTarget.Instance.get("cpt_17inch No.3",
                                                 LCDTarget.Source.CA210,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.
                                                 Native,
                                                 LCDTargetBase.Number.Ramp1792,
                                                 LCDTarget.FileType.VastView,
                                                 null, null);

    LCDTarget xtalklcdTarget = LCDTarget.Instance.get("cpt_17inch No.3",
        LCDTarget.Source.CA210,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.
        Native,
        LCDTargetBase.Number.Test4096,
        LCDTarget.FileType.Logo,
        null, null);

    LCDAdvancedPolynomialRegressionModel model = new
        LCDAdvancedPolynomialRegressionModel(
            xtalklcdTarget);
    model.produceFactor();
    System.out.println(model.getCostTime());

    DeltaEReport[] testReports = model.testForwardModel(xtalklcdTarget, false);
    System.out.println("Training1: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReports));
    System.out.println(testReports[0].getPatchDeltaEReport(.5));

    DeltaEReport[] testReverseReports = model.testReverseModel(xtalklcdTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReverseReports));
    System.out.println(testReverseReports[0].getPatchDeltaEReport(.5));
  }

  /**
   * 求係數
   *
   * @return Factor[]
   */
  protected Factor[] _produceFactor() {
    singleChannel.produceRGBPatch();
    correct.produceGammaCorrector();

    List<Patch> patchList = this.lcdTarget.getPatchList();
    int size = patchList.size();
    double[][] inputArray = new double[size][3];
    double[][] outputArray = new double[size][3];

    //==========================================================================
    // 前導(A2B)
    //==========================================================================
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      //device RGB
      p.getRGB().getValues(inputArray[x], RGB.MaxValue.Double1);
      //linear RGB
      inputArray[x] = correct.gammaCorrect(inputArray[x]);
      outputArray[x] = DoubleArray.minus(p.getXYZ().getValues(),
                                         this.flare.getFlareValues());
    }

    if (this.forwardCoefficientCount == null) {
//      Polynomial.COEF_3 coef = this.findBestForwardPolynomialCoefficient3(
//          OptimalMethod.MaxDeltaE);
      Polynomial.COEF coef = this.findBestForwardPolynomialCoefficient(
          OptimalMethod.AverageDeltaE);
      this.forwardCoefficientCount = coef;
    }

    //linear->XYZ
    PolynomialRegression forwardRegression = new PolynomialRegression(
        inputArray,
        outputArray, forwardCoefficientCount);
    forwardRegression.regress();
    //==========================================================================

    //==========================================================================
    // 反推(B2A)
    //==========================================================================
    if (this.reverseCoefficientCount == null) {
//      Polynomial.COEF_3 coef  = this.findBestReversePolynomialCoefficient3(
//          OptimalMethod.MaxDeltaE, (Polynomial.COEF_3) forwardCoefficientCount);
      Polynomial.COEF coef = this.findBestReversePolynomialCoefficient(
          OptimalMethod.AverageDeltaE, forwardCoefficientCount);
      this.reverseCoefficientCount = coef;
    }
    //XYZ->linear
    PolynomialRegression reverseRegression = new PolynomialRegression(
        outputArray, inputArray, reverseCoefficientCount);
    reverseRegression.regress();
    //==========================================================================

    Factor factor = new Factor(forwardCoefficientCount,
                               forwardRegression.getCoefs(),
                               reverseCoefficientCount,
                               reverseRegression.getCoefs());

    Factor[] factors = new Factor[] {
        factor, null, null};
//    this.setTheModelFactors(factors);
    return factors;
  }

  /**
   * 所採用的係數項
   */
  private Polynomial.COEF forwardCoefficientCount;
  private Polynomial.COEF reverseCoefficientCount;

  public String getDescription() {
    return "Poly forward[" + forwardCoefficientCount + "] reverse[" +
        reverseCoefficientCount + "]";
  }

  /**
   * 計算XYZ
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, LCDModel.Factor[] factor) {
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
//    double[] rgbValues = rgb.getValues();
    rgbValues = correct.gammaCorrect(rgbValues);

    double[] input = Polynomial.getCoef(rgbValues,
                                        ( (Factor) factor[0]).
                                        forwardCoefficientCount);
    double[][] coefficients = ( (Factor) factor[0]).forwardCoefficients;
    double[] result = Regression.getPredict(new double[][] {input},
                                            coefficients)[0];
    CIEXYZ XYZ = new CIEXYZ(result, this.targetWhitePoint);
    return XYZ;
  }

  protected RGB _getRGB(CIEXYZ XYZ, LCDModel.Factor[] factor
      ) {
    double[] XYZValues = XYZ.getValues();

    double[] input = Polynomial.getCoef(XYZValues,
                                        ( (Factor) factor[0]).
                                        reverseCoefficientCount);
    double[][] coefficients = ( (Factor) factor[0]).reverseCoefficients;
    double[] result = Regression.getPredict(new double[][] {input},
                                            coefficients)[0];

    result = correct.gammaUncorrect(result);
    result = LCDModelUtil.fixRGB(result, lcdTarget.getMaxValue());
    if (result == null) {
      return null;
    }
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, result, RGB.MaxValue.Double1);
    return rgb;
  }

  public void setForwardCoefficientCount(COEF forwardCoefficientCount) {
    this.forwardCoefficientCount = forwardCoefficientCount;
  }

  public void setReverseCoefficientCount(COEF reverseCoefficientCount) {
    this.reverseCoefficientCount = reverseCoefficientCount;
  }

  public void setCooperateWithLCDTargetInterpolator(boolean
      cooperateWithLCDTargetInterpolator) {
    this.cooperateWithLCDTargetInterpolator =
        cooperateWithLCDTargetInterpolator;
  }

  /**
   * 尋找ForwardCoefficient時只在COEF_1
   * @param findForwardCoefficientInCOEF_1 boolean
   */
  public void setFindForwardCoefficientInCOEF_1(boolean
                                                findForwardCoefficientInCOEF_1) {
    this.findForwardCoefficientInCOEF_1 = findForwardCoefficientInCOEF_1;
  }

  /**
   * 尋找ReverseCoefficient時只在COEF_1
   * @param findReverseCoefficientInCOEF_1 boolean
   */
  public void setFindReverseCoefficientInCOEF_1(boolean
                                                findReverseCoefficientInCOEF_1) {
    this.findReverseCoefficientInCOEF_1 = findReverseCoefficientInCOEF_1;
  }

  public Polynomial.COEF_3 findBestReversePolynomialCoefficient3(
      OptimalMethod method, Polynomial.COEF_3 forwardCoefficientCount) {

    Polynomial.COEF_3 bestPolynomial = null;
    double minIndex = Double.MAX_VALUE;
    double index = -1;

    for (Polynomial.COEF_3 c : Polynomial.COEF_3.values()) {
      LCDAdvancedPolynomialRegressionModel model = new
          LCDAdvancedPolynomialRegressionModel(this.
                                               lcdTarget);
      model.setForwardCoefficientCount(forwardCoefficientCount);
      model.setReverseCoefficientCount(c);
      model.produceFactor();
      DeltaEReport report = model.testReverseModel(this.lcdTarget, false)[0];

      switch (method) {
        case AverageDeltaE:
          index = report.meanDeltaE.getMeasuredDeltaE();
          break;
        case MaxDeltaE:
          index = report.maxDeltaE.getMeasuredDeltaE();
          break;
      }

      if (index < minIndex) {
        minIndex = index;
        bestPolynomial = c;
      }
    }

    return bestPolynomial;
  }

  public Polynomial.COEF_3 findBestForwardPolynomialCoefficient3(
      OptimalMethod method) {

    Polynomial.COEF_3 bestPolynomial = null;
    double minIndex = Double.MAX_VALUE;
    double index = -1;

    for (Polynomial.COEF_3 c : Polynomial.COEF_3.values()) {
      LCDAdvancedPolynomialRegressionModel model = new
          LCDAdvancedPolynomialRegressionModel(this.
                                               lcdTarget, rCorrectLCDTarget, c,
                                               c,
                                               cooperateWithLCDTargetInterpolator);
      model.produceFactor();
      DeltaEReport report = model.testForwardModel(this.lcdTarget, false)[
          0];

      switch (method) {
        case AverageDeltaE:
          index = report.meanDeltaE.getMeasuredDeltaE();
          break;
        case MaxDeltaE:
          index = report.maxDeltaE.getMeasuredDeltaE();
          break;
      }

      if (index < minIndex) {
        minIndex = index;
        bestPolynomial = c;
      }
    }

    return bestPolynomial;
  }

  private boolean findForwardCoefficientInCOEF_1 = false;
  private boolean findReverseCoefficientInCOEF_1 = false;

  public Polynomial.COEF findBestForwardPolynomialCoefficient(
      OptimalMethod method) {

    Polynomial.COEF bestPolynomial = null;
    double minIndex = Double.MAX_VALUE;
    double index = -1;

    Object[] coef = null;
    if (findForwardCoefficientInCOEF_1) {
      coef = Polynomial.COEF_1.values();
    }
    else {
      coef = Utils.concatArray(Polynomial.COEF_1.values(),
                               Polynomial.COEF_3.values());
    }

    for (Object o : coef) {
      Polynomial.COEF c = (Polynomial.COEF) o;
      LCDAdvancedPolynomialRegressionModel model = new
          LCDAdvancedPolynomialRegressionModel(this.
                                               lcdTarget, rCorrectLCDTarget, c,
                                               c,
                                               cooperateWithLCDTargetInterpolator);
      model.produceFactor();
      DeltaEReport report = model.testForwardModel(this.lcdTarget, false)[
          0];

      switch (method) {
        case AverageDeltaE:
          index = report.meanDeltaE.getMeasuredDeltaE();
          break;
        case MaxDeltaE:
          index = report.maxDeltaE.getMeasuredDeltaE();
          break;
      }

      if (index < minIndex) {
        minIndex = index;
        bestPolynomial = c;
      }
    }

    return bestPolynomial;
  }

  public Polynomial.COEF findBestReversePolynomialCoefficient(
      OptimalMethod method, Polynomial.COEF forwardCoefficientCount) {

    Polynomial.COEF bestPolynomial = null;
    double minIndex = Double.MAX_VALUE;
    double index = -1;

    Object[] coef = null;
    if (findReverseCoefficientInCOEF_1) {
      coef = Polynomial.COEF_1.values();
    }
    else {
      coef = Utils.concatArray(Polynomial.COEF_1.values(),
                               Polynomial.COEF_3.values());
    }

    for (Object o : coef) {
      Polynomial.COEF c = (Polynomial.COEF) o;
      LCDAdvancedPolynomialRegressionModel model = new
          LCDAdvancedPolynomialRegressionModel(this.
                                               lcdTarget);
      model.setForwardCoefficientCount(forwardCoefficientCount);
      model.setReverseCoefficientCount(c);
      model.produceFactor();
      DeltaEReport report = model.testReverseModel(this.lcdTarget, false)[0];

      switch (method) {
        case AverageDeltaE:
          index = report.meanDeltaE.getMeasuredDeltaE();
          break;
        case MaxDeltaE:
          index = report.maxDeltaE.getMeasuredDeltaE();
          break;
      }

      if (index < minIndex) {
        minIndex = index;
        bestPolynomial = c;
      }
    }

    return bestPolynomial;
  }

  private boolean cooperateWithLCDTargetInterpolator = false;
  public static enum OptimalMethod {
    AverageDeltaE, MaxDeltaE,
  }
}
