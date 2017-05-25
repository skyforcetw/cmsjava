package shu.cms.hvs.cam.icam;

import java.io.*;
import javax.imageio.*;

import java.awt.image.*;

import shu.cms.colorspace.depend.*;
import shu.cms.image.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.math.array.*;

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
public class BackgroundEffect
    extends iCAMFramework {
//  protected DeviceIndependentImage DIImage;
//  protected int imageWidth;
//  protected int imageHeight;

  public BackgroundEffect(String filename, ProfileColorSpace pcs) throws
      IOException {
    super(filename, pcs);
  }

  public DeviceIndependentImage backgroundEffect() throws IOException {
    System.gc();
    //kernel 41 for determine white
    //計算參考白
    DeviceIndependentImage whiteImage = determineImageWhite(DIImage, 41);
    System.gc();
    //以參考白做色適應
    DeviceIndependentImage adaptImage = chromaticAdaptation(whiteImage);
    System.gc();
    //將適應完的影像進ipt做BackgroundEffect運算
    DeviceIndependentImage IPT = convert2IPT(adaptImage);
    return IPT;
  }

  protected DeviceIndependentImage convert2IPT(DeviceIndependentImage
                                               adaptImage) {
    DeviceIndependentImage lmsImage = change2HuntCATType(adaptImage);
    PlaneImage planeLMSImage = PlaneImage.getInstance(lmsImage,
        PlaneImage.Domain.LMS);

    //Determine the exponents , based on the luminance of the input image
    double[][] iptKern = kernel(9);
    PlaneImage planeDIImage = PlaneImage.getInstance(DIImage,
        PlaneImage.Domain.XYZ);
    double[][] yIm = planeDIImage.getPlaneImage(1);
    double[][] yLow = Matlab.conv2(yIm, iptKern, Matlab.Conv2Type.Same);

    double expScale = 1.0 / 2.0;
    double[][] iptExp = iptExp(yIm, expScale, yLow);
    minThreshold(iptExp);
    lmsImNL(planeLMSImage, iptExp);

    DeviceIndependentImage IPTImage = DeviceIndependentImage.
        cloneDeviceIndependentImage(DIImage);
    planeLMSImage.restoreToDeviceIndependentImage(IPTImage);
    return IPTImage;
  }

  /**
   * 設定最小的門檻值為0.4
   * @param iptExp double[][]
   */
  protected static void minThreshold(double[][] iptExp) {
    int height = iptExp.length;
    int width = iptExp[0].length;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        iptExp[y][x] = iptExp[y][x] < 0.4 ? 0.4 : iptExp[y][x];
      }
    }
  }

  public static void main(String[] args) {
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGB.ColorSpace.
        sRGB);
//    try {
//      BackgroundEffect backgroundeffect = new BackgroundEffect(
//          "Image/iCAM/background.tif", pcs);
//      DeviceIndependentImage di = backgroundeffect.backgroundEffect();
//      BufferedImage bi = di.getBufferedImage();
//      ImageIO.write(bi, "tif", new File("BackgroundEffect.tif"));
//
//      BufferedImage I = getIBufferedImage(di);
//      ImageIO.write(I, "tif", new File("BackgroundEffect-I.tif"));
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//    }

    try {
      BackgroundEffect backgroundeffect = new BackgroundEffect(
          "portrait.jpg", pcs);
      DeviceIndependentImage di = backgroundeffect.chromaticAdaptationImage();
      BufferedImage bi = di.getBufferedImage();
      ImageIO.write(bi, "tif", new File("bg-ca.tif"));
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * 計算gaussian funciton的kernel
   * @param kernelSize int
   * @return double[][]
   */
  protected double[][] kernel(int kernelSize) {
    double[][] kern = table(kernelSize);
    double divisor = Mathematica.plus(Mathematica.flatten(kern));
    kern = DoubleArray.times(kern, 1 / divisor);
    return kern;
  }

  /**
   * 輔助計算kernal用
   * @param kernelSize int
   * @return double[][]
   */
  protected static double[][] table(int kernelSize) {
    int min = - (kernelSize - 1) / 2;
    int max = (kernelSize - 1) / 2;
    int size = (max - min) + 1;
    double[][] table = new double[size][size];
    double kernSqure = Maths.sqr(kernelSize);

    for (int m = 0; m < size; m++) {
      for (int n = 0; n < size; n++) {
        int x = m + min;
        int y = n + min;
        table[m][n] = Math.pow(Math.E,
                               - (Maths.sqr(x) + Maths.sqr(y)) / kernSqure);
      }
    }
    return table;
  }
}
