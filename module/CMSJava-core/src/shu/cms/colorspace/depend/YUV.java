package shu.cms.colorspace.depend;

import shu.math.array.*;

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
public class YUV
    extends OpponentColorBase {

  private ITU_R itu_r = ITU_R.BT709;
  private final static int[] lrgybIndex = new int[] {
      0, 2, 1};

  public double Y;
  public double U;
  public double V;

  public YUV(RGB rgb) {
    super(rgb, lrgybIndex);
    this.itu_r = ITU_R.getITU_R(rgb.getRGBColorSpace());
  }

  public YUV(RGB.ColorSpace colorSpace) {
    super(colorSpace, lrgybIndex);
    this.itu_r = ITU_R.getITU_R(colorSpace);
  }

  public YUV(RGB.ColorSpace colorSpace, double[] yuvValues) {
    super(colorSpace, yuvValues, lrgybIndex);
    this.itu_r = ITU_R.getITU_R(colorSpace);
  }

  private YUV(RGB.ColorSpace colorSpace, double[] yuvValues, ITU_R itu_r) {
    super(colorSpace, yuvValues, lrgybIndex);
    this.itu_r = itu_r;
  }

  public YUV(ColorAppearanceBase colorAppearanceBase) {
    super(colorAppearanceBase);
  }

  protected final double[] _getValues(double[] values) {
    values[0] = Y;
    values[1] = U;
    values[2] = V;
    return values;
  }

  protected void _setValues(double[] values) {
    Y = values[0];
    U = values[1];
    V = values[2];
  }

  public RGB toRGB() {
    return toRGB(this);
  }

  protected double[] _fromRGB(RGB rgb) {
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
    RGB.ColorSpace rgbColorSpacce = rgb.getRGBColorSpace();
    ITU_R itu_r = ITU_R.getITU_R(rgbColorSpacce);
    double[][] m = getFromRGBMatrix(itu_r);
    double[] yuvValues = DoubleArray.times(m, rgbValues);
    return yuvValues;
  }

  public static void main(String[] args) {
    RGB rgb = new RGB(RGB.ColorSpace.SMPTE_C, new int[] {100, 21, 241});
//    rgb.changeMaxValue(RGB.MaxValue.Double1);
//    YUV yuv = fromRGB(rgb);
    YUV yuv = new YUV(rgb);
    System.out.println(yuv);
    RGB rgb2 = yuv.toRGB();
    rgb2.changeMaxValue(RGB.MaxValue.Double255);
    System.out.println(rgb2);
  }

  /**
   * 參考自http://en.wikipedia.org/wiki/YUV
   * @param rgb RGB
   * @return YUV
   */
  /*public final static YUV fromRGB(RGB rgb) {
    double[] rgbValues = rgb.getValues(new double[3], RGB.MaxValue.Double1);
    RGB.ColorSpace rgbColorSpacce = rgb.getRGBColorSpace();
    ITU_R itu_r = ITU_R.getITU_R(rgbColorSpacce);
    double[][] m = getFromRGBMatrix(itu_r);
    double[] yuvValues = DoubleArray.times(m, rgbValues);
    YUV yuv = new YUV(rgbColorSpacce, yuvValues, itu_r);
    return yuv;
     }*/

  private static double[][] fromRGB = null;
  private static double[][] toRGB = null;

  private final static double[][] getFromRGBMatrix(ITU_R itu_r) {
    if (fromRGB == null) {
      fromRGB = itu_r.getFromRGBMatrix();
    }
    return fromRGB;
  }

  private final static double[][] getToRGBMatrix(ITU_R itu_r) {
    if (toRGB == null) {
      double[][] from = getFromRGBMatrix(itu_r);
      toRGB = DoubleArray.inverse(from);
    }
    return toRGB;
  }

  public static RGB toRGB(YUV yuv) {
    double[][] m = getToRGBMatrix(yuv.itu_r);
    double[] rgbValues = DoubleArray.times(m, yuv.getValues());

    RGB rgb = new RGB(yuv.rgbColorSpace, rgbValues, RGB.MaxValue.Double1);
    return rgb;
  }

  public String[] getBandNames() {
    return new String[] {
        "Y", "U", "V"};
  }

}
