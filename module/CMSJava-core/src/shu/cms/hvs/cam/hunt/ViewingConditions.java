package shu.cms.hvs.cam.hunt;

import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ViewingConditions
    implements CAMViewingConditions {
  public CIEXYZ white;
  double _Nc, _Nb;
  double LA, LAS;
  double _Yb;
  double _Ncb, _Nbb;
  Situation situation;
  String description;
  double d;

  public ViewingConditions(CIEXYZ white, double LA, double Yb,
                           Surround surround, String description, double d) {
    this(white, LA, Yb, Situation.getSituation(surround), description, d);
  }

  public ViewingConditions(CIEXYZ white, double LA, double Yb,
                           Situation situation, String description, double d) {
    this.white = white;
    this.LA = LA;
    this._Yb = Yb;
    this.situation = situation;
    judgeSituation(situation);
    this.description = description;
    this.d = d;

    this.LAS = this.computeLAS(this);
    this._Ncb = _Nbb = this.computeNcb(this);
  }

  public static enum Situation {
    SmallAreasInUniformBackgroundsAndSurrounds(1.0, 300),
    NormalScenes(1.0, 75),
    TVandCRTInDimSurrounds(1.0, 25),
    LargeTransparenciesOnLightBox(0.7, 25),
    ProjectedTransparenciesInDarkSurrounds(0.7, 10);

    Situation(double Nc, double Nb) {
      this._Nc = Nc;
      this._Nb = Nb;
    }

    protected double _Nc, _Nb;

    public final static Situation getSituation(Surround surround) {
      switch (surround) {
        case AverageAbove4:
          return NormalScenes;
        case Average:
          return SmallAreasInUniformBackgroundsAndSurrounds;
        case Dim:
          return TVandCRTInDimSurrounds;
        case Dark:
          return ProjectedTransparenciesInDarkSurrounds;
        case CutSheet:
          return LargeTransparenciesOnLightBox;
        default:
          return null;
      }
    }
  }

  protected final static double computeLAS(ViewingConditions vc) {
    double cct = vc.white.getCCT();
    //(12.1)
    double LAS = 2.26 * vc.LA * Maths.cubeRoot( (cct / 4000.) - 0.4);
//    double LAS = 2.26 * vc.LA * Math.pow( (cct / 4000.) - 0.4, 1. / 3);

    return LAS;
  }

  protected final static double computeNcb(ViewingConditions vc) {
    double Yw = vc.white.Y;
    //(12.2),(12.3)
    double Ncb = 0.725 * Math.pow(Yw / vc._Yb, 0.2);
    return Ncb;
  }

  protected final void judgeSituation(Situation situation) {
    _Nc = situation._Nc;
    _Nb = situation._Nb;
  }

}
