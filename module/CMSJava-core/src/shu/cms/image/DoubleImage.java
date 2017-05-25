package shu.cms.image;

import java.io.*;

import java.awt.image.*;

import shu.cms.colorspace.independ.*;
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
public final class DoubleImage
    implements ImageInterface {

  public static enum Source {
    BufferedImage, CIEXYZ, CIEXYZValues
  }

  public static void main(String[] args) throws IOException {
    BufferedImage im = ImageUtils.loadImage("Image/D200.jpg");
    DoubleImage di = new DoubleImage(im);
    double[] pixel = new double[3];
    for (int x = 0; x < di.getWidth(); x++) {
      for (int y = 0; y < di.getHeight(); y++) {
        di.getPixel(x, y, pixel);
        pixel[0] += 20;
        di.setPixel(x, y, pixel);
      }
    }
    BufferedImage im2 = di.getBufferedImage();
    ImageUtils.storeJPEGImage("test.jpg", im2);
  }

  public final Object clone() {
    BufferedImage bi = this.getBufferedImage();
    BufferedImage cloneBI = ImageUtils.cloneBufferedImage(bi);
    DoubleImage cloneDI = new DoubleImage(cloneBI, this.maxValue);
    return cloneDI;
  }

  protected double[][][] imageData = null;
  protected BufferedImage bufferedImage = null;
  protected CIEXYZ[][] XYZImage = null;
  protected double[][][] XYZValuesImage = null;
  protected double maxValue = -1;
  public DoubleImage(BufferedImage bufferedImage) {
    this(bufferedImage, 255);
  }

  protected Source source;

  public void scale(double maxValue) {
    int w = this.getWidth();
    int h = this.getHeight();
    double factor = maxValue / this.maxValue;

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        tmp = this.getPixel(x, y, tmp);
        for (int c = 0; c < 3; c++) {
          tmp[c] *= factor;
        }
        this.setPixel(x, y, tmp);
      }
    }
    this.maxValue = maxValue;
  }

  public DoubleImage(BufferedImage bufferedImage, double maxValue) {
    this.maxValue = maxValue;
    this.bufferedImage = bufferedImage;
    source = Source.BufferedImage;
    initImageData(bufferedImage);
  }

  public int getWidth() {
    return imageData[0].length;
  }

  public int getHeight() {
    return imageData.length;
  }

  public BufferedImage getBufferedImage() {
    recoverToBufferedImage();
    return bufferedImage;
  }

  public boolean isSupportBufferedImage() {
    return! (source == Source.CIEXYZ || source == Source.CIEXYZValues);
  }

  protected void recoverToBufferedImage() {
    int h = imageData.length;
    int w = imageData[0].length;

    if (!isSupportBufferedImage()) {
      throw new UnsupportedOperationException();
    }

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        double[] pixel = imageData[y][x];
        if (pixel != null) {
          bufferedImage.getRaster().setPixel(x, y, pixel);
        }
      }
    }
  }

  public DoubleImage(CIEXYZ[][] XYZImage) {
    source = Source.CIEXYZ;
    this.XYZImage = XYZImage;
    initImageData(XYZImage);
  }

  public DoubleImage(double[][][] XYZValuesImage) {
    source = Source.CIEXYZ;
    this.XYZValuesImage = XYZValuesImage;
    initImageData(XYZValuesImage);
  }

  private void initImageData(BufferedImage bufferedImage) {
    int h = bufferedImage.getHeight();
    int w = bufferedImage.getWidth();
    imageData = new double[h][w][];
  }

  private void initImageData(CIEXYZ[][] XYZImage) {
    int h = XYZImage.length;
    int w = XYZImage[0].length;
    imageData = new double[h][w][];
  }

  private void initImageData(double[][][] XYZValuesImage) {
    int h = XYZValuesImage.length;
    int w = XYZValuesImage[0].length;
    imageData = new double[h][w][];
  }

  public void setPixel(int x, int y, double dArray[]) {
    double[] pixel = imageData[y][x];
    if (pixel == null) {
      pixel = new double[3];
      imageData[y][x] = pixel;
    }
    System.arraycopy(dArray, 0, pixel, 0, 3);
  }

  public void rationalize() {
    int w = this.getWidth();
    int h = this.getHeight();

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        tmp = this.getPixel(x, y, tmp);
        for (int c = 0; c < 3; c++) {
          tmp[c] = tmp[c] < 0 ? 0 : tmp[c];
          tmp[c] = tmp[c] > maxValue ? maxValue : tmp[c];
        }
        this.setPixel(x, y, tmp);
      }
    }
  }

  public void setSubPixel0(int x, int y, double value) {
    getPixel(x, y, tmp);
    tmp[0] = value;
    setPixel(x, y, tmp);
  }

  public void setPixel(int x, int y, int ch, double value) {
    getPixel(x, y, tmp);
    tmp[ch] = value;
    setPixel(x, y, tmp);
  }

  public void setSubPixel1(int x, int y, double value) {
    getPixel(x, y, tmp);
    tmp[1] = value;
    setPixel(x, y, tmp);
  }

  public void setSubPixel2(int x, int y, double value) {
    getPixel(x, y, tmp);
    tmp[2] = value;
    setPixel(x, y, tmp);
  }

  protected double[] tmp = new double[3];

  public double getSubPixel0(int x, int y) {
    return getPixel(x, y, tmp)[0];
  }

  public double getSubPixel1(int x, int y) {
    return getPixel(x, y, tmp)[1];
  }

  public double getSubPixel2(int x, int y) {
    return getPixel(x, y, tmp)[2];
  }

  public double getPixel(int x, int y, int ch) {
    return getPixel(x, y, tmp)[ch];
  }

  public int[] getPixel(int x, int y, int iArray[]) {
    double[] dPixel = getPixel(x, y, new double[3]);
    if (iArray == null) {
      iArray = new int[3];
    }
    for (int c = 0; c < 3; c++) {
      iArray[c] = (int) dPixel[c];
    }
    return iArray;
  }

  public void setPixel(int x, int y, int iArray[]) {
    for (int c = 0; c < 3; c++) {
      tmp[c] = iArray[c];
    }
    this.setPixel(x, y, tmp);
  }

  public double[] getPixel(int x, int y, double dArray[]) {
    double[] pixel = imageData[y][x];
    if (pixel == null) {
      switch (source) {
        case BufferedImage:
          pixel = bufferedImage.getRaster().getPixel(x, y, pixel);
          break;
        case CIEXYZ:
          pixel = XYZImage[y][x].getValues();
          break;
        case CIEXYZValues:
          pixel = XYZValuesImage[y][x];
      }

      imageData[y][x] = pixel;
    }
    if (dArray == null) {
      dArray = new double[3];
    }
    System.arraycopy(pixel, 0, dArray, 0, 3);
    return dArray;
  }
}
