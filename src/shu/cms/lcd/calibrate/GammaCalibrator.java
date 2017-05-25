package shu.cms.lcd.calibrate;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.hvs.*;
import shu.cms.lcd.*;
import shu.cms.lcd.material.*;
import shu.cms.plot.*;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.lut.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 產生gamma校正的code
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class GammaCalibrator {
  protected LCDModel lcdModel;

  public static void main(String[] args) {
//    LCDTarget lcdTarget = LCDTargetMaterial.getCPT320WF01SC_0904();
    LCDTarget lcdTarget = LCDTargetMaterial.getCPT320WF01SC_0825();
    lcdTarget = lcdTarget.targetFilter.getRamp1021();

//    lcdTarget = LCDTarget.Operator.gradationReverseFix(lcdTarget);
    MultiMatrixModel model = new MultiMatrixModel(lcdTarget);
    model.produceFactor();

//    GammaCalibrator calibrator = new GammaCalibrator(lcdTarget);
    GammaCalibrator calibrator = new GammaCalibrator(model);
    RGBBase.Channel ch = RGBBase.Channel.G;

//    RGB[] result1 = calibrator.smoothCalibrate(ch, 254);
//    RGB[] result2 = calibrator.scale(ch, 254);
//    Plot2D plot = Plot2D.getInstance();
//    for (int x = 0; x < result1.length; x++) {
//      plot.addCacheScatterLinePlot("smooth", x, result1[x].G);
//      plot.addCacheScatterLinePlot("scale", x, result2[x].G);
//    }
//    plot.setVisible();

    double[] gammaCurve = calibrator.getOriginalWhiteGammaCurve(254)[1];
    RGB[] result = calibrator.gammaCalibrate(ch, gammaCurve, 254);
    Plot2D plot = Plot2D.getInstance();
    for (int x = 0; x < result.length; x++) {
      double v = result[x].getValue(ch, RGB.MaxValue.Double255);
      plot.addCacheScatterLinePlot("1", x, v);
      plot.addCacheScatterLinePlot("2", x, (x / 255.) * 254);
    }
//    plot.addLinePlot("", 0, 254, gammaCurve);
    plot.setVisible();
  }

  public GammaCalibrator(LCDModel lcdModel) {
    init(lcdModel);
  }

  protected void init(LCDModel lcdModel) {
    this.lcdModel = lcdModel;
    lcdModel.produceFactor();
    lcdModel.singleChannel.produceRGBPatch();
    lcdModel.correct.produceGammaCorrector();
    /**
     * 視覺評估後的最佳結果是採用Quad內插
     */
    lcdModel.correct.setCorrectorAlgo(Interpolation1DLUT.Algo.
                                      QUADRATIC_POLYNOMIAL);

  }

  public GammaCalibrator(LCDTarget rampTarget) {
    if (rampTarget.getNumber() != LCDTargetBase.Number.Ramp1021 &&
        rampTarget.getNumber() != LCDTargetBase.Number.Ramp1024 &&
        rampTarget.getNumber() != LCDTargetBase.Number.Ramp1792) {
      throw new IllegalArgumentException(
          "rampTarget != Ramp1021/Ramp1024/Ramp1792");
    }
    lcdModel = new MultiMatrixModel(rampTarget);
//    lcdModel.produceFactor();
    init(lcdModel);
  }

  public RGB[] gCodeCalibrate(RGB[] gCodeArray) {
    RGB[] result = RGBArray.deepClone(gCodeArray);

    return result;
  }

  /**
   *
   * @param channel Channel
   * @param maxcode double
   * @return RGB[]
   */
  public RGB[] sRGBCalibrate(RGBBase.Channel channel, double maxcode) {
    if (channel == RGBBase.Channel.W) {
      throw new IllegalArgumentException("RGBBase.Channel.W is not support.");
    }
    RGB[] result = new RGB[256];
    double[] tmpValues = new double[3];
    //對應最大code的最大亮度normal值
    double maxnormal = getMaxNormal(channel, maxcode);

    for (int code = 0; code < 256; code++) {
      tmpValues[channel.getArrayIndex()] = code / 255.;
      //將線性亮度轉成gamma
      double[] normal = RGB.toLinearRGBValues(tmpValues, RGB.ColorSpace.sRGB);
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, RGB.MaxValue.Double1);
      rgb.setValue(channel, normal[channel.getArrayIndex()] * maxnormal);

      //亮度轉code
      lcdModel.correct.gammaUncorrect(rgb);
      //normal轉255
      result[code] = rgb;
    }
    return result;
  }

  public RGB[] LStarCalibrate(RGBBase.Channel channel) {
    RGB[] result = new RGB[256];
    for (int code = 0; code < 256; code++) {
      //將線性亮度轉成gamma
      double[] normal = new double[] {
          code / 255., code / 255., code / 255.};
      double[] rgbValues = RGB.toLinearRGBValues(normal,
                                                 RGB.ColorSpace.LStarRGB);
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB,
                        RGB.MaxValue.Double1);
      rgb.setValue(channel, rgbValues[0]);
      //亮度轉code
      lcdModel.correct.gammaUncorrect(rgb);
      //normal轉255
      result[code] = rgb;
    }
    return result;
  }

  private double findWhiteGamma(double maxWhiteCode) {
    double[][] gammaCurve = getOriginalWhiteGammaCurve(maxWhiteCode);
    double[] input = gammaCurve[0];
    double[] output = gammaCurve[1];

    double gamma = GammaFinder.findingGamma(input, output);
    return gamma;
  }

  private double findGamma(RGBBase.Channel channel, double maxCode) {
    if (!channel.isPrimaryColorChannel()) {
      throw new IllegalArgumentException("Channel " + channel +
                                         " is not support.");
    }

    int max = (int) maxCode;
    double[] input = new double[max];
    double[] output = new double[max];
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB,
                      RGB.MaxValue.Double255);

    for (int x = 0; x < max; x++) {
      rgb.setValue(channel, x);
      lcdModel.correct.gammaCorrect(rgb);
      input[x] = x;
      output[x] = rgb.getValue(channel);
    }

    Maths.normalize(input, input[input.length - 1]);
    Maths.normalize(output, output[output.length - 1]);
    double gamma = GammaFinder.findingGamma(input, output);
    return gamma;
  }

  public RGB[] smoothCalibrate(RGBBase.Channel channel, double maxCode) {
    double gamma = findGamma(channel, maxCode);
    return gammaCalibrate(channel, gamma, maxCode);
  }

  /**
   * 以maxcode的亮度為最大亮度, 線性調整code
   * @param maxcode double
   * @param channel Channel
   * @return RGB[]
   */
  public final static RGB[] scale(RGBBase.Channel channel, double maxcode) {
    RGB[] result = new RGB[256];
    for (int code = 0; code < 256; code++) {
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB,
                        RGB.MaxValue.Double1);
      double scale = (code / 255.) * (maxcode / 255.);
      rgb.setValue(channel, scale);
      result[code] = rgb;
    }
    return result;
  }

  public double[] whiteCalibrateGSDF(RGB whiteRGB) {
    lcdModel.changeMaxValue(whiteRGB);
    CIEXYZ whiteXYZ = lcdModel.getXYZ(whiteRGB, false);
    CIEXYZ flareXYZ = lcdModel.flare.getFlare();
    double whiteY = whiteXYZ.Y;
    double flareY = flareXYZ.Y;
    double[] YArray = new double[256];
    double whiteJNDI = GSDF.DICOM.getJNDIndex(whiteY);
    double flareJNDI = GSDF.DICOM.getJNDIndex(flareY);
    double deltaJNDI = (whiteJNDI - flareJNDI) / 255.;
    double jndi = flareJNDI + deltaJNDI;
    YArray[0] = flareY;
    YArray[255] = whiteY;

    for (int code = 1; code < 255; code++) {
      YArray[code] = GSDF.DICOM.getLuminance(jndi);
      jndi += deltaJNDI;
    }

    return YArray;
  }

  /**
   * 計算white的亮度以及flare(也就是black)的亮度
   * @param whiteRGB RGB
   * @return double[]
   */
  private double[] getWhiteAndFlare(RGB whiteRGB) {
    //準備白的RGB
    RGB whiteClone = (RGB) whiteRGB.clone();
    lcdModel.changeMaxValue(whiteClone);

    double whiteY = lcdModel.getXYZ(whiteClone, false).Y;
    double flareY = lcdModel.flare.getFlare().Y;
    return new double[] {
        whiteY, flareY};
  }

  public double[] sRGBWhiteCalibrate(final RGB whiteRGB) {
    double[] whiteAndFalre = getWhiteAndFlare(whiteRGB);
    double whiteY = whiteAndFalre[0];
    double flareY = whiteAndFalre[1];
    double max = whiteY - flareY;
    double[] YArray = new double[256];

    for (int code = 0; code < 256; code++) {
      RGB rgb = new RGB(RGB.ColorSpace.sRGB, new int[] {code, code, code});
      double Y = rgb.toXYZ().Y;
      YArray[code] = max * Y + flareY;
    }
    return YArray;
  }

  public double[] smoothWhiteCalibrate(RGB whiteRGB, double maxCode) {
    double gamma = findWhiteGamma(maxCode);
    return whiteCalibrate(gamma, whiteRGB);
  }

  public double[][] getOriginalGammaCurve(RGB.Channel channel, double maxCode) {
    int max = (int) maxCode;
    double[] input = new double[max];
    double[] output = new double[max];
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB,
                      RGB.MaxValue.Double255);

    for (int x = 0; x < max; x++) {
      rgb.setValue(channel, x);
      lcdModel.correct.gammaCorrect(rgb);
      input[x] = x;
      output[x] = rgb.getValue(channel);
    }

    Maths.normalize(input, input[input.length - 1]);
    Maths.normalize(output, output[output.length - 1]);
    return new double[][] {
        input, output};
  }

  /**
   * 傳回面板原始的gamma curve
   * @param maxWhiteCode double
   * @return double[][] {input,output}
   */
  public double[][] getOriginalWhiteGammaCurve(double maxWhiteCode) {
    int max = (int) maxWhiteCode;
    double[] input = new double[max];
    double[] output = new double[max];
    LCDTarget target = lcdModel.getLCDTarget();
//    target.getPatch(RGB.Channel.W, 1);

    for (int x = 0; x < max; x++) {
      Patch p = target.getPatch(RGB.Channel.W, x, RGB.MaxValue.Double255);
      input[x] = x;
      output[x] = p.getXYZ().Y;
    }

    input = DoubleArray.minus(input, input[0]);
    Maths.normalize(input, input[input.length - 1]);
    output = DoubleArray.minus(output, output[0]);
    Maths.normalize(output, output[output.length - 1]);
    return new double[][] {
        input, output};
  }

  public double[] whiteCalibrate(double[] gammaCurve,
                                 final RGB whiteRGB) {
    double[] whiteAndFalre = getWhiteAndFlare(whiteRGB);
    double whiteY = whiteAndFalre[0];
    double flareY = whiteAndFalre[1];
    double[] YArray = new double[256];
    double max = whiteY - flareY;

    for (int code = 0; code < 256; code++) {
      YArray[code] = max * gammaCurve[code] + flareY;
    }
    return YArray;
  }

  public double[] whiteCalibrate(double gamma,
                                 final RGB whiteRGB) {
    double[] whiteAndFalre = getWhiteAndFlare(whiteRGB);
    double whiteY = whiteAndFalre[0];
    double flareY = whiteAndFalre[1];
    double[] YArray = new double[256];

    for (int code = 0; code < 256; code++) {
      //將線性亮度轉成gamma
      double normal = Math.pow( (code / 255.), gamma);
      YArray[code] = (whiteY - flareY) * normal + flareY;
    }
    return YArray;
  }

  /**
   * 如果channel設定為W, 則是RGB皆進行校正
   * @param channel Channel
   * @param gamma double
   * @param maxCode double
   * @return RGB[]
   */
  public RGB[] gammaCalibrate(RGBBase.Channel channel, double gamma,
                              double maxCode) {
    if (!channel.isPrimaryColorChannel()) {
      throw new IllegalArgumentException("Channel " + channel +
                                         " is not support.");
    }

    //對應最大code的最大亮度normal值
    double maxnormal = getMaxNormal(channel, maxCode);

    RGB[] result = new RGB[256];

    for (int code = 0; code < 256; code++) {
      //將線性亮度轉成gamma
      double normal = Math.pow( (code / 255.), gamma) * maxnormal;
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB,
                        RGB.MaxValue.Double1);
      rgb.setValue(channel, normal);
      //亮度轉code
      lcdModel.correct.gammaUncorrect(rgb);
      //normal轉255
      result[code] = rgb;
    }
    return result;
  }

  /**
   * 取得maxcode下的亮度對應的normal值
   * @param channel Channel
   * @param maxcode double
   * @return double
   */
  private double getMaxNormal(RGBBase.Channel channel, double maxcode) {
    RGB maxrgb = new RGB(RGB.ColorSpace.unknowRGB,
                         RGB.MaxValue.Double1);
    maxrgb.setValue(channel, maxcode / 255.);
    lcdModel.correct.gammaCorrect(maxrgb);
    //對應最大code的最大亮度normal值
    double maxnormal = maxrgb.getValue(channel);
    return maxnormal;
  }

  public RGB[] gammaCalibrate(RGBBase.Channel channel, double[] gammaCurve,
                              double maxCode) {
    //對應最大code的最大亮度normal值
    double maxnormal = getMaxNormal(channel, maxCode);
    Interpolation1DLUT lut = new Interpolation1DLUT(DoubleArray.buildX(0, 255,
        gammaCurve.length), gammaCurve);

    RGB[] result = new RGB[256];

    for (int code = 0; code < 256; code++) {
      //將線性亮度轉成gamma
      double normal = lut.getValue(code) * maxnormal;
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB,
                        RGB.MaxValue.Double1);
      rgb.setValue(channel, normal);
      //亮度轉code
      lcdModel.correct.gammaUncorrect(rgb);
      //normal轉255
      result[code] = rgb;
    }
    return result;
  }

}
