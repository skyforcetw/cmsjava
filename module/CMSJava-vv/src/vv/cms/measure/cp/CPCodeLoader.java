package vv.cms.measure.cp;

import java.io.*;

import java.awt.*;
import javax.swing.*;

//import auo.cms.measure.cp.i2c.*;
import shu.cms.colorspace.depend.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.lcd.material.*;
import shu.cms.util.*;
import shu.util.*;
import shu.util.log.*;
import vv.cms.lcd.material.*;
//import vv.cms.lcd.calibrate.shm.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CPCodeLoader {
  public static class DummyAdapter {
    /**
     * �]�wdummy LCDModel, ��cp code����LCDModel��
     * @param lcdModel LCDModel
     */
    public void setDummyLCDModel(LCDModel lcdModel) {
      DummyLCDModel = lcdModel;
    }

    /**
     * �O�_���˸��J, ���˸��J�ä��|�u����cp code����rom�h
     * �p�G��dummy LCDModel, �|����LCDModel�h
     * @param dummy boolean
     */
    public void setDummyLoading(boolean dummy) {
      DummyLoading = dummy;
    }
  }

  public final static String BINARY_FILE_NAME = "cp_code.bin";
  public final static String EXCEL_FILE_NAME = "_cp_code.xls";
  public final static String CP_LOADER_EXECUTE = "Send_CP.exe";
  public final static String CP_LOADER_DIR = "..\\lib\\Send_CP_Table";

  /**
   * cp load�{���O�_�s�b
   * @return boolean
   */
  public final static boolean exists() {
    File lib = new File(CP_LOADER_DIR, CP_LOADER_EXECUTE);
    return lib.exists();
  }

  private static Mode mode = Mode.Com;
  /**
   * �O�_�@���˸��J
   */
  private static boolean DummyLoading = false;
  /**
   * �O�_����
   */
  private static boolean DarkInsert = mode == Mode.Command;
//  private static boolean DarkInsert = false;


  /**
   * ���˸��J���ؼ�LCDModel
   */
  private static LCDModel DummyLCDModel;

  /**
   * ���J�e�O�_���µe���קK�ù����{�{
   * @param insert boolean
   */
  final static void setDarkInsert(boolean insert) {
    DarkInsert = insert;
  }

  /**
   * ���Jcp code
   * @param excelFilename String �n���J��excel�ɦW
   * @param maxValue MaxValue �ؼ�ic��bit��
   * @return boolean �O�_���J���\
   */
  public final static boolean load(String excelFilename, RGB.MaxValue maxValue) {
    RGB[] rgbArray = null;
    try {
      rgbArray = RGBArray.loadVVExcel(excelFilename);
    }
    catch (jxl.read.biff.BiffException ex) {
      Logger.log.error("", ex);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    return load(rgbArray, maxValue);
  }

  /**
   * �N256�X�R��257��RGB, ��12bit IC�Ϊ�
   * @param rgbArray256 RGB[]
   * @return RGB[]
   */
  protected final static RGB[] extend256To257(RGB[] rgbArray256) {
    if (rgbArray256.length != 256) {
      throw new IllegalArgumentException(
          "rgbArray.length != 256");
    }
    RGB[] rgbArray257 = new RGB[257];
    System.arraycopy(rgbArray256, 0, rgbArray257, 0, 256);
    rgbArray257[256] = (RGB) rgbArray257[255].clone();
    return rgbArray257;
  }

  /**
   * ���¥Ϊ�frame
   */
  private static DarkFrame darkFrame = null;
  /**
   * �O�_�j����J, �n�O�}�ҫ�o�͵L�k���J, �|�H�T���q���ư���ê, ������J���\
   */
  private static boolean ForceLoading = true;
  /**
   * �O�_���g���J�L��lcp code
   */
  private static boolean OriginalLoaded = false;

  /**
   * �O�_���J�Fcp code
   * @return boolean
   */
  public final static boolean isCPCodeLoading() {
    return!OriginalLoaded;
  }

  /**
   * ���J��l��cp code, �]�N�O��0 1 2 3....255
   * @param maxValue MaxValue �ؼ�ic��bit��
   * @return boolean �O�_���J���\
   */
  public final static boolean loadOriginal(RGB.MaxValue maxValue) {
//    System.out.println("xx");
    RGB[] original = RGBArray.getOriginalRGBArray();
//    System.out.println("xx2");
    return load(original, maxValue, true, false);
  }

  /**
   * @deprecated
   */
  public final static void loadOriginalDummy() {
    if (DummyLCDModel == null) {
      throw new IllegalStateException("dummyLCDModel == null");
    }
    DummyLCDModel.setDisplayLUT(null);
  }

  /**
   * ���Jcp code
   * @param rgbArray RGB[]
   * @param maxValue MaxValue �ؼ�ic��bit��
   * @return boolean �O�_���J���\
   */
  public final static boolean load(RGB[] rgbArray, RGB.MaxValue maxValue) {
    return load(rgbArray, maxValue, false, false);
  }

  public final static boolean load(RGB[] rgbArray, RGB.MaxValue maxValue,
                                   boolean dummyLoading) {
    return load(rgbArray, maxValue, dummyLoading, false);
  }

  /**
   * �C�X�����J�j��@�@��gc
   */
  private final static int EachGCForLoadingCount = 20;
  /**
   * ���J���`����
   */
  private static int LoadingCount = 0;

  public static enum Mode {
    //�z�L�R�O�C�Ҧ����श�{��load
    Command,
    //�z�Lshare memory�ǰecode�@load
    ShareMemory,
    Com
  }

  /**
   * �W�@�����J��cp code, �Ψ��קK�e���{�{�Ҩϥ�.
   */
  private static RGB[] LastCPCode;

  /**
   * �ˬd�t����
   */
  private static boolean CheckDifference = false;
  private static int AcceptDifference = 63;

  final static void setAcceptDifference(int accept) {
    AcceptDifference = accept;
  }

  final static void setCheckDifference(boolean check) {
    CheckDifference = check;
  }

  /**
   * �߬dcode���t���ʬO�_�p�󵥩�accept
   * @param rgbArray RGB[]
   * @param accept int
   * @return boolean
   */
  final static boolean checkCPCodeDifference(RGB[] rgbArray,
                                             int accept) {
    int size = rgbArray.length;
    for (int x = 0; x < size - 1; x++) {
      RGB rgb = rgbArray[x];
      RGB nextRGB = rgbArray[x + 1];
      boolean result = Math.abs(rgb.R - nextRGB.R) <= accept &&
          Math.abs(rgb.G - nextRGB.G) <= accept &&
          Math.abs(rgb.B - nextRGB.B) <= accept;
      if (!result) {
        return false;
      }
    }
    return true;
  }

  private final static boolean load0(RGB[] rgbArray,
                                     RGB.MaxValue maxValue) {
    if (CheckDifference && !checkCPCodeDifference(rgbArray, AcceptDifference)) {
      throw new IllegalArgumentException("CP Code difference is large than " +
                                         AcceptDifference + ".");
    }
    if (rgbArray.length == 256 && maxValue == RGB.MaxValue.Int12Bit) {
      //12bit��ic�ݭnextend��257��
      rgbArray = extend256To257(rgbArray);
    }

    LastCPCode = rgbArray;
    RGBArray.storeVVExcel(rgbArray, "_CPCodeLoader.xls");
    boolean result = false;
    switch (mode) {
      case Command:
        result = load0ByCommand(rgbArray, maxValue);
        break;
      case ShareMemory:
        if (null == loader) {
          try {
            loader = (LoaderInterface) Class.forName(
                "vv.cms.lcd.calibrate.shm.ShareMemoryConnector").newInstance();
          }
          catch (ClassNotFoundException ex) {
            Logger.log.error("", ex);
          }
          catch (IllegalAccessException ex) {
            Logger.log.error("", ex);
          }
          catch (InstantiationException ex) {
            Logger.log.error("", ex);
          }
        }
        result = load0ByInterface(rgbArray, maxValue, loader);
        result = SkipShareMemoryAck ? true : result;

//        result = load0ByShareMemory(rgbArray, maxValue);
        break;
      case Com:
        if (null == loader) {
          try {
            loader = (LoaderInterface) Class.forName(
                "auo.cms.measure.cp.i2c.DigitalGammaLoader").newInstance();
          }
          catch (ClassNotFoundException ex) {
            Logger.log.error("", ex);
          }
          catch (IllegalAccessException ex) {
            Logger.log.error("", ex);
          }
          catch (InstantiationException ex) {
            Logger.log.error("", ex);
          }
        }
        result = load0ByInterface(rgbArray, maxValue, loader);

//        result = load0ByCom(rgbArray, maxValue);
        break;
      default:
        throw new IllegalStateException("Unsupported mode.");
    }
    return result;
  }

//  private static I2C i2c;
//  private static String AUOTcon = AutoCPOptions.getString("CPL_AUOTCon");

//  private final static boolean load0ByCom(RGB[] rgbArray,
//                                          RGB.MaxValue maxValue) {
//    if (i2c == null) {
//      i2c = new I2C();
//    }
//    RGB[] cpcode = RGBArray.deepClone(rgbArray);
//    RGBArray.changeMaxValue(cpcode, maxValue);
//
//    RGBArray.storeAUOExcel(cpcode, EXCEL_FILE_NAME);
//    i2c.frc_dg_en(AUOTcon, true, false);
//    boolean result = i2c.write_lut(AUOTcon,
//                                   new File(EXCEL_FILE_NAME).getAbsolutePath());
//    i2c.frc_dg_en(AUOTcon, true, true);
//    return result;
//  }

  private final static boolean load0ByCommand(RGB[] rgbArray,
                                              RGB.MaxValue maxValue) {
    if (!exists()) {
      JOptionPane.showMessageDialog(null, "! CPCodeLoader.exists()",
                                    "Warning", JOptionPane.WARNING_MESSAGE);
    }

//    if (rgbArray.length == 256 && maxValue == RGB.MaxValue.Int12Bit) {
//      //12bit��ic�ݭnextend��257��
//      rgbArray = extend256To257(rgbArray);
//    }

    //�s���G�i���ɮ�
    RGBArray.storeBinaryFile(rgbArray,
                             CP_LOADER_DIR + "\\" + BINARY_FILE_NAME,
                             maxValue);
    //�A�s��excel��, �u�O�ΨӽT�{��
    RGBArray.storeVVExcel(rgbArray,
                          CP_LOADER_DIR + "\\" + EXCEL_FILE_NAME);

    String cmd = "cmd /c " + CP_LOADER_DIR + "\\cp_code_load.bat";
    try {
      int result = Utils.execAndWaitFor(cmd);
      return 1 == result;
    }
    catch (InterruptedException ex) {
      Logger.log.error("", ex);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    return false;
  }

//  /**
//   *
//   * @param rgbArray RGB[]
//   * @param maxValue MaxValue
//   * @return boolean
//   */
//  private final static boolean load0ByShareMemory(RGB[] rgbArray,
//                                                  RGB.MaxValue maxValue) {
//    if (connector == null) {
//      connector = ShareMemoryConnector.getInstance();
//    }
//    boolean result = connector.loadCode(rgbArray, maxValue);
//    return SkipShareMemoryAck ? true : result;
//  }

  private final static boolean load0ByInterface(RGB[] rgbArray,
                                                RGB.MaxValue maxValue,
                                                LoaderInterface loader) {
    return loader.loadCode(rgbArray, maxValue);
  }

  private static LoaderInterface loader;

  public interface LoaderInterface {
    public boolean loadCode(final RGB[] cpcodeArray, final RGB.MaxValue icBit);
  }

  /**
   * �n���n���L�ˬddown load code�O�_���\
   */
  private static boolean SkipShareMemoryAck = AutoCPOptions.get(
      "CPL_SkipShareMemoryAck");

//  private static ShareMemoryConnector connector;

  /**
   * �̨�̷t��code.
   * @param rgbArray RGB[]
   * @return Color
   */
  private static Color findDarkestColor(RGB[] rgbArray) {
    int size = rgbArray.length;
    double minValue = Double.MAX_VALUE;
    int minIndex = -1;
    for (int x = 0; x < size; x++) {
      RGB rgb = rgbArray[x];
      double value = rgb.getValue(RGBBase.Channel.W);
      if (value < minValue) {
        minValue = value;
        minIndex = x;
      }
    }
    Color c = new Color(minIndex, minIndex, minIndex);
    return c;
  }

  /**
   * ���Jcp code
   *
   * 1. �ˬd�O�_��original load, �קK����load original
   * 2. �ˬddummyLoading
   * 3. �ˬd�O�_show�µe��
   * 4. �ˬdcode�ƶq
   * 5.
   *
   * @param rgbArray RGB[]
   * @param maxValue MaxValue �ؼ�ic��bit��
   * @param originalLoading boolean �O�_���J��lcp code
   * @param dummyLoading boolean
   * @return boolean �O�_���J���\
   */
  private final static boolean load(RGB[] rgbArray, RGB.MaxValue maxValue,
                                    boolean originalLoading,
                                    boolean dummyLoading) {
    boolean dummy = DummyLoading || dummyLoading;
    if (rgbArray.length != 256 && rgbArray.length != 257) {
      throw new IllegalArgumentException(
          "rgbArray.length != 256 && rgbArray.length != 257");
    }

    if (!dummy) {
      initDarkFrame();
      setDarkFrame(true);
    }

    //==========================================================================
    // ����ư�original��loading
    //==========================================================================
    if (OriginalLoaded == true && OriginalLoaded == originalLoading) {
      //�w�g���Lorignal��loading, �ҥH���ΦAload�F
      setDarkFrame(false);
      return true;
    }
    OriginalLoaded = originalLoading;
    //==========================================================================

    if (dummy) {
      if (DummyLCDModel != null) {
        DisplayLUT displayLUT = new DisplayLUT(rgbArray);
        DummyLCDModel.setDisplayLUT(displayLUT);
      }
      return true;
    }

    LoadingCount++;
    if (LoadingCount % EachGCForLoadingCount == 0) {
      System.gc();
    }
    boolean autoretry = false;
    boolean result = false;
    while ( (result = load0(rgbArray, maxValue)) != true && ForceLoading) {
      if (!autoretry) {
        autoretry = true;
        Logger.log.error("CP Code loading fail, auto retry one time.");
        continue;
      }

      int ans = JOptionPane.showConfirmDialog(darkFrame,
                                              "retry?",
                                              "CP Code loading fail!",
                                              JOptionPane.YES_NO_OPTION,
                                              JOptionPane.QUESTION_MESSAGE);
      if (ans == JOptionPane.NO_OPTION) {
        if (DarkInsert) {
          darkFrame.setVisible(false);
        }
        //�p�G��retry, ��X�T���åB�^��false
        JOptionPane.showMessageDialog(darkFrame,
                                      "CP Code loading fail, stop measure.",
                                      "Stop measure",
                                      JOptionPane.WARNING_MESSAGE);
        Logger.log.error("CP Code loading fail, stop measure.");
        return false;
      }
      else {
        //�p�G�nretry, �����@�U�T��
        Logger.log.error("CP Code loading fail, retry.");
      }
    }
    if (WaitTime != 0) {
      try {
        Thread.currentThread().sleep(WaitTime);
      }
      catch (InterruptedException ex) {
        Logger.log.error("", ex);
      }
    }
    setDarkFrame(false);

    return true;
  }

  private final static void setDarkFrame(boolean visible) {
    if (DarkInsert && darkFrame != null) {
      darkFrame.setVisible(visible);
    }

  }

  private final static void initDarkFrame() {
    if (DarkInsert) {
      /**
       * �ѩ�cp code���J���ɭ�, �e���|�{�{(�o�u�O���F�Ⱥε۷Q..., �]���{�Ӱ{�h������), ���F
       * ���C�{�{���{�H, �ҥH�bcp code�̭�, ���̷t��code, �n���e�����e, show�o�ӳ̷t��
       * code, �N������|�{�{�F.
       */
      Color black = (LastCPCode != null) ? findDarkestColor(LastCPCode) :
          Color.black;
      //show�µe��
      darkFrame = (darkFrame == null) ? new DarkFrame(black) : darkFrame;
      darkFrame.setColor(black);
//      setDarkFrame(true);
    }

  }

  public final static int getLoadingCount() {
    return LoadingCount;
  }

  static void setWaitTime(long waitTime) {
    WaitTime = waitTime;
  }

  private static long WaitTime = 0;

  public static void main(String[] args) {
    CPCodeLoader.loadOriginal(RGB.MaxValue.Int12Bit);
  }
}
