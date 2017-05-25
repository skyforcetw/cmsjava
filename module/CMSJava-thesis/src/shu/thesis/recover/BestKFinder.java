package shu.thesis.recover;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.dc.ideal.*;
import shu.cms.recover.*;
import shu.cms.reference.spectra.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 尋找光譜式空間轉換的最佳k值(基底向量數目)
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class BestKFinder {

  public static void example1(String[] args) {
//    double[][] spectralData = SpectraDatabase.SOCS.getTypical();
    double[][] spectralData = SpectraDatabase.MunsellBook.getGlossyEdition();
    Illuminant illuminant = Illuminant.D65;
    Spectra illuminantSpectra = illuminant.getSpectra().reduce(400, 700,
        10);

//    IdealDigitalCamera camera = Materials.getCIECamera();
//    IdealDigitalCamera camera = IdealDigitalCamera.getSunInstance(Illuminant.
//        E);
//    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
//        IdealDigitalCamera.Source.AdobeRGB);
    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
        IdealDigitalCamera.Source.CIEXYZ);
    camera.reduceSensors(400, 700, 10);

    int count = spectralData.length;

    //==========================================================================
    // 資料初始化
    //==========================================================================
    List<Spectra>
        spectraListOrg = Materials.getIlluminantSpectraList(spectralData,
        illuminantSpectra);
    double[][] rmsDataOrg = Materials.getSpectraData(spectraListOrg);
    double[][] rgbData = Materials.getRGBValues(spectraListOrg, camera);
    rgbData = Materials.getOriginalOutputRGBValues(rgbData, camera);

    List<Patch> deltaEDataOrg = Patch.Produce.LabPatches(spectraListOrg,
        ColorMatchingFunction.
        CIE_1931_2DEG_XYZ, illuminantSpectra);
    //==========================================================================
//    DeltaEReport.setDecimalFormat(new DecimalFormat("##.###"));

    double[][] rmsDataEst = new double[count][31];
    for (int k = 3; k <= 12; k++) {
      if (k == 3) {
        List<Spectra> spectraListEst = new ArrayList<Spectra> (count);

        PseudoInverse estimator = new PseudoInverse(camera,
            SpectraDatabase.Content.MunsellGlossy, k, illuminantSpectra);

        for (int x = 0; x < count; x++) {
          Spectra sw = estimator.estimateSpectra(rgbData[x]);
          rmsDataEst[x] = sw.getData();
          spectraListEst.add(sw);
        }
        double rmsd = Maths.RMSD(rmsDataOrg, rmsDataEst);
        System.out.printf("k: " + k + " rmsd: %1.5f\n", rmsd);

        List<Patch> deltaEDataEst = Patch.Produce.LabPatches(spectraListEst,
            ColorMatchingFunction.CIE_1931_2DEG_XYZ, illuminantSpectra);
        DeltaEReport[] reports = DeltaEReport.Instance.patchReport(
            deltaEDataOrg,
            deltaEDataEst, false);
        System.out.println(reports[0]);

      }

      List<Spectra> spectraListEst = new ArrayList<Spectra> (count);
      Wiener estimator = new Wiener(camera,
                                    SpectraDatabase.Content.MunsellGlossy, k,
                                    illuminantSpectra);
//      Wiener estimator = new Wiener(camera, SVD.Munsell.Glossy, k);

      for (int x = 0; x < count; x++) {
//        double[] estimate = estimator.estimateSpectraData(rgbData[x]);
//        Spectra sw = Materials.getSpectra(estimate);
        Spectra sw = estimator.estimateSpectra(rgbData[x]);
        rmsDataEst[x] = sw.getData();
        spectraListEst.add(sw);
      }
      double rmsd = Maths.RMSD(rmsDataOrg, rmsDataEst);
      System.out.printf("k: " + k + " rmsd: %1.5f\n", rmsd);

      List<Patch> deltaEDataEst = Patch.Produce.LabPatches(spectraListEst,
          ColorMatchingFunction.CIE_1931_2DEG_XYZ, illuminantSpectra);
      DeltaEReport[] reports = DeltaEReport.Instance.patchReport(deltaEDataOrg,
          deltaEDataEst, false);
      System.out.println(reports[0]);
    }

  }

  public static void main(String[] args) {
    test1(null);
  }

  public static void test1(String[] args) {
//    double[][] spectralData = SpectraDatabase.SOCS.getTypical();
    double[][] spectralData = SpectraDatabase.MunsellBook.
        getGlossyEditionPrecise();
    Illuminant illuminant = Illuminant.D65;
    Spectra illuminantSpectra = illuminant.getSpectra().reduce(380, 780, 5);

//    IdealDigitalCamera camera = IdealDigitalCamera.getInstance(
//        IdealDigitalCamera.Source.CIEXYZ);
    SpectralCamera camera = new SpectralCamera(RGB.ColorSpace.AdobeRGB);

    int count = spectralData.length;

    //==========================================================================
    // 資料初始化
    //==========================================================================
    List<Spectra>
        spectraListOrg = Materials.getIlluminantSpectraList(spectralData,
        illuminantSpectra);
    double[][] rmsDataOrg = Materials.getSpectraData(spectraListOrg);
    double[][] rgbData = Materials.getRGBValues(spectraListOrg, camera);
//    rgbData = Materials.getOriginalOutputRGBValues(rgbData, camera);


    List<Patch> deltaEDataOrg = Patch.Produce.LabPatches(spectraListOrg,
        ColorMatchingFunction.
        CIE_1931_2DEG_XYZ, illuminantSpectra);
    //==========================================================================
//    DeltaEReport.setDecimalFormat(new DecimalFormat("##.###"));

    double[][] rmsDataEst = new double[count][];
    for (int k = 3; k <= 12; k++) {

      List<Spectra> spectraListEst = new ArrayList<Spectra> (count);

      for (int x = 0; x < count; x++) {
        Spectra sw = camera.getSpectra(new RGB(RGB.ColorSpace.AdobeRGB,
                                               rgbData[x]));
        rmsDataEst[x] = sw.getData();
        spectraListEst.add(sw);
      }
      double rmsd = Maths.RMSD(rmsDataOrg, rmsDataEst);
      double r2 = Maths.rSquare(rmsDataOrg, rmsDataEst);
      System.out.printf("k: " + k + " rmsd: %1.5f r2: %1.5f\n", rmsd, r2);

      List<Patch> deltaEDataEst = Patch.Produce.LabPatches(spectraListEst,
          ColorMatchingFunction.CIE_1931_2DEG_XYZ, illuminantSpectra);
      DeltaEReport[] reports = DeltaEReport.Instance.patchReport(deltaEDataOrg,
          deltaEDataEst, false);
      System.out.println(reports[0]);
    }

  }

}
