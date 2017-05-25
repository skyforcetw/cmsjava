package shu.cms.lcd.material;

import java.io.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.devicemodel.lcd.xtalk.*;
import shu.cms.lcd.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.cms.measure.cp.*;
import shu.cms.measure.meter.*;
import shu.cms.profile.*;
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
public class Material {

  /**
   *
   * @return ViewingParameter
   * @deprecated
   */
  public final static ViewingParameter getViewingParameter() {
    ViewingParameter vp = new ViewingParameter();
    return vp;
  }

  /**
   *
   * @return AdjustParameter
   * @deprecated
   */
  public final static AdjustParameter getAdjustParameter() {
    AdjustParameter ap = new AdjustParameter();
//    ap.smoothGreenCalibrated = false;
//    ap.smoothGreenCompromiseCalibrate = false;
    return ap;
  }

  /**
   *
   * @return ColorProofParameter
   * @deprecated
   */
  public final static ColorProofParameter getCPParameter() {
    ColorProofParameter p = new ColorProofParameter();
    p.gamma = ColorProofParameter.Gamma.Custom;
    p.customGamma = 2.2;
    p.cctCalibrate = ColorProofParameter.CCTCalibrate.uvpByDE00;
    p.gammaBy = ColorProofParameter.GammaBy.W;
    p.calibrateBits = RGBBase.MaxValue.Int10Bit;
    p.icBits = RGBBase.MaxValue.Int10Bit;
//    p.inverseMeasure = true;
    return p;
  }

  /**
   *
   * @return MeasureParameter
   * @deprecated
   */
  public final static MeasureParameter getMeasureParameter() {
    MeasureParameter mp = new MeasureParameter();
//    mp.measureBlankInsert = true;
    return mp;
  }

  public final static LCDModel getsRGBLCDModel() {
    CIExyY flare = CorrelatedColorTemperature.CCT2DIlluminantxyY(18000);
    flare.Y = 0.31;
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGBBase.ColorSpace.
        sRGB);
    ProfileColorSpaceModel model = new ProfileColorSpaceModel(pcs, 500,
        flare.toXYZ());
    model.produceFactor();
    model.setAutoRGBChangeMaxValue(true);

    LCDModelAdapter adapter = new LCDModelAdapter(model,
                                                  LCDTargetBase.Number.
                                                  Complex1021_4108);
    LCDTarget target = LCDTarget.Instance.get(adapter);
    model.setLCDTarget(target);
    model.setGammaCorrectLCDTarget(target);

    return model;
  }

  public static enum ModelType {
    sRGB, Store, Measure
  }

  public final static LCDModel getLCDModel() {
    LCDModel model = getLCDModel(AutoCPOptions.get("MeasuredModel") ?
                                 ModelType.Measure : ModelType.Store);
    return model;
  }

  public final static Meter getMeter() {
    if (AutoCPOptions.get("DummyMeter")) {
      CPCodeMeasurement.setMaxWaitTime(0);
      LCDModel model = Material.getLCDModel();
//      LCDModel model = Material.getStoreLCDModel();
      model.produceFactor();
      return new DummyMeter(model);
    }
    else {
      if (AutoCPOptions.get("ShareMemoryMode")) {
        return new ShareMemoryMeter();
      }
      else {
        return RemoteMeter.getDefaultInstance();
      }
    }
  }

  public final static LCDModel getLCDModel(ModelType type) {
    switch (type) {
      case sRGB:
        return getsRGBLCDModel();
      case Store:
        return getStoreLCDModel();
      case Measure:
        return getMeasuredLCDModel(mp, cp, LCDTarget.Source.Remote);
      default:
        return null;
    }
  }

  public final static void setMeasureParameter(MeasureParameter
                                               measureParameter) {
    mp = measureParameter;
  }

  private static MeasureParameter mp;
  private static ColorProofParameter cp;
  public final static void setColorProofParameter(ColorProofParameter
                                                  colorProofParameter) {
    cp = colorProofParameter;
  }

  public final static LCDTarget getRamp1021Target() {
    return rampTarget;
  }

  public final static LCDTarget getXTalkTarget() {
    return xtalkTarget;
  }

  private static LCDTarget rampTarget;
  private static LCDTarget xtalkTarget;

  public final static LCDModel getMeasuredLCDModel(
      MeasureParameter mp, ColorProofParameter cp, LCDTarget.Source source) {

    //==========================================================================
    // 量測設定
    //==========================================================================
    LCDTarget.Measured.setInverseModeMeasure(mp.inverseMeasure);
    LCDTarget.Measured.setMeasureBlankInsert(mp.measureBlankInsert);
    LCDTarget.Measured.setMeasureBlankTime(mp.measureBlankTime);
    LCDTarget.Measured.setMeasureWaitTime(mp.measureWaitTime);
    LCDTarget.Measured.setBlankColor(mp.blankColor);
    LCDTarget.Measured.setBackgroundColor(mp.backgroundColor);
    //==========================================================================
    //by pass
    CPCodeLoader.loadOriginal(cp.icBits);
    //量測ramp target
    rampTarget = LCDTarget.Measured.measure(source, mp.rampLCDTarget, false);
    if (AutoCPOptions.get("FixBlueHook")) {
      rampTarget.hook.fixBlueHook();
    }

    if (!LCDTarget.Operator.checkIncreaseProgressively(
        rampTarget)) {
      //檢查並修正ramp target
      LCDTarget.Operator.gradationReverseFix(rampTarget);
      Logger.log.info("Ramp1021Target do gradation reverse fix.");
    }

    if (AutoCPOptions.get("UseXTalkModel")) {
      //量測xtalk target
      xtalkTarget = LCDTarget.Measured.measure(source,
                                               mp.xtalkLCDTarget, false);
    }
    LCDModel model = getLCDModel(rampTarget, xtalkTarget);
    model.produceFactor();

    return model;
  }

  private final static LCDModel getLCDModel(LCDTarget rampTarget,
                                            LCDTarget xtalkTarget) {
    LCDModel model = null;
    if (AutoCPOptions.get("UseXTalkModel")) {
      model = new AdjacentPixelXtalkModel(rampTarget,
                                          xtalkTarget);
    }
    else {
      if (AutoCPOptions.get("UseCCTv3Model")) {
//        model = new CCTv3Model(rampTarget);
        model = new PLCCModel(rampTarget);
      }
      else {
        model = new MultiMatrixModel(rampTarget);
      }
    }
    return model;
  }

  public final static LCDTarget getStoredLCDTarget() {
    return getLCDTarget(
        AutoCPOptions.getString("LCD_Device"),
        AutoCPOptions.getString("LCD_Dir"),
        AutoCPOptions.getString("LCD_FileTag"),
        LCDTargetBase.Number.valueOf(AutoCPOptions.getString("LCD_Number")));
  }

  public final static LCDTarget getLCDTarget(String device, String dirtag,
                                             String filetag,
                                             LCDTargetBase.Number number) {
    LCDTarget.FileType fileType = LCDTarget.FileType.Logo;
    LCDTarget.Source source = LCDTarget.Source.CA210;

    LCDTarget lcdTarget = LCDTarget.Instance.get(device, source,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.
                                                 Native, number,
                                                 fileType, dirtag, filetag);
//    LCDTarget.Operator.gradationReverseFix(lcdTarget);
    return lcdTarget;
  }

  private static LCDModel lcdModel;
  public final static LCDModel getStoreLCDModel() {
    if (lcdModel == null) {
      //==========================================================================
      // xtalk LCD model
      //==========================================================================
      LCDTarget storedLCDTarget = getStoredLCDTarget();

      rampTarget = storedLCDTarget.targetFilter.get(mp.rampLCDTarget);
      if (AutoCPOptions.get("FixBlueHook")) {
        rampTarget.hook.fixBlueHook();
      }

      if (!LCDTarget.Operator.checkIncreaseProgressively(
          rampTarget)) {
        LCDTarget.Operator.gradationReverseFix(rampTarget);
        try {
          LCDTarget.IO.store(rampTarget, "RampGradationReverseFix.logo");
        }
        catch (IOException ex) {
          Logger.log.error("", ex);
        }
        Logger.log.info("Ramp1021Target do gradation reverse fix.");
      }
      if (AutoCPOptions.get("UseXTalkModel")) {
        xtalkTarget = storedLCDTarget.targetFilter.get(mp.xtalkLCDTarget);
      }
//      if (AutoCPOptions.get("UseXTalkModel")) {
//        xtalkTarget = storedLCDTarget.targetFilter.get(mp.xtalkLCDTarget);
//        lcdModel = new AdjacentPixelXtalkModel(rampTarget, xtalkTarget);
//      }
//      else {
//        lcdModel = new MultiMatrixModel(rampTarget);
//      }
      lcdModel = getLCDModel(rampTarget, xtalkTarget);
      lcdModel.produceFactor();
    }
    return lcdModel;
  }
}
