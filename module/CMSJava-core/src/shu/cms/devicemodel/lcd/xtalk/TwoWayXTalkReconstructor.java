package shu.cms.devicemodel.lcd.xtalk;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.devicemodel.lcd.util.*;

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
public class TwoWayXTalkReconstructor
    extends XTalkReconstructor {
  private RBCalculator calculator;
  private double tolerance = RGB.MaxValue.Int31Bit.getStepIn255();

  TwoWayXTalkReconstructor(MultiMatrixModel mmModel,
                           XTalkProperty xtalkProperty) {
    super(mmModel, xtalkProperty);
    this.calculator = new RBCalculator(mmModel);
  }

  /**
   * getXTalkRGB
   *
   * @param XYZ CIEXYZ
   * @param originalRGB RGB
   * @param relativeXYZ boolean
   * @return RGB
   */
  public RGB getXTalkRGB(CIEXYZ XYZ, RGB originalRGB, boolean relativeXYZ) {
    if (!originalRGB.isSecondaryChannel()) {
      //originalRGB一定只能是二次色
      return null;
    }

    RGBBase.Channel selfChannel = xtalkProperty.getSelfChannel(originalRGB.
        getSecondaryChannel());
    RGB xtalkRGB = calculator.getRGB(XYZ, originalRGB, tolerance, selfChannel,
                                     relativeXYZ);
//    System.out.println(originalRGB + " " + xtalkRGB);
    this._getXTalkRGBDeltaE = calculator.getRBDeltaE();

    return xtalkRGB;
  }

}
