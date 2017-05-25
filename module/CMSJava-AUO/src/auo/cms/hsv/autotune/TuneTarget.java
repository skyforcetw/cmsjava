package auo.cms.hsv.autotune;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class TuneTarget {
  public abstract CIEXYZ getTarget(double hue, double saturation, double value);

  public final CIELab getTargetLab(HSV hsv) {
    CIEXYZ targetXYZ = getTarget(hsv);
    CIELab targetLab = new CIELab(targetXYZ, getTargetWhite());
    return targetLab;

  }

  public CIEXYZ getTarget(HSV hsv) {
    return getTarget(hsv.H, hsv.S, hsv.V);
  }

  public abstract CIEXYZ getTarget(int hue);

  private CIEXYZ targetWhite = null;
  public final CIEXYZ getTargetWhite() {
    if (null == targetWhite) {
      targetWhite = getTarget(0, 0, 100);
    }
    return targetWhite;
  }

  public abstract HSV getTuneSpot(double hue);

  /**
   * 取得要用來調整的點
   * @param hue double
   * @return HSV[]
   */
  public abstract HSV[] getTuneSpots(double hue);
}
