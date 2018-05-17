package shu.cms.gma;

import java.awt.*;
import javax.swing.*;

import org.math.plot.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 收錄了各種壓縮的方式
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class Mapper {

  /**
   *
   * @param reproductionMax CIELCh
   * @param dot CIELCh
   * @return CIELCh
   */
  public static CIELCh chromaClipping(CIELCh reproductionMax, CIELCh dot) {
    CIELCh result = (CIELCh) dot.clone();

    if (dot.C > reproductionMax.C) {
//      result.L = reproductionMax.L;
      result.C = reproductionMax.C;
    }
    return result;
  }

  public static CIELCh LCClipping(CIELCh reproductionMax, CIELCh dot) {
    CIELCh result = (CIELCh) dot.clone();

    if (dot.C > reproductionMax.C) {
      result.L = reproductionMax.L;
      result.C = reproductionMax.C;
    }
    return result;
  }

  public static CIELCh LChClipping(CIELCh reproductionMax, CIELCh dot) {
    CIELCh result = (CIELCh) dot.clone();

    if (dot.C > reproductionMax.C) {
      result.L = reproductionMax.L;
      result.C = reproductionMax.C;
      result.h = reproductionMax.h;
    }
    return result;
  }

  public static double linearCompression(double originalMax,
                                         double reproductionMax,
                                         double dot) {
    return reproductionMax * dot / originalMax;
  }

  public static double gammaCompression(double originalMax,
                                        double reproductionMax,
                                        double dot, double gamma) {
    return reproductionMax * Math.pow(dot / originalMax, gamma);
  }

  public static double SCompression(double originalMax,
                                    double reproductionMax,
                                    double dot, double gammaHigh,
                                    double gammaLow, double turn) {
    if (turn > reproductionMax) {
      throw new IllegalArgumentException("turn > reproductionMax");
    }
    if (dot > reproductionMax) {
      return turn + gammaCompression(originalMax - reproductionMax,
                                     reproductionMax - turn,
                                     dot - reproductionMax,
                                     gammaHigh);
    }
    else if (dot < reproductionMax) {
      return gammaCompression(reproductionMax,
                              turn, dot,
                              gammaLow);
    }
    else {
      return turn;
    }
  }

  protected static double f(double A, double x, double alpha, double beta,
                            double C) {
    return A * Math.pow(x, alpha) / (Math.pow(x, beta) + C);
  }

  public static void main(String[] args) {

    Plot2D plot = Plot2D.getInstance();
    for (int x = 0; x < 101; x++) {
      double compress = gammaCompression(100, 70, x, .5);
      double linear = linearCompression(100, 70, x);
      double s = SCompression(100, 70, x, 2.2, .6, 50);
      plot.addCacheScatterLinePlot("compress", x, compress);
      plot.addCacheScatterLinePlot("linear", x, linear);
      plot.addCacheScatterLinePlot("s", x, s);
      plot.addCacheScatterLinePlot("org", x, x);
    }
    plot.setVisible();
  }
}
