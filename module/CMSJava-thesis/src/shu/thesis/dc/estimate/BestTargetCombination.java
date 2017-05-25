package shu.thesis.dc.estimate;

import java.text.*;

import shu.cms.*;
import shu.cms.dc.*;
import shu.cms.dc.estimate.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 訓練導具組合的最佳化
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class BestTargetCombination {

  public static void main(String[] args) {
    DecimalFormat df = new DecimalFormat("##.##");
    LightSource.i1Pro[] lightSource = new LightSource.i1Pro[] {
        LightSource.i1Pro.D50, LightSource.i1Pro.D65,
        LightSource.i1Pro.F8, LightSource.i1Pro.F12};
    Spectra[] lightSourceSpectras = DCUtils.getSpectra(lightSource);
    double[] factor = DCUtils.produceNormalFactorToEqualLuminance(
        lightSourceSpectras);

    DCTarget D50CCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             lightSource[0],
                                             factor[0] * 0.938223176,
                                             DCTarget.Chart.CCSG);
    DCTarget D65CCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             lightSource[1],
                                             factor[1] * 0.953338021,
                                             DCTarget.Chart.CCSG);
    DCTarget F8CCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                            lightSource[2],
                                            factor[2], DCTarget.Chart.CCSG);
    DCTarget F12CCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             lightSource[3],
                                             factor[3] * 0.999148463,
                                             DCTarget.Chart.CCSG);

    DCTarget D50CC24 = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             lightSource[0],
                                             factor[0] * 0.938223176,
                                             DCTarget.Chart.CC24);
    DCTarget D65CC24 = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             lightSource[1],
                                             factor[1] * 0.953338021,
                                             DCTarget.Chart.CC24);
    DCTarget F8CC24 = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                            lightSource[2],
                                            factor[2], DCTarget.Chart.CC24);
    DCTarget F12CC24 = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             lightSource[3],
                                             factor[3] * 0.999148463,
                                             DCTarget.Chart.CC24);

    //========================================================================
    // 訓練導具
    //=======================================================================
    DCTarget[] trainingTargetArray = new DCTarget[] {
        D50CCSG, D65CCSG, F8CCSG, F12CCSG};
    //=======================================================================

    //========================================================================
    // 測試導具
    //=======================================================================
    DCTarget[] testTargetArray = new DCTarget[] {
        D50CCSG, D65CCSG, F8CCSG, F12CCSG, D50CC24, D65CC24, F8CC24, F12CC24};
    int testTargetArraySize = testTargetArray.length;
    //=======================================================================

    //========================================================================
    // 訓練導具組合
    //=======================================================================
    DCTarget[][] trainingTargetSet = TargetSelector.selectTarget(
        trainingTargetArray);
    //=======================================================================

    int size = trainingTargetSet.length;

    for (int x = 0; x < size; x++) {
      DCTarget[] theTrainingTargetArray = trainingTargetSet[x];
//      System.out.println("set " + (x + 1) + ":" + theTrainingTargetArray.length +
//                         " target(s)");
      for (int m = 0; m < theTrainingTargetArray.length; m++) {
        System.out.print(theTrainingTargetArray[m].getDescription() + "/");
      }
      System.out.println("");

      PrincipalEigenvectorEstimator estimator = new
          PrincipalEigenvectorEstimator(
              theTrainingTargetArray);
      //========================================================================
      // 基底向量的數量
      //========================================================================
      Spectra[] spectras = estimator.estimate(4);
      //========================================================================

      //========================================================================
      // 光譜進行合理化
      //========================================================================
      spectras = PrincipalEigenvectorEstimator.rationalize(spectras);
//      PrincipalEigenvectorEstimator.normalize(spectras);
      System.out.println(DoubleArray.toString(spectras[0].getData()));
      System.out.println(DoubleArray.toString(spectras[1].getData()));
      System.out.println(DoubleArray.toString(spectras[2].getData()));
      //========================================================================

      double absErrMeanRGB = 0.;
      double relErrMeanRGB = 0.;
      double absErrMaxRGB = 0.;
      double relErrMaxRGB = 0.;

      double cc24RelErrMeanRGB = 0.;

//      int testTargetArraySize = theTrainingTargetArray.length;
      for (int y = 0; y < testTargetArraySize; y++) {
        DCTarget testTarget = testTargetArray[y];
        EstimatorReport report = PrincipalEigenvectorEstimator.
            getEstimatorReport(spectras, testTarget);

//        System.out.println(" " + (y + 1) + "/" + testTargetArraySize +
//                           " " + testTarget.getDescription());
//        System.out.println(report);
//        System.out.println("AbsErrMeanRGB:" + report.getAbsErrMeanDeltaRGB());
//        System.out.println("RelErrMeanRGB:" + report.getRelErrMeanDeltaRGB());

        System.out.print(df.format(report.getRelErrMeanDeltaRGB()) + " ");
        System.out.print(df.format(report.getRelErrMaxDeltaRGB()) + " ");
        System.out.println(df.format(Math.sqrt(report.getRelErrMeanDeltaRGB() *
                                               report.getRelErrMaxDeltaRGB())));

        absErrMeanRGB += report.getAbsErrMeanDeltaRGB();
        relErrMeanRGB += report.getRelErrMeanDeltaRGB();
        absErrMaxRGB += report.getAbsErrMaxDeltaRGB();
        relErrMaxRGB += report.getRelErrMaxDeltaRGB();

        if (y >= 4) {
          cc24RelErrMeanRGB += report.getRelErrMeanDeltaRGB();
        }
      }
      absErrMeanRGB /= testTargetArraySize;
      relErrMeanRGB /= testTargetArraySize;
      absErrMaxRGB /= testTargetArraySize;
      relErrMaxRGB /= testTargetArraySize;

      System.out.println(df.format(relErrMeanRGB));
//      System.out.println(df.format(cc24RelErrMeanRGB/4.));

//      System.out.println("AbsErrMeanRGB:" + absErrMeanRGB);
//      System.out.println("RelErrMeanRGB:" + df.format(relErrMeanRGB));
//      System.out.println("AbsErrMaxRGB:" + absErrMaxRGB);
//      System.out.println("RelErrMaxRGB:" + relErrMaxRGB);
//      System.out.println("AbsErrMixRGB:" +
//                         Math.sqrt(absErrMeanRGB * absErrMaxRGB));
//      System.out.println("RelErrMixRGB:" +
//                         Math.sqrt(relErrMeanRGB * relErrMaxRGB));
      System.out.println("");
    }
  }
}
