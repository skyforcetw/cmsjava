package auo.cms.test.stability;

import shu.io.files.ExcelFile;
import java.io.*;
import jxl.read.biff.*;
import shu.cms.plot.Plot2D;

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
public class FlickerTester {
  static double[][] readLuminanceAndFlicker(String filename) {
    ExcelFile xls = null;
    try {
      xls = new ExcelFile(filename);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
    xls.selectSheet("Measure");
    int rows = xls.getRows();
    int size = (rows - 1) / 2;
    double[][] result = new double[2][size];
    int index = 0;
    for (int x = 1; x < rows; x += 2) {
//      System.out.println(xls.getCell(0, x));
      double luminance = xls.getCell(5, x);
      double flicker = xls.getCell(5, x + 1);
//      System.out.println(luminance + " " + flicker);
      result[0][index] = luminance;
      result[1][index] = flicker;
      index++;
    }

    return result;
  }

  static void printDoubleArray(double[] array) {

    for (double d : array) {
      System.out.println(d);
    }
  }

  public static void main(String[] args) {
    String blfilename =
        "D:\\ณnล้\\nobody zone\\exp data\\CCTv3\\2012\\120622\\stability-bl.xls";
    String lcfilename =
        "D:\\ณnล้\\nobody zone\\exp data\\CCTv3\\2012\\120622\\stability-lc.xls";
    double[][] data0 = readLuminanceAndFlicker(blfilename);
    double[][] data1 = readLuminanceAndFlicker(lcfilename);
    printDoubleArray(data0[0]);
    System.out.println("");
    printDoubleArray(data1[0]);
    System.out.println("");
    printDoubleArray(data1[1]);

//    Plot2D plot = Plot2D.getInstance();
//    plot.setVisible();
  }
}
