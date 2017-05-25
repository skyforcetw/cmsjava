package shu.thesis.dc.estimate;

import java.text.*;

import shu.cms.*;
import shu.cms.dc.*;
import shu.cms.dc.estimate.*;
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
public class BestTargetCombination2 {

  public static void main(String[] args) {
//    test1(args);
    test2(args);
  }

  public static void test1(String[] args) {
    DecimalFormat df = new DecimalFormat("##.##");
    LightSource.i1Pro[] lightSource = new LightSource.i1Pro[] {
        LightSource.i1Pro.Flash, LightSource.i1Pro.A};
    Spectra[] lightSourceSpectra = DCUtils.getSpectra(lightSource);
    double[] factor = DCUtils.produceNormalFactorToEqualLuminance(
        lightSourceSpectra);

    DCTarget FlashCCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                               lightSource[0],
                                               factor[0] * 0.9784603529126596,
                                               DCTarget.Chart.CCSG);
    DCTarget ACCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                           lightSource[1],
                                           factor[1] * 1,
                                           DCTarget.Chart.CCSG);

    //========================================================================
    // 訓練導具
    //=======================================================================
    DCTarget[] trainingTargetArray = new DCTarget[] {
        FlashCCSG, ACCSG};
    //=======================================================================

    //========================================================================
    // 測試導具
    //=======================================================================
    DCTarget[] testTargetArray = new DCTarget[] {
        FlashCCSG, ACCSG};
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

  public static void test2(String[] args) {
    DecimalFormat df = new DecimalFormat("##.##");
    LightSource.i1Pro[] lightSource = new LightSource.i1Pro[] {
        LightSource.i1Pro.Flash, LightSource.i1Pro.A};
    Spectra[] lightSourceSpectra = DCUtils.getSpectra(lightSource);
//    double[] factor = DCUtils.produceNormalFactorToEqualLuminance(
//        lightSourceSpectra);
    double[] factor = DCUtils.produceNormalFactorToEqualLuminance(
        lightSourceSpectra, 1);

    int lssize = lightSource.length;
    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);

    for (int x = 0; x < lssize; x++) {
      Spectra s = lightSourceSpectra[x];
      s.times(factor[x]);
      plot.addSpectra(null, s);
      System.out.println(s.getName() + " " + s.getXYZ());
    }

    DCTarget FlashCCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                               lightSource[0],
                                               factor[0] * 0.9784603529126596,
                                               DCTarget.Chart.CCSG);
    DCTarget ACCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                           lightSource[1],
                                           factor[1] * 1,
                                           DCTarget.Chart.CCSG);

    //========================================================================
    // 訓練導具
    //=======================================================================
    DCTarget[] trainingTargetArray = new DCTarget[] {
        FlashCCSG, ACCSG};
    //=======================================================================

    //========================================================================
    // 測試導具
    //=======================================================================
    DCTarget[] testTargetArray = new DCTarget[] {
        FlashCCSG, ACCSG};
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
      //光譜的正規化
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
