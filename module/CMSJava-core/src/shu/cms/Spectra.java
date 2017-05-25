package shu.cms;

import java.io.*;
import java.util.*;

import de.pxlab.pxl.spectra.*;
import shu.cms.colorformat.cxf.*;
import shu.cms.colorformat.cxf.attr.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.ideal.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public final class Spectra
    implements SpectraIF, Serializable, Cloneable, NameIF {
  /**
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 用來表示這個光譜的性質,目前在程式內沒有其特別用途
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public static enum SpectrumType {
    NO_ASSIGN, //未特別指定
    EMISSION, //發光光譜
    REFLECTANCE, //反射光譜
    FUNCTION, //光譜函式
    AMBIENTLIGHT, //環境光光譜
    TRANSMISSION //光譜穿透率
  }

  /**
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 用來表示這個光譜單位的性質,目前在程式內沒有其特別用途
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public static enum UnitType {
    UNKNOW, RELATIVE, ABSOLUTE
  }

  protected SpectrumType spectraType = SpectrumType.NO_ASSIGN;
  protected UnitType unitType = UnitType.UNKNOW;

  public String toString() {
    return "[" + name + "/" + start + "-" + end + " " + interval + "nm/" +
        spectraType + "/" + unitType + "] " + DoubleArray.toString(data);
  }

  /**
   * 是否是能量值
   * @return boolean
   */
  public final boolean isPowerSpectrum() {
    if (spectraType == SpectrumType.AMBIENTLIGHT ||
        spectraType == SpectrumType.EMISSION) {
      return true;
    }
    else {
      return false;
    }
  }

  public final boolean isSameFormat(Spectra s) {
    return this.spectraType == s.spectraType && this.start == s.start &&
        this.end == s.end && this.interval == s.interval;
  }

  public UnitType getUnitType() {
    return unitType;
  }

  public void setUnitType(UnitType unitType) {
    checkReadOnly();

    this.unitType = unitType;
  }

  protected double[] data;
  protected int start;
  protected int interval;
  protected int end;
  protected boolean readOnly = false;

  protected String name;

  public String getName() {
    return name;
  }

  public Spectra(SpectralDistribution spectralDistribution) {
    this.start = spectralDistribution.getFirst();
    this.end = spectralDistribution.getLast();
    this.interval = spectralDistribution.getStep();
    this.data = FloatArray.toDoubleArray(spectralDistribution.getData());
  }

  public Spectra(String name, SpectrumType spectraType, int start, int end,
                 int interval, double[] data) {
    this.name = name;
    this.spectraType = spectraType;
    this.start = start;
    this.end = end;
    this.interval = interval;
    this.data = data;
  }

  public Spectra(String name, Spectrum spectrum, Conditions conditions) {
    this.name = name;
    parseSpectrum(spectrum);
    parseConditions(conditions);
  }

  /**
   * 將最大值正規化到1
   */
  public void normalizeDataToMax() {
    checkReadOnly();

    double max = 0.0;
    max = Math.max(max, Maths.max(data));
    normalizeData(max);
  }

  /**
   * 以normal作為正規化1
   * @param normal double
   */
  public void normalizeData(double normal) {
    checkReadOnly();

    for (int x = 0; x < data.length; x++) {
      data[x] = (data[x] / normal);
    }

  }

  /**
   * 取得峰值
   * @return int
   */
  public int getPeak() {
    return getPeak(start, end);
  }

  public int getPeak(int wavelengthStart, int wavelengthEnd) {
    double max = 0.0;
    int peak = 0;
    int startIndex = getDataIndex(wavelengthStart);
    int endIndex = getDataIndex(wavelengthEnd);
    for (int x = startIndex; x <= endIndex; x++) {
      if (data[x] > max) {
        max = data[x];
        peak = x;
      }
    }
    return peak * interval + start;
  }

  /**
   * 拓展資料的數量
   * @param start int
   * @param end int
   * @return Spectra
   */
  private Spectra doFillPurlieus(int start, int end) {
    int leftFill = (this.start - start) / interval;
    int rightFill = (end - this.end) / interval;

    double[] modifyData = fillPurlieusData(data, leftFill, rightFill);
    Spectra spectra = new Spectra(this.name, this.spectraType, start, end,
                                  this.interval,
                                  modifyData);
    return spectra;
  }

  protected CIEXYZ CIE1931XYZ;
  protected CIEXYZ CIE1964XYZ;

  public CIEXYZ getCIE1964XYZ() {
    if (CIE1964XYZ == null) {
      CIE1964XYZ = getXYZ(ColorMatchingFunction.CIE_1964_10DEG_XYZ);
    }

    return CIE1964XYZ;
  }

  public CIEXYZ getXYZ() {
    if (CIE1931XYZ == null) {
      CIE1931XYZ = getXYZ(ColorMatchingFunction.CIE_1931_2DEG_XYZ);
    }

    return CIE1931XYZ;
  }

  public void clearCIE1931XYZ() {
    CIE1931XYZ = null;
  }

  public CIEXYZ getXYZ(Illuminant illuminant) {
//    Spectra illuminantSpectra = illuminant.getNormalizeSpectra();
    return getXYZ(illuminant.getSpectra());
  }

  public CIEXYZ getXYZ(Spectra spectra) {
    return timesAndReturn(spectra).getXYZ();
  }

  /**
   * 計算XYZ,需要帶入CMF才可計算
   * @param cmf ColorMatchingFunction
   * @return CIEXYZ
   */
  public CIEXYZ getXYZ(ColorMatchingFunction cmf) {
    double[] XYZValues = sigmaValuesFill(cmf);
    CIEXYZ result = new CIEXYZ(XYZValues);
    result.setDegree(DeviceIndependentSpace.Degree.getDegree(cmf.getDegree()));
    return result;
  }

  public CIELMS getLMS(ConeFundamental cf) {
    CIELMS result = new CIELMS(getLMSValues(cf), cf);
    result.setDegree(DeviceIndependentSpace.Degree.getDegree(cf.getDegree()));
    return result;
  }

  public double[] getLMSValues(ConeFundamental cf) {
    double[] lmsValues = sigmaValuesFill(cf);
    return lmsValues;
  }

  /**
   * 計算光譜反射率
   * @param illuminant Illuminant
   * @return Spectra
   */
  public Spectra getSpectralReflectance(Spectra illuminant) {
    Spectra s = divideAndReturn(illuminant);
    s.spectraType = SpectrumType.REFLECTANCE;
    return s;
  }

  public double[] getRGBValues(IdealDigitalCamera camera) {
    Spectra[] sensors = camera.getSensors();
    Spectra sensor0 = sensors[0];
    double[] modifyData = this.doFillPurlieus(sensor0.getStart(),
                                              sensor0.getEnd()).getData();
    int maxInterval = Math.max(sensor0.interval, interval);

    double[] rgb = new double[3];
    for (int x = 0; x < 3; x++) {
      Spectra sensorSpectra = sensors[x];
      rgb[x] = sigma(sensorSpectra.start, sensorSpectra.end,
                     sensorSpectra.data, sensorSpectra.interval, modifyData,
                     interval) * maxInterval;
    }

    return rgb;
  }

  /**
   * 從光譜反射率以及光源光譜能量值計算反射物光譜能量
   * reflectSpectra與illuminant的光譜資料筆數可不同
   * @param reflectSpectra List
   * @param illuminant Illuminant
   * @return List
   */
  public static List<Spectra> produceSpectraPowerList(List<Spectra>
      reflectSpectra,
      Illuminant illuminant) {
    return produceSpectraPowerList(reflectSpectra, illuminant, false);

  }

  public static List<Spectra> produceSpectraPowerList(List<Spectra>
      reflectSpectra, Illuminant illuminant, boolean takeBigInterval) {
    Spectra firstReflect = reflectSpectra.get(0);

    //取interval
    int interval = Math.min(firstReflect.getInterval(),
                            illuminant.getInterval());
    if (takeBigInterval) {
      //如果是取大的interval
      interval = Math.max(firstReflect.getInterval(),
                          illuminant.getInterval());
    }
    //取範圍較小的
    int start = Math.max(firstReflect.getStart(), illuminant.getStart());
    int end = Math.min(firstReflect.getEnd(), illuminant.getEnd());
    int size = reflectSpectra.size();
    Illuminant alterIlluminant = new Illuminant(illuminant.getSpectra().
                                                fillAndInterpolate(start, end,
        interval, Interpolation.Algo.Lagrange));
    Spectra.fillAndInterpolate(start, end,
                               interval, Interpolation.Algo.Lagrange,
                               reflectSpectra);
    List<Spectra> spectraPowerList = new ArrayList<Spectra> (size);

    for (int x = 0; x < size; x++) {
      Spectra s = reflectSpectra.get(x);
      Spectra power = s.timesAndReturn(alterIlluminant);
      spectraPowerList.add(power);
    }
    return spectraPowerList;
  }

  /**
   * 從光譜反射率以及光源光譜能量值計算反射物光譜能量
   * reflectSpectra與illuminant的光譜資料筆數可須相同
   * @param reflectSpectra List
   * @param illuminantSpectra Spectra
   * @return List
   */
  public static List<Spectra> produceSpectraPowerList(List<Spectra>
      reflectSpectra,
      Spectra illuminantSpectra) {
    int size = reflectSpectra.size();
    List<Spectra> spectraPowerList = new ArrayList<Spectra> (size);

    for (int x = 0; x < size; x++) {
      Spectra s = reflectSpectra.get(x);
      Spectra power = s.timesAndReturn(illuminantSpectra);
      power.spectraType = Spectra.SpectrumType.EMISSION;
      spectraPowerList.add(power);
    }
    return spectraPowerList;
  }

  private static boolean fillZero = false;

  /**
   * 兩邊不足處是否補零(否則就是用頭/尾的數去補)
   * @param zero boolean
   */
  public static void setFillZero(boolean zero) {
    fillZero = zero;
  }

  public void setData(double[] data) {
    checkReadOnly();

    this.data = data;
  }

  public void setName(String name) {
    checkReadOnly();

    this.name = name;
  }

  private void checkReadOnly() {
    if (true == readOnly) {
      throw new IllegalStateException("true == readOnly");
    }
  }

  public void setReadOnly(boolean readOnly) {
    checkReadOnly();

    this.readOnly = readOnly;
  }

  /**
   * 拓展資料的數量
   * @param data double[]
   * @param leftBorder int
   * 左邊要填的數量
   * @param rightBorder int
   * 右邊要填的數量
   * @return double[]
   */
  private static double[] fillPurlieusData(final double[] data,
                                           int leftBorder, int rightBorder) {
    double[] result = new double[data.length + leftBorder + rightBorder];
    //    int size = leftFill + (end - start) / interval + rightFill + 1;
    double first = fillZero ? 0 : data[0];
    double last = fillZero ? 0 : data[data.length - 1];

    for (int x = 0; x < leftBorder; x++) {
      result[x] = first;
    }

    int srcPos = leftBorder < 0 ? -leftBorder : 0;
    int destPos = leftBorder < 0 ? 0 : leftBorder;
    int length = leftBorder < 0 ? leftBorder + data.length :
        data.length;
    length = rightBorder < 0 ? length + rightBorder : length;

    System.arraycopy(data, srcPos, result, destPos, length);

    for (int x = leftBorder + data.length; x < result.length; x++) {
      result[x] = last;
    }
    return result;
  }

  /**
   * 透過interval的設定做適當的相乘加總計算
   * data1與data2具有不同interval時,做加總計算
   * @param start int
   * @param end int
   * @param data1 double[]
   * @param interval1 int
   * @param data2 double[]
   * @param interval2 int
   * @return double
   */
  private static double sigma(int start, int end,
                              double[] data1, int interval1,
                              double[] data2, int interval2) {

    double[] a = null, b = null;
    int step = 0, size = 0;
    if (data1.length > data2.length) {
      a = data2;
      b = data1;
      step = interval2 / interval1;
      size = ( (end - start) / interval2) + 1;
    }
    else {
      a = data1;
      b = data2;
      step = interval1 / interval2;
      size = ( (end - start) / interval1) + 1;
    }

    double total = 0.0;
    for (int x = 0; x < size; x++) {
      total += a[x] * b[x * step];
    }
    return total;
  }

  /**
   * data1和data2具有相同的interval
   * @param start int
   * @param end int
   * @param interval int
   * @param data1 double[]
   * @param data2 double[]
   * @return double
   */
  static double sigma(int start, int end, int interval,
                      double[] data1, double[] data2) {
    if (data1.length != data2.length) {
      throw new IllegalArgumentException("data1.length != data2.length");
    }
    double total = 0.0;

    int size = ( (end - start) / interval) + 1;
    for (int x = 0; x < size; x++) {
      total += data1[x] * data2[x];
    }
    return total;
  }

  private final static double romberg(double x[], double y[]) {
    double Ih = 0.0, Ik = 0.0;
    int i, j = 1, k = 2;
    double I = 0;
    int n = x.length;

    while (k > 0) {
      if ( (n - 1) % k == 0) {
        for (i = 1; i < n; i++) {
          Ih = Ih + (x[i + 1] - x[i]) * (y[i] + y[i + 1]) / 2.0;
        }

        while (j < n) {
          Ik = Ik + (x[j + k] - x[j]) * (y[j] + y[j + k]) / 2.0;
          j = j + k;
        }

        I = (Ih + (Ih - Ik) / (k * k - 1.0));

        break;
      }
      else {
        k = k + 1;
      }
    }

    return I;
  }

  /**
   * 解析CxF的Conditions
   * @param conditions Conditions
   */
  private void parseConditions(Conditions conditions) {
    Physical physical = Physical.getInstance(conditions.
                                             getAttribute());
    if (physical.spectrumType != null) {
      if (physical.spectrumType.equals("Emission")) {
        this.spectraType = SpectrumType.EMISSION;
      }
      else if (physical.spectrumType.equals("AmbientLight")) {
        this.spectraType = SpectrumType.AMBIENTLIGHT;
      }
      else if (physical.spectrumType.equals("Reflectance") ||
               physical.spectrumType.equals("Remission")) {
        this.spectraType = SpectrumType.REFLECTANCE;
      }
    }

    this.end = physical.wavelengthSpectrumMax;
    this.start = physical.wavelengthSpectrumMin;
    int numOfDataPoints = physical.numberOfDataPointsSpectrum;
    this.interval = (end - start) / (numOfDataPoints - 1);
  }

  /**
   * 解析CxF的Spectrum
   * @param spectrum Spectrum
   */
  private void parseSpectrum(Spectrum spectrum) {
    List<Value> values = spectrum.getValue();
    data = new double[values.size()];
    int index = 0;
    for (Value val : values) {
      data[index++] = Double.parseDouble(val.getvalue());
    }
  }

  /**
   * 解析回CxF的Spectrum
   * @return Spectrum
   */
  public Spectrum toSpectrum() {
    Spectrum spectrum = new Spectrum();
    List<Value> valueList = spectrum.getValue();
    int waveLength = this.start;
    for (double d : data) {
      Value val = new Value();
      val.setName(String.valueOf(waveLength));
      val.setvalue(Double.toString(d));
      valueList.add(val);
      waveLength += interval;
    }
    return spectrum;
  }

  public double[] getData() {
    return data;
  }

  public double getData(int wavelength) {
    if (wavelength > end || wavelength < start) {
      throw new IndexOutOfBoundsException();
    }
    return data[getDataIndex(wavelength)];
  }

  public void setData(int wavelength, double power) {
    checkReadOnly();

    if (wavelength > end || wavelength < start) {
      throw new IndexOutOfBoundsException();
    }
    data[getDataIndex(wavelength)] = power;
  }

  protected final int getDataIndex(int wavelength) {
    if (wavelength > end || wavelength < start) {
      throw new IndexOutOfBoundsException();
    }
    return (wavelength - start) / interval;
  }

  /**
   * 取得具有資料的左鄰接波長
   * @param wavelength int
   * @return int
   */
  private int getLeftAdjoin(int wavelength) {
    int mod = wavelength % interval;
    int result = wavelength - mod;

    return result < start ? start : result;
  }

  /**
   * 取得具有資料的右鄰接波長
   * @param wavelength int
   * @return int
   */
  private int getRightAdjoin(int wavelength) {
    int mod = wavelength % interval;
    int result = wavelength + (interval - mod);

    return result > end ? end : result;
  }

  public int getEnd() {
    return end;
  }

  public int getInterval() {
    return interval;
  }

  public int getStart() {
    return start;
  }

  public SpectrumType getSpectraType() {
    return spectraType;
  }

  /**
   * 進行內插
   * @param newInterval int
   * 新的間隔
   * @param interpolationType Type
   * 使用的內插法
   * @return Spectra
   */
  private Spectra doInterpolating(int newInterval,
                                  Interpolation.Algo interpolationType) {
    int size = (end - start) / newInterval + 1;
    double[] newData = new double[size];

    for (int x = 0; x < size; x++) {
      int lambda = start + x * newInterval;
      int left1 = getLeftAdjoin(lambda);
      if (left1 == lambda) {
        //不用內插的場合
        newData[x] = this.getData(lambda);
        continue;
      }

      double[] xn = null, yn = null;
      if (interpolationType == Interpolation.Algo.Linear) {
        int right1 = getRightAdjoin(lambda);
        xn = new double[] {
            left1, right1};
        yn = new double[] {
            getData(left1), getData(right1)};
      }
      else {
        int right1 = getRightAdjoin(lambda);
        if (left1 == start) {
          //到了左邊界限
          int left2 = left1;
          left1 = getRightAdjoin(left2 + 1);
          right1 = getRightAdjoin(left1 + 1);
          int right2 = getRightAdjoin(right1 + 1);

          xn = new double[] {
              left2, left1, right1, right2};
          yn = new double[] {
              getData(left2), getData(left1), getData(right1), getData(right2)};
        }
        else if (right1 == end) {
          //到了右邊界限
          int right2 = right1;
          right1 = getLeftAdjoin(right2 - 1);
          left1 = getLeftAdjoin(right1 - 1);
          int left2 = getLeftAdjoin(left1 - 1);

          xn = new double[] {
              left2, left1, right1, right2};
          yn = new double[] {
              getData(left2), getData(left1), getData(right1), getData(right2)};
        }
        else {
          int left2 = getLeftAdjoin(left1 - 1);
          int right2 = getRightAdjoin(right1 + 1);
          xn = new double[] {
              left2, left1, right1, right2};
          yn = new double[] {
              getData(left2), getData(left1), getData(right1), getData(right2)};
        }
      }

      newData[x] = Interpolation.interpolate(xn, yn, lambda,
                                             interpolationType);
    }
    Spectra spectra = new Spectra(this.name, this.spectraType, start, end,
                                  newInterval,
                                  newData);
    return spectra;
  }

  /**
   * 乘上另外一個光譜內的數值
   * @param spectra Spectra
   */
  public void times(Spectra spectra) {
    checkReadOnly();

    int wavelenthCount = data.length;
    int thatWavelenthCount = spectra.data.length;
    if (wavelenthCount != thatWavelenthCount) {
      throw new IllegalArgumentException("wavelenth count is not euqal!");
    }

    for (int x = 0; x < wavelenthCount; x++) {
      data[x] *= spectra.data[x];
    }
  }

  public void minus(Spectra spectra) {
    checkReadOnly();

    int wavelenthCount = data.length;
    int thatWavelenthCount = spectra.data.length;
    if (wavelenthCount != thatWavelenthCount) {
      throw new IllegalArgumentException("wavelenth count is not euqal!");
    }

    for (int x = 0; x < wavelenthCount; x++) {
      data[x] -= spectra.data[x];
    }
  }

  public void plus(Spectra spectra) {
    checkReadOnly();

    int wavelenthCount = data.length;
    int thatWavelenthCount = spectra.data.length;
    if (wavelenthCount != thatWavelenthCount) {
      throw new IllegalArgumentException("wavelenth count is not euqal!");
    }

    for (int x = 0; x < wavelenthCount; x++) {
      data[x] += spectra.data[x];
    }
  }

  public Spectra minusAndReturn(Spectra spectra) {
    Spectra clone = (Spectra)this.clone();
    clone.minus(spectra);
    return clone;
  }

  public Spectra plusAndReturn(Spectra spectra) {
    Spectra clone = (Spectra)this.clone();
    clone.plus(spectra);
    return clone;
  }

  public void times(double factor) {
    checkReadOnly();

    int size = data.length;
    for (int x = 0; x < size; x++) {
      data[x] *= factor;
    }
    this.clearCIE1931XYZ();
  }

  public Spectra timesAndReturn(double factor) {
    Spectra clone = (Spectra)this.clone();
    clone.times(factor);
    return clone;
  }

  /**
   * 除上另外一個光譜內的數值
   * @param spectra Spectra
   */
  public void divide(Spectra spectra) {
    checkReadOnly();

    int wavelenthCount = data.length;
    int thatWavelenthCount = spectra.data.length;
    if (wavelenthCount != thatWavelenthCount) {
      throw new IllegalArgumentException("wavelenth count is not euqal!");
    }

    for (int x = 0; x < wavelenthCount; x++) {
      data[x] /= spectra.data[x];
    }
  }

  public Spectra timesAndReturn(Illuminant illuminant) {
    Spectra s = timesAndReturn(illuminant.getSpectra());
    s.spectraType = SpectrumType.EMISSION;
    return s;
  }

  public Spectra timesAndReturn(Spectra spectra) {
    Spectra clone = (Spectra)this.clone();
    clone.times(spectra);
    return clone;
  }

  public Spectra divideAndReturn(Spectra spectra) {
    Spectra clone = (Spectra)this.clone();
    clone.divide(spectra);
    return clone;
  }

  /**
   * 將光譜資料減量成spectra的大小
   * @param spectra Spectra
   * @return Spectra
   */
  public Spectra reduceTo(Spectra spectra) {
    return reduce(spectra.getStart(), spectra.getEnd(), spectra.getInterval());
  }

  /**
   * 將光譜資料減量
   * @param start int
   * @param end int
   * @param interval int
   * @return Spectra
   */
  public Spectra reduce(int start, int end, int interval) {
    checkReadOnly();

    int count = (end - start) / interval + 1;
    double[] reducedData = new double[count];

    for (int x = 0; x < count; x++) {
      reducedData[x] = getData(start + x * interval);
    }

    return new Spectra(this.name, this.spectraType, start, end, interval,
                       reducedData);
  }

  /**
   * 將負值調整成為0
   */
  public void rationalize() {
    checkReadOnly();

    int size = data.length;
    for (int x = 0; x < size; x++) {
      data[x] = data[x] < 0 ? 0 : data[x];
    }
  }

  /**
   * 將spectraArray內的Spectra進行平均
   * @param spectraArray Spectra[]
   * @return Spectra
   */
  public final static Spectra average(Spectra[] spectraArray) {
    Spectra first = spectraArray[0];
    int size = first.getData().length;
    double[] data = new double[size];

    for (int x = 0; x < size; x++) {
      double v = 0;
      for (Spectra s : spectraArray) {
        v += s.getData()[x];
      }
      v /= spectraArray.length;
      data[x] = v;
    }
    Spectra ave = new Spectra(first.name, first.getSpectraType(), first.start,
                              first.end, first.interval, data);
    return ave;
  }

  public final Spectra fillAndInterpolate(Spectra reference) {
    return fillAndInterpolate(reference.start, reference.end,
                              reference.interval);
  }

  public final Spectra fillAndInterpolate(int start, int end,
                                          int interval) {
    return fillAndInterpolate(start, end, interval,
                              Interpolation.Algo.Lagrange4);
  }

  /**
   * 拓展資料且內插
   * @param start int
   * @param end int
   * @param interval int
   * @param interpolationType Algo
   * @return Spectra
   */
  public final Spectra fillAndInterpolate(int start, int end,
                                          int interval,
                                          Interpolation.Algo
                                          interpolationType) {
    checkReadOnly();

    //拓展
    Spectra s = doFillPurlieus(start, end);
    //內插
    s = s.doInterpolating(interval, interpolationType);
    return s;
  }

  /**
   * 拓展資料且內插
   * @param start int
   * @param end int
   * @param interval int
   * @param interpolationType Type
   * @param spectraList List
   */
  public final static void fillAndInterpolate(int start, int end, int interval,
                                              Interpolation.Algo
                                              interpolationType,
                                              List<Spectra> spectraList) {
    for (int x = 0; x < spectraList.size(); x++) {
      Spectra spectra = spectraList.remove(x);
      spectra = spectra.fillAndInterpolate(start, end, interval,
                                           interpolationType);
      spectraList.add(x, spectra);
    }
  }

  public final static void times(double factor, List<Spectra>
      spectraList) {

    for (int x = 0; x < spectraList.size(); x++) {
      Spectra spectra = spectraList.remove(x);
      spectra.times(factor);
      spectraList.add(x, spectra);
    }

  }

  public static void main(String[] args) {

    System.out.print("Input Start Wavelength:");
    int start = Integer.parseInt(System.console().readLine());
    System.out.print("Input Wavelength Interval:");
    int interval = Integer.parseInt(System.console().readLine());

    while (true) {
      char read = 0;
      StringBuilder buf = new StringBuilder();
      System.out.println("Input Spectrum data:");

      try {
        while (true) {
          read = (char) System.in.read();
          int bufsize = buf.length();
          if (bufsize >= 1 && buf.charAt(bufsize - 1) == ' ' && read == '\n') {
            break;
          }

          if (read == '\n') {
            buf.append( (char) ' ');
          }
          else if (read != '\r') {
            buf.append( (char) read);
          }
        }
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
      StringTokenizer tokenizer = new StringTokenizer(buf.toString(), " ");
      int counts = tokenizer.countTokens();
      if (counts == 0) {
        break;
      }
      double[] data = new double[counts];
      for (int x = 0; x < counts; x++) {
        data[x] = Double.parseDouble(tokenizer.nextToken());
      }
      int end = start + (counts - 1) * interval;
      Spectra s = new Spectra("", SpectrumType.NO_ASSIGN, start, end, interval,
                              data);
      CIEXYZ XYZ = s.getXYZ();
      CIExyY xyY = new CIExyY(XYZ);

      System.out.println("CIEXYZ: " + XYZ);
      System.out.println("CIExyY: " + xyY);
      double CCT = xyY.getCCT();
      System.out.printf("CCT: %4.2f\n", CCT);
      if (CCT != -1 && !Double.isNaN(CCT)) {
//        System.out.println("duv with blackbody: " +
//                           CorrelatedColorTemperature.getduvWithBlackbody(XYZ));
        System.out.printf("duv with blackbody: %.4f\n",
                          CorrelatedColorTemperature.getduvWithBlackbody(XYZ));
//        System.out.println("duv with D-Illuminant: " +
//                           CorrelatedColorTemperature.getduvWithDIlluminant(XYZ));
        System.out.printf("duv with D-Illuminant: %.4f\n",
                          CorrelatedColorTemperature.getduvWithDIlluminant(XYZ));
      }
    }
  }

  public Object clone() {
    double[] cloneData = data.clone();
    return new Spectra(this.name, this.spectraType, this.start, this.end,
                       this.interval, cloneData);
  }

  double[] sigmaValuesFill(ColorMatchingFunction cf) {
    Spectra modifySpectra = fillAndInterpolate(cf.getStart(), cf.getEnd(),
                                               cf.getInterval());

    double[] values = new double[3];
    for (int x = 0; x < 3; x++) {
      Spectra cmfSpectra = cf.getSpectra(x);
      values[x] = .683002 *
          Spectra.sigma(cmfSpectra.start, cmfSpectra.end, cmfSpectra.interval,
                        modifySpectra.data, cmfSpectra.data) *
          cmfSpectra.interval;
    }
    return values;
  }
}
