package shu.jai.operators.mosaic;

import javax.media.jai.*;

import java.awt.*;
import java.awt.image.*;

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
public class CFAMosaicOpImage
    extends PointOpImage {
  // The source image.
  private RenderedImage source;
  // The operator parameters.
  private char[] ch;

  /**
   * The constructor for the class, which will call the superclass constructor
   * and store the parameters' values locally.
   * @param source RenderedImage the source image
   * @param ch0 char
   * @param ch1 char
   * @param ch2 char
   * @param ch3 char
   * @param layout ImageLayout the image layout
   * @param hints RenderingHints the rendering hints
   * @param b boolean indicates whether computeRect() expects contiguous sources. (??)
   */
  public CFAMosaicOpImage(RenderedImage source,
                          char ch0, char ch1, char ch2, char ch3,
                          ImageLayout layout,
                          RenderingHints hints, boolean b) {
    super(source, layout, hints, b);
    this.source = source;
    ch = new char[] {
        ch0, ch1, ch2, ch3};
  }

  /**
   * This method will be called when we need to compute a tile for that image.
   */
  public Raster computeTile(int x, int y) {
    Raster r = source.getTile(x, y);
    int minX = r.getMinX();
    int minY = r.getMinY();
    int width = r.getWidth();
    int height = r.getHeight();
    // Notice that we *must* create a WritableRaster with position coordinates!
    WritableRaster wr =
        r.createCompatibleWritableRaster(minX, minY, width, height);
    // The main algorithm is here.
    for (int n = 0; n < r.getHeight(); n++) {
      for (int m = 0; m < r.getWidth(); m++) {
        int index = ( (n % 2 == 0) ? 0 : 2) + ( (m % 2 == 0) ? 0 : 1);
        char c = ch[index];

        int chIndex = -1;
        switch (c) {
          case 'R':
          case 'r':
            chIndex = 0;
            break;
          case 'G':
          case 'g':
            chIndex = 1;
            break;
          case 'B':
          case 'b':
            chIndex = 2;
            break;
        }
//        int p = r.getSample(m + minX, n + minY, 0);
//        int p = r.getSample(c + minX, l + minY, 0);
//        if (p < threshold1) {
//          p = 0;
//        }
//        else if (p > threshold2) {
//          p = 255;
//        }
//        else {
//          p = 127;
//        }
        int p = r.getSample(m + minX, n + minY, chIndex);
        wr.setSample(m + minX, n + minY, chIndex, p);
      }
    }
    return wr;
  }

}
