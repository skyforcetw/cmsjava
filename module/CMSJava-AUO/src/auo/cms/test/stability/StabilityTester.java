package auo.cms.test.stability;

import java.io.*;
import java.util.*;

import jxl.read.biff.*;
import shu.cms.colorformat.adapter.xls.*;
import shu.io.files.ExcelFile;
import shu.cms.colorspace.independ.*;
import shu.math.*;
import shu.math.lut.*;

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
public class StabilityTester {
  static double[] readLuminanceArray(ExcelFile xls) {
    int size = xls.getRows() - 1;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      double Y = xls.getCell(3, x + 1);
      result[x] = Y;
    }
    return result;
  }

  static double findMaxVariation(double[] data, int width) {
    int size = data.length;
    int last = size - width + 1;
    double maxDelta = Double.MIN_VALUE;
    int maxIndex = -1;
    for (int x = 0; x < last; x++) {
      for (int y = x + 1; y < x + width; y++) {
        double delta = Math.abs(data[x] - data[y]);
//        maxDelta = Math.max(delta, maxDelta);
        if (maxDelta < delta) {
          maxDelta = delta;
          maxIndex = x;
        }
      }
//      double delta = Math.abs(data[x] - data[x + width]);
//      maxDelta = Math.max(delta, maxDelta);
    }
    System.out.println(maxIndex);
    return maxDelta;
  }

  static void test(String filename) {
    try {
      ExcelFile xls0 = new ExcelFile(filename);
      double[] luminance0 = readLuminanceArray(xls0);
      System.out.println(findMaxVariation(luminance0, 4));
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }

  }

  static double[] getLuminanceArray(String filename) {
    try {
      AUOCPTableXLSAdapter cp = new AUOCPTableXLSAdapter(filename);
      List<CIEXYZ> XYZList = cp.getXYZList();
      int size = XYZList.size();
      double[] result = new double[size];
      for (int x = 0; x < size; x++) {
        CIEXYZ XYZ = XYZList.get(x);
        result[size - 1 - x] = XYZ.Y;
      }
//      DoubleArray.inv
      return result;
    }

    catch (BiffException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
//    test("stability/LCD.xls");
//    test("stability/Copy of LCD.xls");
//    test("stability/BL.xls");
    double[] luminance = getLuminanceArray("stability/Cal_Table01.xls");
    Maths.normalize(luminance);
//    GammaFinder.normalize(luminance,luminance[luminance.length-1],luminance[0]);
    double[] grayLevel = new double[256];
    for (int x = 0; x < 256; x++) {
      grayLevel[x] = x;
    }
    Interpolation1DLUT lut = new Interpolation1DLUT(grayLevel, luminance,
        Interpolation1DLUT.Algo.LINEAR);

    for (int x = 0; x < 256; x++) {
      double normal = x / 255.;
      double gamma = Math.pow(normal, 2.2);
      double key = lut.getKey(gamma);
      System.out.println(key);
    }
  }
}
