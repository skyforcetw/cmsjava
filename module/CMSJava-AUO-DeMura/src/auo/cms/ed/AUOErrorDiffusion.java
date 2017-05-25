package auo.cms.ed;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


import auo.mura.util.ArrayUtils;
import auo.cms.frc.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AUOErrorDiffusion {

    //==============================================================================================
    // 沒有特別用途不需使用, hardware並不使用到以下選項
    //==============================================================================================

    //==============================================================================================

    //==============================================================================================
    // 通用選項
    //==============================================================================================

    private static boolean randomThreshold = true;
    private static int randomThresholdValue = 0;

    private static boolean noErrorWhenBlack = false; //false for normal, true for check
    private static boolean w135Floor = false;
    //是否照書上的weighting傳遞方式
    private static boolean bookWeighting = false;
    //==============================================================================================

    //==============================================================================================
    // hardWareThresholdModulation Only
    //==============================================================================================
    //模擬hardware處理方式, 以line為基礎的ED
    private static boolean lineBasedWeighting = true;
    //智哲提出的random
    private static boolean ccRandom = false;
    //順廷提出的random
    private static boolean stRandom = true;
    //順廷提出的threshold方式
    private static boolean stThreshold = true;
    protected static int stThresholdCount = 0;
    //==============================================================================================

    public static double[] error0Weighting = null;

    private short[][] errorDiffusion(short[][] image12bit, int edbit) {

        // http://caca.zoy.org/study/part3.html
        if (Matrix.FloydSteinbergIdeal == matrix) {
            return floydSteinbergIdeal(image12bit);
        } else if (Matrix.Hardware_2_ == matrix) {
            return hardWareFloydSteinberg_2_(image12bit, edbit);
        } else if (Matrix.HardwareThresholdModulation == matrix) {
            //向右直接採1/2的運算
            boolean useHalfWeighting = true;
            double[][] weightingsForModulation = null;
            int[] strengthForModulation = null;

            if (useHalfWeighting) {

                weightingsForModulation = new double[][] {
                                          //====================================
                                          // error 0
                                          //====================================
//                                          {64, 16, 48}, //error0 for half
//                                          {56, 32, 40}, //error0 for 3 weighting
                                          (error0Weighting != null ? error0Weighting :
//                                           new double[] {56, 32, 40}), //error0 for 745
//                                           new double[] {64, 16, 48}), //half for original
                                           new double[] {64, 43, 21}), //half for best
                                          //====================================
                                          {64, 30, 34}, {64, 14, 50},
                                          //====================================
                                          //error 3
                                          //====================================
                                          {64, 17, 47}, //good
//                                          {64, 47, 17}, //bad
                                          //====================================
                                          {64, 27, 37}, {64, 34, 30}, {64, 25, 39}, {64, 7, 57}, {64, 16, 48}, {64, 7,
                                          57}, {64, 25, 39}, {64,
                                          34, 30}, {64, 27, 37},
                                          //====================================
                                          // error 13
                                          //====================================
                                          {64, 47, 17},
//                                          {64, 17, 47}, //為了對稱, 但是效果不彰
                                          //====================================
                                          {64, 14, 50},
                                          //====================================
                                          // error 15
                                          //====================================
//                                          {64,  64,0} // BEST for error 15 and had zero
                                          {64, 55, 9} //BEST for error 15 and non zero
                                          //====================================
                };

                weightingsForModulation = fullWeightingBase2Weighting(weightingsForModulation);
                strengthForModulation = new int[] {
                                        //======================================
                                        //error0 modulation threshold
                                        1, //original
                                        //======================================
                                        2, 2, 2, 1, 2,
                                        2, 2, 4, 2, 3,
                                        2, 1, 2, 1, 1
                }; //opt for 1/2 only

                //==============================================================
                //為了讓error0更容易進位, 所以base設14
//                error0ModualtionBase = 14;
                //==============================================================
//                }; //opt for 1/2 only, test purpose

                boolean verifyWithHardware = false;
                if (verifyWithHardware) {

                    weightingsForModulation = null;
                    strengthForModulation = null;

                }

            } else { //!useHalfWeighting

                weightingsForModulation = new double[][] { {7, 4, 5}, {48, 38, 42}, {48, 32, 48}, {60, 25, 43}, {64, 27,
                                          37}, {60, 31, 37}, {59, 29, 40}, {44, 41, 43}, {88, 20, 20}, {44, 41, 43},
                                          {59, 29, 40}, {60, 31, 37}, {64, 27, 37}, {60, 25, 43}, {48, 32, 48}, {48, 38,
                                          42},

                }; //opt for full2

                weightingsForModulation = fullWeightingBase2Weighting(weightingsForModulation);
                strengthForModulation = new int[] {1, 4, 2, 1, 1, 2, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1}; //org opt+manual2

            }

            short[] modualtionBases = {16,
                                      16, 16, 16, 16, 16,
                                      16, 16, 16, 16, 16,
                                      16, 16, 16, 16, 16,
            };

            return hardWareFloydSteinberg_hardWareThresholdModulation(image12bit, edbit, weightingsForModulation,
                    strengthForModulation, modualtionBases, true, randomThreshold);

        } else {
            return null;
        }

    }

    public short[][][] ed10bit(short[][][] image12bit, PatternType type) {
        switch (type) {
        case PixelBase:
            return pixelBaseED10bit(image12bit);
        case SubPixelBase:
            return subPixelBaseED10bit(image12bit);
        default:
            return null;
        }
    }


    public short[][][] ed8bit(short[][][] image12bit, PatternType type) {
        switch (type) {
        case PixelBase:
            return pixelBaseED8bit(image12bit);
        case SubPixelBase:
            return subPixelBaseED8bit(image12bit);
        default:
            return null;
        }
    }

    public short[][] pixelBaseED10bit(short[][] image12bit) {
        return ed10bit1(image12bit);
    }

    public short[][] pixelBaseED8bit(short[][] image12bit) {
        return ed8bit0(image12bit);
    }


    public short[][][] pixelBaseED10bit(short[][][] image12bit) {
        short[][] r = ed10bit1(image12bit[0]);
        short[][] g = ed10bit1(image12bit[0]);
        short[][] b = ed10bit1(image12bit[0]);
        short[][][] result = new short[][][] {r, g, b};
        return result;
    }

    public short[][][] pixelBaseED8bit(short[][][] image12bit) {
        short[][] r = ed8bit0(image12bit[0]);
        short[][] g = ed8bit0(image12bit[0]);
        short[][] b = ed8bit0(image12bit[0]);
        short[][][] result = new short[][][] {r, g, b};
        return result;
    }


    public short[][][] subPixelBaseED10bit(short[][][] image12bit) {
        short[][] oneImage = ArrayUtils.rgb2One(image12bit);
        short[][] oneImageED = ed10bit1(oneImage);
        short[][][] image10bitED = ArrayUtils.one2RGB(oneImageED);

        return image10bitED;
    }

    public short[][][] subPixelBaseED8bit(short[][][] image12bit) {
        short[][] oneImage = ArrayUtils.rgb2One(image12bit);
        short[][] oneImageED = ed8bit0(oneImage);
        short[][][] image8bitED = ArrayUtils.one2RGB(oneImageED);

        return image8bitED;
    }


    private short[][] ed8bit0(short[][] image12bit) {
        return errorDiffusion(image12bit, 4);
    }

    private short[][] ed10bit1(short[][] image12bit) {
        return errorDiffusion(image12bit, 2);
    }


    public static short[][] floydSteinbergIdeal(short[][] image12bit) {
        short[] matrix = new short[] {1, 5, 3, 7};
        return floydSteinbergIdeal(image12bit, matrix);
    }

    public static short[][] floydSteinbergIdeal(short[][] image12bit,
                                                short[] matrix) {
        int height = image12bit.length;
        int width = image12bit[0].length;

        int bit_AV = 8;
        int bitpower = (int) Math.pow(2, bit_AV);

        double[][] input = new double[height][width];
        double[][] error = new double[height][width];
        short[][] output = new short[height][width];
        double[][] w7 = new double[height][width];
        double[][] w153 = new double[height][width];
        double[][] preerror = new double[height][width];
        double[][] total = new double[height][width];
        double[][] total2 = new double[height][width];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                input[h][w] = image12bit[h][w] / 16.;
            }
        }
        boolean w7_Y8 = false, w1_Y26 = false;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (h > 0) {
                    double e1 = 0, e3 = 0, e5 = 0;
                    //w5w1
                    if (w > 0) {
                        e1 = (error[h - 1][w - 1] - output[h - 1][w - 1]) *
                             matrix[0];
                    }
                    e5 = (error[h - 1][w] - output[h - 1][w]) * matrix[1];
                    if (w < width - 1) {
                        e3 = (error[h - 1][w + 1] - output[h - 1][w + 1]) *
                             matrix[2];
                    }
                    w153[h][w] = ((e1 + e5 + e3) * 16);

                    if (!w1_Y26 && 0 == w) {
                        w153[h][w] = 0;
                    }
                    preerror[h][w] = (w153[h][w] % bitpower / 16); //bitpower=256

                    if (noErrorWhenBlack && 0 == input[h][w]) {
                        preerror[h][w] = 0;
                    }

                }

                if (w > 0 && h < height) {
                    w7[h][w] = ((error[h][w - 1] -
                                 output[h][w - 1]) * matrix[3]);
                    if (!w7_Y8 && 0 == h) {
                        w7[h][w] = 0;
                    }

                }

                total[h][w] = w7[h][w] + preerror[h][w] +
                              input[h][w] * 16;
                total2[h][w] = total[h][w] / 16;
                error[h][w] = total2[h][w];

                output[h][w] = (short) Math.floor(error[h][w]);

            }
        }
        checksum = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                output[h][w] = (short) (output[h][w] * 16);
                checksum += output[h][w];
            }
        }
        return output;
    }

    public static byte[][] hardwareRemainError;
    protected static byte[][] hardwareTotalError;
    public static short[][] hardwareThreshold;
    protected static short[][] hardwareErrW135;
    protected static short[][][] hardwareErr135;

    public static short[][] toShortArray(byte[][] bytearray) {
        int height = bytearray.length;
        int width = bytearray[0].length;
        short[][] result = new short[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[h][w] = bytearray[h][w];
            }
        }
        return result;
    }

    public static void clearStaticData() {
        hardwareRemainError = null;
        hardwareTotalError = null;
        hardwareThreshold = null;
        hardwareErrW135 = null;
        hardwareErr135 = null;
    }

    /**
     * 與硬體完全相同的演算方法
     * @param image12bit short[][]
     * @param edbit int
     * @return short[][]
     */
    public static short[][] hardWareFloydSteinberg_2_(short[][] image12bit,
            int edbit) {
        int height = image12bit.length;
        int width = image12bit[0].length;

        short[][] input = new short[height][width];
        byte[][] error = new byte[height][width];
        short[][] output = new short[height][width];

        short base = (short) Math.pow(2, edbit);

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                input[h][w] = (short) (image12bit[h][w] % base);
                output[h][w] = 0;
            }
        }

        short err3 = 0, err5 = 0, err1 = 0, err = 0, err7;
        boolean prelineIsBlack = false;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (bookWeighting) {
                    err1 = w > 0 && h > 0 ? error[h - 1][w - 1] : 0;
                    err5 = (h > 0 && w < width - 1) ? error[h - 1][w] : 0;
                    err5 = (h > 0 && w < width) ? error[h - 1][w] : 0; //?
                    err3 = (w < width - 1 && h > 0) ? error[h - 1][w + 1] : 0;
                    err7 = (w > 0) ?
                           (short) Math.round(error[h][w - 1] * 7 / 16.) : 0;
                } else {
                    err1 = w > 0 && h > 0 ? error[h - 1][w - 1] : 0;
                    err5 = (h > 0 && w < width) ? error[h - 1][w] : 0;
                    err3 = (w < width - 1 && h > 0) ? error[h - 1][w + 1] : 0;
                    err7 = (w > 0) ?
                           (short) Math.round(error[h][w - 1] * 7 / 16.) : 0;

                }

                if (w135Floor) {
                    err = (short) (Math.floor((err3 * 3 + err5 * 5 + err1) /
                                              16.) + err7 + input[h][w]);

                } else {
                    err = (short) (Math.round((err3 * 3 + err5 * 5 + err1) /
                                              16.) + err7 + input[h][w]);
                }

                if (bookWeighting && (0 == h || 0 == w)) {
                    err = input[h][w];
                }

                if (noErrorWhenBlack && (0 == input[h][w] || prelineIsBlack)) {
                    if (0 == input[h][w]) {
                        err = 0;
                    } else if (prelineIsBlack) {
                        err = input[h][w];
                    }

                }

                if (noErrorWhenBlack && (width - 1 == w) && prelineIsBlack) {
                    prelineIsBlack = false;
                }

                if (noErrorWhenBlack && (width - 1 == w) && 0 == input[h][w]) {
                    prelineIsBlack = true;
                }

                short threshold = base;
                if (err >= threshold) {
                    output[h][w] = 1;
                    error[h][w] = (byte) (err - base);
                } else {
                    output[h][w] = 0;
                    error[h][w] = (byte) err;
                }

            }
        }
        checksum = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                output[h][w] = (short) ((image12bit[h][w] / 16 + output[h][w]) *
                                        16);
                output[h][w] = output[h][w] > 4095 ? 4095 : output[h][w];

                checksum += output[h][w];
            }
        }
        hardwareRemainError = error;

        return output;
    }


    public static short[][] hardWareFloydSteinberg_hardWareThresholdModulation(final short[][]
            image12bit, int edbit, double[][] weightings,
            int[] modulationStrengths, short modualtionBase,
            boolean newModulation, boolean randomThreshold) {
        short m = modualtionBase;
        short[] modualtionBases = {m, m, m, m,
                                  m, m, m, m,
                                  m, m, m, m};
        return hardWareFloydSteinberg_hardWareThresholdModulation(image12bit, edbit, weightings,
                modulationStrengths, modualtionBases, newModulation, randomThreshold);
    }


    static int checksum(short[] ...array) {
        int sum = 0;
        for (short[] d : array) {
            int size = d.length;
            for (int x = 0; x < size; x++) {
                sum += d[x];
            }
        }
        return sum;
    }

    protected static long[] staticSeeds;
    protected static long[] finalSeeds;
    public static short[][] hardWareFloydSteinberg_hardWareThresholdModulation(final short[][]
            image12bit, int edbit, double[][] weightings,
            int[] modulationStrengths, short[] modualtionBases,
            boolean newModulation, boolean randomThreshold) {
        int imagecksum = checksum(image12bit);
        int height = image12bit.length;
        int width = image12bit[0].length;
        int halfWidth = width / 2;

        byte[][] input = new byte[height][width];
        byte[][] remainError = new byte[height][width];
        byte[][] remainError7Only = new byte[height][width];
        byte[][] totalError = new byte[height][width];
        short[][] output = new short[height][width];

        short[][] thresholdArray = new short[height][width];
        short[][] errw135Array = new short[height][width];
        short[][][] err135Array = new short[3][height][width];

        short base = (short) Math.pow(2, edbit);

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                input[h][w] = (byte) (image12bit[h][w] % base);
                output[h][w] = 0;
                totalError[h][w] = 0;
            }
        }

        byte err3 = 0, err5 = 0, err1 = 0, err = 0, err7 = 0, rounderr7 = 0,
                err0 = 0, rounderr0 = 0;
        double weight0 = 0, weight7 = 0, weight3 = 0, weight5 = 0, weight1 = 0;
        double[] weight;
        double totalweight = 0;
        int modulationStrength = 0;
        byte localerror = 0;
        boolean prelineIsBlack = false;

        //==========================================================================================
        // random
        //==========================================================================================
        Random javarandom = new Random(0);
        AUORandom random = AUORandom.getWikiLFSR32_16Instance(7533967);
        AUORandom[] randoms = new AUORandom[4];
        boolean nonSyncSim = false; //模擬左右seed不sync
        AUORandom[] randomsRight = new AUORandom[4];
        long[] seeds = {3939889, 28825252, 4125252, 27662000};
        if (null != staticSeeds) {
            seeds = staticSeeds;
        }
        boolean XOR2LFSR = true; //兩組LFSR做XOR
        boolean useJavaRandom = false;
        boolean simplify = false; //簡化版, 不要用,效果不好.
        final int randomCount = simplify ? 2 : 4;
        boolean showRandomNum = false;
        //==============================================================================================================

        //==============================================================================================================
        // 紀錄random
        //==============================================================================================================
        BufferedWriter randnumwriter = null;
        if (showRandomNum) {
            try {
                randnumwriter = new BufferedWriter(new FileWriter("randnum.txt"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //==========================================================================================


        for (int x = 0; x < randomCount; x++) {
            if (XOR2LFSR) {
                if (x % 2 == 0) {
                    randoms[x] = AUORandom.getWikiLFSRInstance(seeds[x], 32, 32, 30, 26, 25);
                    randomsRight[x] = AUORandom.getWikiLFSRInstance(seeds[x], 32, 32, 30, 26, 25);
                } else {
                    randoms[x] = AUORandom.getWikiLFSRInstance(seeds[x], 31, 31, 30, 29, 28);
                    randomsRight[x] = AUORandom.getWikiLFSRInstance(seeds[x], 31, 31, 30, 29, 28);
                }
            } else {
                randoms[x] = AUORandom.getWikiLFSR32_16Instance(seeds[x]);
            }
        }

        short threshold = 0; //modualtionBase;
        short modualtionBase = 0;
        short[] err135Line = new short[width];
        short maxresult = 0;
        short maxerr = 0;
        boolean wrongWeighting = false;

        for (int h = 0; h < height; h++) {
            for (int windex = 0; windex < width; windex++) {

                int w = windex;
                if (!wrongWeighting) {
                    localerror = input[h][w];
                }
                //0,1 維持原樣, 2~15
                weight = null != weightings ? weightings[localerror] : new double[] {0, 7, 3, 5, 1};
                weight0 = weight[0];
                weight7 = weight[1];
                weight3 = weight[2];
                weight5 = weight[3];
                weight1 = weight[4]; //1在新的weighting是沒有
                modulationStrength = null != modulationStrengths ? modulationStrengths[localerror] :
                                     randomThresholdValue;
                if (modulationStrength > 5) {
                    throw new java.lang.IllegalStateException("modulationStrength > 5");
                }
                modualtionBase = null != modualtionBases ? modualtionBases[localerror] : 16;
                totalweight = weight[0] + weight[1] + weight[2] + weight[3] +
                              weight[4];

                if (w > 1) {
                    short preerror = input[h][w - 1];
                    double[] preweights = null != weightings ? weightings[preerror] :
                                          new double[] {0, 7, 3, 5, 1};
                    if (lineBasedWeighting) {
                        weight7 = preweights[1];
                    }
                }
                /**
                 * 0 * 7
                 * 3 5 1
                 *
                 * 1 5 3
                 * 7 * 0
                 */

                if (bookWeighting) {
                    err5 = (h > 0 && w < width - 1) ? remainError[h - 1][w] : 0;
                } else {
                    err5 = (h > 0 && w < width) ? remainError[h - 1][w] : 0;
                }

                //==================================================================================
                // pre line
                //==================================================================================
                err1 = w > 0 && h > 0 ? remainError[h - 1][w - 1] : 0;
                err3 = (w < width - 1 && h > 0) ? remainError[h - 1][w + 1] : 0;
                if (null != err135Array) {
                    err135Array[0][h][w] = err1;
                    err135Array[1][h][w] = err3;
                    err135Array[2][h][w] = err5;
                }
                //==================================================================================
                // this line
                //==================================================================================
                err0 = (w < width - 1) ? remainError[h][w + 1] : 0;
                err7 = (w > 0) ? remainError7Only[h][w - 1] : 0;
                //==================================================================================

                if (wrongWeighting) {
                    localerror = input[h][w];
                }

                if (stThreshold) {
                    if (err7 >= 0) {
                        rounderr7 = (byte) Math.round(err7 * weight7 / totalweight);
                    } else {
                        rounderr7 = (byte) - err7;
                    }

                } else {
                    rounderr7 = (byte) Math.round(err7 * weight7 / totalweight);
                }
                rounderr0 = (byte) Math.round(err0 * weight0 / totalweight);

                if (lineBasedWeighting) {
                    short err135 = err135Line[w];
                    err135Line[w] = 0;
                    if (w135Floor) {
                        err135 = (short) Math.floor(err135 / totalweight);
                    } else {
                        err135 = (short) Math.round(err135 / totalweight);
                    }
                    if (null != errw135Array) {
                        errw135Array[h][w] = err135;
                    }
                    if (h == 3 && (w == 1535)) {
                        int a = 1;
                    }

                    err = (byte) (err135 + rounderr7 + rounderr0 + localerror);
                } else {
                    short err135 = 0;
                    if (w135Floor) {
                        err135 = (short) Math.floor((err3 * weight3 + err5 * weight5 +
                                err1 * weight1) / totalweight);
                    } else {
                        err135 = (short) Math.round((err3 * weight3 + err5 * weight5 +
                                err1 * weight1) / totalweight);
                    }
                    err = (byte) (err135 + rounderr7 + rounderr0 + localerror);
                }

                if ((0 == h || 0 == w) && bookWeighting) {
                    err = localerror;
                }

                if (noErrorWhenBlack && (0 == localerror || prelineIsBlack)) {
                    if (0 == localerror) {
                        err = 0;
                    } else if (prelineIsBlack) {
                        err = localerror;
                    }

                }
                totalError[h][w] = err;

                if (noErrorWhenBlack && (width - 1 == w) && prelineIsBlack) {
                    prelineIsBlack = false;
                }

                if (noErrorWhenBlack && (width - 1 == w) && 0 == localerror) {
                    prelineIsBlack = true;
                }

                if (randomThreshold) {

                    if (!newModulation) {
                        //老方法
                        int randombit = modulationStrength;
                        random.next(randombit);
                        int randnum = random.next(randombit);
                        int step = sumRandomNumber(randnum, randombit);
                        threshold = (short) (modualtionBase + step);
                    } else {
                        //新方法
                        int r1 = w % randomCount;
                        int add = 0;

                        if (0 != modulationStrength) {
                            int maxrandnum = 2 * modulationStrength + 1;
                            int randombit = getRandombit(modulationStrength);

                            //目前取XOR2LFSR
                            if (XOR2LFSR) {
                                if (w % 4 == 0) {
                                    //每4個pixel才跑一次亂數
                                    for (int x = 0; x < randomCount; x++) {

                                        if (nonSyncSim) {
                                            if (w >= halfWidth) {
                                                randomsRight[x].next(); //twice shift for random
                                                randomsRight[x].next();
                                            } else {
                                                randoms[x].next(); //twice shift for random
                                                randoms[x].next();
                                            }
                                        } else {
                                            randoms[x].next(); //twice shift for random
                                            randoms[x].next();
                                        }
                                        //==========================================================================================
                                        // 紀錄random
                                        //==========================================================================================
                                        if (showRandomNum) {
                                            try {
                                                randnumwriter.write(Long.toString(randoms[x].
                                                        getSeed()));
                                                randnumwriter.newLine();
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                        //==========================================================================================
                                    }
                                }

                                if (simplify) {
                                    //目前不用simplify, 會使亂數效果不佳
                                    long randnum1 = randoms[0].getSeed();
                                    long randnum2 = randoms[1].getSeed();
                                    long xor = randnum1 ^ randnum2;
                                    int mask = (int) Math.pow(2, randombit) - 1;
                                    //r1 0~3
                                    int randnum = (int) ((xor >> (r1 * 7)) & mask);
                                    add = randnum >= maxrandnum ? modulationStrength :
                                          randnum;
                                } else {
                                    //==================================================================================
                                    // 目前硬體所採用的random機制在此
                                    //==================================================================================
                                    int r2 = (r1 + 1) == randomCount ? 0 : r1 + 1;
                                    long randnum1 = randoms[r1].getSeed();
                                    long randnum2 = randoms[r2].getSeed();

                                    if (nonSyncSim && w >= halfWidth) {
                                        randnum1 = randomsRight[r1].getSeed();
                                        randnum2 = randomsRight[r2].getSeed();
                                    }

                                    long xor = randnum1 ^ randnum2;
                                    if (stRandom) {
                                        int mask = (int) Math.pow(2, 4) - 1;
                                        int randnum = (int) (xor & mask);

                                        final short[][] randomLut = {
                                                //step1
                                                {16, 15, 17, 16, 15, 17, 16, 15,
                                                17, 16, 15, 17, 16, 15, 17, 16}
                                                //step2
                                                , {14, 15, 16, 17, 18, 17, 16, 15
                                                , 14, 15, 16, 17, 18, 17, 16, 15}
                                                //step3
                                                , {16, 15, 17, 14, 18, 13, 19, 16
                                                , 15, 17, 14, 18, 13, 19, 16, 15}
                                                //step4(12-20)
                                                , {16, 15, 17, 14, 18, 13, 19, 12
                                                , 20, 16, 15, 17, 14, 18, 13, 19}
                                                //step5, step只做到5
                                                , {16, 15, 17, 14, 18, 13, 19, 12
                                                , 20, 11, 21, 16, 15, 17, 14, 18}
                                        };
                                        short minThreshold = (short) (16 - modulationStrength);
                                        add = randomLut[modulationStrength - 1][randnum];
                                        add = add - minThreshold;

                                    } else if (ccRandom) {
                                        int randomCase = modulationStrength * 2 + 1;
                                        int lsbnum = randombit - 1;

                                        int mask = (int) Math.pow(2, 4) - 1;
                                        int randnum = (int) (xor & mask);

                                        int lsb = randnum & (int) (Math.pow(2, lsbnum) - 1);
                                        int shiftbit = (randnum >> lsbnum) & 1;
                                        if (0 == shiftbit) {
                                            add = lsb;

                                        } else {
                                            add = randomCase - lsb - 1;
                                        }

                                    } else {
                                        int mask = (int) Math.pow(2, randombit) - 1;
                                        int randnum = (int) (xor & mask);

                                        add = randnum >= maxrandnum ? modulationStrength :
                                              randnum;
                                    }

                                }

                            } else {
                                int randnum = randoms[r1].next(randombit);
                                add = randnum >= maxrandnum ? modulationStrength :
                                      randnum;
                            }

                            if (useJavaRandom) {
                                add = javarandom.nextInt(maxrandnum);
                            }
                        }
                        short internalModBase = modualtionBase;
                        threshold = (short) (internalModBase - modulationStrength + add);

                    }
                }

                //==================================================================================
                // re-threshold
                //==================================================================================
                if (null != thresholdArray) {
                    thresholdArray[h][w] = threshold;
                }
                boolean rethreshold = false;

                if (err >= (2 * threshold) && stThreshold) {
                    rethreshold = true;
                }
                //==================================================================================


                if (err >= threshold || rethreshold) {
                    if (stThreshold && err >= 24 && (err - threshold) >= 13) {
                        output[h][w] = 1;

                        //st採用的對照表方式
                        byte result = (byte) (err - threshold);
                        remainError[h][w] = result;
                        if (result >= 13 && result <= 14) {
                            remainError7Only[h][w] = -7;
                        } else {
                            remainError7Only[h][w] = -8;
                        }
                        stThresholdCount++;

                    } else {
                        //原本的threshold方式
                        output[h][w] = 1;

                        byte result = (byte) (err - threshold);
                        if (rethreshold) {
                            result = (byte) Math.floor(err / 2);
                        }

                        maxresult = result > maxresult ? result : maxresult;
                        maxerr = err > maxerr ? err : maxerr;

                        remainError[h][w] = result;
                        remainError7Only[h][w] = result;
                    }
                } else {
                    output[h][w] = 0;
                    remainError[h][w] = (byte) err;
                    remainError7Only[h][w] = (byte) err;
                }

                if (lineBasedWeighting) {
                    short remain = remainError[h][w];
                    if (w > 0) {
                        err135Line[w - 1] += remain * weight3; //error3
                    }
                    err135Line[w] += remain * weight5; //error5

                    if (w < (width - 1)) {
                        err135Line[w + 1] += remain * weight1; //error1
                    }

                }

            }
        }
        checksum = 0;
        boolean output12bit = true;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (output12bit) {
                    //==============================================================
                    // 12bit輸出
                    //==============================================================
                    output[h][w] = (short) ((image12bit[h][w] / 16 + output[h][w]) *
                                            16);
                } else {
                    //==============================================================
                    // 8bit輸出
                    //==============================================================
                    output[h][w] = (short) ((image12bit[h][w] / 16 + output[h][w]));
                    //==============================================================
                }
                checksum += output[h][w];
            }
        }
        hardwareRemainError = remainError;
        hardwareThreshold = thresholdArray;
        hardwareTotalError = totalError;
        hardwareErrW135 = errw135Array;
        hardwareErr135 = err135Array;

        if (imagecksum != checksum(image12bit)) {
            throw new IllegalStateException();
        }
        //==============================================================================================================
        // 紀錄random
        //==============================================================================================================
        if (null != randnumwriter) {
            try {
                randnumwriter.flush();
                randnumwriter.close();
                randnumwriter = null;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //==============================================================================================================
        for (int x = 0; x < randomCount; x++) {
            seeds[x] = randoms[x].getSeed();
        }
        finalSeeds = seeds;
        return output;
    }

    static int getRandombit(int modulationStrength) {
        int randombit = 0;
        switch (modulationStrength) {
        case 1:
            randombit = 2;
            break;
        case 2:
            randombit = 3;
            break;
        case 3:
            randombit = 3;
            break;
        case 4:
            randombit = 4;
            break;
        case 5:
            randombit = 4;
            break;
        case 6:
            randombit = 4;
            break;
        case 7:
            randombit = 4;
            break;
        case 8:
            randombit = 5;
            break;
        default:
            throw new IllegalStateException("");
        }
        return randombit;
    }

    static int sumRandomNumber(int randnum, int bits) {
        int sum = 0;
        for (int x = 0; x < bits; x++) {
            sum = sum + ((randnum >> x) & 1);
        }
        return sum;
    }


    private static long checksum;
    public final static long getChecksum() {
        return checksum;
    }

    public static enum Matrix {
        Hardware_2_, //與第一版硬體結果相同, 純粹的1/3/5/7
        FloydSteinbergIdeal, //理想的ED, 與YagiSan的結果相同
        HardwareThresholdModulation; //與第二版硬體結果相同, 包含random及1/2水平方向拋出
    }


    private Matrix matrix = Matrix.HardwareThresholdModulation;
    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;

    }

    /**
     *
     * @param weightingBase double[][]
     * @return double[][]
     */
    public static double[][] fullWeightingBase2Weighting(double[][] weightingBase) {
        double[][] result = new double[16][];
        for (int x = 0; x < 16; x++) {
            result[x] = new double[5];
            for (int n = 0; n < 3; n++) {
                result[x][n + 1] = weightingBase[x][n];
            }
        }
        return result;
    }

    protected static boolean[][] shrink(short[][] shortarray, short threshold) {
        int height = shortarray.length;
        int width = shortarray[0].length;

        boolean[][] result = new boolean[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[h][w] = shortarray[h][w] >= threshold;
            }
        }
        return result;
    }


    public static short[][] main(short grayLevel, Matrix matrix) {
        int height = 1080;
        int width = 1920;

        short[][] hoseip = new short[height][width];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                hoseip[h][w] = grayLevel;
            }
        }

        AUOErrorDiffusion ed = new AUOErrorDiffusion();
        ed.setMatrix(matrix);
        short[][] result = ed.ed8bit0(hoseip);

        return result;
    }


}
