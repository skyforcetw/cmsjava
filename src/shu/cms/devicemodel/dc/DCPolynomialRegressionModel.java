package shu.cms.devicemodel.dc;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.devicemodel.*;
import shu.cms.devicemodel.dc.DCModel.*;
import shu.cms.util.*;
import shu.math.*;
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
public class DCPolynomialRegressionModel
    extends DCModel {

  /**
   *
   * @param XYZ CIEXYZ
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ) {
    Factor factor = (Factor) theModelFactor;
    double[] XYZValues = XYZ.getValues();

    double[] input = Polynomial.getCoef(XYZValues,
                                        factor.inverseCoefficientCount);
    double[][] coefficients = factor.inverseCoefficients;
    double[] result = Regression.getPredict(new double[][] {input},
                                            coefficients)[0];

    //=======================================================================
    // 進行gamma反校正
    //=======================================================================
    result = this.gammaUncorrect(result);
    //=======================================================================

    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, result);
    rgb.changeMaxValue(this.getMaxValue());
    return rgb;
  }

  protected CIEXYZ _getXYZ(RGB rgb) {
    Factor factor = (Factor) theModelFactor;
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);

    //=======================================================================
    // 進行gamma校正
    //=======================================================================
    rgbValues = gammaCorrect(rgbValues);
    //=======================================================================

    double[] input = Polynomial.getCoef(rgbValues,
                                        factor.forwardCoefficientCount);
    double[][] coefficients = factor.forwardCoefficients;
    double[] result = Regression.getPredict(new double[][] {input},
                                            coefficients)[0];
    CIEXYZ XYZ = new CIEXYZ(result, this.whitePatchXYZ);
    return XYZ;
  }

  /**
   * 求係數
   *
   * @return Factor[]
   */
  protected Factor _produceFactor() {
    //=======================================================================
    // 進行gamma校正,但是測試結果是不要校正比較好-.-,是流程錯誤嗎?
    //=======================================================================
    produceGammaCorrector();
    //=======================================================================

    List<Patch> patchList = dcTarget.filter.patchListForProfile();
    int size = patchList.size();
    double[][] input = new double[size][3];
    double[][] output = new double[size][3];

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      p.getRGB().getValues(input[x], RGB.MaxValue.Double1);

      //=======================================================================
      // 進行gamma校正
      //=======================================================================
      input[x] = gammaCorrect(input[x]);
      //=======================================================================

      p.getNormalizedXYZ().getValues(output[x]);
    }

    //進行回歸運算
    if (forwardCoef == null) {
      forwardCoef = PolynomialRegression.findBestPolynomialCoefficient3(
          input, output);
    }
    forwardRegression = new
        ReductionPolynomialRegression(input, output, forwardCoef);
    forwardRegression.regressBestDataSet();

    //進行回歸運算
    if (inverseCoef == null) {
      inverseCoef = PolynomialRegression.findBestPolynomialCoefficient3(
          output, input);
    }
    inverseRegression = new
        ReductionPolynomialRegression(output, input, inverseCoef);
    inverseRegression.regressBestDataSet();

    Factor factor = new Factor(forwardRegression.getCoefs(), forwardCoef,
                               inverseRegression.getCoefs(), inverseCoef,
                               _RrCorrector, _GrCorrector, _BrCorrector,
                               this.getWhitePatchXYZ(), normal);
    this.theModelFactor = factor;
    return factor;
  }

  private ReductionPolynomialRegression forwardRegression;
  private ReductionPolynomialRegression inverseRegression;

  /**
   * 使用模式
   * @param modelFactorFilename String
   */
  public DCPolynomialRegressionModel(String modelFactorFilename) {
    this( (DCModelFactor) Load.modelFactorFile(modelFactorFilename));
  }

  protected Polynomial.COEF_3 forwardCoef;
  protected Polynomial.COEF_3 inverseCoef;

  public String getDescription() {
    return "PolyBy" + forwardCoef.item;
  }

  /**
   * 求值模式
   * @param dcTarget DCTarget
   * @param doGammaCorrect boolean
   * @param forwardCoef COEF_3
   * @param targetRGBNormalize boolean 是否依照白色色塊進行RGB正規化
   */
  public DCPolynomialRegressionModel(DCTarget dcTarget, boolean doGammaCorrect,
                                     Polynomial.COEF_3 forwardCoef,
                                     boolean targetRGBNormalize) {

    super(dcTarget, doGammaCorrect, targetRGBNormalize);
    this.forwardCoef = forwardCoef;
  }

  /**
   * 求值模式
   * @param dcTarget DCTarget
   * @param doGammaCorrect boolean
   * @param forwardCoef COEF_3 前導係數
   * @param inverseCoef COEF_3 反推係數
   * @param targetRGBNormalize boolean 是否依照白色色塊進行RGB正規化
   */
  public DCPolynomialRegressionModel(DCTarget dcTarget, boolean doGammaCorrect,
                                     Polynomial.COEF_3 forwardCoef,
                                     Polynomial.COEF_3 inverseCoef,
                                     boolean targetRGBNormalize) {

    super(dcTarget, doGammaCorrect, targetRGBNormalize);
    this.forwardCoef = forwardCoef;
    this.inverseCoef = inverseCoef;
  }

  /**
   * 使用模式
   * @param factor DCModelFactor
   */
  public DCPolynomialRegressionModel(DCModelFactor factor) {
    super(factor);
  }

  public static class Factor
      extends DCModel.Factor {
    public double[][] forwardCoefficients;
    public Polynomial.COEF_3 forwardCoefficientCount;
    public double[][] inverseCoefficients;
    public Polynomial.COEF_3 inverseCoefficientCount;

    Factor() {

    }

    Factor(double[][] coefficients, Polynomial.COEF_3 coefficientCount,
           GammaCorrector RrCorrecor,
           GammaCorrector GrCorrecor,
           GammaCorrector BrCorrecor, CIEXYZ whitePatch, double[] normal) {
      super(RrCorrecor, GrCorrecor, BrCorrecor, whitePatch, normal);
      this.forwardCoefficients = coefficients;
      this.forwardCoefficientCount = coefficientCount;
    }

    Factor(double[][] forwardCoefficients,
           Polynomial.COEF_3 forwardCoefficientCount,
           double[][] inverseCoefficients,
           Polynomial.COEF_3 inverseCoefficientCount,
           GammaCorrector RrCorrecor,
           GammaCorrector GrCorrecor,
           GammaCorrector BrCorrecor, CIEXYZ whitePatch, double[] normal) {
      super(RrCorrecor, GrCorrecor, BrCorrecor, whitePatch, normal);
      this.forwardCoefficients = forwardCoefficients;
      this.forwardCoefficientCount = forwardCoefficientCount;
      this.inverseCoefficients = inverseCoefficients;
      this.inverseCoefficientCount = inverseCoefficientCount;
    }

  }

  public static void main(String[] args) {
    List<Patch> patchList = new ArrayList<Patch> (24);

    RGB r = RGB.Red;
    RGB g = RGB.Green;
    RGB b = RGB.Blue;
    RGB w = RGB.White;
    patchList.add(new Patch(r.toString(), r.toXYZ(RGB.ColorSpace.sRGB), null, r));
    patchList.add(new Patch(g.toString(), g.toXYZ(RGB.ColorSpace.sRGB), null, g));
    patchList.add(new Patch(b.toString(), b.toXYZ(RGB.ColorSpace.sRGB), null, b));
    patchList.add(new Patch(w.toString(), w.toXYZ(RGB.ColorSpace.sRGB), null, w));

    for (int x = 0; x < 20; x++) {
//      Patch p = new
      RGB rgb = new RGB(RGB.ColorSpace.sRGB,
                        new int[] {
                        (int) (Math.random() * 255),
                        (int) (Math.random() * 255),
                        (int) (Math.random() * 255)});
      CIEXYZ XYZ = rgb.toXYZ();
      Patch p = new Patch(rgb.toString(), XYZ, null, rgb);
      patchList.add(p);
    }
    for (Patch p : patchList) {
      System.out.println(p);
    }
    DCTarget target = DCTarget.Instance.get(patchList, Illuminant.D65);
    DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(target, false,
        Polynomial.COEF_3.BY_3, Polynomial.COEF_3.BY_3, false);
    model.produceFactor();
    double[][] coefs = model.forwardRegression.getCoefs();
    for (double[] coef : coefs) {
      System.out.println(Arrays.toString(coef));
    }
  }

  public static void example1(String[] args) {
    DCTarget target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                            LightSource.i1Pro.D65,
                                            1., DCTarget.Chart.CCSG);

    DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(target, false,
        Polynomial.COEF_3.BY_19C, true);
    DCModel.Factor factor = model.produceFactor();
    DCModelFactor dcModelFactor = model.produceDCModelFactor(factor);
    String filename = "factor/" + dcModelFactor.getModelFactorFilename() +
        ".factor";
    System.out.println(filename);
    model.store.modelFactorFile(dcModelFactor, filename);

    DeltaEReport[] testReports = model.testTarget(target, false);

    System.out.println("Training: " + target.getDescription());
    System.out.println(Arrays.toString(testReports));

    for (Patch p : target.getPatchList()) {
      RGB rgb = p.getRGB();
      rgb.changeMaxValue(RGB.MaxValue.Int8Bit);
//      System.out.println(p.getName()+" "+ rgb + " " + p.getXYZ() + " " + p.getLab());
      System.out.println(p.getName() + " " + p.getLab());
    }
  }
}
