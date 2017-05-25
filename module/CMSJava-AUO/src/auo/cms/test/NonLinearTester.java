package auo.cms.test;

import shu.cms.plot.Plot2D;
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
public class NonLinearTester {
  public static void main(String[] args) {
    Plot2D plot = Plot2D.getInstance();
    int a = 1;
    int b = 1;
    for (int x = 0; x < 256; x++) {
      int y = x + a * (x >> 1);
      int y2 = x + (x >> 2);
      int y3 = x + (x >> 3);
      int y4 = x + (x >> 4);
//      System.out.println( (x >> 1) + " " + (x >> 2) + " " + y);
      plot.addCacheScatterLinePlot("org", x, x);
      plot.addCacheScatterLinePlot("adj", x, y);
      plot.addCacheScatterLinePlot("adj2", x, y2);
      plot.addCacheScatterLinePlot("adj3", x, y3);
      plot.addCacheScatterLinePlot("adj4", x, y4);
    }

    plot.setVisible();
  }
}
