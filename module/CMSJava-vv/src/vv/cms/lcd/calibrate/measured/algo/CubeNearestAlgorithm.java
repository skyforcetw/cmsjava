package vv.cms.lcd.calibrate.measured.algo;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
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
 * @author not attributable
 * @version 1.0
 */
public class CubeNearestAlgorithm
    extends NearestAlgorithm {

  public CubeNearestAlgorithm(CIEXYZ white, MeasureInterface mi,
                              JNDIInterface jndiIF) {
    super(white, mi);
    this.lAlgo = new LightnessNearestAlgorithm(white, mi, jndiIF);
    this.uvNearAlgo = new CIEuv1960NearestAlgorithm(white, mi);
  }

  /**
   * 撿出delta JNDI小於deltaJNDITolerance者
   * @param patchList List
   * @param center CIEXYZ
   * @return CheckResult
   */
  protected CheckResult checkQualify(List<Patch> patchList, CIEXYZ center) {
    int size = patchList.size();
    List<Patch> result = new ArrayList<Patch> (size);

    boolean allQualify = true;
    boolean allNonQualify = true;

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      CIEXYZ XYZ = p.getXYZ();
      double[] delta = lAlgo.getDelta(center, XYZ);
      if (delta[0] <= deltaJNDITolerance) {
        result.add(p);
        allNonQualify = false;
      }
      else {
        allQualify = false;
      }
    }

    CheckResult checkResult = new CheckResult(result, allQualify, allNonQualify);
    return checkResult;
  }

  private double deltaJNDITolerance = 1.0;
  private LightnessNearestAlgorithm lAlgo;
  private CIEuv1960NearestAlgorithm uvNearAlgo;

  public AlgoResult getNearestRGB(CIEXYZ center, RGB[] aroundRGB) {
    MeasureResult measureResult = getMeasureResult(aroundRGB);
    List<Patch> patchList = measureResult.result;
    CheckResult checkResult = checkQualify(patchList, center);
    RGB[] qualifyRGB = checkResult.toRGBArray();
    if (qualifyRGB.length == 0) {
      qualifyRGB = aroundRGB;
    }

    AlgoResult result = uvNearAlgo.getNearestRGB(center, qualifyRGB);
    result.passAllQualify = checkResult.passAllQualify;
    result.allQualifyNonPass = checkResult.allQualifyNonPass;
    return result;
  }

  /**
   * getDelta
   *
   * @param center CIEXYZ
   * @param XYZ CIEXYZ
   * @return double[]
   */
  protected double[] getDelta(CIEXYZ center, CIEXYZ XYZ) {
    throw new UnsupportedOperationException();
  }

  /**
   * getIndex
   *
   * @param center CIEXYZ
   * @param around CIEXYZ
   * @return double
   */
  protected double getIndex(CIEXYZ center, CIEXYZ around) {
    throw new UnsupportedOperationException();
  }

  public static void main(String[] args) {
  }
}
