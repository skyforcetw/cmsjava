package shu.cms.dc.raw;

import java.io.*;
import javax.imageio.*;

import java.awt.image.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import shu.cms.*;
import shu.cms.dc.*;
import shu.cms.image.*;
import shu.image.*;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * Image Pipeline參考:
 * PixelsCorrection
 * OpticalBlackCompensation
 * WhiteBalanceCorrection
 * CFAInterpolation
 * ColorCalibration
 * GammaCorrection
 * ColorSpaceConversion
 * EdgeEngancement&NoiseReduction
 * ContrastAdjustment
 *
 * Image Pipeline參考2:
 BayerData
 CMOS Sensor Interface
 Black Level Control
 Defect Pixel Concealment
 Lens Shading Correction
 Color Interpolation
 Edge Enhancement
 Color Space Conversion
 Gamma
 Hue/Saturation Control
 Color Matrix
 False Color Suppression
 Bright/Contrast Control
 Ouput Fomatting
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class RawConverter {

  protected final static BufferedImage getOriginalRawImage(String rawFilename) throws
      IOException {
    File f = new File(rawFilename);
    return ImageIO.read(f);
  }

  /**
   * convert1,r/g/b不因為白平衡做gain調整
   * @param rawFilename String
   * @return BufferedImage
   * @throws IOException
   */
  public BufferedImage convert(String rawFilename) throws IOException {
    BufferedImage originalImage = getOriginalRawImage(rawFilename);
    IntegerImage intImage = new IntegerImage(originalImage, 4095);
    intImage = interpolate(intImage);
    intImage.scale(65535);
    return intImage.getBufferedImage();
  }

  /**
   * 先依照DCTarget(限定CCSG)的E5進行正規化,再轉圖
   * @param rawFilename String
   * @param ccsg DCTarget
   * @return BufferedImage
   * @throws IOException
   */
  public BufferedImage convert(String rawFilename, DCTarget ccsg) throws
      IOException {
    if (ccsg.getType() != DCTarget.Chart.CCSG) {
      return null;
    }
    Patch whitePath = ccsg.getPatch(44);
    double[] whiteRGBValues = whitePath.getRGB().getValues();

    BufferedImage originalImage = getOriginalRawImage(rawFilename);
    IntegerImage intImage = new IntegerImage(originalImage, 4095, true);
    intImage = interpolate(intImage);
    intImage.scale(65535);
    intImage.normalize(whiteRGBValues);
    return intImage.getBufferedImage();
  }

  protected void normalize(BufferedImage image, DCTarget ccsg) {
    Patch whitePath = ccsg.getPatch(44);
    double[] whiteRGBValues = whitePath.getRGB().getValues();
    int w = image.getWidth();
    int h = image.getHeight();
    double[] pixel = new double[3];

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        image.getRaster().getPixel(x, y, pixel);
        for (int ch = 0; ch < 3; ch++) {
          pixel[ch] /= whiteRGBValues[ch];
        }
        image.getRaster().setPixel(x, y, pixel);
      }
    }
  }

  /**
   *
   * @param image BufferedImage
   * @deprecated
   */
  protected void normalize(BufferedImage image) {
    ImageSkeleton im = ImageSkeleton.getInstance(image);
    int w = im.getWidth();
    int h = im.getHeight();
    double[] normal = {
        0.32733333333333337, 0.5866666666666667, 0.3630980392156863};

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        double[] pixel = im.getPixel(x, y, new double[3]);
        for (int ch = 0; ch < 3; ch++) {
          pixel[ch] /= normal[ch];
        }

        im.setPixel(x, y, pixel);
      }
    }
  }

  /**
   * 將12bit轉換到16bit
   * @param image BufferedImage
   */
  protected void scaleImage(BufferedImage image) {
    ImageSkeleton im = ImageSkeleton.getInstance(image);
    int w = im.getWidth();
    int h = im.getHeight();

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        double[] pixel = im.getPixel(x, y, new double[3]);
//        int[] pixel = im.getPixel(x, y, new int[3]);
        for (int ch = 0; ch < 3; ch++) {
          pixel[ch] *= 16;
        }
        im.setPixel(x, y, pixel);
      }
    }
  }

  /**
   *
   * @param image IntegerImage
   * @return IntegerImage
   * @deprecated
   */
  protected IntegerImage interpolate(IntegerImage image) {
//    return CFAInterpolatorOld.variableNumberGradientsMethod(image);
    return CFAInterpolator.nearestNeighborReplication(image);
//    return CFAInterpolator.laplacianLinearInterpolation(image);
//    return CFAInterpolator.variableNumberGradientsMethod(image);
  }

  /**
   *
   * @param image BufferedImage\
   * @deprecated
   */
  protected void interpolate(BufferedImage image) {
    DeviceIndependentImage diImg = DeviceIndependentImage.getInstance(image, null);
    PlaneImage pImg = PlaneImage.getInstance(diImg, PlaneImage.Domain.RGB);
    double[][][] planeIMGArray = pImg.getPlaneImage();
    planeIMGArray = CFAInterpolatorOld.variableNumberGradientsMethod(
        planeIMGArray);
//    planeIMGArray = CFAInterpolator.nearestNeighborReplication(planeIMGArray,
//        CFAInterpolator.Direction.Upper);
    pImg.setPlaneImage(planeIMGArray);
    pImg.restoreToDeviceIndependentImage(diImg);
  }

  public static void main(String[] args) throws IOException {
    /*String dirName = "Camera Files/D200_2/Psychophysics/D50/";
         File dir = new File(dirName);
         RawConverter converter = new RawConverter();
         for (File file : dir.listFiles(new NEFFilter())) {
      String filename = file.getCanonicalPath();
      System.out.println(filename);
      BufferedImage img = converter.convert(filename);
     String rawFilename = filename.substring(0, filename.lastIndexOf(".NEF")) +
          ".tif";
      File o = new File(rawFilename);
      ImageIO.write(img, "tiff", o);
         }*/

    DCTarget target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                            LightSource.i1Pro.D65,
                                            1., DCTarget.Chart.CCSG);
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File(System.getProperty(
        "user.dir")));
    chooser.addChoosableFileFilter(new FileNameExtensionFilter(
        "NEF (*.nef)", "nef"));
    chooser.showOpenDialog(null);
    String filename = chooser.getSelectedFile().getAbsolutePath();
    long start = System.currentTimeMillis();
    RawConverter converter = new RawConverter();
    BufferedImage img = converter.convert(filename, target);
//    BufferedImage img = converter.getOriginalRawImage(filename);
//         BufferedImage img = converter.convert(filename, target);
    System.out.println(System.currentTimeMillis() - start);

    String rawFilename = filename.substring(0, filename.lastIndexOf(".NEF")) +
        ".tif";

    File o = new File(rawFilename);
    ImageIO.write(img, "tiff", o);
  }

  protected static class NEFFilter
      implements FilenameFilter {
    public boolean accept(File dir, String name) {
      return name.endsWith(".NEF");
    }
  }
}
