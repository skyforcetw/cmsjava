package shu.util;

import java.util.concurrent.*;

import shu.math.*;
import shu.math.array.*;

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
public class ThreadExecutor {
  public ThreadExecutor(ThreadTask threadTask, int threadCount) {
    this.threadTask = threadTask;
    this.threadCount = threadCount;
  }

  protected int threadCount = 4;
  protected ThreadTask threadTask;
//  protected final static int Thread_COUNT = 4;

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   *
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: </p>
   *
   * @author not attributable
   * @version 1.0
   */
  public interface ThreadTask {
    /**
     * 回傳起始值給ThreadExecutor運算
     * @return double[]
     */
    public double[] getStartValues();

    /**
     * 回傳終止值給ThreadExecutor運算
     * @return double[]
     */
    public double[] getEndValues();

    /**
     * 回傳步進值給ThreadExecutor運算
     * @return double[]
     */
    public double[] getStepValues();

    /**
     * 將產生好的值餵給ThreadTask進行運算.
     * 要是終止，則回傳出false
     * @param variables double[]
     * @return boolean 是否繼續下一次運算
     */
    public boolean setVariables(double[] variables);
  }

  public final void start() {
    start(threadTask);
  }

  protected final void start(ThreadTask threadTask) {
    //==========================================================================
    //多執行緒運算
    //==========================================================================
    //設定執行緒
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    double[] start = threadTask.getStartValues();
    double[] end = threadTask.getEndValues();
    double[] step = threadTask.getStepValues();
    int count = (int) Math.floor( (end[0] - start[0]) / step[0]) + 1;
    Future<?> [] futureArray = new Future<?>[count];
    int index = 0;

    for (double x = start[0]; x <= end[0]; x += step[0]) {
      double[] taskStart = DoubleArray.copy(start);
      double[] taskEnd = DoubleArray.copy(end);
      taskStart[0] = x;
      taskEnd[0] = x;
      Task task = new Task(taskStart, taskEnd, step, threadTask);
      futureArray[index++] = executorService.submit(task);
    }

    try {
      for (int x = 0; x < count; x++) {
        futureArray[x].get();
      }
    }
    catch (ExecutionException ex) {
      ex.printStackTrace();
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    finally {
      //關閉執行緒池
      executorService.shutdown();
    }
  }

  public final static double[] produceSteps(double[] start, double[] end,
                                            double stepRate) {
    int size = start.length;
    double[] steps = new double[size];
    for (int x = 0; x < size; x++) {
      steps[x] = (end[x] - start[x]) * stepRate;
    }
    return steps;
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 變數迭代器
   *
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: </p>
   *
   * @author not attributable
   * @version 1.0
   */
  private static class VariablesIterator {

    public VariablesIterator(ThreadTask threadTask, double[] start,
                             double[] end, double[] step) {
      this.threadTask = threadTask;
      this.start = start;
      this.end = end;
      size = start.length;
      values = new double[size];
      values[0] = start[0];
      steps = step;
    }

    protected ThreadTask threadTask;
    protected double[] start;
    protected double[] end;
    protected double[] values;
    protected double[] steps;
    protected int size;

    /**
     * 變數的迭代
     * @param index int 迭代第index個變數
     */
    protected void iterative(int index) {
      for (double x = start[index]; x <= end[index]; x += steps[index]) {
        values[index] = x;
        if (index == size - 1) {
          /**
           * @todo M 如果ThreadExecutor需要回傳值,從這邊改起
           */
          if (!threadTask.setVariables(values)) {
            //如果setVariables回傳false,就停止
            break;
          }
        }
        else {
          iterative(index + 1);
        }
        if (steps[index] == 0) {
          break;
        }
      }
    }

  }

  private final static class Task
      implements Callable<Object> {
    double[] start;
    double[] end;
    double[] step;
    ThreadExecutor.ThreadTask threadTask;

    Task(double[] start, double[] end, double[] step,
         ThreadExecutor.ThreadTask threadTask) {
      this.start = start;
      this.end = end;
      this.step = step;
      this.threadTask = threadTask;
    }

    public Object call() {
      ThreadExecutor.VariablesIterator iter = new ThreadExecutor.
          VariablesIterator(threadTask, start, end,
                            step);
      iter.iterative(1);
      return null;
    }
  }

}
