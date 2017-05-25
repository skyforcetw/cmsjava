package sky4s.test.thread.executor;

import java.util.concurrent.*;

public class ExecutorDemo {
  public static void main(String[] args) {
    ExecutorService service = Executors.newFixedThreadPool(5);

    for (int i = 0; i < 10; i++) {
      final int count = i;
      Thread t = new Thread() {
        public void run() {
          System.out.println(count);
        }
      };
      service.submit(t);
//      service.submit(new Runnable() {
//        public void run() {
//          System.out.println(count);
//          try {
//            Thread.sleep(2000);
//          }
//          catch (InterruptedException e) {
//            e.printStackTrace();
//          }

//        }
//      });
    }

    service.shutdown(); // 最後記得關閉Thread pool
  }
}
