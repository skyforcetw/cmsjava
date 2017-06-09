package auo.cms.frc.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import auo.cms.frc.FRCPattern;
import static auo.cms.frc.impl.FRC3_16Util.isOverlapping;
import static auo.cms.frc.impl.FRC3_16Util.lineToFrame;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Calendar;
import shu.math.array.IntArray;

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
public class FRC3_16PatternProducerNew {

    enum BaseOn {
        FRC1_8, FRC1_16
    }

    enum Method {
        AB, Eight16
    }

    static boolean check_neighbor_frame(Frame frame) {
        int height = frame.frameIndex.length;
        for (int h = 1; h < height; h++) {
            byte[] pre = frame.frameIndex[h - 1];
            byte[] current = frame.frameIndex[h];
            for (byte c : current) {
                for (byte p : pre) {
                    if (c == p) {
                        return false;
                    }
                }
            }

        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        String dir = "FRC/FRC 3-16/";
        BaseOn baseon = BaseOn.FRC1_8;
        FRCPattern frcPattern = null;
        switch (baseon) {
            case FRC1_8:
                frcPattern = new FRCPattern(dir + "1_8frc-auo.txt", 0);
                break;
            case FRC1_16:
                frcPattern = new FRCPattern(dir + "1_16frc.txt", 0);
                break;
        }
        System.out.println(frcPattern);
        boolean[][][] ok4frame = FRCUtil.getOkPosition(frcPattern);

        if (false) {
            System.out.println("ok frame");
            for (boolean[][] o : ok4frame) {
                System.out.println(FRCUtil.toString(o));
            }
        }
        boolean[][][] frcpattern = frcPattern.pattern[0];
        int framecount = frcpattern.length;

//        Method method = Method.Eight16;
//        Method method = Method.AB;
        LinkedList<Frame> frameList[] = new LinkedList[4];

//        boolean choiceFirstFor16 = false; //16要加在哪裡
//        boolean choiceFirstFor16 = true; //16要加在哪裡
//        boolean plusAdjoin = true; //是否要+1在相鄰的frame
        String pickfilename = dir + "pick" + ".obj";
        LinkedList<FRCPattern> frcList = null;

        if (new File(pickfilename).exists() && false) {
            try {
                ObjectInputStream f = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pickfilename)));
                frcList = (LinkedList<FRCPattern>) f.readObject();
                f.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        } else {

            for (int f = 0; f < framecount; f++) {
                frameList[f] = new LinkedList<Frame>();

                LinkedList<boolean[][]> pickFrame = FRCUtil.five16FramePicker(frcpattern[f], ok4frame[f]);
                for (boolean[][] pick : pickFrame) {
                    Frame frame = new Frame(pick);

                    if (CheckTool.h_check16_n_even(frame, 3)
                            && CheckTool.h_check16_n_odd(frame, 3)
                            && CheckTool.v_check16_n(pick, 3) 
//                            && CheckTool.grid_check_n(pick, 3)
                            && check_neighbor_frame(frame)) {
                        frameList[f].add(frame);
                    }
//                    frameList[f].add(frame);
                }

            }

            frcList = frcPicker3(frameList, 0, dir, false, false);
            System.out.println(frcList.size());

        }

        LinkedList<FRCPattern> filterFRCList = new LinkedList<FRCPattern>();
        int index = 0;
        for (FRCPattern frc : frcList) {
            frc.caculateInfo();
            int[] Lcount = frc.Lcount;
            int[] hcount = frc.twohcount;
            int[] vcount = frc.twovcount;
            int[][] greenPixel = frc.greenPixel;

            boolean ok = true;

            if (!CheckTool.check_plus_one_sum(frc.plusOneSum, 2)) {
                ok = false;
            }
            if (!CheckTool.check_plus_one_sum(frc.plusOneSum, 3)) {
                ok = false;
            }
            for (int f = 0; f < 4; f++) {
                if (!(hcount[f] >= vcount[f] && vcount[f] >= Lcount[f])
                        || frc.maxSlash > 4
                        || Lcount[f] > 0 /*||
                                                                 vcount[f] != 0*/) {
                    ok = false;
                    break;
                }
            }
            int size = greenPixel.length;
            for (int x = 0; x < size; x++) {
                for (int c = 0; c < 3; c++) {
                    if (0 == greenPixel[x][c]) {
                        ok = false; //釋放很多
                        break;
                    }
                }
            }
            byte[][] balancedSum = frc.balancedSum;
            int height = balancedSum.length;
            int width = balancedSum[0].length;
//            int[] adjoinPolarityArray = new int[8];

            //鄰接都是0的狀況就去除
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    if (balancedSum[h][w] == 0) {
                        if ((h > 0 && balancedSum[h - 1][w] == 0)
                                || (w > 0 && balancedSum[h][w - 1] == 0)
                                || (w < width - 1 && balancedSum[h][w + 1] == 0)
                                || (h < height - 1 && balancedSum[h + 1][w] == 0)) {
//                            ok = false; //release 3xxx
                            break;
                        }
                    }
                }
            }

            //橫線長度>=4就去除
            if (frc.maxHLine >= 2) {
                ok = false;
            }

            int[] posAdjoin = CheckTool.getAdjoinCount(frc, false);
            int[] negAdjoin = CheckTool.getAdjoinCount(frc, true);
            int maxAdjoinPolarity = 0;
            for (int x = posAdjoin.length - 1; x > 0; x--) {
                if (posAdjoin[x] != 0 || negAdjoin[x] != 0) {
                    maxAdjoinPolarity = x;
                    break;
                }
            }
            if (maxAdjoinPolarity > 4) {
                ok = false;
            }

            int[][] twovGcount = CheckTool.getTwovGcount(frc);
            int[] twovGsimplify = new int[twovGcount.length];
            for (int x = 0; x < twovGcount.length; x++) {
                twovGsimplify[x] = IntArray.sum(twovGcount[x]);
            }
            int twovG = IntArray.sum(twovGsimplify);
            if (twovG > 12) {
                ok = false;
            }

            if (ok) {
                filterFRCList.add(frc);
                System.out.println((index++) + " " + "maxAdjoinPolarity: " + maxAdjoinPolarity);
//                System.out.println("maxAdjoinPolarity: "+maxAdjoinPolarity);
                System.out.println(frc);
                boolean noinfo = true;
//                boolean noinfo = false;
                if (noinfo) {
                    frc.artifacts = null;
                    frc.balancedSum = null;
                    frc.greenPixel = null;
                    frc.twohcount = null;
                    frc.twovcount = null;
                    frc.Lcount = null;
                    frc.maxHLine = 0;
                    frc.maxSlash = 0;
                }
//                System.out.println(frc);
//                System.out.println(Arrays.toString(adjoinPolarityCountArray) + " " + frc.maxSlash);

            }
        }
        System.out.println(filterFRCList.size() + " / " + frcList.size());
    }

    static void frcPicker(LinkedList< boolean[][]>[] frameresult) {

    }

    static LinkedList<FRCPattern> frcPicker3(LinkedList<Frame> frameList[], int f1_start,
            String dir,
            boolean checkingOverlapping, boolean skipSameFrame) throws IOException {
        return frcPicker3(frameList[0], frameList[1], frameList[2], frameList[3], f1_start, 1, false,
                0, dir, checkingOverlapping, skipSameFrame);
    }

    private static LinkedList<FRCPattern> frcPicker3(final LinkedList<Frame> frame01,
            final LinkedList<Frame> frame02, final LinkedList<Frame> frame03,
            final LinkedList<Frame> frame04, int f1_start, int f1_step, boolean oneF1Only,
            int f2_start, String dir,
            boolean checkingOverlapping,
            boolean skipSameFrame) throws
            IOException {
        final int size1 = frame01.size();
        final int size2 = frame02.size();
//        frcPicker2Count = 0;
        final LinkedList<FRCPattern> result = new LinkedList<FRCPattern>();

        Calendar c = Calendar.getInstance();
        String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE);
        String frame_filename = dir + time + "-frc3-16" + ".obj";

        final Writer log = new BufferedWriter(new FileWriter(dir + "frc" + ".log"));
        final ObjectOutputStream obj = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(frame_filename)));

        int f1_end = oneF1Only ? f1_start + 1 : size1;
        long start = 0, end = 0;
        for (int f1 = f1_start; f1 < f1_end; f1 += f1_step) {
            if (f1 % 100 == 0) {
                System.out.println("f1: " + f1);
            }
            if (start != 0) {
                long cost = (end - start);
                long totalcost = (f1_end - f1) * cost / 1000;
//                System.out.println(getCostInfo(cost, totalcost));
            }
            System.gc();
            start = System.currentTimeMillis();
            final Frame frame1 = frame01.get(f1);

            for (int f2 = f2_start; f2 < size2; f2++) {

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
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, true, true); //0
//                        boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, false, true, true); //31104
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, true, false); //0
//                                  boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, false, true); //0
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, false, false); //0
                        boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, false, false, false); //f6656

                        if (overallAnalyze) {
                            result.add(frc);

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

        obj.flush();
        obj.close();
        log.flush();
        log.close();

        return result;
    }

}
