package shu.cms.hvs.cam.icam;

import java.io.*;
import javax.imageio.*;

import java.awt.image.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.cms.image.*;
import shu.cms.profile.*;
import shu.math.*;

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
public abstract class iCAMFramework {
  public static final String iCAM_IMAGE_DIR = "Image/iCAM";

  protected DeviceIndependentImage DIImage;
  protected int imageWidth;
  protected int imageHeight;

  public iCAMFramework(String filename, ProfileColorSpace pcs) throws
      IOException {
    BufferedImage image = ImageIO.read(new File(filename));
    imageWidth = image.getWidth();
    imageHeight = image.getHeight();
    DIImage = DeviceIndependentImage.getInstance(image, pcs,
                                                 CAMConst.CATType.CAT02);
  }

  /**
   * Calculate the RGB of the blurred "whitepoint" image. First define a
   *  function to threshold the minimum of the image to 1. This means the
   *  darkest RGB adapting value is 1, and prevents division by zero, or
   * division by a small number.
   *
   * @param planeImage PlaneImage
   */
  protected final static void threshold(PlaneImage planeImage) {
    int height = planeImage.getHeight();
    int width = planeImage.getWidth();

    double[][] plane1 = planeImage.getPlaneImage(0);
    double[][] plane2 = planeImage.getPlaneImage(1);
    double[][] plane3 = planeImage.getPlaneImage(2);

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        plane1[y][x] = plane1[y][x] > 0.01 ? plane1[y][x] : 0.01;
        plane2[y][x] = plane2[y][x] > 0.01 ? plane2[y][x] : 0.01;
        plane3[y][x] = plane3[y][x] > 0.01 ? plane3[y][x] : 0.01;
      }
    }
  }

  protected final static double[] maxRGB(PlaneImage planeImage) {
    int height = planeImage.getHeight();
    int width = planeImage.getWidth();

    double[][] plane1 = planeImage.getPlaneImage(0);
    double[][] plane2 = planeImage.getPlaneImage(1);
    double[][] plane3 = planeImage.getPlaneImage(2);

    double[] max = new double[] {
        Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        max[0] = Math.max(max[0], plane1[y][x]);
        max[1] = Math.max(max[1], plane2[y][x]);
        max[2] = Math.max(max[2], plane3[y][x]);
      }
    }
    return max;
  }

  /**
   * 計算Rc/Gc/Bc
   * @param ch int
   * @param whiteImage PlaneImage
   * @param planeDIImage PlaneImage
   * @param Df double
   * @param expScale double
   * @param rgbWhitePoint LMS
   * @param maxRGB double[]
   * @return double[][]
   */
  protected final static double[][] calculateAdaptedSignals(int ch,
      PlaneImage whiteImage, PlaneImage planeDIImage, double Df,
      double expScale, LMS rgbWhitePoint,
      double[] maxRGB) {

    double numerator = Df * rgbWhitePoint.getValues()[ch];

    double[][] plane = whiteImage.getPlaneImage(ch);
    int height = whiteImage.getHeight();
    int width = whiteImage.getWidth();
    double[][] multiplicand = new double[height][width];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        multiplicand[y][x] = maxRGB[ch] *
            Math.pow(plane[y][x] / maxRGB[ch], expScale);
        multiplicand[y][x] = (numerator / multiplicand[y][x]) + 1 - Df;
      }
    }
    return times(multiplicand, planeDIImage.getPlaneImage(ch));
  }

  protected final static double[][] times(double[][] array1, double[][] array2) {
    int height = array1.length;
    int width = array1[0].length;

    if (height != array2.length || width != array2[0].length) {
      throw new IllegalArgumentException("height or width is not equal.");
    }

    double[][] result = new double[height][width];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        result[y][x] = array1[y][x] * array2[y][x];
      }
    }

    return result;
  }

  protected final static DeviceIndependentImage change2HuntCATType(
      DeviceIndependentImage image) {
    BufferedImage bi = image.getBufferedImage();
    DeviceIndependentImage hunt = DeviceIndependentImage.getInstance(bi,
        image.getProfileColorSpace(),
        CAMConst.CATType.Hunt);
    return hunt;
  }

  protected final static double[][] iptExp(double[][] yIm, double expScale,
                                           double[][] yLow) {
    int height = yIm.length;
    int width = yIm[0].length;
    if (height != yLow.length || width != yLow[0].length) {
      throw new IllegalArgumentException("height or width is not equal.");
    }
    double[][] iptExp = new double[height][width];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        iptExp[y][x] = Math.pow(yIm[y][x], expScale) / yLow[y][x];
      }
    }

    return iptExp;
  }

  protected final static void lmsImNL(PlaneImage lmsImage, double[][] iptExp) {
    int width = lmsImage.getWidth();
    int height = lmsImage.getHeight();
    if (height != iptExp.length || width != iptExp[0].length) {
      throw new IllegalArgumentException("height or width is not equal.");
    }
    for (int ch = 0; ch < 3; ch++) {
      double[][] plane = lmsImage.getPlaneImage(ch);
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          plane[y][x] = Math.pow(plane[y][x], iptExp[y][x] * .43);
        }
      }
      lmsImage.setPlaneImage(ch, plane);
    }
  }

  public final static BufferedImage getIBufferedImage(DeviceIndependentImage
      DIImage) {
    PlaneImage planeIPT = PlaneImage.getInstance(DIImage,
                                                 PlaneImage.Domain.IPTfromLMS);
    double[][] planeI = planeIPT.getPlaneImage(0);
    planeI = ImageUtils.normailizeTo255(planeI);
    BufferedImage gray = ImageUtils.toGrayBufferedImage(planeI, 1);
    return gray;
  }

  /**
   * 以DIImage取得IPT計算chroma影像
   * @param DIImage DeviceIndependentImage
   * @return BufferedImage
   */
  public final static BufferedImage getChromaBufferedImage(
      DeviceIndependentImage
      DIImage) {
    PlaneImage planeIPT = PlaneImage.getInstance(DIImage,
                                                 PlaneImage.Domain.IPTfromLMS);

    double[][] planeP = planeIPT.getPlaneImage(1);
    double[][] planeT = planeIPT.getPlaneImage(2);
    double[][] chromaImage = makeChromaImage(planeP, planeT);
    chromaImage = ImageUtils.normailizeTo255(chromaImage);
    BufferedImage chroma = ImageUtils.toGrayBufferedImage(chromaImage, 1);
    return chroma;
  }

  /**
   * 從IPT的P/T影像計算出chroma影像
   * @param planeP double[][]
   * @param planeT double[][]
   * @return double[][]
   */
  protected static double[][] makeChromaImage(double[][] planeP,
                                              double[][] planeT) {

    if (planeP.length != planeT.length ||
        planeP[0].length != planeT[0].length) {
      throw new IllegalArgumentException("width or height is not equal.");
    }
    int height = planeP.length;
    int width = planeP[0].length;
    double[][] chromaImage = new double[height][width];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        chromaImage[y][x] = Math.sqrt(Maths.sqr(planeP[y][x]) +
                                      Maths.sqr(planeT[y][x]));
      }
    }
    return chromaImage;
  }

  /**
   * 藉由XYZWhite作為參考白, 計算色適應後的影像
   * @param XYZWhite DeviceIndependentImage
   * @return DeviceIndependentImage
   */
  protected final DeviceIndependentImage chromaticAdaptation(
      DeviceIndependentImage XYZWhite) {
    ViewingConditions vc = new ViewingConditions(Illuminant.D65.getNormalizeXYZ(),
                                                 100, 0,
                                                 Surround.Average,
                                                 "iCAM default ViewingConditions");
    return chromaticAdaptation(XYZWhite, vc);
  }

  public DeviceIndependentImage chromaticAdaptationImage() {
    //計算參考白
    DeviceIndependentImage whiteImage = determineImageWhite(DIImage, 41);
    System.gc();
    //以參考白做色適應
    DeviceIndependentImage adaptImage = chromaticAdaptation(whiteImage);
    System.gc();
    return adaptImage;
  }

  /**
   * 藉由XYZWhite作為參考白, 計算色適應後的影像
   * @param XYZWhite DeviceIndependentImage
   * @param vc ViewingConditions
   * @return DeviceIndependentImage
   */
  protected final DeviceIndependentImage chromaticAdaptation(
      DeviceIndependentImage XYZWhite, ViewingConditions vc) {
    //==========================================================================
    // 設定色外貌參數
    //==========================================================================
    //Degree of Adaptation, 適應程度計算
    double Df = vc.d;
    //Adapting Whitepoint; 適應的目標白點
    CIEXYZ whitePoint = vc.white;
    whitePoint.normalize(NormalizeY.Normal1);
    //==========================================================================

    //將獨立色影像轉為LMS的平面影像
    PlaneImage rgbI = PlaneImage.getInstance(DIImage,
                                             PlaneImage.Domain.LMS);

    //Calculate RGB values for input image, blurred image, and whitepoint
    LMS rgbWhitePoint = LMS.fromXYZ(whitePoint, CAMConst.CATType.CAT02);
    PlaneImage planeRGBWhite = PlaneImage.getInstance(XYZWhite,
        PlaneImage.Domain.LMS);
    threshold(planeRGBWhite);

    //Calculate the Maximum R, G, B value
    double[] maxRGB = maxRGB(rgbI);

    //Adaptation Exponent
    double expScale = 1.0 / 3.0;

    //Calculate Adapted RGB Signals
    double[][] Rc = calculateAdaptedSignals(0, planeRGBWhite, rgbI,
                                            Df, expScale,
                                            rgbWhitePoint, maxRGB);
    double[][] Gc = calculateAdaptedSignals(1, planeRGBWhite, rgbI,
                                            Df, expScale,
                                            rgbWhitePoint, maxRGB);
    double[][] Bc = calculateAdaptedSignals(2, planeRGBWhite, rgbI,
                                            Df, expScale,
                                            rgbWhitePoint, maxRGB);
    PlaneImage RGBc = rgbI;

    RGBc.setPlaneImage(0, Rc);
    RGBc.setPlaneImage(1, Gc);
    RGBc.setPlaneImage(2, Bc);
    DeviceIndependentImage adaptImage = DeviceIndependentImage.
        cloneDeviceIndependentImage(DIImage);
    RGBc.restoreToDeviceIndependentImage(adaptImage);

    return adaptImage;
  }

  /**
   * 偵測影像的適應白
   * @param DIImage DeviceIndependentImage
   * @param kernelSize int
   * @return DeviceIndependentImage
   */
  protected final DeviceIndependentImage determineImageWhite(
      DeviceIndependentImage DIImage, int kernelSize) {
    //low-pass的filter kernal
    double[][] kern = kernel(kernelSize);
    //將獨立色影像轉成平面影像
    PlaneImage planeImage = PlaneImage.getInstance(DIImage,
        PlaneImage.Domain.XYZ);

    //XYZ分別施以low-pass
    double[][] xWhite = Matlab.conv2(planeImage.getPlaneImage(0), kern,
                                     Matlab.Conv2Type.Mathematica);
    double[][] yWhite = Matlab.conv2(planeImage.getPlaneImage(1), kern,
                                     Matlab.Conv2Type.Mathematica);
    double[][] zWhite = Matlab.conv2(planeImage.getPlaneImage(2), kern,
                                     Matlab.Conv2Type.Mathematica);

    //將low-pass運算後的影像設定回平面影像
    planeImage.setPlaneImage(0, xWhite);
    planeImage.setPlaneImage(1, yWhite);
    planeImage.setPlaneImage(2, zWhite);

    DeviceIndependentImage whiteImage = DeviceIndependentImage.
        cloneDeviceIndependentImage(DIImage);
    //平面影像轉回獨立色影像, 此獨立色影像即為計算白點所用
    planeImage.restoreToDeviceIndependentImage(whiteImage);
    return whiteImage;
  }

  protected abstract double[][] kernel(int kernelSize);

}
