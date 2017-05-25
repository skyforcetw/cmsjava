package shu.cms.lcd.calibrate.measured.algo;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.measure.cp.*;
import shu.math.*;

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 以CIE1960 uv為基礎, 找到最接近點, 同時加上限制式取得折衷解.
 * 限制式為: delta uv必須維持在同一個象限(與黑點在同一象限).
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CIEuv1960CompromiseAlgorithm
    extends CIEuv1960NearestAlgorithm {

  public CIEuv1960CompromiseAlgorithm(CIEXYZ white, MeasureInterface mi,
                                      DeltauvQuadrant quadrant) {
    super(white, mi);
    this.quadrant = quadrant;
  }

  private DeltauvQuadrant quadrant;
  /**
   * 是否要做折衷的取捨, 開了之後似乎效果不佳
   */
  private boolean compromise = false;

  /**
   * 是否符合折衷的資格: 一定要處於某個象限內
   * @param XYZ CIEXYZ
   * @return boolean
   */
  protected boolean isQualify(CIEXYZ XYZ) {
    if (quadrant == null) {
      throw new IllegalStateException("quadrant == null");
    }
    double[] duvp = getDelta(white, XYZ);
    boolean result = quadrant.isQualified(duvp);
    return result;
  }

  public AlgoResult getNearestRGB(CIEXYZ center, RGB[] aroundRGB) {
    MeasureResult measureResult = getMeasureResult(aroundRGB);
    List<Patch> patchList = measureResult.result;

    //========================================================================
    // 把符合要求的過濾出來
    //========================================================================
    List<Patch> filterList = new ArrayList<Patch> ();
    int listSize = patchList.size();
    CIEXYZ[] aroundXYZ = new CIEXYZ[listSize];

    for (int x = 0; x < listSize; x++) {
      Patch patch = patchList.get(x);
      CIEXYZ XYZ = patch.getXYZ();
      aroundXYZ[x] = XYZ;
      if (compromise && isQualify(XYZ)) {
        filterList.add(patch);
      }
    }
    //========================================================================

    //要是過濾出來都沒有通過資格的, 只好全數通過
    if (filterList.size() == 0) {
      filterList.addAll(patchList);
    }

    int size = filterList.size();
    double[] dist = new double[size];

    for (int x = 0; x < size; x++) {
      Patch patch = filterList.get(x);
      double[] duvp = getDelta(center, patch.getXYZ());
      double de = Math.sqrt(Maths.sqr(duvp[0]) + Maths.sqr(duvp[1]));
      dist[x] = de;
    }
    int index = Maths.minIndex(dist);

    RGB rgb = filterList.get(index).getRGB();
    AlgoResult result = new AlgoResult(rgb, dist, aroundRGB, aroundXYZ, index,
                                       measureResult.practicalMeasureCount);
    return result;
  }

  public static void main(String[] args) {
    DeltauvQuadrant d = new DeltauvQuadrant(true, true);
    CIEXYZ white = Illuminant.D65WhitePoint;
//    MeterMeasurement mm = new MeterMeasurement(new DummyMeter(), false);
//    LCDModel model = new
//        RGBColorSpaceModel(RGB.ColorSpace.sRGB);
    ProfileColorSpaceModel model = new ProfileColorSpaceModel(RGB.ColorSpace.
        sRGB);
    model.produceFactor();
    CPCodeMeasurement cpm = CPCodeMeasurement.getInstance(model, true);
    CIEuv1960CompromiseAlgorithm algo1 = new CIEuv1960CompromiseAlgorithm(
        white, cpm, d);
    CIEuv1960NearestAlgorithm algo2 = new CIEuv1960NearestAlgorithm(white,
        (MeasureInterface) cpm);
    CIExyY xyY = new CIExyY( (CIEXYZ) white.clone());
    xyY.Y /= 2;
    CIEXYZ XYZ = xyY.toXYZ();
    AlgoResult result1 = algo1.getNearestRGB(XYZ,
                                             new RGB[] {new RGB(128, 128, 128),
                                             new RGB(129, 128, 128)});
    System.out.println(result1);

    AlgoResult result2 = algo2.getNearestRGB(XYZ,
                                             new RGB[] {new RGB(128, 128, 128),
                                             new RGB(129, 128, 128)});
    System.out.println(result2);
  }
}
