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
public interface CubeTableIF {
  public int size();

  public double[][] getKeyValueArray(int index);

  public CubeTable.KeyValue getKeyValue(int index);

  public int getIndex(double[] gridKey);

  public int getIndex(int[] gridKeyIndex);

//         public   KeyValue[] getNNearKeyValueByRMSSort(int n, double[] value) ;

  public double[] getKeyMaxValue();

  public double[] getKeyMinValue();

//  public double[] getStair();

  public double[] getAxisKeys(int axis);

//  public double[] getStair(int axis);
  /**
   *
   * @return int
   */
//  public int getGrid();

  public int getGrid(int axis);
}
