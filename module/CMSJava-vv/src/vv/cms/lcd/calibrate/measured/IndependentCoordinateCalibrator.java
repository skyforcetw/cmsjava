package vv.cms.lcd.calibrate.measured;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.devicemodel.lcd.util.*;
import shu.cms.lcd.*;
import vv.cms.lcd.calibrate.*;
import vv.cms.lcd.calibrate.gsdf.*;
import vv.cms.lcd.calibrate.measured.find.*;
import vv.cms.lcd.calibrate.measured.util.*;
import vv.cms.lcd.calibrate.parameter.*;
import shu.cms.lcd.material.*;
import shu.cms.measure.*;
import shu.cms.plot.*;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.regress.*;
import shu.util.log.*;
import vv.cms.lcd.material.*;

//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 1. 首先針對亮度調整, 將白往目標亮度調整一步.
 * 2. 調整後接下來調整R/B一步, 使色度接近目標值,
 * 3. 再回到1,2不斷循環, 直到兩個再也不需要調整.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class IndependentCoordinateCalibrator
    extends CoordinateCalibrator {

  public IndependentCoordinateCalibrator(LCDTarget logoLCDTaget,
                                         LCDTarget originalRampLCDTarget,
                                         MeterMeasurement meterMeasurement,
                                         ColorProofParameter p,
                                         WhiteParameter wp,
                                         AdjustParameter ap,
                                         MeasureParameter mp) {
    super(logoLCDTaget, meterMeasurement, p, wp, ap, mp);
    this.setChromaticExpandForChromaAround(true);
    this.originalRampLCDTarget = originalRampLCDTarget;
  }

  protected LCDTarget getWhiteRelativeTarget(LCDTarget measureTarget) {
    return RelativeTarget.
        getLuminanceAndChromaticityRelativeInstance(
            getOriginalTarget(), measureTarget, cp.turnCode, lcdModel, cp, wp);
  }

  /**
   * RelativeTarget & SmoothGreenByModel所需
   */
  private LCDModel lcdModel;

  public static void main(String[] args) {
    RGB rgb0 = new RGB(0, 0, 0);
    RGB rgb1 = new RGB(2, 1, 1);
    RGB rgb2 = new RGB(2, 2, 2);
    RGB rgb3 = new RGB(3, 3, 3);
    RGB rgb4 = new RGB(4, 4, 4);
    RGB rgb5 = new RGB(4, 4, 5);
    RGB rgb6 = new RGB(4, 4, 6);
    RGB rgb7 = new RGB(4, 4, 7);
    RGB[] rgbArray = new RGB[] {
        rgb0, rgb1, rgb2, rgb3, rgb4, rgb5, rgb6, rgb7};
    for (RGB rgb : rgbArray) {
      rgb.changeMaxValue(RGB.MaxValue.Double255);
    }

  }

  public RGB[][] getCalibratedResult() {
    RGB[][] result = null;

    int size = batchList.size();
    result = new RGB[size][];
    for (int x = 0; x < size; x++) {
      Batch b = batchList.get(x);
      result[x] = b.calibratedResult;
    }

    return result;
  }

  private void storeRGBArrayExcel(RGB[][] results, String[] filenames
      ) {
    if (results.length != filenames.length) {
      throw new IllegalArgumentException(
          "results.length != filenames.length");
    }
    int size = results.length;
    for (int x = 0; x < size; x++) {
      if (filenames[x] != null && results[x] != null) {
        CalibrateUtils.storeRGBArrayExcel(results[x],
                                          rootDir + "/" +
                                          filenames[x], cp);
      }
    }
  }

  private List<Batch> batchList = new ArrayList<Batch> ();
  public void addBatch(Batch batch) {
    batchList.add(batch);
  }

  public void addBatch(Batch ...batchArray) {
    for (Batch b : batchArray) {
      batchList.add(b);
    }
  }

  public final static class Batch {
    public Batch(boolean enable, boolean[] alreadyCalibrated,
                 boolean luminanceCalibrate,
                 boolean chromaticCalibrate, String beforeInterpFilename,
                 String calibratedFilename, String cubeCheckFilename,
                 String testInterpFilename, int calibratedInterval) {
      this.enable = enable;
      this.alreadyCalibrated = alreadyCalibrated;
      this.luminanceCalibrate = luminanceCalibrate;
      this.chromaticCalibrate = chromaticCalibrate;
      this.beforeInterpFilename = beforeInterpFilename;
      this.calibratedFilename = calibratedFilename;
      this.cubeCheckFilename = cubeCheckFilename;
      this.testInterpFilename = testInterpFilename;
      this.calibratedInterval = calibratedInterval;
    }

    public boolean enable;
    public boolean[] alreadyCalibrated;

    public boolean luminanceCalibrate;
    public boolean chromaticCalibrate;
    public String beforeInterpFilename;
    public String calibratedFilename;
    public String cubeCheckFilename;
    public String testInterpFilename;
    public int calibratedInterval;
    public RGB[] calibratedResult;
  }

  private RGB[] _calibrate(Batch batch, RGB[] calibratedResult) {
    mi.reset();
    boolean[] alreadyCalibrated = batch.alreadyCalibrated;
    RGB[][] results = getCalibratedRGBArrays(calibratedResult,
                                             batch.luminanceCalibrate,
                                             batch.chromaticCalibrate,
                                             alreadyCalibrated,
                                             batch.calibratedInterval);
    storeRGBArrayExcel(results,
                       new String[] {batch.calibratedInterval != 1 ?
                       batch.beforeInterpFilename : null,
                       batch.calibratedFilename,
                       ap.cubeCheckAtLowLuminance ?
                       batch.cubeCheckFilename : null,
                       batch.testInterpFilename});
    batch.calibratedResult = results[1];
    return results[1];
  }

  /**
   * 執行校正, 內部呼叫用
   *
   * @return RGB[]
   */
  protected RGB[] _calibrate() {
    initCalibrate();
    RGB[] calibratedResult = this.getCPCodeRGBArray();

    for (Batch b : batchList) {
      if (b.enable) {
        calibratedResult = _calibrate(b, calibratedResult);
      }
    }
    CalibrateUtils.storeRGBArrayExcel(calibratedResult,
                                      rootDir + "/" + IndependFinal, cp);
    logFindingThread();
    return calibratedResult;
  }

  private void logFindingThread() {
    java.util.logging.Logger log = Logger.getDefaultLogger("findingThread");
    log.info(this.getThreadListTrace());
  }

  private LCDTarget originalRampLCDTarget;

  /**
   * 校正的結果2同時即為cube check的結果
   *
   * @param initRGBArray RGB[]
   * @param luminanceCalibrate boolean
   * @param chromaticCalibrate boolean
   * @param alreadyCalibrated boolean[]
   * @param calibratedInterval int
   * @return RGB[][] {初步校正完成的結果(未內插), 校正的結果(內插且崩潰修復), 候選的校正的結果(內插且崩潰修復) }
   */
  protected RGB[][] getCalibratedRGBArrays(final RGB[] initRGBArray,
                                           boolean luminanceCalibrate,
                                           boolean chromaticCalibrate,
                                           boolean[] alreadyCalibrated,
                                           int calibratedInterval) {
    //初步校正完成的結果(未內插)
    RGB[] originalCalibrated = RGBArray.deepClone(initRGBArray);
    this.luminanceCalibrate = luminanceCalibrate;
    this.chromaticCalibrate = chromaticCalibrate;
    this.setCalibrated(alreadyCalibrated);

    findingInfo.setInitRGBArray(originalCalibrated);
    startCalibrate();
    //校正的結果,內插且崩潰修復
    RGB[] calibratedResult = RGBArray.deepClone(originalCalibrated);
    //校正的結果2, 內插且崩潰修復
    RGB[] candilateCalibratedResult = RGBArray.deepClone(findingInfo.
        getCandilateCalibratedRGBArray());
    RGB[] interpolateTestResult = null;
    if (calibratedInterval != 1 && interpolator != null) {
      //========================================================================
      // 內插設定
      //========================================================================
      //標示已經校正的, 其實在流程中沒有被校正; 因此已被校正的才要被內插.
      boolean[] interpolate = Arrays.copyOf(alreadyCalibrated,
                                            alreadyCalibrated.length);
      interpolate[0] = interpolate[interpolate.length - 1] = false;
      //========================================================================

      //========================================================================
      // test
      //========================================================================
      if (testInterpolate) {
        interpolateTestResult = interpolator.interpolateResult(
            calibratedResult, interpolate, cp.calibrateBits,
            Interpolator.Mode.Quadratic);
        quantizatioCollapseFix(interpolateTestResult);
      }
      //========================================================================

      //========================================================================
      // cali1
      //========================================================================
      calibratedResult = interpolator.interpolateResult(
          calibratedResult, interpolate, cp.calibrateBits);
      //========================================================================

      //========================================================================
      // cali2
      //========================================================================
      candilateCalibratedResult = interpolator.interpolateResult(
          candilateCalibratedResult, interpolate, cp.calibrateBits);
      //========================================================================

    }

    quantizatioCollapseFix(calibratedResult);
    quantizatioCollapseFix(candilateCalibratedResult);

    //==========================================================================
    RGB[][] result = new RGB[][] {
        originalCalibrated, calibratedResult, candilateCalibratedResult,
        interpolateTestResult};
    return result;
  }

  /**
   * 是否要進行內插演算法的測試
   */
  private boolean testInterpolate = true;

  /**
   * 將smoothGreen跟whiteBased的RGBArray合併
   * 簡單的說, 就是把smoothGreen中的green替換掉whiteBased中的Green
   * @param smoothGreenRGBArray RGB[]
   * @param whiteBasedRGBArray RGB[]
   * @return RGB[]
   */
  private RGB[] combineSmoothGreenAndWhiteBasedRGBArray(RGB[]
      smoothGreenRGBArray, RGB[] whiteBasedRGBArray) {
    if (smoothGreenRGBArray.length != whiteBasedRGBArray.length) {
      throw new IllegalArgumentException(
          "moothGreenRGBArray.length != whiteBasedRGBArray.length");
    }
    int size = smoothGreenRGBArray.length;
    RGB[] combineRGBArray = new RGB[size];
    for (int x = 0; x < size; x++) {
      combineRGBArray[x] = (RGB) whiteBasedRGBArray[x].clone();
      combineRGBArray[x].G = smoothGreenRGBArray[x].G;
    }
    return combineRGBArray;
  }

  /**
   *
   * @param smoothGreenRGBArray RGB[]
   * @return RGB[]
   */
  private RGB[] getSmoothGreenByModel(RGB[] smoothGreenRGBArray) {
    RBCalculator rbc = new RBCalculator(this.lcdModel);
    CIExyY[] targetxyYArray = this.getTargetxyYArray();
    int size = smoothGreenRGBArray.length;
    RGB[] modelRGBArray = new RGB[size];
    double tolerance = RGB.MaxValue.Int10Bit.getStepIn255();

    for (int x = 0; x < size; x++) {
      CIExyY xyY = targetxyYArray[x];
      RGB green = smoothGreenRGBArray[x];
      RGB rgb = rbc.getRB(xyY, green.G, tolerance, false);
      rgb.quantization(this.maxValue);
      rgb.G = green.G;
      modelRGBArray[x] = rgb;
      Logger.log.info(x + " " + rgb);
    }

    return modelRGBArray;
  }

  private Interpolator interpolator;

  private SmoothGreen smoothGreen = new SmoothGreen();
  private static enum SmoothMethod {
    LowPass,
    /**
     * 恆定的gamma, 不建議使用
     */
    ConstGamma,
    VariableGamma, LinearInterpolate
  }

  private class SmoothGreen {
    private SmoothMethod method = SmoothMethod.VariableGamma;
//    private SmoothMethod method = SmoothMethod.ConstGamma;

    /**
     * 計算出green smooth的LCDTarget
     *
     * pseudo code:
     *   先量測出green原始的XYZ數值
     *   以green的XYZ計算出JNDI, 並且預測出一條smooth的JNDI
     *   將smooth JNDI轉換回大Y
     *   以green的大Y, 推算出對應的green code及對應的XYZ
     *   將green code和XYZ存到List<Patch>
     *   以List<Patch>產生出LCDTarget
     *
     * @param rgbArray RGB[]
     * @param originalRampLCDTarget LCDTarget
     * @return LCDTarget
     */
    protected final LCDTarget getSmoothGreenLCDTarget(final RGB[] rgbArray,
        LCDTarget originalRampLCDTarget) {
      //量測
      LCDTarget rampG = measure(rgbArray, RGBBase.Channel.G, null, false, true);
      //替換rgb
      LCDTarget replaceRamp = LCDTargetUtils.getReplacedLCDTarget(rampG,
          LCDTarget.Number.Ramp256G_W);

      //==========================================================================
      // 產生smooth target
      //==========================================================================

      double[] smoothYArray = null;
      switch (method) {
        case LowPass: {

          //計算最平滑的亮度資料
          GSDFPredicter.setGSDFLowPassPrimeCheckThreshold(ap.
              gsdfLowPassPrimeCheckThreshold);
          GSDFPredicter.PredictData predictData = GSDFPredicter.
              getGSDFLowPassPredictData(replaceRamp, cp, plotting);

          //轉換成Y
          smoothYArray = predictData.predictLuminance;
          break;
        }
        case ConstGamma: {
          LCDTarget rampGOnly = replaceRamp.targetFilter.getByNumber(LCDTarget.
              Number.Ramp256G, null);
          smoothYArray = getConstGammaSmoothLuminanceArray(rampGOnly);
          break;
        }
        case VariableGamma: {
          LCDTarget rampGOnly = replaceRamp.targetFilter.getByNumber(LCDTarget.
              Number.Ramp256G, null);
          smoothYArray = getVariableGammaSmoothLuminanceArray(rampGOnly);
          break;
        }
      }

      List<Patch>
          smoothPatchList = getPatchListFromLumiArray(originalRampLCDTarget,
          smoothYArray, RGBBase.Channel.G);
      LCDTarget smoothTarget = LCDTarget.Instance.get(smoothPatchList,
          LCDTarget.Number.Ramp256W, mm.isDo255InverseMode());
      //==========================================================================
      return smoothTarget;
    }

    private final double[] getConstGammaSmoothLuminanceArray(LCDTarget
        rampTarget) {
      double[] YArray = rampTarget.filter.YArray();
      double min = YArray[0];
      double max = YArray[YArray.length - 1];

      double[] normalOutput = DoubleArray.minus(YArray, min);
      Maths.normalize(normalOutput, normalOutput[YArray.length - 1]);
      double gamma = GammaFinder.findGamma(GammaFinder.NormalInput,
                                              normalOutput);
      double[] result = GammaFinder.gammaCurve(GammaFinder.NormalInput, gamma);
      result = DoubleArray.times(result, max - min);
      result = DoubleArray.plus(result, min);

      return result;
    }

    /**
     * 利用多項式預測input和output之間的關係
     * @param input double[]
     * @param output double[]
     * @param start int
     * @param end int
     * @param coef COEF_1
     * @return double[]
     */
    private final double[] getRangePredictCurve(double[] input,
                                                double[] output, int start,
                                                int end, Polynomial.COEF_1 coef) {
      double[] in = DoubleArray.getRangeCopy(input, start, end);
      double[] out = DoubleArray.getRangeCopy(output, start, end);
      PolynomialRegression regress = new PolynomialRegression(in, out, coef);
      regress.regress();
      double[] predict = regress.getMultiPredict(input);
      return predict;
    }

    private final double[] getRangePredictCurve(double[] input,
                                                double[] output, int start,
                                                int end) {
      double[] in = DoubleArray.getRangeCopy(input, start, end);
      double[] out = DoubleArray.getRangeCopy(output, start, end);
      Polynomial.COEF_1 coef = PolynomialRegression.
          findBestPolynomialCoefficient1(input, output);
      System.out.println(coef);
      PolynomialRegression regress = new PolynomialRegression(in, out, coef);
      regress.regress();
      double[] predict = regress.getMultiPredict(input);
      return predict;
    }

    private final double[] getVariableGammaSmoothLuminanceArray(LCDTarget
        rampTarget) {
      double[] YArray = rampTarget.filter.YArray();
      double min = YArray[0];
      double max = YArray[YArray.length - 1];

      double[] normalOutput = GammaFinder.normalize(YArray, min, max);
      double[] gammas = GammaFinder.findGammas(GammaFinder.NormalInput,
                                                  normalOutput);
      double[] normalInput = DoubleArray.getRangeCopy(GammaFinder.NormalInput,
          1, GammaFinder.NormalInput.length - 2);
      double[] predict = getRangePredictCurve(normalInput, gammas,
                                              ap.variableGammaSmoothStart,
                                              ap.variableGammaSmoothEnd);
      double[] result = DoubleArray.copy(GammaFinder.NormalInput);
      for (int x = 0; x < result.length; x++) {
        int predictIndex = x - 1;
        if (predictIndex >= 0 && predictIndex < predict.length) {
          result[x] = Math.pow(result[x], predict[x - 1]);
        }
        result[x] *= max - min;
        result[x] += min;
      }

      if (plotting) {
        Plot2D plot = Plot2D.getInstance();
        plot.addLegend();
        plot.addLinePlot("predict", 1, 254, predict);
        plot.addLinePlot("gammas", 1, 254, gammas);
        plot.setVisible();
      }
      return result;
    }

    /**
     * 從lumiArray以及rampTarget推算出XYZ,
     * 並且與RGB一起儲存到Patch裡.
     *
     * rampTarget是用來計算所有的ramp code, 不存在的code則採用內插的方式.
     *
     * pseudo code:
     *   for Y : lumiArray
     *     以Y在rampTarget中找到對應的code
     *     從code再到rampTarget推算出XYZ
     *     將RGB及XYZ設定到Patch並且新增到List<Patch>
     *   回傳List<Patch>
     *
     * @param rampTarget LCDTarget
     * @param lumiArray double[]
     * @param channel Channel
     * @return List
     */
    private final List<Patch> getPatchListFromLumiArray(LCDTarget
        rampTarget, double[] lumiArray, RGBBase.Channel channel) {
      LCDTargetInterpolator interp = LCDTargetInterpolator.Instance.get(
          rampTarget, channel);

      int size = lumiArray.length;
      List<Patch> patchList = new ArrayList<Patch> (size);
      for (int x = 0; x < size; x++) {
        //從Y找到對應的code
        RGB mapRGB = interp.getRGB(channel, lumiArray[x]);
        //從G的code找到對應的XYZ
        CIEXYZ XYZ = interp.getPatch(channel, mapRGB.getValue(channel)).getXYZ();
        mapRGB.quantization(maxValue);
        Patch p = new Patch(Integer.toString(x + 1), XYZ, null, mapRGB);
        patchList.add(p);
      }
      return patchList;
    }

  }

  /**
   * 對rgbArray中的green亮度作smooth, 重新產生出對應的green code
   * 並且依照參數設定需求決定是否要做折衷修正
   *
   * @param initRGBArray RGB[]
   * @return RGB[]
   */
  protected RGB[] getSmoothGreenRGBArray(final RGB[] initRGBArray) {
    LCDTarget smoothTarget = smoothGreen.getSmoothGreenLCDTarget(initRGBArray,
        originalRampLCDTarget);

    //==========================================================================
    // JNDI的校正
    //==========================================================================
    JNDICalibrator calibrator = new JNDICalibrator(smoothTarget, this,
        RGBBase.Channel.G, originalRampLCDTarget);
    //三個boolean分別為 預測/最小誤差/折衷 校正的開關
    calibrator.setCalibrate(false, true, ap.smoothGreenCompromiseCalibrate);
    RGB[] result = calibrator.calibrate();
    //==========================================================================

    //==========================================================================
    // 補足R&B的部分
    //==========================================================================
    switch (ap.smoothGreenBasedOn) {
      case Model:
        result = getSmoothGreenByModel(result);

        //model預測可能造成誤差, 修正回來
        result[result.length - 1] = initRGBArray[initRGBArray.length - 1];
        break;
      case White:
        result = combineSmoothGreenAndWhiteBasedRGBArray(
            result, initRGBArray);
        break;
    }

    //==========================================================================

    quantizatioCollapseFix(result, RGBBase.Channel.G);
    return result;
  }

  /**
   * 對start以及end之間進行calibrate, 並且以triggerBool作為觸發量測用
   * @param triggerBool boolean[]
   * @param start int
   * @param end int
   */
  protected void calibrated0(boolean[] triggerBool, int start, int end) {
    for (int x = start; x < end; x++) {
      if (triggerBool[x] == false) {
        IndependentFindingThread t = new IndependentFindingThread(x, maxValue,
            findingInfo, triggerBool, this.luminanceCalibrate,
            this.chromaticCalibrate, this.white, access);
        t.setCubeCheckInOneJNDI(ap.cubeCheckAtLowLuminance &&
                                x <= ap.cubeCheckStartCode);
        if (algoFindByModel) {
          t.findByModel = algoFindByModel;
        }
        addToThreadList(t);
        t.setUncaughtExceptionHandler(this);
        if (parallelExcute) {
          t.start();
        }
        else {
          t.run();
        }
      }
    }
//    if (!algoDebug) {
    Trigger trigger = new Trigger(triggerBool);
    if (trigger.hasNextMeasure()) {
      mi.triggerMeasure(trigger);
    }
//    }
  }

  private boolean algoFindByModel = AutoCPOptions.get(
      "MeasuredCalibrator_FindByModel");

  public String getCalibratedInfomation() {
    StringBuilder buf = new StringBuilder();

    buf.append("AccumulateMeasureTimes: ");
    buf.append(getAccumulateMeasureCount());
    buf.append('\n');

    buf.append("NonQualifyTimes: ");
    buf.append(info.getNonQualifyTimes());
    buf.append('\n');

    buf.append("NonQualifyInfo: ");
    buf.append(info.getNonQualifyInfo());
    buf.append('\n');

    buf.append("CPLoadingCount: ");
    buf.append(info.getCPLoadingCount());
    buf.append('\n');

    int[] measureCount = info.getMeasureCount();
    buf.append("CPM MeasureCount(" + measureCount.length + "): ");
    buf.append(Arrays.toString(measureCount));
    buf.append('\n');

    return buf.toString();
  }

  /**
   * 設定LCDModel, RelativeTarget計算時所需
   * @param lcdModel LCDModel
   */
  public void setLCDModel(LCDModel lcdModel) {
    this.lcdModel = lcdModel;
  }

  public void setInterpolator(Interpolator interpolator) {
    this.interpolator = interpolator;
  }

}
