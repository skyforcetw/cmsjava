package fiti.cms.frc.impl;

import java.util.Arrays;
import java.util.LinkedList;

import fiti.cms.frc.FRCPattern;

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
public class FRCUtil {
    static String toString(byte[][] array) {
        StringBuilder buf = new StringBuilder();
        int height = array.length;

        for (int h = 0; h < height; h++) {
            int width = array[h].length;
            for (int w = 0; w < width; w++) {
                buf.append(array[h][w]);
                buf.append(' ');
            }
            buf.append('\n');
        }
        return buf.toString();
    }

    static String toString(boolean[][] array) {
        StringBuilder buf = new StringBuilder();
        int height = array.length;
        int width = array[0].length;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                buf.append(array[h][w] ? "1 " : "0 ");
            }
            buf.append('\n');
        }
        return buf.toString();
    }

    static void clean(boolean[][] array) {
        int height = array.length;
        int width = array[0].length;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                array[h][w] = false;
            }
        }
    }

    static boolean[][] copy(boolean[][] array) {
        int height = array.length;
        int width = array[0].length;
        boolean[][] copy = new boolean[height][];
        for (int h = 0; h < height; h++) {
            copy[h] = Arrays.copyOf(array[h], width);
        }
        return copy;
    }

    public static boolean[][][][] copy(boolean[][][][] pattern) {
        int level = pattern.length;
        int frame = pattern[0].length;
        boolean[][][][] newpattern = new boolean[level][frame][][];
        for (int l = 0; l < level; l++) {
            for (int f = 0; f < frame; f++) {
                newpattern[l][f] = FRCUtil.copy(pattern[l][f]);
            }
        }
        return newpattern;
    }


    public static byte[][] copy(byte[][] array) {
        int height = array.length;
        int width = array[0].length;
        byte[][] copy = new byte[height][];
        for (int h = 0; h < height; h++) {
            copy[h] = Arrays.copyOf(array[h], width);
        }
        return copy;
    }

    static int ok;
    static boolean[][][] getOkPosition(FRCPattern p) {
        boolean[][][][] bpattern = p.pattern;
        int height = bpattern[0][0].length;
        int width = bpattern[0][0][0].length;
        int frame = 4;
        boolean[][][] check = new boolean[frame][height][width];

        //======================================================================================
        // 找到ok的position
        //======================================================================================
        int count = 0;
        int eachok = 0;
        for (int f = 0; f < frame; f++) {

            int checkok = 0;
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    if (false == bpattern[0][f][h][w]) { //找到可以擺的位置(空缺的)

                        boolean[][][][] npattern = FRCUtil.copy(bpattern);
                        npattern[0][f][h][w] = true;
                        FRCPattern frc = new FRCPattern(npattern, false);
                        ArtifactsAnalyzer analyzer = new ArtifactsAnalyzer(
                                ArtifactsAnalyzer.Inversion.Dot, frc);

                        double[][] sum = analyzer.analyzeSubpixelBaseSum(
                                1);
                        if (CheckTool.checkNoneTwoArtifacts(sum)) {
                            count++;
                            checkok++;
                            check[f][h][w] = true;
                        } else {
//                            int a = 1;
                        }

                    }
                }

            }
            eachok += (checkok / height);
        }
        ok = eachok / frame;
        return check;
    }

    static boolean isOverlap(int[] position, int index) {
        for (int x = 0; x < index; x++) {
            if (position[x] == position[index]) {
                return true;
            }
        }
        return false;
    }

    static LinkedList<boolean[][]> eight16FramePicker(boolean[][] orgFrame, boolean[][] okPos
            ) {

        int height = okPos.length;
        int width = okPos[0].length;
        //先計算ok的數量
        int[] okcount = new int[height];
        for (int h = 0; h < height; h++) {
            int ok = 0;
            for (int w = 0; w < width; w++) {
                if (okPos[h][w]) {
                    ok++;
                }
            }
            okcount[h] = ok;
        }

        //把ok的index排出來
        byte[][] okPosIndex = new byte[height][];
        for (int h = 0; h < height; h++) {
            okPosIndex[h] = new byte[okcount[h]];
            int index = 0;
            for (byte w = 0; w < width; w++) {
                if (okPos[h][w]) {
                    okPosIndex[h][index++] = w;
                }
            }
        }

        LinkedList<boolean[][]> result = new LinkedList<boolean[][]>();
        int[] position = new int[height];

        for (int L1 = 0; L1 < okcount[0]; L1++) {
            position[0] = okPosIndex[0][L1];

            for (int L2 = 0; L2 < okcount[1]; L2++) {
                position[1] = okPosIndex[1][L2];
                if (isOverlap(position, 1)) {
                    continue;
                }

                for (int L3 = 0; L3 < okcount[2]; L3++) {
                    position[2] = okPosIndex[2][L3];
                    if (isOverlap(position, 2)) {
                        continue;
                    }

                    for (int L4 = 0; L4 < okcount[3]; L4++) {
                        position[3] = okPosIndex[3][L4];
                        if (isOverlap(position, 3)) {
                            continue;
                        }

                        for (int L5 = 0; L5 < okcount[4]; L5++) {
                            position[4] = okPosIndex[4][L5];
                            if (isOverlap(position, 4)) {
                                continue;
                            }

                            for (int L6 = 0; L6 < okcount[5]; L6++) {
                                position[5] = okPosIndex[5][L6];
                                if (isOverlap(position, 5)) {
                                    continue;
                                }

                                for (int L7 = 0; L7 < okcount[6]; L7++) {
                                    position[6] = okPosIndex[6][L7];
                                    if (isOverlap(position, 6)) {
                                        continue;
                                    }

                                    for (int L8 = 0; L8 < okcount[7]; L8++) {
                                        position[7] = okPosIndex[7][L8];
                                        if (isOverlap(position, 7)) {
                                            continue;
                                        }

                                        boolean[][] frc = FRCUtil.copy(orgFrame);
                                        for (int x = 0; x < height; x++) {
                                            frc[x][position[x]] = true;
                                        }

                                        result.add(frc);

                                    }
                                }

                            }
                        }

                    }
                }
            }
        }

        return result;
    }

}
