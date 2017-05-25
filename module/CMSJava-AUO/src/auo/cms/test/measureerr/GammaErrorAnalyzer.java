package auo.cms.test.measureerr;

import shu.math.GammaFinder;
import shu.plot.*;
import java.awt.Color;

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
public class GammaErrorAnalyzer {

  public static void main(String[] args) {
    double maxY = 490.2655029296875;
    double gamma = 2.2;
//    double offsetStart = maxY - maxY * 100;
//    double offsetStep = maxY/1000;
    Plot2D plot = Plot2D.getInstance();
    for (double realY = maxY - 0.3; realY < maxY + 0.3; realY += 0.025) {
//      System.out.println(realY);
      Color c = realY < maxY ? Color.red : Color.green;
      String name = Double.toString(realY - maxY);
      System.out.println(name + " " + ( (realY - maxY) / maxY) * 100 + "%");
      for (int x = 244; x <= 254; x++) {
        double normal = x / 255.;
        double normalgamma = Math.pow(normal, gamma);
        double Y = maxY * normalgamma;
        double realGamma = GammaFinder.findGamma(normal, Y / realY);

        plot.addCacheScatterLinePlot(name, c, x, realGamma);

        if ( (realGamma > 2.22 || realGamma < 2.18) && x <= 250) {
          plot.addCacheScatterPlot("", Color.black, x, realGamma);
        }

      }

    }
    plot.setVisible();
    plot.setFixedBounds(0, 244, 255);
    plot.setFixedBounds(1, 2.12, 2.28);
  }
}
