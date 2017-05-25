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
  //12位元
  TwelveBits(RGBBase.MaxValue.Int12Bit),
  //10位元
  TenBits(RGBBase.MaxValue.Int10Bit),
  //9位元
  NineBits(RGBBase.MaxValue.Int9Bit),
  //8位元
  EightBits(RGBBase.MaxValue.Int8Bit);

  public RGBBase.MaxValue getMaxValue() {
    return maxValue;
  }

  MeasureBits(RGBBase.MaxValue maxValue) {
    this.maxValue = maxValue;
  }

  private RGBBase.MaxValue maxValue;
}
