package shu.math.lut;

import java.io.*;
import java.util.*;

import flanagan.math.*;
//import shu.cms.colorspace.depend.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.lut.CubeTable.KeyValue;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * (using Sakamoto algorithm?)
 * 四面體內插法
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class TetrahedralInterpolation
    extends Asymmetric3DLUT implements Serializable {

  /**
   * 暫存Coordinate
   */
  protected double[] c;
  protected double rmsTolerance = Double.MAX_VALUE;
  protected int throughMaxScan = Integer.MAX_VALUE;
  protected int valueCounts;

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 以value找key, 反向尋找的方法
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static enum GetKeyMethod {
    Quick, Through, Grid, Minimisation;
  }

  public void setThroughMaxScan(int throughMaxScan) {
    this.throughMaxScan = throughMaxScan;
  }

  public void setValueCounts(int valueCounts) {
    this.valueCounts = valueCounts;
  }

  public void setThroughMaxScanRate(double rate) {
    this.throughMaxScan = (int) (keyValuePairs.size() * rate);
  }

  public void setRMSTolerance(double rmsTolerance) {
    this.rmsTolerance = rmsTolerance;
  }

  public final BlockInterpolation3DIF getBlockInterpolation3DIFInstance() {
    return new BlockInterpolation3D(this);
  }

  public static class BlockInterpolation3D
      implements BlockInterpolation3DIF {
    private TetrahedralInterpolation tetrahedralInterpolation;
    private CubeTable keyValuePairs;
    private CubeTable.KeyValue[] interpolateCell;
    public BlockInterpolation3D(TetrahedralInterpolation
                                tetrahedralInterpolation) {
      this.tetrahedralInterpolation = tetrahedralInterpolation;
      this.keyValuePairs = tetrahedralInterpolation.keyValuePairs;
    }

    public KeyValue[] getKeyValueInterpolateCell() {
      return interpolateCell;
    }

    public double[] interpolateValue(double[] key, KeyValue[] cell) {
      return cubeTetrahedralInterpolate(key, cell,
                                        tetrahedralInterpolation.valueCounts);
    }

    public void registerInterpolateCell(double[] key) {
      int[] nodeKeyIndex = keyValuePairs.getNearestNodeKeyIndex(key);
      int[] C = getCoordinateIndex(nodeKeyIndex);

      CubeTable.KeyValue[] result = new CubeTable.KeyValue[8];
      //000 100 010 110 001 101 011 111
      result[0] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[0], C[1], C[2]})); //000
      result[1] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[3], C[1], C[2]})); //100
      result[2] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[0], C[4], C[2]})); //010
      result[3] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[3], C[4], C[2]})); //110
      result[4] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[0], C[1], C[5]})); //001
      result[5] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[3], C[1], C[5]})); //101
      result[6] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[0], C[4], C[5]})); //011
      result[7] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[3], C[4], C[5]})); //111
      this.interpolateCell = result;
//      return result;

    }

  }

  public class Interpolation3D
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
      double[][] valueLUT = processValueLUT(cell, valueCounts);
      if (valueLUT == null) {
        return null;
      }
      else {
        return cubeTetrahedralInterpolate(key, c, valueLUT, valueCounts);
      }
    }

    private boolean showCellChange = false;
    public void showCellChange(boolean show) {
      this.showCellChange = show;
    }

    private int[] preNodeKeyIndex;
    /**
     * 取得內插所需要的八個點
     * @param key double[]
     * @return double[][][]
     */
    public double[][][] getInterpolateCell(double[] key) {
      int[] nodeKeyIndex = keyValuePairs.getNearestNodeKeyIndex(key);
      //========================================================================
      if (showCellChange && null != preNodeKeyIndex &&
          !Arrays.equals(preNodeKeyIndex, nodeKeyIndex)) {
        System.out.println("changed: " + IntArray.toString(preNodeKeyIndex) +
                           "->" + IntArray.toString(nodeKeyIndex));
      }
      preNodeKeyIndex = nodeKeyIndex;
      //========================================================================
      int[] C = getCoordinateIndex(nodeKeyIndex);

      /**
       * c的內容包括以下六個點
       * x0, y0, z0, x1, y1, z1
       */
      c = getCoordinate(C);
      //C 六點的索引值
      //c 六點的值

      double[][] r000 = keyValuePairs.getKeyValueArray(keyValuePairs.getIndex(new int[] {
          C[0], C[1], C[2]})); //P000
      double[][] r100 = keyValuePairs.getKeyValueArray(keyValuePairs.getIndex(new int[] {
          C[3], C[1], C[2]})); //P100
      double[][] r010 = keyValuePairs.getKeyValueArray(keyValuePairs.getIndex(new int[] {
          C[0], C[4], C[2]})); //P010
      double[][] r110 = keyValuePairs.getKeyValueArray(keyValuePairs.getIndex(new int[] {
          C[3], C[4], C[2]})); //P110
      double[][] r001 = keyValuePairs.getKeyValueArray(keyValuePairs.getIndex(new int[] {
          C[0], C[1], C[5]})); //P001
      double[][] r101 = keyValuePairs.getKeyValueArray(keyValuePairs.getIndex(new int[] {
          C[3], C[1], C[5]})); //P101
      double[][] r011 = keyValuePairs.getKeyValueArray(keyValuePairs.getIndex(new int[] {
          C[0], C[4], C[5]})); //P011
      double[][] r111 = keyValuePairs.getKeyValueArray(keyValuePairs.getIndex(new int[] {
          C[3], C[4], C[5]})); //P111

      //cell內的八個點, 其排列為 { 點1,2,3...8}
      //其中點的內為 {{kx,ky,kz},{vx,vy,vz}}
      // { {{kx,ky,kz},{vx,vy,vz}}1, {{kx,ky,kz},{vx,vy,vz}}2, ...8}
      return new double[][][] {
          r000, r100, r010, r110, r001, r101, r011, r111};
    }

    public CubeTable.KeyValue[] getKeyValueInterpolateCell(double[] key) {
      int[] nodeKeyIndex = keyValuePairs.getNearestNodeKeyIndex(key);
      //========================================================================
      if (showCellChange && null != preNodeKeyIndex &&
          !Arrays.equals(preNodeKeyIndex, nodeKeyIndex)) {
        System.out.println("changed: " + IntArray.toString(preNodeKeyIndex) +
                           "->" + IntArray.toString(nodeKeyIndex));
      }
      preNodeKeyIndex = nodeKeyIndex;
//========================================================================

      int[] C = getCoordinateIndex(nodeKeyIndex);

      CubeTable.KeyValue[] result = new CubeTable.KeyValue[8];
      result[0] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[0], C[1], C[2]}));
      result[1] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[3], C[1], C[2]}));
      result[2] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[0], C[4], C[2]}));
      result[3] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[3], C[4], C[2]}));
      result[4] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[0], C[1], C[5]}));
      result[5] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[3], C[1], C[5]}));
      result[6] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[0], C[4], C[5]}));
      result[7] = keyValuePairs.getKeyValue(keyValuePairs.getIndex(new int[] {
          C[3], C[4], C[5]}));

      return result;
    }

    public double[] interpolateValue(double[] key, KeyValue[] cell) {
      return cubeTetrahedralInterpolate(key, cell, valueCounts);
    }

  }

  /**
   * 取得與values最接近的key
   * @param value double[]
   * @param nonZero boolean
   * @return double[]
   */
  protected final double[] getNearestKey(double[] value, boolean nonZero) {
    /**
     * @todo H 設定n為多少較好?
     */
    CubeTable.KeyValue[] keyValues = keyValuePairs.getNNearKeyValueByRMSSort(
        100, value);

    if (nonZero) {
      int size = keyValues.length;
      for (int x = 0; x < size; x++) {
        double[] key = keyValues[0].getKey();
        double dot = 1;
        for (double k : key) {
          dot *= k;
        }
        if (dot != 0) {
          return key;
        }
      }
      return null;
    }
    else {
      return keyValues[0].getKey();
    }
  }

  /**
   * 從RMS去找最接近的value群,然後找出key
   * @param value double[]
   * @param slice int
   * @return double[]
   */
  protected final double[] getKeyGrid(double[] value, int slice) {
    double[] nearestKey = getNearestKey(value, false);
    double[] coordinate = getCoordinate(nearestKey);
    double[] step = new double[] {
        (coordinate[3] - coordinate[0]) / slice,
        (coordinate[4] - coordinate[1]) / slice,
        (coordinate[5] - coordinate[2]) / slice};
    double[] gridKey = new double[3];
    double bestRMS = Double.MAX_VALUE;
    double[] key = new double[3];

    for (double x = coordinate[0]; x <= coordinate[3]; x += step[0]) {
      for (double y = coordinate[1]; y <= coordinate[4]; y += step[1]) {
        for (double z = coordinate[2]; z <= coordinate[5]; z += step[2]) {
          gridKey[0] = x;
          gridKey[1] = y;
          gridKey[2] = z;

          double[] gridValue = getValues(gridKey);
          double rmsd = Maths.RMSD(gridValue, value);
          if (rmsd < bestRMS) {
            bestRMS = rmsd;
            System.arraycopy(gridKey, 0, key, 0, 3);
          }
        }
      }
    }
    return key;
  }

  /**
   * 用較快的速度取得key(但是較不準)
   * @param value double[]
   * @param n int 取樣數量,越大越準,但是會變慢
   * @return double[]
   */
  protected final double[] getKeyQuick(double[] value, int n) {
    CubeTable.KeyValue[] keyValues = keyValuePairs.getNNearKeyValueByRMSSort(n,
        value);
    int size = keyValues.length;

    for (int p0 = 0; p0 < size - 3; p0++) {
      double[] p0v = keyValues[p0].getValue();
      for (int p1 = p0 + 1; p1 < size - 2; p1++) {
        double[] p1v = keyValues[p1].getValue();
        for (int p2 = p1 + 1; p2 < size - 1; p2++) {
          double[] p2v = keyValues[p2].getValue();
          for (int p3 = p2 + 1; p3 < size; p3++) {
            double[] p3v = keyValues[p3].getValue();

            if (isInsideTetrahedron(value, p0v, p1v, p2v, p3v)) {
              double[][] xn = new double[][] {
                  p0v, p1v, p2v, p3v};
              double[][] yn = new double[][] {
                  keyValues[p0].getKey(),
                  keyValues[p1].getKey(),
                  keyValues[p2].getKey(),
                  keyValues[p3].getKey()};
              return tetrahedralInterpolate(value, xn, yn);
            }
          }
        }
      }
    }

    return null;
  }

  protected class KeyMinimisation
      implements MinimisationFunction {
    private double[] targetValue;
    protected KeyMinimisation(double[] targetValue) {
      this.targetValue = targetValue;
    }

    /**
     * function
     *
     * @param key double[]
     * @return double
     */
    public double function(double[] key) {
      double[] estValue = getValues(key);
      return Maths.RMSD(estValue, targetValue);
    }

  }

  protected final double[] getKeyMinimisation(double[] value) {
    Minimisation min = new Minimisation();
    double[] start = getNearestKey(value, true);
    double[] maxValue = keyValuePairs.getKeyMaxValue();
    double[] minValue = keyValuePairs.getKeyMinValue();
    for (int x = 0; x < 3; x++) {
      min.addConstraint(x, 1, maxValue[x]);
      min.addConstraint(x, -1, minValue[x]);
    }
    KeyMinimisation func = new KeyMinimisation(value);
    min.nelderMead(func, start);
    double[] param = min.getParamValues();
    return param;
  }

  /**
   * 從頭到尾去找key
   * @param value double[]
   * @return double[]
   */
  protected final double[] getKeyThrough(double[] value) {
    CubeTable.KeyValue[] keyValues = keyValuePairs.getKeyValueByRMSDSort(value);
    int size = keyValues.length;

    for (int p0 = 0; p0 < size - 3; p0++) {
      if (rmsTolerance != 0 && keyValues[p0].rmsd > rmsTolerance ||
          throughMaxScan != 0 && p0 > throughMaxScan) {
        break;
      }
      double[] p0v = keyValues[p0].getValue();
      for (int p1 = p0 + 1; p1 < size - 2; p1++) {
        if (rmsTolerance != 0 && keyValues[p1].rmsd > rmsTolerance ||
            throughMaxScan != 0 && p1 > throughMaxScan) {
          break;
        }
        double[] p1v = keyValues[p1].getValue();
        for (int p2 = p1 + 1; p2 < size - 1; p2++) {
          if (rmsTolerance != 0 && keyValues[p2].rmsd > rmsTolerance ||
              throughMaxScan != 0 && p2 > throughMaxScan) {
            break;
          }
          double[] p2v = keyValues[p2].getValue();
          for (int p3 = p2 + 1; p3 < size; p3++) {
            if (rmsTolerance != 0 && keyValues[p3].rmsd > rmsTolerance ||
                throughMaxScan != 0 && p3 > throughMaxScan) {
              break;
            }

            double[] p3v = keyValues[p3].getValue();

            if (isInsideTetrahedron(value, p0v, p1v, p2v, p3v)) {
              double[][] xn = new double[][] {
                  p0v, p1v, p2v, p3v};
              double[][] yn = new double[][] {
                  keyValues[p0].getKey(),
                  keyValues[p1].getKey(),
                  keyValues[p2].getKey(),
                  keyValues[p3].getKey()};
              return tetrahedralInterpolate(value, xn, yn);
            }
          }
        }
      }
    }
    return null;
  }

  protected void initGetKeyFunction() {
    keyValuePairs.initIndexOfValueSort();
  }

  /**
   * 以value找key, 反向尋找
   * @param values double[]
   * @param method GetKeyMethod
   * @return double[]
   */
  public final double[] getKeys(double[] values, GetKeyMethod method) {
    switch (method) {
      case Quick:
        return getKeyQuick(values, 32);
      case Through:
        return getKeyThrough(values);
      case Grid:
        return getKeyGrid(values, 8);
      case Minimisation:
        return getKeyMinimisation(values);
      default:
        return null;
    }
  }

  /**
   * 以value找key, 反向尋找
   * @param value double[]
   * @return double[]
   */
  public final double[] getKeys(double[] value) {
    return getKeys(value, GetKeyMethod.Grid);
  }

  /**
   * TetrahedralInterpolation建構式
   * 傳入對照表以便進行內插
   * @param keyValuePairs CubeTable
   */
  public TetrahedralInterpolation(CubeTable keyValuePairs) {
    this(keyValuePairs, true);

  }

  public TetrahedralInterpolation(CubeTable keyValuePairs, boolean getKey) {
    super(keyValuePairs);
    this.valueCounts = keyValuePairs.getValueCounts();
    /**
     * @todo H 為啥要mark掉?
     */
    if (getKey) {
      this.initGetKeyFunction();
    }
//做Interpolation3DIF的註冊動作
    registerInterpolation3DIF(new Interpolation3D());
  }

  /**
   * 取得最接近key的grid key(簡而言之, 就是整數點)
   * @param key double[]
   * @return double[]
   * @deprecated
   */
  protected final double[] getGridKey(double[] key) {
    // key - min
//    double[] value = DoubleArray.minus(key, keyValuePairs.getKeyMinValue());
//    double[] remainder = DoubleArray.modulus(value, keyValuePairs.getStair());

    double[] nearestNodeKey = keyValuePairs.getNearestNodeKey(key);
//    double[] remainder2 = DoubleArray.minus(key, nearestNodeKey);
//
//    double[] grid = DoubleArray.minus(key, remainder);
//    return grid;
    return nearestNodeKey;
  }

  public static interface CoordinateIF {
    public double[] getCoordinate(double[] key);
//    public int[] getCoordinateIndex(int[] keyIndex);
  }

  private CoordinateIF coordinateIF;
  public void registerCoordinateIF(CoordinateIF coordinateIF) {
    this.coordinateIF = coordinateIF;
  }

  /**
   * 取得正立方體的六個座標點:
   *   x0, y0, z0, x1, y1, z1
   *
   * 由此六個點就可以組成正立方體的八個點:
   *  x0 y0 z0
   *  x1 y0 z0
   *  x0 y1 z0
   *  x0 y0 z1
   *  x0 y1 z1
   *  x1 y1 z0
   *  x1 y0 z1
   *  x1 y1 z1
   * @param key double[]
   * @return double[]
   */
  protected double[] getCoordinate(double[] key) {
    if (coordinateIF != null) {
      return coordinateIF.getCoordinate(key);
    }
    else {
//      double[] gridKey = getGridKey(key);
//      keyValuePairs.geta
//      double[] gridKey = keyValuePairs.getNearestNodeKey(key);
//      double[] stair = keyValuePairs.getStair();
      int[] nodeKeyIndex = keyValuePairs.getNearestNodeKeyIndex(key);

      double x0 = keyValuePairs.getAxisKeys(0)[nodeKeyIndex[0]];
      double y0 = keyValuePairs.getAxisKeys(1)[nodeKeyIndex[1]];
      double z0 = keyValuePairs.getAxisKeys(2)[nodeKeyIndex[2]];
      double x1 = keyValuePairs.getAxisKeys(0)[nodeKeyIndex[0] + 1];
      double y1 = keyValuePairs.getAxisKeys(1)[nodeKeyIndex[1] + 1];
      double z1 = keyValuePairs.getAxisKeys(2)[nodeKeyIndex[2] + 1];
      return new double[] {
          x0, y0, z0, x1, y1, z1};
    }
  }

  protected double[] getCoordinate(int[] coordinateIndex) {
    double x0 = keyValuePairs.getAxisKeys(0)[coordinateIndex[0]];
    double y0 = keyValuePairs.getAxisKeys(1)[coordinateIndex[1]];
    double z0 = keyValuePairs.getAxisKeys(2)[coordinateIndex[2]];
    double x1 = keyValuePairs.getAxisKeys(0)[coordinateIndex[3]];
    double y1 = keyValuePairs.getAxisKeys(1)[coordinateIndex[4]];
    double z1 = keyValuePairs.getAxisKeys(2)[coordinateIndex[5]];
    return new double[] {
        x0, y0, z0, x1, y1, z1};

  }

  protected static int[] getCoordinateIndex(int[] keyIndex) {
    int x0 = keyIndex[0];
    int y0 = keyIndex[1];
    int z0 = keyIndex[2];
    int x1 = keyIndex[0] + 1;
    int y1 = keyIndex[1] + 1;
    int z1 = keyIndex[2] + 1;
    return new int[] {
        x0, y0, z0, x1, y1, z1};
  }

//  protected int[] getCoordinate(int[] keyIndex) {
//    return new int[] {
//        keyIndex[0], keyIndex[1], keyIndex[2],
//        keyIndex[0] + 1, keyIndex[1] + 1, keyIndex[2] + 1};
//  }

  public static void example(String[] args) {
    double[] i = new double[] {
        3, 140, 85};
    double[] c = new double[] {
        0, 128, 64, 64, 192, 128};

    double[][] l = new double[][] {
        {
        66.46, 71.05, 77.92, 81.53, 67.65, 72.13, 78.85, 82.39}, {
        -52.62, -24.83, -64.63, -40.91, -42.14, -17.57, -56.06, -34.41}, {
        11.72, 18.60, 26.48, 31.73, -13.30, -6.30, 2.52, 7.93},
    };
    double[] o = cubeTetrahedralInterpolate(i, c, l);
    System.out.println(Arrays.toString(o));

  }

  /**
   * 以正立方體為基準的四面體內插法
   *
   * 四面體內插法實作
   * input為存在於coordinate所形成的立方體的一個點,
   * 而lut為coordinate各點所對應的值.
   * 由input與coordinate之間的關係內插出input所應該代表的值
   *
   * @param input double[]
   * input安排方式: channel
   * @param coordinate double[]
   * coordinate安排方式: x0 y0 z0 x1 y1 z1
   * @param lut double[][]
   * lut安排方式: 第二維度 channel / 第一維度 000 100 010 110 (第一層)| 001 101 011 111 (第二層)
   * @return double[]
   */
  public static double[] cubeTetrahedralInterpolate(double[] input,
      double[] coordinate, double[][] lut) {
    return cubeTetrahedralInterpolate(input, coordinate, lut, 3);
  }

  protected final static double[] getfxyz(double[] input, double[] coordinate) {
    double x0 = coordinate[0];
    double y0 = coordinate[1];
    double z0 = coordinate[2];
    double x1 = coordinate[3];
    double y1 = coordinate[4];
    double z1 = coordinate[5];

    double px = input[0];
    double py = input[1];
    double pz = input[2];

    double dx = px - x0;
    double dy = py - y0;
    double dz = pz - z0;

    double fx = dx / (x1 - x0);
    double fy = dy / (y1 - y0);
    double fz = dz / (z1 - z0);
    return new double[] {
        fx, fy, fz};
  }

  /**
   * 判別位於六面體中的哪一個四面體
   * @param dx double
   * @param dy double
   * @param dz double
   * @return int
   */
  protected final static int getTetrahedralInCube(double dx, double dy,
                                                  double dz) {
    if (dx > 1 || dy > 1 || dz > 1) {
      return -1;
    }
    else if (dx >= dy && dy >= dz) {
      //T1
      return 1;
    }
    else if (dx >= dz && dz >= dy) {
      //T2
      return 2;
    }
    else if (dz >= dx && dx >= dy) {
      //T3
      return 3;
    }
    else if (dy >= dx && dx >= dz) {
      //T4
      return 4;
    }
    else if (dy >= dz && dz >= dx) {
      //T5
      return 5;
    }
    else if (dz >= dy && dy >= dx) {
      //T6
      return 6;
    }
    else {
      return -1;
    }
  }

  public static void main(String[] args) {
//    double[] input = {
//        1, 1, 1};
//    double[] coord = {
//        0, 0, 0, 2, 2, 2};
//    double[][] lut = {
//        {
//        0, 1, 1, 1, 1, 1, 1, 1}
//    };
//    double[] output = tetrahedralInterpolate(input, coord, lut, 1);
//    System.out.println(DoubleArray.toString(output));
    double[][][] lut = {};
    double[] xKeys = {};
    double[] yKeys = {};
    double[] zKeys = {};
    CubeTable ctable = new CubeTable(lut, xKeys, yKeys, zKeys);
    TetrahedralInterpolation interp = new TetrahedralInterpolation(ctable);
    System.out.println(DoubleArray.toString(interp.getValues(
        new double[] {1, 1, 1})));
  }

  private final static double[] getfxyz(double[] input,
                                        CubeTable.KeyValue[] cell) {

    double[] xyz0 = cell[0].getKey();
    double[] xyz1 = cell[7].getKey();
    double x0 = xyz0[0];
    double y0 = xyz0[1];
    double z0 = xyz0[2];
    double x1 = xyz1[0];
    double y1 = xyz1[1];
    double z1 = xyz1[2];

    double px = input[0];
    double py = input[1];
    double pz = input[2];

    double dx = px - x0;
    double dy = py - y0;
    double dz = pz - z0;

    double fx = dx / (x1 - x0);
    double fy = dy / (y1 - y0);
    double fz = dz / (z1 - z0);
    return new double[] {
        fx, fy, fz};
  }

  private static double[][] valueLut;
  /**
   * 如果cell內有NaN,會回傳null
   * @param cell double[][][]
   * @param outputChannels int
   * @return double[][]
   */
  protected static double[][] processValueLUT(double[][][] cell,
                                              int outputChannels) {
    //cell內的八個點, 其排列為 { 點1,2,3...8}
    //其中點的內為 {{kx,ky,kz},{vx,vy,vz}}
    // { {{kx,ky,kz},{vx,vy,vz}}1, {{kx,ky,kz},{vx,vy,vz}}2, ...8}
    //valueLut[channel][point]

    int size = 8;
    if (null == valueLut) {
      valueLut = new double[outputChannels][size];
    }

    for (int x = 0; x < size; x++) {
      for (int y = 0; y < outputChannels; y++) {
        valueLut[y][x] = cell[x][1][y];
        if (Double.isNaN(valueLut[y][x])) {
          return null;
        }
      }

    }
    return valueLut;
  }

  protected final static double dens(int x, int y, int z,
                                     CubeTable.KeyValue[] cell, int channel) {
    int index = x + (y << 1) + (z << 2);
    return cell[index].getValue()[channel];
  }

  /**
   * 以正立方體為基準的四面體內插法
   * @param input double[]
   * @param cell KeyValue[] 順序: 000 100 010 110 001 101 011 111
   * @param outputChannels int
   * @return double[]
   */
  public static double[] cubeTetrahedralInterpolate(double[] input,
      CubeTable.KeyValue[] cell, int outputChannels) {
    if (input.length != 3) {
      throw new IllegalArgumentException("input.length != 3");
    }

    double[] fxyz = getfxyz(input, cell);
    double fx = fxyz[0];
    double fy = fxyz[1];
    double fz = fxyz[2];

    int tetrahedralIndex = getTetrahedralInCube(fx, fy, fz);
    if (tetrahedralIndex == -1) {
      throw new IllegalStateException("input is not in cube.");
    }
    double[] output = new double[outputChannels];
    double c1 = 0, c2 = 0, c3 = 0;
    //lut安排方式: 第二維度 channel / 第一維度 000 100 010 110 (第一層)| 001 101 011 111 (第二層)

    /* cell內的八個點, 其排列為 { 點1,2,3...8}
            其中點的內為 {{kx,ky,kz},{vx,vy,vz}}
             { {{kx,ky,kz},{vx,vy,vz}}1, {{kx,ky,kz},{vx,vy,vz}}2, ...8}
        valueLut[y][x] = cell[x][1][y];
     */


    for (int ch = 0; ch < outputChannels; ch++) {
      // These are the 6 Tetrahedral
      switch (tetrahedralIndex) {
        case 1: //T1
          c1 = dens(1, 0, 0, cell, ch) - dens(0, 0, 0, cell, ch);
          c2 = dens(1, 1, 0, cell, ch) - dens(1, 0, 0, cell, ch);
          c3 = dens(1, 1, 1, cell, ch) - dens(1, 1, 0, cell, ch);
          break;
        case 2: //T2
          c1 = dens(1, 0, 0, cell, ch) - dens(0, 0, 0, cell, ch);
          c2 = dens(1, 1, 1, cell, ch) - dens(1, 0, 1, cell, ch);
          c3 = dens(1, 0, 1, cell, ch) - dens(1, 0, 0, cell, ch);
          break;
        case 3: //T3
          c1 = dens(1, 0, 1, cell, ch) - dens(0, 0, 1, cell, ch);
          c2 = dens(1, 1, 1, cell, ch) - dens(1, 0, 1, cell, ch);
          c3 = dens(0, 0, 1, cell, ch) - dens(0, 0, 0, cell, ch);
          break;
        case 4: //T4
          c1 = dens(1, 1, 0, cell, ch) - dens(0, 1, 0, cell, ch);
          c2 = dens(0, 1, 0, cell, ch) - dens(0, 0, 0, cell, ch);
          c3 = dens(1, 1, 1, cell, ch) - dens(1, 1, 0, cell, ch);
          break;
        case 5: //T5
          c1 = dens(1, 1, 1, cell, ch) - dens(0, 1, 1, cell, ch);
          c2 = dens(0, 1, 0, cell, ch) - dens(0, 0, 0, cell, ch);
          c3 = dens(0, 1, 1, cell, ch) - dens(0, 1, 0, cell, ch);
          break;
        case 6: //T6
          c1 = dens(1, 1, 1, cell, ch) - dens(0, 1, 1, cell, ch);
          c2 = dens(0, 1, 1, cell, ch) - dens(0, 0, 1, cell, ch);
          c3 = dens(0, 0, 1, cell, ch) - dens(0, 0, 0, cell, ch);
          break;
      }
      //P000+C1 * dx+C2 * dy+C3 * dz
      double p000 = dens(0, 0, 0, cell, ch);
      output[ch] = p000 + c1 * fx + c2 * fy + c3 * fz;
    }
    return output;
  }

  /**
   * 以正立方體為基準的四面體內插法
   *
   * 四面體內插法實作
   * input為存在於coordinate所形成的四方體的一個點,
   * 而lut為coordinate各點所對應的值.
   * 由input與coordinate之間的關係內插出input所應該代表的值
   *
   * @param input double[]
   * input安排方式: channel
   * @param coordinate double[]
   * coordinate安排方式: x0 y0 z0 x1 y1 z1
   * @param lut double[][]
   * lut安排方式: 第二維度 channel / 第一維度 000 100 010 110 (第一層)| 001 101 011 111 (第二層)
   * 000=> x0 y0 z0, 100=> x1 y0 z0, 010=> x0 y1 z0...餘類推
   * @param outputChannels int
   * 輸出值的channel數
   * @return double[]
   */
  public static double[] cubeTetrahedralInterpolate(double[] input,
      double[] coordinate, double[][] lut, int outputChannels) {
    if (input.length != 3) {
      throw new IllegalArgumentException("input.length != 3");
    }

    double[] fxyz = getfxyz(input, coordinate);
    double fx = fxyz[0];
    double fy = fxyz[1];
    double fz = fxyz[2];

    int tetrahedralIndex = getTetrahedralInCube(fx, fy, fz);
    if (tetrahedralIndex == -1) {
      throw new IllegalStateException("input is not in cube.");
    }
    double[] output = new double[outputChannels];
    double c1 = 0, c2 = 0, c3 = 0;

    for (int ch = 0; ch < outputChannels; ch++) {
      // These are the 6 Tetrahedral
      switch (tetrahedralIndex) {
        case 1: //T1
          c1 = dens(1, 0, 0, lut[ch]) - dens(0, 0, 0, lut[ch]);
          c2 = dens(1, 1, 0, lut[ch]) - dens(1, 0, 0, lut[ch]);
          c3 = dens(1, 1, 1, lut[ch]) - dens(1, 1, 0, lut[ch]);
          break;
        case 2: //T2
          c1 = dens(1, 0, 0, lut[ch]) - dens(0, 0, 0, lut[ch]);
          c2 = dens(1, 1, 1, lut[ch]) - dens(1, 0, 1, lut[ch]);
          c3 = dens(1, 0, 1, lut[ch]) - dens(1, 0, 0, lut[ch]);
          break;
        case 3: //T3
          c1 = dens(1, 0, 1, lut[ch]) - dens(0, 0, 1, lut[ch]);
          c2 = dens(1, 1, 1, lut[ch]) - dens(1, 0, 1, lut[ch]);
          c3 = dens(0, 0, 1, lut[ch]) - dens(0, 0, 0, lut[ch]);
          break;
        case 4: //T4
          c1 = dens(1, 1, 0, lut[ch]) - dens(0, 1, 0, lut[ch]);
          c2 = dens(0, 1, 0, lut[ch]) - dens(0, 0, 0, lut[ch]);
          c3 = dens(1, 1, 1, lut[ch]) - dens(1, 1, 0, lut[ch]);
          break;
        case 5: //T5
          c1 = dens(1, 1, 1, lut[ch]) - dens(0, 1, 1, lut[ch]);
          c2 = dens(0, 1, 0, lut[ch]) - dens(0, 0, 0, lut[ch]);
          c3 = dens(0, 1, 1, lut[ch]) - dens(0, 1, 0, lut[ch]);
          break;
        case 6: //T6
          c1 = dens(1, 1, 1, lut[ch]) - dens(0, 1, 1, lut[ch]);
          c2 = dens(0, 1, 1, lut[ch]) - dens(0, 0, 1, lut[ch]);
          c3 = dens(0, 0, 1, lut[ch]) - dens(0, 0, 0, lut[ch]);
          break;
      }
      //P000+C1 * dx+C2 * dy+C3 * dz
      output[ch] = dens(0, 0, 0, lut[ch]) + c1 * fx + c2 * fy + c3 * fz;
    }
    return output;
  }

  /**
   * 測試p是否在p0~p3所組成的四面體內
   * @param alpha double
   * @param beta double
   * @param gamma double
   * @return boolean
   */
  protected static boolean isInsideTetrahedron(double alpha, double beta,
                                               double gamma) {
    if (alpha >= 0 && beta >= 0 && gamma >= 0 &&
        (alpha + beta + gamma) <= 1) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * 測試p是否在p0~p3所組成的四面體內
   * @param p double[]
   * @param p0 double[]
   * @param p1 double[]
   * @param p2 double[]
   * @param p3 double[]
   * @return boolean
   */
  public static boolean isInsideTetrahedron(double[] p, double[] p0,
                                            double[] p1, double[] p2,
                                            double[] p3) {
    double[] abr = calculateAlphaBetaGamma(p, p0, p1, p2, p3);
    if (abr == null) {
      return false;
    }
    return isInsideTetrahedron(abr[0], abr[1], abr[2]);
  }

  protected static double[] calculateAlphaBetaGamma(double[] p, double[] p0,
      double[] p1,
      double[] p2, double[] p3) {
    double[][] delta = new double[][] {
        {
        p1[0] - p0[0], p2[0] - p0[0], p3[0] - p0[0]}, {
        p1[1] - p0[1], p2[1] - p0[1], p3[1] - p0[1]}, {
        p1[2] - p0[2], p2[2] - p0[2], p3[2] - p0[2]}
    };
    if (!DoubleArray.isNonsingular(delta)) {
      return null;
    }
    double[][] deltaInv = DoubleArray.inverse(delta);
    double[] deltaP = new double[] {
        p[0] - p0[0], p[1] - p0[1], p[2] - p0[2]};
    double[] abr = DoubleArray.times(deltaInv, deltaP);
    return abr;
  }

  /**
   * 以任意四點為基礎的四面體內插法
   * @param x double[]
   * @param xn double[][]
   * @param yn double[][]
   * @return double[]
   */
  public static double[] tetrahedralInterpolate(double[] x, double[][] xn,
                                                double[][] yn) {
    if (xn.length != 4 || yn.length != 4) {
      throw new IllegalArgumentException(" xn.length or yn.length != 4");
    }
    double[] abr = calculateAlphaBetaGamma(x, xn[0], xn[1], xn[2], xn[3]);
    if (!isInsideTetrahedron(abr[0], abr[1], abr[2])) {
      throw new IllegalArgumentException("x is not in xn");
    }

    double[][] delta = new double[][] {
        {
        yn[1][0] - yn[0][0], yn[2][0] - yn[0][0], yn[3][0] - yn[0][0]}, {
        yn[1][1] - yn[0][1], yn[2][1] - yn[0][1], yn[3][1] - yn[0][1]}, {
        yn[1][2] - yn[0][2], yn[2][2] - yn[0][2], yn[3][2] - yn[0][2]}
    };
    double[] timesResult = DoubleArray.timesFast(delta, abr);
    //yn[0][0]~[2]是原點
    timesResult[0] += yn[0][0];
    timesResult[1] += yn[0][1];
    timesResult[2] += yn[0][2];

    return timesResult;
  }

  public int getValueCounts() {
    return valueCounts;
  }

}
