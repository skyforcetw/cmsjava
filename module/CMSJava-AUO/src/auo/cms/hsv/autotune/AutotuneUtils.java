package auo.cms.hsv.autotune;

import shu.cms.colorspace.depend.RGB;
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
 * @author skyforce
 * @version 1.0
 */
public class AutotuneUtils {
  private final static double[][] toYCbCrMatrix = new double[][] {
    {
    65.481, 128.533, 24.966}, {
    -37.797, -74.203, 112}, {
    112, -93.786, -18.214}
};

  public final static boolean isSkin1(RGB rgb) {
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
    double[] orgYCCValues = DoubleArray.timesFast(toYCbCrMatrix, rgbValues);
    double[] yccValues = DoubleArray.plus(orgYCCValues, new double[] {16, 128,
                                          128});
    double y = yccValues[0];
    double Cb = yccValues[1];
    double Cr = yccValues[2];
//    if (Cb >= 97.5 && Cb <= 142.5 && Cr >= 134 && Cr <= 176) {
//    if (y > 120 && Cb > 95 && Cr > 110) {
    if (y > 130 && Cb >= 97.5 && Cb <= 142.5 && Cr >= 134 && Cr <= 176) {
      return true;
    }
    else {
      return false;
    }
  }
}
