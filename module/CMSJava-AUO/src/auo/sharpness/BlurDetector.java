package auo.sharpness;

import shu.image.ImageUtils;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.independ.CIEXYZ;
import shu.cms.colorspace.independ.CIELab;
import shu.math.*;
import shu.math.array.*;

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
public class BlurDetector {

  static double[] get9Luminance(int x, int y, WritableRaster raster) {
    int[] rgbValues = new int[3];
    double[] lumiArray = new double[9];
    lumiArray[0] = getLightness(raster.getPixel(x - 1, y - 1, rgbValues));
    lumiArray[1] = getLightness(raster.getPixel(x, y - 1, rgbValues));
    lumiArray[2] = getLightness(raster.getPixel(x + 1, y - 1, rgbValues));
    lumiArray[3] = getLightness(raster.getPixel(x - 1, y, rgbValues));
    lumiArray[4] = getLightness(raster.getPixel(x, y, rgbValues));
    lumiArray[5] = getLightness(raster.getPixel(x + 1, y, rgbValues));
    lumiArray[6] = getLightness(raster.getPixel(x - 1, y + 1, rgbValues));
    lumiArray[7] = getLightness(raster.getPixel(x, y + 1, rgbValues));
    lumiArray[8] = getLightness(raster.getPixel(x + 1, y + 1, rgbValues));
    return lumiArray;
  }

  public static double getLightness(int[] rgbValues) {
    RGB rgb = new RGB(RGB.ColorSpace.sRGB, rgbValues);
    CIEXYZ XYZ = rgb.toXYZ();
    CIELab Lab = new CIELab(XYZ, WhiteXYZ);
    return Lab.L;
  }

  static double[][] getLumiImage(BufferedImage img) {
    int w = img.getWidth();
    int h = img.getHeight();
    WritableRaster raster = img.getRaster();
    double[][] lumiImage = new double[w][h];
    int[] rgbValues = new int[3];
    double[] rgbValuesD = new double[3];
    double[][] toXYZMatrix = RGB.ColorSpace.sRGB.toXYZMatrix;
    double[] toY = toXYZMatrix[1];
    double r22 = 2.2;

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        raster.getPixel(x, y, rgbValues);
        rgbValuesD[0] = Math.pow(rgbValues[0] / 255., r22);
        rgbValuesD[1] = Math.pow(rgbValues[1] / 255., r22);
        rgbValuesD[2] = Math.pow(rgbValues[2] / 255., r22);

        double Y = DoubleArray.times(toY, rgbValuesD);
        lumiImage[x][y] = Y;
//        RGB rgb = new RGB(RGB.ColorSpace.sRGB, rgbValues);
//        CIEXYZ XYZ = rgb.toXYZ();
//        lumiImage[x][y] = XYZ.Y;
      }
    }
    return lumiImage;
  }

  static double[] get9Luminance(int x, int y, double[][] lumiImage) {
    double[] lumiArray = new double[9];
    lumiArray[0] = lumiImage[x - 1][y - 1];
    lumiArray[1] = lumiImage[x][y - 1];
    lumiArray[2] = lumiImage[x + 1][y - 1];
    lumiArray[3] = lumiImage[x - 1][y];
    lumiArray[4] = lumiImage[x][y];
    lumiArray[5] = lumiImage[x + 1][y];
    lumiArray[6] = lumiImage[x - 1][y + 1];
    lumiArray[7] = lumiImage[x][y + 1];
    lumiArray[8] = lumiImage[x + 1][y + 1];
    return lumiArray;

  }

  static CIEXYZ WhiteXYZ = RGB.ColorSpace.sRGB.getReferenceWhiteXYZ();
  static BufferedImage getBlueImage(String filename) {
    try {
      BufferedImage img = ImageUtils.loadImage(filename);
      BufferedImage result = ImageUtils.cloneBufferedImage(img);
      int w = img.getWidth();
      int h = img.getHeight();
      WritableRaster raster = result.getRaster();
      int[] rgbValues = new int[3];
      double[][] lumiImage = getLumiImage(img);

      for (int x = 1; x < w - 1; x++) {
        for (int y = 1; y < h - 1; y++) {
          double[] lumiArray = get9Luminance(x, y, lumiImage);
          double maxLumi = Maths.max(lumiArray);
          double minLumi = Maths.min(lumiArray);
          maxLumi = Math.pow(maxLumi, 1. / 2.2);
          minLumi = Math.pow(minLumi, 1. / 2.2);
          double deltaLumi = maxLumi - minLumi;
          double dLumiInr22 = Math.pow(deltaLumi, 1 / 2.2);
          double dLumiIn255 = dLumiInr22 * 255;
          rgbValues[0] = rgbValues[1] = rgbValues[2] = (int) Math.round(
              dLumiIn255);

          raster.setPixel(x, y, rgbValues);
        }
      }
      lumiImage = null;
      return result;
    }
    catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }

  }

  public static void main(String[] args) {
    String dir = "D:\\ณnล้\\nobody zone\\Pattern\\Sharpness Pattern\\From Iting";
    String output =
        "D:\\ณnล้\\nobody zone\\Pattern\\Sharpness Pattern\\From Iting\\Blue Image";
    try {
      for (File f : new File(dir).listFiles()) {
        if (f.isFile()) {
          BufferedImage img = getBlueImage(f.getAbsolutePath());
          String filename = f.getName();
          int dotIndex = filename.indexOf(".");
          String nameOnly = filename.substring(0, dotIndex);
          String newfilename = nameOnly + ".png";
          ImageUtils.storePNGImage(output + "/" + newfilename, img);
          img = null;
        }
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

  }
}
