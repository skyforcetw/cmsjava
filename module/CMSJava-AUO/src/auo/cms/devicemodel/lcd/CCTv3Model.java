package auo.cms.devicemodel.lcd;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.devicemodel.lcd.LCDModelBase.*;
import shu.cms.lcd.*;
import shu.cms.Patch;
import java.util.List;
import shu.cms.plot.Plot2D;
import shu.math.*;
import shu.math.regress.PolynomialRegression;
import shu.cms.util.RGBArray;
import shu.math.lut.Interpolation1DLUT;
import java.awt.Color;
//import vv.cms.lcd.material.Material;
import shu.math.array.*;
///import shu.plot.*;

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
public class CCTv3Model
    extends LCDModel {
  public static void main(String[] args) {
//    modelCompare(args);
//    modelDigging(args);
    modelTest(args);
  }

  public static void modelTest(String[] args) {
//    LCDTarget target = Material.getStoredLCDTarget();
//    target = target.targetFilter.getRamp1021();
//    LCDTarget target = LCDTarget.Measured.measure(LCDTarget.Source.Remote,
//                                                  LCDTarget.Number.Ramp1021, false);
    LCDTarget target = LCDTarget.Instance.getFromCA210Logo("auo_T370HW02",
        LCDTarget.Number.Ramp1021, "091225");
    LCDTarget.Operator.gradationReverseFix(target);

    CCTv3Model cctv3 = new CCTv3Model(target);
    cctv3.produceFactor();
    PLCCModel plcc = new PLCCModel(target);
    plcc.produceFactor();
    double gamma = 2.4;

    CIEXYZ white = plcc.getWhiteXYZ(false);
    CIEXYZ black = plcc.flare.getFlare();
    double normalY = white.Y - black.Y;
    RGB[] dgcodePLCC = new RGB[256];
    for (int x = 0; x < 256; x++) {
      double normal = x / 256.;
      double Y = Math.pow(normal, gamma) * normalY + black.Y;
      CIEXYZ targetXYZ = (CIEXYZ) white.clone();
      targetXYZ.scaleY(Y);
      dgcodePLCC[x] = plcc.getRGB(targetXYZ, false);
    }

//    RGB[] dgcode = model.getDigitalGammaCode(gamma, 30, 50);
    RGB[] dgcode = cctv3.getDigitalGammaCode(gamma, 50);
    RGBArray.storeAUOExcel(dgcodePLCC, "plcc.xls", RGB.MaxValue.Int12Bit);
    RGBArray.storeAUOExcel(dgcode, "cctv3.xls", RGB.MaxValue.Int12Bit);

//    for (RGB rgb : dgcodePLCC) {
//      CIEXYZ XYZ = plcc.getXYZ(rgb, false);
//      System.out.println(rgb.getValue(RGB.Channel.W) + " " + XYZ.getCCT());
//    }
  }

  public static void modelDigging(String[] args) {
//    LCDTarget target = Material.getStoredLCDTarget();
//    target = target.targetFilter.getRamp1021();
//    CCTv3Model cctv3 = new CCTv3Model(target);
//    cctv3.produceFactor();
//
//    PolynomialRegression regress = cctv3.regress;
//    double[][] rgbRatio = DoubleArray.transpose(cctv3.whiteRGBRatio);
//    double[] whiteYArray = cctv3.relativeWhiteYArray;
//    int size = whiteYArray.length;
//    Plot2D plot = Plot2D.getInstance();
//    for (int x = 0; x < size; x++) {
//      plot.addCacheScatterLinePlot("R", Color.red, rgbRatio[0][x],
//                                   whiteYArray[x]);
//      plot.addCacheScatterLinePlot("G", Color.green, rgbRatio[1][x],
//                                   whiteYArray[x]);
//      plot.addCacheScatterLinePlot("B", Color.blue, rgbRatio[2][x],
//                                   whiteYArray[x]);
//      double predictW = regress.getPredict(new double[] {rgbRatio[0][x]})[0];
//      plot.addCacheScatterLinePlot("regress", Color.black, rgbRatio[0][x],
//                                   predictW);
//    }
////    plot.addLinePlot("R", Color.red, 0, rgbRatio[0].length, rgbRatio[0]);
////    plot.addLinePlot("G", Color.green, 0, rgbRatio[0].length, rgbRatio[1]);
////    plot.addLinePlot("B", Color.blue, 0, rgbRatio[0].length, rgbRatio[2]);
//
//
//    plot.setVisible();
//    plot.setAxeLabel(0, "W_R W_G W_B");
//    plot.setAxeLabel(1, "W_Y");
//    plot.setFixedBounds(0, 0, 255);
//    plot.setFixedBounds(1, 0, 700);
//    plot.setLinePlotWidth(2);
//
//    Plot2D plot2 = Plot2D.getInstance();
//    RGB[] dgcode = cctv3.getDigitalGammaCode(2.4, 50);
//    int size2 = dgcode.length;
//    for (int x = 0; x < size2; x++) {
//      RGB rgb = dgcode[x];
//      plot2.addCacheScatterLinePlot("R", Color.red, x, rgb.R);
//      plot2.addCacheScatterLinePlot("G", Color.green, x, rgb.G);
//      plot2.addCacheScatterLinePlot("B", Color.blue, x, rgb.B);
//    }
//    plot2.setVisible();
//    plot2.setLinePlotWidth(2);
//    plot2.setAxeLabel(0, "input");
//    plot2.setAxeLabel(1, "output");
//    plot2.setFixedBounds(0, 0, 255);
//    plot2.setFixedBounds(1, 0, 255);

  }

  public static void modelCompare(String[] args) {
//    LCDTarget target = Material.getStoredLCDTarget();
//    target = target.targetFilter.getRamp1021();
//
//    CCTv3Model cctv3 = new CCTv3Model(target);
//    cctv3.produceFactor();
//    PLCCModel plcc = new PLCCModel(target);
//    plcc.produceFactor();
//    double gamma = 2.2;
//
//    CIEXYZ white = plcc.getWhiteXYZ(false);
//    CIEXYZ black = plcc.flare.getFlare();
//    double normalY = white.Y - black.Y;
//    RGB[] dgcodePLCC = new RGB[256];
//    for (int x = 0; x < 256; x++) {
//      double normal = x / 256.;
//      double Y = Math.pow(normal, gamma) * normalY + black.Y;
//      CIEXYZ targetXYZ = (CIEXYZ) white.clone();
//      targetXYZ.scaleY(Y);
//      dgcodePLCC[x] = plcc.getRGB(targetXYZ, false);
//    }
//
////    RGB[] dgcode = model.getDigitalGammaCode(2.2, 30, 50);
//    RGB[] dgcode = cctv3.getDigitalGammaCode(gamma, 50);
//
//    int size = dgcode.length;
//    for (RGB.Channel ch : RGB.Channel.RGBChannel) {
//      Plot2D plot = Plot2D.getInstance(ch.name());
//      for (int x = 0; x < size; x++) {
//        plot.addCacheScatterLinePlot("CCTv3", Color.red, x,
//                                     dgcode[x].getValue(ch));
//        plot.addCacheScatterLinePlot("PLCC", Color.black, x,
//                                     dgcodePLCC[x].getValue(ch));
//        plot.addCacheScatterLinePlot("diff", Color.cyan, x,
//                                     dgcode[x].getValue(ch) -
//                                     dgcodePLCC[x].getValue(ch));
//      }
//
//      plot.addLegend();
//      plot.setVisible();
//      plot.setFixedBounds(0, 0, 255);
//      plot.setFixedBounds(1, 0, 255);
//      plot.setAxeLabel(0, "input");
//      plot.setAxeLabel(1, "input");
//      plot.setLinePlotWidth(2);
//    }
//
//    Plot2D plotgamma = Plot2D.getInstance("Gamma");
//    for (RGB.Channel ch : RGB.Channel.RGBChannel) {
//      double max = target.getSaturatedChannelPatch(ch).getXYZ().Y - black.Y;
//      for (int x = 0; x < size; x++) {
//        Patch p = target.getPatch(ch, x, RGB.MaxValue.Int8Bit);
//        plotgamma.addCacheScatterLinePlot(ch.name(), ch.color, x,
//                                          (p.getXYZ().Y - black.Y) / max);
//      }
//    }
//    plotgamma.setVisible();
//
//    Plot2D plot = Plot2D.getInstance("RGB ratio");
//    for (int x = 0; x < size; x++) {
//      Patch p = target.getPatch(RGB.Channel.W, x, RGB.MaxValue.Int8Bit);
//      RGB rgb = cctv3.getLinearRGB(p.getXYZ(), false);
//      plot.addCacheScatterLinePlot("R", Color.red, x, rgb.R);
//      plot.addCacheScatterLinePlot("G", Color.green, x, rgb.G);
//      plot.addCacheScatterLinePlot("B", Color.blue, x, rgb.B);
//    }
//    plot.setVisible();
  }

  public CCTv3Model(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  private RGB[] getDigitalGammaCode0(double gamma) {
    CIEXYZ flare = this.flare.getFlare();
    CIEXYZ whitePoint = this.getWhiteXYZ(false);
    CIEXYZ relativeXYZ = CIEXYZ.minus(whitePoint, flare);

    RGB[] dgRGBArray = new RGB[256];
    for (int x = 1; x < 256; x++) {
      double normal = Math.pow(x / 255., gamma);
      CIEXYZ targetXYZ = (CIEXYZ) relativeXYZ.clone();
      targetXYZ.times(normal);
      targetXYZ = CIEXYZ.plus(targetXYZ, flare);
      RGB rgb = getRGB(targetXYZ, false);
      dgRGBArray[x] = rgb;
    }
    dgRGBArray[0] = RGB.Black;
    return dgRGBArray;
  }

  public RGB[] getDigitalGammaCode(double gamma, int rbInterpCode) {
    RGB[] dgRGBArray = getDigitalGammaCode0(gamma);
    RGB interpRGB = dgRGBArray[rbInterpCode];
    for (int x = 1; x < rbInterpCode; x++) {
      RGB rgb = dgRGBArray[x];
      rgb.R = Interpolation.linear(0, rbInterpCode, 0, interpRGB.R, x);
      rgb.G = Interpolation.linear(0, rbInterpCode, 0, interpRGB.G, x);
      rgb.B = Interpolation.linear(0, rbInterpCode, 0, interpRGB.B, x);
    }
    return dgRGBArray;
  }

  public RGB[] getDigitalGammaCode(double gamma, int p1, int p2) {
    RGB[] dgRGBArray = getDigitalGammaCode0(gamma);

    RGB p1RGB = dgRGBArray[p1];
    RGB p2RGB = dgRGBArray[p2];

    for (int x = 1; x <= p1; x++) {
      RGB rgb = dgRGBArray[x];
      rgb.G = Interpolation.linear(0, p1, 0, p1RGB.G, x);
      rgb.R = rgb.B = rgb.G;
    }
    for (int x = p1 + 1; x < p2; x++) {
      RGB rgb = dgRGBArray[x];
      double gRatio = (rgb.G - p1RGB.G) / (p2RGB.G - p1RGB.G);
      for (RGB.Channel ch : new RGB.Channel[] {RGB.Channel.R, RGB.Channel.B}) {
        double value = p1RGB.getValue(ch) +
            (p2RGB.getValue(ch) - p1RGB.getValue(ch)) * gRatio;
        rgb.setValue(ch, value);
      }
    }
    return dgRGBArray;
  }

  /**
   * 計算RGB,反推模式
   *
   * @param relativeXYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ relativeXYZ, Factor[] factor) {
    double luminance = coefscd[0] * relativeXYZ.Y + coefscd[1];
    //修正在範圍內
    double rLuminance = rgbInterpolator[0].correctValueInRange(luminance);
    double gLuminance = rgbInterpolator[1].correctValueInRange(luminance);
    double bLuminance = rgbInterpolator[2].correctValueInRange(luminance);
    //查表
    double r = rgbInterpolator[0].getKey(rLuminance);
    double g = rgbInterpolator[1].getKey(gLuminance);
    double b = rgbInterpolator[2].getKey(bLuminance);
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, new double[] {r, g, b},
                      RGB.MaxValue.Double255);
    return rgb;
  }

  /**
   * 計算XYZ,前導模式
   *
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, Factor[] factor) {
    return multiMatrixModel.getXYZ(rgb, true);
//    throw new UnsupportedOperationException();
  }

  /**
   * 求係數
   *
   * @return Factor[]
   */
  protected Factor[] _produceFactor() {
    multiMatrixModel = new MultiMatrixModel(lcdTarget);
    multiMatrixModel.produceFactor();

    List<Patch> patchList = lcdTarget.filter.grayPatch(true);
    int size = patchList.size();
    whiteRGBRatio = new double[size][];
    relativeWhiteYArray = new double[size];

    RGB[] rgbCodeArray = new RGB[size];
    RGB[] rgbLuminanceArray = new RGB[size];

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      rgbCodeArray[x] = p.getRGB();
      CIEXYZ XYZ = p.getXYZ();
      CIEXYZ fromXYZ = this.fromXYZ(XYZ, true);
      RGB lumiRGB = this.matries.XYZToRGBByMaxMatrix(fromXYZ);
      rgbLuminanceArray[x] = lumiRGB;
      whiteRGBRatio[x] = lumiRGB.getValues();
      relativeWhiteYArray[x] = fromXYZ.Y;
    }

    rgbInterpolator = RGBArray.getRGBInterpLUT(rgbCodeArray, rgbLuminanceArray);

    regress = new PolynomialRegression(whiteRGBRatio,
                                       DoubleArray.transpose(
                                           relativeWhiteYArray),
//                                       Polynomial.COEF_3.BY_3C);
                                       Polynomial.COEF_3.BY_19C);
    regress.regress();
    coefs = regress.getCoefs()[0];
    double a123 = coefs[1] + coefs[2] + coefs[3];
    coefscd = new double[] {
        1 / a123, -coefs[0] / a123};

    return new Factor[3];
  }

  private PolynomialRegression regress;

  public static enum RegressMethod {
    Whole, HeadAndTail
  }

//  private RegressMethod regressMethod = RegressMethod.Whole;
//  private RegressMethod regressMethod = RegressMethod.HeadAndTail;

  private double[] coefscd;
  private MultiMatrixModel multiMatrixModel;
  private Interpolation1DLUT[] rgbInterpolator;
  private double[] coefs;
  private double[][] whiteRGBRatio;
  private double[] relativeWhiteYArray;

  /**
   * getDescription
   *
   * @return String
   * @todo Implement this shu.cms.devicemodel.DeviceCharacterizationModel
   *   method
   */
  public String getDescription() {
    return "";
  }
}
