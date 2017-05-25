package shu.thesis.dc.estimate;

import java.text.*;

import java.awt.*;

import shu.cms.*;
import shu.cms.dc.*;
import shu.cms.dc.estimate.*;
import shu.cms.plot.*;
import shu.math.array.*;

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

    LightSource.i1Pro[] lightSource = new LightSource.i1Pro[] {
        LightSource.i1Pro.D50};
    Spectra[] lightSourceSpectra = DCUtils.getSpectra(lightSource);
    double[] factor = DCUtils.produceNormalFactorToEqualLuminance(
        lightSourceSpectra);

    DCTarget ccsg = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                          lightSource[0],
                                          factor[0], DCTarget.Chart.CCSG);
    DCTarget cc24 = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                          lightSource[0],
                                          factor[0], DCTarget.Chart.CC24);

    DCTarget[] trainingTargetArray = new DCTarget[] {
        ccsg};

//    DCTarget[] testTargetArray = new DCTarget[] {
//        cc24};
    DCTarget[] testTargetArray = new DCTarget[] {
        ccsg};

    Plot2D plot = Plot2D.getInstance();
    plot.setVisible();
    for (int x = 1; x <= 36; x++) {
      PrincipalEigenvectorEstimator estimator = new
          PrincipalEigenvectorEstimator(
              trainingTargetArray);
      Spectra[] spectras = estimator.estimate(x);

      //========================================================================
      // 是否進行合理化
      //========================================================================
      spectras = PrincipalEigenvectorEstimator.rationalize(spectras);
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


        plot.addSpectra(null, Color.RED, spectras[0]);
        plot.addSpectra(null, Color.GREEN, spectras[1]);
        plot.addSpectra(null, Color.BLUE, spectras[2]);
//        System.out.println(DoubleArray.toString(spectras[0].getData()));
//        System.out.println(DoubleArray.toString(spectras[1].getData()));
//        System.out.println(DoubleArray.toString(spectras[2].getData()));
//        System.out.println("");
//        plot.setTitle(String.valueOf(x));
//        try {
//          Thread.sleep(500);
//        }
//        catch (InterruptedException ex) {
//        }
//        plot.removeAllPlots();
      }

    }

  }
}
