package shu.cms.devicemodel.lcd.xtalk;

import shu.cms.colorspace.depend.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * xtalk效應的消除器, 用來記錄xtalk的影響
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class XTalkEliminator {

    private static enum Mode {

        ThreeLut, OneLut
    }
    private Mode mode;
    private Interpolation2DLUT rLut;
    private Interpolation2DLUT gLut;
    private Interpolation2DLUT bLut;
    TouchedLut touchedLut;

    /**
     *
     * @param adjacentValues double[] 所有鄰接值
     * @param selfValues double[] 所有自身值
     * @param rCorrectionValues double[][] r修正值
     * @param gCorrectionValues double[][] g修正值
     * @param bCorrectionValues double[][] b修正值
     */
    protected XTalkEliminator(double[] adjacentValues, double[] selfValues,
            double[][] rCorrectionValues,
            double[][] gCorrectionValues,
            double[][] bCorrectionValues) {
        this(adjacentValues, selfValues, rCorrectionValues, gCorrectionValues,
                bCorrectionValues, positiveFixLUT);
    }

    protected XTalkEliminator(double[] adjacentValues, double[] selfValues,
            double[][] rCorrectionValues,
            double[][] gCorrectionValues,
            double[][] bCorrectionValues, boolean fix) {
        this(adjacentValues, selfValues, rCorrectionValues,
                adjacentValues, selfValues, gCorrectionValues,
                adjacentValues, selfValues, bCorrectionValues, fix);
    }
    double[][][] correctLut;

//  protected XTalkEliminator(double[] rAdjacentValues,
//                            double[] rSelfValues,
//                            double[][] rCorrectionValues,
//                            double[] gAdjacentValues,
//                            double[] gSelfValues,
//                            double[][] gCorrectionValues,
//                            double[] bAdjacentValues,
//                            double[] bSelfValues,
//                            double[][] bCorrectionValues) {
//    this(rAdjacentValues, rSelfValues, rCorrectionValues, gAdjacentValues,
//         gSelfValues, gCorrectionValues, bAdjacentValues, bSelfValues,
//         bCorrectionValues, fixLUT);
//  }
    /**
     * 使用RGB對照表的形式
     * @param rAdjacentValues double[]
     * @param rSelfValues double[]
     * @param rCorrectionValues double[][]
     * @param gAdjacentValues double[]
     * @param gSelfValues double[]
     * @param gCorrectionValues double[][]
     * @param bAdjacentValues double[]
     * @param bSelfValues double[]
     * @param bCorrectionValues double[][]
     * @param fix boolean
     */
    protected XTalkEliminator(double[] rAdjacentValues,
            double[] rSelfValues,
            double[][] rCorrectionValues,
            double[] gAdjacentValues,
            double[] gSelfValues,
            double[][] gCorrectionValues,
            double[] bAdjacentValues,
            double[] bSelfValues,
            double[][] bCorrectionValues, boolean fix) {
        this.mode = Mode.ThreeLut;
        this.correctLut = new double[][][]{
                    rCorrectionValues, gCorrectionValues, bCorrectionValues};
        /**
         * 內插法採用bicubic效果比bilinear好很多
         * adjacent是x(橫), self是y(直)
         */
        rLut = new Interpolation2DLUT(rAdjacentValues, rSelfValues,
                rCorrectionValues, interpAlgo);
        gLut = new Interpolation2DLUT(gAdjacentValues, gSelfValues,
                gCorrectionValues, interpAlgo);
        bLut = new Interpolation2DLUT(bAdjacentValues, bSelfValues,
                bCorrectionValues, interpAlgo);
        fixLUT(rCorrectionValues, fix, false);
        fixLUT(gCorrectionValues, fix, false);
        fixLUT(bCorrectionValues, fix, false);
        touchedLut = new TouchedLut(rAdjacentValues, rSelfValues, gAdjacentValues,
                gSelfValues, bAdjacentValues, bSelfValues);
    }

    static class TouchedLut {

        private int[][] rLut;
        private int[][] gLut;
        private int[][] bLut;
        private double[][] rValues;
        private double[][] gValues;
        private double[][] bValues;

        private TouchedLut(double[] rAdjacentValues,
                double[] rSelfValues,
                double[] gAdjacentValues,
                double[] gSelfValues,
                double[] bAdjacentValues,
                double[] bSelfValues) {
            int rWidth = rAdjacentValues.length;
            int rHeight = rSelfValues.length;
            int gWidth = gAdjacentValues.length;
            int gHeight = gSelfValues.length;
            int bWidth = bAdjacentValues.length;
            int bHeight = bSelfValues.length;
            rLut = new int[rWidth][rHeight];
            gLut = new int[gWidth][gHeight];
            bLut = new int[bWidth][bHeight];
//      touchedLut = new int[][][] {
//          rLut, gLut, bLut};
            rValues = new double[][]{
                        rAdjacentValues, rSelfValues};
            gValues = new double[][]{
                        gAdjacentValues, gSelfValues};
            bValues = new double[][]{
                        bAdjacentValues, bSelfValues};
        }

        double[][][] getTouchedLUT() {
            double[][][] touchedLut = new double[][][]{
                IntArray.toDoubleArray(rLut), IntArray.toDoubleArray(gLut),
                IntArray.toDoubleArray(bLut)
            };
            return touchedLut;
        }

        private double[][] getValues(RGBBase.Channel selfChannel) {
            switch (selfChannel) {
                case R:
                    return rValues;
                case G:
                    return gValues;
                case B:
                    return bValues;
                default:
                    return null;
            }
        }

        private int[][] getLut(RGBBase.Channel selfChannel) {
            switch (selfChannel) {
                case R:
                    return rLut;
                case G:
                    return gLut;
                case B:
                    return bLut;
                default:
                    return null;
            }

        }

        private void touched(RGBBase.Channel selfChannel,
                double selfValue,
                double adjacentValue) {
            double[][] values = getValues(selfChannel);

            //x的根
            int xIndex = Searcher.leftBinarySearch(values[0], adjacentValue);
            //y的根
            int yIndex = Searcher.leftBinarySearch(values[1], selfValue);
            int[][] lut = getLut(selfChannel);
            if (xIndex == -1 || yIndex == -1) {
                System.out.println("");
            }
            lut[xIndex][yIndex]++;
            lut[xIndex + 1][yIndex]++;
            lut[xIndex][yIndex + 1]++;
            lut[xIndex + 1][yIndex + 1]++;
        }
    }

    /**
     * 僅使用單一對照表的形式
     * @param adjacentValues double[]
     * @param selfValues double[]
     * @param correctionValues double[][]
     * @param fix boolean
     */
    protected XTalkEliminator(double[] adjacentValues,
            double[] selfValues,
            double[][] correctionValues, boolean fix) {
        this.mode = Mode.OneLut;
        this.correctLut = new double[][][]{
                    correctionValues};
        /**
         * 內插法採用bicubic效果比bilinear好很多
         */
        rLut = new Interpolation2DLUT(adjacentValues, selfValues,
                correctionValues, interpAlgo);
        fixLUT(correctionValues, fix, false);
    }
    /**
     * 將對照表修正為全正值
     */
    protected final static boolean positiveFixLUT = true;

    /**
     * 修正不合理的修正值
     * @param values double[][]
     * @param positiveFix boolean 修正後只留下正值
     * @param negativeFix boolean 修正後只留下負值
     */
    protected final static void fixLUT(double[][] values, boolean positiveFix,
            boolean negativeFix) {
        int height = values.length;
        int width = values[0].length;

        if (!positiveFix && !negativeFix) {
            return;
        }

        /**
         * @note 目前採用最簡單的修正方式, 把負修正的部份移除掉, 不敢保證其正確性, 有必要再驗證
         */
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                if (positiveFix) {
                    values[x][y] = values[x][y] < 0 ? 0 : values[x][y];
                }
                if (negativeFix) {
                    values[x][y] = values[x][y] > 0 ? 0 : values[x][y];
                }
            }
        }

    }
    /**
     * linear的二維內差結果最佳
     */
    protected final static Interpolation2DLUT.Algo interpAlgo =
            Interpolation2DLUT.Algo.BILINEAR;

    public double getCorrectionValue(RGBBase.Channel selfChannel,
            double selfValue,
            double adjacentValue) {
        if (this.mode != Mode.ThreeLut) {
            throw new IllegalStateException("this.mode != Mode.ThreeLut");
        }
        if (selfValue == 0 || adjacentValue == 0) {
            return 0;
        }
        touchedLut.touched(selfChannel, selfValue, adjacentValue);
        switch (selfChannel) {
            case R:
                return rLut.getValue(adjacentValue, selfValue);
            case G:
                return gLut.getValue(adjacentValue, selfValue);
            case B:
                return bLut.getValue(adjacentValue, selfValue);
            default:
                throw new IllegalArgumentException("selfChannel != R/G/B");
        }
    }

    public double getCorrectionValue(double selfValue,
            double adjacentValue) {
        if (this.mode != Mode.OneLut) {
            throw new IllegalStateException("this.mode != Mode.OneLut");
        }
        if (selfValue == 0 || adjacentValue == 0) {
            return 0;
        }
        return rLut.getValue(adjacentValue, selfValue);
    }
}
