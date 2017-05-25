package shu.cms.colorspace.independ;

import shu.cms.hvs.cam.*;
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
public final class LMS
    extends LMSBasis {
  public double L;
  public double M;
  public double S;

  public LMS(LMSConvertible convertible) {
    super(convertible.getLMSValues(), convertible.getCATType());
  }

  public LMS() {
  }

  public LMS(double L, double M, double S) {
    super(L, M, S);
  }

  public LMS(double L, double M, double S, CAMConst.CATType catType) {
    super(L, M, S, catType);
  }

  public LMS(double[] LMSValues) {
    super(LMSValues);
  }

  public LMS(CIEXYZ XYZ, CAMConst.CATType catType) {
    super(fromXYZValues(XYZ.getValues(), catType), catType);
  }

  public LMS(double[] LMSValues, CAMConst.CATType catType) {
    super(LMSValues, catType);
  }

  protected void _setValues(double[] values) {
    L = values[0];
    M = values[1];
    S = values[2];
  }

  protected final double[] _getValues(double[] values) {
    values[0] = L;
    values[1] = M;
    values[2] = S;
    return values;
  }

  public CIEXYZ toXYZ() {
    return toXYZ(this, catType);
  }

  public static final LMS fromXYZ(final CIEXYZ XYZ, CAMConst.CATType type) {
    double[] XYZValues = XYZ.getValues();
    double[] LMSValues = fromXYZValues(XYZValues, type);
    return new LMS(LMSValues, type);
  }

  public final static double[] fromXYZValues(final double[] XYZValues,
                                             CAMConst.CATType type) {
    double[][] XYZ2LMS = CAMConst.getXYZ2LMSMatrix(type);
    double[] LMSValues = DoubleArray.timesFast(XYZ2LMS, XYZValues);
    return LMSValues;
  }

  public final static double[] fromXYZValues(final double[] XYZValues,
                                             double[][] XYZ2LMS) {
    double[] LMSValues = DoubleArray.timesFast(XYZ2LMS, XYZValues);
    return LMSValues;
  }

  public static final CIEXYZ toXYZ(final LMS lms, CAMConst.CATType type) {
    double[] LMSValues = lms.getValues();
    double[] XYZValues = toXYZValues(LMSValues, type);
    return new CIEXYZ(XYZValues, lms.white);
  }

  /**
   *
   * @param LMSValues double[]
   * @param type CATType
   * @return double[]
   */
  public final static double[] toXYZValues(final double[] LMSValues,
                                           CAMConst.CATType type) {
    double[][] LMS2XYZ = CAMConst.getLMS2XYZMatrix(type);
    double[] XYZValues = DoubleArray.timesFast(LMS2XYZ, LMSValues);
    return XYZValues;
  }

  public final static double[] toXYZValues(final double[] LMSValues,
                                           double[][] LMS2XYZ) {
    double[] XYZValues = DoubleArray.timesFast(LMS2XYZ, LMSValues);
    return XYZValues;
  }

  public String[] getBandNames() {
    return new String[] {
        "L", "M", "S"};
  }
}
