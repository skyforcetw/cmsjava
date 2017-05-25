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
public class IPTLChVendor
    extends LChVendor {
  public IPTLChVendor(ProfileColorSpace pcs) {
    super(pcs);
  }

  public double[] getLChValues(double[] rgbValues) {
    double[] d65XYZValues = pcs.toD65CIEXYZValues(
        rgbValues);
    CIEXYZ XYZ = new CIEXYZ(d65XYZValues, pcs.getD65ReferenceWhite());
    XYZ.normalizeWhite();
    IPT ipt = new IPT(XYZ);
    ipt.scaleToCIELab();
    CIELCh LCh = new CIELCh(ipt);
    double[] LChValues = LCh.getValues();
    XYZ = null;
    ipt = null;
    LCh = null;
    return LChValues;
  }

  public double[] getLChValues(double[] rgbValues,
                               CIEXYZ referenceWhite) {
    return getLChValues(rgbValues);
  }
}
