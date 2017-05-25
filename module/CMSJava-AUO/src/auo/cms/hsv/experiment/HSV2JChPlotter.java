package auo.cms.hsv.experiment;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.cms.plot.*;
import shu.math.array.*;
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
public class HSV2JChPlotter {
  public static void main(String[] args) {
    hue(args);
//    saturationforJSC(args);
//    saturationforL(args);
//    valueforJSC(args);

  }

  public static void hue(String[] args) {

    CIECAM02 cam02 = new CIECAM02(ViewingConditions.
                                  sRGBReferenceViewingConditions);
    CIEXYZ sRGBWhite = RGB.ColorSpace.sRGB.getReferenceWhiteXYZ();
    double[] jchMinHueArray = DoubleArray.fill(24, Double.MAX_VALUE);
    double[] jchMaxHueArray = new double[24];

    Plot2D plot = Plot2D.getInstance();

    for (int v = 25; v <= 100; v += 25) {
      for (int s = 25; s <= 100; s += 25) {
        double preHue = -1;
        for (int h = 0; h < 360; h += 15) {
          HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h, s, v});
          RGB rgb = hsv.toRGB();
          CIEXYZ XYZ = rgb.toXYZ();
          CIECAM02Color jch = cam02.forward(XYZ);
//          CIELab Lab = new CIELab(XYZ, sRGBWhite);

          int hueIndex = h / 15;
          double jchHue = jch.h;
          jchHue = jchHue < preHue ? jchHue + 360 : jchHue;
          jchMinHueArray[hueIndex] = jchHue < jchMinHueArray[hueIndex] ? jchHue :
              jchMinHueArray[hueIndex];
          jchMaxHueArray[hueIndex] = jchHue > jchMaxHueArray[hueIndex] ? jchHue :
              jchMaxHueArray[hueIndex];

          System.out.println(hsv.H + " " + jchHue);
          plot.addCacheScatterLinePlot(String.valueOf(s) + "+" +
                                       String.valueOf(v), hsv.H,
                                       jchHue);
          preHue = jchHue;
        }
      }
    }
    plot.setAxeLabel(0, "H of HSV");
    plot.setAxeLabel(1, "h of JCh");
    plot.addLinePlot("", 0, 0, 360, 360);
    plot.addLegend();
    plot.setVisible();

    Plot2D plot3 = Plot2D.getInstance();
    double[] minus1 = DoubleArray.minus(jchMaxHueArray, jchMinHueArray);
    plot3.addLinePlot("JCh", 0, 345, minus1);
    plot3.setAxeLabel(0, "H of HSV");
    plot3.setAxeLabel(1, "deltaH");
    plot3.addLegend();
    plot3.setVisible();
  }

  public static void saturationforJSC(String[] args) {

    CIECAM02 cam02 = new CIECAM02(ViewingConditions.
                                  sRGBReferenceViewingConditions);

//    double v = 50;
//    for (int v : new int[] {25, 50, 75}) {
    for (int v : new int[] {50}) {
      Plot2D plotc = Plot2D.getInstance();
      Plot2D plots = Plot2D.getInstance();
      Plot2D plotj = Plot2D.getInstance();
      for (int h = 0; h < 360; h += 15) {
        HSV basehsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h, 100, 100});
        Color c = basehsv.toRGB().getColor();
        for (int s = 10; s <= 100; s += 10) {
          HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h, s, v});
          RGB rgb = hsv.toRGB();
          CIEXYZ XYZ = rgb.toXYZ();
          CIECAM02Color jch = cam02.forward(XYZ);

          plotc.addCacheScatterLinePlot(String.valueOf(h), c, hsv.S, jch.C);
          plots.addCacheScatterLinePlot(String.valueOf(h), c, hsv.S, jch.s);
          plotj.addCacheScatterLinePlot(String.valueOf(h), c, hsv.S, jch.J);
        }
      }

      plotc.setAxeLabel(0, "S of HSV");
      plotc.setAxeLabel(1, "C of JCh");
      plotc.setVisible();
      plotc.setFixedBounds(1, 0, 100);
      plotc.setTitle("v(" + String.valueOf(v) + ") @C");

      plots.setAxeLabel(0, "S of HSV");
      plots.setAxeLabel(1, "S of JCh");
      plots.setVisible();
      plots.setFixedBounds(1, 0, 100);
      plots.setTitle("v(" + String.valueOf(v) + ") @S");

      plotj.setAxeLabel(0, "S of HSV");
      plotj.setAxeLabel(1, "J of JCh");
      plotj.setVisible();
      plotj.setFixedBounds(1, 0, 100);
      plotj.setTitle("v(" + String.valueOf(v) + ") @J");

    }

  }

  public static void saturationforL(String[] args) {

    CIECAM02 cam02 = new CIECAM02(ViewingConditions.
                                  sRGBReferenceViewingConditions);

    for (int v : new int[] {25, 50, 75}) {
      Plot2D plotc = Plot2D.getInstance();
//      Plot2D plots = Plot2D.getInstance();
      for (int h = 0; h < 360; h += 15) {
        HSV basehsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h, 100, 100});
        Color c = basehsv.toRGB().getColor();
        for (int s = 10; s <= 100; s += 10) {
          HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h, s, v});
          RGB rgb = hsv.toRGB();
          CIEXYZ XYZ = rgb.toXYZ();
          CIECAM02Color jch = cam02.forward(XYZ);

          plotc.addCacheScatterLinePlot(String.valueOf(h), c, hsv.S, jch.J);
//          plots.addCacheScatterLinePlot(String.valueOf(h), c, hsv.S, jch.J);
        }
      }

      plotc.setAxeLabel(0, "S of HSV");
      plotc.setAxeLabel(1, "J of JCh");
//      plotc.addLegend();
      plotc.setVisible();
      plotc.setFixedBounds(1, 0, 100);
      plotc.setTitle("v(" + String.valueOf(v) + ") @J");

//      plots.setAxeLabel(0, "S of HSV");
//      plots.setAxeLabel(1, "S of JCh");
//      plots.setVisible();
//      plots.setFixedBounds(1, 0, 100);
//      plots.setTitle("v(" + String.valueOf(v) + ") @S");
    }

  }

  public static void valueforJSC(String[] args) {

    CIECAM02 cam02 = new CIECAM02(ViewingConditions.
                                  sRGBReferenceViewingConditions);

//    for (int s : new int[] {25, 50, 75}) {
    for (int s : new int[] {50}) {
      Plot2D plotc = Plot2D.getInstance();
      Plot2D plotj = Plot2D.getInstance();
      Plot2D plots = Plot2D.getInstance();
      for (int h = 0; h < 360; h += 15) {
        HSV basehsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h, 100, 100});
        Color c = basehsv.toRGB().getColor();
        for (int v = 10; v <= 100; v += 10) {
          HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h, s, v});
          RGB rgb = hsv.toRGB();
          CIEXYZ XYZ = rgb.toXYZ();
          CIECAM02Color jch = cam02.forward(XYZ);

          plotc.addCacheScatterLinePlot(String.valueOf(h), c, hsv.V, jch.C);
          plotj.addCacheScatterLinePlot(String.valueOf(h), c, hsv.V, jch.J);
          plots.addCacheScatterLinePlot(String.valueOf(h), c, hsv.V, jch.s);
        }
      }

      plotc.setAxeLabel(0, "V of HSV");
      plotc.setAxeLabel(1, "C of JCh");
      plotc.setVisible();
      plotc.setFixedBounds(1, 0, 100);
      plotc.setTitle("v(" + String.valueOf(s) + ") @C");

      plotj.setAxeLabel(0, "V of HSV");
      plotj.setAxeLabel(1, "J of JCh");
      plotj.setVisible();
      plotj.setFixedBounds(1, 0, 100);
      plotj.setTitle("v(" + String.valueOf(s) + ") @J");

      plots.setAxeLabel(0, "V of HSV");
      plots.setAxeLabel(1, "s of JCh");
      plots.setVisible();
      plots.setFixedBounds(1, 0, 100);
      plots.setTitle("v(" + String.valueOf(s) + ") @s");
    }

  }

}
