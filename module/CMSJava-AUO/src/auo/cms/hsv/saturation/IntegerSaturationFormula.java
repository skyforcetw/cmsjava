package auo.cms.hsv.saturation;

import auo.cms.colorspace.depend.*;
import shu.cms.colorspace.depend.*;
import auo.cms.hsv.saturation.backup.*;

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
public class IntegerSaturationFormula
    implements SaturationFormula {
  public static enum Version {
    v1, v2, v3, v4; //v4 from paul
  }

  private static Version version = Version.v4;
  private byte turnPoint;
  private int turnPointBit;
  private double turnPointPercent;
  public IntegerSaturationFormula(byte turnPoint, int turnPointBit) {
    this.turnPoint = turnPoint;
    this.turnPointBit = turnPointBit;

    turnPointPercent = SaturationPrecisionEvaluator.getShortTurnPoint( (byte) (
        turnPoint + 1), version, turnPointBit) / 1023. * 100;
  }

  public double getSaturartion(double originalSaturation, double adjustValue) {
    return getSaturartion(originalSaturation, adjustValue, interpolateAdjust);
  }

  boolean interpolateAdjust = false;
  public void setInterpolateAdjust(boolean interpolateAdjust) {
    this.interpolateAdjust = interpolateAdjust;
  }

  public short getSaturartion(short originalSaturation, short adjustValue) {
    boolean runInComplement = true;
    boolean showMessage = false;
    int bitg2 = 11;
    int bitgain = 12;
    int bitOutputSaturation = 10;
    short result = SaturationPrecisionEvaluator.getShortSaturation(
        originalSaturation, adjustValue, turnPoint, runInComplement, bitg2,
        bitgain, bitOutputSaturation, version, turnPointBit, showMessage);
    return result;
  }

  public double getSaturartion(double originalSaturation, double adjustValue,
                               boolean interpolateAdjust) {
    //==========================================================================
    // setting
    //==========================================================================
    int bitSaturation = 10;
    int bitOutputSaturation = 10;
//    byte turnPoint = 0; //0~7
    int bitgain = 12;

    short shortAdjustValue = (short) adjustValue;
    short lutAdjustValue = interpolateAdjust ? shortAdjustValue :
        (short) (shortAdjustValue << 4); //7 bit push to 10bit
//    boolean runInPercent = false;
    boolean runInComplement = true;
    boolean showMessage = false;
//    Version ver = Version.v2;
    //==========================================================================
    int bitg2 = 11;
    int endSaturation = getMaximum(bitSaturation);
    short saturation = (short) Math.round(originalSaturation / 100. *
                                          endSaturation);
    short result = SaturationPrecisionEvaluator.getShortSaturation(saturation,
        lutAdjustValue, turnPoint, runInComplement, bitg2, bitgain,
        bitOutputSaturation, version, turnPointBit, showMessage);
    double doubleResult = result / 1023. * 100.;
    return doubleResult;
  }

  static int getMaximum(int bit) {
    return (1 << bit) - 1;
  }

  public byte getAdjustOffset(double adjustValue) {
    byte offset = (byte) Math.round(adjustValue / 100. * 63);
    return offset;
  }

  public double getAdjustValue(short originalSaturation, short newSaturation) {
    return getAdjustValue(originalSaturation / 1023. * 100,
                          newSaturation / 1023. * 100);
  }

  public double getAdjustValue(double originalSaturation, double newSaturation) {
    //算出offset, 是saturation的差異
    double offset = newSaturation - originalSaturation;
    double adjustValue = -1;
    //判斷是在 TP 前還後
    if (originalSaturation < turnPointPercent) {
      //算出gain值
      double gain = offset / originalSaturation;
      adjustValue = turnPointPercent * gain;
    }
    else {
      double gain = offset / (originalSaturation - 100);
      adjustValue = (turnPointPercent - 100) * gain;
    }
    return adjustValue;
  }

  public String getName() {
    return "2gainI_" + Byte.toString(turnPoint);
  }

  public static void reverseTest(String[] args) {
    for (int x = 0; x < 100; x++) {
      int r = (int) (Math.random() * 255);
      int g = (int) (Math.random() * 255);
      int b = (int) (Math.random() * 255);
      RGB rgb = new RGB(r, g, b);
      AUOHSV auoHSV = new AUOHSV(rgb);
      short[] auoHSVValues = auoHSV.getValues();
//      short max = auoHSVValues[2];
//      short min = (short) rgb.getValue(rgb.getMinChannel(),
//                                       RGB.MaxValue.Int10Bit);
      byte offset = (byte) (Math.random() * 126 - 63);
      for (byte t = 0; t <= 15; t++) {
        IntegerSaturationFormula f = new IntegerSaturationFormula(t, 4);
//        double saturation = hsv.S;
        double saturation = 0;
        double newSaturation = f.getSaturartion(saturation, offset);
        double newAdjust = f.getAdjustValue(saturation, newSaturation);
        byte newOffset = f.getAdjustOffset(newAdjust);
        System.out.println(newOffset - offset);
      }
    }
  }

  public static void test(String[] args) {
    RGB rgb = new RGB(RGB.ColorSpace.sRGB, new double[] {1020, 1016, 1016},
                      RGB.MaxValue.Int10Bit);
//    HSV hsv = new HSV(rgb);
    AUOHSV auoHSV = new AUOHSV(rgb);
//    short[] auoHSVValues = HSV.AUO.toHSVValues(hsv);
    short[] auoHSVValues = auoHSV.getValues();
    IntegerSaturationFormula formula = new IntegerSaturationFormula( (byte) 7,
        4);
//    auoHSVValues[1]/100.*1023;
    double s = auoHSVValues[1] / 1023. * 100;
    double result = formula.getSaturartion(s, 30);
    System.out.println(result);
  }

  public static void main(String[] args) {
//    test(args);
//    reverseTest(args);
    IntegerSaturationFormula formula = new IntegerSaturationFormula( (byte) 7,
        4);
//    formula.getSaturartion( (short) 190, (short) - 140);
//    version = Version.v4;
    formula.setInterpolateAdjust(true);
    System.out.println(formula.getSaturartion( (short) 512, (short) 20));
//    formula.setInterpolateAdjust(false);
//    System.out.println(formula.getSaturartion( (short) 512, (short) 20));
//    version = Version.v3;
//    System.out.println(formula.getSaturartion( (short) 120, (short) - 356));
  }
}
