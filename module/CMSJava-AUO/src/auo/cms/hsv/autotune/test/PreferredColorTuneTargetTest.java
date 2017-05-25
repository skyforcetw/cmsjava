package auo.cms.hsv.autotune.test;

import java.awt.*;

import auo.cms.hsv.autotune.*;
import auo.cms.hsv.saturation.*;
import auo.cms.prefercolor.*;
import auo.cms.prefercolor.model.*;
import auo.cms.prefercolor.model.test.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.cms.profile.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class PreferredColorTuneTargetTest {
  public static void main(String[] args) {
//    skinTest(args);
//    test(args);
    autoTuneTest(args);
//    targetCheck(args);
//    deltaETest(args);
//    whqlDeltaETest(args);
//    preferredTest(args);
  }

  public static LCDTarget RampTarget = null;
  public static LCDTarget PreferredTarget = null;

  public final static TuneTarget getPreferredTuneTarget() {
    RampTarget = LCDTarget.Instance.getFromAUORampXLS(
        "prefered/B156HW03/Dell WRGB.xls", LCDTarget.Number.Ramp256_6Bit);
    RampTarget.changeMaxValue(RGB.MaxValue.Double255);
    ProfileColorSpace limitpcs = ProfileColorSpaceUtils.
        getProfileColorSpaceFromRampTarget(RampTarget);

//    ProfileColorSpace colorSpacePCS = ProfileColorSpace.Instance.get(RGB.
//        ColorSpace.sRGB);
    PreferredColorModel preferredColorModel = PreferredColorModel.
        getInstance(limitpcs);
    ProfileColorSpace pcs = preferredColorModel.produceHSVPreferredProfileColorSpaceInsRGBSignal();

//    RampTarget = LCDTarget.Instance.getFromAUORampXLS(
//        "prefered/B156HW03/Dell WRGB.xls", LCDTarget.Number.Ramp256_6Bit);
//    RampTarget.changeMaxValue(RGB.MaxValue.Double255);
//    ProfileColorSpace limitpcs = ProfileColorSpaceUtils.
//        getProfileColorSpaceFromRampTarget(RampTarget);

    PreferredColorTuneTarget target = new PreferredColorTuneTarget(pcs,
        limitpcs);
    target.setPreferredColorModel(preferredColorModel);
    return target;
  }

  public final static TuneTarget getTestTuneTarget(boolean useSingleSpot) {
    PreferredTarget = LCDTarget.Instance.getTest729FromAUOXLS(
        "prefered/SONY KDL-40HX800/cinema_871.xls");
//        "prefered/SONY KDL-40HX800/sport_871.xls");
//        "prefered/Sony 70x7000/729color_standard.xls");
//        "prefered/B156HW03/Dell 729.xls");
    RampTarget = LCDTarget.Instance.getFromAUORampXLS(
        "prefered/B125XW02/WRGB.xls", LCDTarget.Number.Ramp1024);
//        "prefered/B156HW03/Dell WRGB.xls", LCDTarget.Number.Ramp256_6Bit);
    LCDTarget.Operator.gradationReverseFix(RampTarget);
    RampTarget.changeMaxValue(RGB.MaxValue.Double255);

    LCDTarget.Operator.gradationReverseFix(RampTarget);
//    boolean useSingleSpot = true;

    TuneTarget tuneTarget = null;
    if (useSingleSpot) {
      tuneTarget = sRGBTuneTarget.getInstance(sRGBTuneTarget.Patches.Integrated,
                                              PreferredTarget);
    }
    else {
      PreferredColorTuneTarget preferredColorTuneTarget =
          PreferredColorTuneTarget.getInstance(
              PreferredTarget, RampTarget, 100,
              PreferredColorTuneTarget.Type.HSV2LCHGrid);
      tuneTarget = preferredColorTuneTarget;
    }
    return tuneTarget;
  }

  public static void autoTuneTest(String[] args) {
    boolean useSingleSpot = false;
    boolean skinFirst = true;

    TuneTarget tuneTarget = getPreferredTuneTarget();

    MultiMatrixModel model = new MultiMatrixModel(RampTarget);
    model.produceFactor();
    IntegerSaturationFormula integerSaturationFormula = new
        IntegerSaturationFormula( (byte) 7, 4);
    ColorIndex colorIndex = new ColorIndex(model);
    colorIndex.setKlch(1, 1, 1);
    AutoTuner autotuner = new AutoTuner(model, integerSaturationFormula,
                                        colorIndex);
    if (useSingleSpot) {
      autotuner.setFitMode(AutoTuner.FitMode.SingleSpot);
    }
    else {
      autotuner.setFitMode(AutoTuner.FitMode.MultiSpot);
    }

    TuneParameter tuneParameter = null;
    if (tuneTarget instanceof PreferredColorTuneTarget) {
      PreferredColorTuneTarget ptunrTarget = (PreferredColorTuneTarget)
          tuneTarget;

      ptunrTarget.setSkinFirstInHSVGrid(skinFirst);

      System.out.println(PreferredColorTuneTarget.Type.HSVGrid);
      ptunrTarget.setType(PreferredColorTuneTarget.Type.HSVGrid);
      tuneParameter = autotuner.getTuneParameter(ptunrTarget);

      System.out.println(tuneParameter.toToolkitFormatString());
      PreferredColorModel preferredColorModel = ptunrTarget.
          getPreferredColorModel();
      double[] hueOfHSVArray = preferredColorModel.getHueOfHSVArray();
      tuneParameter.interpolate(hueOfHSVArray);
      System.out.println(tuneParameter.toToolkitFormatString());
    }
    else {
      tuneParameter = autotuner.getTuneParameter(tuneTarget);
      System.out.println(tuneParameter.toToolkitFormatString());
    }

  }

  public static void skinTest(String[] args) {
    CIELab Lab = MemoryColorPatches.Korean.getInstance().getSkin();
    RGB rgb = new RGB(RGB.ColorSpace.SMPTE_C, Lab.toXYZ());
    System.out.println(AutotuneUtils.isSkin1(rgb));
    System.out.println(AutotuneUtils.isSkin1(RGB.White));
  }

  public static void whqlDeltaETest(String[] args) {
//    System.out.println(System.getProperty("user.dir"));
    LCDTarget nbTarget = LCDTarget.Instance.getFromAUOXLS(
//    LCDTarget nbTarget = LCDTarget.Instance.getTest729FromAUOXLS(
        "prefered/B125XW02/NB_871.xls"); //original
//        "prefered/B125XW02/cinema/single_871.xls");
//      "prefered/B125XW02/cinema/hsv_871.xls");
//        "prefered/B125XW02/cinema/hsv2lch_871.xls");
//    int start = 729 + 7;
//    int end = 729 + 14;
    int start = 729 + 7 + 7;
    int end = 729 + +7 + 7 + 32;
    LCDTarget nbWHQLTarget = LCDTarget.Instance.getByPart(nbTarget, start,
        end, null);

    LCDTarget tvTarget = LCDTarget.Instance.getFromAUOXLS(
        "prefered/SONY KDL-40HX800/cinema_871.xls");
    LCDTarget tvWHQLlTarget = LCDTarget.Instance.getByPart(tvTarget, start,
        end, null);
    LCDTarget tvWideTarget = LCDTarget.Instance.getFromAUOXLS(
        "prefered/SONY KDL-40HX800/sport_871.xls");

//    WHQLVerifier verifier = new WHQLVerifier(nbWHQLTarget);
//    System.out.println(verifier.integratedLCDVerify());
//    System.out.println(verifier.getIntegratedLCDReport());
//
//    verifier = new WHQLVerifier(tvWHQLlTarget);
//    System.out.println(verifier.integratedLCDVerify());
//    System.out.println(verifier.getIntegratedLCDReport());
    DeltaEReport[] report = DeltaEReport.Instance.patchReport(nbWHQLTarget.
        getLabPatchList(), tvWHQLlTarget.getLabPatchList(), false);
    System.out.println(report[0]);

    LocusPlot locusPlot = new LocusPlot();
    locusPlot.drawCIExyLocus(false);
    locusPlot.drawGamutTriangle("AUO", nbTarget, java.awt.Color.red,
                                LocusPlot.xyTrasnfer);
    locusPlot.drawGamutTriangle("SOXY", tvTarget, java.awt.Color.green,
                                LocusPlot.xyTrasnfer);
//    locusPlot.drawGamutTriangle("sRGB", RGB.ColorSpace.sRGB,
//                                java.awt.Color.black, LocusPlot.xyTrasnfer);

    Plot2D plot = locusPlot.getPlot2D();
    CIExyY cxyY = new CIExyY(nbTarget.getPatch(RGB.Channel.C, 255).getXYZ());
    CIExyY mxyY = new CIExyY(nbTarget.getPatch(RGB.Channel.M, 255).getXYZ());
    CIExyY yxyY = new CIExyY(nbTarget.getPatch(RGB.Channel.Y, 255).getXYZ());
    CIExyY wxyY = new CIExyY(nbTarget.getPatch(RGB.Channel.W, 255).getXYZ());
    plot.addScatterPlot("", Color.cyan, cxyY.x, cxyY.y);
    plot.addScatterPlot("", Color.magenta, mxyY.x, mxyY.y);
    plot.addScatterPlot("", Color.yellow, yxyY.x, yxyY.y);
    plot.addScatterPlot("", Color.black, wxyY.x, wxyY.y);

    cxyY = new CIExyY(tvTarget.getPatch(RGB.Channel.C, 255).getXYZ());
    mxyY = new CIExyY(tvTarget.getPatch(RGB.Channel.M, 255).getXYZ());
    yxyY = new CIExyY(tvTarget.getPatch(RGB.Channel.Y, 255).getXYZ());
    wxyY = new CIExyY(tvTarget.getPatch(RGB.Channel.W, 255).getXYZ());
    plot.addScatterPlot("", Color.cyan, cxyY.x, cxyY.y);
    plot.addScatterPlot("", Color.magenta, mxyY.x, mxyY.y);
    plot.addScatterPlot("", Color.yellow, yxyY.x, yxyY.y);
    plot.addScatterPlot("", Color.black, wxyY.x, wxyY.y);
//    locusPlot.drawGamutTriangle("SOXY-WCG", tvWideTarget, java.awt.Color.pink,
//                                LocusPlot.xyTrasnfer);
//    locusPlot.dra
    locusPlot.setVisible();
    locusPlot.getPlot2D().addLegend();
  }

  public static void deltaETest(String[] args) {
    LCDTarget nbTarget = LCDTarget.Instance.getFromAUORampXLS(
//    LCDTarget nbTarget = LCDTarget.Instance.getTest729FromAUOXLS(
//      "prefered/B125XW02/NB_871.xls");
//        "prefered/B125XW02/cinema/single_871.xls");
//      "prefered/B125XW02/cinema/hsv_871.xls");
        "prefered/B125XW02/cinema/hsv2lch_871.xls",
        LCDTarget.Number.Complex729_46_96);

//    LCDTarget.Instance.getByPart()

    LCDTarget tvTarget = LCDTarget.Instance.getTest729FromAUOXLS(
        "prefered/SONY KDL-40HX800/cinema_871.xls");

    DeltaEReport[] report = DeltaEReport.Instance.patchReport(nbTarget.
        getLabPatchList(), tvTarget.getLabPatchList(), false);
    System.out.println(report[0]);
  }

  public static void targetCheck(String[] args) {
    LCDTarget preferredTarget = LCDTarget.Instance.getTest729FromAUOXLS(
//        "prefered/SONY KDL-40HX800/cinema_871.xls");
//        "prefered/SONY KDL-40HX800/sport_871.xls");
        "prefered/Sony 70x7000/729color_standard.xls");
    Plot3D plot = Plot3D.getInstance();
//    Plot3D plot2 = Plot3D.getInstance();
    for (Patch p : preferredTarget.getPatchList()) {
      java.awt.Color c = p.getRGB().getColor();
      CIELab Lab = p.getLab();
      CIELCh LCh = new CIELCh(Lab);
      plot.addScatterPlot("", c, LCh.h, LCh.L, LCh.C);
//      plot2.addScatterPlot("", c, Lab.a, Lab.b, Lab.L);

    }
    plot.setVisible();
//    plot2.setVisible();
  }

}
