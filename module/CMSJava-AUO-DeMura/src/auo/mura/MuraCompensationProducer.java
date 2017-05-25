package auo.mura;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import auo.cms.ed.AUOErrorDiffusion;
//import auo.cms.frc.impl.FRCPattern;
import auo.math.lut.CubeTable;
import auo.mura.img.MuraImageUtils;
import auo.mura.interp.Interpolation3DIF;
import auo.mura.interp.ThreeDInterpolation;
import auo.mura.util.ArrayUtils;
import auo.mura.util.DeMuraDebugger;
import auo.mura.util.DigitalGamma;
import shu.cms.colorspace.depend.RGBBase;
import shu.image.ImageUtils;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MuraCompensationProducer {

    public MuraCompensationProducer(CorrectionData correctionData) {
        this.correctionData = correctionData;
        muraImageUtils = new MuraImageUtils(dataBit, imageWidth, imageHeight);
    }

    public void setImageResolution(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
        muraImageUtils = new MuraImageUtils(dataBit, imageWidth, imageHeight);
    }

    public void setBound(short lowerBound, short upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }


    public void setDataBit(int dataBit) {
        this.dataBit = dataBit;
    }

    public enum DitheringType {
        FloydSteinbergIdeal, Hardware_2_, HardwareThresholdModulation;
    }


    public void setDitheringType(DitheringType ditheringType) {
        this.ditheringType = ditheringType;
    }

    public void setStore16BitImage(boolean store16BitImage) {
        this.store16BitImage = store16BitImage;
    }

    public void setInterpolation14Bit(boolean interpolation14Bit) {
        this.interpolation14Bit = interpolation14Bit;
    }

    public void setEDBit(int edbit) {
        this.edbit = edbit;
    }

    public void setED(boolean ED) {
        this.ED = ED;
    }

//    public void setFRC(boolean FRC) {
//        this.FRC = FRC;
//    }

    public void setDMC(boolean DMC) {
        this.DMC = DMC;
    }

    public void setDG(boolean dg) {
        this.DG = dg;
    }

    public boolean isDG() {
        return DG;
    }

    public final long getCompensationDataCheckSum() {
        return compensationDataCheckSum;
    }

    public MuraImageUtils getMuraImageUtils() {
        return muraImageUtils;
    }

    public long getDitheringCheckSum() {
        return ditheringCheckSum;
    }

    //=====================================================================================================================
    // basic info
    //=====================================================================================================================
    private short lowerBound = 0;
    private short upperBound = 1023;
    int imageWidth = 1920;
    int imageHeight = 1080;
    private int dataBit = 12;
    private int inputBit = 10;
    private CubeTable cubeTable;
    private CorrectionData correctionData;
    ThreeDInterpolation threeDInterpolation;
    //=====================================================================================================================

    //=====================================================================================================================
    // flags for function
    //=====================================================================================================================
//    private final boolean use3DInterpolation = true;
    private boolean multiThreadED = true;
    public static boolean SingleChannelProcess = false;

    private boolean DG = false;
    private boolean DMC = true;
//    private boolean FRC = false;
    private boolean ED = false;
//    private FRCPattern frc;

    private int edbit = 4;
    private DitheringType ditheringType = DitheringType.Hardware_2_;

    private boolean interpolation14Bit = false;
    //=====================================================================================================================

    //=====================================================================================================================
    // image data
    //=====================================================================================================================
    private short[][][] inputImage10BitData;
    private short[][][] compensationImage12Bit;
    private short[][][] compensationImage12Bit_DG;
    private short[][][] edImage8Bit;
    private short[][][] edImage10Bit;
    private short[][][] outputEDImage10Bit;
    private short[][][][] frcImage8Bit;
    private short[][][][] frcImage10Bit;
    //=====================================================================================================================

    //=====================================================================================================================
    // for check usage
    //=====================================================================================================================
    private boolean store16BitImage = false;
    private boolean storeDeMuraData = false;

    private Object compensationDataCheckSumLock = new Object();
    private long compensationDataCheckSum;
    private Object ditheringCheckSumLock = new Object();
    private long ditheringCheckSum;

    private short[][] deMuraData;
    public static boolean HWCheck = false;
    public static int HWCheck_WP;
    public static int HWCheck_GrayLevel;
    //=====================================================================================================================

    public MuraImageUtils muraImageUtils;

    private void init3DInterpolation() {
        threeDInterpolation = new ThreeDInterpolation(cubeTable, correctionData.getDeMuraParameter());
        Interpolation3DIF triLinearInterpolation3D = null;
        if (interpolation14Bit) {
            boolean ks22 = false;
            triLinearInterpolation3D = threeDInterpolation.new TriLinearInterpolation3D_14Bit(ks22);
        } else {
            triLinearInterpolation3D = threeDInterpolation.new YagiTriLinearInterpolation3D();
        }
        threeDInterpolation.registerInterpolation3DIF(triLinearInterpolation3D);
    }


    protected final short[][][] getCompensationData(final short[][][] imageData) {
        if (null == cubeTable) {

            cubeTable = correctionData.getCubeTable(lowerBound, upperBound,
                    imageWidth, imageHeight, 0, inputBit, dataBit);

        }

        //==========================================================================
        //內插預備
        //==========================================================================
        compensationDataCheckSum = 0;
        init3DInterpolation();

        //==========================================================================

        DeMuraDebugger debug = null;
        if (storeDeMuraData) {
            debug = new DeMuraDebugger("dmc/deMura12bit.xls");
        }

        if (SingleChannelProcess) {
            getCompensationData0(imageData[0], threeDInterpolation, RGBBase.Channel.G);

            ArrayUtils.copy(imageData[0], imageData[1]);
            ArrayUtils.copy(imageData[0], imageData[2]);

        } else {
            //多執行緒加速
            if (multiThreadED) {
                ExecutorService pool = Executors.newFixedThreadPool(16);

                for (final RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
                    for (int part = 0; part < 4; part++) {
                        final int partindex = part;
                        Thread thread1 = new Thread() {
                            public void run() {
                                getCompensationData0(imageData[ch.getArrayIndex()], threeDInterpolation, ch, 4,
                                        partindex);
                            }
                        };
                        pool.submit(thread1);

                    }
                }

                pool.shutdown();
                try {
                    pool.awaitTermination(80, TimeUnit.SECONDS);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                System.out.println("multithread down");
                System.gc();
            } else {
                getCompensationData0(imageData[0], threeDInterpolation, RGBBase.Channel.R);
                int row = storeDeMuraData ? debug.storeDeMuraData(0, deMuraData) :
                          0;

                getCompensationData0(imageData[1], threeDInterpolation, RGBBase.Channel.G);
                row = storeDeMuraData ? debug.storeDeMuraData(row, deMuraData) :
                      0;

                getCompensationData0(imageData[2], threeDInterpolation, RGBBase.Channel.B);
                row = storeDeMuraData ? debug.storeDeMuraData(row, deMuraData) :
                      0;
            }

            if (storeDeMuraData) {
                debug.close();
            }

        }

        return imageData;
    }


    public final void store12BitInputImageToHexFormat(String dirname,
            int port) throws
            IOException {
        short[][][] inputImage12BitData = ArrayUtils.copy(inputImage10BitData);
        ArrayUtils.convert10BitTo12Bit(inputImage12BitData);
        MuraImageUtils.storeImageToHexFormat(inputImage12BitData, dirname, "",
                                             port);
    }


    public final void store12BitCompensationImageToHexFormat(String dirname,
            int port) throws
            IOException {
        MuraImageUtils.storeImageToHexFormat(compensationImage12Bit, dirname,
                                             "",
                                             port);
    }

    public final void store12BitCompensationImageDGToHexFormat(String dirname,
            int port) throws
            IOException {
        MuraImageUtils.storeImageToHexFormat(compensationImage12Bit_DG, dirname,
                                             "dg_", port);
    }

    public final void store10BitCompensationImageFRCToHexFormat(String dirname,
            int port) throws
            IOException {
        for (int x = 0; x < 4; x++) {
            MuraImageUtils.storeImageToHexFormat(frcImage10Bit[x], dirname,
                                                 "frc" + (x + 1) + "_", port);
        }

    }

    public final void store8BitCompensationImageFRCToHexFormat(String dirname,
            int port) throws
            IOException {
        for (int x = 0; x < 4; x++) {
            MuraImageUtils.storeImageToHexFormat(frcImage8Bit[x], dirname,
                                                 "frc" + (x + 1) + "_", port);
        }

    }


    public final void storeEDImageToHexFormat(String dirname, int port) throws
            IOException {
        if (null == edImage8Bit && null == edImage10Bit) {
            throw new IllegalStateException("");
        }
        if (null == outputEDImage10Bit) {
            //雖然dithering結果為8bit, 但是最終輸出為10bit, 因此需要升轉到10bit再儲存
            if (null != edImage8Bit) {
                outputEDImage10Bit = ArrayUtils.copy(edImage8Bit);
                ArrayUtils.convert8BitTo10Bit(outputEDImage10Bit);
                ArrayUtils.convert10BitTo12Bit(outputEDImage10Bit);
            } else if (null != edImage10Bit) {
                outputEDImage10Bit = ArrayUtils.copy(edImage10Bit);
            }

        }
        MuraImageUtils.storeImageToHexFormat(outputEDImage10Bit, dirname,
                                             "ed_", port);
    }

    public final void produceCompensationImage(String inputImageFilename,
                                               String image16bitFilename,
                                               String image8bitFilename) throws
            IOException {

        BufferedImage image = ImageUtils.loadImage(inputImageFilename);
        if (null == image) {
            image = ImageUtils.loadImageByJAI(inputImageFilename);
        }
        int w = image.getWidth();
        int h = image.getHeight();
        this.setImageResolution(w, h);

        short[][][] inputImage10BitData = muraImageUtils.get10BitImageData(image, false);

        produceCompensationImage(inputImage10BitData, image16bitFilename,
                                 image8bitFilename);
    }

    public short[][][] getCompensationImage12Bit() {
        return compensationImage12Bit;
    }

    public short[][][] getEDImage8Bit() {
        return edImage8Bit;
    }


    public final BufferedImage getCompensationImage(int rGrayLevel,
            int gGrayLevel,
            int bGrayLevel) {
        short[][][] compensationData = getCompensationData(rGrayLevel,
                gGrayLevel,
                bGrayLevel);
        produceEDCompensationData(compensationData[0]);
        produceEDCompensationData(compensationData[1]);
        produceEDCompensationData(compensationData[2]);
        return muraImageUtils.getBufferedImage(compensationData);
    }

    public final short[][][] getCompensationData(int rGrayLevel, int gGrayLevel,
                                                 int bGrayLevel) {

        short[][][] imageData = muraImageUtils.getPlaneImageData(rGrayLevel,
                gGrayLevel,
                bGrayLevel);
        return getCompensationData(imageData);

    }


    /**
     *
     * @param imageData short[][]
     * @param interp ThreeDInterpolation
     * @param ch Channel
     * @return short[][]
     */
    protected final short[][] getCompensationData0(short[][] imageData,
            ThreeDInterpolation interp, RGBBase.Channel ch) {
        return getCompensationData0(imageData, interp, ch, 1, 0);

    }


    protected final short[][] getCompensationData0(short[][] imageData,
            ThreeDInterpolation interp, RGBBase.Channel ch, int part, int processPart) {

        //==========================================================================
        //變數準備
        //==========================================================================
        int blockHCount = correctionData.getBlockHCount();
        int blockWCount = correctionData.getBlockWCount();
        int blockH = (int) Math.ceil((double) imageHeight / (blockHCount - 1));
        int blockW = imageWidth / (blockWCount - 1);
        //==========================================================================
        deMuraData = new short[imageHeight][imageWidth];

        int partHCount = (int) Math.round((double) blockHCount / part);
        int hstart = partHCount * processPart;
        int hend = hstart + partHCount;
        hend = hend > blockHCount ? blockHCount : hend;
        long checksum = 0;

        for (int h = hstart; h < hend; h++) {
            for (int w = 0; w < blockWCount; w++) {

                for (int hp = 0; hp < blockH; hp++) {
                    for (int wp = 0; wp < blockW; wp++) {

                        short hp_ = (short) (hp + h * blockH);
                        short wp_ = (short) (wp + w * blockW);

                        if (hp_ >= imageHeight || wp_ >= imageWidth) {
                            continue;
                        }
                        short grayLevel = imageData[hp_][wp_];

                        short[] v = interp.getValues(new short[] {wp_, hp_,
                                grayLevel}, ch);
                        short interpValue = v[0];
                        deMuraData[hp_][wp_] = interpValue;

                        short integerValue = (short) (grayLevel * 4 + interpValue);
                        integerValue = (integerValue < 0) ? 0 : integerValue;
                        integerValue = (integerValue > 4095) ? 4095 :
                                       integerValue;
                        checksum += integerValue;

                        imageData[hp_][wp_] = integerValue;
                    }
                }

            }
        }
        synchronized (compensationDataCheckSumLock) {
            compensationDataCheckSum += checksum;
        }

        return imageData;
    }

    public void produceEDCompensationData(short[][] compensationData) {
        AUOErrorDiffusion ed = new AUOErrorDiffusion();
        switch (ditheringType) {

        case FloydSteinbergIdeal:
            ed.setMatrix(AUOErrorDiffusion.Matrix.FloydSteinbergIdeal);
            break;
        case Hardware_2_:
            ed.setMatrix(AUOErrorDiffusion.Matrix.Hardware_2_);
            break;
        case HardwareThresholdModulation:
            ed.setMatrix(AUOErrorDiffusion.Matrix.HardwareThresholdModulation);
            break;

        }
        short[][] result = null;
        if (2 == edbit) {
            result = ed.pixelBaseED10bit(compensationData);
        } else if (4 == edbit) {
            result = ed.pixelBaseED8bit(compensationData);
        }

        long checksum = 0;
        for (int h = 0; h < result.length; h++) {
            for (int w = 0; w < result[0].length; w++) {
                checksum += result[h][w]; //checksum
                compensationData[h][w] = result[h][w]; //copy
            }
        }
        synchronized (ditheringCheckSumLock) {
            ditheringCheckSum += checksum;
        }
    }

    public final void produceCompensationImage(final int rGrayLevel,
                                               final int gGrayLevel,
                                               final int bGrayLevel,
                                               String image16bitFilename,
                                               String image8bitFilename) throws
            FileNotFoundException, IOException {
        if (rGrayLevel == gGrayLevel && gGrayLevel == bGrayLevel) {
            SingleChannelProcess = true;
        } else {
            SingleChannelProcess = false;
        }

        short[][][] imageData = muraImageUtils.getPlaneImageData(rGrayLevel,
                gGrayLevel, bGrayLevel);

        produceCompensationImage(imageData, image16bitFilename,
                                 image8bitFilename);

    }


    private void produceDitheringImage(String image8bitFilename,
                                       final short[][][] image, boolean store) throws
            IOException {
        ditheringCheckSum = 0;
        if (SingleChannelProcess) {
            //12bit dithering成8bit
            produceEDCompensationData(image[0]);
            ArrayUtils.copy(image[0], image[1]);
            ArrayUtils.copy(image[0], image[2]);
        } else {
            if (!multiThreadED || DitheringType.FloydSteinbergIdeal == ditheringType) {
                produceEDCompensationData(image[0]);
                produceEDCompensationData(image[1]);
                produceEDCompensationData(image[2]);
            } else {
                ExecutorService pool = Executors.newFixedThreadPool(4);

                for (final RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
                    Thread thread = new Thread() {
                        public void run() {
                            produceEDCompensationData(image[ch.getArrayIndex()]);
                        }
                    };
                    pool.submit(thread);
                }

                pool.shutdown();
                try {
                    pool.awaitTermination(30, TimeUnit.SECONDS);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }
        if (store) {
            //存8bit
            muraImageUtils.store8BitImageBMP(image,
                                             image8bitFilename);
        }
    }


//    private short[][][][] produceFRCImage(short[][][] compensationImage12Bit) {
//        if (null == frc) {
//            try {
//                frc = new FRCPattern("frc/auofrc.csv" );
////                frc = new AUOFRC("frc/auofrc16.csv", AUOFRC.PatternCount.FRC16);
//            } catch (FileNotFoundException ex) {
//                ex.printStackTrace();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        boolean subPixelBase = true;
//
//        short[][][][] result = null;
//        if (subPixelBase) {
//            result = frc.frc8bit(compensationImage12Bit,
//                                 PatternType.SubPixelBase);
//        } else {
//            result = frc.frc8bit(compensationImage12Bit,
//                                 PatternType.PixelBase);
//        }
//        return result;
//    }


    private void produceDG(short[][][] image12BitData, DigitalGamma dg) {
        int height = image12BitData[0].length;
        int width = image12BitData[0][0].length;
        short r, g, b;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {

                r = image12BitData[0][h][w];
                g = image12BitData[1][h][w];
                b = image12BitData[2][h][w];

                r = (short) dg.gammaR12Bit(r);
                g = (short) dg.gammaG12Bit(g);
                b = (short) dg.gammaB12Bit(b);
                if (0 == w) {
                    int a = 1;
                }
                image12BitData[0][h][w] = r;
                image12BitData[1][h][w] = g;
                image12BitData[2][h][w] = b;
            }
        }

    }


    public final void produceCompensationImage(short[][][] inputImage10BitData,
                                               String image16bitFilename,
                                               String image8bitFilename) throws
            FileNotFoundException, IOException {
        this.inputImage10BitData = inputImage10BitData;

        if (DMC) {
            compensationImage12Bit = getCompensationData(ArrayUtils.copy(
                    inputImage10BitData));
        } else {
            compensationImage12Bit = ArrayUtils.copy(inputImage10BitData);
            ArrayUtils.convert10BitTo12Bit(compensationImage12Bit);
        }

        if (store16BitImage) {
            //store 16bit
            int[][][] compensationData16Bit = ArrayUtils.copyToInt(
                    compensationImage12Bit);
            ArrayUtils.convert12BitTo16Bit(compensationData16Bit);
            //把12bit存成16bit
            muraImageUtils.store16BitImageTiff(compensationData16Bit,
                                               image16bitFilename);
        }

        if (DG) {
            compensationImage12Bit_DG = ArrayUtils.copy(compensationImage12Bit);
            DigitalGamma dg = new DigitalGamma(
                    "Y:/Verify Items/Simulation Result/(0011) - 2Vertical/ED Check/dg.txt");
            produceDG(compensationImage12Bit_DG, dg);
        }

//        if (FRC) {
//            //frame, ch, h, w
//            if (null != compensationImage12Bit_DG) {
//                frcImage8Bit = produceFRCImage(compensationImage12Bit_DG);
//            } else {
//                frcImage8Bit = produceFRCImage(compensationImage12Bit);
//            }
//
//            frcImage10Bit = new short[4][][][];
//            for (int frame = 0; frame < 4; frame++) {
//                frcImage10Bit[frame] = ArrayUtils.copy(frcImage8Bit[frame]);
//                ArrayUtils.convert8BitTo10Bit(frcImage10Bit[frame]);
//            }
//        }

        if (ED) {
            short[][][] sourceImage = null;
            if (null != compensationImage12Bit_DG) {
                sourceImage = compensationImage12Bit_DG;
            } else {
                sourceImage = compensationImage12Bit;
            }
            if (4 == edbit) {
                System.gc();
                edImage8Bit = ArrayUtils.copy(sourceImage);
                produceDitheringImage(image8bitFilename, sourceImage, true);

            } else if (2 == edbit) {
                edImage10Bit = ArrayUtils.copy(sourceImage);
                produceDitheringImage(image8bitFilename, edImage10Bit, true);
            }

        }
    }


}
