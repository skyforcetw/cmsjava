package sky4s.test.cat;

import shu.image.ImageUtils;
import java.awt.image.BufferedImage;
import java.io.*;
import shu.cms.image.DeviceIndependentImage;
import shu.cms.profile.ProfileColorSpace;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.independ.*;
import java.util.*;
import shu.cms.hvs.cam.ChromaticAdaptation;
import shu.cms.Illuminant;
import shu.cms.hvs.cam.CAMConst;
import shu.cms.colorspace.depend.RGBBase.ColorSpace;
import shu.cms.colorspace.depend.RGBBase;
import shu.math.Maths;

/**
 * <p>Title: Colour Management System - thesis</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CATTester {

  public static void main(String[] args) {
    try {
      BufferedImage bi = ImageUtils.loadImage("test.jpg");
      ProfileColorSpace sRGBpcs = ProfileColorSpace.Instance.get(RGB.ColorSpace.
          sRGB, "");

      ColorSpace D93sRGB = new ColorSpace(Illuminant.D93,
                                          ColorSpace.GammaType.sRGB, 0.64, 0.33,
                                          0.21, 0.71, 0.15, 0.06);

      ProfileColorSpace D93sRGBpcs = ProfileColorSpace.Instance.get(D93sRGB, "");

      CIEXYZ D65 = Illuminant.D65WhitePoint;
      CIEXYZ D93 = Illuminant.D93WhitePoint;
      ChromaticAdaptation ca = new ChromaticAdaptation(D65, D93,
          CAMConst.CATType.CAT02);

      DeviceIndependentImage di = DeviceIndependentImage.getInstance(bi,
          sRGBpcs);
      for (int x = 0; x < di.getWidth(); x++) {
        for (int y = 0; y < di.getHeight(); y++) {
          //double[] XYZValues = di.getXYZValues(x, y, new double[3]);
          //double[] caXYZValues = ca.getDestinationColor(XYZValues);
          //di.setXYZValues(x, y, caXYZValues);
          //====================================================================
          // case2
          //====================================================================
//          double[] rgbValues = di.getRGBValues(x, y, new double[3]);
//          rgbValues = Maths.normalize(rgbValues, 255);
//          double[] XYZValues = D93sRGBpcs.toCIEXYZValues(rgbValues);
//          di.setXYZValues(x, y, XYZValues);
          //====================================================================

          //====================================================================
          // case3
          //====================================================================
//          double[] XYZValues = di.getXYZValues(x, y, new double[3]);
//          double[] D93XYZValues = ca.getDestinationColor(XYZValues);
//          double[] rgbValues = D93sRGBpcs.fromCIEXYZValues(D93XYZValues);
//          rgbValues = Maths.undoNormalize(rgbValues, 255);
//          rgbValues = RGB.rationalize(rgbValues, RGB.MaxValue.Double255);
//          di.setRGBValues(x, y, rgbValues);
          //====================================================================

          //====================================================================
          // case4
          //====================================================================
          double[] XYZValues = di.getXYZValues(x, y, new double[3]);
          double[] D93XYZValues = ca.getDestinationColor(XYZValues);
          double[] rgbValues = sRGBpcs.fromCIEXYZValues(D93XYZValues);
          Maths.undoNormalize(rgbValues, 255);
          rgbValues = RGB.rationalize(rgbValues, RGB.MaxValue.Double255);
          di.setRGBValues(x, y, rgbValues);
          //====================================================================
        }
      }
      BufferedImage result = di.getBufferedImage();
      ImageUtils.storeJPEGImage("result.jpg", result);
//      System.out.println(Arrays.toString(result.getRaster().getPixel(318, 418,
//          new double[3])));

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
