package shu.cms.measure.intensity;

import java.util.*;

import shu.cms.colorspace.depend.*;

/**
 * <p>Title: CMSJava-core</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DGLutGenerator {
  private List<Component> componentList;
  private ComponentLUT lut;
  public DGLutGenerator(List<Component> componentList) {
    this.componentList = componentList;
    initComponent(componentList);
  }

  private void initComponent(List<Component> componentList) {
    lut = new ComponentLUT(componentList);
  }

  private double[] targetIntensit = new double[3];
  public RGB getDGCode(double rIntensity,
                       double gIntensity,
                       double bIntensity) {
    if (true) {
      rIntensity = lut.correctIntensityInRange(RGB.Channel.R, rIntensity);
//      rIntensity = Double.isInfinite(rIntensity) ? 0 : rIntensity;
//        rCorrect = lut->hasCorrectedInRange(Channel::R);
      gIntensity = lut.correctIntensityInRange(RGB.Channel.G, gIntensity);
//      gIntensity = Double.isInfinite(gIntensity) ? 0 : gIntensity;
//        gCorrect = lut->hasCorrectedInRange(Channel::G);
      bIntensity = lut.correctIntensityInRange(RGB.Channel.B, bIntensity);
//      bIntensity = Double.isInfinite(bIntensity) ? 0 : bIntensity;
//        bCorrect = lut->hasCorrectedInRange(Channel::B);
    }
    targetIntensit[0] = rIntensity;
    targetIntensit[1] = gIntensity;
    targetIntensit[2] = bIntensity;
    try {
      double r = Double.isInfinite(rIntensity) ? 0 :
          lut.getCode(RGB.Channel.R, rIntensity);
      double g = Double.isInfinite(gIntensity) ? 0 :
          lut.getCode(RGB.Channel.G, gIntensity);
      double b = Double.isInfinite(bIntensity) ? 0 :
          lut.getCode(RGB.Channel.B, bIntensity);

      RGB rgb = new RGB(r, g, b);
      return rgb;
    }
    catch (IndexOutOfBoundsException ex) {
      return new RGB(0, 0, 0);
    }
  }

  public double[] getTargetIntensit() {
    return targetIntensit;
  }

}
