package shu.cms.gma;

import shu.cms.colorspace.independ.*;
import shu.cms.gma.gbd.*;
import shu.cms.profile.*;
import shu.cms.gma.gbp.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 色域對應演算法的抽象類別
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class GamutMappingAlgorithm {
  protected GamutBoundaryPoint gbp;
  protected GamutBoundaryDescriptor gbd;
  protected double chromaOffset = 0;

  public GamutMappingAlgorithm(GamutBoundaryPoint gbp,
                               FocalPoint.FocalType focalType) {
    this(null, gbp, focalType, BoundaryDescriptorType.Bound2DPlus);
  }

  public GamutMappingAlgorithm(ProfileColorSpace profileColorSpace,
                               FocalPoint.FocalType focalType) {
    this(profileColorSpace, null, focalType,
         BoundaryDescriptorType.BoundRGB);
  }

  public static enum BoundaryDescriptorType {
    Bound3D, Bound2DPlus, BoundRGB, None
  }

  private GamutMappingAlgorithm(ProfileColorSpace profileColorSpace,
                                GamutBoundaryPoint gbp,
                                FocalPoint.FocalType focalType,
                                BoundaryDescriptorType boundaryDescriptorType) {

    if (boundaryDescriptorType == BoundaryDescriptorType.BoundRGB) {
      //採用RGB作為Boundary計算
      this.gbd = GamutBoundaryRGBDescriptor.getInstance(
          GamutBoundaryRGBDescriptor.Style.Step, profileColorSpace);
    }
    else if (boundaryDescriptorType == BoundaryDescriptorType.None) {

    }
    else {
      //Bound3D跟Bound2DPlus
      if (gbp == null) {
        this.gbp = new GamutBoundaryPoint(profileColorSpace);
      }
      else {
        this.gbp = gbp;
      }
//      if (calculateBoundaryByLCh) {
//        this.gbp.calculateGamutByLCh();
//      }
//      else {
      this.gbp.calculateGamut();
//      }

      switch (boundaryDescriptorType) {
        case Bound3D:
          this.gbd = new GamutBoundary3DDescriptor(this.gbp, focalType);
          break;
        case Bound2DPlus:
          this.gbd = new GamutBoundary2DPlusDescriptor(this.gbp, focalType);
          break;
      }

    }

  }

  protected abstract CIELCh _gamutMapping(final CIELCh LCh);

  public CIELCh gamutMapping(final CIELCh LCh) {
    return _gamutMapping(LCh);
  }

  public final void gamutMapping(double[][] LChValues) {
    int size = LChValues.length;
    CIELCh LCh = new CIELCh();

    for (int x = 0; x < size; x++) {
      LCh.setValues(LChValues[x]);
      CIELCh result = gamutMapping(LCh);
      result.getValues(LChValues[x]);
    }
  }

  /**
   * 邊界不確定的次數
   * (抓到的邊界並沒有落在三角形內)
   * @return double
   */
  public final int getUncertainBoundaryCount() {
    if (gbd instanceof GamutBoundary3DDescriptor) {
      return ( (GamutBoundary3DDescriptor) gbd).getUncertainBoundaryCount();
    }
    else {
      return -1;
    }

  }

  public GamutBoundaryPoint getGamutBoundaryPoint() {
    return gbp;
  }

  public GamutBoundaryDescriptor getGamutBoundaryDescriptor() {
    return gbd;
  }

  public void setChromaOffset(double chromaOffset) {
    this.chromaOffset = chromaOffset;
  }

}
