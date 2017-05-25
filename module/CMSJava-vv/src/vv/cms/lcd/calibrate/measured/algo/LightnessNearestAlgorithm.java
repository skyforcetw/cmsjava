package vv.cms.lcd.calibrate.measured.algo;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import vv.cms.measure.cp.*;

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
public class LightnessNearestAlgorithm
    extends NearestAlgorithm {

  public LightnessNearestAlgorithm(CIEXYZ white, MeasureInterface mi,
                                   JNDIInterface jndiIF) {
    super(white, mi);
    this.jndiIF = jndiIF;
  }

  private JNDIInterface jndiIF;

  public double[] getDelta(CIEXYZ center, CIEXYZ XYZ) {
    double centerJNDI = jndiIF.getJNDI(center);
    double JNDI = jndiIF.getJNDI(XYZ);
    return new double[] {
        JNDI - centerJNDI};
  }

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
//      dist[x] = Math.abs(getDelta(center, XYZ)[0]);
//    }
//    int index = Maths.minIndex(dist);
//    RGB rgb = patchList.get(index).getRGB();
//    AlgoResult result = new AlgoResult(rgb, dist, aroundXYZ, index, aroundRGB,
//                                       measureResult.practicalMeasureCount);
//    return result;
//  }

  protected double getIndex(CIEXYZ center, CIEXYZ around) {
    return Math.abs(getDelta(center, around)[0]);
  }

  protected String toString(List<Patch> patchList) {
    StringBuilder buf = new StringBuilder();
    for (Patch p : patchList) {
      buf.append(p.getRGB());
      buf.append(" ");
    }
    return buf.toString();
  }
}
