package auo.cms.hsv.old;

import auo.cms.hsv.autotune.*;
import auo.cms.hsv.saturation.*;
import auo.cms.hsv.saturation.turnpoint.*;
import shu.cms.colorspace.depend.*;
import shu.cms.plot.*;
import shu.math.array.*;
import shu.math.lut.*;
import auo.cms.colorspace.depend.AUOHSV;
import auo.cms.hsv.*;

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
 * @deprecated
 */
public class HSVLUTInterpolator {
  public HSVLUTInterpolator(double[] hueAdjustValue,
                            double[] saturationAdjustValue,
                            double[] valueAdjustValue
      ) {
    integerMode = false;
    init(hueAdjustValue, saturationAdjustValue, valueAdjustValue);
  }

  public HSVLUTInterpolator(short[] hueAdjustValue,
                            byte[] saturationAdjustValue,
                            byte[] valueAdjustValue
      ) {
    integerMode = true;
    init(hueAdjustValue, saturationAdjustValue, valueAdjustValue);
  }

  public HSVLUTInterpolator(TuneParameter tuneParameter) {
    integerMode = true;
    init(tuneParameter.getHueAdjustValue(),
         tuneParameter.getSaturationAdjustValue(),
         tuneParameter.getValueAdjustValue());
  }

  private boolean integerMode = false;
//  public short getHue(short hue) {
//    double doubleHue = getHueAdjustDouble(hue);
//    double normalizedHue = doubleHue / 360;
//    short shortHue = (short) Math.round(normalizedHue * 768.);
//    return shortHue;
//  }

  /**
   *
   * @param shortHue short
   * @return double
   * @deprecated
   */
  private static double getDoubleHue(short shortHue) {
    return -1;
//    return ( (double) shortHue) / HSV.AUO.PIECE_OF_HUE * 360.;
  }

  public short getSaturationAdjust(short hue) {
    return getSaturationAdjust(getDoubleHue(hue));
  }

  public short getValueAdjust(short hue) {
    return getValueAdjust(getDoubleHue(hue));
  }

  /**
   *
   * @param hue double
   * @return short 11bit
   */
  public short getSaturationAdjust(double hue) {
    //11bit
    double doubleSaturationAdjust = getSaturationAdjustDouble(hue);
    double normalizedSaturationAdjust = doubleSaturationAdjust / 64.;
    double piece = 1. / Math.pow(2, 11);
    double byteSaturationAdjust = normalizedSaturationAdjust / piece;
    return (short) Math.round(byteSaturationAdjust);
  }

  public double getHueAdjustDouble(double hue) {
    double hueAdjust = hueAdjustmentLUT.getValue(hue);
    hueAdjust = hueAdjust % 768;
    return hueAdjust;
  }

  public double getQuantizationHueAdjustDouble(double hue) {
    //0~768
    double hueAdjust = getHueAdjustDouble(hue);
    hueAdjust = (int) Math.round(hueAdjust * Math.pow(2, 3));
    hueAdjust = hueAdjust / Math.pow(2, 3);
    return hueAdjust;
  }

  public double getSaturationAdjustDouble(double hue) {
    double doubleSaturationAdjust = saturationAdjustmentLUT.getValue(hue);
    return doubleSaturationAdjust;
  }

  public double getValueAdjustDouble(double hue) {
    double doubleValueAdjust = valueAdjustmentLUT.getValue(hue);
    return doubleValueAdjust;
  }

  /**
   *
   * @param hue double
   * @return short 14bit + 1 sign bit
   */
  public short getValueAdjust(double hue) {
    double doubleValueAdjust = getValueAdjustDouble(hue);
    double normalizedSValueAdjust = doubleValueAdjust / 64.;
    double piece = 1. / Math.pow(2, 15);
    double byteValueAdjust = normalizedSValueAdjust / piece;
    return (short) Math.round(byteValueAdjust);
  }

  private Interpolation1DLUT hueAdjustmentLUT = null;
  private Interpolation1DLUT saturationAdjustmentLUT = null;
  private Interpolation1DLUT valueAdjustmentLUT = null;
  private void init(double[] hueAdjustValue,
                    double[] saturationAdjustValue,
                    double[] valueAdjustValue) {
    hueAdjustmentLUT = getInterpolation1DLUT(hueAdjustValue, false);
    saturationAdjustmentLUT = getInterpolation1DLUT(saturationAdjustValue, false);
    valueAdjustmentLUT = getInterpolation1DLUT(valueAdjustValue, false);
  }

  private void init(short[] hueAdjustValue,
                    byte[] saturationAdjustValue,
                    byte[] valueAdjustValue) {
    hueAdjustmentLUT = getInterpolation1DLUT(DoubleArray.fromShortArray(
        hueAdjustValue), true);
    saturationAdjustmentLUT = getInterpolation1DLUT(DoubleArray.fromByteArray(
        saturationAdjustValue), false);
    valueAdjustmentLUT = getInterpolation1DLUT(DoubleArray.fromByteArray(
        valueAdjustValue), false);
  }

  public static short[] ManualHueLut = new short[] {
      0, 1, 95, 96, 97, 191, 192,
      193, 287, 288, 289, 383, 384,
      385, 479, 480, 481, 575, 576,
      577, 671, 672, 673, 736};

  public static byte[] ManualSaturationLut = new byte[] {
      +63, -63, +61, -20,
      +63, -63, +61, -20,
      +63, -63, +61, -20,
      +63, -63, +61, -20,
      +63, -63, +61, -20,
      +63, -63, +61, -20};

  public static byte[] ManualValueLut = new byte[] {
      -63, 20, -2, +54,
      -63, 20, -2, +54,
      -63, 20, -2, +54,
      -63, 20, -2, +54,
      -63, 20, -2, +54,
      -63, 20, -2, +54, };

  public final static HSVLUTInterpolator getInstance(boolean manual) {
    short[] hHue = null;
    byte[] sLut = null;
    byte[] vLut = null;
    if (manual) {
      hHue = ManualHueLut;
      sLut = ManualSaturationLut;
      vLut = ManualValueLut;
    }
    else {
      IntegerSaturationFormula formula = new IntegerSaturationFormula( (byte)
          7, 4);
      HSVAdjustProducer producer = Util.produceHSVAdjustProducer();
      double[] saturationAdjustArray = producer.getSaturationAdjustArray(
          formula);
//      producer.getHueAdjustArray();
      double[] valueAdjustArray = producer.getValueAdjustArray();

      hHue = new short[] {
          0, 60, 120, 180, 240, 300};
      sLut = new byte[6];
      vLut = new byte[6];

      for (int x = 0; x < 6; x++) {
        sLut[x] = (byte) saturationAdjustArray[x];
        vLut[x] = (byte) valueAdjustArray[x];
      }
    }
    HSVLUTInterpolator lut = new HSVLUTInterpolator(hHue, sLut, vLut);
    return lut;

  }

  public static void main(String[] args) {
//    interpolateTest(args);
    dump(args);
  }

  public static void dump(String[] args) {
    RGB rgb1 = new RGB(106, 92, 84);
//    HSV hsv1 = new HSV(rgb1);
//    short[] hsvValues1 = HSV.AUO.toHSVValues(hsv1);
    AUOHSV auoHSV = new AUOHSV(rgb1);
    short[] hsvValues1 = auoHSV.getValues();
    int h1 = hsvValues1[0] % 256;

    RGB rgb2 = new RGB(84, 92, 106);
    AUOHSV auoHSV2 = new AUOHSV(rgb2);
    short[] hsvValues2 = auoHSV2.getValues();
//    HSV hsv2 = new HSV(rgb2);
//    short[] hsvValues2 = HSV.AUO.toHSVValues(hsv2);
    int h2 = hsvValues2[0] % 256;
    int x = 1;
  }

  public static void interpolateTest(String[] args) {
//    short[] hHue = new short[] {
//        0, 1, 95, 96, 97, 191, 192,
//        193, 287, 288, 289, 383, 384,
//        385, 479, 480, 481, 575, 576,
//        577, 671, 672, 673, 736};
//    byte[] sLut = new byte[] {
//        +63, -63, +61, -20,
//        +63, -63, +61, -20,
//        +63, -63, +61, -20,
//        +63, -63, +61, -20,
//        +63, -63, +61, -20,
//        +63, -63, +61, -20};
////    byte[] sLut = new byte[] {
////        +63, 0, -63, 0,
////        +63, 0, -63, 0,
////        +63, 0, -63, 0,
////        +63, 0, -63, 0,
////        +63, 0, -63, 0,
////        +63, 0, -63, 0, };
//
//    byte[] vLut = new byte[] {
//        -63, 20, -2, +54,
//        -63, 20, -2, +54,
//        -63, 20, -2, +54,
//        -63, 20, -2, +54,
//        -63, 20, -2, +54,
//        -63, 20, -2, +54, };

    Plot2D plot = Plot2D.getInstance();
    HSVLUTInterpolator lut = HSVLUTInterpolator.getInstance(false);

    RGB rgb = new RGB(RGB.ColorSpace.sRGB, new double[] {92, 106, 84},
                      RGB.MaxValue.Double255);
//    double[] rgbValues10bit = rgb.getValues(new double[3],
//                                            RGB.MaxValue.Int10Bit);
//    HSV hsv = new HSV(rgb);
    AUOHSV hsv = new AUOHSV(rgb);
    //======================================================================
    // 原始HSV
    //======================================================================
    //轉成auo的hsv格式
    short[] auoHSVValues = hsv.getValues();

//    HSV.AUO.toHSVValues(hsv, auoHSVValues);
    short saturationAdjust = lut.getSaturationAdjust(auoHSVValues[0]);
    short valueAdjust = lut.getValueAdjust(auoHSVValues[0]);
    short valueAdjust10bit = (short) (valueAdjust >> 5);
//    lut.getValueAdjust()
//    for (double x = 0; x < 360; x++) {
//      short s = lut.getSaturationAdjust(x);
//      plot.addCacheScatterLinePlot("", x, s);
//    }
//    plot.setVisible();
  }

  public static Interpolation1DLUT getInterpolation1DLUT(double[]
      adjustValue, boolean hueAdjust) {

    int size = adjustValue.length;
    double[] original = new double[size + 1];
    double[] processAdjustValue = DoubleArray.copy(adjustValue);

    boolean reverse = false;
    for (int x = 0; x < size; x++) {

      if (reverse) {
        double result = processAdjustValue[x] + 768;
        original[x] = result < (768 * 2) ? result : processAdjustValue[x];
      }
      else {
        original[x] = processAdjustValue[x];
      }
      if (true == hueAdjust && (x < size - 1) &&
          processAdjustValue[x] > processAdjustValue[x + 1]) {
        reverse = true;
      }

    }
    //最後一筆填第一筆的值
    original[size] = reverse ? processAdjustValue[0] + 768 :
        processAdjustValue[0];

    int size_1 = size + 1;
    double[] hueArray = new double[size_1];
    int eachHue = 360 / size;
    for (int x = 0; x < size_1; x++) {
      hueArray[x] = x * eachHue;
    }
    Interpolation1DLUT lut = new Interpolation1DLUT(hueArray, original,
        Interpolation1DLUT.Algo.LINEAR);

    double[] keys = new double[25];
    double[] values = new double[25];
    for (int x = 0; x < 25; x++) {
      double hue = x * 15;
      keys[x] = hue;
      double s = Math.round(lut.getValue(hue));
      values[x] = s;
    }

    Interpolation1DLUT lut2 = new Interpolation1DLUT(keys, values,
        Interpolation1DLUT.Algo.LINEAR);
    return lut2;
  }
}
