package shu.cms.grabber;

import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;

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
public class ObjectGrabber {
  protected ValueProvider provider;

  public ObjectGrabber(ValueProvider provider) {
    this.provider = provider;
  }

  public ValueProperty getObject(ValueProperty property) {
    double[] values = property.getValues();
    double[] resultValues = provider.getValues(values);
    ValueProperty resultProperty = (ValueProperty) property.clone();
    resultProperty.setValues(resultValues);
    return resultProperty;
  }

  public static void main(String[] args) {

    ObjectGrabber grabber = new ObjectGrabber(new ValueProvider() {
      DisplayLUT lut;
      {
        lut = DisplayLUT.Instance.getLinearMap(new RGB(255, 255, 255));
//        lut = DisplayLUT.Instance.getInverse(lut);
      }

      public double[] getValues(double[] values) {
        return lut.getInputValues(values);
      }
    });
    RGB rgb = (RGB) grabber.getObject(new RGB(5, 2, 2));
    System.out.println(rgb);
  }
}
