package vv.cms.lcd.calibrate.measured;

import java.text.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import vv.cms.lcd.calibrate.measured.algo.*;
import vv.cms.lcd.calibrate.measured.util.*;
import vv.cms.lcd.calibrate.parameter.*;
import shu.cms.measure.*;
import shu.math.array.*;

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
public class WhitePointCalibrator
    extends MeasuredCalibrator {

  public WhitePointCalibrator(LCDTarget logoLCDTaget,
                              MeasuredCalibrator measuredCalibrator) {
    super(logoLCDTaget, measuredCalibrator);
    initAlgo();
  }

  public WhitePointCalibrator(LCDTarget logoLCDTaget,
                              MeterMeasurement meterMeasurement,
                              ColorProofParameter p,
                              WhiteParameter wp, MeasureParameter mp) {
    super(logoLCDTaget, meterMeasurement, p, wp, null, mp);
    initAlgo();
  }

  private void initAlgo() {
    chromaAroundAlgo = new ChromaticAroundAlgorithm(wp.maxWhiteCode);
    chromaAroundAlgo.setMode(Algorithm.Mode.White);
    nearestAlgo = new CIEuv1960NearestAlgorithm(null, mi);
    nearestAlgo.setMode(Algorithm.Mode.White);
  }

  private RGB initWhiteRGB;

  /**
   * 執行校正, 內部呼叫用
   *
   * @return RGB[]
   */
  protected RGB[] _calibrate() {
    RGB whiteRGB = null;
    if (initWhiteRGB != null) {
      whiteRGB = (RGB) initWhiteRGB.clone();
    }
    else {
      RGB[] cpcode = getCPCodeRGBArray();
      whiteRGB = (RGB) cpcode[cpcode.length - 1].clone();
    }

    if (startWithIntegerRGB) {
      whiteRGB.quantization(RGB.MaxValue.Int8Bit);
    }
    RGB nearestRGB = whiteRGB;
    CIEXYZ whiteXYZ = getWhitexyY().toXYZ();

    if (startWithIntegerRGB) {
      //先以整數找到最接近的RGB (用整數找最快)
      nearestRGB = getWhiteRGBInInteger(nearestRGB, whiteXYZ);
    }
    for (double step = 1; step >= maxValue.getStepIn255(); step /= 2) {
      boolean firstNearest = false;
      this.traceDetail("(255) start calibrate, in step " + step);
      do {
//        RGB[] aroundRGB = chromaAroundAlgo.getAroundRGB(nearestRGB, step);
        RGB[] aroundRGB = getAroundRGB(nearestRGB, step);
        AlgoResult result = nearestAlgo.getNearestRGB(whiteXYZ, aroundRGB);
        nearestRGB = result.nearestRGB;
        this.traceDetail("(255) " + Arrays.toString(aroundRGB) + " near:" +
                         nearestRGB);
        CIEXYZ[] aroundXYZ = result.aroundXYZ;
        firstNearest = MeasuredUtils.isFirstNearestXYZInuvPrime(whiteXYZ,
            aroundXYZ);
      }
      while (!firstNearest);
      this.traceDetail(
          "(255) find rgb:" + nearestRGB + ", in step " + step);
    }
    this.traceDetail("(255) calibrate end (rgb:" + nearestRGB + ")");
    calculateDelta(nearestRGB, whiteXYZ);
    this.traceDetail("(255) deltaE: " + df4.format(deltaE) +
                     " deltauv': " + DoubleArray.toString(df4, deltauvp));

    return new RGB[] {
        nearestRGB};
  }

  private RGB[] getAroundRGB(RGB centerRGB, double step) {
    RGB[] aroundRGB = chromaAroundAlgo.getAroundRGB(centerRGB, step);
    if (mp.whiteSequenceMeasure) {
      //插入色塊序列
      int count = mp.sequenceMeasureCount;
      int itemWidth = count + 1;

      int size = aroundRGB.length * itemWidth;
      RGB[] result = new RGB[size];
      for (int x = 0; x < aroundRGB.length; x++) {
        int targetIndex = x * itemWidth + count;
        result[targetIndex] = aroundRGB[x];
        for (int y = targetIndex - count; y < targetIndex; y++) {
          result[y] = (RGB) aroundRGB[x].clone();
          int v = - (targetIndex - y);
          result[y].addValues(v);
          result[y].rationalize();
        }
      }
      return result;
    }

    return aroundRGB;
  }

  private final static DecimalFormat df4 = new DecimalFormat("####.####");

  private double deltaE;
  private double[] deltauvp;

  /**
   * 計算rgb的XYZ與目標XYZ之間的差異，包括DeltaE 2000和Delta u'v'
   * @param rgb RGB
   * @param target CIEXYZ
   */
  private void calculateDelta(RGB rgb, CIEXYZ target) {
    Patch p = this.measure(new RGB[] {rgb}).getPatch(0);
    CIEXYZ XYZ = p.getXYZ();
    CIEXYZ targetXYZ = (CIEXYZ) target.clone();
    targetXYZ.scaleY(XYZ.Y);
    DeltaE deltaE = new DeltaE(XYZ, targetXYZ, targetXYZ);
    this.deltaE = deltaE.getCIE2000DeltaE();

    CIExyY targetxyY = new CIExyY(target);
    CIExyY xyY = new CIExyY(XYZ);
    this.deltauvp = targetxyY.getDeltauvPrime(xyY);

  }

  /**
   * 先把RGB量化成整數再進行尋找，其用意是以整數開始找，在第一輪可以完全不用到load cp code。
   * 但是實際差異其實不會太大，因此建議關閉即可。
   */
  private final boolean startWithIntegerRGB = false;

  protected RGB getWhiteRGBInInteger(RGB initRGB, CIEXYZ whiteXYZ) {
    RGB nearestRGB = initRGB;
    boolean findNearest = false;
    //關閉自動關閉的功能, 避免一直閃爍
    this.mm.setAutoClose(false);

    do {
      RGB[] aroundRGB = getAroundRGB(nearestRGB, 1);
      AlgoResult result = nearestAlgo.getNearestRGB(whiteXYZ, aroundRGB);
      CIEXYZ[] aroundXYZ = result.aroundXYZ;
      nearestRGB = result.nearestRGB;
      findNearest = MeasuredUtils.isFirstNearestXYZInuvPrime(whiteXYZ,
          aroundXYZ);
    }
    while (!findNearest);
    this.mm.setAutoClose(true);

    return nearestRGB;
  }

  private ChromaticAroundAlgorithm chromaAroundAlgo;
  private CIEuv1960NearestAlgorithm nearestAlgo;

  /**
   * 回傳校正結果, 包過RGB以及目標XYZ等
   *
   * @return List
   */
  public List getCalibratedPatchList() {
    throw new UnsupportedOperationException();
  }

  /**
   * 設定初始的white RGB, 如果此處有設定, 就會忽略targetLogoFilename的white RGB
   * @param initWhiteRGB RGB
   */
  public void setInitWhiteRGB(RGB initWhiteRGB) {
    this.initWhiteRGB = initWhiteRGB;
  }

  /**
   * 取得校正過程的資訊
   * @return String
   * @todo M getCalibratedInfomation
   */
  public String getCalibratedInfomation() {
    return null;
  }

//  public double getDeltaE() {
//    return deltaE;
//  }
//
//  public double[] getDeltauvp() {
//    return deltauvp;
//  }
}
