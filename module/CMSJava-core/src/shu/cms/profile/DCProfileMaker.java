package shu.cms.profile;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.devicemodel.dc.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.ciecam02.CIECAM02;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;

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
public class DCProfileMaker
    extends ProfileMaker {

  protected void init(DCModel dcModel) {
    init();

    //==========================================================================
    //�Ѽƪ��ǳ�
    //==========================================================================
    deviceWhite = dcModel.getWhitePatchXYZ();
    deviceNormalizeWhite = (CIEXYZ) deviceWhite.clone();
    deviceNormalizeWhite.normalizeY();
    //==========================================================================

    //==========================================================================
    //���ͦ�A��
    //==========================================================================
    //��A���ҲլO�H relativeDeviceNormalizeWhite���ഫ��¦,�ϥΤW�n�S�O�`�N
    D50chromaticAdaptation = new ChromaticAdaptation(deviceNormalizeWhite,
        D50White, this.catType);
    //==========================================================================
  }

  /**
   * �s�@�Ʀ�۾���Profile
   * @param dcModel DCModel
   * @return Profile
   */
  public Profile makeLabProfile(DCModel dcModel) {
    //==========================================================================
    //�Ѽƪ��ǳ�
    //==========================================================================
    init(dcModel);
    //==========================================================================


    //==========================================================================
    //���͹�Ӫ�
    //==========================================================================
    double[][][] AToB = produceRGBToXYZArray(dcModel, NUMBER_OF_GRID_POINTS, true);
    CIELab.fromXYZValues(AToB[1], D50White.getValues());
    CIELab.rationalize(AToB[1]);
    ColorSpaceConnectedLUT AToB1 = ProfileUtils.produceRGBToLabCSCLUT(dcModel,
        AToB[0], AToB[1], NUMBER_OF_GRID_POINTS);
    //==========================================================================

    //==========================================================================
    // �ͦ�
    //==========================================================================
    String description = dcModel.getModelFactor().getModelFactorFilename() +
        " Lab-" + this.catType.name();
    return ProfileUtils.produceDCProfile(Profile.PCSType.Lab, D50White,
                                         D50chromaticAdaptation.
                                         getAdaptationMatrixToDestination(),
                                         description,
                                         AToB1, AToB1, null);
    //==========================================================================
  }

  /**
   * �H�ץ�����CIECAM�i��JabLab Profile���s�@
   * (ps:�X�G�L�t��)
   * @param dcModel DCModel
   * @param vc ViewingConditions
   * @return Profile
   */
  public Profile makeJabLabModifiedProfile(DCModel dcModel,
                                           ViewingConditions vc) {
    CIECAM02.setModifyState(true);
    Profile p = makeJabLabProfile(dcModel, vc);
    CIECAM02.setModifyState(false);
    return p;
  }

  public Profile makeJabLabProfile(DCModel dcModel, ViewingConditions vc) {
    //==========================================================================
    //�Ѽƪ��ǳ�
    //==========================================================================
    init(dcModel);
    //==========================================================================


    //==========================================================================
    //���͹�Ӫ�
    //==========================================================================
    double[][][] AToB = produceRGBToXYZArray(dcModel, NUMBER_OF_GRID_POINTS, false);
    //��Jab
    report.errorXYZ2JabCount = ProfileUtils.XYZToJabArray(AToB[1], vc);
    //�X�z��
    CIELab.rationalize(AToB[1]);
    //�t���C�⪺�ץ�
    report.errorDarkLabValuesCount1 = ProfileUtils.fixDarkLabValue(AToB[1]);
    //��XYZ
    report.errorJab2XYZCount = ProfileUtils.JabToXYZArray(AToB[1],
        referenceMediumViewingConditions);
    //�X�z��2
    CIEXYZ.rationalize(AToB[1]);
    //��LAb
    CIELab.fromXYZValues(AToB[1], D50White.getValues());
    //�X�z��3
    CIELab.rationalize(AToB[1]);
    //�t���C�⪺�ץ�
    report.errorDarkLabValuesCount2 = ProfileUtils.fixDarkLabValue(AToB[1]);

    ColorSpaceConnectedLUT AToB1 = ProfileUtils.produceRGBToLabCSCLUT(dcModel,
        AToB[0], AToB[1], NUMBER_OF_GRID_POINTS);
    //==========================================================================

    //==========================================================================
    // �ͦ�
    //==========================================================================
    String description = dcModel.getModelFactor().getModelFactorFilename() +
        " JabLab" + (CIECAM02.isModifyState() ? "Modified-" : "-") +
        referenceMediumViewingConditions.description;
    return ProfileUtils.produceDCProfile(Profile.PCSType.Lab, D50White,
                                         D50chromaticAdaptation.
                                         getAdaptationMatrixToDestination(),
                                         description, AToB1, AToB1, null);
    //==========================================================================
  }

  public Profile makeJabProfile(DCModel dcModel, ViewingConditions vc) {
    //==========================================================================
    //�Ѽƪ��ǳ�
    //==========================================================================
    init(dcModel);
    //==========================================================================


    //==========================================================================
    //���͹�Ӫ�
    //==========================================================================
    double[][][] AToB = produceRGBToXYZArray(dcModel, NUMBER_OF_GRID_POINTS, false);
    report.errorXYZ2JabCount = ProfileUtils.XYZToJabArray(AToB[1], vc);
    CIELab.rationalize(AToB[1]);
    report.errorDarkLabValuesCount1 = ProfileUtils.fixDarkLabValue(AToB[1]);
    ColorSpaceConnectedLUT AToB1 = ProfileUtils.produceRGBToLabCSCLUT(dcModel,
        AToB[0], AToB[1], NUMBER_OF_GRID_POINTS);
    //==========================================================================

    //==========================================================================
    // �ͦ�
    //==========================================================================
    String description = dcModel.getModelFactor().getModelFactorFilename() +
        " Jab" + (CIECAM02.isModifyState() ? "Modified-" : "-") +
        referenceMediumViewingConditions.description;
    return ProfileUtils.produceDCProfile(Profile.PCSType.Lab, D50White,
                                         D50chromaticAdaptation.
                                         getAdaptationMatrixToDestination(),
                                         description,
                                         AToB1, AToB1, null);
    //==========================================================================
  }

  protected double[][][] produceRGBToXYZArray(DCModel model,
                                              int grid, boolean CATToD50) {

    //==========================================================================
    // ���n���ƾڥ��ǳƦn
    //==========================================================================
    double[][][] inputAndOutput = new double[2][][];
    double step = 255. / (grid - 1);
    int size = (int) Math.pow(grid, 3);
    double[][] input = new double[size][3];
    int index = 0;
    double[][] output = new double[size][];
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, new double[] {0, 0, 0},
                      RGB.MaxValue.Double1);

    //==========================================================================
    // ���N���ͩҦ���RGB��M�쪺XYZ
    //==========================================================================
    for (double r = 0; r <= 255.; r += step) {
      for (double g = 0; g <= 255.; g += step) {
        for (double b = 0; b <= 255.; b += step) {
          input[index] = new double[] {
              r / 255, g / 255, b / 255};
          rgb.setValues(input[index]);
          CIEXYZ XYZ = model.getXYZ(rgb, false);
          //���W��
          XYZ.normalize(deviceNormalizeWhite);
          output[index] = XYZ.getValues();
          index++;
        }
      }
    }
    //==========================================================================

    //==========================================================================
    // �ѩ󲣥ͥX�Ӫ�XYZ�B��relativeDeviceNormalizeWhite,�ҥH�o���PCS(D50)�åB�X�z��
    //==========================================================================
    if (CATToD50) {
      output = D50chromaticAdaptation.adaptationToDestination(output);
    }
    //==========================================================================
    CIEXYZ.rationalize(output);

    inputAndOutput[0] = input;
    inputAndOutput[1] = output;

    return inputAndOutput;
  }

  protected double[][][] produceRGBToD50XYZArray(DCModel model,
                                                 int grid) {

    //==========================================================================
    // ���n���ƾڥ��ǳƦn
    //==========================================================================
    double[][][] inputAndOutput = new double[2][][];
    double step = 255. / (grid - 1);
    int size = (int) Math.pow(grid, 3);
    double[][] input = new double[size][3];
    int index = 0;
    double[][] output = new double[size][];
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, new double[] {0, 0, 0},
                      RGB.MaxValue.Double1);

    //==========================================================================
    // ���N���ͩҦ���RGB��M�쪺XYZ
    //==========================================================================
    for (double r = 0; r <= 255.; r += step) {
      for (double g = 0; g <= 255.; g += step) {
        for (double b = 0; b <= 255.; b += step) {
          input[index] = new double[] {
              r / 255, g / 255, b / 255};
          rgb.setValues(input[index]);
          CIEXYZ XYZ = model.getXYZ(rgb, false);
          //���W��
          XYZ.normalize(deviceNormalizeWhite);
          output[index] = XYZ.getValues();
          index++;
        }
      }
    }
    //==========================================================================

    //==========================================================================
    // �ѩ󲣥ͥX�Ӫ�XYZ�B��relativeDeviceNormalizeWhite,�ҥH�o���PCS(D50)�åB�X�z��
    //==========================================================================
    output = D50chromaticAdaptation.adaptationToDestination(output);
    CIEXYZ.rationalize(output);
    //==========================================================================

    inputAndOutput[0] = input;
    inputAndOutput[1] = output;

    return inputAndOutput;
  }

  public static void main(String[] args) {

    //==========================================================================
    // ���I/���ҳ]�w
    //==========================================================================
    CIEXYZ whiteXYZ = LightSource.getIlluminant(LightSource.i1Pro.D50).
        getNormalizeXYZ();
//    CIEXYZ whiteXYZ = LightSource.getIlluminant(LightSource.i1Pro.F12).
//        getNormalizeXYZ();
    whiteXYZ.times(100.);

    ViewingConditions vc = new ViewingConditions(whiteXYZ,
                                                 .11, 20,
                                                 Surround.
                                                 Dim, "D50");
//    ViewingConditions vc = new ViewingConditions(whiteXYZ,
//                                                 .07, 20,
//                                                 ViewingConditions.Surround.
//                                                 Dim, "F12");
    //==========================================================================

//    String filename = "factor/D200Raw_F12 CCSG_PolyBy32_20070605.factor";
    String filename = "factor/D200Raw_D65 CCSG_PolyBy20_20070626.factor";
    DCModel model = new DCPolynomialRegressionModel(filename);
    CAMConst.CATType catType = CAMConst.CATType.vonKries;

    DCProfileMaker profileMaker = new DCProfileMaker();
    profileMaker.setCATType(catType);
//    Profile p = profileMaker.makeLabProfile(model);
//    profileMaker.setReferenceMediumViewingConditions(ProfileUtils.
//        DimViewingConditions);
//    Profile p = profileMaker.makeJabLabProfile(model, vc);
    Profile p = profileMaker.makeJabProfile(model, vc);

    String profileFilename = "Profile/Camera/" + SoftwareName +
        "_" + p.profileDescription + ".icc";
    System.out.println(profileFilename);
    iccessAdapter.storeICCProfileLutByLut16(p, profileFilename);

//    System.out.println("errJab2XYZ: " + profileMaker.getErrorJab2XYZCount());
//    System.out.println("errXYZ2Jab: " + profileMaker.getErrorXYZ2JabCount());
//    System.out.println("errDarkLabValuesCount1: " +
//                       profileMaker.getErrorDarkLabValuesCount1());
//    System.out.println("errDarkLabValuesCount2: " +
//                       profileMaker.getErrorDarkLabValuesCount2());
  }
}
