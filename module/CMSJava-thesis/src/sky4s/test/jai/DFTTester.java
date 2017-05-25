package sky4s.test.jai;

import javax.media.jai.*;
import javax.media.jai.operator.*;

import java.awt.image.renderable.*;

import shu.jai.jaistuff.display.*;

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
public class DFTTester {

  public static void main(String[] args) {
    PlanarImage src = JAI.create("fileload", "Image/S-CIELAB/hats.tiff");

    // Create the ParameterBlock.
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(src);
    pb.add(DFTDescriptor.SCALING_NONE);
    pb.add(DFTDescriptor.REAL_TO_COMPLEX);

    // Create the DFT operation.
    PlanarImage dft = (PlanarImage) JAI.create("dft", pb, null);
    System.out.println("dft end.");
    JAI.create("filestore", dft, "dft.tif", "TIFF");

    new DisplayJAIWithPixelInfo(dft);

//    // Calculate the cutoff "frequencies" from the threshold.
//    threshold /= 200.0F;
//    int minX = (int) (width * threshold);
//    int maxX = width - 1 - minX;
//    int minY = (int) (height * threshold);
//    int maxY = height - 1 - minY;
//
//    // Retrieve the DFT data.
//    Raster dftData = dft.getData();
//    double[] real =
//        dftData.getSamples(0, 0, width, height, 0, (double[])null);
//    double[] imag =
//        dftData.getSamples(0, 0, width, height, 1, (double[])null);
//
//    double[] HR = new double[real.length];
//    double[] HI = new double[imag.length];
//    double[] LR = new double[real.length];
//    double[] LI = new double[imag.length];

  }
}
