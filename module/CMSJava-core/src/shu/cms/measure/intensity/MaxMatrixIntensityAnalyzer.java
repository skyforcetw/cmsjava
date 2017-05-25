package shu.cms.measure.intensity;

import java.awt.*;
import java.io.*;
import java.util.List;

import shu.cms.*;
import shu.cms.colorformat.adapter.xls.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.depend.RGBBase.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.measure.*;
import shu.math.array.*;
import shu.plot.*;

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
public class MaxMatrixIntensityAnalyzer
    implements IntensityAnalyzerIF {
  public MaxMatrixIntensityAnalyzer(MeterMeasurement mm) {
    this.mm = mm;
    calculateOnly = false;
  }

  public MaxMatrixIntensityAnalyzer() {
    calculateOnly = true;
  }

  private boolean calculateOnly = false;
  private MeterMeasurement mm;
  private CIEXYZ XYZ;
  private CIEXYZ rXYZ, gXYZ, bXYZ, wXYZ;
  private double[][] inverseMatrix;
  private double[] targetRatio;
  private double[] rgbValues;

  /**
   * getIntensity
   *
   * @param rgb RGB
   * @return RGB
   */
  public RGB getIntensity(RGB rgb) {
    if (calculateOnly) {
      throw new IllegalStateException("");
    }
    Patch p = mm.measure(rgb, rgb.toString());
    XYZ = p.getXYZ();

    return getIntensity(XYZ);
  }

  public RGB getIntensity(CIEXYZ XYZ) {
    rgbValues = DoubleArray.times(inverseMatrix, XYZ.getValues());
//        DoubleArray.times(rgbValues, 100);
    DoubleArray.timesAndNoReturn(rgbValues, 100);
    double[] intensityValues = new double[3];
    intensityValues[0] = rgbValues[0] / targetRatio[0];
    intensityValues[1] = rgbValues[1] / targetRatio[1];
    intensityValues[2] = rgbValues[2] / targetRatio[2];
    RGB intensity = new RGB(RGB.ColorSpace.unknowRGB, intensityValues);
    return intensity;
  };

  /**
   * getCIEXYZ
   *
   * @return CIEXYZ
   */
  public CIEXYZ getCIEXYZ() {
    return XYZ;
  }

  /**
   * setupComponent
   *
   * @param ch Channel
   * @param rgb RGB
   */
  public void setupComponent(Channel ch, RGB rgb) {
    if (calculateOnly) {
      throw new IllegalStateException("");
    }

    Patch p = mm.measure(rgb, rgb.toString());
    CIEXYZ measureXYZ = p.getXYZ();
    setupComponent(ch, measureXYZ);
  }

  public void setupComponent(Channel ch,
                             CIEXYZ measureXYZ) {
    switch (ch) {
      case R:
        rXYZ = measureXYZ;
        break;
      case G:
        gXYZ = measureXYZ;
        break;
      case B:
        bXYZ = measureXYZ;
        break;
      case W:
        wXYZ = measureXYZ;
        break;
    }
    ;
  };
  /**
   * enter
   *
   */
  public void enter() {

    if (!calculateOnly) {
      mm.setMeasureWindowsVisible(false);
    }
    if (rXYZ == null || gXYZ == null || bXYZ == null || wXYZ == null) {
      throw new IllegalStateException(
          "Excute setupComponent() with RGBW first.");
    }

    double[][] m = new double[][] {
        rXYZ.getValues(), gXYZ.getValues(), bXYZ.getValues()};
    m = DoubleArray.transpose(m);
    inverseMatrix = DoubleArray.inverse(m);
    targetRatio = DoubleArray.times(inverseMatrix, wXYZ.getValues());
  }

  /**
   * beginAnalyze
   *
   */
  public void beginAnalyze() {
    if (calculateOnly) {
      throw new IllegalStateException("");
    }

    mm.setMeasureWindowsVisible(true);
  }

  /**
   * endAnalyze
   *
   */
  public void endAnalyze() {
    if (calculateOnly) {
      throw new IllegalStateException("");
    }

    mm.setMeasureWindowsVisible(false);
  }

  /**
   * getReferenceColor
   *
   * @return CIEXYZ
   */
  public CIEXYZ getReferenceColor() {
    return XYZ;
  }

  public static void main(String[] args) {
    AUORampXLSAdapter xls = null;
    try {

      xls = new AUORampXLSAdapter("Measurement09.xls");

    }
    catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    LCDTarget target = LCDTarget.Instance.get(xls);
    MaxMatrixIntensityAnalyzer analyzer = new MaxMatrixIntensityAnalyzer();

    CIEXYZ rXYZ = target.getSaturatedChannelPatch(RGB.Channel.R).getXYZ();
    CIEXYZ gXYZ = target.getSaturatedChannelPatch(RGB.Channel.G).getXYZ();
    CIEXYZ bXYZ = target.getSaturatedChannelPatch(RGB.Channel.B).getXYZ();
    CIEXYZ wXYZ = target.getWhitePatch().getXYZ();
    analyzer.setupComponent(RGB.Channel.R, rXYZ);
    analyzer.setupComponent(RGB.Channel.G, gXYZ);
    analyzer.setupComponent(RGB.Channel.B, bXYZ);
    analyzer.setupComponent(RGB.Channel.W, wXYZ);
    analyzer.enter();

    List<Patch> rpatchList = target.filter.oneValueChannel(RGB.Channel.R);
    List<Patch> gpatchList = target.filter.oneValueChannel(RGB.Channel.G);
    List<Patch> bpatchList = target.filter.oneValueChannel(RGB.Channel.B);

    Plot2D plot = Plot2D.getInstance();
    Plot2D ploterr = Plot2D.getInstance();
    plotting(plot, ploterr, rpatchList, RGB.Channel.R, analyzer);
    plotting(plot, ploterr, gpatchList, RGB.Channel.G, analyzer);
    plotting(plot, ploterr, bpatchList, RGB.Channel.B, analyzer);

    List<Patch> patchList = target.filter.grayPatch();
    int index = 1;
    for (Patch p : patchList) {
      CIEXYZ XYZ = p.getXYZ();
      RGB intensity = analyzer.getIntensity(XYZ);
      intensity.changeMaxValue(RGB.MaxValue.Double100);
//            System.out.println(p.getRGB() + " " + intensity);
      plot.addCacheScatterLinePlot("W-R", Color.black, index, intensity.R);
      plot.addCacheScatterLinePlot("W-G", Color.black, index, intensity.G);
      plot.addCacheScatterLinePlot("W-B", Color.black, index, intensity.B);
      index++;
    }
    plot.setVisible();
    ploterr.setVisible();
  }

  static void plotting(Plot2D plot, Plot2D ploterr, List<Patch> patchList,
      RGB.Channel ch,
      MaxMatrixIntensityAnalyzer analyzer) {
    int index = 1;
    for (Patch p : patchList) {
      CIEXYZ XYZ = p.getXYZ();
      RGB intensity = analyzer.getIntensity(XYZ);
      intensity.changeMaxValue(RGB.MaxValue.Double100);
//            plot.addCacheLinePlot(ch.name(), ch.color, index++,
//                                  intensity.getValue(ch));

      RGB.Channel[] channels = RGB.Channel.getBesidePrimaryChannel(ch);
      double err = Math.abs(intensity.getValue(channels[0])) +
          Math.abs(intensity.getValue(channels[1]));
      System.out.println(p.getRGB() + " " + intensity + " " + err);
      plot.addCacheScatterLinePlot(ch.name(), ch.color, index,
                                   intensity.getValue(ch));
      ploterr.addCacheScatterLinePlot(ch.name(), ch.color, index++, err);
    }

  }

  public void setTargetRatio(double[] targetRatio) {
    this.targetRatio = targetRatio;
  }

  public static MaxMatrixIntensityAnalyzer getReadyAnalyzer(CIEXYZ rXYZ,
      CIEXYZ gXYZ, CIEXYZ bXYZ, CIEXYZ wXYZ) {
    MaxMatrixIntensityAnalyzer ma = new MaxMatrixIntensityAnalyzer();
    ma.setupComponent(RGB.Channel.R, rXYZ);
    ma.setupComponent(RGB.Channel.G, gXYZ);
    ma.setupComponent(RGB.Channel.B, bXYZ);
    ma.setupComponent(RGB.Channel.W, wXYZ);
    ma.enter();

    return ma;
  }

  public static MaxMatrixIntensityAnalyzer getReadyAnalyzer(CIExyY rxyY,
      CIExyY gxyY, CIExyY bxyY, CIExyY wxyY) {
    MaxMatrixIntensityAnalyzer ma = new MaxMatrixIntensityAnalyzer();
    ma.setupComponent(RGB.Channel.R, rxyY.toXYZ());
    ma.setupComponent(RGB.Channel.G, gxyY.toXYZ());
    ma.setupComponent(RGB.Channel.B, bxyY.toXYZ());
    ma.setupComponent(RGB.Channel.W, wxyY.toXYZ());
    ma.enter();

    return ma;
  }

  public static MaxMatrixIntensityAnalyzer getReadyAnalyzer(
      IntensityAnalyzerIF originalAnalyzer, CIEXYZ wXYZ) {
    CIEXYZ rXYZ = originalAnalyzer.getPrimaryColor(RGB.Channel.R);
    CIEXYZ gXYZ = originalAnalyzer.getPrimaryColor(RGB.Channel.G);
    CIEXYZ bXYZ = originalAnalyzer.getPrimaryColor(RGB.Channel.B);
    return getReadyAnalyzer(rXYZ, gXYZ, bXYZ, wXYZ);
  }

  public CIEXYZ getPrimaryColor(final RGB.Channel ch) {
    switch (ch) {
      case R:
        return rXYZ;
      case G:
        return gXYZ;
      case B:
        return bXYZ;
      default:
        throw new IllegalArgumentException();
    }
  }

  public double[] getTargetRatio() {
    return targetRatio;
  }
}
