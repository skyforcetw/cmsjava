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
 * �ĥΰ�����i��Ҧ����B��ݷf�t�����O
 * ���ѤF�ϥΰ�����B�⪺�@�����O�H�Ψ禡
 *
 * ThreadCalculator���]�p�z��:
 * ���}�oSCurve2 & YY�Ҧ� ��,�o�{���M�Ҧ��������Үt��,
 * ���O�D�Ȫ��覡,���~�G�N�O���N: ���ܰѼƨåB�����t
 * �H��H�����ӻ�,�覡�i�H���O�@�˪�.
 * �]���Ҽ{�⭡�N���\��W�ߥX��,�ҥH�~���ͤFThreadCalculator.
 *
 * �t�~,�]�����֤߳B�z�����S��,�¦����{���L�k�o�����֤ߪ��į��b�O�ܥi��.
 * ���u�n�����B��֤ߪ������]�p���h��Thread���覡,
 * ���PThread�N�i�H���t�줣�P���֤ߤW�B��.
 * �p�����i�W�i�B��Ĳv30%~50%
 * �ҥH�~�� "Thread"Calculator �o�ӦW�r���Ѩ�.
 *
 * ����:
 * �ѩ�ThreadCalculator���]�p�O�bLCDModel�]�p��@�b�~�M�w�[�i�Ӫ�,
 * �ܦh�a�賣�O���F�}�o��K�~��������]�p.
 * �]���s�b�ܦh�Pı�ܦh�l�S�M�����]�p...
 * ���L�}�o�ɶ�������U,�M�w�����z�|....Orz
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
   * ����(false)�����,�ӥB���}�٦��@�ǰ��D�s�b(�p�⵲�G���@�P)
   * �ĪG�|������,��ĳ���n���
   */
  public final static boolean STOP_WHEN_TOUCHED = false;

  //�B�⪺������ƶq
  public final static int THREAD_COUNT = 4;

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * �n�PThreadCalculator�@�������O,�ݭn��@������
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
     * ���N�åB���ͳ��i
     * @param coefficient IterateCoefficient
     * @return IterativeReport
     */
    IterativeReport iterateAndReport(IterateCoefficient coefficient);

    /**
     * ���ͭ��N�Ѽ�
     * @return IterateCoefficient
     */
    IterateCoefficient produceIterateCoefficient();

    /**
     * ���N������
     * @return int
     */
    int getMaxIterativeTimes();

    /**
     * ���Nstep�����
     * @return int
     */
    double getStepRate();

    /**
     * ���N�d�򪺤��
     * @return int
     */
    double getRangeRate();

    void modifyCoefficientsRange(LCDModel.Factor[] LCDModelFactors,
                                 int iterateIndex);
  }

  /**
   * �qfutureList��,���̨Ϊ�IterativeReport
   * @param futureList List
   * @param executorService ExecutorService
   * @return IterativeReport
   */
  public final static IterativeReport getBestIterativeReport(List<Future<
      IterativeReport>> futureList, ExecutorService executorService) {
    //==========================================================================
    //�N�Ҧ��B�⵲�G��,��X�̨ε��G
    //==========================================================================
    IterativeReport bestReport = null;
    int size = futureList.size();
    //�����W�@��deltaE
    double lastMeasuredDeltaE = Double.MAX_VALUE;
    try {
      for (Future<IterativeReport> f : futureList) {
        Logger.log.info( (futureList.indexOf(f) + 1) + "/" + size);
        //���X�B�⵲�G.�p�G�B��|�������G,process�|�𺢦bget(),���쵲�G�X�Ӭ���.
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
          //#1 iReport�N�i��X�{�ŭ�
          //======================================================================
          bestReport = IterativeReport.analyzeIterativeReport(bestReport,
              iReport);
          //======================================================================
        }
        else {
          //======================================================================
          //#2 �̷��Ͷճ]�wbestReport
          //======================================================================
          //��ܩ|���]�wdeltaE
          if (lastMeasuredDeltaE == Double.MAX_VALUE) {
            lastMeasuredDeltaE = measuredDeltaE;
          }
          else {
            double diff = measuredDeltaE - lastMeasuredDeltaE;
            lastMeasuredDeltaE = measuredDeltaE;
            //���deltaE�}�l���W�ɪ��Ͷ�,���᪺���N�i�H����
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

    //�����������
    executorService.shutdown();

    return bestReport;
  }

  /**
   * �ǤJ��@Cooperation������,�i���̦n�����N���i
   * @param c Cooperation
   * @return IterativeReport
   */
  public static IterativeReport produceBestIterativeReport(Cooperation
      c) {
    //==========================================================================
    // ���N���ܼ�
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
        //��X�̦n�����G
        IterativeReport result = IterativeReport.analyzeIterativeReport(
            bestIterativeReport,
            iReport);
        if (result == iReport) {
          bestIterativeReport = result;
        }
      }

      //�p�G�٦��U�@�����N,�N�i�� �ܧ�Y�ƽd��
      if (x + 1 < size) {
        //�ܧ�Y�ƽd��
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
      //�ھڰѼƲ��ͦ��
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
        //#1 �����ˬd�Ҧ�deltaE
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

          //�N����s��best report
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
        //#2�ھ�deltaE�����ըM�w�O�_�n�~��
        //=========================================================
        if (bestReport == null) {
          bestReport = new
              IterativeReport(factors,
                              reports[0]);

        }

        double measuredDeltaE = reports[0].meanDeltaE.getMeasuredDeltaE();

        //��ܩ|���]�wdeltaE
        if (lastMeasuredDeltaE == Double.MAX_VALUE) {
          lastMeasuredDeltaE = measuredDeltaE;
        }
        else {
          double diff = measuredDeltaE - lastMeasuredDeltaE;
          lastMeasuredDeltaE = measuredDeltaE;
          //���deltaE�}�l���W�ɪ��Ͷ�,���᪺���N�i�H����
          if (diff > 0) {
//          break;
            return bestReport;
          }

          DeltaEReport result =
              DeltaEReport.Analyze.analyzeDeltaEReport(
                  bestReport.deltaEReport,
                  reports[0], analyzeType, true);

          //�N����s��best report
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
   * �p��Ϊ��Y�Ƥ��e�~�Ӧۦ����O
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
