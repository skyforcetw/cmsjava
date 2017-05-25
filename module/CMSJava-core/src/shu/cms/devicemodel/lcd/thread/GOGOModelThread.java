package shu.cms.devicemodel.lcd.thread;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;

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
public class GOGOModelThread
    extends GOGModelThread {

  protected CoefficientsRange[] gogoCoefficientsRange = null;

  public GOGOModelThread(LCDModelFactor factor) {
    super(factor);
    this.rational.setDoRGBRational(true);
  }

  public GOGOModelThread(String modelFactorFilename) {
    this( (LCDModelFactor) Load.modelFactorFile(modelFactorFilename));
  }

  public GOGOModelThread(LCDTarget lcdTarget) {
    super(lcdTarget);
    this.rational.setDoRGBRational(true);
  }

  protected Factor[] _produceFactor() {
    initCoefficientsRange();

    //==========================================================================
    //多執行緒運算
    //==========================================================================
    IterativeReport[] reports = SimpleThreadCalculator.
        produceBestIterativeReport(
            gogoCoefficientsRange, this);
    //==========================================================================
    Factor[] factors = new Factor[] {
        (Factor) reports[0].factor, (Factor) reports[1].factor,
        (Factor) reports[2].factor};

//    this.setTheModelFactors(factors);
    this.iterativeReports = reports;
    gogoCoefficientsRange = null;

    return factors;
  }

  public static void main(String[] args) {
//    LCDTarget.Device device = LCDTarget.Device.Dell_M1210;
    String device = "Dell_M1210";
    LCDTarget lcdTarget = LCDTarget.Instance.get(device,
                                                 LCDTarget.Source.i1pro,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.D65,
                                                 LCDTargetBase.Number.Ramp1021, null, null).
        targetFilter.getRamp509From1021();

    //dell用1021求得的效過較差,也慢很多(符合條件的色塊較多)
    //729有最佳結果

    GOGOModelThread model = new GOGOModelThread(lcdTarget);
    double start = System.currentTimeMillis();
    LCDModelBase.Factor[] factors = model.produceFactor();
    System.out.println("use time: " + (System.currentTimeMillis() - start));
    LCDModelFactor lcdModelFactor = model.produceLCDModelFactor(factors);

    /**
     * 此處係數以 序列化 處理,不使用XML儲存是因為懶的處理tag :P
     * 加上Factor間有繼承關係,目前不知道XML-Java工具是否可以很好的處理.
     */
    model.store.modelFactorFile(lcdModelFactor, "t.tmp");

    System.out.println("R:\n" + factors[0]);
    System.out.println("G:\n" + factors[1]);
    System.out.println("B:\n" + factors[2]);

    IterativeReport[] reports = model.getIterativeReports();
    for (IterativeReport r : reports) {
      System.out.println(r.deltaEReport);
    }

    LCDTarget lcdTestTarget = LCDTarget.Instance.get(device,
        LCDTarget.Source.i1pro,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.D65,
        LCDTargetBase.Number.Test4096, null, null);

//    DeltaEReport[] testReports = model.testTarget(lcdTestTarget, factors, true);
    DeltaEReport[] testReports = model.testForwardModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReports));

    DeltaEReport[] testReverseReports = model.testReverseModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReverseReports));

    System.out.println(model.getWhiteDeltaE().getCIE2000DeltaE());
  }

  public CIEXYZ _getXYZ(RGB rgb, LCDModelBase.Factor[] factor) {
    RGB newRGB = getLuminanceRGB(rgb, factor);

    /*
     (Factor factorR = (Factor) factor[0];
         Factor factorG = (Factor) factor[1];
         Factor factorB = (Factor) factor[2];

         double R = GOGO(rgb.R, factorR.gain, factorR.offset, factorR.gamma,
                    factorR.offset2);
         double G = GOGO(rgb.G, factorG.gain, factorG.offset, factorG.gamma,
                    factorG.offset2);
         double B = GOGO(rgb.B, factorB.gain, factorB.offset, factorB.gamma,
                    factorB.offset2);

     RGB newRGB = new RGB(rgb.getRGBColorSpace(), new double[] {R, G, B});
     */
    return matries.RGBToXYZByMaxMatrix(newRGB);
  }

//  public final RGB getNewRGB(RGB rgb) {
//    return getNewRGB(rgb, theModelFactors);
//  }

  protected final RGB getLuminanceRGB(RGB rgb, LCDModelBase.Factor[] factor) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    double R = GOGO(rgb.R, factorR.gain, factorR.offset, factorR.gamma,
                    factorR.offset2);
    double G = GOGO(rgb.G, factorG.gain, factorG.offset, factorG.gamma,
                    factorG.offset2);
    double B = GOGO(rgb.B, factorB.gain, factorB.offset, factorB.gamma,
                    factorB.offset2);

    RGB newRGB = new RGB(rgb.getRGBColorSpace(), new double[] {R, G, B});
    return newRGB;
  }

  /**
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, LCDModelBase.Factor[] factor) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    RGB rgb = this.matries.XYZToRGBByMaxMatrix(XYZ);

    double r = GOGOInverse(rgb.R, factorR.gain, factorR.offset, factorR.gamma,
                           factorR.offset2);
    double g = GOGOInverse(rgb.G, factorG.gain, factorG.offset, factorG.gamma,
                           factorG.offset2);
    double b = GOGOInverse(rgb.B, factorB.gain, factorB.offset, factorB.gamma,
                           factorB.offset2);

    RGB originalRGB = new RGB(RGB.ColorSpace.unknowRGB, new double[] {r, g,
                              b});
    return originalRGB;
  }

  public static class Factor
      extends GOGModelThread.Factor {
    public double offset2;

    public double[] getVariables() {
      return new double[] {
          gain, offset, gamma, offset2};
    }

    public String toString() {
      return "[" + channel + "] gain[" + gain + "] offset[" + offset +
          "] gamma[" + gamma + "] offset2[" + offset2 + "]";
    }

    public Factor() {

    }

    /**
     *
     * @param gain double
     * @param offset double
     * @param gamma double
     * @param offset2 double
     * @param channel Channel
     * @deprecated
     */
    public Factor(double gain, double offset, double gamma, double offset2,
                  RGBBase.Channel channel) {
      super(gain, offset, gamma, channel);
      this.offset2 = offset2;
    }

    public Factor(double[] variables, RGBBase.Channel channel) {
      super(variables, channel);
      this.offset2 = variables[3];
    }
  }

  protected static double GOGOInverse(double fx, double gain, double offset,
                                      double gamma, double offset2) {
    return (Math.pow(fx - offset2, 1. / gamma) - offset) / gain;
  }

  protected static double GOGO(double d, double gain, double offset,
                               double gamma, double offset2) {
    if (gamma == 0 || (gain * d + offset) < 0) {
      return 0;
    }
    else {
      return Math.pow(gain * d + offset, gamma) + offset2;
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

  private class CoefficientsRange
      extends GOGModelThread.CoefficientsRange {

    Range offset2;

    /**
     *
     * @return double[]
     */
    public double[] getStartVariables() {
      return new double[] {
          gain.start, offset.start, gamma.start, offset2.start};
    }

    /**
     *
     * @return double[]
     */
    public double[] getEndVariables() {
      return new double[] {
          gain.end, offset.end, gamma.end, offset2.end};
    }

    public void setStartVariables(double[] start) {
      super.setStartVariables(start);
      offset2.start = start[3];
    }

    public void setEndVariables(double[] end) {
      super.setStartVariables(end);
      offset2.end = end[3];
    }

    CoefficientsRange(Factor factor, CoefficientsRange old) {
      super(factor, old);
      offset2 = Range.determineRange(factor.offset2, old.offset2,
                                     getStepRate(),
                                     getRangeRate());
    }

    CoefficientsRange(double gainS,
                      double gainE,
                      double offsetS,
                      double offsetE,
                      double gammaS,
                      double gammaE,
                      double offset2S,
                      double offset2E,
                      RGBBase.Channel channel) {
      super(gainS, gainE, offsetS, offsetE, gammaS, gammaE, channel);
      offset2 = new Range(offset2S, offset2E, getStepRate());
    }
  }

  /**
   * 初始化迭帶係數
   * 可以視情況調整此係數值,讓模式有更好的運算表現
   */
  public void initCoefficientsRange() {
    CoefficientsRange coefR = new CoefficientsRange(.5, 3., -2., 1., 0., 2.8,
        -2, 1,
        RGBBase.Channel.R);
    CoefficientsRange coefG = new CoefficientsRange(.5, 3., -2., 1., 0., 2.8,
        -2, 1,
        RGBBase.Channel.G);
    CoefficientsRange coefB = new CoefficientsRange(.5, 3., -2., 1., 0., 2.8,
        -2, 1,
        RGBBase.Channel.B);

    gogoCoefficientsRange = new CoefficientsRange[] {
        coefR, coefG, coefB};

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
    CoefficientsRange coef = (CoefficientsRange) coefRange;

    double gainS = coef.gain.start;
    double gainE = coef.gain.end;
    double gainStep = coef.gain.step;

    double offsetS = coef.offset.start;
    double offsetE = coef.offset.end;
    double offsetStep = coef.offset.step;

    double gammaS = coef.gamma.start;
    double gammaE = coef.gamma.end;
    double gammaStep = coef.gamma.step;

    double offset2S = coef.offset2.start;
    double offset2E = coef.offset2.end;
    double offset2Step = coef.offset2.step;

    Factor[] factors = new Factor[] {
        new Factor(), new Factor(), new Factor()};

    //迭代
    for (double gain = gainS; gain <= gainE; gain += gainStep) {
      for (double offset = offsetS; offset <= offsetE; offset += offsetStep) {
        for (double gamma = gammaS; gamma <= gammaE; gamma += gammaStep) {
          for (double offset2 = offset2S; offset2 <= offset2E;
               offset2 += offset2Step) {
            factors[coef.channel.getArrayIndex()] = new Factor(gain, offset,
                gamma,
                offset2,
                coef.channel);
            bestReport = getBestIterativeReport(factors, patchList, whitePatch,
                                                bestReport, coef.channel);
          }
        }
      }
    }
    iterativeReports[coef.channel.getArrayIndex()] = bestReport;
    return bestReport;
  }

}
