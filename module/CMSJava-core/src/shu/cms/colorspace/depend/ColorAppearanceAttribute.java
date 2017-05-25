package shu.cms.colorspace.depend;

import shu.cms.colorspace.depend.RGBBase.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 提供色外貌三屬性 明度 彩度 色相 的基礎功能
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ColorAppearanceAttribute
    extends ColorAppearanceBase {

  public double Luma;
  public double Saturation;
  public double Hue;

  public String[] getBandNames() {
    return new String[] {
        "Luma", "Saturation", "Hue"};
  }

  private final static int[] lshIndex = new int[] {
      0, 1, 2};

  /**
   * ColorAttribute
   *
   * @param colorSpace RGBColorSpace
   * @param values double[]
   */
  public ColorAppearanceAttribute(ColorSpace colorSpace, double[] values) {
    super(colorSpace, values, lshIndex);
  }

  public ColorAppearanceAttribute(ColorAppearanceBase colorAppearanceBase) {
    super(colorAppearanceBase.getRGBColorSpace(),
          colorAppearanceBase.getLSHValues(), lshIndex);
  }

  public ColorAppearanceAttribute(OpponentColorBase
                                  opponentColorBase) {
    super(opponentColorBase.getRGBColorSpace(),
          opponentColorBase.getPolarCoordinatesValues(), lshIndex);
  }

  /**
   * _fromRGB
   *
   * @param rgb RGB
   * @return RGBBase
   */
  protected double[] _fromRGB(RGB rgb) {
    throw new UnsupportedOperationException();
  }

  /**
   * _setValues
   *
   * @param values double[]
   */
  protected void _setValues(double[] values) {
    Luma = values[0];
    Saturation = values[1];
    Hue = values[2];
  }

  /**
   * getValues
   *
   * @param values double[]
   * @return double[]
   */
  protected double[] _getValues(double[] values) {
    values[0] = Luma;
    values[1] = Saturation;
    values[2] = Hue;
    return values;
  }

  /**
   * toRGB
   *
   * @return RGB
   */
  public RGB toRGB() {
    throw new UnsupportedOperationException();
  }

  public static void main(String[] args) {
    HSL hsl = new HSL(RGB.ColorSpace.sRGB, new double[] {125, .5, .6});
    ColorAppearanceAttribute caa = new ColorAppearanceAttribute(hsl);
    System.out.println(caa);
    OpponentColorAttribute oca = new OpponentColorAttribute(hsl);
    OpponentColorAttribute oca2 = new OpponentColorAttribute(caa);
    System.out.println(oca);
    System.out.println(oca2);
    ColorAppearanceAttribute caa2 = new ColorAppearanceAttribute(oca2);
    System.out.println(caa2);
//      HSL hsl2 =new HSL()
  }
}
