package shu.cms.lcd.calibrate.parameter;

import java.util.*;

import shu.cms.colorspace.depend.*;

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
public class ColorProofParameter
    implements Parameter {
  /**
   * gamma調整的趨勢
   */
  public Gamma gamma = Gamma.Custom;
  /**
   * 自訂gamma的gamma數值
   */
  public double customGamma = 2.2;
  /**
   * 自訂Gamma曲線
   */
  public double[] customCurve;
  /**
   * 校正的最小單位
   */
  public RGBBase.MaxValue calibrateBits = RGBBase.MaxValue.Int10Bit;
  /**
   * ic的bits數
   */
  public RGBBase.MaxValue icBits = RGBBase.MaxValue.Int10Bit;
  /**
   * 輸出的cpcode精確度
   */
  public RGBBase.MaxValue outputBits = RGBBase.MaxValue.Double255;

  public static enum Format {
    VastView, AUO
  }

  /**
   * 輸出的cpcode檔案格式
   */
  public Format outputFileFormat = Format.AUO;

  /**
   * 轉折點, 轉折點之後才開始轉折(就是轉折點-1開始轉折), 而不是轉折點本身就開始轉折.
   */
  public int turnCode = 50;
  /**
   * 評估色溫轉折的pattern的code間隔
   */
  public int grayInterval = 8;

  /**
   * 產生CCT曲線的方式
   */
  public CCTCalibrate cctCalibrate = CCTCalibrate.IPT;
  /**
   * CCT曲線適應起點
   */
  public int cctAdaptiveStart = 3;

  public double getTolerance() {
    return calibrateBits.getStepIn255() / 4.;
  }

  public GammaBy gammaBy = GammaBy.W;

  public static enum GammaBy {
    G, W;
  }

  public static enum Gamma {
    /**
     * 依照原始的gamma不動
     */
    Native,
    /**
     * 依照原始的gamma不動僅做作線性比例的調整
     */
    Scale,
    /**
     * 從原始的Gamma估算出最接近的gamma指數
     */
    Smooth,
    /**
     * 自訂gamma指數
     */
    Custom,
    /**
     * 依照sRGB gamma校正
     */
    sRGB,
    /**
     * DICOM GSDF
     */
    GSDF,
    /**
     * 自訂GammaCurve
     */
    CustomCurve,
    /**
     * 指定G code, calibrate ByG才可使用
     */
    GCode
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 產生CCT的方式
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static enum CCTCalibrate {
    /**
     * 從色溫曲線為基礎再作調整而產生
     */
    Corrected,
    /**
     * 在u'v'空間以CIEDE00均勻變化而產生
     */
    uvpByDE00,
    /**
     * 在u'v'空間以IPT均勻變化而產生
     */
    uvpByIPT,
    /**
     * 在IPT空間均勻變化而產生
     */
    IPT,
    /**
     * 在CIECAM02空間均勻變化而產生
     */
    CIECAM02,
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    StringBuilder buf = new StringBuilder();
    switch (gamma) {
      case Custom:
        buf.append("Gamma: Custom(" + customGamma + ")");
        break;
      case CustomCurve:
        buf.append("Gamma: CustomCurve" + Arrays.toString(customCurve));
        break;
      default:
        buf.append("Gamma: " + gamma);
    }
    buf.append(" (GammaBy: " + gammaBy + ")\n");
    buf.append("\nCalibrateBits: " + calibrateBits);
    buf.append("\nicBits: " + icBits);
    buf.append("\nturnCode: " + turnCode);
    buf.append("\ngrayInterval: " + grayInterval);
    buf.append("\nCCTCalibrate: " + cctCalibrate);
    buf.append("\ncctAdaptiveStart: " + cctAdaptiveStart);
    buf.append("\nTolerance: " + this.getTolerance());
    buf.append("\nkeepBlackPoint: " + keepBlackPoint);
    buf.append("\nrunCount: " + runCount);
    return buf.toString();
  }

  /**
   * 最暗點保持不變
   */
  public boolean keepBlackPoint = true;

  /**
   * 校正的次數
   */
  public int runCount = 1;

  public RGB[] gCodeArray;
  /**
   * 注射CPCode, 用來替代掉model產出的結果
   */
  public RGB[] injectedCPCodeArray;
}
