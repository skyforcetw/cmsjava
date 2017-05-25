package shu.cms.applet.lcd;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.measure.*;

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
public class LCDModelVerifier {
  private LCDModel model;
  private CIEXYZ white;
  private MeterMeasurement meterMeasurement;

  public LCDModelVerifier(LCDModel model, MeterMeasurement meterMeasurement) {
    this.model = model;
    this.meterMeasurement = meterMeasurement;
  }

  public DeltaE getDeltaE(RGB rgb) {
    CIEXYZ XYZ1 = model.getXYZ(rgb, false);
    Patch p = meterMeasurement.measure(rgb, rgb.toString());
    CIEXYZ XYZ2 = p.getXYZ();
    DeltaE dE = new DeltaE(XYZ1, XYZ2, white);
    return dE;
  }
}
