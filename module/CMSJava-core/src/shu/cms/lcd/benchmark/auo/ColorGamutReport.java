package shu.cms.lcd.benchmark.auo;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.depend.RGBBase.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.cms.util.*;

///import shu.plot.*;

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
public class ColorGamutReport
    extends Benchmark {
  public ColorGamutReport(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  private final static CIExyY[] getRGBxyY(RGB.ColorSpace colorspace) {
    CIExyY rxyY = new CIExyY(RGB.toXYZ(RGB.Red, colorspace));
    CIExyY gxyY = new CIExyY(RGB.toXYZ(RGB.Green, colorspace));
    CIExyY bxyY = new CIExyY(RGB.toXYZ(RGB.Blue, colorspace));
    return new CIExyY[] {
        rxyY, gxyY, bxyY};
  }

  private String gamutName;

  /**
   * report
   *
   * @return String
   */
  public String report() {

    rgbxyY = GamutUtil.getPrimaryxyY(lcdTarget);
    basedxyYArray = getRGBxyY(basedColorSpace);
    double gamutPercent = GamutUtil.getGamutPercent(rgbxyY, basedxyYArray);

    StringBuilder report = new StringBuilder();
    report.append("R: " + rgbxyY[0].x + ", " + rgbxyY[0].y + "\n");
    report.append("G: " + rgbxyY[1].x + ", " + rgbxyY[1].y + "\n");
    report.append("B: " + rgbxyY[2].x + ", " + rgbxyY[2].y + "\n");
    report.append("GamutPercent: " + gamutPercent + "\n");

    return report.toString();
  }

  private RGB.ColorSpace basedColorSpace = RGB.ColorSpace.NTSCRGB;
  private CIExyY[] rgbxyY;
  private CIExyY[] basedxyYArray;

  public Plot2D getPlot() {
    locusPlot = new LocusPlot();

    locusPlot.drawCIExyLocus(false);
    locusPlot.drawGamutTriangle(gamutName, rgbxyY[0], rgbxyY[1], rgbxyY[2],
                                Color.red,
                                LocusPlot.xyTrasnfer);
    locusPlot.drawGamutTriangle("NTSC", basedxyYArray[0], basedxyYArray[1],
                                basedxyYArray[2],
                                Color.green, LocusPlot.xyTrasnfer);
    plot2D = locusPlot.getPlot2D();
    plot2D.addLegend();
    locusPlot.setVisible();
    return plot2D;
  }

  private Plot2D plot2D;
  private LocusPlot locusPlot;

  public static void main(String[] args) throws Exception {
    LCDTarget lcdTarget = LCDTarget.Instance.getFromAUOXLS(
        "D:\\My Documents\\工作\\華山計畫\\LG 42SL90QD\\Standard\\2223.xls");

    ColorGamutReport report = new ColorGamutReport(lcdTarget);
    report.setGamutName("Wide");
    System.out.println(report.report());
    Plot2D plot = report.getPlot();
    LocusPlot locusPlot = report.getLocusPlot();

//    LCDTarget stdTarget = LCDTarget.Instance.getFromAUOXLS(
//        "D:\\My Documents\\工作\\華山計畫\\LG 42SL90QD\\Standard\\2223-standard gamut.xls");
//    CIExyY[] stdrgbxyY = GamutUtil.getPrimaryxyY(stdTarget);
//    locusPlot.drawGamutTriangle("Standard", stdrgbxyY[0], stdrgbxyY[1],
//                                stdrgbxyY[2], Color.blue, LocusPlot.xyTrasnfer);

  }

  public void setGamutName(String gamutName) {
    this.gamutName = gamutName;
  }

  public void setBasedColorSpace(ColorSpace basedColorSpace) {
    this.basedColorSpace = basedColorSpace;
  }

  public LocusPlot getLocusPlot() {
    return locusPlot;
  }
}
