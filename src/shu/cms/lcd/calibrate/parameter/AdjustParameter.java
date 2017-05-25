package shu.cms.lcd.calibrate.parameter;

import shu.cms.lcd.calibrate.measured.*;

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �վ�Ѽ�
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
  // adjust�覡�����
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
   * �G��based���ե�
   */
  public boolean luminanceBasedCalibrate = false;

  /**
   * white based���ե�
   */
  public boolean whiteBasedCalibrate = true;

  /**
   * ��green�@smooth���ե�
   */
  public boolean smoothGreenCalibrate = true;

  /**
   * green based���ե�
   */
  public boolean greenBasedCalibrate = true;

  /**
   * white based���ե�2
   */
  public boolean whiteBased2Calibrate = true;

  /**
   * �G��based���ե�2 (New)
   */
  public boolean luminanceBased2Calibrate = false;

  /**
   * variable gamma smooth���^�k�_�I (new)
   */
  public int variableGammaSmoothStart = 1;

  /**
   * variable gamma smooth���^�k���I (new)
   */
  public int variableGammaSmoothEnd = 250;

  public static enum GreenBased {
    White, Model
  }

  public GreenBased smoothGreenBasedOn = GreenBased.Model;

  /**
   * ���@smooth���ɭԬO�_�n�@��J�ե�
   */
  public boolean smoothGreenCompromiseCalibrate = true;

  /**
   * ���@smooth���ɭ�, ����prime�ɩү�Ԩ���threshold�j�p
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
   * �G��based�ե���code���j
   */
  public int luminanceCalibratedInterval = 2;

  /**
   * white based�ե���code���j
   */
  public int whiteCalibratedInterval = 2;
  /**
   * green based�ե���code���j
   */
  public int greenCalibratedInterval = 1;
  /**
   * white based�ե�2��code���j
   */
  public int whiteCalibrated2Interval = 2;
  /**
   * �G��based�ե�2��code���j (New)
   */
  public int luminanceCalibrated2Interval = 2;

  /**
   * �O�_�n�i��q�ƱY��״_
   */
  public boolean quantizationCollapseFix = true;
  /**
   * �q�ƱY��ɪ��״_�O�_�Ҷq�i�״_��
   */
  public boolean concernCollapseFixable = true;

  /**
   * �b�C�G�׮ɱĥ�cube�ˬd (new)
   */
  public boolean cubeCheckAtLowLuminance = false;
  /**
   * �ĥ�cube�ե���code (new)
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
