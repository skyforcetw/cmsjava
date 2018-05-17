package auo.cms.hsv.autotune;

import java.util.*;

import auo.cms.colorspace.depend.*;
import auo.cms.hsv.saturation.*;
import auo.cms.hsv.value.*;
import auo.cms.hsvinteger.*;
import flanagan.math.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.plot.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.math.Interpolation;
import auo.cms.hsv.HSVVersion;
import auo.cms.hsv.value.backup.*;

//import auo.cms.hsv.old.*;

///import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * ClipIndex ColorIndex(dE) LCDModel
 * AutoTuner -> TuneParameter
 * TuneTarget -> sRGB/GMB getTarget(int hue,double saturation, double value);
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */

public class AutoTuner {

  public static enum FitMode {
    SingleSpot, MultiSpot //, Plane
  }

  /**
   * 單點或多點
   */
  private FitMode fitMode = FitMode.SingleSpot;
  LCDModel model;
  private Index index;
  public void setFitMode(FitMode fitMode) {
    this.fitMode = fitMode;
  }

  public AutoTuner(LCDModel model, Index index) {
    this(model, null, index);
  }

  public AutoTuner(LCDModel model,
                   IntegerSaturationFormula integerSaturationFormula) {
    this(model, integerSaturationFormula, null);
  }

  public AutoTuner(LCDModel model,
                   IntegerSaturationFormula integerSaturationFormula,
                   Index index) {
    this.model = model;
    modelWhiteXYZ = this.model.getWhiteXYZ();
    model.setMaxValue(RGB.MaxValue.Double255);
    this.integerSaturationFormula = integerSaturationFormula;
    this.index = index;
  }

  private RGB.MaxValue bitDepth = RGB.MaxValue.Int9Bit;
  public void setPanelBitDepth(RGB.MaxValue bitDepth) {
    this.bitDepth = bitDepth;
  }

  private double[] indexes;
  public double[] getIndexes() {
    return indexes;
  }

  private double tolerance = -1;
  public void setTolerance(double tolerance) {
    this.tolerance = tolerance;
  }

  private TuneParameter getTuneParameterMultiSpot(TuneTarget tuneTarget) {
    // 1. 要決定Integer Saturation的轉折點
    // 2. 再計算出調整量
    SingleHueAdjustValue[] singleHueAdjustValue = new SingleHueAdjustValue[24];
    SingleHueAdjustValue[] interSingleHueAdjustValue = doInterTuneParameter ?
        new SingleHueAdjustValue[24] : null;
    double luminance = model.getWhiteXYZ().Y;
    indexes = new double[24];

    //共24區, 每區15度
    for (double hue = 0; hue < 360; hue += 7.5) {
      if (!doInterTuneParameter && hue % 15 != 0) {
        continue;
      }
      int index = (int) hue / 15;
      // 1. 取得基礎值
      HSV[] tuneSpots = tuneTarget.getTuneSpots(hue);
      int tuneSpotsSize = tuneSpots.length;
      // 2. 取得目標值CIEXYZ
      CIEXYZ[] targetXYZArray = new CIEXYZ[tuneSpotsSize];
      for (int x = 0; x < tuneSpotsSize; x++) {
        HSV hsv = tuneSpots[x];
        CIEXYZ targetXYZ = tuneTarget.getTarget(hsv);
        //若要有tolerance, 先在這邊擴充

        if (targetXYZ.getNormalizeY() == NormalizeY.Normal1) {
          targetXYZ.times(luminance);
        }
        CIEXYZ newTargetXYZ = getTargetXYZ(targetXYZ);
        targetXYZArray[x] = newTargetXYZ;
      }

      // 3. 對H/S/V最佳化, 並且以model預測所有點的色差值, 並取色差最小者
      int initIndex = findSuitableSpotIndex(tuneSpots);
      SingleHueAdjustValue hsvAdjustValue = getOptimalHSVAdjustValue(tuneSpots,
          targetXYZArray, initIndex);

      if (doSkipPrimaryColorAdjust && hue % 60 == 0) {
        hsvAdjustValue.hueAdjustValue = (short) Math.round(hue / 360. * 768);
      }

      if (hue % 15 == 0) {
        indexes[index] = this.minimumIndex;
        singleHueAdjustValue[index] = hsvAdjustValue;
      }
      else if (doInterTuneParameter) {
        interSingleHueAdjustValue[index] = hsvAdjustValue;
      }
      //========================================================================
      // verify
      //========================================================================

      if (doCheckSpot) {
        checkSpot(hue, hsvAdjustValue);
      }

    }

    TuneParameter tuneParameter = new TuneParameter(singleHueAdjustValue);
    if (doInterTuneParameter) {
      interTuneParameter = new TuneParameter(interSingleHueAdjustValue);
    }
    return tuneParameter;
  }

  /**
   *
   * @param normalTuneParameter TuneParameter
   * @param interTuneParameter TuneParameter
   * @return TuneParameter
   * @deprecated
   */
  public final static TuneParameter getTuneParameter(TuneParameter
      normalTuneParameter, TuneParameter interTuneParameter) {

    double[] interHueAdjust = new double[26];
    System.arraycopy(IntArray.toDoubleArray(interTuneParameter.
                                            getHueAdjustValue()), 0,
                     interHueAdjust, 1, 24);
    interHueAdjust[0] = interHueAdjust[24] - 768;
    interHueAdjust[25] = interHueAdjust[1] + 768;

    double[] interSaturationAdjust = new double[26];
    System.arraycopy(IntArray.toDoubleArray(interTuneParameter.
                                            getSaturationAdjustValue()), 0,
                     interSaturationAdjust, 1, 24);
    interSaturationAdjust[0] = interSaturationAdjust[24];
    interSaturationAdjust[25] = interSaturationAdjust[1];

    double[] interValueAdjust = new double[26];
    System.arraycopy(IntArray.toDoubleArray(interTuneParameter.
                                            getValueAdjustValue()), 0,
                     interValueAdjust, 1, 24);
    interValueAdjust[0] = interValueAdjust[24];
    interValueAdjust[25] = interValueAdjust[1];

    Interpolation1DLUT interHueLut = new Interpolation1DLUT(DoubleArray.buildX(
        -7.5, 360, 26), interHueAdjust, Interpolation1DLUT.Algo.LINEAR);
    Interpolation1DLUT interSaturatioLut = new Interpolation1DLUT(DoubleArray.
        buildX( -7.5, 360, 26), interSaturationAdjust,
        Interpolation1DLUT.Algo.LINEAR);
    Interpolation1DLUT interValueLut = new Interpolation1DLUT(DoubleArray.
        buildX( -7.5, 360, 26), interValueAdjust,
        Interpolation1DLUT.Algo.LINEAR);

    short[] normalHueAdjust = normalTuneParameter.getHueAdjustValue();
    byte[] normalSaturationAdjust = normalTuneParameter.
        getSaturationAdjustValue();
    byte[] normalValueAdjust = normalTuneParameter.getValueAdjustValue();

    for (int x = 0; x < 24; x++) {
      int hue = x * 15;
      double h = interHueLut.getValue(hue);
      double s = interSaturatioLut.getValue(hue);
      double v = interValueLut.getValue(hue);
      normalHueAdjust[x] = (short) Math.round( (normalHueAdjust[x] + h) / 2);
      normalSaturationAdjust[x] = (byte) Math.round( (normalSaturationAdjust[x] +
          s) / 2);
      normalValueAdjust[x] = (byte) Math.round( (normalValueAdjust[x] + v) / 2);
    }

    TuneParameter result = new TuneParameter(normalHueAdjust,
                                             normalSaturationAdjust,
                                             normalValueAdjust);
    return result;
  }

  private TuneParameter interTuneParameter;
  /**
   * 是否計算區與區之間的調整值(7.5度)
   */
  private boolean doInterTuneParameter = false;
  /**
   * 是否略過主/補色不要調
   */
  private boolean doSkipPrimaryColorAdjust = false;
  public void setDoSkipPrimaryColorAdjust(boolean skip) {
    this.doSkipPrimaryColorAdjust = skip;
  }

  public void setDoInterTuneParameter(boolean doInterTuneParameter) {
    this.doInterTuneParameter = doInterTuneParameter;
  }

  public TuneParameter getInterTuneParameter() {
    return interTuneParameter;
  }

  /**
   * 找到最佳化的HSVAdjustValue
   * 會將ColorIndex降到最小
   * @param tuneSpots HSV[]
   * @param targetXYZArray CIEXYZ[]
   * @param initIndex int 起始產生調整量的patch index
   * @return HSVAdjustValue
   */
  private SingleHueAdjustValue getOptimalHSVAdjustValue(final HSV[] tuneSpots,
      CIEXYZ[] targetXYZArray, int initIndex) {
    if (! (this.index instanceof ColorIndex)) {
      throw new IllegalStateException("! (this.index instanceof ColorIndex)");
    }
    //先強制轉型成ColorIndex, 否則沒辦法把目標值送進去
    final ColorIndex colorIndex = (ColorIndex) index;
    //送目標值, 好計算色差
    colorIndex.setTargetCIEXYZ(targetXYZArray);
    colorIndex.setTargetWhiteXYZ(modelWhiteXYZ);
    //會在func裡面狂被call, 所以乾脆先把變數固定下來
    final int tuneSpotsSize = tuneSpots.length;

    //==========================================================================
    //用來計算色差及最佳化(色差)的函數
    //==========================================================================
    MinimisationFunction func = new MinimisationFunction() {
      public double function(double[] param) {
        //把參數轉為HSVAdjustValue, 方便計算成HSV
        SingleHueAdjustValue hsvAdjustValue = new SingleHueAdjustValue( (short)
            param[0], (byte) param[1], (byte) param[2]); //調整後的HSV
        HSV[] adjustedTuneSpot = new HSV[tuneSpotsSize];
        //計算出所有調整後的HSV
        for (int i = 0; i < tuneSpotsSize; i++) {
          HSV hsv = tuneSpots[i];
          //調整後的HSV
          HSV adjustedHSV = getAdjustedHSV(hsv, hsvAdjustValue);
          //轉回RGB, 因為要量化
          RGB adjustedRGB = adjustedHSV.toRGB();
          //量化
          adjustedRGB.changeMaxValue(bitDepth);
          //量化之後再轉回HSV
          adjustedHSV = new HSV(adjustedRGB);
          //塞到陣列裡
          adjustedTuneSpot[i] = adjustedHSV;
        }
        //把調整後HSV塞到Index去
        colorIndex.setTuneSpots(adjustedTuneSpot);
        //計算出index, 也就是色差
        double index = colorIndex.getIndex();
        return index;
      };
    };
    //==========================================================================

    Minimisation minimisation = new Minimisation();
    minimisation.supressNoConvergenceMessage();

    //==========================================================================
    // 限制範圍
    //==========================================================================
    // hue
    minimisation.addConstraint(0, -1, 0);
    minimisation.addConstraint(0, 1, 768);
    // saturation
    minimisation.addConstraint(1, -1, -64);
    minimisation.addConstraint(1, 1, 53);
    // value
    minimisation.addConstraint(2, -1, -64);
    minimisation.addConstraint(2, 1, 63);
    //==========================================================================

    //以第一個點為調整參考值, 若只有一個點, 大概也就是這個值(吧?)
    SingleHueAdjustValue init = getHSVAdjustValue(new AUOHSV(tuneSpots[
        initIndex]), targetXYZArray[initIndex]);
    //找到最小點
    double[] step = new double[] {
        1, 1, 1};
    double[] initAdjustValues = init.getAdjustDoubleArray();
//    initAdjustValues[1] = 0;
//    initAdjustValues[2] = 0;

    minimisation.nelderMead(func, initAdjustValues, step);
    minimumIndex = minimisation.getMinimum();

    double[] paramValues = minimisation.getParamValues();
    byte s = (byte) Math.round(paramValues[1]);
    byte v = (byte) Math.round(paramValues[2]);
    s = (s > 63) ? 63 : s;
    s = (s < -64) ? -64 : s;
    v = (v > 63) ? 63 : v;
    v = (v < -64) ? -64 : v;
    SingleHueAdjustValue hsvAdjustValue = new SingleHueAdjustValue(
        (short) Math.round(paramValues[0]), (byte) s, (byte) v);
    return hsvAdjustValue;
  }

  private static int findSuitableSpotIndex(HSV[] tuneSpots) {
    int size = tuneSpots.length;
    List<Integer> indexList = new ArrayList<Integer> ();
    for (int x = 0; x < size; x++) {
      HSV hsv = tuneSpots[x];
      if (hsv.S > 40 && hsv.S < 60) {
        indexList.add(x);
      }
    }

    for (int index : indexList) {
      HSV hsv = tuneSpots[index];
      if (hsv.V > 40 && hsv.V < 60) {
        return index;
      }
    }
    return 0;
  }

  private double minimumIndex;

  private static short getHueAdjustValue(AUOHSV modefiedHSV) {
    return (short) (Math.round(modefiedHSV.getHueInDegree() / 360. * 768));
  }

  private byte getSaturationAdjustValue(AUOHSV hsv, AUOHSV modefiedHSV) {
    if (HSVVersion.v2 == hsvVersion) {
      double saturationAdjust = integerSaturationFormula.getAdjustValue(hsv.
          saturation, modefiedHSV.saturation);
      //量化
      return integerSaturationFormula.getAdjustOffset(
          saturationAdjust);
    }
    else if (HSVVersion.v1 == hsvVersion) {
      if (null == originalSaturationFormula) {
        originalSaturationFormula = new OriginalSaturationFormula();
      }
      double gain = originalSaturationFormula.getAdjustValue(hsv.saturation,
          modefiedHSV.saturation);
      byte adjustValues = originalSaturationFormula.getAdjustOffset(gain);
      return adjustValues;
    }
    else {
      throw new IllegalStateException("");
    }
  }

  private HSVVersion hsvVersion = HSVVersion.v2;
  public void setHSVVersion(HSVVersion version) {
    this.hsvVersion = version;
  }

  private OriginalSaturationFormula originalSaturationFormula;
  private byte getValueAdjustValue(AUOHSV hsv, AUOHSV modefiedHSV) {

    short max = hsv.value;
    short min = hsv.min;
    short newValue = modefiedHSV.value;
    if (HSVVersion.v2 == hsvVersion) {
      //量化
      return ValuePrecisionEvaluator.getOffset(max, min, newValue);
    }
    else if (HSVVersion.v1 == hsvVersion) {

      int offset = modefiedHSV.value - hsv.value;
      double originalValue = (offset / 1.) / ( ( (max - min) / 1.) / 128);
      byte adjustValue = (byte) Math.round(originalValue);
      return adjustValue;
    }
    else {
      throw new IllegalStateException("");
    }
  }

  private HSV getAdjustedHSV(HSV originalHSV,
                             SingleHueAdjustValue hsvAdjustValue) {

    //==========================================================================
    // hue
    //==========================================================================
    double newHue = hsvAdjustValue.getDoubleHueAdjustValue();
    //==========================================================================

    //==========================================================================
    // saturation
    //==========================================================================
    double newSaturation = integerSaturationFormula.getSaturartion(originalHSV.
        S, hsvAdjustValue.saturationAdjustValue);
    //==========================================================================
    RGB rgb = originalHSV.toRGB();
    rgb.changeMaxValue(RGB.MaxValue.Int10Bit);
    short max = (short) rgb.getValue(rgb.getMaxChannel());
    short min = (short) rgb.getValue(rgb.getMinChannel());
    short shortValue = ValuePrecisionEvaluator.getV(max, min,
        hsvAdjustValue.valueAdjustValue);
    double newValue = shortValue / 1023. * 100;
    //==========================================================================
    HSV adjustedHSV = new HSV(originalHSV.getRGBColorSpace(),
                              new double[] {newHue, newSaturation, newValue});
    return adjustedHSV;
  }

  private CIEXYZ modelWhiteXYZ;

  /**
   * 是否透過色彩外貌去調整?
   * 是必要的, XYZ的調整只能保證物理量相等, 視覺不見得相等
   */
  private boolean adjustByColorAppearance = true;

  private SingleHueAdjustValue getHSVAdjustValue(AUOHSV hsv, CIEXYZ targetXYZ) {
    //直接從XYZ轉RGB, 將造成白點無法對齊
    RGB targetRGB = model.getRGB(targetXYZ, false);
    targetRGB.changeMaxValue(RGB.MaxValue.Int10Bit);
    AUOHSV targetHSV = new AUOHSV(targetRGB);

    //========================================================================
    // Hue
    //========================================================================
    //量化
    short hueAdjustValue = getHueAdjustValue(targetHSV);
    //========================================================================
    //========================================================================
    //計算Saturation應該的調整量
    //========================================================================
    byte saturationAdjustValue = getSaturationAdjustValue(hsv, targetHSV);
    //========================================================================
    //========================================================================
    // Value
    //========================================================================
    //量化
    byte valueAdjustValue = getValueAdjustValue(hsv, targetHSV);

    SingleHueAdjustValue hsvAdjustValue = new SingleHueAdjustValue(
        hueAdjustValue, saturationAdjustValue, valueAdjustValue);

    return hsvAdjustValue;
  }

  private CIEXYZ getTargetXYZ(CIEXYZ originalTargetXYZ) {
    CIEXYZ tuneTargetWhite = tuneTarget.getTargetWhite();
    if (adjustByColorAppearance) {
      //CIEXYZ(in target white) ==> CIELab(in human vision) ==> CIEXYZ(in model white) ==> HSV
      CIELab targetLab = new CIELab(originalTargetXYZ, tuneTargetWhite);
      CIEXYZ newTargetXYZ = CIELab.toXYZ(targetLab, modelWhiteXYZ);
      return newTargetXYZ;
    }
    else {
      return originalTargetXYZ;
    }
  }

  private boolean interpolateInSingleSpot = true;

  /**
   *
   * @param tuneTarget TuneTarget
   * @return TuneParameter
   */
  private TuneParameter getTuneParameterSingleSpot(TuneTarget tuneTarget) {
    // 1. 要決定Integer Saturation的轉折點
    // 2. 再計算出調整量

    SingleHueAdjustValue[] singleHueAdjustValues = new SingleHueAdjustValue[24];
    //共24區, 每區15度
    for (int hue = 0; hue < 360; hue += 15) {
      HSV hsv = tuneTarget.getTuneSpot(hue);
      CIEXYZ targetXYZ = tuneTarget.getTarget(hue);
      if (null == targetXYZ) {
        //會造成null
        continue;
      }

      /**
       * @todo 若要有tolerance, 先在這邊擴充
       */
//      if (targetXYZ.getNormalizeY() == NormalizeY.Normal1) {
//        targetXYZ.times(luminance);
//      }


      CIEXYZ newTargetXYZ = getTargetXYZ(targetXYZ);
      SingleHueAdjustValue hsvAdjustValue = getHSVAdjustValue(new AUOHSV(hsv),
          newTargetXYZ);

      int index = hue / 15;
      singleHueAdjustValues[index] = hsvAdjustValue;
      if (doCheckSpot) {
        checkSpot(hue, hsvAdjustValue);
      }
      //========================================================================
    }

    if (interpolateInSingleSpot) {
      interpolateInSingleSpot(singleHueAdjustValues);
    }

    TuneParameter tuneParameter = new TuneParameter(singleHueAdjustValues);
    return tuneParameter;
  }

  private int findNonNullIndex(SingleHueAdjustValue[]
                               singleHueAdjustValues, int start) {
    int size = singleHueAdjustValues.length;
    for (int x = start; x < size; x++) {
      if (null != singleHueAdjustValues[x]) {
        return x;
      }
    }
    return -1;
  }

  private void interpolateInSingleSpot(SingleHueAdjustValue[]
                                       singleHueAdjustValues) {
    int lastNonNullIndex = -1;
    for (int hue = 0; hue < 360; hue += 15) {
      int index = hue / 15;
      SingleHueAdjustValue adjustValue = singleHueAdjustValues[index];
      if (null == adjustValue) {
        int nonNullIndex = findNonNullIndex(singleHueAdjustValues, index);
        SingleHueAdjustValue v0 = singleHueAdjustValues[lastNonNullIndex];
        boolean around = false;
        if ( -1 == nonNullIndex) {
          nonNullIndex = findNonNullIndex(singleHueAdjustValues, 0);
          around = true;
        }
        SingleHueAdjustValue v1 = singleHueAdjustValues[nonNullIndex];
        int nextNonNullIndex = around ? nonNullIndex + 24 : nonNullIndex;
        short h = (short) Math.round(Interpolation.linear(lastNonNullIndex,
            nextNonNullIndex, v0.hueAdjustValue,
            v1.hueAdjustValue + (around ? 768 : 0),
            index));

        byte s = (byte) Math.round(Interpolation.linear(lastNonNullIndex,
            nextNonNullIndex, v0.saturationAdjustValue,
            v1.saturationAdjustValue, index));

        byte v = (byte) Math.round(Interpolation.linear(lastNonNullIndex,
            nextNonNullIndex, v0.valueAdjustValue, v1.valueAdjustValue, index));
        singleHueAdjustValues[index] = new SingleHueAdjustValue(h, s, v);
      }
      else {
        lastNonNullIndex = index;
      }
    }

  }

  //============================================================================
  // check
  //============================================================================
  private boolean doCheckSpot = true;
  private boolean doCheckSpot_HuePlane = false;
  private boolean doCheckSpot_Hue60 = false;
  //============================================================================

  private IntegerHSVIP hsvIP;
  private RGB getNewHSVIPRGB(HSV hsv, SingleHueAdjustValue hsvAdjustValue) {
    if(null == hsvIP) {
//      hsvIP = new IntegerHSVIP()
    }
    AUOHSV auoHSV = new AUOHSV(hsv);
    short[] hsvValues = IntegerHSVIP.getHSVValues(auoHSV, hsvAdjustValue,
                                                  integerSaturationFormula, true);
    AUOHSV newAUOHSV = AUOHSV.fromHSVValues3(hsvValues);
    RGB newRGB = newAUOHSV.toRGB();
    return newRGB;
  }

  private boolean showMessage = false;
  /**
   *
   * @param hue double
   * @param hsvAdjustValue SingleHueAdjustValue
   */
  private void checkSpot(double hue, SingleHueAdjustValue hsvAdjustValue) {
    if (tuneTarget instanceof SingleTuneTarget) {
      //如果是單點, 就check該點的色差
      HSV tuneSpot = tuneTarget.getTuneSpot(hue);
      RGB newRGB = getNewHSVIPRGB(tuneSpot, hsvAdjustValue);
      CIELab newLab = model.getLab(newRGB, true);
      CIELab targetLab = tuneTarget.getTargetLab(tuneSpot);
      if (showMessage) {
        System.out.println("Tune Spot: " + tuneSpot + " DE00:" +
                           new DeltaE(newLab, targetLab).getCIE2000DeltaE());
      }
    }
    HuePlanePlotter inspector = new HuePlanePlotter(hsvAdjustValue, this);
    if (doCheckSpot_Hue60 && hue == 60) {
      Plot3D hue60Plot = Plot3D.getInstance(Double.toString(hue) + "!");
      inspector.plotTuneTargetHuePlane(hue, hue60Plot, "target",
                                       java.awt.Color.red);
      inspector.plotHSVIPHuePlane(hue, hue60Plot, "new", java.awt.Color.black);
      hue60Plot.setVisible();
    }
    if (doCheckSpot_HuePlane) {
      inspector.plotModelHuePlane(hue).setVisible();
      inspector.plotHSVIPHuePlane(hue).setVisible();
      inspector.plotTuneTargetHuePlane(hue).setVisible();
    }
  }

  IntegerSaturationFormula integerSaturationFormula;
  private void produceIntegerSaturationFormula() {
    integerSaturationFormula = new IntegerSaturationFormula( (byte) 7, 4);
  }

  public TuneParameter getTuneParameter(TuneTarget tuneTarget) {
    this.tuneTarget = tuneTarget;

    if (null == integerSaturationFormula) {
      produceIntegerSaturationFormula();
    }
    /**
     * multi和single可視為同依個case, 只是single只看一個點而已(嗎?)
     */
    switch (fitMode) {
      case SingleSpot:
        return getTuneParameterSingleSpot(tuneTarget);
      case MultiSpot:
        if (null == index) {
          throw new IllegalStateException("null == index");
        }
        return getTuneParameterMultiSpot(tuneTarget);
      default:
        throw new IllegalArgumentException("");
    }
  }

  TuneTarget tuneTarget;

}
