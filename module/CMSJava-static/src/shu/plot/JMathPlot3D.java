package shu.plot;

import java.io.*;

import java.awt.*;
import javax.swing.*;

import org.math.plot.*;
import org.math.plot.plotObjects.*;
import org.math.plot.plots.*;
import org.math.plot.render.*;
import quickhull3d.*;
import shu.plot.plots.*;
import shu.util.log.*;
import java.util.LinkedList;

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
public class JMathPlot3D
    extends Plot3D {
  protected Plot3DPanel plot;

  public int getPlotSize() {
    return plot.plotCanvas.plots.size();
  }

  public void setPlotVisible(boolean b) {
    for (Plot p : plot.getPlots()) {
      p.setVisible(b);
    }
  }

  public void setPlotVisible(int num, boolean b) {
    plot.getPlot(num).setVisible(b);
  }

  public void setBackground(Color bg) {
    plot.plotCanvas.setBackground(bg);
  }

  public void setFixedBounds(int axe, double min, double max) {
    plot.setFixedBounds(axe, min, max);

  }

  public void setGridVisible(int axe, boolean v) {
    plot.getAxis(axe).setGridVisible(v);
  }

  public void setAxeLabel(int axe, String label) {
    plot.setAxisLabel(axe, label);
  }

  public void setAxisVisible(int axe, boolean v) {
    plot.getAxis(axe).setVisible(v);
  }

  JMathPlot3D(String title, int width, int height) {
    super(title, width, height);
    plot = new Plot3DPanel();
    this.setContentPane(plot);
    Projection3D.PROJECT_FACTOR = DEFAULT_PROJECT_FACTOR;
  }

  public void addImage(Image img, float alpha, double[] xySW,
                       double[] xySE, double[] xyNW) {
    RasterImage p = new RasterImage(img, alpha, xySW, xySE, xyNW);
    plot.addPlotable(p);
  }

  public int addGridPlot(String name, Color c, double[] X, double[] Y,
                         double[][] Z) {
    return plot.addGridPlot(name, c, X, Y, Z);
  }

  public int addPlanePlot(String name, Color c, double[][][] data) {
    PlanePlot plane = new PlanePlot(name, c, data);
    return plot.addPlot(plane);
  }

  public int addLinePlot(String name, Color color, double[] ...XY) {
    return plot.addLinePlot(name, color, XY);
  }

  public int addScatterPlot(String name, Color c, double[] ...XY) {
    return plot.addScatterPlot(name, c, XY);
  }

  public int addPolygonPlot(String name, Color c, double[] p0,
                            double[] p1, double[] p2, double[] p3) {
    PolygonPlot polygonPlot = new PolygonPlot(name, c, p0, p1, p2, p3);
    return this.addPlot(polygonPlot);
  }

  public int addPolygonPlot(String name, Color c, double[] p0, double[] p1,
                            double[] p2) {
    PolygonPlot polygonPlot = new PolygonPlot(name, c, p0, p1, p2);
    return this.addPlot(polygonPlot);
  }

  public void addQuickHull3D(Color c, QuickHull3D hull, float alpha) {
    //抓點
    Point3d[] vertices = hull.getVertices();
    //抓面 (點構成的面)
    int[][] faceIndices = hull.getFaces();

    for (int[] index : faceIndices) {
      Point3d p0 = vertices[index[0]];
      Point3d p1 = vertices[index[1]];
      Point3d p2 = vertices[index[2]];

      PolygonPlot polygon = new PolygonPlot("", c,
                                            new double[] {p0.x, p0.y, p0.z},
                                            new double[] {p1.x, p1.y, p1.z},
                                            new double[] {p2.x, p2.y, p2.z});
      polygon.alpha = alpha;
      this.addPlot(polygon);
    }
  }

  public void removeAllPlots() {
    plot.removeAllPlots();
  }

  public void removePlot(int index) {
    plot.removePlot(index);
  }

  public void addLegend() {
    plot.addLegend("south");
  }

  public int addPlot(Plot newPlot) {
    return plot.addPlot(newPlot);
  }

  protected final static double DEFAULT_PROJECT_FACTOR = 1.25;

  public int addHistogramPlot(String name, Color c, double[][] XYdX) {
    return plot.addHistogramPlot(name, c, XYdX);
  }

  public void rotate(int vec0, int vec1) {
    plot.rotate(vec0, vec1);
  }

  public void zoom(double wPercent, double hPercent) {
    plot.zoom(wPercent, hPercent);
  }

  public JPanel getPlotPanel() {
    return plot;
  }

  public double[] getFixedBounds(int axe) {
    return plot.getFixedBounds(axe);
  }

  public void addLegend(String o) {
    plot.addLegend(o);
  }

  public void addVectortoPlot(int numPlot, double[][] v) {
    plot.addVectortoPlot(numPlot, v);
  }

  public static void main(String[] args) {
    Plot3D p = Plot3D.getInstance();
    double[][] grid = new double[][] {
        {
        0, 1}, {
        0, -1}
    };
    p.addGridPlot("", new double[] {0, 1}, new double[] {0, 1}, grid);
    p.setVisible();
    p.setView(0, 0);
  }

  public void _dispose() {
    plot = null;
  }

  public void setAxisScale(int axe, Scale scale) {
    plot.setAxisScale(axe, scale.label);
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

  public void setView(double theta, double phi) {
    plot.setView(theta, phi);
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

}
