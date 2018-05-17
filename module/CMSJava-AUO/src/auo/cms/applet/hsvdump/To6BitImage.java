package auo.cms.applet.hsvdump;

import shu.image.ImageUtils;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class To6BitImage {
  public static void to6Bit(BufferedImage img) {
    WritableRaster raster = img.getRaster();
    int w = raster.getWidth();
    int h = raster.getHeight();
    int[] rgb = new int[3];
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        raster.getPixel(x, y, rgb);
        rgb[0] = (rgb[0] / 4) * 4;
        rgb[1] = (rgb[1] / 4) * 4;
        rgb[2] = (rgb[2] / 4) * 4;
        raster.setPixel(x, y, rgb);
      }
    }
  }

  public static void main(String[] args) {
    try {
      BufferedImage img = ImageUtils.loadImage(
          "D:\\ณnล้\\nobody zone\\exp data\\12307\\Side Effect\\Samsung.png");
      to6Bit(img);
//      WritableRaster raster = img.getRaster();
//      int w = raster.getWidth();
//      int h = raster.getHeight();
//      int[] rgb = new int[3];
//      for (int x = 0; x < w; x++) {
//        for (int y = 0; y < h; y++) {
//          raster.getPixel(x, y, rgb);
//          rgb[0] = (rgb[0] / 4) * 4;
//          rgb[1] = (rgb[1] / 4) * 4;
//          rgb[2] = (rgb[2] / 4) * 4;
//          raster.setPixel(x, y, rgb);
//        }
//      }
//      ImageUtils.storeTIFFImage("avatar_.tif",img);
      ImageUtils.storePNGImage("avatar_.png", img);

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
