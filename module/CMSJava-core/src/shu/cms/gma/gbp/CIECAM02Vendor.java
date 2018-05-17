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
    //先從pcs撈出d50 (or d65?) 的data
    //轉進cam, 得到JCh
    //XYZ的data應該對齊cam的白點吧！
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
