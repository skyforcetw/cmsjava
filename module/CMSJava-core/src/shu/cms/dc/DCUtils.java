package shu.cms.dc;

import flanagan.math.*;
import shu.cms.*;
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
public class DCUtils {
  public final static double EV(double timeValue, double apertureValue) {
    return -Fmath.log2(timeValue) + 2 * Fmath.log2(apertureValue);
  }

  public final static double EV(double timeValue, double apertureValue,
                                double ISO) {
    double EV100 = EV(timeValue, apertureValue);
    double compensate = Math.log10(ISO / 100.) / Math.log10(2);
    return EV100 + compensate;
  }

  public final static double luminance(double EV) {
    return Math.pow(2, EV - 3);
  }

  /**
   * ㄏ┮Τ方peak常タ砏て1
   * @param lightSource i1Pro[]
   * @return double[]
   * @deprecated
   */
  public final static double[] produceNormalizedFactor(LightSource.i1Pro[]
      lightSource) {
    Illuminant[] illuminants = getIlluminant(lightSource);
    double[] peakPower = getPeakPower(illuminants);
    int size = illuminants.length;
    double[] factor = new double[size];

    for (int x = 0; x < size; x++) {
      factor[x] = 1. / peakPower[x];
    }

    return factor;
  }

  public final static double[] produceNormalizedFactor(Spectra[]
      spectras) {
    double[] peakPower = getPeakPower(spectras);
    int size = spectras.length;
    double[] factor = new double[size];

    for (int x = 0; x < size; x++) {
      factor[x] = 1. / peakPower[x];
    }

    return factor;
  }

  /**
   *
   * @param lightSource i1Pro[]
   * @return double[]
   * @deprecated
   */
  public final static double[] normalizeFactor(LightSource.i1Pro[] lightSource) {
    int size = lightSource.length;
    Illuminant[] illuminants = new Illuminant[size];
    double[] peakPower = new double[size];

    //眔方peak秖
    for (int x = 0; x < size; x++) {
      illuminants[x] = LightSource.getIlluminant(lightSource[x]);
      Spectra s = illuminants[x].getSpectra();
      int peak = s.getPeak();
      peakPower[x] = s.getData(peak);
    }
    //т程
    double max = Maths.max(peakPower);
    double normal = max;

    double[] factor = new double[size];
    //waveLength秖程方1,玻ネ╰计
    for (int x = 0; x < size; x++) {
      factor[x] = illuminants[x].getSpectra().getXYZ().Y / Maths.sqr(normal);
    }
    return factor;
  }

  /**
   * 玻ネタ砏て玒计,ㄏlightSource┮Τ方獹璓
   * @param lightSource i1Pro[]
   * @return double[]
   * @deprecated
   */
  public final static double[] produceNormalFactorToEqualLuminance(LightSource.
      i1Pro[]
      lightSource) {
    Illuminant[] illuminants = getIlluminant(lightSource);
    double[] peakPower = getPeakPower(illuminants);

    int maxIndex = Maths.maxIndex(peakPower);
    Spectra maxIndexSpectra = illuminants[maxIndex].getSpectra();
    double maxIndexNormalFactor = 1. /
        maxIndexSpectra.getData(maxIndexSpectra.getPeak());
    double normalY = maxIndexSpectra.timesAndReturn(maxIndexNormalFactor).
        getXYZ().Y;
//    double normalY = 1;

    int size = lightSource.length;
    double[] factor = new double[size];

    for (int x = 0; x < size; x++) {
      factor[x] = normalY / illuminants[x].getSpectra().getXYZ().Y;
    }
    return factor;
  }

  public final static double[] produceNormalFactorToEqualLuminance(Spectra[]
      spectras, int normalIndex) {

//    double[] peakPower = getPeakPower(spectras);

    int maxIndex = normalIndex;
//    int maxIndex = Maths.maxIndex(peakPower);
    Spectra maxIndexSpectra = spectras[maxIndex];
    double maxIndexNormalFactor = 1. /
        maxIndexSpectra.getData(maxIndexSpectra.getPeak());
    double normalY = maxIndexSpectra.timesAndReturn(maxIndexNormalFactor).
        getXYZ().Y;

    int size = spectras.length;
    double[] factor = new double[size];

    for (int x = 0; x < size; x++) {
      factor[x] = normalY / spectras[x].getXYZ().Y;
    }
    return factor;
  }

  public final static double[] produceNormalFactorToEqualLuminance(Spectra[]
      spectras) {
    double[] peakPower = getPeakPower(spectras);

    int maxIndex = Maths.maxIndex(peakPower);
    /*Spectra maxIndexSpectra = spectras[maxIndex];
       double maxIndexNormalFactor = 1. /
        maxIndexSpectra.getData(maxIndexSpectra.getPeak());
       double normalY = maxIndexSpectra.timesAndReturn(maxIndexNormalFactor).
        getXYZ().Y;

       int size = spectras.length;
       double[] factor = new double[size];

       for (int x = 0; x < size; x++) {
      factor[x] = normalY / spectras[x].getXYZ().Y;
       }
       return factor;*/
    return produceNormalFactorToEqualLuminance(spectras, maxIndex);
  }

  /**
   *
   * @param spectras Spectra[]
   * @return double[]
   * @deprecated
   */
  public final static double[] produceNormalFactorToEqualLuminanceN(Spectra[]
      spectras) {
    double[] peakPower = getPeakPower(spectras);

    int maxIndex = Maths.maxIndex(peakPower);
    Spectra maxIndexSpectra = spectras[maxIndex];
    double maxIndexNormalFactor = 1. /
        maxIndexSpectra.getData(maxIndexSpectra.getPeak());
    double normalY = maxIndexSpectra.timesAndReturn(maxIndexNormalFactor).
        getXYZ().Y;

    int size = spectras.length;
    double[] factor = new double[size];

    for (int x = 0; x < size; x++) {
      factor[x] = normalY / spectras[x].getXYZ().Y;
    }
    return factor;
  }

  /**
   * peak程normal,璸衡タ砏て玒计
   * @param lightSource i1Pro[]
   * @return double
   * @deprecated
   */
  public final static double produceNormalFactorByMaxPeak(LightSource.i1Pro[]
      lightSource) {
    Illuminant[] illuminants = getIlluminant(lightSource);
    double[] peakPower = getPeakPower(illuminants);

    //т程
    double max = Maths.max(peakPower);
    return 1. / max;
  }

  public final static double produceNormalFactorByMaxPeak(Spectra[] spectras) {
//  Illuminant[] illuminants = getIlluminant(lightSource);
    double[] peakPower = getPeakPower(spectras);

    //т程
    double max = Maths.max(peakPower);
    return 1. / max;
  }

  public final static Spectra[] getSpectra(LightSource.i1Pro[]
                                           lightSource) {
    int size = lightSource.length;
    Spectra[] spectras = new Spectra[size];
    for (int x = 0; x < size; x++) {

      spectras[x] = LightSource.getIlluminant(lightSource[x]).getSpectra();
    }
    return spectras;
  }

  protected final static Illuminant[] getIlluminant(LightSource.i1Pro[]
      lightSource) {
    int size = lightSource.length;
    Illuminant[] illuminants = new Illuminant[size];
    for (int x = 0; x < size; x++) {
      illuminants[x] = LightSource.getIlluminant(lightSource[x]);
    }
    return illuminants;
  }

  protected final static double[] getPeakPower(Illuminant[] illuminants) {
    int size = illuminants.length;
    double[] peakPower = new double[size];
    for (int x = 0; x < size; x++) {
      Spectra s = illuminants[x].getSpectra();
      int peak = s.getPeak();
      peakPower[x] = s.getData(peak);
    }
    return peakPower;
  }

  protected final static double[] getPeakPower(Spectra[] spectras) {
    int size = spectras.length;
    double[] peakPower = new double[size];
    for (int x = 0; x < size; x++) {
      Spectra s = spectras[x];
      int peak = s.getPeak();
      peakPower[x] = s.getData(peak);
    }
    return peakPower;
  }

  public static void main(String[] args) {
    System.out.println(EV(1. / 30, 4));
    System.out.println(luminance(EV(1. / 30, 4)));
  }
}
