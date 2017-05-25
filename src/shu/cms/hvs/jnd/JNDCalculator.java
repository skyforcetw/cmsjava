package shu.cms.hvs.jnd;

import java.util.*;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;
import shu.math.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class JNDCalculator {

  protected final static double deltauvPrimeJND = 0.001;
  protected final static int defaultCirclePoints = 8;

//  public final static DeltaE[] getJNDCircle(CIExyY center, CIExyY white) {
//    return null;
//  }

  public final static double[] getCIExyMinimumJND(CIExyY center, int points) {
    CIExyY[] circle = getJNDCircle(center, points);
    double mindx = Double.MAX_VALUE;
    double mindy = Double.MAX_VALUE;

    for (CIExyY xyY : circle) {
      double[] dxy = xyY.getDeltaxy(center);
      double dx = Math.abs(dxy[0]);
      double dy = Math.abs(dxy[1]);
      if (dx < mindx) {
        mindx = dx;
      }
      if (dy < mindy) {
        mindy = dy;
      }
    }
    return new double[] {
        mindx, mindy};
  }

  /**
   * 以center為中心點, 取points個點, 以JND(u'v'=0.001)為半徑, 形成圓
   * @param center CIExyY
   * @param points int
   * @return CIExyY[]
   */
  public final static CIExyY[] getJNDCircle(CIExyY center, int points) {
    double[] centeruvp = center.getuvPrimeValues();
    double[][] circle = getJNDCircle(centeruvp, points, deltauvPrimeJND);
    CIExyY[] xyYCircle = new CIExyY[points];

    for (int x = 0; x < points; x++) {
      CIExyY xyY = new CIExyY();
      double[] uvp = circle[x];
      xyY.setuvPrimeValues(uvp);
      xyY.Y = center.Y;
      xyYCircle[x] = xyY;
    }
    return xyYCircle;
  }

  /**
   * 以uvPrime為中心點, 取points個點, 形成圓.
   * @param uvPrime double[]
   * @param points int
   * @param deltaJND double JND的定義
   * @return double[][]
   */
  public final static double[][] getJNDCircle(double[] uvPrime, int points,
                                              double deltaJND) {
    double[][] result = new double[points][];
    double angleStep = 360. / points;

    for (int x = 0; x < points; x++) {
      double angle = Math.toRadians(x * angleStep);

      double du = deltaJND * Math.cos(angle);
      double dv = deltaJND * Math.sin(angle);
      result[x] = new double[] {
          uvPrime[0] + du, uvPrime[1] + dv};
    }

    return result;
  }

  public static void main(String[] args) {
//    example1(args);
    example2(args);
    CIExyY xyY = CorrelatedColorTemperature.CCT2DIlluminantxyY(6500);
    double[] values = getCIExyMinimumJND(xyY, 100);
    System.out.println(Arrays.toString(values));
  }

  /**
   * 在xy及u'v'上畫出JND圓
   * @param args String[]
   */
  public static void example2(String[] args) {
    Plot2D plot = Plot2D.getInstance();
    Plot2D plot2 = Plot2D.getInstance();

    CIEXYZ whiteXYZ = Illuminant.D65WhitePoint;
    CIExyY whitexyY = new CIExyY(whiteXYZ);
    double[] whiteuvp = whitexyY.getuvPrimeValues();
    plot.addScatterPlot("", whiteuvp[0], whiteuvp[1]);
    double[] xy = new double[] {
        whitexyY.x, whitexyY.y};
    plot2.addScatterPlot("", whitexyY.x, whitexyY.y);
    double[][] circle = getJNDCircle(xy, 16, 0.0015);
    for (double[] c : circle) {
      CIExyY xyY = new CIExyY(c[0], c[1], 1);
      plot2.addScatterPlot("", Color.black, xyY.x, xyY.y);
      double[] uvp = xyY.getuvPrimeValues();
      plot.addScatterPlot("", Color.black, uvp[0], uvp[1]);
      double[] duvp = xyY.getDeltauvPrime(whitexyY);
      double dist = Math.sqrt(Maths.sqr(duvp[0]) + Maths.sqr(duvp[1]));
      System.out.println(dist + " " + Arrays.toString(duvp));
    }
    plot.setVisible();
    plot2.setVisible();
  }

  /**
   * 畫出JND圓在CIExy上
   * @param args String[]
   */
  public static void example1(String[] args) {

    Plot2D p = Plot2D.getInstance();
    new LocusPlot(p).drawCIExyLocus(true);
    p.setVisible();
    for (RGBBase.Channel ch : RGBBase.Channel.RGBYMCWChannel) {
      RGB rgb = new RGB(RGB.ColorSpace.sRGB);
      rgb.setValue(ch, 1, RGB.MaxValue.Double1);
      CIEXYZ XYZ = rgb.toXYZ();
      CIExyY[] circle = getJNDCircle(new CIExyY(XYZ), 8);

      int size = circle.length;
      for (int x = 1; x < size; x++) {
        CIExyY prexyY = circle[x - 1];
        CIExyY xyY = circle[x];
        p.addLinePlot(ch.name(), Color.black, prexyY.x, prexyY.y, xyY.x,
                      xyY.y);
        if (x == (size - 1)) {
          CIExyY firstxyY = circle[0];
          p.addLinePlot(ch.name(), Color.black, firstxyY.x, firstxyY.y,
                        xyY.x, xyY.y);
        }
      }
    }

//    RGB rgb = new RGB(RGB.ColorSpace.sRGB);
//    rgb.setColor(Color.white);
//    CIEXYZ XYZ = rgb.toXYZ();
//    double[] jnd = getCIExyMinimumJND(new CIExyY(XYZ), 16);
//    System.out.println(Arrays.toString(jnd));
  }
}
