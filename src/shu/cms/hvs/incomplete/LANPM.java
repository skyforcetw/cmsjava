package shu.cms.hvs.incomplete;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
//import shu.plot.*;

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
public class LANPM {
  public LANPM(CIEXYZ complete, CIEXYZ incomplete) {
    this.complete = complete;
    this.incomplete = incomplete;
  }

  private CIEXYZ complete;
  private CIEXYZ incomplete;

  protected double[][] getManp() {
    double Yr = getYr(incomplete.Y);
    return getM(Yr);
  }

  public static void main(String[] args) {
    Plot2D p = Plot2D.getInstance();

    int[] CCTArray = new int[] {
        2985, 3350, 4000, 4500, 4975, 5500, 5900, 6650, 6900, 9800, 17240};

    for (int CCT : CCTArray) {
      for (double Yr = 1; Yr <= 300; Yr++) {
        CIExyY xyY = CorrelatedColorTemperature.CCT2BlackbodyxyY(CCT);
        xyY.Y = 10000;
        double Y = 10000. / Yr;
        CIExyY incomplete = (CIExyY) xyY.clone();
        incomplete.Y = Y;
        CIEXYZ XYZ = xyY.toXYZ();
        LANPM lanpm = new LANPM(XYZ, incomplete.toXYZ());
        double[][] Manp = lanpm.getManp();
        double[] result = DoubleArray.times(Manp, XYZ.getValues());
        CIEXYZ resultXYZ = new CIEXYZ(result);
        double resultCCT = CorrelatedColorTemperature.XYZ2CCTByRobertson(
            resultXYZ);
        p.addCacheScatterLinePlot(Integer.toString(CCT), Yr, resultCCT);
      }
    }
    p.setVisible();
  }

  protected static double getYr(double Y) {
    return 10000. / Y;
  }

  protected final static double[][] getM(double Yr) {
//    double Yr = getYr(Y);
//    double Yr = Y2/Y1;
    double m11 = getMij(M11, Yr);
    double m12 = getMij(M12, Yr);
    double m22 = getMij(M22, Yr);
    double m31 = getMij(M31, Yr);
    double m32 = getMij(M32, Yr);
    double m33 = getMij(M33, Yr);
    double[][] M = new double[][] {
        {
        m11, m12, 0}, {
        0, m22, 0}, {
        m31, m32, m33}
    };
    return M;
  }

  protected final static double getMij(double[] MCoefficients, double Yr) {
    double[] M = MCoefficients;
//    double Yr10 = Math.pow(10,Yr);
    double logYr = Math.log10(Yr);
//    double logYr = Math.log(YR);
//double logYr=    Math.exp(Yr);
    return M[0] + M[1] * logYr + M[2] * Maths.sqr(logYr) +
        M[3] * Math.pow(logYr, 3);
  }

  public final static double[] M11 = new double[] {
      0.236127, -0.238463, 0.079615, -0.008769};
  public final static double[] M12 = new double[] {
      -0.01446, 0.022991, -0.009535, 0.0011919};
  public final static double[] M22 = new double[] {
      0.22182, -0.215768, 0.070227, -0.007598};
  public final static double[] M31 = new double[] {
      0.0096571, -0.019651, 0.0085579, -0.0010708};
  public final static double[] M32 = new double[] {
      -0.027298, 0.048127, -0.020417, 0.0025542};
  public final static double[] M33 = new double[] {
      0.238429, -0.242869, 0.081576, -0.0090233};

}
