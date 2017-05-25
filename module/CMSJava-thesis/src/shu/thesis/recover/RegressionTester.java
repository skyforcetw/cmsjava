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
 * ���դ��P�����U���j�k�Y��,�Ŷ��ഫ���ǽT��
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

    //���и�ƪ��ǳ�
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

    //�ǳ�rgb�ƭȥH�θg�L�^�k�p��ӱo�����
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
