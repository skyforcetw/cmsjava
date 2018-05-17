package shu.cms.hvs;

import flanagan.math.*;
import shu.cms.plot.*;
///import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class ContrastSensitivityFunction {
  public static Barten barten = new Barten();
  public static Dusan dusan = new Dusan();
  public static Movshon movshon = new Movshon();
  public static Stephen stephen = new Stephen();
  public static double achromaticCSF(double f, Instance instance) {
    switch (instance) {
      case Movshon:
        return movshon.achromaticCSF(f);
      case Barten:
        return barten.achromaticCSF(f);
      case Stephen:
        return stephen.achromaticCSF(f);
      case Dusan:
        return dusan.achromaticCSF(f);
      default:
        throw new IllegalArgumentException("instance is unknow");
    }
  }

  public static double rgChromaticCSF(double f, Instance instance) {
    switch (instance) {
      case Movshon:
        return movshon.rgChromaticCSF(f);
      case Barten:
        return barten.rgChromaticCSF(f);
      case Stephen:
        return stephen.rgChromaticCSF(f);
      case Dusan:
        return dusan.rgChromaticCSF(f);
      default:
        throw new IllegalArgumentException("instance is unknow");
    }

  }

  public static boolean isAchromaticVisible(double contrast, double frequence,
                                            Instance instance) {
    switch (instance) {
      case Movshon:
        return movshon.isAchromaticVisible(contrast, frequence);
      case Barten:
        return barten.isAchromaticVisible(contrast, frequence);
      case Stephen:
        return stephen.isAchromaticVisible(contrast, frequence);
      case Dusan:
        return dusan.isAchromaticVisible(contrast, frequence);
      default:
        throw new IllegalArgumentException("instance is unknow");
    }

  }

  public static double byChromaticCSF(double f, Instance instance) {
    switch (instance) {
      case Movshon:
        return movshon.byChromaticCSF(f);
      case Barten:
        return barten.byChromaticCSF(f);
      case Stephen:
        return stephen.byChromaticCSF(f);
      case Dusan:
        return dusan.byChromaticCSF(f);
      default:
        throw new IllegalArgumentException("instance is unknow");
    }

  }

  public abstract double achromaticCSF(double f);

  protected MaxInvisibleContrastFunc func = null;
  protected class MaxInvisibleContrastFunc
      implements MaximisationFunction {
    /**
     * function
     *
     * @param doubleArray double[]
     * @return double
     */
    public double function(double[] doubleArray) {
      double f = doubleArray[0];
      double invisibleContrast = achromaticCSF(f);
      return invisibleContrast;
    }

  }

  /**
   * 回傳任何頻率下都無法察覺的頻率及對比
   * @return double[] {頻率,對比}
   */
  public final double[] getAchromaticInvisible() {
    //Create instance of Maximisation
    Maximisation max = new Maximisation();

    if (func == null) {
      // Create instace of class holding function to be maximised
      func = new MaxInvisibleContrastFunc();
    }

    // initial estimates
    double[] start = {
        4.0D};

    // initial step sizes
    double[] step = {
        0.1D};

    // convergence tolerance
    double ftol = 0.05;

    max.addConstraint(0, -1, 0);
    max.addConstraint(0, 1, 100);

    // Nelder and Mead maximisation procedure
    max.nelderMead(func, start, step, ftol);
    // get the maximum value
    double maximum = max.getMaximum();
//    System.out.println( max.getNiter());

    // get values of y and z at maximum
    double[] param = max.getParamValues();
    double[] result = new double[] {
        param[0], maximum};
    return result;
  }

  public final boolean isAchromaticVisible(double contrast) {
    double s = 1. / contrast;
    double[] invisible = getAchromaticInvisible();
    double cs = invisible[1];
    return s < cs;
  }

  /**
   * 被察覺的可能性
   * @param contrast double
   * @return double >1則可能被看見, <1則可能看不見
   */
  public final double visibleRatio(double contrast) {
    double s = 1. / contrast;
    double[] invisible = getAchromaticInvisible();
    double cs = invisible[1];
    return cs / s;
  }

  public final double visibleRatio(double contrast, double frequence) {
    double s = 1. / contrast;
    double cs = achromaticCSF(frequence);
    return cs / s;
  }

  public final boolean isAchromaticVisible(double contrast, double frequence) {
    double s = 1. / contrast;
    double cs = achromaticCSF(frequence);
    return s < cs;
  }

  public abstract double rgChromaticCSF(double f);

  public abstract double byChromaticCSF(double f);

  public enum Instance {
    //最簡單的model
    Movshon(movshon),
    //考量亮度及size
    Barten(barten),
    //考量亮度、size及背景的色度
    Stephen(stephen),
    //不知道哪裡來的data
    Dusan(dusan);

    Instance(ContrastSensitivityFunction csf) {
      this.csf = csf;
    }

    protected ContrastSensitivityFunction csf;
  }

  public final static ContrastSensitivityFunction getInstance(Instance instance) {
    return instance.csf;
  }

  public static class Movshon
      extends ContrastSensitivityFunction {
    public double achromaticCSF(double f) {
      double s = 75 * Math.pow(f, 0.8) * Math.pow(Math.E, -0.2 * f);
      s = s / 100 * 200;
      return s;
    }

    public double rgChromaticCSF(double f) {
      return chromaticCSF(f, 109.14130, -0.00038, 3.42436, 93.59711, -0.00367,
                          2.16771);
    }

    public double chromaticCSF(double f, double a1, double b1, double c1,
                               double a2, double b2, double c2) {
      double e = Math.E;
      return a1 * Math.pow(e, (b1 * Math.pow(f, c1))) +
          a2 * Math.pow(e, (b2 * Math.pow(f, c2)));
    }

    public double byChromaticCSF(double f) {
      return chromaticCSF(f, 7.032845, -0.000004, 4.258205, 40.690950,
                          -0.103909,
                          1.648658);
    }
  }

  /**
   * 刺激的大小(sizeInDegrees)
   */
  protected double w = 10;
  //亮度
  protected double L = 50;
  /**
   * CIEu'v'空間上的距離
   */
  protected double d = 0;
  public void setStimulusSize(double sizeInDegrees) {
    this.w = sizeInDegrees;
  }

  public void setMeanLuminance(double luminance) {
    this.L = luminance;
  }

  public void setDistance(double dist) {
    this.d = dist;
  }

  public static class Barten
      extends ContrastSensitivityFunction {

    public double achromaticCSF(double f) {
      double a = (540. * Math.pow( (1. + 0.7 / L), -0.2)) /
          (1 + 12 * Math.pow( (1. + f / 3.), -2) / w);
      double b = 0.3 * Math.pow( (1 + 100. / L), 0.15);
      double c = 0.06;
      double s = csf(a, f, b, c);
      return s;
    }

    protected static double csf(double a, double f, double b, double c) {
      double e = Math.E;
      double s = a * f * Math.pow(e, -b * f) *
          Math.pow( (1 + c * Math.pow(e, b * f)), 0.5);
      return s;
    }

    public double rgChromaticCSF(double f) {
      throw new UnsupportedOperationException("");
    }

    public double byChromaticCSF(double f) {
      throw new UnsupportedOperationException("");
    }
  }

  public static class Stephen
      extends Barten {

    public double achromaticCSF(double f, double uvPrimeDistance,
                                double meanLuminance, double stimulusSize) {

      setDistance(uvPrimeDistance);
      setMeanLuminance(meanLuminance);
      setStimulusSize(stimulusSize);
      double s = achromaticCSF(f);
      return s;
    }

    public double achromaticCSF(double f) {
      double p1 = 0.6349 * (1 - 10.5102 * d);
      double s = csf(p1, 0.2186, 0.1434, L, w, f);
      return s;
    }

    protected static double a(double p1, double L, double f, double w) {
      double a = (1000. * p1 * Math.pow( (1. + 0.7 / L), -0.2)) /
          (1 + 12 * Math.pow( (1. + f / 3.), -2) / w);
      return a;
    }

    protected static double b(double p2, double L) {
      double b = p2 * Math.pow( (1 + 100. / L), 0.15);
      return b;
    }

    protected static double csf(double p1, double p2, double p3, double L,
                                double w, double f) {
      double a = a(p1, L, f, w);
      double b = b(p2, L);
      double c = p3;
      double s = csf(a, f, b, c);
      return s;
    }

    public double rgChromaticCSF(double f) {
      throw new UnsupportedOperationException("");
    }

    public double byChromaticCSF(double f) {
      throw new UnsupportedOperationException("");
    }
  }

  public static class Dusan
      extends ContrastSensitivityFunction {

    public double achromaticCSF(double f) {
      double e = Math.E;
      double s = 2.6 * (0.0192 + 0.114 * f) *
          Math.pow(e, -Math.pow(0.114 * f, 1.1));
      return s;
    }

    public double rgChromaticCSF(double f) {
      throw new UnsupportedOperationException("");
    }

    public double byChromaticCSF(double f) {
      throw new UnsupportedOperationException("");
    }
  }

  public static void main(String[] args) {
    Plot2D p = Plot2D.getInstance();
    ContrastSensitivityFunction csf = ContrastSensitivityFunction.barten;
    csf.setStimulusSize(1);

    double[] lumis = new double[] {
        10, 15.8, 25.5, 31.6};
    double[] dists = new double[] {
        30, 60};

    for (double dist : dists) {
      for (double lumi : lumis) {
        csf.setDistance(dist);
        for (double f = 0.1; f < 10; f += 0.1) {
          csf.setMeanLuminance(lumi);
          double ct = csf.achromaticCSF(f);
          p.addCacheScatterLinePlot(Double.toString(lumi) + "-" +
                                    Double.toString(dist), f, 1. / ct);
        }
      }
    }
    p.drawCachePlot();
    p.setAxisScale(0, Plot2D.Scale.Log);
    p.setFixedBounds(0, 0.01, 10);
    p.setFixedBounds(1, 0, 0.05);

//    p.setAxisScale(1, Plot2D.Scale.Log);
//    p.addLegend();
    p.setVisible();

  }

//  public static double achromaticCSF(double f) {
//    double s = 75 * Math.pow(f, 0.8) * Math.pow(Math.E, -0.2 * f);
//    s = s / 100 * 200;
//    return s;
//  }

  /**
   * 是否可被人眼所察覺
   * @param contrast double 對比
   * @param frequence double 頻率
   * @return boolean
   */
//  public static boolean isAchromaticVisible(double contrast, double frequence) {
//    double s = 1. / contrast;
//    double cs = achromaticCSF(frequence);
//    return s < cs;
//  }

//  public static double chromaticCSF_rg(double f) {
//    return chromaticCSF(f, 109.14130, -0.00038, 3.42436, 93.59711, -0.00367,
//                        2.16771);
//  }
//
//  public static double chromaticCSF(double f, double a1, double b1, double c1,
//                                    double a2, double b2, double c2) {
//    double e = Math.E;
//    return a1 * Math.pow(e, (b1 * Math.pow(f, c1))) +
//        a2 * Math.pow(e, (b2 * Math.pow(f, c2)));
//  }

//  public static double chromaticCSF_by(double f) {
//    return chromaticCSF(f, 7.032845, -0.000004, 4.258205, 40.690950, -0.103909,
//                        1.648658);
//  }

}
