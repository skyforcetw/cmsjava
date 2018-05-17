package shu.cms.lcd.benchmark.verify;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;

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
public class RGBColorSpaceVerifier
    extends Verifier {

  public RGBColorSpaceVerifier(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  public VerifierReport RGBColorSapceVerify(RGB.ColorSpace rgbColorSpace) {
//    RGBColorSpaceModel model = new RGBColorSpaceModel(rgbColorSpace);
    ProfileColorSpaceModel model = new ProfileColorSpaceModel(rgbColorSpace);
    model.produceFactor();
    DeltaEReport report = model.testForwardModel(this.lcdTarget, false)[0];
    return new VerifierReport(report);
  }

  /**
   * checkLCDTarget
   *
   * @param lcdTarget LCDTarget
   * @return boolean
   */
  protected boolean checkLCDTarget(LCDTarget lcdTarget) {
    return true;
  }

  public static void main(String[] args) {
//    LCDTarget lcdTarget = LCDTarget.Instance.get("eizo_cg241w",
//                                                 LCDTarget.Source.CA210,
//                                                 LCDTarget.Room.Dark,
//                                                 LCDTarget.TargetIlluminant.
//                                                 Native,
//                                                 LCDTargetBase.Number.Patch125,
//                                                 LCDTarget.FileType.Logo,
//                                                 null, null);
    LCDTarget lcdTarget = LCDTarget.Instance.getFromAUOXLS(
        "D:\\My Documents\\工作\\華山計畫\\LG 42SL90QD\\Standard\\2223.xls");
    lcdTarget = lcdTarget.targetFilter.getTest();
    RGBColorSpaceVerifier verifier = new RGBColorSpaceVerifier(lcdTarget);
    DeltaEReport.setOnlyCountMeasuredDeltaE(false);
    VerifierReport report = verifier.RGBColorSapceVerify(RGB.ColorSpace.sRGB);

    System.out.println(report.result);
  }
}
