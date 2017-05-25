package shu.math.lut;

import shu.math.lut.CubeTable.KeyValue;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �t�XAsymmetric3DLUT�ϥ�
 * ���Ѥ����禡���p��
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
   * �g��cell,��value�Ȥ����Xkey��
   * @param value double[]
   * @param cell double[][][]
   * @return double[]
   */
  double[] interpolateKey(double[] value, double[][][] cell);

  /**
   * �g��cell,��key�Ȥ����Xvalue��
   * @param key double[]
   * @param cell double[][][]
   * @return double[]
   */
  double[] interpolateValue(double[] key, double[][][] cell);

  /**
   * ���o��key�Ҧb��cell�H�K�i�椺��
   * @param key double[]
   * @return double[][][]
   */
  double[][][] getInterpolateCell(double[] key);

  CubeTable.KeyValue[] getKeyValueInterpolateCell(double[] key);

  double[] interpolateValue(double[] key, KeyValue[] cell);
}
