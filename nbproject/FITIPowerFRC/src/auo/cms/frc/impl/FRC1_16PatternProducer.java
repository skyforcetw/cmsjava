package auo.cms.frc.impl;

import java.util.ArrayList;

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
public class FRC1_16PatternProducer {
    public FRC1_16PatternProducer() {

    }

    static boolean[][] getMethod1BasePattern() {
        boolean[][] basedPattern = new boolean[8][8];
        for (int x = 0; x < 8; x++) {
            basedPattern[x][x] = true;
        }
        return basedPattern;
    }

    public static void method1() {
        boolean[][] basedPattern = getMethod1BasePattern();
        int basesize = basedPattern.length;

        ArrayList<int[]> frame1_list = new ArrayList<int[]>();
        for (int L1 = 0; L1 < basesize; L1++) {
            for (int L2 = 0; L2 < basesize && L2 != L1; L2++) {
                for (int L3 = 0; L3 < basesize & L3 != L2 && L3 != L1; L3++) {
                    for (int L4 = 0; L4 < basesize && L4 != L3 && L4 != L2 && L4 != L1; L4++) {
                        int[] array = {L1, L2, L3, L4};
                        frame1_list.add(array);
                    }

                }
            }

        }

        int frame1_size = frame1_list.size();

        ArrayList<int[][]> frame13_list = new ArrayList<int[][]>();

        for (int f1 = 0; f1 < frame1_size; f1++) {
            for (int f2 = 0; f2 < frame1_size && f2 != f1; f2++) {
                int[] f1_ = frame1_list.get(f1);
                int[] f3_ = frame1_list.get(f2);
                int x = 0;
                for (x = 0; x < 4 && f1_[x] != f3_[x]; x++) {
                    ;
                }
                if (x == 4) {
                    int[][] f1f3 = {f1_, f3_};
                    frame13_list.add(f1f3);
                }
            }
        }
        int frame13_size = frame13_list.size();
        for (int f13 = 0; f13 < frame13_size; f13++) {
            for (int f24 = 0; f24 < frame13_size && f13 != f24; f24++) {

            }
        }

    }



    public static void main(String[] args) {
        System.out.println("Start");
        method1();
        System.out.println("End");
    }
}
