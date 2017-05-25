package shu.cms.colorspace.depend;

import java.io.*;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.image.*;
import shu.cms.plot.*;

//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class HSV
    extends ColorAppearanceBase {

  public double H;
  public double S;
  public double V;

  public static void main(String[] args) {
//    double[] hsv = new double[] {
//        .5, .5, .5};
//    Sandbox.fastToRGBValues(hsv);
//    System.out.println(Arrays.toString(hsv));
    RGB rgb = new RGB(RGB.ColorSpace.sRGB, new double[] {0.116, 0.675, 0.255});
    HSV hsv1 = new HSV(rgb);
//    HSV hsv2 = AUO.fromRGB(rgb, true);
//    System.out.println(hsv1);
//    System.out.println(hsv2);
//    System.out.println(HSV.AUO.getStaticHuePrime());

  }

  public static void hueColor(String[] args) {
    for (int h = 0; h < 360; h += 15) {
      HSV hsv = new HSV(RGB.ColorSpace.unknowRGB, new double[] {h, 100, 100});
      RGB rgb = hsv.toRGB();
      rgb.changeMaxValue(RGB.MaxValue.Int8Bit);
      System.out.println(h + " " + rgb);
    }
  }

  public static void hue2RGB(String[] args) {
    int width = 1920;
    int linePatchCount = 4;
    int gap = 75;

    int[] vArray = new int[] {
        50, };
    int totalColotCount = 24 * vArray.length;
    int patchWidth = width / linePatchCount;
    int height = patchWidth * (totalColotCount / linePatchCount);
    int colorPatchWidth = patchWidth - 2 * gap;

    IntegerImage img = new IntegerImage(width, height);
    Graphics2D g = img.getBufferedImage().createGraphics();
    g.setColor(Color.gray);
    g.fillRect(0, 0, width, height);

    int index = 0;

    for (int v : vArray) {
      for (int x = 0; x < 360; x += 15) {
        HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {x, v, v});
        RGB orgRGB = hsv.toRGB();
        orgRGB.changeMaxValue(RGB.MaxValue.Int8Bit);
        g.setColor(orgRGB.getColor());
        g.fillRect( (index % linePatchCount) * patchWidth + gap,
                   (index / linePatchCount) * patchWidth + gap, colorPatchWidth,
                   colorPatchWidth);
        index++;
      }
    }

    try {
      ImageUtils.storeTIFFImage("hue.tif", img.getBufferedImage());
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  public static void diffCompare(String[] args) {
    CIEXYZ white = RGB.White.toXYZ(RGB.ColorSpace.sRGB);
    Plot2D plot = Plot2D.getInstance();

    for (int x = 0; x < 360; x += 15) {
      HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {x, 50, 50});
      RGB orgRGB = hsv.toRGB();

//       hsv.H += 1;
      hsv.S += 10;
//      hsv.V += 1;

      RGB rgb = hsv.toRGB();
      CIEXYZ orgXYZ = orgRGB.toXYZ();
      CIEXYZ XYZ = rgb.toXYZ();
      CIELab orgLab = new CIELab(orgXYZ, white);
      CIELab Lab = new CIELab(XYZ, white);
      DeltaE de = new DeltaE(orgLab, Lab);
      double[] dLCh = de.getCIE2000DeltaLCh();
      CIELCh orgLCh = new CIELCh(orgLab);
      CIELCh LCh = new CIELCh(Lab);
      double dL = orgLCh.L - LCh.L;
      double dC = orgLCh.C - LCh.C;
      double dh = orgLCh.h - LCh.h;
      double dS = orgLCh.getSaturation() - LCh.getSaturation();

//      plot.addCacheLinePlot("dL", 0, 1, dLCh[0]);
//      plot.addCacheLinePlot("dC", 0, 1, dLCh[1]);
//      plot.addCacheLinePlot("dh", 0, 1, dLCh[2]);
      plot.addCacheLinePlot("dL", 0, 1, dL);
      plot.addCacheLinePlot("dC", 0, 1, dC);
      plot.addCacheLinePlot("dh", 0, 1, dh);
      plot.addCacheLinePlot("dS", 0, 1, dS);
//      plot.addScatterPlot(null, rgb.getColor(), x, dLCh[0]);
    }
    plot.addLegend();
    plot.setVisible();
  }

  private final static int[] lshIndex = new int[] {
      2, 1, 0};

  public HSV(RGB rgb) {
    super(rgb, lshIndex);
  }

  public HSV(OpponentColorBase opponentColorBase) {
    super(opponentColorBase);
  }

  public HSV(RGB.ColorSpace colorSpace) {
    this(colorSpace, null, new MaxValue[] {MaxValue.Double360,
         MaxValue.Double100, MaxValue.Double100});
  }

  public HSV(RGB.ColorSpace colorSpace, double[] hsvValues) {
    this(colorSpace, hsvValues, new MaxValue[] {MaxValue.Double360,
         MaxValue.Double100, MaxValue.Double100});
  }

  public HSV(RGB.ColorSpace colorSpace, double[] hsvValues,
             MaxValue[] maxValues) {
    super(colorSpace, hsvValues, maxValues, lshIndex);
  }

  public RGB toRGB() {
    double[] rgbValues = toRGBValues(getValues());
//    return new RGB(rgbColorSpace, rgbValues, RGB.MaxValue.Double1);
    return new RGB(rgbColorSpace, rgbValues, RGB.MaxValue.Double255);
  }

  protected final double[] _getValues(double[] values) {
    values[0] = H;
    values[1] = S;
    values[2] = V;
    return values;
  }

  protected void _setValues(double[] values) {
    H = values[0];
    S = values[1];
    V = values[2];
  }

  public static double[] toRGBValues(double[] hsvValues) {
    double R = 0.0, G = 0.0, B = 0.0;
    double bmax, bmid, bmin, q;
    double h = hsvValues[0], s = hsvValues[1], b = hsvValues[2];

    bmax = (b * 255) / 100;
    bmin = (100 - s) * bmax / 100;
    q = (bmax - bmin) / 60;

    if (h >= 0 && h < 60) {
      bmid = (h - 0) * q + bmin;
      R = bmax;
      G = bmid;
      B = bmin;
    }
    else if (h >= 60 && h < 120) {
      bmid = - (h - 120) * q + bmin;
      R = bmid;
      G = bmax;
      B = bmin;
    }
    else if (h >= 120 && h < 180) {
      bmid = (h - 120) * q + bmin;
      R = bmin;
      G = bmax;
      B = bmid;
    }
    else if (h >= 180 && h < 240) {
      bmid = - (h - 240) * q + bmin;
      R = bmin;
      G = bmid;
      B = bmax;
    }
    else if (h >= 240 && h < 300) {
      bmid = (h - 240) * q + bmin;
      R = bmid;
      G = bmin;
      B = bmax;
    }
    else if (h >= 300 && h < 360) {
      bmid = - (h - 360) * q + bmin;
      R = bmax;
      G = bmin;
      B = bmid;
    }
//    R /= 255.;
//    G /= 255.;
//    B /= 255.;
    return new double[] {
        R, G, B};
  }

//
//  public static class AUO {
//
//    public final static short filterHueBit(HSV hsv, int bit) {
//      double angle = hsv.H % 60;
//
////      short shortAngle = (short) Math.floor(angle / 60. * Math.pow(2, bit));
//      short shortAngle = (short) Math.ceil(angle / 60. * Math.pow(2, bit));
//      if (bit == 10 && (hsv.H == 60 || hsv.H == 180)) {
//        shortAngle = 1023;
//      }
//      return shortAngle;
//    }
//
//    public final static HSV fromRGB(RGB rgb, boolean doNormalize) {
//      double[] rgbValues = new double[3];
//      rgb.getValues(rgbValues, RGB.MaxValue.Double1);
//
//      double[] hsvValues = fromRGBValues(rgbValues, doNormalize);
//      HSV hsv = new HSV(rgb.getRGBColorSpace(), hsvValues);
//      return hsv;
//
//    }
//
//    public final static HSV fromRGB(RGB rgb) {
//      return fromRGB(rgb, true);
//    }
//
//    private static double staticHuePrime;
//    public final static double getStaticHuePrime() {
//      return staticHuePrime;
//    }
//
//    public final static double[] fromRGBValues(double[] rgbValues,
//                                               boolean doNormalize) {
//      double bmax, bmin, hue = 0.0, saturation = 0.0, value = 0.0;
//      double r = rgbValues[0], g = rgbValues[1], b = rgbValues[2];
//
//      if (r > g) {
//        bmax = r;
//        bmin = g;
//      }
//      else {
//        bmax = g;
//        bmin = r;
//      }
//
//      if (b > bmax) {
//        bmax = b;
//      }
//      else if (b < bmin) {
//        bmin = b;
//      }
//
//      value = bmax * 100;
//
//      if (bmax > bmin) {
//        double chroma = (bmax - bmin);
//        saturation = chroma * 100 / bmax;
//
//        double originalHuePrime = getHuePrime(bmax, r, g, b, chroma);
//        double huePrime = originalHuePrime;
//        int one = (int) Math.pow(2, 10);
//        if (huePrime < 0) {
//          huePrime = (int) Math.floor(huePrime * one);
//        }
//        else {
//          huePrime = (int) (huePrime * one);
//        }
//        if (doNormalize) {
//          huePrime = huePrime / one;
//        }
//
//        if (bmax == r) {
//          if (b > g) {
//            hue = huePrime * 60 + 360;
//          }
//          else {
//            hue = huePrime * 60;
//          }
//          staticHuePrime = originalHuePrime;
//        }
//        else if (bmax == g) {
//          hue = huePrime * 60 + 120;
//          staticHuePrime = originalHuePrime + 2;
//        }
//        else if (bmax == b) {
//          hue = huePrime * 60 + 240;
//          staticHuePrime = originalHuePrime + 4;
//        }
//      }
//      return new double[] {
//          hue, saturation, value};
//    }
//
//    private static double getHuePrime(double bmax, double r, double g, double b,
//                                      double chroma) {
//      double huePrime = 0;
//      if (bmax == r) {
//        huePrime = (g - b) / chroma;
//      }
//      else if (bmax == g) {
//        huePrime = (b - r) / chroma; //2
//      }
//      else if (bmax == b) {
//        huePrime = (r - g) / chroma; //4
//      }
//      return huePrime;
//    }
//
//    public final static int PIECE_OF_HUE = 768;
//
//    /**
//     *
//     * @param hsv HSV
//     * @param auoHSVValues short[] 10/10/10bit
//     * @return short[]
//     */
//    public static short[] toHSVValues(HSV hsv, short[] auoHSVValues) {
//      //13bit
//      short h = (short) Math.round(hsv.H / 360. * PIECE_OF_HUE);
//      short s = (short) (hsv.S / 100. * 1024); //1024
//      short v = (short) Math.round(hsv.V / 100. * 1020); //1020
//      s = (s == 1024) ? 1023 : s;
//      auoHSVValues[0] = h;
//      auoHSVValues[1] = s;
//      auoHSVValues[2] = v;
//      return auoHSVValues;
//    }
//
//    public static HSV fromHSVValues(short[] auoHSVValues) {
//      double h = ( (double) auoHSVValues[0]) / PIECE_OF_HUE * 360;
//      double s = auoHSVValues[1] / 1024. * 100;
//      double v = auoHSVValues[2] / 1020. * 100;
//      HSV hsv = new HSV(RGB.ColorSpace.unknowRGB, new double[] {h, s, v});
//      return hsv;
//    }
//
//    public static short[] toHSVValues(HSV hsv) {
//      short[] auoHSVValues = new short[3];
//      toHSVValues(hsv, auoHSVValues);
//      return auoHSVValues;
//    }
//
//  }

  public static class Sandbox {

    private static double[] RGBValues = new double[3];
    private static double bmax, bmid, bmin, q, h, s, b;

    public static void fastToRGBValues(double[] hsvValues) {
      h = hsvValues[0];
      s = hsvValues[1];
      b = hsvValues[2];

      bmax = (b * 255);
      bmin = (1 - s) * bmax;
      q = (bmax - bmin) / 60;

      int hIndex = (int) (h / 60);
      switch (hIndex) {
        case 0:
          bmid = (h - 0) * q + bmin;
          RGBValues[0] = bmax;
          RGBValues[1] = bmid;
          RGBValues[2] = bmin;
          break;
        case 1:
          bmid = - (h - 120) * q + bmin;
          RGBValues[0] = bmid;
          RGBValues[1] = bmax;
          RGBValues[2] = bmin;
          break;
        case 2:
          bmid = (h - 120) * q + bmin;
          RGBValues[0] = bmin;
          RGBValues[1] = bmax;
          RGBValues[2] = bmid;
          break;
        case 3:
          bmid = - (h - 240) * q + bmin;
          RGBValues[0] = bmin;
          RGBValues[1] = bmid;
          RGBValues[2] = bmax;
          break;
        case 4:
          bmid = (h - 240) * q + bmin;
          RGBValues[0] = bmid;
          RGBValues[1] = bmin;
          RGBValues[2] = bmax;
          break;
        case 5:
          bmid = - (h - 360) * q + bmin;
          RGBValues[0] = bmax;
          RGBValues[1] = bmin;
          RGBValues[2] = bmid;
          break;
      }

      hsvValues[0] = RGBValues[0];
      hsvValues[1] = RGBValues[1];
      hsvValues[2] = RGBValues[2];
    }
  }

  public final static double[] fromRGBValues(double[] rgbValues) {
    double bmax, bmin, q, hue = 0.0, saturation = 0.0, value = 0.0;
    double r = rgbValues[0], g = rgbValues[1], b = rgbValues[2];

    if (r > g) {
      bmax = r;
      bmin = g;
    }
    else {
      bmax = g;
      bmin = r;
    }

    if (b > bmax) {
      bmax = b;
    }
    else if (b < bmin) {
      bmin = b;
    }

    value = bmax * 100;

    if (bmax > bmin) {
      double chroma = (bmax - bmin);
      saturation = chroma * 100 / bmax;
      q = 60 / chroma;

      if (bmax == r) {
        if (b > g) { //Math.abs(r - g) - Math.abs(g - b)
          hue = q * (g - b) + 360;
        }
        else { // g>=b ==>  Math.abs(g - b)
          hue = q * (g - b);
        }
      }
      else if (bmax == g) {
        hue = q * (b - r) + 120; //2
      }
      else if (bmax == b) {
        hue = q * (r - g) + 240; //4
      }
    }
    return new double[] {
        hue, saturation, value};
  }

  protected double[] _fromRGB(RGB rgb) {
    double[] rgbValues = new double[3];
    rgb.getValues(rgbValues, RGB.MaxValue.Double1);

    double[] hsvValues = fromRGBValues(rgbValues);
    return hsvValues;
  }

  public String[] getBandNames() {
    return new String[] {
        "H", "S", "V"};
  }

  public double getChroma() {
    return S / 100. * V;
  }

  public double getMinimum() {
    return V - getChroma();
  }

  public static Color getLineColor(double h) {
    HSV color = new HSV(RGB.ColorSpace.sRGB, new double[] {h, 100, 85});
    return color.toRGB().getColor();
  }

  public void clip() {
    S = S < 0 ? 0 : S;
    S = S > 100 ? 100 : S;
    V = V < 0 ? 0 : V;
    V = V > 100 ? 100 : V;
  }
}
