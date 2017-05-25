package shu.cms.devicemodel.lcd.thread;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �ĥΥ[�j�����Y�ƭ��N�k
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GOGModelThread
    extends ChannelIndependentModel {

  protected CoefficientsRange[] coefficientsRange = null;

  public GOGModelThread(LCDTarget lcdTarget) {
    super(lcdTarget);
    this.rational.setDoRGBRational(true);
  }

  /**
   * �ϥμҦ�
   * @param factor LCDModelFactor
   */
  public GOGModelThread(LCDModelFactor factor) {
    super(factor);
    this.rational.setDoRGBRational(true);
  }

  public GOGModelThread(String modelFactorFilename) {
    this( (LCDModelFactor) Load.modelFactorFile(modelFactorFilename));
  }

  protected Factor[] _produceFactor() {
    initCoefficientsRange();

    //==========================================================================
    //�h������B��
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
    /*LCDTarget.Device device = LCDTarget.Device.Dell_M1210;
         LCDTarget lcdTarget = LCDTarget.Instance.getInstance(device,
                                                LCDTarget.Source.i1pro,
                                                LCDTarget.Room.Dark,
                                                LCDTarget.TargetIlluminant.D65,
     LCDTargetBase.Number.Patch1021);*/
    LCDTarget lcdTarget = LCDTarget.Instance.get("CPT_17inch_Demo2",
                                                 LCDTarget.Source.CA210,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.
                                                 Native,
                                                 LCDTargetBase.Number.Ramp1792, null, null);
//    LCDTarget testLCDTarget = trainingLCDTarget;

    //dell��1021�D�o���ĹL���t,�]�C�ܦh(�ŦX���󪺦�����h)
    //729���̨ε��G

    GOGModelThread model = new GOGModelThread(
        lcdTarget);
    double start = System.currentTimeMillis();
    LCDModelBase.Factor[] factors = model.produceFactor();
    System.out.println("use time: " + (System.currentTimeMillis() - start));
    LCDModelFactor lcdModelFactor = model.produceLCDModelFactor(factors);

    /**
     * ���B�Y�ƥH �ǦC�� �B�z,���ϥ�XML�x�s�O�]���i���B�ztag :P
     * �[�WFactor�����~�����Y,�ثe�����DXML-Java�u��O�_�i�H�ܦn���B�z.
     */
//    Utils.writeObject(lcdModelFactor, "gog.factor");

    System.out.println("R:\n" + factors[0]);
    System.out.println("G:\n" + factors[1]);
    System.out.println("B:\n" + factors[2]);

    IterativeReport[] reports = model.getIterativeReports();
    for (IterativeReport r : reports) {
      System.out.println(r.deltaEReport);
    }

    /*LCDTarget lcdTestTarget = LCDTarget.Instance.getInstance(device,
        LCDTarget.Source.i1pro,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.D65,
        LCDTargetBase.Number.Patch4096);*/
    LCDTarget lcdTestTarget = LCDTarget.Instance.getFromVastView(
        "RGBCMYW_original.txt", LCDTargetBase.Number.Ramp1024);

    DeltaEReport[] testReports = model.testForwardModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReports));

    DeltaEReport[] testReverseReports = model.testReverseModel(lcdTestTarget, false);
    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReverseReports));

    System.out.println(model.getWhiteDeltaE().getCIE2000DeltaE());
//    System.out.println(model.getWhite());
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

    double r = GOGInverse(rgb.R, factorR.gain, factorR.offset, factorR.gamma);
    double g = GOGInverse(rgb.G, factorG.gain, factorG.offset, factorG.gamma);
    double b = GOGInverse(rgb.B, factorB.gain, factorB.offset, factorB.gamma);

    RGB originalRGB = new RGB(RGB.ColorSpace.unknowRGB, new double[] {r, g,
                              b});
    return originalRGB;
  }

  protected CIEXYZ _getXYZ(RGB rgb, LCDModelBase.Factor[] factor) {

    RGB newRGB = getLuminanceRGB(rgb, factor);
    getXYZRGB = newRGB;
    return matries.RGBToXYZByMaxMatrix(newRGB);
  }

  protected RGB getLuminanceRGB(RGB rgb, LCDModelBase.Factor[] factor) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    double R = GOG(rgb.R, factorR.gain, factorR.offset, factorR.gamma);
    double G = GOG(rgb.G, factorG.gain, factorG.offset, factorG.gamma);
    double B = GOG(rgb.B, factorB.gain, factorB.offset, factorB.gamma);

    RGB newRGB = new RGB(rgb.getRGBColorSpace(), new double[] {R, G, B});
    return newRGB;
  }

  public static class Factor
      extends LCDModelBase.Factor {
    RGBBase.Channel channel;
    public double gain;
    public double offset;
    public double gamma;

    public GammaCorrector rCorrector;

    public double[] getVariables() {
      return new double[] {
          gain, offset, gamma};
    }

    public String toString() {
      return "[" + channel + "] gain[" + gain + "] offset[" + offset +
          "] gamma[" +
          gamma + "]";
    }

    public Factor() {

    }

    public Factor(double gain, double offset, double gamma,
                  RGBBase.Channel channel) {
      this.gain = gain;
      this.offset = offset;
      this.gamma = gamma;
      this.channel = channel;
    }

    public Factor(double[] variables, RGBBase.Channel channel) {
      this.gain = variables[0];
      this.offset = variables[1];
      this.gamma = variables[2];
      this.channel = channel;
    }

    public Factor(double[] variables, GammaCorrector rCorrector,
                  RGBBase.Channel channel) {
      this(variables, channel);
      this.rCorrector = rCorrector;
    }

    public Factor(GammaCorrector rCorrector) {
      this.rCorrector = rCorrector;
    }

  }

  protected final static double GOGInverse(double fx, double gain,
                                           double offset,
                                           double gamma) {
    double value = Math.pow(fx, 1. / gamma);
//    if (value < 0) {
//      return 0;
//    }
//    else {
    return (value - offset) / gain;
//    }
  }

  protected final static double GOG(double d, double gain, double offset,
                                    double gamma) {
    double value = gain * d + offset;
    if (gamma == 0 || value < 0) {
      return 0;
    }
    else {
      return Math.pow(value, gamma);
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

  protected class CoefficientsRange
      extends SimpleThreadCalculator.CoefficientsRange {

    public String toString() {
      return "gain[" + gain + "] offset[" + offset + "] gamma[" + gamma + "]";
    }

    Range gain;
    Range offset;
    Range gamma;

    /**
     *
     * @return double[]
     */
    public double[] getStartVariables() {
      return new double[] {
          gain.start, offset.start, gamma.start};
    }

    /**
     *
     * @return double[]
     */
    public double[] getEndVariables() {
      return new double[] {
          gain.end, offset.end, gamma.end};
    }

    public void setStartVariables(double[] start) {
      gain.start = start[0];
      offset.start = start[1];
      gamma.start = start[2];
    }

    public void setEndVariables(double[] end) {
      gain.end = end[0];
      offset.end = end[1];
      gamma.end = end[2];
    }

    CoefficientsRange(Factor factor, CoefficientsRange old) {
      gain = Range.determineRange(factor.gain, old.gain, getStepRate(),
                                  getRangeRate());

      offset = Range.determineRange(factor.offset, old.offset,
                                    getStepRate(),
                                    getRangeRate());

      gamma = Range.determineRange(factor.gamma, old.gamma,
                                   getStepRate(),
                                   getRangeRate());

      this.channel = factor.channel;
    }

    CoefficientsRange(double gainS,
                      double gainE,
                      double offsetS,
                      double offsetE,
                      double gammaS,
                      double gammaE,
                      RGBBase.Channel channel) {

      gain = new Range(gainS, gainE, getStepRate());
      offset = new Range(offsetS, offsetE, getStepRate());
      gamma = new Range(gammaS, gammaE, getStepRate());

      this.channel = channel;
    }
  }

  public int getMaxIterativeTimes() {
    return 15;
  }

  /**
   * ��l�ƭ��a�Y��
   * �i�H�����p�վ㦹�Y�ƭ�,���Ҧ�����n���B���{
   */
  public void initCoefficientsRange() {
    if (coefficientsRange == null) {
      CoefficientsRange coefR = new CoefficientsRange(.5, 3, -.2, 1., 0, 2.8,
          RGBBase.Channel.R);
      CoefficientsRange coefG = new CoefficientsRange(.5, 3, -.2, 1., 0, 2.8,
          RGBBase.Channel.G);
      CoefficientsRange coefB = new CoefficientsRange(.5, 3, -.2, 1., 0, 2.8,
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

    Factor[] factors = new Factor[] {
        new Factor(), new Factor(), new Factor()};

    //���N
    for (double gain = gainS; gain <= gainE; gain += gainStep) {
      for (double offset = offsetS; offset <= offsetE; offset += offsetStep) {
        for (double gamma = gammaS; gamma <= gammaE; gamma += gammaStep) {
          factors[coef.channel.getArrayIndex()] = new Factor(gain, offset,
              gamma,
              coef.channel);
          bestReport = getBestIterativeReport(factors, patchList, whitePatch,
                                              bestReport, coef.channel);

        }
      }
    }
//    System.out.println(Arrays.toString(factors));
    iterativeReports[coef.channel.getArrayIndex()] = bestReport;
    return bestReport;
  }

  public String getDescription() {
    return "GOG";
  }

}
