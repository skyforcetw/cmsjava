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
   * D50White乘上係數0.5
   */
  protected CIEXYZ LUTPCSWhite;
  protected final static double NORMALIZE_Y = 32768. / 65535.;

  public static enum LCDProfileType {
    Lut, Matrix, MatrixLut
  }

  /**
   * 相對設備白,減掉flare的white
   */
  protected CIEXYZ relativeDeviceWhite;
  /**
   * relativeDeviceWhite經正規化處理
   */
  protected CIEXYZ relativeDeviceNormalizeWhite;

  public LCDProfileMaker() {
    LUTPCSWhite = (CIEXYZ) D50White.clone();
    LUTPCSWhite.scaleY(NORMALIZE_Y);
  }

  protected void init(LCDModel lcdModel) {
    init();

    //==========================================================================
    //參數的準備
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
    //產生色適應
    //==========================================================================
    //色適應模組是以 relativeDeviceNormalizeWhite為轉換基礎,使用上要特別注意
    D50chromaticAdaptation = new ChromaticAdaptation(
        relativeDeviceNormalizeWhite,
        D50White, this.catType);
    //==========================================================================
  }

  /**
   * 製作以Lab為PCS的Profile
   * @param lcdModel LCDModel
   * @return Profile
   */
  public Profile makeLabProfile(LCDModel lcdModel) {
    //==========================================================================
    //參數的準備
    //==========================================================================
    init(lcdModel);
    //==========================================================================

    //==========================================================================
    //產生對照表
    //==========================================================================

    //==========================================================================
    // AToB
    //==========================================================================
    //PCS下的RGB->XYZ
    double[][][] AToB = produceRGBToXYZArray(lcdModel, NUMBER_OF_GRID_POINTS, true);
    //產生CSCLUT供BToA用
    ColorSpaceConnectedLUT AToB1XYZ = ProfileUtils.produceRGBToXYZCSCLUT(
        lcdModel, AToB[0], AToB[1], NUMBER_OF_GRID_POINTS);

    //將RGB->XYZ變成 RGB->Lab
    CIELab.fromXYZValues(AToB[1], D50White.getValues());
    //合理化
    report._LabRationalCount += CIELab.rationalize(AToB[1]);
    //產生AToB的CSCLUT
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
    // 生成
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
   * 產生以PCS(Lab)對照表,但是中間銜接採用Jab
   * A2B: RGB -> XYZ -vc1-> Jab -vc2-> XYZ -> Lab
   *         (DC)   (螢幕vc)    (參考vc)     (白點D50)
   *
   * B2A: Lab -> XYZ -vc2-> Jab -vc1> XYZ -> RGB
   *       (白點D50) (參考vc)    (螢幕vc)  (DC)
   * @param lcdModel LCDModel
   * @param vc ViewingConditions
   * @return Profile
   */
  public Profile makeJabLabProfile(LCDModel lcdModel, ViewingConditions vc) {
    //==========================================================================
    //參數的準備
    //==========================================================================
    init(lcdModel);
    //==========================================================================

    //==========================================================================
    //產生對照表
    //==========================================================================

    //==========================================================================
    // AToB
    //==========================================================================
    //設備空間下的XYZ
    double[][][] AToB = produceRGBToXYZArray(lcdModel, NUMBER_OF_GRID_POINTS, false);
    //設備下的XYZ
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
    // 在色外貌空間做gma, xyz->Jab
    ColorSpaceConnectedLUT BToA1 = produceJabToRGB1ByLut(lcdModel, AToB1XYZ,
        GMA2ClippingType.RGB, vc);
    //==========================================================================

    //==========================================================================

    //==========================================================================
    // 生成
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
   * @todo M icc 不知道是流程有問題,還是根本不適合用色外貌Jab當Lab
   */
  public Profile makeJabProfile(LCDModel lcdModel, ViewingConditions vc) {
    //==========================================================================
    //參數的準備
    //==========================================================================
    init(lcdModel);
    //==========================================================================

    //==========================================================================
    //產生對照表
    //==========================================================================

    //==========================================================================
    // AToB
    //==========================================================================
    //設備空間下的XYZ
    double[][][] AToB = produceRGBToXYZArray(lcdModel, NUMBER_OF_GRID_POINTS, false);
    //設備下的XYZ
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
    // 在色外貌空間做gma, xyz->Jab
    ColorSpaceConnectedLUT BToA1 = produceJabToRGB1ByLut(lcdModel, AToB1XYZ,
        GMA2ClippingType.RGB, vc);
    //==========================================================================

    //==========================================================================

    //==========================================================================
    // 生成
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
   * 製作以XYZ為PCS的Profile
   * @param lcdModel LCDModel
   * @param type LCDProfileType
   * @return Profile
   */
  public Profile makeXYZProfile(LCDModel lcdModel, LCDProfileType type) {

    //==========================================================================
    //參數的準備
    //==========================================================================
    init(lcdModel);
    //==========================================================================

    //==========================================================================
    //產生對照表
    //==========================================================================
    double[][][] AToB = produceRGBToXYZArray(lcdModel, NUMBER_OF_GRID_POINTS, true);
    //將最大正規化到0.5
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
         * @todo M icc Lut模式有問題
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
    // 生成
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
   * 將D50的XYZ轉換到device下的XYZ
   * @param lutPCSXYZ double[][]
   * @param lutPCSWhite CIEXYZ
   */
  protected void PCSXYZToRelativeDeviceXYZ(double[][] lutPCSXYZ,
                                           CIEXYZ lutPCSWhite) {
    //從壓縮的A2B white(0.5) 還原成正常的D50 white
    lutPCSXYZ = CIEXYZ.times(lutPCSXYZ, lutPCSWhite.getValues(),
                             D50White.Y);
    //從D50轉回到device
    lutPCSXYZ = D50chromaticAdaptation.adaptationFromDestination(lutPCSXYZ);
    report.XYZRationalCount += CIEXYZ.rationalize(lutPCSXYZ);
    lutPCSXYZ = CIEXYZ.times(lutPCSXYZ,
                             relativeDeviceNormalizeWhite.getValues(),
                             relativeDeviceWhite.Y);
  }

  /**
   * 產生Lab2RGB的對照表
   * 1.產生D50Lab
   * 2.產生D50色域邊界
   * 3.進行色域對應
   * 4.產生對照表
   * @param lcdModel LCDModel
   * @param AToB1XYZ ColorSpaceConnectedLUT 計算色域邊界用的A2B
   * @param clipType GMA2ClippingType 進行第二次GMA時所採用的方式
   * @return ColorSpaceConnectedLUT
   */
  protected ColorSpaceConnectedLUT produceLabToRGB1ByLut(LCDModel
      lcdModel, ColorSpaceConnectedLUT AToB1XYZ, GMA2ClippingType clipType) {
    int grid = AToB1XYZ.getNumberOfGridPoints();
    //==========================================================================
    // 產生D50的Lab
    //==========================================================================
    double[][] D50input = ColorSpaceConnectedLUT.produceInputLabCLUT(grid);
    double[][] inputTables = null;
    if (DO_LAB_COORDINATES_OFFSET) {
      abCoordinatesOffset(D50input, 0.5);

      //========================================================================
      // 需要透過ab的1D Lut來做座標的平移
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
    // 色域邊界用色彩空間(因為是用A2B,所以是在D50空間下)
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
    // 進行色域對映
    //==========================================================================
    //D50的Lab
    double[][] relativeDeviceInput = DoubleArray.copy(D50input);
    //以D50(PCS)進行GMA
    chromaClippingGamutMappint(relativeDeviceInput,
                               boundaryColorSpace, FocalPoint.FocalType.None);
    //==========================================================================

    CIELab.toXYZValues(relativeDeviceInput,
                       D50White.getValues());
    //從PCS轉到device下
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
    // 產生色外貌的Jab
    //==========================================================================
    double[][] LabInput = ColorSpaceConnectedLUT.produceInputLabCLUT(grid);
    //==========================================================================

    //==========================================================================
    // 色域邊界用色彩空間(DeviceXYZ)
    //==========================================================================
    ProfileColorSpace boundaryColorSpace = getBoundaryColorSpace(AToB1DeviceXYZ,
        Polynomial.COEF_3.BY_19C);
    //==========================================================================

    //==========================================================================
    // 進行色域對映
    //==========================================================================
    //PCS Lab
    double[][] relativeDeviceInput = DoubleArray.copy(LabInput);
    //Lab先轉XYZ(PCS)
    CIELab.toXYZValues(relativeDeviceInput, D50White.getValues());
    report.XYZRationalCount += CIEXYZ.rationalize(relativeDeviceInput);
    //(PCS XYZ轉到Jab下)
    ProfileUtils.XYZToJabArray(relativeDeviceInput,
                               referenceMediumViewingConditions);
    report._LabRationalCount += CIELab.rationalize(relativeDeviceInput);

    /**
     * @todo M gma 色外貌的GMA
     */
    //目前是無作動的
    //利用Jab進行gma
    chromaClippingGamutMappint(relativeDeviceInput,
                               boundaryColorSpace, FocalPoint.FocalType.None);

    //將Jab轉成XYZ
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
   * 利用AToB1的數值,進行回歸取得model,作為色域邊界計算時使用
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
   * 產用inverseModel產生B2A
   * @param lcdModel LCDModel
   * @param AToB1 ColorSpaceConnectedLUT
   * @param clipType ClippingType
   * @return ColorSpaceConnectedLUT
   */
  protected ColorSpaceConnectedLUT produceXYZToRGB1ByLut(LCDModel
      lcdModel, ColorSpaceConnectedLUT AToB1, GMA2ClippingType clipType) {
    int grid = AToB1.getNumberOfGridPoints();
    //==========================================================================
    // 產生D50的XYZ
    //==========================================================================
    double[][] D50input = ColorSpaceConnectedLUT.produceInputXYZCLUT(grid);
    //==========================================================================

    //==========================================================================
    // 色域邊界用色彩空間(因為是用A2B,所以是在D50空間下)
    //==========================================================================
    ProfileColorSpace boundaryColorSpace = getBoundaryColorSpace(AToB1,
        Polynomial.COEF_3.BY_19C);
    //==========================================================================

    //==========================================================================
    // 進行色域對映
    //==========================================================================
    //D50的XYZ
    double[][] relativeDeviceInput = DoubleArray.copy(D50input);
    //以D50(PCS)進行GMA
    chromaClippingGamutMappint(relativeDeviceInput,
                               boundaryColorSpace,
                               FocalPoint.FocalType.MultiByKMeans, LUTPCSWhite);
    //從PCS轉到device下
    PCSXYZToRelativeDeviceXYZ(relativeDeviceInput, LUTPCSWhite);
    //==========================================================================

    double[][] output = produceB2AOutput(lcdModel, relativeDeviceInput,
                                         clipType);

    ColorSpaceConnectedLUT csclut = ProfileUtils.produceXYZToRGBCSCLUT(lcdModel,
        D50input, output, grid);

    return csclut;
  }

  /**
   * 採用matrix+lut的方式進行XYZ2RGB對照表的運算.
   * 先將XYZ透過matrix轉到一RGB空間,充分利用所有的對照表空間.
   * 再將RGB值轉回到XYZ,以此XYZ進行色域對映
   * @param lcdModel LCDModel
   * @param AToB1 ColorSpaceConnectedLUT
   * @param clipType ClippingType
   * @return ColorSpaceConnectedLUT
   */
  protected ColorSpaceConnectedLUT produceXYZToRGB1ByMatrixLut(LCDModel
      lcdModel, ColorSpaceConnectedLUT AToB1, GMA2ClippingType clipType) {

    //==========================================================================
    // 參考RGB空間
    //==========================================================================
    RGB.ColorSpace rgbColorSpace = RGB.ColorSpace.WideGamutRGB;
    CIEXYZ RGBRefWhite = rgbColorSpace.referenceWhite.getNormalizeXYZ();
    //==========================================================================

    //==========================================================================
    // 產生RGB的XYZ
    //==========================================================================
    int grid = AToB1.getNumberOfGridPoints();
    double[][] RGBInput = ColorSpaceConnectedLUT.produceInputXYZCLUT(grid);
    int size = RGBInput.length;
    //==========================================================================

    //==========================================================================
    // 色域邊界用色彩空間(因為是用A2B,所以是在D50空間下)
    //==========================================================================
    ProfileColorSpace boundaryColorSpace = getBoundaryColorSpace(AToB1,
        Polynomial.COEF_3.BY_19C);
    //==========================================================================

    //==========================================================================
    // 進行色域對映
    //==========================================================================
    //D50的XYZ
    double[][] relativeDeviceInput = new double[size][];
    for (int x = 0; x < size; x++) {
      double[] rgbValues = RGBInput[x];
      relativeDeviceInput[x] = RGB.linearToXYZValues(rgbValues, rgbColorSpace);
    }
    //以D50(PCS)進行GMA
    chromaClippingGamutMappint(relativeDeviceInput,
                               boundaryColorSpace,
                               FocalPoint.FocalType.MultiByKMeans, RGBRefWhite);
    //從PCS轉到device下
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
   * 依照relativeDeviceInput內的數值,
   * 經lcdModel轉換後所得的RGB,取得B2A的輸出值.
   * 如果B2A的輸出值有不合理處,依照clipType的方法進行clip
   * @param lcdModel LCDModel
   * @param relativeDeviceInputXYZ double[][]
   * @param clipType ClippingType
   * @return double[][]
   */
  protected double[][] produceB2AOutput(LCDModel lcdModel,
                                        double[][] relativeDeviceInputXYZ,
                                        GMA2ClippingType clipType) {
    //==========================================================================
    // 參數的準備
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
    // 產生B2A對照表
    //==========================================================================
    /*for (int x = 0; x < size; x++) {
      //先加上flare
      double[] XYZValues = DoubleArray.plus(relativeDeviceInputXYZ[x],
                                            deviceFlare);
      XYZ.setValues(XYZValues);
      //再用LCDModel反推RGB
      RGB rgb = lcdModel.getRGB(XYZ, false);

      //如果rgb不存在或者不合理
      if (DO_GAMUT_MAPPING2 && (rgb == null || !rgb.isLegal())) {
        gma2Count++;
        if (clipType == GMA2ClippingType.RGB) {
          //採用RGB clipping的方式
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
      //再用LCDModel反推RGB
      RGB rgb = lcdModel.getRGB(XYZ, true);

      //如果rgb不存在或者不合理
      if (DO_GAMUT_MAPPING2 && (rgb == null || !rgb.isLegal())) {
        gma2Count++;
        if (clipType == GMA2ClippingType.RGB) {
          //採用RGB clipping的方式
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
   * 產生RGB的grid,計算出對映的設備XYZ.此XYZ已經減去flare.
   * 再將此設備的XYZ從設備色溫轉到D50色溫(PCS)
   * @param model LCDModel
   * @param grid int
   * @return double[][][]
   * @deprecated
   */
  protected double[][][] produceRGBToD50XYZArray(LCDModel model,
                                                 int grid) {

    //==========================================================================
    // 必要的數據先準備好
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
    // 迭代產生所有的RGB對映到的XYZ
    //==========================================================================
    for (double r = 0; r <= 255.; r += step) {
      for (double g = 0; g <= 255.; g += step) {
        for (double b = 0; b <= 255.; b += step) {
          input[index] = new double[] {
              r / 255, g / 255, b / 255};
          rgb.setValues(input[index]);
          CIEXYZ XYZ = model.getXYZ(rgb, false);
          XYZ.getValues(XYZValues);
          //去掉flare
          double[] relativeXYZValues = DoubleArray.minus(XYZValues, flareValues);
          XYZ.setValues(relativeXYZValues);
          //正規化
          XYZ.normalize(relativeDeviceWhite);
          XYZ.getValues(relativeXYZValues);
          output[index] = relativeXYZValues;
          index++;
        }
      }
    }
    //==========================================================================

    //==========================================================================
    // 由於產生出來的XYZ處於relativeDeviceNormalizeWhite,所以得轉到PCS(D50)並且合理化
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
    // 必要的數據先準備好
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
    // 迭代產生所有的RGB對映到的XYZ
    //==========================================================================
    for (double r = 0; r <= 255.; r += step) {
      for (double g = 0; g <= 255.; g += step) {
        for (double b = 0; b <= 255.; b += step) {
          input[index] = new double[] {
              r / 255, g / 255, b / 255};
          rgb.setValues(input[index]);
          CIEXYZ XYZ = model.getXYZ(rgb, true);
//          XYZ.getValues(XYZValues);
          //去掉flare
//          double[] relativeXYZValues = DoubleArray.minus(XYZValues, flareValues);
//          XYZ.setValues(relativeXYZValues);
          //正規化
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
    // 由於產生出來的XYZ處於relativeDeviceNormalizeWhite,所以得轉到PCS(D50)並且合理化
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
   * 模仿GMB的Profile的格式實作B2A
   * 只採用一個3x3 matrix進行運算
   * @param lcdModel LCDModel
   * @param AToB1 ColorSpaceConnectedLUT
   * @return ColorSpaceConnectedLUT
   */
  protected ColorSpaceConnectedLUT produceXYZToRGB1ByMatrix(LCDModel
      lcdModel,
      ColorSpaceConnectedLUT AToB1) {
    int grid = AToB1.getNumberOfGridPoints();
    //產生D50的XYZ
    double[][] D50input = ColorSpaceConnectedLUT.produceInputXYZCLUT(grid);
    int size = D50input.length;
    double[][] output = new double[size][];

    for (int x = 0; x < size; x++) {
      output[x] = DoubleArray.copy(D50input[x]);
    }

    //==========================================================================
    // 計算B2A的3x3 matrix
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
   * 色域對映演算法
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
      //white以A2B的為主 (X,.5,Z)
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
   * 進行GMA
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
