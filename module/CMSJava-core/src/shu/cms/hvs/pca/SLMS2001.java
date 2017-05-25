package shu.cms.hvs.pca;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.cms.plot.*;
import shu.math.*;
//import shu.plot.*;

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
public class SLMS2001
    extends SLMS implements DFactorIF {
  public SLMS2001(ViewingConditions vc) {
    super(vc, CAMConst.CATType.CIECAM97s2);
//    this.initDFactorIF(this);
  }

  public static void main(String[] args) {
    Plot2D p = Plot2D.getInstance();

    for (double Y : new double[] {35, 70}) {
      for (int CCT = 3000; CCT <= 17000; CCT += 100) {
        CIExyY xyY = CorrelatedColorTemperature.CCT2BlackbodyxyY(CCT);
        xyY.Y = Y;
        CIEXYZ display = xyY.toXYZ();
        ViewingConditions vc = new ViewingConditions(display,
            new CIEXYZ(), Surround.Dark);

        PartialChromaticAdaptation pca = new SLMS(vc);
//        PartialChromaticAdaptation pca = new SLMS2001(vc);

        CIEXYZ aw = pca.getAdaptedDisplayWhite();
//        CIEXYZ aw = pca.getAdaptedWhite();
        double awCCT = CorrelatedColorTemperature.XYZ2CCTByRobertson(aw);
        p.addCacheScatterLinePlot(Double.toString(Y), CCT, awCCT);
      }

    }
    p.setVisible();
  }

  public double getDFactor() {
    double F = shu.cms.hvs.cam.ciecam02.ViewingConditions.getF(vc.surround);
    double YA = displayWhite.Y;
    double D = F *
        (1 - 1. / (1 + 2 * Math.pow(YA, 1. / 4) + Maths.sqr(YA) / 300));
    return D;
  }

  protected double[] getdLMSValues() {
    if (dLMSValues == null) {
      dLMSValues = getLMSValues(normalDisplayWhite.getValues());
      double D = getDFactor();
      dLMSValues[0] = D + dLMSValues[0] * (1 - D);
      dLMSValues[1] = D + dLMSValues[1] * (1 - D);
      dLMSValues[2] = D + dLMSValues[2] * (1 - D);
    }

    return dLMSValues;
  }

  private double[] dLMSValues;

  /**
   * incompleteChromaticAdaptation
   *
   * @param XYZ CIEXYZ
   * @return LMS
   */
//  protected LMS incompleteChromaticAdaptation(CIEXYZ XYZ) {
//    double[] dLMSValues = getdLMSValues();
//    LMS lms = this.getLMS(XYZ);
//    lms.L /= dLMSValues[0];
//    lms.M /= dLMSValues[1];
//    lms.S /= dLMSValues[2];
//    return lms;
//  }

}
