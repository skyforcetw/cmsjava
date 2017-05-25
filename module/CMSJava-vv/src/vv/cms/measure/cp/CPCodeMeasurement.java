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
 * ���Jcp code��rom��, �åB�i��q��
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
   * �O�_�n�z�LĲ�o�~�i��q��
   */
  private boolean nonTriggerMode = false;

  public void storeBuffer(String filename) {
    Persistence.writeObject(buffer, filename);
  }

  public void storeBufferAsXML(String filename) {
    Persistence.writeObjectAsXML(buffer, filename);
  }

  /**
   * cpm���غc��
   * @param mm MeterMeasurement �Ψ��X�ʶq������������
   * @param ic MaxValue ic��bit��
   * @param bufferFilename String �Ȧs�q���ƭȪ��ɮצW��
   */

  /**
   * cpm���غc��
   * @param mm MeterMeasurement MeterMeasurement �Ψ��X�ʶq������������
   * @param ic MaxValue ic��bit��
   * @param bufferFilename String �Ȧs�q���ƭȪ��ɮצW��
   * @param dummyMode boolean �O�_�bdummy���A�U? �p�G�O�h���|���Jcpcode, �����Jcpcode
   * �N���|��LCDModel��DIsplayLUT����ʪ��ʧ@, �T�Omulti-thread safe
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
   * �]�w�O�_�n��buffer�h�x�s���q�L�����
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
      //�T��global��handlers
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
        Logger.log.error("�L�k���o�t�Τu��C");
      } //�u��C�ϥ�

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
   * �q�����|���̤j�ƶq
   */
  private final static int MeasureStackMaximum = 254;
  private MeasureBuffer buffer = new MeasureBuffer(MeasureBuffer.BufferMode.
      CIEXYZ);

  /**
   * �q��rgb
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
   * �O�_�w�g�S��task�ݭn����?
   * @return boolean
   */
  public boolean isTaskEmpty() {
    synchronized (waitTaskList) {
      return waitTaskList.size() == 0;
    }
  }

  /**
   * �̪�@���q���ݨD���ɶ�
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
   * �q��rgbList
   * @param rgbList List
   * @param forceTrigger boolean �O�_�j��Ĳ�o�q��
   * @return List
   */
  public MeasureResult measureResult(List<RGB> rgbList, boolean forceTrigger) {
    return measureResult(rgbList, forceTrigger, true);
  }

  /**
   * �����q�����i�J�q�����ݧǦC
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
   * �q��rgbList
   * @param rgbList List
   * @param forceTrigger boolean �O�_�j��Ĳ�o�q��
   * @param trigger boolean �O�_�n�i��Ĳ�o (�YforceTrigger��true, �h���ﶵ�w�]��true)
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
   * �@�w�ݭn�q�����ƶq(�L�k�qbuffer��쪺�ƶq)
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
   * �P�_�O�_�nĲ�o�q��
   * @param forceTrigger boolean ���קP�_���G, �j��Ĳ�o
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
      // �নRGB�åB���Jcp code
      //========================================================================
      boolean all8bit = isAll8BitCode(request.rgbList);
      LCDTarget lcdTarget = null;

      if (dummyMode || all8bit) {
        //�p�G����8bit, �N���θ�code, ��H��l��cp code, �M�᪽�����ܦ�����ƭȨӹF��q��
        if (!dummyMode && all8bit) {
          CPCodeLoader.loadOriginal(ic);
        }
        lcdTarget = LCDTarget.Measured.measure(mm, nonDummyRGBList, nameList);
      }
      else {
        //�뺡256��RGB
        RGB[] cpcodeRGBArray = toRGBArray(request);
        Color blank = new Color(request.blankIndex, request.blankIndex,
                                request.blankIndex);
        Color background = new Color(request.backgroundIndex,
                                     request.backgroundIndex,
                                     request.backgroundIndex);
        mm.setBlankAndBackground(blank, background);
        mm.setCPBlankAndBackground(blank, background);

        //���J��cp table�z
        if (!CPCodeLoader.load(cpcodeRGBArray, ic)) {
          throw new IllegalStateException("CP Code loading fail.");
        }

        //�]���h��@�ӷ�I��&blank, �ҥHsize�n+2
        //���ͥX�Ӫ�rgbList�|�۰ʲ��L�I����&blank
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
     * �󴫬�
     * triggerMeasure0->loadCPCodeAndMeasure2->loadCPCodeAndMeasureInMaxMeasureItems
     *
     * �hcode�����bloadCPCodeAndMeasureInMaxMeasureItems�o�@�h�B�z.
     */

    /**
     *
     * @param measureRGBList List
     * @return List
     */
    private List<Patch> loadCPCodeAndMeasure(List<RGB> measureRGBList) {
      //==========================================================================
      // �q��
      //==========================================================================
      if (measureRGBList.size() == 0) {
        return null;
      }

      int size = measureRGBList.size();
      List<Patch> patchList = new ArrayList<Patch> (size);

      for (int x = 0; x < size; ) {
        int end = x + MeasureStackMaximum;
        //�p�Gend�W�Xsize, �վ��Psize�P
        end = end > size ? size : end;
        boolean available = false;
        MeasureRequest request = null;
        for (; !available; end--) {
          request = filler.getMeasureRequestAndAddBackgroundBlank(
              measureRGBList, x, end);
          available = isCPLoadAvailable(request.rgbList) ||
              isAll8BitCode(request.rgbList);
          if (!available) {
            // ������J, �N�ɶq��
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
     * ���ͥΨ�é�Fmeasure��RGB List
     * @param size int
     * @return List
     */
    private List<RGB> produceRGBList(int size) {
      size = size > BackgroundIndex ? size + 1 : size;
      size = size > BlankIndex ? size + 1 : size;
      List<RGB> rgbList = new ArrayList<RGB> (size);
      for (int x = 0; x < size; x++) {
        if (x == BackgroundIndex || x == BlankIndex) {
          //�I���Ⲥ�L���n�q��
          continue;
        }
        RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, new int[] {x, x, x});
        rgbList.add(rgb);
      }
      return rgbList;
    }

    /**
     * rgbList�নRGB�}�C, �åB���LBackgroundIndex�MBlankIndex���.
     * �]��BackgroundIndex����O�Ψӷ�@�I����ܥΪ�, BlankIndex�O�ΰ�����.
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
          //�̫�@�ӭY�S���Q�Ψ�, �˦����դ�K��ܤ�r.
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
     * ���Jcp code�åB�q��, �åB�̦h�q��254��code
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
      // �নRGB�åB���Jcp code
      //========================================================================
      boolean all8bit = isAll8BitCode(measureRGBList);
      LCDTarget lcdTarget = null;

      if (dummyMode || all8bit) {
        //�p�G����8bit, �N���θ�code, ��H��l��cp code, �M�᪽�����ܦ�����ƭȨӹF��q��
        if (all8bit) {
          CPCodeLoader.loadOriginal(ic);
        }
        lcdTarget = LCDTarget.Measured.measure(mm, measureRGBList, nameList);
      }
      else {
        //�뺡256��RGB
        RGB[] measureRGBArray = toRGBArray(measureRGBList,
                                           mm.getBackgroundColor(),
                                           mm.getBlank());
//        mm.setBlankAndBackground(blankColor, getOldFashionBackground());
        mm.setCPBlankAndBackground(blankColor, getOldFashionBackground());

        //���J��cp table�z
        if (!CPCodeLoader.load(measureRGBArray, ic)) {
          throw new IllegalStateException("CP Code loading fail.");
        }

        //�]���h��@�ӷ�I��&blank, �ҥHsize�n+2
        //���ͥX�Ӫ�rgbList�|�۰ʲ��L�I����&blank
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
     * ���Jcp code�åB�q��
     * @param measureRGBList List
     * @return List
     */
    private List<Patch> loadCPCodeAndMeasure(List<RGB> measureRGBList) {
      //==========================================================================
      // �q��
      //==========================================================================
      if (measureRGBList.size() == 0) {
        return null;
      }

      int size = measureRGBList.size();
      if (size <= MeasureStackMaximum) {
        //========================================================================
        // �নRGB�åB���Jcp code
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
     * ��Jfill�����, �Ϧ���i�H��cp loading.
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
     * ���odummy�}�C
     * @param rgbList List �Ҧ���RGB List
     * @param dummyRGBList List �䤤��Dummy RGB List
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
     * ���X�������e, �åB�[�W�I���Mblank, �æ^��MeasureRequest
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
        //���]�t�I��, �N�[�I��
        result.add(meterBackground);
        sort(result);
      }
      boolean containBlank = binarySearch(result, meterBlank) >= 0;
      if (!containBlank) {
        //���]�tblank, �N�[blank
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
   * �O���F�q�������, �H�ΰ����(���ݶq��), �I���H��blank�����ޭ�
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
   * �O�_rgbList�����Ҧ�rgb���O8bit���
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
   * �]�wpatch��rgb�H��name.
   * �]��LCDTarget.Measured.measure�q���^�Ӫ�RGB���Ƕ�, �n��������ڪ�RGB�~���T.
   * �ܩ�name, ������CPM�U���q���Ǹ�, ��K����X�Ӷq�L�X���C��.
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
   * �ֿn�q��������
   */
  private volatile int accumulateMeasureCount = 0;
  /**
   * �q����index��
   */
  private int measureIndex = 1;

  /**
   * Ĳ�o�q��
   * @param measureTrigger MeasureTrigger �ΨӽT�{�O�_�n�~��Ĳ�o������
   */
  public void triggerMeasure(MeasureTrigger measureTrigger) {
    try {
      //========================================================================
      // �T�w�u���}�l��task�A�~��U�h
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
   * triggerMeasure��, �ܤ֭nMinWaitTime�S������s���q���ݨD�i�J, �~�}�l�i��q��
   */
  private static long MaxWaitTime = 300;
  public final static void setMaxWaitTime(int maxWaitTime) {
    MaxWaitTime = maxWaitTime;
  }

  /**
   * �q���L�{��, �U���ˬd���ɶ����j��
   */
  private final static long CheckInterval = 10;
  private final static RGBLumiComparator rgbComparator = RGBLumiComparator.
      getInstance();

  /**
   * Ĳ�o�q��, �w�]�Ҧ�
   * @return boolean �O�_���i��q��
   */
  public boolean triggerMeasure() {
    return triggerMeasure0(false);
  }

  /**
   * �p�G�q����v����@�b, �N�h���@�����ɶ�, ���L�R��
   * @return long ���ݮɶ�
   */
  private long getWaitTime() {
    int mustMeasureCount = getMustMeasureCount();
    double measureRatio = ( (double) mustMeasureCount) / MeasureStackMaximum;
    //�p�G�q����v����@�b, �N�h���@�����ɶ�, ���L�R��
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
   * �P�O�O�_����@�b���q�����
   */
  private boolean halfMeasureRatio = false;

  /**
   * request�W�ᱼ���ɶ�
   */
  private long totalRequestTime;
  /**
   * �S���q���ݨD�N��Ĳ�o�q��(���n�ϥ�)
   * �S���q���ݨD���ɭ�, �٬O�ݭn�q��Ĳ�o. ���MĲ�o���ᤣ�|�i��q��, ���O�|��W�@���q������
   * �G�^�Ǧ^�h. �ҥH����]���S���q���ݨD�N��Ĳ�o.
   */
  private boolean nonMeasureWhenNoCount = false;

  private boolean judgeMeasure0(boolean forceTrigger) {
    int mustMeasureCount = getMustMeasureCount();
    if (true == nonMeasureWhenNoCount && mustMeasureCount == 0) {
      return false;
    }
    //==========================================================================
    // �p�G�٦�������measure���I�s, �ܤ֭n��300ms�H�W�~��Ĳ�o
    // �����O�ɶq��processTaskList�뺡�A�}�l�q��
    //==========================================================================
    //�p�G�q����v����@�b, �N�h���@�����ɶ�, ���L�R��
    long waitTime = getWaitTime();
    long now = System.currentTimeMillis();
    nearestRequestTime = (nearestRequestTime == 0) ? now : nearestRequestTime;
    long requestTime = now - nearestRequestTime;

    if (!forceTrigger && requestTime < waitTime) {
      //�p�G�S���j��Ĳ�o, �N�����ˬd��request�ɶ��W�XwaitTime�~�i��q��
      //�N��N�O���ܤ֭n����waitTime���ɶ���~
      return false;
    }
    totalRequestTime += requestTime;
    //==========================================================================
    return true;
  }

  /**
   * Ĳ�o�q��
   * @param forceTrigger boolean
   * @return boolean �O�_���u���i��q��
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
        //�q���ݶq�����ǦC��, ��ݭn�q��������L�o�X��.
        List<RGB> rgbList = task.rgbList;
        //�ݭn�q�������
        List<RGB> canMeasureList = buffer.getMustMeasureRGBList(rgbList);
        int size = canMeasureList.size();

        total += size;
        //�N�Ҧ��ݭn�q���������z�X��
        measureRGBList.addAll(canMeasureList);
        //�[��w�g�B�̪��ǦC
        processTaskList.add(task);
      }

      //==========================================================================
      // �N���b�B�z���q���ݪ�����
      //==========================================================================
      for (MeasureTask task : processTaskList) {
        waitTaskList.remove(task);
      }
      //==========================================================================
    }

    //==========================================================================
    // �q��
    //==========================================================================
    //���̷�sRGB�G�ױƧ�
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
    // �q��
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
   * �O�_�ĥΰ����t�����q����k, �åB�t�X���ͥi��IC������rom
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
   * �N�q�����G���o�X�h
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
          //�n�O�S���s�A��, �u�n��w�s
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
   * ���o�C���q�����ƪ��}�C
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
   * ���ݶq�����M��
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
   * �]�w�O�_�n�z�LĲ�o�~�i��q��
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
