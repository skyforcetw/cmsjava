package vv.cms.lcd.calibrate;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.independ.IPT;
import shu.cms.devicemodel.lcd.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.cms.hvs.cam.ciecam02.CIECAM02;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.lut.*;
import shu.math.operator.*;
import shu.math.operator.Operator;
import shu.math.regress.*;
import shu.util.log.*;
import vv.cms.lcd.calibrate.parameter.*;
import vv.cms.lcd.material.Material;

///import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 符合LCD特性的CCT曲線產生器
 * 主要提供三種CCTCurve的產生方法
 * 1. getAdaptivexyYCurve(int adaptiveStart, int adaptiveEnd, ColorSpace colorSpace)
 *
 * 2. getAdaptivexyYCurve(int adaptiveStart, int adaptiveEnd, Method method)
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CCTCurveProducer {

    private LCDModel model;
    //pattern1 間隔17 (疏) 沒通過沒關係, 因為pat-gen沒此pattern.
    public final static int[] PATTERN_1 = new int[] {
                                          51, 34, 17};
    //pattern2 間隔8 (密)
    public final static int[] PATTERN_2 = new int[] {
                                          56, 48, 40, 32, 24, 16, 8};
    //pattern2 變形, 間隔8
    public final static int[] PATTERN_2_EVOLVE = new int[] {
                                                 50, 42, 34, 26, 18, 10};
    //pattern 間隔4 (更密)
    public final static int[] PATTERN_3 = new int[] {
                                          56, 52, 48, 44, 40, 36, 32, 28, 24,
                                          20, 16, 12, 8, 4};

    private CIEXYZ white;

    /**
     * 依照 model 特性產生CCT Curve
     * @param model LCDModel
     * @param parameter Parameter
     */
    protected CCTCurveProducer(LCDModel model, CCTParameter parameter) {
        this(model, null, parameter);
    }

    /**
     * 依照 model 特性產生CCT Curve
     * @param model LCDModel
     * @param idealWhiteYArray double[]
     * @param parameter CCTParameter
     */
    protected CCTCurveProducer(LCDModel model, double[] idealWhiteYArray,
                               CCTParameter parameter) {
        this.model = model;
        if (idealWhiteYArray != null) {
            if (idealWhiteYArray.length != 256) {
                throw new IllegalArgumentException(
                        "idealWhiteYArray.length != 256");
            }
            double[] codeArray = DoubleArray.buildX(0, 255, 256);
            //產生白的對照表
            whiteYLut = new Interpolation1DLUT(codeArray,
                                               idealWhiteYArray);
        }
        setParameter(parameter);
    }

    private RGB rgb = new RGB(RGB.ColorSpace.unknowRGB,
                              RGB.MaxValue.Double1);

    /**
     * code對亮度的對照表
     */
    private Interpolation1DLUT whiteYLut = null;

    /**
     *
     * <p>Title: Colour Management System</p>
     *
     * <p>Description: a Colour Management System by Java</p>
     * 以色溫變化為基礎, 找到人眼可接受的色溫變化曲線後, 再轉到色度座標, 得到CIExy曲線
     *
     * <p>Copyright: Copyright (c) 2008</p>
     *
     * <p>Company: skygroup</p>
     *
     * @author skyforce
     * @version 1.0
     */
    protected final class CorrectedxyYCurve {
        private double simutlatedabThreshold;
        protected double getSimutlatedabThreshold() {
            return simutlatedabThreshold;
        }

        /**
         * 依照dabThreshold可容許色差模擬並進行回歸
         * @param dabThreshold double
         * @return PolynomialRegression
         */
        protected final PolynomialRegression simulateRegression(double
                dabThreshold) {
            if (p == null) {
                throw new IllegalStateException("p == null");
            }

            double[] CCTArray = simulate(dabThreshold);
            double[] doubleCodeArray = IntArray.toDoubleArray(p.codeArray);

            Polynomial.COEF_1 coef = PolynomialRegression.
                                     findBestPolynomialCoefficient1(
                    doubleCodeArray, CCTArray);
            PolynomialRegression regress = new PolynomialRegression(
                    doubleCodeArray,
                    CCTArray, coef);
            regress.regress();

            return regress;
        }

        /**
         * 每一次CCT調整所採用的step(step過小消耗時間, step過大會不精確)
         */
        private final static int CCTStep = 1;

        /**
         *
         * @param dabThreshold double
         * @return double[]
         */
        protected final double[] simulate(double dabThreshold) {
            if (p == null) {
                throw new IllegalStateException("p == null");
            }

            int size = p.codeArray.length;
            double[] CCTArray = new double[size];
            CCTArray[0] = p.getCCT();
            double[] YArray = getYArray(IntArray.toDoubleArray(p.codeArray));

            if (dabThreshold == -1) {
                //沒有設定色差閾值的狀況下
                double finalCCT = p.black.getCCT();
                dabThreshold = 0.0;
                PolynomialRegression regress = null;
                double CCT = 0;

                //逐漸增加色差閾值, 讓最終色溫可以超出螢幕的漏光色溫
                do {
                    dabThreshold += 0.0125;
                    regress = simulateRegression(dabThreshold);
                    CCT = regress.getPredict(new double[] {0})[0];
                } while (CCT < finalCCT);

                simutlatedabThreshold = dabThreshold;
                p.dabThreshold = dabThreshold;
                CCTArray = regress.getMultiPredict(IntArray.toDoubleArray(p.
                        codeArray));

            } else {
                for (int x = 1; x < size; x++) {
                    double CCT = CCTArray[x - 1];
                    CIExyY xyY = CCT2xyY((int) CCT);
                    xyY.Y = YArray[x];
                    CIEXYZ XYZ = xyY.toXYZ();

                    while (true) {
                        CCT += CCTStep;
                        CIExyY targetxyY = CCT2xyY((int) CCT);
                        targetxyY.Y = YArray[x];
                        CIEXYZ targetXYZ = targetxyY.toXYZ();
                        DeltaE de = new DeltaE(XYZ, targetXYZ, white);
                        double dab = de.getCIE2000Deltaab();
                        if (dab >= dabThreshold) {
                            //當色差剛好大過閾值, 就跳出
                            CCTArray[x] = CCT - CCTStep;
                            break;
                        }
                    }
                }
            }

            return CCTArray;
        }

        /**
         * 以regression fit的CCT Curve, 計算出所有code(0~255)對應的CCT
         * @return double[]
         */
        protected final double[] getRegressionCCTCurve() {
            PolynomialRegression regress = simulateRegression(p.dabThreshold);
            int size = p.startCode + 1;
            double[] CCTCurve = new double[256];

            //轉折點以前, 透過回歸結果來預測適當CCT
            for (int x = 0; x < size; x++) {
                CCTCurve[x] = regress.getPredict(new double[] {x})[0];
            }
            //轉折點以後, CCT與白點同
            for (int x = size; x < 256; x++) {
                CCTCurve[x] = p.getCCT();
            }

            return CCTCurve;
        }

        /**
         * 修正漏光的色度座標點所產生的新CCT曲線
         * 產生流程:
         * simulate => simulateRegression => getRegressionCCTCurve
         *  => getCorrectedxyYCurve
         * @return CIExyY[]
         */
        public final CIExyY[] getCorrectedxyYCurve() {
            //以regression產生fit的CCT曲線
            double[] CCTCurve = getRegressionCCTCurve();
            int size = CCTCurve.length;

            //==========================================================================
            // 計算白點調整的參數
            // 從色溫座標轉到p的白點色度坐標上
            //==========================================================================
            //回歸出來的白點u'v'
            double[] CCTWhiteuvp = CCT2xyY((int) CCTCurve[size - 1]).
                                   getuvPrimeValues();
            //目標的白點u'v'
            double[] whiteuvp = p.getWhiteXYZ().getuvPrimeValues();
            //計算兩個白點的偏移
            double[] whiteoffset = DoubleArray.minus(CCTWhiteuvp, whiteuvp);
            //產生修正可偏移的物件
            OffsetOperator whiteoffsetOP = new OffsetOperator(whiteoffset);
            //==========================================================================

            //==========================================================================
            // 計算CIExyY
            //==========================================================================
            double[][] uvpCurve = new double[size][];
            for (int x = 0; x < size; x++) {
                //有可能找不到對應的xy(D光源的狀況)
                CIExyY xyY = CCT2xyY((int) CCTCurve[x]);
                double[] uvp = xyY.getuvPrimeValues();
                //白點的修正
                uvpCurve[x] = whiteoffsetOP.getXY(uvp);
            }
            //==========================================================================

            double[] panelblackuvp = p.black.getuvPrimeValues();
            double[] blackuvp = uvpCurve[0];

            //==========================================================================
            // 產生調整用OP
            //==========================================================================
            Operator offsetOP = Operator.getAdjustOperator(whiteuvp, blackuvp,
                    panelblackuvp);
            //==========================================================================

            //==========================================================================
            // 進行調整
            //==========================================================================
            CIExyY[] correctedxyYCurve = new CIExyY[size];
            for (int x = 0; x < size; x++) {
                double[] correct = offsetOP.getXY(uvpCurve[x]);
                CIExyY xyY = new CIExyY();
                xyY.setuvPrimeValues(correct);
                xyY.Y = getY(x);
                correctedxyYCurve[x] = xyY;
            }
            //==========================================================================

            return correctedxyYCurve;
        }
    }


    public static enum ColorSpace {
        /**
         * 在IPT空間上取等視覺差異
         */
        IPT,
        /**
         * 在CIECAM02空間上取等視覺差異
         */
        CIECAM02
    }


    public static enum Method {
        /**
         * 以IPT取等間距視覺差異
         */
        ByIPT,
        /**
         * DeltaE取等間距視覺差異,在此是採用CIEDE2000
         */
        ByDeltaE
    }


    protected abstract class AdaptivexyYCurveBase {
        private CompositePolynomialRegression adaptiveRegress;
        protected int start;
        /**
         * 產生一個在start到end之間的code, delta ab均勻的CIExyY變化曲線.
         * 期望產生0~255的所有CIExyY
         *
         * 因為預期Y對於dab影響不大, 所以用panel原生的Y來替代(當然這個假設尚未也較難被驗證)
         * 但如果有目標的亮度曲線，則以目標亮度的Y來替代，應該較為準確。
         *
         * @return CIExyY[]
         */
        protected final CIExyY[] getAdaptivexyYCurve() {
            //0~startCode
            int size = p.startCode;
            CIExyY[] xyYCurve = new CIExyY[256];

            //========================================================================
            // 計算0和1兩個code的xyY; 以回歸預測.
            //========================================================================

            for (int x = 0; x < 2; x++) {
//        CIEXYZ XYZ = model.getXYZ(new RGB(x, x, x), false, true);
//        CIExyY xyY = new CIExyY(XYZ);
//        xyY.Y = getY(x);
//        xyYCurve[x] = xyY;
                CIExyY xyY = this.getRegressionPredictxyY(x);
                xyYCurve[x] = xyY;
            }
            //========================================================================

            //========================================================================
            // 計算startCode~255的xyY; 轉折點以後僅改變亮度.
            //========================================================================
            CIExyY whitexyY = new CIExyY(p.getWhiteXYZ());
            for (int x = size; x < 256; x++) {
                CIExyY xyY = (CIExyY) whitexyY.clone();
                xyY.Y = getY(x);
                xyYCurve[x] = xyY;
            }
            //========================================================================

            //========================================================================
            // 計算2~ startCode-1 的xyY; 基於視覺均勻產生
            //========================================================================
            CIExyY[] dimxyYCurve = getPredictedxyYCurve(xyYCurve[1],
                    xyYCurve[size], false);
            System.arraycopy(dimxyYCurve, 0, xyYCurve, 2, dimxyYCurve.length);
            //========================================================================

            return xyYCurve;
        }

        protected final double[] getRegressPredict(double code) {
            return DoubleArray.transpose(adaptiveRegress.getMultiPredict(new double[] {
                    code}))[0];
        }

        protected final void initAdaptiveRegress() {
            //以適應code區段以及u'和v'作回歸
//      adaptiveRegress = getAdaptiveRegression(start, end);
            adaptiveRegress = getAdaptiveRegression(start);
        }

        /**
         * 將CIEXYZ轉換到相對應的色彩座標
         * @param XYZ CIEXYZ
         * @return double[]
         */
        protected abstract double[] getColorSpaceValues(final CIEXYZ XYZ);

        /**
         * 抓取白點與螢幕的adaptiveStart的色度座標, 所得到的一次回歸方程式.
         * 建立code與選定色彩座標的ab座標, 之間的關係.
         *
         * @param adaptiveCode int 適應起點
         * @return CompositePolynomialRegression
         */
        protected final CompositePolynomialRegression getAdaptiveRegression(
                int adaptiveCode) {
//      int size = 2;
            double[][] abdata = new double[2][];

            //填滿適應所採用的ab
            rgb.setValue(RGBBase.Channel.W, adaptiveCode, RGB.MaxValue.Int8Bit);
            CIEXYZ XYZ = model.getXYZ(rgb, false);
            abdata[0] = getColorSpaceValues(XYZ);
            abdata[1] = getColorSpaceValues(white);

            double[][] output = DoubleArray.transpose(abdata);

            CompositePolynomialRegression regress = new
                    CompositePolynomialRegression(
                            new double[] {adaptiveCode, p.startCode}, output[0],
                            output[1],
                            Polynomial.COEF_1.BY_1C,
                            CompositePolynomialRegression.Type.Series);
            regress.regress();
            return regress;
        }

        /**
         * 以回歸的方式預測code所對應到的XYZ
         * @param code double
         * @return CIEXYZ
         */
        protected abstract CIExyY getRegressionPredictxyY(double code);

        /**
         * 預測出code~到轉折點的xyY曲線
         * @param code1xyY CIExyY
         * @param startcodexyY CIExyY
         * @param regress boolean
         * @return CIExyY[]
         */
        protected abstract CIExyY[] getPredictedxyYCurve(CIExyY code1xyY,
                CIExyY startcodexyY, boolean regress);

    }


    /**
     *
     * <p>Title: Colour Management System</p>
     *
     * <p>Description: a Colour Management System by Java</p>
     * 設定好適應code的區間, 以多項式建立適應區間以及白點間的CIExyY直線.
     * 再基於此直線, 產生視覺可接受的CIExy曲線.
     *
     * <p>Copyright: Copyright (c) 2008</p>
     *
     * <p>Company: skygroup</p>
     *
     * @author skyforce
     * @version 1.0
     */
    protected class AdaptivexyYCurve extends AdaptivexyYCurveBase {
        private double dab;
        private Interpolation1DLUT accDeltaLut;
        private Method method;

        protected double[] getColorSpaceValues(final CIEXYZ XYZ) {
            double[] uvp = XYZ.getuvPrimeValues();
//      double[] result = new double[] {
//          uvp[0], uvp[1]};
            return uvp;
        }

        /**
         * 將code間隔的均勻色差對應的色度座標
         * @param code1xyY CIExyY
         * @param startcodexyY CIExyY
         * @param regress boolean 是否以回歸重新產生其值
         * @return CIExyY[]
         */
        protected CIExyY[] getPredictedxyYCurve(CIExyY code1xyY,
                                                CIExyY startcodexyY,
                                                boolean regress) {
            int size = p.startCode;
            //暗部code的大小
            int dimsize = size - 2;
            double[] codeArray = DoubleArray.buildX(2, size - 1, dimsize);
            double partdab = this.dab / (p.startCode - 1.);
            double[][] output = new double[2][dimsize];
            CIExyY[] predictxyYCurve = new CIExyY[dimsize];

            //========================================================================
            // 計算2~ startCode-1 的xyY
            //========================================================================

            for (int x = size - 1; x >= 2; x--) {
                int index = size - x;
                double dab = partdab * index;
                dab = this.accDeltaLut.correctValueInRange(dab);
                double pseudocode = this.accDeltaLut.getKey(dab);
                CIExyY xyY = this.getRegressionPredictxyY(pseudocode);
                xyY.Y = getY(x);
                predictxyYCurve[x - 2] = xyY;
                double[] uvp = xyY.getuvPrimeValues();
                output[0][x - 2] = uvp[0];
                output[1][x - 2] = uvp[1];
            }
            //========================================================================

            if (regress) {
                CIExyY[] regressxyYCurve = getRegressionxyYCurve(p.startCode,
                        code1xyY, startcodexyY, codeArray, output[0], output[1],
                        Polynomial.COEF_1.BY_4C);
                return regressxyYCurve;
            } else {
                return predictxyYCurve;
            }
        }

        /**
         * 計算出累積的色差變化
         * @param basecode double
         * @param baseXYZ CIEXYZ
         * @param code double
         * @return double[]
         */
        private double[] getAccumulateDeltaab(double basecode, CIEXYZ baseXYZ,
                                              double code) {
            int size = (int) ((basecode - step - code) / step) + 1;
            double[] dabArray = new double[size];
            double[] codeArray = new double[size];
            int index = 0;

            CIEXYZ preXYZ = baseXYZ;
            for (double c = basecode - step; c >= code; c -= step) {
                CIEXYZ XYZ = getRegressionPredictxyY(c).toXYZ();
                DeltaE dE = new DeltaE(XYZ, preXYZ, white);
                dabArray[index] = dE.getCIE2000Deltaab();
                codeArray[index] = c;
                index++;
                preXYZ = XYZ;
            }
            dabArray = DoubleArray.accumulate(dabArray);
            accDeltaLut = new Interpolation1DLUT(codeArray, dabArray);
            return dabArray;
        }

        /**
         *
         * @param startcode double 轉折點
         * @param startXYZ CIEXYZ 轉折點三刺激值
         * @param endcode double 最後一點, 通常是1
         * @return double[]
         */
        private double[] getAccumulateDeltapt(double startcode, CIEXYZ startXYZ,
                                              double endcode) {
            int size = (int) ((startcode - step - endcode) / step) + 1;
            double[] dptArray = new double[size];
            double[] codeArray = new double[size];
            int index = 0;

            IPT preIPT = IPT.fromXYZ(startXYZ, white);
            for (double c = startcode - step; c >= endcode; c -= step) {
                CIEXYZ XYZ = getRegressionPredictxyY(c).toXYZ();
                IPT ipt = IPT.fromXYZ(XYZ, white);
                double delta = Maths.delta(ipt.getValues(), preIPT.getValues());
                dptArray[index] = delta;
                codeArray[index] = c;
                index++;
                preIPT = ipt;
            }
            dptArray = DoubleArray.accumulate(dptArray);
            accDeltaLut = new Interpolation1DLUT(codeArray, dptArray);
            return dptArray;
        }

        protected AdaptivexyYCurve(int adaptiveStart, Method method
                ) {
            this.start = adaptiveStart;
//      this.end = adaptiveEnd;
            this.method = method;
            init();
        }

        protected final static double step = 0.015625;
        protected CIEXYZ startCodeXYZ;

        /**
         * 以code找到regress對應的uv', 再轉回XYZ
         * @param code double
         * @return CIEXYZ
         */
        protected CIExyY getRegressionPredictxyY(double code) {
            //以pattern預測的uv'
            double[] puvp = getRegressPredict(code);
            CIExyY xyY = new CIExyY();
            xyY.setuvPrimeValues(puvp);
            xyY.Y = getY(code);
            return xyY;
        }

        protected void init() {
            //以適應code區段以及u'和v'作回歸
            this.initAdaptiveRegress();
            //取得轉折點的XYZ, 其色度座標其實與白點相同, 也可不經回歸運算而得.
            startCodeXYZ = getRegressionPredictxyY(p.startCode).toXYZ();

            switch (method) {
            case ByIPT: {
                double[] dabArray = this.getAccumulateDeltapt(p.startCode,
                        startCodeXYZ, 1);
                this.dab = dabArray[dabArray.length - 1];
            }
            break;
            case ByDeltaE: {
                double[] dabArray = this.getAccumulateDeltaab(p.startCode,
                        startCodeXYZ, 1);
                this.dab = dabArray[dabArray.length - 1];
            }
            break;
            }
        }

    }


    /**
     * 抓取白點與螢幕的adaptiveStart~adaptiveEnd的色度座標, 作回歸所得的目標xy曲線
     * @param adaptiveStart int 適應起點
     * @param method Method 產生的方法
     * @return CIExyY[]
     */
    protected final CIExyY[] getAdaptivexyYCurve(int adaptiveStart,
                                                 Method method
            ) {
        adaptive = new AdaptivexyYCurve(adaptiveStart,
                                        method);
        return adaptive.getAdaptivexyYCurve();
    }

    /**
     * 抓取白點與螢幕的adaptiveStart~adaptiveEnd的色度座標, 作回歸所得的目標xy曲線
     * @param adaptiveStart int
     * @param colorSpace ColorSpace
     * @return CIExyY[]
     */
    protected final CIExyY[] getAdaptivexyYCurve(int adaptiveStart,
                                                 ColorSpace colorSpace
            ) {
        uniformAdaptive = new UniformAdaptivexyYCurve(adaptiveStart, colorSpace);
        return uniformAdaptive.getAdaptivexyYCurve();
    }

    private AdaptivexyYCurve adaptive;
    private UniformAdaptivexyYCurve uniformAdaptive;

    /**
     * 修正漏光的色度座標點所產生的新CCT曲線
     * 產生流程:
     * simulate => simulateRegression => getRegressionCCTCurve
     *  => getCorrectedxyYCurve
     * @return CIExyY[]
     */
    public final CIExyY[] getCorrectedxyYCurve() {
        if (correctedxyYCurve == null) {
            correctedxyYCurve = new CorrectedxyYCurve();
        }
        return correctedxyYCurve.getCorrectedxyYCurve();
    }

    private CorrectedxyYCurve correctedxyYCurve = null;

    protected final double getY(double whiteCode) {
        return getYArray(new double[] {whiteCode})[0];
    }

    /**
     * 依照whiteCodeArray的數值, 計算出相對應的Y(亮度).
     * 但在僅有white的狀況下, 只能產生理想的對應亮度, 未必能完全對應實際狀況.
     * @param whiteCodeArray int[]
     * @return double[]
     */
    protected final double[] getYArray(double[] whiteCodeArray) {
        int size = whiteCodeArray.length;
        double[] YArray = new double[size];
        model.changeMaxValue(rgb);

        //==========================================================================
        // 對映亮度的計算, 目前以RGB產生的亮度為基準
        //==========================================================================
        for (int x = 0; x < size; x++) {
            if (whiteYLut != null) {
                //如果有whiteYLut, 則是回傳理想的白的亮度
                YArray[x] = whiteYLut.getValue(whiteCodeArray[x]);
            } else {
                //沒有whiteLut, 回傳的是面板原始在該code下的亮度
                rgb.setValue(RGBBase.Channel.W, whiteCodeArray[x],
                             RGB.MaxValue.Double255);
                YArray[x] = model.getXYZ(rgb, false).Y;
            }
        }
        //==========================================================================
        return YArray;
    }

    /**
     * 檢查pattern是否通過
     * @param CCTCurve double[]
     * @param pattern int[]
     * @return boolean
     */
    protected final boolean checkPattern(double[] CCTCurve, int[] pattern) {
        return checkPattern(toCIExyYArray(CCTCurve), pattern);
    }

    /**
     * 檢查所有的pattern是否通過
     * @param xyYArray CIExyY[]
     * @return boolean[]
     */
    protected final boolean[] checkPattern(CIExyY[] xyYArray) {
        boolean[] result = new boolean[4];
        result[0] = checkPattern(xyYArray, this.PATTERN_1);
        result[1] = checkPattern(xyYArray, this.PATTERN_2);
        result[2] = checkPattern(xyYArray, this.PATTERN_2_EVOLVE);
        result[3] = checkPattern(xyYArray, this.PATTERN_3);
        return result;
    }

    /**
     * 以pattern檢查是否通過測試
     * @param xyYArray CIExyY[]
     * @param pattern int[]
     * @return boolean
     */
    protected final boolean checkPattern(CIExyY[] xyYArray, int[] pattern) {
        int size = pattern.length;
        double[] YArray = getYArray(IntArray.toDoubleArray(pattern));

        for (int x = 1; x < size; x++) {
            int precode = pattern[x - 1];
            int code = pattern[x];

            CIExyY prexyY = xyYArray[precode];
            prexyY.Y = YArray[x - 1];
            CIEXYZ preXYZ = prexyY.toXYZ();
            CIExyY xyY = xyYArray[code];
            xyY.Y = YArray[x];
            CIEXYZ XYZ = xyY.toXYZ();
            DeltaE de = new DeltaE(XYZ, preXYZ, white);
            double dab = de.getCIE2000Deltaab();
            if (dab > p.dabThreshold) {
                return false;
            }
        }

        return true;
    }

    /**
     * CCT轉色度座標
     * @param CCTArray double[]
     * @return CIExyY[]
     */
    protected final CIExyY[] toCIExyYArray(double[] CCTArray) {
        int size = CCTArray.length;
        CIExyY[] xyYArray = new CIExyY[size];
        for (int x = 0; x < size; x++) {
            xyYArray[x] = CCT2xyY((int) CCTArray[x]); ;
        }
        return xyYArray;
    }

    /**
     * 參數
     */
    private CCTParameter p;

    /**
     * 設定參數
     * @param parameter Parameter
     */
    public void setParameter(CCTParameter parameter) {
        this.p = parameter;
        double Y = getY(255);
        this.white = (CIEXYZ) p.getWhiteXYZ().clone();
        this.white.scaleY(Y);
    }

    protected static enum WhiteStyle {
        CCT, XYZ
    }


    protected final CIExyY CCT2xyY(int tempK) {
        return CCT2xyY(tempK, p.cctStyle);
    }

    /**
     * 依照cctStyle指定的方式, 把tempK轉到xyY
     * @param tempK int
     * @param cctStyle CCTStyle
     * @return CIExyY
     */
    public final static CIExyY CCT2xyY(int tempK, CCTStyle cctStyle) {
        switch (cctStyle) {
        case DIlluminant:
            try {
                return CIExyY.fromCCT2DIlluminant(tempK);
            } catch (IllegalArgumentException ex) {
                Logger.log.error("", ex);
                return null;
            }
            case Blackbody:
                CIEXYZ XYZ = CorrelatedColorTemperature.
                             getSpectraOfBlackbodyRadiator(
                                     tempK, 400, 700, 10).getXYZ();
                return new CIExyY(XYZ);
        default:
            return null;
        }
    }

    public final static CCTParameter getCCTParameter(CIEXYZ blackXYZ,
            CIEXYZ whiteXYZ, ColorProofParameter p, WhiteParameter wp) {
        CCTParameter pa = new CCTParameter(p.turnCode,
                                           p.grayInterval, whiteXYZ, blackXYZ,
                                           wp.cctStyle, p);
        return pa;
    }

    public final static class CCTParameter {

        /**
         * 用確切的三刺激值作為白點
         * @param startCode int
         * @param codeInterval int
         * @param white CIEXYZ
         * @param black CIEXYZ
         * @param cctStyle CCTStyle
         * @param p Parameter
         */
        public CCTParameter(int startCode, int codeInterval,
                            CIEXYZ white, CIEXYZ black, CCTStyle cctStyle,
                            ColorProofParameter p) {
            this.startCode = startCode;
            this.codeInterval = codeInterval;
            this.white = white;
            this.black = black;
            this.codeArray = getCodeArray(this.startCode, this.codeInterval);
            whiteStyle = WhiteStyle.XYZ;
            this.cctStyle = cctStyle;
            this.p = p;
        }

        /**
         * 以色溫作為白點
         * @param startCode int
         * @param codeInterval int
         * @param whiteCCT int
         * @param black CIEXYZ
         * @param cctStyle CCTStyle
         * @param p Parameter
         */
        public CCTParameter(int startCode, int codeInterval, int whiteCCT,
                            CIEXYZ black, CCTStyle cctStyle,
                            ColorProofParameter p) {
            this.startCode = startCode;
            this.codeInterval = codeInterval;
            this.whiteCCT = whiteCCT;
            this.black = black;
            this.codeArray = getCodeArray(this.startCode, this.codeInterval);
            whiteStyle = WhiteStyle.CCT;
            this.cctStyle = cctStyle;
            this.p = p;
        }

        /**
         * 從start和interval產生出code array
         *
         * @param start int
         * @param interval int
         * @return int[]
         * @todo H acp 產生的code array最後一碼要多少, 可能會影響最後產生的色溫
         */
        protected final static int[] getCodeArray(int start, int interval) {
            int size = (start / interval) + 1;
            int[] codeArray = new int[size];
            for (int x = 0; x < size; x++) {
                codeArray[x] = start - x * interval;
            }
            return codeArray;
        }

        protected double getCCT() {
            if (whiteStyle == WhiteStyle.CCT) {
                return whiteCCT;
            } else if (whiteStyle == WhiteStyle.XYZ) {
                return white.getCCT();
            } else {
                return -1;
            }
        }

        public CIEXYZ getWhiteXYZ() {
            if (whiteStyle == WhiteStyle.CCT) {
                return CCT2xyY(whiteCCT, this.cctStyle).toXYZ();
            } else if (whiteStyle == WhiteStyle.XYZ) {
                return white;
            } else {
                return null;
            }
        }

        private CCTStyle cctStyle;
        private WhiteStyle whiteStyle;
        /**
         * 轉折點及code的間隔
         */
        private int startCode = -1, codeInterval = -1;
        private int whiteCCT;
        private CIEXYZ black;
        private int[] codeArray = null;
        private CIEXYZ white;
        private ColorProofParameter p;
        /**
         * 灰階色塊之間的允許ab色差
         */
        protected double dabThreshold = -1;
    }


    public static void main(String[] args) throws Exception {
        //==========================================================================
        // xtalk LCD model
        //==========================================================================
        LCDModel model2 = Material.getLCDModel();
        LCDTarget lcdTarget = model2.getLCDTarget();
        //==========================================================================

        //==========================================================================
        // CCT model
        //==========================================================================
        CIEXYZ black = lcdTarget.getBlackPatch().getXYZ();
        CIEXYZ targetWhite = Illuminant.D93WhitePoint;
        CCTParameter p = new CCTParameter(50, 8, targetWhite, black,
                                          CCTStyle.DIlluminant, null);
        CCTCurveProducer producer = new CCTCurveProducer(model2, p);

        producer.setParameter(p);
        CIExyY[] curve2 = producer.getAdaptivexyYCurve(1, Method.ByIPT);

        CIEXYZ code1XYZ = producer.adaptive.getRegressionPredictxyY(1).toXYZ();
        CIEXYZ code50XYZ = producer.adaptive.getRegressionPredictxyY(50).toXYZ();
        CIEXYZ white = lcdTarget.getLuminance();

        double[] uvp1 = code1XYZ.getuvPrimeValues();
        double[] uvp50 = code50XYZ.getuvPrimeValues();
        IPT code1ipt = IPT.fromXYZ(code1XYZ, white);
        IPT code50ipt = IPT.fromXYZ(code50XYZ, white);

        ViewingConditions vc = new ViewingConditions(Illuminant.
                getD65WhitePoint(),
                0.1, 20,
                Surround.Dark,
                "Dark");
        code1XYZ.normalize(white);
        code50XYZ.normalize(white);
        CIECAM02 cam = new CIECAM02(vc);
        CIECAM02Color jab1 = cam.forward(code1XYZ);
        CIECAM02Color jab50 = cam.forward(code50XYZ);

        Plot2D pt = Plot2D.getInstance();
        pt.setVisible();
        pt.addLegend();
        pt.setAxeLabel(0, "u'");
        pt.setAxeLabel(1, "v'");

        Plot2D pt2 = Plot2D.getInstance();
        pt2.setVisible();
        pt2.addLegend();

        for (int x = 1; x <= 50; x++) {
            CIEXYZ XYZ = producer.adaptive.getRegressionPredictxyY(x).toXYZ();

            //========================================================================
            // uv'
            //========================================================================
            double up = Interpolation.linear(1, 50, uvp1[0], uvp50[0], x);
            double vp = Interpolation.linear(1, 50, uvp1[1], uvp50[1], x);
            pt.addCacheScatterLinePlot("uvp ", Color.red, up, vp);
            //========================================================================

            //========================================================================
            // ipt
            //========================================================================
            double iptp = Interpolation.linear(1, 50, code1ipt.P, code50ipt.P,
                                               x);
            double iptt = Interpolation.linear(1, 50, code1ipt.T, code50ipt.T,
                                               x);
            IPT ipt = IPT.fromXYZ(XYZ, white);
            ipt.P = iptp;
            ipt.T = iptt;
            CIEXYZ iptXYZ = ipt.toXYZ();
            CIEXYZ orgXYZ = iptXYZ.getXYZAdaptedFromD65();
            double[] iptuvp = orgXYZ.getuvPrimeValues();
            pt.addCacheScatterLinePlot("ipt", Color.green, iptuvp[0], iptuvp[1]);
            System.out.print(Math.abs(iptuvp[0] - up) + " " +
                             Math.abs(iptuvp[1] - vp));

            //========================================================================

            //========================================================================
            // cam
            //========================================================================
            XYZ.normalize(white);
            CIECAM02Color jacbc = cam.forward(XYZ);
            double jabac = Interpolation.linear(1, 50, jab1.ac, jab50.ac, x);
            double jabbc = Interpolation.linear(1, 50, jab1.bc, jab50.bc, x);
            jacbc = new CIECAM02Color(jacbc.J, jabac, jabbc);
            CIEXYZ camXYZ = cam.inverse(jacbc);
            double[] camuvp = camXYZ.getuvPrimeValues();
            pt.addCacheScatterLinePlot("cam", Color.blue, camuvp[0], camuvp[1]);
            System.out.println("/" + Math.abs(camuvp[0] - up) + " " +
                               Math.abs(camuvp[1] - vp) + " " + camXYZ.getCCT());
            pt2.addCacheScatterLinePlot("CCT", Color.black, x, camXYZ.getCCT());
            //========================================================================

        }
        pt.drawCachePlot();
        pt2.drawCachePlot();
    }

    /**
     *
     * <p>Title: Colour Management System</p>
     *
     * <p>Description: a Colour Management System by Java</p>
     * 基於均勻色彩空間下, 以適應法產生的色度曲線
     *
     * <p>Copyright: Copyright (c) 2008</p>
     *
     * <p>Company: skygroup</p>
     *
     * @author skyforce
     * @version 1.0
     */
    protected final class UniformAdaptivexyYCurve extends AdaptivexyYCurveBase {
        //code與均勻色彩空間中明度的關係
        private Interpolation1DLUT lightnessLut;
        private ColorSpace colorSpace;
        private CIECAM02 cam;
        private CIEXYZ normalWhite;

        protected CIExyY[] getPredictedxyYCurve(CIExyY code1xyY,
                                                CIExyY startcodexyY,
                                                boolean regress) {
            int size = p.startCode;
            //暗部code的大小
            int dimsize = size - 2;
            double[] codeArray = DoubleArray.buildX(2, size - 1, dimsize);
            double[][] output = new double[2][dimsize];
            CIExyY[] predictxyYCurve = new CIExyY[dimsize];

            //========================================================================
            // 計算2~ startCode-1 的xyY
            //========================================================================
            for (int x = size - 1; x >= 2; x--) {
                CIExyY xyY = this.getRegressionPredictxyY(x);
                xyY.Y = getY(x);
                predictxyYCurve[x - 2] = xyY;

                double[] uvp = xyY.getuvPrimeValues();
                output[0][x - 2] = uvp[0];
                output[1][x - 2] = uvp[1];
            }
            //========================================================================

            if (regress) {
                CIExyY[] regressxyYCurve = getRegressionxyYCurve(p.startCode,
                        code1xyY, startcodexyY, codeArray, output[0], output[1],
                        Polynomial.COEF_1.BY_4C);
                return regressxyYCurve;
            } else {
                return predictxyYCurve;
            }
        }

        protected final double[] getColorSpaceValues(final CIEXYZ XYZ) {
            switch (colorSpace) {
            case IPT: {
                IPT ipt = IPT.fromXYZ(XYZ, white);
                return ipt.getValues();
            }
            case CIECAM02:
                CIEXYZ clone = (CIEXYZ) XYZ.clone();
                clone.normalize(white);
                CIECAM02Color jab = cam.forward(clone);
                return jab.getValues();
            default:
                return null;
            }
        }

        protected UniformAdaptivexyYCurve(int adaptiveStart,
                                          ColorSpace colorSpace) {
            this.start = adaptiveStart;
            this.colorSpace = colorSpace;
            init();
        }

        /**
         * 以code找到regress對應的uv', 再轉回XYZ
         * @param code double
         * @return CIEXYZ
         */
        protected CIExyY getRegressionPredictxyY(double code) {
            double[] predictab = getRegressPredict(code);
            double lightness = this.lightnessLut.getValue(code);

            switch (colorSpace) {
            case IPT: {
                IPT ipt = new IPT(lightness, predictab[0], predictab[1]);
                CIEXYZ iptXYZ = ipt.toXYZ();
                CIEXYZ D65XYZ = iptXYZ.getXYZAdaptedFromD65(white);
                return new CIExyY(D65XYZ);
            }
            case CIECAM02: {
                CIECAM02Color jab = new CIECAM02Color(lightness, predictab[0],
                        predictab[1]);
                jab = new CIECAM02Color(jab.J, predictab[0], predictab[1]);
                CIEXYZ camXYZ = cam.inverse(jab);
                return new CIExyY(camXYZ);
            }
            default:
                return null;
            }
        }

        protected void init() {
            if (colorSpace == ColorSpace.CIECAM02) {
                //ciecam02必要的變數初始化
                normalWhite = (CIEXYZ) white.clone();
                normalWhite.normalizeY100();
                ViewingConditions vc = null; //ViewingConditions.getDarkViewingConditions(
                //normalWhite);
                cam = new CIECAM02(vc);
            }
            //以適應code區段以及u'和v'作回歸
            this.initAdaptiveRegress();
            //建立code與明度的關係
            initLightnessLut();
        }

        /**
         * 建立code與均勻色彩空間中的明度之間的關係
         */
        protected void initLightnessLut() {
            if (lightnessLut == null) {
                final int size = 256;
                double[] LArray = new double[size];
                double[] codeArray = DoubleArray.buildX(0, size - 1, size);
                //code對應的亮度
                double[] YArray = getYArray(codeArray);
                double[] wxyValues = white.getxyValues();
                CIExyY xyY = new CIExyY(wxyValues[0], wxyValues[1], 1);

                for (int x = 0; x < size; x++) {
                    xyY.Y = YArray[x];
                    CIEXYZ XYZ = xyY.toXYZ();
                    double[] values = this.getColorSpaceValues(XYZ);
                    LArray[x] = values[0];
                }
                lightnessLut = new Interpolation1DLUT(codeArray, LArray);
            }
        }

    }


    /**
     * 將coord陣列以回歸逼近後，重新產生其xyY值
     *
     * @param startcode int
     * @param code1xyY CIExyY
     * @param startcodexyY CIExyY
     * @param codeArray double[]
     * @param coordAArray double[]
     * @param coordBArray double[]
     * @param coef COEF_1
     * @return CIExyY[]
     */
    protected final CIExyY[] getRegressionxyYCurve(int startcode,
            CIExyY code1xyY,
            CIExyY startcodexyY,
            double[] codeArray,
            double[] coordAArray,
            double[] coordBArray,
            Polynomial.COEF_1 coef) {
        int dimsize = codeArray.length;
        int size = dimsize + 2;

        //========================================================================
        // 回歸
        //========================================================================
        Polynomial.COEF_1[] coefs = CompositePolynomialRegression.
                                    findBestPolynomialCoefficient1(codeArray,
                coordAArray, coordBArray,
                CompositePolynomialRegression.Type.
                Series);

        CompositePolynomialRegression regress = new
                                                CompositePolynomialRegression(
                codeArray, coordAArray, coordBArray, coef, coefs[1],
                CompositePolynomialRegression.Type.Series);
        regress.regress();
        //========================================================================

        //========================================================================
        // 計算operator
        //========================================================================
        double[][] actual = new double[][] {
                            code1xyY.getuvPrimeValues(),
                            startcodexyY.getuvPrimeValues()};

        Operator operator = getOperator(regress, 1, startcode, actual);
        regress.setOperator(operator);
        //========================================================================

        //========================================================================
        // 利用回歸預測結果
        //========================================================================
        CIExyY[] regressxyYCurve = new CIExyY[dimsize];
        double[][] predict = regress.getMultiPredict(codeArray);

        for (int x = size - 1; x >= 2; x--) {
            CIExyY xyY = new CIExyY();
            double[] uvp = new double[] {
                           predict[0][x - 2], predict[1][x - 2]};
            xyY.setuvPrimeValues(uvp);
            xyY.Y = getY(x);
            regressxyYCurve[x - 2] = xyY;
        }
        //========================================================================

        return regressxyYCurve;
    }

    /**
     * 計算出色溫的xyY曲線
     * @param model LCDModel
     * @param cctp CCTParameter
     * @param idealWhiteYArray double[]
     * @return CIExyY[]
     */
    public final static CIExyY[] getxyYCurve(LCDModel model,
                                             CCTCurveProducer.CCTParameter cctp,
                                             double[] idealWhiteYArray) {
        ColorProofParameter p = cctp.p;
        //==========================================================================
        // 4.以G code以及轉折點, 產生轉折點(包括轉折點)以前的與白點等色溫的區段
        // 以及轉折點以後到黑點的變化色溫區段
        //==========================================================================
        CCTCurveProducer producer = new CCTCurveProducer(model,
                idealWhiteYArray, cctp);
        CIExyY[] xyYCurve = null;
        switch (p.cctCalibrate) {
        case Corrected:
            xyYCurve = producer.getCorrectedxyYCurve();
            break;
        case uvpByDE00:
            xyYCurve = producer.getAdaptivexyYCurve(p.cctAdaptiveStart,
                    CCTCurveProducer.Method.
                    ByDeltaE);
            break;
        case uvpByIPT:
            xyYCurve = producer.getAdaptivexyYCurve(p.cctAdaptiveStart,
                    CCTCurveProducer.Method.ByIPT);
            break;
        case IPT:
            xyYCurve = producer.getAdaptivexyYCurve(p.cctAdaptiveStart,
                    CCTCurveProducer.ColorSpace.
                    IPT);
            break;
        case CIECAM02:
            xyYCurve = producer.getAdaptivexyYCurve(p.cctAdaptiveStart,
                    CCTCurveProducer.ColorSpace.
                    CIECAM02);
            break;
        default:
            throw new IllegalArgumentException(p.cctCalibrate +
                                               " is not supported.");
        }
        //==========================================================================

//    for (int x = 0; x < xyYCurve.length; x++) {
//      System.out.println(xyYCurve[x]);
//    }

        return xyYCurve;
    }

    /**
     * 產生 將startIndex和endIndex對齊到actualOutput 的Operator
     * @param regress CompositePolynomialRegression
     * @param startIndex int 要對齊的起點index
     * @param endIndex int 要對齊的終點index
     * @param actualOutput double[][] 要對齊的資料
     * @return Operator
     */
    protected final static Operator getOperator(CompositePolynomialRegression
                                                regress, int startIndex,
                                                int endIndex,
                                                double[][] actualOutput) {
        //========================================================================
        // 將回歸的起點及終點調整回原始值
        //========================================================================
        double[][] predict = DoubleArray.transpose(
                regress.getMultiPredict(new double[] {startIndex, endIndex}));

        Operator op = Operator.getAdjustOperator(actualOutput[0],
                                                 actualOutput[1],
                                                 predict[0], predict[1]);
        return op;
    }
}
