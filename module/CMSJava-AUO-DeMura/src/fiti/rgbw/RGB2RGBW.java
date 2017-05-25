package fiti.rgbw;

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
public class RGB2RGBW {
    public static void main(String[] args) {
        for (int r = 0; r <= 256; r += 128) {
            for (int g = 0; g <= 256; g += 128) {
                for (int b = 0; b <= 256; b += 128) {
                    int r0 = r == 256 ? 255 : r;
                    int g0 = g == 256 ? 255 : g;
                    int b0 = b == 256 ? 255 : b;
                    System.out.println(r0 + " " + g0 + " " + b0);
                }
            }

        }
    }
}
