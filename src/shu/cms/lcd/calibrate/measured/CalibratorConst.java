package shu.cms.lcd.calibrate.measured;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 儲存校正結果所需要用到的檔案名稱
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public interface CalibratorConst {
  public final static String MeaRelativeLogoFilename = "[MEAS]-1-Relative";

  //============================================================================
  // cord
  //============================================================================
  public final static String CordInteger = "[CORD]-1-Integer.xls";
  public final static String CordCalibrated = "[CORD]-2-Calibrated.xls";
  public final static String CordCalibrated2 = "[CORD]-3-Calibrated2.xls";
  public final static String CordFinal = "[CORD]-4-Final.xls";
  //============================================================================

  //============================================================================
  // JNDI
  //============================================================================
  public final static String JNDIEstimate = "[JNDI]-1-Estimate.xls";
  public final static String JNDIMinDelta = "[JNDI]-2-MinDelta.xls";
  public final static String JNDICalibrated = "[JNDI]-3-Calibrated.xls";
  public final static String JNDICompromise = "[JNDI]-4-Compromise.xls";
  public final static String JNDIFinal = "[JNDI]-5-Final.xls";
  //============================================================================

  //============================================================================
  // green
  //============================================================================
  public final static String GreenSmoothLogo = "[GREN]-1-Smooth.logo";
  public final static String GreenJNDICalibrated =
      "[GREN]-2-JNDICalibrated.xls";
  public final static String GreenFinal = "[GREN]-3-Final.xls";
  //============================================================================

  //============================================================================
  // indep
  //============================================================================
  public final static String IndependLuminanceBasedBefInterp =
      "[INDP]-0.9-LuminanceBased_BefInterp.xls";
  public final static String IndependLuminanceBased =
      "[INDP]-1.0-LuminanceBased.xls";
  public final static String IndependLuminanceBasedCube =
      "[INDP]-1.1-LuminanceBased_Cube.xls";

  public final static String IndependWhiteBasedBefInterp =
      "[INDP]-1.9-WhiteBased_BefInterp.xls";
  public final static String IndependWhiteBased = "[INDP]-2.0-WhiteBased.xls";
  public final static String IndependWhiteBasedCube =
      "[INDP]-2.1-WhiteBased_Cube.xls";

  public final static String IndependSmoothGreenLogo =
      "[INDP]-3-SmoothGreen.logo";
  public final static String IndependSmoothGreen = "[INDP]-4-SmoothGreen.xls";

  public final static String IndependGreenBasedBefInterp =
      "[INDP]-4.9-GreenBased_BefInterp.xls";
  public final static String IndependGreenBased = "[INDP]-5.0-GreenBased.xls";
  public final static String IndependGreenBasedCube =
      "[INDP]-5.1-GreenBased_Cube.xls";

  public final static String IndependWhiteBased2BefInterp =
      "[INDP]-5.9-WhiteBased2_BefInterp.xls";
  public final static String IndependWhiteBased2 = "[INDP]-6.0-WhiteBased2.xls";
  public final static String IndependWhiteBased2Cube =
      "[INDP]-6.1-WhiteBased2_Cube.xls";

  public final static String IndependLuminanceBased2BefInterp =
      "[INDP]-6.9-LuminanceBased2_BefInterp.xls";
  public final static String IndependLuminanceBased2 =
      "[INDP]-7.0-LuminanceBased2.xls";
  public final static String IndependLuminanceBased2Cube =
      "[INDP]-7.1-LuminanceBased2_Cube.xls";

  public final static String IndependFinal = "[INDP]-7-Final.xls";
  //============================================================================

  public final static String InterpTest = "InterpTest-";
}
