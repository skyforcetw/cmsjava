package shu.cms.lcd.calibrate.test;

import shu.cms.colorspace.depend.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.cms.lcd.calibrate.tester.*;
import shu.cms.lcd.material.*;

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
public class BasicParameterProducer
    implements ParameterProducer {
  public ViewingParameter getViewingParameter() {
    return new ViewingParameter();
  }

  public AdjustParameter getAdjustParameter() {
    AdjustParameter ap = new AdjustParameter();
    ap.smoothGreenCalibrate = true;
    ap.smoothGreenCompromiseCalibrate = true;
    return ap;
  }

  public WhiteParameter[] getWhiteParameterArray() {
    WhiteParameter[] wpArray = new WhiteParameter[] {
        new WhiteParameter(6500), new WhiteParameter(9300)};
    for (WhiteParameter wp : wpArray) {
      wp.maxWhiteCode = 254;
    }
    return wpArray;
  }

  public MeasureParameter getMeasureParameter() {
    MeasureParameter mp = new MeasureParameter();
    mp.whiteSequenceMeasure = false;
    return mp;
  }

  public ColorProofParameter.CCTCalibrate[] getCCTCalibrateArray() {
    ColorProofParameter.CCTCalibrate[] cctCalArray = new ColorProofParameter.
        CCTCalibrate[] {
        ColorProofParameter.CCTCalibrate.uvpByDE00,
        ColorProofParameter.CCTCalibrate.uvpByIPT,
        ColorProofParameter.CCTCalibrate.IPT, };
    return cctCalArray;
  }

  public int[] getTuneCodeArray() {
    int[] tuneCodeArray = new int[] {
        40, 50};
    return tuneCodeArray;
  }

  public double[] getGammaArray() {
    double[] gammaArray = new double[] {
        1.8, 2.0, 2.2, 2.4};
    return gammaArray;
  }

  public LCDModel getLCDModel() {
    Material.setMeasureParameter(this.getMeasureParameter());
    Material.setColorProofParameter(this.getInitCPParameter());
    return Material.getLCDModel();
  }

  public ColorProofParameter getInitCPParameter() {
    ColorProofParameter p = new ColorProofParameter();
    p.gamma = ColorProofParameter.Gamma.Custom;
    p.customGamma = 2.2;
    p.gammaBy = ColorProofParameter.GammaBy.W;
    p.calibrateBits = RGBBase.MaxValue.Int10Bit;
    p.icBits = RGBBase.MaxValue.Int10Bit;
    return p;
  }

}
