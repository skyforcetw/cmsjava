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
public abstract class ColorAppearanceBase
    extends RGBBasis {
  public ColorAppearanceBase(RGB rgb, int[] lshIndex) {
    super(rgb);
    this.lshIndex = lshIndex;
  }

  public ColorAppearanceBase(OpponentColorBase opponentColorBase) {
    super(opponentColorBase.getRGBColorSpace(),
          opponentColorBase.getPolarCoordinatesValues());
    this.lshIndex = new int[] {
        0, 1, 2};
  }

  public ColorAppearanceBase(ColorSpace colorSpace, int[] lshIndex) {
    super(colorSpace);
    this.lshIndex = lshIndex;
  }

  public ColorAppearanceBase(ColorSpace colorSpace, double[] values,
                             int[] lshIndex) {
    super(colorSpace, values);
    this.lshIndex = lshIndex;
  }

  public ColorAppearanceBase(ColorSpace colorSpace, double[] values,
                             MaxValue[] maxValues,
                             int[] lshIndex) {
    super(colorSpace, values, maxValues);
    this.lshIndex = lshIndex;
  }

  private int[] lshIndex;
  public final double[] getLSHValues() {
    double[] values = this.getValues();
    double[] result = new double[] {
        values[lshIndex[0]], values[lshIndex[1]], values[lshIndex[2]]};
    return result;
  }

  public final void setLSHValues(double[] LSHValues) {
    this.setValues(new double[] {LSHValues[lshIndex[0]],
                   LSHValues[lshIndex[1]], LSHValues[lshIndex[2]]});
  }

  public double[] getCartesianCoordinatesValues() {
    return polar2cartesianCoordinatesValues(this.getLSHValues());
  }
}
