package shu.thesis.dc.estimate;

import shu.cms.*;
import shu.cms.dc.*;
import shu.cms.plot.*;
import shu.math.array.*;

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
public class i1LightSourceComparator {
  public static void main(String[] args) {
//    XYZCompare(args);
    normalizeCompare(args);
  }

  public static void normalizeCompare(String[] args) {
    LightSource.i1Pro[] lightSource = new LightSource.i1Pro[] {
        LightSource.i1Pro.Flash,
        LightSource.i1Pro.A};
    Spectra[] lightSourceSpectra = DCUtils.getSpectra(lightSource);
    double[] factor = DCUtils.produceNormalizedFactor(
        lightSourceSpectra);
    int size = lightSource.length;

    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);

    for (int x = 0; x < size; x++) {
      Spectra s = lightSourceSpectra[x];
      s.times(factor[x]);
      plot.addSpectra(null, s);
      System.out.println(DoubleArray.toString(s.getData()));
      System.out.println(s.getName() + " " + s.getXYZ());
    }
    Spectra ave = Spectra.average(lightSourceSpectra);
    ave.normalizeDataToMax();
    System.out.println(DoubleArray.toString(ave.getData()));
    plot.addSpectra(null, ave);
//    Spectra plus=ave.timesAndReturn(2);
//    plus.normalizeDataToMax();
//    plot.addSpectra(plus);
  }

  public static void XYZCompare(String[] args) {
    LightSource.i1Pro[] lightSource = new LightSource.i1Pro[] {

        LightSource.i1Pro.D50, LightSource.i1Pro.D65, LightSource.i1Pro.F8,
        LightSource.i1Pro.F12, LightSource.i1Pro.Flash,
        LightSource.i1Pro.A};
    Spectra[] lightSourceSpectra = DCUtils.getSpectra(lightSource);
    double[] factor = DCUtils.produceNormalFactorToEqualLuminance(
        lightSourceSpectra);
    int size = lightSource.length;

    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);

    for (int x = 0; x < size; x++) {
      Spectra s = lightSourceSpectra[x];
      s.times(factor[x]);
      plot.addSpectra(null, s);
      System.out.println(s.getName() + " " + s.getXYZ());
    }

  }
}
