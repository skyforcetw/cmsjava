package shu.cms.lcd.benchmark.diqam;

import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.math.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class LowGrayReproduction
    extends GrayScaleFidelity {
  /**
   * LowGrayReproduction
   *
   * @param lcdTarget LCDTarget
   */
  public LowGrayReproduction(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  public void report(double[] idealGammaCurve) {
    super.report(idealGammaCurve);
  }

  protected String getPlotName() {
    return "GrayScaleFidelity";
  }

  public Plot2D getPlot() {
    plot2D = Plot2D.getInstance("GrayScaleFidelity");
    double[] testJNDICurvePrime = Maths.firstOrderDerivatives(testJNDICurve);
    double[] idealJNDICurvePrime = Maths.firstOrderDerivatives(idealJNDICurve);
    plot2D.addLinePlot("Test", 1, 255, testJNDICurvePrime);
    plot2D.addLinePlot("Ideal", 1, 255, idealJNDICurvePrime);
    plot2D.setAxeLabel(0, "Gray");
    plot2D.setAxeLabel(1, "dJNDI");
    plot2D.setFixedBounds(0, 0, 255);
    plot2D.addLegend();
    plot2D.setVisible();
    return plot2D;
  }

  public static void main(String[] args) {
    LCDTarget ramp = LCDTarget.Instance.getFromAUORampXLS(
        "D:\\My Documents\\工作\\華山計畫\\LG 42SL90QD\\Standard\\ramp.xls");
    LCDTarget.Operator.gradationReverseFix(ramp);

    LowGrayReproduction report = new LowGrayReproduction(ramp);
    report.report(2.4);
//    System.out.println(fidelity.getRevisedStd());
//    System.out.println(fidelity.getY());
    report.getPlot();
  }
}
