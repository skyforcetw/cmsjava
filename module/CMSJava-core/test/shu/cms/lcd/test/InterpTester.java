package shu.cms.lcd.test;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.plot.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class InterpTester {
  public static void main(String[] args) {
    RGB[] rgbArray = new RGB[256];
    double[] gammas = new double[] {
        2.0, 2.2, 2.4};
    Plot2D plot = Plot2D.getInstance();

    for (int x = 0; x < 256; x++) {
      double normal = x / 256.;
//      Math.pow(normal,2.2)
      double[] values = new double[3];
      for (int y = 0; y < 3; y++) {
        values[y] = Math.pow(normal, gammas[y]) * 256;
      }
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, values,
                        RGB.MaxValue.Double255);
      rgbArray[x] = rgb;
      plot.addCacheScatterLinePlot("R", Color.red, x, rgb.R);
      plot.addCacheScatterLinePlot("G", Color.green, x, rgb.G);
      plot.addCacheScatterLinePlot("B", Color.blue, x, rgb.B);
    }
    plot.setVisible();

  }
}
