package shu.cms.lcd.benchmark.diqam;

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
public abstract class DIQAM {
  public DIQAM(LCDTarget lcdTarget) {
    this(null, lcdTarget);
  }

  public DIQAM(String title, LCDTarget lcdTarget) {
    this.title = title;
    this.lcdTarget = lcdTarget;
  }

  public abstract Plot2D getPlot();

  public void setPlot(Plot2D plot) {
    this.plot2D = plot;
  }

  private final String getPlotTitle() {
    return (title != null ? " - " + title : "");
  }

  protected abstract String getPlotName();

  protected Plot2D plot2D;
  protected LCDTarget lcdTarget;
  protected String title;
  protected final Plot2D _getPlot() {
    if (plot2D == null) {
      plot2D = Plot2D.getInstance(getPlotName() + getPlotTitle());
    }
    return plot2D;
  }

  public static void main(String[] args) {
    String[] modes = new String[] {
         "Standard", "Movie", "Game", "PC", "USER","Photo"};

    Plot2D grayColorGamutPlot = Plot2D.getInstance("Gray Color Gamut Plot");
    grayColorGamutPlot.addLegend();

    for (String mode : modes) {
      LCDTarget ramp = LCDTarget.Instance.getFromAUORampXLS(
          "D:\\My Documents\\工作\\華山計畫\\Sharp LC-46LX1\\Modes\\" + mode + "\\ramp.xls");
      LCDTarget.Operator.gradationReverseFix(ramp);
      System.out.println(mode);
      benchmark(mode, ramp, 2.4, grayColorGamutPlot);
    }
//    grayColorGamutPlot.addLegend();
  }

  public final static void benchmark(String title, LCDTarget lcdTarget,
                                     double idealGamma,
                                     Plot2D grayColorGamutPlot) {
    GrayScaleFidelity fidelity = new GrayScaleFidelity(title, lcdTarget);
    fidelity.report(idealGamma);
    double grayScaleFidelityIndex = fidelity.getY();
    Plot2D plot1 = fidelity.getPlot();
    plot1.setChartTitle(title);
    System.out.println("GrayScaleFidelity Index: " + grayScaleFidelityIndex);

    GrayColorGamut grayColorGamut = new GrayColorGamut(title, lcdTarget);
    grayColorGamut.report();
    grayColorGamut.setPlot(grayColorGamutPlot);
    grayColorGamutPlot = grayColorGamut.getPlot();
//    plot2.setChartTitle("Gray Color Gamut");

    GrayChromaticityLinearity grayChromaticityLinearity = new
        GrayChromaticityLinearity(title, lcdTarget);
    grayChromaticityLinearity.report();
    double linearityIndex = grayChromaticityLinearity.getLinearityIndex();
    Plot2D plot3 = grayChromaticityLinearity.getPlot();
    System.out.println("GrayChromaticityLinearity Index: " + linearityIndex);
//    PlotUtils.arrange(new PlotWindow[] {plot1, plot2, plot3}, 3, true);
  }
}
