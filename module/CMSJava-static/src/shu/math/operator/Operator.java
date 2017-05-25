package shu.math.operator;

import java.util.*;

import shu.math.array.*;

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
public abstract class Operator {
  public final double[] getXY(double[] xy) {
    double[] result = _getXY(xy);
    for (Operator o : operatorList) {
      result = o.getXY(result);
    }
    return result;
  }

  public final double[] getXYZ(double[] xyz) {
    double[] result = _getXY(xyz);
    for (Operator o : operatorList) {
      result = o.getXY(result);
    }
    return result;
  }

  public abstract Operator getReverseOperator();

  public final double[] getXY(double x, double y) {
    return getXY(new double[] {x, y});
  }

  protected abstract double[] _getXY(double ...xy);

//  public final double[] getXYZ(double x, double y,double z) {
//  return getXY(new double[] {x, y});
//}
//
//protected abstract double[] _getXY(double x, double y);


  protected List<Operator> operatorList = new ArrayList<Operator> ();
  public final void addOperator(Operator operator) {
    operatorList.add(operator);
  }

  public final void addOperators(Operator ...operators) {
    for (Operator o : operators) {
      operatorList.add(o);
    }
  }

  /**
   * start1不動, 將end2調到end1
   * @param start1 double[]
   * @param end1 double[]
   * @param end2 double[]
   * @return Operator
   */
  public final static Operator getAdjustOperator(double[] start1, double[] end1,
                                                 double[] end2) {
    //==========================================================================
    // 調整的參數計算
    //==========================================================================
    OffsetOperator offsetOP = new OffsetOperator(start1);
    double[] end2offset = offsetOP.getXY(end2);
    double[] end1offset = offsetOP.getXY(end1);
    double[] end2Ch = cartesian2polarCoordinatesValues(new double[] {
        end2offset[0], end2offset[1]});
    double[] end1Ch = cartesian2polarCoordinatesValues(new double[] {
        end1offset[0], end1offset[1]});
    double hueOffset = end2Ch[1] - end1Ch[1];
    double scale = end2Ch[0] / end1Ch[0];
    //==========================================================================

    //==========================================================================
    // 產生調整用OP
    //==========================================================================
    RotationOperator rotationOP = new RotationOperator(hueOffset);
    ScaleOperator scaleOP = new ScaleOperator(scale);
    OffsetOperator unoffsetOP = (OffsetOperator) offsetOP.getReverseOperator();
    offsetOP.addOperator(rotationOP);
    offsetOP.addOperator(scaleOP);
    offsetOP.addOperator(unoffsetOP);
    //==========================================================================
    return offsetOP;
  }

  /**
   * 將start2調到start1, end2調到end1
   * @param start1 double[]
   * @param end1 double[]
   * @param start2 double[]
   * @param end2 double[]
   * @return Operator
   */
  public final static Operator getAdjustOperator(double[] start1, double[] end1,
                                                 double[] start2, double[] end2) {
    double[] centerOffset = DoubleArray.minus(start2, start1);
    OffsetOperator recenterOP = new OffsetOperator(centerOffset);
    double[] newe2 = recenterOP.getXY(end2);

    //1.將start1歸零
    OffsetOperator offsetOP = new OffsetOperator(start1);
    //2.計算出歸零後的e1,e2
    double[] offsete1 = offsetOP.getXY(end1);
    double[] offsete2 = offsetOP.getXY(newe2);
    //3.計算出極座標的e1,e2
    double[] che1 = cartesian2polarCoordinatesValues(offsete1);
    double[] che2 = cartesian2polarCoordinatesValues(offsete2);
    //4.計算角度差和比例差
    double hueOffset = che1[1] - che2[1];
    double scale = che1[0] / che2[0];

    RotationOperator rotationOP = new RotationOperator(hueOffset);
    ScaleOperator scaleOP = new ScaleOperator(scale);
    OffsetOperator unoffsetOP = (OffsetOperator) offsetOP.getReverseOperator();
    recenterOP.addOperator(offsetOP);
    recenterOP.addOperator(rotationOP);
    recenterOP.addOperator(scaleOP);
    recenterOP.addOperator(unoffsetOP);

    return recenterOP;
  }

  public static final double[] cartesian2polarCoordinatesValues(final double[]
      cartesianValues) {
    double[] polarValues = new double[2];

//    polarValues[0] = cartesianValues[0];
    double t1 = cartesianValues[0];
    double t2 = cartesianValues[1];
    polarValues[0] = Math.sqrt(Math.pow(cartesianValues[0], 2) +
                               Math.pow(cartesianValues[1], 2));
    if (t1 == 0 && t2 == 0) {
      polarValues[1] = 0;
    }
    else {
      polarValues[1] = Math.atan2(t2, t1);
    }
    polarValues[1] *= (180.0 / Math.PI);
    while (polarValues[1] >= 360.0) { // Not necessary, but included as a check.
      polarValues[1] -= 360.0;
    }
    while (polarValues[1] < 0) {
      polarValues[1] += 360.0;
    }
    return polarValues;
  }

  /**
   * 極座標=>笛卡兒座標
   * @param polarValues double[]
   * @return double[]
   */
  public static final double[] polar2cartesianCoordinatesValues(final double[]
      polarValues) {
    double t = (polarValues[1] * Math.PI) / 180.0;

    double[] cartesianValues = new double[2];
    cartesianValues[0] = polarValues[0] * Math.cos(t);
    cartesianValues[1] = polarValues[0] * Math.sin(t);

    return cartesianValues;
  }

  public static void main(String[] args) {
    double[] s1 = new double[] {
        1, 2};
    double[] e1 = new double[] {
        10, 19};
    double[] s2 = new double[] {
        2, 1};
    double[] e2 = new double[] {
        11, 22};
    Operator op = getAdjustOperator(s1, e1, s2, e2);
    System.out.println(Arrays.toString(op.getXY(s2)));
    System.out.println(Arrays.toString(op.getXY(e2)));
    System.out.println("");
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj the reference object with which to compare.
   * @return <code>true</code> if this object is the same as the obj argument;
   *   <code>false</code> otherwise.
   * @todo Implement this java.lang.Object method
   */
  public boolean equals(Object obj) {
    List<Operator> thisList = this.operatorList;
    List<Operator> thatList = ( (Operator) obj).operatorList;
    if (thisList.size() != thatList.size()) {
      return false;
    }
    int size = thisList.size();
    for (int x = 0; x < size; x++) {
      Operator o0 = thisList.get(x);
      Operator o1 = thatList.get(x);
      if (false == o0.equals(o1)) {
        return false;
      }
    }
    return true;
  }
}
