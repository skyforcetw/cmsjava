package shu.cms.dc.estimate;

import java.util.*;
import java.util.List;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.DoubleArray;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * Θだ疭紉秖箇代稰じン眯は琈ㄧ计
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class PrincipalEigenvectorEstimator
    extends SensorResponseEstimator {
  protected DCTarget target;
  protected List<Patch> patchList;

  protected DCTarget[] tagetArray;

  /**
   * target箇代眯は琈ㄧ计
   * @param target DCTarget
   */
  public PrincipalEigenvectorEstimator(DCTarget target) {
    this.target = target;
    patchList = target.filter.patchListForProfile();
  }

  /**
   * target箇代眯は琈ㄧ计
   * @param targetArray DCTarget[]
   */
  public PrincipalEigenvectorEstimator(DCTarget[] targetArray) {
    this.tagetArray = targetArray;
    patchList = producePatchList(tagetArray);
  }

  /**
   * 盢tagetArray┮Τ︹遏锣ΘList<Patch>
   * @param tagetArray DCTarget[]
   * @return List
   */
  protected final static List<Patch> producePatchList(DCTarget[] tagetArray) {
    int size = tagetArray.length;
    List<Patch> patchList = new LinkedList<Patch> ();
    for (int x = 0; x < size; x++) {
      patchList.addAll(tagetArray[x].filter.patchListForProfile());
    }
    return patchList;
  }

  protected double[][] getc(int k) {
    int size = patchList.size();
    double[] ck = new double[size];
    double[] RGBValues = new double[3];
    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      p.getRGB().getValues(RGBValues);
      ck[x] = RGBValues[k];
    }

    double[][] c = DoubleArray.transpose(ck);
    return c;
  }

  protected Spectra typicalSpectra;

  protected double[][] getR() {
    int size = patchList.size();
    //pxn
    double[][] R = new double[size][];

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      Spectra s = p.getSpectra();
      R[x] = s.getData();
    }
    typicalSpectra = patchList.get(0).getSpectra();
    //nxp
    return DoubleArray.transpose(R);
  }

  protected final static double[][] getWMinus(double[] sv, int r) {
    double[] realsv = new double[r];
    System.arraycopy(sv, 0, realsv, 0, r);
    realsv = DoubleArray.reciprocal(realsv);
    double[][] W = new double[r][r];
    DoubleArray.diagonal(W, realsv);
    return DoubleArray.transpose(W);
  }

  protected final static double[][] getV(double[][] V, int r) {
    int size = V.length;
    double[][] realV = new double[size][r];
    for (int x = 0; x < size; x++) {
      System.arraycopy(V[x], 0, realV[x], 0, r);
    }
    return realV;
  }

  protected final static double[][] getU(double[][] U, int r) {
    int size = U.length;
    double[][] realU = new double[size][r];
    for (int x = 0; x < size; x++) {
      System.arraycopy(U[x], 0, realU[x], 0, r);
    }
    return realU;
  }

  /**
   * 箇代稰じン眯は琈ㄧ计
   * @param r int 蹦ノ膀┏秖计秖
   * @return Spectra[]
   */
  public Spectra[] estimate(int r) {
    //nxp
    double[][] R = getR();
    SVDLib svd = new SVDLib(R);
    double[][] V = getV(svd.getV(), r);
    double[][] WMinus = getWMinus(svd.getSingularValues(), r);
    double[][] Ut = DoubleArray.transpose(getU(svd.getU(), r));

    double[][] c0 = this.getc(0);
    double[][] c1 = this.getc(1);
    double[][] c2 = this.getc(2);

    double[][] w = DoubleArray.times(DoubleArray.times(V, WMinus), Ut);
    double[][] w0 = DoubleArray.times(DoubleArray.transpose(w), c0);
    double[][] w1 = DoubleArray.times(DoubleArray.transpose(w), c1);
    double[][] w2 = DoubleArray.times(DoubleArray.transpose(w), c2);

    double[] f0 = DoubleArray.transpose(w0)[0];
    double[] f1 = DoubleArray.transpose(w1)[0];
    double[] f2 = DoubleArray.transpose(w2)[0];

    Spectra[] spectraArray = new Spectra[3];
    Spectra s = typicalSpectra;
    spectraArray[0] = new Spectra(null, Spectra.SpectrumType.FUNCTION,
                                  s.getStart(), s.getEnd(), s.getInterval(), f0);
    spectraArray[1] = new Spectra(null, Spectra.SpectrumType.FUNCTION,
                                  s.getStart(), s.getEnd(), s.getInterval(), f1);
    spectraArray[2] = new Spectra(null, Spectra.SpectrumType.FUNCTION,
                                  s.getStart(), s.getEnd(), s.getInterval(), f2);

    return spectraArray;
  }

  /**
   * 盢眯瞶て
   * @param spectraArray Spectra[]
   * @return Spectra[]
   */
  public final static Spectra[] rationalize(Spectra[] spectraArray) {
    int size = spectraArray.length;
    for (int x = 0; x < size; x++) {
      Spectra s = spectraArray[x];
      s.rationalize();
    }
    return spectraArray;
  }

  /**
   *
   * @param args String[]
   * @deprecated
   */
  public static void main(String[] args) {
    LightSource.i1Pro[] lightSource = new LightSource.i1Pro[] {
        LightSource.i1Pro.D50 /*, LightSource.i1Pro.D65,
                           LightSource.i1Pro.F8, LightSource.i1Pro.F12*/};
    double[] factor = DCUtils.normalizeFactor(lightSource);
//    Spectra[] lightSourceSpectra = DCUtils.getSpectra(lightSource);
//    double[] factor = DCUtils.produceNormalFactorByMaxPeak(lightSourceSpectra);

    DCTarget D50Target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                               lightSource[0],
                                               factor[0], DCTarget.Chart.CCSG);
//    DCTarget D65Target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
//                                               lightSource[1],
//                                               factor[1], DCTarget.Chart.CCSG);
//    DCTarget F8Target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
//                                              lightSource[2],
//                                              factor[2], DCTarget.Chart.CCSG);
//    DCTarget F12Target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
//                                               lightSource[3],
//                                               (1.0) * factor[3],
//                                               DCTarget.Chart.CCSG);

    DCTarget[] trainingTargetArray = new DCTarget[] {
        D50Target
        /*, D65Target/*, F2Target, F8Target*/};

//    DCTarget[] testTargetArray = new DCTarget[] {
//        D50Target, D65Target, F8Target, F12Target};

    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);
    for (int x = 3; x <= 7; x++) {
      PrincipalEigenvectorEstimator estimator = new
          PrincipalEigenvectorEstimator(
              trainingTargetArray);
      Spectra[] spectras = estimator.estimate(x);
      spectras = rationalize(spectras);

      IdealDigitalCamera dc = new IdealDigitalCamera(spectras, null);
//      System.out.println("max:" + DoubleArray.toString(dc.getMaximunRGBValues()));
      DCTarget testTarget = D50Target;
      int size = testTarget.size();
      double[] deltaRG = new double[size];
      double[][] deltaRGB = new double[size][3];
      for (int m = 0; m < size; m++) {
        Patch p = testTarget.getPatch(m);
        Spectra s = p.getSpectra();
        double[] RGBValues = dc.capture(s);
        double[] orgRGBValues = dc.getOriginalOutputRGBValues( (double[])
            RGBValues.clone());
        RGB estimateRGB = new RGB(RGB.ColorSpace.unknowRGB, orgRGBValues,
                                  RGB.MaxValue.Double1);
//        estimateRGB.changeMaxValue(RGB.MaxValue.Int8Bit);
        RGB realRGB = p.getRGB();
//        realRGB.changeMaxValue(RGB.MaxValue.Int8Bit);
        deltaRG[m] = RGB.Delta.deltaRG(estimateRGB, realRGB);
        deltaRGB[m] = RGB.Delta.deltaRGB(estimateRGB, realRGB);
        deltaRGB[m] = DoubleArray.times(deltaRGB[m], 255.);
//        System.out.println(estimateRGB + " " + realRGB + ":" + deltaRG[m] + "/" +
//                           DoubleArray.toString(deltaRGB[m])); // + " -- " +
//                           DoubleArray.toString(RGBValues) + "/" +
//                           DoubleArray.toString(orgRGBValues));
      }
      System.out.println("r:" + x);
      System.out.println("deltaRG:" + Maths.mean(deltaRG));
      double[][] deltaRGBT = DoubleArray.transpose(deltaRGB);
//      deltaRGBT = DoubleArray.times(deltaRGBT, 255.);
      System.out.println("meanDeltaRGB:" + Maths.mean(deltaRGBT[0]) + " " +
                         Maths.mean(deltaRGBT[1]) + " " +
                         Maths.mean(deltaRGBT[2]));
      System.out.println("maxDeltaRGB:" + Maths.max(deltaRGBT[0]) + " " +
                         Maths.max(deltaRGBT[1]) + " " +
                         Maths.max(deltaRGBT[2]));
      System.out.println("minDeltaRGB:" + Maths.min(deltaRGBT[0]) + " " +
                         Maths.min(deltaRGBT[1]) + " " +
                         Maths.min(deltaRGBT[2]));
      System.out.println("stdminDeltaRGB:" + Maths.std(deltaRGBT[0]) + " " +
                         Maths.std(deltaRGBT[1]) + " " +
                         Maths.std(deltaRGBT[2]));

      plot.addSpectra(null, Color.RED, spectras[0]);
      plot.addSpectra(null, Color.GREEN, spectras[1]);
      plot.addSpectra(null, Color.BLUE, spectras[2]);
      plot.setTitle(String.valueOf(x));
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException ex) {
      }
      plot.removeAllPlots();
    }

  }

}
