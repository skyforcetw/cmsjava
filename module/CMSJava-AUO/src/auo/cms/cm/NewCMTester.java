package auo.cms.cm;

import shu.math.array.*;
import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import shu.cms.colorspace.independ.CIELCh;
import shu.cms.colorspace.depend.HSV;

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
public class NewCMTester {

  static double[] getCMRGBValues(double[][] cm1, double[][] cm2, double ratio,
                                 double[] rgbValues) {
    double[] rgbValues1 = DoubleArray.times(cm1, rgbValues);
    double[] rgbValues2 = DoubleArray.times(cm2, rgbValues);
    double[] result = new double[3];
    for (int x = 0; x < 3; x++) {
      result[x] = rgbValues1[x] * ratio + rgbValues2[x] * (1 - ratio);
    }
    return result;
  }

  static double[] getCMRGBValues(double[][] cm1, double[][] cm2,
                                 double[] rgbValues) {
    double max = DoubleArray.max(rgbValues);
    double min = DoubleArray.min(rgbValues);
    double c = max - min;
    double ratio = c / max;
//    double ratio = c;
//    double ratio = max / 255;
//    double ratio = 0.5;
//    if (ratio != 1) {
//      System.out.println(ratio);
//    }

    double[] rgbValues1 = DoubleArray.times(cm1, rgbValues);
    double[] rgbValues2 = DoubleArray.times(cm2, rgbValues);
    double[] result = new double[3];

    for (int x = 0; x < 3; x++) {
//      result[x] = rgbValues1[x] * ratio + rgbValues2[x] * (1 - ratio);
      result[x] = rgbValues1[x];
      result[x] = (result[x] > 255) ? 255 : result[x];
      result[x] = (result[x] < 0) ? 0 : result[x];
    }
    return result;
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();

    frame.setSize(800, 800);
    frame.setVisible(true);

    Container c = frame.getContentPane();
    Graphics g = c.getGraphics();

    int width = c.getWidth();
    int height = c.getHeight();
    BufferedImage img = new BufferedImage(width,
                                          height, BufferedImage.TYPE_INT_RGB);
    int half = (height / 2) - 1;

    double[] hsbValues = new double[3];
    hsbValues[2] = 100;
    double[] LabValues = new double[3];
    LabValues[1] = half;
    double maxC = CIELCh.fromLabValues(LabValues)[1];
    int xOriginal = (width - height) / 2;
    int xEnd = height + xOriginal;
    int xHalf = half + xOriginal;

//    double[][] cm1 = new double[][] {
//        {
//        1, 0, 0}, {
//        0, 1, 0}, {
//        0, 0, 1}
//    };
//    double[][] cm1 = new double[][] {
//        {
//        1.1, -2.4097461355931544E-14, -0.09999999999997598}, {
//        -0.20000000000016357, 1.3, -0.09999999999983646}, {
//        -0.19999999999979007, -2.0987656057513955E-13, 1.2}
//    };
//    double[][] cm1 = new double[][] {
//        {
//        1.12, -0.0263, -0.0937}, {
//        -0.1724, 1.2661, -0.0937}, {
//        -0.1724, -0.0263, 1.1987}
//    };
    double[] cm = new double[] {
        1.2, 0, 0,
        0, 1, 0,
        0, 0, 1};
    double[][] cm1 = DoubleArray.to2DDoubleArray(cm, 3);

//    double[][] cm1 = new double[][] {
//        {
//        1.1, -0.05, -0.05}, {
//        -0.15, 1.3, -0.15}, {
//        -0.1, -0.1, 1.2}
//    };

//    double[][] cm2 = new double[][] {
//        {
//        1.1, 0, 0}, {
//        0, 1.1, 0}, {
//        0, 0, 1.1}
//    };
    double[][] cm2 = new double[][] {
        {
        1, 0, 0}, {
        0, 1, 0}, {
        0, 0, 1}
    };

    double cmratio = 0.5;

    for (int x = xOriginal; x < xEnd; x++) {
      for (int y = 0; y < height; y++) {
        int a = x - xHalf;
        int b = - (y - half);
        LabValues[1] = a;
        LabValues[2] = b;
        double[] LChValues = CIELCh.fromLabValues(LabValues);
        if (LChValues[1] <= maxC) {

          double s = (LChValues[1] / maxC) * 100;

          hsbValues[0] = LChValues[2];
          hsbValues[1] = s;

          double[] rgbValues = HSV.toRGBValues(hsbValues);
          int codeR = (int) (rgbValues[0] * 255);
          int codeG = (int) (rgbValues[1] * 255);
          int codeB = (int) (rgbValues[2] * 255);

          rgbValues = new double[] {
              codeR, codeG, codeB};
//          rgbValues = getCMRGBValues(cm1, cm2, cmratio, rgbValues);
          rgbValues = getCMRGBValues(cm1, cm2, rgbValues);

          codeR = (int) (rgbValues[0]);
          codeG = (int) (rgbValues[1]);
          codeB = (int) (rgbValues[2]);

          img.setRGB(x, y, ( (codeR << 16) | (codeG << 8) | codeB));

        }
      }
    }

    g.drawImage(img, 0, 0, width, height, null);

  }
}
