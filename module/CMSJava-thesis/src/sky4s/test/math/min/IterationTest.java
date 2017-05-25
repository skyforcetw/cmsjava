package sky4s.test.math.min;

import java.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class IterationTest {

  public double function(double[] vars) {
    return vars[0] + vars[1];
  }

  public static void main(String[] args) {
    IterationTest i = new IterationTest();
    double[] s = new double[] {
        4, 4};
    double[] step = new double[] {
        .1, .2};
    double[] e = new double[] {
        5.1, 5};
    i.iteration(s, step, e);
  }

  public void iteration(double[] start, double[] step, double[] end) {
    int iterTimes = 2000;
    int size = start.length;
    double[] vars = new double[size];
    System.arraycopy(start, 0, vars, 0, size);
    int index = 0;
    int x = 0;
    for (x = 0; x < iterTimes; x++) {
      int varsIndex = 0;
      boolean looping = false;

      for (;
           vars[varsIndex] <= end[varsIndex]; ) {
        System.out.println(Arrays.toString(vars));
        double r = function(vars);

//        System.out.println(varsIndex);
//        System.out.println(vars[varsIndex] + step[varsIndex]+" "+ end[varsIndex]);

        if (varsIndex != size - 1) {
          if (vars[varsIndex] + step[varsIndex] <= end[varsIndex]) {
            vars[varsIndex] += step[varsIndex];
            looping = true;
          }
          varsIndex++;
        }
        else {
          vars[varsIndex] += step[varsIndex];
        }

//        System.out.println("-");
      }

      vars[size - 1] = start[size - 1];

      if (!looping) {
        break;
      }
//      System.out.println(x);
    }
    System.out.println(x);
  }
}
