package shu.cms.colorspace.depend;

import shu.cms.colorspace.depend.RGBBase.ColorSpace;
import shu.math.array.DoubleArray;

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
public class RYGCBM
    extends RGBBasis {
  public double Rm, Rg, Gr, Gb, Bg, Bm, Yr, Yg, Cg, Cb, Mr, Mb;

  public RYGCBM(RGB.ColorSpace colorSpace, double[] rgbycmValues) {
    super(colorSpace, rgbycmValues);
  }

  /**
   * _getValues
   *
   * @param values double[]
   * @return double[]
   */
  protected double[] _getValues(double[] values) {
    values[0] = Rm;
    values[1] = Rg;
    values[2] = Yr;
    values[3] = Yg;
    values[4] = Gr;
    values[5] = Gb;
    values[6] = Cg;
    values[7] = Cb;
    values[8] = Bg;
    values[9] = Bm;
    values[10] = Mb;
    values[11] = Mr;
    return values;
  }

  /**
   * _setValues
   *
   * @param values double[]
   */
  protected void _setValues(double[] values) {
    Rm = values[0];
    Rg = values[1];
    Yr = values[2];
    Yg = values[3];
    Gr = values[4];
    Gb = values[5];
    Cg = values[6];
    Cb = values[7];
    Bg = values[8];
    Bm = values[9];
    Mb = values[10];
    Mr = values[11];
  }

  /**
   * getBandNames
   *
   * @return String[]
   */
  public String[] getBandNames() {
    return new String[] {
        "Rm", "Rg", "Yr", "Yg", "Gr", "Gb", "Cg", "Cb", "Bg", "Bm", "Mb", "Mr"};
  }

  /**
   * getNumberBands
   *
   * @return int
   */
  protected int getNumberBands() {
    return 12;
  }

  /**
   * RGBYCM
   *
   * @param rgb RGB
   */
  public RYGCBM(RGB rgb) {
    super(rgb);
  }

  /**
   * RGBYCM
   *
   * @param colorSpace ColorSpace
   */
  public RYGCBM(ColorSpace colorSpace) {
    super(colorSpace);
  }

  /**
   * _fromRGB
   *
   * @param rgb RGB
   * @return RGBBasis
   */
  protected double[] _fromRGB(RGB rgb) {
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
    return fromRGBValues(rgbValues);
  }

  public final static double[] fromRGBValues(double[] rgbValues) {
    double[] rygcbmValues = new double[12];
    //"Rm", "Rg", "Yr", "Yg", "Gr", "Gb", "Cg", "Cb", "Bg", "Bm", "Mb", "Mr"
    rygcbmValues[0] = rgbValues[0] / 4; //Rm
    rygcbmValues[1] = rgbValues[0] / 4; //Rg
    rygcbmValues[2] = rgbValues[0] / 4; //Yr
    rygcbmValues[3] = rgbValues[1] / 4; //Yg
    rygcbmValues[4] = rgbValues[1] / 4; //Gr
    rygcbmValues[5] = rgbValues[1] / 4; //Gb
    rygcbmValues[6] = rgbValues[1] / 4; //Cg
    rygcbmValues[7] = rgbValues[2] / 4; //Cb
    rygcbmValues[8] = rgbValues[2] / 4; //Bg
    rygcbmValues[9] = rgbValues[2] / 4; //Bm
    rygcbmValues[10] = rgbValues[2] / 4; //Bm
    rygcbmValues[11] = rgbValues[0] / 4; //Bm
    rygcbmValues = DoubleArray.times(rygcbmValues, 4);

    return rygcbmValues;

  }

  /**
   * toRGB
   *
   * @return RGB
   */
  public RGB toRGB() {
    RGB rgb = new RGB(this.rgbColorSpace);
    rgb.setValues(toRGBValues(this.getValues()));
    return rgb;
  }

  public final static double[] toRGBValues(double[] rygcbmValues) {
    double[] rgbValues = new double[3];
    //"R", "Yr", "Yg", "G", "Cg", "Cb", "B", "Mb", "Mr"
    rgbValues[0] = rygcbmValues[0] + rygcbmValues[1] + rygcbmValues[2] +
        rygcbmValues[11];
    rgbValues[1] = rygcbmValues[3] + rygcbmValues[4] + rygcbmValues[5] +
        rygcbmValues[6];
    rgbValues[2] = rygcbmValues[7] + rygcbmValues[8] + rygcbmValues[9] +
        rygcbmValues[10];
    rgbValues = DoubleArray.times(rgbValues, 0.25);
    return rgbValues;
  }

  public static void main(String[] args) {
    for (int h = 0; h < 360; h += 30) {
      HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h, 100, 100});
      RGB rgb = hsv.toRGB();
      RYGCBM rgbycm = new RYGCBM(rgb);
      System.out.println(rgb + " " + hsv + " " + rgbycm);
      System.out.println(rgbycm.toRGB());
    }
    RGB rgb = new RGB(255, 255, 255);
    RYGCBM rgbycm = new RYGCBM(rgb);
    System.out.println(rgb + " " + rgbycm);
    System.out.println(rgbycm.toRGB());
  }
}
