package auo.mura.interp;

import auo.math.lut.CubeTable;
import auo.math.lut.CubeTable.KeyValue;
import auo.mura.DeMuraParameter;
import auo.mura.MuraCompensationProducer;
import shu.cms.colorspace.depend.RGBBase;

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
    private short[] zAxisKeys;
    public ThreeDInterpolation(CubeTable keyValuePairs,
                               DeMuraParameter parameter) {
        this.keyValuePairs = keyValuePairs;
        this.parameter = parameter;
        this.blackLimitGrayLevel = parameter.blackLimit;
        this.whiteLimitGrayLevel = parameter.whiteLimit;

        int layerNumber = parameter.layerNumber;
        zAxisKeys = new short[layerNumber + 2];
        zAxisKeys[0] = blackLimitGrayLevel;
        for (int x = 0; x < layerNumber; x++) {
            zAxisKeys[x + 1] = parameter.planeLevel[x];
        }
        zAxisKeys[layerNumber + 1] = whiteLimitGrayLevel;
        this.initCoefs(parameter);
    }

    private DeMuraParameter parameter;


    private void initCoefs(DeMuraParameter parameter) {
        coefs = parameter.planeCoef;
        this.setOffset(parameter.dataOffset);
        this.setMagnitude(parameter.dataMag);
        this.setBlockSize(parameter.vBlockSize, parameter.hBlockSize);
    }


    private short[] coefs;
    private short blackLimitGrayLevel, whiteLimitGrayLevel;
    private int blockHeight, blockWidth;
    protected Interpolation3DIF interpolation3D;
    protected CubeTable keyValuePairs;
    private short[] magnitude = {0, 0, 0};
    private short[] offset = {0, 0, 0};

    public void setBlockSize(int height, int width) {
        this.blockHeight = height;
        this.blockWidth = width;
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


        public static void main(String[] args) {
            System.out.println(1 << 1);
            System.out.println(1 << 2);
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


        public void setMagnitude(short[] magnitude) {
            this.magnitude = magnitude;
        }

        public void setOffset(short[] offset) {
            this.offset = offset;
        }

        protected CubeTable.KeyValue[] getKeyValueInterpolateCell(short[] key) {
            int[] nodeKeyIndex = keyValuePairs.getNearestNodeKeyIndex(key);

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


        public final short[] getValues(short[] key, RGBBase.Channel ch) {
            short[] values = null;
            if (null != interpolation3D) {
                CubeTable.KeyValue[] cell = getKeyValueInterpolateCell(key);
                values = interpolation3D.interpolateValue(key, cell, ch);
            } else {
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


        public class YagiTriLinearInterpolation3D implements Interpolation3DIF {

            private LayerType getLayerType(short grayLevel) {
                int layerNumber = getLayerNumber(grayLevel);
                if (layerNumber == 0) {
                    return LayerType.Black;
                } else if (layerNumber < parameter.layerNumber) {
                    return LayerType.Normal;
                } else if (layerNumber == parameter.layerNumber) {
                    return LayerType.White;
                } else {
                    throw new IllegalArgumentException("");
                }

            }

            private int getLayerNumber(short grayLevel) {
                /**
                 *         zAxisKeys = new short[layerNumber + 2];
                         zAxisKeys[0] = blackLimitGrayLevel;
                         for (int x = 0; x < layerNumber; x++) {
                             zAxisKeys[x + 1] = parameter.planeLevel[x];
                         }
                         zAxisKeys[layerNumber + 1] = whiteLimitGrayLevel;

                 */
                for (int n = 0; n < zAxisKeys.length - 1; n++) {
                    if (n == parameter.layerNumber && grayLevel >= zAxisKeys[n] && grayLevel <= zAxisKeys[n + 1]) {
                        return n;
                    } else if (grayLevel >= zAxisKeys[n] && grayLevel < zAxisKeys[n + 1]) {
                        return n;
                    }
                }
                return -1;
            }

            public short[] interpolateValue(short[] key, KeyValue[] cell, RGBBase.Channel ch) {
                KeyValue cell0 = cell[0];
                KeyValue cell7 = cell[cell.length - 1];
                int zIndex = 2;
                int delta = (int) (cell7.getKey()[zIndex] -
                                   cell0.getKey()[zIndex]);
                short[] R0 = new short[4];
                short[] R1 = new short[4];
                for (int x = 0; x < 4; x++) {
                    R0[x] = (short) cell[x].getValue()[ch.getArrayIndex()];
                    R1[x] = (short) cell[x + 4].getValue()[ch.getArrayIndex()];
                }
                short x = (short) (key[0] % blockWidth);
                short y = (short) (key[1] % blockHeight);
                short grayLevel = key[2];
                short level = (short) (grayLevel - cell[0].getKey()[2]);

                //取出係數用的index
                int layerNumber = getLayerNumber(grayLevel);
                //判斷內插的方法
                LayerType layerType = getLayerType(grayLevel);

                short result = -1;

                if (LayerType.Black == layerType) {
                    short coef = coefs[0];
                    result = trilinearBlackLimit(R1, (short) delta, x, y, level,
                                                 coef,
                                                 offset[0], magnitude[0]);
                } else if (LayerType.Normal == layerType) {
                    short coef = coefs[layerNumber];
                    result = trilinearNormal(R0, R1, (short) delta, x, y, level,
                                             coef,
                                             offset[layerNumber - 1],
                                             magnitude[layerNumber - 1],
                                             offset[layerNumber],
                                             magnitude[layerNumber]);
                } else if (LayerType.White == layerType) {
                    short coef = coefs[layerNumber];
                    result = trilinearWhiteLimit(R0, (short) delta, x, y, level,
                                                 coef,
                                                 offset[layerNumber - 1],
                                                 magnitude[layerNumber - 1]);
                }

                return new short[] {
                        result};
            }


            public YagiTriLinearInterpolation3D() {
                vxh = blockHeight * blockWidth;
            }

            protected int vxh;
            private int two_16 = (int) Math.pow(2, 16);

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
                int c0c1 = ((delta - level) * c0 + level * c1);
                short linearValue = (short) (c0c1 * (coef) / two_16);

                if (MuraCompensationProducer.HWCheck) {

                }

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
                int linearValue0 = ((delta - level) * coef * c);
                //dt2 + K x { (x1-x)dt0 + (x-x0)dt1 } pcoeff >> 16 = linearValue1


                //========================================================================
                //無條件捨去
                short linearValue1 = (short) (linearValue0 / two_16);
                short linearValue = (short) (c - linearValue1);

                return linearValue;
            }

//            int index = 1;

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
                                              short level, short coef,
                                              short offset,
                                              short magnitude) {

                short[] S1234 = getS1234(blockWidth, blockHeight, x, y); //8bit
                short c = getDeMuravalue(S1234, R, vxh, offset, magnitude); //8bit
                int linearValue0 = (level * coef * c);
//                int linearValue0 = ((delta - level) * coef * c);

                int linearValue1 = (int) (linearValue0 / two_16);
                short linearValue = (short) (c - linearValue1);

                if (MuraCompensationProducer.HWCheck) {

                }

                return linearValue;
            }

            protected short[] getS1234(int h, int v, int x, int y) {
                short[] result = new short[] {
                                 (short) (x * y), //4+4=8
                                 (short) ((h - x) * y),
                                 (short) (x * (v - y)),
                                 (short) ((h - x) * (v - y))
                };
                return result;
            }

            protected short getDeMuravalue(short[] S, short[] R, int vxh,
                                           short offset,
                                           short magnitude) {
                //yagi-san: 00 10 01 11 (rg)
                int s = (S[3] * R[0] + S[2] * R[1] + S[1] * R[2] + S[0] * R[3]);
                int mag = 1 << magnitude;
                int base = (int) (s * mag / vxh);
                int result = base + offset;
                return (short) result;
            }

        }


        public class TriLinearInterpolation3D_14Bit extends YagiTriLinearInterpolation3D {
            private boolean ks22;
            public TriLinearInterpolation3D_14Bit(boolean ks22) {
                this.ks22 = ks22;
                if (ks22) {
                    two = (int) Math.pow(2, 22);
                } else {
                    two = (int) Math.pow(2, 20);
                }
            }

            private final int two;

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
                                          short x, short y, short level, short coef,
                                          short offset0, short magnitude0,
                                          short offset1, short magnitude1) {
                short[] S1234 = getS1234(blockWidth, blockHeight, x, y); //8bit
                short c0 = getDeMuravalue(S1234, R0, vxh, offset0, magnitude0); //8bit
                short c1 = getDeMuravalue(S1234, R1, vxh, offset1, magnitude1);
                int c0c1 = ((delta - level) * c0 + level * c1);
                short linearValue = (short) (c0c1 * (coef) / two);

                if (MuraCompensationProducer.HWCheck) {

                }

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
                int linearValue0 = ((delta - level) * coef * c);

                //========================================================================
                //無條件捨去
                short linearValue1 = (short) (linearValue0 / two);
                short linearValue = (short) (c - linearValue1);

                return linearValue;
            }

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
                                              short level, short coef,
                                              short offset,
                                              short magnitude) {

                short[] S1234 = getS1234(blockWidth, blockHeight, x, y); //8bit
                short c = getDeMuravalue(S1234, R, vxh, offset, magnitude); //8bit
                int linearValue0 = (level * coef * c);

                int linearValue1 = (int) (linearValue0 / two);
                short linearValue = (short) (c - linearValue1);

                if (MuraCompensationProducer.HWCheck) {

                }

                return linearValue;
            }


        }
    }
