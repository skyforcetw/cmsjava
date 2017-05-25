package auo.mura.etc;

import auo.mura.img.MuraImageUtils;
import shu.image.ImageUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import auo.mura.img.PatternGen;

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
public class rbImageProducer {

    static double getRGr(short r, short g, short b) {
        return ((double) r) / (r + g + b);
    }

    static double getRGg(short r, short g, short b) {
        return ((double) g) / (r + g + b);
    }

    public static void main(String[] args) throws IOException {
//        String imagefilename = "D:/ณnล้/nobody zone/DMC Experiment/20140627_DMC Result/20140627_40 100 128_DG default_(Ratio 0 0 0)_i5/20140627--0002_5_0100.bmp";
        String imagefilename = "\\\\10.84.33.53/dmc/FHD_Color_DeMura/20140627--0025_5_0100.bmp";
        BufferedImage image = ImageUtils.loadImage(imagefilename);
        MuraImageUtils utils = new MuraImageUtils(10, image.getWidth(), image.getHeight());
        short[][][] image10Bit = utils.get10BitImageData(image, false);

        int height = image10Bit[0].length;
        int width = image10Bit[0][0].length;
        short[][][] rgImage10Bbit = new short[3][height][width];
        double RGr, RGg;
        short r, g, b;

        short centerR = image10Bit[0][height / 2][width / 2];
        short centerG = image10Bit[0][height / 2][width / 2];
        short centerB = image10Bit[0][height / 2][width / 2];
        double centerr = getRGr(centerR, centerG, centerB);
        double centerg = getRGg(centerR, centerG, centerB);
        double rmaxroffset = 0;
        double gmaxroffset = 0;
        double rminroffset = Double.MAX_VALUE;
        double gminroffset = Double.MAX_VALUE;
        int base = 128;
//        int offsetbase = 16000;
        int offsetbase = 6000;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                r = image10Bit[0][h][w];
                g = image10Bit[1][h][w];
                b = image10Bit[2][h][w];
                RGr = getRGr(r, g, b);
                RGg = getRGg(r, g, b);

                double roffset = (RGr - centerr) * offsetbase;
                double goffset = (RGg - centerg) * offsetbase;

                rmaxroffset = Math.max(rmaxroffset, roffset);
                gmaxroffset = Math.max(gmaxroffset, goffset);
                rminroffset = Math.min(rminroffset, roffset);
                gminroffset = Math.min(gminroffset, goffset);

//                System.out.println(roffset + " " + goffset);
                rgImage10Bbit[0][h][w] = (short) Math.round(base * 4 + roffset);
                rgImage10Bbit[1][h][w] = (short) Math.round(base * 4 + goffset);
                rgImage10Bbit[2][h][w] = (short) (base * 4);

            }
        }
        System.out.println("r " + centerr);
        System.out.println("g " + centerg);
        System.out.println("r offset: " + rmaxroffset + " " + rminroffset);
        System.out.println("g offset: " + gmaxroffset + " " + gminroffset);
        PatternGen.clip(rgImage10Bbit, (short) 1020);
        utils.store8BitImageBMP(rgImage10Bbit, "rg/rg.bmp");
    }
}
