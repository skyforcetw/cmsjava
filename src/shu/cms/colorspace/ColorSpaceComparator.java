package shu.cms.colorspace;

import java.util.*;

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
public class ColorSpaceComparator
    implements Comparator {
  private int compareIndex = 0;

  public void setCompareIndex(int index) {
    this.compareIndex = index;
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
    ColorSpace c1 = (ColorSpace) o1;
    ColorSpace c2 = (ColorSpace) o2;
    double v1 = c1.getValues()[compareIndex];
    double v2 = c2.getValues()[compareIndex];
    return Double.compare(v1, v2);
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
    return false;
  }

}
