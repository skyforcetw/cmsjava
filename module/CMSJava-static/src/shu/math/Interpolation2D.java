package shu.math;

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
public class Interpolation2D {
  public static void main(String[] args) {
    double[] xn = new double[] {
        3, 5};
    double[] yn = new double[] {
        2, 3};
//    double[] values = new double[] {
//        4, 5, 2, 3};
    double[] values = new double[] {
        4, 2, 5, 3};
    double r = bilinear(xn, yn, values, 3.1, 2.4);
    System.out.println(r);
  }

  /**
   *
   * @param xn double[]
   * @param yn double[]
   * @param values double[] values的順序必須為00 01 10 11
   * @param x double
   * @param y double
   * @return double
   */
  public static double bilinear(double[] xn, double[] yn,
                                double[] values, double x, double y) {
    double denominator = (yn[1] - yn[0]) * (xn[1] - xn[0]);

    double q11 = values[0] * ( (yn[1] - y) * (xn[1] - x)) / denominator;
    double q21 = values[1] * ( (y - yn[0]) * (xn[1] - x)) / denominator;
    double q12 = values[2] * ( (yn[1] - y) * (x - xn[0])) / denominator;
    double q22 = values[3] * ( (y - yn[0]) * (x - xn[0])) / denominator;
    double result = q11 + q21 + q12 + q22;
    return result;
  }
}
