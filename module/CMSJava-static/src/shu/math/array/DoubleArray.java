package shu.math.array;

import java.text.*;
import java.util.*;

import Jama.*;

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
public abstract class DoubleArray
    extends org.math.array.LinearAlgebra {

  public final static double[] parseDoubleArray(String str) {
    String trim = str.trim();
    StringTokenizer tokenizer = new StringTokenizer(trim);
    int tokens = tokenizer.countTokens();
    double[] array = new double[tokens];
    for (int x = 0; x < tokens; x++) {
      String token = tokenizer.nextToken();
      double d = Double.parseDouble(token);
      array[x] = d;
    }

    return array;
  }

  public static void main(String[] args) {
    String m = " 3.2404542 -1.5371385 -0.4985314 -0.9692660  1.8760108  0.0415560     0.0556434 -0.2040259  1.0572252";
    double[] mm = parseDoubleArray(m);
    System.out.println(toString(mm));
//    DoubleArray.times(null,1);
  }

  public static void timesAndNoReturn(double[] v1, double v) {
    for (int i = 0; i < v1.length; i++) {
      v1[i] = v1[i] * v;
    }
  }

  public final static void sort(double[][] twoDimensionArray) {
    if (doubleArrayComparator == null) {
      doubleArrayComparator = new DoubleArrayComparator();
    }
    Arrays.sort(twoDimensionArray, doubleArrayComparator);
  }

  public final static int compare(double[] array1, double[] array2) {
    if (doubleArrayComparator == null) {
      doubleArrayComparator = new DoubleArrayComparator();
    }
    return doubleArrayComparator.compare(array1, array2);
  }

  public final static DoubleArrayComparator getDoubleArrayComparatorInstance() {
    if (doubleArrayComparator == null) {
      doubleArrayComparator = new DoubleArrayComparator();
    }

    return doubleArrayComparator;
  }

  private static DoubleArrayComparator doubleArrayComparator;
  private static class DoubleArrayComparator
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
      double[] array1 = (double[]) o1;
      double[] array2 = (double[]) o2;
      int width = array1.length;
      for (int x = 0; x < width; x++) {
        int result = Double.compare(array1[x], array2[x]);
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

  public final static String toString(DecimalFormat df,
                                      double[] ...v) {
    StringBuffer str = new StringBuffer();
    for (int i = 0; i < v.length; i++) {
      for (int j = 0; j < v[i].length; j++) {
        if (j == v[i].length - 1) {
          str.append(df.format(v[i][j]));
        }
        else {
          str.append(df.format(v[i][j]) + " ");
        }
      }
      if (i < v.length - 1) {
        str.append("\n");
      }
    }
    return str.toString();
  }

  /**
   * 轉置後為正方形矩陣
   * @param a double[]
   * @return double[][]
   */
  public final static double[][] transposeSquare(double[] a) {
    int am = a.length;
    int an = a.length;
    double[][] result = new double[an][am];
    for (int j = 0; j < an; j++) {
      result[j][0] = a[j];
    }
    return result;
  }

  public final static double[][] transpose(double[] a) {
    int an = a.length;
    double[][] result = new double[an][1];
    for (int j = 0; j < an; j++) {
      result[j][0] = a[j];
    }
    return result;
  }

  public final static double[][] to2DDoubleArray(double[] doubleArray,
                                                 int width) {
    int height = doubleArray.length / width;
    double[][] result = new double[height][width];
    for (int x = 0; x < height; x++) {
      System.arraycopy(doubleArray, x * width, result[x], 0, width);
    }
    return result;
  }

  public final static double[][][] to3DDoubleArray(double[][] doubleArray,
      int m, int n) {
    double[][][] doubleArray3D = new double[m][n][];
    int index = 0;
    for (int x = 0; x < m; x++) {
      for (int y = 0; y < n; y++) {
        doubleArray3D[x][y] = doubleArray[index++];
      }
    }
    return doubleArray3D;
  }

  public final static double[] to1DDoubleArray(double[][] doubleArray) {
    int mSize = doubleArray.length;
    int nSize = doubleArray[0].length;
    double[] result = new double[mSize * nSize];
    int index = 0;
    for (int x = 0; x < mSize; x++) {
      for (int y = 0; y < nSize; y++) {
        result[index++] = doubleArray[x][y];
      }
    }
    return result;
  }

  public final static double[][] diagonal(double[][] array, double ...c) {
    for (int i = 0; i < c.length; i++) {
      array[i][i] = c[i];
    }
    return array;
  }

  public final static String dimension(double[][] array) {
    return array.length + "x" + array[0].length;
  }

  /**
   * 取倒數
   * @param a double[]
   * @return double[]
   */
  public final static double[] reciprocal(double[] a) {
    int size = a.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = 1. / a[x];
    }
    return result;
  }

  public final static double times(double[] a, double[] b) {
    if (a.length != b.length) {
      throw new IllegalArgumentException("a.length != b.length");
    }
    int size = a.length;
    double result = 0;
    for (int x = 0; x < size; x++) {
      result += a[x] * b[x];
    }
    return result;
  }

  public final static double[] weighting(double[] m, double[] weighting) {
    if (m.length != weighting.length) {
      throw new IllegalArgumentException("m.length != weighting.length");
    }
    int size = m.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = m[x] * weighting[x];
    }
    return result;
  }

  /**
   * 取餘數
   * @param a double[]
   * @param b double[]
   * @return double[]
   */
  public final static double[] modulus(double[] a, double[] b) {
    int size = a.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = a[x] % b[x];
    }
    return result;
  }

  public final static int[] plus(int[] a, int value) {
    int size = a.length;
    int[] result = new int[size];
    for (int x = 0; x < size; x++) {
      result[x] = a[x] + value;
    }
    return result;
  }

  public final static double[] plus(double[] a, double[] b) {
    int size = a.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = a[x] + b[x];
    }
    return result;
  }

  /**
   * 提供 3x3 * 3x1 的矩陣相乘快速運算
   * @param a double[][]
   * @param b double[]
   * @return double[]
   */
  public final static double[] timesFast(double[][] a, double[] b) {
    if (b.length != a.length || a.length != 3) {
      throw new IllegalArgumentException(
          "inner dimensions must 3.");
    }

    double[] result = new double[3];
    result[0] = a[0][0] * b[0] + a[0][1] * b[1] + a[0][2] * b[2];
    result[1] = a[1][0] * b[0] + a[1][1] * b[1] + a[1][2] * b[2];
    result[2] = a[2][0] * b[0] + a[2][1] * b[1] + a[2][2] * b[2];

    return result;
  }

  /**
   * 提供 3x3 * 3x1 的矩陣相乘運算
   * @param a double[][]
   * @param b double[]
   * @return double[]
   */
  public final static double[] times(double[][] a, double[] b) {
    return timesFast(a, b);
  }

  public static double[][] times(double[][] v1, double[][] v2) {
//    checkRowDimension(v2, v1.length);
//    checkColumnDimension(v2, v1[0].length);
//    double[][] array = new double[v1.length][v2[0].length];
//    for (int i = 0; i < array.length; i++) {
//      for (int j = 0; j < array[i].length; j++) {
//        double tmp = 0;
//        for (int k = 0; k < v1[0].length; k++) {
//          tmp += v1[i][k] * v2[k][j];
//        }
//        array[i][j] = tmp;
//      }
//    }
//    return array;
    return new Matrix(v1).times(new Matrix(v2)).getArray();
  }

  /**
   * 提供 1x3 * 3x3 的矩陣相乘運算
   * @param a double[]
   * @param b double[][]
   * @return double[]
   */
  public final static double[] times(double[] a, double[][] b) {
    return timesFast(a, b);
  }

  /**
   * 提供 1x3 * 3x3 的矩陣相乘快速運算
   * 不使用迴圈,可以減少運算的時間及空間
   * @param a double[]
   * @param b double[][]
   * @return double[]
   */
  public final static double[] timesFast(double[] a, double[][] b) {
    if (b.length != a.length || a.length != 3) {
      throw new IllegalArgumentException(
          "inner dimensions must 3.");
    }

    double[] result = new double[3];
    result[0] = a[0] * b[0][0] + a[1] * b[1][0] + a[2] * b[2][0];
    result[1] = a[0] * b[0][1] + a[1] * b[1][1] + a[2] * b[2][1];
    result[2] = a[0] * b[0][2] + a[1] * b[1][2] + a[2] * b[2][2];

    return result;
  }

  public final static boolean isFullRank(double[][] a) {
    QRDecomposition qr = org.math.array.LinearAlgebra.QR(a);
    return qr.isFullRank();
  }

  public final static boolean isNonsingular(double[][] a) {
    LUDecomposition lu = org.math.array.LinearAlgebra.LU(a);
    return lu.isNonsingular();
  }

  public final static double[][] mergeRows(double[][] array1,
                                           double[][] array2) {
    if (array1[0].length != array2[0].length) {
      throw new IllegalArgumentException(
          "array1[0].length != array2[0].length");
    }

    int height = array1.length + array2.length;
    int width = array1[0].length;
    double[][] merge = new double[height][width];

    for (int x = 0; x < array1.length; x++) {
      System.arraycopy(array1[x], 0, merge[x], 0, width);
    }

    for (int x = 0; x < array2.length; x++) {
      System.arraycopy(array2[x], 0, merge[x + array1.length], 0, width);
    }

    return merge;
  }

  public static void copy(double[] source, double[] destination) {
    if (source == null || destination == null ||
        source.length != destination.length) {
      throw new IllegalArgumentException("");
    }
    System.arraycopy(source, 0, destination, 0, source.length);
  }

  public static void abs(double[] m) {
    int size = m.length;
    for (int x = 0; x < size; x++) {
      m[x] = Math.abs(m[x]);
    }
  }

  public static void abs(double[][] m) {
    int h = m.length;
    int w = m[0].length;
    for (int x = 0; x < h; x++) {
      for (int y = 0; y < w; y++) {
        m[x][y] = Math.abs(m[x][y]);
      }
    }
  }

  public final static boolean hasNegative(double[] m) {
    int size = m.length;
    for (int x = 0; x < size; x++) {
      if (m[x] < 0) {
        return true;
      }
    }
    return false;
  }

  public static float[] toFloatArray(double[] array) {
    int size = array.length;
    float[] result = new float[size];
    for (int x = 0; x < size; x++) {
      result[x] = (float) array[x];
    }
    return result;
  }

  /**
   *
   * @param array double[]
   * @return int[]
   */
  public static int[] toIntArray(double[] array) {
    int size = array.length;
    int[] result = new int[size];
    for (int x = 0; x < size; x++) {
      result[x] = (int) Math.floor(array[x]);
    }
    return result;
  }

  public static int[] toIntArray(double[] doubleArray, int[] intArray) {
    int size = doubleArray.length;
//  int[] result = new int[size];
    for (int x = 0; x < size; x++) {
      intArray[x] = (int) Math.floor(doubleArray[x]);
    }
    return intArray;
  }

  public static int[][] toIntArray(double[][] array) {
    int size = array.length;
    int[][] result = new int[size][];
    for (int x = 0; x < size; x++) {
//    result[x] = (int) Math.floor(array[x]);
      result[x] = toIntArray(array[x]);
    }
    return result;
  }

  /**
   * 對array的作累積相加計算
   * @param array double[]
   * @return double[]
   */
  public final static double[] accumulate(double[] array) {
    double[] accumulate = DoubleArray.copy(array);
    int size = accumulate.length;
    for (int x = 1; x < size; x++) {
      accumulate[x] = accumulate[x] + accumulate[x - 1];
    }
    return accumulate;
  }

  public static double[] buildX(double Xmin, double Xmax, int n) {
    if (Xmax < Xmin) {
      throw new IllegalArgumentException(
          "First argument must be less than second");
    }
    double[] X = new double[n];
    for (int i = 0; i < n; i++) {
      X[i] = Xmin + (Xmax - Xmin) * (double) i / (double) (n - 1);
    }
    return X;
  }

  public static double[][] list2DoubleArray(List < double[] > list) {
    int size = list.size();
    double[][] doubleArray = new double[size][];
    for (int x = 0; x < size; x++) {
      doubleArray[x] = list.get(x);
    }
    return doubleArray;
  }

  public static double[] list2DoubleArray(List<Double> list) {
    int size = list.size();
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = list.get(x);
    }
    return result;
  }

  public final static double divide2(double[] a, double[] b) {
    if (a.length != b.length) {
      throw new IllegalArgumentException("a.length != b.length");
    }
    int size = a.length;
    double result = 0;
    for (int x = 0; x < size; x++) {
      result += a[x] / b[x];
    }
    return result;
  }

  /**
   *
   * @param a double[][]
   * @return double[][]
   */
  public final static double[][] pseudoInverse(double[][] a) {
    if (!isFullRank(a)) {
      double[][] t = DoubleArray.transpose(a);
      return DoubleArray.transpose(inverse(t));
    }
    else {
      return inverse(a);
    }
  }

  public final static double[] fromByteArray(byte[] byteArray) {
    int size = byteArray.length;
    double[] doubleArray = new double[size];
    for (int x = 0; x < size; x++) {
      doubleArray[x] = byteArray[x];
    }
    return doubleArray;
  }

  public final static double[] fromShortArray(short[] shortArray) {
    int size = shortArray.length;
    double[] doubleArray = new double[size];
    for (int x = 0; x < size; x++) {
      doubleArray[x] = shortArray[x];
    }
    return doubleArray;
  }

  /**
   * Subtracts a scalar value from each element of an array
   * @param v1 Minuend Array.
   * @param v Subtrahend scalar
   */
  public static void minusAndNoReturn(double[] v1, double v) {
    for (int i = 0; i < v1.length; i++) {
      v1[i] -= v;
    }
  }

  /**
   * Add a scalar value to each element of an array.
   * @param v1 Array
   * @param v Scalar
   */
  public static void plusAndNoReturn(double[] v1, double v) {
    for (int i = 0; i < v1.length; i++) {
      v1[i] += v;
    }
  }
}
