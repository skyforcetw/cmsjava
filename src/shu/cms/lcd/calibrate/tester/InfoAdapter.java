package shu.cms.lcd.calibrate.tester;

import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.cms.lcd.calibrate.measured.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.cms.lcd.calibrate.tester.CalibrateTester.*;
import shu.cms.lcd.material.*;
import shu.cms.measure.*;
import shu.math.*;
import shu.cms.lcd.calibrate.measured.util.MeasuredUtils;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 提供校正時所需訊息的adapter
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class InfoAdapter
    implements InfoInterface {

  private MeasureParameter mp;
  private ColorProofParameter cpp;
  private AdjustParameter ap;

  public InfoAdapter(ColorProofParameter cpp, MeasureParameter mp,
                     AdjustParameter ap) {
    this.cpp = cpp;
    this.mp = mp;
    this.ap = ap;
  }

  public InfoAdapter(ParameterProducer pp) {
    this(pp.getInitCPParameter(), pp.getMeasureParameter(),
         pp.getAdjustParameter());
  }

  public String getOutputDirname(CalibrateTester.TestTask task) {
    WhiteParameter wp = task.parameters.whiteParameter;
    ColorProofParameter p = task.parameters.colorProofParameter;
    String gammaDesc = ( (p.gamma == ColorProofParameter.Gamma.Custom) ?
                        Double.toString(p.customGamma) : p.gamma.name()) + "By" +
        p.gammaBy;
    LCDTarget lcdTarget = getOriginalRamp1021Target();

    return wp.getDescription() + "_" + p.cctCalibrate + "_" + p.turnCode + "_" +
        gammaDesc + (lcdTarget != null ? "@" + lcdTarget.getDevice() : "");
  }

  public String getModelCalibrateFilename() {
    return "lcdmodel-calibrate";
  }

  public String getMeasuredCalibrateFilename() {
    return "coordinate-calibrate";
  }

  public String getLuminanceBasedResultFilename() {
    return CalibratorConst.IndependLuminanceBased;
  }

  public String getWhiteBasedResultFilename() {
    return CalibratorConst.IndependWhiteBased;
  }

  public String getGreenBasedResultFilename() {
    return CalibratorConst.IndependGreenBased;
  }

  public String getWhiteBased2ResultFilename() {
    return CalibratorConst.IndependWhiteBased2;
  }

  public String getLuminanceBased2ResultFilename() {
    return CalibratorConst.IndependLuminanceBased2;
  }

  public String getGreenTargetLogoFilename() {
    return "[MEAS]-1-Relative-[IndependentCoordinateCalibrator].logo";
  }

  public String getSmoothGreenTargetLogoFilename() {
    return "[MEAS]-1-Relative-[JNDICalibrator].logo";
  }

  private MeterMeasurement mm;
  public MeterMeasurement getMeterMeasurement() {
    if (mm == null) {
      mm = new MeterMeasurement(getMeter(), false);
//      MeasureParameter mp = pp.getMeasureParameter();
      mm.setDoBlankInsert(mp.measureBlankInsert);
      mm.setBlankTimes(mp.measureBlankTime);
//      mm.setBlank(mp.blankColor);
//      mm.setBackground(mp.backgroundColor);
      mm.setBlankAndBackground(mp.blankColor, mp.backgroundColor);
      mm.setWaitTimes(mp.measureWaitTime);

      if (AutoCPOptions.get("DummyMeter")) {
        mm.setFakeMeasure(true);
      }
    }
    return mm;
  }

  private boolean[] calibrated = null;

  private boolean[] luminanceCalibrated = null;
  private boolean[] whiteCalibrated = null;
  private boolean[] greenCalibrated = null;
  private boolean[] whiteCalibrated2 = null;
  private boolean[] luminanceCalibrated2 = null;

  public boolean[] getLuminanceCalibrated() {
    if (luminanceCalibrated == null) {
      luminanceCalibrated = getCalibrated(ap.luminanceCalibratedInterval,
                                          ap.adjustStart, ap.adjustEnd);
    }
    return luminanceCalibrated;
  }

  public boolean[] getLuminanceCalibrated2() {
    if (luminanceCalibrated2 == null) {
      luminanceCalibrated2 = getCalibrated(ap.luminanceCalibrated2Interval,
                                           ap.adjustStart, ap.adjustEnd);
    }
    return luminanceCalibrated2;

  }

  public boolean[] getWhiteCalibrated() {
    if (whiteCalibrated == null) {
      whiteCalibrated = getCalibrated(ap.whiteCalibratedInterval,
                                      ap.adjustStart, ap.adjustEnd);
    }
    return whiteCalibrated;
  }

  public boolean[] getGreenCalibrated() {
    if (greenCalibrated == null) {
      greenCalibrated = getCalibrated(ap.greenCalibratedInterval,
                                      ap.adjustStart, ap.adjustEnd);
    }
    return greenCalibrated;
  }

  public boolean[] getWhiteCalibrated2() {
    if (whiteCalibrated2 == null) {
      whiteCalibrated2 = getCalibrated(ap.whiteCalibrated2Interval,
                                       ap.adjustStart, ap.adjustEnd);
    }
    return whiteCalibrated2;

  }

  /**
   * 產生 "已校正"/"不需校正" 的陣列
   * true: 免校
   * false: 要校正
   * @param interval int
   * @return boolean[]
   * @deprecated
   */
  private boolean[] getCalibrated(int interval) {
    boolean[] alreadyCalibrated = new boolean[256];
    if (interval == 1) {
      //通通要校, 除了頭跟尾
      alreadyCalibrated[0] = alreadyCalibrated[alreadyCalibrated.length - 1] = true;
    }
    else {
      //0, interval, intervalx2 ...免校
      //1, interval+1, intervalx2+1 ...要校
      for (int x = 1; x < 256; x += interval) {
        alreadyCalibrated[x] = true;
      }
      MeasuredUtils.inverse(alreadyCalibrated);
      alreadyCalibrated[0] = alreadyCalibrated[alreadyCalibrated.length - 1] = true;
    }
    return alreadyCalibrated;
  }

  private boolean[] getCalibrated(int interval, int start, int end) {
    int size = 256;
    boolean[] alreadyCalibrated = new boolean[size];
    if (interval != 1) {
      //0, interval, intervalx2 ...免校
      //1, interval+1, intervalx2+1 ...要校
      for (int x = 1; x < size; x += interval) {
        alreadyCalibrated[x] = true;
      }
      MeasuredUtils.inverse(alreadyCalibrated);
    }
    for (int x = 0; x < start; x++) {
      alreadyCalibrated[x] = true;
    }
    for (int x = end + 1; x < size; x++) {
      alreadyCalibrated[x] = true;
    }
    alreadyCalibrated[0] = alreadyCalibrated[alreadyCalibrated.length - 1] = true;
    return alreadyCalibrated;
  }

  /**
   *
   * @param rgbArray RGB[]
   * @return RGB[]
   * @deprecated
   */
  public RGB[] interpolateResult(RGB[] rgbArray) {
    if (calibrated == null) {
      return rgbArray;
    }
    else {
      RGB[] result = rgbArray;
      for (int x = 0; x < result.length; x++) {
        if (false == calibrated[x]) {
          int preIndex = 0, nextIndex = 0;
          for (preIndex = x; preIndex >= 0 && calibrated[preIndex] == true;
               preIndex--) {
          }
          for (nextIndex = x;
               nextIndex < result.length && calibrated[nextIndex] == true;
               nextIndex++) {
          }
          RGB pre = result[preIndex - 1];
          RGB next = result[nextIndex + 1];
          result[x].R = Interpolation.linear(0, 1, pre.R, next.R, 0.5);
          result[x].G = Interpolation.linear(0, 1, pre.G, next.G, 0.5);
          result[x].B = Interpolation.linear(0, 1, pre.B, next.B, 0.5);
          result[x].quantization(cpp.calibrateBits);
        }
      }
      return result;
    }
  }
}
