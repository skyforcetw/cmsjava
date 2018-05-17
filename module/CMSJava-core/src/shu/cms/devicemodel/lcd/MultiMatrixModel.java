package shu.cms.devicemodel.lcd;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.LCDModelBase.*;
import shu.cms.devicemodel.lcd.util.*;
import shu.cms.lcd.*;
//import vv.cms.lcd.material.Material;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;

//import shu.cms.devicemodel.lcd.material.AutoCPOptions;
//import vv.cms.lcd.material.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 前導模式:
 * 去除掉漏光影響後,假設LCD加成性依舊成立,進行XYZ的相加
 * 反推模式:
 * 利用多個不同RGB的XYZ值所產生的max matrix,不斷的反推RGB,直到可容許為止
 *
 * @note 1:FlareType務必設為Darkest,是為了保證所有relative XYZ都是正數.
 * 要是有relative XYZ是負數,getRGB計算時,產生的maxInverMatrix會有問題,造成計算誤差很大.
 * 因此要保證XYZ為正數,務必設定參數為Darkest.
 *
 * @note 2:到目前為止(08/03/22)所做的次次準確LCD Model
 * @note 3:排名變更, 為次準確LCD Model(08/05/16)
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MultiMatrixModel
    extends ChannelIndependentModel {

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * gamma校正的方法.
   * (不建議用ByPower)
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: </p>
   *
   * @author not attributable
   * @version 1.0
   */
  public static enum GammaCorrectMethod {

    ByPower, ByLuminance
  }

  private GammaCorrectMethod gammaCorrectMethod = GammaCorrectMethod.
      ByLuminance;

  /**
   * 使用模式
   * @param factor LCDModelFactor
   */
  public MultiMatrixModel(LCDModelFactor factor) {
    super(factor);
    this.rational.setDoRGBRational(true);
  }

  /**
   * 求值模式
   * @param lcdTarget LCDTarget
   */
  public MultiMatrixModel(LCDTarget lcdTarget) {
    this(lcdTarget, lcdTarget, GammaCorrectMethod.ByLuminance);
  }

  static {
//    setFlareType(FlareType.Darkest);
  }

  /**
   * 求值模式
   * @param lcdTarget LCDTarget
   * @param rCorrectLCDTarget LCDTarget
   * @param gammaCorrectMethod GammaCorrectMethod
   */
  public MultiMatrixModel(LCDTarget lcdTarget, LCDTarget rCorrectLCDTarget,
                          GammaCorrectMethod gammaCorrectMethod) {
    super(lcdTarget, rCorrectLCDTarget);

    integerMaxValue = RGB.MaxValue.getIntegerMaxValueByLevel(lcdTarget.getLevel());
    if (integerMaxValue == null) {
      integerMaxValue = RGB.MaxValue.Int8Bit;
    }

    this.gammaCorrectMethod = gammaCorrectMethod;
    this.rational.setDoRGBRational(true);

  }

  private LCDTargetInterpolator interpolator;
  /**
   * 採用multi-matrix求解時,最多的迭代(求解)次數
   */
  public final static int MAX_ITERATIVE_TIMES = 50;
  private boolean touchMaxIterativeTimes = false;

  /**
   * 重置 touch max iterative次數 的計算
   */
  protected void resetTouchMaxIterativeTimes() {
    touchMaxIterativeTimes = false;
  }

  /**
   * 設定max iterative的次數
   * @param times int
   */
  protected void setTouchMaxIterativeTimes(int times) {
    touchMaxIterativeTimes = (times == MAX_ITERATIVE_TIMES - 1);
  }

  public static enum GetRGBMode {
    //單純使用下Mode1比Mode2來的準

    Mode1(true, false), //Mode1, 不用Matries2, 即是用最大值RGB去組成矩陣
    Mode2(false, true), //Mode2, 採用Matries2, 即是用最相近的亮度的白色(R=G=B), 去組成矩陣
    //但是兩者搭配使用又會比單獨使用更來的準, 但是其數值會比較不smooth
    Mode12(true, true);

    private GetRGBMode(boolean mode1, boolean mode2) {
      this.mode1 = mode1;
      this.mode2 = mode2;
    }

    private boolean mode1;
    private boolean mode2;
  }

  /**
   * 設定getRGB時採用的模式, mode1較為穩定
   */
  private GetRGBMode getRGBMode = GetRGBMode.Mode1;
  /* GetRGBMode.valueOf(AutoCPOptions.getString(
    "MultiMatrix_GetRGBMode"));*/

  public void setGetRGBMode(GetRGBMode mode) {
    this.getRGBMode = mode;
  }

  /**
   * 計算RGB,反推模式
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, Factor[] factor) {
    CIEXYZ rationalXYZ = (CIEXYZ) XYZ.clone();
    rational.XYZRationalize(rationalXYZ);

    RGB rgb1 = null, rgb2 = null;

    //==========================================================================
    // 利用兩種不同的演算法, 計算出色差最小者
    // 這是一個 1+1 >2 的最好例子, 互補缺點, 善用優點
    //==========================================================================
    if (getRGBMode.mode1) {
      rgb1 = getRGBByMultiMatrixRange(rationalXYZ, false);
    }
    if (getRGBMode.mode2) {
      rgb2 = getRGBByMultiMatrixRange(rationalXYZ, true);
    }
    if (getRGBMode.mode1 == true && getRGBMode.mode1 == getRGBMode.mode2) {
      double deltaE1 = calculateGetRGBDeltaE(rgb1, rationalXYZ, true).
          getCIE2000DeltaE();
      double deltaE2 = calculateGetRGBDeltaE(rgb2, rationalXYZ, true).
          getCIE2000DeltaE();
      return (deltaE1 < deltaE2) ? rgb1 : rgb2;
    }
    else {
      return getRGBMode.mode1 ? rgb1 : rgb2;
    }

    //==========================================================================
  }

  private double[] minLuminanceRGBValues;
  private boolean avoidLessThenZero = true;

  private void avoidLessThenZero(RGB rgb) {
    if (avoidLessThenZero) {
      double maxChannelValue = rgb.getValue(rgb.getMaxChannel());
      maxChannelValue = maxChannelValue <= 0 ? 1 : maxChannelValue;
      for (RGB.Channel ch : RGB.Channel.RGBChannel) {
        if (rgb.getValue(ch) <= 0) {
          rgb.setValue(ch, maxChannelValue);
          rgb.setFixed(true);
        }
      }
//      rgb.R = (rgb.R <= 0) ? maxChannelValue : rgb.R;
//      rgb.G = (rgb.G <= 0) ? maxChannelValue : rgb.G;
//      rgb.B = (rgb.B <= 0) ? maxChannelValue : rgb.B;
    }
  }

  /**
   *
   * @param luminanceRoughRGB RGB
   * @param XYZ CIEXYZ
   * @return RGB 新的Luminance RGB
   */
  protected RGB getRGBByMultiMatrix(final RGB luminanceRoughRGB,
                                    final CIEXYZ XYZ) {
    //==========================================================================
    // 修正為DAC Value
    //==========================================================================
    RGB rgb = (RGB) luminanceRoughRGB.clone();
    double[] luminanceRGBValues = new double[3];
    rgb.getValues(luminanceRGBValues, RGB.MaxValue.Double1);
    correct.gammaUncorrect(rgb);
    //==========================================================================

    //產生最大值矩陣最好不要有0值
    avoidLessThenZero(rgb);
    double[][] inverseMatrix = getXYZInverseMatrix(rgb);
    if (inverseMatrix == null) {
      //直接回傳原本的luminanceRoughRGB即可
      return luminanceRoughRGB;
    }
    else {
      //此時回傳的RGB為luminance RGB的比值
      RGB newRGB = matries.XYZToRGBByMatrix(XYZ, inverseMatrix);

      if (newRGB == null) {
        return luminanceRoughRGB;
      }
      //========================================================================
      // 這兩個步驟極為重要! 首先要將RGB作正規化到0~1,才能乘上原本的RGB luminance值
      // 計算出來的才是正確新的RGB luminance
      //========================================================================

      newRGB.changeMaxValue(RGB.MaxValue.Double1);
      double[] rgbValues = new double[3];

      rgbValues[0] = newRGB.R * luminanceRGBValues[0];
      rgbValues[1] = newRGB.G * luminanceRGBValues[1];
      rgbValues[2] = newRGB.B * luminanceRGBValues[2];
      //========================================================================

      rgbValues[0] = luminanceRGBValues[0] < minLuminanceRGBValues[0] ? 0
          : rgbValues[0];
      rgbValues[1] = luminanceRGBValues[1] < minLuminanceRGBValues[1] ? 0
          : rgbValues[1];
      rgbValues[2] = luminanceRGBValues[2] < minLuminanceRGBValues[2] ? 0
          : rgbValues[2];
      rgb.setValues(rgbValues, RGB.MaxValue.Double1);
      changeMaxValue(rgb);
      rgbValues = null;
      return rgb;
    }
  }

  /**
   * 將luminanceRGB轉回DAC Value RGB後,再量化,看是否相等
   * @param luminanceRGB1 RGB
   * @param luminanceRGB2 RGB
   * @param maxValue MaxValue
   * @return boolean
   */
  private boolean equalsAfterQuantization(RGB luminanceRGB1,
                                          RGB luminanceRGB2,
                                          RGB.MaxValue maxValue) {
    RGB rgb1 = (RGB) luminanceRGB1.clone();
    RGB rgb2 = (RGB) luminanceRGB2.clone();
    correct.gammaUncorrect(rgb1);
    correct.gammaUncorrect(rgb2);
    return rgb1.equalsAfterQuantization(rgb2, maxValue);
  }

  private RGB.MaxValue integerMaxValue;
  private matries2 matries2 = new matries2();

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 找到亮度最相近的white的XYZ, 以此XYZ當作 最大值矩陣 去進行matrix運算, 得到第一筆RGB值
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  protected class matries2
      implements MatriesInterface {

    protected Set<Patch> wChannelPatch;
    protected GammaCorrector _WrCorrector;

    protected matries2() {
      wChannelPatch = lcdTarget.filter.grayScalePatchSet(RGBBase.Channel.W);
      _WrCorrector = GammaCorrector.getLUTInstance(wChannelPatch,
          RGBBase.Channel.W);
    }

    /**
     * 找到相同亮度的白色頻道code
     * @param XYZ CIEXYZ
     * @return double
     */
    protected double getWhiteDACValues(CIEXYZ XYZ) {
      CIEXYZ normalXYZ = (CIEXYZ) XYZ.clone();
      if (normalXYZ.getNormalizeY() == NormalizeY.Not) {
        normalXYZ.normalize(getLuminance());
      }
      else {
        normalXYZ.normalize(NormalizeY.Normal1);
      }
      double whiteDACValues = _WrCorrector.uncorrect(normalXYZ.Y);
      return whiteDACValues;
    }

    /**
     *
     * @param XYZ CIEXYZ
     * @return RGB
     */
    public final RGB XYZToRGBByMaxMatrix(CIEXYZ XYZ) {
      double w = getWhiteDACValues(XYZ);
      RGB luminanceRGB = new RGB(RGB.ColorSpace.unknowRGB, new double[] {w,
                                 w, w}, RGB.MaxValue.Double1);
      changeMaxValue(luminanceRGB);
      RGB newRGB = getRGBByMultiMatrix(luminanceRGB, XYZ);
      return newRGB;
    }

    public CIEXYZ RGBToXYZByMaxMatrix(RGB rgb) {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * 將RGB數值作擴展,取得最小的色差作回傳
   * (getRGBByMultiMatrix的進階版)
   * @param XYZ CIEXYZ
   * @param byMatries2 boolean 採用byMatries2計算
   * @return RGB
   */
  protected RGB getRGBByMultiMatrixRange(final CIEXYZ XYZ, boolean byMatries2) {
    RGB rgb = null;
    if (byMatries2) {
      rgb = matries2.XYZToRGBByMaxMatrix(XYZ);
    }
    else {
      rgb = matries.XYZToRGBByMaxMatrix(XYZ);
    }
    rgbResult.firstRGB = (RGB) rgb.clone();
    avoidLessThenZero(rgb);
    RGB oldrgb = rgb;

    for (int x = 0; x < MAX_ITERATIVE_TIMES; x++) {
      RGB newRGB = getRGBByMultiMatrix(rgb, XYZ);

      if ( (newRGB.equals(rgb) || newRGB.equals(oldrgb))
          && isRGBClosely(newRGB, rgb)) {
        //RGB相等,RGB是否夠接近,newRGB是否與上上次RGB相等
        break;
      }

      if (equalsAfterQuantization(newRGB, rgb, integerMaxValue)) {
        //量化後相等
        break;
      }

      oldrgb = rgb;
      rgb = newRGB;
      setTouchMaxIterativeTimes(x);
    }

    correct.gammaUncorrect(rgb);
    //將正規化的DAC RGB轉回原始大小
    changeMaxValue(rgb);

    rational.RGBRationalize(rgb);
    return rgb;
  }

  /**
   * step的一半
   */
  private final double halfStep = lcdTarget.getStep() / 2.;
//  private boolean rangeRGBWork = false;
//
//  public final boolean isRangeRGBWork() {
//    return rangeRGBWork;
//  }

  /**
   * 驗證first與second兩者是否是合理的RGB(是否相近)
   * @param first RGB
   * @param second RGB
   * @return boolean
   */
  private final boolean isRGBClosely(RGB first, RGB second) {
    //正負號不同的次數
    int diffSignTimes = 0;
    //差異達到兩倍的次數
    int doubleTimes = 0;

    for (RGBBase.Channel c : RGBBase.Channel.RGBChannel) {
      double v1 = first.getValue(c);
      double v2 = second.getValue(c);
      double t = v1 * v2;
      double min = Math.min(v1, v2);
      //正負號不同的次數, 正負號不同會使 t<0,
      diffSignTimes += (t < 0)
          ? //如果在一半的step以下又異號, 代表兩者其實很接近0, 可忽略這個問題
          (Math.abs(min) < halfStep ? 0 : 1) : 0;
      t = (Math.max(v1, v2) / min);
      //差異達到兩倍
      doubleTimes += (!Double.isInfinite(t) && t >= 2.) ? 1 : 0;

      if (diffSignTimes >= 1 && doubleTimes >= 1) {
        return false;
      }
    }

    return true;
  }

  /**
   * 計算XYZ值的反矩陣
   * @param rgb RGB
   * @return double[][] 回傳
   */
  private final double[][] getXYZInverseMatrix(RGB rgb) {
    CIEXYZ rXYZ = this.getXYZ(rgb.R, 0, 0);
    CIEXYZ gXYZ = this.getXYZ(0, rgb.G, 0);
    CIEXYZ bXYZ = this.getXYZ(0, 0, rgb.B);

    double[][] maxMatrix = matries.getMaxMatrix(rXYZ, gXYZ, bXYZ);
    DoubleArray.abs(maxMatrix);
    if (!DoubleArray.isNonsingular(maxMatrix)) {
      return null;
    }
    else {
      return DoubleArray.inverse(maxMatrix);
    }
  }

  /**
   * 計算XYZ,前導模式
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, Factor[] factor) {
    CIEXYZ XYZ = getXYZ(rgb.R, rgb.G, rgb.B);
    return XYZ;
  }

  /**
   * 根據r/g/b內插出XYZ
   * @param r double
   * @param g double
   * @param b double
   * @return CIEXYZ relative XYZ,方便內部運算用
   */
  protected CIEXYZ getXYZ(double r, double g, double b) {
    return getXYZ(r, g, b, true);
  }

  protected CIEXYZ getXYZ(double r, double g, double b, boolean relativeXYZ) {
    //interpolator所計算出來的XYZ都是絕對值
    CIEXYZ rXYZ = interpolator.getPatch(RGBBase.Channel.R, r).getXYZ();
    CIEXYZ gXYZ = interpolator.getPatch(RGBBase.Channel.G, g).getXYZ();
    CIEXYZ bXYZ = interpolator.getPatch(RGBBase.Channel.B, b).getXYZ();

    return recoverAbsoluteOrRelative(rXYZ, gXYZ, bXYZ, relativeXYZ);
  }

  public CIEXYZ getNeutralXYZ(double code, boolean relativeXYZ) {
    CIEXYZ XYZ = interpolator.getPatch(RGBBase.Channel.W, code).getXYZ();
    //因為interpolator出來是絕對值, 所以反而要相反處理
    //所以這邊要採用fromXYZ而非toXYZ, 而且要把relativeXYZ施以負邏輯
    XYZ = fromXYZ(XYZ, !relativeXYZ);
    return XYZ;
  }

  /**
   * 將rXYZ+gXYZ+bXYZ的absolute值轉到relative or absolute
   * @param rXYZ CIEXYZ
   * @param gXYZ CIEXYZ
   * @param bXYZ CIEXYZ
   * @param relativeXYZ boolean
   * @return CIEXYZ
   */
  private final CIEXYZ recoverAbsoluteOrRelative(CIEXYZ rXYZ, CIEXYZ gXYZ,
                                                 CIEXYZ bXYZ,
                                                 boolean relativeXYZ) {
    CIEXYZ flare = this.flare.getFlare();
    CIEXYZ recover = LCDModelUtil.recover(rXYZ, gXYZ, bXYZ, flare);
    if (relativeXYZ) {
      //recover的結果,會剩一組flare,再把他扣掉,回傳相對值給LCDModel去處理
      recover = CIEXYZ.minus(recover, flare);
    }
    return recover;
  }

  /**
   * 求係數
   *
   * @return Factor[]
   */
  protected Factor[] _produceFactor() {
    Interpolation.Algo[] interpolationType = LCDTargetInterpolator.
        InterpolationLinear;
    interpolator = LCDTargetInterpolator.Instance.get(lcdTarget,
        interpolationType);

    switch (gammaCorrectMethod) {
      case ByPower:
        singleChannel.produceRGBPowerPatch();
        correct.producePowerCorrector();
        break;
      case ByLuminance:
        singleChannel.produceRGBPatch();
        correct.produceGammaCorrector();
        break;
    }

    //計算最小亮度值
    double minValue = lcdTarget.getStep() / lcdTarget.getMaxValue().max;
    double[] minValues = new double[] {
        minValue, minValue, minValue};
    minLuminanceRGBValues = correct.gammaCorrect(minValues);

    Factor[] factors = new Factor[] {
        null, null, null};
    return factors;
  }

  /**
   * getDescription
   *
   * @return String
   */
  public String getDescription() {
    return "MultiMatrix";
  }

  public static void main(String[] args) {

    LCDTarget lcdTarget = LCDTarget.Instance.getFromAUORampXLS("Measurement Files\\Monitor\\auo_T370HW02\\ca210\\darkroom\\native\\110922\\Measurement00_.xls",
        LCDTarget.Number.Ramp1024);
    LCDTarget.Operator.gradationReverseFix(lcdTarget);
//    LCDTarget lcdTarget = Material.getStoredLCDTarget();
    MultiMatrixModel.setFlareType(MultiMatrixModel.FlareType.Black);
    MultiMatrixModel mm = new MultiMatrixModel(lcdTarget);
    mm.produceFactor();
    System.out.println(lcdTarget.getBlackPatch());
    System.out.println(lcdTarget.getDarkestPatch());
    System.out.println(mm.getXYZ(0, 0, 0, true));
  }

  /**
   * 上一次的getRGB計算,是否達到MaxIterativeTimes
   * @return boolean
   */
  public boolean isTouchMaxIterativeTimes() {
    return touchMaxIterativeTimes;
  }
}
