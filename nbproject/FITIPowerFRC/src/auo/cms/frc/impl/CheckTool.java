package auo.cms.frc.impl;

import java.util.ArrayList;

import auo.cms.frc.FRCPattern;
import shu.math.Maths;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013</p>
 *
 * <p>
 * Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CheckTool {

    public static int getTrueCount(boolean[] array, int start, int end) {
        int count = 0;
        for (int x = start; x < end; x++) {
            if (array[x]) {
                count++;
            }
        }
        return count;
    }

    /**
     * 檢查左右的+1數量是否相同
     *
     * @param frc FRCPattern
     * @return boolean
     */
    public static boolean checkLeftRightBalance(FRCPattern frc) {
        boolean[][][] pattern = frc.pattern[0];
        int left = 0, right = 0;
        for (int f = 0; f < 4; f++) {
            left += getTrueCount(pattern[f][0], 0, 4);
            right += getTrueCount(pattern[f][0], 4, 8);
        }

        return right == left;
    }

    public static int[] getQuadrantCount(boolean[][] pattern) {
        int halfHeight = pattern.length / 2;
        int halfWidth = pattern[0].length / 2;
        int Q1 = 0, Q2 = 0, Q3 = 0, Q4 = 0;
        for (int h = 0; h < halfHeight; h++) {
            for (int w = 0; w < halfWidth; w++) {

                if (pattern[h][w + halfWidth]) {
                    Q1++;
                }
                if (pattern[h][w]) {
                    Q2++;
                }
                if (pattern[h + halfHeight][w]) {
                    Q3++;
                }
                if (pattern[h + halfHeight][w + halfWidth]) {
                    Q4++;
                }

            }
        }
        return new int[]{Q1, Q2, Q3, Q4};
    }

    /**
     * 正負極性數量是否對稱
     *
     * @param frc FRCPattern
     * @return boolean
     */
    public static boolean checkPolarityBalance(FRCPattern frc) {
        byte[][] sum = frc.balancedSum;
        int height = sum.length;
        int width = sum[0].length;
        int pos = 0;
        int neg = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (sum[h][w] > 0) {
                    pos++;
                } else if (sum[h][w] < 0) {
                    neg++;
                }
            }
        }
        return pos == neg && pos != 0 && neg != 0;
    }

    /**
     * artifacts是否左右對稱 1. 是否左右相同 2. 是否左右鏡射
     *
     * @param frc FRCPattern
     * @return boolean
     */
    public static boolean checkArtifactsSymmetry(FRCPattern frc) {
        byte[][] artifacts = frc.artifacts;
        int height = artifacts.length;
        int width = artifacts[0].length;
        int halfwidth = width / 2;
        boolean allequal = true;
        boolean allreflact = true;

        for (int h = 0; h < height; h++) {
            boolean equal = true;
            boolean reflect = true;
            for (int w = 0; w < halfwidth; w++) {
                equal = equal && artifacts[h][w] == artifacts[h][w + halfwidth];
                reflect = reflect
                        && artifacts[h][w] == artifacts[h][width - w - 1];
            }
            allequal = allequal && equal;
            allreflact = allreflact && reflect;
        }

        return allequal || allreflact;
    }

    /**
     * 檢查是否有極性不平衡的case
     *
     * @param sum double[][]
     * @return boolean
     */
    public static boolean checkNoneTwoArtifacts(double[][] sum) {
        int height = sum.length;
        int width = sum[0].length;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (sum[h][w] <= -2 || sum[h][w] >= 1.75) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int[] checkLCount(FRCPattern frc) {
        boolean[][][] pattern = frc.pattern[0];
        int frame = pattern.length;
        int[] result = new int[frame];
        for (int f = 0; f < frame; f++) {
            result[f] = checkLCount(pattern[f]);
        }
        return result;
    }

    public static int getMaxSlash(FRCPattern frc) {
        byte[][] sum = frc.balancedSum;
        if (null == sum) {
            return 0;
        }
        int height = sum.length;
        int width = sum[0].length;
        int maxSlash = 0;
        for (int h = 0; h < height - 1; h++) {
            for (int w = 0; w < width; w++) {
                if (w < (width - 1) && sum[h][w] == sum[h + 1][w + 1]) { // \
                    int slash = 2;
                    for (;
                            h + slash < height && w + slash < width
                            && sum[h][w] == sum[h + slash][w + slash];
                            slash++) {

                    }
                    maxSlash = slash > maxSlash ? slash : maxSlash;
                }
                if (w > 0 && sum[h][w] == sum[h + 1][w - 1]) { // /
                    int slash = 2;
                    for (;
                            h + slash < height && w - slash >= 0
                            && sum[h][w] == sum[h + slash][w - slash];
                            slash++) {

                    }
                    maxSlash = slash > maxSlash ? slash : maxSlash;
                }
            }
        }
        return maxSlash;
    }

    public static int checkLCount(boolean[][] pattern) {
        int height = pattern.length;
        int width = pattern[0].length;

        int result = 0;

        for (int h = 0; h < height - 1; h++) {
            for (int w = 0; w < width - 1; w++) {
                /**
                 * -+ +- | |
                 */

                if (pattern[h][w] && pattern[h][w + 1] && pattern[h + 1][w + 1]) {
                    result++;
                }
                if (pattern[h][w] && pattern[h][w + 1] && pattern[h + 1][w]) {
                    result++;
                }
                /**
                 * |
                 * +-
                 */
                if (pattern[h][w] && pattern[h + 1][w] && pattern[h + 1][w + 1]) {
                    result++;
                }
            }
        }

        /**
         * |
         * -+
         */
        for (int h = 1; h < height; h++) {
            for (int w = 0; w < width - 1; w++) {
                if (pattern[h][w] && pattern[h][w + 1] && pattern[h - 1][w + 1]) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * 計算所有Frame累積 的Green且為+1的pixel數量 由於RGB排列會有三種方式: RGB GBR BRG,
     * 所以也有三種Green的計算結果
     *
     * @param frc FRCPattern
     * @return int[][]
     */
    public static int[][] getGreenPixelCount(FRCPattern frc) {
        boolean[][][] pattern = frc.pattern[0];
        int frame = pattern.length;
        int height = pattern[0].length;

        int[][] result = new int[height][3];
        for (int f = 0; f < frame; f++) {
            int[][] green = getGreenPixelCount(pattern[f]);
            for (int h = 0; h < height; h++) {
                for (int c = 0; c < 3; c++) {
                    result[h][c] += green[h][c];
                }

            }
        }

        return result;
    }

    /**
     * 計算Green且為+1的pixel數量 由於RGB排列會有三種方式: RGB GBR BRG, 所以也有三種Green的計算結果
     *
     * @param pattern boolean[][]
     * @return int[][]
     */
    public static int[][] getGreenPixelCount(boolean[][] pattern) {
        int height = pattern.length;
        int width = pattern[0].length;
        int[][] result = new int[height][3];
        for (int h = 0; h < height; h++) {
            for (int c = 0; c < 3; c++) {
                int start = (c + 1) % 3;
                for (int w = start; w < width; w += 3) {
                    if (pattern[h][w]) {
                        result[h][c]++;
                    }
                }
            }
        }

        return result;
    }

    public static int getContinueHorizontalCount(boolean[][] pattern, int count) {
        int height = pattern.length;
        int width = pattern[0].length;

        int result = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width - (count - 1); w++) {
                if (pattern[h][w] == true && pattern[h][w] == pattern[h][w + 1]) {
                    result++;
                }
            }
        }

        return result;
    }

    public static int getContinueVerticalCount(boolean[][] pattern, int count) {
        int height = pattern.length;
        int width = pattern[0].length;

        int result = 0;
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height - (count - 1); h++) {
                if (pattern[h][w] == true && pattern[h][w] == pattern[h + 1][w]) {
                    result++;
                }
            }
        }
        return result;
    }

    public static int[] getContinueVerticalCount(FRCPattern frc, int count) {
        boolean[][][] pattern = frc.pattern[0];
        int frame = pattern.length;
        int[] result = new int[frame];
        for (int f = 0; f < frame; f++) {
            result[f] = getContinueVerticalCount(pattern[f], count);
        }
        return result;
    }

    public static int[] getContinueHorizontalCount(FRCPattern frc, int count) {
        boolean[][][] pattern = frc.pattern[0];
        int frame = pattern.length;
        int[] result = new int[frame];
        for (int f = 0; f < frame; f++) {
            result[f] = getContinueHorizontalCount(pattern[f], count);
        }
        return result;
    }

    public static boolean checkVerticalLessThanHorizontal(FRCPattern frc) {
        frc.twohcount
                = getContinueHorizontalCount(frc, 2);
        frc.twovcount
                = getContinueVerticalCount(frc, 2);
        int hcount = Maths.sum(frc.twohcount);
        int vcount = Maths.sum(frc.twovcount);
        if (vcount > hcount) {
            return false;
        }
        //如果垂直連續兩點的數量>水平連續兩點
        int twohsize = frc.twohcount.length;
        for (int t = 0; t < twohsize; t++) {
            if (frc.twovcount[t] > frc.twohcount[t]) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkVerticalCount(FRCPattern frc, int count) {
        boolean[][][] pattern = frc.pattern[0];
        int frame = pattern.length;
        for (int f = 0; f < frame; f++) {
            if (!checkVerticalCount(pattern[f], count)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkVerticalCount(boolean[][] pattern, int count) {
        int height = pattern.length;
        int width = pattern[0].length;
        for (int w = 0; w < width; w++) {
            int truecount = 0;
            for (int h = 0; h < height; h++) {
                if (pattern[h][w]) {
                    truecount++;
                }
            }
            if (truecount != count) {
                return false;
            }

        }
        return true;
    }

    /**
     * 檢查斜線的長度
     *
     * @param pattern boolean[][]
     * @param length int
     * @return boolean
     */
    public static boolean checkSlash(boolean[][] pattern, int length) {

        int height = pattern.length;
        int width = pattern[0].length;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int x = 0;
                // \
                for (x = 0; x < length + 1; x++) {
                    if ((h + x) < height && (w + x) < width
                            && pattern[h + x][w + x]) {
                        int a = 1;
                    } else {
                        break;
                    }
                }
                if (x == (length + 1)) {
                    return false;
                }
                //  /
                for (x = 0; x < length + 1; x++) {
                    if ((h - x) >= 0 && (w + x) < width
                            && pattern[h - x][w + x]) {
                    } else {
                        break;
                    }
                }
                if (x == (length + 1)) {
                    return false;
                }

            }
        }

        return true;
    }

    public static boolean checkSlash(FRCPattern frc, int length) {
        boolean[][][][] pattern = frc.pattern;
        int level = pattern.length;
        int frame = pattern[0].length;
        int height = pattern[0][0].length;
        int width = pattern[0][0][0].length;

        for (int l = 0; l < level; l++) {
            for (int f = 0; f < frame; f++) {
                for (int h = 0; h < height; h++) {
                    for (int w = 0; w < width; w++) {
                        int x = 0;
                        // \
                        for (x = 0; x < (length + 1); x++) {
                            if (h + x < height && w + x < width
                                    && pattern[l][f][h + x][w + x]) {
                            } else {
                                break;
                            }
                        }
                        if (x == (length + 1)) {
                            return false;
                        }
                        //  /
                        for (x = 0; x < (length + 1); x++) {
                            if (h - x >= 0 && w + x < width
                                    && pattern[l][f][h - x][w + x]) {
                            } else {
                                break;
                            }
                        }
                        if (x == (length + 1)) {
                            return false;
                        }

                    }
                }
            }
        }

        return true;
    }

    public static boolean checkSame(FRCPattern frc, ArrayList<FRCPattern> list) {
        for (FRCPattern f : list) {
            if (f.equals(frc)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkQuadrantCount(boolean[][] pattern) {
        int height = pattern.length;
        int width = pattern[0].length;
        int halfh = height / 2;
        int halfw = width / 2;
        int[] q = new int[4];
        for (int w = 0; w < halfw; w++) {
            for (int h = 0; h < halfh; h++) {
                q[0] += pattern[h][w] ? 1 : 0;
                q[1] += pattern[h + halfh][w] ? 1 : 0;
                q[2] += pattern[h][w + halfw] ? 1 : 0;
                q[3] += pattern[h + halfh][w + halfw] ? 1 : 0;
            }

        }
        return q[0] == q[1] && q[1] == q[2] && q[2] == q[3];

    }

    /**
     * 檢查正負極性是否都不為0
     *
     * @param frc FRCPattern
     * @return boolean
     */
    public static boolean checkPolarityNonZero(FRCPattern frc) {
        boolean[][][][] pattern = frc.pattern;
        int frame = pattern[0].length;
        int height = pattern[0][0].length;
        int width = pattern[0][0][0].length;
        for (int f = 0; f < frame; f++) {
            int acount = 0;
            int bcount = 0;
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    if (pattern[0][f][h][w]) {
                        if (h % 2 == 0 && w % 2 == 0
                                || h % 2 == 1 && w % 2 == 1) {
                            acount++;
                        } else if (h % 2 == 0 && w % 2 == 1
                                || h % 2 == 1 && w % 2 == 0) {
                            bcount++;
                        }
                    }
                }
            }
            if (acount == 0 || bcount == 0) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkArtifacts(FRCPattern frcPattern, boolean none0, boolean symmetry) {
        FRCPattern frc = new FRCPattern(frcPattern.pattern, false);
        ArtifactsAnalyzer analyzer = new ArtifactsAnalyzer(
                ArtifactsAnalyzer.Inversion.Dot, frc);
        boolean ok = true;

        ok = ok && analyzer.checkSubpixelBaseArtifacts(1, true, true, false,
                none0, true, symmetry);
        frcPattern.artifacts = analyzer.checkArtifacts;
        return ok;

    }

    public static boolean checkBalancedSum(FRCPattern frcPattern) {
        FRCPattern frc = new FRCPattern(frcPattern.pattern, false);
        ArtifactsAnalyzer analyzer = new ArtifactsAnalyzer(
                ArtifactsAnalyzer.Inversion.Dot, frc);
        boolean ok = analyzer.checkBalancedSum(1);
        frcPattern.balancedSum = analyzer.checkBalancedSum;
        return ok;
    }

    public static boolean checkAdjoin(boolean[][] pattern) {

        int height = pattern.length;
        int width = pattern[0].length;

        for (int h = 1; h < height - 1; h++) {
            for (int w = 1; w < width - 1; w++) {
                if (pattern[h][w]) {
                    //上
                    if (pattern[h - 1][w]) {
                        return false;
                    }
                    //下
                    if (pattern[h + 1][w]) {
                        return false;
                    }
                    //左
                    if (pattern[h][w - 1]) {
                        return false;
                    }
                    //右
                    if (pattern[h][w + 1]) {
                        return false;
                    }
                }

            }
        }

        return true;
    }

    /**
     * 檢查上下左右是否無相鄰
     *
     * @param frc FRCPattern
     * @return boolean
     */
    public static boolean checkAdjoin(FRCPattern frc) {
        boolean[][][][] pattern = frc.pattern;
        int level = pattern.length;
        int frame = pattern[0].length;
        int height = pattern[0][0].length;
        int width = pattern[0][0][0].length;

        for (int l = 0; l < level; l++) {
            for (int f = 0; f < frame; f++) {
                for (int h = 1; h < height - 1; h++) {
                    for (int w = 1; w < width - 1; w++) {
                        if (pattern[l][f][h][w]) {
                            //上
                            if (pattern[l][f][h - 1][w]) {
                                return false;
                            }
                            //下
                            if (pattern[l][f][h + 1][w]) {
                                return false;
                            }
                            //左
                            if (pattern[l][f][h][w - 1]) {
                                return false;
                            }
                            //右
                            if (pattern[l][f][h][w + 1]) {
                                return false;
                            }
                        }

                    }
                }
            }
        }
        return true;
    }

    public static boolean checkContinueVerticalCount(boolean[][] pattern, int windex, int starth,
            int count) {
        int height = pattern.length;

        for (int h = starth; h < height - (count - 1); h++) {

            if (pattern[h][windex]) {
                boolean eq = true;
                for (int c = 1; c < count; c++) {
                    eq = eq && pattern[h + c][windex];
                }
                if (eq) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkContinueVerticalCount(boolean[][] pattern, int starth, int count) {
        int width = pattern[0].length;

        for (int w = 0; w < width; w++) {
            if (checkContinueVerticalCount(pattern, w, starth, count)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkContinueVerticalCount(FRCPattern frc, int starth, int count) {
        //[level][frame][h][w]
        boolean[][][] pattern = frc.pattern[0];
        int frame = pattern.length;

        for (int f = 0; f < frame; f++) {
            if (checkContinueVerticalCount(pattern[f], starth, count)) {
                return true;
            }

        }
        return false;
    }

    public static boolean checkContinueVerticalCount(FRCPattern frc, int count) {
        return checkContinueVerticalCount(frc, 0, count);

    }

    public static boolean checkPatternVerticalIsNotCount(FRCPattern frc, int count) {
        //[level][frame][h][w]
        boolean[][][] pattern = frc.pattern[0];
        int frame = pattern.length;
        for (int f = 0; f < frame; f++) {
            if (!checkVerticalIsNotCount(pattern[f], count)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 檢查垂直的true數量非等於count
     *
     * @param pattern boolean[][]
     * @param count int
     * @return boolean
     */
    public static boolean checkVerticalIsNotCount(boolean[][] pattern, int count) {
        int height = pattern.length;
        int width = pattern[0].length;
        for (int w = 0; w < width; w++) {
            int truecount = 0;
            for (int h = 0; h < height; h++) {
                if (pattern[h][w]) {
                    truecount++;
                }
            }
            if (truecount == count) {
                return false;
            }

        }
        return true;
    }

    public static int getMaxContinuePolarityHLine(FRCPattern frc) {
        byte[][] balancedSum = frc.balancedSum;
        int height = balancedSum.length;
        int width = balancedSum[0].length;
        int maxHLine = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width - 1; w++) {
                if (balancedSum[h][w] == balancedSum[h][w + 1]) {
                    int hline = 2;
                    for (;
                            (w + hline) < width
                            && balancedSum[h][w] == balancedSum[h][w + hline]; hline++) {
                    }
                    maxHLine = hline > maxHLine ? hline : maxHLine;
                }
            }
        }
        return maxHLine;

    }

    /**
     * 計算八個鄰接pixel, 哪一些是同極性的
     *
     * @param frc FRCPattern
     * @param negative boolean
     * @return int[]
     */
    public static int[] getAdjoinCount(FRCPattern frc, boolean negative) {
        byte[][] balancedSum = frc.balancedSum;
        int height = balancedSum.length;
        int width = balancedSum[0].length;
        int[] adjoinPolarityArray = new int[9];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (negative ? balancedSum[h][w] < 0 : balancedSum[h][w] > 0) {
                    int adjoinPolarityCount = 0;
                    for (int x = 0; x < 9; x++) {
                        int windex = x % 3 - 1;
                        int hindex = x / 3 - 1;
                        int realw = windex + w;
                        int realh = hindex + h;
                        if ((realh != h || realw != w) && realh >= 0 && realw >= 0
                                && realh < height && realw < width
                                && balancedSum[realh][realw] == balancedSum[h][w]
                                && (negative ? balancedSum[realh][realw] < 0
                                        : balancedSum[realh][realw] > 0)) {
                            adjoinPolarityCount++;
                        }
                    }
                    adjoinPolarityArray[adjoinPolarityCount]++;
                }
            }
        }
        return adjoinPolarityArray;
    }

    public static int[][] getTwovGcount(FRCPattern frc) {
        boolean[][][] pattern = frc.pattern[0];
        int framecount = pattern.length;
        int height = pattern[0].length;
        int width = pattern[0][0].length;
        boolean[][][] twoframePattern = new boolean[framecount][height][width];
        for (int f = 0; f < framecount; f++) {
            int f1 = f;
            int f2 = (f1 + 1) % framecount;
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    twoframePattern[f][h][w] = pattern[f1][h][w] || pattern[f2][h][w];
                }
            }
        }
        int[][] result = new int[framecount][3];
        /**
         * c=0 GBRGBRGB ^ ^ ^
         *
         * c=1 RGBRGBRG ^ ^ ^
         *
         * c=2 BRGBRGBR ^ ^
         */
        for (int c = 0; c < 3; c++) {
            for (int h = 0; h < height; h++) {
                for (int m = 0; m < 3; m++) {
                    int w = c + m * 3;
                    for (int f = 0; f < framecount; f++) {
                        if (h < height - 1) {
                            if (w < width && twoframePattern[f][h][w]
                                    && twoframePattern[f][h + 1][w]) {
                                result[f][c]++;
                            }
                        } else {
                            if (w < width && twoframePattern[f][h][w]
                                    && twoframePattern[f][0][w]) {
                                result[f][c]++;
                            }
                        }
                    }
                }

            }

        }
        return result;
    }

    public static int[][] getPlus1SumAdjoin(FRCPattern frc) {
        byte[][] plus1sum = frc.plusOneSum;
        int height = plus1sum.length;
        int width = plus1sum[0].length;

        //======================================================================================
// 分析+1 sum
//======================================================================================
        int[][] plusAdjoin = new int[4][4];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                for (int x = 0; x < 9; x++) {
                    int realw = w - 1 + x % 3;
                    int realh = h - 1 + x / 3;
                    if ((realw != w || realh != h) && realw >= 0 && realw < width && realh >= 0
                            && realh < height) {
                        plusAdjoin[plus1sum[h][w]][plus1sum[realh][realw]]++;
                    }
                }

            }
        }

//======================================================================================
        return plusAdjoin;
    }

    public static boolean h_check16_n_even(Frame frame, int n) {
        //final int height = frame.frameIndex.length;
        final int L01_size = frame.frameIndex[0].length + frame.frameIndex[1].length;
        final int L23_size = frame.frameIndex[2].length + frame.frameIndex[3].length;
        final int L45_size = frame.frameIndex[4].length + frame.frameIndex[5].length;
        final int L67_size = frame.frameIndex[6].length + frame.frameIndex[7].length;
        final boolean test = n == L01_size && n == L23_size && n == L45_size && n == L67_size;
        return test;
    }

    public static boolean h_check16_n_odd(Frame frame, int n) {
        //final int height = frame.frameIndex.length;
        final int L12_size = frame.frameIndex[1].length + frame.frameIndex[2].length;
        final int L34_size = frame.frameIndex[3].length + frame.frameIndex[4].length;
        final int L56_size = frame.frameIndex[5].length + frame.frameIndex[6].length;
        final int L07_size = frame.frameIndex[0].length + frame.frameIndex[7].length;
        final boolean test = n == L12_size && n == L34_size && n == L56_size && n == L07_size;
        return test;
    }

    public static boolean v_check16_n(boolean[][] frame, int n) {
        final int height = frame.length;
        final int width = frame[0].length;
        int true_count_in_column[] = new int[width];
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                if (frame[h][w]) {
                    true_count_in_column[w]++;
                }
            }
        }
        final int C01_true = true_count_in_column[0] + true_count_in_column[1];
        final int C23_true = true_count_in_column[2] + true_count_in_column[3];
        final int C45_true = true_count_in_column[4] + true_count_in_column[5];
        final int C67_true = true_count_in_column[6] + true_count_in_column[7];

        final int C12_true = true_count_in_column[1] + true_count_in_column[2];
        final int C34_true = true_count_in_column[3] + true_count_in_column[4];
        final int C56_true = true_count_in_column[5] + true_count_in_column[6];
        final int C07_true = true_count_in_column[0] + true_count_in_column[7];

        final boolean test = n == C01_true && n == C23_true && n == C45_true && n == C67_true;
        final boolean test2 = n == C12_true && n == C34_true && n == C56_true && n == C07_true;
        return test && test2;
    }

    public static boolean v_check8_n(boolean[][] frame, int n) {
        final int height = frame.length;
        final int width = frame[0].length;
        int true_count_in_column[] = new int[width];
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                if (frame[h][w]) {
                    true_count_in_column[w]++;
                }
            }
        }
        for (int x = 0; x < width; x++) {
            if (true_count_in_column[x] != n) {
                return false;
            }
        }
        return true;

    }

    public static boolean grid_check16_n(boolean[][] frame, int n) {
        final int height = frame.length;
        final int width = frame[0].length;
        int grid[] = new int[4];
        for (int h = 0; h < height / 2; h++) {
            for (int w = 0; w < width / 2; w++) {
                grid[0] += frame[h][w] ? 1 : 0;
                grid[1] += frame[h][w + width / 2] ? 1 : 0;
                grid[2] += frame[h + height / 2][w] ? 1 : 0;
                grid[3] += frame[h + height / 2][w + width / 2] ? 1 : 0;
            }
        }
        return n == grid[0] && n == grid[1] && n == grid[2] && n == grid[3];
    }

    public static boolean grid_check16_n_overlap(boolean[][] frame, int n) {
        final int height = frame.length;
        final int width = frame[0].length;
        int grid[][] = new int[5][5];
        for (int h0 = 0; h0 < height - 3; h0++) {
            for (int w0 = 0; w0 < width - 3; w0++) {
                for (int h = h0; h < h0 + 4; h++) {
                    for (int w = w0; w < w0 + 4; w++) {
                        grid[h0][w0] += frame[h][w] ? 1 : 0;
                    }
                }
            }
        }

        for (int h0 = 0; h0 < height - 3; h0++) {
            for (int w0 = 0; w0 < width - 3; w0++) {
                if (grid[0][0] != grid[h0][w0] && grid[0][0] != n) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean grid_check9_n_overlap(boolean[][] frame, int n) {
        final int height = frame.length;
        final int width = frame[0].length;
        int grid[][] = new int[6][6];

        for (int h0 = 0; h0 < height - 2; h0++) {
            for (int w0 = 0; w0 < width - 2; w0++) {
                for (int h = h0; h <= h0 + 2; h++) {
                    for (int w = w0; w <= w0 + 2; w++) {
                        grid[h0][w0] += frame[h][w] ? 1 : 0;
                    }
                }
            }
        }

        for (int h0 = 0; h0 < height - 2; h0++) {
            for (int w0 = 0; w0 < width - 2; w0++) {

                if (grid[h0][w0] > n) {
                    return false;
                }
            }
        }
        return true;
    }
}
