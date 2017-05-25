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
  // 量測設定區
  //============================================================================
  /**
   * 是否採用Dummy Meter.
   * 此選項與MeasuredModel有交互作用
   *
   * MeasuredModel DummyMeter
   * true  true : Model以量測結果產生, Meter值以Model產生; model採量測, meter以model運作 (速度次快)
   * true  false: Model以量測結果產生, Meter值以儀器產生;   model採量測, meter接儀器 (最慢)
   * false true : Model以儲存資料產生, Meter值以Model產生; 純以store資料運作 (速度最快)
   * false false: Model以儲存資料產生, Meter值以儀器產生;   model以store運作, meter接儀器 (以儀器輔助舊data的model)
   */
  static boolean DummyMeter = false;

  /**
   * 面板的原始特性是否透過量測取得?
   * 若為true, 則連接儀器進行量測.
   */
  static boolean MeasuredModel = false;
  //============================================================================

  //============================================================================
  // 參數設定區
  //============================================================================
  /**
   * 是否透過command呼叫AutoCP GUI控制downaload及measure
   */
  static boolean ShareMemoryMode = false;

  static boolean LoggingDetail = true;
  //============================================================================


  //============================================================================
  // cpm控制
  //============================================================================
  /**
   * CPM是否要顯示除錯用的訊息
   */
  static boolean CPM_ShowTrayIcon = false;
  /**
   * 送出量測需求的同時是否要觸發量測 (預設false)
   */
  static boolean CPM_MeasureRequestThanTrigger = false;
  /**
   * CP Code Load之後, ack回傳值是否忽略.
   */
  static boolean CPL_SkipShareMemoryAck = false;
//  static String CPL_AUOTCon = "AUO12202";
  static String CPL_AUOTCon = "AUO12401-K1 Dual";
  //============================================================================

  //============================================================================
  // LCDTarget檔案設定
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
  // ModelCalibrator相關開關
  //============================================================================
  static boolean ModelCalibrator_MultiThread = false;
  //============================================================================

  //============================================================================
  // MeasuredCalibrator相關開關
  //============================================================================
  static boolean MeasuredCalibrator_LoggingDetail = true && LoggingDetail;
  static boolean MeasuredCalibrator_ConsiderHKEffect = true;
  static boolean MeasuredCalibrator_UseRelativeTarget = true;
  static boolean MeasuredCalibrator_ChromaticityRelative = true;
  /**
   * find by model基本上就是完全不透過儀器做校正, 等同measure步驟完全無效
   */
  static boolean MeasuredCalibrator_FindByModel = false;
  //============================================================================

  //============================================================================
  // FindingThread相關開關
  //============================================================================
  //最後是否再以cube作為校正
  static boolean FindingThread_CubeCalibrateFinal = false;
  //最後是否已明度校正收尾
  static boolean FindingThread_LuminanceCalibrateFinal = true;
  //色度是否步進校正
  static boolean FindingThread_ChromaticInStep = true;
  //明度是否步進校正
  static boolean FindingThread_LuminanceInStep = true;
  //最後再做一次check(不建議使用)
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
