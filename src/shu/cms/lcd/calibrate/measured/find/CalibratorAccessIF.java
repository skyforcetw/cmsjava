package shu.cms.lcd.calibrate.measured.find;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.calibrate.measured.algo.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * ����Calibrator�һݭn���򥻤���, �]�A�t��k�H�ήե���T������.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public interface CalibratorAccessIF
    extends AlogorithmAccessIF {

  public void trace(String msg);

  public CIExyY getTargetxyY(int index);

  public void addMaxAroundTouched();

  public NearestAlgorithm getIndexNearestAlogorithm();

  public RGBBase.MaxValue getInitStep();
}
