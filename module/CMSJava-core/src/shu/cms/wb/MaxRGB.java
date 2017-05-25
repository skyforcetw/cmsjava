package shu.cms.wb;

import java.io.*;
import java.util.*;

import java.awt.image.*;

import shu.cms.hvs.cam.*;
import shu.cms.image.*;
import shu.image.*;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class MaxRGB
    extends WhiteBalance {
  public MaxRGB() {
  }

  /**
   * processRGBCATCoefficients
   *
   * @param whiteBalanceCoefficients double[]
   * @return double[]
   */
  protected double[] processRGBCATCoefficients(double[]
                                               whiteBalanceCoefficients) {
    double p1 = whiteBalanceCoefficients[0];
    double p2 = whiteBalanceCoefficients[1];
    double p3 = whiteBalanceCoefficients[2];
    double k = (p1 + p2 + p3) / 3;

    p1 = k / p1;
    p2 = k / p2;
    p3 = k / p3;

    return new double[] {
        p1, p2, p3};
  }

  /**
   * processWhiteBalanceCoefficients
   *
   * @param imageSkeleton ImageSkeleton
   * @return double[]
   */
  protected double[] processWhiteBalanceCoefficients(ImageSkeleton
      imageSkeleton) {
    int width = imageSkeleton.getWidth();
    int height = imageSkeleton.getHeight();
    double p1 = 0, p2 = 0, p3 = 0;
    double[] pixel = new double[3];

    short[][] histogram = new short[3][101];
    //因為是local variable,不會進行init,手動歸零
    Arrays.fill(histogram[0], (short) 0);
    Arrays.fill(histogram[1], (short) 0);
    Arrays.fill(histogram[2], (short) 0);

    //==========================================================================
    // 產生直方圖
    //==========================================================================
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        pixel = imageSkeleton.getPixel(i, j, pixel);

        p1 = pixel[0] * 100;
        p2 = pixel[1] * 100;
        p3 = pixel[2] * 100;
        p1 = p1 > 100 ? 100 : Math.round(p1);
        p2 = p2 > 100 ? 100 : Math.round(p2);
        p3 = p3 > 100 ? 100 : Math.round(p3);

        histogram[0][ (int) p1]++;
        histogram[1][ (int) p2]++;
        histogram[2][ (int) p3]++;
      }
    }
    //==========================================================================

    //要取樣的像素數量
    int pickCount = (int) (width * height * SAMPLE_RATE);
    double[] coefs = new double[3];

    //==========================================================================
    // 進行白點估測
    //==========================================================================
    for (int x = 0; x < histogram.length; x++) {
      int count = 0;
      for (int y = histogram[x].length - 1; y >= 0; y--) {
        count += histogram[x][y];
        if (count >= pickCount) {
          int total = 0;
          int pixelCount = 0;
          for (int z = histogram[x].length - 1; z >= y; z--) {
            total += z * histogram[x][z];
            pixelCount += histogram[x][z];
          }
          coefs[x] = (double) total / pixelCount;
          break;
        }
      }
    }
    //==========================================================================

    return coefs;
  }

  public final static double SAMPLE_RATE = 0.05;

  public static void main(String[] args) {
    BufferedImage image = null;
    try {
      image = ImageUtils.loadImage("image/d200.jpg");

      MaxRGB maxRGB = new MaxRGB();

      BufferedImage rgb = maxRGB.RGBWhiteBalance(image);
      ImageUtils.storeJPEGImage("rgb.jpg", rgb);
      BufferedImage cat = maxRGB.CIEWhiteBalanceByCAT(image,
          CAMConst.CATType.Bradford);
      ImageUtils.storeJPEGImage("cat.jpg", cat);

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

  }

}
