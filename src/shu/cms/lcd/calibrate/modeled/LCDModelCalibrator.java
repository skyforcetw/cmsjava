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
        //如果指定了白點用code, 直接回傳
        whiteRGB = (RGB) wp.whiteCode.clone();
      }
      else {
        //依照指定的色度座標, 找到最接近的白點用code
        whiteRGB = model.calculateWhiteRGB(wp.getWhitexyY(), wp.maxWhiteCode,
                                           cp.getTolerance(), false);
      }
    }
    return whiteRGB;
  }

  /**
   * 產生貼近目標的rgb code(或稱cp code或lut)
   * gamma的校正是以G為基準
   *
   * 1.依照所需白點找到RGB的最大值
   * 2.依照G的最大值, 產生gamma校正結果的G code
   * 3.將G code調整避免量化崩潰
   * 4.依照G code計算出W的理想亮度曲線
   * 5.以G code以及轉折點, 產生轉折點(包括轉折點)以前的與白點等色溫的區段
   *   以及轉折點以後到黑點的變化色溫區段(CCTCurve)
   * @return RGB[]
   */
  protected RGB[] calibrateByGreen() {
    if (cp == null || wp == null) {
      throw new IllegalStateException("cp == null || wp == null");
    }

    //==========================================================================
    // 1.依照所需白點找到RGB的最大值
    //==========================================================================
    RGB whiteRGB = getWhiteRGB();
    adapter.setWhiteRGB(whiteRGB);
    //==========================================================================

    //==========================================================================
    // 2.依照G的最大值, 產生gamma校正結果的G code
    //==========================================================================
    RGB[] caliRGBArray = codeCalibrator.greenGammaCalibrate(whiteRGB.G);
    //修正code有crash現象的狀況
    processGreenArray1(caliRGBArray, GreenArray1);
    //==========================================================================

    //==========================================================================
    // 3.將G code調整避免量化崩潰
    //==========================================================================
    caliRGBArray = processCalibratedRGBArray2(caliRGBArray, GreenArray2);
    RGB[] greenCodeArray = RGBArray.deepClone(caliRGBArray);
    //==========================================================================

    //==========================================================================
    // 4.依照G code計算出W的理想亮度曲線
    //==========================================================================
    double[] whiteYArray = codeCalibrator.whiteLuminanceCalibrate(caliRGBArray);
    //==========================================================================

    //==========================================================================
    // 5.以G code以及轉折點, 產生轉折點(包括轉折點)以前的與白點等色溫的區段
    // 以及轉折點以後到黑點的變化色溫區段
    // 目標值的產生需要先產出Green, 由Green的亮度再推出目標...似乎有點本末倒置!?
    //==========================================================================
    targetxyYCurve = CCTCurveProducer.getxyYCurve(this.model,
                                                  this.getCCTCurveParameter(),
                                                  whiteYArray);
    //==========================================================================

    //==========================================================================
    // 依照目標值, 產生校正結果
    //==========================================================================
    caliRGBArray = codeCalibrator.greenCalibrate(caliRGBArray, targetxyYCurve,
                                                 multiThreadCalibrate);
    //修正code有crash現象的狀況
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
    // 5.將G code量化並避免量化崩潰
    //==========================================================================
    caliRGBArray = CalibrateUtils.quantization(caliRGBArray, cp.calibrateBits);

    //避免量化崩潰
    quantizationCollapseFix(caliRGBArray, RGBBase.Channel.R);
    quantizationCollapseFix(caliRGBArray, RGBBase.Channel.G);
    quantizationCollapseFix(caliRGBArray, RGBBase.Channel.B);
    CalibrateUtils.storeRGBArrayExcel(caliRGBArray, rootDir + "/" + filename,
                                      cp);
    //==========================================================================
    return caliRGBArray;
  }

  private void terminate(RGB[] caliRGBArray) {
    //修正黑白點RGB
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
   * 產生以白色校正為基礎的 目標xyY曲線
   * @return CIExyY[]
   */
  protected CIExyY[] productTargetxyYCurveByWhite() {
    if (cp == null || wp == null) {
      throw new IllegalStateException("cp == null || wp == null");
    }
    //==========================================================================
    // 1.依照所需白點找到RGB的最大值
    //==========================================================================
    RGB whiteRGB = getWhiteRGB();
    adapter.setWhiteRGB(whiteRGB);
    //==========================================================================

    //==========================================================================
    // 2.從設定的gamma校正方式, 產生理想White YArray(亮度曲線)
    //==========================================================================
    double[] whiteYArray = codeCalibrator.wGammaCalibrate(whiteRGB);
    if (ensureWhiteXYZ) {
      //為了確保白點一定如同設定時的色度, 而不受到model的再一次影響, 因此強制修正白點色度值
      this.setWhiteXYZ(wp.getWhitexyY(), whiteYArray[whiteYArray.length - 1]);
    }
    //==========================================================================

    //==========================================================================
    // 3.計算出CCTCurve
    //==========================================================================
    CIExyY[] targetxyYCurve = CCTCurveProducer.getxyYCurve(this.model,
        this.getCCTCurveParameter(), whiteYArray);
    //==========================================================================

    return targetxyYCurve;
  }

  /**
   * 是否確保白點一定如同設定時的色度
   */
  private boolean ensureWhiteXYZ = false;

  /**
   * 以多執行緒的方式預測RGB
   */
  private boolean multiThreadCalibrate = AutoCPOptions.get(
      "ModelCalibrator_MultiThread");

  /**
   * 產生貼近目標的rgb code(或稱cp code或lut)
   * gamma的校正是以W為基準, 因此整體流程與calibrate()稍有不同
   *
   * 1.依照所需白點找到RGB的最大值
   * 2.從設定的gamma校正方式, 產生理想White YArray(亮度曲線)
   * 3.計算出CCTCurve
   * 4.計算出CCTCurve對應的RGB
   * 5.將G code調整平順
   * @return RGB[]
   */
  protected RGB[] calibrateByWhite() {
    if (cp == null || wp == null) {
      throw new IllegalStateException("cp == null || wp == null");
    }

    //==========================================================================
    // 1.依照所需白點找到RGB的最大值
    // 2.從設定的gamma校正方式, 產生理想White YArray(亮度曲線)
    // 3.計算出CCTCurve
    //==========================================================================
    targetxyYCurve = productTargetxyYCurveByWhite();
    //==========================================================================
    RGB[] caliRGBArray = null;
    if (cp.injectedCPCodeArray == null) {
      //==========================================================================
      // 4.計算出CCTCurve對應的RGB
      //==========================================================================
      caliRGBArray = codeCalibrator.rgbCalibrate(targetxyYCurve,
                                                 multiThreadCalibrate);
      processRGBArray1(caliRGBArray);
      //==========================================================================

      //==========================================================================
      // 5.將G code量化並避免量化崩潰
      //==========================================================================
      caliRGBArray = processCalibratedRGBArray2(caliRGBArray, WhiteArray2);
      terminate(caliRGBArray);
      //==========================================================================
    }
    else {
      //將注射的CPCode注入
      caliRGBArray = cp.injectedCPCodeArray;
    }

    return caliRGBArray;
  }

  private void blackAndWhiteFix(RGB[] caliRGBArray) {
    //修正白點RGB
    caliRGBArray[caliRGBArray.length - 1] = getFixingWhiteRGB();
    if (cp.keepBlackPoint) {
      caliRGBArray[0] = new RGB();
    }

  }

  /**
   * 取得修正的白點RGB
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
     * 從gArray的gamma特性, 推算出white的理想gamma曲線
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
     * 從greenCodeArray的green為基礎, 以targetxyYCurve為目標, 兜出巿符合目標的RGB code
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
      // 2.從設定的gamma校正方式, 產生理想White YArray(亮度曲線)
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
     * 對G作gamma校正
     * @param maxG double
     * @return RGB[]
     */
    protected RGB[] greenGammaCalibrate(double maxG) {
      //==========================================================================
      // 2.依照G的最大值, 產生gamma校正結果的G code
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

          //會保持面板的S curve特性
          gArray = GammaCalibrator.scale(RGBBase.Channel.G, maxG);
          break;
        case Custom:
          gArray = cali.gammaCalibrate(RGBBase.Channel.G, cp.customGamma, maxG);
          break;

        case sRGB:

          //不建議使用
          gArray = cali.sRGBCalibrate(RGBBase.Channel.G, maxG);
          break;
        case GCode:
          gArray = cali.gCodeCalibrate(cp.gCodeArray);
          break;
        default:
          throw new IllegalArgumentException("Not support type!");
      }
      //避免反轉或負值
      IrregularUtil.irregularFix(gArray);
      RGBArray.changeMaxValue(gArray, RGB.MaxValue.Double255);

      //避免量化崩潰
      quantizationCollapseFix(gArray, RGBBase.Channel.G);

      return gArray;
    }

  }

  DeltaE[] rbDeltaEArray;
  DeltaE[] rDeltaEArray;
  /**
   * 校正結果的rgb Array
   */
  RGB[] calibrateRGBArray;

  /**
   * 產生CCTCurve產生參數
   * @return Parameter
   */
  protected CCTCurveProducer.CCTParameter getCCTCurveParameter() {
    CIEXYZ whiteXYZ = this.getWhiteXYZ();
    CIEXYZ blackXYZ = model.flare.getFlare();
    return CCTCurveProducer.getCCTParameter(blackXYZ, whiteXYZ, cp, wp);
  }

  /**
   * 取得理想的目標patchList
   * 包括估算RGB,以及目標的XYZ
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
