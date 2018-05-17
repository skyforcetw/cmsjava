package shu.cms.test.gamma;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 探討兩種case
 * 1. 物理上的gamma完全相等
 * 2. 心理上的gamma完全相等
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class GammaTester2 {
  public static void main(String[] args) {
//    scene1(args);
    scene1(100, 1000);
//    scene2(200, 1000);
  }

  public static void scene2(double maxLuminance, double contrast) {
    double normalMin = maxLuminance / contrast / maxLuminance;
    double darkgamma = 2.4;
    CIEXYZ white = (CIEXYZ) Illuminant.D65WhitePoint.clone();
    ViewingConditions vc1 = ViewingConditions.getDarkViewingConditions(white,
        100);
    CIECAM02 cam1 = new CIECAM02(vc1);
    ViewingConditions vc2 = ViewingConditions.getDimViewingConditions(white,
        maxLuminance);
    CIECAM02 cam2 = new CIECAM02(vc2);

    Plot2D plot = Plot2D.getInstance();
    double[] dimGammaCurve = new double[256];
    double[] input = new double[256];

    for (int x = 0; x < 256; x++) {
      double normal = x / 255.;
      input[x] = normal;
      double power = Math.pow(normal, darkgamma);
      CIEXYZ XYZ = (CIEXYZ) white.clone();
      XYZ.times(power, false);
      CIECAM02Color c = cam1.forward(XYZ);
      CIEXYZ XYZ2 = cam2.inverse(c);
      plot.addCacheScatterLinePlot("dark", x, XYZ.Y * 100);
      plot.addCacheScatterLinePlot("dim", x, XYZ2.Y);
      dimGammaCurve[x] = XYZ2.Y;
    }

//    System.out.println(GammaFinder.findingGamma(dimGammaCurve));
    Maths.normalize(dimGammaCurve, dimGammaCurve[dimGammaCurve.length - 1]);
    double dimGamma = GammaFinder.findGamma(input, dimGammaCurve);
    System.out.println(dimGamma);
    for (int x = 0; x < 256; x++) {

      double normal = x / 255.;
      double power = Math.pow(normal, dimGamma);
      plot.addCacheScatterLinePlot(Double.toString(dimGamma), x, power * 100);
    }
    plot.addLegend();
    plot.setVisible();
    plot.setFixedBounds(0, 0, 255);
    plot.setFixedBounds(1, 0, 100);

//    System.out.println(GammaFinder.findingGamma(input, dimGammaCurve));
//    GammaFinder.findingGamma(input, dimGammaCurve);
  }

  public static void scene1(double maxLuminance, double contrast) {
    Plot2D plot = Plot2D.getInstance("gamma");
    Plot2D Yplot = Plot2D.getInstance("luminance");
    Plot2D jplot = Plot2D.getInstance("JCh");
    Plot2D djplot = Plot2D.getInstance("dJCh");

//    boolean showJNDIPlot = false;
    double normalMin = 1. / contrast;
    double gamma = 2.4;
    double offset = GammaTester.getOffset(normalMin, 1, gamma);

    CIEXYZ white = (CIEXYZ) Illuminant.D65WhitePoint.clone();
//    CIEXYZ white = (CIEXYZ) Illuminant.E.getNormalizeXYZ().clone();
    ViewingConditions vc = null; //ViewingConditions.getDarkViewingConditions(white);
    CIECAM02 cam = new CIECAM02(vc);

    double[][] lumiCurve = new double[3][256];
    double[][] JCurve = new double[3][256];
    double[][] dJCurve = new double[3][255];

    for (int x = 0; x < 256; x++) {
      double normal = x / 255.;
      double power = Math.pow(normal, gamma);

      double normal2 = x / 255. * (1 - offset);
      double power2 = Math.pow(normal2 + offset, gamma);

      double power4 = Math.pow(normal, gamma);
      power4 = power4 * (1 - normalMin) + normalMin;

      plot.addCacheScatterLinePlot("abs", x, power);
      plot.addCacheScatterLinePlot("in", x, power2);
//      plot.addCacheScatterLinePlot("3", x, power3);
      plot.addCacheScatterLinePlot("out", x, power4);

//      double norma3 = x / 255.;
//      double power3 = Math.pow(norma3 + normalMin, gamma);

      //========================================================================
      // luminance
      //========================================================================
      double luminance = power * maxLuminance;
      double luminance2 = power2 * maxLuminance;
      double luminance4 = power4 * maxLuminance;
      lumiCurve[0][x] = luminance;
      lumiCurve[1][x] = luminance2;
      lumiCurve[2][x] = luminance4;

      Yplot.addCacheScatterLinePlot("abs", x, luminance);
      Yplot.addCacheScatterLinePlot("in", x, luminance2);
      Yplot.addCacheScatterLinePlot("out", x, luminance4);
      //========================================================================

      //========================================================================
      // ciecam
      //========================================================================
      CIEXYZ XYZ = (CIEXYZ) white.clone();
      CIEXYZ XYZ2 = (CIEXYZ) white.clone();
      CIEXYZ XYZ4 = (CIEXYZ) white.clone();
      XYZ.times(power, false);
      XYZ2.times(power2, false);
      XYZ4.times(power4, false);
      CIECAM02Color JCh = cam.forward(XYZ);
      CIECAM02Color JCh2 = cam.forward(XYZ2);
      CIECAM02Color JCh4 = cam.forward(XYZ4);
      JCurve[0][x] = JCh.J;
      JCurve[1][x] = JCh2.J;
      JCurve[2][x] = JCh4.J;
      if (x > 0) {
        dJCurve[0][x - 1] = JCurve[0][x] - JCurve[0][x - 1];
        dJCurve[1][x - 1] = JCurve[1][x] - JCurve[1][x - 1];
        dJCurve[2][x - 1] = JCurve[2][x] - JCurve[2][x - 1];
      }
      jplot.addCacheScatterLinePlot("abs", x, JCh.J);
      jplot.addCacheScatterLinePlot("in", x, JCh2.J);
      jplot.addCacheScatterLinePlot("out", x, JCh4.J);
      //========================================================================

    } //end of loop

    //==========================================================================
    // ideal lightness
    //==========================================================================
    double[] idealLightnessCurve = new double[256];
    idealLightnessCurve[0] = JCurve[1][0];
    for (int x = 1; x < idealLightnessCurve.length; x++) {
      idealLightnessCurve[x] = idealLightnessCurve[x - 1] + dJCurve[0][x - 1];
    }

    Maths.normalizeKeepMinimum(idealLightnessCurve, 100);
    double[] idealdJCurve = Maths.firstOrderDerivatives(idealLightnessCurve);
    //==========================================================================

    djplot.addLinePlot("abs", 1, 255, dJCurve[0]);
    djplot.addLinePlot("in", 1, 255, dJCurve[1]);
    djplot.addLinePlot("out", 1, 255, dJCurve[2]);
    djplot.addLinePlot("ideal", 1, 255, idealdJCurve);
    djplot.addLegend();
    djplot.setVisible();
    djplot.setAxisLabels("Gray Level", "dLightness");
    djplot.setFixedBounds(0, 0, 25);
    //==========================================================================
    double[] idealLumiCurve = new double[256];
    for (int x = 0; x < 256; x++) {
      double J = idealLightnessCurve[x];
      CIECAM02Color JCh = new CIECAM02Color(J, 0, 0);
      CIEXYZ XYZ = cam.inverse(JCh);
      idealLumiCurve[x] = XYZ.Y / 100. * maxLuminance;
    }
    Maths.normalizeKeepMinimum(idealLumiCurve, 100);
    Yplot.addLinePlot("ideal", 0, 256, idealLumiCurve);

    //==========================================================================
    //底下是多餘的
    //==========================================================================
    double idealGammaWithOffset = GammaFinder.findGammaWithOffset(
        idealLumiCurve);

    System.out.println("r: " + GammaFinder.findGamma(idealLumiCurve));
    System.out.println("ideal r w/offset: " + idealGammaWithOffset);

    double[] testLumiCurve = DoubleArray.copy(idealLumiCurve);
    DoubleArray.minusAndNoReturn(testLumiCurve, testLumiCurve[0]);
    Maths.normalize(testLumiCurve, testLumiCurve[testLumiCurve.length - 1]);
    double testGamma = GammaFinder.findGamma(testLumiCurve);
    System.out.println("test r: " + testGamma);
//    idealGammaWithOffset = 2.4;

    Plot2D idealplot = Plot2D.getInstance("ideal");
    idealplot.addLinePlot("ideal", Color.red, 0, 255, idealLumiCurve);

    double idealoffset = GammaFinder.getGammaOffset(idealLumiCurve[0],
        idealLumiCurve[255], idealGammaWithOffset);
    for (int x = 0; x < 256; x++) {
      double normal = x / 255. * (1 - idealoffset);
      double power = Math.pow(normal + idealoffset, idealGammaWithOffset);
      double luminance = power * 100;
      idealplot.addCacheScatterLinePlot("w/offset", x, luminance);

      double normal2 = x / 255.;
      double power2 = Math.pow(normal2, testGamma);
      double luminance2 = power2 * (100 - idealLumiCurve[0]) + idealLumiCurve[0];
      idealplot.addCacheScatterLinePlot("test", x, luminance2);
    }
    idealplot.addLegend();
    idealplot.setVisible();
    idealplot.setAxisLabels("Gray Level", "Luminance");
    idealplot.setFixedBounds(0, 0, 255);
//    idealplot.setFixedBounds(0, 0, 25);
    //==========================================================================


    jplot.addLinePlot("ideal", 0, 255, idealLightnessCurve);
    jplot.addLegend();
    jplot.setVisible();
    jplot.setAxisLabels("Gray Level", "Lightness");
    jplot.setFixedBounds(0, 0, 255);
//    jplot.setFixedBounds(0, 0, 25);

    plot.addLegend();
    plot.setVisible();
    plot.setAxisLabels("Gray Level", "Luminance");
    plot.setFixedBounds(0, 0, 255);
//    plot.setFixedBounds(0, 0, 25);

    Yplot.addLegend();
    Yplot.setVisible();
    Yplot.setAxisLabels("Gray Level", "Luminance");
    Yplot.setFixedBounds(0, 0, 255);
//    Yplot.setFixedBounds(0, 0, 25);

  }

}
