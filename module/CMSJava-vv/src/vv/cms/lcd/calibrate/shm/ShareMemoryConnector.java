package vv.cms.lcd.calibrate.shm;

import java.io.*;
import java.util.*;

import java.awt.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import vv.cms.lcd.calibrate.*;
import vv.cms.lcd.calibrate.measured.*;
import vv.cms.lcd.calibrate.parameter.*;
import shu.cms.util.*;
import shu.util.log.*;
import shu.util.shm.*;
import vv.cms.measure.cp.CPCodeLoader;

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
public class ShareMemoryConnector implements ShareMemoryAdapter.
        DataReadyListener,
        CPCodeLoader.LoaderInterface {

    public static enum Whoami {
        AutoCP(652), CP(9020);

        Whoami(int code) {
            this.code = code;
        }

        public static Whoami geByCode(int code) {
            for (Whoami whoami : values()) {
                if (whoami.code == code) {
                    return whoami;
                }
            }
            return null;
        }

        public int code;
    }


    public static enum Command {
        Load(10001), Ack(10002), Measure(10004), MeasureData(10008),
        WhiteParameter(10016), ViewingParameter(10032), ColorProofParameter(
                10064),
        GreenAdjustParameter(10128), MeasureParameter(10256), DownloadCode(
                10512),
        Message(11024), Measure2(12048), DownloadCode2(14096);

        Command(int code) {
            this.code = code;
        }

        public int code;

        public static Command geByCode(int code) {
            for (Command command : values()) {
                if (command.code == code) {
                    return command;
                }
            }
            return null;
        }
    }


    private ShareMemoryConnector() {
        adapter = new ShareMemoryAdapter();
        adapter.setDataReadyListener(this);
    }

    public static void main(String[] args) {
        ShareMemoryConnector connector = ShareMemoryConnector.getInstance();
        connector.sendMessage("");
    }

    private static void rgbArray2DataOutputStream(RGB[] rgbArray,
                                                  DataOutputStream dos,
                                                  RGB.MaxValue maxValue) {
        int size = rgbArray.length;
        try {
            for (int x = 0; x < size; x++) {
                RGB rgb = rgbArray[x];
                double[] rgbValues = rgb.getValues(new double[3], maxValue);
                dos.writeShort((short) rgbValues[0]);
                dos.writeShort((short) rgbValues[1]);
                dos.writeShort((short) rgbValues[2]);
            }
        } catch (IOException ex) {
            Logger.log.error("", ex);
        }
    }

    /**
     * 量測的同時, 指定背景和插色的index. (GUI端尚未支援)
     * @param rgbArray RGB[]
     * @param backgroundIndex int
     * @param blankIndex int
     * @return CIEXYZ[]
     */
    public CIEXYZ[] measure(RGB[] rgbArray, int backgroundIndex, int blankIndex) {
        //==========================================================================
        // 將code載入到shm
        //==========================================================================
        try {
            sendMeasureRGBArray(rgbArray, backgroundIndex, blankIndex);
        } catch (IOException ex) {
            Logger.log.debug("", ex);
            return null;
        }
        //==========================================================================

        try {
            CIEXYZ[] result = readXYZ();
            return result;
        } catch (IOException ex) {
            Logger.log.debug("", ex);
            return null;
        }
    }

    public CIEXYZ[] measure(RGB[] rgbArray) {
        //==========================================================================
        // 將code載入到shm
        //==========================================================================
        try {
            sendRGBArray(new RGB[][][] { {rgbArray}
            }, null, Command.Measure, -1);
        } catch (IOException ex) {
            Logger.log.debug("", ex);
            return null;
        }
        //==========================================================================

        try {
            CIEXYZ[] result = readXYZ();
            return result;
        } catch (IOException ex) {
            Logger.log.debug("", ex);
            return null;
        }
    }

    private CIEXYZ[] readXYZ() throws IOException {
        DataInputStream dis = adapter.getDataInputStream();
        Header h = Operator.getHeader(dis);
        if (h.command != Command.MeasureData) {
            throw new IllegalStateException("Command != MeasureData");
        }
        int datasize = h.dataSize;
        int dataCount = datasize / 18;
        byte[] data = new byte[datasize];
        dis.read(data);

        CIEXYZ[] XYZArray = new CIEXYZ[dataCount];
        for (int x = 0; x < dataCount; x++) {
            int index = x * 18;

            byte[] xdata = Arrays.copyOfRange(data, index, index + 6);
            byte[] ydata = Arrays.copyOfRange(data, index + 6, index + 12);
            byte[] zdata = Arrays.copyOfRange(data, index + 12, index + 18);

            double X = toDouble(xdata);
            double Y = toDouble(ydata);
            double Z = toDouble(zdata);
            XYZArray[x] = new CIEXYZ(X, Y, Z);
        }
        return XYZArray;
    }

    private final static double toDouble(byte[] byteFormat) {
        int exp = byteFormat[0] >> 4 & 0xf;
        long l = ((long) byteFormat[0] & 0xf) << 40;
//    l <<= 32;
        for (int x = 0; x < 5; x++) {
            int offset = (4 - x) * 8;
            long v = byteFormat[x + 1] & 0xff;
            v = v << offset;
            l |= v;
        }
        return l / Math.pow(10, exp);
    }

    private static class Format {
        private static double getDouble(int intPart, long floatPart) {
            long floatDigit = longDigitSize(Math.abs(floatPart));
            double result = ((double) floatPart) / Math.pow(10, floatDigit) +
                            intPart;
            return result;
        }

        private final static int[] sizeTable = {
                                               9, 99, 999, 9999, 99999, 999999,
                                               9999999,
                                               99999999, 999999999,
                                               Integer.MAX_VALUE};

        // Requires positive x
        private static int integerDigitSize(int x) {
            for (int i = 0; ; i++) {
                if (x <= sizeTable[i]) {
                    return i + 1;
                }
            }
        }

        private static int longDigitSize(long x) {
            long p = 10;
            for (int i = 1; i < 19; i++) {
                if (x < p) {
                    return i;
                }
                p = 10 * p;
            }
            return 19;
        }

        private static int readUnsignedByte(byte[] data, int index) {
            int c1 = ((int) ((char) data[index] & 0xff)) << 8;
            int c2 = ((int) ((char) data[index + 1] & 0xff)) << 0;
            return c1 + c2;
        }

        private static long readUnsignedInt(byte[] data, int index) {
            long c1 = ((long) ((char) data[index] & 0xff)) << 24;
            long c2 = ((long) ((char) data[index + 1] & 0xff)) << 16;
            long c3 = ((long) ((char) data[index + 2] & 0xff)) << 8;
            long c4 = ((long) ((char) data[index + 3] & 0xff)) << 0;
            return c1 + c2 + c3 + c4;
        }

        /**
         *
         * @param intPart short
         * @param floatPart int
         * @return double
         */
        private static double getDouble(short intPart, int floatPart) {
            int floatDigit = integerDigitSize(Math.abs(floatPart));
            double result = ((double) floatPart) / Math.pow(10, floatDigit) +
                            intPart;
            return result;
        }
    }


    public boolean sendMessage(String msg) {
        try {
            DataOutputStream dos = adapter.getDataOutputStream();
            dos.writeInt(Whoami.AutoCP.code);
            dos.writeInt(Command.Message.code);
            byte[] bytes = msg.getBytes();
            int size = bytes.length;
            dos.writeInt(size);
            dos.write(bytes);
            dos.flush();
//      return readAck();
            return true;
        } catch (IOException ex) {
            Logger.log.debug("", ex);
            return false;
        }
    }

    public void sendAck(boolean result) {
        try {
            DataOutputStream dos = adapter.getDataOutputStream();
            dos.writeInt(Whoami.AutoCP.code);
            dos.writeInt(Command.Ack.code);
            dos.writeInt(1);
            dos.writeByte(result ? 1 : 0);
            dos.flush();
        } catch (IOException ex) {
            Logger.log.debug("", ex);
        }
    }

    public boolean isInputStreamAvailable() {
        return adapter.isInputStreamAvailable();
    }

    private boolean readAck() throws IOException {
        if (!adapter.isInputStreamAvailable()) {
            throw new IllegalArgumentException(
                    "ShareMemoryAdapter.InputStream is unavailable.");
        }
        //==========================================================================
        // 解析ack
        //==========================================================================
        DataInputStream dis = adapter.getDataInputStream();
        Header h = Operator.getHeader(dis);
        if (h.command != Command.Ack) {
            throw new IllegalStateException("Command != Command.Ack");
        }
        byte ack = dis.readByte();
        return ack == 1;

        //==========================================================================
    }

    private final static int getByteArraySize(RGB[] rgbArray) {
        return 6 * rgbArray.length;
    }

    private final static int getByteArraySize(RGB[][] rgbArrayArray) {
        int size = 0;
        for (RGB[] rgbArray : rgbArrayArray) {
            size += getByteArraySize(rgbArray);
        }
        return size;
    }

    private final static int getByteArraySize(RGB[][][] rgb3Array) {
        int size = 0;
        for (RGB[][] rgb2Array : rgb3Array) {
            size += getByteArraySize(rgb2Array);
        }
        return size;
    }

    private void sendMeasureRGBArray(RGB[] rgbArray, int backgroundIndex,
                                     int blankIndex) throws
            IOException {
        DataOutputStream dos = adapter.getDataOutputStream();
        dos.writeInt(Whoami.AutoCP.code);
        dos.writeInt(Command.Measure2.code);

        int byteArraySize = getByteArraySize(rgbArray);

        dos.writeInt(byteArraySize);
        dos.writeByte(backgroundIndex);
        dos.writeByte(blankIndex);

        rgbArray2DataOutputStream(rgbArray, dos, RGB.MaxValue.Int8Bit);
        dos.flush();
    }

    private void sendRGBArray(RGB[][][] rgb3Array, RGB.MaxValue icBit,
                              Command cmd, int extraInfo) throws
            IOException {
        DataOutputStream dos = adapter.getDataOutputStream();
        dos.writeInt(Whoami.AutoCP.code);
        dos.writeInt(cmd.code);

        int byteArraySize = getByteArraySize(rgb3Array);
        boolean downloadCodeMode = (cmd == Command.DownloadCode ||
                                    cmd == Command.DownloadCode2);

        byteArraySize = (icBit == null) ? byteArraySize : byteArraySize + 1;
        byteArraySize = downloadCodeMode ? byteArraySize + 1 : byteArraySize;
        dos.writeInt(byteArraySize);
        //==========================================================================
        // 如果需要記錄ic bit
        //==========================================================================
        if (icBit != null) {
            switch (icBit) {
            case Int10Bit:
                dos.writeByte(10);
                break;
            case Int12Bit:
                dos.writeByte(12);
                break;
            default:
                throw new IllegalArgumentException("Unsuppoted ic bit: " +
                        icBit);
            }
        }
        //==========================================================================
        if (downloadCodeMode) {
            if (extraInfo > Byte.MAX_VALUE) {
                Logger.log.warn("extraInfo(" + extraInfo +
                                ") > Byte.MAX_VALUE(" +
                                Byte.MAX_VALUE + "), could be make error.");
            }
            dos.writeByte(extraInfo);
        }

        RGB.MaxValue maxValue = (icBit != null) ? icBit : RGB.MaxValue.Int8Bit;
        for (RGB[][] rgb2Array : rgb3Array) {
            for (RGB[] rgbArray : rgb2Array) {
                rgbArray2DataOutputStream(rgbArray, dos, maxValue);
            }
        }
        dos.flush();
    }

    public boolean sendDownloadCode(final RGB[][][] cpcode3Array,
                                    final RGB.MaxValue icBit) {
        for (RGB[][] cpcode2Array : cpcode3Array) {
            for (RGB[] cpcodeArray : cpcode2Array) {
                if (cpcodeArray != null && cpcodeArray.length != 256) {
                    throw new IllegalArgumentException(
                            "cpcodeArray.length != 256");
                }
            }
        }

        try {
            sendRGBArray(cpcode3Array, icBit, Command.DownloadCode,
                         cpcode3Array.length);
        } catch (IOException ex) {
            Logger.log.error("", ex);
        }

        return true;
    }

    public boolean sendDownloadCode2(final RGB[][] cpcode2Array,
                                     final RGB.MaxValue icBit, int number) {
        for (RGB[] cpcodeArray : cpcode2Array) {
            if (cpcodeArray != null && cpcodeArray.length != 256) {
                throw new IllegalArgumentException("cpcodeArray.length != 256");
            }
        }

        try {
            sendRGBArray(new RGB[][][] {cpcode2Array}, icBit,
                         Command.DownloadCode2,
                         number);
        } catch (IOException ex) {
            Logger.log.error("", ex);
        }

        //==========================================================================
        // 解析ack
        //==========================================================================
        try {
            return readAck();
        } catch (IOException ex) {
            Logger.log.debug("", ex);
            return false;
        }
        //==========================================================================
    }

    private boolean sendCodeAndAck(final RGB[] cpcodeArray,
                                   final RGB.MaxValue icBit,
                                   Command cmd, boolean readAck) {
        //==========================================================================
        // 將code載入到shm
        //==========================================================================
        try {
            sendRGBArray(new RGB[][][] { {cpcodeArray}
            }, icBit, cmd, -1);
        } catch (IOException ex) {
            Logger.log.debug("", ex);
            return false;
        }
        //==========================================================================

        if (readAck) {
            //==========================================================================
            // 解析ack
            //==========================================================================
            try {
                boolean ack = readAck();
                return ack;
            } catch (IOException ex) {
                Logger.log.debug("", ex);
                return false;
            }
        } else {
            return true;
        }
        //==========================================================================
    }

    private final static DataInputStream getDataInputStream(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        return dis;
    }

    private WhiteParameter getWhiteParameter(byte[] data) throws IOException {

        DataInputStream dis = getDataInputStream(data);
        byte whiteSelect = dis.readByte();
        double whitex = Format.getDouble(0, dis.readShort());
        double whitey = Format.getDouble(0, dis.readShort());
        int CCT = dis.readShort();
        byte CCTStyleCode = dis.readByte();

        int whiteR = dis.readShort();
        int whiteG = dis.readShort();
        int whiteB = dis.readShort();
        int maxR = dis.readShort();
        int maxG = dis.readShort();
        int maxB = dis.readShort();
        int maxWhite = dis.readShort();
        RGB whiteRGB = new RGB(RGB.ColorSpace.unknowRGB, new double[] {whiteR,
                               whiteG, whiteB}, RGB.MaxValue.Int12Bit);
        RGB maxRGB = new RGB(RGB.ColorSpace.unknowRGB, new double[] {maxR, maxG,
                             maxB}, RGB.MaxValue.Int8Bit);

        WhiteParameter wp = null;
        switch (whiteSelect) {
        case 1: { //CIExyY
            CIExyY xyY = new CIExyY(whitex, whitey, 1);
            wp = new WhiteParameter(xyY);
            wp.maxRGB = maxRGB;
        }
        break;
        case 2: { //CCT
            CCTStyle cctStyle = CCTStyleCode == 0 ? CCTStyle.DIlluminant :
                                CCTStyle.Blackbody;
            wp = new WhiteParameter(CCT, cctStyle, maxRGB);
        }
        break;
        case 3: { //whiteCode
            wp = new WhiteParameter(whiteRGB);
        }
        break;
        default:
            throw new IllegalStateException();
        }
        wp.maxWhiteCode = maxWhite;
        return wp;
    }

    private final static ColorProofParameter.GammaBy getGammaBy(byte code) {
        switch (code) {
        case 1:
            return ColorProofParameter.GammaBy.G;
        case 2:
            return ColorProofParameter.GammaBy.W;
        default:
            return null;
        }
    }

    private final static ColorProofParameter.CCTCalibrate getCCTCalibrate(byte
            code) {
        switch (code) {
        case 1:
            return ColorProofParameter.CCTCalibrate.Corrected;
        case 2:
            return ColorProofParameter.CCTCalibrate.uvpByDE00;
        case 3:
            return ColorProofParameter.CCTCalibrate.uvpByIPT;
        case 4:
            return ColorProofParameter.CCTCalibrate.IPT;
        case 5:
            return ColorProofParameter.CCTCalibrate.CIECAM02;
        default:
            return null;
        }
    }

    private final static ColorProofParameter.Gamma getGamma(byte select) {
        switch (select) {
        case 1:
            return ColorProofParameter.Gamma.Native;
        case 2:
            return ColorProofParameter.Gamma.Smooth;
        case 3:
            return ColorProofParameter.Gamma.Scale;
        case 4:
            return ColorProofParameter.Gamma.Custom;
        case 5:
            return ColorProofParameter.Gamma.sRGB;
        case 6:
            return ColorProofParameter.Gamma.GSDF;
        case 7:
            return ColorProofParameter.Gamma.CustomCurve;
        case 8:
            return ColorProofParameter.Gamma.GCode;
        default:
            return null;
        }
    }

    private final static double[] getCustomCurve(DataInputStream dis) throws
            IOException {
        double[] customCurve = new double[256];
        byte[][] originalByteCurve = new byte[256][];
        long[] originalCurve = new long[256];

        double max = Math.pow(2, 52) - 1;
        byte[] byteArray = new byte[7];
        for (int x = 0; x < 256; x++) {
            dis.read(byteArray);
            long value = ((long) (byteArray[0] & 0xf) << 48) +
                         ((long) (byteArray[1] & 0xff) << 40) +
                         ((long) (byteArray[2] & 0xff) << 32) +
                         ((long) (byteArray[3] & 0xff) << 24) +
                         ((long) (byteArray[4] & 0xff) << 16) +
                         ((long) (byteArray[5] & 0xff) << 8) +
                         ((long) (byteArray[6] & 0xff));
            originalByteCurve[x] = byteArray;
            originalCurve[x] = value;
            customCurve[x] = value / max;
        }
        return customCurve;
    }

    private ColorProofParameter getColorProofParameter(byte[] data) throws
            IOException {
        DataInputStream dis = getDataInputStream(data);

        ColorProofParameter.Gamma gamma = getGamma(dis.readByte());
        double customGamma = Format.getDouble(dis.readByte(),
                                              dis.readByte());
        double[] customCurve = getCustomCurve(dis);

        ColorProofParameter.CCTCalibrate cctCalibrate = getCCTCalibrate(dis.
                readByte());
        ColorProofParameter.GammaBy gammaBy = getGammaBy(dis.readByte());
        int CCTStart = dis.readByte();
        int CCTEnd = dis.readByte();
        int turnCode = dis.readByte();
        int grayInterval = dis.readByte();
        int icBits = dis.readByte();
        int calibratedBits = dis.readByte();
        boolean keepBlack = dis.readBoolean();
        int runCount = dis.readByte();
        RGB[] gCodeArray = NewVersion ? getGreenRGBArray(dis, 256) : null;

        if (gamma == null || cctCalibrate == null || gammaBy == null) {
            throw new IllegalStateException();
        }

        ColorProofParameter cp = new ColorProofParameter();
        cp.gamma = gamma;
        cp.customGamma = customGamma;
        cp.customCurve = customCurve;
        cp.cctCalibrate = cctCalibrate;
        cp.gammaBy = gammaBy;
        cp.cctAdaptiveStart = CCTStart;
//    cp.cctAdaptiveEnd = CCTEnd;
        cp.turnCode = turnCode;
        cp.grayInterval = grayInterval;
        cp.icBits = getRGBMaxValue(icBits);
        cp.calibrateBits = getRGBMaxValue(calibratedBits);
        cp.keepBlackPoint = keepBlack;
        cp.runCount = runCount;

        cp.gCodeArray = gCodeArray;

        return cp;
    }

    private final static RGB[] getGreenRGBArray(DataInputStream dis, int size) throws
            IOException {
        RGB[] rgbArray = new RGB[size];
        for (int x = 0; x < size; x++) {
            int g = dis.readShort();
            RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, new double[] {0,
                              g, 0}, RGB.MaxValue.Int12Bit);
            rgbArray[x] = rgb;
        }
        return rgbArray;
    }

    private final static boolean NewVersion = true;

    private final static RGB.MaxValue getRGBMaxValue(int bits) {
        switch (bits) {
        case 6:
            return RGB.MaxValue.Int6Bit;
        case 7:
            return RGB.MaxValue.Int7Bit;
        case 8:
            return RGB.MaxValue.Int8Bit;
        case 9:
            return RGB.MaxValue.Int9Bit;
        case 10:
            return RGB.MaxValue.Int10Bit;
        case 11:
            return RGB.MaxValue.Int11Bit;
        case 12:
            return RGB.MaxValue.Int12Bit;
        default:
            return null;
        }
    }

    private MeasureParameter getMeasureParameter(byte[] data) throws
            IOException {
        DataInputStream dis = getDataInputStream(data);
        boolean interpolateReplaceMeasure = dis.readBoolean();
        RGB.MaxValue interpolateUnit = getRGBMaxValue(dis.readByte());
        boolean measureBlankInsert = dis.readBoolean();
        int measureBlankTime = dis.readShort();
        int blankR = dis.readShort();
        int blankG = dis.readShort();
        int blankB = dis.readShort();
        int measureWaitTime = dis.readShort();
        boolean whiteSequenceMeasure = dis.readBoolean();
        int sequenceMeasureCount = dis.readByte();
        boolean inverseMeasure = dis.readBoolean();
        boolean parallelExcute = dis.readBoolean();
        int downloadWaitTime = dis.readShort();

        boolean useDifferenceMeasure = dis.readBoolean();
        int CPCodeAcceptDifference = dis.readByte();

        int bgR = NewVersion ? dis.readShort() : 0;
        int bgG = NewVersion ? dis.readShort() : 0;
        int bgB = NewVersion ? dis.readShort() : 0;

        MeasureParameter mp = new MeasureParameter();
        mp.interpolateReplaceMeasure = interpolateReplaceMeasure;
        mp.interpolateUnit = interpolateUnit;
        mp.measureBlankInsert = measureBlankInsert;
        mp.measureBlankTime = measureBlankTime;
        mp.blankColor = new Color(blankR, blankG, blankB);
        mp.measureWaitTime = measureWaitTime;
        mp.whiteSequenceMeasure = whiteSequenceMeasure;
        mp.sequenceMeasureCount = sequenceMeasureCount;
        mp.inverseMeasure = inverseMeasure;
        mp.parallelExcute = parallelExcute;
        mp.downloadWaitTime = downloadWaitTime;

        mp.useDifferenceMeasure = useDifferenceMeasure;
        mp.CPCodeAcceptDifference = CPCodeAcceptDifference;

        mp.backgroundColor = new Color(bgR, bgG, bgB);

        return mp;
    }

    private AdjustParameter getAdjustParameter(byte[] data) throws
            IOException {
        DataInputStream dis = getDataInputStream(data);

        boolean skip = dis.readBoolean();
        boolean SGC = dis.readBoolean();
        boolean SGCC = dis.readBoolean();
        byte SGBO = dis.readByte();
        boolean WBC = dis.readBoolean();
        boolean GBC = dis.readBoolean();
        byte WCI = dis.readByte();
        byte GCI = dis.readByte();

        boolean LBC = dis.readBoolean();
        byte LCI = dis.readByte();
        boolean QCF = dis.readBoolean();
        boolean CCF = dis.readBoolean();
        int VGSS = dis.readShort();
        int VGSE = dis.readShort();

        boolean LBC2 = dis.readBoolean();
        byte LC2I = dis.readByte();
        byte IM = dis.readByte();

        boolean MR = dis.readBoolean();
        boolean LBR = dis.readBoolean();
        boolean WBR = dis.readBoolean();
        boolean GBR = dis.readBoolean();
        boolean LB2R = dis.readBoolean();

        AdjustParameter ap = new AdjustParameter();
//    ap.smoothGreenCalibrate = SGC;
        ap.smoothGreenCalibrate = false;
        ap.smoothGreenCompromiseCalibrate = SGCC;
        ap.smoothGreenBasedOn = getGreenBased(SGBO);
        ap.whiteBasedCalibrate = WBC;
        ap.greenBasedCalibrate = GBC;
        ap.whiteCalibratedInterval = WCI;
        ap.greenCalibratedInterval = GCI;

        ap.luminanceBasedCalibrate = LBC;
        ap.luminanceCalibratedInterval = LCI;
        ap.quantizationCollapseFix = QCF;
        ap.concernCollapseFixable = CCF;
        ap.variableGammaSmoothStart = VGSS;
        ap.variableGammaSmoothEnd = VGSE;

        ap.luminanceBased2Calibrate = LBC2;
        ap.luminanceCalibrated2Interval = LC2I;

        ap.interpolateMethod = getInterpolateMethod(IM);

        ap.runModelReport = MR;
        ap.runLuminanceBasedReport = LBR;
        ap.runWhiteBasedReport = WBR;
        ap.runGreenBasedReport = GBR;
        ap.runLuminanceBased2Report = LB2R;

        return ap;
    }

    private final static Interpolator.Mode getInterpolateMethod(byte select) {
        switch (select) {
        case 1:
        default:
            return Interpolator.Mode.Linear;
        case 2:
            return Interpolator.Mode.Quadratic;
        case 3:
            return Interpolator.Mode.LinearAtJNDI;
        case 4:
            return Interpolator.Mode.QuadraticAtJNDI;
        }
    }

    private final static AdjustParameter.GreenBased getGreenBased(byte
            select) {
        switch (select) {
        case 1:
            return AdjustParameter.GreenBased.White;
        case 2:
            return AdjustParameter.GreenBased.Model;
        default:
            return null;
        }
    }

    private ViewingParameter getViewingParameter(byte[] data) throws
            IOException {
        DataInputStream dis = getDataInputStream(data);
        double distance = Format.getDouble(dis.readByte(), dis.readByte());
        double size = Format.getDouble(dis.readByte(), dis.readByte());
        int h = dis.readShort();
        int v = dis.readShort();

        ViewingParameter vp = new ViewingParameter();
        vp.distanceInches = distance;
        vp.LCDSize = size;
        vp.resolution = Resolution.getResolution(new Dimension(h, v));
        return vp;
    }

    private Parameter[] getParameterArray(DataInputStream dis) throws
            IOException {
        Logger.log.trace("Start decode parameter.");

        Header whiteHeader = Operator.getHeader(dis);
        byte[] whiteData = getByteData(whiteHeader, dis);
        WhiteParameter wp = getWhiteParameter(whiteData);
        Logger.log.trace("White parameter decode end.");

        Header viewHeader = Operator.getCommandAndSize(dis);
        byte[] viewData = getByteData(viewHeader, dis);
        ViewingParameter vp = getViewingParameter(viewData);
        Logger.log.trace("View parameter decode end.");

        Header cpHeader = Operator.getCommandAndSize(dis);
        byte[] cpData = getByteData(cpHeader, dis);
        ColorProofParameter cp = getColorProofParameter(cpData);
        Logger.log.trace("ColorProos parameter decode end.");

        Header adjustHeader = Operator.getCommandAndSize(dis);
        byte[] adjustData = getByteData(adjustHeader, dis);
        AdjustParameter ap = getAdjustParameter(adjustData);
        Logger.log.trace("Adjust parameter decode end.");

        Header measureHeader = Operator.getCommandAndSize(dis);
        byte[] measureData = getByteData(measureHeader, dis);
        MeasureParameter mp = getMeasureParameter(measureData);
        Logger.log.trace("Measure parameter decode end.");

        Logger.log.trace("All parameters decode end.");
        Parameter[] parameterArray = new Parameter[] {
                                     wp, vp, cp, ap, mp};
        return parameterArray;
    }

    private byte[] getByteData(Header header, DataInputStream dis) throws
            IOException {
        int dataSize = header.dataSize;
        byte[] data = new byte[dataSize];
        dis.read(data);
        return data;
    }

    public interface ParameterCallback {
        public void callback(Parameter[] parameters);
    }


    private ParameterCallback parameterCallback;
    public void setParameterCallback(ParameterCallback callback) {
        this.parameterCallback = callback;
    }

    public boolean loadCode(final RGB[] cpcodeArray, final RGB.MaxValue icBit) {
        return sendCodeAndAck(cpcodeArray, icBit, Command.Load, true);
    }

    public final void waitForDataReady() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ex) {
                Logger.log.debug("", ex);
            }
        }
    }

    private static class Header {
        public Whoami whoami;
        public Command command;
        public int dataSize;
        public Header(Whoami whoami, Command command, int dataSize) {
            this.whoami = whoami;
            this.command = command;
            this.dataSize = dataSize;
        }
    }


    private static class Operator {

        private final static Header getCommandAndSize(DataInputStream dis) throws
                IOException {
            int commandCode = Transformer.readInt(dis);
            int size = Transformer.readInt(dis);

            Command command = Command.geByCode(commandCode);
            Header h = new Header(null, command, size);
            return h;
        }

        private final static Header getHeader(DataInputStream dis) throws
                IOException {
            int whoamiCode = dis.readInt();
            Whoami whoami = Whoami.geByCode(whoamiCode);
            Header h = getCommandAndSize(dis);
            h.whoami = whoami;
            return h;
        }

    }


    private Object lock = new Object();
    public void dataReady() {
//    Logger.log.trace("Data ready.");
        synchronized (lock) {
            lock.notifyAll();
        }

        DataInputStream dis = adapter.getDataInputStream();
        try {
            //==========================================================================
            // 解析header
            //==========================================================================
            Header h = Operator.getHeader(dis);
            dis.reset();

            if (h.command == Command.WhiteParameter && parameterCallback != null) {
                Logger.log.trace(
                        "h.command == Command.WhiteParameter && parameterCallback != null");
                //如果是parameter(唯一主動傳給frontend的), 就解析並且傳給callback
                try {
                    Parameter[] parameterArray = getParameterArray(dis);
                    Logger.log.trace("call callback().");
                    parameterCallback.callback(parameterArray);
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
//          this.sendMessage(ex.toString());
                }
            }
            //==========================================================================
        } catch (IOException ex) {
            Logger.log.debug("", ex);
        }

    }

    private ShareMemoryAdapter adapter;
    private static ShareMemoryConnector instance;

    public final static ShareMemoryConnector getInstance() {
        if (instance == null) {
            instance = new ShareMemoryConnector();
        }
        return instance;
    }

}
