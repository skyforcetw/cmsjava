package shu.math.regress;

import shu.math.*;
import shu.math.Polynomial.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 多項式回歸
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class PolynomialRegression
    extends Regression {
  protected Polynomial.COEF polynomialCoef;

  /**
   * 利用RMS找到最佳的多項式係數個數
   * @param input double[][]
   * @param output double[][]
   * @return COEF_3
   */
  public static Polynomial.COEF_3 findBestPolynomialCoefficient3(double[][]
      input, double[][] output) {
    Polynomial.COEF_3 bestPolynomial = null;
    double minRMSD = Double.MAX_VALUE;
    for (Polynomial.COEF_3 c : Polynomial.COEF_3.values()) {
      PolynomialRegression p = new PolynomialRegression(input, output, c);
      p.regress();
      double rmsd = p.getRMSD();
      if (rmsd < minRMSD) {
        minRMSD = rmsd;
        bestPolynomial = c;
      }
    }
    return bestPolynomial;
  }

  public static Polynomial.COEF findBestPolynomialCoefficient(double[][]
      input, double[][] output) {
    Polynomial.COEF bestPolynomial = null;
    double minRMS = Double.MAX_VALUE;

    for (Polynomial.COEF_3 c : Polynomial.COEF_3.values()) {
      PolynomialRegression p = new PolynomialRegression(input, output, c);
      p.regress();
      double rmsd = p.getRMSD();
      if (rmsd < minRMS) {
        minRMS = rmsd;
        bestPolynomial = c;
      }
    }

    for (Polynomial.COEF_1 c : Polynomial.COEF_1.values()) {
      PolynomialRegression p = new PolynomialRegression(input, output, c);
      p.regress();
      double rmsd = p.getRMSD();
      if (rmsd < minRMS) {
        minRMS = rmsd;
        bestPolynomial = c;
      }
    }

    return bestPolynomial;
  }

  public static Polynomial.COEF_1 findBestPolynomialCoefficient1(double[]
      input, double[] output) {
    Polynomial.COEF_1 bestPolynomial = null;
    double minRMS = Double.MAX_VALUE;
    for (Polynomial.COEF_1 c : Polynomial.COEF_1.values()) {
      if (input.length < c.item) {
        break;
      }
      PolynomialRegression p = new PolynomialRegression(input, output, c);
      p.regress();
      double rmsd = p.getRMSD();
      if (rmsd < minRMS) {
        minRMS = rmsd;
        bestPolynomial = c;
      }
    }
    return bestPolynomial;
  }

  public PolynomialRegression(double[][] input, double[][] output,
                              Polynomial.COEF polynomialCoef) {
    super(processPolynomialInput(input, polynomialCoef), output);
    this.polynomialCoef = polynomialCoef;
  }

  public PolynomialRegression(double[] input, double[] output,
                              Polynomial.COEF_1 polynomialCoef) {
    super(processPolynomialInput(input, polynomialCoef),
          processRegressionOutput(output));
    this.polynomialCoef = polynomialCoef;
  }

  /**
   *
   * @param input double[][]
   * @param output double[][]
   * @param polynomialCoef COEF_3
   */
  public PolynomialRegression(double[][] input, double[][] output,
                              Polynomial.COEF_3 polynomialCoef) {
    super(processPolynomialInput(input, polynomialCoef), output);
    this.polynomialCoef = polynomialCoef;
  }

  /**
   * 產生多項式的係數
   * @param input double[][]
   * @param polynomialCoef COEF
   * @return double[][]
   */
  protected static double[][] processPolynomialInput(double[][] input,
      Polynomial.COEF polynomialCoef) {
    int size = input.length;
    double[][] polynomialInput = new double[size][];

    for (int x = 0; x < size; x++) {
      polynomialInput[x] = Polynomial.getCoef(input[x], polynomialCoef);
    }
    return polynomialInput;
  }

  /**
   * 產生一元多項式的係數
   * @param input double[]
   * @param polynomialCoef COEF_1
   * @return double[][]
   */
  protected static double[][] processPolynomialInput(double[] input,
      Polynomial.COEF_1 polynomialCoef) {
    int size = input.length;
    double[][] polynomialInput = new double[size][];

    for (int x = 0; x < size; x++) {
      polynomialInput[x] = Polynomial.getCoef(input[x], polynomialCoef);
    }
    return polynomialInput;
  }

//  public static void main(String[] args) {
//    double[] in = new double[] {
//        0, 25.388330045523777, 100};
//    double[] out = new double[] {
//        0, 21.352751668475975, 100};
//    PolynomialRegression re = new PolynomialRegression(in, out,
//        Polynomial.COEF_1.BY_2);
//    re.regress();
//
//    double[] in2 = new double[] {
//        0, 35.65004986060882, 100};
//    double[] out2 = new double[] {
//        0, 31.930234985291396, 100};
//    PolynomialRegression re2 = new PolynomialRegression(in2, out2,
//        Polynomial.COEF_1.BY_2);
//    re2.regress();
//
//    double[] in3 = new double[] {
//        0, 25.388330045523777, 35.65004986060882, 100};
//    double[] out3 = new double[] {
//        0, 21.352751668475975, 31.930234985291396, 100};
//    PolynomialRegression re3 = new PolynomialRegression(in3, out3,
//        Polynomial.COEF_1.BY_2);
//    re3.regress();
//
//    Plot2D plot = Plot2D.getInstance();
//    plot.setVisible(true);
//    for (int x = 0; x <= 100; x += 5) {
//      double predict = re.getPredict(new double[] {x})[0];
//      double predict2 = re2.getPredict(new double[] {x})[0];
//      double predict3 = re3.getPredict(new double[] {x})[0];
//      plot.addCacheScatterLinePlot("predict", Color.red, x, predict);
//      plot.addCacheScatterLinePlot("predict2", Color.green, x, predict2);
//      plot.addCacheScatterLinePlot("predict3", Color.black, x, predict3);
//    }
//    plot.drawCachePlot();
//  }

  protected static double[][] processRegressionOutput(double[] output) {
    int size = output.length;
    double[][] regressionOutput = new double[size][];

    for (int x = 0; x < size; x++) {
      regressionOutput[x] = new double[] {
          output[x]};
    }
    return regressionOutput;
  }

  public static double[][] getPredict(double[][] input, double[][] coefs,
                                      Polynomial.COEF_3 polynomialCoef) {
    return Regression.getPredict(processPolynomialInput(input, polynomialCoef),
                                 coefs);
  }

  public double[][] getPredict(double[][] input) {
    return Regression.getPredict(processPolynomialInput(input, polynomialCoef),
                                 coefs);
  }

  /**
   * 以多項式預測input對應的output, 一元多項式用
   * @param input double[]
   * @return double[]
   */
  public double[] getPredict(double[] input) {
    return Regression.getPredict(processPolynomialInput(input,
        (Polynomial.COEF_1) polynomialCoef),
                                 coefs)[0];
  }

  public double[] getMultiPredict(double[] multiInput) {
    if (! (polynomialCoef instanceof Polynomial.COEF_1)) {
      throw new IllegalStateException(
          "! (polynomialCoef instanceof Polynomial.COEF_1");
    }
    double[][] inputs = DoubleArray.transpose(multiInput);
    double[][] predictResult = getPredict(inputs);
    predictResult = DoubleArray.transpose(predictResult);
    return predictResult[0];
  }

  public Polynomial.COEF getPolynomialCoef() {
    return polynomialCoef;
  }

}
