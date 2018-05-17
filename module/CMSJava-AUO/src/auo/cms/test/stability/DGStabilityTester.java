package auo.cms.test.stability;

import shu.cms.colorformat.adapter.xls.AUOCPTableXLSAdapter;
import jxl.read.biff.*;
import java.io.*;
import java.util.*;
import shu.cms.colorspace.depend.*;
import shu.cms.plot.Plot2D;
import java.awt.Color;
import jxl.biff.StringHelper;
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
public class DGStabilityTester {
  static List<RGB> readDG(String filename) {
    AUOCPTableXLSAdapter dg = null;

    try {
      dg = new AUOCPTableXLSAdapter(filename, RGB.MaxValue.Int12Bit);
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    List<RGB> rgbList = dg.getRGBList();
    return rgbList;
  }

  static List<RGB>[] readDGArray(String dirname) {
    java.io.File dir = new java.io.File(dirname);
    java.io.File[] files = dir.listFiles(new FilenameFilter() {
      public boolean accept(java.io.File dir, String name) {
        return name.indexOf(".xls") == (name.length() - 4);
      }
    });

    int size = files.length;
    List[] result = new List[size];
    int count = 0;
    for (java.io.File file : files) {
      String filename = file.getAbsolutePath();
      List<RGB> dg = readDG(filename);
      result[count++] = dg;
    }
//    for (String filename : filenames) {
//      System.out.println(filename);
//    }
    return result;
  }

  public static void main(String[] args) {
    StringHelper.UNICODE_ENCODING = "utf-16LE";
    String dirname =
        "D:\\軟體\\nobody zone\\exp data\\CCTv3\\2012\\120611\\500x1-2";
//        "D:\\軟體\\nobody zone\\exp data\\CCTv3\\2012\\120611\\400x1";
//        "D:\\軟體\\nobody zone\\exp data\\CCTv3\\2012\\120611\\500x1";
//        "D:\\軟體\\nobody zone\\exp data\\CCTv3\\2012\\120608\\新資料夾 (5)(700x3)";
//        "D:\\軟體\\nobody zone\\exp data\\CCTv3\\2012\\120608\\新資料夾 (4)(700x1)";
//        "D:\\軟體\\nobody zone\\exp data\\CCTv3\\2012\\120608\\新資料夾 (3)(500x3)";
//        "D:\\軟體\\nobody zone\\exp data\\CCTv3\\2012\\120608\\新資料夾 (2)(500x2)";
//        "D:\\軟體\\nobody zone\\exp data\\CCTv3\\2012\\120608\\新資料夾 (400x1)";
    List<RGB> [] dgArray = readDGArray(dirname);
    Plot2D plot = Plot2D.getInstance();
    int size = dgArray.length;
    int checkStart = 26;

    for (int x = 1; x < size; x++) {
      List<RGB> dg0 = dgArray[0];
      List<RGB> dg = dgArray[x];
      int count = dg0.size();
      for (int c = checkStart; c < 100; c++) {
        RGB rgb0 = dg0.get(c);
        RGB rgb = dg.get(c);
        double deltar = Math.abs(rgb0.getValue(RGB.Channel.R,
                                               RGB.MaxValue.Int11Bit) -
                                 rgb.getValue(RGB.Channel.R,
                                              RGB.MaxValue.Int11Bit));
        double deltag = Math.abs(rgb0.getValue(RGB.Channel.G,
                                               RGB.MaxValue.Int11Bit) -
                                 rgb.getValue(RGB.Channel.G,
                                              RGB.MaxValue.Int11Bit));
        double deltab = Math.abs(rgb0.getValue(RGB.Channel.B,
                                               RGB.MaxValue.Int11Bit) -
                                 rgb.getValue(RGB.Channel.B,
                                              RGB.MaxValue.Int11Bit));
        plot.addCacheScatterLinePlot("r" + x, Color.red, c, deltar);
        plot.addCacheScatterLinePlot("g" + x, Color.green, c, deltag);
        plot.addCacheScatterLinePlot("b" + x, Color.blue, c, deltab);
      }
    }
    plot.setVisible();
    plot.setFixedBounds(0, checkStart, 99);
    plot.setAxeLabel(0, "Gray Level");
    plot.setAxeLabel(1, "Delta DG(11bit)");
    PlotUtils.setAUOFormat(plot);
  }
}
