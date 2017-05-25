package shu.cms.lcd.material;

import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class AutoCPOptions {
  public final static void setTestEnvironment() {
    DummyMeter = true;
    MeasuredModel = false;
    UseXTalkModel = false;
    ModelCalibrator_MultiThread = false;
  }

  public static void main(String[] args) {
    System.out.println("produce options file \"autocp.xml\"");
    options.storeToXML();
  }

  //============================================================================
  // �q���]�w��
  //============================================================================
  /**
   * �O�_�ĥ�Dummy Meter.
   * ���ﶵ�PMeasuredModel���椬�@��
   *
   * MeasuredModel DummyMeter
   * true  true : Model�H�q�����G����, Meter�ȥHModel����; model�Ķq��, meter�Hmodel�B�@ (�t�צ���)
   * true  false: Model�H�q�����G����, Meter�ȥH��������;   model�Ķq��, meter������ (�̺C)
   * false true : Model�H�x�s��Ʋ���, Meter�ȥHModel����; �¥Hstore��ƹB�@ (�t�׳̧�)
   * false false: Model�H�x�s��Ʋ���, Meter�ȥH��������;   model�Hstore�B�@, meter������ (�H�������U��data��model)
   */
  static boolean DummyMeter = false;

  /**
   * ���O����l�S�ʬO�_�z�L�q�����o?
   * �Y��true, �h�s�������i��q��.
   */
  static boolean MeasuredModel = false;
  //============================================================================

  //============================================================================
  // �ѼƳ]�w��
  //============================================================================
  /**
   * �O�_�z�Lcommand�I�sAutoCP GUI����downaload��measure
   */
  static boolean ShareMemoryMode = false;

  static boolean LoggingDetail = true;
  //============================================================================


  //============================================================================
  // cpm����
  //============================================================================
  /**
   * CPM�O�_�n��ܰ����Ϊ��T��
   */
  static boolean CPM_ShowTrayIcon = false;
  /**
   * �e�X�q���ݨD���P�ɬO�_�nĲ�o�q�� (�w�]false)
   */
  static boolean CPM_MeasureRequestThanTrigger = false;
  /**
   * CP Code Load����, ack�^�ǭȬO�_����.
   */
  static boolean CPL_SkipShareMemoryAck = false;
//  static String CPL_AUOTCon = "AUO12202";
  static String CPL_AUOTCon = "AUO12401-K1 Dual";
  //============================================================================

  //============================================================================
  // LCDTarget�ɮ׳]�w
  //============================================================================
  static String LCD_Device = "auo_B140XW01";
  static String LCD_Dir = "091125";
//  static String LCD_Device = "auo_B156HW01";
//  static String LCD_Dir = "091116";

//  static String LCD_Device = "auo_M240HW01";
//  static String LCD_Dir = "091217";
//  static String LCD_Device = "auo_T370HW02";
//  static String LCD_Dir = "091218";

//  static String LCD_Device = "cpt_320WF01SC";
//  static String LCD_Dir = "0805";

  static String LCD_FileTag = "";
//  static String LCD_Number = "Complex257_589";
  static String LCD_Number = "Ramp257_6Bit";
//  static String LCD_Number = "Complex1021_769";
//  static String LCD_Number = "Ramp1021";
  static boolean FixBlueHook = false;
  //============================================================================


  //============================================================================
  static boolean UseXTalkModel = false;
  static boolean XTalkModel_LoggingDetail = false && LoggingDetail;
  static boolean UseCCTv3Model = false;
  static String MultiMatrix_GetRGBMode = "Mode1";

  //============================================================================
  // plot
  //============================================================================
  static boolean CalibrateTester_Plotting = false;
  static boolean FindingThread_PlotStep = false;
  //============================================================================

  //============================================================================
  // ModelCalibrator�����}��
  //============================================================================
  static boolean ModelCalibrator_MultiThread = false;
  //============================================================================

  //============================================================================
  // MeasuredCalibrator�����}��
  //============================================================================
  static boolean MeasuredCalibrator_LoggingDetail = true && LoggingDetail;
  static boolean MeasuredCalibrator_ConsiderHKEffect = true;
  static boolean MeasuredCalibrator_UseRelativeTarget = true;
  static boolean MeasuredCalibrator_ChromaticityRelative = true;
  /**
   * find by model�򥻤W�N�O�������z�L�������ե�, ���Pmeasure�B�J�����L��
   */
  static boolean MeasuredCalibrator_FindByModel = false;
  //============================================================================

  //============================================================================
  // FindingThread�����}��
  //============================================================================
  //�̫�O�_�A�Hcube�@���ե�
  static boolean FindingThread_CubeCalibrateFinal = false;
  //�̫�O�_�w���׮ե�����
  static boolean FindingThread_LuminanceCalibrateFinal = true;
  //��׬O�_�B�i�ե�
  static boolean FindingThread_ChromaticInStep = true;
  //���׬O�_�B�i�ե�
  static boolean FindingThread_LuminanceInStep = true;
  //�̫�A���@��check(����ĳ�ϥ�)
  static boolean FindingThread_FinalCheck = false;
  //============================================================================

  private final static OptionsFile options = new OptionsFile("autocp.xml",
      "autocp.ini", "AutoCP Options", AutoCPOptions.class);
  public final static boolean get(String name) {
    return options.get(name);
  }

  public final static String getString(String name) {
    return options.getString(name);
  }

}
