package shu.cms.camdc.test;

import java.util.*;

import shu.cms.*;
import shu.cms.camdc.model.DCUtils;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;
import shu.cms.devicemodel.dc.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;

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
public class RGB2XYZMatrixEstimator {

  public static void main(String[] args) {
    adapationTest(args);
//    rgb2XYZ(args);
//    XYZ2rgb(args);
//    String m = " 3.2404542 -1.5371385 -0.4985314 -0.9692660  1.8760108  0.0415560     0.0556434 -0.2040259  1.0572252";
//    double[] mm = DoubleArray.parseDoubleArray(m);
//
//    IdealDigitalCamera c = produceIdealDigitalCamerar(DoubleArray.
//        to2DDoubleArray(mm, 3), null);
//    Plot2D plot = Plot2D.getInstance();
//    Spectra[] sensor = c.getSensors();
//    plot.addSpectra("r", sensor[0]);
//    plot.addSpectra("g", sensor[1]);
//    plot.addSpectra("b", sensor[2]);
//    plot.setVisible();
  }

  public static void XYZ2rgb(String[] args) {

  }

  private static DCTarget rgb2XYZMatrixTarget;
  private final static double[][] getRGB2XYZMatrix(LightSource.Source
      lightSource, IdealDigitalCamera camera) {
    rgb2XYZMatrixTarget = DCTarget.Instance.get(camera, lightSource,
                                                DCTargetBase.Chart.CC24);
    DCUtils.calibrateWhitePatchInCC24(rgb2XYZMatrixTarget);

    DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(
        rgb2XYZMatrixTarget, false, Polynomial.COEF_3.BY_3,
        Polynomial.COEF_3.BY_3, false);
    DCPolynomialRegressionModel.Factor factor = (DCPolynomialRegressionModel.
                                                 Factor) model.produceFactor();
    double[][] rgb2XYZm = factor.forwardCoefficients;
    return rgb2XYZm;
  }

  public static void adapationTest(String[] args) {
    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.BestEstimatedD200);
    LightSource.Source lightSource6000 = new LightSource.BlackbodyIlluminant(
        6000);
    //利用6k的轉換矩陣
    double[][] rgb2XYZm6k = getRGB2XYZMatrix(lightSource6000, camera);
//    DCTarget target6k = rgb2XYZMatrixTarget;

    System.out.println("6k");
    System.out.println(DoubleArray.toString(rgb2XYZm6k));

    double[][] XYZ2rgbm6k = DoubleArray.inverse(rgb2XYZm6k);
    //預測出相機光譜
    IdealDigitalCamera estimatedCamera = DCUtils.produceIdealDigitalCamerar(
        XYZ2rgbm6k, null);

    LightSource.Source lightSource3000 = new LightSource.BlackbodyIlluminant(
        3000);
    //把已知的相機光譜, 產生出3k下的轉換矩陣
    double[][] rgb2XYZm3k = getRGB2XYZMatrix(lightSource3000, camera);
    DCTarget target3k = rgb2XYZMatrixTarget;

    //然後利用預測的相機光譜, 再生出新的轉換矩陣
    double[][] rgb2XYZm6kto3k = getRGB2XYZMatrix(lightSource3000,
                                                 estimatedCamera);
//                                                 camera);
    DCTarget target6kto3k = rgb2XYZMatrixTarget;

    //同樣是3k下的轉換矩陣, 兩者做比較
    System.out.println("3k");
    System.out.println(DoubleArray.toString(rgb2XYZm3k));
    System.out.println("6kto3k");
    System.out.println(DoubleArray.toString(rgb2XYZm6kto3k));
    System.out.println("");

    List<Patch> list3k = target3k.getPatchList();
    List<Patch> list6kto3k = target6kto3k.getPatchList();
    CIEXYZ luminance = target3k.getLuminance();

    int size = list3k.size();
    for (int x = 0; x < size; x++) {
      Patch p0 = list3k.get(x);
      Patch p1 = list6kto3k.get(x);
      RGB rgb = p0.getRGB();
      Patch.Operator.setRGB(p1, rgb);
//      double[] XYZValues = DoubleArray.times(rgb2XYZm6kto3k, rgb.getValues());
//      CIEXYZ XYZ = new CIEXYZ(XYZValues);
//      XYZ.times(luminance.Y);
//      Patch.Operator.setXYZ(p1, XYZ);
    }
    DCUtils.replaceXYZByRGB2XYZMatrix(list6kto3k, rgb2XYZm6kto3k, luminance);

    list3k = target3k.getLabPatchList();
    list6kto3k = target6kto3k.getLabPatchList();
//    Patch p0 = list3k.get(3);
//    Patch p1 = list6kto3k.get(3);

    DeltaEReport[] report = DeltaEReport.Instance.patchReport(list3k,
        list6kto3k, false);
    System.out.println(report[0]);
    System.out.println(report[0].getPatchDeltaEReport());

    for (int x = 0; x < 3; x++) {
      camera.getSensors()[x].normalizeDataToMax();
      estimatedCamera.getSensors()[x].normalizeDataToMax();
    }
//    Plot2D plot = Plot2D.getInstance();
//    DCUtils.plotSensor(plot, camera);
//    DCUtils.plotSensor(plot, estimatedCamera);
//    plot.setVisible();
  }

  public static void rgb2XYZ(String[] args) {
    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.BestEstimatedD200);

    double[][] D50Matrix = null;
//    List<IdealDigitalCamera> cameraList = new ArrayList<IdealDigitalCamera> ();
    Plot2D plot = Plot2D.getInstance();

    for (int cct = 3000; cct <= 6000; cct += 3000) {
      LightSource.Source lightSource = null;

//      if (cct < 4000) {
      lightSource = new LightSource.BlackbodyIlluminant(cct);
//      }
//      else {
//      lightSource = new LightSource.DaylightIlluminant(cct);
//      }

      System.out.println(cct);
//      DCTarget dcTarget = DCTarget.Instance.get(camera, lightSource,
//                                                DCTargetBase.Chart.CC24);
//      calibrateWhitePatchInCC24(dcTarget);
//
//      DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(
//          dcTarget, false, Polynomial.COEF_3.BY_3, Polynomial.COEF_3.BY_3, false);
//      DCPolynomialRegressionModel.Factor factor = (DCPolynomialRegressionModel.
//          Factor) model.produceFactor();
//      double[][] rgb2XYZm = factor.forwardCoefficients;
      double[][] rgb2XYZm = getRGB2XYZMatrix(lightSource, camera);
      System.out.println(DoubleArray.toString(rgb2XYZm));

//      Patch whitePatch = dcTarget.getPatch(3);
//      Patch whitePatch = dcTarget.getBrightestPatch();
      Patch whitePatch = null;
      double[][] XYZ2rgbm = DoubleArray.inverse(rgb2XYZm);
      IdealDigitalCamera estimatedCamera = DCUtils.produceIdealDigitalCamerar(
          XYZ2rgbm, whitePatch);
//      cameraList.add(estimatedCamera);

      Spectra[] sensors = estimatedCamera.getSensors();
      plot.addSpectra(cct + "R", sensors[0]);
      plot.addSpectra(cct + "G", sensors[1]);
      plot.addSpectra(cct + "B", sensors[2]);

      //從XYZ2rgb的矩陣推算出sensor的spectra, 然後再用這spectra去推算不同色溫下的轉換矩陣
      //接下來, 就是如何估算白點的色度座標


//      if (cct == 5000) {
//        D50Matrix = rgb2XYZm;
//      }
//      else {
//        double[][] m = DoubleArray.divide(rgb2XYZm, D50Matrix);
////        System.out.println(DoubleArray.toString(m));
//      }
    }

//    Plot2D plot = Plot2D.getInstance();
//    for (IdealDigitalCamera c : cameraList) {
//      Spectra[] sensors = c.getSensors();
//      plot.addSpectra("R", sensors[0]);
//      plot.addSpectra("G", sensors[1]);
//      plot.addSpectra("B", sensors[2]);
//    }
    plot.setVisible();

  }
}
