package auo.cms.test.stability;

import shu.io.files.ExcelFile;
import java.io.*;
import jxl.read.biff.*;
import shu.math.*;
import shu.cms.plot.Plot2D;
import java.awt.Color;
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
public class LCStabilityTester {
  static double[][] getMeasureData(String filename) {
    double[][] result = null;
    try {
      ExcelFile xls = new ExcelFile(filename);
      xls.selectSheet("Measure");
      int rows = xls.getRows() - 1;
      result = new double[rows][];
      for (int c = 0; c < rows; c++) {
        double grayLevel = xls.getCell(1, c + 1);
        double x = xls.getCell(7, c + 1);
        double y = xls.getCell(8, c + 1);
        double Y = xls.getCell(5, c + 1);
        result[c] = new double[] {
            grayLevel, x, y, Y};
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
    return result;

  }

  static double[][] getMaxVariation(double[][] data) {
    int size = data.length;
    int datacount = 20;
    int count = size / datacount;
    double[][] result = null;
    Plot2D xplot = Plot2D.getInstance("dx");
    Plot2D yplot = Plot2D.getInstance("dy");
    for (int x = 0; x < count; x++) {
//      double[] xs = new double[datacount];
//      double[] ys = new double[datacount];
      int n = 0;
      int init = x * datacount;
      double[] initdata = data[init + 1];
//      Color c = x >= (count / 2) ? Color.red : Color.green;

      for (int y = init + 1; y < init + datacount; y++) {
        double[] d = data[y];
        double dx = Math.abs(d[1] - initdata[1]);
        double dy = Math.abs(d[2] - initdata[2]);
        double grayLevel = d[0];
        if (0 == grayLevel) {
          continue;
        }
//        System.out.println(grayLevel);
        if (x < count / 2) {
          xplot.addCacheScatterLinePlot(grayLevel + "->", Color.red, n, dx);
          yplot.addCacheScatterLinePlot(grayLevel + "->", Color.red, n, dy);
        }
        else {
          xplot.addCacheScatterLinePlot(grayLevel + "<-", Color.green, n, dx);
          yplot.addCacheScatterLinePlot(grayLevel + "<-", Color.green, n, dy);
        }
        n++;

//        xs[n] = data[y][1];
//        ys[n] = data[y][2];
//        n++;
      }
//      Maths.mean(xs);
    }
    xplot.setVisible();
    yplot.setVisible();
//    PlotUtils.setAUOFormat(xplot);
//    PlotUtils.setAUOFormat(yplot);
    return result;
  }

  public static void main(String[] args) {
    String filename = "stability/stability-2(no blank).xls";
//    double[][] data = getMeasureData("stability/stability-2(no blank).xls");
//    double[][] data = getMeasureData("stability/stability(black blank).xls");
    double[][] data = getMeasureData(filename);
    getMaxVariation(data);
  }
}
