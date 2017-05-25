package shu.math.lut;

import shu.math.lut.CubeTable.KeyValue;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 配合Asymmetric3DLUT使用
 * 提供內插函式的計算
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public interface Interpolation3DIF {
  /**
   * 經由cell,由value值內插出key值
   * @param value double[]
   * @param cell double[][][]
   * @return double[]
   */
  double[] interpolateKey(double[] value, double[][][] cell);

  /**
   * 經由cell,由key值內插出value值
   * @param key double[]
   * @param cell double[][][]
   * @return double[]
   */
  double[] interpolateValue(double[] key, double[][][] cell);

  /**
   * 取得該key所在的cell以便進行內插
   * @param key double[]
   * @return double[][][]
   */
  double[][][] getInterpolateCell(double[] key);

  CubeTable.KeyValue[] getKeyValueInterpolateCell(double[] key);

  double[] interpolateValue(double[] key, KeyValue[] cell);
}
