package sky4s.test;

import shu.math.array.*;
import shu.util.*;

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
public class ThreadExecutorTest {

  public static void main(String[] args) {
    for (int x = 0; x < 1; x++) {
      ThreadExecutor executor = new ThreadExecutor(new TestTask(), 4);
//      ThreadExecutor.start(new TestTask());
      executor.start();
    }

  }
}

class TestTask
    implements ThreadExecutor.ThreadTask {
  public double[] getStartValues() {
    return new double[] {
        0, 0};
  }

  public double[] getEndValues() {
    return new double[] {
        10, 10};
  }

  public double[] getStepValues() {
    return new double[] {
        5, 2.5};
  }

  public boolean setVariables(double[] variables) {
    System.out.println(DoubleArray.toString(variables));
    if (variables[1] > 5) {
      return false;
    }
    return true;
  }

}
