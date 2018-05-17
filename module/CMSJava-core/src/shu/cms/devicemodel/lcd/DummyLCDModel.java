package shu.cms.devicemodel.lcd;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 偽裝的LCDModel,方便用來操作LCDModel的物件方法時使用
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
   * 計算RGB,反推模式
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, Factor[] factor) {
    throw new UnsupportedOperationException();
  }

  /**
   * 計算XYZ,前導模式
   *
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, Factor[] factor) {
    throw new UnsupportedOperationException();
  }

  /**
   * 求係數
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
