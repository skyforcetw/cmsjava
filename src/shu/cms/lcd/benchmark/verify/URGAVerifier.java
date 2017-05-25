package shu.cms.lcd.benchmark.verify;

import java.io.*;
import java.text.*;
import java.util.*;

import java.awt.color.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.math.array.*;

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
public class URGAVerifier
    extends Verifier {
  protected LCDTarget urgaTarget;
  public URGAVerifier(LCDTarget rampTarget, LCDTarget urgaTarget) {
    super(rampTarget);
    if (urgaTarget.getNumber() != LCDTargetBase.Number.URGA_ProfileQuality) {
      throw new IllegalArgumentException(
          "urgaTarget.getNumber() != LCDTargetBase.Number.URGA_ProfileQuality");
    }
    this.urgaTarget = urgaTarget;

  }

  protected boolean checkLCDTarget(LCDTarget lcdTarget) {
    return lcdTarget.getNumber() == LCDTargetBase.Number.Ramp256W;
  }

  public final VerifierReport blackPointVerify() {

    Patch blackPatch = lcdTarget.getBlackPatch();
    CIEXYZ black = blackPatch.getXYZ();

    double luminance = black.Y;
    if (black.getNormalizeY() == NormalizeY.Normal1) {
      luminance *= lcdTarget.getLuminance().Y;
    }
//    System.out.println("Luminance:\t" + luminance + " Cd/m2");
    StringBuilder buf = new StringBuilder("Luminance:\t" + luminance +
                                          " Cd/m2\n");

    CIELCh blackLCh = new CIELCh(blackPatch.getLab());
//    System.out.println("Chromaticity::\t" + blackLCh.C + " Chroma (Lab)");
    buf.append("Chromaticity::\t" + blackLCh.C + " Chroma (Lab)");
    return new VerifierReport(buf.toString());
  }

  public final VerifierReport whitePointVerify(
      CIEXYZ targetWhitePoint) {

    CIEXYZ white = lcdTarget.getWhitePatch().getXYZ();
    StringBuilder buf = new StringBuilder("XYZ:\t\t\t" + lcdTarget.getLuminance());
    buf.append("XYZ (normalized):\t" + white);
    buf.append("Luminance::\t\t" + lcdTarget.getLuminance().Y +
               " Cd/m2");
    buf.append("Next Temperature:\t" + white.getCCT() + " Kelvin");
    buf.append("Assumed Target Whitepoint:\t" + targetWhitePoint.getCCT() +
               " Kelvin");

    DeltaE dE = new DeltaE(white, targetWhitePoint, targetWhitePoint);
    double ciedE = dE.getCIEDeltaE();
    buf.append("Distance to assumed Target Whitepoint:\t" + ciedE +
               " deltaE");
    boolean result = ciedE <= 1;
    return new VerifierReport(result, buf.toString());
  }

  public final VerifierReport profileQualityVerify(
      ICC_ProfileRGB iccProfile) {

    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(iccProfile,"");
    ProfileColorSpaceModel model = new ProfileColorSpaceModel(pcs);
//    ICCProfileLCDModel model = new ICCProfileLCDModel(iccProfile);
    model.produceFactor();
    DeltaEReport.setOnlyCountMeasuredDeltaE(false);
    DeltaEReport report = model.testForwardModel(urgaTarget, false)[0];
    DeltaEReport.PatchDeltaEReport patchReport = report.getPatchDeltaEReport();
    double[] rgbValues = new double[3];
    int size = patchReport.size();
    StringBuilder buf = new StringBuilder();

    for (int x = 0; x < size; x++) {
      Patch p = patchReport.getPatch(x);
      DeltaE dE = patchReport.getDeltaE(x);
      p.getRGB().getValues(rgbValues, RGB.MaxValue.Int8Bit);
      buf.append(DoubleArray.toString(df, rgbValues) + " " + p.getLab() +
                 " " + dE.getCIEDeltaE());
    }

    double ave = report.meanDeltaE.getCIEDeltaE();
    double max = report.maxDeltaE.getCIEDeltaE();
    buf.append("Average:\t" + ave);
    buf.append("Maximum:\t" + max);
    boolean result = ave <= 3 && max <= 6;
    return new VerifierReport(result, buf.toString());
  }

  public final VerifierReport grayBalanceVerify() {

    CIEXYZ black = lcdTarget.getBlackPatch().getXYZ();
    CIEXYZ white = lcdTarget.getWhitePatch().getXYZ();
    double luminance = white.Y;
    int size = lcdTarget.size();
    double[] cctArray = new double[size];
    double[] chromaArray = new double[size];
    double gammaTotal = 0;
    int gammaCount = 0;
    List<Patch> patchList = lcdTarget.getPatchList();
    StringBuilder buf = new StringBuilder();

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);

      CIEXYZ XYZ = p.getXYZ();
      CIELab Lab = p.getLab();
      double w = p.getRGB().getValue(RGBBase.Channel.W, RGB.MaxValue.Double1);
      double percent = w * 100;
      double cct = XYZ.getCCT();
      cctArray[x] = cct;
      double L = p.getLab().L;
      CIELCh LCh = new CIELCh(Lab);
      double chroma = LCh.C;
      chromaArray[x] = chroma;
      double[] input = new double[] {
          0, w, 1};
      double[] output = new double[] {
          black.Y / luminance, XYZ.Y / luminance, 1};
      double gamma = GammaFinder.findingGamma(input, output);
      if (Double.isNaN(gamma)) {

        buf.append(df.format(percent) + "\t" +
                   cctdf.format(cct) + "\t" +
                   df.format(XYZ.Y) +
                   "\t" + df.format(L) +
                   "\t" + df.format(chroma));
      }
      else {
        buf.append(df.format(percent) + "\t" +
                   cctdf.format(cct) + "\t" +
                   df.format(XYZ.Y) +
                   "\t" + df.format(L) +
                   "\t" + df.format(chroma) +
                   "\t" + df.format(gamma));

        gammaTotal += gamma;
        gammaCount++;
      }

    }

    buf.append(df.format(Maths.mean(cctArray)) + "\t\t\t\t" +
               df.format(Maths.mean(chromaArray)) +
               "\t" + df.format(gammaTotal / gammaCount));

    return new VerifierReport(buf.toString());
  }

  protected final static DecimalFormat df = new DecimalFormat("###.##");
  protected final static DecimalFormat cctdf = new DecimalFormat("####");

  public static void main(String[] args) throws IOException {
    LCDTarget ramp256 = LCDTarget.Instance.getFromLogo(
        "256.logo");
    LCDTarget whql = LCDTarget.Instance.getFromLogo(
        "whql.logo");

    URGAVerifier verifier = new URGAVerifier(ramp256, whql);
    verifier.whitePointVerify(Illuminant.D65WhitePoint);
    System.out.println("");
    verifier.blackPointVerify();
    System.out.println("");
    verifier.grayBalanceVerify();
    System.out.println("");

    ICC_ProfileRGB profile = (ICC_ProfileRGB) ICC_ProfileRGB.getInstance(
        "C:/WINDOWS/system32/spool/drivers/color/2407-Spyder.icm");
    verifier.profileQualityVerify(profile);
  }

}
