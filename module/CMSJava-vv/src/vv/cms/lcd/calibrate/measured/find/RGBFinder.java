package vv.cms.lcd.calibrate.measured.find;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.DeviceDependentSpace.MaxValue;
import shu.cms.hvs.gradient.*;
import vv.cms.lcd.calibrate.measured.algo.*;
import vv.cms.measure.cp.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * RGBFinder是以一個起始rgb, 利用色度以及亮度逐漸逼近的迭代演算法, 透過量測(或者模擬量測), 得到目
 * 標值XYZ的作法.
 *
 * 原本該作法是應用在MeasuredCalibrator, 也就是透過量測方式校正螢幕所被使用.
 * 但是該方法逼近目標值的結果良好, 因此透過RGBFinder將該演算法的各核心class包裝起來, 並且純化
 * 介面, 使得過程能單純化得到目標RGB.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class RGBFinder
    implements JNDIInterface {
  private FindingInfo info = new FindingInfo(1);
  private CIExyY targetxyY;
  private IndependentFindingThread findingThread;
  private boolean[] calibrated = new boolean[1];
  private boolean luminanceCalibrate = true;
  private boolean chromaticCalibrate = true;
  protected GSDFGradientModel gm = new GSDFGradientModel();
  private CalibratorInstance calibrator;

  public static void main(String[] args) {
//    String str = "autocp";
//    System.out.println(str.hashCode());
//    System.out.println( (int) 'a' + 'u' + 't' + 'o' + 'c' + 'p');
    System.out.println(Float.MAX_VALUE);
    System.out.println(Double.MAX_VALUE);
  }

  public RGBFinder(CIEXYZ white, RGB.MaxValue targetMaxValue,
                   CPCodeMeasurement cpm) {
    this(white, targetMaxValue, cpm, 255);
  }

  public RGBFinder(CIEXYZ white, RGB.MaxValue targetMaxValue,
                   CPCodeMeasurement cpm, double maxCode) {
    calibrator = new CalibratorInstance(white, (MeasureInterface) cpm, this, null,
                                        maxCode);
    findingThread = new IndependentFindingThread(0, targetMaxValue, info,
                                                 calibrated, luminanceCalibrate,
                                                 chromaticCalibrate, white,
                                                 calibrator);
    findingThread.setEnableTrace(false);
    gm.setHKStrategy(GSDFGradientModel.HKStrategy.None);
    info.init();
  }

  public RGB getRGB(RGB initRGB, CIEXYZ targetXYZ) {
    targetxyY = new CIExyY(targetXYZ);
    info.setInitRGBArray(new RGB[] {initRGB});
    calibrated[0] = false;
    findingThread.run();
//    CPCodeLoader.loadOriginalDummy();
    return info.getCalibratedRGB(0);
  }

  protected class CalibratorInstance
      extends CalibratorAccessAdapter {

    public CalibratorInstance(CIEXYZ white, MeasureInterface mi,
                              JNDIInterface jndi, DeltauvQuadrant quadrant,
                              double maxCode) {
      super(white, mi, jndi, quadrant, maxCode, false);
    }

    public CalibratorInstance(CIEXYZ white, CPCodeMeasurement cpm,
                              JNDIInterface jndi) {
      super(white, cpm, jndi);
    }

    /**
     * getTargetxyY
     *
     * @param index int
     * @return CIExyY
     */
    public CIExyY getTargetxyY(int index) {
      return targetxyY;
    }

    /**
     * getIndexNearestAlogorithm
     *
     * @return NearestAlogorithm
     */
    public NearestAlgorithm getIndexNearestAlogorithm() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * getJNDI
   *
   * @param XYZ CIEXYZ
   * @return double
   */
  public double getJNDI(CIEXYZ XYZ) {
    return gm.getJNDIndex(XYZ);
  }
}
