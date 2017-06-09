package auo.cms.frc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import auo.mura.util.ArrayUtils;
import shu.io.ascii.ASCIIFileFormat;
import shu.io.ascii.ASCIIFileFormatParser;
import shu.io.files.CSVFile;
import shu.math.Maths;
import auo.cms.frc.impl.*;

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
public class FRCPattern implements Serializable {

    private static final long serialVersionUID = -7987193038121787820L;
    //[level][frame][h][w]
    public boolean[][][][] pattern;
    public byte[][] artifacts;
    public byte[][] balancedSum; //���t�����P�j��
    public double[][] sum; //���t�������P�j��
    public byte[][][] polarityPattern;
    public byte[][] plusOneSum;
    public int[][] greenPixel;
    public int[] twovcount;
    public int[] twohcount;
    public int[] Lcount;
    public int maxSlash;
    public int maxHLine;
    public int[][] twovGcount;

    public boolean[][][] getFRCPattern(int level) {
        return pattern[level];
    }

    public int getFrameCount() {
        return pattern[0].length;
    }

    public String getPolarityString(int h, int w) {
        if (null == polarityPattern) {
            return null;
        }
        int frame = polarityPattern.length;
//        int height = polarityPattern[0].length;
//        int width=polarityPattern[0][0].length;
        StringBuilder buf1 = new StringBuilder();
//        StringBuilder buf2 = new StringBuilder();
        for (int f = 0; f < frame; f++) {
            if (polarityPattern[f][h][w] == 1) {
//                buf1.append('��');
//                buf1.append("�z�{");
//                buf2.append("�}�|");
            } else if (polarityPattern[f][h][w] == -1) {
                buf1.append('_');
//                buf1.append("�{�z");
//                buf2.append("�|�}");
            } else if (polarityPattern[f][h][w] == 0) {
                buf1.append('-');
//                buf1.append("    ");
//                buf2.append("�v�v");

            }
        }
//        buf1.append('\n');
//        buf1.append(buf2);
        return buf1.toString();
    }

    public FRCPattern(boolean[][][][] pattern) {
        this(pattern, null, false);
    }

    public FRCPattern(boolean[][][][] pattern, boolean mapping) {
        this(pattern, null, mapping);
    }

    private static boolean[][][][] mapping(boolean[][][][] input) {
        int size = input.length;
        if (size == 4) {
            boolean[][][] FRC_5_8 = ArrayUtils.inverse(input[2]);
            boolean[][][] FRC_6_8 = ArrayUtils.inverse(input[1]);
            boolean[][][] FRC_7_8 = ArrayUtils.inverse(input[0]);

            return new boolean[][][][]{
                input[0], input[1], input[2], input[3], FRC_5_8, FRC_6_8,
                FRC_7_8};
        } else if (size == 8) {

        }
        return null;
    }

    public FRCPattern(boolean[][][][] pattern, byte[][] artifacts) {
        this(pattern, artifacts, false);
    }

    private FRCPattern(boolean[][][][] pattern, byte[][] artifacts, boolean mapping) {
        if (mapping) {
            this.pattern = mapping(pattern);
        } else {
            this.pattern = pattern;
        }
        this.artifacts = artifacts;
    }

    public FRCPattern(String filename, int index) throws IOException {

        parseFile(filename, index);

        this.sourceInfo = new File(filename).getName() + "_" + index;
    }

    public FRCPattern(String filename) throws FileNotFoundException, IOException {
        if (isAUOFile(filename)) {
            parseAUOFile(filename);
        } else {
            parseFile(filename);
        }

        this.sourceInfo = new File(filename).getName();
    }

    static boolean[][][] parseAUOFRC(int hoffset, CSVFile csv) {
        boolean[][][] result = new boolean[4][8][8];
        for (int count = 0; count < 4; count++) {
            for (int h = 0; h < 8; h++) {
                for (int w = 0; w < 8; w++) {
                    int y = hoffset + h;
                    int x = count * 9 + w;
                    boolean b = (csv.getCell(y, x) == 1) ? true : false;
                    result[count][h][w] = b;
                }
            }
        }
        return result;
    }

    private boolean isAUOFile(String filename) throws FileNotFoundException, IOException {
        CSVFile csv = new CSVFile(filename, (char) 9);
        boolean result = csv.getCellAsString(0, 0).equalsIgnoreCase("F1");
        csv = null;
        return result;
    }

    private void parseAUOFile(String filename) throws
            FileNotFoundException,
            IOException {
        CSVFile csv = new CSVFile(filename, (char) 9);
        int rows = csv.getRows();
        boolean isFRC16 = rows > 51;

        if (!isFRC16) {
            boolean[][][] FRC_1_8 = parseAUOFRC(2, csv);
            boolean[][][] FRC_2_8 = parseAUOFRC(15, csv);
            boolean[][][] FRC_3_8 = parseAUOFRC(28, csv);
            boolean[][][] FRC_4_8 = parseAUOFRC(42, csv);
            pattern = mapping(new boolean[][][][]{FRC_1_8, FRC_2_8, FRC_3_8,
                FRC_4_8});
        } else {
            boolean[][][] FRC_1_16 = parseAUOFRC(2, csv);
            boolean[][][] FRC_2_16 = parseAUOFRC(16, csv);
            boolean[][][] FRC_3_16 = parseAUOFRC(30, csv);
            boolean[][][] FRC_4_16 = parseAUOFRC(44, csv);
            boolean[][][] FRC_5_16 = parseAUOFRC(57, csv);
            boolean[][][] FRC_6_16 = parseAUOFRC(70, csv);
            boolean[][][] FRC_7_16 = parseAUOFRC(83, csv);
            boolean[][][] FRC_8_16 = parseAUOFRC(97, csv);
            boolean[][][] FRC_9_16 = ArrayUtils.inverse(FRC_7_16);
            boolean[][][] FRC_10_16 = ArrayUtils.inverse(FRC_6_16);
            boolean[][][] FRC_11_16 = ArrayUtils.inverse(FRC_5_16);
            boolean[][][] FRC_12_16 = ArrayUtils.inverse(FRC_4_16);
            boolean[][][] FRC_13_16 = ArrayUtils.inverse(FRC_3_16);
            boolean[][][] FRC_14_16 = ArrayUtils.inverse(FRC_2_16);
            boolean[][][] FRC_15_16 = ArrayUtils.inverse(FRC_1_16);
            pattern = new boolean[][][][]{
                FRC_1_16, FRC_2_16, FRC_3_16, FRC_4_16, FRC_5_16, FRC_6_16,
                FRC_7_16, FRC_8_16, FRC_9_16, FRC_10_16, FRC_11_16, FRC_12_16,
                FRC_13_16, FRC_14_16, FRC_15_16};
        }
    }

    public String sourceInfo;

    public String getSourceInfo() {
        return sourceInfo;
    }

    public void caculateInfo() {
        FRCPattern p = this;
        p.twohcount = CheckTool.
                getContinueHorizontalCount(p, 2);
        p.twovcount = CheckTool.
                getContinueVerticalCount(p, 2);
        p.greenPixel = CheckTool.
                getGreenPixelCount(p);
        p.Lcount = CheckTool.checkLCount(p);
        p.maxSlash = CheckTool.getMaxSlash(p);
        p.maxHLine = CheckTool.getMaxContinuePolarityHLine(p);
        p.twovGcount = CheckTool.getTwovGcount(p);
    }

    private void parseFile(String filename) throws IOException {
        ASCIIFileFormatParser parser = new ASCIIFileFormatParser(filename);
        ASCIIFileFormat format = parser.parse();
        int size = format.size();
        int onelevelH = 9 * 4;
        int frccount = size / onelevelH;
        int height = 8;
        pattern = new boolean[frccount][4][height][height];

        for (int l = 0; l < frccount; l++) {
            for (int f = 0; f < 4; f++) {
                for (int h = 0; h < height; h++) {
                    int linenum = onelevelH * l + 9 * f + h;
                    ASCIIFileFormat.LineObject line = format.getLine(linenum);
                    String s = line.stringArray[0];
//                        System.out.println(s);

                    for (int w = 0; w < height; w++) {
                        boolean t = s.charAt(w) == '1' ? true : false;
                        pattern[l][f][h][w] = t;
                    }
                }
            }
        }

    }

    private void parseFile(String filename, int index) throws IOException {
        ASCIIFileFormatParser parser = new ASCIIFileFormatParser(filename);
        ASCIIFileFormat format = parser.parse();
        int height = 8;
        pattern = new boolean[1][4][height][height];

        for (int x = 0; x < height; x++) {
            int h = x + 1 + 10 * index;
            ASCIIFileFormat.LineObject line = format.getLine(h);
            int strcount = line.stringArray.length;
            if (12 == strcount) {
                String s0 = line.stringArray[0];
                String s1 = line.stringArray[1];
                String s2 = line.stringArray[2];
                String s3 = line.stringArray[3];
                for (int w = 0; w < height; w++) {
                    pattern[0][0][x][w] = s0.charAt(w) == '1' ? true : false;
                    pattern[0][1][x][w] = s1.charAt(w) == '1' ? true : false;
                    pattern[0][2][x][w] = s2.charAt(w) == '1' ? true : false;
                    pattern[0][3][x][w] = s3.charAt(w) == '1' ? true : false;
                }
            } else {
                for (int w = 0; w < height; w++) {
                    pattern[0][0][x][w] = line.stringArray[w].equals("1") ? true : false;
                    pattern[0][1][x][w] = line.stringArray[w
                            + 9].equals("1") ? true : false;
                    pattern[0][2][x][w] = line.stringArray[w
                            + 18].equals("1") ? true : false;
                    pattern[0][3][x][w] = line.stringArray[w
                            + 27].equals("1") ? true : false;
                }

            }
        }

    }

    public String getArtifactsString() {
        return artifactsToString(artifacts, false);
    }
    
        public String getArtifactsString(boolean lineFeed) {
        return artifactsToString(artifacts, lineFeed);
    }

    public static String artifactsToString(byte[][] artifacts, boolean lineFeed) {
        StringBuilder b = new StringBuilder();
        int height = artifacts.length;
        int width = artifacts[0].length;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (artifacts[h][w] > 0) {
                    b.append('+');
                } else if (artifacts[h][w] < 0) {
                    b.append('-');
                } else {
                    b.append('0');
                }
//                b.append((artifacts[h][w] >= 0) ? artifacts[h][w] : "-");
                b.append(' ');
            }
            if (lineFeed) {
                b.append('\n');
            }
        }
        return b.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj argument;
     * <code>false</code> otherwise.
     * @todo Implement this java.lang.Object method
     */
    public boolean equals(Object obj) {
        return equalsPattern((FRCPattern) obj);
//        byte[][] thisA = this.artifacts;
//        byte[][] thatA = ((FRCPattern) obj).artifacts;
//        if (null == thatA) {
//            throw new IllegalArgumentException("");
//        }
//        int height = thisA.length;
//        int width = thisA[0].length;
//        if (height != thatA.length || width != thatA[0].length) {
//            return false;
//        }
//        boolean eq = true;
//        for (int h = 0; h < height; h++) {
//            eq = eq && Arrays.equals(thisA[h], thatA[h]);
//        }
//        return eq;
    }

    public boolean equalsPattern(FRCPattern frc) {
        boolean[][][][] thisp = this.pattern;
        boolean[][][][] thatp = frc.pattern;
        //[level][frame][h][w]
        int level = thisp.length;
        int frame = thisp[0].length;
        int height = thisp[0][0].length;
//        int width = thisp[0][0][0].length;
//        boolean eq = true;
        for (int l = 0; l < level; l++) {
            for (int f = 0; f < frame; f++) {
                for (int h = 0; h < height; h++) {
//                    eq = eq && Arrays.equals(thisp[l][f][h], thatp[l][f][h]);
                    if (!Arrays.equals(thisp[l][f][h], thatp[l][f][h])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean equalsArtifacts(FRCPattern frc) {
        byte[][] thisA = this.artifacts;
        byte[][] thatA = frc.artifacts;
        if (null == thatA) {
            throw new IllegalArgumentException("");
        }
        int height = thisA.length;
        int width = thisA[0].length;
        if (height != thatA.length || width != thatA[0].length) {
            return false;
        }
        boolean eq = true;
        for (int h = 0; h < height; h++) {
            eq = eq && Arrays.equals(thisA[h], thatA[h]);
        }
        return eq;
    }

    public boolean equalsBalancedSum(FRCPattern frc) {
        byte[][] thisA = this.balancedSum;
        byte[][] thatA = frc.balancedSum;
        if (null == thatA) {
            throw new IllegalArgumentException("");
        }
        int height = thisA.length;
        int width = thisA[0].length;
        if (height != thatA.length || width != thatA[0].length) {
            return false;
        }
        boolean eq = true;
        for (int h = 0; h < height; h++) {
            eq = eq && Arrays.equals(thisA[h], thatA[h]);
        }
        return eq;
    }

//        public boolean equalsArtifacts(Object obj) {
//
//        }
    public static String toString(boolean[][] pattern) {
        int height = pattern.length;
        int width = pattern[0].length;
        StringBuilder b = new StringBuilder();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                b.append(pattern[h][w] ? '1' : '0');
//                b.append(pattern[h][w] ? '1' : ' ');
                b.append(' ');
            }
            b.append(' ');

            b.append('\n');
        }
        return b.toString();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        //[level][frame][h][w]
        int level = pattern.length;
        int frame = pattern[0].length;
        int height = pattern[0][0].length;
        int width = pattern[0][0][0].length;
        StringBuilder b = new StringBuilder();

        //==========================================================================================
        // header
        //==========================================================================================
        b.append(" FRC Pattern                                                          | ");
        if (null != artifacts) {
            b.append(" Artifacts      ");
        }
        if (null != balancedSum) {
            b.append("| Balanced Sum    ");
        }
        if (null != plusOneSum) {
            b.append("| +1 Sum          ");
        }

        if (null != greenPixel) {
            b.append("|G Pixel|");
        }
        if (null != twohcount) {
            b.append("2H  |");
        }
        if (null != twovcount) {
            b.append("2V  |");
        }
        if (null != Lcount) {
            b.append("L");
        }

        b.append('\n');
        //==========================================================================================

        for (int l = 0; l < level; l++) {
            if (level > 1) {
                b.append("Level: " + (l + 1));
                b.append('\n');
            }
            for (int h = 0; h < height; h++) {
                for (int f = 0; f < frame; f++) {
                    for (int w = 0; w < width; w++) {
                        b.append(pattern[l][f][h][w] ? '1' : '0');
                        b.append(' ');
                    }
                    b.append("| ");
                }
                if (null != artifacts) {
                    for (int w = 0; w < width; w++) {
                        b.append(artifacts[h][w] > 0 ? '+'
                                : artifacts[h][w] == 0 ? '0' : '-');
                        b.append(' ');
                    }

                }
                if (null != balancedSum) {
                    b.append("| ");
                    for (int w = 0; w < width; w++) {
                        b.append(balancedSum[h][w] > 0 ? '+'
                                : balancedSum[h][w] == 0 ? '0' : '-');
                        b.append(' ');
                    }
                }
                if (null != plusOneSum) {
                    b.append("| ");
                    for (int w = 0; w < width; w++) {
                        b.append(plusOneSum[h][w]);
                        b.append(' ');
                    }
                }

                if (null != greenPixel) {
                    b.append("| ");
                    int gwidth = greenPixel[0].length;
                    for (int w = 0; w < gwidth; w++) {
                        b.append(greenPixel[h][w]);
                        b.append(' ');
                    }
                }
                if (null != this.twohcount) {
                    if (h < twohcount.length) {
                        b.append("|h ");
                        b.append(twohcount[h]);
                        b.append(' ');
                    } else if (h == twohcount.length) {
                        b.append("| " + Maths.sum(twohcount) + " ");
                    } else {
                        b.append("|    ");
                    }

                }
                if (null != this.twovcount) {
                    if (h < twovcount.length) {
                        b.append("|v ");
                        b.append(twovcount[h]);
                        b.append(' ');
                    } else if (h == twovcount.length) {
                        b.append("| " + Maths.sum(twovcount) + " ");
                    } else {
                        b.append("|    ");
                    }

                }

                if (null != this.Lcount) {
                    if (h < Lcount.length) {
                        b.append("|L ");
                        b.append(Lcount[h]);
                        b.append(' ');
                    } else {
                        b.append("|    ");
                    }

                }

                if (height != 1) {
                    b.append('\n');
                }
            }
            if (this.maxHLine != 0) {
                b.append("maxHLine: " + maxHLine + " ");
            }
            if (this.maxSlash != 0) {
                b.append("maxSlash: " + maxSlash + " ");
            }
            if (this.maxHLine != 0 || this.maxSlash != 0) {
                b.append('\n');
            }
        }
        return b.toString();
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not support
     * the <code>Cloneable</code> interface. Subclasses that override the
     * <code>clone</code> method can also throw this exception to indicate that
     * an instance cannot be cloned.
     */
    protected Object clone() throws CloneNotSupportedException {
        boolean[][][][] newpattern = FRCUtil.copy(pattern);
        byte[][] newartifacts = FRCUtil.copy(artifacts);

        return new FRCPattern(newpattern, newartifacts);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String filename = "D:/My Documents/�u�@/Project/FRC/FRC Research/NewFRC/AllNewFRC2.txt";

//         AUOFRC frc = new AUOFRC(filename, AUOFRC.PatternCount.FRC16);
        FRCPattern frc = new FRCPattern(filename);
        System.out.println(frc);
    }
}
