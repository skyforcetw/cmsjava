package auo.mura.evaluate;

import shu.math.array.*;
import shu.plot.*;
import java.awt.Color;
import shu.cms.colorspace.depend.*;
import shu.cms.plot.Plot3D;

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
public class YCCEvaluator {

  static double[] rgb2ycc1(double[] rgbValues) {
    double r = rgbValues[0];
    double g = rgbValues[1];
    double b = rgbValues[2];
    double Y = .25 * r + .5 * g + .25 * b;
    double cb = (g - b) / 2 + 128;
    double cr = (g - r) / 2 + 128;
    return new double[] {
        Y, cb, cr};
  }

  static double[] rgb2ycc(double[] rgbValues, double Kr, double Kb) {
    double Kg = 1 - Kr - Kb;
    double r = rgbValues[0];
    double g = rgbValues[1];
    double b = rgbValues[2];
    double Y = Kr * r + Kg * g + Kb * b;
    double cb = -Kr * r - Kg * g + (1 - Kb) * b;
    double cr = (1 - Kr) * r - Kg * g - Kb * b;
    return new double[] {
        Y, cb, cr};
  }

  static double[] rgb2ycc3(double[] rgbValues) {
    double r = rgbValues[0];
    double g = rgbValues[1];
    double b = rgbValues[2];
    double Y = .25 * r + .5 * g + .25 * b;
//    double cb = (b - g) / 2 + 128;
//    double cb = 3 / 4. * b - 1 / 2. * g + 128;
    double cb = -0.25 * r - 0.5 * g + (1 - 0.25) * b;
//    double cr = (r - g) / 2 + 128;
//    double cr = 3 / 4. * r - 1 / 2. * g + 128;
//    double cr = r - Y;
    double cr = (1 - 0.25) * r - 0.5 * g - 0.25 * b;
    return new double[] {
        Y, cb, cr};
  }

  static double[] ycc2rgb1(double[] yccValues) {
    double Y = yccValues[0];
    double cb = yccValues[1];
    double cr = yccValues[2];

    double g = (2 * Y + (cb - 128) + (cr - 128)) / 2;
    double r = g - 2 * (cr - 128);
    double b = g - 2 * (cb - 128);
    return new double[] {
        r, g, b};
  }

  static double[] rgb2ycc2(double[] rgbValues) {
    double r = rgbValues[0];
    double g = rgbValues[1];
    double b = rgbValues[2];
    double Y = .299 * r + .587 * g + .114 * b;
    double cb = 128 - (0.168736 * r) - (0.331264 * g) + 0.5 * b;
    double cr = 128 + (0.5 * r) - (0.418688 * g) - 0.081312 * b;
    return new double[] {
        Y, cb, cr};
  }

  static double[] ycc2rgb2(double[] yccValues) {
    double Y = yccValues[0];
    double cb = yccValues[1];
    double cr = yccValues[2];
    double r = Y + 1.402 * (cr - 128);
    double g = Y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128);
    double b = Y + 1.772 * (cb - 128);
    return new double[] {
        r, g, b};
  }

  public static void showPlot(String[] args) {
    Plot3D plot1 = Plot3D.getInstance("sRGB");
    Plot3D plot2 = Plot3D.getInstance("Simplify1");
    int interval = 34;
    for (int r = 255; r > 0; r -= interval) {
      for (int g = 255; g > 0; g -= interval) {
        for (int b = 255; b > 0; b -= interval) {
//      int g = r, b = r;
          RGB rgb0 = new RGB(RGB.ColorSpace.sRGB, new int[] {r, g, b});
          RGB rgb1 = new RGB(RGB.ColorSpace.unknowRGB, new int[] {r,
                             g, b});

          YCbCr ycc0 = new YCbCr(rgb0);
          YCbCr ycc1 = new YCbCr(rgb1);

//                    plot1.addScatterPlot("", rgb0.getColor(), ycc0.Cb, ycc0.Cr,
//                                         ycc0.Y);
//                    plot2.addScatterPlot("", rgb1.getColor(), ycc1.Cb, ycc1.Cr,
//                                         ycc1.Y);
          YCbCr ycc0_ = (YCbCr) ycc0.clone();
          YCbCr ycc1_ = (YCbCr) ycc1.clone();
          ycc0.Y += 50;
          ycc1.Y += 50;

          boolean over1 = ycc0.Y > 255;
          boolean over2 = ycc1.Y > 255;

          ycc0.Y = over1 ? 255 : ycc0.Y;
          ycc1.Y = over2 ? 255 : ycc1.Y;

          RGB rgb0_ = ycc0.toRGB();
          RGB rgb1_ = ycc1.toRGB();
          rgb0_.changeMaxValue(RGB.MaxValue.Int8Bit);
          rgb1_.changeMaxValue(RGB.MaxValue.Int8Bit);
//                    rgb1_.rationalize();
//                    rgb2_.rationalize();
          if ( (rgb0_.R != rgb1_.R || rgb0_.G != rgb1_.G ||
                rgb0_.B != rgb1_.B) /*&& (!over1 && !over2)*/) {
            plot1.addScatterPlot("", Color.black, ycc0.Cb,
                                 ycc0.Cr,
                                 ycc0.Y);
            plot2.addScatterPlot("", Color.black, ycc1.Cb,
                                 ycc1.Cr,
                                 ycc1.Y);
            if (ycc0.Y != 255 || ycc1.Y != 255) {
              System.out.println(rgb0 + " " + rgb0_ + " " + rgb1_ + " " +
                                 ycc0.Y + " " + ycc1.Y + " " + over1 + " " +
                                 over2);
            }

          }
          else {
            plot1.addScatterPlot("", rgb0.getColor(), ycc0.Cb,
                                 ycc0.Cr,
                                 ycc0.Y);
            plot2.addScatterPlot("", rgb1.getColor(), ycc1.Cb,
                                 ycc1.Cr,
                                 ycc1.Y);

          }

        }
      }

    }
//    for (RGB.Channel ch : RGB.Channel.RGBYMCChannel) {
//
//      for (int v = 255; v > 0; v -= 17) {
//        RGB rgb0 = new RGB(RGB.ColorSpace.sRGB, RGB.MaxValue.Int8Bit);
//        RGB rgb1 = new RGB(RGB.ColorSpace.sRGB, RGB.MaxValue.Int8Bit);
//        rgb0.setValue(ch, v);
//        rgb1.setValue(ch, v);
//        YCbCr ycc0 = new YCbCr(rgb0);
//        YCbCr ycc1 = new YCbCr(rgb1);
//
//        plot1.addScatterPlot("", Color.black, ycc0.Cb,
//                             ycc0.Cr,
//                             ycc0.Y);
//        plot2.addScatterPlot("", Color.black, ycc1.Cb,
//                             ycc1.Cr,
//                             ycc1.Y);
//
//      }
//    }

    plot1.setVisible();
    plot2.setVisible();
    plot1.setAxisLabels("Cb", "Cr", "Y");
    plot2.setAxisLabels("Cb", "Cr", "Y");
    plot1.setFixedBounds(0, 0, 255);
    plot1.setFixedBounds(1, 0, 255);
    plot1.setFixedBounds(2, 0, 255);

    plot2.setFixedBounds(0, 0, 255);
    plot2.setFixedBounds(1, 0, 255);
    plot2.setFixedBounds(2, 0, 255);
//        plot2.setAutoRotate();
  }

  public static void pureCompare(String[] args) {
    int r = 255, g = 0, b = 0;
    RGB rgb0 = new RGB(RGB.ColorSpace.sRGB, new int[] {r, g, b});
    RGB rgb1 = new RGB(RGB.ColorSpace.unknowRGB, new int[] {r,
                       g, b});

    YCbCr ycc0 = new YCbCr(rgb0);
    YCbCr ycc1 = new YCbCr(rgb1);
    ycc0.Y += 20;
    ycc1.Y += 20;
    RGB rgb0_ = ycc0.toRGB();
    RGB rgb1_ = ycc1.toRGB();
    System.out.println(rgb0_);
    System.out.println(rgb1_);
  }

  public static void main(String[] args) {
//    sameRGB(null);
//    notsameRGB(null);
//    notsameRGBCompare(null);
//    plotYCC(null);
//    showPlot(null);
    pureCompare(null);
//    System.out.println(DoubleArray.toString(rgb2ycc2(new double[] {255, 0,
//        0})));
//    YCbCr ycc2 = new YCbCr(new RGB(RGB.ColorSpace.sRGB, new int[] {255, 0,
//                                   0}));
//    System.out.println(ycc2);
  }

  public static void diffcompare(String[] args) {

//
    RGB.ColorSpace[] colorSpaces = new RGB.ColorSpace[] {
        RGB.ColorSpace.
        sRGB, RGB.ColorSpace.unknowRGB};

    double maxdrg = 0;
    for (int r = 0; r < 256; r++) {
      for (int g = 0; g < 256; g++) {
        for (int b = 0; b < 256; b++) {
          RGB rgb1 = new RGB(colorSpaces[0], new int[] {r, g, b});
          RGB rgb2 = new RGB(colorSpaces[1], new int[] {r, g, b});
          YCbCr ycc1 = new YCbCr(rgb1);
          YCbCr ycc2 = new YCbCr(rgb2);
          ycc1.Y += 50;
          ycc2.Y += 50;

          boolean over1 = ycc1.Y > 255;
          boolean over2 = ycc2.Y > 255;

          ycc1.Y = over1 ? 255 : ycc1.Y;
          ycc2.Y = over2 ? 255 : ycc2.Y;

          RGB rgb1_ = ycc1.toRGB();
          RGB rgb2_ = ycc2.toRGB();
          rgb1_.changeMaxValue(RGB.MaxValue.Int8Bit);
          rgb2_.changeMaxValue(RGB.MaxValue.Int8Bit);
//                    rgb1_.rationalize();
//                    rgb2_.rationalize();
          if ( (rgb1_.R != rgb2_.R || rgb1_.G != rgb2_.G ||
                rgb1_.B != rgb2_.B) &&
              (rgb1_.isLegal() && rgb2_.isLegal()) &&
              (!over1 && !over2)) {
            System.out.println(rgb1_ + " " + rgb2_);
          }

        }

      }

    }

    for (int x = 50; x <= 50; x++) {
      RGB rgb1 = new RGB(colorSpaces[0], new int[] {200, 160, 30});
      RGB rgb2 = new RGB(colorSpaces[1], new int[] {200, 160, 30});
      YCbCr ycc1 = new YCbCr(rgb1);
      YCbCr ycc2 = new YCbCr(rgb2);
      ycc1.Y += x;
      ycc2.Y += x;
      RGB rgb1_ = ycc1.toRGB();
      RGB rgb2_ = ycc2.toRGB();
      System.out.println(x + " " + rgb1_ + " " + rgb2_);
    }

  }

  public static void plotYCC(String[] args) {
    Plot3D plot = Plot3D.getInstance();
    for (int r = 255; r >= 0; r -= 34) {
      for (int g = 255; g >= 0; g -= 34) {
        for (int b = 255; b >= 0; b -= 34) {
          RGB rgb = new RGB(RGB.ColorSpace.sRGB, new int[] {r, g, b});
//          YCbCr ycbcr = new YCbCr(rgb);
//          double[] ycc = ycbcr.getValues();
//          double[] ycc = YCbCr.fromRGB(rgb, 1 / 3., 1 / 3.);
//          ycc[0] *= 255;
//          double[] ycc = rgb2ycc3(new double[] {r, g, b});
          double[] ycc = rgb2ycc(new double[] {r, g, b}, .299, .114);
//          ycc[1] -= 128;
//          ycc[2] -= 128;
          Color c = rgb.getColor();
          plot.addScatterPlot("", c, ycc);
        }
      }
    }
    plot.setVisible();
    plot.setAxisLabels("Y", "Cb", "Cr");
//    System.out.println(DoubleArray.toString(rgb2ycc3(new double[] {255, 255,
//        255})));
//    plot.addLinePlot("", Color.black, new double[] {0, 0, 0}, new double[] {255,
//                     0, 0});
//    System.out.println(DoubleArray.toString(rgb2ycc1(new double[] {255, 0,
//        0})));
//    System.out.println(DoubleArray.toString(rgb2ycc2(new double[] {255, 255,
//        255})));

//    RGB rgb = new RGB(RGB.ColorSpace.sRGB, new int[] {250, 156, 10});
//    YCbCr ycbcr = new YCbCr(rgb);
//    System.out.println(ycbcr);
  }

  static void adjustY(double[] ycc, int adjustment) {
    ycc[0] += adjustment;
    ycc[0] = ycc[0] < 0 ? 0 : ycc[0];
    ycc[0] = ycc[0] > 255 ? 255 : ycc[0];
  }

  static void adjustY(double[] ycc, double adjustment) {
    ycc[0] += adjustment;
    ycc[0] = ycc[0] < 0 ? 0 : ycc[0];
    ycc[0] = ycc[0] > 255 ? 255 : ycc[0];
  }

  static boolean useFormula1 = true;
  static double[] getAdjustYRGB(double[] rgb, int adjustment) {
    double[] ycc1 = useFormula1 ? rgb2ycc1(rgb) : rgb2ycc2(rgb);
    adjustY(ycc1, adjustment);
    double[] result = useFormula1 ? ycc2rgb1(ycc1) : ycc2rgb2(ycc1);
    for (int x = 0; x < 3; x++) {
      result[x] = result[x] > 255 ? 255 : result[x];
      result[x] = result[x] < 0 ? 0 : result[x];
    }
    return result;
  }

  static double[] getAdjustYRGB(double[] rgb, double adjustment,
                                boolean formula1) {
    double[] ycc1 = formula1 ? rgb2ycc1(rgb) : rgb2ycc2(rgb);
    adjustY(ycc1, adjustment);
    double[] result = formula1 ? ycc2rgb1(ycc1) : ycc2rgb2(ycc1);
    for (int x = 0; x < 3; x++) {
      result[x] = result[x] > 255 ? 255 : result[x];
      result[x] = result[x] < 0 ? 0 : result[x];
    }
    return result;
  }

  static double[] getRGrg(double[] rgb) {
    double sum = DoubleArray.sum(rgb);
    if (sum != 0) {
      double rgR = rgb[0] / sum;
      double rgG = rgb[1] / sum;
      return new double[] {
          rgR, rgG};
    }
    else {
      return null;
    }
  }

  public static void notsameRGBCompare(String[] args) {
    int maxAdjustmentIn8Bit = 30;
    int step = 1;
    Plot2D plot = Plot2D.getInstance();

    for (double adj = 0; adj <= maxAdjustmentIn8Bit; adj += 1) {
      double[] maxDeltaRGB = new double[3];
      double[][] maxdRGB = new double[3][3];
      double[][] maxdRGrg = new double[2][2];
      double[][][] maxRGRGB = new double[2][2][3];
      for (int r = 0; r <= 255; r += step) {
        for (int g = 0; g <= 255; g += step) {
          for (int b = 0; b <= 255; b += step) {
            final double[] rgb0 = new double[] {
                r, g, b};
            double[] rgb1 = getAdjustYRGB(rgb0, adj, false);
            double[] rgb2 = getAdjustYRGB(rgb0, adj, true);
            double[] delta = DoubleArray.minus(rgb1, rgb2);
            DoubleArray.abs(delta);
            if (delta[0] > maxDeltaRGB[0]) {
              maxDeltaRGB[0] = delta[0];
              maxdRGB[0][0] = r;
              maxdRGB[0][1] = g;
              maxdRGB[0][2] = b;
            }
            if (delta[1] > maxDeltaRGB[1]) {
              maxDeltaRGB[1] = delta[1];
              maxdRGB[1][0] = r;
              maxdRGB[1][1] = g;
              maxdRGB[1][2] = b;

            }
            if (delta[2] > maxDeltaRGB[2]) {
              maxDeltaRGB[2] = delta[2];
              maxdRGB[2][0] = r;
              maxdRGB[2][1] = g;
              maxdRGB[2][2] = b;

            }
            double sum = r + g + b;
            if (0 != sum) {
              double[] rg0 = getRGrg(rgb0);
              double[] rg1 = getRGrg(rgb1);
              double[] rg2 = getRGrg(rgb2);
              double[] drg01 = DoubleArray.minus(rg1, rg0);
              double[] drg02 = DoubleArray.minus(rg2, rg0);
              DoubleArray.abs(drg01);
              DoubleArray.abs(drg02);
              //f1
              if (drg01[0] > maxdRGrg[0][0]) {
                maxdRGrg[0][0] = drg01[0];
                maxRGRGB[0][0] = rgb0;
              }
              if (drg01[1] > maxdRGrg[0][1]) {
                maxdRGrg[0][1] = drg01[1];
                maxRGRGB[0][1] = rgb0;
              }
              //f2
              if (drg02[0] > maxdRGrg[1][0]) {
                maxdRGrg[1][0] = drg02[0];
                maxRGRGB[1][0] = rgb0;
              }
              if (drg02[1] > maxdRGrg[1][1]) {
                maxdRGrg[1][1] = drg02[1];
                maxRGRGB[1][1] = rgb0;
              }
            }
          }
        }

      }
      //========================================================================
      // rgb compare
      //========================================================================
      System.out.println(adj + " " + DoubleArray.toString(maxDeltaRGB));

      System.out.println("     " + DoubleArray.toString(maxdRGB[0]) + "/" +
                         DoubleArray.toString(maxdRGB[1]) + "/" +
                         DoubleArray.toString(maxdRGB[2]));
      for (int x = 0; x < 3; x++) {
        double[] rgb1 = getAdjustYRGB(maxdRGB[x], adj, false);
        double[] rgb2 = getAdjustYRGB(maxdRGB[x], adj, true);
        System.out.println(x + " (1)" + DoubleArray.toString(rgb1) + " / (2)" +
                           DoubleArray.toString(rgb2) + " : (d)" +
                           DoubleArray.toString(DoubleArray.minus(rgb1, rgb2)));
        System.out.println("    " + DoubleArray.toString(getRGrg(maxdRGB[x])));
        System.out.println("    " + DoubleArray.toString(getRGrg(rgb1)));
        System.out.println("    " + DoubleArray.toString(getRGrg(rgb2)));
      } //========================================================================
      //========================================================================
      //rg compare
      //========================================================================
//      System.out.println(DoubleArray.toString(maxdRGrg));
      //========================================================================
      System.out.println("");

//      plot.addCacheScatterLinePlot("r", Color.red, adj, maxDeltaRGB[0]);
//      plot.addCacheScatterLinePlot("g", Color.green, adj, maxDeltaRGB[1]);
//      plot.addCacheScatterLinePlot("b", Color.blue, adj, maxDeltaRGB[2]);

//      System.out.println("adj " + adj + ": " + maxdRGr + " " + maxdRGg);
//      System.out.println(DoubleArray.toString(maxdRGrRGB) + " / " +
//                         DoubleArray.toString(maxdRGgRGB));
//      double[] rgb1 = getAdjustYRGB(maxdRGrRGB, adj);
//      double[] rgb2 = getAdjustYRGB(maxdRGgRGB, adj);
//      System.out.println(DoubleArray.toString(rgb1) + " / " +
//                         DoubleArray.toString(rgb2));
//      plot.addCacheScatterLinePlot("r", Color.red, adj, maxdRGr);
//      plot.addCacheScatterLinePlot("g", Color.green, adj, maxdRGr);
    }
    plot.setVisible();
  }

  public static void notsameRGB(String[] args) {
    int maxAdjustmentIn8Bit = 15;
    int step = 1;
    Plot2D plot = Plot2D.getInstance();

    for (int adj = 0; adj <= maxAdjustmentIn8Bit; adj++) {
      double maxdRGr = 0, maxdRGg = 0;
      double[] maxdRGrRGB = new double[3];
      double[] maxdRGgRGB = new double[3];
      for (int r = 0; r <= 255; r += step) {
        for (int g = 0; g <= 255; g += step) {
          for (int b = 0; b <= 255; b += step) {
//            double[] rgb = getAdjustYRGB(new double[] {r, g, b}, adj);
            double[] rgb = getAdjustYRGB(new double[] {r, g, b}, adj, false);

            if ( (r + g + b) != 0) {
              double[] rg = getRGrg(new double[] {r, g, b});
              double[] rgp = getRGrg(rgb);
              double dRGr = Math.abs(rg[0] - rgp[0]);
              double dRGg = Math.abs(rg[1] - rgp[1]);
              if (dRGr > maxdRGr) {
                maxdRGr = dRGr;
                maxdRGrRGB[0] = r;
                maxdRGrRGB[1] = g;
                maxdRGrRGB[2] = b;
              }
              if (dRGg > maxdRGg) {
                maxdRGg = dRGg;
                maxdRGgRGB[0] = r;
                maxdRGgRGB[1] = g;
                maxdRGgRGB[2] = b;

              }
            }

          }
        }

      }

      System.out.println("adj " + adj + ": " + maxdRGr + " " + maxdRGg);
      System.out.println(DoubleArray.toString(maxdRGrRGB) + " / " +
                         DoubleArray.toString(maxdRGgRGB));
      double[] rgb1 = getAdjustYRGB(maxdRGrRGB, adj);
      double[] rgb2 = getAdjustYRGB(maxdRGgRGB, adj);
      System.out.println(DoubleArray.toString(rgb1) + " / " +
                         DoubleArray.toString(rgb2));
      plot.addCacheScatterLinePlot("r", Color.red, adj, maxdRGr);
      plot.addCacheScatterLinePlot("g", Color.green, adj, maxdRGr);
    }
    plot.setVisible();
  }

  public static void sameRGB(String[] args) {
//    rgb2ycc1(new double[]{255

    int maxAdjustmentIn8Bit = 1;
    for (int adj = -maxAdjustmentIn8Bit; adj <= maxAdjustmentIn8Bit; adj++) {
      double maxDelta = 0;
      for (int x = 0; x <= 1020; x++) {
        double grayLevel = x / 4.;
//        System.out.println(grayLevel);
        double[] ycc1 = rgb2ycc1(new double[] {grayLevel, grayLevel, grayLevel});
        ycc1[0] += adj;
        ycc1[0] = ycc1[0] < 0 ? 0 : ycc1[0];
        ycc1[0] = ycc1[0] > 255 ? 255 : ycc1[0];
        double[] rgb = ycc2rgb1(ycc1);
        System.out.println(DoubleArray.toString(rgb));
        double max = DoubleArray.max(rgb);
        double min = DoubleArray.min(rgb);
        double delta = max - min;
        maxDelta = delta > maxDelta ? delta : maxDelta;
      }
      System.out.println(adj + " " + maxDelta);
    }
  }
}
