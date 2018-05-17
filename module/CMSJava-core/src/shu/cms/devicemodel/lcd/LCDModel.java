package shu.cms.devicemodel.lcd;

import java.io.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.covert.*;
import shu.cms.devicemodel.lcd.thread.*;
import shu.cms.devicemodel.lcd.util.*;
import shu.cms.lcd.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * LCDModel總共有三種類型
 * 1.不需迭代求係數,如PLCC,多項式迴歸
 *
 * 2.需迭代求係數,但三頻道間的係數彼此獨立,因此可以個別運算
 *   配合SimpleThreadCalculator使用.
 *   詳細使用方式請見SimpleThreadCalculator
 *
 * 3.需迭代求係數,但三頻道間的係數彼此相關,需三頻道的係數同時作迭代
 *   配合ThreadCalculator使用.
 *   詳細使用方式請見ThreadCalculator
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class LCDModel extends LCDModelBase {

    /**
     * 使用模式
     * @param flare CIEXYZ
     * @param RMax CIEXYZ
     * @param GMax CIEXYZ
     * @param BMax CIEXYZ
     * @param factors Factor[]
     */
    protected void init(CIEXYZ flare, CIEXYZ RMax, CIEXYZ GMax, CIEXYZ BMax,
                      Factor[] factors) {
        this.flare.setFlare(flare);

        //==========================================================================
        // 減掉漏光因素
        //==========================================================================
        if (RMax != null && GMax != null && BMax != null) {
            max = matries.getMaxMatrix(CIEXYZ.minus(RMax, flare),
                                       CIEXYZ.minus(GMax, flare),
                                       CIEXYZ.minus(BMax, flare));
            maxInverse = DoubleArray.inverse(max);
        }
        //==========================================================================

        this.theModelFactors = factors;
        evaluationMode = false;
    }

    protected LCDModel() {

    }

    /**
     * 使用模式
     * @param factor LCDModelFactor
     */
    public LCDModel(LCDModelFactor factor) {
        super(factor);

        this.flare.flareValues = factor.flare;
        this.flare.flareXYZ = new CIEXYZ(flare.flareValues,
                                         factor.targetWhitePoint);
        this.max = factor.max;
        this.maxInverse = factor.maxInverse;
        this.theModelFactors = factor.factors;
        this.luminance = factor.luminance;
        this.correct._RrCorrector = factor.rCorrector[0];
        this.correct._GrCorrector = factor.rCorrector[1];
        this.correct._BrCorrector = factor.rCorrector[2];
        this.targetWhitePoint = factor.targetWhitePoint;
    }

    /**
     * 求值模式
     * @param lcdTarget LCDTarget
     */
    public LCDModel(LCDTarget lcdTarget) {
        this(lcdTarget, lcdTarget);
    }

    /**
     *
     * @param lcdTarget LCDTarget
     * @param rCorrectLCDTarget LCDTarget
     * @param cooperateWithLCDTargetInterpolator boolean 是否與LCDTargetInterpolator共作?
     */
    protected LCDModel(LCDTarget lcdTarget, LCDTarget rCorrectLCDTarget,
                       boolean cooperateWithLCDTargetInterpolator) {
        init(LCDModelUtil.evaluateFlare(FLARE_TYPE, lcdTarget),
             cooperateWithLCDTargetInterpolator ? null :
             lcdTarget.getSaturatedChannelPatch(RGBBase.Channel.R).getXYZ(),
             cooperateWithLCDTargetInterpolator ? null :
             lcdTarget.getSaturatedChannelPatch(RGBBase.Channel.G).getXYZ(),
             cooperateWithLCDTargetInterpolator ? null :
             lcdTarget.getSaturatedChannelPatch(RGBBase.Channel.B).getXYZ(),
             null);

        this.lcdTarget = lcdTarget;
        whiteRGB = lcdTarget.getWhitePatch().getRGB();
        this.rCorrectLCDTarget = rCorrectLCDTarget;
        this.luminance = lcdTarget.getLuminance();
        this.targetWhitePoint = lcdTarget.getWhitePatch().getXYZ();
        //由於計算非常耗資源,所以把執行緒設小
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 1);
        evaluationMode = true;

    }

    public LCDModel(LCDTarget lcdTarget, LCDTarget rCorrectLCDTarget) {
        this(lcdTarget, rCorrectLCDTarget, false);
    }

    /**
     * 比較 "模式預測的白點" 與 "導具白點" 之間的色差
     * @return DeltaE
     */
    public DeltaE getWhiteDeltaE() {
//    CIEXYZ modelWhite = getXYZ(whiteRGB, false);
//    CIEXYZ targetWhite = targetWhitePoint;
//    CIELab modelLab = CIELab.fromXYZ(modelWhite, targetWhite);
//    CIELab targetLab = CIELab.fromXYZ(targetWhite, targetWhite);
//    DeltaE deltaE = new DeltaE(modelLab, targetLab);
//    return deltaE;
        return getWhiteDeltaE(this.theModelFactors);
    }

    protected DeltaE getWhiteDeltaE(Factor[] factors) {
        CIEXYZ modelWhite = getXYZ(whiteRGB, factors, false);
        CIEXYZ targetWhite = targetWhitePoint;
        CIELab modelLab = CIELab.fromXYZ(modelWhite, targetWhite);
        CIELab targetLab = CIELab.fromXYZ(targetWhite, targetWhite);
        DeltaE deltaE = new DeltaE(modelLab, targetLab);
        return deltaE;
    }

    protected DisplayLUT displayLUT = null;

    /**
     * 前導,利用模式內的係數計算XYZ
     * @param rgb RGB
     * @param relativeXYZ boolean 如果是relativeXYZ,就不會計入漏光,如果是relativeXYZ,就會把漏光計入
     * @return CIEXYZ
     */
    public final CIEXYZ getXYZ(RGB rgb, boolean relativeXYZ) {
        return getXYZ(rgb, theModelFactors, relativeXYZ);
    }

    private CIEXYZ normalizeWhiteXYZ;
    public final CIEXYZ getNormalizeXYZ(RGB rgb) {
        CIEXYZ XYZ = getXYZ(rgb, theModelFactors, false);
        if (null == normalizeWhiteXYZ) {
            normalizeWhiteXYZ = this.getWhiteXYZ(false);
        }
        XYZ.normalize(normalizeWhiteXYZ);
        return XYZ;
    }

    public final CIEXYZ getWhiteXYZ() {
        if (null == whiteXYZ) {
            RGB rgb = (RGB)this.whiteRGB.clone();
            this.changeMaxValue(rgb);
            whiteXYZ = getXYZ(rgb, false);
        }
        return whiteXYZ;
    }

    private CIEXYZ whiteXYZ = null;
    /**
     *
     * @param relativeXYZ boolean
     * @return CIEXYZ
     * @deprecated
     */
    public final CIEXYZ getWhiteXYZ(boolean relativeXYZ) {
        RGB rgb = (RGB)this.whiteRGB.clone();
        this.changeMaxValue(rgb);
        return getXYZ(rgb, relativeXYZ);
    }

    public final CIELab getLab(RGB rgb, boolean changeMaxValue) {
        CIEXYZ XYZ = getXYZ(rgb, false, changeMaxValue);
        CIEXYZ whiteXYZ = this.getWhiteXYZ();
        CIELab Lab = new CIELab(XYZ, whiteXYZ);
        return Lab;
    }

    public final CIEXYZ getXYZ(RGB rgb, boolean relativeXYZ,
                               boolean changeMaxValue) {
        if (changeMaxValue) {
            this.changeMaxValue(rgb);
        }
        return getXYZ(rgb, relativeXYZ);
    }

    public final boolean isCoverting() {
        if (this.covert != null) {
            return covert.isCoverting();
        } else {
            return false;
        }
    }

    final RGB getRGB(CIEXYZ XYZ, boolean relativeXYZ, boolean doCovert) {
        RGB rgb = null;
        if (doCovert && covertMode) {
            initReverseModelCovert();
            rgb = covert.getRGB(XYZ, relativeXYZ);
            rgbResult.covertRGB = true;
        } else {
            rgb = getRGB(XYZ, theModelFactors, relativeXYZ);
            rgbResult.covertRGB = false;
        }

//    if (!rgb.isLegal()) {
//      System.out.println("");
//    }

        rgb.changeMaxValue(this.getMaxValue());
        //==========================================================================
        // 計算inverseLab的deltaE
        //==========================================================================
        _getRGBDeltaE = calculateGetRGBDeltaE(rgb, XYZ, relativeXYZ);
        //==========================================================================
        rgbResult.resultRGB = rgb;
        return rgb;
    }

    /**
     * 反推
     * @param XYZ CIEXYZ
     * @param relativeXYZ boolean
     * @return RGB
     */
    public final RGB getRGB(CIEXYZ XYZ, boolean relativeXYZ) {
        return getRGB(XYZ, relativeXYZ, true);
    }

//  private RGB covertInitRGB;
    /**
     * 初始化反推的covert(利用前導彌補反推)
     */
    protected void initReverseModelCovert() {
        if (covert == null) {
            covert = new ReverseModelCovert(this);
//      covert = new ReverseModelCovert(this,ReverseModelCovert.Mode.NonCovert);
//      covert = new ReverseModelCovert(this, ReverseModelCovert.Mode.Around);
            covert.setWhiteRGB(this.whiteRGB);
        }
    }

    /**
     * 用前導模式去預測(內插)反推模式，弭補反推模式的不準確性.
     */
    private ReverseModelCovert covert = null;
    /**
     * 用前導模式去預測(內插)反推模式，弭補反推模式的不準確性.
     */
    protected boolean covertMode = false;

    /**
     * 用前導模式去預測(內插)反推模式，弭補反推模式的不準確性.
     * @param covert boolean
     */
    public void setCovertMode(boolean covert) {
        this.covertMode = covert;
    }

    /**
     * 找到white維xyY所組成的white RGB
     * @param xyY CIExyY
     * @param maxcode double
     * @param tolerance double
     * @param maxLuminanceLimit boolean
     * @return RGB
     */
    public final RGB calculateWhiteRGB(CIExyY xyY, double maxcode,
                                       double tolerance,
                                       boolean maxLuminanceLimit) {
        if (whiteRGBCalc == null) {
            whiteRGBCalc = new RBCalculator(this);
        }
        if (maxLuminanceLimit) {
            //計算出最大可用到的亮度
            double maxLumi = getAvailableWhiteMaxLuminance(xyY);
            //設定最大亮度限制條件
            whiteRGBCalc.setMaxLuminanceConstraint(maxLumi);
        }
        RGB rgb = whiteRGBCalc.getWhiteRGB(xyY, maxcode, tolerance, false);
        //重新指定最大值, 避免些許的運算誤差
        rgb.setValue(rgb.getMaxChannel(), maxcode);
        return rgb;
    }

    protected RBCalculator whiteRGBCalc = null;

    public final DeltaE getWhiteRGBDeltaE() {
        if (whiteRGBCalc == null) {
            return null;
        }
        return whiteRGBCalc.getRBDeltaE();
    }

    public static class RGBResult {
        public RGB firstRGB;
        public RGB resultRGB;
        public boolean illegalXYZ;
        public boolean covertRGB;
    }


    protected RGBResult rgbResult = new RGBResult();
    /**
     * 不是multi-thread safe
     * @return RGBResult
     */
    public RGBResult getRGBResult() {
        return rgbResult;
    }

    protected final RGB getRGB(CIEXYZ XYZ, LCDModel.Factor[] factor,
                               boolean relativeXYZ) {
        if (factor.length != 3) {
            throw new IllegalArgumentException("factor.length !=3");
        }
        CIEXYZ fromXYZ = this.fromXYZ(XYZ, relativeXYZ);
        rgbResult.illegalXYZ = !fromXYZ.isLegal();
        RGB rgb = _getRGB(fromXYZ, factor);
        rgb = rational.RGBRationalize(rgb);
        if (displayLUT != null) {
            rgb = displayLUT.getOutputRGB(rgb);
        }

        return rgb;
    }

    /**
     * getXYZ時, 自動修改RGB的MaxValue值
     */
    private boolean autoRGBChangeMaxValue = false;

    /**
     * 計算XYZ
     * @param rgb RGB
     * @param factor Factor[]
     * @param relativeXYZ boolean
     * @return CIEXYZ
     */
    protected CIEXYZ getXYZ(RGB rgb, LCDModel.Factor[] factor,
                            boolean relativeXYZ) {
        RGB.MaxValue rgbMaxValue = rgb.getMaxValue();
        RGB.MaxValue targetMaxValue = (null != lcdTarget) ?
                                      lcdTarget.getMaxValue() : null;
        if (autoRGBChangeMaxValue) {
            this.changeMaxValue(rgb);
        } else if (null != lcdTarget && (rgbMaxValue != targetMaxValue) &&
                   !(rgbMaxValue == RGB.MaxValue.Double255 &&
                     targetMaxValue == RGB.MaxValue.Int8Bit)) {
            throw new IllegalArgumentException(
                    "rgb.getMaxValue() != lcdTarget.getMaxValue()");
        }

        if (factor.length != 3) {
            throw new IllegalArgumentException("factor.length !=3");
        }

        if (displayLUT != null) {
            rgb = displayLUT.getOutputRGB(rgb);
        }

        CIEXYZ XYZ = _getXYZ(rgb, factor);
        XYZ = rational.XYZRationalize(XYZ);
        XYZ = this.toXYZ(XYZ, relativeXYZ);
        return XYZ;
    }

    /**
     * 計算XYZ,前導模式
     * @param rgb RGB
     * @param factor Factor[]
     * @return CIEXYZ
     */
    protected abstract CIEXYZ _getXYZ(RGB rgb, LCDModel.Factor[] factor);

    /**
     * 計算RGB,反推模式
     * @param relativeXYZ CIEXYZ
     * @param factor Factor[]
     * @return RGB
     */
    protected abstract RGB _getRGB(CIEXYZ relativeXYZ, LCDModel.Factor[] factor);

    /**
     * 求係數
     * @return Factor[]
     */
    protected abstract Factor[] _produceFactor();

    public final Factor[] produceFactor() {
        if (theModelFactors == null) {
            this.produceStart();
            Factor[] factor = _produceFactor();
            this.setTheModelFactors(factor);
            this.produceEnd();
        }
        return theModelFactors;
    }

    public final List<Patch> produceReverseModelPatchList(final List<Patch>
            XYZpatchList) {
        return test.reverseModelPatchList(XYZpatchList, theModelFactors,
                                          targetWhitePoint);
    }

    public final List<Patch> produceForwardModelPatchList(final List<Patch>
            RGBpatchList) {
        return test.forwardModelPatchList(RGBpatchList, theModelFactors,
                                          targetWhitePoint);
    }

    /**
     * 經過模式調整後的RGB係數
     */
    protected RGB getXYZRGB;

    public Factor[] getModelFactors() {
        return theModelFactors;
    }

    private void setTheModelFactors(Factor[] modelFactors) {
        this.theModelFactors = modelFactors;
    }

    /**
     * 設定LCDModel的DisplayLUT
     * @param displayLUT DisplayLUT
     */
    public void setDisplayLUT(DisplayLUT displayLUT) {
        this.displayLUT = displayLUT;
    }

    /**
     * getXYZ時, 是否自動修改RGB的MaxValue值
     * @param autoRGBChangeMaxValue boolean
     */
    public void setAutoRGBChangeMaxValue(boolean autoRGBChangeMaxValue) {
        this.autoRGBChangeMaxValue = autoRGBChangeMaxValue;
    }

    public void setWhiteRGB(RGB whiteRGB) {
        this.whiteRGB = whiteRGB;
    }

    public boolean hasDisplayLUT() {
        return displayLUT != null;
    }

    /**
     * 從factors產生的patch與patchList的色差分析並且產生DeltaEReport.
     * @param factors Factor[]
     * @param patchList List
     * @param whitePatch Patch
     * @return DeltaEReport[]
     */
    protected DeltaEReport[] getDeltaEReport(Factor[] factors, List<Patch>
            patchList, Patch whitePatch) {
        List<Patch>
                modelPatchList = test.forwardModelPatchList(patchList, factors,
                whitePatch.getXYZ());

        DeltaEReport[] reports = DeltaEReport.Instance.patchReport(patchList,
                modelPatchList, false);
        reports[0].whitePointDeltaE = getWhiteDeltaE(factors);
        return reports;
    }

    public static void main(String[] args) throws IOException {
//    example2(null);
//    System.exit( -1);
        LCDTarget lcdTarget = LCDTarget.Instance.get("EIZO_CE240W",
                LCDTarget.Source.i1pro,
                LCDTarget.Room.Dark,
                LCDTarget.TargetIlluminant.D65,
                LCDTargetBase.Number.Ramp1021, null, null);

        GOGModelThread model = new GOGModelThread(lcdTarget);
    }

    protected RGB whiteRGB = null;

    /**
     * 利用model的係數算出設備白點
     * @return CIEXYZ
     */
    public final CIEXYZ getModelWhite() {
        if (modelWhite == null) {
            RGB rgb = null;
            if (null != lcdTarget) {
                rgb = (whiteRGB == null) ? lcdTarget.getWhitePatch().getRGB() :
                      whiteRGB;
            } else {
                rgb = RGB.White;
            }
//      RGB rgb = (whiteRGB == null) ? lcdTarget.getWhitePatch().getRGB() :
//          whiteRGB;
            modelWhite = getXYZ(rgb, false);
        }
        return modelWhite;
    }

    protected void setModelWhite(CIEXYZ modelWhite) {
        this.modelWhite = modelWhite;
    }

    public RGB getWhiteRGB() {
        return whiteRGB;
    }

    private CIEXYZ modelWhite = null;

    private transient DeltaE _getRGBDeltaE;

    public DeltaE getRGBDeltaE() {
        return _getRGBDeltaE;
    }

    public Test test = new Test();

    public final class Test {

        /**
         * 檢查LCDModel是否具有對稱性 ( A->B & B->A 都成立)
         * @param rgb RGB
         * @return DeltaE
         */
        public final DeltaE symmetry(RGB rgb) {
            CIEXYZ XYZ1 = getXYZ(rgb, false);
            RGB rgb1 = getRGB(XYZ1, false);
            CIEXYZ XYZ2 = getXYZ(rgb1, false);
            return new DeltaE(XYZ1, XYZ2, getLuminance());
        }

        /**
         * 檢查LCDModel是否具有對稱性 ( A->B & B->A 都成立)
         * @param rgbList List
         * @return DeltaEReport[]
         */
        public final DeltaEReport[] symmetry(List<RGB> rgbList) {
            List<Patch> rgbPatchList = Patch.Produce.RGBPatches(rgbList);
            int size = rgbPatchList.size();

            for (int x = 0; x < size; x++) {
                Patch p = rgbPatchList.get(x);
                CIEXYZ XYZ = getXYZ(p.getRGB(), false);
                CIELab Lab = new CIELab(XYZ, targetWhitePoint);
                RGB rgb = getRGB(XYZ, false);
                Patch.Operator.setName(p, p.getRGB().toString());
                Patch.Operator.setXYZ(p, XYZ);
                Patch.Operator.setLab(p, Lab);
                Patch.Operator.setRGB(p, rgb);
            }

            return testForwardModel(rgbPatchList, false);
        }

        /**
         * 以係數產生Patch (求值時使用)
         * @param RGBpatchList List
         * @param factors Factor[]
         * @param whitePoint CIEXYZ
         * @return List
         */
        public final List<Patch> forwardModelPatchList(final List<Patch>
                RGBpatchList, Factor[] factors, CIEXYZ whitePoint) {
            if (factors.length != 3) {
                throw new IllegalArgumentException("factors.length != 3");
            }

            int size = RGBpatchList.size();

            List<Patch> modelPatchList = new ArrayList<Patch>(size);

            for (int x = 0; x < size; x++) {
                Patch p = RGBpatchList.get(x);
                RGB rgb = p.getRGB();
                CIEXYZ XYZ = getXYZ(rgb, factors, false);
                CIELab Lab = CIELab.fromXYZ(XYZ, whitePoint);
//        CIELab Lab = CIELab.fromXYZAdaptedToD65(XYZ, whitePoint);

                Patch mp = new Patch(p.getName(), XYZ, Lab, rgb);
                modelPatchList.add(mp);
            }

            return modelPatchList;
        }

        private final List<Patch> forwardModelPatchList(final List<Patch>
                RGBpatchList) {
            return forwardModelPatchList(RGBpatchList, theModelFactors,
                                         targetWhitePoint);
        }

        /**
         * 產生驗證反推模式所需的patchList
         *
         * RGB -> XYZ" -> RGB' -> XYZ'
         * XYZ
         * 計算XYZ和XYZ'的色差
         *
         * 假設前導是完全正確的前提下, XYZ和XYZ"應該是相同的, 會有差異的只有XYZ"與XYZ'間,
         * 因為是由反推模式 XYZ" -> RGB' 所造成的誤差
         *
         * @param XYZpatchList List
         * @param factors Factor[]
         * @param whitePoint CIEXYZ
         * @return List
         */
        private final List<Patch> reverseModelPatchList(final List<Patch>
                XYZpatchList, Factor[] factors, CIEXYZ whitePoint) {
            if (factors.length != 3) {
                throw new IllegalArgumentException("factors.length != 3");
            }

            int size = XYZpatchList.size();

            List<Patch> modelPatchList = new ArrayList<Patch>(size);

            for (int x = 0; x < size; x++) {
                final Patch p = XYZpatchList.get(x);
                //先從色塊的RGB計算出XYZ
                CIEXYZ actualXYZ = getXYZ(p.getRGB(), false);
                //再從XYZ算出RGB'
//        {
//          RGB rgb = p.getRGB();
//          if (rgb.R == 17 && rgb.G == 34 && rgb.B == 170) {
//            System.out.println("");
//          }
//        }
                RGB reverseRGB = getRGB(actualXYZ, false);
                if (reverseRGB != null) {
                    //再從RGB'算出XYZ'
                    CIEXYZ forwardXYZ = getXYZ(reverseRGB, factors, false);
                    //XYZ'計算出Lab'
                    CIELab Lab = CIELab.fromXYZ(forwardXYZ, whitePoint);
//          CIELab Lab = CIELab.fromXYZAdaptedToD65(forwardXYZ, whitePoint);

                    Patch mp = new Patch(p.getName(), forwardXYZ, Lab,
                                         reverseRGB);
                    modelPatchList.add(mp);
                } else {
                    //無法取出正確值的色塊,只好使其色差為0
                    Patch mp = new Patch(p.getName(), actualXYZ, p.getLab(),
                                         p.getRGB());
                    modelPatchList.add(mp);
                }

            }

            return modelPatchList;
        }

        private final List<Patch> reverseModelPatchList(final List<Patch>
                XYZpatchList) {
            return reverseModelPatchList(XYZpatchList, theModelFactors,
                                         targetWhitePoint);
        }
    }


    private Factor[] theModelFactors;

}
