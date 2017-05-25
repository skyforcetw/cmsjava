package auo.cms.hsv.saturation.backup;

import shu.cms.plot.*;
import java.awt.*;
import auo.cms.hsv.saturation.*;

//import shu.plot.*;

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
public class SaturationPrecisionEvaluator {

  public static double getDoubleSaturation(short saturation, byte offset) {
    double gain = offset / 50.;
    double result = (saturation < 67) ? saturation + saturation * gain :
        saturation + 2. * gain * (100.5 - saturation);
    return result;

  }

  private static int getTurnPointPercent(byte turnPoint, int turnPointBit) {
    int maxTurnPoint = (int) Math.pow(2, turnPointBit);
    return (int) ( (turnPoint + 1) * (100. / maxTurnPoint));
//    return ( (turnPoint + 2) * 10);
  }

//  private static int getTurnPointPercent(short turnPoint) {
//    return turnPoint == 0 ? 0 : (int) (turnPoint * (100. / 15));
////    return ( (turnPoint + 2) * 10);
//  }

//  private static short getTurnPoint(short turnPoint, int bit) {
//    return (short) (getTurnPointPercent(turnPoint) / 100. * getMaximum(bit));
//  }

  static int getMaximum(int bit) {
    return (1 << bit) - 1;
  }

  /*public static short getS(short saturation, byte adjustValue,
                           byte turnPoint, int bitSaturation, int bitn,
                           int bitOutputSaturation,
                           boolean calculateInPercent, boolean complement) {
    double gain = -1;
    double s = -1;
    turnPoint++;
    int endSaturation = (1 << bitSaturation) + (complement ? -1 : 0);
    int newEndSaturation = (1 << bitOutputSaturation) + (complement ? -1 : 0);
    short shotTurnPoint = (short) (getTurnPoint( (short) (turnPoint),
                                                bitSaturation) + 1);

    int deltaBit = bitOutputSaturation - bitSaturation;
//    int adjust = adjustValue << deltaBit; //7bit+delta bit


    short result = -1;
//    short shortSaturation = (short) (saturation << deltaBit);

    if (calculateInPercent) {
      double saturationPercent = ( (double) saturation) / endSaturation * 100;
      double turnPointPercent = getTurnPointPercent(turnPoint); //0~100
      if (saturation < shotTurnPoint) {
        gain = adjustValue / turnPointPercent;
        s = saturation * (1 + gain) * Math.pow(2, deltaBit);
      }
      else {
        gain = adjustValue / (turnPointPercent - 100);
        double offset = (saturationPercent - 100) * gain;

        s = saturationPercent + offset;
        s = (s / 100. * newEndSaturation);
      }

    }
    else {
      double n = Math.pow(2, bitn) - 1;
      double n2 = 1. / n;
      double g1 = ( (double) adjustValue) / n;
      double g1n2 = g1 / n2;
      int intg1 = ( (int) g1n2); //7bit
//      System.out.println(g1n2+" "+intg1);
//
//      double g2 = n / (7 - turnPoint);
//      double g2n2 = g2 / n2;
//      int intg2 = (int) g2n2;
//
//      int intg1g2 = (intg1 * intg2) >> 12;

      if (saturation < shotTurnPoint) {
        //======================================================================
        // S' = S + LUT * (S/T)
        //    = S + (LUT/T) * S = S * ( 1+ (LUT/T) )
        //======================================================================

//        double g1 = ( (double) adjustValue) / n; //7bit
//        double g1n2 = g1 / n2;
//        int intg1 = ( (int) g1n2);
//
//        double g2 = n / (turnPoint); //3bit => 5bit整數/2bit浮點
//        double g2n2 = g2 / n2; //留8個bit
//        int intg2 = ( (int) g2n2) >> 8;
//
//        int intg1g2 = (intg1 * intg2) >> 8;
//        int theOne = (int) Math.pow(2, bitgain) - 1;
//        int ints = saturation * (theOne + intg1g2);
//        s = ints >> 6;


//        double g1 = ( (double) adjustValue) / n;
//        double g1n2 = g1 / n2;
//        int intg1 = ( (int) g1n2); //7bit
//        if (saturation == shotTurnPoint - 1) {
//          int x = 1;
//        }

        double g2 = n / (turnPoint); //8bit / 3bit
        double g2n2 = g2 / n2;
        int intg2 = ( (int) g2n2); //16bit
        intg2 = intg2 >> ( (bitn - 8) << 1);

   int intg1g2 = (intg1 * intg2) >> (12 - deltaBit); //7 + 16 bit = 22 -12 = 10
   int saturationOffset = (saturation << deltaBit) * intg1g2; //10 bit + 12 = 22
   saturationOffset = saturationOffset >> (8 + deltaBit); //22-8 = 12 = 10?
        maxSaturationOffset = saturationOffset > maxSaturationOffset ?
            saturationOffset : maxSaturationOffset;
        int ints = (saturation << deltaBit) + saturationOffset; //10+10
        s = ints;
      }
      else {
        //======================================================================
        // S' = S + LUT / (S-100%)/(100%-T)
        //    = S + LUT / (100%-T) * (S-100%)
        //    = S + (LUT/n) * (n/(100%-T) * (100%-S)
        //======================================================================
//        int intg1g2 = adjustValue * (7 - turnPoint);
//        int saturationOffset = (newEndSaturation - saturation) * intg1g2;
//        saturationOffset = saturationOffset >> 9;
//        int ints = saturation + saturationOffset + 16;
//        s = ints;

//        double g1 = ( (double) adjustValue) / n;
//        double g1n2 = g1 / n2;
//        int intg1 = ( (int) g1n2);

        double g2 = n / (7 - turnPoint);
        double g2n2 = g2 / n2;
        int intg2 = (int) g2n2;
        intg2 = intg2 >> ( (bitn - 8) << 1);

        int intg1g2 = (intg1 * intg2) >> (12 - deltaBit);
        int saturationOffset = (newEndSaturation - (saturation << deltaBit)) *
            intg1g2;
        saturationOffset = saturationOffset >> (8 + deltaBit);
        maxSaturationOffset = saturationOffset > maxSaturationOffset ?
            saturationOffset : maxSaturationOffset;
        int ints = (saturation << deltaBit) + saturationOffset;
        s = ints;

//        s = getSaturation(bitn, adjustValue, (byte) (7 - turnPoint),
//                          (short) (newEndSaturation - saturation));
      }

    }

//    System.out.println(s);
//    result = (short) (s / 100. * newEndSaturation);
    result = (short) s; //可以達12bit

    return result;

     }*/

//  public static short getShortTurnPoint(byte turnPoint,
//                                        IntegerSaturationFormula.Version v) {
//    return getShortTurnPoint(turnPoint, v, 3);
//  }

  public static short getShortTurnPoint(byte turnPoint,
                                        IntegerSaturationFormula.Version v,
                                        int bit) {
    short shotTurnPoint = -1;
    int shiftBit = 10 - bit;
    switch (v) {
      case v1:
        shotTurnPoint = (short) ( (turnPoint << shiftBit)); //11bit
        break;
      case v2:
        shotTurnPoint = (short) ( (turnPoint << shiftBit) - 1); //10bit
        break;
      case v3:
      case v4:
        shotTurnPoint = (short) ( (turnPoint << shiftBit) - 1);
        break;
    }
    return shotTurnPoint;
  }

  /*public static short getSShort_2(short saturation, short adjustValue,
   byte turnPoint, boolean complement, int bitG2,
                                  int bitgain, int bitOutputSaturation,
                                  boolean showMessage,
                                  IntegerSaturationFormula.Version v) {
    int newEndSaturation = (1 << bitOutputSaturation) + (complement ? -1 : 0);

    short result = -1;
    int intg1 = adjustValue; //9bit
    double n = 1;
    double n2 = 1. / Math.pow(2, bitG2);

    int intg1g2Shift = bitG2 - 3 + (12 - bitgain);
    int offsetShift = 10 + (bitgain - 12) + (0); //乘出來的offset做縮減

    if (showMessage) {
//      System.out.println("intg1g2Shift: " + intg1g2Shift);
//      System.out.println("offsetShift: " + offsetShift);
    }

    boolean select = false;
    turnPoint++; //0~15 => 1~16
    short shotTurnPoint = getShortTurnPoint(turnPoint, v);
    switch (v) {
      case v1:
        select = saturation < shotTurnPoint;
        break;
      case v2:
        select = saturation <= shotTurnPoint;
        break;
      case v3:
        select = saturation < shotTurnPoint;
        break;
    }

    if (select) {
      //======================================================================
      // S' = S + LUT * (S/T)
      //    = S + (LUT/T) * S = S * ( 1+ (LUT/T) )
      //======================================================================
      double g2 = n / (turnPoint); // 3bit
      double g2n2 = g2 / n2;
      int intg2_turnpoint = ( (int) g2n2); //9bit register

      int intg1g2 = (intg1 * intg2_turnpoint) >> intg1g2Shift; //9+9=18 18-5=13
      int saturationOffset = saturation * intg1g2; // 12 + 13 = 23 (22?)
      int saturationOffset2 = saturationOffset >> offsetShift; //22 - 10 = 12
      int ints = saturation + saturationOffset2; //12 + 12

      result = (short) ints;
      if (showMessage) {
        System.out.println("0: " + saturation + " " + intg1g2 + " " +
                           saturationOffset + " " + saturationOffset2);
      }
      if (saturationOffset2 > maxSaturationOffset) {
        maxSaturationOffset = saturationOffset2;
      }
    }

//    if (saturation >= shotTurnPoint) {
    else {
      //======================================================================
      // S' = S + LUT / (S-100%)/(100%-T)
      //    = S + LUT / (100%-T) * (S-100%)
      //    = S + (LUT/n) * (n/(100%-T) * (100%-S)
      //======================================================================
      double g2 = n / (8 - turnPoint);
      double g2n2 = g2 / n2;
      int intg2_turnpoint = (int) g2n2; //9bit register

      int intg1g2 = (intg1 * intg2_turnpoint) >> intg1g2Shift;
      int saturationOffset = (newEndSaturation - saturation) * intg1g2;
      int saturationOffset2 = saturationOffset >> offsetShift;
      int ints = saturation + saturationOffset2;

//      if (saturation == shotTurnPoint && showMessage) {
//        System.out.println("Turnpoint delta: " + (result - ints));
//      }

      result = (short) ints;
      if (showMessage) {
        System.out.println("1: " + saturation + " " + intg1g2 + " " +
                           saturationOffset + " " + saturationOffset2);
      }
    }
    return result;
     }*/

  public static short getShortSaturation(short saturation, short adjustValue,
                                         byte turnPoint, boolean complement,
                                         int bitG2,
                                         int bitgain, int bitOutputSaturation,
                                         IntegerSaturationFormula.Version v,
                                         int turnPointBit,
                                         boolean showMessage) {

    int newEndSaturation = (1 << bitOutputSaturation) + (complement ? -1 : 0);

    short result = -1;
    int intg1 = adjustValue; //10bit
    double n = 1;
    double n2 = 1. / (Math.pow(2, bitG2) - 1);

    int intg1g2Shift = bitG2 - 3 + (12 - bitgain);
    int offsetShift = 10 + (bitgain - 12) + (3 - turnPointBit); //乘出來的offset做縮減

    if (showMessage) {
      System.out.println("intg1g2Shift: " + intg1g2Shift);
      System.out.println("offsetShift: " + offsetShift);
    }

    boolean select = false;
    turnPoint++; //turnPoint +1
    short shotTurnPoint = getShortTurnPoint(turnPoint, v, turnPointBit);
    if (showMessage) {
      System.out.println("shotTurnPoint: " + shotTurnPoint);
    }
    switch (v) {
      case v1:
        select = saturation < shotTurnPoint;
        break;
      case v2:
        select = saturation <= shotTurnPoint;
        break;
      case v3:
      case v4:
        select = saturation < shotTurnPoint;
        break;

    }

    int turnPointMax = (int) Math.pow(2, turnPointBit);

    if (select) {
      //======================================================================
      // S' = S + LUT * (S/T)
      //    = S + (LUT/T) * S = S * ( 1+ (LUT/T) )
      //======================================================================
      double g2 = n / (turnPoint); // 3bit
      double g2n2 = g2 / n2;
      int intg2_turnpoint = (int) g2n2; //9bit register
      if (showMessage) {
        System.out.println("intg2_turnpoint: " + intg2_turnpoint);
      }
      int ints = 0;
      if (IntegerSaturationFormula.Version.v4 == v) {
        int originalIntg1g2 = saturation * intg2_turnpoint;
        boolean negative = intg1 < 0;
//        originalIntg1g2 = Math.abs(originalIntg1g2);

        int intg1g2 = originalIntg1g2 >> intg1g2Shift; //9+9=18 18-5=13
        int saturationOffset = intg1 * intg1g2; // 12 + 13 = 23 (22?)
        saturationOffset = Math.abs(saturationOffset);
        int saturationOffset2 = saturationOffset >> offsetShift; //22 - 10 = 12
        ints = negative ? saturation - saturationOffset2 :
            saturation + saturationOffset2; //12 + 12
      }
      else {
        int originalIntg1g2 = intg1 * intg2_turnpoint;
        boolean negative = originalIntg1g2 < 0;
        originalIntg1g2 = Math.abs(originalIntg1g2);

        int intg1g2 = originalIntg1g2 >> intg1g2Shift; //9+9=18 18-5=13
        int saturationOffset = saturation * intg1g2; // 12 + 13 = 23 (22?)
        int saturationOffset2 = saturationOffset >> offsetShift; //22 - 10 = 12
        ints = negative ? saturation - saturationOffset2 :
            saturation + saturationOffset2; //12 + 12
        if (showMessage) {
          System.out.println("intg1g2: " + intg1g2);
          System.out.println("saturationOffset: " + saturationOffset);
          System.out.println("saturationOffset2: " + saturationOffset2);
          System.out.println("ints: " + ints);
        }

        if (showMessage) {
          System.out.println("0: " + saturation + " " + intg1g2 + " " +
                             saturationOffset + " " + saturationOffset2);
        }
        if (saturationOffset2 > maxSaturationOffset) {
          maxSaturationOffset = saturationOffset2;
        }
      }

      result = (short) ints;

    }
    else { //saturation >= shotTurnPoint
      //======================================================================
      // S' = S + LUT / (S-100%)/(100%-T)
      //    = S + LUT / (100%-T) * (S-100%)
      //    = S + (LUT/n) * (n/(100%-T) * (100%-S)
      //======================================================================
      double g2 = n / (turnPointMax - turnPoint);
      double g2n2 = g2 / n2;
      int intg2_turnpoint = (int) g2n2; //9bit register
      if (showMessage) {
        System.out.println("intg2_turnpoint: " + intg2_turnpoint);
      }

      int ints = 0;
      if (IntegerSaturationFormula.Version.v4 == v) {
        int originalIntg1g2 = (newEndSaturation - saturation) * intg2_turnpoint;
        boolean negative = intg1 < 0;
//        originalIntg1g2 = Math.abs(originalIntg1g2);

        int intg1g2 = originalIntg1g2 >> intg1g2Shift;
        int saturationOffset = intg1 * intg1g2;
        saturationOffset = Math.abs(saturationOffset);
        int saturationOffset2 = saturationOffset >> offsetShift;
        ints = negative ? saturation - saturationOffset2 :
            saturation + saturationOffset2; //12 + 12
      }
      else {
        int originalIntg1g2 = intg1 * intg2_turnpoint;
        boolean negative = originalIntg1g2 < 0;
        originalIntg1g2 = Math.abs(originalIntg1g2);

        int intg1g2 = originalIntg1g2 >> intg1g2Shift;
        int saturationOffset = (newEndSaturation - saturation) * intg1g2;
        int saturationOffset2 = saturationOffset >> offsetShift;
        ints = negative ? saturation - saturationOffset2 :
            saturation + saturationOffset2; //12 + 12

        if (showMessage) {
          System.out.println("intg1g2: " + intg1g2);
          System.out.println("saturationOffset: " + saturationOffset);
          System.out.println("saturationOffset2: " + saturationOffset2);
          System.out.println("ints: " + ints);
        }

        if (showMessage) {
          System.out.println("1: " + saturation + " " + intg1g2 + " " +
                             saturationOffset + " " + saturationOffset2);
        }

      }

      result = (short) ints;
    }
    return result;

  }

  private static int maxSaturationOffset = 0;
  public static void main(String[] args) {
    evaluate(args);
//    dump(args);
  }

  public static void evaluate(String[] args) {

    Plot2D plot = Plot2D.getInstance("curve");
    Plot2D plot2 = Plot2D.getInstance("delta");
    //    Plot2D plot2 = Plot2D.getInstance("curve");
    //    Plot2D plot3 = Plot2D.getInstance("delta");
    //==========================================================================
    // setting
    //==========================================================================
    int bitSaturation = 10;
    int bitOutputSaturation = 10;
//    byte turnPoint = 0; //0~15, <=7 is never clip
    byte beginTurnPoint = 8;
    byte endTurnPoint = 8;
    int turnPointBit = 4;
    //4bit optimaize: 11,12 ;3bit optimaize: 11,11
    int bitgain = 12; //more important
    int bitg2 = 11;
    //bitgain,bitg2,turnPointBit:
    //9,9,3:
    //11,11,4: 3 will inverse
    //11,12,4: all ok

    short beginLutAdjustValue = 32;
    short endLutAdjustValue = 32;
//    short lutAdjustValue = 63;

    boolean runInComplement = true;
    boolean showMessage = false;
    boolean showPlot = true;
    boolean skipIllegalAdjust = false;
    IntegerSaturationFormula.Version ver = IntegerSaturationFormula.Version.v4;
    //==========================================================================


    int deltaBit = bitOutputSaturation - bitSaturation;
    for (byte turnPoint = beginTurnPoint; turnPoint <= endTurnPoint; turnPoint++) {
      int turnPointPercent = getTurnPointPercent(turnPoint, turnPointBit);

      System.out.println(turnPoint + " Turn Point%: " + turnPointPercent);

      int endSaturation = getMaximum(bitSaturation); // (1 << bitSaturation) - 1;

//    for (int bitg2 = 9; bitg2 <= 9; bitg2++) {
//    System.out.println("bitg2: " + bitg2);]
      for (short lutAdjustValue = beginLutAdjustValue;
           lutAdjustValue <= endLutAdjustValue; lutAdjustValue++) {

        short adjustValue = (short) (lutAdjustValue << 4); //7 bit push to 10bit
//      System.out.println(adjustValue);
        boolean inverse = false;
        boolean illegalAdjust = false;
        short lastR = 0;
        for (short x = 0; x <= endSaturation; x++) {
//        for (short x = 767; x <= 767; x++) {
          short r = getShortSaturation(x, adjustValue, turnPoint,
                                       runInComplement, bitg2,
                                       bitgain, bitOutputSaturation,
                                       ver, turnPointBit,
                                       showMessage);
          if (r > endSaturation) {
            illegalAdjust = true;
            if (skipIllegalAdjust) {
              break;
            }
          }
          if (r < lastR) {
            inverse = true;
          }
          lastR = r;

          int input = (x << deltaBit);
          if (showPlot) {
            plot.addCacheScatterLinePlot(Integer.toString(bitg2) + " " +
                                         Short.toString(lutAdjustValue), x, r);
            plot2.addCacheScatterLinePlot(Integer.toString(bitg2) + " " +
                                          Short.toString(lutAdjustValue), x,
                                          r - input);
          }
        } // for x loop

        if (illegalAdjust && skipIllegalAdjust) {
          break;
        }

//        plot.addLegend();
        if (showPlot) {
          if (!plot.isVisible()) {
            plot.addLinePlot("Original", Color.blue, 0, 1023, 0,
                             endSaturation << deltaBit);
          }
          plot.setVisible();
        }
        plot.setFixedBounds(0, 0, endSaturation);
        plot.setFixedBounds(1, 0, getMaximum(bitOutputSaturation));
//        plot2.addLegend();
        if (showPlot) {
          plot2.setVisible();
        }
        plot2.setFixedBounds(0, 0, endSaturation);
        plot2.setFixedBounds(1, 0, getMaximum(bitOutputSaturation));

        if (inverse) {
          System.out.println("Inverse!!");
          System.out.println("adjustValue: " + adjustValue + " / " +
                             lutAdjustValue);
          System.out.println("maxSaturationOffset: " + maxSaturationOffset);
        }
      } // for lutAdjustValue loop
    }

  }
}
