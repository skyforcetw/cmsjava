package shu.cms.measure.intensity;

import shu.math.lut.Interpolation1DLUT;
import java.util.List;
import shu.cms.colorspace.depend.RGB;

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
public class ComponentLUT {
  private Interpolation1DLUT rLut;
  private Interpolation1DLUT gLut;
  private Interpolation1DLUT bLut;
  private List<Component> componentList;
  public ComponentLUT(List<Component> componentList) {
    this.componentList = componentList;
    init(componentList);
  }

  public double correctIntensityInRange(RGB.Channel ch,
                                        double intensity) {
    switch (ch) {
      case R:
        return rLut.correctValueInRange(intensity);
      case G:
        return gLut.correctValueInRange(intensity);
      case B:
        return bLut.correctValueInRange(intensity);
      default:
        throw new IllegalArgumentException();
    }
  }

  public double getIntensity(RGB.Channel ch, double code) {
    switch (ch) {
      case R:
        return rLut.getValue(code);
      case G:
        return gLut.getValue(code);
      case B:
        return bLut.getValue(code);
      default:
        throw new IllegalArgumentException();
    }

  }

  public double getCode(RGB.Channel ch, double intensity) {
    switch (ch) {
      case R:
        return rLut.getKey(intensity);
      case G:
        return gLut.getKey(intensity);
      case B:
        return bLut.getKey(intensity);
      default:
        throw new IllegalArgumentException();
    }
  }

  protected void init(List<Component> componentList) {
    int size = componentList.size();
    double[] rKeys = new double[size];
    double[] gKeys = new double[size];
    double[] bKeys = new double[size];
    double[] rValues = new double[size];
    double[] gValues = new double[size];
    double[] bValues = new double[size];
    double[] values = new double[3];

    for (int x = 0; x < size; x++) {
      Component component = componentList.get(x);
      RGB intensity = component.intensity;
      RGB code = component.rgb;
      code.getValues(values, RGB.MaxValue.Double255);

      int index = size - 1 - x;
      rKeys[index] = values[0];
      gKeys[index] = values[1];
      bKeys[index] = values[2];
      rValues[index] = intensity.R;
      gValues[index] = intensity.G;
      bValues[index] = intensity.B;
    }

    rLut = new Interpolation1DLUT(rKeys, rValues);
    gLut = new Interpolation1DLUT(gKeys, gValues);
    bLut = new Interpolation1DLUT(bKeys, bValues);
  }
}
