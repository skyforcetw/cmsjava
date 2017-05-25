package shu.cms.applet.sky4s;

import java.io.*;

import java.awt.image.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.image.*;
import shu.cms.profile.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 以獨立色空間分析影像
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class ImageAnalyzer {
  public static void main2(String[] args) {
    HSV hsb = new HSV(RGB.ColorSpace.sRGB);
    hsb.V = 80;
    CIEXYZ w = RGB.ColorSpace.sRGB.getReferenceWhiteXYZ();
    for (int x = 0; x < 360; x += 90) {
      hsb.H = x;
      for (int y = 30; y < 100; y += 25) {
        hsb.S = y;
        RGB rgb = hsb.toRGB();
        rgb.changeMaxValue(RGB.MaxValue.Double1);
        CIEXYZ XYZ = RGB.toXYZ(rgb);
        CIELab Lab = CIELab.fromXYZ(XYZ, w);
        CIELCh LCh = new CIELCh(Lab);
        System.out.println(hsb + " " + (hsb.S * hsb.V) + " " + LCh);
      }
    }
  }

  public static void main(String[] args) {
    main2(args);
    System.exit(0);
    try {
      BufferedImage img = ImageUtils.loadImage("Image/aperture/B.jpg");
      DeviceIndependentImage diImg = DeviceIndependentImage.getInstance(img,
          ProfileColorSpace.Instance.get(RGB.ColorSpace.sRGB));
      chromaAnalyze(diImg);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void chromaAnalyze(DeviceIndependentImage diImage) {
    int h = diImage.getHeight();
    int w = diImage.getWidth();
    int size = h * w;
    double[] XYZValues = new double[3];
    CIEXYZ XYZ = new CIEXYZ();
    CIEXYZ wp = diImage.getReferenceWhite();
    CIELab brightnessWhite = CIELab.fromXYZ(wp, wp);
//    System.out.println(brightnessWhite);

    double[] chromaArray = new double[size];
    double[] saturationArray = new double[size];
    int index = 0;
    double colorfulness = 0;

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        diImage.getXYZValues(x, y, XYZValues);
        XYZ.setValues(XYZValues);
        CIELab Lab = CIELab.fromXYZ(XYZ, wp);
        CIELCh LCh = new CIELCh(Lab);

        if (LCh.L != 0 || LCh.C >= 30) {
          chromaArray[index] = LCh.C;
          colorfulness = LCh.C * brightnessWhite.L;
          saturationArray[index] = colorfulness / LCh.L;
        }

        saturationArray[index] = saturationArray[index] > 100 ? 100 :
            saturationArray[index];
        index++;
      }
    }
    System.out.println("Chroma:");
    System.out.println("mean: " + mean(chromaArray));
    System.out.println("max: " + Maths.max(chromaArray));
//    System.out.println("std: " + Cal.std(chromaArray));

    System.out.println("Saturation:");
    System.out.println("mean: " + mean(saturationArray));
    System.out.println("max: " + Maths.max(saturationArray));
//    System.out.println("std: " + Cal.std(saturationArray));
  }

  public static double mean(double[] values) {
//  return Stat.mean(values);
    double total = 0;
    int size = values.length;
    int count = 0;
    for (int x = 0; x < size; x++) {
      if (values[x] != 0) {
        total += values[x];
        count++;
      }
    }
    return total / count;
  }

}
