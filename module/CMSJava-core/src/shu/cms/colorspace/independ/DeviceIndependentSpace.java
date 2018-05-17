package shu.cms.colorspace.independ;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.*;
import shu.cms.hvs.cam.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 設備獨立色空間的公用函式
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class DeviceIndependentSpace
    extends ColorSpace {

  public static enum Degree {
    Two(2), Ten(10), Unknow( -1);

    Degree(int degree) {
      this.degree = degree;
    }

    public final static Degree getDegree(int degree) {
      for (Degree de : values()) {
        if (de.degree == degree) {
          return de;
        }
      }
      return null;
    }

    private int degree;
  }

  protected Degree degree = Degree.Unknow;
  public Degree getDegree() {
    return degree;
  }

  public void setDegree(Degree degree) {
    if (this.degree == Degree.Unknow) {
      this.degree = degree;
    }
    else {
      throw new IllegalStateException("Degree had assigned.");
    }
  }

  protected final int getNumberBands() {
    return 3;
  }

  protected DeviceIndependentSpace() {

  }

  protected DeviceIndependentSpace(CIEXYZ white) {
    this.white = white;
  }

  protected DeviceIndependentSpace(double[] values, CIEXYZ white) {
    this.setValues(values);

    if (null == white) {
      this.adaptedToD65 = true;
    }
    else {
      this.white = white;
    }
  }

  protected DeviceIndependentSpace(double[] values) {
    this.setValues(values);
  }

  public abstract CIEXYZ toXYZ();

  protected DeviceIndependentSpace(double value1, double value2, double value3) {
    this(new double[] {value1, value2, value3});
  }

  protected DeviceIndependentSpace(double value1, double value2, double value3,
                                   CIEXYZ white) {
    this(new double[] {value1, value2, value3}, white);
  }

  protected DeviceIndependentSpace(double value1, double value2, double value3,
                                   CIEXYZ white, CIEXYZ originalWhite) {
    this(new double[] {value1, value2, value3}, white, originalWhite,
         originalWhite != null);
  }

  protected DeviceIndependentSpace(double[] values, CIEXYZ white,
                                   CIEXYZ originalWhite, boolean adaptedToD65) {
    this.setValues(values);
    this.white = white;
    this.originalWhite = originalWhite;
    this.adaptedToD65 = adaptedToD65;
  }

  public final CIEXYZ getWhite() {
    return white;
  }

  protected CIEXYZ white;
  protected CIEXYZ originalWhite;
  protected boolean adaptedToD65 = false;
  public final boolean isAdaptedToD65() {
    return adaptedToD65;
  }

  public final static double ACTUAL_EPSILON = 0.008856;
  public final static double ACTUAL_KAPPA = 903.3;

  public final static double INTENT_EPSILON = 216.0 / 24389.0;
  public final static double INTENT_KAPPA = 24389.0 / 27.0;

  public static enum CIEStandard {
    //CIE提供的值
    ActualStandard,
    //修正CIE的值(會較準確)
    IntentStandard
  }

  protected static double epsilon = INTENT_EPSILON;
  protected static double kappa = INTENT_KAPPA;

  public static void setCIEStandard(CIEStandard standard) {
    if (standard == CIEStandard.ActualStandard) {
      epsilon = ACTUAL_EPSILON;
      kappa = ACTUAL_KAPPA;
    }
    else {
      epsilon = INTENT_EPSILON;
      kappa = INTENT_KAPPA;
    }
  }

  public static void main(String[] args) {
//    CIEXYZ wp = Illuminant.D50.getNormalizeXYZ();
//
//    CIEXYZ XYZ = new CIEXYZ(new double[] {Math.random(), Math.random(),
//                            Math.random()});
//    System.out.println("XYZ:" + XYZ);
//    IPT ipt = IPT.XYZ2IPT(XYZ);
//    System.out.println("IPT:" + ipt);
//    ipt.scaleToCIELab();
//    System.out.println("IPT_:" + ipt);
//
//    double[] LCh = CIELab.Lab2LChabValues(ipt.getValues());
//    System.out.println("LCh:" + DoubleArray.toString(LCh));
//    IPT ipt2 = new IPT(CIELCh.LChab2LabValues(LCh));
//
//    System.out.println("IPT2:" + ipt2);
//    ipt2.recoverCIELabScale();
//    System.out.println("IPT2_:" + ipt2);
//    CIEXYZ XYZ2 = IPT.IPT2XYZ(ipt2);
//    System.out.println("XYZ2:" + XYZ2);

//    LMS lms = XYZ.XYZ2LMS(XYZ, CAMConst.CATType.Bradford);
  }

  public static void example(String[] args) {
    //設定要轉換的XYZ
    CIEXYZ XYZ = new CIEXYZ(new double[] {.80, 1, .30});
    //設定白點
    CIEXYZ wp = new CIEXYZ(new double[] {.90, 1, .88});
    //從XYZ轉到Luv
    CIELuv luv = CIELuv.fromXYZ(XYZ, wp);
    //顯示
    System.out.println(luv);
    //從Luv轉回XYZ
    CIEXYZ XYZ2 = CIELuv.toXYZ(luv, wp);
    //顯示
    System.out.println(XYZ2);
  }

  /**
   * 儲存各種色適應轉換
   */
  protected final static Map<CIEXYZ, ChromaticAdaptation>
      chromaticAdaptationMap = new HashMap<CIEXYZ, ChromaticAdaptation> ();

  /**
   * 從D65XYZValues色適應到originalWhiteXYZValues為白點的XYZValues
   * @param D65XYZValues double[]
   * @param originalWhiteXYZValues double[]
   * @return double[]
   */
  protected final static double[] getXYZValuesFromD65(double[] D65XYZValues,
      double[] originalWhiteXYZValues) {
    ChromaticAdaptation ca = getChromaticAdaptationToD65(originalWhiteXYZValues);
    double[] XYZValues = ca.getSourceColor(D65XYZValues);
    return XYZValues;
  }

  /**
   * 從D65XYZValues色適應到originalWhiteXYZValues為白點的XYZValues
   * @param D65XYZValues double[]
   * @param originalWhiteXYZ CIEXYZ
   * @return double[]
   */
  protected final static double[] getXYZValuesFromD65(double[] D65XYZValues,
      CIEXYZ originalWhiteXYZ) {
    ChromaticAdaptation ca = getChromaticAdaptationToD65(originalWhiteXYZ);
    double[] XYZValues = ca.getSourceColor(D65XYZValues);
    return XYZValues;

  }

  /**
   * 將XYZValues以whiteXYZValues為白點, 色適應到D65下的XYZValues
   * @param XYZValues double[]
   * @param whiteXYZValues double[]
   * @return double[] D65下的XYZValues
   */
  protected final static double[] getD65XYZValues(double[] XYZValues,
                                                  double[] whiteXYZValues) {
    ChromaticAdaptation ca = getChromaticAdaptationToD65(whiteXYZValues);
    double[] D65XYZValues = ca.getDestinationColor(XYZValues);
    return D65XYZValues;
  }

  /**
   * 將XYZValues以whiteXYZValues為白點, 色適應到D65下的XYZValues
   * @param XYZValues double[]
   * @param whiteXYZ CIEXYZ
   * @return double[]
   */
  protected final static double[] getD65XYZValues(final double[] XYZValues,
                                                  final CIEXYZ whiteXYZ) {
    ChromaticAdaptation ca = getChromaticAdaptationToD65(whiteXYZ);
    double[] D65XYZValues = ca.getDestinationColor(XYZValues);
    return D65XYZValues;
  }

  /**
   * 從whiteXYZValues色適應到D65的色適應轉換
   * @param whiteXYZValues double[]
   * @return ChromaticAdaptation
   */
  protected final static ChromaticAdaptation getChromaticAdaptationToD65(double[]
      whiteXYZValues) {
    CIEXYZ whiteXYZ = new CIEXYZ(whiteXYZValues);
    if (chromaticAdaptationMap.containsKey(whiteXYZ)) {
      return chromaticAdaptationMap.get(whiteXYZ);
    }
    else {
      ChromaticAdaptation ca = new ChromaticAdaptation(whiteXYZ,
          Illuminant.D65WhitePoint, CAMConst.CATType.CAT02);
      chromaticAdaptationMap.put(whiteXYZ, ca);
      return ca;
    }
  }

  /**
   * 從whiteXYZValues色適應到D65的色適應轉換
   * @param whiteXYZ CIEXYZ
   * @return ChromaticAdaptation
   */
  protected final static ChromaticAdaptation getChromaticAdaptationToD65(CIEXYZ
      whiteXYZ) {
    if (chromaticAdaptationMap.containsKey(whiteXYZ)) {
      return chromaticAdaptationMap.get(whiteXYZ);
    }
    else {
      ChromaticAdaptation ca = new ChromaticAdaptation(whiteXYZ,
          Illuminant.D65WhitePoint, CAMConst.CATType.CAT02);
      chromaticAdaptationMap.put(whiteXYZ, ca);
      return ca;
    }
  }

}
