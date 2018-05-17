package auo.cms.cm;

import shu.cms.lcd.LCDTarget;
import shu.cms.devicemodel.lcd.MultiMatrixModel;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.*;
import shu.cms.*;
import shu.math.regress.*;
import shu.math.array.DoubleArray;
import shu.math.array.*;
import shu.cms.devicemodel.lcd.thread.GOGModelThread;
import shu.cms.devicemodel.lcd.LCDModel;
import shu.cms.devicemodel.lcd.thread.RegularGOGModelThread;
import shu.cms.devicemodel.lcd.PLCCModel;
import flanagan.math.Minimisation;
import java.util.*;
import flanagan.math.MinimisationFunction;
import shu.cms.plot.*;
import java.awt.Color;
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
 * @author not attributable
 * @version 1.0
 */
public class ColorMatrixCalculator {

  static class TestFunction
      implements MinimisationFunction {
    private double a = 0.5D;

    // evaluation function
    public double function(double[] x) {
      double z = a + x[0] * x[0] + 3.0D * Math.pow(x[1], 4);
//      return z;
      return x[0] * x[1];
    }

  }

  public static void test(String[] args) {
    Minimisation min = new Minimisation();
    min.addConstraint(new int[] {0, 1}, new int[] {1, 1}, 0, 1);
    min.nelderMead(new TestFunction(), new double[] {
      0, 0
    }, new double[] {
        0.1, 0.1});
    System.out.println(min.getMinimum());
    double[] result = min.getParamValues();
    System.out.println(Arrays.toString(result));
    System.out.println(result[0] + result[1]);
  }

  public static void main(String[] args) {
//    cmtest(args);
//    stock(args);

    List<RGB> whqlRGBList = LCDTarget.Instance.getRGBList(LCDTarget.Number.WHQL);
//    List<RGB> testRGBList = getWithoutNeutralRGBList(whqlRGBList);
    List<RGB> testRGBList = whqlRGBList.subList(7, 14);
//    whqlRGBListWithoutNeutral = getWithoutPureRGBList(whqlRGBListWithoutNeutral);


    String sourceFilename = "dell/Measurement01(DG On).xls";
    LCDTarget ramp = LCDTarget.Instance.getFromAUORampXLS(sourceFilename,
        LCDTarget.Number.Ramp256_6Bit);

//    String sourceFilename = "dell/ramp.xls";
//    LCDTarget ramp = LCDTarget.Instance.getFromAUORampXLS(sourceFilename);
//    LCDTarget.Operator.gradationReverseFix(ramp);

    LCDModel model = new MultiMatrixModel(ramp);
//    LCDModel model = new PLCCModel(ramp);
    model.produceFactor();
    model.setAutoRGBChangeMaxValue(true);

    Minimisation min = new Minimisation();
    double[] index1 = new double[] {
        1, 1, 1};
    double[] index0 = new double[] {
        0, 0, 0};
    //==========================================================================

    //==========================================================================
//    DeltaEFunction func = new DeltaEFunction(model, whqlRGBListWithoutNeutral);
//    min.addConstraint(0, -1, 1);
//    min.addConstraint(1, -1, 1);
//    min.addConstraint(2, -1, 1);
//    min.addConstraint(0, 1, 2);
//    min.addConstraint(1, 1, 2);
//    min.addConstraint(2, 1, 2);
    //==========================================================================
//    DeltaEFunction func = new DeltaEFunction2(model, whqlRGBListWithoutNeutral);

//    min.addConstraint(0, -1, 1);
//    min.addConstraint(4, -1, 1);
//    min.addConstraint(8, -1, 1);
//
//    min.addConstraint(0, 1, 2);
//    min.addConstraint(1, 1, 0);
//    min.addConstraint(2, 1, 0);
//    min.addConstraint(3, 1, 0);
//    min.addConstraint(4, 1, 2);
//    min.addConstraint(5, 1, 0);
//    min.addConstraint(6, 1, 0);
//    min.addConstraint(7, 1, 0);
//    min.addConstraint(8, 1, 2);

//    min.addConstraint(new int[] {0, 1, 2}, index1, 0, 1);
//    min.addConstraint(new int[] {3, 4, 5}, index1, 0, 1);
//    min.addConstraint(new int[] {6, 7, 8}, index1, 0, 1);
//    min.addConstraint(new int[] {0, 1, 2}, index1, -1, 0.8);
//    min.addConstraint(new int[] {3, 4, 5}, index1, -1, 0.8);
//    min.addConstraint(new int[] {6, 7, 8}, index1, -1, 0.8);
//    min.addConstraint(new int[] {0, 1, 2}, index1, 1, 1.2);
//    min.addConstraint(new int[] {3, 4, 5}, index1, 1, 1.2);
//    min.addConstraint(new int[] {6, 7, 8}, index1, 1, 1.2);
//    double[] step = new double[9];
//    Arrays.fill(step, 0.03125);
//    min.nelderMead(func, new double[] {1, 0, 0, 0, 1, 0, 0, 0, 1}, step);

    //==========================================================================
    //0 1 2: r g b gain
    //3 r part, 4 g part, 5 b part

    DeltaEFunction func = new DeltaEFunction3(model, testRGBList);
    min.addConstraint(0, -1, 1);
    min.addConstraint(1, -1, 1);
    min.addConstraint(2, -1, 1);
//    min.addConstraint(0, 1, 1.3);
//    min.addConstraint(1, 1, 1.3);
//    min.addConstraint(2, 1, 1.3);

    min.addConstraint(3, -1, 0);
    min.addConstraint(4, -1, 0);
    min.addConstraint(5, -1, 0);
    min.addConstraint(3, 1, 1);
    min.addConstraint(4, 1, 1);
    min.addConstraint(5, 1, 1);

    double[] step = new double[6];
    Arrays.fill(step, 0.03125);
    min.nelderMead(func, new double[] {1, 1, 1, 0.5, 0.5, 0.5}, step);
    //==========================================================================


//    min.addConstraint(new int[]{0,1,2},index,0,1);

//    min.nelderMead(func, new double[] {1, 1, 1});


    double deltaE = min.getMinimum();
    double[] rgbGain = min.getParamValues();
    System.out.println("deltaE: " + deltaE);
    double[] cm = DeltaEFunction3.getColorMatrix(rgbGain,
                                                 new int[] {0, 0, 0});

    System.out.println("rgbGain: " + DoubleArray.toString(cm));

//    System.out.println("rgbGain: " +
//                       DoubleArray.toString(
//                           getColorMatrixWithOffset(cm, new int[] { -5, -5, -5})));

    System.out.println(func.function(new double[] {1, 1, 1, 0.5, 0.5, 0.5}));

  }

  private static double[] getColorMatrixWithOffset(double[] cm, int[] offset) {
    double gain = 255. / (255 + offset[0]);
    double r = cm[0] * gain;
    double g = cm[4] * gain;
    double b = cm[8] * gain;
//    double r0 = (1 - r) * (cm[1] / (cm[1] + cm[2]));
//    double r1 = (1 - r) * (cm[2] / (cm[1] + cm[2]));
//    double g0 = (1 - g) * (cm[3] / (cm[3] + cm[5]));
//    double g1 = (1 - g) * (cm[5] / (cm[3] + cm[5]));
//    double b0 = (1 - b) * (cm[6] / (cm[6] + cm[7]));
//    double b1 = (1 - b) * (cm[7] / (cm[6] + cm[7]));
    return new double[] {
        r, cm[1], cm[2], cm[3], g, cm[5], cm[6], cm[7], b};
  }

  private static RGB getColorMatirxRGB(RGB original, double[] rgbGain,
                                       int[] offset) {
    double rp = - (rgbGain[0] - 1) / 2;
    double gp = - (rgbGain[1] - 1) / 2;
    double bp = - (rgbGain[2] - 1) / 2;
    double[][] cm = new double[][] {
        {
        rgbGain[0], rp, rp}, {
        gp, rgbGain[1], gp}, {
        bp, bp, rgbGain[2]}
    };
    double[] rgbValues = original.getValues();
    rgbValues = DoubleArray.minus(rgbValues, IntArray.toDoubleArray(offset));
    rgbValues = DoubleArray.times(cm, rgbValues);
    RGB cmrgb = (RGB) original.clone();
    cmrgb.setValues(rgbValues);
    return cmrgb;
  }

  private static RGB getColorMatirxRGB(RGB original, double[][] colorMatrix,
                                       int[] offset) {
    double[] rgbValues = original.getValues();
    rgbValues = DoubleArray.minus(rgbValues, IntArray.toDoubleArray(offset));
    rgbValues = DoubleArray.times(colorMatrix, rgbValues);
    RGB cmrgb = (RGB) original.clone();
    cmrgb.setValues(rgbValues);
    return cmrgb;
  }

  private static double calculateDeltaE(LCDModel model, List<RGB> testRGBList,
      double[] rgbGain, int[] offset, boolean matrixMode) {
    double[][] cm = matrixMode ? DoubleArray.to2DDoubleArray(rgbGain, 3) : null;

    CIEXYZ whiteXYZ = model.getWhiteXYZ(false);
    int size = testRGBList.size();
    List<CIELab> targetLabList = new ArrayList<CIELab> (size);
    List<CIELab> panelLabList = new ArrayList<CIELab> (size);
    CIEXYZ targetWhiteXYZ = RGB.ColorSpace.sRGB.getReferenceWhiteXYZ();
    targetWhiteXYZ.scaleY(whiteXYZ);

    for (int x = 0; x < size; x++) {
      RGB rgb = (RGB) testRGBList.get(x).clone();
      CIEXYZ targetXYZ = rgb.toXYZ(RGB.ColorSpace.sRGB);
      targetXYZ.times(whiteXYZ.Y); //base為1, 乘大為panel亮度

      RGB cmRGB = matrixMode ? getColorMatirxRGB(rgb, cm, offset) :
          getColorMatirxRGB(rgb, rgbGain, offset);
      CIEXYZ panelXYZ = model.getXYZ(cmRGB, false);

      CIELab targetLab = new CIELab(targetXYZ, targetWhiteXYZ);
      CIELab panelLab = new CIELab(panelXYZ, whiteXYZ);
      targetLabList.add(targetLab);
      panelLabList.add(panelLab);
    }
    DeltaEReport.setOnlyCountMeasuredDeltaE(false);
    DeltaEReport report = DeltaEReport.Instance.CIELabReport(targetLabList,
        panelLabList);
    double deltaE94 = report.meanDeltaE.getCIE94DeltaE();
    return deltaE94;
  }

  static class DeltaEFunction
      implements MinimisationFunction {
    protected LCDModel model;
    protected List<RGB> testRGBList;

    public DeltaEFunction(LCDModel model, List<RGB> testRGBList) {
      this.model = model;
      this.testRGBList = testRGBList;
    }

    public double function(double[] param) {
      return calculateDeltaE(model, testRGBList, param, new int[] {0, 0, 0}, false);
    }
  }

  static class DeltaEFunction2
      extends DeltaEFunction {

    public DeltaEFunction2(LCDModel model, List<RGB> testRGBList) {
      super(model, testRGBList);
    }

    public double function(double[] param) {
      return calculateDeltaE(model, testRGBList, param, new int[] {0, 0, 0}, true);
    }
  }

  static class DeltaEFunction3
      extends DeltaEFunction {
    private int[] offset = new int[] {
        0, 0, 0};
    public DeltaEFunction3(LCDModel model, List<RGB> testRGBList) {
      super(model, testRGBList);
    }

    public DeltaEFunction3(LCDModel model, List<RGB> testRGBList, int[] offset) {
      super(model, testRGBList);
      this.offset = offset;
    }

    public double function(double[] param) {
      double[] cm = getColorMatrix(param, offset);
      return calculateDeltaE(model, testRGBList, cm, offset, true);
    }

    public static double[] getColorMatrix(double[] params, int[] offset) {
      //0 1 2: r g b gain
      //3 r part, 4 g part, 5 b part
      double gain = 255. / (255 - offset[0]);
      double r = params[0] * gain;
      double g = params[1] * gain;
      double b = params[2] * gain;
      double r0 = (1 - r) * params[3];
      double r1 = (1 - r) * (1 - params[3]);
      double g0 = (1 - g) * params[4];
      double g1 = (1 - g) * (1 - params[4]);
      double b0 = (1 - b) * params[5];
      double b1 = (1 - b) * (1 - params[5]);
      return new double[] {
          r, r0, r1, g0, g, g1, b0, b1, b};
    }

  }

  private static List<RGB> getWithoutNeutralRGBList(List<RGB> rgbList) {
    List<RGB> withoutNeutralRGBList = new ArrayList<RGB> ();
    for (RGB rgb : rgbList) {
      if (!rgb.isGray()) {
        withoutNeutralRGBList.add(rgb);
      }
    }
    return withoutNeutralRGBList;
  }

  private static List<RGB> getWithoutPureRGBList(List<RGB> rgbList) {
    List<RGB> withoutPureRGBList = new ArrayList<RGB> ();
    for (RGB rgb : rgbList) {
      if (!rgb.isPrimaryChannel()) {
        withoutPureRGBList.add(rgb);
      }
    }

    return withoutPureRGBList;
  }

  public static void stock(String[] args) {
    String sourceFilename =
//        "dell/ramp.xls";
        "D:\\My Documents\\工作\\Project\\Product\\Dell\\B156HW03\\1125\\for cm2\\ramp.xls";
    String targetFilename =
//        "dell/sRGB target.xls";
        "D:\\My Documents\\工作\\Project\\Product\\Dell\\B156HW03\\1125\\for cm2\\sRGB46(Target sRGB).xls";
//    String sourceFilename = "suorce_data.xls";
//    String targetFilename = "srgb46_raw.xls";

    LCDTarget ramp = LCDTarget.Instance.getFromAUORampXLS(sourceFilename);
    LCDTarget.Operator.gradationReverseFix(ramp);
    LCDModel model = new MultiMatrixModel(ramp);
//    LCDModel model = new PLCCModel(ramp);
    model.produceFactor();
    model.setAutoRGBChangeMaxValue(true);
    CIEXYZ whiteXYZ = model.getWhiteXYZ(false);

    LCDTarget target = LCDTarget.Instance.getFromAUOXLS(targetFilename);
    Patch whitePatch = target.getWhitePatch();
    double YScale = whiteXYZ.Y / whitePatch.getXYZ().Y;

    List<Patch> patchList = target.getPatchList();
//    patchList = patchList.subList(7, 14);
    int size = patchList.size();
    double[][] input = new double[size][];
    double[][] output = new double[size][];

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      RGB rgb = p.getRGB();
      CIEXYZ XYZ = p.getXYZ();
      XYZ.times(YScale);
      RGB rgb2 = model.getRGB(XYZ, false);
//      double[] rgbValues = rgb.getValues();
//      double[] rgb2Values = rgb2.getValues();
//      double[] processValues = DoubleArray.minus(rgb2Values, rgbValues);
      /*for (int m = 0; m < processValues.length; m++) {
        if (processValues[m] < 0) {
          RGB.Channel ch = RGB.Channel.getChannel(m + 1);
//          rgb2.setValue(ch, rgb.getValue(ch));
//          rgb.setValue(ch, 0);
//          rgb2.setValue(ch, 0);
        }
             }
             System.out.println(rgb + " " + rgb2);*/
      System.out.println(rgb + " " + rgb2);
      input[x] = rgb.getValues();
      output[x] = rgb2.getValues();
    }

    Regression regression = new Regression(input, output);
    regression.regress();
    double[][] coefs = regression.getCoefs();
    System.out.println(DoubleArray.toString(coefs));

    Plot2D plot = Plot2D.getInstance();
    for (Patch p : target.getPatchList()) {
      RGB rgb = p.getRGB();
      CIEXYZ XYZ = model.getXYZ(rgb, false);
//      CIEXYZ XYZ = p.getXYZ();
      CIEXYZ sRGBXYZ = rgb.toXYZ(RGB.ColorSpace.sRGB);
      CIExyY xyY = new CIExyY(XYZ);
      CIExyY sRGBxyY = new CIExyY(sRGBXYZ);
      plot.addCacheScatterPlot("panel", Color.green, xyY.x, xyY.y);
      plot.addCacheScatterPlot("sRGB", Color.red, sRGBxyY.x,
                               sRGBxyY.y);
    }
    plot.addLegend();
    plot.setVisible();

  }
}
