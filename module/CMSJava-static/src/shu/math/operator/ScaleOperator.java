package shu.math.operator;

import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * ÁY©ñ
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ScaleOperator
    extends Operator {
  protected double scale;

  public boolean equals(Object obj) {
    return scale == ( (ScaleOperator) obj).scale &&
        super.equals(obj);
  }

  public ScaleOperator(double scale) {
    this.scale = scale;
  }

  public Operator getReverseOperator() {
    return new ScaleOperator( -scale);
  }

  /**
   * getXY
   *
   * @param x double
   * @param y double
   * @return double[]
   */
  public double[] _getXY(double... xy) {
//    double[] LCh = CIELCh.cartesian2polarCoordinatesValues(new double[] {0, x,
//        y});
//    LCh[1] *= scale;
//    double[] Lab = CIELCh.polar2cartesianCoordinatesValues(LCh);
//    return new double[] {
//        Lab[1], Lab[2]};

    double[] Ch = cartesian2polarCoordinatesValues(xy);
    Ch[0] *= scale;
    double[] ab = polar2cartesianCoordinatesValues(Ch);
    return new double[] {
        ab[0], ab[1]};
  }

  public static void main(String[] args) {
    ScaleOperator op = new ScaleOperator(2);
    System.out.println(DoubleArray.toString(op.getXY(5, 5)));
  }
}
