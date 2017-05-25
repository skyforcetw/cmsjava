package auo.cms.hsv.saturation;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OriginalSaturationFormula
    implements SaturationFormula {
  /**
   *
   * @param originalSaturation double 0~100%
   * @param adjustValue double
   * @return double
   */
  public double getSaturartion(double originalSaturation, double adjustValue) {
    double saturation = originalSaturation * (adjustValue / 32.);
    return saturation;
  }

  /**
   *
   * @param originalSaturation double 0~100%
   * @param newSaturation double 0~100%
   * @return double
   */
  public double getAdjustValue(double originalSaturation, double newSaturation) {
    double gain = newSaturation / originalSaturation;
    return gain * 32;
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return "Original Saturation";
  }

  public byte getAdjustOffset(double adjustValue) {
    byte adjustOffset = (byte) Math.floor(adjustValue);
    return adjustOffset;
  }

  public short getSaturartion(short originalSaturation, short adjustValue) {
    double doubleSaturation = getSaturartion(originalSaturation / 1023. * 100.,
                                             (double) adjustValue);
    return (short) Math.round(doubleSaturation);
  };
}
