package auo.cms.hsv.autotune;

import org.math.plot.*;
import auo.cms.colorspace.depend.*;
import auo.cms.hsv.saturation.*;
import auo.cms.hsvinteger.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.plot.*;
import shu.plot.plots.*;

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
public class HuePlanePlotter {
  private static enum Type {
    NoHSVIP, HSVIP, TuneTarget
  }

  private LCDModel model;
  private TuneTarget tuneTarget;
  private IntegerSaturationFormula integerSaturationFormula;
  public HuePlanePlotter(LCDModel model,
                         SingleHueAdjustValue hsvAdjustValue,
                         TuneTarget tuneTarget,
                         IntegerSaturationFormula integerSaturationFormula) {
    this.model = model;
    this.hsvAdjustValue = hsvAdjustValue;
    this.tuneTarget = tuneTarget;
    this.integerSaturationFormula = integerSaturationFormula;
  }

  public HuePlanePlotter(SingleHueAdjustValue hsvAdjustValue,
                         AutoTuner autoTuner) {
    this(autoTuner.model, hsvAdjustValue, autoTuner.tuneTarget,
         autoTuner.integerSaturationFormula);
  }

  public SingleHueAdjustValue hsvAdjustValue;

  private double[][][] getLCHValuesArray(Type type, double hue) {
    double[][][] LChValuesArray = new double[10][10][];

    for (int s = 10; s <= 100; s += 10) {
      int m = s / 10 - 1;
      for (int v = 10; v <= 100; v += 10) {
        int n = v / 10 - 1;
        HSV hsv = new HSV(RGB.ColorSpace.unknowRGB, new double[] {hue, s, v});
        CIELCh LCh = null;

        switch (type) {
          case NoHSVIP:
            CIELab orgLab = model.getLab(hsv.toRGB(), true);
            LCh = new CIELCh(orgLab);
            break;
          case HSVIP:
            AUOHSV auoHSV = new AUOHSV(hsv);
            short[] hsvValues = IntegerHSVIP.getHSVValues(auoHSV,
                hsvAdjustValue,
                integerSaturationFormula, true);
            AUOHSV newAUOHSV = AUOHSV.fromHSVValues3(hsvValues);
            RGB newRGB = newAUOHSV.toRGB();
            CIELab newLab = model.getLab(newRGB, true);
            LCh = new CIELCh(newLab);
            break;
          case TuneTarget:
            CIELab targetLab = tuneTarget.getTargetLab(hsv);
            LCh = new CIELCh(targetLab);
            break;
        }
        LChValuesArray[m][n] = LCh.getValues();
      }
    }
    return LChValuesArray;
  }

  private static void plotPlane(double hue, Plot3D plot3D,
                                String name, java.awt.Color c,
                                double[][][] LChValuesArray) {
    //hue的修正
    for (int x = 0; x < LChValuesArray.length; x++) {
      for (int y = 0; y < LChValuesArray[0].length; y++) {
        double[] LChValues = LChValuesArray[x][y];
        if (hue == 0 || hue == 15) {
          LChValues[2] = (LChValues[2] > 180) ? LChValues[2] - 360 :
              LChValues[2];
        }
        else if (hue == 345) {
          LChValues[2] = (LChValues[2] < 180) ? LChValues[2] + 360 :
              LChValues[2];
        }
      }
    }
//target的plot和設定

    int num = plot3D.addPlanePlot(name, c, LChValuesArray);
    Plot3DPanel panel = (Plot3DPanel) plot3D.getPlotPanel();
    PlanePlot plane = (PlanePlot) panel.getPlot(num);
    plane.fill_shape = false;

    plot3D.setAxisLabels("L*", "C*", "h*");
    plot3D.setFixedBounds(0, 0, 100);
    plot3D.setFixedBounds(1, 0, 100);
    plot3D.setFixedBounds(2, 0, 360);
    plot3D.rotateToAxis(3);
  }

  public Plot3D plotModelHuePlane(double hue, Plot3D plot, String name,
                                  java.awt.Color c) {
    plotPlane(hue, plot, name, c, getLCHValuesArray(Type.NoHSVIP, hue));
    return plot;
  }

  public Plot3D plotModelHuePlane(double hue) {
    String name = Double.toString(hue);
    Plot3D plot = Plot3D.getInstance(name);
    return plotModelHuePlane(hue, plot, name, HSV.getLineColor(hue));
  }

  public Plot3D plotHSVIPHuePlane(double hue, Plot3D plot, String name,
                                  java.awt.Color c) {
    plotPlane(hue, plot, name, c, getLCHValuesArray(Type.HSVIP, hue));
    return plot;
  }

  public Plot3D plotHSVIPHuePlane(double hue) {
    String name = Double.toString(hue);
    Plot3D plot = Plot3D.getInstance(name);
    return plotHSVIPHuePlane(hue, plot, name, HSV.getLineColor(hue));
  }

  public Plot3D plotTuneTargetHuePlane(double hue, Plot3D plot, String name,
                                       java.awt.Color c) {
    plotPlane(hue, plot, name, c, getLCHValuesArray(Type.TuneTarget, hue));
    return plot;
  }

  public Plot3D plotTuneTargetHuePlane(double hue) {
    String name = Double.toString(hue);
    Plot3D plot = Plot3D.getInstance(name);
    return plotTuneTargetHuePlane(hue, plot, name, HSV.getLineColor(hue));
  }

}
