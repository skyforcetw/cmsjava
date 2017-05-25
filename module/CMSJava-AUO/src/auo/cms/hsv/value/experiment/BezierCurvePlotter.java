package auo.cms.hsv.value.experiment;

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
 * @author not attributable
 * @version 1.0
 */
public class BezierCurvePlotter {
  public BezierCurvePlotter() {
  }

  public static void main(String[] args) {
//    double p0=0.5;
    double p1x = 0.19;
    double p1y = 0.5;

    Plot2D plot = Plot2D.getInstance();
    int piece = 30;

//    for (double t = 0; t <= 1.0; t += 0.05) {
    for (int index = 0; index < piece; index++) {
      double t = ( (double) (index) / (piece - 1));
      double x = 2 * t * (1 - t) * p1x + t * t;
      double y = 2 * t * (1 - t) * p1y;

//      double t2 = 1 - t;
//      double x2 = 2 * t * (1 - t) * p1x + (1 - t) * (1 - t);

      double x2 = (1 - t) * (t * (2 * p1x - 1) + 1);
//      System.out.println(x2 + " " + x21);
      double y2 = 2 * x2 * (1 - x2) * p1y;

      plot.addCacheScatterLinePlot("1", x, y);
      plot.addCacheScatterLinePlot("2", t, y2);
//      System.out.println(t + " " + x + " " + y);
    }
    plot.addLegend();
    plot.setVisible();
  }
}
