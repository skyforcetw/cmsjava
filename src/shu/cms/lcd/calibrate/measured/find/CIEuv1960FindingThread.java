package shu.cms.lcd.calibrate.measured.find;

import java.text.*;
import java.util.*;
import java.util.List;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.depend.RGBBase.*;
import shu.cms.colorspace.depend.DeviceDependentSpace.MaxValue;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.calibrate.measured.algo.*;
import shu.cms.lcd.calibrate.measured.util.*;
import shu.cms.lcd.material.*;
import shu.cms.util.*;
import shu.math.array.*;
import shu.util.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CIEuv1960FindingThread
    extends Thread {

  /**
   * 是否要把每一步都plot出來
   */
  private boolean plotStep = AutoCPOptions.get("FindingThread_PlotStep");
  /**
   * 提供校正資訊的介面
   */
  protected CalibratorAccessIF access;
  /**
   * 白點
   */
  private CIEXYZ white;
  /**
   * 是否要進行色度校正
   */
  protected boolean chromaticCalibrate = true;
  /**
   * 是否要進行亮度校正
   */
  protected boolean luminanceCalibrate = true;
  /**
   * 在最後一次做Around搜尋的時候, 用cube擴展
   */
  private boolean cubeAroundMode = true;

  public CIEuv1960FindingThread(int index, MaxValue targetMaxValue,
                                FindingInfo info, boolean[] calibrated,
                                boolean luminanceCalibrate,
                                boolean chromaticCalibrate,
                                boolean cubeAroundMode, CIEXYZ white,
                                CalibratorAccessIF accessIF, boolean plot) {
    this.index = index;
    this.targetMaxValue = targetMaxValue;
    this.calibrated = calibrated;
    this.access = accessIF;
    initAlgorithm(access);
    this.luminanceCalibrate = luminanceCalibrate;
    this.chromaticCalibrate = chromaticCalibrate;
    this.cubeAroundMode = cubeAroundMode;
    this.white = white;

    this.info = info;
    this.plotting = plot;
    if (plot) {
      p = new FindingPlotter("index: " + Integer.toString(index));
      if (plotStep) {
        pstep = new FindingPlotter("index: " + Integer.toString(index) +
                                   " step");
      }
    }
  }

  protected boolean plotting = false;

  protected void initAlgorithm(CalibratorAccessIF c) {
    this.deltaENearAlgo = c.getDeltaE00NearestAlogorithm();
    this.lightnessNearAlgo = c.getLightnessNearestAlogorithm();
    this.uvNearAlgo = c.getCIEuv1960NearestAlogorithm();
    this.lightnessAroundAlgo = c.getLightnessAroundAlgorithm();
    this.chromaAroundAlgo = c.getChromaticAroundAlgorithm();
    this.compoundNearAlogo = c.getCompoundNearestAlogorithm();
    this.stepAroundAlgo = c.getStepAroundAlgorithm();
    this.cubeNearAlgo = c.getCubeNearestAlgorithm();
  }

  private DeltaE00NearestAlgorithm deltaENearAlgo;
  private LightnessNearestAlgorithm lightnessNearAlgo;
  private CIEuv1960NearestAlgorithm uvNearAlgo;
  private CompoundNearestAlgorithm compoundNearAlogo;
  private CubeNearestAlgorithm cubeNearAlgo;

  private LightnessAroundAlgorithm lightnessAroundAlgo;
  private ChromaticAroundAlgorithm chromaAroundAlgo;
  private StepAroundAlgorithm stepAroundAlgo;

  /**
   * 把plotter關掉, 就是把繪圖關掉
   */
  public void closePlotter() {
    if (plotting) {
      p.close();
      if (plotStep) {
        pstep.close();
      }
    }
  }

  /**
   * 校正的資訊
   */
  protected FindingInfo info;
  /**
   * 用作繪圖用的物件
   */
  private FindingPlotter pstep, p;

  /**
   * 目前校正的索引值
   */
  protected int index;
  /**
   * 目標的maxValue
   */
  protected MaxValue targetMaxValue;
  /**
   * 用來判斷是否已經做了校正
   */
  protected boolean[] calibrated;

  /**
   * 計算校正的參考index
   * @param targetXYZ CIEXYZ
   * @param result Result
   */
  private void calculateIndex(CIEXYZ targetXYZ, AlgoResult result) {
    CIEXYZ XYZ = result.getNearestXYZ();
    //色差
    double deltaE = deltaENearAlgo.getDelta(targetXYZ, XYZ)[0];
    //JNDI差
    double deltaJNDI = Math.abs(lightnessNearAlgo.getDelta(targetXYZ, XYZ)[0]);
    //u'v'差
    double[] duvp = uvNearAlgo.getDelta(targetXYZ, XYZ);
    info.setIndex(index, result.getIndex(), deltaE, deltaJNDI, duvp);
  }

  protected void plot(CIExyY targetxyY) {
    if (plotting) {
      //========================================================================
      //標出目標點
      //========================================================================
      if (plotStep) {
        pstep.plotTargetAtuv(targetxyY.getuvPrimeYValues());
      }
      p.plotTargetAtuv(targetxyY.getuvPrimeYValues());
      //========================================================================
    }
  }

  private StringBuilder traceBuf = new StringBuilder();
  public String getTrace() {
    return traceBuf.toString();
  }

  private boolean enableTrace = true;

  protected void addTrace(String msg) {
    if (enableTrace) {
      traceBuf.append(msg);
      traceBuf.append('\n');
    }
  }

  public void run() {
    if (calibrated[index] == true) {
      addTrace("(" + index + ") calibrated, stop calibrate");
      return;
    }
    CIExyY targetxyY = access.getTargetxyY(index);
    plot(targetxyY);

    //========================================================================
    // 初始化
    //========================================================================
    //目標點
    final CIEXYZ targetXYZ = targetxyY.toXYZ();
    //設定初始step
    info.setStep(index, RGB.MaxValue.Int8Bit);
    //最小的step
    double minstep = targetMaxValue.getStepIn255();
    //========================================================================

    //以不同單位慢慢逼近
    AlgoResult result = findNearestResultInLoop(minstep, targetXYZ, true, false);

    //========================================================================
    // 找到最近點後, 找出該點周邊點.
    // 看看周邊點之中, 最接近點是不是真的離目標點最近.
    //========================================================================
    AlgoResult r1 = getNearestRGBInAround(result.nearestRGB, targetXYZ, minstep);
    addTrace("(" + index + ") find rgb in around:" + r1.nearestRGB +
             " (dist:" + r1.getIndex() + ")");
    //========================================================================

    //========================================================================
    // 從過程中找到符合的點
    //========================================================================
    //把around和尋找過程中的RGB都整理起來
    List<RGB> totalList = new LinkedList<RGB> (r1.totalList);
    totalList.addAll(result.totalList);

    //從走過的路程中找到符合複合要求的rgb點
    result = getNearestRGBInCompound(targetXYZ, totalList);
    //========================================================================

    //========================================================================
    // 收尾動作
    //========================================================================
    terminate(targetXYZ, result);
    //========================================================================
  }

  /**
   * 以迴圈不斷縮小step, 找到最接近的結果
   * @param minstep double
   * @param targetXYZ CIEXYZ
   * @param chromaticInStep boolean 色度的尋找每次僅一step(否則就是一次到定位)
   * @param luminanceInStep boolean 亮度的尋找每次僅一step(否則就是一次到定位)
   * @return Result
   */
  protected AlgoResult findNearestResultInLoop(final double minstep,
                                               final CIEXYZ targetXYZ,
                                               boolean chromaticInStep,
                                               boolean luminanceInStep) {
    AlgoResult result = null;
    RGB nearestRGB = info.getCalibratedRGB(index);

    //========================================================================
    // 以不同單位慢慢逼近
    //========================================================================
    for (double step = 1; step >= minstep; step /= 2.) {
      addTrace("(" + index + ") start calibrate, in step " + step);
      //找到最接近的RGB的結果
      result = findNearestRGB(targetXYZ, nearestRGB, step, chromaticInStep,
                              luminanceInStep);
      //更新最接近的RGB
      nearestRGB = result.nearestRGB;
      if (!findByModel) {
        addTrace("(" + index + ") find rgb:" + nearestRGB +
                 ", in step " + step + " (dist:" + result.getIndex() + ")");
      }
      //計算出更細的step
      RGB.MaxValue nowStep = RGB.MaxValue.getIntegerMaxValueByMax( (int) (RGB.
          MaxValue.Int8Bit.max / step));
      //更新step
      info.setStep(index, nowStep);
    }
    //========================================================================

    nearestRGB = null;
    return result;
  }

  /**
   * 直接由model預測, 而不採用任何find algo.
   */
  public boolean findByModel = false;

  protected void terminate(CIEXYZ targetXYZ, AlgoResult result) {
    if (true == findByModel) {
      //校正所取得的RGB
      info.setCalibratedRGB(index, result.nearestRGB);
      //標定已經校正ok
      calibrated[index] = true;
    }
    else {
      //========================================================================
      // 收尾動作
      //========================================================================
      //計算索引值
      calculateIndex(targetXYZ, result);
      //校正所取得的RGB
      info.setCalibratedRGB(index, result.nearestRGB);
      if (result.candilateNearestRGB != null) {
        info.setCandilateCalibratedRGB(index, result.candilateNearestRGB);
      }
      //標定已經校正ok
      calibrated[index] = true;
      //========================================================================
      addTrace("(" + index + ") calibrate end (rgb:" + result.nearestRGB +
               ") (dist:" + result.getIndex() + ")");
      double deltaE = info.getDeltaEIndexArray()[index];
      double dJNDI = info.getDeltaJNDIIndexArray()[index];
      double[] duv = info.getDeltaunvpIndexArray()[index];
      addTrace("(" + index + ") deltaE: " + df4.format(deltaE) + " deltaJNDI: " +
               df4.format(dJNDI) + " deltauv': " +
               DoubleArray.toString(df4, duv));

      access.trace(traceBuf.toString());
    }
  }

  private final static DecimalFormat df4 = new DecimalFormat("####.####");

  protected void plotStep(AlgoResult result, Color color) {
    if (this.plotting && plotStep) {
      pstep.plot(result, color);
    }
  }

  protected void plot(AlgoResult result, Color color) {
    if (this.plotting) {
      p.plot(result, color);
    }
  }

  /**
   * 亮度的迭代尋找
   * 依照deltaJNDI, 調整到更逼近的RGB, RGB的調整單位以white為基礎,
   *  也就是r/g/b同時加減一個單位做調整
   * @param initRGB RGB
   * @param targetXYZ CIEXYZ
   * @param step double
   * @param runonce boolean
   * @param finalCheck boolean 最後再做一次check
   * @return Result
   */
  protected final AlgoResult luminanceIterative(final RGB initRGB,
                                                final CIEXYZ targetXYZ,
                                                double step, boolean runonce,
                                                boolean finalCheck) {
    addTrace("(" + index + ") start luminance calibrate");
    RGB nearestRGB = initRGB;
    DuplicateLinkedList<RGB> list = new DuplicateLinkedList<RGB> (nearestRGB);
    DuplicateLinkedList<RGB> totallist = new DuplicateLinkedList<RGB> ();
    AlgoResult result = null;
    int redundantMeasure = 0;

    do {
      //計算出delta
      double[] delta = lightnessNearAlgo.getDelta(targetXYZ, nearestRGB);
      //依照delta, 產生更逼近的aroundRGB
      RGB[] aroundRGB = lightnessAroundAlgo.getAroundRGB(nearestRGB, delta,
          step);

      //========================================================================
      // result處理
      //========================================================================
      //亮度找到最近的(以JNDI做計算)
      result = lightnessNearAlgo.getNearestRGB(targetXYZ, aroundRGB);
      redundantMeasure += result.getRedundantMeasure(nearestRGB);
      nearestRGB = result.nearestRGB;
      //========================================================================
      list.add(nearestRGB);
      totallist.addAll(result.totalList);
      plotStep(result, Color.black);
      //========================================================================

      info.addCalibrateCount();
      addTrace("(" + index + ") " +
               Arrays.toString(aroundRGB) + " near:" + nearestRGB);
    }
    //如果有找到重複的 就停止
    while (!list.duplicate() && !runonce);

    if (finalCheck) {
      //再用delta JNDI判斷出誤差最小的
      result = lightnessNearAlgo.getNearestRGB(targetXYZ, list,
                                               NearestRangeCheck);
      result.setInfomation(initRGB, list, totallist);
    }
    addTrace("(" + index + ") luminance calibrate end (dist:" +
             result.getIndex() + ")");
    result.setRedundantMeasure(redundantMeasure);
    this.redundantMeasure += redundantMeasure;
    return result;
  }

  protected int redundantMeasure;

  /**
   * 只檢查最接近幾個的範圍(0的話則代表全檢查)
   */
  private final static int NearestRangeCheck = 0;

  /**
   * 色度的迭代尋找
   * @param initRGB RGB 初始的RGB
   * @param targetXYZ CIEXYZ 目標XYZ
   * @param step double 迭代的單位
   * @param runonce boolean 是否只跑一次尋找
   * @param finalCheck boolean 最後再做一次check
   * @return Result
   */
  protected final AlgoResult chromaticIterative(final RGB initRGB,
                                                CIEXYZ targetXYZ,
                                                double step, boolean runonce,
                                                boolean finalCheck) {
    addTrace("(" + index + ") start chromatic calibrate");
    RGB nearestRGB = initRGB;
    DuplicateLinkedList<RGB>
        list = new DuplicateLinkedList<RGB> (nearestRGB);
    DuplicateLinkedList<RGB>
        totallist = new DuplicateLinkedList<RGB> ();
    CIEuv1960NearestAlgorithm nearAlgo = uvNearAlgo;
    AlgoResult result = null;
    int redundantMeasure = 0;

    do {
      //計算出delta u'v'
      double[] delta = uvNearAlgo.getDelta(targetXYZ, nearestRGB);
      //由du'v'來推算最有可能逼近目標點的RGB組合
      RGB[] aroundRGB = chromaAroundAlgo.getAroundRGB(nearestRGB, delta, step);

      //========================================================================
      // result處理
      //========================================================================
      // 找到色度座標上最接近的解
      result = nearAlgo.getNearestRGB(targetXYZ, aroundRGB);
      redundantMeasure += result.getRedundantMeasure(nearestRGB);
      //色度找到最近
      nearestRGB = result.nearestRGB;
      //========================================================================
      list.add(nearestRGB);
      totallist.addAll(result.totalList);
      plotStep(result, Color.green);
      //========================================================================

      info.addCalibrateCount();
      addTrace("(" + index + ") " +
               Arrays.toString(aroundRGB) + " near:" + nearestRGB);
      /**
       * 除了色度找到最近的以外, 跟參考白相比, delta u'v'還要保持相同正負號
       * 至於要正還是要負, 要計算參考白與黑點而定出來
       */
    }
    //如果有找到重複的 就停止, 或者只需要跑一次 也停止
    while ( (!list.duplicate()) && !runonce);

    if (finalCheck) {
      //再用色差判斷出最小的
      result = nearAlgo.getNearestRGB(targetXYZ, list, NearestRangeCheck);
      result.setInfomation(nearestRGB, list, totallist);
    }
    addTrace("(" + index + ") chromatic calibrate end (dist:" +
             result.getIndex() + ")");
    result.setRedundantMeasure(redundantMeasure);
    this.redundantMeasure += redundantMeasure;
    return result;
  }

  /**
   * 尋找流程:
   * 1. 找到最接近亮度
   * 2. 找到最接近色度
   * 3. 再找到最接近亮度
   * 4. 重複2/3直到過程中找到的rgb開始有重複
   *
   * @param targetXYZ CIEXYZ
   * @param rgb RGB
   * @param step double
   * @param chromaticInStep boolean 色度尋找過程中只找一個step(否則就是直接找到定位)
   * @param luminanceInStep boolean 亮度尋找過程中只找一個step(否則就是直接找到定位)
   * @return Result
   */
  protected AlgoResult findNearestRGB(final CIEXYZ targetXYZ, final RGB rgb,
                                      final double step,
                                      boolean chromaticInStep,
                                      boolean luminanceInStep) {
    RGB nearestRGB = rgb;
    RGB luminanceNearestRGB = null;
    RGB chromaticNearestRGB = null;
    DuplicateLinkedList<RGB>
        list = new DuplicateLinkedList<RGB> (nearestRGB);
    DuplicateLinkedList<RGB>
        totallist = new DuplicateLinkedList<RGB> ();

    NearestAlgorithm nearestAlgo = access.getIndexNearestAlogorithm();

    if (luminanceCalibrate) {
      //先找到最接近亮度
      AlgoResult result = luminanceIterative(nearestRGB, targetXYZ, step, false, true);
      totallist.addAll(result.totalList);
      nearestRGB = result.nearestRGB;
      list.add(nearestRGB);
      plot(result, Color.red);
    }

    do {

      //========================================================================
      // 找色度一次
      //========================================================================
      if (chromaticCalibrate) {
        AlgoResult result = chromaticIterative(nearestRGB, targetXYZ, step,
                                               chromaticInStep, true);
        totallist.addAll(result.totalList);
        chromaticNearestRGB = result.nearestRGB;
        nearestRGB = chromaticNearestRGB;
        addTrace("(" + index +
                 ") chromatic calibrate nearestRGB: " + nearestRGB);
      }
      //========================================================================

      //========================================================================
      // 再找到最接近亮度
      //========================================================================
      if (luminanceCalibrate) {
        AlgoResult result = luminanceIterative(nearestRGB, targetXYZ, step,
                                               luminanceInStep, true);
        totallist.addAll(result.totalList);
        luminanceNearestRGB = result.nearestRGB;
        nearestRGB = luminanceNearestRGB;
        addTrace("(" + index +
                 ") luminance calibrate nearestRGB: " + nearestRGB);
        plot(result, Color.red);
      }
      //========================================================================
      list.add(nearestRGB);

    }
    //兩次以上輪迴是相同結果 就停止
    while (!list.duplicate());
    //色度與亮度校正結果相同 就停止 因為代表根本不需要校了, 但事實證明這樣的方式不佳

    //再找差距最小的
    AlgoResult result = nearestAlgo.getNearestRGB(targetXYZ, list,
                                                  NearestRangeCheck);
    result.setInfomation(nearestRGB, list, totallist);
    nearestRGB = result.nearestRGB;

    addTrace("(" + index + ") findNearestRGB end (dist:" +
             result.getIndex() + ")");

    return result;
  }

  /**
   * 以deltaE為索引值找到最近的RGB
   * @param targetXYZ CIEXYZ
   * @param rgbList List
   * @return Result
   * @deprecated
   */
  protected AlgoResult getNearestRGBInDeltaE(CIEXYZ targetXYZ, List<RGB>
      rgbList) {
    RGB[] rgbArray = RGBArray.toRGBArray(rgbList);
    AlgoResult result = deltaENearAlgo.getNearestRGB(targetXYZ, rgbArray);
    return result;
  }

  /**
   * 複合的考量之下, 找到最近的RGB
   * @param targetXYZ CIEXYZ
   * @param rgbList List
   * @return Result
   */
  private AlgoResult getNearestRGBInCompound(CIEXYZ targetXYZ, List<RGB>
      rgbList) {
    RGB[] rgbArray = RGBArray.toRGBArray(rgbList);
    AlgoResult result = compoundNearAlogo.getNearestRGB(targetXYZ, rgbArray);
    if (!result.passAllQualify || result.allQualifyNonPass) {
      //如果不是全部的資格都通過 或者全部的資格都沒通過, 就是non-Qualify
      info.setNonQualify(index);
      Logger.log.info("index(" + index + ") NonQualify: passAllQualify[" +
                      result.passAllQualify + "] allQualifyNonPass[" +
                      result.allQualifyNonPass + "]");
    }
    return result;
  }

  /**
   * 利用around擴展的方式(擴展8個點) 並且迭帶找到最接近點
   * @param initRGB RGB
   * @param targetXYZ CIEXYZ
   * @param step double
   * @return Result
   */
  private AlgoResult getNearestRGBInAround(final RGB initRGB,
                                           final CIEXYZ targetXYZ,
                                           double step) {
    //初始的rgb
    RGB rgb = initRGB;
    //是否找到最接近點?
    boolean centerNearest = false;
    //採用立方體擴展搜尋(27個點)
    boolean cubeSearch = false;
    //尋找的結果
    AlgoResult result = null;
    //尋找過程的rgb
    DuplicateLinkedList<RGB> totalList = new DuplicateLinkedList<RGB> ();

    for (int t = 0; t < MaxAroundIterativeTimes; t++) {
      RGB[] aroundRGB = null;
      if (cubeAroundMode && cubeSearch) {
        //是否要進行立方體搜尋? 立方體搜尋將尋找27個點
        aroundRGB = stepAroundAlgo.getCubeAroundRGB(rgb, step);
        //立方體搜尋次數+1
        info.addCubeSearchTimes();
      }
      else {
        //步進搜尋
        aroundRGB = stepAroundAlgo.getAroundRGB(rgb, step, true);
      }
      //將找到的rgb收集起來
      totalList.addAll(aroundRGB);

      //只是為了取aroundXYZ而已
      CIEXYZ[] aroundXYZ = uvNearAlgo.getNearestRGB(targetXYZ,
          aroundRGB).aroundXYZ;

      if (plotting) {
        //======================================================================
        // 繪圖
        //======================================================================
        for (int x = 1; x < aroundXYZ.length; x++) {
          p.plot(aroundXYZ[x], Color.cyan);
          p.plotRGB(aroundRGB[x], Color.cyan, null);
        }
        p.plot(aroundXYZ[0], Color.blue);
        p.plotRGB(aroundRGB[0], Color.blue, null);
        //======================================================================
      }
      //色差最接近的點是中央點? 用三種色差公式方式下去評估
      boolean[] bools = isFirstNearestXYZInDeltaE(targetXYZ, aroundXYZ,
                                                  white);
      //至少一個色差公式說是最近點
      centerNearest = Utils.or(bools);

      //取最接近的rgb
      result = deltaENearAlgo.getNearestRGB(targetXYZ, aroundRGB);

      if (cubeSearch || centerNearest) {
        if (cubeSearch) {
          if (!rgb.equals(result.nearestRGB)) {
            //最後一次量測 or cube量測之後, 發現最接近點移動了!
            //所以不能就這麼結束, 繼續找下去.
            String measure = cubeAroundMode ? "cube measure." :
                "last measure.";
            Logger.log.info("index(" + index +
                            ") nearestRGB is not equal in " + measure);
            rgb = result.nearestRGB;
            cubeSearch = false;
            continue;
          }
          else {
            //最後的一次量測之後發現rgb沒有變動, 已經到達最接近點, 所以結束.
            result.totalList = totalList;
            return result;
          }
        }
        else {
          //nearest之後, 再多一加次量測
          cubeSearch = true;
        }

      }
      else {
        //沒找到最接近點, 繼續輪迴吧
        if (rgb.equals(result.nearestRGB)) {
          Logger.log.info("index(" + index + ") nearestRGB duplicate.");
        }
        else {
          rgb = result.nearestRGB;
        }
      }
    }
    //達到最大的輪迴次數
    access.addMaxAroundTouched();
    Logger.log.info("index(" + index + ") MaxAroundIterativeTimes(" +
                    MaxAroundIterativeTimes + ") meet.");
    result.totalList = totalList;

    return result;
  }

  /**
   * Around最多的迭代次數
   */
  protected final static int MaxAroundIterativeTimes = 100;

  /**
   * 以三種色差公式同時考量, 第一個aroundXYZ是否是色差最小者
   * @param targetXYZ CIEXYZ
   * @param aroundXYZ CIEXYZ[]
   * @param white CIEXYZ
   * @return boolean[]
   */
  protected boolean[] isFirstNearestXYZInDeltaE(CIEXYZ targetXYZ,
                                                CIEXYZ[] aroundXYZ,
                                                CIEXYZ white) {
    boolean[] result = new boolean[3];
    result[0] = MeasuredUtils.isFirstNearestXYZInDeltaE00(targetXYZ,
        aroundXYZ, white, false);
    result[1] = MeasuredUtils.isFirstNearestXYZInDeltaE(targetXYZ,
        aroundXYZ, white);
    result[2] = MeasuredUtils.isFirstNearestXYZInDeltaEuv(targetXYZ,
        aroundXYZ, white);
    return result;
  }

  /**
   * 多餘的量測次數, 意思就是指, 量測後卻沒有採用的RGB
   * @return int
   */
  public int getRedundantMeasure() {
    return redundantMeasure;
  }

  public void setEnableTrace(boolean enableTrace) {
    this.enableTrace = enableTrace;
  }

  /**
   * 從cube檢查是不是最靠近目標的
   * @param nearestRGB RGB
   * @param targetXYZ CIEXYZ
   * @param step double
   * @return AlgoResult
   */
  protected AlgoResult cubeCheckInOneJNDI(final RGB nearestRGB,
                                          final CIEXYZ targetXYZ,
                                          double step) {
    if (cubeCheckInOneJNDI) {
      RGB[] aroundRGB = stepAroundAlgo.getCubeAroundRGB(nearestRGB, step);
      AlgoResult result = cubeNearAlgo.getNearestRGB(targetXYZ, aroundRGB);
      if (!nearestRGB.equals(result.nearestRGB)) {
        this.addTrace("Find " + nearestRGB + "->" + result.nearestRGB +
                      " in cube check.");
      }
      return result;
    }
    return null;
  }

  /**
   * 從cube檢查是不是最靠近目標的
   */
  private boolean cubeCheckInOneJNDI = true;
  public void setCubeCheckInOneJNDI(boolean cubeCheckInOneJNDI) {
    this.cubeCheckInOneJNDI = cubeCheckInOneJNDI;
  }
}
