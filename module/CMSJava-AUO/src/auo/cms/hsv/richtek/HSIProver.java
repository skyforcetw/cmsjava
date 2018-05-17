package auo.cms.hsv.richtek;

import java.io.*;

import java.awt.image.*;

import shu.cms.colorspace.ColorSpace;
import shu.cms.colorspace.depend.*;
import shu.image.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class HSIProver {

  public static void main(String[] args) {
    BufferedImage img = calculateHSBImage();
    try {
      ImageUtils.storeTIFFImage("richtek.tif", img);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  static BufferedImage calculateHSBImage() {
//   Dimension size = ditherCanvas1.getSize();
    int height = 600;
    int width = 600;
    BufferedImage HSBImage = new BufferedImage(width, height,
                                               BufferedImage.TYPE_INT_RGB);
//   if (HSBImage == null || HSBImage.getWidth() != width
//       || HSBImage.getHeight() != height) {
//     HSBImage = new BufferedImage(width,
//                                  height, BufferedImage.TYPE_INT_RGB);
//   }
//   else if (R == hsbRGBSelected[0] && G == hsbRGBSelected[1]
//            && B == hsbRGBSelected[2]) {
//     return HSBImage;
//   }
//   hsbRGBSelected[0] = R;
//   hsbRGBSelected[1] = G;
//   hsbRGBSelected[2] = B;

    int half = (height / 2) - 1;

    double[] hsbValues = new double[3];
    hsbValues[2] = 1;
    double[] LabValues = new double[3];
    LabValues[1] = half;
    double maxRadial = ColorSpace.fastCartesian2RadialValues(LabValues);
    int xOriginal = (width - height) / 2;
    int xEnd = height + xOriginal;
//    int xMiddle = width / 2;
    int yEnd = height / 2;
    int xHalf = half + xOriginal;
    int codeR, codeG, codeB;
    double radial = -1;

    for (int x = xOriginal; x < xEnd; x++) {
      for (int y = 0; y < yEnd; y++) {
        LabValues[1] = x - xHalf;
        LabValues[2] = - (y - half);
        //еbо|
        radial = ColorSpace.fastCartesian2RadialValues(LabValues);
        if (radial <= maxRadial) {

          //====================================================================
          // part1
          //====================================================================
          //идл╫

          hsbValues[0] = ColorSpace.fastCartesian2AngularValues(LabValues);
          hsbValues[1] = radial / maxRadial;
          hsbValues[2] = 1;
          ColorAppearanceAttribute caa = new ColorAppearanceAttribute(RGB.
              ColorSpace.sRGB, hsbValues);
          YUV ycc = new YUV(caa);
          RGB rgb = ycc.toRGB();

//          HSV.Sandbox.fastToRGBValues(hsbValues);
          codeR = (int) rgb.R;
          codeG = (int) rgb.G;
          codeB = (int) rgb.B;

          HSBImage.setRGB(x, y, ( (codeR << 16) | (codeG << 8) | codeB));
          HSBImage.setRGB(x, height - y - 1,
                          ( (codeR << 16) | (codeB << 8) | codeG));
          //====================================================================

        }

      }
    }
    return HSBImage;
  }

}
