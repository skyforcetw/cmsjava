package auo.cms.frc.impl;

import java.io.Serializable;
import java.util.Arrays;

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
public class Frame implements Serializable {
    public TwoLine[] twoLine;
    public byte[][] frameIndex;
    public Frame(byte[][] frameIndex, TwoLine t1, TwoLine t2, TwoLine t3, TwoLine t4) {
        this.frameIndex = frameIndex;
        this.twoLine = new TwoLine[] {t1, t2, t3, t4};
    }


    public Frame(boolean[][] frame) {
        frameIndex = toIndex(frame);
    }

    static byte[][] toIndex(boolean[][] frame) {
        int height = frame.length;
        int width = frame[0].length;

        byte[][] result = new byte[height][];
        byte[] tmp = new byte[width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                tmp[w] = 0;
            }
            int index = 0;
            for (int w = 0; w < width; w++) {

                if (frame[h][w]) {
                    tmp[index++] = (byte) w;
                }
            }
            result[h] = new byte[index];
            for (int x = 0; x < index; x++) {
                result[h][x] = tmp[x];
            }
        }
        return result;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     * @todo Implement this java.lang.Object method
     */
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (byte[] index : frameIndex) {
            buf.append(Arrays.toString(index));
            buf.append('\n');
        }

        return buf.toString();
    }

}
