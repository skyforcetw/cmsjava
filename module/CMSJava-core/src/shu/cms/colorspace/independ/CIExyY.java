package shu.cms.colorspace.independ;

import shu.cms.*;
import shu.math.array.*;
import javax.vecmath.Point2d;
import shu.math.geometry.Geometry;
import shu.math.Maths;

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
public final class CIExyY
    extends DeviceIndependentSpace implements NormalizeYOperator {
  public double x;
  public double y;
  public double Y;

  public void normalize(NormalizeY normalizeY) {
    this.Y = normalizeY.normal;
    this.normalizeY = normalizeY;
  }

  public void normalizeY() {
    Y = NormalFactor;
    normalizeY = NormalizeY.Normal1;
  }

  public void normalizeY100() {
    normalizeY();
    normalize(NormalizeY.Normal100);
  }

  public void setNormalizeNot() {
    normalizeY = NormalizeY.Not;
  }

  protected NormalizeY normalizeY = NormalizeY.Not;

  protected final double[] _getValues(double[] values) {
    values[0] = x;
    values[1] = y;
    values[2] = Y;
    return values;
  }

  public final double[] getDeltaxy(CIExyY xyY) {
    double[] deltaxy = new double[2];
    deltaxy[0] = this.x - xyY.x;
    deltaxy[1] = this.y - xyY.y;
    return deltaxy;
  }

  public final double[] getDeltauvPrime(CIExyY xyY) {
    double[] uvp1 = this.getuvPrimeValues();
    double[] uvp2 = xyY.getuvPrimeValues();
    double[] duvp = DoubleArray.minus(uvp1, uvp2);
    return duvp;
  }

  public final double[] getDeltauv(CIExyY xyY) {
    double[] uvp1 = this.getuvValues();
    double[] uvp2 = xyY.getuvValues();
    double[] duvp = DoubleArray.minus(uvp1, uvp2);
    return duvp;
  }

  public void rationalize() {
    x = Double.isNaN(x) ? 0 : x;
    y = Double.isNaN(y) ? 0 : y;
  }

  public boolean isLegal() {
    return Y >= 0 && (x + y) <= 1;
  }

  public CIExyY() {
  }

  public final static CIExyY fromCCT2DIlluminant(int CCT) {
    return CorrelatedColorTemperature.CCT2DIlluminantxyY(CCT);
  }

  public final static CIExyY fromCCT2Blackbody(int CCT) {
    return CorrelatedColorTemperature.CCT2BlackbodyxyY(CCT);
  }

  public double getCCT() {
    return this.toXYZ().getCCT();
  }

  public CIExyY(CIEXYZ XYZ) {
    double[] xyValues = XYZ.getxyValues();
    this.setValues(xyValues[0], xyValues[1], XYZ.Y);
    this.normalizeY = XYZ.normalizeY;
  }

  public CIExyY(double x, double y, double Y) {
    super(x, y, Y);
  }

  public CIExyY(double x, double y) {
    super(x, y, 1);
  }

  public CIExyY(double x, double y, double Y, NormalizeY normalizeY) {
    super(x, y, Y);
    this.normalizeY = normalizeY;
  }

  public CIExyY(double[] xyYValues) {
    super(xyYValues);
  }

  public CIExyY(double[] xyYValues, NormalizeY normalizeY) {
    super(xyYValues);
    this.normalizeY = normalizeY;
  }

  protected void _setValues(double[] values) {
    x = values[0];
    y = values[1];
    Y = values[2];
  }

  public final double[] getxyValues() {
    return new double[] {
        x, y};
  }

  public final double[] getuvValues() {
    double denominator = ( -2 * x + 12 * y + 3);
    double u = 4 * x / denominator;
    double v = 6 * y / denominator;
    return new double[] {
        u, v};
  }

  /**
   *
   * @param uvPrimeValues double[]
   */
  public final void setuvPrimeValues(double[] uvPrimeValues) {
    double u = uvPrimeValues[0];
    double v = uvPrimeValues[1];
    double denominator = 9 * u / 2 - 12 * v + 9;

    x = (27 * u / 4) / denominator;
    y = 3 * v / denominator;
  }

  public final void setuvPrimeYValues(double[] uvPrimeYValues) {
    this.setuvPrimeValues(uvPrimeYValues);
    this.Y = uvPrimeYValues[2];
  }

  public final void setuvValues(double[] uvValues) {
    double u = uvValues[0];
    double v = uvValues[1];
    double denominator = 2 * u - 8 * v + 4;

    x = 3 * u / denominator;
    y = 2 * u / denominator;
  }

  public final void setuvYValues(double[] uvYValues) {
    setuvValues(uvYValues);
    Y = uvYValues[2];
  }

  public final double[] getuvPrimeYValues() {
    double[] uvp = getuvPrimeValues();
    double[] uvpY = new double[] {
        uvp[0], uvp[1], Y};
    return uvpY;
  }

  public final double[] getuvPrimeValues() {
    double denominator = ( -2 * x + 12 * y + 3);
    double u = 4 * x / denominator;
    double v = 9 * y / denominator;
    return new double[] {
        u, v};
  }

  public static void main(String[] args) {
    CIExyY d65 = new CIExyY(Illuminant.getD65WhitePoint());
//    CIExyY stock = new CIExyY(0.3112071380058744, 0.3286580473219368,
//                              194.1427307128906);
//    CIExyY sky = new CIExyY(0.3078692596572398, 0.3309664292264988,
//                            203.8674926757812);
//    double[] dstock = d65.getDeltauvPrime(stock);
//    double[] dsky = d65.getDeltauvPrime(sky);
//    System.out.println(Math.sqrt(Maths.sqr(dstock[0]) + Maths.sqr(dstock[1])));
//    System.out.println(Math.sqrt(Maths.sqr(dsky[0]) + Maths.sqr(dsky[1])));
    CIExyY a = new CIExyY(0.299072716, 0.318057306, 1);
    CIExyY b = new CIExyY(0.309284717, 0.33547619, 1);
    System.out.println("D65 xyY: " + d65);
    double[] duvpa = d65.getDeltauvPrime(a);
    double[] dxya = d65.getDeltaxy(a);
    double[] duvpb = d65.getDeltauvPrime(b);
    double[] dxyb = d65.getDeltaxy(b);

    System.out.println(Math.sqrt(Maths.sqr(duvpa[0]) + Maths.sqr(duvpa[1])));
    System.out.println(Math.sqrt(Maths.sqr(dxya[0]) + Maths.sqr(dxya[1])));
    System.out.println(Math.sqrt(Maths.sqr(duvpb[0]) + Maths.sqr(duvpb[1])));
    System.out.println(Math.sqrt(Maths.sqr(dxyb[0]) + Maths.sqr(dxyb[1])));
  }

  public CIEXYZ toXYZ() {
    return toXYZ(this);
  }

  public final static CIExyY[] toxyYArray(CIEXYZ[] XYZArray) {
    int size = XYZArray.length;
    CIExyY[] xyYArray = new CIExyY[size];
    for (int x = 0; x < size; x++) {
      xyYArray[x] = new CIExyY(XYZArray[x]);
    }
    return xyYArray;
  }

  public final static CIEXYZ[] toXYZArray(CIExyY[] xyYArray) {
    int size = xyYArray.length;
    CIEXYZ[] XYZArray = new CIEXYZ[size];
    for (int x = 0; x < size; x++) {
      XYZArray[x] = xyYArray[x].toXYZ();
    }
    return XYZArray;
  }

  public static final CIExyY fromXYZ(final CIEXYZ XYZ) {
    CIExyY xyY = new CIExyY(XYZ);
    return xyY;
//    double[] xyValues = XYZ.getxyValues();
//    CIExyY xyY = new CIExyY(xyValues[0], xyValues[1], XYZ.Y, XYZ.normalizeY);
//    return xyY;
  }

  public static final CIEXYZ toXYZ(final CIExyY xyY) {
    CIEXYZ XYZ = new CIEXYZ();

    XYZ.X = (xyY.x / xyY.y) * xyY.Y;
    XYZ.Y = xyY.Y;
    XYZ.Z = ( (1 - xyY.x - xyY.y) / xyY.y) * xyY.Y;
    XYZ.normalizeY = xyY.normalizeY;
    XYZ.degree = xyY.degree;
    return XYZ;
  }

  protected final static CIExyY D65xyY = new CIExyY(Illuminant.D65WhitePoint);

  /**
   * for 65
   * @return double[] W: 白度,Tw: 淡色調
   */
  public double[] getWhitenessIndex() {
    double[] dxy = D65xyY.getDeltaxy(this);
    double W = Y + 800 * dxy[0] + 1700 * dxy[1];
    double Tw = 1000 * dxy[0] - 650 * dxy[1];

    return new double[] {
        W, Tw};
  }

  public double getSaturation(CIExyY referenceWhite) {
    Point2d whitexyPoint = new Point2d(referenceWhite.getxyValues());
    Point2d thisPoint = new Point2d(this.getxyValues());
    return Geometry.getDistance(thisPoint, whitexyPoint);
  }

  public String[] getBandNames() {
    return new String[] {
        "x", "y", "Y"};
  }
}
