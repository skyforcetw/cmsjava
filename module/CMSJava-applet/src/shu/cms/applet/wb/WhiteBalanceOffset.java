package shu.cms.applet.wb;

import shu.cms.dc.DCUtils;
import shu.cms.hvs.cam.ciecam02.CIECAM02;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.cms.colorspace.independ.*;
import shu.cms.CorrelatedColorTemperature;
import shu.cms.hvs.cam.Surround;
import shu.cms.hvs.cam.ciecam02.CIECAM02Color;

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
public class WhiteBalanceOffset {
  public static void main(String[] args) {
    ViewingConditions typicalvc = ViewingConditions.
        TypicalViewingConditions;
    CIECAM02 typicalcam = new CIECAM02(typicalvc);

    System.out.print("\t");
    for (int cct = 2500; cct <= 10000; cct += 500) {
      System.out.print(cct + "\t");
    }
    System.out.println("");
    for (int ev = -6; ev <= 16; ev++) {
      double luminance = DCUtils.luminance(ev);
      System.out.print(ev + "\t");
      for (int cct = 2500; cct <= 10000; cct += 500) {
        CIExyY whitexyY = null;
        if (cct < 4000) {
          whitexyY = CorrelatedColorTemperature.CCT2BlackbodyxyY(cct);
        }
        else {
          whitexyY = CorrelatedColorTemperature.CCT2DIlluminantxyY(cct);
        }
        CIEXYZ whiteXYZ = whitexyY.toXYZ();
        ViewingConditions vc = new ViewingConditions(whiteXYZ,
            luminance, 20, Surround.Dim, "");
        CIECAM02 cam = new CIECAM02(vc);
        CIECAM02Color color = new CIECAM02Color(100, 0, 0);
        CIEXYZ eyeWhite = cam.inverse(color);
        double eyeCCT = CorrelatedColorTemperature.XYZ2CCTByRobertson(eyeWhite);
        int deltaCCT = (int) (eyeCCT - cct);
        deltaCCT = deltaCCT / 50 * 50;

        CIECAM02Color whiteColor = cam.forward(whiteXYZ);
        CIEXYZ inverseWhite = typicalcam.inverse(whiteColor);
        double inverseCCT = CorrelatedColorTemperature.XYZ2CCTByRobertson(
            inverseWhite);

        System.out.print(deltaCCT + "\t");
      }
      System.out.println("");
    }
  }
}
