package shu.cms.hvs.gradient;

import java.util.*;

import shu.math.*;

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 用來記錄不順pattern的相關訊息
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class Pattern {
  /**
   * pattern所在的index(也就是code)
   */
  public int index;
  /**
   * 計算出來的pattern大小
   */
  public double pattern;
  /**
   * pattern相鄰的兩個加速度
   */
  public double[] acceleration;
  /**
   * 超出threshold的比例
   */
  public float overRatio;
  /**
   * pattern所在code的JNDI
   */
  public double jndIndex;
  /**
   * 該pattern對應的狀況下的threshold
   */
  public double threshold;

  /**
   *
   * @param index int pattern所在的index(也就是code)
   * @param jndIndex double pattern所在code的JNDI
   * @param pattern double 計算出來的pattern大小
   * @param acceleration double[] pattern相鄰的兩個加速度
   * @param overRatio float 超出threshold的比例
   * @param threshold double 該pattern對應的狀況下的threshold
   */
  public Pattern(int index, double jndIndex, double pattern,
                 double[] acceleration, float overRatio, double threshold) {
    this.index = index;
    this.jndIndex = jndIndex;
    this.pattern = pattern;
    this.acceleration = acceleration;
    this.overRatio = overRatio;
    this.threshold = threshold;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    return "[" + index + "] JNDI:" + jndIndex + " pattern:" + pattern + " acc:" +
        Arrays.toString(acceleration) +
        "(mean:" + Maths.mean(acceleration) + ") over:" + +overRatio + "%";
  }

  public static void main(String[] args) {
    Pattern pat = new Pattern(3, 11, 3, new double[] {1, 3}, 1.1f, 2);
    Pattern pat2 = new Pattern(3, 11, 3, new double[] {1, 3}, 1.1f, 2);
    System.out.println(pat.equals(pat2));
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj the reference object with which to compare.
   * @return <code>true</code> if this object is the same as the obj argument;
   *   <code>false</code> otherwise.
   */
  public boolean equals(Object obj) {
    Pattern that = (Pattern) obj;
    boolean result = Arrays.equals(this.acceleration, that.acceleration) &&
        this.index == that.index && this.jndIndex == that.jndIndex &&
        this.overRatio == that.overRatio && this.pattern == that.pattern &&
        this.threshold == that.threshold;
    return result;
  }
}
