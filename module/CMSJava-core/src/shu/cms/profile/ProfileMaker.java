package shu.cms.profile;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來製作Profile
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
   * LUT格點的數量
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
   * 設定色適應的model
   * @param type CATType
   */
  public static void setCATType(CAMConst.CATType type) {
    catType = type;
  }

  /**
   * 設備白
   */
  protected CIEXYZ deviceWhite;

  protected CIEXYZ D50White = (CIEXYZ) Illuminant.D50WhitePoint.clone();

  /**
   * deviceWhite經正規化處理
   */
  protected CIEXYZ deviceNormalizeWhite;

//  protected int errorXYZ2JabCount;
//  protected int errorJab2XYZCount;
//  protected int errorDarkLabValuesCount1;
//  protected int errorDarkLabValuesCount2;

  /**
   * 轉換到設備色溫的XYZ
   * @param D50XYZ double[][]
   * @return double[][]
   */
  protected double[][] produceDeviceXYZGrid(double[][] D50XYZ) {
    int size = D50XYZ.length;
    //從D50轉到螢幕色溫
    double[][] deviceInput = new double[size][3];
    System.arraycopy(D50XYZ, 0, deviceInput, 0, size);
    deviceInput = D50chromaticAdaptation.adaptationFromDestination(deviceInput);
    CIEXYZ.rationalize(deviceInput);
    return deviceInput;
  }

//  protected final static double GMA_L_STEP = .5;
//  protected final static double GMA_C_STEP = .5;




  /**
   * 設定參考環境的參數
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
   * 第二次GAM所採用的方法
   * 1.RGB 直接對RGB以clip的方式處理,無法保持色相恆定
   * 2.以LCh的C的clip做處理,可以保持色相恆定
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
     * 進行第二次gma的次數,次數越多代表原始gma的設計越不良
     */
    public int gma2ProcessCount;
    /**
     * XYZ2Jab轉換產生錯誤的次數(計算出非合理數)
     */
    public int errorXYZ2JabCount;
    /**
     * Jab2XYZ轉換產生錯誤的次數(計算出非合理數)
     */
    public int errorJab2XYZCount;
    /**
     * 將一些轉換之後,造成錯誤的暗部顏色,修正成黑色的次數
     */
    public int errorDarkLabValuesCount1;
    /**
     * 將一些轉換之後,造成錯誤的暗部顏色,修正成黑色的次數
     * 此為第二次的修正
     */
    public int errorDarkLabValuesCount2;

    /**
     * Lab遭合理化的次數
     */
    public int _LabRationalCount;
    /**
     * XYZ遭合理化的次數
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
