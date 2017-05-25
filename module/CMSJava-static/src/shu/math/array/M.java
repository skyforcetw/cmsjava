package shu.math.array;

import shu.math.*;

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
public abstract class M
    extends DoubleArray {
  public final static double[] to1D(double ...values) {
    return values;
  }

  public final static double[][] square(double ...values) {
    int width = (int) Math.sqrt(values.length);
    if (Maths.sqr(width) != values.length) {
      throw new IllegalArgumentException("Cannot make square matrix.");
    }
    return DoubleArray.to2DDoubleArray(values, width);
  }

  public final static double[][] to2D(int width, double ...values) {
    int height = values.length / width;
    if (height * width != values.length) {
      throw new IllegalArgumentException("Cannot make matrix.");
    }
    return DoubleArray.to2DDoubleArray(values, width);
  }

  public static void main(String[] args) {
//    System.out.println(DoubleArray.toString(square(1, 2, 3, 4, 5, 6, 7, 8, 9)));
    System.out.println(DoubleArray.toString(to2D(5, 1, 2, 3, 4, 5, 6, 7, 8, 9)));
  }
}
