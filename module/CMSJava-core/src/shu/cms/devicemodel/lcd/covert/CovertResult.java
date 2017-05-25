package shu.cms.devicemodel.lcd.covert;

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
public class CovertResult {

  CovertResult(RGB rgb, boolean converting, ReverseModelCovert.Mode mode) {
    this.rgb = rgb;
    this.converting = converting;
    this.mode = mode;
  }

  RGB rgb;
  boolean converting;
  ReverseModelCovert.Mode mode;
}
