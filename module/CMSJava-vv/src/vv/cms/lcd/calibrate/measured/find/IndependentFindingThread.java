package vv.cms.lcd.calibrate.measured.find;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.depend.RGBBase.*;
import shu.cms.colorspace.depend.DeviceDependentSpace.MaxValue;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import vv.cms.lcd.calibrate.measured.algo.*;
import vv.cms.lcd.calibrate.measured.util.*;
import shu.cms.lcd.material.*;
import vv.cms.lcd.material.*;

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
public class IndependentFindingThread
    extends CIEuv1960FindingThread {
  /**
   * IndependentFindingThread
   *
   * @param index int
   * @param targetMaxValue MaxValue
   * @param info CalibratedInfo
   * @param calibrated boolean[]
   * @param luminanceCalibrate boolean
   * @param chromaticCalibrate boolean
   * @param white CIEXYZ
   * @param accessIF CalibratorAccessIF
   */
  public IndependentFindingThread(int index, MaxValue targetMaxValue,
                                  FindingInfo info,
                                  boolean[] calibrated,
                                  boolean luminanceCalibrate,
                                  boolean chromaticCalibrate,
                                  CIEXYZ white, CalibratorAccessIF accessIF
      ) {
    super(index, targetMaxValue, info, calibrated, luminanceCalibrate,
          chromaticCalibrate, false, white, accessIF, false);
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
    info.setStep(index, access.getInitStep());
    //最小的step
    double minstep = targetMaxValue.getStepIn255();
    //========================================================================

    //以不同單位慢慢逼近
    AlgoResult result = findNearestResultInLoop(minstep, targetXYZ,
                                                chromaticInStep,
                                                luminanceInStep);

    //========================================================================
    // 收尾動作
    //========================================================================
    terminate(targetXYZ, result);
    //========================================================================
  }

  private boolean cubeCalibrateFinal = AutoCPOptions.get(
      "FindingThread_CubeCalibrateFinal");
  private boolean luminanceCalibrateFinal = AutoCPOptions.get(
      "FindingThread_LuminanceCalibrateFinal");
  private boolean chromaticInStep = AutoCPOptions.get(
      "FindingThread_ChromaticInStep");
  private boolean luminanceInStep = AutoCPOptions.get(
      "FindingThread_LuminanceInStep");
  /**
   * 最後再做一次check(不建議使用)
   */
  private boolean finalCheck = AutoCPOptions.get(
      "FindingThread_FinalCheck");
  public LCDModel model;

  /**
   * 尋找流程:
   * 1. 找到最接近明度
   * 2. 找到最接近色度
   * 3. 再找到最接近明度
   * 4. 重複2/3直到過程中找到的rgb開始有重複
   *
   * @param targetXYZ CIEXYZ
   * @param startRGB RGB
   * @param step double
   * @param chromaticInStep boolean 色度尋找過程中只找一個step(否則就是直接找到定位)
   * @param luminanceInStep boolean 明度尋找過程中只找一個step(否則就是直接找到定位)
   * @return Result
   */
  protected AlgoResult findNearestRGB(final CIEXYZ targetXYZ,
                                      final RGB startRGB,
                                      final double step,
                                      boolean chromaticInStep,
                                      boolean luminanceInStep) {
    return findNearestRGB(targetXYZ, startRGB, step, chromaticInStep,
                          luminanceInStep, luminanceCalibrateFinal,
                          cubeCalibrateFinal);
  }

  /**
   * 尋找流程:
   * 1. 找到最接近明度
   * 2. 找到最接近色度
   * 3. 再找到最接近明度
   * 4. 重複2/3直到過程中找到的rgb開始有重複
   *
   * @param targetXYZ CIEXYZ
   * @param startRGB RGB
   * @param step double
   * @param chromaticInStep boolean 色度尋找過程中只找一個step(否則就是直接找到定位)
   * @param luminanceInStep boolean 明度尋找過程中只找一個step(否則就是直接找到定位)
   * @param luminanceCalibrateFinal boolean 最後的尋找是以明度(否則就是色度)
   * @param cubeCalibrateFinal boolean 最後以cube search為最後校正
   * @return AlgoResult
   */
  protected AlgoResult findNearestRGB(final CIEXYZ targetXYZ,
                                      final RGB startRGB,
                                      final double step,
                                      boolean chromaticInStep,
                                      boolean luminanceInStep,
                                      boolean luminanceCalibrateFinal,
                                      boolean cubeCalibrateFinal) {

    if (findByModel && model != null) {
      RGB rgb = model.getRGB(targetXYZ, false);
      rgb.quantization(RGB.MaxValue.Int10Bit);
      AlgoResult result = new AlgoResult(rgb, null, new RGB[0], null, index, 0);
      return result;
    }

    RGB nearestRGB = startRGB;
    RGB luminanceNearestRGB = null;
    RGB chromaticNearestRGB = null;
    DuplicateLinkedList<RGB>
        list = new DuplicateLinkedList<RGB> (nearestRGB);
    DuplicateLinkedList<RGB>
        totallist = new DuplicateLinkedList<RGB> ();
    /**
     * list與cycle重複的差異性:
     * 如果list重複, 代表cycle最後每次找到的都一樣.
     * 如果cycle重複, 代表一次cycle內, 不論是 明色明、明明色 兩種順序, 已經有重複找到的狀況.
     *         list
     *          T F
     * cycle  T 1 2
     *        F 3 4
     *
     * list跟cycle總共會有四種狀況, 其意義如下:
     * 1. 一round跟一cycle都有重複的情形 (一round含n次cycle)
     * 2.
     */
    DuplicateLinkedList<RGB>
        cycle = new DuplicateLinkedList<RGB> (nearestRGB);
    AlgoResult result = null;

    //==========================================================================
    // 明度先尋找
    //==========================================================================
    if (luminanceCalibrate) {
      //先找到最接近明度
      result = luminanceIterative(nearestRGB, targetXYZ, step, false,
                                  finalCheck);
      nearestRGB = result.nearestRGB;
      totallist.addAll(result.totalList);
      cycle.add(nearestRGB);
      plot(result, Color.red);
    }
    //==========================================================================

    // 是否有彩度校正
    boolean chromaticCalibrated = false;
    // 是否有明度校正
    boolean luminanceCalibrated = false;
    // 是否有重複的case
    boolean duplicateCase = false;
    // 是否是沒有進行校正的case
    boolean nonCalibratedCase = false;
    StringBuilder listbuf = new StringBuilder();

    do {
      //========================================================================
      // 找色度一次
      //========================================================================
      if (chromaticCalibrate) {
        chromaticNearestRGB = nearestRGB;
        result = chromaticIterative(nearestRGB, targetXYZ, step,
                                    chromaticInStep, finalCheck);
        chromaticCalibrated = !chromaticNearestRGB.equals(result.nearestRGB);
        chromaticNearestRGB = result.nearestRGB;
        nearestRGB = chromaticNearestRGB;
        totallist.addAll(result.totalList);
        cycle.add(nearestRGB);
        addTrace("(" + index +
                 ") chromatic calibrate nearestRGB: " + nearestRGB);
      }
      //========================================================================

      //========================================================================
      // 再找到最接近明度
      //========================================================================
      if (luminanceCalibrateFinal && luminanceCalibrate) {
        luminanceNearestRGB = nearestRGB;
        result = luminanceIterative(nearestRGB, targetXYZ, step,
                                    luminanceInStep, finalCheck);
        luminanceCalibrated = !luminanceNearestRGB.equals(result.nearestRGB);
        luminanceNearestRGB = result.nearestRGB;
        nearestRGB = luminanceNearestRGB;
        totallist.addAll(result.totalList);
        cycle.add(nearestRGB);
        addTrace("(" + index +
                 ") luminance calibrate nearestRGB: " + nearestRGB);
        plot(result, Color.red);
      }
      //========================================================================

      //一個循環
      list.add(nearestRGB);
      duplicateCase = list.duplicate(); //|| cycleDuplicateCase;
      if (list.duplicate()) {
        //如果list的內容開始重複
        duplicateLog(listbuf, list, cycle);
      }

      nonCalibratedCase = ! (chromaticCalibrated || luminanceCalibrated);
    }
    //兩次以上輪迴是相同結果 就停止, 或者兩個都沒有校正到, 也結束
    while (! (duplicateCase || nonCalibratedCase));

    String situation = getSituation(duplicateCase, nonCalibratedCase,
                                    chromaticCalibrated, luminanceCalibrated);
    listbuf.insert(0, situation);
    addTrace(listbuf.toString());

    //==========================================================================
    // 從cube檢查是不是最靠近目標的
    //==========================================================================
    AlgoResult cubeResult = cubeCheckInOneJNDI(nearestRGB, targetXYZ, step);
    //==========================================================================
    if (cubeCalibrateFinal && cubeResult != null) {
      nearestRGB = cubeResult.nearestRGB;
    }
    result.setInfomation(nearestRGB, list, totallist);
    result.candilateNearestRGB = (cubeResult != null) ? cubeResult.nearestRGB :
        null;
    return result;
  }

  /**
   * 如果list的內容開始重複, 將重覆了什麼儲存起來
   * @param listbuf StringBuilder
   * @param list DuplicateLinkedList
   * @param cycle DuplicateLinkedList
   */
  private void duplicateLog(StringBuilder listbuf,
                            DuplicateLinkedList<RGB> list,
      DuplicateLinkedList<RGB> cycle
      ) {
    //======================================================================
    // 將重覆了什麼儲存起來
    //======================================================================
    listbuf.append("\nlist[" + list.duplicate(true) + "] cycle[" +
                   cycle.duplicate(true) + "]");
    listbuf.append("\nlist(" + list.size() + "): ");
    for (RGB rgb : list) {
      listbuf.append(rgb.toString());
      listbuf.append(' ');
    }
    listbuf.append("\ncycle(" + cycle.size() + "): ");
    for (RGB rgb : cycle) {
      listbuf.append(rgb.toString());
      listbuf.append(' ');
    }
    //======================================================================
  }

  private String getSituation(boolean duplicateCase, boolean nonCalibratedCase,
                              boolean chromaticCalibrated,
                              boolean luminanceCalibrated) {
    String situation = "(" + index + ") stop result: duplicate[" +
        (duplicateCase ? 'O' : 'X') +
        "] nonCalibrated[" + (nonCalibratedCase ? 'O' : 'X') +
        "] (chromatic[" + (chromaticCalibrated ? 'O' : 'X') +
        "] luminance[" + (luminanceCalibrated ? 'O' : 'X') +
        "]) (chromaticCalibrate[" + (chromaticCalibrate ? 'O' : 'X') +
        "] luminanceCalibrate[" + (luminanceCalibrate ? 'O' : 'X') + "])";
    return situation;
  }

}
