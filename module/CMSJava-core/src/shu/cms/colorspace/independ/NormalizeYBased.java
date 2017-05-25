package shu.cms.colorspace.independ;

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
public abstract class NormalizeYBased
    extends DeviceIndependentSpace {
  protected NormalizeYBased() {
    super();
  }

  protected NormalizeYBased(CIEXYZ white) {
    super(white);
  }

  protected NormalizeYBased(double[] values, CIEXYZ white) {
    super(values, white);
  }

  protected NormalizeYBased(double[] values) {
    super(values);
  }

  protected NormalizeYBased(double value1, double value2, double value3) {
    super(value1, value2, value3);
  }

  protected NormalizeYBased(double value1, double value2, double value3,
                            CIEXYZ white) {
    super(value1, value2, value3, white);
  }

  protected NormalizeYBased(double value1, double value2, double value3,
                            CIEXYZ white, CIEXYZ originalWhite) {
    super(value1, value2, value3, white, originalWhite);
  }

  protected NormalizeYBased(double[] values, CIEXYZ white, CIEXYZ originalWhite,
                            boolean adaptedToD65) {
    super(values, white, originalWhite, adaptedToD65);
  }

}
