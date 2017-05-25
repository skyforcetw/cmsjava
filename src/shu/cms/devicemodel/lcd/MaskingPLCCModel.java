package shu.cms.devicemodel.lcd;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.util.*;
import shu.cms.lcd.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * ノMasking瞶,┏︹竚传よΑ璸衡
 *
 * @note ヘ玡ゎ(08/03/22)┮暗Ω非絋LCD Model.
 * σ納干︹,┮箇代穦非絋.
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class MaskingPLCCModel
    extends ChannelDependentModel {

  /**
   * ―家Α
   * @param lcdTarget LCDTarget
   */
  public MaskingPLCCModel(LCDTarget lcdTarget) {
    super(lcdTarget);
    if (lcdTarget.getNumber() != LCDTargetBase.Number.Ramp1792 &&
        lcdTarget.getNumber() != LCDTargetBase.Number.Ramp7147 &&
        lcdTarget.getNumber() != LCDTargetBase.Number.Ramp7168) {
      throw new IllegalArgumentException(
          "lcdTarget.getNumber() != LCDTargetBase.Number.Patch1792 && lcdTarget.getNumber() != LCDTargetBase.Number.Patch7147 && lcdTarget.getNumber() != LCDTargetBase.Number.Patch7168");
    }

  }

  protected LCDTargetInterpolator rgbInterpolator;
  protected LCDTargetInterpolator ymcInterpolator;
  protected LCDTargetInterpolator grayInterpolator;

//  /**
//   * ―家Α
//   * @param lcdTarget LCDTarget
//   * @param rCorrectLCDTarget LCDTarget
//   * @param cooperateWithLCDTargetInterpolator boolean
//   */
//  protected MaskingPLCCModel(LCDTarget lcdTarget, LCDTarget rCorrectLCDTarget,
//                             boolean cooperateWithLCDTargetInterpolator) {
//    super(lcdTarget, rCorrectLCDTarget, cooperateWithLCDTargetInterpolator);
//  }

//  public MaskingPLCCModel(LCDTarget lcdTarget, LCDTarget rCorrectLCDTarget) {
//    super(lcdTarget, rCorrectLCDTarget);
//  }

  /**
   * 璸衡RGB,は崩家Α
   *
   * @param XYZ CIEXYZ
   * @param factor Factor[]
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ, Factor[] factor) {
    return null;
  }

  /**
   * 璸衡XYZ,玡旧家Α
   *
   * @param rgb RGB
   * @param factor Factor[]
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb, Factor[] factor) {

    MaskColor color = new MaskColor(rgb);

    CIEXYZ primaryXYZ = getPartialXYZ(rgbInterpolator, color.primaryChannel,
                                      color.getPrimaryColorHighValue(),
                                      color.getPrimaryColorLowValue());

    CIEXYZ secondaryXYZ = getPartialXYZ(ymcInterpolator, color.secondaryChannel,
                                        color.getSecondaryColorHighValue(),
                                        color.getSecondaryColorLowValue());
    CIEXYZ grayXYZ = grayInterpolator.getPatch(RGBBase.Channel.W,
                                               color.grayValue).
        getXYZ();
    CIEXYZ recover = LCDModelUtil.recover(primaryXYZ, secondaryXYZ, grayXYZ);
    CIEXYZ flare = this.flare.getFlare();
    recover = CIEXYZ.minus(recover, flare);
    return recover;
  }

  protected final static CIEXYZ getPartialXYZ(LCDTargetInterpolator
                                              interpolator, RGBBase.Channel ch,
                                              double highValue,
                                              double lowValue) {
    CIEXYZ XYZHigh = interpolator.getPatch(ch, highValue).
        getXYZ();
    CIEXYZ XYZLow = interpolator.getPatch(ch, lowValue).
        getXYZ();
    CIEXYZ partialXYZ = CIEXYZ.minus(XYZHigh, XYZLow);
    return partialXYZ;
  }

  protected static class MaskColor {
    public MaskColor(RGB rgb) {
      RGBBase.Channel maxch = rgb.getMaxChannel();
      RGBBase.Channel medch = rgb.getMedChannel();
      RGBBase.Channel minch = rgb.getMinChannel();
      grayValue = rgb.getValue(RGBBase.Channel.W);
      primaryColorValue = rgb.getValue(maxch) - rgb.getValue(medch);
      secondaryColorValue = rgb.getValue(medch) - rgb.getValue(minch);
      primaryChannel = maxch;
      secondaryChannel = RGBBase.Channel.getChannelByArrayIndex(maxch.index +
          medch.index);
    }

    public MaskColor(double grayValue, RGBBase.Channel primaryChannel,
                     double primaryColorValue, RGBBase.Channel secondaryChannel,
                     double secondaryColorValue) {
      this.grayValue = grayValue;
      this.primaryChannel = primaryChannel;
      this.primaryColorValue = primaryColorValue;
      this.secondaryChannel = secondaryChannel;
      this.secondaryColorValue = secondaryColorValue;
    }

    public double getPrimaryColorHighValue() {
      return primaryColorValue + secondaryColorValue + grayValue;
    }

    public double getPrimaryColorLowValue() {
      return secondaryColorValue + grayValue;
    }

    public double getSecondaryColorHighValue() {
      return secondaryColorValue + grayValue;
    }

    public double getSecondaryColorLowValue() {
      return grayValue;
    }

    double grayValue;
    double primaryColorValue;
    RGBBase.Channel primaryChannel;
    double secondaryColorValue;
    RGBBase.Channel secondaryChannel;
    public String toString() {
      return "(G:" + grayValue + " " + primaryChannel + ":" + primaryColorValue +
          " " +
          secondaryChannel + ":" + secondaryColorValue + ")";
    }
  }

  /**
   * ―玒计
   *
   * @return Factor[]
   */
  protected Factor[] _produceFactor() {
    Interpolation.Algo[] rgbInterpolation = LCDTargetInterpolator.Find.
        optimumInterpolationType(lcdTarget,
                                 LCDTargetInterpolator.OptimumType.Max);
    Interpolation.Algo[] ymcInterpolation = LCDTargetInterpolator.Find.
        optimumInterpolationType(lcdTarget,
                                 LCDTargetInterpolator.OptimumType.Max,
                                 RGBBase.Channel.YMCChannel);
    RGBBase.Channel[] whiteChannels = new RGBBase.Channel[] {
        RGBBase.Channel.W, RGBBase.Channel.W, RGBBase.Channel.W};

    rgbInterpolator = LCDTargetInterpolator.Instance.get(lcdTarget,
        rgbInterpolation);
    ymcInterpolator = LCDTargetInterpolator.Instance.get(lcdTarget,
        ymcInterpolation,
        RGBBase.Channel.YMCChannel);
    Interpolation.Algo[] grayInterpolation = LCDTargetInterpolator.Find.
        optimumInterpolationType(lcdTarget,
                                 LCDTargetInterpolator.OptimumType.Max,
                                 whiteChannels);
    grayInterpolator = LCDTargetInterpolator.Instance.get(lcdTarget,
        grayInterpolation,
        whiteChannels);
    return new Factor[3];
  }

  /**
   * getDescription
   *
   * @return String
   */
  public String getDescription() {
    return "MaskingPLCC";
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
    MaskingPLCCModel maskModel = new MaskingPLCCModel(lcdTarget);
//    MultiMatrixModel maskModel = new MultiMatrixModel(lcdTarget,lcdTarget);
    maskModel.produceFactor();

//    LCDTarget lcdTestTarget = lcdTarget.filter.getPatch1792From7147();
//    LCDTarget lcdTestTarget = lcdTarget;
    LCDTarget lcdTestTarget = LCDTarget.Instance.get("cpt_17inch No.3",
        LCDTarget.Source.CA210,
        LCDTarget.Room.Dark,
        LCDTarget.TargetIlluminant.
        Native,
        LCDTargetBase.Number.Test4096,
        LCDTarget.FileType.Logo,
        null, null);

    DeltaEReport[] testReports = maskModel.testForwardModel(lcdTestTarget, false);

    System.out.println("Training1: " + lcdTarget.getDescription());
    System.out.println(Arrays.toString(testReports));
    System.out.println(testReports[0].getPatchDeltaEReport(.5));
  }
}
