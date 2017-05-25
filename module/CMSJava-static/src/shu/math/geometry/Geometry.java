package shu.math.geometry;

import javax.vecmath.*;

import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class Geometry {

  /**
   * �p����I�����Z��
   * @param p1 Point
   * @param p2 Point
   * @return double
   */
  public final static double getDistance(Point2d p1, Point2d p2) {
    return Math.sqrt(Maths.sqr(p1.x - p2.x) + Maths.sqr(p1.y - p2.y));
  }

  /**
   * �p���I��u�������Z��
   * @param p Point
   * @param l LinearFunction
   * @return double
   */
  public final static double getDistance(Point2d p, LinearFunction l) {
    return Math.sqrt(Maths.sqr(l.a * p.x + l.b * p.y + l.c)) /
        Math.sqrt(Maths.sqr(l.a) + Maths.sqr(l.b));
  }

  /**
   * �M�������u�����I
   * @param func1 LinearFunction
   * @param func2 LinearFunction
   * @return Point
   */
  public final static Point2d getCrossPoint(LinearFunction func1,
                                            LinearFunction func2) {
    Matrix mA = new Matrix(new double[][] { {func1.a, func1.b}, {func2.a,
                           func2.b}
    });
    Matrix mC = new Matrix(new double[][] { { -func1.c}, { -func2.c}
    });
    double[][] xy = mA.solve(mC).getArray();
    Point2d crossPoint = new Point2d(xy[0][0], xy[1][0]);
    return crossPoint;
  }

  public static void main(String[] args) {
    Point2d p0 = new Point2d(1, 2);
    Point2d p1 = new Point2d(2, 2);
    p1.negate();
    p0.add(p1);
    System.out.println(p0);
    System.out.println(p1);
  }

  /**
   * p�O�_�btriangle��
   * @param p Point
   * @param triangle Point[]
   * @return boolean
   */
  public final static boolean isInTriangle(Point2d p, Point2d[] triangle) {
    double s = getTriangleArea(triangle[0], triangle[1], triangle[2]);
    double s0 = getTriangleArea(p, triangle[1], triangle[2]);
    double s1 = getTriangleArea(triangle[0], p, triangle[2]);
    double s2 = getTriangleArea(triangle[0], triangle[1], p);

    if ( (s0 + s1 + s2) > s) {
      return false;
    }
    else {
      return true;
    }
  }

  public final static double getTriangleArea(Point2d[] triangle) {
    if (triangle.length != 3) {
      throw new IllegalArgumentException("triangle.length != 3");
    }
    return getTriangleArea(triangle[0], triangle[1], triangle[2]);
  }

  /**
   * �p��T���Ϊ����n
   * @param p0 Point
   * @param p1 Point
   * @param p2 Point
   * @return double
   */
  public final static double getTriangleArea(Point2d p0, Point2d p1, Point2d p2) {
    double a = getDistance(p0, p1);
    double b = getDistance(p0, p2);
    double c = getDistance(p1, p2);
    double p = (a + b + c) / 2.;
    //���s����
    double S = Math.sqrt(p * (p - a) * (p - b) * (p - c));
    return S;
  }

  /**
   * �p��|���骺��n
   * @param p0 Point3d
   * @param p1 Point3d
   * @param p2 Point3d
   * @param p3 Point3d
   * @return double
   */
  public final static double volume(Point3d p0, Point3d p1, Point3d p2,
                                    Point3d p3) {
    double[][] array = new double[][] {
        {
        1, p0.x, p0.y, p0.z}, {
        1, p1.x, p1.y, p1.z}, {
        1, p2.x, p2.y, p2.z}, {
        1, p3.x, p3.y, p3.z}
    };
    array = DoubleArray.transpose(array);
    return DoubleArray.det(array) * (1. / 6.);
  }

}
