package shu.thesis.lcd;

import java.text.*;

import shu.cms.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.devicemodel.lcd.thread.*;
import shu.cms.lcd.*;
import shu.math.*;
import shu.math.Polynomial.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 找尋各種模式下的最佳訓練色塊
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class TrainingTargetFinder {
  public static enum Model {
    GOG, GOG2, GOPLCC, GOGO, PLCC, SC1, SC2, POLY, MAT, SYY
  }

  /**
   *
   * @param model Model
   * @param target LCDTarget
   * @return LCDModel
   * @deprecated
   */
  protected final static LCDModel getLCDModel(Model model, LCDTarget target) {
    switch (model) {
      case PLCC:
        return new PLCCModel(target);
      case GOG:
        return new GOGModelThread(target);
      case GOG2:
        return new GOGModelThread2(target);
      case GOGO:
        return new GOGOModelThread(target);
      case GOPLCC:
        return new GOPLCCModelThread(target);
      case SC1:
        return new SCurveModel1Thread(target);
      case SC2:
        return new SCurveModel2Thread(target);
      case SYY:
        return new SimpleYYModel(target);
      case POLY:
        return new LCDPolynomialRegressionModel(target, COEF_3.BY_19C);
      case MAT:
        return new LCDPolynomialRegressionModel(target, COEF_3.BY_3);
      default:
        return null;
    }
  }

  public void find(String device, Model model) {
    LCDTargetBase.Number[] numberArray = LCDTargetBase.Number.values();

    LCDTarget testTarget = LCDTarget.Instance.get(device,
                                                  LCDTarget.Source.i1pro,
                                                  LCDTarget.Room.Dark,
                                                  LCDTarget.TargetIlluminant.
                                                  D65,
                                                  LCDTargetBase.Number.Test4096, null, null);

    for (LCDTargetBase.Number number : numberArray) {
      if (number == LCDTargetBase.Number.Test4096) {
        continue;
      }

      LCDTarget trainingTarget = LCDTarget.Instance.get(device,
          LCDTarget.Source.i1pro,
          LCDTarget.Room.Dark,
          LCDTarget.TargetIlluminant.D65,
          number, null, null);

      LCDModel lcdModel = getLCDModel(model, trainingTarget);
      LCDModel.Factor[] factors = lcdModel.produceFactor();

      DeltaEReport[] testReports = lcdModel.testForwardModel(testTarget, false);
//      System.out.println("Training: " + trainingTarget.getDescription());
//      System.out.println(Arrays.toString(testReports));
//      System.out.println(lcdModel.getWhiteDeltaE().getCIE2000DeltaE() + "\n");

      DeltaEReport r = testReports[0];
      StringBuilder buf = new StringBuilder();

      buf.append(Utils.fmt(r.meanDeltaE.getCIE2000DeltaE()) + " ");
      buf.append(Utils.fmt(r.minDeltaE.getCIE2000DeltaE()) + " ");
      buf.append(Utils.fmt(r.maxDeltaE.getCIE2000DeltaE()) + " ");
      buf.append(Utils.fmt(r.mixDeltaE.getCIE2000DeltaE()) + " ");
      buf.append(Utils.fmt(r.stdDeltaE.getCIE2000DeltaE()) + " ");
      buf.append(Utils.fmt(r.meanCIE2000DeltaLCH.getCIE2000DeltaLCh()) + " ");
      buf.append(Utils.fmt(lcdModel.getWhiteDeltaE().getCIE2000DeltaE()));
      System.out.println(buf);
    }

  }

  public static void main(String[] args) {
    DeltaEReport.setDecimalFormat(new DecimalFormat("##.###"));
    String device = "Dell_2407WFP_HC";

    TrainingTargetFinder finder = new TrainingTargetFinder();

    Model[] modelArray = Model.values();
//    Model[] modelArray = new Model[]{Model.POLY};

    for (Model m : modelArray) {
      if (m == Model.SC2) {
//        continue;
      }
      /*System.out.println(
          "======================================================");
             System.out.println(m);
             System.out.println(
          "======================================================");*/
      finder.find(device, m);
    }

  }
}
