package auo.cms.test.intensity;

import shu.cms.plot.Plot2D;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CoordinatorPlotter {

  public static void main(String[] args) {
    Plot2D cplot = Plot2D.getInstance("chromaticity");
    Plot2D plot = Plot2D.getInstance("");
    double gammax = 1.5;
    double gammay = 2;
    for (int x = 0; x <= 50; x++) {
      double normal = x / 50.;
      double powerx = Math.pow(normal, gammax);
      double powery = Math.pow(normal, gammay);
      double gammaxResult = 50 * powerx;
      double gammayResult = 50 * powery;
//      System.out.println(gammaResult);
      cplot.addCacheScatterLinePlot("", gammaxResult, gammayResult);
      plot.addCacheScatterLinePlot("x", x, gammaxResult);
      plot.addCacheScatterLinePlot("y", x, gammayResult);
    }
    cplot.setVisible();
    plot.setVisible();
  }
}
