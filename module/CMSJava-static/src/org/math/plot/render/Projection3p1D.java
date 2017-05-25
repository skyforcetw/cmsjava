package org.math.plot.render;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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
public class Projection3p1D
    extends Projection3D {
  /**
   * Projection3p1D
   *
   * @param _draw AWTDrawer
   */
  public Projection3p1D(AWTDrawer _draw) {
    super(_draw);
  }

  protected double[] baseCoordsScreenProjectionRatio(double[] xyz) {
    double factor = PROJECT_FACTOR;
    double[] sC = new double[3];
    sC[0] = 0.5 + (cos(theta) *
                   ( (xyz[1] -
                      (draw.canvas.base.roundXmax[1] +
                       draw.canvas.base.roundXmin[1]) / 2) /
                    (draw.canvas.base.roundXmax[1] -
                     draw.canvas.base.roundXmin[1]))

                   - sin(theta) *
                   ( (xyz[0] -
                      (draw.canvas.base.roundXmax[0] +
                       draw.canvas.base.roundXmin[0]) / 2) /
                    (draw.canvas.base.roundXmax[0] -
                     draw.canvas.base.roundXmin[0])))
        / factor;

    sC[1] = 0.5 + (cos(phi) *
                   ( (xyz[2] -
                      (draw.canvas.base.roundXmax[2] +
                       draw.canvas.base.roundXmin[2]) / 2) /
                    (draw.canvas.base.roundXmax[2] -
                     draw.canvas.base.roundXmin[2]))

                   - sin(phi) * cos(theta) *
                   ( (xyz[0] -
                      (draw.canvas.base.roundXmax[0] +
                       draw.canvas.base.roundXmin[0]) / 2) /
                    (draw.canvas.base.roundXmax[0] -
                     draw.canvas.base.roundXmin[0]))

                   - sin(phi) * sin(theta) *
                   ( (xyz[1] -
                      (draw.canvas.base.roundXmax[1] +
                       draw.canvas.base.roundXmin[1]) / 2) /
                    (draw.canvas.base.roundXmax[1] -
                     draw.canvas.base.roundXmin[1])))
        / factor;

    sC[2] = ( -sin(phi) * xyz[2]
             - cos(phi) * cos(theta) * xyz[0]
             - cos(phi) * sin(theta) * xyz[1]);

    // System.out.println("Theta = " + theta + " Phi = " + phi);
    // System.out.println("(" + xyz[0] +"," + xyz[1] +"," + xyz[2] + ") ->
    // (" + sC[0] + "," + sC[1] + ")");
    return sC;
  }

  protected void initBaseCoordsProjection() {
    // System.out.println("Projection.initBaseCoordsProjection");
    baseScreenCoords = new int[draw.canvas.base.baseCoords.length][2];
    for (int i = 0; i < draw.canvas.base.dimension + 1; i++) {
      double[] ratio = baseCoordsScreenProjectionRatio(draw.canvas.base.
          baseCoords[i]);
      baseScreenCoords[i][0] = (int) (draw.canvas.getWidth() *
                                      (borderCoeff +
                                       (1 - 2 * borderCoeff) * ratio[0]));
      baseScreenCoords[i][1] = (int) (draw.canvas.getHeight() -
                                      draw.canvas.getHeight() *
                                      (borderCoeff +
                                       (1 - 2 * borderCoeff) * ratio[1]));
    }
  }

}
