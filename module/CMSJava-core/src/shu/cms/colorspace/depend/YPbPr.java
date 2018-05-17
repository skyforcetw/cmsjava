package shu.cms.colorspace.depend;

import java.awt.*;

import shu.cms.colorspace.depend.RGBBase.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;
import shu.math.array.*;

//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * from 阿彭
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class YPbPr
    extends OpponentColorBase {
  public YPbPr(RGB rgb) {
    super(rgb, lrgybIndex);
  }

  public YPbPr(ColorSpace colorSpace) {
    super(colorSpace, lrgybIndex);
  }

  public YPbPr(ColorSpace colorSpace, double[] values) {
    super(colorSpace, values, lrgybIndex);
  }

  private final static int[] lrgybIndex = new int[] {
      0, 2, 1};

  /**
   * _setValues
   *
   * @param values double[]
   */
  protected void _setValues(double[] values) {
    Y = values[0];
    Pb = values[1];
    Pr = values[2];
  }

  /**
   * getValues
   *
   * @param values double[]
   * @return double[]
   */
  protected double[] _getValues(double[] values) {
    values[0] = Y;
    values[1] = Pb;
    values[2] = Pr;
    return values;
  }

  /**
   * toRGB
   *
   * @return RGB
   */
  public RGB toRGB() {
    return toRGB(this);
  }

  public static RGB toRGB(YPbPr ypp) {
    double[] yppValues = DoubleArray.times(invm, ypp.getValues());

    RGB rgb = new RGB(ypp.rgbColorSpace, yppValues);
    return rgb;
  }

  protected final static double[][] m = DoubleArray.times(new double[][] { {
      218, 732, 74}, {
      -117, -395, 512}, {
      512, -465, -47}
  }, 1. / 1024);

  protected final static double[][] invm = DoubleArray.times(new double[][] { {
      1024, 0, 1612}, {
      1024, -192, -480}, {
      1024, 1900, -2}
  }, 1. / 1024);

  protected double[] _fromRGB(RGB rgb) {
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
    double[] yppValues = DoubleArray.times(m, rgbValues);
    return yppValues;
  }

  /*public final static YPbPr fromRGB(RGB rgb) {
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
    double[] yppValues = DoubleArray.times(m, rgbValues);
    YPbPr ypp = new YPbPr(rgb.getRGBColorSpace(), yppValues);
    return ypp;
     }*/

  public YPbPr(ColorAppearanceBase colorAppearanceBase) {
    super(colorAppearanceBase);
  }

  public static void main(String[] args) {
    boolean showIllegal = false;

    for (RGBBase.Channel ch : RGBBase.Channel.RGBYMCChannel) {
      RGB rgb = new RGB(RGB.ColorSpace.sRGB);
      rgb.setValue(ch, 1, RGB.MaxValue.Double1);

      YPbPr ypp = new YPbPr(rgb);
      ColorAppearanceAttribute base = new ColorAppearanceAttribute(ypp);
      ColorAppearanceAttribute oca = (ColorAppearanceAttribute) base.clone();
      double saturation = oca.Saturation;

      Plot2D plot = Plot2D.getInstance(ch.name());

      for (double s = oca.Saturation; s >= 0; s -= 0.01) {
        oca.Saturation = s;
        YPbPr ypbpr = new YPbPr(oca);
        RGB ypprgb = ypbpr.toRGB();
        if (!ypprgb.isLegal() && showIllegal) {
          continue;
        }
        ypprgb.rationalize();
        CIEXYZ XYZ = ypprgb.toXYZ();
        double[] uvp = XYZ.getuvPrimeValues();
        plot.addCacheScatterLinePlot("org", ch.color, uvp[0], uvp[1]);
      }

      for (double hueshift = 1; hueshift <= 30; hueshift++) {

        for (double s = saturation; s >= 0; s -= 0.01) {
          oca.Saturation = s;

          ColorAppearanceAttribute shift = (ColorAppearanceAttribute) oca.clone();
          shift.Hue += hueshift;
          YPbPr sypbpr = new YPbPr(shift);
          RGB sypprgb = sypbpr.toRGB();
          if (!sypprgb.isLegal() && showIllegal) {
            continue;
          }
          sypprgb.rationalize();
          CIEXYZ sXYZ = sypprgb.toXYZ();
          double[] suvp = sXYZ.getuvPrimeValues();
          plot.addCacheScatterLinePlot("shift " + hueshift, Color.black, suvp[0],
                                       suvp[1]);
        }
      }
//    plot.addLegend();
      plot.setAxeLabel(0, "u'");
      plot.setAxeLabel(1, "v'");
      plot.drawCachePlot();
      plot.setVisible();
    }
//    RGB rgb = new RGB(RGB.RGBColorSpace.sRGB, new int[] {0, 255, 0});

  }

  public static void uvPlot(String[] args) {
//    hueShift(args);


    for (int Y = 1; Y <= 1; Y++) {
      double y = ( (double) Y) / 10.;
      Plot2D plot = Plot2D.getInstance(Double.toString(y));
      for (double hue = 0; hue <= 360; hue += 5) {
        for (double s = 0.002; s < 0.6; s += 0.002) {
          ColorAppearanceAttribute ca = new ColorAppearanceAttribute(RGB.
              ColorSpace.
              sRGB,
              new double[] {y, s, hue});
          YPbPr ypp = new YPbPr(ca);
          RGB rgb = ypp.toRGB();
          if (!rgb.isLegal()) {
            continue;
          }
          CIEXYZ XYZ = rgb.toXYZ();
          double[] uvp = XYZ.getuvPrimeValues();
          plot.addCacheScatterLinePlot(Double.toString(hue), rgb.getColor(),
                                       uvp[0],
                                       uvp[1]);
        }
      }
      plot.setAxeLabel(0, "u'");
      plot.setAxeLabel(1, "v'");
      plot.setFixedBounds(0, 0.1, 0.45);
      plot.setFixedBounds(1, 0.15, 0.6);
      plot.setVisible();
    }

  }

  public static void hueShift(String[] args) {
    Plot2D plot = Plot2D.getInstance();
    plot.setVisible();

    for (double shift = 1; shift <= 25; shift++) {
      for (double hue = 0; hue <= 360; hue += 30) {
        for (double s = 0.001; s < 0.6; s += 0.001) {
          ColorAppearanceAttribute ca = new ColorAppearanceAttribute(RGB.
              ColorSpace.
              sRGB,
              new double[] {0.8, s, hue});
          YPbPr ypp = new YPbPr(ca);
          RGB rgb = ypp.toRGB();
          if (!rgb.isLegal()) {
            continue;
          }
          plot.addCacheScatterLinePlot(Double.toString(hue), rgb.getColor(),
                                       ypp.Pb,
                                       ypp.Pr);

          YPbPr ypp2 = new YPbPr(rgb);
          ColorAppearanceAttribute ca2 = new ColorAppearanceAttribute(ypp);
          ca2.Hue += shift;
          ypp2 = new YPbPr(ca2);
          RGB rgb2 = ypp2.toRGB();
          if (!rgb2.isLegal()) {
            continue;
          }

          plot.addCacheScatterLinePlot(Double.toString(hue) + "+" + shift,
                                       Color.black,
                                       ypp2.Pb,
                                       ypp2.Pr);
        }
      }

      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException ex) {
      }

      plot.setTitle("+" + Double.toString(shift));
      plot.removeAllPlots();
      plot.drawCachePlot();
    }
//    plot.drawCachePlot();


  }

  public double Y;
  public double Pb;
  public double Pr;

  public String[] getBandNames() {
    return new String[] {
        "Y", "Pb", "Pr"};
  }

}
