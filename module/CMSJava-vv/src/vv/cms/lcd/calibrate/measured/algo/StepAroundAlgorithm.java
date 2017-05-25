package vv.cms.lcd.calibrate.measured.algo;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.cms.plot.*;
import shu.cms.util.*;
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
public class StepAroundAlgorithm
    extends AroundAlgorithm {

  protected double maxCode = 255;
  public StepAroundAlgorithm(double maxCode) {
    this.maxCode = maxCode;
  }

  public StepAroundAlgorithm() {
    this(255);
  }

  /**
   * 取得centerRGB周邊的RGB, 以step為一個step單位
   * 並且由delta來決定某些周邊RGB是否要略過
   * @param centerRGB RGB
   * @param delta double[]
   * @param step double
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double[] delta, double step) {
    return getAroundRGB(centerRGB, delta, step, false);
  }

  /**
   * 設定色度擴張模式是否生效
   * 如果不處於色度擴張模式, 則RB只會找四個點; 色度擴張模式會找8個點.
   */
  protected boolean chromaticExpandMode = false;

  /**
   * 在R/B平面上, 擴展共8個點, 包括:
   * R+1/B+1 R+1/B R+1/B-1 R/B+1 R/B-1 R-1/B+1 R-1/B R-1/B-1
   * @param centerRGB RGB
   * @param delta double[]
   * @param step double
   * @return List
   */
  protected List<RGB> getChromaticExpandAround(RGB centerRGB, double[] delta,
                                               double step) {
    RGBBase.Channel maxChannel = centerRGB.getMaxChannel();
    List<RGB> rgbList = new ArrayList<RGB> ();

    if (chromaticExpandMode) {
      for (double rStep = -step; rStep <= step; rStep += step) {
        for (double bStep = -step; bStep <= step; bStep += step) {
          if (rStep == 0 && bStep == 0) {
            //中央的不需用重複
            continue;
          }

          boolean check =
              checkAdjustable(centerRGB, rStep, maxChannel, RGBBase.Channel.R,
                              delta) &&
              checkAdjustable(centerRGB, bStep, maxChannel, RGBBase.Channel.B,
                              delta);

          if (check) {
            RGB rgb = (RGB) centerRGB.clone();
            rgb.changeMaxValue(RGB.MaxValue.Double255);
            rgb.R += rStep;
            rgb.B += bStep;
            rgbList.add(rgb);
          }

        }
      }
      return rgbList;
    }
    else {
      return null;
    }
  }

  private List<RGB> rgbAdjust(RGBBase.Channel[] channels,
                              RGBBase.Channel maxChannel,
                              RGB centerRGB, double adjustStep, double[] delta) {
    List<RGB> rgbList = new ArrayList<RGB> ();
    for (RGBBase.Channel ch : channels) {
      if (checkAdjustable(centerRGB, adjustStep, maxChannel, ch, delta)) {
        RGB rgb = (RGB) centerRGB.clone();
        rgb.changeMaxValue(RGB.MaxValue.Double255);
        rgb.addValue(ch, adjustStep);
        rgbList.add(rgb);
      }
    }
    return rgbList;
  }

  private List<RGB> whiteAdjust(RGBBase.Channel maxChannel, RGB centerRGB,
                                double adjustStep) {
    List<RGB> rgbList = new ArrayList<RGB> ();
    if (whiteCheckAdjustable(centerRGB, adjustStep, maxChannel)) {
      RGB rgb = (RGB) centerRGB.clone();
      rgb.changeMaxValue(RGB.MaxValue.Double255);
      rgb.addValues(adjustStep);
      rgbList.add(rgb);
    }
    return rgbList;
  }

  /**
   * 取得centerRGB周邊的RGB, 以step為一個step單位
   * 並且由delta來決定某些周邊RGB是否要略過
   * @param centerRGB RGB
   * @param delta double[]
   * @param step double
   * @param involveWhite boolean 周邊的RGB要不要包括白
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double[] delta, double step,
                            boolean involveWhite) {
    List<RGB> rgbList = new ArrayList<RGB> ();
    rgbList.add(centerRGB);
    RGBBase.Channel maxChannel = centerRGB.getMaxChannel();

    if (chromaticExpandMode) {
      //======================================================================
      // R & B
      //======================================================================
      rgbList.addAll(getChromaticExpandAround(centerRGB, delta, step));
      //======================================================================

      double adjustStep = step;
      for (int x = 0; x < 2; x++) {
        //======================================================================
        // G
        //======================================================================
        rgbList.addAll(rgbAdjust(new RGBBase.Channel[] {RGBBase.Channel.G},
                                 maxChannel,
                                 centerRGB, adjustStep, delta));
        //======================================================================

        //======================================================================
        // W
        //======================================================================
        if (involveWhite) {
          rgbList.addAll(whiteAdjust(maxChannel, centerRGB, adjustStep));
        }
        //======================================================================

        adjustStep = -adjustStep;
      }
    }
    else {
      double adjustStep = step;
      for (int x = 0; x < 2; x++) {
        //======================================================================
        // R/G/B
        //======================================================================
        rgbList.addAll(rgbAdjust(RGBBase.Channel.RGBChannel, maxChannel,
                                 centerRGB, adjustStep, delta));
        //=====================================================================

        //======================================================================
        // W
        //======================================================================
        if (involveWhite) {
          rgbList.addAll(whiteAdjust(maxChannel, centerRGB, adjustStep));
        }
        //======================================================================
        adjustStep = -adjustStep;
      }
    }

    return RGBArray.toRGBArray(rgbList);
  }

  public static void main(String[] args) {
    StepAroundAlgorithm algo = new StepAroundAlgorithm();
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, new int[] {125, 111, 122});
    algo.setChromaticExpandMode(true);
//    RGB[] cube = algo.getCubeAroundRGB(rgb, 1);
    RGB[] cube = algo.getAroundRGB(rgb, 90, true);
    System.out.println(cube.length);
    for (RGB c : cube) {
      System.out.println(c);
    }
    Plot3D p = Plot3D.getInstance();
    for (RGB r : cube) {
      System.out.println(r);
      p.addColorSpace(r.toString(), r.getColor(), r);
    }
    p.setVisible();
  }

  /**
   * 立方體的擴展, 包括中央就是27個點, 也就是擴展26個點出去.
   * @param centerRGB RGB
   * @param step double
   * @return RGB[]
   */
  public RGB[] getCubeAroundRGB(RGB centerRGB, double step) {
    List<RGB> rgbList = new ArrayList<RGB> ();
    rgbList.add(centerRGB);
    RGBBase.Channel maxChannel = centerRGB.getMaxChannel();

    for (double rStep = -step; rStep <= step; rStep += step) {
      for (double gStep = -step; gStep <= step; gStep += step) {
        for (double bStep = -step; bStep <= step; bStep += step) {
          if (rStep == 0 && gStep == 0 && bStep == 0) {
            //中央的不需用重複
            continue;
          }

          boolean check =
              checkAdjustable(centerRGB, rStep, maxChannel, RGBBase.Channel.R, null) &&
              checkAdjustable(centerRGB, gStep, maxChannel, RGBBase.Channel.G, null) &&
              checkAdjustable(centerRGB, bStep, maxChannel, RGBBase.Channel.B, null);

          if (check) {
            RGB rgb = (RGB) centerRGB.clone();
            rgb.changeMaxValue(RGB.MaxValue.Double255);
            rgb.R += rStep;
            rgb.G += gStep;
            rgb.B += bStep;
            rgbList.add(rgb);
          }
        }
      }
    }
    return RGBArray.toRGBArray(rgbList);
  }

  /**
   * 取得centerRGB周邊的RGB, 以step為一個step單位
   * @param centerRGB RGB
   * @param step double step單位
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double step) {
    return getAroundRGB(centerRGB, null, step, false);
  }

  /**
   * 取得centerRGB周邊的RGB, 以step為一個step單位
   * @param centerRGB RGB
   * @param step double
   * @param involeWhite boolean 周邊的RGB要不要包括白
   * @return RGB[]
   */
  public RGB[] getAroundRGB(RGB centerRGB, double step, boolean involeWhite) {
    return getAroundRGB(centerRGB, null, step, involeWhite);
  }

  /**
   * 從delta所給的數據, 來決定要不要略過R或B的增減
   */
  protected static boolean ChromaticSkipByDelta = true;

  protected boolean whiteCheckAdjustable(RGB rgb, double step,
                                         RGBBase.Channel maxChannel) {
    if (this.isGreenMode()) {
      return false;
    }
    //調整後的點要 1.>=0 2.<=maxcode
    double maxAfterValue = rgb.getValue(maxChannel) + step;
    double minAfterValue = rgb.getValue(rgb.getMinChannel()) + step;
    return maxAfterValue <= maxCode && maxAfterValue >= 0 &&
        minAfterValue <= maxCode && minAfterValue >= 0;
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
    else if (this.isGreenMode()) {
      if (adjustChannel == RGBBase.Channel.G && step != 0) {
        //只要動到G頻道就是不行!
        return false;
      }
    }

    if (ChromaticSkipByDelta == true && delta != null) {
      if (adjustChannel == RGBBase.Channel.R && delta[0] * step > 0) {
        /**
         * 同號就是不行
         * du>0的話, R一定要往負號. 所以相乘一定<0
         * 如果du*step >0的話, 代表
         * 1.du>0且R往正調整 2.du<0且R往負調整
         * 以上都是多餘的調整
         */
        return false;
      }
      if (adjustChannel == RGBBase.Channel.B && delta[1] * step < 0) {
        /**
         * 異號就是不行
         * dv>0的話, B一定要往正號. 所以相乘一定>0
         * 如果dv*step <0的話, 代表
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
   * 設定色度擴張模式是否生效
   * 如果不處於色度擴張模式, 則RB只會找四個點; 色度擴張模式會找8個點.
   * @param chromaticExpandMode boolean
   */
  public void setChromaticExpandMode(boolean chromaticExpandMode) {
    this.chromaticExpandMode = chromaticExpandMode;
  }
}
