package org.math.plot.plots;

import java.util.*;

import java.awt.*;
import javax.swing.*;

import org.math.plot.*;
import org.math.plot.render.*;
import org.math.plot.utils.*;
import shu.math.operator.*;

/**
 * @author Yann RICHET
 * @version 1.0
 */

/**Layer to add a vector field to an existing Plot*/
public class VectorLayerPlot
    extends LayerPlot {

  public static int RADIUS = 5;
  public static int WIDTH = 1;

  double[][] V;

  /**Create a vector fiels based on data of a plot
    @param p Base plot to support vector field
    @param v Vector field of same lenght that p data */
  public VectorLayerPlot(Plot p, double[][] v) {
    super("Vector of " + p.name, p);
    if (v != null) {
      Array.checkRowDimension(v, p.getData().length);
      Array.checkColumnDimension(v, p.getData()[0].length);
    }
    V = v;

  }

  @Override
  public void setData(double[][] v) {
    V = v;
  }

  @Override
  public double[][] getData() {
    return V;
  }

  public void plot(AbstractDrawer draw, Color c) {
    if (!plot.visible) {
      return;
    }

    draw.setColor(c);

    draw.setLineType(AbstractDrawer.CONTINOUS_LINE);
    draw.setLineWidth(this.WIDTH);
    draw.setDotRadius(this.RADIUS);

    for (int i = 0; i < plot.getData().length; i++) {
      double[] d = Array.getRowCopy(plot.getData(), i);
      for (int j = 0; j < d.length; j++) {
        d[j] += V[i][j];
      }
      double[] start = plot.getData()[i];
      draw.drawLine(start, d);

      if (drawArrow && start.length == 2) {
        //draw arrow at position d
        OffsetOperator cw = new OffsetOperator(d);
        OffsetOperator ccw = new OffsetOperator(d);
        Operator unoffset = cw.getReverseOperator();
        cw.addOperators(SCALE, CW, unoffset);
        ccw.addOperators(SCALE, CCW, unoffset);
        double[] arrow1 = cw.getXY(start);
        double[] arrow2 = ccw.getXY(start);
        draw.drawLine(arrow1, d);
        draw.drawLine(arrow2, d);
      }
    }
  }

  public boolean drawArrow = true;

  private final static ScaleOperator SCALE = new ScaleOperator(0.1);
  private final static RotationOperator CW = new RotationOperator(30.);
  private final static RotationOperator CCW = new RotationOperator( -30.);
  public static void main(String[] args) {
//    vector2D(args);
    vector3D(args);
  }

  public static void vector3D(String[] args) {
    Plot3DPanel p2 = new Plot3DPanel();
    double[][] XYZ = new double[100][3];
    double[][] dXYZ = new double[100][3];
    Random random = new Random(0);

    for (int j = 0; j < XYZ.length; j++) {
      XYZ[j][0] = random.nextDouble() * 10;
      XYZ[j][1] = random.nextDouble() * 10;
      XYZ[j][2] = random.nextDouble() * 10;
      dXYZ[j][0] = 1.0 / Math.sqrt(1 + Math.log(XYZ[j][0]) * Math.log(XYZ[j][0]));
      dXYZ[j][1] = Math.log(XYZ[j][0]) /
          Math.sqrt(1 + Math.log(XYZ[j][0]) * Math.log(XYZ[j][0]));
      dXYZ[j][2] = Math.log(XYZ[j][0]) /
          Math.sqrt(1 + Math.log(XYZ[j][0]) * Math.log(XYZ[j][0]));
    }
    p2.addScatterPlot("toto", XYZ);

    p2.addVectortoPlot(0, dXYZ);
    new FrameView(p2).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public static void vector2D(String[] args) {
    Plot2DPanel p2 = new Plot2DPanel();
    double[][] XYZ = new double[100][2];
    double[][] dXYZ = new double[100][2];
    Random random = new Random(0);

    for (int j = 0; j < XYZ.length; j++) {
      XYZ[j][0] = random.nextDouble() * 10;
      XYZ[j][1] = random.nextDouble() * 10;
      dXYZ[j][0] = 1.0 / Math.sqrt(1 + Math.log(XYZ[j][0]) * Math.log(XYZ[j][0]));
      dXYZ[j][1] = Math.log(XYZ[j][0]) /
          Math.sqrt(1 + Math.log(XYZ[j][0]) * Math.log(XYZ[j][0]));
    }
    p2.addScatterPlot("toto", XYZ);

    p2.addVectortoPlot(0, dXYZ);
    new FrameView(p2).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
