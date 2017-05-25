package shu.cms.devicemodel.lcd.xtalk;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;

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
public abstract class AbstractXTalkReconstructor {
  public abstract RGB getXTalkRGB(CIEXYZ XYZ, final RGB originalRGB,
                                  boolean relativeXYZ);

  protected MultiMatrixModel mmModel;
  protected LCDModelExposure adapter;
  protected XTalkProperty xtalkProperty;

  protected AbstractXTalkReconstructor(MultiMatrixModel mmModel,
                                       XTalkProperty xtalkProperty) {
    this.mmModel = mmModel;
    this.adapter = new LCDModelExposure(mmModel);
    this.xtalkProperty = xtalkProperty;
  }

  protected DeltaE _getXTalkRGBDeltaE;

  public final DeltaE getXTalkRGBDeltaE() {
    return _getXTalkRGBDeltaE;
  }
}
