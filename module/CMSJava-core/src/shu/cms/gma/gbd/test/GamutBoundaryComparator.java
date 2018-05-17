package shu.cms.gma.gbd.test;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.gma.*;
import shu.cms.gma.gbd.*;
import shu.cms.gma.gbp.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
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
public class GamutBoundaryComparator {
  public static void surfaceTest(String[] args) {
//    Plot3D plot = Plot3D.getInstance();
//    //2 利用ramp加上XYZ內插產生boundary
//    String surfaceFilename =
//        "../module/CMSJava-AUO/workdir/prefered/EIZO S2031W/surface.xls";
//    LCDTarget surface = LCDTarget.Instance.getFromAUOXLS(surfaceFilename);
//    for (Patch p : surface.getPatchList()) {
//      RGB rgb = p.getRGB();
//      plot.addColorSpace("", rgb.getColor(), rgb);
//    }
//    plot.setVisible();

    for (RGB rgb : LCDTargetBase.SurfaceTarget.getSurface(17)) {
      System.out.println(rgb);
    }
  }

  public static void main(String[] args) {
    compare(args);
//    surfaceTest(args);
  }

  public static void compare(String[] args) {
    //1 利用surface加上gamut 內插產生出boundary
    //2 利用ramp加上XYZ內插產生boundary
    String surfaceFilename =
        "../module/CMSJava-AUO/workdir/prefered/EIZO S2031W/surface.xls";
    String rampFilename =
        "../module/CMSJava-AUO/workdir/prefered/EIZO S2031W/ramp.xls";
//    LCDTarget surface = LCDTarget.Instance.getFromAUOXLS(surfaceFilename);

    RGB.ColorSpace colorspace = RGB.ColorSpace.sRGB;

    ProfileColorSpaceModel pcsmodel = new ProfileColorSpaceModel(colorspace);
    pcsmodel.produceFactor();
    LCDTarget surface = LCDTarget.Instance.get(new LCDModelAdapter(pcsmodel,
        LCDTarget.Number.Surface1352), LCDTarget.Number.Surface1352);

    LCDTarget ramp = LCDTarget.Instance.getFromAUOXLS(rampFilename);

    LCDTarget.Operator.gradationReverseFix(ramp);
    LCDTarget.Operator.setNumber(ramp, LCDTarget.Number.Ramp1024);

//    ProfileColorSpace surfacePCS = ProfileColorSpace.Instance.get(surface,
//        "surface");
    ProfileColorSpace surfacePCS = ProfileColorSpace.Instance.get(colorspace);
    MultiMatrixModel model = new MultiMatrixModel(ramp);
    model.produceFactor();
//    ProfileColorSpace rampPCS = ProfileColorSpace.Instance.get(model, "ramp");

    //==========================================================================
    // surface
    //==========================================================================

    GamutBoundaryPoint gbp = new GamutBoundaryPoint(surfacePCS);
    gbp.calculateGamut();

    GamutBoundary3DDescriptor gbdSurface = new GamutBoundary3DDescriptor(gbp,
        FocalPoint.FocalType.None);
    //==========================================================================

    //==========================================================================
    // ramp
    //==========================================================================
    GamutBoundaryRGBDescriptor gbdRGB = GamutBoundaryRGBDescriptor.getInstance(
//        GamutBoundaryRGBDescriptor.Style.D65Threshold, rampPCS);
        GamutBoundaryRGBDescriptor.Style.D65Threshold, surfacePCS);
    //==========================================================================




    Plot2D plot = Plot2D.getInstance();
    Plot3D plot3 = Plot3D.getInstance();

    int index = 0;
    for (Patch p : surface.getPatchList()) {
//      RGB rgb = p.getRGB();
      if (index == 496) {
        int x = 1;
      }
      CIELab Lab = p.getLab();
//      CIELab Lab = new
//      CIELab Lab = new CIELab(RGB.toXYZ(rgb, RGB.ColorSpace.sRGB),
//                              RGB.ColorSpace.sRGB.getReferenceWhiteXYZ());
      CIELCh realBoundary = new CIELCh(Lab);
      realBoundary.L = realBoundary.L > 100 ? 100 : realBoundary.L;
      CIELCh testLCh = (CIELCh) realBoundary.clone();
      testLCh.C = .1;
      CIELCh surfaceBoundary = gbdSurface.getBoundaryLCh(testLCh);
      CIELCh rampBoundary = gbdRGB.getBoundaryLCh(testLCh);
//      System.out.println(realBoundary.C + " " + surfaceBoundary.C + " " +
//                         rampBoundary.C);
//      plot.addCacheScatterPlot("real", Color.red, index, realBoundary.C);
//      double deltaSurface = surfaceBoundary.C - realBoundary.C;
      double deltaRamp = Math.abs(rampBoundary.C - realBoundary.C);
      if (deltaRamp > 1) {
        int a = 1;
        System.out.println(p.getRGB());
        System.out.println(realBoundary + " " + rampBoundary);
        plot3.addColorSpace("", Color.blue, realBoundary);
        plot3.addColorSpace("", Color.red, rampBoundary);
      }
      else {
        plot3.addColorSpace("", p.getRGB().getColor(), realBoundary);
      }
//      plot.addCacheScatterPlot("surface", Color.green, index, deltaSurface);
      plot.addCacheScatterPlot("ramp", Color.blue, index, deltaRamp);
      index++;
    }
    plot.setVisible();
    plot3.setVisible();
//    for(RGB rgb:
  }
}
