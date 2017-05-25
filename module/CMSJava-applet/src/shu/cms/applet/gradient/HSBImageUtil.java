package shu.cms.applet.gradient;

import java.io.*;

import java.awt.*;
import java.awt.image.*;

//import shu.cms.colorspace.ColorSpace;
//import shu.cms.colorspace.depend.*;
import shu.image.*;
import shu.math.Maths;

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
public abstract class HSBImageUtil {
    private HSBImageUtil() {

    }

    public final static void fillRectHSBImage(boolean R, boolean G,
                                              boolean B, BufferedImage image,
                                              boolean saturationChange) {
        int height = image.getHeight();
        int width = image.getWidth();

        double[] hsbValues = new double[3];
        int xEnd = width / 3;

        double[] hueVariance = new double[xEnd];
        double[] yVariance = new double[height];
        for (int x = 0; x < xEnd; x++) {
            hueVariance[x] = (((double) x) / width) * 360;
        }

        for (int y = 0; y < height; y++) {
            yVariance[y] = (((double) y) / height);
        }

        int codeR, codeG, codeB;
        for (int x = 0; x < xEnd; x++) {
            double h = (((double) x) / width) * 360;
            for (int y = 0; y < height; y++) {
                hsbValues[0] = h;
                if (saturationChange) {
                    hsbValues[1] = yVariance[y];
                    hsbValues[2] = 1;
                } else {
                    hsbValues[1] = 1;
                    hsbValues[2] = yVariance[y];
                }

                HSV.Sandbox.fastToRGBValues(hsbValues);
                codeR = R ? (int) (hsbValues[0]) : 0;
                codeG = G ? (int) (hsbValues[1]) : 0;
                codeB = B ? (int) (hsbValues[2]) : 0;

                //0~120
                image.setRGB(x, y, ((codeR << 16) | (codeG << 8) | codeB));
                //120~240
                image.setRGB(xEnd + x, y, ((codeB << 16) | (codeR << 8) | codeG));
                //240~360
                image.setRGB(xEnd * 2 + x, y,
                             ((codeG << 16) | (codeB << 8) | codeR));
            }
        }

    }

    public static void main(String[] args) {
        BufferedImage image = new BufferedImage(3840, 2160,
                                                BufferedImage.TYPE_INT_RGB);
//        fillCircleHSBImageWall(image, 3, 3);
        fillCircleHSBImage(true, true, true, image);
//        fillRectHSBImage(true,true,true,image,false);
        halfSwap(image);
        try {
//            ImageUtils.storeTIFFImage("multi-hue.tif", image);
            ImageUtils.storeBMPImage("hue.bmp", image);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static void halfSwap(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        int halfwidth = width / 2;
        int rgb, rgb2;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < halfwidth; w++) {
                rgb = image.getRGB(w, h);
                rgb2 = image.getRGB(w + halfwidth, h);

                image.setRGB(w, h, rgb2);
                image.setRGB(w + halfwidth, h, rgb);
            }
        }

    }

    public final static void fillCircleHSBImageWall(BufferedImage image,
            int hcount, int vcount) {
        int totalcount = hcount * vcount;
        int width = image.getWidth();
        int height = image.getHeight();
        int widthPiece = width / hcount;
        int heightPiece = height / vcount;
        int index = totalcount;

        for (int x = 0; x < hcount; x++) {
            for (int y = 0; y < vcount; y++) {
                int xx = x * widthPiece;
                int yy = y * heightPiece;
                BufferedImage subimage = image.getSubimage(xx, yy, widthPiece,
                        heightPiece);
                double value = ((double) index) / totalcount;
                fillCircleHSBImage(true, true, true, subimage, value);
                index--;
            }
        }
    }

    public final static void fillCircleHSBImage(boolean R, boolean G,
                                                boolean B, BufferedImage image) {
        fillCircleHSBImage(R, G, B, image, 1);
    }

    public final static void fillCircleHSBImage(boolean R, boolean G,
                                                boolean B, BufferedImage image,
                                                double value) {

        int height = image.getHeight();
        int width = image.getWidth();

        int half = (height / 2) - 1;

        double[] hsbValues = new double[3];
        hsbValues[2] = 1;
        double[] LabValues = new double[3];
        LabValues[1] = half;
        double maxRadial = ColorSpace.fastCartesian2RadialValues(LabValues);
        int xOriginal = (width - height) / 2;
        int xEnd = height + xOriginal;
        int yEnd = height / 2;
        yEnd = (height % 2 == 1) ? yEnd + 1 : yEnd;
        int xHalf = half + xOriginal;
        int codeR, codeG, codeB;
        double radial = -1;

//    Graphics g = image.getGraphics();
//    g.setColor(Color.white);
//    g.fillRect(0, 0, width, height);

        for (int x = xOriginal; x < xEnd; x++) {
            for (int y = 0; y < yEnd; y++) {
                LabValues[1] = x - xHalf;
                LabValues[2] = -(y - half);
                //еbо|
                radial = ColorSpace.fastCartesian2RadialValues(LabValues);
                if (radial <= maxRadial) {

                    //====================================================================
                    // part1
                    //====================================================================
                    //идл╫

                    hsbValues[0] = ColorSpace.fastCartesian2AngularValues(
                            LabValues);
                    hsbValues[1] = radial / maxRadial;
                    hsbValues[2] = value;

                    HSV.Sandbox.fastToRGBValues(hsbValues);
                    codeR = R ? (int) (hsbValues[0]) : 0;
                    codeG = G ? (int) (hsbValues[1]) : 0;
                    codeB = B ? (int) (hsbValues[2]) : 0;

                    image.setRGB(x, y, ((codeR << 16) | (codeG << 8) | codeB));
                    image.setRGB(x, height - y - 1,
                                 ((codeR << 16) | (codeB << 8) | codeG));
                    //====================================================================

                }

            }
        }
    }
}


class HSV {
    public static class Sandbox {

        private static double[] RGBValues = new double[3];
        private static double bmax, bmid, bmin, q, h, s, b;

        public static void fastToRGBValues(double[] hsvValues) {
            h = hsvValues[0];
            s = hsvValues[1];
            b = hsvValues[2];

            bmax = (b * 255);
            bmin = (1 - s) * bmax;
            q = (bmax - bmin) / 60;

            int hIndex = (int) (h / 60);
            switch (hIndex) {
            case 0:
                bmid = (h - 0) * q + bmin;
                RGBValues[0] = bmax;
                RGBValues[1] = bmid;
                RGBValues[2] = bmin;
                break;
            case 1:
                bmid = -(h - 120) * q + bmin;
                RGBValues[0] = bmid;
                RGBValues[1] = bmax;
                RGBValues[2] = bmin;
                break;
            case 2:
                bmid = (h - 120) * q + bmin;
                RGBValues[0] = bmin;
                RGBValues[1] = bmax;
                RGBValues[2] = bmid;
                break;
            case 3:
                bmid = -(h - 240) * q + bmin;
                RGBValues[0] = bmin;
                RGBValues[1] = bmid;
                RGBValues[2] = bmax;
                break;
            case 4:
                bmid = (h - 240) * q + bmin;
                RGBValues[0] = bmid;
                RGBValues[1] = bmin;
                RGBValues[2] = bmax;
                break;
            case 5:
                bmid = -(h - 360) * q + bmin;
                RGBValues[0] = bmax;
                RGBValues[1] = bmin;
                RGBValues[2] = bmid;
                break;
            }

            hsvValues[0] = RGBValues[0];
            hsvValues[1] = RGBValues[1];
            hsvValues[2] = RGBValues[2];
        }
    }

}


class ColorSpace {
    private static double[] PolarValues = new double[3];
    private static double t1, t2;
    private final static double PI180 = (180.0 / Math.PI);

    public static final double fastCartesian2AngularValues(final double[]
            cartesianValues) {
        t1 = cartesianValues[1];
        t2 = cartesianValues[2];
        double angular = 0;
        if (t1 == 0 && t2 == 0) {
            angular = 0;
        } else {
            angular = Math.atan2(t2, t1);
        }

        angular *= PI180;
        while (PolarValues[2] >= 360.0) { // Not necessary, but included as a check.
            angular -= 360.0;
        } while (PolarValues[2] < 0) {
            angular += 360.0;
        }
        return angular;
    }

    public static final double fastCartesian2RadialValues(final double[]
            cartesianValues) {

        return Math.sqrt(Maths.sqr(cartesianValues[1]) +
                         Maths.sqr(cartesianValues[2]));
    }

}
