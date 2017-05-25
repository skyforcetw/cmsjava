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
public class Consumer
    implements Runnable {
  private Clerk clerk;

  public Consumer(Clerk clerk) {
    this.clerk = clerk;
  }

  public void run() {
    System.out.println(
        "消費者開始消耗整數......");

    // 消耗10個整數
    for (int i = 1; i <= 10; i++) {
      try {
        // 等待隨機時間
        Thread.sleep( (int) (Math.random() * 3000));
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }

      // 從店員處取走整數
      clerk.getProduct();
    }
  }
}
