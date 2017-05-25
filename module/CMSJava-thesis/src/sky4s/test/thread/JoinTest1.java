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
class T1
    extends Thread {
  public T1() {
    super("T1 Thread");
  };
  public void run() {
    System.out.println(Thread.currentThread().getName() + " start!");
    try {
      for (int i = 0; i < 10; i++) {
        System.out.println(Thread.currentThread().getName() + " loop at " + i);
        Thread.sleep(1000);
      }
      System.out.println(Thread.currentThread().getName() + " end!");
    }
    catch (Exception e) {
      System.out.println("Exception from T1.run");
    }
  }
}

class T
    extends Thread {
  T1 t1;
  public T(T1 t1) {
    super("T Thread");
    this.t1 = t1;
  }

  public void run() {
    System.out.println(Thread.currentThread().getName() + " start!");
    try {
      t1.join();
      System.out.println(Thread.currentThread().getName() + " end!");
    }
    catch (Exception e) {
      System.out.println("Exception from T.run");
    }
  }
}

public class JoinTest1 {
  public static void main(String[] args) {
    System.out.println(Thread.currentThread().getName() + " start!");
    T1 t1 = new T1();
    T t = new T(t1);
    try {
      t1.start();
      Thread.sleep(2000);
      t.start();
      t.join();
    }
    catch (Exception e) {
      System.out.println("Exception from main");
    }
    System.out.println(Thread.currentThread().getName() + " end!");
  }
}
