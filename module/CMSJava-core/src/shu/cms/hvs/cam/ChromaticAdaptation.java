package shu.cms.hvs.cam;

import java.io.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 色適應轉換
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class ChromaticAdaptation
    implements Serializable {

  protected CIEXYZ sourceWhite;
  protected CIEXYZ destinationWhite;
  protected double[][] adaptationMatrixToDest;
  protected double[][] adaptationMatrixFromDest;
  /**
   * 預設的色適應公式
   */
  public final static CAMConst.CATType DEFAULT_CAT_TYPE = CAMConst.CATType.
      Bradford;

  /**
   * 計算deviceWhite到PCS(D50)的色適應物件
   * @param deviceWhite CIEXYZ
   * @param catType CATType
   * @return ChromaticAdaptation
   */
  public final static ChromaticAdaptation getInstanceAdaptToPCS(CIEXYZ
      deviceWhite, CAMConst.CATType catType) {
    ChromaticAdaptation ca = new ChromaticAdaptation(deviceWhite, PCS_WHITE,
        catType);
    return ca;
  }

  /**
   * 計算deviceWhite到D50的色適應物件
   * @param deviceWhite CIEXYZ
   * @param catType CATType
   * @return ChromaticAdaptation
   */
  public final static ChromaticAdaptation getInstanceAdaptToD65(CIEXYZ
      deviceWhite, CAMConst.CATType catType) {
    ChromaticAdaptation ca = new ChromaticAdaptation(deviceWhite, D65_WHITE,
        catType);
    return ca;
  }

  public ChromaticAdaptation(CIEXYZ sourceWhite, CIEXYZ destinationWhite,
                             CAMConst.CATType catType) {
    this.sourceWhite = sourceWhite;
    this.destinationWhite = destinationWhite;
    adaptationMatrixToDest = getAdaptationMatrix(sourceWhite, destinationWhite,
                                                 catType);
    adaptationMatrixFromDest = DoubleArray.inverse(adaptationMatrixToDest);
  }

  /**
   * 物件方法,須配合建構式使用
   * 將sourceXYZ轉到destXYZ
   * @param sourceXYZ CIEXYZ
   * @return CIEXYZ
   */
  public CIEXYZ getDestinationColor(CIEXYZ sourceXYZ) {
    double[] dest = getDestinationColor(sourceXYZ.getValues());
    CIEXYZ destXYZ = new CIEXYZ(dest, this.destinationWhite);
    return destXYZ;
  }

  public CIELab getDestinationColor(CIELab sourceLab) {
    CIEXYZ XYZ = sourceLab.toXYZ();
    CIEXYZ destinationXYZ = getDestinationColor(XYZ);
    CIELab destinationLab = new CIELab(destinationXYZ, destinationWhite);
    return destinationLab;
  }

  /**
   * 物件方法,須配合建構式使用
   * 將sourceXYZValues陣列轉換到destXYZValues陣列
   * @param sourceXYZValues double[]
   * @return double[]
   */
  public double[] getDestinationColor(double[] sourceXYZValues) {
    return adaptation(sourceXYZValues, adaptationMatrixToDest);
  }

  /**
   * 物件方法,須配合建構式使用
   * 將adaptedValues陣列轉換到sourceXYZValues陣列
   * @param adaptedXYZValues double[]
   * @return double[]
   */
  public double[] getSourceColor(double[] adaptedXYZValues) {
    return adaptation(adaptedXYZValues, adaptationMatrixFromDest);
  }

  /**
   * 物件方法,須配合建構式使用
   * 將adaptedXYZ轉到sourceXYZ
   * @param adaptedXYZ CIEXYZ
   * @return CIEXYZ
   */
  public CIEXYZ getSourceColor(CIEXYZ adaptedXYZ) {
    double[] source = getSourceColor(adaptedXYZ.getValues());
    CIEXYZ sourceXYZ = new CIEXYZ(source, this.sourceWhite);
    return sourceXYZ;
  }

  /**
   * 計算色適應矩陣
   * @param sourceWhite CIEXYZ
   * @param destinationWhite CIEXYZ
   * @param catType CATType
   * @return double[][]
   */
  public final static double[][] getAdaptationMatrix(CIEXYZ sourceWhite,
      CIEXYZ destinationWhite,
      CAMConst.CATType catType) {
    LMS srcWhite = LMS.fromXYZ(sourceWhite, catType);
    LMS destWhite = LMS.fromXYZ(destinationWhite, catType);
    double[][] lmsM = new double[][] {
        {
        destWhite.L / srcWhite.L, 0, 0}, {
        0, destWhite.M / srcWhite.M, 0}, {
        0, 0, destWhite.S / srcWhite.S}
    };
    double[][] m = DoubleArray.times(DoubleArray.times(CAMConst.
        getLMS2XYZMatrix(catType), lmsM),
                                     CAMConst.getXYZ2LMSMatrix(catType)
        );
    return m;
  }

  /**
   * 類別方法(靜態方法),將sourceColor轉換到destColor
   * @param sourceColor CIEXYZ
   * @param adaptationMatrix double[][]
   * @return CIEXYZ
   */
  public final static CIEXYZ adaptation(CIEXYZ sourceColor,
                                        double[][] adaptationMatrix) {
    double[] destValues = adaptation(sourceColor.getValues(), adaptationMatrix);
    return new CIEXYZ(destValues);
  }

  /**
   * 類別方法(靜態方法),將sourceXYZValues陣列轉換到dest XYZValues
   * @param sourceXYZValues double[]
   * @param adaptationMatrix double[][]
   * @return double[]
   */
  public final static double[] adaptation(double[] sourceXYZValues,
                                          double[][] adaptationMatrix) {
    double[] destXYZValues = DoubleArray.times(adaptationMatrix,
                                               sourceXYZValues);
    return destXYZValues;
  }

  /**
   * 類別方法(靜態方法),將sourceColor轉換到destColor
   * @param sourceColor CIEXYZ
   * @param sourceWhite CIEXYZ
   * @param destinationWhite CIEXYZ
   * @param catType CATType
   * @return CIEXYZ
   */
  public final static CIEXYZ adaptation(CIEXYZ sourceColor, CIEXYZ sourceWhite,
                                        CIEXYZ destinationWhite,
                                        CAMConst.CATType catType) {
    return adaptation(sourceColor,
                      getAdaptationMatrix(sourceWhite, destinationWhite,
                                          catType));
  }

  /**
   * 物件方法,須配合建構式使用
   * 將(source)XYZValues轉換到dest XYZValues
   * @param XYZValues double[][]
   * @return double[][]
   */
  public double[][] adaptationToDestination(double[][] XYZValues) {
    return adaptation(XYZValues, adaptationMatrixToDest);
  }

  /**
   * 物件方法,須配合建構式使用
   * 將(dest)XYZValues轉換到source XYZValues
   * @param XYZValues double[][]
   * @return double[][]
   */
  public double[][] adaptationFromDestination(double[][] XYZValues) {
    return adaptation(XYZValues, adaptationMatrixFromDest);
  }

  /**
   * 類別方法(靜態方法),將(source)XYZValues陣列轉換到dest XYZValues
   * @param XYZValues double[][]
   * @param chromaticAdaptationMatrix double[][]
   * @return double[][]
   */
  public final static double[][] adaptation(double[][] XYZValues,
                                            double[][]
                                            chromaticAdaptationMatrix) {
    int size = XYZValues.length;
    for (int x = 0; x < size; x++) {
      XYZValues[x] = adaptation(XYZValues[x],
                                chromaticAdaptationMatrix);
    }
    return XYZValues;
  }

  protected final static CIEXYZ PCS_WHITE = Illuminant.D50WhitePoint;
  protected final static CIEXYZ D65_WHITE = Illuminant.D65WhitePoint;

  /**
   * 類別方法(靜態方法)
   * @param deviceWhite CIEXYZ
   * @param catType CATType
   * @return double[][]
   * @deprecated
   */
  public final static double[][] getAdaptationMatrixToD50(CIEXYZ
      deviceWhite, CAMConst.CATType catType) {
    return getAdaptationMatrix(deviceWhite, PCS_WHITE, catType);
  }

  /**
   * 物件方法,須配合建構式使用
   * 取得source->dest的色適應矩陣
   * @return double[][]
   */
  public double[][] getAdaptationMatrixToDestination() {
    return adaptationMatrixToDest;
  }

  /**
   * 物件方法,須配合建構式使用
   * 取得dest->source的色適應矩陣
   * @return double[][]
   */
  public double[][] getAdaptationMatrixFromDestination() {
    return adaptationMatrixFromDest;
  }

  public static void main(String[] args) {
    CIEXYZ white = Illuminant.A.getNormalizeXYZ();
    System.out.println("A:" + white);
    ChromaticAdaptation ca = ChromaticAdaptation.getInstanceAdaptToPCS(white,
        CAMConst.CATType.Bradford);
    CIEXYZ D50 = ca.getDestinationColor(white);
    D50.normalizeY();
    System.out.println("D50:" + D50);

    System.out.println("NORMALIZE_D50:" + PCS_WHITE);

    CIEXYZ ad = ChromaticAdaptation.adaptation(white, white, PCS_WHITE,
                                               CAMConst.CATType.Bradford);
    ad.normalizeY();
    System.out.println("ad" + ad);

    System.out.println(DoubleArray.toString(ca.getAdaptationMatrixToDestination()));
//    System.out.println(DoubleArray.toString(ca.
//                                            getAdaptationMatrixFromDestination()))\;
    double[][] tran = DoubleArray.transpose(ca.getAdaptationMatrixToDestination());
    System.out.println(DoubleArray.toString(tran));
  }
}
