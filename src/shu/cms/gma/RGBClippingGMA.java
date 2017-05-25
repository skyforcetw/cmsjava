package shu.cms.gma;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.profile.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 以RGB空間作clip的色域對應
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
    //將LCh轉成RGB
    rgb.setValues(pcs.fromPCSCIELChValues(tmpValues));
    //進行合理化(clip)
    rgb.rationalize();
    rgb.getValues(tmpValues);
    //clip完的rgb,再轉成LCh,回傳
    return new CIELCh(pcs.toPCSCIELChValues(tmpValues));
  }

}
