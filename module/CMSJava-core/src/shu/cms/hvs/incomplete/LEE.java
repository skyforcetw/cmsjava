package shu.cms.hvs.incomplete;

import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.math.*;

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
public class LEE {
  protected final static CIExyY NeutralPoint = new CIExyY(0.319, 0.319, 1);

  public final static CIEXYZ incompleteAdaptation(CIEXYZ XYZ, CIEXYZ white) {
    double[] XYZValues = XYZ.getValues();
    double[] rgb = CIECAM02.XYZToCAT02(XYZValues);
    return null;
  }

  protected final static ViewingConditions getViewingConditions() {
//    ViewingConditions vc = new ViewingConditions(white,0,0,
//    return vc;
    return null;
  }

  private static double getDs(double La) {
    return 0.61 + 0.52 * Math.pow(1 - Math.exp( -0.82 * Math.log10(La)), 6.70);
  }

  private static double getDm(CIExyY xyY, double La) {
    return 0.96 * getDc(xyY) *
        (Math.pow(1 - Math.exp( -4.28 * Math.log10(La)), 406.5) - 1) + 1;
  }

  private static double getDm(double Pxy, double La) {
    return 0.96 * getDc(Pxy) *
        (Math.pow(1 - Math.exp( -4.28 * Math.log10(La)), 406.5) - 1) + 1;
  }

  private static double getDc(double Pxy) {
    return 1 - Math.exp( -5.30 * Pxy);
  }

  private static double getDc(CIExyY xyY) {
    return 1 - Math.exp( -5.30 * getPxy(xyY));
  }

  private static double getPxy(CIExyY xyY) {
    return Math.sqrt(Maths.sqr(xyY.x - NeutralPoint.x) +
                     Maths.sqr(xyY.y - NeutralPoint.y));
  }

  public static void main(String[] args) {

  }

}
