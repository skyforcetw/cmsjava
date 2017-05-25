package shu.cms.plot;

import shu.cms.colorspace.ColorSpace;
import shu.cms.colorspace.depend.*;

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
public abstract class RGB2ColorSpaceAdapter
    extends RGB2ColorSpaceTransfer {

  /**
   * _getColorSpace
   *
   * @param rgb RGB
   * @return ColorSpace
   */
  public abstract ColorSpace _getColorSpace(RGB rgb);

  /**
   * getRGB
   *
   * @param colorspaceValues double[]
   * @return RGB
   */
  public RGB getRGB(double[] colorspaceValues) {
    return null;
  }
}
