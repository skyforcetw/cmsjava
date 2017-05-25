package shu.cms.plot.test;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
///import shu.plot.*;

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
public class ColorShiftVerifier {
  public static void main(String[] args) {
    LCDTarget lcdTarget1 = LCDTarget.Instance.getFromAUOXLS(
        "D:\\My Documents\\工作\\華山計畫\\LG 42SL90QD\\Standard\\2223.xls");
    LCDTarget lcdTarget2 = LCDTarget.Instance.getFromAUOXLS(
        "D:\\My Documents\\工作\\華山計畫\\LG 42SL90QD\\Standard\\2223-standard gamut.xls");

    LCDTarget test1 = lcdTarget1.targetFilter.getSurface1352From2223();
    LCDTarget test2 = lcdTarget2.targetFilter.getSurface1352From2223();
//    LCDTarget test1 = lcdTarget1.targetFilter.getWHQL();
//    LCDTarget test2 = lcdTarget2.targetFilter.getWHQL();

    Plot2D plot = Plot2D.getInstance();
    int size = test1.size();
    for (int x = 0; x < size; x++) {
      Patch p1 = test1.getPatch(x);
      Patch p2 = test2.getPatch(x);
      CIELab Lab1 = p1.getLab();
      CIELab Lab2 = p2.getLab();
//      System.out.println(Lab1 + " " + Lab2);
      int i = plot.addScatterPlot(p1.getRGB().toString(), p1.getRGB().getColor(),
                                  Lab1.a, Lab1.b);

      plot.addVectortoPlot(i, new double[][] {new double[] {Lab2.a - Lab1.a,
                           Lab2.b - Lab1.b}
      });
//      plot.addVectortoPlot(i, new double[][] {new double[] {Lab1.a - Lab2.a,
//                           Lab1.b - Lab2.b}
//      });
    }
//    new LocusPlot(plot).drawCIELabLocus();
    plot.setAxeLabel(0, "a*");
    plot.setAxeLabel(1, "b*");
//    plot.addLegend();
    plot.setFixedBounds(0, -100, 100);
    plot.setFixedBounds(1, -100, 100);
    plot.setVisible();
  }
}
