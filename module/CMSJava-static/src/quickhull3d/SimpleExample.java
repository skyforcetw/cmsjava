package quickhull3d;

//import shu.cms.plot.Plot3D;
import java.awt.Color;
//import org.math.plot.plots.PolygonPlot;

/**
 * Simple example usage of QuickHull3D. Run as the command
 * <pre>
 *   java quickhull3d.SimpleExample
 * </pre>
 */
public class SimpleExample {
  /**
   * Run for a simple demonstration of QuickHull3D.
   */
  public static void main(String[] args) {
    // x y z coordinates of 6 points
    Point3d[] points = new Point3d[] {
        new Point3d(0.0, 0.0, 0.0),
        new Point3d(1.0, 0.5, 0.0),
        new Point3d(2.0, 0.0, 0.0),
        new Point3d(0.5, 0.5, 0.5),
        new Point3d(0.0, 0.0, 2.0),
        new Point3d(0.1, 0.2, 0.3),
        new Point3d(0.0, 2.0, 0.0),
    };

    QuickHull3D hull = new QuickHull3D();
    hull.build(points);

    System.out.println("Vertices:");
    Point3d[] vertices = hull.getVertices();
    for (int i = 0; i < vertices.length; i++) {
      Point3d pnt = vertices[i];
      System.out.println(pnt.x + " " + pnt.y + " " + pnt.z);
    }

    System.out.println("Faces:");
    int[][] faceIndices = hull.getFaces();
    for (int i = 0; i < vertices.length; i++) {
      for (int k = 0; k < faceIndices[i].length; k++) {
        System.out.print(faceIndices[i][k] + " ");
      }
      System.out.println("");
    }

//    Plot3D plot = Plot3D.getInstance();
//    for (Point3d p : points) {
//      plot.addScatterPlot("", Color.black, p.x, p.y, p.z);
//    }
//    for (Point3d p : vertices) {
//      plot.addScatterPlot("", Color.red, p.x, p.y, p.z);
//    }
//    plot.addQuickHull3D(Color.red, hull, .1f);

//    for (int[] index : faceIndices) {
//      Point3d p0 = vertices[index[0]];
//      Point3d p1 = vertices[index[1]];
//      Point3d p2 = vertices[index[2]];
//
//      PolygonPlot polygon = new PolygonPlot("", Color.green,
//                                            new double[] {p0.x, p0.y, p0.z},
//                                            new double[] {p1.x, p1.y, p1.z},
//                                            new double[] {p2.x, p2.y, p2.z});
//      polygon.alpha = 0.1f;
//      plot.addPlot(polygon);
//    }

//    plot.setVisible();
  }
}
