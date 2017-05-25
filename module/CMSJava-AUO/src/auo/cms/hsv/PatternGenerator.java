package auo.cms.hsv;

import java.awt.image.WritableRaster;
import shu.cms.colorspace.depend.HSV;
import shu.image.ImageUtils;
import java.awt.image.BufferedImage;

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
public class PatternGenerator {
  public static void main(String[] args) {
    try {
      makePattern(1920, 1080, "pattern.bmp");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void makePattern(int width,
                                 int height, String filename) throws Exception {
    BufferedImage img = new BufferedImage(width, height,
                                          BufferedImage.TYPE_INT_RGB);
    int w = img.getWidth();
    int h = img.getHeight();
    int eachPixelPerHue = (w * h) / 359;
    WritableRaster raster = img.getRaster();
    int index = 0;
    double[] values = new double[3];
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        int hue = (int) Math.round( ( (double) index) / eachPixelPerHue);
        double s = Math.random();
        double v = Math.random();
        values[0] = hue;
        values[1] = s;
        values[2] = v;
        HSV.Sandbox.fastToRGBValues(values);
        raster.setPixel(x, y, values);
        index++;
      }

    }
//    ImageUtils.storeTIFFImage("ComparePattern.tif", img);
    ImageUtils.storeBMPImage(filename, img);
  }
}
