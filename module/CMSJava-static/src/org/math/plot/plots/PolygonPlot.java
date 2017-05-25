package org.math.plot.plots;

import java.awt.Color;

import org.math.plot.FrameView;
import org.math.plot.Plot3DPanel;
import org.math.plot.PlotPanel;
import org.math.plot.render.AbstractDrawer;

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
public class PolygonPlot
    extends Plot {

  public boolean draw_lines = true;

  public boolean fill_shape = true;

  public float alpha;

  public double[][] polygon;

  public Color lineColor;

  public PolygonPlot(String n, Color c, double[] ...polygon) {
    this(n, c, .4f, polygon);
  }

  public PolygonPlot(String n, Color c, float _alpha, double[] ...polygon) {
    super(n, c);
    this.alpha = _alpha;
    this.polygon = polygon;
    lineColor = c;

  }

  public void plot(AbstractDrawer draw, Color c) {
    if (!visible) {
      return;
    }

    if (draw_lines) {
      draw.setLineType(AbstractDrawer.CONTINOUS_LINE);
      draw.setColor(lineColor);
      draw.drawPolygon(polygon);
    }

    if (fill_shape) {
      draw.setColor(c);
      draw.fillPolygon(alpha, polygon);
    }
  }

  @Override
  public void setData(double[][] polygon) {
    this.polygon = polygon;
  }

  @Override
  public double[][] getData() {
    return polygon;
  }

  public double[] isSelected(int[] screenCoordTest, AbstractDrawer draw) {
//    for (int i = 0; i < X.length; i++) {
//      for (int j = 0; j < Y.length; j++) {
//        double[] XY = {
//            X[i], Y[j], Z[j][i]};
//        int[] screenCoord = draw.project(XY);
//
//        if ( (screenCoord[0] + note_precision > screenCoordTest[0]) &&
//            (screenCoord[0] - note_precision < screenCoordTest[0])
//            && (screenCoord[1] + note_precision > screenCoordTest[1]) &&
//            (screenCoord[1] - note_precision < screenCoordTest[1])) {
//          return XY;
//        }
//      }
//    }
    return null;
  }

  public static void main(String[] args) {

    Plot3DPanel p = new Plot3DPanel();
    PolygonPlot polygon = new PolygonPlot("", Color.red, new double[] {1, 1, 1},
                                          new double[] {1, .2, 1},
                                          new double[] {.2, 1, 1});
    polygon.draw_lines = false;
//    polygon.fill_shape = false;
    p.addPlot(polygon);
//    p.addPlot(new PolygonPlot("", Color.green, new double[] {1, 1, .5},
//                              new double[] {1, .2, .5}, new double[] {.2, 1, .5}));
//    p.addPlot(new PolygonPlot("", Color.blue, new double[] {1, 1, .2},
//                              new double[] {1, .2, .2}, new double[] {.2, 1, .2}));
    p.setFixedBounds(0, 0, 1);
    p.setFixedBounds(1, 0, 1);
    p.setFixedBounds(2, 0, 1);
    p.setLegendOrientation(PlotPanel.SOUTH);
    new FrameView(p);
  }
}
