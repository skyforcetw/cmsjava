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

import auo.cms.frc.FRCPattern;
import static auo.cms.frc.impl.FRC3_16Util.*;
import shu.math.array.IntArray;

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
public class FRC5_16PatternProducer {

    public static void main(String[] args) throws IOException {
        String dir = "FRC/FRC 5-16/";
//         String _2_8_FRCFilename = "2_8frc-auo.txt";
        String _2_8_FRCFilename = "2_8frc-2.txt";

        boolean choiceFirstFor16 = false; //16要加在哪裡
//        boolean choiceFirstFor16 = true; //16要加在哪裡

        boolean plusAdjoin = true; //是否要+1在相鄰的frame

        boolean showBasicInfo = true;
        boolean showResult = true;
        boolean showNoInfoResult = true;
        ArtifactsAnalyzer.InversionMode inversion = ArtifactsAnalyzer.InversionMode._1V1H;

        /**
         * 5/16可以用新或舊的2/8來尋找.
         * 因為新的2/8候選有8組, 所以8組都拿來試試看.
         */
        for (int x = 0; x < 8; x++) {
            main(x, dir, _2_8_FRCFilename, choiceFirstFor16, plusAdjoin, showBasicInfo, showResult, showNoInfoResult,
                 inversion);
        }
    }

    public static void main(int indexOfFRC, String dir, String _2_8_FRCFilename, boolean choiceFirstFor16,
                            boolean plusAdjoin, boolean showBasicInfo,
                            boolean showResult,
                            boolean showFRCPatternOnly, ArtifactsAnalyzer.InversionMode inversion) throws IOException {

        FRCPattern frcPattern = null;

        frcPattern = new FRCPattern(dir + _2_8_FRCFilename, indexOfFRC);
        if (showBasicInfo) {
            System.out.println(frcPattern);
        }
        boolean[][][] ok4frame = FRCUtil.getOkPosition(frcPattern);

        if (showBasicInfo) {
            for (int x = 0; x < ok4frame.length; x++) {
                System.out.println("ok frame: " + (x + 1));
                System.out.println(FRCUtil.toString(ok4frame[x]));
            }
        }
        boolean[][][] frcpattern = frcPattern.pattern[0];
        int framecount = frcpattern.length;

        LinkedList<Frame> frameList[] = new LinkedList[4];

        String pickfilename = dir + "pick" + (choiceFirstFor16 ? "1" : "2") +
                              (plusAdjoin ? "_" : "") + "+" + frcPattern.sourceInfo + ".obj";
        LinkedList<FRCPattern>
                frcList = null;

        //==========================================================================================
        // 生成
        //==========================================================================================
        if (new File(pickfilename).exists() && false) {
            try {
                ObjectInputStream f = new ObjectInputStream(new BufferedInputStream(new
                        FileInputStream(pickfilename)));
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
                int index = choiceFirstFor16 ? f : f + 1;
                if (plusAdjoin ? (f == 1 || f == 2) : index % 2 == 0) {
                    LinkedList<boolean[][]>
                            pickFrame = FRCUtil.eight16FramePicker(frcpattern[f],
                            ok4frame[f]);
                    for (boolean[][] pick : pickFrame) {
                        Frame frame = new Frame(pick);
                        frameList[f].add(frame);
                    }
                    int a = 1;

                } else {
                    Frame frame = new Frame(frcpattern[f]);
                    frameList[f].add(frame);
                }
            }

            try {
                frcList = frcPicker5(frameList, 0, 2, dir, false, false, inversion);
                System.out.println(frcList.size());

                ObjectOutputStream f = new ObjectOutputStream(new BufferedOutputStream(new
                        FileOutputStream(pickfilename)));
                f.writeObject(frcList);
                f.flush();
                f.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

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

            boolean ok = true;
            for (int f = 0; f < 4; f++) {
                if (!(hcount[f] >= vcount[f] && vcount[f] >= Lcount[f]) ||
                    frc.maxSlash > 4 || Lcount[f] > 0
                        /*||
                                                                 vcount[f] != 0*/) {
                    ok = false;
                    break;
                }
            }
            if (IntArray.sum(hcount) > 8) {
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
                        if ((h > 0 && balancedSum[h - 1][w] == 0) ||
                            (w > 0 && balancedSum[h][w - 1] == 0) ||
                            (w < width - 1 && balancedSum[h][w + 1] == 0) ||
                            (h < height - 1 && balancedSum[h + 1][w] == 0)) {
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
//                ok = false;
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
                    System.out.println("No." + (index++) + " " + "maxAdjoinPolarity: " +
                                       maxAdjoinPolarity);
                    System.out.println("twovG: " + twovG);
                    System.out.println(frc);
                }
                if (showResult && showFRCPatternOnly) {
                    frc.artifacts = null;
                    frc.balancedSum = null;
                    frc.greenPixel = null;
                    frc.twohcount = null;
                    frc.twovcount = null;
                    frc.Lcount = null;
                    frc.maxHLine = 0;
                    frc.maxSlash = 0;
                    System.out.println(frc);
                }

            }
        }
        System.out.println("filter/all: " + filterFRCList.size() + " / " + frcList.size());
//        System.out.println(mintwovG);
    }


    static LinkedList<FRCPattern> frcPicker5(LinkedList<Frame> frameList[], int f1_start,
            int method, String dir,
            boolean checkingOverlapping, boolean skipSameFrame,
            ArtifactsAnalyzer.InversionMode inversion) throws IOException {
        return frcPicker5(frameList[0], frameList[1], frameList[2], frameList[3], f1_start, 1, false,
                          0, method, dir, checkingOverlapping, skipSameFrame, inversion);
    }

    private static LinkedList<FRCPattern> frcPicker5(final LinkedList<Frame> frame01,
            final LinkedList<Frame> frame02, final LinkedList<Frame> frame03,
            final LinkedList<Frame> frame04, int f1_start, int f1_step, boolean oneF1Only,
            int f2_start, int method, String dir, boolean checkingOverlapping,
            boolean skipSameFrame, ArtifactsAnalyzer.InversionMode inversion
            ) throws
            IOException {
        final int size1 = frame01.size();
        final int size2 = frame02.size();
//    frcPicker2Count = 0;
        final LinkedList<FRCPattern> result = new LinkedList<FRCPattern>();

        Calendar c = Calendar.getInstance();
        String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE);
        String frame_filename = dir + time + "-frc3-16" + method + ".obj";

        final Writer log = new BufferedWriter(new FileWriter(dir + "frc" + method + ".log"));
        final ObjectOutputStream obj = new ObjectOutputStream(new BufferedOutputStream(new
                FileOutputStream(frame_filename)));

        int f1_end = oneF1Only ? f1_start + 1 : size1;
        long start = 0, end = 0;
        for (int f1 = f1_start; f1 < f1_end; f1 += f1_step) {
            if (f1 % 100 == 0) {
                System.out.println("f1: " + f1);
            }
            if (start != 0) {
                long cost = (end - start);
                long totalcost = (f1_end - f1) * cost / 1000;
                System.out.println(getCostInfo(cost, totalcost));
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
                    if (checkingOverlapping &&
                        (isOverlapping(frame1, frame3) || isOverlapping(frame2, frame3))) {
                        continue;
                    }
                    boolean[][] frame3_ = lineToFrame(frame3.frameIndex);

                    for (int f4 = 0; f4 < size4; f4++) {
                        if (skipSameFrame && f2 == f4) {
                            continue;
                        }

                        Frame frame4 = frame04.get(f4);
                        if (checkingOverlapping &&
                            (isOverlapping(frame1, frame4) ||
                             isOverlapping(frame2, frame4) ||
                             isOverlapping(frame3, frame4))) {
                            continue;
                        }

                        boolean[][] frame4_ = lineToFrame(frame4.frameIndex);
                        boolean[][][][] frcArray = { {frame1_, frame2_, frame3_,
                                frame4_}
                        };

                        FRCPattern frc = new FRCPattern(frcArray);
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc);
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, true, true); //
                        boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, false, true, true, inversion); //0
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, true, false); //
//                                  boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, false, true); //
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, false, false); //
//                                 boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, false, false, false); //

                        if (overallAnalyze) {
                            result.add(frc);
//                            frcPicker2Count++;
                            boolean writeToFile = false;
                            if (writeToFile) {
                                try {
                                    String text = f1 + " " + f2 + " " + f3 + " " + f4 +
                                                  "\n" +
                                                  frc.toString();
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
//    System.out.println("FRC Pick: " + frcPicker2Count);
        return result;
    }

}
