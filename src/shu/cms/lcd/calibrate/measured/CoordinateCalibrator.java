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
 * ���_�ܤ�RGB�ƭ�, ������̱����y�Ъ��ե��覡
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
   * ����CIEuv1960FindingThread�����n��T
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
   * ��l��
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
   * �x�s�ե��|�Ψ쪺������T
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
   * �t��k����l��
   */
  private final void initAlgorithm() {
    //�����I
    white = this.getCalibratedTarget().getWhitePatch().getXYZ();
    //�p��H��
    DeltauvQuadrant quadrant = white == null ? null :
        this.getBlackCIEuvQuadrant();

    access = new AccessInstance(white, mi, this.jndi, quadrant,
                                wp.maxWhiteCode, !parallelExcute);
    access.getCompoundNearestAlogorithm().setDeltauvPrimeTolerance(this.
        deltauvPrimeTolerance);

    //==========================================================================
    // ��J�t��k
    // ��J�t��k����l��, �ѩ�ҿת���J, �O�n���̱�����I���O�S����I�B��P�@�ӶH��.
    // �ҥH�S�����I�����p�U, �ڥ��S����J�����n.
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
   * ��״M��P���I���ɭ�, �O�_�i���X�i? (���X�i��|�I, �X�i��K�I)
   */
  private boolean chromaticExpandForChromaAround = false;

  /**
   * step�M��P���I���ɭ�, ����ת����פW, �O�_�i���X�i
   */
  private boolean chromaticExpandForStepAround = false;

  /**
   * ���I
   */
  protected CIEXYZ white;

  /**
   * �b�̫�@����Around�j�M���ɭ�, ��cube�X�i
   */
  private boolean cubeAroundMode = true;

  //============================================================================
  // �t��k
  //============================================================================
//  private StepAroundAlgorithm stepAroundAlgo = new StepAroundAlgorithm(wp.
//      maxCode);
//  private ChromaticAroundAlgorithm chromaAroundAlgo = (this.wp == null) ?
//      new ChromaticAroundAlgorithm() :
//      new ChromaticAroundAlgorithm(wp.maxCode);

  /**
   * �H��t�ӭp��̪��I
   */
//  private DeltaE00NearestAlogorithm deltaENearAlgo;
  /**
   * �b��׸�G�׶�������
   */
//  private CompoundNearestAlogorithm compoundNearAlogo;
  /**
   * uv�����W���̪��I(compromise�\��ثe�L��)
   */
//  private CIEuv1960NearestAlogorithm uvNearAlgo;
  /**
   * JNDI�W��̪��I
   */
//  private LightnessNearestAlogorithm lightnessNearAlgo;
  //============================================================================

  /**
   * delta u'v'���e�\��
   */
  private double deltauvPrimeTolerance = 0.0009;
  public void setDeltauvPrimeTolerance(double deltauvPrimeTolerance) {
    this.deltauvPrimeTolerance = deltauvPrimeTolerance;
  }

  public Info info = new Info();

  public class Info {

    /**
     * �`�ե�����
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
     * ���������
     * @return double
     */
    public double getCompletePercentage() {
      return findingInfo.getCompletePercentage(maxValue);
    }

    /**
     * �H���������ʷ��������
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
                //����s�~��ܥX��
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
   * ����I�@�ե�
   */
  private final void whiteCalibrate() {
    if (wp.isDoWhiteCalibrate()) {
      WhitePointCalibrator wpc = new WhitePointCalibrator(this.logoLCDTaget, this);
      RGB whiteRGB = wpc.calibrate()[0];
      //�ⲣ�ͥX�Ӫ��ե����I, ��������cp code RGBArray
      RGB[] cpcode = getCPCodeRGBArray();

      findingInfo.setCalibratedRGB(255, whiteRGB);
      cpcode[255] = whiteRGB;

    }
  }

  /**
   * ���Y��code���G�ץH�W, ����������Oí�w��
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
   *  í�w���G��
   */
  private final static double StableLuminance = 2.;

  /**
   * ���X��í�w�G�ץH�U��calibrated array
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
   *  ��CalibratedInfo�����@��l��
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
   * �ե���l��,�]�t:
   *  ��ե����ե�, ��l�Ʈե�, �t��k��l��, �M��í�w�G�ת�code, ��Ʈե�
   */
  protected final void initCalibrate() {

    initFindingInfo();

    /**
     * ��ե����ե� (�C���Ҥ��P)
     * ���̱���ؼЭȪ���
     */
    whiteCalibrate();
    /**
     * ��l�Ʈե� (�C���Ҥ��P)
     * �Ѧ����ͥX�s���ؼЭ�
     */
    initMeasure(RGBBase.Channel.W, false);
    /**
     * �t��k��l�� (�C���Ҥ��P)
     * �N�ե��ҥΨ쪺�t��k���A����l��
     */
    initAlgorithm();
    /**
     * �M��í�w�G�ת�code (�C���Ҥ��P)
     * ��������ӻ�, �q�����G��í�w���̧Ccode
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
    // ���⤣í�w���B�z�@�U, �קK�����}�l��í
    //==========================================================================
    boolean[] unstableCalibrated = getUnstableCalibratedArray(stableCode,
        findingInfo.getAlreadyCalibrated());
    calibrated0(unstableCalibrated, 1, stableCode);
    for (int x = 1; x < unstableCalibrated.length; x++) {
      findingInfo.setCalibrated(x, unstableCalibrated[x]);
    }
    //==========================================================================

    //==========================================================================
    // ��ѤU���B�̧�
    //==========================================================================
    calibrated0(findingInfo.getAlreadyCalibrated(), stableCode, 255);
    //==========================================================================

    //���l�q��
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
   * �Ҧ��ե�Thread��List
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
   * ��start�H��end�����i��calibrate, �åB�Hcalibrated�@��Ĳ�o�q����
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
   * �O�_�n�i���׮ե�
   */
  protected boolean chromaticCalibrate = true;
  /**
   * �O�_�n�i��G�׮ե�
   */
  protected boolean luminanceCalibrate = true;

  /**
   * �ΨӰO����Ĳ��MaxAroundIterativeTimes������
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
   * �O�_�n�i���׮ե�
   * @param chromaticCalibrate boolean
   */
  public void setChromaticCalibrate(boolean chromaticCalibrate) {
    this.chromaticCalibrate = chromaticCalibrate;
  }

  /**
   * �O�_�n�H�ߤ���@�X�i? ����h�I�ǤJ�Ҷq
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
   * ���o���I�����I�����ӶH��
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
   * �q�ƱY��ץ�
   * @param rgbArray RGB[] �n�ץ���code
   * @param ch Channel �ץ����W�D
   */
  protected void quantizatioCollapseFix(RGB[] rgbArray, RGBBase.Channel ch) {
    if (ap.quantizationCollapseFix) {
      double max = this.getWhiteCPCode().getValue(ch, maxValue);
      //�q�ƱY��ץ�
      CalibrateUtils.quantizationCollapseFix(rgbArray, ch, maxValue,
                                             false, ap.concernCollapseFixable,
                                             max);
    }
  }

  protected void quantizatioCollapseFix(RGB[] rgbArray) {
    if (ap.quantizationCollapseFix) {
      double[] maxArray = this.getWhiteCPCode().getValues(new double[3],
          maxValue);
      //�q�ƱY��ץ�
      CalibrateUtils.quantizationCollapseFix(rgbArray, maxValue,
                                             ap.concernCollapseFixable,
                                             maxArray);
    }
  }

}
