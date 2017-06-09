package auo.cms.frc.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.Calendar;
import java.util.LinkedList;

//import static auo.cms.frc.impl.FRC3_16PatternProducer.*;
import static auo.cms.frc.impl.FRC3_16Util.*;
import shu.math.array.IntArray;
import auo.cms.frc.*;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2017</p>
 *
 * <p>
 * Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FRC5_16PatternProducerNew {

    public static void main(String[] args) throws IOException {
        for (int x = 0; x < 359; x++) {
            System.out.println("Start process pattern No. " + x + " :");
            main(x, true, true, true);
            System.out.print("next...");

        }
    }

//    static boolean check_plus_one_sum(byte[][] plus_one_sum) {
//        final int height = plus_one_sum.length;
//        final int width = plus_one_sum[0].length;
//        for (int h = 1; h < height - 1; h++) {
//            for (int w = 1; w < width - 1; w++) {
//                if (plus_one_sum[h][w] == 2) {
//                    boolean check = plus_one_sum[h - 1][w - 1] == 2 || plus_one_sum[h - 1][w] == 2 || plus_one_sum[h - 1][w + 1] == 2
//                            || plus_one_sum[h][w - 1] == 2 || plus_one_sum[h][w + 1] == 2
//                            || plus_one_sum[h + 1][w - 1] == 2 || plus_one_sum[h + 1][w] == 2 || plus_one_sum[h + 1][w + 1] == 2;
//                    if (check) {
//                        return false;
//                    }
//                }
//            }
//        }
//        return true;
//    }

    static int get_artifacts_16sum(byte[][] artifacts) {
        final int height = artifacts.length;
        final int width = artifacts[0].length;
        final int check_w = 4;
        final int check_count = width - check_w + 1;
        int check_result[] = new int[check_count * check_count];
        int check_index = 0;

        for (int h = 0; h < height - (check_w - 1); h++) {
            for (int w = 0; w < width - (check_w - 1); w++) {
                int count_of_1 = 0;
                for (int h0 = h; h0 < h + check_w; h0++) {
                    for (int w0 = w; w0 < w + check_w; w0++) {
                        if (artifacts[h0][w0] > 0) {
                            count_of_1++;
                        }
                    }

                }
                check_result[check_index++] = count_of_1;

            }
        }
        return IntArray.max(check_result);//>= 10;
 
    }

    public static void main(int indexOfFRC, boolean showBasicInfo, boolean showResult,
            boolean showNoInfoResult) throws IOException {
        String dir = "FRC/FRC 5-16/";
        FRCPattern frcPattern = null;

//        frcPattern = new FRCPattern(dir + "2_8frc-auo.txt", indexOfFRC); //4,6
//        frcPattern = new FRCPattern(dir + "2_8frc-2.txt", indexOfFRC);
        frcPattern = new FRCPattern(dir + "my2-8.txt", indexOfFRC);

        if (showBasicInfo) {
            System.out.println(frcPattern);
        }
        boolean[][][] ok4frame = FRCUtil.getOkPosition(frcPattern);

        if (showBasicInfo && false) {
            for (int x = 0; x < ok4frame.length; x++) {
                System.out.println("Produce ok frame " + (x + 1));
                System.out.println(FRCUtil.toString(ok4frame[x]));
            }
        }
        boolean[][][] frcpattern = frcPattern.pattern[0];
        int framecount = frcpattern.length;

//        Method method = Method.Eight16;
//        Method method = Method.AB;
        LinkedList<Frame> frameList[] = new LinkedList[4];

        boolean choiceFirstFor16 = false; //16要加在哪裡
//        boolean choiceFirstFor16 = true; //16要加在哪裡

        boolean plusAdjoin = true; //是否要+1在相鄰的frame

        String pickfilename = dir + "pick" + (choiceFirstFor16 ? "1" : "2")
                + (plusAdjoin ? "_" : "") + "+" + frcPattern.sourceInfo + ".obj";
        LinkedList<FRCPattern> frcList = null;

        //==========================================================================================
        // 生成
        //==========================================================================================
        if (/*new File(pickfilename).exists() &&*/false) {
//            try {
//                ObjectInputStream f = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pickfilename)));
//                frcList = (LinkedList<FRCPattern>) f.readObject();
//                f.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            } catch (ClassNotFoundException ex) {
//                ex.printStackTrace();
//            }
        } else {

            //==========================================================================================
            // 從ok frame排出所有可能, 先過濾有重疊的部分
            //==========================================================================================
            for (int f = 0; f < framecount; f++) {
                frameList[f] = new LinkedList<Frame>();
//                int index = choiceFirstFor16 ? f : f + 1;
                //final boolean do_pick = plusAdjoin ? (f == 1 || f == 2) : index % 2 == 0;
                final boolean do_pick = true;
                if (do_pick) {
                    LinkedList<boolean[][]> pickFrame = FRCUtil.five16FramePicker(frcpattern[f],
                            ok4frame[f]);

                    for (boolean[][] pick : pickFrame) {
                        Frame frame = new Frame(pick);
                        if (CheckTool.h_check16_n_even(frame, 5) && CheckTool.h_check16_n_odd(frame, 5) && CheckTool.v_check16_n(pick, 5) && CheckTool.grid_check16_n(pick, 5)) {
                            frameList[f].add(frame);
                        }
                    }
                    int a = 1;

                } else {
                    Frame frame = new Frame(frcpattern[f]);
                    frameList[f].add(frame);
                }
            }
            //==========================================================================================

            try {
                frcList = frcPicker5(frameList, 0, dir, false, false);
                System.out.println("Picker process done, result size: " + frcList.size());

                ObjectOutputStream f = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(pickfilename)));
                f.writeObject(frcList);
                f.flush();
                f.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        System.out.println("FRC size before filter: " + frcList.size());

        //==========================================================================================
        // 過濾
        //==========================================================================================
        LinkedList<FRCPattern> filterFRCList = new LinkedList<FRCPattern>();
        int index = 0;
        for (FRCPattern frc : frcList) {
            frc.caculateInfo();
            int[] Lcount = frc.Lcount;
            int[] hcount = frc.twohcount;
            int[] vcount = frc.twovcount;
            int[][] greenPixel = frc.greenPixel;
            byte[][] artifacts = frc.artifacts;

            boolean ok = true;

//            System.out.println(frc.getArtifactsString(true));
            if (get_artifacts_16sum(artifacts) >= 8) {
                ok = false;
            }
            if (! CheckTool.check_plus_one_sum(frc.plusOneSum,2)) {
                ok = false;
            }

            //if(frc.maxSlash > 4) {
            if (frc.maxSlash > 8) {
                ok = false;
            }
            for (int f = 0; f < 4; f++) {
                if (!(hcount[f] >= vcount[f] && vcount[f] >= Lcount[f]) || Lcount[f] > 0 /*||  vcount[f] != 0*/) {
                    ok = false;
                    break;
                }
            }
            if (IntArray.sum(hcount) > 6) {
                ok = false;
            }
            if ((IntArray.sum(hcount) + IntArray.sum(vcount)) > 10) {
                ok = false;
            }

            int size = greenPixel.length;
            for (int x = 0; x < size; x++) {
                for (int c = 0; c < 3; c++) {
                    if (0 == greenPixel[x][c]) {
                        ok = false;
                        break;
                    }
                }
            }
            byte[][] balancedSum = frc.balancedSum;
            int height = balancedSum.length;
            int width = balancedSum[0].length;

            //鄰接都是0的狀況就去除
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    if (balancedSum[h][w] == 0) {
                        if ((h > 0 && balancedSum[h - 1][w] == 0)
                                || (w > 0 && balancedSum[h][w - 1] == 0)
                                || (w < width - 1 && balancedSum[h][w + 1] == 0)
                                || (h < height - 1 && balancedSum[h + 1][w] == 0)) {
                            ok = false;
                            break;
                        }
                    }
                }
            }
            //橫線長度>=4就去除
            if (frc.maxHLine >= 4) {
                ok = false;
            }
            int[] posAdjoin = CheckTool.getAdjoinCount(frc, false);
            int[] neaAdjoin = CheckTool.getAdjoinCount(frc, true);
            int maxAdjoinPolarity = 0;
            for (int x = posAdjoin.length - 1; x > 0; x--) {
                if (posAdjoin[x] != 0 || neaAdjoin[x] != 0) {
                    maxAdjoinPolarity = x;
                    break;
                }
            }
            if (maxAdjoinPolarity > 3) {
                ok = false;
            }

            int[][] twovGcount = CheckTool.getTwovGcount(frc);
            int[] twovGsimplify = new int[twovGcount.length];
            for (int x = 0; x < twovGcount.length; x++) {
                twovGsimplify[x] = IntArray.sum(twovGcount[x]);
            }
            int twovG = IntArray.sum(twovGsimplify);
//            mintwovG = twovG < mintwovG ? twovG : mintwovG;
            if (twovG > 40) {
//                ok = false;
            }

            //======================================================================================
            // 結束判斷並且印出
            //======================================================================================
            if (ok) {
                filterFRCList.add(frc);
                if (showResult) {
                    System.out.print("In picker process, find Ok result, No." + ((index++) + 1) + " " + "maxAdjoinPolarity: "
                            + maxAdjoinPolarity);
                    System.out.println(" twovG: " + twovG);
                    System.out.println(frc);
                }
//                boolean noinfo = true;
//                boolean noinfo = false;
                if (showResult && showNoInfoResult) {
                    frc.artifacts = null;
                    frc.balancedSum = null;
                    frc.greenPixel = null;
                    frc.twohcount = null;
                    frc.twovcount = null;
                    frc.Lcount = null;
                    frc.maxHLine = 0;
                    frc.maxSlash = 0;
                    frc.plusOneSum = null;
//                    System.out.println(frc);
                }
//                if (showResult) {
//                    System.out.println(frc);
//                }
//                System.out.println(Arrays.toString(adjoinPolarityCountArray) + " " + frc.maxSlash);

            }
        }
        System.out.println("Filter process done, result filter/all: " + filterFRCList.size() + " / " + frcList.size());
//        System.out.println(mintwovG);
    }

//    static int mintwovG = 999;
    static LinkedList<FRCPattern> frcPicker5(LinkedList<Frame>[] frameList, int f1_start, String dir, boolean checkingOverlapping, boolean skipSameFrame) throws IOException {
        return frcPicker5(frameList[0], frameList[1], frameList[2], frameList[3], f1_start, 1, false,
                0, dir, checkingOverlapping, skipSameFrame);
    }

    private static LinkedList<FRCPattern> frcPicker5(final LinkedList<Frame> frame01, final LinkedList<Frame> frame02, final LinkedList<Frame> frame03, final LinkedList<Frame> frame04,
            int f1_start, int f1_step, boolean oneF1Only, int f2_start, String dir, boolean checkingOverlapping, boolean skipSameFrame) throws
            IOException {
        final int size1 = frame01.size();
        final int size2 = frame02.size();
//    frcPicker2Count = 0;
        final LinkedList<FRCPattern> result = new LinkedList<FRCPattern>();

        Calendar c = Calendar.getInstance();
        String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE);
        //String frame_filename = dir + time + "-frc3-16" + method + ".obj";
        String frame_filename = dir + time + "-frc3-16.obj";

        //final Writer log = new BufferedWriter(new FileWriter(dir + "frc" + method + ".log"));
        final Writer log = new BufferedWriter(new FileWriter(dir + "frc.log"));
        final ObjectOutputStream obj = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(frame_filename)));

        int f1_end = oneF1Only ? f1_start + 1 : size1;
        long start = 0, end = 0;
        System.out.print("On Processing picker...\nin process in f1");
        for (int f1 = f1_start; f1 < f1_end; f1 += f1_step) {
//            if (f1 % 10 == 0) {
            System.out.print(" " + f1 + "...");
//            }
            if (start != 0) {
                long cost = (end - start);
                long totalcost = (f1_end - f1) * cost / 1000;
                System.out.println(getCostInfo(cost, totalcost));
            }
            System.gc();
            start = System.currentTimeMillis();
            final Frame frame1 = frame01.get(f1);

            for (int f2 = f2_start; f2 < size2; f2++) {
//                if (f2 % 10 == 0) {
//                    System.out.print(" " + f2 + "...");
//                }

                final Frame frame2 = frame02.get(f2);

                if (checkingOverlapping && isOverlapping(frame1, frame2)) {
                    continue;
                }

                System.gc();

                final boolean[][] frame1_ = lineToFrame(frame1.frameIndex);

                int size3 = frame03.size();
                int size4 = frame04.size();
                boolean[][] frame2_ = lineToFrame(frame2.frameIndex);
                for (int f3 = 0; f3 < size3; f3++) {
                    if (skipSameFrame && f1 == f3) {
                        continue;
                    }
                    final Frame frame3 = frame03.get(f3);
                    if (checkingOverlapping
                            && (isOverlapping(frame1, frame3) || isOverlapping(frame2, frame3))) {
                        continue;
                    }
                    boolean[][] frame3_ = lineToFrame(frame3.frameIndex);

                    for (int f4 = 0; f4 < size4; f4++) {
                        if (skipSameFrame && f2 == f4) {
                            continue;
                        }

                        Frame frame4 = frame04.get(f4);
                        if (checkingOverlapping
                                && (isOverlapping(frame1, frame4)
                                || isOverlapping(frame2, frame4)
                                || isOverlapping(frame3, frame4))) {
                            continue;
                        }

                        boolean[][] frame4_ = lineToFrame(frame4.frameIndex);
                        boolean[][][][] frcArray = {{frame1_, frame2_, frame3_,
                            frame4_}
                        };

                        FRCPattern frc = new FRCPattern(frcArray);
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc);
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, true, true); //
//                        boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, false, true, true); //final solution
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, true, false); //
//                                  boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, false, true); //
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, false, false); //
                        boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, false, false, false); //

                        if (overallAnalyze) {
                            result.add(frc);
//                            frcPicker2Count++;
                            boolean writeToFile = false;
                            if (writeToFile) {
                                try {
                                    String text = f1 + " " + f2 + " " + f3 + " " + f4
                                            + "\n"
                                            + frc.toString();
                                    log.write(text);
                                    obj.writeObject(frcArray);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        frc = null;
                        frcArray = null;
                        frame4_ = null;

                    }
                }
                frame2_ = null;
            }
            end = System.currentTimeMillis();
        }
        System.out.println("\nProcessing done.");

        obj.flush();
        obj.close();
        log.flush();
        log.close();
//    System.out.println("FRC Pick: " + frcPicker2Count);
        return result;
    }

}
