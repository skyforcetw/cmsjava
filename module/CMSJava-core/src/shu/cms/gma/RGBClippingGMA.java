package shu.cms.gma;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.profile.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �HRGB�Ŷ��@clip��������
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class RGBClippingGMA
    extends GamutMappingAlgorithm {

  protected ProfileColorSpace pcs;
  protected double[] tmpValues = new double[3];
  protected double[] PCSWhiteValues = new double[3];
  protected RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, null,
                              RGB.MaxValue.Double1);

  public RGBClippingGMA(ProfileColorSpace profileColorSpace) {
    super(profileColorSpace, null);
    this.pcs = profileColorSpace;
    pcs.getPCSReferenceWhite().getValues(PCSWhiteValues);
  }

  /**
   * _gamutMapping
   *
   * @param LCh CIELCh
   * @return CIELCh
   */
  protected CIELCh _gamutMapping(CIELCh LCh) {
    LCh.getValues(tmpValues);
    //�NLCh�নRGB
    rgb.setValues(pcs.fromPCSCIELChValues(tmpValues));
    //�i��X�z��(clip)
    rgb.rationalize();
    rgb.getValues(tmpValues);
    //clip����rgb,�A�নLCh,�^��
    return new CIELCh(pcs.toPCSCIELChValues(tmpValues));
  }

}
