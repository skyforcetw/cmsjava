package shu.cms.lcd.test;

import java.util.*;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.devicemodel.lcd.xtalk.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.math.array.*;

//import shu.plot.*;

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
public class LCDCharacterPloter {
  public static void main(String[] args) {

//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("auo_B140XW01",
//        LCDTarget.Number.Ramp257_6Bit, "091125");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("auo_B156HW01",
//        LCDTarget.Number.Ramp1021, "091116");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("auo_M240HW01",
//        LCDTarget.Number.Ramp1021, "091217");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("auo_T370HW02",
//        LCDTarget.Number.Ramp1021, "091218");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("cpt_17inch No.3",
//        LCDTarget.Number.Ramp1021, "080506");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("cpt_32inch No.2",
//        LCDTarget.Number.Complex1021_4096_4333, "080616");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("cpt_320WA01C",
//        LCDTarget.Number.Ramp1021, "090717");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("cpt_320WF01C",
//        LCDTarget.Number.Ramp1021, "091008");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("cpt_320WF01SC",
//        LCDTarget.Number.Ramp1021, "0805");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("cpt_320WF0141",
//        LCDTarget.Number.Complex1021_4096_4108, "1024","ch1_1024");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("cpt_370WF02",
//        LCDTarget.Number.Complex1021_4108, "0121");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("cpt_370WF02C",
//        LCDTarget.Number.Ramp1021, "091007");
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("hannstar_100IFW1",
//        LCDTarget.Number.Complex1021_4096_4333, "080703");
//    DummyLCDModel model = new DummyLCDModel(target);
//    CIEXYZ flare = model.flare.getEstimatedFlare();
    //        LCDTarget.Number.Ramp1021);
    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS("Measurement Files\\Monitor\\auo_T370HW02\\ca210\\darkroom\\native\\110922\\Measurement00_.xls",
        LCDTarget.Number.Ramp1024);
    LCDTarget.Operator.gradationReverseFix(target);
    if (false) {

      LCDTarget.Operator.gradationReverseFix(target);
      int[] wDiscreteIndex = LCDTarget.Discrete.getDiscreteIndex(target,
          RGB.Channel.W, 0.0005, 2);
      //    System.out.println(Arrays.toString(wDiscreteInde));
      //    LCDTarget.Discrete.fixDiscreteChromaticity(target, RGB.Channel.W,
      //                                               wDiscreteIndex);
      LCDTarget.Discrete.fixDiscreteChromaticity(target, RGB.Channel.W,
                                                 LCDTarget.Discrete.Method.
                                                 PolarByBlack, wDiscreteIndex);

      int[] rDiscreteIndex = LCDTarget.Discrete.getDiscreteIndex(
          target, RGB.Channel.R, 0.0005, 3);
      int[] gDiscreteIndex = LCDTarget.Discrete.getDiscreteIndex(
          target, RGB.Channel.G, 0.0005, 3);
      int[] bDiscreteIndex = LCDTarget.Discrete.getDiscreteIndex(
          target, RGB.Channel.B, 0.0005, 3);
      int[][] discreteIndex = new int[][] {
          rDiscreteIndex, gDiscreteIndex, bDiscreteIndex};
      System.out.println(Arrays.toString(rDiscreteIndex));
      System.out.println(Arrays.toString(gDiscreteIndex));
      System.out.println(Arrays.toString(bDiscreteIndex));
      //    for (int x = 0; x < 3; x++) {
      for (int x = 0; x < 1; x++) {
        for (RGB.Channel ch : RGB.Channel.RGBChannel) {
          //      for (RGB.Channel ch : new RGB.Channel[] {RGB.Channel.R}) {
          //        LCDTarget.Discrete.fixDiscreteChromaticity(target, ch,
          //            discreteIndex[ch.getArrayIndex()][x]);
          LCDTarget.Discrete.fixDiscreteChromaticity(target, ch,
              LCDTarget.Discrete.Method.
              Shift,
              discreteIndex[ch.getArrayIndex()][
              x]);
        }
      }

      //    LCDTarget.Discrete.fixDiscreteChromaticity(target, RGB.Channel.R,
      //                                               rDiscreteIndex[0]);
      //    LCDTarget.Discrete.fixDiscreteChromaticity(target, RGB.Channel.G,
      //                                               gDiscreteIndex[0]);
      //    LCDTarget.Discrete.fixDiscreteChromaticity(target, RGB.Channel.B,
      //                                               bDiscreteIndex[0]);
    }

    plotGrayScale(target, true);
//    matrix(target);
    shift(target, false, false);
//    xtalk(target, false);
  }

  public static void xtalk(LCDTarget target, boolean withoutFlare) {
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("auo_T370HW02",
//        LCDTarget.Number.Ramp1021, "091218");
    MultiMatrixModel model = new MultiMatrixModel(target);
    model.produceFactor();
    model.setAutoRGBChangeMaxValue(true);
    CIEXYZ whiteXYZ = model.getWhiteXYZ(false);
//    CIEXYZ flare = model.flare.getEstimatedFlare();
    Plot2D plot = Plot2D.getInstance();
    Plot2D plot2 = Plot2D.getInstance();

    for (int x = 50; x < 256; x++) {
      Color c = new Color(x, x, x);
      CIEXYZ XYZ = model.getNeutralXYZ(x, withoutFlare);
      CIEXYZ XYZ2 = model.getXYZ(new RGB(x, x, x), withoutFlare);
      CIExyY xyY = new CIExyY(XYZ);
      CIExyY xyY2 = new CIExyY(XYZ2);
      double[] duvp = xyY.getDeltauvPrime(xyY2);
      double[] dxy = xyY.getDeltaxy(xyY2);
      String name = Integer.toString(x);
      plot.addScatterPlot(name, c, Math.abs(duvp[0]), Math.abs(duvp[1]));
      plot2.addScatterPlot(name, c, Math.abs(dxy[0]), Math.abs(dxy[1]));
//      DeltaE de = new
    }
    plot.setAxeLabel(0, "u'");
    plot.setAxeLabel(1, "v'");
    plot2.setAxeLabel(0, "x");
    plot2.setAxeLabel(1, "y");
    plot.setVisible();
    plot2.setVisible();
  }

  public static void shift(LCDTarget target, boolean plotuvPrime,
                           boolean withoutFlare) {

    DummyLCDModel.setFlareType(DummyLCDModel.FlareType.Estimate);
    DummyLCDModel model = new DummyLCDModel(target);
    CIEXYZ flare = model.flare.getFlare();
//    Plot2D xyplot = Plot2D.getInstance(target.getDevice(), 700, 700);

    for (RGB.Channel ch : RGB.Channel.RGBChannel) {
      Plot2D plot = Plot2D.getInstance(target.getDevice() + " " + ch, 700, 700);
      for (int x = 0; x < 256; x++) {
        Patch p = target.getPatch(ch, x, RGB.MaxValue.Int8Bit);
        CIEXYZ XYZ = p.getXYZ();
        if (withoutFlare) {
          XYZ = CIEXYZ.minus(XYZ, flare);
        }
        double[] values = plotuvPrime ? XYZ.getuvPrimeValues() :
            XYZ.getxyValues();
        String name = Double.toString(p.getRGB().getValue(ch));
        plot.addScatterPlot(name, p.getRGB().getColor(),
                            values[0], values[1]);
//        xyplot.addScatterPlot(name, p.getRGB().getColor(),
//                              values[0], values[1]);
      }
      plot.setAxeLabel(0, plotuvPrime ? "u'" : "x");
      plot.setAxeLabel(1, plotuvPrime ? "v'" : "y");
      plot.setVisible();
    }
//    xyplot.setVisible();
  }

  public static void matrix(LCDTarget target) {
//    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("auo_T370HW02",
//        LCDTarget.Number.Ramp1021, "091218");
    Patch r = target.getSaturatedChannelPatch(RGB.Channel.R);
    Patch g = target.getSaturatedChannelPatch(RGB.Channel.G);
    Patch b = target.getSaturatedChannelPatch(RGB.Channel.B);
    Patch w = target.getWhitePatch();
    Patch k = target.getBlackPatch();
    CIEXYZ kXYZ = k.getXYZ();
    CIEXYZ rXYZ = r.getXYZ();
    CIEXYZ gXYZ = g.getXYZ();
    CIEXYZ bXYZ = b.getXYZ();
    CIEXYZ wXYZ = w.getXYZ();
    rXYZ = CIEXYZ.minus(rXYZ, kXYZ);
    gXYZ = CIEXYZ.minus(gXYZ, kXYZ);
    bXYZ = CIEXYZ.minus(bXYZ, kXYZ);
    wXYZ = CIEXYZ.minus(wXYZ, kXYZ);
    double[][] m = {
        rXYZ.getValues(), gXYZ.getValues(), bXYZ.getValues()};
    m = DoubleArray.transpose(m);
    m = DoubleArray.inverse(m);
    double[] result = DoubleArray.times(m, wXYZ.getValues());
    System.out.println(DoubleArray.toString(result));
  }

  public static void plotGrayScale(LCDTarget target, boolean plotuvPrime) {

    if (target.getNumber().isComplex()) {
      target = target.targetFilter.getRamp();
    }
    Plot2D plot = Plot2D.getInstance(target.getDevice(), 700, 700);
    plot.setChartTitle(target.getDevice());
    plot.setLinePlotDrawDot(true);

    LCDTarget xtalktarget = LCDTarget.Instance.getFromCA210Logo("auo_T370HW02",
        LCDTarget.Number.Xtalk769, "091218");
    AdjacentPixelXtalkModel model = new AdjacentPixelXtalkModel(target,
        xtalktarget);
    model.produceFactor();
    model.setAutoRGBChangeMaxValue(true);
    MultiMatrixModel mmodel = new MultiMatrixModel(target);
    mmodel.produceFactor();
    mmodel.setAutoRGBChangeMaxValue(true);
//    LCDModel model = mmodel;

    for (int x = 0; x < 256; x++) {
      CIEXYZ XYZ = mmodel.getNeutralXYZ(x, false);
      CIExyY xyY = new CIExyY(XYZ);
      double[] uvValues = plotuvPrime ? xyY.getuvPrimeValues() :
          xyY.getuvValues();
      RGB rgb = new RGB(x, x, x);
//      plot.addCacheScatterLinePlot("W", Color.green, uvValues[0], uvValues[1]);
      plot.addScatterPlot(Integer.toString(x), Color.green, uvValues[0],
                          uvValues[1]);

      CIEXYZ modelXYZ = model.getXYZ(rgb, false);
      CIExyY modelxyY = new CIExyY(modelXYZ);
      double[] modeluvValues = plotuvPrime ? modelxyY.getuvPrimeValues() :
          modelxyY.getuvValues();
//      plot.addCacheScatterLinePlot("R+G+B", Color.red, modeluvValues[0],
//                                   modeluvValues[1]);
      plot.addScatterPlot(Integer.toString(x), Color.red, modeluvValues[0],
                          modeluvValues[1]);
      double[] dxy = xyY.getDeltaxy(modelxyY);
//      System.out.println(x + "\t" + dxy[0] + "\t" + dxy[1] + "\t" +
//                         Math.sqrt(Maths.sqr(dxy[0]) + Maths.sqr(dxy[1])));
    }

    for (int CCT = 10000; CCT <= 25000; CCT += 100) {
      CIExyY blackbodyxyY = CorrelatedColorTemperature.CCT2BlackbodyxyY(CCT);
      CIExyY DxyY = CorrelatedColorTemperature.CCT2DIlluminantxyY(CCT);
      double[] bbuvValues = plotuvPrime ? blackbodyxyY.getuvPrimeValues() :
          blackbodyxyY.getuvValues();
      double[] DValues = plotuvPrime ? DxyY.getuvPrimeValues() :
          DxyY.getuvValues();
      plot.addCacheScatterLinePlot("BlackBody-locus", Color.black, bbuvValues[0],
                                   bbuvValues[1]);
      plot.addCacheScatterLinePlot("D-locus", Color.cyan, DValues[0], DValues[1]);
    }

    plot.setAxeLabel(0, plotuvPrime ? "u'" : "u");
    plot.setAxeLabel(1, plotuvPrime ? "v'" : "v");

//    plot.addLegend();
    plot.setVisible();
    plot.setLinePlotWidth(2);
//    double[] xbounds = plot.getFixedBounds(0);
//    double[] ybounds = plot.getFixedBounds(1);
//    plot.setFixedBoundsByYAxis(xbounds[0], ybounds[0], ybounds[1]);
    plot.setFixedBoundsByYAxis();
  }
}
