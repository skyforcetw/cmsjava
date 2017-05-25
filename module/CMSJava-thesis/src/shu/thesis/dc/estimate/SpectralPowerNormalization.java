package shu.thesis.dc.estimate;

import java.text.*;

import shu.cms.*;
import shu.cms.dc.*;
import shu.cms.devicemodel.dc.*;
import shu.math.*;
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
public class SpectralPowerNormalization {

  public static void main(String[] args) {
//    experiment1(args);
    experiment2(args);
  }

  public static void experiment1(String[] args) {
    DecimalFormat df = new DecimalFormat("##.##");
    LightSource.i1Pro[] lightSource = new LightSource.i1Pro[] {
        LightSource.i1Pro.D50, LightSource.i1Pro.D65,
        LightSource.i1Pro.F8, LightSource.i1Pro.F12};
    Spectra[] spectras = DCUtils.getSpectra(lightSource);
    double[] factor = DCUtils.produceNormalFactorToEqualLuminance(spectras);

    DCTarget D50CCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             lightSource[0],
                                             factor[0] * 1,
                                             DCTarget.Chart.CCSG);
    DCTarget D65CCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             lightSource[1],
                                             factor[1] * 1,
                                             DCTarget.Chart.CCSG);
    DCTarget F8CCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                            lightSource[2],
                                            factor[2], DCTarget.Chart.CCSG);
    DCTarget F12CCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                             lightSource[3],
                                             factor[3] * 1,
                                             DCTarget.Chart.CCSG);
    Patch D50w = D50CCSG.filter.getCC24LabPatchListFromCCSG().get(3);
    Patch D65w = D65CCSG.filter.getCC24LabPatchListFromCCSG().get(3);
    Patch F8w = F8CCSG.filter.getCC24LabPatchListFromCCSG().get(3);
    Patch F12w = F12CCSG.filter.getCC24LabPatchListFromCCSG().get(3);

    //==========================================================================
    // 正規化的E5 SPD(亮度相同)
    //==========================================================================
    Spectra[] whitePatchs = new Spectra[] {
        D50w.getSpectra(), D65w.getSpectra(), F8w.getSpectra(), F12w.getSpectra()};
    double[] factor2 = DCUtils.produceNormalFactorToEqualLuminance(whitePatchs);

    Spectra D50wn = D50w.getSpectra().timesAndReturn(factor2[0]);
    Spectra D65wn = D65w.getSpectra().timesAndReturn(factor2[1]);
    Spectra F8wn = F8w.getSpectra().timesAndReturn(factor2[2]);
    Spectra F12wn = F12w.getSpectra().timesAndReturn(factor2[3]);

    System.out.println(DoubleArray.toString(D50wn.getData()));
    System.out.println(DoubleArray.toString(D65wn.getData()));
    System.out.println(DoubleArray.toString(F8wn.getData()));
    System.out.println(DoubleArray.toString(F12wn.getData()));
    //==========================================================================

    //==========================================================================
    // coef luminance
    //==========================================================================
    DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(F8CCSG, true,
        Polynomial.COEF_3.BY_3, false);
    model.produceFactor();

    double F8Y = model.gammaCorrect(F8CCSG.getPatch(44).getRGB().
                                    getValues())[1];
    double D50r = model.gammaCorrect(D50CCSG.getPatch(44).getRGB().getValues())[
        1] / F8Y;
    double D65r = model.gammaCorrect(D65CCSG.getPatch(44).getRGB().getValues())[
        1] / F8Y;
    double F12r = model.gammaCorrect(F12CCSG.getPatch(44).getRGB().getValues())[
        1] / F8Y;

    System.out.println("D50r " + D50r);
    System.out.println("D65r " + D65r);
    System.out.println("F12r " + F12r);
    //==========================================================================

    Spectra e5R = F8w.getReflectSpectra();
    D50wn.times(D50r);
    D50wn.divide(e5R);

    D65wn.times(D65r);
    D65wn.divide(e5R);

    F8wn.divide(e5R);

    F12wn.times(F12r);
    F12wn.divide(e5R);

    double normalFactor = DCUtils.produceNormalFactorByMaxPeak(new Spectra[] {
        D50wn, D65wn, F8wn, F12wn});
    D50wn.times(normalFactor);
    D65wn.times(normalFactor);
    F8wn.times(normalFactor);
    F12wn.times(normalFactor);

    System.out.println(DoubleArray.toString(D50wn.getData()));
    System.out.println(DoubleArray.toString(D65wn.getData()));
    System.out.println(DoubleArray.toString(F8wn.getData()));
    System.out.println(DoubleArray.toString(F12wn.getData()));
  }

  public static void experiment2(String[] args) {
//    DecimalFormat df = new DecimalFormat("##.##");
    LightSource.i1Pro[] lightSource = new LightSource.i1Pro[] {
        LightSource.i1Pro.Flash, LightSource.i1Pro.A};
    Spectra[] lightSourceSpectra = DCUtils.getSpectra(lightSource);
    double[] factor = DCUtils.produceNormalFactorToEqualLuminance(
        lightSourceSpectra);
//    double[] factor = new double[] {
//        1, 1, 1, 1};


    //==========================================================================
    // 顯示光源的SPD
    //==========================================================================
//    Spectra[] ls = DCUtils.getSpectra(lightSource);
//    double[] lsfactor=DCUtils.produceNormalizedFactor(lightSource);
//    ls[0].times(lsfactor[0]);
//    ls[1].times(lsfactor[1]);
//    System.out.println(DoubleArray.toString(ls[0].getData()));
//    System.out.println(DoubleArray.toString(ls[1].getData()));
    //==========================================================================

    DCTarget FlashCCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                               lightSource[0],
                                               factor[0] * 1,
                                               DCTarget.Chart.CCSG);
    DCTarget ACCSG = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                           lightSource[1],
                                           factor[1] * 1,
                                           DCTarget.Chart.CCSG);

    Patch Flashw = FlashCCSG.filter.getCC24LabPatchListFromCCSG().get(3);
    Patch Aw = ACCSG.filter.getCC24LabPatchListFromCCSG().get(3);

//    System.out.println(Flashw);
//    System.out.println(Aw);
//    RGB flashRGB = Flashw.getRGB();
//    flashRGB.changeMaxValue(RGB.MaxValue.Double255);
//    RGB ARGB = Aw.getRGB();
//    ARGB.changeMaxValue(RGB.MaxValue.Double255);
//    System.out.println(flashRGB);
//    System.out.println(ARGB);

    //==========================================================================
    // 正規化的E5 SPD(亮度相同)
    //==========================================================================
    Spectra[] whitePatchs = new Spectra[] {
        Flashw.getSpectra(), Aw.getSpectra()};
    double[] factor2 = DCUtils.produceNormalFactorToEqualLuminance(whitePatchs);

    Spectra Flashwn = Flashw.getSpectra().timesAndReturn(factor2[0]);
    Spectra Awn = Aw.getSpectra().timesAndReturn(factor2[1]);

    System.out.println(DoubleArray.toString(Flashwn.getData()));
    System.out.println(DoubleArray.toString(Awn.getData()));
    //==========================================================================

    //==========================================================================
    // coef luminance
    //==========================================================================
    DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(
        ACCSG, true,
        Polynomial.COEF_3.BY_3, false);
    model.produceFactor();

    double AY = model.gammaCorrect(ACCSG.getPatch(44).getRGB().getValues())[
        1];

    double FlashY = model.gammaCorrect(FlashCCSG.getPatch(44).getRGB().
                                       getValues())[1];

//    System.out.println(ACCSG.getPatch(44).getRGB());
//    System.out.println(FlashCCSG.getPatch(44).getRGB());

    double Flashr = FlashY / AY;

    System.out.println("AY:" + AY);
    System.out.println("FlashY:" + FlashY);
    System.out.println("Flashr " + Flashr);
    //==========================================================================

    Spectra e5R = Flashw.getReflectSpectra();
    Flashwn.times(Flashr);
    Flashwn.divide(e5R);

    Awn.divide(e5R);

    double normalFactor = DCUtils.produceNormalFactorByMaxPeak(new Spectra[] {
        Flashwn, Awn});
    Flashwn.times(normalFactor);
    Awn.times(normalFactor);

    System.out.println(DoubleArray.toString(Flashwn.getData()));
    System.out.println(DoubleArray.toString(Awn.getData()));
  }
}
