package shu.cms.devicemodel.lcd;

import shu.cms.colorspace.depend.*;
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
public interface MatriesInterface {
  public RGB XYZToRGBByMaxMatrix(CIEXYZ XYZ);

  public CIEXYZ RGBToXYZByMaxMatrix(RGB rgb);
}
