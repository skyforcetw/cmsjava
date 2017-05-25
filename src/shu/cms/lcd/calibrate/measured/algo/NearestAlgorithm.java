package shu.cms.lcd.calibrate.measured.algo;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.calibrate.measured.util.*;
import shu.cms.lcd.material.*;
import shu.cms.measure.cp.*;
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
public abstract class NearestAlgorithm
    extends Algorithm {

  public final AlgoResult getNearestRGB(CIEXYZ center, DuplicateLinkedList<RGB>
      rgbList, int lastCount) {
    return getNearestRGB(center, rgbList.lastToArray(new RGB[lastCount]));
  }

  protected abstract double[] getDelta(CIEXYZ center, CIEXYZ XYZ);

  public final double[] getDelta(CIEXYZ XYZ, RGB rgb) {
    Patch p = mi.measure(rgb, this.isForceTrigger(),
                         AutoCPOptions.get(
                             "CPM_MeasureRequestThanTrigger"));
    return getDelta(XYZ, p.getXYZ());
  }

  public NearestAlgorithm(CIEXYZ white, MeasureInterface mi) {
    this.white = white;
    this.mi = mi;
  }

  private MeasureInterface mi;
  protected CIEXYZ white;

  protected MeasureResult getMeasureResult(RGB[] aroundRGB) {
    MeasureResult measureResult =
        mi.measureResult(aroundRGB,
                         this.isForceTrigger(),
                         AutoCPOptions.get("CPM_MeasureRequestThanTrigger"));
    return measureResult;
  }

  public AlgoResult getNearestRGB(CIEXYZ center, RGB[] aroundRGB) {
    MeasureResult measureResult = getMeasureResult(aroundRGB);
    List<Patch> patchList = measureResult.result;
    int size = patchList.size();
    double[] dist = new double[size];
    CIEXYZ[] aroundXYZ = new CIEXYZ[size];

    for (int x = 0; x < size; x++) {
      Patch patch = patchList.get(x);
      CIEXYZ XYZ = patch.getXYZ();
      aroundXYZ[x] = XYZ;
      dist[x] = getIndex(center, XYZ);
    }
    int index = Maths.minIndex(dist);
    RGB rgb = patchList.get(index).getRGB();
    AlgoResult result = new AlgoResult(rgb, dist, aroundRGB, aroundXYZ, index,
                                       measureResult.practicalMeasureCount);
    return result;
  }

  protected abstract double getIndex(CIEXYZ center, CIEXYZ around);
}
