package shu.plot.plots;

import org.math.plot.plots.Plot;
import java.awt.Color;
import org.math.plot.render.AbstractDrawer;
import org.math.plot.*;
import org.math.plot.FrameView;
import org.math.plot.PlotPanel;

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
public class GridPlot2D
    extends Plot {

  public static void main(String[] args) {

    int n = 10;
    int m = 10;
    Plot2DPanel p = new Plot2DPanel();
    double[][] X = new double[m][n];
    double[][] Y = new double[m][n];
//    double[][] Z = new double[m][n];

    for (int i = 0; i < n; i++) {
//      X[i] = i / (double) n;
      for (int j = 0; j < m; j++) {
//        Y[j] = j / (double) m;
//        Z[j][i] = Math.exp(X[i]) + Y[j];
        X[i][j] = i;
        Y[i][j] = j;
      }
    }
//      p.addGridPlot("toto", X, Y, Z);
    GridPlot2D grid = new GridPlot2D("", Color.red, X, Y);
////    grid.draw_lines = false;
    p.addPlot(grid);

    p.setLegendOrientation(PlotPanel.SOUTH);
    new FrameView(p);
  }

//  double[] X;
//  double[] Y;
//  double[][] Z;
  double[][] X;
  double[][] Y;
  private double[][] XYZ_list;
  public boolean draw_lines = true;

  private void buildXYZ_list() {
    XYZ_list = new double[X.length * Y.length][2];
    for (int i = 0; i < X.length; i++) {
      for (int j = 0; j < Y.length; j++) {
        XYZ_list[i + (j) * X.length][0] = X[i][j];
        XYZ_list[i + (j) * X.length][1] = Y[i][j];
//        XYZ_list[i + (j) * X.length][1] = Z[j][i];
      }
    }
  }

//  /**
//   * GridPlot2D
//   *
//   * @param n String
//   * @param c Color
//   * @param _X double[]
//   * @param _Y double[]
//   * @param _Z double[][]
//   */
//  public GridPlot2D(String n, Color c, double[] _X, double[] _Y, double[][] _Z) {
//    super(n, c);
//    X = _X;
//    Y = _Y;
//    Z = _Z;
//    buildXYZ_list();
//
//  }
//
//  public GridPlot2D(String n, Color c, double[][] _Z) {
//    super(n, c);
////    X = _X;
////    Y = _Y;
//    Z = _Z;
//    buildXYZ_list();
//  }

  public GridPlot2D(String n, Color c, double[][] _X, double[][] _Y) {
    super(n, c);
    if (_X.length != _Y.length || _X[0].length != _Y[0].length) {
      throw new IllegalArgumentException(
          "_X.length != _Y.length || _X[0].length != _Y[0].length");
    }
    X = _X;
    Y = _Y;
    buildXYZ_list();
  }

  /**
   * getData
   *
   * @return double[][]
   */
  public double[][] getData() {
    return XYZ_list;
//    return null;
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
   */
  public void plot(AbstractDrawer draw, Color c) {
    if (!visible) {
      return;
    }

    draw.setColor(c);

    if (draw_lines) {
      draw.setLineType(AbstractDrawer.CONTINOUS_LINE);
      //直的直的
      for (int i = 0; i < X.length; i++) {
        for (int j = 0; j < Y.length - 1; j++) {
//          draw.drawLine(new double[] {X[i], Y[j]}, new double[] {X[i],
//                        Y[j + 1]});
          draw.drawLine(new double[] {X[i][j], Y[i][j]},
                        new double[] {X[i][j + 1],
                        Y[i][j + 1]});
        }
      }

      //橫的橫的
      for (int j = 0; j < Y.length; j++) {
        for (int i = 0; i < X.length - 1; i++) {
          draw.drawLine(new double[] {X[i][j], Y[i][j]},
                        new double[] {X[i + 1][j], Y[i + 1][j]});
        }
      }
    }
//    else {
//      draw.setDotType(AbstractDrawer.ROUND_DOT);
//      draw.setDotRadius(AbstractDrawer.DEFAULT_DOT_RADIUS);
//      for (int i = 0; i < X.length; i++) {
//        for (int j = 0; j < Y.length; j++) {
//          draw.drawDot(new double[] {X[i], Y[j], Z[j][i]});
//        }
//      }
//    }

//if (fill_shape) {
//    for (int j = 0; j < Y.length - 1; j++) {
//        for (int i = 0; i < X.length - 1; i++) {
//            draw.fillPolygon(0.2f, new double[]{X[i], Y[j], Z[j][i]},
//                    new double[]{X[i + 1], Y[j], Z[j][i + 1]},
//                    new double[]{X[i + 1], Y[j + 1],
//                        Z[j + 1][i + 1]}, new double[]{X[i], Y[j + 1],
//                        Z[j + 1][i]});
//        }
//    }
//}

  }

  /**
   * setData
   *
   * @param d double[][]
   */
  public void setData(double[][] d) {
    this.XYZ_list = d;
  }

}
