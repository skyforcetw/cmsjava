package shu.cms.devicemodel.lcd.thread;

import java.util.*;
import java.util.concurrent.*;

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
 * �䴩�h�u�{�p��,�i�H�^��HT/Duo Core/Duo processor���į�!
 * �K�K�K!
 *
 * �|�Q��SCurveI���B�⵲�G,�@�����N���_�I.
 * �p�G fixedMainCoefA �O false,�|�s�D�n�ѼƤ@�_���N,
 * ��M�t�׷|�C�D�`�h,�ӥB���O�ҷ|����n���ƾ�.
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class SCurveModel2Thread
    extends ChannelDependentModel {

  public SCurveModel2Thread(LCDTarget lcdTarget) {
    super(lcdTarget);
    this.rCorrectorLCDTarget = lcdTarget;
  }

  public SCurveModel2Thread(LCDTarget lcdTarget,
                            LCDTarget rCorrectorLCDTarget) {
    super(lcdTarget);
    this.rCorrectorLCDTarget = rCorrectorLCDTarget;
  }

  /**
   * �ϥμҦ�
   * @param factor LCDModelFactor
   */
  public SCurveModel2Thread(LCDModelFactor factor) {
    super(factor);
  }

  public SCurveModel2Thread(String modelFactorFilename) {
    this( (LCDModelFactor) Load.modelFactorFile(modelFactorFilename));
  }

  protected LCDTarget rCorrectorLCDTarget;

  protected static double f(double x, double alpha, double beta, double C) {
    if (x == 0.) {
      return 0.;
    }
    double result = Math.pow(x, alpha) / (Math.pow(x, beta) + C);
    return result;
  }

  /**
   * first-order derivative of f(x)
   * @param x double
   * @param alpha double
   * @param beta double
   * @param C double
   * @return double
   */
  protected static double f_(double x, double alpha,
                             double beta, double C) {
    if (x == 0.) {
      return 0.;
    }
    double result = ( (alpha - beta) * Math.pow(x, alpha + beta - 1) +
                     alpha * C * Math.pow(x, alpha - 1)) /
        Maths.sqr(Math.pow(x, beta) + C);
    return result;
  }

  public CIEXYZ _getXYZ(RGB rgb, LCDModel.Factor[] factor) {
    Factor factorR = (Factor) factor[0];
    Factor factorG = (Factor) factor[1];
    Factor factorB = (Factor) factor[2];

    double ar = factorR.alpha;
    double br = factorR.beta;
    double Cr = factorR.C;
    double Arr = factorR.Ar;
    double Arg = factorR.Ag;
    double Arb = factorR.Ab;

    double ag = factorG.alpha;
    double bg = factorG.beta;
    double Cg = factorG.C;
    double Agr = factorG.Ar;
    double Agg = factorG.Ag;
    double Agb = factorG.Ab;

    double ab = factorB.alpha;
    double bb = factorB.beta;
    double Cb = factorB.C;
    double Abr = factorB.Ar;
    double Abg = factorB.Ag;
    double Abb = factorB.Ab;

    double fR = f(rgb.R, ar, br, Cr);
    double fR_ = f_(rgb.R, ar, br, Cr);
    double fG = f(rgb.G, ag, bg, Cg);
    double fG_ = f_(rgb.G, ag, bg, Cg);
    double fB = f(rgb.B, ab, bb, Cb);
    double fB_ = f_(rgb.B, ab, bb, Cb);

    double R = Arr * fR + Arg * fG_ + Arb * fB_;
    double G = Agr * fR_ + Agg * fG + Agb * fB_;
    double B = Abr * fR_ + Abg * fG_ + Abb * fB;
    RGB newRGB = new RGB(rgb.getRGBColorSpace(), new double[] {R, G, B},
                         RGB.MaxValue.Double1);
    getXYZRGB = newRGB;
    return matries.RGBToXYZByMaxMatrix(newRGB);
  }

  /**
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, LCDModel.Factor[] factor) {
    return null;
  }

  public static class Factor
      extends LCDModel.Factor {
    RGBBase.Channel channel;
    double Ar;
    double Ag;
    double Ab;
    double alpha;
    double beta;
    double C;

    /**
     *
     * @return double[]
     */
    public double[] getVariables() {
      return null;
    }

    public String toString() {
      return "Ar[" + Ar + "] Ag[" + Ag + "] Ab[" + Ab + "] alpha[" + alpha +
          "] beta[" + beta + "] C[" + C +
          "]";
    }

    public Factor() {

    }

    public Factor(double Ar, double Ag, double Ab, double alpha, double beta,
                  double C,
                  RGBBase.Channel channel) {
      this.Ar = Ar;
      this.Ag = Ag;
      this.Ab = Ab;
      this.alpha = alpha;
      this.beta = beta;
      this.C = C;
      this.channel = channel;
    }

  }

  private class CoefficientsRange {
    RGBBase.Channel channel;

    Range Ar;
    Range Ag;
    Range Ab;

    CoefficientsRange(double ArS,
                      double ArE,
                      double AgS,
                      double AgE,
                      double AbS,
                      double AbE,
                      RGBBase.Channel channel) {

      Ar = new Range(ArS, ArE, getStepRate());
      Ag = new Range(AgS, AgE, getStepRate());
      Ab = new Range(AbS, AbE, getStepRate());

      this.channel = channel;
    }
  }

  public static void main(String[] args) {
    String device = "EIZO_CG221";
    LCDTarget lcdTarget = LCDTarget.Instance.get(device,
                                                 LCDTarget.Source.i1pro,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.D65,
                                                 LCDTargetBase.Number.Test729, null, null);
//    LCDTarget lcdTarget2 = lcdTarget;
    LCDTarget lcdTarget2 = LCDTarget.Instance.get(device,
                                                  LCDTarget.Source.i1pro,
                                                  LCDTarget.Room.Dark,
                                                  LCDTarget.TargetIlluminant.
                                                  D65,
                                                  LCDTargetBase.Number.
                                                  Ramp1021, null, null);
    SCurveModel2Thread model = new SCurveModel2Thread(lcdTarget, lcdTarget2);
    double start = System.currentTimeMillis();
    LCDModel.Factor[] factors = model.produceFactor();
    LCDModelFactor lcdModelFactor = model.produceLCDModelFactor(factors);
    model.store.modelFactorFile(lcdModelFactor, "scurve2.factor");

    System.out.println("use time: " + (System.currentTimeMillis() - start));
    System.out.println(factors[0]);
    System.out.println(factors[1]);
    System.out.println(factors[2]);

    System.out.println(model.getIterativeReport().deltaEReport);

    LCDTarget lcdTestTarget = LCDTarget.Instance.get(device,
        LCDTarget.Source.i1pro,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.D65,
        LCDTargetBase.Number.Test4096, null, null);

    DeltaEReport[] testReports = model.testForwardModel(lcdTestTarget, false);

    System.out.println("Training: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReports));

//    DeltaEReport[] testReverseReports = model.testReverseModel(lcdTestTarget, false);
//    System.out.println("Training: " + lcdTarget.getDescription());
//    System.out.println(Arrays.toString(testReverseReports));

    System.out.println(model.getWhiteDeltaE().getCIE2000DeltaE());
  }

  protected final Factor[] _produceFactor() {
    return _produceFactor(FIXED_MAIN_COEFA);
  }

  protected CoefficientsRange[] coefficientsRange = null;

  public void initCoefficientsRange() {
    //sony
    /*IterativeCoefficient coefR = new IterativeCoefficient(
        3, 5,
        -0.04, 0.04, -0.04, 0.04,
        RGBBase.Channel.R);
           IterativeCoefficient coefG = new IterativeCoefficient(
        -0.04, 0.04,
        3, 5,
        -0.04, 0.04,
        RGBBase.Channel.G);
           IterativeCoefficient coefB = new IterativeCoefficient(
        -0.04, 0.04, -0.04, 0.04,
        4, 6,
        RGBBase.Channel.B);*/

    //dell 729(��4096��)
    CoefficientsRange coefR = new CoefficientsRange(
        3, 7,
        -0.08, 0.08, -0.08, 0.08,
        RGBBase.Channel.R);
    CoefficientsRange coefG = new CoefficientsRange(
        -0.04, 0.04,
        2, 8,
        -0.12, 0.02,
        RGBBase.Channel.G);
    CoefficientsRange coefB = new CoefficientsRange(
        -0.02, 0.06, -0.04, 0.08,
        3, 8,
        RGBBase.Channel.B);

    //dell 4096
    /*IterativeCoefficient coefR = new IterativeCoefficient(
        5, 7,
        -0.04, 0.04, 0.00, 0.08,
        RGBBase.Channel.R);
           IterativeCoefficient coefG = new IterativeCoefficient(
        -0.04, 0.04,
        3, 5,
        -0.11, -0.03,
        RGBBase.Channel.G);
           IterativeCoefficient coefB = new IterativeCoefficient(
        0.02, 0.10, -0.04, 0.04,
        4, 6,
        RGBBase.Channel.B);*/

    coefficientsRange = new CoefficientsRange[] {
        coefR, coefG, coefB};

  }

  public void modifyCoefficientsRange(LCDModel.Factor[] LCDModelFactors,
                                      int iterateIndex) {
    Factor[] factors = toFactorArray(LCDModelFactors);

    for (int x = 0; x < factors.length; x++) {
      coefficientsRange[x].Ar = Range.determineRange(factors[x].Ar,
          coefficientsRange[x].Ar, this);
      coefficientsRange[x].Ag = Range.determineRange(factors[x].Ag,
          coefficientsRange[x].Ag, this);
      coefficientsRange[x].Ab = Range.determineRange(factors[x].Ab,
          coefficientsRange[x].Ab, this);
    }

  }

  /**
   *
   * @param fixedMainCoefA boolean �O�_�T�w�D�n�Y��
   * (���T�w�B���b�O�Ӥ[�F,�ӥB�t��k�W�٦��I���D,�D�X�Ӫ�deltaE�������p)
   * @return Factor[]
   */
  protected Factor[] _produceFactor(boolean fixedMainCoefA) {
    this.fixedMainCoefA = fixedMainCoefA;

    //�p��model1�Ѽ�
    model1 = new SCurveModel1Thread(this.rCorrectorLCDTarget);
    model1.produceFactor();

    //��l�ƭ��N�Y��
    initCoefficientsRange();

    //==========================================================================
    // ���ͰV�m�Φ��
    //==========================================================================
    patchList = lcdTarget.filter.leastOneZeroChannel();
    whitePatch = lcdTarget.getWhitePatch();
    patchList.add(whitePatch);
    CIEXYZ whitePoint = whitePatch.getXYZ();
    patchList = Patch.Produce.LabPatches(patchList, whitePoint);
    //==========================================================================

    //�PThreadCalculator�@�@�p��
    IterativeReport bestIterativeReport = ThreadCalculator.
        produceBestIterativeReport(this);
    iterativeReport = bestIterativeReport;
    //�Y�ƭ��s�]�w
    coefficientsRange = null;
//    this.setTheModelFactors(bestIterativeReport.factors);
    return toFactorArray(bestIterativeReport.factors);
  }

  /**
   * ����B���������ƥ�
   */
  protected int threadCount = ThreadCalculator.THREAD_COUNT;

  /**
   * �O�_�T�w�D�n�Y��
   */
  private final static boolean FIXED_MAIN_COEFA = true;

  /**
   * ���ͭ��N�һ�iterativeCoefficient
   * @param iterativeCoefficient CoefficientsRange[]
   * @param model1Factors Factor[]
   * @return CoefficientsRange[]
   */
  private CoefficientsRange[] produceCoefficientsRange(
      CoefficientsRange[]
      iterativeCoefficient, SCurveModel1Thread.Factor[] model1Factors) {
    CoefficientsRange ARCoefs = iterativeCoefficient[0];
    CoefficientsRange AGCoefs = iterativeCoefficient[1];
    CoefficientsRange ABCoefs = iterativeCoefficient[2];

    SCurveModel1Thread.Factor factorR = model1Factors[RGBBase.Channel.R.
        getArrayIndex()];
    SCurveModel1Thread.Factor factorG = model1Factors[RGBBase.Channel.G.
        getArrayIndex()];
    SCurveModel1Thread.Factor factorB = model1Factors[RGBBase.Channel.B.
        getArrayIndex()];

    //=========================================================================
    //�]�w�D�n�Y��
    //=========================================================================
    double ArrS = 0;
    double ArrE = 0;
    double AggS = 0;
    double AggE = 0;
    double AbbS = 0;
    double AbbE = 0;

    if (fixedMainCoefA) {
      ArrS = ArrE = factorR.A;
      AggS = AggE = factorG.A;
      AbbS = AbbE = factorB.A;
    }
    else {
      ArrS = ARCoefs.Ar.start;
      ArrE = ARCoefs.Ar.end;
      AggS = AGCoefs.Ag.start;
      AggE = AGCoefs.Ag.end;
      AbbS = ABCoefs.Ab.start;
      AbbE = ABCoefs.Ab.end;
    }
    //=========================================================================
    CoefficientsRange AR = new CoefficientsRange(
        ArrS, ArrE,
        ARCoefs.Ag.start, ARCoefs.Ag.end,
        ARCoefs.Ab.start, ARCoefs.Ab.end,
        ARCoefs.channel);

    CoefficientsRange AG = new CoefficientsRange(
        AGCoefs.Ar.start, AGCoefs.Ar.end,
        AggS, AggE,
        AGCoefs.Ab.start, AGCoefs.Ab.end,
        AGCoefs.channel);

    CoefficientsRange AB = new CoefficientsRange(
        ABCoefs.Ar.start, ABCoefs.Ar.end,
        ABCoefs.Ag.start, ABCoefs.Ag.end,
        AbbS, AbbE,
        ABCoefs.channel);

    return new CoefficientsRange[] {
        AR, AG, AB};
  }

  protected static class IterateCoefficient
      implements ThreadCalculator.IterateCoefficient {
    public IterateCoefficient(CoefficientsRange[]
                              coefficientsRange,
                              SCurveModel1Thread.Factor[] model1Factors) {
      this.coefficientsRange = coefficientsRange;
      this.model1Factors = model1Factors;
    }

    CoefficientsRange[]
        coefficientsRange;
    SCurveModel1Thread.Factor[] model1Factors;

  }

  protected List<Patch> patchList;
  protected Patch whitePatch;
  protected SCurveModel1Thread model1;
  protected boolean fixedMainCoefA;

  public IterateCoefficient produceIterateCoefficient() {

    SCurveModel1Thread.Factor[] model1Factors = model1.toFactorArray(model1.
        getModelFactors());

    IterateCoefficient coefs = new IterateCoefficient(
        coefficientsRange,
        model1Factors);

    return coefs;
  }

  public IterativeReport iterateAndReport(ThreadCalculator.IterateCoefficient
                                          coefficient) {
    IterateCoefficient coef = (IterateCoefficient) coefficient;

    //==========================================================================
    //���ͫY��
    //==========================================================================
    CoefficientsRange[] coefs = produceCoefficientsRange(
        coef.coefficientsRange, coef.model1Factors);
    CoefficientsRange ARCoefs = coefs[0];
    double ArrS = ARCoefs.Ar.start;
    double ArrE = ARCoefs.Ar.end;
    double ArrStep = ARCoefs.Ar.step;
    double ArgS = ARCoefs.Ag.start;
    double ArgE = ARCoefs.Ag.end;
    double ArgStep = ARCoefs.Ag.step;
    //==========================================================================

    //==========================================================================
    //�]�w�����
    //==========================================================================
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    List<Future<IterativeReport>>
        futureList = new LinkedList<Future<IterativeReport>> ();
    //==========================================================================

    //==========================================================================
    //�h������B��
    //==========================================================================
    for (double Arr = ArrS; Arr <= ArrE; Arr += ArrStep) {
      for (double Arg = ArgS; Arg <= ArgE;
           Arg += ArgStep) {
        IterativeFactorThread task = new IterativeFactorThread(Arr, Arg, coefs,
            coef.model1Factors, this);
        //�N�B��u�@���Thread pool����������
        Future<IterativeReport> future = executorService.submit(task);
        //�N�B�⵲�G���List��
        futureList.add(future);
      }
    }
    //==========================================================================

    //==========================================================================
    //�N�Ҧ��B�⵲�G��,��X�̨ε��G
    //==========================================================================
    return ThreadCalculator.getBestIterativeReport(futureList, executorService);

  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * ���ѥHThread����¦���B��
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  private class IterativeFactorThread
      extends ThreadCalculator.IterativeFactorThread {
    //r�W�D���ƭȩM�d��
    double Arr;
    double Arg;
    double ArbS;
    double ArbE;
    double ArbStep;

    //g�W�D���d��
    double AgrS;
    double AgrE;
    double AgrStep;
    double AggS;
    double AggE;
    double AggStep;
    double AgbS;
    double AgbE;
    double AgbStep;

    //b�W�D���d��
    double AbrS;
    double AbrE;
    double AbrStep;
    double AbgS;
    double AbgE;
    double AbgStep;
    double AbbS;
    double AbbE;
    double AbbStep;

    //r�W�D��a b C
    double ar;
    double br;
    double Cr;
    //g�W�D��a b C
    double ag;
    double bg;
    double Cg;
    //b�W�D��a b C
    double ab;
    double bb;
    double Cb;

    public IterativeFactorThread(double Arr, double Arg,
                                 CoefficientsRange[] coefficientsRange,
                                 SCurveModel1Thread.Factor[] model1Factors,
                                 LCDModel lcdModel) {
      super(lcdModel);
      this.Arr = Arr;
      this.Arg = Arg;

      CoefficientsRange ARCoefs = coefficientsRange[0];
      CoefficientsRange AGCoefs = coefficientsRange[1];
      CoefficientsRange ABCoefs = coefficientsRange[2];

      //=========================================================================
      //�]�w�Y��
      //=========================================================================
      ArbS = ARCoefs.Ab.start;
      ArbE = ARCoefs.Ab.end;
      ArbStep = ARCoefs.Ab.step;

      AgrS = AGCoefs.Ar.start;
      AgrE = AGCoefs.Ar.end;
      AgrStep = AGCoefs.Ar.step;
      AggS = AGCoefs.Ag.start;
      AggE = AGCoefs.Ag.end;
      AggStep = AGCoefs.Ag.step;
      AgbS = AGCoefs.Ab.start;
      AgbE = AGCoefs.Ab.end;
      AgbStep = AGCoefs.Ab.step;

      AbrS = ABCoefs.Ar.start;
      AbrE = ABCoefs.Ar.end;
      AbrStep = ABCoefs.Ar.step;
      AbgS = ABCoefs.Ag.start;
      AbgE = ABCoefs.Ag.end;
      AbgStep = ABCoefs.Ag.step;
      AbbS = ABCoefs.Ab.start;
      AbbE = ABCoefs.Ab.end;
      AbbStep = ABCoefs.Ab.step;
      //=========================================================================

      SCurveModel1Thread.Factor factorR = model1Factors[RGBBase.Channel.R.
          getArrayIndex()];
      SCurveModel1Thread.Factor factorG = model1Factors[RGBBase.Channel.G.
          getArrayIndex()];
      SCurveModel1Thread.Factor factorB = model1Factors[RGBBase.Channel.B.
          getArrayIndex()];

      //=========================================================================
      //�]�wf�禡���Y��
      //=========================================================================
      ar = factorR.alpha;
      br = factorR.beta;
      Cr = factorR.C;
      ag = factorG.alpha;
      bg = factorG.beta;
      Cg = factorG.C;
      ab = factorB.alpha;
      bb = factorB.beta;
      Cb = factorB.C;
      //=========================================================================
    }

    public IterativeReport call() {
      Factor[] factors = new Factor[3];
      IterativeReport bestReport = null;

      //=========================================================================
      //�j�魡�N
      //=========================================================================
      for (double Arb = ArbS; Arb <= ArbE;
           Arb += ArbStep) {

        for (double Agr = AgrS; Agr <= AgrE;
             Agr += AgrStep) {
          for (double Agg = AggS; Agg <= AggE;
               Agg += AggStep) {
            for (double Agb = AgbS; Agb <= AgbE;
                 Agb += AgbStep) {

              for (double Abr = AbrS; Abr <= AbrE;
                   Abr += AbrStep) {
                for (double Abb = AbbS; Abb <= AbbE;
                     Abb += AbbStep) {
//                  double lastMeasuredDeltaE = 0;
                  for (double Abg = AbgS; Abg <= AbgE;
                       Abg += AbgStep) {

                    //==========================================================
                    //�]�w�Ѽ�
                    //==========================================================
                    factors[RGBBase.Channel.R.getArrayIndex()] = new
                        Factor(Arr, Arg, Arb, ar, br, Cr,
                               RGBBase.Channel.R);

                    factors[RGBBase.Channel.G.getArrayIndex()] = new
                        Factor(Agr, Agg, Agb, ag, bg, Cg,
                               RGBBase.Channel.G);

                    factors[RGBBase.Channel.B.getArrayIndex()] = new
                        Factor(Abr, Abg, Abb, ab, bb, Cb,
                               RGBBase.Channel.B);
                    //==========================================================

                    bestReport = getBestIterativeReport(factors, patchList,
                        whitePatch,
                        DeltaEReport.AnalyzeType.Average,
                        bestReport);

                  }
                }
              }
            }
          }
        }
      }
      //=========================================================================

      return bestReport;
    }

  }

  private Factor[] toFactorArray(LCDModel.Factor[] factors) {
    int size = factors.length;
    Factor[] result = new Factor[size];
    System.arraycopy(factors, 0, result, 0, size);
    return result;
  }

  public int getMaxIterativeTimes() {
    return 10;
  }

  public String getDescription() {
    return "ScurveII";
  }

}
