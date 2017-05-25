package sky4s.test.timer;

import java.util.*;

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
public class TimerTester {
  public static void main(String[] args) {
    Timer timer = new Timer();
    timer.schedule(new DateTask(), 5000);
    System.out.println("現在時間：" + new Date());

    try {
      Thread.sleep(8000);
    }
    catch (InterruptedException e) {
    }

    timer.cancel();
  }
}

class DateTask
    extends TimerTask {
  public void run() {
    System.out.println("任務時間：" + new Date());
  }
}
