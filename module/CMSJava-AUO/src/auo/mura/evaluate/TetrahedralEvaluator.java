package auo.mura.evaluate;

import java.io.*;

import auo.mura.*;
import jxl.read.biff.*;
import shu.cms.colorspace.depend.*;

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
public class TetrahedralEvaluator {
  public TetrahedralEvaluator(CorrectionData correctionData) {
    this.correctionData = correctionData;
//    integerCorrectionData = correctionData.getIntegerCorrectData(RGB.Channel.G,
//        12);
    integerCorrectionData = correctionData.getIntegerCorrectData(RGB.Channel.G,
        12, (short) 0, (short) 1023);
    grayLevelOfIntegerCorrectData = correctionData.
        getGrayLevelOfIntegerCorrectData();
    muraCompensationProducer = new MuraCompensationProducer(
        correctionData);
  }

  /**
   * [level][height][width]
   */
  private short[][][] integerCorrectionData;
  private short[] grayLevelOfIntegerCorrectData;
  private CorrectionData correctionData;

  private MuraCompensationProducer muraCompensationProducer;
  public static void main(String[] args) throws BiffException, IOException {
    String filename =
        "24inch No2/correctiondata 121x271 No2 20130227--0010(1)_data(final).csv";
    CorrectionData correctiondata = new CorrectionData(filename);
    TetrahedralEvaluator evaluator = new TetrahedralEvaluator(correctiondata);
    for (int graylevel = 0; graylevel < 1024; graylevel++) {
      evaluator.evaluateDouble(graylevel);
      evaluator.evaluateNoShrinkInteger(graylevel);
      System.out.println(evaluator.isIntegerEqualToDouble());
    }
  }

  private short[][] doubleData;
  private short[][] integerData;
  private int imageHeight = 1080;
  private int imageWidth = 1920;

  public boolean isIntegerEqualToDouble() {
    if (null == doubleData || null == integerData) {
      throw new IllegalStateException("");
    }
    for (int h = 0; h < imageHeight; h++) {
//      boolean result = Arrays.equals(doubleData[h], integerData[h]);
//      if (!result) {
//        return false;
//      }
      for (int w = 0; w < imageWidth; w++) {
        short d = doubleData[h][w];
        short i = integerData[h][w];
        if (d != i) {
          return false;
        }
      }
    }
    return true;
  }

  public short[][] evaluateDouble(int grayLevelIn10Bit) {
    muraCompensationProducer.setBlockInterpolation(true);
    doubleData = muraCompensationProducer.
        getCompensationDataFrom10BitGrayLevel(grayLevelIn10Bit);
    return doubleData;
  }

  public short[][] evaluateNoShrinkInteger(int grayLevelIn10Bit) {
    muraCompensationProducer.setBlockInterpolation(false);
    integerData = muraCompensationProducer.
        getCompensationDataFrom10BitGrayLevel(grayLevelIn10Bit);
    return integerData;

  }

  private short[] cubelut = new short[8];

  private void initCubeLut(int pixelX, int pixelY, int grayLevelIn10Bit) {

    int blockHCount = correctionData.getBlockHCount();
    int blockWCount = correctionData.getBlockWCount();
    int blockH = imageHeight / (blockHCount - 1);
    int blockW = imageWidth / (blockWCount - 1);

    int x = pixelX - 1;
    int y = pixelY - 1;
    int blockXIndex = x / blockW;
    int blockYIndex = y / blockH;
  }

  public short[][] evaluateInteger(int grayLevelIn10Bit) {

    //==========================================================================
    //變數準備
    //==========================================================================
    int blockHCount = correctionData.getBlockHCount();
    int blockWCount = correctionData.getBlockWCount();
    int blockH = imageHeight / (blockHCount - 1);
    int blockW = imageWidth / (blockWCount - 1);
    //==========================================================================

    for (int blockY = 0; blockY < blockHCount; blockY++) {
      for (int blockX = 0; blockX < blockWCount; blockX++) {
        int pixelY = 1 + blockY * blockH;
        int pixelX = 1 + blockX * blockW;
        initCubeLut(pixelX, pixelY, grayLevelIn10Bit);

        for (int hInBlock = 0; hInBlock < blockH; hInBlock++) {
          for (int wInBlock = 0; wInBlock < blockW; wInBlock++) {

          }
        }
      }
    }

//    for (int v = 0; v < imageHeight; v++) {
//      for (int w = 0; w < imageWidth; w++) {
//        int v_ = v + 1;
//        int w_ = w + 1;
//      }
//    }
//    muraCompensationProducer.setBlockInterpolation(false);
//    integerData = muraCompensationProducer.
//        getCompensationDataFrom10BitGrayLevel(grayLevelIn10Bit);
    return integerData;

  }

  short[] getC123(short[] lut, int tetrahedralIndex) {
    short c1 = 0, c2 = 0, c3 = 0;

// These are the 6 Tetrahedral
    switch (tetrahedralIndex) {
      case 1: //T1
        c1 = (short) (dens(1, 0, 0, lut) - dens(0, 0, 0, lut));
        c2 = (short) (dens(1, 1, 0, lut) - dens(1, 0, 0, lut));
        c3 = (short) (dens(1, 1, 1, lut) - dens(1, 1, 0, lut));
        break;
      case 2: //T2
        c1 = (short) (dens(1, 0, 0, lut) - dens(0, 0, 0, lut));
        c2 = (short) (dens(1, 1, 1, lut) - dens(1, 0, 1, lut));
        c3 = (short) (dens(1, 0, 1, lut) - dens(1, 0, 0, lut));
        break;
      case 3: //T3
        c1 = (short) (dens(1, 0, 1, lut) - dens(0, 0, 1, lut));
        c2 = (short) (dens(1, 1, 1, lut) - dens(1, 0, 1, lut));
        c3 = (short) (dens(0, 0, 1, lut) - dens(0, 0, 0, lut));
        break;
      case 4: //T4
        c1 = (short) (dens(1, 1, 0, lut) - dens(0, 1, 0, lut));
        c2 = (short) (dens(0, 1, 0, lut) - dens(0, 0, 0, lut));
        c3 = (short) (dens(1, 1, 1, lut) - dens(1, 1, 0, lut));
        break;
      case 5: //T5
        c1 = (short) (dens(1, 1, 1, lut) - dens(0, 1, 1, lut));
        c2 = (short) (dens(0, 1, 0, lut) - dens(0, 0, 0, lut));
        c3 = (short) (dens(0, 1, 1, lut) - dens(0, 1, 0, lut));
        break;
      case 6: //T6
        c1 = (short) (dens(1, 1, 1, lut) - dens(0, 1, 1, lut));
        c2 = (short) (dens(0, 1, 1, lut) - dens(0, 0, 1, lut));
        c3 = (short) (dens(0, 0, 1, lut) - dens(0, 0, 0, lut));
        break;
    }
    return new short[] {
        c1, c2, c3};
  }

  public void evalutefxyz() {

  }

  /**
   * 以正立方體為基準的四面體內插法
   *
   * 四面體內插法實作
   * input為存在於coordinate所形成的四方體的一個點,
   * 而lut為coordinate各點所對應的值.
   * 由input與coordinate之間的關係內插出input所應該代表的值
   *
   * @param input double[]
   * input安排方式: channel
   * @param coordinate double[]
   * coordinate安排方式: x0 y0 z0 x1 y1 z1
   * @param lut short[]
   * lut安排方式:   000 100 010 110 (第一層)| 001 101 011 111 (第二層)
   * 000=> x0 y0 z0, 100=> x1 y0 z0, 010=> x0 y1 z0...餘類推
   * @return double[]
   */
  public short cubeTetrahedralInterpolate(short[] input,
                                          short[] coordinate, short[] lut) {
    if (input.length != 3) {
      throw new IllegalArgumentException("input.length != 3");
    }

    short[] fxyz = getIntegerfxyz(input, coordinate);
    short fx = fxyz[0];
    short fy = fxyz[1];
    short fz = fxyz[2];

    int tetrahedralIndex = getTetrahedralInCube(fx, fy, fz);
    if (tetrahedralIndex == -1) {
      throw new IllegalStateException("input is not in cube.");
    }
    double[] dfxyz = getDoublefxyz(input, coordinate);
    int tetrahedralIndexDouble = getTetrahedralInCube(dfxyz[0], dfxyz[1],
        dfxyz[2]);
    if (tetrahedralIndex != tetrahedralIndexDouble) {
      throw new IllegalArgumentException("");
    }

    short[] c123 = getC123(lut, tetrahedralIndex);
    short c1 = c123[0];
    short c2 = c123[1];
    short c3 = c123[2];

    //P000+C1 * dx+C2 * dy+C3 * dz
    short output = (short) (dens(0, 0, 0, lut) + c1 * fx + c2 * fy + c3 * fz);
    return output;
  }

  private int dxyz_bit = 10;
  private int fxyz_bit = 10;
  private boolean fxyz_45 = false;
  protected final short[] getIntegerfxyz(short[] input, short[] coordinate) {
    int x0 = coordinate[0]; //10bit
    int y0 = coordinate[1];
    int z0 = coordinate[2];
    int x1 = coordinate[3];
    int y1 = coordinate[4];
    int z1 = coordinate[5];

    int px = input[0]; //10bit
    int py = input[1];
    int pz = input[2];

    int dx = px - x0;
    int dy = py - y0;
    int dz = pz - z0;

    dx <<= dxyz_bit;
    dy <<= dxyz_bit;
    dz <<= dxyz_bit;

    int fx = dx / (x1 - x0);
    int fy = dy / (y1 - y0);
    int fz = dz / (z1 - z0);

    if (dxyz_bit != fxyz_bit) {
      if (fxyz_45) {
        fx += 1;
        fy += 1;
        fz += 1;
      }
      int dbit = dxyz_bit - fxyz_bit;
      fx >>= dbit;
      fy >>= dbit;
      fz >>= dbit;
    }
    int max = getMax(fxyz_bit);
//    short ifx = fxyz_45 ? (short) Math.round(fx & 1023) : (short) (fx * max);
//    short ify = fxyz_45 ? (short) Math.round(fy * max) : (short) (fy * max);
//    short ifz = fxyz_45 ? (short) Math.round(fz * max) : (short) (fz * max);
    short ifx = (short) (fx & max);
    short ify = (short) (fy & max);
    short ifz = (short) (fz & max);
    return new short[] {
        ifx, ify, ifz};
  }

  protected final double[] getDoublefxyz(short[] input, short[] coordinate) {
    int x0 = coordinate[0]; //10bit
    int y0 = coordinate[1];
    int z0 = coordinate[2];
    int x1 = coordinate[3];
    int y1 = coordinate[4];
    int z1 = coordinate[5];

    int px = input[0]; //10bit
    int py = input[1];
    int pz = input[2];

    double dx = px - x0;
    double dy = py - y0;
    double dz = pz - z0;

    double fx = dx / (x1 - x0);
    double fy = dy / (y1 - y0);
    double fz = dz / (z1 - z0);
//  int max = getMax(fxyz_bit);
//  int ifx = fxyz_45 ? (int) Math.round(fx * max) : (int) (fx * max);
//  int ify = fxyz_45 ? (int) Math.round(fy * max) : (int) (fy * max);
//  int ifz = fxyz_45 ? (int) Math.round(fz * max) : (int) (fz * max);
    return new double[] {
        fx, fy, fz};
  }

  private int getMax(int bit) {
    int max = (int) (Math.pow(2, bit) - 1);
    return max;
  }

  /**
   *
   * @param x int
   * @param y int
   * @param z int
   * @param lut double[]
   * lut安排方式 000 100 010 110 (第一層)| 001 101 011 111 (第二層)
   * @return double
   */
  protected final static short dens(int x, int y, int z, short[] lut) {
    int index = x + (y << 1) + (z << 2);
    return lut[index];
  }

  /**
   * 判別位於六面體中的哪一個四面體
   * @param dx double
   * @param dy double
   * @param dz double
   * @return int
   */
  protected final static int getTetrahedralInCube(double dx, double dy,
                                                  double dz) {
    if (dx > 1 || dy > 1 || dz > 1) {
      return -1;
    }
    else if (dx >= dy && dy >= dz) {
      //T1
      return 1;
    }
    else if (dx >= dz && dz >= dy) {
      //T2
      return 2;
    }
    else if (dz >= dx && dx >= dy) {
      //T3
      return 3;
    }
    else if (dy >= dx && dx >= dz) {
      //T4
      return 4;
    }
    else if (dy >= dz && dz >= dx) {
      //T5
      return 5;
    }
    else if (dz >= dy && dy >= dx) {
      //T6
      return 6;
    }
    else {
      return -1;
    }
  }

}
