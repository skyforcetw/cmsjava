package shu.plot;

import java.io.*;
import java.util.*;
import java.util.List;

import java.awt.*;
import javax.swing.*;

import org.math.plot.plots.*;
import shu.plot.livegraphics3d.*;
import shu.math.array.*;

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
public class LiveGraphics3D
    extends Plot3D {

  public static void main(String[] args) {
    LiveGraphics3D plot = new LiveGraphics3D("", 300, 300);
    plot.setVisible();
  }

  LiveGraphics3D(String title, int width, int height) {
    super(title, width, height);
//     this.setContentPane(plot);
    applet = new LiveApplet(this.frame, title, width, height);
  }

  public void setInput(String input) {
//    Logger.log.info(input);
    this.input = input;
    applet.setInput(input);
  }

  public void setInputFile(String filename) {
    applet.setInputFile(filename);
  }

  public String getInput() {
    return input;
  }

  private String input;
  private LiveApplet applet;
  private StringBuilder primitives = new StringBuilder();
  private Graphics3DOptions options = new Graphics3DOptions();

  private static class Graphics3DOptions {

    double[][] plotRange = null;
    double[] boxRatios = new double[] {
        1, 1, 1};
    boolean boxed = true;
    boolean[] axes = new boolean[] {
        true, true, true};
    String[] axesLabel = new String[] {
        "X", "Y", "Z"};
    boolean lighting = false;

    public String toString() {
      StringBuilder buf = new StringBuilder();
      if (plotRange != null) {
        buf.append(", PlotRange->");
        buf.append(replace(Arrays.deepToString(plotRange)));
      }
      if (boxRatios != null) {
        buf.append(", BoxRatios->");
        buf.append(replace(Arrays.toString(boxRatios)));
      }
      buf.append(", Boxed->" + (boxed ? "True" : "False"));
      buf.append(", Axes->" + replace(Arrays.toString(axes)).replace('t', 'T'));
      buf.append(", AxesLabel->" + replace(Arrays.toString(axesLabel)));
      buf.append(", Lighting->" + (lighting ? "True" : "False"));
      return buf.toString();
    }
  }

  private static String replace(String s) {
    return s.replace('[', '{').replace(']', '}');
  }

  /**
   *
   * @param name String
   * @param c Color
   * @param data double[][][]
   * @return int
   * @todo
   */
  public int addPlanePlot(String name, Color c, double[][][] data) {
    return -1;
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
   * @todo Implement this shu.cms.plot.Plot3D method
   */
  public int addGridPlot(String name, Color c, double[] X, double[] Y,
                         double[][] Z) {
    if (X.length != Z[0].length || Y.length != Z.length) {
      throw new java.lang.IllegalArgumentException(
          "X.length != Z[0].length || Y.length != Z.length");
    }
    int height = Y.length;
    int width = X.length;
    for (int m = 0; m < (height - 1); m++) {
      for (int n = 0; n < (width - 1); n++) {
        double x1 = X[n];
        double y1 = Y[m];

        double x2 = X[n + 1];
        double y2 = Y[m + 1];

        double z1 = Z[m][n];
        double z2 = Z[m][n + 1];
        double z3 = Z[m + 1][n];
        double z4 = Z[m + 1][n + 1];

        double[] p1 = new double[] {
            x1, y1, z1};
        double[] p2 = new double[] {
            x2, y1, z2};
        double[] p3 = new double[] {
            x1, y2, z3};
        double[] p4 = new double[] {
            x2, y2, z4};
        addPolygonPlot(name, c, p1, p2, p4, p3);
      }
    }
    return -1;

//    if (X.length != Z.length || Y.length != Z[0].length) {
//      throw new java.lang.IllegalArgumentException(
//          "X.length != Z.length || Y.length != Z[0].length");
//    }
//    int height = X.length;
//    int width = Y.length;
//    for (int m = 0; m < (height - 1); m++) {
//      for (int n = 0; n < (width - 1); n++) {
//        double x1 = X[m];
//        double y1 = Y[n];
//        double x2 = X[m + 1];
//        double y2 = Y[n + 1];
//        double z1 = Z[m][n];
//        double z2 = Z[m + 1][n];
//        double z3 = Z[m][n + 1];
//        double z4 = Z[m + 1][n + 1];
//        double[] p1 = new double[] {
//            x1, y1, z1};
//        double[] p2 = new double[] {
//            x2, y1, z2};
//        double[] p3 = new double[] {
//            x1, y2, z3};
//        double[] p4 = new double[] {
//            x2, y2, z4};
//        addPolygonPlot(name, c, p1, p2, p4, p3);
//      }
//    }
//    return -1;
  }

  /**
   * addHistogramPlot
   *
   * @param name String
   * @param c Color
   * @param XYdX double[][]
   * @return int
   * @todo Implement this shu.cms.plot.Plot3D method
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
   * @todo Implement this shu.cms.plot.Plot3D method
   */
  public void addImage(Image img, float alpha, double[] xySW, double[] xySE,
                       double[] xyNW) {
  }

  /**
   * addLegend
   *
   * @param o String
   */
  public void addLegend(String o) {
  }

  /**
   * addLegend
   *
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
   */
  public int addLinePlot(String name, Color c, double[] ...XY) {
    return setPlot(name, c, "Line[" + replace(Arrays.deepToString(XY)));
  }

  private List<String> nameList = new LinkedList<String> ();

  private int setPlot(String name, Color c, String description) {
    nameList.add(name);
    primitives.append(" { RGBColor");
    primitives.append(Arrays.toString(c.getColorComponents(new float[3])));
//    float[] com = c.getColorComponents(new float[3]);
//    primitives.append("[" + com[0] + "," + com[1] + "," + com[2] + ",0.5]");
    primitives.append(", ");
    primitives.append(description);
    primitives.append("] },");
    return plotIndex++;
  }

  private boolean polygonDrawLine = true;
  public void setPolygonDrawLine(boolean drawLine) {
    this.polygonDrawLine = drawLine;
  }

  /**
   * addPlot
   *
   * @param newPlot Plot
   * @return int
   * @todo Implement this shu.cms.plot.Plot3D method
   */
  public int addPlot(Plot newPlot) {
    if (newPlot instanceof PolygonPlot) {
      PolygonPlot p = (PolygonPlot) newPlot;
      polygonDrawLine = p.draw_lines;
      switch (p.polygon.length) {
        case 3:
          return this.addPolygonPlot(p.name, p.color, p.polygon[0], p.polygon[1],
                                     p.polygon[2]);
        case 4:
          return this.addPolygonPlot(p.name, p.color, p.polygon[0], p.polygon[1],
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
    String s = (polygonDrawLine ? "" : "EdgeForm[],") + "Polygon[{" +
        toString(new double[][] {p0, p1, p2}) + "}";
    return setPlot(name, c, s);
  }

  public int addPolygonPlot(String name, Color c, double[] p0, double[] p1,
                            double[] p2, double[] p3) {

    String s = (polygonDrawLine ? "" : "EdgeForm[],") + "Polygon[{" +
        toString(new double[][] {p0, p1, p2, p3}) + "}";
    return setPlot(name, c, s);
  }

  private final static String toString(double[][] points) {
    StringBuilder buf = new StringBuilder();
    int size = points.length;
    for (int x = 0; x < size; x++) {
      double[] p = points[x];
      //String s = Arrays.toString(p);
      String s = "["
          + // DoubleArray.toString("################.################",
          (DoubleArray.toString("%16.16f", p)).replace(' ', ',') + "]";
      //String s = "[" + DoubleArray.toString("%16.16f", p) + "]";
      buf.append(replace(s));
      if (x != size - 1) {
        buf.append(", ");
      }
    }
    String result = buf.toString();
    return result;
  }

  /**
   * addScatterPlot
   *
   * @param name String
   * @param c Color
   * @param XY double[][]
   * @return int
   * @todo Implement this shu.cms.plot.Plot3D method
   */
  public int addScatterPlot(String name, Color c, double[] ...XY) {
    return setPlot(name, c, "Point[" + replace(Arrays.toString(XY[0])));
  }

  private int plotIndex = 0;

  /**
   * getFixedBounds
   *
   * @param axe int
   * @return double[]
   */
  public double[] getFixedBounds(int axe) {
    throw new UnsupportedOperationException();
  }

  /**
   * getPlotPanel
   *
   * @return JPanel
   * @todo Implement this shu.cms.plot.PlotWindow method
   */
  public JPanel getPlotPanel() {
    return null;
  }

  /**
   * getPlotSize
   *
   * @return int
   * @todo Implement this shu.cms.plot.PlotWindow method
   */
  public int getPlotSize() {
    return 0;
  }

  /**
   * removeAllPlots
   *
   */
  public void removeAllPlots() {
    throw new UnsupportedOperationException();
  }

  public void removePlot(int index) {
    throw new UnsupportedOperationException();
  }

  /**
   * rotate
   *
   * @param vec0 int
   * @param vec1 int
   * @todo Implement this shu.cms.plot.Plot3D method
   */
  public void rotate(int vec0, int vec1) {
  }

  /**
   *
   * @param wPercent double
   * @param hPercent double
   * @todo Implement this shu.cms.plot.Plot3D method
   */
  public void zoom(double wPercent, double hPercent) {

  }

  /**
   * setAxeLabel
   *
   * @param axe int
   * @param label String
   */
  public void setAxeLabel(int axe, String label) {
    options.axesLabel[axe] = label;
  }

  public void setAxisVisible(int axe, boolean v) {
    options.axes[axe] = v;
  }

  /**
   * setBackground
   *
   * @param bg Color
   */
  public void setBackground(Color bg) {
    applet.setBackgroundColor(Integer.toHexString(bg.getRGB() & 0x00ffffff));
  }

  /**
   * setFixedBounds
   *
   * @param axe int
   * @param min double
   * @param max double
   * @todo Implement this shu.cms.plot.PlotWindow method
   */
  public void setFixedBounds(int axe, double min, double max) {
  }

  /**
   * setGridVisible
   *
   * @param axe int
   * @param v boolean
   * @todo Implement this shu.cms.plot.Plot3D method
   */
  public void setGridVisible(int axe, boolean v) {
  }

  /**
   * setPlotVisible
   *
   * @param b boolean
   * @todo Implement this shu.cms.plot.PlotWindow method
   */
  public void setPlotVisible(boolean b) {
  }

  /**
   *
   * @param num int
   * @param b boolean
   */
  public void setPlotVisible(int num, boolean b) {

  }

  public void setVisible(boolean visible) {
    super.setVisible(visible);
    if (visible) {
      this.setVisible();
    }
  }

  public void setVisible() {
    super.setVisible(true);
    if (primitives.length() != 0) {
      primitives.insert(0, "Graphics3D[{");
      primitives.append('}');
      primitives.append(options.toString());
      primitives.append(']');
//      System.out.println(primitives.toString());
      this.setInput(primitives.toString());
//      applet.setInput(input);
    }
    applet.setVisible();
  }

  /**
   *
   * @param numPlot int
   * @param v double[][]
   * @todo L addVectortoPlot
   */
  public void addVectortoPlot(int numPlot, double[][] v) {
  }

  public void _dispose() {
  }

  /**
   *
   * @param axe int
   * @param scale Scale
   * @todo L setAxisScale
   */
  public void setAxisScale(int axe, Scale scale) {
  }

  /**
   *
   * @param file File
   * @todo L toGraphicFile
   */
  public void toGraphicFile(File file) {
  }

  public void setChartTitle(String title) {
  }

  public void setView(double theta, double phi) {

  }

  public void setLinePlotDrawDot(boolean drawDot) {}

  public void setLinePlotWidth(int lineWidth) {}

  public void setLineType(LineType lineType) {}

  public boolean setScatterPlotPattern(int index, Pattern pattern) {
    return false;
  }

  public boolean setDotRadius(int index, int radius) {
    return false;
  }

  public boolean setDotFill(int index, DotFill dotFill) {
    return false;
  }
}
