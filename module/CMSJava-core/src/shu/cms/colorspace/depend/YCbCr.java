package shu.cms.colorspace.depend;

import shu.cms.colorspace.depend.RGBBase.*;
import shu.cms.plot.*;
import java.awt.Color;

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
public class YCbCr
    extends OpponentColorBase {

  private ITU_R itu_r;

  public double Y;
  public double Cb;
  public double Cr;
  private final static int[] lrgybIndex = new int[] {
      0, 2, 1};

  public YCbCr(RGB rgb) {
    super(rgb, lrgybIndex);
    this.itu_r = ITU_R.getITU_R(rgb.getRGBColorSpace());
  }

  public YCbCr(ColorSpace colorSpace) {
    super(colorSpace, lrgybIndex);
    this.itu_r = ITU_R.getITU_R(colorSpace);
  }

  public YCbCr(ColorSpace colorSpace, double[] values) {
    super(colorSpace, values, lrgybIndex);
    this.itu_r = ITU_R.getITU_R(colorSpace);
  }

  public YCbCr(ColorAppearanceBase colorAppearanceBase) {
    super(colorAppearanceBase);
    this.itu_r = ITU_R.getITU_R(colorAppearanceBase.getRGBColorSpace());
  }

  /**
   * _fromRGB
   *
   * @param rgb RGB
   * @return RGBBase
   */
  protected double[] _fromRGB(RGB rgb) {
    RGB.ColorSpace colorSpace = rgb.getRGBColorSpace();
    ITU_R itu_r = ITU_R.getITU_R(colorSpace);
    return fromRGB(rgb, itu_r);

  }

  final static boolean UseSimplify2 = true;

  public static final double[] fromRGB(RGB rgb, ITU_R itu_r) {

    if (itu_r != ITU_R.Simplify) {

      double[][] ituMatrix = itu_r.getFromRGBMatrix();
      double[] rgbValues = rgb.getValues(new double[3],
                                         RGB.MaxValue.Double255);
      double[] yccValues = shu.math.array.DoubleArray.times(ituMatrix,
          rgbValues);
      yccValues[1] += 128;
      yccValues[2] += 128;

      return yccValues;
    }
    else {
      double[] rgbValues = rgb.getValues(new double[3],
                                         RGB.MaxValue.Double255);
      double[] yccValues = new double[3];
      yccValues[0] = (rgbValues[0] + 2 * rgbValues[1] + rgbValues[2]) /
          4.;
      if (!UseSimplify2) {
        //Simplify1
        yccValues[1] = (rgbValues[1] - rgbValues[2]) / 2. +
            128;
        yccValues[2] = (rgbValues[1] - rgbValues[0]) / 2. +
            128;
      }
      else {
        //Simplify2
        yccValues[1] = (rgbValues[2] - yccValues[0]) / 2. + 128;
        yccValues[2] = (rgbValues[0] - yccValues[0]) / 2. + 128;
      }
      return yccValues;
    }
  }

//    public static final double[][] getFromRGBMatrix(double Kr, double Kb) {
//        double[][] matrix = new double[3][3];
//        double Kg = 1 - Kr - Kb;
//        matrix[0][0] = Kr;
//        matrix[0][1] = Kg;
//        matrix[0][2] = Kb;
//
//        matrix[1][0] = -Kr / 2 * (1 - Kb);
//        matrix[1][1] = -Kg / 2 * (1 - Kb);
//        matrix[1][2] = 0.5;
//
//        matrix[2][0] = 0.5;
//        matrix[2][1] = -Kg / 2 * (1 - Kr);
//        matrix[2][2] = -Kb / 2 * (1 - Kr);
//        return matrix;
//    }

  /**
   * _getValues
   *
   * @param values double[]
   * @return double[]
   */
  protected double[] _getValues(double[] values) {
    values[0] = Y;
    values[1] = Cb;
    values[2] = Cr;
    return values;
  }

  /**
   * _setValues
   *
   * @param values double[]
   */
  protected void _setValues(double[] values) {
    Y = values[0];
    Cb = values[1];
    Cr = values[2];

  }

  /**
   * toRGB
   *
   * @return RGB
   * @todo Implement this shu.cms.colorspace.RGBBase method
   */
  public RGB toRGB() {

    if (this.itu_r != ITU_R.Simplify) {
      double[][] ituMatrix = itu_r.getToRGBMatrix();
      double[] yccValues = this.getValues();
      yccValues[1] -= 128;
      yccValues[2] -= 128;
      double[] rgbValues = shu.math.array.DoubleArray.times(ituMatrix,
          yccValues);
      RGB rgb = new RGB(this.rgbColorSpace, rgbValues,
                        RGB.MaxValue.Double255);

      return rgb;
    }
    else {
      double[] rgbValues = null;
      if (!UseSimplify2) {
        //Simplify1
        double g = (2 * Y + (Cb - 128) + (Cr - 128)) / 2.;
        double r = g - 2 * (Cr - 128);
        double b = g - 2 * (Cb - 128);
        rgbValues = new double[] {
            r, g, b};

      }
      else {
        //Simplify2
//        double r = 2 * (Cr - 128) * (1 - 0.25) + Y;
//        double g = Y - (Cb - 128) - (Cr - 128);
//        double b = 2 * (Cb - 128) * (1 - 0.25) + Y;
        double r = 2 * (Cr - 128) + Y;
        double g = Y - (Cb - 128) - (Cr - 128);
        double b = 2 * (Cb - 128) + Y;

        rgbValues = new double[] {
            r, g, b};
      }

      RGB rgb = new RGB(this.rgbColorSpace, rgbValues,
                        RGB.MaxValue.Double255);

      return rgb;

    }
  }

  public static void main(String[] args) {
//        showPlot(null);
//        diffcompare(null);
  }

  public String[] getBandNames() {
    return new String[] {
        "Y", "Cb", "Cr"};
  }

}
