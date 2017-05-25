package shu.cms.lcd.benchmark.diqam;

import shu.cms.colorspace.depend.*;
import shu.cms.hvs.*;
import shu.cms.lcd.*;
import shu.cms.lcd.calibrate.*;
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
public class GrayScaleFidelity
    extends DIQAM {
  public GrayScaleFidelity(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  public GrayScaleFidelity(String title, LCDTarget lcdTarget) {
    super(title, lcdTarget);
  }

  public void report(double idealGamma) {
    GammaCalibrator calibrator = new GammaCalibrator(lcdTarget);
    double[] idealGammaCurve = calibrator.whiteCalibrate(idealGamma, RGB.White);
    report(idealGammaCurve);
  }

  private final static double getRevisedStd(double[] test, double[] ideal) {
    if (test.length != ideal.length) {
      throw new IllegalArgumentException("test.length != ideal.length");
    }
    int size = test.length;
    double total = 0;
    for (int x = 0; x < size; x++) {
      double v = (ideal[x] - test[x]) / ideal[x];
      total += Maths.sqr(v);
    }
    double std = Math.sqrt(total / (size - 1));
    return std;
  }

  public static void main(String[] args) {
    LCDTarget ramp = LCDTarget.Instance.getFromAUORampXLS(
        "D:\\My Documents\\工作\\華山計畫\\LG 42SL90QD\\Standard\\ramp.xls");
    LCDTarget.Operator.gradationReverseFix(ramp);

    GrayScaleFidelity fidelity = new GrayScaleFidelity(ramp);
    fidelity.report(2.4);
    System.out.println(fidelity.getRevisedStd());
    System.out.println(fidelity.getY());
    fidelity.getPlot();
  }

  public void report(double[] idealGammaCurve) {
    GSDF dicom = GSDF.getDICOMInstance();
    idealJNDICurve = GSDF.getJNDICurve(idealGammaCurve, dicom);
    double[] testGammaCurve = lcdTarget.targetFilter.getRamp256W().filter.
        YArray();
    testJNDICurve = GSDF.getJNDICurve(testGammaCurve, dicom);
    this.revisedStd = getRevisedStd(testJNDICurve, idealJNDICurve);
    if (revisedStd > 0 && revisedStd < 0.5) {
      this.Y = -20 * revisedStd + 10;
    }
    else {
      this.Y = 0;
    }
  }

  protected double[] testJNDICurve;
  protected double[] idealJNDICurve;

  public double getRevisedStd() {
    return revisedStd;
  }

  public double getY() {
    return Y;
  }

  protected String getPlotName() {
    return "GrayScaleFidelity";
  }

  public Plot2D getPlot() {
    plot2D = _getPlot();
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

  private double revisedStd;
  private double Y;
}
