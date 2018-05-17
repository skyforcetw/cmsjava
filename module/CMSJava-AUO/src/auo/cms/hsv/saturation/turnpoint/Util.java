package auo.cms.hsv.saturation.turnpoint;

import shu.cms.lcd.LCDTarget;
import auo.cms.hsv.saturation.backup.SaturationImageAdjustor;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.*;
import auo.cms.hsv.old.HSVAdjustProducer;
import shu.cms.lcd.LCDTargetBase;
import shu.cms.devicemodel.lcd.LCDModel;
import shu.cms.devicemodel.lcd.MultiMatrixModel;
import shu.cms.colorspace.depend.RGB;
import auo.cms.hsv.*;
import shu.cms.DeltaEReport;
import java.util.*;
import shu.cms.*;
import auo.cms.hsv.saturation.IntegerSaturationFormula;
import auo.cms.hsv.value.backup.ValuePrecisionEvaluator;

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
public class Util {
  /**
   *
   * @param hsv HSV
   * @param hsvAdjust double[]
   * @param formula IntegerSaturationFormula
   * @return HSV
   * @deprecated
   */
  public static HSV getNewHSV(HSV hsv, double[] hsvAdjust,
                              IntegerSaturationFormula formula) {
    double hueAdjust = hsvAdjust[0];
    double saturationAdjust = hsvAdjust[1];
    double valueAdjust = hsvAdjust[2];
    //====================================================================
    // HSV的調整
    //====================================================================
    HSV hsv2 = (HSV) hsv.clone();
    hsv2.H += hueAdjust;
    hsv2.H = hsv2.H < 0 ? hsv2.H + 360 : hsv2.H;
    hsv2.S = formula.getSaturartion(hsv.S, saturationAdjust);
    hsv2.S = hsv2.S > 100 ? 100 : hsv2.S;
    hsv2.V = ValuePrecisionEvaluator.getV( (short) (hsv.V / 100. * 1023),
                                          (short) (hsv.getMinimum() /
        100. * 1023), (byte) valueAdjust);
    hsv2.V = hsv2.V / 1023. * 100;
    return hsv2;
  }

  public static LCDModel model;
  public static CIEXYZ modelWhiteXYZ;
  public static HSVAdjustProducer producer;
  public static HSVAdjustProducer produceHSVAdjustProducer() {
    return produceHSVAdjustProducer("sRGB Adjust Evaluation/fpga.xls",
                                    LCDTarget.Number.Ramp1024);
  }

  public static HSVAdjustProducer produceHSVAdjustProducer(String
      targetFilename, LCDTargetBase.Number number) {
    HSVAdjustProducer.TargetPatch targetPatch = HSVAdjustProducer.TargetPatch.
        Integrated;
    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(targetFilename,
        number);
    LCDTarget.Operator.gradationReverseFix(target);
    target.changeMaxValue(RGB.MaxValue.Int8Bit);
    model = new MultiMatrixModel(target);
    model.produceFactor();
    modelWhiteXYZ = model.getWhiteXYZ(false);
//    model.setAutoRGBChangeMaxValue(true);
    RGB.ColorSpace targetColorspace = SaturationImageAdjustor.
        getTargetColorSpace(target, RGB.ColorSpace.sRGB_gamma22);
    producer = new
        HSVAdjustProducer(targetColorspace, model, targetPatch);
    return producer;
  }

  public static void main(String[] args) {
    produceHSVAdjustProducer();
//    LCDTarget target = LCDTarget.Instance.getFromAUOXLS(
//        "D:\\軟體\\nobody zone\\exp data\\HSV IP\\110418\\871-sRGB.xls");
    LCDTarget target = LCDTarget.Instance.getFromAUOXLS(
        "D:\\軟體\\nobody zone\\exp data\\HSV IP\\110418\\871-cc24.xls");
//    DeltaEReport de = DeltaEReport.Instance.CIELabReport()

    List<Patch> patchList = target.getPatchList();
    int size = patchList.size();
    List<CIELab> measureLabList = new ArrayList<CIELab> (size);
    List<CIELab> modelLabList = new ArrayList<CIELab> (size);
    for (int x = 0; x < size; x++) {
      Patch p = target.getPatch(x);
      CIELab lab1 = p.getLab();
      measureLabList.add(lab1);
      RGB rgb = p.getRGB();
      CIEXYZ XYZ = model.getXYZ(rgb, false);
      CIELab lab2 = new CIELab(XYZ, modelWhiteXYZ);
      modelLabList.add(lab2);
    }
    int start = 0; //729 cube
    int end = 729;
//    int start = 736; //sRGB Integrated
//    int end = 743;
//    int start = 743; //IEC61966-4
//    int end = 776;
    measureLabList = measureLabList.subList(start, end);
    modelLabList = modelLabList.subList(start, end);
//    for (CIELab lab : measureLabList) {
//      System.out.println(lab);
//    }
    DeltaEReport deReport = DeltaEReport.Instance.CIELabReport(measureLabList,
        modelLabList);
    System.out.println(deReport);
  }
}
