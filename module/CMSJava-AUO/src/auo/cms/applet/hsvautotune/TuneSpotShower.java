package auo.cms.applet.hsvautotune;

import java.io.*;

import org.math.io.files.*;
import shu.cms.colorspace.depend.*;
import shu.cms.plot.*;

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
public class TuneSpotShower {

  public static void main(String[] args) {
    boolean plotHueIn3D = true;
    Plot3D plot = plotHueIn3D ? Plot3D.getInstance() : null;
    Plot3D plot2 = plotHueIn3D ? Plot3D.getInstance() : null;

    for (int x = 0; x < 360; x += 15) {
      double[][] hsvArray = ASCIIFile.readDoubleArray(new File("hsvapplet/" +
          Integer.toString(x) + "HSV.dat"));
      double[][] LChArray = ASCIIFile.readDoubleArray(new File("hsvapplet/" +
          Integer.toString(x) + "LCh.dat"));
      java.awt.Color c = HSV.getLineColor(x);
//      double[] hsvArray = bfHSV.readDoubleArray();
//      double[] LChArray = bfLCh.readDoubleArray();
      int size = hsvArray.length;
      for (int y = 0; y < size; y++) {

        plot.addCacheScatterPlot(Integer.toString(x), c, hsvArray[y]);
        plot2.addCacheScatterPlot(Integer.toString(x), c, LChArray[y]);
      }
    }
    plot.setVisible();
    plot2.setVisible();
  }
}
