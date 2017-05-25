package shu.math.test;

import java.text.*;

import shu.math.*;
import shu.math.array.*;
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
public class CoefficientFinder {
  public static void main(String[] args) {
    //==========================================================================
    // 變數產生
    //==========================================================================
    int size = 80;
    double[][] output = new double[size][];
    double[][] input = new double[size][];
    for (int x = 0; x < size; x++) {
      double[] in = new double[] {
          x, x * 2. + x / 2., Math.sqrt(x / 135.)};
      input[x] = in;
      output[x] = new double[1];
      output[x][0] = in[0] * in[1] + in[2] * 5. * in[0] + in[0];
//       output[x][1] = in[0] * in[1] + 4 * in[2];
//        output[x][2] = in[0] * in[1] + 4 * in[2];
      System.out.println(x + ": " + DoubleArray.toString(in) + " " +
                         DoubleArray.toString(output[x]));
    }
    //==========================================================================



//    Polynomial.COEF coef = Polynomial.COEF_3.BY_3;
    Polynomial.COEF coef = PolynomialRegression.findBestPolynomialCoefficient3(
        input, output);
    System.out.println(coef);
    double[][] coefInput = new double[size][];
    for (int x = 0; x < size; x++) {
      coefInput[x] = Polynomial.getCoef(input[x], coef);
    }

    PolynomialRegression regress = new PolynomialRegression(input, output, coef);
    regress.regress();
    double[][] coefs = regress.getCoefs();
    String s = DoubleArray.toString(new DecimalFormat("##.###"), coefs);
    System.out.println(s);
    System.out.println(regress.getRMSD());

    regress.setCoefs(new double[][] { {0, 1, 0, 0, 1, 0, 5., 0}
    });
    System.out.println(regress.getRMSD());
  }
}
