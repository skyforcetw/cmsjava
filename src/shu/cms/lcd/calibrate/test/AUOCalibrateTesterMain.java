package shu.cms.lcd.calibrate.test;

import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.cms.lcd.calibrate.tester.*;
import shu.cms.lcd.material.*;
import shu.cms.util.*;
import shu.cms.measure.meter.*;
import jxl.read.biff.*;
import java.io.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AUOCalibrateTesterMain {

  private static class AUO9BitParameterProducer
      extends BasicParameterProducer {
    public MeasureParameter getMeasureParameter() {
      MeasureParameter mp = new MeasureParameter();
      mp.measureWaitTime = 750;
//      mp.rampLCDTarget = LCDTarget.Number.Ramp1021;
      mp.rampLCDTarget = LCDTarget.Number.Ramp257_6Bit;
//      mp.xtalkLCDTarget = LCDTarget.Number.Xtalk769;
      mp.xtalkLCDTarget = LCDTarget.Number.Xtalk589_6Bit;
      return mp;
    }

    public ColorProofParameter getInitCPParameter() {
      ColorProofParameter p = new ColorProofParameter();
      p.gamma = ColorProofParameter.Gamma.Scale;
      p.customGamma = 2.2;
      p.gammaBy = ColorProofParameter.GammaBy.G;
      p.calibrateBits = RGBBase.MaxValue.Int9Bit;
      p.icBits = RGBBase.MaxValue.Int10Bit;
      p.outputBits = RGBBase.MaxValue.Int10Bit;
      p.runCount = 1;
      p.cctAdaptiveStart = 5;
      return p;
    }

    public WhiteParameter[] getWhiteParameterArray() {
      WhiteParameter[] wpArray = new WhiteParameter[] {
          new WhiteParameter(new RGB(252, 252, 252))};
      for (WhiteParameter wp : wpArray) {
        wp.maxWhiteCode = 252;
      }
      return wpArray;
    }

    public int[] getTuneCodeArray() {
      int[] tuneCodeArray = new int[] {
          50};
      return tuneCodeArray;
    }

    public double[] getGammaArray() {
      double[] gammaArray = new double[] {
          2.2};
      return gammaArray;
    }

    public AdjustParameter getAdjustParameter() {
      AdjustParameter ap = new AdjustParameter();

      ap.runModelReport = false;

      ap.luminanceBasedCalibrate = true;
      ap.luminanceCalibratedInterval = 4;
      ap.runLuminanceBasedReport = false;

      ap.whiteBasedCalibrate = false;
      ap.whiteCalibratedInterval = 4;
      ap.runWhiteBasedReport = false;

      ap.greenBasedCalibrate = true;
      ap.greenCalibratedInterval = 4;
      ap.runGreenBasedReport = false;

      ap.luminanceBased2Calibrate = false;
      ap.luminanceCalibrated2Interval = 1;
      ap.runLuminanceBased2Report = false;

      ap.quantizationCollapseFix = true;
      ap.concernCollapseFixable = true;

      ap.smoothGreenCalibrate = false;
      ap.smoothGreenBasedOn = AdjustParameter.GreenBased.Model;
      ap.smoothGreenCompromiseCalibrate = false;

      return ap;
    }

    public ColorProofParameter.CCTCalibrate[] getCCTCalibrateArray() {
      ColorProofParameter.CCTCalibrate[] cctCalArray = new ColorProofParameter.
          CCTCalibrate[] {
          ColorProofParameter.CCTCalibrate.uvpByDE00,
      };
      return cctCalArray;
    }
  }

  private static class AUOTestParameterProducer
      extends AUO10BitParameterProducer {
    public ColorProofParameter getInitCPParameter() {
      ColorProofParameter p = new ColorProofParameter();
      p.gamma = ColorProofParameter.Gamma.Custom;
      p.customGamma = 2.4;
      p.gammaBy = ColorProofParameter.GammaBy.W;
      p.calibrateBits = RGBBase.MaxValue.Int10Bit;
      p.icBits = RGBBase.MaxValue.Int12Bit;
      p.outputBits = RGBBase.MaxValue.Int12Bit;
      p.runCount = 1;
      p.cctAdaptiveStart = 2;
      /*try {
        RGB[] cpcodeArray = RGBArray.loadAUOExcel(
            "D:\\skyforce\\Gamma Table\\Calib00_Table.xls",
            RGBBase.MaxValue.Int12Bit);
        cpcodeArray = RGBArray.deepClone(cpcodeArray, 0, 256);
        p.injectedCPCodeArray = cpcodeArray;
             }
             catch (BiffException ex) {
        ex.printStackTrace();
             }
             catch (IOException ex) {
        ex.printStackTrace();
             }*/
      return p;
    }

    public AdjustParameter getAdjustParameter() {
      AdjustParameter ap = new AdjustParameter();

      ap.runModelReport = true;
      ap.setCalibrate(true, true, true, false, false);
      ap.setRunMeasuredReport(true, true, true, false, false);
      ap.setInterval(2, 2, 2, 2, 2);

      ap.quantizationCollapseFix = true;
      ap.concernCollapseFixable = true;

      ap.setSmoothGreen(false, AdjustParameter.GreenBased.Model, false);

      return ap;
    }

    public MeasureParameter getMeasureParameter() {
      MeasureParameter mp = new MeasureParameter();
      mp.measureWaitTime = 750;
//      mp.rampLCDTarget = LCDTarget.Number.Ramp1021;
      mp.rampLCDTarget = LCDTarget.Number.Ramp257_6Bit;
//      mp.xtalkLCDTarget = LCDTarget.Number.Xtalk769;
      mp.xtalkLCDTarget = LCDTarget.Number.Xtalk589_6Bit;
      return mp;
    }
  }

  private static class AUO10BitParameterProducer
      extends BasicParameterProducer {
    public MeasureParameter getMeasureParameter() {
      MeasureParameter mp = new MeasureParameter();
      mp.measureWaitTime = 300;
      mp.rampLCDTarget = LCDTarget.Number.Ramp1021;
//      mp.rampLCDTarget = LCDTarget.Number.Ramp257_6Bit;
      mp.xtalkLCDTarget = LCDTarget.Number.Xtalk769;
//      mp.xtalkLCDTarget = LCDTarget.Number.Xtalk589_6Bit;
      return mp;
    }

    public ColorProofParameter getInitCPParameter() {
      ColorProofParameter p = new ColorProofParameter();
      p.gamma = ColorProofParameter.Gamma.Custom;
      p.customGamma = 2.2;
      p.gammaBy = ColorProofParameter.GammaBy.W;
      p.calibrateBits = RGBBase.MaxValue.Int10Bit;
      p.icBits = RGBBase.MaxValue.Int12Bit;
      p.outputBits = RGBBase.MaxValue.Int12Bit;
      p.runCount = 1;
      p.cctAdaptiveStart = 5;
      return p;
    }

    public WhiteParameter[] getWhiteParameterArray() {
      WhiteParameter[] wpArray = new WhiteParameter[] {
          new WhiteParameter(new RGB(255, 255, 255))};
      for (WhiteParameter wp : wpArray) {
        wp.maxWhiteCode = 255;
      }
      return wpArray;
    }

    public int[] getTuneCodeArray() {
      int[] tuneCodeArray = new int[] {
          50};
      return tuneCodeArray;
    }

    public double[] getGammaArray() {
      double[] gammaArray = new double[] {
          2.2};
      return gammaArray;
    }

    public AdjustParameter getAdjustParameter() {
      AdjustParameter ap = new AdjustParameter();

      ap.runModelReport = true;

      ap.luminanceBasedCalibrate = true;
      ap.luminanceCalibratedInterval = 8;
      ap.runLuminanceBasedReport = false;

      ap.whiteBasedCalibrate = true;
      ap.whiteCalibratedInterval = 2;
      ap.runWhiteBasedReport = false;

      ap.greenBasedCalibrate = true;
      ap.greenCalibratedInterval = 2;
      ap.runGreenBasedReport = true;

      ap.luminanceBased2Calibrate = false;
      ap.luminanceCalibrated2Interval = 1;
      ap.runLuminanceBased2Report = false;

      ap.quantizationCollapseFix = true;
      ap.concernCollapseFixable = true;

      ap.smoothGreenCalibrate = false;
      ap.smoothGreenBasedOn = AdjustParameter.GreenBased.Model;
      ap.smoothGreenCompromiseCalibrate = false;

      return ap;
    }

    public ColorProofParameter.CCTCalibrate[] getCCTCalibrateArray() {
      ColorProofParameter.CCTCalibrate[] cctCalArray = new ColorProofParameter.
          CCTCalibrate[] {
          ColorProofParameter.CCTCalibrate.uvpByDE00,
      };
      return cctCalArray;
    }
  }

  public static void main(String[] args) {
    AutoCPOptions.setTestEnvironment();
    BasicParameterProducer tpp = new AUO9BitParameterProducer();
//    BasicParameterProducer tpp = new AUO10BitParameterProducer();
//    BasicParameterProducer tpp = new AUOTestParameterProducer();

    TestTaskProducer producer = new TestTaskProducer(tpp);

    CalibrateTester tester = new CalibrateTester(new InfoAdapter(tpp) {
      public Meter getMeter() {
        return Material.getMeter();
      }

      public LCDTarget getOriginalRamp1021Target() {
        return Material.getRamp1021Target();
      }

      public boolean isMeasuredCalibrate() {
        return false;
      }
    });

    CalibrateTester.TestTask[] taskArray = producer.getTaskArray();

    for (CalibrateTester.TestTask t : taskArray) {
      tester.addTask(t);
    }
    try {
      tester.excute();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
//    CalibratedResult result = taskArray[taskArray.length - 1].calibratedResult;
    RGB[] finalResult = taskArray[taskArray.length -
        1].calibratedResult.getFinalResult();

  }

}

/**
 * 若要在AutoCP中插入CCTv3的校正結果, 有幾個方式可以達成:
 * 1. 插入在Model產出CP Code之後
 *  Model過程不可以直接省略掉, 因為目標在此產出.
 *  可能的作法是在目標產出後, 直接把要回傳的CP Code代換掉然後結束.
 *  就不要進入產出CP Code的階段.
 *  (先採用此作法)
 *
 * 2. 在Measured階段, 偷偷把model產出的結果代換掉.
 *  但是這樣變成Model做了一些白工.
 * 3. 把目標產出跟Model分離, 然後Model的步驟直接替換掉. =>最理想但是耗時, 有必要?
 * (有時間朝這目標改一下, 頂多一天吧)
 */
