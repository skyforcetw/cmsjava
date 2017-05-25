package shu.thesis.dc;

import java.util.*;

import shu.cms.*;
import shu.cms.dc.*;
import shu.cms.devicemodel.dc.*;
import shu.math.*;
import shu.math.Polynomial.*;
import shu.thesis.dc.estimate.*;

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
public class BestPolynomialCoefficientFinder {

  /**
   * 分析delta找到最佳的多項式項數
   * @param lightSource i1Pro
   */
  public final static void analysisDeltaE(LightSource.i1Pro lightSource) {
    DCTarget dcTarget = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                              lightSource, 1.,
                                              DCTarget.Chart.CCSG);

    DCTarget dcTargetTest = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                                  lightSource, 1.,
                                                  DCTarget.Chart.CC24);

//RGB正規化
    double[] normal = dcTarget.getPatch(44).getRGB().getValues();
    dcTargetTest.normalizeRGB(normal);

    for (Polynomial.COEF_3 coef : Polynomial.COEF_3.values()) {
      DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(
          dcTarget, false, coef, true);

      DCModel.Factor factor = model.produceFactor();

      DeltaEReport[] testReports1 = model.testTarget(dcTarget, false);
      DeltaEReport[] testReports2 = model.testTarget(dcTargetTest, false);

      DeltaEReport r = testReports1[0];
      System.out.println(ExperimentUtils.format(r));

      r = testReports2[0];
      System.out.println(ExperimentUtils.format(r));
    }

  }

  /**
   * 產生所有項數的多項式係數
   * @param lightSource i1Pro
   */
  public final static void factorMaker(LightSource.i1Pro lightSource) {

    for (Polynomial.COEF_3 coef : Polynomial.COEF_3.values()) {
      DCTarget target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                              lightSource,
                                              1., DCTarget.Chart.CCSG);

      DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(
          target, false, coef, true);
      DCModel.Factor factor = model.produceFactor();
      DCModel.DCModelFactor dcModelFactor = model.produceDCModelFactor(factor);
      String filename = "factor/" + dcModelFactor.getModelFactorFilename() +
          ".factor";
      System.out.println(filename);
      model.store.modelFactorFile(dcModelFactor, filename);

      DeltaEReport[] testReports = model.testTarget(target, false);

      System.out.println("Training: " + target.getDescription());
      System.out.println(Arrays.toString(testReports));
    }
  }

  public static void main(String[] args) {
    LightSource.i1Pro lightSource = LightSource.i1Pro.D50;
    analysisDeltaE(lightSource);
//    factorMaker(lightSource);
  }
}
