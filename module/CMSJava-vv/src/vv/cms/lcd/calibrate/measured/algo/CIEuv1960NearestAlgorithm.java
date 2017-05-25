package vv.cms.lcd.calibrate.measured.algo;

import shu.cms.colorspace.independ.*;
import vv.cms.lcd.calibrate.measured.util.*;
import vv.cms.measure.cp.*;
import shu.math.*;

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
public class CIEuv1960NearestAlgorithm
    extends NearestAlgorithm {

  public CIEuv1960NearestAlgorithm(CIEXYZ white, MeasureInterface mi) {
    super(white, mi);
  }

  /**
   * 計算delta, 也就是delta u'v'
   * @param center CIEXYZ 目標值
   * @param XYZ CIEXYZ 計算值
   * @return double[] {delta u', delta v'}
   */
  public double[] getDelta(CIEXYZ center, CIEXYZ XYZ) {
    return MeasuredUtils.getDeltauvPrime(center, XYZ);
  }

//
//  public AlgoResult getNearestRGB(CIEXYZ center, RGB[] aroundRGB) {
//    MeasureResult measureResult = getMeasureResult(aroundRGB);
//    List<Patch> patchList = measureResult.result;
//    int size = patchList.size();
//    double[] dist = new double[size];
//    CIEXYZ[] aroundXYZ = new CIEXYZ[size];
//
//    for (int x = 0; x < size; x++) {
//      Patch patch = patchList.get(x);
//      CIEXYZ XYZ = patch.getXYZ();
//      aroundXYZ[x] = XYZ;
//      double[] duvp = getDelta(center, XYZ);
//      double de = Math.sqrt(Maths.sqr(duvp[0]) + Maths.sqr(duvp[1]));
//      dist[x] = de;
//    }
//    int index = Maths.minIndex(dist);
//    RGB rgb = patchList.get(index).getRGB();
//    AlgoResult result = new AlgoResult(rgb, dist, aroundXYZ, index, aroundRGB,
//                                       measureResult.practicalMeasureCount);
//    return result;
//  }

  protected double getIndex(CIEXYZ center, CIEXYZ around) {
    double[] duvp = getDelta(center, around);
    double de = Math.sqrt(Maths.sqr(duvp[0]) + Maths.sqr(duvp[1]));
    return de;
  }
}
