package shu.math;

import java.util.*;
import javax.vecmath.*;

import org.python.core.*;
import org.python.modules.*;
import flanagan.analysis.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 簡單的運算集合類別,部分套用 Michael Thomas Flanagan's Java Library
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class Maths {
  /**
   * 計算平均
   * @param values double[]
   * @return double
   */
  public final static double mean(double[] values) {
    return Stat.mean(values);
  }

  public final static double mean(double[][] values) {
    int size = values.length;
    double[] mean = new double[size];
    for (int x = 0; x < size; x++) {
      mean[x] = mean(values[x]);
    }
    return mean(mean);
  }

  /**
   * 計算平方
   * @param v double
   * @return double
   */
  public final static double sqr(double v) {
    return v * v;
  }

  public final static double sqrt(double v) {
    return Math.sqrt(v);
  }

  public final static double[] sqr(double[] v) {
    int size = v.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = v[x] * v[x];
    }
    return result;
  }

  /**
   * 計算標準差
   * @param values double[]
   * @return double
   */
  public final static double std(double[] values) {
    return Stat.standardDeviation(values);
  }

  /**
   * 找出最大值
   * @param values double[]
   * @return double
   */
  public final static double max(double[] values) {
    double max = Double.NEGATIVE_INFINITY;
    for (double d : values) {
      max = Math.max(max, d);
    }
    return max;
  }

  public final static double max(double[][] values) {
    int size = values.length;
    double[] max = new double[size];
    for (int x = 0; x < size; x++) {
      max[x] = max(values[x]);
    }
    return max(max);
  }

  public final static int[] maxIndex(double[][] values) {
    int size = values.length;
    int[] maxIndex = new int[size];
    double[] maxValues = new double[size];
    for (int x = 0; x < size; x++) {
      maxIndex[x] = maxIndex(values[x]);
      maxValues[x] = values[x][maxIndex[x]];
    }
    int maxIndexY = maxIndex(maxValues);
    return new int[] {
        maxIndexY, maxIndex[maxIndexY]};
  }

  public final static int max(int[] values) {
    int max = 0;
    for (int d : values) {
      max = Math.max(max, d);
    }
    return max;
  }

  public final static short max(short[] values) {
    int max = 0;
    for (int d : values) {
      max = Math.max(max, d);
    }
    return (short) max;
  }

  public final static int[] max2Index(double[] values) {
    if (values.length < 2) {
      throw new IllegalArgumentException("values.length < 2");
    }
    int[] indexs = new int[2];
    double[] max2 = new double[] {
        Double.MIN_VALUE, Double.MIN_VALUE};

    for (int x = 0; x < values.length; x++) {
      double v = values[x];
      if (v >= max2[0]) {
        max2[1] = max2[0];
        max2[0] = v;

        indexs[1] = indexs[0];
        indexs[0] = x;
      }
      else if (v >= max2[1]) {
        max2[1] = v;

        indexs[1] = x;
      }
    }

    return indexs;
  }

  public final static int[] min2Index(double[] values) {
    if (values.length < 2) {
      throw new IllegalArgumentException("values.length < 2");
    }
    int[] indexs = new int[2];
    double[] min2 = new double[] {
        Double.MAX_VALUE, Double.MAX_VALUE};

    for (int x = 0; x < values.length; x++) {
      double v = values[x];
      if (v <= min2[0]) {
        min2[1] = min2[0];
        min2[0] = v;

        indexs[1] = indexs[0];
        indexs[0] = x;
      }
      else if (v <= min2[1]) {
        min2[1] = v;

        indexs[1] = x;
      }
    }

    return indexs;
  }

  public final static int[] min3Index(double[] values) {
    if (values.length < 3) {
      throw new IllegalArgumentException("values.length < 3");
    }
    int[] indexs = new int[3];
    double[] min3 = new double[] {
        Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};

    for (int x = 0; x < values.length; x++) {
      double v = values[x];
      if (v <= min3[0]) {
        min3[2] = min3[1];
        min3[1] = min3[0];
        min3[0] = v;

        indexs[2] = indexs[1];
        indexs[1] = indexs[0];
        indexs[0] = x;
      }
      else if (v <= min3[1]) {
        min3[2] = min3[1];
        min3[1] = v;

        indexs[2] = indexs[1];
        indexs[1] = x;
      }
      else if (v <= min3[2]) {
        min3[2] = v;

        indexs[2] = x;
      }
    }

    return indexs;
  }

  public final static int[] min4Index(double[] values) {
    if (values.length < 4) {
      throw new IllegalArgumentException("values.length < 4");
    }
    int[] indices = new int[4];
    double[] min4 = new double[] {
        Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};

    for (int x = 0; x < values.length; x++) {
      double v = values[x];
      if (v <= min4[0]) {
        min4[3] = min4[2];
        min4[2] = min4[1];
        min4[1] = min4[0];
        min4[0] = v;

        indices[3] = indices[2];
        indices[2] = indices[1];
        indices[1] = indices[0];
        indices[0] = x;
      }
      else if (v <= min4[1]) {
        min4[3] = min4[2];
        min4[2] = min4[1];
        min4[1] = v;

        indices[3] = indices[2];
        indices[2] = indices[1];
        indices[1] = x;
      }
      else if (v <= min4[2]) {
        min4[3] = min4[2];
        min4[2] = v;

        indices[3] = indices[2];
        indices[2] = x;
      }
      else if (v <= min4[3]) {
        min4[3] = v;

        indices[3] = x;
      }

    }

    return indices;
  }

  public final static int[] max3Index(double[] values) {
    if (values.length < 3) {
      throw new IllegalArgumentException("values.length < 3");
    }
    int[] indexs = new int[3];
    double[] max3 = new double[] {
        Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};

    for (int x = 0; x < values.length; x++) {
      double v = values[x];
      if (v >= max3[0]) {
        max3[2] = max3[1];
        max3[1] = max3[0];
        max3[0] = v;

        indexs[2] = indexs[1];
        indexs[1] = indexs[0];
        indexs[0] = x;
      }
      else if (v >= max3[1]) {
        max3[2] = max3[1];
        max3[1] = v;

        indexs[2] = indexs[1];
        indexs[1] = x;
      }
      else if (v >= max3[2]) {
        max3[2] = v;

        indexs[2] = x;
      }
    }

    return indexs;
  }

  /**
   * 會變動到values
   * @param values double[]
   * @return double[]
   */
  public final static double[] max3(double[] values) {
    Arrays.sort(values);
    double[] result = new double[3];
    for (int x = 0; x < 3; x++) {
      result[x] = values[values.length - 1 - x];
    }
    return result;
  }

  /**
   *
   * @param values double[]
   * @param a boolean
   * @return double[]
   * @deprecated
   */
  public final static double[] sort(double[] values, boolean a) {
    Arrays.sort(values);
    if (a) {
      return values;
    }
    int size = values.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = values[values.length - 1 - x];
    }
    return result;
  }

  public final static int maxIndex(double[] values) {
    double max = Double.NEGATIVE_INFINITY;
    int maxIndex = -1;
    for (int x = 0; x < values.length; x++) {
      if (values[x] > max) {
        max = values[x];
        maxIndex = x;
      }
    }
    return maxIndex;
  }

  public final static int maxIndex(int[] values) {
    int max = Integer.MIN_VALUE;
    int maxIndex = -1;
    for (int x = 0; x < values.length; x++) {
      if (values[x] > max) {
        max = values[x];
        maxIndex = x;
      }
    }
    return maxIndex;
  }

  public final static int minIndex(double[] values) {
    double min = Double.POSITIVE_INFINITY;
    int minIndex = -1;
    for (int x = 0; x < values.length; x++) {
      if (values[x] < min) {
        min = values[x];
        minIndex = x;
      }
    }
    return minIndex;
  }

  public final static double min(double[] values) {
    double min = Double.POSITIVE_INFINITY;
    for (double d : values) {
      min = Math.min(min, d);
    }
    return min;
  }

  public final static int min(int[] values) {
    int min = Integer.MAX_VALUE;
    for (int d : values) {
      min = Math.min(min, d);
    }
    return min;
  }

  public final static short min(short[] values) {
    int min = Short.MAX_VALUE;
    for (int d : values) {
      min = Math.min(min, d);
    }
    return (short) min;
  }

  public static void main(String[] args) {
    double[] a = {
        1, 3};
    double[] b = {
        1.3, 3.1};
    System.out.println(delta(a, b));
//    System.out.println(distance(a, b));
  }

  /**
   * original每個元素各,自與normal中相對應index的元素進行正規化
   * @param original double[]
   * @param normal double[]
   * @return double[]
   */
  public final static void normalize(double[] original, double[] normal) {
    int size = original.length;
    for (int x = 0; x < size; x++) {
      original[x] /= normal[x];
    }
//    return original;
  }

  public final static void undoNormalize(double[] original, double[] normal) {
    int size = original.length;
    for (int x = 0; x < size; x++) {
      original[x] *= normal[x];
    }
//    return original;
  }

  public final static void normalizeKeepMinimum(final double[] original,
                                                final double normal) {
    double min = original[0];
    double newnormal = normal - min;
    DoubleArray.minusAndNoReturn(original, min);
    normalize(original, original[original.length - 1]);
    DoubleArray.timesAndNoReturn(original, newnormal);
    DoubleArray.plusAndNoReturn(original, min);

  }

  public final static void normalize(final double[] original,
                                     final double normal) {
    for (int x = 0; x < original.length; x++) {
      original[x] /= normal;
    }
  }

  public final static void normalize(final double[] original) {
    double base = original[0];
    int size = original.length;
    double normal = original[size - 1] - base;
    for (int x = 0; x < original.length; x++) {
      original[x] = (original[x] - base) / normal;
    }
//    return original;
  }

  public final static void undoNormalize(double[] original, double normal) {
    for (int x = 0; x < original.length; x++) {
      original[x] *= normal;
    }
//    return original;
  }

  public final static double sum(double[] values) {
    double sum = 0.0;
    for (double d : values) {
      sum += d;
    }
    return sum;
  }

  public final static int sum(int[] values) {
    int sum = 0;
    for (int d : values) {
      sum += d;
    }
    return sum;
  }

  /**
   * more precisse than return pow(t, 1.0/3.0);
   * @param x double
   * @return double
   */
  public final static double cubeRoot(double x) {
//    if (true) {
//      return Math.pow(x, 1. / 3.);
//    }

    double fr, r;
    int ex, shx;

    // Argument reduction
    PyTuple p = math.frexp(x); // separate into mantissa and exponent
    fr = (Double) p.get(0);
    ex = (Integer) p.get(1);
    shx = ex % 3;

    if (shx > 0) {
      shx -= 3; // compute shx such that (ex - shx) is divisible by 3
    }

    ex = (ex - shx) / 3; // exponent of cube root
    fr = math.ldexp(fr, shx);

    // 0.125 <= fr < 1.0

    // Use quartic rational polynomial with error < 2^(-24)

    fr = ( ( ( ( (45.2548339756803022511987494 * fr +
                  192.2798368355061050458134625) * fr +
                119.1654824285581628956914143) * fr +
              13.43250139086239872172837314) * fr +
            0.1636161226585754240958355063)
          /
          ( ( ( (14.80884093219134573786480845 * fr +
                 151.9714051044435648658557668) * fr +
               168.5254414101568283957668343) * fr +
             33.9905941350215598754191872) * fr +
           1.0));
    r = math.ldexp(fr, ex); // 24 bits of precision
    return r;
  }

  /**
   * 評估goodness-fitting coefficient(GFC)
   * GFC >=  0.99: acceptable
   * GFC >= 0.999: very good
   * GFC >=0.9999: almost exact
   * @param data1 double[]
   * @param data2 double[]
   * @return double
   */
  public final static double gfc(double[] data1, double[] data2) {
    if (data1.length != data2.length) {
      return -1;
    }
    double gfc = Math.abs(DoubleArray.times(data1, data2)) /
        (Math.sqrt(Math.abs(DoubleArray.times(data1, data1))) *
         Math.sqrt(Math.abs(DoubleArray.times(data2, data2))));

    return gfc;
  }

  public final static double rSquare(double[] observedData,
                                     double[] predictedData) {
    if (observedData.length != predictedData.length) {
      throw new IllegalArgumentException(
          "observedData.length != predictedData.length");
    }
    double observedMean = Maths.mean(observedData);
    double err = 0.0;
    double tot = 0.0;
    for (int x = 0; x < observedData.length; x++) {
      err += Maths.sqr(observedData[x] - predictedData[x]);
      tot += Maths.sqr(observedData[x] - observedMean);
    }
    return (1. - err / tot);
  }

  public final static double rSquare(double[][] observedData,
                                     double[][] predictedData) {
    if (observedData.length != predictedData.length ||
        observedData[0].length != predictedData[0].length) {
      throw new IllegalArgumentException(
          "observedData.length != predictedData.length || observedData[0].length != predictedData[0].length");
    }
    return rSquare(DoubleArray.to1DDoubleArray(observedData),
                   DoubleArray.to1DDoubleArray(predictedData));
  }

  /**
   *
   * @param data1 double[][]
   * @param data2 double[][]
   * @return double
   */
  public final static double RMSD(double[][] data1, double[][] data2) {
    if (data1.length != data2.length || data1[0].length != data2[0].length) {
      throw new IllegalArgumentException(
          "data1.length != data2.length || data1[0].length != data2[0].length");
    }
    double rmsd = 0.0;
    int dataSize = data1[0].length;
    for (int x = 0; x < data1.length; x++) {
      double dist = 0.0;
      for (int y = 0; y < dataSize; y++) {
        double d = data1[x][y] - data2[x][y];
        dist += Maths.sqr(d);
      }
      rmsd += Math.sqrt(dist / dataSize);
    }
    return rmsd / data1.length;
  }

  public final static double delta(double[] difference) {
    double delta = 0;
    for (double d : difference) {
      delta += sqr(d);
    }
    delta = Math.sqrt(delta);
    return delta;
  }

  public final static double delta(double[] data1, double[] data2) {
    int size = data1.length;
    if (size != data2.length) {
      throw new IllegalArgumentException("data1.length != data2.length");
    }
    double delta = 0;
    for (int x = 0; x < size; x++) {
      delta += Maths.sqr(data1[x] - data2[x]);
    }
    delta = Math.sqrt(delta);
    return delta;
  }

  public final static double RMSD(double[] data1, double[] data2) {
    return RMSD(new double[][] {data1}, new double[][] {data2});
  }

  public final static double[] RMSD(double[][] dataArray, double[] data) {
    int size = dataArray.length;
    double[] rmsArray = new double[size];
    for (int x = 0; x < size; x++) {
      rmsArray[x] = RMSD(dataArray[x], data);
    }
    return rmsArray;
  }

  public final static int[] floor(double[] dArray) {
    int size = dArray.length;
    int[] iAraray = new int[size];
    for (int x = 0; x < size; x++) {
      iAraray[x] = (int) Math.floor(dArray[x]);
    }
    return iAraray;
  }

  /**
   * 一次微分
   * @param data double[]
   * @return double[]
   */
  public final static double[] firstOrderDerivatives(double[] data) {
    int size = data.length;
    double[] derivatives = new double[size - 1];
    for (int x = 1; x < size; x++) {
      derivatives[x - 1] = data[x] - data[x - 1];
    }
    return derivatives;
  }

  public final static double angle(double vectorX1, double vectorY1,
                                   double vectorX2, double vectorY2) {
//    Vector2D v1 = new Vector2D(vectorX1, vectorY1);
//    Vector2D v2 = new Vector2D(vectorX2, vectorY2);

    Vector2d v1 = new Vector2d(vectorX1, vectorY1);
    Vector2d v2 = new Vector2d(vectorX2, vectorY2);
    return v1.angle(v2);

//    return Vector2D.angle(v1, v2);
  }

  private Maths() {

  }
}
