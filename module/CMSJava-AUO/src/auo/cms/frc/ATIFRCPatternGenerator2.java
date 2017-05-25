package auo.cms.frc;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import shu.image.ImageUtils;

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
public class ATIFRCPatternGenerator2 {
  static int[][] PATTERN_13 = new int[][] {
      {
      0, 1, 0}, {
      1, 0, 0}, {
      0, 0, 1}
  };
  static int[][] PATTERN_12 = new int[][] {
      {
      0, 1}, {
      1, 0}
  };
  static int[][] PATTERN_23 = new int[][] {
      {
      1, 0, 1}, {
      0, 1, 1}, {
      1, 1, 0}
  };
//  static int[][][] PATTERN =

  public static BufferedImage[] getFRCBufferedImage(BufferedImage original) {
    BufferedImage[] result = new BufferedImage[6];
    for (int x = 0; x < 6; x++) {
      result[x] = getFRCBufferedImage(original, x);
    }
    return result;
  }

  public static void main(String[] args) {
    main1(args);
//    for (int frame = 0; frame < 6; frame++) {
////  int frame = 3;
//      int pixel = 1;
//      int p = pixel % 4;
//      for (int y = 0; y < 20; y++) {
//        for (int x = 0; x < 6; x++) {
//          int frc = getFRC(y, x, frame, p);
//          System.out.print(frc + " ");
//        }
//        System.out.println("");
//      }
//      System.out.println("\n");
//    }
  }

  public static void main1(String[] args) {
    if (args.length == 0) {
      System.out.println("Please provide filename!");
      return;
    }

    try {
//      BufferedImage img = ATIFRCPatternGenerator.getRGBCMYWGrayScaleImage();
//      BufferedImage grayscale = ATIFRCPatternGenerator.getGrayLevelBlockImage(
//          131);
//      ImageUtils.storeTIFFImage("256grayscale.tif", grayscale);



      BufferedImage img = ImageUtils.loadImage(args[0]);
//      BufferedImage img = ImageUtils.loadImage("line.bmp");

      BufferedImage img6bit = ATIFRCPatternGenerator.get6BitBufferedImage(img);
      WritableRaster raster6bit = img6bit.getRaster();
      int w = raster6bit.getWidth();
      int h = raster6bit.getHeight();
      int[] rgb = new int[3];
      int[] rgb6bit = new int[3];

//      ImageUtils.storeTIFFImage("frc/6bit.tif", img6bit);
      for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
          img.getRaster().getPixel(x, y, rgb);
          System.out.printf("%2.2f ", (rgb[0] / 4.));
        }
        System.out.println("");
      }

      BufferedImage[] imgs = getFRCBufferedImage(img);
//      BufferedImage[] imgs = getFRCBufferedImage(img);

      for (int m = 0; m < imgs.length; m++) {
        BufferedImage frc = imgs[m];
        ImageUtils.storeTIFFImage("frc_" + Integer.toString(m) + ".tif",
                                  frc);
        WritableRaster raster = frc.getRaster();
        System.out.println(m);
        for (int y = 0; y < h; y++) {
          for (int x = 0; x < w; x++) {
            raster.getPixel(x, y, rgb);
            raster6bit.getPixel(x, y, rgb6bit);
            System.out.print(rgb[0] - rgb6bit[0]);
          }
          System.out.println("");
        }
        System.out.println("");
        System.out.println("");
      }

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void showPattern() {
    int pixel = 3;

    for (int frame = 0; frame < 6; frame++) {
      System.out.println("frame: " + frame);
      for (int h = 0; h < 8; h++) {
        for (int w = 0; w < 3; w++) {
          int frc = getFRC(h, w, frame, pixel);
          System.out.print( (frc == 1) ? "***" : "   ");
        }
        System.out.println("");
      }
      System.out.println("================");
    }
  }

  private static int getFRC(int h, int w, int frame, int pixel) {
    int wrollIndex = 0;
    int block = 0;
    int[][] pattern = null;
    int windex = w % 3;
    int framerollIndex = 0;

//    if (w == 34) {
//      int x = 1;
//    }

    switch (pixel) {
      case 1: //+1 .25

//        wrollIndex = (windex == 1) ? -1 : (windex == 2) ? 1 : windex;
//        wrollIndex = (windex == 1) ? 1 : (windex == 2) ? -1 : windex;
        wrollIndex = windex;
        pattern = PATTERN_13;
        block = 3;
        frame = frame % block;
        framerollIndex = (frame == 1) ? -1 : (frame == 2) ? 1 : frame;
        break;
      case 2: //+2 .5
        wrollIndex = w % 2;
        pattern = PATTERN_12;
        block = 2;
        framerollIndex = frame % 2;
        break;
      case 3: //+3 .75

//        wrollIndex = (windex == 1) ? 1 : (windex == 2) ? -1 : windex;
        wrollIndex = windex;
        pattern = PATTERN_23;
        block = 3;
        frame = frame % block;
        framerollIndex = (frame == 1) ? 1 : (frame == 2) ? -1 : frame;
        break;
      default:
        return 0;
    }
//    int framerollIndex =(frame == 1) ? -1 : (frame == 2) ? 1 : frame;
    int frameindex = (framerollIndex + wrollIndex) % block;
    frameindex = (frameindex < 0) ? (frameindex + 3) % block : frameindex;
    int hindex = h % block;
//    return pattern[frameindex][hindex % block];
    int result = pattern[hindex][frameindex];
    return result;
  }

  public static BufferedImage getFRCBufferedImage(BufferedImage original,
                                                  int frame) {
    WritableRaster raster = original.getRaster();
    BufferedImage result = ATIFRCPatternGenerator.get6BitBufferedImage(original);
    WritableRaster raster2 = result.getRaster();
    int w = original.getWidth();
    int h = original.getHeight();
    int[] pixles = new int[3];
    int[] pixles2 = new int[3];

    for (int x = 0; x < h; x++) {
      for (int y = 0; y < w; y++) {
        raster.getPixel(y, x, pixles);
        raster2.getPixel(y, x, pixles2);

        for (int m = 0; m < 3; m++) {
          int index = pixles[m] % 4;
          if (index == 0) {
            continue;
          }
          int frc = getFRC(x, y, frame, index) * 4;
          pixles2[m] += frc;
          pixles2[m] = (pixles2[m] > 255) ? 255 : pixles2[m];
        }

//        int mean = pixles[0] / 4 + pixles[1] / 2 + pixles[2] / 4;
//        int mean = pixles[1];
//        int index = mean % 4;
//        if (index == 0) {
//          continue;
//        }
//
//        int frc = getFRC(x, y, frame, index) * 4;
//        raster2.getPixel(y, x, pixles);
//        for (int m = 0; m < 3; m++) {
//          pixles[m] += frc;
//          pixles[m] = (pixles[m] > 255) ? 255 : pixles[m];
//        }
        raster2.setPixel(y, x, pixles2);
      }
    }

    return result;
  }

}
