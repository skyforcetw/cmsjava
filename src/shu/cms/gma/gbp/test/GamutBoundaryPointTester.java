package shu.cms.gma.gbp.test;

import shu.cms.gma.gbp.GamutBoundaryPoint;
import shu.cms.profile.ProfileColorSpace;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.Plot2D;
import shu.cms.gma.gbp.RGBSurface;
import shu.cms.gma.gbp.CIECAM02Vendor;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.cms.gma.gbp.IPTLChVendor;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GamutBoundaryPointTester {
  public static void main(String[] args) {
//    example1(args);
//    example2(args);
//    sRGBExample(args);
    boundaryExample(args);
  }

  public static void boundaryExample(String[] args) {
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGB.ColorSpace.sRGB);
//    double[] XYZValues = pcs.toCIEXYZValues(new double[] {1, 1, 1});
    CIEXYZ white = pcs.getReferenceWhite();
    GamutBoundaryPoint gbp = new GamutBoundaryPoint(pcs);
    RGBSurface boundary = new RGBSurface(gbp);
    ViewingConditions vc = ViewingConditions.getTypicalViewingConditions(white);
    CIECAM02Vendor vendor = new CIECAM02Vendor(pcs, vc);
//    IPTLChVendor vendor = new IPTLChVendor(pcs);
    boundary.setLChVendor(vendor);
    gbp.setBoundary(boundary);
    gbp.calculateGamut();
    System.out.println(gbp.getVolume());
  }

  public static void sRGBExample(String[] args) {
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGB.ColorSpace.sRGB);
    GamutBoundaryPoint gbp = new GamutBoundaryPoint(pcs);

    long start = System.currentTimeMillis();
//    gbp.calculateGamutByRGB();
//    System.out.println(gbp.getVolumn());
//    System.out.println(System.currentTimeMillis() - start);

//    start = System.currentTimeMillis();
//    gbp.type = Type.RGBSurface;
//    gbp.lchType = LChType.IPTLCh;
    gbp.calculateGamut();
    System.out.println(gbp.getVolume());
    System.out.println(System.currentTimeMillis() - start);
//    System.out.println(Arrays.toString(gbp.getNearestLightness(11)));
  }

  public static void example2(String[] args) {
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGB.ColorSpace.
        sRGB);
    GamutBoundaryPoint gbp = new GamutBoundaryPoint(pcs);
    gbp.calculateGamut();
    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);
    try {
      /*
             for (double l = 88; l <= 88; l += 1) {
        double[] d = gbp.getLightnessHCArray(l);
        plot.addLinePlot(null, 0, 360, d);
        plot.setTitle(String.valueOf(l));
        Thread.sleep(500);
//        plot.removeAllPlots();
             }*/

      int HLevel = gbp.HLevel;
      int LLevel = gbp.LLevel;
      double[][] rgbLabBoundary = gbp.getRGBLabBoundary();

      // for (int l = 90; l <= LLevel; l += 2) {
      for (int l = 50; l <= 50; l += 2) {
        plot.setTitle(String.valueOf(l));

        for (int h = 0; h < HLevel; h++) {
//        for (int h = 0; h < 45; h++) {
          int index = l + h * LLevel;
          double[] rgbLab = rgbLabBoundary[index];
          plot.addScatterPlot(null, new double[][] { {rgbLab[4]}, {rgbLab[5]}
          });
          plot.setFixedBounds(0, -128, 127);
          plot.setFixedBounds(1, -128, 127);
        }
        Thread.sleep(5000);
        //    plot.removeAllPlots();
      }
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }

  }

  public static void example1(String[] args) {
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(RGB.ColorSpace.
        sRGB);
    GamutBoundaryPoint gbp = new GamutBoundaryPoint(pcs);
    gbp.calculateGamut();
    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);
    try {
      for (double l = 45; l <= 90; l += 1) {
        double[] d = gbp.getLightnessHCArray(l);
        plot.addLinePlot(null, 0, 360, d);
        plot.setTitle(String.valueOf(l));
        Thread.sleep(500);
//        plot.removeAllPlots();
      }

      /*int HLevel = gbp.HLevel;
             int LLevel = gbp.LLevel;
             double[][] rgbLabBoundary = gbp.getRGBLabBoundary();

             for (int l = 90; l <= LLevel; l += 2) {
        plot.setTitle(String.valueOf(l));

        for (int h = 0; h < HLevel; h++) {
          int index = l + h * LLevel;
          double[] rgbLab = rgbLabBoundary[index];
          plot.addScatterPlot(null, new double[][] { {rgbLab[4]}, {rgbLab[5]}
          });
          plot.setFixedBounds(0, -128, 127);
          plot.setFixedBounds(1, -128, 127);
        }
        Thread.sleep(5000);
        plot.removeAllPlots();
             }*/
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }

  }
}
