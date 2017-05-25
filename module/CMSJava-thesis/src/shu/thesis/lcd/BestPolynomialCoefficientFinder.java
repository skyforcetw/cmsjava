package shu.thesis.lcd;

import shu.cms.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.math.*;
import shu.math.Polynomial.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 比較各種項數的多項式色差
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class BestPolynomialCoefficientFinder {
  public static void main(String[] args) {
    String device = "EIZO_CG221_2";
    LCDTarget lcdTarget = LCDTarget.Instance.get(device,
                                                 LCDTarget.Source.i1pro,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.D65,
                                                 LCDTargetBase.Number.Test729, null, null);

    LCDTarget lcdTestTarget = LCDTarget.Instance.get(device,
        LCDTarget.Source.i1pro,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.D65,
        LCDTargetBase.Number.Test4096, null, null);

    for (Polynomial.COEF_3 coef : Polynomial.COEF_3.values()) {
      LCDPolynomialRegressionModel model = new LCDPolynomialRegressionModel(
          lcdTarget);
      model.setCoefficientCount(coef);
//      System.out.println(coef);

      LCDModelBase.Factor[] factors = model.produceFactor();

      DeltaEReport[] testReports = model.testForwardModel(lcdTestTarget, false);

      DeltaEReport r = testReports[0];
      StringBuilder buf = new StringBuilder();

      //========================================================================
      // 前導
      //========================================================================
      buf.append(Utils.fmt(r.meanDeltaE.getCIE2000DeltaE()) + " ");
      buf.append(Utils.fmt(r.minDeltaE.getCIE2000DeltaE()) + " ");
      buf.append(Utils.fmt(r.maxDeltaE.getCIE2000DeltaE()) + " ");
      buf.append(Utils.fmt(r.mixDeltaE.getCIE2000DeltaE()) + " ");
      buf.append(Utils.fmt(r.stdDeltaE.getCIE2000DeltaE()) + " ");
      buf.append(Utils.fmt(r.meanCIE2000DeltaLCH.getCIE2000DeltaLCh()) + " ");
      buf.append(Utils.fmt(model.getWhiteDeltaE().getCIE2000DeltaE()));
      //========================================================================

      DeltaEReport[] testReverseReports = model.testReverseModel(lcdTestTarget, false);
      r = testReverseReports[0];

      //========================================================================
      // 反推
      //========================================================================
//      buf.append(LCDUtils.fmt(r.meanDeltaE.getCIE2000DeltaE()) + " ");
//      buf.append(LCDUtils.fmt(r.minDeltaE.getCIE2000DeltaE()) + " ");
//      buf.append(LCDUtils.fmt(r.maxDeltaE.getCIE2000DeltaE()) + " ");
//      buf.append(LCDUtils.fmt(r.mixDeltaE.getCIE2000DeltaE()) + " ");
//      buf.append(LCDUtils.fmt(r.stdDeltaE.getCIE2000DeltaE()) + " ");
//      buf.append(LCDUtils.fmt(r.meanCIE2000DeltaLCH.getCIE2000DeltaLCH()) + " ");
//      buf.append(LCDUtils.fmt(model.getWhiteDeltaE().getCIE2000DeltaE()));
      //========================================================================

      System.out.println(buf);
//      System.out.println(
//          "======================================================");
    }

  }
}
