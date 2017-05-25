package sky4s.test.math;

/*      This example of the use of BiCubicSpline demonstrates
        interpolation within a data set of the refractive indices of
        water tabulated as a function of both wavelength and temperature.

        Michael Thomas Flanagan
        Created:    May 2003
        Updated:    1 May 2005

 */

import flanagan.interpolation.*;

public class BiCubicSplineExample {

  public static void main(String arg[]) {

    // Array of wavelengths (m)
    double[] wavelength = {
        404.6e-9, 589.32e-9, 706.52e-9};
    // Array of temperatures (Celsius)
    double[] temperature = {
        0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    // Two dimensional array of corresponding refractive indices
    double[][] refrindex = {
        {
        1.34359, 1.34351, 1.34287, 1.3418, 1.34039, 1.33867, 1.33669, 1.33447,
        1.33204, 1.32942, 1.32663}, {
        1.33346, 1.33341, 1.33283, 1.33184, 1.33052, 1.32892, 1.32707, 1.325,
        1.32274, 1.32029, 1.31766}, {
        1.33086, 1.33073, 1.33007, 1.32903, 1.32766, 1.32603, 1.32417, 1.32209,
        1.31983, 1.31739, 1.31481}
    };
    // Interpolation variables
    double x1, x2, y1;

    // Create a BiCubicSpline instance and initialise it to the data stored in the arrays wavelength, temperature and refrindex
    BiCubicSpline bcs = new BiCubicSpline(wavelength, temperature, refrindex);

    // First interpolation at a wavelength of 404.6 nm and a temperature of 20 degrees Celsius
    // also calculates the reqired derivatives for tis and susequent calls to this method
    x1 = 4.046e-7;
    x2 = 20.0;
    y1 = bcs.interpolate(x1, x2);
    System.out.println("The refractive index of water at " + x1 * 1.0e9 +
                       " nm and " + x2 + " C is " + y1);

    // Second interpolation at a wavelength of 590 nm and a temperature of 33 degrees Celsius
    // uses derivatives calculated by first call to this method
    x1 = 5.9e-7;
    x2 = 33.0;
    y1 = bcs.interpolate(x1, x2);
    System.out.println("The refractive index of water at " + x1 * 1.0e9 +
                       " nm and " + x2 + " C is " + y1);
  }
}
