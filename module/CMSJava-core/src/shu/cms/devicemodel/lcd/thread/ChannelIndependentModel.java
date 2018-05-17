package shu.cms.devicemodel.lcd.thread;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 安砞繵笵縒ミLCD家Α
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class ChannelIndependentModel
    extends LCDModel implements SimpleThreadCalculator.Cooperation {

  public ChannelIndependentModel(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  public ChannelIndependentModel(LCDModelFactor factor) {
    super(factor);
  }

  protected IterativeReport[] iterativeReports = new IterativeReport[3];

  /**
   * 矗ㄑ繵笵IterativeReport (ex:scurve1)
   * @return IterativeReport[]
   */
  public final IterativeReport[] getIterativeReports() {
    return iterativeReports;
  }

  /**
   * 玻ネ程ㄎIterativeReport
   * @param factors Factor[]
   * @param patchList List
   * @param whitePatch Patch
   * @param bestReport IterativeReport
   * @param channel Channel
   * @return IterativeReport
   */
  protected final IterativeReport getBestIterativeReport(Factor[] factors,
      List<Patch>
      patchList, Patch whitePatch, IterativeReport bestReport,
      RGBBase.Channel channel) {

    DeltaEReport[] reports = getDeltaEReport(factors, patchList, whitePatch);
    if (bestReport == null) {
      bestReport = new IterativeReport(factors[channel.getArrayIndex()],
                                       reports[0]);
    }
    else {
      //だ猂程ㄎreport
      DeltaEReport result = DeltaEReport.Analyze.analyzeDeltaEReport(
          bestReport.deltaEReport,
          reports[0]);
      //狦硂report纔ヘ玡程ㄎ,碞穝
      if (result == reports[0]) {
        bestReport = new IterativeReport(factors[channel.getArrayIndex()],
                                         reports[0]);

      }
    }
    return bestReport;
  }

  public final double getStepRate() {
    return 0.125;
  }

  public final double getRangeRate() {
    return 0.6;
  }
}
