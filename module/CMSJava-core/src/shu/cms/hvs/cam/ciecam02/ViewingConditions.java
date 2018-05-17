package shu.cms.hvs.cam.ciecam02;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class ViewingConditions
    implements CAMViewingConditions {
  public CIEXYZ white;
  /**
   * AdaptingLuminance
   */
  public double LA;
  protected double aw;
  /**
   * Specifies the Degree of Adaptation
   */
  public double d;
  public String description;

  /**
   * BackgroundLuminance
   */
  protected double _Yb;
  protected Surround surround;
  /**
   * SurroundImpact, impact of surrounding
   */
  protected double c;
  /**
   * ChromaticInduction, 色誘導係數, chromatic induction factor
   */
  protected double _Nc;
  /**
   * luminance level adaptation factor (FL)
   */
  protected double FL;
  /**
   * factor determining degree of adaptation
   */
  protected double F;
  /**
   * Function of the luminance factor of the background and provides a very
   *  limited model of spatial color appearance.<br>
   * The value of n ranges from 0 for a background luminance factor of zero to
   *  1 fro background luminance factor equal to tje luminance factor of adopted white point.
   */
  protected double n;
  protected double z, nbb, _Ncb;

  public ViewingConditions(CIEXYZ white, double LA, double Yb,
                           Surround surround, String description) {
    this.white = (CIEXYZ) white.clone();
    this.white.normalizeY100();
    this.LA = LA;
    this._Yb = Yb;
    if (surround != null) {
      this.surround = surround;
      judgeSurround(surround);
      computeParameters();
    }

    this.description = description;
  }

  /**
   *
   * @param white CIEXYZ Relative Tristimulus Values of the White, 光源本身的三刺激值
   * @param LA double Adapting Field Luminance in cd/m2 (often 20% of the luminance of white), 適應區亮度(cd/m\u00B2)
   * @param Yb double Relative Luminance of the Background, 原始環境背景相對亮度值(cd/m\u00B2)
   * @param surround Surround
   * @param description String
   * @param d double Specifies the Degree of Adaptation:
   * D = 1.0, (Complete Adaptation or Discounting)
   * D = 0.0, (No Adaptation)
   * D in Between, (Various Degrees of Incomplete Adaptation)

   */
  public ViewingConditions(CIEXYZ white, double LA, double Yb,
                           Surround surround, String description, double d) {
    this(white, LA, Yb, surround, description);
    this.d = d;
  }

  public final static double getF(Surround surround) {
    switch (surround) {
      case Average:
        return 1.0;
      case Dim:
        return .9;
      case Dark:
        return .8;
      default:
        return -1;
    }
  }

  /**
   * 依照surround的值,給定相對應的係數的值,包括F/c/Nc等
   * @param surround Surround
   */
  protected void judgeSurround(Surround surround) {
    switch (surround) {
      case Average:
        this.c = 0.69;
        this._Nc = 1.00;
        break;
      case Dim:
        this.c = 0.59;

        //The CIECAM02 Color Apperance Model論文中是0.95, wiki也是0.95
//        this._Nc = 0.95;
        //網站 http://www.hpl.hp.com/personal/Nathan_Moroney/ciecam02/ciecam02.html 是採用0.9
        //Failrchild的Color Appearance Models也是0.9
        this._Nc = 0.9;
        break;
      case Dark:
        this.c = 0.525;
        this._Nc = 0.800;
        break;
      default:
    }
    this.F = getF(surround);
  }

  protected void computeParameters() {
    this.n = computeN(this);
    this.z = computeZ(this);
    this.FL = computeFL(this);
    this.nbb = computeNbb(this);
    this._Ncb = this.nbb;
    this.d = computeD(this);
    this.aw = achromaticResponse2White(this);
  }

  protected final static double achromaticResponse2White(ViewingConditions vc) {
    double[] rgb = CIECAM02.XYZToCAT02(vc.white.getValues());
    double[] rgbC = CIECAM02.chromaticAdaptationTransform(vc, rgb, rgb);

    double[] rgbP = CIECAM02.CAT02ToHPE(rgbC);
    double[] rgbPa = CIECAM02.nonlinearAdaptation(rgbP, vc.FL);
    return CIECAM02.A(vc, rgbPa);
  }

  /**
   * Theoretically, D ranges from
   *     0 = no adaptation to the adopted white point,
   *  to 1 = complete adaptation to the adopted white point.
   * In practice, the minimum D value will not be less than 0.65 for a
   * dark surround and exponentially converges to 1 for average surrounds
   * with increasingly large values of L_A.
   *
   * L_A is the luminance of the adapting field in cd/m^2.
   */

  /**
   * degree of adaptation
   * @param vc ViewingConditions
   * @return double
   */
  protected final static double computeD(ViewingConditions vc) {
    //D(8)
    return vc.F *
        (1.0 - ( (1.0 / 3.6) * Math.exp( ( -vc.LA - 42.0) / 92.0)));
//    double d = (vc.F *
//                (1.0 - ( (1.0 / 3.6) * Math.exp( ( -vc.LA - 42.0) / 92.0))));
//    double d2 = (vc.F - ( (1.0 / 3.6) * Math.exp( ( -vc.LA - 42.0) / 92.0)));
//    return (vc.F - ( (1.0 / 3.6) * Math.exp( ( -vc.LA - 42.0) / 92.0)));
  }

  protected final static double computeN(ViewingConditions vc) {
    //(3)
    return (vc._Yb / vc.white.Y);
  }

  protected final static double computeZ(ViewingConditions vc) {
    //(5)
    return (1.48 + Math.pow(vc.n, 0.5));
  }

  protected final static double computeNbb(ViewingConditions vc) {
    //(4)
    return (0.725 * Math.pow( (1.0 / vc.n), 0.2));
  }

  protected final static double computeFL(ViewingConditions vc) {
    return computeFL(vc.LA);
//    double k, fl;
//    //(1)
//    k = 1.0 / ( (5.0 * vc.LA) + 1.0);
//    //(2)
//    fl = 0.2 * Math.pow(k, 4.0) * (5.0 * vc.LA) + 0.1 *
//        (Math.pow( (1.0 - Math.pow(k, 4.0)), 2.0)) *
//        (Math.pow( (5.0 * vc.LA), (1.0 / 3.0)));
//    return fl;
  }

  public final static double computeFL(double LA) {
    double k = 1.0 / ( (5.0 * LA) + 1.0);
//(2)
    double fl = 0.2 * Math.pow(k, 4.0) * (5.0 * LA) + 0.1 *
        (Math.pow( (1.0 - Math.pow(k, 4.0)), 2.0)) *
//        (Math.pow( (5.0 * LA), (1.0 / 3.0)));
        Maths.cubeRoot(5.0 * LA);
    return fl;

  }

//  protected final static double achromaticResponse2White(ViewingConditions vc) {
//    double[] rgb = CIECAM02.XYZToCAT02(vc.white.getValues());
//    double[] rgbC = CIECAM02.chromaticAdaptationTransform(vc, rgb, rgb);
//
//    double[] rgbP = CIECAM02.CAT02ToHPE(rgbC);
//    double[] rgbPa = CIECAM02.nonlinearAdaptation(rgbP, vc.FL);
//    return CIECAM02.A(vc, rgbPa);
//  }
  private static CIEXYZ D50;
  private static CIEXYZ D65;
  private static CIEXYZ C;

  static {
    D50 = (CIEXYZ) Illuminant.D50WhitePoint.clone();
    D50.times(100.);
    D65 = (CIEXYZ) Illuminant.D65WhitePoint.clone();
    D65.times(100.);
    C = (CIEXYZ) Illuminant.C.getNormalizeXYZ().clone();
    C.times(100.);
  }

  /**
   * Munsell建議參考環境
   * C, 80 (when abs white is 400nits), 20, ViewingConditions.Surround.Average
   */
  public final static ViewingConditions MunsellReferenceViewingConditions = new
      ViewingConditions(C, 80, 20, Surround.Average,
                        "CIE Reference Viewing Conditions");

  /**
   * sRGB建議觀測環境
   * D65, 4.07, 20, ViewingConditions.Surround.Average
   */
  public final static ViewingConditions sRGBReferenceViewingConditions =
      getsRGBLikeViewingConditions(D65);
//  new ViewingConditions(D65, 4.07, 20, Surround.Average,
//                        "sRGB Reference Viewing Conditions", 1.);

  /**
   * sRGB記載典型的觀測環境(一般office)
   * D65, 12.73, 20, ViewingConditions.Surround.Average
   */
  public final static ViewingConditions TypicalViewingConditions =
      getTypicalViewingConditions(D65);
//      new ViewingConditions(D65, 12.73, 20, Surround.Average,
//                            "sRGB Typical Viewing Conditions", 1.);

  /**
   * ICCProfile建議的perceptual intent觀測環境
   * D50, 31.83, 20, ViewingConditions.Surround.Average
   */
  public final static ViewingConditions PerceptualIntentViewingConditions = new
      ViewingConditions(D50, 31.83, 20, Surround.Average, "Perceptual", 1.);
  /**
   * D50, 0.1, 20, ViewingConditions.Surround.Dim
   */
  public final static ViewingConditions DimViewingConditions = new
      ViewingConditions(D50, 0.1, 20, Surround.Dim, "Dim");

  public final static ViewingConditions CIEReferenceViewingConditions = new
      ViewingConditions(D65, 64, 20, Surround.Average,
                        "CIE Reference Viewing Conditions");

  public final static ViewingConditions getDimViewingConditions(CIEXYZ white) {
    ViewingConditions vc = new ViewingConditions(white, 0.1, 20, Surround.Dim,
                                                 "Dim");
    return vc;
  }

  public final static ViewingConditions getDimViewingConditions(CIEXYZ white,
      double whiteLuminance) {
    double La = whiteLuminance / 5;
    ViewingConditions vc = new ViewingConditions(white, La, 20, Surround.Dim,
                                                 "Dim");
    return vc;
  }

  public final static ViewingConditions getsRGBLikeViewingConditions(
      CIEXYZ white) {
    CIEXYZ vcWhite = (CIEXYZ) white.clone();
    vcWhite.times(100.);
    ViewingConditions vc = new
        ViewingConditions(vcWhite, 4.07, 20, Surround.Average,
                          "sRGB Reference Viewing Conditions", 1.);

    return vc;
  }

  public final static ViewingConditions getTypicalViewingConditions(
      CIEXYZ white) {
    CIEXYZ vcWhite = (CIEXYZ) white.clone();
    vcWhite.times(100.);
    ViewingConditions vc = new
        ViewingConditions(vcWhite, 12.73, 20, Surround.Average,
                          "sRGB Typical Viewing Conditions", 1.);

    return vc;
  }

//  public final static ViewingConditions getDarkViewingConditions(CIEXYZ white) {
//    ViewingConditions vc = new ViewingConditions(white, 0.1, 20, Surround.Dark,
//                                                 "Dark");
//    return vc;
//  }

  public final static ViewingConditions getDarkViewingConditions(CIEXYZ white,
      double whiteLuminance) {
    double La = whiteLuminance / Math.PI; //from 現代顏色技術原理及應用 p158
    ViewingConditions vc = new ViewingConditions(white, La, 20, Surround.Dark,
                                                 "Dark");
    return vc;
  }

  /*static {
    CIEXYZ D50 = (CIEXYZ) Illuminant.D50WhitePoint.clone();
    D50.times(100.);
    CIEXYZ D65 = (CIEXYZ) Illuminant.D65WhitePoint.clone();
    D65.times(100.);
    CIEXYZ C = (CIEXYZ) Illuminant.C.getNormalizeXYZ().clone();
    C.times(100.);

    sRGBReferenceViewingConditions = new ViewingConditions(D65, 4.07, 20,
        Surround.Average, "sRGB Reference Viewing Conditions",
        1.);

    TypicalViewingConditions = new ViewingConditions(D65, 12.73, 20,
        Surround.Average, "sRGB Typical Viewing Conditions",
        1.);
    PerceptualIntentViewingConditions = new ViewingConditions(D50, 31.83, 20,
        Surround.Average, "Perceptual", 1.);

    DimViewingConditions = new ViewingConditions(D50, 0.1, 20,
                                                 Surround.Dim,
                                                 "Dim");

    CIEReferenceViewingConditions = new ViewingConditions(D65, 64, 20,
        Surround.Average, "CIE Reference Viewing Conditions");

    MunsellReferenceViewingConditions = new ViewingConditions(C, 80, 20,
        Surround.Average, "CIE Reference Viewing Conditions");
     }*/
  public final static double getAdaptingLuminance(double illuminance) {
    return illuminance / Math.PI * 0.2;
  }
};
