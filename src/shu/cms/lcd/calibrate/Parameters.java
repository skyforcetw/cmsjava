package shu.cms.lcd.calibrate;

import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.calibrate.parameter.*;

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
public class Parameters {

  public Parameters(LCDModel lcdModel,
                    WhiteParameter whiteParameter,
                    ViewingParameter viewingParameter,
                    ColorProofParameter colorProofParameter,
                    AdjustParameter adjustParameter,
                    MeasureParameter measureParameter) {
    this.lcdModel = lcdModel;
    this.whiteParameter = whiteParameter;
    this.adjustParameter = adjustParameter;
    this.viewingParameter = viewingParameter;
    this.colorProofParameter = colorProofParameter;
    this.measureParameter = measureParameter;
  }

  public LCDModel lcdModel;
  public WhiteParameter whiteParameter;
  public AdjustParameter adjustParameter;
  public ViewingParameter viewingParameter;
  public ColorProofParameter colorProofParameter;
  public MeasureParameter measureParameter;
}
