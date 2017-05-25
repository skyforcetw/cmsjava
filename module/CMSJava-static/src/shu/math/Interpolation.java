package shu.math;

import flanagan.interpolation.*;
import shu.math.array.*;
import shu.math.regress.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 提供內插法的運算
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class Interpolation {
  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * LUMINANCE效果不好
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static enum Algo {
    CubicPolynomial, Lagrange, Lagrange4, Lagrange8, Linear, Spline2,
    QuadraticPolynomial, Gamma, Gamma2, Cubic
  }

  protected double[] _xn;
  protected double[] _yn;

  public Interpolation(double[] xn, double[] yn) {
    if (xn.length < 4 || yn.length < 4) {
      throw new IllegalArgumentException("xn.length < 4 || yn.length < 4");
    }
    this._xn = xn;
    this._yn = yn;
  }

  /**
   *
   * @param name String
   * @return Algo
   * @deprecated
   */
  public static Algo parseType(String name) {
    if (name.equalsIgnoreCase("CUBICPOLYNOMIAL")) {
      return Algo.CubicPolynomial;
    }
    else if (name.equalsIgnoreCase("LAGRANGE")) {
      return Algo.Lagrange;
    }
    return null;
  }

  public double interpolate(double x,
                            Algo interpolationType) {
    double[] xnpart = null;
    double[] ynpart = null;
    int noInterpolateIndex = -1;
    switch (interpolationType) {

      case QuadraticPolynomial: {
        int startIndex = getxnPartStartIndex(_xn, x, 3);
        if (startIndex < 0) {
          noInterpolateIndex = ( -startIndex) - 1;
          break;
        }
        xnpart = getPart(_xn, startIndex, 3);
        ynpart = getPart(_yn, startIndex, 3);
        break;
      }
      case Gamma:
      case Gamma2:
//      case Luminance:
      case CubicPolynomial:
      case Lagrange4: {
        int startIndex = getxnPartStartIndex(_xn, x, 4);
        if (startIndex < 0) {
          noInterpolateIndex = ( -startIndex) - 1;
          break;
        }
        xnpart = getPart(_xn, startIndex, 4);
        ynpart = getPart(_yn, startIndex, 4);
        break;
      }
//      case Gamma8:
      case Lagrange8: {
        int startIndex = getxnPartStartIndex(_xn, x, 8);
        if (startIndex < 0) {
          noInterpolateIndex = ( -startIndex) - 1;
          break;
        }
        xnpart = getPart(_xn, startIndex, 8);
        ynpart = getPart(_yn, startIndex, 8);
        break;
      }
      case Linear: {
        int startIndex = getxnPartStartIndex(_xn, x, 2);
        if (startIndex < 0) {
          noInterpolateIndex = ( -startIndex) - 1;
          break;
        }
        xnpart = getPart(_xn, startIndex, 2);
        ynpart = getPart(_yn, startIndex, 2);
        break;
      }
      default:
        xnpart = _xn;
        ynpart = _yn;
        break;
    }

    if (noInterpolateIndex != -1) {
      return _yn[noInterpolateIndex];
    }
    else {
      return interpolate(xnpart, ynpart, x, interpolationType);
    }
  }

  protected final static double[] getPart(double[] array, int startIndex,
                                          int width) {
    double[] part = new double[width];
    System.arraycopy(array, startIndex, part, 0, width);
    return part;
  }

  protected static int getxnPartStartIndex(double[] xn, double x,
                                           int width) {
    /**
     * 測試看看getxnPartStartIndexForExtrapolation套用下去會不會出問題
     */
    return getxnPartStartIndexForExtrapolation(xn, x, width);
  }

  /**
   * 找到xn的起始索引值(提供外插法使用)
   * @param xn double[]
   * @param x double
   * @param width int
   * @return int
   */
  protected static int getxnPartStartIndexForExtrapolation(double[] xn,
      double x, int width) {
    if (width < 2) {
      throw new IllegalArgumentException("width < 2");
    }
    int[] xnIndexArray = Searcher.leftNearBinarySearchAll(xn, x);
    if (xnIndexArray[1] >= 0) {
      return - (xnIndexArray[1] + 1);
    }
    int xnIndex = xnIndexArray[0] != -1 ? xnIndexArray[0] : -xnIndexArray[1];
    int startIndex = xnIndex - (width / 2 - 1);
    startIndex = startIndex < 0 ? 0 : startIndex;
    startIndex = startIndex > (xn.length - width) ? xn.length - width :
        startIndex;
    return startIndex;
  }

  /**
   * 由xn及yn的值,內插x對應的y值,且對xn,yn不進行處理,因此在限制變數數量的:
   * CUBICPOLYNOMIAL (4)
   * LAGRANGE4 (4)/LAGRANGE8 (8)
   * LINEAR (2)
   * 需自行處理過變數數量才傳入,否則會無法計算或計算錯誤.
   * 如需由程式自動擷取必要的變數範圍,請改用 物件方法interpolate (同名異式)
   * 而非本 類別方法interpolate
   *
   * @param xn double[]
   * @param yn double[]
   * @param x double
   * @param interpolationType Type
   * @return double
   */
  public static double interpolate(double[] xn, double[] yn, double x,
                                   Algo interpolationType) {

    switch (interpolationType) {
      case CubicPolynomial:
        return cubicPolynomial(xn, yn, x);
      case QuadraticPolynomial:
        return quadraticPolynomail(xn, yn, x);
      case Lagrange:
      case Lagrange4:
      case Lagrange8:
        return lagrange(xn, yn, x);
      case Linear:
        return linear2(xn, yn, x);
      case Spline2:
        return spline2(xn, yn, x);
      case Gamma:
        return gamma(xn, yn, x);
      case Gamma2:

        boolean calculable = isGamma2Calculable(xn, yn, x);
        if (calculable) {
          return gamma2(xn, yn, x);
        }
        else {
          return gamma(xn, yn, x);
        }
      case Cubic:
        return cubic(xn, yn, x);
      default:
        return -1;
    }
  }

  /**
   * 趨勢的確認, 用來確保沒有over fitting
   * @param xn double[]
   * @param yn double[]
   * @param x double
   * @param interpolationY double
   * @return boolean
   */
  public final static boolean trendCheck(double[] xn, double[] yn, double x,
                                         double interpolationY) {
    int index = Searcher.leftBinarySearch(xn, x);
    if (yn[index + 1] > yn[index]) {
      //右邊較大的狀況
      return interpolationY <= yn[index + 1] && interpolationY >= yn[index];
    }
    else {
      //左邊較大的狀況
      return interpolationY >= yn[index + 1] && interpolationY <= yn[index];
    }
  }

  /*public static double interpolate(double[] xn, double[] yn, double x,
                                   int interpolationType) {
    switch (interpolationType) {
      case CUBICPOLYNOMIAL:
        return cubicPolynomial(xn, yn, x);
      case LAGRANGE:
        return lagrange(xn, yn, x);
      case LINEAR:
        return linear(xn, yn, x);
      case SPLINE:
        return spline(xn, yn, x);
      case SPLINE2:
        return spline2(xn, yn, x);
      default:
        return -1;
    }
     }*/



  /**
   * 線性內插法
   * @param x1 double
   * @param x2 double
   * @param y1 double
   * @param y2 double
   * @param x double
   * @return double
   */
  public static double linear(double x1, double x2, double y1,
                              double y2, double x) {
    double ratio = (x - x1) / (x2 - x1);
    return y1 + (y2 - y1) * ratio;
  }

  public static double linear(double[] xn, double[] yn, double x) {
//    int index = Searcher.leftNearBinarySearch(xn, x);
    int index = getxnPartStartIndex(xn, x, 2);
    return linear(xn[index], xn[index + 1], yn[index], yn[index + 1], x);
  }

  /**
   *
   * @param xn double[]
   * @param yn double[]
   * @param x double
   * @return double
   * @deprecated
   */
  public static double luminance(double[] xn, double[] yn, double x) {
    if (xn.length != 4 || yn.length != 4) {
      throw new IllegalArgumentException("xn.length != 4 || yn.length != 4");
    }
    if (Searcher.leftNearBinarySearch(xn, x) != 1) {
//      System.out.println("li");
      return linear(xn, yn, x);
    }

    double[] diff = Maths.firstOrderDerivatives(yn);
    double lowerRatio = diff[0] / (diff[0] + diff[2]);
    double ratio = linear(new double[] {xn[1], (xn[1] + xn[2]) / 2., xn[2]},
                          new double[] {0, lowerRatio, 1}, x);
    double adjustx = (xn[2] - xn[1]) * ratio + xn[1];
    double result = linear(xn, yn, adjustx);
    return result;
  }

  public static double linear2(double[] xn, double[] yn, double x) {
    if (xn.length != 2 || yn.length != 2) {
      throw new IllegalArgumentException();
    }
    return linear(xn[0], xn[1], yn[0], yn[1], x);
  }

  public static double quadraticPolynomail(double[] xn, double[] yn, double x) {
    if (xn.length != 3 || yn.length != 3) {
      throw new IllegalArgumentException("xn.length != 3 || yn.length != 3");
    }
    PolynomialRegression regress = new PolynomialRegression(xn, yn,
        Polynomial.COEF_1.BY_2C);
    regress.regress();
    return regress.getPredict(new double[] {x})[0];
  }

  public static double cubic(double[] xn, double[] yn, double x) {
    return cubic(xn, yn, x, 0);
  }

  public static double cubic(double[] xn, double[] yn, double x,
                             int numerDiffOption) {
    if (xn.length < 3 || yn.length < 3) {
      throw new IllegalArgumentException("xn.length < 3 || yn.length < 3");
    }
    CubicInterpolation cubic = new CubicInterpolation(xn, yn, numerDiffOption);
    return cubic.interpolate(x);
  }

  /**
   * 三次方多項式內插
   * @param xn double[]
   * @param yn double[]
   * @param x double
   * @return double
   */
  public static double cubicPolynomial(double[] xn, double[] yn, double x) {
    if (xn.length != 4 || yn.length != 4) {
      throw new IllegalArgumentException("xn.length != 4 || yn.length != 4");
    }

    double[][] a1 = new double[4][];
    for (int i = 0; i < 4; i++) {
      double sqr = Maths.sqr(xn[i]);
      a1[i] = new double[] {
          xn[i] * sqr, sqr, xn[i], 1};
    }
    double[][] a2 = DoubleArray.transpose(yn);

    if (!DoubleArray.isNonsingular(a1)) {
      return Double.NaN;
    }

    double[][] coef = DoubleArray.times(DoubleArray.inverse(a1), a2);
    double[][] result = DoubleArray.times(new double[][] { {Math.pow(x, 3),
                                          Math.pow(x, 2), x, 1}
    }, coef);
    return result[0][0];
  }

  /**
   * from apache.commons.math.analysis
   * 類似PLCC原理的內插法
   * @param xn double[]
   * @param yn double[]
   * @param x double
   * @return double
   */
//  public static double spline(double[] xn, double[] yn, double x) {
//
//    UnivariateRealInterpolator interpolator = new SplineInterpolator();
//    UnivariateRealFunction function = null;
//    try {
//      function = interpolator.interpolate(xn, yn);
//      return function.value(x);
//    }
//    catch (MathException ex) {
//      ex.printStackTrace();
//      throw new IllegalArgumentException();
//    }
//  }

  /**
   * from flanagan.interpolation
   * 類似PLCC原理的內插法
   * @param xn double[]
   * @param yn double[]
   * @param x double
   * @return double
   */
  public static double spline2(double[] xn, double[] yn, double x) {
    if (x < xn[0] || x > xn[xn.length - 1]) {
      throw new IllegalArgumentException(
          "x (" + x + ") is outside the range of data points (" + xn[0] +
          " to " + xn[xn.length - 1] + ")");
    }
    CubicSpline cs = new CubicSpline(xn, yn);
    return cs.interpolate(x);
  }

  public double lagrange(double x, int dataWidth) {
    int startIndex = getxnPartStartIndex(_xn, x, dataWidth);
    double[] xnpart = getPart(_xn, startIndex, dataWidth);
    double[] ynpart = getPart(_yn, startIndex, dataWidth);

    return lagrange(xnpart, ynpart, x);
  }

  /**
   * lagrange的內插法
   * @param xn double[] 已知x值集合
   * @param yn double[] 已知y值集合
   * @param x double 欲求x值
   * @return double 對應到x值內插而得的y值
   */
  public static double lagrange(double[] xn, double[] yn, double x) {
    if (xn.length != yn.length) {
      throw new IllegalArgumentException();
    }

    int size = xn.length;
    double value = 0.0;
    for (int i = 0; i < size; i++) {

      double num = 1.0;
      for (int j = 0; j < size - 1; j++) {
        num *= x - xn[ (i + j + 1) % size];
      }
      double denom = 1.0;
      for (int j = 0; j < size - 1; j++) {
        denom *= xn[i] - xn[ (i + j + 1) % size];
      }
      value += (num * yn[i]) / denom;
    }

    return value;
  }

  public static double gamma(final double[] xn, final double[] yn, double x) {
    if (xn.length != yn.length) {
      throw new IllegalArgumentException("xn.length != yn.length");
    }
    int size = xn.length;
    double[] xn2 = DoubleArray.copy(xn);
    double[] yn2 = DoubleArray.copy(yn);
    Maths.normalize(xn2);
    Maths.normalize(yn2);
    double gammax = (x - xn[0]) / (xn[size - 1] - xn[0]);

    double gamma = GammaFinder.findGamma(xn2, yn2);
    if (Double.isNaN(gamma)) {
      return linear(xn, yn, x);
    }
    double gammay = Math.pow(gammax, gamma);
    double y = gammay * (yn[size - 1] - yn[0]) + yn[0];
    return y;
  }

  public static boolean isGamma2Calculable(final double[] xn, final double[] yn,
                                           double x) {
    if (xn.length != yn.length) {
      throw new IllegalArgumentException("xn.length != yn.length");
    }
    int size = xn.length;
    int index = Searcher.leftNearSequentialSearch(xn, x);
    return! (index < 1 || (index + 1) >= (size - 1));
  }

  public static double gamma2(final double[] xn, final double[] yn, double x) {
    if (xn.length != yn.length) {
      throw new IllegalArgumentException("xn.length != yn.length");
    }
    int size = xn.length;
    int index = Searcher.leftNearSequentialSearch(xn, x);
    if (!isGamma2Calculable(xn, yn, x)) {
      throw new IllegalArgumentException("x cannot adjacent to head or tail.");
    }
    double[] xn1 = new double[] {
        xn[0], xn[index], xn[size - 1]};
    double[] xn2 = new double[] {
        xn[0], xn[index + 1], xn[size - 1]};
    Maths.normalize(xn1);
    Maths.normalize(xn2);
    double[] yn1 = new double[] {
        yn[0], yn[index], yn[size - 1]};
    double[] yn2 = new double[] {
        yn[0], yn[index + 1], yn[size - 1]};
    Maths.normalize(yn1);
    Maths.normalize(yn2);

    double gamma1 = GammaFinder.findGamma(xn1, yn1);
    double gamma2 = GammaFinder.findGamma(xn2, yn2);
    double gamma = linear(xn[index], xn[index + 1], gamma1, gamma2, x);
    double normalx = (x - xn[0]) / (xn[size - 1] - xn[0]);
    double gammay = Math.pow(normalx, gamma);
    double y = gammay * (yn[size - 1] - yn[0]) + yn[0];
//    double y = (yn[size-1]-yn[0])
//    linear(

//    double[] yn2 = new double[3];
//
//    double[] xn2 = Maths.normalize(DoubleArray.copy(xn));
//    double[] yn2 = Maths.normalize(DoubleArray.copy(yn));
//    double gammax = (x - xn[0]) / (xn[size - 1] - xn[0]);
//
//    double gamma = GammaFinder.findingGamma(xn2, yn2);
//    if (Double.isNaN(gamma)) {
//      return linear(xn, yn, x);
//    }
//    double gammay = Math.pow(gammax, gamma);
//    double y = gammay * (yn[size - 1] - yn[0]) + yn[0];
//    return y;
    return y;
  }
}
