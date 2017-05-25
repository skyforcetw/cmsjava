package vv.cms.lcd.calibrate.measured.find;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.depend.RGBBase.*;
import shu.cms.colorspace.depend.DeviceDependentSpace.MaxValue;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import vv.cms.lcd.calibrate.measured.algo.*;
import vv.cms.lcd.calibrate.measured.util.*;
import shu.cms.lcd.material.*;
import vv.cms.lcd.material.*;

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
public class IndependentFindingThread
    extends CIEuv1960FindingThread {
  /**
   * IndependentFindingThread
   *
   * @param index int
   * @param targetMaxValue MaxValue
   * @param info CalibratedInfo
   * @param calibrated boolean[]
   * @param luminanceCalibrate boolean
   * @param chromaticCalibrate boolean
   * @param white CIEXYZ
   * @param accessIF CalibratorAccessIF
   */
  public IndependentFindingThread(int index, MaxValue targetMaxValue,
                                  FindingInfo info,
                                  boolean[] calibrated,
                                  boolean luminanceCalibrate,
                                  boolean chromaticCalibrate,
                                  CIEXYZ white, CalibratorAccessIF accessIF
      ) {
    super(index, targetMaxValue, info, calibrated, luminanceCalibrate,
          chromaticCalibrate, false, white, accessIF, false);
  }

  public void run() {
    if (calibrated[index] == true) {
      addTrace("(" + index + ") calibrated, stop calibrate");
      return;
    }
    CIExyY targetxyY = access.getTargetxyY(index);
    plot(targetxyY);

    //========================================================================
    // ��l��
    //========================================================================
    //�ؼ��I
    final CIEXYZ targetXYZ = targetxyY.toXYZ();
    //�]�w��lstep
    info.setStep(index, access.getInitStep());
    //�̤p��step
    double minstep = targetMaxValue.getStepIn255();
    //========================================================================

    //�H���P���C�C�G��
    AlgoResult result = findNearestResultInLoop(minstep, targetXYZ,
                                                chromaticInStep,
                                                luminanceInStep);

    //========================================================================
    // �����ʧ@
    //========================================================================
    terminate(targetXYZ, result);
    //========================================================================
  }

  private boolean cubeCalibrateFinal = AutoCPOptions.get(
      "FindingThread_CubeCalibrateFinal");
  private boolean luminanceCalibrateFinal = AutoCPOptions.get(
      "FindingThread_LuminanceCalibrateFinal");
  private boolean chromaticInStep = AutoCPOptions.get(
      "FindingThread_ChromaticInStep");
  private boolean luminanceInStep = AutoCPOptions.get(
      "FindingThread_LuminanceInStep");
  /**
   * �̫�A���@��check(����ĳ�ϥ�)
   */
  private boolean finalCheck = AutoCPOptions.get(
      "FindingThread_FinalCheck");
  public LCDModel model;

  /**
   * �M��y�{:
   * 1. ���̱������
   * 2. ���̱�����
   * 3. �A���̱������
   * 4. ����2/3����L�{����쪺rgb�}�l������
   *
   * @param targetXYZ CIEXYZ
   * @param startRGB RGB
   * @param step double
   * @param chromaticInStep boolean ��״M��L�{���u��@��step(�_�h�N�O�������w��)
   * @param luminanceInStep boolean ���״M��L�{���u��@��step(�_�h�N�O�������w��)
   * @return Result
   */
  protected AlgoResult findNearestRGB(final CIEXYZ targetXYZ,
                                      final RGB startRGB,
                                      final double step,
                                      boolean chromaticInStep,
                                      boolean luminanceInStep) {
    return findNearestRGB(targetXYZ, startRGB, step, chromaticInStep,
                          luminanceInStep, luminanceCalibrateFinal,
                          cubeCalibrateFinal);
  }

  /**
   * �M��y�{:
   * 1. ���̱������
   * 2. ���̱�����
   * 3. �A���̱������
   * 4. ����2/3����L�{����쪺rgb�}�l������
   *
   * @param targetXYZ CIEXYZ
   * @param startRGB RGB
   * @param step double
   * @param chromaticInStep boolean ��״M��L�{���u��@��step(�_�h�N�O�������w��)
   * @param luminanceInStep boolean ���״M��L�{���u��@��step(�_�h�N�O�������w��)
   * @param luminanceCalibrateFinal boolean �̫᪺�M��O�H����(�_�h�N�O���)
   * @param cubeCalibrateFinal boolean �̫�Hcube search���̫�ե�
   * @return AlgoResult
   */
  protected AlgoResult findNearestRGB(final CIEXYZ targetXYZ,
                                      final RGB startRGB,
                                      final double step,
                                      boolean chromaticInStep,
                                      boolean luminanceInStep,
                                      boolean luminanceCalibrateFinal,
                                      boolean cubeCalibrateFinal) {

    if (findByModel && model != null) {
      RGB rgb = model.getRGB(targetXYZ, false);
      rgb.quantization(RGB.MaxValue.Int10Bit);
      AlgoResult result = new AlgoResult(rgb, null, new RGB[0], null, index, 0);
      return result;
    }

    RGB nearestRGB = startRGB;
    RGB luminanceNearestRGB = null;
    RGB chromaticNearestRGB = null;
    DuplicateLinkedList<RGB>
        list = new DuplicateLinkedList<RGB> (nearestRGB);
    DuplicateLinkedList<RGB>
        totallist = new DuplicateLinkedList<RGB> ();
    /**
     * list�Pcycle���ƪ��t����:
     * �p�Glist����, �N��cycle�̫�C����쪺���@��.
     * �p�Gcycle����, �N��@��cycle��, ���׬O ������B������ ��ض���, �w�g�����Ƨ�쪺���p.
     *         list
     *          T F
     * cycle  T 1 2
     *        F 3 4
     *
     * list��cycle�`�@�|���|�ت��p, ��N�q�p�U:
     * 1. �@round��@cycle�������ƪ����� (�@round�tn��cycle)
     * 2.
     */
    DuplicateLinkedList<RGB>
        cycle = new DuplicateLinkedList<RGB> (nearestRGB);
    AlgoResult result = null;

    //==========================================================================
    // ���ץ��M��
    //==========================================================================
    if (luminanceCalibrate) {
      //�����̱������
      result = luminanceIterative(nearestRGB, targetXYZ, step, false,
                                  finalCheck);
      nearestRGB = result.nearestRGB;
      totallist.addAll(result.totalList);
      cycle.add(nearestRGB);
      plot(result, Color.red);
    }
    //==========================================================================

    // �O�_���m�׮ե�
    boolean chromaticCalibrated = false;
    // �O�_�����׮ե�
    boolean luminanceCalibrated = false;
    // �O�_�����ƪ�case
    boolean duplicateCase = false;
    // �O�_�O�S���i��ե���case
    boolean nonCalibratedCase = false;
    StringBuilder listbuf = new StringBuilder();

    do {
      //========================================================================
      // ���פ@��
      //========================================================================
      if (chromaticCalibrate) {
        chromaticNearestRGB = nearestRGB;
        result = chromaticIterative(nearestRGB, targetXYZ, step,
                                    chromaticInStep, finalCheck);
        chromaticCalibrated = !chromaticNearestRGB.equals(result.nearestRGB);
        chromaticNearestRGB = result.nearestRGB;
        nearestRGB = chromaticNearestRGB;
        totallist.addAll(result.totalList);
        cycle.add(nearestRGB);
        addTrace("(" + index +
                 ") chromatic calibrate nearestRGB: " + nearestRGB);
      }
      //========================================================================

      //========================================================================
      // �A���̱������
      //========================================================================
      if (luminanceCalibrateFinal && luminanceCalibrate) {
        luminanceNearestRGB = nearestRGB;
        result = luminanceIterative(nearestRGB, targetXYZ, step,
                                    luminanceInStep, finalCheck);
        luminanceCalibrated = !luminanceNearestRGB.equals(result.nearestRGB);
        luminanceNearestRGB = result.nearestRGB;
        nearestRGB = luminanceNearestRGB;
        totallist.addAll(result.totalList);
        cycle.add(nearestRGB);
        addTrace("(" + index +
                 ") luminance calibrate nearestRGB: " + nearestRGB);
        plot(result, Color.red);
      }
      //========================================================================

      //�@�Ӵ`��
      list.add(nearestRGB);
      duplicateCase = list.duplicate(); //|| cycleDuplicateCase;
      if (list.duplicate()) {
        //�p�Glist�����e�}�l����
        duplicateLog(listbuf, list, cycle);
      }

      nonCalibratedCase = ! (chromaticCalibrated || luminanceCalibrated);
    }
    //�⦸�H�W���j�O�ۦP���G �N����, �Ϊ̨�ӳ��S���ե���, �]����
    while (! (duplicateCase || nonCalibratedCase));

    String situation = getSituation(duplicateCase, nonCalibratedCase,
                                    chromaticCalibrated, luminanceCalibrated);
    listbuf.insert(0, situation);
    addTrace(listbuf.toString());

    //==========================================================================
    // �qcube�ˬd�O���O�̾a��ؼЪ�
    //==========================================================================
    AlgoResult cubeResult = cubeCheckInOneJNDI(nearestRGB, targetXYZ, step);
    //==========================================================================
    if (cubeCalibrateFinal && cubeResult != null) {
      nearestRGB = cubeResult.nearestRGB;
    }
    result.setInfomation(nearestRGB, list, totallist);
    result.candilateNearestRGB = (cubeResult != null) ? cubeResult.nearestRGB :
        null;
    return result;
  }

  /**
   * �p�Glist�����e�}�l����, �N���ФF�����x�s�_��
   * @param listbuf StringBuilder
   * @param list DuplicateLinkedList
   * @param cycle DuplicateLinkedList
   */
  private void duplicateLog(StringBuilder listbuf,
                            DuplicateLinkedList<RGB> list,
      DuplicateLinkedList<RGB> cycle
      ) {
    //======================================================================
    // �N���ФF�����x�s�_��
    //======================================================================
    listbuf.append("\nlist[" + list.duplicate(true) + "] cycle[" +
                   cycle.duplicate(true) + "]");
    listbuf.append("\nlist(" + list.size() + "): ");
    for (RGB rgb : list) {
      listbuf.append(rgb.toString());
      listbuf.append(' ');
    }
    listbuf.append("\ncycle(" + cycle.size() + "): ");
    for (RGB rgb : cycle) {
      listbuf.append(rgb.toString());
      listbuf.append(' ');
    }
    //======================================================================
  }

  private String getSituation(boolean duplicateCase, boolean nonCalibratedCase,
                              boolean chromaticCalibrated,
                              boolean luminanceCalibrated) {
    String situation = "(" + index + ") stop result: duplicate[" +
        (duplicateCase ? 'O' : 'X') +
        "] nonCalibrated[" + (nonCalibratedCase ? 'O' : 'X') +
        "] (chromatic[" + (chromaticCalibrated ? 'O' : 'X') +
        "] luminance[" + (luminanceCalibrated ? 'O' : 'X') +
        "]) (chromaticCalibrate[" + (chromaticCalibrate ? 'O' : 'X') +
        "] luminanceCalibrate[" + (luminanceCalibrate ? 'O' : 'X') + "])";
    return situation;
  }

}
