package auo.mura.verify;

import auo.mura.CorrectionData;
import jxl.read.biff.BiffException;
import java.io.IOException;
import shu.cms.colorspace.depend.*;
import auo.mura.MuraCompensationProducer;
import shu.math.lut.Interpolation2DLUT;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.*;
import java.io.BufferedWriter;
import java.io.Writer;
import java.util.List;
import java.util.ArrayList;

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

    int neww = (int) Math.round( (double) screenWidth / targetBlockWidth) + 1;
    int newh = (int) Math.round( (double) screenHeight / targetBlockHeight) + 1;

    double[][] result = new double[newh][neww];
    for (int x = 0; x < newh; x++) {
      for (int y = 0; y < neww; y++) {
        int x_ = x * targetBlockHeight + 1;
        int y_ = y * targetBlockWidth + 1;
        if (x_ > xkeys[h - 1] || y_ > ykeys[w - 1]) {
          if (x_ > xkeys[h - 1] && y_ > ykeys[w - 1]) {
            result[x][y] = lut.getValue(xkeys[h - 1], ykeys[w - 1]);
          }
          else if (x_ > xkeys[h - 1]) {
            result[x][y] = lut.getValue(xkeys[h - 1], y_);
          }
          else if (y_ > ykeys[w - 1]) {
            result[x][y] = lut.getValue(x_, ykeys[w - 1]);
          }
//          System.out.println(x_ + " " + y_);
        }
        else {
          result[x][y] = lut.getValue(x_, y_);
        }

      }
    }

    return result;
  }

  static double[][][] interpolate(CorrectionData data, int originalBlockWidth,
                                  int originalBlockHeight, int targetBlockWidth,
                                  int targetBlockHeight, int screenWidth,
                                  int screenHeight
      ) {
    double[][] data10 = data.getCorrectData(0, RGB.Channel.R);
    double[][] data30 = data.getCorrectData(1, RGB.Channel.R);
    double[][] data70 = data.getCorrectData(2, RGB.Channel.R);
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
    Writer writer = new BufferedWriter(new FileWriter(filename));
    CSVWriter csv = new CSVWriter(writer, ',', CSVWriter.NO_QUOTE_CHARACTER,
                                  CSVWriter.NO_ESCAPE_CHARACTER);

    csv.writeNext(new String[] {"Correction Data"});
    csv.writeNext(new String[] {"3", "0"});
    double[][] data10 = result[0];
    double[][] data30 = result[1];
    double[][] data70 = result[2];
    String height = Integer.toString(data10.length);
    String width = Integer.toString(data10[0].length);

    List<String[]> data10List = toListString(data10);
    List<String[]> data30List = toListString(data30);
    List<String[]> data70List = toListString(data70);
    List<String[]> [] dataList = new List[] {
        data10List, data30List, data70List};
    int[] grayLevelArray = new int[] {
        25, 76, 178};
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
    }

    csv.flush();
    csv.close();
  }

  static double[][][] produceLimitLUT_0(int blockWidth,
                                        int blockHeight, int screenWidth,
                                        int screenHeight,
                                        int[][][] patternArray,
                                        int[] grayLevelArray, int[] mag) {
    if (patternArray.length != 5) {
      throw new IllegalArgumentException("patternArray.length != 5");
    }
    int blockWidthCount = (int) Math.ceil( (double) screenWidth / blockWidth) +
        1;
    int blockHeightCount = (int) Math.ceil( (double) screenHeight / blockHeight) +
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
      }
      else {
        start = (n + 1) - 1;
        end = blockWidthCount - 1;
      }

      for (int h = 0; h < blockHeightCount; h++) {
        for (int w = start; w <= end; w++) {
          int h_ = h % patternH;
          int w_ = w % patternW;
          double plane1 = (grayLevelArray[0] * 16. + pattern[h_][w_] * mag1) /
              16.;
          double plane2 = (grayLevelArray[1] * 16. + pattern[h_][w_] * mag2) /
              16.;
          double plane3 = (grayLevelArray[2] * 16. + pattern[h_][w_] * mag3) /
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

  static double[][][] produceLimitLUT_1(int blockWidth,
                                        int blockHeight, int screenWidth,
                                        int screenHeight,
                                        int[][][] patternArray,
                                        int[] grayLevelArray, int[] mag) {
    if (patternArray.length != 5) {
      throw new IllegalArgumentException("patternArray.length != 5");
    }
    int blockWidthCount = (int) Math.ceil( (double) screenWidth / blockWidth) +
        1;
    int blockHeightCount = (int) Math.ceil( (double) screenHeight / blockHeight) +
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
      }
      else {
        start = (n + 1) - 1;
        end = blockHeightCount - 1;
      }

      for (int w = 0; w < blockWidthCount; w++) {
        for (int h = start; h <= end; h++) {
          int h_ = h % patternH;
          int w_ = w % patternW;
          double plane1 = (grayLevelArray[0] * 16. + pattern[h_][w_] * mag1) /
              16.;
          double plane2 = (grayLevelArray[1] * 16. + pattern[h_][w_] * mag2) /
              16.;
          double plane3 = (grayLevelArray[2] * 16. + pattern[h_][w_] * mag3) /
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

  static double[][][] produceLimitLUT_2(int blockWidth,
                                        int blockHeight, int screenWidth,
                                        int screenHeight,
                                        int[][][] patternArray,
                                        int[] grayLevelArray, int[] mag) {
    if (patternArray.length != 5) {
      throw new IllegalArgumentException("patternArray.length != 5");
    }
    int blockWidthCount = (int) Math.ceil( (double) screenWidth / blockWidth) +
        1;
    int blockHeightCount = (int) Math.ceil( (double) screenHeight / blockHeight) +
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
      }
      else {
        start = (n + 1) - 1;
        end = blockWidthCount - 1;
      }

      for (int h = 0; h < blockHeightCount; h++) {
        for (int w = start; w <= end; w++) {
          int h_ = h % patternH;
          int w_ = w % patternW;
          double plane1 = (grayLevelArray[0] * 16. + pattern[h_][w_] * mag1) /
              16.;
          double plane2 = (grayLevelArray[1] * 16. + pattern[h_][w_] * mag2) /
              16.;
          double plane3 = (grayLevelArray[2] * 16. + pattern[h_][w_] * mag3) /
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

  public static void main(String[] args) throws IOException, BiffException {
    produceLimitCase();
//    produceRealCase();
  }

  enum Resolution {
    FHD(1920, 1080), WQHD(2560, 1440), _4K2K(3840, 2160);

    Resolution(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public int width;
    public int height;
  }

  public static void produceLimitCase() throws IOException {
    int[][][] pattern = new int[][][] {
        {
        {
        127, -127}, {
        127, -127}, {
        -127, -127}, {
        127, 127}
    },

        {
        {
        127, -127}, {
        -127, 127}
    },

        {
        {
        0, 0}, {
        127, 0}
    },

        {
        {
        0, 0}, {
        -127, 0}
    },

        {
        {
        127, 126}, {
        1, 0}, {
        -127, -126}, {
        -1, 0}
    }
    };

    int[][] magArray = new int[][] {
        {
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

//        {
//        4, 32}, {
//        8, 16}, {
//        16, 8}, {
//        32, 4}, {
//        4, 32}, {
//        8, 16}, {
//        16, 8}, {
//        32, 4}
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
    };

    Resolution res = Resolution._4K2K;
//    Resolution res = Resolution.WQHD;
//    int[][] blockcase = blockcase1920;
    int[][] blockcase = (res == Resolution.FHD) ? blockcase1920 :
        (res == Resolution.WQHD) ? blockcase2560 : blockcase4k2k;

//    int screenWidth = 1920;
//    int screenHeight = 1080;
    int screenWidth = res.width;
    int screenHeight = res.height;
    String dirname = "demura sim/block size/Limit Case/" + screenWidth + "x" +
        screenHeight + "/";
    int no = 28;
    boolean special = false;
    if (special) {
      _produceLimitCase0(screenWidth, screenHeight, blockcase[0], pattern,
                         dirname, magArray[0], no++);
    }
    else {
      if (res == Resolution.FHD) {
        //==8 8
        _produceLimitCase1(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[0], no++);
        _produceLimitCase1(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[1], no++);
        _produceLimitCase1(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[2], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[3], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[4], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[5], no++);
        //==2 32
        _produceLimitCase1(screenWidth, screenHeight, blockcase[1], pattern,
                           dirname, magArray[0], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[1], pattern,
                           dirname, magArray[1], no++);
        //==32 2
        _produceLimitCase1(screenWidth, screenHeight, blockcase[2], pattern,
                           dirname, magArray[2], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[2], pattern,
                           dirname, magArray[3], no++);
        //==8 16
        _produceLimitCase1(screenWidth, screenHeight, blockcase[3], pattern,
                           dirname, magArray[4], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[3], pattern,
                           dirname, magArray[5], no++);
        //==16 8
        _produceLimitCase1(screenWidth, screenHeight, blockcase[4], pattern,
                           dirname, magArray[0], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[4], pattern,
                           dirname, magArray[1], no++);
        //==16 16
        _produceLimitCase1(screenWidth, screenHeight, blockcase[5], pattern,
                           dirname, magArray[2], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[5], pattern,
                           dirname, magArray[3], no++);
        //==4 16
        _produceLimitCase1(screenWidth, screenHeight, blockcase[6], pattern,
                           dirname, magArray[4], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[6], pattern,
                           dirname, magArray[5], no++);
        //==16 4
        _produceLimitCase1(screenWidth, screenHeight, blockcase[7], pattern,
                           dirname, magArray[0], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[7], pattern,
                           dirname, magArray[1], no++);
      }
      else if (res == Resolution.WQHD) {
        //==8 16
        _produceLimitCase1(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[0], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[1], no++);
        //16x8
        _produceLimitCase1(screenWidth, screenHeight, blockcase[1], pattern,
                           dirname, magArray[2], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[1], pattern,
                           dirname, magArray[3], no++);

      }
      else if (res == Resolution._4K2K) {
        //==16 16
        _produceLimitCase1(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[0], no++);
        _produceLimitCase1(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[1], no++);
        _produceLimitCase1(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[2], no++);

        _produceLimitCase2(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[3], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[0], pattern,
                           dirname, magArray[4], no++);
        _produceLimitCase2(screenWidth, screenHeight, blockcase[0], pattern,
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
    int[][] resolutionArray = new int[][] {
        {
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

    int[][] blockcase_2560 = new int[][] {
        {
        4, 32}, {
        8, 16}, {
        16, 8}, {
        32, 4}
    };

    int[][] blockcase_3840 = new int[][] {
        {
        16, 16}
    };

    int[][][] blockcases = new int[][][] {
        blockcase1920, blockcase_2560, blockcase_3840};

    int[][][] pattern = new int[][][] {
        {
        {
        127, -127}, {
        127, -127}, {
        -127, -127}, {
        127, 127}
    },

        {
        {
        127, -127}, {
        -127, 127}
    },

        {
        {
        0, 0}, {
        127, 0}
    },

        {
        {
        0, 0}, {
        -127, 0}
    },

        {
        {
        127, 126}, {
        1, 0}, {
        -127, -126}, {
        -1, 0}
    }
    };

    int[][] magArray = new int[][] {
        {
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

      String dirname = "demura sim/block size/Limit Case/" + screenWidth + "x" +
          screenHeight + "/";
//      _produceLimitCase2(screenWidth, screenHeight, blockcase, pattern, dirname,
//                         magArray);
    }
  }

  /**
   *
   * @param screenWidth int
   * @param screenHeight int
   * @param blockcase int[][]
   * @param pattern int[][][]
   * @param dirname String
   * @throws IOException
   * @deprecated
   */
  public static void X_produceLimitCase1(int screenWidth, int screenHeight,
                                         int[][] blockcase, int[][][] pattern,
                                         String dirname) throws IOException {
    int[] grayLevelArray = new int[] {
        25, 76, 178};
    for (int[] block : blockcase) {
      int blockwidth = block[0];
      int blockheight = block[1];
      for (int magnitude = 0; magnitude <= 2; magnitude++) {
        double[][][] data = produceLimitLUT_0(blockwidth, blockheight,
                                              screenWidth,
                                              screenHeight, pattern,
                                              grayLevelArray,
                                              new int[] {magnitude, magnitude,
                                              magnitude});
        String subdirname = dirname + "/limit1_" + block[0] +
            " " + block[1] + "(X" + magnitude + ")/";
        String filename = subdirname + "limit1_" + block[0] +
            " " + block[1] + "(X" + magnitude + ").csv";
        storeLimitCase(data, subdirname, filename);

        data = produceLimitLUT_1(blockwidth, blockheight,
                                 screenWidth,
                                 screenHeight, pattern,
                                 grayLevelArray, new int[] {magnitude,
                                 magnitude, magnitude});
        subdirname = dirname + "/limit2_" + block[0] +
            " " + block[1] + "(X" + magnitude + ")/";
        filename = subdirname + "limit2_" + block[0] +
            " " + block[1] + "(X" + magnitude + ").csv";
        storeLimitCase(data, subdirname, filename);

        data = produceLimitLUT_2(blockwidth, blockheight,
                                 screenWidth,
                                 screenHeight, pattern,
                                 grayLevelArray, new int[] {magnitude,
                                 magnitude, magnitude});
        subdirname = dirname + "/limit3_" + block[0] +
            " " + block[1] + "(X" + magnitude + ")/";
        filename = subdirname + "limit3_" + block[0] +
            " " + block[1] + "(X" + magnitude + ").csv";
        storeLimitCase(data, subdirname, filename);

      }
    }
  }

  private static void _produceLimitCase0(int screenWidth, int screenHeight,
                                         int[] block, int[][][] pattern,
                                         String dirname, int[] magArray, int no) throws
      IOException {
    int[] grayLevelArray = new int[] {
        25, 76, 178};

    double[][][] data = null;
    String subdirname, filename;
    String magName;

    int blockwidth = block[0];
    int blockheight = block[1];

    data = produceLimitLUT_0(blockwidth, blockheight,
                             screenWidth,
                             screenHeight, pattern,
                             grayLevelArray, magArray);
    magName = magArray[0] + "-" + magArray[1] + "-" + magArray[2];
    subdirname = dirname + "/" + no + ".limit0_" + block[0] +
        " " + block[1] + "(X" + magName + ")/";
    filename = subdirname + "demura_table.csv";
    storeLimitCase(data, subdirname, filename);
  }

  private static void _produceLimitCase1(int screenWidth, int screenHeight,
                                         int[] block, int[][][] pattern,
                                         String dirname, int[] magArray, int no) throws
      IOException {
    int[] grayLevelArray = new int[] {
        25, 76, 178};

    double[][][] data = null;
    String subdirname, filename;
    String magName;

    int blockwidth = block[0];
    int blockheight = block[1];

    data = produceLimitLUT_1(blockwidth, blockheight,
                             screenWidth,
                             screenHeight, pattern,
                             grayLevelArray, magArray);
    magName = magArray[0] + "-" + magArray[1] + "-" + magArray[2];
    subdirname = dirname + "/" + no + ".limit1_" + block[0] +
        " " + block[1] + "(X" + magName + ")/";
    filename = subdirname + "demura_table.csv";
    storeLimitCase(data, subdirname, filename);
  }

  private static void _produceLimitCase2(int screenWidth, int screenHeight,
                                         int[] block, int[][][] pattern,
                                         String dirname, int[] magArray, int no) throws
      IOException {
    int[] grayLevelArray = new int[] {
        25, 76, 178};

    double[][][] data = null;
    String subdirname, filename;
    String magName;

    int blockwidth = block[0];
    int blockheight = block[1];

    data = produceLimitLUT_2(blockwidth, blockheight,
                             screenWidth,
                             screenHeight, pattern,
                             grayLevelArray, magArray);
    magName = magArray[0] + "-" + magArray[1] + "-" + magArray[2];
    subdirname = dirname + "/" + no + ".limit2_" + block[0] +
        " " + block[1] + "(X" + magName + ")/";
    filename = subdirname + "demura_table.csv";
    storeLimitCase(data, subdirname, filename);
  }

  private static void storeLimitCase(double[][][] data, String dirname,
                                     String filename) throws IOException {
    System.out.println(dirname.substring(dirname.lastIndexOf("//") + 2,
                                         dirname.lastIndexOf("/")));
//    System.out.println(dirname);
    File subdir = new File(dirname);
    if (!subdir.exists()) {
      subdir.mkdir();
    }

    writeToCSV(filename, data);
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

    int[][] blockcase_2560 = new int[][] {
        {
        4, 32}, {
        8, 16}, {
        16, 8}, {
        32, 4}
    };

    int[][] blockcase_3840 = new int[][] {
        {
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

  private static void execRealCase(int[][] blockCase, int width, int height,
                                   String dirname,
                                   CorrectionData data) throws IOException {
    for (int[] block : blockCase) {
      double[][][] result = interpolate(data, 8, 8, block[0], block[1], width,
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
