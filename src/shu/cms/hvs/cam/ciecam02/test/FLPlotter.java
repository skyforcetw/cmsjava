package shu.cms.hvs.cam.ciecam02.test;

import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.cms.plot.*;
import shu.math.*;
import shu.cms.hvs.cam.hunt.Hunt;

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
public class FLPlotter {

  public static void main(String[] args) {
    Plot2D plot = Plot2D.getInstance();
    for (double LA = 1E-1; LA <= 1E4; LA += 1) {
      double FL = ViewingConditions.computeFL(LA);
//      double FL2 = 0.1715 * Maths.cubeRoot(LA);

//      double k = 1.0 / ( (5.0 * LA) + 1.0);
////(2)
//      double fl = 0.2 * Math.pow(k, 4.0) * (5.0 * LA) + 0.1 *
//          (Math.pow( (1.0 - Math.pow(k, 4.0)), 2.0)) *
//          (Maths.cubeRoot(5.0 * LA));

      double fl = Hunt.FL(LA);

      plot.addCacheScatterLinePlot("FL", LA, FL);
//      plot.addCacheScatterLinePlot("FL2", LA, FL2);
      plot.addCacheScatterLinePlot("FL2", LA, fl);
    }

    plot.setVisible();
    plot.setFixedBounds(0, 0.1, 10000);
    plot.setAxisScale(0, Plot2D.Scale.Log);
    plot.setAxisScale(1, Plot2D.Scale.Log);
  }
}
