package shu.math.test;

import java.util.*;

import shu.math.array.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System - static</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PolynomialFunction {
  public PolynomialFunction(int order, int variableCount) {
    this.order = order;
    this.variableCount = variableCount;
    basicPowers = getBasicPower(variableCount);
    switch (variableCount) {
      case 1:
        powerComparator = PowerComparator.getUniVariablesInstance();
        break;
      case 3:
        powerComparator = PowerComparator.getTriVariablesInstance();
        break;
      default:
        throw new IllegalArgumentException("Unsupported variables: " +
                                           variableCount);
    }
    init();
  }

  private PowerComparator powerComparator;
  private void init() {
    List<int[]> termList = new LinkedList<int[]> ();
//    Set<int[]> termList = new TreeSet<int[]> (powerComparator);
    termList.add(IntArray.buildX(0, 0, variableCount));
    Set<int[]> result = new TreeSet<int[]> (powerComparator);

    for (int x = 0; x < order; x++) {
      for (int[] p0 : termList) {
        for (int[] p1 : basicPowers) {
          int[] p = IntArray.plus(p0, p1);
          result.add(p);
        }
      }
      termList.clear();
      termList.addAll(result);
    }
//    termPowerArray = result.toArray(new int[termList.size()][]);
    powerComparator.setDuplicateCompare(true);
    Collections.sort(termList, powerComparator);
    termPowerArray = termList.toArray(new int[termList.size()][]);
  }

  public static void main(String[] args) {
    PolynomialFunction f = new PolynomialFunction(3, 3);
    System.out.println(f.toString());
    System.out.println(f.termCount());
    double[] terms = f.terms(new double[] {1, 2, 3});
    System.out.println(Arrays.toString(terms));
  }

  public String toString() {
    return toString(termPowerArray);
  }

  private final static String toString(int[][] powers) {
    StringBuilder buf = new StringBuilder();
    int size = powers.length;
    for (int x = 0; x < size; x++) {
      int[] term = powers[x];
      int termSize = term.length;
      if (x != 0) {
        buf.append('+');
      }
      for (int y = 0; y < termSize; y++) {
        if (term[y] != 0) {
          buf.append( ( (char) ('x' + y)));
          if (term[y] > 1) {
            buf.append("^" + term[y]);
          }
        }
      }
    }
    return buf.toString();
  }

  public int order() {
    return order;
  }

  public int termCount() {
    return termPowerArray.length;
  }

  public double[] terms(double[] variables) {
    int size = termCount();
    double[] terms = new double[size];
    for (int x = 0; x < size; x++) {
      int[] power = termPowerArray[x];
      terms[x] = getTerm(variables, power);
    }
    return terms;
  }

  private double getTerm(double[] variables, int[] power) {
    if (variables.length != power.length) {
      throw new IllegalArgumentException("variables.length != power.length");
    }
    int size = variables.length;
    double term = 1;
    for (int x = 0; x < size; x++) {
      term *= Math.pow(variables[x], power[x]);
    }
    return term;
  }

  private double[] variables;
  private int[][] termPowerArray;
  private int order;
  private int variableCount;
  private int[][] basicPowers;
  private int[][] getBasicPower(int variables) {
    int[][] diagonal = IntArray.diagonal(IntArray.buildX(1, 1,
        variables));
    return diagonal;
  }

  private final static class PowerComparator
      implements Comparator {
    private PowerComparator() {

    }

    public final static PowerComparator getUniVariablesInstance() {
      return new PowerComparator(UniWeighting);
    }

    public final static PowerComparator getTriVariablesInstance() {
      return new PowerComparator();
    }

    private PowerComparator(int[] weighting) {
      this.weighting = weighting;
    }

    private boolean duplicateCompare = false;
    private void setDuplicateCompare(boolean duplicateCompare) {
      this.duplicateCompare = duplicateCompare;
    }

    /**
     * Compares its two arguments for order.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *   argument is less than, equal to, or greater than the second.
     */
    public int compare(Object o1, Object o2) {
      int[] p1 = (int[]) o1;
      int[] p2 = (int[]) o2;
      int result = 0;
      if (duplicateCompare) {
        result = order(p1) * 1000 + duplicateWeighting(p1) -
            (order(p2) * 1000 + duplicateWeighting(p2));
      }
      else {
        result = (equals0(p1, p2)) ? 0 : weighting(p1) - weighting(p2);
      }
      return result;
    }

    private int order(int[] p) {
      return Maths.sum(p);
    }

    private final static int duplicateWeighting(int[] p0) {
      int weighting = 0;
      for (int p : p0) {
        weighting += Math.pow(p, p);
      }
      return weighting;
    }

    private final static boolean equals0(int[] p0, int[] p1) {
      if (p0.length != p1.length) {
        return false;
      }
      int size = p0.length;
      boolean nonEqual = false;
      for (int x = 0; x < size; x++) {
        nonEqual |= ! (p0[x] == p1[x]);
      }
      return!nonEqual;
    }

    private final static int[] TriWeighting = {
        1, 1000, 1000000};
//        1000000, 1000, 1};
    private final static int[] UniWeighting = {
        1};
    private int[] weighting = TriWeighting;
    private int weighting(int[] p) {
      return IntArray.times(p, weighting);
    }

    /**
     * Indicates whether some other object is &quot;equal to&quot; this
     * comparator.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> only if the specified object is also a
     *   comparator and it imposes the same ordering as this comparator.
     */
    public boolean equals(Object obj) {
      return false;
    }

  }

  public void setVariables(double[] variables) {
    this.variables = variables;
  }

  private final static int getNonZeroCount(int[] p) {
    int size = p.length;
    int count = 0;
    for (int x = 0; x < size; x++) {
      if (p[x] != 0) {
        count++;
      }
    }
    return count;
  }

  private final static boolean isSingleVariable(int[] p) {
    return getNonZeroCount(p) == 1;
  }

}
