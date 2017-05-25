package shu.cms.plot.test;

import java.awt.*;

import quickhull3d.*;
import shu.cms.plot.*;
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
public class QuickHull3DTester {

  public static void main(String[] args) {
    int count = 100;
//    double[][] dataset = new double[count][];
    Point3d[] points = new Point3d[count];
    for (int x = 0; x < count; x++) {
      points[x] = new Point3d(Math.random(), Math.random(), Math.random());
    }
    QuickHull3D qh = new QuickHull3D(points);
    Plot3D plot = Plot3D.getInstance();
    plot.addQuickHull3D(Color.red, qh, .1f);
    plot.setVisible();
  }
}
