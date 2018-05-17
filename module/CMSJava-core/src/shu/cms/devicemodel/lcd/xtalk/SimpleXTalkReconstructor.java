package shu.cms.devicemodel.lcd.xtalk;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;

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
public class SimpleXTalkReconstructor
    extends XTalkReconstructor {
  /**
   * SimpleXTalkReconstructor
   *
   * @param mmModel MultiMatrixModel
   * @param xtalkProperty XTalkProperty
   */
  protected SimpleXTalkReconstructor(MultiMatrixModel mmModel,
                                     XTalkProperty xtalkProperty) {
    super(mmModel, xtalkProperty);
  }

  protected XTalkRGBRecover recover = new XTalkRGBRecover();

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

    //根據relativeXYZ處理XYZ
    CIEXYZ fromXYZ = adapter.fromXYZ(XYZ, relativeXYZ);
    /**
     * @todo H acp 是否要留存rationalize
     */
    fromXYZ.rationalize();
    return getXTalkRGBByMinimisation(fromXYZ, originalRGB);
  }

  /**
   * 已知XYZ,還原RGB,且確定了XTalk的頻道,利用優化的方式求得最佳解
   *
   * 演算法說明:
   * (1)計算Xtalk channel
   * (2)利用優化的方式求得最佳解
   * (3)計算整個演算法誤差所造成的色差
   * @param XYZ CIEXYZ
   * @param originalRGB RGB
   * @return RGB
   */
  protected RGB getXTalkRGBByMinimisation(CIEXYZ XYZ, final RGB originalRGB) {
    //(1)
    RGBBase.Channel selfChannel = xtalkProperty.getSelfChannel(originalRGB.
        getSecondaryChannel());
    //(2)
    RGB rgb = recover.getXTalkRGB(XYZ, originalRGB, selfChannel, false);

    //==========================================================================
    // 計算inverseLab的deltaE
    //==========================================================================
    //(3)
    _getXTalkRGBDeltaE = mmModel.calculateGetRGBDeltaE(rgb, XYZ, true);
    //==========================================================================

    //==========================================================================
    RGB rgb2 = recover.getXTalkRGB(XYZ, originalRGB, selfChannel);
//    DeltaE de = mmModel.calculateGetRGBDeltaE(rgb2, XYZ, true);
    CIEXYZ XYZ1 = mmModel.getXYZ(rgb, false);
    CIEXYZ XYZ2 = mmModel.getXYZ(rgb2, false);
    //==========================================================================

    return rgb;
  }

  /**
   * 已知XYZ,還原RGB,且確定了XTalk的頻道,利用優化的方式求得最佳解
   *
   * 演算法說明:
   * (1)計算Xtalk channel
   * (2)利用優化的方式求得最佳解
   * (3)計算整個演算法誤差所造成的色差
   * @param XYZ CIEXYZ
   * @param originalRGB RGB
   * @return RGB
   */
//  protected RGB getXTalkRGBByMinimisation(CIEXYZ XYZ, final RGB originalRGB) {
//    //(1)
//    RGBBase.Channel selfChannel = xtalkProperty.getSelfChannel(originalRGB.
//        getSecondaryChannel());
//    //(2)
//    RGB rgb = recover.getXTalkRGB(XYZ, originalRGB, selfChannel);
//
//    //==========================================================================
//    // 計算inverseLab的deltaE
//    //==========================================================================
//    //(3)
//    _getXTalkRGBDeltaE = mmModel.calculateGetRGBDeltaE(rgb, XYZ, true);
//    //==========================================================================
//    return rgb;
//  }


}
