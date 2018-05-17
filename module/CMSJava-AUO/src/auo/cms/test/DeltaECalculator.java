package auo.cms.test;

import java.math.*;
import shu.cms.lcd.LCDTarget;
import shu.cms.devicemodel.lcd.ProfileColorSpaceModel;
import java.io.IOException;
import jxl.read.biff.BiffException;
import shu.cms.colorformat.adapter.LCDModelAdapter;
import java.util.List;
import shu.cms.lcd.LCDTargetBase;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.independ.*;
import shu.cms.Patch;
import shu.cms.colorformat.adapter.xls.AUOMeasureXLSAdapter;
import shu.cms.DeltaEReport;
import shu.io.files.ExcelFile;
import jxl.write.WriteException;
import shu.cms.plot.Plot2D;
import java.awt.Color;
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
public class DeltaECalculator {

  static LCDTarget getsRGBLCDTarget(double luminance, List<RGB> rgbList) {
    ProfileColorSpaceModel pcsm = new ProfileColorSpaceModel(RGB.ColorSpace.
        sRGB, luminance, new CIEXYZ());
    pcsm.produceFactor();

    LCDModelAdapter modelAdapter = new LCDModelAdapter(pcsm, rgbList);
    LCDTarget lcdTarget = LCDTarget.Instance.get(modelAdapter);

    return lcdTarget;
  }

  static List<RGB> getMeasureRGBList() {

    AUOMeasureXLSAdapter measureAdapter = null;
    try {
      measureAdapter = new AUOMeasureXLSAdapter(
          "ColorList_all(871).xls");
    }
    catch (BiffException ex) {
      ex.notifyAll();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    List<RGB> measureRGBList = measureAdapter.getRGBList();
    return measureRGBList;
  }

  static LCDTarget getMeasurementLCDTarget(String measurementFilename) {
    try {
      AUOMeasureXLSAdapter measureAdapter = new AUOMeasureXLSAdapter(
          measurementFilename);
      return LCDTarget.Instance.get(measureAdapter);
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  static DeltaEReport[] compareWithsRGB(String measurementFilename) {
    LCDTarget measurementLCDTarget = getMeasurementLCDTarget(
        measurementFilename);
    double luminance = measurementLCDTarget.getWhitePatch().getXYZ().Y;
    List<RGB> rgbList = measurementLCDTarget.filter.rgbList();
    LCDTarget sRGBLCDTarget = getsRGBLCDTarget(luminance, rgbList);
    return compare(measurementLCDTarget, sRGBLCDTarget);
  }

  static DeltaEReport[] compareWithEIZO(String measurementFilename) {
    LCDTarget measurementLCDTarget = getMeasurementLCDTarget(
        measurementFilename);
//    double luminance = measurementLCDTarget.getWhitePatch().getXYZ().Y;
//    List<RGB> rgbList = measurementLCDTarget.filter.rgbList();
    LCDTarget eizoLCDTarget = getMeasurementLCDTarget("EIZO-srgb.xls");

    return compare(measurementLCDTarget, eizoLCDTarget);
  }

  final static int Start = 7;
  final static int End = 14;
  static DeltaEReport[] compare(LCDTarget referenceTarget, LCDTarget target) {
    CIEXYZ referenceWhite = referenceTarget.getWhitePatch().getXYZ();
    List<Patch> referencePatchList = referenceTarget.
        getLabPatchList(referenceWhite);
    CIEXYZ targetWhite = target.getWhitePatch().getXYZ();
    List<Patch> targetPatchList = target.getLabPatchList(targetWhite);

    referencePatchList = referencePatchList.subList(Start, End);
    targetPatchList = targetPatchList.subList(Start, End);

    DeltaEReport[] reports = DeltaEReport.Instance.patchReport(
        referencePatchList, targetPatchList, false);
    return reports;
  }

  static void makesRGBTarget(double luminance) {
    List<RGB> rgbList = LCDTarget.Instance.getRGBList(LCDTarget.Number.WHQL);
    LCDTarget sRGBLCDTarget = getsRGBLCDTarget(luminance, rgbList);
    try {
      ExcelFile xls = new ExcelFile("srgb.xls", true);
      xls.setCell(0, 0, "Num");
      xls.setCell(1, 0, "R");
      xls.setCell(2, 0, "G");
      xls.setCell(3, 0, "B");
      xls.setCell(4, 0, "x");
      xls.setCell(5, 0, "y");
      xls.setCell(6, 0, "Y");

      int size = rgbList.size();
      for (int x = 0; x < size; x++) {
        int index = x + 1;
        xls.setCell(0, index, index);
        Patch p = sRGBLCDTarget.getPatch(x);
        RGB rgb = p.getRGB();
        CIExyY xyY = new CIExyY(p.getXYZ());
        xls.setCell(1, index, rgb.R);
        xls.setCell(2, index, rgb.G);
        xls.setCell(3, index, rgb.B);
        xls.setCell(4, index, xyY.x);
        xls.setCell(5, index, xyY.y);
        xls.setCell(6, index, xyY.Y);

      }

      xls.close();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
    catch (WriteException ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
//    LCDTarget measurementLCDTarget = getMeasurementLCDTarget(
//        "EIZO.xls");
//    double luminance = measurementLCDTarget.getWhitePatch().getXYZ().Y;
//    makesRGBTarget(luminance);

//    compareWithEIZO("DG+HueFinal.xls");
//    DeltaEReport.setOnlyCountMeasuredDeltaE(false);

//    DeltaEReport[] reports = compareWithsRGB("alloff.xls");
//    DeltaEReport[] reports = compareWithsRGB("DG.xls");
//    DeltaEReport[] reports = compareWithsRGB("hue.xls");
//    DeltaEReport[] reports = compareWithsRGB("hue+ost12.xls");
//    DeltaEReport[] reports = compareWithsRGB("CM(EIZO)+ost12.xls");
//    DeltaEReport[] reports = compareWithsRGB("CM(sRGB)+ost12.xls");
//    DeltaEReport[] reports = compareWithsRGB("EIZO-srgb.xls");

//    DeltaEReport[] reports = compareWithsRGB("DG+Hue+Ostt12.xls");
//    DeltaEReport[] reports = compareWithsRGB("DG+CM-EIZO+Ofs12.xls");
//    DeltaEReport[] reports = compareWithsRGB("DG+CM-sRGB+Ofs12.xls");


//    DeltaEReport[] reports = compareWithEIZO("alloff.xls");
//    DeltaEReport[] reports = compareWithEIZO("DG.xls");
//        DeltaEReport[] reports = compareWithEIZO("Hue.xls");
//    DeltaEReport[] reports = compareWithEIZO("Hue+ost12.xls");
//    DeltaEReport[] reports = compareWithEIZO("CM(EIZO)+ost12.xls");
//    DeltaEReport[] reports = compareWithEIZO("CM(sRGB)+ost12.xls");


//    DeltaEReport[] reports = compareWithEIZO("AllFuc-Off.xls");
//    DeltaEReport[] reports = compareWithEIZO("DG.xls");
//    DeltaEReport[] reports = compareWithEIZO("DG+Hue(6axis).xls");
//    DeltaEReport[] reports = compareWithEIZO("DG+Hue.xls");
//    DeltaEReport[] reports = compareWithEIZO("DG+Hue+Ostt12.xls");
//     DeltaEReport[] reports = compareWithEIZO("DG+CM-EIZO+Ofs12.xls");
//      DeltaEReport[] reports = compareWithEIZO("DG+CM-sRGB+Ofs12.xls");

    DeltaEReport.setOnlyCountMeasuredDeltaE(false);
//     DeltaEReport[] reports = compareWithsRGB("dell/dg off.xls");
//    DeltaEReport[] reports = compareWithsRGB("dell/dg on.xls");
    DeltaEReport[] reports = compareWithsRGB("秖代/dg off(46).xls");
//    DeltaEReport[] reports = compareWithsRGB("秖代/dg on(46).xls");
//    DeltaEReport[] reports = compareWithsRGB("秖代/Acer(46).xls");
//    DeltaEReport[] reports = compareWithsRGB("秖代/eizo like(46).xls");
//    DeltaEReport[] reports = compareWithsRGB("秖代/eizo like(weak gamma).xls");
//    DeltaEReport[] reports = compareWithsRGB("秖代/cm2.xls");
//    DeltaEReport[] reports = compareWithsRGB("秖代/sRGB(46).xls");

//    DeltaEReport[] reports = compareWithsRGB("dell/cm1.xls");

    for (DeltaEReport report : reports) {
      Plot2D plot = Plot2D.getInstance();
      System.out.println(report);
      System.out.println(report.getPatchDeltaEReport());
      DeltaEReport.PatchDeltaEReport preport = report.getPatchDeltaEReport();
      List<DeltaEReport.PatchDeltaE> list = preport.getPatchDeltaEList();

      for (DeltaEReport.PatchDeltaE pde : list) {
        Patch p0 = pde.getOriginalPatch();
        Patch p1 = pde.getModelPatch();
        CIELab Lab0 = p0.getLab();
        CIELab Lab1 = p1.getLab();
        plot.addCacheScatterPlot("AUO", Color.red, Lab0.a, Lab0.b);
        plot.addCacheScatterPlot("sRGB", Color.green, Lab1.a, Lab1.b);
//        System.out.println(Lab1.a + " " + Lab1.b);
        CIEXYZ XYZ = p1.getXYZ();
        CIExyY xyY = new CIExyY(XYZ);
        System.out.println(xyY.x + " " + xyY.y);
      }
      plot.addLegend();
//      plot.setVisible();
    }

  }
}
