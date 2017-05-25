package shu.cms.colorspace.depend;

import shu.math.*;

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
public class TSL
    extends ColorAppearanceBase {

  public double T;
  public double S;
  public double L;
  private final static int[] lshIndex = new int[] {
      2, 1, 0};
  public TSL(RGB rgb) {
    super(rgb, lshIndex);
  }

  public TSL(OpponentColorBase opponentColorBase) {
    super(opponentColorBase);
  }

  public TSL(RGB.ColorSpace colorSpace) {
    super(colorSpace, lshIndex);
  }

  public TSL(RGB.ColorSpace colorSpace, double[] tslValues) {
    super(colorSpace, tslValues, lshIndex);
  }

  public RGB toRGB() {
    throw new UnsupportedOperationException();
  }

  /**
   * getValues
   *
   * @param values double[]
   * @return double[]
   */
  protected double[] _getValues(double[] values) {
    values[0] = T;
    values[1] = S;
    values[2] = L;
    return values;
  }

  /**
   * setValues
   *
   * @param values double[]
   */
  protected void _setValues(double[] values) {
    T = values[0];
    S = values[1];
    L = values[2];
  }

  protected double[] _fromRGB(RGB rgb) {
    double r = rgb.getRGr();
    double g = rgb.getRGg();
    double rp = r - (1. / 3);
    double gp = g - (1. / 3);
    rp = Double.isNaN(rp) ? 0 : rp;
    gp = Double.isNaN(gp) ? 0 : gp;

//    TSL tsl = new TSL(rgb.rgbColorSpace);
    double[] tslValues = new double[3];
    tslValues[1] = Math.sqrt( (9. / 5) * (Maths.sqr(rp) + Maths.sqr(gp)));

    if (gp > 0) {
      tslValues[0] = Math.atan(rp / gp) / (2 * Math.PI) + 1. / 4;
    }
    else if (gp < 0) {
      tslValues[0] = Math.atan(rp / gp) / (2 * Math.PI) + 3. / 4;
    }
    else {
      tslValues[0] = 1. / 2;
    }

    tslValues[2] = 0.299 * rgb.R + 0.587 * rgb.G + 0.114 * rgb.B;
    return tslValues;
  }

  /*public final static TSL fromRGB(RGB rgb) {
    double r = rgb.getRGr();
    double g = rgb.getRGg();
    double rp = r - (1. / 3);
    double gp = g - (1. / 3);
    rp = Double.isNaN(rp) ? 0 : rp;
    gp = Double.isNaN(gp) ? 0 : gp;

    TSL tsl = new TSL(rgb.rgbColorSpace);
    tsl.S = Math.sqrt( (9. / 5) * (Maths.sqr(rp) + Maths.sqr(gp)));

    if (gp > 0) {
      tsl.T = Math.atan(rp / gp) / (2 * Math.PI) + 1. / 4;
    }
    else if (gp < 0) {
      tsl.T = Math.atan(rp / gp) / (2 * Math.PI) + 3. / 4;
    }
    else {
      tsl.T = 1. / 2;
    }

    tsl.L = 0.299 * rgb.R + 0.587 * rgb.G + 0.114 * rgb.B;

    return tsl;
  }*/

  public String[] getBandNames() {
    return new String[] {
        "T", "S", "L"};
  }

}
