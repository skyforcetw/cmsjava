package auo.cms.frc.impl;

import java.util.*;

import auo.cms.frc.*;
import java.io.IOException;

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
public class FRC3_8PatternProducer {
    public FRC3_8PatternProducer() {
        super();
    }


    /**
     * �ˬd�O�_�T�Ӭ۳s
     * ���G�S�J��
     * @param frc FRCPattern
     * @return boolean
     */
    static boolean checkThreeAdjoin(FRCPattern frc) {
        boolean[][][] pattern = frc.pattern[0];
        int frame = pattern.length;
        int height = pattern[0].length;
        int width = pattern[0][0].length;
        for (int h = 0; h < height; h++) {
            boolean adjoin = false;
            for (int f = 0; f < frame; f++) {
                for (int w = 0; w < width - 1; w++) {
                    if (pattern[f][h][w] == true && pattern[f][h][w] == pattern[f][h][w + 1]) {
                        if (adjoin) {
                            return true;
                        } else {
                            adjoin = true;
                        }
                    } else {
                        adjoin = false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * �Hleft/right���O�ˬd, frame�����O�_���ƥX�{adjoin
     * ���G�S�J��
     * @param frc FRCPattern
     * @return boolean
     */
    static boolean checkContinueAdjoinLeftRight(FRCPattern frc) {
        boolean[][][] pattern = frc.pattern[0];
        int frame = pattern.length;
        int height = pattern[0].length;
        int width = pattern[0][0].length;
        int halfw = width / 2;

        for (int h = 0; h < height; h++) {
            for (int f = 0; f < frame; f++) {
                boolean leftadjoin = false;
                boolean rightadjoin = false;
                for (int w = 0; w < halfw - 1; w++) {
                    if (pattern[f][h][w] == true && pattern[f][h][w] == pattern[f][h][w + 1]) {
                        if (leftadjoin) {
                            return true;
                        } else {
                            leftadjoin = true;
                        }
                    } else {
                        leftadjoin = false;
                    }

                    if (pattern[f][h][w + halfw] == true &&
                        pattern[f][h][w + halfw] == pattern[f][h][w + halfw + 1]) {
                        if (rightadjoin) {
                            return true;
                        } else {
                            rightadjoin = true;
                        }
                    } else {
                        rightadjoin = false;
                    }

                }
            }
        }
        return false;

    }


    static boolean check(String frcp, FRCPattern f) {
//        String frcp = "0	1	1	0	0	1	0	0		0	0	0	1	1	0	0	1		1	0	0	0	1	0	1	0		1	0	1	0	0	0	1	0";
        StringTokenizer tokenizer = new StringTokenizer(frcp);
        boolean[] frcb = new boolean[8 * 4];
        int n = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            frcb[n++] = token.equals("1");
        }
//        for (FRCPattern f : comboarray[1]) {
        boolean[][][][] pp = f.pattern;
        boolean[] compare = new boolean[8];
        boolean eq = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                compare[j] = frcb[i * 8 + j];
            }
            eq = eq && Arrays.equals(pp[0][i][0], compare);
        }
        return eq;
//            System.out.println(eq);
//        }

    }


    static final boolean F = false;
    static final boolean T = true;
    public static void main(String[] args) throws IOException {
        //==============================================================================================================
        // check rule
        //==============================================================================================================
        boolean checkPatternOnly = true; //�ucheck pattern�Ϊ� pattern�MArtifacts�@��check?
        boolean showImportantOnly = true; //�u���pattern���͹L�{�������n�T��

        boolean check38FRCMatch = false; //check�O�_��쥻��3/8�����|
        boolean checkArtifactsSymmetry = false; //�ˬdArtifacts���S����٩�
        boolean checkNone0Artifacts = false; //�ˬdArtifacts�O���O���Q���ʤ��M

        boolean checkArtifactsSymmetryAndReflect = true; //�ˬdArtifacts���S����٩ʩΪ���g
        boolean checkEqualsPolarity = true; //���t���ʼƶq�O�_���
        boolean checkLeftRightBalance = true; // �ˬd���k��+1�ƶq�O�_�ۦP
        boolean checkBalancedsum = true; //�ˬd���t���ʪ��ӼƥH�α׽u

        boolean filterAllFRCAtFinal = true; //�N�q�L�Ҧ�rule check��pattern, �A�̫ᰵArtifacts��check
        //==============================================================================================================


        //==============================================================================================================
        // inversion type
        //==============================================================================================================
        ArtifactsAnalyzer.InversionMode inversion = ArtifactsAnalyzer.InversionMode._1V1H;
        //==============================================================================================================

        // 3/8�O��2/8�h��X�Ӫ�, �ҥH�n���w2/8��pattern
        String sourcePatternFilename = "FRC/FRC 3-8/2_8frc-auo.txt";

        for (int x = 0; x < 2; x++) { //sourcePatternFilename�̭���pattern�ƶq
            FRCPattern p = new FRCPattern(sourcePatternFilename, x);
            ArrayList<FRCPattern> filteredFRCList = new ArrayList<FRCPattern>();

            System.out.println("FRC pattern(" + x + "): ");
            System.out.println(p);
            boolean[][][][] bpattern = p.pattern;
            int height = bpattern[0][0].length;
            int width = bpattern[0][0][0].length;
            int frame = bpattern[0].length;

            //======================================================================================
            // ���ok��position
            //======================================================================================
            boolean[][][] check = FRCUtil.getOkPosition(p);
            int okcount = FRCUtil.okcount;

            ArrayList<FRCPattern> [] comboarray = new ArrayList[height];

            //======================================================================================
            // �Hline�����h��X�Ҧ��զX
            //======================================================================================
            for (int h = 0; h < height; h++) {
                if (!showImportantOnly) {
                    System.out.println("h: " + h);
                }
                boolean[][][][] frc = new boolean[1][frame][1][width];

                //======================================================================================
                // ��z�Xok��position
                //======================================================================================
                int[][] okpos = new int[frame][okcount];
                for (int f = 0; f < frame; f++) {
                    int okindex = 0;
                    for (int w = 0; w < width; w++) {
                        frc[0][f][0][w] = bpattern[0][f][h][w];
                        if (check[f][h][w]) {
                            if (!showImportantOnly) {
                                System.out.print('v');
                            }
                            okpos[f][okindex++] = w;
                        } else if (!showImportantOnly) {
                            System.out.print(bpattern[0][f][h][w] ? '1' : '0');
                        }
                    }
                    if (!showImportantOnly) {
                        System.out.print(" ");
                    }
                }
                //======================================================================================
                if (!showImportantOnly) {
                    System.out.println("");
                }
                ArrayList<FRCPattern> combo = new ArrayList<FRCPattern>();

                //======================================================================================
                for (int f1 = 0; f1 < okcount; f1++) {
                    for (int f2 = 0; f2 < okcount; f2++) {
                        for (int f3 = 0; f3 < okcount; f3++) {
                            for (int f4 = 0; f4 < okcount; f4++) {
                                boolean[][][][] copyfrc = FRCUtil.copy(frc);

                                copyfrc[0][0][0][okpos[0][f1]] = true;
                                copyfrc[0][1][0][okpos[1][f2]] = true;
                                copyfrc[0][2][0][okpos[2][f3]] = true;
                                copyfrc[0][3][0][okpos[3][f4]] = true;

                                FRCPattern auofrc = new FRCPattern(copyfrc, false);
                                ArtifactsAnalyzer analyzer = new
                                        ArtifactsAnalyzer(inversion, auofrc);
                                //���B���]�w�ȦҼ{��dot inv, �Y�n��ܧO���X�ʤ覡, �n���s��Ҧ��B���]�p�覡
                                if (h % 2 == 1) {
                                    analyzer.setInverse(true);
                                }
                                byte[][] artifacts = analyzer.getArtifactsArray(1);

                                FRCPattern frcpattern = new FRCPattern(copyfrc,
                                        artifacts);
                                frcpattern.unbalancedSum = analyzer.analyzeSubpixelBaseUnbalancedSum(1);
                                frcpattern.balancedSum = analyzer.getBalancedSumArray(1);

                                if ((checkPatternOnly ?
                                     !hasSamePattern(combo, frcpattern) :
                                     !hasSameArtifacts(combo, frcpattern)) &&

                                    (!checkArtifactsSymmetryAndReflect ||
                                     CheckTool.checkArtifactsSymmetryAndReflect(frcpattern)) &&
                                    (!checkEqualsPolarity || CheckTool.checkPolarityBalance(frcpattern)) &&
                                    (!checkLeftRightBalance ||
                                     CheckTool.checkLeftRightBalance(frcpattern))
                                        ) {
                                    combo.add(frcpattern);
                                }

                                boolean show = false;
                                if (show) {
                                    for (int f = 0; f < 4; f++) {
                                        for (int w = 0; w < width; w++) {
                                            System.out.print(copyfrc[0][f][0][w] ?
                                                    '1' : '0');
                                        }
                                        System.out.print(' ');
                                    }
                                    System.out.println("");
                                }

                            }
                        }
                    }
                }
                //======================================================================================

                //��combo�X��
                boolean showocmbo = !showImportantOnly && true;
                if (showocmbo) {
                    int index = 1;
                    for (FRCPattern f : combo) {
                        System.out.print(index++ +": " + f);
                        boolean[][][][] pattern = f.pattern;
                        System.out.print("| ");
                        for (int ff = 0; ff < frame; ff++) {
                            int left = CheckTool.getTrueCount(pattern[0][ff][0], 0, 4);
                            int right = CheckTool.getTrueCount(pattern[0][ff][0], 4, 8);
                            System.out.print(left + "" + right + " ");
                        }
                        System.out.println("");
                    }
                }
                comboarray[h] = combo;

            }

            //======================================================================================
            // check�O�_��쥻��3/8�����|
            //======================================================================================

            if (check38FRCMatch) {
                FRCPattern p38 = new FRCPattern("FRC/3_8frc-auo.txt", 0);

                p38.caculateInfo();
                System.out.println("3/8:\n" + p38);

                for (int h = 0; h < height; h++) {
                    int size = comboarray[0].size();
                    for (int s = 0; s < size; s++) {
                        FRCPattern frc = comboarray[h].get(s);
                        boolean eq = true;

                        for (int f = 0; f < frame; f++) {
                            boolean[] pattern38 = p38.pattern[0][f][h];
                            boolean[] pattern = frc.pattern[0][f][0];
                            eq = eq && Arrays.equals(pattern, pattern38);
                        }
                        if (eq) {
                            System.out.println(h + " " + (s + 1) + " ok");
                        }

                    }

                }
            }
            //======================================================================================

            //======================================================================================
            // ��u���wline���զX
            //======================================================================================
//            boolean manualDecode = false;
//            System.out.println("");
//            if (manualDecode) {
//                int[] frccode = {4, 5, 4, 5, 4, 5, 4, 5};
//
//                int size = frccode.length;
//                for (int s = 0; s < size; s++) {
//                    FRCPattern f = comboarray[s].get(frccode[s] - 1);
//                    System.out.print(f);
//                }
//            }
            //======================================================================================

            //======================================================================================
            // ��C��line���Ҧ��զX���_��
            //======================================================================================
            boolean loopdecode = true;

            if (loopdecode) {
                int index = 0;
                int count = 0;
                long total = comboarray[0].size();
                for (int i = 1; i < comboarray.length; i++) {
                    total = total * comboarray[i].size();
                }

                int[] sizeArray = new int[comboarray.length];
                for (int size = 0; size < comboarray.length; size++) {
                    sizeArray[size] = comboarray[size].size();
                }

                for (int l1 = 0; l1 < sizeArray[0]; l1++) {
                    FRCPattern f1 = comboarray[0].get(l1);

                    for (int l2 = 0; l2 < sizeArray[1]; l2++) {
                        FRCPattern f2 = comboarray[1].get(l2);

                        for (int l3 = 0; l3 < sizeArray[2]; l3++) {
                            FRCPattern f3 = comboarray[2].get(l3);

                            FRCPattern frc123 = combine(new FRCPattern[] {f1, f2, f3});
                            if (CheckTool.checkContinueVerticalCount(frc123, 0, 3)) {
                                continue;
                            }

                            for (int l4 = 0; l4 < sizeArray[3]; l4++) {
                                FRCPattern f4 = comboarray[3].get(l4);

                                FRCPattern frc1234 = combine(new FRCPattern[] {f1, f2, f3, f4});

                                if (!CheckTool.checkPatternVerticalIsNotCount(frc1234, 3) ||
                                    CheckTool.checkContinueVerticalCount(frc1234, 1, 3)) {
                                    continue;
                                }

                                for (int l5 = 0; l5 < sizeArray[4]; l5++) {
                                    FRCPattern f5 = comboarray[4].get(l5);

                                    FRCPattern frc12345 = combine(new FRCPattern[] {f1, f2,
                                            f3, f4, f5});

                                    if (CheckTool.checkContinueVerticalCount(frc12345, 2, 3)) {
                                        continue;
                                    }

                                    for (int l6 = 0; l6 < sizeArray[5]; l6++) {
                                        FRCPattern f6 = comboarray[5].get(l6);

                                        FRCPattern frc123456 = combine(new FRCPattern[] {f1, f2,
                                                f3, f4, f5, f6});

                                        if (CheckTool.checkContinueVerticalCount(frc123456,
                                                3, 3)) {
                                            continue;
                                        }

                                        for (int l7 = 0; l7 < sizeArray[6]; l7++) {
                                            FRCPattern f7 = comboarray[6].get(l7);

                                            FRCPattern frc1234567 = combine(new FRCPattern[] {f1,
                                                    f2, f3, f4, f5, f6, f7});

                                            if (CheckTool.checkContinueVerticalCount(
                                                    frc1234567, 4, 3)) {
                                                continue;
                                            }

                                            for (int l8 = 0; l8 < sizeArray[7]; l8++) {
                                                index++;
                                                FRCPattern f8 = comboarray[7].get(l8);
                                                FRCPattern frc5678 = combine(new FRCPattern[] {f5,
                                                        f6, f7, f8});
                                                if (!CheckTool.
                                                        checkPatternVerticalIsNotCount(
                                                        frc5678, 3)) {
                                                    continue;
                                                }
                                                FRCPattern frc = combine(new FRCPattern[] {f1, f2,
                                                        f3, f4, f5, f6, f7, f8});

                                                if (CheckTool.checkContinueVerticalCount(
                                                        frc, 5, 3)) {
                                                    continue;
                                                }

                                                if (!CheckTool.checkVerticalCount(frc, 3)) {
                                                    continue;
                                                }

                                                frc.greenPixel = CheckTool.
                                                        getGreenPixelCount(frc);
                                                frc.Lcount = CheckTool.checkLCount(frc);

                                                if (!CheckTool.
                                                        checkVerticalLessThanHorizontal(frc)) {
                                                    continue;
                                                }

                                                if ((!checkBalancedsum ||
                                                        CheckTool.checkBalancedSum(frc, inversion)) &&
                                                        CheckTool.checkArtifacts(frc, inversion, checkNone0Artifacts,
                                                        checkArtifactsSymmetry)
                                                        ) {
                                                    count++;
                                                    if (filterAllFRCAtFinal) {
                                                        if (!hasSameArtifacts(filteredFRCList, frc)) {
                                                            filteredFRCList.add(frc);
                                                            System.out.println(frc);
                                                        }
                                                    } else {
                                                        System.out.println(count);
                                                        System.out.println(frc);
                                                    }

                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
            //======================================================================================

            System.out.println("Filtered FRC List size: " + filteredFRCList.size());
            for (FRCPattern frc : filteredFRCList) {
                System.out.println(frc);
            }

        }

    }


    static FRCPattern combine(FRCPattern[] frcArray) {
        int frame = frcArray[0].pattern[0].length;
        int width = frcArray[0].pattern[0][0][0].length;
        int height = frcArray.length;

        boolean[][][][] pattern = new boolean[1][frame][height][width];
        for (int f = 0; f < frame; f++) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    pattern[0][f][h][w] = frcArray[h].pattern[0][f][0][w];
//                    pattern[0][f][h][w] = frcArray[h]
                }
            }
        }
        return new FRCPattern(pattern);
    }


    static boolean hasSameArtifacts(ArrayList<FRCPattern> list, FRCPattern frc) {
        for (FRCPattern f : list) {
            if (f.equalsArtifacts(frc)) {
                return true;
            }
        }
        return false;
    }

    static boolean hasSameBalancedSum(ArrayList<FRCPattern> list, FRCPattern frc) {
        for (FRCPattern f : list) {
            if (f.equalsBalancedSum(frc)) {
                return true;
            }
        }
        return false;
    }


    static boolean hasSamePattern(ArrayList<FRCPattern> list, FRCPattern frc) {
        for (FRCPattern f : list) {
//            double[][] thissum = f.sum;
//            double[][] thatsum = frc.sum;
//            if (f.equals(frc) && equals(thissum, thatsum)) {
//                return true;
//            }
            if (f.equalsPattern(frc)) {
                return true;
            }
        }
        return false;
    }

    static boolean equals(double[][] d1, double[][] d2) {
        if (d1.length != d2.length) {
            throw new IllegalArgumentException("");
        }
        int length = d1.length;
        for (int x = 0; x < length; x++) {
            if (!Arrays.equals(d1[x], d2[x])) {
                return false;
            }
        }
        return true;
    }

    static boolean hasSame(ArrayList < double[] > list, double[] array) {
        for (double[] d : list) {
            if (Arrays.equals(d, array)) {
                return true;
            }
        }
        return false;
    }
}
