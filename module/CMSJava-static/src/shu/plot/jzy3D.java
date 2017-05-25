package shu.plot;

import java.io.*;
import java.util.*;
import java.util.List;

import java.awt.*;
import javax.swing.*;

import org.jzy3d.chart.*;
import org.jzy3d.global.*;
import org.jzy3d.maths.*;
import org.jzy3d.plot3d.primitives.*;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axes.layout.*;
import org.jzy3d.plot3d.rendering.canvas.*;
import org.jzy3d.plot3d.rendering.scene.*;
import org.math.plot.plots.*;
import shu.plot.PlotInterface.*;
import shu.plot.jzy3d.*;

//import org.jzy3d.maths.Scale;

//import org.jzy3d.colors.Color;

/**
 * <p>Title: Colour Management System - static</p>
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
public class jzy3D
    extends Plot3D {
  public final static String CHART_TYPE = "swing";
  public final static Quality CHART_QUALITY = Quality.Nicest;

  static jzy3D getjzy3DInstance(String title, int width, int height) {
    Chart chart = new Chart(CHART_QUALITY, CHART_TYPE);
//    chart.getScene().getGraph().add(surface);

    Settings.getInstance().setHardwareAccelerated(true);
    Rectangle rectangle = new Rectangle(width, height);
    JFrame frame = ChartLauncher.openChart(chart, rectangle, title);
    jzy3D jzy = new jzy3D(frame, chart);
    return jzy;
  }

  public static void main(String[] args) {
    Plot3D plot = Plot3D.getInstance("", Plot3D.Instance.jzy3D);
//    Plot3D plot = Plot3D.getInstance("", Plot3D.Instance.JMathPlot3D);
    Chart chart = ( (jzy3D) plot).chart;
//    chart.getView().setSquared(false);
//    chart.getView().setMaximized(true);
//    chart.getView().getCamera().setStretchToFill(false);
//    chart.getView().setCameraMode(CameraMode.PERSPECTIVE);

    int n = 10;
    int m = 8;
    double[] X = new double[n];
    double[] Y = new double[m];
    double[][] Z = new double[m][n];

    for (int i = 0; i < X.length; i++) {
      X[i] = i; // / (double) X.length;
      for (int j = 0; j < Y.length; j++) {
        Y[j] = j; // / (double) Y.length;
        Z[j][i] = Math.exp(X[i]) + Y[j];
      }
    }

    Color c = new Color(.5f, .5f, .5f, .2f);
    plot.addGridPlot("", c, Z);
//    plot.setAxisVisible(0, false);
    plot.setVisible();
    plot.addScatterPlot("", new double[] {1, 2, 900}, new double[] {2, 2, 900});
  }

  private Chart chart;
  private Graph graph;
  private final static org.jzy3d.colors.Color GRID_COLOR = org.jzy3d.colors.
      Color.GRAY;
  private jzy3D(JFrame frame, Chart chart) {
    super(frame);
    this.chart = chart;
    graph = chart.getScene().getGraph();
    IAxeLayout axeLayout = chart.getAxeLayout();
    axeLayout.setGridColor(GRID_COLOR);
    axeLayout.setXTickColor(GRID_COLOR);
    axeLayout.setYTickColor(GRID_COLOR);
    axeLayout.setZTickColor(GRID_COLOR);
//    axeLayout.setXAxeLabelDisplayed(false);
//    axeLayout.setXTickLabelDisplayed(false);
//    axeLayout.setFaceDisplayed(false);
//    plot = new Plot3DPanel();
//    this.setContentPane(plot);
//    Projection3D.PROJECT_FACTOR = DEFAULT_PROJECT_FACTOR;
  }

  /**
   * _dispose
   *
   * @todo Implement this shu.plot.PlotWindow method
   */
  public void _dispose() {
  }

  public final static org.jzy3d.colors.Color jzy3DColor(Color c) {
    return new org.jzy3d.colors.Color( (float) (c.getRed() / 255.),
                                      (float) (c.getGreen() / 255.),
                                      (float) (c.getBlue() / 255.),
                                      (float) (c.getAlpha() / 255.));
  }

  public final static org.jzy3d.colors.Color jzy3DColor(Color c, float alpha) {
    return new org.jzy3d.colors.Color( (float) (c.getRed() / 255.),
                                      (float) (c.getGreen() / 255.),
                                      (float) (c.getBlue() / 255.),
                                      alpha);
  }

  /**
   * addGridPlot
   *
   * @param name String
   * @param c Color
   * @param X double[]
   * @param Y double[]
   * @param Z double[][]
   * @return int
   * @todo Implement this shu.plot.Plot3D method
   */
  public int addGridPlot(String name, Color c, double[] X, double[] Y,
                         double[][] Z) {
    int height = Y.length;
    int width = X.length;
    List<Polygon> polygons = new ArrayList<Polygon> ();
    org.jzy3d.colors.Color jzyc = jzy3DColor(c);

    for (int y = 0; y < (height - 1); y++) {
      for (int x = 0; x < (width - 1); x++) {
        float x1 = (float) X[x];
        float y1 = (float) Y[y];

        float x2 = (float) X[x + 1];
        float y2 = (float) Y[y + 1];

        float z1 = (float) Z[y][x];
        float z2 = (float) Z[y][x + 1];
        float z3 = (float) Z[y + 1][x];
        float z4 = (float) Z[y + 1][x + 1];

        float[] p1 = new float[] {
            x1, y1, z1};
        float[] p2 = new float[] {
            x2, y1, z2};
        float[] p3 = new float[] {
            x1, y2, z3};
        float[] p4 = new float[] {
            x2, y2, z4};

        Polygon polygon = new Polygon();
        polygon.add(new Point(new Coord3d(p1)));
        polygon.add(new Point(new Coord3d(p2)));
        polygon.add(new Point(new Coord3d(p4)));
        polygon.add(new Point(new Coord3d(p3)));
        polygons.add(polygon);
      }
    }

    Shape surface = new Shape(polygons);
//    surface.setColorMapper(new ColorMapper(nenw ColorMapGrayscale(),
//                                           surface.getBounds().getZmin(),
//                                           surface.getBounds().getZmax(), jzyc));
//    surface.setColorMapper(new ColorMapper(new ColorMapGrayscale(),
//                                           surface.getBounds().getZmin(),
//                                           surface.getBounds().getZmax(),
//                                           new
//                                           org.jzy3d.colors.Color(1, 1, 1, .5f)));

    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(org.jzy3d.colors.Color.BLACK);
    surface.setColor(jzyc);

//    Graph graph = chart.getScene().getGraph();
    graph.add(surface);
    return getIndex(surface);
  }

  /**
   * addHistogramPlot
   *
   * @param name String
   * @param c Color
   * @param XYdX double[][]
   * @return int
   * @todo Implement this shu.plot.Plot3D method
   */
  public int addHistogramPlot(String name, Color c, double[][] XYdX) {
    return 0;
  }

  /**
   * addImage
   *
   * @param img Image
   * @param alpha float
   * @param xySW double[]
   * @param xySE double[]
   * @param xyNW double[]
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void addImage(Image img, float alpha, double[] xySW, double[] xySE,
                       double[] xyNW) {
  }

  /**
   * addLegend
   *
   * @param o String
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void addLegend(String o) {
  }

  /**
   * addLegend
   *
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void addLegend() {
  }

  /**
   * addLinePlot
   *
   * @param name String
   * @param c Color
   * @param XY double[][]
   * @return int
   * @todo Implement this shu.plot.PlotInterface method
   */
  public int addLinePlot(String name, Color c, double[] ...XY) {
    return 0;
  }

  /**
   * addPlanePlot
   *
   * @param name String
   * @param c Color
   * @param data double[][][]
   * @return int
   * @todo Implement this shu.plot.Plot3D method
   */
  public int addPlanePlot(String name, Color c, double[][][] data) {
    return 0;
  }

  private boolean polygonDrawLine = true;
  /**
   * addPlot
   *
   * @param newPlot Plot
   * @return int
   */
  public int addPlot(Plot newPlot) {
    if (newPlot instanceof PolygonPlot) {
      PolygonPlot p = (PolygonPlot) newPlot;
      polygonDrawLine = p.draw_lines;
      Color c = new Color( (float) (p.color.getRed() / 255.),
                          (float) (p.color.getGreen() / 255.),
                          (float) (p.color.getBlue() / 255.), p.alpha);

      switch (p.polygon.length) {
        case 3:
          return this.addPolygonPlot(p.name, c, p.polygon[0], p.polygon[1],
                                     p.polygon[2]);
        case 4:
          return this.addPolygonPlot(p.name, c, p.polygon[0], p.polygon[1],
                                     p.polygon[2], p.polygon[3]);
        default:
          return -1;
      }

    }
    else {
      throw new UnsupportedOperationException();
    }

  }

  /**
   * addPolygonPlot
   *
   * @param name String
   * @param c Color
   * @param p0 double[]
   * @param p1 double[]
   * @param p2 double[]
   * @return int
   */
  public int addPolygonPlot(String name, Color c, double[] p0, double[] p1,
                            double[] p2) {
    Polygon polygon = new Polygon();
    polygon.add(new Point(new Coord3d(p0[0], p0[1], p0[2])));
    polygon.add(new Point(new Coord3d(p1[0], p1[1], p1[2])));
    polygon.add(new Point(new Coord3d(p2[0], p2[1], p2[2])));
    polygon.setColor(jzy3DColor(c));
    polygon.setWireframeDisplayed(polygonDrawLine);
    graph.add(polygon);

    return getIndex(polygon);
  }

  public int addPolygonPlot(String name, Color c, double[] p0,
                            double[] p1, double[] p2, double[] p3) {
    Polygon polygon = new Polygon();
    polygon.add(new Point(new Coord3d(p0[0], p0[1], p0[2])));
    polygon.add(new Point(new Coord3d(p1[0], p1[1], p1[2])));
    polygon.add(new Point(new Coord3d(p2[0], p2[1], p2[2])));
    polygon.add(new Point(new Coord3d(p3[0], p3[1], p3[2])));
    polygon.setColor(jzy3DColor(c));
    polygon.setWireframeDisplayed(polygonDrawLine);
    graph.add(polygon);

    return getIndex(polygon);
  }

  public void addDrawable(AbstractDrawable drawable) {
    graph.add(drawable);
  }

  /**
   * addScatterPlot
   *
   * @param name String
   * @param c Color
   * @param XY double[][]
   * @return int
   * @todo Implement this shu.plot.PlotInterface method
   */
  public int addScatterPlot(String name, Color c, double[] ...XY) {
    int size = XY.length;
    Coord3d[] points = new Coord3d[size];

    for (int i = 0; i < size; i++) {
//      x = (float) Math.random() - 0.5f;
//      y = (float) Math.random() - 0.5f;
//      z = (float) Math.random() - 0.5f;
      double[] xyz = XY[i];
      points[i] = new Coord3d(xyz[0], xyz[1], xyz[2]);
    }
    Scatter scatter = new Scatter(points, jzy3DColor(c), 3f);
//    scatter.setLegendDisplayed(true);
    chart.getScene().add(scatter);

    return getIndex(scatter);
  }

  private int getIndex(AbstractDrawable drawable) {
    int num = graph.getAll().indexOf(drawable);
    return num;
  }

  /**
   * addVectortoPlot
   *
   * @param numPlot int
   * @param v double[][]
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void addVectortoPlot(int numPlot, double[][] v) {
  }

  /**
   * getFixedBounds
   *
   * @param axe int
   * @return double[]
   * @todo Implement this shu.plot.PlotInterface method
   */
  public double[] getFixedBounds(int axe) {
    return null;
  }

  /**
   * getPlotPanel
   *
   * @return JPanel
   * @todo Implement this shu.plot.PlotInterface method
   */
  public JPanel getPlotPanel() {
    return null;
  }

  /**
   * getPlotSize
   *
   * @return int
   */
  public int getPlotSize() {
    return graph.getAll().size();
  }

  /**
   * removeAllPlots
   *
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void removeAllPlots() {

  }

  /**
   * removePlot
   *
   * @param index int
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void removePlot(int index) {
    List<AbstractDrawable> list = graph.getAll();
//   AbstractDrawable  draw= list.get(index);
    list.remove(index);
  }

  /**
   * rotate
   *
   * @param vec0 int
   * @param vec1 int
   * @todo Implement this shu.plot.Plot3D method
   */
  public void rotate(int vec0, int vec1) {
  }

  /**
   * setAxeLabel
   *
   * @param axe int
   * @param label String
   */
  public void setAxeLabel(int axe, String label) {

    IAxeLayout axeLayout = chart.getAxeLayout();
    switch (axe) {
      case 0:
        axeLayout.setXAxeLabel(label);
        break;
      case 1:
        axeLayout.setYAxeLabel(label);
        break;
      case 2:
        axeLayout.setZAxeLabel(label);
        break;
    }
  }

  /**
   * setAxisScale
   *
   * @param axe int
   * @param scale Scale
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void setAxisScale(int axe, Scale scale) {
  }

  public void setAxisVisible(int axe, boolean v) {
    IAxeLayout axeLayout = chart.getAxeLayout();
    switch (axe) {
      case 0:
        axeLayout.setXAxeLabelDisplayed(v);
        axeLayout.setXTickLabelDisplayed(v);
        break;
      case 1:
        axeLayout.setYAxeLabelDisplayed(v);
        axeLayout.setYTickLabelDisplayed(v);
        break;
      case 2:
        axeLayout.setZAxeLabelDisplayed(v);
        axeLayout.setZTickLabelDisplayed(v);
        break;
    }
  }

  /**
   * setBackground
   *
   * @param bg Color
   */
  public void setBackground(Color bg) {
    chart.getView().setBackgroundColor(jzy3DColor(bg));
  }

  /**
   * setChartTitle
   *
   * @param title String
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void setChartTitle(String title) {
  }

  /**
   * setDotFill
   *
   * @param index int
   * @param dotFill DotFill
   * @return boolean
   * @todo Implement this shu.plot.PlotInterface method
   */
  public boolean setDotFill(int index, DotFill dotFill) {
    return false;
  }

  /**
   * setDotRadius
   *
   * @param index int
   * @param radius int
   * @return boolean
   * @todo Implement this shu.plot.PlotInterface method
   */
  public boolean setDotRadius(int index, int radius) {
    return false;
  }

  /**
   * setFixedBounds
   *
   * @param axe int
   * @param min double
   * @param max double
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void setFixedBounds(int axe, double min, double max) {
//    IAxeLayout axeLayout = chart.getAxeLayout();
    org.jzy3d.maths.Scale scale = chart.getScale();

  }

  /**
   * setGridVisible
   *
   * @param axe int
   * @param v boolean
   * @todo Implement this shu.plot.Plot3D method
   */
  public void setGridVisible(int axe, boolean v) {
  }

  /**
   * setLinePlotDrawDot
   *
   * @param drawDot boolean
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void setLinePlotDrawDot(boolean drawDot) {
  }

  /**
   * setLinePlotWidth
   *
   * @param lineWidth int
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void setLinePlotWidth(int lineWidth) {
  }

  /**
   * setLineType
   *
   * @param lineType LineType
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void setLineType(LineType lineType) {
  }

  /**
   * setPlotVisible
   *
   * @param b boolean
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void setPlotVisible(boolean b) {
  }

  /**
   * setPlotVisible
   *
   * @param num int
   * @param b boolean
   * @todo Implement this shu.plot.PlotInterface method
   */
  public void setPlotVisible(int num, boolean b) {
  }

  /**
   * setScatterPlotPattern
   *
   * @param index int
   * @param pattern Pattern
   * @return boolean
   * @todo Implement this shu.plot.PlotInterface method
   */
  public boolean setScatterPlotPattern(int index, Pattern pattern) {
    return false;
  }

  /**
   * setView
   *
   * @param theta double
   * @param phi double
   * @todo Implement this shu.plot.Plot3D method
   */
  public void setView(double theta, double phi) {
  }

  /**
   * toGraphicFile
   *
   * @param file File
   * @todo Implement this shu.plot.PlotWindow method
   */
  public void toGraphicFile(File file) {
  }

  /**
   * zoom
   *
   * @param wPercent double
   * @param hPercent double
   * @todo Implement this shu.plot.Plot3D method
   */
  public void zoom(double wPercent, double hPercent) {
  }
}
