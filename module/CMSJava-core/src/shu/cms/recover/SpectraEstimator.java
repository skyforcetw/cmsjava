package shu.cms.recover;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.ideal.*;
import shu.cms.plot.*;
import static shu.cms.recover.SVD.*;
import shu.cms.reference.spectra.*;
import shu.math.*;
import shu.math.array.*;
//import shu.plot.*;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 利用IdealDigitalCamera的光譜光反應函數的特性, 從RGB預測光譜值.
 * 光譜的反推是基於Munsell Book
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class SpectraEstimator {
  protected IdealDigitalCamera camera;
  protected SpectraDatabase.Content source;
  protected int k;
  protected Spectra illuminant;
  protected SVD svd;

  protected int start = 400;
  protected int end = 700;
  protected int interval = 10;

  /**
   * 不考慮到照明光源的使用方式, 此時假設光源為E光源
   * @param camera IdealDigitalCamera
   * @param source Content 以Munsell做光譜基底向量時,採用的Edition
   * @param k int 基底向量的個數
   */
  public SpectraEstimator(IdealDigitalCamera camera,
                          SpectraDatabase.Content source,
                          int k) {
    this(camera, source, k, null);
  }

  /**
   * 考慮照明光源對於物體色影響的使用方式
   * @param camera IdealDigitalCamera
   * @param source Content 以Munsell做光譜基底向量時,採用的Edition
   * @param k int 基底向量的個數
   * @param illuminant Spectra 照明光源的光譜
   */
  public SpectraEstimator(IdealDigitalCamera camera,
                          SpectraDatabase.Content source,
                          int k, Spectra illuminant) {
    this.camera = camera;
    this.source = source;
    this.k = k;

    //==========================================================================
    // 整個Estimator的運作採用與 source 相同的設定
    //==========================================================================
    this.start = source.start;
    this.end = source.end;
    this.interval = source.interval;
    //==========================================================================

    if (illuminant != null) {
      this.illuminant = illuminant.reduce(start, end, interval);
    }

    svd = new SVD(source, illuminant);
  }

  /**
   * 從RGB值預測光譜
   * @param RGBValues double[]
   * @return double[]
   */
  public abstract double[] estimateSpectraData(double[] RGBValues);

  public Spectra estimateSpectra(double[] RGBValues) {
    //預測出來的光譜資料
    double[] spectraData = estimateSpectraData(RGBValues);
    //轉成光譜物件
    Spectra sw = new Spectra(null, Spectra.SpectrumType.EMISSION, start,
                             end, interval, spectraData);
    return sw;
  }

  protected double[][] _U;
  protected double[][] _Uk;

  /**
   * 取得基底向量U以及部分基底向量Uk
   */
  protected void getUAndUk() {
    _U = svd.getU();
    _Uk = getUk(_U, k);
  }

  /**
   * 將光譜反射率的數值合理化 (限制其為0~1)
   * @param reflective double[]
   * @return double[]
   */
  protected double[] rationalize(double[] reflective) {
    int size = reflective.length;
    for (int x = 0; x < size; x++) {
      reflective[x] = reflective[x] > 1 ? 1 : reflective[x];
      reflective[x] = reflective[x] < 0 ? 0 : reflective[x];
    }
    return reflective;
  }

  /**
   * 部分基底向量,前k個基底向量組成
   * @param U double[][]
   * @param k int
   * @return double[][]
   */
  protected final static double[][] getUk(double[][] U, int k) {
    return DoubleArray.getColumnsRangeCopy(U, 0, k - 1);
  }

  /**
   * 誤差
   * @param k int 基向量係數
   * @param sv double[] 奇異值
   * @return double 特性生存比
   */
  protected final static double e(int k, double[] sv) {
    double sum = 0.0;
    for (int x = k; x < sv.length; x++) {
      sum += Maths.sqr(sv[x]);
    }
    return Math.sqrt(sum);
  }

  /**
   * 相對誤差
   * @param k int
   * @param sv double[]
   * @return double
   */
  protected final static double er(int k, double[] sv) {
    return e(k, sv) / e(0, sv);
  }

  /**
   * 特性生存比
   * @param k int
   * @param sv double[]
   * @return double
   */
  protected final static double en(int k, double[] sv) {
    return 1. - er(k, sv);
  }

  /**
   * 取得光譜weight function, 即為camera的光譜光反應函數
   * @param camera IdealDigitalCamera
   * @return double[][]
   */
  protected double[][] WT(IdealDigitalCamera camera) {
    Spectra[] sensor = camera.getSensors();
    int size = (end - start) / interval + 1;

    double[][] W = new double[3][size];
    for (int x = 0; x < 3; x++) {
      Spectra spectra = sensor[x];
      for (int w = 0; w < size; w++) {
        W[x][w] = spectra.getData(start + w * interval) * interval;
      }
    }
    return W;
  }

  /**
   * 利用RGBpatchList預測光譜,並且以illuminant為白點計算Lab,並回傳
   * @param RGBpatchList List
   * @param illuminant Spectra
   * @return List
   */
  public final List<Patch> produceModelPatchList(final List<Patch>
                                                 RGBpatchList,
                                                 Spectra illuminant) {
    int size = RGBpatchList.size();
    List<Spectra> spectraList = new ArrayList<Spectra> (size);
    double[] rgbValues = new double[3];

    for (int x = 0; x < size; x++) {
      Patch p = RGBpatchList.get(x);
      RGB rgb = p.getRGB();
      rgb.getValues(rgbValues, RGB.MaxValue.Double1);
      double[] spectraData = estimateSpectraData(rgbValues);

      Spectra s = new Spectra(null, Spectra.SpectrumType.EMISSION, start, end,
                              interval, spectraData);
      spectraList.add(s);
    }

    List<Patch> modelPatchList = Patch.Produce.LabPatches(spectraList,
        ColorMatchingFunction.CIE_1931_2DEG_XYZ, illuminant);

    return modelPatchList;

  }

  public static void main(String[] args) {
    Spectra d50 = Illuminant.D50.getSpectra();
    SpectraEstimator est = new Wiener(IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.CIEXYZ),
                                      SpectraDatabase.Content.MunsellGlossy, 3,
                                      d50);
    CIEXYZ XYZ = d50.getXYZ();
    Spectra s = est.estimateSpectra(XYZ.getValues());
    Spectra reduceD50 = d50.reduceTo(s);
    Spectra r = s.getSpectralReflectance(reduceD50);

    Spectra e = Illuminant.E.getSpectra();
    SpectraEstimator est2 = new Wiener(IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.CIEXYZ),
                                       SpectraDatabase.Content.MunsellGlossy, 3,
                                       e);
    CIEXYZ XYZ2 = e.getXYZ();
    Spectra s2 = est2.estimateSpectra(XYZ2.getValues());
    Spectra reduceE = e.reduceTo(s2);
    Spectra r2 = s2.getSpectralReflectance(reduceE);

    Plot2D plot = Plot2D.getInstance();
    plot.addSpectra("d", s);
    plot.addSpectra("e", s2);
//    plot.addSpectra("d", r);
//    plot.addSpectra("e", r2);
    plot.addLegend();
    plot.setVisible();
  }
}
