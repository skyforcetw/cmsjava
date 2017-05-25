package shu.cms.colorspace.independ;

import shu.cms.hvs.cam.*;

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
public abstract class LMSBasis
    extends DeviceIndependentSpace {
  protected CAMConst.CATType catType = null; //CAMConst.CATType.Bradford;

  public final CAMConst.CATType getCATType() {
    return catType;
  }

  protected LMSBasis() {
    super();
  }

  protected LMSBasis(double[] values, CIEXYZ white) {
    super(values, white);
  }

  protected LMSBasis(double[] values, CIEXYZ white, CAMConst.CATType catType) {
    super(values, white);
    this.catType = catType;
  }

  protected LMSBasis(double[] values) {
    super(values);
  }

  protected LMSBasis(double[] values, CAMConst.CATType catType) {
    super(values);
    this.catType = catType;
  }

  protected LMSBasis(double value1, double value2, double value3) {
    super(value1, value2, value3);
  }

  protected LMSBasis(double value1, double value2, double value3,
                     CAMConst.CATType catType) {
    super(value1, value2, value3);
    this.catType = catType;
  }

  protected LMSBasis(double value1, double value2, double value3, CIEXYZ white,
                     CIEXYZ originalWhite, CAMConst.CATType catType) {
    super(value1, value2, value3, white, originalWhite);
    this.catType = catType;
  }

  protected LMSBasis(double[] values, CIEXYZ white,
                     CIEXYZ originalWhite, boolean adaptedToD65,
                     CAMConst.CATType catType) {
    super(values, white, originalWhite, adaptedToD65);
    this.catType = catType;
  }
}
