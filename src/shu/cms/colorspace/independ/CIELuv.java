package shu.cms.colorspace.independ;

import shu.cms.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 1976 CIE L*u*v* color space
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class CIELuv
    extends DeviceIndependentSpace implements LChConvertible {
  public double L;
  public double u;
  public double v;

  protected final double[] _getValues(double[] values) {
    values[0] = L;
    values[1] = u;
    values[2] = v;
    return values;
  }

  public CIELuv() {
  }

  public CIELuv(CIELCh LCh) {
    super(LCh.getCartesianValues(), LCh.white);
  }

  public CIELuv(double[] LuvValues, CIEXYZ white) {
    super(LuvValues, white);
  }

  public CIELCh.Style getStyle() {
    return CIELCh.Style.Luv;
  }

  public CIELuv(CIEXYZ XYZ, CIEXYZ white) {
    super(fromXYZValues(XYZ.getValues(), white.getValues()), white);
  }

  public CIELuv(double L, double u, double v) {
    super(L, u, v);
  }

  public CIELuv(double L, double u, double v, CIEXYZ white) {
    super(L, u, v, white);
  }

  public double[] getDeltauvStar(CIELuv Luv) {
    double[] deltauv = new double[2];
    deltauv[0] = u - Luv.u;
    deltauv[1] = v - Luv.v;
    return deltauv;
  }

  public CIELuv(double[] LuvValues) {
    super(LuvValues);
  }

  protected void _setValues(double[] values) {
    L = values[0];
    u = values[1];
    v = values[2];
  }

  public CIEXYZ toXYZ() {
    return toXYZ(this, this.white);
  }

  public static final CIEXYZ toXYZ(final CIELuv Luv, final CIEXYZ whitePoint) {
    CIEXYZ XYZ = new CIEXYZ();

    if (Luv.L > kappa * epsilon) {
      XYZ.Y = Math.pow( (Luv.L + 16) / 116, 3);
    }
    else {
      XYZ.Y = Luv.L / kappa;
    }

    double u0 = 4 * whitePoint.X /
        (whitePoint.X + 15 * whitePoint.Y + 3 * whitePoint.Z);
    double v0 = 9 * whitePoint.Y /
        (whitePoint.X + 15 * whitePoint.Y + 3 * whitePoint.Z);

    double a = (52 * Luv.L / (Luv.u + 13 * Luv.L * u0) - 1) / 3;
    double b = -5 * XYZ.Y;
    double c = - (1. / 3);
    double d = XYZ.Y * (39 * Luv.L / (Luv.v + 13 * Luv.L * v0) - 5);

    XYZ.X = (d - b) / (a - c);
    XYZ.Z = XYZ.X * a + b;
    XYZ.white = whitePoint;

    return XYZ;
  }

  public static void main(String[] args) {
    CIEXYZ white = Illuminant.D65WhitePoint;
    CIEXYZ XYZ = new CIEXYZ(.7, .5, .4, white);
    CIELuv Luv1 = fromXYZ(XYZ, white);
    System.out.println(Luv1);
    System.out.println(Luv1.getLuvAdaptedToD65());
  }

  public static final double[] fromXYZValues(final double[] XYZ,
                                             final double[] whitePoint) {

    double[] LuvValues = new double[3];
    double yr = XYZ[1] / whitePoint[1];
    if (yr > epsilon) {
      LuvValues[0] = 116 * Maths.cubeRoot(yr) - 16; ;
    }
    else {
      LuvValues[0] = kappa * yr;
    }
    double up = 4 * XYZ[0] / (XYZ[0] + 15 * XYZ[1] + 3 * XYZ[2]);
    double vp = 9 * XYZ[1] / (XYZ[0] + 15 * XYZ[1] + 3 * XYZ[2]);
    double urp = 4 * whitePoint[0] /
        (whitePoint[0] + 15 * whitePoint[1] + 3 * whitePoint[2]);
    double vrp = 9 * whitePoint[1] /
        (whitePoint[0] + 15 * whitePoint[1] + 3 * whitePoint[2]);

    LuvValues[1] = 13 * LuvValues[0] * (up - urp);
    LuvValues[2] = 13 * LuvValues[0] * (vp - vrp);

    return LuvValues;

  }

  /**
   * for Lu*v*
   * @param XYZ CIEXYZ
   * @param whitePoint CIEXYZ
   * @return CIELuv
   */
  public static final CIELuv fromXYZ(final CIEXYZ XYZ, final CIEXYZ whitePoint) {
    return new CIELuv(fromXYZValues(XYZ.getValues(), whitePoint.getValues()),
                      whitePoint);
  }

  public String[] getBandNames() {
    return new String[] {
        "L", "u", "v"};
  }

  public final CIELuv getLuvAdaptedToD65() {
    if (this.adaptedToD65 || this.white.equalsValues(Illuminant.D65WhitePoint)) {
      return this;
    }
    else {
      CIEXYZ XYZ = toXYZ();
      CIEXYZ D65XYZ = XYZ.getXYZAdaptedToD65();
      CIELuv D65Luv = new CIELuv(D65XYZ, Illuminant.D65WhitePoint);
      D65Luv.originalWhite = XYZ.white;
      D65Luv.adaptedToD65 = true;
      return D65Luv;
    }
  }

}
