package shu.cms.recover;

import java.io.*;

import shu.cms.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;
import static shu.cms.recover.SVD.*;
import shu.cms.reference.spectra.*;
import shu.io.files.*;
import shu.math.*;
import shu.math.array.*;
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
public class Wiener
    extends SpectraEstimator {

  public Wiener(IdealDigitalCamera camera, SpectraDatabase.Content source,
                int k) {
    this(camera, source, k, null);
  }

  public Wiener(IdealDigitalCamera camera, SpectraDatabase.Content source,
                int k, Spectra illuminant) {
    super(camera, source, k, illuminant);
    if (!cProducer.checkCDataFile(k)) {
      cProducer.produceCDataFile(k);
    }
  }

  /**
   * 箇代眯は甮瞯
   * @param RGBValues double[]
   * @return double[]
   */
  public double[] estimateSpectraData(double[] RGBValues) {
    return getSpectrumByWiener(RGBValues);
  }

  /**
   * Wiener眔RGBValues癸莱眯
   * @param RGBValues double[]
   * @return double[]
   */
  public final double[] getSpectrumByWiener(double[] RGBValues) {
    if (_U == null) {
      //膀┏秖
      getUAndUk();
    }
    //眔C玒计
    double[][] c = getC(RGBValues, k);
    double[][] R = DoubleArray.times(_Uk, c);
    R = DoubleArray.transpose(R);
    return R[0];
  }

  public static void main(String[] args) {
    //==========================================================================
    // 计诀
    //==========================================================================
    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.CIEXYZ);
//    IdealDigitalCamera camera = IdealDigitalCamera.getSunInstance(null);
//    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
//        IdealDigitalCamera.Source.BestEstimatedD200);
    //==========================================================================

    //==========================================================================
    // 方
    //==========================================================================
//    Spectra illuminant = Illuminant.D65.getSpectra().reducedSpectra(400, 700,
//        10);
    Illuminant lightSource = LightSource.getIlluminant(LightSource.CIE.D65);
    Spectra spectra = lightSource.getSpectra().reduce(400, 700, 10);
    spectra.normalizeDataToMax();
    //==========================================================================

    //==========================================================================
    // Wiener猭
    //==========================================================================
    Wiener wiener = new Wiener(camera, SpectraDatabase.Content.MunsellGlossy, 3,
                               spectra);
    System.out.println(wiener.cProducer.checkCDataFile(3));
    //==========================================================================
  }

  protected static double[][] wiener(double[][] Ck, double[][] WT,
                                     double[][] Uk) {
    double[][] CkT = DoubleArray.transpose(Ck);
    double[][] CkCkT = DoubleArray.times(Ck, CkT);

    double[][] WTUk = DoubleArray.times(WT, Uk);
    double[][] WTUkT = DoubleArray.transpose(WTUk);
    double[][] WTUkCkCkT = DoubleArray.times(WTUk, CkCkT);

    double[][] c2 = DoubleArray.times(WTUkCkCkT, WTUkT);
    double[][] inv = DoubleArray.inverse(c2);
    double[][] c = DoubleArray.times(DoubleArray.times(CkCkT, WTUkT), inv);
    return c;
  }

  protected final double[][] produceC(int k, double[][] U, double[] sv,
                                      double[][] V) {
    double[][] Uk = getUk(U, k);
    double[][] Ck = getCk(sv, V, k);
    double[][] WT = WT(camera);

    double[][] c = wiener(Ck, WT, Uk);

    return c;
  }

  protected CProducer cProducer = new CProducer();
  protected class CProducer {
    protected final boolean deleteCDataFile(int k) {
      String cameraName = camera.getName();
      if (!RecoverUtils.checkDir(svd.getPath(cameraName))) {
        return true;
      }

      String cFile = svd.getPath(cameraName + "/" + "c" + k + ".dat");
      return RecoverUtils.deleteFile(cFile);
    }

    /**
     * 玻ネ玒计郎(惠璶玻ネΩ)
     * @param k int
     */
    protected final void produceCDataFile(int k) {
      double[][] U = svd.getU();
      double[] sv = svd.getSv();
      double[] sv2 = new double[sv.length - 1];
      System.arraycopy(sv, 0, sv2, 0, sv2.length);
      double[][] V = svd.getV();
      double[][] c = produceC(k, U, sv2, V);

      String cameraName = camera.getName();
      RecoverUtils.checkAndMkdir(svd.getPath(cameraName));
      String cFile = svd.getPath(cameraName + "/" + "c" + k + ".dat");
      BinaryFile.writeDoubleArray(cFile, c);
    }

    protected final boolean checkCDataFile(int k) {
      String cameraName = camera.getName();
      if (!RecoverUtils.checkDir(svd.getPath(cameraName))) {
        return false;
      }
      String cFile = svd.getPath(cameraName + "/" + "c" + k + ".dat");
      return new File(cFile).exists();
    }

  }

  protected final static double[][] getCk(double[] sv, double[][] V, int k) {
    double[] d = new double[k];
    System.arraycopy(sv, 0, d, 0, k);
    double[][] sigma = sigma(d, V.length);
    return DoubleArray.times(sigma, DoubleArray.transpose(V));
  }

  protected final static double[][] sigma(double[] singularValues, int m) {
    int n = singularValues.length;
    double[][] ones = DoubleArray.fill(n, m, 0);
    return DoubleArray.diagonal(ones, singularValues);
  }

  /**
   * 眔c3把计
   * @param k int
   * @return double[][]
   */
  protected double[][] get_c(int k) {
    if (_c == null) {
      String cameraName = camera.getName();
      String cfilename = svd.getPath(cameraName + "/" + "c" + k + ".dat");
      _c = BinaryFile.readDoubleArray(cfilename, 3);
    }
    return _c;
  }

  /**
   * 眔c把计
   * @param XYZValues double[]
   * @param k int
   * @return double[][]
   */
  protected final double[][] getC(double[] XYZValues, int k) {
    get_c(k);
    double[][] XYZ = DoubleArray.transpose(XYZValues);
    double[][] c = DoubleArray.times(_c, XYZ);
    return c;
  }

  protected double[][] _c;

}
