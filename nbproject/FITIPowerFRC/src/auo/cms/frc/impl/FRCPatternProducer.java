package auo.cms.frc.impl;

import java.util.ArrayList;
import java.util.Arrays;

import auo.cms.frc.FRCPattern;

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
public class FRCPatternProducer {
    private final int length = 8;
    private final int frame = 4;

    public FRCPattern[] getCheckedFRCPattern(int level) {
        switch (level) {
        case 1:
            return getCheckedFRCPatternLv1();
        case 2:
            return getCheckedFRCPatternLv2();
        case 3:
            return getCheckedFRCPatternLv3();
        default:
            return null;
        }

    }

    private FRCPattern[] getCheckedFRCPatternLv1() {
        ArrayList<FRCPattern> list = new ArrayList<FRCPattern>();

        for (int a = 0; a < length - 3; a++) {
            for (int b = a + 1; b < length - 2; b++) {
                for (int c = b + 1; c < length - 1; c++) {
                    for (int d = c + 1; d < length; d++) {
                        boolean[][] basepattern = new boolean[frame][length];
                        basepattern[0][a] = true;
                        basepattern[1][b] = true;
                        basepattern[2][c] = true;
                        basepattern[3][d] = true;

                        Mapping[] mappings = Mapping.values();
                        for (Mapping mapping : mappings) {
                            FRCPattern p = block24ToFRCPattern(
                                    basepattern, 2, 4, mapping);
                            if (CheckTool.checkArtifacts(p, false, true)) {
                                list.add(p);
                            }

                        }
                    }
                }
            }
        }
        int size = list.size();
        return list.toArray(new FRCPattern[size]);

    }

    /**
     * 0: *_
     *    _*
     *
     * 1: _*
     *    *_
     * @param based boolean[][]
     * @param index int
     * @param type int
     */
    static void setFRCBased(boolean[][] based, int index, int type) {
        switch (type) {
        case 0:
            based[0][index] = true;
            based[1][index + 1] = true;
            break;
        case 1:
            based[0][index + 1] = true;
            based[1][index] = true;
            break;
        }
    }

    static String toString(boolean[][] array) {
        StringBuilder buf = new StringBuilder();
        for (int h = 0; h < array.length; h++) {
            for (int w = 0; w < array[0].length; w++) {
                buf.append(array[h][w] ? "1 " : "0 ");
            }
            buf.append('\n');
        }
        return buf.toString();
    }

    static boolean checkGreen(boolean[][] based) {
        int height = based.length;
        for (int h = 0; h < height; h++) {
            if (based[h][1] == T && based[h][4] == T ||
                based[h][4] == T && based[h][7] == T ||
                based[h][2] == T && based[h][5] == T ||
                based[h][0] == T && based[h][3] == T ||
                based[h][3] == T && based[h][6] == T) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<boolean[][]> getBasedFRCPatternLv2(boolean checkGreen) {
        ArrayList<boolean[][]> list = new ArrayList<boolean[][]>();
        int wcount = length / 2;

        int count = 0;
        boolean[][] based = new boolean[2][length];
        for (int x = 0; x < wcount; x++) {
            for (int xt = 0; xt < 2; xt++) {
                for (int y = x + 2; y < wcount; y++) {
                    for (int yt = 0; yt < 2; yt++) {

                        FRCUtil.clean(based);
                        setFRCBased(based, x * 2, xt);
                        setFRCBased(based, y * 2, yt);

                        if (!checkGreen || checkGreen(based)) {
                            list.add(FRCUtil.copy(based));
                            count++;
                        }
                    }
                }
            }
        }
        System.out.println(count);
        return list;
    }

    private boolean isContain(ArrayList < boolean[][] > list, boolean[][] pattern) {
        int height = pattern.length;
        for (boolean[][] b : list) {
            boolean eq = true;
            for (int h = 0; h < height; h++) {
                eq = eq && Arrays.equals(b[h], pattern[h]);
            }
            if (eq) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<boolean[][]> filterQuadrant(ArrayList < boolean[][] > based) {
        ArrayList<boolean[][]> filtered = new ArrayList<boolean[][]>();
        boolean[][] template = based.get(0);
        int height = template.length;
        int width = template[0].length;
        int halfh = height / 2;
        int halfw = width / 2;
        boolean[][][] Q = new boolean[4][halfh][halfw];
        for (boolean[][] b : based) {
            for (int h = 0; h < halfh; h++) {
                for (int w = 0; w < halfw; w++) {
                    Q[0][h][w] = b[h][w];
                    Q[1][h][w] = b[h + halfh][w];
                    Q[2][h][w] = b[h][w + halfw];
                    Q[3][h][w] = b[h + halfh][w + halfw];
                }
            }
            for (int q = 0; q < 4; q++) {
                if (!isContain(filtered, Q[q])) {
                    filtered.add(FRCUtil.copy(Q[q]));
                    System.out.println(toString(Q[q]));
                }
            }
        }
        return filtered;
    }

    private ArrayList<boolean[][]> getFrameFRCPatternLv2(boolean checkGreen, boolean checkAdjoin,
            boolean checkSlash, int checkSlashLength) {
        ArrayList<boolean[][]> based = getBasedFRCPatternLv2(checkGreen);
        int basedsize = based.size();

        int count = 0;
        boolean[][] frame = new boolean[length][length];
        ArrayList<boolean[][]> result = new ArrayList<boolean[][]>();
        int index = 0;

        for (int L1 = 0; L1 < basedsize; L1++) {
            setBooleanArray(based.get(L1)[0], frame, 0);
            setBooleanArray(based.get(L1)[1], frame, 1);
            for (int L2 = 0; L2 < basedsize; L2++) {
                setBooleanArray(based.get(L2)[0], frame, 2);
                setBooleanArray(based.get(L2)[1], frame, 3);
                for (int L3 = 0; L3 < basedsize; L3++) {
                    setBooleanArray(based.get(L3)[0], frame, 4);
                    setBooleanArray(based.get(L3)[1], frame, 5);
                    for (int L4 = 0; L4 < basedsize; L4++) {
                        setBooleanArray(based.get(L4)[0], frame, 6);
                        setBooleanArray(based.get(L4)[1], frame, 7);
                        if (CheckTool.checkVerticalCount(frame, 2) &&
                            (checkAdjoin && CheckTool.checkAdjoin(frame)) &&
                            (checkSlash && CheckTool.checkSlash(frame, checkSlashLength)) &&
                            CheckTool.checkQuadrantCount(frame)) {
                            result.add(FRCUtil.copy(frame));
                            System.out.println((count++) + "\n" + toString(frame));
                        } else {

                        }
                        index++;
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<FRCPattern> getCheckedFRCPatternLv2_2(boolean checkGreen) {

        ArrayList<boolean[][]> frame = getFrameFRCPatternLv2(checkGreen, true, true, 4);
        int size = frame.size();

        int count = 0;
        ArrayList<FRCPattern> list = new ArrayList<FRCPattern>();
        boolean doCheckSame = false;
        boolean doCheckSlash = true;
        long index = 0;

        for (int f1 = 0; f1 < size; f1++) {
            for (int f2 = 0; f2 < size; f2++) {
                if (f1 == f2) {
                    continue;
                }
                for (int f3 = 0; f3 < size; f3++) {
                    if (f1 == f3 || f2 == f3) {
                        continue;
                    }
                    for (int f4 = 0; f4 < size; f4++) {
                        if (f1 == f4 || f2 == f4 || f3 == f4) {
                            continue;
                        }
                        index++;
//                        if (index < 2833967296L) {
//                        if (index < 1826000000) {
//                            continue;
//                        }

                        boolean[][] f1_ = frame.get(f1);
                        boolean[][] f2_ = frame.get(f2);
                        boolean[][] f3_ = frame.get(f3);
                        boolean[][] f4_ = frame.get(f4);
                        boolean[][][][] p = new boolean[][][][] { {
                                            f1_, f2_, f3_, f4_}
                        };

                        FRCPattern frc = new FRCPattern(p);

                        if ((index) % 1000000 == 0) {
                            System.out.println(index + " " + count + " - " +
                                               ((double) index) / 98344960000L);
                        }
                        if (CheckTool.checkPolarityNonZero(frc) &&
                            CheckTool.checkAdjoin(frc) &&
                            (!doCheckSlash || CheckTool.checkSlash(frc, 5)) &&
                            (!doCheckSame || !CheckTool.checkSame(frc, list)) &&
                            CheckTool.checkArtifacts(frc, true, true)
                                ) {
//
                            frc = new FRCPattern(FRCUtil.copy(p), frc.artifacts);
                            list.add(frc);
                            System.out.println(count++);
                            System.out.println(frc.toString());
                        }
                    }
                }
            }

        }
        System.out.println(count);
        return list;
    }


    private FRCPattern[] getCheckedFRCPatternLv2() {
        ArrayList<FRCPattern> list = new ArrayList<FRCPattern>();

        int count = 0;
        for (int f1a = 0; f1a < length - 1; f1a++) {
            for (int f1b = f1a + 1; f1b < length; f1b++) {

                for (int f2a = 0; f2a < length - 1; f2a++) {
                    for (int f2b = f2a + 1; f2b < length; f2b++) {

                        for (int f3a = 0; f3a < length - 1; f3a++) {
                            for (int f3b = f3a + 1; f3b < length; f3b++) {

                                for (int f4a = 0; f4a < length - 1; f4a++) {
                                    for (int f4b = f4a + 1; f4b < length; f4b++) {
                                        count++;

                                        boolean[][] basepattern = new boolean[
                                                frame][length];

                                        basepattern[0][f1a] = true;
                                        basepattern[0][f1b] = true;

                                        basepattern[1][f2a] = true;
                                        basepattern[1][f2b] = true;

                                        basepattern[2][f3a] = true;
                                        basepattern[2][f3b] = true;

                                        basepattern[3][f4a] = true;
                                        basepattern[3][f4b] = true;

                                        Mapping[] mappings = Mapping.values();
                                        for (Mapping mapping : mappings) {
                                            FRCPattern p = blockToFRCPattern(
                                                    basepattern, 2, 4,
                                                    mapping);
                                            if (CheckTool.checkArtifacts(p, true, true)) {
                                                list.add(p);
                                            }
                                            p = blockToFRCPattern(
                                                    basepattern, 4, 2,
                                                    mapping);
                                            if (CheckTool.checkArtifacts(p, true, true)) {
                                                list.add(p);
                                            }
                                            p = blockToFRCPattern(
                                                    basepattern, 1, 8,
                                                    mapping);
                                            if (CheckTool.checkArtifacts(p, true, true)) {
                                                list.add(p);
                                            }
                                            p = blockToFRCPattern(
                                                    basepattern, 8, 1,
                                                    mapping);
                                            if (CheckTool.checkArtifacts(p, true, true)) {
                                                list.add(p);
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

        int size = list.size();
        return list.toArray(new FRCPattern[size]);

    }

    private ArrayList<boolean[]> getBasedFRCPatternLv3() {
        ArrayList<boolean[]> based = new ArrayList<boolean[]>();

        for (int L1 = 0; L1 < length - 4; L1++) {
            for (int L2 = L1 + 2; L2 < length - 2; L2++) {
                for (int L3 = L2 + 2; L3 < length; L3++) {
                    boolean[] b = new boolean[length];
                    b[L1] = true;
                    b[L2] = true;
                    b[L3] = true;
                    based.add(b);

                }
            }
        }

        return based;
    }

    private void setBooleanArray(boolean[] set, boolean[][] array, int index) {
        if (array[index].length != set.length) {
            throw new IllegalArgumentException(
                    "array[index].length != set.length");
        }
        int size = set.length;

        for (int x = 0; x < size; x++) {
            array[index][x] = set[x];
        }
    }


    private FRCPattern[] getCheckedFRCPatternLv3() {

        ArrayList<boolean[]> based = getBasedFRCPatternLv3();
        int basedsize = based.size();

        ArrayList<boolean[][]> oneframeList = new ArrayList<boolean[][]>();
        long count = 0;
        long totalcount = 0;
        boolean[][] basepattern = new boolean[length][length];
        for (int L1 = 0; L1 < basedsize; L1++) {
            setBooleanArray(based.get(L1),
                            basepattern, 0);
            for (int L2 = 0; L2 < basedsize; L2++) {
                setBooleanArray(based.get(L2),
                                basepattern, 1);
                for (int L3 = 0; L3 < basedsize; L3++) {
                    setBooleanArray(based.get(L3),
                                    basepattern, 2);
                    for (int L4 = 0; L4 < basedsize; L4++) {
                        if (L1 == L2 && L2 == L3 && L3 == L4) {
                            continue;
                        }
                        setBooleanArray(based.get(L4),
                                        basepattern, 3);
                        for (int L5 = 0; L5 < basedsize; L5++) {
                            setBooleanArray(based.get(L5),
                                            basepattern, 4);
                            for (int L6 = 0; L6 < basedsize; L6++) {
                                setBooleanArray(based.get(L6),
                                                basepattern, 5);
                                for (int L7 = 0; L7 < basedsize; L7++) {
                                    setBooleanArray(based.get(L7),
                                            basepattern, 6);
                                    for (int L8 = 0; L8 < basedsize; L8++) {

                                        setBooleanArray(based.get(L8),
                                                basepattern, 7);
                                        totalcount++;
                                        if (CheckTool.checkVerticalCount(basepattern, 3)) {
                                            oneframeList.add(FRCUtil.copy(
                                                    basepattern));
                                            System.out.println(count++ +"/" +
                                                    totalcount);
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println(oneframeList.size());

        ArrayList<FRCPattern> list = new ArrayList<FRCPattern>();
        int size = list.size();
        return list.toArray(new FRCPattern[size]);

    }


    static boolean T = true;
    static boolean F = false;


    public static void example(String[] args) {

        int height = 2;
        int width = 4;
        int length = height * width;
        int frame = 4;

        int count = 0;
        int okindex = 0;
        for (int a = 0; a < length - 3; a++) {
            for (int b = a + 1; b < length - 2; b++) {
                for (int c = b + 1; c < length - 1; c++) {
                    for (int d = c + 1; d < length; d++) {
                        boolean[][] basepattern = new boolean[frame][length];
                        basepattern[0][a] = true;
                        basepattern[1][b] = true;
                        basepattern[2][c] = true;
                        basepattern[3][d] = true;

                        Mapping[] mappings = {Mapping.Reflect};
                        for (Mapping mapping : mappings) {
                            FRCPattern p = block24ToFRCPattern(
                                    basepattern, height, width, mapping);

                            //======================================================
                            FRCPattern frc = new FRCPattern(p.pattern, false);
                            ArtifactsAnalyzer analyzer = new ArtifactsAnalyzer(
                                    ArtifactsAnalyzer.Inversion.Dot, frc);
                            boolean ok = false;
                            /*if (analyzer.checkSubpixelBaseArtifacts(1)) {
                                System.out.print("All ok: " + count);
                                ok = true;
                                                         } else*/
                            if (analyzer.checkSubpixelBaseArtifacts(1, true, true, false, false, true, false)) {
                                System.out.print("Lv1 ok: " + count);
                            } else if (analyzer.checkSubpixelBaseArtifacts(1, true, false, false, false, true, false)) {
                                System.out.print("Lv2 ok: " + count);
                            }

                            //======================================================

                        }
                        count++;

                    }
                }
            }
        }
    }


    enum Mapping {
        Copy, Reflect,
    }


    static FRCPattern blockToFRCPattern(boolean[][] b, int height,
                                        int width,
                                        Mapping mapping) {
        mapping = Mapping.Reflect;
        int size = b[0].length;
        if (height * width != size) {
            throw new IllegalArgumentException("height * width != size");
        }
        int newheight = 8, newwidth = 8;
        //[level][frame][h][w]
        boolean[][][][] frcPattern = new boolean[1][4][newheight][newwidth];
        for (int f = 0; f < 4; f++) {

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    frcPattern[0][f][h][w] = b[f][h * width + w];
                }
            }

            //�Ĥ@���B�z
            boolean lineMirror = false;
            for (int windex = 1; windex < newwidth / width; windex++) {
                boolean copy = false;
                switch (mapping) {
                case Copy:
                    copy = true;
                    break;
                case Reflect:
                    copy = windex % 2 != 1;
                    break;
                }
                for (int w0 = 0; w0 < width; w0++) {
                    int w = windex * width + (copy ? w0 : width - w0 - 1);
                    for (int h0 = 0; h0 < height; h0++) {
                        int h = (width != 1 || windex % 2 == 0) ? h0 :
                                height - h0 - 1;
                        frcPattern[0][f][h][w] = frcPattern[0][f][h0][w0];
                    }
                }
                lineMirror = true;
            }

            //��l���B�z
            for (int hindex = 1; hindex < newheight / height; hindex++) {
                boolean copy = false;
                switch (mapping) {
                case Copy:
                    copy = true;
                    break;
                case Reflect:
                    copy = hindex % 2 != 1;
                    break;
                }

                for (int h0 = 0; h0 < height; h0++) {
                    int h = hindex * height + (copy ? h0 : height - h0 - 1);
                    for (int w0 = 0; w0 < newwidth; w0++) {
                        int w = lineMirror || hindex % 2 == 0 ? w0 :
                                newwidth - w0 - 1;
                        frcPattern[0][f][h][w] = frcPattern[0][f][h0][w0];
                    }
                }

            }

        }
        return new FRCPattern(frcPattern);

    }

    static FRCPattern block24ToFRCPattern(boolean[][] b, int height,
                                          int width,
                                          Mapping mapping) {
        blockToFRCPattern(b, height, width, mapping);
        int size = b[0].length;
        if (height * width != size) {
            throw new IllegalArgumentException("height * width != size");
        }
        int newheight = 8, newwidth = 8;

        //[level][frame][h][w]
        boolean[][][][] frcPattern = new boolean[1][4][newheight][newwidth];
        for (int f = 0; f < 4; f++) {
            for (int x = 0; x < size; x++) {
                int h = x / width;
                int w = x % width;
                frcPattern[0][f][h][w] = b[f][h * width + w];
                switch (mapping) {
                case Copy:
                    frcPattern[0][f][h][w + 4] = b[f][h * width + w];
                    break;
                case Reflect:
                    frcPattern[0][f][h][7 - w] = b[f][h * width + w];
                    break;
                }

            }

            switch (mapping) {
            case Copy:
                for (int x = 0; x < 3; x++) {
                    for (int h = 0; h < height; h++) {
                        for (int w = 0; w < width * 2; w++) {
                            frcPattern[0][f][h + (x + 1) * height][w]
                                    = frcPattern[0][f][h][w];
                        }
                    }
                }
                break;

            case Reflect:
                for (int x = 0; x < 3; x++) {
                    for (int hindex = 0; hindex < height; hindex++) {
                        int h = (x % 2 == 0) ? 1 - hindex : hindex;
                        for (int w = 0; w < width * 2; w++) {
                            frcPattern[0][f][h + (x + 1) * height][w]
                                    = frcPattern[0][f][hindex][w];
                        }
                    }

                }
                break;
            }
        }

        return new FRCPattern(frcPattern);
    }

    static boolean[][][][] block42ToFRCPattern(boolean[][] b, int height,
                                               int width,
                                               Mapping mapping) {
        int size = b[0].length;
        if (height * width != size) {
            throw new IllegalArgumentException("height * width != size");
        }
        int newheight = 8, newwidth = 8;

        //[level][frame][h][w]
        boolean[][][][] frcPattern = new boolean[1][4][newheight][newwidth];
        for (int f = 0; f < 4; f++) {
            for (int x = 0; x < size; x++) {
                int h = x / width;
                int w = x % width;
                frcPattern[0][f][h][w] = b[f][h * width + w];
                switch (mapping) {
                case Copy:
                    frcPattern[0][f][h][w + 4] = b[f][h * width + w];
                    break;
                case Reflect:
                    frcPattern[0][f][h][7 - w] = b[f][h * width + w];
                    break;
                }

            }

            switch (mapping) {
            case Copy:
                for (int x = 0; x < 3; x++) {
                    for (int h = 0; h < height; h++) {
                        for (int w = 0; w < width * 2; w++) {
                            frcPattern[0][f][h + (x + 1) * height][w]
                                    = frcPattern[0][f][h][w];
                        }
                    }
                }
                break;

            case Reflect:
                for (int x = 0; x < 3; x++) {
                    for (int hindex = 0; hindex < height; hindex++) {
                        int h = (x % 2 == 0) ? 1 - hindex : hindex;
                        for (int w = 0; w < width * 2; w++) {
                            frcPattern[0][f][h + (x + 1) * height][w]
                                    = frcPattern[0][f][hindex][w];
                        }
                    }

                }
                break;
            }
        }

        return frcPattern;
    }

}
