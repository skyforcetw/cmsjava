package auo.mura.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
public class ArrayUtils {
    public static void remap(short[][][] image, int a, int aprime, int b,
                             int bprime) {
        int height = image[0].length;
        int width = image[0][0].length;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                short d = image[0][h][w];

                if (d == a) {
                    image[0][h][w] = image[1][h][w] = image[2][h][w] =
                            (short) aprime;
                } else if (d == b) {
                    image[0][h][w] = image[1][h][w] = image[2][h][w] =
                            (short) bprime;
                }
            }
        }

    }

    public static long sum(short[][] data) {
        int height = data.length;
        int width = data[0].length;
        long sum = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                sum += data[h][w];
            }
        }
        return sum;
    }

    public static void print(short[][] data, boolean showstar) {
        int height = data.length;
        int width = data[0].length;
        String v;
//        int heightcount =  Math.lo
        int heightcount = (int) Math.log10(height);

        for (int h = 0; h < height; h++) {
            String L = MuraImageUtils.fillZero(Integer.toString(h + 1),
                                               heightcount);
            System.out.print("L" + L + ":");
            for (int w = 0; w < width; w++) {
                v = Short.toString(data[h][w]);
                String vfill = MuraImageUtils.fillZero(v, 2);
                if (showstar) {
//                    System.out.print(vfill.equals("16") ? "*" : ".");
                    System.out.print(vfill.equals("16") ? "* " : ". ");
                } else {
                    System.out.print(vfill + " ");
                }
            }
            System.out.println("");
        }
    }

    public static void printWithError(short[][] data, short[][] error,
                                      short[][] threshold, boolean showstar, boolean printToSTIO) {
        int height = data.length;
        int width = data[0].length;
        String v;
        int sum = 0;
        int heightcount = (int) Math.log10(height) + 1;
        StringBuilder buf = new StringBuilder();

        for (int h = 0; h < height; h++) {

            buf.append("L" +
                       MuraImageUtils.fillZero(Integer.toString(h + 1),
                                               heightcount) + ":");
            for (int w = 0; w < width; w++) {
//                v = Short.toString(data[h][w]);
                v = Integer.toHexString(data[h][w]);
                String vfill = MuraImageUtils.fillZero(v, 2);
                if (showstar) {
                    if (vfill.equals("16")) {
                        buf.append("*");
                        sum++;
                    } else {
                        buf.append(".");
                    }

                } else {
                    buf.append(vfill + " ");
                }
            }

            if (null != error) {
                buf.append(" ||E ");
                for (int w = 0; w < width; w++) {
                    v = Integer.toHexString(error[h][w]);
                    v = v.equals("10") ? "g" : v;
                    v = v.equals("11") ? "h" : v;
                    v = v.equals("12") ? "i" : v;
                    v = v.equals("13") ? "j" : v;
                    v = v.equals("14") ? "k" : v;
                    v = v.equals("15") ? "L" : v;
                    if (16 == data[h][w]) {
                        buf.append(v + "_");
                    } else {
                        buf.append(v + " ");
                    }

                }
            }

            if (null != error) {
                buf.append(" ||DE ");
                for (int w = 1; w < width; w++) {
                    int delta = error[h][w] - error[h][w - 1];
                    delta = Math.abs(delta);
                    v = Integer.toHexString(delta);
                    if (16 == data[h][w]) {
                        buf.append(v + "_");
                    } else {
                        buf.append(v + " ");
                    }

                }
            }
            if (null != threshold) {
                buf.append(" ||T ");
                for (int w = 0; w < width; w++) {
                    int t = threshold[h][w];
                    v = Integer.toHexString(t);
                    if (16 == data[h][w]) {
                        buf.append(v + "_");
                    } else {
                        buf.append(v + " ");
                    }

                }
            }

            buf.append("\n");
        }
        buf.append("sum: " + sum);
        buf.append("\n");
        String output = buf.toString();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("print.txt"));
            writer.write(output);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (printToSTIO) {
            System.out.println(output);
        }
    }

    public static void copy(short[][] source, short[][] dest) {
        int height = source.length;
        int size = source[0].length;
        for (int h = 0; h < height; h++) {
            System.arraycopy(source[h], 0, dest[h], 0, size);
        }
    }

    public static void copy(short[][] source, int[][] dest) {
        int height = source.length;
        int width = source[0].length;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                dest[h][w] = source[h][w];
            }
        }
    }

    public static double[][] copyToDouble(short[][] source) {
        int height = source.length;
        int width = source[0].length;
        double[][] result = new double[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[h][w] = source[h][w];
            }
        }
        return result;
    }


    public static void convert8BitTo10Bit(short[][][] data) {
        int d1 = data.length;
        int d2 = data[0].length;
        int d3 = data[0][0].length;
        int gain = (int) Math.pow(2, 10 - 8);
        for (int ch = 0; ch < d1; ch++) {
            for (int y = 0; y < d2; y++) {
                for (int x = 0; x < d3; x++) {
                    data[ch][y][x] *= gain;
                }
            }
        }
    }


    public static int[][][] copyToInt(short[][][] data) {
        int d1 = data.length;
        int d2 = data[0].length;
        int d3 = data[0][0].length;
        int[][][] result = new int[d1][d2][d3];
        for (int x = 0; x < d1; x++) {
            copy(data[x], result[x]);
        }
        return result;

    }

    public static short[][][] copy(short[][][] data) {
        int d1 = data.length;
        int d2 = data[0].length;
        int d3 = data[0][0].length;
        short[][][] result = new short[d1][d2][d3];
        for (int x = 0; x < d1; x++) {
            copy(data[x], result[x]);
        }
        return result;
    }

    public static short[][] copy(short[][] data) {
        int d1 = data.length;
        int d2 = data[0].length;
        short[][] result = new short[d1][d2];
        copy(data, result);
        return result;
    }

    public static void convert12BitTo16Bit(int[][][] data) {
        int d1 = data.length;
        int d2 = data[0].length;
        int d3 = data[0][0].length;
        int gain = (int) Math.pow(2, 16 - 12);
        for (int ch = 0; ch < d1; ch++) {
            for (int y = 0; y < d2; y++) {
                for (int x = 0; x < d3; x++) {
                    data[ch][y][x] *= gain;
                }
            }
        }
    }

    public static void convert10BitTo12Bit(short[][][] data) {
        int d1 = data.length;
        int d2 = data[0].length;
        int d3 = data[0][0].length;
        int gain = (int) Math.pow(2, 12 - 10);
        for (int ch = 0; ch < d1; ch++) {
            for (int y = 0; y < d2; y++) {
                for (int x = 0; x < d3; x++) {
                    data[ch][y][x] *= gain;
                }
            }
        }
    }

    public static void convert12BitTo10Bit(short[][][] data) {
        int d1 = data.length;
        int d2 = data[0].length;
        int d3 = data[0][0].length;
//        int gain = (int) Math.pow(2, 12 - 10);
        for (int ch = 0; ch < d1; ch++) {
            for (int y = 0; y < d2; y++) {
                for (int x = 0; x < d3; x++) {
                    data[ch][y][x] = (short) (data[ch][y][x] / 4);
                }
            }
        }
    }


    public static boolean compare(short[][][][] r0, short[][][][] r1) {
        int d0 = r0.length;
        int d1 = r0[0].length;
        int d2 = r0[0][0].length;
        int d3 = r0[0][0][0].length;
        for (int c0 = 0; c0 < d0; c0++) {
            for (int c1 = 0; c1 < d1; c1++) {
                for (int c2 = 0; c2 < d2; c2++) {
                    for (int c3 = 0; c3 < d3; c3++) {
                        if (r0[c0][c1][c2][c3] != r1[c0][c1][c2][c3]) {
                            return false;
                        }
                    }

                }
            }

        }
        return true;
    }

    public static short[][][] one2RGB(short[][] one) {
        int height = one.length;
        int width = one[0].length;
        int width13 = width / 3;

        short[][][] result = new short[3][height][width13];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width13; w++) {
                result[0][h][w] = one[h][w * 3];
                result[1][h][w] = one[h][w * 3 + 1];
                result[2][h][w] = one[h][w * 3 + 2];
            }
        }
        return result;
    }

    public static short[][] rgb2One(short[][][] rgbImage12bit) {
        int height = rgbImage12bit[0].length;
        int width = rgbImage12bit[0][0].length;
        short[][] result = new short[height][width * 3];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[h][w * 3] = rgbImage12bit[0][h][w];
                result[h][w * 3 + 1] = rgbImage12bit[1][h][w];
                result[h][w * 3 + 2] = rgbImage12bit[2][h][w];
            }
        }
        return result;
    }

    public static boolean[][][] inverse(boolean[][][] pattern) {
        int frame = pattern.length;
        int height = pattern[0].length;
        int width = pattern[0][0].length;
        boolean[][][] result = new boolean[frame][height][width];
        for (int count = 0; count < frame; count++) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    result[count][h][w] = !pattern[count][h][w];
                }
            }
        }

        return result;
    }

    public static void checkColumn1(short[][] image, int col) {
        int height = image.length;
        short[] column = new short[height];
        for (int h = 0; h < height; h++) {
            column[h] = image[h][col];
        }
        int a = 1;
    }

}
