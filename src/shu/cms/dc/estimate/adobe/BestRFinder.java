package shu.cms.dc.estimate.adobe;

import java.text.*;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.dc.*;
import shu.cms.dc.estimate.*;
import shu.cms.plot.*;
import shu.cms.reference.spectra.*;
import shu.math.*;
import shu.math.array.DoubleArray;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 列出不同的r的估測誤差,供評斷最佳的r值
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class BestRFinder {
  public static void main(String[] args) {
    DecimalFormat df = new DecimalFormat("##.##");
    Illuminant blackbody6500k = new Illuminant(CorrelatedColorTemperature.
                                               getSpectraOfBlackbodyRadiator(
        6500));
    SpectraDatabaseAdapter munsell = new SpectraDatabaseAdapter(SpectraDatabase.
        Content.MunsellGlossy, Illuminant.D65, RGB.ColorSpace.AdobeRGB);
//    SpectraDatabaseAdapter munsell = new SpectraDatabaseAdapter(SpectraDatabase.
//        Content.MunsellGlossy, blackbody6500k, RGB.RGBColorSpace.AdobeRGB);
    //不要用會比較準
//    munsell = munsell.getRationalRGBAdapter();

    DCTarget ccsg = DCTarget.Instance.get(munsell, munsell, LightSource.CIE.D65,
                                          DCTarget.Chart.MunsellGlossy);

    DCTarget[] trainingTargetArray = new DCTarget[] {
        ccsg};

    DCTarget[] testTargetArray = new DCTarget[] {
        ccsg};

    Plot2D plot = Plot2D.getInstance();
    plot.setVisible();
    for (int x = 2; x <= 5; x++) {
      PrincipalEigenvectorEstimator estimator = new
          PrincipalEigenvectorEstimator(
              trainingTargetArray);
      Spectra[] spectras = estimator.estimate(x);

      //========================================================================
      // 是否進行合理化
      //========================================================================
      spectras = PrincipalEigenvectorEstimator.rationalize(spectras);
//      spectras[0] = spectras[0].reduceSpectra(380, 730, 10);
//      spectras[1] = spectras[1].reduceSpectra(380, 730, 10);
//      spectras[2] = spectras[2].reduceSpectra(380, 730, 10);
      //========================================================================

      for (int y = 0; y < testTargetArray.length; y++) {
        DCTarget testTarget = testTargetArray[y];

        EstimatorReport r = PrincipalEigenvectorEstimator.
            getEstimatorReport(spectras, testTarget);
        System.out.println("r:" + x);

        //========================================================================
        // 絕對誤差
        //========================================================================
        System.out.println(DoubleArray.toString(df, r.absErrRGBMean) + "," +
                           df.format(r.getAbsErrMeanDeltaRGB()) + " | " +
                           DoubleArray.toString(df, r.absErrRGBMax) + "," +
                           df.format(r.getAbsErrMaxDeltaRGB()));
        //========================================================================

        //========================================================================
        // 相對誤差
        //========================================================================
        System.out.println(DoubleArray.toString(df, r.relErrRGBMean) + "," +
                           df.format(r.getRelErrMeanDeltaRGB()) + " | " +
                           DoubleArray.toString(df, r.relErrRGBMax) + "," +
                           df.format(r.getRelErrMaxDeltaRGB()));
        //========================================================================
//        System.out.println("");

        plot.addSpectra(null, Color.RED, spectras[0]);
        plot.addSpectra(null, Color.GREEN, spectras[1]);
        plot.addSpectra(null, Color.BLUE, spectras[2]);
        System.out.println(DoubleArray.toString(spectras[0].getData()));
        System.out.println(DoubleArray.toString(spectras[1].getData()));
        System.out.println(DoubleArray.toString(spectras[2].getData()));
//        System.out.println("");
        plot.setTitle(String.valueOf(x));
//        try {
//          Thread.sleep(2000);
//        }
//        catch (InterruptedException ex) {
//        }
        plot.removeAllPlots();
      }

    }

  }
}
