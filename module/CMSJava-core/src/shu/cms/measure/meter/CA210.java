package shu.cms.measure.meter;

import java.util.*;

import shu.cms.colorformat.logo.*;
import shu.cms.measure.meterapi.*;
import shu.cms.Spectra;

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
public class CA210
    extends Meter {
  protected static shu.cms.measure.meterapi.CA210API _CA210API = null;
  protected Date calibrateTime;
  protected int measureCount = 0;

  public CA210() {
    init();
  }

  protected void init() {
    if (_CA210API == null) {
      _CA210API = new shu.cms.measure.meterapi.CA210API();
    }
  }

  /**
   * isConnected
   *
   * @return boolean
   */
  public boolean isConnected() {
    return true;
  }

  /**
   * calibrate
   *
   */
  public void calibrate() {
    _CA210API.calibrate();
    calibrateTime = new Date();
    measureCount = 0;
  }

  public static void main(String[] args) {
    CA210 ca210 = new CA210();
    System.out.println(_CA210API.getAveragingMode());
    System.out.println(_CA210API.getCalStandard());
    _CA210API.setCalStandard(shu.cms.measure.meterapi.CA210API.CalStandard.
                             CT9300K);

  }

  /**
   * getCalibrationDescription
   *
   * @return String
   */
  public String getCalibrationDescription() {
//    return "請將Probe轉到0-CAL進行校正.";
    return "Set the pointing ring to the 0-CAL position.";
  }

  /**
   * setPatchIntensity
   *
   * @param patchIntensity PatchIntensity
   */
  public void setPatchIntensity(PatchIntensity patchIntensity) {
    switch (patchIntensity) {
      case Bleak:
        _CA210API.setAveragingMode(CA210API.AveragingMode.
                                   FAST);
        break;
      case Bright:
        _CA210API.setAveragingMode(CA210API.AveragingMode.
                                   SLOW);
        break;
      case Auto:
        _CA210API.setAveragingMode(CA210API.AveragingMode.
                                   AUTO);
        break;
    }
  }

  /**
   * triggerMeasurementInXYZ
   *
   * @return double[]
   */
  public double[] triggerMeasurementInXYZ() {
    //init();
    measureCount++;
    float[] result = _CA210API.triggerMeasurement();
    return new double[] {
        result[0], result[1], result[2]};
  }
  /**
   *
   * @return double[]
   * @deprecated
   */
  public double[] triggerMeasurementInSpectrum() {
    throw new UnsupportedOperationException();
  }

  public Spectra triggerMeasurementInSpectra() {
    throw new UnsupportedOperationException();
  }

  /**
   * getLastCalibration
   *
   * @return String
   */
  public String getLastCalibration() {
    return calibrateTime.toString();
  }

  /**
   * getCalibrationCount
   *
   * @return String
   */
  public String getCalibrationCount() {
    return String.valueOf(measureCount);
  }

  public static enum CalStandard {
    CT6500K, CT9300K;
  }

  public void setCalStandard(CalStandard calStandard) {
    _CA210API.setCalStandard(CA210API.CalStandard.valueOf(
        calStandard.name()));

  }

  public static enum RemoteMode {
    OFF, ON, LOCKED;
  }

  /**
   *
   * @param mode RemoteMode
   */
  public void setRemoteMode(RemoteMode mode) {
    _CA210API.setRemoteMode(CA210API.RemoteMode.valueOf(mode.
        name()));
  }

  public void setSyncMode(float frequency) {
    _CA210API.setSyncMode(frequency);
  }

  public static enum SyncMode {
    NTSC, PAL, EXT, UNIV;
  }

  public void setSyncMode(SyncMode mode) {
    _CA210API.setSyncMode(CA210API.SyncMode.valueOf(mode.
        name()));
  }

  /**
   * setScreenType
   *
   * @param screenType ScreenType
   */
  public void setScreenType(ScreenType screenType) {
  }

  /**
   * getType
   *
   * @return Type
   */
  public Instr getType() {
    return Instr.CA210;
  }

  /**
   *
   * @param logo LogoFile
   */
  public void setLogoFileHeader(LogoFile logo) {
    logo.setHeader(LogoFile.Reserved.Created, new Date().toString());
    logo.setHeader(LogoFile.Reserved.Instrumentation, getType().name());

    //連線過久沒有作動可能會跟CA-210斷線, 造成再呼叫CA-210的時候會造成com錯誤
    //所以乾脆取消跟CA-210要校正標準的動作
//    logo.setHeader(LogoFile.Reserved.MeasurementSource,
//                   "Illumination=Unknown	ObserverAngle=Unknown	WhiteBase=" +
//                   _CA210API.getCalStandard().name() + "	Filter=Unknown");
    logo.setHeader(LogoFile.Reserved.MeasurementSource,
                   "Illumination=Unknown	ObserverAngle=Unknown	WhiteBase=Abs	Filter=Unknown");
    logo.setNumberOfFields(8);
    logo.addKeyword("SampleID");
    logo.addKeyword("SAMPLE_NAME");
    logo.setDataFormat(
        "SampleID	SAMPLE_NAME	RGB_R	RGB_G	RGB_B	XYZ_X	XYZ_Y	XYZ_Z");

  }

  public void close() {
    _CA210API.close();
    _CA210API = null;
    System.gc();
  }
}
