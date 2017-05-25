package shu.cms.image;

import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.media.jai.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import org.apache.commons.io.*;
import com.sun.image.codec.jpeg.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 與影像相關的公用函式
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class ImageUtils {
  public final static BufferedImage cloneBufferedImage(BufferedImage
      original) {
    BufferedImage newImage = new BufferedImage(original.getColorModel(),
                                               original.copyData(null), false, null);
    return newImage;
  }

  public final static boolean equals(BufferedImage imageA, BufferedImage imageB) {
    if (imageA.getWidth() != imageB.getWidth() ||
        imageA.getHeight() != imageB.getHeight()) {
      return false;
//      throw new IllegalArgumentException();
    }

    int width = imageA.getWidth();
    int height = imageA.getHeight();
    WritableRaster rasterA = imageA.getRaster();
    WritableRaster rasterB = imageB.getRaster();

    int[] pixelA = new int[3];
    int[] pixelB = new int[3];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        rasterA.getPixel(x, y, pixelA);
        rasterB.getPixel(x, y, pixelB);
        if (!Arrays.equals(pixelA, pixelB)) {
          return false;
        }
      }
    }

    return true;
  }

  public final static BufferedImage loadImage(String filename) throws
      IOException {
//    PlanarImage planar = JAI.create("FileLoad", filename);
//    return planar.getAsBufferedImage();
    BufferedImage image = ImageIO.read(new File(filename));
    return image;
  }

  public final static double[][] normailizeTo255(double[][] original) {
    int height = original.length;
    int width = original[0].length;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        original[y][x] *= 255;
        original[y][x] = original[y][x] > 255 ? 255 : original[y][x];
      }
    }

    return original;
  }

  public final static BufferedImage toGrayBufferedImage(double[][] imageData,
      double gamma) {
    int height = imageData.length;
    int width = imageData[0].length;
    BufferedImage newImage = new BufferedImage(width, height,
                                               BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        double v = imageData[y][x];
        v = Math.pow(v / 255, 1 / gamma) * 255;
        newImage.getRaster().setPixel(x, y, new double[] {v, v, v});
      }
    }

    return newImage;
  }

  /*public final static WritableRaster toGrayWritableRaster(int[][] imageData) {
    WritableRaster raster = WritableRaster.createBandedRaster(DataBuffer.
        TYPE_INT, 100, 100, 1, null);

    return raster;
     }*/

  public final static BufferedImage toGrayBufferedImage(int[][] imageData) {
    int height = imageData.length;
    int width = imageData[0].length;
    BufferedImage newImage = new BufferedImage(width, height,
                                               BufferedImage.TYPE_BYTE_GRAY);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int v = imageData[y][x];
        //newImage.getRaster().setPixel(x, y, new double[] {v, v, v});
        newImage.getRaster().setPixel(x, y, new int[] {v});
      }
    }
    return newImage;
  }

  public static void main(String[] args) {
    int[][] img = new int[100][100];
    BufferedImage bimg = toGrayBufferedImage(img);
    try {
      storeTIFFImage("123.tif", bimg);
    }
    catch (IOException ex) {
    }
  }

  public final static void storeJPEGImage(String filename,
                                          BufferedImage image) throws
      IOException {
    FileOutputStream fs = new FileOutputStream(filename);
    JPEGEncodeParam jpe = JPEGCodec.getDefaultJPEGEncodeParam(image);
    jpe.setQuality(1.0f, false);
    JPEGImageEncoder o = JPEGCodec.createJPEGEncoder(fs, jpe);
    o.encode(image);
    fs.flush();
    fs.close();
  }

  public final static void storeTIFFImage(String filename,
                                          BufferedImage image) throws
      IOException {
    JAI.create("filestore", image, filename, "TIFF");
  }

  public final static void storeBMPFImage(String filename,
                                          BufferedImage image) throws
      IOException {
    JAI.create("filestore", image, filename, "BMP");
  }

  // This method returns true if the specified image has transparent pixels
  public static boolean hasAlpha(Image image) {
    // If buffered image, the color model is readily available
    if (image instanceof BufferedImage) {
      BufferedImage bimage = (BufferedImage) image;
      return bimage.getColorModel().hasAlpha();
    }

    // Use a pixel grabber to retrieve the image's color model;
    // grabbing a single pixel is usually sufficient
    PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
    try {
      pg.grabPixels();
    }
    catch (InterruptedException e) {
    }

    // Get the image's color model
    ColorModel cm = pg.getColorModel();
    return cm.hasAlpha();
  }

  public static BufferedImage toBufferedImage(Image image) {
    if (image instanceof BufferedImage) {
      return (BufferedImage) image;
    }

    // This code ensures that all the pixels in the image are loaded
    image = new ImageIcon(image).getImage();

    // Determine if the image has transparent pixels; for this method's
    // implementation, see e661 Determining If an Image Has Transparent Pixels
    boolean hasAlpha = hasAlpha(image);

    // Create a buffered image with a format that's compatible with the screen
    BufferedImage bimage = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try {
      // Determine the type of transparency of the new buffered image
      int transparency = Transparency.OPAQUE;
      if (hasAlpha) {
        transparency = Transparency.BITMASK;
      }

      // Create the buffered image
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();
      bimage = gc.createCompatibleImage(
          image.getWidth(null), image.getHeight(null), transparency);
    }
    catch (HeadlessException e) {
      // The system does not have a screen
    }

    if (bimage == null) {
      // Create a buffered image using the default color model
      int type = BufferedImage.TYPE_INT_RGB;
      if (hasAlpha) {
        type = BufferedImage.TYPE_INT_ARGB;
      }
      bimage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                                 type);
    }

    // Copy image to buffered image
    Graphics g = bimage.createGraphics();

    // Paint the image onto the buffered image
    g.drawImage(image, 0, 0, null);
    g.dispose();

    return bimage;
  }

  public static Collection<File> listImageFiles(String dirname) {
    File dir = new File(dirname);
    return FileUtils.listFiles(dir, new String[] {"bmp", "tif", "gif", "jpg",
                               "png"}, false);

  }
}
