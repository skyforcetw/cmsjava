package auo.sharpness;

import shu.image.ImageUtils;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.image.WritableRaster;
import shu.image.*;
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
public class SharpnessAreaCounter {

  public static void main(String[] args) {
    String dir =
        "D:/軟體/nobody zone/Pattern/Sharpness Pattern/Sharpness Area Compare/新資料夾/";
    String filename1 = dir + "shutterstock_10421110_cr.bmp";
    String filename2 = dir + "shutterstock_10421110_cr_s4_frame2.bmp";
    try {
      BufferedImage img1 = ImageUtils.loadImage(filename1);
      BufferedImage img2 = ImageUtils.loadImage(filename2);
      int w = img1.getWidth();
      int h = img1.getHeight();
      WritableRaster raster1 = img1.getRaster();
      WritableRaster raster2 = img2.getRaster();
      int[] rgbValues = new int[3];
      int[] lumiArray = new int[101];

      for (int x = 0; x < w; x++) {
        for (int y = 0; y < h; y++) {
          raster1.getPixel(x, y, rgbValues);
          double lumi1 = BlurDetector.getLightness(rgbValues);
          raster2.getPixel(x, y, rgbValues);
          double lumi2 = BlurDetector.getLightness(rgbValues);
//          System.out.println(lumi1+" "+lumi2);
          double dLumi = Math.abs(lumi1 - lumi2);
//          System.out.println(lumi1+" "+lumi2+" "+dLumi);
          lumiArray[ ( (int) Math.round(dLumi))]++;
        }
      }

      for (int x = 0; x < 101; x++) {
        System.out.println(x + " " + lumiArray[x]);
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
