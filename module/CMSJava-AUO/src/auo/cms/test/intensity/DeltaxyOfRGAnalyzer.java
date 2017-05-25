package auo.cms.test.intensity;

import shu.io.files.ExcelFile;
import java.io.*;
import jxl.read.biff.*;
import shu.cms.plot.Plot2D;
import java.awt.Color;
import shu.math.array.*;
import shu.cms.colorspace.independ.*;

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
public class DeltaxyOfRGAnalyzer {

  public static void main(String[] args) {
    CIExyY whitexy = new CIExyY(0.27975, 0.28989, 1);
    CIExyY blackxy = new CIExyY(0.25255, 0.24748, 1);
    double[] dxyOfWK = whitexy.getDeltaxy(blackxy);
    ExcelFile xls = null;
    try {
      xls = new ExcelFile("dxofR.xls");
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }

    Plot2D plot = Plot2D.getInstance();
    int size = xls.getRows();
    double[][] dxdyArray = new double[size - 1][];
    for (int x = 1; x < xls.getRows(); x++) {
      double dx = xls.getCell(0, x);
      double dy = xls.getCell(1, x);
      dxdyArray[x - 1] = new double[] {
          dx, dy};
      System.out.println(x + " " + dx + " " + dy);
      plot.addCacheScatterLinePlot("dx", Color.red, x, dx);
      plot.addCacheScatterLinePlot("dy", Color.green, x, dy);
    }
    plot.setVisible();
    double[][] dxdyArrayT = DoubleArray.transpose(dxdyArray);
//    double sumOfDx = DoubleArray.sum(dxdyArrayT[0]);
//    double sumOfDy = DoubleArray.sum(dxdyArrayT[1]);
    double[][] accumulatedxdyArrayT = DoubleArray.copy(dxdyArrayT);
    int arraySize = accumulatedxdyArrayT[0].length;
    for (int x = 1; x < arraySize; x++) {
      accumulatedxdyArrayT[0][x] += accumulatedxdyArrayT[0][x - 1];
      accumulatedxdyArrayT[1][x] += accumulatedxdyArrayT[1][x - 1];
    }
    Plot2D plot2 = Plot2D.getInstance();
    for (int x = 0; x < size; x++) {
//       accumulatedxdyArrayT[0][x]/
    }
    plot2.setVisible();
  }
}
