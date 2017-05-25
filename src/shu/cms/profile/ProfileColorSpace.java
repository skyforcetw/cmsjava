package shu.cms.profile;

import java.io.*;

import java.awt.color.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.CAMConst.*;
import shu.cms.lcd.*;
import shu.math.array.*;
import shu.math.lut.*;

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
public abstract class ProfileColorSpace
    implements Serializable {
  protected Style style;
  protected ChromaticAdaptation PCSChromaticAdaptation;
  protected ChromaticAdaptation D65ChromaticAdaptation;
  protected String description;

  public final boolean isLookUpTableType() {
    return style == Style.CLUT;
  }

  protected ProfileColorSpace(CIEXYZ referenceWhite, String description) {
    this(referenceWhite, ChromaticAdaptation.DEFAULT_CAT_TYPE, description);
  }

  protected CIEXYZ referenceWhite;
  protected CAMConst.CATType catType;

  protected ProfileColorSpace(CIEXYZ referenceWhite, CAMConst.CATType catType,
                              String description) {
    this.catType = catType;
    PCSChromaticAdaptation = ChromaticAdaptation.getInstanceAdaptToPCS(
        referenceWhite, catType);
    D65ChromaticAdaptation = ChromaticAdaptation.getInstanceAdaptToD65(
        referenceWhite, catType);
    this.referenceWhite = referenceWhite;
    this.description = description;
  }

  protected static enum Style {
    CLUT, ICCProfile, LCDModel, LCDTarget, Profile, RGBColorSpace, Tetrahedral
  }

  /**
   * 從Profile原生的CIEXYZ轉到RGB
   * @param XYZValues double[]
   * @return double[]
   */
  public abstract double[] fromCIEXYZValues(double[] XYZValues);

  /**
   * 從RGB轉到原生的CIEXYZ
   * @param rgbValues double[]
   * @return double[]
   */
  public abstract double[] toCIEXYZValues(double[] rgbValues);

  public final CIEXYZ getReferenceWhite() {
    return referenceWhite;
  }

  public shu.cms.profile.ProfileColorSpace.Style getStyle() {
    return style;
  }

  public final static CIEXYZ getPCSReferenceWhite() {
    return Illuminant.D50WhitePoint;
  }

  public final static CIEXYZ getD65ReferenceWhite() {
    return Illuminant.D65WhitePoint;
  }

  private final static double[] PCSWhiteValues = getPCSReferenceWhite().
      getValues();

  /**
   * 從PCS的CIEXYZ->Device CIEXYZ->RGB
   * @param PCSXYZValues double[]
   * @return double[]
   */
  public double[] fromPCSCIEXYZValues(double[] PCSXYZValues) {
    double[] deviceXYZValues = PCSChromaticAdaptation.getSourceColor(
        PCSXYZValues);
    return fromCIEXYZValues(deviceXYZValues);
  }

  public double[] fromPCSCIELChValues(double[] PCSLChValues) {
    double[] deviceXYZValues = toDeviceCIEXYZValues(PCSLChValues);
    return fromCIEXYZValues(deviceXYZValues);
  }

  public double[] toDeviceCIEXYZValues(double[] PCSLChValues) {
    double[] XYZValues = CIELCh.LChab2XYZValues(PCSLChValues, PCSWhiteValues);
    double[] deviceXYZValues = PCSChromaticAdaptation.getSourceColor(
        XYZValues);
    return deviceXYZValues;
  }

  public double[] fromD65CIEXYZValues(double[] D65XYZValues) {
    double[] deviceXYZValues = D65ChromaticAdaptation.getSourceColor(
        D65XYZValues);
    return fromCIEXYZValues(deviceXYZValues);
  }

  /**
   * 從RGB轉到PCS的CIEXYZ,也就是D50下的CIEXYZ
   * @param rgbValues double[]
   * @return double[]
   */
  public double[] toPCSCIEXYZValues(double[] rgbValues) {
    double[] deviceXYZValues = toCIEXYZValues(rgbValues);
    double[] pcsXYZValues = PCSChromaticAdaptation.getDestinationColor(
        deviceXYZValues);
    return pcsXYZValues;
  }

  public double[] toPCSCIELChValues(double[] rgbValues) {
    double[] XYZValues = toPCSCIEXYZValues(rgbValues);
    return CIELCh.XYZ2LChabValues(XYZValues, PCSWhiteValues);
  }

//  public double[]

  public double[] toD65CIEXYZValues(double[] rgbValues) {
    double[] deviceXYZValues = toCIEXYZValues(rgbValues);
    return D65ChromaticAdaptation.getDestinationColor(deviceXYZValues);
  }

  public final static class Instance {

    /**
     * 預設採用RelativeColorimetric
     * @param profile Profile
     * @param catType CATType
     * @param description String
     * @return ProfileColorSpace
     */
    public final static ProfileColorSpace get(Profile profile,
                                              CAMConst.CATType catType,
                                              String description) {
      return get(profile, RenderingIntent.RelativeColorimetric, catType,
                 description);
    }

    public final static ProfileColorSpace get(Profile profile,
                                              String description) {
      return get(profile, RenderingIntent.RelativeColorimetric,
                 ChromaticAdaptation.DEFAULT_CAT_TYPE, description);
    }

    /**
     * 採用Profile產生ProfileColorSpace
     * @param profile Profile
     * @param intent RenderingIntent
     * @param catType CATType
     * @param description String
     * @return ProfileColorSpace
     */
    public final static ProfileColorSpace get(Profile profile,
                                              RenderingIntent intent,
                                              CAMConst.CATType catType,
                                              String description) {
      if (profile == null) {
        //這是測試用的
        return new ByRGBColorSpace(RGB.ColorSpace.sRGB, catType, description);
      }
      else {
        ByProfile profileColorSpace = new ByProfile(profile, intent, catType,
            description);
        return profileColorSpace;
      }
    }

    public final static ProfileColorSpace get(Profile profile,
                                              RenderingIntent intent,
                                              String description) {
      return get(profile, intent, ChromaticAdaptation.DEFAULT_CAT_TYPE,
                 description);
    }

    /**
     * 採用ColorSpaceConnectedLUT產生ProfileColorSpace
     * @param AToB ColorSpaceConnectedLUT
     * @param BToA ColorSpaceConnectedLUT
     * @param white CIEXYZ
     * @param catType CATType
     * @param description String
     * @return ProfileColorSpace
     */
    public final static ProfileColorSpace get(ColorSpaceConnectedLUT AToB,
                                              ColorSpaceConnectedLUT BToA,
                                              CIEXYZ white,
                                              CAMConst.CATType catType,
                                              String description) {
      ByCLUT byCLUT = new ByCLUT(AToB, BToA, white, catType, description);
      return byCLUT;
    }

    public final static ProfileColorSpace get(ColorSpaceConnectedLUT AToB,
                                              ColorSpaceConnectedLUT BToA,
                                              CIEXYZ white, String description) {
      return get(AToB, BToA, white,
                 ChromaticAdaptation.DEFAULT_CAT_TYPE, description);
    }

    public final static ProfileColorSpace get(TetrahedralInterpolation AToB,
                                              TetrahedralInterpolation BToA,
                                              CIEXYZ white,
                                              CAMConst.CATType catType,
                                              String description) {
      ByTetrahedral byTetrahedral = new ByTetrahedral(AToB, BToA, white,
          catType, description);
      return byTetrahedral;
    }

    public final static ProfileColorSpace get(TetrahedralInterpolation AToB,
                                              TetrahedralInterpolation BToA,
                                              CIEXYZ white, String description) {
      return get(AToB, BToA, white,
                 ChromaticAdaptation.DEFAULT_CAT_TYPE, description);
    }

    /**
     * 採用RGB.RGBColorSpace產生ProfileColorSpace
     * @param rgbColorSpace ColorSpace
     * @param catType CATType
     * @param description String
     * @return ProfileColorSpace
     */
    public final static ProfileColorSpace get(RGB.ColorSpace rgbColorSpace,
                                              CAMConst.CATType catType,
                                              String description) {
      ByRGBColorSpace profileColorSpace = new ByRGBColorSpace(rgbColorSpace,
          catType, description);
      return profileColorSpace;
    }

    public final static ProfileColorSpace get(RGB.ColorSpace rgbColorSpace,
                                              CAMConst.CATType catType) {
      return get(rgbColorSpace, catType, "");
    }

    public final static ProfileColorSpace get(LCDTarget lcdTarget,
                                              CAMConst.CATType catType,
                                              String description) {
      return new ByLCDTarget(lcdTarget, catType, description);
    }

    public final static ProfileColorSpace get(LCDTarget lcdTarget,
                                              String description) {
      return get(lcdTarget, ChromaticAdaptation.DEFAULT_CAT_TYPE, description);
    }

    public final static ProfileColorSpace get(RGB.ColorSpace rgbColorSpace,
                                              String description) {
      return get(rgbColorSpace, ChromaticAdaptation.DEFAULT_CAT_TYPE,
                 description);
    }

    public final static ProfileColorSpace get(RGB.ColorSpace rgbColorSpace) {
      return get(rgbColorSpace, ChromaticAdaptation.DEFAULT_CAT_TYPE,
                 "");
    }

    public final static ProfileColorSpace get(LCDModel model,
                                              CAMConst.CATType catType,
                                              String description) {
      ByLCDModel profileColorSpace = new ByLCDModel(model, catType, description);
      return profileColorSpace;
    }

    public final static ProfileColorSpace get(LCDModel
                                              model, String description) {
      return get(model, ChromaticAdaptation.DEFAULT_CAT_TYPE, description);
    }

    public final static ProfileColorSpace get(ICC_ProfileRGB iccProfile,
                                              CAMConst.CATType catType,
                                              String description) {
      return new ByICCProfile(iccProfile, catType, description);
    }

    public final static ProfileColorSpace get(ICC_ProfileRGB iccProfile,
                                              String description) {
      return get(iccProfile, ChromaticAdaptation.DEFAULT_CAT_TYPE, description);
    }

  }

  /**
   *
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
  protected static class ByProfile
      extends ByCLUT {
    protected Profile profile;
    protected RenderingIntent intent;

    protected ByProfile(Profile profile, RenderingIntent intent,
                        CAMConst.CATType catType, String description) {
      super(profile.getAToB(intent), profile.getBToA(intent),
            profile.getMediaWhitePoint(), catType, description);
      this.profile = profile;
      this.style = Style.Profile;
      this.intent = intent;
    }

    public double[] fromCIEXYZValues(double[] XYZValues) {
      double[] PCSXYZValues = PCSChromaticAdaptation.getDestinationColor(
          XYZValues);
      return fromPCSCIEXYZValues(PCSXYZValues);
    }

    public double[] fromPCSCIEXYZValues(double[] PCSXYZValues) {
      return super.fromCIEXYZValues(PCSXYZValues);
    }

    public double[] toPCSCIEXYZValues(double[] rgbValues) {
      return super.toCIEXYZValues(rgbValues);
    }

    public double[] toCIEXYZValues(double[] rgbValues) {
      double[] PCSXYZValues = toPCSCIEXYZValues(rgbValues);
      return PCSChromaticAdaptation.getSourceColor(PCSXYZValues);
    }

  }

  protected static class ByICCProfile
      extends ProfileColorSpace {
    protected ICC_ColorSpace colorSpace;
    protected CIEXYZ referenceWhite;

    /**
     * ByICCProfile
     * @param iccProfile ICC_ProfileRGB
     * @param catType CATType
     * @param description String
     */
    protected ByICCProfile(ICC_ProfileRGB iccProfile, CATType catType,
                           String description) {
      super(new CIEXYZ(FloatArray.toDoubleArray(iccProfile.
                                                getMediaWhitePoint())),
            catType, description);
      this.style = Style.ICCProfile;
      colorSpace = new ICC_ColorSpace(iccProfile);
    }

    /**
     * 從Profile原生的CIEXYZ轉到RGB
     *
     * @param XYZValues double[]
     * @return double[]
     */
    public double[] fromCIEXYZValues(double[] XYZValues) {
      float[] PCSXYZValues = DoubleArray.toFloatArray(PCSChromaticAdaptation.
          getDestinationColor(XYZValues));
      return FloatArray.toDoubleArray(colorSpace.fromCIEXYZ(PCSXYZValues));
    }

    /**
     * 從RGB轉到原生的CIEXYZ
     *
     * @param rgbValues double[]
     * @return double[]
     */
    public double[] toCIEXYZValues(double[] rgbValues) {
      double[] PCSXYZValues = FloatArray.toDoubleArray(colorSpace.toCIEXYZ(
          DoubleArray.toFloatArray(rgbValues)));
      return PCSChromaticAdaptation.getSourceColor(PCSXYZValues);
    }

  }

  protected static class ByRGBColorSpace
      extends ProfileColorSpace {
    protected RGB.ColorSpace rgbColorSpace;
    public RGB.ColorSpace getRGBColorSpace() {
      return rgbColorSpace;
    }

    protected ByRGBColorSpace(RGB.ColorSpace rgbColorSpace,
                              CAMConst.CATType catType, String description) {
      super(rgbColorSpace.getReferenceWhiteXYZ(), catType, description);
      this.rgbColorSpace = rgbColorSpace;
      this.style = Style.RGBColorSpace;
    }

    public double[] fromCIEXYZValues(double[] XYZValues) {
      return RGB.fromXYZValues(XYZValues, rgbColorSpace);
    }

    public double[] toCIEXYZValues(double[] rgbValues) {
      return RGB.toXYZValues(rgbValues, rgbColorSpace);
    }

  }

  protected static class ByLCDModel
      extends ProfileColorSpace {
    protected LCDModel model;
    protected RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, null,
                                RGB.MaxValue.Double1);
    protected CIEXYZ XYZ = new CIEXYZ();

    protected ByLCDModel(LCDModel model, CAMConst.CATType catType,
                         String description) {
      super(model.getModelWhite(), catType, description);
      this.model = model;
      this.style = Style.LCDModel;
    }

    public double[] fromCIEXYZValues(double[] XYZValues) {
      XYZ.setValues(XYZValues);
      RGB result = model.getRGB(XYZ, false);
      LCDModel.RGBResult rgbResult = model.getRGBResult();
      if (rgbResult.illegalXYZ /*|| result.isFixed()*/) {
        return rgbResult.firstRGB.getValues(new double[3], RGB.MaxValue.Double1);
      }
//      if (result.isFixed()) {
//        int x = 1;
//      }
      if (result != null) {
        return result.getValues(new double[3], RGB.MaxValue.Double1);
      }
      else {
        return null;
      }
    }

    public double[] toCIEXYZValues(double[] rgbValues) {
      rgb.setValues(rgbValues, RGB.MaxValue.Double1);
      model.changeMaxValue(rgb);
      CIEXYZ result = model.getXYZ(rgb, false);
      return result.getValues();
    }

  }

  protected static class ByLCDTarget
      extends ProfileColorSpace {

    protected LCDTarget lcdTarget;
    protected RGB keyRGB;

    private final static CIEXYZ getWhiteOrWhitestXYZ(LCDTarget lcdTarget) {
      Patch white = lcdTarget.getWhitePatch();
      if (white != null) {
        return white.getXYZ();
      }
      else {
        return lcdTarget.getBrightestPatch().getXYZ();
      }
    }

    /**
     * ByLCDTarget
     * @param lcdTarget LCDTarget
     * @param catType CATType
     * @param description String
     */
    protected ByLCDTarget(LCDTarget lcdTarget, CATType catType,
                          String description) {
      super(getWhiteOrWhitestXYZ(lcdTarget), catType, description);
      this.style = Style.LCDTarget;
      this.lcdTarget = lcdTarget;
      keyRGB = lcdTarget.getKeyRGB();
    }

    /**
     * 從Profile原生的CIEXYZ轉到RGB
     *
     * @param XYZValues double[]
     * @return double[]
     */
    public double[] fromCIEXYZValues(double[] XYZValues) {
      throw new UnsupportedOperationException();
    }

    /**
     * 從RGB轉到原生的CIEXYZ
     *
     * @param rgbValues double[]
     * @return double[]
     */
    public double[] toCIEXYZValues(double[] rgbValues) {
      keyRGB.setValues(rgbValues, RGB.MaxValue.Double1);
      keyRGB.quantization(RGB.MaxValue.Int8Bit);

      Patch p = lcdTarget.getPatch(keyRGB);
      if (p != null) {
        return p.getXYZ().getValues();
      }
      else {
        throw new IllegalArgumentException("rgbValues is not exist.");
      }
    }
  }

  protected static class ByTetrahedral
      extends ProfileColorSpace {
    protected TetrahedralInterpolation A2B;
    protected TetrahedralInterpolation B2A;
    protected CIEXYZ referenceWhite;

    protected ByTetrahedral(TetrahedralInterpolation AToB,
                            TetrahedralInterpolation BToA,
                            CIEXYZ white, CAMConst.CATType catType,
                            String description) {
      super(white, catType, description);
      this.style = Style.Tetrahedral;
      this.referenceWhite = white;

      if (AToB != null) {
        this.A2B = AToB;
      }
      if (BToA != null) {
        this.B2A = BToA;
      }

    }

    public double[] fromCIEXYZValues(double[] XYZValues) {
      return B2A.getValues(XYZValues);
    }

    public double[] toCIEXYZValues(double[] rgbValues) {
      double[] rgbValues8Bit = DoubleArray.times(rgbValues, 255);
      return A2B.getValues(rgbValues8Bit);
    }

  }

  /**
   *
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
   * @todo H icc Lab對照表的處理
   */
  protected static class ByCLUT
      extends ByTetrahedral {

    protected ByCLUT(ColorSpaceConnectedLUT AToB, ColorSpaceConnectedLUT BToA,
                     CIEXYZ white, CAMConst.CATType catType, String descriptio) {
      super(AToB != null ? AToB.produceTetrahedralInterpolation() : null,
            BToA != null ? BToA.produceTetrahedralInterpolation() : null,
            white, catType, descriptio);
      this.style = Style.CLUT;
    }

  }

  public static void main(String[] args) throws IOException {
    ICC_Profile profile = ICC_Profile.getInstance(
        "C:/WINDOWS/system32/spool/drivers/color/WideGamutRGB.icc");

    ProfileColorSpace pcs = ProfileColorSpace.Instance.get( (ICC_ProfileRGB)
        profile, ChromaticAdaptation.DEFAULT_CAT_TYPE, "");
    System.out.println(DoubleArray.toString(pcs.toD65CIEXYZValues(new double[] {
        0, .5, .8})));

    System.out.println(DoubleArray.toString(pcs.toPCSCIEXYZValues(new double[] {
        0, .5, .8})));
    System.out.println(DoubleArray.toString(pcs.fromPCSCIEXYZValues(new double[] {
        0.112054, 0.168102, 0.484659})));
  }

}
