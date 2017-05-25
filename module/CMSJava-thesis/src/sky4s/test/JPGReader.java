package sky4s.test;

import java.io.*;

import java.awt.image.*;

import com.sun.image.codec.jpeg.*;

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
public class JPGReader {
  public JPGReader() {
  }

  public static void main(String[] args) {
    String source = "d2x.jpg";
    String target = "out.jpg";

    BufferedImage srcImg = null;
    JPEGEncodeParam jpd = null;
    try {
      FileInputStream fs = new FileInputStream(source);
      JPEGImageDecoder in = JPEGCodec.createJPEGDecoder(fs);
      srcImg = in.decodeAsBufferedImage();
      jpd = JPEGCodec.getDefaultJPEGEncodeParam(in.getJPEGDecodeParam());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    if (srcImg == null) {
      return;
    }

    int w = srcImg.getWidth();
    int h = srcImg.getHeight();
    int mx = srcImg.getMinX();
    int my = srcImg.getMinY();
    WritableRaster raster = srcImg.getRaster();

    int r, g, b;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        r = raster.getSample(x + mx, y + my, 0);
        g = raster.getSample(x + mx, y + my, 1);
        b = raster.getSample(x + mx, y + my, 2);

        raster.setSample(x + mx, y + my, 0, (int) r);
        raster.setSample(x + mx, y + my, 1, (int) g);
        raster.setSample(x + mx, y + my, 2, (int) b);

      } //for x
    } //for y
    try {
      FileOutputStream fs = new FileOutputStream(target);
      JPEGImageEncoder o = JPEGCodec.createJPEGEncoder(fs, jpd);
      o.encode(srcImg);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
