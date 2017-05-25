package sky4s.test.jai;

import javax.media.jai.*;

import java.awt.*;
import java.awt.image.renderable.*;
import javax.swing.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
/**
 * This class demonstrates the usage of the histogram operator.
 */
public class Histogram {
  /**
   * The entry point for the application.
   * @param args
   */
  public static void main(String[] args) {
    // Read the image.
    PlanarImage image = JAI.create("fileload", "Image/miko.jpg");

    // Create one histogram with 256 bins.
    ParameterBlock pb1 = new ParameterBlock();
    pb1.addSource(image);
    pb1.add(null); // The ROI
    pb1.add(1);
    pb1.add(1); // Sampling
    pb1.add(new int[] {256}); // Bins
    pb1.add(new double[] {0});
    pb1.add(new double[] {256}); // Range for inclusion
    PlanarImage dummyImage1 = JAI.create("histogram", pb1);
    // Gets the histogram.
    javax.media.jai.Histogram histo1 = (javax.media.jai.Histogram) dummyImage1.
        getProperty("histogram");

    // Show those histograms in a GUI application. Set some parameters on the
    // DisplayHistogram components to adjust the
    JFrame f = new JFrame("Histograms");
    DisplayHistogram dh1 = new DisplayHistogram(histo1, "256 bins");
    dh1.setBinWidth(2);
    dh1.setHeight(160);
    dh1.setIndexMultiplier(1);

    f.getContentPane().setLayout(new GridLayout(1, 1));
    f.getContentPane().add(dh1);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.pack();
    f.setVisible(true);
  }
}
