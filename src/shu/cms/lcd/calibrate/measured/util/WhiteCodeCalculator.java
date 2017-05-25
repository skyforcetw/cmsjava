package shu.cms.lcd.calibrate.measured.util;

import java.util.*;

import shu.cms.colorspace.depend.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * white code �P rgb code�������ഫ�p��.
 * �ҿת�white code���N�O�u��g code
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class WhiteCodeCalculator {
  /**
   * ���orgbArray��white code, �]�N�Ogreen��code
   * @param rgbArray RGB[]
   * @return double[]
   */
  public final static double[] getWhitecodeArray(RGB[] rgbArray) {
    int size = rgbArray.length;
    double[] gArray = new double[size];
    for (int x = 0; x < size; x++) {
      RGB rgb = rgbArray[x];
      gArray[x] = rgb.getValue(RGBBase.Channel.G, RGB.MaxValue.Double255);
    }
    return gArray;

  }

  private RGB[] originalRGBArray;
  private RGB test;
  public WhiteCodeCalculator(RGB[] rgbArray) {
    this.originalRGBArray = rgbArray;
    test = (RGB) rgbArray[0].clone();
  }

  private double[] rgbValues = new double[3];

  /**
   * �qwhitecode, �٭�^RGB Array.
   * �@�k�O�q�p��Xwhitecode�PcpcodeRGBArray��G���t��. �M��N�t���P�ɥ[�^RGB.
   * @param whitecode double[]
   * @param maxValue MaxValue
   * @return RGB[]
   */
  public RGB[] getRGBArray(double[] whitecode, RGB.MaxValue maxValue) {
    RGB[] rgbArray = Arrays.copyOf(originalRGBArray, originalRGBArray.length);
    if (rgbArray.length != whitecode.length) {
      throw new IllegalArgumentException(
          "rgbArray.length != whitecode.length");
    }
    int size = rgbArray.length;

    for (int x = 1; x < size; x++) {
      RGB rgb = (RGB) rgbArray[x].clone();
      RGB prergb = rgbArray[x - 1];
      double code = whitecode[x];
      rgb.getValues(rgbValues, RGB.MaxValue.Double255);
      double delta = code - rgbValues[1];
//      System.out.println(delta);
      rgbValues[0] += delta;
      rgbValues[1] += delta;
      rgbValues[2] += delta;
      test.setValues(rgbValues, RGB.MaxValue.Double255);
      test.quantization(maxValue);

      if (test.isLegal() && !test.equals(rgb) && test.compareTo(prergb) >= 0) {
        //�ץ��᪺�ȭn�X�z(>=0) , �n��e�@��rgb�Ȥj
        rgb.setValues(rgbValues, RGB.MaxValue.Double255);
        rgb.quantization(maxValue);
        rgbArray[x] = rgb;
      }
//      rgbArray[x] = getRGB(whitecode[x], maxValue, x);
    }
    return rgbArray;
  }

  /**
   *
   * @param whitecode double
   * @param maxValue MaxValue
   * @param index int
   * @return RGB
   * @deprecated
   */
  public RGB getRGB(double whitecode, RGB.MaxValue maxValue, int index) {
    RGB rgb = (RGB) originalRGBArray[index].clone();
    RGB prergb = originalRGBArray[index - 1];
    double code = whitecode;
    rgb.getValues(rgbValues, RGB.MaxValue.Double255);
    double delta = code - rgbValues[1];
    rgbValues[0] += delta;
    rgbValues[1] += delta;
    rgbValues[2] += delta;
    test.setValues(rgbValues, RGB.MaxValue.Double255);
    test.quantization(maxValue);

    if (test.isLegal() && !test.equals(rgb) && test.compareTo(prergb) >= 0) {
      //�ץ��᪺�ȭn�X�z(>=0) , �n��e�@��rgb�Ȥj
      rgb.setValues(rgbValues, RGB.MaxValue.Double255);
      rgb.quantization(maxValue);
      return rgb;
    }
    else {
      return null;
    }
  }
}
