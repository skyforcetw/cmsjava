package shu.cms.devicemodel.lcd;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.*;
import shu.cms.devicemodel.lcd.LCDModelBase.*;
import shu.cms.devicemodel.lcd.util.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.math.lut.*;
import shu.math.regress.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 透過CLUT可產生反推模式
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class CLUTRegressionReverseModel
    extends LCDModel {

  protected ColorSpaceConnectedLUT A2BClut;
  protected TetrahedralInterpolation interpolation;
  protected CubeTable cubeTable;
  protected Polynomial.COEF_3 coefficientCount;

  public static class Factor
      extends LCDModel.Factor {
    public double[][] reverseCoefficients;
    Polynomial.COEF_3 coefficientCount;

    /**
     *
     * @return double[]
     */
    public double[] getVariables() {
      return null;
    }

    Factor() {

    }

    Factor(double[][] reverseCoefficients,
           Polynomial.COEF_3 coefficientCount) {
      this.reverseCoefficients = reverseCoefficients;
      this.coefficientCount = coefficientCount;
    }

  }

//  /**
//   * 使用模式
//   *
//   * @param factor LCDModelFactor
//   */
//  public CLUTRegressionReverseModel(LCDModelFactor factor) {
//    super(factor);
//  }

//  /**
//   *
//   * @param modelFactorFilename String
//   */
//  public CLUTRegressionReverseModel(String modelFactorFilename) {
//    this( (LCDModelFactor) Load.modelFactorFile(modelFactorFilename));
//  }

  public CLUTRegressionReverseModel(ColorSpaceConnectedLUT A2BClut,
                                    Polynomial.COEF_3 coefficientCount,
                                    double A2BNormalize) {
    this.A2BClut = A2BClut;
    this.cubeTable = A2BClut.produceCubeTable();
    this.interpolation = new TetrahedralInterpolation(cubeTable);
    this.coefficientCount = coefficientCount;
    this.A2BNormalize = 1. / A2BNormalize;
    this.flare.setFlare(new CIEXYZ());
    this.setMaxValue(A2BClut.getMaxValue());
    CIEXYZ modelWhite = new CIEXYZ(interpolation.getValues(RGB.White.getValues()));
    this.setModelWhite(modelWhite);
    this.targetWhitePoint = modelWhite;
  }

  protected double A2BNormalize;

  public CLUTRegressionReverseModel(ColorSpaceConnectedLUT A2BClut,
                                    Polynomial.COEF_3 coefficientCount) {
    this(A2BClut, coefficientCount, 1.);
  }

  public ColorSpaceConnectedLUT getB2AClut() {
    if (B2AClut == null) {
      B2AClut = produceB2AClut();
    }
    return B2AClut;
  }

  protected ColorSpaceConnectedLUT B2AClut;

  protected ColorSpaceConnectedLUT produceB2AClut() {
    int grid = A2BClut.getNumberOfGridPoints();
    double[][] input = ColorSpaceConnectedLUT.produceInputXYZCLUT(grid);
    int size = input.length;
    double[][] output = new double[size][];
    CIEXYZ XYZ = new CIEXYZ();

    for (int x = 0; x < size; x++) {
      XYZ.setValues(input[x]);
      RGB rgb = this.getRGB(XYZ, false);
      if (!rgb.isLegal()) {
        RGB.rationalize(rgb);
      }
      output[x] = rgb.getValues();
    }

    int inputChannels = A2BClut.getInputChannels();
    int outputChannels = A2BClut.getOutputChannels();

    ColorSpaceConnectedLUT B2A = new ColorSpaceConnectedLUT(inputChannels,
        outputChannels, grid, input, new double[] {0, 0, 0}, new double[] {1, 1,
        1}, output, null, null, ColorSpaceConnectedLUT.Style.AToB,
        ColorSpaceConnectedLUT.PCSType.XYZ);

    return B2A;
  }

  /**
   * 計算RGB,反推模式
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, LCDModel.Factor[] factor
      ) {
//    double[] XYZValues = fromXYZ(XYZ.getValues(), relativeXYZ);
    double[] XYZValues = XYZ.getValues();

    double[] input = Polynomial.getCoef(XYZValues, coefficientCount);
    double[][] coefficients = ( (Factor) factor[0]).reverseCoefficients;
    double[] result = Regression.getPredict(new double[][] {input},
                                            coefficients)[0];
    RGB rgb = null;
    if (null != lcdTarget) {
      result = LCDModelUtil.fixRGB(result, lcdTarget.getMaxValue());
      rgb = new RGB(RGB.ColorSpace.unknowRGB, result, RGB.MaxValue.Double1);
    }
    else {
      rgb = new RGB(RGB.ColorSpace.unknowRGB, result, this.getMaxValue());
    }
    rgb.rationalize();
    return rgb;
  }

  /**
   * 計算XYZ,前導模式
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, LCDModel.Factor[] factor
      ) {
    double[] XYZValues = interpolation.getValues(rgb.getValues());
    CIEXYZ result = new CIEXYZ(XYZValues, getModelWhite());
    result.times(A2BNormalize);
    return result;
  }

  /**
   * 求係數
   * @return Factor[]
   */
  protected Factor[] _produceFactor() {
    int size = (int) Math.pow(cubeTable.getGrid(), 3);
    double[][] inputArray = new double[size][];
    double[][] XYZArray = new double[size][];
    CIEXYZ XYZ = new CIEXYZ();

    for (int x = 0; x < size; x++) {
      inputArray[x] = cubeTable.getKey(x);
      XYZ.setValues(cubeTable.getValue(x));
      XYZ.times(A2BNormalize);
      XYZArray[x] = XYZ.getValues();
    }

    //==========================================================================
    // 反推(B2A)
    //==========================================================================
    //XYZ->linear
    PolynomialRegression inverseRegression = new PolynomialRegression(
        XYZArray, inputArray, coefficientCount);
    inverseRegression.regress();
    //==========================================================================

    Factor factor = new Factor(inverseRegression.getCoefs(), coefficientCount);

    Factor[] factors = new Factor[] {
        factor, null, null};
    return factors;
  }

  public String getDescription() {
    return "RegressionReverse";
  }

}
