package shu.math.operator;

import java.util.*;

import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * ¥­²¾
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class OffsetOperator
    extends Operator {
  public boolean equals(Object obj) {
    return Arrays.equals(center, ( (OffsetOperator) obj).center) &&
        super.equals(obj);
  }

  protected double[] center;
  public OffsetOperator(double[] center) {
//    if (center.length != 2) {
//      throw new IllegalArgumentException("center.length != 2");
//    }
    this.center = center;
  }

  public Operator getReverseOperator() {
    return new OffsetOperator(DoubleArray.minus(new double[center.length],
                                                center));
  }

  public double[] _getXY(double ...xy) {
    return DoubleArray.minus(xy, center);
//    return new double[] {
//        x - center[0], y - center[1]};
  }

  public static void main(String[] args) {
    OffsetOperator offset = new OffsetOperator(new double[] {4, 5});
    RotationOperator op = new RotationOperator(90);
    offset.addOperator(op);
    System.out.println(DoubleArray.toString(offset.getXY(5, 5)));
  }
}
