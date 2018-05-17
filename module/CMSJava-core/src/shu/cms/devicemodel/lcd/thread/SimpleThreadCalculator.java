package shu.cms.devicemodel.lcd.thread;

import java.util.*;
import java.util.concurrent.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.math.array.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 對頻道獨立的LCD模式,提供多執行緒運算的介面以及函式
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class SimpleThreadCalculator {

  private static double torlerence = 0.001;

  /**
   *
   * @param coefficientsRange CoefficientsRange[]
   * @param c Cooperation
   * @return Factor[]
   * @deprecated 改用produceIterativeReport
   */
  public final static LCDModelBase.Factor[] produceFactor(CoefficientsRange[]
      coefficientsRange,
      Cooperation c) {
    //==========================================================================
    //多執行緒運算
    //==========================================================================
    //設定執行緒
    ExecutorService executorService = Executors.newFixedThreadPool(3);

    IterativeFactorThreadOld taskR = new
        IterativeFactorThreadOld(coefficientsRange[0], c);
    IterativeFactorThreadOld taskG = new
        IterativeFactorThreadOld(coefficientsRange[1], c);
    IterativeFactorThreadOld taskB = new
        IterativeFactorThreadOld(coefficientsRange[2], c);

    Future<LCDModelBase.Factor> futureR = executorService.submit(taskR);
    Future<LCDModelBase.Factor> futureG = executorService.submit(taskG);
    Future<LCDModelBase.Factor> futureB = executorService.submit(taskB);

    LCDModelBase.Factor factorR = null;
    LCDModelBase.Factor factorG = null;
    LCDModelBase.Factor factorB = null;
    try {
      factorR = futureR.get();
      factorG = futureG.get();
      factorB = futureB.get();
    }
    catch (ExecutionException ex) {
      Logger.log.error("", ex);
    }
    catch (InterruptedException ex) {
      Logger.log.error("", ex);
    }
    executorService.shutdown();
    //==========================================================================
    LCDModelBase.Factor[] factors = new LCDModelBase.Factor[] {
        factorR, factorG, factorB};

    return factors;
  }

  /**
   * 加強版
   * @param coefficientsRange CoefficientsRange[]
   * @param model ChannelIndependentModel
   * @return IterativeReport[]
   */
  public final static IterativeReport[] produceBestIterativeReport(
      CoefficientsRange[]
      coefficientsRange, ChannelIndependentModel model) {
    //==========================================================================
    //多執行緒運算
    //==========================================================================
    //設定執行緒
    ExecutorService executorService = Executors.newFixedThreadPool(3);

    IterativeFactorThread taskR = new
        IterativeFactorThread(coefficientsRange[0], model);
    IterativeFactorThread taskG = new
        IterativeFactorThread(coefficientsRange[1], model);
    IterativeFactorThread taskB = new
        IterativeFactorThread(coefficientsRange[2], model);

    Future<IterativeReport> futureR = executorService.submit(taskR);
    Future<IterativeReport> futureG = executorService.submit(taskG);
    Future<IterativeReport> futureB = executorService.submit(taskB);

    IterativeReport reportR = null;
    IterativeReport reportG = null;
    IterativeReport reportB = null;
    try {
      reportR = futureR.get();
      reportG = futureG.get();
      reportB = futureB.get();
    }
    catch (ExecutionException ex) {
      Logger.log.error("", ex);
    }
    catch (InterruptedException ex) {
      Logger.log.error("", ex);
    }
    executorService.shutdown();

    IterativeReport[] reports = new IterativeReport[] {
        reportR, reportG, reportB
    };

    return reports;
  }

  public interface Cooperation {
    public double getStepRate();

    public double getRangeRate();

    public int getMaxIterativeTimes();

    public void initCoefficientsRange();

    /**
     *
     * @param coefRange CoefficientsRange
     * @param patchList List
     * @param whitePatch Patch
     * @param bestReport IterativeReport
     * @return IterativeReport
     * @deprecated
     */
    public IterativeReport iterateAndReport(
        CoefficientsRange coefRange,
        List<Patch> patchList,
        Patch whitePatch, IterativeReport bestReport);

    public CoefficientsRange getNewCoefficientsRange(LCDModelBase.Factor
        factor,
        CoefficientsRange old);

    public LCDTarget getLCDTarget();

    public LCDModelBase.Factor[] getFactors(double[] variables,
                                            RGBBase.Channel channel);
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 迭代時所使用的係數範圍
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public static abstract class CoefficientsRange {
    public RGBBase.Channel channel;

    public abstract double[] getStartVariables();

    public abstract double[] getEndVariables();

    public abstract void setStartVariables(double[] start);

    public abstract void setEndVariables(double[] end);

  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 改良版
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  private static class IterativeFactorThread
      implements Callable<IterativeReport>, VariablesReceptor {
    protected CoefficientsRange coef;
    protected ChannelIndependentModel model;
    protected List<Patch> patchList;
    protected Patch whitePatch;
    protected IterativeReport bestReport;

    protected IterativeFactorThread(CoefficientsRange coef,
                                    ChannelIndependentModel m) {
      this.coef = coef;
      this.model = m;
      init();
    }

    protected void init() {
      LCDTarget lcdTargetPatch = model.getLCDTarget();
      patchList = lcdTargetPatch.filter.oneValueChannel(coef.channel);

      whitePatch = lcdTargetPatch.getWhitePatch();
      CIEXYZ whitePoint = whitePatch.getXYZ();
      patchList = Patch.Produce.LabPatches(patchList, whitePoint);
    }

    public IterativeReport call() {

      IterativeReport iReport = null;
      int times = model.getMaxIterativeTimes();
      double previousMeasuredDeltaE = 0;
      //沒有顯著意義的進步
      boolean uselessAdvance = false;

      for (int x = 0; x < times; x++) {
        Logger.log.info("[" + coef.channel + "] iterative " + (x + 1) +
                        " begin");
        double[] start = coef.getStartVariables();
        double[] end = coef.getEndVariables();
        VariablesIterator iterator = new VariablesIterator(this, start, end,
            model.getStepRate());
        iterator.iterative();
        iReport = bestReport;

        DeltaEReport report = iReport.deltaEReport;
        double measuredDeltaE = report.meanDeltaE.getMeasuredDeltaE();
        Logger.log.info(iReport.factor.toString());
        if (previousMeasuredDeltaE != 0) {
          double adv = previousMeasuredDeltaE - measuredDeltaE;
          Logger.log.info(" " + DeltaE.getMeasuredDeltaEDescription() + ": " +
                          measuredDeltaE + " (adv " + adv + ")");
          if (adv < torlerence) {
            if (uselessAdvance) {
              break;
            }
            else {
              uselessAdvance = true;
            }
          }
        }
        else {
          Logger.log.info(" " + //"[" + coef.channel + "] " +
                          DeltaE.getMeasuredDeltaEDescription() + ": " +
                          measuredDeltaE);

        }
        previousMeasuredDeltaE = measuredDeltaE;
        if (x < times - 1) {
          LCDModelBase.Factor factor = (LCDModelBase.Factor) iReport.factor;
          this.coef = model.getNewCoefficientsRange(factor, coef);
        }
        Logger.log.info("[" + coef.channel + "] iterative " + (x + 1) +
                        " end");
      }

      return iReport;
    }

    public CoefficientsRange getNewCoefficientsRange(LCDModelBase.Factor
        factor,
        SimpleThreadCalculator.CoefficientsRange old, double[] steps) {
      double[] vars = factor.getVariables();
      double[] start = DoubleArray.plus(vars, steps);
      double[] end = DoubleArray.minus(vars, steps);
      old.setStartVariables(start);
      old.setEndVariables(end);

      return old;
    }

    public void setVariables(double[] variables) {
      LCDModelBase.Factor[] factors = model.getFactors(variables, coef.channel);
      bestReport = model.getBestIterativeReport(factors, patchList, whitePatch,
                                                bestReport, coef.channel);
    }
  }

  /**
   *
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
   * @deprecated
   */
  private static class IterativeFactorThreadOld
      implements Callable<LCDModelBase.Factor> {
    CoefficientsRange coef;
    Cooperation cooperation;

    IterativeFactorThreadOld(CoefficientsRange coef, Cooperation c) {
      this.coef = coef;
      this.cooperation = c;
    }

    public LCDModelBase.Factor call() {
      //=======================================================================
      // 資料預備
      //=======================================================================
      LCDTarget lcdTargetPatch = cooperation.getLCDTarget();
      List<Patch>
          patchList = lcdTargetPatch.filter.oneValueChannel(coef.channel);

      Patch whitePatch = lcdTargetPatch.getWhitePatch();
      CIEXYZ whitePoint = whitePatch.getXYZ();
      patchList = Patch.Produce.LabPatches(patchList, whitePoint);
      //=======================================================================

      IterativeReport iReport = null;
      int times = cooperation.getMaxIterativeTimes();
      double previousMeasuredDeltaE = 0;

      for (int x = 0; x < times; x++) {
        System.out.println("[" + coef.channel + "] iterative " + (x + 1) +
                           " begin");
        iReport = cooperation.iterateAndReport(coef,
                                               patchList, whitePatch, iReport);

        DeltaEReport report = iReport.deltaEReport;
        double measuredDeltaE = report.meanDeltaE.getMeasuredDeltaE();
        System.out.print(iReport.factor);
        if (previousMeasuredDeltaE != 0) {
          System.out.println(" " + DeltaE.getMeasuredDeltaEDescription() + ": " +
                             measuredDeltaE + " (adv " +
                             (previousMeasuredDeltaE - measuredDeltaE) + ")");
        }
        else {
          System.out.println(" " + //"[" + coef.channel + "] " +
                             DeltaE.getMeasuredDeltaEDescription() + ": " +
                             measuredDeltaE);

        }
        previousMeasuredDeltaE = measuredDeltaE;
        if (x < times - 1) {
          LCDModelBase.Factor factor = (LCDModelBase.Factor) iReport.factor;

          this.coef = cooperation.getNewCoefficientsRange(factor, coef);
        }
        System.out.println("[" + coef.channel + "] iterative " + (x + 1) +
                           " end");
      }

      return iReport.factor;
    }

  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 變數接收器,與VariablesIterator協同使用.
   * VariablesIterator每一次的迭代變數都會傳送給VariablesReceptor
   * VariablesReceptor再利用variables進行必要的運算
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  private static interface VariablesReceptor {
    public void setVariables(double[] variables);
  }

  protected final static double[] produceSteps(double[] start, double[] end,
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
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  private static class VariablesIterator {

    public VariablesIterator(VariablesReceptor receptor, double[] start,
                             double[] end, int divide) {
      this(receptor, start, end, 1. / divide);
    }

    public VariablesIterator(VariablesReceptor receptor, double[] start,
                             double[] end, double stepRate) {
      this.receptor = receptor;
      this.start = start;
      this.end = end;
      size = start.length;
      values = new double[size];
      steps = produceSteps(start, end, stepRate);
    }

    protected VariablesReceptor receptor;
    protected double[] start;
    protected double[] end;
    protected double[] values;
    protected double[] steps;
    protected int size;

    public void iterative() {
      iterative(0);
    }

    /**
     * 遞迴,速度最快
     * @param index int
     */
    private void iterative(int index) {
      for (double x = start[index]; x <= end[index]; x += steps[index]) {
        values[index] = x;
        if (index == size - 1) {
          receptor.setVariables(values);
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
}
