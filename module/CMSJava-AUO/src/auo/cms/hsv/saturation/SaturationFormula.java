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
 * @author skyforce
 * @version 1.0
 */
public interface SaturationFormula {
  /**
   *
   * @param originalSaturation double 0~100%
   * @param adjustValue double
   * @return double
   */
  public double getSaturartion(double originalSaturation, double adjustValue);

  /**
   *
   * @param originalSaturation double 0~100%
   * @param newSaturation double 0~100%
   * @return double
   */
  public double getAdjustValue(double originalSaturation, double newSaturation);

  public short getSaturartion(short originalSaturation, short adjustValue);

  public String getName();

}
