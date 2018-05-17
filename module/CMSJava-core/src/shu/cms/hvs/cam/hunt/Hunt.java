package shu.cms.hvs.cam.hunt;

import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * Hunt色外貌模式
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Hunt
    implements ColorAppearanceModel, CAMConst.Hunt {
  private double[] rgbWhite = null;
  private ViewingConditions vc;
  /**
   * Luminance level adaptation factor
   */
  private double FL;
  private double[] F, B, D;
  private Interpolation esInterp;
  private final static double[] hs = new double[] {
      20.14, 90.00, 164.25, 237.53};
  private final static double[] es = new double[] {
      0.8, 0.7, 1.0, 1.2
  };

  public static void main(String[] args) {
//    double r = Math.tan(1.3);
//    System.out.println(r);

    System.out.println(Math.toDegrees(Math.atan(Math.tan(Math.toRadians(89)))) +
                       180);

    ViewingConditions vc = new ViewingConditions(
        new CIEXYZ(95.05, 100.00, 108.88),
        318.31, 1,
        ViewingConditions.Situation.NormalScenes, "", 1);
    Hunt hunt = new Hunt(vc);
    hunt.forward(new CIEXYZ(19.01, 20.00, 21.78));
  }

  public Hunt(ViewingConditions vc) {
    this.vc = vc;
    init();
  }

  protected void init() {
    if (rgbWhite == null) {
      rgbWhite = XYZToRGB(vc.white.getValues());
    }
    FL = FL(vc.LA);
    F = F(vc.LA, rgbWhite);
    B = B(vc.LA, rgbWhite);
    D = D(vc._Yb, vc.white.Y, FL, F);
    esInterp = new Interpolation(hs, es);
  }

  public final HuntColor forward(CIEXYZ XYZ) {
    //cone response
    double[] rgb = XYZToRGB(XYZ.getValues());
    //chromatic adaptation cone response
    double[] rgba = a(rgb);
    //(12.29) opponent-type visual response, Achromatic post-adaptation signal
    double Aa = 2 * rgba[0] + rgba[1] + (1. / 20.) * rgba[2] - 3.05 + 1;
    //opponent-type visual response, color difference signals
    double[] C = C(rgba);

    double c = ( (1. / 2.) * (C[1] - C[2]) / 4.5) /
        (C[0] - (C[1] / 11.));
    //(12.33) Hue angle
//    double hs = Math.toDegrees(Math.atan(c));

//    double hs = Math.toDegrees(Math.atan(Math.toDegrees(c)));
    double hs = Math.atan(Math.toRadians(c));
//    if (hs < 0.0) {
//      hs -= 360.0;
//    }

//    double hs = Math.atan( ( (1. / 2.) * (C[1] - C[2]) / 4.5) /
//                                         (C[0] - (C[1] / 11.)));
//    double hs = Math.toDegrees(Math.atan(Math.toRadians( ( (1. / 2.) *
//        (C[1] - C[2]) / 4.5) /
//        (C[0] - (C[1] / 11.)))));

    //(Table 12.2)
    double es = esInterp.interpolate(hs, Interpolation.Algo.Linear);

    double Ft = Ft(vc.LA);
    //(12.34)
    double MYB = 100 * ( (1. / 2.) * (C[1] - C[2]) / 4.5) *
        (es * (10. / 13.) * vc._Nc * vc._Ncb * Ft);
    //(12.35)
    double MRG = 100 * (C[0] - (C[1] / 11.)) *
        (es * (10. / 13) * vc._Nc * vc._Ncb);
    //(12.37)
    double M = Math.sqrt(Maths.sqr(MYB) + Maths.sqr(MRG));
    //(12.38)
    double s = 50. * M / (Maths.sum(rgba));
    //(12.39)
//    double As = 3.05
    return null;
  }

  protected final static double j(double LAS) {
    //(12.41)
    return 0.00001 / ( (5 * LAS / 2.26) + 0.00001);
  }

  protected final static double FLS(double LAS) {
    //(12.40)
    double j = j(LAS);
    double j2 = Maths.sqr(j);
    double c = 5 * LAS / 2.26;
    double c6 = Math.pow(c, 1. / 6.);
    //(12.40)
    return 3800 * j2 * c + 0.2 * Math.pow( (1 - j2), 4) * c6;
  }

  protected final static double Ft(double LA) {
    //(12.36)
    return LA / (LA + 0.1);
  }

  protected final static double[] C(double[] rgba) {
    double[] C = new double[3];
    //(12.30)
    C[0] = rgba[0] - rgba[1];
    //(12.31)
    C[1] = rgba[1] - rgba[2];
    //(12.32)
    C[2] = rgba[2] - rgba[0];
    return C;
  }

  /**
   * chromatic adaptation model
   * @param rgb double[]
   * @return double[]
   */
  protected final double[] a(double[] rgb) {
    double[] a = new double[3];
    //(12.5)
    a[0] = B[0] * (fn(FL * F[0] * (rgb[0] / rgbWhite[0])) + D[0]) + 1;
    //(12.6)
    a[1] = B[1] * (fn(FL * F[1] * (rgb[1] / rgbWhite[1])) + D[1]) + 1;
    //(12.7)
    a[2] = B[2] * (fn(FL * F[2] * (rgb[2] / rgbWhite[2])) + D[2]) + 1;
    return a;
  }

  protected final static double[] XYZToRGB(double[] XYZValues) {
    //(12.4)
    double[] result = DoubleArray.times(CAMConst.Hunt.M, XYZValues);
    return result;
  }

  protected final static double[] adjustedReferenceWhite(double[] rgbWhite,
      double[] rgbBackgound, double[] rgbProximalField, double p) {
    double[] rgbp = new double[3];
    //(12.26)
    rgbp[0] = rgbProximalField[0] / rgbBackgound[0];
    //(12.27)
    rgbp[1] = rgbProximalField[1] / rgbBackgound[1];
    //(12.28)
    rgbp[2] = rgbProximalField[2] / rgbBackgound[2];

    double pp = 1 + p;
    double np = 1 - p;
    double[] rgbWhitep = new double[3];
    rgbWhitep[0] = (rgbWhite[0] * Math.sqrt(
        (np * rgbp[0] + pp / rgbp[0]))) / (Math.sqrt(pp * rgbp[0] + np / rgbp[0]));
    rgbWhitep[1] = (rgbWhite[1] * Math.sqrt(
        (np * rgbp[1] + pp / rgbp[1]))) / (Math.sqrt(pp * rgbp[1] + np / rgbp[1]));
    rgbWhitep[2] = (rgbWhite[2] * Math.sqrt(
        (np * rgbp[2] + pp / rgbp[2]))) / (Math.sqrt(pp * rgbp[2] + np / rgbp[2]));
    return rgbWhitep;
  }

  /**
   * General hyperbolic function that is used to model the nolinear behavior of
   * various visual responses.
   * @param I double
   * @return double
   */
  protected final static double fn(double I) {
    double I073 = Math.pow(I, 0.73);
    //(12.8)
    return 40. * (I073 / (I073 + 2.));
  }

  /**
   * Luminance-level adaptation factor
   * @param LA double
   * @return double
   */
  public final static double FL(double LA) {
    double k = k(LA);
    double kQuartic = Math.pow(k, 4);
    //(12.9)
    return 0.2 * kQuartic * (5 * LA) +
        0.1 * Maths.sqr(1 - kQuartic) * Maths.cubeRoot(5 * LA);
  }

  /**
   * Incorporated with FL
   * @param LA double
   * @return double
   */
  protected final static double k(double LA) {
    //(12.10)
    return 1. / (5 * LA + 1);
  }

  /**
   * Chromatic adaptation factors
   * @param LA double
   * @param white double[]
   * @return double[]
   */
  protected final static double[] F(double LA, double[] white) {
//    double LACubicRoot = Math.pow(LA, 1. / 3);
    double LACubicRoot = Maths.cubeRoot(LA);
    double[] h = h(white);
    double[] F = new double[3];
    //(12.11)
    F[0] = (1 + LACubicRoot + h[0]) / (1 + LACubicRoot + 1. / h[0]);
    //(12.12)
    F[1] = (1 + LACubicRoot + h[1]) / (1 + LACubicRoot + 1. / h[1]);
    //(12.13)
    F[2] = (1 + LACubicRoot + h[2]) / (1 + LACubicRoot + 1. / h[2]);
    return F;
  }

  /**
   * Incorporated with F
   * The parameters h(hr,hg,hb) can be throught of as chromaticity coordinates
   * scaled relative to illuminant E.
   * @param white double[]
   * @return double[]
   */
  protected final static double[] h(double[] white) {
    double sum = Maths.sum(white);
    double[] h = new double[3];
    //(12.14)
    h[0] = 3 * white[0] / sum;
    //(12.15)
    h[1] = 3 * white[1] / sum;
    //(12.16)
    h[2] = 3 * white[2] / sum;
    return h;
  }

  /**
   * Included to allow prediction of the Helson-Judd effect
   * @param Yb double
   * @param YW double
   * @param FL double
   * @param F double[]
   * @return double[]
   */
  protected final static double[] D(double Yb, double YW, double FL, double[] F) {
    double[] D = new double[3];
    //(12.17)
    D[0] = fn( (Yb / YW) * FL * F[1]) - fn( (Yb / YW) * FL * F[0]);
    //(12.18)
    D[1] = 0.0;
    //(12.19)
    D[2] = fn( (Yb / YW) * FL * F[1]) - fn( (Yb / YW) * FL * F[1]);
    return D;
  }

  /**
   * Cone bleach factors.
   * @param LA double
   * @param white double[]
   * @return double[]
   */
  protected final static double[] B(double LA, double[] white) {
    double ten7 = Math.pow(10, 7);
    double[] B = new double[3];
    //(12.20)
    B[0] = ten7 / (ten7 + 5 * LA * (white[0] / 100.));
    //(12.21)
    B[1] = ten7 / (ten7 + 5 * LA * (white[1] / 100.));
    //(12.22)
    B[2] = ten7 / (ten7 + 5 * LA * (white[2] / 100.));
    return B;
  }
}
