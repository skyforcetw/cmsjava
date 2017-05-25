package shu.cms.camdc.test;

import java.awt.*;

import shu.cms.*;
import shu.cms.camdc.model.*;
import shu.cms.colorspace.depend.*;
import shu.cms.dc.ideal.*;
import shu.cms.plot.*;

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
public class WhitePointPlotter {
  public static void main(String[] args) {
    IdealDigitalCamera camera = CameraSensorEstimator.estimateHTCLegend(false, false);

//    Illuminant.getda
    Plot2D plot = Plot2D.getInstance();

    Illuminant[] illuminants = Illuminant.getDailyIlluminants();
    int index = 0;
    for (Illuminant i : illuminants) {
      Spectra s = i.getSpectra();
      double[] rgbValues = camera.captureOriginalOutput(s);
      RGB rgb = new RGB(rgbValues[0], rgbValues[1], rgbValues[2]);
      double[] rg = rgb.getRGrg();

      Color c = null;
      if (index < 31) {
        c = Color.black;
      }
      else if (index < 56) {
        c = Color.blue;
      }
      else {
        c = Color.green;
      }

      plot.addScatterPlot(s.getName(), c, rg[0], rg[1]);
      System.out.println(s.getName() + " " + rgb);
      index++;
    }

//    for (int nm = 400; nm <= 700; nm++) {
//      if (nm == 548 || nm == 497) {
//        continue;
//      }
//
//      double[] data = new double[301];
//      data[nm - 400] = 1;
//      Spectra s = new Spectra("", Spectra.SpectrumType.NO_ASSIGN, 400, 700, 1,
//                              data);
//      double[] rgbValues = camera.captureOriginalOutput(s);
//      RGB rgb = new RGB(rgbValues[0], rgbValues[1], rgbValues[2]);
//      double[] rg = rgb.getRGrg();
//
//      CIEXYZ XYZ = s.getXYZ();
//      CIExyY xyY = CIExyY.fromXYZ(XYZ);
//      xyY.Y = 1;
//
//      RGB rgb2 = RGB.fromXYZ(xyY.toXYZ(), RGB.ColorSpace.WideGamutRGB, true);
//      rgb2.rationalize();
//      Color c = rgb2.getColor();
//
////      plot.addCacheScatterLinePlot("locus", rg[0], rg[1]);
//      plot.addScatterPlot(Integer.toString(nm), c, rg[0], rg[1]);
//    }

    plot.setAxisLabels("r", "g");
    plot.setVisible();
  }
}
