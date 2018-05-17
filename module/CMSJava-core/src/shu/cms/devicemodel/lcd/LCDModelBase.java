package shu.cms.devicemodel.lcd;

import java.util.*;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.*;
import shu.cms.devicemodel.lcd.util.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.util.*;

//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class LCDModelBase
    extends DeviceCharacterizationModel {
  public LCDModelBase(ModelFactor modelFactor) {
    super(modelFactor);
  }

  protected LCDModelBase() {
    super();
  }

  /**
   * 取得白點的亮度
   * @return CIEXYZ
   */
  public CIEXYZ getLuminance() {
    return luminance;
  }

  /**
   * 螢幕亮度(絕對值)
   */
  protected CIEXYZ luminance;
  protected LCDTarget lcdTarget;
  protected double[][] maxInverse;
  protected double[][] max;

  protected LCDTarget rCorrectLCDTarget;

  /**
   * 導具的參考白(未減掉漏光)
   */
  protected CIEXYZ targetWhitePoint;
  /**
   * 瑩幕導表的白,為未減掉漏光的相對值
   * @return CIEXYZ
   */
  public CIEXYZ getLCDTargetWhite() {
    return targetWhitePoint;
  }

  public LCDTarget getLCDTarget() {
    return lcdTarget;
  }

  public RGB.MaxValue getMaxValue() {
    if (maxValue != null) {
      return maxValue;
    }
    else if (null != lcdTarget) {
      return lcdTarget.getMaxValue();
    }
    else {
      return null;
    }
  }

  private RGB.MaxValue maxValue = null;
  public void setMaxValue(RGB.MaxValue maxValue) {
    this.maxValue = maxValue;
  }

  public void changeMaxValue(RGB rgb) {
    rgb.changeMaxValue(getMaxValue());
  }

  public Correct correct = new Correct();

  public class Correct {

    /**
     * Gamma反校正
     * 反gamma修正,將luminance RGB修正回DAC RGB
     * @param rgb RGB
     */
    public void gammaUncorrect(RGB rgb) {
      double[] rgbValues = new double[3];
      rgb.getValues(rgbValues, RGB.MaxValue.Double1);
      //反gamma修正,將luminance RGB修正回DAC RGB
      correct.gammaUncorrect(rgbValues);
      rgb.setFixed(rgb.isFixed() || correct.hasCorrectedInRange());
      //此時的rgb為DAC RGB
      rgb.setValues(rgbValues, RGB.MaxValue.Double1);
    }

    /**
     * Gamma校正
     * gamma修正,將DAC RGB修正成luminance RGB
     * @param rgb RGB
     */
    public void gammaCorrect(RGB rgb) {
      double[] rgbValues = new double[3];
      rgb.getValues(rgbValues, RGB.MaxValue.Double1);
      //gamma修正,將DAC RGB修正成luminance RGB
      correct.gammaCorrect(rgbValues);
      rgb.setFixed(correct.hasCorrectedInRange());
      //此時的rgb為DAC RGB
      rgb.setValues(rgbValues, RGB.MaxValue.Double1);
    }

    public void setCorrectorAlgo(Interpolation1DLUT.Algo algo) {
      if (_RrCorrector != null) {
        _RrCorrector.setLUTAlgo(algo);
      }
      if (_GrCorrector != null) {
        _GrCorrector.setLUTAlgo(algo);
      }
      if (_BrCorrector != null) {
        _BrCorrector.setLUTAlgo(algo);
      }
      if (_RPowerCorrector != null) {
        _RPowerCorrector.setLUTAlgo(algo);
      }
      if (_GPowerCorrector != null) {
        _GPowerCorrector.setLUTAlgo(algo);
      }
      if (_BPowerCorrector != null) {
        _BPowerCorrector.setLUTAlgo(algo);
      }
    }

    /**
     * RGB的亮度gamma校正
     */
    public GammaCorrector _RrCorrector = null;
    public GammaCorrector _GrCorrector = null;
    public GammaCorrector _BrCorrector = null;
    /**
     * RGB的能量gamma校正(能量=X+Y+Z)
     */
    public GammaCorrector _RPowerCorrector;
    public GammaCorrector _GPowerCorrector;
    public GammaCorrector _BPowerCorrector;

    public double[][] getOutputTables() {
      if (!correct.isDoGammaCorrect() && _RrCorrector != null && _GrCorrector != null &&
          _BrCorrector != null) {
        double[] rOutputTable = _RrCorrector.getUncorrectTable(512);
        double[] gOutputTable = _GrCorrector.getUncorrectTable(512);
        double[] bOutputTable = _BrCorrector.getUncorrectTable(512);
        return new double[][] {
            rOutputTable, gOutputTable, bOutputTable
        };
      }
      else {
        return null;
      }
    }

    public double[][] getInputTables() {
      if (!correct.isDoGammaCorrect() && _RrCorrector != null && _GrCorrector != null &&
          _BrCorrector != null) {
        double[] rInputTable = _RrCorrector.getCorrectTable(512);
        double[] gInputTable = _GrCorrector.getCorrectTable(512);
        double[] bInputTable = _BrCorrector.getCorrectTable(512);
        return new double[][] {
            rInputTable, gInputTable, bInputTable
        };
      }
      else {
        return null;
      }
    }

    public GammaCorrector getGammaCorrector(RGBBase.Channel ch) {
      switch (ch) {
        case R:
          return _RrCorrector;
        case G:
          return _GrCorrector;
        case B:
          return _BrCorrector;
        default:
          return null;
      }
    }

    /**
     * 進行gamma校正前,先檢查是否可以進行校正,
     * 避免不能校正的狀況進行校正,將會拋出例外
     */
    private boolean checkGammaCorrect = false;
    private boolean checkPowerCorrect = false;

    /**
     * 是否要進行gamma correct
     * @param doGammaCorrect boolean
     */
    public void setDoGammaCorrect(boolean doGammaCorrect) {
      this.doGammaCorrect = doGammaCorrect;
    }

    /**
     * 是否要進行gamma correct
     * @return boolean
     */
    public boolean isDoGammaCorrect() {
      return doGammaCorrect;
    }

    /**
     * 是否進行gamma校正
     */
    private boolean doGammaCorrect = true;

    /**
     * gamma correct前是否要檢查gamma correct的合理性
     * @param checkGammaCorrect boolean
     */
    public void setCheckGammaCorrect(boolean checkGammaCorrect) {
      this.checkGammaCorrect = checkGammaCorrect;
    }

    /**
     * 進行gamma校正(DAC value修正到Y)
     * @param input double[]
     * @return double[]
     */
    public final double[] gammaCorrect(double[] input) {
      if (checkGammaCorrect &&
          (!_RrCorrector.isCorrectOk(input[0]) ||
           !_GrCorrector.isCorrectOk(input[1]) ||
           !_BrCorrector.isCorrectOk(input[2]))) {
        return null;
      }
      if (_RrCorrector == null || _GrCorrector == null || _BrCorrector == null) {
        throw new IllegalStateException(
            "call correct.produceGammaCorrector() first.");
      }
      if (doGammaCorrect) {
        input[0] = _RrCorrector.correct(input[0]);
        input[1] = _GrCorrector.correct(input[1]);
        input[2] = _BrCorrector.correct(input[2]);
        hasCorrectedInRange = _RrCorrector.hasCorrectedInRange() ||
            _GrCorrector.hasCorrectedInRange() ||
            _BrCorrector.hasCorrectedInRange();
      }
      return input;
    }

    /**
     * 進行gamma反校正(Y轉換回DAC Value)
     * @param input double[]
     * @return double[]
     */
    public final double[] gammaUncorrect(double[] input) {
      if (checkGammaCorrect &&
          (!_RrCorrector.isUncorrectOk(input[0]) ||
           !_GrCorrector.isUncorrectOk(input[1]) ||
           !_BrCorrector.isUncorrectOk(input[2]))) {
        return null;
      }
      if (doGammaCorrect) {
        input[0] = _RrCorrector.uncorrect(input[0]);
        input[1] = _GrCorrector.uncorrect(input[1]);
        input[2] = _BrCorrector.uncorrect(input[2]);
        hasCorrectedInRange = _RrCorrector.hasCorrectedInRange() ||
            _GrCorrector.hasCorrectedInRange() ||
            _BrCorrector.hasCorrectedInRange();
      }
      return input;
    }

    private transient boolean hasCorrectedInRange = false;
    public boolean hasCorrectedInRange() {
      return hasCorrectedInRange;
    }

    /**
     * 進行power校正(DAC value修正到power)
     * @param input double[]
     * @return double[]
     */
    public final double[] powerCorrect(double[] input) {
      if (checkPowerCorrect &&
          (!_RPowerCorrector.isCorrectOk(input[0]) ||
           !_GPowerCorrector.isCorrectOk(input[1]) ||
           !_BPowerCorrector.isCorrectOk(input[2]))) {
        return null;
      }
      input[0] = _RPowerCorrector.correct(input[0]);
      input[1] = _GPowerCorrector.correct(input[1]);
      input[2] = _BPowerCorrector.correct(input[2]);
      return input;
    }

    /**
     * 進行power反校正(power轉換回DAC Value)
     * @param input double[]
     * @return double[]
     */
    public final double[] powerUncorrect(double[] input) {
      if (checkPowerCorrect &&
          (!_RPowerCorrector.isUncorrectOk(input[0]) ||
           !_GPowerCorrector.isUncorrectOk(input[1]) ||
           !_BPowerCorrector.isUncorrectOk(input[2]))) {
        return null;
      }
      input[0] = _RPowerCorrector.uncorrect(input[0]);
      input[1] = _GPowerCorrector.uncorrect(input[1]);
      input[2] = _BPowerCorrector.uncorrect(input[2]);
      return input;
    }

    /**
     * 產生RGB的gamma校正
     */
    public void produceGammaCorrector() {
      if (singleChannel.rChannelPatch == null || singleChannel.gChannelPatch == null ||
          singleChannel.bChannelPatch == null) {
        throw new IllegalStateException(
            "call singleChannel.produceRGBPatch() first.");
      }
      if (_RrCorrector == null) {
        _RrCorrector = GammaCorrector.getLUTInstance(singleChannel.
            rChannelPatch, RGBBase.Channel.R);
      }
      if (_GrCorrector == null) {
        _GrCorrector = GammaCorrector.getLUTInstance(singleChannel.
            gChannelPatch, RGBBase.Channel.G);
      }
      if (_BrCorrector == null) {
        _BrCorrector = GammaCorrector.getLUTInstance(singleChannel.
            bChannelPatch, RGBBase.Channel.B);
      }
    }

    /**
     * 產生能量校正器
     */
    public void producePowerCorrector() {
      if (singleChannel.rChannelPowerPatch == null ||
          singleChannel.gChannelPowerPatch == null ||
          singleChannel.bChannelPowerPatch == null) {
        throw new IllegalStateException(
            "call produceRGBSingleChannelPowerPatch() first.");
      }

      _RPowerCorrector = GammaCorrector.getLUTInstance(singleChannel.
          rChannelPowerPatch,
          RGBBase.Channel.R, GammaCorrector.Method.ByPowerXYZ);
      _GPowerCorrector = GammaCorrector.getLUTInstance(singleChannel.
          gChannelPowerPatch,
          RGBBase.Channel.G, GammaCorrector.Method.ByPowerXYZ);
      _BPowerCorrector = GammaCorrector.getLUTInstance(singleChannel.
          bChannelPowerPatch,
          RGBBase.Channel.B, GammaCorrector.Method.ByPowerXYZ);
    }
  }

  public static abstract class Factor
      extends DeviceCharacterizationModel.Factor {

    public Factor() {

    }

    public abstract double[] getVariables();
  }

  public Flare flare = new Flare();

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * LCDModel的內隱類別,用來將flare相關的函數做邏輯上的分類
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: </p>
   *
   * @author not attributable
   * @version 1.0
   */
  public final class Flare {

    /**
     * 取得ch以外channel所造成的flare,並且以最佳化的方式計算flare本身的r/g/b比例
     * @param ch Channel
     * @return CIEXYZ
     */
    public final CIEXYZ getOptimumBesidesEstimatedFlare(RGBBase.Channel ch) {
      if (optimumFlareProportionValue == -1) {
        FlareProportionCalculator cal = new FlareProportionCalculator(
            lcdTarget);
        optimumFlareProportionValue = cal.getFlareProportionValue();
      }

      return getBesidesEstimatedFlare(ch, optimumFlareProportionValue);
    }

    /**
     * 取得ch以外channel所造成的flare
     * @param ch Channel
     * @param value double
     * @return CIEXYZ
     */
    public final CIEXYZ getBesidesEstimatedFlare(RGBBase.Channel ch,
                                                 double value) {
      CIEXYZ flare1 = null, flare2 = null;
      switch (ch) {
        case R:
          flare1 = getEstimatedFlare(RGBBase.Channel.G, value);
          flare2 = getEstimatedFlare(RGBBase.Channel.B, value);
          break;
        case G:
          flare1 = getEstimatedFlare(RGBBase.Channel.R, value);
          flare2 = getEstimatedFlare(RGBBase.Channel.B, value);
          break;
        case B:
          flare1 = getEstimatedFlare(RGBBase.Channel.R, value);
          flare2 = getEstimatedFlare(RGBBase.Channel.G, value);
          break;
      }

      CIEXYZ flareXYZ = CIEXYZ.plus(flare1, flare2);
      return flareXYZ;
    }

    /**
     * 以最佳化(估測)的方式找到最適當的flare
     * @return CIEXYZ
     */
    public CIEXYZ getEstimatedFlare() {
      if (estimatedFlare == null) {
        FlareCalculator cal = new FlareCalculator(lcdTarget);
        estimatedFlare = cal.getFlare();
      }
      return estimatedFlare;
    }

    public double[] getFlareValues() {
      return flareValues;
    }

    public void setFlare(CIEXYZ flare) {
      this.flareXYZ = flare;
      this.flareValues = flare.getValues();
    }

    protected CIEXYZ flareXYZ;
    protected double[] flareValues;
    private CIEXYZ estimatedFlare;
    private double optimumFlareProportionValue = -1;
    private double[] rgbFlareProportion;

    /**
     * 以估測的漏光,加上code=value時的RGB比例,計算最適當的漏光CIEXYZ
     * @param ch Channel
     * @param value double
     * @return CIEXYZ
     */
    public final CIEXYZ getEstimatedFlare(RGBBase.Channel ch, double value) {
      CIEXYZ flare = getEstimatedFlare();

      RGB keyRGB = lcdTarget.getKeyRGB();
      keyRGB.setColor(Color.black);
      keyRGB.setValue(RGBBase.Channel.R, value);
      CIEXYZ rXYZ = lcdTarget.getPatch(keyRGB).getXYZ();
      keyRGB.setColor(Color.black);
      keyRGB.setValue(RGBBase.Channel.G, value);
      CIEXYZ gXYZ = lcdTarget.getPatch(keyRGB).getXYZ();
      keyRGB.setColor(Color.black);
      keyRGB.setValue(RGBBase.Channel.B, value);
      CIEXYZ bXYZ = lcdTarget.getPatch(keyRGB).getXYZ();

      //==========================================================================
      // 減掉漏光因素
      //==========================================================================
      double[][] m = DoubleArray.transpose(new double[][] {
                                           CIEXYZ.minus(rXYZ, flare).
                                           getValues(),
                                           CIEXYZ.minus(gXYZ, flare).
                                           getValues(),
                                           CIEXYZ.minus(bXYZ, flare).
                                           getValues()
      });
      double[][] mInv = DoubleArray.inverse(m);
      //==========================================================================
      double[] flareProportion = DoubleArray.times(mInv, flare.getValues());

      int chIndex = ch.getArrayIndex();
      double[] flareXYZValues = DoubleArray.times(DoubleArray.transpose(m)[
                                                  chIndex],
                                                  flareProportion[chIndex]);
      return new CIEXYZ(flareXYZValues, targetWhitePoint);
    }

    /**
     * 取得漏光
     * @return CIEXYZ
     */
    public CIEXYZ getFlare() {
      return flareXYZ;
    }

    /**
     * 取得單一頻道所貢獻的漏光
     * @param ch Channel
     * @return CIEXYZ
     */
    public final CIEXYZ getFlare(RGBBase.Channel ch) {
      if (rgbFlareProportion == null) {
        rgbFlareProportion = DoubleArray.times(maxInverse, flareValues);
      }
      int chIndex = ch.getArrayIndex();
      double[] flareXYZValues = DoubleArray.times(DoubleArray.transpose(max)[
                                                  chIndex],
                                                  rgbFlareProportion[chIndex]);
      return new CIEXYZ(flareXYZValues, targetWhitePoint);
    }
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 取得Flare的方式
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: </p>
   *
   * @author not attributable
   * @version 1.0
   */
  public static enum FlareType {

    /**
     * 第一個rgb=0的色塊
     */
    Black,
    /**
     * Y值最低的色塊
     */
    Darkest,
    /**
     * 估測所得的XYZ
     */
    Estimate
  }

  /**
   * 設定Flare取得的方式
   * @param flareType FlareType
   */
  public static void setFlareType(FlareType flareType) {
    FLARE_TYPE = flareType;
  }

  protected static FlareType FLARE_TYPE = FlareType.Estimate;
  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 提供永續儲存係數的class
   * 經由此class所儲存的數值,可以使model以使用模式實體化並且使用
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public static class LCDModelFactor
      extends ModelFactor {

    public double[] flare;
    public double[][] max;
    public double[][] maxInverse;
    public Illuminant illuminant;
    public Factor[] factors;
    public CIEXYZ luminance;
    public CIEXYZ targetWhitePoint;
    public GammaCorrector[] rCorrector;
  }

  protected LuminanceChannel luminanceChannel = new LuminanceChannel();
  public final class LuminanceChannel {
    /**
     * 取得單一頻道有值的亮度Patch Set
     * @return Set
     */
    public final Set<Patch> getLuminancePatchSet() {
      Set<Patch> singleChannelPatch = new TreeSet<Patch> ();

      singleChannelPatch.addAll(rChannelLuminancePatch);
      singleChannelPatch.addAll(gChannelLuminancePatch);
      singleChannelPatch.addAll(bChannelLuminancePatch);
      return singleChannelPatch;
    }

    /**
     * 把R/G/B單一頻道的patch,轉換成luminance RGB
     * @param model LCDModel
     */
    public void produceLuminancePatch(LuminanceRGBModelIF model) {
      singleChannel.produceRGBPatch();
      rChannelLuminancePatch = produceLuminancePatch(singleChannel.
          rChannelPatch,
          model);
      gChannelLuminancePatch = produceLuminancePatch(singleChannel.
          gChannelPatch,
          model);
      bChannelLuminancePatch = produceLuminancePatch(singleChannel.
          bChannelPatch,
          model);
    }

    /**
     * rgb單一頻道的patch set,for luminance RGB
     */
    public Set<Patch> rChannelLuminancePatch;
    public Set<Patch> gChannelLuminancePatch;
    public Set<Patch> bChannelLuminancePatch;

    /**
     * 將singleChannelPatch,依照model內的模式,把RGB轉換成luminance RGB
     * @param singleChannelPatch Set
     * @param model LCDModel
     * @return Set
     */
    protected Set<Patch> produceLuminancePatch(Set<Patch> singleChannelPatch,
        LuminanceRGBModelIF model) {
      int size = singleChannelPatch.size();
      Set<Patch> luminancePatchSet = new LinkedHashSet<Patch> (size);

      for (Patch p : singleChannelPatch) {
        RGB luminanceRGB = model.getLuminanceRGB(p.getRGB());
        Patch luminancePatch = new Patch(p.getName(), p.getXYZ(), p.getXYZ(),
                                         p.getLab(),
                                         luminanceRGB, p.getSpectra(),
                                         p.getReflectSpectra());
        luminancePatchSet.add(luminancePatch);
      }
      return luminancePatchSet;
    }

  }

  public SingleChannel singleChannel = new SingleChannel();

  public final class SingleChannel {

    /**
     * rgb單一頻道的patch Set
     */
    public Set<Patch> rChannelPatch = null;
    public Set<Patch> gChannelPatch = null;
    public Set<Patch> bChannelPatch = null;
    /**
     * rgb單一頻道的patch set,for power
     */
    public Set<Patch> rChannelPowerPatch;
    public Set<Patch> gChannelPowerPatch;
    public Set<Patch> bChannelPowerPatch;

    /**
     * 取得單一頻道有值的Patch Set
     * @return Set
     */
    public final Set<Patch> getPatchSet() {
      Set<Patch> singleChannelPatch = new TreeSet<Patch> ();

      singleChannelPatch.addAll(rChannelPatch);
      singleChannelPatch.addAll(gChannelPatch);
      singleChannelPatch.addAll(bChannelPatch);
      return singleChannelPatch;
    }

    /**
     * 產生(過濾出)R/G/B單一頻道的Patch
     */
    public final void produceRGBPatch() {
      produceRGBPatch(rCorrectLCDTarget, true);
    }

    /**
     * 產生R/G/B單一頻道的patch
     * @param lcdTarget LCDTarget 從lcdTarget產生
     * @param validateY boolean 是否驗證Y值(Luminance),如有不合理的Y值(逆轉)則將該patch刪除
     */
    protected final void produceRGBPatch(LCDTarget lcdTarget,
                                         boolean validateY) {
      //過濾出單一頻道的值
      if (rChannelPatch == null) {
        rChannelPatch = lcdTarget.filter.grayScalePatchSet(RGBBase.Channel.R);
//        rChannelPatch = LCDModelUtil.producePatchSet(lcdTarget,
//            RGBBase.Channel.R);
      }
      if (gChannelPatch == null) {
        gChannelPatch = lcdTarget.filter.grayScalePatchSet(RGBBase.Channel.G);
//        gChannelPatch = LCDModelUtil.producePatchSet(lcdTarget,
//            RGBBase.Channel.G);
      }
      if (bChannelPatch == null) {
        bChannelPatch = lcdTarget.filter.grayScalePatchSet(RGBBase.Channel.B);
//        bChannelPatch = LCDModelUtil.producePatchSet(lcdTarget,
//            RGBBase.Channel.B);
      }
      if (validateY) {
        //確認Y是否為遞增,若為否,則移除反轉值
        rChannelPatch = LCDModelUtil.validate(rChannelPatch, false);
        gChannelPatch = LCDModelUtil.validate(gChannelPatch, false);
        bChannelPatch = LCDModelUtil.validate(bChannelPatch, false);
      }
    }

    public final void produceRGBPowerPatch() {
      produceRGBPowerPatch(rCorrectLCDTarget, true);
    }

    /**
     *
     * @param lcdTarget LCDTarget
     * @param validatePower boolean
     */
    protected final void produceRGBPowerPatch(LCDTarget lcdTarget,
                                              boolean validatePower) {
      //過濾出單一頻道的值
      if (rChannelPowerPatch == null) {
        rChannelPowerPatch = lcdTarget.filter.grayScalePatchSet(RGBBase.Channel.
            R);
//        rChannelPowerPatch = LCDModelUtil.producePatchSet(lcdTarget,
//            RGBBase.Channel.R);
      }
      if (gChannelPowerPatch == null) {
        gChannelPowerPatch = lcdTarget.filter.grayScalePatchSet(RGBBase.Channel.
            G);
//        gChannelPowerPatch = LCDModelUtil.producePatchSet(lcdTarget,
//            RGBBase.Channel.G);
      }
      if (bChannelPowerPatch == null) {
        bChannelPowerPatch = lcdTarget.filter.grayScalePatchSet(RGBBase.Channel.
            B);
//        bChannelPowerPatch = LCDModelUtil.producePatchSet(lcdTarget,
//            RGBBase.Channel.B);
      }
      if (validatePower) {
        //確認Y是否為遞增,若為否,則移除反轉值
        rChannelPowerPatch = LCDModelUtil.validate(rChannelPowerPatch, true);
        gChannelPowerPatch = LCDModelUtil.validate(gChannelPowerPatch, true);
        bChannelPowerPatch = LCDModelUtil.validate(bChannelPowerPatch, true);
      }
    }

  }

  protected matries matries = new matries();

  public final class matries
      implements MatriesInterface {

    /**
     * 透過RGB最大值的max矩陣,預測該XYZ的RGB值
     * @param XYZ CIEXYZ
     * @return RGB luminance
     */
    public final RGB XYZToRGBByMaxMatrix(CIEXYZ XYZ) {
      return XYZToRGBByMatrix(XYZ, maxInverse);
    }

    /**
     * XYZToRGB的結果是否為負
     */
    private transient boolean negativeXYZToRGB = false;
    protected boolean isNegativeXYZToRGB() {
      return negativeXYZToRGB;
    }

    /**
     * 以matrix推算XYZ對應的RGB
     *
     * 以XYZ以及inverseMatrix推算相對應的RGB
     * @param XYZ CIEXYZ
     * @param inverseMatrix double[][]
     * @return RGB luminance RGB
     */
    protected final RGB XYZToRGBByMatrix(CIEXYZ XYZ,
                                         double[][] inverseMatrix
        ) {
      XYZ = rational.XYZLimit(XYZ);

      /**
       * @note 在XYZ過小的狀況下, RGBValues將可能為負數
       */
      double[] RGBValues = LCDModelUtil.XYZ2RGB(XYZ.getValues(),
                                                inverseMatrix);

      negativeXYZToRGB = DoubleArray.hasNegative(RGBValues);

      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, RGBValues,
                        RGB.MaxValue.Double1);
      changeMaxValue(rgb);
      return rgb;
    }

    /**
     * 計算R/G/B最大值的XYZ所形成的矩陣
     * @param RMax CIEXYZ
     * @param GMax CIEXYZ
     * @param BMax CIEXYZ
     * @return double[][]
     */
    protected final double[][] getMaxMatrix(CIEXYZ RMax, CIEXYZ GMax,
                                            CIEXYZ BMax) {
      double[][] maxMatrix = DoubleArray.transpose(new double[][] {
          RMax.getValues(),
          GMax.getValues(),
          BMax.getValues()
      });
      return maxMatrix;

    }

    /**
     * 透過RGB最大值組成的max矩陣,預測該rgb的XYZ值
     * @param rgb RGB
     * @return CIEXYZ
     */
    public CIEXYZ RGBToXYZByMaxMatrix(RGB rgb) {
      if (rgb.getMaxValue() != RGB.MaxValue.Double1) {
        throw new IllegalArgumentException("rgb.maxValue != Double1");
      }
      rgb = rational.RGBRationalize(rgb);

      double[] XYZValues = LCDModelUtil.RGB2XYZ(rgb.getValues(), max);
      CIEXYZ XYZ = new CIEXYZ(XYZValues, targetWhitePoint);

      return rational.XYZLimit(XYZ);
    }
  }

  public Rational rational = new Rational();

  public final class Rational {

    /**
     *
     * @param val double
     * @return double
     * @deprecated
     */
    protected final double XYZrationalize(double val) {
      val = val < 0 ? 0 : val;
      return val;
    }

    /**
     *
     * @param values double[]
     * @return double[]
     * @deprecated
     */
    protected final double[] XYZrationalize(double[] values) {
      int size = values.length;
      for (int x = 0; x < size; x++) {
        values[x] = XYZrationalize(values[x]);
      }
      return values;
    }

    /**
     * 是否要進行RGB的合理化
     * @param doRGBRational boolean
     */
    public void setDoRGBRational(boolean doRGBRational) {
      this.doRGBRational = doRGBRational;
    }

    /**
     * 是否要進行XYZ的最大值限制
     * @param doXYZLimit boolean
     */
    public void setDoXYZLimit(boolean doXYZLimit) {
      this.doXYZLimit = doXYZLimit;
    }

    public void setDoXYZRational(boolean doXYZRational) {
      this.doXYZRational = doXYZRational;
    }

    private double xMax = 0, yMax = 0, zMax = 0;

    /**
     * 限制XYZ不要超過Max
     * (限制之後的deltaE更大?)
     * @param XYZ CIEXYZ
     * @return CIEXYZ
     */
    private CIEXYZ XYZLimit(CIEXYZ XYZ) {
      if (XYZ != null && doXYZLimit) {
        double[] dXYZ = XYZ.getValues();

        if (xMax == 0) {
          xMax = Maths.max(max[0]);
          yMax = Maths.max(max[1]);
          zMax = Maths.max(max[2]);
        }

        dXYZ[0] = (dXYZ[0] > xMax) ? xMax : dXYZ[0];
        dXYZ[1] = (dXYZ[1] > yMax) ? yMax : dXYZ[1];
        dXYZ[2] = (dXYZ[2] > zMax) ? zMax : dXYZ[2];
        XYZ.setValues(dXYZ);
      }
      return XYZ;
    }

    protected CIEXYZ XYZRationalize(CIEXYZ XYZ) {
      if (XYZ != null && doXYZRational) {
//        XYZ.X = XYZrationalize(XYZ.X);
//        XYZ.Y = XYZrationalize(XYZ.Y);
//        XYZ.Z = XYZrationalize(XYZ.Z);
        XYZ.rationalize();
      }
      return XYZ;
    }

    /**
     * 是否要對XYZ作limit動作,limit會限制XYZ在max內
     * 但是limit之後往往會失準,不建議使用
     * (不要用)
     */
    private boolean doXYZLimit = false;
    /**
     * 讓XYZ合理化(>=0)
     */
    private boolean doXYZRational = false;
    /**
     * 是否要對RGB作合理化動作(限制最大在1.0,最小為0)
     * (不要用)
     */
    private boolean doRGBRational = false;

    /**
     * 對RGB進行合理化
     * @param rgb RGB
     * @return RGB
     */
    public RGB RGBRationalize(RGB rgb) {
      if (rgb != null && doRGBRational) {
        rationalized = RGB.rationalize(rgb);
      }
      return rgb;
    }
  }

  private boolean rationalized = false;

  /**
   * 將經過getRGB運算所得到的getRGB數值, 帶到前導模式得到的預測XYZ與實際XYZ的色差
   * @param getRGB RGB
   * @param XYZ CIEXYZ
   * @param relativeXYZ boolean
   * @return DeltaE
   */
  public DeltaE calculateGetRGBDeltaE(RGB getRGB, CIEXYZ XYZ,
                                      boolean relativeXYZ) {
    return calculateGetRGBDeltaE(getRGB, XYZ, null, relativeXYZ);
  }

  public DeltaE calculateGetRGBDeltaE(RGB getRGB, CIEXYZ XYZ, CIEXYZ whitePoint,
                                      boolean relativeXYZ) {
    //==========================================================================
    // 計算inverLab的deltaE
    //==========================================================================
    //計算deltaE用absolute XYZ
    CIEXYZ forwardXYZ = this.getXYZ(getRGB, false);
    CIEXYZ testedXYZ = this.toXYZ(XYZ, !relativeXYZ);
    if (whitePoint == null) {
      return getDeltaE(forwardXYZ, testedXYZ);
    }
    else {
      return new DeltaE(forwardXYZ, testedXYZ, whitePoint);
    }
    //==========================================================================
  }

  /**
   * 由relativeXYZ將XYZ處理成相對或絕對值
   * (由內部處理轉換到外部處理)
   * @param XYZ CIEXYZ
   * @param relativeXYZ boolean
   * @return CIEXYZ
   */
  protected CIEXYZ toXYZ(CIEXYZ XYZ, boolean relativeXYZ) {
    if (relativeXYZ) {
      return XYZ;
    }
    else {
      double[] resultValues = DoubleArray.plus(XYZ.getValues(),
                                               flare.flareValues);
      CIEXYZ result = new CIEXYZ(resultValues, targetWhitePoint);
      return result;
    }
  }

  /**
   * 由relativeXYZ將XYZ處理成相對或絕對值
   * (由外部處理轉換到內部處理)
   * @param XYZ CIEXYZ
   * @param relativeXYZ boolean
   * @return CIEXYZ
   */
  protected CIEXYZ fromXYZ(CIEXYZ XYZ, boolean relativeXYZ) {
    if (relativeXYZ) {
      return XYZ;
    }
    else {
      /**
       * 如果不是relativeXYZ,就是絕對值,要先減掉漏光,再進矩陣運算.
       * 因為矩陣構成時,也已經先減掉漏光的因素了.
       */
      CIEXYZ result = CIEXYZ.minus(XYZ, flare.flareXYZ);
      return result;
    }
  }

  /**
   * 取得與亮度線性的RGB
   * @param XYZ CIEXYZ
   * @param relativeXYZ boolean
   * @return RGB
   */
  public RGB getLinearRGB(CIEXYZ XYZ, boolean relativeXYZ) {
    XYZ = this.fromXYZ(XYZ, relativeXYZ);
    return matries.XYZToRGBByMaxMatrix(XYZ);
  }

  public DeltaE getDeltaE(CIEXYZ XYZ1, CIEXYZ XYZ2) {
    DeltaE dE = new DeltaE(XYZ1, XYZ2, targetWhitePoint);
    dE.getCIE2000DeltaE();
    return dE;
  }

  /**
   * 產生可永續儲存係數的class
   * @param factors Factor[]
   * @return LCDModelFactor
   */
  public final LCDModelFactor produceLCDModelFactor(Factor[] factors) {

    return produceLCDModelFactor(factors, lcdTarget.getDevice(),
                                 lcdTarget.getDescription() + "_" +
                                 this.getDescription());
  }

  public final LCDModelFactor produceLCDModelFactor(Factor[] factors,
      String device, String description) {
    LCDModelFactor factor = new LCDModelFactor();
    factor.flare = this.flare.flareValues;
    factor.max = this.max;
    factor.maxInverse = this.maxInverse;
    factor.luminance = this.luminance;
    factor.rCorrector = new GammaCorrector[] {
        correct._RrCorrector, correct._GrCorrector, correct._BrCorrector
    };
    factor.factors = factors;
    factor.device = device;
    factor.description = description;
    factor.targetWhitePoint = this.lcdTarget.getWhitePatch().getXYZ();
    return factor;

  }

  /**
   * 計算出該色度座標下, 最大可達到的亮度
   * @param xyY CIExyY
   * @return double
   */
  protected double getAvailableWhiteMaxLuminance(CIExyY xyY) {
    CIEXYZ whiteXYZ = xyY.toXYZ();
    CIEXYZ lumi = this.getLuminance();

    double[] whiteXYZValues = whiteXYZ.getValues();
    double[] lumiValues = lumi.getValues();
    double maxY = Double.MIN_VALUE;
    for (int x = 0; x < 3; x++) {
      double ratio = lumiValues[x] / whiteXYZValues[x];
      CIEXYZ adjust = (CIEXYZ) whiteXYZ.clone();
      adjust.times(ratio);
      CIEXYZ diff = CIEXYZ.minus(lumi, adjust);
      double[] diffValues = diff.getValues();
      double min = Maths.min(diffValues);

      if (min >= 0 && adjust.Y > maxY) {
        maxY = adjust.Y;
      }
    }

    return maxY;
  }

  /**
   * 標準model測試報告
   * @param testTarget LCDTarget
   * @param reportMinimumDeltaE double
   * @return String
   */
  public final String getStandardModelTestReport(LCDTarget testTarget,
                                                 double reportMinimumDeltaE) {
    StringBuilder buf = new StringBuilder();

    buf.append("Forward Model Test:\n");
    DeltaEReport[] testReports = testForwardModel(testTarget, false);
    buf.append("Training Target: " + lcdTarget.getDescription() + "\n");
    buf.append(Arrays.toString(testReports) + "\n");
    buf.append(testReports[0].getPatchDeltaEReport(reportMinimumDeltaE) + "\n");

    buf.append("Reverse Model Test:\n");
    DeltaEReport[] testReverseReports = testReverseModel(testTarget, false);
    buf.append("Training Target: " + lcdTarget.getDescription() + "\n");
    buf.append(Arrays.toString(testReverseReports) + "\n");
    buf.append(testReverseReports[0].getPatchDeltaEReport(
        reportMinimumDeltaE) + "\n");
    return buf.toString();
  }

  /**
   *
   * @return String
   */
  protected String getStoreFilename() {
    String desc = Utils.getAcronym(getDescription()) + lcdTarget.getDescription();
    return desc;
  }

  public final static shu.plot.PlotWindow[] plotModelReport(ModelReport report) {
    shu.plot.PlotWindow[] plots = new shu.plot.PlotWindow[6];

    //==========================================================================
    // report analyze
    //==========================================================================
    Plot3D p3a = report.forwardReport[0].getPatchDeltaEReport(new double[] {
        0.0009, 0.0009}).plot();
    p3a.setTitle("forward");
    p3a.addLinePlot("", Color.black, new double[][] {new double[] {0, 0, 0},
                    new double[] {255, 255, 255}
    });
    p3a.rotate(500, -20);
    plots[0] = p3a;

    Plot3D p3b = report.reverseReport[0].getPatchDeltaEReport(
        new double[] {0.0009, 0.0009}).plot();
    p3b.setTitle("reverse");
    p3b.addLinePlot("", Color.black, new double[][] {new double[] {0, 0, 0},
                    new double[] {255, 255, 255}
    });
    p3b.rotate(500, -20);
    plots[1] = p3b;

    Plot2D p2a = report.forwardReport[0].getPatchDeltaEReport(new double[] {
        0.0009, 0.0009}).
        plotDeltaEvsDeltauvPrime(false);
    p2a.setTitle("forward " + p2a.getTitle());
    plots[2] = p2a;

    Plot2D p2b = report.forwardReport[0].getPatchDeltaEReport(new double[] {
        0.0009, 0.0009}).
        plotDeltaEvsDeltauvPrime(true);
    p2b.setTitle("forward " + p2b.getTitle());
    plots[3] = p2b;

    Plot2D p2c = report.reverseReport[0].getPatchDeltaEReport(new double[] {
        0.0009, 0.0009}).
        plotDeltaEvsDeltauvPrime(false);
    p2c.setTitle("reverse " + p2c.getTitle());
    plots[3] = p2c;

    Plot2D p2d = report.reverseReport[0].getPatchDeltaEReport(new double[] {
        0.0009, 0.0009}).
        plotDeltaEvsDeltauvPrime(true);
    p2d.setTitle("reverse " + p2d.getTitle());
    plots[4] = p2d;
    //==========================================================================

    return plots;
  }

  protected String getTrainingTarget() {
    StringBuilder buf = new StringBuilder();
    buf.append("Training Target: " + lcdTarget.getDescription() + "\n");
    return buf.toString();
  }

  public Report report = new Report();
  public class Report {

    /**
     * model測試報告
     * @param testTarget LCDTarget
     * @param reportMinimumDeltaE double
     * @return String
     */
    public final ModelReport getModelReport(LCDTarget
                                            testTarget,
                                            double reportMinimumDeltaE) {
      return getModelReport(testTarget, reportMinimumDeltaE, null, true);
    }

    public final ModelReport getModelReport(LCDTarget
                                            testTarget,
                                            double[] reportMinimumDeltauvp) {
      return getModelReport(testTarget, -1, reportMinimumDeltauvp, true);
    }

    public final ModelReport getModelReport(LCDTarget testTarget) {
      return getModelReport(testTarget, -1, null, false);
    }

    private final ModelReport getModelReport(LCDTarget
                                             testTarget,
                                             double reportMinimumDeltaE,
                                             double[] reportMinimumDeltauvp,
                                             boolean patchDeltaEReport) {
      boolean duvMode = reportMinimumDeltauvp != null;

      StringBuilder buf = new StringBuilder();

      buf.append("Forward Model Test:\n");
      DeltaEReport[] testReports = testForwardModel(testTarget, false);
      buf.append(getTrainingTarget());
      buf.append("Test Target: " + testTarget.getDescription() + "\n");
      buf.append(Arrays.toString(testReports) + "\n");
      if (patchDeltaEReport) {
        if (duvMode) {
          buf.append(testReports[0].getPatchDeltaEReport(reportMinimumDeltauvp) +
                     "\n");
        }
        else if (reportMinimumDeltaE > 0) {
          buf.append(testReports[0].getPatchDeltaEReport(reportMinimumDeltaE) +
                     "\n");
        }
      }
      buf.append("Reverse Model Test:\n");
      DeltaEReport[] testReverseReports = testReverseModel(testTarget, false);
      buf.append(getTrainingTarget());
      buf.append("Test Target: " + testTarget.getDescription() + "\n");
      buf.append(Arrays.toString(testReverseReports) + "\n");
      if (patchDeltaEReport) {
        if (duvMode) {
          buf.append(testReverseReports[0].getPatchDeltaEReport(
              reportMinimumDeltauvp) + "\n");
        }
        else if (reportMinimumDeltaE > 0) {
          buf.append(testReverseReports[0].getPatchDeltaEReport(
              reportMinimumDeltaE) + "\n");
        }
      }
      ModelReport report = new ModelReport(buf.toString(), testReports,
                                           testReverseReports);
      return report;
    }

  }

}
