package shu.cms.lcd.calibrate.measured;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.gradient.*;
import shu.cms.hvs.gradient.Pattern;
import shu.cms.lcd.*;
import shu.cms.lcd.calibrate.*;
import shu.cms.lcd.calibrate.measured.util.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.cms.measure.*;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 1.預測校正: 比較實際量測結果與目標值的差異, 為減小差異以內插的方式調整code, 使其JNDI接近目標值.
 * 速度最快, 快速接近目標值.
 *
 * 2.最小誤差校正: 然後再以量測逐步調整到最接近目標值.
 * 最花時間, 但是誤差最小.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class JNDICalibrator
    extends MeasuredCalibrator {

  public JNDICalibrator(LCDTarget logoLCDTaget,
                        MeterMeasurement meterMeasurement,
                        ColorProofParameter p,
                        AdjustParameter ap, MeasureParameter mp) {
    this(logoLCDTaget, meterMeasurement, p, ap, mp, RGBBase.Channel.W, null);
  }

  /**
   * 由某個MeasuredCalibrator內部作JNDI校正
   * @param logoLCDTaget LCDTarget 目標LCDTarget
   * @param measuredCalibrator MeasuredCalibrator 呼叫來源的MeasuredCalibrator
   * @param channel Channel 校正頻道
   * @param originalRampLCDTarget LCDTarget 原始未校正的ramp LCDTarget, 用來判斷
   * smooth程度的
   */
  JNDICalibrator(LCDTarget logoLCDTaget,
                 MeasuredCalibrator measuredCalibrator,
                 RGBBase.Channel channel, LCDTarget originalRampLCDTarget) {
    super(logoLCDTaget, measuredCalibrator);
    init(channel, originalRampLCDTarget);
    this.step = this.maxValue.getStepIn255();
  }

  /**
   * 初始化
   * @param channel Channel
   * @param originalRampLCDTarget LCDTarget
   */
  private void init(RGBBase.Channel channel, LCDTarget originalRampLCDTarget) {
    this.calibratedChannel = channel;
    this.originalRampLCDTarget = originalRampLCDTarget;
//    if (!considerHKEffect) {
//      this.gm.setHKStrategy(GradientModel.HKStrategy.None);
//    }
  }

  public JNDICalibrator(LCDTarget logoLCDTaget,
                        MeterMeasurement meterMeasurement,
                        ColorProofParameter p,
                        AdjustParameter ap, MeasureParameter mp,
                        RGBBase.Channel channel,
                        LCDTarget originalRampLCDTarget) {
    super(logoLCDTaget, meterMeasurement, p, null, ap, mp);
    init(channel, originalRampLCDTarget);
    this.step = this.maxValue.getStepIn255();
  }

  private double step;

  /**
   * 要校正的頻道
   */
  private RGBBase.Channel calibratedChannel = RGBBase.Channel.W;

  /**
   * 預測校正結果
   */
  private RGB[] estimateRGBArray;
  /**
   * 最小誤差校正結果
   */
  private RGB[] minDeltaRGBArray;
  /**
   * 折衷校正結果
   */
  private RGB[] compromiseRGBArray;
  /**
   * 是否要進行最小誤差校正
   */
  private boolean minDeltaCalibrate = true;
  /**
   * 是否進行預測校正
   */
  private boolean estimateCalibrate = false;
  /**
   * 是否進行折衷校正, 就是在最小偏離目標點的狀況下, 使其smooth
   */
  private boolean compromiseCalibrate = false;

  /**
   * 設定要進行的校正
   * @param estimateCalibrate boolean 預測校正, 是否要進行預測校正, 用預測的方式先逼近,
   *  減少實際量測的時間耗損.
   * @param minDeltaCalibrate boolean 最小誤差校正, 是否要進行 最小誤差校正. 打開會讓校
   * 正時間拉長(預設是打開的)
   * @param compromiseCalibrate boolean 取捨(折衷)校正
   */
  public void setCalibrate(boolean estimateCalibrate,
                           boolean minDeltaCalibrate,
                           boolean compromiseCalibrate) {
    this.estimateCalibrate = estimateCalibrate;
    this.minDeltaCalibrate = minDeltaCalibrate;
    this.compromiseCalibrate = compromiseCalibrate;
  }

  /**
   * 檢查cp code是否正確
   * 如果不是白(也就是R/G/B), 就要確定cp code只有該頻道的數值
   * @return boolean
   */
  protected boolean checkCPCodeRGBArray() {
    if (this.calibratedChannel != RGBBase.Channel.W) {
      RGB[] cpCode = this.getCPCodeRGBArray();
      for (RGB rgb : cpCode) {
        if (!rgb.isBlack() && (rgb.getZeroChannelCount() != 2 ||
                               rgb.getMaxChannel() != this.calibratedChannel)) {
          return false;
        }
      }
    }
    return true;
  }

  private static class MeasureResult {
    /**
     * 與目標值的delta JNDI Array
     */
    private double[] deltaArray;
    /**
     * 量測結果的LCDTarget
     */
    private LCDTarget measuredLCDTarget;
    private MeasureResult(LCDTarget measuredLCDTarget, double[] deltaArray) {
      this.measuredLCDTarget = measuredLCDTarget;
      this.deltaArray = deltaArray;
    }
  }

  /**
   * 載入, 量測, 繪delta 並且儲存
   * @param rgbArray RGB[]
   * @param title String
   * @param storeFilename String
   * @param plot boolean 是否要繪delta
   * @param store boolean 是否要儲存
   * @return double[]
   */
  protected MeasureResult loadMeasurePlotAndStore(RGB[] rgbArray, String title,
                                                  String storeFilename,
                                                  boolean plot, boolean store) {
    //==========================================================================
    // load and measure
    //==========================================================================
    RGB[] cpcodeArray = this.getCPCodeRGBArray();
    RGB white = cpcodeArray[cpcodeArray.length - 1];
    LCDTarget target = this.measure(rgbArray, this.calibratedChannel, white, true, false);
    //==========================================================================

    List<Patch> measurePatchList = target.getPatchList();
    double[] delta = jndi.plotDeltaJNDI(title, plot, measurePatchList);
    MeasureResult result = new MeasureResult(target, delta);

    //==========================================================================
    // store
    //==========================================================================
    if (store && storeFilename != null) {
      CalibrateUtils.storeRGBArrayExcel(rgbArray, storeFilename, cp);
    }
    //==========================================================================
    return result;
  }

  public RGB[] _calibrate() {
    this.setChromaticityRelative(false);
    LCDTarget target = initMeasure(this.calibratedChannel, true);
    //繪出delta JNDI
    double[] delta1Array = jndi.calculateDeltaJNDICurve(target);
    jndi.plotDeltaJNDI(delta1Array, "delta");

    RGB[] calibratedRGBArray = null;

    //==========================================================================
    // 預測校正
    //==========================================================================
    if (estimateCalibrate) {
      estimateRGBArray = estimateCalibrate(target, maxValue);
      delta1Array = loadMeasurePlotAndStore(estimateRGBArray, "1",
                                            rootDir + "/" + JNDIEstimate, true, true).
          deltaArray;
      calibratedRGBArray = Arrays.copyOf(estimateRGBArray,
                                         estimateRGBArray.length);
    }
    else {
      RGB[] cpcodeRGBArray = this.getCPCodeRGBArray();
      calibratedRGBArray = Arrays.copyOf(cpcodeRGBArray, cpcodeRGBArray.length);
    }
    //==========================================================================

    //==========================================================================
    // 最小誤差校正
    //==========================================================================
    if (minDeltaCalibrate) {
      minDeltaRGBArray = minimumDeltaJNDICalibrate2(
          calibratedRGBArray, delta1Array);
      if (this.calibratedChannel != RGBBase.Channel.W) {
        IrregularUtil.irregularFix(minDeltaRGBArray,
                                   this.calibratedChannel);
      }
      loadMeasurePlotAndStore(minDeltaRGBArray, "2",
                              rootDir + "/" + JNDIMinDelta, true, true);
      calibratedRGBArray = Arrays.copyOf(minDeltaRGBArray,
                                         minDeltaRGBArray.length);
    }
    //==========================================================================

    //==========================================================================
    // 折衷校正
    //==========================================================================
    if (compromiseCalibrate) {
//      this.cpm.reset();
      compromiseRGBArray = compromiseCalibrate(calibratedRGBArray);
      loadMeasurePlotAndStore(compromiseRGBArray, "4",
                              rootDir + "/" + JNDICompromise, true, true);
      calibratedRGBArray = Arrays.copyOf(compromiseRGBArray,
                                         compromiseRGBArray.length);
    }
    //==========================================================================

    CalibrateUtils.storeRGBArrayExcel(calibratedRGBArray,
                                      rootDir + "/" + JNDIFinal, cp);
    return calibratedRGBArray;
  }

  /**
   * 從measurePatchList產生出LCDTarget供Compromise校正使用
   * @param measurePatchList List
   * @return LCDTarget
   */
  protected LCDTarget getCompromiseLCDTarget(List<Patch> measurePatchList) {
    List<Patch> patchList = new ArrayList<Patch> (measurePatchList);
    LCDTarget.Number number = MeasuredUtils.getMeasureNumber(this.
        calibratedChannel, true, false);
    LCDTarget measureLCDTarget = LCDTarget.Instance.get(patchList, number,
        this.mm.isDo255InverseMode());
    return measureLCDTarget;
  }

  public List<Patch> getCalibratedPatchList() {
    if (minDeltaCalibrate) {
      if (minDeltaRGBArray == null) {
        throw new IllegalStateException("minDeltaRGBArray == null");
      }
      else {
        return LCDTargetUtils.getReplacedPatchList(getCalibratedTarget(),
            this.minDeltaRGBArray);
      }

    }
    else {
      if (estimateRGBArray == null) {
        throw new IllegalStateException("estimateRGBArray == null");
      }
      else {
        return LCDTargetUtils.getReplacedPatchList(getCalibratedTarget(),
            this.estimateRGBArray);
      }
    }

  }

  /**
   * 找到JNDI誤差最小的code
   * @param calibratedRGBArray RGB[]
   * @param delta1Array double[]
   * @return RGB[]
   */
  protected RGB[] minimumDeltaJNDICalibrate2(final RGB[] calibratedRGBArray,
                                             final double[] delta1Array) {
    MinimumDeltaCalibrator calibrator = new MinimumDeltaCalibrator(
        calibratedRGBArray, delta1Array);

    RGB[] result = calibrator.calibrate();
    return result;

//    final double step = maxValue.getStepIn255();
//    //載入初始的rgb
//    final double[] targetJNDIArray = jndi.getTargetJNDICurve();
//    final boolean[] calibrated = new boolean[256];
//    calibrated[0] = calibrated[255] = true;
//
//    for (int x = 1; x < 255; x++) {
//      final int index = x;
//
//      // 以thread作平行調整, 以便塞滿CPCodeMeasurement
//      new Thread() {
//        public void run() {
//
//          double delta = delta1Array[index];
//          if (delta != 0) {
//            Logger.log.trace(index + " start adjust");
//          }
//          else {
//            //沒有誤差就直接結束校正, 機率應該很低吧
//            calibrated[index] = true;
//          }
//          while (delta != 0) {
//            RGB measureRGB = (RGB) calibratedRGBArray[index].clone();
//            //如果太高就降低, 太低就升高
//            double adjuststep = delta > 0 ? -step : step;
//            measureRGB.addValues(adjuststep);
//            if (calibratedChannel != RGBBase.Channel.W) {
//              // 白以外的顏色要另外處裡
//              measureRGB.reserveValue(calibratedChannel);
//            }
//            if (!measureRGB.isLegal()) {
//              //已經有了不合理的RGB值, 代表怎麼調都無能為力了
//              Logger.log.trace(index + " abnormal adjust end");
//              calibrated[index] = true;
//              break;
//            }
//
//            //量測結果
//            Patch p = cpm.measure(measureRGB);
//            Logger.log.trace(index + " measure " + p);
//            //計算出JNDI
//            double JNDI = getJNDI(p.getXYZ());
//            double newdelta = JNDI - targetJNDIArray[index];
//            if (newdelta * delta < 0) {
//              //跨越正負號, 代表已經找到最接近的JNDI, 因為跨過了target, 才會有正負號的delta
//              if (Math.abs(delta) > Math.abs(newdelta)) {
//                //這一次調整的code比較接近, 代表new delta比delta小
//                //選擇這一次的rgb為校正的rgb
//                calibratedRGBArray[index] = measureRGB;
//              }
//              Logger.log.trace(index + " adjust end");
//              calibrated[index] = true;
//              //無論如何, 找到最接近就應該要結束了
//              break;
//            }
//            //更新delta
//            delta = newdelta;
//            //更新RGB
//            calibratedRGBArray[index] = measureRGB;
//          }
//
//        }
//      }.start();
//
//    }
//
//    cpm.triggerMeasure(new Trigger(calibrated));
//    Logger.log.trace("measure end");
//
//    return calibratedRGBArray;
  }

  protected double getJNDI(CIEXYZ XYZ) {
    return jndi.getJNDI(XYZ);
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 調整方法的回傳類別
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  private static class AdjustResult {
    /**
     * 調整的索引值
     */
    private int adjustIndex;
    /**
     * 調整的方向
     */
    private boolean upAdjust;
    /**
     * 調整後的rgb
     */
    private RGB adjustedRGB;

    private AdjustResult(int adjustIndex, boolean upAdjust, RGB adjustedRGB) {
      this.adjustIndex = adjustIndex;
      this.upAdjust = upAdjust;
      this.adjustedRGB = adjustedRGB;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
      return "Index(" + adjustIndex + ") rgb:" + adjustedRGB + " Up:" +
          upAdjust;
    }

  }

  /**
   * 是否有特定的pattern
   * @param patternList List 要檢查的pattern List
   * @param negativePattern boolean true:檢查負的pattern false:檢查正的pattern
   * @return boolean
   */
  private final static boolean hasSpecificPattern(List<Pattern> patternList,
      boolean negativePattern) {
    for (Pattern pattern : patternList) {
      if (pattern.pattern < 0 == negativePattern) {
        return true;
      }
    }
    return false;
  }

  /**
   * 正的折衷校正
   */
  private boolean positiveCompromiseCalibrate = true;
  /**
   * 負的折衷校正
   */
  private boolean negativeCompromiseCalibrate = true;
  /**
   * 折衷校正的次數
   */
  private int compromiseCalibrateCount;

  /**
   * 從code之間的加速度差異為基礎, 在準度以及平滑之間做取捨
   * @param calibratedRGBArray RGB[]
   * @return RGB[]
   */
  protected RGB[] compromiseCalibrate(final RGB[] calibratedRGBArray) {
    CompromiseCalibrator calibrator = new CompromiseCalibrator(
        calibratedRGBArray);

    RGB[] result = calibrator.calibrate();
    compromiseCalibrateCount = calibrator.getAdjustCount();
    return result;
  }

  protected static interface CalibratorIF {
    public RGB[] calibrate();

    public int getAdjustCount();
  }

  protected class MinimumDeltaCalibrator
      implements CalibratorIF {
    private RGB[] calibratedRGBArray;
    private double[] deltaArray;
    protected MinimumDeltaCalibrator(final RGB[] calibratedRGBArray,
                                     final double[] deltaArray) {
      this.calibratedRGBArray = calibratedRGBArray;
      this.deltaArray = deltaArray;
    }

    public RGB[] calibrate() {
      final double step = maxValue.getStepIn255();
      //載入初始的rgb
      final double[] targetJNDIArray = jndi.getTargetJNDICurve();
      final boolean[] calibrated = new boolean[256];
      calibrated[0] = calibrated[255] = true;

      for (int x = 1; x < 255; x++) {
        final int index = x;

        // 以thread作平行調整, 以便塞滿CPCodeMeasurement
        new Thread() {
          private StringBuilder buf = new StringBuilder();
          public void run() {

            double delta = deltaArray[index];
            if (delta != 0) {
              buf.append(index + " start adjust (" +
                         calibratedRGBArray[index] + ")\n");
            }
            else {
              //沒有誤差就直接結束校正, 機率應該很低吧
              calibrated[index] = true;
            }
            while (delta != 0) {
              RGB measureRGB = (RGB) calibratedRGBArray[index].clone();
              //如果太高就降低, 太低就升高
              double adjuststep = delta > 0 ? -step : step;
              measureRGB.addValues(adjuststep);
              if (calibratedChannel != RGBBase.Channel.W) {
                // 白以外的顏色要另外處裡
                measureRGB.reserveValue(calibratedChannel);
              }
              if (!measureRGB.isLegal()) {
                //已經有了不合理的RGB值, 代表怎麼調都無能為力了
                buf.append(index + " abnormal adjust end\n");
                calibrated[index] = true;
                break;
              }

              //量測結果
              Patch p = mi.measure(measureRGB);
              buf.append(index + " measure " + p + '\n');
              //計算出JNDI
              double JNDI = getJNDI(p.getXYZ());
              double newdelta = JNDI - targetJNDIArray[index];
              if (newdelta * delta < 0) {
                //跨越正負號, 代表已經找到最接近的JNDI, 因為跨過了target, 才會有正負號的delta
                if (Math.abs(delta) > Math.abs(newdelta)) {
                  //這一次調整的code比較接近, 代表new delta比delta小
                  //選擇這一次的rgb為校正的rgb
                  calibratedRGBArray[index] = measureRGB;
                }
                buf.append(index + " adjust end (" +
                           calibratedRGBArray[index] + ")\n");
                calibrated[index] = true;
                //無論如何, 找到最接近就應該要結束了
                break;
              }
              //更新delta
              delta = newdelta;
              //更新RGB
              calibratedRGBArray[index] = measureRGB;
            }
            traceDetail(buf.toString());
          }
        }.start();

      }

      mi.triggerMeasure(new Trigger(calibrated));
      traceDetail("measure end");

      return calibratedRGBArray;
    }

    public int getAdjustCount() {
      return -1;
    }
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * smooth調整演算法
   *
   * check的index有2:
   * 1.sum of "超出threshold pattern的pattern"
   * 2.sum of delta a
   * 每次調整要符合的優先序為  1.1要<=調整前 2.2要<=調整前, 並且找最小的
   *
   * 演算法的流程為:
   * 1. 找到score最大的pattern, 其code為n
   * 2. 對pattern的 n-1, n, n+1 分別做調整,調整要依照pattern的型態而定.
   *    如果是+pattern 則是 -,+,-調整. 如果是-pattern 則是+,-,+
   * 3. 分別對三種調整計算index1及index2, 先經過index1的檢驗, 若通過, 再以index2檢驗
   *    並找最小者.
   *
   * 處理的原則為:
   * 1. 先處理正的pattern, 因為人眼對正pattern較有明顯感受;
   *    正pattern是亮線(突然增很多), 負pattern是暗線(突然掉下來).
   *    亮暗線通常是一起出現的(亮線旁就是暗線).
   * 2. 檢查是否還有正的pattern, 如果有就先把正pattern處理完; 處理完再來弄負pattern
   * 3. 處理pattern採用多執行緒平行處理, 不要讓下一個等上一個, 把cp code盡量塞滿
   * 4. 為了避免處理時有所重複, 每個被處理的pattern間隔要在3以上(含3), 避免計算加速度差時互相干擾
   * 5. 處理的準則是index1要<= 調整前, index2要<= 調整前 並且找最小的.
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  protected class CompromiseCalibrator
      implements CalibratorIF {
    private RGB[] originalRGBArray;
    private RGB[] calibratedRGBArray;
    /**
     * 用來收集調整過的組合
     */
    private Set<RGB> adjustSet = new HashSet<RGB> ();
    /**
     * 原始未調整前所計算的check Index
     */
    private double[] originalCheckIndex;
    /**
     * 計算暫存用的checkIndex Array
     */
    private double[][] tmpCheckIndexArray;
    /**
     * 取得原始的checking value
     * @return double
     */
    private double getOriginalCheckingValue() {
      if (originalCheckIndex != null) {
        return originalCheckIndex[1];
      }
      else {
        return -1;
      }
    }

    protected CompromiseCalibrator(RGB[] originalRGBArray) {
      this.originalRGBArray = originalRGBArray;

      //========================================================================
      // init
      //========================================================================
      loadMeasurePlotAndStore(originalRGBArray, "3",
                              rootDir + "/" + JNDICalibrated, true, true);
      calibratedRGBArray = Arrays.copyOf(originalRGBArray,
                                         originalRGBArray.length);
      //========================================================================
    }

    private boolean hasPositivePattern(GSDFGradientModel.PatternAndScore pas) {
      boolean hasPositivePattern = positiveCompromiseCalibrate &&
          hasSpecificPattern(pas.patternList, false);
      return hasPositivePattern;
    }

    public RGB[] calibrate() {
      /**
       *
       * pseudo code(sequence版):
       *   for(如果不smooth就持續跑下去) {
       *     for(pattern:patternList) {
       *       if 還有正pattern and 遇到負pattern
       *         continue;
       *
       *       if pattern.index與上一次調整的index差異<3
       *         continue;
       *
       *       計算出可以調整的3個code
       *
       *       依照可調整的3個code, 調整code並且量測
       *       找到最適合的調整code
       *       將選擇的調整code設定
       *     }
       *   }
       *
       * pseudo code(multi-thread版):
       *   上一個thread全跑完才可以繼續分析
       *   for(如果不smooth就持續跑下去) {
       *     for(pattern:patternList) {
       *       if 還有正pattern and 遇到負pattern
       *         continue;
       *
       *       if pattern.index與上一次調整的index差異<3
       *         continue;
       *
       *       計算出可以調整的3個code
       *
       *       new Thread() {
       *         調整code並且量測
       *         找到最適合的調整code
       *         將選擇的調整code設定
       *       }.start;
       *     }
       *   }
       *
       * @return RGB[]
       */
      GSDFGradientModel.PatternAndScore pas = getPatternAndScore(
          originalRGBArray);
      originalCheckIndex = getCheckIndex(pas);

      //是不是還有正的pattern
      boolean hasPositivePattern = hasPositivePattern(pas);
      //略過正的pattern
      boolean skipPositivePattern = false;

      for (adjustCount = 0; !pas.isSmooth(); adjustCount++) {
        //有不順點
        List<Pattern> patternList = new ArrayList<Pattern> (pas.patternList);
        //由大到小排序過
        Collections.sort(patternList, patternComparator);
        boolean adjusted = false;
        Pattern preAdjustedPattern = null;

        /**
         * 1.如果做過一次調整後, 發現都沒調整到, 先不要結束, 下一次都略過正的調整.
         * 如果略過正的調整之後還是發現都沒調整到(也就是負的也沒的調) 就真的結束掉.
         *
         * 2.如果做過一次調整後, 發現負的都被pass掉, 先不要結束, 下一次都略過正的調整.
         * 如果略過正的調整之後還是發現都沒調整到(也就是負的也沒的調) 就真的結束掉.
         */

        for (Pattern pattern : patternList) {
          if ( ( (!positiveCompromiseCalibrate || skipPositivePattern) &&
                pattern.pattern > 0) ||
              ( (!negativeCompromiseCalibrate ||
                 (hasPositivePattern && !skipPositivePattern)) &&
               pattern.pattern < 0)) {
            //如果不校正正的, 正的pattern就略過
            //如果要校正負的, 但是還有正的pattern, 也是略過
            continue;
          }

          if (preAdjustedPattern != null &&
              (pattern.index - preAdjustedPattern.index) < 3) {
            //間距一定要在3以上, 否則會重疊到, 重疊到會造成計算smooth出錯.
            continue;
          }

          AdjustResult result = getBestAdjustResult(pattern,
              calibratedRGBArray);
          if (result != null && !adjustSet.contains(result.adjustedRGB)) {
            //不是null代表評估後適合做調整
            RGB rgb = result.adjustedRGB;
            calibratedRGBArray[result.adjustIndex] = rgb;
            preAdjustedPattern = pattern;
            adjustSet.add(rgb);
            adjusted = true;
            Logger.log.trace("[AdjustResult] " + result);
          }
        }

        //======================================================================
        // 檢查是否做過調整
        //======================================================================
        if (!adjusted) {
          if (skipPositivePattern) {
            //沒有做任何調整的話就結束吧!
            Logger.log.trace("Non-calibrated, stop!");
            break;
          }
          else {
            skipPositivePattern = true;
          }
        }
        //======================================================================

        pas = getPatternAndScore(calibratedRGBArray);
        hasPositivePattern = hasPositivePattern(pas);
      }

      return calibratedRGBArray;
    }

    private GSDFGradientModel.PatternAndScore getPatternAndScore(RGB[]
        measuredRGBArray) {
      loadMeasurePlotAndStore(measuredRGBArray, null, null, false, false);

      //========================================================================
      // 從量測結果計算smooth的程度
      //========================================================================
      LCDTarget target = getMeasuredLCDTarget();
      GSDFGradientModel gm = getGSDFGradientModel(target);
      GSDFGradientModel.PatternAndScore pas = gm.getPatternAndScore();
      //========================================================================

      return pas;
    }

    public int getAdjustCount() {
      return adjustCount;
    }

    private int adjustCount;
    private final static int AdjustItemCount = 3;
    private final static int MimimumCPCode = 0;
    private final static int MaximumCPCode = 255;

    /**
     * 從三種調整方式中找到最適合的調整法
     * @param pattern Pattern
     * @param originalRGBArray RGB[]
     * @return AdjustResult
     */
    private AdjustResult getBestAdjustResult(Pattern pattern,
                                             final RGB[] originalRGBArray) {
      RGB[] rgbArray1 = Arrays.copyOf(originalRGBArray,
                                      originalRGBArray.length);
      RGB[] rgbArray2 = Arrays.copyOf(originalRGBArray,
                                      originalRGBArray.length);
      RGB[] rgbArray3 = Arrays.copyOf(originalRGBArray,
                                      originalRGBArray.length);
      RGB[][] rgbArray = new RGB[][] {
          rgbArray1, rgbArray2, rgbArray3};
      RGB[] adjustRGBArray = new RGB[AdjustItemCount];
      final boolean positivePattern = pattern.pattern > 0;
      //調整的index
      final int[] adjustIndexArray = new int[] {
          pattern.index - 1, pattern.index, pattern.index + 1};
      //調整的方向, 依照pattern而有所不同
      final boolean[] adjustUpDirection = new boolean[] {
          !positivePattern, positivePattern, !positivePattern};

      GSDFGradientModel.PatternAndScore[] pasArray = new GSDFGradientModel.
          PatternAndScore[AdjustItemCount];

      //========================================================================
      // 產生調整的cp code
      //========================================================================
      Logger.log.trace("Candilate: ");
      for (int x = 0; x < AdjustItemCount; x++) {
        RGB[] array = rgbArray[x];
        int adjustIndex = adjustIndexArray[x];
        RGB rgb = (RGB) array[adjustIndex].clone();
        double adjustValue = rgb.getValue(calibratedChannel);
        double adjustStep = adjustUpDirection[x] ? step : -step;
        adjustValue += adjustStep;
        if (adjustValue <= MimimumCPCode || adjustValue >= MaximumCPCode) {
          //如果調整後的值不合理就直接跳開
          continue;
        }
        rgb.setValue(calibratedChannel, adjustValue);
        Logger.log.trace(array[adjustIndex] + "->" + rgb);
        adjustRGBArray[x] = rgb;
        array[adjustIndex] = rgb;

        //======================================================================
        // 計算該調整的index
        //======================================================================
        MeasureResult result = loadMeasurePlotAndStore(array, null, null, false, false);
        GSDFGradientModel gm = getGSDFGradientModel(result.measuredLCDTarget);
        pasArray[x] = gm.getPatternAndScore();
        //======================================================================
      }
      //========================================================================

      //========================================================================
      // 找到最佳調整法
      //========================================================================
      double[] checkingValue = getCheckingValueArray(pasArray);
      double min = Maths.min(checkingValue);
      if (min < getOriginalCheckingValue()) {

        int minIndex = Maths.minIndex(checkingValue);
        double minCheckingValue = checkingValue[minIndex];
        if (minCheckingValue != Double.MAX_VALUE) {
          //如果minCheckingValue是MAX_VALUE, 代表沒一個是候選
          AdjustResult result = new AdjustResult(pattern.index - 1 + minIndex,
                                                 adjustUpDirection[minIndex],
                                                 adjustRGBArray[minIndex]);
          Logger.log.trace("min(" + min + ") < OriginalCheckingValue(" +
                           getOriginalCheckingValue() + ")");
          return result;
        }
      }
      //========================================================================
      return null;
    }

    private final GSDFGradientModel getGSDFGradientModel(LCDTarget lcdTarget) {
      GSDFGradientModel gm = new GSDFGradientModel(lcdTarget);
      gm.setImageChannel(calibratedChannel);
      gm.setPatternSign(GSDFGradientModel.PatternSign.Threshold);
      gm.setRecommendThresholdPercent(calibratedChannel);
      gm.setTargetxyYArray(getTargetxyYArray());
      gm.getAllPatternIndex();
      gm.statistics();

      return gm;
    }

    private final double[] getCheckingValueArray(GSDFGradientModel.
                                                 PatternAndScore[] pasArray) {
      boolean[] check1Pass = getCheck1PassArray(pasArray);

      double[] check2Value = getCheck2ValueArray(pasArray, check1Pass);
      return check2Value;
    }

    private final double[] getCheck2ValueArray(GSDFGradientModel.
                                               PatternAndScore[] pasArray,
                                               boolean[] check1PassArray) {
      if (pasArray.length != check1PassArray.length) {
        throw new IllegalArgumentException(
            "pasArray.length != check1PassArray.length");
      }
      double[] check2Value = new double[AdjustItemCount];
      int size = pasArray.length;
      for (int x = 0; x < size; x++) {
        boolean check1 = check1PassArray[x];
        if (false == check1) {
          check2Value[x] = Double.MAX_VALUE;
        }
        else {
          double[] checkIndex = tmpCheckIndexArray[x];
          check2Value[x] = checkIndex[1];
        }
      }
      return check2Value;
    }

    /**
     * 判斷check1是否通過
     * @param pasArray PatternAndScore[]
     * @return boolean[]
     */
    private final boolean[] getCheck1PassArray(GSDFGradientModel.
                                               PatternAndScore[] pasArray) {
      boolean[] check1Pass = new boolean[AdjustItemCount];
      tmpCheckIndexArray = new double[AdjustItemCount][];

      for (int x = 0; x < AdjustItemCount; x++) {
        GSDFGradientModel.PatternAndScore pas = pasArray[x];
        if (pas == null) {
          check1Pass[x] = false;
          continue;
        }
        double[] checkIndex = getCheckIndex(pas);
        tmpCheckIndexArray[x] = checkIndex;
        if (checkIndex[0] > originalCheckIndex[0]) {
          check1Pass[x] = false;
          continue;
        }
        check1Pass[x] = true;
      }
      return check1Pass;
    }

    /**
     * 由pas計算出checkIndex 1 & 2
     * @param pas PatternAndScore
     * @return double[]
     */
    private final double[] getCheckIndex(GSDFGradientModel.
                                         PatternAndScore pas) {
      double sumPatternOfOverThreshold = getSumPatternOfOverThreshold(pas);
      double sumOfDeltaAccel = getSumOfDeltaAccel(pas);
      return new double[] {
          sumPatternOfOverThreshold, sumOfDeltaAccel};
    }

    /**
     * sum of delta a
     * @param pas PatternAndScore
     * @return double
     */
    private final double getSumOfDeltaAccel(GSDFGradientModel.
                                            PatternAndScore pas) {
      DoubleArray.abs(pas.deltaAccelArray);
      double sumOfdeltaa = Maths.sum(pas.deltaAccelArray);
      return sumOfdeltaa;
    }

    /**
     * sum of "超出threshold pattern的pattern"
     * @param pas PatternAndScore
     * @return double
     */
    private final double getSumPatternOfOverThreshold(GSDFGradientModel.
        PatternAndScore pas) {
      double sumPatternOfOverThreshold = 0;
      List<Pattern> patternList = pas.patternList;
      int size = patternList.size();
      for (int x = 0; x < size; x++) {
        Pattern p = patternList.get(x);
        if (Math.abs(p.overRatio) > 100.) {
          sumPatternOfOverThreshold += Math.abs(p.pattern);
        }
      }
      return sumPatternOfOverThreshold;
    }

    protected RGB[] getCalibratedResult() {
      return calibratedRGBArray;
    }
  }

  private static PatternComparator patternComparator = new PatternComparator();
  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 搭配PatternComparator選擇的sort Index
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  private static enum SortBy {
    Pattern, OverRatio, JNDIndex
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * pattern比較器, 用來做pattern List排列用的.
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  protected static class PatternComparator
      implements Comparator {

    private SortBy sortBy = SortBy.OverRatio;
    private boolean inverseSort = true;

    protected void setInverseSort(boolean inverseSort) {
      this.inverseSort = inverseSort;
    }

    protected void setSortBy(SortBy sortBy) {
      this.sortBy = sortBy;
    }

    /**
     * Compares its two arguments for order.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *   argument is less than, equal to, or greater than the second.
     */
    public int compare(Object o1, Object o2) {
      Pattern p1 = (Pattern) o1;
      Pattern p2 = (Pattern) o2;
      double v1 = getValue(sortBy, p1);
      double v2 = getValue(sortBy, p2);
      return inverseSort ? Double.compare(v2, v1) : Double.compare(v1, v2);
    }

    private static double getValue(SortBy sortBy, Pattern pattern) {
      switch (sortBy) {
        case Pattern:
          return pattern.pattern;
        case OverRatio:
          return pattern.overRatio;
        case JNDIndex:
          return pattern.jndIndex;
        default:
          return -1;
      }
    }

    /**
     * Indicates whether some other object is &quot;equal to&quot; this
     * comparator.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> only if the specified object is also a
     *   comparator and it imposes the same ordering as this comparator.
     */
    public boolean equals(Object obj) {
      return false;
    }

  }

  /**
   * 計算panel原始校正頻道的每一個code的jndi'
   * @param in256Level boolean 是否僅以256作計算,如果為true, 則只有256 level.
   *                           如果為false, 則內插到LCD的解析度(如10bit的1021 level)
   * @return double[]
   */
  protected double[] getOriginalJNDIndexPrimeArray(boolean in256Level) {
    LCDTarget originalRamp = getOriginalRamp();
    LCDTargetInterpolator interp = LCDTargetInterpolator.Instance.get(
        originalRamp, calibratedChannel);
    GSDFGradientModel gm = new GSDFGradientModel(originalRamp);

    if (true == this.maxValue.integer) {
      RGB.MaxValue maxValue = in256Level ? RGB.MaxValue.Int8Bit :
          this.maxValue;
      int level = (int) maxValue.max + 1;
      double step = maxValue.getStepIn255();
      double[] jndiArray = new double[level];
      int index = 0;

      for (double code = 0; code <= 255; code += step) {
        double jndi = this.jndi.getJNDI(interp, code, this.calibratedChannel,
                                        gm);
        jndiArray[index] = jndi;
        index++;
      }
      double[] jndiPrimeArray = Maths.firstOrderDerivatives(jndiArray);
      return jndiPrimeArray;
    }
    else {
      throw new IllegalStateException("calibrated maxValue is not integer");
    }
  }

  /**
   * 原始的ramp LCDTarget
   */
  private LCDTarget originalRampLCDTarget;

  /**
   * 取得原始的ramp LCDTarget
   * @return LCDTarget
   */
  protected LCDTarget getOriginalRamp() {
    //==========================================================================
    // 量測原始的code, 為的是了解加速度的變化
    //==========================================================================
    if (originalRampLCDTarget == null) {
      //如果建構時候沒有設定, 只好自行作量測
      originalRampLCDTarget = measure(RGBArray.getOriginalRGBArray(),
                                      this.calibratedChannel, null, true, true);
    }
    //==========================================================================
    return originalRampLCDTarget;
  }

  protected RGB[] estimateCalibrate(LCDTarget ramp, RGB.MaxValue maxValue) {
    //==========================================================================
    // 第一次調整
    //==========================================================================
    //從量測結果估算調整的code
    double[] estimateResult = estimate(ramp, maxValue);
    RGB[] result = wcc.getRGBArray(estimateResult, maxValue);
    //僅保存要校正的頻道
    if (this.calibratedChannel != RGBBase.Channel.W) {
      for (RGB rgb : result) {
        rgb.reserveValue(this.calibratedChannel);
      }
    }
    return result;
  }

  /**
   * 從量測結果, 內插出離目標code最接近的實際code.
   * @param code int
   * @param interp LCDTargetInterpolator
   * @param maxValue MaxValue
   * @return double
   */
  protected double getNearestMeasuredCode(int code,
                                          LCDTargetInterpolator interp,
                                          RGB.MaxValue maxValue) {
    double targetJNDI = jndi.getJNDI(getCalibratedTarget(), code);
    double measureJNDI = jndi.getJNDI(interp, code, this.calibratedChannel);
    double delta = measureJNDI - targetJNDI;
    if (delta == 0) {
      //代表沒有差異, 直接回傳
      return code;
    }
    double step = delta > 0 ? -maxValue.getStepIn255() :
        maxValue.getStepIn255();
    double lastdelta = delta;

    for (double newcode = code + step; ; newcode += step) {
      double JNDI = jndi.getJNDI(interp, newcode, this.calibratedChannel);
      double newdelta = JNDI - targetJNDI;
      if (newdelta * delta < 0) {
        //代表delta正負號變化, 越過中線
        if (lastdelta < newdelta) {
          return newcode - step;
        }
        else {
          return newcode;
        }
      }
      lastdelta = newdelta;
      if (newcode > 255) {
        Logger.log.warn("newcode > 255, finish!");
        return 255;
      }
    }

  }

  /**
   * 計算量測值跟理想值的誤差, 然後從誤差估算校正後的cp code
   * @param measure LCDTarget 量測到的LCDTarget
   * @param maxValue MaxValue 校正的bit數
   * @return double[]
   */
  private double[] estimate(LCDTarget measure,
                            RGB.MaxValue maxValue) {
    Interpolation.Algo[] algos = LCDTargetInterpolator.Find.
        optimumInterpolationType(measure,
                                 LCDTargetInterpolator.OptimumType.Max,
                                 calibratedChannel);
    //建立內插LCDTarget
    LCDTargetInterpolator targetInterp = LCDTargetInterpolator.Instance.get(
        measure, algos, calibratedChannel);

    double[] origin = new double[256];
    double[] result = new double[256];

    //==========================================================================
    // 估算出非cp code的理想值
    //==========================================================================
    for (int x = 1; x < 255; x++) {
      //從量測的結果, 找到最接近目標值的code
      double nearestCode = getNearestMeasuredCode(x, targetInterp, maxValue);
      //避免超出範圍, 會造成超出範圍的原因在於, 量測的不穩定(?).
      nearestCode = nearestCode > 255 ? 255 : nearestCode;
      nearestCode = nearestCode < 0 ? 0 : nearestCode;
      result[x] = nearestCode;
      origin[x] = x;
    }
    result[255] = origin[255] = 255;
    //==========================================================================

    //==========================================================================
    // 估算出cp code的理想值
    //==========================================================================
    //目標的g code
    double[] targetGArray = WhiteCodeCalculator.getWhitecodeArray(
        getCPCodeRGBArray());
    Interpolation interp = new Interpolation(origin, targetGArray);
    double[] estimate = new double[256];
    //從最接近的code, 以及目標code的內插, 找到預測的code
    for (int x = 1; x < 256; x++) {
      if (result[x] <= 0) {
        estimate[x] = targetGArray[x];
      }
      else {
        estimate[x] = interp.interpolate(result[x], Interpolation.Algo.Linear);
      }

    }
    //==========================================================================

    return estimate;
  }

  /**
   * 是否要進行預測校正, 用預測的方式先逼近, 減少實際量測的時間耗損.
   * @param estimateCalibrate boolean
   */
  public void setEstimateCalibrate(boolean estimateCalibrate) {
    this.estimateCalibrate = estimateCalibrate;
  }

  /**
   * 取得校正過程的資訊
   * @return String
   * @todo M getCalibratedInfomation
   */
  public String getCalibratedInfomation() {
    return null;
  }
}
