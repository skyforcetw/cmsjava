package shu.cms.lcd.calibrate.measured.algo;

import shu.cms.colorspace.depend.*;

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
public abstract class AroundAlgorithm
    extends Algorithm {

  public abstract RGB[] getAroundRGB(RGB centerRGB, double step);

  public abstract RGB[] getAroundRGB(RGB centerRGB, double[] delta,
                                     double step);
}
