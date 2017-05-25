package shu.cms.colorspace.independ;

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
public enum NormalizeY {
  Normal100(100), Normal1(1), Not( -1);

  NormalizeY(double normal) {
    this.normal = normal;
  }

  final double normal;

}

interface NormalizeYOperator {
  public void normalize(NormalizeY normalizeY);

  public void normalizeY();

  public void normalizeY100();

  public void setNormalizeNot();

  public final static double NormalFactor = 1;
}
