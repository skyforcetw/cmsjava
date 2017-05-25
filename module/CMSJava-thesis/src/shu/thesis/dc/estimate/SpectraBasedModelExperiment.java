package shu.thesis.dc.estimate;

import shu.cms.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;
import shu.cms.devicemodel.dc.*;
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
public class SpectraBasedModelExperiment {
  public static void main(String[] args) {
    basisVectorLightSourceTest();
//    lightSourceAdaptionTest();
  }

  public static void lightSourceAdaptionTest() {
    LightSource.i1Pro D50 = LightSource.i1Pro.F12;
    DCTarget D50Target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                               D50, 1.,
                                               DCTarget.Chart.CCSG);
    double[] d50normal = D50Target.getPatch(44).getRGB().getValues();

    DCPolynomialRegressionModel polyModel = new DCPolynomialRegressionModel(
        D50Target, false, Polynomial.COEF_3.BY_19C, true);
    polyModel.produceFactor();

    LightSource.i1Pro[] lightSourceArray = new LightSource.i1Pro[] {
        LightSource.i1Pro.D50, LightSource.i1Pro.D65, LightSource.i1Pro.F8,
        LightSource.i1Pro.F12};

    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.EstimatedD200_1);

    for (LightSource.i1Pro lightSource : lightSourceArray) {
      System.out.println(lightSource);
      //========================================================================
      // 旧ㄣ非称
      //========================================================================
      DCTarget ccsg = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                            lightSource, 1.,
                                            DCTarget.Chart.CCSG);
      DCTarget cc24 = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                            lightSource, 1.,
                                            DCTarget.Chart.CC24);
      //========================================================================

      //========================================================================
      // 方非称
      //========================================================================
//      Illuminant illuminant = LightSource.getIlluminant(lightSource);
//      Spectra spectra = illuminant.getSpectra();
      //========================================================================

      //========================================================================
      // 眯家Α
      //========================================================================
      SpectraBasedModel PIAtE = new SpectraBasedModel(ccsg, camera, false,
          null);
      PIAtE.produceFactor();

      System.out.println(ExperimentUtils.format(PIAtE.testTarget(ccsg, false)[
                                                0]));
      System.out.println(ExperimentUtils.format(PIAtE.testTarget(cc24, false)[
                                                0]));
      //========================================================================

      //========================================================================
      // 兜Α家Α
      //========================================================================
      //RGBタ砏て
//      double[] normal = ccsg.getPatch(44).getRGB().getValues();
      double[] normal = d50normal;
      ccsg.normalizeRGB(normal);
      cc24.normalizeRGB(normal);

      System.out.println(ExperimentUtils.format(polyModel.testTarget(ccsg, false)[
                                                0]));
      System.out.println(ExperimentUtils.format(polyModel.testTarget(cc24, false)[
                                                0]));
      //========================================================================
    }
  }

  public static void basisVectorLightSourceTest() {
    LightSource.i1Pro[] lightSourceArray = new LightSource.i1Pro[] {
        LightSource.i1Pro.D50, LightSource.i1Pro.D65, LightSource.i1Pro.F8,
        LightSource.i1Pro.F12};

    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.EstimatedD200_1);

    for (LightSource.i1Pro lightSource : lightSourceArray) {
      System.out.println(lightSource);
      //========================================================================
      // 旧ㄣ非称
      //========================================================================
      DCTarget ccsg = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                            lightSource, 1.,
                                            DCTarget.Chart.CCSG);
      DCTarget cc24 = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                            lightSource, 1.,
                                            DCTarget.Chart.CC24);
      //========================================================================

      //========================================================================
      // 方非称
      //========================================================================
      Illuminant illuminant = LightSource.getIlluminant(lightSource);
      Spectra spectra = illuminant.getSpectra();
      //========================================================================

      //========================================================================
      // 家Α非称
      //========================================================================
      SpectraBasedModel PIAtE = new SpectraBasedModel(ccsg, camera, false,
          null);
      PIAtE.produceFactor();
//      System.out.println("PIAtE");
      System.out.println(ExperimentUtils.format(PIAtE.testTarget(ccsg, false)[
                                                0]));
      System.out.println(ExperimentUtils.format(PIAtE.testTarget(cc24, false)[
                                                0]));

      SpectraBasedModel wienerAtE = new SpectraBasedModel(ccsg, camera, true,
          null);
      wienerAtE.produceFactor();
//      System.out.println("wienerAtE");
      System.out.println(ExperimentUtils.format(wienerAtE.testTarget(ccsg, false)[
                                                0]));
      System.out.println(ExperimentUtils.format(wienerAtE.testTarget(cc24, false)[
                                                0]));

      SpectraBasedModel PIAtLightSource = new SpectraBasedModel(ccsg, camera, false,
          spectra);
      PIAtLightSource.produceFactor();
//      System.out.println("PIAtLightSource");
      System.out.println(ExperimentUtils.format(PIAtLightSource.testTarget(ccsg, false)[
                                                0]));
      System.out.println(ExperimentUtils.format(PIAtLightSource.testTarget(cc24, false)[
                                                0]));

      SpectraBasedModel wienerAtLightSource = new SpectraBasedModel(ccsg,
          camera, true,
          spectra);
      wienerAtLightSource.produceFactor();
//      System.out.println("wienerAtLightSource");
      System.out.println(ExperimentUtils.format(wienerAtLightSource.testTarget(
          ccsg, false)[
                                                0]));
      System.out.println(ExperimentUtils.format(wienerAtLightSource.testTarget(
          cc24, false)[
                                                0]));

      //========================================================================
      System.out.println("");
    }
  }
}
