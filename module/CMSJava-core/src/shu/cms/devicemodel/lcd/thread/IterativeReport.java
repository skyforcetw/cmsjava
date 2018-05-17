package shu.cms.devicemodel.lcd.thread;

import shu.cms.*;
import shu.cms.devicemodel.lcd.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 迭代報告
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class IterativeReport {
  public final String toString() {
    StringBuilder buf = new StringBuilder();
    if (factor == null) {
      for (LCDModelBase.Factor f : factors) {
        buf.append(f + "\n");
      }
    }
    else {
      buf.append(factor);
    }
    buf.append(deltaEReport);
    return buf.toString();
  }

  public IterativeReport(LCDModelBase.Factor factor, DeltaEReport report) {
    this.factor = factor;
    this.deltaEReport = report;
  }

  public IterativeReport(LCDModelBase.Factor[] factors, DeltaEReport report) {
    int size = factors.length;
    this.factors = new LCDModelBase.Factor[size];
    System.arraycopy(factors, 0, this.factors, 0, size);
    this.deltaEReport = report;
  }

  public LCDModelBase.Factor factor;
  public LCDModelBase.Factor[] factors;
  public DeltaEReport deltaEReport;

  /**
   * 分析報告,會找出最佳的結果
   * @param report1 IterativeReport
   * @param report2 IterativeReport
   * @return IterativeReport
   */
  public final static IterativeReport analyzeIterativeReport(IterativeReport
      report1, IterativeReport report2) {
    DeltaEReport result = DeltaEReport.Analyze.analyzeDeltaEReport(report1.
        deltaEReport,
        report2.deltaEReport);
    if (result == report1.deltaEReport) {
      return report1;
    }
    else {
      return report2;
    }
  }

}
