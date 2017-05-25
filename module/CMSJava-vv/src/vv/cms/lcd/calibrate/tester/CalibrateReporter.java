package vv.cms.lcd.calibrate.tester;

import java.io.*;
import java.lang.reflect.*;
import java.util.List;

import java.awt.*;
import javax.swing.*;

import shu.cms.*;
import shu.cms.colorformat.file.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.gradient.*;
import shu.cms.hvs.gradient.Pattern;
import shu.cms.lcd.*;
import vv.cms.lcd.calibrate.*;
import vv.cms.lcd.calibrate.measured.*;
import vv.cms.lcd.calibrate.parameter.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
import shu.util.log.*;

///import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 用來產生校正後報告
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CalibrateReporter {
  /**
   * 量測的LCDTarget
   */
  private LCDTarget measuredLCDTarget;
  /**
   * 量測的gray LCDTarget
   */
  private LCDTarget measuredGrayLCDTarget;
  /**
   * 量測的green LCDTarget
   */
  private LCDTarget measuredGreenLCDTarget;
  /**
   * 目標的LCDTarget(gray)
   */
  private LCDTarget targetLCDTarget;
  /**
   * 目標的green LCDTarget
   */
  private LCDTarget greenTargetLCDTarget;
  private String reportDirname;
  private String measurementDirname;
  private ColorProofParameter p;

  /**
   *
   * @param measuredLCDTarget LCDTarget 量測得到的LCDTarget
   * @param targetLCDTarget LCDTarget 目標的LCDTarget
   * @param greenTargetLCDTarget LCDTarget LCDTarget green頻道的目標LCDTarget(可為null)
   * @param p ColorProofParameter 校正的參數
   * @param reportDirname String report存放的目錄
   * @param measurementDirname String 量測結果存放的目錄
   */
  public CalibrateReporter(LCDTarget measuredLCDTarget,
                           LCDTarget targetLCDTarget,
                           LCDTarget greenTargetLCDTarget,
                           ColorProofParameter p,
                           String reportDirname, String measurementDirname) {
    this.measuredLCDTarget = measuredLCDTarget;
    this.measuredGrayLCDTarget = measuredLCDTarget.targetFilter.getRamp256W();
    this.measuredGreenLCDTarget = measuredLCDTarget.targetFilter.getRamp256(
        RGBBase.Channel.G, true);
    this.targetLCDTarget = LCDTargetUtils.getLCDTargetWithLinearRGB(
        targetLCDTarget, LCDTarget.Number.Ramp256W);
    this.greenTargetLCDTarget = greenTargetLCDTarget;
    this.p = p;
    this.reportDirname = reportDirname;
    this.measurementDirname = measurementDirname;
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  }

  /**
   *
   * @param measuredLCDTarget LCDTarget
   * @param targetLCDTarget LCDTarget
   * @param p ColorProofParameter
   * @param reportDirname String
   * @deprecated
   */
  public CalibrateReporter(LCDTarget measuredLCDTarget,
                           LCDTarget targetLCDTarget, ColorProofParameter p,
                           String reportDirname) {
    this(measuredLCDTarget, targetLCDTarget, null, p, reportDirname, "");
  }

  /**
   * 螢幕的大小，繪圖時所需
   */
  private Dimension screenSize;
  /**
   * 將量測結果儲存成vastview格式
   */
  private void saveMeasurementAsVastViewFile() {
    String filename = measurementDirname + "/vastview.txt";
    VastViewFile file = new VastViewFile(filename, measuredLCDTarget);
    try {
      file.save();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

  private void saveMeasurementAsLogoFile() {
    //儲存量測結果
    try {
      LCDTarget.IO.store(measuredLCDTarget, measurementDirname + "/1021.logo");
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }

//    String filename = measurementDirname + "/vastview.txt";
//    VastViewFile file = new VastViewFile(filename, measuredLCDTarget);
//    try {
//      file.save();
//    }
//    catch (IOException ex) {
//      Logger.log.error("", ex);
//    }
  }

  private void saveMeasurementAsAUORampExcel() {
    String filename = measurementDirname + "/AUOramp.xls";
    AUORampXLSFile file = new AUORampXLSFile(filename, measuredLCDTarget);
    try {
      file.save();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

  private void closePlot(Plot2D plot) {
    plot.setVisible(false);
    plot.dispose();
    plot = null;
    System.gc();

  }

  /**
   * 取得繪圖用的plot, 對使用過的plot作關閉的動作同時產生新的.
   * @param title String
   * @param label0 String
   * @param label1 String
   * @return Plot2D
   */
  private Plot2D getPlot(String title, String label0, String label1) {
    if (plot != null) {
      closePlot(plot);
    }

    plot = Plot2D.getInstance(reportDirname, screenSize.width,
                              screenSize.height);

    plot.setTitle(title);
    plot.setAxeLabel(0, label0);
    plot.setAxeLabel(1, label1);
    return plot;
  }

  private static boolean waitForDraw = true;

  /**
   * 將plot繪到檔案file裡, 以png格式儲存
   * @param plot Plot2D
   * @param filename String
   */
  private final static void drawToGraphicFile(final Plot2D plot,
                                              final String filename) {
    plot.addLegend();
    plot.setVisible();
    plot.toBack();
    try {
      SwingUtilities.invokeAndWait(new Thread() {

        public void run() {
          if (waitForDraw) {
            try {
              Thread.sleep(500);
            }
            catch (InterruptedException ex) {
              Logger.log.error("", ex);
            }
          }
          File file = new File(filename);
          plot.toGraphicFile(file);
          file = null;
        }
      }
      );
    }
    catch (InvocationTargetException ex) {
      Logger.log.error("", ex);
    }
    catch (InterruptedException ex) {
      Logger.log.error("", ex);
    }
  }

  /**
   * 從lcdTarget中取得灰階色塊(包含black)
   * @param lcdTarget LCDTarget
   * @return List
   */
  private final static List<Patch> getGrayPatchList(LCDTarget lcdTarget) {
    List<Patch> grayPatchList = lcdTarget.filter.grayPatch();
    Patch black = lcdTarget.getBlackPatch();
    grayPatchList.add(0, black);
    return grayPatchList;
  }

  /**
   * 繪出delta u'v'
   */
  private void drawDeltauvPrime() {
    List<Patch> grayPatchList = getGrayPatchList(measuredLCDTarget);
    Patch white = measuredLCDTarget.getWhitePatch();
    CIExyY whitexyY = new CIExyY(white.getXYZ());

    int size = grayPatchList.size();
    double[] dupArray = new double[size];
    double[] dvpArray = new double[size];

    for (int x = 0; x < size; x++) {
      Patch p = grayPatchList.get(x);
      CIExyY xyY = new CIExyY(p.getXYZ());
      double[] duvp = whitexyY.getDeltauvPrime(xyY);
      dupArray[x] = duvp[0];
      dvpArray[x] = duvp[1];
    }

    Plot2D plot = getPlot("Delta uv'", "code", "delta");

    plot.addLinePlot("du'", 0, dupArray.length, dupArray);
    plot.addLinePlot("dv'", 0, dvpArray.length, dvpArray);
    plot.setFixedBounds(0, 0, size);
    drawToGraphicFile(plot, reportDirname + "/deltauvp.png");
  }

  private static Plot2D plot;

  /**
   * 繪出delta JNDI
   */
  private void drawDeltaJNDI() {

    GSDFGradientModel measuredGM = new GSDFGradientModel(measuredLCDTarget);
    GSDFGradientModel targetGM = new GSDFGradientModel(targetLCDTarget);
    double[] measuredJNDIArray = measuredGM.getJNDIndexCurve();
    double[] targetJNDIArray = targetGM.getJNDIndexCurve();
    double[] deltaJNDIArray = DoubleArray.minus(measuredJNDIArray,
                                                targetJNDIArray);
    int size = deltaJNDIArray.length;
    Plot2D plot = getPlot("Delta JNDI", "code", "delta JNDI");

    plot.addLinePlot("delta", 0, size, deltaJNDIArray);
    plot.setFixedBounds(0, 0, size);
    drawToGraphicFile(plot, reportDirname + "/deltaJNDI.png");
  }

  private double[] getCurve(double[] luminanceArray, boolean inJNDI) {
    double[] curve = null;
    if (inJNDI) {
      GSDFGradientModel gm = new GSDFGradientModel(luminanceArray);
      curve = gm.getJNDIndexCurve();
    }
    else {
      curve = DoubleArray.minus(luminanceArray, luminanceArray[0]);
      Maths.normalize(curve, curve[curve.length - 1]);
    }
    return curve;
  }

  private void drawWhiteAndGreenGammaDifferentialInJNDI() {
    double[] grayYArray = getLuminanceArray(measuredGrayLCDTarget,
                                            RGBBase.Channel.W);
    double[] greenYArray = getLuminanceArray(measuredGreenLCDTarget,
                                             RGBBase.Channel.G);

    double[] grayCurve = getCurve(grayYArray, true);
    double[] greenCurve = getCurve(greenYArray, true);
    double[] curve = DoubleArray.minus(grayCurve, greenCurve);

    Plot2D plot = getPlot("Gamma Differential", "code", "delta JNDI");

    plot.addLinePlot(null, 0, 256, curve);
    plot.setFixedBounds(0, 0, 256);
    drawToGraphicFile(plot, reportDirname + "/gammaDiffWG.png");
  }

  private void drawGamma(boolean inJNDI, String filename) {
    double[] grayYArray = getLuminanceArray(measuredGrayLCDTarget,
                                            RGBBase.Channel.W);
    double[] greenYArray = getLuminanceArray(measuredGreenLCDTarget,
                                             RGBBase.Channel.G);

    double[] grayCurve = getCurve(grayYArray, inJNDI);
    double[] greenCurve = getCurve(greenYArray, inJNDI);

    String label = inJNDI ? "JNDI" : "Gamma";
    Plot2D plot = getPlot("Gamma", "code", label);

    plot.addLinePlot("Gray", Color.black, 0, 256, grayCurve);
    plot.addLinePlot("Green", Color.green, 0, 256, greenCurve);
    plot.setFixedBounds(0, 0, 256);
    drawToGraphicFile(plot, reportDirname + "/" + filename);
  }

  private void drawGammas(String filename) {
    double[] grayYArray = getLuminanceArray(measuredGrayLCDTarget,
                                            RGBBase.Channel.W);
    double[] redYArray = getLuminanceArray(measuredLCDTarget,
                                           RGBBase.Channel.R);
    double[] greenYArray = getLuminanceArray(measuredGreenLCDTarget,
                                             RGBBase.Channel.G);
    double[] blueYArray = getLuminanceArray(measuredLCDTarget,
                                            RGBBase.Channel.B);

    double[] grayCurve = getCurve(grayYArray, false);
    double[] redCurve = getCurve(redYArray, false);
    double[] greenCurve = getCurve(greenYArray, false);
    double[] blueCurve = getCurve(blueYArray, false);

    double[] grayGammas = GammaFinder.findGammas(GammaFinder.NormalInput,
        grayCurve);
    double[] redGammas = GammaFinder.findGammas(GammaFinder.NormalInput,
        redCurve);
    double[] greenGammas = GammaFinder.findGammas(GammaFinder.NormalInput,
        greenCurve);
    double[] blueGammas = GammaFinder.findGammas(GammaFinder.NormalInput,
        blueCurve);

    grayGammas = DoubleArray.getRangeCopy(grayGammas, 1, grayGammas.length - 1);
    redGammas = DoubleArray.getRangeCopy(redGammas, 1,
                                         redGammas.length - 1);
    greenGammas = DoubleArray.getRangeCopy(greenGammas, 1,
                                           greenGammas.length - 1);
    blueGammas = DoubleArray.getRangeCopy(blueGammas, 1,
                                          blueGammas.length - 1);

    Plot2D plot = getPlot("Gamma", "code", "Gammas");

    plot.addLinePlot("Gray", Color.black, 1, 255, grayGammas);
    plot.addLinePlot("Red", Color.red, 1, 255, redGammas);
    plot.addLinePlot("Green", Color.green, 1, 255, greenGammas);
    plot.addLinePlot("Blue", Color.blue, 1, 255, blueGammas);
    plot.setFixedBounds(0, 0, 256);
    drawToGraphicFile(plot, reportDirname + "/" + filename);
  }

  /**
   * 繪出gamma
   */
  private void drawGammaDifferential() {
    double targetGamma = p.customGamma;
    double tolerance = 0.1;
    double before = targetGamma - tolerance;
    double after = targetGamma + tolerance;
    double[] beforeGammaCurve = null;
    double[] afterGammaCurve = null;

    switch (p.gamma) {
      case Custom:
        beforeGammaCurve = GammaFinder.gammaCurve(GammaFinder.NormalInput, before);
        afterGammaCurve = GammaFinder.gammaCurve(GammaFinder.NormalInput, after);
        break;
      default:
        return;
    }

    double[] YArray = getLuminanceArray(measuredGrayLCDTarget,
                                        RGBBase.Channel.W);

    //==========================================================================
    // 產生tolerence的gamma亮度曲線
    //==========================================================================
    double maxLuminance = YArray[YArray.length - 1] - YArray[0];
    double[] beforeYCurve = DoubleArray.times(beforeGammaCurve, maxLuminance);
    double[] afterYCurve = DoubleArray.times(afterGammaCurve, maxLuminance);
    beforeYCurve = DoubleArray.plus(beforeYCurve, YArray[0]);
    afterYCurve = DoubleArray.plus(afterYCurve, YArray[0]);
    //==========================================================================

    //==========================================================================
    // 計算jndi
    //==========================================================================
    GSDFGradientModel beforegm = new GSDFGradientModel(beforeYCurve);
    GSDFGradientModel aftergm = new GSDFGradientModel(afterYCurve);
    GSDFGradientModel gm = new GSDFGradientModel(YArray);

    double[] JNDIArray = gm.getJNDIndexCurve();
    double[] beforeJNDIArray = beforegm.getJNDIndexCurve();
    double[] afterJNDIArray = aftergm.getJNDIndexCurve();

    double[] beforeDeltaJNDIArray = DoubleArray.minus(JNDIArray,
        beforeJNDIArray);
    double[] afterDeltaJNDIArray = DoubleArray.minus(JNDIArray, afterJNDIArray);
    //==========================================================================

    Plot2D plot = getPlot("Gamma Differential", "code", "delta JNDI");

    plot.addLinePlot("before(" + before + ")", 0, 256, beforeDeltaJNDIArray);
    plot.addLinePlot("after(" + after + ")", 0, 256, afterDeltaJNDIArray);
    plot.setFixedBounds(0, 0, 256);
    drawToGraphicFile(plot, reportDirname + "/gammaDiff.png");
  }

  /**
   * 取得亮度陣列
   * @param lcdTarget LCDTarget
   * @param ch Channel
   * @return double[]
   */
  private double[] getLuminanceArray(LCDTarget lcdTarget, RGBBase.Channel ch) {
    LCDTarget ramp = null;
    switch (ch) {
      case R:
      case G:
      case B:
        ramp = lcdTarget.targetFilter.getRamp256(ch, false);
        break;
      case W:
        ramp = lcdTarget.targetFilter.getRamp256W();
    }
    double[] YArray = ramp.filter.YArray();
    return YArray;
  }

  public void report() {
    CalibrateUtils.checkDir(reportDirname);
    CalibrateUtils.checkDir(measurementDirname);

    saveMeasurementAsLogoFile();
    saveMeasurementAsVastViewFile();
    saveMeasurementAsAUORampExcel();

    //==========================================================================
    // draw
    //==========================================================================
    drawDeltauvPrime();
    drawDeltaJNDI();
    drawRGBWAcceleration();
    drawRGBWOverRatio();
    drawGammaDifferential();
    drawGamma(false, "gamma.png");
    drawGamma(true, "gammaInJNDI.png");
    drawGammas("gammas.png");
    drawWhiteAndGreenGammaDifferentialInJNDI();
    //==========================================================================

    //==========================================================================
    // text
    //==========================================================================
    String whiteGamma = calculateGamma(false);
    String greenGamma = calculateGamma(true);
    String overScore = calculateOverScore();
    writeToFile(whiteGamma + "\r\n" + greenGamma + "\r\n" + overScore,
                reportDirname + "/CalibratedReport.txt");
    //==========================================================================
    closePlot(plot);
  }

  private GSDFGradientModel rgm, ggm, bgm, wgm;

  /**
   * 取得對應ch的GradientModel
   * @param ch Channel
   * @return GSDFGradientModel
   */
  private GSDFGradientModel getGradientModel(RGBBase.Channel ch) {
    switch (ch) {
      case R:
        if (rgm == null) {
          LCDTarget measureTarget = measuredLCDTarget.targetFilter.getRamp256(
              ch, true);
          rgm = getGradientModel(measureTarget, null, ch);
        }
        return rgm;
      case G:
        if (ggm == null) {
          ggm = getGradientModel(measuredGreenLCDTarget, greenTargetLCDTarget,
                                 ch);
        }
        return ggm;
      case B:
        if (bgm == null) {
          LCDTarget measureTarget = measuredLCDTarget.targetFilter.getRamp256(
              ch, true);
          bgm = getGradientModel(measureTarget, null, ch);
        }
        return bgm;
      case W:
        if (wgm == null) {
          wgm = getGradientModel(measuredGrayLCDTarget, targetLCDTarget, ch);
        }
        return wgm;
      default:
        return null;
    }
  }

  /**
   * 繪出加速度圖
   * @param ch Channel
   */
  private void drawAcceleration(RGBBase.Channel ch) {
    GSDFGradientModel gm = getGradientModel(ch);
    Plot2D plot = getPlot("Acceleration", "code", "acc");

    gm.plotPatternAccelInfo(plot, true, true);
    drawToGraphicFile(plot, reportDirname + "/accel-" + ch + ".png");
  }

  /**
   * 產生GradientModel
   * @param measureTarget LCDTarget 量測的結果Target
   * @param target LCDTarget 目標Target(用作計算delta用)
   * @param ch Channel model的channel
   * @return GSDFGradientModel
   */
  private GSDFGradientModel getGradientModel(LCDTarget measureTarget,
                                             LCDTarget target,
                                             RGBBase.Channel ch) {
    CIExyY[] targetxyYArray = target != null ? target.filter.xyYArray() : null;
    GSDFGradientModel gm = new GSDFGradientModel(measureTarget);
    gm.setImageChannel(ch);
    gm.setPatternSign(GSDFGradientModel.PatternSign.Threshold);
    gm.setTargetxyYArray(targetxyYArray);
    gm.setRecommendThresholdPercent(ch);
    return gm;
  }

  /**
   * 繪出RGBW的加速度圖
   */
  private void drawRGBWAcceleration() {
    drawAcceleration(RGBBase.Channel.R);
    drawAcceleration(RGBBase.Channel.G);
    drawAcceleration(RGBBase.Channel.B);
    drawAcceleration(RGBBase.Channel.W);
  }

  /**
   * 繪出RGBW的不smooth程度圖
   */
  private void drawRGBWOverRatio() {
    drawOverRatio(RGBBase.Channel.R);
    drawOverRatio(RGBBase.Channel.G);
    drawOverRatio(RGBBase.Channel.B);
    drawOverRatio(RGBBase.Channel.W);
  }

  /**
   * 繪出不smooth的程度圖
   * @param ch Channel
   */
  private void drawOverRatio(RGBBase.Channel ch) {
    GSDFGradientModel gm = getGradientModel(ch);
    List<Pattern> patternList = gm.getAllPatternIndex();
    Plot2D plot = this.getPlot("OverRation " + ch, "code", "%");
    for (Pattern pat : patternList) {
      plot.addCacheScatterLinePlot("", pat.index, pat.overRatio);
    }
    plot.setFixedBounds(0, 0, 256);
    drawToGraphicFile(plot, reportDirname + "/overratio-" + ch + ".png");
  }

  private String calculateOverScore() {
    StringBuilder buf = new StringBuilder();
    for (RGBBase.Channel ch : RGBBase.Channel.RGBWChannel) {
      GSDFGradientModel gm = getGradientModel(ch);
      double score = gm.getPatternAndScore().overScore;
      buf.append("Channel(" + ch + ") over score: " + score + "\r\n");
    }
    return buf.toString();
  }

  /**
   * 計算CPT的gamma指數
   * @param calculateGreen boolean 計算white or green
   * @return String
   */
  private String calculateGamma(boolean calculateGreen) {
    double[] YArray = null;
    if (!calculateGreen) {
      YArray = getLuminanceArray(this.measuredLCDTarget, RGBBase.Channel.G);
    }
    else {
      YArray = getLuminanceArray(this.measuredLCDTarget, RGBBase.Channel.W);
    }

    double[] normal = DoubleArray.minus(YArray, YArray[0]);
    Maths.normalize(normal, normal[normal.length - 1]);
    double gamma = GammaFinder.findGamma(GammaFinder.NormalInput, normal);
    double cptGamma1 = GammaFinder.findGammaCPT1(GammaFinder.NormalInput,
        normal);
    double cptGamma2 = GammaFinder.findGammaCPT1(GammaFinder.NormalInput,
        normal);

    StringBuilder buf = new StringBuilder();
    buf.append("[" + (calculateGreen ? "Green" : "White") + " Gamma]\r\n");
    buf.append("Gamma: " + gamma + "\r\n");
    buf.append("CPT Gamma1: " + cptGamma1 + "\r\n");
    buf.append("CPT Gamma2: " + cptGamma2);
    return buf.toString();
  }

  static void writeToFile(String text, String filename) {
    File file = new File(filename);
    try {
      FileWriter writer = new FileWriter(file);
      writer.write(text);
      writer.flush();
      writer.close();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }

  }
}
