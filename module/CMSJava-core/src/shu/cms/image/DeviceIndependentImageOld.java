package shu.cms.image;

import java.io.*;

import java.awt.image.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.independ.IPT;
import shu.cms.hvs.cam.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.image.*;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 獨立色空間的影像
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class DeviceIndependentImageOld {
  protected BufferedImage bufferedImage;

  protected DoubleImage doubleImage;
  protected CAMConst.CATType catType;
  protected ProfileColorSpace profileColorSpace;
  protected ChromaticAdaptation chromaticAdaptation;
  protected double normal;

//  protected static class BufferedImageSource
//      extends DeviceIndependentImage {
//    public BufferedImageSource(BufferedImage image,
//                               ProfileColorSpace profileColorSpace,
//                               CAMConst.CATType catType) {
//      super(profileColorSpace, catType);
//
//      this.bufferedImage = image;
//      source = Source.BufferedImage;
//      //R+G+B的bit數量
//      int pixelSize = bufferedImage.getColorModel().getPixelSize();
//      this.normal = Math.pow(2, pixelSize / 3) - 1;
//
//      this.doubleImage = new DoubleImage(bufferedImage);
//
//    }
//
//  }

  public static enum Source {
    BufferedImage, CIEXYZ
  }

  protected Source source;

  public CAMConst.CATType getCatType() {
    return catType;
  }

  public static void main(String[] args) throws IOException {
    BufferedImage bi = ImageUtils.loadImage("img.jpg");
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGB.ColorSpace.
        sRGB);
    DeviceIndependentImageOld im = new DeviceIndependentImageOld(bi, pcs);
  }

  public DeviceIndependentImageOld(BufferedImage image,
                                   ProfileColorSpace profileColorSpace) {
    this(image, profileColorSpace, null);
  }

  public DeviceIndependentImageOld(BufferedImage image,
                                   ProfileColorSpace profileColorSpace,
                                   CAMConst.CATType catType) {
    this.bufferedImage = image;
    source = Source.BufferedImage;
    //R+G+B的bit數量
    int pixelSize = bufferedImage.getColorModel().getPixelSize();
    this.normal = Math.pow(2, pixelSize / 3) - 1;

    this.doubleImage = new DoubleImage(bufferedImage);
    this.catType = catType;
    this.profileColorSpace = profileColorSpace;
    if (catType != null && profileColorSpace != null) {
      this.chromaticAdaptation = ChromaticAdaptation.getInstanceAdaptToPCS(
          profileColorSpace.getReferenceWhite(), catType);
    }
  }

  public DeviceIndependentImageOld(ProfileColorSpace profileColorSpace,
                                   CAMConst.CATType catType) {
    this.catType = catType;
    this.profileColorSpace = profileColorSpace;
    if (catType != null && profileColorSpace != null) {
      this.chromaticAdaptation = ChromaticAdaptation.getInstanceAdaptToPCS(
          profileColorSpace.getReferenceWhite(), catType);
    }
  }

  public final static DeviceIndependentImageOld cloneDeviceIndependentImage(
      DeviceIndependentImageOld
      original) {
    BufferedImage cloneBufferedImage = ImageUtils.cloneBufferedImage(
        original.getBufferedImage());
    DeviceIndependentImageOld clone = new DeviceIndependentImageOld(
        cloneBufferedImage, original.profileColorSpace, original.catType);
    return clone;
  }

  public BufferedImage getBufferedImage() {
    this.doubleImage.getBufferedImage();
    return bufferedImage;
  }

  public void setXYZValues(int x, int y, double[] dArray) {
    double[] rgbValues = profileColorSpace.fromCIEXYZValues(dArray);

    rgbValues[0] = rgbValues[0] > 1 ? 1 : rgbValues[0];
    rgbValues[1] = rgbValues[1] > 1 ? 1 : rgbValues[1];
    rgbValues[2] = rgbValues[2] > 1 ? 1 : rgbValues[2];

    rgbValues[0] = rgbValues[0] < 0 ? 0 : rgbValues[0];
    rgbValues[1] = rgbValues[1] < 0 ? 0 : rgbValues[1];
    rgbValues[2] = rgbValues[2] < 0 ? 0 : rgbValues[2];

    Maths.undoNormalize(rgbValues, normal);
//    bufferedImage.getRaster().setPixel(x, y, rgbValues);
    doubleImage.setPixel(x, y, rgbValues);
  }

  protected double[][] LMS2XYZMatrix = null;

  public final void setLMSValues(int x, int y, double[] dArray) {
    if (catType == null) {
      throw new UnsupportedOperationException();
    }

    if (LMS2XYZMatrix == null) {
      LMS2XYZMatrix = CAMConst.getLMS2XYZMatrix(catType);
    }

    double[] XYZValues = LMS.toXYZValues(dArray, LMS2XYZMatrix);
    this.setXYZValues(x, y, XYZValues);
  }

  public double[] getRGBValues(int x, int y, double[] dArray) {
    if (dArray == null) {
      dArray = new double[3];
    }
    doubleImage.getPixel(x, y, dArray);
    return dArray;
  }

  public void setRGBValues(int x, int y, double[] dArray) {
    doubleImage.setPixel(x, y, dArray);
  }

  public double[] getXYZValues(int x, int y, double[] dArray) {
    if (dArray == null) {
      dArray = new double[3];
    }

    doubleImage.getPixel(x, y, dArray);
    Maths.normalize(dArray, normal);
    double[] XYZValues = profileColorSpace.toCIEXYZValues(dArray);
    System.arraycopy(XYZValues, 0, dArray, 0, dArray.length);
    return dArray;
  }

  /**
   *
   * @param x int
   * @param y int
   * @param dArray double[]
   * @return double[]
   */
  public double[] getPCSXYZValues(int x, int y, double[] dArray) {
    dArray = this.getXYZValues(x, y, dArray);
    double[] result = chromaticAdaptation.getDestinationColor(dArray);
    System.arraycopy(result, 0, dArray, 0, 3);
    return dArray;
  }

  /**
   *
   * @param x int
   * @param y int
   * @param dArray double[]
   */
  public final void setPCSXYZValues(int x, int y, double[] dArray) {
    double[] source = chromaticAdaptation.getSourceColor(dArray);
    this.setXYZValues(x, y, source);
  }

  protected double[][] XYZ2LMSMatrix = null;

  public final double[] getLMSValues(int x, int y, double[] dArray) {
    if (catType == null) {
      throw new UnsupportedOperationException();
    }

    if (XYZ2LMSMatrix == null) {
      XYZ2LMSMatrix = CAMConst.getXYZ2LMSMatrix(catType);
    }

    double[] XYZValues = getXYZValues(x, y, dArray);
    double[] LMSValues = LMS.fromXYZValues(XYZValues, XYZ2LMSMatrix);
    System.arraycopy(LMSValues, 0, dArray, 0, dArray.length);
    return dArray;
  }

  public final double[] getOPPValues(int x, int y, double[] dArray) {
    if (catType == null) {
      throw new UnsupportedOperationException();
    }

    double[] LMSValues = getLMSValues(x, y, dArray);
    double[] OPPValues = OPP.fromLMSValues(LMSValues);
    System.arraycopy(OPPValues, 0, dArray, 0, dArray.length);
    return dArray;
  }

  public final double[] getIPTValuesFromXYZ(int x, int y, double[] dArray) {
    double[] XYZValues = getXYZValues(x, y, dArray);
    double[] IPTValues = IPT.fromXYZValues(XYZValues);
    System.arraycopy(IPTValues, 0, dArray, 0, dArray.length);
    return dArray;
  }

  public final double[] getIPTValuesFromLMS(int x, int y, double[] dArray) {
    double[] LMSValues = getLMSValues(x, y, dArray);
    double[] IPTValues = IPT.fromLMSValues(LMSValues);
    System.arraycopy(IPTValues, 0, dArray, 0, dArray.length);
    return dArray;
  }

  public int getWidth() {
    return doubleImage.getWidth();
  }

  public int getHeight() {
    return doubleImage.getHeight();
  }

  public CIEXYZ getReferenceWhite() {
    return this.profileColorSpace.getReferenceWhite();
  }

  public CIEXYZ getPCSReferenceWhite() {
    return this.profileColorSpace.getPCSReferenceWhite();
  }

  public ProfileColorSpace getProfileColorSpace() {
    return profileColorSpace;
  }
}
