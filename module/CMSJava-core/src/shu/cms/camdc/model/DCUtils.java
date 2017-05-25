package shu.cms.camdc.model;

import shu.cms.*;
import shu.cms.dc.ideal.*;
import shu.cms.plot.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.cms.dc.LightSource;
import shu.cms.devicemodel.dc.DCPolynomialRegressionModel;
import shu.math.Polynomial;
import shu.cms.dc.DCTargetBase;
import shu.cms.dc.*;
import shu.cms.colorspace.depend.RGB;
import java.util.List;
import shu.cms.colorspace.independ.CIEXYZ;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class DCUtils {
  private DCUtils() {

  }

  public final static void plotSensor(Plot2D plot, IdealDigitalCamera camera) {
    Spectra[] sensors = camera.getSensors();
    plot.addSpectra("r", sensors[0]);
    plot.addSpectra("g", sensors[1]);
    plot.addSpectra("b", sensors[2]);
  }

  public final static Interpolation1DLUT average(Interpolation1DLUT[] luts) {
    double[] input = new double[256];
    double[] output = new double[256];
    int size = luts.length;

    for (int x = 0; x < 256; x++) {
      input[x] = x;
//      output[x]=
      double total = 0;
      for (int y = 0; y < size; y++) {
        double v = luts[y].getValue(x);
        total += v;
      }
      double finalv = total / size;
      output[x] = finalv;
    }

    Interpolation1DLUT lut = new Interpolation1DLUT(input, output,
        Interpolation1DLUT.Algo.LINEAR);

    return lut;
  }

  public final static IdealDigitalCamera average(IdealDigitalCamera[] cameras) {
    int size = cameras.length;
    Spectra[][] spectras = new Spectra[3][size];
    for (int x = 0; x < size; x++) {
      Spectra[] sensors = cameras[x].getSensors();
      spectras[0][x] = sensors[0];
      spectras[1][x] = sensors[1];
      spectras[2][x] = sensors[2];
    }
    Spectra r = Spectra.average(spectras[0]);
    Spectra g = Spectra.average(spectras[1]);
    Spectra b = Spectra.average(spectras[2]);
    IdealDigitalCamera camera = new IdealDigitalCamera(new Spectra[] {r, g, b});
    return camera;
  }

  public final static IdealDigitalCamera produceIdealDigitalCamerar(double[][]
      XYZ2rgbMatrix, Patch whitePatch) {
    ColorMatchingFunction cmf = ColorMatchingFunction.CIE_1931_2DEG_XYZ;
    int start = cmf.getStart();
    int end = cmf.getEnd();
    int interval = cmf.getInterval();
    int size = (end - start) / interval + 1;
    double[][] sensorsData = new double[3][size];
    int index = 0;

    for (int nm = start; nm <= end; nm += interval) {
      double X = cmf.getSpectra(0).getData(nm);
      double Y = cmf.getSpectra(1).getData(nm);
      double Z = cmf.getSpectra(2).getData(nm);
      double[] XYZ = new double[] {
          X, Y, Z};
      double[] rgbValues = DoubleArray.times(XYZ2rgbMatrix, XYZ);
      sensorsData[0][index] = rgbValues[0];
      sensorsData[1][index] = rgbValues[1];
      sensorsData[2][index] = rgbValues[2];
      index++;
    }

    Spectra[] sensors = new Spectra[3];
    sensors[0] = new Spectra("R", Spectra.SpectrumType.FUNCTION, start, end,
                             interval, sensorsData[0]);
    sensors[1] = new Spectra("G", Spectra.SpectrumType.FUNCTION, start, end,
                             interval, sensorsData[1]);
    sensors[2] = new Spectra("B", Spectra.SpectrumType.FUNCTION, start, end,
                             interval, sensorsData[2]);
    IdealDigitalCamera camera = new IdealDigitalCamera(sensors);
    camera.setOriginalOutputOnly(true);

//    {
//    if (null != whitePatch) {
//      CIEXYZ XYZ = whitePatch.getNormalizedXYZ();
//      RGB rgb = whitePatch.getOriginalRGB();
//      double[] orgRGBValues = rgb.getValues(new double[3],
//                                            RGB.MaxValue.Double1);
//      double[] rgbValues = DoubleArray.times(XYZ2rgbMatrix, XYZ.getValues());
//      int a = 1;
//    }
//    }

    if (null != whitePatch) {
      calibrateSensorFactor(camera, whitePatch);
    }
    return camera;
  }

  public final static void calibrateSensorFactor(IdealDigitalCamera camera,
                                                 Patch whitePatch) {
    Spectra whiteSpectra = whitePatch.getSpectra();
    double[] whiteOutputValues = camera.captureOriginalOutput(whiteSpectra);
//    double[] rgbValues = whitePatch.getRGB().getValues();
//    double[] rgbValues = whitePatch.getOriginalRGB().getValues();
    double factor = 1. / whiteOutputValues[1];
    double[] factors = new double[] {
        factor, factor, factor};
    camera.setSensorFactor(factors);
//    double[] whiteOutputValues2 = camera.captureOriginalOutput(whiteSpectra);
//    int a = 1;
  }

  /**
   * 將CC24 White Patch調整成目標值 0.885583
   * @param dcTarget DCTarget
   */
  public final static void calibrateWhitePatchInCC24(DCTarget dcTarget) {
    Patch whitePatch = dcTarget.getPatch(3);
    double gfactor = 0.885583 / whitePatch.getRGB().G;
    for (Patch p : dcTarget.getPatchList()) {
      RGB rgb = p.getRGB();
      rgb.R *= gfactor;
      rgb.G *= gfactor;
      rgb.B *= gfactor;
    }
  }

  public final static double[][] getRGB2XYZMatrix(Illuminant illuminant,
                                                  IdealDigitalCamera camera) {
    DCTarget target = DCTarget.Instance.get(camera, illuminant,
                                            DCTargetBase.Chart.CC24);
    calibrateWhitePatchInCC24(target);

    DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(
        target, false, Polynomial.COEF_3.BY_3,
        Polynomial.COEF_3.BY_3, false);
    DCPolynomialRegressionModel.Factor factor = (DCPolynomialRegressionModel.
                                                 Factor) model.produceFactor();
    double[][] rgb2XYZm = factor.forwardCoefficients;
//    double[] w = DoubleArray.times(rgb2XYZm,
//                                   target.getPatch(7).getRGB().getValues());
    return rgb2XYZm;
  }

  public final static void replaceXYZByRGB2XYZMatrix(List<Patch> patchList,
      double[][] rgb2XYZMatrix, CIEXYZ luminance) {
    for (Patch p : patchList) {
      double[] rgbValues = p.getRGB().getValues();
      double[] XYZValues = DoubleArray.times(rgb2XYZMatrix, rgbValues);
      CIEXYZ XYZ = new CIEXYZ(XYZValues);
      if (null != luminance) {
        XYZ.times(luminance.Y);
      }
      Patch.Operator.setXYZ(p, XYZ);
    }
  }
}
