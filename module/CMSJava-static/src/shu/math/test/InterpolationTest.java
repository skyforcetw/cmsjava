package shu.math.test;

import shu.math.Interpolation;
import flanagan.interpolation.*;
import shu.plot.*;

/**
 * <p>Title: Colour Management System - static</p>
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
public class InterpolationTest {
  public static void main(String[] args) {
//    interpolationTest(args);
    cubic(args);
  }

  public static void cubic(String[] args) {
//    double[] xn = new double[] {
//        0.689, 0.691, 0.74, .75};
//    double[] yn = new double[] {
//        95.0, 98.0, 100.0, 101.0};
    double[] xn = new double[] {
        0.689, 0.691, 0.74};
    double[] yn = new double[] {
        95.0, 98.0, 100.0};
    double input = .701;
    CubicInterpolation interp = new CubicInterpolation(xn, yn, 0);
    System.out.println(interp.interpolate(input));

    CubicSpline interp2 = new CubicSpline(xn, yn);
    System.out.println(interp2.interpolate(input));
  }

  public static void interpolationTest(String[] args) {
//    System.out.println(linear(1, 3, 2, 4, 2));
    double[] xn = new double[] {
        0.689, 0.691, 0.74, .75};
    double[] yn = new double[] {
        95.0, 98.0, 100.0, 101.0};
//    double input = .76;
    double input = .701;

    Plot2D plot = Plot2D.getInstance();
    plot.addLinePlot("", xn, yn);
    plot.setVisible();

    Interpolation interp = new Interpolation(xn, yn);
    System.out.println(interp.interpolate(input,
                                          Interpolation.Algo.CubicPolynomial));
    System.out.println(interp.interpolate(input,
                                          Interpolation.Algo.Cubic));
    System.out.println(interp.interpolate(input,
                                          Interpolation.Algo.Lagrange));
    System.out.println(interp.interpolate(input,
                                          Interpolation.Algo.Lagrange4));
//    System.out.println(interp.interpolate(input,
//                                          Interpolation.Algo.Spline2));
    System.out.println(interp.interpolate(input,
                                          Interpolation.Algo.Linear));
    System.out.println(interp.interpolate(input,
                                          Interpolation.Algo.
                                          QuadraticPolynomial));
//    System.out.println(interp.interpolate(input,
//                                          Interpolation.Algo.Luminance));
    System.out.println(interp.interpolate(input,
                                          Interpolation.Algo.Gamma));
    System.out.println(interp.interpolate(input,
                                          Interpolation.Algo.Gamma2));
  }

}
