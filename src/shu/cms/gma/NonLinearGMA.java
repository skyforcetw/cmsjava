package shu.cms.gma;

import shu.cms.colorspace.independ.*;
import shu.cms.gma.gbp.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class NonLinearGMA
    extends GamutMappingAlgorithm {
  public NonLinearGMA(GamutBoundaryPoint gbp,
                      FocalPoint.FocalType focalType) {
    super(gbp, focalType);
  }

  /**
   *
   * @param LCh CIELCh
   * @return CIELCh
   * @todo M gma NonLinearMultiple
   */
  protected CIELCh _gamutMapping(final CIELCh LCh) {
    CIELCh boundaryLCh = gbd.getBoundaryLCh(LCh);
    CIELCh result = Mapper.chromaClipping(boundaryLCh, LCh);
    return result;

  }

}
