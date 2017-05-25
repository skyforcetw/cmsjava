package shu.jai.jaistuff;

import javax.media.jai.*;

import java.awt.image.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class RegionOperators {

  public static PlanarImage smooth(RenderedImage input, float[] kernelMatrix,
                                   int kernelWidth, int kernelHeight) {
//    System.out.println(kernelWidth+" "+kernelHeight);
//    System.out.println(Arrays.toString(kernelMatrix));
//    int w = input.getWidth();
//    int h = input.getHeight();
//    float[] r = new float[w * h];
//    r = input.getData().getPixels(0, 0, w, h, r);
//    System.out.println(Arrays.toString(r));

    KernelJAI kernel = new KernelJAI(kernelWidth, kernelHeight, kernelMatrix);
    PlanarImage output = JAI.create("convolve", input, kernel);

//    System.out.println(kernel.getWidth()+" "+kernel.getHeight());
//   r= output.getData().getPixels(0, 0, w, h, r);
//   System.out.println(Arrays.toString(r));

    return output;
  }
}
