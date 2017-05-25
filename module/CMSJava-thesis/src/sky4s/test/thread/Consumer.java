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
        "���O�̶}�l���Ӿ��......");

    // ����10�Ӿ��
    for (int i = 1; i <= 10; i++) {
      try {
        // �����H���ɶ�
        Thread.sleep( (int) (Math.random() * 3000));
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }

      // �q�����B�������
      clerk.getProduct();
    }
  }
}
