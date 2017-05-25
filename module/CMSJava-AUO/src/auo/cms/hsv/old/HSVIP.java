package auo.cms.hsv.old;

import java.awt.image.*;

import org.math.plot.*;
import auo.cms.hsv.autotune.*;
import auo.cms.hsv.saturation.*;
import auo.cms.hsv.saturation.turnpoint.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.image.*;
import shu.cms.plot.*;
import shu.math.array.*;
import shu.plot.plots.*;
import java.awt.*;
import auo.cms.plot.PlotUtils;
import auo.cms.hsv.value.backup.ValuePrecisionEvaluator;

import auo.cms.hsv.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 * @deprecated
 */
public class HSVIP {
  private IntegerSaturationFormula integerSaturationFormula;
  private TuneParameter tuneParameter;
//  /**
//   *
//   * @param integerSaturationFormula IntegerSaturationFormula
//   * @param tuneTarget TuneTarget
//   * @deprecated
//   */
//  public HSVIP(IntegerSaturationFormula integerSaturationFormula,
//               TuneTarget tuneTarget) {
//    this.integerSaturationFormula = integerSaturationFormula;
//    TuneParameter tuneParameter = AutoTuner.getTestTuneParameter(tuneTarget,
//        integerSaturationFormula, null, null);
//    hsvLUTInterpolator = new HSVLUTInterpolator(
//        tuneParameter);
//    integerSaturationFormula.setInterpolateAdjust(true);
//  }

  public HSVIP(IntegerSaturationFormula integerSaturationFormula,
               TuneParameter tuneParameter) {
    this.integerSaturationFormula = integerSaturationFormula;
    this.tuneParameter = tuneParameter;
    hsvLUTInterpolator = new HSVLUTInterpolator(
        this.tuneParameter);
    integerSaturationFormula.setInterpolateAdjust(true);
  }

  private HSVLUTInterpolator hsvLUTInterpolator;
  public RGB getHSVRGB(RGB rgb) {
    HSV hsv = new HSV(rgb);
    HSV hsv2 = getHSV(hsv);
    return hsv2.toRGB();
  }

  private short shortSaturationAdjust;
  private short shortValueAdjust;

  public short getSaturationAdjust() {
    return shortSaturationAdjust;
  }

  public short getValueAdjust() {
    return shortValueAdjust;
  }

  public HSV getHSV(HSV hsv, SingleHueAdjustValue hsvAdjustValue) {
    return null;
  }

  private static HSV getNewHSV(HSV hsv, double[] hsvAdjust,
                              IntegerSaturationFormula formula) {
    double hueAdjust = hsvAdjust[0];
    double saturationAdjust = hsvAdjust[1];
    double valueAdjust = hsvAdjust[2];
    //====================================================================
    // HSVªº½Õ¾ã
    //====================================================================
    HSV hsv2 = (HSV) hsv.clone();
    hsv2.H += hueAdjust;
    hsv2.H = hsv2.H < 0 ? hsv2.H + 360 : hsv2.H;
    hsv2.S = formula.getSaturartion(hsv.S, saturationAdjust);
    hsv2.S = hsv2.S > 100 ? 100 : hsv2.S;
    hsv2.V = ValuePrecisionEvaluator.getV( (short) (hsv.V / 100. * 1023),
                                          (short) (hsv.getMinimum() /
        100. * 1023), (byte) valueAdjust);
    hsv2.V = hsv2.V / 1023. * 100;
    return hsv2;
  }

  public HSV getHSV(HSV hsv) {
    double hue = hsv.H;
    double huePrime = hsvLUTInterpolator.getQuantizationHueAdjustDouble(hue);
    huePrime = huePrime / 768 * 360 - hue;
    short saturationAdjust = hsvLUTInterpolator.getSaturationAdjust(hue); //11bit
    saturationAdjust = (short) (saturationAdjust >> 1); //11->10bit
    shortSaturationAdjust = saturationAdjust;
    double valueAdjust = hsvLUTInterpolator.getValueAdjust(hue); //15bit
    valueAdjust = valueAdjust / Math.pow(2, 15) * Math.pow(2, 6);
    shortValueAdjust = (short) (valueAdjust * Math.pow(2, 4));
    HSV hsv2 = Util.getNewHSV(hsv, new double[] {huePrime, saturationAdjust,
                              valueAdjust}, integerSaturationFormula);
    return hsv2;
  }

  public static void main(String[] args) {
    RGB.ColorSpace colorspace = RGB.ColorSpace.sRGB;
    short[] hueParameter = new short[24];
    for (int x = 0; x < 24; x++) {
      hueParameter[x] = (short) (x * 32);
    }

    byte saturationAdjustValue = 0;
    byte valueAdjustValue = 20;
    byte[] sParameter = new byte[24];
    byte[] vParameter = new byte[24];
    for (int x = 0; x < 24; x++) {
      sParameter[x] = saturationAdjustValue;
      vParameter[x] = valueAdjustValue;
    }
    TuneParameter parameter = new TuneParameter(hueParameter, sParameter,
                                                vParameter);
//    System.out.println(parameter);
    IntegerSaturationFormula integerSaturationFormula = new
        IntegerSaturationFormula( (byte) 4, 4);
    HSVIP ip = new HSVIP(integerSaturationFormula, parameter);
    CIEXYZ whiteXYZ = colorspace.getReferenceWhiteXYZ();

    for (int h = 0; h < 30; h += 30) {
      double[][] sArray = new double[11][11];
      double[][] vArray = new double[11][11];

      double[][] orgCArray = new double[11][11];
      double[][] orgLArray = new double[11][11];
      double[][] CArray = new double[11][11];
      double[][] LArray = new double[11][11];

      for (int s = 0; s <= 100; s += 10) {
        for (int v = 0; v <= 100; v += 10) {
          HSV hsv = new HSV(colorspace, new double[] {h, s, v});
          HSV hsv2 = ip.getHSV(hsv);
          hsv2.clip();

          int sIndex = s / 10;
          int vIndex = v / 10;
          sArray[sIndex][vIndex] = hsv2.S;
          vArray[sIndex][vIndex] = hsv2.V;

          CIELCh LCh2 = CIELCh.getInstanceFromLab(hsv2.toRGB().toXYZ(),
                                                  whiteXYZ);
          CArray[sIndex][vIndex] = LCh2.C;
          LArray[sIndex][vIndex] = LCh2.L;

          CIELCh LChOrg = CIELCh.getInstanceFromLab(hsv.toRGB().toXYZ(),
              whiteXYZ);
          orgCArray[sIndex][vIndex] = LChOrg.C;
          orgLArray[sIndex][vIndex] = LChOrg.L;

        }
      }

      Plot2D plot = Plot2D.getInstance(Integer.toString(h));

      plot.addGridPlot("", HSV.getLineColor(h), sArray, vArray);
      plot.setAxisLabels("S", "V");
      plot.setVisible();
      plot.setFixedBounds(0, 0, 100);
      plot.setFixedBounds(1, 0, 100);

      Plot2D plot2 = Plot2D.getInstance(Integer.toString(h));

//      plot2.addGridPlot("HSV'", HSV.getLineColor(h), CArray, LArray);

      GridPlot2D fromGrid = new GridPlot2D("", HSV.getLineColor(h), orgCArray,
                                           orgLArray);
      GridPlot2D toGrid = new GridPlot2D("", HSV.getLineColor(h), CArray,
                                         LArray);
      PlotUtils.addScatterPlotAndVectortoPlot(plot2, "", Color.red, fromGrid,
                                              toGrid);
//      double[][] data1 = grid1.getData();
//      double[][] data2 = grid2.getData();
//      for (int x = 0; x < data1.length; x++) {
//        double[] xy1 = data1[x];
//        double[] xy2 = data2[x];
//        int num = plot2.addScatterPlot("", Color.darkGray, xy2[0], xy2[1]);
//
//        double[] vec = DoubleArray.minus(xy1, xy2);
//        plot2.addVectortoPlot(num, new double[][] {vec});
//
////        plot2.setPlotVisible(num, false);
//      }
//      plot2.addVectortoPlot(num, grid.getData());
//      plot2.addGridPlot("Org", Color.gray, orgCArray, orgLArray);

      plot2.setAxisLabels("C", "L*");
      plot2.setVisible();
      plot2.setFixedBounds(0, 0, 130);
      plot2.setFixedBounds(1, 0, 100);
//      ( (Plot2DPanel) plot2.getPlotPanel()).removePlotToolBar();

    }

  }
  /*
    public static void IPCompare(String[] args) throws Exception {
      IntegerSaturationFormula integerSaturationFormula = new
          IntegerSaturationFormula( (byte) 7, 4);
      sRGBTuneTarget tuneTarget = new sRGBTuneTarget(sRGBTuneTarget.Patches.
          Integrated);

      //==========================================================================
      // Tune patameter setup
      //==========================================================================
      //auto
      TuneParameter tuneParameter = AutoTuner.getTestTuneParameter(tuneTarget,
          integerSaturationFormula, null, null);
      System.out.println(tuneParameter);
      //==========================================================================
      //manual
//    short[] hueAdjustValue = new short[] {
//        1, 31, 66, 94, 131, 157,
//        196, 220, 261, 283, 326, 346,
//        391, 409, 456, 472, 521, 535,
//        586, 598, 651, 661, 716, 724
//    };
//    byte[] saturationAdjustValue = new byte[24];
//    byte[] valueAdjustValue = new byte[24];
//    TuneParameter tuneParameter = new TuneParameter(hueAdjustValue,
//        saturationAdjustValue, valueAdjustValue);
      //==========================================================================

//    HSVIP hsvIP = new HSVIP(integerSaturationFormula, tuneTarget);
      HSVIP hsvIP = new HSVIP(integerSaturationFormula, tuneParameter);

      //==========================================================================
      // setting filename
      //==========================================================================
      String imageFilename = "hsv/pattern.bmp";
      //==========================================================================
      BufferedImage img = ImageUtils.loadImage(imageFilename);
      WritableRaster raster = img.getRaster();
      int width = img.getWidth();
      int height = img.getHeight();
      int[] pixel = new int[3];
      int index = 1;
      for (int y = 0; y < 1; y++) {
        for (int x = 0; x < width; x++) {

          if (x == 2) {
            System.out.println("");
          }
          raster.getPixel(x, y, pixel);

          RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, pixel);
          HSV auoHSV = HSV.AUO.fromRGB(rgb, true);
          HSV hsv2 = hsvIP.getHSV(auoHSV);
          short shortAngle = HSV.AUO.filterHueBit(auoHSV, 10);
          short newAngle = (short) (hsv2.H / 360. * 768 * 8);
          short saturationAdjust = hsvIP.getSaturationAdjust();
          short valueAdjust = hsvIP.getValueAdjust();

          int shortSaturation = (int) (hsv2.S / 100 * Math.pow(2, 10));
          int shortValue = (int) (hsv2.V / 100 * Math.pow(2, 10));
          RGB rgb2 = hsv2.toRGB();
          rgb2.changeMaxValue(RGB.MaxValue.Int10Bit);

          System.out.println( (index++) + "\t" + rgb.R + " " + rgb.G + " " +
                             rgb.B + " " + shortAngle + " " +
                             newAngle + " " + saturationAdjust + " " +
                             valueAdjust + " " + shortSaturation + " " +
                             shortValue + " " + rgb2.R + " " + rgb2.G + " " +
                             rgb2.B + "/" + hsv2);

        }
      }

//    HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {100, 50, 10});
//    RGB rgb1 = hsv.toRGB();
//    RGB rgb2 = hsvIP.getHSVRGB(rgb1);
//    HSV hsv2 = new HSV(rgb2);
//    System.out.println(hsv);
//    System.out.println(hsv2);
    }*/
}
