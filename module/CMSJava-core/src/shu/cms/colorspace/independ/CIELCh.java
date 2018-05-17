package shu.cms.colorspace.independ;

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
public final class CIELCh
    extends DeviceIndependentSpace {
  public static final void fromLabValues(final double[][] LabValuesArray) {
    int size = LabValuesArray.length;

    for (int x = 0; x < size; x++) {
      double[] LChValues = CIELCh.fromLabValues(LabValuesArray[x]);
      System.arraycopy(LChValues, 0, LabValuesArray[x], 0, 3);
    }

  }

  /**
   *
   * @param LChValuesArray double[][]
   * @param whiteXYZValues double[]
   * @todo M 用多執行緒處理
   */
  public static final void LChab2XYZValues(final double[][] LChValuesArray,
                                           final double[] whiteXYZValues) {
    int size = LChValuesArray.length;
    for (int x = 0; x < size; x++) {
      double[] XYZValues = CIELCh.LChab2XYZValues(LChValuesArray[x],
                                                  whiteXYZValues);
      System.arraycopy(XYZValues, 0, LChValuesArray[x], 0, 3);
    }
  }

  /**
   *
   * @param XYZValuesArray double[][]
   * @param whiteXYZValues double[]
   * @todo M 用多執行緒處理
   */
  public final static void XYZ2LChabValues(final double[][] XYZValuesArray,
                                           final double[] whiteXYZValues) {

    int size = XYZValuesArray.length;

    for (int x = 0; x < size; x++) {
      double[] LChValues = CIELCh.XYZ2LChabValues(XYZValuesArray[x],
                                                  whiteXYZValues);
      System.arraycopy(LChValues, 0, XYZValuesArray[x], 0, 3);
    }
  }

  public static enum Style {
    Lab, Luv, IPT, CIECAM02, Unknow
  }

  protected Style style = Style.Unknow;
  ;
//  protected CIEXYZ white;

//  public CIEXYZ getWhite() {
//    return white;
//  }

  public double L;
  public double C;
  public double h;

  public final double[] getCartesianValues() {
    return polar2cartesianCoordinatesValues(this.getValues());
  }

  protected final double[] _getValues(double[] values) {
    values[0] = L;
    values[1] = C;
    values[2] = h;
    return values;
  }

  public CIELCh(LChConvertible convertible) {
    super(cartesian2polarCoordinatesValues(convertible.getValues()),
          convertible.getWhite());
    this.style = convertible.getStyle();
  }

  public CIELCh() {
    this.style = Style.Unknow;
  }

  public CIELCh(double L, double C, double h) {
    super(L, C, h);
  }

  public CIELCh(double L, double C, double h, Style type) {
    super(L, C, h);
    this.style = type;
  }

  public CIELCh(double[] LChValues) {
    super(LChValues);
  }

  public CIELCh(double[] LChValues, Style type) {
    super(LChValues);
    this.style = type;
  }

  protected void _setValues(double[] values) {
    L = values[0];
    C = values[1];
    h = values[2];
  }

  public Style getType() {
    return style;
  }

  public boolean isLegal() {
    return L >= 0 && L <= 100 && h >= 0 && h <= 360;
  }

  public CIEXYZ toXYZ() {
    throw new UnsupportedOperationException();
  }

  public double[] toXYZValues(XYZValuesRetriever retriever) {
    return retriever.getXYZValues(this.getValues());
  }

  public static final void toLabValues(final double[][] LChValuesArray) {
    int size = LChValuesArray.length;

    for (int x = 0; x < size; x++) {
      double[] LabValues = toLabValues(LChValuesArray[x]);
      System.arraycopy(LabValues, 0, LChValuesArray[x], 0, 3);
    }
  }

  public static final double[] fromLabValues(final double[] Lab) {
    return cartesian2polarCoordinatesValues(Lab);
  }

  public static final double[] XYZ2LChabValues(final double[] XYZ,
                                               final double[] whitePoint) {
    double[] LabValues = CIELab.fromXYZValues(XYZ, whitePoint);
    return fromLabValues(LabValues);
  }

  public static final double[] toLabValues(final double[] LChValues) {
    return polar2cartesianCoordinatesValues(LChValues);
  }

  public static final double[] LChab2XYZValues(final double[] LCh,
                                               final double[] whitePoint) {
    double[] LabValues = toLabValues(LCh);
    return CIELab.toXYZValues(LabValues, whitePoint);
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * XYZ與LCh互轉方式的定義
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static interface XYZValuesRetriever {
    public double[] getXYZValues(double[] LChValues);

    public double[] getLChValues(double[] XYZValues);
  }

  public void setWhite(CIEXYZ white) {
    this.white = white;
  }

  public double getSaturation() {
    return C / L;
  }

  public String[] getBandNames() {
    return new String[] {
        "L", "C", "h"};
  }

  public final static CIELCh getInstanceFromLab(CIEXYZ XYZ, CIEXYZ whiteXYZ) {
    CIELab Lab = new CIELab(XYZ, whiteXYZ);
    CIELCh LCh = new CIELCh(Lab);
    return LCh;
  }
}
