package shu.cms.util;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.lut.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class GammaCorrector
    implements Serializable {

  protected boolean exponentCorrection;
  protected GammaFinder exponent;
  protected Interpolation1DLUT lut;
  /**
   * 是否修正key和value在範圍內
   */
  protected boolean correctInRange = true;
  /**
   * 紀錄最大值
   */
  protected double maxValue;
  protected double minValue;
  protected boolean hasCorrectedInRange = false;

  /**
   *  檢查上一次呼叫correct時(或uncorrect), 是否有超出範圍然後被修正的狀況.
   * @return boolean
   */
  public boolean hasCorrectedInRange() {
    return hasCorrectedInRange;
  }

  public GammaCorrector(GammaFinder exponent) {
    exponentCorrection = true;
    this.exponent = exponent;
  }

  public GammaCorrector(Interpolation1DLUT lut) {
    exponentCorrection = false;
    this.lut = lut;
  }

  /**
   * 是否可以做uncorrect. 也就是檢查是否在可修正的範圍內
   * @param correct double
   * @return boolean
   */
  public boolean isUncorrectOk(double correct) {
    if (exponentCorrection) {
      return correct >= this.minValue && correct <= this.maxValue;
    }
    else {
      return lut.isKeyInRange(correct);
    }
  }

  /**
   * 設定內插的演算法
   * @param algo Algo
   */
  public void setLUTAlgo(Interpolation1DLUT.Algo algo) {
    if (lut != null) {
      lut.setInterpolationType(algo);
    }
  }

  /**
   * 從output推到input(去gamma運算)
   * @param correct double
   * @return double
   */
  public double uncorrect(double correct) {
    if (exponentCorrection) {
      double gamma = 1. / exponent.getGamma();
      return exponent.gamma( (correct - minValue) / maxValue, gamma) * maxValue +
          minValue;
    }
    else {
      boolean negative = false;
      if (correct < 0) {
        negative = true;
        correct = -correct;
      }
      if (correctInRange) {
        correct = lut.correctKeyInRange(correct);
        hasCorrectedInRange = lut.hasCorrectedInRange();
      }
      double result = lut.getValue(correct);
      if (negative) {
        result = -result;
      }

      return result;
    }
  }

  /**
   * 是否可以做correct. 也就是檢查是否在可修正的範圍內
   * @param original double
   * @return boolean
   */
  public boolean isCorrectOk(double original) {
    if (exponentCorrection) {
      return original >= this.minValue && original <= this.maxValue;
    }
    else {
      return lut.isValueInRange(original);
    }
  }

  /**
   * 取得等間距的table
   * @param level int
   * @return double[]
   */
  public static double[] getTable(int level) {
    double[] table = new double[level];
    double step = 1. / (level - 1);
    for (int x = 0; x < level; x++) {
      table[x] = x * step;
    }
    return table;
  }

  /**
   * 將correct用的表回傳
   * @param level int
   * @return double[]
   * @todo H icc 遇到數位相機的場合,會有問題需要修正
   */
  public double[] getCorrectTable(int level) {
    double[] table = new double[level];
    double step = 1. / (level - 1);
    for (int x = 0; x < level; x++) {
      table[x] = correct(x * step);
    }
    return table;
  }

  /**
   * 將uncorrect用的表回傳
   * @param level int
   * @return double[]
   * @todo H icc 遇到數位相機的場合,會有問題需要修正
   */
  public double[] getUncorrectTable(int level) {
    double[] table = new double[level];
    double step = 1. / (level - 1);
    for (int x = 0; x < level; x++) {
      table[x] = uncorrect(x * step);
    }
    return table;

  }

  /**
   * 從input推到output(加上gamma運算)
   * @param original double
   * @return double
   */
  public double correct(double original) {
    if (exponentCorrection) {
      double gamma = exponent.getGamma();
      return exponent.gamma( (original - minValue) / maxValue, gamma) *
          maxValue + minValue;
    }
    else {

      boolean negative = false;
      if (original < 0) {
        negative = true;
        original = -original;
      }
      if (correctInRange) {
        original = lut.correctValueInRange(original);
        hasCorrectedInRange = lut.hasCorrectedInRange();
      }
      double result = lut.getKey(original);
      if (negative) {
        result = -result;
      }
      /**
       * @note 為了避免有負值的出現(gamma計算不應該要有負值)
       */
      result = RGB.rationalize(result, RGB.MaxValue.Double1);
      return result;

    }
  }

  /**
   * 是否採用指數進行Gamma校正
   * (否則就是採用LUT)
   */
  public final static boolean GAMMA_EXPONENT_CORRECTION = false;
  /**
   * 是否需要針對code=0的亮度也修正成0,再進行正規劃的計算(一般來說是必要的)
   */
  public final static boolean BLACK_CORRECTION = true;

  /**
   *
   * @param singleChannelPatch Set
   * @param ch Channel
   * @param gammaStart double
   * @param gammaEnd double
   * @return GammaCorrector
   */
  public final static GammaCorrector getExponentInstance(Set<Patch>
      singleChannelPatch, RGBBase.Channel ch) {
    return getInstance(singleChannelPatch, ch, false, true, Method.ByLuminance);
  }

  /**
   * 以對照表(LUT)產生GammaCorrector
   * @param singleChannelPatch Set
   * @param ch Channel
   * @return GammaCorrector
   */
  public final static GammaCorrector getLUTInstance(Set<Patch>
      singleChannelPatch, RGBBase.Channel ch) {
    return getInstance(singleChannelPatch, ch, false, true, Method.ByLuminance);
  }

  /**
   * 以對照表(LUT)產生GammaCorrector
   * @param singleChannelPatch Set
   * @param ch Channel
   * @param method Method
   * @return GammaCorrector
   */
  public final static GammaCorrector getLUTInstance(Set<Patch>
      singleChannelPatch, RGBBase.Channel ch, Method method) {
    return getInstance(singleChannelPatch, ch, false, true, method);
  }

  /**
   * 驗證Y的合理性
   * 不應該有亮度反轉的狀況.
   * 如果有,可能是 1.儀器問題 2.LCD不穩定問題
   * 模式的推演應該忽略這樣的問題
   * @param input double[]
   * @param outputY double[]
   * @return double[][]
   * @deprecated
   */
  protected static double[][] validateY(double[] input, double[] outputY) {
    int size = outputY.length;
    double forwardVales = outputY[0];
    int ignoreCount = 0;

    for (int x = 1; x < size; x++) {
      if (outputY[x] < forwardVales) {
        outputY[x] = -1;
        ignoreCount++;
      }
    }

    int newSize = size - ignoreCount;
    double[] validInput = new double[newSize];
    double[] validOutput = new double[newSize];
    int validIndex = 0;

    for (int x = 0; x < size; x++) {
      if (outputY[x] == -1) {
        continue;
      }
      validInput[validIndex] = input[x];
      validOutput[validIndex] = outputY[x];
      validIndex++;
    }

    return new double[][] {
        validInput, validOutput};
  }

  /**
   * 檢查values是遞增
   * @param values double[]
   * @return boolean
   */
  private static boolean checkIncrease(double[] values) {
    int size = values.length;
    for (int x = 0; x < size - 1; x++) {
      if (values[x] > values[x + 1]) {
        return false;
      }
    }
    return true;
  }

  private static int checkDecreaseIndex(double[] values) {
    int size = values.length;
    for (int x = 0; x < size - 1; x++) {
      if (values[x] > values[x + 1]) {
        return x;
      }
    }
    return -1;
  }

  /**
   * 取得GammaCorrector的實體
   * @param singleChannelPatch Set 單一頻道的色塊
   * @param ch Channel 頻道
   * @param useGammaExponentCorrection boolean 以指數運算做校正
   * @param doYNormalize boolean 是否要對Y做正規化
   * @param method Method 產生內插值的方法
   * @return GammaCorrector
   */
  protected final static GammaCorrector getInstance(Set<Patch>
      singleChannelPatch, RGBBase.Channel ch,
      boolean useGammaExponentCorrection,
      boolean doYNormalize, Method method) {
    int size = singleChannelPatch.size();
    double[] input = new double[size];
    double[] output = new double[size];
    double YMax = Double.MIN_VALUE;

    //==========================================================================
    // Set<Patch>轉到input/output
    //==========================================================================
    int x = 0;
    for (Patch p : singleChannelPatch) {
      RGB rgb = p.getRGB();
      //DAC Values
      input[x] = rgb.getValue(ch, RGB.MaxValue.Double1);

      //Luminance(Y)
      switch (method) {
        case ByLuminance:
          output[x] = p.getXYZ().Y;
          break;
        case ByPowerXYZ:
          output[x] = p.getXYZ().getPowerByXYZ();
          break;

      }
      YMax = Math.max(output[x], YMax);
      x++;
    }
    //檢查有沒有反轉
    int inputDecreaseIndex = checkDecreaseIndex(input);
    int outputDecreaseIndex = checkDecreaseIndex(output);
    if (inputDecreaseIndex != -1 || outputDecreaseIndex != -1) {
      String desc = ( (inputDecreaseIndex != -1) ?
                     "input: " + inputDecreaseIndex : "") +
          ( (outputDecreaseIndex != -1) ? "output: " + outputDecreaseIndex : "");
      throw new IllegalArgumentException("Channel " + ch +
                                         " singleChannelPatch is not increase progressively: " +
                                         desc);
    }
    //==========================================================================

    double YMin = output[0];

    //對亮度作正規化
    if (YMin != 0 && BLACK_CORRECTION) {
      //先使黑點歸零
      output = DoubleArray.minus(output, YMin);
    }
    if (doYNormalize) {
      Maths.normalize(output, YMax - YMin);
    }

    if (useGammaExponentCorrection) {
      GammaFinder corrector = new GammaFinder(input, output);
      corrector.calculateGamma();

      GammaCorrector r = new GammaCorrector(corrector);
      r.maxValue = YMax;
      r.minValue = YMin;
      return r;
    }
    else {
      Interpolation1DLUT lut = new Interpolation1DLUT(output, input,
          Interpolation1DLUT.Algo.LINEAR);
      GammaCorrector r = new GammaCorrector(lut);
      r.maxValue = YMax;
      r.minValue = YMin;
      return r;
    }

  }

  public static enum Method {
    /**
     * 從亮度產生修正
     */
    ByLuminance,
    /**
     * 從XYZ所得的power產生修正
     */
    ByPowerXYZ,

  }

  public GammaFinder getExponent() {
    return exponent;
  }

  /**
   * correct用的內插對照表
   * @return Interpolation1DLUT
   */
  public Interpolation1DLUT getLut() {
    return lut;
  }

  public double getMaxValue() {
    return maxValue;
  }

}
