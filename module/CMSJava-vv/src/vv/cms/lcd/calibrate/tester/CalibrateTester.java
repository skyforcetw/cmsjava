package vv.cms.lcd.calibrate.tester;

import java.io.*;
import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import vv.cms.lcd.calibrate.*;
import vv.cms.lcd.calibrate.measured.*;
import vv.cms.lcd.calibrate.measured.Interpolator;
import vv.cms.lcd.calibrate.measured.util.*;
import vv.cms.lcd.calibrate.modeled.*;
import vv.cms.lcd.calibrate.parameter.*;
import shu.cms.lcd.material.*;
import shu.cms.measure.*;
import vv.cms.measure.cp.*;
import shu.cms.measure.meter.*;
import shu.cms.util.*;
import shu.util.log.*;
import vv.cms.lcd.material.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 這是一個用來進行校正以及將校正結果進行報告的類別.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CalibrateTester
    implements Plottable {
  public final static String CALIBRATE_DIRNAME = "lcd.calibrate";

  public static class TestTask {
    public CalibratedResult calibratedResult;
    public Parameters parameters;
    public TestTask(Parameters parameters) {
      this.parameters = parameters;
    }

    public final boolean isLuminanceBasedCalibrated() {
      return parameters.adjustParameter.luminanceBasedCalibrate;
    }

    public final boolean isWhiteBasedCalibrated() {
      return parameters.adjustParameter.whiteBasedCalibrate;
    }

    public final boolean isWhiteBased2Calibrated() {
      return parameters.adjustParameter.whiteBased2Calibrate;
    }

    /**
     *
     * @return boolean
     */
    public final boolean isSmoothGreenCalibrated() {
      return parameters.adjustParameter.smoothGreenCalibrate;
    }

    public final boolean isGreenBasedCalibrated() {
      return parameters.adjustParameter.greenBasedCalibrate;
    }

    public final boolean isLuminanceBased2Calibrated() {
      return parameters.adjustParameter.luminanceBased2Calibrate;
    }
  }

  private List<TestTask> taskList = new ArrayList<TestTask> ();

  public void addTask(TestTask testTask) {
    taskList.add(testTask);
  }

  public void excute() {
    int size = taskList.size();
    for (int x = 0; x < size; x++) {
      TestTask task = taskList.get(x);
      String dir = this.calibrateDirname + "/(" + x + ")" +
          info.getOutputDirname(task);
      CalibratedResult result = excute0(task, dir);
      task.calibratedResult = result;
      resultList.add(result);
      clear(task);
    }
    Logger.log.info("Calibrate end.");
  }

  private List<CalibratedResult> resultList = new ArrayList<CalibratedResult> ();

  public CalibratedResult getCalibratedResult(int index) {
    return resultList.get(index);
  }

  private void clear(TestTask task) {
    System.gc();
  }

  private static enum ReportType {
    LCDModel(1), LuminanceBased(2), WhiteBased(3), GreenBased(4), Measured( -1),
    WhiteBased2(5), LuminanceBased2(6), ;

    ReportType(int number) {
      this.number = number;
    }

    public int number;
  }

  public final static String getCalibrateResultFilename(ReportType type,
      InfoInterface info, String dirname) {
    switch (type) {
      case LCDModel:
        return dirname + "/" + info.getModelCalibrateFilename();
      case Measured:
        return dirname + "/" + info.getMeasuredCalibrateFilename();
      case LuminanceBased:
        return dirname + "/" + info.getLuminanceBasedResultFilename();
      case WhiteBased:
        return dirname + "/" + info.getWhiteBasedResultFilename();
      case GreenBased:
        return dirname + "/" + info.getGreenBasedResultFilename();
      case WhiteBased2:
        return dirname + "/" + info.getWhiteBased2ResultFilename();
      case LuminanceBased2:
        return dirname + "/" + info.getLuminanceBased2ResultFilename();
    }
    return null;
  }

  public final static String getCalibrateTargetFilename(ReportType type,
      InfoInterface info, String dirname) {
    switch (type) {
      case LCDModel:
        return dirname + "/" + info.getModelCalibrateFilename() + ".logo";
      case Measured:
      case LuminanceBased:
      case LuminanceBased2:
      case WhiteBased:
      case WhiteBased2:
      case GreenBased:
        return dirname + "/" + info.getMeasuredCalibrateFilename() + ".logo";
    }
    return null;
  }

  private static String getReportDrianme(String dirname, ReportType type) {
    return dirname + "/(" + type.number + ")" + type.name() + "Report";
  }

  private RGB[] getCPCode(TestTask task, String dirname, ReportType type) {
    //先找到對應的cpcode
    String calibrateResult = getCalibrateResultFilename(type, info, dirname);
    calibrateResult = (calibrateResult.indexOf(".xls") == -1) ?
        calibrateResult + ".xls" : calibrateResult;
    RGB[] rgbArray = null;
    try {
      switch (task.parameters.colorProofParameter.outputFileFormat) {
        case VastView:
          rgbArray = RGBArray.loadVVExcel(calibrateResult);
          break;
        case AUO:
          rgbArray = RGBArray.loadAUOExcel(calibrateResult,
                                           task.parameters.colorProofParameter.
                                           outputBits);
          break;
      }
    }
    catch (jxl.read.biff.BiffException ex) {
      Logger.log.error("", ex);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    return rgbArray;
  }

  private void runReport(TestTask task, String dirname, ReportType type) {
    String reportDirname = getReportDrianme(dirname, type);
    CalibrateUtils.checkDir(reportDirname);
    MeterMeasurement mm = info.getMeterMeasurement();

    //==========================================================================
    // 載入 cpcode
    //==========================================================================
    //找到對應的cpcode
    RGB[] rgbArray = getCPCode(task, dirname, type);
    //載入到rom
    ColorProofParameter p = task.parameters.colorProofParameter;
    CPCodeLoader.load(rgbArray, p.icBits);
    //==========================================================================

    //==========================================================================
    // 量測
    //==========================================================================
    LCDTarget measuredLCDTarget = LCDTarget.Measured.measure(mm,
        LCDTarget.Number.Ramp1021);
    //==========================================================================

    //==========================================================================
    // 載入目標target
    //==========================================================================
    String targetFilename = getCalibrateTargetFilename(type, info, dirname);
    LCDTarget targetLCDTarget = LCDTargetUtils.getLogoLCDTarget(targetFilename);

    LCDTarget greenTargetLCDTarget = null;
    if (info.isMeasuredCalibrate() && task.isGreenBasedCalibrated()) {
      //如果有綠色的target則載入
      if (task.isSmoothGreenCalibrated()) {
        greenTargetLCDTarget = LCDTargetUtils.getLogoLCDTarget(utils.
            getSmoothGreenTargetLogoFilename(dirname));
      }
      else {
        greenTargetLCDTarget = LCDTargetUtils.getLogoLCDTarget(utils.
            getGreenTargetLogoFilename(dirname));
      }
    }
    //==========================================================================

    CalibrateReporter reporter = new CalibrateReporter(measuredLCDTarget,
        targetLCDTarget, greenTargetLCDTarget, p, reportDirname,
        reportDirname + "/measurement");
    reporter.report();
    reporter = null;
  }

  private IndependentCoordinateCalibrator.Batch[] getBatchArray(AdjustParameter
      ap) {
    IndependentCoordinateCalibrator.Batch b1 = new
        IndependentCoordinateCalibrator.Batch(ap.luminanceBasedCalibrate,
                                              info.getLuminanceCalibrated(), true, false,
                                              CalibratorConst.
                                              IndependLuminanceBasedBefInterp,
                                              CalibratorConst.
                                              IndependLuminanceBased,
                                              CalibratorConst.
                                              IndependLuminanceBasedCube,
                                              CalibratorConst.InterpTest +
                                              CalibratorConst.
                                              IndependLuminanceBased,
                                              ap.luminanceCalibratedInterval);

    IndependentCoordinateCalibrator.Batch b2 = new
        IndependentCoordinateCalibrator.Batch(ap.whiteBasedCalibrate,
                                              info.getWhiteCalibrated(), true, true,
                                              CalibratorConst.
                                              IndependWhiteBasedBefInterp,
                                              CalibratorConst.
                                              IndependWhiteBased,
                                              CalibratorConst.
                                              IndependWhiteBasedCube,
                                              CalibratorConst.InterpTest +
                                              CalibratorConst.
                                              IndependWhiteBased,
                                              ap.whiteCalibratedInterval);

    IndependentCoordinateCalibrator.Batch b3 = new
        IndependentCoordinateCalibrator.Batch(ap.greenBasedCalibrate,
                                              info.getGreenCalibrated(), false, true,
                                              CalibratorConst.
                                              IndependGreenBasedBefInterp,
                                              CalibratorConst.
                                              IndependGreenBased,
                                              CalibratorConst.
                                              IndependGreenBasedCube,
                                              CalibratorConst.InterpTest +
                                              CalibratorConst.
                                              IndependGreenBased,
                                              ap.greenCalibratedInterval);

    IndependentCoordinateCalibrator.Batch b4 = new
        IndependentCoordinateCalibrator.Batch(ap.whiteBased2Calibrate,
                                              info.getWhiteCalibrated2(), true, true,
                                              CalibratorConst.
                                              IndependWhiteBased2BefInterp,
                                              CalibratorConst.
                                              IndependWhiteBased2,
                                              CalibratorConst.
                                              IndependWhiteBased2Cube,
                                              CalibratorConst.InterpTest +
                                              CalibratorConst.
                                              IndependWhiteBased2,
                                              ap.whiteCalibrated2Interval);

    IndependentCoordinateCalibrator.Batch b5 = new
        IndependentCoordinateCalibrator.Batch(ap.luminanceBased2Calibrate,
                                              info.getLuminanceCalibrated2(), true, false,
                                              CalibratorConst.
                                              IndependLuminanceBased2BefInterp,
                                              CalibratorConst.
                                              IndependLuminanceBased2,
                                              CalibratorConst.
                                              IndependLuminanceBased2Cube,
                                              CalibratorConst.InterpTest +
                                              CalibratorConst.
                                              IndependLuminanceBased2,
                                              ap.luminanceCalibrated2Interval);

    return new
        IndependentCoordinateCalibrator.Batch[] {
        b1, b2, b3, b4, b5};
  }

  private RGB[][] runMeasuredCalibrator(TestTask task, String dirname) {
    //==========================================================================
    // setup
    //==========================================================================
    ColorProofParameter p = task.parameters.colorProofParameter;
    WhiteParameter wp = task.parameters.whiteParameter;
    AdjustParameter ap = task.parameters.adjustParameter;
    MeasureParameter mp = task.parameters.measureParameter;
    LCDModel lcdModel = task.parameters.lcdModel;
    //==========================================================================

    //==========================================================================
    // coordinate cal
    //==========================================================================
    String modelResultFilename = utils.getModelCalibratorFilename(dirname) +
        ".logo";

    LCDTarget lcdTarget = info.getOriginalRamp1021Target();
    IndependentCoordinateCalibrator cal = new IndependentCoordinateCalibrator(
        LCDTargetUtils.getLogoLCDTarget(modelResultFilename),
        lcdTarget, info.getMeterMeasurement(), p, wp, ap, mp);
    cal.setInterpolator(new Interpolator(ap.interpolateMethod));
    cal.setPlotting(this.plotting);
    cal.setRootDir(dirname);
    //==========================================================================

    //==========================================================================

    //========================================================================
    // 參數設定區
    //========================================================================
    cal.setLCDModel(lcdModel);
    cal.setChromaticExpandForChromaAround(true);
    cal.setChromaticExpandForStepAround(true);
    cal.addBatch(getBatchArray(ap));
    //========================================================================
    RGB[] result = cal.calibrate();

    cal.plot.plotCalibratedIndex(CoordinateCalibrator.Index.Luminance);
    cal.plot.plotCalibratedIndex(CoordinateCalibrator.Index.CIEuv1960);
    cal.plot.plotCalibratedIndex(CoordinateCalibrator.Index.DeltaE);

    //========================================================================
    // store
    //========================================================================
    String calibrateResult = utils.getMeasuredCalibratorFilename(dirname);
    //儲存cp code
    ColorProofParameter cp = task.parameters.colorProofParameter;
    CalibrateUtils.storeRGBArrayExcel(result, calibrateResult + ".xls", cp);
    //存到logo file
    MeasuredUtils.saveToLogoFile(calibrateResult + ".logo",
                                 cal.getCalibratedPatchList());
    //========================================================================

    //========================================================================
    // 資訊的顯示
    //========================================================================
    Logger.log.info(cal.getCalibratedInfomation());
    Logger.log.info("RedundantMeasureCount: " + cal.getRedundantMeasure());
    //==========================================================================

    RGB[][] calibratedResult = cal.getCalibratedResult();
    cal.close();
    cal = null;

    return calibratedResult;
  }

  private Utils utils = new Utils();
  private class Utils {

    String getModelCalibratorFilename(String dirname) {
      return getCalibrateResultFilename(ReportType.LCDModel, info, dirname);
    }

    String getMeasuredCalibratorFilename(String dirname) {
      return getCalibrateResultFilename(ReportType.Measured, info, dirname);
    }

    String getGreenTargetLogoFilename(String dirname) {
      return dirname + "/" + info.getGreenTargetLogoFilename();
    }

    String getSmoothGreenTargetLogoFilename(String dirname) {
      return dirname + "/" + info.getSmoothGreenTargetLogoFilename();
    }
  }

  private RGB[] runLCDModelCalibrator(TestTask task, String dirname) {
    LCDModel lcdModel = task.parameters.lcdModel;
    lcdModel.setDisplayLUT(null);
    LCDModelCalibrator cal = new LCDModelCalibrator(task.parameters);
    cal.setRootDir(dirname);
    cal.setPlotting(this.plotting);

    RGB[] result = cal.calibrate();
    Logger.log.info("irregularCount: " +
                    IrregularUtil.irregularCount(result));
    Logger.log.info(cal.getDeltauvPrimeReport());
    //==========================================================================
    // store
    //==========================================================================
    //儲存model的校正結果
    String filename = utils.getModelCalibratorFilename(dirname);
    ColorProofParameter cp = task.parameters.colorProofParameter;
    CalibrateUtils.storeRGBArrayExcel(result, filename + ".xls", cp);

    //儲存model的校正target
    MeasuredUtils.saveToLogoFile(filename + ".logo", cal.getTargetPatchList());
    cal = null;

    //儲存量測的ramp和xtalk target
    try {
      String targetDirname = dirname + "/OriginalTarget";
      CalibrateUtils.checkDir(targetDirname);

      LCDTarget rampTarget = lcdModel.getLCDTarget();
      int rampPatchCount = rampTarget.getNumber().getPatchCount();
      LCDTarget.IO.store(rampTarget,
                         targetDirname + "/" + rampPatchCount + ".logo");

      if (lcdModel instanceof ChannelDependentModel) {
        ChannelDependentModel dependModel = (ChannelDependentModel) lcdModel;
        LCDTarget xtalkTarget = dependModel.getXtalkLCDTarget();
        int xtalkPatchCount = xtalkTarget.getNumber().getPatchCount();
        LCDTarget.IO.store(xtalkTarget,
                           targetDirname + "/" + xtalkPatchCount + ".logo");
      }
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    //==========================================================================
    return result;
  }

  private void runParameterReport(TestTask task, String dirname) {
    StringBuilder buf = new StringBuilder();
    String replaceNewLine = "\r\n\t";
    buf.append("[ColorProofParameter]\r\n");
    buf.append("\t" +
               task.parameters.colorProofParameter.toString().replaceAll("\n",
        replaceNewLine) + "\r\n\r\n");
    buf.append("[WhiteParameter]\r\n");
    buf.append("\t" + task.parameters.whiteParameter.toString().replaceAll("\n",
        replaceNewLine) + "\r\n\r\n");
    buf.append("[AdjustParameter]\r\n");
    buf.append("\t" +
               task.parameters.adjustParameter.toString().replaceAll("\n",
        replaceNewLine) + "\r\n\r\n");
    buf.append("[MeasureParameter]\r\n");
    buf.append("\t" +
               task.parameters.measureParameter.toString().replaceAll("\n",
        replaceNewLine) + "\r\n\r\n");
    buf.append("[ViewingParameter]\r\n");
    buf.append("\t" +
               task.parameters.viewingParameter.toString().replaceAll("\n",
        replaceNewLine) + "\r\n\r\n");
    String parameter = buf.toString();
    Logger.log.trace(parameter);
    CalibrateReporter.writeToFile(parameter, dirname + "/parameter.txt");
  }

  private CalibratedResult excute0(TestTask task, String dirname) {
    CalibrateUtils.checkDir(dirname);

    //==========================================================================
    // 將校正參數儲存起來
    //==========================================================================
    runParameterReport(task, dirname);
    //==========================================================================


    //==========================================================================
    // 以lcd model方式校正
    //==========================================================================
    Logger.log.info("LCDModel calibrate start...");
    RGB[] modelResult = runLCDModelCalibrator(task, dirname);
    Logger.log.info("LCDModel calibrate end.");
    //==========================================================================

    //==========================================================================
    // 對measure方式校正
    //==========================================================================
    RGB[][] measuredResult = null;
    if (info.isMeasuredCalibrate()) {
      Logger.log.info("Measured calibrate start...");
      measuredResult = runMeasuredCalibrator(task, dirname);
      Logger.log.info("Measured calibrate end.");
    }
    //==========================================================================

    AdjustParameter ap = task.parameters.adjustParameter;

    //==========================================================================
    // 進行報告
    //==========================================================================
    if (ap.runModelReport) {
      Logger.log.info("Model report start...");
      runReport(task, dirname, ReportType.LCDModel);
      Logger.log.info("Model report end.");
    }
    if (info.isMeasuredCalibrate()) {
      if (task.isLuminanceBasedCalibrated() && ap.runLuminanceBasedReport) {
        Logger.log.info("LuminanceBased report start...");
        runReport(task, dirname, ReportType.LuminanceBased);
        Logger.log.info("LuminanceBased report end.");
      }

      if (task.isWhiteBasedCalibrated() && ap.runWhiteBasedReport) {
        Logger.log.info("WhiteBased report start...");
        runReport(task, dirname, ReportType.WhiteBased);
        Logger.log.info("WhiteBased report end.");
      }

      if ( (task.isGreenBasedCalibrated() || task.isSmoothGreenCalibrated()) &&
          ap.runGreenBasedReport) {
        Logger.log.info("GreenBased report start...");
        runReport(task, dirname, ReportType.GreenBased);
        Logger.log.info("GreenBased report end.");
      }
      if (task.isWhiteBased2Calibrated() && ap.runWhiteBased2Report) {
        Logger.log.info("WhiteBased2 report start...");
        runReport(task, dirname, ReportType.WhiteBased2);
        Logger.log.info("WhiteBased2 report end.");
      }
      if (task.isLuminanceBased2Calibrated() && ap.runLuminanceBased2Report) {
        Logger.log.info("LuminanceBased2 report start...");
        runReport(task, dirname, ReportType.LuminanceBased2);
        Logger.log.info("LuminanceBased2 report end.");
      }
    }
    //==========================================================================

    if (extra != null) {
      extra.extraProcess(task, dirname, info);
    }
    if (measuredResult != null) {
      return new CalibratedResult(modelResult, measuredResult[0],
                                  measuredResult[1], measuredResult[2],
                                  measuredResult[3], measuredResult[4]);
    }
    else {
      return new CalibratedResult(modelResult);
    }
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 提供校正時所需訊息
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static interface InfoInterface {
    /**
     * 輸出的目錄名稱
     * @param task TestTask
     * @return String
     */
    public String getOutputDirname(TestTask task);

    /**
     * 取得bypass狀況下的1021灰階
     * @return LCDTarget
     */
    public LCDTarget getOriginalRamp1021Target();

    public String getModelCalibrateFilename();

    public String getMeasuredCalibrateFilename();

    public String getLuminanceBasedResultFilename();

    public String getWhiteBasedResultFilename();

    public String getGreenBasedResultFilename();

    public String getLuminanceBased2ResultFilename();

    public String getGreenTargetLogoFilename();

    public String getWhiteBased2ResultFilename();

    public String getSmoothGreenTargetLogoFilename();

    /**
     * 是否進行量測校正
     * @return boolean
     */
    public boolean isMeasuredCalibrate();

    public Meter getMeter();

    public MeterMeasurement getMeterMeasurement();

    public boolean[] getLuminanceCalibrated();

    public boolean[] getWhiteCalibrated();

    public boolean[] getGreenCalibrated();

    public boolean[] getWhiteCalibrated2();

    public boolean[] getLuminanceCalibrated2();

  }

  /**
   *
   * @param infoIF InfoInterface 提供資訊的物件
   */
  public CalibrateTester(InfoInterface infoIF) {
    this(CALIBRATE_DIRNAME, infoIF, null);
  }

  /**
   *
   * @param infoIF InfoInterface 提供資訊的物件
   * @param extraIF ExtraProcessIF 額外處理的物件
   */
  public CalibrateTester(InfoInterface infoIF, ExtraProcessIF extraIF) {
    this(CALIBRATE_DIRNAME, infoIF, extraIF);
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 經過標準校正以及報告後, 如有額外的處理需要, 實作此介面並加入到CalibrateTester中.
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static interface ExtraProcessIF {
    public void extraProcess(TestTask task, String dirname, InfoInterface info);
  }

  /**
   *
   * @param calibrateDirname String 校正所儲存的目錄
   * @param infoIF InfoInterface 提供資訊的物件
   * @param extraIF ExtraProcessIF 額外處理的物件
   */
  public CalibrateTester(String calibrateDirname, InfoInterface infoIF,
                         ExtraProcessIF extraIF) {
    CalibrateUtils.checkDir(calibrateDirname);
    this.calibrateDirname = calibrateDirname;
    this.info = infoIF;
    this.extra = extraIF;
  }

  private String calibrateDirname;
  private InfoInterface info;
  private ExtraProcessIF extra;
  private boolean plotting = AutoCPOptions.get("CalibrateTester_Plotting");
  public void setPlotting(boolean plotting) {
    this.plotting = plotting;
  }

}
