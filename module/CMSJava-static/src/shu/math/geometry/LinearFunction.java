package shu.math.geometry;

import javax.vecmath.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 用來表示線性方程式ax+by+c=0
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class LinearFunction {

  public static void main(String[] args) {
//    LinearFunction lf = getInstance(new double[] {0, 0}, new double[] {1, 2});
    LinearFunction lf = getInstance(new double[] {0.1754825277104071,
                                    0.005286339105915228},
                                    new double[] {0.33331438077735165,
                                    0.3332877057993168});
    System.out.println(lf.getY(0.33331438077735165));
  }

  public double getY(double x) {
    return - (c + a * x) / b;
//    return x * getSlope() - c;
  }

  public String toString() {
    return "a:" + a + " b:" + b + " c:" + c;
  }

  public double a;
  public double b;
  public double c;

  /**
   * 利用兩點式得直線方程式
   * @param p1 Point
   * @param p2 Point
   * @return LinearFunction
   */
  public final static LinearFunction getInstance(Point2d p1, Point2d p2) {

    return new LinearFunction(p1.y - p2.y, p2.x - p1.x,
                              (p1.x * p2.y - p2.x * p1.y));
  }

  public final static LinearFunction getInstance(double[] xyValues1,
                                                 double[] xyValues2) {
    return getInstance(new Point2d(xyValues1), new Point2d(xyValues2));
  }

  public final static LinearFunction getInstance(Point2d p1, double slope) {
    return new LinearFunction(slope, -1, p1.y - slope * p1.x);
  }

  public LinearFunction(double a, double b, double c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  /**
   * 斜率
   * @return double
   */
  public double getSlope() {
//    return a;
//    return -a / b;
    return -a / b;
  }

  /**
   * p點是否在此線之上 (線是否在這個點之下)
   * @param p Point
   * @return boolean
   */
  public boolean isLower(Point2d p) {
    return p.y > - (c + a * p.x) / b;
  }

  /**
   * p點是否在此線之下 (線是否在這個點之上)
   * @param p Point
   * @return boolean
   */
  public boolean isUpper(Point2d p) {
    return p.y < - (c + a * p.x) / b;
  }
}
