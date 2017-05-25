package shu.thesis.recover;

import java.text.*;
import java.util.*;

import shu.cms.*;
import shu.cms.dc.*;
import shu.cms.dc.ideal.*;
import shu.cms.devicemodel.dc.*;
import shu.cms.recover.*;
import shu.cms.reference.spectra.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * ����^�k�H�Υ��Ц����Ŷ��ഫ�ǽT��
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class DeltaEComparator {

  public static void main(String[] args) {
    //==========================================================================
    // ��ƪ��ǳ�
    //==========================================================================
    //���и��
    double[][] spectralData = SpectraDatabase.MunsellBook.getGlossyEdition();
//    double[][] spectralData = StandardObjectColourSpectra.getSOCSSpectra();
//    double[][] spectralData = StandardObjectColourSpectra.getSOCSTypicalSpectra();
    System.out.println(spectralData.length);

    //�����]�w
//    Spectra blackbody5000k=CorrelatedColorTemperature.getSpectraOfBlackbodyRadiator(5500);
//    Illuminant illuminant = new Illuminant(blackbody5000k);
    Illuminant illuminant = Illuminant.D65;

    Spectra illuminantSpectra = illuminant.getSpectra().reduce(400, 700,
        10);
    //�����U�C�Ӧ�������Я�q��
    List<Spectra>
        spectraListOrg = Materials.getIlluminantSpectraList(spectralData,
        illuminantSpectra);
    //�নPatch,��K�@��t�p��
    List<Patch> deltaEDataOrg = Patch.Produce.LabPatches(spectraListOrg,
        ColorMatchingFunction.
        CIE_1931_2DEG_XYZ, illuminantSpectra);

    //D50����
    Illuminant D50 = Illuminant.D50;
    Spectra D50Spectra = D50.getSpectra().reduce(400, 700,
                                                 10);
    //D50�����U�C�Ӧ�������Я�q��
    List<Spectra>
        D50spectraList = Materials.getIlluminantSpectraList(spectralData,
        D50Spectra);
    //�নPatch,��K�@��t�p��
    List<Patch> D50deltaEData = Patch.Produce.LabPatches(D50spectraList,
        ColorMatchingFunction.
        CIE_1931_2DEG_XYZ, D50Spectra);
    DeltaEReport.setDecimalFormat(new DecimalFormat("##.###"));
    //==========================================================================

    //==========================================================================
    // �Ʀ�۾��P�����u�ѼƥH�ά۾��Ҧ�
    //==========================================================================
//    IdealDigitalCamera camera = Materials.getSunCamera();
    IdealDigitalCamera camera = IdealDigitalCamera.getSunInstance(Illuminant.
        D50);
    DCTarget target = Materials.getDCTarget(camera, illuminant);
    DCPolynomialRegressionModel model = Materials.get3x3RegressionModel(target);
    //==========================================================================

    //==========================================================================
    // �ǳ�rgb�ƭȥH�θg�L�^�k�p��ӱo�����
    //==========================================================================
    double[][] rgbValues = Materials.getRGBValues(spectraListOrg, camera);
    List<Patch> patchList = Materials.getRGBPatchList(rgbValues);
    List<Patch>
        regressionModelPatchList = model.produceForwardModelPatchList(patchList);

    DeltaEReport[] regressionReports = DeltaEReport.Instance.patchReport(
        deltaEDataOrg,
        regressionModelPatchList, false);
    System.out.println("Regression:");
    System.out.println(Arrays.toString(regressionReports));
    //==========================================================================

    //==========================================================================
    // D50�U���^�k
    //==========================================================================
    double[][] D50rgbValues = Materials.getRGBValues(D50spectraList, camera);
    List<Patch> D50patchList = Materials.getRGBPatchList(D50rgbValues);
    List<Patch>
        D50regressionModelPatchList = model.produceForwardModelPatchList(
            D50patchList);

    DeltaEReport[] D50regressionReports = DeltaEReport.Instance.patchReport(
        D50deltaEData,
        D50regressionModelPatchList, false);
    System.out.println("D50 Regression:");
    System.out.println(Arrays.toString(D50regressionReports));
    //==========================================================================

    //���o�Ʀ�۾���l��Digital Value
    List<Patch> originalOutputPatchList = Materials.
        getOriginalOutputRGBPatchList(rgbValues, camera);

    //==========================================================================
    // PI
    //==========================================================================
    PseudoInverse pi = new PseudoInverse(camera,
                                         SpectraDatabase.Content.MunsellGlossy,
                                         3);
    List<Patch>
        piPatchList = pi.produceModelPatchList(
            originalOutputPatchList,
            illuminantSpectra);
    DeltaEReport[] piReports = DeltaEReport.Instance.patchReport(deltaEDataOrg,
        piPatchList, false);
    System.out.println("PI");
    System.out.println(Arrays.toString(piReports));
    //==========================================================================

    //==========================================================================
    // Wiener
    //==========================================================================
    Wiener wiener = new Wiener(camera, SpectraDatabase.Content.MunsellGlossy, 3);
//    Wiener wiener = new Wiener(camera, MunsellSVD.Type.Glossy, 3,
//                                    Illuminant.D65.getSpectra());

    List<Patch>
        wienerPatchList = wiener.produceModelPatchList(
            originalOutputPatchList,
            illuminantSpectra);
    DeltaEReport[] wienerReports = DeltaEReport.Instance.patchReport(
        deltaEDataOrg,
        wienerPatchList, false);
    System.out.println("Wiener");
    System.out.println(Arrays.toString(wienerReports));
    //==========================================================================
  }
}
