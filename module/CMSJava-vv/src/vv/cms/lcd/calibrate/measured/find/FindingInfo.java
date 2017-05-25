package vv.cms.lcd.calibrate.measured.find;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.depend.RGBBase.*;
import shu.cms.colorspace.depend.DeviceDependentSpace.MaxValue;
import shu.cms.util.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �x�s�ե��|�Ψ쪺������T
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class FindingInfo {
  /**
   * �ե����ͪ�RGB��m��, �P�ɤ]�O��l��RGB
   */
  private RGB[] calibratedRGBArray;
  private RGB[] candilateCalibratedRGBArray;

  /**
   * �]�w�ե��o�쪺RGB
   * @param index int
   * @param rgb RGB
   */
  public void setCalibratedRGB(int index, RGB rgb) {
    calibratedRGBArray[index] = rgb;
  }

  public void setCandilateCalibratedRGB(int index, RGB rgb) {
    candilateCalibratedRGBArray[index] = rgb;
  }

  /**
   * ���X�ե�������RGB
   * @param index int
   * @return RGB
   */
  public RGB getCalibratedRGB(int index) {
    return calibratedRGBArray[index];
  }

  /**
   * �ե��ұĥΪ����ޭ�, ���B�|�x�s�ե��̫᪺���G
   */
  private double[] calibratedIndexArray;

  /**
   * deltaE�����ޭ�
   */
  private double[] deltaEIndexArray;
  /**
   * delta JNDI�����ޭ�
   */
  private double[] deltaJNDIIndexArray;
  /**
   * delta u'v'���Z��
   */
  private double[] deltauvpDistArray;

  /**
   * delta u'v'�����ޭ�
   */
  private double[][] deltaunvpIndexArray;

  private boolean[] nonQualifyArray;

  /**
   * �]�w���q�L�~��{�i
   * @param index int
   */
  protected void setNonQualify(int index) {
    nonQualifyArray[index] = true;
    this.nonQualifyTimes++;
  }

  /**
   * �S���ŦX��檺������T(�ثe�u��index)
   * @return String
   */
  public String getNonQualifyInfo() {
    if (nonQualifyArray != null) {
      StringBuilder buf = new StringBuilder("NonQualifyIndex: ");
      for (int x = 0; x < size; x++) {
        if (nonQualifyArray[x] == true) {
          buf.append(Integer.toString(x) + ", ");
        }
      }

      buf.delete(buf.length() - 2, buf.length());
      return buf.toString();
    }
    else {
      return null;
    }
  }

  /**
   * �Щw�O�_�w�g�����ե�
   */
  private boolean[] alreadyCalibrated = null;
  /**
   * �Щw�U��code�ثe��step
   */
  private RGB.MaxValue[] stepArray;
  protected void setStep(int index, RGB.MaxValue step) {
    this.stepArray[index] = step;
  }

  public static void main(String[] args) {
    FindingInfo info = new FindingInfo();
    info.init();

    for (int x = 0; x < 256; x++) {
      info.setStep(x, RGB.MaxValue.Int10Bit);
    }
    for (int x = 0; x < 253; x++) {
      info.setCalibrated(x, true);
    }
    double p = info.getCompletePercentage(RGB.MaxValue.Int10Bit);
    System.out.println(p);
  }

  /**
   * �]�w�ե��ѦҥΪ����ޭ�
   * @param index int
   * @param calibratedIndex double
   * @param deltaE double
   * @param deltaJNDI double
   * @param deltauvp double[]
   */
  public void setIndex(int index, double calibratedIndex, double deltaE,
                       double deltaJNDI, double[] deltauvp) {
    calibratedIndexArray[index] = calibratedIndex;
    deltaEIndexArray[index] = deltaE;
    deltaJNDIIndexArray[index] = deltaJNDI;
    double duvpdist = Maths.sqrt(Maths.sqr(deltauvp[0]) + Maths.sqr(deltauvp[1]));
    deltauvpDistArray[index] = duvpdist;
    deltaunvpIndexArray[index] = deltauvp;
  }

  /**
   * �٨S���ե��������ƶq
   * @return int
   */
  public int getNonCalibrated() {
    int noncalibrated = 0;
    for (boolean b : alreadyCalibrated) {
      if (!b) {
        noncalibrated++;
      }
    }
    return noncalibrated;
  }

  private final static int DefaultSize = 256;
  private int size;

  public FindingInfo() {
    this(DefaultSize);
  }

  public FindingInfo(int size) {
    this.size = size;
  }

  private boolean init = false;
  public boolean isInit() {
    return init;
  }

  public void init() {
    if (!init) {
      //========================================================================
      // index�}�C��l��
      //========================================================================
      calibratedIndexArray = new double[size];
      deltaEIndexArray = new double[size];
      deltaJNDIIndexArray = new double[size];
      deltauvpDistArray = new double[size];
      deltaunvpIndexArray = new double[size][];
      //========================================================================

      nonQualifyArray = new boolean[size];

      if (alreadyCalibrated == null) {
        alreadyCalibrated = new boolean[size];
      }
      alreadyCalibrated[0] = alreadyCalibrated[size - 1] = true;
      stepArray = new MaxValue[size];

      this.init = true;
    }
  }

  /**
   * �i��ե�������
   */
  private volatile int calibrateCount = 0;

  /**
   * �ե����ƪ��p��+1
   * @return int
   */
  protected int addCalibrateCount() {
    return (calibrateCount++);
  }

  /**
   * ���o�ե�����
   * @return int
   */
  public int getCalibrateCount() {
    return calibrateCount;
  }

  private int nonQualifyTimes = 0;

  public int getNonQualifyTimes() {
    return nonQualifyTimes;
  }

  public int getCubeSearchTimes() {
    return cubeSearchTimes;
  }

  public boolean[] getAlreadyCalibrated() {
    return alreadyCalibrated;
  }

  public double[] getDeltaEIndexArray() {
    return deltaEIndexArray;
  }

  public double[] getDeltaJNDIIndexArray() {
    return deltaJNDIIndexArray;
  }

  public double[] getDeltauvpDistArray() {
    return deltauvpDistArray;
  }

  public RGB[] getCalibratedRGBArray() {
    return calibratedRGBArray;
  }

  public RGB[] getCandilateCalibratedRGBArray() {
    return candilateCalibratedRGBArray;
  }

  public double[][] getDeltaunvpIndexArray() {
    return deltaunvpIndexArray;
  }

  /**
   * index�������O�_�w�g�����ե�
   * @param index int
   * @return boolean
   */
  public boolean isCalibrated(int index) {
    return alreadyCalibrated[index];
  }

  /**
   * �]�w�O�_�����ե�
   * @param index int
   * @param calibrated boolean
   */
  public void setCalibrated(int index, boolean calibrated) {
    this.alreadyCalibrated[index] = calibrated;
  }

  /**
   * �ߤ���j�M������
   */
  private int cubeSearchTimes = 0;

  /**
   * cube�j�M���p��+1
   * @return int
   */
  protected int addCubeSearchTimes() {
    return (cubeSearchTimes++);
  }

  /**
   * �p��X�������ʤ����
   * @param targetMaxValue MaxValue �ؼЪ�maxValue
   * @return double ���������
   */
  public double getCompletePercentage(RGB.MaxValue targetMaxValue) {
    if (alreadyCalibrated == null) {
      return 0;
    }
    int base = 0;
    double percentPercode = 1. / 256;
    double result = 0;

    for (int x = 0; x < 256; x++) {
      if (true == alreadyCalibrated[x]) {
        base++;
      }
      else if (stepArray != null && stepArray[x] != null) {
        RGB.MaxValue step = stepArray[x];
        //�Z���ؼЭ��٦��X��
        double factor = (targetMaxValue.max / step.max);
        result += percentPercode / factor / 2.;
      }
    }

    result += base * percentPercode;
    return result * 100;
  }

  /**
   * �]�w�ե��}�C
   * @param calibrated boolean[]
   */
  public void setAlreadyCalibrated(boolean[] calibrated) {
    this.alreadyCalibrated = calibrated;
  }

  /**
   * �]�w��l��RGB
   * @param initRGBArray RGB[]
   */
  public void setInitRGBArray(RGB[] initRGBArray) {
    this.calibratedRGBArray = initRGBArray;
    this.candilateCalibratedRGBArray = RGBArray.deepClone(initRGBArray);
  }

}
