package shu.cms.devicemodel.lcd.spectra;

import shu.cms.Spectra;

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
public class LCTransmission {
  private double cellGap;
  boolean normalBlack;
  double dn0, dn1, dnInterval;
  public LCTransmission(double cellGap, boolean normalBlack, double dn0,
                        double dn1, double dnInterval) {
    this.cellGap = cellGap;
    this.normalBlack = normalBlack;
    this.dn0 = dn0;
    this.dn1 = dn1;
    this.dnInterval = dnInterval;
  }

  public double getTransmission(int wavelength, double dn) {
    double r = 2 * dn * cellGap / (wavelength / 1000.); //retardation
    double pr2 = 1 + Math.pow(r, 2);
    double result = Math.pow(Math.sin(0.5 * Math.PI * Math.sqrt(pr2)), 2) / pr2;
    return normalBlack ? 1 - result : result;
  }

  public double getMaxDeltaN(int wavelength) {
    double lastTransmission = -1;
    for (double dn = dn0; dn <= dn1; dn += dnInterval) {
      double t = getTransmission(wavelength, dn);
      if (t < lastTransmission) {
        return dn - dnInterval;
      }
      lastTransmission = t;
    }
    return -1;
  }

  public double[] getTransmission(int wavelength) {
    return getTransmission(wavelength, dn0, dn1, dnInterval);
  }

  public double[] getTransmission(int wavelength, double dn0, double dn1,
                                  double dnInterval) {
    int size = (int) ( (dn1 - dn0) / dnInterval + 1);
    double[] data = new double[size];
    for (int x = 0; x < size; x++) {
      double dn = dn0 + x * dnInterval;
      double t = getTransmission(wavelength, dn);
      data[x] = t;
    }
    return data;
  }

  public Spectra getTransmission(int wavelengthStart, int wavelengthEnd,
                                 int interval, double dn) {
    int size = (wavelengthEnd - wavelengthStart) / interval + 1;
    double[] spectradata = new double[size];
    for (int x = 0; x < size; x++) {
      int wavelength = wavelengthStart + x * interval;
      spectradata[x] = getTransmission(wavelength, dn);
    }
    Spectra s = new Spectra("", Spectra.SpectrumType.TRANSMISSION,
                            wavelengthStart, wavelengthEnd, interval,
                            spectradata);
    return s;
  }
}
