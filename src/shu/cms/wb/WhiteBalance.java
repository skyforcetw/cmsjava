package shu.cms.wb;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import java.awt.image.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.cms.image.*;
import shu.cms.profile.*;
import shu.util.log.*;

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
public abstract class WhiteBalance {
  protected Map<Illuminant, Profile> profileMap;

  protected void initProfileMap(Profile[] profiles) {
    profileMap = new HashMap<Illuminant, Profile> ();
    for (Profile profile : profiles) {
//      Illuminant illuminant = profile.getCIEIlluminant(profile.
//          getViewingConditions().illuminantType.standardIlluminant);
      Illuminant illuminant = Illuminant.getCIEIlluminant(profile.
          getMediaWhitePoint());
      profileMap.put(illuminant, profile);
    }
  }

  public WhiteBalance() {

  }

  public WhiteBalance(Profile[] profiles) {
    initProfileMap(profiles);
  }

  /**
   * 傳統RGB基礎的WB
   * @param image BufferedImage
   * @return BufferedImage
   */
  public final BufferedImage RGBWhiteBalance(BufferedImage image) {
    BufferedImage cloneImage = ImageUtils.cloneBufferedImage(image);

    ImageSkeleton imageSkeleton = ImageSkeleton.getInstance(cloneImage);
    //計算wb係數
    double[] whiteBalanceCoefficients = processWhiteBalanceCoefficients(
        imageSkeleton);
    //計算色適應係數
    double[] CATCoefficients = processRGBCATCoefficients(
        whiteBalanceCoefficients);
    //計算係數
    return RGBChromaticAdaptationTransform(CATCoefficients, cloneImage);
  }

  /**
   * 計畫書中所提出的CIE色度學基礎之WB
   * @param image BufferedImage
   * @return BufferedImage
   */
  public final BufferedImage CIEWhiteBalance(BufferedImage image) {
    return null;
  }

  /**
   * 完全依照CIE色度學的基礎的WB
   * @param image BufferedImage
   * @param catType CATType
   * @return BufferedImage
   */
  public BufferedImage CIEWhiteBalanceByLMS(BufferedImage image,
                                            CAMConst.CATType catType) {
    //取得設備D65下之Profile
    Profile D65Profile = getDeviceProfile(Illuminant.D65);
    //取得D65下之LMS影像
    DeviceIndependentImage DIImage = DeviceIndependentImage.getInstance(image,
        ProfileColorSpace.Instance.get(D65Profile, ""), catType);
    ImageSkeleton imageSkeleton = ImageSkeleton.getInstance(DIImage, true);
    //計算wb係數
    double[] whiteBalanceCoefficients = processWhiteBalanceCoefficients(
        imageSkeleton);
    //計算白點XYZ
    CIEXYZ wp = processEstimativeWhitePoint(whiteBalanceCoefficients, catType);
    //計算最接近之光源
    Illuminant ls = processLightSource(wp);
    //計算光源下Profile
    Profile profile = getDeviceProfile(ls);

    DeviceIndependentImage realDIImage = DeviceIndependentImage.getInstance(
        image, ProfileColorSpace.Instance.get(profile, ""), catType);
    return null;
  }

  /**
   * 完全依照CIE色度學的基礎的WB
   * @param image BufferedImage
   * @param catType CATType
   * @return BufferedImage
   */
  public BufferedImage CIEWhiteBalanceByCAT(BufferedImage image,
                                            CAMConst.CATType catType) {
    BufferedImage cloneImage = ImageUtils.cloneBufferedImage(image);
    //取得設備D65下之Profile
    Profile D65Profile = getDeviceProfile(Illuminant.D65);
    //取得D65下之lms影像
    DeviceIndependentImage lmsImage = DeviceIndependentImage.getInstance(
        cloneImage, ProfileColorSpace.Instance.get(D65Profile, ""), catType);
    ImageSkeleton imageSkeleton = ImageSkeleton.getInstance(lmsImage, true);
    //計算wb係數
    double[] whiteBalanceCoefficients = processWhiteBalanceCoefficients(
        imageSkeleton);
    //計算白點XYZ
    CIEXYZ wp = processEstimativeWhitePoint(whiteBalanceCoefficients, catType);

    //計算最接近之光源
    Illuminant ls = processLightSource(wp);

    //計算光源下Profile
    Profile profile = getDeviceProfile(ls);

    DeviceIndependentImage DIImage = DeviceIndependentImage.getInstance(
        cloneImage, ProfileColorSpace.Instance.get(profile, ""), catType);
//    XYZChromaticAdaptationTransform(DIImage, ls.getSpectra().getXYZ(), catType);
//    XYZChromaticAdaptationTransform(DIImage, wp, catType);
    XYZChromaticAdaptationTransformThread(DIImage, wp, catType);
    return DIImage.getBufferedImage();
  }

  public static void main(String[] args) {
    BufferedImage image = null;
    String dir = "Image/WhiteBalance/";
    try {
      image = ImageUtils.loadImage("Image/walt.jpg");

      MaxRGB maxRGB = new MaxRGB();

      BufferedImage rgb = maxRGB.RGBWhiteBalance(image);
      ImageUtils.storeJPEGImage(dir + "MaxRGB-RGB.jpg", rgb);
      BufferedImage cat = maxRGB.CIEWhiteBalanceByCAT(image,
          CAMConst.CATType.Bradford);
      ImageUtils.storeJPEGImage(dir + "MaxRGB-CAT.jpg", cat);

      GWT gwt = new GWT();
      rgb = gwt.RGBWhiteBalance(image);
      ImageUtils.storeJPEGImage(dir + "GWT-RGB.jpg", rgb);
      cat = gwt.CIEWhiteBalanceByCAT(image,
                                     CAMConst.CATType.Bradford);
      ImageUtils.storeJPEGImage(dir + "GWT-CAT.jpg", cat);

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  protected final static CIEXYZ processEstimativeWhitePoint(double[]
      whiteBalanceCoefficients, CAMConst.CATType catType) {
    LMS lms = new LMS(whiteBalanceCoefficients);
    return lms.toXYZ(lms, catType);
  }

  protected final static Illuminant processLightSource(CIEXYZ whitePoint) {
    return Illuminant.getCIEIlluminant(whitePoint);
  }

  protected final Profile getDeviceProfile(Illuminant lightSource) {
    if (profileMap == null) {
      return null;
    }
    else {
      Profile profile = profileMap.get(lightSource);
      if (profile == null) {
        profile = profileMap.get(Illuminant.D65);
      }
      return profile;
    }
  }

  protected abstract double[] processWhiteBalanceCoefficients(ImageSkeleton
      imageSkeleton);

  protected abstract double[] processRGBCATCoefficients(double[]
      whiteBalanceCoefficients);

  protected final static void XYZChromaticAdaptationTransform(
      DeviceIndependentImage XYZImage, CIEXYZ sourceWhite,
      CAMConst.CATType catType) {
    CIEXYZ D65XYZ = Illuminant.D65.getNormalizeXYZ();
//    D65XYZ.normalizeY();
    sourceWhite.normalizeY();
    ChromaticAdaptation chromaticAdaptation = new ChromaticAdaptation(
        sourceWhite, D65XYZ, catType);
//    double[][] adaptationMatrix = ChromaticAdaptation.getAdaptationMatrix(
//        sourceWhite, D65XYZ, catType);

    int width = XYZImage.getWidth();
    int height = XYZImage.getHeight();
    double[] XYZValues = new double[3];

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        XYZValues = XYZImage.getXYZValues(i, j, XYZValues);
        XYZValues = chromaticAdaptation.getDestinationColor(XYZValues);
//        XYZValues = ChromaticAdaptation.adaptation(XYZValues, adaptationMatrix);
        XYZImage.setXYZValues(i, j, XYZValues);
      }
    }
  }

  /**
   * 以多執行緒進行色適應轉換,配合HT或雙核心將可提升運算速度
   * @param XYZImage DeviceIndependentImage
   * @param sourceWhite CIEXYZ
   * @param catType CATType
   */
  protected final static void XYZChromaticAdaptationTransformThread(
      DeviceIndependentImage XYZImage, CIEXYZ sourceWhite,
      CAMConst.CATType catType) {
    CIEXYZ D65XYZ = Illuminant.D65.getNormalizeXYZ();
    sourceWhite.normalizeY();
    double[][] adaptationMatrix = ChromaticAdaptation.getAdaptationMatrix(
        sourceWhite, D65XYZ, catType);

    int width = XYZImage.getWidth();
    int height = XYZImage.getHeight();

    int heightDivide = height / XYZCATThread_COUNT;
    int heightStart = 0;

    //==========================================================================
    //設定執行緒
    //==========================================================================
    ExecutorService executorService = Executors.newFixedThreadPool(
        XYZCATThread_COUNT);

    Future<?> [] futureArray = new Future<?>[XYZCATThread_COUNT];
    for (int x = 0; x < XYZCATThread_COUNT; x++) {
      int heightEnd = heightStart + heightDivide;
      if ( (height - heightEnd) < heightDivide) {
        heightEnd = height;
      }
      XYZCATTask task = new XYZCATTask(0, width,
                                       heightStart,
                                       heightEnd,
                                       XYZImage,
                                       adaptationMatrix);
      futureArray[x] = executorService.submit(task);
      heightStart += heightDivide;
    }
    try {
      for (int x = 0; x < XYZCATThread_COUNT; x++) {
        futureArray[x].get();
      }
    }
    catch (ExecutionException ex) {
      Logger.log.error("", ex);
    }
    catch (InterruptedException ex) {
      Logger.log.error("", ex);
    }
    finally {
      //關閉執行緒池
      executorService.shutdown();
    }
  }

  protected final static int XYZCATThread_COUNT = 4;

  protected static class XYZCATTask
      implements Callable<Object> {
    int xStart;
    int xEnd;
    int yStart;
    int yEnd;
    DeviceIndependentImage XYZImage;
    double[][] adaptationMatrix;

    public XYZCATTask(int xStart, int xEnd, int yStart, int yEnd,
                      DeviceIndependentImage XYZImage,
                      double[][] adaptationMatrix) {
      this.xStart = xStart;
      this.xEnd = xEnd;
      this.yStart = yStart;
      this.yEnd = yEnd;
      this.XYZImage = XYZImage;
      this.adaptationMatrix = adaptationMatrix;
    }

    public Object call() {
      double[] XYZValues = new double[3];
      for (int x = xStart; x < xEnd; x++) {
        for (int y = yStart; y < yEnd; y++) {
          XYZValues = XYZImage.getXYZValues(x, y, XYZValues);
          XYZValues = ChromaticAdaptation.adaptation(XYZValues,
              adaptationMatrix);
          XYZImage.setXYZValues(x, y, XYZValues);
        }
      }
      return null;
    }
  }

  protected final static void LMSChromaticAdaptationTransform(double[]
      coefficients, DeviceIndependentImage lmsImage) {
    int width = lmsImage.getWidth();
    int height = lmsImage.getHeight();
    double[] pixel = new double[3];

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        pixel = lmsImage.getLMSValues(i, j, pixel);
        pixel[0] *= coefficients[0];
        pixel[1] *= coefficients[1];
        pixel[2] *= coefficients[2];

        lmsImage.setLMSValues(i, j, pixel);
      }
    }
  }

  protected final static BufferedImage RGBChromaticAdaptationTransform(double[]
      coefficients,
      BufferedImage image) {
//    BufferedImage newImage = ImageSkeleton.cloneBufferedImage(image);
    int width = image.getWidth();
    int height = image.getHeight();

    WritableRaster raster = image.getRaster();
    double[] pixel = new double[3];

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        pixel = raster.getPixel(i, j, pixel);
        pixel[0] *= coefficients[0];
        pixel[1] *= coefficients[1];
        pixel[2] *= coefficients[2];

        //====================================================================
        //超出255時會跳回0,所以要加以限制其值
        //====================================================================
        if (pixel[0] > 255) {
          pixel[0] = 255;
        }
        if (pixel[1] > 255) {
          pixel[1] = 255;
        }
        if (pixel[2] > 255) {
          pixel[2] = 255;
        }
        //====================================================================

        raster.setPixel(i, j, pixel);
      }
    }

    return image;
  }
}
