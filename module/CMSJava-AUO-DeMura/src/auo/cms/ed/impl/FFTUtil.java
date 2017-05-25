package auo.cms.ed.impl;

import shu.image.IntegerImage;
import java.awt.image.BufferedImage;
import shu.math.lut.Interpolation2DLUT;
import ij.process.ShortProcessor;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import java.io.IOException;
import ij.process.FloatProcessor;
import shu.image.ImageUtils;
import shu.cms.colorspace.ColorSpace;
import shu.math.array.DoubleArray;
import shu.plot.Plot2D;

import auo.cms.ed.*;

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
public class FFTUtil {
    public static double[] getHorizontalProfile(double[][] dft) {
        int height = dft.length;
        int halfheight = height / 2;
        double[] result = new double[halfheight];
        for (int x = 1; x < halfheight; x++) {
            result[x] = dft[halfheight][halfheight + x];
        }
        return result;
    }

    public static double[] getVertialProfile(double[][] dft) {
        int height = dft.length;
        int halfheight = height / 2;
        double[] result = new double[halfheight];
        for (int x = 1; x < halfheight; x++) {
            result[x] = dft[halfheight - x][halfheight];
        }
        return result;
    }

    public static double[] getDiagonalProfile(double[][] dft) {
        int height = dft.length;
        int halfheight = height / 2;
        double[] result = new double[halfheight];
        for (int x = 1; x < halfheight; x++) {
            result[x] = dft[halfheight - x][halfheight + x];
        }
        return result;
    }

    private static int BartlettMethodSlice = 4;
    public final static void setBartlettMethodSlice(int slice) {
        BartlettMethodSlice = slice;
    }

    public static double[][] bartlettMethodDFT(BufferedImage image) {
        return bartlettMethodDFT(image, BartlettMethodSlice);
    }

    public static double[][] bartlettMethodDFT(BufferedImage image, int K) {
        int slice = K * K;
        int height = image.getHeight();
        int width = image.getWidth();
        int heightslice = height / K;
        int widthslice = width / K;
        IntegerImage[] images = new IntegerImage[slice];

        int index = 0;
        int[] pixels = new int[3];
        for (int hindex = 0; hindex < K; hindex++) {
            for (int windex = 0; windex < K; windex++) {
                images[index] = new IntegerImage(widthslice, heightslice);
                for (int h0 = 0; h0 < heightslice; h0++) {
                    for (int w0 = 0; w0 < widthslice; w0++) {
                        int h = h0 + heightslice * hindex;
                        int w = w0 + widthslice * windex;
                        image.getRaster().getPixel(w, h, pixels);
                        images[index].setPixel(w0, h0, pixels);
                    }
                }
                index++;
            }

        }

        double[][][] dft = new double[slice][][];
        double[][] interpdft = null;
        double[][] result = new double[height][width];
        for (int x = 0; x < slice; x++) {
            dft[x] = discreteFourierTransform(images[x].getBufferedImage());
            interpdft = interpolate2D(dft[x], K);

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    result[h][w] = result[h][w] + interpdft[h][w];
                }
            }

        }
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[h][w] = result[h][w] / slice;
            }
        }
        return result;
    }

    public static double[][] bartlettMethodDFT(short[][] image, int K) {
        int slice = K * K;
        int height = image.length;
        int width = image[0].length;
        int heightslice = height / K;
        int widthslice = width / K;
        IntegerImage[] images = new IntegerImage[slice];

        int index = 0;
        int[] pixels = new int[3];
        for (int hindex = 0; hindex < K; hindex++) {
            for (int windex = 0; windex < K; windex++) {
                images[index] = new IntegerImage(widthslice, heightslice);
                for (int h0 = 0; h0 < heightslice; h0++) {
                    for (int w0 = 0; w0 < widthslice; w0++) {
                        int h = h0 + heightslice * hindex;
                        int w = w0 + widthslice * windex;
//                        image.getRaster().getPixel(w, h, pixels);
                        short pixel = image[h][w];
                        pixels[0] = pixels[1] = pixels[2] = pixel;
                        images[index].setPixel(w0, h0, pixels);
                    }
                }
                index++;
            }

        }

        double[][][] dft = new double[slice][][];
        double[][] interpdft = null;
        double[][] result = new double[height][width];
        for (int x = 0; x < slice; x++) {
            dft[x] = discreteFourierTransform(images[x].getBufferedImage());
            interpdft = interpolate2D(dft[x], K);

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    result[h][w] = result[h][w] + interpdft[h][w];
                }
            }

        }
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[h][w] = result[h][w] / slice;
            }
        }
        return result;
    }

    public static boolean StoreDFTImage = false;
    public static double[][] discreteFourierTransform(BufferedImage image) {
        ImagePlus imp = new ImagePlus("", image);
        ImageProcessor ip = imp.getProcessor();
        FFT_ fft = new FFT_();
        fft.setup("", imp);
        fft.run(ip);

        ImagePlus resultImage = fft.getResultImage();
        ImageProcessor processor = resultImage.getProcessor();
        BufferedImage fftimage = null;
        if (processor instanceof ShortProcessor) {
            fftimage = ((ShortProcessor) processor).get16BitBufferedImage();
        } else if (processor instanceof FloatProcessor) {
            fftimage = ((FloatProcessor) processor).getBufferedImage();
        } else {
            fftimage = resultImage.getBufferedImage();
        }

        if (StoreDFTImage) {
            try {
                ImageUtils.storeTIFFImage("fht.tif", fftimage);

            } catch (IOException ex) {
            }
        }

        int height = fftimage.getHeight();
        int width = fftimage.getWidth();
        int[] pixel = new int[3];
        double[][] result = new double[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                fftimage.getRaster().getPixel(w, h, pixel);
                result[h][w] = pixel[0];
            }
        }

        return result;
    }

    private static double[][] interpolate2D(double[][] original, int times) {
        int height = original.length;
        int width = original[0].length;
        double[] xkeys = new double[height];
        double[] ykeys = new double[width];
        for (int x = 0; x < height; x++) {
            xkeys[x] = x;
        }
        for (int x = 0; x < width; x++) {
            ykeys[x] = x;
        }
        Interpolation2DLUT lut = new Interpolation2DLUT(xkeys, ykeys, original,
                Interpolation2DLUT.Algo.BILINEAR);

        int newheight = height * times;
        int newwidth = width * times;
        double[][] result = new double[newheight][newwidth];
        for (int h0 = 0; h0 < newheight; h0++) {
            for (int w0 = 0; w0 < newwidth; w0++) {
                double h = ((double) h0) / (newheight - 1) * (height - 1);
                double w = ((double) w0) / (newwidth - 1) * (width - 1);
                result[h0][w0] = lut.getValue(h, w);
            }
        }

        return result;
    }

    public static double[] getRadiallyAveragedPowerSpectrums(double[][] dft,
            int startDegree, int endDegree) {
        return getRadiallyAveragedPowerSpectrums(dft, startDegree, endDegree, 1);
    }

    public static double[] getRadiallyAveragedPowerSpectrums(double[][] dft,
            int startDegree, int endDegree, double endDistance) {
        int size = dft.length;
        int halfsize = size / 2;
        double[] result = new double[halfsize];

        double[] keys = new double[size];
        for (int x = 0; x < size; x++) {
            keys[x] = x;
        }
        Interpolation2DLUT lut = new Interpolation2DLUT(keys, keys, dft,
                Interpolation2DLUT.Algo.BILINEAR);
        int enddist = (int) Math.round(halfsize * endDistance);

        for (double degree = startDegree; degree <= endDegree; degree++) {
            double d = degree < 0 ? 360 + degree : degree;
            for (int dist = 0; dist < enddist; dist++) {
                double[] xy = ColorSpace.polar2cartesianCoordinatesValues(dist,
                        d);
                double x = halfsize + xy[0];
                double y = halfsize + xy[1];
                double v = lut.getValue(y, x);
                result[dist] = result[dist] + v;
            }

        }
        int delta = endDegree - startDegree + 1;
        for (int dist = 0; dist < halfsize; dist++) {
            result[dist] = result[dist] / delta;
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        double[][] dft = BlueNoise.getBlueNoiseSpectrum(1);
        double[] curve = getRadiallyAveragedPowerSpectrums(dft, 0, 15);

        Plot2D plot = Plot2D.getInstance();
        plot.addLinePlot("", 0, 1, curve);
        plot.setVisible();
    }

}
