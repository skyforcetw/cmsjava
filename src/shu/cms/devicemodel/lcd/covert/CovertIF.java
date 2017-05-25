package shu.cms.devicemodel.lcd.covert;

import shu.cms.colorspace.independ.*;

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
public interface CovertIF {
  CovertResult getRGB(CIEXYZ XYZ, boolean relativeXYZ);
}
