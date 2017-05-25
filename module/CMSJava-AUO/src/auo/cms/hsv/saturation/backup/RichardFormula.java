package auo.cms.hsv.saturation.backup;

import shu.cms.lcd.LCDTarget;
import shu.cms.lcd.LCDTargetBase;
import shu.cms.colorspace.depend.RGB;
import shu.cms.devicemodel.lcd.LCDModel;
import shu.cms.devicemodel.lcd.MultiMatrixModel;
import auo.cms.hsv.saturation.*;

/**
 *
 * <p>Title: Colour Management System</p>
 * RichardFormula Âùgain
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class RichardFormula
    implements SaturationFormula {
  public String getName() {
    return "2gai" + Integer.toString(turnPoint);
  }

  private int turnPoint;
  public RichardFormula(int turnPoint) {
    this.turnPoint = turnPoint;
  }

  public double getSaturartion(double originalSaturation, double adjustValue) {
    if (originalSaturation == 0) {
      return 0;
    }
    double gain = -1;
    double c = Math.pow(2, 0);
    if (originalSaturation < turnPoint) {

      gain = (adjustValue / c) * (c / turnPoint);
      return originalSaturation + originalSaturation * gain;
    }
    else {
      gain = (adjustValue / c) * (c / (turnPoint - 100));
      return originalSaturation + gain * (originalSaturation - 100);
    }
  }

  public double getAdjustValue(double originalSaturation,
                               double newSaturation) {
    double delta = newSaturation - originalSaturation;

    if (originalSaturation < turnPoint) {
      double gain = delta / originalSaturation;
      return turnPoint * gain;
    }
    else {
      double gain = delta / (originalSaturation - 100);
      return (turnPoint - 100) * gain;
    }
  }

  public short getSaturartion(short originalSaturation, short adjustValue) {
    throw new UnsupportedOperationException();
  }

  public static void main(String[] args) {

  }
}
