package shu.cms.profile;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �Ψӻs�@Profile
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class ProfileMaker {
  /**
   * LUT���I���ƶq
   */
  protected final static int NUMBER_OF_GRID_POINTS = 33;

  public final static String SoftwareName = "jColor";

  public ProfileMaker() {
//    D50White = Illuminant.D50.getNormalizeXYZ();
  }

  protected ChromaticAdaptation D50chromaticAdaptation;
  static CAMConst.CATType catType = CAMConst.CATType.
      Bradford;

  static ViewingConditions referenceMediumViewingConditions =
      ViewingConditions.PerceptualIntentViewingConditions;

  /**
   * �]�w��A����model
   * @param type CATType
   */
  public static void setCATType(CAMConst.CATType type) {
    catType = type;
  }

  /**
   * �]�ƥ�
   */
  protected CIEXYZ deviceWhite;

  protected CIEXYZ D50White = (CIEXYZ) Illuminant.D50WhitePoint.clone();

  /**
   * deviceWhite�g���W�ƳB�z
   */
  protected CIEXYZ deviceNormalizeWhite;

//  protected int errorXYZ2JabCount;
//  protected int errorJab2XYZCount;
//  protected int errorDarkLabValuesCount1;
//  protected int errorDarkLabValuesCount2;

  /**
   * �ഫ��]�Ʀ�Ū�XYZ
   * @param D50XYZ double[][]
   * @return double[][]
   */
  protected double[][] produceDeviceXYZGrid(double[][] D50XYZ) {
    int size = D50XYZ.length;
    //�qD50���ù����
    double[][] deviceInput = new double[size][3];
    System.arraycopy(D50XYZ, 0, deviceInput, 0, size);
    deviceInput = D50chromaticAdaptation.adaptationFromDestination(deviceInput);
    CIEXYZ.rationalize(deviceInput);
    return deviceInput;
  }

//  protected final static double GMA_L_STEP = .5;
//  protected final static double GMA_C_STEP = .5;




  /**
   * �]�w�Ѧ����Ҫ��Ѽ�
   * @param vc ViewingConditions
   */
  public final static void setReferenceMediumViewingConditions(
      ViewingConditions vc) {
    referenceMediumViewingConditions = vc;
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * �ĤG��GAM�ұĥΪ���k
   * 1.RGB ������RGB�Hclip���覡�B�z,�L�k�O����۫�w
   * 2.�HLCh��C��clip���B�z,�i�H�O����۫�w
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static enum GMA2ClippingType {
    RGB, LCh;
  }

  public static class Report {
    /**
     * �i��ĤG��gma������,���ƶV�h�N���lgma���]�p�V���}
     */
    public int gma2ProcessCount;
    /**
     * XYZ2Jab�ഫ���Ϳ��~������(�p��X�D�X�z��)
     */
    public int errorXYZ2JabCount;
    /**
     * Jab2XYZ�ഫ���Ϳ��~������(�p��X�D�X�z��)
     */
    public int errorJab2XYZCount;
    /**
     * �N�@���ഫ����,�y�����~���t���C��,�ץ����¦⪺����
     */
    public int errorDarkLabValuesCount1;
    /**
     * �N�@���ഫ����,�y�����~���t���C��,�ץ����¦⪺����
     * �����ĤG�����ץ�
     */
    public int errorDarkLabValuesCount2;

    /**
     * Lab�D�X�z�ƪ�����
     */
    public int _LabRationalCount;
    /**
     * XYZ�D�X�z�ƪ�����
     */
    public int XYZRationalCount;

    public boolean isLCDModelDoGammaCorrect;

    public final String toString() {
      StringBuilder buf = new StringBuilder();

      buf.append("GMA2ProcessCount: " + gma2ProcessCount + "\n");
      buf.append("LabRationalCount: " + _LabRationalCount + "\n");
      buf.append("XYZRationalCount: " + XYZRationalCount + "\n");

      buf.append("\nColor Appearance Model Transform:");
      buf.append("errJab2XYZ: " + errorJab2XYZCount + "\n");
      buf.append("errXYZ2Jab: " + errorXYZ2JabCount + "\n");
      buf.append("errDarkLabValuesCount1: " + errorDarkLabValuesCount1 + "\n");
      buf.append("errDarkLabValuesCount2: " + errorDarkLabValuesCount2 + "\n");

      buf.append("\nLCD Model:");

      return buf.toString();
    }
  }

  protected Report report;
  public final Report getReport() {
    return report;
  }

  protected void init() {
    report = new Report();
  }
}
