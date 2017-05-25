package auo.cms.frc.impl;

import java.io.FileNotFoundException;
import java.io.IOException;

import auo.cms.frc.FRCPattern;
import shu.math.array.DoubleArray;

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
public class ArtifactsAnalyzer {

    public static boolean overallAnalyze(FRCPattern frcpattern, ArtifactsAnalyzer.InversionMode inversion) {
        return overallAnalyze(frcpattern, true, true, true, inversion);
    }

    public static void analyze(FRCPattern frcpattern, ArtifactsAnalyzer.InversionMode inversion) {
        analyze(frcpattern, 1, inversion);
    }

    public static void analyze(FRCPattern frcpattern, int level, ArtifactsAnalyzer.InversionMode inversion) {
//        boolean[][][][] pattern = frcpattern.pattern;
//        AUOFRC auofrc = new AUOFRC(pattern, false);
        ArtifactsAnalyzer analyzer = new
                                     ArtifactsAnalyzer(inversion,
                frcpattern);

        byte[][] artifacts = analyzer.getArtifactsArray(level);

        frcpattern.artifacts = artifacts;
        frcpattern.unbalancedSum = analyzer.analyzeSubpixelBaseUnbalancedSum(level);
        frcpattern.balancedSum = analyzer.getBalancedSumArray(level);
        frcpattern.plusOneSum = analyzer.analyzeSubpixelBasePlusOneSum(level);
        frcpattern.polarityPattern = analyzer.getPolarityPattern(level);
        frcpattern.caculateInfo();
    }

    /**
     *
     * @param frcpattern FRCPattern
     * @param checkArtifactsSymmetry boolean artifacts是否左右對稱
     * @param checkEqualsPolarity boolean 正負極性數量是否對稱
     * @param checkLeftRightBalance boolean 檢查左右的+1數量是否相同
     * @return boolean
     */
    public static boolean overallAnalyze(FRCPattern frcpattern, boolean checkArtifactsSymmetry,
                                         boolean checkEqualsPolarity,
                                         boolean checkLeftRightBalance, ArtifactsAnalyzer.InversionMode inversion) {
        analyze(frcpattern, inversion);
//        boolean[][][][] pattern = frcpattern.pattern;
//        AUOFRC auofrc = new AUOFRC(pattern, false);
//        ArtifactsAnalyzer analyzer = new
//                                     ArtifactsAnalyzer(
//                                             ArtifactsAnalyzer.Inversion.Dot,
//                                             auofrc);
//
//        byte[][] artifacts = analyzer.getArtifactsArray(
//                1);
//
//        frcpattern.artifacts = artifacts;
//        frcpattern.sum = analyzer.analyzeSubpixelBaseSum(1);
//        frcpattern.balancedSum = analyzer.getBalancedSumArray(1);
//        frcpattern.plusOneSum = analyzer.analyzeSubpixelBasePlusOneSum(1);
//        frcpattern.caculateInfo();

        boolean check1 = (!checkArtifactsSymmetry || CheckTool.checkArtifactsSymmetryAndReflect(frcpattern));
        boolean check2 = (!checkEqualsPolarity || CheckTool.checkPolarityBalance(frcpattern));
        boolean check3 = (!checkLeftRightBalance || CheckTool.checkLeftRightBalance(frcpattern));
        if (check1 && check2 && check3) {
            return true;
        } else {
            return false;
        }

    }


    public static enum InversionMode {
        _1V1H, _1V2H, _1V2Hp1, _1V8H, _2Vp1_1H, _2Vp1_2H, _2Vp1_2Hp1
    }


    private void setInversion(InversionMode mode) {
        switch (mode) {
        case _1V1H:
            break;
        case _1V2H:
            this.NLineDotInversion = 2;
            break;
        case _1V2Hp1:
            this.NLineDotInversion = 2;
            this.NLinePlus1 = true;
            break;
        case _1V8H:
            this.NLineDotInversion = 8;
            break;
        case _2Vp1_1H:
            this.NColumnDotInversion = 2;
            this.NColumnPlus1 = true;
            break;
        case _2Vp1_2H:
            this.NLineDotInversion = 2;
            this.NColumnDotInversion = 2;
            this.NColumnPlus1 = true;
            break;
        case _2Vp1_2Hp1:
            this.NLineDotInversion = 2;
            this.NLinePlus1 = true;
            this.NColumnDotInversion = 2;
            this.NColumnPlus1 = true;

            break;
        default:
            throw new IllegalArgumentException("");
        }
    }

//    private Inversion inversion;
    private FRCPattern frc;
    private int frameInversionCount = 1;
    private int NLineDotInversion = 1; //n-line , H
    private int NColumnDotInversion = 1; //n-column, V
    private boolean NLinePlus1 = false; //nH+1
    private boolean NColumnPlus1 = false; //nV+1
    private boolean inverse = false;


//    public ArtifactsAnalyzer(Inversion inversion, FRCPattern frc) {
////        this.inversion = inversion;
//        this.frc = frc;
//    }

    public ArtifactsAnalyzer(InversionMode inversionMode, FRCPattern frc) {
        setInversion(inversionMode);
        this.frc = frc;
    }

    private boolean[][] getPolarity(int frame) {
        boolean[][][] frcPattern = frc.getFRCPattern(0);
        int height = frcPattern[0].length;
        int width = frcPattern[0][0].length;

        boolean pstart = ((frame % (frameInversionCount * 2)) / frameInversionCount) <=
                         0.5;
        pstart = inverse ? !pstart : pstart;

        boolean[][] polarity = new boolean[height][width];
        final int nV = NColumnDotInversion;
        final int nH = NLineDotInversion;
        final boolean plusV = NColumnPlus1;
        final boolean plusH = NLinePlus1;
//        if (inversion == Inversion.Dot) {
        for (int h = 0; h < height; h++) {

            for (int w = 0; w < width; w++) {
                polarity[h][w] = pstart;
                if (w < width - 1) {
                    pstart = ((w + 1 + (plusV ? -1 : 0)) % nV) == 0 ? !pstart : pstart;
                }
            }
            pstart = ((h + 1 + (plusH ? -1 : 0)) % nH) == 0 ? pstart : !pstart;
            pstart = (plusV) ? !pstart : pstart;
        }

        return polarity;
    }


    private boolean[][] getPolarity_(int frame) {
//        boolean[][][] frcPattern = frc.getFRCPattern(0);
//        int height = frcPattern[0].length;
//        int width = frcPattern[0][0].length;
//
//        boolean pstart = ((frame % (frameInversionCount * 2)) / frameInversionCount) <=
//                         0.5;
//        boolean[][] polaritybase = null;
//        if (inversion == Inversion.Dot) {
////            int dotHeight = (NLineDotInversion * 2) +
////                            (NPlus1DotInversion ? 1 : 0);
//            int dotHeight = (NLineDotInversion * 2);
//            polaritybase = new boolean[dotHeight][width];
//            boolean p = pstart;
//
//            if (NPlus1DotInversion) {
//                for (int w = 0; w < width; w++) {
//                    polaritybase[0][w] = p;
//                    p = !p;
//                }
//                p = !p;
//            }
//            int h0start = NPlus1DotInversion ? 1 : 0;
//            for (int h0 = 0; h0 < NLineDotInversion; h0++) {
//                for (int w = 0; w < width; w++) {
//                    polaritybase[h0 + h0start][w] = p;
//                    p = !p;
//                    polaritybase[h0 + h0start + NLineDotInversion][w] = p;
//                }
//            }
//
//        } else if (inversion == Inversion.Column) {
//            polaritybase = new boolean[1][width];
//            boolean p = pstart;
//            for (int w = 0; w < width; w++) {
//                polaritybase[0][w] = p;
//                p = !p;
//            }
//        }
//        boolean[][] polarity = new boolean[height][width];
//        if (null != polaritybase) {
//            int baseheight = polaritybase.length;
//            int count = (0 == height % baseheight) ? height / baseheight :
//                        height / baseheight + 1;
//
//            for (int x = 0; x < count; x++) {
//                for (int h0 = 0; h0 < baseheight; h0++) {
//                    int h = x * baseheight + h0;
//                    if (h >= height) {
//                        break;
//                    }
//                    for (int w = 0; w < width; w++) {
//                        polarity[h][w] = !inverse ? polaritybase[h0][w] :
//                                         !polaritybase[h0][w];
//                    }
//                }
//            }
//
//            return polarity;
//        } else {
//            throw new IllegalStateException("null == polaritybase");
//        }
        return null;
    }

    /**
     * 將非balance的sum結果轉成artifacts
     * @param sum double[][]
     * @return byte[][]
     */
    private static byte[][] sumToArtifactsArray(double[][] sum) {
        int height = sum.length;
        int width = sum[0].length;
        byte[][] artifacts = new byte[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (sum[h][w] < 0) {
                    artifacts[h][w] = -1;
                } else if (sum[h][w] > 0) {
                    artifacts[h][w] = 1;
                }
            }
        }

        return artifacts;
    }


    private static String sumToLuminanceString(double[][] sum) {
        int height = sum.length;
        int width = sum[0].length;
        StringBuilder b = new StringBuilder();
        double[] minarray = DoubleArray.min(sum);
        double min = DoubleArray.min(minarray);

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (sum[h][w] == min) {
                    b.append(' ');
                } else {
                    b.append('+');
                }
                b.append(' ');
            }
            b.append('\n');
        }
        return b.toString();
    }

    byte[][] checkArtifacts;

    private static boolean checkNone0Artifacts(byte[][] artifacts) {
        int height = artifacts.length;
        int width = artifacts[0].length;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (0 == artifacts[h][w]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkArtifactsSymmetry(byte[][] artifacts) {
        int halfh = artifacts.length / 2;
        int halfw = artifacts[0].length / 2;
        for (int h = 0; h < halfh; h++) {
            for (int w = 0; w < halfw; w++) {

                if (!(artifacts[h][w] == artifacts[h][w + halfw] &&
                      artifacts[h][w] == artifacts[h + halfh][w] &&
                      artifacts[h][w] == artifacts[h + halfh][w + halfw])) {
                    return false;
                }
            }
        }
        return true;
    }


    public boolean checkSubpixelBaseArtifacts(int level, boolean illLv2,
                                              boolean illLv1, boolean healthy,
                                              boolean none0Artifacts, boolean none2Artifacts, boolean symmetry) {
        double[][] unbalancedSum = analyzeSubpixelBaseUnbalancedSum(
                level);

        boolean check = true;
        if (none2Artifacts) {
            check = check && CheckTool.checkNoneTwoArtifacts(unbalancedSum);
        }
        checkArtifacts = sumToArtifactsArray(unbalancedSum);
        if (check && none0Artifacts) {
            check = check && checkNone0Artifacts(checkArtifacts);
        }
        if (check && symmetry) {
            check = check && checkArtifactsSymmetry(checkArtifacts);
        }

        if (check) {
            check = check &&
                    (checkArtifactsUpdownReflectSymmetry(checkArtifacts) ||
                     checkArtifactsLeftrightReflectSymmetry(checkArtifacts));
        }

        if (check && illLv2) {
            check = check && checkIllArtifactsLv2(checkArtifacts);
        }
        if (check && illLv1) {
            check = check && checkIllArtifactsLv1(checkArtifacts);
        }
        if (check && healthy) {
            check = check && checkHealthyArtifacts(checkArtifacts);
        }
        return check;

    }

    private static boolean checkArtifactsUpdownReflectSymmetry(byte[][] artifacts) {
        int height = artifacts.length;
        int halfh = height / 2;
        int width = artifacts[0].length;
        boolean reflect = true;
        boolean symmetry = true;
        for (int h = 0; h < halfh; h++) {
            for (int w = 0; w < width; w++) {
                reflect = reflect && (artifacts[h][w] == artifacts[height - h - 1][w]);
                symmetry = symmetry && (artifacts[h][w] == artifacts[h + halfh][w]);
            }
        }
        return (reflect || symmetry);
    }

    private static boolean checkArtifactsLeftrightReflectSymmetry(byte[][] artifacts) {
        int height = artifacts.length;
        int width = artifacts[0].length;
        int halfw = width / 2;

        boolean reflect = true;
        boolean symmetry = true;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < halfw; w++) {
                reflect = reflect && (artifacts[h][w] == artifacts[h][width - w - 1]);
                symmetry = symmetry && (artifacts[h][w] == artifacts[h][w + halfw]);
            }
        }
        return (reflect || symmetry);
    }


    public double[][] analyzeSubpixelBaseUnbalancedSum(int level) {
        return getSubpixelBaseUnbalancedSum(frc.getFRCPattern(level - 1));
    }

    public double[][] analyzeSubpixelBaseBalancedSum(int level) {
        return getSubpixelBaseBalancedSum(frc.getFRCPattern(level - 1));
    }


    public byte[][] getArtifactsArray(int level) {
        //Artifacts的算法: 先算出sum(非balance), 再由sum轉成Artifacts
        double[][] unbalancedSum = analyzeSubpixelBaseUnbalancedSum(level);
        return sumToArtifactsArray(unbalancedSum);
    }

    public byte[][] getBalancedSumArray(int level) {
        double[][] sum = analyzeSubpixelBaseBalancedSum(level);
        return sumToArtifactsArray(sum);
    }

    public byte[][][] getPolarityPattern(int level) {
        return getPolarityPattern(frc.getFRCPattern(level - 1));
    }

    public byte[][][] getPolarityPattern(boolean[][][] frcPattern) {
        int framecount = frc.getFrameCount();
        int height = frcPattern[0].length;
        int width = frcPattern[0][0].length;
        byte[][][] result = new byte[framecount][height][width];
        for (int f = 0; f < framecount; f++) {
            boolean[][] frame = frcPattern[f];
            boolean[][] polarity = getPolarity(f);

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    result[f][h][w] = (byte) ((frame[h][w] ? 1 : 0) * (polarity[h][w] ? 1 : -1));

                }

            }
        }
        return result;
    }

    private double[][] getSubpixelBaseBalancedSum(boolean[][][] frcPattern) {
        int framecount = frc.getFrameCount();
        int height = frcPattern[0].length;
        int width = frcPattern[0][0].length;
        double[][] sum = new double[height][width];
        for (int x = 0; x < framecount; x++) {
            boolean[][] frame = frcPattern[x];
            boolean[][] polarity = getPolarity(x);

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    sum[h][w] = sum[h][w] +
                                (polarity[h][w] ? 1 : -1) *
                                (frame[h][w] ? 1 : 0); //有pattern就是1, 沒有就是0

                }

            }
        }
        return sum;
    }

    public byte[][] analyzeSubpixelBasePlusOneSum(int level) {
        return getSubpixelBasePlusOneSum(frc.getFRCPattern(level - 1));
    }

    private byte[][] getSubpixelBasePlusOneSum(boolean[][][] frcPattern) {
        int framecount = frc.getFrameCount();
        int height = frcPattern[0].length;
        int width = frcPattern[0][0].length;
        byte[][] sum = new byte[height][width];
        for (int x = 0; x < framecount; x++) {
            boolean[][] frame = frcPattern[x];

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    sum[h][w] = (byte) (sum[h][w] +
                                        (frame[h][w] ? 1 : 0)); //有pattern就是1, 沒有就是0

                }

            }
        }
        return sum;
    }

    private double[][] getSubpixelBaseUnbalancedSum(boolean[][][] frcPattern) {
        int framecount = frc.getFrameCount();
        int height = frcPattern[0].length;
        int width = frcPattern[0][0].length;
        double[][] unbalancedSum = new double[height][width];
        for (int x = 0; x < framecount; x++) {
            boolean[][] frame = frcPattern[x];
            boolean[][] polarity = getPolarity(x);

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {

                    unbalancedSum[h][w] = unbalancedSum[h][w] +
                                          (polarity[h][w] ? 0.875 : -1.125) * //正的就是0.8, 負的是-1.1
                                          (frame[h][w] ? 1 : 0); //有pattern就是1, 沒有就是0

                }

            }
        }
        return unbalancedSum;
    }


    public double[][] analyzeSubpixelBaseLuminance(int level) {
        int framecount = frc.getFrameCount();
        boolean[][][] frcPattern = frc.getFRCPattern(level - 1);
        int height = frcPattern[0].length;
        int width = frcPattern[0][0].length;
        double[][] sum = new double[height][width];
        for (int x = 0; x < framecount; x++) {
            boolean[][] frame = frcPattern[x];

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    sum[h][w] = sum[h][w] + (frame[h][w] ? 1 : 0);
                }
            }
        }
        System.out.println(sumToLuminanceString(sum));
        return sum;
    }


    private static boolean checkHealthyArtifacts(byte[][] artifacts) {
        int height = artifacts.length;
        int width = artifacts[0].length;
        //check chessboard
        boolean check = true;
        for (int h = 0; h < height; h++) {
            check = check && h == 0 ? true :
                    artifacts[h][0] != artifacts[h - 1][0];
            check = check && (artifacts[h][0] != artifacts[h][1]);
            for (int w = 2; w < width; w += 2) {
                check = check && (artifacts[h][w - 2] == artifacts[h][w]);
            }
            for (int w = 3; w < width; w += 2) {
                check = check && (artifacts[h][w - 2] == artifacts[h][w]);
            }

        }
        if (check) {
            return true;
        }
        //check same polarity
        boolean same = true;
        for (int h = 0; h < height; h++) {
            same = (h != 0) ? (artifacts[h][0] == artifacts[h - 1][0]) : same;
            for (int w = 0; w < width - 1; w++) {
                same = same && (artifacts[h][w] == artifacts[h][w + 1]);
            }
        }
        if (same) {
            return true;
        }
        return false;
    }

    private static boolean checkIllArtifactsLv2(byte[][] artifacts) {
        int height = artifacts.length;
        int width = artifacts[0].length;
        if (height < 4) {
            return true;
        }

        //======================================================================
        //check double H-line
        //======================================================================
        boolean hline = true;
        for (int h = 0; h < height; h += 4) {
            hline = hline && (artifacts[h][0] == artifacts[h + 1][0]); //L0==L1
            hline = hline && (artifacts[h + 1][0] != artifacts[h + 2][0]); //L1!=L2
            hline = hline && (artifacts[h + 2][0] == artifacts[h + 3][0]); //L3==L4

            for (int w = 0; w < width - 1; w++) {
                //L0~4整條都一樣
                hline = hline && (artifacts[h][w] == artifacts[h][w + 1]);
                hline = hline && (artifacts[h + 1][w] == artifacts[h + 1][w + 1]);
                hline = hline && (artifacts[h + 2][w] == artifacts[h + 2][w + 1]);
                hline = hline && (artifacts[h + 3][w] == artifacts[h + 3][w + 1]);
            }
        }

        if (hline) {
            return false;
        }
        //======================================================================

        //======================================================================
        //check double V-line
        //======================================================================
        boolean vline = true;
        for (int w = 0; w < width; w += 4) {
            vline = vline && (artifacts[0][w] == artifacts[0][w + 1]); //L0==L1
            vline = vline && (artifacts[0][w + 1] != artifacts[0][w + 2]); //L1!=L2
            vline = vline && (artifacts[0][w + 2] == artifacts[0][w + 3]); //L3==L4

            for (int h = 0; h < height - 1; h++) {
                vline = vline && (artifacts[h][w] == artifacts[h + 1][w]);
                vline = vline && (artifacts[h][w + 1] == artifacts[h + 1][w + 1]);
                vline = vline && (artifacts[h][w + 2] == artifacts[h + 1][w + 2]);
                vline = vline && (artifacts[h][w + 3] == artifacts[h + 1][w + 3]);
            }

        }
        if (vline) {
            return false;
        }
        //======================================================================

        //======================================================================
        //上下的0 hbar, 會形成double 0 hbar
        //======================================================================
        boolean hside0 = true;
        for (int w = 0; w < width - 1; w++) {
            hside0 = hside0 && (artifacts[0][w] == artifacts[0][w + 1]);
            hside0 = hside0 &&
                     (artifacts[height - 1][w] == artifacts[height - 1][w + 1]);
        }
        if (hside0) {
            return false;
        }
        //======================================================================

        //======================================================================
        //上下的0 vbar, 會形成double 0 vbar
        //======================================================================
        boolean vside0 = true;
        for (int h = 0; h < height - 1; h++) {
            vside0 = vside0 && (artifacts[h][0] == artifacts[h + 1][0]);
            vside0 = vside0 &&
                     (artifacts[h][width - 1] == artifacts[h + 1][width - 1]);
        }
        if (vside0) {
            return false;
        }
        //======================================================================

        //======================================================================
        // 檢查double 0 hbar
        //======================================================================
        for (int h = 0; h < height - 1; h++) {
            if (artifacts[h][0] == artifacts[h + 1][0]) {
                boolean eq = true;
                for (int w = 0; w < width - 1; w++) {
                    eq = eq && artifacts[h][w] == artifacts[h][w + 1];
                    eq = eq && artifacts[h + 1][w] == artifacts[h + 1][w + 1];
                }
                if (eq) {
                    return false;
                }

            }
        }

        //======================================================================

        //======================================================================
        // 檢查double 0 vbar
        //======================================================================
        for (int w = 0; w < width - 1; w++) {
            if (artifacts[0][w] == artifacts[0][w + 1]) {
                boolean eq = true;
                for (int h = 0; h < height - 1; h++) {
                    eq = eq && (artifacts[h][w] == artifacts[h + 1][w]);
                    eq = eq && (artifacts[h][w + 1] == artifacts[h + 1][w + 1]);
                }
                if (eq) {
                    return false;
                }
            }
        }
        //======================================================================

        byte[][] q = quadrupleCopy(artifacts);
        //======================================================================
        // 檢查2x6
        //======================================================================
        for (int h = 0; h < q.length - 1; h++) {
            for (int w = 0; w < q[0].length - 5; w++) {
                if (q[h][w] == q[h + 1][w]) {
                    boolean eq = true;
                    for (int x = 0; x < 5; x++) {
                        eq = eq && (q[h][w + x] == q[h][w + x + 1]);
                        eq = eq && (q[h + 1][w + x] == q[h + 1][w + x + 1]);
                    }
                    if (eq) {
                        return false;
                    }
                }
            }
        }

        //======================================================================
        // 檢查6x2
        //======================================================================


        return true;
    }

    private static byte[][] quadrupleCopy(byte[][] b) {
        int height = b.length;
        int width = b[0].length;
        byte[][] result = new byte[height * 2][width * 2];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[h][w] = b[h][w];
                result[h + height][w] = b[h][w];
                result[h][w + width] = b[h][w];
                result[h + height][w + width] = b[h][w];

            }
        }
        return result;
    }

    private static boolean checkIllArtifactsLv1(byte[][] artifacts) {
        int height = artifacts.length;
        int width = artifacts[0].length;
        //check strip H-line
        boolean hline = true;
        for (int h = 0; h < height; h += 2) {
            hline = hline && (artifacts[h][0] != artifacts[h + 1][0]);
            for (int w = 0; w < width - 1; w++) {
                hline = hline && (artifacts[h][w] == artifacts[h][w + 1]);
                hline = hline && (artifacts[h + 1][w] == artifacts[h + 1][w + 1]);
            }
        }
        if (hline) {
            return false;
        }
        //check strip V-line
        boolean vline = true;
        for (int w = 0; w < width; w += 2) {
            vline = vline && (artifacts[0][w] != artifacts[0][w + 1]);
            for (int h = 0; h < height - 1; h++) {
                vline = vline && (artifacts[h][w] == artifacts[h + 1][w]);
                vline = vline && (artifacts[h][w + 1] == artifacts[h + 1][w + 1]);
            }
        }
        if (vline) {
            return false;
        }

        //check one H-line
        for (int h = 0; h < height; h++) {
            boolean eq = true;
            for (int w = 0; w < width - 1; w++) {
                eq = eq && (artifacts[h][w] == artifacts[h][w + 1]);
            }
            if (eq) {
                return false;
            }
        }
        //check one V-line
        for (int w = 0; w < width; w++) {
            boolean eq = true;
            for (int h = 0; h < height - 1; h++) {
                eq = eq && (artifacts[h][w] == artifacts[h + 1][w]);
            }
            if (eq) {
                return false;
            }

        }

        return true;
    }


    private final static boolean F = false;
    private final static boolean T = true;
    public final static boolean[][][] BLANK_PATTERN = { { {F, F, F, F}, {F, F,
            F, F}
    }, { {F, F, F, F}, {F, F, F, F}
    }, { {F, F, F, F}, {F, F, F, F}
    }, { {F, F, F, F}, {F, F, F, F}
    }
    };

    public static void main(String[] args) throws FileNotFoundException,
            IOException {
        for (ArtifactsAnalyzer.InversionMode mode : ArtifactsAnalyzer.InversionMode.values()) {
            ArtifactsAnalyzer analyzer = new ArtifactsAnalyzer(mode,
                    new FRCPattern("frc/auofrc.csv"));
            System.out.println(mode);
            System.out.println(FRCUtil.toString(analyzer.getPolarity(0)));
            System.out.println("");
        }

    }

    public void setFrameInversionCount(int frameInversionCount) {
        this.frameInversionCount = frameInversionCount;
    }


    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }


//    public void setNLineDotInversion(int NLineDotInversion) {
//        this.NLineDotInversion = NLineDotInversion;
//    }
//
//    public void setNPlus1DotInversion(boolean NPlus1DotInversion) {
//        this.NPlus1DotInversion = NPlus1DotInversion;
//    }


    byte[][] checkBalancedSum;

    /**
     * 檢查正負極性的個數以及斜線
     *
     * @param level int
     * @return boolean
     */
    public boolean checkBalancedSum(int level) {
        byte[][] sum = getBalancedSumArray(level);
        checkBalancedSum = sum;

        int height = sum.length;
        int width = sum[0].length;

        int poscount = 0;
        int negcount = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (sum[h][w] > 0) {
                    poscount++;
                } else if (sum[h][w] < 0) {
                    negcount++;
                }
            }
        }

        if (poscount != negcount) {
            return false;
        }

        //check slash
        boolean slash1 = true;
        boolean slash2 = true;
        for (int h = 0; h < height - 1; h++) {
            slash1 = slash1 && (sum[h][h] == sum[h + 1][h + 1] && sum[h][h] != 0);
            slash2 = slash2 &&
                     (sum[h][width - h - 1] == sum[h + 1][width - h - 2] &&
                      sum[h][width - h - 1] != 0);
        }
        if (slash1 || slash2) {
            return false;
        }

        return true;
    }


    private static boolean checkxyAxisBalance(byte[][] pattern) {
        int height = pattern.length;
        int width = pattern[0].length;
        int xcount = 0;
        for (int h = 0; h < height; h++) {
            int x = 0;
            for (int w = 0; w < width; w++) {
                x += pattern[h][w];
            }
            if (0 == xcount) {
                xcount = x;
            } else if (xcount != x) {
                return false;
            }
        }
        int ycount = 0;
        for (int w = 0; w < width; w++) {
            int y = 0;
            for (int h = 0; h < height; h++) {
                y += pattern[h][w];
            }
            if (0 == ycount) {
                ycount = y;
            } else if (ycount != y) {
                return false;
            }

        }
        return true;
    }


}
