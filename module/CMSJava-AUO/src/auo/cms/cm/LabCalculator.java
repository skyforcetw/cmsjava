package auo.cms.cm;

import shu.cms.lcd.LCDTarget;
import shu.cms.devicemodel.lcd.MultiMatrixModel;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.*;
import shu.cms.*;
import shu.math.regress.*;
import shu.math.array.DoubleArray;
import shu.math.array.*;
import shu.cms.devicemodel.lcd.thread.GOGModelThread;
import shu.cms.devicemodel.lcd.LCDModel;
import shu.cms.devicemodel.lcd.thread.RegularGOGModelThread;
import shu.cms.devicemodel.lcd.PLCCModel;
import flanagan.math.Minimisation;
import java.util.*;
import flanagan.math.MinimisationFunction;
import shu.cms.plot.Plot2D;
import java.awt.Color;
import shu.cms.plot.LocusPlot;
import shu.math.geometry.Geometry;
import javax.vecmath.*;
import shu.cms.devicemodel.lcd.ProfileColorSpaceModel;
import shu.cms.colorformat.adapter.LCDModelAdapter;
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
public class LabCalculator {
  public static void main(String[] args) {
    String[] filenames = new String[] {
        "dell/cm1.xls", "dell/cm3.xls", "sRGB"};
    Color[] colors = new Color[] {
        Color.red, Color.green, Color.black};
    Plot2D plot = Plot2D.getInstance();
//    CIEXYZ sRGBWhiteXYZ = RGB.ColorSpace.sRGB.referenceWhite.getNormalizeXYZ();
    Point2d center = new Point2d(0, 0);
//    LCDTarget.Instance.get()

    List<RGB> whqlRGBList = LCDTarget.Instance.getRGBList(LCDTarget.Number.WHQL);
    ProfileColorSpaceModel sRGBModel = new ProfileColorSpaceModel(RGB.
        ColorSpace.sRGB);
    sRGBModel.produceFactor();
    LCDModelAdapter sRGBAdapter = new LCDModelAdapter(sRGBModel, whqlRGBList);

    for (int x = 0; x < filenames.length; x++) {
      String filename = filenames[x];
      Color color = colors[x];

      LCDTarget target = filename.equals("sRGB") ?
          LCDTarget.Instance.get(sRGBAdapter) :
          LCDTarget.Instance.getFromAUOXLS(filename);

      List<Patch> patchList = target.getPatchList();
      for (int y = 7; y < 14; y++) {
        Patch p = patchList.get(y);
        RGB rgb = p.getRGB();
        CIELab Lab = p.getLab();
//        System.out.println(rgb + " " + Lab);

        plot.addCacheScatterPlot(filename, color, Lab.a, Lab.b);
//        CIEXYZ sRGBXYZ = rgb.toXYZ(RGB.ColorSpace.sRGB);
//        CIELab sRGBLab = new CIELab(sRGBXYZ, sRGBWhiteXYZ);
//        plot.addCacheScatterPlot("sRGB", Color.black, sRGBLab.a, sRGBLab.b);
      }
      double area = 0;
      for (int m = 7; m < 13; m++) {
        Patch p0 = patchList.get(m);
        Patch p1 = patchList.get(m + 1);
        CIELab Lab0 = p0.getLab();
        CIELab Lab1 = p1.getLab();
        Point2d p20 = new Point2d(Lab0.a, Lab0.b);
        Point2d p21 = new Point2d(Lab1.a, Lab1.b);
        double a = Geometry.getDistance(p20, center);
        double b = Geometry.getDistance(p21, center);
        double c = Geometry.getDistance(p20, p21);
        double partArea = heronFormula(a, b, c);
        area += partArea;
      }
      {
        Patch p0 = patchList.get(7);
        Patch p1 = patchList.get(12);
        CIELab Lab0 = p0.getLab();
        CIELab Lab1 = p1.getLab();
        Point2d p20 = new Point2d(Lab0.a, Lab0.b);
        Point2d p21 = new Point2d(Lab1.a, Lab1.b);
        double a = Geometry.getDistance(p20, center);
        double b = Geometry.getDistance(p21, center);
        double c = Geometry.getDistance(p20, p21);
        double partArea = heronFormula(a, b, c);
        area += partArea;
      }
      System.out.println(filename);
      System.out.println("area " + area);
    }
    plot.addLegend();
    plot.setVisible();
//    LocusPlot locusplot = new LocusPlot(plot);
//    locusplot.drawCIELabLocus();
  }

  private static double heronFormula(double a, double b, double c) {
    double p = (a + b + c) / 2;
    double S = Math.sqrt(p * (p - a) * (p - b) * (p - c));
    return S;

  }
}
