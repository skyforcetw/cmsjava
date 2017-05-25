package auo.mura.table;

import java.io.IOException;

import auo.cms.ed.AUOErrorDiffusion;
import auo.cms.ed.PatternType;
import auo.mura.img.MuraImageUtils;

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
public class EDVerifyDMCTableProducer {


    public static void main(String[] args) throws IOException {
        producePattern();
//    produceTable();
    }


    /**
     * |-----------|
     * |     |     |
     * |  A  |  B  |
     * |-----------|
     * |     C     |
     * |-----------|
     * @param startGrayLevel int
     * @param reverse boolean
     * @return short[][][]
     */
    public static short[][][] getEDFHDTestPattern2_CompareToHW(int
            startGrayLevel, boolean reverse) {
        int width = 1920;
        int height = 1080;
        short bgGrayLevle8bit = 5;
        short[][][] result = new short[3][height][width];

        int halfWidth = width / 2;
        int baseline = 16 * BlockHeight * 8;

        //填左邊讓dmc造pattern (Zone A)
        for (int h = 0; h < baseline; h++) {
            for (int w = 0; w < halfWidth; w++) {
                for (int ch = 0; ch < 3; ch++) {
                    result[ch][h][w] = (short) (16 * bgGrayLevle8bit);
                }
            }
        }
        //底部填grayLevle8bit (Zone C)
        for (int h = baseline; h < height; h++) {
            for (int w = 0; w < width; w++) {
                for (int ch = 0; ch < 3; ch++) {
                    result[ch][h][w] = (short) (16 * bgGrayLevle8bit);
                }
            }
        }

        int blackwidth = 8;
        //Zone B
        for (int ww = 0; ww < 8; ww++) {
            for (int hh = 0; hh < 16; hh++) {
                int hsta = hh * BlockHeight * 8 + blackwidth;
                int hend = hsta + (BlockHeight * 8) - 2 * blackwidth;
                int wsta = halfWidth + ww * BlockWidth * 8 + blackwidth;
                int wend = wsta + (BlockWidth * 8) - 2 * blackwidth;
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

    /**
     * |-----------|
     * |     |     |
     * |  A  |  B  |
     * |-----------|
     * |     C     |
     * |-----------|
     * @param startGrayLevel int
     * @param reverse boolean
     * @return short[][][]
     */
    public static short[][][] getEDFHDTestPattern3_CompareToHW(int
            startGrayLevel, boolean reverse) {
        int width = 1920;
        int height = 1080;
        short bgGrayLevle8bit = 0;
        short[][][] result = new short[3][height][width];

        int halfWidth = width / 2;
        int baseline = 16 * BlockHeight * 8;

        //填左邊讓dmc造pattern (Zone A)
        for (int h = 0; h < baseline; h++) {
            for (int w = 0; w < halfWidth; w++) {
                for (int ch = 0; ch < 3; ch++) {
                    result[ch][h][w] = (short) (16 * bgGrayLevle8bit);
                }
            }
        }
        //底部填grayLevle8bit (Zone C)
        for (int h = baseline; h < height; h++) {
            for (int w = 0; w < width; w++) {
                for (int ch = 0; ch < 3; ch++) {
                    result[ch][h][w] = (short) (16 * bgGrayLevle8bit);
                }
            }
        }

        int blackwidth = 8;
        //Zone B
        for (int ww = 0; ww < 1; ww++) {
            for (int hh = 0; hh < 16; hh++) {
                int hsta = hh * BlockHeight * 8 + blackwidth;
                int hend = hsta + (BlockHeight * 8) - 2 * blackwidth;
//                int wsta = halfWidth + ww * BlockWidth * 8 + blackwidth;
//                int wend = wsta + (BlockWidth * 8) - 2 * blackwidth;

                int wsta = halfWidth + blackwidth;
                int wend = width - blackwidth;

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

    public static short[][][] getEDFHDCheckPattern(int width, int height) {

        short[][][] result = new short[3][height][width];

        int pieceheight = height / 15;

        for (short err = 1; err <= 15; err++) {
            for (int hindex = 0; hindex < pieceheight; hindex++) {
                int h = pieceheight * (err - 1) + hindex;
                for (int w = 0; w < width; w++) {
                    result[0][h][w] = result[1][h][w] = result[2][h][w] = err;
                }
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
     * @return short[][][]
     */
    public static short[][][] getEDFHDTestPattern4_16GrayLevelHPatternWithBlack(int
            startGrayLevel, boolean reverse, int width, int height) {

        short[][][] result = new short[3][height][width];

        int blackwidth = 1;
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

    public static void producePattern() throws IOException {
        MuraImageUtils utils = new MuraImageUtils(12, 1920, 1080);
        AUOErrorDiffusion ed = new AUOErrorDiffusion();
//        short[][][] pattern = getErrorDiffusionFHDTestPattern2((short) 0,false);
        short[][][] pattern = getEDFHDTestPattern2_CompareToHW((short) 128, true);

        short[][][] result = ed.ed8bit(pattern, PatternType.PixelBase);

        utils.store8BitImageTiff(result, "ED Check/ED_check.tif");

    }

    static int BlockWidth = 15;
    static int BlockHeight = 8;

    public static void produceTable() throws IOException {
        int blockh = 136;
        int blockw = 241;
        double[][][] dmcTable = new double[3][blockh][blockw];
        int halfblockw = blockw / 2;
        int grayLevel = 0;
        double data = 0;
        int firstGrayLevel = 5;
        int secondGrayLevel = 10;
        int thirdGrayLevel = 178;
        int[] grayLevelArray = new int[] {firstGrayLevel, secondGrayLevel,
                               thirdGrayLevel};
        for (int level = 0; level < 3; level++) {
            for (int h = 0; h < blockh; h++) {
                for (int w = 0; w < blockw; w++) {
                    dmcTable[level][h][w] = grayLevelArray[level];
                }
            }

        }
        for (int w = 0; w < halfblockw; w += BlockWidth) {
            int hindex = 0;
            for (int h = 0; h < blockh && hindex < 16; h += BlockHeight,
                         hindex++) {
                int wend = w + BlockWidth - 1;
                int hend = h + BlockHeight;
                System.out.println(h + " " + w + " " + hend + " " + wend);
                data = grayLevel / 16.;
                for (int hh = h; hh < hend; hh++) {
                    for (int ww = w; ww < wend; ww++) {
                        dmcTable[0][hh][ww] = data;
                        dmcTable[1][hh][ww] = data;
                        dmcTable[2][hh][ww] = thirdGrayLevel;
                    }

                    dmcTable[0][hh][wend] = 0;
                    dmcTable[1][hh][wend] = 0;
                    dmcTable[2][hh][wend] = thirdGrayLevel;

                }
                for (int ww = w; ww < wend; ww++) {
                    dmcTable[0][h + 7][ww] = 0;
                    dmcTable[1][h + 7][ww] = 0;
                    dmcTable[2][h + 7][ww] = thirdGrayLevel;
                }

                grayLevel++;

            }
        }
        DeMuraTableProducer.writeToCSV("ed.csv", dmcTable, grayLevelArray);
        int a = 1;
    }
}
