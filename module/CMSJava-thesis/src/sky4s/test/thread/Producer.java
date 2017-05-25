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
public class Producer
    implements Runnable {
  private Clerk clerk;

  public Producer(Clerk clerk) {
    this.clerk = clerk;
  }

  public void run() {
    System.out.println(
        "生產者開始生產整數......");

    // 生產1到10的整數
    for (int product = 1; product <= 10; product++) {
      try {
        // 暫停隨機時間
        Thread.sleep( (int) (Math.random() * 3000));
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      // 將產品交給店員
      clerk.setProduct(product);
    }
  }
}
