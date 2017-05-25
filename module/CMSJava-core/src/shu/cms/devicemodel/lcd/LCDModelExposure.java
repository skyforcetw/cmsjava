package shu.cms.devicemodel.lcd;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 用來包裝LCDModel函式的物件, 方便暴露非public的函式給外部呼叫.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public final class LCDModelExposure {
  private LCDModel model;
  public LCDModelExposure(LCDModel model) {
    this.model = model;
  }

  public void setWhiteRGB(RGB whiteRGB) {
    model.setWhiteRGB(whiteRGB);
  }

  public final double[] getXTalkElementValues(RGBBase.Channel ch) {
    return ( (ChannelDependentModel) model).
        getXTalkElementValues(ch);
  }

  /**
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @param relativeXYZ boolean
   * @return RGB
   * @deprecated
   */
  public final RGB getRGB(CIEXYZ XYZ, LCDModel.Factor[] factor,
                          boolean relativeXYZ) {
    return model.getRGB(XYZ, factor, relativeXYZ);
  }

  public final RGB getRGB(CIEXYZ XYZ, boolean relativeXYZ) {
    return model.getRGB(XYZ, model.getModelFactors(), relativeXYZ);
  }

  public final CIEXYZ fromXYZ(CIEXYZ XYZ, boolean relativeXYZ) {
    return model.fromXYZ(XYZ, relativeXYZ);
  }

  public CIEXYZ getXYZ(double r, double g, double b) {
    return ( (MultiMatrixModel) model).getXYZ(r, g, b);
  }

  public void resetTouchMaxIterativeTimes() {
    ( (MultiMatrixModel) model).resetTouchMaxIterativeTimes();
  }

  public void setTouchMaxIterativeTimes(int times) {
    ( (MultiMatrixModel) model).setTouchMaxIterativeTimes(times);
  }

  public RGB getRGB(CIEXYZ XYZ, boolean relativeXYZ, boolean doCovert) {
    return model.getRGB(XYZ, relativeXYZ, doCovert);
  }

  public DeltaE getRGBDeltaE() {
    return model.getRGBDeltaE();
  }
}
