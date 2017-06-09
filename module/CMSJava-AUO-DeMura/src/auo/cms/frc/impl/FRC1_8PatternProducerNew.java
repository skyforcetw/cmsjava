/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auo.cms.frc.impl;

import auo.cms.frc.FRCPattern;
import static auo.cms.frc.impl.FRC3_16Util.isOverlapping;
import static auo.cms.frc.impl.FRC3_16Util.lineToFrame;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.Calendar;
import java.util.LinkedList;

/**
 *
 * @author skyforce.shen
 */
public class FRC1_8PatternProducerNew {

    static boolean check(int... args) {
        int length = args.length;
        for (int x = 0; x < length; x++) {
            if (!check0(x, args)) {
                return false;
            }
        }
        return true;
    }

    static boolean check0(int check_idx, int... args) {
        int length = args.length;
        for (int x = 0; x < length; x++) {
            if (x == check_idx) {
                continue;
            } else if (args[x] == args[check_idx]) {
                return false;
            }

        }
        return true;
    }

    static LinkedList<Frame> get_ok_frame() {
        LinkedList<Frame> ok_frame = new LinkedList<>();
        for (int L1 = 0; L1 < 8; L1++) {
            for (int L2 = 0; L2 < 8; L2++) {
                if (!check(L1, L2)) {
                    continue;
                }

                for (int L3 = 0; L3 < 8; L3++) {
                    if (!check(L1, L2, L3)) {
                        continue;
                    }

                    for (int L4 = 0; L4 < 8; L4++) {
                        if (!check(L1, L2, L3, L4)) {
                            continue;
                        }

                        for (int L5 = 0; L5 < 8; L5++) {
                            if (!check(L1, L2, L3, L4, L5)) {
                                continue;
                            }

                            for (int L6 = 0; L6 < 8; L6++) {
                                if (!check(L1, L2, L3, L4, L5, L6)) {
                                    continue;
                                }

                                for (int L7 = 0; L7 < 8; L7++) {
                                    if (!check(L1, L2, L3, L4, L5, L7)) {
                                        continue;
                                    }

                                    for (int L8 = 0; L8 < 8; L8++) {
                                        if (!check(L1, L2, L3, L4, L5, L7, L8)) {
                                            continue;
                                        }
                                        boolean[][] pattern = new boolean[8][8];
                                        pattern[0][L1] = true;
                                        pattern[1][L2] = true;
                                        pattern[2][L3] = true;
                                        pattern[3][L4] = true;
                                        pattern[4][L5] = true;
                                        pattern[5][L6] = true;
                                        pattern[6][L7] = true;
                                        pattern[7][L8] = true;
                                        Frame frame = new Frame(pattern);
                                        if (CheckTool.h_check16_n_even(frame, 2)
                                                && CheckTool.h_check16_n_odd(frame, 2)
                                                && CheckTool.v_check16_n(pattern, 2)
                                                && CheckTool.grid_check16_n(pattern, 2)
                                                && CheckTool.v_check8_n(pattern, 1)
                                                && CheckTool.grid_check16_n_overlap(pattern, 2)
                                                && CheckTool.grid_check9_n_overlap(pattern, 2)) {
                                            ok_frame.add(frame);
//                                            count++;
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return ok_frame;
    }

    public static void main(String[] args) throws IOException {
        LinkedList<Frame> ok_frame = get_ok_frame();
        final int pattern_length = ok_frame.size();

        LinkedList<FRCPattern> frcPicker3 = frcPicker3(ok_frame, ok_frame, ok_frame, ok_frame, true, true);

//        int count = 0;
//        for (int f1 = 0; f1 < pattern_length; f1++) {
//            for (int f2 = 0; f2 < pattern_length; f2++) {
//                if (f1 == f2) {
//                    continue;
//                }
//                for (int f3 = 0; f3 < pattern_length; f3++) {
//                    if (f1 == f3 || f2 == f3) {
//                        continue;
//                    }
//                    for (int f4 = 0; f4 < pattern_length; f4++) {
//                        if (f1 == f4 || f2 == f4 || f3 == f4) {
//                            continue;
//                        }
//                        count++;
//                    }
//
//                }
//
//            }
//
//        }
//         Frame frame = new Frame(ok_frame.get(0));
//        CheckTool.grid_check9_n_overlap(ok_frame.get(0), 1);
        System.out.println(frcPicker3.size());
        int a = 1;
    }

    private static LinkedList<FRCPattern> frcPicker3(final LinkedList<Frame> frame01,
            final LinkedList<Frame> frame02, final LinkedList<Frame> frame03,
            final LinkedList<Frame> frame04,
            //            int f1_start, int f1_step, boolean oneF1Only,
            //            int f2_start, String dir,
            boolean checkingOverlapping,
            boolean skipSameFrame) throws
            IOException {
        final int size1 = frame01.size();
        final int size2 = frame02.size();
//        frcPicker2Count = 0;
        final LinkedList<FRCPattern> result = new LinkedList<FRCPattern>();

        Calendar c = Calendar.getInstance();
        String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE);
//        String frame_filename = dir + time + "-frc3-16" + ".obj";

//        final Writer log = new BufferedWriter(new FileWriter(dir + "frc" + ".log"));
//        final ObjectOutputStream obj = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(frame_filename)));
//        int f1_end = oneF1Only ? f1_start + 1 : size1;
        long start = 0, end = 0;
        for (int f1 = 0; f1 < size1; f1 += 1) {
//            if (f1 % 100 == 0) {
                System.out.println("f1: " + f1);
//            }
            if (start != 0) {
                long cost = (end - start);
                long totalcost = (size1 - f1) * cost / 1000;
//                System.out.println(getCostInfo(cost, totalcost));
            }
            System.gc();
            start = System.currentTimeMillis();
            final Frame frame1 = frame01.get(f1);

            for (int f2 = 0; f2 < size2; f2++) {

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
                        boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, true, true); //0
//                        boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, false, true, true); //31104
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, true, false); //0
//                                  boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, false, true); //0
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, false, false); //0
//                        boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, false, false, false); //f6656

                        if (overallAnalyze) {
                            result.add(frc);

//                            boolean writeToFile = false;
//                            if (writeToFile) {
//                                try {
//                                    String text = f1 + " " + f2 + " " + f3 + " " + f4
//                                            + "\n"
//                                            + frc.toString();
//                                    log.write(text);
//                                    obj.writeObject(frcArray);
//                                } catch (IOException ex) {
//                                    ex.printStackTrace();
//                                }
//                            }
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

//        obj.flush();
//        obj.close();
//        log.flush();
//        log.close();
        return result;
    }
}
