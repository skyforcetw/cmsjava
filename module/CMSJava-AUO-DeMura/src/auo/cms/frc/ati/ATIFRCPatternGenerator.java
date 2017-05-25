package auo.cms.frc.ati;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Arrays;

import shu.cms.colorspace.depend.RGB;
import shu.image.ImageUtils;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ATIFRCPatternGenerator {
    static int[][] PATTERN_13 = new int[][] { {
                                0, 1, 0}, {
                                0, 0, 1}, {
                                1, 0, 0}
    };
    static int[][] PATTERN_12 = new int[][] { {
                                0, 1, 0}, {
                                1, 0, 1}
    };
    static int[][] PATTERN_23 = new int[][] { {
                                1, 0, 1}, {
                                0, 1, 1}, {
                                1, 1, 0}
    };

    public static BufferedImage[] getFRCBufferedImage(BufferedImage original) {
        BufferedImage[] result = new BufferedImage[6];
        for (int x = 0; x < 6; x++) {
            result[x] = getFRCBufferedImage(original, x);
        }
        return result;
    }

    public static BufferedImage getFRCBufferedImage(BufferedImage original,
            int frame) {
        return getFRCBufferedImage(original, PATTERN_13[frame % 3],
                                   PATTERN_12[frame % 2], PATTERN_23[frame % 3]);

    }

    public static BufferedImage getFRCBufferedImage(BufferedImage original,
            int[] pattern1,
            int[] pattern2,
            int[] pattern3) {

        WritableRaster raster = original.getRaster();
        BufferedImage result = get6BitBufferedImage(original);
        WritableRaster raster2 = result.getRaster();
        int w = original.getWidth();
        int h = original.getHeight();
        int[] pixles = new int[3];
        int[][] patterns = new int[][] {
                           pattern1, pattern2, pattern3};

        for (int x = 0; x < h; x++) {
            for (int y = 0; y < w; y++) {
                raster.getPixel(y, x, pixles);
                int mean = (pixles[0] + pixles[1] + pixles[2]) / 3;
                int index = mean % 4;
                if (index == 0) {
                    continue;
                }
                int[] pattern = patterns[index - 1];

                for (int z = 0; z < 3; z++) {
                    int m = x - 1 + z;
                    if (m < 0 || m >= h) {
                        continue;
                    }
                    raster2.getPixel(y, m, pixles);
                    int p = pattern[z];
                    for (int n = 0; n < 3; n++) {
                        pixles[n] += p * 4;
                        pixles[n] = (pixles[n] > 255) ? 255 : pixles[n];
                    }
                    raster2.setPixel(y, m, pixles);
                }
            }
        }
        return result;
    }

    public static BufferedImage get6BitBufferedImage(BufferedImage original) {
        BufferedImage result = ImageUtils.cloneBufferedImage(original);
        WritableRaster raster = result.getRaster();
        int w = original.getWidth();
        int h = original.getHeight();
        int[] pixles = new int[3];

        for (int x = 0; x < h; x++) {
            for (int y = 0; y < w; y++) {
                raster.getPixel(y, x, pixles);
                for (int m = 0; m < 3; m++) {
                    pixles[m] = (pixles[m] >> 2) << 2; ;
//          int a = 1;
                }
                raster.setPixel(y, x, pixles);
            }
        }
        return result;
    }

    static BufferedImage getGrayScaleImage(int height) {
        BufferedImage img = new BufferedImage(256, height,
                                              BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = img.getRaster();
        int[] pixles = new int[3];
        for (int x = 0; x < 256; x++) {
            pixles[0] = x;
            pixles[1] = x;
            pixles[2] = x;
            for (int y = 0; y < height; y++) {
                raster.setPixel(x, y, pixles);
            }
        }
        return img;
    }

    static BufferedImage getGrayLevelBlockImage(int grayLevel) {
        BufferedImage img = new BufferedImage(3, 3,
                                              BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = img.getRaster();
        int[] pixles = new int[3];
        pixles[0] = grayLevel;
        pixles[1] = grayLevel;
        pixles[2] = grayLevel;

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                raster.setPixel(x, y, pixles);
            }

        }
        return img;
    }

    static BufferedImage getRGBCMYWGrayScaleImage() {
        int height = 7;
        BufferedImage img = new BufferedImage(256, height * 7,
                                              BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = img.getRaster();
        int[] pixles = new int[3];
        for (int x = 0; x < 256; x++) {
            for (RGB.Channel ch : RGB.Channel.RGBYMCWChannel) {
                RGB.Channel[] chs = RGB.Channel.getPrimaryColorChannel(ch);
                Arrays.fill(pixles, 0);
                for (RGB.Channel ch2 : chs) {
                    int index = ch2.getArrayIndex();
                    pixles[index] = x;
                }

                for (int y = 0; y < height; y++) {
                    raster.setPixel(x, ch.getArrayIndex() * height + y, pixles);
                }

            }
//      pixles[0] = x;
//      pixles[1] = x;
//      pixles[2] = x;
//      for (int y = 0; y < 7; y++) {
//        raster.setPixel(x, y, pixles);
//      }
        }
        return img;
    }

    public static void main(String[] args) {
        try {
//      BufferedImage grayscale = getRGBCMYWGrayScaleImage();
            BufferedImage grayscale = getGrayLevelBlockImage(130);
            ImageUtils.storeTIFFImage("256grayscale.tif", grayscale);

//      BufferedImage img = ImageUtils.loadImage("win7icon2.bmp");
            BufferedImage[] imgs = getFRCBufferedImage(grayscale);
//      BufferedImage[] imgs = getFRCBufferedImage(img);
            for (int x = 0; x < imgs.length; x++) {
                BufferedImage frc = imgs[x];
                ImageUtils.storeTIFFImage("frc/frc_" + Integer.toString(x) + ".tif",
                                          frc);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
