package shu.cms.colorspace.independ;

import shu.cms.*;
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
public class IPT
    extends LMSBasis implements LChConvertible, LMSConvertible {

  public static enum Scale {
    CIELab, IPT
  }

  public double I;
  public double P;
  public double T;

  protected Scale scale = Scale.IPT;

  public IPT() {
  }

  public IPT(CIELCh LCh) {
    super(LCh.getCartesianValues(), LCh.white, CAMConst.CATType.IPT);
  }

  /**
   *
   * @param XYZ CIEXYZ
   */
  public IPT(CIEXYZ XYZ) {
    super(fromXYZValues(XYZ.getValues()), Illuminant.D65WhitePoint,
          XYZ.originalWhite, XYZ.adaptedToD65, CAMConst.CATType.IPT);
  }

  public IPT(LMS lms) {
    super(fromLMSValues(lms.getValues()), lms.white, lms.getCATType());
  }

  public IPT(double I, double P, double T) {
    super(I, P, T, Illuminant.D65WhitePoint, Illuminant.D65WhitePoint,
          CAMConst.CATType.IPT);
  }

  public IPT(double[] iptValues) {
    super(iptValues, Illuminant.D65WhitePoint, CAMConst.CATType.IPT);
  }

  public IPT(double[] iptValues, Scale scale) {
    super(iptValues, Illuminant.D65WhitePoint, CAMConst.CATType.IPT);
    this.scale = scale;
  }

  public IPT(double[] iptValues, Scale scale, CAMConst.CATType catType) {
    super(iptValues, Illuminant.D65WhitePoint, catType);
    this.scale = scale;
  }

  protected double[] _getValues(double[] values) {
    values[0] = I;
    values[1] = P;
    values[2] = T;
    return values;
  }

  public double[] getValues(double[] values, Scale scale) {
    values = getValues(values);
    if (scale != this.scale) {
      switch (scale) {
        case IPT:
          recoverCIELabScale(values);
          break;
        case CIELab:
          scaleToCIELab(values);
          break;
      }
    }
    return values;
  }

  protected void _setValues(double[] values) {
    I = values[0];
    P = values[1];
    T = values[2];
  }

  public void setScale(Scale scale) {
    if (scale != this.scale) {
      switch (scale) {
        case IPT:
          recoverCIELabScale();
          break;
        case CIELab:
          scaleToCIELab();
          break;
      }
//      this.scale = scale;
    }
  }

  /**
   * 將原本的IPT尺度轉成Lab的尺度
   */
  public final void scaleToCIELab() {
    I *= 100.;
    P *= 150.;
    T *= 150.;
    this.scale = Scale.CIELab;
  }

  /**
   * 將iptValues原本的IPT尺度轉成Lab的尺度
   * @param iptValues double[]
   * @return double[]
   */
  protected final static double[] scaleToCIELab(double[] iptValues) {
    iptValues[0] *= 100.;
    iptValues[1] *= 150.;
    iptValues[2] *= 150.;
    return iptValues;
  }

  /**
   * 將Lab尺度的IPT轉回IPT原本的尺度
   */
  public final void recoverCIELabScale() {
    I /= 100.;
    P /= 150.;
    T /= 150.;
    this.scale = Scale.IPT;
  }

  /**
   * 將Lab尺度的iptValues轉回IPT原本的尺度
   * @param iptValues double[]
   * @return double[]
   */
  protected final static double[] recoverCIELabScale(double[] iptValues) {
    iptValues[0] /= 100.;
    iptValues[1] /= 150.;
    iptValues[2] /= 150.;
    return iptValues;
  }

  public static void main(String[] args) {
    CIEXYZ XYZ1 = new CIEXYZ(0.2, 0.5, 0.3);
    CIEXYZ XYZ2 = new CIEXYZ(0.1, 0.5, 0.5);

//    System.out.println(new IPT(XYZ1) + " " + new IPT(XYZ2));
    CIEXYZ XYZ = Illuminant.D65WhitePoint;
    XYZ = (CIEXYZ) XYZ.clone();
    XYZ.normalizeY();

    System.out.println(XYZ);
    IPT ipt = new IPT(XYZ);
    System.out.println(ipt);
    ipt.scaleToCIELab();
    System.out.println(ipt);
  }

  public CIEXYZ toXYZ() {
    return toXYZ(this);
  }

  public final static CIEXYZ toXYZ(final IPT ipt) {
    double[] XYZValues = toXYZValues(ipt.getValues());
    CIEXYZ XYZ = new CIEXYZ(XYZValues, ipt.white, ipt.originalWhite,
                            NormalizeY.Normal1);
    return XYZ;
  }

  /**
   *
   * @param IPTValuesArray double[][]
   * @param recoverCIELabScale boolean
   * @todo M 用多執行緒處理
   */
  public static final void toXYZValues(final double[][] IPTValuesArray,
                                       boolean recoverCIELabScale) {
    int size = IPTValuesArray.length;
//    IPT ipt = new IPT();

    for (int x = 0; x < size; x++) {
      if (recoverCIELabScale) {
//        ipt.setValues(IPTValuesArray[x]);
//        ipt.recoverCIELabScale();
//        ipt.getValues(IPTValuesArray[x]);
        IPTValuesArray[x] = IPT.recoverCIELabScale(IPTValuesArray[x]);
      }
      double[] XYZValues = toXYZValues(IPTValuesArray[x]);
      System.arraycopy(XYZValues, 0, IPTValuesArray[x], 0, 3);
    }
  }

  /**
   *
   * @param IPTValues double[]
   * @return double[]
   */
  public final static double[] toXYZValues(final double[] IPTValues) {
    double[] LMSValues = toLMSValues(IPTValues);
    double[][] LMS2XYZ = CAMConst.getLMS2XYZMatrix(CAMConst.CATType.IPT);
    double[] XYZ = DoubleArray.timesFast(LMS2XYZ, LMSValues);
    return XYZ;
  }

  /**
   * IPTValues轉回到XYZValues後, 再以catType的色適應轉換到whitePoint下的XYZValues
   * @param IPTValues double[]
   * @param whiteValues double[]
   * @param catType CATType
   * @return double[]
   */
  public final static double[] toXYZValues(final double[] IPTValues,
                                           final double[] whiteValues,
                                           CAMConst.CATType catType) {
    double[] D65XYZValues = toXYZValues(IPTValues);
    double[][] adaptationMarix = ChromaticAdaptation.getAdaptationMatrix(
        Illuminant.D65WhitePoint,
        new CIEXYZ(whiteValues), catType);
    double[] XYZValues = ChromaticAdaptation.adaptation(D65XYZValues,
        adaptationMarix);

    return XYZValues;
  }

  public final static IPT fromXYZ(final CIEXYZ XYZ) {
    CIEXYZ D65XYZ = XYZ.getXYZAdaptedToD65();
    IPT ipt = new IPT(D65XYZ);
    return ipt;
  }

  /**
   *
   * @param XYZValuesArray double[][]
   * @param scaleToCIELab boolean
   * @todo M 用多執行緒處理
   */
  public final static void fromXYZValues(final double[][] XYZValuesArray,
                                         boolean scaleToCIELab) {
    int size = XYZValuesArray.length;
    IPT ipt = new IPT();

    for (int x = 0; x < size; x++) {
      double[] IPTValues = fromXYZValues(XYZValuesArray[x]);
      if (scaleToCIELab) {
//        ipt.setValues(IPTValues);
//        ipt.scaleToCIELab();
//        ipt.getValues(IPTValues);
        IPTValues = IPT.scaleToCIELab(IPTValues);
      }
      System.arraycopy(IPTValues, 0, XYZValuesArray[x], 0, 3);
    }
  }

  /**
   *
   * @param XYZValues double[] D65下的XYZValues
   * @return double[]
   */
  public final static double[] fromXYZValues(final double[] XYZValues) {
    double[][] XYZ2LMS = CAMConst.getXYZ2LMSMatrix(CAMConst.CATType.IPT);
    double[] LMSValues = DoubleArray.timesFast(XYZ2LMS, XYZValues);
    double[] IPTValues = fromLMSValues(LMSValues);
    return IPTValues;
  }

  /**
   * 會先將XYZValues以catType的色適應轉換到D65, 再進行IPT Values的運算
   * @param XYZValues double[]
   * @param whiteXYZValues double[] XYZValues的白點
   * @return double[]
   */
  public final static double[] fromXYZValues(final double[] XYZValues,
                                             final double[] whiteXYZValues) {
    double[] D65XYZValues = getD65XYZValues(XYZValues, whiteXYZValues);
    double[] IPTValues = fromXYZValues(D65XYZValues);
    return IPTValues;
  }

  public final static IPT fromXYZ(final CIEXYZ XYZ, final CIEXYZ white) {
    CIEXYZ D65XYZ = XYZ.getXYZAdaptedToD65(white);
    IPT ipt = IPT.fromXYZ(D65XYZ);
    return ipt;
  }

  public static interface IPTConst {

    double[][] M_LMS2IPT = new double[][] {
        {
        0.4000, 0.4000, 0.2000}, {
        4.4550, -4.8510, 0.3960}, {
        0.8056, 0.3572, -1.1628}
    };
    double[][] M_IPT2LMS = DoubleArray.inverse(M_LMS2IPT);

  }

  public final static double[] fromLMSValues(final double[] LMSValues) {
    double[] _LMSValues = new double[3];
    for (int x = 0; x < 3; x++) {
      if (LMSValues[x] >= 0) {
        _LMSValues[x] = Math.pow(LMSValues[x], 0.43);
      }
      else {
        _LMSValues[x] = -Math.pow( -LMSValues[x], 0.43);
      }
    }

    double[] IPT = DoubleArray.timesFast(IPTConst.M_LMS2IPT, _LMSValues);
    return IPT;
  }

  public final static double[] toLMSValues(final double[] IPTValues) {
    double[] _LMSValues = DoubleArray.timesFast(IPTConst.M_IPT2LMS, IPTValues);

    for (int x = 0; x < 3; x++) {
      if (_LMSValues[x] >= 0) {
        _LMSValues[x] = Math.pow(_LMSValues[x], 1. / 0.43);
      }
      else {
        _LMSValues[x] = -Math.pow( -_LMSValues[x], 1. / 0.43);
      }
    }

    return _LMSValues;
  }

  public CIELCh.Style getStyle() {
    return CIELCh.Style.IPT;
  }

  public double[] getLMSValues() {
    return toLMSValues(this.getValues());
  }

  public String[] getBandNames() {
    return new String[] {
        "I", "P", "T"};
  }
}
