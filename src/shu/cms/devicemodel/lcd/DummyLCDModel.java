package shu.cms.devicemodel.lcd;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * ���˪�LCDModel,��K�ΨӾާ@LCDModel�������k�ɨϥ�
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author vastview.com.tw
 * @version 1.0
 */
public class DummyLCDModel
    extends LCDModel {
  public DummyLCDModel(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  /**
   * �p��RGB,�ϱ��Ҧ�
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, Factor[] factor) {
    throw new UnsupportedOperationException();
  }

  /**
   * �p��XYZ,�e�ɼҦ�
   *
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, Factor[] factor) {
    throw new UnsupportedOperationException();
  }

  /**
   * �D�Y��
   *
   * @return Factor[]
   */
  protected Factor[] _produceFactor() {
    throw new UnsupportedOperationException();
  }

  /**
   * getDescription
   *
   * @return String
   */
  public String getDescription() {
    return "DummyLCDModel";
  }

}
