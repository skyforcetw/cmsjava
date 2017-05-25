package shu.math;

import java.io.*;

import shu.math.array.*;
import shu.plot.*;
import shu.math.regress.*;

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
public class GammaFinder
    implements Serializable {

  public final static double[] NormalInput = DoubleArray.buildX(0, 1, 256);

  /**
   * 計算gamma
   * input及output為正規化的值:範圍0.0~1.0
   * 否則可能無法計算
   * @param input double[]
   * @param output double[]
   */
  public GammaFinder(double[] input, double[] output) {
    if (! (validateValues(input) && validateValues(output))) {
      throw new IllegalArgumentException(
          "input or output values is not normalization.");
    }
    this.input = input;
    this.output = output;
  }

  protected double[] input;
  protected double[] output;
  protected double gamma;

  protected boolean validateValues(double[] values) {
    for (double v : values) {
      if (v < 0 || v > 1) {
        return false;
      }
    }
    return true;
  }

  /**
   * 根據calGamma的結果,將input的值gamma校正之後回傳
   * @return double[]
   */
  public double[] gammaCorrect() {
    double gammaCompensate = 1 / gamma;
    return gammaCurve(this.input, gammaCompensate);
  }

  public final static double[] gammaCurve(int piece, double gamma) {
    if (piece < 3) {
      throw new IllegalArgumentException("piece < 3");
    }
    double[] gammaCurve = new double[piece];
    for (int x = 0; x < piece; x++) {
      double normal = x / (piece - 1.);
      gammaCurve[x] = gamma(normal, gamma);
    }
    return gammaCurve;
  }

  public final static double[] gammaCurve(double[] input, double gamma) {
    int size = input.length;
    double[] correcInput = new double[size];
    for (int x = 0; x < size; x++) {
      correcInput[x] = gamma(input[x], gamma);
    }
    return correcInput;
  }

  public final static double[] gammaCurve(double[] input, double maxValue,
                                          double gamma) {
    int size = input.length;
    double[] correcInput = new double[size];
    for (int x = 0; x < size; x++) {
      correcInput[x] = gamma(input[x], maxValue, gamma);
    }
    return correcInput;
  }

  public final static double gamma(double input, double maxValue, double gamma) {
    return maxValue * gamma(input / maxValue, gamma);
  }

  public final static double gamma(double input, double gamma) {
    return Math.pow(input, gamma);
  }

  public double calculateGamma() {
    gamma = findGamma(input, output);
    if (gamma == Double.POSITIVE_INFINITY) {
      gamma = 1.;
    }
    return gamma;
  }

//  /**
//   * 產生gamma表
//   * @param original double
//   * @param gammaStart double
//   * @param gammEnd double
//   * @return double[]
//   */
//  protected static double[] produceGammaTable(double original,
//                                              double gammaStart,
//                                              double gammEnd) {
//    return produceGammaTable(original, gammaStart, gammEnd, 2);
//  }
//
//  /**
//   * 產生gamma表,切成pieces份
//   * @param original double
//   * @param gammaStart double
//   * @param gammaEnd double
//   * @param pieces int
//   * @return double[]
//   */
//  protected static double[] produceGammaTable(double original,
//                                              double gammaStart,
//                                              double gammaEnd, int pieces) {
//    if (pieces < 2) {
//      throw new IllegalArgumentException("pieces <2");
//    }
//    double[] table = new double[pieces];
//    table[0] = Math.pow(original, gammaStart);
//    double aPiecs = (gammaEnd - gammaStart) / (pieces - 1);
//    for (int x = 1; x < pieces - 1; x++) {
//      double gamma = aPiecs * x + gammaStart;
//      table[x] = Math.pow(original, gamma);
//    }
//    table[pieces - 1] = Math.pow(original, gammaEnd);
//    return table;
//  }

  public double getGamma() {
    return gamma;
  }

  protected final static double[][] normalize(final double[] input,
                                              final double[] output,
                                              double inputMinValue,
                                              double outputMinValue,
                                              double inputMaxValue,
                                              double outputMaxValue) {
    if (input.length != output.length) {
      throw new IllegalArgumentException(
          "input.length != output.length");
    }
    double[] normalInput = normalize(input, inputMinValue, inputMaxValue);
    double[] normalOutput = normalize(output, outputMinValue, outputMaxValue);

    double[][] normalize = new double[][] {
        normalInput, normalOutput};
    return normalize;
  }

  public final static double[] normalize(final double[] valueArray,
                                         final double minValue,
                                         final double maxValue
      ) {
    double[] normal = DoubleArray.minus(valueArray, minValue);
    Maths.normalize(normal, maxValue - minValue);
    return normal;
  }

  /**
   *
   * @param input double[]
   * @param output double[]
   * @param inputMaxValue double
   * @param outputMaxValue double
   * @return double[][]
   * @deprecated
   */
  protected final static double[][] normalize(final double[] input,
                                              final double[] output,
                                              double inputMaxValue,
                                              double outputMaxValue) {
    if (input.length != output.length) {
      throw new IllegalArgumentException(
          "input.length != output.length");
    }

    double inputMinValue = input[0];
    double outputMinValue = output[0];
    double[] normalInput = DoubleArray.minus(input, inputMinValue);
    double[] normalOutput = DoubleArray.minus(output, outputMinValue);

    Maths.normalize(normalInput, inputMaxValue - inputMinValue);
    Maths.normalize(normalOutput,
                    outputMaxValue - outputMinValue);
    double[][] normalize = new double[][] {
        normalInput, normalOutput};
    return normalize;
  }

  public final static double findGamma(double[] luminanceArray) {
    int size = luminanceArray.length;
    int N = size - 1;
    double L1 = luminanceArray[N];
    double summary = 0;
    for (int x = 1; x < N; x++) {
      double L = luminanceArray[x];
      double n = ( (double) x) / N;
      double gamma = Math.log10(L / L1) / Math.log10(n);
      summary += gamma;
    }
    double result = 1. / (N - 1) * summary;
    return result;
  }

  public static double getGammaOffset(double minLuminance, double maxLuminance,
                                      double gamma) {
    double normal = minLuminance / maxLuminance;
    double n = Math.log10(normal) / gamma;
    double offset = Math.pow(10, n);
    return offset;
  }

  public final static double findGammaWithOffset(double[] luminanceArray) {
    double gammaWithoutOffset = findGamma(luminanceArray);
    int size = luminanceArray.length;
    int N = size - 1;
    double offset = getGammaOffset(luminanceArray[0],
                                   luminanceArray[N],
                                   gammaWithoutOffset);

    double L1 = luminanceArray[N];
    double summary = 0;
    for (int x = 1; x < N; x++) {
      double L = luminanceArray[x];
      double n = ( (double) x) / N;
      double gamma = Math.log10(L / L1) / Math.log10(n + offset);
      summary += gamma;
    }
    double result = 1. / (N - 1) * summary;
    return result;
  }

  public static void main(String[] args) {
    int size = 11;
    double[] luminanceArray = new double[size];
    double[] normalInput = new double[size];
    for (int x = 0; x < size; x++) {
      double normal = ( (double) x) / (size - 1);
      normalInput[x] = normal;
      double power = Math.pow(normal, 2.589) + 0.01;
      luminanceArray[x] = power;
    }
    Maths.normalize(luminanceArray, luminanceArray[size - 1]);
    double gamma1 = findGamma(luminanceArray);
    double gamma2 = findGamma(normalInput, luminanceArray);
//    System.out.println(findingGamma(luminanceArray)); //比較準!
//    System.out.println(findingGamma(normalInput, luminanceArray));
    Plot2D plot = Plot2D.getInstance();
    plot.addLinePlot("", 0, size - 1, luminanceArray);

    for (int x = 0; x < size; x++) {
      double normal = ( (double) x) / (size - 1);
      double power1 = Math.pow(normal, gamma1);
      double power2 = Math.pow(normal, gamma2);
      plot.addCacheScatterLinePlot(Double.toString(gamma1), x, power1);
      plot.addCacheScatterLinePlot(Double.toString(gamma2), x, power2);
    }
    plot.addLegend();
    plot.setVisible();

  }

  public final static double findGamma(final double[] input,
                                       final double[] output,
                                       double inputMinValue,
                                       double outputMinValue,
                                       double inputMaxValue,
                                       double outputMaxValue) {
    if (input.length != output.length) {
      throw new IllegalArgumentException(
          "input.length != output.length");
    }

    double[][] normalize = normalize(input, output, inputMinValue,
                                     outputMinValue, inputMaxValue,
                                     outputMaxValue);
    return findGamma(normalize[0], normalize[1]);
  }

  /**
   * 計算出每一點所屬的gamma值
   * @param normalizeInput double[]
   * @param normalizeOutput double[]
   * @return double[]
   */
  public final static double[] findGammas(double[] normalizeInput,
                                          double[] normalizeOutput) {
    if (normalizeInput.length != normalizeOutput.length) {
      throw new IllegalArgumentException(
          "normalizeInput.length != normalizeOutput.length");
    }
    int size = normalizeInput.length - 2;
    double[] gammas = new double[size];
    for (int x = 0; x < size; x++) {
      double[] input = new double[] {
          normalizeInput[0], normalizeInput[x + 1], normalizeInput[size + 1]};
      double[] output = new double[] {
          normalizeOutput[0], normalizeOutput[x + 1], normalizeOutput[size + 1]};
      gammas[x] = findGamma(input, output);
    }
    return gammas;
  }

  public final static double findGamma(double[] normalizeInput,
                                       double[] normalizeOutput) {
    if (normalizeInput.length != normalizeOutput.length) {
      throw new IllegalArgumentException(
          "normalizeInput.length != normalizeOutput.length");
    }

    double denominator = 0;
    for (int x = 0; x < normalizeInput.length; x++) {
      if (normalizeInput[x] == 0) {
        continue;
      }
      denominator += Maths.sqr(Math.log(normalizeInput[x]));
    }
    double numerator = 0;
    for (int x = 0; x < normalizeInput.length; x++) {
      if (normalizeInput[x] == 0 || normalizeOutput[x] == 0) {
        continue;
      }
      double gamma = Math.log(normalizeInput[x]) * Math.log(normalizeOutput[x]);
//      System.out.println("g:" + gamma);
      numerator += gamma;
    }
    return numerator / denominator;
  }

  private final static int[] ReferenceCode = new int[] {
      16, 32, 48, 64, 80, 96, 112, 128, 144, 160, 176, 192, 208, 224, 240,
      255
  };

  /**
   * CPT的gamma公式1, 對於亮部的考量較多
   * @param input double[]
   * @param output double[]
   * @return double
   */
  public final static double findGammaCPT1(double[] input,
                                           double[] output) {
    if (input.length != output.length) {
      throw new IllegalArgumentException(
          "input.length != output.length");
    }
    double[] in = DoubleArray.getCopy(input, ReferenceCode);
    double[] out = DoubleArray.getCopy(output, ReferenceCode);
    int size = ReferenceCode.length;
    out = DoubleArray.minus(out, output[0]);
    for (int x = 0; x < size; x++) {
      in[x] = Math.log10(in[x]);
      out[x] = Math.log10(out[x]);
    }
    PolynomialRegression regress = new PolynomialRegression(in, out,
        Polynomial.COEF_1.BY_1C);
    regress.regress();
    double[] coefs = regress.getCoefs()[0];

    return coefs[1];
  }

  /**
   * CPT的gamma公式2, 對於暗部的考量較多
   * @param input double[]
   * @param output double[]
   * @return double
   */
  public final static double findGammaCPT2(double[] input, double[] output) {
    if (input.length != output.length) {
      throw new IllegalArgumentException(
          "input.length != output.length");
    }
    int size = input.length - 1;
    double[] logV = new double[size];
    double[] logL_Lb = new double[size];
    double[] LogVLogL_Lb = new double[size];
    double[] LogV2 = new double[size];

    for (int x = 0; x < size; x++) {
      logV[x] = Math.log10(input[x + 1]);
      double L_Lb = output[x + 1] - output[0];
      if (L_Lb > 0.00001) {
        logL_Lb[x] = Math.log10(L_Lb);
      }
      else {
        logL_Lb[x] = 0;
      }
      LogVLogL_Lb[x] = logV[x] * logL_Lb[x];
      LogV2[x] = Maths.sqr(logV[x]);
    }
    double H = Maths.sum(logV);
    double I = Maths.sum(logL_Lb);
    double J = Maths.sum(LogVLogL_Lb);
    double K = Maths.sum(LogV2);
    double gamma = (J - H * I / 255) / (K - H * H / 255);
    return gamma;
  }

  public static double findGamma(double normalInput, double normalOutput) {
    double gamma = Math.log(normalOutput) / Math.log(normalInput);
    return gamma;
  }

  public static double findNormalInput(double normalOutput, double gamma) {
    //10^log(n)/2.2 = normal
    double n = Math.pow(10, Math.log10(normalOutput) / gamma);
    return n;
  }
}
