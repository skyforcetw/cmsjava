package auo.cms.prefercolor.model.test;

import java.io.*;

import java.awt.image.*;

import auo.cms.prefercolor.model.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;
import shu.cms.profile.*;
import shu.image.*;
import shu.math.lut.*;

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
public class PreferredColorModelTester {
  public static void main(String[] args) {
    modelTest(args);
//    test(args);
  }

  public static void showModelInHSV(PreferredColorModel model) {
    Plot3D plot = Plot3D.getInstance();
    RGB.ColorSpace colorspace = RGB.ColorSpace.sRGB;
//    CIEXYZ white = colorspace.getReferenceWhiteXYZ();

    for (int h = 0; h < 360; h += 60) {
      double[][][] planeData = new double[11][11][];
      for (int s = 0; s <= 100; s += 10) {
        for (int v = 0; v <= 100; v += 10) {
          HSV hsv = new HSV(colorspace, new double[] {h, s, v});
          HSV hsv2 = model.getHSV(hsv);
//          HSV hsv2 = hsv;

          planeData[s / 10][v / 10] = new double[] {
              hsv2.S, hsv2.V, h};

        }
      }

      plot.addPlanePlot(Integer.toString(h), HSV.getLineColor(h), planeData);
    }

    plot.setFixedBounds(0, 0, 100);
    plot.setFixedBounds(1, 0, 100);
    plot.setFixedBounds(2, 0, 360);
    plot.setAxisLabels("S", "V", "H");
    plot.setVisible();
  }

  public static void showModelInLCh(PreferredColorModel model) {
    Plot3D plot = Plot3D.getInstance();
    RGB.ColorSpace colorspace = RGB.ColorSpace.sRGB;
    CIEXYZ white = colorspace.getReferenceWhiteXYZ();

    for (int h = 0; h < 360; h += 60) {
      double[][][] planeData = new double[11][11][];
      for (int s = 0; s <= 100; s += 10) {
        for (int v = 0; v <= 100; v += 10) {
          HSV hsv = new HSV(colorspace, new double[] {h, s, v});
          CIEXYZ XYZ = hsv.toRGB().toXYZ();
          CIELab Lab = new CIELab(XYZ, white);
          CIELCh LCh = new CIELCh(Lab);
          if (LCh.C != 0 /*&& s != 100*/) {
            CIELCh LChp = model.getLCh(LCh);
            CIELab Labp = new CIELab(LChp);

            CIEXYZ XYZp = CIELab.toXYZ(Labp, white);
            RGB rgbp = new RGB(colorspace, XYZp);
            HSV hsvp = new HSV(rgbp);
            double sp = hsvp.S;
            double vp = hsvp.V;
            sp = (sp > 100) ? 100 : sp;
            vp = (vp > 100) ? 100 : vp;
            planeData[s / 10][v / 10] = new double[] {
                sp, vp, h};
          }
          else {
            planeData[s / 10][v / 10] = new double[] {
                hsv.S, hsv.V, h};
          }
        }
      }

      plot.addPlanePlot(Integer.toString(h), HSV.getLineColor(h), planeData);
    }

    plot.setFixedBounds(0, 0, 100);
    plot.setFixedBounds(1, 0, 100);
    plot.setFixedBounds(2, 0, 360);
    plot.setAxisLabels("S", "V", "H");
    plot.setVisible();
  }

//  public final static PreferredColorModel getTestInstance(ProfileColorSpace
//      limitColorSpace) {
//    //==========================================================================
//    LCDTarget eizoTarget = LCDTarget.Instance.getFromAUORampXLS(
//        "psychophysics/eizo ramp.xls");
//    LCDTarget.Operator.gradationReverseFix(eizoTarget);
//    //==========================================================================
//
//    //==========================================================================
//    ExperimentAnalyzer analyzer = new ExperimentAnalyzer(eizoTarget,
//        "psychophysics/data");
//    analyzer.analyze();
//    MemoryColorInterface memoryColor = analyzer.getMemoryColorInterface();
//    //==========================================================================
//
//    GamutBoundaryDescriptor gbd = GamutBoundaryRGBDescriptor.getInstance(
//        GamutBoundaryRGBDescriptor.Style.D65Threshold, limitColorSpace);
//
//    PreferredColorModel preferredcolormodel = new PreferredColorModel(
//        MemoryColorPatches.Korean.getInstance(), memoryColor, gbd);
//
//    preferredcolormodel.setReferenceWhite(limitColorSpace.getReferenceWhite());
//    return preferredcolormodel;
//  }

  public static void modelTest(String[] args) {
//    LCDTarget eizoTarget = LCDTarget.Instance.getFromAUORampXLS(
//        "psychophysics/eizo ramp.xls");
//    LCDTarget.Operator.gradationReverseFix(eizoTarget);
//    ExperimentAnalyzer analyzer = new ExperimentAnalyzer(eizoTarget,
//        "psychophysics/data");
//    analyzer.analyze();
    ProfileColorSpace colorSpacePCS = ProfileColorSpace.Instance.get(RGB.
        ColorSpace.sRGB);
//    GamutBoundaryDescriptor gbd = GamutBoundaryRGBDescriptor.getInstance(
//        GamutBoundaryRGBDescriptor.Style.D65Threshold, colorSpacePCS);
//    MemoryColorInterface memoryColor = analyzer.getMemoryColorInterface();
//
//    PreferredColorModel preferredcolormodel = new PreferredColorModel(
//        memoryColor, gbd);
//
//    preferredcolormodel.setReferenceWhiteValues(colorSpacePCS.getReferenceWhite().
//                                                getValues());
    PreferredColorModel preferredcolormodel = PreferredColorModel.getInstance(
        colorSpacePCS);
//    showModelInLCh(preferredcolormodel);
    showModelInHSV(preferredcolormodel);
//    double[] result = preferredcolormodel.getRGBValues(new double[] {
//        205, 207, 206}, colorSpacePCS);
//    System.out.println(Arrays.toString(result));

//    TetrahedralInterpolation tetrahedralInterpolation = preferredcolormodel.
//        produceLChA2ATetrahedralInterpolation(colorSpacePCS, 51);
    TetrahedralInterpolation tetrahedralInterpolation2 = preferredcolormodel.
        produceHSVA2ATetrahedralInterpolation(colorSpacePCS, 51);
    System.out.println("start transform...");
    BufferedImage image = null;
    try {
//      image = ImageUtils.loadImage("71252_03.jpg");
//      image = ImageUtils.loadImage("7125_10.jpg");
      image = ImageUtils.loadImage("7125_08.jpg");

//      BufferedImage image2 = preferredcolormodel.processImage(image,
//          tetrahedralInterpolation);
//      ImageUtils.storeTIFFImage("test_LCh.tif", image2);

      BufferedImage image3 = preferredcolormodel.processImage(image,
          tetrahedralInterpolation2);
      ImageUtils.storeTIFFImage("test_HSV.tif", image3);

//      image2 = preferredcolormodel.processImage(image, colorSpacePCS);
//      ImageUtils.storeJPEGImage("test_.jpg", image2);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    System.out.println("done");

//    Plot2D plot = Plot2D.getInstance();
//    for (int x = 0; x < 101; x++) {
//      double normal = x / 100.;
//      double l = preferredcolormodel.lightnessLUT.getValue(normal);
//      plot.addCacheScatterLinePlot("lightness", normal, l);
//    }
//    plot.setVisible();

//    Plot2D plotHue = Plot2D.getInstance();
//    for (int x = 0; x < 360; x++) {
////      double normal = x / 100.;
////      double l = preferredcolormodel.lightnessLUT.getValue(normal);
////      plotHue.addCacheScatterLinePlot("lightness", normal, l);
//      double hue2 = preferredcolormodel.hueLUT.getValue(x);
//      plotHue.addCacheScatterLinePlot("hue", x, hue2);
//    }
//    plotHue.addLinePlot("linear", Color.red, 0, 0, 360, 360);
//    plotHue.setVisible();


//
//    System.out.println(new CIELCh(memoryColor.getSkin()));
//    MemoryColorInterface cieMemoryColor = MemoryColorPatches.
//        getOrientalInstance();
//    CIELab Lab = cieMemoryColor.getSkin();
//    CIELCh LCh = new CIELCh(Lab);
//    System.out.println(preferredcolormodel.getLCh(LCh));


  }

}
