package auo.cms.hsv.old;

import java.util.*;

import auo.cms.hsv.saturation.*;
import auo.cms.hsv.value.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import auo.cms.hsv.util.*;
import auo.cms.hsv.*;
import auo.cms.hsv.value.backup.*;

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
 * @deprecated
 */
public class HSVAdjustProducer {
  private LCDModel model;
  private RGB.ColorSpace targetColorSpace;
  private List<Patch> integratedPatchList;
//  static {
//    LCDTarget whqlTarget = LCDTarget.Instance.get(LCDTarget.Number.WHQL);
//    integratedPatchList = whqlTarget.filter.getRange(7, 13);
////    integratedPatchList = whqlTarget.filter.getRange(0, 6);
////    for (Patch p : integratedPatchList) {
////      System.out.println(p);
////    }
//  }

  public LCDModel getLCDModel() {
    return model;
  }

  public static enum TargetPatch {
    StandAlone, Integrated
  }

//  private TargetPatch targetPatch;
  public HSVAdjustProducer(
      RGB.ColorSpace targetColorSpace,
      LCDModel model, TargetPatch targetPatch) {
    this.model = model;
//    model.produceFactor();
    this.targetColorSpace = targetColorSpace;

    LCDTarget whqlTarget = LCDTarget.Instance.get(LCDTarget.Number.WHQL);
    switch (targetPatch) {
      case StandAlone:
        integratedPatchList = whqlTarget.filter.getRange(0, 6);
        break;
      case Integrated:
        integratedPatchList = whqlTarget.filter.getRange(7, 13);
        break;
    }

  }

//  public double[] getValueAdjustArray() {
//    double luminance = model.getLuminance().Y;
//    double[] adjustArray = new double[6];
//
//    for (int x = 0; x < 6; x++) {
//      Patch p = integratedPatchList.get(x);
//      RGB rgb = new RGB(targetColorSpace,
//                        p.getRGB().getValues(new double[3],
//                                             RGB.MaxValue.Double1),
//                        RGB.MaxValue.Double1);
//      //原始的HSV
//      HSV hsv = new HSV(rgb);
//
//      //算出目標XYZ
//      CIEXYZ XYZ = rgb.toXYZ();
//      //將目標XYZ調整至LCD物理量一致
//      XYZ.times(luminance);
//      //計算出LCD上理想的RGB
//      RGB rgb2 = model.getRGB(XYZ, false);
//      //推回HSV
//      HSV hsv2 = new HSV(rgb2);
//      //計算hue應該的調整量
//
//      adjustArray[x] = getValueAdjust(hsv.V, hsv2.V, hsv.getMinimum());
////    adjustArray[x] = hsv2.H - hsv.H;
//    }
//
//    return adjustArray;
//  }

//  public double[] getHueAdjustArray() {
//    double luminance = model.getLuminance().Y;
//    double[] adjustArray = new double[6];
//
//    for (int x = 0; x < 6; x++) {
//      Patch p = integratedPatchList.get(x);
//      RGB rgb = new RGB(targetColorSpace,
//                        p.getRGB().getValues(new double[3],
//                                             RGB.MaxValue.Double1),
//                        RGB.MaxValue.Double1);
//      //原始的HSV
//      HSV hsv = new HSV(rgb);
//
//      //算出目標XYZ
//      CIEXYZ XYZ = rgb.toXYZ();
//      //將目標XYZ調整至LCD物理量一致
//      XYZ.times(luminance);
//      //計算出LCD上理想的RGB
//      RGB rgb2 = model.getRGB(XYZ, false);
//      //推回HSV
//      HSV hsv2 = new HSV(rgb2);
//      //計算hue應該的調整量
//      if (hsv.H == 0 && hsv2.H >= 345) {
//        adjustArray[x] = hsv2.H - 360;
//      }
//      else {
//        adjustArray[x] = hsv2.H - hsv.H;
//      }
//    }
//
//    return adjustArray;
//  }

//  public double[] getSaturationAdjustArray(Method method) {
//    double luminance = model.getLuminance().Y;
//    double[] adjustArray = new double[6];
//
//    for (int x = 0; x < 6; x++) {
//      Patch p = integratedPatchList.get(x);
//      RGB rgb = new RGB(targetColorSpace,
//                        p.getRGB().getValues(new double[3],
//                                             RGB.MaxValue.Double1),
//                        RGB.MaxValue.Double1);
//      //原始的HSV
//      HSV hsv = new HSV(rgb);
//
//      //算出目標XYZ
//      CIEXYZ XYZ = rgb.toXYZ();
//      //將目標XYZ調整至LCD物理量一致
//      XYZ.times(luminance);
//      //計算出LCD上理想的RGB
//      RGB rgb2 = model.getRGB(XYZ, false);
//      //推回HSV
//      HSV hsv2 = new HSV(rgb2);
//      //計算Saturation應該的調整量
//      adjustArray[x] = getSaturationAdjust(hsv.S, hsv2.S, method);
//    }
//
//    return adjustArray;
//  }
  double[] hueAdjustArray = new double[6];
  double[] saturationAdjustArray = new double[6];
  double[] valueAdjustArray = new double[6];

  public double[] getHueAdjustArray() {
    return hueAdjustArray;
  }

  public double[] getValueAdjustArray() {
    return valueAdjustArray;
  }

  public double[] getSaturationAdjustArray(SaturationFormula saturationFormula) {
    double luminance = model.getLuminance().Y;
//    double[] saturationAdjustArray = new double[6];

    for (int x = 0; x < 6; x++) {
      Patch p = integratedPatchList.get(x);
      RGB rgb = new RGB(targetColorSpace,
                        p.getRGB().getValues(new double[3],
                                             RGB.MaxValue.Double1),
                        RGB.MaxValue.Double1);
      //原始的HSV
      HSV hsv = new HSV(rgb);

      //算出目標XYZ
      CIEXYZ XYZ = rgb.toXYZ();
      //將目標XYZ調整至LCD物理量一致
      XYZ.times(luminance);
      //計算出LCD上理想的RGB
      RGB rgb2 = model.getRGB(XYZ, false);
      //推回HSV
      HSV hsv2 = new HSV(rgb2);
      //========================================================================
      //計算Saturation應該的調整量
      //========================================================================
      double saturationAdjust = saturationFormula.getAdjustValue(hsv.S, hsv2.S);
      if (saturationFormula instanceof IntegerSaturationFormula) {
        saturationAdjust = ( (IntegerSaturationFormula) saturationFormula).
            getAdjustOffset(saturationAdjust);
      }

      saturationAdjustArray[x] = saturationAdjust;
      //========================================================================
      // h
      //========================================================================
      double deltaH = hsv2.H - hsv.H;
      if (hsv.H == 0 && hsv2.H >= 345) {
        deltaH = hsv2.H - 360.;
      }
      else {
        deltaH = hsv2.H - hsv.H;
      }
      deltaH = ( (int) Math.round(deltaH / 360. * 768)) / 768. * 360;
      hueAdjustArray[x] = deltaH;
      //========================================================================

      //========================================================================
      // v
      //========================================================================
      short max = (short) Math.round(hsv.V / 100. * 1023);
      short min = (short) Math.round(hsv.getMinimum() / 100. * 1023);
      short newValue = (short) Math.round(hsv2.V / 100. * 1023);
      double valueAdjust = ValuePrecisionEvaluator.getOffset(max, min, newValue);
      valueAdjustArray[x] = valueAdjust;
      //========================================================================

    }

    return saturationAdjustArray;
  }

  /**
   *
   * @param originalValue double
   * @param newValue double
   * @param min double
   * @return double
   */
//  private static double getValueAdjust(double originalValue,
//                                       double newValue, double min) {
//    double deltaValue = newValue - originalValue;
//    double chroma = originalValue - min;
//    double offset = deltaValue /
//        (chroma * (255 - originalValue) * originalValue / 128 / 128 / 128);
//    double offset2 = deltaValue /
//        (chroma * (100 - originalValue) * originalValue / 128 / 128 / 128);
//    return offset;
//  }

//  private static double getSaturationAdjust(double originalSaturation,
//                                            double newSaturation,
//                                            Method method) {
//    switch (method) {
//      case Gain:
//        return SaturationImageAdjustor.gain.getAdjustValue(originalSaturation,
//            newSaturation);
//      case Modified:
//        return SaturationImageAdjustor.quadratic.getAdjustValue(
//            originalSaturation, newSaturation);
//      case Modified2:
//        return SaturationImageAdjustor.cubic.getAdjustValue(
//            originalSaturation, newSaturation);
////      case Modified12:
////        return SaturationImageAdjustor.modified12.getAdjustValue(
////            originalSaturation, newSaturation);
//      case Combine:
//        return SaturationImageAdjustor.twoQuadratic.getAdjustValue(
//            originalSaturation, newSaturation);
////      case GainCombine:
////        return SaturationImageAdjustor.gaincombine.getAdjustValue(
////            originalSaturation, newSaturation);
//      case Richard:
//        return SaturationImageAdjustor.richard.getAdjustValue(
//            originalSaturation, newSaturation);
//    }
//    return -1;
//  }

  static enum Method {
    Gain, Modified, Modified2, Modified12, Combine, GainCombine, Richard
  }

  static List<RGB> getNonNeutralWHQLRGBList() {
    List<RGB> rgbList = LCDTarget.Instance.getRGBList(LCDTarget.Number.WHQL);
    List<RGB> result = new ArrayList<RGB> ();
//    Set<RGB> r = new tree
    for (RGB rgb : rgbList) {
      if (!rgb.isGray()) {
        result.add(rgb);
      }
    }
    return result;
  }

  static void sortByHue(List<RGB> rgbList) {
    Collections.sort(rgbList, new HueComparator());
//    Arrays.binarySearch()
  }

  public static void main(String[] args) {
//    deltaTest(args);

    List<RGB> rgbList = getNonNeutralWHQLRGBList();
//    sortByHue(rgbList);
    for (RGB rgb : rgbList) {
      HSV hsv = new HSV(rgb);
      System.out.println(rgb + " " + hsv);
    }

  }

  public static void deltaTest(String[] args) {
    int sLength = 26;
    int vLength = 18;

    double[][] grid = new double[sLength][vLength];
    double offset = 10;
    for (int sindex = 0; sindex < sLength; sindex++) {
      for (int vindex = 0; vindex < vLength; vindex++) {
        double v = vindex * 15;
        double s = sindex * 4;
//      for (int v = 0; v <= 255; v += 15) {
        double deltaV = AdjustmentEvaluator.ModifiedHSV.getDeltaValue(offset, s,
            v, false);
        grid[sindex][vindex] = deltaV;
      }
//  AdjustmentEvaluator.ModifiedHSV.getDeltaValue()
    }

    double[] sArray = new double[sLength];
    double[] vArray = new double[vLength];
    for (int x = 0; x < sLength; x++) {
      sArray[x] = x * 4;
    }
    for (int x = 0; x < vLength; x++) {
      vArray[x] = x * 15;
    }

    Plot3D plot = Plot3D.getInstance();
//    DoubleArray.f
    plot.addGridPlot("", vArray, sArray, grid);
    plot.setAxeLabel(0, "Value");
    plot.setAxeLabel(1, "Saturation");
    plot.setVisible();

  }
}
