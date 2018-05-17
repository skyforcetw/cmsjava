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

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class LCDPolynomialRegressionModel
    extends LCDModel {

  /**
   * ㄏノ家Α
   *
   * @param factor LCDModelFactor
   */
  public LCDPolynomialRegressionModel(LCDModelFactor factor) {
    super(factor);
    this.coefficientCount = ( (Factor) factor.factors[0]).coefficientCount;
  }

  /**
   * ―家Α
   * @param lcdTarget LCDTarget
   */
  public LCDPolynomialRegressionModel(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  /**
   * ―家Α
   * @param lcdTarget LCDTarget
   * @param coefficientCount COEF_3
   */
  public LCDPolynomialRegressionModel(LCDTarget lcdTarget,
                                      COEF_3 coefficientCount) {
    super(lcdTarget);
    this.setCoefficientCount(coefficientCount);
  }

  /**
   * ㄏノ家Α
   * @param modelFactorFilename String
   */
  public LCDPolynomialRegressionModel(String modelFactorFilename) {
    this( (LCDModelFactor) Load.modelFactorFile(modelFactorFilename));
  }

  /**
   * ┮蹦ノ玒计兜
   */
  protected Polynomial.COEF coefficientCount;

  public String getDescription() {
    return "Poly" + coefficientCount;
  }

  /**
   * 璸衡XYZ
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, LCDModel.Factor[] factor
      ) {
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
    rgbValues = correct.gammaCorrect(rgbValues);

    double[] input = Polynomial.getCoef(rgbValues,
                                        ( (Factor) factor[0]).coefficientCount);
    double[][] coefficients = ( (Factor) factor[0]).coefficients;
    double[] result = Regression.getPredict(new double[][] {input},
                                            coefficients)[0];

    CIEXYZ XYZ = new CIEXYZ(result, this.targetWhitePoint);
    return XYZ;
  }

  protected RGB _getRGB(CIEXYZ XYZ, LCDModel.Factor[] factor
      ) {
    double[] XYZValues = XYZ.getValues();

    double[] input = Polynomial.getCoef(XYZValues, coefficientCount);
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

  /**
   * ―玒计
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
    // 玡旧(A2B)
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

    if (this.coefficientCount == null) {
      Polynomial.COEF coef = PolynomialRegression.
          findBestPolynomialCoefficient(inputArray, outputArray);
      this.coefficientCount = coef;
    }

    //linear->XYZ
    PolynomialRegression forwardRegression = new PolynomialRegression(
        inputArray,
        outputArray, coefficientCount);
    forwardRegression.regress();
    //==========================================================================

    //==========================================================================
    // は崩(B2A)
    //==========================================================================
    //XYZ->linear
    PolynomialRegression reverseRegression = new PolynomialRegression(
        outputArray, inputArray, coefficientCount);
    reverseRegression.regress();
    //==========================================================================

    Factor factor = new Factor(forwardRegression.getCoefs(),
                               reverseRegression.getCoefs(), coefficientCount);

    Factor[] factors = new Factor[] {
        factor, null, null};
//    this.setTheModelFactors(factors);
    return factors;
  }

  public static class Factor
      extends LCDModel.Factor {
    double[][] coefficients;
    double[][] reverseCoefficients;
    Polynomial.COEF coefficientCount;

    /**
     *
     * @return double[]
     */
    public double[] getVariables() {
      return null;
    }

    Factor() {

    }

    Factor(double[][] coefficients, double[][] reverseCoefficients,
           Polynomial.COEF coefficientCount) {
      this.coefficients = coefficients;
      this.reverseCoefficients = reverseCoefficients;
      this.coefficientCount = coefficientCount;
    }

    public String toString() {
      return "coefCount[" + coefficientCount + "]\ncoefs[" +
          DoubleArray.toString(coefficients) + "]\n\n" + "invCoefs[" +
          DoubleArray.toString(reverseCoefficients) + "]";

    }
  }

  public static void example1(String[] args) {
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

    LCDPolynomialRegressionModel model = new LCDPolynomialRegressionModel(
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

  public void setCoefficientCount(COEF coefficientCount) {
    this.coefficientCount = coefficientCount;
  }

}
