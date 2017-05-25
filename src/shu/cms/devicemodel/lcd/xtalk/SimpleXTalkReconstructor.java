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
      //originalRGB�@�w�u��O�G����
      return null;
    }

    //�ھ�relativeXYZ�B�zXYZ
    CIEXYZ fromXYZ = adapter.fromXYZ(XYZ, relativeXYZ);
    /**
     * @todo H acp �O�_�n�d�srationalize
     */
    fromXYZ.rationalize();
    return getXTalkRGBByMinimisation(fromXYZ, originalRGB);
  }

  /**
   * �w��XYZ,�٭�RGB,�B�T�w�FXTalk���W�D,�Q���u�ƪ��覡�D�o�̨θ�
   *
   * �t��k����:
   * (1)�p��Xtalk channel
   * (2)�Q���u�ƪ��覡�D�o�̨θ�
   * (3)�p���Ӻt��k�~�t�ҳy������t
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
    // �p��inverseLab��deltaE
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
   * �w��XYZ,�٭�RGB,�B�T�w�FXTalk���W�D,�Q���u�ƪ��覡�D�o�̨θ�
   *
   * �t��k����:
   * (1)�p��Xtalk channel
   * (2)�Q���u�ƪ��覡�D�o�̨θ�
   * (3)�p���Ӻt��k�~�t�ҳy������t
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
//    // �p��inverseLab��deltaE
//    //==========================================================================
//    //(3)
//    _getXTalkRGBDeltaE = mmModel.calculateGetRGBDeltaE(rgb, XYZ, true);
//    //==========================================================================
//    return rgb;
//  }


}
