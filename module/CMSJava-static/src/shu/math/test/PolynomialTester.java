package shu.math.test;

import java.util.*;

import shu.math.array.*;

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
public class PolynomialTester {
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

  private final static boolean isSkip(int[] p0, int[] p1) {
    int p0Index = getFirstNonZeroIndex(p0);
    int p1Index = getFirstNonZeroIndex(p1);
    return p1Index < p0Index;
  }

  private final static boolean isSingleVariable(int[] p) {
    return getNonZeroCount(p) == 1;
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

  private final static int getFirstNonZeroIndex(int[] p) {
    int size = p.length;
    for (int x = 0; x < size; x++) {
      if (p[x] != 0) {
        return x;
      }
    }
    return -1;
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

  public static void main(String[] args) {
//    int[][] powers = new int[][] {
//        {
//        1, 0, 0}, {
//        0, 1, 0}, {
//        0, 0, 1}
//    };
    int[][] powers = new int[][] {
        {
        1}
    };
    int order = 5;
    List<int[]> polyPowersList = new LinkedList<int[]> ();
    polyPowersList.add(new int[] {0});
    Set<int[]> result = new TreeSet<int[]> (powerComparator);

    for (int x = 0; x < order; x++) {
      for (int[] p0 : polyPowersList) {
        for (int[] p1 : powers) {
//          if (true == isSkip(p0, p1)) {
//            continue;
//          }

          int[] p = IntArray.plus(p0, p1);
          result.add(p);
        }
      }
      polyPowersList.clear();
      polyPowersList.addAll(result);
    }

//    for (int[] p : polyPowersList) {
//      if (isSingleVariable(p)) {
//        singleVariableSet.add(p);
//      }
//    }
//    polyPowersList.removeAll(singleVariableSet);
//    polyPowersList.addAll(singleVariableSet);

    int[][] powerArray = polyPowersList.toArray(new int[polyPowersList.size()][]);

//    String s = toString(powerArray);
//    System.out.println(s);
    System.out.println(polyPowersList.size());
//    Arrays.sort(powerArray, 0, powerArray.length, powerComparator);
//    Collections.sort(powerArray, powerComparator);
    System.out.println(toString(powerArray));
  }

  private final static PowerComparator powerComparator = new PowerComparator(new int[] {
      1});
  private final static class PowerComparator
      implements Comparator {
    private PowerComparator() {

    }

    private PowerComparator(int[] weighting) {
      this.weighting = weighting;
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
//      int result = weighting(p1) - weighting(p2);
      int result = (equals0(p1, p2)) ? 0 : weighting(p1) - weighting(p2);
      return result;
    }

    private static int[] weighting = {
        1000000, 1000, 1};
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
}
