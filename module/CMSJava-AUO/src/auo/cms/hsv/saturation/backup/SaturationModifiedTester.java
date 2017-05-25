package auo.cms.hsv.saturation.backup;

import shu.cms.plot.*;
import shu.math.array.*;

import auo.cms.hsv.saturation.*;
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
public class SaturationModifiedTester {

  public static void main(String[] args) {
    double[] saturationAdjust = new double[101];
    double[] originalSaturaion = new double[101];
    double offset = 63;
    SaturationFormula formula1 = SaturationImageAdjustor.quadratic; //25
    SaturationFormula formula2 = SaturationImageAdjustor.cubic; //12
    SaturationFormula formula3 = SaturationImageAdjustor.gain;
//    SaturationFormula formula4 = SaturationImageAdjustor.modified12;
//    SaturationFormula formula5 = SaturationImageAdjustor.modified3; //12
    SaturationFormula richard = new RichardFormula(20);
//    SaturationFormula richard = new RichardFormula(67);

    Plot2D plot = Plot2D.getInstance("Saturation");
    Plot2D plot2 = Plot2D.getInstance("DeltaSaturation");

    for (int x = 0; x <= 100; x++) {
      double dx = (x / 100.);
//      double s = x + (offset) * dx * (100 - x) / 100. * 4;
      double s1 = SaturationImageAdjustor.twoQuadratic.getSaturartion(x, offset);
//      double s1 = formula1.getSaturartion(x, offset);
      double s2 = formula2.getSaturartion(x, offset);
      double s3 = formula3.getSaturartion(x, 1);
//      double s4 = formula4.getSaturartion(x, offset);
//      double s5 = formula5.getSaturartion(x, offset);

      double dsaturation = x / 100.;
      double s6 = x +
          offset * dsaturation * (1 - dsaturation * dsaturation) * 2.7;
      double s7 = x +
          offset * dsaturation * dsaturation * (1 - dsaturation * dsaturation) *
          5.4;
//      double ss8 = offset * dsaturation * (1 - dsaturation) * (dsaturation - .3) *
//          20;
      double ss8 = offset * (1 - dsaturation) * (dsaturation - .3) *
          10;

      ss8 = ss8 < 0 ? 0 : ss8;
      double s8 = x + ss8;

      double dsaturation1 = x / 50.;
      double s9 = x <= 50 ? x + (dsaturation1 * dsaturation1) * offset :
          (x + dsaturation * (1 - dsaturation) * offset * 4);

      double gain = offset / 50;
      double s10 = (x < 67) ? x + x * gain : x + 2 * gain * (100.5 - x);
      double s11 = richard.getSaturartion(x, offset);

      if (s1 > 100 || s2 > 100) {
        System.out.print("*");
      }
      s1 = s1 > 100 ? 100 : s1;
      s2 = s2 > 100 ? 100 : s2;
      s3 = s3 > 100 ? 100 : s3;
//      s4 = s4 > 100 ? 100 : s4;
//      s5 = s5 > 100 ? 100 : s5;
      s6 = s6 > 100 ? 100 : s6;
//      saturationAdjust[x] = s;
//      originalSaturaion[x] = x;
      s7 = s7 > 100 ? 100 : s7;
      s8 = s8 < 0 ? 0 : s8;
      s8 = s8 > 100 ? 100 : s8;
      s9 = s9 > 100 ? 100 : s9;
      s10 = s10 > 100 ? 100 : s10;
      s11 = s11 > 100 ? 100 : s11;

//      plot.addCacheScatterLinePlot("2 times", x, s1);
//      plot.addCacheScatterLinePlot("3 times", x, s2);
//      plot.addCacheScatterLinePlot("Gain", x, s3);
//      plot.addCacheScatterLinePlot("Modified1+2", x, s4);
//      plot.addCacheScatterLinePlot("Modified3", x, s5);
//      plot.addCacheScatterLinePlot("s6", x, s6);
//      plot.addCacheScatterLinePlot("s7", x, s7);
//      plot.addCacheScatterLinePlot("s8", x, s8);
//      plot.addCacheScatterLinePlot("2square", x, s9);
//      plot.addCacheScatterLinePlot("s10", x, s10);
      plot.addCacheScatterLinePlot("s11", x, s11);

//      plot2.addCacheScatterLinePlot("2 times", x, s1 - x);
//      plot2.addCacheScatterLinePlot("3 times", x, s2 - x);
//      plot2.addCacheScatterLinePlot("Gain", x, s3 - x);
//      plot2.addCacheScatterLinePlot("Modified1+2", x, s4 - x);
//      plot2.addCacheScatterLinePlot("Modified3", x, s5 - x);
//      plot2.addCacheScatterLinePlot("s6", x, s6 - x);
//      plot2.addCacheScatterLinePlot("s7", x, s7 - x);
//      plot2.addCacheScatterLinePlot("s8", x, s8 - x);
//      plot2.addCacheScatterLinePlot("2square", x, s9 - x);
//      plot2.addCacheScatterLinePlot("s10", x, s10 - x);
      plot2.addCacheScatterLinePlot("s11", x, s11 - x);
    }

//    plot.addLinePlot("", 0, 100, saturationAdjust);
    plot.setAxeLabel(0, "Input Saturation");
    plot.setAxeLabel(1, "Output Saturation");
    plot.addLegend();
    plot.setVisible();

//    double[] deltaSaturation = DoubleArray.minus(saturationAdjust,
//                                                 originalSaturaion);

//    plot2.addLinePlot("", 0, 100, deltaSaturation);
    plot2.setAxeLabel(0, "Input Saturation");
    plot2.setAxeLabel(1, "Delta Saturation");
    plot2.addLegend();
    plot2.setVisible();

  }
}
