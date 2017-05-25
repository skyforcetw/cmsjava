package shu.cms.image;

import java.io.*;
import javax.media.jai.*;

import java.awt.image.*;

import com.sun.image.codec.jpeg.*;
import shu.math.*;
import shu.math.array.DoubleArray;
import shu.image.*;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來作為影像的Wrapper,因為影像有RGB跟LMS兩種,
 * 但是對於白點偵測來說兩種影像並沒有不同,
 * 因此透過此Wrapper來簡化影像的操作
 *
 * 與DeviceIndependentImage的分野在於:
 * 要是對於影像的XYZ/RGB/LMS沒有通用性的操作需求
 * 就盡量以DeviceIndependentImage為主
 *
 * 例外的是,純粹需要RGB操作時,以ImageSkeleton為主
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class ImageSkeleton
    implements ImageInterface {

  public final static ImageSkeleton getInstance(BufferedImage bufferedImage) {
    ByBufferedImage byBufferedImage = new ByBufferedImage(bufferedImage);
    return byBufferedImage;
  }

  public final static ImageSkeleton getInstance(DeviceIndependentImage lmsImage,
                                                boolean byLMS) {
    if (byLMS) {
      ByLMSImage byLMSImage = new ByLMSImage(lmsImage);
      return byLMSImage;
    }
    else {
      ByXYZImage byXYZImage = new ByXYZImage(lmsImage);
      return byXYZImage;
    }
  }

  protected static enum Style {
    BufferedImageType, LMSImageType, XYZImageType;
  }

  protected Style type;

  public abstract void setPixel(int x, int y, double[] dArray);

  public abstract void setPixel(int x, int y, int[] dArray);

  public abstract double[] getPixel(int x, int y, double[] dArray);

  public abstract int[] getPixel(int x, int y, int[] dArray);

  public abstract BufferedImage getBufferedImage();

  protected static class ByBufferedImage
      extends ImageSkeleton {
    protected BufferedImage bufferedImage;
    public ByBufferedImage(BufferedImage bufferedImage) {
      this.bufferedImage = bufferedImage;
      type = Style.BufferedImageType;
    }

    public void setPixel(int x, int y, double[] dArray) {
      bufferedImage.getRaster().setPixel(x, y, dArray);
    }

    public double[] getPixel(int x, int y, double[] dArray) {
      double[] result = bufferedImage.getRaster().getPixel(x, y, dArray);
      return result;
    }

    public int[] getPixel(int x, int y, int[] dArray) {
      int[] result = bufferedImage.getRaster().getPixel(x, y, dArray);
      return result;
    }

    public void setPixel(int x, int y, int[] dArray) {
      bufferedImage.getRaster().setPixel(x, y, dArray);
    }

    public int getWidth() {
      return bufferedImage.getWidth();
    }

    public int getHeight() {
      return bufferedImage.getHeight();
    }

    /**
     *
     * @return BufferedImage
     */
    public BufferedImage getBufferedImage() {
      return bufferedImage;
    }
  }

  protected static class ByLMSImage
      extends ImageSkeleton {
    protected DeviceIndependentImage DIImage;

    public ByLMSImage(DeviceIndependentImage lmsImage) {
      this.DIImage = lmsImage;
      type = Style.LMSImageType;
    }

    public void setPixel(int x, int y, double[] dArray) {
      DIImage.setLMSValues(x, y, dArray);
    }

    public void setPixel(int x, int y, int[] dArray) {
      throw new UnsupportedOperationException();
    }

    public double[] getPixel(int x, int y, double[] dArray) {
      return DIImage.getLMSValues(x, y, dArray);
    }

    public int[] getPixel(int x, int y, int[] dArray) {
      throw new UnsupportedOperationException();
    }

    public int getWidth() {
      return DIImage.getWidth();
    }

    public int getHeight() {
      return DIImage.getHeight();
    }

    /**
     *
     * @return BufferedImage
     */
    public BufferedImage getBufferedImage() {
      return DIImage.getBufferedImage();
    }
  }

  protected static class ByXYZImage
      extends ImageSkeleton {
    protected DeviceIndependentImage DIImage;

    public ByXYZImage(DeviceIndependentImage lmsImage) {
      this.DIImage = lmsImage;
      type = Style.XYZImageType;
    }

    public void setPixel(int x, int y, double[] dArray) {
      DIImage.setXYZValues(x, y, dArray);
    }

    public void setPixel(int x, int y, int[] dArray) {
      throw new UnsupportedOperationException();
    }

    public double[] getPixel(int x, int y, double[] dArray) {
      return DIImage.getXYZValues(x, y, dArray);
    }

    public int[] getPixel(int x, int y, int[] dArray) {
      throw new UnsupportedOperationException();
    }

    public int getWidth() {
      return DIImage.getWidth();
    }

    public int getHeight() {
      return DIImage.getHeight();
    }

    /**
     *
     * @return BufferedImage
     */
    public BufferedImage getBufferedImage() {
      return DIImage.getBufferedImage();
    }
  }

  /**
   *
   * @param filename String
   * @return BufferedImage
   * @throws IOException
   * @deprecated 改用loadImage
   */
  public final static BufferedImage loadJPEGImage(String filename) throws
      IOException {
    FileInputStream fs = new FileInputStream(filename);
    JPEGImageDecoder in = JPEGCodec.createJPEGDecoder(fs);
    return in.decodeAsBufferedImage();
  }

  public static void main(String[] args) throws IOException {
    BufferedImage bi = ImageUtils.loadImage("oyp.jpg");
    ImageSkeleton i = ImageSkeleton.getInstance(bi);
    double[] p = i.getPixel(0, 0, new double[3]);
    System.out.println(p.length);
    System.out.println(DoubleArray.toString(p));
  }

  /**
   *
   * @param original double[][]
   * @return double[][]
   * @deprecated
   */
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

  /**
   *
   * @param imageData double[][]
   * @param gamma double
   * @return BufferedImage
   * @deprecated
   */
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

  /**
   *
   * @param filename String
   * @param image BufferedImage
   * @throws IOException
   * @deprecated
   */
  public final static void storeJPEGImage(String filename,
                                          BufferedImage image) throws
      IOException {
    FileOutputStream fs = new FileOutputStream(filename);
    JPEGEncodeParam jpd = JPEGCodec.getDefaultJPEGEncodeParam(image);
    jpd.setQuality(1.0f, false);
    JPEGImageEncoder o = JPEGCodec.createJPEGEncoder(fs, jpd);
    o.encode(image);
  }

  /**
   *
   * @param filename String
   * @param image BufferedImage
   * @throws IOException
   * @deprecated
   */
  public final static void storeTIFFImage(String filename,
                                          BufferedImage image) throws
      IOException {
    JAI.create("filestore", image, filename, "TIFF");
  }

  public abstract int getWidth();

  public abstract int getHeight();
}
