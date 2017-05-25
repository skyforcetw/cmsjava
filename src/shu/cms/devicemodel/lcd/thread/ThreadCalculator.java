package shu.cms.devicemodel.lcd.thread;

import java.util.*;
import java.util.concurrent.*;

import shu.cms.*;
import shu.cms.devicemodel.lcd.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 採用執行緒進行模式的運算需搭配此類別
 * 提供了使用執行緒運算的一些類別以及函式
 *
 * ThreadCalculator的設計理論:
 * 當初開發SCurve2 & YY模式 時,發現雖然模式本身有所差異,
 * 但是求值的方式,不外乎就是迭代: 改變參數並且比較色差
 * 以抽象概念來說,方式可以說是一樣的.
 * 因此考慮把迭代的功能獨立出來,所以才產生了ThreadCalculator.
 *
 * 另外,因應雙核心處理器的特性,舊有的程式無法發揮雙核心的效能實在是很可惜.
 * 其實只要能夠把運算核心的部份設計成多個Thread的方式,
 * 不同Thread就可以分配到不同的核心上運算.
 * 如此約可增進運算效率30%~50%
 * 所以才有 "Thread"Calculator 這個名字的由來.
 *
 * 附註:
 * 由於ThreadCalculator的設計是在LCDModel設計到一半才決定加進來的,
 * 很多地方都是為了開發方便才做的妥協設計.
 * 因此存在很多感覺很多餘又愚蠢的設計...
 * 不過開發時間的限制下,決定先不理會....Orz
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class ThreadCalculator {

  /**
   * 關掉(false)比較快,而且打開還有一些問題存在(計算結果不一致)
   * 效果尚未測試,建議不要更動
   */
  public final static boolean STOP_WHEN_TOUCHED = false;

  //運算的執行緒數量
  public final static int THREAD_COUNT = 4;

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 要與ThreadCalculator共做的類別,需要實作此介面
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public interface Cooperation {
    /**
     * 迭代並且產生報告
     * @param coefficient IterateCoefficient
     * @return IterativeReport
     */
    IterativeReport iterateAndReport(IterateCoefficient coefficient);

    /**
     * 產生迭代參數
     * @return IterateCoefficient
     */
    IterateCoefficient produceIterateCoefficient();

    /**
     * 迭代的次數
     * @return int
     */
    int getMaxIterativeTimes();

    /**
     * 迭代step的比例
     * @return int
     */
    double getStepRate();

    /**
     * 迭代範圍的比例
     * @return int
     */
    double getRangeRate();

    void modifyCoefficientsRange(LCDModel.Factor[] LCDModelFactors,
                                 int iterateIndex);
  }

  /**
   * 從futureList當中,找到最佳的IterativeReport
   * @param futureList List
   * @param executorService ExecutorService
   * @return IterativeReport
   */
  public final static IterativeReport getBestIterativeReport(List<Future<
      IterativeReport>> futureList, ExecutorService executorService) {
    //==========================================================================
    //將所有運算結果中,找出最佳結果
    //==========================================================================
    IterativeReport bestReport = null;
    int size = futureList.size();
    //紀錄上一個deltaE
    double lastMeasuredDeltaE = Double.MAX_VALUE;
    try {
      for (Future<IterativeReport> f : futureList) {
        Logger.log.info( (futureList.indexOf(f) + 1) + "/" + size);
        //取出運算結果.如果運算尚未有結果,process會遲滯在get(),直到結果出來為止.
        IterativeReport iReport = f.get();
        if (iReport == null) {
          continue;
        }

        DeltaEReport report = iReport.deltaEReport;
        double measuredDeltaE = report.meanDeltaE.getMeasuredDeltaE();

        Logger.log.info(Arrays.deepToString(iReport.factors));
        Logger.log.info(DeltaE.getMeasuredDeltaEDescription() + ": " +
                        measuredDeltaE);

        if (bestReport == null) {
          bestReport = iReport;
        }

        if (ThreadCalculator.STOP_WHEN_TOUCHED) {
          //======================================================================
          //#1 iReport將可能出現空值
          //======================================================================
          bestReport = IterativeReport.analyzeIterativeReport(bestReport,
              iReport);
          //======================================================================
        }
        else {
          //======================================================================
          //#2 依照趨勢設定bestReport
          //======================================================================
          //表示尚未設定deltaE
          if (lastMeasuredDeltaE == Double.MAX_VALUE) {
            lastMeasuredDeltaE = measuredDeltaE;
          }
          else {
            double diff = measuredDeltaE - lastMeasuredDeltaE;
            lastMeasuredDeltaE = measuredDeltaE;
            //表示deltaE開始有上升的趨勢,往後的迭代可以取消
            if (diff > 0) {
              executorService.shutdownNow();
              break;
            }
            bestReport = iReport;
          }
          //======================================================================
        }

      }
    }
    catch (ExecutionException ex) {
      Logger.log.error("", ex);
    }
    catch (InterruptedException ex) {
      Logger.log.error("", ex);
    }
    //==========================================================================

    //關閉執行緒池
    executorService.shutdown();

    return bestReport;
  }

  /**
   * 傳入實作Cooperation的物件,可找到最好的迭代報告
   * @param c Cooperation
   * @return IterativeReport
   */
  public static IterativeReport produceBestIterativeReport(Cooperation
      c) {
    //==========================================================================
    // 迭代用變數
    //==========================================================================
    IterativeReport bestIterativeReport = null;
    int size = c.getMaxIterativeTimes();
    //==========================================================================


    for (int x = 0; x < size; x++) {
      Logger.log.info("iterative " + (x + 1) + " begin");

      IterateCoefficient coef = c.produceIterateCoefficient();

      IterativeReport iReport = c.iterateAndReport(coef);
      if (bestIterativeReport == null) {
        bestIterativeReport = iReport;
      }
      else {
        //找出最好的結果
        IterativeReport result = IterativeReport.analyzeIterativeReport(
            bestIterativeReport,
            iReport);
        if (result == iReport) {
          bestIterativeReport = result;
        }
      }

      //如果還有下一次迭代,就進行 變更係數範圍
      if (x + 1 < size) {
        //變更係數範圍
        c.modifyCoefficientsRange(bestIterativeReport.factors, x);
      }

      Logger.log.info("iterative " + (x + 1) + " end");
    }

    return bestIterativeReport;

  }

  public abstract static class IterativeFactorThread
      implements Callable<IterativeReport> {
    public abstract IterativeReport call();

    public IterativeFactorThread(LCDModel lcdModel) {
      this.lcdModel = lcdModel;
    }

    protected LCDModel lcdModel;
    private double lastMeasuredDeltaE = Double.MAX_VALUE;

    public enum CheckType {
      CheckAll, CheckTrend
    }

    protected final static CheckType checkType = CheckType.CheckTrend;

    public final IterativeReport getBestIterativeReport(LCDModel.Factor[]
        factors, List<Patch> patchList, Patch whitePatch,
        DeltaEReport.AnalyzeType analyzeType, IterativeReport bestReport) {

      //==========================================================
      //根據參數產生色塊
      //==========================================================
      List<Patch>
          modelPatchList =
          lcdModel.test.forwardModelPatchList(patchList,
                                              factors, whitePatch.getXYZ());
      //==========================================================

      DeltaEReport[] reports = null;

      if (ThreadCalculator.STOP_WHEN_TOUCHED) {
        reports = DeltaEReport.Instance.patchReport(patchList,
            modelPatchList, false, 10.);
      }
      else {
        reports = DeltaEReport.Instance.patchReport(patchList,
            modelPatchList, false);
      }

      if (reports == null) {
//        continue;
        return bestReport;
      }

      if (checkType == CheckType.CheckAll) {
        //=========================================================
        //#1 完全檢查所有deltaE
        //=========================================================
        if (bestReport == null) {
          bestReport = new
              IterativeReport(factors,
                              reports[0]);

        }
        else {
          DeltaEReport result =
              DeltaEReport.Analyze.analyzeDeltaEReport(
                  bestReport.deltaEReport,
                  reports[0], analyzeType, true);

          //代表找到新的best report
          if (result == reports[0]) {
            bestReport = new
                IterativeReport(factors,
                                reports[0]);
          }
        }
        //=========================================================
      }
      else {
        //=========================================================
        //#2根據deltaE的走勢決定是否要繼續
        //=========================================================
        if (bestReport == null) {
          bestReport = new
              IterativeReport(factors,
                              reports[0]);

        }

        double measuredDeltaE = reports[0].meanDeltaE.getMeasuredDeltaE();

        //表示尚未設定deltaE
        if (lastMeasuredDeltaE == Double.MAX_VALUE) {
          lastMeasuredDeltaE = measuredDeltaE;
        }
        else {
          double diff = measuredDeltaE - lastMeasuredDeltaE;
          lastMeasuredDeltaE = measuredDeltaE;
          //表示deltaE開始有上升的趨勢,往後的迭代可以取消
          if (diff > 0) {
//          break;
            return bestReport;
          }

          DeltaEReport result =
              DeltaEReport.Analyze.analyzeDeltaEReport(
                  bestReport.deltaEReport,
                  reports[0], analyzeType, true);

          //代表找到新的best report
          if (result == reports[0] &&
              result != bestReport.deltaEReport) {
            bestReport = new
                IterativeReport(factors,
                                reports[0]);
          }
        }
        //====================================================================
      }

      return bestReport;
    }

  }

  /**
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 計算用的係數內容繼承自此類別
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public interface IterateCoefficient {

  }

}
