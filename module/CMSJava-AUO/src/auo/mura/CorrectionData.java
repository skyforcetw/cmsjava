package auo.mura;

import java.io.*;

import auo.mura.interp.*;
import jxl.read.biff.*;
import shu.cms.colorspace.depend.*;
import shu.image.*;
import shu.io.files.*;
import shu.math.lut.*;
import shu.util.*;

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
public class CorrectionData {

  public static enum Type {
    Floating, Integer1, Integer2, AUOHex
  }

  public CorrectionData(String filename) throws IOException, BiffException {
    this(filename, Type.Floating);
    this.deMuraParameter = new DeMuraParameter();
  }

  private DeMuraParameter deMuraParameter;

  public CorrectionData(String filename, DeMuraParameter deMuraParameter) throws
      FileNotFoundException, IOException {
    this.deMuraParameter = deMuraParameter;

    this.type = Type.AUOHex;
    this.filename = filename;
    dataFile = new CSVFile(filename);
    init();

  }

  public Type getType() {
    return type;
  }

  private Type type;
  public CorrectionData(String filename, Type type) throws IOException,
      BiffException {
    this.type = type;
    this.filename = filename;
    dataFile = new CSVFile(filename);
    init();
  }

  private int wblock, hblock;
  private int level;
  private double[][][][] data; //data[levelcount][rgb][h][w] = cell;
  private int[] grayLevelOfData;

  public int getGrayLevelOfData(int indexOfLevel) {
    return grayLevelOfData[indexOfLevel];
  }

  public int[] getGrayLevelOfData() {
    return grayLevelOfData;
  }

  public double getCorrectData(int indexOfLevel, RGB.Channel ch, int hOfIndex,
                               int wOfIndex) {
    return data[indexOfLevel][ch.getArrayIndex()][hOfIndex][wOfIndex];
  }

//  public final short[][] getIntegerCorrectData(int indexOfLevel, RGB.Channel ch,
//                                               int bit) {
//    short[][] integerData = new short[hblock][wblock];
//    double gain = (int) (Math.pow(2, bit - 8));
//
//    for (int y = 0; y < hblock; y++) {
//      for (int x = 0; x < wblock; x++) {
//        double doubledata = data[indexOfLevel][ch.getArrayIndex()][y][x];
//        short intdata = (short) (doubledata * gain); //無條件捨去
//        integerData[y][x] = intdata;
//      }
//    }
//    return integerData;
//  }

  private int getMax(int bit) {
    int max = (int) (Math.pow(2, bit) - 1);
    return max;
  }

  private short[] getIntegerZKeys(short lowerBound, short upperBound, int bit) {
    short max = (short) getMax(bit);
    int gain = (int) Math.pow(2, bit - 8);
    boolean addLowerLevel = lowerBound != 0;
    boolean addUpperBound = upperBound != max;
    int totalLevel = level + 2 + (addLowerLevel ? 1 : 0) +
        (addUpperBound ? 1 : 0);
    short[] zKeys = new short[totalLevel];
    int zIndex = 0;
    zKeys[zIndex++] = 0;
    if (addLowerLevel) {
      zKeys[zIndex++] = lowerBound;
    }

    for (int x = 0; x < level; zIndex++, x++) {
      int grayLevel8Bit = grayLevelOfData[x];
      zKeys[zIndex] = (short) (grayLevel8Bit * gain);
    }
    if (addUpperBound) {
      zKeys[zIndex++] = upperBound;
    }
    zKeys[zIndex++] = max;
    return zKeys;
  }

  private void setValueToCorrectData(short[][][] correctData, int index,
                                     short value) {
    int h = correctData[0].length;
    int w = correctData[0][0].length;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        correctData[index][y][x] = value;
      }
    }
  }

  private short[] grayLevelOfIntegerCorrectData;

  /**
   *
   * @param ch Channel
   * @param bit int
   * @param lowerBound short
   * @param upperBound short
   * @return short[][][]
   * @deprecated
   */
  public final short[][][] getIntegerCorrectData(RGB.Channel ch,
                                                 int bit, short lowerBound,
                                                 short upperBound) {
    grayLevelOfIntegerCorrectData = getIntegerZKeys(lowerBound, upperBound, bit);

    short max = (short) getMax(bit);
    boolean addLowerLevel = lowerBound != 0;
    boolean addUpperBound = upperBound != max;

    int levelCount = grayLevelOfIntegerCorrectData.length;
    short[][][] integerData = new short[levelCount][hblock][wblock];

    int index = 1;

    if (addLowerLevel) {
      setValueToCorrectData(integerData, index++, lowerBound);
    }

    int countOfData = grayLevelOfData.length;
    for (int level = 0; level < countOfData; level++) {
      for (int y = 0; y < hblock; y++) {
        for (int x = 0; x < wblock; x++) {
          double d = data[level][ch.getArrayIndex()][y][x];
          double normaldata = d / 255. * this.maxOutput;
          short shortdata = (short) normaldata;
          integerData[index][y][x] = shortdata;
        }

      }
      index++;
    }

    if (addUpperBound) {
      setValueToCorrectData(integerData, index++, upperBound);
    }
    setValueToCorrectData(integerData, index++, maxOutput);

    return integerData;
  }

  /**
   *
   * @param ch Channel
   * @param bit int
   * @return short[][][]
   * @deprecated
   */
  public final short[][][] getIntegerCorrectData(RGB.Channel ch,
                                                 int bit) {
    int level = grayLevelOfData.length + 2;
    short[][][] integerData = new short[level][hblock][wblock];
    double gain = (int) (Math.pow(2, bit - 8));

    for (int levelcount = 0; levelcount < grayLevelOfData.length; levelcount++) {
      for (int y = 0; y < hblock; y++) {
        for (int x = 0; x < wblock; x++) {
          double doubledata = data[levelcount][ch.getArrayIndex()][y][x];
          short intdata = (short) (doubledata * gain); //無條件捨去
          integerData[levelcount + 1][y][x] = intdata;
        }
      }
    }

    //255
    for (int y = 0; y < hblock; y++) {
      for (int x = 0; x < wblock; x++) {
//        short intdata = (short) (255 * gain); //無條件捨去
        integerData[level - 1][y][x] = maxOutput; //intdata;
      }
    }

    return integerData;
  }

  private short maxOutput = 4080;

  public double[][] getCorrectData(int indexOfLevel, RGB.Channel ch) {
    return data[indexOfLevel][ch.getArrayIndex()];
  }

  private static int setValue2KeyValue(double[] xKeys, double[] yKeys,
                                       int index, double key, double value,
                                       CubeTable.KeyValue[] keyValues) {
    for (double y : yKeys) {
      for (double x : xKeys) {

        CubeTable.KeyValue keyValue = new CubeTable.KeyValue(new double[] {x, y,
            key}, new double[] {value, value, value});
        keyValues[index++] = keyValue;
      }
    }

    return index;
  }

  public boolean isIntegerType() {
    boolean integer = type == Type.Integer2 || type == Type.Integer1 ||
        type == Type.AUOHex;
    return integer;
  }

  public CubeTable getCubeTable(int lowerBound, double upperBound,
                                int width, int height, int startIndex,
                                int inputBit, int outputBit) {
    final double[] xKeys = new double[wblock];
    final double[] yKeys = new double[hblock];
    int wpiece = (int) Math.ceil( (double) width / (wblock - 1));
    int hpiece = (int) Math.ceil( (double) height / (hblock - 1));
    for (int x = 0; x < wblock; x++) {
      xKeys[x] = wpiece * x + startIndex;
    }
    for (int x = 0; x < hblock; x++) {
      yKeys[x] = hpiece * x + startIndex;
    }

    final double max = (inputBit == 10) ? 1023 : 255;
    final double[] zKeys = getZKeys(lowerBound, upperBound, max);
    int index = 0;
    int size = zKeys.length * xKeys.length * yKeys.length;
    CubeTable.KeyValue[] keyValues = new CubeTable.KeyValue[size];

    boolean addLowerLevel = lowerBound != 0;
    boolean addUpperBound = upperBound != max;
    //0
    index = setValue2KeyValue(xKeys, yKeys, index, 0.0, 0, keyValues);
    int tableBit = (type == Type.Integer2 || type == Type.AUOHex) ? 12 : 8;
    boolean integer = isIntegerType();
    double gain = integer ? 1 : Math.pow(2, outputBit - tableBit);
    //lowerLevel
    if (addLowerLevel) {
      index = setValue2KeyValue(xKeys, yKeys, index, lowerBound * gain,
                                lowerBound, keyValues);
    }

    int level = 0;
    for (int grayLevel : grayLevelOfData) {
      int yindex = 0;
      for (double y : yKeys) {
        int xindex = 0;
        for (double x : xKeys) {

          //data[levelcount][rgb][h][w] = cell;
          double r = data[level][0][yindex][xindex];
          double g = data[level][1][yindex][xindex];
          double b = data[level][2][yindex][xindex];
          r = (int) Math.round(r * gain);
          g = (int) Math.round(g * gain);
          b = (int) Math.round(b * gain);

          CubeTable.KeyValue keyValue = new CubeTable.KeyValue(new double[] {x,
              y, grayLevel}, new double[] {r, g, b});
          keyValues[index++] = keyValue;
          xindex++;
        }
        yindex++;
      }
      level++;
    }

    //upperLevel
    if (addUpperBound) {
      index = setValue2KeyValue(xKeys, yKeys, index, upperBound,
                                upperBound * gain, keyValues);
    }

    //255
    index = setValue2KeyValue(xKeys, yKeys, index, max, maxOutput, keyValues);

    CubeTable cubeTable = new CubeTable(keyValues, xKeys, yKeys, zKeys);
    cubeTable.registerIndexFinderIF(new CubeTable.IndexFinderIF() {
      public int getIndex(double[] gridKey) {
        int xIndex = Searcher.leftBinarySearch(xKeys, gridKey[0]);
        int yIndex = Searcher.leftBinarySearch(yKeys, gridKey[1]);
        int zIndex = Searcher.leftBinarySearch(zKeys, gridKey[2]);
        int index = zIndex * xyPlaneSize + xKeys.length * yIndex + xIndex;
        return index;
      }

      private final int xyPlaneSize = xKeys.length * yKeys.length;
      public int getIndex(int[] gridKeyIndex) {
        int xIndex = gridKeyIndex[0];
        int yIndex = gridKeyIndex[1];
        int zIndex = gridKeyIndex[2];
        int index = zIndex * xyPlaneSize + xKeys.length * yIndex + xIndex;
        return index;
      }
    });
    return cubeTable;
  }

  private double[] getZKeys(int lowerBound, double upperBound, double max) {
    boolean addLowerLevel = lowerBound != 0;
    boolean addUpperBound = upperBound != max;
    int totalLevel = level + 2 + (addLowerLevel ? 1 : 0) +
        (addUpperBound ? 1 : 0);
    double[] zKeys = new double[totalLevel];
    int zIndex = 0;
    zKeys[zIndex++] = 0;
    if (addLowerLevel) {
      zKeys[zIndex++] = lowerBound;
    }

    for (int x = 0; x < level; zIndex++, x++) {
      zKeys[zIndex] = grayLevelOfData[x];
    }
    if (addUpperBound) {
      zKeys[zIndex++] = upperBound;
    }
    zKeys[zIndex++] = max;
    return zKeys;
  }

  public void storeToFlashFormat(String filename,
                                 MuraCompensationProducer
                                 muraCompensationProducer) throws
      IOException {
    byte[] header = getFlashFormatHeader(muraCompensationProducer);
    byte[] lut = getFlashFormatLUT();
    StringBuilder buf = new StringBuilder();
    for (byte b : header) {
      String hex = toHexString(b);
      buf.append(hex);
      buf.append('\n');
    }
    for (byte b : lut) {
      String hex = toHexString(b);
      buf.append(hex);
      buf.append('\n');
    }

    Writer writer = new BufferedWriter(new FileWriter(filename));
    writer.write(buf.toString());
    writer.flush();
    writer.close();
  }

  private byte[] getFlashFormatHeader(MuraCompensationProducer
                                      muraCompensationProducer) {
    byte[] header = new byte[33];
    int layerNum = this.getGrayLevelOfData().length;
    header[0] = (byte) layerNum;

    int hLutNum = hblock;
    int vLutNum = wblock;
    header[1] = (byte) (hLutNum >> 4);
    header[2] = (byte) ( ( (hLutNum & 15) << 4) + ( (vLutNum >> 8) & 15));
    header[3] = (byte) (vLutNum & 255);

    int hblocksize0 = muraCompensationProducer.imageHeight / (hblock - 1);
    int wblocksize0 = muraCompensationProducer.imageWidth / (wblock - 1);
    int hblocksize = (int) (Math.log10(hblocksize0) / 0.3) - 1;
    int wblocksize = (int) (Math.log10(wblocksize0) / 0.3) - 1;
    header[4] = (byte) ( (hblocksize << 4) + wblocksize);

    int level1 = grayLevelOfData[0] * 4;
    int level2 = grayLevelOfData[1] * 4;
    int level3 = grayLevelOfData[2] * 4;

    header[9] = (byte) (level1 >> 2);
    header[10] = (byte) ( ( (level1 & 3) << 6) + (level2 >> 4));
    header[11] = (byte) ( ( (level2 & 15) << 4) + (level3 >> 8));
    header[12] = (byte) (level3 & 255);

    ThreeDInterpolation interp = muraCompensationProducer.threeDInterpolation;
    short[] coefs = interp.getCoefs();

    header[15] = (byte) (coefs[0] >> 8);
    header[16] = (byte) (coefs[0] & 255);
    header[17] = (byte) (coefs[1] >> 8);
    header[18] = (byte) (coefs[1] & 255);
    header[19] = (byte) (coefs[2] >> 8);
    header[20] = (byte) (coefs[2] & 255);
    header[21] = (byte) (coefs[3] >> 8);
    header[22] = (byte) (coefs[3] & 255);

    int blackLimit = interp.getBlackLimitGrayLevel() * 4;
    int whiteLimit = interp.getWhiteLimitGrayLevel() * 4;
    header[23] = (byte) (blackLimit >> 4);
    header[24] = (byte) ( ( (blackLimit & 15) << 4) + ( (whiteLimit >> 8) & 15));
    header[25] = (byte) (whiteLimit & 255);

    short[] magnitude = interp.getMagnitude();
    header[27] = (byte) ( ( (magnitude[2] & 3) << 4) +
                         ( (magnitude[1] & 3) << 2) + ( (magnitude[0] & 3)));

    short[] offset = interp.getOffset();

    header[28] = (byte) ( (offset[0] >> 2) & 255);
    header[29] = (byte) ( ( (offset[0] & 3) << 6) + ( (offset[1] >> 4) & 63));
    header[30] = (byte) ( ( (offset[1] & 15) << 4) + ( (offset[2] >> 6) & 15));
    header[31] = (byte) ( (offset[2] & 63) << 2);

    return header;
  }

  private static String toHexString(byte b) {
    int b0 = b;
    if (b < 0) {
      b0 = 255 + b + 1;
    }

    if (b0 < 16) {
      return "0" + Integer.toHexString(b0);
    }
    else {
//      return Integer.toHexString(b0 >> 4) + Integer.toHexString(b0 & 15);
      return Integer.toHexString(b0);
    }

  }

  private byte[] getFlashFormatLUT() {
    int size = level * hblock * wblock;
    byte[] lut = new byte[size];
    //private double[][][][] data; //data[levelcount][rgb][h][w] = cell;
    int index = 0;
    for (int l = 0; l < this.level; l++) {
      for (int h = 0; h < this.hblock; h++) {
        for (int w = 0; w < this.wblock; w++) {
          lut[index++] = (byte) data[l][0][h][w];
        }
      }
    }
    return lut;
  }

  private void parseFloating() {
    level = (int) dataFile.getCell(1, 0);
    wblock = (int) dataFile.getCell(2, 0);
    hblock = (int) dataFile.getCell(2, 1);
    data = new double[level][3][hblock][wblock];
    grayLevelOfData = new int[level];

    int hOfChannel = 1 + hblock;
    int hOfLevel = 1 + hOfChannel * 3;
    int yOfLevelIndex = 2;

    for (int levelcount = 0; levelcount < level; levelcount++) {
      int xOfLevelIndex = 2 + levelcount * hOfLevel + 1;
      double grayLevel = dataFile.getCell(xOfLevelIndex, yOfLevelIndex);
      grayLevelOfData[levelcount] = (int) grayLevel * 4;

      for (int rgb = 0; rgb < 3; rgb++) {
        for (int h = 0; h < hblock; h++) {
          for (int w = 0; w < wblock; w++) {

            int x = 2 + levelcount * hOfLevel + rgb * hOfChannel + 2 + h;
            int y = w;
            double cell = dataFile.getCell(x, y);
            data[levelcount][rgb][h][w] = cell;
          }
        }
      }
    }

  }

  private void parseInteger2() {
    level = 3; //(int) dataFile.getCell(1, 0);
    wblock = (int) dataFile.getCell(1, 0);
    hblock = (int) dataFile.getCell(1, 1);
    data = new double[level][3][hblock][wblock];
    grayLevelOfData = new int[level];

    int hOfLevel = 1 + hblock;
    int yOfLevelIndex = 1;

    for (int levelcount = 0; levelcount < level; levelcount++) {
      int xOfLevelIndex = 2 + levelcount * hOfLevel;
      double grayLevel = dataFile.getCell(xOfLevelIndex, yOfLevelIndex);
      grayLevelOfData[levelcount] = (int) grayLevel;

      for (int h = 0; h < hblock; h++) {
        for (int w = 0; w < wblock; w++) {

          int x = 3 + levelcount * hOfLevel + h;
          int y = w;
          double cell = dataFile.getCell(x, y);
          data[levelcount][0][h][w] = cell;
        }
      }

      for (int ch = 1; ch <= 2; ch++) {
        for (int h = 0; h < hblock; h++) {
          System.arraycopy(data[levelcount][0][h], 0, data[levelcount][ch][h],
                           0, wblock);
        }
      }

    }

  }

  private void parseInteger1() {
    level = 3; //(int) dataFile.getCell(1, 0);
    wblock = (int) dataFile.getCell(0, 1);
    hblock = (int) dataFile.getCell(0, 2);
    data = new double[level][3][hblock][wblock];
    grayLevelOfData = new int[] {
        100, 204, 502};

    int hOfLevel = hblock;

    for (int levelcount = 0; levelcount < level; levelcount++) {
      for (int h = 0; h < hblock; h++) {
        for (int w = 0; w < wblock; w++) {
          int x = 1 + levelcount * hOfLevel + h;
          int y = w;
          double cell = dataFile.getCell(x, y);
          data[levelcount][0][h][w] = cell;
        }
      }
    }

    for (int ch = 1; ch <= 2; ch++) {
      for (int levelcount = 0; levelcount < level; levelcount++) {
        for (int h = 0; h < hblock; h++) {
          System.arraycopy(data[levelcount][0][h], 0,
                           data[levelcount][ch][h],
                           0, wblock);
        }
      }

    }

  }

  private void parseAUOHex() {
    level = 3; //deMuraParameter.layerNumber;
    wblock = deMuraParameter.hLutNumber;
    hblock = deMuraParameter.vLutNumber;
    data = new double[level][3][hblock][wblock];
//    grayLevelOfData = new int[level];
    grayLevelOfData = new int[] {
        deMuraParameter.planeLevel1, deMuraParameter.planeLevel2,
        deMuraParameter.planeLevel3};

    int yindex = 0;

    for (int levelcount = 0; levelcount < level; levelcount++) {

      for (int h = 0; h < hblock; h++) {
        for (int w = 0; w < wblock; w++) {
          String cellString = dataFile.getCellAsString(yindex++, 0);
          byte b = UnsignedByte.valueOf(cellString, 16);
//          int b = Integer.valueOf(cellString, 16);

          data[levelcount][0][h][w] = b;
        }
      }

    }

    for (int ch = 1; ch <= 2; ch++) {
      for (int levelcount = 0; levelcount < level; levelcount++) {
        for (int h = 0; h < hblock; h++) {
          System.arraycopy(data[levelcount][0][h], 0, data[levelcount][ch][h],
                           0, wblock);
        }
      }
    }

  }

  private void init() {
    if (type == Type.Floating) {
      parseFloating();
    }
    else if (type == Type.Integer2) {
      parseInteger2();
    }
    else if (type == Type.Integer1) {
      parseInteger1();
    }
    else if (type == Type.AUOHex) {
      parseAUOHex();
    }

  }

  public int getBlockWCount() {
    return wblock;
  }

  public int getBlockHCount() {
    return hblock;
  }

  private FileExtractIF dataFile;
  private String filename;

  public static void produceDeMuraImage(int grayLevel) throws
      IOException,
      BiffException {
    long start = System.currentTimeMillis();
//    String correctFilename ="24inch No2/correctiondata 121x271 No2 20130227--0010(1)_data(final).csv";
//    CorrectionData correctiondata = new CorrectionData(correctFilename,CorrectionData.Type.Floating);

//    String correctFilename = "24inch No2/MuraData Mode1 Type1.csv";
    String correctFilename = "24inch No2/複製 -MuraData Mode1 Type1.csv";

    CorrectionData correctiondata = new CorrectionData(correctFilename,
        CorrectionData.Type.Integer1);

//    String correctFilename = "24inch No2/Sample CorrectionData(12bit).csv";
//    CorrectionData correctiondata = new CorrectionData(correctFilename,
//        CorrectionData.Type.Integer2);

    int imageHeight = 1080;
    int imageWidth = 1920;
    IntegerImage image = new IntegerImage(imageWidth, imageHeight);

    int startIndex = 1;
    CubeTable cubeTable = correctiondata.getCubeTable(0, 255, imageWidth,
        imageHeight,
        startIndex, 0, 12);
    cubeTable.setLeftNearSearch(false);

    TetrahedralInterpolation interp = new TetrahedralInterpolation(cubeTable, false);
    interp.setValueCounts(1);
//    BlockInterpolation3DIF block = interp.getBlockInterpolation3DIFInstance();
//    interp.registerBlockInterpolation3DIF(block);

    System.out.println("read: " + (System.currentTimeMillis() - start));

    int blockHCount = correctiondata.getBlockHCount();
    int blockWCount = correctiondata.getBlockWCount();
    int blockH = imageHeight / (blockHCount - 1);
    int blockW = imageWidth / (blockWCount - 1);
    boolean showDetail = false;
    int checksum = 0;

    int[][] result = new int[imageHeight][imageWidth];

    for (int h = 0; h < blockHCount - 1; h++) {
      for (int w = 0; w < blockWCount - 1; w++) {
//        int x = h * blockH + 1;
//        int y = w * blockW + 1;

//        block.registerInterpolateCell(new double[] {y, x, grayLevel});
        for (int hp = 0; hp < blockH; hp++) {
          for (int wp = 0; wp < blockW; wp++) {

            int hp_ = hp + h * blockH + 1;
            int wp_ = wp + w * blockW + 1;
            double[] v = interp.getValues(new double[] {wp_, hp_, grayLevel});
            int integerValue = (int) v[0];
            checksum += integerValue;
            result[hp_ - 1][wp_ - 1] = integerValue;

            image.setPixel(wp_ - 1, hp_ - 1, new double[] {v[0], v[0], v[0]});
            if (!showDetail) {
              System.out.println(h + " " + w + " / " + hp + " " + wp + ": " +
                                 +integerValue + "/\t" + v[0]);
            }
          }
        }
        if (w == 1) {
          showDetail = true;
          ( (TetrahedralInterpolation.Interpolation3D) interp.
           getInterpolation()).
              showCellChange(false);
        }

      }
    }
    System.out.println("checksum : " + checksum);

//    System.out.println("All Cal: " + (System.currentTimeMillis() - start));
//    System.out.println("cell: " + interp.cellTime);
//    System.out.println("interp:" + interp.interpTime);
    ImageUtils.storeTIFFImage("demura/demura-" + grayLevel + ".tif",
                              image.getBufferedImage());

  }

  public void convertFloatingToIntegerType() {
    if (type != Type.Floating) {
      throw new IllegalStateException();
    }

    this.type = Type.Integer2;
  }

  public static void main(String[] args) throws BiffException, IOException {
//    CorrectionData correctiondata = new CorrectionData(
//        "24inch No2/Sample CorrectionData(12bit).csv", Type.Integer);

//    String filename =
//        "24inch No2/correctiondata 121x271 No2 20130227--0010(1)_data(final).csv"; //floating
//    produceDeMuraImage(25);
  }

  public int getLevel() {
    return level;
  }

  /**
   *
   * @return short[]
   * @deprecated
   */
  public short[] getGrayLevelOfIntegerCorrectData() {
    return grayLevelOfIntegerCorrectData;
  }

  public DeMuraParameter getDeMuraParameter() {
    return deMuraParameter;
  }

}
