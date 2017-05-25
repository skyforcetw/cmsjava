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
 * 以GSDF為單位來做預測
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
     * 多項式預測
     */
    Polynomial,
    /**
     * 三次方多項式內插
     */
    CubicInterpolation,
    /**
     * 四次方多項式內插
     */
    QuadraticInterpolation,
    /**
     * 線性內插
     */
    LinearInterpolation,
    /**
     * SPLine的方法內插
     */
    Spline,
    /**
     * 以low-pass過的資訊再內插還原
     */
    LowPass,
    /**
     * 針對GSDF優化的LowPass演算法 (不建議使用)
     */
    GSDFLowPass,
    /**
     * 針對GSDF優化的LowPass演算法, 並且在頻率域做LowPass.
     * 頻率域以DCT作轉換. (不建議使用)
     */
    GSDFLowPassInDCT
  }

  /**
   * low-pass所採用的運算kernel
   */
  private double[] lowPassKernel = new double[] {
      1, 2, 1};

  public GSDFPredicter(GSDFGradientModel gm, ColorProofParameter p) {
    this.turnIndex = p.turnCode;
    this.gm = gm;
  }

  private int turnIndex;

  /**
   * 評估人眼是否覺得smooth的物件, 此外透過此物件來消弭H-K效應
   */
  private GSDFGradientModel gm;

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 承載預測運算結果的資料
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
   * 調整實際以及預測的資料, 讓他們頭尾一致
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
   * 產生GSDF low-pass的預測資料
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
    //從預測的JNDI反推回亮度
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
    //probeDelta及probeSmooth並沒有必要開
    double[] result = filter.lowPass(data, false, false, true, GSDFPart1LowPass,
                                     GSDFPart2LowPass);
    return result;
  }

  /**
   * 進行GSDF lowpass的時候, part1是否進行lowpass
   */
  private static boolean GSDFPart1LowPass = true;
  /**
   * 進行GSDF lowpass的時候, part2是否進行lowpass
   */
  private static boolean GSDFPart2LowPass = true;

  /**
   * 作lowpass的可接受delta
   */
  private static double GSDFLowPassDeltaThreshold = 1;
  /**
   * 作lowpass時的prime可接受threshold
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
   * 處理方式: 內插/低通/在GSDF domain上低通
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
   * 從原始的GSDF以及GSDF的預測加速度, 還原出GSDF
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
    //計算出JNDIndex
    final double[] gsdf = DoubleArray.getRangeCopy(gm.getJNDIndexCurve(),
        start, end);
    //JNDIndex一次微分
    double[] gsdfp = Maths.firstOrderDerivatives(gsdf);

    if (predictType == PredictType.GSDFLowPass) {
      //因為GSDFLowPass本身就是對prime作low pass, 所以不需要再做prime的處理
      predictByPrime = false;
    }

    //gsdf的預測
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
      //如果是對加速度作smooth, 就要從加速度還原回gsdf
      predict = recoverGSDF(gsdf, predict);
      predict = adjustPredictDataAlign(gsdf, predict);
    }
    if (IrregularUtil.isIrregular(predict)) {
      IrregularUtil.irregularFix(predict);
    }
    double[] predictgsdf = predict;

    //==========================================================================
    // prime資料
    //==========================================================================
    //gsdf'的預測
    double[] predictgsdfp = null;
    if (needPrimeData) {
      predictgsdfp = Maths.firstOrderDerivatives(predictgsdf);
    }
    //==========================================================================

    //產生code的資料
    double[] codeArray = DoubleArray.buildX(0, 255, 256);
    PredictData predictData = new PredictData(codeArray, gsdf, gsdfp,
                                              predictgsdf, predictgsdfp);
    return predictData;
  }

  /**
   * 取得GSDF以及預測的GSDF
   * @param start int 預測起始點
   * @param end int 預測終點
   * @param needPrimeData boolean 需要一次微分的資料嗎
   * @param turnIndex int 轉折點
   * @param adjustAlign boolean 需要調整頭尾一致嗎
   * @param predictByPrime boolean
   * @return PredictData
   */
  private PredictData getGSDFAndPredictDataPolynomial(int start, int end,
      boolean needPrimeData, int turnIndex, boolean predictByPrime,
      boolean adjustAlign) {
    //計算出JNDIndex
    final double[] gsdf = DoubleArray.getRangeCopy(gm.getJNDIndexCurve(),
        start, end);
    //JNDIndex一次微分
    double[] gsdfp = Maths.firstOrderDerivatives(gsdf);

    //產生回歸資料
    double[][] regressData = null;
    if (predictByPrime) {
      regressData = DoubleArray.transpose(DoubleArray.buildXY(0,
          gsdfp.length - 1, gsdfp));
    }
    else {
      regressData = DoubleArray.transpose(DoubleArray.buildXY(0,
          gsdf.length - 1, gsdf));
    }

    //gsdf的預測
    /**
     * getRegressPredictData採用兩段分區
     * getRegressPredictData2採用三段分區
     * 分區的依據是基於sRGB, sRGB將11以上及以下分為兩段, 為的是避免11以下採用gamma2.2時,
     * 因為斜率過大造成值過小.
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

    //gsdf'的預測

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
   * 產生預測資料
   * 自動找到最佳的係數, 並且可依lowerOrder的指定決定要不要降係數
   * @param regressData double[][]
   * @param lowerOrder boolean
   * @return double[]
   */
  private final static double[] getRegressPredictData0(double[][] regressData,
      boolean lowerOrder) {
    Polynomial.COEF_1 coef = PolynomialRegression.
        findBestPolynomialCoefficient1(regressData[0], regressData[1]);
    //降低一個order會更不準!?
    if (lowerOrder && coef.hasLowerOrder()) {
      coef = coef.getLowerOrder();
    }
    PolynomialRegression regress = new PolynomialRegression(regressData[0],
        regressData[1], coef);
    regress.regress();

    //產生回歸預測資料
    double[] predict = regress.getMultiPredict(regressData[0]);
    return predict;
  }

  /**
   * 區域重疊的部分有多少
   */
  private final static int CoverRange = 30;
  private final static int Part0End = 10;

  /**
   * 以回歸預測資料, 並且以turnIndex為界分出兩個區段, 以不同的回歸作預測, 提高準確度
   * @param regressData double[][] {input[],output[]}
   * @param turnIndex int
   * @param frontUpperOrder boolean 第一區段提高項次(第一區段指0~轉折點)
   * @return double[]
   * @deprecated
   */
  public final static double[] getRegressPredictData(double[][] regressData,
      int turnIndex, boolean frontUpperOrder) {
    if (turnIndex != -1) {
      //讓他有所重疊, 重疊的區域為
      int size = regressData[0].length;
      Polynomial.COEF_1 coef = PolynomialRegression.
          findBestPolynomialCoefficient1(regressData[0], regressData[1]);
      Polynomial.COEF_1 frontCoef = coef;
      if (frontUpperOrder && frontCoef.hasUpperOrder()) {
        frontCoef = frontCoef.getUpperOrder();
      }

      //========================================================================
      // 第一區段
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
      // 第二區段
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

      //合併
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

    //產生回歸預測資料
    double[] predict = regress.getMultiPredict(regressData[0]);
    return predict;
  }

  /**
   * 以回歸預測資料, 並且以turnIndex為界分出三個區段, 以不同的回歸作預測, 提高準確度
   * 包括: 第一區段0~11
   *      第二區段11~轉折點
   *      第三區段轉折點~255
   * @param regressData double[][]
   * @param turnIndex int
   * @param frontUpperOrder boolean
   * @return double[]
   */
  private final static double[] getRegressPredictData3Part(double[][]
      regressData,
      int turnIndex, boolean frontUpperOrder) {
    if (turnIndex != -1) {
      //讓他有所重疊, 重疊的區域為
      int size = regressData[0].length;
      Polynomial.COEF_1 coef = PolynomialRegression.
          findBestPolynomialCoefficient1(regressData[0], regressData[1]);
      Polynomial.COEF_1 frontCoef = coef;
      if (frontUpperOrder && frontCoef.hasUpperOrder()) {
        frontCoef = frontCoef.getUpperOrder();
      }

      //========================================================================
      // 第零區段
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
      // 第一區段
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
      // 第二區段
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

      //合併
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
