package shu.thesis.recover;

import shu.cms.*;
import shu.cms.dc.ideal.*;
import shu.cms.recover.*;
import shu.cms.reference.spectra.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *  感光元件的Response最佳化(only for Sun)
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class ResponseOptimization {

  /**
   *
   * @param maxIterativeTimes int 最大迭帶次數
   * @param rate double 每次迭代的比率
   */
  public static void optimize(int maxIterativeTimes, double rate) {

    Spectra illuminant = Illuminant.D50.getSpectra().reduce(
        400, 700, 10);
    System.out.println(illuminant.getName());
    double[][] spectralData = SpectraDatabase.MunsellBook.getGlossyEdition();
    int count = spectralData.length;
    double[][] rmsData1 = new double[count][];
    double[][] rmsData2 = new double[count][];

    double minRMS = Double.MAX_VALUE;
    double r = 0, g = 0, b = 0;
//      IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
//          IdealDigitalCamera.Type.Sun);
    IdealDigitalCamera camera = IdealDigitalCamera.getSunInstance(null);
    Spectra[] sensors = camera.getSensors();
    double[] s0 = sensors[0].getData();
    double[] s1 = sensors[1].getData();
    double[] s2 = sensors[2].getData();

    Spectra[] munsellSpectralData = new Spectra[count];
    for (int x = 0; x < count; x++) {
      munsellSpectralData[x] = new Spectra(null,
                                           Spectra.SpectrumType.EMISSION,
                                           400,
                                           700,
                                           10,
                                           spectralData[x]);
      munsellSpectralData[x].times(illuminant);
      rmsData1[x] = munsellSpectralData[x].getData();
    }

    double step = .01;
    double rStart = 0, rEnd = 1, gStart = 0, gEnd = 1; //, bStart = 1, bEnd = 1;
//      int iterativeTimes = 10;

    for (int i = 0; i < maxIterativeTimes; i++) {
      for (double rFactor = rStart; rFactor <= rEnd;
           rFactor += step) {
        for (double gFactor = gStart; gFactor <= gEnd;
             gFactor += step) {
          for (double bFactor = 1; bFactor <= 1; bFactor += 1) {
            if (rFactor == 0 || gFactor == 0 || bFactor == 0) {
              continue;
            }
            sensors[0].setData(DoubleArray.times(s0, rFactor));
            sensors[1].setData(DoubleArray.times(s1, gFactor));
            sensors[2].setData(DoubleArray.times(s2, bFactor));

//              PseudoInverse estimator = new PseudoInverse(camera,
//                  MunsellSVD.Type.Glossy, 3);
            Wiener estimator = new Wiener(camera,
                                          SpectraDatabase.Content.MunsellGlossy,
                                          3);
//            estimator.deleteCDataFile(3);
//            estimator.produceCDataFile(3);

            for (int x = 0; x < count; x++) {
              double[] rgb = munsellSpectralData[x].getRGBValues(camera);
              double[] R = estimator.estimateSpectraData(rgb);
              rmsData2[x] = R;
            }

            double rmsd = Maths.RMSD(rmsData1, rmsData2);
//              System.out.println(rms);
            if (rmsd < minRMS) {
              minRMS = rmsd;
              r = rFactor;
              g = gFactor;
              b = bFactor;
            }
          }
        }
      }

      step *= rate;
//        System.out.println(step);
      if (step < 1E-16) {
        break;
      }
      rStart = r - step;
      rEnd = r + step;
      gStart = g - step;
      gEnd = g + step;
      System.out.println(i + ": " + minRMS + "(minRMS) " + r + "(R) " + g +
                         "(G) " + b + "(B)");
    }
    System.out.println("over");

    /*for (double rFactor = .9920503881; rFactor <= .9920503883;
         rFactor += 0.00000000001) {
      for (double gFactor = .9964701092; gFactor <= .9964701094;
           gFactor += 0.00000000001) {
        for (double bFactor = 1; bFactor <= 1; bFactor += 0.001) {
          sensors[0].setData(DoubleArray.times(s0, rFactor));
          sensors[1].setData(DoubleArray.times(s1, gFactor));
          sensors[2].setData(DoubleArray.times(s2, bFactor));

          Wiener wiener = new Wiener(camera, MunsellSVD.Type.Glossy, 3);

          for (int x = 0; x < count; x++) {
            double[] rgb = munsellSpectralData[x].getRGBValues(camera);
            double[] R = wiener.estimateSpectra(rgb);
            rmsData2[x] = R;
          }

          double rms = Maths.rms(rmsData1, rmsData2);
          System.out.println(rms);
          if (rms < minRMS) {
            minRMS = rms;
            r = rFactor;
            g = gFactor;
            b = bFactor;
          }
        }
      }
           }
           System.out.println(minRMS + " " + r + " " + g + " " + b);*/


  }

  public static void main(String[] args) {
    optimize(30, .1);
  }
}
