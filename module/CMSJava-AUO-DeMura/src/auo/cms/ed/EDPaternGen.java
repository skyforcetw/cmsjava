package auo.cms.ed;

import java.io.File;
import java.io.IOException;

import auo.mura.img.MuraImageUtils;
import auo.mura.img.MuraPatternGen;
import auo.mura.util.ArrayUtils;

import auo.cms.frc.*;

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
public class EDPaternGen {
    static int BlockWidth = 15;
    static int BlockHeight = 8;

    public static void produceSquarePattern(int startGrayLevel, int length,
                                            AUOErrorDiffusion.Matrix m) throws IOException {
        String dir = "ED/ED Square Pattern/" + m.name();
        File dirfile = new File(dir);
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }
        boolean remap = true;
        for (int err = 8; err <= 8; err++) {
            short[][][] pattern = getSquarePattern(startGrayLevel, err, length);
            AUOErrorDiffusion ed = new AUOErrorDiffusion();
            ed.setMatrix(m);
            System.out.println("err: " + err);
            final short[][][] result = ed.ed8bit(pattern, PatternType.PixelBase);

            if (remap) {
                ArrayUtils.remap(result, 16, 96 * 16, 32, 192 * 16);
            }
            MuraImageUtils utils = new MuraImageUtils(12, length, length);

            String filename = dir + "/err" + String.format("%02d", err) + ".tif";
            utils.store8BitImageTiff(result, filename);
        }

    }

    /**
     * @deprecated
     */
    public static void testError15Weighting() {
        int height = 1080;
        int width = 1920;
        boolean remap = true;
        String filename = null;
        try {
            for (int x = 1; x <= 63; x++) {
                int d_11 = x;
                int d01 = 64 - d_11;
//                AUOErrorDiffusion.testWeightings[15][1] = d_11;
//                AUOErrorDiffusion.testWeightings[15][2] = d01;
                filename = "ED/" + x + ".tif";

//                short[][][] pattern = getSquarePattern(0, 1, height);
//
//                AUOErrorDiffusion ed = new AUOErrorDiffusion();
//                ed.setMatrix(AUOErrorDiffusion.Matrix.HardwareThresholdModulation);
//
//                final short[][][] result = ed.ed8bit(pattern, PatternType.PixelBase);
//
//                if (remap) {
//                    ArrayUtils.remap(result, 0, 128 * 16, 16, 255 * 16);
//                }
//                int imageHeight = pattern[0].length;
//                int imageWidth = pattern[0][0].length;
//                MuraImageUtils utils = new MuraImageUtils(12, imageWidth, imageHeight);
//
//                utils.store8BitImageTiff(result, filename);

                MuraImageUtils utils = new MuraImageUtils(12, width, height);
                short[][][] pattern = EDPaternGen.get16GrayLevelHPatternWithBlack(
                        64, false, width, height, 1);

                AUOErrorDiffusion ed = new AUOErrorDiffusion();
                ed.setMatrix(AUOErrorDiffusion.Matrix.HardwareThresholdModulation);

                final short[][][] result = ed.ed8bit(pattern, PatternType.PixelBase);

                if (true) {
                    ArrayUtils.remap(result, 64, 64 * 16, 80, 200 * 16);
                }
                utils.store8BitImageTiff(result, "ED/" + x + ".tif");

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        int width = 1920;
        int height = 1080;
//        int height = 40;
        int patternno = 0;

        String filename = null;

        if (false) {
            produceSquarePattern(16, 600, AUOErrorDiffusion.Matrix.HardwareThresholdModulation);
            return;
        }
        if (false) {
            testError15Weighting();
            return;

        }

        short[][][] pattern = null;
        switch (patternno) {
        case 0: //circle
            pattern = get16LevelCirclePattern(0, false, width, height);
            filename = "ED/circle.tif";
            break;
        case 1: //square
            pattern = getSquarePattern(0, 1, height);
            filename = "ED/square.tif";
            break;
        case 2: //random mura pattern
            pattern = MuraPatternGen.getRandomMuraPattern((short) 0, height, 1920, (short) 16, 200,
                    0);
            filename = "ED/randommura.tif";
            break;
        }

        short[][][] result = null;
        boolean doED = true;
        boolean remap = true;
        if (doED) {
            AUOErrorDiffusion ed = new AUOErrorDiffusion();
            ed.setMatrix(AUOErrorDiffusion.Matrix.HardwareThresholdModulation);

            result = ed.ed8bit(pattern, PatternType.PixelBase);
            ArrayUtils.printWithError(result[0], AUOErrorDiffusion.toShortArray(AUOErrorDiffusion.hardwareRemainError),
                                      AUOErrorDiffusion.hardwareThreshold, true, false);

            if (remap) {
                ArrayUtils.remap(result, 0, 128 * 16, 16, 255 * 16);
            }
        }
//        filterVerticalLine(result, (short) (255 * 16));


        int imageHeight = pattern[0].length;
        int imageWidth = pattern[0][0].length;
        MuraImageUtils utils = new MuraImageUtils(12, imageWidth, imageHeight);
        utils.store8BitImageTiff(result != null ? result : pattern, filename);

    }

    static final double[] polar2cartesianCoordinatesValues(final double
            distance, final double angle) {

        double t = (angle * Math.PI) / 180.0;

        double[] cartesianValues = new double[2];
        cartesianValues[0] = distance * Math.cos(t);
        cartesianValues[1] = distance * Math.sin(t);

        return cartesianValues;
    }

    public static short[][][] getSquarePattern(int startGrayLevel, int error, int length) {
        short[][][] result = new short[3][length][length];
        for (int h = 0; h < length; h++) {
            for (int w = 0; w < length; w++) {
                result[0][h][w] = (short) (startGrayLevel + error);
                result[1][h][w] = (short) (startGrayLevel + error);
                result[2][h][w] = (short) (startGrayLevel + error);
            }
        }
        return result;
    }

    public static short[][][] getUniformPattern(short grayLevel, int height, int width) {
        short[][][] result = new short[3][height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[0][h][w] = grayLevel;
                result[1][h][w] = grayLevel;
                result[2][h][w] = grayLevel;
            }
        }
        return result;
    }


    public static short[][][] get16LevelCirclePattern(int
            startGrayLevel, boolean reverse, int width, int height) {
//        int heightpiece = height/16;
        int radiuspiecce = height / 15 / 2;
        int centerh = height / 2;
        int centerw = width / 2;
//        int halfw = width / 2;
        short[][][] result = new short[3][height][width];
        if (!reverse) {

        } else {

        }
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[0][h][w] = (short) startGrayLevel;
                result[1][h][w] = (short) startGrayLevel;
                result[2][h][w] = (short) startGrayLevel;
            }
        }
        int preh = -1;
        int errstart = !reverse ? 1 : 15;
        int errend = !reverse ? 16 : 0;
        int errstep = !reverse ? 1 : -1;
        for (int err = errstart, x = 1; err != errend; err += errstep, x++) {

            int radius = radiuspiecce * (16 - x);
            for (double angle = 270; angle <= 360; angle += 0.0625) {
                double[] yx = polar2cartesianCoordinatesValues(radius, angle);
                int h = (int) (centerh - Math.round(yx[0]));
                int w = (int) Math.round(yx[1]) + centerw;
                if (preh != h) {
                    for (int w_ = w; w_ < centerw; w_++) {
                        result[0][h][w_] = (short) (startGrayLevel + err);
                        result[1][h][w_] = (short) (startGrayLevel + err);
                        result[2][h][w_] = (short) (startGrayLevel + err);
                    }
                }
                preh = h;

            }
        }

        for (int h = 0; h < centerh; h++) {
            for (int w = centerw; w < width; w++) {
                result[0][h][w] = result[0][h][centerw * 2 - 1 - w];
                result[1][h][w] = result[1][h][centerw * 2 - 1 - w];
                result[2][h][w] = result[2][h][centerw * 2 - 1 - w];
            }
        }

        for (int h = centerh; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[0][h][w] = result[0][centerh * 2 - 1 - h][w];
                result[1][h][w] = result[1][centerh * 2 - 1 - h][w];
                result[2][h][w] = result[2][centerh * 2 - 1 - h][w];
            }
        }

        return result;
    }


//    public static short[][][] get16LevelHPatternWithBlack(int
//            startGrayLevel, boolean reverse, int width, int height) {
//
//        short[][][] result = new short[3][height][width];
//
//        int blackwidth = 1;
//        //Zone B
//        for (int ww = 0; ww < 1; ww++) {
//            for (int hh = 0; hh < 16; hh++) {
//                int hsta = hh * BlockHeight * 8 + blackwidth;
//                int hend = hsta + (BlockHeight * 8) - 2 * blackwidth;
//
//                int wsta = 0; // blackwidth;
//                int wend = width; //- blackwidth;
//
//                for (int w = wsta; w < wend; w++) {
//                    for (int h = hsta; h < hend; h++) {
//
//                        for (int ch = 0; ch < 3; ch++) {
//                            result[ch][h][w] = (short) (startGrayLevel);
//                        }
//                    }
//
//                }
//                if (reverse) {
//                    startGrayLevel--;
//                } else {
//                    startGrayLevel++;
//                }
//            }
//        }
//        return result;
//    }

    public static short[][][] get16GrayLevelVPatternWithBlack(int
            startGrayLevel, boolean reverse, int width, int height, int blackwidth) {

        short[][][] result = new short[3][height][width];

//        int blackwidth = 1;
        int blockWidth = (width - blackwidth * 15) / 16;

        //Zone B
        for (int ww = 0; ww < 16; ww++) {
            for (int hh = 0; hh < 1; hh++) {
                int hsta = 0;
                int hend = height;

                int wsta = ww * blackwidth + ww * blockWidth;
                int wend = wsta + blockWidth;

                for (int w = wsta; w < wend; w++) {
                    for (int h = hsta; h < hend; h++) {

                        for (int ch = 0; ch < 3; ch++) {
                            result[ch][h][w] = (short) (startGrayLevel);
                        }
                    }

                }

            }
            if (reverse) {
                startGrayLevel--;
            } else {
                startGrayLevel++;
            }

        }
        return result;
    }


    /**
     * GrayLevel0 (error0)
     * Black
     * GrayLevel1 (error1)
     * Black
     * GrayLevel2 (error2)
     * Black
     * .
     * .
     * GrayLevel15 (error15)
     * Black
     *
     * @param startGrayLevel int
     * @param reverse boolean
     * @param width int
     * @param height int
     * @param blackwidth int
     * @return short[][][]
     */
    public static short[][][] get16GrayLevelHPatternWithBlack(int
            startGrayLevel, boolean reverse, int width, int height, int blackwidth) {

        short[][][] result = new short[3][height][width];

//        int blackwidth = 1;
        //Zone B
        for (int ww = 0; ww < 1; ww++) {
            for (int hh = 0; hh < 16; hh++) {
                int hsta = hh * BlockHeight * 8 + blackwidth;
                int hend = hsta + (BlockHeight * 8) - 2 * blackwidth;

                int wsta = 0; // blackwidth;
                int wend = width; //- blackwidth;

                for (int w = wsta; w < wend; w++) {
                    for (int h = hsta; h < hend; h++) {

                        for (int ch = 0; ch < 3; ch++) {
                            result[ch][h][w] = (short) (startGrayLevel);
                        }
                    }

                }
                if (reverse) {
                    startGrayLevel--;
                } else {
                    startGrayLevel++;
                }
            }
        }
        return result;
    }

}
