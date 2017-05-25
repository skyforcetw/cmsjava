/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auo.cms.hsv.experiment;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.depend.DeviceDependentSpace.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.cms.profile.*;

//import shu.plot.*;

/**
 *
 * @author SkyforceShen
 */
public class PanelComparator {

  public static void main(String[] args) {
    LCDTarget target45 = LCDTarget.Instance.getFromAUORampXLS(
        "sRGB Adjust Evaluation/45% DG On.xls");
    LCDTarget target60 = LCDTarget.Instance.getFromAUORampXLS(
        "sRGB Adjust Evaluation/60% DG On.xls", LCDTarget.Number.Ramp256_6Bit);

    LCDTarget.Operator.gradationReverseFix(target45);
    LCDTarget.Operator.gradationReverseFix(target60);

    Plot2D gammaplot = Plot2D.getInstance();
//        List<Patch> patchList45 = target45.getPatchList();
//        List<Patch> patchList60 = target60.getPatchList();

    for (int x = 0; x < 64; x++) {
      double v = x / 63.;
      double v2 = Math.pow(v, 2.2);
      gammaplot.addCacheScatterLinePlot("r22", Color.pink, x * 4, v2);
    }

    double target45Y = target45.getLuminance().Y;
    for (int x = 1; x < 256; x++) {
      RGB r = new RGB(x, 0, 0);
      RGB g = new RGB(0, x, 0);
      RGB b = new RGB(0, 0, x);
      Patch rpatch = target45.getPatch(r);
      Patch gpatch = target45.getPatch(g);
      Patch bpatch = target45.getPatch(b);
      gammaplot.addCacheScatterLinePlot("r45", Color.red, x,
                                        rpatch.getXYZ().Y / target45Y);
      gammaplot.addCacheScatterLinePlot("g45", Color.green, x,
                                        gpatch.getXYZ().Y / target45Y);
      gammaplot.addCacheScatterLinePlot("b45", Color.blue, x,
                                        bpatch.getXYZ().Y / target45Y);
    }

    double target60Y = target60.getLuminance().Y;
    for (int x = 1; x < 64; x++) {
      RGB r = new RGB(x * 4, 0, 0);
      RGB g = new RGB(0, x * 4, 0);
      RGB b = new RGB(0, 0, x * 4);
      r.changeMaxValue(MaxValue.Int6Bit);
      g.changeMaxValue(MaxValue.Int6Bit);
      b.changeMaxValue(MaxValue.Int6Bit);
      Patch rpatch = target60.getPatch(r);
      Patch gpatch = target60.getPatch(g);
      Patch bpatch = target60.getPatch(b);
      gammaplot.addCacheScatterLinePlot("r60", Color.black, x * 4,
                                        rpatch.getXYZ().Y / target60Y);
      gammaplot.addCacheScatterLinePlot("g60", Color.black, x * 4,
                                        gpatch.getXYZ().Y / target60Y);
      gammaplot.addCacheScatterLinePlot("b60", Color.black, x * 4,
                                        bpatch.getXYZ().Y / target60Y);
    }
    gammaplot.addLegend();
    gammaplot.setVisible();

    MultiMatrixModel model45 = new MultiMatrixModel(target45);
    model45.produceFactor();

    MultiMatrixModel model60 = new MultiMatrixModel(target60);
    model60.produceFactor();

    ProfileColorSpace pcs45 = ProfileColorSpace.Instance.get(model45, "");
    ProfileColorSpace pcs60 = ProfileColorSpace.Instance.get(model60, "");

    Gamut3DPlot plot = new Gamut3DPlot("", 800, 800);
    plot.drawCIExyYGamut(pcs45);

    plot.setFillShape(false);
    plot.setPolygonDrawLine(true);
    plot.setPaintRGBColor(false);
    plot.drawCIExyYGamut(pcs60);
//        plot.setVisible();
  }
}
