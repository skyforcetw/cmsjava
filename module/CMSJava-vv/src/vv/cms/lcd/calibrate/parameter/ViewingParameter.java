package vv.cms.lcd.calibrate.parameter;

import shu.cms.util.*;

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �[�����Ҫ��Ѽ�, �ΦbCSF���p��W�ϥ�
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ViewingParameter
    implements Parameter {
  /**
   * �Z���ù����Z��(inch)
   */
  public double distanceInches = 32;
  /**
   * �ù��j�p(inch)
   */
  public double LCDSize = 32;
  /**
   * �ù��ѪR��
   */
  public Resolution resolution = Resolution.getScreenResolution();
  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("distanceInches: ");
    buf.append(distanceInches);
    buf.append("\nLCDSize: ");
    buf.append(LCDSize);
    buf.append("\nresolution: ");
    buf.append(resolution);
    return buf.toString();
  }
}
