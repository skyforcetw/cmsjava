package auo.cms.hsv.saturation.backup;

import java.io.*;
import java.util.*;
import javax.vecmath.*;

import java.awt.*;
import java.awt.image.*;

import org.apache.commons.io.*;
import auo.cms.hsv.old.*;
import auo.cms.hsv.saturation.*;
import auo.cms.hsv.value.backup.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.cms.profile.*;
import shu.image.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.geometry.*;
import shu.math.lut.*;
import shu.math.regress.*;
//import shu.plot.*;

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
public class SaturationImageAdjustor {
  private Interpolation1DLUT saturationAdjustmentLUT = null;
  private Interpolation1DLUT hueAdjustmentLUT = null;
  private Interpolation1DLUT valueAdjustmentLUT = null;
  public SaturationImageAdjustor(double[] saturationAdjustValue) {
    if (saturationAdjustValue.length == 6 || saturationAdjustValue.length == 24) {
      saturationAdjustmentLUT = getInterpolation1DLUT(saturationAdjustValue);
//      for (int x = 0; x < 360; x++) {
//        System.out.println(x + " " + saturationAdjustmentLUT.getValue(x));
//      }
//      System.out.println("");
    }
    else {
      throw new IllegalArgumentException("");
    }

  }

  public SaturationImageAdjustor(double[] hueAdjustValue,
                                 double[] saturationAdjustValue,
                                 double[] valueAdjustValue,
                                 SaturationFormula saturationFormula) {
//    if (hueAdjustValue.length != 6 ||
//        saturationAdjustValue.length != 6 ||
//        valueAdjustValue.length != 6) {
//      throw new IllegalArgumentException("");
//    }
    hueAdjustmentLUT = getInterpolation1DLUT(hueAdjustValue);
    saturationAdjustmentLUT = getInterpolation1DLUT(saturationAdjustValue);
    valueAdjustmentLUT = getInterpolation1DLUT(valueAdjustValue);
    this.saturationFormula = saturationFormula;
  }

  private LCDModel model;
  private RGB.ColorSpace targetColorSpace;
  public SaturationFormula saturationFormula;
  public SaturationImageAdjustor(double[] saturationAdjustArray, LCDModel model,
                                 RGB.ColorSpace targetColorSpace,
                                 SaturationFormula saturationFormula) {
    this(saturationAdjustArray);
    this.model = model;
    this.targetColorSpace = targetColorSpace;
    this.saturationFormula = saturationFormula;
  }

  public SaturationImageAdjustor(double[][] hsvAdjustArray, LCDModel model,
                                 RGB.ColorSpace targetColorSpace) {
    this(hsvAdjustArray[0], hsvAdjustArray[1], hsvAdjustArray[2], null);
    this.model = model;
    this.targetColorSpace = targetColorSpace;
  }

//  private double getInterpolationAdjustValue(double h) {
//    return saturationAdjustmentLUT.getValue(h);
//  }

  //============================================================================
  // setting
  //============================================================================
  private boolean halfProcess = false;
  private boolean simulation = false;
  private boolean hueProcess = false;
  private boolean saturationProcess = true;
  private boolean valueProcess = true;
  //============================================================================

  public void setSaturationProcess(boolean process) {
    this.saturationProcess = process;
  }

  public void setValueProcess(boolean process) {
    this.valueProcess = process;
  }

  public void setHalfProcess(boolean half) {
    this.halfProcess = half;
  }

  public void setSimulation(boolean enable) {
    if (true == enable && model == null) {
      throw new IllegalStateException("model == null");
    }
    this.simulation = enable;
  }

  private boolean hsvClipping = false;
  public void setHSVClipping(boolean clip) {
    this.hsvClipping = clip;
  }

  public long hueShiftTimes = 0;
  public long totalPixels = 0;
  public BufferedImage getAdjustBufferedImage(BufferedImage originalImage) {
    return getAdjustBufferedImage(originalImage, saturationFormula);
  }

//  private static boolean doSaturationAdjust = true;
//  private static boolean doValueAdjust = false;
//  private static boolean doHueAdjust = false;

  public BufferedImage getAdjustBufferedImage(BufferedImage originalImage,
                                              SaturationFormula formula) {
    BufferedImage clone = ImageUtils.cloneBufferedImage(originalImage);
    int w = halfProcess ? clone.getWidth() / 2 : clone.getWidth();
    int h = clone.getHeight();
    WritableRaster raster = clone.getRaster();
    double[] dpixel = new double[3];
    RGB rgb = new RGB(RGB.ColorSpace.sRGB, RGB.MaxValue.Double255);
    double luminance = model != null ? model.getLuminance().Y : -1;

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        raster.getPixel(x, y, dpixel);

        rgb.setValues(dpixel);
        HSV hsv = new HSV(rgb);

        //經過調整後的Hue
        if (hueProcess) {
          //調整的hue
          double adjustHue = hueAdjustmentLUT.getValue(hsv.H);

//          adjustHue = ( (int) (adjustHue / 360. * 768)) / 768. * 360;
          hsv.H = adjustHue;
        }
        //經過調整後的Saturation
        if (saturationProcess) {
          //調整的saturation
          double adjustSaturation = saturationAdjustmentLUT.getValue(hsv.H);
          hsv.S = formula.getSaturartion(hsv.S, adjustSaturation);
          //HSV clipping
          if (hsvClipping) {
            hsv.S = (hsv.S > 100) ? 100 : hsv.S;
            hsv.S = (hsv.S < 0) ? 0 : hsv.S;
          }
        }
        if (valueProcess) {
          //調整的value
          double adjustValue = valueAdjustmentLUT.getValue(hsv.H);
          short max = (short) (Maths.max(dpixel) * 4);
          short min = (short) (Maths.min(dpixel) * 4);
          short result = ValuePrecisionEvaluator.getV(max, min,
              (byte) adjustValue);
          double doubleV = result / 1023. * 100.;
          hsv.V = doubleV;
        }

        RGB rgb2 = hsv.toRGB();
        //rgb clipping
        rgb2.clip();
        HSV hsv2 = new HSV(rgb2);
        totalPixels++;
        if (Math.abs(hsv2.H - hsv.H) > 1) {
          hueShiftTimes++;
        }

        if (simulation && model != null) {
          //====================================================================
          // 調整完後進行模擬
          //====================================================================
          CIEXYZ XYZ = model.getXYZ(rgb2, false);
          XYZ.times(1. / luminance);
          RGB targetRGB = new RGB(targetColorSpace, XYZ);
          targetRGB.clip();
          targetRGB.getValues(dpixel, RGB.MaxValue.Double255);
          //====================================================================
        }
        else {
          rgb2.getValues(dpixel, RGB.MaxValue.Double255);
        }
        raster.setPixel(x, y, dpixel);
      }
    }
    return clone;
  }

  private static Interpolation1DLUT getInterpolation1DLUT(double[]
      hueAdjustValue) {

    int size = hueAdjustValue.length;
    double[] original = new double[size + 1];

    for (int x = 0; x < size; x++) {
      original[x] = hueAdjustValue[x];
    }
    original[size] = hueAdjustValue[0];

    int size_1 = size + 1;
    double[] hueArray = new double[size_1];
    int eachHue = 360 / size;
    for (int x = 0; x < size_1; x++) {
      hueArray[x] = x * eachHue;
    }
    Interpolation1DLUT lut = new Interpolation1DLUT(hueArray, original,
        Interpolation1DLUT.Algo.LINEAR);

    double[] keys = new double[25];
    double[] values = new double[25];
    for (int x = 0; x < 25; x++) {
      double hue = x * 15;
      keys[x] = hue;
      double s = lut.getValue(hue);
      values[x] = s;
    }

    Interpolation1DLUT lut2 = new Interpolation1DLUT(keys, values,
        Interpolation1DLUT.Algo.LINEAR);
    return lut2;
  }

  public static void main(String[] args) throws Exception {
//    imgTest(args);
//    methodTest(args);
//    makePattern(args);
    allImgTest(args);
//    dump(args);
  }

  public static void dump(String[] args) {

  }

  public static void methodTest(String[] args) {
    Plot2D plot = Plot2D.getInstance("method test");
    SaturationFormula richard3 = new RichardFormula(33);
    SaturationFormula richard5 = new RichardFormula(50);
    SaturationFormula richard6 = new RichardFormula(67);
    CombineFormula c = new CombineFormula(CombineFormula.Type.Type2);
    System.out.println(richard3.getAdjustValue(20,
                                               richard3.getSaturartion(20, 5)));
    System.out.println(richard3.getAdjustValue(77,
                                               richard3.getSaturartion(77, 5)));

    System.out.println(richard5.getAdjustValue(20,
                                               richard5.getSaturartion(20, 5)));
    System.out.println(richard5.getAdjustValue(77,
                                               richard5.getSaturartion(77, 5)));

    System.out.println(richard6.getAdjustValue(20,
                                               richard6.getSaturartion(20, 5)));
    System.out.println(richard6.getAdjustValue(77,
                                               richard6.getSaturartion(77, 5)));

    for (int x = 0; x <= 100; x++) {
//      plot.addCacheScatterLinePlot("gain", x, gain.getSaturartion(x, 1.5));
//      plot.addCacheScatterLinePlot("m", x, modified.getSaturartion(x, 15));
//      plot.addCacheScatterLinePlot("m'", x, modified2.getSaturartion(x, 15));

      plot.addCacheScatterLinePlot("richard3", x,
                                   richard3.getSaturartion(x, 5) - x);
      plot.addCacheScatterLinePlot("richard5", x,
                                   richard5.getSaturartion(x, 5) - x);
      plot.addCacheScatterLinePlot("richard6", x,
                                   richard6.getSaturartion(x, 5) - x);
      plot.addCacheScatterLinePlot("combine", x,
                                   c.getSaturartion(x, 5) - x);
//      plot.addCacheScatterLinePlot("m", x,
//                                   modified.getSaturartion(x, 5.737704918032783) -
//                                   x);
//      plot.addCacheScatterLinePlot("m'", x,
//                                   modified2.getSaturartion(x,
//          5.737704918032783) -
//                                   x);

    }
    plot.setVisible();
  }

  public static RGB.ColorSpace getTargetColorSpace(LCDTarget target,
      RGB.ColorSpace r) {
    Patch bpatch = target.getSaturatedChannelPatch(RGB.Channel.B);
    CIExyY whitexyY = new CIExyY(target.getWhitePatch().getXYZ());
    CIEXYZ lcdblueXYZ = bpatch.getNormalizedXYZ();
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(r, "");
    double[] blueXYZValues = pcs.toCIEXYZValues(new double[] {0, 0, 1});
    CIEXYZ blueXYZ = new CIEXYZ(blueXYZValues);

    CIExyY lcdbluexyY = new CIExyY(lcdblueXYZ);
    CIExyY bluexyY = new CIExyY(blueXYZ);

    double d = Geometry.getDistance(new Point2d(lcdbluexyY.x, lcdbluexyY.y),
                                    new Point2d(bluexyY.x, bluexyY.y));
    PolynomialRegression regress = new PolynomialRegression(new double[] {
        lcdbluexyY.x, whitexyY.x}, new double[] {lcdbluexyY.y, whitexyY.y},
        Polynomial.COEF_1.BY_1C);
    regress.regress();
    double xdist = whitexyY.x - lcdbluexyY.x;
    double wbdist = Geometry.getDistance(new Point2d(lcdbluexyY.x, lcdbluexyY.y),
                                         new Point2d(whitexyY.x, whitexyY.y));
    double deltax = (d / wbdist) * xdist;
    double x = lcdbluexyY.x - deltax;
    double predict = regress.getPredict(new double[] {x})[0];
    RGB.ColorSpace result = new RGB.ColorSpace(Illuminant.D65,
                                               2.2, r.rx, r.ry, r.gx, r.gy, x,
                                               predict);
    return result;
  }

  private static void lineTester() {

    LinearFunction l = LinearFunction.getInstance(new double[] {1, 2},
                                                  new double[] {10, 5});
    Plot2D plot = Plot2D.getInstance();
    for (int x = 0; x <= 10; x++) {
      plot.addCacheScatterLinePlot("", x, l.getY(x));
    }
    plot.setVisible();

  }

  public static void allImgTest(String[] args) {

    //========================================================================
    // panel setting
    //========================================================================
    LCDTarget targets[] = new LCDTarget[] {
        LCDTarget.Instance.getFromAUORampXLS(
            "sRGB Adjust Evaluation/45% DG On.xls"),
//        LCDTarget.Instance.getFromAUORampXLS(
//            "sRGB Adjust Evaluation/60% DG On.xls",
//            LCDTarget.Number.Ramp256_6Bit),
//
    };
    //========================================================================

    //========================================================================
    // target color space setting
    //========================================================================
    RGB.ColorSpace targetColorspaces[] = new RGB.ColorSpace[] {
//        RGB.ColorSpace.sRGB_gamma22,
        getTargetColorSpace(targets[0], RGB.ColorSpace.sRGB_gamma22),
//        getTargetColorSpace(targets[0], RGB.ColorSpace.AdobeRGB)
    };
    //========================================================================

//    int size = 6;
//    double[][] saturationAdjustArray = new double[size][];
//    double[] valueAdjustArray = null;
//    double[] hueAdjustArray = null;
//    String dirname = "D:\\軟體\\nobody zone\\Pattern\\skyforce Pattern Collect\\Saturation Evaluation Picture\\Duplicate";
//    String dirname = "D:\\軟體\\nobody zone\\Pattern\\skyforce Pattern Collect\\Saturation Evaluation Picture2\\Duplicate";
//    String dirname = "D:\\軟體\\nobody zone\\Pattern\\skyforce Pattern Collect";
//    String dirname = "D:\\軟體\\nobody zone\\Pattern\\skyforce Pattern Collect\\Saturation Evaluation Picture4\\Duplicate";
    String dirname = "D:\\軟體\\nobody zone\\Pattern\\skyforce Pattern Collect\\Saturation  Test Picture\\Duplicate";

    //==========================================================================
    // setting
    //==========================================================================
    String targetDirname = "hsv";
    boolean halfProc = false;
    boolean panelSimulation = false;
    boolean hsvClipping = false;
    boolean processImage = true;
    boolean drawCurvePlot = false;
    boolean drawLocus = false;

    // adjust setting
    boolean doSaturationProcess = true;
    boolean doValueProcess = false;
    //==========================================================================

    HSVAdjustProducer.TargetPatch targetPatch = HSVAdjustProducer.TargetPatch.
        Integrated;
    ArrayList<Plot2D> plotList = new ArrayList<Plot2D> ();

    try {

      for (LCDTarget target : targets) {
        String targetBasename = FilenameUtils.getBaseName(target.getFilename());
        LCDTarget.Operator.gradationReverseFix(target);
        target.changeMaxValue(RGB.MaxValue.Int8Bit);
        LCDModel model = new MultiMatrixModel(target);
        model.produceFactor();
        model.setAutoRGBChangeMaxValue(true);
        model = panelSimulation ? model : null;

        //======================================================================

        for (RGB.ColorSpace targetColorspace : targetColorspaces) {
          String colorSpaceName = targetColorspace == targetColorspaces[0] ?
              "sRGB" : "aRGB";
          System.out.println(target.getFilename() + " " + colorSpaceName);
          //====================================================================
          // draw locus
          //====================================================================
          if (drawLocus) {
            LocusPlot lplot = new LocusPlot();
            CIExyY rxyY = new CIExyY(model.getXYZ(new RGB(255, 0, 0), false));
            CIExyY gxyY = new CIExyY(model.getXYZ(new RGB(0, 255, 0), false));
            CIExyY bxyY = new CIExyY(model.getXYZ(new RGB(0, 0, 255), false));
            RGB.ColorSpace cs = targetColorspace;
            CIExyY srxyY = new CIExyY(new RGB(cs, new int[] {255, 0, 0}).toXYZ());
            CIExyY sgxyY = new CIExyY(new RGB(cs, new int[] {0, 255, 0}).toXYZ());
            CIExyY sbxyY = new CIExyY(new RGB(cs, new int[] {0, 0, 255}).toXYZ());

            lplot.drawGamutTriangle("LCD", rxyY, gxyY, bxyY, Color.red,
                                    LocusPlot.xyTrasnfer);
            lplot.drawGamutTriangle("sRGB", srxyY, sgxyY, sbxyY, Color.green,
                                    LocusPlot.xyTrasnfer);
            Plot2D p = lplot.getPlot2D();
            p.addLegend();
            lplot.setVisible();
            p.setFixedBounds(0, 0, 1);
            p.setFixedBounds(1, 0, 1);
          }
          //====================================================================
          IntegerSaturationFormula formula7 = new IntegerSaturationFormula( (byte)
              7, 4);
          SaturationFormula[] formulas = new SaturationFormula[] {
              formula7};
          int formulaLength = formulas.length;
          double[][] saturationAdjustArray = new double[formulaLength][];
          double[][] valueAdjustArray = new double[formulaLength][];
          double[][] hueAdjustArray = new double[formulaLength][];

          HSVAdjustProducer producer = new
              HSVAdjustProducer(targetColorspace, model, targetPatch);

          for (int x = 0; x < formulaLength; x++) {
            if (panelSimulation) {
              saturationAdjustArray[x] = producer.getSaturationAdjustArray(
                  formulas[x]);
              hueAdjustArray[x] = producer.getHueAdjustArray();
              valueAdjustArray[x] = producer.getValueAdjustArray();
            }
            else {
              hueAdjustArray[x] = new double[24];
              for (int h = 0; h < 24; h++) {
                hueAdjustArray[x][h] = h * 15;
              }

              saturationAdjustArray[x] = new double[] {
                  63, -63, 63, -63, 63, -63,
                  63, -63, 63, -63, 63, -63,
                  63, -63, 63, -63, 63, -63,
                  63, -63, 63, -63, 63, -63, };

              valueAdjustArray[x] = new double[] {
                  63, -63, 63, -63, 63, -63,
                  63, -63, 63, -63, 63, -63,
                  63, -63, 63, -63, 63, -63,
                  63, -63, 63, -63, 63, -63, };

            }
          }

          for (int x = 0; x < formulaLength; x++) {
            double[] adjust = saturationAdjustArray[x];
            System.out.println(formulas[x].getName() + "\nSat: " +
                               Arrays.toString(adjust) + " max:" +
                               DoubleArray.max(adjust));
            double[] hueAdjust = hueAdjustArray[x];
            System.out.println("Hue: " + Arrays.toString(hueAdjust));
            double[] valueAdjust = valueAdjustArray[x];
            System.out.println("Val: " + Arrays.toString(valueAdjust));

          }

          //====================================================================
          // Adjustor setting
          //====================================================================
          SaturationImageAdjustor[] adjustors = new SaturationImageAdjustor[
              formulaLength];

          for (int x = 0; x < formulaLength; x++) {
            if (panelSimulation) {
              //面板模擬
              adjustors[x] = new SaturationImageAdjustor(
                  saturationAdjustArray[x], model, targetColorspace, formulas[x]);
            }
            else {
              //僅處理圖像
              adjustors[x] = new SaturationImageAdjustor(
                  hueAdjustArray[x], saturationAdjustArray[x],
                  valueAdjustArray[x], formulas[x]);
            }

            adjustors[x].setHalfProcess(halfProc);
            adjustors[x].setSimulation(panelSimulation);
            adjustors[x].setHSVClipping(hsvClipping);
            adjustors[x].setSaturationProcess(doSaturationProcess);
            adjustors[x].setValueProcess(doValueProcess);
          }

          //====================================================================
          // curve compare
          //====================================================================
          if (drawCurvePlot) {
            Plot2D[] plots = new Plot2D[6];
            for (int x = 0; x < 6; x++) {
              plots[x] = Plot2D.getInstance(target.getFilename() + " " +
                                            colorSpaceName + ": " +
                                            Integer.toString(x * 60));
              plotList.add(plots[x]);
            }

            for (int hueIndex = 0; hueIndex < 6; hueIndex++) {
              for (int formulaindex = 0; formulaindex < formulaLength;
                   formulaindex++) {
                SaturationFormula formula = formulas[formulaindex];
                for (int sat = 0; sat <= 100; sat++) {
                  double s0 = formulas[formulaindex].getSaturartion(sat,
                      saturationAdjustArray[formulaindex][hueIndex]);
                  plots[hueIndex].addCacheScatterLinePlot(formula.getName(),
                      sat, s0);
                }
              }
            }

            for (int x = 0; x < 6; x++) {
              plots[x].addLegend();
              plots[x].setVisible();
              plots[x].setFixedBounds(1, 0, 100);
            }
          }
          //====================================================================


          //====================================================================

          if (processImage) {
            for (File file : ImageUtils.listImageFiles(dirname)) {

              BufferedImage img = ImageUtils.loadImage(file.getAbsolutePath());
              String baseName = FilenameUtils.getBaseName(file.getName());
              System.out.println(baseName);
              String storeDirname = targetDirname + "/" + targetBasename + "/";

              //==================================================================
              for (SaturationImageAdjustor adjustor : adjustors) {
                String formulaName = adjustor.saturationFormula.getName();
                BufferedImage result = adjustor.getAdjustBufferedImage(img);
                ImageUtils.storeTIFFImage(storeDirname + baseName + "_" +
                                          formulaName + ".tif", result);
              }
            }
          }
//          System.out.println(adjustorGain.hueShiftTimes + " / " +
//                             adjustorGain.totalPixels);
        }
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

    int plotSize = plotList.size();
    Plot2D[] plotArray = new Plot2D[plotSize];
    plotList.toArray(plotArray);
    for (Plot2D plot : plotArray) {
//      JMathPlot2D p = (JMathPlot2D) ( (PlotWrapperInterface) plot).
//          getOriginalPlot();
//      p.removePlotToolBar();
    }
//    PlotUtils.arrange(plotArray, 4, 3);
  }

  public static void imgTest(String[] args) {
    String filename = "a044.jpg";

    SaturationImageAdjustor adjustor = new SaturationImageAdjustor(new double[] {
        1.5, .5, .5, .5, .5, .5});
    BufferedImage img = null;
    try {
      img = ImageUtils.loadImage(filename);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    BufferedImage processed = adjustor.getAdjustBufferedImage(img, gain);
    try {
      File file = new File(filename);
      ImageUtils.storeTIFFImage("hsv/" + file.getName(), img);
      ImageUtils.storeTIFFImage("hsv/_" + file.getName(), processed);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  static SaturationFormula bypass = new ByPassFormula();
  static SaturationFormula gain = new GainFormula();
  static SaturationFormula quadratic = new QuadraticFormula();
  static SaturationFormula cubic = new CubicFormula();
//  static SaturationFormula modified12 = new Modified12Formula();
//  static SaturationFormula modified3 = new Modified3Formula();
  static SaturationFormula twoQuadratic = new CombineFormula(CombineFormula.
      Type.Type2);
//  static SaturationFormula gaincombine = new GainCombineFormula();
//  static SaturationFormula richard = new RichardFormula(67);
  static SaturationFormula richard33 = new RichardFormula(33);
  static SaturationFormula richard50 = new RichardFormula(50);
  static SaturationFormula richard67 = new RichardFormula(67);
  static SaturationFormula richard80 = new RichardFormula(80);
}

class ByPassFormula
    implements SaturationFormula {
  public double getSaturartion(double originalSaturation, double adjustValue) {
    return originalSaturation;
  }

  public double getAdjustValue(double originalSaturation, double newSaturation) {
    return 0;
  }

  public String getName() {
    return "bypass";
  }

  public short getSaturartion(short originalSaturation, short adjustValue) {
    throw new UnsupportedOperationException();
  }
}

class GainFormula
    implements SaturationFormula {
  public double getSaturartion(double originalSaturation, double adjustValue) {
    double s = originalSaturation * adjustValue;
//    s = s > 100 ? 100 : s;
    return s;
  }

  public double getAdjustValue(double originalSaturation, double newSaturation) {
    return newSaturation / originalSaturation;
  }

  public String getName() {
    return "gain__";
  }

  public short getSaturartion(short originalSaturation, short adjustValue) {
    throw new UnsupportedOperationException();
  }
}

class QuadraticFormula
    implements SaturationFormula {
  public double getSaturartion(double originalSaturation, double adjustValue) {
    double dsaturation = originalSaturation / 100.;
    return originalSaturation +
        adjustValue * dsaturation * (1 - dsaturation) * 4;
  }

  public double getAdjustValue(double originalSaturation, double newSaturation) {
    double offset = newSaturation - originalSaturation;
    double dsaturation = originalSaturation / 100.;
    double adjustValue = offset / (dsaturation * (1 - dsaturation) * 4);
    return adjustValue;
  }

  public String getName() {
    return "Quadra";
  }

  public short getSaturartion(short originalSaturation, short adjustValue) {
    throw new UnsupportedOperationException();
  }
}

class CubicFormula
    implements SaturationFormula {
  public double getSaturartion(double originalSaturation, double adjustValue) {
    double dsaturation = originalSaturation / 100.;
    return originalSaturation +
        adjustValue * dsaturation * dsaturation * (1 - dsaturation) * 8;
//    return originalSaturation +
//        adjustValue * dsaturation * (1 - dsaturation * dsaturation) ;
  }

  public double getAdjustValue(double originalSaturation, double newSaturation) {
    double offset = newSaturation - originalSaturation;
    double dsaturation = originalSaturation / 100.;
    double adjustValue = offset /
        (dsaturation * dsaturation * (1 - dsaturation) * 8);
    return adjustValue;
  }

  public String getName() {
    return "Cubic_";
  }

  public short getSaturartion(short originalSaturation, short adjustValue) {
    throw new UnsupportedOperationException();
  }
}

class Modified12Formula
    implements SaturationFormula {
  public double getSaturartion(double originalSaturation, double adjustValue) {
    if (originalSaturation < 50) {
      return SaturationImageAdjustor.cubic.getSaturartion(
          originalSaturation, adjustValue);
    }
    else {
      return SaturationImageAdjustor.quadratic.getSaturartion(
          originalSaturation, adjustValue);
    }
  }

  public double getAdjustValue(double originalSaturation, double newSaturation) {
    if (originalSaturation < 50) {
      return SaturationImageAdjustor.cubic.getAdjustValue(
          originalSaturation, newSaturation);
    }
    else {
      return SaturationImageAdjustor.quadratic.getAdjustValue(
          originalSaturation, newSaturation);
    }
  }

  public String getName() {
    return "Modified12";
  }

  public short getSaturartion(short originalSaturation, short adjustValue) {
    throw new UnsupportedOperationException();
  }

}

//class Modified3Formula
//    implements SaturationFormula {
//  public double getSaturartion(double originalSaturation, double adjustValue) {
//    double dsaturation = originalSaturation / 100.;
//    return originalSaturation +
//        adjustValue * dsaturation * (1 - dsaturation * dsaturation) * 2.5;
//  }
//
//  public double getAdjustValue(double originalSaturation,
//                               double newSaturation) {
//    double offset = newSaturation - originalSaturation;
//    double dsaturation = originalSaturation / 100.;
//    double adjustValue = offset /
//        (dsaturation * dsaturation * (1 - dsaturation) * 8);
//    return adjustValue;
//  }
//}

/**
 *
 * <p>Title: Colour Management System</p>
 * double square curve
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
class CombineFormula
    implements SaturationFormula {
  public static enum Type {
    Type1, Type2
  }

  public String getName() {
    return "2Quadr";
  }

  private Type type;
  public CombineFormula() {
    this(Type.Type1);
  }

  public CombineFormula(Type type) {
    this.type = type;
  }

  public double getSaturartion(double originalSaturation, double adjustValue) {
    double dsaturation = originalSaturation / 100.;

    double dsaturation1 = originalSaturation / 50.;
    double result = originalSaturation <= 50 ?
        originalSaturation + (dsaturation1 * dsaturation1) * adjustValue :
        (type == Type.Type1) ?
        (originalSaturation + dsaturation * (1 - dsaturation) * adjustValue * 4) :
        (originalSaturation +
         adjustValue * 4 * (1 - dsaturation * (2 - dsaturation)));
//        (originalSaturation + adjustValue * (1 - (1.5 - dsaturation) *
//                                             (dsaturation - .5) * 4));
//        (originalSaturation + adjustValue *
//         4 * (1 - 2 * dsaturation + dsaturation * dsaturation));

//        (originalSaturation +
//         adjustValue *
//         (1 + 4 * dsaturation * (2 - dsaturation) - 2));

//        originalSaturation + adjustValue *
//        (1 - (dsaturation1 - 0.75 - dsaturation1 * dsaturation1) * 3 *
//         adjustValue);
    return result;
  }

  public double getAdjustValue(double originalSaturation,
                               double newSaturation) {
    if (originalSaturation > 50) {
      throw new IllegalArgumentException("");
    }
    double offset = newSaturation - originalSaturation;
    double dsaturation50 = originalSaturation / 50.;
    double adjustValue = offset / (dsaturation50 * dsaturation50);
    return adjustValue;
  }

  public short getSaturartion(short originalSaturation, short adjustValue) {
    throw new UnsupportedOperationException();
  }

}

/**
 *
 * <p>Title: Colour Management System</p>
 * gain + square curve
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
class GainCombineFormula
    implements SaturationFormula {
  public double getSaturartion(double originalSaturation, double adjustValue) {
    double dsaturation = originalSaturation / 100.;
    double gain = (50 + adjustValue) / 50;
    double result = originalSaturation <= 50 ?
        originalSaturation * gain :
        (originalSaturation + dsaturation * (1 - dsaturation) * adjustValue * 4);
    return result;
  }

  public double getAdjustValue(double originalSaturation,
                               double newSaturation) {
    if (originalSaturation > 50) {
      throw new IllegalArgumentException("");
    }
    double gain = newSaturation / originalSaturation;
    double adjustValue = 50 * gain - 50;
    return adjustValue;
  }

  public String getName() {
    return "gain+2";
  }

  public short getSaturartion(short originalSaturation, short adjustValue) {
    throw new UnsupportedOperationException();
  }
}
