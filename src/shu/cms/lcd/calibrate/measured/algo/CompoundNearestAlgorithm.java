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
 * �ƦX�Ҷq���̪�RGB���t��k
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
   * �T�{��檺�ɭԬO�_�n�Ҷqv�H��
   */
  private boolean qualifyVQuadrant = true;
  /**
   * �T�{��檺�ɭԬO�_�n�Ҷqu�H��
   */
  private boolean qualifyUQuadrant = true;
  /**
   * �T�{��檺�ɭ�, �O�_�n�Ҷqdv<du
   */
  private boolean qualifydvLessThandu = false;
  /**
   * �T�{��檺�ɭ�, �O�_�n�Ҷqdelta JNDI
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
    // �ˬd�Ҧ����
    //==========================================================================
    int size = patchList.size();
    boolean[][] qualifyArray = new boolean[QualifyItems][size];
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      boolean[] qualify = getQualify(p.getXYZ(), center);
      //��m
      for (int y = 0; y < QualifyItems; y++) {
        qualifyArray[y][x] = qualify[y];
      }
    }
    //==========================================================================

    //==========================================================================
    // �Ӷ����ˬdqualify�O�_���涰
    //==========================================================================
    boolean[] qualify = qualifyArray[0];
    boolean passAllQualify = true;
    for (int x = 1; x < QualifyItems; x++) {
      //�Ӷ��Ǥ@�Ӥ@���ˬd���P��qualify
      boolean[] test = qualifyArray[x];
      boolean[] result = intersection(qualify, test);
      if (Utils.or(result)) {
        //�N���涰
        qualify = result;
      }
      else {
        //�N��S�涰, �����~���ˬd�U�h�F
        passAllQualify = false;
        break;
      }
    }
    //==========================================================================

    //==========================================================================
    // ��q�L��patch�o�X��
    //==========================================================================
    List<Patch> result = new ArrayList<Patch> ();
    boolean allQualifyNonPass = false;

    if (Utils.or(qualify)) {
      //qualify�����G�Otrue��, �N��ܤ֤@�ӬOtrue
      for (int x = 0; x < size; x++) {
        boolean b = qualify[x];
        if (b) {
          Patch p = patchList.get(x);
          result.add(p);
        }
      }
    }
    else {
      //���G�Ofalse, �N��@��true���S��=.=
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

    // �ŦX�n�D��XYZ�MRGB
    List<CIEXYZ> qualifyXYZList = Patch.Filter.XYZList(filterList);
    CIEXYZ[] qualifyXYZArray = qualifyXYZList.toArray(new CIEXYZ[qualifyXYZList.
        size()]);

    //==========================================================================
    // ���̤p��delta JNDI
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

    //�ŦX�n�D��RGB
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
   * �^��qualify�����
   * @param XYZ CIEXYZ
   * @param center CIEXYZ
   * @return boolean[] 1. duv�n�p��deltauvPrimeTolerance
   * 2. dv��>0 or <0 ���Ŧⰾ�����n�t�Ӧh.
   * 3. delta JNDI <= deltaJNDITolerance
   * 4. du��>0 or <0
   * 5. dv<du(�Ŧ⪺�����n�����p, ���F�Ŧ�smooth�۷Q)
   */
  public boolean[] getQualify(CIEXYZ XYZ, CIEXYZ center) {
    boolean[] qualify = new boolean[QualifyItems];

    //==========================================================================
    // duv�n�p��deltauvPrimeTolerance
    //==========================================================================
    double[] duvp = MeasuredUtils.getDeltauvPrime(white, XYZ);
    double[] absduvp = DoubleArray.copy(duvp);
    DoubleArray.abs(absduvp);
    qualify[0] = (absduvp[0] < deltauvPrimeTolerance &&
                  absduvp[1] < deltauvPrimeTolerance);
    //==========================================================================

    //==========================================================================
    // dv��>0 or <0 ���Ŧⰾ�����n�t�Ӧh.
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
    // du��>0 or <0
    //==========================================================================
    qualify[3] = qualifyUQuadrant ? quadrant.isUQualified(duvp[0]) : true;
    //==========================================================================

    //==========================================================================
    // dv<du(�Ŧ⪺�����n�����p, ���F�Ŧ�smooth�۷Q)
    //==========================================================================
    qualify[4] = qualifydvLessThandu ? (absduvp[1] < absduvp[0]) : true;
    //==========================================================================

    return qualify;
  }

  public void setDeltauvPrimeTolerance(double deltauvPrimeTolerance) {
    this.deltauvPrimeTolerance = deltauvPrimeTolerance;
  }

}
