package shu.cms.lcd.calibrate.measured.util;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.lcd.calibrate.*;
import shu.cms.lcd.calibrate.parameter.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 相對LCDTarget的計算, 主要是調整使亮度維持相對值
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class RelativeTarget {

  /**
   * 取得相對亮度的陣列
   * @param originalTarget LCDTarget 原始的目標target
   * @param measuredTarget LCDTarget 量測得到的target
   * @param channel Channel 要取得相對亮度的頻道
   * @return double[]
   */
  private final static double[] getRelativeYArray(LCDTarget originalTarget,
                                                  LCDTarget measuredTarget,
                                                  RGBBase.Channel channel) {
    CIEXYZ black = measuredTarget.getBlackPatch().getXYZ();
    CIEXYZ saturate = null;
    if (channel == RGBBase.Channel.W) {
      //如果是白色頻道, 最亮的點就用RGB為白的色塊
      saturate = measuredTarget.getWhitePatch().getXYZ();
    }
    else {
      //如果非白色頻道, 最亮的點就用該頻道最飽和的色塊(也就是最亮的色塊)
      saturate = measuredTarget.getSaturatedChannelPatch(channel).getXYZ();
    }
    CIEXYZ targetBlack = originalTarget.getBlackPatch().getXYZ();
    int size = originalTarget.size();
    //==========================================================================
    // 產生相對亮度
    //==========================================================================
    double[] relTargetYArray = new double[size];
    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = originalTarget.getPatch(x).getXYZ();
      relTargetYArray[x] = XYZ.Y - targetBlack.Y;
    }
    //正規化
    Maths.normalize(relTargetYArray, relTargetYArray[size - 1]);
    //還原
    relTargetYArray = DoubleArray.times(relTargetYArray, saturate.Y - black.Y);
    //加底
    relTargetYArray = DoubleArray.plus(relTargetYArray, black.Y);
    //==========================================================================
    return relTargetYArray;
  }

  /**
   * 取得相對亮度以及量測白點為白點的LCDTarget
   * 以measuredTarget量測到的亮度為基礎作調整, 重新產生相對亮度的LCDTarget.
   * 並且將measuredTarget量測到的白點作為白點, 重新產生轉折點之後對應的色度座標的LCDTarget.
   *
   * 如果具備LCDModel的話, 則還可以針對暗點, 使轉折點以下的目標色度作調整.
   * LCDModel是用作CCTCurveProducer重新產生目標色度所需.
   *
   * @param originalTarget LCDTarget
   * @param measuredTarget LCDTarget
   * @param turncode int
   * @param model LCDModel
   * @param p Parameter
   * @param wp WhiteParameter
   * @return LCDTarget
   */
  public final static LCDTarget getLuminanceAndChromaticityRelativeInstance(
      LCDTarget originalTarget, LCDTarget measuredTarget, int turncode,
      LCDModel model, ColorProofParameter p, WhiteParameter wp) {

    //計算出相對亮度的陣列
    double[] relTargetYArray = getRelativeYArray(originalTarget,
                                                 measuredTarget,
                                                 RGBBase.Channel.W);
    //取得白的XYZ
    CIEXYZ whiteXYZ = measuredTarget.getWhitePatch().getXYZ();

    //==========================================================================
    // 重新計算目標xyY Curve
    //==========================================================================
    CIExyY[] xyYcurve = null;
    /**
     * 低灰階是否要對色度作相對調整
     */
    boolean doDimCodeRelChromaticity = false;
    if (model != null && p != null && wp != null) {
      //如果有model且參數都有的話
      doDimCodeRelChromaticity = true;
      CIEXYZ blackXYZ = measuredTarget.getBlackPatch().getXYZ();
      CCTCurveProducer.CCTParameter cctp = CCTCurveProducer.getCCTParameter(
          blackXYZ, whiteXYZ, p, wp);
      xyYcurve = CCTCurveProducer.getxyYCurve(model, cctp, relTargetYArray);
    }
    //==========================================================================

    //==========================================================================
    // 從相對亮度產生相對目標
    //==========================================================================
    List<Patch>
        relTargetPatchList = Patch.Produce.copyOf(originalTarget.getPatchList());
    int size = originalTarget.size();
    //換算白的xyY
    CIExyY whitexyY = new CIExyY(whiteXYZ);

    for (int x = 0; x < size; x++) {
      Patch patch = relTargetPatchList.get(x);
      CIEXYZ XYZ = (CIEXYZ) patch.getXYZ().clone();
      if (relTargetYArray[x] != 0) {
        //調整亮度, 使其亮度為相對亮度
        XYZ.scaleY(relTargetYArray[x]);
      }
      if (x >= turncode) {
        //轉折點以上, 一併調整色度值, 使色度值與白點色度相等
        CIExyY xyY = new CIExyY(XYZ);
        xyY.x = whitexyY.x;
        xyY.y = whitexyY.y;
        CIEXYZ XYZ2 = xyY.toXYZ();
        XYZ.setValues(XYZ2.getValues());
      }
      else if (doDimCodeRelChromaticity) {
        //有model、p、wp才能進到這邊來
        //轉折點以下, 將重新產生的CCT曲線產生的色度曲線, 重新設定上
        CIExyY xyY = new CIExyY(XYZ);
        CIExyY cctxyY = xyYcurve[x];
        xyY.x = cctxyY.x;
        xyY.y = cctxyY.y;
        CIEXYZ XYZ2 = xyY.toXYZ();
        XYZ.setValues(XYZ2.getValues());
      }
      Patch.Operator.setXYZ(patch, XYZ);
    }
    LCDTarget relativeTarget = LCDTarget.Instance.get(relTargetPatchList,
        originalTarget.getNumber(), originalTarget.isInverseModeMeasure());
    //==========================================================================

    return relativeTarget;

  }

  /**
   * 取得相對亮度以及量測白點為白點的LCDTarget
   * 以measuredTarget量測到的亮度為基礎作調整, 重新產生相對亮度的LCDTarget.
   * 並且將measuredTarget量測到的白點作為白點, 重新產生轉折點之後對應的色度座標的LCDTarget.
   *
   * @param originalTarget LCDTarget
   * @param measuredTarget LCDTarget
   * @param turncode int
   * @return LCDTarget
   */
  public final static LCDTarget getLuminanceAndChromaticityRelativeInstance(
      LCDTarget originalTarget, LCDTarget measuredTarget, int turncode) {
    return getLuminanceAndChromaticityRelativeInstance(originalTarget,
        measuredTarget, turncode, null, null, null);
  }

  /**
   * 取得相對亮度的LCDTarget
   * 以measuredTarget量測到的亮度為基礎作調整, 重新產生相對亮度的LCDTarget.
   *
   * @param originalTarget LCDTarget
   * @param measuredTarget LCDTarget
   * @param channel Channel
   * @return LCDTarget
   */
  public final static LCDTarget getLuminanceRelativeInstance(LCDTarget
      originalTarget, LCDTarget measuredTarget, RGBBase.Channel channel) {
    //計算出相對亮度的矩陣
    double[] relTargetYArray = getRelativeYArray(originalTarget,
                                                 measuredTarget, channel);

    //==========================================================================
    // 從相對亮度產生相對目標
    //==========================================================================
    List<Patch>
        relTargetPatchList = Patch.Produce.copyOf(originalTarget.getPatchList());
    int size = originalTarget.size();
    for (int x = 0; x < size; x++) {
      Patch p = relTargetPatchList.get(x);
      CIEXYZ XYZ = (CIEXYZ) p.getXYZ().clone();
      Patch.Operator.setXYZ(p, XYZ);
      XYZ.scaleY(relTargetYArray[x]);
    }
    LCDTarget relativeTarget = LCDTarget.Instance.get(relTargetPatchList,
        originalTarget.getNumber(), originalTarget.isInverseModeMeasure());
    //==========================================================================

    return relativeTarget;
  }
}
