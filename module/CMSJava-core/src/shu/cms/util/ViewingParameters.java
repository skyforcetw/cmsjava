package shu.cms.util;

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
public class ViewingParameters {
  /**
   * 取得影像呈現所需要的dpi
   * @param numpixels double
   * @param viewdist double
   * @param viewangle double
   * @return double
   */
  public static double dpi(double numpixels, double viewdist, double viewangle) {
    double s = viewdist * Math.tan(viewangle * Math.PI / 180);
    return numpixels / s;
  }

  /**
   * 影像所需要的像素
   * @param viewdist double
   * @param dpi double
   * @param viewangle double
   * @return double
   */
  public static double numberPixels(double viewdist, double dpi,
                                    double viewangle) {
    double s = viewdist * Math.tan(viewangle * Math.PI / 180.);
    return dpi * s;
  }

  /**
   * 取得影像呈現所需要的距離
   * @param numpixels double
   * @param dpi double Dpi is the dot-per-inch of the monitor.
   * @param viewangle double viewangle is in degrees (of angle).
   * @return double Viewdist is in inches
   */
  public static double viewDistance(double numpixels, double dpi,
                                    double viewangle) {
    double s = numpixels / dpi;
    return viewDistance(s, viewangle);
  }

  public static double viewDistance(double objectTotal, double viewangle) {
    return objectTotal / Math.tan(viewangle * Math.PI / 180.);
  }

  public static void main(String[] args) {
//    double d = viewDistance(39.37, 100, 1);
//    System.out.println(d);
//    System.out.println(visualAngle(2, 114));
    System.out.println( (8.7 * 0.92));
    System.out.println( (8.7 * 0.92) / visualAngle(8.7, 250));
    System.out.println(visualAngle(8.7, 250));

  }

  public static double visualAngle(double objectTotal, double viewdist) {
    return Math.tan(objectTotal / viewdist) * 180. / Math.PI;
  }

  /**
   * 取得視角
   * @param numpixels double
   * @param viewdist double
   * @param dpi double
   * @return double
   */
  public static double visualAngle(double numpixels, double viewdist,
                                   double dpi) {
    double s = numpixels / dpi;
    return visualAngle(s, viewdist);
  }

}
