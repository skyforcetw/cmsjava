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
   * ����ե�, �����I�s��
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
      //���H��Ƨ��̱���RGB (�ξ�Ƨ�̧�)
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
      //���J����ǦC
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
   * �p��rgb��XYZ�P�ؼ�XYZ�������t���A�]�ADeltaE 2000�MDelta u'v'
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
   * ����RGB�q�Ʀ���ƦA�i��M��A��ηN�O�H��ƶ}�l��A�b�Ĥ@���i�H�������Ψ�load cp code�C
   * ���O��ڮt����ꤣ�|�Ӥj�A�]����ĳ�����Y�i�C
   */
  private final boolean startWithIntegerRGB = false;

  protected RGB getWhiteRGBInInteger(RGB initRGB, CIEXYZ whiteXYZ) {
    RGB nearestRGB = initRGB;
    boolean findNearest = false;
    //�����۰��������\��, �קK�@���{�{
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
   * �^�Ǯե����G, �]�LRGB�H�Υؼ�XYZ��
   *
   * @return List
   */
  public List getCalibratedPatchList() {
    throw new UnsupportedOperationException();
  }

  /**
   * �]�w��l��white RGB, �p�G���B���]�w, �N�|����targetLogoFilename��white RGB
   * @param initWhiteRGB RGB
   */
  public void setInitWhiteRGB(RGB initWhiteRGB) {
    this.initWhiteRGB = initWhiteRGB;
  }

  /**
   * ���o�ե��L�{����T
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
