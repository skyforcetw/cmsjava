package shu.cms.gma.gbd;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.gma.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.math.array.*;
import shu.cms.gma.gbp.*;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來敘述色域表面的邊界
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class GamutBoundaryDescriptor {
  protected GamutBoundaryPoint gbp;
  protected FocalPoint fp;

  protected List<CIELCh> getHuePlane(double hue) {
    return gbp.getHuePlane(hue);
  }

  protected GamutBoundaryDescriptor(GamutBoundaryPoint gbp,
                                    FocalPoint.FocalType focalType) {
    this.gbp = gbp;
    fp = FocalPoint.getInstance(gbp, focalType);
  }

  protected GamutBoundaryDescriptor(FocalPoint.FocalType focalType) {
    fp = FocalPoint.getInstance(focalType);
  }

  protected GamutBoundaryDescriptor() {

  }

  public static void main(String[] args) {
//    ProfileColorSpace srgb = ProfileColorSpace.Instance.get(RGB.ColorSpace.
//        sRGB);
//    GamutBoundaryPoint sGBP = new GamutBoundaryPoint(srgb);
//    sGBP.calculateGamutByLCh();
//
//    List<CIELCh> plane = sGBP.getHuePlane(166);
//    double[] array = sGBP.getHueLCArray(166);
//    for (int x = 0; x < plane.size(); x++) {
//      System.out.print(plane.get(x).C + " ");
//    }
//    System.out.println("");
//    System.out.println(DoubleArray.toString(array));
  }

  /**
   * 計算該點所對映到的邊界
   * @param LCh CIELCh
   * @return CIELCh
   */
  public abstract CIELCh getBoundaryLCh(CIELCh LCh);

  /**
   * 是否在色域之外
   * @param LCh CIELCh
   * @param boundary CIELCh
   * @return boolean
   */
  public final static boolean isOutOfGamut(CIELCh LCh, CIELCh boundary) {
    return LCh.C > boundary.C;
  }

  public boolean isOutOfGamut(CIELCh LCh) {
    CIELCh boundary = getBoundaryLCh(LCh);
    return isOutOfGamut(LCh, boundary);
  }

}
