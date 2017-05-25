package auo.cms.hsv.autotune;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 用來記錄一組Hue的調整值
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class SingleHueAdjustValue {
  public SingleHueAdjustValue(short hueAdjustValue,
                              byte saturationAdjustValue,
                              byte valueAdjustValue) {
    this.hueAdjustValue = hueAdjustValue;
    this.saturationAdjustValue = saturationAdjustValue;
    this.valueAdjustValue = valueAdjustValue;
  }

  public double[] getAdjustDoubleArray() {
    return new double[] {
        hueAdjustValue, saturationAdjustValue, valueAdjustValue};
  }

  public short[] getAdjustShortArray() {
    return new short[] {
        hueAdjustValue, saturationAdjustValue, valueAdjustValue};
  }

  public double getDoubleHueAdjustValue() {
    return hueAdjustValue / 768. * 360;
  }

  public short hueAdjustValue;
  public byte saturationAdjustValue;
  public byte valueAdjustValue;
  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    return hueAdjustValue + " " + saturationAdjustValue + " " +
        valueAdjustValue;
  }
}
