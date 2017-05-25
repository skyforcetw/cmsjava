package auo.mura.exec2;

import java.io.BufferedReader;
import java.io.File;
//import auo.mura.Dithering;
import java.io.FileNotFoundException;
import java.io.IOException;

import auo.mura.CorrectionData;
import auo.mura.DeMuraParameter;
import auo.mura.MuraCompensationProducer;
import auo.mura.Resolution;
import auo.mura.exec.MonoDMCSimulator;
import auo.mura.img.PatternGen;
import jxl.read.biff.BiffException;

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
public class PatternResultSingleProducer {

//  private static short[][][] getFullPattern(short grayLevel) {
//    short[][][] image = new short[3][1080][1920];
//    for (int h = 0; h < 1080; h++) {
//      for (int w = 0; w < 1920; w++) {
//        image[0][h][w] = grayLevel;
//        image[1][h][w] = grayLevel;
//        image[2][h][w] = grayLevel;
//      }
//    }
//    return image;
//  }

    public static void specialProduce(short grayLevel) throws
            FileNotFoundException,
            IOException {
        String inputdir =
//        "Y:/Verify Items/Simulation Result/(3003)35.limit1_4 32(X0-0-0)";
//        "Y:/Verify Items/Verify LUT/Limit Case/1920x1080/13.limit2_8 16(X2-1-0)";
//        "\\\\labd3c-e7f-021\\DeMura\\Verify Items\\Verify LUT\\Limit Case\\1920x1080\\low level debug";
                "Y:/Verify Items/Verify LUT/Limit Case/1920x1080/7.limit2_8 8(X2-1-0)/";

//    String imageFilename =
//        "Y:/Verify Items/Limit Pattern/(3)pattern_11level_1920x1080.bmp";
//        "Y:/Verify Items/Limit Pattern/(1)pattern_256level_1920x1080.bmp";
//        "Y:/Verify Items/Limit Pattern/(2)pattern_2vertical1920x1080.bmp";
        short[][][] imageData = PatternGen.getFHDPattern(grayLevel);

        String parameterFilename =
//        "\\\\labd3c-e7f-021/DeMura/Verify Items/Verify LUT/Real Case/No.1/1920x1080/real_8x8/par.csv";
                inputdir + "/par.csv";
        String correctFilename =
                inputdir + "/lut.csv";

        DeMuraParameter parameter = new DeMuraParameter(parameterFilename);

//    parameter.dataOffset1 = 4021;
//    parameter.dataMag1 = 0;
        parameter.blackLimit = 0;
//        parameter.planeB1Coef = 928;

        CorrectionData correctiondata = new CorrectionData(correctFilename,
                parameter);
        String outputdirname = inputdir + Integer.toString(grayLevel);
        new File(outputdirname).mkdir();
//        "D:/ณnล้/nobody zone/Mura/Verify Simulation Experiment/";
//        "Y:/Verify Items/Verify LUT/Special Case/No.3 real_8x8 - 4";
//        inputdir;

//    MuraCompensationExecuter.execute(correctiondata, imageFilename, outputdirname,
//                                     MuraCompensationProducer.DitheringType.
//                                     FloydSteinberg_Wiki_LineBased, true);
        MonoDMCSimulator.simulate(correctiondata, imageData,
                                              outputdirname,
                                              MuraCompensationProducer.DitheringType.
                                              Hardware_2_, true, false,
                                              false, 10, 4);
    }

    static int[] error = new int[1920];
    public static void compare(String filename1, String filename2, int port) throws
            FileNotFoundException, IOException {
        java.io.BufferedReader reader1 = new BufferedReader(new java.io.
                FileReader(new File(filename1)));
        java.io.BufferedReader reader2 = new BufferedReader(new java.io.
                FileReader(new File(filename2)));
        int index = 0;
        int errcount = 0;

        while (reader1.ready() && reader2.ready()) {
            String line1 = reader1.readLine();
            String line2 = reader2.readLine();
            if (!line1.equalsIgnoreCase(line2)) {
                int w = (index % 480);
                int h = index / 480;
                int wpixel = (w * 4 + 1 + (port - 1));
                System.out.println((index + 1) + " " + (h + 1) + " " +
                                   wpixel + ": " + line1 +
                                   "/" + line2 + " p" + port);
                error[wpixel - 1]++;
                errcount++;
            }

            index++;
        }
        System.out.println("errcount: " + errcount);

    }


    private static void checkPixel1(String filename) throws
            FileNotFoundException, IOException {
        java.io.BufferedReader reader1 = new BufferedReader(new java.io.
                FileReader(new File(filename)));

        int index = 0;
        String preline = null;
        while (reader1.ready()) {
            String line = reader1.readLine();
            boolean pixel2 = ((index % 480) + 1) == 2;
            if (null != preline && !preline.equalsIgnoreCase(line) && pixel2) {
                int i2 = Integer.parseInt(line, 16);
                int i1 = Integer.parseInt(preline, 16);
                if (i1 > i2) {
                    System.out.println("oh!:" + (index + 1));
                }
            }

            preline = line;
//           if (!line1.equalsIgnoreCase(line2)) {
//               int w = (index % 480);
//               int h = index / 480;
//               int wpixel = (w * 4 + 1 + (port - 1));
////                int p = (index % 4) + 1;
//               System.out.println((index + 1) + " " + (h + 1) + " " +
//                                  wpixel + ": " + line1 +
//                                  "/" + line2 + " p" + port);
//               error[wpixel - 1]++;
////                return;
//           }

            index++;
        }

    }

    public static void main(String[] args) throws IOException, BiffException {
        normalProduce(args);
        if (false) {
            String ch = "g";
            String dir =
                    "Y:/Verify Items/Simulation Result/(0011) - 2Vertical/";
//        checkPixel1(dir + "ED Check/ED_" + ch + "1.txt");
            compare(
                    dir + "dmc_ed/ed_" + ch + "1.txt",
                    dir + "ED Check/ED_" + ch + "1.txt", 1);

//        compare(
//                "Y:/Verify Items/Simulation Result/(0011) - 2Vertical/dmc_ed/ed_" +
//                ch + "2.txt",
//                "Y:/Verify Items/Simulation Result/(0011) - 2Vertical/ED Check/ED_" +
//                ch + "2.txt",
//                2);
//        compare(
//                "Y:/Verify Items/Simulation Result/(0011) - 2Vertical/dmc_ed/ed_" +
//                ch + "3.txt",
//                "Y:/Verify Items/Simulation Result/(0011) - 2Vertical/ED Check/ED_" +
//                ch + "3.txt",
//                3);
//        compare(
//                "Y:/Verify Items/Simulation Result/(0011) - 2Vertical/dmc_ed/ed_" +
//                ch + "4.txt",
//                "Y:/Verify Items/Simulation Result/(0011) - 2Vertical/ED Check/ED_" +
//                ch + "4.txt",
//                4);
        }

//        System.out.println("pixel err:");
//        for (int x = 0; x < 1920; x++) {
//            if (error[x] != 0) {
//                System.out.println(x);
//            }
//        }


//    for (short grayLevel = 1019; grayLevel <= 1021; grayLevel++) {
//      specialProduce(grayLevel);
//    }
    }

    public static void normalProduce(String[] args) throws BiffException,
            IOException {
//        Dithering.Debug = false;
        MuraCompensationProducer.SingleChannelProcess = false;
        simulate();
//    for (int x = 1; x <= 26; x++) {
//      main(x);
//    }
//    main(2);
    }

    static void convertTo8Bit(short[][][] imageData) {
        int chs = imageData.length;
        int height = imageData[0].length;
        int width = imageData[0][0].length;
        int data;
        for (int ch = 0; ch < chs; ch++) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    data = imageData[ch][h][w] / 4;
                    imageData[ch][h][w] = (short) (data * 4);
                }
            }
        }
    }

    static String findDir(String basedir, int no) {
        File dir = new File(basedir);
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                String name = f.getName();
                int index1 = name.indexOf('(') + 1;
                int index2 = name.indexOf(')');
                if ( -1 != index1 && -1 != index2) {
                    String nostr = name.substring(index1, index2);
                    if (Integer.parseInt(nostr) == no) {
                        return f.getAbsolutePath();
                    }

                }

            }
        }
        return null;
    }

//    enum Resolution {
//        FHD, WQHD, _4K2K
//    }


    static void simulate() throws IOException {

        final boolean T = true;
        final boolean F = false;

//    String inputdir = "\\\\labd3c-e7f-021/DeMura/Verify Items/Verify LUT/Limit Case/1920x1080/limit2_8 8(X0)";
//    String inputdir = "\\\\labd3c-e7f-021/DeMura/Verify Items/Verify LUT/Limit Case/1920x1080/limit3_8 8(X0)";
        String basedir = "Y:/Verify Items/Simulation Result/";
        //======================================================================
        // setting
        //======================================================================
        String inputdir = findDir(basedir, 7);
//        String inputdir =
//                "D:/WorkSpace/CMSJava2/module/CMSJava-AUO-DeMura/workdir/ED Check/";
//        inputdir = "\\\\itingsun\\upload\\DeMura_Verification\\AUO12411\\TEST\\4K2K\\32.limit2_16x16(X2-2-1)\\";
        inputdir = "Y:/Verify Items/Verify LUT/Limit Case/1920x1080/17.limit2_16 16(X1-1-0)";
//        inputdir = "d:\\51.limit2_4 32(X0-0-0)\\";
        int patternno = 2;
        int port = 2;
        Resolution res = Resolution.FHD;

        boolean fromBMP = F;
        boolean fromPG8Bit = F;

        boolean useDMC = T;
        boolean useDG = F;
        boolean useFRC = F;
        boolean useED = T;
        int edbit = 4;
        //======================================================================

//        String imageFilename =
//                "Y:/Verify Items/Limit Pattern/(3)pattern_11level_1920x1080.bmp";
//                "Y:/Verify Items/Limit Pattern/(1)pattern_256level_1920x1080.bmp";
//                "Y:/Verify Items/Limit Pattern/(2)pattern_2vertical1920x1080.bmp";
        short[][][] imageData = null;
        String imageFilename = null;
        switch (patternno) {
        case 1:
            if (res == Resolution.FHD) {
                imageData = PatternGen.getFHDPattern1_1023L();
            } else if (res == Resolution.WQHD) {
                imageData = PatternGen.getWQHDPattern1_1023L();
            }

            imageFilename =
                    "Y:/Verify Items/Limit Pattern/(1)pattern_256level_1920x1080.bmp";
            break;
        case 2:
            if (res == Resolution.FHD) {
                imageData = PatternGen.getFHDPattern2_1023L_2Vertical();
            } else if (res == Resolution.WQHD) {
                imageData = PatternGen.getWQHDPattern2_1023L_2Vertical();
            } else if (res == Resolution._4K2K) {
                imageData = PatternGen.get4K2KPattern2_1023L_2Vertical();
            } else if (res == Resolution._5120) {
                imageData = PatternGen.get5120Pattern2_1023L_2Vertical();
            }

            imageFilename =
                    "Y:/Verify Items/Limit Pattern/(2)pattern_2vertical1920x1080.bmp";
            break;
        case 3:
            if (res == Resolution.FHD) {
                imageData = PatternGen.getFHDPattern3_11L_();
            }
            imageFilename =
                    "Y:/Verify Items/Limit Pattern/(3)pattern_11level_1920x1080.bmp";
            break;
        case 4:
            break;
        case 5:
            if (res == Resolution.FHD) {
                imageData = PatternGen.getFHDPattern5_FRC();
            }
            break;
        case 6:
            if (res == Resolution.FHD) {
                imageData = PatternGen.getFHDPattern((short) (5 * 4));
            } else if (res == Resolution._4K2K) {
                imageData = PatternGen.getWholeFramePattern((short) (25 * 4), 3840, 2160);
            }
            break;
        default:

//            return;
        }

        if (true == fromBMP && true == fromPG8Bit) {
            fromBMP = false;
            convertTo8Bit(imageData);

        }

        String parameterFilename = inputdir + "/par.csv";
        String correctFilename = inputdir + "/lut.csv";

        DeMuraParameter parameter = new DeMuraParameter(parameterFilename);
        CorrectionData correctiondata = new CorrectionData(correctFilename,
                parameter);
        String outputdirname = inputdir;

        if (fromBMP) {
            MonoDMCSimulator.simulate(correctiondata, imageFilename,
                                                  outputdirname,
                                                  MuraCompensationProducer.
                                                  DitheringType.Hardware_2_,
                                                  useDMC, useDG, port);

        } else {
            MonoDMCSimulator.simulate(correctiondata, imageData,
                                                  outputdirname,
                                                  MuraCompensationProducer.
                                                  DitheringType.Hardware_2_,
                                                  useDMC, useDG, useED, edbit,
                                                  port);

        }

    }


}
