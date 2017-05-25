package auo.mura.exec;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import auo.mura.CorrectionData;
import auo.mura.DeMuraParameter;
import auo.mura.MuraCompensationProducer;
import auo.mura.img.MuraImageUtils;
import jxl.read.biff.BiffException;
import shu.image.ImageUtils;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 以不同參數去執行MuraCompensationProducer
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MonoDMCSimulator {

    public final static void simulate(CorrectionData correctiondata,
                                      String imageFilename, String outputDirname,
                                      MuraCompensationProducer.
                                      DitheringType type, boolean DMC,
                                      boolean DG,
                                      int port) throws
            IOException {
        simulate(correctiondata, imageFilename, outputDirname, type, DMC, true, DG, port);
    }

    private static boolean storeInput = false;
    private static boolean storeOutput = false;

    /**
     *
     * @param correctiondata CorrectionData
     * @param imageFilename String
     * @param outputDirname String
     * @param type DitheringType
     * @param DMC boolean
     * @param ED boolean
     * @param DG boolean
     * @param port int
     * @throws IOException
     */
    public final static void simulate(CorrectionData correctiondata,
                                      String imageFilename, String outputDirname,
                                      MuraCompensationProducer.
                                      DitheringType type, boolean DMC,
                                      boolean ED, boolean DG, int port) throws
            IOException {
        MuraCompensationProducer muracompensationproducer = new
                MuraCompensationProducer(correctiondata);
        muracompensationproducer.setDitheringType(type);
        muracompensationproducer.setDG(DG);

        muracompensationproducer.setStore16BitImage(false);
        muracompensationproducer.setDMC(DMC);
        muracompensationproducer.setED(ED);
        muracompensationproducer.setStore16BitImage(false);

        String image8bitFilename = outputDirname + "/8bit_" + type.name() +
                                   ".tiff";
        String dmcDir = outputDirname + "/dmc";
        new File(dmcDir).mkdir();
        muracompensationproducer.produceCompensationImage(
                imageFilename, dmcDir + "/16bit.tiff", image8bitFilename);

        System.out.println("Dithering Checksum: " +
                           muracompensationproducer.getDitheringCheckSum());

        if (storeInput) {
            String inputDir = outputDirname + "/in";
            new File(inputDir).mkdir();
            muracompensationproducer.store12BitInputImageToHexFormat(inputDir,
                    port);
        }

        if (DMC) {
            muracompensationproducer.store12BitCompensationImageToHexFormat(
                    dmcDir, port);
        }
        if (DG) {
            String dgDir = outputDirname + "/dmc_dg";
            new File(dgDir).mkdir();
            muracompensationproducer.store12BitCompensationImageDGToHexFormat(
                    dgDir, port);
        }

        if (ED) {
            String edDir = outputDirname + "/dmc_ed";

            new File(edDir).mkdir();
            muracompensationproducer.storeEDImageToHexFormat(edDir, port);

        }

    }

    public final static void simulate(CorrectionData correctiondata,
                                      short[][][] image10Bit,
                                      String outputDirname,
                                      MuraCompensationProducer.
                                      DitheringType type, boolean DMC,
                                      boolean DG,
                                      boolean ED, int edbit, int port
            ) throws
            IOException {
        MuraCompensationProducer muracompensationproducer = new
                MuraCompensationProducer(correctiondata);
        muracompensationproducer.setDitheringType(type);
        muracompensationproducer.setDG(DG);
        muracompensationproducer.setStore16BitImage(false);
        muracompensationproducer.setDMC(DMC);
        muracompensationproducer.setED(ED);
        muracompensationproducer.setEDBit(edbit);

        String image8bitFilename = outputDirname + "/8bit_" + type.name() +
                                   ".tiff";
        int h = image10Bit[0].length;
        int w = image10Bit[0][0].length;
        muracompensationproducer.setImageResolution(w, h);
        String dmcDir = outputDirname + "/dmc";
        new File(dmcDir).mkdir();
        muracompensationproducer.produceCompensationImage(image10Bit,
                dmcDir + "/16bit.tiff", image8bitFilename);
        System.out.println("Compensation Checksum: " +
                           muracompensationproducer.getCompensationDataCheckSum());
        System.out.println("Dithering Checksum: " +
                           muracompensationproducer.getDitheringCheckSum());
        int internalPort = (8 == port) ? port : 4;

        if (storeInput) {
            String inputDir = outputDirname + "/in";
            new File(inputDir).mkdir();
            muracompensationproducer.store12BitInputImageToHexFormat(inputDir,
                    internalPort);
        }

        if (storeOutput) {
            if (DMC) {
                muracompensationproducer.store12BitCompensationImageToHexFormat(
                        dmcDir, internalPort);
            }
            if (DG) {
                String dgDir = outputDirname + "/dmc_dg";
                new File(dgDir).mkdir();
                muracompensationproducer.store12BitCompensationImageDGToHexFormat(
                        dgDir, internalPort);
            }

            if (ED) {
                String edDir = outputDirname + "/dmc_ed";

                new File(edDir).mkdir();
                muracompensationproducer.storeEDImageToHexFormat(edDir, port);

            }
        }
    }

    /**
     *
     * @throws IOException
     * @throws BiffException
     * @deprecated
     */
    static void run() throws IOException, BiffException {
        String inputdir =
//                "Y:/Verify Items/Verify LUT/Special Case/No.3 real_8x8 - 2";
                "DMC/No1/LUT/";
        String filename =
                "DMC/GrayLevel25.bmp";

        String parameterFilename =
                inputdir + "/par.csv";
        String correctFilename =
                inputdir + "/lut.csv";

        DeMuraParameter parameter = new DeMuraParameter(parameterFilename,
                DeMuraParameter.Version.v0);
        CorrectionData correctiondata = new CorrectionData(correctFilename,
                parameter);
        String outputdirname =
                "DMC/No1/";

        simulate(correctiondata, filename, outputdirname,
                 MuraCompensationProducer.DitheringType.HardwareThresholdModulation, true, false, 4
                );
    }

    static void run(String sourcedirname, DeMuraParameter.Version parVersion,
                    CorrectionData.Type lutType, MuraCompensationProducer.DitheringType dithering,
                    String patternFilename) throws IOException,
            BiffException {

        String parameterFilename =
                sourcedirname + "/par.csv";
        String correctFilename =
                sourcedirname + "/lut.csv";

        DeMuraParameter parameter = new DeMuraParameter(parameterFilename, parVersion);
        CorrectionData correctiondata = new CorrectionData(correctFilename,
                parameter, lutType);

        simulate(correctiondata, sourcedirname + "/" + patternFilename, sourcedirname, dithering, true, false, 4
                );
    }


    public static void run(String inputdir, String outputdir,
                           String patternFilename, boolean DG) throws
            IOException {

        String parameterFilename =
                inputdir + "/par.csv";
        String correctFilename =
                inputdir + "/lut.csv";

        DeMuraParameter parameter = new DeMuraParameter(parameterFilename);
        CorrectionData correctiondata = new CorrectionData(correctFilename,
                parameter);

        simulate(correctiondata, patternFilename, outputdir,
                 MuraCompensationProducer.DitheringType.Hardware_2_, true, DG, 4
                );

    }

    /**
     * mono可執行兩種模擬
     * 1. 均一灰階pattern
     * 2. 外部載入image
     *
     * @param args String[]
     * @throws BiffException
     * @throws IOException
     */
    public static void main(String[] args) throws BiffException, IOException {
        boolean fromUnifromPattern = false;
        if (fromUnifromPattern) {
            String inputdir = "DMC Example/mono/uniform pattern/";
            int width = 1920;
            int height = 1080;
            for (int x = 0; x <= 255; x++) {
                run(x, inputdir, DeMuraParameter.Version.v1, CorrectionData.Type.AUOHex,
                    MuraCompensationProducer.DitheringType.HardwareThresholdModulation,
                    width, height);
            }

        }

        else {
            String inputdir = "DMC Example/mono/image pattern/";
            String imgfilename = "GrayLevel25.bmp";
            run(inputdir, DeMuraParameter.Version.v0, CorrectionData.Type.AUOHex,
                MuraCompensationProducer.DitheringType.HardwareThresholdModulation, imgfilename);

        }

    }

    public static void run(int grayLevel, String sourcedirname, DeMuraParameter.Version parVersion,
                           CorrectionData.Type lutType, MuraCompensationProducer.DitheringType dithering,
                           int patternWidth, int patternHeight) throws IOException,
            BiffException {

        DeMuraParameter parameter = new DeMuraParameter(
                sourcedirname + "/par.csv", parVersion);
        String correctFilename = sourcedirname + "lut.csv";

        CorrectionData correctiondata = new CorrectionData(correctFilename,
                parameter, lutType);

        //建一個目錄存pattern, 避免每次模擬都重建pattern
        String patterndirname = sourcedirname + "/pattern/";
        if (!new File(patterndirname).exists()) {
            new File(patterndirname).mkdir();
        }
        String filename = patterndirname + "/grayLevel" + grayLevel + ".bmp";
        //pattern檔案若不存在, 就需要產生好pattern 並且存起來
        boolean produceImage = !new File(filename).exists();
        if (produceImage) {
            MuraImageUtils utils = new MuraImageUtils(8, patternWidth, patternHeight);
            short[][][] data = utils.getPlaneImageData(grayLevel, grayLevel,
                    grayLevel);
            BufferedImage image = utils.getBufferedImage(data);
            ImageUtils.storeBMPImage(filename, image);
        }

        String outputdirname = sourcedirname + "/grayLevel_" + grayLevel + "/";
        File dir = new File(outputdirname);
        if (!dir.exists()) {
            dir.mkdir();
        }

        simulate(correctiondata, filename, outputdirname, dithering, true, true, false, 4);

    }


}
