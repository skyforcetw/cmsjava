package shu.cms.lcd;

import java.text.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.gradient.*;
import shu.cms.lcd.material.*;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 根據R/G/B單一頻道的測量值,內插出所需要的色塊；目前僅能內插同樣單一頻道的色塊。
 * 並且可以根據需求,自動選擇最佳的內插演算法。
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class LCDTargetInterpolator {

  private LCDTarget lcdTarget;
  /**
   * 以最均勻的uv'來做內插是最佳的
   */
  private Domain domain = Domain.uvPrime;

  private double[] codeArray;
  private double[][] rArray;
  private double[][] gArray;
  private double[][] bArray;
  private double[][] wArray;

  private static class Interpolator {
    private Interpolation[] interpolation = new Interpolation[3];
    private Interpolator(double[] input, double[][] output) {
      interpolation[0] = new Interpolation(input, output[0]);
      interpolation[1] = new Interpolation(input, output[1]);
      interpolation[2] = new Interpolation(input, output[2]);
    }

    private double[] interpolate(double x, Interpolation.Algo[] algos) {
      double v0 = interpolation[0].interpolate(x, algos[0]);
      double v1 = interpolation[1].interpolate(x, algos[1]);
      double v2 = interpolation[2].interpolate(x, algos[2]);
      return new double[] {
          v0, v1, v2};
    }
  }

  private Interpolator rInterpolator;
  private Interpolator gInterpolator;
  private Interpolator bInterpolator;
  private Interpolator wInterpolator;

  private Interpolation.Algo[] interpolationType;

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: </p>
   * 內插所使用的數值.
   * XYZ的色差會較大,不建議使用
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: </p>
   *
   * @author not attributable
   * @version 1.0
   */
  public static enum Domain {
    xyY, XYZ, uvPrime
  }

  protected CIEXYZ getXYZFromDomainValues(double[] values) {
    switch (domain) {
      case xyY: {
        CIExyY xyY = new CIExyY(values);
        if (xyY.x == 0 && xyY.y == 0 && xyY.Y == 0) {
          return new CIEXYZ();
        }
        else {
          CIEXYZ XYZ = CIExyY.toXYZ(xyY);
          return XYZ;
        }
      }
      case XYZ: {
        return new CIEXYZ(values);
      }
      case uvPrime: {
        CIExyY xyY = new CIExyY();
        xyY.setuvPrimeYValues(values);
        return CIExyY.toXYZ(xyY);
      }
      default:
        return null;
    }
  }

  protected double[] getDomainValues(CIEXYZ XYZ) {
    switch (domain) {
      case XYZ: {
        return XYZ.getValues();
      }
      case xyY: {
        CIExyY xyY = CIExyY.fromXYZ(XYZ);
        xyY.x = Double.isNaN(xyY.x) ? 0 : xyY.x;
        xyY.y = Double.isNaN(xyY.y) ? 0 : xyY.y;
        return xyY.getValues();
      }
      case uvPrime: {
        CIExyY xyY = CIExyY.fromXYZ(XYZ);
        double[] uvpY = xyY.getuvPrimeYValues();
        return uvpY;
      }
      default:
        return null;
    }
  }

  /**
   * 初始化內插運算(for uniform)
   * @param channels Channel[]
   */
  protected void initInterpolationUniform(RGBBase.Channel[] channels) {

    int size = this.lcdTarget.getLevel();
    double step = this.lcdTarget.getStep();
    step *= 255 / lcdTarget.getMaxValue().max;
    rArray = new double[size][];
    gArray = new double[size][];
    bArray = new double[size][];
    wArray = new double[size][];
    double[][][] rgbArray = new double[][][] {
        rArray, gArray, bArray, wArray};

    boolean codeArrayInit = false;
    codeArray = new double[size];
    int codeArrayIndex = 0;

    RGB keyRGB = lcdTarget.getKeyRGB();
    for (int x = 0; x < channels.length; x++) {
      RGBBase.Channel c = channels[x];
      int arrayIndex = getArrayIndex(c);

      int index = 0;
      keyRGB.setColorBlack();
      for (double code = 0; code <= 255; code += step) {
        //取得該頻道下0~255的量測值
        keyRGB.setValue(c, code, RGB.MaxValue.Double255);
        Patch p = lcdTarget.getPatch(keyRGB);
        //將對映的domain資料取出, 放到rgbArray. 內插將依照rgbArray的數值.
        rgbArray[arrayIndex][index++] = getDomainValues(p.getXYZ());
        if (false == codeArrayInit) {
          codeArray[codeArrayIndex++] = code;
        }
      }
      codeArrayInit = true;
    }

    if (rArray[0] != null) {
      rArray = DoubleArray.transpose(rArray);
      rInterpolator = new Interpolator(codeArray, rArray);
    }

    if (gArray[0] != null) {
      gArray = DoubleArray.transpose(gArray);
      gInterpolator = new Interpolator(codeArray, gArray);
    }

    if (bArray[0] != null) {
      bArray = DoubleArray.transpose(bArray);
      bInterpolator = new Interpolator(codeArray, bArray);
    }

    if (wArray[0] != null) {
      wArray = DoubleArray.transpose(wArray);
      wInterpolator = new Interpolator(codeArray, wArray);
    }

  }

  private List<Patch>[] filterPatchListArray(RGB.Channel[] channels) {
    int size = channels.length;
    List<Patch> [] patchListArray = new List[size];
    for (int x = 0; x < size; x++) {
      patchListArray[x] = lcdTarget.filter.grayScalePatch(channels[x], true);
//      patchListArray[x] = lcdTarget.filter.oneValueChannel(channels[x]);
    }
    return patchListArray;
  }

  /**
   * 初始化內插運算(for various)
   * @todo M 看for various是不是可以取代for uniform
   */
  protected void initInterpolationVarious() {
    //==========================================================================
    // 首先過濾出僅有R/G/B/W的色塊
    //==========================================================================1
    List<Patch>
        [] patchListArray = filterPatchListArray(RGB.Channel.RGBWChannel);
    int channels = patchListArray.length;
    //==========================================================================

    //==========================================================================
    // 加全黑的色塊
    //==========================================================================
//    Patch darkestPatch = lcdTarget.getDarkestPatch();
    int[] sizeArray = new int[channels];
    double[][] codeArray = new double[channels][];
    double[][][] rgbwArray = new double[channels][][];

    for (int x = 0; x < channels; x++) {
      List<Patch> patchList = patchListArray[x];
//      patchList.add(0, darkestPatch);
      int size = patchList.size();
      sizeArray[x] = size;
      codeArray[x] = new double[size];
      rgbwArray[x] = new double[size][];

      //==========================================================================
      // 將List內的所有色塊, 一一儲存到rgbArray
      //==========================================================================
      int index = 0;
      for (Patch p : patchList) {
        codeArray[x][index] = p.getRGB().getValue(RGBBase.Channel.
                                                  getChannelByArrayIndex(x));
        rgbwArray[x][index] = getDomainValues(p.getXYZ());
        index++;
      }
      //==========================================================================
    }

    //轉置
    rArray = DoubleArray.transpose(rgbwArray[0]);
    gArray = DoubleArray.transpose(rgbwArray[1]);
    bArray = DoubleArray.transpose(rgbwArray[2]);
    wArray = DoubleArray.transpose(rgbwArray[3]);

    //==========================================================================
    // 產生內插物件
    //==========================================================================
    rInterpolator = new Interpolator(codeArray[0], rArray);
    gInterpolator = new Interpolator(codeArray[1], gArray);
    bInterpolator = new Interpolator(codeArray[2], bArray);
    wInterpolator = new Interpolator(codeArray[3], wArray);
    //==========================================================================
  }

  /**
   * 以lcdTarget為基準,產生內插值
   * 因為沒有預設Interpolation.Type,所以要呼叫
   * getPatch(RGBBase.Channel ch, double value,Interpolation.Type type)
   * 即時設定內插演算法
   * @param lcdTarget LCDTarget
   */
  private LCDTargetInterpolator(LCDTarget lcdTarget) {
    this(lcdTarget, null, Domain.uvPrime,
         RGBBase.Channel.RGBWChannel);
  }

  private LCDTargetInterpolator(LCDTarget lcdTarget,
                                RGBBase.Channel ...channels) {
    this(lcdTarget, null, Domain.uvPrime, channels);
  }

  private RGBBase.Channel[] channels = RGBBase.Channel.RGBWChannel;

  /**
   * 以lcdTarget為基準,產生內插值.
   * 內插演算法並且以rInterpolationType/gInterpolationType/bInterpolationType做計算.
   * @param lcdTarget LCDTarget
   * @param algos Algo[]
   * @param channels Channel[]
   * @param domain Domain
   */
  private LCDTargetInterpolator(LCDTarget lcdTarget,
                                Interpolation.Algo[] algos,
                                Domain domain,
                                RGBBase.Channel ...channels) {
    this.lcdTarget = lcdTarget;
    this.interpolationType = algos;
    this.channels = channels;
    this.domain = domain;

    if (lcdTarget.getNumber().isRamp()) {

      this.sampleType = OnlyUseInterpolationVarious ? SampleType.Various :
          SampleType.Uniform;
    }
    else if (lcdTarget.getNumber() == LCDTargetBase.Number.Patch79 ||
             lcdTarget.getNumber() == LCDTargetBase.Number.Test729 ||
             lcdTarget.getNumber() == LCDTargetBase.Number.Test4096) {
      this.sampleType = SampleType.Various;
    }
    else {
      throw new IllegalArgumentException("Unsupport LCDTaret.Number: " +
                                         lcdTarget.getNumber());
    }

    initInterpolation(channels);
    initGammaCorrector(channels);
  }

  /**
   * 初始化gamma corrector, 供Luminance RGB轉DAC Value RGB時用
   * @param channels Channel[]
   */
  protected void initGammaCorrector(RGBBase.Channel[] channels) {
    correctors = new GammaCorrector[4];

    for (RGBBase.Channel channel : channels) {
      Set<Patch>
          singlePatchSet = lcdTarget.filter.grayScalePatchSet(channel);

      GammaCorrector corrector = GammaCorrector.getLUTInstance(singlePatchSet,
          channel);
      int index = this.getArrayIndex(channel);
      correctors[index] = corrector;
    }
  }

  /**
   * Luminance RGB轉DAC Value RGB時用
   */
  private GammaCorrector[] correctors;

  private final static boolean OnlyUseInterpolationVarious = true;

  protected void initInterpolation(RGBBase.Channel[] channels) {

    switch (sampleType) {
      case Uniform:
        initInterpolationUniform(channels);

        /**
         * @todo M acp initInterpolationVarious會有問題
         * @note uniform的patch用initInterpolationVarious去產生內插值,
         * 會有錯誤,原因?請再花時間細看...
         */
//        initInterpolationVarious();
        break;
      case Various:
        initInterpolationVarious();
        break;
    }

  }

  /**
   * 取得ch所在的array index
   * @param ch Channel
   * @return int
   */
  protected final int getArrayIndex(RGBBase.Channel ch) {
    return ch == RGBBase.Channel.W ? 3 : ch.getArrayIndex();
  }

  protected final Interpolation.Algo getInterpolationAlgo(RGBBase.Channel ch) {
    int index = Arrays.binarySearch(channels, ch);
    return interpolationType[index];
  }

  /**
   * 內插值(CIEXYZ)
   * @param ch Channel
   * @param code double
   * @param type Type
   * @return double[]
   */
  protected double[] getValues(RGBBase.Channel ch, double code,
                               Interpolation.Algo type) {
    //==========================================================================
    // 某些演算法只適合亮度的計算, 因此非亮度的計算須以LINEAR處理
    //==========================================================================
    Interpolation.Algo nonlumiType = null;
    if ( (type == Interpolation.Algo.Gamma || type == Interpolation.Algo.Gamma2) &&
        (domain == Domain.xyY || domain == Domain.uvPrime)) {
      nonlumiType = Interpolation.Algo.Linear;
    }
    else {
      nonlumiType = type;
    }
    Interpolation.Algo[] algos = new Interpolation.Algo[] {
        nonlumiType, nonlumiType, type};
    //==========================================================================

    int index = getArrayIndex(ch);
    switch (index) {
      case 0:
        return rInterpolator.interpolate(code, algos);
      case 1:
        return gInterpolator.interpolate(code, algos);
      case 2:
        return bInterpolator.interpolate(code, algos);
      case 3:
        return wInterpolator.interpolate(code, algos);
      default:
        return null;
    }
  }

  /**
   * 內插出Patch
   * @param ch Channel
   * @param value double
   * @return Patch
   */
  public final Patch getPatch(RGBBase.Channel ch, double value) {
    if (interpolationType == null) {
      throw new IllegalStateException("interpolationType == null");
    }
    return getPatch(ch, value, getInterpolationAlgo(ch));
  }

  private CIEXYZ blackXYZ;
  private CIEXYZ[] saturatedXYZ = new CIEXYZ[4];

  protected double normalizeLuminance(RGBBase.Channel ch, double luminance) {
    if (blackXYZ == null) {
      blackXYZ = this.lcdTarget.getBlackPatch().getXYZ();
    }
    int index = this.getArrayIndex(ch);
    if (saturatedXYZ[index] == null) {
      Patch p = this.lcdTarget.getPatch(ch, 1, RGB.MaxValue.Double1);
      saturatedXYZ[index] = p.getXYZ();
    }
    double normal = (luminance - blackXYZ.Y) /
        (saturatedXYZ[index].Y - blackXYZ.Y);
    return normal;
  }

  public final RGB getRGB(RGBBase.Channel ch, double luminance) {
    double normalLumi = normalizeLuminance(ch, luminance);
    int index = this.getArrayIndex(ch);
    double uncorrect = correctors[index].uncorrect(normalLumi);
    RGB rgb = new RGB();
    rgb.setValue(ch, uncorrect, RGB.MaxValue.Double1);
    rgb.changeMaxValue(this.lcdTarget.getMaxValue());
    return rgb;
  }

  /**
   * 內插出Patch
   * @param ch Channel
   * @param value double
   * @param type Type
   * @return Patch
   */
  public final Patch getPatch(RGBBase.Channel ch, double value,
                              Interpolation.Algo type) {
    double[] values = getValues(ch, value, type);
    RGB rgb = lcdTarget.getKeyRGB();
    rgb.setColorBlack();
    rgb.setValue(ch, value);

    CIEXYZ white = lcdTarget.getWhitePatch().getXYZ();
    CIEXYZ XYZ = getXYZFromDomainValues(values);
    CIELab Lab = CIELab.fromXYZ(XYZ, white);

    Patch p = new Patch("Interp", XYZ, Lab, rgb);
    return p;
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 最佳化的判斷基準(以色差為基準)
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static enum OptimumType {
    Average, Max, Mix
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 所採用的樣本型態
   * Uniform與Various的差別在於:
   * Uniform為連續code不間斷的色塊.
   * Various則為code有間斷的色塊.
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static enum SampleType {
    Uniform, Various
  }

  private SampleType sampleType = SampleType.Uniform;

  public static class Find {
    /**
     * 從lessTarget建立model, 再用moreTarget做驗證, 找到最佳內插法
     * @param lessTarget LCDTarget
     * @param moreTarget LCDTarget
     * @param type OptimumType 找到最佳解的指標
     * @return Type[]
     */
    public final static Interpolation.Algo[] optimumInterpolationType(
        LCDTarget lessTarget, LCDTarget moreTarget, OptimumType type) {
      return optimumInterpolationType(lessTarget, moreTarget, type,
                                      RGBBase.Channel.RGBWChannel);
    }

    public final static Interpolation.Algo[] optimumInterpolationType(
        LCDTarget lcdTarget, OptimumType type) {
      return optimumInterpolationType(lcdTarget, lcdTarget, type,
                                      RGBBase.Channel.RGBWChannel);
    }

    /**
     * 從lessTarget建立model, 再用moreTarget做驗證, 找到最佳內插法
     * @param lessTarget LCDTarget
     * @param moreTarget LCDTarget
     * @param type OptimumType 找到最佳解的指標
     * @param channels Channel[]
     * @return Algo[]
     */
    public final static Interpolation.Algo[] optimumInterpolationType(
        LCDTarget lessTarget, LCDTarget moreTarget,
        OptimumType type, RGBBase.Channel ...channels) {
      Interpolation.Algo[] interpolationType = new Interpolation.Algo[channels.
          length];
      int index = 0;
      for (RGBBase.Channel ch : channels) {
        interpolationType[index++] = optimumInterpolationType(lessTarget,
            moreTarget, type, ch);
      }
      return interpolationType;
    }

    protected final static Interpolation.Algo optimumInterpolationType(
        LCDTarget lessTarget, LCDTarget moreTarget, OptimumType type,
        RGBBase.Channel ch) {
      List<Patch> morePatchList = moreTarget.filter.oneValueChannel(ch);
      List<Patch> lessPatchList = lessTarget.filter.oneValueChannel(ch);
      int size = morePatchList.size() - lessPatchList.size();
      double[] deltaEArray = new double[size];

      //利用less target做內插,目的是希望內插結果可以逼近moreTarget
      LCDTargetInterpolator interpolator = new LCDTargetInterpolator(lessTarget,
          new RGBBase.Channel[] {ch});
      RGB keyRGB = lessTarget.getKeyRGB();
      keyRGB.setColorBlack();

      Interpolation.Algo bestInterpType = null;
      double bestIndex = Double.MAX_VALUE;
      int morePatchSize = morePatchList.size();

      double[] rgbValues = new double[3];

      for (Interpolation.Algo interpType : Interpolation.Algo.values()) {
        //將各種內插演算法列出來
        if (interpType == Interpolation.Algo.Lagrange ||
            interpType == Interpolation.Algo.Spline2) {
          //迭代不同的內插法,但是略過LAGRANGE(太多內插速度會過慢),還有SPLINE2(太慢)
          continue;
        }
        int arrayIndex = 0;
        for (int x = 0; x < morePatchSize; x++) {
          Patch p = morePatchList.get(x);

          p.getRGB().getValues(rgbValues);
          keyRGB.setValues(rgbValues);

          if (lessTarget.getPatch(keyRGB) == null) {
            double code = p.getRGB().getValue(ch);
            Patch interpPatch = interpolator.getPatch(ch, code, interpType);
            CIELab actualLab = p.getLab();
            CIELab interpLab = interpPatch.getLab();
            deltaEArray[arrayIndex++] = DeltaE.CIE2000DeltaE(actualLab,
                interpLab);
          }
        }
        double index = Double.MAX_VALUE;
        //依照不同的最佳化方法,計算出index值
        switch (type) {
          case Average:
            index = Maths.mean(deltaEArray);
            break;
          case Max:
            index = Maths.max(deltaEArray);
            break;
          case Mix:
            index = Math.sqrt(Maths.mean(deltaEArray) * Maths.max(deltaEArray));
            break;
        }
        if (index < bestIndex) {
          //找到最佳演算法
          bestInterpType = interpType;
          bestIndex = index;
        }
      }

      return bestInterpType;
    }

    public final static Interpolation.Algo[] optimumInterpolationType(
        LCDTarget lcdTarget, OptimumType type, RGBBase.Channel ...channels) {
      return optimumInterpolationType(lcdTarget, lcdTarget, type, channels);
    }
  }

  public String getReport(LCDTarget moreLCDTarget, Interpolation.Algo algo,
                          RGBBase.Channel channel, boolean dJNDIOnly) {
    RGB keyRGB = moreLCDTarget.getKeyRGB();
    double step = moreLCDTarget.getStep();
    double step2 = lcdTarget.getStep();
    int level = moreLCDTarget.getLevel() - lcdTarget.getLevel();

    double[] deArray = new double[level];
    double[][] deltauvArray = new double[level][];
    double[] deltaJNDI = new double[level];
    int index = 0;
    GSDFGradientModel gm = new GSDFGradientModel(lcdTarget);

    for (double code = step; code < 255; code += step) {
      if (code % step2 == 0) {
        continue;
      }

      Patch p = this.getPatch(channel, code, algo);
      CIEXYZ interpXYZ = p.getXYZ();
      CIExyY interpxyY = new CIExyY(interpXYZ);
      CIELab interpLab = p.getLab();
      double interpJNDI = gm.getJNDIndex(interpXYZ);

      keyRGB.setColorBlack();
      keyRGB.setValue(channel, code);
      Patch targetP = moreLCDTarget.getPatch(keyRGB);
      CIEXYZ targetXYZ = targetP.getXYZ();
      CIExyY targetxyY = new CIExyY(targetXYZ);
      CIELab targetLab = targetP.getLab();
      double targetJNDI = gm.getJNDIndex(targetXYZ);

      //========================================================================
      // 差異計算
      //========================================================================
      double de = DeltaE.CIE2000DeltaE(interpLab, targetLab);
      deArray[index] = de;
      deltauvArray[index] = targetxyY.getDeltauvPrime(interpxyY);
      deltaJNDI[index] = Math.abs(targetJNDI - interpJNDI);
      //========================================================================

      index++;
    }
    DoubleArray.transpose(deltauvArray);

    StringBuilder builder = new StringBuilder();
    DecimalFormat df = new DecimalFormat("##.####");

    if (!dJNDIOnly) {
      builder.append("[du'] ave:" + df.format(Maths.mean(deltauvArray[0])) +
                     " max:" +
                     df.format(Maths.max(deltauvArray[0])) + " std:" +
                     df.format(Maths.std(deltauvArray[0])));
      builder.append('\n');
      builder.append("[dv'] ave:" + df.format(Maths.mean(deltauvArray[1])) +
                     " max:" +
                     df.format(Maths.max(deltauvArray[1])) + " std:" +
                     df.format(Maths.std(deltauvArray[1])));
      builder.append('\n');
      builder.append("[de] ave:" + df.format(Maths.mean(deArray)) + " max:" +
                     df.format(Maths.max(deArray)) + " std:" +
                     df.format(Maths.std(deArray)));
      builder.append('\n');
    }
    builder.append("[dJNDI] ave:" + df.format(Maths.mean(deltaJNDI)) + " max:" +
                   df.format(Maths.max(deltaJNDI)) + " std:" +
                   df.format(Maths.std(deltaJNDI)));
    builder.append('\n');
//    int[] perceptibleIndexArray = getIndexArrayMaxThanOne(deltaJNDI);
//    builder.append("Perceptible dJNDI count: " + perceptibleIndexArray.length);
//    builder.append('\n');
    return builder.toString();
  }

  private final static LCDTarget getTestLCDTarget() {
    LCDTarget lcdTarget = LCDTarget.Instance.get("cpt_370WF02",
                                                 LCDTarget.Source.CA210,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.
                                                 Native,
                                                 LCDTargetBase.Number.Ramp4096,
                                                 LCDTarget.FileType.Logo,
                                                 "0119",
                                                 "");
    return lcdTarget;
  }

  private final static LCDTarget getTestLessLCDTarget(LCDTarget lcdTarget) {
    //lcdTarget.getLabPatchList();
    LCDTarget lessLCDTarget = lcdTarget.targetFilter.getRamp2048From4096().
        targetFilter.getRamp1024From2048();

    LCDTarget.Operator.gradationReverseFix(lessLCDTarget);
    LCDTarget.Operator.clippingFix(lessLCDTarget, 254);
    return lessLCDTarget;

  }

  public static void algoTest(String[] args) {
//    LCDTarget.setRGBNormalize(false);
    LCDTarget lcdTarget = getTestLCDTarget();
//    showLuminanceCurve(lcdTarget);
    LCDTarget lessLCDTarget = getTestLessLCDTarget(lcdTarget);

    LCDTargetInterpolator interp = new LCDTargetInterpolator(lessLCDTarget);

    for (Interpolation.Algo algo : Interpolation.Algo.values()) {
      if (algo == Interpolation.Algo.Lagrange ||
          algo == Interpolation.Algo.Lagrange4 ||
          algo == Interpolation.Algo.Lagrange8 ||
          algo == Interpolation.Algo.Spline2) {
        continue;
      }
      System.out.println(algo);

      long start = System.currentTimeMillis();
      for (RGBBase.Channel c : RGBBase.Channel.RGBWChannel) {
        System.out.println(c);
        System.out.print(interp.getReport(lcdTarget, algo, c, true));

      }
      System.out.println("cost: " + (System.currentTimeMillis() - start));
    }
  }

  public static void main(String[] args) {
//    algoTest(args);
    test6bit(args);

  }

  public final static Interpolation.Algo[] InterpolationLinear = {
      Interpolation.Algo.Linear, Interpolation.Algo.Linear,
      Interpolation.Algo.Linear, Interpolation.Algo.Linear};
  public final static Interpolation.Algo[] InterpolationQuadratic = {
      Interpolation.Algo.QuadraticPolynomial,
      Interpolation.Algo.QuadraticPolynomial,
      Interpolation.Algo.QuadraticPolynomial,
      Interpolation.Algo.QuadraticPolynomial};

  public static void test6bit(String[] args) {
    LCDTarget target = LCDTargetMaterial.getAUO_B156HW01();
    LCDTarget ramp260 = target.targetFilter.getRamp1021().targetFilter.
        getRamp260();
    LCDTarget.Operator.gradationReverseFix(ramp260);
//    Interpolation.Algo[] algos = LCDTargetInterpolator.Find.
//        optimumInterpolationType(ramp260, OptimumType.Max);
//    Interpolation.Algo[] algos = {
//        Interpolation.Algo.Linear, Interpolation.Algo.Linear,
//        Interpolation.Algo.Linear, Interpolation.Algo.Linear};
    LCDTargetInterpolator interp = LCDTargetInterpolator.Instance.get(ramp260,
        InterpolationLinear);
    TargetInterpolatorAdapter adapter = new TargetInterpolatorAdapter(interp,
        LCDTargetBase.Number.Ramp1021);
    List<CIEXYZ> XYZList = adapter.getXYZList();
    List<RGB> rgbList = adapter.getRGBList();
    int size = XYZList.size();
    for (int x = 0; x < size; x++) {
      System.out.println(rgbList.get(x) + " " + XYZList.get(x));
    }
  }

  public final static class Instance {
    public final static LCDTargetInterpolator get(LCDTarget lessLCDTarget,
                                                  LCDTarget moreLCDTarget,
                                                  OptimumType optimumType) {
      LCDTargetInterpolator interp = LCDTargetInterpolator.Instance.get(
          lessLCDTarget,
          Find.optimumInterpolationType(lessLCDTarget, moreLCDTarget,
                                        optimumType));
      return interp;
    }

    public final static LCDTargetInterpolator get(LCDTarget lcdTarget,
                                                  Interpolation.Algo[]
                                                  interpolationTypes) {
      LCDTargetInterpolator interp = LCDTargetInterpolator.Instance.get(
          lcdTarget, interpolationTypes, RGBBase.Channel.RGBWChannel);
      return interp;
    }

    public final static LCDTargetInterpolator get(LCDTarget lcdTarget,
                                                  Interpolation.Algo[]
                                                  interpolationTypes,
                                                  RGBBase.Channel ...channels) {
      LCDTargetInterpolator interp = new LCDTargetInterpolator(lcdTarget,
          interpolationTypes, Domain.xyY, channels);
      return interp;
    }

    public final static LCDTargetInterpolator get(
        LCDTarget rampTarget, RGBBase.Channel channel) {
      Interpolation.Algo[] algos = LCDTargetInterpolator.Find.
          optimumInterpolationType(rampTarget,
                                   LCDTargetInterpolator.OptimumType.Max,
                                   channel);
      LCDTargetInterpolator interp = LCDTargetInterpolator.Instance.get(
          rampTarget, algos, channel);
      return interp;
    }
  }
}
