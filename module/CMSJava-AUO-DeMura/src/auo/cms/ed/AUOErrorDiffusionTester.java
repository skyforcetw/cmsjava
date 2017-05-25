package auo.cms.ed;

import java.io.ObjectOutputStream;
import auo.mura.img.MuraImageUtils;

import java.io.IOException;

import auo.mura.util.ArrayUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import auo.mura.etc.PortDataRender;
import java.io.ObjectInputStream;

import java.io.FileOutputStream;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AUOErrorDiffusionTester extends AUOErrorDiffusion {
    public static void main_analysis(int framecheck) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream("ed.obj");
            ois = new ObjectInputStream(fis);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            int errindex = 0;
            int height = 300;
            int width = 300;

            while (fis.available() > 0) {
                boolean[][][] shrinkarray = (boolean[][][]) ois.readObject();
                for (int frame = 2; frame < framecheck; frame++) {
                    int[][] count = new int[height][width];

                    for (int x = 0; x < framecheck; x++) {
                        int f = x % frame;
                        for (int h = 0; h < height; h++) {
                            for (int w = 0; w < width; w++) {
                                count[h][w] += shrinkarray[f][h][w] ? 1 : 0;
                            }
                        }

                    }
                    int okcount = 0;
                    int ngcount = 0;
                    int zerocount = 0;
                    int okvalue = framecheck / 16 * (errindex + 1);
                    for (int h = 0; h < height; h++) {
                        for (int w = 0; w < width; w++) {
                            if (okvalue == count[h][w]) {
                                okcount++;
                            } else if (count[h][w] == 0) {
                                zerocount++;
                            } else {
                                ngcount++;
                            }

                        }
                    }
                    System.out.println("err: " + (errindex + 1) + " frame:" + frame + " ok " + okcount + " ng " +
                                       ngcount +
                                       " zero " + zerocount);
                    int a = 1;
                }
                errindex++;
                System.out.println("");
            }

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main_debug(int framecheck) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream("ed.obj");
            oos = new ObjectOutputStream(fos);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        for (int err = 1; err <= 15; err++) {
            double[] partAverage = new double[framecheck];
            long[] seeds = null;
            boolean[][][] shrinkarray = new boolean[framecheck][][];

            for (int frame = 0; frame < framecheck; frame++) {
                if (null != seeds) {
                    staticSeeds = seeds;
                }
                short[][] result = main((short) err, Matrix.HardwareThresholdModulation);
                int height = result.length;
                int width = result[0].length;

                boolean linearCheck = true;
                boolean printError = false;
                if (linearCheck) {
                    int checkSize = 300;
                    short[][] checkPattern = new short[checkSize][checkSize];
                    int starth = height / 2 - checkSize / 2;
                    int startw = width / 2 - checkSize / 2;
                    for (int h = 0; h < checkSize; h++) {
                        for (int w = 0; w < checkSize; w++) {
                            checkPattern[h][w] = result[h + starth][w + startw];
                        }
                    }
                    double full = (ArrayUtils.sum(result) / (height * width * 16.));
                    double part = (ArrayUtils.sum(checkPattern) / (checkSize * checkSize * 16.));
                    partAverage[frame] = part;
                    System.out.println(err + " f" + frame + " average: " + full + " : " + part +
                                       " : " + err / 16. + "(normal)");

                    boolean[][] shrink = shrink(checkPattern, (short) 16);
                    shrinkarray[frame] = shrink;

                }
                if (printError) {
                    ArrayUtils.print(result, true);
                    System.out.println("");
                    ArrayUtils.printWithError(result, toShortArray(hardwareRemainError), hardwareThreshold, true, true);
                }

                result = null;
                boolean checksum = false;
                if (checksum) {
                    System.out.println("cksum e" + err + " f" + frame + ": " + getChecksum());
                }
                System.gc();
                clearStaticData();
                seeds = finalSeeds;
            }
            try {
                oos.writeObject(shrinkarray);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws IOException {
        boolean analysis = false;
        int framecheck = 256;

        if (analysis) {
            main_analysis(framecheck);
            return;
        }

//        if (debug = false) {
//            main_debug(framecheck);
//            return;
//        }

//        MuraImageUtils utils = new MuraImageUtils(12, 1920, 1080);
        int height = 1080;
        int width = 1920;
//        Matrix[] ms = new Matrix[] {Matrix.Hardware_2_};
        Matrix[] ms = new Matrix[] {Matrix.HardwareThresholdModulation};

//        Matrix[] ms = new Matrix[] {Matrix.ExchangeError};
//        Matrix[] ms = new Matrix[] {Matrix.FloydSteinberg4Serpentine};
//        Matrix[] ms = Matrix.values();

//        int[] grayLevelArray = {64, 96, 128, 192, 400};
//        int[] grayLevelArray = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79};
//        int[] grayLevelArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        int[] grayLevelArray = {1};

        for (Matrix m : ms) {
            for (int grayLevel : grayLevelArray) {

//                short[][][] pattern = EDPaternGen.get16GrayLevelHPatternWithBlack(
//                        grayLevel, false, width, height);
//                short[][][] pattern = EDPaternGen.get16GrayLevelVPatternWithBlack( //for New ED Test
//                        grayLevel, true, width, height, 1);
                short[][][] pattern = EDPaternGen.getUniformPattern((short) grayLevel, height, width);
//                short[][][] pattern = EDPaternGen.get16LevelCirclePattern(grayLevel, false, width,
//                        height);

//                short[][][] pattern = MuraPatternGen.getRandomMuraPattern((short) grayLevelArray[0],
//                        height, width, (short) 15, 400, 0);

//                AUOErrorDiffusion.noErrorWhenBlack = true;

//                EDPaternGen.getSquarePattern(0,0,0);
//                short[][][] pattern = null;
                //                                  getEDFHDCheckPattern(width, height);

                AUOErrorDiffusion ed = new AUOErrorDiffusion();
                ed.setMatrix(m);
                //            ed.setTemplateWeight(new double[] {0, 7, 3, 5, 1});
                //              ed.setTemplateWeight(new double[] {0, 7, 4, 5, 0});
                //            ed.serpentineScan = true;
                //            ed.randomThreshold=true;
                //            ed.thresholdSeed=2;

                final short[][][] result = ed.ed8bit(pattern, PatternType.PixelBase);
                System.out.println("stThresholdCount: " + stThresholdCount);
                //            ArrayUtils.printWithError(result[0], ed.hardwareError, false);
                //            AUOErrorDiffusion.serpentineScan = false;

                boolean imgcheck = false;
                boolean remap = true;
                MuraImageUtils utils = new MuraImageUtils(12, width, height);
                if (imgcheck) {
                    //                int grayLevel = 7;
                    //                ArrayUtils.remap(result, 0, grayLevel * 16, 16,
                    //                                 (grayLevel + 1) * 16);

                    if (remap) {
                        ArrayUtils.remap(result, 0, 64 * 16, 16,
                                         255 * 16);
                    }
                    utils.store8BitImageTiff(result,
                                             "ED/ED_Test_" + m.name() + "_" +
                                             grayLevel +
                                             ".tif");
                } else {
                    String dir = "ED/ED 12411 Check/";
//                    ArrayUtils.convert12BitTo10Bit(result);
                    MuraImageUtils.storeImageToHexFormat(result, dir,
                            "", 4);

//                    String dir =
//                            "D:\\My Documents\\工作\\Project\\ED\\ed_12411_data (位於 chihchehsu)\\1920x1080_8b_single\\";
                    boolean compare = false;
                    if (compare) {
                        short[][] image12411 = PortDataRender.fourPortToOneImage(dir,
                                "12411_ed_r1_mst.txt",
                                "12411_ed_r2_mst.txt",
                                "12411_ed_r3_mst.txt",
                                "12411_ed_r4_mst.txt");

                        PortDataRender.CompareCallback callback = new PortDataRender.
                                CompareCallback() {
                            public void callback(int h, int w, int value1, int value2) {
                                result[0][h][w] = 255 * 16;
                                result[1][h][w] = 0;
                                result[2][h][w] = 0;
                            }
                        };

                        PortDataRender.compare(result[0], image12411, callback);
                    }
//                    ArrayUtils.remap(result, 4, 128 * 16, 32, 255 * 16);
                    if (remap) {
                        ArrayUtils.remap(result, 0, 64 * 16, 15,
                                         200 * 16);
                    }
                    utils.store8BitImageTiff(result,
                                             dir + "ED_Test_" + m.name() +
                                             ".tif");

                    boolean showInputPattern = false;
                    if (showInputPattern) {
                        MuraImageUtils.storeImageToHexFormat(pattern, dir,
                                "input_", 4);
                    }

                    boolean showExtraInfo = false;

                    boolean showThreshold = true;
                    boolean showTotalError = false;
                    boolean showErrW135 = true;
                    boolean showErr135 = true;
                    boolean showReminError = false;

                    if (showExtraInfo) {
                        if (showThreshold) {
                            short[][] threshold = ed.hardwareThreshold;
                            short[][][] data = {threshold, threshold, threshold};
                            MuraImageUtils.storeImageToHexFormat(data, dir,
                                    "th_", 4);
                        }
                        if (showTotalError) {
                            short[][] totalError = toShortArray(ed.hardwareTotalError);
                            short[][][] data = {totalError, totalError, totalError};
                            MuraImageUtils.storeImageToHexFormat(data, dir,
                                    "total_", 4);
                        }
                        if (showErrW135) {
                            short[][] err135 = ed.hardwareErrW135;
                            short[][][] data = {err135, err135, err135};
                            MuraImageUtils.storeImageToHexFormat(data, dir,
                                    "errW135_", 4);
                        }
                        if (showErr135) {
                            short[][][] err135 = ed.hardwareErr135;
                            short[][][] data3 = {err135[1], err135[1], err135[1]};
                            short[][][] data5 = {err135[2], err135[2], err135[2]};
                            MuraImageUtils.storeImageToHexFormat(data3, dir,
                                    "err3_", 4);
                            MuraImageUtils.storeImageToHexFormat(data5, dir,
                                    "err5_", 4);
                        }
                    }
                    if (showReminError) {
                        short[][] remain = toShortArray(ed.hardwareRemainError);
                        short[][][] data = {remain, remain, remain};
                        MuraImageUtils.storeImageToHexFormat(data, dir,
                                "remain_", 4);
                    }
                }
            }
        }
    }
}
