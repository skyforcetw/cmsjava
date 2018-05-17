package shu.cms.devicemodel.lcd.thread;

import java.util.*;

import flanagan.roots.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 這個model的壞處在於,求值時完全以單一channel做deltaE的考量
 * 但是單一channel的deltaE低不代表整體的就會跟的低,有時候還會相反過來!
 * 這是需要改進的地方!(因為沒有考慮到頻道相依性)
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class SCurveModel1Thread
    extends ChannelIndependentModel implements RealRootDerivFunction,
    LuminanceRGBModelIF {

  public static Factor[] toFactorArray(LCDModelBase.Factor[] factor) {
    int size = factor.length;
    Factor[] newFactor = new Factor[size];
    System.arraycopy(factor, 0, newFactor, 0, size);
    return newFactor;
  }

  /**
   * 使用模式
   * @param factor LCDModelFactor
   */
  public SCurveModel1Thread(LCDModelFactor factor) {
    super(factor);
    this.rational.setDoRGBRational(true);
  }

  public SCurveModel1Thread(LCDTarget lcdTarget) {
    super(lcdTarget);
    this.rational.setDoRGBRational(true);
  }

  public SCurveModel1Thread(String modelFactorFilename) {
    this( (LCDModelFactor) Load.modelFactorFile(modelFactorFilename));
  }

  protected CoefficientsRange[] coefficientsRange = null;

  /**
   * 初始化迭帶係數
   * 可以視情況調整此係數值,讓模式有更好的運算表現
   */
  public void initCoefficientsRange() {
    if (coefficientsRange == null) {
      //dell 1st 4096 & 729(最佳結果)
      CoefficientsRange coefR = new CoefficientsRange(1.1, 10, 1, 4, 0, 8,
          RGBBase.Channel.R);
      CoefficientsRange coefG = new CoefficientsRange(1.1, 10, 1, 6, 0, 8,
          RGBBase.Channel.G);
      CoefficientsRange coefB = new CoefficientsRange(1.1, 9, 1, 4, 0, 8,
          RGBBase.Channel.B);

      coefficientsRange = new CoefficientsRange[] {
          coefR, coefG, coefB};
    }
  }

  public void setCoefficientsRange(double[] rCoefsRange, double[] bCoefsRange,
                                   double[] gCoefsRange) {
    double[] r = rCoefsRange;
    double[] g = gCoefsRange;
    double[] b = bCoefsRange;
    CoefficientsRange coefR = new CoefficientsRange(r[0], r[1], r[2], r[3], r[4],
        r[5], RGBBase.Channel.R);
    CoefficientsRange coefG = new CoefficientsRange(g[0], g[1], g[2], g[3], g[4],
        g[5], RGBBase.Channel.G);
    CoefficientsRange coefB = new CoefficientsRange(b[0], b[1], b[2], b[3], b[4],
        b[5], RGBBase.Channel.B);

    coefficientsRange = new CoefficientsRange[] {
        coefR, coefG, coefB};
  }

  protected final Factor[] _produceFactor() {
    initCoefficientsRange();

    //==========================================================================
    //多執行緒運算
    //==========================================================================
    IterativeReport[] reports = SimpleThreadCalculator.
        produceBestIterativeReport(
            coefficientsRange, this);
    //==========================================================================
    Factor[] factors = new Factor[] {
        (Factor) reports[0].factor, (Factor) reports[1].factor,
        (Factor) reports[2].factor};

//    this.setTheModelFactors(factors);
    this.iterativeReports = reports;
    coefficientsRange = null;

    return factors;
  }

  public static void main(String[] args) {
    LCDTarget lcdTarget = LCDTarget.Instance.get("CPT_37inch",
                                                 LCDTarget.Source.CA210,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.
                                                 Native,
                                                 LCDTargetBase.Number.Ramp7147, null, null);

    //dell用1021求得的效過較差,也慢很多(符合條件的色塊較多)
    //729有最佳結果


    SCurveModel1Thread model = new SCurveModel1Thread(
        lcdTarget);
    LCDModelBase.Factor[] factors = model.produceFactor();
    System.out.println("use time: " + model.getCostTime());
    LCDModelFactor lcdModelFactor = model.produceLCDModelFactor(factors);

    /**
     * 此處係數以 序列化 處理,不使用XML儲存是因為懶的處理tag :P
     * 加上Factor間有繼承關係,目前不知道XML-Java工具是否可以很好的處理.
     */
    model.store.modelFactorFile(lcdModelFactor, "scurve1.factor");

    System.out.println("R:\n" + factors[0]);
    System.out.println("G:\n" + factors[1]);
    System.out.println("B:\n" + factors[2]);

    IterativeReport[] reports = model.getIterativeReports();
    for (IterativeReport r : reports) {
      System.out.println(r.deltaEReport);
    }

    LCDTarget lcdTestTarget = lcdTarget;

//    LCDTarget lcdTestTarget = LCDTarget.Instance.getInstanceFromCA210(
//        "RGBCMYW_original.txt",
//        CA210Adapter.Type.RGBCMYW_256);

    DeltaEReport[] testForwardReports = model.testForwardModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testForwardReports));

    DeltaEReport[] testReverseReports = model.testReverseModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReverseReports));

    System.out.println(model.getWhiteDeltaE().getCIE2000DeltaE());

//    DeltaEReport.PatchDeltaEReport patchDeltaEReport = testForwardReports[0].
//        getPatchDeltaEReport();
//    System.out.println(patchDeltaEReport);
  }

  protected Factor functionFactor;
  protected double functionOutput;
  // 1/512 = 0.001953125
  public final static double ROOT_TOLERANCE = 0.001953125;

  public double[] function(double x) {
    Factor factor = functionFactor;
    double[] y = new double[2];
    y[0] = f(factor.A, x, factor.alpha, factor.beta, factor.C) - functionOutput;
    y[1] = f_(factor.A, x, factor.alpha, factor.beta, factor.C);
    return y;
  }

  /**
   * A * x^a/ ( x^b+C)
   * @param A double
   * @param x double
   * @param alpha double
   * @param beta double
   * @param C double
   * @return double
   */
  public static double f(double A, double x, double alpha, double beta,
                            double C) {
    return A * Math.pow(x, alpha) / (Math.pow(x, beta) + C);
  }

  /**
   * first-order derivative of f(x)
   * @param A double
   * @param x double
   * @param alpha double
   * @param beta double
   * @param C double
   * @return double
   */
  protected static double f_(double A, double x, double alpha,
                             double beta, double C) {
    return A * ( (alpha - beta) * Math.pow(x, alpha + beta - 1) +
                alpha * C * Math.pow(x, alpha - 1)) /
        Maths.sqr(Math.pow(x, beta) + C);
  }

  /**
   * @param rgb RGB
   * @param factor Factor[]
   * @return RGB
   */
  protected final static RGB f_(RGB rgb, LCDModelBase.Factor[] factor) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    double ar = factorR.alpha;
    double br = factorR.beta;
    double Cr = factorR.C;
    double Ar = factorR.A;

    double ag = factorG.alpha;
    double bg = factorG.beta;
    double Cg = factorG.C;
    double Ag = factorG.A;

    double ab = factorB.alpha;
    double bb = factorB.beta;
    double Cb = factorB.C;
    double Ab = factorB.A;

    double R = f_(Ar, rgb.R, ar, br, Cr);
    double G = f_(Ag, rgb.G, ag, bg, Cg);
    double B = f_(Ab, rgb.B, ab, bb, Cb);

    RGB newRGB = new RGB(rgb.getRGBColorSpace(), new double[] {R, G, B},
                         RGB.MaxValue.Double1);
    return newRGB;

  }

  protected CIEXYZ _getXYZ(RGB rgb, LCDModelBase.Factor[] factor) {
    RGB newRGB = getLuminanceRGB(rgb, factor);
    getXYZRGB = newRGB;
    return matries.RGBToXYZByMaxMatrix(newRGB);
  }

  /**
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, LCDModelBase.Factor[] factor) {
    RGB rgb = matries.XYZToRGBByMaxMatrix(XYZ);
    rgb = rational.RGBRationalize(rgb);
    if (rgb.isLegal()) {
      //如果rgb合理,才可以推出原始的RGB值
      return getOriginalRGB(rgb, factor);
    }
    else {
      return null;
    }
  }

  public final RGB getLuminanceRGB(RGB rgb) {
    if (rgb.getMaxValue() != RGB.MaxValue.Double1) {
      throw new IllegalArgumentException(
          "rgb.getMaxValue() != RGB.MaxValue.Double1");
    }
    return getLuminanceRGB(rgb, this.getModelFactors());
  }

  /**
   * 提供給YY模式呼叫使用
   * @param rgb RGB
   * @param factor Factor[]
   * @return RGB
   */
  protected final RGB getLuminanceRGB(RGB rgb, LCDModelBase.Factor[] factor) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];
    double R = f(factorR.A, rgb.R, factorR.alpha, factorR.beta, factorR.C);
    double G = f(factorG.A, rgb.G, factorG.alpha, factorG.beta, factorG.C);
    double B = f(factorB.A, rgb.B, factorB.alpha, factorB.beta, factorB.C);

    RGB newRGB = new RGB(rgb.getRGBColorSpace(), new double[] {R, G, B},
                         RGB.MaxValue.Double1);
    return newRGB;
  }

  /**
   *
   * @param luminanceRGB RGB
   * @return RGB
   */
  public final RGB getOriginalRGB(RGB luminanceRGB) {
    return getOriginalRGB(luminanceRGB, this.getModelFactors());
  }

  /**
   * 利用牛頓法反推回原始RGB
   * @param luminanceRGB RGB
   * @param factor Factor[]
   * @return RGB
   */
  protected final RGB getOriginalRGB(RGB luminanceRGB,
                                     LCDModelBase.Factor[] factor) {
    RealRoot realRoot = new RealRoot();
    realRoot.setTolerance(ROOT_TOLERANCE);
    RGB clone = (RGB) luminanceRGB.clone();

    this.functionFactor = (Factor) factor[0];
    this.functionOutput = luminanceRGB.R;
    clone.R = realRoot.newtonRaphson(this, luminanceRGB.R);

    this.functionFactor = (Factor) factor[1];
    this.functionOutput = luminanceRGB.G;
    clone.G = realRoot.newtonRaphson(this, luminanceRGB.G);

    this.functionOutput = luminanceRGB.B;
    this.functionFactor = (Factor) factor[2];
    clone.B = realRoot.newtonRaphson(this, luminanceRGB.B);
    return clone;
  }

  /**
   *
   * @param coefRange CoefficientsRange
   * @param patchList List
   * @param whitePatch Patch
   * @param bestReport IterativeReport
   * @return IterativeReport
   * @deprecated
   */
  public IterativeReport iterateAndReport(
      SimpleThreadCalculator.CoefficientsRange coefRange,
      List<Patch> patchList,
      Patch whitePatch, IterativeReport bestReport) {
    return null;
  }

  public static class Factor
      extends LCDModelBase.Factor {
    RGBBase.Channel channel;
    public double A;
    public double alpha;
    public double beta;
    public double C;

    public double[] getVariables() {
      return new double[] {
          A, alpha, beta, C};
    }

    public String toString() {
      return "[" + channel + "] A[" + A + "] alpha[" + alpha + "] beta[" +
          beta + "] C[" + C +
          "]";
    }

    public Factor() {

    }

    public Factor(double[] variables, RGBBase.Channel channel) {
      this.A = variables[0];
      this.alpha = variables[1];
      this.beta = variables[2];
      this.C = variables[0] - 1.;
//      this.C = variables[3];
      this.channel = channel;
    }
  }

  /**
   *
   * @param variables double[]
   * @param channel Channel
   * @return Factor[]
   */
  public LCDModelBase.Factor[] getFactors(double[] variables,
                                          RGBBase.Channel channel) {
    Factor[] factors = new Factor[] {
        new Factor(), new Factor(), new Factor()};
    factors[channel.getArrayIndex()] = new Factor(variables, channel);
    return factors;
  }

  public CoefficientsRange getNewCoefficientsRange(LCDModelBase.Factor factor,
      SimpleThreadCalculator.CoefficientsRange old) {
    return new CoefficientsRange( (Factor) factor, (CoefficientsRange) old);
  }

  public class CoefficientsRange
      extends SimpleThreadCalculator.CoefficientsRange {

    Range A;
    Range a;
    Range b;
//    Range C;

    /**
     *
     * @return double[]
     */
    public double[] getStartVariables() {
      return new double[] {
          A.start, a.start, b.start};
    }

    /**
     *
     * @return double[]
     */
    public double[] getEndVariables() {
      return new double[] {
          A.end, a.end, b.end};
    }

    public void setStartVariables(double[] start) {
      A.start = start[0];
      a.start = start[1];
      b.start = start[2];
//      C.start = start[3];
    }

    public void setEndVariables(double[] end) {
      A.end = end[0];
      a.end = end[1];
      b.end = end[2];
//      C.end = end[3];
    }

    CoefficientsRange(Factor factor, CoefficientsRange old) {
      A = Range.determineRange(factor.A, old.A, getStepRate(),
                               getRangeRate());

      a = Range.determineRange(factor.alpha, old.a, getStepRate(),
                               getRangeRate());

      b = Range.determineRange(factor.beta, old.b, getStepRate(),
                               getRangeRate());

//      C = Range.determineRange(factor.C, old.C, getStepRate(),
//                               getRangeRate());

      this.channel = factor.channel;
    }

    /**
     *
     * @param AS double
     * @param AE double
     * @param aS double
     * @param aE double
     * @param bS double
     * @param bE double
     * @param CS double
     * @param CE double
     * @param channel Channel
     * @deprecated
     */
    CoefficientsRange(double AS,
                      double AE,
                      double aS,
                      double aE,
                      double bS,
                      double bE,
                      double CS,
                      double CE,
                      RGBBase.Channel channel) {

      A = new Range(AS, AE, getStepRate());
      a = new Range(aS, aE, getStepRate());
      b = new Range(bS, bE, getStepRate());
//      C = new Range(CS, CE, getStepRate());

      this.channel = channel;
    }

    CoefficientsRange(double AS,
                      double AE,
                      double aS,
                      double aE,
                      double bS,
                      double bE,
                      RGBBase.Channel channel) {

      A = new Range(AS, AE, getStepRate());
      a = new Range(aS, aE, getStepRate());
      b = new Range(bS, bE, getStepRate());
//      C = new Range(AS - 1., AE + 1., getStepRate());

      this.channel = channel;
    }
  }

  public int getMaxIterativeTimes() {
    return 15;
  }

  public String getDescription() {
    return "SCurveI";
  }

}
