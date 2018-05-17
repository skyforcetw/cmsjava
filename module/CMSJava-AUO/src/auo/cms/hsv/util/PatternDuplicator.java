package auo.cms.hsv.util;

import java.io.*;
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
public class PatternDuplicator {

  public static void main(String[] args) {
    String dirname = "D:\\ณnล้\\nobody zone\\Pattern\\skyforce Pattern Collect";
    File dir = new File(dirname);

    for (File file : dir.listFiles()) {
      String filename = file.getName();
//      for (String filename : dir.list()) {
      if (filename.indexOf(".jpg") == -1 && filename.indexOf(".tif") == -1 &&
          filename.indexOf(".TIF") == -1 && filename.indexOf(".bmp") == -1) {
        continue;
      }
      BufferedImage img = null;
      try {
        img = ImageUtils.loadImage(file.getAbsolutePath());
      }
      catch (IOException ex) {
        ex.printStackTrace();
        continue;
      }

      int w = img.getWidth();
      int h = img.getHeight();

      BufferedImage newimg = new BufferedImage(w * 2, h,
                                               BufferedImage.TYPE_INT_RGB);
      java.awt.Graphics g = newimg.getGraphics();
      g.drawImage(img, 0, 0, null);
      g.drawImage(img, w, 0, null);
      try {
        ImageUtils.storeTIFFImage(dirname + "/Duplicate/" + filename, newimg);
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }

    }
  }
}
