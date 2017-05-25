package shu.cms.plot;

import java.io.*;

import java.awt.*;
import javax.swing.*;

import static org.math.plot.utils.Array.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
//import shu.plot.PlotBase.Scale;
//import shu.plot.Plot2D.DotFill;
//import shu.plot.Plot2D.LineType;
//import shu.plot.Plot2D.Pattern;
import shu.plot.PlotBase;

//import shu.plot.Plot2D.DotFill;
//import shu.plot.Plot2D.LineType;
//import shu.plot.Plot2D.Pattern;

//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class Plot2D
    extends shu.plot.Plot2D {

  private static Plot2D staticPlot;
  public final static Plot2D getStaticInstance() {
    if (staticPlot == null) {
      staticPlot = getInstance();
    }
    return staticPlot;
  }

  public final static Plot2D getInstance() {
    return Plot2D.getInstance("Plot2D", 600, 600);
  }

  public final static Plot2D getInstance(String title, int width, int height) {
    shu.plot.Plot2D plot = shu.plot.Plot2D.getInstance(title, width, height);
    Plot2DWrapper wrapper = new Plot2DWrapper(plot);
    return wrapper;
  }

  public final static Plot2D getInstance(String title) {
    return getInstance(title, 600, 600);
  }

  public abstract void setPlotOnTop(boolean onTop);

  /**
   * 讓x/y軸的縮放比例相同,以x軸為主
   * @param xMin double
   * @param xMax double
   * @param yMin double
   */
  public abstract void setFixedBoundsByXAxis(double xMin, double xMax,
                                             double yMin);

  /**
   * 讓x/y軸的縮放比例相同,以Y軸為主
   * @param xMin double
   * @param yMin double
   * @param yMax double
   */
  public abstract void setFixedBoundsByYAxis(double xMin, double yMin,
                                             double yMax);

//  public void setFixedBoundsByXAxis() {
//    double[] xbounds = this.getFixedBounds(0);
//    double[] ybounds = this.getFixedBounds(1);
//    this.setFixedBoundsByXAxis(xbounds[0], xbounds[1], ybounds[0]);
//  }

//  public void setFixedBoundsByYAxis() {
//    double[] xbounds = this.getFixedBounds(0);
//    double[] ybounds = this.getFixedBounds(1);
//    this.setFixedBoundsByYAxis(xbounds[0], ybounds[0], ybounds[1]);
//  }

  protected Plot2D(String title, int width, int height) {
    super(title, width, height);
  }

  protected Plot2D(shu.plot.Plot2D plot) {
    super(plot);
  }

  public abstract int addStaircasePlot(String name, Color c, double[] ...XY);

  public abstract int addBarPlot(String name, Color c, double[] ...XY);

  public abstract int addBoxPlot(String name, Color c, double[][] XYdxdY);

  public final int addSpectra(String name, Color c, Spectra spectra) {
    double[][] lineData = produceLineData(spectra.getStart(), spectra.getEnd(),
                                          spectra.getData());

    if (name == null) {
      name = spectra.getName();
    }
    return addLinePlot(name, c, lineData);

  }

  public final int addSpectra(String name, Spectra spectra) {
    return addSpectra(name, this.getNewColor(), spectra);
  }

  public static void test(String[] args) {
    Plot2D h = Plot2D.getInstance();
    h.addCacheScatterPlot("s", 1, 2);
    h.addCacheScatterPlot("s", 2, 2);
    h.addCacheScatterPlot("s", 3, 2);

    h.addCacheScatterLinePlot("a", 1, 3);
    h.addCacheScatterLinePlot("a", 2, 3);
    h.addCacheScatterLinePlot("a", 3, 3);

//    h.addCacheBoxPlot("c", 1, 2, .5, .5);
//    h.addCacheBoxPlot("c", 1, 2, .6, .6);
    h.addCacheLinePlot("l", 0, 2, 2);
    h.addCacheLinePlot("l", 0, 2, 2.1);
    h.addCacheLinePlot("l", 0, 2, 2.2);
    h.setVisible();
//    h.setFixedBounds(0, 0, 1);
    System.out.println("");
  }

  public abstract void setLinePlotDrawDot(boolean drawDot);

  public abstract void setLinePlotWidth(int lineWidth);

  public abstract void setLineType(LineType lineType);

  public final void addPatchDeltaEReport(DeltaEReport.PatchDeltaEReport
                                         report,
                                         ReportType reportType) {
    //==========================================================================
    // 設定軸
    //==========================================================================
    setAxeLabel(1, "DeltaE");
    switch (reportType) {
      case Hue:
        setAxeLabel(0, "Hue");
        break;
      case Luminance:
        setAxeLabel(0, "Luminance");
        break;
      case Chroma:
        setAxeLabel(0, "Chroma");
        break;
    }
    //==========================================================================

    int size = report.size();
    for (int x = 0; x < size; x++) {
      DeltaE deltaE = report.getDeltaE(x);
      Patch patch = report.getPatch(x);
      CIELab Lab = patch.getLab();
      CIELCh LCh = new CIELCh(Lab);
      Color c = patch.getRGB().getColor();
      switch (reportType) {
        case Hue:
          addScatterPlot(null, c, LCh.h, deltaE.getMeasuredDeltaE());
          break;
        case Luminance:
          addScatterPlot(null, c, LCh.L, deltaE.getMeasuredDeltaE());
          break;
        case Chroma:
          addScatterPlot(null, c, LCh.C, deltaE.getMeasuredDeltaE());
          break;
      }
    }
  }

  public final void drawRGBColorSpaceInCIExy(Color c,
                                             RGB.ColorSpace rgbColorSpace) {
    RGB r = new RGB(rgbColorSpace, new double[] {1, 0, 0});
    RGB g = new RGB(rgbColorSpace, new double[] {0, 1, 0});
    RGB b = new RGB(rgbColorSpace, new double[] {0, 0, 1});
    CIEXYZ rXYZ = RGB.toXYZ(r);
    CIEXYZ gXYZ = RGB.toXYZ(g);
    CIEXYZ bXYZ = RGB.toXYZ(b);
    CIExyY rxyY = CIExyY.fromXYZ(rXYZ);
    CIExyY gxyY = CIExyY.fromXYZ(gXYZ);
    CIExyY bxyY = CIExyY.fromXYZ(bXYZ);

    addLinePlot(null, c, rxyY.x, gxyY.x, new double[] {rxyY.y, gxyY.y});
    addLinePlot(null, c, rxyY.x, bxyY.x, new double[] {rxyY.y, bxyY.y});
    addLinePlot(null, c, bxyY.x, gxyY.x, new double[] {bxyY.y, gxyY.y});
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * addPatchDeltaEReport所採用的報告型態
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public static enum ReportType {
    Hue, Luminance, Chroma
  }

  public abstract boolean setScatterPlotPattern(int index, Pattern pattern);

  public abstract boolean setDotRadius(int index, int radius);

  public abstract boolean setDotFill(int index, DotFill dotFill);

  public abstract int addGridPlot(String name, Color c, double[][] X,
                                  double[][] Y);

  public static void main(String[] args) {
    Spectra s = Illuminant.D65.getSpectra();
    Plot2D p = Plot2D.getInstance();
    p.addSpectra("", s);
    p.setVisible();
  }

  static class Plot2DWrapper
      extends Plot2D implements PlotWrapperInterface {
    private shu.plot.Plot2D plot;
    public PlotBase getOriginalPlot() {
      return plot;
    }

    protected Plot2DWrapper(shu.plot.Plot2D plot) {
      super(plot);
      this.plot = plot;
    }

    public void _dispose() {
      plot._dispose();
    }

    public int addBarPlot(String name, Color c, double[] ...XY) {
      return plot.addBarPlot(name, c, XY);
    }

    public int addBoxPlot(String name, Color c, double[][] XYdxdY) {
      return plot.addBoxPlot(name, c, XYdxdY);
    }

    public int addGridPlot(String name, Color c, double[][] X, double[][] Y) {
      return plot.addGridPlot(name, c, X, Y);
    }

    public void addImage(Image img, float alpha, double[] xySW, double[] xySE,
                         double[] xyNW) {
      plot.addImage(img, alpha, xySW, xySE, xyNW);
    }

    public void addLegend(String o) {
      plot.addLegend(o);
    }

    public void addLegend() {
      plot.addLegend();
    }

    public int addLinePlot(String name, Color c, double[] ...XY) {
      return plot.addLinePlot(name, c, XY);
    }

    public int addScatterPlot(String name, Color c, double[] ...XY) {
      return plot.addScatterPlot(name, c, XY);
    }

    public int addStaircasePlot(String name, Color c, double[] ...XY) {
      return plot.addStaircasePlot(name, c, XY);
    }

    public void addVectortoPlot(int numPlot, double[][] v) {
      plot.addVectortoPlot(numPlot, v);
    }

    public double[] getFixedBounds(int axe) {
      return plot.getFixedBounds(axe);
    }

    public JPanel getPlotPanel() {
      return plot.getPlotPanel();
    }

    public int getPlotSize() {
      return plot.getPlotSize();
    }

    public void removeAllPlots() {
      plot.removeAllPlots();
    }

    public void removePlot(int index) {
      plot.removePlot(index);
    }

    public void setAxeLabel(int axe, String label) {
      plot.setAxeLabel(axe, label);
    }

    public void setGridVisible(int axe, boolean v) {
      plot.setGridVisible(axe, v);
    }

    public void setAxisVisible(int axe, boolean v) {
      plot.setAxisVisible(axe, v);
    }

    public void setAxisScale(int axe, Scale scale) {
      plot.setAxisScale(axe, scale);
    }

    public void setBackground(Color bg) {
      plot.setBackground(bg);
    }

    public void setChartTitle(String title) {
      plot.setChartTitle(title);
    }

    public boolean setDotFill(int index, DotFill dotFill) {
      return plot.setDotFill(index, dotFill);
    }

    public boolean setDotRadius(int index, int radius) {
      return plot.setDotRadius(index, radius);
    }

    public void setFixedBounds(int axe, double min, double max) {
      plot.setFixedBounds(axe, min, max);
    }

    public void setFixedBoundsByXAxis(double xMin, double xMax, double yMin) {
      plot.setFixedBoundsByXAxis(xMin, xMax, yMin);
    }

    public void setFixedBoundsByYAxis(double xMin, double yMin, double yMax) {
      plot.setFixedBoundsByYAxis(xMin, yMin, yMax);
    }

    public void setLinePlotDrawDot(boolean drawDot) {
      plot.setLinePlotDrawDot(drawDot);
    }

    public void setLinePlotWidth(int lineWidth) {
      plot.setLinePlotWidth(lineWidth);
    }

    public void setLineType(LineType lineType) {
      plot.setLineType(lineType);
    }

    public void setPlotOnTop(boolean onTop) {
      plot.setPlotOnTop(onTop);
    }

    public void setPlotVisible(boolean b) {
      plot.setPlotVisible(b);
    }

    public void setPlotVisible(int num, boolean b) {
      plot.setPlotVisible(num, b);
    }

    public boolean setScatterPlotPattern(int index, Pattern pattern) {
      return plot.setScatterPlotPattern(index, pattern);
    }

    public void toGraphicFile(File file) {
      plot.toGraphicFile(file);
    }

  }
}
