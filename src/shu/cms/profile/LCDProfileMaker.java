package shu.cms.profile;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.gma.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.ciecam02.CIECAM02;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.math.*;
import shu.math.array.DoubleArray;

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
public class LCDProfileMaker
    extends ProfileMaker {
  /**
   * D50White���W�Y��0.5
   */
  protected CIEXYZ LUTPCSWhite;
  protected final static double NORMALIZE_Y = 32768. / 65535.;

  public static enum LCDProfileType {
    Lut, Matrix, MatrixLut
  }

  /**
   * �۹�]�ƥ�,�flare��white
   */
  protected CIEXYZ relativeDeviceWhite;
  /**
   * relativeDeviceWhite�g���W�ƳB�z
   */
  protected CIEXYZ relativeDeviceNormalizeWhite;

  public LCDProfileMaker() {
    LUTPCSWhite = (CIEXYZ) D50White.clone();
    LUTPCSWhite.scaleY(NORMALIZE_Y);
  }

  protected void init(LCDModel lcdModel) {
    init();

    //==========================================================================
    //�Ѽƪ��ǳ�
    //==========================================================================
    lcdModel.correct.setDoGammaCorrect(true);
    deviceWhite = lcdModel.getModelWhite();
    relativeDeviceWhite = new CIEXYZ(DoubleArray.minus(deviceWhite.getValues(),
        lcdModel.flare.getFlare().getValues()));
    relativeDeviceNormalizeWhite = (CIEXYZ) relativeDeviceWhite.clone();
    relativeDeviceNormalizeWhite.normalizeY();
    deviceNormalizeWhite = (CIEXYZ) deviceWhite.clone();
    deviceNormalizeWhite.normalizeY();
    //==========================================================================

    //==========================================================================
    //���ͦ�A��
    //==========================================================================
    //��A���ҲլO�H relativeDeviceNormalizeWhite���ഫ��¦,�ϥΤW�n�S�O�`�N
    D50chromaticAdaptation = new ChromaticAdaptation(
        relativeDeviceNormalizeWhite,
        D50White, this.catType);
    //==========================================================================
  }

  /**
   * �s�@�HLab��PCS��Profile
   * @param lcdModel LCDModel
   * @return Profile
   */
  public Profile makeLabProfile(LCDModel lcdModel) {
    //==========================================================================
    //�Ѽƪ��ǳ�
    //==========================================================================
    init(lcdModel);
    //==========================================================================

    //==========================================================================
    //���͹�Ӫ�
    //==========================================================================

    //==========================================================================
    // AToB
    //==========================================================================
    //PCS�U��RGB->XYZ
    double[][][] AToB = produceRGBToXYZArray(lcdModel, NUMBER_OF_GRID_POINTS, true);
    //����CSCLUT��BToA��
    ColorSpaceConnectedLUT AToB1XYZ = ProfileUtils.produceRGBToXYZCSCLUT(
        lcdModel, AToB[0], AToB[1], NUMBER_OF_GRID_POINTS);

    //�NRGB->XYZ�ܦ� RGB->Lab
    CIELab.fromXYZValues(AToB[1], D50White.getValues());
    //�X�z��
    report._LabRationalCount += CIELab.rationalize(AToB[1]);
    //����AToB��CSCLUT
    ColorSpaceConnectedLUT AToB1 = ProfileUtils.produceRGBToLabCSCLUT(lcdModel,
        AToB[0], AToB[1], NUMBER_OF_GRID_POINTS);
    //==========================================================================

    //==========================================================================
    // BToA
    //==========================================================================
    ColorSpaceConnectedLUT BToA1 = null;
    BToA1 = produceLabToRGB1ByLut(lcdModel, AToB1XYZ, GMA2ClippingType.RGB);
    //==========================================================================

    //==========================================================================

    //==========================================================================
    // �ͦ�
    //==========================================================================
    CIEXYZ luminance = lcdModel.getLuminance();
    String description = lcdModel.getModelFactor().getModelFactorFilename() +
        " Lab-" + this.catType.name();
    return ProfileUtils.produceLCDProfile(Profile.PCSType.Lab,
                                          deviceNormalizeWhite,
                                          D50chromaticAdaptation.
                                          getAdaptationMatrixToDestination(),
                                          description,
                                          AToB1, AToB1, null, BToA1, BToA1, null,
                                          luminance);
    //==========================================================================
  }

  public Profile makeJabLabModifiedProfile(LCDModel lcdModel,
                                           ViewingConditions vc) {
    CIECAM02.setModifyState(true);
    Profile p = makeJabLabProfile(lcdModel, vc);
    CIECAM02.setModifyState(false);
    return p;
  }

  /**
   * ���ͥHPCS(Lab)��Ӫ�,���O�����α��ĥ�Jab
   * A2B: RGB -> XYZ -vc1-> Jab -vc2-> XYZ -> Lab
   *         (DC)   (�ù�vc)    (�Ѧ�vc)     (���ID50)
   *
   * B2A: Lab -> XYZ -vc2-> Jab -vc1> XYZ -> RGB
   *       (���ID50) (�Ѧ�vc)    (�ù�vc)  (DC)
   * @param lcdModel LCDModel
   * @param vc ViewingConditions
   * @return Profile
   */
  public Profile makeJabLabProfile(LCDModel lcdModel, ViewingConditions vc) {
    //==========================================================================
    //�Ѽƪ��ǳ�
    //==========================================================================
    init(lcdModel);
    //==========================================================================

    //==========================================================================
    //���͹�Ӫ�
    //==========================================================================

    //==========================================================================
    // AToB
    //==========================================================================
    //�]�ƪŶ��U��XYZ
    double[][][] AToB = produceRGBToXYZArray(lcdModel, NUMBER_OF_GRID_POINTS, false);
    //�]�ƤU��XYZ
    ColorSpaceConnectedLUT AToB1XYZ = ProfileUtils.produceRGBToXYZCSCLUT(
        lcdModel, AToB[0], AToB[1], NUMBER_OF_GRID_POINTS);

    report.errorXYZ2JabCount = ProfileUtils.XYZToJabArray(AToB[1], vc);
    report._LabRationalCount += CIELab.rationalize(AToB[1]);
    report.errorDarkLabValuesCount1 = ProfileUtils.fixDarkLabValue(AToB[1]);
    report.errorJab2XYZCount = ProfileUtils.JabToXYZArray(AToB[1],
        ViewingConditions.PerceptualIntentViewingConditions);
    report.XYZRationalCount += CIEXYZ.rationalize(AToB[1]);
    CIELab.fromXYZValues(AToB[1], D50White.getValues());
    report._LabRationalCount += CIELab.rationalize(AToB[1]);
    report.errorDarkLabValuesCount2 = ProfileUtils.fixDarkLabValue(AToB[1]);

    ColorSpaceConnectedLUT AToB1 = ProfileUtils.produceRGBToLabCSCLUT(lcdModel,
        AToB[0], AToB[1], NUMBER_OF_GRID_POINTS);
    //==========================================================================

    //==========================================================================
    // BToA
    //==========================================================================
    // �b��~���Ŷ���gma, xyz->Jab
    ColorSpaceConnectedLUT BToA1 = produceJabToRGB1ByLut(lcdModel, AToB1XYZ,
        GMA2ClippingType.RGB, vc);
    //==========================================================================

    //==========================================================================

    //==========================================================================
    // �ͦ�
    //==========================================================================
    CIEXYZ luminance = lcdModel.getLuminance();
    String description = lcdModel.getModelFactor().getModelFactorFilename() +
        " JabLab" + (CIECAM02.isModifyState() ? "Modified-" : "-") +
        referenceMediumViewingConditions.description;

    return ProfileUtils.produceLCDProfile(Profile.PCSType.Lab,
                                          deviceNormalizeWhite,
                                          D50chromaticAdaptation.
                                          getAdaptationMatrixToDestination(),
                                          description,
                                          AToB1, AToB1, null, BToA1, BToA1, null,
                                          luminance);
    //==========================================================================
  }

  /**
   *
   * @param lcdModel LCDModel
   * @param vc ViewingConditions
   * @return Profile
   * @todo M icc �����D�O�y�{�����D,�٬O�ڥ����A�X�Φ�~��Jab��Lab
   */
  public Profile makeJabProfile(LCDModel lcdModel, ViewingConditions vc) {
    //==========================================================================
    //�Ѽƪ��ǳ�
    //==========================================================================
    init(lcdModel);
    //==========================================================================

    //==========================================================================
    //���͹�Ӫ�
    //==========================================================================

    //==========================================================================
    // AToB
    //==========================================================================
    //�]�ƪŶ��U��XYZ
    double[][][] AToB = produceRGBToXYZArray(lcdModel, NUMBER_OF_GRID_POINTS, false);
    //�]�ƤU��XYZ
    ColorSpaceConnectedLUT AToB1XYZ = ProfileUtils.produceRGBToXYZCSCLUT(
        lcdModel, AToB[0], AToB[1], NUMBER_OF_GRID_POINTS);

    report.errorXYZ2JabCount = ProfileUtils.XYZToJabArray(AToB[1], vc);
    report._LabRationalCount += CIELab.rationalize(AToB[1]);
    report.errorDarkLabValuesCount1 = ProfileUtils.fixDarkLabValue(AToB[1]);
    ColorSpaceConnectedLUT AToB1 = ProfileUtils.produceRGBToLabCSCLUT(lcdModel,
        AToB[0], AToB[1], NUMBER_OF_GRID_POINTS);
    //==========================================================================

    //==========================================================================
    // BToA
    //==========================================================================
    // �b��~���Ŷ���gma, xyz->Jab
    ColorSpaceConnectedLUT BToA1 = produceJabToRGB1ByLut(lcdModel, AToB1XYZ,
        GMA2ClippingType.RGB, vc);
    //==========================================================================

    //==========================================================================

    //==========================================================================
    // �ͦ�
    //==========================================================================
    CIEXYZ luminance = lcdModel.getLuminance();
    String description = lcdModel.getModelFactor().getModelFactorFilename() +
        " Jab-" + referenceMediumViewingConditions.description;
    return ProfileUtils.produceLCDProfile(Profile.PCSType.Lab,
                                          deviceNormalizeWhite,
                                          D50chromaticAdaptation.
                                          getAdaptationMatrixToDestination(),
                                          description,
                                          AToB1, AToB1, null, BToA1, BToA1, null,
                                          luminance);
    //==========================================================================
  }

  /**
   * �s�@�HXYZ��PCS��Profile
   * @param lcdModel LCDModel
   * @param type LCDProfileType
   * @return Profile
   */
  public Profile makeXYZProfile(LCDModel lcdModel, LCDProfileType type) {

    //==========================================================================
    //�Ѽƪ��ǳ�
    //==========================================================================
    init(lcdModel);
    //==========================================================================

    //==========================================================================
    //���͹�Ӫ�
    //==========================================================================
    double[][][] AToB = produceRGBToXYZArray(lcdModel, NUMBER_OF_GRID_POINTS, true);
    //�N�̤j���W�ƨ�0.5
    AToB[1] = CIEXYZ.times(AToB[1], D50White.getValues(), NORMALIZE_Y);

    ColorSpaceConnectedLUT AToB1 = ProfileUtils.produceRGBToXYZCSCLUT(lcdModel,
        AToB[0], AToB[1],
        NUMBER_OF_GRID_POINTS);
    ColorSpaceConnectedLUT BToA1 = null;

    switch (type) {
      case Matrix:
        BToA1 = produceXYZToRGB1ByMatrix(lcdModel,
                                         AToB1);
        break;
      case Lut:

        /**
         * @todo M icc Lut�Ҧ������D
         */
        BToA1 = produceXYZToRGB1ByLut(lcdModel, AToB1, GMA2ClippingType.RGB);
        break;
      case MatrixLut:
        BToA1 = produceXYZToRGB1ByMatrixLut(lcdModel,
                                            AToB1, GMA2ClippingType.RGB);
        break;
      default:
        throw new UnsupportedOperationException(type.name());

    }
    //==========================================================================

    //==========================================================================
    // �ͦ�
    //==========================================================================
    CIEXYZ luminance = lcdModel.getLuminance();
    String description = lcdModel.getModelFactor().getModelFactorFilename() +
        " XYZ" + type.name();
    return ProfileUtils.produceLCDProfile(Profile.PCSType.XYZ,
                                          deviceNormalizeWhite,
                                          D50chromaticAdaptation.
                                          getAdaptationMatrixToDestination(),
                                          description,
                                          AToB1, AToB1, null, BToA1, BToA1, null,
                                          luminance);
    //==========================================================================
  }

  /**
   * �ND50��XYZ�ഫ��device�U��XYZ
   * @param lutPCSXYZ double[][]
   * @param lutPCSWhite CIEXYZ
   */
  protected void PCSXYZToRelativeDeviceXYZ(double[][] lutPCSXYZ,
                                           CIEXYZ lutPCSWhite) {
    //�q���Y��A2B white(0.5) �٭즨���`��D50 white
    lutPCSXYZ = CIEXYZ.times(lutPCSXYZ, lutPCSWhite.getValues(),
                             D50White.Y);
    //�qD50��^��device
    lutPCSXYZ = D50chromaticAdaptation.adaptationFromDestination(lutPCSXYZ);
    report.XYZRationalCount += CIEXYZ.rationalize(lutPCSXYZ);
    lutPCSXYZ = CIEXYZ.times(lutPCSXYZ,
                             relativeDeviceNormalizeWhite.getValues(),
                             relativeDeviceWhite.Y);
  }

  /**
   * ����Lab2RGB����Ӫ�
   * 1.����D50Lab
   * 2.����D50������
   * 3.�i�������
   * 4.���͹�Ӫ�
   * @param lcdModel LCDModel
   * @param AToB1XYZ ColorSpaceConnectedLUT �p������ɥΪ�A2B
   * @param clipType GMA2ClippingType �i��ĤG��GMA�ɩұĥΪ��覡
   * @return ColorSpaceConnectedLUT
   */
  protected ColorSpaceConnectedLUT produceLabToRGB1ByLut(LCDModel
      lcdModel, ColorSpaceConnectedLUT AToB1XYZ, GMA2ClippingType clipType) {
    int grid = AToB1XYZ.getNumberOfGridPoints();
    //==========================================================================
    // ����D50��Lab
    //==========================================================================
    double[][] D50input = ColorSpaceConnectedLUT.produceInputLabCLUT(grid);
    double[][] inputTables = null;
    if (DO_LAB_COORDINATES_OFFSET) {
      abCoordinatesOffset(D50input, 0.5);

      //========================================================================
      // �ݭn�z�Lab��1D Lut�Ӱ��y�Ъ�����
      //========================================================================
      inputTables = new double[3][];
      inputTables[0] = new double[grid];
      inputTables[1] = new double[grid];
      inputTables[2] = new double[grid];
      for (int x = 0; x < grid; x++) {
        inputTables[0][x] = D50input[x * grid * grid][0];
        inputTables[1][x] = D50input[x][2];
        inputTables[2][x] = D50input[x][2];
      }
      //========================================================================
    }
    //==========================================================================

    //==========================================================================
    // �����ɥΦ�m�Ŷ�(�]���O��A2B,�ҥH�O�bD50�Ŷ��U)
    //==========================================================================
    ProfileColorSpace boundaryColorSpace = null;
    if (lcdModel instanceof ProfileColorSpaceModel) {
//      boundaryColorSpace = ProfileColorSpace.Instance.get( ( (
//          RGBColorSpaceModel)
//          lcdModel).getRGBColorSpace());

      boundaryColorSpace = ( (ProfileColorSpaceModel) lcdModel).
          getProfileColorSpace();
//      boundaryColorSpace = ProfileColorSpace.getInstance(lcdModel);
    }
    else {
      boundaryColorSpace = getBoundaryColorSpace(AToB1XYZ,
                                                 Polynomial.COEF_3.BY_19C);
    }
    //==========================================================================

    //==========================================================================
    // �i�����M
    //==========================================================================
    //D50��Lab
    double[][] relativeDeviceInput = DoubleArray.copy(D50input);
    //�HD50(PCS)�i��GMA
    chromaClippingGamutMappint(relativeDeviceInput,
                               boundaryColorSpace, FocalPoint.FocalType.None);
    //==========================================================================

    CIELab.toXYZValues(relativeDeviceInput,
                       D50White.getValues());
    //�qPCS���device�U
    PCSXYZToRelativeDeviceXYZ(relativeDeviceInput, D50White);

    double[][] output = produceB2AOutput(lcdModel, relativeDeviceInput,
                                         clipType);

    ColorSpaceConnectedLUT csclut = ProfileUtils.produceLabToRGBCSCLUT(lcdModel,
        D50input, output, grid, inputTables);
    return csclut;
  }

  protected ColorSpaceConnectedLUT produceJabToRGB1ByLut(LCDModel
      lcdModel, ColorSpaceConnectedLUT AToB1DeviceXYZ,
      GMA2ClippingType clipType,
      ViewingConditions vc) {
    int grid = AToB1DeviceXYZ.getNumberOfGridPoints();
    //==========================================================================
    // ���ͦ�~����Jab
    //==========================================================================
    double[][] LabInput = ColorSpaceConnectedLUT.produceInputLabCLUT(grid);
    //==========================================================================

    //==========================================================================
    // �����ɥΦ�m�Ŷ�(DeviceXYZ)
    //==========================================================================
    ProfileColorSpace boundaryColorSpace = getBoundaryColorSpace(AToB1DeviceXYZ,
        Polynomial.COEF_3.BY_19C);
    //==========================================================================

    //==========================================================================
    // �i�����M
    //==========================================================================
    //PCS Lab
    double[][] relativeDeviceInput = DoubleArray.copy(LabInput);
    //Lab����XYZ(PCS)
    CIELab.toXYZValues(relativeDeviceInput, D50White.getValues());
    report.XYZRationalCount += CIEXYZ.rationalize(relativeDeviceInput);
    //(PCS XYZ���Jab�U)
    ProfileUtils.XYZToJabArray(relativeDeviceInput,
                               referenceMediumViewingConditions);
    report._LabRationalCount += CIELab.rationalize(relativeDeviceInput);

    /**
     * @todo M gma ��~����GMA
     */
    //�ثe�O�L�@�ʪ�
    //�Q��Jab�i��gma
    chromaClippingGamutMappint(relativeDeviceInput,
                               boundaryColorSpace, FocalPoint.FocalType.None);

    //�NJab�নXYZ
    ProfileUtils.JabToXYZArray(relativeDeviceInput, vc);
    report.XYZRationalCount += CIEXYZ.rationalize(relativeDeviceInput);
    //==========================================================================

    double[][] output = produceB2AOutput(lcdModel, relativeDeviceInput,
                                         clipType);

    ColorSpaceConnectedLUT csclut = ProfileUtils.produceLabToRGBCSCLUT(lcdModel,
        LabInput, output, grid);
    return csclut;
  }

  /**
   * �Q��AToB1���ƭ�,�i��^�k���omodel,�@�������ɭp��ɨϥ�
   * @param AToB1 ColorSpaceConnectedLUT
   * @param coefs COEF_3
   * @return ProfileColorSpace
   */
  protected final static ProfileColorSpace getBoundaryColorSpace(
      ColorSpaceConnectedLUT AToB1, Polynomial.COEF_3 coefs) {

//    RegressionReverseModel reverseModel = new RegressionReverseModel(AToB1,
//        coefs, NORMALIZE_Y);
    CLUTRegressionReverseModel reverseModel = new CLUTRegressionReverseModel(
        AToB1,
        coefs, 1);
    reverseModel.produceFactor();
    ProfileColorSpace boundaryColorSpace = ProfileColorSpace.Instance.get(
        reverseModel, "");
    return boundaryColorSpace;
  }

  /**
   * ����inverseModel����B2A
   * @param lcdModel LCDModel
   * @param AToB1 ColorSpaceConnectedLUT
   * @param clipType ClippingType
   * @return ColorSpaceConnectedLUT
   */
  protected ColorSpaceConnectedLUT produceXYZToRGB1ByLut(LCDModel
      lcdModel, ColorSpaceConnectedLUT AToB1, GMA2ClippingType clipType) {
    int grid = AToB1.getNumberOfGridPoints();
    //==========================================================================
    // ����D50��XYZ
    //==========================================================================
    double[][] D50input = ColorSpaceConnectedLUT.produceInputXYZCLUT(grid);
    //==========================================================================

    //==========================================================================
    // �����ɥΦ�m�Ŷ�(�]���O��A2B,�ҥH�O�bD50�Ŷ��U)
    //==========================================================================
    ProfileColorSpace boundaryColorSpace = getBoundaryColorSpace(AToB1,
        Polynomial.COEF_3.BY_19C);
    //==========================================================================

    //==========================================================================
    // �i�����M
    //==========================================================================
    //D50��XYZ
    double[][] relativeDeviceInput = DoubleArray.copy(D50input);
    //�HD50(PCS)�i��GMA
    chromaClippingGamutMappint(relativeDeviceInput,
                               boundaryColorSpace,
                               FocalPoint.FocalType.MultiByKMeans, LUTPCSWhite);
    //�qPCS���device�U
    PCSXYZToRelativeDeviceXYZ(relativeDeviceInput, LUTPCSWhite);
    //==========================================================================

    double[][] output = produceB2AOutput(lcdModel, relativeDeviceInput,
                                         clipType);

    ColorSpaceConnectedLUT csclut = ProfileUtils.produceXYZToRGBCSCLUT(lcdModel,
        D50input, output, grid);

    return csclut;
  }

  /**
   * �ĥ�matrix+lut���覡�i��XYZ2RGB��Ӫ��B��.
   * ���NXYZ�z�Lmatrix���@RGB�Ŷ�,�R���Q�ΩҦ�����Ӫ�Ŷ�.
   * �A�NRGB����^��XYZ,�H��XYZ�i�����M
   * @param lcdModel LCDModel
   * @param AToB1 ColorSpaceConnectedLUT
   * @param clipType ClippingType
   * @return ColorSpaceConnectedLUT
   */
  protected ColorSpaceConnectedLUT produceXYZToRGB1ByMatrixLut(LCDModel
      lcdModel, ColorSpaceConnectedLUT AToB1, GMA2ClippingType clipType) {

    //==========================================================================
    // �Ѧ�RGB�Ŷ�
    //==========================================================================
    RGB.ColorSpace rgbColorSpace = RGB.ColorSpace.WideGamutRGB;
    CIEXYZ RGBRefWhite = rgbColorSpace.referenceWhite.getNormalizeXYZ();
    //==========================================================================

    //==========================================================================
    // ����RGB��XYZ
    //==========================================================================
    int grid = AToB1.getNumberOfGridPoints();
    double[][] RGBInput = ColorSpaceConnectedLUT.produceInputXYZCLUT(grid);
    int size = RGBInput.length;
    //==========================================================================

    //==========================================================================
    // �����ɥΦ�m�Ŷ�(�]���O��A2B,�ҥH�O�bD50�Ŷ��U)
    //==========================================================================
    ProfileColorSpace boundaryColorSpace = getBoundaryColorSpace(AToB1,
        Polynomial.COEF_3.BY_19C);
    //==========================================================================

    //==========================================================================
    // �i�����M
    //==========================================================================
    //D50��XYZ
    double[][] relativeDeviceInput = new double[size][];
    for (int x = 0; x < size; x++) {
      double[] rgbValues = RGBInput[x];
      relativeDeviceInput[x] = RGB.linearToXYZValues(rgbValues, rgbColorSpace);
    }
    //�HD50(PCS)�i��GMA
    chromaClippingGamutMappint(relativeDeviceInput,
                               boundaryColorSpace,
                               FocalPoint.FocalType.MultiByKMeans, RGBRefWhite);
    //�qPCS���device�U
    PCSXYZToRelativeDeviceXYZ(relativeDeviceInput, RGBRefWhite);
    //==========================================================================

    double[][] output = produceB2AOutput(lcdModel, relativeDeviceInput,
                                         clipType);

    ColorSpaceConnectedLUT csclut = ProfileUtils.produceXYZToRGBCSCLUT(lcdModel,
        RGBInput, output, grid);
    csclut.matrix = DoubleArray.times(DoubleArray.transpose(rgbColorSpace.
        toRGBMatrix), 1. / NORMALIZE_Y);

    return csclut;
  }

  /**
   * �̷�relativeDeviceInput�����ƭ�,
   * �glcdModel�ഫ��ұo��RGB,���oB2A����X��.
   * �p�GB2A����X�Ȧ����X�z�B,�̷�clipType����k�i��clip
   * @param lcdModel LCDModel
   * @param relativeDeviceInputXYZ double[][]
   * @param clipType ClippingType
   * @return double[][]
   */
  protected double[][] produceB2AOutput(LCDModel lcdModel,
                                        double[][] relativeDeviceInputXYZ,
                                        GMA2ClippingType clipType) {
    //==========================================================================
    // �Ѽƪ��ǳ�
    //==========================================================================
    int size = relativeDeviceInputXYZ.length;
//    double[] deviceFlare = lcdModel.getFlare().getValues();
    double[][] output = new double[size][3];
    double[] rgbValues;
    CIEXYZ XYZ = new CIEXYZ();
//    double[] deviceWhiteValues = deviceWhite.getValues();
    int gma2Count = 0;
    //==========================================================================

    //==========================================================================
    // ����B2A��Ӫ�
    //==========================================================================
    /*for (int x = 0; x < size; x++) {
      //���[�Wflare
      double[] XYZValues = DoubleArray.plus(relativeDeviceInputXYZ[x],
                                            deviceFlare);
      XYZ.setValues(XYZValues);
      //�A��LCDModel�ϱ�RGB
      RGB rgb = lcdModel.getRGB(XYZ, false);

      //�p�Grgb���s�b�Ϊ̤��X�z
      if (DO_GAMUT_MAPPING2 && (rgb == null || !rgb.isLegal())) {
        gma2Count++;
        if (clipType == GMA2ClippingType.RGB) {
          //�ĥ�RGB clipping���覡
          rgb.rationalize();
        }
        else if (clipType == GMA2ClippingType.LCh) {
          throw new IllegalStateException(
              "no recommend using GMA2ClippingType.LCh");
        }
      }

      rgbValues = rgb.getValues();
      System.arraycopy(rgbValues, 0, output[x], 0, 3);
         }*/

    for (int x = 0; x < size; x++) {
      double[] XYZValues = relativeDeviceInputXYZ[x];

      XYZ.setValues(XYZValues);
      //�A��LCDModel�ϱ�RGB
      RGB rgb = lcdModel.getRGB(XYZ, true);

      //�p�Grgb���s�b�Ϊ̤��X�z
      if (DO_GAMUT_MAPPING2 && (rgb == null || !rgb.isLegal())) {
        gma2Count++;
        if (clipType == GMA2ClippingType.RGB) {
          //�ĥ�RGB clipping���覡
          rgb.rationalize();
        }
        else if (clipType == GMA2ClippingType.LCh) {
          throw new IllegalStateException(
              "no recommend using GMA2ClippingType.LCh");
        }
      }

      rgbValues = rgb.getValues();
      System.arraycopy(rgbValues, 0, output[x], 0, 3);
    }
    //==========================================================================
    report.gma2ProcessCount = gma2Count;

    return output;
  }

  /**
   * ����RGB��grid,�p��X��M���]��XYZ.��XYZ�w�g��hflare.
   * �A�N���]�ƪ�XYZ�q�]�Ʀ�����D50���(PCS)
   * @param model LCDModel
   * @param grid int
   * @return double[][][]
   * @deprecated
   */
  protected double[][][] produceRGBToD50XYZArray(LCDModel model,
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
    double[] flareValues = model.flare.getFlare().getValues();
    double[] XYZValues = new double[3];

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
          XYZ.getValues(XYZValues);
          //�h��flare
          double[] relativeXYZValues = DoubleArray.minus(XYZValues, flareValues);
          XYZ.setValues(relativeXYZValues);
          //���W��
          XYZ.normalize(relativeDeviceWhite);
          XYZ.getValues(relativeXYZValues);
          output[index] = relativeXYZValues;
          index++;
        }
      }
    }
    //==========================================================================

    //==========================================================================
    // �ѩ󲣥ͥX�Ӫ�XYZ�B��relativeDeviceNormalizeWhite,�ҥH�o���PCS(D50)�åB�X�z��
    //==========================================================================
    output = D50chromaticAdaptation.adaptationToDestination(output);
    report.XYZRationalCount += CIEXYZ.rationalize(output);
    //==========================================================================

    inputAndOutput[0] = input;
    inputAndOutput[1] = output;

    return inputAndOutput;
  }

  protected double[][][] produceRGBToXYZArray(LCDModel model,
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
//    double[] flareValues = model.getFlare().getValues();
//    double[] XYZValues = new double[3];

    //==========================================================================
    // ���N���ͩҦ���RGB��M�쪺XYZ
    //==========================================================================
    for (double r = 0; r <= 255.; r += step) {
      for (double g = 0; g <= 255.; g += step) {
        for (double b = 0; b <= 255.; b += step) {
          input[index] = new double[] {
              r / 255, g / 255, b / 255};
          rgb.setValues(input[index]);
          CIEXYZ XYZ = model.getXYZ(rgb, true);
//          XYZ.getValues(XYZValues);
          //�h��flare
//          double[] relativeXYZValues = DoubleArray.minus(XYZValues, flareValues);
//          XYZ.setValues(relativeXYZValues);
          //���W��
          XYZ.normalize(relativeDeviceWhite);
//          XYZ.getValues(relativeXYZValues);
//          output[index] = relativeXYZValues;
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
    report.XYZRationalCount += CIEXYZ.rationalize(output);

    inputAndOutput[0] = input;
    inputAndOutput[1] = output;

    return inputAndOutput;
  }

  public static void main(String[] args) {
//    String filename =
//        "factor/EIZO_CG221_2_i1pro Dark D65 Patch729_PolyBY_20_20070605.factor";
    String filename =
        "factor/Dell_M1210_2_i1pro Dark D65 Patch729_PolyBY_20_20070721.factor";
    LCDModel model = new LCDPolynomialRegressionModel(filename);
//    RGBColorSpaceModel model = new RGBColorSpaceModel(RGB.RGBColorSpace.sRGB);
//    RGBColorSpaceModel model = new RGBColorSpaceModel(RGB.RGBColorSpace.skyRGB);

    CIEXYZ deviceWhite = model.getModelWhite();
    deviceWhite.normalizeY();
    deviceWhite.times(100);

    ViewingConditions vc = new ViewingConditions(deviceWhite,
                                                 0.02, 20,
                                                 Surround.Dim,
                                                 "");
    LCDProfileMaker profileMaker = new LCDProfileMaker();
    profileMaker.setCATType(CAMConst.CATType.Bradford);
//    Profile p = profileMaker.makeXYZProfile(model,
//                                            LCDProfileMaker.LCDProfileType.
//                                            MatrixLut);
//    Profile p = profileMaker.makeJabProfile(model, vc);

    Profile p = profileMaker.makeLabProfile(model);
//    profileMaker.setReferenceMediumViewingConditions(ProfileUtils.
//        DimViewingConditions);
//    Profile p = profileMaker.makeJabLabProfile(model, vc);
//    Profile p = profileMaker.makeJabProfile(model, vc);
    p.profileProductionRecord = profileMaker.getReport().toString();

    System.out.println(profileMaker.getReport());

    String profileFilename = "Profile/Monitor/" + SoftwareName +
        "_" + p.profileDescription + ".icc";
    iccessAdapter.storeICCProfileLutByLut16(p, profileFilename);

  }

  /**
   * �ҥ�GMB��Profile���榡��@B2A
   * �u�ĥΤ@��3x3 matrix�i��B��
   * @param lcdModel LCDModel
   * @param AToB1 ColorSpaceConnectedLUT
   * @return ColorSpaceConnectedLUT
   */
  protected ColorSpaceConnectedLUT produceXYZToRGB1ByMatrix(LCDModel
      lcdModel,
      ColorSpaceConnectedLUT AToB1) {
    int grid = AToB1.getNumberOfGridPoints();
    //����D50��XYZ
    double[][] D50input = ColorSpaceConnectedLUT.produceInputXYZCLUT(grid);
    int size = D50input.length;
    double[][] output = new double[size][];

    for (int x = 0; x < size; x++) {
      output[x] = DoubleArray.copy(D50input[x]);
    }

    //==========================================================================
    // �p��B2A��3x3 matrix
    //==========================================================================
    CLUTRegressionReverseModel reverseModel = new CLUTRegressionReverseModel(
        AToB1,
        Polynomial.COEF_3.BY_3);
    reverseModel.produceFactor();
    LCDModel.Factor[] ModelFactors = reverseModel.getModelFactors();
    double[][] coefs = ( (CLUTRegressionReverseModel.Factor) ModelFactors[0]).
        reverseCoefficients;

    ColorSpaceConnectedLUT csclut = ProfileUtils.produceXYZToRGBCSCLUT(lcdModel,
        D50input, output, grid);
    csclut.matrix = coefs;

    return csclut;
  }

  /**
   * ����M�t��k
   * @param XYZGrid double[][]
   * @param boundaryColorSpace ProfileColorSpace
   * @param focalType FocalType
   * @param white CIEXYZ
   */
  protected final static void chromaClippingGamutMappint(double[][]
      XYZGrid,
      ProfileColorSpace
      boundaryColorSpace,
      FocalPoint.FocalType
      focalType,
      CIEXYZ white) {
    if (DO_GAMUT_MAPPING) {
      //white�HA2B�����D (X,.5,Z)
      CIELCh.XYZ2LChabValues(XYZGrid, white.getValues());
      chromaClippingGamutMappint(XYZGrid, boundaryColorSpace, focalType);
      CIELCh.LChab2XYZValues(XYZGrid, white.getValues());
    }
  }

  protected final static void chromaClippingGamutMappint(double[][]
      LabGrid,
      ProfileColorSpace
      boundaryColorSpace,
      FocalPoint.FocalType
      focalType) {
    if (DO_GAMUT_MAPPING) {

      CIELCh.fromLabValues(LabGrid);
//      GamutMappingAlgorithm gma = new ClippingGMA(boundaryColorSpace, focalType);
      GamutMappingAlgorithm gma = new RGBClippingGMA(boundaryColorSpace);
      gma.setChromaOffset(0);
      gma.gamutMapping(LabGrid);
      CIELCh.toLabValues(LabGrid);
    }
  }

  /**
   * �i��GMA
   */
  protected final static boolean DO_GAMUT_MAPPING = true;

  protected final static boolean DO_GAMUT_MAPPING2 = true;

  protected final static boolean DO_LAB_COORDINATES_OFFSET = true;

//  protected final static boolean DO_KEEP_NEUTRAL_GRAY = true;

  protected final static void abCoordinatesOffset(double[][] D50input,
                                                  double offset) {
    int size = D50input.length;
    for (int x = 0; x < size; x++) {
      double[] LabValues = D50input[x];
      if (LabValues[1] != -128. && LabValues[1] != 127.) {
        LabValues[1] += offset;
      }

      if (LabValues[2] != -128. && LabValues[2] != 127.) {
        LabValues[2] += offset;
      }
    }
  }
}
