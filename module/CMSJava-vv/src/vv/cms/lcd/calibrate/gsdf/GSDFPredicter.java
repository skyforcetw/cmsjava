package vv.cms.lcd.calibrate.gsdf;

import shu.cms.hvs.gradient.*;
import shu.cms.lcd.*;
import vv.cms.lcd.calibrate.*;
import vv.cms.lcd.calibrate.parameter.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.regress.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �HGSDF�����Ӱ��w��
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class GSDFPredicter
    implements Plottable {

  private boolean plotting = false;

  public void setPlotting(boolean plotting) {
    this.plotting = plotting;
  }

  public static enum PredictMethod {
    /**
     * �h�����w��
     */
    Polynomial,
    /**
     * �T����h��������
     */
    CubicInterpolation,
    /**
     * �|����h��������
     */
    QuadraticInterpolation,
    /**
     * �u�ʤ���
     */
    LinearInterpolation,
    /**
     * SPLine����k����
     */
    Spline,
    /**
     * �Hlow-pass�L����T�A�����٭�
     */
    LowPass,
    /**
     * �w��GSDF�u�ƪ�LowPass�t��k (����ĳ�ϥ�)
     */
    GSDFLowPass,
    /**
     * �w��GSDF�u�ƪ�LowPass�t��k, �åB�b�W�v�찵LowPass.
     * �W�v��HDCT�@�ഫ. (����ĳ�ϥ�)
     */
    GSDFLowPassInDCT
  }

  /**
   * low-pass�ұĥΪ��B��kernel
   */
  private double[] lowPassKernel = new double[] {
      1, 2, 1};

  public GSDFPredicter(GSDFGradientModel gm, ColorProofParameter p) {
    this.turnIndex = p.turnCode;
    this.gm = gm;
  }

  private int turnIndex;

  /**
   * �����H���O�_ı�osmooth������, ���~�z�L������Ӯ���H-K����
   */
  private GSDFGradientModel gm;

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * �Ӹ��w���B�⵲�G�����
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public final static class PredictData {
    /**
     *
     * @param code double[]
     * @param gsdf double[]
     * @param gsdfPrime double[]
     * @param predictGSDF double[]
     * @param primeOfPredictGSDF double[]
     */
    protected PredictData(double[] code, double[] gsdf, double[] gsdfPrime,
                          double[] predictGSDF,
                          double[] primeOfPredictGSDF) {
      this.code = code;
      this.gsdf = gsdf;
      this.gsdfPrime = gsdfPrime;
      this.predictGSDF = predictGSDF;
      this.primeOfPredictGSDF = primeOfPredictGSDF;
    }

    public double[] code;
    public double[] gsdf;
    public double[] gsdfPrime;
    public double[] predictGSDF;
    public double[] predictLuminance;
    public double[] primeOfPredictGSDF;
    public PredictMethod predictMethod;
  }

  /**
   * �վ��ڥH�ιw�������, ���L���Y���@�P
   * @param actual double[]
   * @param predict double[]
   * @return double[]
   */
  final static double[] adjustPredictDataAlign(final double[] actual,
                                               final double[] predict) {
    if (actual.length != predict.length) {
      throw new IllegalArgumentException("actual.length != predict.length");
    }
    double[] adjust = DoubleArray.minus(predict, predict[0]);
    int size = actual.length;
    double ratio = (actual[size - 1] - actual[0]) / adjust[size - 1];
    adjust = DoubleArray.times(adjust, ratio);
    adjust = DoubleArray.plus(adjust, actual[0]);
    return adjust;
  }

  /**
   *
   * @param start int
   * @param end int
   * @param needPrimeData boolean
   * @param method PredictMethod
   * @param predictByPrime boolean
   * @return PredictData
   */
  public PredictData getGSDFAndPredictData(int start, int end,
                                           boolean needPrimeData,
                                           PredictMethod method,
                                           boolean predictByPrime) {
    PredictData predictData = null;
    switch (method) {
      case Polynomial:
        predictData = getGSDFAndPredictDataPolynomial(start, end, needPrimeData,
            predictByPrime);
        break;
      case LinearInterpolation:
        predictData = getGSDFAndPredictData(start, end, needPrimeData,
                                            predictByPrime,
                                            PredictType.Interpolation,
                                            Interpolation.Algo.Linear);
        break;
      case CubicInterpolation:
        predictData = getGSDFAndPredictData(start, end, needPrimeData,
                                            predictByPrime,
                                            PredictType.Interpolation,
                                            Interpolation.Algo.CubicPolynomial);
        break;
      case QuadraticInterpolation:
        predictData = getGSDFAndPredictData(start, end, needPrimeData,
                                            predictByPrime,
                                            PredictType.Interpolation,
                                            Interpolation.Algo.
                                            QuadraticPolynomial);
        break;
      case Spline:
        predictData = getGSDFAndPredictData(start, end, needPrimeData,
                                            predictByPrime,
                                            PredictType.Interpolation,
                                            Interpolation.Algo.Spline2);
        break;
      case LowPass:
        predictData = getGSDFAndPredictData(start, end, needPrimeData,
                                            predictByPrime,
                                            PredictType.LowPass, null);
        break;
      case GSDFLowPass:
        predictData = getGSDFAndPredictData(start, end, needPrimeData,
                                            predictByPrime,
                                            PredictType.GSDFLowPass, null);
        break;
    }
    predictData.predictMethod = method;
    return predictData;
  }

  /**
   *
   * @param start int
   * @param end int
   * @param needPrimeData boolean
   * @return PredictData
   * @deprecated
   */
  public PredictData getGSDFAndPredictDataByGSDFLowPass(int start, int end,
      boolean needPrimeData) {
    return getGSDFAndPredictData(start, end, needPrimeData,
                                 PredictMethod.GSDFLowPass, false);
  }

  /**
   * ����GSDF low-pass���w�����
   * @param lcdTarget LCDTarget
   * @param p Parameter
   * @param plot boolean
   * @return PredictData
   * @deprecated
   */
  public final static GSDFPredicter.PredictData getGSDFLowPassPredictData(
      LCDTarget lcdTarget, ColorProofParameter p, boolean plot) {

    GSDFGradientModel gm = new GSDFGradientModel(lcdTarget);
    GSDFPredicter predicter = new GSDFPredicter(gm, p);
    predicter.setPlotting(plot);
    GSDFPredicter.PredictData pdata = predicter.
        getGSDFAndPredictDataByGSDFLowPass(0, 255, false);
    //�q�w����JNDI�ϱ��^�G��
    double[] predictLumiCurve = gm.getLuminanceCurve(pdata.predictGSDF);
    pdata.predictLuminance = predictLumiCurve;
    return pdata;
  }

  private PredictData getGSDFAndPredictDataPolynomial(int start, int end,
      boolean needPrimeData, boolean predictByPrime) {
    return getGSDFAndPredictDataPolynomial(start, end, needPrimeData,
                                           turnIndex, predictByPrime, true);
  }

  /**
   *
   * @param data double[]
   * @return double[]
   * @deprecated
   */
  private double[] predictDataByGSDFLowPass(double[] data) {
    GSDFLowPassFilter filter = new GSDFLowPassFilter();
    filter.setPlotting(this.plotting);
    filter.setDeltaThreshold(this.GSDFLowPassDeltaThreshold);
    filter.setPrimeCheckThreshold(this.GSDFLowPassPrimeCheckThreshold);
    //probeDelta��probeSmooth�èS�����n�}
    double[] result = filter.lowPass(data, false, false, true, GSDFPart1LowPass,
                                     GSDFPart2LowPass);
    return result;
  }

  /**
   * �i��GSDF lowpass���ɭ�, part1�O�_�i��lowpass
   */
  private static boolean GSDFPart1LowPass = true;
  /**
   * �i��GSDF lowpass���ɭ�, part2�O�_�i��lowpass
   */
  private static boolean GSDFPart2LowPass = true;

  /**
   * �@lowpass���i����delta
   */
  private static double GSDFLowPassDeltaThreshold = 1;
  /**
   * �@lowpass�ɪ�prime�i����threshold
   */
  private static double GSDFLowPassPrimeCheckThreshold = 0.1;

  public final static void setGSDFLowPassPrimeCheckThreshold(double threshold) {
    GSDFLowPassPrimeCheckThreshold = threshold;
  }

  private static double[] predictDataByLowPass(double[] data, double[] kernel) {
    double[] result = Convolution.convole(data, kernel);
    return result;
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * �B�z�覡: ����/�C�q/�bGSDF domain�W�C�q
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author not attributable
   * @version 1.0
   */
  private static enum PredictType {
    Interpolation, LowPass, GSDFLowPass
  }

  /**
   * �q��l��GSDF�H��GSDF���w���[�t��, �٭�XGSDF
   * @param originalGSDF double[]
   * @param predcitGSDFPrime double[]
   * @return double[]
   */
  final static double[] recoverGSDF(double[] originalGSDF,
                                    double[] predcitGSDFPrime) {
    int size = originalGSDF.length;
    double[] predictGSDF = new double[size];
    predictGSDF[0] = originalGSDF[0];
    for (int x = 1; x < size; x++) {
      predictGSDF[x] = predictGSDF[x - 1] + predcitGSDFPrime[x - 1];
    }
    return predictGSDF;
  }

  /**
   *
   * @param start int
   * @param end int
   * @param needPrimeData boolean
   * @param predictByPrime boolean
   * @param predictType PredictType
   * @param algo Algo
   * @return PredictData
   */
  private PredictData getGSDFAndPredictData(int start,
                                            int end, boolean needPrimeData,
                                            boolean predictByPrime,
                                            PredictType predictType,
                                            Interpolation.Algo algo) {
    //�p��XJNDIndex
    final double[] gsdf = DoubleArray.getRangeCopy(gm.getJNDIndexCurve(),
        start, end);
    //JNDIndex�@���L��
    double[] gsdfp = Maths.firstOrderDerivatives(gsdf);

    if (predictType == PredictType.GSDFLowPass) {
      //�]��GSDFLowPass�����N�O��prime�@low pass, �ҥH���ݭn�A��prime���B�z
      predictByPrime = false;
    }

    //gsdf���w��
    double[] predict = predictByPrime ? gsdfp : gsdf;

    switch (predictType) {
      case Interpolation:
        predict = CalibrateUtils.predictDataByInterpolation(predict, algo);
        break;
      case LowPass:
        predict = predictDataByLowPass(predict, lowPassKernel);
        break;
      case GSDFLowPass:
        predict = predictDataByGSDFLowPass(predict);
        break;
    }

    if (predictByPrime) {
      //�p�G�O��[�t�ק@smooth, �N�n�q�[�t���٭�^gsdf
      predict = recoverGSDF(gsdf, predict);
      predict = adjustPredictDataAlign(gsdf, predict);
    }
    if (IrregularUtil.isIrregular(predict)) {
      IrregularUtil.irregularFix(predict);
    }
    double[] predictgsdf = predict;

    //==========================================================================
    // prime���
    //==========================================================================
    //gsdf'���w��
    double[] predictgsdfp = null;
    if (needPrimeData) {
      predictgsdfp = Maths.firstOrderDerivatives(predictgsdf);
    }
    //==========================================================================

    //����code�����
    double[] codeArray = DoubleArray.buildX(0, 255, 256);
    PredictData predictData = new PredictData(codeArray, gsdf, gsdfp,
                                              predictgsdf, predictgsdfp);
    return predictData;
  }

  /**
   * ���oGSDF�H�ιw����GSDF
   * @param start int �w���_�l�I
   * @param end int �w�����I
   * @param needPrimeData boolean �ݭn�@���L������ƶ�
   * @param turnIndex int ����I
   * @param adjustAlign boolean �ݭn�վ��Y���@�P��
   * @param predictByPrime boolean
   * @return PredictData
   */
  private PredictData getGSDFAndPredictDataPolynomial(int start, int end,
      boolean needPrimeData, int turnIndex, boolean predictByPrime,
      boolean adjustAlign) {
    //�p��XJNDIndex
    final double[] gsdf = DoubleArray.getRangeCopy(gm.getJNDIndexCurve(),
        start, end);
    //JNDIndex�@���L��
    double[] gsdfp = Maths.firstOrderDerivatives(gsdf);

    //���ͦ^�k���
    double[][] regressData = null;
    if (predictByPrime) {
      regressData = DoubleArray.transpose(DoubleArray.buildXY(0,
          gsdfp.length - 1, gsdfp));
    }
    else {
      regressData = DoubleArray.transpose(DoubleArray.buildXY(0,
          gsdf.length - 1, gsdf));
    }

    //gsdf���w��
    /**
     * getRegressPredictData�ĥΨ�q����
     * getRegressPredictData2�ĥΤT�q����
     * ���Ϫ��̾ڬO���sRGB, sRGB�N11�H�W�ΥH�U������q, �����O�קK11�H�U�ĥ�gamma2.2��,
     * �]���ײv�L�j�y���ȹL�p.
     */
    //    double[] predict = getRegressPredictData(regressData, turnIndex, false);
    double[] predict = getRegressPredictData3Part(regressData, turnIndex, false);

//    if (true) {
//      Plot2D p = Plot2D.getInstance("gsdfp");
//      p.addLinePlot("predict", 0, predict.length, predict);
//      p.addLinePlot("gsdfp", 0, gsdfp.length, gsdfp);
//      p.setVisible();
//    }

    if (predictByPrime) {
      predict = recoverGSDF(gsdf, predict);
    }
    if (IrregularUtil.isIrregular(predict)) {
      IrregularUtil.irregularFix(predict);
    }
//    reverseFixedPredictData(predict);
    if (adjustAlign) {
      predict = adjustPredictDataAlign(gsdf, predict);
    }

//    if (true) {
//      Plot2D p = Plot2D.getInstance("gsdf");
//      p.addLinePlot("predict", 0, predict.length, predict);
//      p.addLinePlot("gsdf", 0, gsdf.length, gsdf);
//      p.setVisible();
//    }

    //gsdf'���w��

    double[] predictgsdfp = null;

    if (needPrimeData) {
      predictgsdfp = Maths.firstOrderDerivatives(predict);
    }

    double[] codeArray = DoubleArray.buildX(0, gsdf.length - 1, gsdf.length);
    PredictData predictData = new PredictData(codeArray, gsdf, gsdfp,
                                              predict, predictgsdfp);
    return predictData;
  }

  /**
   * ���͹w�����
   * �۰ʧ��̨Ϊ��Y��, �åB�i��lowerOrder�����w�M�w�n���n���Y��
   * @param regressData double[][]
   * @param lowerOrder boolean
   * @return double[]
   */
  private final static double[] getRegressPredictData0(double[][] regressData,
      boolean lowerOrder) {
    Polynomial.COEF_1 coef = PolynomialRegression.
        findBestPolynomialCoefficient1(regressData[0], regressData[1]);
    //���C�@��order�|�󤣷�!?
    if (lowerOrder && coef.hasLowerOrder()) {
      coef = coef.getLowerOrder();
    }
    PolynomialRegression regress = new PolynomialRegression(regressData[0],
        regressData[1], coef);
    regress.regress();

    //���ͦ^�k�w�����
    double[] predict = regress.getMultiPredict(regressData[0]);
    return predict;
  }

  /**
   * �ϰ쭫�|���������h��
   */
  private final static int CoverRange = 30;
  private final static int Part0End = 10;

  /**
   * �H�^�k�w�����, �åB�HturnIndex���ɤ��X��ӰϬq, �H���P���^�k�@�w��, �����ǽT��
   * @param regressData double[][] {input[],output[]}
   * @param turnIndex int
   * @param frontUpperOrder boolean �Ĥ@�Ϭq��������(�Ĥ@�Ϭq��0~����I)
   * @return double[]
   * @deprecated
   */
  public final static double[] getRegressPredictData(double[][] regressData,
      int turnIndex, boolean frontUpperOrder) {
    if (turnIndex != -1) {
      //���L���ҭ��|, ���|���ϰ쬰
      int size = regressData[0].length;
      Polynomial.COEF_1 coef = PolynomialRegression.
          findBestPolynomialCoefficient1(regressData[0], regressData[1]);
      Polynomial.COEF_1 frontCoef = coef;
      if (frontUpperOrder && frontCoef.hasUpperOrder()) {
        frontCoef = frontCoef.getUpperOrder();
      }

      //========================================================================
      // �Ĥ@�Ϭq
      //========================================================================
      double[][] regressData1 = DoubleArray.getColumnsRangeCopy(regressData, 0,
          turnIndex + CoverRange);
      double[] predict1 = getRegressPredictData0(regressData1, frontCoef);
      predict1 = DoubleArray.getRangeCopy(predict1, 0,
                                          predict1.length - 1 - CoverRange);
      double[] actual1 = DoubleArray.getRangeCopy(regressData1[1], 0,
                                                  regressData1[1].length - 1 -
                                                  CoverRange);
      predict1 = adjustPredictDataAlign(actual1, predict1);
      //========================================================================

      //========================================================================
      // �ĤG�Ϭq
      //========================================================================
      double[][] regressData2 = DoubleArray.getColumnsRangeCopy(regressData,
          turnIndex - CoverRange, size - 1);
      double[] predict2 = getRegressPredictData0(regressData2, coef);
      predict2 = DoubleArray.getRangeCopy(predict2, 1 + CoverRange,
                                          predict2.length - 1);
      double[] actual2 = DoubleArray.getRangeCopy(regressData2[1],
                                                  1 + CoverRange,
                                                  regressData2[1].length - 1);
      predict2 = adjustPredictDataAlign(actual2, predict2);
      //========================================================================

      //�X��
      double[] predict = DoubleArray.merge(predict1, predict2);
      return predict;
    }
    else {
      return getRegressPredictData0(regressData, false);
    }

  }

  private final static double[] getRegressPredictData0(double[][] regressData,
      Polynomial.COEF_1 coef) {
    PolynomialRegression regress = new PolynomialRegression(regressData[0],
        regressData[1], coef);
    regress.regress();

    //���ͦ^�k�w�����
    double[] predict = regress.getMultiPredict(regressData[0]);
    return predict;
  }

  /**
   * �H�^�k�w�����, �åB�HturnIndex���ɤ��X�T�ӰϬq, �H���P���^�k�@�w��, �����ǽT��
   * �]�A: �Ĥ@�Ϭq0~11
   *      �ĤG�Ϭq11~����I
   *      �ĤT�Ϭq����I~255
   * @param regressData double[][]
   * @param turnIndex int
   * @param frontUpperOrder boolean
   * @return double[]
   */
  private final static double[] getRegressPredictData3Part(double[][]
      regressData,
      int turnIndex, boolean frontUpperOrder) {
    if (turnIndex != -1) {
      //���L���ҭ��|, ���|���ϰ쬰
      int size = regressData[0].length;
      Polynomial.COEF_1 coef = PolynomialRegression.
          findBestPolynomialCoefficient1(regressData[0], regressData[1]);
      Polynomial.COEF_1 frontCoef = coef;
      if (frontUpperOrder && frontCoef.hasUpperOrder()) {
        frontCoef = frontCoef.getUpperOrder();
      }

      //========================================================================
      // �Ĺs�Ϭq
      //========================================================================
      double[][] regressData0 = DoubleArray.getColumnsRangeCopy(regressData, 0,
          Part0End);
      double[] predict0 = getRegressPredictData0(regressData0, frontCoef);
      predict0 = DoubleArray.getRangeCopy(predict0, 0,
                                          predict0.length - 1);
      double[] actual0 = DoubleArray.getRangeCopy(regressData0[1], 0,
                                                  regressData0[1].length - 1);
      predict0 = adjustPredictDataAlign(actual0, predict0);
      //========================================================================

      //========================================================================
      // �Ĥ@�Ϭq
      //========================================================================
      double[][] regressData1 = DoubleArray.getColumnsRangeCopy(regressData, 11,
          turnIndex + CoverRange);
      double[] predict1 = getRegressPredictData0(regressData1, frontCoef);
      predict1 = DoubleArray.getRangeCopy(predict1, 0,
                                          predict1.length - 1 - CoverRange);
      double[] actual1 = DoubleArray.getRangeCopy(regressData1[1], 0,
                                                  regressData1[1].length - 1 -
                                                  CoverRange);
      predict1 = adjustPredictDataAlign(actual1, predict1);
      //========================================================================

      //========================================================================
      // �ĤG�Ϭq
      //========================================================================
      double[][] regressData2 = DoubleArray.getColumnsRangeCopy(regressData,
          turnIndex - CoverRange, size - 1);
      double[] predict2 = getRegressPredictData0(regressData2, coef);
      predict2 = DoubleArray.getRangeCopy(predict2, 1 + CoverRange,
                                          predict2.length - 1);
      double[] actual2 = DoubleArray.getRangeCopy(regressData2[1],
                                                  1 + CoverRange,
                                                  regressData2[1].length - 1);
      predict2 = adjustPredictDataAlign(actual2, predict2);
      //========================================================================

      //�X��
      double[] predict = DoubleArray.merge(predict0,
                                           DoubleArray.merge(predict1, predict2));
      return predict;
    }
    else {
      return getRegressPredictData0(regressData, false);
    }

  }

  /**
   *
   * @param lowPassKernel double[]
   * @deprecated
   */
  public void setLowPassKernel(double[] lowPassKernel) {
    this.lowPassKernel = lowPassKernel;
  }

  /**
   *
   * @param GSDFPart2 boolean
   * @deprecated
   */
  public static void setGSDFPart2LowPass(boolean GSDFPart2) {
    GSDFPart2LowPass = GSDFPart2;
  }

  /**
   *
   * @param GSDFPart1 boolean
   * @deprecated
   */
  public static void setGSDFPart1LowPass(boolean GSDFPart1) {
    GSDFPart1LowPass = GSDFPart1;
  }

  /**
   *
   * @param deltaThreshold double
   * @deprecated
   */
  public static void setGSDFLowPassDeltaThreshold(double deltaThreshold) {
    GSDFLowPassDeltaThreshold = deltaThreshold;
  }

}
