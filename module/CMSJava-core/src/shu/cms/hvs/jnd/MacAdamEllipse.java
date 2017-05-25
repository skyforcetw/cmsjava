package shu.cms.hvs.jnd;

import java.awt.*;

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
public class MacAdamEllipse {

  public static class Ellipse {
    public Ellipse(double a, double b, double theta, double[] center) {
      this.a = a;
      this.b = b;
      this.theta = theta;
      rad = Math.toRadians(theta);
      cos = Math.cos(rad);
      sin = Math.sin(rad);
      this.center = center;
    }

    public Ellipse(double a, double b, double theta) {
      this(a, b, theta, new double[] {0, 0});
    }

    public double[][] getxy(double x) {
      double y = Math.sqrt( (1. - Maths.sqr(x - center[0]) / Maths.sqr(a)) *
                           Maths.sqr(b));
      double y1 = y;
      double y2 = -y;
      double X1 = x * cos + y1 * sin + center[0];
      double X2 = x * cos + y2 * sin + center[0];
      double Y1 = y1 * cos - x * sin + center[1];
      double Y2 = y2 * cos - x * sin + center[1];

      return new double[][] {
          {
          X1, Y1}, {
          X2, Y2}
      };
    }

    public void plot(Plot2D plot, double precision) {
      double[] xrange = getxRange();
      for (double x = xrange[0]; x <= xrange[1]; x += precision) {
        double[][] xy = getxy(x);
        plot.addScatterPlot("", Color.black, xy[0][0], xy[0][1]);
        plot.addScatterPlot("", Color.black, xy[1][0], xy[1][1]);
      }
    }

    public double[] getxRange() {
      return new double[] {
          -a * cos - b * sin + center[0], a * cos + b * sin + center[0]};
    }

//    public double[] getyRange() {
//      return new double[] {
//          -b * cos - a * sin, b * cos + a * sin};
//    }

    protected double[] center;
    protected double cos, sin;
    protected double rad;
    protected double a, b, theta;
//    protected double g11;
//    protected double g12;
//    protected double g22;
  }

  public static void main(String[] args) {
//    Plot2D p = Plot2D.getInstance();
//    double a = 1;
//    double b = 2;
//    double theta = 45;
//    double d = Math.toRadians(theta);
//    for (double x = -a; x <= a; x += 0.01) {
//      double y = Math.sqrt( (1 - Maths.sqr(x) / Maths.sqr(a)) * Maths.sqr(b));
//      double y1 = y;
//      double y2 = -y;
//      double X1 = x * Math.cos(d) + y1 * Math.sin(d);
//      double X2 = x * Math.cos(d) + y2 * Math.sin(d);
//      double Y1 = y1 * Math.cos(d) - x * Math.sin(d);
//      double Y2 = y2 * Math.cos(d) - x * Math.sin(d);
//      p.addScatterPlot("", Color.black, X1, Y1);
//      p.addScatterPlot("", Color.black, X2, Y2);
//    }
//    p.setVisible();
    double[] p = OBSERVED_PARAMETER[12];
    Ellipse e = new Ellipse(p[0] / 1000., p[1] / 1000., p[2], COLOR_CENTER[12]);
    Plot2D plot = Plot2D.getInstance();
    e.plot(plot, 0.0001);
    plot.setVisible();
  }

  private final static double[][] OBSERVED_PARAMETER = new double[][] {
      {
      0.85, 0.35, 62.5}, {
      2.2, 0.55, 77.0}, {
      2.5, 0.50, 55.5}, {
      9.6, 2.3, 105.0}, {
      4.7, 2.0, 112.5},

      {
      5.8, 2.3, 100.0}, {
      5.0, 2.0, 92.0}, {
      3.8, 1.9, 110.0}, {
      4.0, 1.5, 75.5}, {
      4.4, 1.2, 70.0},

      {
      2.1, 0.95, 104.0}, {
      3.1, 0.90, 72.0}, {
      2.3, 0.90, 58.0}, {
      3.8, 1.6, 65.5}, {
      3.2, 1.4, 51.0},

      {
      2.6, 1.3, 20.0}, {
      2.9, 1.1, 28.5}, {
      2.4, 1.2, 29.5}, {
      2.6, 1.3, 13.0}, {
      2.3, 0.90, 60.0},

      {
      2.5, 1.0, 47.0}, {
      2.8, 0.95, 34.5}, {
      2.4, 0.55, 57.5}, {
      2.9, 0.60, 54.0}, {
      3.6, 0.95, 40.0}
  };

  private final static double[][] COLOR_CENTER = new double[][] {
      {
      0.160, 0.057}, {
      0.187, 0.118}, {
      0.253, 0.125}, {
      0.150, 0.680}, {
      0.131, 0.521},

      {
      0.212, 0.550}, {
      0.258, 0.450}, {
      0.152, 0.365}, {
      0.280, 0.385}, {
      0.380, 0.498},

      {
      0.160, 0.200}, {
      0.228, 0.250}, {
      0.305, 0.323}, {
      0.385, 0.393}, {
      0.472, 0.399},

      {
      0.527, 0.350}, {
      0.475, 0.300}, {
      0.510, 0.236}, {
      0.596, 0.283}, {
      0.344, 0.284},

      {
      0.390, 0.237}, {
      0.441, 0.198}, {
      0.278, 0.223}, {
      0.300, 0.163}, {
      0.365, 0.153}

  };
}
