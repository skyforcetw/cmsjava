package shu.cms.image;

import java.util.*;

import java.awt.image.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.recover.*;
import shu.cms.reference.spectra.*;
import shu.image.ImageUtils;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ColorTemperatureImageSimulator {
  public ColorTemperatureImageSimulator(RGB.ColorSpace rgbColorSpace,
                                        Style style) {
    this.rgbColorSpace = rgbColorSpace;
    camera = new SpectralCamera(rgbColorSpace,
                                SpectraDatabase.Content.MunsellGlossy);
    this.style = style;
  }

  protected Style style;
  protected SpectralCamera camera;
  protected RGB.ColorSpace rgbColorSpace;

  public BufferedImage[] simulate(BufferedImage image,
                                  int[] colorTemperatureArray) {
    int size = colorTemperatureArray.length;
    BufferedImage[] imageArray = new BufferedImage[size];
    for (int x = 0; x < size; x++) {
      imageArray[x] = simulate(image, colorTemperatureArray[x]);
    }
    return imageArray;
  }

  public static enum Style {
    Blackbody, DIlluminant
  }

  public BufferedImage simulate(BufferedImage image, int colorTemperature) {

    BufferedImage simulate = ImageUtils.cloneBufferedImage(image);
    int w = simulate.getWidth();
    int h = simulate.getHeight();
    double[] rgbValues = new double[3];
    RGB rgb = new RGB(rgbColorSpace, new double[] {255, 255, 255},
                      RGB.MaxValue.Int8Bit);
    Spectra whiteSpectra = camera.getReflectSpectra(rgb);

    Spectra illuminant = null;
    switch (style) {
      case Blackbody:
        illuminant = CorrelatedColorTemperature.getSpectraOfBlackbodyRadiator(
            colorTemperature);
        break;
      case DIlluminant:
        illuminant = Illuminant.getDaylightByTemperature(colorTemperature).
            getSpectra();
        break;
    }

    illuminant = illuminant.reduce(whiteSpectra.getStart(), whiteSpectra.getEnd(),
                                   whiteSpectra.getInterval());
    whiteSpectra.times(illuminant);
    CIEXYZ white = whiteSpectra.getXYZ();
    Map<RGB, RGB> rgbMap = new HashMap<RGB, RGB> ();
    long start = System.currentTimeMillis();

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        simulate.getRaster().getPixel(x, y, rgbValues);
        rgb = new RGB(rgbColorSpace, rgbValues,
                      RGB.MaxValue.Int8Bit);
//        rgb.setValues(rgbValues,RGB.MaxValue.Int8Bit);
        if (rgbMap.containsKey(rgb)) {
          RGB simulateRGB = rgbMap.get(rgb);
          simulateRGB.getValues(rgbValues);
          simulate.getRaster().setPixel(x, y, rgbValues);
        }
        else {
          Spectra s = camera.getReflectSpectra(rgb);
          s.times(illuminant);
          CIEXYZ XYZ = s.getXYZ();
          XYZ.normalize(white);
          RGB simulateRGB = RGB.fromXYZ(XYZ, rgbColorSpace);
          simulateRGB.clip();
          simulateRGB.changeMaxValue(RGB.MaxValue.Int8Bit);
          simulateRGB.getValues(rgbValues);
          simulate.getRaster().setPixel(x, y, rgbValues);
          rgbMap.put(rgb, simulateRGB);
        }
      }

    }
    System.out.println(System.currentTimeMillis() - start);
    return simulate;
  }

  public static void main(String[] args) throws Exception {
    BufferedImage img = ImageUtils.loadImage(
//        "Reference Files/RGB Reference Images/OTHERS/ColorTemperatureSimulate/HIKEY5X7.JPG");
        "Reference Files/RGB Reference Images/testimgae/testimage_1920x1200.jpg");

    int[] ctArray = new int[] {
        4000, 5000, 6000, 6500, 7000, 8000, 9000, 10000};
//    int[] ctArray = new int[] {
//        6500};

    ColorTemperatureImageSimulator simulator = new
        ColorTemperatureImageSimulator(RGB.ColorSpace.sRGB, Style.DIlluminant);
    for (int ct : ctArray) {
      BufferedImage sim = simulator.simulate(img, ct);
      ImageUtils.storeJPEGImage(ct + "k.jpg", sim);
    }
  }
}
