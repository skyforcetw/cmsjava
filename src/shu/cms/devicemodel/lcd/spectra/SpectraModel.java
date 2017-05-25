package shu.cms.devicemodel.lcd.spectra;

import java.io.*;
import java.util.*;

import jxl.read.biff.*;
import shu.cms.*;
import shu.cms.colorformat.file.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;
import shu.cms.devicemodel.lcd.*;

//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SpectraModel
    extends LCDModelBase {
  public SpectraModel(LCTransmission transmission, Spectra[] rgbColorFilter,
                      Spectra backlight, double digitCount2DeltaNGamma,
                      double fixFactor) {
    this.transmission = transmission;
    this.rgbColorFilter = rgbColorFilter;
//    this.backlight = backlight;
    this.backlight = backlight.fillAndInterpolate(380, 780, 5);
    this.maxDeltan = transmission.getMaxDeltaN(550);
    this.digitCount2DeltaNGamma = digitCount2DeltaNGamma;
    this.fixFactor = fixFactor;
  }

  private double maxDeltan;
  private LCTransmission transmission;
  private Spectra[] rgbColorFilter;
  private Spectra backlight;
  private double digitCount2DeltaNGamma;
  private double fixFactor;

  public void setDigitCount2DeltaNGamma(double gamma) {
    digitCount2DeltaNGamma = gamma;
  }

  private double getDeltaN(double normalizeDigitCount) {
    double value = Math.pow(normalizeDigitCount, digitCount2DeltaNGamma);
    return (maxDeltan - transmission.dn0) * value + transmission.dn0;
  }

  private Spectra getSpectraTransmission(double normalizeDigitCount) {
    double dn = getDeltaN(normalizeDigitCount);
    return transmission.getTransmission(380, 780, 5, dn);
  }

  private Spectra getSpectra(double normalizeDigitCount) {
    Spectra transmission = getSpectraTransmission(normalizeDigitCount);
    transmission.times(backlight);
    return transmission;
  }

  /**
   * getDescription
   *
   * @return String
   * @todo Implement this shu.cms.devicemodel.DeviceCharacterizationModel
   *   method
   */
  public String getDescription() {
    return "";
  }

  /**
   * getRGB
   *
   * @param XYZ CIEXYZ
   * @param relativeXYZ boolean
   * @return RGB
   */
  public RGB getRGB(CIEXYZ XYZ, boolean relativeXYZ) {
    throw new UnsupportedOperationException();
  }

  /**
   * getXYZ
   *
   * @param rgb RGB
   * @param relativeXYZ boolean
   * @return CIEXYZ
   * @todo Implement this shu.cms.devicemodel.DeviceCharacterizationModel
   *   method
   */
  public CIEXYZ getXYZ(RGB rgb, boolean relativeXYZ) {
    double[] normalizeDigitCount = rgb.getValues(new double[3],
                                                 RGB.MaxValue.Double1);
    Spectra rSpectra = getSpectra(normalizeDigitCount[0]);
    Spectra gSpectra = getSpectra(normalizeDigitCount[1]);
    Spectra bSpectra = getSpectra(normalizeDigitCount[2]);
    rSpectra.times(rgbColorFilter[0]);
    gSpectra.times(rgbColorFilter[1]);
    bSpectra.times(rgbColorFilter[2]);
    CIEXYZ rXYZ = rSpectra.getXYZ();
    CIEXYZ gXYZ = gSpectra.getXYZ();
    CIEXYZ bXYZ = bSpectra.getXYZ();
    CIEXYZ XYZ = CIEXYZ.plus(rXYZ, gXYZ);
    XYZ = CIEXYZ.plus(XYZ, bXYZ);
    XYZ.times(fixFactor);
    return XYZ;
  }

  /**
   * 將RGBpatchList的色塊,經由前導模式計算出XYZ,回傳成List<Patch>
   *
   * @param RGBpatchList List
   * @return List
   * @todo Implement this shu.cms.devicemodel.DeviceCharacterizationModel
   *   method
   */
  public List produceForwardModelPatchList(List RGBpatchList) {
    return null;
  }

  /**
   * 將XYZpatchList的色塊,經由反推模式計算出RGB,回傳成List<Patch>
   *
   * @param XYZpatchList List
   * @return List
   */
  public List produceReverseModelPatchList(List XYZpatchList) {
    throw new UnsupportedOperationException();
  }

  public static Spectra[] getRGBColorFilter() {
    return getRGBColorFilter("Reference Files/ColorFilter.xls");
  }

  public static Spectra[] getRGBColorFilter(String filename) {
    Spectra[] rgbColorFilter = new Spectra[3];
    try {
//      ExcelFile cf = new ExcelFile("Reference Files/ColorFilter.xls");
      ExcelFile cf = new ExcelFile(filename);
      int rows = cf.getRows();
      double[][] spectraDatas = new double[3][81];
      for (int x = 1; x < rows; x++) {
        spectraDatas[0][x - 1] = cf.getCell(1, x);
        spectraDatas[1][x - 1] = cf.getCell(2, x);
        spectraDatas[2][x - 1] = cf.getCell(3, x);
      }
      rgbColorFilter[0] = new Spectra("R", Spectra.SpectrumType.TRANSMISSION,
                                      380, 780, 5, spectraDatas[0]);
      rgbColorFilter[1] = new Spectra("G", Spectra.SpectrumType.TRANSMISSION,
                                      380, 780, 5, spectraDatas[1]);
      rgbColorFilter[2] = new Spectra("B", Spectra.SpectrumType.TRANSMISSION,
                                      380, 780, 5, spectraDatas[2]);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
    return rgbColorFilter;
  }

  public static void main(String[] args) {
    Plot2D plot = Plot2D.getInstance();
    /*Spectra[] rgbColorFilter = getRGBColorFilter();
         plot.addSpectra("r", rgbColorFilter[0], Color.red);
         plot.addSpectra("g", rgbColorFilter[1], Color.green);
         plot.addSpectra("b", rgbColorFilter[2], Color.blue);
         plot.setFixedBounds(0, 380, 780);
     */

//    double start = 0.001;
//    double end = 2;
//    LCTransmission t = new LCTransmission(.3, true, start, end, 0.01);

//    System.out.println(t.getMaxDeltaN(550));
//    double[] data = t.getTransmission(550, start, end, 0.01);
//    plot.addLinePlot("", start, end, data);

    //Spectra backlight = getWhiteLED();
    //Spectra backlight = Illuminant.F8.getSpectra();
    Spectra backlight = Illuminant.F11.getSpectra();

    Spectra[] rgbColorFilter = getRGBColorFilter();
    LCTransmission lct = new LCTransmission(.3, true, 0.01, 2, 0.01);
    SpectraModel model = new SpectraModel(lct, rgbColorFilter, backlight, 1, 21);
//    CIEXYZ wXYZ = model.getXYZ(new RGB(255, 255, 255), false);
//    CIEXYZ kXYZ = model.getXYZ(new RGB(0, 0, 0), false);
//    System.out.println(wXYZ.getCCT());
//    System.out.println(kXYZ.getCCT());
    for (RGB.Channel ch : RGB.Channel.RGBChannel) {
      for (int x = 0; x < 255; x++) {
        RGB rgb = new RGB();
        rgb.setValue(ch, x, RGB.MaxValue.Double255);
        //CIEXYZ XYZ = model.getXYZ(new RGB(x, x, x), false);
        CIEXYZ XYZ = model.getXYZ(rgb, false);
        //      plot.addCacheScatterLinePlot("", x, XYZ.Y);
        CIExyY xyY = new CIExyY(XYZ);
        //double CCT = xyY.getCCT();
        //plot.addCacheScatterLinePlot("", x, CCT);
        plot.addCacheScatterPlot(ch.name(), ch.color, xyY.x, xyY.y);
      }
    }
//    System.out.println(led.getXYZ().getCCT());
//    plot.addSpectra("", led);
//    plot.addSpectra("F8", Illuminant.F8.getSpectra());
//    plot.addSpectra("F11", Illuminant.F11.getSpectra());
//    plot.addLegend();

    plot.setVisible();
  }

  static Spectra getWhiteLED() {
    Spectra led = (Spectra) ColorMatchingFunction.CIE_1931_2DEG_XYZ.getSpectra(
        2).clone();
    led = led.reduce(360, 830, 2);
    led = new Spectra("", Spectra.SpectrumType.EMISSION, 360, 595, 1,
                      led.getData());
    led = led.fillAndInterpolate(360, 830, 1);

    Spectra g = (Spectra) ColorMatchingFunction.CIE_1931_2DEG_XYZ.getSpectra(1).
        clone();
    g = g.fillAndInterpolate(420, 830, 1);
    g = new Spectra("", Spectra.SpectrumType.EMISSION, 400, 810, 1, g.getData());
    g = g.fillAndInterpolate(360, 830, 1);
    g.times(0.8);

    led.plus(g);
    led.normalizeDataToMax();
    return led;

  }
}
