package shu.cms.measure.verify;

import java.util.*;
import java.util.List;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 用來驗證儀器的準確度及穩定度
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class MeterVerifier {
  protected LCDTarget reference;
  protected LCDTarget[] measurement;
  protected Repeatability repeatability;

  /**
   * 繪出measurement的Y重複率
   * @return Plot2D
   */
  public Plot2D plotYRepeatability() {
    LCDTarget lcdTarget = measurement[0];
    Plot2D plot = Plot2D.getInstance("Y(%) Repeatability (" +
                                     lcdTarget.getDescription() +
                                     ")");
    int size = lcdTarget.size();
    for (int x = 0; x < size; x++) {
      Patch p = lcdTarget.getPatch(x);
      double luminance = p.getXYZ().Y;
      double[] std = repeatability.getxyYSTD(luminance);
      double Y = (std[2] / luminance) * 2. * 100.;
      plot.addCacheScatterLinePlot("Y(%)", Color.blue, luminance, Y);
    }

    plot.drawCachePlot();
    plot.setAxeLabel(0, "Luminance");
    plot.setAxeLabel(1, "deltaY");
    plot.addLegend();

    plot.setVisible(true);
    return plot;
  }

  public double getMeanDeltaEMCDM() {
    return repeatability.getMeanDeltaEMCDM();
  }

  public double getMeanDeltaEAccuracy() {
    CIEXYZ white = reference.getWhitePatch().getXYZ();
    double totalDeltaE = 0;
    LCDTarget lcdTarget = measurement[0];

    int size = reference.size();
    for (int x = 0; x < size; x++) {
      Patch p = reference.getPatch(x);
      double luminance = p.getXYZ().Y;

      CIEXYZ targetXYZ = null;
      if (isRamp256Target) {
        double[] target = repeatability.getxyYMean(luminance);
        targetXYZ = new CIExyY(target).toXYZ();
      }
      else {
        targetXYZ = lcdTarget.getPatch(x).getXYZ();
      }

      DeltaE dE = new DeltaE(p.getXYZ(), targetXYZ, white);
      totalDeltaE += dE.getCIE2000DeltaE();
    }

    return totalDeltaE / size;

  }

  /**
   * 繪出measurement的xy準確性(參考自reference)
   * @return Plot2D
   */
  public Plot2D plotxyAccuracy() {
    LCDTarget lcdTarget = measurement[0];
    Plot2D plot = Plot2D.getInstance("xy Accuracy (" + reference.getDescription() +
                                     " vs " + lcdTarget.getDescription() + ")");

    int size = reference.size();
    for (int index = 0; index < size; index++) {
      Patch p = reference.getPatch(index);
      double[] xyValues = p.getXYZ().getxyValues();
      double luminance = p.getXYZ().Y;

      double[] target = null;
      if (isRamp256Target) {
        target = repeatability.getxyYMean(luminance);
      }
      else {
        target = lcdTarget.getPatch(index).getXYZ().getxyValues();
      }

      double x = Math.abs(xyValues[0] - target[0]);
      double y = Math.abs(xyValues[1] - target[1]);
      if (isRamp256Target) {
        plot.addCacheScatterLinePlot("x", Color.red, luminance, x);
        plot.addCacheScatterLinePlot("y", Color.green, luminance, y);
      }
      else {
        plot.addCacheScatterPlot("x", Color.red, luminance, x);
        plot.addCacheScatterPlot("y", Color.green, luminance, y);
      }

    }
//    if (isRamp256Target) {
//      plot.drawCachePlot();
//    }
//    else {
    plot.drawCachePlot();
//    }

    plot.setAxeLabel(0, "Luminance");
    plot.setAxeLabel(1, "delta");
    plot.addLegend();

    plot.setVisible(true);
    return plot;
  }

  public final static Plot2D plotxyDistribution(LCDTarget[] lcdTargets) {
    Plot2D plot = Plot2D.getInstance("xy Distribution");
    int index = 0;
    boolean isRamp256Target = lcdTargets[0].getNumber() ==
        LCDTargetBase.Number.Ramp256W;

    for (LCDTarget target : lcdTargets) {
      Color c = plot.getNewColor(index++);
      List<Patch> patchList = target.filter.grayPatch();
      String name = target.getDescription();
      for (Patch p : patchList) {
//        double[] xyValues = p.getXYZ().getxyValues();
        CIExyY xyY = new CIExyY(p.getXYZ());
        if (isRamp256Target) {
//          plot.addCacheScatterPlot(name + "-x", c, xyY.Y, xyY.x);
//          plot.addCacheScatterPlot(name + "-y", c, xyY.Y, xyY.y);
          plot.addCacheScatterLinePlot(name + "-x", c, xyY.Y, xyY.x);
          plot.addCacheScatterLinePlot(name + "-y", c, xyY.Y, xyY.y);
        }
        else {
          plot.addCacheScatterPlot(name + "-x", c, xyY.Y, xyY.x);
          plot.addCacheScatterPlot(name + "-y", c, xyY.Y, xyY.y);
        }

      }
    }

//    if (isRamp256Target) {
    plot.drawCachePlot();
//    }
//    else {
//      plot.drawCacheScatterPlot();
//    }

    plot.setAxeLabel(0, "Luminance");
    plot.setAxeLabel(1, "x/y");
    plot.addLegend();

    plot.setVisible(true);
    return plot;
  }

  public Plot2D plotDeltaEAccuracy() {
    LCDTarget lcdTarget = measurement[0];
    Plot2D plot = Plot2D.getInstance("DeltaE Accuracy (" +
                                     lcdTarget.getDescription() +
                                     ")");
    CIEXYZ white = reference.getWhitePatch().getXYZ();

    int size = reference.size();
    for (int index = 0; index < size; index++) {
      Patch p = reference.getPatch(index);
      CIEXYZ XYZ = p.getXYZ();
      double luminance = p.getXYZ().Y;

      CIEXYZ targetXYZ = null;
//      if (isRamp256Target) {
//        double[] target = repeatability.getxyYMean(luminance);
//        targetXYZ = new CIExyY(target).toXYZ();
//      }
//      else {
      targetXYZ = lcdTarget.getPatch(index).getXYZ();
//      }

      DeltaE de = new DeltaE(XYZ, targetXYZ, white);
      if (isRamp256Target) {
        plot.addCacheScatterLinePlot("dE00", Color.red, luminance,
                                     de.getCIE2000DeltaE());
        plot.addCacheScatterLinePlot("dab00", Color.green, luminance,
                                     de.getCIE2000Deltaab());
      }
      else {
        plot.addCacheScatterPlot("dE00", Color.red, luminance,
                                 de.getCIE2000DeltaE());
        plot.addCacheScatterPlot("dab00", Color.green, luminance,
                                 de.getCIE2000Deltaab());
      }

    }
//    if (isRamp256Target) {
//      plot.drawCachePlot();
//    }
//    else {
    plot.drawCachePlot();
//    }

    plot.setAxeLabel(0, "Luminance");
    plot.setAxeLabel(1, "delta");
    plot.addLegend();

    plot.setVisible(true);
    return plot;
  }

  /**
   * 繪出measurement的Y準確率(參考自reference)
   * @return Plot2D
   */
  public Plot2D plotYAccuracy() {
    LCDTarget lcdTarget = measurement[0];
    Plot2D plot = Plot2D.getInstance("Y Accuracy (" + lcdTarget.getDescription() +
                                     ")");

    int size = reference.size();
    for (int index = 0; index < size; index++) {
      Patch p = reference.getPatch(index);
      double luminance = p.getXYZ().Y;

      double[] target = null;
//      if (isRamp256Target) {
//        target = repeatability.getxyYMean(luminance);
//      }
//      else {
      target = new CIExyY(lcdTarget.getPatch(index).getXYZ()).getValues();
//      }

      double Y = (Math.abs(luminance - target[2]) / luminance) * 100.;

      if (isRamp256Target) {
        plot.addCacheScatterLinePlot("Y(%)", Color.green, luminance, Y);
      }
      else {
        plot.addCacheScatterPlot("Y(%)", Color.green, luminance, Y);
      }

    }

//    if (isRamp256Target) {
    plot.drawCachePlot();
//    }
//    else {
//      plot.drawCacheScatterPlot();
//    }

    plot.setAxeLabel(0, "Luminance");
    plot.setAxeLabel(1, "deltaY");
    plot.addLegend();

    plot.setVisible(true);
    return plot;
  }

  public double[] getMeanxyAccuracy() {
    double xTotal = 0;
    double yTotal = 0;
    LCDTarget lcdTarget = measurement[0];

    int size = reference.size();
    for (int index = 0; index < size; index++) {
      Patch p = reference.getPatch(index);
      double luminance = p.getXYZ().Y;

      double[] target = null;
      if (isRamp256Target) {
        target = repeatability.getxyYMean(luminance);
      }
      else {
        target = lcdTarget.getPatch(index).getXYZ().getxyValues();
      }

      double[] xyValues = p.getXYZ().getxyValues();
      xTotal += Math.abs(xyValues[0] - target[0]);
      yTotal += Math.abs(xyValues[1] - target[1]);
    }

    return new double[] {
        xTotal / size, yTotal / size};
  }

  public double[] getMeanxyRepeatability() {
//    LCDTarget lcdTarget = measurement[0];
    double xTotal = 0;
    double yTotal = 0;

    int size = reference.size();
    for (int index = 0; index < size; index++) {
      Patch p = reference.getPatch(index);
      double luminance = p.getXYZ().Y;
      double[] std = repeatability.getxyYSTD(luminance);
      xTotal += std[0] * 2.;
      yTotal += std[1] * 2.;
    }
    return new double[] {
        xTotal / size, yTotal / size};
  }

  /**
   * 繪出measurement的xy重複性
   * @return Plot2D
   */
  public Plot2D plotxyRepeatability() {
    LCDTarget lcdTarget = measurement[0];
    Plot2D plot = Plot2D.getInstance("xy Repeatability (" +
                                     lcdTarget.getDescription() +
                                     ")");

    int size = reference.size();
    for (int index = 0; index < size; index++) {
      Patch p = reference.getPatch(index);
      double luminance = p.getXYZ().Y;
      double[] std = repeatability.getxyYSTD(luminance);
      double x = std[0] * 2.;
      double y = std[1] * 2.;
      plot.addCacheScatterLinePlot("x", Color.red, luminance, x);
      plot.addCacheScatterLinePlot("y", Color.green, luminance, y);
    }
    plot.drawCachePlot();
    plot.setAxeLabel(0, "Luminance");
    plot.setAxeLabel(1, "delta");
    plot.addLegend();

    plot.setVisible(true);
    return plot;
  }

  public MeterVerifier(LCDTarget refTarget, LCDTarget measureTarget) {
    this(refTarget, new LCDTarget[] {measureTarget, measureTarget});
  }

  /**
   * 只有isRamp256Target才能用repeatability運算, 因為isRamp256Target的狀況下,
   * 一個亮度才會對應到唯一的色塊
   */
  protected boolean isRamp256Target = false;

  public MeterVerifier(LCDTarget refTarget, LCDTarget[] measureTargets) {
    this.reference = refTarget;
    this.measurement = measureTargets;
    this.repeatability = new Repeatability(measureTargets);
    isRamp256Target = (refTarget.getNumber() == LCDTargetBase.Number.Ramp256W);
  }

  public MeterVerifier(LCDTarget[] refTargets, LCDTarget[] measureTargets) {
    this(LCDTarget.Operator.average(refTargets), measureTargets);
  }

  public MeterVerifier(LCDTarget[] measureTargets) {
    this.measurement = measureTargets;
    this.repeatability = new Repeatability(measureTargets);
    isRamp256Target = (measureTargets[0].getNumber() ==
                       LCDTargetBase.Number.Ramp256W);
  }

  public static void main(String[] args) {
    LCDTarget.setRGBNormalize(false);

//    LCDTarget refTarget = LCDTarget.Instance.getFromLogo(
//        CMSDir.Measure.Monitor +
//        "/hp_dv2102/ca210/darkroom/D65/99.logo");
    LCDTarget ca1 = LCDTarget.Instance.getFromVastView(
        CMSDir.Measure.Monitor +
        "/cpt_170EA/ca210/darkroom/native/ca-210-1-verify.txt");
    LCDTarget ca1_256 = ca1.targetFilter.getRamp256W();

//    LCDTarget samTarget = LCDTarget.Instance.getFromLogo(
//        CMSDir.Measure.Monitor +
//        "/hp_dv2102/i1pro/darkroom/D65/99.logo");
    LCDTarget ca2 = LCDTarget.Instance.getFromVastView(
        CMSDir.Measure.Monitor +
        "/cpt_170EA/ca210/darkroom/native/ca-210-2-verify.txt");
    LCDTarget ca2_256 = ca2.targetFilter.getRamp256W();
    LCDTarget ca3 = LCDTarget.Instance.getFromVastView(
        CMSDir.Measure.Monitor +
        "/cpt_170EA/ca210/darkroom/native/ca-210-3-verify.txt");
    LCDTarget ca3_256 = ca3.targetFilter.getRamp256W();

//    FourColorCalibrator.calibrate(ca3, ca2);

    MeterVerifier verifier = new MeterVerifier(ca3_256, ca2_256);
    verifier.plotxyRepeatability();
    verifier.plotYRepeatability();
    verifier.plotxyAccuracy();
    verifier.plotYAccuracy();
    verifier.plotDeltaEAccuracy();
    System.out.println("mcdm:" + verifier.getMeanDeltaEMCDM() + " dE00");
    System.out.println("Accuracy:" + verifier.getMeanDeltaEAccuracy() + " dE00");
    System.out.println("Repeatability:" +
                       Arrays.toString(verifier.getMeanxyRepeatability()) +
                       " (x,y)");
    System.out.println("Accuracy:" + Arrays.toString(verifier.getMeanxyAccuracy()) +
                       " (x,y)");

    LCDTarget[] targets = new LCDTarget[] {
        ca1, ca2, ca3};
    MeterVerifier.plotxyDistribution(targets);

  }
}
