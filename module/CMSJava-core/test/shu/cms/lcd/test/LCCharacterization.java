package shu.cms.lcd.test;

import shu.math.Maths;
import shu.cms.plot.*;
import shu.cms.lcd.*;
import java.awt.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.*;
import java.util.*;

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
public class LCCharacterization {
  public final static LCCharacterization getInstance() {
    return getInstance(10000, 0.1091, 13000, 2, 1.1);
  }

  public final static LCCharacterization getInstance(double d, double deltaN,
      int blackCCT, double gamma, double blueFactor) {
    LCDTarget target = LCDTarget.Instance.getFromLogo(
        "Measurement Files/Monitor/eizo_ce240w/i1pro/darkroom/native/99.logo");
    Spectra rspectra = target.getSaturatedChannelPatch(RGB.Channel.R).
        getSpectra();
    Spectra gspectra = target.getSaturatedChannelPatch(RGB.Channel.G).
        getSpectra();
    Spectra bspectra = target.getSaturatedChannelPatch(RGB.Channel.B).
        getSpectra();
    bspectra.times(blueFactor);
    Spectra blackspectra = target.getBlackPatch().getSpectra();
    Spectra kspectra = Illuminant.getDaylightByTemperature(blackCCT).getSpectra();
    double blackY = blackspectra.getXYZ().Y;
    double kY = kspectra.getXYZ().Y;
    double kfactor = blackY / kY;
    kspectra.times(kfactor);
    kY = kspectra.getXYZ().Y;
    kspectra = kspectra.fillAndInterpolate(blackspectra);

//    double d = 10000;
//    double deltaN = 0.1091;
    LCCharacterization lc = new LCCharacterization(d, deltaN);
    lc.setPrimamrySpectra(rspectra, gspectra, bspectra);
    lc.setBlackSpectra(kspectra);
//    lc.setg
    lc.setGamma(gamma);
    return lc;
  }

  private double d;
  private double deltaN;
//  private double deltaNRange = 0.13;
//  private double minT = 0.001;
  private double deltaNFactor = .41;
  private double rhoFactor = 1;
  public LCCharacterization(double d, double deltaN) {
    this.d = d;
    this.deltaN = deltaN;
  }

  public double getT(double lamda, double grayLevel) {
//    double minDeltaN = deltaN * (1 - deltaNRange);
//    double deltan = grayLevel / 255. * (deltaN - minDeltaN) + minDeltaN;
    double deltan = grayLevel / 255. * deltaN;
    deltan = deltan * deltaNFactor;
    double rho = getRho(lamda, deltan, d) * rhoFactor;
    double t = 1 - getT(rho);
    return t;
  }

  public void setPrimamrySpectra(Spectra r, Spectra g, Spectra b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public void setBlackSpectra(Spectra k) {
    this.k = k;
  }

  private Spectra r, g, b, k;
  public Spectra getSpectra(double r, double g, double b) {
    Spectra sr = getSpectra0(RGB.Channel.R, r);
    Spectra sg = getSpectra0(RGB.Channel.G, g);
    Spectra sb = getSpectra0(RGB.Channel.B, b);
    sr.plus(sg);
    sr.plus(sb);
    sr.plus(k);
    return sr;
  }

  public Spectra getSpectra(RGB.Channel ch, int grayLevel) {
    Spectra s = getSpectra0(ch, grayLevel);
    s.plus(k);
    return s;
  }

  private double gamma = 2;
  public void setGamma(double gamma) {
    this.gamma = gamma;
  }

  public Spectra getSpectra0(RGB.Channel ch, double grayLevel) {
    Spectra s = null;
    switch (ch) {
      case R:
        s = (Spectra) r.clone();
        break;
      case G:
        s = (Spectra) g.clone();
        break;
      case B:
        s = (Spectra) b.clone();
        break;
    }
    int start = s.getStart();
    int end = s.getEnd();
    int interval = s.getInterval();
    double normal = grayLevel / 255.;
    double realGrayLevel = 255 * Math.pow(normal, gamma);

    for (int lamda = start; lamda <= end; lamda += interval) {
      double T = getT(lamda, realGrayLevel);
      double power = s.getData(lamda);
      s.setData(lamda, power * T);
    }
//    s.plus(k);
    return s;
  }

  public static void main(String[] args) {
    LCCharacterization lc = getInstance();

//    LCDTarget target = LCDTarget.Instance.getFromLogo(
//        "Measurement Files/Monitor/eizo_ce240w/i1pro/darkroom/native/99.logo");
//    Spectra rspectra = target.getSaturatedChannelPatch(RGB.Channel.R).
//        getSpectra();
//    Spectra gspectra = target.getSaturatedChannelPatch(RGB.Channel.G).
//        getSpectra();
//    Spectra bspectra = target.getSaturatedChannelPatch(RGB.Channel.B).
//        getSpectra();
////    rspectra.times(0.7);
////    bspectra.times(1.4);
//    Spectra blackspectra = target.getBlackPatch().getSpectra();
//    Spectra kspectra = Illuminant.getDaylightByTemperature(15000).getSpectra();
//    double blackY = blackspectra.getXYZ().Y;
//    double kY = kspectra.getXYZ().Y;
//    double kfactor = blackY / kY;
//    kspectra.times(kfactor);
//    kY = kspectra.getXYZ().Y;
//    kspectra = kspectra.fillAndInterpolate(blackspectra);

//    LCCharacterization.getInstance();

    Plot2D plot = Plot2D.getInstance("Transmission");
    Plot2D splot = Plot2D.getInstance("spectra");
    Plot2D cplot = Plot2D.getInstance("chromaticity");
    Plot2D Yplot = Plot2D.getInstance("lumninance");

//    splot.addSpectra("R", Color.red, rspectra);
//    splot.addSpectra("G", Color.green, gspectra);
//    splot.addSpectra("B", Color.blue, bspectra);

//    double d = 0.001;
//    double d = 10000;
    //wv 380 nm
    // 1um = 1000nm, 10um = 10000nm


//    double factor = 3.98;
//    double deltaN = 0.1091;
//    double d = 0.00005;
//    double factor = 80;
    final int EndGrayLevel = 255;
//    LCCharacterization lc = new LCCharacterization(d, deltaN);
//    lc.setPrimamrySpectra(rspectra, gspectra, bspectra);
//    lc.setBlackSpectra(kspectra);

    double whiteLuminance = lc.getSpectra(255, 255, 255).getXYZ().Y;
    for (int x = 0; x < 256; x++) {
      double normal = x / 255.;
      double pow = Math.pow(normal, 2.4);
      double luminance = whiteLuminance * pow;
      Yplot.addCacheScatterLinePlot("ideal", Color.gray, x, luminance);
    }

    for (RGB.Channel ch : RGB.Channel.RGBChannel) {
      String name = ch.name();
      int wavelength = 700 - ch.index * 100;
      Color c = ch.color;
      for (int x = 0; x <= EndGrayLevel; x += 1) {

        Spectra s = lc.getSpectra(ch, x);
        splot.addSpectra(name, c, s);
        double[] xyValues = s.getXYZ().getxyValues();
        cplot.addCacheScatterLinePlot(name, c, xyValues[0], xyValues[1]);
        Yplot.addCacheScatterLinePlot(name, c, x, s.getXYZ().Y);
        plot.addCacheScatterLinePlot(name, c, x, lc.getT(wavelength, x));

      }
    }
    Plot2D cctPlot = Plot2D.getInstance("cct");
    for (int x = 0; x <= EndGrayLevel; x += 1) {
      Spectra s = lc.getSpectra(x, x, x);
      CIEXYZ XYZ = s.getXYZ();
      Yplot.addCacheScatterLinePlot("W", Color.black, x, XYZ.Y);
      double[] xyValues = XYZ.getxyValues();
      cplot.addCacheScatterLinePlot("W", Color.black, xyValues[0], xyValues[1]);
      double CCT = XYZ.getCCT();
      cctPlot.addCacheScatterLinePlot("", x, CCT);
    }

    plot.addLinePlot("", EndGrayLevel, 0, EndGrayLevel, 1);
    plot.addLegend();
    plot.setVisible();
    splot.setVisible();
    Yplot.setVisible();
    Yplot.setFixedBounds(0, 0, EndGrayLevel);
    cctPlot.setVisible();
    cctPlot.setFixedBounds(0, 0, EndGrayLevel);

    cplot.setVisible();
    LocusPlot locus = new LocusPlot(cplot);
    locus.drawCIExyLocus(true);
    cplot.setFixedBounds(0, 0, 1);
    cplot.setFixedBounds(1, 0, 1);

  }

  public final static double getRho(double lamda, double deltan, double d) {
    return 2 * Math.PI * deltan * d / lamda;
  }

  public final static double getT(double deltan, double h, double lamda) {
    //sin^2[ (pi/2) * sqrt( 1+(2dn*h/l)^2 ) ] / 2[1+(2dn*h/l)2]
    //sin^2[ (pi/2) * sqrt( 1+(2dn*h/l)^2 ) ]
//    double rho = 2 * Math.PI * deltan * h / lamda;
    double numerator = Maths.sqr(
        Math.sin(Math.PI / 2 *
                 Math.sqrt(1 + Maths.sqr(2 * deltan * h / lamda)))
        );
    //2[1+(2dn*h/l)2]
    double denominator = (1 + Maths.sqr(2 * deltan * h / lamda));
    return numerator / denominator;
  }

  public final static double getT(double rho) {
    // sin^2(  0.5*pi*sqrt(  1+(r/pi)^2  )  )  / (1+ (r/pi)^2)
    double n = 1 + Maths.sqr(rho / Math.PI);
    double numerator = Math.sin(0.5 * Math.PI * Math.sqrt(n));
    numerator = numerator * numerator;
    double denominator = n;
    return numerator / denominator;
  }
}
