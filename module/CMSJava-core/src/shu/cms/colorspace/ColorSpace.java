package shu.cms.colorspace;

import java.io.*;
import java.text.*;
import java.util.*;

import shu.cms.grabber.*;
import shu.math.array.*;
import shu.util.*;
import shu.util.log.*;
import shu.math.Maths;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.depend.DeviceDependentSpace;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 所有色空間皆繼承此類別
 * (目前沒什功能...)
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class ColorSpace
    implements Cloneable, Serializable, NameIF, ValueProperty, Comparable {

  public String getName() {
    return getClass().getSimpleName();
  }

  public abstract String[] getBandNames();

  protected abstract int getNumberBands();

  public double[] getValues() {
    double[] values = new double[getNumberBands()];
    return getValues(values);
  }

  public double[] getValues(double[] values) {
    if (values.length != getNumberBands()) {
      throw new IllegalArgumentException("values.length != getNumberBands()");
    }
    return _getValues(values);
  }

  protected abstract double[] _getValues(double[] values);

  protected abstract void _setValues(double[] values);

  public final void setValues(double ...values) {
    if (setValuesLocked == true) {
      throw new IllegalStateException("setValuesLocked == true");
    }
    if (values.length != getNumberBands()) {
      throw new IllegalArgumentException("values.length != getNumberBands()");
    }
    _setValues(values);
  }

  public final void setValues(double value1, double value2, double value3) {
    if (this.getNumberBands() != 3) {
      throw new UnsupportedOperationException();
    }
    this.setValues(new double[] {value1, value2, value3});
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    return "(" + DoubleArray.toString(getValues()) + ")";
  }

  public String toString(DecimalFormat df) {
    return "(" + DoubleArray.toString(df, getValues()) + ")";
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj the reference object with which to compare.
   * @return <code>true</code> if this object is the same as the obj argument;
   *   <code>false</code> otherwise.
   */
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj != null && getClass() == obj.getClass()) {
      ColorSpace cs = (ColorSpace) obj;
      boolean values = Arrays.equals(this.getValues(), cs.getValues());
      boolean lock = this.setValuesLocked == cs.setValuesLocked;
      if (values && lock) {
        return true;
      }
    }
    return false;
  }

  public boolean equalsValues(ColorSpace cs) {
    if (this == cs) {
      return true;
    }
    if (cs != null) {
      boolean values = Arrays.equals(this.getValues(), cs.getValues());
      return values;
    }
    return false;
  }

  /**
   * Creates and returns a copy of this object.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException if the object's class does not support
   *   the <code>Cloneable</code> interface. Subclasses that override the
   *   <code>clone</code> method can also throw this exception to indicate
   *   that an instance cannot be cloned.
   */
  public Object clone() {
    try {
      ColorSpace clone = (ColorSpace)super.clone();
      clone.setValuesLock(false);
      clone.setValues(this.getValues());
      return clone;
    }
    catch (CloneNotSupportedException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object.
   */
  public int hashCode() {
//    return super.hashCode();
    double[] values = this.getValues();
    int size = values.length;
    int hashCode = 0;
    for (int x = 0; x < size; x++) {
      hashCode += Double.valueOf(values[x]).hashCode();
    }
    hashCode += Boolean.valueOf(setValuesLocked).hashCode();
    return hashCode;
  }

  private boolean setValuesLocked = false;

  public void setValuesLock(boolean lock) {
    setValuesLocked = lock;
  }

  /**
   * 笛卡兒座標=>極座標
   * @param cartesianValues double[]
   * @return double[]
   */
  public static final double[] cartesian2polarCoordinatesValues(final double[]
      cartesianValues) {
    if (cartesianValues.length != 3) {
      throw new IllegalArgumentException("cartesianValues.length != 3");
    }
    double[] polarValues = new double[3];

    polarValues[0] = cartesianValues[0];
    double t1 = cartesianValues[1];
    double t2 = cartesianValues[2];
    polarValues[1] = Math.sqrt(Maths.sqr(cartesianValues[1])
                               + Maths.sqr(cartesianValues[2]));
    if (t1 == 0 && t2 == 0) {
      polarValues[2] = 0;
    }
    else {
      polarValues[2] = Math.atan2(t2, t1);
    }
    polarValues[2] *= (180.0 / Math.PI);
    while (polarValues[2] >= 360.0) { // Not necessary, but included as a check.
      polarValues[2] -= 360.0;
    }
    while (polarValues[2] < 0) {
      polarValues[2] += 360.0;
    }
    return polarValues;
  }

  public static final double[] cartesian2polarCoordinatesValues(final double x,
      final double y) {
    double[] polarValues = new double[2];

    double t1 = x;
    double t2 = y;
    polarValues[0] = Math.sqrt(Math.pow(t1, 2)
                               + Math.pow(t2, 2));
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
    if (polarValues.length != 3) {
      throw new IllegalArgumentException("polarValues.length != 3");
    }
    double t = (polarValues[2] * Math.PI) / 180.0;

    double[] cartesianValues = new double[3];
    cartesianValues[0] = polarValues[0];
    cartesianValues[1] = polarValues[1] * Math.cos(t);
    cartesianValues[2] = polarValues[1] * Math.sin(t);

    return cartesianValues;
  }

  public static final double[] polar2cartesianCoordinatesValues(final double
      distance, final double angle) {

    double t = (angle * Math.PI) / 180.0;

    double[] cartesianValues = new double[2];
    cartesianValues[0] = distance * Math.cos(t);
    cartesianValues[1] = distance * Math.sin(t);

    return cartesianValues;
  }

  private static double[] PolarValues = new double[3];
  private static double t1, t2;
  private final static double PI180 = (180.0 / Math.PI);

  public static final double fastCartesian2RadialValues(final double[]
      cartesianValues) {

    return Math.sqrt(Maths.sqr(cartesianValues[1]) +
                     Maths.sqr(cartesianValues[2]));
  }

  public static final double fastCartesian2AngularValues(final double[]
      cartesianValues) {
    t1 = cartesianValues[1];
    t2 = cartesianValues[2];
    double angular = 0;
    if (t1 == 0 && t2 == 0) {
      angular = 0;
    }
    else {
      angular = Math.atan2(t2, t1);
    }

    angular *= PI180;
    while (PolarValues[2] >= 360.0) { // Not necessary, but included as a check.
      angular -= 360.0;
    }
    while (PolarValues[2] < 0) {
      angular += 360.0;
    }
    return angular;
  }

  /**
   * Compares this object with the specified object for order.
   *
   * @param o the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is
   *   less than, equal to, or greater than the specified object.
   */
  public int compareTo(Object o) {
    double[] thisValues = this.getValues();
    double[] thatValues = ( (ColorSpace) o).getValues();
    int result = DoubleArray.compare(thisValues, thatValues);
    return result;
  }
}
