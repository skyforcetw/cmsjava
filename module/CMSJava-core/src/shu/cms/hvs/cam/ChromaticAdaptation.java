package shu.cms.hvs.cam;

import java.io.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * ��A���ഫ
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
   * �w�]����A������
   */
  public final static CAMConst.CATType DEFAULT_CAT_TYPE = CAMConst.CATType.
      Bradford;

  /**
   * �p��deviceWhite��PCS(D50)����A������
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
   * �p��deviceWhite��D50����A������
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
   * �����k,���t�X�غc���ϥ�
   * �NsourceXYZ���destXYZ
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
   * �����k,���t�X�غc���ϥ�
   * �NsourceXYZValues�}�C�ഫ��destXYZValues�}�C
   * @param sourceXYZValues double[]
   * @return double[]
   */
  public double[] getDestinationColor(double[] sourceXYZValues) {
    return adaptation(sourceXYZValues, adaptationMatrixToDest);
  }

  /**
   * �����k,���t�X�غc���ϥ�
   * �NadaptedValues�}�C�ഫ��sourceXYZValues�}�C
   * @param adaptedXYZValues double[]
   * @return double[]
   */
  public double[] getSourceColor(double[] adaptedXYZValues) {
    return adaptation(adaptedXYZValues, adaptationMatrixFromDest);
  }

  /**
   * �����k,���t�X�غc���ϥ�
   * �NadaptedXYZ���sourceXYZ
   * @param adaptedXYZ CIEXYZ
   * @return CIEXYZ
   */
  public CIEXYZ getSourceColor(CIEXYZ adaptedXYZ) {
    double[] source = getSourceColor(adaptedXYZ.getValues());
    CIEXYZ sourceXYZ = new CIEXYZ(source, this.sourceWhite);
    return sourceXYZ;
  }

  /**
   * �p���A���x�}
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
   * ���O��k(�R�A��k),�NsourceColor�ഫ��destColor
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
   * ���O��k(�R�A��k),�NsourceXYZValues�}�C�ഫ��dest XYZValues
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
   * ���O��k(�R�A��k),�NsourceColor�ഫ��destColor
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
   * �����k,���t�X�غc���ϥ�
   * �N(source)XYZValues�ഫ��dest XYZValues
   * @param XYZValues double[][]
   * @return double[][]
   */
  public double[][] adaptationToDestination(double[][] XYZValues) {
    return adaptation(XYZValues, adaptationMatrixToDest);
  }

  /**
   * �����k,���t�X�غc���ϥ�
   * �N(dest)XYZValues�ഫ��source XYZValues
   * @param XYZValues double[][]
   * @return double[][]
   */
  public double[][] adaptationFromDestination(double[][] XYZValues) {
    return adaptation(XYZValues, adaptationMatrixFromDest);
  }

  /**
   * ���O��k(�R�A��k),�N(source)XYZValues�}�C�ഫ��dest XYZValues
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
   * ���O��k(�R�A��k)
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
   * �����k,���t�X�غc���ϥ�
   * ���osource->dest����A���x�}
   * @return double[][]
   */
  public double[][] getAdaptationMatrixToDestination() {
    return adaptationMatrixToDest;
  }

  /**
   * �����k,���t�X�غc���ϥ�
   * ���odest->source����A���x�}
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
