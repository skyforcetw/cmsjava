package shu.cms.devicemodel.dc;

import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.dc.*;
import shu.cms.devicemodel.*;
import shu.cms.util.*;
import shu.math.*;
import shu.math.array.*;

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
public abstract class DCModel
    extends DeviceCharacterizationModel {

  public Illuminant getIlluminant() {
    return illuminant;
  }

  public CIEXYZ getWhitePatchXYZ() {
    if (whitePatchXYZ == null) {
      whitePatchXYZ = dcTarget.getBrightestPatch().getXYZ();
    }
    return whitePatchXYZ;
  }

  public static class Factor
      extends DeviceCharacterizationModel.Factor {
    public Factor() {

    }

    Factor(GammaCorrector RrCorrecor,
           GammaCorrector GrCorrecor,
           GammaCorrector BrCorrecor, CIEXYZ whitePatch, double[] normal) {
      this.RrCorrecor = RrCorrecor;
      this.GrCorrecor = GrCorrecor;
      this.BrCorrecor = BrCorrecor;
      this.whitePatch = whitePatch;
      this.normal = normal;
    }

    GammaCorrector RrCorrecor;
    GammaCorrector GrCorrecor;
    GammaCorrector BrCorrecor;
    CIEXYZ whitePatch;
    double[] normal;

  }

  /**
   * 求係數
   * @return Factor[]
   */
  protected abstract Factor _produceFactor();

  public Factor produceFactor() {
    this.produceStart();
    Factor factor = _produceFactor();
    this.produceEnd();
    return factor;
  }

  public RGB.MaxValue getMaxValue() {
    if (null != dcTarget) {
      return dcTarget.getMaxValue();
    }
    else {
      return null;
    }
  }

  protected CIEXYZ whitePatchXYZ;
  protected CIEXYZ luminance;
  protected Factor theModelFactor;
  protected Illuminant illuminant;
  protected DCTarget dcTarget;
  protected static ColorMatchingFunction cmf = ColorMatchingFunction.
      CIE_1931_2DEG_XYZ;
  protected boolean doGammaCorrect = false;
  protected GammaCorrector _RrCorrector;
  protected GammaCorrector _GrCorrector;
  protected GammaCorrector _BrCorrector;
  protected double[] normal;

  public GammaCorrector getGammaCorrector(RGB.Channel ch) {
    switch (ch) {
      case R:
        return _RrCorrector;
      case G:
        return _GrCorrector;
      case B:
        return _BrCorrector;
      default:
        return null;
    }
  }

  public double[][] getInputTables() {
    double[][] inputTables = null;

    if (_RrCorrector != null && _GrCorrector != null && _BrCorrector != null) {
      double[] rInputTable = _RrCorrector.getCorrectTable(512);
      double[] gInputTable = _GrCorrector.getCorrectTable(512);
      double[] bInputTable = _BrCorrector.getCorrectTable(512);
      inputTables = new double[][] {
          rInputTable, gInputTable, bInputTable};
    }
    else {
      double[] inputTable = produceLinearInputTable(512);
      inputTables = new double[][] {
          inputTable, inputTable, inputTable};
    }

    if (normal != null) {
      normalizeInputTable(inputTables[0], normal[0]);
      normalizeInputTable(inputTables[1], normal[1]);
      normalizeInputTable(inputTables[2], normal[2]);
    }

    return inputTables;
  }

  public static void main(String[] args) {
    DCTarget F8Target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                              LightSource.i1Pro.D50,
                                              1, DCTarget.Chart.CCSG);

    DCPolynomialRegressionModel model = new DCPolynomialRegressionModel(
        F8Target, true, Polynomial.COEF_3.BY_19C, false);

    model.produceFactor();
    GammaCorrector g = model._RrCorrector;
    double[] t = g.getTable(256);
    double[] ct = g.getCorrectTable(256);
    double[] ut = g.getUncorrectTable(256);

    System.out.println(DoubleArray.toString(t));
    System.out.println(DoubleArray.toString(ct));
    System.out.println(DoubleArray.toString(ut));
  }

  protected final static double[] produceLinearInputTable(int level) {
    double[] table = new double[level];
    double step = 1. / (level - 1);
    for (int x = 0; x < level; x++) {
      table[x] = x * step;
    }
    return table;
  }

  protected void normalizeInputTable(double[] inputTable, double normal) {
    int size = inputTable.length;
    for (int x = 0; x < size; x++) {
      inputTable[x] /= normal;
      inputTable[x] = inputTable[x] > 1 ? 1 : inputTable[x];
    }
  }

  public RGB getLuminanceRGB(RGB digitRGB) {
    double[] rgbValues = digitRGB.getValues(new double[3], RGB.MaxValue.Double1);
    gammaCorrect(rgbValues);
    RGB luminanceRGB = new RGB(digitRGB.getRGBColorSpace(), rgbValues,
                               RGB.MaxValue.Double1);
    return luminanceRGB;
  }

  /**
   * 進行gamma校正
   * @param input double[]
   * @return double[]
   */
  public double[] gammaCorrect(double[] input) {
    if (doGammaCorrect) {
      //=======================================================================
      // 進行gamma校正
      //=======================================================================
      input[0] = _RrCorrector.correct(input[0]);
      input[1] = _GrCorrector.correct(input[1]);
      input[2] = _BrCorrector.correct(input[2]);
      //=======================================================================
    }

    return input;
  }

  public double[] gammaUncorrect(double[] input) {
    if (doGammaCorrect) {
      //=======================================================================
      // 進行gamma反校正
      //=======================================================================
      input[0] = _RrCorrector.uncorrect(input[0]);
      input[1] = _GrCorrector.uncorrect(input[1]);
      input[2] = _BrCorrector.uncorrect(input[2]);
      //=======================================================================
    }
    return input;
  }

  protected void produceGammaCorrector() {
    //=======================================================================
    // 進行gamma校正,但是測試結果是不要校正比較好-.-,是流程錯誤嗎?
    //=======================================================================
    if (doGammaCorrect) {
      List<Patch> grayScale = dcTarget.filter.grayScale();
      Set<Patch> grayScaleSet = new TreeSet<Patch> (grayScale);
      RGB black = dcTarget.getKeyRGB();
      black.setColorBlack();
      Patch blackPatch = new Patch(null, new CIEXYZ(), null, black);
      grayScaleSet.add(blackPatch);

      _RrCorrector = produceGammaCorrector(grayScaleSet, RGBBase.Channel.R);
      _GrCorrector = produceGammaCorrector(grayScaleSet, RGBBase.Channel.G);
      _BrCorrector = produceGammaCorrector(grayScaleSet, RGBBase.Channel.B);
    }
    //=======================================================================

  }

  private static GammaCorrector produceGammaCorrector(Set<Patch> grayScale,
      RGBBase.Channel ch) {
//    if (doGammaCorrect) {
    GammaCorrector rCorrecor = GammaCorrector.getLUTInstance(grayScale, ch);
    return rCorrecor;
//    }
//    else {
//      //不進行gamma校正,所以產生一個gamma為1的GammaCorrector
//      GammaCorrector rCorrecor = GammaCorrector.getExponentInstance(grayScale,
//          ch, 1.0, 1.0);
//      return rCorrecor;
//    }
  }

//  private static Set<Patch> addBlackPatch(Set<Patch> patchSet) {
//    Patch black = new Patch(null, new CIEXYZ(new double[] {0, 0, 0}), null,
//                            new
//                            RGB(RGB.RGBColorSpace.unknowRGB,
//                                new int[] {0, 0, 0}));
//    patchSet.add(black);
//    return patchSet;
//  }

  private Set<Patch> addWhitePatch(Set<Patch> patchSet) {
//    Patch white = new Patch(null, new CIEXYZ(new double[] {1, 1, 1}), null,
    Patch white = new Patch(null, illuminant.getNormalizeXYZ(), null,
                            new RGB(RGB.ColorSpace.unknowRGB,
                                    new int[] {255, 255, 255}));
    patchSet.add(white);
    return patchSet;
  }

  public final CIEXYZ getXYZ(RGB rgb) {
    return getXYZ(rgb, false);
  }

  public final RGB getRGB(CIEXYZ XYZ) {
    return getRGB(XYZ, false);
  }

  /**
   * 利用模式內的係數計算XYZ
   * @param rgb RGB
   * @param relativeXYZ boolean
   * @return CIEXYZ
   */
  public final CIEXYZ getXYZ(RGB rgb, boolean relativeXYZ) {
    if (rgb.getMaxValue() != dcTarget.getMaxValue()) {
      throw new IllegalArgumentException(
          "rgb.getMaxValue() != lcdTarget.getMaxValue()");
    }
    CIEXYZ result = _getXYZ(rgb);
    if (!relativeXYZ) {
      result.times(this.luminance.Y);
    }
    else {
      result.normalize(NormalizeY.Normal1);
    }
    return result;
  }

  public final RGB getRGB(CIEXYZ XYZ, boolean relativeXYZ) {
    if (!relativeXYZ) {
      XYZ = (CIEXYZ) XYZ.clone();
      XYZ.normalize(this.luminance);
    }
    return _getRGB(XYZ);
  }

  /**
   * 計算XYZ
   * @param rgb RGB
   * @return CIEXYZ
   */
  protected abstract CIEXYZ _getXYZ(RGB rgb);

  protected abstract RGB _getRGB(CIEXYZ XYZ);

  public static class DCModelFactor
      extends ModelFactor {
    public Illuminant illuminant;
    public Factor factor;

  }

  /**
   * 使用模式
   * @param dcModelFactor DCModelFactor
   */
  public DCModel(DCModelFactor dcModelFactor) {
    super(dcModelFactor);

    this.illuminant = dcModelFactor.illuminant;
    this.theModelFactor = dcModelFactor.factor;

    this._RrCorrector = theModelFactor.RrCorrecor;
    this._GrCorrector = theModelFactor.GrCorrecor;
    this._BrCorrector = theModelFactor.BrCorrecor;
    this.whitePatchXYZ = theModelFactor.whitePatch;
    this.normal = theModelFactor.normal;
  }

  /**
   * 求值模式
   * @param dcTarget DCTarget
   * @param doGammaCorrect boolean
   * @deprecated
   */
  public DCModel(DCTarget dcTarget, boolean doGammaCorrect) {
    this.dcTarget = dcTarget;
    this.illuminant = dcTarget.getIlluminant();
    evaluationMode = true;
    this.doGammaCorrect = doGammaCorrect;
  }

  /**
   * 求值模式
   * @param dcTarget DCTarget
   * @param doGammaCorrect boolean 是否進行gamma校正
   * @param targetRGBNormalize boolean 是否依照白色色塊進行RGB正規化
   */
  public DCModel(DCTarget dcTarget, boolean doGammaCorrect,
                 boolean targetRGBNormalize) {
    this.dcTarget = dcTarget;
    this.illuminant = dcTarget.getIlluminant();
    this.luminance = dcTarget.getLuminance();
    evaluationMode = true;
    this.doGammaCorrect = doGammaCorrect;

    if (targetRGBNormalize) {
      //依照白色色塊進行RGB正規化
      switch (this.dcTarget.getType()) {
        case CC24:
          normal = dcTarget.getPatch(18).getRGB().getValues();
          break;
        case CCSG:
          normal = dcTarget.getPatch(44).getRGB().getValues();
          break;
      }

      this.dcTarget.normalizeRGB(normal);
    }
  }

  protected DCModel() {

  }

  /**
   * 產生可永續儲存係數的class
   * @param factor Factor
   * @return LCDModelFactor
   */
  public final DCModelFactor produceDCModelFactor(Factor factor) {
    return produceDCModelFactor(factor, dcTarget.getDevice(),
                                dcTarget.getDescription() + "_" +
                                this.getDescription());
  }

  public final DCModelFactor produceDCModelFactor(Factor factor, String device,
                                                  String description) {
    DCModelFactor dcModelFactor = new DCModelFactor();
    dcModelFactor.illuminant = this.illuminant;
    dcModelFactor.factor = factor;
    dcModelFactor.device = device;
    dcModelFactor.description = description;
    return dcModelFactor;
  }

  /**
   * 以製作Profile所用的部份評估色差
   * 所謂製作Profile的部份,以CCSG和CCDC來說,就是去掉周圍的灰階色塊
   * 其他導表則沒有差異
   *
   * @param targetPatch DCTarget
   * @param doColorDividing boolean
   * @return DeltaEReport[]
   */
  public final DeltaEReport[] testTargetForProfile(DCTarget targetPatch,
      boolean doColorDividing) {
    return testForwardModel(targetPatch.filter.patchListForProfile(),
                            doColorDividing);
  }

  /**
   * 以整張導表做色差評估
   * @param targetPatch DCTarget
   * @param doColorDividing boolean
   * @return DeltaEReport[]
   */
  public final DeltaEReport[] testTarget(DCTarget targetPatch,
                                         boolean doColorDividing) {
    return testForwardModel(targetPatch.getLabPatchList(), doColorDividing);
  }

  public final List<Patch> produceForwardModelPatchList(final List<Patch>
      RGBpatchList) {
    int size = RGBpatchList.size();

    List<Patch> modelPatchList = new ArrayList<Patch> (size);
    CIEXYZ whitePoint = this.luminance;

    for (int x = 0; x < size; x++) {
      Patch p = RGBpatchList.get(x);
      RGB rgb = p.getRGB();
//      rgb.changeMaxValue(RGB.MaxValue.Double1);
      CIEXYZ XYZ = getXYZ(rgb, false);
      CIELab Lab = CIELab.fromXYZ(XYZ, whitePoint);

      Patch mp = new Patch(p.getName(), XYZ, Lab, rgb);
      modelPatchList.add(mp);
    }

    return modelPatchList;
  }

  /**
   *
   * @param XYZpatchList List
   * @return List
   */
  public final List<Patch> produceReverseModelPatchList(final List<Patch>
      XYZpatchList) {
    int size = XYZpatchList.size();

    List<Patch> modelPatchList = new ArrayList<Patch> (size);
    CIEXYZ whitePoint = this.luminance;

    /**
     * XYZ-> RGB 不像RGB->XYZ, 可以計算原始的XYZ跟預測的XYZ的誤差;
     * 所以採用羅老師的想法:
     * 1. RGB->XYZ
     * 2. XYZ->RGB'
     * 3. RGB'->XYZ'
     */
    for (int x = 0; x < size; x++) {
      Patch p = XYZpatchList.get(x);
      //前導先預測出XYZ
      CIEXYZ actualXYZ = getXYZ(p.getRGB(), false);
      //在反推得到RGB?
      RGB reverseRGB = getRGB(actualXYZ, false);
      if (reverseRGB != null) {
        //若得到RGB, 再前導算出XYZ
        CIEXYZ forwardXYZ = getXYZ(reverseRGB, false);
        CIELab Lab = CIELab.fromXYZ(forwardXYZ, whitePoint);
        Patch mp = new Patch(p.getName(), forwardXYZ, Lab, reverseRGB);
        modelPatchList.add(mp);
      }
      else {
        //無法取出正確值的色塊,只好使其色差為0
        Patch mp = new Patch(p.getName(), actualXYZ, p.getLab(), p.getRGB());
        modelPatchList.add(mp);
      }

    }

    return modelPatchList;
  }

  /**
   *
   * @return String
   */
  protected String getStoreFilename() {
    return null;
  }
}
