package auo.cms.test;

import shu.math.lut.Interpolation1DLUT;
import shu.cms.colorformat.adapter.xls.AUOCPTableXLSAdapter;
import jxl.read.biff.*;
import java.io.*;
import shu.cms.colorspace.depend.RGB;
import java.util.List;
import shu.cms.plot.Plot2D;
import java.awt.Color;
import shu.math.array.DoubleArray;
import java.util.*;
///import shu.plot.*;

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
public class CCTMapper {
  public static void main(String[] args) {
//    double[] percent = new double[] {
//        0.999494636, 0.999494636, 0.99941656, 0.999090944, 0.999461679,
//        0.999436532,
//        1.000251619, 1.001338391, 1.001944216, 1.002046996, 1.001219626,
//        1.000519645, 0.999109677, 0.998999112, 0.998999112
//    };
//    double[] grayLevel = new double[] {
//        0, 63, 127, 144, 152, 160,
//        168, 176, 184, 192, 200,
//        208, 216, 224, 255
//    };

    /*double[] percent = new double[] {
        0.999647294, 0.999647294, 0.999675131, 0.999814192, 0.99995336,
        1.000152343,
        1.000467737, 1.000873412, 1.001390313, 1.001843309, 1.001073384,
        1.000359747, 0.999741211, 0.999452954, 0.999452954
         };

         double[] grayLevel = new double[] {
        0, 128, 136, 144, 152, 160, 168, 176, 184, 192, 200, 208, 216, 224, 255
         };*/
//    double[] percent = new double[] {
//        1.00051837, 1.00051837, 1.000670655, 1.000797229, 1.001012171,
//        1.001026686, 1.002080985, 1.004732604, 1.004120767, 1.004285127
//    };
//    double[] grayLevel = new double[] {
//        0, 128, 144, 160, 176, 192, 208, 224, 240, 255
//    };

    double[] percent = new double[] {
        1.000026634,
        0.999984934,
        0.999960264,
        0.999950681,
        0.99996079,
        0.999980508,
        0.999941852,
        0.999922913,
        0.999932928,
        1.000047648,
        1.000056849,
        0.999981174,
        0.999971952,
        0.99998142,
        1.000027668,
        1.000091565,
        1.00000909,
        1.000099171,
        1.000125163,
        1.00007084,
        1.000044182,
        1.000349491,
        1.001126814,
        1.002756871,
        1.003133297,
        1.002540247,
        1.001898164,
        1.002715047


    };

    double[] grayLevel = new double[] {
        0,
        10,
        20,
        30,
        40,
        50,
        60,
        70,
        80,
        90,
        100,
        110,
        120,
        130,
        140,
        150,
        160,
        170,
        180,
        190,
        192,
        200,
        210,
        220,
        230,
        240,
        250,
        255,

    };

    //grayLevel = reverse(grayLevel);
    //percent = reverse(percent);

    Interpolation1DLUT lut = new Interpolation1DLUT(grayLevel, percent,
        Interpolation1DLUT.Algo.LINEAR);
    try {
      AUOCPTableXLSAdapter cp = new AUOCPTableXLSAdapter("Cal_Table00.xls",
          RGB.MaxValue.Int12Bit);
      List<RGB> rgbList = cp.getRGBList();
      Plot2D plot = Plot2D.getInstance();
      Plot2D plot2 = Plot2D.getInstance();

//      for (int x = 255; x >= 0; x--) {
      for (int x = 0; x < 256; x++) {
        RGB rgb = rgbList.get(x);

        double rp = lut.getValue(rgb.getValue(RGB.Channel.R,
                                              RGB.MaxValue.Double255));
        double gp = lut.getValue(rgb.getValue(RGB.Channel.G,
                                              RGB.MaxValue.Double255));
        double bp = lut.getValue(rgb.getValue(RGB.Channel.B,
                                              RGB.MaxValue.Double255));
        System.out.println(rp + " " + gp + " " + bp);
        plot.addCacheScatterLinePlot("r", Color.red, x, rp );
        plot.addCacheScatterLinePlot("g", Color.green, x, gp);
        plot.addCacheScatterLinePlot("b", Color.blue, x, bp);
        plot2.addCacheScatterLinePlot("b/r", Color.blue, x,  1/rp);
//        plot2.addCacheScatterLinePlot("b/r+", Color.blue, x, bp * rp);
      }

      plot.setVisible();
      plot2.setVisible();
    }

    catch (BiffException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private static double[] reverse(double[] original) {
    int size = original.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = original[size - 1 - x];
    }
    return result;
  }
}
