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
   * �O�_�ץ�key�Mvalue�b�d��
   */
  protected boolean correctInRange = true;
  /**
   * �����̤j��
   */
  protected double maxValue;
  protected double minValue;
  protected boolean hasCorrectedInRange = false;

  /**
   *  �ˬd�W�@���I�scorrect��(��uncorrect), �O�_���W�X�d��M��Q�ץ������p.
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
   * �O�_�i�H��uncorrect. �]�N�O�ˬd�O�_�b�i�ץ����d��
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
   * �]�w�������t��k
   * @param algo Algo
   */
  public void setLUTAlgo(Interpolation1DLUT.Algo algo) {
    if (lut != null) {
      lut.setInterpolationType(algo);
    }
  }

  /**
   * �qoutput����input(�hgamma�B��)
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
   * �O�_�i�H��correct. �]�N�O�ˬd�O�_�b�i�ץ����d��
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
   * ���o�����Z��table
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
   * �Ncorrect�Ϊ���^��
   * @param level int
   * @return double[]
   * @todo H icc �J��Ʀ�۾������X,�|�����D�ݭn�ץ�
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
   * �Nuncorrect�Ϊ���^��
   * @param level int
   * @return double[]
   * @todo H icc �J��Ʀ�۾������X,�|�����D�ݭn�ץ�
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
   * �qinput����output(�[�Wgamma�B��)
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
       * @note ���F�קK���t�Ȫ��X�{(gamma�p�⤣���ӭn���t��)
       */
      result = RGB.rationalize(result, RGB.MaxValue.Double1);
      return result;

    }
  }

  /**
   * �O�_�ĥΫ��ƶi��Gamma�ե�
   * (�_�h�N�O�ĥ�LUT)
   */
  public final static boolean GAMMA_EXPONENT_CORRECTION = false;
  /**
   * �O�_�ݭn�w��code=0���G�פ]�ץ���0,�A�i�楿�W�����p��(�@��ӻ��O���n��)
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
   * �H��Ӫ�(LUT)����GammaCorrector
   * @param singleChannelPatch Set
   * @param ch Channel
   * @return GammaCorrector
   */
  public final static GammaCorrector getLUTInstance(Set<Patch>
      singleChannelPatch, RGBBase.Channel ch) {
    return getInstance(singleChannelPatch, ch, false, true, Method.ByLuminance);
  }

  /**
   * �H��Ӫ�(LUT)����GammaCorrector
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
   * ����Y���X�z��
   * �����Ӧ��G�פ��઺���p.
   * �p�G��,�i��O 1.�������D 2.LCD��í�w���D
   * �Ҧ������t���ө����o�˪����D
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
   * �ˬdvalues�O���W
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
   * ���oGammaCorrector������
   * @param singleChannelPatch Set ��@�W�D�����
   * @param ch Channel �W�D
   * @param useGammaExponentCorrection boolean �H���ƹB�ⰵ�ե�
   * @param doYNormalize boolean �O�_�n��Y�����W��
   * @param method Method ���ͤ����Ȫ���k
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
    // Set<Patch>���input/output
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
    //�ˬd���S������
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

    //��G�ק@���W��
    if (YMin != 0 && BLACK_CORRECTION) {
      //���϶��I�k�s
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
     * �q�G�ײ��ͭץ�
     */
    ByLuminance,
    /**
     * �qXYZ�ұo��power���ͭץ�
     */
    ByPowerXYZ,

  }

  public GammaFinder getExponent() {
    return exponent;
  }

  /**
   * correct�Ϊ�������Ӫ�
   * @return Interpolation1DLUT
   */
  public Interpolation1DLUT getLut() {
    return lut;
  }

  public double getMaxValue() {
    return maxValue;
  }

}
