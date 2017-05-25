package sky4s.test.jai;

import java.io.*;
import javax.media.jai.*;

import java.awt.*;
import java.awt.image.renderable.*;

import com.sun.media.jai.codec.*;

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
public class JAISampleProgram {

  /**
   *
   * @param args String[]
   * @deprecated
   */

  public static void main(String[] args) {
    args = new String[] {
        "Image/aperture/A.jpg"};
    /* Validate input. */
    if (args.length != 1) {
      System.out.println("Usage: java JAISampleProgram " +
                         "input_image_filename");
      System.exit( -1);
    }

    /*
     * Create an input stream from the specified file name
     * to be used with the file decoding operator.
     */
    FileSeekableStream stream = null;
    try {
      stream = new FileSeekableStream(args[0]);
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }

    /* Create an operator to decode the image file. */
    RenderedOp image1 = JAI.create("stream", stream);

    /*
     * Create a standard bilinear interpolation object to be
     * used with the "scale" operator.
     */
    Interpolation interp = Interpolation.getInstance(
        Interpolation.INTERP_BILINEAR);

    /**
     * Stores the required input source and parameters in a
     * ParameterBlock to be sent to the operation registry,
     * and eventually to the "scale" operator.
     */
    ParameterBlock params = new ParameterBlock();
    params.addSource(image1);
//    params.add(2.0F); // x scale factor
//    params.add(2.0F); // y scale factor
//    params.add(0.0F); // x translate
//    params.add(0.0F); // y translate
//    params.add(interp); // interpolation method

    /* Create an operator to scale image1. */
    RenderedOp image2 = JAI.create("scale", params);

    /* Get the width and height of image2. */
    int width = image2.getWidth();
    int height = image2.getHeight();

    /* Attach image2 to a scrolling panel to be displayed. */
    javax.media.jai.widget.ScrollingImagePanel panel = new javax.media.jai.
        widget.ScrollingImagePanel(
            image1, width, height);

    /* Create a frame to contain the panel. */
    Frame window = new Frame("JAI Sample Program");
    window.add(panel);
    window.pack();
    window.show();
  }
}
