package shu.cms.colorspace.depend;

import shu.cms.colorspace.depend.RGBBase.*;

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
public class OpponentColorAttribute
    extends OpponentColorBase {

  public double Luma;
  public double Rg;
  public double Yb;
  private final static int[] lrgybIndex = new int[] {
      0, 1, 2};
  private RGB rgb;
  public OpponentColorAttribute(ColorSpace colorSpace, double[] values) {
    super(colorSpace, values, lrgybIndex);

  }

  public OpponentColorAttribute(OpponentColorBase opponentColorBase) {
    super(opponentColorBase.getRGBColorSpace(),
          opponentColorBase.getLRgYbValues(), lrgybIndex);
    rgb = opponentColorBase.toRGB();
  }

  public OpponentColorAttribute(ColorAppearanceBase colorAppearanceBase) {
    super(colorAppearanceBase.getRGBColorSpace(),
          colorAppearanceBase.getCartesianCoordinatesValues(), lrgybIndex);
    rgb = colorAppearanceBase.toRGB();
  }

  /**
   * _fromRGB
   *
   * @param rgb RGB
   * @return RGBBasis
   */
  protected double[] _fromRGB(RGB rgb) {
    throw new UnsupportedOperationException();
  }

  /**
   * _getValues
   *
   * @param values double[]
   * @return double[]
   */
  protected double[] _getValues(double[] values) {
    return new double[] {
        Luma, Rg, Yb};
  }

  /**
   * _setValues
   *
   * @param values double[]
   */
  protected void _setValues(double[] values) {
    this.Luma = values[0];
    this.Rg = values[1];
    this.Yb = values[2];
  }

  public String[] getBandNames() {
    return new String[] {
        "Luma", "Rg", "Yb"};
  }

  /**
   * toRGB
   *
   * @return RGB
   */
  public RGB toRGB() {
    return rgb;
  }
}
