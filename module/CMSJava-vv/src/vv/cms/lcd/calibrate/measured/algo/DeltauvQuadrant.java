package vv.cms.lcd.calibrate.measured.algo;

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
public class DeltauvQuadrant {
  public DeltauvQuadrant(boolean uPostive, boolean vPostive) {
    this.uPostive = uPostive;
    this.vPostive = vPostive;
  }

  public boolean isUQualified(double u) {
    return (u >= 0 == uPostive);
  }

  public boolean isVQualified(double v) {
    return (v >= 0 == vPostive);
  }

  public boolean isQualified(double[] uv) {
    return (uv[0] >= 0 == uPostive) && (uv[1] >= 0 == vPostive);
  }

  public boolean uPostive;
  public boolean vPostive;
}
