package shu.math.array;

import org.math.array.IntegerArray;
import java.util.Comparator;
import java.util.Arrays;

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
public class IntArray
    extends IntegerArray {

  public static void abs(int[] m) {
    int size = m.length;
    for (int x = 0; x < size; x++) {
      m[x] = Math.abs(m[x]);
    }
  }

  public static void main(String[] args) {
    int[][] a = new int[][] {
        {
        1, 2}, {
        1, 1}, {
        0, 2}, {
        0, 1}
    };
    System.out.println(IntArray.toString(a));
    sort(a);
    System.out.println("");
    System.out.println(IntArray.toString(a));
  }

  public final static void sort(int[][] twoDimensionArray) {
    if (intArrayComparator == null) {
      intArrayComparator = new IntArrayComparator();
    }
    Arrays.sort(twoDimensionArray, intArrayComparator);
  }

  private static IntArrayComparator intArrayComparator;
  private static class IntArrayComparator
      implements Comparator {
    /**
     * Compares its two arguments for order.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *   argument is less than, equal to, or greater than the second.
     */
    public int compare(Object o1, Object o2) {
      int[] array1 = (int[]) o1;
      int[] array2 = (int[]) o2;
      int width = array1.length;
      for (int x = 0; x < width; x++) {
        int result = array1[x] - array2[x];
        if (result == 0) {
          continue;
        }
        else {
          return result;
        }
      }
      return 0;
    }

    /**
     * Indicates whether some other object is &quot;equal to&quot; this
     * comparator.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> only if the specified object is also a
     *   comparator and it imposes the same ordering as this comparator.
     */
    public boolean equals(Object obj) {
      throw new UnsupportedOperationException();
    }

  }

  public final static int[] plus(int[] a, int[] b) {
    int size = a.length;
    int[] result = new int[size];
    for (int x = 0; x < size; x++) {
      result[x] = a[x] + b[x];
    }
    return result;
  }

  public final static int[] minus(int[] a, int[] b) {
    int size = a.length;
    int[] result = new int[size];
    for (int x = 0; x < size; x++) {
      result[x] = a[x] - b[x];
    }
    return result;
  }

  public final static int times(int[] a, int[] b) {
    if (a.length != b.length) {
      throw new IllegalArgumentException("a.length != b.length");
    }
    int size = a.length;
    int result = 0;
    for (int x = 0; x < size; x++) {
      result += a[x] * b[x];
    }
    return result;
  }

  public static double[][] toDoubleArray(int[][] intArray) {
    int m = intArray.length;
    int n = intArray[0].length;
    double[][] doubleArray = new double[m][n];
    for (int y = 0; y < m; y++) {
      for (int x = 0; x < n; x++) {
        doubleArray[y][x] = intArray[y][x];
      }
    }
    return doubleArray;
  }

  public static double[] toDoubleArray(int[] intArray) {
    int size = intArray.length;
    double[] doubleArray = new double[size];
    for (int x = 0; x < size; x++) {
      doubleArray[x] = intArray[x];
    }
    return doubleArray;
  }

  public static int[] buildX(int Xmin, int Xmax, int n) {
    if (Xmax < Xmin) {
      throw new IllegalArgumentException(
          "First argument must be less than second");
    }
    int[] X = new int[n];
    for (int i = 0; i < n; i++) {

      X[i] = Xmin +
          (int) Math.round( (Xmax - Xmin) * (double) i / (double) (n - 1));
    }
    return X;
  }

  public static int[][] diagonal(int ...c) {
    int[][] I = new int[c.length][c.length];
    for (int i = 0; i < I.length; i++) {
      I[i][i] = c[i];
    }
    return I;
  }

  public final static double[] toDoubleArray(short[] shortArray) {
    int size = shortArray.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = shortArray[x];
    }
    return result;
  }

  public final static double[] toDoubleArray(byte[] byteArray) {
    int size = byteArray.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = byteArray[x];
    }
    return result;
  }

//  public final static int[] times(int[] a, int value) {
//    int size = a.length;
//    int[] result = new int[size];
//    for (int x = 0; x < size; x++) {
//      result[x] = a[x] * value;
//    }
//    return result;
//  }
}
