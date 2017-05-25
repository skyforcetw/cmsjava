package auo.cms.hsv.saturation.backup;

import java.io.*;

import java.awt.image.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.image.*;
import shu.math.array.*;

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
public class SaturationAdjustor {

  public static void main(String[] args) throws IOException {
    String dirname =
        "D:\\ณnล้\\nobody zone\\Pattern\\skyforce Pattern Collect\\Saturation Evaluation Picture\\Duplicate";
    File dir = new File(dirname);

//    for (File file : dir.listFiles()) {

    File file = new File("rev-pattern.TIF");
//    File file = new File("sat_pattern.TIF");
//    File file = new File("lwf0001.jpg");

    String filename = file.getName();
    if (filename.indexOf(".jpg") == -1 && filename.indexOf(".tif") == -1) {
//        continue;
    }
    BufferedImage img = null;
    try {
      img = ImageUtils.loadImage(file.getAbsolutePath());
    }
    catch (IOException ex) {
      ex.printStackTrace();
      System.out.println(filename);
//        continue;
    }
    if (img == null) {
      System.out.println(filename);
//        continue;
    }
    int w = img.getWidth();
    int h = img.getHeight();
    int[] ipixel = new int[3];
    double[] dpixel = new double[3];
    WritableRaster raster = img.getRaster();
    CIEXYZ white = RGB.ColorSpace.sRGB.getReferenceWhiteXYZ();
    boolean halfProc = false;
    w = halfProc ? w / 2 : w;

    for (int x = 0; x < h; x++) {
      for (int y = 0; y < w; y++) {
        raster.getPixel(y, x, ipixel);
        RGB rgb = new RGB(RGB.ColorSpace.sRGB, ipixel);

        //====================================================================
        // HSL
        //====================================================================
//          HSL hsl = new HSL(rgb);
//          hsl.S = hsl.S * 1.2;
//          hsl.S = hsl.S > 100 ? 100 : hsl.S;
//          RGB rgb2 = hsl.toRGB();
        //====================================================================

        //====================================================================
        // HSV
        //====================================================================
        HSV hsv = new HSV(rgb);
        hsv.S = hsv.S * 1.5;
        hsv.S = hsv.S > 100 ? 100 : hsv.S;
        RGB rgb2 = hsv.toRGB();
        //====================================================================

        //====================================================================
        // Lab
        //====================================================================
//        CIEXYZ XYZ = rgb.toXYZ();
//        CIELab Lab = new CIELab(XYZ, white);
//        CIELCh LCh = new CIELCh(Lab);
//        LCh.C = LCh.C * 1.3;
//        Lab = new CIELab(LCh);
//        XYZ = Lab.toXYZ();
//        RGB rgb2 = new RGB(RGB.ColorSpace.sRGB, XYZ);
        //====================================================================

        rgb2.clip();
        rgb2.getValues(dpixel, RGB.MaxValue.Double255);
        DoubleArray.toIntArray(dpixel, ipixel);
        raster.setPixel(y, x, ipixel);
//        rgb2.getvas
      }

    }

    String name = file.getName();
    System.out.println(name);
//      String tiffilename = "sat_adj.tif";
    ImageUtils.storeTIFFImage("hsv/" + name, img);

//    } // loop of dir

  }
}
