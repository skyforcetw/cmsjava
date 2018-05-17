package auo.cms.test.gamma;

import shu.io.files.ExcelFile;
import shu.math.lut.Interpolation1DLUT;
import shu.math.*;
import shu.cms.plot.Plot2D;
import shu.cms.plot.PlotUtils;

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
public class AGFinder {

  public static void main(String[] args) throws Exception {
    ExcelFile excel = new ExcelFile(
        "D:/ณnล้/nobody zone/exp data/debug from TV Team/20120308/raw.xls");
    double[] luminances = new double[256];
    double[] keys = new double[256];
    for (int x = 0; x < 256; x++) {
      double Y = excel.getCell(1, x + 1);
      luminances[255 - x] = Y;
      keys[x] = x;
    }
    final double maxLuminance = Maths.max(luminances);
    final double tolerance = 0.02;
    Interpolation1DLUT lut = new Interpolation1DLUT(keys, luminances);
    double gamma = 2.2;
    Plot2D toleranceYPlot = Plot2D.getInstance();
    Plot2D targetYPlot = Plot2D.getInstance();
    double preY = 0;
    double[] toleranceYArray = new double[256];

    for (int x = 0; x < 256; x++) {
      double normal = x / 255.;
      double gammaNormal = Math.pow(normal, gamma);
      double Y = gammaNormal * maxLuminance;
      targetYPlot.addCacheScatterLinePlot("r2.2", x, Y);
      double toleranceGammaNormal = Math.pow(normal, gamma + tolerance);
      double toleranceGammaY = toleranceGammaNormal * maxLuminance;
      double toleranceY = (Y - toleranceGammaY) * 8;
      toleranceYArray[x] = toleranceY;
      toleranceYPlot.addCacheScatterLinePlot("tol", x, toleranceY);
      double deltaY = Y - preY;
      preY = Y;
      toleranceYPlot.addCacheScatterLinePlot("delta", x, deltaY);
    }

    double targetY = maxLuminance;
    double[] idealLuminanceArray = new double[256];
    idealLuminanceArray[255] = targetY;
//    Plot2D idealYPlot = Plot2D.getInstance();
    int turnGrayLevel = -1;
    double turnGamma = -1;

    for (int x = 254; x >= 0; x--) {
      double tolteranceY = toleranceYArray[x];
      double deltaY = Math.pow( ( (x + 1) / 255.), gamma) * maxLuminance -
          Math.pow( (x / 255.), gamma) * maxLuminance;

      if (deltaY < tolteranceY) {
        if ( -1 == turnGrayLevel) {
          turnGrayLevel = x + 1;
          double preLuminance = idealLuminanceArray[x + 1];
          turnGamma = Math.log(preLuminance / maxLuminance) /
              Math.log( (x + 1) / 255.);

        }

        double smoothGamma = Interpolation.linear(1, turnGrayLevel, 2.2,
                                                  turnGamma, x);
        double normal = x / 255.;
        idealLuminanceArray[x] = Math.pow(normal, smoothGamma) * maxLuminance;
        targetYPlot.addCacheScatterLinePlot("ideal", x, idealLuminanceArray[x]);

      }
      else {
        //critical zone
        idealLuminanceArray[x] = idealLuminanceArray[x + 1] - tolteranceY;
        targetYPlot.addCacheScatterLinePlot("ideal", x, idealLuminanceArray[x]);
      }
      double simDeltaY = idealLuminanceArray[x + 1] - idealLuminanceArray[x];
      toleranceYPlot.addCacheScatterLinePlot("sim", x, simDeltaY);
    }

    toleranceYPlot.addLegend();
    toleranceYPlot.setVisible();
    PlotUtils.setAUOFormat(toleranceYPlot);
    toleranceYPlot.setFixedBounds(0, 0, 255);
    targetYPlot.setVisible();
    targetYPlot.setFixedBounds(0, 0, 255);

    targetYPlot.setVisible();
    targetYPlot.setFixedBounds(0, 0, 255);
    System.out.println("");
  }
}
