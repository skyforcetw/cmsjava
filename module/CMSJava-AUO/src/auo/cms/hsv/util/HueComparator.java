package auo.cms.hsv.util;

import shu.cms.colorspace.depend.RGB;
import java.util.Comparator;
import shu.cms.colorspace.depend.HSV;

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
public class HueComparator
    implements Comparator<RGB> {
  /**
   * Compares its two arguments for order.
   *
   * @param rgb1 the first object to be compared.
   * @param rgb2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first
   *   argument is less than, equal to, or greater than the second.
   */
  public int compare(RGB rgb1, RGB rgb2) {
    HSV hsv1 = new HSV(rgb1);
    HSV hsv2 = new HSV(rgb2);
    return Double.compare(hsv1.H, hsv2.H);
  }

  /**
   * Indicates whether some other object is &quot;equal to&quot; this
   * comparator.
   *
   * @param obj the reference object with which to compare.
   * @return <code>true</code> only if the specified object is also a
   *   comparator and it imposes the same ordering as this comparator.
   */
  public boolean equals(Object obj) {
    throw new UnsupportedOperationException();
  }

}
