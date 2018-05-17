package auo.cms.hsv.value.backup;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
import auo.cms.colorspace.depend.AUOHSV;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ValuePrecisionEvaluator {

  public static double getV0(short max, short min, short offset, short minusMax) {
    double deltaV = ( (double) offset) * (max - min) * (minusMax - max) *
        max / Math.pow(2, 25);
    return (max + deltaV);
  }

  /**
   *
   * @param max short 10bit
   * @param min short 10bit
   * @param offset byte 6bit
   * @return short V'
   */
  public static short getV(short max, short min, short offset) {
    short v = getV(max, min, offset, false);
    return v;

  }

  public static short getV(short max, short min, short offset,
                           boolean showMessage) {
    short v = (short) getV(max, min, offset, (short) 1023, 1, 8, 10,
                           interpolateOffset, showMessage);
    return v;
  }

  public static byte getOffset(short max, short min, short newValue) {
    return getOffset(max, min, newValue, (short) 1023, 1, 8, 10);
  }

  public static byte getOffset(short max, short min, short newValue,
                               short minusMax, int bit1,
                               int bit2, int bit3) {

    int max_min10 = max - min; //10bit
    int max_bar10 = minusMax - max; //10bit bar的取法會導致最後結果為10 or 11bit
    final int totalBit = 29 - bit1 - bit2 - bit3;

    int originalResult = newValue << totalBit;
    double gain32 = ( (double) originalResult) / max;
    gain32 = gain32 * Math.pow(2, bit1);
    long theone = (long) Math.pow(2, 29 - bit2 - bit3); //29 - bit2 - bit3
    double offsetXmax_minXmax_bar30 = gain32 - theone;
    double offsetXmax_min20Xmax_bar10 = offsetXmax_minXmax_bar30 *
        Math.pow(2, bit2);
    double offsetXmax_min20 = offsetXmax_min20Xmax_bar10 / max_bar10;
    offsetXmax_min20 = offsetXmax_min20 * Math.pow(2, bit3);
    double offset10 = offsetXmax_min20 / max_min10;
    offset10 = offset10 / Math.pow(2, 4);
    byte result = (byte) Math.round(offset10);

    return result;
  }

  private static boolean interpolateOffset = true;
  public static void setInterpolateOffset(boolean enable) {
    interpolateOffset = enable;
  }

  /**
   *
   * @param max short
   * @param min short
   * @param offset short
   * @param minusMax short
   * @param bit1 int
   * @param bit2 int
   * @param bit3 int
   * @param interpolateOffset boolean
   * @param showMessage boolean
   * @return int
   */
  public static int getV(short max, short min, short offset, short minusMax,
                         int bit1, int bit2, int bit3, //1023, 1, 8, 10
                         boolean interpolateOffset, boolean showMessage) {
    int offset10 = interpolateOffset ? offset : offset << 4; //6+4=10
    boolean negative = offset < 0;
    offset10 = Math.abs(offset10);
    int max_min10 = max - min; //10bit
    int max_bar10 = minusMax - max; //10bit bar的取法會導致最後結果為10 or 11bit
    if (showMessage) {
      System.out.println("offset10: " + offset10);
    }

    // V * (1 + offset*(max-min)*V)

    long offsetXmax_min20 = (offset10 * max_min10) >> bit3; // 20 - bit3 bit
    if (showMessage) {
      System.out.println("offsetXmax_min20: " + offsetXmax_min20);
    }

    long offsetXmax_min20Xmax_bar10 = offsetXmax_min20 * max_bar10; //最大只到 20 - bit3 + 7 bit
    _offsetXmax_min20Xmax_bar10 = offsetXmax_min20Xmax_bar10 >
        _offsetXmax_min20Xmax_bar10 ? offsetXmax_min20Xmax_bar10 :
        _offsetXmax_min20Xmax_bar10; //以bit3=10來說, 為17bit
    if (showMessage) {
      System.out.println("offsetXmax_min20Xmax_bar10: " +
                         offsetXmax_min20Xmax_bar10);
    }

    long offsetXmax_minXmax_bar30 = (offsetXmax_min20Xmax_bar10) >> bit2; //28 - bit2 -bit3 bit
    _offsetXmax_minXmax_bar30 = offsetXmax_minXmax_bar30 >
        _offsetXmax_minXmax_bar30 ? offsetXmax_minXmax_bar30 :
        _offsetXmax_minXmax_bar30; //以bit2 = 8, bit3 = 10來說, 為9bit
    if (showMessage) {
      System.out.println("offsetXmax_minXmax_bar30: " +
                         offsetXmax_minXmax_bar30);
    }

    long theone = (long) Math.pow(2, 29 - bit2 - bit3); //29 - bit2 - bit3
    if (showMessage) {
      System.out.println("theone: " + theone);
    }

    long originalGain = negative ? theone - offsetXmax_minXmax_bar30 :
        theone + offsetXmax_minXmax_bar30;
//    long originalGain = theone + offsetXmax_minXmax_bar30;
    if (showMessage) {
      System.out.println("originalGain: " + originalGain);
    }

//    long gain32 = (long) (originalGain / Math.pow(2, bit1)); //29 - bit1 - bit2 - bit3
    long gain32 = originalGain >> bit1;
//    gain32 = negative ? -gain32 : gain32;
    if (showMessage) {
      System.out.println("gain32: " + gain32);
    }

    long result = (max * gain32); //10+11
    if (showMessage) {
      System.out.println("result: " + result);
    }

    final int totalBit = 29 - bit1 - bit2 - bit3;

    //最後輸出bit數, 受bit 1/2/3影響
    //固定輸出為10bit 0~1023
    int shiftResult = (int) (result >> (totalBit));
    if (showMessage) {
      System.out.println("shiftResult: " + shiftResult);
    }

    staticResult = (result > staticResult) ? result : staticResult;
    if (shiftResult > staticShiftResult) {
      staticShiftResult = shiftResult;
      _max = max;
      _min = min;
    }
//    staticShiftResult = (shiftResult > staticShiftResult) ? shiftResult :
//        staticShiftResult;
    return shiftResult; //可以達11bit
//    return (int) (max + ( (double) offset) * (max - min) / (Math.pow(2, 8) - 1));
  }

  static long _offsetXmax_min20Xmax_bar10;
  static long _offsetXmax_minXmax_bar30;
  static int _max, _min;
  static long staticResult;
  static long staticShiftResult;

  private static double errorRMSD(double[][] arrayA, double[][] arrayB) {
    if (arrayA.length != arrayB.length || arrayA[0].length != arrayB[0].length) {
      throw new java.lang.IllegalArgumentException();
    }
    int zeroCount = 0;
    int height = arrayA.length;
    int width = arrayA[0].length;
    for (int x = 0; x < height; x++) {
      for (int y = 0; y < width; y++) {
        if (arrayA[x][y] == 0 && arrayB[x][y] == 0) {
          zeroCount++;
        }
      }
    }

    int length = height * width - zeroCount;
    double[] rmsdArrayA = new double[length];
    double[] rmsdArrayB = new double[length];
    int index = 0;

    for (int x = 0; x < height; x++) {
      for (int y = 0; y < width; y++) {
        if (arrayA[x][y] != 0 && arrayB[x][y] != 0) {
          rmsdArrayA[index] = arrayA[x][y];
          rmsdArrayB[index] = arrayB[x][y];
          index++;
        }
      }
    }

    return Maths.RMSD(rmsdArrayA, rmsdArrayB);
  }

  public static void main(String[] args) {
//    evaluate(args);
//    functionTest(args);
//    dump(args);
    reverseTest(args);
  }

  public static void reverseTest(String[] args) {
    for (int x = 0; x < 10000; x++) {
      int r = (int) (Math.random() * 255);
      int g = (int) (Math.random() * 255);
      int b = (int) (Math.random() * 255);
      RGB rgb = new RGB(r, g, b);
//      AUOHSV auoHSV = new AUOHSV(rgb);
      AUOHSV auoHSV = new AUOHSV(rgb);
      short[] auoHSVValues = auoHSV.getValues();

//      HSV hsv = new HSV(rgb);
//      short[] auoHSVValues = HSV.AUO.toHSVValues(hsv);
//      auoHSV.getValues();
      short max = auoHSVValues[2];
      short min = (short) rgb.getValue(rgb.getMinChannel(),
                                       RGB.MaxValue.Int10Bit);
      byte offset = (byte) (Math.random() * 126 - 63);
      short newValue = getV(max, min, offset, false);
      if (newValue > 1023) {
        continue;
      }
      byte estOffset = getOffset(max, min, newValue);
      int delta = offset - estOffset;
      System.out.println(delta);
      if (delta > 2) {
        System.out.println("");
        getV(max, min, offset, false);
        getOffset(max, min, newValue);
      }
    }

  }

  public static void dump(String[] args) {
    RGB rgb = new RGB(175, 0, 0);
    AUOHSV auoHSV = new AUOHSV(rgb);
    short[] auoHSVValues = auoHSV.getValues();
//    HSV hsv = new HSV(rgb);
//    getV( (short) 1023, (short) 0, (byte) 63, false);
//    short[] auoHSVValues = HSV.AUO.toHSVValues(hsv);
    short max = auoHSVValues[2];
    short min = (short) rgb.getValue(rgb.getMinChannel(), RGB.MaxValue.Int10Bit);
    short newValue = getV(max, min, (byte) 25, true);
    short estOffset = getOffset(max, min, newValue);

    Plot2D plot = Plot2D.getInstance();
    byte offsetStart = 25;
    byte offsetEnd = 25;
    for (byte offset = offsetStart; offset <= offsetEnd; offset += 3) {
      for (short x = 0; x < 1024; x++) {
        short y = getV(x, (short) 0, (byte) offset, false);
//        System.out.println(x + " " + y);
//        y = y > 2047 ? 2047 : y;
//        y = y > 1023 ? 1023 : y;
        plot.addCacheScatterLinePlot(Byte.toString(offset), x, y);
      }
    }
    plot.setVisible();
    plot.setFixedBounds(0, 0, 1023);
//    plot.setFixedBounds(1, 0, 1023);
    plot.setFixedBounds(1, 0, 1100);
//    plot.setFixedBounds(1, 0, 2048);
//    getV( (short) 1020, (short) 0, (byte) 63, true);
  }

  public static void functionTest(String[] args) {
    for (int x = 0; x < 100; x++) {
      short m1 = (short) (Math.random() * 1023);
      short m2 = (short) (Math.random() * 1023);
      short max = (short) Math.max(m1, m2);
      short min = (short) Math.min(m1, m2);
      byte offset = (byte) (Math.random() * 63);

      short vp = getV(max, min, offset);
      byte offset2 = getOffset(max, min, vp);

//      System.out.println(offset2 - offset);
      int delta = Math.abs(offset - offset2);
//      if (delta > 10) {
      System.out.println(max + " " + min + " " + offset + " " + (vp - max));
//      }

    }
  }

  public static void evaluate(String[] args) {
    short minusMax = 1023;
    boolean show3DPlot = true;
    boolean show3DDataPlot = true;
    boolean showAllError = false;

    boolean showRMSDOnly = true;
//    int bit1 = 9;
//    int bit2 = 5;
//    int bit3 = 0;
//    double vv0 = getV0( (short) 360, (short) 119, (byte) 63, (short) 1020);

//    getV( (short) 360, (short) 119, (byte) 63, minusMax, 0, 0, 0);
//    double vv0 = getV0( (short) 680, (short) 0, (byte) 63, minusMax);
//    getV( (short) 680, (short) 0, (byte) 63, minusMax, 0, 0, 0);

    double[][] idealgrid = new double[1021][1021];
    double[][] acturalgrid = new double[1021][1021];
    double[][] deltagrid = new double[1021][1021];
    double[][] deltagrid2 = new double[1021][1021];
//      double[][] errorgrid = new double[1021][1021];

//    for (int bit1 = 0; bit1 <= 0; bit1++) { //0~2
//      for (int bit2 = 0; bit2 <= 0; bit2++) { //12~13
//        for (int bit3 = 0; bit3 <= 0; bit3++) { //10~16

//    getV( (short) 511, (short) 0, (byte) 63, (short) 1023, 1, 8, 10);
//    getV( (short) 511, (short) 0, (byte) 63, (short) 1020, 1, 8, 10);

//    for (int bit1 = 0; bit1 <= 1; bit1++) { //0~2
//      for (int bit2 = 8; bit2 <= 10; bit2++) { //12~13
//        for (int bit3 = 10; bit3 <= 12; bit3++) { //10~16

    for (int bit1 = 1; bit1 <= 1; bit1++) { //0~2 //best??
      for (int bit2 = 8; bit2 <= 8; bit2++) { //12~13
        for (int bit3 = 10; bit3 <= 10; bit3++) { //10~14

//    double vv0 = getV0( (short) 900, (short) 50, (byte) 63, minusMax);
//    double vv1 = getV( (short) 900, (short) 50, (byte) 63, (short) minusMax, 0,
//                      0, 27);

//    short mm = (short)~ ( (short) 50);

//    double[] value = new double[1021];
//    Arrays.fill(value, 0, 1021, 1);

//    for (byte offset = -64; offset <= 35; offset++) {
//    for (byte offset = 1; offset <= 1; offset++) {
          for (byte offset = 63; offset <= 63; offset++) {
            DoubleArray.clear(idealgrid);
            DoubleArray.clear(acturalgrid);
            DoubleArray.clear(deltagrid);
            DoubleArray.clear(deltagrid2);
//             DoubleArray.clear(deltagrid2);

            for (short min = 0; min <= 1020; min++) {
              for (short max = (short) (min + 1); max <= 1020; max++) {
                double v0 = getV0(max, min, offset, (short) 1023);
//                v0 = (int) v0;
                double v1 = getV(max, min, offset, minusMax, bit1, bit2, bit3,
                                 interpolateOffset, false);

//          grid[min][max] = delta;


                idealgrid[min][max] = v0; //調整結果
                acturalgrid[min][max] = v1;
                deltagrid[min][max] = Math.abs(v0 - max); //調整量
                deltagrid2[min][max] = Math.abs(v1 - v0); //實際與理想調整量的差異
              }
            }
//      Plot3D plot = Plot3D.getInstance("", 800, 800);
//      plot.addGridPlot("", value, value, grid);
//      plot.setVisible();

//      System.out.println(delta);
            double error = Maths.max(deltagrid2);
//            double rmsd = Maths.RMSD(idealgrid, acturalgrid);
            double rmsd = errorRMSD(idealgrid, acturalgrid);

            int a = (29 - bit1 - bit2 - bit3);
            int b = (30 - bit2 - bit3);
//            int b = (28 - bit2 - bit3);
            int c = (20 - bit3);
            String casestring = bit1 + " " + bit2 + " " + bit3 + " ==> " + a +
                " " + b + " " + c + " = " + (a + b + c);
//System.out.println(bit1 + " " + bit2 + " " + bit3 + " ==> " + a +
//                   " " + b + " " + c + " = " + (a + b + c));


            if (rmsd <= 2 || showAllError) {

              if (showRMSDOnly) {
                System.out.println(casestring + " " + rmsd);
              }
              else {
                System.out.println("offset: " + offset);
                System.out.println(casestring);
                System.out.println("maximum Value: " + Maths.max(idealgrid));
                System.out.println("maximum adjust Value: " +
                                   Maths.max(deltagrid));
                System.out.println("maximum Value error: " + error);
                System.out.println("RMSD: " + rmsd);
                System.out.println("maximum adjust Value Index: " +
                                   Arrays.toString(Maths.maxIndex(deltagrid)));
                System.out.println("maximum Value error Index: " +
                                   Arrays.toString(Maths.maxIndex(deltagrid2)));
              }
            }

            int piece = 31;
            double[] value = new double[piece];
            double[][] data = new double[piece][piece];
            double[][] data2 = new double[piece][piece];
            double[][] delta = new double[piece][piece];
            for (int min = 0; min <= (piece - 1); min++) {
              double min_ = min / (piece - 1.) * 1020;
              value[min] = min_;
              for (int max = (min + 1); max <= (piece - 1); max++) {
                double max_ = max / (piece - 1.) * 1020;
                double v0 = getV0( (short) max_, (short) min_, offset,
                                  (short) 1020);
                double v1 = getV( (short) max_, (short) min_, offset,
                                 minusMax, bit1, bit2, bit3, interpolateOffset, false);
                data[min][max] = v0;
                data2[min][max] = v1;
                delta[min][max] = Math.abs(v0 - v1);
              }
            }

            if ( (rmsd <= 2 || showAllError) && show3DPlot) {
              Plot3D plot = Plot3D.getInstance(casestring, 800, 800);
              if (show3DDataPlot) {
                plot.addGridPlot("data", value, value, data);
                plot.addGridPlot("data'", value, value, data2);
              }

              double[][] deltadata2 = new double[piece][piece];

              boolean plotSmooth = false;
              if (plotSmooth) {
                for (int min = 0; min <= (piece - 1); min++) {
                  for (int max = (min + 1); max <= (piece - 1); max++) {

                    double[] deltas = new double[8];
                    int index = 0;
                    for (int x = -1; x <= 1; x++) {
                      for (int y = -1; y <= 1; y++) {
                        int m = min + x;
                        int n = max + y;
                        if (m < 0 || m > (piece - 1) || n < 0 ||
                            n > (piece - 1) ||
                            x == y) {
                          continue;
                        }
                        deltas[index++] = delta[m][n];
                      }
                    }

                    deltas = DoubleArray.minus(deltas, delta[min][max]);
                    DoubleArray.abs(deltas);

                    deltadata2[min][max] = Maths.max(deltas);
                  }
                }
                plot.addGridPlot("smooth", value, value, deltadata2);
              }

              plot.addGridPlot("err", value, value, delta);
              plot.setFixedBounds(0, 0, 1020);
              plot.setFixedBounds(1, 0, 1020);
              plot.setAxeLabel(0, "Max");
              plot.setAxeLabel(1, "min");
              plot.setAxeLabel(2, "dValue");
              plot.setVisible();
              plot.setFixedBounds(2, 0, 20);
              plot.rotateToAxis(4);
            }
          }
        }
      }
    }
//    System.out.println("maxdeltaV: " + maxdeltaV);
    System.out.println("staticResult: " + staticResult);
    System.out.println("staticShiftResult: " + staticShiftResult);
    System.out.println("max: " + _max + " min: " + _min);
    System.out.println("_offsetXmax_min20Xmax_bar10: " +
                       _offsetXmax_min20Xmax_bar10);
    System.out.println("_offsetXmax_minXmax_bar30: " +
                       _offsetXmax_minXmax_bar30);
  }

}
