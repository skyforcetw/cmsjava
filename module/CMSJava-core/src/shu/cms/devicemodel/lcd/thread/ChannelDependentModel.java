package shu.cms.devicemodel.lcd.thread;

import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 假設頻道相依的LCD模式
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class ChannelDependentModel
    extends LCDModel implements ThreadCalculator.Cooperation {

  public ChannelDependentModel(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  public ChannelDependentModel(LCDModelFactor factor) {
    super(factor);
  }

  protected IterativeReport iterativeReport;

  /**
   * 提供三頻道共用的IterativeReport (ex:scurve2,YY)
   * @return IterativeReport
   */
  protected IterativeReport getIterativeReport() {
    return iterativeReport;
  }

  public final double getStepRate() {
    return 0.25;
  }

  public final double getRangeRate() {
    return 0.6;
  }

}
