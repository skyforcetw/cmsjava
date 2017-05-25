package shu.cms.hvs.cam.ciecam02;

import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 以CIECAM02為基礎的色差公式.
 * (檢查ok,重點在於am/bm的計算)
 *
 * ref: M. R. Luo, C. Li and G. Cui,
 * Combining Colour Appearance Model with Colour Difference Formula,
 * ,AIC Colour 05
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CIECAM02DeltaE {
  public static enum Style {
    SCD, LCD, Normal, Urban
  }

  protected static class DeltaEColor {
    double J, M, a, b, C, h;

    protected DeltaEColor(CIECAM02Color color, Style deltaEType) {

      switch (deltaEType) {
        case LCD:
          J = (1.7 * color.J) / (1. + 0.007 * color.J);
          M = (1. / 0.0053) * Math.log(1. + 0.0053 * color.M);
          a = M * Math.cos( (color.h * Math.PI) / 180.0);
          b = M * Math.sin( (color.h * Math.PI) / 180.0);
          break;
        case SCD:
          J = (1.7 * color.J) / (1. + 0.007 * color.J);
          M = (1. / 0.0363) * Math.log(1. + 0.0363 * color.M);
          a = M * Math.cos( (color.h * Math.PI) / 180.0);
          b = M * Math.sin( (color.h * Math.PI) / 180.0);
          break;
        case Normal:
          J = color.J;
          a = color.ac;
          b = color.bc;
          break;
        case Urban:
          J = color.J;
          C = color.C;

//          h = color.h;
          h = Math.cos( (color.h * Math.PI) / 180.0);
      }

    }
  }

  public final static double getDeltaE(CIECAM02Color color1,
                                       CIECAM02Color color2,
                                       Style deltaEType) {
    return new CIECAM02DeltaE(color1, color2, deltaEType).getCIECAM02DeltaE();
  }

  public final double getCIECAM02DeltaE() {
    double dJ = deColor1.J - deColor2.J;

    if (deltaEType == Style.Urban) {
      double JBar = (deColor1.J + deColor2.J) / 2.;
      double CBar = (deColor1.C + deColor2.C) / 2.;
      double SJ = 0.5 + Maths.sqr(JBar / 100.);
      double SC = 1 + 0.02 * CBar;
      double Sh = 1 + 0.01 * CBar;
      double dC = deColor1.C - deColor2.C;
      double dh = deColor1.h - deColor2.h;
      return Math.sqrt(Maths.sqr(dJ / SJ) + Maths.sqr(dC / SC) +
                       Maths.sqr(dh / Sh));
    }

    double da = deColor1.a - deColor2.a;
    double db = deColor1.b - deColor2.b;
    return Math.sqrt(Maths.sqr(dJ / getKJ(deltaEType)) + Maths.sqr(da) +
                     Maths.sqr(db));
  }

  public final double getCIECAM02Deltaab() {
    if (deltaEType == Style.Urban) {
      throw new UnsupportedOperationException();
    }
    double da = deColor1.a - deColor2.a;
    double db = deColor1.b - deColor2.b;
    return Math.sqrt(Maths.sqr(da) + Maths.sqr(db));
  }

  public CIECAM02DeltaE(CIECAM02Color color1, CIECAM02Color color2,
                        Style deltaEType) {
    this.deColor1 = new DeltaEColor(color1, deltaEType);
    this.deColor2 = new DeltaEColor(color2, deltaEType);
    this.deltaEType = deltaEType;
  }

  protected DeltaEColor deColor1; // = new DeltaEColor(color1, deltaEType);
  protected DeltaEColor deColor2; // = new DeltaEColor(color2, deltaEType);
  protected Style deltaEType;

  protected final static double getKJ(Style deltaEType) {
    switch (deltaEType) {
      case SCD:
        return 1.24;
      case LCD:
        return 0.77;
      case Normal:
        return 1.;
      default:
        return -1;
    }
  }

}
