package shu.cms.lcd.calibrate.test;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
///import shu.plot.*;

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
 * @deprecated
 */
public class AutoCPVerifier {

  public static void main(String[] args) {
    LCDTarget.setRGBNormalize(false);
//    String device = "cpt_320WF01SC";
    String device = "cpt_320WF0141";
//    String tag = "6500k_cp_ch1";
//    String tag = "cp_ch1_1021";
//    String tag = "ch1_1024";
    String tag = "ch1_1027_yuyi";
    LCDTarget.FileType fileType = LCDTarget.FileType.Logo;
//    LCDTarget.FileType fileType = LCDTarget.FileType.XLS;
    LCDTarget.Source source = LCDTarget.Source.CA210;

    LCDTarget lcdTarget = LCDTarget.Measured.measure(LCDTarget.Source.CA210,
        LCDTargetBase.Number.Ramp256W, false);

    Plot2D plot = Plot2D.getInstance("uv' diagram");
    Plot2D cctplot = Plot2D.getInstance("CCT");
    Plot2D dplot = Plot2D.getInstance("delta uv'");

    double[][] duvpArray = new double[256][];
    CIEXYZ whiteXYZ = lcdTarget.getWhitePatch().getXYZ();
    CIExyY whitexyY = new CIExyY(whiteXYZ);

    for (int x = 0; x < 256; x++) {
      Patch p = lcdTarget.getPatch(RGBBase.Channel.W, x, RGB.MaxValue.Double255);
      CIEXYZ XYZ = p.getXYZ();
      CIExyY xyY = new CIExyY(XYZ);
      double[] uvp = XYZ.getuvPrimeValues();
      double[] duvp = xyY.getDeltauvPrime(whitexyY);
      duvpArray[x] = duvp;
      plot.addCacheScatterLinePlot("measure", Color.green, uvp[0], uvp[1]);
      cctplot.addCacheScatterLinePlot("simulate CCT", Color.green, x,
                                      XYZ.getCCT());

      dplot.addCacheScatterLinePlot("acutal-du'", Color.red, x, duvp[0]);
      dplot.addCacheScatterLinePlot("acutal-dv'", Color.green, x, duvp[1]);
    }

    plot.setAxeLabel(0, "u'");
    plot.setAxeLabel(1, "v'");
    plot.drawCachePlot();
    plot.addLegend();
    plot.setVisible();

    cctplot.setAxeLabel(0, "code");
    cctplot.setAxeLabel(1, "CCT");
    cctplot.drawCachePlot();
    cctplot.addLegend();
    cctplot.setFixedBounds(0, 0, 255);
    cctplot.setVisible();

    dplot.setAxeLabel(0, "code");
    dplot.setAxeLabel(1, "delta");
    dplot.drawCachePlot();
    dplot.setFixedBounds(0, 0, 255);
    dplot.addLegend();
    dplot.setVisible();

//    System.out.println(LCDModelCalibrator.getDeltauvPrimeReport(duvpArray, 50));

//    LogoFileAdapter logo = new LogoFileAdapter("9300.logo");
//    List<CIEXYZ> XYZList = logo.getXYZList();
//    for (int x = 0; x < 256; x++) {
//      Patch p = lcdTarget.getPatch(RGBBase.Channel.W, x);
//      CIEXYZ targetXYZ = XYZList.get(x);
//      CIEXYZ XYZ = p.getXYZ();
//      DeltaE de = new DeltaE(targetXYZ, XYZ, whiteXYZ);
//      System.out.println(x + " " + de.getMeasuredDeltaE() + "/" + targetXYZ +
//                         " " + XYZ);
//    }
  }
}
