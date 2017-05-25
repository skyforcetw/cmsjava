package shu.cms.profile;

import java.io.*;

import java.awt.image.*;

import shu.cms.hvs.cam.*;
import shu.cms.image.*;
import shu.image.*;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 套用兩個ICC Profile進行複雜影像的轉換
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class ProfileConverter {

  /**
   *
   * @param sourceImage BufferedImage
   * @param sourceColor ProfileColorSpace
   * @param destinationColor ProfileColorSpace
   * @return BufferedImage
   * @deprecated
   */
  public BufferedImage convertByXYZ(BufferedImage sourceImage,
                                    ProfileColorSpace sourceColor,
                                    ProfileColorSpace destinationColor) {
    DeviceIndependentImage sourceDI = DeviceIndependentImage.getInstance(
        sourceImage,
        sourceColor);
    ImageSkeleton destImage = ImageSkeleton.getInstance(ImageUtils.
        cloneBufferedImage(sourceImage));

    int w = sourceDI.getWidth();
    int h = sourceDI.getHeight();
    double[] XYZValues = new double[3];

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        sourceDI.getXYZValues(x, y, XYZValues);
        double[] RGBValues = destinationColor.fromCIEXYZValues(XYZValues);
        destImage.setPixel(x, y, RGBValues);
      }
    }

    return destImage.getBufferedImage();
  }

  public final static BufferedImage convertByLab(String imageFilename,
                                                 String
                                                 sourceICCProfileFilename,
                                                 String destICCProfileFilename) throws
      IOException {
    Profile sourceProfile = iccessAdapter.loadProfile(sourceICCProfileFilename);
    Profile destProfile = iccessAdapter.loadProfile(destICCProfileFilename);

    ProfileColorSpace sourcePCS = ProfileColorSpace.Instance.get(sourceProfile,
        "");
    ProfileColorSpace destPCS = ProfileColorSpace.Instance.get(destProfile, "");
    BufferedImage image = ImageUtils.loadImage(imageFilename);

    return convertByLab(image, sourcePCS, destPCS);
  }

  public final static BufferedImage convertByLab(BufferedImage sourceImage,
                                                 ProfileColorSpace sourceColor,
                                                 ProfileColorSpace
                                                 destinationColor) {
    DeviceIndependentImage sourceDI = DeviceIndependentImage.getInstance(
        sourceImage,
        sourceColor, CAMConst.CATType.Bradford);
//    DeviceIndependentImage destDI = new

    ImageSkeleton destImage = ImageSkeleton.getInstance(ImageUtils.
        cloneBufferedImage(sourceImage));

//    double[] srcWhite = sourceColor.getReferenceWhite().getValues();
//    double[] destWhite = destinationColor.getReferenceWhite().getValues();

    int w = sourceDI.getWidth();
    int h = sourceDI.getHeight();
    double[] XYZValues = new double[3];

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        sourceDI.getPCSXYZValues(x, y, XYZValues);
//        sourceDI.getXYZValues(x, y, XYZValues);
//        double[] Lab = CIEXYZ.XYZ2LabValues(XYZValues, srcWhite);
//        double[] destXYZ = CIEXYZ.Lab2XYZValues(Lab, destWhite);
        double[] RGBValues = destinationColor.fromPCSCIEXYZValues(XYZValues);
//        double[] RGBValues = destinationColor.fromCIEXYZValues(destXYZ);
        destImage.setPixel(x, y, RGBValues);
      }
    }

    return destImage.getBufferedImage();
  }

  /**
   *
   * @param sourceImage BufferedImage
   * @param sourceColor ProfileColorSpace
   * @param destinationColor ProfileColorSpace
   * @return BufferedImage
   * @deprecated
   */
  public BufferedImage convert(BufferedImage sourceImage,
                               ProfileColorSpace sourceColor,
                               ProfileColorSpace destinationColor) {
    DeviceIndependentImage sourceDI = DeviceIndependentImage.getInstance(
        sourceImage,
        sourceColor);
    ImageSkeleton destImage = ImageSkeleton.getInstance(ImageUtils.
        cloneBufferedImage(sourceImage));

    int w = sourceDI.getWidth();
    int h = sourceDI.getHeight();
    double[] XYZValues = new double[3];

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        sourceDI.getXYZValues(x, y, XYZValues);
        double[] RGBValues = destinationColor.fromCIEXYZValues(XYZValues);
        destImage.setPixel(x, y, RGBValues);
      }
    }

    return destImage.getBufferedImage();
  }

  public static void main(String[] args) throws IOException {

    String imageFilename = "Camera Files/D200_2/Psychophysics/F12/_DSC2960.tif";
    String sourceICCProfile =
        "Profile/Camera/jCMS_D200Raw_F12 CCSG_PolyBy32_20070605 Lab-Bradford.icc";
    String destICCProfile =
        "Profile/Monitor/jCMS_EIZO_CG221_2_i1pro Dark D65 Patch729_PolyBY_20_20070605 Lab-Bradford.icc";
    BufferedImage image = convertByLab(imageFilename, sourceICCProfile,
                                       destICCProfile);
    ImageUtils.storeJPEGImage("test.jpg", image);
  }
}
