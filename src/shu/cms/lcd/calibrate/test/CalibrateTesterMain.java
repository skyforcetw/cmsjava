package shu.cms.lcd.calibrate.test;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.cms.lcd.calibrate.tester.*;
import shu.cms.lcd.material.*;
import shu.cms.measure.meter.*;

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
public class CalibrateTesterMain {

  private static class TestParameterProducer12Bit
      extends ApongTestParameterProducer {
    public ColorProofParameter getInitCPParameter() {
      ColorProofParameter p = new ColorProofParameter();
      p.gamma = ColorProofParameter.Gamma.Custom;
//      p.gamma = ColorProofParameter.Gamma.Native;
      p.customGamma = 2.2;
      p.gammaBy = ColorProofParameter.GammaBy.W;
//      p.gammaBy = ColorProofParameter.GammaBy.G;
      p.calibrateBits = RGBBase.MaxValue.Int12Bit;
      p.icBits = RGBBase.MaxValue.Int12Bit;
      p.runCount = 99;
      return p;
    }

    public AdjustParameter getAdjustParameter() {
      AdjustParameter ap = new AdjustParameter();
      ap.luminanceBasedCalibrate = true;
      ap.luminanceCalibratedInterval = 2;
      ap.whiteBasedCalibrate = true;
      ap.whiteCalibratedInterval = 2;
      ap.greenBasedCalibrate = true;
      ap.greenCalibratedInterval = 1;

      ap.smoothGreenCalibrate = false;
      ap.smoothGreenCompromiseCalibrate = true;
      return ap;
    }

    public ColorProofParameter.CCTCalibrate[] getCCTCalibrateArray() {
      ColorProofParameter.CCTCalibrate[] cctCalArray = new ColorProofParameter.
          CCTCalibrate[] {
//          ColorProofParameter.CCTCalibrate.Corrected,
          ColorProofParameter.CCTCalibrate.uvpByDE00};
//      Parameter.CCTCalibrate[] cctCalArray = new Parameter.CCTCalibrate[] {
//          Parameter.CCTCalibrate.uvpByDE00};
      return cctCalArray;
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
  }

  private static class ApongTestParameterProducer
      extends BasicParameterProducer {
    public ColorProofParameter getInitCPParameter() {
      ColorProofParameter p = new ColorProofParameter();
      p.gamma = ColorProofParameter.Gamma.Custom;
      p.customGamma = 2.2;
      p.gammaBy = ColorProofParameter.GammaBy.W;
      p.calibrateBits = RGBBase.MaxValue.Int10Bit;
      p.icBits = RGBBase.MaxValue.Int10Bit;
      p.runCount = 1;
      return p;
    }

    public WhiteParameter[] getWhiteParameterArray() {
//      WhiteParameter[] wpArray = new WhiteParameter[] {
//          new WhiteParameter(9300)};
      WhiteParameter[] wpArray = new WhiteParameter[] {
          new WhiteParameter(new RGB(254, 254, 254))};
      for (WhiteParameter wp : wpArray) {
        wp.maxWhiteCode = 254;
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

      ap.luminanceBasedCalibrate = true;
      ap.luminanceCalibratedInterval = 2;
      ap.whiteBasedCalibrate = true;
      ap.whiteCalibratedInterval = 2;
      ap.greenBasedCalibrate = true;
      ap.greenCalibratedInterval = 1;
      ap.luminanceBased2Calibrate = true;
      ap.luminanceCalibrated2Interval = 2;

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

  private static class TestParameterProducer2
      extends BasicParameterProducer {

    public ColorProofParameter getInitCPParameter() {
      ColorProofParameter p = super.getInitCPParameter();
      p.calibrateBits = RGB.MaxValue.Int10Bit;
      return p;
    }

    public ColorProofParameter.CCTCalibrate[] getCCTCalibrateArray() {
      ColorProofParameter.CCTCalibrate[] cctCalArray = new ColorProofParameter.
          CCTCalibrate[] {
          ColorProofParameter.CCTCalibrate.uvpByDE00};
      return cctCalArray;
    }

    public WhiteParameter[] getWhiteParameterArray() {
      WhiteParameter[] wpArray = new WhiteParameter[] {
          new WhiteParameter(new RGB(254, 254, 254))};
      for (WhiteParameter wp : wpArray) {
        wp.maxWhiteCode = 254;
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
      ap.luminanceBasedCalibrate = false;
      ap.luminanceCalibratedInterval = 1;
      ap.whiteBasedCalibrate = true;
      ap.whiteCalibratedInterval = 1;
      ap.greenBasedCalibrate = false;
      ap.smoothGreenCalibrate = false;
      ap.smoothGreenCompromiseCalibrate = false;
      return ap;
    }

    public MeasureParameter getMeasureParameter() {
      MeasureParameter mp = new MeasureParameter();
      mp.measureBlankInsert = false;
//      mp.blankColor = new Color(254, 254, 254);
      mp.blankColor = new Color(0, 0, 0);
//      mp.measureWaitTime = 600;
      mp.bufferMeasure = false;
      return mp;
    }

  }

  private static class TestParameterProducer
      extends BasicParameterProducer {

    public ColorProofParameter getInitCPParameter() {
      ColorProofParameter p = new ColorProofParameter();
      p.gamma = ColorProofParameter.Gamma.Custom;
      p.customGamma = 2.2;
      p.gammaBy = ColorProofParameter.GammaBy.W;
      p.calibrateBits = RGBBase.MaxValue.Int10Bit;
      p.icBits = RGBBase.MaxValue.Int10Bit;
      p.runCount = 1;

      return p;
    }

    public ColorProofParameter.CCTCalibrate[] getCCTCalibrateArray() {
      ColorProofParameter.CCTCalibrate[] cctCalArray = new ColorProofParameter.
          CCTCalibrate[] {
          ColorProofParameter.CCTCalibrate.IPT};
      return cctCalArray;
    }

    public WhiteParameter[] getWhiteParameterArray() {
      WhiteParameter[] wpArray = new WhiteParameter[] {
          new WhiteParameter(new RGB(254, 254, 254))};
      for (WhiteParameter wp : wpArray) {
        wp.maxWhiteCode = 254;
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
      ap.luminanceBasedCalibrate = true;
//      ap.luminanceBasedCalibrate = true;
      ap.luminanceCalibratedInterval = 4;
      ap.whiteBasedCalibrate = false;
      ap.whiteCalibratedInterval = 1;
      ap.greenBasedCalibrate = false;
      ap.luminanceBased2Calibrate = false;

      ap.smoothGreenCalibrate = false;
      ap.smoothGreenCompromiseCalibrate = false;

      ap.runLuminanceBasedReport = true;
      return ap;
    }

    public MeasureParameter getMeasureParameter() {
      MeasureParameter mp = new MeasureParameter();
      mp.whiteSequenceMeasure = true;
      mp.sequenceMeasureCount = 24;
      return mp;
    }

  }

  private static class FullParameterProducer
      extends BasicParameterProducer {

    public AdjustParameter getAdjustParameter() {
      AdjustParameter ap = new AdjustParameter();
      ap.smoothGreenCalibrate = false;
      ap.smoothGreenCompromiseCalibrate = false;
      return ap;
    }

    public double[] getGammaArray() {
      double[] gammaArray = new double[] {
          1.8, 1.9, 2.0, 2.1, 2.2, 2.3, 2.4};
      return gammaArray;
    }

    public WhiteParameter[] getWhiteParameterArray() {
      WhiteParameter[] wpArray = new WhiteParameter[] {
          new WhiteParameter(5000), new WhiteParameter(6500),
          new WhiteParameter(7000), new WhiteParameter(8000),
          new WhiteParameter(9300)};
      for (WhiteParameter wp : wpArray) {
        wp.maxWhiteCode = 255;
      }
      return wpArray;
    }
  }

  private static class CMOParameterProducer
      extends BasicParameterProducer {
    public ColorProofParameter getInitCPParameter() {
      ColorProofParameter p = new ColorProofParameter();
      p.gamma = ColorProofParameter.Gamma.Custom;
      p.customGamma = 2.2;
      p.gammaBy = ColorProofParameter.GammaBy.W;
      p.calibrateBits = RGBBase.MaxValue.Int9Bit;
      p.icBits = RGBBase.MaxValue.Int10Bit;
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
      ap.whiteBasedCalibrate = true;
      ap.smoothGreenCalibrate = false;
      ap.smoothGreenBasedOn = AdjustParameter.GreenBased.Model;
      ap.smoothGreenCompromiseCalibrate = false;
      ap.greenBasedCalibrate = true;
      ap.whiteCalibratedInterval = 1;
      ap.greenCalibratedInterval = 1;
      return ap;
    }

    public ColorProofParameter.CCTCalibrate[] getCCTCalibrateArray() {
      ColorProofParameter.CCTCalibrate[] cctCalArray = new ColorProofParameter.
          CCTCalibrate[] {
          ColorProofParameter.CCTCalibrate.uvpByDE00,
      };
      return cctCalArray;
    }

    public MeasureParameter getMeasureParameter() {
      MeasureParameter mp = new MeasureParameter();
      mp.inverseMeasure = false;
      return mp;
    }

  }

  public static void main(String[] args) {
//    TestTaskProducer producer = new TestTaskProducer(new BasicParameterProducer());
//    ApongTestParameterProducer tpp = new ApongTestParameterProducer();
//    TestParameterProducer12Bit tpp = new TestParameterProducer12Bit();
//    TestParameterProducer tpp = new TestParameterProducer();
    AUOParameterProducer tpp = new AUOParameterProducer();
//    CMOParameterProducer tpp = new CMOParameterProducer();
//    BasicParameterProducer tpp = new BasicParameterProducer();

    TestTaskProducer producer = new TestTaskProducer(tpp);

    CalibrateTester tester = new CalibrateTester(new InfoAdapter(tpp) {
      public Meter getMeter() {
        return Material.getMeter();
      }

      public LCDTarget getOriginalRamp1021Target() {
        return Material.getRamp1021Target();
      }

      public boolean isLCDModelCalibrate() {
        return true;
      }

      public boolean isMeasuredCalibrate() {
        return true;
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
  }

  private static class AUOParameterProducer
      extends BasicParameterProducer {
    public ColorProofParameter getInitCPParameter() {
      ColorProofParameter p = new ColorProofParameter();
      p.gamma = ColorProofParameter.Gamma.Custom;
      p.customGamma = 2.2;
      p.gammaBy = ColorProofParameter.GammaBy.W;
      p.calibrateBits = RGBBase.MaxValue.Int10Bit;
      p.icBits = RGBBase.MaxValue.Int10Bit;
      p.runCount = 1;
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

      ap.luminanceBasedCalibrate = false;
      ap.luminanceCalibratedInterval = 2;
      ap.whiteBasedCalibrate = false;
      ap.whiteCalibratedInterval = 2;
      ap.greenBasedCalibrate = false;
      ap.greenCalibratedInterval = 1;
      ap.luminanceBased2Calibrate = false;
      ap.luminanceCalibrated2Interval = 2;

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
}
