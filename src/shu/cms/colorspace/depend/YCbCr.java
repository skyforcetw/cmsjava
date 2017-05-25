package shu.cms.colorspace.depend;

import shu.cms.colorspace.depend.RGBBase.*;

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

  private ITU_R itu_r = ITU_R.BT709;

  public double Y;
  public double Cb;
  public double Cr;
  private final static int[] lrgybIndex = new int[] {
      0, 2, 1};

  public YCbCr(RGB rgb) {
    super(rgb, lrgybIndex);
  }

  public YCbCr(ColorSpace colorSpace) {
    super(colorSpace, lrgybIndex);
  }

  public YCbCr(ColorSpace colorSpace, double[] values) {
    super(colorSpace, values, lrgybIndex);
  }

  public YCbCr(ColorAppearanceBase colorAppearanceBase) {
    super(colorAppearanceBase);
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
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
    double[] yccValues = new double[3];
    yccValues[0] = itu_r.Kr * rgbValues[0] + itu_r.Kg * rgbValues[1] +
        itu_r.Kb * rgbValues[2];
    yccValues[1] = (1. / 2.) * (rgbValues[2] - yccValues[0]) / (1. - itu_r.Kb);
    yccValues[2] = (1. / 2.) * (rgbValues[0] - yccValues[0]) / (1. - itu_r.Kr);

    return yccValues;
  }

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
    return null;
  }

  public static void main(String[] args) {

//    YCbCr ycc = new YCbCr(RGB.RGBColorSpace.sRGB,null
    YCbCr ycc = new YCbCr(new RGB(RGB.ColorSpace.SMPTE_C, new int[] {13, 53,
                                  211}));
    System.out.println(ycc);

    YCbCr ycc2 = new YCbCr(new RGB(RGB.ColorSpace.sRGB, new int[] {13, 53,
                                   211}));
    System.out.println(ycc2);

  }

  public String[] getBandNames() {
    return new String[] {
        "Y", "Cb", "Cr"};
  }

}
