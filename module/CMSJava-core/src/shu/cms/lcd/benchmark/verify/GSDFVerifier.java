package shu.cms.lcd.benchmark.verify;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.math.*;
//import shu.plot.*;

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
public class GSDFVerifier
    extends Verifier {
  public GSDFVerifier(LCDTarget lcdTarget) {
    super(lcdTarget);

  }

  protected boolean checkLCDTarget(LCDTarget lcdTarget) {
    LCDTarget.Number number = lcdTarget.getNumber();
    return number.isRamp() && number.getStep() == 1;
  }

  public static void main(String[] args) {
//    LCDTarget ramp = LCDTarget.Instance.getFromLogo(
//        "Measurement Files/Monitor/cpt_17inch 3/ca210/darkroom/native/1021.logo");
    LCDTarget ramp = LCDTarget.Instance.getFromVastView(
        "Measurement Files/Monitor/cpt_17inch 3/ca210/darkroom/native/1024-Huey.txt");
    GSDFVerifier verifier = new GSDFVerifier(ramp);
    double[] jndIndexDiff = (double[]) verifier.jndIndexDifferVerify(RGB.
        ColorSpace.AdobeRGB, RGBBase.Channel.R).result;
    double[] jndIndexSlope = (double[]) verifier.jndIndexStepSlopeVerify(RGB.
        Channel.R).result;
    double[] jndIndexSlopeDiff = (double[]) verifier.
        jndIndexStepSlopeDifferVerify(RGB.
                                      Channel.R).result;
    System.out.println(jndIndexDiff.length + " " + jndIndexSlope.length + " " +
                       jndIndexSlopeDiff.length);

    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);
    plot.addLegend();
    plot.addLinePlot("diff", 0, 255, jndIndexDiff);
    plot.addLinePlot("slope", .5, 254.5, jndIndexSlope);
    plot.addLinePlot("slopeDiff", 1, 254, jndIndexSlopeDiff);
//    plot.addLinePlot("1", 0, 255, new double[] {1});
//    plot.addLinePlot("-1", 0, 255, new double[] { -1});

    System.out.println(verifier.jndIndexDifferVerify(RGB.ColorSpace.AdobeRGB,
        RGBBase.Channel.R));
  }

  /**
   * 以rgbColorSpace的gamma為基準去驗證channel的平順程度
   * @param rgbColorSpace RGBColorSpace
   * @param channel Channel
   * @return VerifierReport
   */
  public VerifierReport jndIndexDifferVerify(RGB.ColorSpace rgbColorSpace,
                                             RGBBase.Channel channel) {
    CIEXYZ black = (CIEXYZ) lcdTarget.getBlackPatch().getXYZ();
    CIEXYZ white = (CIEXYZ) lcdTarget.getWhitePatch().getXYZ();
    double[] rgbValues = new double[3];
    double[] jndIndexDiffArray = new double[256];

    for (int x = 0; x < 256; x++) {
      Patch p = lcdTarget.getPatch(channel, x, RGB.MaxValue.Int8Bit);
      p.getRGB().getValues(rgbValues, RGB.MaxValue.Double1);
      double[] idealXYZValues = RGB.toXYZValues(rgbValues, rgbColorSpace);
      double idealLuminance = (white.Y - black.Y) * idealXYZValues[1] + black.Y;
      double measureLuminance = p.getXYZ().Y;
      double jndIndexDiff = GSDF.DICOM.getJNDIndex(
          idealLuminance) -
          GSDF.DICOM.getJNDIndex(measureLuminance);
      jndIndexDiffArray[x] = jndIndexDiff;
    }

    return new VerifierReport(jndIndexDiffArray);
  }

  /**
   * 檢驗channel JND變化的斜率的一次微分 (加速度的改變量)
   * @param channel Channel
   * @return VerifierReport
   */
  public VerifierReport jndIndexStepSlopeDifferVerify(RGBBase.Channel channel) {
    double[] result = Maths.firstOrderDerivatives( (double[])
                                                  jndIndexStepSlopeVerify(
        channel).result);
    return new VerifierReport(result);
  }

  /**
   * 檢驗channel JND變化的斜率
   * @param channel Channel
   * @return VerifierReport
   */
  public VerifierReport jndIndexStepSlopeVerify(RGBBase.Channel channel) {
    double[] jndIndexArray = new double[256];
    for (int x = 0; x < 256; x++) {
      Patch p = lcdTarget.getPatch(channel, x, RGB.MaxValue.Int8Bit);
      double luminance = p.getXYZ().Y;
      double index = GSDF.DICOM.getJNDIndex(luminance);
      jndIndexArray[x] = index;
    }

    double[] jndStepDifferArray = Maths.firstOrderDerivatives(jndIndexArray);
    return new VerifierReport(jndStepDifferArray);
  }
}
