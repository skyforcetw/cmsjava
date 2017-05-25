package auo.mura;

import java.io.*;
import java.util.*;

import java.awt.image.*;

import shu.image.*;
import shu.plot.*;

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
public class Dithering {
  private int databit = 12;
  public Dithering() {
  }

  public short[][] getFRC(short[][] compensationData) {
    return null;
  }

  static final short[][] BayerMatrix = new short[][] {
      {
      0, 8, 2, 10}, {
      12, 4, 14, 6}, {
      3, 11, 1, 9}, {
      15, 7, 13, 5}
  };

  static final short[][] SpiralMatrix = new short[][] {
      {
      10, 9, 8, 7}, {
      11, 16, 15, 6}, {
      12, 13, 14, 5}, {
      1, 2, 3, 4}
  };

  static final short[][] ClassicalMatrix = new short[][] {
      {
      4, 8, 10, 1}, {
      11, 15, 14, 5}, {
      7, 16, 13, 9}, {
      3, 12, 6, 2}
  };
  static short[][] thresholdMatrix = new short[][] {
      {
      11, 4, 3, 12}, {
      3, 15, 7, 8}, {
      10, 5, 13, 5}, {
      4, 14, 9, 6}
  };

  public short[][] getOrderDithring(short[][]
                                    compensationData) {
    return getOrderDithring(compensationData, thresholdMatrix, databit);

  }

  public short[][] getOrderDithring(short[][]
                                    compensationData,
                                    short[][] thresholdMatrix) {
    return getOrderDithring(compensationData, thresholdMatrix, databit);
  }

  public static short[][] getOrderDithring(short[][]
                                           compensationData,
                                           final short[][] thresholdMatrix,
                                           int dataBit) {

    int h = compensationData.length;
    int w = compensationData[0].length;
    int orderWCount = w / 4;
    int orderHCount = h / 4;
    short delta = (short) (Math.pow(2, dataBit - 8));

    for (int y = 0; y < orderHCount; y++) {
      for (int x = 0; x < orderWCount; x++) {
        int hBegin = y * 4;
        int wBegin = x * 4;
        for (int h_ = 0; h_ < 4; h_++) {
          for (int w_ = 0; w_ < 4; w_++) {
            int hindex = hBegin + h_;
            int windex = wBegin + w_;

            short data = compensationData[hindex][windex];
            short integer = (short) (data / delta * delta);
            short error = (short) (data - integer);
            short threshold = thresholdMatrix[h_][w_];
            if (error > threshold) {
              integer += delta;
            }

//
//            short data = compensationData[hindex][windex];
//            data += threshold;
//            short integer = (short) (data / delta * delta);

            compensationData[hindex][windex] = integer;
          }
        }
      }
    }

    return compensationData;
  }

  static long checksum(short[][] data) {
    long checksum = 0;
    for (int x = 0; x < data.length; x++) {
      for (int y = 0; y < data[0].length; y++) {
        int d = data[x][y];
        if (d < 0) {
          return -1;
        }
        checksum += d;
      }
    }
    return checksum;
  }

  public short[][] getFloydSteinbergWithOrderDithring(short[][]
      compensationData, short[][] thresholdMatrix) {
    getFloydSteinbergWithOrderDithring(compensationData, thresholdMatrix,
                                       databit);
    return compensationData;

  }

  public short[][] getFloydSteinbergWithOrderDithring(short[][]
      compensationData) {
    return getFloydSteinbergWithOrderDithring(compensationData, thresholdMatrix);
  }

  static boolean modifiedFloydSteinberg = true;
  static void floydSteinberg(int[] abcd, int error) {
    floydSteinberg(abcd, error, modifiedFloydSteinberg);
  }

  static void floydSteinberg(int[] abcd, int error, boolean modified) {
    //╊Θabcd场, 碞琌рerror┻硂pixel  : 7 1 5 3
    if (modified) {
      abcd[0] = (int) Math.round(error * 7. / 16);
      abcd[1] = (error == 1 || error == 7) ? 1 : (error == 9 || error == 15) ?
          0 : (int) Math.round(error * 1. / 16); //1
      abcd[2] = (int) Math.round(error * 5. / 16);
      abcd[3] = (error == 8) ? 0 : (int) Math.round(error * 3. / 16); //3

    }
    else {
      abcd[0] = (int) Math.round(error * 7. / 16);
      abcd[1] = (int) Math.round(error * 1. / 16);
      abcd[2] = (int) Math.round(error * 5. / 16);
      abcd[3] = (int) Math.round(error * 3. / 16);
    }
  }

  static void floydSteinbergCC(int[] abcd, int error, int h, int w, int height,
                               int width) {
    //╊Θabcd场, 碞琌рerror┻硂pixel  : 7 1 5 3

    // *a
    //dcb
    abcd[0] = (int) Math.round(error * 7. / 16);
    abcd[1] = (int) Math.round(error * 1. / 16);
    abcd[2] = (int) Math.round(error * 5. / 16);
    abcd[3] = (int) Math.round(error * 3. / 16);

    if (h == 0) {
      abcd[0] = 0;
    }
    else if (h == (height - 1)) {
      abcd[1] = abcd[2] = abcd[3] = 0;
    }

    if (w == 0) {
      abcd[2] = abcd[3] = 0;
    }
    else if (w == 1) {
      abcd[3] = 0;
    }
    else if (w == (width - 2)) {
      abcd[0] = abcd[1] = 0;
    }
    else if (w == (width - 1)) {
      abcd[0] = abcd[1] = abcd[2] = 0;
    }
  }

  public static void lena(String[] args) throws IOException {
    BufferedImage lena = ImageUtils.loadImage("lena.gif");
    WritableRaster raster = lena.getRaster();
    int width = lena.getWidth();
    int height = lena.getHeight();
    int[] pixels = new int[3];
    int[][] pic = new int[width][height];

    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        raster.getPixel(w, h, pixels);
        pic[w][h] = pixels[0];
//        int oldpixels = pixels[0];
//        int newpixels = oldpixels >= 128 ? 255 : 0;
//        int error = oldpixels - newpixels;
//        pixels[0] = pixels[1] = pixels[2] = newpixels;
//
////        pixels[w + 1][h][0] = pixels[w + 1][h][0] + (int) (7. / 16 * error);
//
//
//        System.out.println(pixels[0]);
      }
    }
    int[] abcd = new int[4];
    for (int h = 0; h < height - 1; h++) {
      for (int w = 1; w < width - 1; w++) {

        int oldpixels = pic[w][h];
        int newpixels = oldpixels >= 128 ? 255 : 0;
        int error = oldpixels - newpixels;
        floydSteinberg(abcd, error);

        pic[w][h] = newpixels;
        pic[w + 1][h] += abcd[0];
        pic[w - 1][h + 1] += abcd[1];
        pic[w][h + 1] += abcd[2];
        pic[w + 1][h + 1] += abcd[3];

//        pic[w + 1][h] += (7. / 16 * error);
//        pic[w - 1][h + 1] += (3. / 16 * error);
//        pic[w][h + 1] += (5. / 16 * error);
//        pic[w + 1][h + 1] += (1. / 16 * error);
      }
    }

    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        pixels[0] = pixels[1] = pixels[2] = pic[w][h];
        raster.setPixel(w, h, pixels);
      }
    }
    ImageUtils.storeBMPImage("lena.bmp", lena);
  }

  public static void grayLevelTest(String[] args) {
    int[] abcd = new int[4];
    for (int error = 0; error < 16; error++) {
      floydSteinberg(abcd, error);
      int total = abcd[0] + abcd[1] + abcd[2] + abcd[3];
      System.out.println(error + " " + total + " " + Arrays.toString(abcd));
    }
  }

//  public static short[][] getFloydSteinbergWikipedia(short[][]
//      compensationData) {
//    return getFloydSteinbergWikipediaWithOrderDithring(compensationData, null);
//  }
  public static short[][] getFloydSteinbergWikipediaWithOrderDithring(short[][]
      compensationData) {
    return getFloydSteinbergWikipediaWithOrderDithring(compensationData,
        thresholdMatrix);
  }

  public static boolean Debug = false;
  public static short[][] getFloydSteinbergWikipediaWithOrderDithring(short[][]
      compensationData, final short[][] thresholdMatrix) {
    int height = compensationData.length;
    int width = compensationData[0].length;

    short delta = (short) (Math.pow(2, 12 - 8));
    int[] abcd = new int[4];
    int matrixh = null != thresholdMatrix ? thresholdMatrix.length : 0;
    int matrixw = null != thresholdMatrix ? thresholdMatrix[0].length : 0;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        short data = (short) compensationData[y][x]; //p
        int error = data % delta;
        short integer = (short) (data - error);
        short threshold = 8;

        if (null != thresholdMatrix) {
          int h_ = y % matrixh;
          int w_ = x % matrixw;
          threshold =
              thresholdMatrix[h_][w_]; //癸莱order matrix
        }

        if (error >= threshold) { //pr>th
//          if (integer >= (255 * 16)) {
//            error = data - integer;
//          }
//          else {
          integer += delta;
          error = error - delta;
//          }
        }
        compensationData[y][x] = integer;
//        System.out.println(y + " " + x + ": " + integer / 16);

        //р计翴error data,┮矪瞶ぇ逞俱计
        if (Debug) {
          switch (compensationData[y][x]) {
            case 16:
              compensationData[y][x] = 64 * 16;
              break;
            case 32:
              compensationData[y][x] = 128 * 16;
              break;
            case 64:
              compensationData[y][x] = 255 * 16;
              break;
            default:
              compensationData[y][x] = 0;
              break;
          }
        }

        //╊Θabcd场, 碞琌рerror┻硂pixel  : 7 1 5 3
        floydSteinberg(abcd, error, false);
//        floydSteinbergCC(abcd, error, height, width, height, width);
//        abcd[0] = (int) (error * 7. / 16);
//        abcd[1] = (int) (error * 1. / 16);
//        abcd[2] = (int) (error * 5. / 16);
//        abcd[3] = (int) (error * 3. / 16);

        // *a
        //dcb
        if (x + 1 < width) {
          compensationData[y][x + 1] += abcd[0];
//          compensationData[y][x + 1] = (compensationData[y][x + 1] < 0) ? 0 :
//              compensationData[y][x + 1];

        }
        if (x + 1 < width && y + 1 < height) {
          compensationData[y + 1][x + 1] += abcd[1];
//          compensationData[y + 1][x + 1] = (compensationData[y + 1][x + 1] < 0) ?
//              0 : compensationData[y + 1][x + 1];
        }
        if (y + 1 < height) {
          compensationData[y + 1][x] += abcd[2];
//          compensationData[y + 1][x] = (compensationData[y + 1][x] < 0) ? 0 :
//              compensationData[y + 1][x];
        }
        if (x - 1 >= 0 && y + 1 < height) {
          compensationData[y + 1][x - 1] += abcd[3];
//          compensationData[y + 1][x - 1] = (compensationData[y + 1][x - 1] < 0) ?
//              0 : compensationData[y + 1][x - 1];
        }

      }
    }

    return compensationData;
  }

  public static short[][] getFloydSteinbergWikipediaWithOrderDithring_LineBased(short[][]
      compensationData, final short[][] thresholdMatrix) {
    int height = compensationData.length;
    int width = compensationData[0].length;

    short delta = (short) (Math.pow(2, 12 - 8));
    int[] abcd = new int[4];
    int matrixh = null != thresholdMatrix ? thresholdMatrix.length : 0;
    int matrixw = null != thresholdMatrix ? thresholdMatrix[0].length : 0;
    short[] preLine = new short[width];
    short[] nextLine = new short[width];
    for (int x = 0; x < width; x++) {
      preLine[x] = nextLine[x] = 0;
    }
    short preError = 0;
    int max = 0;
//    compensationData[0][0] = (short) (compensationData[0][0] / delta * delta);

//    for (int y = 0; y < (height); y++) {
         for (int y = 0; y < (1); y++) {
      for (int x = 0; x < (width); x++) {
        preLine[x] = nextLine[x];
        nextLine[x] = 0;
      }

      for (int x = 0; x < width; x++) {
        short otherError = (short) (preError + preLine[x]);

        short puredata = compensationData[y][x];
//        System.out.println(puredata);
        short data = (short) (puredata + otherError);
        int error = data % delta;
        short integer = (short) (data - error);

        short threshold = 8;

        if (null != thresholdMatrix) {
          int h_ = y % matrixh;
          int w_ = x % matrixw;
          threshold =
              thresholdMatrix[h_][w_]; //癸莱order matrix
        }

        if (error >= threshold) { //pr>th
          integer += delta;
          error = error - delta;
        }
        compensationData[y][x] = integer;
        max = Math.max(integer, max);

        //р计翴error data,┮矪瞶ぇ逞俱计
//        if (Debug) {
//          switch (compensationData[y][x]) {
//            case 16: //1
//              compensationData[y][x] = 64 * 16;
//              break;
//            case 32: //2
//              compensationData[y][x] = 128 * 16;
//              break;
//            case 48: //3
//              compensationData[y][x] = 255 * 16;
//              break;
//            default:
//              compensationData[y][x] = 0;
//              break;
//          }
//        }

        //╊Θabcd场, 碞琌рerror┻硂pixel  : 7 1 5 3
        floydSteinberg(abcd, error, false);
//        abcd[0] = (int) (error * 7 / 16.);
//        abcd[1] = (int) (error / 16.);
//        abcd[2] = (int) (error * 5 / 16.);
//        abcd[3] = (int) (error * 3 / 16.);

        // *a
        //dcb
        preError = (short) abcd[0];

        if (x + 1 < width) {
          nextLine[x + 1] += abcd[1];
        }
        nextLine[x] += abcd[2];
        if (x - 1 >= 0) {
          nextLine[x - 1] += abcd[3];
        }

      }
    }
//    System.out.println("wiki max data: " + max);
    return compensationData;
  }

  public static short[][] getFloydSteinbergWithOrderDithring(short[][]
      compensationData, final short[][] thresholdMatrix, int dataBit) {
    int h = compensationData.length;
    int w = compensationData[0].length;

    short delta = (short) (Math.pow(2, dataBit - 8));
    int[] abcd = new int[4];

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        short data = (short) compensationData[y][x];
        short integer = (short) (data / delta * delta); //埃计翴, 痙俱计场

        int error = data - integer; //error计翴, 碞琌璶砆┻场だ

        //╊Θabcd场, 碞琌рerror┻硂pixel  : 7 1 5 3
        floydSteinberg(abcd, error);

        compensationData[y][x] = integer; //р计翴error data,┮矪瞶ぇ逞俱计
        if (x + 1 < w) {
          compensationData[y][x + 1] += abcd[0];
        }
        if (x + 1 < w && y + 1 < h) {
          compensationData[y + 1][x + 1] += abcd[1];
        }
        if (y + 1 < h) {
          compensationData[y + 1][x] += abcd[2];
        }
        if (x - 1 >= 0 && y + 1 < h) {
          compensationData[y + 1][x - 1] += abcd[3];
        }

      }
    }

    int orderWCount = w / 4;
    int orderHCount = h / 4;
    for (int y = 0; y < orderHCount; y++) {
      for (int x = 0; x < orderWCount; x++) {
        int hBegin = y * 4;
        int wBegin = x * 4;
        for (int h_ = 0; h_ < 4; h_++) {
          for (int w_ = 0; w_ < 4; w_++) {
            int hindex = hBegin + h_;
            int windex = wBegin + w_;

            short data = compensationData[hindex][windex]; //floyd竒矪柑筁, ┮矪dataΤ俱计
            short threshold = (thresholdMatrix != null) ?
                thresholdMatrix[h_][w_] : 8; //癸莱order matrix
//            short error = errorData[hindex][windex]; //矪瞶ㄓ计场
//            short totaldata = (short) (data + error); //ㄖ俱计籔攫
            //璶穝璸衡俱计籔计, error场纗禬筁1
            short integer = (short) (data / delta * delta); //だ瞒俱计籔计
            short error = (short) (data - integer);

            //璝禬筁threshold, 碞琌 order matrix,  碞俱计+1
            if (error >= threshold) {
              integer += delta;
            }
            if (error > 0) {
              int a = 1;
            }

            compensationData[hindex][windex] = integer;
          }
        }
      }
    }
    return compensationData;

  }

  static void print(short[][] image) {
    int height = image.length;
    int width = image[0].length;
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        System.out.print(image[h][w] + " ");
      }
      System.out.println("");
    }
    System.out.println("");
  }

  public static void main(String[] args) {
//    grayLevelTest(null);
    Plot2D plot = Plot2D.getInstance();
    Dithering dithering = new Dithering();
    for (short grayLevel = 0; grayLevel <= 16; grayLevel++) {
      int height = 1920;
      int width = 1080;
//      short grayLevel = 13;
      short[][][] image = new short[3][height][width];
      for (int h = 0; h < height; h++) {
        for (int w = 0; w < width; w++) {
          image[0][h][w] = grayLevel;
          image[1][h][w] = grayLevel;
          image[2][h][w] = grayLevel;
        }
      }

      System.out.println("GrayLevel: " + grayLevel);
      dithering.getYagiDithering(image[0], true);
      double c1 = checksum(image[0]) / ( (double) height * width);
      System.out.println("yagi: " + c1);
//      print(image[0]);
//      System.out.println("");

      dithering.getFloydSteinbergWikipediaWithOrderDithring(image[1],
          thresholdMatrix);
      double c2 = checksum(image[1]) / ( (double) height * width);
      System.out.println("wiki: " + c2);
//      print(image[1]);
//      System.out.println("");

      dithering.modifiedFloydSteinberg = false;
      dithering.getFloydSteinbergWithOrderDithring(image[2], null);
      double c3 = checksum(image[2]) / ( (double) height * width);
      System.out.println("floyd: " + c3);
      dithering.modifiedFloydSteinberg = true;
//      print(image[2]);

      System.out.println("");
      plot.addCacheScatterLinePlot("yagi", grayLevel, c1);
      plot.addCacheScatterLinePlot("wiki", grayLevel, c2);
      plot.addCacheScatterLinePlot("floyd", grayLevel, c3);
    }
    plot.setVisible();
    plot.addLegend();
//dithering.getYagiWithOrderDithering(image[1], true, null);
//dithering.getYagiWithOrderDithering(image[2], true, null);

  }

  public static void outputToCC(String[] args) throws IOException {
    int height = 1080;
    int width = 1920;
    short grayLevel = 4007;
    short[][][] image = new short[3][height][width];
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        image[0][h][w] = grayLevel;
        image[1][h][w] = grayLevel;
        image[2][h][w] = grayLevel;
      }
    }
    Dithering dithering = new Dithering();
    dithering.getYagiDithering(image[0], true);
    dithering.getYagiDithering(image[1], true);
    dithering.getYagiDithering(image[2], true);

    for (int ch = 0; ch < 3; ch++) {
      for (int h = 0; h < height; h++) {
        for (int w = 0; w < width; w++) {
          image[ch][h][w] = (short) (image[ch][h][
                                     w] * 4);
        }
      }
    }

//    MuraCompensationProducer.storeCompensationImageToHexFormat(image,
//        "dithering/" + grayLevel,
//        "", 4);
  }

  public static short[][] getYagiDithering(short[][]
                                           compensationData,
                                           boolean negativeError) {
//    negativeError = false;
    int height = compensationData.length;
    int width = compensationData[0].length;

    short[][] diza = new short[height][width];
    diza[0][0] = (short) (compensationData[0][0] / 16);
    int[] abcd = new int[4];
    int max = 0;
    for (int h = 0; h < (height - 1); h++) {
      for (int w = 1; w < (width - 1); w++) {
        int data = compensationData[h][w];

        if (0 != data) {
          int a = 1;
        }
        int error = data % 16;
        if (negativeError && error >= 8) {
          diza[h][w] = (short) ( (data / 16) + 1);
          error = error - 16;
        }
        else {
          diza[h][w] = (short) (data / 16);
        }
        max = Math.max(data, max);
//        System.out.println(h + " " + w + ": " + diza[h][w]);

        if (Debug) {
          switch (diza[h][w]) {
            case 1:
              diza[h][w] = 64;
              break;
            case 2:
              diza[h][w] = 128;
              break;
            case 3:
              diza[h][w] = 255;
              break;
            default:
              diza[h][w] = 0;
          }
        }
        int ii = 0, jj = 0, tmperror = 0, threshold = 8;

        abcd[0] = (int) Math.round(error * 7. / 16);
        abcd[1] = (int) Math.round(error * 3. / 16);
        abcd[2] = (int) Math.round(error * 5. / 16);
        abcd[3] = (int) Math.round(error * 1. / 16);
//        abcd[0] = (int) (error * 7. / 16);
//        abcd[1] = (int) (error * 3. / 16);
//        abcd[2] = (int) (error * 5. / 16);
//        abcd[3] = (int) (error * 1. / 16);

        for (int c = 1; c <= 4; c++) {

          switch (c) {
            case 1:
              ii = 1;
              jj = 0;
              break;
            case 2:
              ii = -1;
              jj = 1;
              break;
            case 3:
              ii = 0;
              jj = 1;
              break;
            case 4:
              ii = 1; //width
              jj = 1; //height
              break;
          }

          short erroromomi = (short) abcd[c - 1];

          compensationData[h + jj][w + ii] += erroromomi;
//          short hoseip = compensationData[h + jj][w + ii];
//          tmperror = hoseip % 16;

          //–滁Ω舩, ㄨ璸衡琌璶秈

//          if (tmperror >= threshold) {
//            diza[h + jj][w + ii]
//                = (short) (hoseip / 16 + 1);
//          }
//          else {
//            diza[h + jj][w + ii]
//                = (short) (hoseip / 16);
//          }

        }

      }
    }
    System.out.println("yagi max data: " + max);

    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        compensationData[h][w] = (short) (diza[h][w] * 16);
      }
    }

    return compensationData;

  }

}
