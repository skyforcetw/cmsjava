package shu.cms.colorspace.independ;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.recover.*;
import shu.math.*;
import shu.math.array.*;

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
public final class CIEXYZ
    extends DeviceIndependentSpace implements NormalizeYOperator {

  public double X;
  public double Y;
  public double Z;

  public final CIEXYZ getXYZAdaptedFromD65(CIEXYZ originalWhite) {
    double[] orgXYZValues = getXYZValuesFromD65(this.getValues(),
                                                originalWhite);
    CIEXYZ XYZ = new CIEXYZ(orgXYZValues, originalWhite,
                            originalWhite.normalizeY);
    return XYZ;

  }

  public final CIEXYZ getXYZAdaptedFromD65() {
    if (adaptedToD65) {
      return getXYZAdaptedFromD65(this.originalWhite);
    }
    else {
      return null;
    }
  }

  public final CIEXYZ getXYZAdaptedToD65(final CIEXYZ white) {
    double[] D65XYZValues = getD65XYZValues(this.getValues(), white);
    CIEXYZ XYZ = new CIEXYZ(D65XYZValues, Illuminant.D65WhitePoint, white,
                            NormalizeY.Normal1);
    return XYZ;

  }

  public final CIEXYZ getXYZAdaptedToD65() {
    if (adaptedToD65) {
      return this;
    }
    else {
      if (white != null) {
        return getXYZAdaptedToD65(white);
      }
      else {
//        throw new IllegalStateException("white == null");
        return null;
      }
    }
  }

  public static enum CorrectMethod {
    Method1, Method2, Method3
  }

  private final static double[][] Matrix1 = new double[][] {
      {
      1, 0.07, -0.07}, {
      0, 1, 0}, {
      0, 0, 1}
  };
  private final static double[][] Matrix2 = new double[][] {
      {
      1, 0.14, -0.14}, {
      0, 1, 0}, {
      0, 0, 1}
  };
  private final static double[][] Matrix3 = new double[][] {
      {
      1, 0.21, -0.21}, {
      0, 1, 0}, {
      0, 0, 1}
  };

  private boolean corrected = false;
  public void correct(CorrectMethod method) {
    if (corrected) {
      return;
    }
    double[][] matrix = null;
    switch (method) {
      case Method1:
        matrix = Matrix1;
        break;
      case Method2:
        matrix = Matrix2;
        break;
      case Method3:
        matrix = Matrix3;
        break;
    }
    double[] XYZValues = getValues();
    double[] result = DoubleArray.times(matrix, XYZValues);
    setValues(result);
    corrected = true;
  }

  public double[] getxyValues() {
    if (Y == 0) {
      return new double[] {
          0, 0};
    }
    else {
      double sum = (X + Y + Z);
      double x = X / sum;
      double y = Y / sum;
      return new double[] {
          x, y};
    }
  }

  public double[] getxyzValues() {
    double sum = (X + Y + Z);
    double x = X / sum;
    double y = Y / sum;
    double z = 1 - x - y;
    return new double[] {
        x, y, z};
  }

  public double getCCT() {
    return CorrelatedColorTemperature.XYZ2CCTByRobertson(this);
  }

  /**
   * Yellowness Index per ASTM MEthod E313.
   * Yellowness Index may only  be calculated  for D65 at two degree view angle.
   * @return double
   */
  public double getYellownessIndex() {
    return 100 * (1.2985 * X - 1.1335 * Z) / Y;
  }

  public double[] getWhitenessIndex() {
    return new CIExyY(this).getWhitenessIndex();
  }

  public double[] getuvPrimeValues() {
    CIExyY xyY = CIExyY.fromXYZ(this);
    return xyY.getuvPrimeValues();
  }

  public double[] getuvValues() {
    CIExyY xyY = CIExyY.fromXYZ(this);
    return xyY.getuvValues();
  }

  public double getPowerByXYZ() {
    return X + Y + Z;
  }

  public final static CIEXYZ plus(CIEXYZ XYZ1, CIEXYZ XYZ2) {
    CIEXYZ result = new CIEXYZ();
    result.X = XYZ1.X + XYZ2.X;
    result.Y = XYZ1.Y + XYZ2.Y;
    result.Z = XYZ1.Z + XYZ2.Z;
    return result;
  }

  public final static CIEXYZ minus(CIEXYZ XYZ1, CIEXYZ XYZ2) {
    CIEXYZ result = new CIEXYZ();
    result.X = XYZ1.X - XYZ2.X;
    result.Y = XYZ1.Y - XYZ2.Y;
    result.Z = XYZ1.Z - XYZ2.Z;
    return result;
  }

  protected double[] _getValues(double[] values) {
    values[0] = X;
    values[1] = Y;
    values[2] = Z;
    return values;
  }

  public CIEXYZ() {
  }

  public CIEXYZ(double X, double Y, double Z, CIEXYZ white) {
    super(X, Y, Z, white);
  }

  public CIEXYZ(double X, double Y, double Z) {
    super(X, Y, Z);
  }

  public CIEXYZ(double X, double Y, double Z, NormalizeY normalizeY) {
    super(X, Y, Z);
    this.normalizeY = normalizeY;
  }

  public CIEXYZ(double[] XYZValues) {
    super(XYZValues);
  }

  public CIEXYZ(double[] XYZValues, CIEXYZ white, NormalizeY normalizeY) {
    super(XYZValues, white);
    this.normalizeY = normalizeY;
  }

  public CIEXYZ(double[] XYZValues, CIEXYZ white) {
    super(XYZValues, white);
  }

  protected CIEXYZ(double[] XYZValues, CIEXYZ white, CIEXYZ originalWhite,
                   NormalizeY normalizeY) {
    super(XYZValues, white, originalWhite, originalWhite != null);
    this.normalizeY = normalizeY;
  }

  public CIEXYZ(double[] XYZValues, NormalizeY normalizeY) {
    super(XYZValues);
    this.normalizeY = normalizeY;
  }

  /**
   * 調整到跟scale一樣(以Y為主)
   * @param scale CIEXYZ
   */
  public void scaleY(CIEXYZ scale) {
    scaleY(scale.Y);
  }

  /**
   * 將Y調整成scaleY
   * @param scaleY double
   */
  public void scaleY(double scaleY) {
    double factor = scaleY / Y;
    times(factor);
  }

  /**
   * 將XYZ都乘上factor
   * @param factor double
   */
  public void times(double factor) {
    times(factor, true);
  }

  public CIEXYZ timesAndReturn(double factor) {
    return timesAndReturn(factor, true);
  }

  public CIEXYZ timesAndReturn(double factor, boolean setNormalizeNot) {
    CIEXYZ result = (CIEXYZ)this.clone();
    result.times(factor, setNormalizeNot);
    return result;
  }

  public void times(double factor, boolean setNormalizeNot) {
    X *= factor;
    Y *= factor;
    Z *= factor;
    if (setNormalizeNot) {
      this.setNormalizeNot();
    }
  }

  public final static double[][] times(double[][] XYZValuesArray,
                                       double[] maxXYZValues,
                                       double normal) {
    int size = XYZValuesArray.length;
    double scale = normal / maxXYZValues[1];
    CIEXYZ XYZ = new CIEXYZ();
    for (int x = 0; x < size; x++) {
      double[] XYZValues = XYZValuesArray[x];
      XYZ.setValues(XYZValues);
      XYZ.times(scale);
      XYZ.getValues(XYZValues);
    }
    return XYZValuesArray;
  }

  /**
   * 針對normal當做1作正規化
   * @param normal CIEXYZ
   */
  public void normalize(CIEXYZ normal) {
    double factor = (NormalFactor / normal.Y);
    X *= factor;
    Y *= factor;
    Z *= factor;
    normalizeY = NormalizeY.Normal1;
  }

  public void normalizeWhite() {
    if (this.white == null) {
      throw new IllegalStateException("this.white == null");
    }
    normalize(white);
  }

//  protected final static double NormalFactor = 1;

  /**
   * 以Y為主作normalize
   */
  public void normalizeY() {
    double factor = NormalFactor / Y;
    X *= factor;
    Z *= factor;
    Y = NormalFactor;
    normalizeY = NormalizeY.Normal1;
  }

  /**
   * 取消normalize的標記
   */
  public void setNormalizeNot() {
    normalizeY = NormalizeY.Not;
  }

  public void normalizeY100() {
    normalizeY();
    normalize(NormalizeY.Normal100);
  }

  protected void _setValues(double[] values) {
    X = values[0];
    Y = values[1];
    Z = values[2];
  }

  public static void main(String[] args) {
    CIEXYZ D50White = Illuminant.getD50WhitePoint();
    double[] xyValues = D50White.getxyValues();
//    System.out.println(D50White);
//    CIExyY xyY = new CIExyY(D50White);
//    xyY.Y = 10;
//    System.out.println(xyY.toXYZ());
//    D50White.scaleY(10);
//    System.out.println(D50White);
  }

  /**
   * 將XYZ值合理化
   * @param XYZValuesArray double[][]
   * @return int 合理化的次數
   */
  public final static int rationalize(double[][] XYZValuesArray) {
    int size = XYZValuesArray.length;
    int rationalizeCount = 0;

    for (int x = 0; x < size; x++) {
      double[] XYZ = XYZValuesArray[x];

      for (int c = 0; c < 3; c++) {
        if (XYZ[c] < 0) {
          rationalizeCount++;
          XYZ[c] = 0;
        }
      }
    }
    return rationalizeCount;
  }

  public boolean isBlack() {
    return X == 0 && Y == 0 && Z == 0;
  }

  /**
   * 判斷該XYZ值是否合理,判斷依據:
   * 1.Y=0的話,其他X,Z也該為0
   * 2.Y!=0的話,X也應該!=0
   * 3.XYZ不該有負值
   * @return boolean
   */
  public boolean isLegal() {
    if (Y == 0 && (X > 0 || Z > 0)) {
      //如果Y=0,就應該為黑色,其他頻道不應該有值
      return false;
    }
    else if (Y != 0 && X == 0) {
      //如果Y有值(具有亮度),X頻道就也應該有值,Z則不一定
      return false;
    }
    else if (X < 0 || Y < 0 || Z < 0) {
      return false;
    }

    return true;
  }

  /**
   * 合理化,依據是:
   * 1.如果是NaN,就設定成0
   * 2.如果<0,就設定成0
   * 3.Y==0的話,X=0,Z=0
   * 4.Y!=0又X==0的話,XYZ都設為0
   */
  public void rationalize() {
    X = Double.isNaN(X) ? 0 : X;
    Y = Double.isNaN(Y) ? 0 : Y;
    Z = Double.isNaN(Z) ? 0 : Z;

    X = (X < 0) ? 0 : X;
    Y = (Y < 0) ? 0 : Y;
    Z = (Z < 0) ? 0 : Z;

    if (Y == 0. && (X > 0. || Z > 0.)) {
      X = 0;
      Z = 0;
    }
    if (Y != 0. && X == 0.) {
      X = 0;
      Y = 0;
      Z = 0;
    }
  }

  /**
   * 判斷該XYZ值是否合理,判斷依據除了與isLegal()相同外
   * 另外再判定該XYZ的值都分別該<= white
   * @param white CIEXYZ
   * @return boolean
   */
  public boolean isLegal(CIEXYZ white) {
    return isLegal() && X <= white.X && Y <= white.Y &&
        Z <= white.Z;
  }

  /**
   * 除了依照rationalize()的合理化以外,再參照white作合理化
   * @param white CIEXYZ
   */
  public void rationalize(CIEXYZ white) {
    rationalize();

    X = (X > white.X) ? white.X : X;
    Y = (Y > white.Y) ? white.Y : Y;
    Z = (Z > white.Z) ? white.Z : Z;
  }

  public double getSaturation(CIEXYZ white) {
    double[] uv = this.getuvPrimeValues();
    double[] uvn = white.getuvPrimeValues();
    return 13 * Math.sqrt(Maths.sqr(uv[0] - uvn[0]) + Maths.sqr(uv[1] - uvn[1]));
  }

  protected NormalizeY normalizeY = NormalizeY.Not;

  public NormalizeY getNormalizeY() {
    return normalizeY;
  }

  /**
   * 改變NormalizeY的值
   * @param normalizeY NormalizeY
   */
  public void normalize(NormalizeY normalizeY) {
    double[] values = getValues(new double[3], normalizeY);
    this.setValues(values);
    this.normalizeY = normalizeY;
  }

  public double[] getValues(double[] values, NormalizeY normalizeY) {
    if (this.normalizeY == NormalizeY.Not) {
      throw new IllegalStateException("this.normalizeY == NormalizeY.Not");
    }
    this.getValues(values);
    if (this.normalizeY == normalizeY) {
      return values;
    }
    switch (this.normalizeY) {
      case Normal100:
        DoubleArray.copy(DoubleArray.times(values, 1. / 100), values);
        break;
      case Normal1:
        DoubleArray.copy(DoubleArray.times(values, 100), values);
        break;
    }
    return values;
  }

  public CIEXYZ toXYZ() {
    return this;
  }

  private final static long serialVersionUID = 8683452581122892182L;

  private static SpectralCamera spectralCamera = null;

  /**
   * 用XYZ回推光譜後, 再重新積分出1964的XYZ, 僅供參考.
   * @return CIEXYZ
   */
  public final CIEXYZ toCIE1964XYZ() {
    if (this.normalizeY == NormalizeY.Not) {
      throw new IllegalStateException("this.normalizeY == NormalizeY.Not");
    }
    if (spectralCamera == null) {
      spectralCamera = new SpectralCamera(RGB.ColorSpace.WideGamutRGB);
    }
    RGB rgb = new RGB(RGB.ColorSpace.WideGamutRGB, this);
    Spectra s = spectralCamera.getSpectra(rgb);
    CIEXYZ CIE1964XYZ = spectralCamera.getCIE1964XYZ(s);
    return CIE1964XYZ;
  }

  public final static CIEXYZ[] getCIEXYZArray(double[][] XYZValues) {
    int size = XYZValues.length;
    CIEXYZ[] XYZArray = new CIEXYZ[size];
    for (int x = 0; x < size; x++) {
      XYZArray[x] = new CIEXYZ(XYZValues[x]);
    }
    return XYZArray;
  }

  public String[] getBandNames() {
    return new String[] {
        "X", "Y", "Z"};
  }
}
