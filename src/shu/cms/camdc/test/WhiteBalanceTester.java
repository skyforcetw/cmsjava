package shu.cms.camdc.test;

import java.io.*;
import java.util.*;

import java.awt.image.*;

import shu.cms.*;
import shu.cms.camdc.model.*;
import shu.cms.camdc.model.DCUtils;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;
import shu.cms.hvs.cam.*;
import shu.cms.image.*;
import shu.cms.plot.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.cms.devicemodel.dc.DCPolynomialRegressionModel;
import shu.cms.util.GammaCorrector;

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
public class WhiteBalanceTester {
  private IdealDigitalCamera camera;
  private DCPolynomialRegressionModel dcmodel;
  private GammaCorrector[] gammaCorrectors;
  public WhiteBalanceTester(IdealDigitalCamera camera,
                            DCPolynomialRegressionModel dcmodel) {
    this.camera = camera;
    this.dcmodel = dcmodel;
  }

  private double getGCorrectFactor(Illuminant illuminant) {
    List<Spectra> targetRefelectSpactra = DCTargetBase.Instance.
        getTargetReflectSpectra(DCTargetBase.Chart.CC24);
    Spectra whiteSpectra = (Spectra) targetRefelectSpactra.get(3).clone();
    List<Spectra> whiteSpectraList = new ArrayList<Spectra> (1);
    whiteSpectraList.add(whiteSpectra);

    whiteSpectraList = Spectra.produceSpectraPowerList(whiteSpectraList,
        illuminant);
    Spectra whitePowerSpetra = whiteSpectraList.get(0);

//    double[] rgbValues = camera.captureOriginalOutput(whitePowerSpetra);
    double[] rgbValues = whitePowerSpetra.getRGBValues(camera);
//    double[] rgbValues2 = illuminant.getSpectra().getRGBValues(camera);
//
//    Plot2D plot = Plot2D.getInstance();
//    plot.addSpectra("", whitePowerSpetra);
//    plot.addSpectra("", illuminant.getSpectra());
//    plot.setVisible();
    double gfactor = 0.885583 / rgbValues[1];
    return gfactor;
//    return null;
  }

  private final ChromaticAdaptation getChromaticAdaptation(Illuminant
      illuminant) {
    CIEXYZ from = illuminant.getNormalizeXYZ();
//  CIEXYZ to=  Illuminant.getD65WhitePoint();
    ChromaticAdaptation ca = ChromaticAdaptation.getInstanceAdaptToD65(from,
        CAMConst.CATType.CAT02);
    return ca;
  }

  private void gammaCorrect(double[] rgbValues) {
    rgbValues[0] = gammaCorrectors[0].correct(rgbValues[0]);
    rgbValues[1] = gammaCorrectors[1].correct(rgbValues[1]);
    rgbValues[2] = gammaCorrectors[2].correct(rgbValues[2]);

  }

  public final BufferedImage whiteBalance(BufferedImage image,
                                          Illuminant illuminant) {
    double gfactor = getGCorrectFactor(illuminant);
    camera.setGreenCorrectFactor(gfactor);

    double[][] rgb2XYZMatrix = DCUtils.getRGB2XYZMatrix(illuminant, camera);
    ChromaticAdaptation ca = getChromaticAdaptation(illuminant);
    double[][] illuminant2D65Matrix = ca.getAdaptationMatrixToDestination();
    double[][] rgb2D65XYZMatrix = DoubleArray.times(illuminant2D65Matrix,
        rgb2XYZMatrix);

    BufferedImage result = ImageUtils.cloneBufferedImage(image);
    int width = result.getWidth();
    int height = result.getHeight();
    WritableRaster raster = result.getRaster();
    double[] rgbValues = new double[3];

//    Interpolation1DLUT luminanceLut = camera.getLuminanceLut();

    rgbValues = new double[] {
//        241, 139, 64};
//        251, 145, 68};
//    rgbValues = new double[] {
        253, 226, 107};
    //======================================================================
    // 轉成亮度線性rgb
    //======================================================================
//    if (null != luminanceLut) {
//      rgbValues[0] = luminanceLut.getValue(rgbValues[0]);
//      rgbValues[1] = luminanceLut.getValue(rgbValues[1]);
//      rgbValues[2] = luminanceLut.getValue(rgbValues[2]);
//    }
    //======================================================================

//    double[] a0 = DoubleArray.times(rgb2XYZMatrix, rgbValues);
    rgbValues = DoubleArray.times(rgbValues, 1 / 255.);
//    this.dcmodel.gammaCorrect(rgbValues);
    gammaCorrect(rgbValues);
//    this.dcmodel.getLuminanceRGB()

    double[] D65XYZValues_ = DoubleArray.times(rgb2D65XYZMatrix, rgbValues);
    double[] sRGBValues_ = RGB.fromXYZValues(D65XYZValues_,
                                             RGB.ColorSpace.sRGB);
    DoubleArray.timesAndNoReturn(sRGBValues_, 255);
//    RGB rgb = new RGB();

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        raster.getPixel(x, y, rgbValues);

        //======================================================================
        // 轉成亮度線性rgb
        //======================================================================
//        rgbValues[0] = luminanceLut.getValue(rgbValues[0]);
//        rgbValues[1] = luminanceLut.getValue(rgbValues[1]);
//        rgbValues[2] = luminanceLut.getValue(rgbValues[2]);



        DoubleArray.timesAndNoReturn(rgbValues, 1. / 255);
        gammaCorrect(rgbValues);
//        rgbValues[0] = gammaCorrectors[0].correct(rgbValues[0]);
//        rgbValues[1] = gammaCorrectors[1].correct(rgbValues[1]);
//        rgbValues[2] = gammaCorrectors[2].correct(rgbValues[2]);
//        this.dcmodel.gammaCorrect(rgbValues);
//        rgb.setValues(rgbValues);

        //======================================================================


        double[] D65XYZValues = DoubleArray.times(rgb2D65XYZMatrix, rgbValues);
        double[] sRGBValues = RGB.fromXYZValues(D65XYZValues,
                                                RGB.ColorSpace.sRGB);
//        this.dcmodel.gammaUncorrect(sRGBValues);

        DoubleArray.timesAndNoReturn(sRGBValues, 255);

        RGB.rationalize(sRGBValues, RGB.MaxValue.Double255);
        raster.setPixel(x, y, sRGBValues);
      }
    }

    return result;
  }

  public static void main(String[] args) {

    /**
     * 重啟白平衡研究
     *
     * 由於設定在日光白平衡下的影像, 已經研究受到clipping...
     * 所以沒辦法以日光白平衡來做為自動白平和衡的source
     *
     * 目前限縮為, 集中解決htc在鎢絲燈下顏色不佳的問題
     * 因此採用日光白平衡所推得的sensor光譜為基礎
     * 但以鎢絲燈的影像來進行白平衡
     */


    /**
     * 有相機光譜、有光源、有影像, 白平衡到D65
     */
//    IdealDigitalCamera camera = DCModelEstimator.estimateHTCLegend(false, false);
    String dayICCFilename = "Measurement Files/camera/htc legend/test3/日.icc";
    String AICCFilename = "Measurement Files/camera/htc legend/test3/鎢絲燈.icc";

    DCTarget ADCTarget = DCTarget.Instance.get(LightSource.CIE.A, 1,
                                               DCTarget.Chart.MiniCC24,
                                               DCTarget.FileType.ICC,
                                               AICCFilename);

//    IdealDigitalCamera camera = CameraSensorEstimator.estimateCamera(
//        iccfilename, false, false, false);
    CameraSensorEstimator estimator = new CameraSensorEstimator();
//    estimator.setGaussianFitting(true);
    IdealDigitalCamera camera = estimator.estimate(dayICCFilename,
        LightSource.CIE.F8);
    DCPolynomialRegressionModel dcmodel = estimator.getDCModel();
    GammaCorrector[] gammaCorrectors = estimator.getGammaCorrectors();

//    iccfilename = "Measurement Files/camera/htc legend/test3/複製 -日.icc";
//    IdealDigitalCamera camera2 = DCModelEstimator.estimateCamera(iccfilename, false, false);

    Plot2D sensorPlot = Plot2D.getInstance();
    DCUtils.plotSensor(sensorPlot, camera);
    sensorPlot.setVisible();

    WhiteBalanceTester wbt = new WhiteBalanceTester(camera, dcmodel);
    wbt.gammaCorrectors = gammaCorrectors;
    BufferedImage image = null;

    try {
//      Illuminant.get
//      image = ImageUtils.loadImage("Image/WhiteBalance/HTC Legend/日光.jpg");
      image = ImageUtils.loadImage("Image/WhiteBalance/HTC Legend/鎢絲燈.jpg");

      Spectra spectra = CorrelatedColorTemperature.
          getSpectraOfBlackbodyRadiator(3000);
//      Illuminant illuminant = new Illuminant(spectra);
      Illuminant illuminant = Illuminant.A;

      BufferedImage wbImage = wbt.whiteBalance(image, illuminant);
      ImageUtils.storeJPEGImage("wb.jpg", wbImage);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    /**
     * 用日光白平衡去拍鎢絲燈, 結果造成亮部變青綠色
     * 研判是日光時... R clipping造成.
     * 因為R若沒有clipping, 原始應該是超過255的值, wb後降回255.
     * 由於被clipping, 一開始就是255的值, wb後小於255, 白色主要為G和B, 確實為青
     *
     * 所以日光會有clipping 問題而無法當作常態白平衡...
     */

  }
}
