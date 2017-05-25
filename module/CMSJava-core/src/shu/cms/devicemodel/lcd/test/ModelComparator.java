package shu.cms.devicemodel.lcd.test;

import shu.cms.devicemodel.lcd.*;
import shu.cms.devicemodel.lcd.util.*;
import shu.cms.devicemodel.lcd.xtalk.*;
import shu.cms.lcd.*;
import shu.cms.lcd.material.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ModelComparator {
  public static void main(String[] args) {
//    LCDTarget target = LCDTargetMaterial.getCPT170EA();
    LCDTarget target = LCDTargetMaterial.getCPT320WF01SC_0227();
//    LCDTarget target = LCDTargetMaterial.getHannstar100IFW1();

    LCDTarget ramp = target.targetFilter.getRamp();
    LCDTarget xtalk = target.targetFilter.getXtalk();
    LCDTarget test = target.targetFilter.getTest();

    LCDTarget.Operator.gradationReverseFix(ramp);

    //==========================================================================
//    LCDModel model = new PLCCModel(ramp);
//    LCDModel model = new LCDPolynomialRegressionModel(test,
//        Polynomial.COEF_3.BY_19C);
//    LCDModel model = new MultiMatrixModel(ramp, ramp);
    LCDModel model = new AdjacentPixelXtalkModel(ramp, xtalk);
//    LCDModel model = AdjacentPixelXtalkModel.getRecommendXTalkPropertyModel(
//        ramp, xtalk);
    //==========================================================================
    model.produceFactor();

//    if (model instanceof AdjacentPixelXtalkModel) {
//      ( (AdjacentPixelXtalkModel) model).plotCorrectLUT();
//    }
    ModelReport report = model.report.getModelReport(test);
    System.out.println(report);

  }
}
