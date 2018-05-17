package auo.mura.interp;

import shu.math.lut.*;
import shu.math.lut.CubeTable.*;
import auo.mura.CorrectionData;
import auo.mura.DeMuraParameter;
import auo.mura.Constant;
import auo.mura.MuraCompensationProducer;

/**
 * <p>Title: Colour Management System</p>
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
public class ThreeDInterpolation {
  private double[] zAxisKeys;
  public ThreeDInterpolation(CubeTable keyValuePairs, DeMuraParameter parameter) {
    this.keyValuePairs = keyValuePairs;
    this.parameter = parameter;
    this.blackLimitGrayLevel = parameter.blackLimit;
    this.whiteLimitGrayLevel = parameter.whiteLimit;

    int layerNumber = parameter.layerNumber;
    zAxisKeys = new double[layerNumber + 2];
    if (1 == layerNumber) {
      zAxisKeys[0] = blackLimitGrayLevel;
      zAxisKeys[1] = parameter.planeLevel1;
      zAxisKeys[2] = whiteLimitGrayLevel;
    }
    else if (2 == layerNumber) {
      zAxisKeys[0] = blackLimitGrayLevel;
      zAxisKeys[1] = parameter.planeLevel1;
      zAxisKeys[2] = parameter.planeLevel2;
      zAxisKeys[3] = whiteLimitGrayLevel;
    }
    else if (3 == layerNumber) {
      zAxisKeys[0] = blackLimitGrayLevel;
      zAxisKeys[1] = parameter.planeLevel1;
      zAxisKeys[2] = parameter.planeLevel2;
      zAxisKeys[3] = parameter.planeLevel3;
      zAxisKeys[4] = whiteLimitGrayLevel;
    }

//    zAxisKeys = keyValuePairs.getAxisKeys(2);
    this.initCoefs(parameter);
  }

  private DeMuraParameter parameter;

//  /**
//   *
//   * @param keyValuePairs CubeTable
//   * @param blackLimitGrayLevel int
//   * @param whiteLimitGrayLevel int
//   * @deprecated
//   */
//  public ThreeDInterpolation(CubeTable keyValuePairs, int blackLimitGrayLevel,
//                             int whiteLimitGrayLevel) {
//    this.keyValuePairs = keyValuePairs;
//    this.blackLimitGrayLevel = blackLimitGrayLevel;
//    this.whiteLimitGrayLevel = whiteLimitGrayLevel;
//    zAxisKeys = keyValuePairs.getAxisKeys(2);
//    initCoefs();
//
//  }

  private int maxInputGrayLevel = 1023;
//  private boolean newCalculate = false;
  private double blackSlope = 1;
  private double whiteSlope = 1;

  private void initCoefs(DeMuraParameter parameter) {
    int layer = parameter.layerNumber;
    coefs = new short[layer + 1];
    if (1 == layer) {
      coefs[0] = (short) parameter.planeB1Coef;
      coefs[1] = (short) parameter.plane3WCoef;
    }
    else if (2 == layer) {
      coefs[0] = (short) parameter.planeB1Coef;
      coefs[1] = (short) parameter.plane12Coef;
      coefs[2] = (short) parameter.plane3WCoef;
    }
    else if (3 == layer) {
      coefs[0] = (short) parameter.planeB1Coef;
      coefs[1] = (short) parameter.plane12Coef;
      coefs[2] = (short) parameter.plane23Coef;
      coefs[3] = (short) parameter.plane3WCoef;
    }

    short offset1 = (short) parameter.dataOffset1;
    short offset2 = (short) parameter.dataOffset2;
    short offset3 = (short) parameter.dataOffset3;
    short mag1 = (short) parameter.dataMag1;
    short mag2 = (short) parameter.dataMag2;
    short mag3 = (short) parameter.dataMag3;
    this.setOffset(new short[] {offset1, offset2, offset3});
    this.setMagnitude(new short[] {mag1, mag2, mag3});
    this.setBlockSize(parameter.vBlockSize, parameter.hBlockSize);
  }

//  /**
//   * @deprecated
//   */
//  private void initCoefs() {
//
//    coefs = new short[4];
//
//    coefs[1] = (short) Math.round(Math.pow(2, 16) / (zAxisKeys[2] - zAxisKeys[1]));
//    coefs[2] = (short) Math.round(Math.pow(2, 16) / (zAxisKeys[3] - zAxisKeys[2]));
//
//    if (newCalculate) {
//      coefs[0] = (short) Math.round(1. / (zAxisKeys[1] - blackLimitGrayLevel) *
//                                    Math.pow(2, 16));
//      coefs[3] = (short) Math.round(1. / (whiteLimitGrayLevel - zAxisKeys[3]) *
//                                    Math.pow(2, 16));
//
//    }
//    else {
//
//      coefs[0] = (short) Math.round(blackSlope * Math.pow(2, 15) /
//                                    zAxisKeys[1]);
//      coefs[3] = (short) Math.round(whiteSlope /
//                                    (maxInputGrayLevel - zAxisKeys[3]) *
//                                    Math.pow(2, 15));
//
//      blackLimitGrayLevel = (int) (zAxisKeys[1] -
//                                   (int) Math.round(Math.pow(2, 16) /
//          (coefs[0] * 2 + 1)));
//      whiteLimitGrayLevel = (int) (zAxisKeys[3] +
//                                   (int) Math.round(Math.pow(2, 16) /
//          (coefs[3] * 2 + 1)));
//      if (blackLimitGrayLevel < 0) {
//        blackLimitGrayLevel = 0;
//      }
//      if (whiteLimitGrayLevel > 1023) {
//        whiteLimitGrayLevel = 1023;
//      }
//    }
//
//  }

//  public ThreeDInterpolation(int blackLimitGrayLevel,
//                             int whiteLimitGrayLevel, int[] zAxisKeys_) {
////  this.keyValuePairs = keyValuePairs;
//    this.blackLimitGrayLevel = blackLimitGrayLevel;
//    this.whiteLimitGrayLevel = whiteLimitGrayLevel;
//
//    zAxisKeys = new double[zAxisKeys_.length];
//    for (int x = 0; x < zAxisKeys_.length; x++) {
//      zAxisKeys[x] = zAxisKeys_[x];
//    }
//
//    initCoefs();
//  }

//  public ThreeDInterpolation(double blackSlope, double whiteSlope,
//                             int[] zAxisKeys_) {
//
//    this.blackSlope = blackSlope;
//    this.whiteSlope = whiteSlope;
//    this.newCalculate = false;
//
//    zAxisKeys = new double[zAxisKeys_.length];
//    for (int x = 0; x < zAxisKeys_.length; x++) {
//      zAxisKeys[x] = zAxisKeys_[x];
//    }
//
//    initCoefs();
//  }

  private short[] coefs;
  private int blackLimitGrayLevel, whiteLimitGrayLevel;
  private int blockHeight, blockWidth;
  public void setBlockSize(int height, int width) {
    this.blockHeight = height;
    this.blockWidth = width;
  }

  public void registerBlockInterpolation3DIF(BlockInterpolation3DIF
                                             blockInterpolation3DIF) {
    this.blockInterpolation3D = blockInterpolation3DIF;
  }

  public void registerInterpolation3DIF(Interpolation3DIF
                                        interpolation3DIF) {
    this.interpolation3D = interpolation3DIF;
  }

  public YagiTriLinearInterpolation3D getYagiTriLinearInterpolation3D() {
    return new YagiTriLinearInterpolation3D();
  }

  static enum LayerType {
    Black, Normal, White, Unknow} ;

    public class YagiTriLinearInterpolation3D
        implements Interpolation3DIF {
      short[] nowkey;

      private LayerType getLayerType(short grayLevel) {
        int layerNumber = getLayerNumber(grayLevel);
        if (layerNumber == 0) {
          return LayerType.Black;
        }
        else if (layerNumber < parameter.layerNumber) {
          return LayerType.Normal;
        }
        else if (layerNumber == parameter.layerNumber) {
          return LayerType.White;
        }
        else {
          return LayerType.Unknow;
        }

      }

      private int getLayerNumber(short grayLevel) {
//        if (1023 == grayLevel) {
//          int a = 1;
//        }
        if (grayLevel < blackLimitGrayLevel) {
          return -1;
        }
        else if (grayLevel >= zAxisKeys[0] && grayLevel < zAxisKeys[1]) {
          return 0;
        }
        else if (grayLevel >= zAxisKeys[1] && grayLevel < zAxisKeys[2] ||
                 (1 == parameter.layerNumber &&
                  grayLevel >= zAxisKeys[1] && grayLevel <= whiteLimitGrayLevel)) {
          return 1;
        }
        else if (grayLevel >= zAxisKeys[2] && grayLevel < zAxisKeys[3]) {
          return 2;
        }
        else if (grayLevel >= zAxisKeys[3] && grayLevel <= whiteLimitGrayLevel) {
          return 3;
        }
        else if (grayLevel > whiteLimitGrayLevel) {
          return 4;
        }
        return -1;
      }

      /**
       * interpolateValue
       *
       * @param key short[]
       * @param cell KeyValue[]
       * @return short[]
       */
      public short[] interpolateValue(short[] key, KeyValue[] cell) {
        KeyValue cell0 = cell[0];
        KeyValue cell7 = cell[cell.length - 1];
        int zIndex = 2;
        int delta = (int) (cell7.getKey()[zIndex] - cell0.getKey()[zIndex]);
        short[] R0 = new short[4];
        short[] R1 = new short[4];
        for (int x = 0; x < 4; x++) {
          R0[x] = (short) cell[x].getValue()[0];
          R1[x] = (short) cell[x + 4].getValue()[0];
        }
        nowkey = key;
        short x = (short) (key[0] % blockWidth);
        short y = (short) (key[1] % blockHeight);
        short grayLevel = key[2];
        short level = (short) (grayLevel - cell[0].getKey()[2]);
        int layerNumber = getLayerNumber(grayLevel);
        LayerType layerType = getLayerType(grayLevel);

        short result = -1;

        if (LayerType.Black == layerType) {
          short coef = coefs[0];
          result = trilinearBlackLimit(R1, (short) delta, x, y, level, coef,
                                       offset[0], magnitude[0]);
        }
        else if (LayerType.Normal == layerType) {
          short coef = coefs[layerNumber];
          result = trilinearNormal(R0, R1, (short) delta, x, y, level, coef,
                                   offset[layerNumber - 1],
                                   magnitude[layerNumber - 1],
                                   offset[layerNumber],
                                   magnitude[layerNumber]);
        }
        else if (LayerType.White == layerType) {
          short coef = coefs[layerNumber];
          result = trilinearWhiteLimit(R0, (short) delta, x, y, level, coef,
                                       offset[layerNumber - 1],
                                       magnitude[layerNumber - 1]);
        }

//        int r0 = result;
//
//        if (grayLevel < blackLimitGrayLevel) {
//          result = 0;
//        }
//        else if (grayLevel >= blackLimitGrayLevel && grayLevel < zAxisKeys[1]) {
//          short coef = coefs[0];
//          result = trilinearBlackLimit(R1, (short) delta, x, y, level, coef,
//                                       offset[0], magnitude[0]);
//        }
//        else if (grayLevel >= zAxisKeys[1] && grayLevel < zAxisKeys[2]) {
//          short coef = coefs[1];
//          result = trilinearNormal(R0, R1, (short) delta, x, y, level, coef,
//                                   offset[0], magnitude[0], offset[1],
//                                   magnitude[1]);
//        }
//        else if (grayLevel >= zAxisKeys[2] && grayLevel < zAxisKeys[3]) {
//          short coef = coefs[2];
//          result = trilinearNormal(R0, R1, (short) delta, x, y, level, coef,
//                                   offset[1], magnitude[1], offset[2],
//                                   magnitude[2]);
//        }
//        else if (grayLevel >= zAxisKeys[3] && grayLevel <= whiteLimitGrayLevel) {
//          short coef = coefs[3];
//          result = trilinearWhiteLimit(R0, (short) delta, x, y, level, coef,
//                                       offset[2], magnitude[2]);
//        }
//        else if (grayLevel > whiteLimitGrayLevel) {
//          result = 0;
//        }
//        if (r0 != result) {
//          int a = 1;
//        }

        return new short[] {
            result};
      }

      public YagiTriLinearInterpolation3D() {
        vxh = blockHeight * blockWidth;
      }

//    private int[] mag = {
//        0, 0, 1};
//    private int[] offset = {
//        10, 0, 10};

      private int vxh;

      /**
       *                TotaldeMuraValue(Color) = ((PlaneLevel2 - InputPicture(i, j, Color)) * CLng(deMuravalue(1, Color)) _
       + (InputPicture(i, j, Color) - PlaneLevel1) * CLng(deMuravalue(2, Color))) _
       * (Plane12Coef * 2 + 1) \ (2 ^ 17)
       *
       * @param R0 short[]
       * @param R1 short[]
       * @param delta short
       * @param x short
       * @param y short
       * @param level short
       * @param coef short
       * @param offset0 short
       * @param magnitude0 short
       * @param offset1 short
       * @param magnitude1 short
       * @return short
       */
      private short trilinearNormal(short[] R0, short[] R1, short delta,
                                    short x,
                                    short y, short level, short coef,
                                    short offset0, short magnitude0,
                                    short offset1, short magnitude1) {
        short[] S1234 = getS1234(blockWidth, blockHeight, x, y); //8bit
        short c0 = getDeMuravalue(S1234, R0, vxh, offset0, magnitude0); //8bit
        short c1 = getDeMuravalue(S1234, R1, vxh, offset1, magnitude1);
        int c0c1 = ( (delta - level) * c0 + level * c1);
        short linearValue = (short) (c0c1 * (coef) / Math.pow(2, 16));
//      short linearValue = (short) (c0c1 * (coef * 2 + 1) / Math.pow(2, 17));
        return linearValue;
      }

      /**
       *                 TotaldeMuraValue(Color) = deMuravalue(1, Color) _
                                              - (PlaneLevel1 - InputPicture(i, j, Color)) * PlaneB1Coef * deMuravalue(1, Color) / (2 ^ 15)
       * @param R short[]
       * @param delta short
       * @param x short
       * @param y short
       * @param level short
       * @param coef short
       * @param offset short
       * @param magnitude short
       * @return short
       */
      private short trilinearBlackLimit(short[] R, short delta, short x,
                                        short y, short level, short coef,
                                        short offset, short magnitude) {
        short[] S1234 = getS1234(blockWidth, blockHeight, x, y); //8bit
        short c = getDeMuravalue(S1234, R, vxh, offset, magnitude); //8bit
//      int x1_x_dt0_x_x0_dt1 = (delta - level);
//      int x1_x_dt0_x_x0_dt1_pcoeff = x1_x_dt0_x_x0_dt1 * coef;
        int linearValue0 = ( (delta - level) * coef * c);
        //dt2 + K x { (x1-x)dt0 + (x-x0)dt1 } pcoeff >> 16 = linearValue1


        //========================================================================
        //無條件捨去
        short linearValue1 = (short) (linearValue0 / Math.pow(2, 16));
        short linearValue = (short) (c - linearValue1);

        //========================================================================
        //四捨五入
//      double linearValue1_ = linearValue0 / Math.pow(2, 15);
//      short linearValue_ = (short) Math.round(c - linearValue1_);
        //========================================================================
//      linearValue = (linearValue < 0) ? 0 : linearValue;

//      if (nowkey[0] % 4 == 0 && nowkey[1] == 0) {
//        int orgGrayLevel = nowkey[2] * 4;
//        int graylevel = (nowkey[2] * 4 + linearValue);
//        String hex = Integer.toHexString(graylevel);
////        String hex2 = Integer.toHexString(68719476736+linearValue0);
////        String hex2 = Long.toHexString(68719476736L + linearValue0);
////        int a = graylevel;
////        System.out.println(index + " " + orgGrayLevel + " " + graylevel + " " +
////                           linearValue + " " + linearValue1 + " " +
////                           linearValue0 + " " + c + " : " + S1234[0] + " " +
////                           S1234[1] + " " + S1234[2] + " " + S1234[3] + " " +
////                           linearValue0 * 8 + " " + x1_x_dt0_x_x0_dt1 + " " +
////                           x1_x_dt0_x_x0_dt1_pcoeff);
//        index++;
//      }

        if (MuraCompensationProducer.HWCheck) {
          int a = MuraCompensationProducer.HWCheck_WP;
          int gl = MuraCompensationProducer.HWCheck_GrayLevel;
          System.out.println(a + " " + c + " " + linearValue0 + " " +
                             linearValue1 + " " + linearValue + " " + S1234[0] +
                             " " + S1234[1] + " " + S1234[2] + " " + S1234[3] +
                             " " + (delta - level) * c + " " +
                             (delta - level) * c * 4 + " " +
                             gl + " " + (gl * 4 + linearValue));
        }

        return linearValue;
      }

      int index = 1;

      /**
       TotaldeMuraValue(Color) = deMuravalue(3, Color) _
                                              - (InputPicture(i, j, Color) - PlaneLevel3) * Plane3WCoef * deMuravalue(3, Color) \ (2 ^ 15)
       * @param R short[]
       * @param delta short
       * @param x short
       * @param y short
       * @param level short
       * @param coef short
       * @param offset short
       * @param magnitude short
       * @return short
       */
      private short trilinearWhiteLimit(short[] R, short delta, short x,
                                        short y,
                                        short level, short coef, short offset,
                                        short magnitude) {
        short[] S1234 = getS1234(blockWidth, blockHeight, x, y); //8bit
        short c = getDeMuravalue(S1234, R, vxh, offset, magnitude); //8bit
        int linearValue0 = (level * coef * c);
        int linearValue1 = (int) (linearValue0 / Math.pow(2, 16));
        short linearValue = (short) (c - linearValue1);

        if (MuraCompensationProducer.HWCheck) {
          int pixel = MuraCompensationProducer.HWCheck_WP;
          int gl = MuraCompensationProducer.HWCheck_GrayLevel;
          int output = gl * 4 + linearValue;
          System.out.println(pixel + " " + c + " " + linearValue0 + " " +
                             linearValue1 + " " + linearValue + " " + S1234[0] +
                             " " + S1234[1] + " " + S1234[2] + " " + S1234[3] +
                             " " + (delta - level) * c + " " +
                             (delta - level) * c * 4 + " " +
                             gl + " " + output + " " + R[0] + " " + R[1] + " " +
                             R[2] + " " + R[3]);
        }

        return linearValue;
      }

      private short[] getS1234(int h, int v, int x, int y) {
        short[] result = new short[] {
            (short) (x * y), //4+4=8
            (short) ( (h - x) * y),
            (short) (x * (v - y)),
            (short) ( (h - x) * (v - y))
        };
        return result;
      }

      private short getDeMuravalue(short[] S, short[] R, int vxh, short offset,
                                   short magnitude) {
        //yagi-san: 00 10 01 11 (rg)
        int s = (S[3] * R[0] + S[2] * R[1] + S[1] * R[2] + S[0] * R[3]);
        int base = (int) (s * Math.pow(2, magnitude) / vxh);
//        int _offset = Constant.CompareWithYagi ?
//            (int) (offset * Math.pow(2, magnitude)) : offset;
        int result = base + offset;
        return (short) result;
      }

    }

    protected BlockInterpolation3DIF blockInterpolation3D;
    protected Interpolation3DIF interpolation3D;
    protected CubeTable keyValuePairs;

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

    protected CubeTable.KeyValue[] getKeyValueInterpolateCell(short[] key) {
      double[] doublekey = toDoubleKey(key);
      CubeTable.KeyValue[] cell = getKeyValueInterpolateCell(doublekey);
      return cell;
    }

    private short[] magnitude = {
        0, 0, 1};
    private short[] offset = {
        10, 0, -10};

    public void setMagnitude(short[] magnitude) {
      this.magnitude = magnitude;
    }

    public void setOffset(short[] offset) {
      this.offset = offset;
    }

    //  private int lowerPlaneIndex;

    protected CubeTable.KeyValue[] getKeyValueInterpolateCell(double[] key) {
      int[] nodeKeyIndex = keyValuePairs.getNearestNodeKeyIndex(key);
      //========================================================================
//    if (showCellChange && null != preNodeKeyIndex &&
//        !Arrays.equals(preNodeKeyIndex, nodeKeyIndex)) {
//      System.out.println("changed: " + IntArray.toString(preNodeKeyIndex) +
//                         "->" + IntArray.toString(nodeKeyIndex));
//    }
//    preNodeKeyIndex = nodeKeyIndex;
//========================================================================

      int[] C = getCoordinateIndex(nodeKeyIndex);
//    lowerPlaneIndex = C[2];

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

    private static double[] toDoubleKey(short[] key) {
      int size = key.length;
      double[] doublekey = new double[size];
      for (int x = 0; x < size; x++) {
        doublekey[x] = key[x];
      }
      return doublekey;
    }

    public final short[] getValues(short[] key) {
      short[] values = null;
      if (null != blockInterpolation3D) {
        CubeTable.KeyValue[] cell = blockInterpolation3D.
            getKeyValueInterpolateCell();
        values = blockInterpolation3D.interpolateValue(key, cell);
      }
      else if (null != interpolation3D) {

        CubeTable.KeyValue[] cell = getKeyValueInterpolateCell(key);
        values = interpolation3D.interpolateValue(key, cell);
      }
      else {
        throw new IllegalStateException("");
      }
      return values;
    }

    public short[] getCoefs() {
      return coefs;
    }

    public int getWhiteLimitGrayLevel() {
      return whiteLimitGrayLevel;
    }

    public int getBlackLimitGrayLevel() {
      return blackLimitGrayLevel;
    }

    public short[] getMagnitude() {
      return magnitude;
    }

    public short[] getOffset() {
      return offset;
    }

    public static void main(String[] args) {
      System.out.println( (int) 0.4);
      System.out.println( (int) 0.9);
    }
  }
