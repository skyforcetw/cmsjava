package auo.cms.test.intensity.spectra;

import shu.cms.colorformat.logo.LogoFile;
import java.io.*;
import shu.cms.colorformat.adapter.LogoFileAdapter;
import shu.cms.lcd.LCDTarget;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.measure.intensity.MaxMatrixIntensityAnalyzer;
import shu.cms.plot.*;
import java.awt.Color;
import java.util.List;
import flanagan.math.MinimisationFunction;
import shu.math.array.DoubleArray;
import shu.math.*;
import flanagan.math.Minimisation;
import shu.math.lut.Interpolation1DLUT;
import shu.cms.measure.intensity.MaxMatrixIntensityAnalyzer;

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
public class SpectraIntensityAnalyzer {
  static double getTransmission(double u) {
    double one_plus_uu = 1 + u * u;
    double up = Math.PI / 2 * Math.sqrt(one_plus_uu);
    double T = 1 / 2. - 1 / 2. * Maths.sqr(Math.sin(up)) / one_plus_uu;
    return T;
  }

  static double findDnd(final int wavelength, int fromIndex,
                        List<Patch> grayPatch) {
    int size = grayPatch.size();
    int arraySize = size - fromIndex;
    double[] wavelengthData = new double[arraySize];
    Spectra whiteSpectra = grayPatch.get(size - 1).getSpectra();
    int peak = whiteSpectra.getPeak();
    double peakPower = whiteSpectra.getData(peak);

    for (int x = fromIndex; x < size; x++) {
      Patch p = grayPatch.get(x);
      Spectra s = p.getSpectra();
      double data = s.getData(wavelength) / peakPower;
      wavelengthData[x - fromIndex] = data;
    }

    MinimisationFunction func = new MinimisationFunction() {

      public double function(double[] param) {
//        double dnd = param[0];
//        double u = 2 * dnd / wavelength;
//        double T = getTransmission(u);
        return -1;
      }

    };

    return -1;
  }

  static double findDn(final int wavelength, int fromIndex,
                       List<Patch> grayPatch, final double cellgap) {
    int size = grayPatch.size();
    int arraySize = size - fromIndex;
//    final double[] wavelengthData = new double[arraySize];
    Spectra whiteSpectra = grayPatch.get(size - 1).getSpectra();
    int peak = whiteSpectra.getPeak();
    double peakPower = whiteSpectra.getData(peak);
    double[] dnArray = new double[arraySize];

    for (int x = fromIndex; x < size; x++) {
      Patch p = grayPatch.get(x);
      Spectra s = p.getSpectra();
      int index = x - fromIndex;
      final double data = s.getData(wavelength) / peakPower *
          0.49999999999988853;
//      wavelengthData[x - fromIndex] = data;

      MinimisationFunction func = new MinimisationFunction() {
        public double function(double[] param) {
          double dn = param[0];
          double u = 2 * dn * cellgap / Math.PI;
          double T = getTransmission(u);
          return Math.abs(T - data);
        }
      };
      double predn = 0;
      if (x > fromIndex) {
        predn = dnArray[x - fromIndex];
      }
      Minimisation minimisation = new Minimisation();
      minimisation.addConstraint(0, -1, predn);
      minimisation.nelderMead(func, new double[] {0.001});
      dnArray[index] = minimisation.getParamValues()[0];
      double u = 2 * dnArray[index] * cellgap / Math.PI;
      double T = getTransmission(u);
      System.out.println(index + " " + data + " " + T + "/" + dnArray[index] +
                         " " +
                         minimisation.getMinimum());
    }

    return -1;
  }

  static double findCellGap(final Spectra s, Spectra white) {
    int peak = white.getPeak();
    double peakdata = white.getData(peak);
    final double[] spectradata = DoubleArray.copy(s.getData());
    Maths.normalize(spectradata, peakdata);

    final int start = s.getStart();
    final int interval = s.getInterval();
    final int end = s.getEnd();

    MinimisationFunction func = new MinimisationFunction() {
      /**
       * 計算index並且回傳
       * @param param double[] : dn d
       * @return double
       */
      public double function(double[] param) {
        double dn = param[0];
        double d = param[1];
        double[] estimateT = new double[spectradata.length];
        int index = 0;
        for (int wl = start; wl <= end; wl += interval) {
          double u = 2 * dn * d / wl;
          double T = getTransmission(u);
          estimateT[index++] = T;
        }
        double rmsd = Maths.RMSD(spectradata, estimateT);
        return rmsd;
      }

    };

    Minimisation minimisation = new Minimisation();
    minimisation.addConstraint(0, -1, 0);
//    minimisation.addConstraint(0, 1, 768);
    minimisation.addConstraint(1, -1, 0);
    minimisation.nelderMead(func, new double[] {0.1, 0.1});
    double[] param = minimisation.getParamValues();

    System.out.println(DoubleArray.toString(param) + " " +
                       minimisation.getMinimum());
    return param[1];
  }

  static void wSpectraAnalysis(LCDTarget target) {
    List<Patch> grayPatch = target.filter.grayPatch(true);
    Spectra whiteSpectra = target.getWhitePatch().getSpectra();
//    findCellGap(whiteSpactra, whiteSpactra);
    int peak = whiteSpectra.getPeak();
//    double dnd = findDnd(peak, 64, grayPatch);
    findDn(peak, 64, grayPatch, 3500);

    Plot2D plot = Plot2D.getInstance();
    int index = 0;
    for (Patch p : grayPatch) {
      Spectra s = p.getSpectra();
      if (index >= 64) {
        findCellGap(s, whiteSpectra);
      }
      int start = s.getStart();
      int end = s.getEnd();
//      plot
//      System.out.println(index + " " + s.getXYZ().Y);
      for (int x = start; x <= end; x += 50) {
        double d = s.getData(x);
        plot.addCacheScatterLinePlot(Integer.toString(x), index, d);
      }
      index++;
    }
    plot.addLegend();
    plot.setVisible();
    /**
     * u= 2 dn d / lamda
     T= 1/2 - 1/2 * sin2(pi/2 * sqrt(1+u^2)) /(1+u^2)
     */


  }

  static void intensityAnalysis(LCDTarget target) {
    Patch rPatch = target.getPatch(RGB.Channel.R, 255);
    Patch gPatch = target.getPatch(RGB.Channel.G, 255);
    Patch bPatch = target.getPatch(RGB.Channel.B, 255);
    Patch wPatch = target.getPatch(255, 255, 255);

    CIEXYZ rXYZ = rPatch.getXYZ();
    CIEXYZ gXYZ = gPatch.getXYZ();
    CIEXYZ bXYZ = bPatch.getXYZ();
    CIEXYZ wXYZ = wPatch.getXYZ();

//==========================================================================
//intensity
//==========================================================================
    MaxMatrixIntensityAnalyzer analyzer = new MaxMatrixIntensityAnalyzer();
    analyzer.setupComponent(RGB.Channel.R, rXYZ);
    analyzer.setupComponent(RGB.Channel.G, gXYZ);
    analyzer.setupComponent(RGB.Channel.B, bXYZ);
    analyzer.setupComponent(RGB.Channel.W, wXYZ);
    analyzer.enter();
    Plot2D rPlot = Plot2D.getInstance("R");
    Plot2D gPlot = Plot2D.getInstance("G");
    Plot2D bPlot = Plot2D.getInstance("B");
    int index = 0;
    for (Patch p : target.filter.grayPatch(true)) {
      RGB intensity = analyzer.getIntensity(p.getXYZ());
      CIEXYZ pureRXYZ =
          target.getPatch(RGB.Channel.R, p.getRGB().getValue(RGB.Channel.R)).
          getXYZ();
      CIEXYZ pureGXYZ =
          target.getPatch(RGB.Channel.G, p.getRGB().getValue(RGB.Channel.G)).
          getXYZ();
      CIEXYZ pureBXYZ =
          target.getPatch(RGB.Channel.B, p.getRGB().getValue(RGB.Channel.B)).
          getXYZ();
      RGB rIntensity = analyzer.getIntensity(pureRXYZ);
      RGB gIntensity = analyzer.getIntensity(pureGXYZ);
      RGB bIntensity = analyzer.getIntensity(pureBXYZ);
      System.out.println(intensity + " " + rIntensity + " " + gIntensity + " " +
                         bIntensity);
      rPlot.addCacheScatterLinePlot("W", Color.black, index, intensity.R);
      rPlot.addCacheScatterLinePlot("R", Color.red, index, rIntensity.R);
      rPlot.addCacheScatterLinePlot("G", Color.green, index, rIntensity.G);
      rPlot.addCacheScatterLinePlot("B", Color.blue, index, rIntensity.B);

      gPlot.addCacheScatterLinePlot("W", Color.black, index, intensity.G);
      gPlot.addCacheScatterLinePlot("R", Color.red, index, gIntensity.R);
      gPlot.addCacheScatterLinePlot("G", Color.green, index, gIntensity.G);
      gPlot.addCacheScatterLinePlot("B", Color.blue, index, gIntensity.B);

      bPlot.addCacheScatterLinePlot("W", Color.black, index, intensity.B);
      bPlot.addCacheScatterLinePlot("R", Color.red, index, bIntensity.R);
      bPlot.addCacheScatterLinePlot("G", Color.green, index, bIntensity.G);
      bPlot.addCacheScatterLinePlot("B", Color.blue, index, bIntensity.B);
      index++;
    }
    rPlot.setVisible();
    gPlot.setVisible();
    bPlot.setVisible();
    rPlot.setFixedBounds(1, 0, 1);
    gPlot.setFixedBounds(1, 0, 1);
    bPlot.setFixedBounds(1, 0, 1);
    //==========================================================================

  }

  static void calculateGamma() {
    double[] partLuminance = new double[] {
        0.513770521, //0
        0.87637949,
        3.452381372,
        8.409438133,
        15.16700935,
        24.68971825,
        37.33418655,
        53.59180069,
        73.4105835,
        97.95310211,
        126.568161,
        161.4513855,
        201.4701843,
        247.6165771,
        299.6734314,
        360.2316895,
        427.7836304 //255

    };
    int size = partLuminance.length;
    double[] keys = new double[size];
    for (int x = 0; x < size; x++) {
      keys[x] = x * 16;
      keys[x] = keys[x] > 255 ? 255 : keys[x];
    }
    Interpolation1DLUT lut = new Interpolation1DLUT(keys, partLuminance,
        Interpolation1DLUT.Algo.LINEAR);
    double maxLuminance = partLuminance[size - 1];
    double minLuminance = partLuminance[0];
    double[] allLuminance = new double[256];
    for (int x = 0; x < 256; x++) {
      allLuminance[x] = lut.getValue(x);
    }
    double[] gammas = new double[256];
    for (int x = 1; x < 255; x++) {
      double normal = (allLuminance[x] - minLuminance) /
          (maxLuminance - minLuminance);
      double gamma = Math.log(normal) / Math.log(x / 255.);
      gammas[x] = gamma;
    }
    Plot2D plot = Plot2D.getInstance();
    plot.addLinePlot("", 0, 255, gammas);
    plot.setVisible();

    Plot2D plot2 = Plot2D.getInstance();
    plot2.addLinePlot("", 0, 255, allLuminance);
    plot2.setVisible();

  }

  public static void main(String[] args) {
    calculateGamma();

    LCDTarget target = LCDTarget.Instance.getFromLogo("spectra/rgbw.logo");
//    intensityAnalysis(target);
    wSpectraAnalysis(target);

//    Patch rPatch = target.getPatch(RGB.Channel.R, 255);
//    Patch gPatch = target.getPatch(RGB.Channel.G, 255);
//    Patch bPatch = target.getPatch(RGB.Channel.B, 255);
//    Patch wPatch = target.getPatch(255, 255, 255);
//
//    CIEXYZ rXYZ = rPatch.getXYZ();
//    CIEXYZ gXYZ = gPatch.getXYZ();
//    CIEXYZ bXYZ = bPatch.getXYZ();
//    CIEXYZ wXYZ = wPatch.getXYZ();

  }
}
