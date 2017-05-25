package shu.thesis.recover;

import java.util.*;

import shu.cms.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;
import shu.cms.devicemodel.dc.*;
import shu.cms.reference.spectra.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 測試不同光源下的迴歸係數,空間轉換的準確性
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class RegressionTester {

  public static void main(String[] args) {

    //光譜資料的準備
//    double[][] spectralData = StandardObjectColourSpectra.
//        getSOCSDifferenceSpectra();
    double[][] spectralData = SpectraDatabase.MunsellBook.getGlossyEdition();
    Illuminant D50 = Illuminant.D65;

    Illuminant A = Illuminant.A;
    Spectra ASpectra = A.getSpectra().reduce(400, 700,
                                             10);

    List<Spectra>
        spectraListA = Materials.getIlluminantSpectraList(spectralData,
        ASpectra);
    List<Patch> illuminantAPatchList = Patch.Produce.LabPatches(spectraListA,
        ColorMatchingFunction.
        CIE_1931_2DEG_XYZ, ASpectra);

    IdealDigitalCamera camera = Materials.getSunCamera();
    DCTarget target = Materials.getDCTarget(camera, D50);
    DCPolynomialRegressionModel model = Materials.getPolynomialRegressionModel(
        target);
//    PolynomialRegressionModel model = Materials.get3x3RegressionModel(target);

    //準備rgb數值以及經過回歸計算而得的色塊
    double[][] rgbValues = Materials.getRGBValues(spectraListA, camera);
    List<Patch> patchList = Materials.getRGBPatchList(rgbValues);
    List<Patch>
        regressionModelPatchList = model.produceForwardModelPatchList(patchList);

    DeltaEReport[] regressionReports = DeltaEReport.Instance.patchReport(
        illuminantAPatchList,
        regressionModelPatchList, false);
    System.out.println(Arrays.toString(regressionReports));
  }
}
