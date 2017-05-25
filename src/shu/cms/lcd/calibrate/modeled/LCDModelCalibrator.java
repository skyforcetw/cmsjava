package shu.cms.lcd.calibrate.modeled;

import java.util.*;
import java.util.concurrent.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.devicemodel.lcd.util.*;
import shu.cms.lcd.calibrate.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.cms.lcd.material.*;
import shu.cms.util.*;
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
public class LCDModelCalibrator
    implements LCDModelCalibratorConst, Plottable {

  protected String rootDir = ".";
  public final void setRootDir(String root) {
    this.rootDir = root;
  }

  public LCDModelCalibrator(Parameters parameters) {
    this(parameters.lcdModel, parameters.whiteParameter,
         parameters.adjustParameter, parameters.viewingParameter,
         parameters.colorProofParameter);
  }

  public LCDModelCalibrator(LCDModel model, WhiteParameter wp,
                            AdjustParameter ap, ViewingParameter vp,
                            ColorProofParameter cp) {
    this.model = model;
    this.adapter = new LCDModelExposure(model);
    this.setWhiteParameter(wp);
    this.ap = ap;
    this.vp = vp;
    this.cp = cp;

    Logger.log.trace("LCDModel: " + model.getDescription() + "\n");
  }

  private ViewingParameter vp;
  private AdjustParameter ap;
  LCDModel model;
  private LCDModelExposure adapter;
  private WhiteParameter wp;
  ColorProofParameter cp;

  private boolean plotting = false;
  public void setPlotting(boolean plotting) {
    this.plotting = plotting;
  }

  CIExyY[] targetxyYCurve;

  protected void setWhiteParameter(WhiteParameter wp) {
    wp.model = model;
    this.wp = wp;
  }

  private void setWhiteXYZ(CIExyY xyY, double luminance) {
    xyY = (CIExyY) xyY.clone();
    xyY.Y = luminance;
    whiteXYZ = xyY.toXYZ();
  }

  private CIEXYZ getWhiteXYZ() {
    if (whiteXYZ == null && whiteRGB != null) {
      RGB clone = (RGB) whiteRGB.clone();
      this.model.changeMaxValue(clone);
      whiteXYZ = this.model.getXYZ(clone, false);
    }
    return whiteXYZ;
  }

  private CIEXYZ whiteXYZ;
  private RGB whiteRGB;

  private RGB getWhiteRGB() {
    if (whiteRGB == null) {
      if (wp.isRGBWhitePoint()) {
        //�p�G���w�F���I��code, �����^��
        whiteRGB = (RGB) wp.whiteCode.clone();
      }
      else {
        //�̷ӫ��w����׮y��, ���̱��񪺥��I��code
        whiteRGB = model.calculateWhiteRGB(wp.getWhitexyY(), wp.maxWhiteCode,
                                           cp.getTolerance(), false);
      }
    }
    return whiteRGB;
  }

  /**
   * ���ͶK��ؼЪ�rgb code(�κ�cp code��lut)
   * gamma���ե��O�HG�����
   *
   * 1.�̷өһݥ��I���RGB���̤j��
   * 2.�̷�G���̤j��, ����gamma�ե����G��G code
   * 3.�NG code�վ��קK�q�ƱY��
   * 4.�̷�G code�p��XW���z�Q�G�צ��u
   * 5.�HG code�H������I, ��������I(�]�A����I)�H�e���P���I����Ū��Ϭq
   *   �H������I�H�����I���ܤƦ�ŰϬq(CCTCurve)
   * @return RGB[]
   */
  protected RGB[] calibrateByGreen() {
    if (cp == null || wp == null) {
      throw new IllegalStateException("cp == null || wp == null");
    }

    //==========================================================================
    // 1.�̷өһݥ��I���RGB���̤j��
    //==========================================================================
    RGB whiteRGB = getWhiteRGB();
    adapter.setWhiteRGB(whiteRGB);
    //==========================================================================

    //==========================================================================
    // 2.�̷�G���̤j��, ����gamma�ե����G��G code
    //==========================================================================
    RGB[] caliRGBArray = codeCalibrator.greenGammaCalibrate(whiteRGB.G);
    //�ץ�code��crash�{�H�����p
    processGreenArray1(caliRGBArray, GreenArray1);
    //==========================================================================

    //==========================================================================
    // 3.�NG code�վ��קK�q�ƱY��
    //==========================================================================
    caliRGBArray = processCalibratedRGBArray2(caliRGBArray, GreenArray2);
    RGB[] greenCodeArray = RGBArray.deepClone(caliRGBArray);
    //==========================================================================

    //==========================================================================
    // 4.�̷�G code�p��XW���z�Q�G�צ��u
    //==========================================================================
    double[] whiteYArray = codeCalibrator.whiteLuminanceCalibrate(caliRGBArray);
    //==========================================================================

    //==========================================================================
    // 5.�HG code�H������I, ��������I(�]�A����I)�H�e���P���I����Ū��Ϭq
    // �H������I�H�����I���ܤƦ�ŰϬq
    // �ؼЭȪ����ͻݭn�����XGreen, ��Green���G�צA���X�ؼ�...���G���I�����˸m!?
    //==========================================================================
    targetxyYCurve = CCTCurveProducer.getxyYCurve(this.model,
                                                  this.getCCTCurveParameter(),
                                                  whiteYArray);
    //==========================================================================

    //==========================================================================
    // �̷ӥؼЭ�, ���ͮե����G
    //==========================================================================
    caliRGBArray = codeCalibrator.greenCalibrate(caliRGBArray, targetxyYCurve,
                                                 multiThreadCalibrate);
    //�ץ�code��crash�{�H�����p
    processGreenArray1(caliRGBArray, GreenArray3);
    caliRGBArray = processCalibratedRGBArray2(caliRGBArray, GreenArray4);
    replaceGreenToRGBArray(greenCodeArray, caliRGBArray);
    terminate(caliRGBArray);
    //==========================================================================

    return caliRGBArray;
  }

  private void replaceGreenToRGBArray(RGB[] greenCodeArray, RGB[] rgbArray) {
    if (greenCodeArray.length != rgbArray.length) {
      throw new IllegalArgumentException(
          "greenCodeArray.length != rgbArray.length");
    }

    int size = greenCodeArray.length;
    for (int x = 0; x < size; x++) {
      RGB g = greenCodeArray[x];
      rgbArray[x].setValue(RGB.Channel.G, g.G);
    }
  }

  private RGB[] processGreenArray1(RGB[] caliRGBArray, String filename) {
    CalibrateUtils.Crash.crashFix(caliRGBArray, RGB.Channel.G);
    CalibrateUtils.storeRGBArrayExcel(caliRGBArray, rootDir + "/" + filename,
                                      cp);
    return caliRGBArray;
  }

  private RGB[] processRGBArray1(RGB[] caliRGBArray) {
    CalibrateUtils.Crash.crashFix(caliRGBArray, RGB.Channel.G);
    CalibrateUtils.storeRGBArrayExcel(caliRGBArray, rootDir + "/" + WhiteArray1,
                                      cp);
    return caliRGBArray;
  }

  private final void quantizationCollapseFix(RGB[] rgbArray,
                                             RGBBase.Channel ch) {
    if (ap.quantizationCollapseFix) {
      double max = this.getWhiteRGB().getValue(ch, cp.calibrateBits);
      CalibrateUtils.quantizationCollapseFix(rgbArray, ch,
                                             cp.calibrateBits,
                                             false, ap.concernCollapseFixable,
                                             max);

    }
  }

  private RGB[] processCalibratedRGBArray2(RGB[] caliRGBArray, String filename) {
    //==========================================================================
    // 5.�NG code�q�ƨ��קK�q�ƱY��
    //==========================================================================
    caliRGBArray = CalibrateUtils.quantization(caliRGBArray, cp.calibrateBits);

    //�קK�q�ƱY��
    quantizationCollapseFix(caliRGBArray, RGBBase.Channel.R);
    quantizationCollapseFix(caliRGBArray, RGBBase.Channel.G);
    quantizationCollapseFix(caliRGBArray, RGBBase.Channel.B);
    CalibrateUtils.storeRGBArrayExcel(caliRGBArray, rootDir + "/" + filename,
                                      cp);
    //==========================================================================
    return caliRGBArray;
  }

  private void terminate(RGB[] caliRGBArray) {
    //�ץ��¥��IRGB
    blackAndWhiteFix(caliRGBArray);
    CalibrateUtils.storeRGBArrayExcel(caliRGBArray, rootDir + "/" + FinalArray,
                                      cp);

    //==========================================================================
  }

  public RGB[] calibrate() {
    switch (cp.gammaBy) {
      case W:
        calibrateRGBArray = calibrateByWhite();
        break;
      case G:
        calibrateRGBArray = calibrateByGreen();
        break;
    }
    return calibrateRGBArray;
  }

  /**
   * ���ͥH�զ�ե�����¦�� �ؼ�xyY���u
   * @return CIExyY[]
   */
  protected CIExyY[] productTargetxyYCurveByWhite() {
    if (cp == null || wp == null) {
      throw new IllegalStateException("cp == null || wp == null");
    }
    //==========================================================================
    // 1.�̷өһݥ��I���RGB���̤j��
    //==========================================================================
    RGB whiteRGB = getWhiteRGB();
    adapter.setWhiteRGB(whiteRGB);
    //==========================================================================

    //==========================================================================
    // 2.�q�]�w��gamma�ե��覡, ���Ͳz�QWhite YArray(�G�צ��u)
    //==========================================================================
    double[] whiteYArray = codeCalibrator.wGammaCalibrate(whiteRGB);
    if (ensureWhiteXYZ) {
      //���F�T�O���I�@�w�p�P�]�w�ɪ����, �Ӥ�����model���A�@���v�T, �]���j��ץ����I��׭�
      this.setWhiteXYZ(wp.getWhitexyY(), whiteYArray[whiteYArray.length - 1]);
    }
    //==========================================================================

    //==========================================================================
    // 3.�p��XCCTCurve
    //==========================================================================
    CIExyY[] targetxyYCurve = CCTCurveProducer.getxyYCurve(this.model,
        this.getCCTCurveParameter(), whiteYArray);
    //==========================================================================

    return targetxyYCurve;
  }

  /**
   * �O�_�T�O���I�@�w�p�P�]�w�ɪ����
   */
  private boolean ensureWhiteXYZ = false;

  /**
   * �H�h��������覡�w��RGB
   */
  private boolean multiThreadCalibrate = AutoCPOptions.get(
      "ModelCalibrator_MultiThread");

  /**
   * ���ͶK��ؼЪ�rgb code(�κ�cp code��lut)
   * gamma���ե��O�HW�����, �]������y�{�Pcalibrate()�y�����P
   *
   * 1.�̷өһݥ��I���RGB���̤j��
   * 2.�q�]�w��gamma�ե��覡, ���Ͳz�QWhite YArray(�G�צ��u)
   * 3.�p��XCCTCurve
   * 4.�p��XCCTCurve������RGB
   * 5.�NG code�վ㥭��
   * @return RGB[]
   */
  protected RGB[] calibrateByWhite() {
    if (cp == null || wp == null) {
      throw new IllegalStateException("cp == null || wp == null");
    }

    //==========================================================================
    // 1.�̷өһݥ��I���RGB���̤j��
    // 2.�q�]�w��gamma�ե��覡, ���Ͳz�QWhite YArray(�G�צ��u)
    // 3.�p��XCCTCurve
    //==========================================================================
    targetxyYCurve = productTargetxyYCurveByWhite();
    //==========================================================================
    RGB[] caliRGBArray = null;
    if (cp.injectedCPCodeArray == null) {
      //==========================================================================
      // 4.�p��XCCTCurve������RGB
      //==========================================================================
      caliRGBArray = codeCalibrator.rgbCalibrate(targetxyYCurve,
                                                 multiThreadCalibrate);
      processRGBArray1(caliRGBArray);
      //==========================================================================

      //==========================================================================
      // 5.�NG code�q�ƨ��קK�q�ƱY��
      //==========================================================================
      caliRGBArray = processCalibratedRGBArray2(caliRGBArray, WhiteArray2);
      terminate(caliRGBArray);
      //==========================================================================
    }
    else {
      //�N�`�g��CPCode�`�J
      caliRGBArray = cp.injectedCPCodeArray;
    }

    return caliRGBArray;
  }

  private void blackAndWhiteFix(RGB[] caliRGBArray) {
    //�ץ����IRGB
    caliRGBArray[caliRGBArray.length - 1] = getFixingWhiteRGB();
    if (cp.keepBlackPoint) {
      caliRGBArray[0] = new RGB();
    }

  }

  /**
   * ���o�ץ������IRGB
   * @return RGB
   */
  private RGB getFixingWhiteRGB() {
    RGB whiteRGB = this.getWhiteRGB();
    RGB result = (RGB) whiteRGB.clone();
    result.quantization(this.cp.calibrateBits);
    return result;
  }

  private CodeCalibrator codeCalibrator = new CodeCalibrator();

  protected class CodeCalibrator {

    /**
     * �qgArray��gamma�S��, ����Xwhite���z�Qgamma���u
     * @param gArray RGB[]
     * @return double[]
     */
    protected double[] whiteLuminanceCalibrate(RGB[] gArray) {
      int size = gArray.length;
      double[] gYArray = new double[size];
      CIEXYZ flareXYZ = model.flare.getFlare();
      for (int x = 0; x < size; x++) {
        model.changeMaxValue(gArray[x]);
        CIEXYZ XYZ = model.getXYZ(gArray[x], false);
        gYArray[x] = XYZ.Y;
      }

      double[] wYArray = new double[size];
      CIEXYZ whiteXYZ = getWhiteXYZ();
      wYArray[0] = flareXYZ.Y;
      for (int x = 0; x < size; x++) {
        double ratio = (gYArray[x] - flareXYZ.Y) /
            (gYArray[size - 1] - flareXYZ.Y);
        wYArray[x] = ratio * (whiteXYZ.Y - flareXYZ.Y) + flareXYZ.Y;
      }
      return wYArray;
    }

    /**
     * �qgreenCodeArray��green����¦, �HtargetxyYCurve���ؼ�, �¥X�]�ŦX�ؼЪ�RGB code
     * @param greenCodeArray RGB[]
     * @param targetxyYCurve CIExyY[]
     * @param multiThread boolean
     * @return RGB[]
     */
    protected RGB[] greenCalibrate(RGB[] greenCodeArray,
                                   CIExyY[] targetxyYCurve, boolean multiThread) {
      if (targetxyYCurve.length != 256) {
        throw new IllegalStateException(
            "targetxyYCurve.length != 256");
      }
      RGB[] rgbArray = new RGB[256];
      final RBCalculator rbc = new RBCalculator(model);
      final double tolerance = cp.calibrateBits.getStepIn255() / 2.;

      if (multiThread) {
        ExecutorService service = Executors.newFixedThreadPool(8);
        List<FutureTask<RGB>>
            futureTaskList = new ArrayList<FutureTask<RGB>> (256);

        for (int x = 0; x < 256; x++) {
          final CIEXYZ XYZ = targetxyYCurve[x].toXYZ();
          final RGB greenCode = greenCodeArray[x];
          Callable<RGB> callable = new Callable<RGB> () {
            public RGB call() throws Exception {
              return rbc.getRB(new CIExyY(XYZ), greenCode.G, tolerance, false);
            }
          };
          FutureTask<RGB> futureTask = new FutureTask<RGB> (callable);
          futureTaskList.add(futureTask);
          service.submit(futureTask);
        }
        try {
          for (int x = 0; x < 256; x++) {
            rgbArray[x] = futureTaskList.get(x).get();
          }
        }
        catch (ExecutionException ex) {
          Logger.log.error("", ex);
        }
        catch (InterruptedException ex) {
          Logger.log.error("", ex);
        }
        service.shutdown();
      }
      else {
        for (int x = 0; x < 256; x++) {
          CIEXYZ XYZ = targetxyYCurve[x].toXYZ();
          RGB greenCode = greenCodeArray[x];
          rgbArray[x] = rbc.getRB(new CIExyY(XYZ), greenCode.G, tolerance, false);
        }
      }

      return rgbArray;
    }

    protected RGB[] rgbCalibrate(CIExyY[] targetxyYCurve, boolean multiThread) {
      if (targetxyYCurve.length != 256) {
        throw new IllegalStateException(
            "targetxyYCurve.length != 256");
      }
      RGB[] rgbArray = new RGB[256];

      if (multiThread) {
        ExecutorService service = Executors.newFixedThreadPool(8);
        List<FutureTask<RGB>>
            futureTaskList = new ArrayList<FutureTask<RGB>> (256);

        for (int x = 0; x < 256; x++) {
          final CIEXYZ XYZ = targetxyYCurve[x].toXYZ();
          Callable<RGB> callable = new Callable<RGB> () {
            public RGB call() throws Exception {
              return adapter.getRGB(XYZ, false, true);
            }
          };
          FutureTask<RGB> futureTask = new FutureTask<RGB> (callable);
          futureTaskList.add(futureTask);
          service.submit(futureTask);
        }
        try {
          for (int x = 0; x < 256; x++) {
            rgbArray[x] = futureTaskList.get(x).get();
          }
        }
        catch (ExecutionException ex) {
          Logger.log.error("", ex);
        }
        catch (InterruptedException ex) {
          Logger.log.error("", ex);
        }
        service.shutdown();
      }
      else {
        for (int x = 0; x < 256; x++) {
          CIEXYZ XYZ = targetxyYCurve[x].toXYZ();
          rgbArray[x] = adapter.getRGB(XYZ, false, true);
        }
      }

      return rgbArray;
    }

    protected double[] wGammaCalibrate(RGB whiteRGB) {
      //==========================================================================
      // 2.�q�]�w��gamma�ե��覡, ���Ͳz�QWhite YArray(�G�צ��u)
      //==========================================================================
      Logger.log.info("White gamma calibrating...");

      double[] YArray = null;
      GammaCalibrator cali = new GammaCalibrator(model);

      switch (cp.gamma) {
        case Native:
          double[][] originalGammaCurve = cali.getOriginalWhiteGammaCurve(wp.
              maxWhiteCode);
          YArray = cali.whiteCalibrate(originalGammaCurve[1], whiteRGB);
          break;
        case Smooth:
          cali.smoothWhiteCalibrate(whiteRGB, wp.maxWhiteCode);
          break;
        case Custom:
          YArray = cali.whiteCalibrate(cp.customGamma, whiteRGB);
          break;
        case sRGB:
          YArray = cali.sRGBWhiteCalibrate(whiteRGB);
          break;
        case GSDF:
          YArray = cali.whiteCalibrateGSDF(whiteRGB);
          break;
        case CustomCurve:
          YArray = cali.whiteCalibrate(cp.customCurve, whiteRGB);
          break;
        case Scale:
        default:
          throw new IllegalArgumentException("Not support type!");
      }

      return YArray;
    }

    /**
     * ��G�@gamma�ե�
     * @param maxG double
     * @return RGB[]
     */
    protected RGB[] greenGammaCalibrate(double maxG) {
      //==========================================================================
      // 2.�̷�G���̤j��, ����gamma�ե����G��G code
      //==========================================================================
      Logger.log.info("G code gamma calibrating...");

      RGB[] gArray = null;
      GammaCalibrator cali = new GammaCalibrator(model);

      switch (cp.gamma) {
        case Native:
          double[][] gammaCurve = cali.getOriginalGammaCurve(RGB.Channel.G,
              maxG);
          gArray = cali.gammaCalibrate(RGB.Channel.G, gammaCurve[1], maxG);
          break;
        case Smooth:
          gArray = cali.smoothCalibrate(RGBBase.Channel.G, maxG);
          break;
        case Scale:

          //�|�O�����O��S curve�S��
          gArray = GammaCalibrator.scale(RGBBase.Channel.G, maxG);
          break;
        case Custom:
          gArray = cali.gammaCalibrate(RGBBase.Channel.G, cp.customGamma, maxG);
          break;

        case sRGB:

          //����ĳ�ϥ�
          gArray = cali.sRGBCalibrate(RGBBase.Channel.G, maxG);
          break;
        case GCode:
          gArray = cali.gCodeCalibrate(cp.gCodeArray);
          break;
        default:
          throw new IllegalArgumentException("Not support type!");
      }
      //�קK����έt��
      IrregularUtil.irregularFix(gArray);
      RGBArray.changeMaxValue(gArray, RGB.MaxValue.Double255);

      //�קK�q�ƱY��
      quantizationCollapseFix(gArray, RGBBase.Channel.G);

      return gArray;
    }

  }

  DeltaE[] rbDeltaEArray;
  DeltaE[] rDeltaEArray;
  /**
   * �ե����G��rgb Array
   */
  RGB[] calibrateRGBArray;

  /**
   * ����CCTCurve���ͰѼ�
   * @return Parameter
   */
  protected CCTCurveProducer.CCTParameter getCCTCurveParameter() {
    CIEXYZ whiteXYZ = this.getWhiteXYZ();
    CIEXYZ blackXYZ = model.flare.getFlare();
    return CCTCurveProducer.getCCTParameter(blackXYZ, whiteXYZ, cp, wp);
  }

  /**
   * ���o�z�Q���ؼ�patchList
   * �]�A����RGB,�H�ΥؼЪ�XYZ
   * @return List
   */
  public List<Patch> getTargetPatchList() {
    int size = calibrateRGBArray.length;
    List<Patch> patchList = new ArrayList<Patch> (size);

    for (int x = 0; x < size; x++) {
      RGB rgb = calibrateRGBArray[x];
      CIExyY xyY = targetxyYCurve[x];
      Patch p = new Patch(Integer.toString(x), xyY.toXYZ(), null, rgb);
      patchList.add(p);
    }
    return patchList;
  }

  public final String getDeltauvPrimeReport() {
    return new LCDModelCalibrateReporter(this).getDeltauvPrimeReport();
  }
}
