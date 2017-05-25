package shu.cms.gma;

import shu.cms.colorspace.independ.*;
import shu.math.array.*;

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
public final class CrossPoint {

  private final static double[] ONE_VECTOR = new double[] {
      1, 1, 1};

  /**
   * 取得p0與p4的連線於triangle的交點
   * @param p0 CIELCh
   * @param p4 CIELCh
   * @param triangle CIELCh[]
   * @return CIELCh
   */
  public final static CIELCh getCrossPoint(CIELCh p0, CIELCh p4,
                                           CIELCh[] triangle) {
    CIELab p0Lab = new CIELab(p0);
    CIELab p4Lab = new CIELab(p4);
    CIELab[] triangleLab = new CIELab[3];
    triangleLab[0] = new CIELab(triangle[0]);
    triangleLab[1] = new CIELab(triangle[1]);
    triangleLab[2] = new CIELab(triangle[2]);

    CIELab cp = getCrossPoint(p0Lab, p4Lab, triangleLab);
    return new CIELCh(cp);
  }

  public final static CIELab getCrossPoint(CIELab p0, CIELab p4,
                                           CIELab[] triangle) {
    double[] p0v = plusOne(p0.getValues());
    double[] p4v = plusOne(p4.getValues());
    double[] p1v = plusOne(triangle[0].getValues());
    double[] p2v = plusOne(triangle[1].getValues());
    double[] p3v = plusOne(triangle[2].getValues());

    double[][] p123 = new double[][] {
        p1v, p2v, p3v
    };
    p123 = DoubleArray.transpose(p123);
    if (!DoubleArray.isNonsingular(p123)) {
      throw new IllegalStateException(
          "triangle is singular.");
//      return null;
    }
    p123 = DoubleArray.inverse(p123);
    double[] abc = DoubleArray.times(ONE_VECTOR, p123);
    double[] d04 = new double[] {
        p0v[0] - p4v[0], p0v[1] - p4v[1], p0v[2] - p4v[2]};
    double delta = DoubleArray.times(abc, d04);
    if (delta == 0) {
      throw new IllegalStateException(
          "there is no crosspoint between line p0-p4 and triangle.");
//      return null;
    }
    double eta = (1 - DoubleArray.times(abc, p4v)) / delta;
    double[] pp = DoubleArray.plus(DoubleArray.times(d04, eta), p4v);
    pp = minusOne(pp);
    CIELab Lab = new CIELab(pp);
    return Lab;
  }

  /**
   * crossPoint是否落在triangle裡
   * @param crossPoint CIELCh
   * @param triangle CIELCh[]
   * @return boolean
   */
  public final static boolean isInTriangle(CIELCh crossPoint, CIELCh[] triangle) {
    CIELab crossPointLab = new CIELab(crossPoint);
    CIELab[] triangleLab = new CIELab[3];
    triangleLab[0] = new CIELab(triangle[0]);
    triangleLab[1] = new CIELab(triangle[1]);
    triangleLab[2] = new CIELab(triangle[2]);
    return isInTriangle(crossPointLab, triangleLab);
  }

  public final static boolean isInTriangle(CIELab crossPoint, CIELab[] triangle) {
    double[] p0v = plusOne(crossPoint.getValues());
    double[] p1v = plusOne(triangle[0].getValues());
    double[] p2v = plusOne(triangle[1].getValues());
    double[] p3v = plusOne(triangle[2].getValues());

    double[][] p123 = new double[][] {
        p1v, p2v, p3v};
    p123 = DoubleArray.transpose(p123);
    if (!DoubleArray.isNonsingular(p123)) {
      return false;
    }
    p123 = DoubleArray.inverse(p123);
    double[] abr = DoubleArray.times(p123, p0v);
    if (abr[0] >= 0 && abr[1] >= 0 && abr[2] >= 0) {
      return true;
    }
    else {
      return false;
    }
  }

  private static double[] plusOne(double[] values) {
    return DoubleArray.plus(values, 1.);
//    return values;
  }

  private static double[] minusOne(double[] values) {
    return DoubleArray.minus(values, 1.);
//    return values;
  }
}
