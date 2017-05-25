package shu.cms.measure.meter;

import shu.cms.measure.meterapi.argyll.*;
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
public class ArgyllSpotMeter
    extends Meter {
  private SpotreadAPI spotread;

  public ArgyllSpotMeter() {
    spotread = new SpotreadAPI();
  }

  /**
   * close
   *
   */
  public void close() {
    spotread.close();
  }

  /**
   * isConnected
   *
   * @return boolean
   */
  public boolean isConnected() {
    return spotread != null;
  }

  /**
   * calibrate
   *
   */
  public void calibrate() {
  }

  /**
   * getCalibrationDescription
   *
   * @return String
   */
  public String getCalibrationDescription() {
    return "Calibration needless.";
  }

  /**
   * setPatchIntensity
   *
   * @param patchIntensity PatchIntensity
   */
  public void setPatchIntensity(PatchIntensity patchIntensity) {
  }

  /**
   * triggerMeasurementInXYZ
   *
   * @return double[]
   */
  public double[] triggerMeasurementInXYZ() {
    return spotread.triggerMeasurement();
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
    return "";
  }

  /**
   * getCalibrationCount
   *
   * @return String
   */
  public String getCalibrationCount() {
    return "";
  }

  /**
   * setScreenType
   *
   * @param screenType ScreenType
   * @todo Implement this shu.cms.measure.Meter method
   */
  public void setScreenType(ScreenType screenType) {
  }

  /**
   * getType
   *
   * @return Instr
   */
  public Instr getType() {
    return Instr.Argyll;
  }

}
