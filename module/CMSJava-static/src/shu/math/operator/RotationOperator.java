package shu.math.operator;

import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * ±ÛÂà
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class RotationOperator
    extends Operator {
  protected double angle;
  public RotationOperator(double angle) {
    this.angle = angle;
  }

  public boolean equals(Object obj) {
    return angle == ( (RotationOperator) obj).angle &&
        super.equals(obj);
  }

  public Operator getReverseOperator() {
    return new RotationOperator( -angle);
  }

  /**
   * getXY
   *
   * @param x double
   * @param y double
   * @return double[]
   */
  public double[] _getXY(double ...xy) {
    double[] Ch = cartesian2polarCoordinatesValues(xy);
    Ch[1] += angle;
    double[] ab = polar2cartesianCoordinatesValues(Ch);
    return new double[] {
        ab[0], ab[1]};
  }

  public static void main(String[] args) {
    RotationOperator op = new RotationOperator(90);
    System.out.println(DoubleArray.toString(op.getXY(5, 5)));
  }
}
