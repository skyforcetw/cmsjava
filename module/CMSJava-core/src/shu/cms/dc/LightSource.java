package shu.cms.dc;

import java.util.List;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorformat.cxf.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;

//import shu.plot.*;
/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 取出測量而得的光源光譜能量分布,再除上測量光源所用的導表反射率,
 * 所得的光譜值才是最接近光源的光譜能量值
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class LightSource {

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 用來作為測量媒介的兩種導表
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   */
  public static enum WhitePatchType {
    CCDCWhite, GrayCard;
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 光源的種類
   *
   * <p>Copyright: Copyright (c) 2006</p>
   *
   * <p>Company: </p>
   *
   * @author cms.shu.edu.tw
   * @version 1.0
   * @deprecated
   */
  public static enum Calibrated
      implements Source {
    A, CWF, Day, TL84, F;
    public String getName() {
      return name();
    }

    public String getParentName() {
      return this.getClass().getSimpleName();
    }
  }

  public static enum i1Pro
      implements Source {
    D65, D50, F10, F8, F2, F12, A, Flash, Day;
    public String getName() {
      return name();
    }

    public String getParentName() {
      return this.getClass().getSimpleName();
    }
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * CIE的標準光源
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static enum CIE
      implements Source {
    D65, D50, D55, F11, F8, F2, A, E;
    public String getName() {
      return name();
    }

    public String getParentName() {
      return this.getClass().getSimpleName();
    }

  }

  public static interface Source {
    public String getName();

    public String getParentName();
  }

  public static interface Illuminantable {
    public Illuminant getIlluminant();
  }

  public static class DaylightIlluminant
      implements Source, Illuminantable {
    private int cct;
    public DaylightIlluminant(int cct) {
      this.cct = cct;
    }

    public Illuminant getIlluminant() {
      return Illuminant.getDaylightByTemperature(cct);
    }

    public String getName() {
      return "DIlluminant " + cct + " k";
    }

    public String getParentName() {
      return this.getClass().getSimpleName();
    }
  }

  public static class BlackbodyIlluminant
      implements Source, Illuminantable {
    private int cct;
    public BlackbodyIlluminant(int cct) {
      this.cct = cct;
    }

    public Illuminant getIlluminant() {
      Spectra spectra = CorrelatedColorTemperature.
          getSpectraOfBlackbodyRadiator(cct);
      Illuminant illuminant = new Illuminant(spectra);
      return illuminant;
    }

    public String getName() {
      return "DIlluminant " + cct + " k";
    }

    public String getParentName() {
      return this.getClass().getSimpleName();
    }
  }

  protected final static String LIGHT_SOURCES_DIR =
      "Measurement Files/Light Sources";

  protected static String getFilename(Source type) {

    String filename = LIGHT_SOURCES_DIR + "/" + type.getParentName() + "/" +
        type.getName() + ".cxf";
    return filename;

//    if (type instanceof Calibrated) {
//      String filename = LIGHT_SOURCES_DIR + "/calibrated/" +
//          type.getName() + ".cxf";
//      return filename;
//    }
//    else if (type instanceof i1Pro) {
//      String filename = LIGHT_SOURCES_DIR + "/i1Pro/" +
//          type.getName() + ".cxf";
//      return filename;
//    }
//
//    else {
//      return type.getName();
//    }
  }

  public final static Illuminant getIlluminant(LightSource.Source lightSource) {
    if (lightSource instanceof CIE) {
      return getIlluminant( (CIE) lightSource);
    }
    else if (lightSource instanceof i1Pro) {
      return getIlluminant( (i1Pro) lightSource);
    }
    else {
      return null;
    }

  }

  protected final static Illuminant getIlluminant(CIE cie) {
    switch (cie) {
      case D65:
        return Illuminant.D65;
      case D55:
        return Illuminant.D55;
      case D50:
        return Illuminant.D50;
      case F11:
        return Illuminant.F11;
      case F8:
        return Illuminant.F8;
      case F2:
        return Illuminant.F2;
      case A:
        return Illuminant.A;
      case E:
        return Illuminant.E;
      default:
        return null;

    }
  }

  public static void main(String[] args) {
//    System.out.println(LightSource.i1Pro.D50.getClass().getSimpleName());
    System.out.println(LightSource.CIE.D50.getParentName());
    LightSource.i1Pro[] lightSource = new LightSource.i1Pro[] {
        LightSource.i1Pro.D50, LightSource.i1Pro.D65,
        LightSource.i1Pro.F8, LightSource.i1Pro.F12};
    Spectra[] lightSourceSpectra = DCUtils.getSpectra(lightSource);
//    double[] factor = DCUtils.normalizeFactor(lightSource);
    double[] factor = DCUtils.produceNormalizedFactor(lightSourceSpectra);
//    double factor = DCUtils.normalFactorByMaxPeak(lightSource);
//    DCUtils.normalFactorToEqualLuminance(lightSource);
//    double factor = 1;
//    double[] factor = new double[] {
//        1, 1, 1, 1};

    Spectra d65 = getIlluminant(i1Pro.D65).getSpectra().timesAndReturn(factor[1]);
    Spectra d50 = getIlluminant(i1Pro.D50).getSpectra().timesAndReturn(factor[0]);
    Spectra f8 = getIlluminant(i1Pro.F8).getSpectra().timesAndReturn(factor[2]);
    Spectra f12 = getIlluminant(i1Pro.F12).getSpectra().timesAndReturn(factor[3]);
    Plot2D plot = Plot2D.getInstance();
    plot.addSpectra(null, Color.red, d65);
    plot.addSpectra(null, Color.green, d50);
    plot.addSpectra(null, Color.yellow, f8);
    plot.addSpectra(null, Color.blue, f12);
    plot.setVisible(true);
    System.out.println(d65.getXYZ());
    System.out.println(d50.getXYZ());
    System.out.println(f8.getXYZ());
    System.out.println(f12.getXYZ());

    System.out.println(DoubleArray.toString(d50.getData()));
    System.out.println(DoubleArray.toString(d65.getData()));
    System.out.println(DoubleArray.toString(f8.getData()));
    System.out.println(DoubleArray.toString(f12.getData()));
  }

  /**
   * 從Illuminant判斷為何種LightSource
   * @param illuminant Illuminant
   * @return Source
   */
  public final static LightSource.Source getLightSourceType(Illuminant
      illuminant) {
    if (illuminant == Illuminant.D65) {
      return CIE.D65;
    }
    else if (illuminant == Illuminant.D55) {
      return CIE.D55;
    }
    else if (illuminant == Illuminant.D50) {
      return CIE.D50;
    }
    else if (illuminant == Illuminant.A) {
      return CIE.A;
    }
    else if (illuminant == Illuminant.E) {
      return CIE.E;
    }
    else if (illuminant == Illuminant.F2) {
      return CIE.F2;
    }
    else if (illuminant == Illuminant.F8) {
      return CIE.F8;
    }
    else if (illuminant == Illuminant.F11) {
      return CIE.F11;
    }
    else {
//      System.out.println("?");
      return null;
    }
  }

  /**
   * 以i1測量而得的光源光譜值
   * @param type i1Pro
   * @return Illuminant
   */
  protected final static Illuminant getIlluminant(i1Pro type) {
    String illuminantFilename = getFilename(type);
    CXFOperator illuminantCxF = new CXFOperator(illuminantFilename);
    List<Spectra> illuminantSpectra = illuminantCxF.getSpectraList();
    Spectra spectra = illuminantSpectra.get(0);
    spectra.setName("i1Pro-" + spectra.getName());
    return new Illuminant(spectra);
  }

  public final static Illuminant getIlluminant(WhitePatchType whitePatch,
                                               Source lightSource) {
    /**
     * CIE照明體特別處理
     */
    if (lightSource instanceof CIE) {
      Illuminant illuminant = getIlluminant( (CIE) lightSource);
      illuminant.getSpectra().normalizeDataToMax();
      return illuminant;
    }

    Spectra light = null;
    Spectra white = null;

    String illuminantFilename = getFilename(lightSource);
    CXFOperator illuminantCxF = new CXFOperator(illuminantFilename);

    List<Spectra> illuminantSpectra = illuminantCxF.getSpectraList();

    switch (whitePatch) {
      case CCDCWhite: {
        String filename = "Reference Files/Camera/ColorChecker DC.cxf";
        CXFOperator cxf = new CXFOperator(filename);
        List<Spectra> targetReflectSpectra = cxf.getSpectraList();
        //CCDC的中央白
        Spectra[] whiteSpectra = new Spectra[] {
            targetReflectSpectra.get(113), targetReflectSpectra.get(114),
            targetReflectSpectra.get(125), targetReflectSpectra.get(126)};
        white = Spectra.average(whiteSpectra);
        //此乃因為光源的CxF檔裡,第二筆資料是CCDC White所測量而得
        light = illuminantSpectra.get(1);
      }
      break;
      case GrayCard: {
        String filename = "Reference Files/Camera/Gray Card.cxf";
        CXFOperator cxf = new CXFOperator(filename);
        List<Spectra> targetReflectSpectra = cxf.getSpectraList();
        //灰卡的中央,灰卡以4x5的區塊測量中央,因此以中央兩格平均當基準
        Spectra[] whiteSpectra = new Spectra[] {
            targetReflectSpectra.get(9), targetReflectSpectra.get(10)};
        white = Spectra.average(whiteSpectra);
        //此乃因為光源的CxF檔裡,第一筆資料是灰卡所測量而得
        light = illuminantSpectra.get(0);
      }
      break;
    }

    return new Illuminant(processLightSource(light, white));
  }

  /**
   * 藉由參考白的光譜反射率和從參考白測量而得的光譜能量值,推算光源的光譜能量值
   * @param light Spectra
   * @param whiteReflection Spectra
   * @return Spectra
   */
  protected final static Spectra processLightSource(Spectra light,
      Spectra whiteReflection) {
    int smallestInterval = Math.min(light.getInterval(),
                                    whiteReflection.getInterval());
    int start = Math.max(light.getStart(), whiteReflection.getStart());
    int end = Math.min(light.getEnd(), whiteReflection.getEnd());

    light = light.fillAndInterpolate(start, end, smallestInterval,
                                     Interpolation.Algo.Lagrange);

    whiteReflection = whiteReflection.fillAndInterpolate(start, end,
        smallestInterval, Interpolation.Algo.Lagrange);

    double[] lightData = light.getData();
    double[] whiteData = whiteReflection.getData();

    if (lightData.length != whiteData.length) {
      throw new IllegalStateException(
          "light's data length != whiteReflect's data length");
    }

    int size = light.getData().length;
    double[] data = new double[size];
    for (int x = 0; x < size; x++) {
      data[x] = lightData[x] / whiteData[x];
    }

    Spectra lightSource = new Spectra(light.getName(), light.getSpectraType(),
                                      start, end, smallestInterval,
                                      data);

    return lightSource;
  }
}
