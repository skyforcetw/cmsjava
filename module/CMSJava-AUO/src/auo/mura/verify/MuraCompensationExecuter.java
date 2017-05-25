package auo.mura.verify;

import java.io.IOException;
import shu.image.ImageUtils;
import jxl.read.biff.BiffException;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.File;
import auo.mura.*;

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
public class MuraCompensationExecuter {

  public final static void execute(CorrectionData correctiondata,
                                   String imageFilename, String outputDirname,
                                   MuraCompensationProducer.
                                   DitheringType type, boolean DG, boolean FRC,
                                   int port) throws
      IOException {
    MuraCompensationProducer muracompensationproducer = new
        MuraCompensationProducer(correctiondata);
    muracompensationproducer.setDitheringType(type);
    muracompensationproducer.setDG(DG);
    muracompensationproducer.setStore16BitImage(true);
    muracompensationproducer.setStore8BitDitheringImage(false);

    String image8bitFilename = outputDirname + "/8bit_" + type.name() + ".tiff";
    String dmcDir = outputDirname + "/dmc";
    new File(dmcDir).mkdir();
    muracompensationproducer.produceCompensationImage(imageFilename,
        dmcDir + "/16bit.tiff", image8bitFilename);
    System.out.println("Dithering Checksum: " +
                       muracompensationproducer.getDitheringCheckSum());

    muracompensationproducer.store12BitCompensationImageToHexFormat(
        dmcDir, 4);
    if (DG) {
      String dgDir = outputDirname + "/dmc_dg";
      new File(dgDir).mkdir();
      muracompensationproducer.store12BitCompensationImageDGToHexFormat(
          dgDir, 4);
    }
    if (FRC) {
      String frcDir = outputDirname + "/dmc_frc";
      new File(frcDir).mkdir();
      muracompensationproducer.store10BitCompensationImageFRCToHexFormat(
          frcDir, port);
    }

  }

  public final static void execute(CorrectionData correctiondata,
                                   short[][][] image10Bit, String outputDirname,
                                   MuraCompensationProducer.
                                   DitheringType type, boolean DG, boolean FRC,
                                   boolean DMC, int port) throws
      IOException {
    MuraCompensationProducer muracompensationproducer = new
        MuraCompensationProducer(correctiondata);
    muracompensationproducer.setDitheringType(type);
    muracompensationproducer.setDG(DG);
    muracompensationproducer.setStore16BitImage(false);
    muracompensationproducer.setStore8BitDitheringImage(false);
    muracompensationproducer.setStore12BitFullFrame(false);
    muracompensationproducer.setDMC(DMC);
    //    muracompensationproducer.setStore8BitFullFrame(false);

    String image8bitFilename = outputDirname + "/8bit_" + type.name() + ".tiff";
    int h = image10Bit[0].length;
    int w = image10Bit[0][0].length;
    muracompensationproducer.setImageResolution(w, h);
    String dmcDir = outputDirname + "/dmc";
    new File(dmcDir).mkdir();
    muracompensationproducer.produceCompensationImage(image10Bit,
        dmcDir + "/16bit.tiff", image8bitFilename);
    System.out.println("Dithering Checksum: " +
                       muracompensationproducer.getDitheringCheckSum());

    muracompensationproducer.store12BitCompensationImageToHexFormat(
        dmcDir, 4);
    if (DG) {
      String dgDir = outputDirname + "/dmc_dg";
      new File(dgDir).mkdir();
      muracompensationproducer.store12BitCompensationImageDGToHexFormat(
          dgDir, 4);
    }
    if (FRC) {
      String frcDir = outputDirname + "/dmc_frc";
      new File(frcDir).mkdir();
      muracompensationproducer.store10BitCompensationImageFRCToHexFormat(
          frcDir, port);
    }

  }

  static void simulate() throws IOException {
//    String inputdir = "\\\\labd3c-e7f-021/DeMura/Verify Items/Verify LUT/Limit Case/1920x1080/limit2_8 8(X0)";
//    String inputdir = "\\\\labd3c-e7f-021/DeMura/Verify Items/Verify LUT/Limit Case/1920x1080/limit3_8 8(X0)";
    String inputdir =
        "Y:/Verify Items/Verify LUT/Special Case/No.3 real_8x8 - 2";
    String filename =
//        "demura sim/Image/limit pattern/pattern_11level_1920x1080.bmp";
        "demura sim/Image/limit pattern/pattern_256level_1920x1080.bmp";
//        "demura sim/Image/limit pattern/pattern_2vertical1920x1080.bmp";

    String parameterFilename =
//        "\\\\labd3c-e7f-021/DeMura/Verify Items/Verify LUT/Real Case/No.1/1920x1080/real_8x8/par.csv";
        inputdir + "/par.csv";
    String correctFilename =
        inputdir + "/lut.csv";

    DeMuraParameter parameter = new DeMuraParameter(parameterFilename);
    CorrectionData correctiondata = new CorrectionData(correctFilename,
        parameter);
    String outputdirname =
//        "D:/軟體/nobody zone/Mura/Verify Simulation Experiment/";
        "Y:/Verify Items/Verify LUT/Special Case/No.3 real_8x8 - 2";

    execute(correctiondata, filename, outputdirname,
            MuraCompensationProducer.DitheringType.
            FloydSteinberg_Wiki_LineBased, false, false, 4);
//    execute(correctiondata, filename, outputdirname,
//            MuraCompensationProducer.DitheringType.FloydSteinberg);
  }

  public static void simulate(String inputdir, String outputdir,
                              String patternFilename, boolean DG) throws
      IOException {

    String parameterFilename =
        inputdir + "/par.csv";
    String correctFilename =
        inputdir + "/lut.csv";

    DeMuraParameter parameter = new DeMuraParameter(parameterFilename);
    CorrectionData correctiondata = new CorrectionData(correctFilename,
        parameter);
//    String outputdirname =
//        "D:/軟體/nobody zone/Mura/Verify Simulation Experiment/";

    execute(correctiondata, patternFilename, outputdir,
            MuraCompensationProducer.DitheringType.
            FloydSteinberg_Wiki_LineBased, DG, false, 4);

  }

  public static void main(String[] args) throws BiffException, IOException {
    Dithering.Debug = false;
    MuraCompensationProducer.SingleChannelProcess = false;
    simulate();
//    for (int x = 1; x <= 26; x++) {
//      main(x);
//    }
//    main(2);
  }

  public static void main(int grayLevel) throws IOException, BiffException {

//    String correctFilename = "24inch No2/MuraData Mode1 Type4.csv";
//    CorrectionData correctiondata = new CorrectionData(correctFilename,
//        CorrectionData.Type.Integer1);

//    DeMuraParameter parameter = new DeMuraParameter(
//        "demura sim/0527_NO8/par_NO8.csv");
//    String correctFilename = "demura sim/0527_NO8/LUT_NO8.csv";
    DeMuraParameter parameter = new DeMuraParameter(
//        "demura sim/Panel 50''/No1/LUT/par.csv");
//        "d:/軟體/nobody zone/Mura/Dithering Experiment/par.csv");
        "\\\\auo/RD/Platform/EE/TCON/DeMura/Simulation Result/AUO Simulation Data/0530_NO3/par_NO3.csv");

//    String correctFilename = "demura sim/Panel 50''/No1/LUT/lut.csv";
    String correctFilename =
        "\\\\auo/RD/Platform/EE/TCON/DeMura/Simulation Result/AUO Simulation Data/0530_NO3/LUT_NO3.csv";
//        "d:/軟體/nobody zone/Mura/Dithering Experiment/lut.csv";

    CorrectionData correctiondata = new CorrectionData(correctFilename,
        parameter);

//    String cremoFlename =
//        "D:/軟體/nobody zone/Mura/Dithering Experiment/20130603--0001(1)_data(final).csv";
//        "Y:/Verify Items/Verify LUT/Limit Case/1920_1080/limit_2x32(X2)/limit_2x32(X2).csv";
//    correctiondata = new CorrectionData(cremoFlename);

//    CorrectionData correctiondata = new CorrectionData(
//        "demura sim/Panel 50''/No1/LUT/20130603--0001(1)_data(final).csv");

//    correctiondata.storeToFlashFormat("demura/flash.hex", 256, 192);
//    int grayLevel = 2;
    String filename = "demura sim/Image/grayLevel" + grayLevel + ".bmp";
    boolean produceImage = !new File(filename).exists();
    if (produceImage) {
      MuraImageUtils utils = new MuraImageUtils(8, 1920, 1080);
      short[][][] data = utils.getPlaneImageData(grayLevel, grayLevel,
                                                 grayLevel);
      BufferedImage image = utils.getBufferedImage(data);
      ImageUtils.storeBMPImage(filename, image);
    }

    String outputdirname =
        "D:/軟體/nobody zone/Mura/Dithering Experiment/grayLevel_" + grayLevel;
    File dir = new File(outputdirname);
    if (!dir.exists()) {
      dir.mkdir();
    }

    execute(correctiondata, filename, outputdirname,
            MuraCompensationProducer.DitheringType.Yagi, false, false, 4);
    execute(correctiondata, filename, outputdirname,
            MuraCompensationProducer.DitheringType.
            FloydSteinberg_Wiki_LineBased, false, false, 4);

//    execute(correctiondata, filename, outputdirname,
//            MuraCompensationProducer.DitheringType.FloydSteinberg_Wiki);
//    execute(correctiondata, filename,
//            MuraCompensationProducer.DitheringType.FloydSteinberg_Wiki_Order);

//    execute(correctiondata, "demura sim/mouse/mouse2.bmp");

//    BufferedImage img16 = ImageUtils.loadImageByJAI(
//        "D:/軟體/nobody zone/Mura/Dithering Experiment/grayLevel_2/16bit.tiff");
//    BufferedImage img8 = ImageUtils.loadImageByJAI(
////        "D:/軟體/nobody zone/Mura/Dithering Experiment/grayLevel_2/8bit.bmp");
//        "8bit.bmp");
//    float[] f16 = new float[3];
//    float[] f8 = new float[3];
//    for (int x = 0; x < 1920; x++) {
//      for (int y = 0; y < 1080; y++) {
//        img16.getRaster().getPixel(x, y, f16);
//        img8.getRaster().getPixel(x, y, f8);
//        if (f16[0] / 256 >= 2) {
//          System.out.println(f16[0] / 256 + " " + f8[0]);
//        }
//      }
//    }

  }

  public static void graylevelTest(int grayLevel10Bit) throws BiffException,
      IOException {
//    String filename =
//        "24inch No2/correctiondata 121x271 No2 20130227--0010(1)_data(final).csv";
    String filename = "24inch No2/Sample CorrectionData(12bit).csv";
    CorrectionData correctiondata = new CorrectionData(filename,
        CorrectionData.Type.Integer2);

    MuraCompensationProducer muracompensationproducer = new
        MuraCompensationProducer(correctiondata);

    muracompensationproducer.setDitheringType(MuraCompensationProducer.
                                              DitheringType.
                                              FloydSteinberg_Order);

    muracompensationproducer.produceCompensationImage(grayLevel10Bit,
        grayLevel10Bit, grayLevel10Bit, "demura/16bit.tiff", "demura/8bit.tiff");
    System.out.println(muracompensationproducer.getDitheringCheckSum());

    short[][][] planeImageData = muracompensationproducer.muraImageUtils.
        getPlaneImageData(grayLevel10Bit * 4, grayLevel10Bit * 4,
                          grayLevel10Bit * 4);
    BufferedImage planeImage = muracompensationproducer.muraImageUtils.
        getBufferedImage(planeImageData);
    ImageUtils.storeTIFFImage("demura/plane.tiff", planeImage);

    muracompensationproducer.store12BitCompensationImageToHexFormat("demura", 4);

  }
}
