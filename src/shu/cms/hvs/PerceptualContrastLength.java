package shu.cms.hvs;

import shu.cms.hvs.cam.ciecam02.CIECAM02;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.cms.colorspace.independ.*;
import shu.cms.Illuminant;
import shu.cms.hvs.cam.ciecam02.CIECAM02Color;

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
public class PerceptualContrastLength {

  public final static double getPCL(double whiteLuminance, double Q) {

    return -1;
  }

  public final static double getBQ(double LQ, double LW) {
    return Math.pow(10, 2.037) * Math.pow(LQ, 0.1401) /
        Math.pow(10, g(LW) * Math.exp(f(LW) * Math.log10(LQ)));
  }

  private final static double g(double whiteLuminance) {
    return 0.99 + 0.124 * Math.pow(whiteLuminance, 0.312);
  }

  private final static double f(double whiteLuminance) {
    return -0.1121 - 0.0827 * Math.pow(whiteLuminance, 0.093);
  }

  public static void main(String[] args) {
    CIEXYZ D65 = (CIEXYZ) Illuminant.D65WhitePoint.clone();
    CIEXYZ white = (CIEXYZ) D65.clone();
    white.times(604.7);
//    D65.times(604.7);

    ViewingConditions vc = ViewingConditions.getDarkViewingConditions(white,
        604.7);
    System.out.println(vc.LA);
    CIECAM02 cam02 = new CIECAM02(vc);
    CIECAM02Color c = cam02.forward(D65);
    System.out.println(c.Q);
  }
}
