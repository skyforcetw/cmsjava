package shu.cms.lcd.benchmark.verify;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
///import shu.plot.*;

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
public class ColorTemperatureVerifier
    extends Verifier {
  /**
   * ColorTemperatureVerifier
   *
   * @param lcdTarget LCDTarget
   */
  public ColorTemperatureVerifier(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  public VerifierReport cctVerify() {
    double[] cct = new double[256];
    for (int x = 0; x < 256; x++) {
      Patch p = lcdTarget.getPatch(RGBBase.Channel.W, x, RGB.MaxValue.Int8Bit);
      CIEXYZ XYZ = p.getXYZ();
      cct[x] = XYZ.getCCT();
    }
    return new VerifierReport(cct);
  }

  public VerifierReport deltauvVerify() {
    double[] deltauv = new double[256];
    for (int x = 0; x < 256; x++) {
      Patch p = lcdTarget.getPatch(RGBBase.Channel.W, x, RGB.MaxValue.Int8Bit);
      CIEXYZ XYZ = p.getXYZ();
      deltauv[x] = CorrelatedColorTemperature.getduvWithBlackbody(XYZ);
    }
    return new VerifierReport(deltauv);
  }

  /**
   * checkLCDTarget
   *
   * @param lcdTarget LCDTarget
   * @return boolean
   */
  protected boolean checkLCDTarget(LCDTarget lcdTarget) {
    LCDTarget.Number number = lcdTarget.getNumber();
    return number.isRamp() && number.getStep() == 1;
  }

  public static void main(String[] args) {
//    LCDTarget ref = LCDTarget.Instance.getFromLogo(CMSDir.Measure.Monitor +
//        "/cpt_17inch 3/ca210/darkroom/native/cal/2.0.logo");
//    LCDTarget sam = LCDTarget.Instance.getFromLogo(CMSDir.Measure.Monitor +
//        "/cpt_17inch 3/huey/darkroom/native/cal/2.0.logo");

    LCDTarget target = LCDTarget.Instance.get("cpt_32inch No.2",
                                              LCDTarget.Source.CA210,
                                              LCDTarget.Room.Dark,
                                              LCDTarget.TargetIlluminant.Native,
                                              LCDTargetBase.Number.Ramp1021,
                                              LCDTarget.FileType.Logo,
                                              null, null);
//    LCDTarget.Calibrate.calibrate(ref, sam, target);

    ColorTemperatureVerifier verifier = new ColorTemperatureVerifier(target);
    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);
    Plot2D plotcct = Plot2D.getInstance();
    plotcct.setVisible(true);
    double[] cct = (double[]) verifier.cctVerify().result;
    plotcct.addLinePlot("cct", 0, 255, cct);
    double[] duv = (double[]) verifier.deltauvVerify().result;
    plot.addLinePlot("duv", 0, 255, duv);

//    System.out.println(CorrelatedColorTemperature.getduvWithBlackbody(
//        Illuminant.D55.getNormalizeXYZ()));
  }
}
