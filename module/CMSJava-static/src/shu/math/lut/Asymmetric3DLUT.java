package shu.math.lut;

import java.io.*;

import shu.math.lut.CubeTable.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 非對稱對照表
 * 意思就是:可以找到不存在但是最接近的key-value對
 * 並且支援內插(但是在此類別內插為一個無作用的函式)
 *
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class Asymmetric3DLUT
    implements LookUpTable, Serializable {
  protected CubeTable keyValuePairs;
  private Interpolation3DIF interpolation;
  private BlockInterpolation3DIF blockInterpolation;
  protected Asymmetric3DLUT(CubeTable keyValuePairs) {
    this.keyValuePairs = keyValuePairs;
  }

  public boolean isRegisterBlockInterpolation3DIF() {
    return null != blockInterpolation;
  }

  public final void registerInterpolation3DIF(Interpolation3DIF
                                              interpolation) {
    this.interpolation = interpolation;
  }

  public final void registerBlockInterpolation3DIF(BlockInterpolation3DIF
      interpolation) {
    this.blockInterpolation = interpolation;
  }

  public long cellTime;
  public long interpTime;
  public final double[] getValues(double[] key) {
    if (!keyValuePairs.isValidKey(key)) {
      return null;
    }
    double[] result = null;
    //BlockInterpolation3DIF優先使用, 因為效率會較佳
    if (null != blockInterpolation) {
      long cellStart = System.currentTimeMillis();
      CubeTable.KeyValue[] cell = blockInterpolation.getKeyValueInterpolateCell();
      long interpStart = System.currentTimeMillis();
      result = blockInterpolation.interpolateValue(key, cell);
      interpTime += System.currentTimeMillis() - interpStart;
      cellTime += interpStart - cellStart;
    }
    else {
      long cellStart = System.currentTimeMillis();
//      double[][][] cell = interpolation.getInterpolateCell(key);
      CubeTable.KeyValue[] cell = interpolation.getKeyValueInterpolateCell(key);
      long interpStart = System.currentTimeMillis();
      result = interpolation.interpolateValue(key, cell);
      interpTime += System.currentTimeMillis() - interpStart;
      cellTime += interpStart - cellStart;
    }
    return result;
  }

  /**
   *
   * @param x int
   * @param y int
   * @param z int
   * @param lut double[]
   * lut安排方式 000 100 010 110 (第一層)| 001 101 011 111 (第二層)
   * @return double
   */
  protected final static double dens(int x, int y, int z, double[] lut) {
    int index = x + (y << 1) + (z << 2);
    return lut[index];
  }

  public CubeTable getKeyValuePairs() {
    return keyValuePairs;
  }

  public Interpolation3DIF getInterpolation() {
    return interpolation;
  }

}
