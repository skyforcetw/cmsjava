package shu.cms.measure.meter;

import com.gretagmacbeth.eyeone.*;
import shu.cms.measure.meterapi.i1api.EyeOneAPI;
import shu.util.log.*;
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
public class EyeOneDisplay2
    extends Meter {

  protected final static EyeOneAPI.ScreenType getEyeOneScreenType(Meter.
      ScreenType
      screenType) {
    switch (screenType) {
      case LCD:
        return EyeOneAPI.ScreenType.LCD;
      case CRT:
        return EyeOneAPI.ScreenType.CRT;
      default:
        return null;
    }
  }

  public EyeOneDisplay2(Meter.ScreenType screenType) {
    try {
      if (_EYE_ONE == null) {
        _EYE_ONE = new EyeOneAPI(getEyeOneScreenType(screenType));
      }

      if (_EYE_ONE.isConnected()) {
        _EYE_ONE.setColorSpace(EyeOneAPI.ColorSpace.CIEXYZ);
        _EYE_ONE.setPatchIntensity(EyeOneAPI.PatchIntensity.Auto);
      }

    }
    catch (EyeOneException ex) {
      Logger.log.error("", ex);
    }
  }

  protected static EyeOneAPI _EYE_ONE;

  /**
   * isConnected
   *
   * @return boolean
   */
  public boolean isConnected() {
    try {
      return _EYE_ONE.isConnected();
    }
    catch (EyeOneException ex) {
      Logger.log.error("", ex);
      return false;
    }
  }

  /**
   * calibrate
   *
   */
  public void calibrate() {
    try {
      _EYE_ONE.calibrate(); ;
    }
    catch (EyeOneException ex) {
      Logger.log.error("", ex);
    }
  }

  public String getCalibrationDescription() {
//    return "請將i1 Display2放在平坦不反光的表面上進行校正.";
    return "Place i1 Display2 on plain surface";
  }

  /**
   * setPatchIntensity
   *
   * @param patchIntensity PatchIntensity
   */
  public void setPatchIntensity(PatchIntensity patchIntensity) {
    try {
      _EYE_ONE.setPatchIntensity(getEyeOnePatchIntensity(patchIntensity));
    }
    catch (EyeOneException ex) {
      Logger.log.error("", ex);
    }
  }

  protected final static EyeOneAPI.PatchIntensity getEyeOnePatchIntensity(Meter.
      PatchIntensity
      patchIntensity) {
    switch (patchIntensity) {
      case Bleak:
        return EyeOneAPI.PatchIntensity.Bleak;
      case Bright:
        return EyeOneAPI.PatchIntensity.Bright;
      case Auto:
        return EyeOneAPI.PatchIntensity.Auto;
      default:
        return null;
    }
  }

  /**
   * triggerMeasurement
   *
   * @return double[]
   */
  public double[] triggerMeasurementInXYZ() {
    try {
      float[] measure = _EYE_ONE.triggerMeasurement();
      double[] result = new double[] {
          measure[0], measure[1], measure[2]};
      return result;
    }
    catch (EyeOneException ex) {
      Logger.log.error("", ex);
      return null;
    }
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
    return _EYE_ONE.getLastCalibration();
  }

  /**
   * getCalibrationCount
   *
   * @return String
   */
  public String getCalibrationCount() {
    return _EYE_ONE.getCalibrationCount();
  }

  /**
   * setScreenType
   *
   * @param screenType ScreenType
   */
  public void setScreenType(ScreenType screenType) {
    try {
      _EYE_ONE.setScreenType(getEyeOneScreenType(screenType));
    }
    catch (EyeOneException ex) {
      Logger.log.error("", ex);
    }
  }

  public Instr getType() {
    return Instr.i1Display2;
  }

  public void close() {

  }
}
