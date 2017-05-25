package shu.cms.hvs.pca;

import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.CAMConst.*;

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
public class TC8_04
    extends PartialChromaticAdaptation implements DFactorIF {
  /**
   * TC8_04
   *
   * @param vc ViewingConditions
   */
  public TC8_04(ViewingConditions vc) {
    super(vc, CATType.CAT02);
    shu.cms.hvs.cam.ciecam02.ViewingConditions cam02vc = new shu.cms.hvs.cam.
        ciecam02.ViewingConditions(vc.displayWhite, vc.LA, 20,
                                   vc.surround, null);
    this.d = cam02vc.d;
//    this.d = 0.632;
  }

  /**
   * 取得部分適應的係數
   *
   * @return double[]
   */
  protected double[] getpLMSValues() {
    throw new UnsupportedOperationException();
  }

  private double d;

  /**
   * getDFactor
   *
   * @return double
   */
  public double getDFactor() {
    return d;
  }

  public static void main(String[] args) {
    CIEXYZ data = new CIEXYZ(38, 40, 59);
    CIEXYZ dispWhite = new CIEXYZ(76.23, 80.00, 118.63);
    CIEXYZ ambWhite = new CIEXYZ(161.53, 160.00, 102.01);
    ViewingConditions vc = new ViewingConditions(dispWhite, ambWhite, 16,
                                                 Surround.Average);
    TC8_04 tc804 = new TC8_04(vc);
    tc804.plotSubjectiveNeutralPoints();
    tc804.plotMCANeutralPoints(100);
  }
}
