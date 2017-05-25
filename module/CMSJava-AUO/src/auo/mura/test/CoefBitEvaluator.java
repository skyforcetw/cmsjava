package auo.mura.test;

import shu.plot.Plot2D;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 * @deprecated
 */
public class CoefBitEvaluator {
  static boolean round = false;
  static int r(double d) {
    if (round) {
      return (int) Math.round(d);
    }
    else {
      return (int) d;
    }
  }

  public static void main(String[] args) {
//    for (int x = 0; x < 100000; x++) {
//      int a = (int) (Math.random() * 256);
//      int b = (int) (Math.random() * 256);
//      int c = (int) (Math.random() * 256);
//      calculate(a, b, c);
//    }
    calculate(40, 40, 40);
  }

  public static void calculate(int plane1Value, int plane2Value,
                               int plane3Value) {
    int blackLimit = 0;
    int plane1 = 100;
    int plane2 = 204;
    int plane3 = 502;

    int whiteLimit = 1023;
    int endGrayLevel = 1023;

    double blackSlope = 1;
    double whiteSlope = 0;
    int minus = 1;

    int planeB1Coef = (int) Math.round(blackSlope * Math.pow(2, 16 - minus) /
                                       plane1); //12
    int plane12Coef = (int) Math.round(Math.pow(2, 16) / (plane2 - plane1)); //12
    int plane23Coef = (int) Math.round(Math.pow(2, 16) / (plane3 - plane2)); //12
    int plane3WCoef = (int) Math.round(whiteSlope * Math.pow(2, 16 - minus) /
                                       (endGrayLevel - plane3)); //12

    boolean showPlot = false;
    Plot2D plot = showPlot ? Plot2D.getInstance() : null;

    for (int x = 0; x <= 1023; x++) {
      int result = 0;

      if (x >= blackLimit && x < plane1) {
        double first = (plane1 - x) * planeB1Coef * plane1Value /
            Math.pow(2, 16 - minus);
        result = (int) Math.round(plane1Value - first);

      }
      else if (x >= plane1 && x < plane2) {

        result = (int) ( ( (plane2 - x) * plane1Value +
                          (x - plane1) * plane2Value) * plane12Coef /
                        Math.pow(2, 16));

      }
      else if (x >= plane2 && x < plane3) {
        result = (int) ( ( (plane3 - x) * plane2Value +
                          (x - plane2) * plane3Value) * plane23Coef /
                        Math.pow(2, 16));

      }
      else if (x >= plane3 && x <= whiteLimit) {
        result = plane3Value -
            (int) ( (x - plane3) * plane3WCoef * plane3Value /
                   Math.pow(2, 16 - minus));

      }

      System.out.println(x + " " + result);
    }
    if (showPlot) {
      plot.setVisible();
      plot.setFixedBounds(0, 0, 1023);
    }
  }
}
