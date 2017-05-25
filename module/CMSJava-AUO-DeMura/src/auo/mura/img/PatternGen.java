package auo.mura.img;

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
public class PatternGen {
    public static void main(String[] args) {
        get4K2KPattern_1024L(true, true, true);
    }

    public static void clip(short[][][] image, short max) {
        for (short[][] v1 : image) {
            for (short[] v2 : v1) {
                for (int x = 0; x < v2.length; x++) {
                    v2[x] = v2[x] > max ? max : v2[x];
                }
            }
        }
    }

    public static short[][][] get4K2KPattern_256L(boolean r, boolean g, boolean b) {
        short[][][] pattern = new short[3][2160][3840];
        int piecewidth = 3840 / 256;
        for (int x = 0; x < 256; x++) {
            for (int n = 0; n < piecewidth; n++) {
                int w = x * piecewidth + n;
                for (int h = 0; h < 2160; h++) {
                    if (r) {
                        pattern[0][h][w] = (short) (x * 4);
                    }
                    if (g) {
                        pattern[1][h][w] = (short) (x * 4);
                    }
                    if (b) {
                        pattern[2][h][w] = (short) (x * 4);
                    }

                }
            }

        }
        return pattern;
    }

    public static short[][][] get4K2KPattern_1024L(boolean r, boolean g, boolean b) {
        short[][][] pattern = new short[3][2160][3840];
//        int piecewidth = 3840 / 256;
        for (int x = 0; x < 256; x++) {
            for (int wp = 0; wp < 3; wp++) {
                int w = x * 3 + wp;
                for (int h = 0; h < 2160; h++) {
                    if (r) {
                        pattern[0][h][w] = (short) x;
                    }
                    if (g) {
                        pattern[1][h][w] = (short) x;
                    }
                    if (b) {
                        pattern[2][h][w] = (short) x;
                    }
                }
            }
        }
        for (int x = 256; x < 1024; x++) {
            for (int wp = 0; wp < 4; wp++) {
                int w = 768 + (x - 256) * 4 + wp;
                for (int h = 0; h < 2160; h++) {
                    if (r) {
                        pattern[0][h][w] = (short) x;
                    }
                    if (g) {
                        pattern[1][h][w] = (short) x;
                    }
                    if (b) {
                        pattern[2][h][w] = (short) x;
                    }
                }
            }
        }
//        for (int x = 0; x < 256; x++) {
//            for (int n = 0; n < piecewidth; n++) {
//                int w = x * piecewidth + n;
//                for (int h = 0; h < 2160; h++) {
//                    if (r) {
//                        pattern[0][h][w] = (short) (x * 4);
//                    }
//                    if (g) {
//                        pattern[1][h][w] = (short) (x * 4);
//                    }
//                    if (b) {
//                        pattern[2][h][w] = (short) (x * 4);
//                    }
//
//                }
//            }
//
//        }
        return pattern;
    }


    public static short[][][] get4K2KPattern2_1023L_2Vertical() {
        short[][][] pattern = new short[3][2160][3840];

        int halfWidth = 3840 / 2;
        for (short gl = 0; gl <= 398; gl++) {
            int L1 = gl * 4;
            for (int w = 0; w < halfWidth; w++) {
                for (int x = 0; x < 4; x++) {
                    for (int ch = 0; ch < 3; ch++) {
                        pattern[ch][L1 + x][w] = gl;
                        pattern[ch][L1 + x][halfWidth + w] = (short) (512 + gl);
                    }

                }

            }
        }

        for (short gl = 399; gl <= 511; gl++) {
            int L1 = 399 * 4 + (gl - 399) * 5;
            for (int w = 0; w < halfWidth; w++) {
                for (int x = 0; x < 5; x++) {
                    if (L1 + x < 2160) {
                        for (int ch = 0; ch < 3; ch++) {
                            pattern[ch][L1 + x][w] = gl;
                            pattern[ch][L1 + x][halfWidth +
                                    w] = (short) (512 + gl);
                        }
                    }

                }

            }
        }
//        short[] check = new short[2160];
//        for (int h = 0; h < 2160; h++) {
//            check[h] = pattern[0][h][0];
//        }

        return pattern;
    }

    public static short[][][] get5120Pattern2_1023L_2Vertical() {
        short[][][] pattern = new short[3][2160][5120];

        int halfWidth = 5120 / 2;
        for (short gl = 0; gl <= 398; gl++) {
            int L1 = gl * 4;
            for (int w = 0; w < halfWidth; w++) {
                for (int x = 0; x < 4; x++) {
                    for (int ch = 0; ch < 3; ch++) {
                        pattern[ch][L1 + x][w] = gl;
                        pattern[ch][L1 + x][halfWidth + w] = (short) (512 + gl);
                    }

                }

            }
        }

        for (short gl = 399; gl <= 511; gl++) {
            int L1 = 399 * 4 + (gl - 399) * 5;
            for (int w = 0; w < halfWidth; w++) {
                for (int x = 0; x < 5; x++) {
                    if (L1 + x < 2160) {
                        for (int ch = 0; ch < 3; ch++) {
                            pattern[ch][L1 + x][w] = gl;
                            pattern[ch][L1 + x][halfWidth +
                                    w] = (short) (512 + gl);
                        }
                    }

                }

            }
        }
//      short[] check = new short[2160];
//      for (int h = 0; h < 2160; h++) {
//          check[h] = pattern[0][h][0];
//      }

        return pattern;
    }


    public static short[][][] getWQHDPattern1_1023L() {
        short[][][] pattern = new short[3][1440][2560];

        for (short x = 0; x <= 511; x++) {
            for (int h = 0; h < 1440; h++) {
                pattern[0][h][x * 2] = x;
                pattern[1][h][x * 2] = x;
                pattern[2][h][x * 2] = x;
                pattern[0][h][x * 2 + 1] = x;
                pattern[1][h][x * 2 + 1] = x;
                pattern[2][h][x * 2 + 1] = x;

            }
        }
        for (short x = 512; x <= 1023; x++) {
            int w = 512 * 2 + (x - 512) * 3;
            for (int h = 0; h < 1440; h++) {
                pattern[0][h][w] = x;
                pattern[1][h][w] = x;
                pattern[2][h][w] = x;
                pattern[0][h][w + 1] = x;
                pattern[1][h][w + 1] = x;
                pattern[2][h][w + 1] = x;
                pattern[0][h][w + 2] = x;
                pattern[1][h][w + 2] = x;
                pattern[2][h][w + 2] = x;
            }
        }

        return pattern;
    }

    public static short[][][] getWQHDPattern2_1023L_2Vertical() {
        short[][][] pattern = new short[3][1440][2560];
        int halfWidth = 2560 / 2;
        for (short gl = 0; gl <= 94; gl++) {
            int L1 = gl * 2;
            for (int w = 0; w < halfWidth; w++) {
                for (int ch = 0; ch < 3; ch++) {
                    pattern[ch][L1][w] = gl;
                    pattern[ch][L1 + 1][w] = gl;
                    pattern[ch][L1][halfWidth + w] = (short) (512 + gl);
                    pattern[ch][L1 + 1][halfWidth + w] = (short) (512 + gl);
                }
            }
        }

        for (short gl = 95; gl <= 511; gl++) {
            int L1 = 95 * 2 + (gl - 95) * 3;
            for (int w = 0; w < halfWidth; w++) {
                for (int ch = 0; ch < 3; ch++) {
                    pattern[ch][L1][w] = gl;
                    pattern[ch][L1 + 1][w] = gl;
                    if (L1 + 2 < 1440) {
                        pattern[ch][L1 + 2][w] = gl;
                    }

                    pattern[ch][L1][halfWidth + w] = (short) (512 + gl);
                    pattern[ch][L1 + 1][halfWidth + w] = (short) (512 + gl);
                    if (L1 + 2 < 1440) {
                        pattern[ch][L1 + 2][halfWidth + w] = (short) (512 + gl);
                    }
                }

            }
        }

        return pattern;
    }

    public static short[][][] getFHDPattern(short grayLevel) {
        return getWholeFramePattern(grayLevel, 1920, 1080);
    }

    public static short[][][] getWholeFramePattern(short grayLevel, int width,
            int height) {
        short[][][] pattern = new short[3][height][width];
        for (int ch = 0; ch < 3; ch++) {
            for (int h = 0; h < height; h++) {
                java.util.Arrays.fill(pattern[ch][h], grayLevel);
            }
        }
        return pattern;
    }

    public static short[][][] getFHDPattern3_11L_() {
        short[][][] pattern = new short[3][1080][1920];
        short[] grayLevel = new short[] {
                            0, 31, 48, 100, 200, 304, 508, 712, 864, 883, 1023};
        short[] levelWidth = new short[] {
                             176, 172, 172, 172, 172, 172, 172, 172, 172, 172,
                             196};

        int piece = grayLevel.length;
        int start = 0, end = 0;
        for (int p = 0; p < piece; p++) {

            start = end;
            int width = levelWidth[p];
            end += width;

            for (int w = start; w < end; w++) {
                for (int h = 0; h < 1080; h++) {
                    pattern[0][h][w] = grayLevel[p];
                    pattern[1][h][w] = grayLevel[p];
                    pattern[2][h][w] = grayLevel[p];
                }
            }

        }
        return pattern;
    }

    public static short[][][] getWQHDPattern3_11L_() {
        short[][][] pattern = new short[3][2560][1440];
        short[] grayLevel = new short[] {
                            0, 31, 48, 100, 200, 304, 508, 712, 864, 883, 1023};
        short[] levelWidth = new short[] {
                             232, 232, 232, 233, 233, 233, 233, 233, 233, 233,
                             233};

        int piece = grayLevel.length;
        int start = 0, end = 0;
        for (int p = 0; p < piece; p++) {

            start = end;
            int width = levelWidth[p];
            end += width;

            for (int w = start; w < end; w++) {
                for (int h = 0; h < 1440; h++) {
                    pattern[0][h][w] = grayLevel[p];
                    pattern[1][h][w] = grayLevel[p];
                    pattern[2][h][w] = grayLevel[p];
                }
            }

        }
        return pattern;
    }

    public static short[][][] getFHDPattern4_12L() {
        short[][][] pattern = new short[3][1080][1920];
        short[] grayLevel = new short[] {
                            0, 1, 2, 3, 4, 5, 6, 7, 1020, 1021, 1022, 1023};

        int piece = grayLevel.length;
        int pieceWidth = 1920 / piece;
        for (int p = 0; p < piece; p++) {
            for (int w = p * pieceWidth; w < (p + 1) * pieceWidth; w++) {
                for (int h = 0; h < 1080; h++) {
                    pattern[0][h][w] = grayLevel[p];
                    pattern[1][h][w] = grayLevel[p];
                    pattern[2][h][w] = grayLevel[p];
                }
            }
        }
        return pattern;
    }

    public static short[][][] getFHDPattern1_1023L() {
        short[][][] pattern = new short[3][1080][1920];

        for (short x = 0; x <= 127; x++) {
            for (int h = 0; h < 1080; h++) {
                pattern[0][h][x] = x;
                pattern[1][h][x] = x;
                pattern[2][h][x] = x;
            }
        }
        for (short x = 128; x <= 1023; x++) {
            int w = 128 + (x - 128) * 2;
            for (int h = 0; h < 1080; h++) {
                pattern[0][h][w] = x;
                pattern[1][h][w] = x;
                pattern[2][h][w] = x;
                pattern[0][h][w + 1] = x;
                pattern[1][h][w + 1] = x;
                pattern[2][h][w + 1] = x;

            }
        }

        return pattern;
    }

    public static short[][][] getFHDPattern2_1023L_2Vertical() {
        short[][][] pattern = new short[3][1080][1920];
        int halfWidth = 1920 / 2;
//    short[] check = new short[1080];

        for (short gl = 0; gl <= 454; gl++) {
            int L1 = gl * 2;
            for (int w = 0; w < halfWidth; w++) {
                pattern[0][L1][w] = gl;
                pattern[1][L1][w] = gl;
                pattern[2][L1][w] = gl;
                pattern[0][L1 + 1][w] = gl;
                pattern[1][L1 + 1][w] = gl;
                pattern[2][L1 + 1][w] = gl;

                pattern[0][L1][halfWidth + w] = (short) (512 + gl);
                pattern[1][L1][halfWidth + w] = (short) (512 + gl);
                pattern[2][L1][halfWidth + w] = (short) (512 + gl);
                pattern[0][L1 + 1][halfWidth + w] = (short) (512 + gl);
                pattern[1][L1 + 1][halfWidth + w] = (short) (512 + gl);
                pattern[2][L1 + 1][halfWidth + w] = (short) (512 + gl);

            }
        }

        for (short gl = 455; gl <= 511; gl++) {
            int L1 = 455 * 2 + (gl - 455) * 3;
            for (int w = 0; w < halfWidth; w++) {
                pattern[0][L1][w] = gl;
                pattern[1][L1][w] = gl;
                pattern[2][L1][w] = gl;
                pattern[0][L1 + 1][w] = gl;
                pattern[1][L1 + 1][w] = gl;
                pattern[2][L1 + 1][w] = gl;

                pattern[0][L1][halfWidth + w] = (short) (512 + gl);
                pattern[1][L1][halfWidth + w] = (short) (512 + gl);
                pattern[2][L1][halfWidth + w] = (short) (512 + gl);
                pattern[0][L1 + 1][halfWidth + w] = (short) (512 + gl);
                pattern[1][L1 + 1][halfWidth + w] = (short) (512 + gl);
                pattern[2][L1 + 1][halfWidth + w] = (short) (512 + gl);
                if (L1 + 2 < 1080) {

                    pattern[0][L1 + 2][w] = gl;
                    pattern[1][L1 + 2][w] = gl;
                    pattern[2][L1 + 2][w] = gl;

                    pattern[0][L1 + 2][halfWidth + w] = (short) (512 + gl);
                    pattern[1][L1 + 2][halfWidth + w] = (short) (512 + gl);
                    pattern[2][L1 + 2][halfWidth + w] = (short) (512 + gl);
                }
            }
        }

//    for (int x = 0; x < 1080; x++) {
//      check[x] = pattern[0][x][0];
//    }

        return pattern;
    }

    public static short[][][] getFHDPattern5_FRC() {
        short[][][] pattern = new short[3][1080][1920];
        int halfWidth = 1920 / 2;
        short data;

        for (short gl = 0; gl <= 67; gl++) {
            int L1 = gl * 16;
            for (int w = 0; w < halfWidth; w++) {

                for (int x = 0; x < 16; x++) {
                    if ((L1 + x) >= 1080) {
                        break;
                    }
                    for (int ch = 0; ch < 3; ch++) {
                        pattern[ch][L1 + x][w] = gl;
                        pattern[ch][L1 + x][w + halfWidth] = (short) (gl + 957);
                        data = pattern[ch][L1 + x][w + halfWidth];
                        pattern[ch][L1 + x][w + halfWidth]
                                = data > 1023 ? 0 : data;
                    }

                }
            }
        }

        short[] check1 = new short[1080];
        short[] check2 = new short[1080];
        for (int x = 0; x < 1080; x++) {
            check1[x] = pattern[0][x][0];
            check2[x] = pattern[0][x][1919];
        }

        return pattern;
    }
}
