package auo.cms.ed.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;



import auo.mura.util.ArrayUtils;
import auo.mura.img.MuraImageUtils;
import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import shu.image.ImageUtils;
import shu.image.IntegerImage;
import shu.math.array.DoubleArray;
import shu.math.array.IntArray;
import shu.math.lut.Interpolation1DLUT;
import shu.plot.Plot2D;

import auo.cms.ed.*;

//import math.jwave.*;
//import math.jwave.transforms.*;

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
public class OstromoukhovAlgorithm {
    public OstromoukhovAlgorithm() {
    }


//    private Transform t;

    private double[] interpolate(double[] blueNoise, int times) {
        int size = blueNoise.length;
        int newsize = times * size;
        double[] result = new double[newsize];
        double[] keys = new double[size];
        for (int x = 0; x < size; x++) {
            keys[x] = x;
        }
        Interpolation1DLUT lut = new Interpolation1DLUT(keys, blueNoise,
                Interpolation1DLUT.Algo.LINEAR);
        for (int x = 0; x < newsize; x++) {
            double key = ((double) x) / (newsize - 1) * (size - 1);
            double v = lut.getValue(key);
            result[x] = v;
        }
        return result;
    }


//    /**
//     *
//     * @param edif ErrorDiffusionIF
//     * @param error int
//     * @param testSize int
//     * @param sum int
//     * @param w double
//     * @return short[]
//     * @throws IOException
//     * @deprecated
//     */
//    public short[] getOptimumWeight3(final ErrorDiffusionIF edif, int error,
//                                     int testSize, final int sum,
//                                     final double w) throws
//            IOException {
//        final double endDistance = 1;
//        final double[] bluenoise = interpolate(BlueNoise.
//                                               getBlueNoiseSpectrumProfile(
//                error, endDistance), testSize / 128);
//
//        MultivariateFunction func = new MultivariateFunction() {
//            public double value(double[] doubleArray) {
//                return getIndex(doubleArray, false, sum, edif, bluenoise, w);
//            }
//
//        };
//
//        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
//        MultivariateFunctionMappingAdapter mapfunc = new
//                MultivariateFunctionMappingAdapter(func, new double[] {0, 0},
//                new double[] {1, 1});
//        LinearConstraint constraint = new LinearConstraint(new double[] {1, 1},
//                Relationship.LEQ, 1);
//        LinearConstraintSet set = new LinearConstraintSet(constraint);
//
//        final PointValuePair optimum
//                = optimizer.optimize(new MaxEval(200),
//                                     new ObjectiveFunction(func),
//                                     GoalType.MINIMIZE,
//                                     new InitialGuess(new double[] {1 / 3.,
//                1 / 3.}),
//                                     new NonNegativeConstraint(true),
//                                     new NelderMeadSimplex(2), set
//                  );
//        double[] param = optimum.getPoint();
//        minimum = optimum.getValue();
//
//        short[] weight = paramToWeight(param, sum);
//
//        return weight;
//
//    }

    private double[] getBlueNoise(int error, int testSize) throws IOException {
        return interpolate(BlueNoise.getBlueNoiseSpectrumProfile(error, 1),
                           testSize / 128);

    }

    static short[] toShortArray(double[] param) {
        int size = param.length;
        short[] result = new short[size];
        for (int x = 0; x < size; x++) {
            result[x] = (short) param[x];
        }
        return result;
    }

    static double checksum(double[] ...array) {
        double sum = 0;
        for (double[] d : array) {
            int size = d.length;
            for (int x = 0; x < size; x++) {
                sum += d[x];
            }
        }
        return sum;
    }

    static int checksum(short[] ...array) {
        int sum = 0;
        for (short[] d : array) {
            int size = d.length;
            for (int x = 0; x < size; x++) {
                sum += d[x];
            }
        }
        return sum;
    }


//    static int checksum(BufferedImage image) {
//        int height = image.getHeight();
//        int width = image.getWidth();
//        int[] pixels = new int[3];
//        int checksum = 0;
//        for (int h = 0; h < height; h++) {
//            for (int w = 0; w < width; w++) {
//
//                image.getRaster().getPixel(w, h, pixels);
//                checksum += pixels[0];
//                checksum += pixels[1];
//                checksum += pixels[2];
//            }
//        }
//        return checksum;
//    }

    private double getIndex(double[] param, boolean isWeighting, int sum,
                            ErrorDiffusionIF edif,
                            double[] bluenoise, double w) {
        short[] shortweight = !isWeighting ? paramToWeight(param, sum) :
                              toShortArray(param);
        double[] weight = {shortweight[0], shortweight[1],
                          shortweight[2]};
        edif.setWeight(weight);
//        BufferedImage image = edif.getBufferedImage();
//        double[][] fft = FFTUtil.bartlettMethodDFT(image, 2);
        short[][] edimage = edif.getEDImage();
        if (checkImageAllSame(edimage)) {
            throw new IllegalStateException();
        }
        double[][] fft = FFTUtil.bartlettMethodDFT(edimage, 2);
//        int imagecksum = checksum(edimage);
//        double fftcksum = checksum(fft);
//        double bluecksum = checksum(bluenoise);
        double Cg = getIndex(fft, bluenoise, weight, w);
        if (0.029166071176044528 == Cg) {
            getIndex(fft, bluenoise, weight, w);
            int a = 1;
        }

        return Cg;

    }

    static boolean checkImageAllSame(short[][] image) {
        int height = image.length;
        int width = image[0].length;
        short start = image[0][0];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (start != image[h][w]) {
                    return false;
                }
            }
        }
        return true;
    }

    public short[] getOptimumWeight2(final ErrorDiffusionIF edif, int error,
                                     int testSize, final int sum,
                                     final double w, double[] start) throws
            IOException {

        final double[] bluenoise = getBlueNoise(error, testSize);

        MinimisationFunction func = new MinimisationFunction() {
            public double function(double[] doubleArray) {
                return getIndex(doubleArray, false, sum, edif, bluenoise, w);

            }

        };

        Minimisation min = new Minimisation();
        int[] pindices = new int[] {0, 1};
        int[] plusminus = new int[] {1, 1};
        min.addConstraint(pindices, plusminus, -1, 0);
        min.addConstraint(pindices, plusminus, 1, 1);

        min.addConstraint(0, 1, 1);
        min.addConstraint(1, 1, 1);
        min.addConstraint(0, -1, 0);
        min.addConstraint(1, -1, 0);
        min.setTolerance(1);

        min.nelderMead(func, start);
        double[] param = min.getParamValues();
        minimum = min.getMinimum();

        short[] weight = paramToWeight(param, sum);

        return weight;
    }

    static short[] paramToWeight(double[] param, int sum) {
        double x = param[0];
        double y = param[1];
        double z = 1 - x - y;
        short[] weight = new short[3];
        weight[0] = (short) Math.round(sum * x);
        weight[1] = (short) Math.round(sum * y);
        weight[2] = (short) Math.round(sum * z);
        int delta = sum - (weight[0] + weight[1] + weight[2]);
        if ((weight[0] + weight[1] + weight[2]) != sum) {
            weight[0] = (short) (weight[0] + (short) delta);
        }
        if ((weight[0] + weight[1] + weight[2]) != sum) {
            throw new IllegalStateException();
        }
        return weight;
    }


    private static PearsonsCorrelation pearsons = new PearsonsCorrelation();
    static Plot2D hdvPlot = Plot2D.getInstance();
    static Plot2D xyPlot = Plot2D.getInstance();
    static boolean showPlot = false;
    static int degreeRange = 0;
    static double minCg = Double.MAX_VALUE;
    private static double getIndex(double[][] fft, double[] blueNoise,
                                   double[] weight, double weighting
            ) {

        double[] h = FFTUtil.getRadiallyAveragedPowerSpectrums(fft,
                0 - degreeRange,
                0 + degreeRange);
        double[] h1 = FFTUtil.getRadiallyAveragedPowerSpectrums(fft,
                22 - degreeRange,
                22 + degreeRange);

        double[] d = FFTUtil.getRadiallyAveragedPowerSpectrums(fft,
                45 - degreeRange,
                45 + degreeRange);
        double[] d1 = FFTUtil.getRadiallyAveragedPowerSpectrums(fft,
                67 - degreeRange,
                67 + degreeRange);

        double[] v = FFTUtil.getRadiallyAveragedPowerSpectrums(fft,
                90 - degreeRange,
                90 + degreeRange);

        double chd = pearsons.correlation(h, d);
        double ch1d = pearsons.correlation(h1, d);
        double ch1h = pearsons.correlation(h1, h);
        double cdv = pearsons.correlation(d, v);
        double cd1v = pearsons.correlation(d1, v);
        double cd1d = pearsons.correlation(d1, d);
        double chv = pearsons.correlation(h, v);

        double corr = 7 - chd - cdv - chv - ch1d - cd1v - ch1h - cd1d;

        double chg = pearsons.correlation(h, blueNoise);
        double ch1g = pearsons.correlation(h1, blueNoise);
        double cdg = pearsons.correlation(d, blueNoise);
        double cd1g = pearsons.correlation(d1, blueNoise);
        double cvg = pearsons.correlation(v, blueNoise);

        double cprofile = 5 - chg - cdg - cvg - ch1g - cd1g;
        double Lg = cprofile;

        double w = weighting;
        double Cg = corr * w + Lg * (1 - w);

        if (showPlot && Cg < minCg) {
            minCg = Cg;
            hdvPlot.setTitle(Double.toString(minCg) + " " +
                             DoubleArray.toString(weight));
            hdvPlot.removeAllPlots();
            hdvPlot.addLinePlot("h", 0, 1, h);
            hdvPlot.addLinePlot("h1", 0, 1, h1);
            hdvPlot.addLinePlot("d", 0, 1, d);
            hdvPlot.addLinePlot("d1", 0, 1, d1);
            hdvPlot.addLinePlot("v", 0, 1, v);
            hdvPlot.addLinePlot("blue", 0, 1, blueNoise);
            hdvPlot.setVisible();
            hdvPlot.addLegend();

            double sum = weight[0] + weight[1] + weight[2];
            double x = weight[0] / sum;
            double y = weight[1] / sum;
            xyPlot.addScatterPlot("", x, y);
            xyPlot.setVisible();
            xyPlot.setFixedBounds(0, 0, 1);
            xyPlot.setFixedBounds(1, 0, 1);

            System.out.print("*");
        }
//        System.out.println(DoubleArray.toString(weight) + " co" + corr + " Lg" +
//                           Lg + " Cg" + Cg + " " +
//                           (null != param ? DoubleArray.toString(param) : ""));
        return Cg;
    }

    private double minimum;

//    public double[] getWeight(ErrorDiffusionIF edif) {
//        double[] initweight = {1 / 3., 1 / 3., 1 / 3.};
//        edif.setWeight(initweight);
////        double[][] image = edif.getEDImage();
//        BufferedImage bimage = edif.getBufferedImage();
//        double[][] dft = BlueNoise.discreteFourierTransform(bimage);
//        double[] v = getVertialProfile(dft);
//        double[] d = getDiagonalProfile(dft);
//        double[] h = getHorizontalProfile(dft);
//
//        return null;
//    }



//    public static double[][] getBartlettMethod(double

//    public static double[] getBartlettMethod(double[] spectrum, int K) {
//        int size = spectrum.length;
//        int L = size / K;
//        for (int k = 0; k < K; k++) {
//            int ks = (k - 1) * L + 1;
//            int ke = ks + L - 1;
//        }
//        return null;
//    }

//    private double[] getDiagonalProfile2(double[][] dft) {
//        int height = dft.length;
//        int halfheight = height / 2;
//        double[] result = new double[halfheight];
//        for (int x = 1; x < halfheight; x++) {
//            result[x] = dft[halfheight - x][halfheight + x] +
//                        dft[halfheight + x][halfheight + x] +
//                        dft[halfheight + x][halfheight - x] +
//                        dft[halfheight - x][halfheight - x];
//            result[x] = result[x] / 4;
//        }
//        return result;
//    }



    private static short[][] getErrorImage(int width, int height, short error) {
        short[][] image = new short[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                image[h][w] = error;
            }
        }

        return image;
    }

    static double[] getRandomStart() {
        double[] start = new double[2];
        do {
            start[0] = Math.random();
            start[1] = Math.random();
        } while ((start[0] + start[1] >= 1));
        return start;
    }

    /**
     *
     * @param w double
     * @param weighting double[][]
     * @throws IOException
     * @deprecated
     */
    public static void optimumThreshold(double w, double[][] weighting) throws
            IOException {

        OstromoukhovAlgorithm algo = new OstromoukhovAlgorithm();
        short starterror = 2;
        short enderror = 8;
        double[] thresholds = new double[7];
        for (short error = starterror; error <= enderror; error++) {
            int index = error - starterror;
            double[] weight = weighting[index];
            double threshold = algo.produceThreshold(error, w, weight);
            thresholds[index] = threshold;
            double min = algo.getMinimum();
            System.out.println("Error: " + error + " Threshold: " +
                               threshold + " Minimum: " + min);

        }
        System.out.println(toString(thresholds));

    }

    public static int[] optimumThresholdModulation2(double w,
            double[][] weighting, short modBase) throws
            IOException {

        OstromoukhovAlgorithm algo = new OstromoukhovAlgorithm();
        short starterror = 1;
        short enderror = 15;
        int totalerror = 15;

        final int testSize = (int) Math.pow(2, TestPower);
//        for (short modBase = 16; modBase <= 16; modBase++) {
        int[] strengths = new int[totalerror];
        double[] minimum = new double[totalerror];
        for (int x = 0; x < totalerror; x++) {
            minimum[x] = Double.MAX_VALUE;
        }

        for (short error = starterror; error <= enderror; error++) {
            int index = error - starterror;
            double[] weight = weighting[index];
            final short[][] image = getErrorImage(testSize, testSize, error);
            int blueerror = error > 8 ? 16 - error : error;
            final double[] bluenoise = algo.getBlueNoise(blueerror, testSize);
            final int sum = (int) DoubleArray.sum(weight);

            for (int strength = 1; strength <= 6; strength++) {
                ErrorDiffusionIF edif = algo.getErrorDiffusionIF(testSize,
                        image, strength, modBase, true);
//                    edif.setRandomThreshold(false);
//                    int imagecksum = checksum(image);
                double i = algo.getIndex(weight, true, sum, edif, bluenoise,
                                         w);

                if (i < minimum[index]) {
                    minimum[index] = i;
                    strengths[index] = strength;
                }
            }
        }
        System.out.println("modBasebase: " + modBase);
        for (int x = 0; x < totalerror; x++) {
            System.out.println(strengths[x] + " " + minimum[x]);
        }

        System.out.println("modBasebase: " + modBase + " " + toString(strengths) +
                           " " + DoubleArray.sum(minimum));
//        }
        return strengths;
    }

    static String toString(int[] array) {
        int size = array.length;
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        for (int x = 0; x < size; x++) {
            buf.append(array[x]);
            if (x != size - 1) {
                buf.append(",");
            }
        }
        buf.append("}");
        return buf.toString();
    }

    static String toString(double[] array) {
        int size = array.length;
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        for (int x = 0; x < size; x++) {
            buf.append(array[x]);
            if (x != size - 1) {
                buf.append(",");
            }
        }
        buf.append("}");
        return buf.toString();
    }


    public static void optimumWeightWithVaringSum(double w, int startbase) throws IOException {
        double[] minimum = new double[7];
        for (int x = 0; x < 7; x++) {
            minimum[x] = Double.MAX_VALUE;
        }
        double[][] coefs = new double[7][];
        OstromoukhovAlgorithm algo = new OstromoukhovAlgorithm();
        short starterror = 2;
        short enderror = 8;
        for (int base = startbase; base <= 12; base++) {
            int sum = (int) Math.pow(2, base);
            for (short error = starterror; error <= enderror; error++) {
                for (int t = 0; t < 500; t++) {

                    double[] start = getRandomStart(); //{1 / 3., 1 / 3.};
                    double[] weight = algo.produceWeight(error, sum, w, start);
                    double min = algo.getMinimum();
                    int index = error - 2;
                    if (min < minimum[index]) {
                        minimum[index] = min;
                        coefs[index] = weight;
                    }
                }
            }
            for (short error = starterror; error <= enderror; error++) {
                int index = error - 2;
                System.out.println(error + " " + minimum[index] + " " +
                                   DoubleArray.toString(coefs[index]));
            }

        }

    }


    static int[] getD10Series(int base) {
//        java.util.ArrayList<Integer> list = new ArrayList<Integer>();
//        java.util.LinkedList<Integer> list = new LinkedList<Integer>();
        java.util.List<Integer> list = new java.util.ArrayList<Integer>();
        for (int a_add = 1; a_add < base; a_add++) {
            for (int plus = -1; plus <= 1; plus += 2) {
                int a = base + a_add * plus;
                list.add(a);
            }
        }
        int size = list.size();
        int[] result = new int[size];
        for (int x = 0; x < size; x++) {
            result[x] = list.get(x);
        }
        return result;
    }

    static int[] getD10SeriesWithPowerOf2(int sum) {

        return new int[] {sum / 2, sum / 4};
    }

    public static double[][] optimumWeightWithVaringSum3(double w, int startbase, int endbase,
            boolean d10WithPowerOf2) throws IOException {
        short starterror = 15;
        short enderror = 15;
        int errorcount = enderror - starterror + 1;

        double[] minimum = new double[errorcount];
        for (int x = 0; x < errorcount; x++) {
            minimum[x] = Double.MAX_VALUE;
        }
        double[][] coefs = new double[errorcount][3];
        OstromoukhovAlgorithm algo = new OstromoukhovAlgorithm();

        for (int base = startbase; base <= endbase; base++) {

            int sum = (int) Math.pow(2, base);
            int d10Base = (int) Math.round(sum * 7. / 16);
            for (short error = starterror; error <= enderror; error++) {
                int[] aSeries = null;
                if (d10WithPowerOf2) {
                    aSeries = getD10SeriesWithPowerOf2(sum);
                } else {
                    aSeries = getD10Series(d10Base);
                }

                for (int a : aSeries) {
                    for (int b = sum - a; b >= 0; b--) {
                        int c = sum - a - b;
                        if (a < b || a < c /* || c < b*/) {
                            continue;
                        }
                        if (c < b) {
                            continue;
                        }
//                        System.out.println(a + " " + b + " " + c);
                        double[] weight = new double[] {a, b, c};

                        double min1 = algo.evaluateIndex(error, weight, w, false);
                        double min2 = algo.evaluateIndex((short) (16 - error), weight, w, false); //對稱的error也應該一併計算
                        double min = min1 + min2;
                        int index = error - starterror;
                        if (min < minimum[index]) {
                            minimum[index] = min;
                            coefs[index] = weight;
                            System.out.println("base: " + base);
                            for (short e = starterror; e <= enderror;
                                           e++) {
                                int i = e - starterror;
                                System.out.println(e + " " + minimum[i] +
                                        " " +
                                        DoubleArray.toString(coefs[i]));
                            }

                        }

                    }
                }

            }

        }
        return coefs;
    }


    public static void optimumWeightWithVaringSum2(double w, int startbase) throws
            IOException {
        short starterror = 1;
        short enderror = 8;
        int errorcount = enderror - starterror + 1;

        double[] minimum = new double[errorcount];
        for (int x = 0; x < errorcount; x++) {
            minimum[x] = Double.MAX_VALUE;
        }
        double[][] coefs = new double[errorcount][3];
        OstromoukhovAlgorithm algo = new OstromoukhovAlgorithm();

        for (int base = startbase; base <= 9; base++) {

            int sum = (int) Math.pow(2, base);
            for (short error = starterror; error <= enderror; error++) {

                for (int a = 1; a < sum - 2; a++) {
                    for (int b = 1; b < sum - a - 1; b++) {
                        int c = sum - a - b;
                        double[] weight = new double[] {a, b, c};

                        double min1 = algo.evaluateIndex(error, weight, w, false);
                        double min2 = algo.evaluateIndex((short) (16 - error), weight, w, false); //對稱的error也應該一併計算
                        double min = min1 + min2;
                        int index = error - starterror;
                        if (min < minimum[index]) {
                            minimum[index] = min;
                            coefs[index] = weight;
                            System.out.println("base: " + base);
                            for (short e = starterror; e <= enderror;
                                           e++) {
                                int i = e - starterror;
                                System.out.println(e + " " + minimum[i] +
                                        " " +
                                        DoubleArray.toString(coefs[i]));
                            }

                        }
                    }
                }

            }

        }

    }

    public double evaluateIndex(short error, double[] param, double w, boolean randomThreshold) throws
            IOException {
        int testSize = (int) Math.pow(2, TestPower);
        short[][] image = getErrorImage(testSize, testSize, error);
        ErrorDiffusionIF edif = getErrorDiffusionIF(testSize, image, randomThreshold);
//        edif.setRandomThreshold(randomThreshold);

        short blueNoiseError = error > 8 ? (short) (16 - error) : error;
        double[] bluenoise = getBlueNoise(blueNoiseError, testSize);
        int sum = (int) DoubleArray.sum(param);
        double index = getIndex(param, true, sum, edif, bluenoise, w);
        return index;
    }

//    public double evaluateIndex(short error, double[] param, double w, ErrorDiffusionIF edif) throws
//            IOException {
//        int testSize = (int) Math.pow(2, TestPower);
////    short[][] image = getErrorImage(testSize, testSize, error);
////    ErrorDiffusionIF edif = getErrorDiffusionIF(testSize, image);
//        double[] bluenoise = getBlueNoise(error, testSize);
//        int sum = (int) DoubleArray.sum(param);
//        double index = getIndex(param, true, sum, edif, bluenoise, w);
//        return index;
//    }


    static enum Condition {
        OptimumWeight, OptimumThreshold, StoreImage, EvaluateIndex,
        EvaluateIndexes, OptimumWeight2, OptimumThreshold2,
        StoreImageThresholdModulation, OptimumWeightAndThreshold
    }


    private static void storeImage(double[][] weighting) throws IOException {
        final int testSize = (int) Math.pow(2, TestPower);

//        double[] strength = {1.0, 1.0, 1.0, 5.0, 4.0, 3.0, 3.0}; //old
        int[] strength = {1, 2, 4, 3, 4, 1, 4}; //new

        short modulationBase = 8;

        boolean thresholdModulation = true;

        for (short error = 2; error <= 8; error++) {
            double[] weight = weighting[error - 2];
            int str = thresholdModulation ? strength[error - 2] : 0;
            double[] w = {0, weight[0], weight[1], weight[2], 0};
            storeEDImage(testSize, w, error, str, modulationBase);
        }

    }

    private static void storeImageThresholdModulation(double[][] weightingsForModulation,
            int[] strehgths) throws IOException {

        int height = 1080;
        int width = 1920;
//        int[] grayLevelArray = {64, 96, 128, 192, 400};
        int[] grayLevelArray = {64};

        for (int grayLevel : grayLevelArray) {
//        short[][][] pattern = EDVerifyDMCTableProducer.getEDFHDCheckPattern(
//                width, height);
            short[][][] pattern = EDPaternGen.get16GrayLevelHPatternWithBlack(
                    grayLevel, false,
                    width, height, 1);
//            short[][][] pattern = EDPaternGen.get16LevelCirclePattern(grayLevel, false, width,
//                    height);

            short[][] edimage = AUOErrorDiffusion.
                                hardWareFloydSteinberg_hardWareThresholdModulation(
                                        pattern[0], 4, weightingsForModulation,
                                        strehgths, (short) 16, true, true);
            int cksum = checksum(edimage);
            System.out.println(cksum);

            short[][][] result = {edimage, edimage, edimage};
            boolean remap = true;
            if (remap) {
//            int grayLevel = 25;
                ArrayUtils.remap(result, grayLevel,
                                 128 * 16, grayLevel + 16, 192 * 16);
//                         0 * 16, 16, 128 * 16);
            }
            MuraImageUtils utils = new MuraImageUtils(12, width, height);
            utils.store8BitImageTiff(result,
                                     "ED/ED Demo/Ostromoukhov/Ostromoukhov_" + grayLevel + ".tif");
        }
    }

    public static void main(String[] args) throws IOException {
//        double[][] a = { {76, 44, 8}, {48, 32, 48}, {56, 32, 40}, {64,
//                       16, 48}, {68, 31, 29}, {52, 56, 20}, {42, 46, 40}, {80, 32, 16}, {40, 45, 43},
//                       {56, 42, 30}, {64, 34,
//                       30}, {64, 16, 48}, {56, 32, 40}, {48, 32, 48}, {91,
//                       34, 3}
//
//        }; //opt for full2
//
//        a = fullWeightingBase2Weighting(a);

        Condition condition = Condition.OptimumWeight2;
//        Condition condition = Condition.OptimumThreshold2;
//        Condition condition = Condition.StoreImageThresholdModulation;
//        Condition condition = Condition.OptimumWeightAndThreshold;

        switch (condition) {
        case OptimumWeight: //速度很慢, 都跑不出來, dprecated
            throw new java.lang.UnsupportedOperationException();
//            optimumWeightWithVaringSum(0.5, 4);
//            return;
        case OptimumWeight2:

//            optimumWeightWithVaringSum2(1, 5);
            optimumWeightWithVaringSum3(1, 5, 7, true);
            return;
        case OptimumThreshold: //產出為floating的threshold, dprecated
            throw new java.lang.UnsupportedOperationException();
//            optimumThreshold(0.5, weighting);
//        return;
        case OptimumThreshold2: //產出為integer的threshold, 採用此法才正確

//            double[][] weighting = { {6, 5, 5}, {6, 4, 6}, {7, 4, 5}, {8, 2, 6}, {6, 5, 5}, {7, 5,
//                                   4}, {5, 5, 6}, {10, 4, 2}
//            }; //4bit

//            double[][] weighting = { {19, 11, 2}, {12, 8, 12}, {14, 8, 10}, {5, 14, 13}, {12, 10,
//                                   10}, {13, 14, 5}, {14, 13, 5}, {20, 8, 4}
//            }   ; //5bit

//            double[][] weighting = { {24, 19, 21}, {12, 8, 12}, {14, 8, 10}, {32, 13, 19}, {32, 17,
//                                   15}, {28, 21, 15}, {21, 23, 20}, {20, 8, 4}
//            }; //6bit
//            double[][] weighting = { {91, 34, 3}, {12, 8, 12}, {14, 8, 10}, {18, 49, 61}, {68, 31,
//                                   29}, {51, 49, 28}, {40, 45, 43}, {20, 8, 4}
//            }                    ; //7bit

            double[][] weighting = { {76, 44, 8}, {48, 32, 48}, {56, 32, 40}, {64, 16, 48}, {68,
                                   31, 29}, {52, 56, 20}, {42, 46, 40}, {80, 32, 16}, {40, 45, 43},
                                   {56, 42, 30}, {64, 34, 30}, {18, 49, 61}, {56, 32, 40}, {48, 32,
                                   48}, {91, 34, 3}

            }; //opt for full

            optimumThresholdModulation2(.5, weighting, (short) 16);
            return;
//        case StoreImage: {
//            storeImage(weighting);
//        }
//        return;
        case StoreImageThresholdModulation: {

//            double[][] weightingsForModulation = { {7, 4, 5}, {91, 34, 3}, {48, 32, 48}, {53, 42,
//                                                 33}, {71, 41,
//                                                 16}, {68, 31, 29}, {51, 49, 28}, {44, 41, 43}, {80,
//                                                 40, 8}, {44, 41, 43}, {51, 49, 28}, {68, 31, 29},
//                                                 {71, 41, 16}, {53, 42, 33}, {48, 32, 48}, {91, 34,
//                                                 3}
//
//            }; //opt for full2


            double[][] weightingsForModulation = { {7, 4, 5}, {48, 38, 42}, {48, 32, 48}, {60, 25,
                                                 43}, {64,
                                                 27, 37}, {60, 31, 37}, {59, 29, 40}, {44, 41, 43},
                                                 {88, 20, 20}, {44, 41, 43}, {59, 29, 40}, {60, 31,
                                                 37}, {64, 27, 37}, {60, 25, 43}, {48, 32, 48}, {48,
                                                 38, 42},

            }; //opt for full2

//            double[][] weightingsForModulation = { {7, 4, 5}, {32, 15, 17}, {32, 7, 25}, {64, 47,
//                                                 17}, {64, 27, 37}, {32, 17, 15}, {64, 25, 39}, {64,
//                                                 7, 57}, {16, 4, 12}, {64, 7, 57}, {64, 25, 39},
//                                                 {32, 17, 15}, {64, 27, 37}, {64, 47, 17}, {32, 7,
//                                                 25}, {32, 15, 17}
//            }; //opt for 1/2 only

            weightingsForModulation = AUOErrorDiffusion.fullWeightingBase2Weighting(
                    weightingsForModulation);

            int[] strengthForModulation = {0, 4, 2, 1, 1, 2, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1}; //org opt+manual2
//            int[] strengthForModulation = {0, 2, 2, 2, 1, 2,
//                                          2, 2, 4, 2, 3,
//                                          2, 1, 2, 1, 1
//            }; //opt for 1/2 only
//            strengthForModulation = null;//no random

            storeImageThresholdModulation(weightingsForModulation, strengthForModulation);
        }
        return;
        case EvaluateIndex: {
            double[] weight = {8.0, 3.0, 5.0};
            short error = 2;
            OstromoukhovAlgorithm algo = new OstromoukhovAlgorithm();
            System.out.println(DoubleArray.toString(weight) + " Error: " +
                               error);
            System.out.println(algo.evaluateIndex(error, weight, 0.5, false));
        }
        return;
        case EvaluateIndexes: {

        }
        return;

        case OptimumWeightAndThreshold: {
            double[][] newWeighting = optimumWeightWithVaringSum3(1, 5, 7, false);
//            newWeighting = AUOErrorDiffusion.fullWeightingBase2Weighting(newWeighting);
            System.out.println(DoubleArray.toString(newWeighting));
            int[] strengths = optimumThresholdModulation2(.5, newWeighting, (short) 16);
            System.out.println(DoubleArray.toString(newWeighting));
            System.out.println(IntArray.toString(strengths));
        }
        return;
        }

    }

    static int[] strehgthBase2strehgth(int[] strehgthBase) {
        int[] result = new int[16];
        for (int x = 1; x <= 8; x++) {
            result[x] = strehgthBase[x - 1];
            result[16 - x] = strehgthBase[x - 1];
        }
        return result;
    }

    static double[][] halfWeightingBase2Weighting(double[][] weightingBase) {
        double[][] result = new double[16][];
        result[0] = new double[] {0, 7, 4, 5, 0};
        for (int x = 1; x <= 8; x++) {
            result[x] = new double[5];
            result[16 - x] = new double[5];
            for (int n = 0; n < 3; n++) {
                result[x][n + 1] = weightingBase[x - 1][n];
                result[16 - x][n + 1] = weightingBase[x - 1][n];
            }
        }
        return result;
    }


    public final static int TestPower = 8;

    private ErrorDiffusionIF getErrorDiffusionIF(final int testSize,
                                                 final short[][] image,
                                                 final boolean randomThreshold) {
        return getErrorDiffusionIF(testSize, image, 0, (short) 16, randomThreshold);
    }

    private ErrorDiffusionIF getErrorDiffusionIF(final int testSize,
                                                 final short[][] image,
                                                 final int strength,
                                                 final short modulationBase,
                                                 final boolean randomThreshold) {
        ErrorDiffusionIF edif = new ErrorDiffusionIF() {
            double[] edweight;
            IntegerImage intimage = new IntegerImage(testSize, testSize);

            public void setWeight(double[] weight) {
                if (3 == weight.length) {
                    double w0 = weight[0]; // * 65535;
                    double w1 = weight[1]; // * 65535;
                    double w2 = weight[2]; // * 65535;
                    edweight = new double[] {0, w0, w1, w2, 0};

                } else if (4 == weight.length) {
                    double w0 = weight[0]; // * 65535;
                    double w1 = weight[1]; // * 65535;
                    double w2 = weight[2]; // * 65535;
                    double w3 = weight[3]; // * 65535;
                    edweight = new double[] {0, w0, w1, w2, w3};

                }

            }

            public short[][] getEDImage() {
                return getErrorDiffusionImage(image, edweight, strength,
                                              modulationBase, randomThreshold);
            }

            public BufferedImage getBufferedImage() {
                short[][] image = getEDImage();
                return OstromoukhovAlgorithm.getBufferedImage(image,
                        intimage);
            }

        };
        return edif;
    }

    /**
     *
     * @param error short
     * @param w double
     * @param weighting double[]
     * @return double
     * @throws IOException
     * @deprecated
     */
    public double produceThreshold(final short error, final double w,
                                   final double[] weighting) throws IOException {
        final int testSize = (int) Math.pow(2, TestPower);
        final short[][] image = getErrorImage(testSize, testSize, error);
        final double[] bluenoise = getBlueNoise(error, testSize);
        final int sum = (int) DoubleArray.sum(weighting);

        MinimisationFunction func = new MinimisationFunction() {
            public double function(double[] doubleArray) {
                int strength = (int) doubleArray[0];
                ErrorDiffusionIF edif = getErrorDiffusionIF(testSize, image,
                        strength, (short) 16, true);
                return getIndex(weighting, true, sum, edif, bluenoise, w);

            }

        };

        Minimisation min = new Minimisation();

        min.addConstraint(0, 1, 1);
        min.addConstraint(0, -1, 0);
//        min.setTolerance(1);
        double[] start = {0.5};
        min.nelderMead(func, start);
        double[] param = min.getParamValues();
        minimum = min.getMinimum();

        return param[0];
    }

    public double[] produceWeight(final short error, int sum, double w,
                                  double[] start) throws
            IOException {
        int testSize = (int) Math.pow(2, TestPower);
        short[][] image = getErrorImage(testSize, testSize, error);
        ErrorDiffusionIF edif = getErrorDiffusionIF(testSize, image, false);

        short[] optimum = getOptimumWeight2(edif, error, testSize, sum,
                                            w, start);

        if (3 == optimum.length) {
            return new double[] {0, optimum[0], optimum[1],
                    optimum[2], 0};
        } else if (4 == optimum.length) {
            return new double[] {0, optimum[0], optimum[1],
                    optimum[2], optimum[3]};
        } else {
            return null;
        }
    }


    public static String toString(short[] ...v) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < v.length; i++) {
            for (int j = 0; j < v[i].length - 1; j++) {
                str.append(v[i][j] + " ");
            }
            str.append(v[i][v[i].length - 1]);
            if (i < v.length - 1) {
                str.append("\n");
            }
        }
        return str.toString();
    }


    private final static short[][] getErrorDiffusionImage(short[][] image,
            double[] edweight, int strength, short modulationBase, boolean randomThreshold) {
        boolean hardware = false;

        if (hardware) {

            short[][] edimage = AUOErrorDiffusion.
                                hardWareFloydSteinberg_2_(image, 4);
            return edimage;

        } else {
            //為了與random threshold接軌, 此處選擇新方法
            boolean NewModulation = true;
            double[][] weights = {edweight, edweight, edweight, edweight, edweight, edweight,
                                 edweight, edweight, edweight, edweight, edweight, edweight,
                                 edweight, edweight, edweight, edweight};
            int[] strengths = {strength, strength, strength, strength, strength, strength, strength,
                              strength, strength, strength, strength, strength, strength, strength,
                              strength, strength};
            short[][] edimage = AUOErrorDiffusion.
                                hardWareFloydSteinberg_hardWareThresholdModulation(image,
                    4, weights, strengths, modulationBase,
                    NewModulation, randomThreshold);
//            int imagecksum = checksum(image);
//            int edimagecksum = checksum(edimage);
            return edimage;

        }

    }

    private static void storeEDImage(int testSize,
                                     double[] templateWeight, short error,
                                     int strength, short modulationBase) throws
            IOException {
        storeEDImage(testSize, testSize, templateWeight, strength,
                     error, modulationBase, "optimum/err" + error + "_128.tif", false);
//        storeEDImage(1920, 1080, templateWeight, strength,
//                     error, "optimum/err" + error + "_FHD.tif", false);

    }


    private static void storeEDImage(int width, int height,
                                     double[] weight, int strength,
                                     short error, short modulationBase,
                                     String filename,
                                     boolean showSpectrum) throws
            IOException {
        short[][] errorIamge = getErrorImage(width, height, error);

        short[][] edimage = getErrorDiffusionImage(errorIamge, weight, strength,
                modulationBase, false);

        ArrayUtils.remap(new short[][][] {edimage, edimage, edimage}, 0,
                         128 * 16, 16, 255 * 16);
        BufferedImage resultImage = getBufferedImage(edimage,
                new IntegerImage(width, height));

        ImageUtils.storeTIFFImage(filename, resultImage);

        if (showSpectrum) {
            double[][] fft = FFTUtil.bartlettMethodDFT(resultImage, 1);
            double[] h = FFTUtil.getRadiallyAveragedPowerSpectrums(fft,
                    0 - degreeRange,
                    0 + degreeRange);

            double[] d = FFTUtil.getRadiallyAveragedPowerSpectrums(fft,
                    45 - degreeRange,
                    45 + degreeRange);

            double[] v = FFTUtil.getRadiallyAveragedPowerSpectrums(fft,
                    90 - degreeRange,
                    90 + degreeRange);
            Plot2D plot = Plot2D.getInstance("Spectrum");
            plot.addLinePlot("h", 0, 1, h);
            plot.addLinePlot("d", 0, 1, d);
            plot.addLinePlot("v", 0, 1, v);
            plot.setVisible();
            plot.addLegend();
        }
    }

    private static BufferedImage getBufferedImage(short[][] image,
                                                  IntegerImage intimage) {
        int[] pixel = new int[3];
        int height = image.length;
        int width = image[0].length;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                pixel[0] = pixel[1] = pixel[2] = image[h][w];
                intimage.setPixel(w, h, pixel);
            }
        }
        return intimage.getBufferedImage();
    }


    public static interface ErrorDiffusionIF {
        public void setWeight(double[] weight);

        public short[][] getEDImage();

        public BufferedImage getBufferedImage();

//        public void setRandomThreshold(boolean randomThreshold);
    }


    public double getMinimum() {
        return minimum;
    }
}
