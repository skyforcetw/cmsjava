package shu.math.regress;

import shu.math.*;
import shu.math.operator.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 組合兩個多項式回歸在一起
 * 可採用串連或者並聯的方式
 * 1.串聯: 以x迴歸ab時, x->a, a->b (通常是串聯的效果比較好)
 * 2.並聯: 以x迴歸ab時, x->a, x->b
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CompositePolynomialRegression {
  public static enum Type {
    Series, Parallel
  }

  private Type type;
  private Polynomial.COEF_1 polynomialCoef1;
  private Polynomial.COEF_1 polynomialCoef2;
  private double[] input, output1, output2;
  private PolynomialRegression regress1;
  private PolynomialRegression regress2;
  private Operator operator;

  public void setOperator(Operator operator) {
    this.operator = operator;
  }

  public CompositePolynomialRegression(double[] input, double[] output1,
                                       double[] output2,
                                       Polynomial.COEF_1 polynomialCoef,
                                       Type type) {
    this.input = input;
    this.output1 = output1;
    this.output2 = output2;
    this.polynomialCoef1 = polynomialCoef;
    this.polynomialCoef2 = polynomialCoef;
    this.type = type;
  }

  /**
   * 找到最佳回歸係數
   * @param input double[]
   * @param output1 double[]
   * @param output2 double[]
   * @param type Type
   * @return COEF_1[]
   */
  public static Polynomial.COEF_1[] findBestPolynomialCoefficient1(double[]
      input, double[] output1, double[] output2, Type type) {
    Polynomial.COEF_1 coef1 = PolynomialRegression.
        findBestPolynomialCoefficient1(input, output1);
    Polynomial.COEF_1 coef2 = null;
    switch (type) {
      case Series:

        //如果是series, 就由output1來找output2
        coef2 = PolynomialRegression.
            findBestPolynomialCoefficient1(output1, output2);
        break;
      case Parallel:

        //如果是parallel, 就由input來找output2
        coef2 = PolynomialRegression.
            findBestPolynomialCoefficient1(input, output2);
        break;
    }

    return new Polynomial.COEF_1[] {
        coef1, coef2};
  }

  public CompositePolynomialRegression(double[] input, double[] output1,
                                       double[] output2,
                                       Polynomial.COEF_1[] polynomialCoefs,
                                       Type type) {
    this(input, output1, output2, polynomialCoefs[0], polynomialCoefs[1], type);
  }

  public CompositePolynomialRegression(double[] input, double[] output1,
                                       double[] output2,
                                       Polynomial.COEF_1 polynomialCoef1,
                                       Polynomial.COEF_1 polynomialCoef2,
                                       Type type) {
    this.input = input;
    this.output1 = output1;
    this.output2 = output2;
    this.polynomialCoef1 = polynomialCoef1;
    this.polynomialCoef2 = polynomialCoef2;
    this.type = type;
  }

  public double[][] getCoefs1() {
    return regress1.getCoefs();
  }

  public double[][] getCoefs2() {
    return regress2.getCoefs();
  }

  public Polynomial.COEF getPolynomialCoef1() {
    return polynomialCoef1;
  }

  public Polynomial.COEF getPolynomialCoef2() {
    return polynomialCoef2;
  }

  public double[][] getMultiPredict(double[] input) {
    double[] output1 = regress1.getMultiPredict(input);
    double[] output2 = null;

    switch (type) {
      case Series:
        output2 = regress2.getMultiPredict(output1);
        break;
      case Parallel:
        output2 = regress2.getMultiPredict(input);
        break;
    }
    double[][] output = new double[][] {
        output1, output2};
    output = operate(output);
    return output;
  }

  protected double[][] operate(double[][] output) {
    if (operator != null) {
      int size = output[0].length;
      for (int x = 0; x < size; x++) {
//        double a = output[0][x];
//        double b = output[1][x];
        double[] ab = operator.getXY(output[0][x], output[1][x]);
        output[0][x] = ab[0];
        output[1][x] = ab[1];
      }
    }
    return output;
  }

  public double[] getRMSD() {
    return new double[] {
        regress1.getRMSD(), regress2.getRMSD()};
  }

  public void regress() {
    regress1 = new PolynomialRegression(input, output1, polynomialCoef1);
    regress1.regress();

    switch (type) {
      case Series:
        regress2 = new PolynomialRegression(output1, output2, polynomialCoef2);
        break;
      case Parallel:
        regress2 = new PolynomialRegression(input, output2, polynomialCoef2);
        break;
    }
    regress2.regress();
  }
}
