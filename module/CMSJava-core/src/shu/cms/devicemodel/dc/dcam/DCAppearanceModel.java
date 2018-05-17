package shu.cms.devicemodel.dc.dcam;

import java.util.*;

import java.awt.image.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorspace.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.independ.IPT;
import shu.cms.dc.*;
import shu.cms.devicemodel.dc.DCModel.*;
import shu.cms.gma.gbd.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.cms.hvs.cam.ciecam02.CIECAM02;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.cms.image.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.math.lut.*;
import shu.util.*;
import shu.util.log.*;
import shu.cms.devicemodel.dc.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class DCAppearanceModel
    extends DCModel {
  public DCAppearanceModel(DCModelFactor dcModelFactor) {
    super(dcModelFactor);
  }

  public static enum Style {
    IPT, CIELab, CIECAM02
  }

  public final static GamutBoundaryRGBDescriptor
      getGBDDescriptorInstance(RGB.ColorSpace rgbColorSpace,
                               Style style) {
    if (rgbColorSpace.equals(RGB.ColorSpace.sRGB)) {
      switch (style) {
        case CIELab:
          return GamutBoundaryRGBDescriptor.loadD65ThresholdDescriptor(CMSDir.
              Reference.GBD + "/sRGB-CIELab.gbd");
        case CIECAM02:
          return GamutBoundaryRGBDescriptor.loadD65ThresholdDescriptor(CMSDir.
              Reference.GBD + "/sRGB-CIECAM02.gbd");
        case IPT:
          return GamutBoundaryRGBDescriptor.loadD65ThresholdDescriptor(CMSDir.
              Reference.GBD + "/sRGB-IPT.gbd");
      }
    }
    return null;
  }

  //轉到色外貌空間下所使用的環境參數
  protected ViewingConditions targetVC;
  protected ViewingConditions vc;

  protected LChPair[] LChPairArray;
  protected LChPair[] hueSortLChPairArray;
  protected HuePlane huePlane;
  public HuePlane getHuePlane() {
    return huePlane;
  }

  protected LChPair[][] getLChPairHuePlane() {
    return huePlane.getLChPairHuePlane();
  }

  protected List<Patch> getHuePlanePatchList() {
    LChPair[][] huePlane = getLChPairHuePlane();
    int size = huePlane.length;
    List<Patch> list = new ArrayList<Patch> (size);
    CIEXYZ white = (CIEXYZ) Illuminant.D65WhitePoint.clone();
    white.normalizeY100();

    for (int x = 0; x < size; x++) {
      int planeSize = huePlane[x].length;
      for (int y = 0; y < planeSize; y++) {
        LChPair pair = huePlane[x][y];
        Patch p = pair.patch;
        CIELab Lab = new CIELab(p.getXYZ(), white);
        Patch.Operator.setLab(p, Lab);
        list.add(pair.patch);
      }
    }
    return list;
  }

  protected final static class HuePlaneComparator
      implements Comparator {

    public static enum Style {
      Hue, Chroma
    }

    protected HuePlaneComparator(Style style) {
      this.style = style;
    }

    private Style style;
    private ColorSpaceComparator comparator = new ColorSpaceComparator();

    /**
     * Compares its two arguments for order.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *   argument is less than, equal to, or greater than the second.
     */
    public int compare(Object o1, Object o2) {
      LChPair[] pair1 = ( (LChPair[]) o1);
      LChPair[] pair2 = ( (LChPair[]) o2);

      CIELCh LCh1 = pair1[0].targetLCh;
      CIELCh LCh2 = pair2[0].targetLCh;
      switch (style) {
        case Hue:
          comparator.setCompareIndex(2);
          return comparator.compare(LCh1, LCh2);
//          return Double.compare(LCh1.h, LCh2.h);
        case Chroma:
          comparator.setCompareIndex(1);
          return comparator.compare(LCh1, LCh2);
//          return Double.compare(LCh1.C, LCh2.C);
        default:
          throw new IllegalStateException("style is not assign");
      }
    }

    /**
     * Indicates whether some other object is &quot;equal to&quot; this
     * comparator.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> only if the specified object is also a
     *   comparator and it imposes the same ordering as this comparator.
     */
    public boolean equals(Object obj) {
      return false;
    }

  }

  public class HuePlane {
    private LChPair[][] LChPairHuePlane;
    private LChPair[][] circularLChPairHuePlane;
    /**
     * 將cameraHue獨立抽取出來, 方便搜尋
     */
    private double[] cameraHue;
    private int[] ignoreIndex = null;
    private boolean onlySameHue = false;

    public double[] getCameraHue() {
      return cameraHue;
    }

    public LChPair[][] getLChPairHuePlane() {
      return LChPairHuePlane;
    }

    /**
     * 找到cameraHue裡最接近的hue其index
     * @param cameraLCh CIELCh
     * @return int
     */
    protected int getClosedIndexByCamera(CIELCh cameraLCh) {
      return Searcher.leftNearBinarySearch(cameraHue, cameraLCh.h);
    }

    /**
     * 相鄰兩個hue的LChPair
     * @param cameraLCh CIELCh
     * @return LChPair[][]
     */
    protected LChPair[][] getCloseLChPair(CIELCh cameraLCh) {
      int index = getClosedIndexByCamera(cameraLCh);
      LChPair[][] result = new LChPair[2][];
      result[0] = circularLChPairHuePlane[index];
      result[1] = circularLChPairHuePlane[index + 1];
      return result;
    }

    /**
     * target的L修正, 修正為camera L(不完全)
     * @param lightnessCorrector Interpolation1DLUT
     */
    protected void targetLightnessCorrect(Interpolation1DLUT lightnessCorrector) {
      int size = circularLChPairHuePlane.length;
      for (int x = 0; x < size; x++) {
        int pairSize = circularLChPairHuePlane[x].length;
        for (int y = 0; y < pairSize; y++) {
          CIELCh targetLCh = circularLChPairHuePlane[x][y].targetLCh;
          targetLCh.L = lightnessCorrector.getValue(targetLCh.L);
        }
      }
    }

    protected boolean checkHueSort() {
      int size = circularLChPairHuePlane.length;
      for (int x = 0; x < size - 1; x++) {
        if (circularLChPairHuePlane[x][0].cameraLCh.h >
            circularLChPairHuePlane[x + 1][0].cameraLCh.h) {
          return false;
        }
      }
      return true;
    }

    public int size() {
      return circularLChPairHuePlane.length;
    }

    public LChPair[] get(int index) {
      return circularLChPairHuePlane[index];
    }

    protected HuePlane(LChPair[] LChPairArray, int[] grayScaleIndex,
                       int[][] sameHueIndex) {
      this(LChPairArray, grayScaleIndex, sameHueIndex, null, false);
    }

    /**
     *
     * @param LChPairArray LChPair[]
     * @param grayScaleIndex int[]
     * @param sameHueIndex int[][]
     * @param ignoreIndex int[]
     * @param onlySameHue boolean 是否只處理同一色相區的資訊
     */
    protected HuePlane(LChPair[] LChPairArray, int[] grayScaleIndex,
                       int[][] sameHueIndex, int[] ignoreIndex,
                       boolean onlySameHue) {
      this.ignoreIndex = ignoreIndex;
      this.onlySameHue = onlySameHue;
      initHuePlane(LChPairArray, grayScaleIndex, sameHueIndex);
      initCircularLChPairArray(LChPairHuePlane);
    }

    protected void initCircularLChPairArray(LChPair[][] LChPairHuePlane) {
      int size = LChPairHuePlane.length;
      int circularSize = size + 2;
      circularLChPairHuePlane = new LChPair[circularSize][];

      for (int x = 0; x < size; x++) {
        LChPair[] pair = LChPairHuePlane[x];
        int pairSize = pair.length;
        circularLChPairHuePlane[x + 1] = Arrays.copyOf(pair, pairSize);
      }

      int endSize = LChPairHuePlane[size - 1].length;
      int startSize = LChPairHuePlane[0].length;
      circularLChPairHuePlane[0] = new LChPair[endSize];
      for (int x = 0; x < endSize; x++) {
        circularLChPairHuePlane[0][x] = (LChPair) LChPairHuePlane[size -
            1][x].clone();
        circularLChPairHuePlane[0][x].cameraLCh.h -= 360;
        circularLChPairHuePlane[0][x].targetLCh.h -= 360;
      }
      circularLChPairHuePlane[circularSize - 1] = new LChPair[startSize];
      for (int x = 0; x < startSize; x++) {
        circularLChPairHuePlane[circularSize - 1][x] =
            (LChPair) LChPairHuePlane[0][x].clone();
        circularLChPairHuePlane[circularSize - 1][x].cameraLCh.h += 360;
        circularLChPairHuePlane[circularSize - 1][x].targetLCh.h += 360;
      }

      cameraHue = new double[circularSize];
      for (int x = 0; x < circularSize; x++) {
        cameraHue[x] = circularLChPairHuePlane[x][0].cameraLCh.h;
      }
    }

    /**
     * 初始化HuePlane
     * @param LChPairArray LChPair[]
     * @param grayScaleIndex int[]
     * @param sameHueIndex int[][]
     */
    protected void initHuePlane(LChPair[] LChPairArray, int[] grayScaleIndex,
                                int[][] sameHueIndex) {
      int pairSize = LChPairArray.length;
      int grayScaleSize = grayScaleIndex.length;
      int sameHueSize = sameHueIndex.length;
      boolean[] used = new boolean[pairSize];
      List<LChPair[]> pairArray = new ArrayList<LChPair[]> ();

      //先把灰階去掉
      for (int x = 0; x < grayScaleSize; x++) {
        int index = grayScaleIndex[x];
        used[index] = true;
      }

      //預期要略過的
      if (ignoreIndex != null) {
        int size = ignoreIndex.length;
        for (int x = 0; x < size; x++) {
          int index = ignoreIndex[x];
          used[index] = true;
        }
      }

      for (int x = 0; x < pairSize; x++) {
        if (used[x] == true) {
          //處理過以及灰階 略過
          continue;
        }

        boolean hasSameHue = false;
        for (int y = 0; y < sameHueSize; y++) {
          int[] sameHue = sameHueIndex[y];
          if (Searcher.sequentialSearch(sameHue, x) >= 0) {
            //有其他色塊也是同一個hue
            int size = sameHue.length;
            LChPair[] sameHuePlane = new LChPair[size];

            for (int z = 0; z < size; z++) {
              int index = sameHue[z];
              sameHuePlane[z] = (LChPair) LChPairArray[index].clone();
              used[index] = true;

            }

            //照彩度排序
            Arrays.sort(sameHuePlane, chromaComparator);

            //==================================================================
            // 將超出色域的cut掉
            //==================================================================
            List<LChPair> pairList = new ArrayList<LChPair> (size);
            double cameraHue = 0;
            double targetHue = 0;

            for (int z = 0; z < size; z++) {
              LChPair pair = sameHuePlane[z];
              RGB rgb = pair.patch.getRGB();
              if (rgb.isLegalAfter8BitQuantization()) {
                rgb.quantization(RGB.MaxValue.Int8Bit);
                pairList.add(pair);
                double cameraH = pair.cameraLCh.h;
                double targetH = pair.targetLCh.h;
                if (Math.abs(cameraH - targetH) > 90) {
                  continue;
                }
                cameraHue += cameraH;
                targetHue += targetH;
              }
            }

            int filterSize = pairList.size();
            LChPair[] filter = new LChPair[filterSize];
            pairList.toArray(filter);

            //==================================================================
            // 取色相的平均來統一色相
            //==================================================================
            cameraHue /= filterSize;
            targetHue /= filterSize;
            for (int z = 0; z < filterSize; z++) {
              filter[z].cameraLCh.h = cameraHue;
              filter[z].targetLCh.h = targetHue;
            }
            //==================================================================

            pairArray.add(filter);
            hasSameHue = true;
          }
        }
        if (!hasSameHue && !onlySameHue) {
          //沒有與其他色塊同一個hue
          LChPair[] sameHuePlane = new LChPair[1];
          sameHuePlane[0] = (LChPair) LChPairArray[x].clone();
          pairArray.add(sameHuePlane);
        }
      }

      this.LChPairHuePlane = list2Array(pairArray);
      Arrays.sort(LChPairHuePlane, huePlaneComparator);
    }
  }

  protected final static LChPair[][] list2Array(List < LChPair[] > pairArray) {
    int size = pairArray.size();
    LChPair[][] array = new LChPair[size][];
    for (int x = 0; x < size; x++) {
      array[x] = pairArray.get(x);
    }
    return array;
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 記載camera和target的LCh
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static class LChPair {
    protected final static LChPair[] filterGrayScale(LChPair[] LChPairArray,
        int[] grayScaleIndex) {
      int size = grayScaleIndex.length;
      LChPair[] result = new LChPair[size];

      for (int x = 0; x < size; x++) {
        int index = grayScaleIndex[x];
        index = IGNORE_DARK_SKIN ? index - 1 : index;
        result[x] = LChPairArray[index];
      }

      return result;
    }

    protected final static LChPair[] filterNonGrayScale(LChPair[] LChPairArray,
        int[] grayScaleIndex) {
      int graySize = grayScaleIndex.length;
      int size = LChPairArray.length;
      int nonGraySize = size - graySize;
      LChPair[] nonGrayLChPairArray = new LChPair[nonGraySize];
      int index = 0;

      //取出除了中性色塊的hue
      for (int x = 0; x < size; x++) {
        if (Searcher.sequentialSearch(grayScaleIndex,
                                      IGNORE_DARK_SKIN ? x + 1 : x) <
            0) {
          nonGrayLChPairArray[index] = (LChPair) LChPairArray[x].clone();
          index++;
        }
      }
      return nonGrayLChPairArray;
    }

    protected LChPair(CIELCh LCh) {
      this.targetLCh = LCh;
      this.cameraLCh = LCh;
    }

    protected LChPair(Patch patch, CIELCh targetLCh, CIELCh cameraLCh,
                      double maximumChroma) {
      this.patch = patch;
      this.targetLCh = targetLCh;
      this.cameraLCh = cameraLCh;
      this.maximumChroma = maximumChroma;

      this.targetChromaRatio = targetLCh.C / maximumChroma;
      this.cameraChromaRatio = cameraLCh.C / maximumChroma;

    }

    protected boolean clone = false;
    protected boolean similar = false;
    public Patch patch;
    public CIELCh cameraLCh;
    public CIELCh targetLCh;
    protected double maximumChroma;
    protected double targetChromaRatio;
    protected double cameraChromaRatio;

    /**
     * Creates and returns a copy of this object.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not
     *   support the <code>Cloneable</code> interface. Subclasses that
     *   override the <code>clone</code> method can also throw this exception
     *   to indicate that an instance cannot be cloned.
     */
    protected Object clone() {
      LChPair clone = new LChPair(patch, (CIELCh) targetLCh.clone(),
                                  (CIELCh) cameraLCh.clone(), maximumChroma);
      clone.similar = this.similar;
      clone.clone = true;
      return clone;
    }
  }

  protected LChPair[] getColosedLCh(CIELCh LCh) {
    LChPair pair = new LChPair(LCh);
    int index = Searcher.leftNearBinarySearch(hueSortLChPairArray, pair,
                                              hueComparator);
    LChPair[] clorseLChPair = new LChPair[4];
    int size = hueSortLChPairArray.length;

    if (index < 1) {
      System.arraycopy(hueSortLChPairArray, index, clorseLChPair, 1, 3);

      //第一個用最後一個補
      clorseLChPair[0] = (LChPair) hueSortLChPairArray[size - 1].clone();
    }
    else {
      int leak = (index + 2 >= size) ? size - index - 1 : 0;
      System.arraycopy(hueSortLChPairArray, index - 1, clorseLChPair, 0,
                       4 - leak);
      if (leak > 0) {
        System.arraycopy(hueSortLChPairArray, 2, clorseLChPair, 4 - leak,
                         leak);
        for (int x = 4 - leak; x < 4; x++) {
          clorseLChPair[x] = (LChPair) hueSortLChPairArray[x].clone();
          clorseLChPair[x].targetLCh.h += 360;
          clorseLChPair[x].cameraLCh.h += 360;
        }
      }
    }
    return clorseLChPair;
  }

  protected Style style;
  protected RGB.ColorSpace rgbColorSpace;
  protected CIEXYZ rgbColorSpaceWhite;
  protected LChPairComparator hueComparator = new LChPairComparator(
      LChPairComparator.Style.Hue);
  protected LChPairComparator chromaComparator = new LChPairComparator(
      LChPairComparator.Style.Chroma);
  protected HuePlaneComparator huePlaneComparator = new HuePlaneComparator(
      HuePlaneComparator.Style.Hue);
  protected GamutBoundaryRGBDescriptor gbd;

  public DCAppearanceModel(DCTarget dcTarget, boolean doGammaCorrect,
                           boolean targetNormalize, Style style,
                           RGB.ColorSpace rgbColorSpace,
                           GamutBoundaryRGBDescriptor gbd) {
    super(dcTarget, doGammaCorrect, targetNormalize);
    this.style = style;

    //==========================================================================
    // camera
    //==========================================================================
    this.rgbColorSpace = rgbColorSpace;
    this.rgbColorSpaceWhite = rgbColorSpace.getReferenceWhiteXYZ();
    this.vc = new ViewingConditions(rgbColorSpaceWhite, 0.1, 20,
                                    Surround.Dim,
                                    "Dim");
    this.cam = new CIECAM02(vc);
    //==========================================================================

    this.gbd = gbd;
    if (gbd instanceof GamutBoundaryRGBDescriptor.D65ThresholdDescriptor) {
      ( (GamutBoundaryRGBDescriptor.D65ThresholdDescriptor) gbd).
          setXYZValuesRetriever(xyzRetriever);
    }
  }

  public DCAppearanceModel(DCTarget dcTarget, Style style,
                           RGB.ColorSpace rgbColorSpace,
                           GamutBoundaryRGBDescriptor gbd) {
    this(dcTarget, false, false, style, rgbColorSpace, gbd);
  }

  public DCAppearanceModel(DCTarget dcTarget, Style style,
                           RGB.ColorSpace rgbColorSpace) {
    this(dcTarget, false, false, style, rgbColorSpace, null);
    ProfileColorSpace pcs = ProfileColorSpace.Instance.get(rgbColorSpace);
    gbd = GamutBoundaryRGBDescriptor.getInstance(GamutBoundaryRGBDescriptor.
                                                 Style.D65Threshold, pcs);
    if (gbd instanceof GamutBoundaryRGBDescriptor.D65ThresholdDescriptor) {
      ( (GamutBoundaryRGBDescriptor.D65ThresholdDescriptor) gbd).
          setXYZValuesRetriever(xyzRetriever);
    }
  }

  protected int[] ignoreIndex = null;

  protected LChPair[] produceLChPairArray(List<Patch> patchList) {
    int size = patchList.size();
    size = IGNORE_DARK_SKIN ? size - 1 : size;

    LChPair[] LChPairArray = new LChPair[size];

    for (int x = 0; x < size; x++) {
      int index = IGNORE_DARK_SKIN ? x + 1 : x;
      Patch p = patchList.get(index);
      CIEXYZ targetXYZ = p.getNormalizedXYZ();
      CIEXYZ cameraXYZ = p.getRGB().toXYZ(rgbColorSpace);
      CIELCh targetLCh = getLCh(targetXYZ);
      CIELCh cameraLCh = getLCh(cameraXYZ);
      double maxChroma = gbd.getBoundaryLCh(cameraLCh).C;
      LChPair pair = new LChPair(p, targetLCh, cameraLCh, maxChroma);
      LChPairArray[x] = pair;

    }

    return LChPairArray;
  }

  protected CIECAM02 cam = null;

  /**
   * 取得target的XYZ對映的LCh
   * @param XYZ CIEXYZ
   * @return LChConvertible
   */
  protected CIELCh getLCh(CIEXYZ XYZ) {
    LChConvertible convertible = null;

    switch (style) {
      case IPT: {
//        convertible = new IPT(XYZ);
        convertible = IPT.fromXYZ(XYZ);
        ( (IPT) convertible).scaleToCIELab();
        break;
      }
      case CIELab: {
        convertible = new CIELab(XYZ, Illuminant.D65WhitePoint);
        break;
      }
      case CIECAM02: {
        convertible = cam.forward(XYZ);
        break;
      }
    }
    return new CIELCh(convertible);
  }

  public XYZRetriever getXYZRetriever() {
    return xyzRetriever;
  }

  protected XYZRetriever xyzRetriever = new XYZRetriever();
  protected class XYZRetriever
      implements CIELCh.XYZValuesRetriever {
    protected CIELCh LCh;
    protected CIEXYZ XYZ;
    protected XYZRetriever() {
      LCh = new CIELCh();
      LCh.setWhite(Illuminant.D65WhitePoint);
      XYZ = new CIEXYZ(new double[] {0, 0, 0}, NormalizeY.Normal1);
    }

    public double[] getXYZValues(double[] LChValues) {
      LCh.setValues(LChValues);
      CIEXYZ XYZ = getXYZ(LCh);
      return XYZ.getValues();
    }

    public double[] getLChValues(double[] XYZValues) {
      XYZ.setValues(XYZValues);
      CIELCh LCh = getLCh(XYZ);
      return LCh.getValues();
    }
  }

  protected CIEXYZ getXYZ(CIELCh LCh) {
    CIEXYZ XYZ = null;
    switch (style) {
      case IPT: {
        IPT ipt = new IPT(LCh);
        ipt.recoverCIELabScale();
        XYZ = ipt.toXYZ();
        break;
      }
      case CIELab: {
        CIELab Lab = new CIELab(LCh);
        XYZ = Lab.toXYZ();
        break;
      }
      case CIECAM02: {
        CIECAM02Color color = new CIECAM02Color(LCh);
        XYZ = cam.inverse(color);
        XYZ.normalize(NormalizeY.Normal1);
        break;
      }
    }
    return XYZ;
  }

  protected DCAppearanceModel() {
    super();
  }

  /**
   * _getRGB
   * 從target XYZ->camera RGB
   *
   * @param XYZ CIEXYZ
   * @return RGB
   */
  protected RGB _getRGB(CIEXYZ XYZ) {
    if (XYZ.isBlack()) {
      return new RGB(RGB.ColorSpace.unknowRGB, RGB.MaxValue.Double1);
    }
    //target LCh
    CIELCh LCh = getLCh(XYZ);

    //==========================================================================
    // correct
    //==========================================================================
    //ICh->I'Ch ,camera的L(不完全)
    //修正1 明度
    if (doLightnessCorrect) {
      LCh.L = lightnessCorrector.correctKeyInRange(LCh.L);
      LCh.L = lightnessCorrector.getValue(LCh.L);
    }
    if (doHueCorrect) {
      //I'Ch->I'Ch' , camera的h
      //修正2 色相
      LCh.h = hueCorrector.getValue(LCh.h);
      LCh.h %= 360;
      LCh.h = (LCh.h < 0) ? LCh.h + 360 : LCh.h;
    }

    if (BY_HUE_PLANE) {
      //以camera LCh的h去找
      LChPair[][] closed = huePlane.getCloseLChPair(LCh);
      if (doLightnessCorrectInHue) {
        //I'Ch'->I"Ch'
        //修正3 特定色相的明度
        LCh.L = getForwardLightnessInHue(LCh, closed);
      }
      if (doChromaCorrect) {
        //I"Ch'->I"C'h' , target.C->camera.C
        //修正4 特性色相的彩度
        LCh.C = getForwardChromaInHue(LCh, closed);
      }
    }
    else {
      //以camera LCh的h去找
      LChPair[] closed4LCh = getColosedLCh(LCh);
      //I"Ch'->I"C'h'
      LCh.C = getForwardChromaInHue(LCh, closed4LCh);
    }

    //==========================================================================

    //轉換為camera XYZ
    CIEXYZ cameraXYZ = getXYZ(LCh);
    cameraXYZ.normalize(NormalizeY.Normal1);
    RGB result = RGB.fromXYZ(cameraXYZ, rgbColorSpace);
    result.rationalize();
    if (GRAY_BALANCE_WITH_CONVERT) {
      grayBalancer.unGrayBalance(result);
    }
    return result;
  }

  /**
   * 依照hue局部調整明度 (for HuePlane版)
   * @param LCh CIELCh camera的hue,camera的L(部分/算target)
   * @param colosedLCh LChPair[][]
   * @return double
   */
  protected double getForwardLightnessInHue(CIELCh LCh, LChPair[][] colosedLCh) {
    double[] predictLightness = new double[2];
    double[] hue = new double[2];

    for (int x = 0; x < 2; x++) {
      hue[x] = colosedLCh[x][0].cameraLCh.h;
      int size = colosedLCh[x].length;
      double[] targetLightness = new double[size + 2];
      double[] cameraLightness = new double[size + 2];

      for (int y = 0; y < size; y++) {
        targetLightness[y + 1] = colosedLCh[x][y].targetLCh.L;
        cameraLightness[y + 1] = colosedLCh[x][y].cameraLCh.L;
      }
      targetLightness[size + 1] = 100;
      cameraLightness[size + 1] = 100;
      Arrays.sort(targetLightness);
      Arrays.sort(cameraLightness);

      predictLightness[x] = Interpolation.linear(targetLightness,
                                                 cameraLightness, LCh.L);
    }

    return Interpolation.linear(hue, predictLightness, LCh.h);
  }

  /**
   * 依照hue局部反調整明度 (for HuePlane版)
   * @param LCh CIELCh camera的L/h
   * @param colosedLCh LChPair[][]
   * @return double
   */
  protected double getInverseLightnessInHue(CIELCh LCh, LChPair[][] colosedLCh) {
    double[] predictLightness = new double[2];
    double[] hue = new double[2];

    for (int x = 0; x < 2; x++) {
      hue[x] = colosedLCh[x][0].cameraLCh.h;
      int size = colosedLCh[x].length;
      double[] targetLightness = new double[size + 2];
      double[] cameraLightness = new double[size + 2];

      for (int y = 0; y < size; y++) {
        targetLightness[y + 1] = colosedLCh[x][y].targetLCh.L;
        cameraLightness[y + 1] = colosedLCh[x][y].cameraLCh.L;
      }
      targetLightness[size + 1] = 100;
      cameraLightness[size + 1] = 100;
      Arrays.sort(targetLightness);
      Arrays.sort(cameraLightness);

      predictLightness[x] = Interpolation.linear(cameraLightness,
                                                 targetLightness, LCh.L);
    }

    return Interpolation.linear(hue, predictLightness, LCh.h);
  }

  /**
   * 將chroma反向處理
   * @param LCh CIELCh
   * @param colosedLCh LChPair[]
   * @return double
   */
  protected double getInverseChromaInHue(CIELCh LCh, LChPair[] colosedLCh) {
    double[] predictChroma = new double[2];
    double[] hue = new double[2];

    double maxChroma = gbd.getBoundaryLCh(LCh).C;
    double chromaRatio = LCh.C / maxChroma;
    chromaRatio = chromaRatio > 1 ? 1 : chromaRatio;

    for (int x = 0; x < 2; x++) {
      hue[x] = colosedLCh[x + 1].targetLCh.h;

      double targetChromaRatio = colosedLCh[x + 1].targetChromaRatio;
      double cameraChromaRatio = colosedLCh[x + 1].cameraChromaRatio;

      if (chromaRatio > cameraChromaRatio) {
        double predictRatio = Interpolation.linear(cameraChromaRatio, 1,
            targetChromaRatio, 1, chromaRatio);
        predictChroma[x] = LCh.C * (predictRatio / chromaRatio);
      }
      else {
        double predictRatio = Interpolation.linear(0, cameraChromaRatio, 0,
            targetChromaRatio, chromaRatio);
        predictChroma[x] = LCh.C * (predictRatio / chromaRatio);
      }
    }

    return Interpolation.linear2(hue, predictChroma, LCh.h);
  }

  /**
   * 依色相修正彩度的前導模式 (for HuePlane版)
   * @param LCh CIELCh target的C, camera的h
   * @param colosedLCh LChPair[][]
   * @return double
   */
  protected double getForwardChromaInHue(CIELCh LCh, LChPair[][] colosedLCh) {
    double[] predictChroma = new double[2];
    double[] hue = new double[2];

    //chroma將會因hue而異, 以camera.h找到max Chroma
    double maxChroma = gbd.getBoundaryLCh(LCh).C;
    double chromaRatio = LCh.C / maxChroma;
    chromaRatio = chromaRatio > 1 ? 1 : chromaRatio;

    for (int x = 0; x < 2; x++) {
      hue[x] = colosedLCh[x][0].cameraLCh.h;
      int size = colosedLCh[x].length;
      double[] targetChromaRatio = new double[size + 2];
      double[] cameraChromaRatio = new double[size + 2];

      for (int y = 0; y < size; y++) {
        targetChromaRatio[y + 1] = colosedLCh[x][y].targetChromaRatio;
        cameraChromaRatio[y + 1] = colosedLCh[x][y].cameraChromaRatio;
      }
      targetChromaRatio[size + 1] = 1;
      cameraChromaRatio[size + 1] = 1;

      double predictRatio = Interpolation.linear(targetChromaRatio,
                                                 cameraChromaRatio, chromaRatio);
      predictChroma[x] = LCh.C * (predictRatio / chromaRatio);
    }
    return Interpolation.linear(hue, predictChroma, LCh.h);
  }

  /**
   * 依色相修正彩度的反導模式 (for HuePlane版)
   * @param LCh CIELCh camera的LCh
   * @param colosedLCh LChPair[][]
   * @return double
   */
  protected double getInverseChromaInHue(CIELCh LCh, LChPair[][] colosedLCh) {
    double[] predictChroma = new double[2];
    double[] hue = new double[2];

    //chroma將會因hue而異, 以camera.h找到max Chroma
    double maxChroma = gbd.getBoundaryLCh(LCh).C;
    double chromaRatio = LCh.C / maxChroma;
    chromaRatio = chromaRatio > 1 ? 1 : chromaRatio;

    for (int x = 0; x < 2; x++) {
      hue[x] = colosedLCh[x][0].cameraLCh.h;
      int size = colosedLCh[x].length;
      double[] targetChromaRatio = new double[size + 2];
      double[] cameraChromaRatio = new double[size + 2];

      for (int y = 0; y < size; y++) {
        targetChromaRatio[y + 1] = colosedLCh[x][y].targetChromaRatio;
        cameraChromaRatio[y + 1] = colosedLCh[x][y].cameraChromaRatio;
      }
      targetChromaRatio[size + 1] = 1;
      cameraChromaRatio[size + 1] = 1;

      double predictRatio = Interpolation.linear(cameraChromaRatio,
                                                 targetChromaRatio, chromaRatio);
      predictChroma[x] = LCh.C * (predictRatio / chromaRatio);
    }
    return Interpolation.linear(hue, predictChroma, LCh.h);
  }

  /**
   * 依色相修正彩度的前導模式 (for舊版)
   * @param LCh CIELCh
   * @param colosedLCh LChPair[]
   * @return double
   */
  protected double getForwardChromaInHue(CIELCh LCh, LChPair[] colosedLCh) {
    double[] predictChroma = new double[2];
    double[] hue = new double[2];

    double maxChroma = gbd.getBoundaryLCh(LCh).C;
    double chromaRatio = LCh.C / maxChroma;
    chromaRatio = chromaRatio > 1 ? 1 : chromaRatio;

    for (int x = 0; x < 2; x++) {
      hue[x] = colosedLCh[x + 1].targetLCh.h;

      double targetChromaRatio = colosedLCh[x + 1].targetChromaRatio;
      double cameraChromaRatio = colosedLCh[x + 1].cameraChromaRatio;

      if (chromaRatio > targetChromaRatio) {
        double predictRatio = Interpolation.linear(targetChromaRatio, 1,
            cameraChromaRatio, 1, chromaRatio);
        predictChroma[x] = LCh.C * (predictRatio / chromaRatio);
      }
      else {
        double predictRatio = Interpolation.linear(0, targetChromaRatio, 0,
            cameraChromaRatio, chromaRatio);
        predictChroma[x] = LCh.C * (predictRatio / chromaRatio);
      }
    }

    return Interpolation.linear2(hue, predictChroma, LCh.h);
  }

  /**
   * 計算XYZ
   * 從camera RGB->target XYZ
   *
   * @param rgb RGB
   * @return CIEXYZ
   */
  protected CIEXYZ _getXYZ(RGB rgb) {
    if (rgb.isBlack()) {
      CIEXYZ XYZ = new CIEXYZ(new double[] {0, 0, 0}, NormalizeY.Normal1);
      return XYZ;
    }
    if (GRAY_BALANCE_WITH_CONVERT) {
      rgb = grayBalancer.grayBalance(rgb);
    }
    CIEXYZ XYZ = RGB.toXYZ(rgb, rgbColorSpace);
    //camera LCh
    CIELCh LCh = getLCh(XYZ);

    //==========================================================================
    // uncorrect
    //==========================================================================
    if (BY_HUE_PLANE) {
      //以camera LCh的h去找
      LChPair[][] closed = huePlane.getCloseLChPair(LCh);
      if (doChromaCorrect) {
        //I"C'h'->I"Ch' , camera的h/L,target的C
        LCh.C = getInverseChromaInHue(LCh, closed);
      }
      if (doLightnessCorrectInHue) {
        //I"Ch'->I'Ch' , camera的h/L
        LCh.L = getInverseLightnessInHue(LCh, closed);
      }
    }
    else {
      //以camera LCh的h找
      LChPair[] closed4LCh = getColosedLCh(LCh);
      //I"C'h'->I"Ch'
      LCh.C = getInverseChromaInHue(LCh, closed4LCh);
    }

    if (doHueCorrect) {
      //I'Ch'->I'Ch
      LCh.h = hueCorrector.getKey(LCh.h);
      LCh.h %= 360;
      LCh.h = (LCh.h < 0) ? LCh.h + 360 : LCh.h;
    }
    if (doLightnessCorrect) {
      //I'Ch->ICh
      LCh.L = lightnessCorrector.correctKeyInRange(LCh.L);
      LCh.L = lightnessCorrector.getKey(LCh.L);
    }
    //==========================================================================

    //轉換為target XYZ
    CIEXYZ result = getXYZ(LCh);
    return result;
  }

  //以hue平面為單位進行轉換
  protected final static boolean BY_HUE_PLANE = true;
  //是否省略第一個色塊(深色膚色)
  protected final static boolean IGNORE_DARK_SKIN = BY_HUE_PLANE ? false : false;
  //gray balance是否應用在轉換上
  protected final static boolean GRAY_BALANCE_WITH_CONVERT = false;
  //是否進行灰平衡
  protected boolean doGrayBalance = true;
  //是否調整明度
  protected boolean doLightnessCorrect = true;
  //是否依照色相的局部明度調整
  protected boolean doLightnessCorrectInHue = true;
  //是否調整彩度
  protected boolean doChromaCorrect = true;
  //是否調整色相
  protected boolean doHueCorrect = true;

  /**
   * 求係數
   *
   * @return Factor[]
   */
  protected Factor _produceFactor() {

    List<Patch> profilePatchList = dcTarget.filter.patchListForProfile();
    //==========================================================================
    // RGB域 gray balance修正
    // 修正項1 : 灰平衡
    //==========================================================================
    if (doGrayBalance) {
      grayBalancer = new GrayBalancer(dcTarget.filter.grayScale(),
                                      GrayBalancer.Style.RGBScale);
      profilePatchList = grayBalancer.grayBalance(profilePatchList);
    }
    //==========================================================================

    //LChPairArray包含所有24色塊
    LChPairArray = produceLChPairArray(profilePatchList);

    final int[] grayIndex = dcTarget.getGrayScaleIndex();
    LChPair[] grayLChPairArray = LChPair.filterGrayScale(LChPairArray,
        grayIndex);

    //==========================================================================
    // 明度修正項
    // 修正項2 : 依中性色塊修正明度
    //==========================================================================
    produceLightnessCorrector(grayLChPairArray);
    //==========================================================================

    if (BY_HUE_PLANE) {
      if (IGNORE_DARK_SKIN == true) {
        throw new IllegalArgumentException("IGNORE_DARK_SKIN == true");
      }
      //如果是it8 target, 只處理相同色相區域
      boolean onlySameHue = dcTarget.getChart() == DCTarget.Chart.IT8;
      huePlane = new HuePlane(LChPairArray, grayIndex,
                              dcTarget.getTargetData().sameHueIndexInProfile,
                              ignoreIndex, onlySameHue);
      huePlane.targetLightnessCorrect(lightnessCorrector);

      //==========================================================================
      //色相修正項
      //修正項3 : 色相修正
      //==========================================================================
      produceHueCorrector(huePlane);
      //==========================================================================

      if (!huePlane.checkHueSort()) {
        Logger.log.info("hue is reverse!");
      }
    }
    else {
      LChPair[] nonGrayLChPairArray = LChPair.filterNonGrayScale(LChPairArray,
          grayIndex);
      //色相修正項
      produceHueCorrector(nonGrayLChPairArray);
      //產生依色相排列色塊
      produceHueSortLChArray(nonGrayLChPairArray);
      if (!checkHueSortLChArray()) {
        Logger.log.info("hue is reverse!");
      }
    }

    return new Factor();
  }

  protected GrayBalancer grayBalancer;
  public GrayBalancer getGrayBalancer() {
    return grayBalancer;
  }

  protected boolean checkHueSortLChArray() {
    int size = hueSortLChPairArray.length;
    for (int x = 0; x < size - 1; x++) {
      if (hueSortLChPairArray[x].cameraLCh.h >
          hueSortLChPairArray[x + 1].cameraLCh.h) {
        return false;
      }
    }
    return true;
  }

  protected void produceLightnessCorrector(final LChPair[] grayLChPairArray) {
    //==========================================================================
    // 1. I->I'
    //==========================================================================
    int size = grayLChPairArray.length;
    double[] input = new double[size + 2];
    double[] output = new double[size + 2];
    for (int x = 0; x < size; x++) {
      input[x + 1] = grayLChPairArray[x].targetLCh.L;
      output[x + 1] = grayLChPairArray[x].cameraLCh.L;

    }

    input[size + 1] = 100;
    output[size + 1] = 100;
    lightnessCorrector = new Interpolation1DLUT(input, output,
                                                Interpolation1DLUT.Algo.LINEAR);
    //==========================================================================
  }

  protected int minHueIndex, maxHueIndex;

  /**
   * 產生按照hue排序的L'Ch' Array
   * @param nonGrayLChPairArray LChPair[]
   */
  protected void produceHueSortLChArray(final LChPair[] nonGrayLChPairArray) {
    int size = nonGrayLChPairArray.length;
    int huePatchSize = size + 2;
    hueSortLChPairArray = new LChPair[huePatchSize];

    //取出除了中性色塊的hue
    for (int x = 0; x < size; x++) {
      hueSortLChPairArray[x + 1] = (LChPair) nonGrayLChPairArray[x].clone();
    }

    //==========================================================================
    // 設定頭尾
    //==========================================================================
    hueSortLChPairArray[0] = (LChPair) hueSortLChPairArray[maxHueIndex].clone();
    hueSortLChPairArray[0].targetLCh.h -= 360;
    hueSortLChPairArray[0].cameraLCh.h -= 360;

    hueSortLChPairArray[huePatchSize - 1]
        = (LChPair) hueSortLChPairArray[minHueIndex].clone();
    hueSortLChPairArray[huePatchSize - 1].targetLCh.h += 360;
    hueSortLChPairArray[huePatchSize - 1].cameraLCh.h += 360;
    //==========================================================================

    Arrays.sort(hueSortLChPairArray, hueComparator);

    //==========================================================================
    // 修正h/L
    //==========================================================================
    for (LChPair pair : hueSortLChPairArray) {
      CIELCh LCh = pair.targetLCh;
      //色相修正
      LCh.h = hueCorrector.getValue(LCh.h);
      //明度修正
      LCh.L = lightnessCorrector.getValue(LCh.L);
    }
    //==========================================================================
  }

  protected void produceHueCorrector(final LChPair[] nonGrayLChPairArray) {
    //==========================================================================
    // 2. h->h'
    //==========================================================================
    int size = nonGrayLChPairArray.length;
    int huePatchSize = size + 2;
    //hue input
    double[] input = new double[huePatchSize];
    //hue output
    double[] output = new double[huePatchSize];
    int index = 0;

    //取出除了中性色塊的hue
    for (int x = 0; x < size; x++) {
      input[index + 1] = nonGrayLChPairArray[x].targetLCh.h;
      output[index + 1] = nonGrayLChPairArray[x].cameraLCh.h;
      index++;
    }
    input[0] = input[huePatchSize - 1] = Double.MAX_VALUE;
    output[0] = output[huePatchSize - 1] = Double.MAX_VALUE;
    minHueIndex = Maths.minIndex(input);

    input[0] = input[huePatchSize - 1] = Double.MIN_VALUE;
    output[0] = output[huePatchSize - 1] = Double.MIN_VALUE;
    maxHueIndex = Maths.maxIndex(input);

    //設定頭尾 <0以及>360的部分
    input[0] = input[maxHueIndex] - 360;
    output[0] = output[maxHueIndex] - 360;
    input[huePatchSize - 1] = input[minHueIndex] + 360;
    output[huePatchSize - 1] = output[minHueIndex] + 360;

    //照hue排序, 方便內插
    Arrays.sort(input);
    Arrays.sort(output);

    hueCorrector = new Interpolation1DLUT(input, output,
                                          Interpolation1DLUT.Algo.LINEAR);
    //==========================================================================
  }

  protected void produceHueCorrector(final HuePlane huePlane) {
    //==========================================================================
    // 2. h->h'
    //==========================================================================
    int size = huePlane.size();
    //hue input
    double[] input = new double[size];
    //hue output
    double[] output = new double[size];

    for (int x = 0; x < size; x++) {
      LChPair[] pair = huePlane.get(x);
      input[x] = pair[0].targetLCh.h;
      output[x] = pair[0].cameraLCh.h;
    }
    hueCorrector = new Interpolation1DLUT(input, output,
                                          Interpolation1DLUT.Algo.LINEAR);
    //==========================================================================
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * LChPair的比較器
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  protected final static class LChPairComparator
      implements Comparator {

    public static enum Style {
      Hue, Chroma
    }

    protected LChPairComparator(Style style) {
      this.style = style;
    }

    private Style style;
    private ColorSpaceComparator comparator = new ColorSpaceComparator();

    /**
     * Compares its two arguments for order.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *   argument is less than, equal to, or greater than the second.
     */
    public int compare(Object o1, Object o2) {
      CIELCh LCh1 = ( (LChPair) o1).cameraLCh;
      CIELCh LCh2 = ( (LChPair) o2).cameraLCh;
      switch (style) {
        case Hue:
          comparator.setCompareIndex(2);
          return comparator.compare(LCh1, LCh2);
//          return Double.compare(LCh1.h, LCh2.h);
        case Chroma:
          comparator.setCompareIndex(1);
          return comparator.compare(LCh1, LCh2);
//          return Double.compare(LCh1.C, LCh2.C);
        default:
          throw new IllegalStateException("style is not assign");
      }
    }

    /**
     * Indicates whether some other object is &quot;equal to&quot; this
     * comparator.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> only if the specified object is also a
     *   comparator and it imposes the same ordering as this comparator.
     */
    public boolean equals(Object obj) {
      return false;
    }

  }

  //target->camera L
  protected Interpolation1DLUT lightnessCorrector;
  public Interpolation1DLUT getLightnessCorrector() {
    return lightnessCorrector;
  }

  //target->camera h
  protected Interpolation1DLUT hueCorrector;
  public Interpolation1DLUT getHueCorrector() {
    return hueCorrector;
  }

  /**
   * getDescription
   *
   * @return String
   */
  public String getDescription() {
    return "DCAppearanceModel";
  }

  public DoubleImage forwardImage(DoubleImage img) {
    DoubleImage clone = (DoubleImage) img.clone();
    int h = clone.getHeight();
    int w = clone.getWidth();
    double[] pixels = new double[3];
    RGB rgb = new RGB(rgbColorSpace, RGB.MaxValue.Double255);

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
//        img.getRaster().getPixel(x, y, pixels);
        img.getPixel(x, y, pixels);
        rgb.setValues(pixels);
        CIEXYZ XYZ = rgb.toXYZ();
        RGB rgb2 = getRGB(XYZ, true);
        rgb2.rationalize();
        rgb2.getValues(pixels, RGB.MaxValue.Double255);
//        clone.getRaster().setPixel(x, y, pixels);
        clone.setPixel(x, y, pixels);
      }
    }

    return clone;
  }

  /**
   * target->camera
   * @param img BufferedImage
   * @return BufferedImage
   */
  public BufferedImage forwardImage(BufferedImage img) {
    DoubleImage doubleImg = new DoubleImage(img);
    return forwardImage(doubleImg).getBufferedImage();
//    BufferedImage clone = ImageUtils.cloneBufferedImage(img);
//    int h = clone.getHeight();
//    int w = clone.getWidth();
//    double[] pixels = new double[3];
//    RGB rgb = new RGB(rgbColorSpace, RGB.MaxValue.Double255);
//
//    for (int x = 0; x < w; x++) {
//      for (int y = 0; y < h; y++) {
//        img.getRaster().getPixel(x, y, pixels);
//        rgb.setValues(pixels);
//        CIEXYZ XYZ = rgb.toXYZ();
//        RGB rgb2 = getRGB(XYZ, true);
//        rgb2.rationalize();
//        rgb2.getValues(pixels, RGB.MaxValue.Double255);
//        clone.getRaster().setPixel(x, y, pixels);
//      }
//    }
//
//    return clone;
  }

  public DoubleImage inverseImage(DoubleImage img) {
    DoubleImage clone = (DoubleImage) img.clone();
    int h = clone.getHeight();
    int w = clone.getWidth();
    double[] pixels = new double[3];
    RGB rgb = new RGB(rgbColorSpace, RGB.MaxValue.Double1);

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        img.getPixel(x, y, pixels);
        rgb.setValues(pixels, RGB.MaxValue.Double255);
        CIEXYZ XYZ = getXYZ(rgb, true);
        RGB rgb2 = RGB.fromXYZ(XYZ, RGB.ColorSpace.sRGB);
        rgb2.rationalize();
        rgb2.getValues(pixels, RGB.MaxValue.Double255);
        clone.setPixel(x, y, pixels);
      }
    }

    return clone;

  }

  /**
   * camera->target
   * @param img BufferedImage
   * @return BufferedImage
   */
  public BufferedImage inverseImage(BufferedImage img) {
    DoubleImage doubleImg = new DoubleImage(img);
    return inverseImage(doubleImg).getBufferedImage();
//    BufferedImage clone = ImageUtils.cloneBufferedImage(img);
//    int h = clone.getHeight();
//    int w = clone.getWidth();
//    double[] pixels = new double[3];
//    RGB rgb = new RGB(rgbColorSpace, RGB.MaxValue.Double1);
//
//    for (int x = 0; x < w; x++) {
//      for (int y = 0; y < h; y++) {
//        img.getRaster().getPixel(x, y, pixels);
//        rgb.setValues(pixels, RGB.MaxValue.Double255);
//        CIEXYZ XYZ = getXYZ(rgb, true);
//        RGB rgb2 = RGB.fromXYZ(XYZ, RGB.RGBColorSpace.sRGB);
//        rgb2.rationalize();
//        rgb2.getValues(pixels, RGB.MaxValue.Double255);
//        clone.getRaster().setPixel(x, y, pixels);
//      }
//    }
//
//    return clone;

  }

  public static void main(String[] args) {
//    GMBICCProfileAdapter profile = new GMBICCProfileAdapter(
//        "Measurement Files/Camera/S5Pro/s5p-std.icc");
//    GMBICCProfileAdapter profile = new GMBICCProfileAdapter(
//        "Measurement Files/Films/Velvia/it8.icc");
    DCChartAdapter profile = new DCChartAdapter(CMSDir.Reference.Camera +
                                                "/IT8 E3199808.cxf",
                                                LightSource.CIE.D65,
                                                RGB.ColorSpace.sRGB);

    LightSource.Source lightsource = LightSource.CIE.D65;

    //==========================================================================
//    DCChartAdapter chart = new DCChartAdapter(DCTarget.Chart.CC24, lightsource);
//    DCTarget target = DCTarget.Instance.get(profile, chart, lightsource,
//                                            DCTarget.Chart.CC24);

    DCChartAdapter chart = new DCChartAdapter(CMSDir.Reference.Camera +
                                              "/IT8 Ideal.cxf",
                                              LightSource.CIE.D65);
    DCTarget target = DCTarget.Instance.get(profile, chart, lightsource,
                                            DCTarget.Chart.IT8);
    //==========================================================================

    RGB.ColorSpace rgbColorSpace = RGB.ColorSpace.sRGB;
    Style style = Style.IPT;

    GamutBoundaryRGBDescriptor gbd = DCAppearanceModel.getGBDDescriptorInstance(
        rgbColorSpace, style);

    DCAppearanceModel model = new DCAppearanceModel(target, style,
        RGB.ColorSpace.sRGB, gbd);
    DCModel.Factor factor = model.produceFactor();
//    List<Patch> test = model.grayBalancer.grayBalance(target.getLabPatchList());
//    List<Patch> test = target.getLabPatchList();
    List<Patch> test = model.getHuePlanePatchList();

    //==========================================================================
    // 色差計算
    //==========================================================================
    DeltaEReport[] report = model.testForwardModel(test, false);
    System.out.println(report[0]);
    System.out.println(report[0].getPatchDeltaEReport(.5));
    DeltaEReport[] report2 = model.testReverseModel(test, false);
    System.out.println(report2[0]);
    System.out.println(report2[0].getPatchDeltaEReport(.5));
    //==========================================================================

    //==========================================================================
    // 誤差計算
    //==========================================================================
    int size = model.LChPairArray.length;

    for (int x = 0; x < size; x++) {
      Patch p = model.LChPairArray[x].patch;
      CIEXYZ XYZ = p.getNormalizedXYZ();
      RGB rgb = model.getRGB(XYZ, true);
      double drg = RGB.Delta.deltaRG(rgb, p.getRGB());
      rgb.changeMaxValue(RGB.MaxValue.Double255);
      p.getRGB().changeMaxValue(RGB.MaxValue.Double255);
      System.out.println( ( (drg > 0.1) ? "X " : "") + (x + 1) + " " + rgb +
                         " " + p.getRGB() + " " + drg);
    }
    //==========================================================================

    //==========================================================================
    // 轉圖測試
    //==========================================================================
//    try {
//      BufferedImage img = ImageUtils.loadImage(
//          "Image/Profile/d300/1_standard.jpg");
//      BufferedImage result = model.forwardImage(img);
//      ImageUtils.storeJPEGImage("result.jpg", result);
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//    }
    //==========================================================================


  }

  /**
   * 設定要略過的色塊索引值
   * @param ignoreIndex int[]
   */
  public void setIgnoreIndex(int[] ignoreIndex) {
    this.ignoreIndex = ignoreIndex;
  }

  /**
   * 是否對RGB做灰平衡
   * @param doGrayBalance boolean
   */
  public void setDoGrayBalance(boolean doGrayBalance) {
    this.doGrayBalance = doGrayBalance;
  }

  /**
   * 是否進行明度的修正
   * @param doLightnessCorrect boolean
   */
  public void setDoLightnessCorrect(boolean doLightnessCorrect) {
    this.doLightnessCorrect = doLightnessCorrect;
  }

  /**
   * 是否進行色相的修正
   * @param doHueCorrect boolean
   */
  public void setDoHueCorrect(boolean doHueCorrect) {
    this.doHueCorrect = doHueCorrect;
  }

  /**
   * 是否進行彩度的修正
   * @param doChromaCorrect boolean
   */
  public void setDoChromaCorrect(boolean doChromaCorrect) {
    this.doChromaCorrect = doChromaCorrect;
  }

  /**
   * 是否進行依色相作明度的修正
   * @param doLightnessCorrectInHue boolean
   */
  public void setDoLightnessCorrectInHue(boolean doLightnessCorrectInHue) {
    this.doLightnessCorrectInHue = doLightnessCorrectInHue;
  }

}
