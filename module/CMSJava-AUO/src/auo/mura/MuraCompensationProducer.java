package auo.mura;

import java.io.*;

import java.awt.image.*;

import auo.cms.frc.*;
import auo.mura.interp.*;
import shu.image.*;
import shu.math.lut.*;
import shu.math.lut.BlockInterpolation3DIF;

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
public class MuraCompensationProducer {
  private CorrectionData correctionData;
  public MuraCompensationProducer(CorrectionData correctionData) {
    this.correctionData = correctionData;
    muraImageUtils = new MuraImageUtils(dataBit, imageWidth, imageHeight);
  }

  public MuraImageUtils muraImageUtils;

  private Dithering dithering = new Dithering();

  private int startIndex = 0;
  private boolean blockInterpolation = false;
  private int lowerBound = 0;
  private double upperBound = 1023;
  int imageWidth = 1920;
  int imageHeight = 1080;
  private int dataBit = 12;
  private int compensationDataCheckSum;
  private long ditheringCheckSum;
  private int blackLimitGrayLevel = 0;
  private int whiteLimitGrayLevel = 1023;
  public final int getCompensationDataCheckSum() {
    return compensationDataCheckSum;
  }

  public MuraImageUtils getMuraImageUtils() {
    return muraImageUtils;
  }

  public long getDitheringCheckSum() {
    return ditheringCheckSum;
  }

  private CubeTable cubeTable;
  ThreeDInterpolation threeDInterpolation;
  private boolean use3DInterpolation = true;
  private boolean storeDeMuraData = false;

  public void init3DInterpolation() {
//    if (Constant.CompareWithYagi) {
//      int blockHeight = imageHeight /
//          (this.correctionData.getBlockHCount() - 1);
//      int blockWidth = imageWidth / (this.correctionData.getBlockWCount() - 1);

//      threeDInterpolation = new ThreeDInterpolation(cubeTable,
//          blackLimitGrayLevel, whiteLimitGrayLevel);
//      threeDInterpolation.setBlockSize(blockHeight, blockWidth);

//    }
//    else {
    threeDInterpolation = new ThreeDInterpolation(cubeTable,
                                                  correctionData.
                                                  getDeMuraParameter());
//    }

  }

  protected final short[][][] getCompensationData(short[][][] imageData) {
    if (null == cubeTable) {

      cubeTable = correctionData.getCubeTable(lowerBound, upperBound,
                                              imageWidth, imageHeight,
                                              startIndex, inputBit, dataBit);

    }
    if (blockInterpolation || startIndex == 0) {
      cubeTable.setLeftNearSearch(false);
    }

    //==========================================================================
    //內插預備
    //==========================================================================
    if (use3DInterpolation) {

      init3DInterpolation();

      ThreeDInterpolation.YagiTriLinearInterpolation3D triLinearInterpolation3D =
          threeDInterpolation.new YagiTriLinearInterpolation3D();
      threeDInterpolation.registerInterpolation3DIF(triLinearInterpolation3D);

      DeMuraDebugger debug = null;
      if (storeDeMuraData) {
        debug = new DeMuraDebugger("demura/deMura12bit.xls");
      }

      if (SingleChannelProcess) {
        getCompensationData(imageData[0], threeDInterpolation);

        copy(imageData[0], imageData[1]);
        copy(imageData[0], imageData[2]);

      }
      else {
        getCompensationData(imageData[0], threeDInterpolation);

        int row = storeDeMuraData ? debug.storeDeMuraData(0, deMuraData) : 0;

        getCompensationData(imageData[1], threeDInterpolation);
        row = storeDeMuraData ? debug.storeDeMuraData(row, deMuraData) : 0;

        getCompensationData(imageData[2], threeDInterpolation);
        row = storeDeMuraData ? debug.storeDeMuraData(row, deMuraData) : 0;
        if (storeDeMuraData) {
          debug.close();
        }

      }

    }
    else {
      TetrahedralInterpolation interp = new TetrahedralInterpolation(cubeTable, false);

      DeMuraDebugger debug = null;
      if (storeDeMuraData) {
        debug = new DeMuraDebugger("demura/deMura12bit.xls");
      }

      if (SingleChannelProcess) {
        interp.setValueCounts(1);
        getCompensationData(imageData[0], interp);

        copy(imageData[0], imageData[1]);
        copy(imageData[0], imageData[2]);
      }
      else {

        getCompensationData(imageData[0], interp);

        int row = storeDeMuraData ? debug.storeDeMuraData(0, deMuraData) : 0;

        getCompensationData(imageData[1], interp);
        row = storeDeMuraData ? debug.storeDeMuraData(row, deMuraData) : 0;

        getCompensationData(imageData[2], interp);
        row = storeDeMuraData ? debug.storeDeMuraData(row, deMuraData) : 0;
        if (storeDeMuraData) {
          debug.close();
        }

      }
    }
    return imageData;
  }

//  private int storeDeMuraData(ExcelFile excel, int row, short[][] deMuraData) throws
//      WriteException {
//    if (!storeDeMuraData) {
//      return -1;
//    }
//    int height = deMuraData.length;
//    int width = deMuraData[0].length;
//
//    for (int h = 0; h < height; h++) {
//      int rowIndex = row + h;
//      for (int w = 0; w < width; w++) {
//        short v = deMuraData[h][w];
//        excel.setCell(w, rowIndex, v);
//      }
//    }
//    return row + height + 1;
//  }

  private static void copy(short[][] source, short[][] dest) {
    int height = source.length;
    int size = source[0].length;
    for (int h = 0; h < height; h++) {
      System.arraycopy(source[h], 0, dest[h], 0, size);
    }
  }

  private static void copy(short[][] source, int[][] dest) {
    int height = source.length;
    int width = source[0].length;

//    int size = source[0].length;
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        dest[h][w] = source[h][w];
      }
//      System.arraycopy(source[h], 0, dest[h], 0, size);
    }
  }

  static int[][][] copyToInt(short[][][] data) {
    int d1 = data.length;
    int d2 = data[0].length;
    int d3 = data[0][0].length;
    int[][][] result = new int[d1][d2][d3];
    for (int x = 0; x < d1; x++) {
      copy(data[x], result[x]);
    }
    return result;

  }

  static short[][][] copy(short[][][] data) {
    int d1 = data.length;
    int d2 = data[0].length;
    int d3 = data[0][0].length;
    short[][][] result = new short[d1][d2][d3];
    for (int x = 0; x < d1; x++) {
      copy(data[x], result[x]);
    }
    return result;
  }

  static short[][] copy(short[][] data) {
    int d1 = data.length;
    int d2 = data[0].length;
    short[][] result = new short[d1][d2];
    copy(data, result);
    return result;
  }

  private static void convert12BitTo16Bit(int[][][] data) {
    int d1 = data.length;
    int d2 = data[0].length;
    int d3 = data[0][0].length;
    int gain = (int) Math.pow(2, 16 - 12);
    for (int ch = 0; ch < d1; ch++) {
      for (int y = 0; y < d2; y++) {
        for (int x = 0; x < d3; x++) {
          data[ch][y][x] *= gain;
        }
      }
    }
  }

  private static void convert10BitTo12Bit(short[][][] data) {
    int d1 = data.length;
    int d2 = data[0].length;
    int d3 = data[0][0].length;
    int gain = (int) Math.pow(2, 12 - 10);
    for (int ch = 0; ch < d1; ch++) {
      for (int y = 0; y < d2; y++) {
        for (int x = 0; x < d3; x++) {
          data[ch][y][x] *= gain;
        }
      }
    }
  }

  private static void convert8BitTo10Bit(short[][][] data) {
    int d1 = data.length;
    int d2 = data[0].length;
    int d3 = data[0][0].length;
    int gain = (int) Math.pow(2, 10 - 8);
    for (int ch = 0; ch < d1; ch++) {
      for (int y = 0; y < d2; y++) {
        for (int x = 0; x < d3; x++) {
          data[ch][y][x] *= gain;
        }
      }
    }
  }

  private static String fillZero(String hex, int fill) {
    int fillCount = fill - hex.length();
    String result = hex;
    if (fillCount > 0) {

      for (int x = 0; x < fillCount; x++) {
        result = "0" + result;
      }
    }
    return result;
  }

  public final void store12BitCompensationImageToHexFormat(String dirname,
      int port) throws
      IOException {
    storeCompensationImageToHexFormat(compensationImage12Bit, dirname, "",
                                      port);
  }

  public final void store12BitCompensationImageDGToHexFormat(String dirname,
      int port) throws
      IOException {
    storeCompensationImageToHexFormat(compensationImage12Bit_DG, dirname,
                                      "dg_",
                                      port);
  }

  public final void store10BitCompensationImageFRCToHexFormat(String dirname,
      int port) throws
      IOException {
    for (int x = 0; x < 4; x++) {
      storeCompensationImageToHexFormat(frcImage10Bit[x], dirname,
                                        "frc" + (x + 1) + "_", port);
    }

  }

  public final void storeDitheringImageToHexFormat(String dirname, int port) throws
      IOException {
    if (null == ditheringImage8Bit) {
      throw new IllegalStateException("");
    }
    if (null == ditheringImage10Bit) {
      //雖然dithering結果為8bit, 但是最終輸出為10bit, 因此需要升轉到10bit再儲存
      ditheringImage10Bit = copy(ditheringImage8Bit);
      convert8BitTo10Bit(ditheringImage10Bit);
    }
    storeCompensationImageToHexFormat(ditheringImage10Bit, dirname,
                                      "dithering", port);
  }

//  private void ch

//  public final static void store8BitImageToHexFormat(short[][][] image,String dirname, int port) throws
//      IOException {
//    if (null == image) {
//      throw new IllegalStateException("");
//    }
////    if (null == ditheringImage8Bit) {
//    short[][][]  result = copy(image);
//      int channel = result.length;
//      int height = result[0].length;
//      int width = result[0][0].length;
//      for (int ch = 0; ch < channel; ch++) {
//        for (int h = 0; h < height; h++) {
//          for (int w = 0; w < width; w++) {
//            result[ch][h][w] = (short) (result[ch][h][
//                w] * 4);
//          }
//        }
//      }
////    }
//    storeCompensationImageToHexFormat(ditheringImage8Bit, dirname,
//                                      "dithering", port);
//  }

  public final static void storeDecFullFrame(short[][] imageData,
                                             String filename,
                                             int shrinkBit) throws
      IOException {
    int width = imageData[0].length; /// port;
    int height = imageData.length;
    Writer frameR = new BufferedWriter(new FileWriter(filename));
    double base = Math.pow(2, shrinkBit);

    for (int h = 0; h < height; h++) { //高
      for (int w = 0; w < width; w++) { //寬
        short value = (short) (imageData[h][w] / base);
        String dec = Integer.toString(value);
//        String hex = Integer.toHexString(value);
        dec = fillZero(dec, 3);
        frameR.write(dec);
        frameR.write(" ");
      }
      frameR.write("\r\n");
    }
    frameR.flush();
    frameR.close();
  }

  private boolean store12BitFullFrame = false;
  private boolean store8BitFullFrame = false;
  public final void storeCompensationImageToHexFormat(short[][][]
      imageData, String dirname, String prefix,
      int port) throws
      IOException {
    if (null == imageData) {
      return;
    }

    int width = imageData[0][0].length; /// port;
    int height = imageData[0].length;
    Writer[] writerR = new Writer[port];
    Writer[] writerG = new Writer[port];
    Writer[] writerB = new Writer[port];

    String fileprefix = prefix.length() != 0 ?
        dirname + "/" + prefix :
        dirname + "/";
    if (store8BitFullFrame) {
      storeDecFullFrame(imageData[0], fileprefix + "frameR_8.txt", 4);
    }
    if (store12BitFullFrame) {
      storeDecFullFrame(imageData[0], fileprefix + "frameR_12.txt", 0);
    }
    for (int p = 0; p < port; p++) {
      writerR[p] = new BufferedWriter(new FileWriter(fileprefix + "r" + (p + 1) +
          ".txt"));
      writerG[p] = new BufferedWriter(new FileWriter(fileprefix + "g" + (p + 1) +
          ".txt"));
      writerB[p] = new BufferedWriter(new FileWriter(fileprefix + "b" + (p + 1) +
          ".txt"));
    }

    for (int h = 0; h < height; h++) { //高
      for (int w = 0; w < width; w++) { //寬

        int p = w % port;

        short value = imageData[0][h][w];
        String hex = Integer.toHexString(value);
        hex = fillZero(hex, 3);

        writerR[p].write(hex);
        writerR[p].write("\r\n");

        value = imageData[1][h][w];
        hex = Integer.toHexString(value);
        hex = fillZero(hex, 3);
        writerG[p].write(hex);
        writerG[p].write("\r\n");

        value = imageData[2][h][w];
        hex = Integer.toHexString(value);
        hex = fillZero(hex, 3);
        writerB[p].write(hex);
        writerB[p].write("\r\n");

      }
    }

    for (int p = 0; p < port; p++) {
      writerR[p].flush();
      writerR[p].close();
      writerG[p].flush();
      writerG[p].close();
      writerB[p].flush();
      writerB[p].close();
    }

  }

  public final void produceCompensationImage(String inputImageFilename,
                                             String image16bitFilename,
                                             String image8bitFilename) throws
      IOException {

    BufferedImage image = ImageUtils.loadImage(inputImageFilename);
    int w = image.getWidth();
    int h = image.getHeight();
    this.setImageResolution(w, h);

    short[][][] inputImage10BitData = muraImageUtils.getImageData(image, true);
//    short[] check = new short[1080];
//    for (int x = 0; x < 1080; x++) {
//      check[x] = inputImage10BitData[0][x][0];
//    }
    produceCompensationImage(inputImage10BitData, image16bitFilename,
                             image8bitFilename);
  }

  private short[][][] inputImage10BitData;
  private short[][][] compensationImage12Bit;
  private short[][][] compensationImage12Bit_DG;
  private short[][][] ditheringImage8Bit;
  private short[][][] ditheringImage10Bit;
  private short[][][][] frcImage8Bit;
  private short[][][][] frcImage10Bit;
  private void produceDitheringImage(String image8bitFilename) throws
      IOException {
    if (SingleChannelProcess) {
      //12bit dithering成8bit
      getDitheringCompensationData(ditheringImage8Bit[0]);
      copy(ditheringImage8Bit[0], ditheringImage8Bit[1]);
      copy(ditheringImage8Bit[0], ditheringImage8Bit[2]);
    }
    else {
      getDitheringCompensationData(ditheringImage8Bit[0]);
      getDitheringCompensationData(ditheringImage8Bit[1]);
      getDitheringCompensationData(ditheringImage8Bit[2]);
    }
//存8bit
    BufferedImage image = muraImageUtils.getBufferedImage(ditheringImage8Bit);
    ImageUtils.storeTIFFImage(image8bitFilename, image);
  }

  private AUOFRC frc;
  private short[][][][] produceFRCImage(short[][][] compensationImage12Bit) {
    if (null == frc) {
      try {
        frc = new AUOFRC("frc/auofrc.csv");
      }
      catch (FileNotFoundException ex) {
        ex.printStackTrace();
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    //frame, h ,w
    short[][][] r = frc.frc8bit(compensationImage12Bit[0]);
    short[][][] g = frc.frc8bit(compensationImage12Bit[1]);
    short[][][] b = frc.frc8bit(compensationImage12Bit[2]);
    short[][][][] rgb = new short[][][][] {
        r, g, b};

    int height = compensationImage12Bit[0].length;
    int width = compensationImage12Bit[0][0].length;

//frame, ch, h, w
    short[][][][] result = new short[4][3][height][width];
    for (int frame = 0; frame < 4; frame++) {
      for (int ch = 0; ch < 3; ch++) {
        copy(rgb[ch][frame], result[frame][ch]);
      }
    }
    return result;
  }

  private void produceDG(short[][][] image12BitData, DigitalGamma dg) {
    int height = image12BitData[0].length;
    int width = image12BitData[0][0].length;
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        image12BitData[0][h][w] = (short) dg.gammaR12Bit(image12BitData[0][h][w]);
        image12BitData[1][h][w] = (short) dg.gammaG12Bit(image12BitData[1][h][w]);
        image12BitData[2][h][w] = (short) dg.gammaB12Bit(image12BitData[2][h][w]);
      }
    }
  }

  private boolean store16BitImage = true;
  private boolean store8BitDitheringImage = true;
  private boolean store8BitFRCData = true;

  public final void produceCompensationImage(short[][][] inputImage10BitData,
                                             String image16bitFilename,
                                             String image8bitFilename) throws
      FileNotFoundException, IOException {
//    SingleChannelProcess = false;
    this.inputImage10BitData = inputImage10BitData;
    if (DMC) {
      compensationImage12Bit = getCompensationData(copy(inputImage10BitData));
    }
    else {
      compensationImage12Bit = copy(inputImage10BitData);
      convert10BitTo12Bit(compensationImage12Bit);
    }

    if (DG) {
      compensationImage12Bit_DG = copy(compensationImage12Bit);
      DigitalGamma dg = new DigitalGamma();
      produceDG(compensationImage12Bit_DG, dg);
    }
    ditheringImage8Bit = copy(compensationImage12Bit);
    if (store8BitFRCData) {
      //frame, ch, h, w
      frcImage8Bit = produceFRCImage(compensationImage12Bit);
      frcImage10Bit = new short[4][][][];
      for (int frame = 0; frame < 4; frame++) {
        frcImage10Bit[frame] = copy(frcImage8Bit[frame]);
        convert8BitTo10Bit(frcImage10Bit[frame]);
      }

    }
    if (store16BitImage) {
      //store 16bit
      int[][][] compensationData16Bit = copyToInt(compensationImage12Bit);
      convert12BitTo16Bit(compensationData16Bit);
      //把12bit存成16bit
      muraImageUtils.store16BitImage(compensationData16Bit, image16bitFilename);
    }
//    muraImageUtils.getBufferedImage()


    //==========================================================================
    //8bit
    //==========================================================================
//    boolean storePure8Bit = false;
//    if (storePure8Bit) {
//      short[][][] storeImage8BitData = copy(compensationData16Bit);
//      BufferedImage storeImage8bit = new MuraImageUtils(16, imageWidth,
//          imageHeight).getBufferedImage(
//              storeImage8BitData);
//      int dotind = image8bitFilename.lastIndexOf(".");
//      String name = image8bitFilename.substring(0, dotind) + "(pure)" +
//          image8bitFilename.substring(dotind, image8bitFilename.length());
//      ImageUtils.storeBMPFImage(name, storeImage8bit);
//    }
    //==========================================================================

    if (store8BitDitheringImage) {
      produceDitheringImage(image8bitFilename);
    }
  }

  public final void produceCompensationImage(final int rGrayLevel,
                                             final int gGrayLevel,
                                             final int bGrayLevel,
                                             String image16bitFilename,
                                             String image8bitFilename) throws
      FileNotFoundException, IOException {
    if (rGrayLevel == gGrayLevel && gGrayLevel == bGrayLevel) {
      SingleChannelProcess = true;
    }
    else {
      SingleChannelProcess = false;
    }

    short[][][] imageData = muraImageUtils.getPlaneImageData(rGrayLevel,
        gGrayLevel, bGrayLevel);

    produceCompensationImage(imageData, image16bitFilename, image8bitFilename);

  }

  public final BufferedImage getCompensationImage(int rGrayLevel,
                                                  int gGrayLevel,
                                                  int bGrayLevel) {
    short[][][] compensationData = getCompensationData(rGrayLevel, gGrayLevel,
        bGrayLevel);
    getDitheringCompensationData(compensationData[0]);
    getDitheringCompensationData(compensationData[1]);
    getDitheringCompensationData(compensationData[2]);
    return muraImageUtils.getBufferedImage(compensationData);
  }

  public final short[][][] getCompensationData(int rGrayLevel, int gGrayLevel,
                                               int bGrayLevel) {

    short[][][] imageData = muraImageUtils.getPlaneImageData(rGrayLevel,
        gGrayLevel,
        bGrayLevel);
    return getCompensationData(imageData);

  }

  public static boolean SingleChannelProcess = false;

  protected final short[][] getCompensationData(short[][] imageData,
                                                TetrahedralInterpolation interp) {

    //==========================================================================
    //變數準備
    //==========================================================================
    int blockHCount = correctionData.getBlockHCount();
    int blockWCount = correctionData.getBlockWCount();
    int blockH = imageHeight / (blockHCount - 1);
    int blockW = imageWidth / (blockWCount - 1);
    //==========================================================================
    compensationDataCheckSum = 0;

    for (int h = 0; h < blockHCount - 1; h++) {
      for (int w = 0; w < blockWCount - 1; w++) {

        for (int hp = 0; hp < blockH; hp++) {
          for (int wp = 0; wp < blockW; wp++) {

            int hp_ = hp + h * blockH + 1;
            int wp_ = wp + w * blockW + 1;
            short grayLevel = imageData[hp_ - 1][wp_ - 1];
            double[] v = interp.getValues(new double[] {wp_, hp_, grayLevel});
            short integerValue = (short) v[0];
            compensationDataCheckSum += integerValue;
            imageData[hp_ - 1][wp_ - 1] = integerValue;

          }
        }

      }
    }

    return imageData;
  }

  private short[][] deMuraData;
  public static boolean HWCheck = false;
  public static int HWCheck_WP;
  public static int HWCheck_GrayLevel;
  protected final short[][] getCompensationData(short[][] imageData,
                                                ThreeDInterpolation interp) {

    //==========================================================================
    //變數準備
    //==========================================================================
    int blockHCount = correctionData.getBlockHCount();
    int blockWCount = correctionData.getBlockWCount();
    int blockH = (int) Math.ceil( (double) imageHeight / (blockHCount - 1));
    int blockW = imageWidth / (blockWCount - 1);
    //==========================================================================
    compensationDataCheckSum = 0;
    boolean integer = correctionData.isIntegerType();
    deMuraData = new short[imageHeight][imageWidth];

    for (int h = 0; h < blockHCount; h++) {
      for (int w = 0; w < blockWCount; w++) {

        if (blockInterpolation) {
        }

        for (int hp = 0; hp < blockH; hp++) {
          for (int wp = 0; wp < blockW; wp++) {

            short hp_ = (short) (hp + h * blockH);
            short wp_ = (short) (wp + w * blockW);

            if (hp_ >= imageHeight || wp_ >= imageWidth) {
              continue;
            }
            short grayLevel = imageData[hp_][wp_];

            if (wp_ >= 1900 && hp_ == 1079 && wp_ % 2 == 0) {
              HWCheck_WP = wp_;
              HWCheck_GrayLevel = grayLevel;
              HWCheck = true;
            }
            else {
              HWCheck = false;
            }

            short[] v = interp.getValues(new short[] {wp_, hp_, grayLevel});
            short interpValue = v[0];
            deMuraData[hp_][wp_] = interpValue;
            short integerValue = integer ?
                (short) (grayLevel * 4 + interpValue) : interpValue;
            integerValue = (integerValue < 0) ? 0 : integerValue;
            integerValue = (integerValue > 4095) ? 4095 : integerValue;
            compensationDataCheckSum += integerValue;

            imageData[hp_][wp_] = integerValue;
          }
        }

      }
    }

    return imageData;
  }

  /**
   *
   * @param grayLevelIn10Bit int
   * @return short[][]
   * @deprecated
   */
  public final short[][] getCompensationDataFrom10BitGrayLevel(int
      grayLevelIn10Bit) {
    double grayLevel = grayLevelIn10Bit / 4.;
    return getCompensationData(grayLevel);
  }

  private int inputBit = 10;

  /**
   *
   * @param grayLevel double
   * @return short[][]
   * @deprecated
   */
  public final short[][] getCompensationData(double grayLevel) {
    CubeTable cubeTable = correctionData.getCubeTable(lowerBound, upperBound,
        imageWidth, imageHeight,
        startIndex, inputBit, dataBit);
    if (blockInterpolation) {
      cubeTable.setLeftNearSearch(false);
    }
    //==========================================================================
    //內插預備
    //==========================================================================
    TetrahedralInterpolation interp = new TetrahedralInterpolation(cubeTable, false);
    interp.setValueCounts(1);

    BlockInterpolation3DIF block = null;
    if (blockInterpolation) {
      block = interp.getBlockInterpolation3DIFInstance();
      interp.registerBlockInterpolation3DIF(block);
    }
    //==========================================================================
    //變數準備
    //==========================================================================
    int blockHCount = correctionData.getBlockHCount();
    int blockWCount = correctionData.getBlockWCount();
    int blockH = imageHeight / (blockHCount - 1);
    int blockW = imageWidth / (blockWCount - 1);
    //==========================================================================
    compensationDataCheckSum = 0;
    short[][] result = new short[imageHeight][imageWidth];

    for (int h = 0; h < blockHCount - 1; h++) {
      for (int w = 0; w < blockWCount - 1; w++) {
        int x = h * blockH + 1;
        int y = w * blockW + 1;
        if (blockInterpolation) {
          block.registerInterpolateCell(new double[] {y, x, grayLevel});
        }
        for (int hp = 0; hp < blockH; hp++) {
          for (int wp = 0; wp < blockW; wp++) {

            int hp_ = hp + h * blockH + 1;
            int wp_ = wp + w * blockW + 1;
            double[] v = interp.getValues(new double[] {wp_, hp_, grayLevel});
            short integerValue = (short) v[0];
            compensationDataCheckSum += integerValue;
            result[hp_ - 1][wp_ - 1] = integerValue;

          }
        }

      }
    }

    return result;
  }

  public void setImageResolution(int width, int height) {
    this.imageWidth = width;
    this.imageHeight = height;
    muraImageUtils = new MuraImageUtils(dataBit, imageWidth, imageHeight);
  }

  public void setBound(int lowerBound, int upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

//  public final boolean equalToBlockInterpolation() {
//    return false;
//  }



  public short[][] getDitheringCompensationData(short[][] compensationData) {
    short[][] result = null;
    switch (ditheringType) {
      case Order:
        result = dithering.getOrderDithring(compensationData);
        break;
      case FloydSteinberg:
        result = dithering.getFloydSteinbergWithOrderDithring(compensationData, null);
        break;
      case FloydSteinberg_Order:
        result = dithering.getFloydSteinbergWithOrderDithring(compensationData);
        break;
      case Yagi:
        result = dithering.getYagiDithering(compensationData, true);
        break;
      case Yagi_Order:
        result = dithering.getYagiDithering(compensationData, true);
        break;
      case FloydSteinberg_Wiki:
        result = dithering.getFloydSteinbergWikipediaWithOrderDithring(
            compensationData, null);
        break;
      case FloydSteinberg_Wiki_Order:
        result = dithering.getFloydSteinbergWikipediaWithOrderDithring(
            compensationData);
        break;
      case FloydSteinberg_Wiki_LineBased:
        result = dithering.
            getFloydSteinbergWikipediaWithOrderDithring_LineBased(
                compensationData, null);
        break;
    }
    ditheringCheckSum = 0;
    for (int h = 0; h < compensationData.length; h++) {
      for (int w = 0; w < compensationData[0].length; w++) {
        ditheringCheckSum += compensationData[h][w];
      }
    }

    return result;
  }

  public void setDataBit(int dataBit) {
    this.dataBit = dataBit;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  public void setBlockInterpolation(boolean blockInterpolation) {
    this.blockInterpolation = blockInterpolation;
  }

  public enum DitheringType {
    Order, FloydSteinberg, FloydSteinberg_Order, Yagi, Yagi_Order,
    FloydSteinberg_Wiki, FloydSteinberg_Wiki_Order,
    FloydSteinberg_Wiki_LineBased
  }

  private DitheringType ditheringType = DitheringType.Yagi;
//  public void setOrderDithering(boolean orderDithering) {
//    this.orderDithering = orderDithering;
//  }
  public void setDitheringType(DitheringType ditheringType) {
    this.ditheringType = ditheringType;
  }

  public void setStore16BitImage(boolean store16BitImage) {
    this.store16BitImage = store16BitImage;
  }

  public void setStore8BitDitheringImage(boolean store8BitDitheringImage) {
    this.store8BitDitheringImage = store8BitDitheringImage;
  }

  public void setStore8BitFRCData(boolean store8BitFRCData) {
    this.store8BitFRCData = store8BitFRCData;
  }

  public void setStore8BitFullFrame(boolean store8BitFullFrame) {
    this.store8BitFullFrame = store8BitFullFrame;
  }

  public void setStore12BitFullFrame(boolean store12BitFullFrame) {
    this.store12BitFullFrame = store12BitFullFrame;
  }

  public void setDMC(boolean DMC) {
    this.DMC = DMC;
  }

  private boolean DG = false;
  public void setDG(boolean dg) {
    this.DG = dg;
  }

  public boolean isDG() {
    return DG;
  }

  private boolean DMC = true;
}
