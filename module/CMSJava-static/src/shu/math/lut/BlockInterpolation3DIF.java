package shu.math.lut;

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
public interface BlockInterpolation3DIF {
  CubeTable.KeyValue[] getKeyValueInterpolateCell();

  double[] interpolateValue(double[] key, CubeTable.KeyValue[] cell);

  void registerInterpolateCell(double[] key);
}
