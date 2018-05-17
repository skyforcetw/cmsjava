package auo.cms.hsv.autotune.test;

import auo.cms.hsv.autotune.*;
import auo.cms.hsv.saturation.*;
import auo.cms.prefercolor.model.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.profile.*;
import shu.io.files.ExcelFile;
import java.io.*;
import jxl.read.biff.*;
import shu.cms.*;
import java.util.*;
import auo.cms.hsv.HSVVersion;

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
public class AutoTunerSimpler {
  public static void main(String[] args) {
    String rampFilename = "hsv autotune/B101EVN06/Measurement02_.xls";
    //目前sRGB只有定義單點調整, 所以不適合採用Multi Spot, 除非增加sRGBsRGBTuneTarget對多點的支援
    TuneParameter tp1 = getsRGBTuneParameter(rampFilename,
                                             AutoTuner.FitMode.SingleSpot);
    System.out.println(tp1.toToolkitFormatString());
//    TuneParameter tp2 = getsRGBTuneParameter(rampFilename,
//                                             AutoTuner.FitMode.MultiSpot);
//    System.out.println(tp2.toToolkitFormatString());
//    TuneParameter tp3 = getPreferredColorTuneParameter(rampFilename);
//    System.out.println(tp3.toToolkitFormatString());
  }

  private final static PreferredColorTuneTarget getPreferredTuneTarget(
      LCDTarget
      rampLCDTarget) {

    //限制在面板的色域內
    ProfileColorSpace limitpcs = ProfileColorSpaceUtils.
        getProfileColorSpaceFromRampTarget(rampLCDTarget);

    PreferredColorModel preferredColorModel = PreferredColorModel.
        getInstance(limitpcs);
    //產生出sRGB訊號規格下的 Preferred Color Space
    ProfileColorSpace pcs = preferredColorModel.
        produceHSVPreferredProfileColorSpaceInsRGBSignal();

    PreferredColorTuneTarget target = new PreferredColorTuneTarget(pcs,
        limitpcs);
    target.setPreferredColorModel(preferredColorModel);
    return target;
  }

  public final static TuneParameter getPreferredColorTuneParameter(String
      rampFilename) {

    MultiMatrixModel model = getMultiMatrixModel(rampFilename);
    LCDTarget rampLCDTarget = model.getLCDTarget();

    IntegerSaturationFormula integerSaturationFormula = new
        IntegerSaturationFormula( (byte) 7, 4);
    ColorIndex colorIndex = new ColorIndex(model);
//    colorIndex.setKlch(1, 1, 1);
    AutoTuner autotuner = new AutoTuner(model, integerSaturationFormula,
                                        colorIndex);
    autotuner.setFitMode(AutoTuner.FitMode.MultiSpot);

    PreferredColorTuneTarget tuneTarget = getPreferredTuneTarget(rampLCDTarget);
    tuneTarget.setSkinFirstInHSVGrid(true);
    tuneTarget.setType(PreferredColorTuneTarget.Type.HSVGrid);
    TuneParameter tuneParameter = autotuner.getTuneParameter(tuneTarget);
    PreferredColorModel preferredColorModel = tuneTarget.
        getPreferredColorModel();
    double[] hueOfHSVArray = preferredColorModel.getHueOfHSVArray();
    tuneParameter.interpolate(hueOfHSVArray);

    return tuneParameter;
  }

  private final static LCDTargetBase.Number number = LCDTargetBase.Number.
      Ramp256_6Bit;

  private final static MultiMatrixModel getMultiMatrixModel(String rampFilename) {
    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(rampFilename,
        number);
    LCDTarget.Operator.gradationReverseFix(target);
    target.changeMaxValue(RGB.MaxValue.Int8Bit);
    MultiMatrixModel model = new MultiMatrixModel(target);
    model.produceFactor();
    return model;
  }

  public final static TuneParameter getsRGBTuneParameter(String rampFilename,
      AutoTuner.FitMode fitMode) {
//    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(rampFilename);
//    LCDTarget.Operator.gradationReverseFix(target);
////target.changeMaxValue(RGB.MaxValue.Int8Bit);
//    MultiMatrixModel model = new MultiMatrixModel(target);
//    model.produceFactor();
    MultiMatrixModel model = getMultiMatrixModel(rampFilename);

    ColorIndex index = new ColorIndex(model);
    IntegerSaturationFormula isf = new IntegerSaturationFormula( (byte) 15, 4);

    AutoTuner tuner = new AutoTuner(model, isf, index);
    tuner.setFitMode(fitMode);
    tuner.setHSVVersion(HSVVersion.v1);

    ProfileColorSpace pcs = getiPad2ProfileColorSpace();
//    sRGBTuneTarget sRGB = new sRGBTuneTarget(sRGBTuneTarget.Patches.Integrated);
    sRGBTuneTarget sRGB = new sRGBTuneTarget(sRGBTuneTarget.Patches.Integrated,
                                             pcs);
    TuneParameter tuneParameter = tuner.getTuneParameter(sRGB);
    return tuneParameter;
  }

  public final static TuneParameter getAUOPreferredTuneParameter(String
      rampFilename) {
    return null;
  }

  private final static ProfileColorSpace getiPad2ProfileColorSpace() {
    ExcelFile xls = null;
    try {
      xls = new ExcelFile(
          "hsv autotune/B101EVN06/ipad2_sRGB_table_20120106.xls"); //richard的
//          "hsv autotune/B101EVN06/ipad2_sRGB_table_20111219.xls"); //acer的
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
    xls.selectSheet("Data");
    List<Patch> patchList = new ArrayList<Patch> ();
    for (int y = 1; y < 47; y++) {
      double r = xls.getCell(1, y);
      double g = xls.getCell(2, y);
      double b = xls.getCell(3, y);
      r = (r == 192) ? 191 : r;
      g = (g == 192) ? 191 : g;
      b = (b == 192) ? 191 : b;

      double Y = xls.getCell(4, y);
      double x_ = xls.getCell(5, y);
      double y_ = xls.getCell(6, y);
      CIExyY xyY = new CIExyY(x_, y_, Y);
      RGB rgb = new RGB(r, g, b);
      Patch p = new Patch(rgb.toString(), xyY.toXYZ(), null, rgb);
      patchList.add(p);
    }
    LCDTarget target = LCDTarget.Instance.get(patchList, LCDTarget.Number.WHQL, false);
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(target, "");
    return pcs;
  }

}
