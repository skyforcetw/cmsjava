package vv.cms.measure.cp;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.*;

import java.awt.*;
import java.awt.event.*;

import org.apache.commons.collections.primitives.*;
import shu.cms.*;
import shu.cms.colorformat.logo.*;
import shu.cms.colorspace.depend.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import vv.cms.lcd.calibrate.parameter.*;
import shu.cms.lcd.material.*;
import shu.cms.measure.*;
import vv.cms.measure.cp.msg.*;
import shu.cms.measure.meter.*;
import shu.cms.util.*;
import shu.util.*;
import shu.util.log.Logger;
import vv.cms.lcd.material.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 載入cp code到rom裡, 並且進行量測
 *
 * measure->measureResult->judgeTrigger->triggerMeasure0->loadCPCodeAndMeasure
 * ->loadCPCodeAndMeasureInMaxMeasureItems->LCDTarget.Measured.measure
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 * @deprecated
 */
public class CPCodeMeasurement
    implements MeasureInterface {

  public final static boolean isCPCodeLoading() {
    return CPCodeLoader.isCPCodeLoading();
  }

  private static java.util.logging.Logger jdk14log = Logger.
      getDefaultFileLogger(
          "cpm", "log" + File.separator + "cpm.log");

  private void log(List<Patch> patchList) {
    int size = patchList.size();
    StringBuilder buf = new StringBuilder();
    buf.append("Measure sequence: (count " + size + ")\n");
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      buf.append(p.toString() + "\n");
    }
    jdk14log.info(buf.toString());
  }

  /**
   * 是否要透過觸發才進行量測
   */
  private boolean nonTriggerMode = false;

  public void storeBuffer(String filename) {
    Persistence.writeObject(buffer, filename);
  }

  public void storeBufferAsXML(String filename) {
    Persistence.writeObjectAsXML(buffer, filename);
  }

  /**
   * cpm的建構式
   * @param mm MeterMeasurement 用來驅動量測儀器的物件
   * @param ic MaxValue ic的bit數
   * @param bufferFilename String 暫存量測數值的檔案名稱
   */

  /**
   * cpm的建構式
   * @param mm MeterMeasurement MeterMeasurement 用來驅動量測儀器的物件
   * @param ic MaxValue ic的bit數
   * @param bufferFilename String 暫存量測數值的檔案名稱
   * @param dummyMode boolean 是否在dummy狀態下? 如果是則不會載入cpcode, 不載入cpcode
   * 就不會對LCDModel的DIsplayLUT有更動的動作, 確保multi-thread safe
   */
  private CPCodeMeasurement(MeterMeasurement mm, RGB.MaxValue ic,
                            String bufferFilename, boolean dummyMode) {
    setMeterMeasurement(mm);
    this.ic = ic;
    this.dummyMode = dummyMode;

    if (bufferFilename != null && new File(bufferFilename).exists()) {
      buffer = (MeasureBuffer) Persistence.readObject(bufferFilename);
    }
    if (ShowTryIcon) {
      tray = new Tray(mm.getMeter().getType().name());
    }
//    buffer.setBufferMeasure(bufferMeasure);
  }

  /**
   * 設定是否要用buffer去儲存測量過的色塊
   * @param bufferMeasure boolean
   */
  public void setBufferMeasure(boolean bufferMeasure) {
    buffer.setBufferMeasure(bufferMeasure);
  }

  private Tray tray;

  private class Tray
      implements ActionListener {
    private java.util.logging.Logger measurelog = Logger.getDefaultLogger(
        "cpm-measure");
    private java.util.logging.Logger triggerlog = Logger.getDefaultLogger(
        "cpm-trigger");
    private String meterName;

    private Tray(String meterName) {
      this.meterName = meterName;
      initTrayIcon();
      initLog();

    }

    private void measureInfo(String msg) {
      measurelog.info(msg);
    }

    private void triggerInfo(String msg) {
      triggerlog.info(msg);
    }

    private CPMMessageFrame frame;

    private void initLog() {
      //禁用global的handlers
      measurelog.setUseParentHandlers(false);
      triggerlog.setUseParentHandlers(false);
      frame = new CPMMessageFrame();

      MessageFrameHandler measureHandler = new MessageFrameHandler(frame.
          getMeasureLoggerInterface());
      MessageFrameHandler triggerHandler = new MessageFrameHandler(frame.
          getTriggerLoggerInterface());

      measurelog.addHandler(new MemoryHandler(measureHandler, 10, Level.ALL));
      triggerlog.addHandler(new MemoryHandler(triggerHandler, 10, Level.ALL));
    }

    private void initTrayIcon() {
      if (SystemTray.isSupported()) {
        SystemTray tray = SystemTray.getSystemTray();
        clockIcon = Toolkit.getDefaultToolkit()
            .getImage(CPCodeMeasurement.class.getResource(
                "clock.png"));
        trayIcon =
            new TrayIcon(clockIcon, "CPCode Measurement: " + meterName);
        trayIcon.addActionListener(this);
        try {
          tray.add(trayIcon);
        }
        catch (AWTException e) {
          e.printStackTrace();
        }
      }
      else {
        Logger.log.error("無法取得系統工具列");
      } //工具列圖示

    }

    private TrayIcon trayIcon;
    private Image clockIcon;
    /**
     * Invoked when an action occurs.
     *
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
      frame.setVisible(true);
    }
  }

  private final static boolean ShowTryIcon = AutoCPOptions.get(
      "CPM_ShowTrayIcon");

  private void setMeterMeasurement(MeterMeasurement mm) {
    this.mm = mm;
//    this.mm.setCPCodeMeasurement(this);
    this.meterBlank = new RGB(RGB.ColorSpace.unknowRGB, mm.getBlank());
    this.meterBackground = new RGB(RGB.ColorSpace.unknowRGB,
                                   mm.getBackgroundColor());
  }

  private RGB meterBlank;
  private RGB meterBackground;

  private boolean dummyMode = false;

  public final static CPCodeMeasurement getInstance(MeterMeasurement mm,
      RGB.MaxValue ic, String bufferFilename) {
    return getInstance(mm, ic, bufferFilename, null);
  }

  public final static CPCodeMeasurement getInstance(MeterMeasurement mm,
      RGB.MaxValue ic, String bufferFilename, MeasureParameter mp) {
    boolean dummyMode = mm.getMeter() instanceof DummyMeter || mm.isFakeMeasure();
    CPCodeMeasurement cpm = new CPCodeMeasurement(mm, ic, bufferFilename,
                                                  dummyMode);
    addToInstanceList(cpm);
    if (mp != null) {
      cpm.setBufferMeasure(mp.bufferMeasure);
      cpm.setAcceptDifference(mp.CPCodeAcceptDifference);
      cpm.setCheckDifference(mp.useDifferenceMeasure);
      cpm.setUseDifferenceMeasure(mp.useDifferenceMeasure);
      cpm.setWaitTime(mp.downloadWaitTime);
    }
    return cpm;
  }

  private static void addToInstanceList(CPCodeMeasurement cpm) {
    instanceList.add(cpm);
  }

  private static List<CPCodeMeasurement> instanceList = new ArrayList<
      CPCodeMeasurement> ();

  public final static CPCodeMeasurement getInstance(LCDModel model,
      boolean fakeMeasure) {
    DummyMeter meter = new DummyMeter(model);
    MeterMeasurement mm = new MeterMeasurement(meter, false);
    mm.setFakeMeasure(fakeMeasure);
    CPCodeMeasurement cpm = getInstance(mm, RGB.MaxValue.DoubleUnlimited, null);
    cpm.setNonTriggerMode(true);
    addToInstanceList(cpm);
    return cpm;
  }

  private MeterMeasurement mm;
  private RGB.MaxValue ic;
  /**
   * 量測堆疊的最大數量
   */
  private final static int MeasureStackMaximum = 254;
  private MeasureBuffer buffer = new MeasureBuffer(MeasureBuffer.BufferMode.
      CIEXYZ);

  /**
   * 量測rgb
   * @param rgb RGB
   * @return Patch
   */
  public Patch measure(RGB rgb) {
    return measure(rgb, false);
  }

  public Patch measure(RGB rgb, boolean forceTrigger) {
    return measure(rgb, forceTrigger, true);
  }

  public Patch measure(RGB rgb, boolean forceTrigger, boolean trigger) {
    List<RGB> rgbList = new ArrayList<RGB> (1);
    rgbList.add(rgb);
    return measure(rgbList, forceTrigger, trigger).get(0);
  }

  /**
   * 是否已經沒有task需要執行?
   * @return boolean
   */
  public boolean isTaskEmpty() {
    synchronized (waitTaskList) {
      return waitTaskList.size() == 0;
    }
  }

  /**
   * 最近一次量測需求的時間
   */
  private long nearestRequestTime = 0;

  private final static boolean hasNull(List<RGB> rgbList) {
    for (RGB rgb : rgbList) {
      if (rgb == null) {
        return true;
      }
    }
    return false;
  }

  public List<Patch> measure(List<RGB> rgbList) {
    return measure(rgbList, false);
  }

  public List<Patch> measure(RGB[] rgbArray) {
    return measure(RGBArray.toRGBListAndCheck(rgbArray), false);
  }

  public List<Patch> measure(RGB[] rgbArray, boolean forceTrigger) {
    return measure(RGBArray.toRGBListAndCheck(rgbArray), forceTrigger);
  }

  public MeasureResult measureResult(RGB[] rgbArray, boolean forceTrigger) {
    return measureResult(RGBArray.toRGBListAndCheck(rgbArray),
                         forceTrigger);
  }

  public MeasureResult measureResult(RGB[] rgbArray, boolean forceTrigger,
                                     boolean trigger) {
    return measureResult(RGBArray.toRGBListAndCheck(rgbArray),
                         forceTrigger, trigger);
  }

  public List<Patch> measure(List<RGB> rgbList, boolean forceTrigger) {
    return measureResult(rgbList, forceTrigger).result;
  }

  public List<Patch> measure(List<RGB> rgbList, boolean forceTrigger,
      boolean trigger) {
    return measureResult(rgbList, forceTrigger, trigger).result;
  }

  /**
   * 量測rgbList
   * @param rgbList List
   * @param forceTrigger boolean 是否強制觸發量測
   * @return List
   */
  public MeasureResult measureResult(List<RGB> rgbList, boolean forceTrigger) {
    return measureResult(rgbList, forceTrigger, true);
  }

  /**
   * 直接量測不進入量測等待序列
   * @param rgbList List
   * @return MeasureResult
   */
  public MeasureResult directMeasureResult(List<RGB> rgbList) {
    List<Patch> patchList = measure0(rgbList);
    MeasureResult result = new MeasureResult(patchList,
                                             patchList.size());
    return result;
  }

  /**
   * 量測rgbList
   * @param rgbList List
   * @param forceTrigger boolean 是否強制觸發量測
   * @param trigger boolean 是否要進行觸發 (若forceTrigger為true, 則此選項預設為true)
   * @return MeasureResult
   */
  public MeasureResult measureResult(List<RGB> rgbList, boolean forceTrigger,
      boolean trigger) {
    if (hasNull(rgbList)) {
      throw new IllegalArgumentException("rgbList has null");
    }
    MeasureTask task = new MeasureTask(rgbList);
    nearestRequestTime = System.currentTimeMillis();
    synchronized (waitTaskList) {
      waitTaskList.add(task);
    }

    FutureTask<MeasureResult> futureTask = new FutureTask<MeasureResult> (task);
    Thread t = new Thread(futureTask);
    t.start();

    if (this.ShowTryIcon) {
      tray.measureInfo("size: " + rgbList.size() + ", forceTrigger: " +
                       (forceTrigger ? "T" : "F") + ", trigger: " +
                       (trigger ? "T" : "F"));
    }

    if (forceTrigger || trigger) {
      judgeTrigger(forceTrigger);
    }

    try {
      return futureTask.get();
    }
    catch (Exception ex) {
      Logger.log.error("", ex);
      return null;
    }
  }

  public static void example1(String[] args) {
    Meter meter = RemoteMeter.getDefaultInstance();
    MeterMeasurement mm = new MeterMeasurement(meter, false);
    final CPCodeMeasurement cpm = new CPCodeMeasurement(mm,
        RGB.MaxValue.Int10Bit, null, false);

//    for (int x = 0; x < 257; x++) {
//      new Thread() {
//        public void run() {
//          System.out.println(cpm.measure(new RGB(RGB.ColorSpace.unknowRGB,
//                                                 new double[] {Math.random(),
//                                                 Math.random(), Math.random()},
//                                                 RGB.MaxValue.Double1)));
//        }
//      }.start();
//    }
//    cpm.triggerMeasure(true);
  }

  /**
   * 一定需要量測的數量(無法從buffer找到的數量)
   * @return int
   */
  private int getMustMeasureCount() {
    int count = 0;
    synchronized (waitTaskList) {
      for (MeasureTask task : waitTaskList) {
        task.canMeasureRGBList = buffer.getMustMeasureRGBList(task.rgbList);
        count += task.canMeasureRGBList.size();
      }
    }
    return count;
  }

  /**
   * 判斷是否要觸發量測
   * @param forceTrigger boolean 不論判斷結果, 強制觸發
   */
  protected void judgeTrigger(boolean forceTrigger) {
    int count = getMustMeasureCount();
    if (nonTriggerMode || forceTrigger || count >= MeasureStackMaximum) {
      triggerMeasure0(nonTriggerMode || forceTrigger);
    }
  }

  public final static Color getOldFashionBackground() {
    Color bg = new Color(OldFashion.BackgroundIndex,
                         OldFashion.BackgroundIndex, OldFashion.BackgroundIndex);
    return bg;
  }

  private Fashion fashion = new Fashion();
  private class Fashion {
    private final List<RGB> produceRGBList(MeasureRequest request) {
      if (request.rgbList.size() > MaxRGBCodeCount) {
        throw new IllegalArgumentException(
            "request.measureRGBList.size() > MaxRGBCodeCount(" +
            MaxRGBCodeCount + ")");
      }

      boolean[] dummyIndex = request.dummyIndex;
      List<RGB> rgbList = new ArrayList<RGB> (request.getNonDummyCount());
      for (int x = 0; x < dummyIndex.length; x++) {
        boolean b = dummyIndex[x];
        if (false == b) {
          RGB rgb = new RGB(x, x, x);
          rgbList.add(rgb);
        }
      }

      return rgbList;
    }

    private final RGB[] toRGBArray(MeasureRequest request) {
      if (request.rgbList.size() > MaxRGBCodeCount) {
        throw new IllegalArgumentException(
            "request.rgbList.size() > MaxRGBCodeCount(" + MaxRGBCodeCount +
            ")");
      }
      RGB[] result = new RGB[MaxRGBCodeCount];
      int index = 0;
      for (RGB rgb : request.rgbList) {
        result[index++] = rgb;
      }
      RGB lastRGB = result[index - 1];
      for (int x = index; x < MaxRGBCodeCount; x++) {
        result[x] = (RGB) lastRGB.clone();
        result[x].addValues(1);
        result[x].rationalize();
        lastRGB = result[x];
      }
      return result;
    }

    private List<Patch> loadCPCodeAndMeasureInMaxMeasureItems(MeasureRequest
        request) {

      if (request.rgbList.size() > MaxRGBCodeCount) {
        throw new IllegalArgumentException(
            "request.rgbList.size() > MaxRGBCodeCount(" + MaxRGBCodeCount +
            ")");
      }

      int size = request.getNonDummyCount();
      accumulateMeasureCount += size;
      List<RGB> nonDummyRGBList = request.getNonDummyRGBList();
      List<String> nameList = toNameList(nonDummyRGBList);
      //========================================================================
      // 轉成RGB並且載入cp code
      //========================================================================
      boolean all8bit = isAll8BitCode(request.rgbList);
      LCDTarget lcdTarget = null;

      if (dummyMode || all8bit) {
        //如果全部8bit, 就不用載code, 改以原始的cp code, 然後直接改變色塊的數值來達到量測
        if (!dummyMode && all8bit) {
          CPCodeLoader.loadOriginal(ic);
        }
        lcdTarget = LCDTarget.Measured.measure(mm, nonDummyRGBList, nameList);
      }
      else {
        //塞滿256個RGB
        RGB[] cpcodeRGBArray = toRGBArray(request);
        Color blank = new Color(request.blankIndex, request.blankIndex,
                                request.blankIndex);
        Color background = new Color(request.backgroundIndex,
                                     request.backgroundIndex,
                                     request.backgroundIndex);
        mm.setBlankAndBackground(blank, background);
        mm.setCPBlankAndBackground(blank, background);

        //載入到cp table理
        if (!CPCodeLoader.load(cpcodeRGBArray, ic)) {
          throw new IllegalStateException("CP Code loading fail.");
        }

        //因為多塞一個當背景&blank, 所以size要+2
        //產生出來的rgbList會自動略過背景色&blank
        List<RGB> rgbList = produceRGBList(request);
        lcdTarget = LCDTarget.Measured.measure(mm, rgbList, nameList);
      }
      //========================================================================

      List<Patch> patchList = lcdTarget.getPatchList();
      setPatchNameAndRGB(patchList, nonDummyRGBList);

      buffer.putToBuffer(patchList); ;
      measureHistory.addAll(patchList);
      log(patchList);
      return patchList;
    }

    /**
     * triggerMeasure0->loadCPCodeAndMeasure->loadCPCodeAndMeasureInMaxMeasureItems
     * 更換為
     * triggerMeasure0->loadCPCodeAndMeasure2->loadCPCodeAndMeasureInMaxMeasureItems
     *
     * 退code必須在loadCPCodeAndMeasureInMaxMeasureItems這一層處理.
     */

    /**
     *
     * @param measureRGBList List
     * @return List
     */
    private List<Patch> loadCPCodeAndMeasure(List<RGB> measureRGBList) {
      //==========================================================================
      // 量測
      //==========================================================================
      if (measureRGBList.size() == 0) {
        return null;
      }

      int size = measureRGBList.size();
      List<Patch> patchList = new ArrayList<Patch> (size);

      for (int x = 0; x < size; ) {
        int end = x + MeasureStackMaximum;
        //如果end超出size, 調整到與size同
        end = end > size ? size : end;
        boolean available = false;
        MeasureRequest request = null;
        for (; !available; end--) {
          request = filler.getMeasureRequestAndAddBackgroundBlank(
              measureRGBList, x, end);
          available = isCPLoadAvailable(request.rgbList) ||
              isAll8BitCode(request.rgbList);
          if (!available) {
            // 不能載入, 就盡量填滿
            request = filler.fill(request);
          }
          if (request.getSize() <= MaxRGBCodeCount) {
            available = true;
          }
        }
        List<Patch> subResult = loadCPCodeAndMeasureInMaxMeasureItems(request);
        patchList.addAll(subResult);
        x += (end - x) + 1;
      }

      return patchList;
      //==========================================================================
    }

  }

  private OldFashion oldFashion = new OldFashion();
  private class OldFashion {
    public final static int BackgroundIndex = 0;
    public final static int BlankIndex = 1;
    private final Color blankColor = new Color(BlankIndex, BlankIndex,
                                               BlankIndex);

    /**
     * 產生用來矇騙measure的RGB List
     * @param size int
     * @return List
     */
    private List<RGB> produceRGBList(int size) {
      size = size > BackgroundIndex ? size + 1 : size;
      size = size > BlankIndex ? size + 1 : size;
      List<RGB> rgbList = new ArrayList<RGB> (size);
      for (int x = 0; x < size; x++) {
        if (x == BackgroundIndex || x == BlankIndex) {
          //背景色略過不要量測
          continue;
        }
        RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, new int[] {x, x, x});
        rgbList.add(rgb);
      }
      return rgbList;
    }

    /**
     * rgbList轉成RGB陣列, 並且略過BackgroundIndex和BlankIndex色塊.
     * 因為BackgroundIndex色塊是用來當作背景顯示用的, BlankIndex是用做插黑.
     *
     * @param rgbList List
     * @param background Color
     * @param blank Color
     * @return RGB[]
     */
    private final RGB[] toRGBArray(List<RGB> rgbList, Color background,
        Color blank) {
      if (rgbList.size() > MeasureStackMaximum) {
        throw new IllegalArgumentException(
            "rgbList.size() > MeasureStackMaximum(" + MeasureStackMaximum + ")");
      }
      RGB[] result = new RGB[MaxRGBCodeCount];
      int index = 0;
      for (RGB rgb : rgbList) {
        if (index == BackgroundIndex) {
          index++;
        }
        if (index == BlankIndex) {
          index++;
        }
        result[index++] = rgb;
      }
      for (int x = index; x < MaxRGBCodeCount; x++) {
        if (x == (MaxRGBCodeCount - 1)) {
          //最後一個若沒有被用到, 弄成全白方便顯示文字.
          result[MaxRGBCodeCount - 1] = RGB.White;
        }
        else {
          result[x] = RGB.Black;
        }
      }
      result[BackgroundIndex] = new RGB(RGB.ColorSpace.unknowRGB, background);
      result[BlankIndex] = new RGB(RGB.ColorSpace.unknowRGB, blank);
      return result;
    }

    /**
     * 載入cp code並且量測, 並且最多量測254個code
     * @param measureRGBList List
     * @return List
     */
    private List<Patch> loadCPCodeAndMeasureInMaxMeasureItems(List<RGB>
        measureRGBList) {
      if (measureRGBList.size() > MeasureStackMaximum) {
        throw new IllegalArgumentException(
            "measureRGBList.size() > MeasureStackMaximum(" +
            MeasureStackMaximum +
            ")");
      }

      int size = measureRGBList.size();
      accumulateMeasureCount += size;
      List<String> nameList = toNameList(measureRGBList);
      //========================================================================
      // 轉成RGB並且載入cp code
      //========================================================================
      boolean all8bit = isAll8BitCode(measureRGBList);
      LCDTarget lcdTarget = null;

      if (dummyMode || all8bit) {
        //如果全部8bit, 就不用載code, 改以原始的cp code, 然後直接改變色塊的數值來達到量測
        if (all8bit) {
          CPCodeLoader.loadOriginal(ic);
        }
        lcdTarget = LCDTarget.Measured.measure(mm, measureRGBList, nameList);
      }
      else {
        //塞滿256個RGB
        RGB[] measureRGBArray = toRGBArray(measureRGBList,
                                           mm.getBackgroundColor(),
                                           mm.getBlank());
//        mm.setBlankAndBackground(blankColor, getOldFashionBackground());
        mm.setCPBlankAndBackground(blankColor, getOldFashionBackground());

        //載入到cp table理
        if (!CPCodeLoader.load(measureRGBArray, ic)) {
          throw new IllegalStateException("CP Code loading fail.");
        }

        //因為多塞一個當背景&blank, 所以size要+2
        //產生出來的rgbList會自動略過背景色&blank
        List<RGB> rgbList = produceRGBList(size);
        lcdTarget = LCDTarget.Measured.measure(mm, rgbList, nameList);
      }
      //========================================================================

      List<Patch> patchList = lcdTarget.getPatchList();
      setPatchNameAndRGB(patchList, measureRGBList);

      buffer.putToBuffer(patchList); ;
      measureHistory.addAll(patchList);
      log(patchList);
      return patchList;
    }

    /**
     * 載入cp code並且量測
     * @param measureRGBList List
     * @return List
     */
    private List<Patch> loadCPCodeAndMeasure(List<RGB> measureRGBList) {
      //==========================================================================
      // 量測
      //==========================================================================
      if (measureRGBList.size() == 0) {
        return null;
      }

      int size = measureRGBList.size();
      if (size <= MeasureStackMaximum) {
        //========================================================================
        // 轉成RGB並且載入cp code
        //========================================================================
        return loadCPCodeAndMeasureInMaxMeasureItems(measureRGBList);
      }
      else {
        List<Patch> patchList = new ArrayList<Patch> (size);

        for (int x = 0; x < size; x += MeasureStackMaximum) {
          int end = x + MeasureStackMaximum;
          end = end > size ? size : end;
          List<RGB> subList = measureRGBList.subList(x, end);
          List<Patch> subResult = loadCPCodeAndMeasureInMaxMeasureItems(subList);
          patchList.addAll(subResult);
        }

        return patchList;
      }

      //==========================================================================
    }

  }

  private class Filler {
    /**
     * 填入fill假色塊, 使色塊可以讓cp loading.
     * @param request MeasureRequest
     * @return MeasureRequest
     */
    private MeasureRequest fill(MeasureRequest request) {
      List<RGB> rgbList = request.rgbList;
      List<RGB> allAddRGBList = new ArrayList<RGB> ();
      int addition = AcceptDifference - 1;
      int measureSize = request.getNonDummyCount();

      while (!isCPLoadAvailable(rgbList)) {
        List<RGB> addRGBList = new ArrayList<RGB> ();
        List<RGB> newRGBList = new ArrayList<RGB> (rgbList.size());
        for (int x = 0; x < rgbList.size() - 1; x++) {
          RGB rgb = rgbList.get(x);
          RGB nextrgb = rgbList.get(x + 1);
          double dR = rgb.R - nextrgb.R;
          double dG = rgb.G - nextrgb.G;
          double dB = rgb.B - nextrgb.B;

          boolean procR = Math.abs(dR) > AcceptDifference;
          boolean procG = Math.abs(dG) > AcceptDifference;
          boolean procB = Math.abs(dB) > AcceptDifference;

          newRGBList.add(rgb);

          if (procR || procG || procB) {
            RGB newRGB = (RGB) rgb.clone();
            if (procR) {
              newRGB.R = (dR > 0) ? newRGB.R - addition : newRGB.R + addition;
            }

            if (procG) {
              newRGB.G = (dG > 0) ? newRGB.G - addition : newRGB.G + addition;
            }

            if (procB) {
              newRGB.B = (dB > 0) ? newRGB.B - addition : newRGB.B + addition;
            }
            newRGB.quantization(RGB.MaxValue.Int8Bit);
            addRGBList.add(newRGB);
            newRGBList.add(newRGB);
          }
        }
        newRGBList.add(rgbList.get(rgbList.size() - 1));
        rgbList = newRGBList;
        allAddRGBList.addAll(addRGBList);
      }

      List<RGB> dummyRGBList = request.getDummyRGBList();
      dummyRGBList.addAll(allAddRGBList);
      boolean[] dummyIndex = getDummyIndex(rgbList, dummyRGBList);

      MeasureRequest result = new MeasureRequest(rgbList, dummyIndex,
                                                 meterBackground, meterBlank);
      jdk14log.info("filling to " + dummyRGBList.size() + " dummy in " +
                    measureSize +
                    " measure.");
      return result;
    }

    /**
     * 取得dummy陣列
     * @param rgbList List 所有的RGB List
     * @param dummyRGBList List 其中的Dummy RGB List
     * @return boolean[]
     */
    private boolean[] getDummyIndex(List<RGB> rgbList, List<RGB> dummyRGBList) {
      int size = rgbList.size();
      boolean[] dummyIndex = new boolean[size];
      for (RGB rgb : dummyRGBList) {
        int index = sequenceSearch(rgbList, rgb);
        dummyIndex[index] = true;
      }
      return dummyIndex;
    }

    /**
     * 取出部份內容, 並且加上背景和blank, 並回傳MeasureRequest
     * @param rgbList List
     * @param fromIndex int
     * @param toIndex int
     * @return MeasureRequest
     */
    private MeasureRequest getMeasureRequestAndAddBackgroundBlank(List<RGB>
        rgbList, int fromIndex, int toIndex) {
      List<RGB> subList = rgbList.subList(fromIndex, toIndex);
      List<RGB> result = new ArrayList<RGB> (subList.size() + 2);
      result.addAll(subList);
      boolean containBackground = binarySearch(result, meterBackground) >= 0;
      if (!containBackground) {
        //不包含背景, 就加背景
        result.add(meterBackground);
        sort(result);
      }
      boolean containBlank = binarySearch(result, meterBlank) >= 0;
      if (!containBlank) {
        //不包含blank, 就加blank
        result.add(meterBlank);
        sort(result);
      }
//      sort(result);
      int size = result.size();
      boolean[] dummyIndex = new boolean[size];
      int backgroundIndex = binarySearch(result, meterBackground);
      int blankIndex = binarySearch(result, meterBlank);
      if (!containBackground) {
        dummyIndex[backgroundIndex] = true;
      }
      if (!containBlank) {
        dummyIndex[blankIndex] = true;
      }
      MeasureRequest request = new MeasureRequest(result, dummyIndex,
                                                  backgroundIndex, blankIndex);
      return request;
    }

  }

  private Filler filler = new Filler();
  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 記錄了量測的色塊, 以及假色塊(不需量測), 背景以及blank的索引值
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  private static class MeasureRequest {
    private List<RGB> rgbList;
    private boolean[] dummyIndex;
    private int backgroundIndex;
    private int blankIndex;
    private int nonDummyCount;

    private int getNonDummyCount() {
      int nonDummyCount = 0;
      for (boolean b : dummyIndex) {
        if (false == b) {
          nonDummyCount++;
        }
      }
      return nonDummyCount;
    }

    private int getSize() {
      return rgbList.size();
    }

    private List<RGB> getDummyRGBList() {
      List<RGB> rgbList = new ArrayList<RGB> ();
      for (int x = 0; x < dummyIndex.length; x++) {
        if (true == dummyIndex[x]) {
          RGB rgb = this.rgbList.get(x);
          rgbList.add(rgb);
        }
      }
      return rgbList;
    }

    private List<RGB> getNonDummyRGBList() {
      List<RGB> dummyRGBList = getDummyRGBList();
      List<RGB> result = new ArrayList<RGB> (rgbList);
      result.removeAll(dummyRGBList);
      return result;
    }

    private MeasureRequest(List<RGB> measureRGBList, boolean[] dummyIndex,
        RGB background, RGB blank) {
      this(measureRGBList, dummyIndex, binarySearch(measureRGBList, background),
           binarySearch(measureRGBList, blank));
    }

    private MeasureRequest(List<RGB> measureRGBList, boolean[] dummyIndex,
        int backgroundIndex, int blankIndex) {
      if (measureRGBList.size() != dummyIndex.length) {
        throw new IllegalArgumentException(
            "measureRGBList.size() != dummyIndex.length");
      }
      this.rgbList = measureRGBList;
      this.dummyIndex = dummyIndex;
      this.backgroundIndex = backgroundIndex;
      this.blankIndex = blankIndex;
      this.nonDummyCount = this.getNonDummyCount();
    }

  }

  public static void main(String[] args) {
//    Meter meter = RemoteMeter.getDefaultInstance();
    Meter meter = new DummyMeter();
    MeterMeasurement mm = new MeterMeasurement(meter, false);
    mm.setFakeMeasure(true);
    CPCodeMeasurement cpm = CPCodeMeasurement.getInstance(mm,
        RGB.MaxValue.Int10Bit, null);

    List<RGB> rgbList = new ArrayList<RGB> ();

//    rgbList.add(new RGB(RGB.ColorSpace.unknowRGB, new double[] {0, 0, 0},
//                        RGB.MaxValue.Double255));
//    for (int x = 1; x < 250; x += 1) {
//      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, new double[] {1.5, 1, 1},
//                        RGB.MaxValue.Double255);
//      rgbList.add(rgb);
//    }
//    for (int x = 1; x < 250; x += 64) {
//      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, new double[] {x, x, x},
//                        RGB.MaxValue.Double255);
//      rgbList.add(rgb);
//    }
//    for (RGB rgb : rgbList) {
//      System.out.println(rgb);
//    }
    rgbList = LCDTargetBase.Instance.get(LCDTargetBase.Number.FourColor).filter.
        rgbList();
//    rgbList = LCDTargetBase.Instance.get(LCDTargetBase.Number.BlackAndWhite).
//        filter.rgbList();
    for (RGB rgb : rgbList) {
      rgb.addValues(0.5);
//      rgb.rationalize();
    }
    List<Patch> result = cpm.fashion.loadCPCodeAndMeasure(rgbList);
    for (Patch p : result) {
      System.out.println(p.getRGB());
    }
  }

  public static void setWaitTime(long waitTime) {
    CPCodeLoader.setWaitTime(waitTime);
  }

  public final static void setAcceptDifference(int acceptDifference) {
    AcceptDifference = acceptDifference;
    CPCodeLoader.setAcceptDifference(acceptDifference);
  }

  public final static void setCheckDifference(boolean check) {
    CPCodeLoader.setCheckDifference(check);
  }

  private static int AcceptDifference = 63;
  private boolean isCPLoadAvailable(final List<RGB> rgbList) {
    boolean checkResult = CPCodeLoader.checkCPCodeDifference(RGBArray.
        toRGBArray(rgbList), AcceptDifference);
    return checkResult;
  }

  /**
   * 是否rgbList中的所有rgb都是8bit整數
   * @param rgbList List
   * @return boolean
   */
  protected static boolean isAll8BitCode(List<RGB> rgbList) {
    boolean all8bit = true;
    List<RGB> rgbArrayList = new ArrayList<RGB> (rgbList);
    for (RGB rgb : rgbArrayList) {
      all8bit = all8bit && rgb.equalsAfterQuantization(RGB.MaxValue.Int8Bit);
    }
    return all8bit;
  }

  /**
   * 設定patch的rgb以及name.
   * 因為LCDTarget.Measured.measure量測回來的RGB為灰階, 要替換成實際的RGB才正確.
   * 至於name, 替換成CPM下的量測序號, 方便分辨出來量過幾個顏色.
   *
   * @param patchList List
   * @param rgbList List
   */
  private void setPatchNameAndRGB(List<Patch> patchList, List<RGB> rgbList) {
    if (patchList.size() != rgbList.size()) {
      throw new IllegalArgumentException("patchList.size() != rgbList.size()");
    }
    int size = rgbList.size();
    for (int x = 0; x < size; x++) {
      RGB rgb = rgbList.get(x);
      Patch p = patchList.get(x);
      Patch.Operator.setName(p, "A" + (measureIndex++));
      Patch.Operator.setRGB(p, rgb);
      Patch.Operator.setOriginalRGB(p, rgb);
    }

  }

  private static List<Patch> measureHistory = new ArrayList<Patch> ();

  public final static void storeMeasureHistory(String filename) throws
      IOException {
    LogoFile logoFile = new LogoFile(filename, measureHistory);
    logoFile.save();
    measureHistory = new ArrayList<Patch> ();
  }

  /**
   * 累積量測的次數
   */
  private volatile int accumulateMeasureCount = 0;
  /**
   * 量測的index值
   */
  private int measureIndex = 1;

  /**
   * 觸發量測
   * @param measureTrigger MeasureTrigger 用來確認是否要繼續做觸發的物件
   */
  public void triggerMeasure(MeasureTrigger measureTrigger) {
    try {
      //========================================================================
      // 確定真的開始有task再繼續下去
      //========================================================================
      while (isTaskEmpty()) {
        Thread.sleep(CheckInterval);
        Thread.yield();
      }
      //========================================================================
      boolean measured = false;
      int noMeasureButHasNextCount = 0;
      long maxNoMeasureCount = MaxWaitTime * 2 / CheckInterval;

      while ( ( (measured = triggerMeasure()) || true) &&
             measureTrigger.hasNextMeasure()) {
        Thread.sleep(CheckInterval);
        Thread.yield();
        if (measureTrigger.hasNextMeasure() && !measured) {
          noMeasureButHasNextCount++;
        }
//        if (noMeasureButHasNextCount > maxNoMeasureCount) {
//          Logger.log.trace("nomeasureButHasNextCount > " + maxNoMeasureCount);
//        }
      }
    }
    catch (InterruptedException ex) {
      Logger.log.error("", ex);
    }
  }

  /**
   * triggerMeasure中, 至少要MinWaitTime沒有任何新的量測需求進入, 才開始進行量測
   */
  private static long MaxWaitTime = 300;
  public final static void setMaxWaitTime(int maxWaitTime) {
    MaxWaitTime = maxWaitTime;
  }

  /**
   * 量測過程中, 各種檢查的時間間隔值
   */
  private final static long CheckInterval = 10;
  private final static RGBLumiComparator rgbComparator = RGBLumiComparator.
      getInstance();

  /**
   * 觸發量測, 預設模式
   * @return boolean 是否有進行量測
   */
  public boolean triggerMeasure() {
    return triggerMeasure0(false);
  }

  /**
   * 如果量測比率不到一半, 就多等一倍的時間, 等他充滿
   * @return long 等待時間
   */
  private long getWaitTime() {
    int mustMeasureCount = getMustMeasureCount();
    double measureRatio = ( (double) mustMeasureCount) / MeasureStackMaximum;
    //如果量測比率不到一半, 就多等一倍的時間, 等他充滿
    if (measureRatio < 0.5) {
      if (!halfMeasureRatio) {
        Logger.log.trace("measureRatio < 0.5, WaitTime = MaxWaitTime * 2 (" +
                         (MaxWaitTime * 2) + "msec)");
      }
      halfMeasureRatio = true;
      return MaxWaitTime * 2;
    }
    else {
      halfMeasureRatio = false;
      return MaxWaitTime;
    }
  }

  /**
   * 判別是否不到一半的量測比例
   */
  private boolean halfMeasureRatio = false;

  /**
   * request上花掉的時間
   */
  private long totalRequestTime;
  /**
   * 沒有量測需求就不觸發量測(不要使用)
   * 沒有量測需求的時候, 還是需要量測觸發. 雖然觸發之後不會進行量測, 但是會把上一次量測的結
   * 果回傳回去. 所以不能因為沒有量測需求就不觸發.
   */
  private boolean nonMeasureWhenNoCount = false;

  private boolean judgeMeasure0(boolean forceTrigger) {
    int mustMeasureCount = getMustMeasureCount();
    if (true == nonMeasureWhenNoCount && mustMeasureCount == 0) {
      return false;
    }
    //==========================================================================
    // 如果還有接收到measure的呼叫, 至少要等300ms以上才做觸發
    // 為的是盡量讓processTaskList塞滿再開始量測
    //==========================================================================
    //如果量測比率不到一半, 就多等一倍的時間, 等他充滿
    long waitTime = getWaitTime();
    long now = System.currentTimeMillis();
    nearestRequestTime = (nearestRequestTime == 0) ? now : nearestRequestTime;
    long requestTime = now - nearestRequestTime;

    if (!forceTrigger && requestTime < waitTime) {
      //如果沒有強制觸發, 就必須檢查讓request時間超出waitTime才進行量測
      //意思就是說至少要等滿waitTime的時間啦~
      return false;
    }
    totalRequestTime += requestTime;
    //==========================================================================
    return true;
  }

  /**
   * 觸發量測
   * @param forceTrigger boolean
   * @return boolean 是否有真正進行量測
   */
  protected synchronized boolean triggerMeasure0(boolean forceTrigger) {
    if (!judgeMeasure0(forceTrigger)) {
      return false;
    }
    int total = 0;
    List<RGB> measureRGBList = new LinkedList<RGB> ();
    List<MeasureTask> processTaskList = new LinkedList<MeasureTask> ();
    buffer.clearFreshBuffer();
    int waitTaskSize = waitTaskList.size();

    synchronized (waitTaskList) {
      for (MeasureTask task : waitTaskList) {
        //從等待量測的序列裡, 把需要量測的色塊過濾出來.
        List<RGB> rgbList = task.rgbList;
        //需要量測的色塊
        List<RGB> canMeasureList = buffer.getMustMeasureRGBList(rgbList);
        int size = canMeasureList.size();

        total += size;
        //將所有需要量測的色塊整理出來
        measureRGBList.addAll(canMeasureList);
        //加到已經處裡的序列
        processTaskList.add(task);
      }

      //==========================================================================
      // 將正在處理的從等待的移除
      //==========================================================================
      for (MeasureTask task : processTaskList) {
        waitTaskList.remove(task);
      }
      //==========================================================================
    }

    //==========================================================================
    // 量測
    //==========================================================================
    //先依照sRGB亮度排序
    sort(measureRGBList);
    measure0(measureRGBList);
    //==========================================================================

    int totalPracticalMeasureCount = measurementDispatch(processTaskList);
    if (this.ShowTryIcon) {
      this.tray.triggerInfo("waitTask(org): " + waitTaskSize +
                            ", procTask: " + processTaskList.size() +
                            ", meaRGB: " + measureRGBList.size() +
                            ", praticalMea: " + totalPracticalMeasureCount);
    }

    return measureRGBList.size() != 0;
  }

  private synchronized List<Patch> measure0(List<RGB> measureRGBList) {
    //==========================================================================
    // 量測
    //==========================================================================
    List<Patch> patchList = null;
    if (useDifferenceMeasure) {
      jdk14log.info("New fashion measuring...");
      patchList = fashion.loadCPCodeAndMeasure(measureRGBList);
    }
    else {
      jdk14log.info("Old fashion measuring...");
      patchList = oldFashion.loadCPCodeAndMeasure(measureRGBList);
    }
    if (patchList != null) {
      measureCountList.add(patchList.size());
    }
    //==========================================================================
    return patchList;
  }

  /**
   * 是否採用偵測差異的量測方法, 並且配合產生可供IC接受的rom
   */
  private boolean useDifferenceMeasure = false;

  private void sort(List<RGB> rgbList) {
    Collections.sort(rgbList, rgbComparator);
  }

  private final static int binarySearch(List<RGB> rgbList, RGB rgb) {
    return Collections.binarySearch(rgbList, rgb, rgbComparator);
  }

  private final static int sequenceSearch(List<RGB> rgbList, RGB rgb) {
    int size = rgbList.size();
    for (int x = 0; x < size; x++) {
      RGB item = rgbList.get(x);
      if (rgbComparator.compare(item, rgb) == 0) {
        return x;
      }
    }
    return -1;
  }

  /**
   * 將量測結果派發出去
   * @param processTaskList List
   * @return int totalPracticalMeasureCount
   */
  private int measurementDispatch(List<MeasureTask> processTaskList) {
    int totalPracticalMeasureCount = 0;
    for (MeasureTask task : processTaskList) {
      int size = task.rgbList.size();
      List<Patch> result = new ArrayList<Patch> (size);
      List<RGB> rgbList = task.rgbList;
      int practicalMeasureCount = 0;

      for (int x = 0; x < size; x++) {
        RGB rgb = rgbList.get(x);
        Patch p = buffer.getPatchFromFreshBuffer(rgb);
        if (p == null) {
          //要是沒有新鮮的, 只好找庫存
          p = buffer.getPatchFromBuffer(rgb);
          if (p == null) {
            throw new IllegalStateException("p == null");
          }
        }
        else {
          practicalMeasureCount++;
        }
        result.add(p);
      }
      task.patchList = result;
      task.practicalMeasureCount = practicalMeasureCount;
      totalPracticalMeasureCount += practicalMeasureCount;
    }
    return totalPracticalMeasureCount;

  }

  private IntList measureCountList = new ArrayIntList();

  /**
   * 取得每次量測次數的陣列
   * @return int[]
   */
  public int[] getMeasureCount() {
    return measureCountList.toArray();
  }

  public void reset() {
    this.buffer.clear();
  }

  private final static int MaxRGBCodeCount = 256;

  private final static List<String> toNameList(List<RGB> rgbList) {
    int size = rgbList.size();
    List<String> nameList = new ArrayList<String> (size);
    for (int x = 0; x < size; x++) {
      RGB rgb = rgbList.get(x);
      nameList.add("[CPCode] " + rgb.toString());
    }
    return nameList;
  }

  /**
   * 等待量測的清單
   */
  private List<MeasureTask> waitTaskList = Collections.synchronizedList(new
      LinkedList<MeasureTask> ());

  protected class MeasureTask
      implements Callable<MeasureResult> {
    protected MeasureResult getMeasureResult() {
      MeasureResult result = new MeasureResult(this.patchList,
                                               this.practicalMeasureCount);
      return result;
    }

    protected MeasureTask(List<RGB> rgbList) {
      this.rgbList = rgbList;
    }

    private int practicalMeasureCount;
    private List<RGB> rgbList;
    private List<RGB> canMeasureRGBList;
    private List<Patch> patchList;
    public MeasureResult call() throws Exception {
      while (patchList == null) {
        Thread.sleep(CheckInterval);
        Thread.yield();
      }
      return this.getMeasureResult();
    }
  }

  public int getAccumulateMeasureCount() {
    return accumulateMeasureCount;
  }

  /**
   * 設定是否要透過觸發才進行量測
   * @param nonTriggerMode boolean
   */
  public void setNonTriggerMode(boolean nonTriggerMode) {
    this.nonTriggerMode = nonTriggerMode;
  }

  public void setUseDifferenceMeasure(boolean useDifferenceMeasure) {
    this.useDifferenceMeasure = useDifferenceMeasure;
  }

  public final MeasureInterface getMeasureInterface() {
    final CPCodeMeasurement cpm = this;
    MeasureInterface mi = new MeasureInterface() {
      public MeasureResult measureResult(RGB[] rgbArray, boolean forceTrigger,
                                         boolean trigger) {
        return cpm.measureResult(rgbArray, forceTrigger, trigger);
      }

      public Patch measure(RGB rgb, boolean forceTrigger, boolean trigger) {
        return cpm.measure(rgb, forceTrigger, trigger);
      }

      public void triggerMeasure(MeasureTrigger measureTrigger) {
        cpm.triggerMeasure(measureTrigger);
      }

      public void reset() {
        cpm.reset();
      }

      public int[] getMeasureCount() {
        return cpm.getMeasureCount();
      }

      public Patch measure(RGB rgb) {
        return cpm.measure(rgb);
      }
    };
    return mi;
  }

}
