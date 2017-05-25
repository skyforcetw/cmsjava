package auo.mura.table;

import java.io.*;
import java.io.File;
import java.util.*;

import au.com.bytecode.opencsv.*;
import auo.mura.*;
import jxl.read.biff.*;
import shu.cms.colorspace.depend.*;
import shu.math.lut.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 * @deprecated
 */
public class DeMuraTableProducer {

    static double[][] interpolate(double[][] data, int originalBlockWidth,
                                  int originalBlockHeight, int targetBlockWidth,
                                  int targetBlockHeight, int screenWidth,
                                  int screenHeight) {
//    data.l
        int w = data[0].length;
        int h = data.length;
        double[] xkeys = new double[h];
        double[] ykeys = new double[w];

        for (int x = 0; x < h; x++) {
            xkeys[x] = originalBlockHeight * x + 1;
        }
        for (int x = 0; x < w; x++) {
            ykeys[x] = originalBlockWidth * x + 1;
        }
        Interpolation2DLUT lut = new Interpolation2DLUT(xkeys, ykeys, data,
                Interpolation2DLUT.Algo.BILINEAR);

        int neww = (int) Math.round((double) screenWidth / targetBlockWidth) +
                   1;
        int newh = (int) Math.round((double) screenHeight / targetBlockHeight) +
                   1;

        double[][] result = new double[newh][neww];
        for (int x = 0; x < newh; x++) {
            for (int y = 0; y < neww; y++) {
                int x_ = x * targetBlockHeight + 1;
                int y_ = y * targetBlockWidth + 1;
                if (x_ > xkeys[h - 1] || y_ > ykeys[w - 1]) {
                    if (x_ > xkeys[h - 1] && y_ > ykeys[w - 1]) {
                        result[x][y] = lut.getValue(xkeys[h - 1], ykeys[w - 1]);
                    } else if (x_ > xkeys[h - 1]) {
                        result[x][y] = lut.getValue(xkeys[h - 1], y_);
                    } else if (y_ > ykeys[w - 1]) {
                        result[x][y] = lut.getValue(x_, ykeys[w - 1]);
                    }
//          System.out.println(x_ + " " + y_);
                } else {
                    result[x][y] = lut.getValue(x_, y_);
                }

            }
        }

        return result;
    }

    /**
     *
     * @param data CorrectionData
     * @param originalBlockWidth int
     * @param originalBlockHeight int
     * @param targetBlockWidth int
     * @param targetBlockHeight int
     * @param screenWidth int
     * @param screenHeight int
     * @return double[][][]
     * @deprecated
     */
    static double[][][] interpolate(CorrectionData data, int originalBlockWidth,
                                    int originalBlockHeight,
                                    int targetBlockWidth,
                                    int targetBlockHeight, int screenWidth,
                                    int screenHeight
            ) {
        double[][] data10 = null; // data.getCorrectData(0, RGB.Channel.R);
        double[][] data30 = null; // data.getCorrectData(1, RGB.Channel.R);
        double[][] data70 = null; //data.getCorrectData(2, RGB.Channel.R);
        double[][] result10 = interpolate(data10, originalBlockWidth,
                                          originalBlockHeight, targetBlockWidth,
                                          targetBlockHeight, screenWidth,
                                          screenHeight);
        double[][] result30 = interpolate(data30, originalBlockWidth,
                                          originalBlockHeight, targetBlockWidth,
                                          targetBlockHeight, screenWidth,
                                          screenHeight);
        double[][] result70 = interpolate(data70, originalBlockWidth,
                                          originalBlockHeight, targetBlockWidth,
                                          targetBlockHeight, screenWidth,
                                          screenHeight);

        return new double[][][] {
                result10, result30, result70};
    }

    static List<String[]> toListString(double[][] data) {
        int height = data.length;
        int width = data[0].length;
        List<String[]> result = new ArrayList(height);
        for (int x = 0; x < height; x++) {
            String[] array = new String[width];
            for (int y = 0; y < width; y++) {
                array[y] = Double.toString(data[x][y]);
            }
            result.add(array);
        }
        return result;
    }

    public static void writeToCSV(String filename, double[][][] result) throws
            IOException {
        int[] grayLevelArray = new int[] {
                               25, 76, 178};
        writeToCSV(filename, result, grayLevelArray);
    }

    public static void writeToCSV(String filename, double[][][] result,
                                  int[] grayLevelArray) throws
            IOException {
        Writer writer = new BufferedWriter(new FileWriter(filename));
//        CSVWriter.INITIAL_STRING_SIZE=1280;
        CSVWriter csv = new CSVWriter(writer, ',', CSVWriter.NO_QUOTE_CHARACTER,
                                      CSVWriter.NO_ESCAPE_CHARACTER);

        csv.writeNext(new String[] {"Correction Data"});
        csv.writeNext(new String[] {"3", "0"});
        double[][] dataL1 = result[0];
        double[][] dataL2 = result[1];
        double[][] dataL3 = result[2];
        String height = Integer.toString(dataL1.length);
        String width = Integer.toString(dataL1[0].length);

        List<String[]> dataL1List = toListString(dataL1);
        List<String[]> dataL2List = toListString(dataL2);
        List<String[]> dataL3List = toListString(dataL3);
        List<String[]> [] dataList = new List[] {
                                     dataL1List, dataL2List, dataL3List};
//    int[] grayLevelArray = new int[] {
//        25, 76, 178};
        for (int x = 0; x < 3; x++) {
            csv.writeNext(new String[] {width, height});
            String grayLevel = Integer.toString(grayLevelArray[x]);
            List<String[]> data = dataList[x];
            csv.writeNext(new String[] {"\"RED\"", "\"Level" + (x + 1) + "\"",
                          grayLevel});

            csv.writeAll(data);

            csv.writeNext(new String[] {"\"GREEN\"", "\"Level" + (x + 1) + "\"",
                          grayLevel});

            csv.writeAll(data);

            csv.writeNext(new String[] {"\"BLUE\"", "\"Level" + (x + 1) + "\"",
                          grayLevel});

            csv.writeAll(data);
            csv.flush();
        }

        csv.close();
    }

    /**
     *
     * @param blockWidth int
     * @param blockHeight int
     * @param screenWidth int
     * @param screenHeight int
     * @param patternArray int[][][]
     * @param grayLevelArray int[]
     * @param mag int[]
     * @return double[][][]
     * 舊設計, 已經不在pattern列表裏
     */
    private static double[][][] _produceLimitLUT_0(int blockWidth,
            int blockHeight, int screenWidth,
            int screenHeight,
            int[][][] patternArray,
            int[] grayLevelArray, int[] mag) {
        if (patternArray.length != 5) {
            throw new IllegalArgumentException("patternArray.length != 5");
        }
        int blockWidthCount = (int) Math.ceil((double) screenWidth / blockWidth) +
                              1;
        int blockHeightCount = (int) Math.ceil((double) screenHeight /
                                               blockHeight) +
                               1;
        int pieceBlockWidthCount = blockWidthCount / 5;
        System.out.println(blockWidthCount + " " + blockHeightCount + " " +
                           pieceBlockWidthCount);
        double[][][] result = new double[3][blockHeightCount][blockWidthCount];
        double mag1 = Math.pow(2, mag[0]);
        double mag2 = Math.pow(2, mag[1]);
        double mag3 = Math.pow(2, mag[2]);

        for (int x = 0; x < 5; x++) {
            int n = x * pieceBlockWidthCount;
            int[][] pattern = patternArray[x];
            int patternH = pattern.length;
            int patternW = pattern[0].length;
            int start = 0, end = 0;
            if (x != 4) { //最後依個
                start = (n + 1) - 1;
                end = n + pieceBlockWidthCount - 1;
            } else {
                start = (n + 1) - 1;
                end = blockWidthCount - 1;
            }

            for (int h = 0; h < blockHeightCount; h++) {
                for (int w = start; w <= end; w++) {
                    int h_ = h % patternH;
                    int w_ = w % patternW;
                    double plane1 = (grayLevelArray[0] * 16. +
                                     pattern[h_][w_] * mag1) /
                                    16.;
                    double plane2 = (grayLevelArray[1] * 16. +
                                     pattern[h_][w_] * mag2) /
                                    16.;
                    double plane3 = (grayLevelArray[2] * 16. +
                                     pattern[h_][w_] * mag3) /
                                    16.;
                    plane1 = plane1 < 0 ? 0 : plane1;
                    plane3 = plane3 > 255 ? 255 : plane3;
                    result[0][h][w] = plane1;
                    result[1][h][w] = plane2;
                    result[2][h][w] = plane3;
                }
            }

        }
        return result;
    }

    private static double[][][] _produceLimitLUT_1(int blockWidth,
            int blockHeight, int screenWidth,
            int screenHeight,
            int[][][] patternArray,
            int[] grayLevelArray, int[] mag) {
        if (patternArray.length != 5) {
            throw new IllegalArgumentException("patternArray.length != 5");
        }
        int blockWidthCount = (int) Math.ceil((double) screenWidth / blockWidth) +
                              1;
        int blockHeightCount = (int) Math.ceil((double) screenHeight /
                                               blockHeight) +
                               1;
        int pieceBlockHeightCount = blockHeightCount / 5;
//    System.out.println(blockWidthCount + " " + blockHeightCount + " " +
//                       pieceBlockHeightCount);
        double[][][] result = new double[3][blockHeightCount][blockWidthCount];
        double mag1 = Math.pow(2, mag[0]);
        double mag2 = Math.pow(2, mag[1]);
        double mag3 = Math.pow(2, mag[2]);

        for (int x = 0; x < 5; x++) {
            int n = x * pieceBlockHeightCount;
            int[][] pattern = patternArray[x];
            int patternH = pattern.length;
            int patternW = pattern[0].length;
            int start = 0, end = 0;
            if (x != 4) { //最後依個
                start = (n + 1) - 1;
                end = n + pieceBlockHeightCount - 1;
            } else {
                start = (n + 1) - 1;
                end = blockHeightCount - 1;
            }

            for (int w = 0; w < blockWidthCount; w++) {
                for (int h = start; h <= end; h++) {
                    int h_ = h % patternH;
                    int w_ = w % patternW;
                    double plane1 = (grayLevelArray[0] * 16. +
                                     pattern[h_][w_] * mag1) /
                                    16.;
                    double plane2 = (grayLevelArray[1] * 16. +
                                     pattern[h_][w_] * mag2) /
                                    16.;
                    double plane3 = (grayLevelArray[2] * 16. +
                                     pattern[h_][w_] * mag3) /
                                    16.;
                    plane1 = plane1 < 0 ? 0 : plane1;
                    plane3 = plane3 > 255 ? 255 : plane3;
                    result[0][h][w] = plane1;
                    result[1][h][w] = plane2;
                    result[2][h][w] = plane3;
                }
            }

        }
        return result;
    }

    private static double[][][] _produceLimitLUT_2(int blockWidth,
            int blockHeight, int screenWidth,
            int screenHeight,
            int[][][] patternArray,
            int[] grayLevelArray, int[] mag) {
        if (patternArray.length != 5) {
            throw new IllegalArgumentException("patternArray.length != 5");
        }
        int blockWidthCount = (int) Math.ceil((double) screenWidth / blockWidth) +
                              1;
        int blockHeightCount = (int) Math.ceil((double) screenHeight /
                                               blockHeight) +
                               1;
        int pieceBlockWidthCount = blockWidthCount / 10;
//    System.out.println(blockWidthCount + " " + blockHeightCount + " " +
//                       pieceBlockWidthCount);
        double[][][] result = new double[3][blockHeightCount][blockWidthCount];
        double mag1 = Math.pow(2, mag[0]);
        double mag2 = Math.pow(2, mag[1]);
        double mag3 = Math.pow(2, mag[2]);

        for (int x = 0; x < 10; x++) {
            int n = x * pieceBlockWidthCount;
            int[][] pattern = patternArray[x % 5];
            int patternH = pattern.length;
            int patternW = pattern[0].length;
            int start = 0, end = 0;
            if (x != 9) { //最後依個
                start = (n + 1) - 1;
                end = n + pieceBlockWidthCount - 1;
            } else {
                start = (n + 1) - 1;
                end = blockWidthCount - 1;
            }

            for (int h = 0; h < blockHeightCount; h++) {
                for (int w = start; w <= end; w++) {
                    int h_ = h % patternH;
                    int w_ = w % patternW;
                    double plane1 = (grayLevelArray[0] * 16. +
                                     pattern[h_][w_] * mag1) /
                                    16.;
                    double plane2 = (grayLevelArray[1] * 16. +
                                     pattern[h_][w_] * mag2) /
                                    16.;
                    double plane3 = (grayLevelArray[2] * 16. +
                                     pattern[h_][w_] * mag3) /
                                    16.;
                    plane1 = plane1 < 0 ? 0 : plane1;
                    plane3 = plane3 > 255 ? 255 : plane3;
                    result[0][h][w] = plane1;
                    result[1][h][w] = plane2;
                    result[2][h][w] = plane3;
                }
            }

        }
        return result;
    }


    static ArrayList[] getTwoLayerLUTData(int[] n, int p1, int p2) {
        ArrayList<Integer> L1 = new ArrayList<Integer>();
        ArrayList<Integer> L2 = new ArrayList<Integer>();
        int n1 = (int) Math.pow(2, n[0]);
        int n2 = (int) Math.pow(2, n[1]);
        for (int L1v = 0; L1v <= 127; L1v++) { //v for lut的值
            int L1c = L1v * n1; //for 補償值 = v * mag
            int L1r = p1 * 4 + L1c; //for L1對應的輸出值 = p1 + L1c

            int L2v = (L1r - p2 * 4) / n2;
            int L2c = L2v * n2;
            int L2r = p2 * 4 + L2c;
            if (n1 == 2 ? (L2r - L1r) == 2 : (L2r - L1r) == 1) {
                L1.add(L1r);
                L2.add(L2r);

            }

        }
        return new ArrayList[] {L1, L2};
    }

    private static void produceItingDelta1Case2() throws IOException {
        final int p1 = 100; //1023 x 10% = 102.3
        final int p2 = 304; //1023 x 30% = 306.9
        final int p3 = 712; //1023 x 70% = 716.1
        final int[] grayLevelArray = new int[] {p1 / 4, p2 / 4, p3 / 4};
        String dir = "iting/";

        //BL-L1
        ArrayList<Double> [] B1 = new ArrayList[3];
        for (int n = 0; n <= 2; n++) {
            B1[n] = new ArrayList<Double>();
            int nn = (int) Math.pow(2, n);
            for (int x = 0, p1Target = p1 * 4 + x * nn; x >= -127 && p1Target > 0;
                                       x--,
                                       p1Target = p1 * 4 + x * nn) {
//                System.out.println(p1Target + " " + x);
                B1[n].add(p1Target / 16.);
            }
        }
        double[][][] B1_1X = generateLUTData(B1[0], 0, p1 / 4);
        double[][][] B1_2X = generateLUTData(B1[1], 0, p1 / 4);
        double[][][] B1_4X = generateLUTData(B1[2], 0, p1 / 4);
        int height = B1_1X[0].length;
        int width = B1_1X[0][0].length;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                B1_1X[1][h][w] = p2 / 4.;
                B1_1X[2][h][w] = p3 / 4.;
                B1_2X[1][h][w] = p2 / 4.;
                B1_2X[2][h][w] = p3 / 4.;
                B1_4X[1][h][w] = p2 / 4.;
                B1_4X[2][h][w] = p3 / 4.;
            }
        }

        writeToCSV(dir + "B1_1X.csv", B1_1X, grayLevelArray);
        writeToCSV(dir + "B1_2X.csv", B1_2X, grayLevelArray);
        writeToCSV(dir + "B1_4X.csv", B1_4X, grayLevelArray);

        //L1-L2 1-2 1-4 2-4
        int[][] nCombo = { {0, 1}, /*{0, 2},*/ {1, 2}
        };
        for (int[] n : nCombo) {
            ArrayList[] L1L2 = getTwoLayerLUTData(n, p1, p2);
            double[][][] L1L2Lut = generateLUTData(L1L2[0], 0, p1 * 4, L1L2[1], 1, p2 * 4);

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    L1L2Lut[0][h][w] /= 16.;
                    L1L2Lut[1][h][w] /= 16.;
                    L1L2Lut[2][h][w] = p3 / 4.;
                }
            }

            int n0 = (int) Math.pow(2, n[0]);
            int n1 = (int) Math.pow(2, n[1]);
            writeToCSV(dir + "L12_" + n0 + "-" + n1 + "X.csv", L1L2Lut,
                       grayLevelArray);
        }
        {
            //L1-L2 1X-2X
            double[][][] L1L2Lut = generateLUTData(new ArrayList<Integer>(), 0, 654,
                    new ArrayList<Integer>(), 1, 708);
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    L1L2Lut[0][h][w] /= 16.;
                    L1L2Lut[1][h][w] /= 16.;
                    L1L2Lut[2][h][w] = p3 / 4.;
                }
            }
            writeToCSV(dir + "L12_1-2X.csv", L1L2Lut,
                       grayLevelArray);
        }

        //L2-L3
        for (int[] n : nCombo) {
            ArrayList[] L2L3 = getTwoLayerLUTData(n, p2, p3);
            double[][][] L2L3Lut = generateLUTData(L2L3[0], 1, p2 * 4, L2L3[1], 2, p3 * 4);

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    L2L3Lut[0][h][w] = p1 / 4.;
                    L2L3Lut[1][h][w] /= 16.;
                    L2L3Lut[2][h][w] /= 16.;
                }
            }

            int n0 = (int) Math.pow(2, n[0]);
            int n1 = (int) Math.pow(2, n[1]);
            writeToCSV(dir + "L23_" + n0 + "-" + n1 + "X.csv", L2L3Lut,
                       grayLevelArray);

        }

        //L3-WL
        ArrayList<Double> [] W3 = new ArrayList[3];
        for (int n = 0; n <= 2; n++) {
            W3[n] = new ArrayList<Double>();
            int nn = (int) Math.pow(2, n);
            for (int x = 0, p3Target = p3 * 4 + x * nn;
                                       x <= 127 && p3Target < 4080; x++,
                                       p3Target = p3 * 4 + x * nn) {
                System.out.println(x + " " + p3Target);
                W3[n].add(p3Target / 16.);
            }
            System.out.println("xxxxx");
//            for (int x = 0, p1Target = p1 + x * nn; x >= -127 && p1Target > 0;
//                                       x--,
//                                       p1Target = p1 + x * nn) {
//                System.out.println(p1Target + " " + x);
//                W3[n].add(x);
//            }
        }
        double[][][] W3_1X = generateLUTData(W3[0], 2, p3 / 4);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                W3_1X[0][h][w] = p1 / 4.;
                W3_1X[1][h][w] = p2 / 4.;
            }
        }
        writeToCSV(dir + "W3_1X.csv", W3_1X, grayLevelArray);

        if (useBackupLutData) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    backupLutData[0][h][w] = p1 / 4.;
                    backupLutData[1][h][w] = p2 / 4.;
                }
            }
            writeToCSV(dir + "W3_1X-2.csv", backupLutData, grayLevelArray);
        }

        double[][][] W3_2X = generateLUTData(W3[1], 2, p3 / 4);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                W3_2X[0][h][w] = p1 / 4.;
                W3_2X[1][h][w] = p2 / 4.;
            }
        }
        writeToCSV(dir + "W3_2X.csv", W3_2X, grayLevelArray);
        if (useBackupLutData) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    backupLutData[0][h][w] = p1 / 4.;
                    backupLutData[1][h][w] = p2 / 4.;
                }
            }

            writeToCSV(dir + "W3_2X-2.csv", backupLutData, grayLevelArray);
        }

        double[][][] W3_4X = generateLUTData(W3[2], 2, p3 / 4);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                W3_4X[0][h][w] = p1 / 4.;
                W3_4X[1][h][w] = p2 / 4.;
            }
        }
        writeToCSV(dir + "W3_4X.csv", W3_4X, grayLevelArray);
        if (useBackupLutData) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    backupLutData[0][h][w] = p1 / 4.;
                    backupLutData[1][h][w] = p2 / 4.;
                }
            }

            writeToCSV(dir + "W3_4X-2.csv", backupLutData, grayLevelArray);
        }

    }

    private static double[][][] backupLutData;
    private static boolean useBackupLutData = false;
    static double[][][] generateLUTData(ArrayList<Double> data, int L, int defaultValue) {
        int height = 136;
        int width = 241;
        double[][][] lutData = new double[3][height][width];
        double[][][] lutData2 = new double[3][height][width];
        int size = data.size();
        useBackupLutData = false;

        for (int x = 0; x < 3; x++) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    lutData[L][h][w] = defaultValue;
                    lutData2[L][h][w] = defaultValue;
                }
            }
        }

        for (int x = 0; x < size; x++) {
            int index = x * 2;

            if (index >= width || (index + 1) >= width) {
                backupLutData = lutData2;
                useBackupLutData = true;
                int newindex = index - width + 1;
                for (int h = 0; h < height; h++) {
                    double d = data.get(x);
                    lutData2[L][h][newindex] = d;
                    lutData2[L][h][newindex + 1] = d;
                }
            } else {
                for (int h = 0; h < height; h++) {
                    double d = data.get(x);
                    lutData[L][h][index] = d;
                    lutData[L][h][index + 1] = d;

                }
            }
        }
        return lutData;
    }

    static double[][][] generateLUTData(ArrayList<Integer> data1, int L1,
            int defaultValue1, ArrayList<Integer> data2, int L2, int defaultValue2) {
        if (data1.size() != data2.size()) {
            throw new IllegalStateException("data1.size() != data2.size()");
        }
        int height = 136;
        int width = 241;
        double[][][] lutData = new double[3][height][width];

//        for (int x = 0; x < 3; x++) {
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                lutData[L1][h][w] = defaultValue1;
                lutData[L2][h][w] = defaultValue2;
            }
        }
//        }

        int size = data1.size();
        for (int x = 0; x < size; x++) {
            int index = x * 2;
            for (int h = 0; h < height; h++) {
                int d1 = data1.get(x);
                lutData[L1][h][index] = d1;
                lutData[L1][h][index + 1] = d1;

                int d2 = data2.get(x);
                lutData[L2][h][index] = d2;
                lutData[L2][h][index + 1] = d2;

            }
        }
        return lutData;
    }


    private static void produceItingDelta1Case() throws IOException {
        int p1 = 102; //1023 x 10% = 102.3
        int p2 = 307; //1023 x 30% = 306.9
        int p3 = 716; //1023 x 70% = 716.1
        int nA = 63;
        int nB = 31;
        int nC = 50;
        int[] plane1A = new int[nA];
        int[] plane1B = new int[nB];
        int[] plane1C = new int[nC];
        for (int x = 0; x < nA; x++) {
            plane1A[x] = (x + 1) * 2;
        }
        for (int x = 0; x < nB; x++) {
            plane1B[x] = (x + 1) * 4;
        }
        for (int x = 0; x < nC; x++) {
            plane1C[x] = (x + 1) * 2;
        }

        int[] plane2A = new int[nA];
        int[] plane2B = new int[nB];
        int[] plane2C = new int[nC];
        for (int x = 0; x < nA; x++) {
            plane2A[x] = (int) Math.floor((p1 + plane1A[x] - p2 + 1) / 2);
        }
        for (int x = 0; x < nB; x++) {
            plane2B[x] = (int) Math.floor((p1 + plane1B[x] - p2 + 1) / 4);
        }
        for (int x = 0; x < nC; x++) {
            plane2C[x] = (int) Math.floor((p1 + plane1C[x] * 2 - p2 + 1) / 4);
        }

        double[][][] dataA = new double[3][136][241];
        double[][][] dataB = new double[3][136][241];
        double[][][] dataC = new double[3][136][241];
        for (int x = 0; x < nA; x++) {
            int index = x * 2;
            dataA[0][0][index] = plane1A[x];
            dataA[0][0][index + 1] = plane1A[x];
            dataA[0][1][index] = plane1A[x];
            dataA[0][1][index + 1] = plane1A[x];

            dataA[1][0][index] = plane2A[x];
            dataA[1][0][index + 1] = plane2A[x];
            dataA[1][1][index] = plane2A[x];
            dataA[1][1][index + 1] = plane2A[x];

        }

        for (int x = 0; x < nB; x++) {
            int index = x * 2;
            dataB[0][0][index] = plane1B[x];
            dataB[0][0][index + 1] = plane1B[x];
            dataB[0][1][index] = plane1B[x];
            dataB[0][1][index + 1] = plane1B[x];

            dataB[1][0][index] = plane2B[x];
            dataB[1][0][index + 1] = plane2B[x];
            dataB[1][1][index] = plane2B[x];
            dataB[1][1][index + 1] = plane2B[x];

        }

        for (int x = 0; x < nC; x++) {
            int index = x * 2;
            dataC[0][0][index] = plane1C[x];
            dataC[0][0][index + 1] = plane1C[x];
            dataC[0][1][index] = plane1C[x];
            dataC[0][1][index + 1] = plane1C[x];

            dataC[1][0][index] = plane2C[x];
            dataC[1][0][index + 1] = plane2C[x];
            dataC[1][1][index] = plane2C[x];
            dataC[1][1][index + 1] = plane2C[x];

        }
        int[] grayLevelArray = new int[] {p1, p2, p3};
        writeToCSV("a.csv", dataA, grayLevelArray);
        writeToCSV("b.csv", dataB, grayLevelArray);
        writeToCSV("c.csv", dataC, grayLevelArray);
    }

    public static void main(String[] args) throws IOException, BiffException {
        produceLimitCase();
//    produceRealCase();
//        produceItingDelta1Case();
//        produceItingDelta1Case2();
    }

//    enum Resolution {
//        FHD(1920, 1080), WQHD(2560, 1440), _4K2K(3840, 2160), _5120(5120, 2160);
//
//        Resolution(int width, int height) {
//            this.width = width;
//            this.height = height;
//        }
//
//        public int width;
//        public int height;
//    }


    public static void produceLimitCase() throws IOException {
        int[][][] pattern = new int[][][] { { {
                            127, -127}, {
                            127, -127}, {
                            -127, -127}, {
                            127, 127}
        },

                { {
                127, -127}, {
                -127, 127}
        },

                { {
                0, 0}, {
                127, 0}
        },

                { {
                0, 0}, {
                -127, 0}
        },

                { {
                127, 126}, {
                1, 0}, {
                -127, -126}, {
                -1, 0}
        }
        };

        pattern = new int[][][] { { {0, 0}, {0, 0}
        }, { {0, 0}, {0, 0}
        }, { {0, 0}, {0, 0}
        }, { {0, 0}, {0, 0}
        }, { {0, 0}, {0, 0}
        }
        };

        int[][] magArray = new int[][] { {
                           0, 0, 0}, {
                           1, 1, 1}, {
                           2, 2, 2}, {
                           1, 1, 0}, {
                           2, 2, 1}, {
                           2, 1, 0}
        };

        int[][] blockcase1920 = new int[][] {

                                {
                                8, 8}, {
                                2, 32}, {
                                32, 2}, {
                                8, 16}, {
                                16, 8}, {
                                16, 16}, {
                                4, 16}, {
                                16, 4}

        };

        int[][] blockcase2560 = new int[][] {

                                {
                                8, 16}, {
                                16, 8}
        };

        int[][] blockcase4k2k = new int[][] {
//        {
//        16, 16}, {
//        8, 32}, {
//        32, 8}, {
//        4, 64}, {
//        64, 4}
                                {
                                16, 16}
//                                {
//                                8, 8}
        };

        Resolution res = Resolution._4K2K;
//    Resolution res = Resolution.WQHD;
//    int[][] blockcase = blockcase1920;
        int[][] blockcase = (res == Resolution.FHD) ? blockcase1920 :
                            (res == Resolution.WQHD) ? blockcase2560 :
                            blockcase4k2k;

        int screenWidth = res.width;
        int screenHeight = res.height;
        String dirname = "demura sim/block size/Limit Case/" + screenWidth +
                         "x" +
                         screenHeight + "/";
        int no = 1;
        boolean special = true;
        if (special) {
            dirname = "";
            produceLimitCase0(screenWidth, screenHeight, blockcase[0], pattern,
                              dirname, magArray[0], no++);
//            produceLimitCase1(screenWidth, screenHeight, blockcase[0], pattern,
//                              dirname, magArray[0], no++);


//            dirname = "Y:/Verify Items/Verify LUT/Limit Case.12411/";
//            produceLimitCase2(1920, 1080, new int[] {2, 32}, pattern, dirname,
//                              magArray[0], no++);
//            produceLimitCase2(1920, 1080, new int[] {4, 16}, pattern, dirname,
//                              magArray[1], no++);
//            produceLimitCase2(3840, 2160, new int[] {8, 8}, pattern, dirname,
//                              magArray[2], no++);
//            produceLimitCase2(5120, 2160, new int[] {16, 4}, pattern, dirname,
//                              magArray[3], no++);

            dirname = "Y:/Verify Items/Verify LUT/Limit Case/3840x2160";
//             dirname = "c:/temp/";
//            produceLimitCase2(3840, 2160, new int[] {2, 16}, pattern, dirname,
//                              magArray[0], no++);
//            produceLimitCase2(3840, 2160, new int[] {8, 4}, pattern, dirname,
//                              magArray[0], no++);
//            produceLimitCase2(3840, 2160, new int[] {8, 8}, pattern, dirname,
//                              magArray[0], no++);
//            produceLimitCase2(3840, 2160, new int[] {32, 2}, pattern, dirname,
//                              magArray[0], no++);
//            produceLimitCase2(3840, 2160, new int[] {4, 16}, pattern, dirname,
//                              magArray[0], no++);
//            produceLimitCase2(3840, 2160, new int[] {4, 32}, pattern, dirname,
//                              magArray[0], no++);
//            produceLimitCase2(3840, 2160, new int[] {16, 8}, pattern, dirname,
//                              magArray[0], no++);

        } else {
            if (res == Resolution.FHD) {
                //==8 8
                produceLimitCase1(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[0], no++);
                produceLimitCase1(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[1], no++);
                produceLimitCase1(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[2], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[3], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[4], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[5], no++);
                //==2 32
                produceLimitCase1(screenWidth, screenHeight, blockcase[1],
                                  pattern,
                                  dirname, magArray[0], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[1],
                                  pattern,
                                  dirname, magArray[1], no++);
                //==32 2
                produceLimitCase1(screenWidth, screenHeight, blockcase[2],
                                  pattern,
                                  dirname, magArray[2], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[2],
                                  pattern,
                                  dirname, magArray[3], no++);
                //==8 16
                produceLimitCase1(screenWidth, screenHeight, blockcase[3],
                                  pattern,
                                  dirname, magArray[4], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[3],
                                  pattern,
                                  dirname, magArray[5], no++);
                //==16 8
                produceLimitCase1(screenWidth, screenHeight, blockcase[4],
                                  pattern,
                                  dirname, magArray[0], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[4],
                                  pattern,
                                  dirname, magArray[1], no++);
                //==16 16
                produceLimitCase1(screenWidth, screenHeight, blockcase[5],
                                  pattern,
                                  dirname, magArray[2], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[5],
                                  pattern,
                                  dirname, magArray[3], no++);
                //==4 16
                produceLimitCase1(screenWidth, screenHeight, blockcase[6],
                                  pattern,
                                  dirname, magArray[4], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[6],
                                  pattern,
                                  dirname, magArray[5], no++);
                //==16 4
                produceLimitCase1(screenWidth, screenHeight, blockcase[7],
                                  pattern,
                                  dirname, magArray[0], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[7],
                                  pattern,
                                  dirname, magArray[1], no++);
            } else if (res == Resolution.WQHD) {
                //==8 16
                produceLimitCase1(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[0], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[1], no++);
                //16x8
                produceLimitCase1(screenWidth, screenHeight, blockcase[1],
                                  pattern,
                                  dirname, magArray[2], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[1],
                                  pattern,
                                  dirname, magArray[3], no++);

            } else if (res == Resolution._4K2K) {
                //==16 16
                produceLimitCase1(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[0], no++);
                produceLimitCase1(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[1], no++);
                produceLimitCase1(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[2], no++);

                produceLimitCase2(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[3], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[4], no++);
                produceLimitCase2(screenWidth, screenHeight, blockcase[0],
                                  pattern,
                                  dirname, magArray[5], no++);
            }
        }
    }

    /**
     *
     * @throws IOException
     * @deprecated
     */
    public static void produceLimitCase1() throws IOException {
        int[][] resolutionArray = new int[][] { {
                                  1920, 1080},

//        1920, 1080}, {
//        2560, 1440}, {
//        3840, 2160}
        };

        int[][] blockcase1920 = new int[][] {

                                {
                                8, 8}, {
                                2, 32}, {
                                32, 2}, {
                                8, 16}, {
                                16, 8}, {
                                16, 16}, {
                                4, 16}, {
                                16, 4}

        };

        int[][] blockcase_2560 = new int[][] { {
                                 4, 32}, {
                                 8, 16}, {
                                 16, 8}, {
                                 32, 4}
        };

        int[][] blockcase_3840 = new int[][] { {
                                 16, 16}
        };

        int[][][] blockcases = new int[][][] {
                               blockcase1920, blockcase_2560, blockcase_3840};

        int[][][] pattern = new int[][][] { { {
                            127, -127}, {
                            127, -127}, {
                            -127, -127}, {
                            127, 127}
        },

                { {
                127, -127}, {
                -127, 127}
        },

                { {
                0, 0}, {
                127, 0}
        },

                { {
                0, 0}, {
                -127, 0}
        },

                { {
                127, 126}, {
                1, 0}, {
                -127, -126}, {
                -1, 0}
        }
        };

        int[][] magArray = new int[][] { {
                           0, 0, 0}, {
                           1, 1, 1}, {
                           2, 2, 2}, {
                           1, 1, 0}, {
                           2, 2, 1}, {
                           2, 1, 0}
        };

        int index = 0;
        for (int[] resolution : resolutionArray) {
            int screenWidth = resolution[0];
            int screenHeight = resolution[1];
            int[][] blockcase = blockcases[index++];

            String dirname = "demura sim/block size/Limit Case/" + screenWidth +
                             "x" +
                             screenHeight + "/";
//      _produceLimitCase2(screenWidth, screenHeight, blockcase, pattern, dirname,
//                         magArray);
        }
    }


    private static void produceLimitCase0(int screenWidth, int screenHeight,
                                          int[] block, int[][][] pattern,
                                          String dirname, int[] magArray,
                                          int no) throws
            IOException {
        int[] grayLevelArray = new int[] {
                               25, 76, 178};
//        grayLevelArray = new int[] {
//                         0, 0, 0};

        double[][][] data = null;
        String subdirname, filename;
        String magName;

        int blockwidth = block[0];
        int blockheight = block[1];

        data = _produceLimitLUT_0(blockwidth, blockheight,
                                  screenWidth,
                                  screenHeight, pattern,
                                  grayLevelArray, magArray);
        magName = magArray[0] + "-" + magArray[1] + "-" + magArray[2];
        subdirname = dirname + "/" + no + ".limit0_" + block[0] +
                     " " + block[1] + "(X" + magName + ")/";
//        subdirname="";
        filename = subdirname + "demura_table.csv";
        storeLimitCase(data, subdirname, filename, grayLevelArray);
    }

    private static void produceLimitCase1(int screenWidth, int screenHeight,
                                          int[] block, int[][][] pattern,
                                          String dirname, int[] magArray,
                                          int no) throws
            IOException {
        int[] grayLevelArray = new int[] {
                               25, 76, 178};

        double[][][] data = null;
        String subdirname, filename;
        String magName;

        int blockwidth = block[0];
        int blockheight = block[1];

        data = _produceLimitLUT_1(blockwidth, blockheight,
                                  screenWidth,
                                  screenHeight, pattern,
                                  grayLevelArray, magArray);
        magName = magArray[0] + "-" + magArray[1] + "-" + magArray[2];
        subdirname = dirname + "/" + no + ".limit1_" + block[0] +
                     " " + block[1] + "(X" + magName + ")/";
        filename = subdirname + "demura_table.csv";
        storeLimitCase(data, subdirname, filename, grayLevelArray);
    }

    private static void produceLimitCase2(int screenWidth, int screenHeight,
                                          int[] block, int[][][] pattern,
                                          String dirname, int[] magArray,
                                          int no) throws
            IOException {
        int[] grayLevelArray = new int[] {
                               25, 76, 178};

        double[][][] data = null;
        String subdirname, filename;
        String magName;

        int blockwidth = block[0];
        int blockheight = block[1];

        data = _produceLimitLUT_2(blockwidth, blockheight,
                                  screenWidth,
                                  screenHeight, pattern,
                                  grayLevelArray, magArray);
        magName = magArray[0] + "-" + magArray[1] + "-" + magArray[2];
        subdirname = dirname + "/" + no + ".limit2_" + block[0] +
                     " " + block[1] + "(X" + magName + ")/";
        filename = subdirname + "demura_table.csv";
        storeLimitCase(data, subdirname, filename, grayLevelArray);
    }

    private static void storeLimitCase(double[][][] data, String dirname,
                                       String filename, int[] grayLevelArray) throws
            IOException {
        System.out.println(dirname.substring(dirname.lastIndexOf("//") + 2,
                                             dirname.lastIndexOf("/")));
//    System.out.println(dirname);
        File subdir = new File(dirname);
        if (!subdir.exists()) {
            subdir.mkdir();
        }

        writeToCSV(filename, data, grayLevelArray);
    }

    public static void produceRealCase() throws BiffException, IOException {
        CorrectionData data = new CorrectionData(
                "demura sim/Panel 50''/No1/LUT/20130603--0001(1)_data(final).csv");
//        "demura sim/Panel 50''/No3/LUT/20130527--0006(1)_data(final).csv");
//        "demura sim/Panel 50''/No8/LUT/20130527--0004(1)_data(final).csv");
//    String dirname = "demura sim/block size/Real Case/No.1";
//    String dirname = "demura sim/block size/Real Case/No.3";
        int[][] blockcase = new int[][] {

                            {
                            8, 8}, {
                            2, 32}, {
                            32, 2}, {
                            8, 16}, {
                            16, 8}, {
                            16, 16}, {
                            4, 16}, {
                            16, 4}

        };

        int[][] blockcase_2560 = new int[][] { {
                                 4, 32}, {
                                 8, 16}, {
                                 16, 8}, {
                                 32, 4}
        };

        int[][] blockcase_3840 = new int[][] { {
                                 16, 16}
        };

        execRealCase(blockcase, 1920, 1080,
                     "demura sim/block size/Real Case/No.1/1920x1080", data);
        execRealCase(blockcase_2560, 2560, 1440,
                     "demura sim/block size/Real Case/No.1/2560x1440", data);
        execRealCase(blockcase_3840, 3840, 2160,
                     "demura sim/block size/Real Case/No.1/3840x2160", data);

//    for (int[] block : blockcase) {
//      double[][][] result = interpolate(data, 8, 8, block[0], block[1], 1920,
//                                        1080);
//
//      String subdirname = dirname + "/real_" + block[0] + "x" + block[1];
//      File dir = new File(subdirname);
//      if (!dir.exists()) {
//        dir.mkdir();
//      }
//      String filename = subdirname + "/real_" + block[0] + "x" + block[1] +
//          ".csv";
//
//      writeToCSV(filename, result);

//  }

        /**
         * "8x8 (FHD), 16x8 (WQHD), 8x16 (WQHD)
         2x32, 4x16, 8x8, 16x4, 32x2
         4x32, 8x16, 16x8, 32x4
         8x32, 16x16, 32x8"

         */
    }

    /**
     *
     * @param blockCase int[][]
     * @param width int
     * @param height int
     * @param dirname String
     * @param data CorrectionData
     * @throws IOException
     * @deprecated
     */
    private static void execRealCase(int[][] blockCase, int width, int height,
                                     String dirname,
                                     CorrectionData data) throws IOException {
        for (int[] block : blockCase) {
            double[][][] result = interpolate(data, 8, 8, block[0], block[1],
                                              width,
                                              height);

            String subdirname = dirname + "/real_" + block[0] + "x" + block[1];
            File dir = new File(subdirname);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String filename = subdirname + "/real_" + block[0] + "x" + block[1] +
                              ".csv";

            writeToCSV(filename, result);

        }

    }
}
