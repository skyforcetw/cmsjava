package auo.cms.hsv.value.backup;

import shu.image.ImageUtils;
import shu.cms.colorspace.depend.*;
import shu.math.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.*;

import auo.cms.hsv.value.*;

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
public class PrecisionImageEvaluator {

  public static void evaluate(String[] args) {
//    PrecisionEvaluator.getV( (short) (1 * 4), (short) (1 * 4),
//                            (byte) 0, (short) 1020, 0, 0, 0);
//    String imageDirname =
//        "D:\\ณnล้\\nobody zone\\Pattern\\skyforce Pattern Collect\\Demo\\Duplicate";
//    BufferedImage pattern = patternGen(800, 600);
//    try {
//      ImageUtils.storeTIFFImage("pattern.tif", pattern);
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//    }

    String imageDirname =
        "D:\\ณnล้\\nobody zone\\Pattern\\skyforce Pattern Collect\\Color Adjust\\Evaluation Picture\\Artificial";
    byte offset = 25;

    try {
//      for (File file : new File(imageDirname).listFiles()) {
      File file = new File("pattern.tif");
      String filename = file.getName();

      if (file.isFile() &&
          (filename.indexOf("jpg") != -1 || filename.indexOf("tif") != -1 ||
           filename.indexOf("bmp") != -1)) {
        BufferedImage img = ImageUtils.loadImage(file.getAbsolutePath());

        BufferedImage floatImg = ImageUtils.cloneBufferedImage(img);
        floatImg = processImage(floatImg, false, offset, 0, 0, 0);
        ImageUtils.storeTIFFImage("hsv/" + "0" + filename, floatImg);

        for (int bit1 = 1; bit1 <= 1; bit1++) { //0~2
          for (int bit2 = 8; bit2 <= 8; bit2++) { //12~13
            for (int bit3 = 10; bit3 <= 10; bit3++) { //10~16

              int a = (29 - bit1 - bit2 - bit3);
              int b = (28 - bit2 - bit3);
              int c = (20 - bit3);
              String casestring = bit1 + " " + bit2 + " " + bit3 + "," + a +
                  " " + b + " " + c + " = " + (a + b + c);

              BufferedImage procImg = ImageUtils.cloneBufferedImage(img);
              procImg = processImage(procImg, false, offset, bit1, bit2,
                                     bit3);
              String tiffilename = "hsv/" + casestring + ".tif";
              System.out.println(tiffilename);
              ImageUtils.storeTIFFImage(tiffilename, procImg);
            } // bit3
          } //bit2
        } //bit1
      }

//      } //for loop

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

//    ImageUtils.loadImage("");
  }

  static BufferedImage processImage(BufferedImage img, boolean procHalf,
                                    byte offset,
                                    int bit1, int bit2, int bit3) {
    WritableRaster raster = img.getRaster();
    int w = raster.getWidth();
    int h = raster.getHeight();
    int[] pixels = new int[3];

    w = (procHalf) ? w / 2 : w;

    for (int x = 0; x < h; x++) {
      for (int y = 0; y < w; y++) {
        raster.getPixel(y, x, pixels);
        int maxIndex = Maths.maxIndex(pixels);
        int max = pixels[maxIndex]; //8bit
        int min = Maths.min(pixels); //8bit
        //1,8,11 => 32 ok
        int v = (ValuePrecisionEvaluator.getV( (short) (max * 4),
                                              (short) (min * 4),
                                              offset, (short) 1023, bit1, bit2,
                                              bit3, false, false)) /
            4;
        RGB rgb = new RGB(RGB.ColorSpace.sRGB, pixels);
        HSV hsv = new HSV(rgb);
        double doublev = v / 255. * 100;

        hsv.V = doublev;
        RGB rgb2 = hsv.toRGB();
        rgb2.clip();
        double[] values = rgb2.getValues(new double[3], RGB.MaxValue.Int8Bit);

        raster.setPixel(y, x, values);
      }
    }
    return img;
  }

  public static void main(String[] args) {
    BufferedImage img = generatePattern(1366, 5);
    try {
      ImageUtils.storeBMPImage("pattern.bmp", img);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static BufferedImage generatePattern(int width, int height) {
    BufferedImage img = new BufferedImage(width, height,
                                          BufferedImage.TYPE_INT_RGB);
    int[] hues = new int[] {
        0, 30, 60, 120, 180, 210, 240, 300, 330};
    int lines = hues.length + 1;
    int barHeight = height / (lines + 1);
    int grayWidth = width / 256;
    int blackHeight = barHeight / (lines + 2);
    Graphics g = img.getGraphics();
    for (int x = 0; x < 256; x++) {
      RGB rgb = new RGB(x, x, x);
      Color c = rgb.getColor();
      g.setColor(c);
      g.fillRect(x * grayWidth, blackHeight, grayWidth, barHeight);
    }

    int hueCount = hues.length;
    for (int hIndex = 0; hIndex < hueCount; hIndex++) {
      int hue = hues[hIndex];
      for (int x = 0; x < 256; x++) {
        HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {hue, 100, x / 2.55});
        RGB rgb = hsv.toRGB();
        Color c = rgb.getColor();
        g.setColor(c);
        g.fillRect(x * grayWidth,
                   blackHeight + (blackHeight + barHeight) * (hIndex + 1),
                   grayWidth, barHeight);
      }
    }

    return img;
  }
}
