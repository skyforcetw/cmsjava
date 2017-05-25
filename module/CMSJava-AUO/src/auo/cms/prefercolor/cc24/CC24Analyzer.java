package auo.cms.prefercolor.cc24;

import shu.cms.lcd.LCDTarget;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.LCDTargetBase;
import shu.cms.devicemodel.lcd.MultiMatrixModel;
import shu.cms.profile.ProfileColorSpace;
import shu.cms.lcd.DC2LCDTargetAdapter;
import shu.cms.dc.DCTarget;
import shu.cms.*;
import shu.cms.plot.*;
import org.math.plot.Plot2DPanel;
import org.math.plot.plots.*;
import auo.cms.hsv.autotune.ProfileColorSpaceUtils;

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
public class CC24Analyzer {

  public static void main(String[] args) {
    //==========================================================================
    // 實體Target
    //==========================================================================
    LCDTarget rampTarget = LCDTarget.Instance.getFromAUORampXLS(
        "prefered/B125XW02/WRGB.xls", LCDTarget.Number.Ramp1024);
    LCDTarget.Operator.gradationReverseFix(rampTarget);
//    ProfileColorSpace pcs = ProfileColorSpaceUtils.
//        getProfileColorSpaceFromRampTarget(rampTarget);

    LCDTarget test729Target = LCDTarget.Instance.getTest729FromAUOXLS(
//        "prefered/EIZO S2031W/729color_movie.xls");
//        "prefered/LG 42SL90QD/Standard/871.xls");
//        "prefered/Sharp LC-46LX1/Modes/Standard/871.xls");
//        "prefered/Sony 70x7000/729color_standard.xls");
//        "prefered/SONY KDL-40ZX1/729colors_standard.xls");
        "prefered/VIZIO VF551XVT-T/標準/色彩提升-鮮豔.xls");

    ProfileColorSpace pcs = ProfileColorSpaceUtils.
        getProfileColorSpaceFrom729Target(test729Target);

    //==========================================================================

    //==========================================================================
    // 虛擬Target
    //==========================================================================
    LCDTarget lcdTarget = DC2LCDTargetAdapter.Instance.getCameraTarget(DCTarget.
        Chart.CC24, RGB.ColorSpace.sRGB_gamma22, pcs);

    LCDTarget cc24target = DC2LCDTargetAdapter.Instance.getCameraTarget(
        DCTarget.Chart.CC24, RGB.ColorSpace.sRGB_gamma22);
    //==========================================================================
    int size = lcdTarget.getPatchList().size();

    Plot2D plot = Plot2D.getInstance(test729Target.getDescription());
    Plot2DPanel panel = (Plot2DPanel) plot.getPlotPanel();

    double totalSaturation = 0;

    for (int x = 0; x < size; x++) {
      if ( (x + 1) % 4 == 0 || x == 24) {
        continue;
      }
      Patch lcdPatch = lcdTarget.getPatch(x);
      Patch cc24Patch = cc24target.getPatch(x);
      RGB rgb = cc24Patch.getRGB();
      rgb.rationalize();
      CIELab measureLab = lcdPatch.getLab();
      CIELab idealLab = cc24Patch.getLab();
      CIELCh meaesureLCh = new CIELCh(measureLab);
      CIELCh idealLCh = new CIELCh(idealLab);

      int n = plot.addScatterPlot("", rgb.getColor(), idealLab.a, idealLab.b);
      ScatterPlot scatter = (ScatterPlot) panel.getPlot(n);
      scatter.setRadius(3);
      double[] vector = new double[] {
          measureLab.a - idealLab.a, measureLab.b - idealLab.b};
      plot.addVectortoPlot(n, new double[][] {vector});

      double measureSaturation = meaesureLCh.getSaturation();
      double idealSaturation = idealLCh.getSaturation();
      double saturation = measureSaturation / idealSaturation;
      totalSaturation += saturation;
      System.out.println( (x + 1) + " " + (meaesureLCh.h - idealLCh.h));
//      System.out.println(measureSaturation / idealSaturation);
    }
    plot.setVisible();
    plot.setAxisLabels("a*", "b*");
    plot.setFixedBounds(0, -60, 60);
    plot.setFixedBounds(1, -80, 80);

    System.out.println("mean saturation: " + totalSaturation / 18);
  }
}
