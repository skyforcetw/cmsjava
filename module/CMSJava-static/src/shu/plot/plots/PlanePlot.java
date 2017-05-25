package shu.plot.plots;

import java.awt.*;

import org.math.plot.plots.*;
import org.math.plot.render.*;
import org.math.plot.*;

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
public class PlanePlot
    extends Plot {
  public PlanePlot(String n, Color c, double[][][] data) {
    super(n, c);
    this.data = data;
    XYZ_list = buildXYZ_list(data);
  }

  public PlanePlot(String name, Color c, double[][] XYZ_list, int m, int n) {
    super(name, c);
    this.XYZ_list = XYZ_list;
    this.data = buildData(XYZ_list, m, n);
  }

  private double[][] XYZ_list;
  private double[][][] data;
  public boolean fill_shape = true;
  public boolean draw_lines = true;

  private final static double[][] buildXYZ_list(double[][][] data) {
    int m = data.length;
    int n = data[0].length;
    double[][] XYZ_list = new double[m * n][];
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        XYZ_list[i * n + j] = data[i][j];
      }
    }
    return XYZ_list;
  }

  private final static double[][][] buildData(double[][] XYZ_list, int m, int n) {
    double[][][] data = new double[m][n][];
    int index = 0;
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        data[i][j] = XYZ_list[index++];
      }
    }
    return data;
  }

  /**
   * getData
   *
   * @return double[][]
   */
  public double[][] getData() {
    return XYZ_list;
  }

  /**
   * isSelected
   *
   * @param screenCoord int[]
   * @param draw AbstractDrawer
   * @return double[]
   * @todo Implement this org.math.plot.plotObjects.Editable method
   */
  public double[] isSelected(int[] screenCoord, AbstractDrawer draw) {
    return null;
  }

  /**
   * plot
   *
   * @param draw AbstractDrawer
   * @param c Color
   * @todo Implement this org.math.plot.plots.Plot method
   */
  public void plot(AbstractDrawer draw, Color c) {
    if (!visible) {
      return;
    }

    draw.setColor(c);

    if (draw_lines) {
      draw.setLineType(AbstractDrawer.CONTINOUS_LINE);
      int size = data.length;
      for (int x = 0; x < size; x++) {
        double[][] line = data[x];
        for (int y = 0; y < line.length - 1; y++) {
          draw.drawLine(line[y], line[y + 1]);
        }
        if (x > 0) {
          double[][] preLine = data[x - 1];
          for (int y = 0; y < line.length; y++) {
            draw.drawLine(line[y], preLine[y]);
          }
        }
      }
    }
    else {
      draw.setDotType(AbstractDrawer.ROUND_DOT);
      draw.setDotRadius(AbstractDrawer.DEFAULT_DOT_RADIUS);
      for (double[] cord : XYZ_list) {
        draw.drawDot(cord);
      }
    }

    if (fill_shape) {
      int m = data.length;
      int n = data[0].length;
      for (int i = 0; i < m - 1; i++) {
        for (int j = 0; j < n - 1; j++) {
          draw.fillPolygon(0.2f, data[i][j], data[i][j + 1], data[i + 1][j + 1],
                           data[i + 1][j]);
        }
      }

    }

  }

  /**
   * setData
   *
   * @param d double[][]
   */
  public void setData(double[][] d) {
    this.XYZ_list = d;
  }

  public static void main(String[] args) {
    double[][][] data = new double[10][10][3];
    for (int x = 0; x < 10; x++) {
      for (int y = 0; y < 10; y++) {
        data[x][y] = new double[] {
            x, y, x + y};
      }
    }

    Plot3DPanel p = new Plot3DPanel();
    PlanePlot plane = new PlanePlot("", Color.red, data);
    plane.draw_lines = true;
    p.addPlot(plane);

    p.setLegendOrientation(PlotPanel.SOUTH);
    new FrameView(p);

  }
}
