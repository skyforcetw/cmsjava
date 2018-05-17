package shu.cms.colorspace.independ;

import shu.cms.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 1976 CIE L*a*b* color space
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class CIELab
    extends DeviceIndependentSpace implements LChConvertible {
  public double L;
  public double a;
  public double b;

  public CIELCh.Style getStyle() {
    return CIELCh.Style.Lab;
  }

  public static void main(String[] args) {
    CIELab lab = new CIELab(new CIEXYZ(229.990691, 100, 0.037977),
                            new CIEXYZ(100, 100, 100));

    CIEXYZ D50White = Illuminant.getD50WhitePoint();
    CIEXYZ D65White = Illuminant.getD65WhitePoint();
    CIELab Lab = new CIELab(60, 10, 20, D50White);

//    System.out.println(XYZ);
    System.out.println("Lab " + Lab);
    CIELab D65Lab = Lab.getLabAdaptedToD65();
    System.out.println("D65Lab " + D65Lab);

    CIEXYZ XYZ = Lab.toXYZ();
    CIEXYZ D65XYZ = XYZ.getXYZAdaptedToD65();
    D65XYZ.getXYZAdaptedToD65();
    System.out.println("XYZ " + XYZ);
    System.out.println("D65XYZ " + D65XYZ);
    CIELab D65Lab2 = new CIELab(D65XYZ, D65White);
    System.out.println("D65Lab2 " + D65Lab2);
  }

  public final CIELab getLabAdaptedToD65() {
    if (this.adaptedToD65 || this.white.equalsValues(Illuminant.D65WhitePoint)) {
      return this;
    }
    else {
      CIEXYZ XYZ = toXYZ();
      CIEXYZ D65XYZ = XYZ.getXYZAdaptedToD65();
      CIELab D65Lab = new CIELab(D65XYZ, Illuminant.D65WhitePoint);
      D65Lab.originalWhite = XYZ.white;
      D65Lab.adaptedToD65 = true;
      return D65Lab;
    }
  }

  public final double[] getabLValues() {
    double[] abLValues = new double[3];
    abLValues[0] = a;
    abLValues[1] = b;
    abLValues[2] = L;
    return abLValues;
  }
  public final double[] getabValues() {
    double[] abValues = new double[2];
    abValues[0] = a;
    abValues[1] = b;
    return abValues;
  }

  protected final double[] _getValues(double[] values) {
    values[0] = L;
    values[1] = a;
    values[2] = b;
    return values;
  }

  public CIELab() {
  }

  public CIELab(double L, double a, double b) {
    super(L, a, b, null);
  }

  public CIELab(double L, double a, double b, CIEXYZ white) {
    super(L, a, b, white);
  }

  public CIELab(double[] LabValues) {
    super(LabValues, null);
  }

  public CIELab(CIEXYZ XYZ, CIEXYZ white) {
    super(fromXYZValues(XYZ.getValues(), white.getValues()), white);
  }

  public CIELab(double[] LabValues, CIEXYZ white) {
    super(LabValues, white);
  }

  public CIELab(CIELCh LCh) {
    super(LCh.getCartesianValues(), LCh.white);
    this.adaptedToD65 = LCh.adaptedToD65;
  }

  protected void _setValues(double[] values) {
    L = values[0];
    a = values[1];
    b = values[2];
  }

  public double[] getDeltaab(CIELab Lab) {
    double[] dab = new double[] {
        a - Lab.a, b - Lab.b};
    return dab;
  }

  public boolean isLegal() {
    return L >= 0 && L <= 100 && a >= -128. && a <= 127. && b >= -128. &&
        b <= 127.;
  }

  /**
   * Lab的合理化,將Lab的L限制在0~100間.
   * a及b限制在-128~127之間.
   * @param LabValuesArray double[][]
   * @return int
   */
  public final static int rationalize(double[][] LabValuesArray) {
    int size = LabValuesArray.length;
    int rationalizeCount = 0;

    for (int x = 0; x < size; x++) {
      double[] LabValues = LabValuesArray[x];
      if (LabValues[0] < 0) {
        rationalizeCount++;
        LabValues[0] = 0;
      }
      if (LabValues[0] > 100.) {
        rationalizeCount++;
        LabValues[0] = 100.;
      }

      for (int c = 1; c < 3; c++) {
        if (LabValues[c] < -128.) {
          rationalizeCount++;
          LabValues[c] = -128.;
        }
        if (LabValues[c] > 127.) {
          rationalizeCount++;
          LabValues[c] = 127.;
        }
      }
    }
    return rationalizeCount;
  }

  public CIEXYZ toXYZ() {
    if (white == null) {
      return null;
    }
    else {
      return toXYZ(this, white);
    }
  }

  public static final CIEXYZ toXYZ(final CIELab lab, final CIEXYZ whitePoint) {
    CIEXYZ result = new CIEXYZ(toXYZValues(lab.getValues(),
                                           whitePoint.getValues()), whitePoint);
    result.normalizeY = whitePoint.normalizeY;
    return result;

  }

  public static final void toXYZValues(final double[][] LabValuesArray,
                                       double[] whitePoint) {
    int size = LabValuesArray.length;

    for (int x = 0; x < size; x++) {
      double[] XYZValues = toXYZValues(LabValuesArray[x], whitePoint);
      System.arraycopy(XYZValues, 0, LabValuesArray[x], 0, 3);
    }
  }

  public static final void fromXYZValues(final double[][] XYZValuesArray,
                                         double[] whitePoint) {
    int size = XYZValuesArray.length;

    for (int x = 0; x < size; x++) {
      double[] LabValues = fromXYZValues(XYZValuesArray[x], whitePoint);
      System.arraycopy(LabValues, 0, XYZValuesArray[x], 0, 3);
    }
  }

  public static final double[] toXYZValues(final double[] lab,
                                           final double[] whitePoint) {

    double[] r = new double[3];
    double[] f = new double[3];

    if (lab[0] > kappa * epsilon) {
      r[1] = Math.pow( ( (lab[0] + 16) / 116), 3);
    }
    else {
      r[1] = lab[0] / kappa;
    }
    if (r[1] > epsilon) {
      f[1] = (lab[0] + 16) / 116;
    }
    else {
      f[1] = (kappa * r[1] + 16) / 116;
    }
    f[0] = lab[1] / 500 + f[1];
    f[2] = f[1] - lab[2] / 200;
    if (Math.pow(f[0], 3) > epsilon) {
      r[0] = Math.pow(f[0], 3);
    }
    else {
      r[0] = (116 * f[0] - 16) / kappa;
    }

    if (Math.pow(f[2], 3) > epsilon) {
      r[2] = Math.pow(f[2], 3);
    }
    else {
      r[2] = (116 * f[2] - 16) / kappa;
    }
    double[] XYZValue = new double[] {
        r[0] * whitePoint[0], r[1] * whitePoint[1], r[2] * whitePoint[2]};
    return XYZValue;
  }

  public static final CIELab fromXYZ(final CIEXYZ XYZ, final CIEXYZ whitePoint) {
    return new CIELab(fromXYZValues(XYZ.getValues(), whitePoint.getValues()),
                      whitePoint);
  }

  public static final double[] fromXYZValues(final double[] XYZValues,
                                             final double[] whitePoint) {
    double[] r = new double[] {
        XYZValues[0] / whitePoint[0], XYZValues[1] / whitePoint[1],
        XYZValues[2] / whitePoint[2]};
    double[] f = new double[3];

    for (int i = 0; i < 3; i++) {
      if (r[i] > epsilon) {
        f[i] = Maths.cubeRoot(r[i]); // more precisse than return pow(t, 1.0/3.0);
      }
      else {
        f[i] = (kappa * r[i] + 16) / 116;
      }
    }

    double[] LabValues = new double[3];
    LabValues[0] = 116.0 * f[1] - 16;
    LabValues[1] = 500.0 * (f[0] - f[1]);
    LabValues[2] = 200.0 * (f[1] - f[2]);
    return LabValues;
  }

  public String[] getBandNames() {
    return new String[] {
        "L", "a*", "b*"};
  }

}
