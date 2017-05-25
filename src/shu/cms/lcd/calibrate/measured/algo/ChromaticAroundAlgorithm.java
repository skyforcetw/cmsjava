package shu.cms.lcd.calibrate.measured.algo;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.plot.*;
import shu.cms.util.*;
///import shu.plot.*;

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 以色度為基礎產生周圍的RGB點
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ChromaticAroundAlgorithm
    extends StepAroundAlgorithm {

  public static void main(String[] args) {
    ChromaticAroundAlgorithm algo = new ChromaticAroundAlgorithm();

    RGB center = new RGB(RGB.ColorSpace.unknowRGB, new int[] {123, 63, 111});
//    algo.expandMode = true;
    Plot3D p = Plot3D.getInstance();
    algo.setChromaticExpandMode(true);
    RGB[] around = algo.getAroundRGB(center, 1);
    for (RGB rgb : around) {
      System.out.println(rgb);
      p.addColorSpace(rgb.toString(), rgb.getColor(), rgb);
    }
    p.setVisible();
  }

  /**
   * 檢查是否可以被調整
   * @param rgb RGB 被調整的rgb
   * @param step double 調整的幅度
   * @param maxChannel Channel 最大值頻道 (whitePointMode判斷所需)
   * @param adjustChannel Channel 要被調整的頻道
   * @param delta double[]
   * @return boolean
   */
  protected boolean checkAdjustable(RGB rgb, double step,
                                    RGBBase.Channel maxChannel,
                                    RGBBase.Channel adjustChannel,
                                    double[] delta) {
    if (this.isWhitePointMode()) {
      //如果是在白點模式
      if (adjustChannel == maxChannel) {
        //只要動到最大頻道就是不行!
        return false;
      }
    }
    else {
      //如果不是在白點模式
      //只要動到G就是不行!
      if (adjustChannel == RGBBase.Channel.G) {
        return false;
      }
    }

    if (ChromaticSkipByDelta == true && delta != null) {
      if (adjustChannel == RGBBase.Channel.R && delta[0] * step > 0) {
        /**
         * 同號就是不行
         * du>0的話, R一定要往負號. 所以相乘一定<0
         * 如果du*step >0的話, 代表:
         * 1.du>0且R往正調整 2.du<0且R往負調整
         * 以上都是多餘的調整
         */
        return false;
      }
      if (adjustChannel == RGBBase.Channel.B && delta[1] * step < 0) {
        /**
         * 異號就是不行
         * dv>0的話, B一定要往正號. 所以相乘一定>0
         * 如果dv*step <0的話, 代表:
         * 1.dv>0且B往負調整 2.dv<0且B往正調整
         * 以上都是多餘的調整
         */
        return false;
      }
    }

    //調整後的點要 1.>=0 2.<=maxcode
    double afterValue = rgb.getValue(adjustChannel) + step;
    return afterValue <= maxCode && afterValue >= 0;
  }

  /**
   *
   * @param centerRGB RGB
   * @param delta double[]
   * @param step double
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double[] delta, double step) {
    List<RGB> rgbList = new ArrayList<RGB> ();
    rgbList.add(centerRGB);
    RGBBase.Channel maxChannel = centerRGB.getMaxChannel();

    if (this.chromaticExpandMode) {
      // RB同時變動
      rgbList.addAll(this.getChromaticExpandAround(centerRGB, delta, step));
    }
    else {
      // R/G/B各自變動
      //========================================================================
      // 這2層迴圈的變動有
      // 1. 正負號: +step 跟 -step的差別
      // 2. RGB頻道: RGB頻道各自做一次step的調整
      //========================================================================
      double adjustStep = step;
      for (int x = 0; x < 2; x++) {
        for (RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
          //變換不同的channel
          if (checkAdjustable(centerRGB, adjustStep, maxChannel, ch, delta)) {
            RGB rgb = (RGB) centerRGB.clone();
            rgb.changeMaxValue(RGB.MaxValue.Double255);
            rgb.addValue(ch, adjustStep);
            rgbList.add(rgb);
          }
        }
        //換正負
        adjustStep = -adjustStep;
      }
    }
    //========================================================================

    return RGBArray.toRGBArray(rgbList);
  }

  public ChromaticAroundAlgorithm() {
    super();
  }

  public ChromaticAroundAlgorithm(double maxCode) {
    super(maxCode);
  }

  /**
   *
   * @param centerRGB RGB
   * @param step double
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double step) {
    return getAroundRGB(centerRGB, null, step);
  }

}
