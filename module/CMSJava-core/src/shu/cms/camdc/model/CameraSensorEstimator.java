package shu.cms.camdc.model;

import shu.math.lut.Interpolation1DLUT;
import shu.cms.dc.DCTarget;
import shu.cms.plot.Plot2D;
import shu.cms.dc.LightSource;
import shu.cms.dc.ideal.IdealDigitalCamera;
import shu.math.array.DoubleArray;
import java.util.*;
import shu.cms.Patch;
import shu.cms.dc.DCTargetBase;
import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.independ.*;
import shu.cms.*;

import flanagan.math.*;
import shu.cms.dc.ideal.SunOptimalSensor;
import shu.math.Maths;
import shu.cms.devicemodel.dc.DCPolynomialRegressionModel;
import shu.math.Polynomial;
import shu.cms.util.GammaCorrector;
import java.awt.Color;
import shu.math.GammaFinder;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CameraSensorEstimator {
  public final static IdealDigitalCamera estimateCamera(String iccFilename,
      boolean plotting, boolean showMessage) {
    return estimateCamera(iccFilename, plotting, showMessage, false);
  }

  private static void processOriginalRGB(DCPolynomialRegressionModel dcmodel,
                                         DCTarget dcTarget) {
    for (Patch p : dcTarget.getPatchList()) {
      RGB rgb = p.getRGB();
      RGB luminanceRGB = dcmodel.getLuminanceRGB(rgb);
      Patch.Operator.setOriginalRGB(p, luminanceRGB);
    }

  }

  public CameraSensorEstimator() {

  }

  private boolean gaussianFitting = false;
  private DCPolynomialRegressionModel dcModel;
  private boolean plotting = true;
  private GammaCorrector[] gammaCorrectos;
  public void setGaussianFitting(boolean fitting) {
    this.gaussianFitting = fitting;
  }

  public DCPolynomialRegressionModel getDCModel() {
    return dcModel;
  }

  public GammaCorrector[] getGammaCorrectors() {
    return gammaCorrectos;
  }

  private void processGammaCorrectors(DCPolynomialRegressionModel dcModel) {
    GammaCorrector rr = dcModel.getGammaCorrector(RGB.Channel.R);
    GammaCorrector gr = dcModel.getGammaCorrector(RGB.Channel.G);
    GammaCorrector br = dcModel.getGammaCorrector(RGB.Channel.B);
    Interpolation1DLUT rlut = rr.getLut();
    Interpolation1DLUT glut = gr.getLut();
    Interpolation1DLUT blut = br.getLut();
    //key相同, value不同
    double[] rValueArray = rlut.getValueArray();
    double[] gValueArray = glut.getValueArray();
    double[] bValueArray = blut.getValueArray();
    double[][] rgbValueArray = new double[][] {
        rValueArray, gValueArray, bValueArray};

    int size = rValueArray.length;
    double gMaxValue = gValueArray[size - 2];

    /**
     * 利用g來重建r和b, 避免clipping造成的lost
     * 這麼作的前提假設是...R和B的gamma是跟G一樣的
     *
     * 就多方的觀察來看, 確實如此, 因此才會這麼做
     */
    double rFactor = rValueArray[size - 2] / gMaxValue;
    double bFactor = bValueArray[size - 2] / gMaxValue;

    double[] newRValueArray = DoubleArray.copy(gValueArray);
    double[] newBValueArray = DoubleArray.copy(gValueArray);
    DoubleArray.timesAndNoReturn(newRValueArray, rFactor);
    DoubleArray.timesAndNoReturn(newBValueArray, bFactor);
    double[] newMaxValue = new double[] {
        newRValueArray[size - 1], gValueArray[size - 1],
        newBValueArray[size - 1]};
    //找到三個的最大值, 以該最大值當作1, 避免有clipping 的現象
    int maxIndex = DoubleArray.maxIndex(newMaxValue);
    double maxValue = newMaxValue[maxIndex];
    Maths.normalize(newRValueArray, maxValue);
    double[] newGValueArray = DoubleArray.copy(gValueArray);
    Maths.normalize(newGValueArray, maxValue);
    Maths.normalize(newBValueArray, maxValue);

    Interpolation1DLUT newrlut = new Interpolation1DLUT(rlut.getKeyArray(),
        newRValueArray, Interpolation1DLUT.Algo.LINEAR);
    Interpolation1DLUT newglut = new Interpolation1DLUT(glut.getKeyArray(),
        newGValueArray, Interpolation1DLUT.Algo.LINEAR);
    Interpolation1DLUT newblut = new Interpolation1DLUT(blut.getKeyArray(),
        newBValueArray, Interpolation1DLUT.Algo.LINEAR);
    GammaCorrector newrr = new GammaCorrector(newrlut);
    GammaCorrector newgr = new GammaCorrector(newglut);
    GammaCorrector newbr = new GammaCorrector(newblut);
    gammaCorrectos = new GammaCorrector[] {
        newrr, newgr, newbr};

    if (plotting) {
      double[] gkeyArray = glut.getKeyArray();
//      double[] gValueArray = glut.getValueArray();
//      int size = gkeyArray.length;
//      double gamma = GammaFinder.findingGamma(gkeyArray, gValueArray);
//      double gamma = GammaFinder.findingGamma(gkeyArray, gValueArray,
//                                              gkeyArray[0],
//                                              gValueArray[0],
//                                              gkeyArray[size - 1],
//                                              gValueArray[size - 1]);

//      double rfactro = 1. / rlut.getValueArray()[size - 2];
//      double gfactro = 1. / glut.getValueArray()[size - 2];
//      double bfactro = 1. / blut.getValueArray()[size - 2];
      double rfactro = 1.;
      double gfactro = 1.;
      double bfactro = 1.;

      Plot2D plot = Plot2D.getInstance("r correct");
      for (int x = 0; x < size; x++) {
        double normal = x / (size - 1.);
//        plot.addCacheScatterLinePlot("gamma", normal,
//                                     Math.pow(normal, gamma) * 1.2634);
        plot.addCacheScatterLinePlot("r", Color.red, rlut.getKeyArray()[x],
                                     rlut.getValueArray()[x] * rfactro);
        plot.addCacheScatterLinePlot("g", Color.green, glut.getKeyArray()[x],
                                     glut.getValueArray()[x] * gfactro);
        plot.addCacheScatterLinePlot("b", Color.blue, blut.getKeyArray()[x],
                                     blut.getValueArray()[x] * bfactro);
        plot.addCacheScatterLinePlot("r'", Color.red, rlut.getKeyArray()[x],
                                     newRValueArray[x]);
        plot.addCacheScatterLinePlot("g'", Color.green, glut.getKeyArray()[x],
                                     newGValueArray[x]);
        plot.addCacheScatterLinePlot("b'", Color.blue, blut.getKeyArray()[x],
                                     newBValueArray[x]);
      }
      plot.setVisible();
    }
  }

  public IdealDigitalCamera estimate(String iccFilename,
                                     LightSource.Source lightSource) {
//    Plot2D gammaPlot = plotting ? Plot2D.getInstance() : null;

    DCTarget dcTarget = DCTarget.Instance.get(lightSource, 1,
                                              DCTarget.Chart.MiniCC24,
                                              DCTarget.FileType.ICC,
                                              iccFilename);

    dcModel = new DCPolynomialRegressionModel(
        dcTarget, true, Polynomial.COEF_3.BY_3, Polynomial.COEF_3.BY_3, false);
    DCPolynomialRegressionModel.Factor factor = (DCPolynomialRegressionModel.
                                                 Factor) dcModel.produceFactor();
    processOriginalRGB(dcModel, dcTarget);
    processGammaCorrectors(dcModel);

//     double[][] rgb2XYZMatrix = factor.forwardCoefficients;
    double[][] XYZ2rgbMatrix = factor.inverseCoefficients;

//    System.out.println(dcmodel.testForwardModel(dcTarget, false)[0]);
//    System.out.println(dcmodel.testReverseModel(dcTarget, false)[0]);

//     DCModelEstimator estimator = new DCModelEstimator(dcTarget);
//     Interpolation1DLUT luminanceLut = estimator.luminanceLUT;
    if (plotting) {
      GammaCorrector rr = dcModel.getGammaCorrector(RGB.Channel.R);
      GammaCorrector gg = dcModel.getGammaCorrector(RGB.Channel.G);
      GammaCorrector bb = dcModel.getGammaCorrector(RGB.Channel.B);

      Plot2D rplot = Plot2D.getInstance();
      for (int x = 0; x < 256; x++) {
        double normal = x / 255.;
//        double l = luminanceLut.getValue(x);
        double r = rr.correct(normal);
        double g = gg.correct(normal);
        double b = bb.correct(normal);
//        rplot.addCacheScatterLinePlot("l", Color.black, x, l);
        rplot.addCacheScatterLinePlot("r", Color.red, x, r);
        rplot.addCacheScatterLinePlot("g", Color.green, x, g);
        rplot.addCacheScatterLinePlot("b", Color.blue, x, b);
        double rgr = r / (r + g + b);
        double rgb = b / (r + g + b);
        if (!Double.isNaN(rgr) && !Double.isNaN(rgb)) {
          rplot.addCacheScatterLinePlot("rgr", Color.magenta, x, rgr);
          rplot.addCacheScatterLinePlot("rgb", Color.black, x, rgb);
        }
      }
      rplot.setVisible();
    }

    Patch whitePatch = dcTarget.getBrightestPatch();
//    Patch whitePatch = null;
    IdealDigitalCamera camera = DCUtils.produceIdealDigitalCamerar(
        XYZ2rgbMatrix, whitePatch);
//    RGB nRGB = dcmodel.getRGB(whitePatch.getXYZ());

    camera.setOriginalOutputOnly(true);

    DCTarget dctarget2 = DCTarget.Instance.get(camera, LightSource.CIE.F8,
                                               DCTarget.Chart.MiniCC24);
    List<Patch> patchList1 = dcTarget.getPatchList();
    List<Patch> patchList2 = dctarget2.getPatchList();
    int size = patchList1.size();
    for (int x = 0; x < size; x++) {
      Patch p1 = patchList1.get(x);
      Patch p2 = patchList2.get(x);
      RGB rgb1 = p1.getOriginalRGB();
      RGB rgb2 = p2.getOriginalRGB();
      rgb2.changeMaxValue(RGB.MaxValue.Double255);

      CIEXYZ XYZ = p1.getNormalizedXYZ();
      double[] rgbValues = DoubleArray.times(XYZ2rgbMatrix,
                                             XYZ.getValues());
      double[] orgRGBValues = rgb1.getValues(new double[3],
                                             RGB.MaxValue.Double1);
      System.out.println(rgb1 + " " + rgb2);
    }

//    camera.setLuminanceLut(luminanceLut);

    if (gaussianFitting) {
      camera = gaussianFitting(camera, dcTarget);
    }

    return camera;
  }

  public final static IdealDigitalCamera estimateCamera(String iccFilename,
      boolean plotting, boolean showMessage, boolean gaussianFitting) {

    Plot2D gammaPlot = plotting ? Plot2D.getInstance() : null;

    DCTarget dcTarget = DCTarget.Instance.get(LightSource.CIE.F8, 1,
                                              DCTarget.Chart.MiniCC24,
                                              DCTarget.FileType.ICC,
                                              iccFilename);

    DCPolynomialRegressionModel dcmodel = new DCPolynomialRegressionModel(
        dcTarget, true, Polynomial.COEF_3.BY_3, Polynomial.COEF_3.BY_3, false);
    DCPolynomialRegressionModel.Factor factor = (DCPolynomialRegressionModel.
                                                 Factor) dcmodel.produceFactor();
    processOriginalRGB(dcmodel, dcTarget);

    double[][] rgb2XYZMatrix = factor.forwardCoefficients;
    double[][] XYZ2rgbMatrix = factor.inverseCoefficients;

//    System.out.println(dcmodel.testForwardModel(dcTarget, false)[0]);
//    System.out.println(dcmodel.testReverseModel(dcTarget, false)[0]);

    DCModelEstimator estimator = new DCModelEstimator(dcTarget);
    Interpolation1DLUT luminanceLut = estimator.luminanceLUT;
    GammaCorrector rr = dcmodel.getGammaCorrector(RGB.Channel.R);
    GammaCorrector gg = dcmodel.getGammaCorrector(RGB.Channel.G);
    GammaCorrector bb = dcmodel.getGammaCorrector(RGB.Channel.B);

    Plot2D rplot = Plot2D.getInstance();
    for (int x = 0; x < 256; x++) {
      double normal = x / 255.;
      double l = luminanceLut.getValue(x);
      double r = rr.correct(normal);
      double g = gg.correct(normal);
      double b = bb.correct(normal);
      rplot.addCacheScatterLinePlot("l", Color.black, x, l);
      rplot.addCacheScatterLinePlot("r", Color.red, x, r);
      rplot.addCacheScatterLinePlot("g", Color.green, x, g);
      rplot.addCacheScatterLinePlot("b", Color.blue, x, b);
    }
    rplot.setVisible();

    if (showMessage) {
      System.out.println(iccFilename);
//      System.out.println(estimator.luminanceGamma);
//      System.out.println(DoubleArray.toString(estimator.rgb2XYZMatrix));
      System.out.println(DoubleArray.toString(rgb2XYZMatrix));
      System.out.println("");
    }

    Patch whitePatch = dcTarget.getBrightestPatch();
//    Patch whitePatch = null;
    IdealDigitalCamera camera = DCUtils.produceIdealDigitalCamerar(
        XYZ2rgbMatrix, whitePatch);
//    RGB nRGB = dcmodel.getRGB(whitePatch.getXYZ());

    camera.setOriginalOutputOnly(true);

    DCTarget dctarget2 = DCTarget.Instance.get(camera, LightSource.CIE.F8,
                                               DCTarget.Chart.MiniCC24);
    List<Patch> patchList1 = dcTarget.getPatchList();
    List<Patch> patchList2 = dctarget2.getPatchList();
    int size = patchList1.size();
    for (int x = 0; x < size; x++) {
      Patch p1 = patchList1.get(x);
      Patch p2 = patchList2.get(x);
      RGB rgb1 = p1.getOriginalRGB();
      RGB rgb2 = p2.getOriginalRGB();
      rgb2.changeMaxValue(RGB.MaxValue.Double255);

      CIEXYZ XYZ = p1.getNormalizedXYZ();
      double[] rgbValues = DoubleArray.times(XYZ2rgbMatrix,
                                             XYZ.getValues());
      double[] orgRGBValues = rgb1.getValues(new double[3],
                                             RGB.MaxValue.Double1);
      System.out.println(rgb1 + " " + rgb2);
    }

    if (plotting) {
      Plot2D sensorPlot = Plot2D.getInstance(iccFilename);
      DCUtils.plotSensor(sensorPlot, camera);
      sensorPlot.setVisible();
      //========================================================================
      List<Patch> grayList = dcTarget.filter.grayScale();
      plotGreenInGrayScale(gammaPlot, iccFilename, grayList);
      //========================================================================
    }

    if (plotting) {
      gammaPlot.setVisible();
    }

//    camera.setLuminanceLut(luminanceLut);

    if (gaussianFitting) {
      camera = gaussianFitting(camera, dcTarget);
    }

    return camera;

  }

//  private final static Plot2D test = Plot2D.getInstance();

  private final static double getDeltaE(DCTarget dcTarget,
                                        Illuminant illuminant,
                                        DCTarget.Chart chart, CIEXYZ luminance,
                                        IdealDigitalCamera referenceCamera,
                                        double[] param) {

//    IdealDigitalCamera newcamera = getAsymmetryCamera(param, referenceCamera);
    IdealDigitalCamera newcamera = referenceCamera;
//    DCUtils.calibrateSensorFactor(newcamera, dcTarget.getBrightestPatch());

//    newcamera.setOriginalOutputOnly(true);
    DCTarget newTarget = DCTarget.Instance.get(newcamera, illuminant, chart);
    List<Patch> patchList = dcTarget.getPatchList();
    List<Patch> newPatchList = newTarget.getPatchList();
    int size = patchList.size();
    double[][] rgbValues = new double[size][];
    double[][] newrgbValues = new double[size][];

    for (int x = 0; x < size; x++) {
//      RGB rgb = patchList.get(x).getRGB();
      RGB rgb = patchList.get(x).getOriginalRGB();
      RGB newrgb = newPatchList.get(x).getRGB();

      rgbValues[x] = rgb.getValues(new double[3], RGB.MaxValue.Double1);
      newrgbValues[x] = newrgb.getValues(new double[3], RGB.MaxValue.Double1);
    }

    double rmsd = Maths.RMSD(rgbValues, newrgbValues);
    System.out.println(rmsd);
    return rmsd;

//    double[][] rgb2XYZMatrix = DCUtils.getRGB2XYZMatrix(illuminant,
//        newcamera);
//
//    //利用新的camera, 去產生該光源下的矩陣, 然後重新產生XYZ, 看跟原本相機的色差
//    DCTarget newTarget = DCTarget.Instance.get(newcamera, illuminant, chart);
//    DCUtils.replaceXYZByRGB2XYZMatrix(newTarget.getPatchList(),
//                                      rgb2XYZMatrix, luminance);
//
//    DeltaEReport[] reports = DeltaEReport.Instance.patchReport(dcTarget.
//        getLabPatchList(), newTarget.getLabPatchList(), false);
//    double deltaE = reports[0].getMeasuredDeltaE(DeltaEReport.AnalyzeType.
//                                                 Average);
//    return deltaE;
  }

//  private final static DCTarget getLinearLuminanceDCTarget(DCTarget dctarget,
//      Interpolation1DLUT luminanceLut) {
//    List<Patch> patchList = dctarget.getPatchList();
//    List<Patch> linearLumiPatchList = Patch.Produce.copyOf(patchList);
//    for (Patch p : linearLumiPatchList) {
//      RGB rgb = (RGB) p.getRGB().clone();
//
//    }
//    return null;
//
//  }

  private final static IdealDigitalCamera gaussianFitting(final
      IdealDigitalCamera camera, final DCTarget dcTarget) {
    final Illuminant illuminant = dcTarget.getIlluminant();
    final DCTarget.Chart chart = dcTarget.getChart();

    final CIEXYZ luminance = dcTarget.getLuminance();

    MinimisationFunction minfunc = new MinimisationFunction() {
      public double function(double[] param) {
        return getDeltaE(dcTarget, illuminant, chart, luminance, camera, param);
      }
    };

    Minimisation min = new Minimisation();
    double[] start = new double[] {
//        SunOptimalSensor.R_RHO, SunOptimalSensor.R_P, SunOptimalSensor.R_t,
//        SunOptimalSensor.G_RHO, SunOptimalSensor.G_P, SunOptimalSensor.G_t,
//        SunOptimalSensor.B_RHO, SunOptimalSensor.B_P, SunOptimalSensor.B_t};
        SunOptimalSensor.R_RHO, SunOptimalSensor.R_RHO,
        SunOptimalSensor.G_RHO, SunOptimalSensor.G_RHO,
        SunOptimalSensor.B_RHO, SunOptimalSensor.B_RHO};

    double[] step = new double[] {
        1, 1, 1, 1, 1, 1};

    min.addConstraint(0, -1, 20);
    min.addConstraint(0, 1, 60);
    min.addConstraint(1, -1, 20);
    min.addConstraint(1, 1, 60);

    min.addConstraint(2, -1, 20);
    min.addConstraint(2, 1, 70);
    min.addConstraint(3, -1, 20);
    min.addConstraint(3, 1, 70);

    min.addConstraint(4, -1, 20);
    min.addConstraint(4, 1, 70);
    min.addConstraint(5, -1, 20);
    min.addConstraint(5, 1, 70);

    min.nelderMead(minfunc, start, step);
    double minimum = min.getMinimum();
    double[] paramValues = min.getParamValues();
    IdealDigitalCamera result = getAsymmetryCamera(paramValues, camera);
//    result.setLuminanceLut(camera.getLuminanceLut());
    DCUtils.calibrateSensorFactor(result, dcTarget.getBrightestPatch());

    double deltaE = getDeltaE(dcTarget, illuminant, chart, luminance, camera,
                              paramValues);

//    double deltaE2 = getDeltaE(dcTarget, illuminant, chart, luminance, result,
//                              paramValues);

//    Plot2D plot = Plot2D.getInstance();
//    DCUtils.plotSensor(plot, camera);
//    DCUtils.plotSensor(plot, result);
//    plot.setVisible();
    return result;
  }

  private static IdealDigitalCamera getSymmetryCamera(double[] params,
      IdealDigitalCamera
      referenceCamera) {
    Spectra[] sensors = referenceCamera.getSensors();
    int start = sensors[0].getStart();
    int end = sensors[0].getEnd();
    int interval = sensors[0].getInterval();
    int rpeak = sensors[0].getPeak();
    int gpeak = sensors[1].getPeak();
    int bpeak = sensors[2].getPeak();

    //P決定位置
    //rho決定幅度
    double rrho = params[0];
    double rt = params[1];
    double grho = params[2];
    double gt = params[3];
    double brho = params[4];
    double bt = params[5];

    Spectra r = SunOptimalSensor.produceSensor("r", rrho, rpeak, rt,
                                               start,
                                               end, interval, true);
    Spectra g = SunOptimalSensor.produceSensor("g", grho, gpeak, gt,
                                               start,
                                               end, interval, true);
    Spectra b = SunOptimalSensor.produceSensor("b", brho, bpeak, bt,
                                               start,
                                               end, interval, true);
    IdealDigitalCamera camera = new IdealDigitalCamera(new Spectra[] {r, g,
        b});
    return camera;

  }

  private static IdealDigitalCamera getAsymmetryCamera(double[] params,
      IdealDigitalCamera referenceCamera) {
    Spectra[] sensors = referenceCamera.getSensors();
    int start = sensors[0].getStart();
    int end = sensors[0].getEnd();
    int interval = sensors[0].getInterval();
    int rpeak = sensors[0].getPeak();
    int gpeak = sensors[1].getPeak();
    int bpeak = sensors[2].getPeak();

    //P決定位置
    //rho決定幅度
    double rLeftRho = params[0];
    double rRightRho = params[1];
    double gLeftRho = params[2];
    double gRightRho = params[3];
    double bLeftRho = params[4];
    double bRightRho = params[5];

    Spectra r = SunOptimalSensor.produceSensor("r", rpeak, rLeftRho, rRightRho,
                                               start, end, interval);
    Spectra g = SunOptimalSensor.produceSensor("g", gpeak, gLeftRho, gRightRho,
                                               start, end, interval);
    Spectra b = SunOptimalSensor.produceSensor("b", bpeak, bLeftRho, bRightRho,
                                               start, end, interval);
    IdealDigitalCamera camera = new IdealDigitalCamera(new Spectra[] {r, g,
        b});
    return camera;

  }

  public final static IdealDigitalCamera estimateHTCLegend(boolean plotting,
      boolean showMessage) {

    String dirname = "Measurement Files/camera/htc legend/test3";
    String[] lightsources = new String[] {
        "日", "陰", "螢"}; //, "鎢"};
    int size = lightsources.length;
    Plot2D gammaPlot = plotting ? Plot2D.getInstance() : null;

    IdealDigitalCamera[] cameras = new IdealDigitalCamera[size];
//    Interpolation1DLUT[] luminanceLuts = new Interpolation1DLUT[size];

    for (int x = 0; x < size; x++) {
      String iccfilename = dirname + "/" + lightsources[x] + ".icc";
      cameras[x] = estimateCamera(iccfilename, plotting, showMessage);
//      luminanceLuts[x] = cameras[x].getLuminanceLut();

//      DCTarget target = DCTarget.Instance.get(LightSource.CIE.F8, 1,
//                                              DCTarget.Chart.MiniCC24,
//                                              DCTarget.FileType.ICC,
//                                              dirname + "/" + lightsources[x] +
//                                              ".icc");
//
//      DCModelEstimator estimator = new DCModelEstimator(target);
//      luminanceLuts[x] = estimator.luminanceLUT;
//
//      if (showMessage) {
//        System.out.println(lightsources[x]);
//        System.out.println(estimator.luminanceGamma);
//        System.out.println(DoubleArray.toString(estimator.rgb2XYZMatrix));
//        System.out.println("");
//      }
//
//      Patch whitePatch = target.getBrightestPatch();
//      IdealDigitalCamera camera = DCUtils.produceIdealDigitalCamerar(estimator.
//          XYZ2rgbMatrix, whitePatch);
//      cameras[x] = camera;
//
//      if (plotting) {
//        Plot2D sensorPlot = Plot2D.getInstance(lightsources[x]);
//        DCUtils.plotSensor(sensorPlot, camera);
//        sensorPlot.setVisible();
//        //========================================================================
//        List<Patch> grayList = target.filter.grayScale();
//        plotGreenInGrayScale(gammaPlot, lightsources[x], grayList);
//        //========================================================================
//      }
    }

    if (plotting) {
      gammaPlot.setVisible();
    }
//    IdealDigitalCamera camera = cameras[0];
    IdealDigitalCamera camera = DCUtils.average(cameras);
    if (plotting) {
      Plot2D sensorPlot = Plot2D.getInstance("ave");
      DCUtils.plotSensor(sensorPlot, camera);
      sensorPlot.setVisible();
    }
//    Interpolation1DLUT luminanceLut = DCUtils.average(luminanceLuts);
//    camera.setLuminanceLut(luminanceLut);
    return camera;
  }

  private final static void plotGreenInGrayScale(Plot2D plot, String name,
                                                 List<Patch> grayScaleList) {
    plot.addCacheScatterLinePlot(name, 0, 0);
    for (int y = 0; y < grayScaleList.size(); y++) {
      Patch p = grayScaleList.get(y);
      RGB rgb = p.getRGB();
      double g = rgb.G;
      plot.addCacheScatterLinePlot(name, y + 1, g);
    }
    plot.addCacheScatterLinePlot(name, grayScaleList.size() + 1, 255);

  }
}
