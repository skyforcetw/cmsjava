package shu.cms.recover;

import java.util.*;

import shu.cms.*;
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
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class PseudoInverse
    extends SpectraEstimator {

  public PseudoInverse(IdealDigitalCamera camera,
                       SpectraDatabase.Content source,
                       int k) {
    super(camera, source, k);
  }

  public PseudoInverse(IdealDigitalCamera camera,
                       SpectraDatabase.Content source,
                       int k, Spectra illuminant) {
    super(camera, source, k, illuminant);
  }

  public double[] estimateSpectraData(double[] RGBValues) {
    return getRByPseudoInverse(RGBValues);
  }

  public final double[] getRByPseudoInverse(double[] XYZValues) {
    if (_U == null) {
      getUAndUk();
    }
    double[][] c = pseudoInverse(XYZValues, _Uk);
    double[][] R = DoubleArray.times(_Uk, c);
    R = DoubleArray.transpose(R);
    return R[0];
  }

  public double[][] pseudoInverse(double[] XYZValues, double[][] Uk) {
    if (WTUkInv == null) {
      double[][] WT = WT(camera);
      double[][] WTUk = DoubleArray.times(WT, Uk);
      WTUkInv = DoubleArray.inverse(WTUk);
    }

    double[][] XYZ = DoubleArray.transpose(XYZValues);
    double[][] result = DoubleArray.times(WTUkInv, XYZ);
    return result;
  }

  protected double[][] WTUkInv;

  public static void main(String[] args) {
//    double[][] glossy = SpectraDatabase.MunsellBook.getGlossyEdition();
//    double[] w = DoubleArray.fill(1, 31, .98)[0];
//    Spectra sw = new Spectra(null, Spectra.SpectrumType.EMISSION, 400, 700, 10,
//                             glossy[444]);
    Spectra sw = Illuminant.D65.getSpectra();

//    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
//        IdealDigitalCamera.Type.CIEXYZ);
    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.CIEXYZ, Illuminant.D65);

    double[] rgb = sw.getRGBValues(camera);
//    rgb = DoubleArray.times(rgb, 0.1);
//    rgb = camera.getOriginalOutputRGBValues(rgb);
//    rgb = sw.getXYZ().getValues();
    System.out.println(Arrays.toString(rgb));

//    PseudoInverse pi = new PseudoInverse(camera,
//                                         SpectraDatabase.Content.
//                                         MunsellGlossyPrecise,
//                                         3, sw);
    Wiener pi = new Wiener(camera, SpectraDatabase.Content.MunsellGlossy,
                           5, sw);
    double[] R = pi.estimateSpectraData(rgb);

//    Plot2D.addSpectraStatic(sw);
//    Plot2D.addLinePlotStatic(null, 400, 700, R);
//
    Plot2D plot = Plot2D.getInstance("PseudoInverse", 600, 600);

    plot.setAxeLabel(0, "Wavelegnth");
    plot.setAxeLabel(1, "Reflect");

    plot.addSpectra(null, sw);
    plot.addLinePlot(null, 400, 700, R);
    double[] swdata = sw.reduce(400, 700, 10).getData();
    System.out.println(Maths.RMSD(swdata, R) + " " + Maths.rSquare(swdata, R));

    plot.setFixedBounds(1, 0., 1.);
    plot.setVisible(true);
  }
}
