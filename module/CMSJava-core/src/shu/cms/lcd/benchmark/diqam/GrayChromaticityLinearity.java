package shu.cms.lcd.benchmark.diqam;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
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
public class GrayChromaticityLinearity
    extends DIQAM {
  public GrayChromaticityLinearity(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  public GrayChromaticityLinearity(String title, LCDTarget lcdTarget) {
    super(title, lcdTarget);
  }

  public void report() {
    LCDTarget ramp = lcdTarget.targetFilter.getRamp256W();
    CIExyY whitexyY = new CIExyY(ramp.getWhitePatch().getXYZ());
    List<Patch> patchList = ramp.getPatchList();
    int size = patchList.size();
    deltauvpArray = new double[size];
    double total = 0;

    for (int x = 0; x < size; x++) {
      CIExyY xyY = new CIExyY(patchList.get(x).getXYZ());
      double[] duvpArray = xyY.getDeltauvPrime(whitexyY);
      double duvp = Math.sqrt(Maths.sqr(duvpArray[0]) + Maths.sqr(duvpArray[1]));
      deltauvpArray[x] = duvp;
      total += (deltauvpArray[x] / 0.004) * 10;
    }
    linearityIndex = total / size;
  }

  private double[] deltauvpArray;
  private double linearityIndex;

  protected String getPlotName() {
    return "GrayChromaticityLinearity";
  }

  public Plot2D getPlot() {
    plot2D = _getPlot();
    plot2D.addLinePlot("", 0, deltauvpArray.length - 1, deltauvpArray);
    plot2D.addLinePlot("0.004", 0, 0.004, deltauvpArray.length - 1, 0.004);
    plot2D.setAxeLabel(0, "Gray");
    plot2D.setAxeLabel(1, "du'v'");
    plot2D.setFixedBounds(0, 0, 255);
    plot2D.addLegend();
    plot2D.setVisible();
    return plot2D;
  }

  public double getLinearityIndex() {
    return linearityIndex;
  }

  public static void main(String[] args) {
    LCDTarget ramp = LCDTarget.Instance.getFromAUORampXLS(
        "D:\\My Documents\\工作\\華山計畫\\LG 42SL90QD\\Standard\\ramp.xls");
    LCDTarget.Operator.gradationReverseFix(ramp);

    GrayChromaticityLinearity report = new GrayChromaticityLinearity(ramp);
    report.report();
//    System.out.println(fidelity.getRevisedStd());
//    System.out.println(fidelity.getY());
    System.out.println(report.getLinearityIndex());
    report.getPlot();

  }
}
