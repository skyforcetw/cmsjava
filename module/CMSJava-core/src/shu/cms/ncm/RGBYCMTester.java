package shu.cms.ncm;

import shu.cms.colorspace.depend.HSV;
import shu.cms.colorspace.depend.RGB;
import shu.math.*;
import java.util.*;
import shu.math.array.DoubleArray;

/**
 * <p>Title: Colour Management System</p>
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
public class RGBYCMTester {
  static double[] getRGBYCMValues(double[] rgbValues) {
    double[] rgbycmValues = new double[6];
    rgbycmValues[0] = rgbValues[0];
    rgbycmValues[1] = rgbValues[1];
    rgbycmValues[2] = rgbValues[2];
    rgbycmValues[3] = rgbValues[0] + rgbValues[1];
    rgbycmValues[4] = rgbValues[1] + rgbValues[2];
    rgbycmValues[5] = rgbValues[0] + rgbValues[2];

    double[] ycmValues = new double[3];
    ycmValues[0] = rgbValues[0] + rgbValues[1];
    ycmValues[1] = rgbValues[1] + rgbValues[2];
    ycmValues[2] = rgbValues[0] + rgbValues[2];
    ycmValues = DoubleArray.minus(ycmValues, Maths.min(ycmValues));

    rgbycmValues[3] = ycmValues[0];
    rgbycmValues[4] = ycmValues[1];
    rgbycmValues[5] = ycmValues[2];

    return rgbycmValues;
  }

  public static void main(String[] args) {
    NCM2005 ncm = new NCM2005();
    for (int x = 0; x < 360; x += 30) {
      HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {x, 100, 100});
      RGB rgb = hsv.toRGB();
      double[] rgbycmValues = getRGBYCMValues(rgb.getValues());
      System.out.println(hsv + " " + Arrays.toString(rgbycmValues));
      double[] rgbymcValues = ncm.getRGBYMCValues(rgb);
      System.out.println(Arrays.toString(rgbymcValues));
    }
  }
}
