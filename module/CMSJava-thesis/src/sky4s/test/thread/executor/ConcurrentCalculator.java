package sky4s.test.thread.executor;

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
import java.util.*;
import java.util.concurrent.*;

public class ConcurrentCalculator {

  private ExecutorService exec;
//  private int cpuCoreNumber;
  private List<Future<Long>> tasks = new ArrayList<Future<Long>> ();

  // ������
  class SumCalculator
      implements Callable<Long> {
//    private int[] numbers;
//    private int start;
//    private int end;
    private long n;
    public SumCalculator(long n) {
      this.n = n;
    }

    public Long call() throws Exception {
      return n;
    }
  }

  public ConcurrentCalculator() {
//    cpuCoreNumber = Runtime.getRuntime().availableProcessors();
    exec = Executors.newFixedThreadPool(2);
  }

  public Long sum(final int[] numbers) {
    // �ھ�CPU�֤߭ӼƩ�����ȡA�Ы�FutureTask�ô����Executor
    for (int i = 0; i < 10; i++) {

      SumCalculator subCalc = new SumCalculator(i + 1);
      FutureTask<Long> task = new FutureTask<Long> (subCalc);
      tasks.add(task);
      if (!exec.isShutdown()) {
        exec.submit(task);
      }
    }

    for (Future<Long> task : tasks) {
      try {
        // �p�G�p�⥼�����h����
        Long subSum = task.get();
        System.out.println(subSum);
//    result += subSum;
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      catch (ExecutionException e) {
        e.printStackTrace();
      }
    }

    return -1L;
//    return getResult();
  }

  /**
   * ���N�C�ӥu���ȡA��o�����M�A�ۥ[��^
   *
   * @return
   */
  public Long getResult() {
    Long result = 0l;
    for (Future<Long> task : tasks) {
      try {
        // �p�G�p�⥼�����h����
        Long subSum = task.get();
        System.out.println(subSum);
        result += subSum;
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      catch (ExecutionException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  public void close() {
    exec.shutdown();
  }

  public static void main(String[] args) {
    int[] numbers = new int[] {
        1, 2, 3, 4, 5, 6, 7, 8, 10, 11};
    ConcurrentCalculator calc = new ConcurrentCalculator();
    Long sum = calc.sum(numbers);
    System.out.println(sum);
    calc.close();

  }
}
