package shu.plot.jzy3d;

import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.plot3d.primitives.Point;
import java.util.ArrayList;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.global.Settings;
import org.jzy3d.ui.ChartLauncher;
import java.awt.Rectangle;

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
public class jzy3DTester {
  public static void main(String[] args) {
    // Build a polygon list
    double[][] distDataProp = new double[][] {
        {
        .25, .45, .20}, {
        .56, .89, .45}, {
        .6, .3, .7}
    };
    List<Polygon> polygons = new ArrayList<Polygon> ();
    for (int i = 0; i < distDataProp.length - 1; i++) {
      for (int j = 0; j < distDataProp[i].length - 1; j++) {
        Polygon polygon = new Polygon();
        polygon.add(new Point(new Coord3d(i, j, distDataProp[i][j])));
        polygon.add(new Point(new Coord3d(i, j + 1, distDataProp[i][j + 1])));
        polygon.add(new Point(new Coord3d(i + 1, j + 1,
                                          distDataProp[i + 1][j + 1])));
        polygon.add(new Point(new Coord3d(i + 1, j, distDataProp[i + 1][j])));
        polygons.add(polygon);
      }
    }

    // Creates the 3d object
    Shape surface = new Shape(polygons);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(),
                                           surface.getBounds().getZmin(),
                                           surface.getBounds().getZmax(),
                                           new
                                           org.jzy3d.colors.Color(1, 1, 1, 1f)));
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(org.jzy3d.colors.Color.BLACK);

    Chart chart = new Chart();
    chart.getScene().getGraph().add(surface);

    Settings.getInstance().setHardwareAccelerated(true);
//    Chart chart = demo.getChart();

//    ChartLauncher.instructions();
    Rectangle rectangle = new Rectangle(600, 600);
    ChartLauncher.openChart(chart, rectangle, "title");
//    ChartLauncher.openStaticChart(chart, rectangle, "title");
  }
}
