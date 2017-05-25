package shu.cms.measure.meterapi.i1api;

import java.io.*;

import com.gretagmacbeth.eyeone.*;
import shu.cms.plot.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EyeOneAPI {
  public enum ScreenType {
    LCD, CRT;
  }

  public enum Reset {
    Reset, All, EyeOne, EyeOneDisplay, SingleEmission, SingleReflectance,
    SingleAmbientLight, ScanningReflectance,

  }

  public enum ColorSpace {
    CIELab("CIELab"),
    CIELCh("CIELCh"),
    CIELuv("CIELuv"),
    CIELChuv("CIELChuv"),
    CIE_UV_Y1960("CIEuvY1960"),
    CIE_UV_Y1976("CIEuvY1976"),
    CIEXYZ("CIEXYZ"),
    CIExyY("CIExyY"),
    HunterLab("HunterLab"),
    RXRYRZ("RxRyRz"),
    LAB_MG("LABmg"),
    LCH_MG("LCHmg"),
    RGB("RGB");

    ColorSpace(String value) {
      this.value = value;
    }

    protected String value;
  }

  public enum DeviceType {
    EyeOne, EyeOneDisplay;

  }

  public enum PatchIntensity {
    Bleak, Bright, Auto;
  }

  public enum MeasurementMode {
    SingleEmission,
    SingleReflectance,
    SingleAmbientLight,
    ScanningReflectance,
    ScanningAmbientLight,

  }

  public EyeOneAPI(ScreenType screenType) throws EyeOneException {
    i1 = com.gretagmacbeth.eyeone.EyeOne.getInstance();
    if (i1.isConnected()) {
      this.setScreenType(screenType);
    }
  }

  public boolean isConnected() throws EyeOneException {
    return i1.isConnected();
  }

  public void calibrate() throws EyeOneException {
    i1.calibrate();
  }

  public int getNumberOfAvailableSamples() throws EyeOneException {
    return i1.getNumberOfAvailableSamples();
  }

  public boolean isKeyPressed() throws EyeOneException {
    return i1.isKeyPressed();
  }

  public float[] triggerMeasurement() throws EyeOneException {
    i1.triggerMeasurement();
    return getTriStimulus(0);
  }

  public float[] triggerSpectrumMeasurement() throws EyeOneException {
    i1.triggerMeasurement();
    return getSpectrum(0);
  }

  public float[] getTriStimulus(long index) throws EyeOneException {
    return i1.getTriStimulus(index);
  }

  public float[] getSpectrum(long index) throws EyeOneException {
    return i1.getSpectrum(index);
  }

  protected void setOption(String key, String value) throws EyeOneException {
    i1.setOption(key, value);
  }

  protected String getOption(String key) {
    return i1.getOption(key);
  }

  public String getVersion() {
    return this.getOption(i1Const.VERSION);
  }

  public String getSerialNumber() {
    return this.getOption(i1Const.SERIAL_NUMBER);
  }

  public String getLastCalibration() {
    return this.getOption(i1Const.LAST_CALIBRATION_TIME);
  }

  public String getCalibrationCount() {
    return this.getOption(i1Const.CALIBRATION_COUNT);
  }

  public String getAvailableMeasurementModes() {
    return this.getOption(i1Const.AVAILABLE_MEASUREMENT_MODES);
  }

  public String getLastError() {
    return this.getOption(i1Const.LAST_ERROR);
  }

  public String getExtendedErrorInformation() {
    return this.getOption(i1Const.EXTENDED_ERROR_INFORMATION);
  }

  protected com.gretagmacbeth.eyeone.EyeOne i1;

  public static void main(String[] args) {

    try {
      EyeOneAPI i1 = new EyeOneAPI(ScreenType.LCD);
      Plot2D plot = Plot2D.getInstance();
      plot.setVisible(true);

      if (i1.isConnected()) {
        i1.setColorSpace(ColorSpace.CIEXYZ);
        i1.calibrate();
        System.out.println("enter to cali");
        System.in.read();
        System.out.println("cali over");
        System.out.println("enter to measure");
        System.in.read();
        long start = System.nanoTime();

        i1.setPatchIntensity(PatchIntensity.Bleak);
        for (int x = 0; ; x++) {
//          long start = System.nanoTime();
          float[] XYZ = i1.triggerMeasurement();
//          System.out.print(Arrays.toString(XYZ) + " ");
          double interval = (System.nanoTime() - start) / Math.pow(10, 6);
//          System.out.println(interval);
          plot.addScatterPlot(null, interval, XYZ[1]);
        }

      }
    }
    catch (EyeOneException ex) {
      ex.printStackTrace();
      System.exit(0);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  public void setColorSpace(ColorSpace colorSpace) throws EyeOneException {
    this.setOption(i1Const.COLOR_SPACE_KEY, colorSpace.value);
  }

  public void setMeasurementMode(MeasurementMode measurementMode) throws
      EyeOneException {
    this.setOption(i1Const.MEASUREMENT_MODE, measurementMode.name());
  }

  public void setDeviceType(DeviceType deviceType) throws EyeOneException {
    this.setOption(i1Const.DEVICE_TYPE, deviceType.name());
  }

  public void setScreenType(ScreenType screenType) throws EyeOneException {
    this.setOption(i1Const.SCREEN_TYPE, screenType.name());
  }

  public void setPatchIntensity(PatchIntensity patchIntensity) throws
      EyeOneException {
    this.setOption(i1Const.PATCH_INTENSITY, patchIntensity.name());
  }

  public void reset(Reset reset) throws EyeOneException {
    this.setOption(i1Const.RESET, reset.name());
  }

  public void setBeep(boolean enable) throws EyeOneException {
    this.setOption(i1Const.IS_BEEP_ENABLED, enable ? i1Const.Yes : i1Const.No);

  }

}

interface i1Const {

  String VERSION = "Version";
  String SERIAL_NUMBER = "SerialNumber";
  String IS_CONNECTED = "Connection";
  String IS_KEY_PRESSED = "IsKeyPressed";
  String IS_RECOGNITION_ENABLED = "Recognition";
  String LAST_CALIBRATION_TIME = "LastCalibrationTime";
  String CALIBRATION_COUNT = "LastCalibrationCounter";
  String LAST_ERROR = "LastError";
  String EXTENDED_ERROR_INFORMATION = "ExtendedErrorInformation";
  String NUMBER_OF_AVAILABLE_SAMPLES = "AvailableSamples";
  String AVAILABLE_MEASUREMENT_MODES = "AvailableMeasurementModes";
  String IS_BEEP_ENABLED = "Beep";
  String LAST_AUTO_DENSITY_FILTER = "LastAutoDensityFilter";
  String IS_ADAPTIVE_MODE_ENABLED = "AdaptiveMode";
  String COLOR_SPACE_KEY = "ColorSpaceDescription.Type";
  String MEASUREMENT_MODE = "MeasurementMode";

  String Yes = "yes";
  String No = "no";

  String DEVICE_TYPE = "DeviceType";
  String SCREEN_TYPE = "ScreenType";
  String PATCH_INTENSITY = "PatchIntensity"; /*used with i1-display*/
  String RESET = "Reset";
  /*reset command parameters: I1_ALL, DeviceTypes, MeasurementModes*/



}
