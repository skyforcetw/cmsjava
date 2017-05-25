package shu.cms.lcd.calibrate.measured.algo;

import shu.cms.colorspace.independ.*;
import shu.cms.lcd.calibrate.measured.util.*;
import shu.cms.measure.cp.*;

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
public class DeltaE00NearestAlgorithm
    extends NearestAlgorithm {

  public DeltaE00NearestAlgorithm(CIEXYZ white, MeasureInterface mi) {
    super(white, mi);
  }

  public double[] getDelta(CIEXYZ center, CIEXYZ XYZ) {
    return MeasuredUtils.getDeltaE00(center, XYZ, white);
  }

//  /**
//   * 從環繞center的aroundRGB, 找到最接近的RGB
//   * @param center CIEXYZ 目標中心點
//   * @param aroundRGB RGB[] 環繞中心點的RGB
//   * @return Result
//   */
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
//      dist[x] = getDelta(center, XYZ)[0];
//    }
//    int index = Maths.minIndex(dist);
//    RGB rgb = patchList.get(index).getRGB();
//    AlgoResult result = new AlgoResult(rgb, dist, aroundXYZ, index, aroundRGB,
//                                       measureResult.practicalMeasureCount);
//    return result;
//  }

  protected double getIndex(CIEXYZ center, CIEXYZ around) {
    return getDelta(center, around)[0];
  }
}
