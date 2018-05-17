package shu.cms.devicemodel.lcd.xtalk;

import flanagan.math.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 重建Xtalk的效應
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class XTalkReconstructor
    extends AbstractXTalkReconstructor {
  protected static enum Method {
    ByMatrix, ByMinimisation
  }

  XTalkReconstructor(MultiMatrixModel mmModel,
                     XTalkProperty xtalkProperty) {
    super(mmModel, xtalkProperty);
  }

  /**
   * 從xtalk後的XYZ以及原始的originalRGB(也就是未受xtalk影響的RGB), 推算出xtalk所造成的RGB改變
   * @param XYZ CIEXYZ
   * @param originalRGB RGB
   * @param relativeXYZ boolean
   * @return RGB
   */
  public RGB getXTalkRGB(CIEXYZ XYZ, final RGB originalRGB, boolean relativeXYZ) {

    RGB byMatrixRGB = getXTalkRGB(XYZ, originalRGB, relativeXYZ,
                                  Method.ByMatrix);
    DeltaE byMatrixRGBDeltaE = getXTalkRGBDeltaE();
    double byMatrixRGBdE = byMatrixRGBDeltaE.getCIE2000DeltaE();

    RGB byMinimisationRGB = getXTalkRGB(XYZ, originalRGB, relativeXYZ,
                                        Method.ByMinimisation);
    DeltaE byMinimisationDeltaE = getXTalkRGBDeltaE();
    double byMinimisationdE = byMinimisationDeltaE.getCIE2000DeltaE();

    //將估算出來色差較小的結果回傳
    if (byMatrixRGBdE < byMinimisationdE) {
      this._getXTalkRGBDeltaE = byMatrixRGBDeltaE;
      byMatrixCount++;
      return byMatrixRGB;
    }
    else {
      this._getXTalkRGBDeltaE = byMinimisationDeltaE;
      byMinimisationxCount++;
      return byMinimisationRGB;
    }
  }

  private int byMatrixCount;
  private int byMinimisationxCount;

  protected RGB getXTalkRGB(CIEXYZ XYZ, final RGB originalRGB,
                            boolean relativeXYZ, Method method) {
    if (!originalRGB.isSecondaryChannel()) {
      //originalRGB一定只能是二次色
      return null;
    }

    //根據relativeXYZ處理XYZ
    CIEXYZ fromXYZ = adapter.fromXYZ(XYZ, relativeXYZ);
    /**
     * @todo H acp 是否要留存rationalize
     */
    fromXYZ.rationalize();

    switch (method) {
      case ByMatrix:
        return getXTalkRGBByMatrix(fromXYZ, originalRGB);
      case ByMinimisation:
        return getXTalkRGBByMinimisation(fromXYZ, originalRGB);
      default:
        return null;
    }
  }

  /**
   * 已知XYZ,還原RGB,且確定了XTalk的頻道,利用優化的方式求得最佳解
   *
   * 演算法說明:
   * (1)計算Xtalk channel
   * (2)利用優化的方式求得最佳解
   * (3)計算整個演算法誤差所造成的色差
   * @param XYZ CIEXYZ
   * @param originalRGB RGB
   * @return RGB
   */
  protected RGB getXTalkRGBByMinimisation(CIEXYZ XYZ, final RGB originalRGB) {
    //(1)
    RGBBase.Channel selfChannel = xtalkProperty.getSelfChannel(originalRGB.
        getSecondaryChannel());
    //(2)
    RGB rgb = recover.getXTalkRGB(XYZ, originalRGB, selfChannel);

    //==========================================================================
    // 計算inverseLab的deltaE
    //==========================================================================
    //(3)
    _getXTalkRGBDeltaE = mmModel.calculateGetRGBDeltaE(rgb, XYZ, true);
    //==========================================================================

    return rgb;
  }

  protected XTalkRGBRecover recover = new XTalkRGBRecover();

  protected class XTalkRGBRecover
      implements MinimisationFunction {
    /**
     *
     * @param XYZ CIEXYZ xtalk後的XYZ
     * @param originalRGB RGB 原始的RGB
     * @param xtalkChannel Channel 被xtalk影響的channel
     * @param signConstraint boolean 是否要限制正負號
     * @param negativeXTalk boolean 正負號的限制方向
     * @return RGB xtalk預測的RGB
     */
    private RGB getXTalkRGB(CIEXYZ XYZ, final RGB originalRGB,
                            RGBBase.Channel xtalkChannel,
                            boolean signConstraint,
                            boolean negativeXTalk) {
      this.measureXYZ = XYZ;
      this.recoverRGB = (RGB) originalRGB.clone();
      this.xtalkChannel = xtalkChannel;

      //Create instance of Minimisation
      Minimisation min = new Minimisation();

      // initial estimates
      double[] start = new double[] {
          recoverRGB.getValue(xtalkChannel)};

      double[] step = new double[] {
          mmModel.getLCDTarget().getStep()};

      if (signConstraint) {
        if (negativeXTalk) {
          min.addConstraint(0, 1, start[0]);
          min.addConstraint(0, -1, 0);
        }
        else {
          min.addConstraint(0, 1, originalRGB.getMaxValue().max);
          min.addConstraint(0, -1, start[0]);
        }
      }
      else {
        min.addConstraint(0, 1, originalRGB.getMaxValue().max);
        min.addConstraint(0, -1, 0);
      }
      // Nelder and Mead minimisation procedure
      min.nelderMead(this, start, step);

      // get values of y and z at minimum
      double[] param = min.getParamValues();
      recoverRGB.setValue(xtalkChannel, param[0]);
      return recoverRGB;

    }

    /**
     *
     * @param XYZ CIEXYZ Crosstalk影響後的XYZ
     * @param originalRGB RGB 原始輸入的RGB訊號
     * @param xtalkChannel Channel Crosstalk影響的頻道
     * @param negativeXTalk boolean Crosstalk是否有負的影響
     * @return RGB
     */
    public RGB getXTalkRGB(CIEXYZ XYZ, final RGB originalRGB,
                           RGBBase.Channel xtalkChannel, boolean negativeXTalk) {
      return getXTalkRGB(XYZ, originalRGB, xtalkChannel, true, negativeXTalk);
    }

    public RGB getXTalkRGB(CIEXYZ XYZ, final RGB originalRGB,
                           RGBBase.Channel xtalkChannel) {
      return getXTalkRGB(XYZ, originalRGB, xtalkChannel, false, false);
    }

    protected RGB recoverRGB;
    protected RGBBase.Channel xtalkChannel;
    protected CIEXYZ measureXYZ;

    /**
     * function
     *
     * @param doubleArray double[]
     * @return double
     */
    public double function(double[] doubleArray) {
      double val = doubleArray[0];
      //調整xtalkChannel的數值
      recoverRGB.setValue(xtalkChannel, val);
      //使deltaE達到最小
      DeltaE de = mmModel.calculateGetRGBDeltaE(recoverRGB, measureXYZ, true);
      return de.getCIE2000DeltaE();
    }

  }

  /**
   * 已知XYZ,還原RGB,且確定了XTalk的頻道
   *
   * 演算法說明:
   * (1)計算Xtalk channel
   * (2)藉由已知的originalRGB, 排除掉XTalk的Channel以外的XYZ, 得到besideXYZ
   * (3)將beside XYZ帶入max矩陣進行運算, 得到粗估Luminance RGB
   * (4)改變矩陣的XYZ, 不斷迭代進行運算, 得到最佳解.
   * (5)將最佳解Luminace RGB以ungammaCorrect得DAC RGB
   * (6)計算整個演算法誤差所造成的色差
   * @param XYZ CIEXYZ
   * @param originalRGB RGB
   * @return RGB
   */
  protected RGB getXTalkRGBByMatrix(CIEXYZ XYZ, final RGB originalRGB) {
    //(1)
    RGBBase.Channel selfChannel = xtalkProperty.getSelfChannel(originalRGB.
        getSecondaryChannel());

    if (XYZ.X < 0 || XYZ.Y < 0 || XYZ.Z < 0) {
      _getXTalkRGBDeltaE = null;
      return originalRGB;
    }
    //只留xtalk的XYZ
    //(2)
    CIEXYZ besideXYZ = getBesideXYZ(XYZ, originalRGB, selfChannel);

    //==========================================================================
    // start
    //==========================================================================
    //(3)
    RGB rgb = XYZToChannelByMax(besideXYZ,
                                selfChannel);

    adapter.resetTouchMaxIterativeTimes();
    for (int x = 0; x < mmModel.MAX_ITERATIVE_TIMES; x++) {
      //(4)
      RGB newRGB = XYZToChannelByMultiMatrix(rgb, besideXYZ,
                                             selfChannel);
      if (newRGB == null || newRGB.equals(rgb)) {
        break;
      }
      rgb = newRGB;
      adapter.setTouchMaxIterativeTimes(x);
    }

    //(5)
    mmModel.correct.gammaUncorrect(rgb);

    //將正規化的DAC RGB轉回原始大小
    rgb.changeMaxValue(mmModel.getLCDTarget().getMaxValue());
    rgb = mmModel.rational.RGBRationalize(rgb);
    RGBBase.Channel[] constChannel = RGBBase.Channel.getBesidePrimaryChannel(
        selfChannel);

    rgb.setValue(constChannel[0], originalRGB.getValue(constChannel[0]));
    rgb.setValue(constChannel[1], originalRGB.getValue(constChannel[1]));

    //==========================================================================
    // 計算inverseLab的deltaE
    //==========================================================================
    //(6)
    _getXTalkRGBDeltaE = mmModel.calculateGetRGBDeltaE(rgb, XYZ, true);
    //==========================================================================
    //==========================================================================

    return rgb;
  }

  /**
   * 計算xtalkChannel的XYZ值
   * @param relativeXYZ CIEXYZ
   * @param originalRGB RGB
   * @param xtalkChannel Channel
   * @return CIEXYZ
   */
  protected CIEXYZ getBesideXYZ(CIEXYZ relativeXYZ, final RGB originalRGB,
                                RGBBase.Channel xtalkChannel) {

    //不受xtalk影響的channel
    RGBBase.Channel[] constChannel = RGBBase.Channel.getBesidePrimaryChannel(
        xtalkChannel);

    RGB rgb = (RGB) originalRGB.clone();
    rgb.setColorBlack();
    rgb.setValue(constChannel[0], originalRGB.getValue(constChannel[0]));
    CIEXYZ ch0XYZ = mmModel.getXYZ(rgb, true);
    rgb.setColorBlack();
    rgb.setValue(constChannel[1], originalRGB.getValue(constChannel[1]));
    CIEXYZ ch1XYZ = mmModel.getXYZ(rgb, true);

    CIEXYZ besideXYZ = CIEXYZ.minus(CIEXYZ.minus(relativeXYZ, ch0XYZ), ch1XYZ);
    return besideXYZ;
  }

  private double[] rMaxInverse = null;
  private double[] gMaxInverse = null;
  private double[] bMaxInverse = null;

  /**
   * 計算channel下的max XYZ所形成的反矩陣
   * @param channel Channel
   * @return double[]
   */
  protected double[] getMaxInverse(RGBBase.Channel channel) {
    switch (channel) {
      case R:
        if (rMaxInverse == null) {
          CIEXYZ maxXYZ = mmModel.getLCDTarget().getSaturatedChannelPatch(
              channel).
              getXYZ();
          rMaxInverse = DoubleArray.transpose(DoubleArray.pseudoInverse(new double[][] {
              maxXYZ.getValues()}))[0];
        }
        return rMaxInverse;
      case G:
        if (gMaxInverse == null) {
          CIEXYZ maxXYZ = mmModel.getLCDTarget().getSaturatedChannelPatch(
              channel).
              getXYZ();
          gMaxInverse = DoubleArray.transpose(DoubleArray.pseudoInverse(new double[][] {
              maxXYZ.getValues()}))[0];
        }
        return gMaxInverse;
      case B:
        if (bMaxInverse == null) {
          CIEXYZ maxXYZ = mmModel.getLCDTarget().getSaturatedChannelPatch(
              channel).
              getXYZ();
          bMaxInverse = DoubleArray.transpose(DoubleArray.pseudoInverse(new double[][] {
              maxXYZ.getValues()}))[0];
        }
        return bMaxInverse;
      default:
        return null;
    }

  }

  /**
   * 從channel的max XYZ,去反算出RGB值
   * @param XYZ CIEXYZ
   * @param channel Channel
   * @return RGB
   */
  protected final RGB XYZToChannelByMax(final CIEXYZ XYZ,
                                        RGBBase.Channel channel) {
    double[] maxInverse = getMaxInverse(channel);
    double relative = DoubleArray.times(XYZ.getValues(), maxInverse);

    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB);
    rgb.setValue(channel, relative, RGB.MaxValue.Double1);
    rgb.changeMaxValue(mmModel.getLCDTarget().getMaxValue());
    rgb = mmModel.rational.RGBRationalize(rgb);
    return rgb;
  }

  /**
   * 取得channel的code為channelValue下的XYZ值反矩陣
   * @param channel Channel
   * @param channelValue double
   * @return double[]
   */
  protected final double[] getChannelInverse(RGBBase.Channel channel,
                                             double channelValue) {
    double r = channel == RGBBase.Channel.R ? channelValue : 0;
    double g = channel == RGBBase.Channel.G ? channelValue : 0;
    double b = channel == RGBBase.Channel.B ? channelValue : 0;
    double[] XYZValues = adapter.getXYZ(r, g, b).getValues();

    if (XYZValues[0] == 0 && XYZValues[1] == 0 && XYZValues[2] == 0) {
      return null;
    }
    else {
      return DoubleArray.transpose(DoubleArray.pseudoInverse(new double[][] {
          XYZValues}))[0];
    }
  }

  /**
   * 以多重Matrix的方式,得到最逼近channelXYZ的RGB code
   * @param luminanceRoughRGB RGB
   * @param channelXYZ CIEXYZ
   * @param channel Channel
   * @return RGB
   */
  protected RGB XYZToChannelByMultiMatrix(final RGB luminanceRoughRGB,
                                          final CIEXYZ channelXYZ,
                                          final RGBBase.Channel channel
      ) {
    //==========================================================================
    // 修正為DAC Value
    //==========================================================================
    RGB rgb = (RGB) luminanceRoughRGB.clone();
    rgb.getValues(luminanceRGBValues);
    mmModel.correct.gammaUncorrect(rgb);
    //==========================================================================

    double[] inverseMatrix = getChannelInverse(channel,
                                               rgb.getValue(channel));
    if (inverseMatrix != null) {
      //      //此時回傳的RGB為luminance RGB的比值
      rgb = XYZToChannelByMatrix(channelXYZ, inverseMatrix, channel);
//      rgb.getValues(rgbValues, RGB.MaxValue.Double1);
      rgb.setValue(channel,
                   rgb.getValue(channel, RGB.MaxValue.Double1) *
                   luminanceRGBValues[channel.getArrayIndex()]);
      return rgb;
    }
    else {
      return null;
    }
  }

  private double[] luminanceRGBValues = new double[3];

  /**
   * 將XYZValues乘上inverseMatrix,得到該Channel luminance RGB
   * @param XYZValues double[]
   * @param inverseMatrix double[]
   * @return double
   */
  protected final double XYZToChannel(double[] XYZValues,
                                      double[] inverseMatrix) {
    return DoubleArray.times(XYZValues, inverseMatrix);
  }

  /**
   * 將XYZ與inveseMatrix計算得到luminance RGB
   * @param XYZ CIEXYZ
   * @param inverseMatrix double[]
   * @param channel Channel
   * @return RGB
   */
  protected final RGB XYZToChannelByMatrix(CIEXYZ XYZ,
                                           double[] inverseMatrix,
                                           final RGBBase.Channel channel) {
    double channelValues = XYZToChannel(XYZ.getValues(), inverseMatrix);

    /**
     * @note 很意外的,但也不意外的,你必須把fixRGB和RGBRationalize關閉,
     * 才能讓預測出來的RB趨於正確。
     * 為什麼?很簡單,RBValues會有>1的狀況,這是正常的狀況!
     * 因為在這裡不再是用"max"所組成的matrix,所以會有>1的狀況也不意外!
     * 相對的,在其他地方的fixRGB和RGBRationalize會不會也遇到這樣的狀況??
     * 需要做進一步驗證!
     */
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB);
    rgb.setValue(channel, channelValues, RGB.MaxValue.Double1);
    rgb.changeMaxValue(mmModel.getLCDTarget().getMaxValue());
    return rgb;
  }

  public static void main(String[] args) {

    LCDTarget.setRGBNormalize(false);
//    LCDTarget.setXYZNormalize(false);
    LCDTarget lcdTarget = LCDTarget.Instance.get("cpt_17inch No.3",
                                                 LCDTarget.Source.CA210,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.
                                                 Native,
                                                 LCDTargetBase.Number.Ramp1792,
                                                 LCDTarget.FileType.VastView,
                                                 null, null);
    MultiMatrixModel mmModel = new MultiMatrixModel(lcdTarget);
    mmModel.produceFactor();
    RGB keyRGB = mmModel.getLCDTarget().getKeyRGB();
    keyRGB.setValues(new double[] {130, 130, 0}, RGB.MaxValue.Double255);
//    CIEXYZ XYZ1 = mmModel.getXYZ(keyRGB, false);
    CIEXYZ XYZ1 = mmModel.getLCDTarget().getPatch(keyRGB).getXYZ();
    RGB rgb1 = mmModel.getRGB(XYZ1, false);

    System.out.println("org:" + keyRGB);
    System.out.println("mm:" + rgb1);
    System.out.println(mmModel.getRGBDeltaE().getCIE2000DeltaE());

    XTalkReconstructor reconstruct = new XTalkReconstructor(mmModel,
        XTalkProperty.getLeftXTalkProperty());
    RGB rgb2 = reconstruct.getXTalkRGB(XYZ1, keyRGB, false,
                                       XTalkReconstructor.Method.ByMatrix);
    System.out.println(reconstruct.getXTalkRGBDeltaE().getCIE2000DeltaE());
    RGB rgb3 = reconstruct.getXTalkRGB(XYZ1, keyRGB, false,
                                       XTalkReconstructor.Method.ByMinimisation);
    System.out.println(reconstruct.getXTalkRGBDeltaE().getCIE2000DeltaE());
    System.out.println("by mm:" + rgb2);
    System.out.println("by min:" + rgb3);
  }

  public int getByMatrixCount() {
    return byMatrixCount;
  }

  public int getByMinimisationxCount() {
    return byMinimisationxCount;
  }
}
