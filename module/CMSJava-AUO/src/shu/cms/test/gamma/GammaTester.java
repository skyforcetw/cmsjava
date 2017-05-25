package shu.cms.test.gamma;

import java.awt.*;

import flanagan.math.*;
import shu.cms.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.cms.hvs.cam.ciecam02.CIECAM02;
import shu.cms.plot.*;
import shu.math.*;
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
public class GammaTester {

    static double getOffset(double minLuminance, double maxLuminance,
                                  double gamma) {
    double normal = minLuminance / maxLuminance;
    double n = Math.log10(normal) / gamma;
    double offset = Math.pow(10, n);
    return offset;
  }

  public static void main(String[] args) {
//    test(100, 10);
    test(100, 1000);
//    test(100, 5000);
//    test(100, 7000);

//    ciecamTest();
//    findViewingConditions();
  }

  /**
   * @deprecated
   */
  static void findViewingConditions() {

    final CIEXYZ white = (CIEXYZ) Illuminant.D65WhitePoint.clone();
//    final CIEXYZ white = (CIEXYZ) Illuminant.E.getNormalizeXYZ().clone();
    GSDF gsdf = GSDF.getDICOMInstance();

    final double[] jndiCurve = new double[2001];
    for (int x = 0; x <= 2000; x++) {
//      CIEXYZ XYZ = (CIEXYZ) white.clone();
//      XYZ.times(x, false);
//      double Y = x / 20.;
      double Y = x / 20.;
      double jndi = gsdf.getJNDIndex(Y);
      if (jndi > 0) {
        jndiCurve[x] = jndi;
      }
    }
    Maths.normalize(jndiCurve, jndiCurve[jndiCurve.length - 1]);
    DoubleArray.timesAndNoReturn(jndiCurve, 100);

    MinimisationFunction func = new MinimisationFunction() {
      public double function(double[] param) {
        double La = param[0];
        double Yb = param[1];
        ViewingConditions vc = new ViewingConditions(white, La, Yb,
            Surround.Dark, "Dark", 1);
        double[] camCurve = new double[2001];
        CIECAM02 cam = new CIECAM02(vc);
        for (int x = 0; x <= 2000; x++) {
          CIEXYZ XYZ = (CIEXYZ) white.clone();
          XYZ.times(x / 2000., false);
          CIECAM02Color c = cam.forward(XYZ);
          camCurve[x] = c.J;
        }
        double result = Maths.RMSD(camCurve, jndiCurve);
        return result;
      }
    };

    Minimisation min = new Minimisation();

    min.addConstraint(0, -1, Double.MIN_VALUE);
    min.addConstraint(0, 1, 1000);
    min.addConstraint(1, -1, Double.MIN_VALUE);
    min.addConstraint(1, 1, 100);
//    min.nelderMead(func, new double[] {0.1, 100}, Double.MIN_VALUE);
    min.nelderMead(func, new double[] {0.1, 100});
    double[] params = min.getParamValues();
    System.out.println(min.getMinimum());
    System.out.println("La: " + params[0] + " Yb: " + params[1]);
  }

  public static void ciecamTest() {
    CIEXYZ white = (CIEXYZ) Illuminant.D65WhitePoint.clone();
//    ViewingConditions vc = ViewingConditions.getDarkViewingConditions(white);
//    ViewingConditions vc = new ViewingConditions(white, .1, 100, Surround.Dark,
//                                                 "Dark");

//    ViewingConditions vc = new ViewingConditions(white, 100, 0.1497783329242653,
//                                                 Surround.Dark,
//                                                 "Dark");
//    ViewingConditions vc = new ViewingConditions(white, 99.99999999830726,
//                                                 0.17398423261645168
//                                                 , Surround.Dark, "Dark");
//    ViewingConditions vc = new ViewingConditions(white, 100,
//                                                 0.17398423261645168
//                                                 , Surround.Dark, "Dark");
    ViewingConditions vc = new ViewingConditions(white, 100, 0.1,
                                                 Surround.Dark, "Dark");

//    System.out.println(vc.LA);
    CIECAM02 cam = new CIECAM02(vc);
    Plot2D plot = Plot2D.getInstance();
    GSDF gsdf = GSDF.getDICOMInstance();
    double[] jndiCurve = new double[2001];

    for (int x = 0; x <= 2000; x++) {
      CIEXYZ XYZ = (CIEXYZ) white.clone();
      XYZ.times(x / 2000., false);

      CIECAM02Color c = cam.forward(XYZ);
      System.out.println(x + " " + c.J);
      plot.addCacheScatterLinePlot("ciecam", x, c.J);
      double jndi = gsdf.getJNDIndex(x / 20.);
      if (jndi > 0) {
        jndiCurve[x] = jndi;
//        plot.addCacheScatterLinePlot("gsdf", x, jndi);
      }
    }
    Maths.normalize(jndiCurve, jndiCurve[jndiCurve.length - 1]);
    DoubleArray.timesAndNoReturn(jndiCurve, 100);
    plot.addLinePlot("gsdf", Color.red, 0, 2000, jndiCurve);
    plot.setVisible();
  }

  public static void test(double maxLuminance, double contrast) {
    Plot2D plot = Plot2D.getInstance("gamma");
    Plot2D Yplot = Plot2D.getInstance("luminance");
//    Plot2D dYplot = Plot2D.getInstance("dluminance");
    Plot2D jndiplot = Plot2D.getInstance("JNDI");
    Plot2D djndiplot = Plot2D.getInstance("dJNDI");
    Plot2D jplot = Plot2D.getInstance("JCh");
    Plot2D djplot = Plot2D.getInstance("dJCh");
    GSDF gsdf = GSDF.getDICOMInstance();

    boolean showJNDIPlot = false;
    double normalMin = maxLuminance / contrast / maxLuminance;
    double gamma = 2.4;
    double offset = getOffset(normalMin, 1, gamma);

    CIEXYZ white = (CIEXYZ) Illuminant.D65WhitePoint.clone();
//    CIEXYZ white = (CIEXYZ) Illuminant.E.getNormalizeXYZ().clone();
    ViewingConditions vc = null;//ViewingConditions.getDarkViewingConditions(white);
    CIECAM02 cam = new CIECAM02(vc);

    double[][] lumiCurve = new double[3][256];
//    double[][] dlumiCurve = new double[3][255];
    double[][] jndiCurve = new double[3][256];
    double[][] djndiCurve = new double[3][255];
    double[] idealdJNDICurve = new double[255];
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

      double norma3 = x / 255.;
      double power3 = Math.pow(norma3 + normalMin, gamma);

      //========================================================================
      // luminance
      //========================================================================
      double luminance = power * maxLuminance;
      double luminance2 = power2 * maxLuminance;
      double luminance4 = power4 * maxLuminance;
      lumiCurve[0][x] = luminance;
      lumiCurve[1][x] = luminance2;
      lumiCurve[2][x] = luminance4;
//      if (x > 0) {
//        dlumiCurve[0][x - 1] = luminance - lumiCurve[0][x - 1];
//        dlumiCurve[1][x - 1] = luminance2 - lumiCurve[1][x - 1];
//        dlumiCurve[2][x - 1] = luminance4 - lumiCurve[2][x - 1];
//        dYplot.addCacheScatterLinePlot("abs", x, dlumiCurve[0][x - 1]);
//        dYplot.addCacheScatterLinePlot("in", x, dlumiCurve[1][x - 1]);
//        dYplot.addCacheScatterLinePlot("out", x, dlumiCurve[2][x - 1]);
//      }
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

      //========================================================================
      // jndi
      //========================================================================
      double jndi = gsdf.getJNDIndex(luminance);
      double jndi2 = gsdf.getJNDIndex(luminance2);
      double jndi4 = gsdf.getJNDIndex(luminance4);
      jndi = jndi < 0 ? 0 : jndi;
      jndi = Double.isNaN(jndi) ? 0 : jndi;
      jndi2 = jndi2 < 0 ? 0 : jndi2;
      jndi4 = jndi4 < 0 ? 0 : jndi4;
      jndiCurve[0][x] = jndi;
      jndiCurve[1][x] = jndi2;
      jndiCurve[2][x] = jndi4;
      if (jndi >= 0) {
        jndiplot.addCacheScatterLinePlot("abs", x, jndi);
      }
      jndiplot.addCacheScatterLinePlot("in", x, jndi2);
      jndiplot.addCacheScatterLinePlot("out", x, jndi4);
      if (x > 0) {
        double djndi = jndi - jndiCurve[0][x - 1];
        idealdJNDICurve[x - 1] = djndi;
        double djndi2 = jndi2 - jndiCurve[1][x - 1];
        double djndi4 = jndi4 - jndiCurve[2][x - 1];
        if (jndi >= 0) {
          djndiplot.addCacheScatterLinePlot("abs", x, djndi);
          djndiCurve[0][x - 1] = djndi;
        }
        djndiplot.addCacheScatterLinePlot("in", x, djndi2);
        djndiplot.addCacheScatterLinePlot("out", x, djndi4);
        djndiCurve[1][x - 1] = djndi2;
        djndiCurve[2][x - 1] = djndi4;
      }
      //========================================================================

    } //end of loop

    //==========================================================================
    // ideal lightness
    //==========================================================================
    double[] idealLightness = new double[256];
    idealLightness[0] = JCurve[1][0];
    for (int x = 1; x < idealLightness.length; x++) {
      idealLightness[x] = idealLightness[x - 1] + dJCurve[0][x - 1];
    }
    double minLightness = idealLightness[0];
    double maxLightness = 100 - minLightness;
    idealLightness = DoubleArray.minus(idealLightness, minLightness);
    Maths.normalize(idealLightness, idealLightness[idealLightness.length - 1]);
    DoubleArray.timesAndNoReturn(idealLightness, maxLightness);
    idealLightness = DoubleArray.plus(idealLightness, minLightness);
    double[] idealdJCurve = Maths.firstOrderDerivatives(idealLightness);
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
    double[] fixdJNDICurve = DoubleArray.copy(idealdJNDICurve);
    for (int x = 0; x < fixdJNDICurve.length; x++) {
      if (fixdJNDICurve[x] != 0) {
        for (int y = 1; y < x; y++) {
          fixdJNDICurve[y] = Interpolation.linear(0, x, 0, fixdJNDICurve[x], y);
        }
        break;
      }
    }
//    djplot.addLinePlot("fix", 1, 255, fixdJNDICurve);
    //==========================================================================
    double[] idealdjndiCurve = DoubleArray.copy(djndiCurve[0]);
    for (int x = 0; x < idealdjndiCurve.length; x++) {
      if (idealdjndiCurve[x] != 0) {
        for (int y = x; y >= 0; y--) {
          idealdjndiCurve[y] = Interpolation.linear(0, 1, idealdjndiCurve[y + 1],
              idealdjndiCurve[y + 2], -1);
        }
        break;
      }
    }
    djndiplot.addLinePlot("ideal", 1, 255, idealdjndiCurve);
    //==========================================================================

    jplot.addLinePlot("ideal", 0, 255, idealLightness);
    jplot.addLegend();
    jplot.setVisible();
    jplot.setAxisLabels("Gray Level", "Lightness");
    jplot.setFixedBounds(0, 0, 255);

    plot.addLegend();
    plot.setVisible();
    plot.setAxisLabels("Gray Level", "Luminance");
    plot.setFixedBounds(0, 0, 255);

    Yplot.addLegend();
    Yplot.setVisible();
    Yplot.setAxisLabels("Gray Level", "Luminance");
    Yplot.setFixedBounds(0, 0, 255);

    if (showJNDIPlot) {
      jndiplot.addLegend();
      jndiplot.setVisible();
      jndiplot.setAxisLabels("Gray Level", "JNDI");
      jndiplot.setFixedBounds(0, 0, 255);

      djndiplot.addLegend();
      djndiplot.setVisible();
      djndiplot.setAxisLabels("Gray Level", "dJNDI");
      djndiplot.setFixedBounds(0, 0, 255);
      Plot2D ddjplot = Plot2D.getInstance("double-dJNDI");
      double[] ddjndiCurve1 = DoubleArray.minus(djndiCurve[1], fixdJNDICurve);
      double[] ddjndiCurve2 = DoubleArray.minus(djndiCurve[2], fixdJNDICurve);
      ddjplot.addLinePlot("in", 1, 255, ddjndiCurve1);
      ddjplot.addLinePlot("out", 1, 255, ddjndiCurve2);
      ddjplot.addLegend();
      ddjplot.setVisible();
      ddjplot.setAxisLabels("Gray Level", "");
      ddjplot.setFixedBounds(0, 0, 255);
    }

  }
}
