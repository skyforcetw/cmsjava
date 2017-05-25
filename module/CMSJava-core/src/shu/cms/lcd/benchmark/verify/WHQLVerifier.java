package shu.cms.lcd.benchmark.verify;

import java.util.*;

import javax.swing.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.xls.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.ui.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * WHQL標準, 也就是sRGB的驗證
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class WHQLVerifier
    extends Verifier {
  protected ProfileColorSpaceModel sRGBModel = new ProfileColorSpaceModel(RGB.
      ColorSpace.sRGB);
  public WHQLVerifier(LCDTarget lcdTarget) {
    super(lcdTarget);

    sRGBModel.produceFactor();
  }

  protected boolean checkLCDTarget(LCDTarget lcdTarget) {
    return lcdTarget.getNumber() == LCDTargetBase.Number.WHQL;
  }

  protected DeltaEReport standAloneLCDReport;
  protected DeltaEReport integratedLCDReport;
  protected DeltaEReport IEC61966_4LCDReport;

  /**
   * 桌上型顯示器的sRGB(WHQL)驗證
   * @return VerifierReport
   */
  public final VerifierReport standAloneLCDVerify() {
    List<Patch> standAlonePatchList = lcdTarget.filter.getRange(0, 7);
    DeltaEReport.setOnlyCountMeasuredDeltaE(false);
    standAloneLCDReport = sRGBModel.testForwardModel(standAlonePatchList, false)[
        0];
    boolean result = standAloneLCDReport.meanDeltaE.getCIE94DeltaE() <= 10 &&
        standAloneLCDReport.maxDeltaE.getCIE94DeltaE() <= 15;
    return new VerifierReport(result);
  }

  /**
   * 整合式顯示器, 也就是筆電或者攜帶設備的顯示器sRGB(WHQL)驗證
   * @return VerifierReport
   */
  public final VerifierReport integratedLCDVerify() {
    List<Patch> integratedPatchList = lcdTarget.filter.getRange(7, 14);
    DeltaEReport.setOnlyCountMeasuredDeltaE(false);
    integratedLCDReport = sRGBModel.testForwardModel(integratedPatchList, false)[
        0];
    boolean result = integratedLCDReport.meanDeltaE.getCIE94DeltaE() <= 10 &&
        integratedLCDReport.maxDeltaE.getCIE94DeltaE() <= 15;
    return new VerifierReport(result);
  }

  /**
   * IEC61966_4的驗證
   * @return VerifierReport
   */
  public final VerifierReport IEC61966_4Verify() {
    List<Patch> IEC61966_4 = lcdTarget.filter.getRange(14, 46);
    DeltaEReport.setOnlyCountMeasuredDeltaE(false);
    IEC61966_4LCDReport = sRGBModel.testForwardModel(IEC61966_4, false)[
        0];

    boolean result = IEC61966_4LCDReport.meanDeltaE.getCIE94DeltaE() <= 20;
    return new VerifierReport(result);
  }

  public static void main(String[] args) throws Exception {

    LCDTarget target871 = LCDTarget.Instance.get(new AUOMeasureXLSAdapter(
        "871.xls"));
    LCDTarget lcdTarget = target871.targetFilter.getWHQL();

    System.out.println("CCT: " + lcdTarget.getLuminance().getCCT());

    WHQLVerifier verifier = new WHQLVerifier(lcdTarget);
    System.out.println("Stand alone\n" + verifier.standAloneLCDVerify());
    System.out.println("Integrated\n" + verifier.integratedLCDVerify());
    System.out.println("IEC61966-4\n" + verifier.IEC61966_4Verify());
    System.out.println("Stand alone:\n" + verifier.standAloneLCDReport);
    System.out.println("Integrated:\n" + verifier.integratedLCDReport);
    System.out.println("IEC61966-4:\n" + verifier.IEC61966_4LCDReport);

    //==========================================================================
    // draw
    //==========================================================================
    int size = lcdTarget.size();
    List<RGB> lcdRGBList = new ArrayList<RGB> (size);
    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = lcdTarget.getPatch(x).getNormalizedXYZ();
      RGB rgb = RGB.fromXYZ(XYZ, RGB.ColorSpace.sRGB);
      rgb.rationalize();
      lcdRGBList.add(rgb);
    }
    List<RGB> orgRGBList = lcdTarget.filter.rgbList();
    JFrame frame = PatchCanvas.getJFrameInstance(400, 300, orgRGBList,
                                                 lcdRGBList);
    frame.setVisible(true);
    //==========================================================================
  }

  public DeltaEReport getStandAloneLCDReport() {
    return standAloneLCDReport;
  }

  public DeltaEReport getIntegratedLCDReport() {
    return integratedLCDReport;
  }

  public DeltaEReport getIEC61966_4LCDReport() {
    return IEC61966_4LCDReport;
  }
}
