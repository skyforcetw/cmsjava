package org.math.plot.render;

import org.math.plot.canvas.PlotCanvas;

public class AWTDrawer3D
    extends AWTDrawer {

  public AWTDrawer3D(PlotCanvas _canvas) {
    super(_canvas);
    projection = new Projection3D(this);
//    projection = new Projection3p1D(this);
  }

  public void rotate(int[] t, int[] panelSize) {
    ( (Projection3D) projection).rotate(t, panelSize);
  }

  public void setView(double _theta, double _phi) {
    ( (Projection3D) projection).setView(_theta, _phi);
  }
}
