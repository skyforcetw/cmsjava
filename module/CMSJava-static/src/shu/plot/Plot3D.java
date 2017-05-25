package shu.plot;

import java.awt.*;

import org.math.plot.plots.*;
import quickhull3d.*;
import shu.plot.*;
import shu.util.log.*;
import javax.swing.JFrame;
import shu.math.array.DoubleArray;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class Plot3D
    extends PlotBase {

  public static enum Instance {

    JMathPlot3D, LiveGraphics3D, jzy3D
  }

  public static void main(String[] args) {
    Plot3D plot = Plot3D.getInstance("", Plot3D.Instance.jzy3D);
//    Plot3D plot = Plot3D.getInstance("", Plot3D.Instance.JMathPlot3D);
//    Plot3D plot = Plot3D.getInstance("", Plot3D.Instance.LiveGraphics3D);

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
//    plot.addGridPlot("toto", X, Y, Z);
    Color c = new Color(1, 0, 0, .5f);

    int num = plot.addGridPlot("toto", c, Z);
    System.out.println(num);

//    double[][] data = new double[9][3];
//    for (int x = 0; x < 9; x++) {
//      data[x][0] = 0;
//      data[x][1] = 1;
//      data[x][2] = 2;
//    }
//    plot.addGridPlot("", Color.red, data);

//    plot.rotateToAxis(2);
    plot.setVisible();
//    plot.zoom(130, 130);
  }

  protected Plot3D(String title, int width, int height) {
    super(title, width, height);
  }

  protected Plot3D(JFrame frame) {
    super(frame);
  }

  protected Plot3D(Plot3D plot) {
    super(plot.frame);
  }

  public abstract int addGridPlot(String name, Color c, double[] X, double[] Y,
                                  double[][] Z);

  public int addGridPlot(String name, double[] X, double[] Y,
                         double[][] Z) {
    return addGridPlot(name, getNewColor(), X, Y, Z);
  }

  public int addGridPlot(String name, Color c, double[][] Z) {
    int height = Z.length;
    int width = Z[0].length;
    double[] X = DoubleArray.buildX(0, width - 1, width);
    double[] Y = DoubleArray.buildX(0, height - 1, height);
    return addGridPlot(name, c, X, Y, Z);

  }

  public int addGridPlot(String name, double[][] Z) {
    return addGridPlot(name, getNewColor(), Z);
  }

  public abstract int addPlanePlot(String name, Color c, double[][][] data);

  public int addPlanePlot(String name, double[][][] data) {
    return addPlanePlot(name, getNewColor(), data);
  }

  public int addLinePlot(String name, Color color, double[] point1,
                         double[] point2) {
    return addLinePlot(name, color, new double[][] {point1, point2});
  }

  public final int addScatterPlot(String name, double[] ...XY) {
    return addScatterPlot(name, getNewColor(), XY);
  }

  public int addVectortoPlot(String name, Color c, double[] XY, double[] v) {
    int num = this.addScatterPlot(name, c, XY);
    addVectortoPlot(num, new double[][] {v});
    return num;
  }

  public final int addScatterPlot(String name, double x, double y,
                                  double z) {
    return addScatterPlot(name, getNewColor(), new double[] {x, y, z});
  }

  public final int addScatterPlot(String name, Color c, double x, double y,
                                  double z) {
    return addScatterPlot(name, c, new double[] {x, y, z});
  }

  public abstract int addPolygonPlot(String name, Color c, double[] p0,
                                     double[] p1, double[] p2);

  public abstract int addPolygonPlot(String name, Color c, double[] p0,
                                     double[] p1, double[] p2, double[] p3);

  public abstract int addPlot(Plot newPlot);

  public static Plot3D getInstance() {
    return Plot3D.getInstance("Plot3D", 600, 600);
  }

  public static Plot3D getInstance(String title, int width, int height,
                                   Instance instance) {
    Plot3D plot = null;
    switch (instance) {
      case JMathPlot3D:
        plot = new JMathPlot3D(title, width, height);
        break;
      case LiveGraphics3D:
        plot = new LiveGraphics3D(title, width, height);
        break;
      case jzy3D:
        plot = jzy3D.getjzy3DInstance(title, width, height);
        break;
    }
    getInstance0(plot);
    return plot;
  }

  public static Plot3D getInstance(String title, int width, int height) {
    return getInstance(title, width, height, Instance.JMathPlot3D);
  }

  public static Plot3D getInstance(String title, Instance instance) {
    return getInstance(title, 600, 600, instance);
  }

  public static Plot3D getInstance(String title) {
    return getInstance(title, 600, 600);
  }

  public final int addHistogramPlot(String name, double[][] XYdX) {
    return addHistogramPlot(name, getNewColor(), XYdX);
  }

  public abstract int addHistogramPlot(String name, Color c, double[][] XYdX);

  public abstract void rotate(int vec0, int vec1);

  public abstract void setView(double theta, double phi);

  public abstract void zoom(double wPercent, double hPercent);

  public void rotateToAxis(int axe) {
    switch (axe) {
      case 0:
        rotate(235, -79);
        break;
      case 1:
        rotate(79, -79);
        break;
      case 2:
        rotate(79, -235);
        break;

      case 4:
        rotate(314, -79);
        break;
      default:
        return;
    }
  }

  public void setAutoRotate(final int vec0, final int vec1, final int interval) {
    Thread t = new Thread() {

      public void run() {
        try {
          while (true) {
            Thread.currentThread().sleep(interval);
            rotate(vec0, vec1);
          }
        }
        catch (InterruptedException ex) {
          Logger.log.error("", ex);
        }
      }
    };
    t.start();
//    t.interrupt();
  }

  public int getAxisCount() {
    return 3;
  }
}
