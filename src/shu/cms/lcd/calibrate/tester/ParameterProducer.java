package shu.cms.lcd.calibrate.tester;

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
public interface ParameterProducer {
  public ViewingParameter getViewingParameter();

  public AdjustParameter getAdjustParameter();

  public WhiteParameter[] getWhiteParameterArray();

  public ColorProofParameter.CCTCalibrate[] getCCTCalibrateArray();

  public MeasureParameter getMeasureParameter();

  public int[] getTuneCodeArray();

  public double[] getGammaArray();

  public LCDModel getLCDModel();

  public ColorProofParameter getInitCPParameter();
}
