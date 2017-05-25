package shu.math.lut;

import java.io.*;
import java.util.*;

import shu.math.*;
import shu.math.array.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * ���Ѥ����k�ɭȪ�1D��Ӫ�
 * ���FLINEAR, ���H�i��|�����઺�{�H, �ϥΤW�n�`�N.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class Interpolation1DLUT
    implements LookUpTable, Serializable {

  public static enum Algo {
    CUBIC_POLYNOMIAL(Interpolation.Algo.CubicPolynomial),
    LAGRANGE(Interpolation.Algo.Lagrange),
    SPLINE(Interpolation.Algo.Spline2),
    LINEAR(Interpolation.Algo.Linear),
    QUADRATIC_POLYNOMIAL(Interpolation.Algo.QuadraticPolynomial);

    private Interpolation.Algo algo;
    Algo(Interpolation.Algo algo) {
      this.algo = algo;
    }

  }

  public void setInterpolationType(Algo interpolationType) {
    this.interpolationType = interpolationType;
  }

  /**
   * �����ұĥΪ��t��k
   */
  private Algo interpolationType;
  private double[] keyArray;
  private double[] valueArray;

  public double[] getKeyArray() {
    return keyArray;
  }

  public double[] getValueArray() {
    return valueArray;
  }

//  private Interpolation interpValue;
//  private Interpolation interpKey;

  public Interpolation1DLUT(double[] keys, double[] values, Algo type) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("key.length != value.length");
    }
    this.keyArray = keys;
    this.valueArray = values;
    this.interpolationType = type;
//    interpKey = new Interpolation(values, keys);
//    interpValue = new Interpolation(keys, values);
  }

  public Interpolation1DLUT(double[] key, double[] value) {
    this(key, value, Algo.LINEAR);
  }

//  protected final double interpolationValue(double key, Algo type,
//                                            boolean ensureTrendMode) {
//    return interpValue.interpolate(key, type.algo);
//  }
//
//  protected final double interpolationKey(double key, Algo type,
//                                          boolean ensureTrendMode) {
//    return interpKey.interpolate(key, type.algo);
//  }

  /**
   *
   * @param key double
   * @param keys double[]
   * @param values double[]
   * @param type Algo
   * @param ensureTrendMode boolean �O�_�B��T�O�Ͷժ��Ҧ��U
   * @return double
   */
  protected final double interpolationValue(double key, double[] keys,
                                            double[] values, Algo type,
                                            boolean ensureTrendMode) {
    if (key == -key) {
      key = 0;
    }
    int index = Searcher.leftNearBinarySearch(keys, key);
    int interpoStart = 0;
    boolean head = false;
    boolean tail = false;

    //==========================================================================
    // ������m�I���P�_
    //==========================================================================
    if (index == 0) {
      interpoStart = index;
      head = true;
    }
    else if (index == (keys.length - 1)) {
      interpoStart = index - 3;
      tail = true;
    }
    else if (index == (keys.length - 2)) {
      interpoStart = index - 2;
      tail = true;
    }
    else if (index == -1) {
      throw new IndexOutOfBoundsException("key[" + key + "] out of keys[" +
                                          keys[0] + "~" + keys[keys.length - 1] +
                                          "]");
    }
    else {
      interpoStart = index - 1;
    }
    //==========================================================================
    double result = Double.NaN;
    if (keys.length == 3) {
      switch (type) {
        case LINEAR:
          double[] xn = new double[2];
          double[] yn = new double[2];
          if (tail) {
            xn = DoubleArray.getRangeCopy(keys, 1, 2);
            yn = DoubleArray.getRangeCopy(values, 1, 2);
          }
          else if (head) {
            xn = DoubleArray.getRangeCopy(keys, 0, 1);
            yn = DoubleArray.getRangeCopy(values, 0, 1);
          }

          result = Interpolation.interpolate(xn, yn, key,
                                             Interpolation.Algo.Linear);
          break;
      }
    }
    else {
      double[] xn = new double[4];
      double[] yn = new double[4];

      System.arraycopy(keys, interpoStart, xn, 0, 4);
      System.arraycopy(values, interpoStart, yn, 0, 4);

      switch (type) {
        case CUBIC_POLYNOMIAL:
          result = Interpolation.interpolate(xn, yn, key,
                                             Interpolation.Algo.CubicPolynomial);
          break;
        case QUADRATIC_POLYNOMIAL:

          //======================================================================
          // ��̱��񪺤T��keys&values
          //======================================================================
          if (!tail && !head) {
            double diff1 = Math.abs(xn[1] - key);
            double diff2 = Math.abs(xn[2] - key);
            if (diff1 < diff2) {
              head = true;
            }
            else {
              tail = true;
            }
          }

          //======================================================================
          if (tail) {
            xn = DoubleArray.getRangeCopy(xn, 1, 3);
            yn = DoubleArray.getRangeCopy(yn, 1, 3);
          }
          else if (head) {
            xn = DoubleArray.getRangeCopy(xn, 0, 2);
            yn = DoubleArray.getRangeCopy(yn, 0, 2);
          }
          result = Interpolation.interpolate(xn, yn, key,
                                             Interpolation.Algo.
                                             QuadraticPolynomial);
          break;
        case LAGRANGE:
          result = Interpolation.interpolate(xn, yn, key,
                                             Interpolation.Algo.Lagrange);
          break;
        case SPLINE:
          result = Interpolation.interpolate(xn, yn, key,
                                             Interpolation.Algo.Spline2);
          break;
        case LINEAR:
          if (tail) {
            xn = DoubleArray.getRangeCopy(xn, 2, 3);
            yn = DoubleArray.getRangeCopy(yn, 2, 3);
          }
          else if (head) {
            xn = DoubleArray.getRangeCopy(xn, 0, 1);
            yn = DoubleArray.getRangeCopy(yn, 0, 1);
          }
          else {
            xn = DoubleArray.getRangeCopy(xn, 1, 2);
            yn = DoubleArray.getRangeCopy(yn, 1, 2);
          }

          result = Interpolation.interpolate(xn, yn, key,
                                             Interpolation.Algo.Linear);
          break;
      }
      if (!ensureTrendMode) {
        //�b�D �T�O�ͶռҦ��U �~�ˬd�Ͷ�
        boolean trendCheck = Interpolation.trendCheck(xn, yn, key, result);
        if (!trendCheck && ensureTrend) {
          //�p�G�Ͷժ��ˬd���G����, �ӥB�S�O�b�n�T�O�Ͷժ����p�U, �N�n��H�u�ʤ���
          return interpolationValue(key, keys, values, Algo.LINEAR, true);
        }
      }
    }

    return result;
  }

  ;
  /**
   * �O�_�n�T�O�@�w�n�����Ͷ�.
   * �p�G�n����, ��o�ͤ��ŦX�Ͷծ�, �|��νu�ʤ���.
   */
  private boolean ensureTrend = true;

  /**
   * �O�_�n�T�O�Ͷե��T
   * @param ensure boolean
   */
  public void setEnsureTrend(boolean ensure) {
    this.ensureTrend = ensure;
  }

  public static void example(String[] args) {
    //�ǳƦn�n�@����Ӫ�keys
    double[] xn = new double[] {
        0, .1, .2, .3, .4};

    //�H�@��Gamma�Y�ƭp��᪺��,�@����Ӫ�values
    double[] yn = new double[5];
    double gamma = 2;
    yn = GammaFinder.gammaCurve(xn, gamma);
    System.out.println(Arrays.toString(yn));

    //��l�ƹ�Ӫ�
    Interpolation1DLUT lut = new Interpolation1DLUT(xn, yn);
    //��key���s�b��keys,�ҥH�H�����k�p��
    double key = .25;
    //�U�ؤ��P�����k�ұo��values
    double cubicVal = lut.getValue(key,
                                   Interpolation1DLUT.Algo.CUBIC_POLYNOMIAL);
//    double gammaVal = lut.getValue(key, InterpolationLUT.TYPE.GAMMA);
    double lagVal = lut.getValue(key, Interpolation1DLUT.Algo.LAGRANGE);
    double splineVal = lut.getValue(key, Interpolation1DLUT.Algo.SPLINE);

    System.out.println(cubicVal);
//    System.out.println(gammaVal);
    System.out.println(lagVal);
    System.out.println(splineVal);

    //�N��Ӫ�����value,�A�H��Ӫ����^key,�Ψ����Ҥ��P�����k���ǽT��
    System.out.println(lut.getKey(cubicVal,
                                  Interpolation1DLUT.Algo.CUBIC_POLYNOMIAL));
//    System.out.println(lut.getKey(gammaVal, InterpolationLUT.TYPE.GAMMA));
    System.out.println(lut.getKey(lagVal, Interpolation1DLUT.Algo.LAGRANGE));
    System.out.println(lut.getKey(splineVal, Interpolation1DLUT.Algo.SPLINE));
  }

  public static void main(String[] args) {

    double[] value = new double[] {
        0.58, 0.63, 0.68, 0.689, 0.691, 0.74};
    double[] key = new double[] {
        70.0, 75.0, 85.0, 95.0, 98.0, 100.0};

    Interpolation1DLUT lut2 = new Interpolation1DLUT(key, value);
    for (Algo algo : Algo.values()) {
      System.out.println(lut2.getValue(69.9, algo));
    }

//    System.out.println(lut2.getValue(69.9));
  }

  public double[] getValues(double[] keys) {
    double result = getValue(keys[0]);
    return new double[] {
        result};
  }

  public double getValue(double key) {
    return getValue(key, interpolationType);
  }

  public double getValue(double key, Algo type) {
    int index = Arrays.binarySearch(keyArray, key);
    if (index >= 0) {
      return valueArray[index];
    }
    else {
      return interpolationValue(key, keyArray, valueArray, type, false);
    }
  }

  public double[] getKeys(double[] values) {
    double result = getKey(values[0]);
    return new double[] {
        result};
  }

  public double getKey(double value) {
    return getKey(value, interpolationType);
  }

  /**
   * ��value�ץ��b�d��,�קK�䤣��Ȫ����p�o��
   * @param value double
   * @return double
   */
  public double correctValueInRange(double value) {
    hasCorrectedInRange = false;
    if (value < valueArray[0]) {
      hasCorrectedInRange = true;
      return valueArray[0];
    }
    else if (value > valueArray[valueArray.length - 1]) {
      hasCorrectedInRange = true;
      return valueArray[valueArray.length - 1];
    }
    return value;
  }

  public boolean isValueInRange(double value) {
    if (value < valueArray[0] || value > valueArray[valueArray.length - 1]) {
      return false;
    }
    return true;

  }

  public boolean hasCorrectedInRange() {
    return hasCorrectedInRange;
  }

  /**
   * �O���O�_���F�ץ��b�d�򤺪��ʧ@
   */
  private boolean hasCorrectedInRange;

  /**
   * ��key�ץ��b�d��,�קK�䤣��Ȫ����p�o��
   * @param key double
   * @return double
   */
  public double correctKeyInRange(double key) {
    hasCorrectedInRange = false;
    if (key < keyArray[0]) {
      hasCorrectedInRange = true;
      return keyArray[0];
    }
    else if (key > keyArray[keyArray.length - 1]) {
      hasCorrectedInRange = true;
      return keyArray[keyArray.length - 1];
    }
    return key;
  }

  public boolean isKeyInRange(double key) {
    if (key < keyArray[0] || key > keyArray[keyArray.length - 1]) {
      return false;
    }
    return true;
  }

  public double getKey(double value, Algo type) {
    int index = Arrays.binarySearch(valueArray, value);
    if (index >= 0) {
      return keyArray[index];
    }
    else {
      return interpolationValue(value, valueArray, keyArray, type, false);
    }
  }

  /**
   * ���ͤ�����Ӫ�
   * @param rgbInput double[][]
   * @param rgbOutput double[][]
   * @return Interpolation1DLUT[]
   */
  public final static Interpolation1DLUT[] getRGBInterpLUT(double[][]
      rgbInput, double[][] rgbOutput) {

    Interpolation1DLUT[] lutArray = new Interpolation1DLUT[3];
    for (int x = 0; x < 3; x++) {
      lutArray[x] = new Interpolation1DLUT(rgbInput[x], rgbOutput[x],
                                           Interpolation1DLUT.Algo.LINEAR);
    }
    return lutArray;
  }
}
