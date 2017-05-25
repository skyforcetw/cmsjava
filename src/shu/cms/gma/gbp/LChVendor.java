package shu.cms.gma.gbp;

import shu.cms.colorspace.independ.*;
import shu.cms.profile.*;

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
public abstract class LChVendor {
  protected ProfileColorSpace pcs;
  public LChVendor(ProfileColorSpace pcs) {
    this.pcs = pcs;
  }

  public abstract double[] getLChValues(double[] rgbValues,
                                        CIEXYZ referenceWhite);

  public abstract double[] getLChValues(double[] rgbValues);
}

class LChabVendor
    extends LChVendor {
  public LChabVendor(ProfileColorSpace pcs) {
    super(pcs);
  }

  public double[] getLChValues(double[] rgbValues) {
    double[] LChValues = pcs.toPCSCIELChValues(rgbValues);
    return LChValues;
  }

  public double[] getLChValues(double[] rgbValues,
                               CIEXYZ referenceWhite) {
    return getLChValues(rgbValues);
  }
}
