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
 * �ŦXLCD�S�ʪ�CCT���u���;�
 * �D�n���ѤT��CCTCurve�����ͤ�k
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
    //pattern1 ���j17 (��) �S�q�L�S���Y, �]��pat-gen�S��pattern.
    public final static int[] PATTERN_1 = new int[] {
                                          51, 34, 17};
    //pattern2 ���j8 (�K)
    public final static int[] PATTERN_2 = new int[] {
                                          56, 48, 40, 32, 24, 16, 8};
    //pattern2 �ܧ�, ���j8
    public final static int[] PATTERN_2_EVOLVE = new int[] {
                                                 50, 42, 34, 26, 18, 10};
    //pattern ���j4 (��K)
    public final static int[] PATTERN_3 = new int[] {
                                          56, 52, 48, 44, 40, 36, 32, 28, 24,
                                          20, 16, 12, 8, 4};

    private CIEXYZ white;

    /**
     * �̷� model �S�ʲ���CCT Curve
     * @param model LCDModel
     * @param parameter Parameter
     */
    protected CCTCurveProducer(LCDModel model, CCTParameter parameter) {
        this(model, null, parameter);
    }

    /**
     * �̷� model �S�ʲ���CCT Curve
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
            //���ͥժ���Ӫ�
            whiteYLut = new Interpolation1DLUT(codeArray,
                                               idealWhiteYArray);
        }
        setParameter(parameter);
    }

    private RGB rgb = new RGB(RGB.ColorSpace.unknowRGB,
                              RGB.MaxValue.Double1);

    /**
     * code��G�ת���Ӫ�
     */
    private Interpolation1DLUT whiteYLut = null;

    /**
     *
     * <p>Title: Colour Management System</p>
     *
     * <p>Description: a Colour Management System by Java</p>
     * �H����ܤƬ���¦, ���H���i����������ܤƦ��u��, �A����׮y��, �o��CIExy���u
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
         * �̷�dabThreshold�i�e�\��t�����öi��^�k
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
         * �C�@��CCT�վ�ұĥΪ�step(step�L�p���Ӯɶ�, step�L�j�|����T)
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
                //�S���]�w��t�H�Ȫ����p�U
                double finalCCT = p.black.getCCT();
                dabThreshold = 0.0;
                PolynomialRegression regress = null;
                double CCT = 0;

                //�v���W�[��t�H��, ���̲צ�ťi�H�W�X�ù����|�����
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
                            //���t��n�j�L�H��, �N���X
                            CCTArray[x] = CCT - CCTStep;
                            break;
                        }
                    }
                }
            }

            return CCTArray;
        }

        /**
         * �Hregression fit��CCT Curve, �p��X�Ҧ�code(0~255)������CCT
         * @return double[]
         */
        protected final double[] getRegressionCCTCurve() {
            PolynomialRegression regress = simulateRegression(p.dabThreshold);
            int size = p.startCode + 1;
            double[] CCTCurve = new double[256];

            //����I�H�e, �z�L�^�k���G�ӹw���A��CCT
            for (int x = 0; x < size; x++) {
                CCTCurve[x] = regress.getPredict(new double[] {x})[0];
            }
            //����I�H��, CCT�P���I�P
            for (int x = size; x < 256; x++) {
                CCTCurve[x] = p.getCCT();
            }

            return CCTCurve;
        }

        /**
         * �ץ��|������׮y���I�Ҳ��ͪ��sCCT���u
         * ���ͬy�{:
         * simulate => simulateRegression => getRegressionCCTCurve
         *  => getCorrectedxyYCurve
         * @return CIExyY[]
         */
        public final CIExyY[] getCorrectedxyYCurve() {
            //�Hregression����fit��CCT���u
            double[] CCTCurve = getRegressionCCTCurve();
            int size = CCTCurve.length;

            //==========================================================================
            // �p����I�վ㪺�Ѽ�
            // �q��Ůy�����p�����I��ק��ФW
            //==========================================================================
            //�^�k�X�Ӫ����Iu'v'
            double[] CCTWhiteuvp = CCT2xyY((int) CCTCurve[size - 1]).
                                   getuvPrimeValues();
            //�ؼЪ����Iu'v'
            double[] whiteuvp = p.getWhiteXYZ().getuvPrimeValues();
            //�p���ӥ��I������
            double[] whiteoffset = DoubleArray.minus(CCTWhiteuvp, whiteuvp);
            //���ͭץ��i����������
            OffsetOperator whiteoffsetOP = new OffsetOperator(whiteoffset);
            //==========================================================================

            //==========================================================================
            // �p��CIExyY
            //==========================================================================
            double[][] uvpCurve = new double[size][];
            for (int x = 0; x < size; x++) {
                //���i��䤣�������xy(D���������p)
                CIExyY xyY = CCT2xyY((int) CCTCurve[x]);
                double[] uvp = xyY.getuvPrimeValues();
                //���I���ץ�
                uvpCurve[x] = whiteoffsetOP.getXY(uvp);
            }
            //==========================================================================

            double[] panelblackuvp = p.black.getuvPrimeValues();
            double[] blackuvp = uvpCurve[0];

            //==========================================================================
            // ���ͽվ��OP
            //==========================================================================
            Operator offsetOP = Operator.getAdjustOperator(whiteuvp, blackuvp,
                    panelblackuvp);
            //==========================================================================

            //==========================================================================
            // �i��վ�
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
         * �bIPT�Ŷ��W������ı�t��
         */
        IPT,
        /**
         * �bCIECAM02�Ŷ��W������ı�t��
         */
        CIECAM02
    }


    public static enum Method {
        /**
         * �HIPT�������Z��ı�t��
         */
        ByIPT,
        /**
         * DeltaE�������Z��ı�t��,�b���O�ĥ�CIEDE2000
         */
        ByDeltaE
    }


    protected abstract class AdaptivexyYCurveBase {
        private CompositePolynomialRegression adaptiveRegress;
        protected int start;
        /**
         * ���ͤ@�Ӧbstart��end������code, delta ab���ê�CIExyY�ܤƦ��u.
         * ���沣��0~255���Ҧ�CIExyY
         *
         * �]���w��Y���dab�v�T���j, �ҥH��panel��ͪ�Y�Ӵ��N(��M�o�Ӱ��]�|���]�����Q����)
         * ���p�G���ؼЪ��G�צ��u�A�h�H�ؼЫG�ת�Y�Ӵ��N�A���Ӹ����ǽT�C
         *
         * @return CIExyY[]
         */
        protected final CIExyY[] getAdaptivexyYCurve() {
            //0~startCode
            int size = p.startCode;
            CIExyY[] xyYCurve = new CIExyY[256];

            //========================================================================
            // �p��0�M1���code��xyY; �H�^�k�w��.
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
            // �p��startCode~255��xyY; ����I�H��ȧ��ܫG��.
            //========================================================================
            CIExyY whitexyY = new CIExyY(p.getWhiteXYZ());
            for (int x = size; x < 256; x++) {
                CIExyY xyY = (CIExyY) whitexyY.clone();
                xyY.Y = getY(x);
                xyYCurve[x] = xyY;
            }
            //========================================================================

            //========================================================================
            // �p��2~ startCode-1 ��xyY; ����ı���ò���
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
            //�H�A��code�Ϭq�H��u'�Mv'�@�^�k
//      adaptiveRegress = getAdaptiveRegression(start, end);
            adaptiveRegress = getAdaptiveRegression(start);
        }

        /**
         * �NCIEXYZ�ഫ��۹�������m�y��
         * @param XYZ CIEXYZ
         * @return double[]
         */
        protected abstract double[] getColorSpaceValues(final CIEXYZ XYZ);

        /**
         * ������I�P�ù���adaptiveStart����׮y��, �ұo�쪺�@���^�k��{��.
         * �إ�code�P��w��m�y�Ъ�ab�y��, ���������Y.
         *
         * @param adaptiveCode int �A���_�I
         * @return CompositePolynomialRegression
         */
        protected final CompositePolynomialRegression getAdaptiveRegression(
                int adaptiveCode) {
//      int size = 2;
            double[][] abdata = new double[2][];

            //�񺡾A���ұĥΪ�ab
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
         * �H�^�k���覡�w��code�ҹ����쪺XYZ
         * @param code double
         * @return CIEXYZ
         */
        protected abstract CIExyY getRegressionPredictxyY(double code);

        /**
         * �w���Xcode~������I��xyY���u
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
     * �]�w�n�A��code���϶�, �H�h�����إ߾A���϶��H�Υ��I����CIExyY���u.
     * �A��󦹪��u, ���͵�ı�i������CIExy���u.
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
         * �Ncode���j�����æ�t��������׮y��
         * @param code1xyY CIExyY
         * @param startcodexyY CIExyY
         * @param regress boolean �O�_�H�^�k���s���ͨ��
         * @return CIExyY[]
         */
        protected CIExyY[] getPredictedxyYCurve(CIExyY code1xyY,
                                                CIExyY startcodexyY,
                                                boolean regress) {
            int size = p.startCode;
            //�t��code���j�p
            int dimsize = size - 2;
            double[] codeArray = DoubleArray.buildX(2, size - 1, dimsize);
            double partdab = this.dab / (p.startCode - 1.);
            double[][] output = new double[2][dimsize];
            CIExyY[] predictxyYCurve = new CIExyY[dimsize];

            //========================================================================
            // �p��2~ startCode-1 ��xyY
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
         * �p��X�ֿn����t�ܤ�
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
         * @param startcode double ����I
         * @param startXYZ CIEXYZ ����I�T��E��
         * @param endcode double �̫�@�I, �q�`�O1
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
         * �Hcode���regress������uv', �A��^XYZ
         * @param code double
         * @return CIEXYZ
         */
        protected CIExyY getRegressionPredictxyY(double code) {
            //�Hpattern�w����uv'
            double[] puvp = getRegressPredict(code);
            CIExyY xyY = new CIExyY();
            xyY.setuvPrimeValues(puvp);
            xyY.Y = getY(code);
            return xyY;
        }

        protected void init() {
            //�H�A��code�Ϭq�H��u'�Mv'�@�^�k
            this.initAdaptiveRegress();
            //���o����I��XYZ, ���׮y�Ш��P���I�ۦP, �]�i���g�^�k�B��ӱo.
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
     * ������I�P�ù���adaptiveStart~adaptiveEnd����׮y��, �@�^�k�ұo���ؼ�xy���u
     * @param adaptiveStart int �A���_�I
     * @param method Method ���ͪ���k
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
     * ������I�P�ù���adaptiveStart~adaptiveEnd����׮y��, �@�^�k�ұo���ؼ�xy���u
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
     * �ץ��|������׮y���I�Ҳ��ͪ��sCCT���u
     * ���ͬy�{:
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
     * �̷�whiteCodeArray���ƭ�, �p��X�۹�����Y(�G��).
     * ���b�Ȧ�white�����p�U, �u�ಣ�Ͳz�Q�������G��, �����৹��������ڪ��p.
     * @param whiteCodeArray int[]
     * @return double[]
     */
    protected final double[] getYArray(double[] whiteCodeArray) {
        int size = whiteCodeArray.length;
        double[] YArray = new double[size];
        model.changeMaxValue(rgb);

        //==========================================================================
        // ��M�G�ת��p��, �ثe�HRGB���ͪ��G�׬����
        //==========================================================================
        for (int x = 0; x < size; x++) {
            if (whiteYLut != null) {
                //�p�G��whiteYLut, �h�O�^�ǲz�Q���ժ��G��
                YArray[x] = whiteYLut.getValue(whiteCodeArray[x]);
            } else {
                //�S��whiteLut, �^�Ǫ��O���O��l�b��code�U���G��
                rgb.setValue(RGBBase.Channel.W, whiteCodeArray[x],
                             RGB.MaxValue.Double255);
                YArray[x] = model.getXYZ(rgb, false).Y;
            }
        }
        //==========================================================================
        return YArray;
    }

    /**
     * �ˬdpattern�O�_�q�L
     * @param CCTCurve double[]
     * @param pattern int[]
     * @return boolean
     */
    protected final boolean checkPattern(double[] CCTCurve, int[] pattern) {
        return checkPattern(toCIExyYArray(CCTCurve), pattern);
    }

    /**
     * �ˬd�Ҧ���pattern�O�_�q�L
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
     * �Hpattern�ˬd�O�_�q�L����
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
     * CCT���׮y��
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
     * �Ѽ�
     */
    private CCTParameter p;

    /**
     * �]�w�Ѽ�
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
     * �̷�cctStyle���w���覡, ��tempK���xyY
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
         * �νT�����T��E�ȧ@�����I
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
         * �H��ŧ@�����I
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
         * �qstart�Minterval���ͥXcode array
         *
         * @param start int
         * @param interval int
         * @return int[]
         * @todo H acp ���ͪ�code array�̫�@�X�n�h��, �i��|�v�T�̫Უ�ͪ����
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
         * ����I��code�����j
         */
        private int startCode = -1, codeInterval = -1;
        private int whiteCCT;
        private CIEXYZ black;
        private int[] codeArray = null;
        private CIEXYZ white;
        private ColorProofParameter p;
        /**
         * �Ƕ�������������\ab��t
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
     * ��󧡤æ�m�Ŷ��U, �H�A���k���ͪ���צ��u
     *
     * <p>Copyright: Copyright (c) 2008</p>
     *
     * <p>Company: skygroup</p>
     *
     * @author skyforce
     * @version 1.0
     */
    protected final class UniformAdaptivexyYCurve extends AdaptivexyYCurveBase {
        //code�P���æ�m�Ŷ������ת����Y
        private Interpolation1DLUT lightnessLut;
        private ColorSpace colorSpace;
        private CIECAM02 cam;
        private CIEXYZ normalWhite;

        protected CIExyY[] getPredictedxyYCurve(CIExyY code1xyY,
                                                CIExyY startcodexyY,
                                                boolean regress) {
            int size = p.startCode;
            //�t��code���j�p
            int dimsize = size - 2;
            double[] codeArray = DoubleArray.buildX(2, size - 1, dimsize);
            double[][] output = new double[2][dimsize];
            CIExyY[] predictxyYCurve = new CIExyY[dimsize];

            //========================================================================
            // �p��2~ startCode-1 ��xyY
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
         * �Hcode���regress������uv', �A��^XYZ
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
                //ciecam02���n���ܼƪ�l��
                normalWhite = (CIEXYZ) white.clone();
                normalWhite.normalizeY100();
                ViewingConditions vc = null; //ViewingConditions.getDarkViewingConditions(
                //normalWhite);
                cam = new CIECAM02(vc);
            }
            //�H�A��code�Ϭq�H��u'�Mv'�@�^�k
            this.initAdaptiveRegress();
            //�إ�code�P���ת����Y
            initLightnessLut();
        }

        /**
         * �إ�code�P���æ�m�Ŷ��������פ��������Y
         */
        protected void initLightnessLut() {
            if (lightnessLut == null) {
                final int size = 256;
                double[] LArray = new double[size];
                double[] codeArray = DoubleArray.buildX(0, size - 1, size);
                //code�������G��
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
     * �Ncoord�}�C�H�^�k�G���A���s���ͨ�xyY��
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
        // �^�k
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
        // �p��operator
        //========================================================================
        double[][] actual = new double[][] {
                            code1xyY.getuvPrimeValues(),
                            startcodexyY.getuvPrimeValues()};

        Operator operator = getOperator(regress, 1, startcode, actual);
        regress.setOperator(operator);
        //========================================================================

        //========================================================================
        // �Q�Φ^�k�w�����G
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
     * �p��X��Ū�xyY���u
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
        // 4.�HG code�H������I, ��������I(�]�A����I)�H�e���P���I����Ū��Ϭq
        // �H������I�H�����I���ܤƦ�ŰϬq
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
     * ���� �NstartIndex�MendIndex�����actualOutput ��Operator
     * @param regress CompositePolynomialRegression
     * @param startIndex int �n������_�Iindex
     * @param endIndex int �n��������Iindex
     * @param actualOutput double[][] �n��������
     * @return Operator
     */
    protected final static Operator getOperator(CompositePolynomialRegression
                                                regress, int startIndex,
                                                int endIndex,
                                                double[][] actualOutput) {
        //========================================================================
        // �N�^�k���_�I�β��I�վ�^��l��
        //========================================================================
        double[][] predict = DoubleArray.transpose(
                regress.getMultiPredict(new double[] {startIndex, endIndex}));

        Operator op = Operator.getAdjustOperator(actualOutput[0],
                                                 actualOutput[1],
                                                 predict[0], predict[1]);
        return op;
    }
}
