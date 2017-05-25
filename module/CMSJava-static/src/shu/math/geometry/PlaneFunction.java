package shu.math.geometry;

import javax.vecmath.*;

//import shu.cms.plot.*;
import shu.math.regress.*;

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
public class PlaneFunction {
  public String toString() {
    return a + "x + " + b + "y + " + c + "z + " + d + " = 0";
//    return "a:" + a + " b:" + b + " c:" + c + " d:" + d;
  }

  public final static PlaneFunction getInstance(Point3d[] point3dArray) {
    int size = point3dArray.length;
    double[][] xyzValues = new double[size][];
    for (int x = 0; x < size; x++) {
      Point3d p = point3dArray[x];
      xyzValues[x] = new double[] {
          p.x, p.y, p.z};
    }
    return getInstance(xyzValues);
  }

  public final static PlaneFunction getInstance(double[][] xyzValues) {
    int size = xyzValues.length;
    double[][] input = new double[size][];
    double[][] output = new double[size][1];
//    DoubleArray.fill()
    for (int x = 0; x < size; x++) {
      double[] xyz = xyzValues[x];
      input[x] = new double[] {
          xyz[0], xyz[2], 1};
      output[x][0] = xyz[1];
    }

    Regression regression = new Regression(input, output);
    regression.regress();
    double[][] coefs = regression.getCoefs();
    double[] coef = coefs[0];
    PlaneFunction planeFunction = new PlaneFunction( -coef[0], 1, -coef[1],
        -coef[2]);
    return planeFunction;
  }

//  public static void main(String[] args) {
//    double[][] xyzValues = new double[][] {
//        {
//        3, -1, 1}, {
//        4, 2, -1}, {
//        7, 0, 3}
//    };
//
//    Plot3D plot = Plot3D.getInstance();
//    plot.setVisible();
//    double[] point = new double[] {
//        3, -1, 2};
//    Point3d p3 = new Point3d(point);
//    plot.addScatterPlot("", Color.green, point);
//    plot.addPolygonPlot("", Color.red, xyzValues[0], xyzValues[1], xyzValues[2]);
//
//    PlaneFunction pf = getInstance(xyzValues);
//    System.out.println(pf);
//    System.out.println(pf.isUpper(p3) + " " + pf.isLower(p3));
//  }

  public double a;
  public double b;
  public double c;
  public double d;

  public PlaneFunction(double a, double b, double c, double d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  /**
   * p點是否在此面之上
   * @param p Point3d
   * @return boolean
   */
  public boolean isUpper(Point3d p) {
    double result = result(p.x, p.y, p.z);
    return result > 0;
  }

  public boolean isUpper(double x, double y, double z) {
    double result = result(x, y, z);
    return result > 0;
  }

  protected double result(double x, double y, double z) {
    return a * x + b * y + c * z + d;
  }

  /**
   * p點是否在此面之下
   * @param p Point
   * @return boolean
   */
  public boolean isLower(Point3d p) {
    double result = result(p.x, p.y, p.z);
    return result < 0;
  }

  public boolean isLower(double x, double y, double z) {
    double result = result(x, y, z);
    return result < 0;
  }

  public boolean isAtPlane(Point3d p) {
    return isAtPlane(p.x, p.y, p.z);
  }

  public boolean isAtPlane(double x, double y, double z) {
    double result = result(x, y, z);
    return result == 0;
  }
}
