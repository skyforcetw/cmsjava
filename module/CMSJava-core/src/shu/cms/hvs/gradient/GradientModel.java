package shu.cms.hvs.gradient;

import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.image.*;

import org.apache.commons.collections.primitives.*;
//import shu.cms.applet.gradient.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.hvs.*;
import shu.cms.hvs.cam.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.cms.hvs.cam.ciecam02.CIECAM02;
import shu.cms.hvs.cam.ciecam02.ViewingConditions;
import shu.cms.hvs.hk.*;
import shu.cms.image.*;
import shu.image.*;
import shu.cms.lcd.*;
import shu.cms.profile.*;
import shu.math.*;
import shu.math.array.*;
import shu.util.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 人眼評估Gradient的model
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class GradientModel {

    protected ProfileColorSpace pcs;
    CIEXYZ white = null;
    private BufferedImage image;
    protected int imageStart, imageEnd;
    private RGBBase.Channel imageChannel;

    /**
     * 從RGBW四個漸層來評斷是否平順
     * @return boolean
     */
    public final boolean isSmooth() {
        boolean smooth = true;
        RGBBase.Channel[] channels = (mode == Mode.LuminanceOnly ||
                                      mode == Mode.JNDIOnly) ?
                                     new RGBBase.Channel[] {
                                     RGBBase.Channel.W}
                                     : RGBBase.Channel.RGBWChannel;

        for (RGBBase.Channel ch : channels) {
            Logger.log.info(ch);
            smooth = isSmooth(ch) && smooth;
        }

        return smooth;
    }

    /**
     * 評斷ch的漸層是否平順
     * @param ch Channel
     * @return boolean
     */
    public final boolean isSmooth(RGBBase.Channel ch) {
        setupImage(0, 255, ch, 256, 256);
        PatternAndScore ps = getPatternAndScore();
        List<Pattern> patternList = ps.patternList;

        for (Pattern p : patternList) {
            Logger.log.info(p);
        }
        //只要有任何值回傳, 就代表不順.
        return ps.isSmooth();
    }

    public GradientModel(LCDTarget target, CIEXYZ white) {
        this(ProfileColorSpace.Instance.get(target, target.getDescription()),
             target, white);

    }

    public GradientModel(LCDTarget target) {
        this(target, null);
    }

    public GradientModel(LCDModel lcdModel, CIEXYZ white) {
        this(ProfileColorSpace.Instance.get(lcdModel, lcdModel.getDescription()), null,
             white);

    }

    public GradientModel(LCDModel lcdModel) {
        this(lcdModel, null);
    }

    public GradientModel(double[] YArray) {
        this(YArray, true);
    }

    protected GradientModel() {

    }

    public GradientModel(double[] dataArray, boolean isLuminance) {
        if (isLuminance) {
            this.YArray = dataArray;
            this.hkStrategy = HKStrategy.None;
            mode = Mode.LuminanceOnly;
        } else {
            this.jndiCurve = dataArray;
            this.hkStrategy = HKStrategy.None;
            mode = Mode.JNDIOnly;
        }

    }

    protected static enum Mode {
        LuminanceOnly, JNDIOnly, Normal
    }


    private Mode mode = Mode.Normal;
    private double[] YArray;

    private GradientModel(ProfileColorSpace pcs, LCDTarget target, CIEXYZ white) {
        this.pcs = pcs;
        if (white == null) {
            this.white = (CIEXYZ) pcs.getReferenceWhite().clone();
        } else {
            this.white = (CIEXYZ) white.clone();
        }

        if (target != null) {
            this.target = target;
            //預設影像的channel
            this.imageChannel = target.getTargetChannel();
        }
        initCAM();
    }

    /**
     * 取得影像的RGB code curve
     * @return double[]
     */
    private double[] getCodeCurve() {
        int size = image.getWidth();
        double[] curve = new double[size];
        int[] RGBValues = new int[3];

        for (int x = 0; x < size; x++) {
            image.getRaster().getPixel(x, 0, RGBValues);
            double max = Maths.max(RGBValues);
            curve[x] = max;
        }
        return curve;
    }

    /**
     * 以code的變化來偵測邊緣的index
     * @return int[]
     */
    private int[] detectIdealBorderIndexByCode() {
        double[] codeCurve = getCodeCurve();
        ArrayList<Integer> list = new ArrayList<Integer>();
        int size = codeCurve.length;
        for (int x = 0; x < size - 1; x++) {
            if (codeCurve[x] != codeCurve[x + 1]) {
                list.add(x);
            }
        }
        return Utils.list2IntArray(list);
    }

    /**
     * 取得邊界的三刺激值
     * @return double[][]
     */
    double[][] getBorderXYZValues() {
        DeviceIndependentImage diImage = DeviceIndependentImage.getInstance(
                image,
                pcs);
        int[] indexArray = detectIdealBorderIndexByCode();
        int size = indexArray.length;
        double[][] borderXYZValues = new double[size][3];
        for (int x = 0; x < size; x++) {
            int index = indexArray[x];
            diImage.getXYZValues(index, 0, borderXYZValues[x]);
        }
        return borderXYZValues;
    }

    /**
     * 設定影像的參數
     * @param start int
     * @param end int
     * @param R boolean
     * @param G boolean
     * @param B boolean
     * @param scale int
     * @param width int
     * @param height int
     */
    protected void setupImage(int start, int end, boolean R, boolean G,
                              boolean B,
                              int scale, int width, int height) {
        image = GradientImage.getImage(new Dimension(width, height),
                                       start,
                                       end, R, G, B, false, false,
                                       scale, false, image);
        this.reset();
    }

    /**
     * 設定smooth相關運算時所使用的image影像
     * @param start int
     * @param end int
     * @param ch Channel
     * @param scale int
     * @param width int
     */
    public void setupImage(int start, int end, RGBBase.Channel ch, int scale,
                           int width) {
        setupImage(start, end, ch, scale, width, DEFAULT_IMAGE_HEIGHT);
    }

    /**
     * 預設的影像高度, 由於不考慮空間的影響, 所以設1就好不需變動.
     */
    public final static int DEFAULT_IMAGE_HEIGHT = 1;

    /**
     * 設定smooth相關運算時所使用的image影像
     * @param start int code的start
     * @param end int code的end
     * @param ch Channel 選用的channel
     * @param scale int RGB總共的變化程度(最多的變化程度為0~255, 也就是256個)
     * @param width int 影像的寬
     * @param height int 影像的高
     */
    public void setupImage(int start, int end, RGBBase.Channel ch, int scale,
                           int width, int height) {
        boolean R = false, G = false, B = false;
        imageStart = start;
        imageEnd = end;
        switch (ch) {
        case R:
            R = true;
            break;
        case G:
            G = true;
            break;
        case B:
            B = true;
            break;
        case W:
            R = G = B = true;
            break;
        }
        setImageChannel(ch);
        setupImage(start, end, R, G, B, scale, width, height);
    }

    public void setImageChannel(RGBBase.Channel ch) {
        this.imageChannel = ch;
    }

    private double[][][] getXYZImage() {
        if (image != null) {
            DeviceIndependentImage di = DeviceIndependentImage.getInstance(
                    image,
                    pcs);
            PlaneImage oppImage = PlaneImage.getInstance(di,
                    PlaneImage.Domain.XYZ);
            return oppImage.getCIEXYZImage();
        } else {
            return null;
        }
    }

    /**
     *
     * <p>Title: Colour Management System</p>
     *
     * <p>Description: a Colour Management System by Java</p>
     * 對比的計算方式
     *
     * <p>Copyright: Copyright (c) 2008</p>
     *
     * <p>Company: skygroup</p>
     *
     * @author skyforce
     * @version 1.0
     */
    public static enum Contrast {
        /**
         * target / background
         */
        Luminance,
        /**
         * (target - background) / (target + background)
         */
        Michelson,
        MichelsonModified
    }


    private class _Contrast {
        /**
         * 取出對比曲線
         * @param type Contrast
         * @return double[]
         */
        public double[] getContrastCurve(Contrast type) {
            final double[][][] XYZValuesImage = getXYZImage();
            return getContrastCurve(type, XYZValuesImage);
        }

        /**
         *
         * @param type Contrast
         * @param values double[]
         * @return double
         */
        protected final double getContrast(Contrast type, double[] values) {
            double background = Interpolation.linear(0, 2, values[0],
                    values[2], 1);
            double target = values[1];
            switch (type) {
            case Luminance:
                return target / background;
            case Michelson:
                return (target - background) / (target + background);
            case MichelsonModified:
                double diff = target - background;
                double angle = Math.atan2((values[2] - values[1]), 2.);
                target = background = diff * Math.cos(angle);
                return (target - background) / (target + background);
            default:
                return -1;
            }
        }

        /**
         *
         * @param type Contrast
         * @param XYZValuesImage double[][][]
         * @return double[]
         */
        protected double[] getContrastCurve(Contrast type,
                                            double[][][] XYZValuesImage) {
            int h = XYZValuesImage.length;
            final double[][] row = XYZValuesImage[h / 2];
            int size = row.length;
            final double[] YArray = DoubleArray.transpose(row)[1];
            double[] contrastArray = new double[size - 3];

            for (int x = 2; x < size - 1; x++) {
                double background = Interpolation.linear(0, 2, YArray[x - 1],
                        YArray[x + 1], 1);
                double target = YArray[x];
                switch (type) {
                case Luminance:
                    contrastArray[x - 2] = target / background;
                    break;
                case Michelson:
                    contrastArray[x - 2]
                            = (target - background) / (target + background);
                    break;
                case MichelsonModified:

                    double diff = target - background;
                    double angle = Math.atan2((YArray[x + 1] - YArray[x - 1]),
                                              2.);
                    target = background = diff * Math.cos(angle);
                }

            }

            return contrastArray;
        }
    }


    public void reset() {
        if (mode != Mode.JNDIOnly) {
            jndiCurve = null;
        }
    }

    /**
     * 取得不順的Pattern以及其smooth的分數
     * @return PatternAndScore
     */
    public abstract PatternAndScore getPatternAndScore();

    protected double thresholdPercent = 10;

    /**
     * 設定threshold percent
     * @param percent double
     */
    public void setThresholdPercent(double percent) {
        this.thresholdPercent = percent;
    }

    /**
     * 設定建議的threshold percent(此乃經驗值)
     * @param channel Channel
     */
    public void setRecommendThresholdPercent(RGBBase.Channel channel) {
        switch (channel) {
        case W:
            this.setThresholdPercent(10);
            break;
        case R:
            this.setThresholdPercent(30);
            break;
        case G:
            this.setThresholdPercent(10);
            break;
        case B:
            this.setThresholdPercent(20);
            break;
        }
    }

    public double getThresholdPercent() {
        return thresholdPercent;
    }

    protected double signalFixedThreshold = 2;
    public void setSignalFixedThreshold(double signalFixedThreshold) {

        this.signalFixedThreshold = signalFixedThreshold;
    }

    /**
     *
     * <p>Title: Colour Management System</p>
     *
     * <p>Description: a Colour Management System by Java</p>
     * 紀錄不順pattern以及對映smooth分數的類別
     *
     * <p>Copyright: Copyright (c) 2008</p>
     *
     * <p>Company: skygroup</p>
     *
     * @author skyforce
     * @version 1.0
     */
    public static class PatternAndScore {

        public boolean isSmooth() {
            return patternList.size() == 0;
        }

        public PatternAndScore(List<Pattern> patternList, double score,
                double overScore,
                double[] signal, double[] deltaAccelArray) {
            this.patternList = patternList;
            this.score = score;
            this.overScore = overScore;
            this.signal = signal;
            this.deltaAccelArray = deltaAccelArray;
        }

        /**
         * 計算signal所採用的signal, 通常就是指JND Index
         */
        public double[] signal;

        /**
         * 不順的pattern
         */
        public List<Pattern> patternList;
        /**
         * smooth的分數, 越低越smooth
         */
        public double score;
        /**
         * 與目標加速度的差異
         */
        public double[] deltaAccelArray;

        /**
         * 不順的分數, 越低越不會不順
         */
        public double overScore;
    }


    /**
     *
     * @param indexList ArrayDoubleList
     * @param signalList ArrayDoubleList
     * @return double[][]
     * @deprecated
     */
    protected static double[][] producePatternIndexAndSignal(ArrayDoubleList
            indexList, ArrayDoubleList signalList) {
        //index值
        double[] indexResult = indexList.toArray();
        //index對應的signal值
        double[] signalResult = signalList.toArray();
        double[][] patternIndexAndSignal = DoubleArray.transpose(new double[][] {
                indexResult, signalResult});
        return patternIndexAndSignal;
    }

    /**
     *
     * @param lists ArrayDoubleList[]
     * @return double[][]
     * @deprecated
     */
    protected static double[][] producePatternIndexAndSignal(ArrayDoubleList ...
            lists) {
        int size = lists.length;
        double[][] result = new double[size][];
        for (int x = 0; x < size; x++) {
            result[x] = lists[x].toArray();
        }
        double[][] patternIndexAndSignal = DoubleArray.transpose(result);
        return patternIndexAndSignal;
    }

    /**
     * 取得channel對應的index.
     * @return int
     */
    protected int getChannelIndex() {
        return getChannelIndex(imageChannel);
    }

    /**
     * 取得channel對應的index.
     * 因為在RGB的channel對應index方法中, white會對應到6.
     * 然而這邊的white要對應到3, 所以另外寫一個方法計算ChannelIndex
     * @param ch Channel
     * @return int
     */
    protected int getChannelIndex(RGBBase.Channel ch) {
        int index = ch.getArrayIndex();
        index = ch == RGBBase.Channel.W ? 3 : index;
        return index;
    }

    public double[] getJNDIndexCurve(CIExyY[] xyYArray) {
        CIEXYZ[] XYZArray = CIExyY.toXYZArray(xyYArray);
        return getJNDIndexCurve(XYZArray);
    }

    public double[] getJNDIndexCurve(CIEXYZ[] XYZArray) {
        int size = XYZArray.length;
        double[] jndiCurve = new double[size];
        for (int x = 0; x < size; x++) {
            jndiCurve[x] = getJNDIndex(XYZArray[x], hkStrategy);
        }
        return jndiCurve;
    }

    /**
     * 將影像的XYZ轉成GSDF曲線
     * 並且考量了色度對JNDI的影響, 考量的方式目前有 Nayatani和CIECAM02.
     * 雖然CIECAM02其實並沒有考量到色度對明度的影響, 但是實際測試結果較Nayatani佳.
     * @return double[]
     */
    public double[] getJNDIndexCurve() {
        if (jndiCurve != null) {
            return jndiCurve;
        }
        if (mode == Mode.LuminanceOnly) {
            //只有亮度數據的狀況, 直接用GSDF轉成JNDI
            int size = this.YArray.length;
            jndiCurve = new double[size];
            for (int x = 0; x < size; x++) {
                double Y = YArray[x];
                jndiCurve[x] = GSDF.DICOM.getJNDIndex(Y);
            }
        } else {
            //有XYZ數據的狀況, 此時要考慮到HK效應, 所以要將XYZ全部擺出來
            final double[][][] XYZValuesImage = getXYZImage();
            int h = XYZValuesImage.length;
            final double[][] row = XYZValuesImage[h / 2];
            int size = row.length;
            jndiCurve = new double[size];

            CIEXYZ XYZ = new CIEXYZ();
            for (int x = 0; x < size; x++) {
                XYZ.setValues(row[x]);
                jndiCurve[x] = getJNDIndex(XYZ, hkStrategy);
            }

        }
        return jndiCurve;
    }

    /**
     * gsdf曲線, 由JNDIndex組成
     */
    private double[] jndiCurve;

    /**
     *
     * <p>Title: Colour Management System</p>
     *
     * <p>Description: a Colour Management System by Java</p>
     * 在解決HK效應所採用的方法
     *
     * <p>Copyright: Copyright (c) 2008</p>
     *
     * <p>Company: skygroup</p>
     *
     * @author skyforce
     * @version 1.0
     */
    public static enum HKStrategy {
        Nayatani,
        CIECAM02,
        CIELuv,
        None
    }


    private HKStrategy hkStrategy = HKStrategy.CIECAM02;

    /**
     * 設定HK效應的解決方法
     * @param strategy HKStrategy
     */
    public void setHKStrategy(HKStrategy strategy) {
        this.hkStrategy = strategy;
    }

    CIECAM02 cam = null;
    private ViewingConditions vc = null;

    /**
     * 建立J<->JNDI的關係
     */
    private CIECAM02JNDIndex camJNDIndex;
    /**
     * 建立J<->Y的關係
     */
    private CIECAM02JNDIndex[] camJNDIArray = null;
    protected LCDTarget target;

    /**
     * 由JNDIndex轉回亮度(Y)
     * 未指定頻道, 則以預設的影像頻道作對映.
     * @param JNDIndex double
     * @return double
     */
    public double getLuminance(double JNDIndex) {
        return getLuminance(JNDIndex, this.hkStrategy);
    }

    /**
     * 由JNDIndex轉回亮度(Y)
     * 已指定頻道, 則以指定頻道的對映表作對映.
     * @param JNDIndex double
     * @param ch Channel
     * @return double
     */
    public double getLuminance(double JNDIndex, RGBBase.Channel ch) {
        return getLuminance(JNDIndex, this.hkStrategy, ch);
    }

    public double[] getLuminanceCurve(double[] JNDIndexCurve) {
        int size = JNDIndexCurve.length;
        double[] luminanceCurve = new double[size];
        for (int x = 0; x < size; x++) {
            luminanceCurve[x] = getLuminance(JNDIndexCurve[x], this.hkStrategy);
        }
        return luminanceCurve;
    }

    /**
     * 將JNDIndex轉回到Luminance, 且要把HK的補償去除
     * @param JNDIndex double
     * @param strategy HKStrategy
     * @return double
     */
    private double getLuminance(double JNDIndex, HKStrategy strategy) {
        return getLuminance(JNDIndex, strategy, imageChannel);
    }

    /**
     * 將JNDIndex轉回到Luminance, 且要把HK的補償去除
     * @param JNDIndex double
     * @param strategy HKStrategy
     * @param ch Channel
     * @return double
     */
    private double getLuminance(double JNDIndex, HKStrategy strategy,
                                RGBBase.Channel ch) {
        switch (strategy) {
        case None:
            return GSDF.DICOM.getLuminance(JNDIndex);
        case CIECAM02:
            return getCIECAM02Luminance(JNDIndex, ch);
        case Nayatani:
            throw new UnsupportedOperationException(strategy.name());
        case CIELuv:
            throw new UnsupportedOperationException(strategy.name());
        default:
            return -1;
        }
    }

    /**
     * 取得JNDIndex, 且考慮了HK的影響做了補償, 並且以預設的HK補償法作補償.
     * @param XYZ CIEXYZ
     * @return double
     */
    public double getJNDIndex(final CIEXYZ XYZ) {
        return getJNDIndex(XYZ, this.hkStrategy);
    }

    /**
     * 取得JNDIndex, 且考慮了HK的影響做了補償
     * @param XYZ CIEXYZ
     * @param strategy HKStrategy
     * @return double
     */
    private double getJNDIndex(final CIEXYZ XYZ, HKStrategy strategy) {
        switch (strategy) {
        case None:
            return GSDF.DICOM.getJNDIndex(XYZ.Y);
        case CIECAM02:
            return getCIECAM02JNDIndex(XYZ);
        case Nayatani:

//        return getNayataniJNDIndex(XYZ);
            throw new UnsupportedOperationException();
        case CIELuv:
            return getCIELuvJNDIndex(XYZ);
        default:
            return -1;
        }
    }

    /**
     * 以CIECAM作為中介的轉換空間(XYZ->JCh->JNDI)
     * @param XYZ CIEXYZ
     * @return double
     */
    private double getCIECAM02JNDIndex(final CIEXYZ XYZ) {
        CIEXYZ clone = (CIEXYZ) XYZ.clone();
        clone.normalize(white);
        clone.normalize(NormalizeY.Normal100);
        //XYZ轉JCh
        CIECAM02Color color = cam.forward(clone);
        //再從J找到對應的JNDI
        double JNDI = camJNDIndex.getJNDIndex(color.J);
        return JNDI;
    }

    private double getCIECAM02Luminance(double JNDIndex, RGBBase.Channel ch) {
        //首先由JNDIndex轉明度
        double lightness = camJNDIndex.getLightness(JNDIndex);
        int index = getChannelIndex(ch);

        //明度轉亮度
        return camJNDIArray[index].getMonochromeLuminance(lightness);
    }

    /**
     *
     * @param JNDIndex double
     * @param ch Channel
     * @return double
     * @todo H  CIELuv HK
     */
    private double getCIELuvLuminance(double JNDIndex, RGBBase.Channel ch) {
        return -1;
    }

    private CIELuvHKModel.Type LuvType = CIELuvHKModel.Type.SandersWyszecki;
    private double getCIELuvJNDIndex(final CIEXYZ XYZ) {
        CIELuv Luv = new CIELuv(XYZ, white);
        double L = CIELuvHKModel.getHKLightness(Luv, LuvType);
        Luv.L = L;
        double hkLumi = Luv.toXYZ().Y;
        return GSDF.DICOM.getJNDIndex(hkLumi);
    }

    private final boolean ignoreChannel(RGBBase.Channel ch) {
        RGBBase.Channel targetch = target != null ? target.getTargetChannel() : null;
        return target != null && (targetch == null ? false : ch != targetch);

    }

    private static int startCode = 0;
    private static int endCode = 255;
    public final static void setAnalyzeRange(int start, int end) {
        startCode = start;
        endCode = end;
    }

    private void initCAM() {
        //==========================================================================
        // 色外貌相關
        //==========================================================================
        CIEXYZ white = (CIEXYZ)this.white.clone();
        double La = white.Y * 0.2;
        white.normalizeY100();
        vc = new ViewingConditions(white, La, 20, Surround.Dim,
                                   "Dim");
        cam = new CIECAM02(vc);
        //==========================================================================

        //==========================================================================
        // CIECAM02
        //==========================================================================
        camJNDIndex = new CIECAM02JNDIndex(this.cam, this.white);
        //==========================================================================

        //==========================================================================
        camJNDIArray = new CIECAM02JNDIndex[4];
        for (RGBBase.Channel ch : RGBBase.Channel.RGBWChannel) {
            if (ignoreChannel(ch)) {
                continue;
            }
            this.setupImage(startCode, endCode, ch, 256,
                            endCode - startCode + 1, 1);
            double[][][] XYZValuesImage = getXYZImage();
            double[][] row = XYZValuesImage[0];
            CIECAM02JNDIndex camjndi = new CIECAM02JNDIndex(this.cam,
                    this.white);
            camjndi.setupMonochromeLUT(CIEXYZ.getCIEXYZArray(row));
            camJNDIArray[this.getChannelIndex(ch)] = camjndi;
        }
        //==========================================================================

    }

    /**
     * 理想的邊緣signal的index值
     * @return int[]
     * @deprecated
     */
    private int[] getIdealBorderSignalIndex() {
        int[] borderIndexes = detectIdealBorderIndexByCode();
        int size = borderIndexes.length - 1;
        //由於每一個code皆會對應 高值 及 低值, 因此要x2
        int[] signalIndex = new int[size * 2];
        for (int x = 0; x < size; x++) {
            signalIndex[x * 2] = borderIndexes[x];
            signalIndex[x * 2 + 1] = borderIndexes[x] + 1;
        }
        return signalIndex;
    }

    /**
     * 計算pattern的總和, 也就是smooth的分數, 越低越好
     * @param patternList List
     * @return double
     */
    protected final static double getPatternScore(List<Pattern> patternList) {
        double score = 0;
        for (Pattern p : patternList) {
            score += Math.abs(p.pattern);
        }
        return score;
    }

    /**
     * 計算達到人眼可辨識的pattern其分數總和
     * @param patternList List
     * @return double
     */
    protected final static double getOverScore(List<Pattern> patternList) {
        double score = 0;
        for (Pattern p : patternList) {
            double overRatio = Math.abs(p.overRatio);
            if (overRatio > 100f) {
                score += overRatio / 100.;
            }
        }
        return score;
    }
}
