package shu.cms.dc.ideal;

import java.util.*;

import flanagan.analysis.*;
import shu.cms.*;
import shu.cms.plot.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class SunOptimalSensor {
  public final static double R_P = 581;
  public final static double R_RHO = 40;
  public final static double R_t = 0.0;

  public final static double G_P = 551;
  public final static double G_RHO = 38;
  public final static double G_t = 0.05;

  public final static double B_P = 450;
  public final static double B_RHO = 25;
  public final static double B_t = 0.12;

  public final static int SENSOR_START = 380;
  public final static int SENSOR_END = 780;
  public final static int INTERVAL = 10;

  protected Spectra[] spectralSensitivityFunctions;

  public SunOptimalSensor() {
    spectralSensitivityFunctions = new Spectra[3];
    spectralSensitivityFunctions[0] = produceSensor("R_Channel", R_RHO, R_P,
        R_t);
    spectralSensitivityFunctions[1] = produceSensor("G_Channel", G_RHO, G_P,
        G_t);
    spectralSensitivityFunctions[2] = produceSensor("B_Channel", B_RHO, B_P,
        B_t);
  }

  public static Spectra produceSensor(String name, double peak, double rhoLeft,
                                      double rhoRight, int start, int end,
                                      int interval) {
    int size = (end - start) / interval + 1;
    double[] data = new double[size];
    double max = Double.MIN_VALUE;
    double rightFactor = -1;

    for (int x = 0; x < size; x++) {
      double lambda = start + x * interval;
      if (lambda < peak) {
        data[x] = sensorSensitivityFunction(rhoLeft, peak, lambda);
      }
      else {

        data[x] = sensorSensitivityFunction(rhoRight, peak, lambda);
        if ( -1 == rightFactor) {
          rightFactor = max / data[x];
        }
        data[x] *= rightFactor;

      }
      max = Math.max(data[x], max);
    }
    Maths.normalize(data, max);

    Spectra spectra = new Spectra(name, Spectra.SpectrumType.FUNCTION,
                                  start, end, interval, data);
    return spectra;
  }

  public static Spectra produceSensor(String name, double rho, double P,
                                      double t, int start, int end,
                                      int interval, boolean adjust) {
    int size = (end - start) / interval + 1;
    double[] data = new double[size];
    double max = Double.MIN_VALUE;

    for (int x = 0; x < size; x++) {
      double lambda = start + x * interval;
      data[x] = sensorSensitivityFunction(rho, P, lambda);
      max = Math.max(data[x], max);
    }
    Maths.normalize(data, max);

    if (adjust) {
      for (int x = 0; x < size; x++) {
        double lambda = start + x * interval;
        double d = data[x];
        d = adjust(d, rho, P, lambda, t);
        data[x] = d;
      }
    }
    Spectra spectra = new Spectra(name, Spectra.SpectrumType.FUNCTION,
                                  start, end, interval, data);
    return spectra;
  }

  protected static Spectra produceSensor(String name, double rho, double P,
                                         double t) {
    int size = (SENSOR_END - SENSOR_START) / INTERVAL + 1;
    double[] data = new double[size];
    double max = Double.MIN_VALUE;

    for (int x = 0; x < size; x++) {
      double lambda = SENSOR_START + x * INTERVAL;
      data[x] = sensorSensitivityFunction(rho, P, lambda);
      max = Math.max(data[x], max);
    }
    Maths.normalize(data, max);

    for (int x = 0; x < size; x++) {
      double lambda = SENSOR_START + x * INTERVAL;
      data[x] = adjust(data[x], rho, P, lambda, t);
    }

    Spectra spectra = new Spectra(name, Spectra.SpectrumType.FUNCTION,
                                  SENSOR_START, SENSOR_END, INTERVAL, data);
    return spectra;
  }

  protected final static double adjust(double origin, double rho, double P,
                                       double lambda, double t) {
    double coef = 0;
    if (lambda <= (P + rho / 2) && lambda >= (P - rho / 2)) {
      coef = 1;
    }
    double result = (1 - t) * origin + t * (coef);
    return result;
  }

  protected final static double sensorSensitivityFunction(double rho, double p,
      double lambda) {
    return Stat.normal(lambda, rho, p);
  }

  public static void main(String[] args) {
    Plot2D plot = Plot2D.getInstance();

    Spectra s = produceSensor("", 500, 38, 50, 380, 700, 1);
    plot.addSpectra("", s);

    plot.setVisible(true);

//    plot.addSpectra(null, spectra2);


  }

  public static void showSunSensor(String[] args) {
    SunOptimalSensor sunoptimalsensor = new SunOptimalSensor();
    System.out.println(Arrays.toString(sunoptimalsensor.
                                       spectralSensitivityFunctions[0].getData()));
    System.out.println(Arrays.toString(sunoptimalsensor.
                                       spectralSensitivityFunctions[1].getData()));
    System.out.println(Arrays.toString(sunoptimalsensor.
                                       spectralSensitivityFunctions[2].getData()));
    Spectra[] sensors = sunoptimalsensor.getSpectralSensitivityFunctions();
    Plot2D plot = Plot2D.getInstance();
    for (int x = 0; x < sensors.length; x++) {
      plot.addSpectra(null, sensors[x]);
    }
    plot.setVisible(true);
  }

  public Spectra[] getSpectralSensitivityFunctions() {
    return spectralSensitivityFunctions;
  }

}
