package fiti.cms.frc.impl;

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
public class FRC3_16Util {

    public static String getCostInfo(long cost, long totalcost) {
        if (totalcost > 86400) {
            return "Remain " + (totalcost / 86400.) + "day (" +
                    cost / 1000 + "s)";
        } else if (totalcost <= 86400 && totalcost >= 3600) {
            return "Remain " + (totalcost / 3600.) + "hour (" +
                    cost / 1000 + "s)";
        } else {
            return "Remain " + totalcost + "s (" + cost / 1000 + "s)";
        }

    }

    public static boolean[][] lineToFrame(byte[][] line) {
        int lineHeight = line.length;
        int frameWidth = 8;
        boolean[][] frame = new boolean[lineHeight][frameWidth];
        for (int h = 0; h < lineHeight; h++) {
            int width = line[h].length;
            for (int w = 0; w < width; w++) {
                int wpos = line[h][w];
                frame[h][wpos] = true;
            }
        }
        return frame;
    }

    public static boolean isOverlapping(boolean[][] array1, boolean[][] array2) {
        if (array1.length != array2.length || array1[0].length != array2[0].length) {
            throw new IllegalArgumentException("");
        }
        int height = array1.length;
        int width = array1[0].length;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (true == array1[h][w] && array1[h][w] == array2[h][w]) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOverlapping(Frame f1, Frame f2) {
        return isOverlapping(f1.frameIndex, f2.frameIndex);
    }

    public static boolean isOverlapping(TwoLine t0, TwoLine t1) {
        return isOverlapping(t0.position, t1.position);
    }

    public static boolean isOverlapping(byte[][] array1, byte[][] array2) {
        if (array1.length != array2.length) {
            throw new IllegalArgumentException("");
        }
        int height = array1.length;

        for (int h = 0; h < height; h++) {
            byte[] b1 = array1[h];
            byte[] b2 = array2[h];

            for (int x = 0; x < b1.length; x++) {
                for (int y = 0; y < b2.length; y++) {
                    if (b1[x] == b2[y]) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
