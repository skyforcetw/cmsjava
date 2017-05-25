package shu.cms.wb;

import java.io.*;

import java.awt.image.*;

import shu.cms.hvs.cam.*;
import shu.cms.image.*;
import shu.cms.profile.*;
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
public class GWT
    extends WhiteBalance {

  public GWT() {

  }

  public GWT(Profile[] profiles) {
    super(profiles);
  }

  public static void main(String[] args) {
    BufferedImage image = null;
    try {
//      image = ImageSkeleton.openJPEGImage("1L.jpg");
      image = ImageUtils.loadImage(
          "Reference Files/RGB Reference Images/DeltaE_8bit_gamma2.2.tif");

      GWT gwt = new GWT();

      BufferedImage rgb = gwt.RGBWhiteBalance(image);
      ImageUtils.storeJPEGImage("rgb.jpg", rgb);
      BufferedImage cat = gwt.CIEWhiteBalanceByCAT(image,
          CAMConst.CATType.Bradford);
      ImageUtils.storeJPEGImage("cat.jpg", cat);

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  /**
   * processRGBCATCoefficients
   *
   * @param image BufferedImage
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

  protected double[] processWhiteBalanceCoefficients(ImageSkeleton
      imageSkeleton) {
    int width = imageSkeleton.getWidth();
    int height = imageSkeleton.getHeight();
    double p1 = 0, p2 = 0, p3 = 0;
    double[] pixel = new double[3];

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        pixel = imageSkeleton.getPixel(i, j, pixel);
        p1 += pixel[0];
        p2 += pixel[1];
        p3 += pixel[2];
      }
    }
    int count = width * height;
    p1 /= count;
    p2 /= count;
    p3 /= count;

    return new double[] {
        p1, p2, p3};
  }
}
