package shu.cms.hvs.cam.ciecam02;

import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.math.array.*;
import shu.cms.Illuminant;

//import sky4s.cms.cam.test2.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * CIECAM02︹华家Α
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class CIECAM02
    implements ColorAppearanceModel, CAMConst.CIECAM02 {

  public CIECAM02(ViewingConditions vc) {
    this.vc = vc;
  }

  private double[] CATConst = null;
  private double[] rgbWhite = null;
  private ViewingConditions vc;
  public ViewingConditions getViewingConditions() {
    return vc;
  }

  protected final double[] chromaticAdaptationTransform(double[] rgb) {
    double[] CATConst = getCATConst();
    double[] rgbC = new double[3];
    //Rc(9)
    rgbC[0] = rgb[0] * CATConst[0];
    rgbC[1] = rgb[1] * CATConst[1];
    rgbC[2] = rgb[2] * CATConst[2];
    return rgbC;
  }

  protected final static double A(ViewingConditions vc, double[] rgbPa) {
    return ( (2.0 * rgbPa[0]) + rgbPa[1] + ( (1.0 / 20.0) * rgbPa[2]) -
            0.305) * vc.nbb;
  }

  protected final static CIECAM02Color calculateCIECAM02Color(double a,
      double b, double[] rgbPa, ViewingConditions vc) {
    CIECAM02Color color = new CIECAM02Color();

    color.h = Math.toDegrees(Math.atan2(b, a));
    if (color.h < 0.0) {
      color.h += 360.0;
    }

    double temp;
    if (color.h < 20.14) {
      temp = ( (color.h + 122.47) / 1.2) + ( (20.14 - color.h) / 0.8);
      color.H = 300 + (100 * ( (color.h + 122.47) / 1.2)) / temp;
    }
    else if (color.h < 90.0) {
      temp = ( (color.h - 20.14) / 0.8) + ( (90.00 - color.h) / 0.7);
      color.H = (100 * ( (color.h - 20.14) / 0.8)) / temp;
    }
    else if (color.h < 164.25) {
      temp = ( (color.h - 90.00) / 0.7) + ( (164.25 - color.h) / 1.0);
      color.H = 100 + ( (100 * ( (color.h - 90.00) / 0.7)) / temp);
    }
    else if (color.h < 237.53) {
      temp = ( (color.h - 164.25) / 1.0) + ( (237.53 - color.h) / 1.2);
      color.H = 200 + ( (100 * ( (color.h - 164.25) / 1.0)) / temp);
    }
    else {
      temp = ( (color.h - 237.53) / 1.2) +
          ( (360 - color.h + 20.14) / 0.8);
      color.H = 300 + ( (100 * ( (color.h - 237.53) / 1.2)) / temp);
    }

    //(20)
//    double A = ( (2.0 * rgbPa[0]) + rgbPa[1] + ( (1.0 / 20.0) * rgbPa[2]) -
//                0.305) * vc.nbb;
    double A = A(vc, rgbPa);

    //(21)
    color.J = 100.0 * Math.pow(A / vc.aw, vc.c * vc.z);

    //(18)?
    double et = (1.0 / 4.0) *
        (Math.cos( ( (color.h * Math.PI) / 180.0) + 2.0) + 3.8);

    //(16)
    double t = ( (50000.0 / 13.0) * vc._Nc * vc._Ncb * et *
                Math.sqrt( (a * a) + (b * b))) /
        (rgbPa[0] + rgbPa[1] + (21.0 / 20.0) * rgbPa[2]);

    //(23)
    color.C = Math.pow(t, 0.9) * Math.sqrt(color.J / 100.0)
        * Math.pow(1.64 - Math.pow(0.29, vc.n), 0.73);

    //(22)
    color.Q = (4.0 / vc.c) * Math.sqrt(color.J / 100.0) *
        (vc.aw + 4.0) * Math.pow(vc.FL, 0.25);

    //(24)
    color.M = color.C * Math.pow(vc.FL, 0.25);

    //(25)
    color.s = 100.0 * Math.sqrt(color.M / color.Q);

    //(26)
    color.ac = color.C * Math.cos( (color.h * Math.PI) / 180.0);
    //(27)
    color.bc = color.C * Math.sin( (color.h * Math.PI) / 180.0);

    color.am = color.M * Math.cos( (color.h * Math.PI) / 180.0);
    color.bm = color.M * Math.sin( (color.h * Math.PI) / 180.0);

    color.as = color.s * Math.cos( (color.h * Math.PI) / 180.0);
    color.bs = color.s * Math.sin( (color.h * Math.PI) / 180.0);
    color.white = vc.white;
    return color;
  }

  /**
   * ︹华家Α玡旧
   * @param XYZ CIEXYZ 箇代︹縀
   * @return CIECAM02Color 箇代泊︹华
   */
  public final CIECAM02Color forward(CIEXYZ XYZ) {
    double[] XYZValues = new double[3];
    XYZ.getValues(XYZValues, NormalizeY.Normal100);

    double[] rgb = XYZToCAT02(XYZValues);
    if (rgbWhite == null) {
      double[] whiteValues = vc.white.getValues(new double[3],
                                                NormalizeY.Normal100);
      rgbWhite = XYZToCAT02(whiteValues);
    }

    //Rc(9)
    double[] rgbC = chromaticAdaptationTransform(rgb);

    //R'
    double[] rgbP = CAT02ToHPE(rgbC);
    DoubleArray.abs(rgbP);

    double[] rgbPa = nonlinearAdaptation(rgbP, vc.FL);

    //(14)
    double a = rgbPa[0] - ( (12.0 * rgbPa[1]) / 11.0) + (rgbPa[2] / 11.0);
    //(15)
    double b = (1.0 / 9.0) * (rgbPa[0] + rgbPa[1] - (2.0 * rgbPa[2]));

    return calculateCIECAM02Color(a, b, rgbPa, vc);
  }

  protected final double[] getCATConst() {
    if (CATConst == null) {
      CATConst = new double[3];
      CATConst[0] = ( ( (vc.white.Y * vc.d) / rgbWhite[0]) + (1.0 - vc.d));
      CATConst[1] = ( ( (vc.white.Y * vc.d) / rgbWhite[1]) + (1.0 - vc.d));
      CATConst[2] = ( ( (vc.white.Y * vc.d) / rgbWhite[2]) + (1.0 - vc.d));
    }
    return CATConst;
  }

  protected final double[] inverseChromaticAdaptationTransform(double[] rgbC) {
    double[] CATConst = getCATConst();
    double[] rgb = new double[3];
    rgb[0] = rgbC[0] / CATConst[0];
    rgb[1] = rgbC[1] / CATConst[1];
    rgb[2] = rgbC[2] / CATConst[2];
    return rgb;
  }

  public final CIEXYZ inverse(CIECAM02Color color) {

    if (rgbWhite == null) {
      rgbWhite = XYZToCAT02(vc.white.getValues());
    }

    double t = Math.pow(color.C /
                        (Math.sqrt(color.J / 100.0) *
                         Math.pow(1.64 - Math.pow(0.29, vc.n), 0.73)),
                        (1.0 / 0.9));
    double et = (1.0 / 4.0) *
        (Math.cos( ( (color.h * Math.PI) / 180.0) + 2.0) + 3.8);

    double a = Math.pow(color.J / 100.0, 1.0 / (vc.c * vc.z)) * vc.aw;

    double p1 = ( (50000.0 / 13.0) * vc._Nc * vc._Ncb) * et / t;
    double p2 = (a / vc.nbb) + 0.305;
    double p3 = 21.0 / 20.0;

    double hr = (color.h * Math.PI) / 180.0;

    double ca, cb;
    double p4, p5;
    if (Math.abs(Math.sin(hr)) >= Math.abs(Math.cos(hr))) {
      p4 = p1 / Math.sin(hr);
      cb = (p2 * (2.0 + p3) * (460.0 / 1403.0)) /
          (p4 + (2.0 + p3) * (220.0 / 1403.0) *
           (Math.cos(hr) / Math.sin(hr)) - (27.0 / 1403.0) +
           p3 * (6300.0 / 1403.0));
      ca = cb * (Math.cos(hr) / Math.sin(hr));
    }
    else {
      p5 = p1 / Math.cos(hr);
      ca = (p2 * (2.0 + p3) * (460.0 / 1403.0)) /
          (p5 + (2.0 + p3) * (220.0 / 1403.0) -
           ( (27.0 / 1403.0) - p3 * (6300.0 / 1403.0)) *
           (Math.sin(hr) / Math.cos(hr)));
      cb = ca * (Math.sin(hr) / Math.cos(hr));
    }

    double[] rgbPa = _Aab2RGB(a, ca, cb, vc.nbb);
    double[] rgbP = new double[3];

    rgbP[0] = inverseNonlinearAdaptation(rgbPa[0], vc.FL);
    rgbP[1] = inverseNonlinearAdaptation(rgbPa[1], vc.FL);
    rgbP[2] = inverseNonlinearAdaptation(rgbPa[2], vc.FL);

    double[] XYZT = HPEToXYZ(rgbP);
    double[] rgbC = XYZToCAT02(XYZT);

    double[] rgb = inverseChromaticAdaptationTransform(rgbC);

    double[] result = CAT02ToXYZ(rgb);
    return new CIEXYZ(result, vc.white, NormalizeY.Normal100);

  }

  /**
   *              [  0.7328  0.4296  -0.1624 ]
   *    M_CAT02 = [ -0.7036  1.6975   0.0061 ]
   *              [  0.0030  0.0136   0.9834 ]
   *
   *              [  1.096124 -0.278869 0.182745 ]
   * M^-1_CAT02 = [  0.454369  0.473533 0.072098 ]
   *              [ -0.009628 -0.005698 1.015326 ]
   */


  /**
   *
   * @param XYZValues double[]
   * @return double[]
   */
  public final static double[] XYZToCAT02(double[] XYZValues) {
    //(6)
    double[] result = DoubleArray.times(CAMConst.CIECAM02.M, XYZValues);
    return result;
  }

  protected final static double[] CAT02ToXYZ(double[] rgb) {
    double[] result = DoubleArray.times(CAMConst.CIECAM02.M_inv, rgb);
    return result;
  }

  protected final static double[] HPEToXYZ(double[] rgb) {
    double[] result = DoubleArray.times(MH_inv, rgb);
    return result;
  }

  private static boolean modifyState = false;

  public final static void setModifyState(boolean modify) {
    modifyState = modify;
    if (modifyState) {
      M_H_CAT02inv = DoubleArray.times(CAMConst.
                                       CIECAM02.MHmod, CAMConst.CIECAM02.M_inv);
      MH_inv = CAMConst.CIECAM02.MHmod_inv;
    }
    else {
      M_H_CAT02inv = DoubleArray.times(CAMConst.
                                       CIECAM02.MH, CAMConst.CIECAM02.M_inv);
      MH_inv = CAMConst.CIECAM02.MH_inv;
    }
  }

  private static double[][] MH_inv = CAMConst.CIECAM02.MH_inv;

  //(10)
  private static double[][] M_H_CAT02inv = DoubleArray.times(CAMConst.
      CIECAM02.MH, CAMConst.CIECAM02.M_inv);

  protected final static double[] CAT02ToHPE(double[] rgb) {
    double[] result = DoubleArray.times(M_H_CAT02inv, rgb);
    return result;
  }

  protected final static double[] nonlinearAdaptation(double[] rgbP, double fl) {
    double[] rgbPa = new double[3];
    rgbPa[0] = nonlinearAdaptation(rgbP[0], fl);
    rgbPa[1] = nonlinearAdaptation(rgbP[1], fl);
    rgbPa[2] = nonlinearAdaptation(rgbP[2], fl);
    return rgbPa;
  }

  protected final static double nonlinearAdaptation(double c, double fl) {
    //(13)
    double p = Math.pow( (fl * c) / 100.0, 0.42);
    return ( (400.0 * p) / (27.13 + p)) + 0.1;
  }

  protected final static double inverseNonlinearAdaptation(double c, double fl) {
    return (100.0 / fl) *
        Math.pow( (27.13 * Math.abs(c - 0.1)) / (400.0 - Math.abs(c - 0.1)),
                 1.0 / 0.42);
  }

  public final static double[] chromaticAdaptationTransform(
      ViewingConditions vc, double[] rgb, double[] white) {
    double[] rgbC = new double[3];
    //Rc(9)
    rgbC[0] = rgb[0] * ( ( (vc.white.Y * vc.d) / white[0]) + (1.0 - vc.d));
    rgbC[1] = rgb[1] * ( ( (vc.white.Y * vc.d) / white[1]) + (1.0 - vc.d));
    rgbC[2] = rgb[2] * ( ( (vc.white.Y * vc.d) / white[2]) + (1.0 - vc.d));
    return rgbC;
  }

  /**
   * ︹华家Α玡旧
   * @param XYZ CIEXYZ
   * @param vc ViewingConditions
   * @return CIECAM02Color
   * @deprecated
   */
  public final static CIECAM02Color forward(CIEXYZ XYZ,
                                            ViewingConditions vc) {
    double et, temp;

    double[] rgb = XYZToCAT02(XYZ.getValues());
    double[] rgbW = XYZToCAT02(vc.white.getValues());

    //Rc(9)
    double[] rgbC = chromaticAdaptationTransform(vc, rgb, rgbW);

    //R'
    double[] rgbP = CAT02ToHPE(rgbC);
    DoubleArray.abs(rgbP);

    double[] rgbPa = nonlinearAdaptation(rgbP, vc.FL);

    //(14)
    double a = rgbPa[0] - ( (12.0 * rgbPa[1]) / 11.0) + (rgbPa[2] / 11.0);
    //(15)
    double b = (1.0 / 9.0) * (rgbPa[0] + rgbPa[1] - (2.0 * rgbPa[2]));

    CIECAM02Color color = new CIECAM02Color();

    color.h = Math.toDegrees(Math.atan2(b, a));
    if (color.h < 0.0) {
      color.h += 360.0;
    }

    if (color.h < 20.14) {
      temp = ( (color.h + 122.47) / 1.2) + ( (20.14 - color.h) / 0.8);
      color.H = 300 + (100 * ( (color.h + 122.47) / 1.2)) / temp;
    }
    else if (color.h < 90.0) {
      temp = ( (color.h - 20.14) / 0.8) + ( (90.00 - color.h) / 0.7);
      color.H = (100 * ( (color.h - 20.14) / 0.8)) / temp;
    }
    else if (color.h < 164.25) {
      temp = ( (color.h - 90.00) / 0.7) + ( (164.25 - color.h) / 1.0);
      color.H = 100 + ( (100 * ( (color.h - 90.00) / 0.7)) / temp);
    }
    else if (color.h < 237.53) {
      temp = ( (color.h - 164.25) / 1.0) + ( (237.53 - color.h) / 1.2);
      color.H = 200 + ( (100 * ( (color.h - 164.25) / 1.0)) / temp);
    }
    else {
      temp = ( (color.h - 237.53) / 1.2) +
          ( (360 - color.h + 20.14) / 0.8);
      color.H = 300 + ( (100 * ( (color.h - 237.53) / 1.2)) / temp);
    }

    //(20)
    double A = A(vc, rgbPa);

    //(21)
    color.J = 100.0 * Math.pow(A / vc.aw, vc.c * vc.z);

    //(18)?
    et = (1.0 / 4.0) *
        (Math.cos( ( (color.h * Math.PI) / 180.0) + 2.0) + 3.8);

    //(16)
    double t = ( (50000.0 / 13.0) * vc._Nc * vc._Ncb * et *
                Math.sqrt( (a * a) + (b * b))) /
        (rgbPa[0] + rgbPa[1] + (21.0 / 20.0) * rgbPa[2]);

    //(23)
    color.C = Math.pow(t, 0.9) * Math.sqrt(color.J / 100.0)
        * Math.pow(1.64 - Math.pow(0.29, vc.n), 0.73);

    //(22)
    color.Q = (4.0 / vc.c) * Math.sqrt(color.J / 100.0) *
        (vc.aw + 4.0) * Math.pow(vc.FL, 0.25);

    //(24)
    color.M = color.C * Math.pow(vc.FL, 0.25);

    //(25)
    color.s = 100.0 * Math.sqrt(color.M / color.Q);

    //(26)
    color.ac = color.C * Math.cos( (color.h * Math.PI) / 180.0);
    //(27)
    color.bc = color.C * Math.sin( (color.h * Math.PI) / 180.0);

    color.am = color.M * Math.cos( (color.h * Math.PI) / 180.0);
    color.bm = color.M * Math.sin( (color.h * Math.PI) / 180.0);

    color.as = color.s * Math.cos( (color.h * Math.PI) / 180.0);
    color.bs = color.s * Math.sin( (color.h * Math.PI) / 180.0);

    return color;
  }

  private final static double[][] M_Aab2RGB = new double[][] {
      {
      0.32787, 0.32145, 0.20527}, {
      0.32787, -0.63507, -0.18603}, {
      0.32787, -0.15681, -4.49038}
  };

  protected final static double[] _Aab2RGB(double A,
                                           double aa,
                                           double bb, double nbb) {
    double x = (A / nbb) + 0.305;
    return DoubleArray.times(M_Aab2RGB, new double[] {x, aa, bb});
  }

  /**
   * ︹华家Αは崩
   * @param color CIECAM02Color
   * @param vc ViewingConditions
   * @return CIEXYZ
   * @deprecated
   */
  public final static CIEXYZ inverse(CIECAM02Color color,
                                     ViewingConditions vc) {

    double a, ca, cb;
    double et, t;
    double p1, p2, p3, p4, p5, hr;

    double[] rgbW = XYZToCAT02(vc.white.getValues());

    t = Math.pow(color.C /
                 (Math.sqrt(color.J / 100.0) *
                  Math.pow(1.64 - Math.pow(0.29, vc.n), 0.73)),
                 (1.0 / 0.9));
    et = (1.0 / 4.0) *
        (Math.cos( ( (color.h * Math.PI) / 180.0) + 2.0) + 3.8);

    a = Math.pow(color.J / 100.0, 1.0 / (vc.c * vc.z)) * vc.aw;

    p1 = ( (50000.0 / 13.0) * vc._Nc * vc._Ncb) * et / t;
    p2 = (a / vc.nbb) + 0.305;
    p3 = 21.0 / 20.0;

    hr = (color.h * Math.PI) / 180.0;

    if (Math.abs(Math.sin(hr)) >= Math.abs(Math.cos(hr))) {
      p4 = p1 / Math.sin(hr);
      cb = (p2 * (2.0 + p3) * (460.0 / 1403.0)) /
          (p4 + (2.0 + p3) * (220.0 / 1403.0) *
           (Math.cos(hr) / Math.sin(hr)) - (27.0 / 1403.0) +
           p3 * (6300.0 / 1403.0));
      ca = cb * (Math.cos(hr) / Math.sin(hr));
    }
    else {
      p5 = p1 / Math.cos(hr);
      ca = (p2 * (2.0 + p3) * (460.0 / 1403.0)) /
          (p5 + (2.0 + p3) * (220.0 / 1403.0) -
           ( (27.0 / 1403.0) - p3 * (6300.0 / 1403.0)) *
           (Math.sin(hr) / Math.cos(hr)));
      cb = ca * (Math.sin(hr) / Math.cos(hr));
    }

    double[] rgbPa = _Aab2RGB(a, ca, cb, vc.nbb);
    double[] rgbP = new double[3];

    rgbP[0] = inverseNonlinearAdaptation(rgbPa[0], vc.FL);
    rgbP[1] = inverseNonlinearAdaptation(rgbPa[1], vc.FL);
    rgbP[2] = inverseNonlinearAdaptation(rgbPa[2], vc.FL);

    double[] XYZT = HPEToXYZ(rgbP);
    double[] rgbC = XYZToCAT02(XYZT);

    double[] rgb = new double[3];
    rgb[0] = rgbC[0] / ( ( (vc.white.Y * vc.d) / rgbW[0]) + (1.0 - vc.d));
    rgb[1] = rgbC[1] / ( ( (vc.white.Y * vc.d) / rgbW[1]) + (1.0 - vc.d));
    rgb[2] = rgbC[2] / ( ( (vc.white.Y * vc.d) / rgbW[2]) + (1.0 - vc.d));
//    double[] rgb2 = chromaticAdaptationTransform(vc, rgb, rgbW);

    double[] result = CAT02ToXYZ(rgb);
    return new CIEXYZ(result, vc.white);
  }

  public static void main(String[] args) {
//    example1(args);
    example2(args);
  }

  public static void example1(String[] args) {

    CIEXYZ white = new CIEXYZ(new double[] {95.02, 100, 108.81});

    CIEXYZ XYZ2 = new CIEXYZ(19.31, 23.93, 10.14, NormalizeY.Normal100);
//    CIEXYZ XYZ2 = new CIEXYZ(10, 20, 30, NormalizeY.Normal100);
//    CIEXYZ white = (CIEXYZ)this.white.clone();
//    white.normalizeY100();
//    double La = 20;

    ViewingConditions vc2 = new ViewingConditions(white, 63.6619, 20,
                                                  Surround.Dark, "Dim");
    CIECAM02 cam2 = new CIECAM02(vc2);
    CIECAM02Color myColor2 = cam2.forward(XYZ2);
    System.out.println(myColor2);
    System.out.println(CIECAM02.forward(XYZ2, vc2));
    System.out.println(CIECAM02.inverse(myColor2, vc2));
    System.out.println(cam2.inverse(myColor2));
  }

  public static void example2(String[] args) {
    CIEXYZ d65 = (CIEXYZ) Illuminant.D65WhitePoint.clone();
    d65.normalizeY100();

    //CIEXYZ white = new CIEXYZ(new double[] {95.047, 100.0, 108.83});
    double maxLuminance = 604.7;
//    ViewingConditions myVC = new ViewingConditions(d65, maxLuminance / 5, 20.0,
//        Surround.Average, "");

    double La = 43; //ViewingConditions.getAdaptingLuminance(150);
//    ViewingConditions myVC = ViewingConditions.getDarkViewingConditions(d65,
//        maxLuminance);
    double Yb = 10;
    ViewingConditions myVC = new ViewingConditions(d65, La, Yb, Surround.Dim,
        "Dim");

    CIECAM02 ciecam02 = new CIECAM02(myVC);
    CIEXYZ black = (CIEXYZ) d65.clone();
    double[] blackValues = black.getValues();
    black.setValues(DoubleArray.times(blackValues,
                                      1. / 566.38888888888888888888888888889));

//    black.times(1. / 1000);
    System.out.println("black XYZ: " + black);

    CIECAM02Color whitecolor = ciecam02.forward(d65);
    CIECAM02Color blackcolor = ciecam02.forward(black);
    System.out.println("white Q: " + whitecolor.Q + " " + whitecolor.J);
    System.out.println("black Q: " + blackcolor.Q + " " + blackcolor.J);
    System.out.println("PCL: " + (whitecolor.Q - blackcolor.Q));

//    ViewingConditions staticvc = new ViewingConditions(d65, 80 / 5, 20.0,
//        Surround.Average, "");
//    CIECAM02 staticcam = new CIECAM02(staticvc);
//    CIEXYZ staticwhite = staticcam.inverse(whitecolor);
//    CIEXYZ staticblack = staticcam.inverse(blackcolor);
//    System.out.println(staticwhite);
//    System.out.println(staticblack);
//    System.out.println(staticwhite.Y / staticblack.Y);

  }

  public final static boolean isModifyState() {
    return modifyState;
  }

}
