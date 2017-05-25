package auo.cms.cm;

import shu.cms.lcd.LCDTarget;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.independ.*;
import java.util.List;

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
public class sRGBTargetMaker {
  public static void main(String[] args) {
    List<RGB> rgbList = LCDTarget.Instance.getRGBList(LCDTarget.Number.WHQL);
    for (RGB rgb : rgbList) {
      CIEXYZ XYZ = rgb.toXYZ(RGB.ColorSpace.sRGB);
//      System.out.println(rgb + " " + XYZ);
      XYZ.times(338.1866455);
//      System.out.println(XYZ);
      CIExyY xyY = new CIExyY(XYZ);
      System.out.println(xyY.toString());
    }
  }
}
