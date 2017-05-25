package auo.cms.test.intensity;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.RGBBase.Channel;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface ChromaticityAdjustEstimatorIF {
  double[] getdxdy(RGB.Channel ch, int grayLevel);
}

class LCDModelChromaticityAdjustEstimator
    implements ChromaticityAdjustEstimatorIF {
  private SimpleLCDModelIF model;
  private RGB.MaxValue bitDepth;
  private RGB[] dglut;
  public LCDModelChromaticityAdjustEstimator(SimpleLCDModelIF model,
                                             RGB[] dglut,
                                             RGB.MaxValue bitDepth) {
    this.model = model;
    this.dglut = dglut;
    this.bitDepth = bitDepth;
  }

  /**
   * getdxdy
   *
   * @param ch Channel
   * @param grayLevel int
   * @return double[]
   */
  public double[] getdxdy(Channel ch, int grayLevel) {
    RGB rgb = (RGB) dglut[grayLevel].clone();
    CIEXYZ XYZ = model.getXYZ(rgb);
    double value = rgb.getValue(ch, bitDepth);
    value++;
    rgb.setValue(ch, value, bitDepth);
    CIEXYZ XYZ2 = model.getXYZ(rgb);
    double[] dxdy = new CIExyY(XYZ2).getDeltaxy(new CIExyY(XYZ));
    return dxdy;
  }

}
