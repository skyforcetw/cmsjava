package shu.math;

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
public class Mathematica {

  public static double[] flatten(double[][] array) {
    return DoubleArray.to1DDoubleArray(array);
  }

  public static double plus(double[] values) {
    double result = 0;
    for (int x = 0; x < values.length; x++) {
      result += values[x];
    }
    return result;
  }
}
