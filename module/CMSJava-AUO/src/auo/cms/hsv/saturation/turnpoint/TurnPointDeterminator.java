package auo.cms.hsv.saturation.turnpoint;

import java.awt.*;

import org.math.plot.*;
import auo.cms.hsv.saturation.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
//import auo.cms.hsv.old.*;

//import shu.plot.*;

//import auo.cms.test.hsv.value.newway.PrecisionEvaluator;

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
public class TurnPointDeterminator {
//  public static void clippingTest(String[] args) {
////    HSVAdjustProducer.TargetPatch targetPatch = HSVAdjustProducer.TargetPatch.
////        Integrated;
////    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(
////        "sRGB Adjust Evaluation/45% DG On.xls");
////
////    LCDTarget.Operator.gradationReverseFix(target);
////    target.changeMaxValue(RGB.MaxValue.Int8Bit);
////    LCDModel model = new MultiMatrixModel(target);
////    model.produceFactor();
////    model.setAutoRGBChangeMaxValue(true);
////    RGB.ColorSpace targetColorspace = SaturationImageAdjustor.
////        getTargetColorSpace(target, RGB.ColorSpace.sRGB_gamma22);
////    HSVAdjustProducer producer = new
////        HSVAdjustProducer(targetColorspace, model, targetPatch);
//    HSVAdjustProducer producer = getHSVAdjustProducer();
//
//    for (int turnPoint = 50; turnPoint <= 80; turnPoint++) {
//      RichardFormula formula = new RichardFormula(turnPoint);
//      double[] saturationAdjustArray = producer.getSaturationAdjustArray(
//          formula);
//
//      Plot2D plot = Plot2D.getInstance(Integer.toString(turnPoint));
//      boolean clip = false;
//      for (int x = 0; x < saturationAdjustArray.length; x++) {
//        double saturationAdjust = saturationAdjustArray[x];
//        int hue = x * 60;
//        for (int s = 0; s <= 100; s++) {
//          double sp = formula.getSaturartion(s, saturationAdjust);
//          plot.addCacheScatterLinePlot(Integer.toString(hue), s, sp);
//          if (sp > 100) {
//            clip = true;
//            System.out.println(hue + " " + turnPoint);
//            break;
//          }
//        }
//      }
//      if (clip) {
//        plot.setVisible();
//      }
//    }
//
//  }

//  private static LCDModel model;
//  private static CIEXYZ modelWhiteXYZ;
//  private static HSVAdjustProducer producer;
//  public static void produceHSVAdjustProducer() {
//    HSVAdjustProducer.TargetPatch targetPatch = HSVAdjustProducer.TargetPatch.
//        Integrated;
//    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(
//        "sRGB Adjust Evaluation/45% DG On.xls");
//    LCDTarget.Operator.gradationReverseFix(target);
//    target.changeMaxValue(RGB.MaxValue.Int8Bit);
//    model = new MultiMatrixModel(target);
//    model.produceFactor();
//    modelWhiteXYZ = model.getWhiteXYZ(false);
////    model.setAutoRGBChangeMaxValue(true);
//    RGB.ColorSpace targetColorspace = SaturationImageAdjustor.
//        getTargetColorSpace(target, RGB.ColorSpace.sRGB_gamma22);
//    producer = new
//        HSVAdjustProducer(targetColorspace, model, targetPatch);
//
//  }

//  private static HSVAdjustProducer getHSVAdjustProducer() {
//    HSVAdjustProducer.TargetPatch targetPatch = HSVAdjustProducer.TargetPatch.
//        Integrated;
//    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(
//        "sRGB Adjust Evaluation/45% DG On.xls");
//    LCDTarget.Operator.gradationReverseFix(target);
//    target.changeMaxValue(RGB.MaxValue.Int8Bit);
//    model = new MultiMatrixModel(target);
//    model.produceFactor();
//    modelWhiteXYZ = model.getWhiteXYZ(false);
////    model.setAutoRGBChangeMaxValue(true);
//    RGB.ColorSpace targetColorspace = SaturationImageAdjustor.
//        getTargetColorSpace(target, RGB.ColorSpace.sRGB_gamma22);
//    HSVAdjustProducer producer = new
//        HSVAdjustProducer(targetColorspace, model, targetPatch);
//    return producer;
//  }

  public static void main(String[] args) {
//    clippingTest(args);
//    saturationTest(args);
    deltaETest(args);
  }

//  private static CIEXYZ getNormalizeModelXYZ(RGB rgb) {
//    CIEXYZ XYZ = model.getXYZ(rgb, false);
//    XYZ.normalize(modelWhiteXYZ);
//    return XYZ;
//  }

  private static void sRGBAlter(RGB rgb) {
    rgb.changeMaxValue(RGB.MaxValue.Int9Bit);
    rgb.changeMaxValue(RGB.MaxValue.Double255);
    rgb.R = rgb.R == 191.5 ? 192 : rgb.R;
    rgb.G = rgb.G == 191.5 ? 192 : rgb.G;
    rgb.B = rgb.B == 191.5 ? 192 : rgb.B;
    rgb.R = rgb.R == 95.5 ? 96 : rgb.R;
    rgb.G = rgb.G == 95.5 ? 96 : rgb.G;
    rgb.B = rgb.B == 95.5 ? 96 : rgb.B;

  }

  private static double[] saturationAdjustArray;
  private static double[] valueAdjustArray;
  private static double[] hueAdjustArray;
  /**
   *
   * @param formula IntegerSaturationFormula
   * @deprecated
   */
  private static void produceAdjustArray(IntegerSaturationFormula formula) {
//    HSVAdjustProducer producer = getHSVAdjustProducer();
    saturationAdjustArray = Util.producer.getSaturationAdjustArray(
        formula);
    valueAdjustArray = Util.producer.getValueAdjustArray();
    hueAdjustArray = Util.producer.getHueAdjustArray();

  }

  private static double[] getHSVAdjust(int hueIndex) {
    double hueAdjust = hueAdjustArray[hueIndex];
    double saturationAdjust = saturationAdjustArray[hueIndex];
    double valueAdjust = valueAdjustArray[hueIndex];
    double[] hsvAdjust = new double[] {
        hueAdjust, saturationAdjust, valueAdjust};
    return hsvAdjust;
  }

  /**
   *
   * @param args String[]
   * @deprecated
   */
  public static void deltaETest(String[] args) {
    //==========================================================================
    // setting
    //==========================================================================
//    int testValue = 75; //75 for sRGB
    int hueStart = 0;
    int hueEnd = 360;
    int hueStep = 60;
    int hueCount = hueEnd / hueStep;

    int turnPointStart = 7;
    int turnPointEnd = 15;

//    boolean showPlot = false;
    boolean adjustValueByCalculting = true;
    final int MaxSaturationAdjust = 63;

    Color[] colors = new Color[] {
        Color.red, Color.orange, Color.yellow, Color.green, Color.cyan,
        Color.blue, Color.magenta, Color.black, Color.black};
    //==========================================================================
    // deltaE setting
    //==========================================================================
    boolean showDeltaEabOnly = false;
    boolean showDeltaEPlot = false;
    //==========================================================================

    boolean showJChVerify = false;
    //==========================================================================
//    HSVAdjustProducer producer = getHSVAdjustProducer();
    CIEXYZ whiteXYZ = RGB.ColorSpace.sRGB.getReferenceWhiteXYZ();
    //==============================================================
    CIECAM02 cam02 = getCIECAM02Instance(whiteXYZ);
//    int valueStart = 0;

//    int valueEnd = 100;

//    boolean showOriginalDeltaE = false;
    //原始的色差 h s v
    double[][][] originalDeltaEArray = new double[hueCount][101][101];
    double[][] sRGBPatchDeltaEArray = new double[16][hueCount];
    StringBuffer adjustbuf = new StringBuffer();
    System.out.println("ave deltaE");
    boolean orgCalculated = false;
    double[] tag = new double[101];
    for (int x = 0; x <= 100; x++) {
      tag[x] = x;
    }
    for (int hue = hueStart; hue < hueEnd; hue += hueStep) {

      Plot2D JChPlot = null;
      if (showJChVerify) {
        JChPlot = Plot2D.getInstance("Hue: " + Integer.toString(hue));
      }

      for (int turnPoint = turnPointStart; turnPoint <= turnPointEnd; turnPoint++) {
        IntegerSaturationFormula formula = new IntegerSaturationFormula( (byte)
            turnPoint, 4);

        //      double[] saturationAdjustArray = producer.getSaturationAdjustArray(
        //          formula);
        //      double[] valueAdjustArray = producer.getValueAdjustArray();
        //      double[] hueAdjustArray = producer.getHueAdjustArray();

        System.out.print(turnPoint + ":  ");
        //      StringBuffer buf = new StringBuffer();
        adjustbuf.append("TurnPoint: " + turnPoint + "\n");
        Util.produceHSVAdjustProducer();
        if (adjustValueByCalculting) {
          produceAdjustArray(formula);
          adjustbuf.append(DoubleArray.toString(hueAdjustArray) + "\n");
          adjustbuf.append(DoubleArray.toString(saturationAdjustArray) + "\n");
          adjustbuf.append(DoubleArray.toString(valueAdjustArray) + "\n");
        }

        saturationAdjustArray = Util.producer.getSaturationAdjustArray(formula);

        int[] monitorIndex = null;

        //======================================================================
        // sAdjust
        //======================================================================
        int sAdjust = 1;
//        for (int sAdjust = MaxSaturationAdjust; sAdjust >= 0; sAdjust--) {
        //======================================================================

        if (!adjustValueByCalculting) {
          hueAdjustArray = new double[] {
              0, 0, 0, 0, 0, 0};
//          saturationAdjustArray = new double[] {
//              0, 0, 0, 0, 0, 0};
          saturationAdjustArray = DoubleArray.fill(6, sAdjust);
          valueAdjustArray = new double[] {
              0, 0, 0, 0, 0, 0};
        }
//        for (int h = hueStart; h < hueEnd; h += hueStep) {

        //======================================================================
        // line color
        //======================================================================
//        Color c = HSV.getLineColor(hue);
        //======================================================================
        int hIndex = hue / hueStep;
        //        double hueAdjust = hueAdjustArray[hIndex];
        //        double saturationAdjust = saturationAdjustArray[hIndex];
        //        double valueAdjust = valueAdjustArray[hIndex];
        //        double[] hsvAdjust = new double[] {
        //            hueAdjust, saturationAdjust, valueAdjust};
        double[] hsvAdjust = getHSVAdjust(hIndex);
        //s v
        double[][] deltaEArray = new double[101][101];
        double[][] deltaSArray = showJChVerify ? new double[101][101] : null;

        for (int v = 0; v <= 100; v++) {
          for (int s = 0; s <= 100; s++) {
            HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {hue, s, v});
            RGB rgb = hsv.toRGB();

            //==================================================================
            // 因為v=0~100 & s=0~100沒辦法剛好碰到sRGB的check patch, 所以只好竄改
            //==================================================================
            sRGBAlter(rgb);
            //==================================================================

            //目標XYZ
            CIEXYZ sRGBXYZ = rgb.toXYZ();
            //目前XYZ
            CIEXYZ XYZ = Util.model.getNormalizeXYZ(rgb);
            CIECAM02Color JCh = cam02.forward(XYZ);
            if (!orgCalculated) { //org deltaE
              DeltaE dE = new DeltaE(XYZ, sRGBXYZ, whiteXYZ);
              double de00 = showDeltaEabOnly ? dE.getCIE2000Deltaab() :
                  dE.getCIE2000DeltaE();
              originalDeltaEArray[hIndex][s][v] = de00;
            }

            //====================================================================
            // HSV的調整
            //====================================================================
            HSV hsv2 = Util.getNewHSV(hsv, hsvAdjust, formula);
            //====================================================================
            RGB rgb2 = hsv2.toRGB();
            rgb2.changeMaxValue(RGB.MaxValue.Int9Bit);
            rgb2.changeMaxValue(RGB.MaxValue.Double255);
            CIEXYZ XYZ2 = Util.model.getNormalizeXYZ(rgb2);
            CIECAM02Color JCh2 = cam02.forward(XYZ2);

            //==================================================================
            // 計算色差
            //==================================================================
            DeltaE dE = new DeltaE(XYZ2, sRGBXYZ, whiteXYZ);
            double de00 = showDeltaEabOnly ? dE.getCIE2000Deltaab() :
                dE.getCIE2000DeltaE();
            deltaEArray[s][v] = de00;
            //==================================================================

            //==================================================================
            // 計算dC
            //==================================================================
            if (showJChVerify) {
              deltaSArray[s][v] = Math.abs(JCh2.s - JCh.s);
            }
            //==================================================================

            if ( ( ( (hue == 0 || hue == 60) && s == 50) ||
                  (hue != 0 && hue != 60 && s == 33)) && v == 75) {
              //遇到sRGB check patch的時候
              sRGBPatchDeltaEArray[turnPoint][hIndex] = de00;
            }

          }
        }

        //======================================================================
        // 每個hue的處理
        //======================================================================
        System.out.print("Mean dE:" + Maths.mean(deltaEArray) + " ");
        if (showDeltaEPlot) {
          double[][] delta = DoubleArray.minus(deltaEArray,
                                               originalDeltaEArray[hIndex]);
          Plot3D plot = Plot3D.getInstance("Hue=" +
                                           Integer.toString(hue) + " org");
          //        plot.addGridPlot("org", tag, tag, originalDeltaEArray[hIndex]);
          plot.addGridPlot("", tag, tag, delta);
          plot.setAxisLabels("V", "S", "dE00");
          plot.setVisible();
        }

        if (showJChVerify) {
          System.out.println("");
          if (null == monitorIndex) {
            monitorIndex = Maths.maxIndex(deltaSArray);
          }
          System.out.println(IntArray.toString(monitorIndex));
          System.out.println(IntArray.toString(Maths.maxIndex(deltaSArray)));
//            double max = Maths.max(deltaCArray);
          //v鎖定在100即可
          double max = deltaSArray[monitorIndex[0]][monitorIndex[1]];
          System.out.println("max dC(of JCh): " + max);
          Color c = colors[ (turnPoint - turnPointStart) % colors.length];
          for (int x = 0; x <= 100; x++) {
            JChPlot.addCacheScatterLinePlot(Integer.toString(turnPoint), c, x,
                                            deltaSArray[x][100]);
          }
          JChPlot.addLegend();
          JChPlot.setVisible();
          JChPlot.setAxisLabels("S of HSV", "S of JCh");
          JChPlot.setFixedBounds(1, 0, 10);

          ( (Plot2DPanel) JChPlot.getPlotPanel()).removePlotToolBar();
        }
        //======================================================================
//        } //sAdjust
        orgCalculated = true;

        //      showOriginalDeltaE = true;
        System.out.println("");
        //      if (showPlot) {
        //        ( (Plot2DPanel) plot.getPlotPanel()).removePlotToolBar();
        //        plot.setAxisLabels("Saturation", "deltaE");
        //        plot.setVisible();
        //        plot.setFixedBounds(1, 0, 14);
        //      }

        //      System.out.println(buf.toString());
      }
      //    Plot2D plot = Plot2D.getInstance("Original");


      //    System.out.println("average diff");
      //==========================================================================
      // org ave deltaE
      //==========================================================================
      System.out.print("org: ");
      for (int h = hueStart; h < hueEnd; h += hueStep) {
        int hIndex = h / hueStep;
        double[][] data = originalDeltaEArray[hIndex];

        System.out.print(Maths.mean(originalDeltaEArray[hIndex]) + " ");
        double de00 = -1;
        if (h == 0 || h == 60) {
          de00 = data[50][75];
        }
        else {
          de00 = data[33][75];
        }
        sRGBPatchDeltaEArray[0][hIndex] = de00;
      }
      System.out.println("");
      //==========================================================================

      System.out.println("=====Mean sRGB deltaE=====");
      System.out.println("first is org");
      System.out.println(DoubleArray.toString(sRGBPatchDeltaEArray));
      System.out.println("");
      System.out.println("=====adjust value=====");
      System.out.println(adjustbuf.toString());
    }
  }

  private static CIECAM02 getCIECAM02Instance(CIEXYZ XYZ) {
    CIEXYZ normalWhiteXYZ = (CIEXYZ) XYZ.clone();
//    normalWhiteXYZ.times(200);
    normalWhiteXYZ.normalizeY100();
    ViewingConditions vc = ViewingConditions.getTypicalViewingConditions(
        normalWhiteXYZ);
    CIECAM02 cam02 = new CIECAM02(vc);
    return cam02;
  }

  /*public static void saturationTest(String[] args) {
    //==========================================================================
    // setting
    //==========================================================================
    int testValue = 75; //75 for sRGB
//    double adjustSaturation = 25;

    //==========================================================================
    HSVAdjustProducer producer = getHSVAdjustProducer();
    IntegerSaturationFormula.Version ver = IntegerSaturationFormula.Version.v2;
    //==============================================================

    CIEXYZ normalWhiteXYZ = (CIEXYZ) modelWhiteXYZ.clone();
    normalWhiteXYZ.normalizeY100();
    ViewingConditions vc = ViewingConditions.getsRGBLikeViewingConditions(
        normalWhiteXYZ);
    CIECAM02 cam02 = new CIECAM02(vc);

    for (int turnPoint = 0; turnPoint < 16; turnPoint++) {
      Plot2D hsvPlot = Plot2D.getInstance("HSV, TP:" +
                                          Integer.toString(turnPoint));
      Plot2D camPlot = Plot2D.getInstance("CAM02, TP:" +
                                          Integer.toString(turnPoint));
      IntegerSaturationFormula formula = new IntegerSaturationFormula( (byte)
          turnPoint, 4);
      double[] saturationAdjustArray = producer.getSaturationAdjustArray(
          formula);
      double[] valueAdjustArray = producer.getValueAdjustArray();
      double[] hueAdjustArray = producer.getHueAdjustArray();

      for (int h = 0; h < 60; h += 60) {
        double hueAdjust = hueAdjustArray[h / 60];
        double saturationAdjust = saturationAdjustArray[h / 60];
        double valueAdjust = valueAdjustArray[h / 60];
        double[] hsvAdjust = new double[] {
            hueAdjust, saturationAdjust, valueAdjust};
        if (h == 0 || h == 60) {

        }

        //======================================================================
        // line color
        //======================================================================
        Color c = HSV.getLineColor(h);
        //======================================================================
//        boolean drawOriginal = false;

        for (int s = 0; s <= 100; s++) {
   HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {h, s, testValue});
          RGB rgb = hsv.toRGB();
          CIEXYZ XYZ = model.getNormalizeXYZ(rgb);

          CIECAM02Color camcolor = cam02.forward(XYZ);
          double originalCAMSaturation = camcolor.s;

          //====================================================================
          // HSV的調整
          //====================================================================
          HSV hsv2 = getNewHSV(hsv, hsvAdjust, formula);
          //====================================================================
          RGB rgb2 = hsv2.toRGB();
//          CIEXYZ XYZ2 = getNormalizeModelXYZ(rgb2);
          CIEXYZ XYZ2 = model.getNormalizeXYZ(rgb2);
//          CIEXYZ XYZ2 = rgb2.toXYZ();

          System.out.println(XYZ + " " + XYZ2);
          CIECAM02Color camcolor2 = cam02.forward(XYZ2);

//          System.out.println(hsv + " " + rgb);
//          System.out.println(hsv.S + " " + hsv2.S);
          hsvPlot.addCacheScatterLinePlot(Integer.toString(h), c,
                                          hsv.S, hsv2.S);
//          camPlot.addCacheScatterLinePlot(Integer.toString(h), c,
//                                          originalCAMSaturation, camcolor2.s);
        }
      }

      hsvPlot.setVisible();
      hsvPlot.setAxeLabel(0, "Input S(%)");
      hsvPlot.setAxeLabel(1, "Output S(%)");

//      camPlot.setVisible();
      camPlot.setFixedBounds(0, 0, 120);
      camPlot.setFixedBounds(1, 0, 120);
      camPlot.setAxeLabel(0, "Input S");
      camPlot.setAxeLabel(1, "Output S");

    }
     }*/
}
