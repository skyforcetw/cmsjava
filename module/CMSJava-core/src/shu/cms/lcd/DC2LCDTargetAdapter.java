package shu.cms.lcd;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;
import shu.cms.profile.*;

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

public abstract class DC2LCDTargetAdapter {
  private DC2LCDTargetAdapter() {};

  private final static void reproduceDCTargetLab(DCTarget dcTarget,
                                                 Patch whitePatch) {
    //======================================================================
    // 依照XYZ重新產生RGB值
    //======================================================================
    List<Patch> patchList = dcTarget.getPatchList();
    CIEXYZ normalizedWhiteXYZ = whitePatch.getNormalizedXYZ();

    for (Patch p : patchList) {
      CIEXYZ normalizedXYZ = p.getNormalizedXYZ();
      CIELab Lab = new CIELab(normalizedXYZ, normalizedWhiteXYZ);
      Patch.Operator.setLab(p, Lab);
    }

  }

//  private final static CIEXYZ getWhiteXYZ(Patch whitestPatch) {
//    CIEXYZ XYZ = whitestPatch.getNormalizedXYZ();
//    CIEXYZ whiteXYZ = (CIEXYZ) XYZ.clone();
//    whiteXYZ.normalizeY();
//    return XYZ;
//  }

  private final static Patch getReferenceWhitePatch(Illuminant referenceWhite) {
    Spectra spectra = (Spectra) referenceWhite.getSpectra().clone();
    CIEXYZ whiteXYZ = spectra.getXYZ();
    CIEXYZ normalizedWhiteXYZ = (CIEXYZ) whiteXYZ.clone();
    normalizedWhiteXYZ.normalizeY();
    Patch p = new Patch("Reference White", whiteXYZ, normalizedWhiteXYZ, null, null,
                        spectra, spectra);
    return p;
  }

  private final static void reproduceDCTargetRGBByColorSpace(DCTarget
      dcTarget, RGB.ColorSpace rgbColorSpace, Patch whitePatch) {
    //======================================================================
    // 依照XYZ重新產生RGB值
    //======================================================================
    List<Patch> patchList = dcTarget.getPatchList();
    patchList.add(whitePatch);
    CIEXYZ normalizedWhiteXYZ = whitePatch.getNormalizedXYZ();

    for (Patch p : patchList) {
      CIEXYZ normalizedXYZ = p.getNormalizedXYZ();

      RGB rgb = new RGB(rgbColorSpace, normalizedXYZ);
      rgb.changeMaxValue(RGB.MaxValue.Double255);
      Patch.Operator.setOriginalRGB(p, rgb);
      Patch.Operator.setRGB(p, rgb);

      CIELab Lab = new CIELab(normalizedXYZ, normalizedWhiteXYZ);
      Patch.Operator.setLab(p, Lab);
    }

    //======================================================================

  }

  public final static class Instance {

    public final static LCDTarget getCameraTarget(DCTargetBase.Chart chart,
                                                  RGB.ColorSpace rgbColorSpace,
                                                  ProfileColorSpace pcs) {
      LCDTarget target = getCameraTarget(chart, rgbColorSpace);
      List<Patch> patchList = target.getPatchList();
      CIEXYZ refereceWhite = pcs.getReferenceWhite();
//      CIEXYZ refereceWhite = new CIEXYZ(pcs.getReferenceWhite(), null);
      double[] rgbValues = new double[3];
      for (Patch patch : patchList) {
        RGB rgb = patch.getRGB();
//        RGB cloneRGB = (RGB) rgb.clone();
        rgb.rationalize();
        rgb.getValues(rgbValues, RGB.MaxValue.Double1);

        double[] XYZValues = pcs.toCIEXYZValues(rgbValues);
        CIEXYZ XYZ = new CIEXYZ(XYZValues, refereceWhite);
        CIEXYZ normalizedXYZ = (CIEXYZ) XYZ.clone();
        normalizedXYZ.normalize(refereceWhite);
        CIELab Lab = new CIELab(XYZ, refereceWhite);

        Patch.Operator.setLab(patch, Lab);
        Patch.Operator.setXYZ(patch, XYZ);
        Patch.Operator.setNormalizedXYZ(patch, normalizedXYZ);
      }
      return target;
    }

    public final static LCDTarget getCameraTarget(DCTargetBase.Chart chart,
                                                  RGB.ColorSpace rgbColorSpace) {
      Illuminant referenceWhite = rgbColorSpace.referenceWhite;
      IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
          IdealDigitalCamera.Source.CIEXYZ, rgbColorSpace.referenceWhite);
      LightSource.Source source = LightSource.getLightSourceType(
          referenceWhite);

      //利用camera
      DCTarget dcTarget = DCTarget.Instance.get(camera, source, chart);
      Patch whitePatch = getReferenceWhitePatch(referenceWhite);
      reproduceDCTargetRGBByColorSpace(dcTarget, rgbColorSpace, whitePatch);
      reproduceDCTargetLab(dcTarget, whitePatch);

      LCDTarget lcdTarget = LCDTarget.Instance.get(dcTarget.getPatchList(),
          LCDTarget.Number.Camera, false);
      return lcdTarget;
    }

  }

  public static void main(String[] args) {
    LCDTarget target = Instance.getCameraTarget(DCTarget.Chart.CC24,
                                                RGB.ColorSpace.sRGB);
    for (Patch p : target.getPatchList()) {
      System.out.println(p);
    }
  }
}
