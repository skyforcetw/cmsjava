package shu.cms.colorspace.depend;

import shu.cms.colorspace.depend.RGBBase.ColorSpace;
import shu.math.Maths;

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
public class CylindricalColorSpace
    extends ColorAppearanceBase {
  private final static int[] lshIndex = new int[] {
      2, 1, 0};
  public CylindricalColorSpace(RGB rgb) {
    super(rgb, lshIndex);
  }

  public CylindricalColorSpace(RGB rgb, Strategys strategys) {
    super(rgb, lshIndex);
    setStrategys(strategys);
  }

  public CylindricalColorSpace(RGB rgb, Strategy polar, Strategy distance,
                               Strategy longitudinal) {
    super(rgb, lshIndex);
    setStrategy(polar, distance, longitudinal);
  }

  /*public CylindricalColorSpace(OpponentColorBase opponentColorBase) {
    super(opponentColorBase);
     }

     public CylindricalColorSpace(ColorSpace colorSpace, int[] lshIndex) {
    super(colorSpace, lshIndex);
     }

     public CylindricalColorSpace(ColorSpace colorSpace, double[] values,
                               int[] lshIndex) {
    super(colorSpace, values, lshIndex);
     }*/

  private CylindricalColorSpace(ColorSpace colorSpace, double H, double H2,
                                double C, double C2, double V,
                                double L, double I, double Y, double S_HSV,
                                double S_HSL, double S_HSI) {
    super(colorSpace, null);
    this.H = H;
    this.H2 = H2;
    this.C = C;
    this.C2 = C2;
    this.V = V;
    this.L = L;
    this.I = I;
    this.Y = Y;
    this.S_HSV = S_HSV;
    this.S_HSL = S_HSL;
    this.S_HSI = S_HSI;
  };

  /**
   * _fromRGB
   *
   * @param rgb RGB
   * @return RGBBasis
   */
  protected double[] _fromRGB(RGB rgb) {
    double rgbValues[] = new double[3];
    rgb.getValues(rgbValues, RGB.MaxValue.Double1);
    double R = rgbValues[0];
    double G = rgbValues[1];
    double B = rgbValues[2];

    double M = Maths.max(rgbValues);
    double m = Maths.min(rgbValues);
    //chroma
    double C = M - m;
    double Hp = 0;
    if (M == R) {
      Hp = ( (G - B)) / C % 6.;
    }
    else if (M == G) {
      Hp = (B - R) / C + 2;
    }
    else if (M == B) {
      Hp = (R - G) / C + 4;
    }
    Hp = Double.isNaN(Hp) ? 0 : Hp;
    //hexagons  hue
    double H = 60 * Hp;
    H = H < 0 ? H + 360 : H;
    double alpha = 1. / 2 * (2 * R - G - B);
    double beta = Math.sqrt(3) / 2 * (G - B);
    //circles hue
    double H2 = Math.toDegrees(Math.atan2(beta, alpha));
    H2 = H2 < 0 ? H2 + 360 : H2;
    //circles chroma
    double C2 = Math.sqrt(Maths.sqr(alpha) + Maths.sqr(beta));
    //intensity
    double I = 1. / 3 * (R + G + B);
    //value
    double V = M;
    //lightness
    double L = 1. / 2 * (M + m);

    double Y = (ITU_R.getITU_R(rgb.getRGBColorSpace()) == ITU_R.BT709) ?
        (21 * R + .72 * G + .07 * B) :
        (.30 * R + .59 * G + .11 * B);

    //Saturation
    double S_HSV = (C == 0) ? 0 : C / V;
    double S_HSL = (C == 0) ? 0 : (L <= .5) ? C / (2 * L) : C / (2 - 2 * L);
    double S_HSI = (C == 0) ? 0 : 1 - m / I;

    double[] values = new double[] {
        H, H2, C, C2, V, L, I, Y, S_HSV, S_HSL, S_HSI};
    return values;
  }

  /**
   * _getValues
   *
   * @param values double[]
   * @return double[]
   */
  protected double[] _getValues(double[] values) {
    if (values.length != getNumberBands()) {
      throw new IllegalArgumentException("values.length != " + getNumberBands());
    }
    if (true == strategy) {
      values[0] = getValue(polar);
      values[1] = getValue(distance);
      values[2] = getValue(longitudinal);
    }
    else {
      values[0] = H;
      values[1] = H2;
      values[2] = C;
      values[3] = C2;
      values[4] = V;
      values[5] = L;
      values[6] = I;
      values[7] = Y;
      values[8] = S_HSV;
      values[9] = S_HSL;
      values[10] = S_HSI;
    }

    return values;
  }

  /**
   * _setValues
   *
   * @param values double[]
   */
  protected void _setValues(double[] values) {
    H = values[0];
    H2 = values[1];
    C = values[2];
    C2 = values[3];
    V = values[4];
    L = values[5];
    I = values[6];
    Y = values[7];
    S_HSV = values[8];
    S_HSL = values[9];
    S_HSI = values[10];
  }

  /**
   * getBandNames
   *
   * @return String[]
   */
  public String[] getBandNames() {
    return true == strategy ? new String[] {
        polar.name(), distance.name(), longitudinal.name()}
        : new String[] {
        "H", "H2", "C", "C2", "V", "L", "I", "Y", "S HSV", "S HSL", "S HSI"};
  }

  /**
   * getNumberBands
   *
   * @return int
   */
  protected int getNumberBands() {
    return true == strategy ? 3 : 11;
  }

  /**
   * toRGB
   *
   * @return RGB
   * @todo Implement this shu.cms.colorspace.depend.RGBBasis method
   */
  public RGB toRGB() {
    return null;
  }

  public double H, H2, C, C2, V, L, I, Y, S_HSV, S_HSL, S_HSI;

  public enum Strategy {
    H(true, false, false),
    H2(true, false, false),
    C(false, true, false),
    C2(false, true, false),
    V(false, false, true),
    L(false, false, true),
    I(false, false, true),
    Y(false, false, true),
    S_HSV(false, true, false),
    S_HSL(false, true, false),
    S_HSI(false, true, false);

    Strategy(boolean polar, boolean distance, boolean longitudinal) {
      this.polar = polar;
      this.distance = distance;
      this.longitudinal = longitudinal;
    }

    boolean polar;
    boolean distance;
    boolean longitudinal;
  }

  public enum Strategys {
    HSV(Strategy.H, Strategy.S_HSV, Strategy.V),
    HSL(Strategy.H, Strategy.S_HSL, Strategy.L),
    HSI(Strategy.H, Strategy.S_HSI, Strategy.I);

    Strategys(Strategy polar, Strategy distance, Strategy longitudinal) {
      this.polar = polar;
      this.distance = distance;
      this.longitudinal = longitudinal;
    }

    Strategy polar;
    Strategy distance;
    Strategy longitudinal;

  }

  public double getValue(Strategy strategy) {
    switch (strategy) {
      case H:
        return H;
      case H2:
        return H2;
      case C:
        return C;
      case C2:
        return C2;
      case V:
        return V;
      case L:
        return L;
      case I:
        return I;
      case Y:
        return Y;
      case S_HSV:
        return S_HSV;
      case S_HSL:
        return S_HSL;
      case S_HSI:
        return S_HSI;
      default:
        throw new IllegalArgumentException();
    }
  }

  public Strategy polar, distance, longitudinal;
  private boolean strategy = false;
  public void setStrategy(Strategy polar, Strategy distance,
                          Strategy longitudinal) {
    strategy = true;
    this.polar = polar;
    this.distance = distance;
    this.longitudinal = longitudinal;
  }

  public void setStrategys(Strategys strategys) {
    setStrategy(strategys.polar, strategys.distance, strategys.longitudinal);
  }

  public static void main(String[] args) {
    RGB rgb = new RGB(.75 * 255, .25 * 255, .75 * 255);
    //RGB rgb = new RGB(255,255,255);
    rgb.setRGBColorSpace(RGB.ColorSpace.SMPTE_C);
    rgb.changeMaxValue(RGB.MaxValue.Double1);

    System.out.println(rgb);
//    System.out.println(rgb.toXYZ());
    CylindricalColorSpace c = new CylindricalColorSpace(rgb);
    System.out.println(c);
    c.setStrategy(Strategy.H, Strategy.S_HSV, Strategy.V);
    System.out.println(c);

    HSV hsv = new HSV(rgb);
    System.out.println("hsv: " + hsv);
    HSL hsl = new HSL(rgb);
    System.out.println("hsl: " + hsl);
  }

}
