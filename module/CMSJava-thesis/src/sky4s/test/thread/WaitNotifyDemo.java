package sky4s.test.thread;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class WaitNotifyDemo {
  public static void main(String[] args) {
    Clerk clerk = new Clerk();

    Thread producerThread = new Thread(
        new Producer(clerk));
    Thread consumerThread = new Thread(
        new Consumer(clerk));

    producerThread.start();
    consumerThread.start();
  }
}
