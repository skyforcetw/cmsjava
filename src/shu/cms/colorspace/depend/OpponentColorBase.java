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
public abstract class OpponentColorBase
    extends RGBBasis {
  public OpponentColorBase(RGB rgb, int[] lrgybIndex) {
    super(rgb);
    this.lrgybIndex = lrgybIndex;
  }

  public OpponentColorBase(ColorSpace colorSpace, int[] lrgybIndex) {
    super(colorSpace);
    this.lrgybIndex = lrgybIndex;
  }

  public OpponentColorBase(ColorSpace colorSpace, double[] values,
                           int[] lrgybIndex) {
    super(colorSpace, values);
    this.lrgybIndex = lrgybIndex;
  }

  public OpponentColorBase(ColorAppearanceBase colorAppearanceBase) {
    super(colorAppearanceBase.getRGBColorSpace(),
          colorAppearanceBase.getCartesianCoordinatesValues());
    this.lrgybIndex = new int[] {
        0, 1, 2};
  }

  private int[] lrgybIndex;
  public final double[] getLRgYbValues() {
    double[] values = this.getValues();
    double[] result = new double[] {
        values[lrgybIndex[0]], values[lrgybIndex[1]], values[lrgybIndex[2]]};
    return result;
  }

  public final void setLRgYbValues(double[] LRgYbValues) {
    this.setValues(new double[] {LRgYbValues[lrgybIndex[0]],
                   LRgYbValues[lrgybIndex[1]], LRgYbValues[lrgybIndex[2]]});
  }

  public double[] getPolarCoordinatesValues() {
    return cartesian2polarCoordinatesValues(this.getLRgYbValues());
  }
}
