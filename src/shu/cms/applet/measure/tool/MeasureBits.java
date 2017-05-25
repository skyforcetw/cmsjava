package shu.cms.applet.measure.tool;

import shu.cms.colorspace.depend.*;

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
public enum MeasureBits {
  //12�줸
  TwelveBits(RGBBase.MaxValue.Int12Bit),
  //10�줸
  TenBits(RGBBase.MaxValue.Int10Bit),
  //9�줸
  NineBits(RGBBase.MaxValue.Int9Bit),
  //8�줸
  EightBits(RGBBase.MaxValue.Int8Bit);

  public RGBBase.MaxValue getMaxValue() {
    return maxValue;
  }

  MeasureBits(RGBBase.MaxValue maxValue) {
    this.maxValue = maxValue;
  }

  private RGBBase.MaxValue maxValue;
}
