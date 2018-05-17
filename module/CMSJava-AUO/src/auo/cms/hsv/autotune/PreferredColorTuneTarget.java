package auo.cms.hsv.autotune;

import java.io.*;
import java.util.*;

import org.math.io.files.*;
import flanagan.math.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.gma.*;
import shu.cms.gma.gbd.*;
import shu.cms.gma.gbp.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.lut.*;
import auo.cms.prefercolor.model.PreferredColorModel;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class PreferredColorTuneTarget
    extends TuneTarget {
  private PreferredColorModel preferredColorModel;
  public void setPreferredColorModel(PreferredColorModel preferredColorModel) {
    this.preferredColorModel = preferredColorModel;
  }

  public PreferredColorModel getPreferredColorModel() {
    return preferredColorModel;
  }

  /**
   * PreferredColorTuneTarget
   * @param prefferedColorSpace ProfileColorSpace
   * @param prefferedCLUT ColorSpaceConnectedLUT 供CLUTOptimizeReverseModel計算使用
   * 若沒有要使用LChGrid, 則沒有必要使用此建構式, 則此變數可為null
   * @param limitColorSpace ProfileColorSpace
   */
  private PreferredColorTuneTarget(ProfileColorSpace prefferedColorSpace,
                                   ColorSpaceConnectedLUT prefferedCLUT,
                                   ProfileColorSpace limitColorSpace) {
    this.prefferedColorSpace = prefferedColorSpace;
    this.limitColorSpace = limitColorSpace;
    initGamutBoundaryDescriptor();
    if (null != prefferedCLUT) {
      this.prefferedCLUT = prefferedCLUT;
      reverseModel = new CLUTOptimizeReverseModel(this.prefferedCLUT);
      reverseModel.produceFactor();
    }
  }

  /**
   * 若不使用LChGrid, "優先建議"使用此建構式
   * @param prefferedColorSpace ProfileColorSpace
   * @param limitColorSpace ProfileColorSpace 用來明定可調整的區域, 方便優化用
   */
  public PreferredColorTuneTarget(ProfileColorSpace prefferedColorSpace,
                                  ProfileColorSpace limitColorSpace) {
    this(prefferedColorSpace, null, limitColorSpace);
  }

  /**
   * 若不使用LChGrid, 則沒有必要使用此建構式
   * @param preferredColorSpace PreferredColorSpace
   * @param limitColorSpace ProfileColorSpace
   * @deprecated
   */
  public PreferredColorTuneTarget(PreferredColorSpace preferredColorSpace,
                                  ProfileColorSpace limitColorSpace) {
    this(preferredColorSpace.pcs, preferredColorSpace.clut, limitColorSpace);
  }

  private void initGamutBoundaryDescriptor() {
    prefferedGBP = new GamutBoundaryPoint(prefferedColorSpace);
    prefferedGBP.calculateGamut();
    prefferedGBD = new GamutBoundary3DDescriptor(prefferedGBP,
                                                 FocalPoint.FocalType.None);
//    prefferedGBD = GamutBoundaryRGBDescriptor.getInstance(
//        GamutBoundaryRGBDescriptor.Style.D65Threshold, prefferedColorSpace);

    limitGBP = new GamutBoundaryPoint(limitColorSpace);
    limitGBP.calculateGamut();
    limitGBD = new GamutBoundary3DDescriptor(limitGBP,
                                             FocalPoint.FocalType.None);
  }

  private ColorSpaceConnectedLUT prefferedCLUT;
  private CLUTOptimizeReverseModel reverseModel;
  private ProfileColorSpace prefferedColorSpace;
  private ProfileColorSpace limitColorSpace;
  private GamutBoundaryPoint prefferedGBP; //計算出色域邊際
  private GamutBoundaryPoint limitGBP;
  private GamutBoundaryDescriptor prefferedGBD; //用來判斷是否超出色域
  private GamutBoundaryDescriptor limitGBD;

  /**
   * getTarget
   *
   * @param hue double
   * @param saturation double
   * @param value double
   * @return CIEXYZ
   */
  public CIEXYZ getTarget(double hue, double saturation, double value) {
    double[] PCSLChValues = getTargetLCh(hue, saturation, value).getValues();
    double[] deviceXYZValues = targetInColorAppearance ?
        limitColorSpace.toDeviceCIEXYZValues(PCSLChValues) :
        prefferedColorSpace.toDeviceCIEXYZValues(PCSLChValues);
    CIEXYZ XYZ = new CIEXYZ(deviceXYZValues);
    return XYZ;
  }

  private boolean targetInColorAppearance = true;
  public void setTargetInColorAppearance(boolean inColorAppearance) {
    this.targetInColorAppearance = inColorAppearance;
  }

  public CIELCh getTargetLCh(double hue, double saturation, double value) {
    HSV hsv = new HSV(RGB.ColorSpace.unknowRGB, new double[] {hue, saturation,
                      value});
    RGB rgb = hsv.toRGB();
    double[] PCSLChValues = prefferedColorSpace.toPCSCIELChValues(rgb.getValues(new double[
        3], RGB.MaxValue.Double1));
    CIELCh LCh = new CIELCh(PCSLChValues, CIELCh.Style.Lab);
    return LCh;
  }

  /**
   * getTarget
   *
   * @param hue double
   * @return CIEXYZ
   */
  public CIEXYZ getTarget(int hue) {
    throw new UnsupportedOperationException();
  }

  /**
   * getTuneSpot
   *
   * @param hue double
   * @return HSV
   */
  public HSV getTuneSpot(double hue) {
    throw new UnsupportedOperationException();
  }

  /**
   * getTuneSpots
   *
   * @param hue int
   * @return HSV[]
   */
  public HSV[] getTuneSpots(double hue) {

    switch (type) {

      case LChGrid: //有可能遇到Hue逆轉問題, 不建議使用
        System.err.println("No recommend use \"LChGrid\"");
        tuneSpots = getTuneSpotsFromLChDomain(hue);
        break;
      case HSV2LCHGrid:
        tuneSpots = getTuneSpotsFromHSV2LChDomain(hue);
        break;
      case HSVGrid: //優先使用
        tuneSpots = getTuneSpotsFromHSVDomain(hue);
        break;
    }
    return tuneSpots;
  }

//  public void setTuneSpots(int hue, HSV[] tuneSpots) {
//    tuneSpotsMap.put(hue, tuneSpots);
//  }

  private Map<Integer, HSV[]> tuneSpotsMap = new HashMap<Integer, HSV[]> ();
  private HSV[] tuneSpots;

//  private boolean tuneSpotsFromLCh = true;
//  public void setTuneSpotsFromLCh(boolean fromLCh) {
//    this.tuneSpotsFromLCh = fromLCh;
//  }

  public void setType(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  private Type type = Type.HSV2LCHGrid;
  public static enum Type {
    LChGrid, HSV2LCHGrid, HSVGrid
  }

  /**
   * 是否在優化的範圍內
   * @param hsv HSV
   * @return boolean
   */
  private boolean isInRange(HSV hsv) {
    return hsv.S < saturationRange[0] && hsv.S > saturationRange[1] &&
        hsv.V < valueRange[0] && hsv.V > valueRange[1];
  }

  private int[] saturationRange = new int[] {
      90, 10};
//      60, 40};
  private int[] valueRange = new int[] {
//      80, 33};
      90, 30};
//      60, 50};
  public void setSaturationRange(int start, int end) {
    saturationRange = new int[] {
        end, start};
  }

  public void setValueRange(int start, int end) {
    valueRange = new int[] {
        end, start};
  }

  private HSV[] getTuneSpotsFromHSVDomain(double hue) {
    List<HSV> hsvList = new ArrayList<HSV> ();
    double[] rgbValues = new double[3];
    for (int s = saturationRange[0]; s >= saturationRange[1]; s -= 10) {
      for (int v = valueRange[0]; v >= valueRange[1]; v -= 10) {
        HSV hsv = new HSV(RGB.ColorSpace.unknowRGB, new double[] {hue, s, v});
        RGB rgb = hsv.toRGB();
        if (skinFirstInHSVGrid && (hue == 0 || hue == 15 || hue == 30) &&
            !AutotuneUtils.isSkin1(rgb)) {
          continue;
        }

        rgb.getValues(rgbValues, RGB.MaxValue.Double1);
        double[] LChValues = limitColorSpace.toPCSCIELChValues(rgbValues);
        CIELCh LCh = new CIELCh(LChValues);
        if (!prefferedGBD.isOutOfGamut(LCh)) {
          hsvList.add(hsv);
        }
      }
    }
    int size = hsvList.size();
    HSV[] hsvArray = hsvList.toArray(new HSV[size]);
    return hsvArray;
  }

  private HSV[] getTuneSpotsFromHSV2LChDomain(double hue) {
    Interpolation1DLUT chromaBoundaryLUT = toChromaBoundaryLUT(hue,
        prefferedColorSpace, limitColorSpace);
    HSV[] hsvVGridArray = toHSVGridArray(chromaBoundaryLUT, hue);
    return hsvVGridArray;
  }

  private HSV[] getTuneSpotsFromLChDomain(double hue) {
    // 由於HSV與LCh的hue並非完全相關, 所以只好以中間值的HSV去找到LCh所對應的hue
    // 並且再以此hue去尋找LCh的plane
    // 中間值HSV : S=50, V=50
    HSV center = new HSV(RGB.ColorSpace.unknowRGB, new double[] {hue, 50, 50});
    double[] centerRGBValues = center.toRGB().getValues();
    double[] centerPCSLChValues = prefferedColorSpace.toPCSCIELChValues(
        centerRGBValues);
    double hueOfLCh = centerPCSLChValues[2];

    //基本上會有101個CIELCh, L=0~100
    double[] boundaryLCArray = getBoundaryLCArray(hueOfLCh);
    List<CIELCh> LChGridList = toLChGridList(boundaryLCArray, hueOfLCh);
    HSV[] tuneSpots = toPrefferedHSVArray(LChGridList);
    return tuneSpots;

  }

  /**
   * 算出hueOfLCh下hue plane兩個色域的交集邊界
   * @param hueOfLCh double
   * @return double[]
   */
  private double[] getBoundaryLCArray(double hueOfLCh) {
    //基本上會有101個CIELCh, L=0~100
    List<CIELCh>
        prefferedHuePlane = prefferedGBP.getHuePlane(hueOfLCh);
    List<CIELCh> limitHuePlane = limitGBP.getHuePlane(hueOfLCh);

    double[] boundaryLCArray = toBoundaryLCArray(prefferedHuePlane,
                                                 limitHuePlane);
    return boundaryLCArray;
  }

  /**
   * 從lightness 100~0的範圍裡, 均勻散佈LC的grid.
   * 然後以優化迭代的方式找到對應該LC的HSV
   * @param chromaBoundaryLUT Interpolation1DLUT
   * @param hueOfHSV double
   * @return HSV[]
   */
  private HSV[] toHSVGridArray(Interpolation1DLUT chromaBoundaryLUT,
                               double hueOfHSV) {
    List<HSV> hsvGridList = new LinkedList<HSV> ();
    HSV preHSV = new HSV(RGB.ColorSpace.unknowRGB, new double[] {hueOfHSV, 50,
                         50});
    for (int lightness = 100; lightness >= 0; lightness -= lightnessStep) {
      double boundaryChroma = chromaBoundaryLUT.getValue(lightness);
      for (int chroma = chromaStep; chroma <= boundaryChroma;
           chroma += chromaStep) {
        HSV hsv = findHSV(lightness, chroma, hueOfHSV, preHSV);
        if (isInRange(hsv)) {
          //找到的hsv若在範圍內就放到list
          hsvGridList.add(hsv);
          preHSV = hsv;
        }
      }
    }
    HSV[] hsvGridArray = new HSV[hsvGridList.size()];
    hsvGridList.toArray(hsvGridArray);
    return hsvGridArray;
  }

  /**
   * 從給定的lightness/chroma 找到對應的HSV.
   * 其中hue已知, 且對映著此處給的lightness/chroma, 所以不會有hue錯誤的疑慮.
   * @param lightness double
   * @param chroma double
   * @param hueOfHSV double
   * @param initHSV HSV
   * @return HSV
   */
  private HSV findHSV(double lightness, double chroma, double hueOfHSV,
                      HSV initHSV) {
    hsvFinder.setParameter(hueOfHSV, lightness, chroma);
    double[] initSVValues = new double[] {
        initHSV.S, initHSV.V};
    //設定S和V的限制
    min.addConstraint(0, -1, 0);
    min.addConstraint(0, 1, 100);
    min.addConstraint(1, -1, 0);
    min.addConstraint(1, 1, 100);
    min.nelderMead(hsvFinder, initSVValues);
    double[] svValues = min.getParamValues();
    double[] hsvValues = new double[] {
        hueOfHSV, svValues[0], svValues[1]};
    HSV hsv = new HSV(RGB.ColorSpace.unknowRGB, hsvValues);
    return hsv;
  }

  private Minimisation min = new Minimisation();
  private HSVFinder hsvFinder = new HSVFinder();
  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 用來尋找最接近 hue/lightnessOfCIELCh/chromaOfCIELCh的HSV
   *
   * <p>Copyright: Copyright (c) 2009</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author not attributable
   * @version 1.0
   */
  private class HSVFinder
      implements MinimisationFunction {
    private double hueOfHSV;
    private double[] LCValues = new double[2];
    public void setParameter(double hueOfHSV, double lightnessOfCIELCh,
                             double chromaOfCIELCh) {
      this.hueOfHSV = hueOfHSV;
      LCValues[0] = lightnessOfCIELCh;
      LCValues[1] = chromaOfCIELCh;
    }

    /**
     * function
     *
     * @param param double[]
     * @return double
     */
    public double function(double[] param) {
      double[] hsvValues = new double[] {
          hueOfHSV, param[0], param[1]};
      double[] rgbValues = HSV.toRGBValues(hsvValues);
      double[] pcsCIELChValues = prefferedColorSpace.toPCSCIELChValues(
          rgbValues);
      double rmsd = Maths.RMSD(LCValues, new double[] {pcsCIELChValues[0],
                               pcsCIELChValues[1]});
      return rmsd;
    }

  }

  /**
   * 計算出兩個ProfileColorSpace交集的色域邊際對照表(lightness->chroma)
   * @param hue double
   * @param colorSpace1 ProfileColorSpace
   * @param colorSpace2 ProfileColorSpace
   * @return Interpolation1DLUT
   */
  private static Interpolation1DLUT toChromaBoundaryLUT(double hue,
      ProfileColorSpace colorSpace1,
      ProfileColorSpace colorSpace2) {
    Interpolation1DLUT prefferedLut = toChromaBoundaryLUT(hue, colorSpace1);
    Interpolation1DLUT limitLut = toChromaBoundaryLUT(hue, colorSpace2);
    double[] lightnessArray = DoubleArray.buildX(0, 100, 101);
    double[] boundaryArray = new double[101];
    for (int x = 0; x <= 100; x++) {
      double prefferedBoundary = prefferedLut.getValue(x);
      double fixKey = limitLut.correctKeyInRange(x);
      double limitBoundary = limitLut.getValue(fixKey);
      //取最小值, 為交集
      boundaryArray[x] = Math.min(prefferedBoundary, limitBoundary);
    }
    Interpolation1DLUT result = new Interpolation1DLUT(lightnessArray,
        boundaryArray, Interpolation1DLUT.Algo.LINEAR);
    return result;
  }

  /**
   * 將colorSpace抽出指定hue的色域邊際對照表(lightness->chroma)
   * @param hue int
   * @param colorSpace ProfileColorSpace
   * @return Interpolation1DLUT
   */
  private static Interpolation1DLUT toChromaBoundaryLUT(double hue,
      ProfileColorSpace colorSpace) {
    double[] lightnessArray = new double[203];
    double[] chromaArray = new double[203];
    double[] rgbValues = new double[3];
    int index = 0;
    lightnessArray[index] = 0;
    chromaArray[index] = 0;
    index++;
    /**
     * |\
     * |  \ => S 0~100, V=100
     * |    \
     * |  / => V 100~0, S=100
     * |/
     */
    for (int v = 0; v <= 100; v++) {
      HSV hsv = new HSV(RGB.ColorSpace.unknowRGB, new double[] {hue, 100, v});
      hsv.toRGB().getValues(rgbValues, RGB.MaxValue.Double1);
      double[] pcsLChValues = colorSpace.toPCSCIELChValues(rgbValues);
      lightnessArray[index] = pcsLChValues[0];
      chromaArray[index] = pcsLChValues[1];
      index++;
    }

    for (int s = 100; s >= 0; s--) {
      HSV hsv = new HSV(RGB.ColorSpace.unknowRGB, new double[] {hue, s, 100});
      hsv.toRGB().getValues(rgbValues, RGB.MaxValue.Double1);
      double[] pcsLChValues = colorSpace.toPCSCIELChValues(rgbValues);
      lightnessArray[index] = pcsLChValues[0];
      chromaArray[index] = pcsLChValues[1];
      index++;
    }
    //產生 lightness和chroma的對照表, 為色域邊際
    Interpolation1DLUT lut = new Interpolation1DLUT(lightnessArray, chromaArray,
        Interpolation1DLUT.Algo.LINEAR);
    return lut;
  }

  private double[] toBoundaryLCArray(List<CIELCh>
      prefferedHuePlane, List<CIELCh> limitHuePlane) {
    int sizeOfHuePlane = prefferedHuePlane.size();
    double[] boundaryLCArray = new double[sizeOfHuePlane];
    for (int x = 0; x < sizeOfHuePlane; x++) {
      //撈出preffered和limit
      CIELCh preffered = prefferedHuePlane.get(x);
      CIELCh limit = limitHuePlane.get(x);
      //找到彼此交集的部份, 定義為要tune的色域邊際
      boundaryLCArray[x] = Math.min(preffered.C, limit.C);
    }
    return boundaryLCArray;
  }

  /**
   * 色域邊際轉為CIELCh grid
   * @param boundaryLCArray double[]
   * @param hueOfLCh double
   * @return List
   */
  private List<CIELCh> toLChGridList(double[] boundaryLCArray, double hueOfLCh) {
    //將色域邊際用為內插
    Interpolation1DLUT lut = new Interpolation1DLUT(DoubleArray.increment(101,
        0., 1.), boundaryLCArray, Interpolation1DLUT.Algo.LINEAR);
    List<CIELCh> LChGridList = new LinkedList<CIELCh> ();
    //每隔一個step設置一個LCh
    for (int lightness = 100; lightness >= 0; lightness -= lightnessStep) {
      double boundaryChroma = lut.getValue(lightness);
      for (int chroma = chromaStep; chroma <= boundaryChroma;
           chroma += chromaStep) {
        CIELCh LCh = new CIELCh(lightness, chroma, hueOfLCh);
        LChGridList.add(LCh);
      }
    }
    return LChGridList;
  }

  private HSV[] toPrefferedHSVArray(List<CIELCh> LChGridList) {
    if (null == reverseModel) {
      throw new IllegalArgumentException("null == reverseModel");
    }
    //將LCh grid轉換為HSV
    int sizeOfGrid = LChGridList.size();
    HSV[] tuneSpots = new HSV[sizeOfGrid];
    for (int x = 0; x < sizeOfGrid; x++) {
      CIELCh LCh = LChGridList.get(x); //PCS LCh
      double[] deviceXYZValues = prefferedColorSpace.toDeviceCIEXYZValues(LCh.
          getValues());
      CIEXYZ deviceXYZ = new CIEXYZ(deviceXYZValues);
      RGB rgb = reverseModel.getRGB(deviceXYZ, false);
      HSV hsv = new HSV(rgb);
      tuneSpots[x] = hsv;
    }
    return tuneSpots;
  }

  private int lightnessStep = 10;
  private int chromaStep = 10;
  public void setLightnessStep(int step) {
    this.lightnessStep = step;
  }

  public void setChromaStep(int step) {
    this.chromaStep = step;
  }

  public final static PreferredColorTuneTarget getInstance(LCDTarget
      preferredTarget, LCDTarget limitTarget, double percent, Type type) {
    CIEXYZ whiteXYZ = preferredTarget.getWhitePatch().getXYZ();

    PreferredColorSpace ppcs = ProfileColorSpaceUtils.
        getPreferredColorSpacee(RGB.ColorSpace.sRGB, preferredTarget,
                                percent, whiteXYZ);
    ProfileColorSpace limitPCS = ProfileColorSpaceUtils.
        getProfileColorSpaceFromRampTarget(limitTarget);

    PreferredColorTuneTarget tuneTarget = new PreferredColorTuneTarget(ppcs,
        limitPCS);
    tuneTarget.setType(type);
    return tuneTarget;
  }

  private boolean skinFirstInHSVGrid = true;
  public void setSkinFirstInHSVGrid(boolean skinFirst) {
    this.skinFirstInHSVGrid = skinFirst;
  }

//  public static class PreferredColorChecker {
//    private PreferredColorTuneTarget preferredColorTuneTarget;
//    private TuneParameter tuneParameter;
//    public PreferredColorChecker(PreferredColorTuneTarget
//                                 preferredColorTuneTarget,
//                                 TuneParameter tuneParameter) {
//      this.preferredColorTuneTarget = preferredColorTuneTarget;
//      this.tuneParameter = tuneParameter;
//    }
//
//    public void check() {
//
//    }
//  }

  public static void test(String[] args) {
    String mode = "Standard";
    LCDTarget preferredTarget = LCDTarget.Instance.getTest729FromAUOXLS(
        "prefered/Sharp LC-46LX1/Modes/" + mode + "/871.xls");
    LCDTarget rampTarget = LCDTarget.Instance.getFromAUORampXLS(
        "prefered/B156HW03/Dell WRGB.xls", LCDTarget.Number.Ramp256_6Bit);
    PreferredColorTuneTarget tuneTarget = getInstance(preferredTarget,
        rampTarget, 0, Type.HSVGrid);

    boolean plotHueIn3D = true;
    Plot3D plot = plotHueIn3D ? Plot3D.getInstance() : null;
    Plot3D plot2 = plotHueIn3D ? Plot3D.getInstance() : null;
    for (int x = 0; x < 360; x += 15) {

      HSV[] tuneSpots = tuneTarget.getTuneSpots(x);
      java.awt.Color c = HSV.getLineColor(x);

      int size = tuneSpots.length;
      double[][] hsvArray = new double[size][];
      double[][] LChArray = new double[size][];
      int index = 0;

      for (HSV hsv : tuneSpots) {
        if (plotHueIn3D) {
          plot.addCacheScatterPlot(Integer.toString(x), c, hsv.getValues());
          double[] lchValues = tuneTarget.getTargetLCh(hsv.H, hsv.S, hsv.V).
              getValues();
          plot2.addCacheScatterPlot(Integer.toString(x), c, lchValues);

          hsvArray[index] = hsv.getValues();
          LChArray[index] = lchValues;
          index++;
        }
      }

      ASCIIFile.writeDoubleArray(new File(Integer.toString(x) +
                                          "HSV.dat"), hsvArray);
      ASCIIFile.writeDoubleArray(new File(Integer.toString(x) +
                                          "LCh.dat"), LChArray);
    }
    if (plotHueIn3D) {
      plot.setVisible();
      plot.setAxisLabels("H", "S", "V");
      plot.setFixedBounds(0, 0, 360);
      plot2.setVisible();
      plot2.setAxisLabels("L*", "C*", "h*");
      plot2.setFixedBounds(2, 0, 360);
    }

  }
}
