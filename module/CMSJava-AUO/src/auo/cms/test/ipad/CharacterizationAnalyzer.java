package auo.cms.test.ipad;

import shu.cms.colorformat.adapter.xls.AUOMeasureXLSAdapter;
import jxl.read.biff.BiffException;
import java.io.IOException;
import shu.cms.lcd.LCDTarget;
import shu.cms.Patch;
import java.util.List;
import shu.cms.colorspace.depend.RGB;
import shu.cms.devicemodel.lcd.MultiMatrixModel;
import java.util.ArrayList;
import shu.cms.colorspace.independ.CIEXYZ;
import shu.cms.colorspace.independ.CIExyY;
import shu.cms.plot.Plot2D;
import shu.cms.colorspace.depend.HSV;
import shu.cms.plot.LocusPlot;
import shu.math.Maths;
import java.awt.Color;
import shu.cms.colorspace.independ.CIELab;
import shu.cms.DeltaE;
import shu.cms.plot.Gamut3DPlot;
import shu.cms.plot.PlotUtils;
import shu.cms.lcd.benchmark.auo.ColorGamutReport;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CharacterizationAnalyzer {

  public static void main(String[] args) throws BiffException, IOException {
    AUOMeasureXLSAdapter adapter = new AUOMeasureXLSAdapter(
        "d:/ณnล้/nobody zone/CCT exp data/2012/120910/iPad.xls");
    LCDTarget target = LCDTarget.Instance.get(adapter);
//    for (Patch p : target.getPatchList()) {
//      System.out.println(p);
//    }


    List<Patch> rPatchList = target.filter.grayScalePatch(RGB.Channel.R, false);
    List<Patch> gPatchList = target.filter.grayScalePatch(RGB.Channel.G, false);
    List<Patch> bPatchList = target.filter.grayScalePatch(RGB.Channel.B, false);
//    List<Patch> wPatchList = target.filter.grayPatch(false);
    List<Patch> wPatchList = target.filter.getRange(1640, 1895);
    List<Patch> newPatchList = new ArrayList<Patch> ();
    newPatchList.add(target.getBlackPatch());
    newPatchList.addAll(rPatchList);
    newPatchList.addAll(gPatchList);
    newPatchList.addAll(bPatchList);
    newPatchList.addAll(wPatchList);
//    newPatchList.add(target.getWhitePatch());

    LCDTarget newtarget = LCDTarget.Instance.get(newPatchList,
                                                 LCDTarget.Number.Ramp1021, false);
    LCDTarget.Operator.gradationReverseFix(newtarget);

    MultiMatrixModel mmmodel = new MultiMatrixModel(newtarget);
    mmmodel.produceFactor();
//    target.filter.
//    List<Patch> grayPatchList = target.filter.grayPatch();
//    for (Patch p : grayPatchList) {
//      RGB rgb = p.getRGB();
//      Patch r = target.getPatch(rgb.R, 0, 0);
//      Patch g = target.getPatch(0, rgb.G, 0);
//      Patch b = target.getPatch(0, 0, rgb.B);
////      System.out.println(p);
//    }
//    checkCrosstalk(target.filter.getRange(1641, 1895), mmmodel);
    checkCrosstalk(target.filter.getRange(0, 871), mmmodel);
    checkGamut(target);

  }

  public static void checkGamut(LCDTarget target) {
    Plot2D srgbplot = Plot2D.getInstance();
    LocusPlot locus = new LocusPlot(srgbplot);
    locus.drawGamutTriangle("sRGB", RGB.ColorSpace.sRGB, Color.red,
                            LocusPlot.xyTrasnfer);
    locus.drawGamutTriangle("NTSC", RGB.ColorSpace.NTSCRGB, Color.green,
                            LocusPlot.xyTrasnfer);
    locus.drawGamutTriangle("iPad", target, Color.blue, LocusPlot.xyTrasnfer);
    locus.drawCIExyLocus(true);
    srgbplot.setVisible();
    srgbplot.setAxeLabel(0, "CIEx");
    srgbplot.setAxeLabel(1, "CIEy");
    srgbplot.setFixedBounds(0, 0, 0.9);
    srgbplot.setFixedBounds(1, 0, 0.9);
    srgbplot.addLegend();
    PlotUtils.setAUOFormat(srgbplot);

    ColorGamutReport report = new ColorGamutReport(target);
    report.setGamutName("Wide");
    System.out.println(report.report());
    Plot2D plot = report.getPlot();
    LocusPlot locusPlot = report.getLocusPlot();

  }

  public static void checkCrosstalk(List<Patch> pattern,
      MultiMatrixModel mmmodel) {

    CIEXYZ whiteXYZ = mmmodel.getWhiteXYZ();

//    Plot2D plot = Plot2D.getInstance();
    Plot2D plot2 = Plot2D.getInstance();
    Plot2D cplot = Plot2D.getInstance();
    Plot2D deplot = Plot2D.getInstance();

    int deindex = 0;
    int x = 0, y = 0;
    for (Patch p : pattern) {
      RGB rgb = p.getRGB();
//      if (!rgb.hasOnlyOneValueChannel()) {
      if (rgb.hasOnlyOneZeroChannel() || true) {
        CIEXYZ XYZ0 = p.getXYZ();
        CIEXYZ XYZ = mmmodel.getXYZ(rgb, false);
        CIExyY xyY0 = new CIExyY(XYZ0);
        CIExyY xyY = new CIExyY(XYZ);
        CIELab Lab0 = new CIELab(XYZ0, whiteXYZ);
        CIELab Lab = new CIELab(XYZ, whiteXYZ);
        DeltaE de = new DeltaE(Lab0, Lab);
        deplot.addCacheScatterLinePlot("", deindex++, de.getCIE2000DeltaE());

        double[] duv = xyY.getDeltauvPrime(xyY0);
        double[] dxy = xyY.getDeltaxy(xyY0);
        double distxy = Math.sqrt(Maths.sqr(dxy[0]) + Maths.sqr(dxy[1]));
        double dist = Math.sqrt(Maths.sqr(duv[0]) + Maths.sqr(duv[1]));
        double v = dist / 0.0024;
        v = Math.pow(v, 1 / 2.2) * 100;
//        v =v*100;
//        System.out.println(rgb + " " + duv[0] + " " + duv[1]);
        System.out.printf("%s %.4f %.4f %.4f %f\n", rgb.toString(), duv[0],
                          duv[1],
                          dist, v);
//        rgb.gets
        HSV hsv = new HSV(rgb);
//doub        hsv.S;
//        plot.addCacheScatterLinePlot("du", x++, Math.abs(duv[0]));
//        plot.addCacheScatterLinePlot("dv", y++, Math.abs(duv[1]));
//        plot2.addCacheScatterPlot("du", hsv.getChroma(), Math.abs(duv[0]));
//        plot2.addCacheScatterPlot("dv", hsv.getChroma(), Math.abs(duv[1]));
        plot2.addCacheScatterPlot("duv", hsv.getChroma(), Math.abs(dist));

        double[] uvpValues0 = xyY0.getuvPrimeValues();
        double[] uvpValues = xyY.getuvPrimeValues();
        if (distxy > 0.001 || true) {

          HSV hsv2 = new HSV(RGB.ColorSpace.unknowRGB, new double[] {120,
                             100, v});
          Color colorindist = hsv2.toRGB().getColor();
          cplot.addCacheScatterPlot(rgb.toString(), colorindist, uvpValues0[0],
                                    uvpValues0[1]);
        }
//        cplot.addVectortoPlot();
      }

//      System.out.println(p.getRGB());
    }
//    plot.setVisible();
//    plot.setAxeLabel(0, "Gray Level");
//    plot.setAxeLabel(1, "du/dv of Crosstalk");
//    plot.setFixedBounds(0, 0, 255);

    plot2.setVisible();
    plot2.setAxeLabel(0, "Saturation");
    plot2.setAxeLabel(1, "du/dv of Crosstalk");

//    LocusPlot locus = new LocusPlot(cplot);
//    locus.drawCIEuvPrimeLocus(true);cplot
    cplot.setVisible();
    cplot.setAxeLabel(0, "CIEu'");
    cplot.setAxeLabel(1, "CIEv'");
//    cplot.setFixedBounds(0, 0, 0.6);
//    cplot.setFixedBounds(1, 0, 0.6);
    cplot.setFixedBounds(0, 0.1, 0.4);
//    cplot.setFixedBounds(1, 0, 0.6);
    cplot.setGridVisible(false);
//    cplot.setAxisVisible(false);

    deplot.setVisible();
    deplot.setAxeLabel(0, "Patch Index");
    deplot.setAxeLabel(1, "DeltaE00");

  }
}
