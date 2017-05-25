package shu.cms.lcd.calibrate.measured.algo;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.calibrate.measured.util.*;
import shu.cms.measure.cp.*;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 複合考量找到最近RGB的演算法
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CompoundNearestAlgorithm
    extends NearestAlgorithm {

  public CompoundNearestAlgorithm(CIEXYZ white, MeasureInterface mi,
                                  JNDIInterface jndiIF,
                                  DeltauvQuadrant quadrant) {
    super(white, mi);
    this.lAlgo = new LightnessNearestAlgorithm(white, mi, jndiIF);
    this.quadrant = quadrant;
  }

  private double deltaJNDITolerance = 1.0;
  private double deltauvPrimeTolerance = 0.001;
  private DeltauvQuadrant quadrant;
  private LightnessNearestAlgorithm lAlgo;

  /**
   * 確認資格的時候是否要考量v象限
   */
  private boolean qualifyVQuadrant = true;
  /**
   * 確認資格的時候是否要考量u象限
   */
  private boolean qualifyUQuadrant = true;
  /**
   * 確認資格的時候, 是否要考量dv<du
   */
  private boolean qualifydvLessThandu = false;
  /**
   * 確認資格的時候, 是否要考量delta JNDI
   */
  private boolean qualifydJNDI = true;

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

  protected CheckResult checkQualify(List<Patch> patchList, CIEXYZ center) {
    //==========================================================================
    // 檢查所有資格
    //==========================================================================
    int size = patchList.size();
    boolean[][] qualifyArray = new boolean[QualifyItems][size];
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      boolean[] qualify = getQualify(p.getXYZ(), center);
      //轉置
      for (int y = 0; y < QualifyItems; y++) {
        qualifyArray[y][x] = qualify[y];
      }
    }
    //==========================================================================

    //==========================================================================
    // 照順序檢查qualify是否有交集
    //==========================================================================
    boolean[] qualify = qualifyArray[0];
    boolean passAllQualify = true;
    for (int x = 1; x < QualifyItems; x++) {
      //照順序一個一個檢查不同的qualify
      boolean[] test = qualifyArray[x];
      boolean[] result = intersection(qualify, test);
      if (Utils.or(result)) {
        //代表有交集
        qualify = result;
      }
      else {
        //代表沒交集, 不用繼續檢查下去了
        passAllQualify = false;
        break;
      }
    }
    //==========================================================================

    //==========================================================================
    // 把通過的patch濾出來
    //==========================================================================
    List<Patch> result = new ArrayList<Patch> ();
    boolean allQualifyNonPass = false;

    if (Utils.or(qualify)) {
      //qualify的結果是true的, 代表至少一個是true
      for (int x = 0; x < size; x++) {
        boolean b = qualify[x];
        if (b) {
          Patch p = patchList.get(x);
          result.add(p);
        }
      }
    }
    else {
      //結果是false, 代表一個true都沒有=.=
      result.addAll(patchList);
      allQualifyNonPass = true;
    }
    //==========================================================================

    CheckResult checkResult = new CheckResult(result, passAllQualify,
                                              allQualifyNonPass);
    return checkResult;
  }

  protected final static boolean[] intersection(boolean[] bool1,
                                                boolean[] bool2) {
    if (bool1.length != bool2.length) {
      throw new IllegalArgumentException("bool1.length != bool2.length");
    }
    int size = bool1.length;
    boolean[] result = new boolean[size];
    for (int x = 0; x < size; x++) {
      result[x] = bool1[x] && bool2[x];
    }
    return result;
  }

  /**
   * getNearestRGB
   *
   * @param center CIEXYZ
   * @param aroundRGB RGB[]
   * @return Result
   */
  public AlgoResult getNearestRGB(CIEXYZ center, RGB[] aroundRGB) {
    MeasureResult measureResult = getMeasureResult(aroundRGB);
    List<Patch> patchList = measureResult.result;

    CheckResult checkResult = checkQualify(patchList, center);
    List<Patch> filterList = checkResult.patchList;

    // 符合要求的XYZ和RGB
    List<CIEXYZ> qualifyXYZList = Patch.Filter.XYZList(filterList);
    CIEXYZ[] qualifyXYZArray = qualifyXYZList.toArray(new CIEXYZ[qualifyXYZList.
        size()]);

    //==========================================================================
    // 找到最小的delta JNDI
    //==========================================================================
    int size = filterList.size();
    double[] dist = new double[size];
    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = qualifyXYZList.get(x);
      double[] dJNDI = lAlgo.getDelta(center, XYZ);
      dist[x] = Math.abs(dJNDI[0]);
    }
    int index = Maths.minIndex(dist);
    //==========================================================================

    List<RGB> qualifyRGBList = Patch.Filter.rgbList(filterList);
    RGB[] qualifyRGBArray = RGBArray.toRGBArray(qualifyRGBList);

    //符合要求的RGB
    RGB rgb = filterList.get(index).getRGB();
    AlgoResult result = new AlgoResult(rgb, dist, qualifyRGBArray,
                                       qualifyXYZArray,
                                       index,
                                       measureResult.practicalMeasureCount);
    result.passAllQualify = checkResult.passAllQualify;
    result.allQualifyNonPass = checkResult.allQualifyNonPass;
    return result;
  }

  protected double getIndex(CIEXYZ center, CIEXYZ around) {
    throw new UnsupportedOperationException();
  }

  private final static int QualifyItems = 5;

  /**
   * 回傳qualify的資格
   * @param XYZ CIEXYZ
   * @param center CIEXYZ
   * @return boolean[] 1. duv要小於deltauvPrimeTolerance
   * 2. dv恆>0 or <0 讓藍色偏移不要差太多.
   * 3. delta JNDI <= deltaJNDITolerance
   * 4. du恆>0 or <0
   * 5. dv<du(藍色的偏移要比紅色小, 為了藍色smooth著想)
   */
  public boolean[] getQualify(CIEXYZ XYZ, CIEXYZ center) {
    boolean[] qualify = new boolean[QualifyItems];

    //==========================================================================
    // duv要小於deltauvPrimeTolerance
    //==========================================================================
    double[] duvp = MeasuredUtils.getDeltauvPrime(white, XYZ);
    double[] absduvp = DoubleArray.copy(duvp);
    DoubleArray.abs(absduvp);
    qualify[0] = (absduvp[0] < deltauvPrimeTolerance &&
                  absduvp[1] < deltauvPrimeTolerance);
    //==========================================================================

    //==========================================================================
    // dv恆>0 or <0 讓藍色偏移不要差太多.
    //==========================================================================
    qualify[1] = qualifyVQuadrant ? quadrant.isVQualified(duvp[1]) : true;
    //==========================================================================

    //==========================================================================
    // delta JNDI <= deltaJNDITolerance
    //==========================================================================
    double[] dJNDI = lAlgo.getDelta(center, XYZ);
    qualify[2] = qualifydJNDI ? (Math.abs(dJNDI[0]) <= deltaJNDITolerance) : true;
    //==========================================================================

    //==========================================================================
    // du恆>0 or <0
    //==========================================================================
    qualify[3] = qualifyUQuadrant ? quadrant.isUQualified(duvp[0]) : true;
    //==========================================================================

    //==========================================================================
    // dv<du(藍色的偏移要比紅色小, 為了藍色smooth著想)
    //==========================================================================
    qualify[4] = qualifydvLessThandu ? (absduvp[1] < absduvp[0]) : true;
    //==========================================================================

    return qualify;
  }

  public void setDeltauvPrimeTolerance(double deltauvPrimeTolerance) {
    this.deltauvPrimeTolerance = deltauvPrimeTolerance;
  }

}
