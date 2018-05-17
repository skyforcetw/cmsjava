package shu.cms.colorspace.independ;

import shu.cms.*;
import shu.cms.hvs.cam.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * S-CIELAB所使用的OPP對立色空間
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class OPP
    extends LMSBasis implements LMSConvertible {
  public double WB;
  public double RG;
  public double BY;

  public OPP() {
  }

  public OPP(CIEXYZ XYZ) {
    super(fromXYZValues(XYZ.getValues()));
  }

  public OPP(double O, double P1, double P2) {
    super(O, P1, P2);
  }

  public OPP(LMS lms) {
    super(fromLMSValues(lms.getValues()), lms.white, lms.getCATType());
  }

  public OPP(double[] OPPValues) {
    super(OPPValues);
  }

  public OPP(double[] OPPValues, CAMConst.CATType catType) {
    super(OPPValues, catType);
  }

  protected void _setValues(double[] values) {
    WB = values[0];
    RG = values[1];
    BY = values[2];
  }

  protected final double[] _getValues(double[] values) {
    values[0] = WB;
    values[1] = RG;
    values[2] = BY;
    return values;
  }

  public CIEXYZ toXYZ() {
    return new CIEXYZ(toXYZValues(this.getValues()), this.white);
  }

  protected static interface OPPConst {
    /**
     * cone coordinate to opponent (Poirson & Wandell 1993)
     */
    double[][] M_LMS2OPP = new double[][] {
        {
        0.9900, -0.1060, -0.0940}, {
        -0.6690, 0.7420, -0.0270}, {
        -0.2120, -0.3540, 0.9110}
    };

    double[][] M_OPP2LMS = new Matrix(M_LMS2OPP).inverse().getArray();

    /**
     * from S-CIELab的matlab程式碼
     */
    double[][] M_XYZ2OPP = new double[][] {
        {
        0.2787336, .7218031, -.1065520}, {
        -.4487736, .2898056, .0771569}, {
        .0859513, -.5899859, .5011089}
    };

    double[][] M_OPP2XYZ = new Matrix(M_XYZ2OPP).inverse().getArray();
  }

  public static void main(String[] args) {
    CIEXYZ XYZ = Illuminant.E.getNormalizeXYZ();
    for (CAMConst.CATType type : CAMConst.CATType.values()) {
      LMS lms = new LMS(XYZ, type);
      System.out.println(type + " lms:" + lms);

      OPP opp = new OPP(XYZ);
      OPP opp2 = new OPP(lms);
      System.out.println(opp);
      System.out.println(opp2);
      System.out.println("");
    }
  }

  /**
   *
   * @param lms LMS
   * @return OPP
   */
  public final static OPP fromLMS(LMS lms) {
    double[] OPPValues = fromLMSValues(lms.getValues());
    return new OPP(OPPValues);
  }

  /**
   *
   * @param LMSValues double[]
   * @return double[]
   */
  public final static double[] fromLMSValues(double[] LMSValues) {
    double[] OPPValues = DoubleArray.timesFast(OPPConst.M_LMS2OPP, LMSValues);
    return OPPValues;
  }

  public final static OPP fromXYZ(final CIEXYZ XYZ) {
    OPP opp = new OPP(XYZ);
    return opp;
  }

  public final static LMS toLMS(OPP opp) {
    double[] LMSValues = toLMSValues(opp.getValues());
    return new LMS(LMSValues);
  }

  public final static double[] toLMSValues(double[] OPPValues) {
    double[] LMSValues = DoubleArray.timesFast(OPPConst.M_OPP2LMS, OPPValues);
    return LMSValues;
  }

  public final static double[] toXYZValues(double[] OPPValues) {
    double[] XYZValues = DoubleArray.timesFast(OPPConst.M_OPP2XYZ, OPPValues);
    return XYZValues;
  }

  public final static double[] fromXYZValues(double[] XYZValues) {
    double[] OPPValues = DoubleArray.timesFast(OPPConst.M_XYZ2OPP, XYZValues);
    return OPPValues;
  }

  /**
   * getLMSValues
   *
   * @return double[]
   */
  public double[] getLMSValues() {
    return toLMSValues(getValues());
  }

  public String[] getBandNames() {
    return new String[] {
        "WB", "RG", "BY"};
  }
}
