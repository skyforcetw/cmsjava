package auo.cms.ed.impl;

//import math.jwave.transforms.*;
//import math.jwave.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import shu.image.ImageUtils;
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
public class BlueNoise {
    private static double heavisideStepFunction(double x) {
        return (1 + Math.signum(x)) / 2.;
    }

    public static double getPrincipalWavelength(double graylevel) {
        return getPrincipalWavelength(1, graylevel);
    }

    private static double getPrincipalWavelength(double pixelperiod,
                                                 double graylevel) {
        if (graylevel <= 0.5) {
            return pixelperiod / Math.sqrt(graylevel);
        } else if (graylevel > 0.5 && graylevel <= 1) {
            return pixelperiod / Math.sqrt(1 - graylevel);
        } else {
            throw new IllegalStateException();
        }

    }


    public static double[] getBlueNoiseSpectrumProfile(int error) throws
            IOException {
        return getBlueNoiseSpectrumProfile(error, 1);
    }

    public static double[] getBlueNoiseSpectrumProfile(int error,
            double endDistance) throws
            IOException {
        double[][] estimated = getBlueNoiseSpectrum(error);
        double[] profile = FFTUtil.getRadiallyAveragedPowerSpectrums(estimated,
                0, 359, endDistance);
        return profile;
    }


    public static double[][] getBlueNoiseSpectrum(int error) throws IOException {
        String filename = "blue noise/128/1/err" + error + ".eps";
        BufferedImage image = EPSUtil.epsToBufferedImage(filename);
        double[][] dft = FFTUtil.bartlettMethodDFT(image);
        return dft;
    }

    public static double[][] getBlueNoiseSpectrumFromMulti(int error) throws
            IOException {
        BufferedImage[] images = getMultiBlueNoiseImage(128, error);
        int height = images[0].getHeight();
        int width = images[0].getWidth();

        double[][] result = new double[height][width];
        double[][] dft = null;
        int size = images.length;
        for (int x = 0; x < size; x++) {
            dft = FFTUtil.bartlettMethodDFT(images[x]);
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    result[h][w] = result[h][w] + dft[h][w];
                }
            }
        }
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                result[h][w] = result[h][w] / size;
            }
        }

        return result;
    }


    private static BufferedImage getBlueNoiseImage(int error) {
        BufferedImage image = null;
        try {
            image = ImageUtils.loadImageByJAI("blue noise/err" + error + ".tif");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return image;

    }

    private static BufferedImage[] getMultiBlueNoiseImage(int resolution,
            int error) throws IOException {

        File maindir = new File("blue noise/" + resolution + "/");
        File[] dirs = maindir.listFiles();
//        String[] dirnames = maindir.list();
        int size = dirs.length;
        BufferedImage[] result = new BufferedImage[size];
        for (int x = 0; x < size; x++) {
            File dir = dirs[x];
            String filename = dir.getCanonicalPath() + "/err" + error +
                              ".eps";
            result[x] = EPSUtil.epsToBufferedImage(filename);
        }
        return result;

    }

    private final static double MaxRadialFrequency = 1 / Math.sqrt(2);
    private final static double MaxFrequency = MaxRadialFrequency * 2 * Math.PI;

    /**
     *
     * @param graylevel double
     * @return Spectrum
     * @deprecated
     */
    public static Spectrum getStepBlueNoiseSpectrum(double graylevel) {

        double principalFreq = 1. / getPrincipalWavelength(1, graylevel);

//        ArrayList<Double> radialfreq = new java.util.ArrayList<Double>();
//        ArrayList<Double> power = new java.util.ArrayList<Double>();

        int size = ((int) (MaxRadialFrequency / 0.015625)) + 1;
        double[] radialfreq = new double[size];
        double[] power = new double[size];
        int index = 0;
        for (double freq = 0; freq <= MaxRadialFrequency; freq += 0.015625) {
//            double delta = 1; //Stat.poissonPDF(freq, 10);
            double p = heavisideStepFunction(freq - principalFreq);
            radialfreq[index] = freq;
            power[index] = p;
            index++;
//            System.out.println(freq + " " + p);
        }

//        int s = radialfreq.size();

        Spectrum spectrum = new Spectrum(radialfreq, power);
        return spectrum;
    }

    public static void main(String[] args) throws IOException {
        double[][] spectrum = getBlueNoiseSpectrumFromMulti(8);
//        double[][] spectrum = getBlueNoiseSpectrum(1);
//        double[][] period = getPeriodogram(spectrum);
        Plot2D plot = Plot2D.getInstance();
        double[] d = FFTUtil.getDiagonalProfile(spectrum);
        double[] h = FFTUtil.getHorizontalProfile(spectrum);
        double[] v = FFTUtil.getVertialProfile(spectrum);
//        double[] d2 = OstromoukhovAlgorithm.getDiagonalProfile(period);
//        plot.addLinePlot("d", 0, 1, d);
////        plot.addLinePlot("d2", 0, 1, d2);
//        plot.setVisible();
//        plot.addLegend();

        plot.addLinePlot("D", 0, 1 / Math.sqrt(2), d);
//        plot.addLinePlot("dia2", 0, 1, algo.getDiagonalProfile2(spectrum));
        plot.addLinePlot("H", 0, .5, h);
        plot.addLinePlot("V", 0, .5, v);
        plot.setVisible();
        plot.addLegend();
        plot.setAxeLabel(0, "Frequence");
        plot.setAxeLabel(1, "Amplitude");

//        BufferedImage err1 = ImageUtils.loadImageByJAI("blue noise/err1.tif");
//        int width = err1.getWidth();
//        int height = err1.getHeight();
//        int[] pixel = new int[3];
//        double[][] data = new double[height][width];
////        double[][] imagdata = new double[height][width];
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                err1.getRaster().getPixel(x, y, pixel);
////                data[y][x] = pixel[0] == 255 ? 100 : 0;
//                data[y][x] = pixel[0];
////                imagdata[y][x] = 0.0;
//            }
//        }
////        DoubleDHT_2D dht = new DoubleDHT_2D(height, width);
////        dht.forward(data);
////        int a = 1;
////        Opener opener = new Opener();
//        ImagePlus imp = new ImagePlus("", err1);
////        ImagePlus imp = opener.openImage("blue noise/err1.tif");
//        ImageProcessor ip = imp.getProcessor();
//        FFT_ fft = new FFT_();
//        fft.setup("", imp);
////        fft.fft(ip, false);
//        fft.run(ip);
//
////        ip.getBufferedImage();
////        imp.show();
////        FHT fht = new FHT(ip);
////        fht.transform();
//
//        ImageUtils.storeTIFFImage("fht.tif",
//                                  fft.getResultImage().getBufferedImage());
    }
}
