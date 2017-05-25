package shu.cms.colorspace.depend;

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
public class HSL
    extends ColorAppearanceBase {

  public double H;
  public double S;
  public double L;

  public static void main(String[] args) {

    for (int x = 0; x < 100000; x++) {
      RGB rgb = new RGB(RGB.ColorSpace.sRGB, new double[] {Math.random(),
                        Math.random(), Math.random()});
      HSL hsl = new HSL(rgb);
      RGB rgb1 = hsl.toRGB();
//      System.out.println(rgb + " HSL: " + hsl + " " + rgb1);
      HSV hsv = new HSV(rgb);
      RGB rgb2 = hsv.toRGB();
//      System.out.println(rgb + " HSV: " + hsv + " " + rgb2);

      if (! (rgb.equalsAfterQuantization(rgb1, RGB.MaxValue.Int10Bit) &&
             rgb.equalsAfterQuantization(rgb2, RGB.MaxValue.Int10Bit))) {
        System.out.println("!!!");
      }

    }

//    System.out.println(hsv);
  }

  private final static int[] lshIndex = new int[] {
      2, 1, 0};
  public HSL(RGB rgb) {
    super(rgb, lshIndex);
  }

  public HSL(OpponentColorBase opponentColorBase) {
    super(opponentColorBase);
  }

  public HSL(RGB.ColorSpace colorSpace) {
    super(colorSpace, lshIndex);
  }

  public HSL(RGB.ColorSpace colorSpace, double[] hsvValues) {
    super(colorSpace, hsvValues, lshIndex);
  }

  public RGB toRGB() {
    return toRGB(this);
  }

  protected final double[] _getValues(double[] values) {
    values[0] = H;
    values[1] = S;
    values[2] = L;
    return values;
  }

  protected void _setValues(double[] values) {
    H = values[0];
    S = values[1];
    L = values[2];
  }

  public static RGB toRGB(HSL hsl) {
    double[] rgbValues = toRGBValues(hsl.getValues());
    return new RGB(hsl.rgbColorSpace, rgbValues, RGB.MaxValue.Double1);
  }

  public static double[] toRGBValues(double[] hslValues) {
    double h = hslValues[0], s = hslValues[1] / 100., l = hslValues[2] / 100.;
    double q = 0;
    if (l < .5) {
      q = l * (1 + s);
    }
    else {
      q = l + s - (l * s);
    }

    double oneThird = 1. / 3.;
    double oneSixth = 1 / 6.;
    double half = 1. / 2.;
    double twoThird = 2. / 3.;

    double p = 2 * l - q;
    double hk = h / 360.;
    double tR = hk + oneThird;
    double tG = hk;
    double tB = hk - oneThird;
    double[] tC = new double[] {
        tR, tG, tB};
    for (int x = 0; x < 3; x++) {
      if (tC[x] < 0) {
        tC[x] += 1.0;
      }
      else if (tC[x] > 1) {
        tC[x] -= 1.0;
      }

      if (tC[x] < oneSixth) {
        tC[x] = p + ( (q - p) * 6 * tC[x]);
      }
      else if (tC[x] >= oneSixth && tC[x] < 1. / 2.) {
        tC[x] = q;
      }
      else if (tC[x] >= half && tC[x] < twoThird) {
        tC[x] = p + ( (q - p) * 6 * (twoThird - tC[x]));
      }
      else {
        tC[x] = p;
      }

    }
    return tC;
//    double R = 0.0, G = 0.0, B = 0.0;
//    double bmax, bmid, bmin, q;
//    double h = hslValues[0], s = hslValues[1], b = hslValues[2];
//
//    bmax = (b * 255) / 100;
//    bmin = (100 - s) * bmax / 100;
//    q = (bmax - bmin) / 60;
//
//    if (h >= 0 && h < 60) {
//      bmid = (h - 0) * q + bmin;
//      R = bmax;
//      G = bmid;
//      B = bmin;
//    }
//    else if (h >= 60 && h < 120) {
//      bmid = - (h - 120) * q + bmin;
//      R = bmid;
//      G = bmax;
//      B = bmin;
//    }
//    else if (h >= 120 && h < 180) {
//      bmid = (h - 120) * q + bmin;
//      R = bmin;
//      G = bmax;
//      B = bmid;
//    }
//    else if (h >= 180 && h < 240) {
//      bmid = - (h - 240) * q + bmin;
//      R = bmin;
//      G = bmid;
//      B = bmax;
//    }
//    else if (h >= 240 && h < 300) {
//      bmid = (h - 240) * q + bmin;
//      R = bmid;
//      G = bmin;
//      B = bmax;
//    }
//    else if (h >= 300 && h < 360) {
//      bmid = - (h - 360) * q + bmin;
//      R = bmax;
//      G = bmin;
//      B = bmid;
//    }
//    R /= 255.;
//    G /= 255.;
//    B /= 255.;
//    return new double[] {
//        R, G, B};
  }

  public final static double[] fromRGBValues(double[] rgbValues) {
    double bmax, bmin, q, hue = 0.0, saturation = 0.0, lightness = 0.0;
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

    lightness = (bmax + bmin) / 2. * 100;

    if (bmax > bmin) {
      if (lightness <= 50) {
        saturation = (bmax - bmin) / (2 * lightness / 100.) * 100;
      }
      else {
        saturation = (bmax - bmin) / (2 - 2 * (lightness / 100.)) * 100;
      }

      q = 60 / (bmax - bmin);

      if (bmax == r) {
        if (b > g) {
          hue = q * (g - b) + 360;
        }
        else {
          hue = q * (g - b);
        }
      }
      else if (bmax == g) {
        hue = q * (b - r) + 120;
      }
      else if (bmax == b) {
        hue = q * (r - g) + 240;
      }
    }
    return new double[] {
        hue, saturation, lightness};
  }

  protected double[] _fromRGB(RGB rgb) {
    double[] rgbValues = new double[3];
    rgb.getValues(rgbValues, RGB.MaxValue.Double1);
    double[] hslValues = fromRGBValues(rgbValues);

//    if (rgb.getMaxValue() != RGB.MaxValue.Double1) {
//      hslValues[2] /= 255.;
//    }
    return hslValues;
  }

  public String[] getBandNames() {
    return new String[] {
        "H", "S", "L"};
  }

}
