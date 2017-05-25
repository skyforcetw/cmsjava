package shu.cms.lcd;

import java.util.*;

import shu.cms.colorspace.depend.*;

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
public class RGBLumiComparator
    implements Comparator {
  private RGBLumiComparator() {

  }

  private static RGBLumiComparator instance;

  public final static RGBLumiComparator getInstance() {
    if (instance == null) {
      instance = new RGBLumiComparator();
    }
    return instance;
  }

  /**
   * Compares its two arguments for order.
   *
   * @param o1 the first object to be compared.
   * @param o2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first
   *   argument is less than, equal to, or greater than the second.
   */
  public int compare(Object o1, Object o2) {
    if (! (o1 instanceof RGB) || ! (o2 instanceof RGB)) {
      throw new IllegalArgumentException(
          "! (o1 instanceof RGB) || ! (o2 instanceof RGB)");
    }
    RGB rgb1 = (RGB) o1;
    RGB rgb2 = (RGB) o2;
    return Double.compare(rgb1.toXYZ(RGB.ColorSpace.sRGB).Y,
                          rgb2.toXYZ(RGB.ColorSpace.sRGB).Y);
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
