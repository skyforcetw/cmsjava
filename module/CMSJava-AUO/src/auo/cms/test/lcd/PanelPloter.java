package auo.cms.test.lcd;

import shu.cms.lcd.LCDTarget;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.independ.*;
import java.util.List;
import shu.cms.lcd.LCDTargetBase;
import java.io.IOException;
import jxl.read.biff.BiffException;
import shu.cms.colorformat.adapter.xls.AUOMeasureXLSAdapter;
import shu.cms.devicemodel.lcd.MultiMatrixModel;
import shu.cms.plot.Plot2D;
import shu.cms.colorformat.adapter.xls.AUORampXLSAdapter;
import shu.cms.plot.LocusPlot;
import java.io.FileNotFoundException;

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
public class PanelPloter {
  public static void main(String[] args) throws FileNotFoundException {
    LCDTarget target = null;

    AUORampXLSAdapter measureAdapter = new AUORampXLSAdapter(
        "dell/ramp.xls");
    target = LCDTarget.Instance.get(measureAdapter);
    LCDTarget.Operator.gradationReverseFix(target);

    MultiMatrixModel model = new MultiMatrixModel(target);
    model.produceFactor();
    CIEXYZ whiteXYZ = model.getWhiteXYZ(false);
    Plot2D plot = Plot2D.getInstance();
    Plot2D plot2 = Plot2D.getInstance();

    for (RGB.Channel ch : RGB.Channel.RGBChannel) {
      RGB rgb = new RGB(0, 0, 0);
      for (int x = 0; x < 255; x++) {
        rgb.setColorBlack();
        rgb.setValue(ch, x);
        CIEXYZ XYZ = model.getXYZ(rgb, false);
        double[] xyValues = XYZ.getxyValues();
        plot.addCacheScatterLinePlot(ch.name(), ch.color, xyValues[0],
                                     xyValues[1]);

        CIELab Lab = new CIELab(XYZ, whiteXYZ);
        plot2.addCacheScatterLinePlot(ch.name(), ch.color, Lab.a, Lab.b);
      }
    }

    LocusPlot locus = new LocusPlot(plot);
    locus.drawCIExyLocus(false);
    plot.setVisible();

//    LocusPlot locus2 = new LocusPlot(plot2);
//    locus2.drawCIELabLocus();
    plot2.setVisible();
    plot2.setAxeLabel(0, "a*");
    plot2.setAxeLabel(1, "b*");
  }

}
