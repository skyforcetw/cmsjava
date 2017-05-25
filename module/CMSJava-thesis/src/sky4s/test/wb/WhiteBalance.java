package sky4s.test.wb;

import java.io.*;
import javax.media.jai.*;

import java.awt.image.*;
import java.awt.image.renderable.*;

import com.sun.image.codec.jpeg.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public abstract class WhiteBalance {

  public final static int PWB = 1;
  public final static int GWT = 2;
  public final static int MaxRGB = 3;
  public final static int Retinex = 4;
  public final static int RGWT = 5;
  public final static int GMA = 6;
  public final static int Hybrid = 7;

  protected abstract Coefficient getCoefficient(BufferedImage image);

  public final BufferedImage getWhiteBalanceImage(BufferedImage image) {
    Coefficient coef = this.getCoefficient(image);
    return vonKriesCAT(image, coef);
  }

  public static final WhiteBalance getInstance(int type) {
    switch (type) {
      case PWB:
        return new PWB();
      case GWT:
        return new GWT();
      case MaxRGB:
        return new MaxRGB();
      case Retinex:
        break;
      case RGWT:
        break;
      case GMA:
        break;
      case Hybrid:
        break;
      default:
        return null;
    }
    return null;
  }

  /**
   * 手動設定
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   *
   * <p>Copyright: Copyright (c) 2001</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw;skyforce
   * @version 1.0
   */
  static class PWB
      extends WhiteBalance {
    private Coefficient coefficient;
    protected Coefficient getCoefficient(BufferedImage image) {
      return coefficient;
    }

    public void setCoefficient(Coefficient coef) {
      this.coefficient = coef;
    }

  }

  /**
   * 最大值RGB
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   *
   * <p>Copyright: Copyright (c) 2001</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw;skyforce
   * @version 1.0
   * @deprecated
   */
  static class MaxRGB
      extends WhiteBalance {
    public static final double SAMPLE_RATE = 0.05;

    /**
     *
     * @param image BufferedImage
     * @return Coefficient
     * @deprecated
     */
    protected Coefficient getCoefficient(BufferedImage image) {
      Histogram histo1 = getHistogram(image);
      int pixels = image.getWidth() * image.getHeight();
      int sampleCount = (int) (pixels * SAMPLE_RATE);

      double[] coef = new double[3];
      int[][] bins = histo1.getBins();
      for (int x = 0; x < bins.length; x++) {
        int[] bin = bins[x];
        int acc = 0;
        for (int y = bin.length - 1; acc <= sampleCount && y >= 0; y--) {
          acc += bin[y];
          coef[x] += y * bin[y];
        }
        coef[x] /= acc;
      }

      int[] pureWhite = RGB.PURE_WHITE.getRGB();
      coef[0] = pureWhite[0] / coef[0];
      coef[1] = pureWhite[1] / coef[1];
      coef[2] = pureWhite[2] / coef[2];

      return new Coefficient(coef);
    }

    /**
     * 利用JAI的直方圖功能
     * @param image BufferedImage
     * @return Histogram
     */
    protected Histogram getHistogram(BufferedImage image) {
      // Create one histogram with 256 bins.
      ParameterBlock pb1 = new ParameterBlock();
      pb1.addSource(image);
      pb1.add(null); // The ROI
      pb1.add(1);
      pb1.add(1); // Sampling
      pb1.add(new int[] {256}); // Bins
      pb1.add(new double[] {0});
      pb1.add(new double[] {256}); // Range for inclusion
      PlanarImage dummyImage1 = JAI.create("histogram", pb1);
// Gets the histogram.
      Histogram histo1 = (javax.media.jai.Histogram)
          dummyImage1.
          getProperty("histogram");
      return histo1;
    }
  }

  /**
   * 灰界理論
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   *
   * <p>Copyright: Copyright (c) 2001</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  static class GWT
      extends WhiteBalance {
    protected Coefficient getCoefficient(BufferedImage image) {
      int width = image.getWidth();
      int height = image.getHeight();
      double r = 0, b = 0, g = 0;
      WritableRaster raster = image.getRaster();

      for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
          r += raster.getSample(i, j, 0);
          g += raster.getSample(i, j, 1);
          b += raster.getSample(i, j, 2);
        }
      }
      int count = width * height;
      r /= count;
      g /= count;
      b /= count;

      double k = (r + g + b) / 3;
      r = k / r;
      g = k / g;
      b = k / b;

      return new Coefficient(new double[] {r, g, b});
    }

  }

  public final static BufferedImage vonKriesCAT(BufferedImage src,
                                                Coefficient coef) {
    int width = src.getWidth();
    int height = src.getHeight();

    BufferedImage newImage = new BufferedImage(src.getColorModel(),
                                               src.copyData(null), false, null);

    WritableRaster raster = newImage.getRaster();

    double[] coefVal = coef.getCoef();
    double r, g, b;

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        r = raster.getSample(i, j, 0);
        g = raster.getSample(i, j, 1);
        b = raster.getSample(i, j, 2);
        r = r * coefVal[0];
        g = g * coefVal[1];
        b = b * coefVal[2];

        //====================================================================
        //超出255時會跳回0,所以要加以限制其值
        //====================================================================
        if (r > 255) {
          r = 255;
        }
        if (g > 255) {
          g = 255;
        }
        if (b > 255) {
          b = 255;
        }
        //====================================================================

        raster.setSample(i, j, 0, r);
        raster.setSample(i, j, 1, g);
        raster.setSample(i, j, 2, b);
      }
    }
    return newImage;
  }

  public static void main(String[] args) {
    String dir = "Image/WhiteBalance";
    String source = "Image/walt.jpg";

    String gwt = dir + "/gwt.jpg";
    String maxrgb = dir + "/maxrgb.jpg";
//    String pwb =  dir+"pwb.jpg";

    BufferedImage srcImg = null;
    try {
      FileInputStream fs = new FileInputStream(source);
      JPEGImageDecoder in = JPEGCodec.createJPEGDecoder(fs);
      srcImg = in.decodeAsBufferedImage();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    if (srcImg == null) {
      return;
    }
    WhiteBalance wb;
    Coefficient coef;
    BufferedImage img;

    //==========================================================================
    //GWT
    //==========================================================================
    wb = WhiteBalance.getInstance(WhiteBalance.GWT);
    coef = wb.getCoefficient(srcImg);
    System.out.println(coef);
    img = wb.getWhiteBalanceImage(srcImg);

    try {
      FileOutputStream fs = new FileOutputStream(gwt);
      JPEGEncodeParam jpd = JPEGCodec.getDefaultJPEGEncodeParam(img);
      jpd.setQuality(1.0f, false);
      JPEGImageEncoder o = JPEGCodec.createJPEGEncoder(fs, jpd);
      o.encode(img);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    //==========================================================================

    //==========================================================================
    //MaxRGB
    //==========================================================================
    wb = WhiteBalance.getInstance(WhiteBalance.MaxRGB);
    coef = wb.getCoefficient(srcImg);
    System.out.println(coef);
    img = wb.getWhiteBalanceImage(srcImg);

    try {
      FileOutputStream fs = new FileOutputStream(maxrgb);
      JPEGEncodeParam jpd = JPEGCodec.getDefaultJPEGEncodeParam(img);
      jpd.setQuality(1.0f, false);
      JPEGImageEncoder o = JPEGCodec.createJPEGEncoder(fs, jpd);
      o.encode(img);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    //==========================================================================


//    //==========================================================================
//    //PWB
//    //==========================================================================
//    wb = WhiteBalance.getInstance(WhiteBalance.PWB);
//    ( (PWB) wb).setCoefficient(new Coefficient(new double[] {1.0, 1.0, 1.0}));
//    coef = wb.getCoefficient(srcImg);
//    System.out.println(coef);
//    img = wb.getWhiteBalanceImage(srcImg);
//
//    try {
//      FileOutputStream fs = new FileOutputStream(pwb);
//      JPEGEncodeParam jpd = JPEGCodec.getDefaultJPEGEncodeParam(img);
//      jpd.setQuality(1.0f, false);
//      JPEGImageEncoder o = JPEGCodec.createJPEGEncoder(fs, jpd);
//      o.encode(img);
//    }
//    catch (IOException e) {
//      e.printStackTrace();
//    }

  }

  protected static class Coefficient {
    double[] coef = new double[3];
    Coefficient(int[] rgb) {
      coef[0] = rgb[0];
      coef[1] = rgb[1];
      coef[2] = rgb[2];
    }

    public double[] getCoef() {
      return coef;
    }

    Coefficient(double[] rgb) {
      coef[0] = rgb[0];
      coef[1] = rgb[1];
      coef[2] = rgb[2];
    }

    public String toString() {
      return coef[0] + "," + coef[1] + "," + coef[2];
    }

  }
}
