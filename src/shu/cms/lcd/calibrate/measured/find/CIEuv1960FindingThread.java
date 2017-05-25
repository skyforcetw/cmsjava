package shu.cms.lcd.calibrate.measured.find;

import java.text.*;
import java.util.*;
import java.util.List;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.depend.RGBBase.*;
import shu.cms.colorspace.depend.DeviceDependentSpace.MaxValue;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.calibrate.measured.algo.*;
import shu.cms.lcd.calibrate.measured.util.*;
import shu.cms.lcd.material.*;
import shu.cms.util.*;
import shu.math.array.*;
import shu.util.*;
import shu.util.log.*;

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
public class CIEuv1960FindingThread
    extends Thread {

  /**
   * �O�_�n��C�@�B��plot�X��
   */
  private boolean plotStep = AutoCPOptions.get("FindingThread_PlotStep");
  /**
   * ���Ѯե���T������
   */
  protected CalibratorAccessIF access;
  /**
   * ���I
   */
  private CIEXYZ white;
  /**
   * �O�_�n�i���׮ե�
   */
  protected boolean chromaticCalibrate = true;
  /**
   * �O�_�n�i��G�׮ե�
   */
  protected boolean luminanceCalibrate = true;
  /**
   * �b�̫�@����Around�j�M���ɭ�, ��cube�X�i
   */
  private boolean cubeAroundMode = true;

  public CIEuv1960FindingThread(int index, MaxValue targetMaxValue,
                                FindingInfo info, boolean[] calibrated,
                                boolean luminanceCalibrate,
                                boolean chromaticCalibrate,
                                boolean cubeAroundMode, CIEXYZ white,
                                CalibratorAccessIF accessIF, boolean plot) {
    this.index = index;
    this.targetMaxValue = targetMaxValue;
    this.calibrated = calibrated;
    this.access = accessIF;
    initAlgorithm(access);
    this.luminanceCalibrate = luminanceCalibrate;
    this.chromaticCalibrate = chromaticCalibrate;
    this.cubeAroundMode = cubeAroundMode;
    this.white = white;

    this.info = info;
    this.plotting = plot;
    if (plot) {
      p = new FindingPlotter("index: " + Integer.toString(index));
      if (plotStep) {
        pstep = new FindingPlotter("index: " + Integer.toString(index) +
                                   " step");
      }
    }
  }

  protected boolean plotting = false;

  protected void initAlgorithm(CalibratorAccessIF c) {
    this.deltaENearAlgo = c.getDeltaE00NearestAlogorithm();
    this.lightnessNearAlgo = c.getLightnessNearestAlogorithm();
    this.uvNearAlgo = c.getCIEuv1960NearestAlogorithm();
    this.lightnessAroundAlgo = c.getLightnessAroundAlgorithm();
    this.chromaAroundAlgo = c.getChromaticAroundAlgorithm();
    this.compoundNearAlogo = c.getCompoundNearestAlogorithm();
    this.stepAroundAlgo = c.getStepAroundAlgorithm();
    this.cubeNearAlgo = c.getCubeNearestAlgorithm();
  }

  private DeltaE00NearestAlgorithm deltaENearAlgo;
  private LightnessNearestAlgorithm lightnessNearAlgo;
  private CIEuv1960NearestAlgorithm uvNearAlgo;
  private CompoundNearestAlgorithm compoundNearAlogo;
  private CubeNearestAlgorithm cubeNearAlgo;

  private LightnessAroundAlgorithm lightnessAroundAlgo;
  private ChromaticAroundAlgorithm chromaAroundAlgo;
  private StepAroundAlgorithm stepAroundAlgo;

  /**
   * ��plotter����, �N�O��ø������
   */
  public void closePlotter() {
    if (plotting) {
      p.close();
      if (plotStep) {
        pstep.close();
      }
    }
  }

  /**
   * �ե�����T
   */
  protected FindingInfo info;
  /**
   * �Χ@ø�ϥΪ�����
   */
  private FindingPlotter pstep, p;

  /**
   * �ثe�ե������ޭ�
   */
  protected int index;
  /**
   * �ؼЪ�maxValue
   */
  protected MaxValue targetMaxValue;
  /**
   * �ΨӧP�_�O�_�w�g���F�ե�
   */
  protected boolean[] calibrated;

  /**
   * �p��ե����Ѧ�index
   * @param targetXYZ CIEXYZ
   * @param result Result
   */
  private void calculateIndex(CIEXYZ targetXYZ, AlgoResult result) {
    CIEXYZ XYZ = result.getNearestXYZ();
    //��t
    double deltaE = deltaENearAlgo.getDelta(targetXYZ, XYZ)[0];
    //JNDI�t
    double deltaJNDI = Math.abs(lightnessNearAlgo.getDelta(targetXYZ, XYZ)[0]);
    //u'v'�t
    double[] duvp = uvNearAlgo.getDelta(targetXYZ, XYZ);
    info.setIndex(index, result.getIndex(), deltaE, deltaJNDI, duvp);
  }

  protected void plot(CIExyY targetxyY) {
    if (plotting) {
      //========================================================================
      //�ХX�ؼ��I
      //========================================================================
      if (plotStep) {
        pstep.plotTargetAtuv(targetxyY.getuvPrimeYValues());
      }
      p.plotTargetAtuv(targetxyY.getuvPrimeYValues());
      //========================================================================
    }
  }

  private StringBuilder traceBuf = new StringBuilder();
  public String getTrace() {
    return traceBuf.toString();
  }

  private boolean enableTrace = true;

  protected void addTrace(String msg) {
    if (enableTrace) {
      traceBuf.append(msg);
      traceBuf.append('\n');
    }
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
    info.setStep(index, RGB.MaxValue.Int8Bit);
    //�̤p��step
    double minstep = targetMaxValue.getStepIn255();
    //========================================================================

    //�H���P���C�C�G��
    AlgoResult result = findNearestResultInLoop(minstep, targetXYZ, true, false);

    //========================================================================
    // ���̪��I��, ��X���I�P���I.
    // �ݬݩP���I����, �̱����I�O���O�u�����ؼ��I�̪�.
    //========================================================================
    AlgoResult r1 = getNearestRGBInAround(result.nearestRGB, targetXYZ, minstep);
    addTrace("(" + index + ") find rgb in around:" + r1.nearestRGB +
             " (dist:" + r1.getIndex() + ")");
    //========================================================================

    //========================================================================
    // �q�L�{�����ŦX���I
    //========================================================================
    //��around�M�M��L�{����RGB����z�_��
    List<RGB> totalList = new LinkedList<RGB> (r1.totalList);
    totalList.addAll(result.totalList);

    //�q���L�����{�����ŦX�ƦX�n�D��rgb�I
    result = getNearestRGBInCompound(targetXYZ, totalList);
    //========================================================================

    //========================================================================
    // �����ʧ@
    //========================================================================
    terminate(targetXYZ, result);
    //========================================================================
  }

  /**
   * �H�j�餣�_�Y�pstep, ���̱��񪺵��G
   * @param minstep double
   * @param targetXYZ CIEXYZ
   * @param chromaticInStep boolean ��ת��M��C���Ȥ@step(�_�h�N�O�@����w��)
   * @param luminanceInStep boolean �G�ת��M��C���Ȥ@step(�_�h�N�O�@����w��)
   * @return Result
   */
  protected AlgoResult findNearestResultInLoop(final double minstep,
                                               final CIEXYZ targetXYZ,
                                               boolean chromaticInStep,
                                               boolean luminanceInStep) {
    AlgoResult result = null;
    RGB nearestRGB = info.getCalibratedRGB(index);

    //========================================================================
    // �H���P���C�C�G��
    //========================================================================
    for (double step = 1; step >= minstep; step /= 2.) {
      addTrace("(" + index + ") start calibrate, in step " + step);
      //���̱���RGB�����G
      result = findNearestRGB(targetXYZ, nearestRGB, step, chromaticInStep,
                              luminanceInStep);
      //��s�̱���RGB
      nearestRGB = result.nearestRGB;
      if (!findByModel) {
        addTrace("(" + index + ") find rgb:" + nearestRGB +
                 ", in step " + step + " (dist:" + result.getIndex() + ")");
      }
      //�p��X��Ӫ�step
      RGB.MaxValue nowStep = RGB.MaxValue.getIntegerMaxValueByMax( (int) (RGB.
          MaxValue.Int8Bit.max / step));
      //��sstep
      info.setStep(index, nowStep);
    }
    //========================================================================

    nearestRGB = null;
    return result;
  }

  /**
   * ������model�w��, �Ӥ��ĥΥ���find algo.
   */
  public boolean findByModel = false;

  protected void terminate(CIEXYZ targetXYZ, AlgoResult result) {
    if (true == findByModel) {
      //�ե��Ҩ��o��RGB
      info.setCalibratedRGB(index, result.nearestRGB);
      //�Щw�w�g�ե�ok
      calibrated[index] = true;
    }
    else {
      //========================================================================
      // �����ʧ@
      //========================================================================
      //�p����ޭ�
      calculateIndex(targetXYZ, result);
      //�ե��Ҩ��o��RGB
      info.setCalibratedRGB(index, result.nearestRGB);
      if (result.candilateNearestRGB != null) {
        info.setCandilateCalibratedRGB(index, result.candilateNearestRGB);
      }
      //�Щw�w�g�ե�ok
      calibrated[index] = true;
      //========================================================================
      addTrace("(" + index + ") calibrate end (rgb:" + result.nearestRGB +
               ") (dist:" + result.getIndex() + ")");
      double deltaE = info.getDeltaEIndexArray()[index];
      double dJNDI = info.getDeltaJNDIIndexArray()[index];
      double[] duv = info.getDeltaunvpIndexArray()[index];
      addTrace("(" + index + ") deltaE: " + df4.format(deltaE) + " deltaJNDI: " +
               df4.format(dJNDI) + " deltauv': " +
               DoubleArray.toString(df4, duv));

      access.trace(traceBuf.toString());
    }
  }

  private final static DecimalFormat df4 = new DecimalFormat("####.####");

  protected void plotStep(AlgoResult result, Color color) {
    if (this.plotting && plotStep) {
      pstep.plot(result, color);
    }
  }

  protected void plot(AlgoResult result, Color color) {
    if (this.plotting) {
      p.plot(result, color);
    }
  }

  /**
   * �G�ת����N�M��
   * �̷�deltaJNDI, �վ���G��RGB, RGB���վ���Hwhite����¦,
   *  �]�N�Or/g/b�P�ɥ[��@�ӳ�찵�վ�
   * @param initRGB RGB
   * @param targetXYZ CIEXYZ
   * @param step double
   * @param runonce boolean
   * @param finalCheck boolean �̫�A���@��check
   * @return Result
   */
  protected final AlgoResult luminanceIterative(final RGB initRGB,
                                                final CIEXYZ targetXYZ,
                                                double step, boolean runonce,
                                                boolean finalCheck) {
    addTrace("(" + index + ") start luminance calibrate");
    RGB nearestRGB = initRGB;
    DuplicateLinkedList<RGB> list = new DuplicateLinkedList<RGB> (nearestRGB);
    DuplicateLinkedList<RGB> totallist = new DuplicateLinkedList<RGB> ();
    AlgoResult result = null;
    int redundantMeasure = 0;

    do {
      //�p��Xdelta
      double[] delta = lightnessNearAlgo.getDelta(targetXYZ, nearestRGB);
      //�̷�delta, ���ͧ�G��aroundRGB
      RGB[] aroundRGB = lightnessAroundAlgo.getAroundRGB(nearestRGB, delta,
          step);

      //========================================================================
      // result�B�z
      //========================================================================
      //�G�ק��̪�(�HJNDI���p��)
      result = lightnessNearAlgo.getNearestRGB(targetXYZ, aroundRGB);
      redundantMeasure += result.getRedundantMeasure(nearestRGB);
      nearestRGB = result.nearestRGB;
      //========================================================================
      list.add(nearestRGB);
      totallist.addAll(result.totalList);
      plotStep(result, Color.black);
      //========================================================================

      info.addCalibrateCount();
      addTrace("(" + index + ") " +
               Arrays.toString(aroundRGB) + " near:" + nearestRGB);
    }
    //�p�G����쭫�ƪ� �N����
    while (!list.duplicate() && !runonce);

    if (finalCheck) {
      //�A��delta JNDI�P�_�X�~�t�̤p��
      result = lightnessNearAlgo.getNearestRGB(targetXYZ, list,
                                               NearestRangeCheck);
      result.setInfomation(initRGB, list, totallist);
    }
    addTrace("(" + index + ") luminance calibrate end (dist:" +
             result.getIndex() + ")");
    result.setRedundantMeasure(redundantMeasure);
    this.redundantMeasure += redundantMeasure;
    return result;
  }

  protected int redundantMeasure;

  /**
   * �u�ˬd�̱���X�Ӫ��d��(0���ܫh�N����ˬd)
   */
  private final static int NearestRangeCheck = 0;

  /**
   * ��ת����N�M��
   * @param initRGB RGB ��l��RGB
   * @param targetXYZ CIEXYZ �ؼ�XYZ
   * @param step double ���N�����
   * @param runonce boolean �O�_�u�]�@���M��
   * @param finalCheck boolean �̫�A���@��check
   * @return Result
   */
  protected final AlgoResult chromaticIterative(final RGB initRGB,
                                                CIEXYZ targetXYZ,
                                                double step, boolean runonce,
                                                boolean finalCheck) {
    addTrace("(" + index + ") start chromatic calibrate");
    RGB nearestRGB = initRGB;
    DuplicateLinkedList<RGB>
        list = new DuplicateLinkedList<RGB> (nearestRGB);
    DuplicateLinkedList<RGB>
        totallist = new DuplicateLinkedList<RGB> ();
    CIEuv1960NearestAlgorithm nearAlgo = uvNearAlgo;
    AlgoResult result = null;
    int redundantMeasure = 0;

    do {
      //�p��Xdelta u'v'
      double[] delta = uvNearAlgo.getDelta(targetXYZ, nearestRGB);
      //��du'v'�ӱ���̦��i��G��ؼ��I��RGB�զX
      RGB[] aroundRGB = chromaAroundAlgo.getAroundRGB(nearestRGB, delta, step);

      //========================================================================
      // result�B�z
      //========================================================================
      // ����׮y�ФW�̱��񪺸�
      result = nearAlgo.getNearestRGB(targetXYZ, aroundRGB);
      redundantMeasure += result.getRedundantMeasure(nearestRGB);
      //��ק��̪�
      nearestRGB = result.nearestRGB;
      //========================================================================
      list.add(nearestRGB);
      totallist.addAll(result.totalList);
      plotStep(result, Color.green);
      //========================================================================

      info.addCalibrateCount();
      addTrace("(" + index + ") " +
               Arrays.toString(aroundRGB) + " near:" + nearestRGB);
      /**
       * ���F��ק��̪񪺥H�~, ��Ѧҥլۤ�, delta u'v'�٭n�O���ۦP���t��
       * �ܩ�n���٬O�n�t, �n�p��ѦҥջP���I�өw�X��
       */
    }
    //�p�G����쭫�ƪ� �N����, �Ϊ̥u�ݭn�]�@�� �]����
    while ( (!list.duplicate()) && !runonce);

    if (finalCheck) {
      //�A�Φ�t�P�_�X�̤p��
      result = nearAlgo.getNearestRGB(targetXYZ, list, NearestRangeCheck);
      result.setInfomation(nearestRGB, list, totallist);
    }
    addTrace("(" + index + ") chromatic calibrate end (dist:" +
             result.getIndex() + ")");
    result.setRedundantMeasure(redundantMeasure);
    this.redundantMeasure += redundantMeasure;
    return result;
  }

  /**
   * �M��y�{:
   * 1. ���̱���G��
   * 2. ���̱�����
   * 3. �A���̱���G��
   * 4. ����2/3����L�{����쪺rgb�}�l������
   *
   * @param targetXYZ CIEXYZ
   * @param rgb RGB
   * @param step double
   * @param chromaticInStep boolean ��״M��L�{���u��@��step(�_�h�N�O�������w��)
   * @param luminanceInStep boolean �G�״M��L�{���u��@��step(�_�h�N�O�������w��)
   * @return Result
   */
  protected AlgoResult findNearestRGB(final CIEXYZ targetXYZ, final RGB rgb,
                                      final double step,
                                      boolean chromaticInStep,
                                      boolean luminanceInStep) {
    RGB nearestRGB = rgb;
    RGB luminanceNearestRGB = null;
    RGB chromaticNearestRGB = null;
    DuplicateLinkedList<RGB>
        list = new DuplicateLinkedList<RGB> (nearestRGB);
    DuplicateLinkedList<RGB>
        totallist = new DuplicateLinkedList<RGB> ();

    NearestAlgorithm nearestAlgo = access.getIndexNearestAlogorithm();

    if (luminanceCalibrate) {
      //�����̱���G��
      AlgoResult result = luminanceIterative(nearestRGB, targetXYZ, step, false, true);
      totallist.addAll(result.totalList);
      nearestRGB = result.nearestRGB;
      list.add(nearestRGB);
      plot(result, Color.red);
    }

    do {

      //========================================================================
      // ���פ@��
      //========================================================================
      if (chromaticCalibrate) {
        AlgoResult result = chromaticIterative(nearestRGB, targetXYZ, step,
                                               chromaticInStep, true);
        totallist.addAll(result.totalList);
        chromaticNearestRGB = result.nearestRGB;
        nearestRGB = chromaticNearestRGB;
        addTrace("(" + index +
                 ") chromatic calibrate nearestRGB: " + nearestRGB);
      }
      //========================================================================

      //========================================================================
      // �A���̱���G��
      //========================================================================
      if (luminanceCalibrate) {
        AlgoResult result = luminanceIterative(nearestRGB, targetXYZ, step,
                                               luminanceInStep, true);
        totallist.addAll(result.totalList);
        luminanceNearestRGB = result.nearestRGB;
        nearestRGB = luminanceNearestRGB;
        addTrace("(" + index +
                 ") luminance calibrate nearestRGB: " + nearestRGB);
        plot(result, Color.red);
      }
      //========================================================================
      list.add(nearestRGB);

    }
    //�⦸�H�W���j�O�ۦP���G �N����
    while (!list.duplicate());
    //��׻P�G�׮ե����G�ۦP �N���� �]���N��ڥ����ݭn�դF, ���ƹ��ҩ��o�˪��覡����

    //�A��t�Z�̤p��
    AlgoResult result = nearestAlgo.getNearestRGB(targetXYZ, list,
                                                  NearestRangeCheck);
    result.setInfomation(nearestRGB, list, totallist);
    nearestRGB = result.nearestRGB;

    addTrace("(" + index + ") findNearestRGB end (dist:" +
             result.getIndex() + ")");

    return result;
  }

  /**
   * �HdeltaE�����ޭȧ��̪�RGB
   * @param targetXYZ CIEXYZ
   * @param rgbList List
   * @return Result
   * @deprecated
   */
  protected AlgoResult getNearestRGBInDeltaE(CIEXYZ targetXYZ, List<RGB>
      rgbList) {
    RGB[] rgbArray = RGBArray.toRGBArray(rgbList);
    AlgoResult result = deltaENearAlgo.getNearestRGB(targetXYZ, rgbArray);
    return result;
  }

  /**
   * �ƦX���Ҷq���U, ���̪�RGB
   * @param targetXYZ CIEXYZ
   * @param rgbList List
   * @return Result
   */
  private AlgoResult getNearestRGBInCompound(CIEXYZ targetXYZ, List<RGB>
      rgbList) {
    RGB[] rgbArray = RGBArray.toRGBArray(rgbList);
    AlgoResult result = compoundNearAlogo.getNearestRGB(targetXYZ, rgbArray);
    if (!result.passAllQualify || result.allQualifyNonPass) {
      //�p�G���O��������泣�q�L �Ϊ̥�������泣�S�q�L, �N�Onon-Qualify
      info.setNonQualify(index);
      Logger.log.info("index(" + index + ") NonQualify: passAllQualify[" +
                      result.passAllQualify + "] allQualifyNonPass[" +
                      result.allQualifyNonPass + "]");
    }
    return result;
  }

  /**
   * �Q��around�X�i���覡(�X�i8���I) �åB���a���̱����I
   * @param initRGB RGB
   * @param targetXYZ CIEXYZ
   * @param step double
   * @return Result
   */
  private AlgoResult getNearestRGBInAround(final RGB initRGB,
                                           final CIEXYZ targetXYZ,
                                           double step) {
    //��l��rgb
    RGB rgb = initRGB;
    //�O�_���̱����I?
    boolean centerNearest = false;
    //�ĥΥߤ����X�i�j�M(27���I)
    boolean cubeSearch = false;
    //�M�䪺���G
    AlgoResult result = null;
    //�M��L�{��rgb
    DuplicateLinkedList<RGB> totalList = new DuplicateLinkedList<RGB> ();

    for (int t = 0; t < MaxAroundIterativeTimes; t++) {
      RGB[] aroundRGB = null;
      if (cubeAroundMode && cubeSearch) {
        //�O�_�n�i��ߤ���j�M? �ߤ���j�M�N�M��27���I
        aroundRGB = stepAroundAlgo.getCubeAroundRGB(rgb, step);
        //�ߤ���j�M����+1
        info.addCubeSearchTimes();
      }
      else {
        //�B�i�j�M
        aroundRGB = stepAroundAlgo.getAroundRGB(rgb, step, true);
      }
      //�N��쪺rgb�����_��
      totalList.addAll(aroundRGB);

      //�u�O���F��aroundXYZ�Ӥw
      CIEXYZ[] aroundXYZ = uvNearAlgo.getNearestRGB(targetXYZ,
          aroundRGB).aroundXYZ;

      if (plotting) {
        //======================================================================
        // ø��
        //======================================================================
        for (int x = 1; x < aroundXYZ.length; x++) {
          p.plot(aroundXYZ[x], Color.cyan);
          p.plotRGB(aroundRGB[x], Color.cyan, null);
        }
        p.plot(aroundXYZ[0], Color.blue);
        p.plotRGB(aroundRGB[0], Color.blue, null);
        //======================================================================
      }
      //��t�̱����I�O�����I? �ΤT�ئ�t�����覡�U�h����
      boolean[] bools = isFirstNearestXYZInDeltaE(targetXYZ, aroundXYZ,
                                                  white);
      //�ܤ֤@�Ӧ�t�������O�̪��I
      centerNearest = Utils.or(bools);

      //���̱���rgb
      result = deltaENearAlgo.getNearestRGB(targetXYZ, aroundRGB);

      if (cubeSearch || centerNearest) {
        if (cubeSearch) {
          if (!rgb.equals(result.nearestRGB)) {
            //�̫�@���q�� or cube�q������, �o�{�̱����I���ʤF!
            //�ҥH����N�o�򵲧�, �~���U�h.
            String measure = cubeAroundMode ? "cube measure." :
                "last measure.";
            Logger.log.info("index(" + index +
                            ") nearestRGB is not equal in " + measure);
            rgb = result.nearestRGB;
            cubeSearch = false;
            continue;
          }
          else {
            //�̫᪺�@���q������o�{rgb�S���ܰ�, �w�g��F�̱����I, �ҥH����.
            result.totalList = totalList;
            return result;
          }
        }
        else {
          //nearest����, �A�h�@�[���q��
          cubeSearch = true;
        }

      }
      else {
        //�S���̱����I, �~����j�a
        if (rgb.equals(result.nearestRGB)) {
          Logger.log.info("index(" + index + ") nearestRGB duplicate.");
        }
        else {
          rgb = result.nearestRGB;
        }
      }
    }
    //�F��̤j�����j����
    access.addMaxAroundTouched();
    Logger.log.info("index(" + index + ") MaxAroundIterativeTimes(" +
                    MaxAroundIterativeTimes + ") meet.");
    result.totalList = totalList;

    return result;
  }

  /**
   * Around�̦h�����N����
   */
  protected final static int MaxAroundIterativeTimes = 100;

  /**
   * �H�T�ئ�t�����P�ɦҶq, �Ĥ@��aroundXYZ�O�_�O��t�̤p��
   * @param targetXYZ CIEXYZ
   * @param aroundXYZ CIEXYZ[]
   * @param white CIEXYZ
   * @return boolean[]
   */
  protected boolean[] isFirstNearestXYZInDeltaE(CIEXYZ targetXYZ,
                                                CIEXYZ[] aroundXYZ,
                                                CIEXYZ white) {
    boolean[] result = new boolean[3];
    result[0] = MeasuredUtils.isFirstNearestXYZInDeltaE00(targetXYZ,
        aroundXYZ, white, false);
    result[1] = MeasuredUtils.isFirstNearestXYZInDeltaE(targetXYZ,
        aroundXYZ, white);
    result[2] = MeasuredUtils.isFirstNearestXYZInDeltaEuv(targetXYZ,
        aroundXYZ, white);
    return result;
  }

  /**
   * �h�l���q������, �N��N�O��, �q����o�S���ĥΪ�RGB
   * @return int
   */
  public int getRedundantMeasure() {
    return redundantMeasure;
  }

  public void setEnableTrace(boolean enableTrace) {
    this.enableTrace = enableTrace;
  }

  /**
   * �qcube�ˬd�O���O�̾a��ؼЪ�
   * @param nearestRGB RGB
   * @param targetXYZ CIEXYZ
   * @param step double
   * @return AlgoResult
   */
  protected AlgoResult cubeCheckInOneJNDI(final RGB nearestRGB,
                                          final CIEXYZ targetXYZ,
                                          double step) {
    if (cubeCheckInOneJNDI) {
      RGB[] aroundRGB = stepAroundAlgo.getCubeAroundRGB(nearestRGB, step);
      AlgoResult result = cubeNearAlgo.getNearestRGB(targetXYZ, aroundRGB);
      if (!nearestRGB.equals(result.nearestRGB)) {
        this.addTrace("Find " + nearestRGB + "->" + result.nearestRGB +
                      " in cube check.");
      }
      return result;
    }
    return null;
  }

  /**
   * �qcube�ˬd�O���O�̾a��ؼЪ�
   */
  private boolean cubeCheckInOneJNDI = true;
  public void setCubeCheckInOneJNDI(boolean cubeCheckInOneJNDI) {
    this.cubeCheckInOneJNDI = cubeCheckInOneJNDI;
  }
}
