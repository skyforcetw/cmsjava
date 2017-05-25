package shu.cms.measure;

import java.text.*;
import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import shu.cms.*;
import shu.cms.applet.measure.tool.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.measure.cp.*;
import shu.cms.measure.meter.*;
import shu.cms.util.*;
import shu.math.array.*;
import shu.util.*;
import shu.util.log.*;

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
public class MeterMeasurement
    implements MeasureWindow.WindowsInvisibleListener {
  private static Dimension screenSize;
  static {
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  }

  public final static Dimension getSize(Meter.Instr type, double LCDSize) {
    double dpi = Resolution.getDPI(LCDSize, screenSize);
    int windowWidth = (int) (dpi * type.widthInch);
    int windowHeight = (int) (dpi * type.lengthInch);
    return new Dimension(windowWidth, windowHeight);
  }

  private static enum MeterMode {
    Argyll, Platform, Normal
  }

  private MeterMode meterMode = MeterMode.Normal;
  /**
   * 用來設定是否真的進行量測
   */
  private boolean fakeMeasure = false;
  /**
   * 用來設定是否真的進行量測.
   * 如果是搭配LCDModel進行模擬量測, 則建議開啟加快量測速度.
   * @param fake boolean
   */
  public void setFakeMeasure(boolean fake) {
    this.fakeMeasure = fake;
  }

  private boolean isAbnormalMode() {
    return meterMode != MeterMode.Normal;
  }

  protected void judgeMeterMode(Meter meter) {
    if (meter instanceof ArgyllDispMeter) {
      meterMode = MeterMode.Argyll;
    }
    else if (meter instanceof ShareMemoryMeter) {
      meterMode = MeterMode.Platform;
    }

  }

  public MeterMeasurement(AppMeasureParameter mp) {
    this(mp.meter, mp.dicomMode, mp.size, mp.calibration);

    this.setDo255InverseMode(mp.inverseMode);
//    this.setBlank(mp.blank);
    this.setBlankAndBackground(mp.blank, this.background);
    this.setBlankTimes( (int) mp.blankTimes);
    this.setDoBlankInsert(mp.blankInsert);
    this.setWaitTimes( (int) mp.delayTimes);
  }

  private MeterMeasurement(Meter meter, DICOM dicom, double LCDSize,
                           boolean calibration) {
    this.meter = meter;
    this.waitTimes = meter.getSuggestedWaitTimes();
    judgeMeterMode(meter);

    if (dicom != null && dicom != DICOM.None) {
      measureWindow = new MeasureWindow(dicom);
    }
    else {
      if (LCDSize != 0) {
        Dimension d = getSize(meter.getType(), LCDSize);
        measureWindow = new MeasureWindow(d.width * 2, d.height * 2);
      }
      else {
        measureWindow = new MeasureWindow();
      }
    }
    measureWindow.setWindowsInvisibleListener(this);

//    measureWindow.addWindowListener(new MeasureWindowListener());
    if (calibration) {
      MeasureUtils.meterCalibrate(this);
    }
  }

  public MeterMeasurement(Meter meter, double LCDSize, boolean calibration) {
    this(meter, null, LCDSize, calibration);
  }

  public MeterMeasurement(Meter meter, boolean calibration) {
    this(meter, null, 0, calibration);
  }

  public MeterMeasurement(Meter meter, DICOM dicom, boolean calibration) {
    this(meter, dicom, 0, calibration);
  }

  public void calibrate() {
    MeasureUtils.meterCalibrate(this);
  }

  public final static MeterMeasurement getDummyInstance() {
    DummyMeter dummy = new DummyMeter();
    MeterMeasurement mm = new MeterMeasurement(dummy, false);
    return mm;
  }

  private double[] unSoftCalXYZValues = null;

  protected double[] softCalibrate(double[] XYZValues) {
    if (this.softCalibrate) {
      unSoftCalXYZValues = XYZValues;
      CIEXYZ XYZ = new CIEXYZ(XYZValues);
      double scale = (XYZ.Y - measuredBlack.Y) * gain + offset +
          measuredBlack.Y;
      XYZ.scaleY(scale);
      double[] result = XYZ.getValues();
      return result;
    }
    else {
      return XYZValues;
    }
  }

  /**
   *
   * @param referenceBlack CIEXYZ
   * @param referenceWhite CIEXYZ
   * @deprecated 暫時不建議使用
   */
  public void softCalibrate(CIEXYZ referenceBlack, CIEXYZ referenceWhite) {
    Patch black = this.measure(RGB.Black, null);
    measuredBlack = black.getXYZ();
    Patch white = this.measure(RGB.White, null);
    offset = referenceBlack.Y - black.getXYZ().Y;
    gain = (referenceWhite.Y - offset) / (white.getXYZ().Y - offset);
    measureWindowsAutoClose();
    softCalibrate = true;
  }

  private CIEXYZ measuredBlack;
  private double offset;
  private double gain;
  private boolean softCalibrate = false;
  private boolean autoClose = true;

  /**
   * 關閉
   */
  public void close() {
    setMeasureWindowsVisible(false);
    measureWindow.dispose();
    meterClose();
  }

  protected void meterClose() {
    meter.close();
  }

  /**
   *  是否已經設定過title名稱
   */
  private boolean titleTouched = false;

  public void setTitle(String title) {
    titleTouched = true;
    this.measureWindow.setTitle(title);
  }

//  public void setVisible(boolean b) {
//      super.setVisible(b);
//    }

  /*private boolean measureWindowClosing = false;
     protected class MeasureWindowListener
      extends WindowAdapter {

    public void windowClosing(WindowEvent e) {
      if (e.getWindow() == measureWindow && e.getOppositeWindow() == null) {
        measureWindowClosing = true;
      }
    };
     }*/

  protected MeasureWindow measureWindow;
  protected Meter meter;
  public final static int DefaultWaitTimes = 300;
  private int waitTimes = DefaultWaitTimes;
  public final static int DefaultBlankTimes = 17;
  private int blankTimes = DefaultBlankTimes;

  public void setWaitTimes(int waitTimes) {
    this.waitTimes = waitTimes;
  }

  public Patch measure(RGB rgb, String patchName) {
    return measure0(rgb, patchName, null, null);
  }

  /**
   * 處理反轉的rgb
   * @param rgbArray RGB[]
   * @return RGB[]
   */
  private RGB[] processInverseRGB(RGB[] rgbArray) {
    if (do255InverseMode) {
      int size = rgbArray.length;
      for (int x = 0; x < size; x++) {
        rgbArray[x] = processInverseRGB(rgbArray[x]);
      }
    }
    return rgbArray;
  }

  private RGB processInverseRGB(RGB rgb) {
    if (do255InverseMode) {
      RGB result = (RGB) rgb.clone();
      result.changeMaxValue(RGB.MaxValue.Double255);
      //如果要處理255反轉的狀態
      //如果有channel的值飽和了(==255)
      result.R = result.R == 255 ? 254 : result.R;
      result.G = result.G == 255 ? 254 : result.G;
      result.B = result.B == 255 ? 254 : result.B;
      return result;
    }
    else {
      return rgb;
    }
  }

  /**
   *
   * @param rgb RGB 量測的RGB色塊
   * @param patchName String 量測色塊的名字, 可以為null
   * @param titleNote String 標題的註記
   * @param timeNote String 量測時間的註記
   * @return Patch
   */
  protected Patch measure0(RGB rgb, String patchName, String titleNote,
                           String timeNote) {
    setMeasureWindowsVisible(true);
    if (isAbnormalMode()) {
      JOptionPane.showMessageDialog(null, "unsupport operation in Argyll meter",
                                    "Unsupport operation",
                                    JOptionPane.WARNING_MESSAGE);
    }

    //量測的顏色, 量測的顏色可能與導具的顏色不同, 所以特別獨立出此變數
    final RGB measureRGB = processInverseRGB(rgb);

    if (doBlankInsert) {
      //如果要插放電畫面
      Color c = isCPCodeLoading() ? cpBlank : blank;
      measureWindow.setColor(c);
      try {
        Thread.sleep(blankTimes);
      }
      catch (InterruptedException ex) {
        Logger.log.error("", ex);
      }

    }

    String name = (patchName == null) ? rgb.toString() : patchName;
    if (!titleTouched) {
      //如果title沒被設定過
      if (titleNote != null) {
        measureWindow.setTitle("Measure Window " + titleNote);
      }
      else {
        measureWindow.setTitle("Measure Window");
      }
      measureWindow.setNorthLabel1(name + "   " + rgb.toString() + "   " +
                                   titleNote);
    }

    //設定好顏色
    Color c = this.isCPCodeLoading() ? this.cpBackground : background;
    measureWindow.setBackground(c);
    measureWindow.setColor(measureRGB.getColor());

    //==========================================================================
    // 變換完視窗顏色的短暫停留
    //==========================================================================
    if (!fakeMeasure) {
      try {
        Thread.sleep(this.waitTimes);
      }
      catch (InterruptedException ex) {
        Logger.log.error("", ex);
      }
    }

    //==========================================================================

    if (meter instanceof DummyMeter) {
      //如果是dummy, 就直接指定RGB, 由dummy轉換成XYZ
      ( (DummyMeter) meter).setRGB(measureRGB);
    }
    if (!fakeMeasure && (!measureWindow.isVisible() || measureWindowInvisible)) {
      measureWindowInvisible = false;
      this.setMeasureWindowsVisible(false);
      //如果視窗被關閉, 就結束量測
      return null;
    }
    double[] result = meter.triggerMeasurementInXYZ();
    if (softCalibrate) {
      result = softCalibrate(result);
    }

    String measureString = getMeasureString(result);
    measureWindow.setNorthLabel3(measureString +
                                 (softCalibrate ?
                                  (" (" +
                                   DoubleArray.toString(unSoftCalXYZValues) +
                                   " )") : ""));
    if (informationProvider != null) {
      String info = informationProvider.getInformation();
      measureWindow.setSouthLabel(info);
    }
    if (timeNote != null) {
      measureWindow.setNorthLabel2(timeNote);
    }
    CIEXYZ XYZ = new CIEXYZ(result);
    Patch patch = new Patch(name, XYZ, null, rgb);
    return patch;
  }

  public static interface InformationProvider {
    public String getInformation();
  }

  private InformationProvider informationProvider;

  private final static DecimalFormat df3 = new DecimalFormat("####.###");
  private final static DecimalFormat df4 = new DecimalFormat("####.####");

  protected final static String getMeasureString(double[] XYZValues) {
    CIEXYZ XYZ = new CIEXYZ(XYZValues);
    StringBuilder buf = new StringBuilder("<html>");
    buf.append("XYZ: ");
    buf.append(XYZ.toString(df3));
    buf.append("<br>");

    CIExyY xyY = new CIExyY(XYZ);
    buf.append("  xyY: ");
    buf.append(xyY.toString(df4));
    buf.append("<br>");

    double[] uvp = xyY.getuvPrimeValues();
    buf.append("  u'v': ");
    buf.append(DoubleArray.toString(df4, uvp));
    buf.append("<br>");

    buf.append("  CCT: ");
    buf.append(df3.format(CorrelatedColorTemperature.XYZ2CCTByRobertson(XYZ)));
    buf.append("<br>");

    buf.append("  CCT(VESA): ");
    buf.append(df3.format(CorrelatedColorTemperature.xy2CCTByMcCamy(xyY)));
    buf.append("<br>");

    return buf.toString();
  }

  /**
   *
   * @param rgbList List
   * @param patchNameList List
   * @return List
   */
  protected List<Patch> platformMeasurement(List<RGB> rgbList,
      List<String> patchNameList) {
    int size = rgbList.size();
    List<Patch> patchList = new ArrayList<Patch> (size);
    RGB[] rgbArray = RGBArray.toRGBArray(rgbList);
    rgbArray = processInverseRGB(rgbArray);
    ShareMemoryMeter platformMeter = ( (ShareMemoryMeter) meter);
    CIEXYZ[] XYZArray = platformMeter.measure(rgbArray);
    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = XYZArray[x];

      String name = null;
      if (patchNameList != null) {
        name = patchNameList.get(x);
      }
      int index = x + 1;
      name = (name == null) ? "A" + index : name;

      RGB rgb = rgbList.get(x);
      Patch patch = new Patch(name, XYZ, null, rgb);
      patchList.add(patch);
    }
    return patchList;
  }

  protected List<Patch> argyllMeasurement(List<RGB> rgbList,
      List<String> patchNameList) {
    int size = rgbList.size();
    List<Patch> patchList = new ArrayList<Patch> (size);
    double[][] results = ( (ArgyllDispMeter) meter).triggerMeasurementInXYZ(
        rgbList);
    for (int x = 0; x < size; x++) {
      double[] result = results[x];
      CIEXYZ XYZ = new CIEXYZ(result);

      String name = null;
      if (patchNameList != null) {
        name = patchNameList.get(x);
      }
      int index = x + 1;
      name = (name == null) ? "A" + index : name;

      RGB rgb = rgbList.get(x);
      Patch patch = new Patch(name, XYZ, null, rgb);
      patchList.add(patch);
    }
    return patchList;
  }

  private boolean do255InverseMode = false;
  public void setDo255InverseMode(boolean do255InverseMode) {
    this.do255InverseMode = do255InverseMode;
  }

  public void setDoBlankInsert(boolean doBlankInsert) {
    this.doBlankInsert = doBlankInsert;
  }

  /**
   * 插黑畫面的顏色
   * @param blank Color
   * @deprecated
   */
  public void setBlank(Color blank) {
    this.blank = blank;
  }

  /**
   * 插黑的時間
   * @param blankTimes int
   */
  public void setBlankTimes(int blankTimes) {
    this.blankTimes = blankTimes;
  }

  private boolean doBlankInsert = false;
  private Color blank = Color.black;
  private Color cpBlank = Color.black;
  private Color background = Color.black;
  private Color cpBackground = Color.black;

  public final boolean isDo255InverseMode() {
    return do255InverseMode;
  }

  public Meter getMeter() {
    return meter;
  }

  public MeasureWindow getMeasureWindow() {
    return measureWindow;
  }

  /**
   *
   * @return Color
   */
  public Color getBlank() {
    return blank;
  }

  public boolean isFakeMeasure() {
    return fakeMeasure;
  }

  public void setBlankAndBackground(Color blank, Color background) {
    this.blank = blank;
    this.background = background;
  }

  public List<Patch> measure(LCDTarget lcdTarget) {
    return measure(lcdTarget.filter.rgbList(), lcdTarget.filter.nameList());
  }

  /**
   * 大量色塊量測的確認
   */
  private boolean longMeasureVerify = false;

  /**
   * 大量色塊量測的確認
   * @param verify boolean
   */
  public void setLongMeasureVerify(boolean verify) {
    this.longMeasureVerify = verify;
  }

  public void setSoftCalibrate(boolean softCalibrate) {
    this.softCalibrate = softCalibrate;
  }

  public void setInformationProvider(shu.cms.measure.MeterMeasurement.
                                     InformationProvider informationProvider) {
    this.informationProvider = informationProvider;
  }

  public void setAutoClose(boolean autoClose) {
    this.autoClose = autoClose;
  }

  public void setCPBlankAndBackground(Color blank, Color background) {
    this.cpBlank = cpBlank;
    this.cpBackground = cpBackground;
  }

  /**
   * 大量色塊量測的確認
   * @param patches int
   * @return boolean
   */
  protected boolean longMeasureVerify(int patches) {
    if (longMeasureVerify) {
      int result = JOptionPane.showConfirmDialog(measureWindow,
                                                 "Continue?",
                                                 "Start measuring " +
                                                 patches +
                                                 " patches.",
                                                 JOptionPane.OK_CANCEL_OPTION,
                                                 JOptionPane.
                                                 INFORMATION_MESSAGE);
      if (result == JOptionPane.CANCEL_OPTION) {
        setMeasureWindowsVisible(false);
        return false;
      }
      else {
        return true;
      }
    }
    return true;
  }

  /**
   *
   * @param rgbList List 量測的RGB色塊
   * @param patchNameList List 量測色塊的名字, 可以為null
   * @return List 量測的Patch
   */
  public List<Patch> measure(List<RGB> rgbList, List<String> patchNameList) {
    setMeasureWindowsVisible(true);

    if (!longMeasureVerify(rgbList.size())) {
      setMeasureWindowsVisible(false);
      //大量色塊量測的確認
      return null;
    }

    PowerPolicy.pausePowerPolicyAndScreenSaver();
    if (patchNameList != null && rgbList.size() != patchNameList.size()) {
      throw new IllegalArgumentException(
          "rgbList.size() != patchNameList.size(");
    }
    int size = rgbList.size();
    List<Patch> patchList = null;
    if (isAbnormalMode()) {
      //========================================================================
      // 量測儀器並非直接控制的狀況下 (透過其他程式去控制儀器)
      //========================================================================
      switch (meterMode) {
        case Argyll:
          patchList = argyllMeasurement(rgbList, patchNameList);
          break;
        case Platform:
          patchList = platformMeasurement(rgbList, patchNameList);
          break;
      }
      //========================================================================
    }
    else {
      //========================================================================
      // 自主控制儀器, 因此需自行控制量測色塊
      //========================================================================
      patchList = new ArrayList<Patch> (size);
      long start = System.currentTimeMillis();
      for (int x = 0; x < size; x++) {
        String name = null;
        if (patchNameList != null) {
          name = patchNameList.get(x);
        }
        int index = x + 1;
        name = (name == null) ? "A" + index : name;
        int elapsed = (int) ( (System.currentTimeMillis() - start) / 1000);
        int remain = (int) ( ( (double) elapsed) / x * (size - x));
        String timeNote = "elapsed:" + elapsed + "(s)     estimate remain:" +
            remain + "(s)";
        Patch p = measure0(rgbList.get(x), name, index + "/" + size, timeNote);
        if (p == null) {
          return patchList;
        }
        patchList.add(p);
      }
      //========================================================================`
    }
    PowerPolicy.restorePowerPolicyAndScreenSaver();
    measureWindowsAutoClose();
    return patchList;

  }

  private void measureWindowsAutoClose() {
    if (this.autoClose) {
      setMeasureWindowsVisible(false);
    }
  }

  private boolean measureWindowInvisible = false;

  public void setMeasureWindowsVisible(boolean visible) {
    if (!isAbnormalMode() && !fakeMeasure && !measureWindowInvisible) {
      measureWindow.setVisible(visible);
//      measureWindowInvisible = !visible;
//      measureWindowClosing = !visible;
    }
  }

  public static void main(String[] args) {
//    DummyMeter meter = new DummyMeter();
    RemoteMeter meter = RemoteMeter.getDefaultInstance();
    MeterMeasurement mm = new MeterMeasurement(meter, false);
//    MeterMeasurement mm2 = new MeterMeasurement(meter, false);
//    System.out.println(mm.equals(mm));
//    System.out.println(mm.equals(mm2));
//    mm.measure(new RGB(RGB.ColorSpace.unknowRGB, new int[] {10, 20, 30}), null);
    RGB[] rgbArray1 = new RGB[] {
        new RGB(0, 0, 0), new RGB(253, 253, 253)};
    RGB[] rgbArray2 = new RGB[] {
        new RGB(0, 0, 0), new RGB(252, 252, 252), new RGB(253, 253, 253)};
    RGB[] rgbArray3 = new RGB[] {
        new RGB(0, 0, 0), new RGB(251, 251, 251), new RGB(252, 252, 252),
        new RGB(253, 253, 253)};
    RGB[] rgbArray4 = new RGB[] {
        new RGB(0, 0, 0), new RGB(254, 254, 254), new RGB(253, 253, 253)};
    RGB[][] rgbArrayArray = new RGB[][] {
        rgbArray1, rgbArray2, rgbArray3, rgbArray4};
    for (RGB[] rgbArray : rgbArrayArray) {
//      RGB[] rgbArray = rgbArray1;
      List<Patch> patchList = mm.measure(RGBArray.toRGBList(rgbArray), null);
//      for (Patch p : patchList) {
//        System.out.println(p);
//      }
      System.out.println(patchList.get(patchList.size() - 1));
    }
  }

  /**
   *
   * @return Color
   */
  public Color getBackgroundColor() {
    return background;
  }

  private boolean isCPCodeLoading() {
    return cpm != null ? cpm.isCPCodeLoading() : false;
  }

  private CPCodeMeasurement cpm;
  public void setCPCodeMeasurement(CPCodeMeasurement cpm) {
    this.cpm = cpm;
  }

  public void windowsInvisible() {
    measureWindowInvisible = true;
  }
}
