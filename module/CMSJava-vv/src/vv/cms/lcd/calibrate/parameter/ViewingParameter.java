package vv.cms.lcd.calibrate.parameter;

import shu.cms.util.*;

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 觀測環境的參數, 用在CSF的計算上使用
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
   * 距離螢幕的距離(inch)
   */
  public double distanceInches = 32;
  /**
   * 螢幕大小(inch)
   */
  public double LCDSize = 32;
  /**
   * 螢幕解析度
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
