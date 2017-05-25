package shu.cms.applet.lcd;

import java.text.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來驗證LCD符合RGB色彩空間的準確度
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class LCDTargetVerifier {

  /**
   * 利用lcdTarget的RGB數值,並且在rgbColorSpace的RGB空間下,計算其Patch,包括XYZ/Lab
   * @param lcdTarget LCDTarget
   * @param rgbColorSpace RGBColorSpace
   * @return List
   */
  public final static List<Patch> produceRGBColorSpacePatchList(LCDTarget
      lcdTarget,
      RGB.ColorSpace rgbColorSpace) {
    List<Patch> targetPatchList = lcdTarget.getPatchList();
    int size = targetPatchList.size();
    List<Patch> rgbColorSpacePatchList = new ArrayList<Patch> (size);
    double[] rgbValues = new double[3];
    CIEXYZ white = rgbColorSpace.getReferenceWhiteXYZ();

    for (int x = 0; x < size; x++) {
      Patch p1 = targetPatchList.get(x);
      RGB rgb = p1.getRGB();
      rgb.getValues(rgbValues);
      double[] XYZValues = RGB.toXYZValues(rgbValues, rgbColorSpace);
      CIEXYZ XYZ = new CIEXYZ(XYZValues, white);
      CIELab Lab = CIELab.fromXYZ(XYZ, white);
      Patch p2 = new Patch(p1.getName(), XYZ, Lab, rgb);
      rgbColorSpacePatchList.add(p2);
    }

    return rgbColorSpacePatchList;
  }

  /**
   * 比較兩台螢幕之間的色差
   * @param lcdTargetReference LCDTarget 參考螢幕,白點將以此螢幕為基準
   * @param lcdTargetTested LCDTarget 待測螢幕
   * @param doColorDividing boolean
   * @return DeltaEReport[]
   */
  public final static DeltaEReport[] verify(LCDTarget lcdTargetReference,
                                            LCDTarget lcdTargetTested,
                                            boolean doColorDividing) {
    CIEXYZ white = lcdTargetReference.getWhitePatch().getXYZ();
    List<Patch> mainTarget = lcdTargetReference.getLabPatchList(white);
    List<Patch> target = lcdTargetTested.getLabPatchList(white);
    DeltaEReport[] reports = DeltaEReport.Instance.patchReport(mainTarget,
        target,
        doColorDividing);
    return reports;
  }

  public final static DeltaEReport[] verify(LCDTarget lcdTarget,
                                            RGB.ColorSpace rgbColorSpace,
                                            boolean doColorDividing) {
    List<Patch>
        rgbColorSpaceListPatch = produceRGBColorSpacePatchList(lcdTarget,
        rgbColorSpace);
    CIEXYZ white = rgbColorSpace.referenceWhite.getNormalizeXYZ();
    DeltaEReport[] reports = DeltaEReport.Instance.patchReport(lcdTarget.
        getLabPatchList(
            white), rgbColorSpaceListPatch, doColorDividing);
    return reports;
  }

  public static void main(String[] args) {
    DecimalFormat df = new DecimalFormat("##.###");
    LCDTarget lcdTarget = LCDTarget.Instance.get("Dell_2407WFP_HC",
                                                 LCDTarget.Source.i1pro,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.
                                                 D65,
                                                 LCDTargetBase.Number.Test4096, null, null);
//    LCDTarget lcdTarget = LCDTarget.Instance.getInstance(
//        "Measurement Files/Monitor/PK/L204WT_Cal3.cxf");

    DeltaEReport[] reports = verify(lcdTarget, RGB.ColorSpace.AdobeRGB, false);
//    DeltaEReport[] reports = verify(lcdTarget1, lcdTarget, false);

    for (DeltaEReport r : reports) {
//      System.out.println(r);
      DeltaEReport.PatchDeltaEReport pdr = r.getPatchDeltaEReport();
      System.out.println(df.format(r.meanDeltaE.getCIE2000DeltaE()) + " " +
                         df.format(r.minDeltaE.getCIE2000DeltaE()) + " " +
                         df.format(r.maxDeltaE.getCIE2000DeltaE()) + " " +
                         df.format(r.mixDeltaE.getCIE2000DeltaE()) + " " +
                         df.format(r.stdDeltaE.getCIE2000DeltaE()) + " " +
                         pdr.sizeOfGreaterThanReportDeltaE());

//      System.out.println(pdr);
    }
  }
}
