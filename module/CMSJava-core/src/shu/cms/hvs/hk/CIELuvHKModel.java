package shu.cms.hvs.hk;

import shu.cms.colorspace.independ.*;

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
public class CIELuvHKModel {
  public static enum Type {
    /**
     * 適用於高亮度 1000 lx
     * 白點約318.3 nits, 中性灰約63.7 nits
     */
    Wyszecki,
    /**
     * 適用於低亮度 30-100 lx
     * 白點約9.5~31.8 nits
     */
    SandersWyszecki;
  }

  public static void main(String[] args) {
    for (int h = 0; h <= 350; h += 10) {
      CIELCh LCh = new CIELCh(50, 150, h);
      CIELuv Luv = new CIELuv(LCh);
      double L = getHKLightness(Luv, Type.Wyszecki);
      System.out.println(L);
    }
  }

  public final static double getHKLightness(CIELuv Luv, Type type) {
    CIELCh LCh = new CIELCh(Luv);
    double qqC = getqqC(LCh);
    double[] alpha = getAlpha(type);
    double result = Luv.L - alpha[0] * qqC + alpha[1] * LCh.C;
    return result;
  }

  private final static double[] getAlpha(Type type) {
    switch (type) {
      case Wyszecki:
        return new double[] {
            -0.1421, 0.0804};
      case SandersWyszecki:
        return new double[] {
            -0.1447, 0.0385};
      default:
        return null;
    }
  }

  private final static double getqqC(CIELCh LCh) {
    double theta = LCh.h;
    double c = LCh.C;
    double q1 = getQ1(theta);
    double q2 = getQ2(theta);
//    System.out.println(LCh + " " + q1 + " " + q2);
    double result = q1 * c + q2 * Math.pow(c, 2) * 1E-4;
    return result;
  }

  private final static double getQ1(double theta) {
    double t = Math.toRadians(theta);
    double q1 = -0.01687 - 0.02302 * Math.cos(t)
        - 0.01737 * Math.cos(2 * t) - 0.00989 * Math.cos(3 * t)
        - 0.01270 * Math.cos(4 * t) + 0.08200 * Math.sin(t)
        + 0.01487 * Math.sin(2 * t) - 0.00525 * Math.sin(3 * t)
        + 0.00375 * Math.sin(4 * t);
    return q1;
  }

  private final static double getQ2(double theta) {
    double t = Math.toRadians(theta);
    double q2 = 0.25162 - 0.62121 * Math.cos(t)
        - 2.77474 * Math.cos(2 * t) - 1.40665 * Math.cos(3 * t)
        + 1.28355 * Math.cos(4 * t) + 6.13941 * Math.sin(t)
        + 3.26886 * Math.sin(2 * t) - 1.36944 * Math.sin(3 * t)
        - 0.99062 * Math.sin(4 * t);

    return q2;
  }
}
