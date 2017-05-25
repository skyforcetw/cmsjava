package shu.thesis.recover;

import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;
import shu.cms.devicemodel.dc.*;
import shu.cms.recover.*;
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
public class Materials {
  public static IdealDigitalCamera getSunCamera() {
    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.Sun);
    return camera;
  }

  public static IdealDigitalCamera getCIECamera() {
    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.CIEXYZ);
    Spectra[] sensors = camera.getSensors();
    for (int x = 0; x < sensors.length; x++) {
      sensors[x] = sensors[x].reduce(400, 700, 10);
    }
    return camera;

  }

  public static DCTarget getDCTarget(IdealDigitalCamera camera,
                                     Illuminant illuminant) {
    DCTarget.Chart chart = DCTarget.Chart.CCSG;
    LightSource.Source lightSource = LightSource.getLightSourceType(illuminant);
//    DCTarget target = DCTarget.Instance.get(camera, lightSource, chart);
    DCChartAdapter chartAdapter = new DCChartAdapter(chart, lightSource);
    IdealDigitalCameraAdapter dcAdapter = new IdealDigitalCameraAdapter(camera,
        chartAdapter.getSpectraList());
    DCTarget target = DCTarget.Instance.get(dcAdapter, chartAdapter,
                                            lightSource,
                                            chart);
    return target;
  }

  public static DCPolynomialRegressionModel getPolynomialRegressionModel(
      DCTarget
      target) {
    DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(target, false,
        Polynomial.COEF_3.BY_19C, false);
    model.produceFactor();
    return model;
  }

  public static DCPolynomialRegressionModel get3x3RegressionModel(DCTarget
      target) {
    DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(target, false,
        Polynomial.COEF_3.BY_3, false);
    model.produceFactor();
    return model;
  }

  public static List<Spectra> getIlluminantSpectraList(double[][] spectraData,
      Spectra illuminant) {
    double[][] illuminantSpectra = getIlluminantSpectraData(spectraData,
        illuminant);

    int size = spectraData.length;
    List<Spectra> spectraList = new ArrayList<Spectra> (size);

    for (int x = 0; x < size; x++) {
      Spectra sw = new Spectra(null, Spectra.SpectrumType.EMISSION, 400,
                               700,
                               10,
                               illuminantSpectra[x]);
      spectraList.add(sw);
    }

    return spectraList;
  }

  public static double[][] getIlluminantSpectraData(double[][] spectraData,
      Spectra illuminant) {
    int size = spectraData.length;
    int width = spectraData[0].length;
    double[][] result = new double[size][width];
    Spectra sw = new Spectra(null, Spectra.SpectrumType.EMISSION, 400, 700, 10, null);

    //==========================================================================
    // 資料初始化
    //==========================================================================
    for (int x = 0; x < size; x++) {

      System.arraycopy(spectraData[x], 0, result[x], 0, width);
//      Spectra sw = new Spectra(null, Spectra.SpectrumType.EMISSION, 400,
//                               700,
//                               10,
//                               result[x]);
      sw.setData(result[x]);

      sw.times(illuminant);
      result[x] = sw.getData();
    }

    return result;
  }

  public static double[][] getSpectraData(List<Spectra> spectraList) {
    int size = spectraList.size();
    double[][] spectraData = new double[size][];

    for (int x = 0; x < size; x++) {
      Spectra s = spectraList.get(x);
      spectraData[x] = s.getData();
    }
    return spectraData;
  }

  public static double[][] getRGBValues(List<Spectra> spectraList,
      SpectralCamera camera) {
    int size = spectraList.size();
    double[][] rgbValues = new double[size][];

    for (int x = 0; x < size; x++) {
      Spectra s = spectraList.get(x);
      rgbValues[x] = camera.getRGB(s).getValues();
//    rgbValues[x] = camera.capture(s);
    }
    return rgbValues;
  }

  public static double[][] getRGBValues(List<Spectra> spectraList,
      IdealDigitalCamera camera) {
    int size = spectraList.size();
    double[][] rgbValues = new double[size][];

    for (int x = 0; x < size; x++) {
      Spectra s = spectraList.get(x);
      rgbValues[x] = camera.capture(s);
    }
    return rgbValues;
  }

  public static List<RGB> getRGBList(double[][] rgbValues) {
    int size = rgbValues.length;
    List<RGB> rgbList = new ArrayList<RGB> (size);

    for (int x = 0; x < size; x++) {
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, rgbValues[x],
                        RGB.MaxValue.Double1);
      rgbList.add(rgb);
    }

    return rgbList;
  }

  public static List<Patch> getRGBPatchList(double[][] rgbValues) {
    int size = rgbValues.length;
    List<Patch> patchList = new ArrayList<Patch> (size);

    for (int x = 0; x < size; x++) {
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, rgbValues[x],
                        RGB.MaxValue.Double1);
      Patch p = new Patch(null, null, null, rgb);
      patchList.add(p);
    }

    return patchList;

  }

  /**
   * 取得數位相機原始未正規化的Digital Value
   * @param rgbData double[][]
   * @param camera IdealDigitalCamera
   * @return double[][]
   */
  public static double[][] getOriginalOutputRGBValues(double[][] rgbData,
      IdealDigitalCamera camera) {
    int size = rgbData.length;
    double[][] originalOutput = new double[size][];

    for (int x = 0; x < size; x++) {
      originalOutput[x] = camera.getOriginalOutputRGBValues(rgbData[x]);
    }

    return originalOutput;
  }

  public static List<Patch> getOriginalOutputRGBPatchList(double[][] rgbData,
      IdealDigitalCamera camera) {
    int size = rgbData.length;
    List<Patch> patchList = new ArrayList<Patch> (size);
    double[][] originalOutput = getOriginalOutputRGBValues(rgbData, camera);

    for (int x = 0; x < size; x++) {
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, originalOutput[x],
                        RGB.MaxValue.Double1);
      Patch p = new Patch(null, null, null, rgb);
      patchList.add(p);
    }

    return patchList;
  }

  public static Spectra getSpectra(double[] spectraData) {
    Spectra sw = new Spectra(null, Spectra.SpectrumType.EMISSION, 400,
                             700, 10, spectraData);
    return sw;

  }
}
