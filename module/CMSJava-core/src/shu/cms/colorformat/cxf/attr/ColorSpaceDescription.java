package shu.cms.colorformat.cxf.attr;

import java.util.*;

import shu.cms.colorformat.cxf.*;

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
public class ColorSpaceDescription
    extends ConditionsAttributes {
  public final static String TYPE_RGB = "RGB";
  public final static String TYPE_CIELab = "CIELab";
  public final static String TYPE_CIEXYZ = "CIEXYZ";

  public static enum Type {
    RGB, CIELab, CIEXYZ;
  }

  public String type;

  public ColorSpaceDescription() {
  }

  protected final static String[] names = new String[] {
      "ColorSpaceDescription.Type",
  };

  public static ColorSpaceDescription getInstance(List<Attribute> attrs) {
    ColorSpaceDescription colorSpaceDescription = new ColorSpaceDescription();
    for (Attribute attr : attrs) {
      if (attr.getName().equals(names[0])) {
        colorSpaceDescription.type = attr.getvalue();
      }
    }
    return colorSpaceDescription;
  }
}
