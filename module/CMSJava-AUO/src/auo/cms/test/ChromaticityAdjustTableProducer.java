package auo.cms.test;

import shu.io.files.ExcelFile;
import java.io.*;
import jxl.read.biff.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;

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
public class ChromaticityAdjustTableProducer {

  public static void main(String[] args) {
//    produce(args);
    parse(args);
  }

  public static void parse(String[] args) {
    try {
//      ExcelFile dglut = new ExcelFile("5.0_dimComponent.xls");
      ExcelFile measure = new ExcelFile("Measurement00_.xls");
      int rows = measure.getRows();
      CIExyY[] xyYArray = new CIExyY[rows - 1];
      for (int x = 1; x < rows; x++) {
        double _x = measure.getCell(1, x);
        double _y = measure.getCell(2, x);
        double Y = measure.getCell(3, x);
        xyYArray[x - 1] = new CIExyY(_x, _y, Y);
      }
      int grayLevelCount = (rows - 1) / 5;
      Plot2D rplot = Plot2D.getInstance("R");
      Plot2D gplot = Plot2D.getInstance("G");
      Plot2D bplot = Plot2D.getInstance("B");
      Plot2D wplot = Plot2D.getInstance("W");
      for (int x = 0; x < grayLevelCount; x++) {
        CIExyY xyYW = xyYArray[x * 5];
        CIExyY xyYB = xyYArray[x * 5 + 1];
        CIExyY xyYG = xyYArray[x * 5 + 2];
        CIExyY xyYR = xyYArray[x * 5 + 3];
        CIExyY xyYK = xyYArray[x * 5 + 4];
//        System.out.println(xyYB+" "+xyYW);
        double[] dxyB = xyYB.getDeltaxy(xyYK);
        double[] dxyG = xyYG.getDeltaxy(xyYK);
        double[] dxyR = xyYR.getDeltaxy(xyYK);
        double[] dxyW = xyYW.getDeltaxy(xyYK);
        int grayLevel = grayLevelCount - x - 1;
        rplot.addCacheScatterLinePlot("x", grayLevel, dxyR[0]);
        rplot.addCacheScatterLinePlot("y", grayLevel, dxyR[1]);
        gplot.addCacheScatterLinePlot("x", grayLevel, dxyG[0]);
        gplot.addCacheScatterLinePlot("y", grayLevel, dxyG[1]);
        bplot.addCacheScatterLinePlot("x", grayLevel, dxyB[0]);
        bplot.addCacheScatterLinePlot("y", grayLevel, dxyB[1]);
        wplot.addCacheScatterLinePlot("x", grayLevel, dxyW[0]);
        wplot.addCacheScatterLinePlot("y", grayLevel, dxyW[1]);
      }
      rplot.addLegend();
      rplot.setVisible();
      rplot.setFixedBounds(1, -0.001, 0.002);
      gplot.addLegend();
      gplot.setVisible();
      bplot.addLegend();
      bplot.setVisible();
      bplot.setFixedBounds(1, -0.002, 0.001);
      wplot.addLegend();
      wplot.setVisible();
//    wplot.setFixedBounds(1, -0.002, 0.001);

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
  }

  public static void produce(String[] args) {
    try {
      ExcelFile xls = new ExcelFile("5.0_dimComponent.xls");
      int rows = xls.getRows();
      int index = 0;
//      double step = 1. / (255 * 8) * 255;
      for (int x = 1; x < rows; x++) {
        double grayLevel = xls.getCell(0, x);
        double r = xls.getCell(1, x) * 8;
        double g = xls.getCell(2, x) * 8;
        double b = xls.getCell(3, x) * 8;
        System.out.println(index++ +" " + r * 2 + " " + g * 2 + " " + b * 2);
        System.out.println(index++ +" " + (r + 1) * 2 + " " + g * 2 + " " +
                           b * 2);
        System.out.println(index++ +" " + r * 2 + " " + (g + 1) * 2 + " " +
                           b * 2);
        System.out.println(index++ +" " + r * 2 + " " + g * 2 + " " +
                           (b + 1) * 2);
        System.out.println(index++ +" " + (r + 1) * 2 + " " + (g + 1) * 2 + " " +
                           (b + 1) * 2);
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
  }
}
