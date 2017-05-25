package shu.cms.devicemodel.lcd;

import shu.cms.devicemodel.lcd.LCDModelBase.*;
import shu.cms.lcd.*;

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
public abstract class ChannelIndependentModel
    extends LCDModel {
  public ChannelIndependentModel(LCDModelFactor factor) {
    super(factor);
  }

  public ChannelIndependentModel(LCDTarget lcdTarget,
                                 LCDTarget rCorrectLCDTarget) {
    super(lcdTarget, rCorrectLCDTarget);
  }

  public ChannelIndependentModel(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  protected ChannelIndependentModel(LCDTarget lcdTarget,
                                    LCDTarget rCorrectLCDTarget,
                                    boolean cooperateWithLCDTargetInterpolator) {
    super(lcdTarget, rCorrectLCDTarget, cooperateWithLCDTargetInterpolator);
  }

}
