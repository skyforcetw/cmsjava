package vv.cms.lcd.calibrate.measured;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.devicemodel.lcd.util.*;
import shu.cms.lcd.*;
import vv.cms.lcd.calibrate.*;
import vv.cms.lcd.calibrate.gsdf.*;
import vv.cms.lcd.calibrate.measured.find.*;
import vv.cms.lcd.calibrate.measured.util.*;
import vv.cms.lcd.calibrate.parameter.*;
import shu.cms.lcd.material.*;
import shu.cms.measure.*;
import shu.cms.plot.*;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.regress.*;
import shu.util.log.*;
import vv.cms.lcd.material.*;

//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 1. �����w��G�׽վ�, �N�թ��ؼЫG�׽վ�@�B.
 * 2. �վ�ᱵ�U�ӽվ�R/B�@�B, �Ϧ�ױ���ؼЭ�,
 * 3. �A�^��1,2���_�`��, �����ӦA�]���ݭn�վ�.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class IndependentCoordinateCalibrator
    extends CoordinateCalibrator {

  public IndependentCoordinateCalibrator(LCDTarget logoLCDTaget,
                                         LCDTarget originalRampLCDTarget,
                                         MeterMeasurement meterMeasurement,
                                         ColorProofParameter p,
                                         WhiteParameter wp,
                                         AdjustParameter ap,
                                         MeasureParameter mp) {
    super(logoLCDTaget, meterMeasurement, p, wp, ap, mp);
    this.setChromaticExpandForChromaAround(true);
    this.originalRampLCDTarget = originalRampLCDTarget;
  }

  protected LCDTarget getWhiteRelativeTarget(LCDTarget measureTarget) {
    return RelativeTarget.
        getLuminanceAndChromaticityRelativeInstance(
            getOriginalTarget(), measureTarget, cp.turnCode, lcdModel, cp, wp);
  }

  /**
   * RelativeTarget & SmoothGreenByModel�һ�
   */
  private LCDModel lcdModel;

  public static void main(String[] args) {
    RGB rgb0 = new RGB(0, 0, 0);
    RGB rgb1 = new RGB(2, 1, 1);
    RGB rgb2 = new RGB(2, 2, 2);
    RGB rgb3 = new RGB(3, 3, 3);
    RGB rgb4 = new RGB(4, 4, 4);
    RGB rgb5 = new RGB(4, 4, 5);
    RGB rgb6 = new RGB(4, 4, 6);
    RGB rgb7 = new RGB(4, 4, 7);
    RGB[] rgbArray = new RGB[] {
        rgb0, rgb1, rgb2, rgb3, rgb4, rgb5, rgb6, rgb7};
    for (RGB rgb : rgbArray) {
      rgb.changeMaxValue(RGB.MaxValue.Double255);
    }

  }

  public RGB[][] getCalibratedResult() {
    RGB[][] result = null;

    int size = batchList.size();
    result = new RGB[size][];
    for (int x = 0; x < size; x++) {
      Batch b = batchList.get(x);
      result[x] = b.calibratedResult;
    }

    return result;
  }

  private void storeRGBArrayExcel(RGB[][] results, String[] filenames
      ) {
    if (results.length != filenames.length) {
      throw new IllegalArgumentException(
          "results.length != filenames.length");
    }
    int size = results.length;
    for (int x = 0; x < size; x++) {
      if (filenames[x] != null && results[x] != null) {
        CalibrateUtils.storeRGBArrayExcel(results[x],
                                          rootDir + "/" +
                                          filenames[x], cp);
      }
    }
  }

  private List<Batch> batchList = new ArrayList<Batch> ();
  public void addBatch(Batch batch) {
    batchList.add(batch);
  }

  public void addBatch(Batch ...batchArray) {
    for (Batch b : batchArray) {
      batchList.add(b);
    }
  }

  public final static class Batch {
    public Batch(boolean enable, boolean[] alreadyCalibrated,
                 boolean luminanceCalibrate,
                 boolean chromaticCalibrate, String beforeInterpFilename,
                 String calibratedFilename, String cubeCheckFilename,
                 String testInterpFilename, int calibratedInterval) {
      this.enable = enable;
      this.alreadyCalibrated = alreadyCalibrated;
      this.luminanceCalibrate = luminanceCalibrate;
      this.chromaticCalibrate = chromaticCalibrate;
      this.beforeInterpFilename = beforeInterpFilename;
      this.calibratedFilename = calibratedFilename;
      this.cubeCheckFilename = cubeCheckFilename;
      this.testInterpFilename = testInterpFilename;
      this.calibratedInterval = calibratedInterval;
    }

    public boolean enable;
    public boolean[] alreadyCalibrated;

    public boolean luminanceCalibrate;
    public boolean chromaticCalibrate;
    public String beforeInterpFilename;
    public String calibratedFilename;
    public String cubeCheckFilename;
    public String testInterpFilename;
    public int calibratedInterval;
    public RGB[] calibratedResult;
  }

  private RGB[] _calibrate(Batch batch, RGB[] calibratedResult) {
    mi.reset();
    boolean[] alreadyCalibrated = batch.alreadyCalibrated;
    RGB[][] results = getCalibratedRGBArrays(calibratedResult,
                                             batch.luminanceCalibrate,
                                             batch.chromaticCalibrate,
                                             alreadyCalibrated,
                                             batch.calibratedInterval);
    storeRGBArrayExcel(results,
                       new String[] {batch.calibratedInterval != 1 ?
                       batch.beforeInterpFilename : null,
                       batch.calibratedFilename,
                       ap.cubeCheckAtLowLuminance ?
                       batch.cubeCheckFilename : null,
                       batch.testInterpFilename});
    batch.calibratedResult = results[1];
    return results[1];
  }

  /**
   * ����ե�, �����I�s��
   *
   * @return RGB[]
   */
  protected RGB[] _calibrate() {
    initCalibrate();
    RGB[] calibratedResult = this.getCPCodeRGBArray();

    for (Batch b : batchList) {
      if (b.enable) {
        calibratedResult = _calibrate(b, calibratedResult);
      }
    }
    CalibrateUtils.storeRGBArrayExcel(calibratedResult,
                                      rootDir + "/" + IndependFinal, cp);
    logFindingThread();
    return calibratedResult;
  }

  private void logFindingThread() {
    java.util.logging.Logger log = Logger.getDefaultLogger("findingThread");
    log.info(this.getThreadListTrace());
  }

  private LCDTarget originalRampLCDTarget;

  /**
   * �ե������G2�P�ɧY��cube check�����G
   *
   * @param initRGBArray RGB[]
   * @param luminanceCalibrate boolean
   * @param chromaticCalibrate boolean
   * @param alreadyCalibrated boolean[]
   * @param calibratedInterval int
   * @return RGB[][] {��B�ե����������G(������), �ե������G(�����B�Y��״_), �Կ諸�ե������G(�����B�Y��״_) }
   */
  protected RGB[][] getCalibratedRGBArrays(final RGB[] initRGBArray,
                                           boolean luminanceCalibrate,
                                           boolean chromaticCalibrate,
                                           boolean[] alreadyCalibrated,
                                           int calibratedInterval) {
    //��B�ե����������G(������)
    RGB[] originalCalibrated = RGBArray.deepClone(initRGBArray);
    this.luminanceCalibrate = luminanceCalibrate;
    this.chromaticCalibrate = chromaticCalibrate;
    this.setCalibrated(alreadyCalibrated);

    findingInfo.setInitRGBArray(originalCalibrated);
    startCalibrate();
    //�ե������G,�����B�Y��״_
    RGB[] calibratedResult = RGBArray.deepClone(originalCalibrated);
    //�ե������G2, �����B�Y��״_
    RGB[] candilateCalibratedResult = RGBArray.deepClone(findingInfo.
        getCandilateCalibratedRGBArray());
    RGB[] interpolateTestResult = null;
    if (calibratedInterval != 1 && interpolator != null) {
      //========================================================================
      // �����]�w
      //========================================================================
      //�Хܤw�g�ե���, ���b�y�{���S���Q�ե�; �]���w�Q�ե����~�n�Q����.
      boolean[] interpolate = Arrays.copyOf(alreadyCalibrated,
                                            alreadyCalibrated.length);
      interpolate[0] = interpolate[interpolate.length - 1] = false;
      //========================================================================

      //========================================================================
      // test
      //========================================================================
      if (testInterpolate) {
        interpolateTestResult = interpolator.interpolateResult(
            calibratedResult, interpolate, cp.calibrateBits,
            Interpolator.Mode.Quadratic);
        quantizatioCollapseFix(interpolateTestResult);
      }
      //========================================================================

      //========================================================================
      // cali1
      //========================================================================
      calibratedResult = interpolator.interpolateResult(
          calibratedResult, interpolate, cp.calibrateBits);
      //========================================================================

      //========================================================================
      // cali2
      //========================================================================
      candilateCalibratedResult = interpolator.interpolateResult(
          candilateCalibratedResult, interpolate, cp.calibrateBits);
      //========================================================================

    }

    quantizatioCollapseFix(calibratedResult);
    quantizatioCollapseFix(candilateCalibratedResult);

    //==========================================================================
    RGB[][] result = new RGB[][] {
        originalCalibrated, calibratedResult, candilateCalibratedResult,
        interpolateTestResult};
    return result;
  }

  /**
   * �O�_�n�i�椺���t��k������
   */
  private boolean testInterpolate = true;

  /**
   * �NsmoothGreen��whiteBased��RGBArray�X��
   * ²�檺��, �N�O��smoothGreen����green������whiteBased����Green
   * @param smoothGreenRGBArray RGB[]
   * @param whiteBasedRGBArray RGB[]
   * @return RGB[]
   */
  private RGB[] combineSmoothGreenAndWhiteBasedRGBArray(RGB[]
      smoothGreenRGBArray, RGB[] whiteBasedRGBArray) {
    if (smoothGreenRGBArray.length != whiteBasedRGBArray.length) {
      throw new IllegalArgumentException(
          "moothGreenRGBArray.length != whiteBasedRGBArray.length");
    }
    int size = smoothGreenRGBArray.length;
    RGB[] combineRGBArray = new RGB[size];
    for (int x = 0; x < size; x++) {
      combineRGBArray[x] = (RGB) whiteBasedRGBArray[x].clone();
      combineRGBArray[x].G = smoothGreenRGBArray[x].G;
    }
    return combineRGBArray;
  }

  /**
   *
   * @param smoothGreenRGBArray RGB[]
   * @return RGB[]
   */
  private RGB[] getSmoothGreenByModel(RGB[] smoothGreenRGBArray) {
    RBCalculator rbc = new RBCalculator(this.lcdModel);
    CIExyY[] targetxyYArray = this.getTargetxyYArray();
    int size = smoothGreenRGBArray.length;
    RGB[] modelRGBArray = new RGB[size];
    double tolerance = RGB.MaxValue.Int10Bit.getStepIn255();

    for (int x = 0; x < size; x++) {
      CIExyY xyY = targetxyYArray[x];
      RGB green = smoothGreenRGBArray[x];
      RGB rgb = rbc.getRB(xyY, green.G, tolerance, false);
      rgb.quantization(this.maxValue);
      rgb.G = green.G;
      modelRGBArray[x] = rgb;
      Logger.log.info(x + " " + rgb);
    }

    return modelRGBArray;
  }

  private Interpolator interpolator;

  private SmoothGreen smoothGreen = new SmoothGreen();
  private static enum SmoothMethod {
    LowPass,
    /**
     * ��w��gamma, ����ĳ�ϥ�
     */
    ConstGamma,
    VariableGamma, LinearInterpolate
  }

  private class SmoothGreen {
    private SmoothMethod method = SmoothMethod.VariableGamma;
//    private SmoothMethod method = SmoothMethod.ConstGamma;

    /**
     * �p��Xgreen smooth��LCDTarget
     *
     * pseudo code:
     *   ���q���Xgreen��l��XYZ�ƭ�
     *   �Hgreen��XYZ�p��XJNDI, �åB�w���X�@��smooth��JNDI
     *   �Nsmooth JNDI�ഫ�^�jY
     *   �Hgreen���jY, ����X������green code�ι�����XYZ
     *   �Ngreen code�MXYZ�s��List<Patch>
     *   �HList<Patch>���ͥXLCDTarget
     *
     * @param rgbArray RGB[]
     * @param originalRampLCDTarget LCDTarget
     * @return LCDTarget
     */
    protected final LCDTarget getSmoothGreenLCDTarget(final RGB[] rgbArray,
        LCDTarget originalRampLCDTarget) {
      //�q��
      LCDTarget rampG = measure(rgbArray, RGBBase.Channel.G, null, false, true);
      //����rgb
      LCDTarget replaceRamp = LCDTargetUtils.getReplacedLCDTarget(rampG,
          LCDTarget.Number.Ramp256G_W);

      //==========================================================================
      // ����smooth target
      //==========================================================================

      double[] smoothYArray = null;
      switch (method) {
        case LowPass: {

          //�p��̥��ƪ��G�׸��
          GSDFPredicter.setGSDFLowPassPrimeCheckThreshold(ap.
              gsdfLowPassPrimeCheckThreshold);
          GSDFPredicter.PredictData predictData = GSDFPredicter.
              getGSDFLowPassPredictData(replaceRamp, cp, plotting);

          //�ഫ��Y
          smoothYArray = predictData.predictLuminance;
          break;
        }
        case ConstGamma: {
          LCDTarget rampGOnly = replaceRamp.targetFilter.getByNumber(LCDTarget.
              Number.Ramp256G, null);
          smoothYArray = getConstGammaSmoothLuminanceArray(rampGOnly);
          break;
        }
        case VariableGamma: {
          LCDTarget rampGOnly = replaceRamp.targetFilter.getByNumber(LCDTarget.
              Number.Ramp256G, null);
          smoothYArray = getVariableGammaSmoothLuminanceArray(rampGOnly);
          break;
        }
      }

      List<Patch>
          smoothPatchList = getPatchListFromLumiArray(originalRampLCDTarget,
          smoothYArray, RGBBase.Channel.G);
      LCDTarget smoothTarget = LCDTarget.Instance.get(smoothPatchList,
          LCDTarget.Number.Ramp256W, mm.isDo255InverseMode());
      //==========================================================================
      return smoothTarget;
    }

    private final double[] getConstGammaSmoothLuminanceArray(LCDTarget
        rampTarget) {
      double[] YArray = rampTarget.filter.YArray();
      double min = YArray[0];
      double max = YArray[YArray.length - 1];

      double[] normalOutput = DoubleArray.minus(YArray, min);
      Maths.normalize(normalOutput, normalOutput[YArray.length - 1]);
      double gamma = GammaFinder.findGamma(GammaFinder.NormalInput,
                                              normalOutput);
      double[] result = GammaFinder.gammaCurve(GammaFinder.NormalInput, gamma);
      result = DoubleArray.times(result, max - min);
      result = DoubleArray.plus(result, min);

      return result;
    }

    /**
     * �Q�Φh�����w��input�Moutput���������Y
     * @param input double[]
     * @param output double[]
     * @param start int
     * @param end int
     * @param coef COEF_1
     * @return double[]
     */
    private final double[] getRangePredictCurve(double[] input,
                                                double[] output, int start,
                                                int end, Polynomial.COEF_1 coef) {
      double[] in = DoubleArray.getRangeCopy(input, start, end);
      double[] out = DoubleArray.getRangeCopy(output, start, end);
      PolynomialRegression regress = new PolynomialRegression(in, out, coef);
      regress.regress();
      double[] predict = regress.getMultiPredict(input);
      return predict;
    }

    private final double[] getRangePredictCurve(double[] input,
                                                double[] output, int start,
                                                int end) {
      double[] in = DoubleArray.getRangeCopy(input, start, end);
      double[] out = DoubleArray.getRangeCopy(output, start, end);
      Polynomial.COEF_1 coef = PolynomialRegression.
          findBestPolynomialCoefficient1(input, output);
      System.out.println(coef);
      PolynomialRegression regress = new PolynomialRegression(in, out, coef);
      regress.regress();
      double[] predict = regress.getMultiPredict(input);
      return predict;
    }

    private final double[] getVariableGammaSmoothLuminanceArray(LCDTarget
        rampTarget) {
      double[] YArray = rampTarget.filter.YArray();
      double min = YArray[0];
      double max = YArray[YArray.length - 1];

      double[] normalOutput = GammaFinder.normalize(YArray, min, max);
      double[] gammas = GammaFinder.findGammas(GammaFinder.NormalInput,
                                                  normalOutput);
      double[] normalInput = DoubleArray.getRangeCopy(GammaFinder.NormalInput,
          1, GammaFinder.NormalInput.length - 2);
      double[] predict = getRangePredictCurve(normalInput, gammas,
                                              ap.variableGammaSmoothStart,
                                              ap.variableGammaSmoothEnd);
      double[] result = DoubleArray.copy(GammaFinder.NormalInput);
      for (int x = 0; x < result.length; x++) {
        int predictIndex = x - 1;
        if (predictIndex >= 0 && predictIndex < predict.length) {
          result[x] = Math.pow(result[x], predict[x - 1]);
        }
        result[x] *= max - min;
        result[x] += min;
      }

      if (plotting) {
        Plot2D plot = Plot2D.getInstance();
        plot.addLegend();
        plot.addLinePlot("predict", 1, 254, predict);
        plot.addLinePlot("gammas", 1, 254, gammas);
        plot.setVisible();
      }
      return result;
    }

    /**
     * �qlumiArray�H��rampTarget����XXYZ,
     * �åB�PRGB�@�_�x�s��Patch��.
     *
     * rampTarget�O�Ψӭp��Ҧ���ramp code, ���s�b��code�h�ĥΤ������覡.
     *
     * pseudo code:
     *   for Y : lumiArray
     *     �HY�brampTarget����������code
     *     �qcode�A��rampTarget����XXYZ
     *     �NRGB��XYZ�]�w��Patch�åB�s�W��List<Patch>
     *   �^��List<Patch>
     *
     * @param rampTarget LCDTarget
     * @param lumiArray double[]
     * @param channel Channel
     * @return List
     */
    private final List<Patch> getPatchListFromLumiArray(LCDTarget
        rampTarget, double[] lumiArray, RGBBase.Channel channel) {
      LCDTargetInterpolator interp = LCDTargetInterpolator.Instance.get(
          rampTarget, channel);

      int size = lumiArray.length;
      List<Patch> patchList = new ArrayList<Patch> (size);
      for (int x = 0; x < size; x++) {
        //�qY��������code
        RGB mapRGB = interp.getRGB(channel, lumiArray[x]);
        //�qG��code��������XYZ
        CIEXYZ XYZ = interp.getPatch(channel, mapRGB.getValue(channel)).getXYZ();
        mapRGB.quantization(maxValue);
        Patch p = new Patch(Integer.toString(x + 1), XYZ, null, mapRGB);
        patchList.add(p);
      }
      return patchList;
    }

  }

  /**
   * ��rgbArray����green�G�ק@smooth, ���s���ͥX������green code
   * �åB�̷ӰѼƳ]�w�ݨD�M�w�O�_�n����J�ץ�
   *
   * @param initRGBArray RGB[]
   * @return RGB[]
   */
  protected RGB[] getSmoothGreenRGBArray(final RGB[] initRGBArray) {
    LCDTarget smoothTarget = smoothGreen.getSmoothGreenLCDTarget(initRGBArray,
        originalRampLCDTarget);

    //==========================================================================
    // JNDI���ե�
    //==========================================================================
    JNDICalibrator calibrator = new JNDICalibrator(smoothTarget, this,
        RGBBase.Channel.G, originalRampLCDTarget);
    //�T��boolean���O�� �w��/�̤p�~�t/��J �ե����}��
    calibrator.setCalibrate(false, true, ap.smoothGreenCompromiseCalibrate);
    RGB[] result = calibrator.calibrate();
    //==========================================================================

    //==========================================================================
    // �ɨ�R&B������
    //==========================================================================
    switch (ap.smoothGreenBasedOn) {
      case Model:
        result = getSmoothGreenByModel(result);

        //model�w���i��y���~�t, �ץ��^��
        result[result.length - 1] = initRGBArray[initRGBArray.length - 1];
        break;
      case White:
        result = combineSmoothGreenAndWhiteBasedRGBArray(
            result, initRGBArray);
        break;
    }

    //==========================================================================

    quantizatioCollapseFix(result, RGBBase.Channel.G);
    return result;
  }

  /**
   * ��start�H��end�����i��calibrate, �åB�HtriggerBool�@��Ĳ�o�q����
   * @param triggerBool boolean[]
   * @param start int
   * @param end int
   */
  protected void calibrated0(boolean[] triggerBool, int start, int end) {
    for (int x = start; x < end; x++) {
      if (triggerBool[x] == false) {
        IndependentFindingThread t = new IndependentFindingThread(x, maxValue,
            findingInfo, triggerBool, this.luminanceCalibrate,
            this.chromaticCalibrate, this.white, access);
        t.setCubeCheckInOneJNDI(ap.cubeCheckAtLowLuminance &&
                                x <= ap.cubeCheckStartCode);
        if (algoFindByModel) {
          t.findByModel = algoFindByModel;
        }
        addToThreadList(t);
        t.setUncaughtExceptionHandler(this);
        if (parallelExcute) {
          t.start();
        }
        else {
          t.run();
        }
      }
    }
//    if (!algoDebug) {
    Trigger trigger = new Trigger(triggerBool);
    if (trigger.hasNextMeasure()) {
      mi.triggerMeasure(trigger);
    }
//    }
  }

  private boolean algoFindByModel = AutoCPOptions.get(
      "MeasuredCalibrator_FindByModel");

  public String getCalibratedInfomation() {
    StringBuilder buf = new StringBuilder();

    buf.append("AccumulateMeasureTimes: ");
    buf.append(getAccumulateMeasureCount());
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

    int[] measureCount = info.getMeasureCount();
    buf.append("CPM MeasureCount(" + measureCount.length + "): ");
    buf.append(Arrays.toString(measureCount));
    buf.append('\n');

    return buf.toString();
  }

  /**
   * �]�wLCDModel, RelativeTarget�p��ɩһ�
   * @param lcdModel LCDModel
   */
  public void setLCDModel(LCDModel lcdModel) {
    this.lcdModel = lcdModel;
  }

  public void setInterpolator(Interpolator interpolator) {
    this.interpolator = interpolator;
  }

}
