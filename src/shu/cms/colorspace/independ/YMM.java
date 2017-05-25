package shu.cms.colorspace.independ;

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
public class YMM
    extends DeviceIndependentSpace {

  public double Y;
  public double M1;
  public double M2;

  protected YMM() {
    super();
  }

  protected YMM(CIEXYZ white) {
    super(white);
  }

  protected YMM(double[] values, CIEXYZ white) {
    super(values, white);
  }

  protected YMM(double[] values) {
    super(values);
  }

  protected YMM(double value1, double value2, double value3) {
    super(value1, value2, value3);
  }

  protected YMM(double value1, double value2, double value3, CIEXYZ white) {
    super(value1, value2, value3, white);
  }

  protected YMM(double value1, double value2, double value3, CIEXYZ white,
                CIEXYZ originalWhite) {
    super(value1, value2, value3, white, originalWhite);
  }

  protected YMM(double[] values, CIEXYZ white, CIEXYZ originalWhite,
                boolean adaptedToD65) {
    super(values, white, originalWhite, adaptedToD65);
  }

  /**
   * _getValues
   *
   * @param values double[]
   * @return double[]
   * @todo Implement this shu.cms.colorspace.ColorSpace method
   */
  protected double[] _getValues(double[] values) {
    return null;
  }

  /**
   * _setValues
   *
   * @param values double[]
   * @todo Implement this shu.cms.colorspace.ColorSpace method
   */
  protected void _setValues(double[] values) {
  }

  /**
   * getBandNames
   *
   * @return String[]
   */
  public String[] getBandNames() {
    return new String[] {
        "Y", "M1", "M2"};
  }

  /**
   * toXYZ
   *
   * @return CIEXYZ
   * @todo Implement this shu.cms.colorspace.independ.DeviceIndependentSpace
   *   method
   */
  public CIEXYZ toXYZ() {
    throw new UnsupportedOperationException();
  }

  public static void main(String[] args) {
    YMM ymm = new YMM();
  }

  public static final CIEXYZ toXYZ(final YMM ymm) {
    double[] ymmValues = ymm.getValues();
    double[] XYZValues = toXYZValues(ymmValues);
    CIEXYZ XYZ = new CIEXYZ(XYZValues, ymm.white);
    return null;
  }

  public final static double[] toXYZValues(final double[] ymmValues) {
    double[] XYZValues = new double[3];
    XYZValues[1] = ymmValues[0];
    XYZValues[0] = ymmValues[1] + ymmValues[0];
    XYZValues[2] = - (ymmValues[2] / 0.4) - ymmValues[0];
    return XYZValues;
  }

  public static final double[] fromXYZValues(final double[] XYZValues) {
    double[] ymmValues = new double[3];
    ymmValues[0] = XYZValues[1];
    ymmValues[1] = XYZValues[0] - XYZValues[1];
    ymmValues[2] = 0.4 * (XYZValues[1] - XYZValues[2]);
    return ymmValues;
  }

}
