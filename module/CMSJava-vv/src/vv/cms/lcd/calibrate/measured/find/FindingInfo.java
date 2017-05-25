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
 * 儲存校正會用到的相關資訊
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
   * 校正產生的RGB放置於此, 同時也是初始的RGB
   */
  private RGB[] calibratedRGBArray;
  private RGB[] candilateCalibratedRGBArray;

  /**
   * 設定校正得到的RGB
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
   * 取出校正完畢的RGB
   * @param index int
   * @return RGB
   */
  public RGB getCalibratedRGB(int index) {
    return calibratedRGBArray[index];
  }

  /**
   * 校正所採用的索引值, 此處會儲存校正最後的結果
   */
  private double[] calibratedIndexArray;

  /**
   * deltaE的索引值
   */
  private double[] deltaEIndexArray;
  /**
   * delta JNDI的索引值
   */
  private double[] deltaJNDIIndexArray;
  /**
   * delta u'v'的距離
   */
  private double[] deltauvpDistArray;

  /**
   * delta u'v'的索引值
   */
  private double[][] deltaunvpIndexArray;

  private boolean[] nonQualifyArray;

  /**
   * 設定未通過品質認可
   * @param index int
   */
  protected void setNonQualify(int index) {
    nonQualifyArray[index] = true;
    this.nonQualifyTimes++;
  }

  /**
   * 沒有符合資格的相關資訊(目前只有index)
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
   * 標定是否已經完成校正
   */
  private boolean[] alreadyCalibrated = null;
  /**
   * 標定各個code目前的step
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
   * 設定校正參考用的索引值
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
   * 還沒有校正完成的數量
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
      // index陣列初始化
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
   * 進行校正的次數
   */
  private volatile int calibrateCount = 0;

  /**
   * 校正次數的計數+1
   * @return int
   */
  protected int addCalibrateCount() {
    return (calibrateCount++);
  }

  /**
   * 取得校正次數
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
   * index的元素是否已經完成校正
   * @param index int
   * @return boolean
   */
  public boolean isCalibrated(int index) {
    return alreadyCalibrated[index];
  }

  /**
   * 設定是否完成校正
   * @param index int
   * @param calibrated boolean
   */
  public void setCalibrated(int index, boolean calibrated) {
    this.alreadyCalibrated[index] = calibrated;
  }

  /**
   * 立方體搜尋的次數
   */
  private int cubeSearchTimes = 0;

  /**
   * cube搜尋的計數+1
   * @return int
   */
  protected int addCubeSearchTimes() {
    return (cubeSearchTimes++);
  }

  /**
   * 計算出完成的百分比例
   * @param targetMaxValue MaxValue 目標的maxValue
   * @return double 完成的比例
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
        //距離目標值還有幾倍
        double factor = (targetMaxValue.max / step.max);
        result += percentPercode / factor / 2.;
      }
    }

    result += base * percentPercode;
    return result * 100;
  }

  /**
   * 設定校正陣列
   * @param calibrated boolean[]
   */
  public void setAlreadyCalibrated(boolean[] calibrated) {
    this.alreadyCalibrated = calibrated;
  }

  /**
   * 設定初始的RGB
   * @param initRGBArray RGB[]
   */
  public void setInitRGBArray(RGB[] initRGBArray) {
    this.calibratedRGBArray = initRGBArray;
    this.candilateCalibratedRGBArray = RGBArray.deepClone(initRGBArray);
  }

}
