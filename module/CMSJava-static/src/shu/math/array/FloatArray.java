package shu.math.array;

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
public final class FloatArray {
  public static double[][] toDoubleArray(float[][] array) {
    int height = array.length;
    int width = array[0].length;
    double[][] result = new double[height][width];

    for (int x = 0; x < height; x++) {
      for (int y = 0; y < width; y++) {
        result[x][y] = array[x][y];
      }
    }
    return result;
  }

  public static double[][] toDoubleArray(float[] array, int width, int height) {
    if (array.length != width * height) {
      throw new IllegalArgumentException("array.length != width * height");
    }
    double[][] result = new double[height][width];
    int index = 0;
    for (int x = 0; x < height; x++) {
      for (int y = 0; y < width; y++) {
        result[x][y] = array[index++];
      }
    }
    return result;
  }

  public static double[] toDoubleArray(float[] array) {
    int size = array.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = array[x];
    }
    return result;
  }

}
