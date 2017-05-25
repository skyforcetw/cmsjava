package shu.plot;

import java.io.*;
import static java.lang.Math.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

import static org.math.array.StatisticSample.*;
import org.math.plot.*;
import org.math.plot.plotObjects.*;
import org.math.plot.plots.*;
import org.math.plot.render.*;
import org.math.plot.utils.*;
import shu.plot.plots.*;
import shu.util.log.*;

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
public class JMathPlot2D
    extends Plot2D {

  protected Plot2DPanel plot;

  public void removePlotToolBar() {
    plot.removePlotToolBar();
  }

  public void setPlotOnTop(boolean onTop) {
    plot.plotCanvas.plotOnTop = onTop;
  }

  public JPanel getPlotPanel() {
    return plot;
  }

  public void setPlotVisible(boolean b) {
    for (Plot p : plot.getPlots()) {
      p.setVisible(b);
    }
  }

  public void setPlotVisible(int num, boolean b) {
    plot.getPlot(num).setVisible(b);
  }

  public void addLegend(String o) {
    plot.addLegend(o);
  }

  public void addLegend() {
    plot.addLegend("south");
  }

  public void setFixedBounds(int axe, double min, double max) {
    plot.setFixedBounds(axe, min, max);
  }

  public double[] getFixedBounds(int axe) {
    return plot.getFixedBounds(axe);
  }

  /**
   * 讓x/y軸的縮放比例相同,以x軸為主
   * @param xMin double
   * @param xMax double
   * @param yMin double
   */
  public void setFixedBoundsByXAxis(double xMin, double xMax, double yMin) {
    plot.setFixedBounds(0, xMin, xMax);
    plot.setFixedBounds(1, yMin, yMin + (xMax - xMin));
  }

  public void setFixedBoundsByXAxis(double yMin) {
    double[] xBounds = plot.getFixedBounds(0);
    plot.setFixedBounds(1, yMin, yMin + (xBounds[1] - xBounds[0]));
  }

  public void setFixedBoundsByYAxis(double xMin) {
    double[] yBounds = plot.getFixedBounds(1);
//  plot.setFixedBounds(1, yMin, yMin + (xBounds[1] - xBounds[0]));
    plot.setFixedBounds(0, xMin, xMin + (yBounds[1] - yBounds[0]));
  }

  /**
   * 讓x/y軸的縮放比例相同,以Y軸為主
   * @param xMin double
   * @param yMin double
   * @param yMax double
   */
  public void setFixedBoundsByYAxis(double xMin, double yMin, double yMax) {
    plot.setFixedBounds(0, xMin, xMin + (yMax - yMin));
    plot.setFixedBounds(1, yMin, yMax);
  }

  public void setAxeLabel(int axe, String label) {
    plot.setAxisLabel(axe, label);
  }

  public void setAxisVisible(int axe, boolean v) {
    plot.getAxis(axe).setVisible(v);
  }

  public void setGridVisible(int axe, boolean v) {

  }

  JMathPlot2D(String title, int width, int height) {
    super(title, width, height);
    plot = new Plot2DPanel();
    this.setContentPane(plot);
  }

  JMathPlot2D(String title) {
    this(title, 600, 600);
  }

  JMathPlot2D() {
    this("Plot2D", 600, 600);
  }

  public int addStaircasePlot(String name, Color c, double[] ...XY) {
    return plot.addStaircasePlot(name, c, XY);
  }

  public int addBarPlot(String name, Color c, double[] ...XY) {
    return plot.addBarPlot(name, c, XY);
  }

  public int addBoxPlot(String name, Color c, double[][] XYdxdY) {
    return plot.addBoxPlot(name, c, XYdxdY);
  }

  public int addCirclePlot(String name, Color c, double[][] XYdxdY) {
    CirclePlot2D circle = new CirclePlot2D(getColumnsRangeCopy(XYdxdY, 0, 1),
                                           getColumnsRangeCopy(XYdxdY, 2, 3), c,
                                           name);
    return plot.addPlot(circle);
  }

  public int addLinePlot(String name, Color c, double[] ...XY) {
    if (XY.length == 2 && XY[0].length == 2) {
      XY = Array.mergeColumns(XY);
    }
    int index = plot.addLinePlot(name, c, XY);
    return index;
  }

  public int getPlotSize() {
    return plot.plotCanvas.plots.size();
  }

  public int addScatterPlot(String name, Color c, double[] ...XY) {
    return plot.addScatterPlot(name, c, XY);
  }

  public void removeAllPlots() {
    plot.removeAllPlots();
  }

  public void removePlot(int index) {
    plot.removePlot(index);
  }

  public void setLinePlotDrawDot(boolean drawDot) {
    LinkedList<org.math.plot.plots.Plot> plots = plot.getPlots();
    int size = plots.size();
    for (int x = 0; x < size; x++) {
      org.math.plot.plots.Plot p = plots.get(x);
      if (p instanceof LinePlot) {
        ( (LinePlot) p).draw_dot = drawDot;
      }
    }
  }

  public void setBackground(Color bg) {
    plot.plotCanvas.setBackground(bg);
  }

  public void addVectortoPlot(int numPlot, double[][] v) {
    plot.addVectortoPlot(numPlot, v);
  }

  public void addImage(Image img, float alpha, double[] xySW,
                       double[] xySE, double[] xyNW) {
    RasterImage p = new RasterImage(img, alpha, xySW, xySE, xyNW);
    plot.addPlotable(p);
  }

  public static void main(String[] args) {
  }

  public boolean setScatterPlotPattern(int index, Pattern pattern) {
    Plot p = this.plot.getPlot(index);
    if (p instanceof ScatterPlot) {
      switch (pattern) {
        case Round:
          ( (ScatterPlot) p).setDotPattern(AbstractDrawer.ROUND_DOT);
          break;
        case X:
          ( (ScatterPlot) p).setDotPattern(AbstractDrawer.CROSS_DOT);
          break;
        case Square:
          ( (ScatterPlot) p).setDotPattern(AbstractDrawer.SQUARE_DOT);
          break;
        case Cross:
          ( (ScatterPlot) p).setDotPattern(AbstractDrawer.CROSS2_DOT);
          break;
        default:
//          ( (ScatterPlot) p).setDotPattern(pattern.pattern);
      }
      return true;
    }
    else {
      return false;
    }
  }

  public boolean setDotRadius(int index, int radius) {
    Plot p = this.plot.getPlot(index);
    if (p instanceof ScatterPlot) {
      ( (ScatterPlot) p).setRadius(radius);
      return true;
    }
    else {
      return false;
    }
  }

  public boolean setDotFill(int index, DotFill dotFill) {
    Plot p = this.plot.getPlot(index);
    if (p instanceof ScatterPlot) {
      switch (dotFill) {
        default:
        case Whole:
          ( (ScatterPlot) p).setDotFill(AbstractDrawer.DOT_FILL_WHOLE);
          break;
        case Inside:
          ( (ScatterPlot) p).setDotFill(AbstractDrawer.DOT_FILL_INSIDE);
          break;
        case None:
          ( (ScatterPlot) p).setDotFill(AbstractDrawer.DOT_FILL_NONE);
          break;
        case Gradation:
          ( (ScatterPlot) p).setDotFill(AbstractDrawer.DOT_FILL_GRADATION);
          break;

      }
      return true;
    }
    else {
      return false;
    }

  }

  public void setAxisScale(int axe, Scale scale) {
    plot.setAxisScale(axe, scale.label);
  }

  public void _dispose() {
    plot = null;
  }

  public void toGraphicFile(File file) {
    try {
      plot.toGraphicFile(file);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

  public void setChartTitle(String title) {
    BaseLabel label = new BaseLabel(title, Color.BLACK, 0.5, 1.1);
    plot.addPlotable(label);
  }

  public void setLinePlotWidth(int lineWidth) {
    LinkedList<org.math.plot.plots.Plot> plots = plot.getPlots();
    for (org.math.plot.plots.Plot p : plots) {
      if (p instanceof LinePlot) {
        ( (LinePlot) p).line_width = lineWidth;
      }
    }
  }

  public void setLineType(LineType lineType) {
    LinkedList<org.math.plot.plots.Plot> plots = plot.getPlots();
    for (org.math.plot.plots.Plot p : plots) {
      if (p instanceof LinePlot) {
        ( (LinePlot) p).line_type = lineType.index;
      }
    }
  }

  public int addGridPlot(String name, Color c, double[][] X,
                         double[][] Y) {
    GridPlot2D grid = new GridPlot2D(name, c, X, Y);
    return plot.addPlot(grid);
  }

  public int addHistogramPlot(String name, Color color,
                              double[] sample, int n) {
    return plot.addHistogramPlot(name, color, sample, n);
  }
//  public   int addHistogramPlot(String name, Color c, double[][] XY,
//                                       double dX){
//     return plot.addHistogramPlot(name, c, XY, dX);
//  }
}
