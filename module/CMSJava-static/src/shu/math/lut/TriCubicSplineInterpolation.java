package shu.math.lut;

import java.io.*;

import flanagan.interpolation.*;
import shu.math.lut.CubeTable.KeyValue;

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
public class TriCubicSplineInterpolation
    extends Asymmetric3DLUT {

  protected TriCubicSpline[] triCubicSplineArray;
  protected int valueCounts;

  public TriCubicSplineInterpolation(CubeTable keyValuePairs) {
    super(keyValuePairs);
//    this.keyValuePairs = keyValuePairs;
    this.valueCounts = keyValuePairs.getValueCounts();
    this.triCubicSplineArray = new TriCubicSpline[valueCounts];
    //做Interpolation3DIF的註冊動作
    registerInterpolation3DIF(new Interpolation3D());
    initTriCubicSpline();
  }

  protected void initTriCubicSpline() {
//    double[][] stairKeys = keyValuePairs.getStairKeys();
//    keyValuePairs.getAxisKeys()
    double[][] stairKeys = new double[][] {
        keyValuePairs.getAxisKeys(0), keyValuePairs.getAxisKeys(1),
        keyValuePairs.getAxisKeys(2)};
    double[][][][] lut = keyValuePairs.getFlanaganLUT();
    for (int x = 0; x < valueCounts; x++) {
      triCubicSplineArray[x] = new TriCubicSpline(stairKeys[0], stairKeys[1],
                                                  stairKeys[2], lut[x]);
    }
    keyValuePairs = null;
    System.gc();
  }

  protected class Interpolation3D
      implements Interpolation3DIF, Serializable {
    public double[] interpolateKey(double[] value, double[][][] cell) {
      throw new UnsupportedOperationException();
    }

    /**
     * 從key和內插用的cell,內插出值
     * @param key double[]
     * @param cell double[][][]
     * @return double[]
     */
    public double[] interpolateValue(double[] key, double[][][] cell) {
      double[] values = new double[valueCounts];
      for (int x = 0; x < valueCounts; x++) {
        values[x] = triCubicSplineArray[x].interpolate(key[0], key[1], key[2]);
      }
      return values;
    }

    /**
     * 取得內插所需要的八個點
     * @param key double[]
     * @return double[][][]
     */
    public double[][][] getInterpolateCell(double[] key) {
      return null;
    }

    public KeyValue[] getKeyValueInterpolateCell(double[] key) {
      return null;
    }

    public double[] interpolateValue(double[] key, KeyValue[] cell) {
      return null;
    }

//    public boolean isInBlockInterpolate() {
//      return false;
//    }
//
//    public void setBlockInterpolate(boolean blockInterpolate) {
//    }
//
//    public boolean isUseKeyValue() {
//      return false;
//    }
  }

  public double[] getKeys(double[] values) {
    throw new UnsupportedOperationException();
  }
}
