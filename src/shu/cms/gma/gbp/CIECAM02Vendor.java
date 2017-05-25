package shu.cms.gma.gbp;

import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
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
public class CIECAM02Vendor
    extends LChVendor {
  public CIECAM02Vendor(ProfileColorSpace pcs, ViewingConditions vc) {
    this(pcs, new CIECAM02(vc));
  }

  public CIECAM02Vendor(ProfileColorSpace pcs, CIECAM02 cam02) {
    super(pcs);
    this.cam02 = cam02;
//    cam02.getViewingConditions().get
    this.white = (CIEXYZ) cam02.getViewingConditions().white.clone();
    this.white.normalizeY();

  }

  private CIEXYZ white;
  private CIECAM02 cam02;

  public double[] getLChValues(double[] rgbValues) {
    //���qpcs���Xd50 (or d65?) ��data
    //��icam, �o��JCh
    //XYZ��data���ӹ��cam�����I�a�I
    double[] XYZValues = pcs.toCIEXYZValues(rgbValues);
    CIEXYZ XYZ = new CIEXYZ(XYZValues, white);
    XYZ.normalizeWhite();
    CIECAM02Color JCh = cam02.forward(XYZ);
    double[] JChValues = JCh.getJChValues();
    return JChValues;
  }

  public double[] getLChValues(double[] rgbValues,
                               CIEXYZ referenceWhite) {
    return getLChValues(rgbValues);
  }
}
