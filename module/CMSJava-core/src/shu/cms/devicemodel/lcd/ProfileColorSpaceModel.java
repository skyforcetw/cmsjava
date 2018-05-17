package shu.cms.devicemodel.lcd;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.profile.*;
import shu.math.array.*;

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
public class ProfileColorSpaceModel
    extends LCDModel {
  public ProfileColorSpaceModel(ProfileColorSpace pcs) {
    this(pcs, pcs.getReferenceWhite().Y, new CIEXYZ());
  }

  public ProfileColorSpaceModel(RGB.ColorSpace colorSpace) {
    this(ProfileColorSpace.Instance.get(colorSpace, ""));
  }

  public ProfileColorSpaceModel(RGB.ColorSpace colorSpace, double luminance,
                                CIEXYZ flare) {
    this(ProfileColorSpace.Instance.get(colorSpace, ""), luminance, flare);
  }

  private double luminanceFactor = 1;

  public ProfileColorSpaceModel(ProfileColorSpace pcs, double luminance,
                                CIEXYZ flare) {
    this.pcs = pcs;
    this.flare.setFlare(flare);
    CIEXYZ white = pcs.getReferenceWhite();
    this.luminance = (CIEXYZ) white.clone();
    this.luminance.scaleY(luminance);
    this.targetWhitePoint = this.luminance;
    this.lcdTarget = LCDTarget.Instance.get(LCDTargetBase.Number.WHQL);
    this.whiteRGB = lcdTarget.getWhitePatch().getRGB();

    this.luminanceFactor = (luminance - flare.Y) / white.Y;

  }

  private ProfileColorSpace pcs;

  /**
   * 計算RGB,反推模式
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, Factor[] factor) {
    double[] XYZValues = DoubleArray.times(XYZ.getValues(),
                                           1. / luminanceFactor);
    double[] rgbValues = pcs.fromCIEXYZValues(XYZValues);
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, rgbValues);
    return rgb;
  }

  /**
   * 計算XYZ,前導模式
   *
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, Factor[] factor) {
    double[] RGBValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
    double[] XYZValues = pcs.toCIEXYZValues(RGBValues);
    CIEXYZ XYZ = new CIEXYZ(XYZValues, this.targetWhitePoint);
    XYZ.times(luminanceFactor);
    return XYZ;
  }

  /**
   * 求係數
   *
   * @return Factor[]
   */
  protected Factor[] _produceFactor() {
    return new Factor[3];
  }

  /**
   * getDescription
   *
   * @return String
   */
  public String getDescription() {
    return "ProfileColorSpace";
  }

  public void setLCDTarget(LCDTarget lcdTarget) {
    this.lcdTarget = lcdTarget;
  }

  public void setGammaCorrectLCDTarget(LCDTarget rLCDTarget) {
    this.rCorrectLCDTarget = rLCDTarget;
  }

  public ProfileColorSpace getProfileColorSpace() {
    return pcs;
  }
}
