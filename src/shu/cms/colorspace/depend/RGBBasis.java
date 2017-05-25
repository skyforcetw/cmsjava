package shu.cms.colorspace.depend;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 提供其他基於RGB空間的色彩空間的基礎建設
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class RGBBasis
    extends DeviceDependentSpace {

  private MaxValue[] maxValues;

  public RGBBasis(RGB rgb) {
    this(rgb.rgbColorSpace, null);
    this.setValues(_fromRGB(rgb));
  }

  public RGBBasis(RGB.ColorSpace colorSpace) {
    this(colorSpace, null);
  }

  public RGBBasis(RGB.ColorSpace colorSpace, double[] values) {
    this(colorSpace, values, null);
//    this.rgbColorSpace = colorSpace;
//    if (values != null) {
//      this.setValues(values);
//    }
  }

  public RGBBasis(RGB.ColorSpace colorSpace, double[] values,
                  MaxValue[] maxValues) {
    this.rgbColorSpace = colorSpace;
    if (values != null) {
      this.setValues(values);
    }

    if (maxValues != null) {
      if (maxValues.length != this.getNumberBands()) {
        throw new IllegalArgumentException(
            "maxValues.length != this.getNumberBands()");
      }
      this.maxValues = maxValues;
    }
  }

  public final MaxValue getMaxValue(int index) {
    return maxValues[index];
  }

  public RGB.ColorSpace getRGBColorSpace() {
    return rgbColorSpace;
  }

  protected RGB.ColorSpace rgbColorSpace;
  public abstract RGB toRGB();

  //protected abstract RGBBasis _fromRGB(RGB rgb);
  protected abstract double[] _fromRGB(RGB rgb);

  /**
   * getNumberBands
   *
   * @return int
   */
  protected int getNumberBands() {
    return 3;
  }

  /**
   * Creates and returns a copy of this object.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException if the object's class does not support
   *   the <code>Cloneable</code> interface. Subclasses that override the
   *   <code>clone</code> method can also throw this exception to indicate
   *   that an instance cannot be cloned.
   */
  public Object clone() {
    RGBBasis rgbBase = (RGBBasis)super.clone();
    rgbBase.rgbColorSpace = this.rgbColorSpace;
    return rgbBase;
  }

  public double getValue(int index, MaxValue maxValue) {
    double[] values = this.getValues();
    changeMaxValue(values, this.getMaxValue(index), maxValue, false);
    return values[index];
  }

  public void setValue(int index, double value, MaxValue type) {
    double[] values = new double[] {
        value};
    MaxValue thisMaxValue = getMaxValue(index);
    if (type != thisMaxValue) {
      changeMaxValue(values, type, thisMaxValue, false);
    }
    this.setValue(index, values[0]);
  }

  public void setValue(int index, double value) {
    double[] values = this.getValues();
    values[index] = value;
    this.setValues(values);
  }

}
