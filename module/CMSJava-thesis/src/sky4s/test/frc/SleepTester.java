package sky4s.test.frc;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SleepTester {

  public static void main(String[] args) {

    long ms = 16;
    int ns = 666666;
    long time = (ms * 1000000 + ns);
    try {
      while (true) {
        long start = System.nanoTime();
        Thread.sleep(ms, ns);
        long diff = (System.nanoTime() - start) - time;
        System.out.println(diff);
      }
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }
}
