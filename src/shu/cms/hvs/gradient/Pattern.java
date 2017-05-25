package shu.cms.hvs.gradient;

import java.util.*;

import shu.math.*;

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * �ΨӰO������pattern�������T��
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
   * pattern�Ҧb��index(�]�N�Ocode)
   */
  public int index;
  /**
   * �p��X�Ӫ�pattern�j�p
   */
  public double pattern;
  /**
   * pattern�۾F����ӥ[�t��
   */
  public double[] acceleration;
  /**
   * �W�Xthreshold�����
   */
  public float overRatio;
  /**
   * pattern�Ҧbcode��JNDI
   */
  public double jndIndex;
  /**
   * ��pattern���������p�U��threshold
   */
  public double threshold;

  /**
   *
   * @param index int pattern�Ҧb��index(�]�N�Ocode)
   * @param jndIndex double pattern�Ҧbcode��JNDI
   * @param pattern double �p��X�Ӫ�pattern�j�p
   * @param acceleration double[] pattern�۾F����ӥ[�t��
   * @param overRatio float �W�Xthreshold�����
   * @param threshold double ��pattern���������p�U��threshold
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
