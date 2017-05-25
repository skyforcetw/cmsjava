package sky4s.test;

/**
 * <p>Title: Colour Management System - thesis</p>
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
public class PokerTester {

  public static void main(String[] args) {
    int dim = 8;
    int[] pop = new int[dim];
    for (int y = 0; y < dim; y++) {
      pop[y] = y;
    }
//    for (int t = 0; t < 1000; t++) {
    for (int x = dim - 1; x > 0; x--) {
      double d = Math.random() * x;
      int tem = (int) d;
//      System.out.println(d + " " + tem);
      int test = pop[tem];
      pop[tem] = pop[x];
      pop[x] = test;
      for (int y = 0; y < dim; y++) {
        System.out.print(pop[y]);
      }
      System.out.println("");
    }
//    for (int y = 0; y < dim; y++) {
//      System.out.print(pop[y]);
//    }
    System.out.println("");

//    }
    int[] a = new int[3];
    a[0] = 1;
    int[] b = (int[]) a.clone();
    b[0] = 2;
    System.out.println(a[0]);
    System.out.println(b[0]);
  }
}
