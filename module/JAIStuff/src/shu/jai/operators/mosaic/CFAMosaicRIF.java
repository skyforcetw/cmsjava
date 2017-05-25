package shu.jai.operators.mosaic;

import javax.media.jai.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.image.renderable.*;

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
public class CFAMosaicRIF
    implements RenderedImageFactory {
  /**
   * Empty constructor -- some examples point that I must have it, I am not
   * sure why.
   */
  public CFAMosaicRIF() {
  }

  /**
   * The create method, that will be called to create a RenderedImage (or chain
   * of operators that represents one).
   * @param paramBlock ParameterBlock
   * @param hints RenderingHints
   * @return RenderedImage
   */
  public RenderedImage create(ParameterBlock paramBlock, RenderingHints hints) {
    // Get data from the ParameterBlock.
    RenderedImage source = paramBlock.getRenderedSource(0);
    char ch0 = paramBlock.getCharParameter(0);
    char ch1 = paramBlock.getCharParameter(1);
    char ch2 = paramBlock.getCharParameter(2);
    char ch3 = paramBlock.getCharParameter(3);
    // We will copy the input image layout to the output image.
    ImageLayout layout = new ImageLayout(source);
    // Create a new image (or chain) with this information.
//    return new ThreeValuesSegmentationOpImage(source, threshold1, threshold2,
//                                              layout, hints, false);
    return new CFAMosaicOpImage(source, ch0, ch1, ch2, ch3, layout, hints, false);
  }
}
