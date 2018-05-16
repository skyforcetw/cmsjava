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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import auo.cms.frc.FRCPattern;
import static auo.cms.frc.impl.FRC3_16Util.*;

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
public class FRC3_16PatternProducer {
    public FRC3_16PatternProducer() {

    }


    /**
     * 挑出2-line的結果
     * @param line1 ArrayList
     * @param line2 ArrayList
     * @param noAdjoin boolean 選出上下2 line沒有重疊的
     * @return ArrayList
     */
    private static ArrayList<byte[][]> twoLinePicker(ArrayList < byte[] > line1,
            ArrayList < byte[] > line2,
            boolean noAdjoin) {
        int size1 = line1.size();
        int size2 = line2.size();
        ArrayList<byte[][]> result = new ArrayList<byte[][]>();

        for (int x = 0; x < size1; x++) {
            for (int y = 0; y < size2; y++) {
                byte[] b1 = line1.get(x);
                byte[] b2 = line2.get(y);
                int b1size = b1.length;
                int b2size = b2.length;
                boolean ok = true;
                for (int i1 = 0; i1 < b1size; i1++) {
                    for (int i2 = 0; i2 < b2size; i2++) {
                        if (noAdjoin && b1[i1] == b2[i2]) {
                            ok = false;
                            break;
                        }
                    }
                }

                if (ok) {
                    byte[][] b = {b1, b2};
                    result.add(b);
                }
            }
        }
        return result;
    }

    /**
     *
     * @param line1 ArrayList
     * @param line2 ArrayList
     * @param noAdjoin boolean 是否要避開有上下相鄰的狀況
     * @return ArrayList
     */
    private static ArrayList<TwoLine> twoLinePicker2(ArrayList < byte[] > line1,
            ArrayList < byte[] > line2,
            boolean noAdjoin) {
        int size1 = line1.size();
        int size2 = line2.size();
        ArrayList<TwoLine> baseResult = new ArrayList<TwoLine>();
        ArrayList<TwoLine> finalResult = new ArrayList<TwoLine>();
        int index = 0;

        for (int x = 0; x < size1; x++) {
            for (int y = 0; y < size2; y++) {
                byte[] b1 = line1.get(x);
                byte[] b2 = line2.get(y);
                int b1size = b1.length;
                int b2size = b2.length;
                boolean ok = true;
                for (int i1 = 0; i1 < b1size; i1++) {
                    for (int i2 = 0; i2 < b2size; i2++) {
                        if (noAdjoin && b1[i1] == b2[i2]) { //兩個line若上下相鄰就跳過
                            ok = false;
                            break;
                        }
                    }
                }

                if (ok) {
                    byte[][] b = {b1, b2};
                    TwoLine t = new TwoLine(b, index++);
                    baseResult.add(t);
                }
            }
        }
        //==========================================================================================
        // 檢查overlap的狀況
        //==========================================================================================
        int size = baseResult.size();
        for (int m = 0; m < size - 1; m++) {
            TwoLine t0 = baseResult.get(m);
            ArrayList<Integer> noOverlap = new ArrayList<Integer>();
            for (int n = 0; n < size; n++) {
                if (m == n) {
                    continue;
                }
                TwoLine t1 = baseResult.get(n);
                if (!isOverlapping(t0, t1)) {
                    noOverlap.add(n);
                }
            }
            if (noOverlap.size() != 0) {
                t0.nonOverlapIndex = toIntArray(noOverlap);
                finalResult.add(t0);
            }

        }
        //==========================================================================================

        return finalResult;
    }

    private static boolean isAdjoin(TwoLine t0, TwoLine t1) {
        byte[] p0 = t0.position[1];
        byte[] p1 = t1.position[0];
        for (byte b : p0) {
            if (Arrays.binarySearch(p1, b) >= 0) {
                return true;
            }
        }
        return false;
    }

    private static int[] toIntArray(ArrayList<Integer> arrayList) {
        int size = arrayList.size();
        int[] result = new int[size];
        for (int x = 0; x < size; x++) {
            result[x] = arrayList.get(x);
        }
        return result;
    }


    private static ArrayList<byte[]> line1Picker() {
        ArrayList<byte[]> result = new ArrayList<byte[]>();
        for (byte x = 0; x < 8; x++) {
            byte[] b = {x};
            result.add(b);
        }
        return result;
    }

    private static ArrayList<byte[]> line2Picker() {
        ArrayList<byte[]> result = new ArrayList<byte[]>();
        for (int x = 0; x < 8; x++) {
            for (int y = (x + 1); y < 8 /*&& x != y && (x != y - 1) && (y != x - 1)*/; y++) {
                if (x == y || (x == y - 1) || (y == x - 1)) {
                    continue;
                }
                byte[] b = {(byte) x, (byte) y};
                result.add(b);
            }
        }
        return result;
    }


    private static String lineToString(byte[][] twoLine) {
        int lineHeight = twoLine.length;

        byte[][] array = new byte[lineHeight][8];
        for (int h = 0; h < lineHeight; h++) {
            for (byte b : twoLine[h]) {
                array[h][b] = 1;
            }
        }
        int height = array.length;
        int width = array[0].length;
        StringBuilder buf = new StringBuilder();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                buf.append(array[h][w] == 1 ? "1 " : "0 ");
            }
            buf.append('\n');
        }
        return buf.toString();
    }

    private static boolean isNonOverlapWithPreFrame(LinkedList<Frame> preFrameList,
            int twoLineIndex, TwoLine twoline) {
        for (Frame f : preFrameList) {
            int[] nonOverlap = f.twoLine[twoLineIndex].nonOverlapIndex;
            int index = twoline.index;
            if (Arrays.binarySearch(nonOverlap, index) >= 0) {
                return true;
            }
        }
        return false;
    }

    private static LinkedList<Frame> framePicker3(LinkedList<Frame> preFrameList,
            boolean checkWithPreFrame,
            ArrayList<TwoLine> twoLine, int[] check,
            boolean noAdjoin, boolean checkQuadrant, boolean Q1BiggerThanQ2) {
        int size = twoLine.size();
        LinkedList<Frame> result = new LinkedList<Frame>();
        int maskWidth = 8;
        int maskHeight = 8;
        byte[] checkVertical = new byte[maskWidth];
        boolean[][] framePlus1 = new boolean[maskHeight][maskWidth];
        int bypassCount = 0;
        int totalCount = 0;

        int bypassByPreFrameCount = 0;

        for (int L1 = 0; L1 < size; L1++) {
            System.out.println("L1 in framePicker3: " + L1);
            TwoLine t1 = twoLine.get(L1);
            if (null == t1.nonOverlapIndex) {
                continue;
            }
            if (checkWithPreFrame && null != preFrameList &&
                !isNonOverlapWithPreFrame(preFrameList, 0, t1)) {
                bypassByPreFrameCount++;
                continue;
            }

            for (int L2 = 0; L2 < size; L2++) {
                if (L1 == L2) {
                    continue;
                }
                TwoLine t2 = twoLine.get(L2);
                if (null == t2.nonOverlapIndex) {
                    continue;
                }
                if (checkWithPreFrame && null != preFrameList &&
                    !isNonOverlapWithPreFrame(preFrameList, 1, t2)) {
                    bypassByPreFrameCount++;
                    continue;
                }
                if (noAdjoin && isAdjoin(t1, t2)) {
                    continue;
                }

                for (int L3 = 0; L3 < size; L3++) {
                    if (L2 == L3 || L1 == L3) {
                        continue;
                    }
                    TwoLine t3 = twoLine.get(L3);
                    if (null == t3.nonOverlapIndex) {
                        continue;
                    }
                    if (checkWithPreFrame && null != preFrameList &&
                        !isNonOverlapWithPreFrame(preFrameList, 2, t3)) {
                        bypassByPreFrameCount++;
                        continue;
                    }
                    if (noAdjoin && isAdjoin(t2, t3)) {
                        continue;
                    }

                    for (int L4 = 0; L4 < size; L4++) {
                        if (L3 == L4 || L2 == L4 || L1 == L4) {
                            continue;
                        }
                        TwoLine t4 = twoLine.get(L4);
                        if (null == t4.nonOverlapIndex) {
                            continue;
                        }
                        if (checkWithPreFrame && null != preFrameList &&
                            !isNonOverlapWithPreFrame(preFrameList, 3, t4)) {
                            bypassByPreFrameCount++;
                            continue;
                        }

                        if (noAdjoin && isAdjoin(t3, t4)) {
                            continue;
                        }

                        totalCount++;

                        //==========================================================================
                        // clear
                        //==========================================================================
                        for (int x = 0; x < maskWidth; x++) {
                            checkVertical[x] = 0;
                        }
                        for (int h = 0; h < maskHeight; h++) {
                            for (int w = 0; w < maskWidth; w++) {
                                framePlus1[h][w] = false;
                            }
                        }
                        //==========================================================================
                        int[] LArray = {L1, L2, L3, L4};
                        byte[][] frameIndex = new byte[maskHeight][];

                        boolean skip = false;
                        for (int x = 0; x < 4; x++) {
                            int L = LArray[x];

                            TwoLine t = twoLine.get(L);
                            byte[][] line = t.position;

                            int height = line.length;
                            for (int h = 0; h < height; h++) {
                                int hpos = x * 2 + h;
                                frameIndex[hpos] = line[h];

                                int width = line[h].length;
                                for (int w = 0; w < width; w++) {
                                    int wpos = line[h][w];

                                    framePlus1[hpos][wpos] = true;
                                    checkVertical[wpos]++;

                                    if (noAdjoin && x > 0 && true == framePlus1[hpos - 1][wpos]) { //上下有重疊到就略過
                                        skip = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (skip) {
                            bypassCount++;
                            continue;
                        }

                        //==========================================================================
                        // 檢查直列的+1數量
                        //==========================================================================
                        boolean ok = true;
                        for (int x = 0; x < maskWidth / 2; x++) {
                            if (checkVertical[x * 2] != check[0] ||
                                checkVertical[x * 2 + 1] != check[1]) {
                                ok = false;
                                break;
                            }
                        }
                        if (!ok) {
                            bypassCount++;
                            continue;
                        }
                        //==========================================================================

                        //==========================================================================
                        // 檢查奇偶數的數量(極性檢查)
                        //==========================================================================
                        if (!checkOddAndEven(framePlus1)) {
                            bypassCount++;
                            continue;
                        }
                        //==========================================================================

                        //==========================================================================
                        // 檢查斜線是否超過2個
                        //==========================================================================
                        if (!checkSlash(framePlus1)) {
                            bypassCount++;
                            continue;
                        }
                        //==========================================================================

                        //==========================================================================
                        // 對角的象限+1數量要對稱
                        //==========================================================================
                        if (checkQuadrant && !checkQuadrant(framePlus1, Q1BiggerThanQ2)) {
                            bypassCount++;
                            continue;
                        }
                        //==========================================================================

                        Frame frame = new Frame(frameIndex, t1, t2, t3, t4);
                        result.add(frame);
//                        System.out.println(frame);
                    }
                }
            }
        }

        System.out.println("bypass/total: " + bypassCount + "/" + totalCount);
        if (checkWithPreFrame) {
            System.out.println("bypassByPreFrame: " + bypassByPreFrameCount);
        }
        return result;
    }

    /**
     * 用2-line的結果組合成一個frame
     * 總共四組2-line來組成.
     *
     * 組合的時候, 要檢查:
     * 1. 上下的2-line組合不能有+1相連
     * 2. 檢查直列的+1數量
     * 3. 檢查奇偶數的數量(極性檢查)
     * 4. 檢查斜線是否超過2個
     * @param twoLine ArrayList
     * @param check int[]
     * @param noAdjoin boolean
     * @param Q1BiggerThanQ2 boolean
     * @return LinkedList
     */
    private static LinkedList<byte[][]> framePicker2(ArrayList < byte[][] > twoLine, int[] check,
            boolean noAdjoin, boolean Q1BiggerThanQ2) {
        int size = twoLine.size();
        LinkedList<byte[][]> result = new LinkedList<byte[][]>();
        int maskWidth = 8;
        int maskHeight = 8;
        byte[] checkVertical = new byte[maskWidth];
        boolean[][] frame = new boolean[maskHeight][maskWidth];
        int bypassCount = 0;
        int totalCount = 0;

        for (int L1 = 0; L1 < size; L1++) {
            for (int L2 = 0; L2 < size; L2++) {
                if (L1 == L2) {
                    continue;
                }
                for (int L3 = 0; L3 < size; L3++) {
                    if (L2 == L3 || L1 == L3) {
                        continue;
                    }
                    for (int L4 = 0; L4 < size; L4++) {
                        if (L3 == L4 || L2 == L4 || L1 == L4) {
                            continue;
                        }
                        totalCount++;

                        //==========================================================================
                        // clear
                        //==========================================================================
                        for (int x = 0; x < maskWidth; x++) {
                            checkVertical[x] = 0;
                        }
                        for (int h = 0; h < maskHeight; h++) {
                            for (int w = 0; w < maskWidth; w++) {
                                frame[h][w] = false;
                            }
                        }
                        //==========================================================================
                        int[] LArray = {L1, L2, L3, L4};
                        byte[][] frameIndex = new byte[maskHeight][];

                        boolean skip = false;
                        for (int x = 0; x < 4; x++) {
                            int L = LArray[x];
                            byte[][] line = twoLine.get(L);
                            int height = line.length;

                            for (int h = 0; h < height; h++) {
                                int hpos = x * 2 + h;
                                frameIndex[hpos] = line[h];

                                int width = line[h].length;
                                for (int w = 0; w < width; w++) {
                                    int wpos = line[h][w];
                                    frame[hpos][wpos] = true;
                                    checkVertical[wpos]++;

                                    if (noAdjoin && x > 0 && true == frame[hpos - 1][wpos]) { //上下有重疊到就略過
                                        skip = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (skip) {
                            bypassCount++;
                            continue;
                        }

                        //==========================================================================
                        // 檢查直列的+1數量
                        //==========================================================================
                        boolean ok = true;
                        for (int x = 0; x < maskWidth / 2; x++) {
                            if (checkVertical[x * 2] != check[0] ||
                                checkVertical[x * 2 + 1] != check[1]) {
                                ok = false;
                                break;
                            }
                        }
                        if (!ok) {
                            bypassCount++;
                            continue;
                        }
                        //==========================================================================

                        //==========================================================================
                        // 檢查奇偶數的數量(極性檢查)
                        //==========================================================================
                        if (!checkOddAndEven(frame)) {
                            bypassCount++;
                            continue;
                        }
                        //==========================================================================

                        //==========================================================================
                        // 檢查斜線是否超過2個
                        //==========================================================================
                        if (!checkSlash(frame)) {
                            bypassCount++;
                            continue;
                        }
                        //==========================================================================

                        //==========================================================================
                        // 對角的象限+1數量要對稱
                        //==========================================================================
                        if (!checkQuadrant(frame, Q1BiggerThanQ2)) {
                            bypassCount++;
                            continue;
                        }
                        //==========================================================================

                        result.add(frameIndex);
                    }
                }
            }
        }

        System.out.println("bypass/total: " + bypassCount + "/" + totalCount);

        return result;
    }

    private static boolean checkQuadrant(boolean[][] frame, boolean Q1BiggerThanQ2) {
        int[] Q = CheckTool.getQuadrantCount(frame);
        int Q1 = Q[0], Q2 = Q[1], Q3 = Q[2], Q4 = Q[3];
        if (Q1 != Q3 || Q2 != Q4) {
            return true;
        }
        if (Q1BiggerThanQ2) {
            if (Q1 < Q2 || Q3 < Q4) {
                return true;
            }
        } else {
            if (Q1 > Q2 || Q3 > Q4) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkSlash(boolean[][] frame) {

        int maskHeight = frame.length;
        int maskWidth = frame[0].length;
        boolean slash = false;
        for (int h = 2; h < maskHeight - 2; h++) {
            for (int w = 2; w < maskWidth - 2; w++) {
                if (true == frame[h][w]) {

                    if (frame[h - 1][w - 1] && frame[h - 2][w - 2] ||
                        frame[h + 1][w + 1] && frame[h + 2][w + 2] ||
                        frame[h - 1][w + 1] && frame[h - 2][w + 2] ||
                        frame[h + 1][w - 1] && frame[h + 2][w - 2]) {
                        slash = true;

                        break;
                    }
                }
            }
        }
        return!slash;
    }

    private static boolean checkOddAndEven(boolean[][] frame) {
        int maskHeight = frame.length;
        int maskWidth = frame[0].length;

        //==========================================================================
        // 檢查奇偶數的數量(極性檢查)
        //==========================================================================
        int odd = 0, even = 0;
        for (int h = 0; h < maskHeight; h++) {
            for (int w = 0; w < maskWidth; w++) {
                if ((true == frame[h][w])) {
                    if ((h + w) % 2 == 0) {
                        even++;
                    } else {
                        odd++;
                    }
                }
            }
        }
        if (odd == 0 || even == 0 || (odd != even)) {
            return false;
        } else {
            return true;
        }
        //==========================================================================

    }


    private static LinkedList<boolean[][]> framePicker(ArrayList < byte[][] > twoLine, int[] check) {
        int size = twoLine.size();
        LinkedList<boolean[][]> result = new LinkedList<boolean[][]>();
        int maskWidth = 8;
        int maskHeight = 8;
        byte[] checkArray = new byte[maskWidth];
        boolean[][] frame = new boolean[maskHeight][maskWidth];

        for (int L1 = 0; L1 < size; L1++) {
            for (int L2 = 0; L2 < size; L2++) {
                if (L1 == L2) {
                    continue;
                }
                for (int L3 = 0; L3 < size; L3++) {
                    if (L2 == L3 || L1 == L3) {
                        continue;
                    }
                    for (int L4 = 0; L4 < size; L4++) {
                        if (L3 == L4 || L2 == L4 || L1 == L4) {
                            continue;
                        }

                        for (int x = 0; x < maskWidth; x++) {
                            checkArray[x] = 0;
                        }
                        for (int h = 0; h < maskHeight; h++) {
                            for (int w = 0; w < maskWidth; w++) {
                                frame[h][w] = false;
                            }
                        }

                        int[] LArray = {L1, L2, L3, L4};

                        boolean skip = false;
                        for (int x = 0; x < 4; x++) {
                            int L = LArray[x];
                            byte[][] line = twoLine.get(L);
                            int height = line.length;

                            for (int h = 0; h < height; h++) {
                                int hpos = x * 2 + h;
                                int width = line[h].length;
                                for (int w = 0; w < width; w++) {
                                    int wpos = line[h][w];
                                    frame[hpos][wpos] = true;
                                    checkArray[wpos]++;

                                    if (x > 0 && true == frame[hpos - 1][wpos]) { //上下有重疊到就略過
                                        skip = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (skip) {
                            continue;
                        }

                        //==========================================================================
                        // 檢查直列的+1數量
                        //==========================================================================
                        boolean ok = true;
                        for (int x = 0; x < maskWidth / 2; x++) {
                            if (checkArray[x * 2] != check[0] || checkArray[x * 2 + 1] != check[1]) {
                                ok = false;
                                break;
                            }
                        }
                        if (!ok) {
                            continue;
                        }
                        //==========================================================================

                        //==========================================================================
                        // 檢查奇偶數的數量(極性檢查)
                        //==========================================================================
                        int odd = 0, even = 0;
                        for (int h = 0; h < maskHeight; h++) {
                            for (int w = 0; w < maskWidth; w++) {
                                if ((true == frame[h][w])) {
                                    if ((h + w) % 2 == 0) {
                                        even++;
                                    } else {
                                        odd++;
                                    }
                                }
                            }
                        }
                        if (odd == 0 || even == 0) {
                            continue;
                        }
                        //==========================================================================

                        result.add(FRCUtil.copy(frame));
                    }
                }
            }
        }
        return result;
    }

    enum Type {
        FullFrame, HalfFrame;
    }


    private static int getNonOverlappingCount(LinkedList < byte[][] > frames1_21) {

        int size = frames1_21.size();
        int nonOverlap = 0;
        for (int x = 0; x < size - 1; x++) {
            byte[][] b0 = frames1_21.get(x);
            System.out.println(x + " / " + size + ": " + nonOverlap);
            for (int y = x + 1; y < size; y++) {
                byte[][] b1 = frames1_21.get(y);
                if (isOverlapping(b0, b1)) {
                    continue;
                }
                nonOverlap++;
            }
        }

        return nonOverlap;
    }



    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String dir = "FRC/FRC 3-16/";
        Type type = Type.FullFrame;
        /**
         * 因為A+B原則, 總共6個+1塞到8格子, 是不會重疊的
         * 所以frame 1and3 2and4也不應該要重疊到
         *
         * 但有因為A+B原則, 無法只造出一半的frame, 去衍生另外一半, 因為會破壞A+B原則
         *
         * 由於僅有3/16, 只佔了 64*3/16=12, 四個frame共48, 應該不會有重疊的狀況
         */

        switch (type) {
        case FullFrame: {
            int method = 2;
            String frame_filename = dir + "fullframe" + method + ".obj";
            boolean fileExist = new File(frame_filename).exists();
            LinkedList<Frame> frame1 = null;
            LinkedList<Frame> frame2 = null;
            LinkedList<Frame> frame3 = null;
            LinkedList<Frame> frame4 = null;

            if (fileExist) {
                ObjectInputStream f = new ObjectInputStream(new BufferedInputStream(new
                        FileInputStream(
                                frame_filename)));
                frame1 = (LinkedList<Frame>) f.readObject();
                frame2 = (LinkedList<Frame>) f.readObject();
                frame3 = (LinkedList<Frame>) f.readObject();
                frame4 = (LinkedList<Frame>) f.readObject();
            } else {

                /**
                 * 1. 同一個frame 的每個line混搭A+B(1+2)的方式達到3/16
                 * 2. 接連的frame 分別以8/16達到3/16
                 */


                /**
                 * 先決定好1-line的組合 , 再來組合成2-line
                 */
                //1個+1的位置
                ArrayList<byte[]> line1 = line1Picker(); //8
                //2個+1的位置
                ArrayList<byte[]> line2 = line2Picker(); //21

                switch (method) {
                case 1: {

                    /**
                     * 產生2-line 的結果, 以line1/line2的+1數量來看, 分別是1 / 2個+1, 以及2 / 1個+1
                     */
                    //是否允許上下相連
                    boolean noAdjoin = true;
                    ArrayList<TwoLine> line12 = twoLinePicker2(line1, line2, noAdjoin); //126
                    ArrayList<TwoLine> line21 = twoLinePicker2(line2, line1, noAdjoin); //126

                    /**
                     * 由2-line的結果組成1個frame
                     */
                    frame1 = framePicker3(null, false, line21, new int[] {2, 1}, noAdjoin, true, true); //
                    System.out.println("Frame1: " + frame1.size());
                    frame2 = framePicker3(frame1, false, line12, new int[] {1, 2}, noAdjoin, true, true); //
                    System.out.println("Frame2: " + frame2.size());
                    frame3 = framePicker3(frame2, false, line21, new int[] {2, 1}, noAdjoin, true, false); //
                    System.out.println("Frame3: " + frame3.size());
                    frame4 = framePicker3(frame3, false, line12, new int[] {1, 2}, noAdjoin, true, false); //
                    System.out.println("Frame4: " + frame4.size());
                }
                break;
                case 2: {
                    //是否允許上下相連
                    boolean noAdjoin = true;
                    ArrayList<TwoLine> line11 = twoLinePicker2(line1, line1, noAdjoin); //55
                    ArrayList<TwoLine> line22 = twoLinePicker2(line2, line2, noAdjoin); //239
                    System.out.println("line11 size: " + line11.size());
                    System.out.println("line22 size: " + line22.size());
                    /**
                     * 由2-line的結果組成1個frame
                     */
//                    frame1 = framePicker3(null, false, line11, new int[] {1, 1}, noAdjoin, false, true); //17854
                    frame1 = framePicker3(null, false, line11, new int[] {1, 1}, noAdjoin, true, true); //4008
                    System.out.println("Frame1: " + frame1.size());
//                    frame2 = framePicker3(frame1, false, line22, new int[] {2, 2}, noAdjoin, false, true); //126178
                    frame2 = framePicker3(frame1, false, line22, new int[] {2, 2}, noAdjoin, true, true); //18960
                    System.out.println("Frame2: " + frame2.size());
                    frame3 = frame1;
                    frame4 = frame2;
//                    frames3_21 = framePicker3(frames2_12, false, line21, new int[] {2, 1}, noAdjoin, false); //42015
//                    System.out.println("Frame3: " + frames3_21.size());
//                    frames4_12 = framePicker3(frames3_21, false, line12, new int[] {1, 2}, noAdjoin, false); //42015
//                    System.out.println("Frame4: " + frames4_12.size());
                }
                break;
                }

                ObjectOutputStream f = new ObjectOutputStream(new BufferedOutputStream(new
                        FileOutputStream(frame_filename)));
                f.writeObject(frame1);
                f.writeObject(frame2);
                f.writeObject(frame3);
                f.writeObject(frame4);
                f.flush();
                f.close();

                System.out.println(frame_filename + " ok!");

            }
            boolean startFrom0 = false;
            //至此, 找到的frame都符合規範, 接下來要把四個 frame兜起來
            if (startFrom0) {
//            frcPicker2(frames1_21, frames2_12);

//                frcPicker2_MultiThread(frames1_21, frames2_12, 0, 1, false, 0, 0);
                frcPicker2_MultiThread(frame1, frame2, frame3, frame4, method, dir);
            } else {
                //1: 2957
                //2: 881
                int startFrom = 1094;
//                frcPicker2_MultiThread(frame1, frame2, frame3, frame4, startFrom, 1, false,
//                                       0, method, dir);
                frcPicker3(frame1, frame2, frame3, frame4, startFrom, 1, false, 0, method, dir, true, true, false);
//                frcPicker4(frame1, frame2, frame3, frame4, startFrom, 1, false, 0, method, dir, false);
//
            }
        }

        break;

        }

        System.out.println("done.");

    }

    /**
     *
     * @param frame13 LinkedList
     * @param frame24 LinkedList
     * @return LinkedList
     * @deprecated
     */
    private static LinkedList<FRCPattern> frcPicker(LinkedList < boolean[][] > frame13,
            LinkedList < boolean[][] > frame24) {
        int size13 = frame13.size();
        int size24 = frame24.size();
        long count = 0;
        LinkedList<FRCPattern> result = new LinkedList<FRCPattern>();

        for (int f1 = 0; f1 < size13; f1++) {
            boolean[][] frame1 = frame13.get(f1);
            for (int f2 = 0; f2 < size24; f2++) {
                boolean[][] frame2 = frame24.get(f2);
                if (isOverlapping(frame1, frame2)) {
                    continue;
                }

                for (int f3 = 0; f3 < size13 && f1 != f3; f3++) {
                    boolean[][] frame3 = frame13.get(f3);
                    if (isOverlapping(frame1, frame3) || isOverlapping(frame2, frame3)) {
                        continue;
                    }

                    for (int f4 = 0; f4 < size24 && f2 != f4; f4++) {
                        boolean[][] frame4 = frame24.get(f4);

                        if (isOverlapping(frame1, frame4) || isOverlapping(frame2, frame4) ||
                            isOverlapping(frame3, frame4)) {
                            continue;
                        }

                        count++;
                    }

                }
            }

        }
        System.out.println(count);
        return result;
    }

    /**
     *
     * @param frame13 LinkedList
     * @param frame24 LinkedList
     * @return LinkedList
     * @throws IOException
     * @deprecated
     */
    private static LinkedList<FRCPattern> frcPicker2(LinkedList < byte[][] > frame13,
            LinkedList < byte[][] > frame24) throws
            IOException {
        int size13 = frame13.size();
        int size24 = frame24.size();
        long count = 0;
        LinkedList<FRCPattern> result = new LinkedList<FRCPattern>();

        String frame_filename = "result.obj";
        ObjectOutputStream f = new ObjectOutputStream(new BufferedOutputStream(new
                FileOutputStream(frame_filename)));

        for (int f1 = 0; f1 < size13; f1++) {
            System.out.println("f1: " + f1);
            byte[][] frame1 = frame13.get(f1);
            boolean[][] frame1_ = lineToFrame(frame1);
//            System.out.println(lineToString(frame1));

            for (int f2 = 0; f2 < size24; f2++) {
                byte[][] frame2 = frame24.get(f2);

                if (isOverlapping(frame1, frame2)) {
                    continue;
                }
                boolean[][] frame2_ = lineToFrame(frame2);
//                System.out.println(lineToString(frame2));
                for (int f3 = 0; f3 < size13 && f1 != f3; f3++) {
                    byte[][] frame3 = frame13.get(f3);

                    if (isOverlapping(frame1, frame3) || isOverlapping(frame2, frame3)) {
                        continue;
                    }
                    boolean[][] frame3_ = lineToFrame(frame3);

                    for (int f4 = 0; f4 < size24 && f2 != f4; f4++) {
                        byte[][] frame4 = frame24.get(f4);

                        if (isOverlapping(frame1, frame4) || isOverlapping(frame2, frame4) ||
                            isOverlapping(frame3, frame4)) {
                            continue;
                        }

                        boolean[][] frame4_ = lineToFrame(frame4);
                        boolean[][][][] frcArray = { {frame1_, frame2_, frame3_, frame4_}
                        };
                        FRCPattern frc = new FRCPattern(frcArray, false);
                        System.out.println(frc);
                        f.writeObject(frcArray);
                        count++;
                    }

                }
            }

        }
        f.flush();
        f.close();
        System.out.println(count);
        return result;
    }

    private static long frcPicker2Count = 0;
    private static Object lock = new Object();

    private static LinkedList<FRCPattern> frcPicker2_MultiThread(final LinkedList<Frame> frame1,
            final LinkedList<Frame> frame2, final LinkedList<Frame> frame3,
            final LinkedList<Frame> frame4, int method, String dir) throws
            IOException {
        return frcPicker2_MultiThread(frame1, frame2, frame3, frame4, 0, 1, false, 0, method, dir
                );
    }

    private static Runnable getRunnable(final int f1, final int f2, final Frame frame1,
                                        final Frame frame2, final LinkedList<Frame> frame03,
                                        final LinkedList<Frame> frame04,
                                        final boolean checkingOverlapping,
                                        final ObjectOutputStream obj, final Writer log) {
        final boolean[][] frame1_ = lineToFrame(frame1.frameIndex);
        Runnable r = new Runnable() {
            public void run() {
                int size3 = frame03.size();
                int size4 = frame04.size();
                boolean[][] frame2_ = lineToFrame(frame2.frameIndex);
                for (int f3 = 0; f3 < size3 && f1 != f3; f3++) {
//                    System.out.println(f1 + " " + f2 + " " + f3);
                    final Frame frame3 = frame03.get(f3);

                    if (checkingOverlapping &&
                        (isOverlapping(frame1, frame3) || isOverlapping(frame2, frame3))) {
                        continue;
                    }
                    boolean[][] frame3_ = lineToFrame(frame3.frameIndex);
                    for (int f4 = 0; f4 < size4 && f2 != f4; f4++) {
                        Frame frame4 = frame04.get(f4);

                        if (checkingOverlapping &&
                            (isOverlapping(frame1, frame4) || isOverlapping(frame2, frame4) ||
                             isOverlapping(frame3, frame4))) {
                            continue;
                        }

                        boolean[][] frame4_ = lineToFrame(frame4.frameIndex);
                        boolean[][][][] frcArray = { {frame1_, frame2_, frame3_, frame4_}
                        };

                        FRCPattern frc = new FRCPattern(frcArray);
                        boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc);
                        if (overallAnalyze) {
                            String text = f1 + " " + f2 + " " + f3 + " " + f4 + "\n" +
                                          frc.toString();
                            System.out.println(text);

                            try {
                                log.write(text);
                                log.flush();
                                obj.writeObject(frcArray);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        frc = null;
                        frcArray = null;
                        frame4_ = null;

                        synchronized (lock) {
                            frcPicker2Count++;
                        }
                    }
                }
                frame2_ = null;
            }
        };

        return r;
    }


    private static LinkedList<FRCPattern> frcPicker2_MultiThread(final LinkedList<Frame> frame01,
            final LinkedList<Frame> frame02, final LinkedList<Frame> frame03,
            final LinkedList<Frame> frame04, int f1_start, int f1_step, boolean oneF1Only,
            int f2_start, int method, String dir) throws
            IOException {
        final int size1 = frame01.size();
        final int size2 = frame02.size();
        frcPicker2Count = 0;
        LinkedList<FRCPattern> result = new LinkedList<FRCPattern>();

        Calendar c = Calendar.getInstance();
        String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE);
        String frame_filename = dir + time + "-frc3-16" + method + ".obj";
        final ObjectOutputStream obj = new ObjectOutputStream(new BufferedOutputStream(new
                FileOutputStream(frame_filename)));
        final Writer log = new BufferedWriter(new FileWriter(dir + "frc" + method + ".log"));

        Executor executor = Executors.newFixedThreadPool(4);

        int f1_end = oneF1Only ? f1_start + 1 : size1;
        long start = 0, end = 0;
        final boolean checkingOverlapping = true;

        for (int f1 = f1_start; f1 < f1_end; f1 += f1_step) {
            if (start != 0) {
                long cost = (end - start);
                long totalcost = (f1_end - f1) * cost / 1000;
                if (totalcost > 86400) {
                    System.out.println("Remain " + (totalcost / 86400.) + "day (" +
                                       cost / 1000 + "s)");
                } else if (totalcost <= 86400 && totalcost >= 3600) {
                    System.out.println("Remain " + (totalcost / 3600.) + "hour (" +
                                       cost / 1000 + "s)");
                } else {
                    System.out.println("Remain " + totalcost + "s (" + cost / 1000 + "s)");
                }
            }
            System.gc();
            start = System.currentTimeMillis();
            System.out.println("f1: " + f1);
            final Frame frame1 = frame01.get(f1);

            for (int f2 = f2_start; f2 < size2; f2++) {
                final Frame frame2 = frame02.get(f2);

                if (checkingOverlapping && isOverlapping(frame1, frame2)) {
                    continue;
                }

                System.gc();
                Runnable r = getRunnable(f1, f2, frame1, frame2, frame03, frame04,
                                         checkingOverlapping, obj, log);
                executor.execute(r);
            }
            end = System.currentTimeMillis();
        }
        obj.flush();
        obj.close();
        System.out.println(frcPicker2Count);
        return result;
    }

    static LinkedList<FRCPattern> frcPicker3(LinkedList<Frame> frameList[], int f1_start,
            int method, String dir, boolean runWithThread,
            boolean checkingOverlapping, boolean skipSameFrame) throws IOException {
        return frcPicker3(frameList[0], frameList[1], frameList[2], frameList[3], f1_start, 1, false,
                          0, method, dir, runWithThread, checkingOverlapping, skipSameFrame);
    }


    private static LinkedList<FRCPattern> frcPicker3(final LinkedList<Frame> frame01,
            final LinkedList<Frame> frame02, final LinkedList<Frame> frame03,
            final LinkedList<Frame> frame04, int f1_start, int f1_step, boolean oneF1Only,
            int f2_start, int method, String dir, boolean runWithThread,
            boolean checkingOverlapping,
            boolean skipSameFrame) throws
            IOException {
        final int size1 = frame01.size();
        final int size2 = frame02.size();
        frcPicker2Count = 0;
        final LinkedList<FRCPattern> result = new LinkedList<FRCPattern>();

        Calendar c = Calendar.getInstance();
        String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE);
        String frame_filename = dir + time + "-frc3-16" + method + ".obj";

        final Writer log = new BufferedWriter(new FileWriter(dir + "frc" + method + ".log"));
        final ObjectOutputStream obj = new ObjectOutputStream(new BufferedOutputStream(new
                FileOutputStream(frame_filename)));

        //==========================================================================================
        // Thread Setting
        //==========================================================================================
        //        Executor executor = Executors.newFixedThreadPool(2 * 2);//11.328
        Executor executor = Executors.newSingleThreadExecutor(); //30.656
        ThreadPoolExecutor threadPool = null;
        if (executor instanceof ThreadPoolExecutor) {
            threadPool = ((ThreadPoolExecutor) executor);
        }
        //==========================================================================================

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

//                if (!runWithRMI) {
                if (runWithThread) {
                    Runnable r = getRunnable(f1, f2, frame1, frame2, frame03, frame04,
                                             checkingOverlapping, obj, log);
                    executor.execute(r);
                } else {

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
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, true, true); //0
                            boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, false, true, true); //31104
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, true, false); //0
//                                  boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, false, true); //0
//                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, true, false, false); //0
//                                 boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(frc, false, false, false); //f6656

                            if (overallAnalyze) {
                                result.add(frc);
                                frcPicker2Count++;
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

//                }
            }
            end = System.currentTimeMillis();
        }

        obj.flush();
        obj.close();
        log.flush();
        log.close();
        System.out.println("FRC Pick: " + frcPicker2Count);
        return result;
    }

    private static LinkedList<FRCPattern> frcPicker4(final LinkedList<Frame> frame01,
            final LinkedList<Frame> frame02, final LinkedList<Frame> frame03,
            final LinkedList<Frame> frame04, int f1_start, int f1_step, boolean oneF1Only,
            final int f2_start, int method, String dir, final boolean runWithRMI) throws
            IOException {
        final int size1 = frame01.size();
        final int size2 = frame02.size();
        frcPicker2Count = 0;
        final LinkedList<FRCPattern> result = new LinkedList<FRCPattern>();

        Calendar c = Calendar.getInstance();
        String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE);
        String frame_filename = dir + time + "-frc3-16" + method + ".obj";

        final Writer log = new BufferedWriter(new FileWriter(dir + "frc" + method + ".log"));
        final ObjectOutputStream obj = new ObjectOutputStream(new BufferedOutputStream(new
                FileOutputStream(frame_filename)));
        Executor executor = Executors.newCachedThreadPool(); //36.766,33.844

        ThreadPoolExecutor threadPool = null;
        if (executor instanceof ThreadPoolExecutor) {
            threadPool = ((ThreadPoolExecutor) executor);
            threadPool.setMaximumPoolSize(3);
            threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        }

        final int f1_end = oneF1Only ? f1_start + 1 : size1;
//        long start = 0, end = 0;
        final boolean checkingOverlapping = true;
        boolean runWithThread = false;
        for (int f1 = f1_start; f1 < f1_end; f1 += f1_step) {
            System.gc();
            System.out.println("f1: " + f1);
            final Frame frame1 = frame01.get(f1);
            final int f1_ = f1;

            if (runWithThread) {
                Runnable r = new Runnable() {
                    public void run() {
                        System.out.println("start");
                        long start = System.currentTimeMillis();
                        for (int f2 = f2_start; f2 < size2; f2++) {

                            final Frame frame2 = frame02.get(f2);

                            if (checkingOverlapping && isOverlapping(frame1, frame2)) {
                                continue;
                            }

                            System.gc();

                            if (!runWithRMI) {

                                final int f2_ = f2;
                                final boolean[][] frame1_ = lineToFrame(frame1.frameIndex);
                                int size3 = frame03.size();
                                int size4 = frame04.size();
                                boolean[][] frame2_ = lineToFrame(frame2.frameIndex);
                                for (int f3 = 0; f3 < size3 && f1_ != f3; f3++) {
                                    final Frame frame3 = frame03.get(f3);
                                    if (checkingOverlapping &&
                                        (isOverlapping(frame1, frame3) ||
                                         isOverlapping(frame2, frame3))) {
                                        continue;
                                    }
                                    boolean[][] frame3_ = lineToFrame(frame3.frameIndex);

                                    for (int f4 = 0; f4 < size4 && f2_ != f4; f4++) {
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
                                        boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(
                                                frc);
                                        if (overallAnalyze) {
                                            String text = f1_ + " " + f2_ + " " + f3 + " " + f4 +
                                                    "\n" +
                                                    frc.toString();
                                            System.out.println(text);

                                            try {
                                                log.write(text);
                                                log.flush();
                                                obj.writeObject(frcArray);
                                            } catch (IOException ex) {
                                            }
                                        }
                                        frc = null;
                                        frcArray = null;
                                        frame4_ = null;

                                        synchronized (lock) {
                                            frcPicker2Count++;
                                        }
                                    }
                                }
                                frame2_ = null;
                            } else {

                            }

                        }
                        long cost = System.currentTimeMillis() - start;
                        long totalcost = (f1_end - f1_) * cost / 1000;
                        System.out.println(getCostInfo(cost, totalcost));
                        System.out.println("done");
                    }
                };
                executor.execute(r);
                if (null != threadPool) {
                    System.out.println("ActiveCount: " + threadPool.getActiveCount());
                }
            } else {
                System.out.println("start");
                long start = System.currentTimeMillis();
                for (int f2 = f2_start; f2 < size2; f2++) {
                    System.out.println("f2: " + f2 + " " + size2);
                    final Frame frame2 = frame02.get(f2);
                    if (checkingOverlapping && isOverlapping(frame1, frame2)) {
                        continue;
                    }

                    if (!runWithRMI) {
                        final int f2_ = f2;
                        final boolean[][] frame1_ = lineToFrame(frame1.frameIndex);
                        int size3 = frame03.size();
                        int size4 = frame04.size();
                        boolean[][] frame2_ = lineToFrame(frame2.frameIndex);
                        for (int f3 = 0; f3 < size3 && f1_ != f3; f3++) {
                            final Frame frame3 = frame03.get(f3);
                            if (checkingOverlapping &&
                                (isOverlapping(frame1, frame3) ||
                                 isOverlapping(frame2, frame3))) {
                                continue;
                            }
                            boolean[][] frame3_ = lineToFrame(frame3.frameIndex);

                            for (int f4 = 0; f4 < size4 && f2_ != f4; f4++) {
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
                                boolean overallAnalyze = ArtifactsAnalyzer.overallAnalyze(
                                        frc);
                                if (overallAnalyze) {
                                    String text = f1_ + " " + f2_ + " " + f3 + " " + f4 +
                                                  "\n" +
                                                  frc.toString();
                                    System.out.println(text);

                                    try {
                                        log.write(text);
                                        log.flush();
                                        obj.writeObject(frcArray);
                                    } catch (IOException ex) {
                                    }
                                }
                                frc = null;
                                frcArray = null;
                                frame4_ = null;

                                synchronized (lock) {
                                    frcPicker2Count++;
                                }
                            }
                        }
                        frame2_ = null;
                    }
                }
                long cost = System.currentTimeMillis() - start;
                long totalcost = (f1_end - f1_) * cost / 1000;
                System.out.println(getCostInfo(cost, totalcost));
                System.out.println("done");
            }
        }
        if (runWithRMI) {
            for (FRCPattern frc : result) {
                obj.writeObject(frc);
            }
        }
        obj.flush();
        obj.close();
        System.out.println(frcPicker2Count);
        return result;
    }


}


class FRCThreadFactory implements ThreadFactory {
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }
}
