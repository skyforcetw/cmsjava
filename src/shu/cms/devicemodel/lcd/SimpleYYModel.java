package shu.cms.devicemodel.lcd;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.thread.*;
import shu.cms.lcd.*;
import shu.cms.util.*;
import shu.math.array.*;
import shu.math.regress.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 擷取部份YY精神的縮減版
 * 1.r/g/b先經過s-curve處裡
 * 2.再經過gamma correct(可選擇是否不要,經實驗發現結果,關掉比較好)
 * 3.套用單一頻道色塊產生的3x3矩陣,得到XYZ.
 *   這個矩陣最主摽的目的,就是要移除光學的crosstalk
 *
 * 原先的設計中,s-curve後再經過一道lut,後來發現移除之後效果更好.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class SimpleYYModel
    extends ChannelDependentModel {

  public SimpleYYModel(LCDTarget lcdTarget) {
    super(lcdTarget);
    this.rational.setDoRGBRational(true);
    this.correct.setDoGammaCorrect(false);
  }

  public static void main(String[] args) {
    LCDTarget lcdTarget1 = LCDTarget.Instance.get("CPT_17inch_Demo2",
                                                  LCDTarget.Source.CA210,
                                                  LCDTarget.Room.Dark,
                                                  LCDTarget.TargetIlluminant.
                                                  Native,
                                                  LCDTargetBase.Number.
                                                  Ramp1792, null, null);

    SimpleYYModel model = new SimpleYYModel(lcdTarget1);
//    model.setDoGammaCorrect(false);

    double start = System.currentTimeMillis();
    LCDModel.Factor[] factors = model.produceFactor();
    System.out.println("use time: " + (System.currentTimeMillis() - start));
    System.out.println(factors[0]);
    System.out.println(factors[1]);
    System.out.println(factors[2]);

    LCDTarget lcdTestTarget = lcdTarget1;

    DeltaEReport[] testReports = model.testForwardModel(lcdTestTarget, false);
    System.out.println("Training1: " + lcdTarget1.getDescription());
    System.out.println(Arrays.toString(testReports));

    DeltaEReport[] testReverseReports = model.testReverseModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget1.getDescription());
    System.out.println(Arrays.toString(testReverseReports));

    System.out.println(model.getWhiteDeltaE().getCIE2000DeltaE());
  }

  /**
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, LCDModel.Factor[] factor) {

    Factor factorR = (Factor) factor[0];

    double[][] XYZ2RGB = DoubleArray.inverse(factorR.RGB2XYZMatrix);
    double[] luminanceRGBValue = DoubleArray.times(XYZ2RGB, XYZ.getValues());
    RGB luminanceRGB = new RGB(RGB.ColorSpace.unknowRGB, luminanceRGBValue);
    luminanceRGB.rationalize();

    SCurveModel1Thread model1 = produceSCurveModel1Thread(factor);
    RGB originalRGB = model1.getOriginalRGB(luminanceRGB);
    return originalRGB;

  }

  protected CIEXYZ _getXYZ(RGB rgb, LCDModel.Factor[] factor) {

    RGB luminanceRGB = this.getLuminanceRGB(rgb, factor);

    Factor factorR = (Factor) factor[0];
    double[][] RGB2XYZ = factorR.RGB2XYZMatrix;
    double[] XYZValues = DoubleArray.times(RGB2XYZ, luminanceRGB.getValues());
    return new CIEXYZ(XYZValues, this.targetWhitePoint);
  }

  protected SCurveModel1Thread produceSCurveModel1Thread(LCDModel.Factor[]
      factor) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    SCurveModel1Thread.Factor[] model1Factors = new SCurveModel1Thread.Factor[] {
        factorR.SCurveModel1ThreadFactor, factorG.SCurveModel1ThreadFactor,
        factorB.SCurveModel1ThreadFactor};

    LCDModelFactor lcdModelFactor = this.produceLCDModelFactor(model1Factors);
    SCurveModel1Thread model1 = new SCurveModel1Thread(lcdModelFactor);
    return model1;
  }

  protected Factor[] _produceFactor() {
    //==========================================================================
    // 計算model1參數
    //==========================================================================
    SCurveModel = new SCurveModel1Thread(this.lcdTarget);
    SCurveModel.produceFactor();
    //==========================================================================

    //==========================================================================
    // 計算3x3 matrix
    //==========================================================================
    luminanceChannel.produceLuminancePatch(SCurveModel);
    //計算gamma correction
    Set<Patch> singleChannelPatchSet = luminanceChannel.getLuminancePatchSet();
    double[][] RGB2XYZ = produceRGB2XYZ_3x3(singleChannelPatchSet); //ok
    //==========================================================================

    Factor[] factors = makeFactor(SCurveModel, RGB2XYZ);
//    this.setTheModelFactors(factors);
    return factors;
  }

  protected Factor[] makeFactor(SCurveModel1Thread model1,
                                double[][] RGB2XYZ) {
    LCDModel.Factor[] model1Factors = model1.getModelFactors();

    Factor factorR = new Factor(RGBBase.Channel.R,
                                (SCurveModel1Thread.Factor) model1Factors[0],
                                correct._RrCorrector);
    factorR.RGB2XYZMatrix = RGB2XYZ;

    Factor factorG = new Factor(RGBBase.Channel.G,
                                (SCurveModel1Thread.Factor) model1Factors[1],
                                correct._GrCorrector);

    Factor factorB = new Factor(RGBBase.Channel.B,
                                (SCurveModel1Thread.Factor) model1Factors[2],
                                correct._BrCorrector);

    Factor[] factors = new Factor[] {
        factorR, factorG, factorB};

    return factors;
  }

  /**
   * 先經過model1的運算後,再經過gamma校正所得的值
   * 簡稱為GS值,但再此可由doGammaCorrect決定是否要作gammaCorrect
   * @param rgbValue double[]
   * @param model1 LuminanceRGBModelIF
   * @return double[]
   * @deprecated
   */
  protected double[] produceGSValue(double[] rgbValue,
                                    LuminanceRGBModelIF model1) {
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, rgbValue,
                      RGB.MaxValue.Double1);
    double[] values = model1.getLuminanceRGB(rgb).getValues();
    values = RGBRationalize(values);
    values = correct.gammaCorrect(values);

    return values;
  }

  /**
   *
   * @param values double[]
   * @return double[]
   * @deprecated
   */
  protected final static double[] RGBRationalize(double[] values) {
    int size = values.length;
    for (int x = 0; x < size; x++) {
      values[x] = RGBRationalize(values[x]);
    }
    return values;
  }

  /**
   *
   * @param val double
   * @return double
   * @deprecated
   */
  protected final static double RGBRationalize(double val) {
    val = RGB.rationalize(val, RGB.MaxValue.Double1);
    return val;
  }

  /**
   *
   * @param GSValue double[]
   * @param model1 SCurveModel1Thread
   * @return double[]
   * @deprecated
   */
  protected double[] produceUnGSValue(double[] GSValue,
                                      SCurveModel1Thread model1) {
    double[] luminancValue = DoubleArray.copy(GSValue);
    luminancValue = correct.gammaUncorrect(GSValue);

    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, luminancValue,
                      RGB.MaxValue.Double1);
    double[] originalValue = model1.getOriginalRGB(rgb).getValues();

    return originalValue;
  }

  protected List<Patch> patchList;
  protected Patch whitePatch;
  protected SCurveModel1Thread SCurveModel;

  public static class Factor
      extends LCDModel.Factor {

    /**
     *
     * @return double[]
     */
    public double[] getVariables() {
      return null;
    }

    Factor() {

    }

    Factor(RGBBase.Channel ch, SCurveModel1Thread.Factor model1Factor,
           GammaCorrector gammaCorrector) {
      this.channel = ch;
      this.SCurveModel1ThreadFactor = model1Factor;
      this.gammaCorrector = gammaCorrector;
    }

    Factor(RGBBase.Channel ch, SCurveModel1Thread.Factor model1Factor,
           GammaCorrector gammaCorrector, Spectra opticalSpectra) {
      this.channel = ch;
      this.SCurveModel1ThreadFactor = model1Factor;
      this.gammaCorrector = gammaCorrector;
      this.opticalSpectra = opticalSpectra;
    }

    RGBBase.Channel channel;
    SCurveModel1Thread.Factor SCurveModel1ThreadFactor;
    GammaCorrector gammaCorrector;
    double[][] RGB2XYZMatrix;
    Spectra opticalSpectra;

    public String toString() {
      return "[" + channel + "] SCurve1[" + SCurveModel1ThreadFactor + "]";
    }

  }

  /**
   *
   * @param singleChannelPatch Collection
   * @return double[][]
   */
  protected final double[][] produceRGB2XYZ_3x3(Collection<Patch>
      singleChannelPatch) {
    //==========================================================================
    // 轉成rgb和XYZ的double陣列
    //==========================================================================
    int size = singleChannelPatch.size();
    double[][] rgbArray = new double[size][3];
    double[][] XYZArray = new double[size][3];
    Patch[] patches = new Patch[size];
    singleChannelPatch.toArray(patches);

    for (int x = 0; x < size; x++) {
      Patch p = patches[x];
      p.getRGB().getValues(rgbArray[x]);
      p.getXYZ().getValues(XYZArray[x]);
      rgbArray[x] = correct.gammaCorrect(rgbArray[x]);
      XYZArray[x] = DoubleArray.minus(XYZArray[x], flare.getFlareValues());
    }
    //==========================================================================

    Regression regression = new Regression(rgbArray, XYZArray);
    regression.regress();
    return regression.getCoefs();
  }

  public String getDescription() {
    return "SimpleYY";
  }

  /**
   *
   * @param rgb RGB
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB getLuminanceRGB(RGB rgb, LCDModel.Factor[] factor) {
    SCurveModel1Thread model1 = produceSCurveModel1Thread(factor);
    return model1.getLuminanceRGB(rgb);
  }
}
