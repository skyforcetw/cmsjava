package shu.cms.util;

import java.io.*;
import java.util.*;

import org.apache.commons.collections.primitives.*;
import jxl.read.biff.*;
import jxl.write.*;
import shu.cms.colorformat.adapter.xls.*;
import shu.cms.colorformat.file.*;
import shu.cms.colorspace.depend.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.util.log.*;
import shu.io.files.ExcelFile;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class RGBArray {
  public final static class Fix {
//    public final static void
  }

  public final static double[][] getRGrgArray(RGB[] rgbArray) {
    int size = rgbArray.length;
    double[][] RGrgArray = new double[size][];
    for (int x = 0; x < size; x++) {
      RGrgArray[x] = rgbArray[x].getRGrg();
    }
    return RGrgArray;
  }

  public final static class Pattern {

//    private _Pattern[] _Patterns = new _Pattern[] {
//        new _Pattern(new double[] {1, -2}, new double[] { -1, 0}),
//        new _Pattern(new double[] {2, -2}, new double[] { -1, 0}),
//        new _Pattern(new double[] { -1, 2}, new double[] {1, 0}),
//        new _Pattern(new double[] { -2, 2}, new double[] {1, 0}),
//        new _Pattern(new double[] { -1, 3}, new double[] {1, 0}),
//        new _Pattern(new double[] {1, -3}, new double[] { -1, 0})};

    private final static boolean patternFix0(RGB[] rgbArray, RGB.Channel ch,
                                             RGB.MaxValue maxValue) {
      int size = rgbArray.length;
      double[][] rgbValuesArray = getDoubleArray(rgbArray, maxValue);
      double[] valuesArray = rgbValuesArray[ch.getArrayIndex()];
      double[] firstOrder = Maths.firstOrderDerivatives(valuesArray);
      double[] secondOrder = Maths.firstOrderDerivatives(firstOrder);
      boolean fixed = false;

      for (int x = 0; x < size - 3; x++) {
        double now = secondOrder[x];
        double next = secondOrder[x + 1];
        if (now != next && Math.abs(now) != 1 && now * next < 0 &&
            Math.abs(now) <= 3 && Math.abs(next) <= 3) {
          fixed = true;
          if (now > 0) {
            valuesArray[x + 1]--;
          }
          else {
            valuesArray[x + 1]++;
          }
        }
      }
      for (int x = 0; x < size; x++) {
        RGB rgb = rgbArray[x];
        rgb.setValue(ch, valuesArray[x]);
      }

      return fixed;
    }

    public final static RGB[] patternFix(RGB[] rgbArray, RGB.MaxValue maxValue) {
//      if(true) {
//        return rgbArray;
//      }
      RGB.MaxValue orgMaxValue = rgbArray[0].getMaxValue();
      RGB[] clone = RGBArray.deepClone(rgbArray);
      RGBArray.changeMaxValue(clone, maxValue);
      for (RGB.Channel ch : RGB.Channel.RGBChannel) {
        while (patternFix0(clone, ch, maxValue)) {
        }
      }
      RGBArray.changeMaxValue(clone, orgMaxValue);
      return clone;
    }

    public final static RGB[] patternFix(RGB[] rgbArray, RGB.Channel ch,
                                         RGB.MaxValue maxValue) {
      RGB.MaxValue orgMaxValue = rgbArray[0].getMaxValue();
      RGB[] clone = RGBArray.deepClone(rgbArray);
      RGBArray.changeMaxValue(clone, maxValue);
      while (patternFix0(clone, ch, maxValue)) {
      }
      RGBArray.changeMaxValue(clone, orgMaxValue);
      return clone;
    }

//    public final static void patternCheck(RGB[] rgbArray, RGB.Channel ch,
//                                          RGB.MaxValue maxValue) {
//      int size = rgbArray.length;
//      RGB[] clone = RGBArray.deepClone(rgbArray);
//      RGBArray.changeMaxValue(clone, maxValue);
//      double[][] rgbValueArray = RGBArray.getDoubleArray(clone, maxValue);
//      double[] valuesPrime = Maths.firstOrderDerivatives(rgbValueArray[1]);
//      System.out.println(DoubleArray.toString(valuesPrime));
////      for (RGB rgb : clone) {
////        System.out.println(rgb);
////      }
//    }

  }

  public final static class Check {

    /**
     * 撿查code是否有遞增
     * @param rgbArray RGB[]
     * @return boolean
     */
    public final static boolean checkIncreaseProgressively(RGB[] rgbArray) {
      for (RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
        if (false == checkIncreaseProgressively(rgbArray, ch)) {
          return false;
        }
      }
      return true;
    }

    /**
     * 撿查code是否有遞增
     * @param rgbArray RGB[]
     * @param ch Channel
     * @return boolean
     */
    public final static boolean checkIncreaseProgressively(RGB[] rgbArray,
        RGBBase.Channel ch) {
      int size = rgbArray.length;
      for (int x = 0; x < size - 1; x++) {
        RGB rgb0 = rgbArray[x];
        RGB rgb1 = rgbArray[x + 1];
        if (rgb0.getValue(ch) > rgb1.getValue(ch)) {
          return false;
        }
      }
      return true;
    }

    /**
     * 返回R/B兩code之間差異大於G的最大差異之index
     * @param rgbArray RGB[]
     * @param start int
     * @param end int
     * @return int[][]
     */
    public final static int[][] getRBDifferentiaAboveGreenMax(RGB[] rgbArray,
        int start, int end) {
      double[][] rgbValues = RGBArray.getDoubleArray(rgbArray,
          RGB.MaxValue.Int9Bit);
      double[][] partRGBValues = start == 0 && (end == rgbArray.length - 1) ?
          rgbValues : DoubleArray.getColumnsRangeCopy(rgbValues, start, end);
      double[] rPrime = Maths.firstOrderDerivatives(partRGBValues[0]);
      double[] gPrime = Maths.firstOrderDerivatives(partRGBValues[1]);
      double[] bPrime = Maths.firstOrderDerivatives(partRGBValues[2]);
      double gMaxDifferentia = Maths.max(gPrime);
      int[] rIndexArray = getDifferentiaAboveThreshold(rPrime, gMaxDifferentia);
      int[] bIndexArray = getDifferentiaAboveThreshold(bPrime, gMaxDifferentia);
      return new int[][] {
          rIndexArray, bIndexArray};
    }

    /**
     * 返回R/B兩code之間差異大於G的最大差異之index
     * @param rgbArray RGB[]
     * @return int[][]
     */
    public final static int[][] getRBDifferentiaAboveGreenMax(RGB[] rgbArray) {
      return getRBDifferentiaAboveGreenMax(rgbArray, 0, rgbArray.length - 1);
    }

    /**
     * 返回values中兩個code之間的差異大於threshold之index
     * @param values double[]
     * @param threshold double
     * @return int[]
     */
    private final static int[] getDifferentiaAboveThreshold(double[] values,
        double threshold) {
      ArrayIntList intList = new ArrayIntList();
      int size = values.length;
      for (int x = 0; x < size; x++) {
        double v = values[x];
        if (v > threshold) {
          intList.add(x);
        }
      }
      return intList.toArray();
    }

  }

  public static void main(String[] args) throws Exception {
    RGB[] rgbArray = RGBArray.loadAUOExcel(
        "lcd.calibrate\\(0)(255.0 255.0 255.0)_uvpByDE00_50_2.2ByW@auo_T370HW02\\coordinate-calibrate.xls",
        RGB.MaxValue.Int12Bit);
    RGB[] result = RGBArray.Pattern.patternFix(rgbArray, RGB.Channel.G,
                                               RGB.MaxValue.Int10Bit);
    RGBArray.storeAUOExcel(result, "result.xls");
//    Plot2D plot = Plot2D.getInstance();
//    double[][] rgArray = RGBArray.getRGrgArray(rgbArray);
//    rgArray = DoubleArray.transpose(rgArray);
//
//    plot.addLinePlot("r", 0, 255, rgArray[0]);
//    plot.addLinePlot("g", 0, 255, rgArray[1]);
//
//    plot.setVisible();
//    int[][] indexArray = Check.getRBDifferentiaAboveGreenMax(rgbArray, 0, 50);
//    System.out.println(Arrays.toString(indexArray[0]));
//    System.out.println(Arrays.toString(indexArray[1]));
//    Pattern.patternCheck(rgbArray, RGB.Channel.G, RGB.MaxValue.Int10Bit);

  }

  public final static String toString(RGB[] rgbArray) {
    StringBuilder buf = new StringBuilder();
    for (RGB rgb : rgbArray) {
      buf.append(rgb);
      buf.append('\n');
    }
    return buf.toString();
  }

  public final static boolean equals(RGB[] rgbArray1, RGB[] rgbArray2) {
    if (rgbArray1.length != rgbArray2.length) {
      throw new IllegalArgumentException("rgbArray1.length != rgbArray2.length");
    }
    int size = rgbArray1.length;
    for (int x = 0; x < size; x++) {
      boolean eq = rgbArray1[x].equals(rgbArray2[x]);
      if (!eq) {
        return false;
      }
    }
    return true;
  }

  public final static Interpolation1DLUT[] getRGBInterpLUT(RGB[] rgbOutput) {
    return Interpolation1DLUT.getRGBInterpLUT(getOriginalRGBDoubleArray(),
                                              getDoubleArray(rgbOutput));
  }

  public final static Interpolation1DLUT[] getRGBInterpLUT(RGB[] rgbInput,
      RGB[] rgbOutput) {
    return Interpolation1DLUT.getRGBInterpLUT(getDoubleArray(rgbInput),
                                              getDoubleArray(rgbOutput));
  }

  public final static void storeBinaryFile(RGB[] rgbArray,
                                           String filename,
                                           RGB.MaxValue maxValue) {
    DataOutputStream dos = null;
    try {
      FileOutputStream fos = new FileOutputStream(filename, false);
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      dos = new DataOutputStream(bos);
    }
    catch (FileNotFoundException ex) {
      Logger.log.error("", ex);
    }

    try {
      switch (maxValue) {
        case Int8Bit: {
          dos.writeShort( (short) 8);
          break;
        }
        case Int10Bit: {
          dos.writeShort( (short) 10);
          break;
        }
        case Int12Bit: {
          dos.writeShort( (short) 12);
          break;
        }
      }

      for (RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
        for (RGB rgb : rgbArray) {
          short val = (short) rgb.getValue(ch, maxValue);
          dos.writeShort(val);
        }
      }

      dos.flush();
      dos.close();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

  public final static RGB[] loadVVExcel(String filename) throws IOException,
      BiffException {
    VVCPTableXLSAdapter adapter = new VVCPTableXLSAdapter(filename);
    List<RGB> rgbList = adapter.getRGBList();
    RGB[] rgbArray = RGBArray.toRGBArray(rgbList);
    return rgbArray;
  }

  public final static RGB[] loadAUOExcel(String filename) throws IOException,
      BiffException {
    return loadAUOExcel(filename, null);
  }

  public final static RGB[] loadAUOExcel(String filename, RGB.MaxValue maxValue) throws
      IOException, BiffException {
    AUOCPTableXLSAdapter adapter = maxValue == null ?
        new AUOCPTableXLSAdapter(filename) :
        new AUOCPTableXLSAdapter(filename, maxValue);
    List<RGB> rgbList = adapter.getRGBList();
    RGB[] rgbArray = RGBArray.toRGBArray(rgbList);
    return rgbArray;
  }

  public final static void storeVVExcel(RGB[] rgbArray,
                                        String filename) {
    storeVVExcel(rgbArray, filename, rgbArray[0].getMaxValue());
  }

  public final static void storeVVExcel(RGB[] rgbArray, String filename,
                                        RGB.MaxValue maxValue) {
    try {
      ExcelFile xls = new ExcelFile(filename, true);
      write2VVExcelCell(xls, rgbArray, maxValue);
      xls.close();
      xls = null;
    }
    catch (WriteException ex) {
      Logger.log.error("", ex);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (BiffException ex) {
      Logger.log.error("", ex);
    }
  }

  public final static void storeAUOExcel(RGB[] rgbArray, String filename) {
    storeAUOExcel(rgbArray, filename, rgbArray[0].getMaxValue());
  }

  public final static void storeAUOExcel(RGB[] rgbArray, String filename,
                                         RGB.MaxValue maxValue) {
    try {
      ExcelFile xls = new ExcelFile(filename, true);
      write2AUOExcelCell(xls, rgbArray, maxValue);
      xls.close();
      xls = null;
    }
    catch (WriteException ex) {
      Logger.log.error("", ex);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (BiffException ex) {
      Logger.log.error("", ex);
    }
  }

  /**
   * 原始的cp code RGB, 也就是0 1 2 3...255
   * @return RGB[]
   */
  public final static RGB[] getOriginalRGBArray() {
    if (OriginalRGBArray == null) {
      OriginalRGBArray = new RGB[256];
      for (int x = 0; x < 256; x++) {
        OriginalRGBArray[x] = new RGB(RGB.ColorSpace.unknowRGB,
                                      new int[] {x, x, x});
      }
    }
    return OriginalRGBArray;
  }

  public final static RGB[] getNullRGBArray() {
//    if (true) {
//      return getOriginalRGBArray();
//    }
    if (NullRGBArray == null) {
      NullRGBArray = new RGB[256];
      for (int x = 0; x < 256; x++) {
        NullRGBArray[x] = new RGB(RGB.ColorSpace.unknowRGB,
                                  new int[] {0, 0, 0});
      }
    }
    return NullRGBArray;
  }

  /**
   * 原始的cp code, 也就是0 1 2 3...255
   */
  private static RGB[] OriginalRGBArray = null;
  private static RGB[] NullRGBArray = null;

  private static double[][] OriginalRGBDoubleArray = null;
  public final static double[][] getOriginalRGBDoubleArray() {
    if (OriginalRGBDoubleArray == null) {
      double[][] result = new double[3][256];
      for (int x = 0; x < 256; x++) {
        result[0][x] = x;
        result[1][x] = x;
        result[2][x] = x;
      }
      OriginalRGBDoubleArray = result;
    }
    return OriginalRGBDoubleArray;
  }

  public final static double[][] getDoubleArray(RGB[] rgbArray) {
    return getDoubleArray(rgbArray, RGB.MaxValue.Double255);
  }

  public final static double[][] getDoubleArray(RGB[] rgbArray,
                                                RGB.MaxValue maxValue) {
    int size = rgbArray.length;
    double[][] result = new double[3][size];
    double[] values = new double[3];
    for (int x = 0; x < size; x++) {
      rgbArray[x].getValues(values, maxValue);
      result[0][x] = values[0];
      result[1][x] = values[1];
      result[2][x] = values[2];
    }
    return result;
  }

  /**
   * 將rgbList轉成rgb陣列
   * @param rgbList List
   * @return RGB[]
   */
  public final static RGB[] toRGBArray(List<RGB> rgbList) {
    int size = rgbList.size();
    return rgbList.toArray(new RGB[size]);
//    return RGBArray.toRGBArray(rgbList);
  }

  public final static List<RGB> toRGBList(RGB[] rgbArray) {
    int size = rgbArray.length;
    List<RGB> rgbList = new ArrayList<RGB> (size);
    for (RGB rgb : rgbArray) {
      if (rgb != null) {
        rgbList.add(rgb);
      }
    }
    return rgbList;
  }

  public final static List<RGB> toRGBListAndCheck(RGB[] rgbArray) {
    if (rgbArray.length == 0) {
      throw new IllegalArgumentException("rgbArray.length == 0");
    }
    List<RGB> rgbList = toRGBList(rgbArray);
    return rgbList;
  }

  public final static RGB[] deepClone(RGB[] rgbArray) {
    int size = rgbArray.length;
    RGB[] clone = Arrays.copyOf(rgbArray, rgbArray.length);
    for (int x = 0; x < size; x++) {
      clone[x] = (RGB) clone[x].clone();
    }
    return clone;
  }

  public final static RGB[] deepClone(RGB[] rgbArray, int start, int end) {
//    int size = rgbArray.length;
    RGB[] clone = Arrays.copyOfRange(rgbArray, start, end);
    int size = clone.length;
    for (int x = 0; x < size; x++) {
      clone[x] = (RGB) clone[x].clone();
    }
    return clone;
  }

  public final static void changeMaxValue(RGB[] rgbArray, RGB.MaxValue maxValue) {
    int size = rgbArray.length;
    for (int x = 0; x < size; x++) {
      RGB rgb = rgbArray[x];
      rgb.changeMaxValue(maxValue);
    }
  }

  public final static void write2VVExcelCell(ExcelFile xls, RGB[] rgbArray) throws
      WriteException {
//    write2VVExcelCell(xls, rgbArray, false);
    write2VVExcelCell(xls, rgbArray, rgbArray[0].getMaxValue());
  }

  private final static void write2VVExcelCell(ExcelFile xls, RGB[] rgbArray,
                                              RGB.MaxValue maxValue) throws
      WriteException {
    int size = rgbArray.length;
    for (int x = 0; x < size; x++) {
      RGB rgb = rgbArray[x];
      xls.setCell(0, x, rgb.getValue(RGBBase.Channel.R, maxValue));
      xls.setCell(1, x, rgb.getValue(RGBBase.Channel.G, maxValue));
      xls.setCell(2, x, rgb.getValue(RGBBase.Channel.B, maxValue));
    }
  }

  private final static void write2AUOExcelCell(ExcelFile xls, RGB[] rgbArray,
                                               RGB.MaxValue maxValue) throws
      WriteException {
    xls.setSheetName("Gamma Table");
    xls.setCell(0, 0, "Gray Level");
    xls.setCell(1, 0, "Gamma R");
    xls.setCell(2, 0, "Gamma G");
    xls.setCell(3, 0, "Gamma B");

    int size = rgbArray.length;

    for (int x = 0, index = x + 1; x < size; x++, index = x + 1) {
      RGB rgb = rgbArray[x];
      xls.setCell(0, index, x);
      xls.setCell(1, index, rgb.getValue(RGBBase.Channel.R, maxValue));
      xls.setCell(2, index, rgb.getValue(RGBBase.Channel.G, maxValue));
      xls.setCell(3, index, rgb.getValue(RGBBase.Channel.B, maxValue));
    }
  }
}
