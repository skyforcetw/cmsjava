package shu.cms.lcd.calibrate.measured;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.lcd.calibrate.*;
import shu.cms.lcd.calibrate.measured.algo.*;
import shu.cms.lcd.calibrate.measured.find.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.cms.measure.*;
import shu.cms.measure.MeterMeasurement.*;
import shu.cms.measure.cp.*;
import shu.cms.plot.*;
import shu.util.log.*;
///import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 不斷變化RGB數值, 直到找到最接近色座標的校正方式
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CoordinateCalibrator
    extends MeasuredCalibrator implements InformationProvider,
    Thread.UncaughtExceptionHandler {

  protected AccessInstance access; // = new CalibratorInstance();
  public void uncaughtException(Thread t, Throwable e) {
    Logger.log.error("", e);
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 提供CIEuv1960FindingThread的必要資訊
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  protected class AccessInstance
      extends CalibratorAccessAdapter {

    /**
     *
     * @param white CIEXYZ
     * @param cpm CPCodeMeasurement
     * @param jndi JNDIInterface
     * @param quadrant DeltauvQuadrant
     * @param maxCode double
     * @param forceTrigger boolean
     * @deprecated
     */
    public AccessInstance(CIEXYZ white, CPCodeMeasurement cpm,
                          JNDIInterface jndi, DeltauvQuadrant quadrant,
                          double maxCode, boolean forceTrigger) {
      super(white, cpm, jndi, quadrant, maxCode, forceTrigger);
    }

    public AccessInstance(CIEXYZ white, MeasureInterface mi,
                          JNDIInterface jndi, DeltauvQuadrant quadrant,
                          double maxCode, boolean forceTrigger) {
      super(white, mi, jndi, quadrant, maxCode, forceTrigger);
    }

    private Index calIndex = Index.Luminance;
    public NearestAlgorithm getIndexNearestAlogorithm() {

      switch (calIndex) {
        case Luminance:
          return lightnessNearAlgo;
        case CIEuv1960:
          return uvNearAlgo;
        default:
          return null;
      }
    }

    public CIEuv1960NearestAlgorithm getCIEuv1960NearestAlogorithm() {
      return uvNearAlgo;
    }

    public void trace(String msg) {
      traceDetail(msg);
    }

    public CIExyY getTargetxyY(int index) {
      return getTargetxyYArray()[index];
    }

    public void addMaxAroundTouched() {
      addMaxAroundTouchedTimes();
    }

  }

  /**
   * 初始化
   * @param mm MeterMeasurement
   */
  private void init(MeterMeasurement mm) {
    mm.setInformationProvider(this);
  }

  public CoordinateCalibrator(LCDTarget logoLCDTaget,
                              MeterMeasurement meterMeasurement,
                              ColorProofParameter p,
                              WhiteParameter wp, AdjustParameter ap,
                              MeasureParameter mp) {
    super(logoLCDTaget, meterMeasurement, p, wp, ap, mp);
    init(meterMeasurement);
    this.parallelExcute = mp.parallelExcute;
  }

  /**
   * 儲存校正會用到的相關資訊
   */
  protected FindingInfo findingInfo = new FindingInfo();

  public Plot plot = new Plot();
  public class Plot {

    private final static boolean PlotStep = false;

    public void plotCalibratedIndex(Index index) {
      if (!plotting) {
        return;
      }

      double[] indexArray = null;
      switch (index) {
        case Luminance:
          indexArray = findingInfo.getDeltaJNDIIndexArray();
          break;
        case CIEuv1960:
          indexArray = findingInfo.getDeltauvpDistArray();
          break;
        case DeltaE:
          indexArray = findingInfo.getDeltaEIndexArray();
          break;
      }
      if (indexArray != null) {
        Plot2D plot = Plot2D.getInstance("Calibrated Index: " + index.name());
        plot.addLinePlot(index.name(), 0, 255, indexArray);
        plot.setAxeLabel(0, "code");
        plot.setAxeLabel(1, "index");
        plot.setFixedBounds(0, 0, 255);
        plot.setVisible();
      }
      else {
        Logger.log.info("indexArray == null");
      }
    }

  }

  protected boolean parallelExcute = true;

  /**
   * 演算法的初始化
   */
  private final void initAlgorithm() {
    //取白點
    white = this.getCalibratedTarget().getWhitePatch().getXYZ();
    //計算象限
    DeltauvQuadrant quadrant = white == null ? null :
        this.getBlackCIEuvQuadrant();

    access = new AccessInstance(white, mi, this.jndi, quadrant,
                                wp.maxWhiteCode, !parallelExcute);
    access.getCompoundNearestAlogorithm().setDeltauvPrimeTolerance(this.
        deltauvPrimeTolerance);

    //==========================================================================
    // 折衷演算法
    // 折衷演算法的初始化, 由於所謂的折衷, 是要找到最接近白點但是又跟黑點處於同一個象限.
    // 所以沒有白點的狀況下, 根本沒有折衷的必要.
    //==========================================================================
    access.getCIEuv1960NearestAlogorithm().setMode(white == null ?
        Algorithm.Mode.White : Algorithm.Mode.Normal);
    //==========================================================================

    if (chromaticExpandForChromaAround) {
      access.getChromaticAroundAlgorithm().setChromaticExpandMode(true);
    }
    if (chromaticExpandForStepAround) {
      access.getStepAroundAlgorithm().setChromaticExpandMode(true);
    }

  }

  /**
   * 色度尋找周邊點的時候, 是否進行擴張? (不擴張找四點, 擴張找八點)
   */
  private boolean chromaticExpandForChromaAround = false;

  /**
   * step尋找周邊點的時候, 對於色度的維度上, 是否進行擴張
   */
  private boolean chromaticExpandForStepAround = false;

  /**
   * 白點
   */
  protected CIEXYZ white;

  /**
   * 在最後一次做Around搜尋的時候, 用cube擴展
   */
  private boolean cubeAroundMode = true;

  //============================================================================
  // 演算法
  //============================================================================
//  private StepAroundAlgorithm stepAroundAlgo = new StepAroundAlgorithm(wp.
//      maxCode);
//  private ChromaticAroundAlgorithm chromaAroundAlgo = (this.wp == null) ?
//      new ChromaticAroundAlgorithm() :
//      new ChromaticAroundAlgorithm(wp.maxCode);

  /**
   * 以色差來計算最近點
   */
//  private DeltaE00NearestAlogorithm deltaENearAlgo;
  /**
   * 在色度跟亮度間做取捨
   */
//  private CompoundNearestAlogorithm compoundNearAlogo;
  /**
   * uv平面上的最近點(compromise功能目前無效)
   */
//  private CIEuv1960NearestAlogorithm uvNearAlgo;
  /**
   * JNDI上找最近點
   */
//  private LightnessNearestAlogorithm lightnessNearAlgo;
  //============================================================================

  /**
   * delta u'v'的容許值
   */
  private double deltauvPrimeTolerance = 0.0009;
  public void setDeltauvPrimeTolerance(double deltauvPrimeTolerance) {
    this.deltauvPrimeTolerance = deltauvPrimeTolerance;
  }

  public Info info = new Info();

  public class Info {

    /**
     * 總校正次數
     * @return int
     */
    public int getCalibrateCount() {
      return findingInfo.getCalibrateCount();
    }

    public int getNonQualifyTimes() {
      return findingInfo.getNonQualifyTimes();
    }

    public String getNonQualifyInfo() {
      return findingInfo.getNonQualifyInfo();
    }

    public int getCPLoadingCount() {
      return CPCodeLoader.getLoadingCount();
    }

    public int[] getMeasureCount() {
      return mi.getMeasureCount();
    }

    public int getCubeSearchTimes() {
      return findingInfo.getCubeSearchTimes();
    }

    /**
     * 完成的比例
     * @return double
     */
    public double getCompletePercentage() {
      return findingInfo.getCompletePercentage(maxValue);
    }

    /**
     * 以執行緒持續監督完成比例
     * @param interval long
     */
    public void excuteShowPercentageThread(final long interval) {
      new Thread() {
        private double lastPercent = -1;
        public void run() {
          try {
            while (true) {
              Thread.sleep(interval);
              double percent = getCompletePercentage();
              if (lastPercent != percent && percent != 0) {
                //有更新才顯示出來
                lastPercent = percent;
                System.out.println(percent + " %");
              }
              if (percent == 100) {
                break;
              }
            }
          }
          catch (InterruptedException ex) {
            ex.printStackTrace();
          }
        }
      }.start();

    }

  }

  /**
   * 對白點作校正
   */
  private final void whiteCalibrate() {
    if (wp.isDoWhiteCalibrate()) {
      WhitePointCalibrator wpc = new WhitePointCalibrator(this.logoLCDTaget, this);
      RGB whiteRGB = wpc.calibrate()[0];
      //把產生出來的校正白點, 替換掉到cp code RGBArray
      RGB[] cpcode = getCPCodeRGBArray();

      findingInfo.setCalibratedRGB(255, whiteRGB);
      cpcode[255] = whiteRGB;

    }
  }

  /**
   * 找到某個code的亮度以上, 對儀器來講是穩定的
   * @return int
   */
  private final int getStableCode() {
    CIExyY[] targetxyY = this.getTargetxyYArray();
    int size = targetxyY.length;
    for (int x = 0; x < size; x++) {
      CIExyY xyY = targetxyY[x];
      if (xyY.Y >= StableLuminance) {
        return x;
      }
    }
    return -1;
  }

  /**
   *  穩定的亮度
   */
  private final static double StableLuminance = 2.;

  /**
   * 取出不穩定亮度以下的calibrated array
   * @param stableCode int
   * @param calibrated boolean[]
   * @return boolean[]
   */
  protected boolean[] getUnstableCalibratedArray(int stableCode,
                                                 boolean[] calibrated) {
    boolean[] array = Arrays.copyOf(calibrated, stableCode);
    return array;
  }

  /**
   *  對CalibratedInfo內部作初始化
   */
  private final void initFindingInfo() {
    if (!findingInfo.isInit()) {
      findingInfo.init();
      if (this.calibrated == null) {
        boolean[] alreadyCalibrated = findingInfo.getAlreadyCalibrated();
        this.calibrated = Arrays.copyOf(alreadyCalibrated,
                                        alreadyCalibrated.length);
      }
      findingInfo.setInitRGBArray(getCPCodeRGBArray());
    }
  }

  /**
   * 校正初始化,包含:
   *  對白先做校正, 初始化校正, 演算法初始化, 尋找穩定亮度的code, 整數校正
   */
  protected final void initCalibrate() {

    initFindingInfo();

    /**
     * 對白先做校正 (每次皆不同)
     * 找到最接近目標值的白
     */
    whiteCalibrate();
    /**
     * 初始化校正 (每次皆不同)
     * 由此產生出新的目標值
     */
    initMeasure(RGBBase.Channel.W, false);
    /**
     * 演算法初始化 (每次皆不同)
     * 將校正所用到的演算法做適當的初始化
     */
    initAlgorithm();
    /**
     * 尋找穩定亮度的code (每次皆不同)
     * 找到對儀器來說, 量測結果為穩定的最低code
     */
    stableCode = getStableCode();
  }

  private int stableCode = -1;

  protected final void startCalibrate() {
    if (stableCode == -1) {
      throw new IllegalStateException(
          "\"stableCode == -1\", call initCalibrate() first.");
    }
    //==========================================================================
    // 先把不穩定的處理一下, 避免儀器開始不穩
    //==========================================================================
    boolean[] unstableCalibrated = getUnstableCalibratedArray(stableCode,
        findingInfo.getAlreadyCalibrated());
    calibrated0(unstableCalibrated, 1, stableCode);
    for (int x = 1; x < unstableCalibrated.length; x++) {
      findingInfo.setCalibrated(x, unstableCalibrated[x]);
    }
    //==========================================================================

    //==========================================================================
    // 把剩下的處裡完
    //==========================================================================
    calibrated0(findingInfo.getAlreadyCalibrated(), stableCode, 255);
    //==========================================================================

    //冗餘量測
    redundantMeasure = this.getRedundantMeasureFromThreadList();
  }

  private int redundantMeasure;

  /**
   * calibrate
   *
   * @return RGB[]
   */
  protected RGB[] _calibrate() {
    initCalibrate();
    startCalibrate();

    findingInfo.setAlreadyCalibrated(null);
    RGB[] result = findingInfo.getCalibratedRGBArray();
//    CalibrateUtils.storeRGBArrayExcel(result, rootDir + "/" + CordCalibrated);
    CalibrateUtils.storeRGBArrayExcel(result, rootDir + "/" + CordCalibrated,
                                      cp);
    quantizatioCollapseFix(result);
//    CalibrateUtils.storeRGBArrayExcel(result, rootDir + "/" + CordFinal);
    CalibrateUtils.storeRGBArrayExcel(result, rootDir + "/" + CordFinal, cp);
    return result;
  }

  /**
   * 所有校正Thread的List
   */
  private List<CIEuv1960FindingThread> threadList = new LinkedList<
      CIEuv1960FindingThread> ();

  protected void addToThreadList(CIEuv1960FindingThread thread) {
    threadList.add(thread);
  }

  protected String getThreadListTrace() {
    StringBuilder buf = new StringBuilder();
    for (CIEuv1960FindingThread t : threadList) {
      buf.append(t.getTrace());
      buf.append('\n');
    }
    return buf.toString();
  }

  private int getRedundantMeasureFromThreadList() {
    int redundantMeasure = 0;
    for (CIEuv1960FindingThread t : threadList) {
      redundantMeasure += t.getRedundantMeasure();
    }
    return redundantMeasure;
  }

  /**
   * 對start以及end之間進行calibrate, 並且以calibrated作為觸發量測用
   * @param calibrated boolean[]
   * @param start int
   * @param end int
   */
  protected void calibrated0(boolean[] calibrated, int start, int end) {
    for (int x = start; x < end; x++) {
      if (calibrated[x] == false) {
        CIEuv1960FindingThread t = new CIEuv1960FindingThread(x, maxValue,
            findingInfo, calibrated, this.luminanceCalibrate,
            this.chromaticCalibrate, this.cubeAroundMode, this.white,
            access, false);
        threadList.add(t);
        t.setUncaughtExceptionHandler(this);
        t.start();
      }
    }
    Trigger trigger = new Trigger(calibrated);
    if (trigger.hasNextMeasure()) {
      mi.triggerMeasure(trigger);
    }
  }

  /**
   * 是否要進行色度校正
   */
  protected boolean chromaticCalibrate = true;
  /**
   * 是否要進行亮度校正
   */
  protected boolean luminanceCalibrate = true;

  /**
   * 用來記錄接觸到MaxAroundIterativeTimes的次數
   */
  private int maxAroundTouchedTimes = 0;

  private void addMaxAroundTouchedTimes() {
    maxAroundTouchedTimes++;
  }

  private int getMaxAroundTouchedTimes() {
    return maxAroundTouchedTimes;
  }

  public int getRedundantMeasure() {
    return redundantMeasure;
  }

  public static enum Index {
    Luminance, CIEuv1960, DeltaE
  }

  /**
   * getCalibratedPatchList
   *
   * @return List
   */
  public List<Patch> getCalibratedPatchList() {
    return LCDTargetUtils.getReplacedPatchList(getCalibratedTarget(),
                                               findingInfo.
                                               getCalibratedRGBArray());
  }

  public void setCalibrated(boolean[] calibrated) {
    if (calibrated.length != 256) {
      throw new IllegalArgumentException("calibrated.length != 256");
    }
    this.calibrated = Arrays.copyOf(calibrated, calibrated.length);
    findingInfo.setAlreadyCalibrated(Arrays.copyOf(calibrated,
        calibrated.length));
  }

  protected boolean[] calibrated;

  /**
   * 是否要進行色度校正
   * @param chromaticCalibrate boolean
   */
  public void setChromaticCalibrate(boolean chromaticCalibrate) {
    this.chromaticCalibrate = chromaticCalibrate;
  }

  /**
   * 是否要以立方體作擴展? 讓更多點納入考量
   * @param cubeAroundMode boolean
   */
  public void setCubeAroundMode(boolean cubeAroundMode) {
    this.cubeAroundMode = cubeAroundMode;
  }

  public void setChromaticExpandForChromaAround(boolean chromaticExpand) {
    this.chromaticExpandForChromaAround = chromaticExpand;
  }

  public void setChromaticExpandForStepAround(boolean
                                              chromaticExpandForStepAround) {
    this.chromaticExpandForStepAround = chromaticExpandForStepAround;
  }

  /**
   * 取得黑點位於白點的哪個象限
   * @return DeltauvQuadrant
   */
  protected DeltauvQuadrant getBlackCIEuvQuadrant() {
    LCDTarget target = getCalibratedTarget();
    CIEXYZ white = target.getWhitePatch().getXYZ();
    CIEXYZ black = target.getBlackPatch().getXYZ();
    CIExyY whitexyY = new CIExyY(white);
    CIExyY blackxyY = new CIExyY(black);
    double[] deltauvp = blackxyY.getDeltauvPrime(whitexyY);
    DeltauvQuadrant quadrant = new DeltauvQuadrant(deltauvp[0] >= 0,
        deltauvp[1] >= 0);
    return quadrant;
  }

  /**
   * getInformation
   *
   * @return String
   */
  public String getInformation() {
    double percent = this.info.getCompletePercentage();
    int calCount = info.getCalibrateCount();
    int measureCount = this.getAccumulateMeasureCount();
    int elapsedTime = (int) (this.timeConsumption.getElapsedTime() / 1000);
    int loadingCount = info.getCPLoadingCount();
//    long totalRequestTime = getTotalRequestTime();

    String info = "<html>complete: " + percent + " %<br>" +
        "calibrate count: " + calCount + "<br>" +
        "measure count: " + measureCount + "<br>" +
        "elapsed time: " + elapsedTime + " (s)<br>" +
        "CP Loading count: " + loadingCount + "<br>"; // +
//        "TotalRequestTime: " + totalRequestTime + "<br>";
    return info;
  }

  public void close() {
    for (CIEuv1960FindingThread t : threadList) {
      t.closePlotter();
      t = null;
    }
    threadList.clear();
  }

  public String getCalibratedInfomation() {
    StringBuilder buf = new StringBuilder();

    buf.append("AccumulateMeasureCount: ");
    buf.append(getAccumulateMeasureCount());
    buf.append('\n');

    buf.append("MaxAroundTouchedTimes: ");
    buf.append(getMaxAroundTouchedTimes());
    buf.append('\n');

    buf.append("CubeSearchTimes: ");
    buf.append(info.getCubeSearchTimes());
    buf.append('\n');

    buf.append("NonQualifyTimes: ");
    buf.append(info.getNonQualifyTimes());
    buf.append('\n');

    buf.append("NonQualifyInfo: ");
    buf.append(info.getNonQualifyInfo());
    buf.append('\n');

    buf.append("CPLoadingCount: ");
    buf.append(info.getCPLoadingCount());
    buf.append('\n');

    buf.append("CPM MeasureCount: ");
    buf.append(Arrays.toString(info.getMeasureCount()));
//    buf.append('\n');

    return buf.toString();
  }

  /**
   * 量化崩潰修正
   * @param rgbArray RGB[] 要修正的code
   * @param ch Channel 修正的頻道
   */
  protected void quantizatioCollapseFix(RGB[] rgbArray, RGBBase.Channel ch) {
    if (ap.quantizationCollapseFix) {
      double max = this.getWhiteCPCode().getValue(ch, maxValue);
      //量化崩潰修正
      CalibrateUtils.quantizationCollapseFix(rgbArray, ch, maxValue,
                                             false, ap.concernCollapseFixable,
                                             max);
    }
  }

  protected void quantizatioCollapseFix(RGB[] rgbArray) {
    if (ap.quantizationCollapseFix) {
      double[] maxArray = this.getWhiteCPCode().getValues(new double[3],
          maxValue);
      //量化崩潰修正
      CalibrateUtils.quantizationCollapseFix(rgbArray, maxValue,
                                             ap.concernCollapseFixable,
                                             maxArray);
    }
  }

}
