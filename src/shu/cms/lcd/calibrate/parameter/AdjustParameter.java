package shu.cms.lcd.calibrate.parameter;

import shu.cms.lcd.calibrate.measured.*;

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 調整參數
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class AdjustParameter
    implements Parameter {
  //============================================================================
  // adjust方式的選擇
  //============================================================================

  public void setCalibrate(boolean luminanceBasedCalibrate,
                           boolean whiteBasedCalibrate,
                           boolean greenBasedCalibrate,
                           boolean whiteBased2Calibrate,
                           boolean luminanceBased2Calibrate) {
    this.luminanceBasedCalibrate = luminanceBasedCalibrate;
    this.whiteBasedCalibrate = whiteBasedCalibrate;
    this.greenBasedCalibrate = greenBasedCalibrate;
    this.whiteBased2Calibrate = whiteBased2Calibrate;
    this.luminanceBased2Calibrate = luminanceBased2Calibrate;
  }

  public void setInterval(int luminanceCalibratedInterval,
                          int whiteCalibratedInterval,
                          int greenCalibratedInterval,
                          int whiteCalibrated2Interval,
                          int luminanceCalibrated2Interval) {
    this.luminanceCalibratedInterval = luminanceCalibratedInterval;
    this.whiteCalibratedInterval = whiteCalibratedInterval;
    this.greenCalibratedInterval = greenCalibratedInterval;
    this.whiteCalibrated2Interval = whiteCalibrated2Interval;
    this.luminanceCalibrated2Interval = luminanceCalibrated2Interval;
  }

  /**
   * 亮度based的校正
   */
  public boolean luminanceBasedCalibrate = false;

  /**
   * white based的校正
   */
  public boolean whiteBasedCalibrate = true;

  /**
   * 對green作smooth的校正
   */
  public boolean smoothGreenCalibrate = true;

  /**
   * green based的校正
   */
  public boolean greenBasedCalibrate = true;

  /**
   * white based的校正2
   */
  public boolean whiteBased2Calibrate = true;

  /**
   * 亮度based的校正2 (New)
   */
  public boolean luminanceBased2Calibrate = false;

  /**
   * variable gamma smooth的回歸起點 (new)
   */
  public int variableGammaSmoothStart = 1;

  /**
   * variable gamma smooth的回歸終點 (new)
   */
  public int variableGammaSmoothEnd = 250;

  public static enum GreenBased {
    White, Model
  }

  public GreenBased smoothGreenBasedOn = GreenBased.Model;

  /**
   * 對綠作smooth的時候是否要作折衷校正
   */
  public boolean smoothGreenCompromiseCalibrate = true;

  /**
   * 對綠作smooth的時候, 驗證prime時所能忍受的threshold大小
   */
  public double gsdfLowPassPrimeCheckThreshold = 0.4;

  public void setSmoothGreen(boolean smoothGreenCalibrate,
                             GreenBased smoothGreenBasedOn,
                             boolean smoothGreenCompromiseCalibrate) {
    this.smoothGreenCalibrate = smoothGreenCalibrate;
    this.smoothGreenBasedOn = smoothGreenBasedOn;
    this.smoothGreenCompromiseCalibrate = smoothGreenCompromiseCalibrate;
  }

  /**
   * 亮度based校正的code間隔
   */
  public int luminanceCalibratedInterval = 2;

  /**
   * white based校正的code間隔
   */
  public int whiteCalibratedInterval = 2;
  /**
   * green based校正的code間隔
   */
  public int greenCalibratedInterval = 1;
  /**
   * white based校正2的code間隔
   */
  public int whiteCalibrated2Interval = 2;
  /**
   * 亮度based校正2的code間隔 (New)
   */
  public int luminanceCalibrated2Interval = 2;

  /**
   * 是否要進行量化崩潰修復
   */
  public boolean quantizationCollapseFix = true;
  /**
   * 量化崩潰時的修復是否考量可修復性
   */
  public boolean concernCollapseFixable = true;

  /**
   * 在低亮度時採用cube檢查 (new)
   */
  public boolean cubeCheckAtLowLuminance = false;
  /**
   * 採用cube校正的code (new)
   */
  public int cubeCheckStartCode = 30;

  public Interpolator.Mode interpolateMethod = Interpolator.Mode.Linear;

  public boolean runModelReport = false;
  public boolean runLuminanceBasedReport = false;
  public boolean runWhiteBasedReport = false;
  public boolean runGreenBasedReport = false;
  public boolean runWhiteBased2Report = false;
  public boolean runLuminanceBased2Report = false;

  public void setRunMeasuredReport(
      boolean runLuminanceBasedReport,
      boolean runWhiteBasedReport,
      boolean runGreenBasedReport,
      boolean runWhiteBased2Report,
      boolean runLuminanceBased2Report) {
    this.runLuminanceBasedReport = runLuminanceBasedReport;
    this.runWhiteBasedReport = runWhiteBasedReport;
    this.runGreenBasedReport = runGreenBasedReport;
    this.runWhiteBased2Report = runWhiteBased2Report;
    this.runLuminanceBased2Report = runLuminanceBased2Report;
  }

  public void setAdjustRange(int start, int end) {
    this.adjustStart = start;
    this.adjustEnd = end;
  }

  public int adjustStart = 0;
  public int adjustEnd = 255;

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("[Threshold]");
    buf.append("\ngsdfLowPassPrimeCheckThreshold: ");
    buf.append(gsdfLowPassPrimeCheckThreshold);

    buf.append("\n[Calibrate]");
    buf.append("\nluminanceBasedCalibrate[" + luminanceCalibratedInterval +
               "]: " + luminanceBasedCalibrate);
    buf.append("\nwhiteBasedCalibrate[" + whiteCalibratedInterval +
               "]: " + whiteBasedCalibrate);
    buf.append("\ngreenBasedCalibrate[" + greenCalibratedInterval +
               "]: " + greenBasedCalibrate);
    buf.append("\nluminanceBasedCalibrate2[" + luminanceCalibrated2Interval +
               "]: " + luminanceBased2Calibrate);

    buf.append("\nsmoothGreenCalibrated: ");
    buf.append(smoothGreenCalibrate);
    buf.append("\nsmoothGreenCompromiseCalibrate: ");
    buf.append(smoothGreenCompromiseCalibrate);
    buf.append("\nsmoothGreenBasedOn: ");
    buf.append(smoothGreenBasedOn);
    buf.append("\nAdjustRange: ");
    buf.append(adjustStart + "~" + adjustEnd);

    buf.append("\n[Options]");
    buf.append("\nquantizationCollapseFix: ");
    buf.append(quantizationCollapseFix + " (concernCollapseFixable: " +
               concernCollapseFixable + ")");
    buf.append("\nvariableGammaSmoothStart: ");
    buf.append(variableGammaSmoothStart);
    buf.append("\nvariableGammaSmoothEnd: ");
    buf.append(variableGammaSmoothEnd);

    buf.append("\ncubeCalibrateAtLowLuminance: ");
    buf.append(cubeCheckAtLowLuminance + " (startCode: " +
               cubeCheckStartCode + ")");
    buf.append("\nInterpolateMethod: ");
    buf.append(interpolateMethod);

    return buf.toString();
  }
  //============================================================================

}
