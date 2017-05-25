package shu.cms.measure.meter;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
//import vv.cms.measure.cp.*;
import shu.cms.Spectra;
import shu.cms.Illuminant;
import shu.cms.plot.Plot2D;
//import shu.plot.*;

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
public class DummyMeter
    extends Meter {

  public static enum Mode {
    sRGB, ColorSpace, LCDModel
  }

  public DummyMeter() {
    rgbColorSpace = RGB.ColorSpace.sRGB;
    mode = Mode.sRGB;
  }

  public DummyMeter(RGB.ColorSpace colorSpace, double luminance, CIEXYZ flare) {
    this.rgbColorSpace = colorSpace;
    CIEXYZ rgbColorSpaceWhite = rgbColorSpace.getReferenceWhiteXYZ();
    this.luminanceFactor = (luminance - flare.Y) / rgbColorSpaceWhite.Y;
    this.flare = flare;
    mode = Mode.ColorSpace;
  }

//  private static CPCodeLoader.DummyAdapter dummyAdapter = new CPCodeLoader.
//      DummyAdapter();

  public DummyMeter(LCDModel lcdModel) {
    this.lcdModel = lcdModel;
    mode = Mode.LCDModel;
//    dummyAdapter.setDummyLCDModel(lcdModel);
//    dummyAdapter.setDummyLoading(true);
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
  }

  /**
   * getCalibrationDescription
   *
   * @return String
   */
  public String getCalibrationDescription() {
    return "dummy calibration";
  }

  /**
   * setPatchIntensity
   *
   * @param patchIntensity PatchIntensity
   */
  public void setPatchIntensity(PatchIntensity patchIntensity) {
  }

  public void setRGB(RGB rgb) {
    this.rgb = rgb;
  }

  private CIEXYZ flare;
  private double luminanceFactor;
  private Mode mode;
  private LCDModel lcdModel = null;
  private RGB rgb;
  private RGB.ColorSpace rgbColorSpace;

  public LCDModel getLCDModel() {
    return lcdModel;
  }

  /**
   * triggerMeasurementInXYZ
   *
   * @return double[]
   */
  public double[] triggerMeasurementInXYZ() {
    if (rgb == null) {
      return new double[] {
          Math.random() * 100., Math.random() * 100., Math.random() * 100.};
    }
    switch (mode) {
      case sRGB:
        return RGB.toXYZ(rgb, rgbColorSpace).getValues();
      case LCDModel:
        RGB measureRGB = (RGB) rgb.clone();
        lcdModel.changeMaxValue(measureRGB);
        return lcdModel.getXYZ(measureRGB, false).getValues();
      case ColorSpace:
        CIEXYZ XYZ = RGB.toXYZ(rgb, rgbColorSpace);
        XYZ.times(luminanceFactor);
        XYZ = CIEXYZ.plus(XYZ, flare);
        return XYZ.getValues();
      default:
        return new double[] {
            Math.random() * 100., Math.random() * 100., Math.random() * 100.};
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
    double[] XYZValues = triggerMeasurementInXYZ();
    Illuminant illuminant = Illuminant.getCIEIlluminant(new CIEXYZ(XYZValues));
    return illuminant.getSpectra();
  }

  /**
   * getLastCalibration
   *
   * @return String
   */
  public String getLastCalibration() {
    return "dummy calibration";
  }

  /**
   * getCalibrationCount
   *
   * @return String
   */
  public String getCalibrationCount() {
    return "dummy calibration";
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
   * @return Instr
   */
  public Instr getType() {
    return Instr.Dummy;
  }

  public int getSuggestedWaitTimes() {
    return 0;
  }

  public void close() {

  }

  public static void main(String[] args) {
    DummyMeter meter = new DummyMeter(RGB.ColorSpace.sRGB, 1,
                                      new CIEXYZ(0.0, 0.0, 0.0));
    meter.setRGB(new RGB(RGB.ColorSpace.unknowRGB, new int[] {255, 240, 200}));
    System.out.println(Arrays.toString(meter.triggerMeasurementInXYZ()));
    Spectra s = meter.triggerMeasurementInSpectra();
    Plot2D p = Plot2D.getInstance();
    p.addSpectra("", s);
    p.setVisible();
  }
}
