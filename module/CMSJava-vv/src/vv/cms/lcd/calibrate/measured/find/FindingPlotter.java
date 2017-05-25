package vv.cms.lcd.calibrate.measured.find;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import vv.cms.lcd.calibrate.measured.algo.*;
import shu.cms.plot.*;
import shu.math.array.*;

//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �ΨӤ䴩CoordinateCalibratorø�ϥΪ����O
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class FindingPlotter {
  /**
   * �O�_�i��plot
   */
  private static boolean Plotting = true;
  /**
   * �O�_�n��RGB plot�X��
   */
  private static boolean PlottingRGB = false;
  /**
   * plot�����O�_�n�Ȱ�
   */
  private static boolean PlottingPause = false;

  private String title;
  public FindingPlotter(String title) {
    this.title = title;
    plotInit();
  }

  void close() {
    if (puv != null) {
      puv.setVisible(false);
      puv.dispose();
    }
    if (prgb != null) {
      prgb.setVisible(false);
      prgb.dispose();
    }
  }

  //==========================================================================
  // plot
  //==========================================================================

  private Plot3D puv, prgb;
  private double[] predot;
  private double[] prergb;

  /**
   * plot����l��
   */
  private void plotInit() {
    if (Plotting && puv == null) {
      puv = Plot3D.getInstance(title + " u'v'Y");
      puv.setAxeLabel(0, "u'");
      puv.setAxeLabel(1, "v'");
      puv.setAxeLabel(2, "Y");
      puv.setVisible();

      if (PlottingRGB) {
        prgb = Plot3D.getInstance(title + " RGB");
        prgb.setAxeLabel(0, "R");
        prgb.setAxeLabel(1, "G");
        prgb.setAxeLabel(2, "B");
        prgb.setVisible();
        PlotUtils.arrange(new shu.plot.PlotWindow[] {puv, prgb}, 2, true);
      }
    }
  }

  /**
   * ��ؼ��I�yø�X��(�buvY�Ŷ��W)
   * @param target double[]
   */
  void plotTargetAtuv(double[] target) {
    if (Plotting) {
//        plotInit();
      puv.addScatterPlot("target", Color.red, target);
    }
  }

  private int plotIndex = 0;

  void plot(CIEXYZ XYZ, Color c) {
    if (Plotting) {
      CIExyY xyY = new CIExyY(XYZ);
      double[] uvpY = xyY.getuvPrimeYValues();
      puv.addScatterPlot("", c, uvpY);
    }
  }

  void plotRGB(RGB rgb, Color c, String name) {
    if (Plotting && PlottingRGB) {
      double[] rgbValues = rgb.getValues();
      if (prergb != null) {
        double[] v = DoubleArray.minus(rgbValues, prergb);
        prgb.addVectortoPlot(name, c, prergb, v);
      }
      prergb = rgbValues;
    }
  }

  /**
   * �N���Gø�X
   * @param result Result
   * @param c Color
   */
  void plot(AlgoResult result, Color c) {
    if (Plotting) {
      String name = "No." + plotIndex++;
      CIEXYZ XYZ = result.getNearestXYZ();
      CIExyY xyY = new CIExyY(XYZ);
      double[] uvpY = xyY.getuvPrimeYValues();
      if (predot != null) {
        double[] v = DoubleArray.minus(uvpY, predot);
        puv.addVectortoPlot(name, c, predot, v);
      }
      predot = uvpY;

      plotRGB(result.nearestRGB, c, name);

      if (PlottingPause) {
        try {
          Thread.currentThread().sleep(1000);
        }
        catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  public final static void setPlotting(boolean plotting) {
    Plotting = plotting;
  }

  //==========================================================================

}
