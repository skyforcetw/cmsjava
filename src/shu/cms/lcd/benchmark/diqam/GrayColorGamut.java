package shu.cms.lcd.benchmark.diqam;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.cms.util.*;
//import shu.plot.*;

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
public class GrayColorGamut
    extends DIQAM {
  public GrayColorGamut(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  public GrayColorGamut(String title, LCDTarget lcdTarget) {
    super(title, lcdTarget);
  }

  private double[] gamutIndexArray;

  public void report() {
    CIExyY[] primaryxyY = GamutUtil.getPrimaryxyY(lcdTarget);

    List<Patch> rPatchList = lcdTarget.filter.oneValueChannel(RGB.Channel.R);
    List<Patch> gPatchList = lcdTarget.filter.oneValueChannel(RGB.Channel.G);
    List<Patch> bPatchList = lcdTarget.filter.oneValueChannel(RGB.Channel.B);
    int size = rPatchList.size();
    gamutIndexArray = new double[size];

    for (int x = size - 1; x >= 0; x--) {
      CIExyY rxyY = new CIExyY(rPatchList.get(x).getXYZ());
      CIExyY gxyY = new CIExyY(gPatchList.get(x).getXYZ());
      CIExyY bxyY = new CIExyY(bPatchList.get(x).getXYZ());
      CIExyY[] grayxyY = new CIExyY[] {
          rxyY, gxyY, bxyY};
      double p = GamutUtil.getGamutPercent(grayxyY, primaryxyY) * 100;
      gamutIndexArray[x] = p;
    }
  }

  public static void main(String[] args) {
    LCDTarget target = LCDTarget.Instance.getFromAUOXLS(
        "D:\\My Documents\\工作\\華山計畫\\LG 42SL90QD\\Standard\\2223.xls");
//        "D:\\My Documents\\工作\\華山計畫\\LG 42SL90QD\\Standard\\2223-standard gamut.xls");
    GrayColorGamut report = new GrayColorGamut(target);
    report.report();
    report.getPlot();
  }

  protected String getPlotName() {
    return "GrayColorGamut";
  }

  public Plot2D getPlot() {
    plot2D = _getPlot();
    plot2D.addLinePlot(title, 1, 255, gamutIndexArray);
    plot2D.setAxeLabel(0, "Gray");
    plot2D.setAxeLabel(1, "Gamut Ratio");
    plot2D.setFixedBounds(0, 0, 255);
    plot2D.setFixedBounds(1, 0, 100);
    plot2D.setVisible();
    return plot2D;
  }
}
