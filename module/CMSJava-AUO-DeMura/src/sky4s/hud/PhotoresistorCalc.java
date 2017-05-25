package sky4s.hud;

import shu.cms.plot.Plot2D;
import shu.math.Interpolation;
import shu.math.lut.Interpolation1DLUT;
import shu.math.regress.PolynomialRegression;
import shu.math.Polynomial.COEF_1;
import shu.math.Polynomial;
import shu.math.Maths;
import java.util.ArrayList;
import java.awt.Color;

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
public class PhotoresistorCalc {
    static double minPulldown = 0.008;
    static double maxPulldown = 0.4;
    public static void main(String[] args) {
        double[] photo5549 = {10, 47.5,
                             100, 6.5,
                             1000, 0.828571429,
                             10000, 0.091632653,
                             100000, 0.006685131,
                             200000, 0.002276858};

        double[] photo5539 = {10, 30,
                             100, 3.5,
                             1000, 0.383333333,
                             10000, 0.037222222,
                             100000, 0.002648148,
                             200000, 0.001005393
        };

        double[] photo9006 = {10, 15,
                             100, 3.5,
                             1000, 0.804166667,
                             10000, 0.182534722,
                             100000, 0.041028067,
                             200000, 0.026134671

        };
        double[] photo5003 = {10, 15,
                             100, 4,
                             1000, 1.025,
                             10000, 0.255625,
                             100000, 0.062515625,
                             200000, 0.040789624
        };

        double maxVoltage = 5;
        double maxLux = 163840;
        Interpolation1DLUT lut5549 = toLogInterpolation1DLUT(photo5549);
        Interpolation1DLUT lut5539 = toLogInterpolation1DLUT(photo5539);
        Interpolation1DLUT lut9006 = toLogInterpolation1DLUT(photo9006);
        Interpolation1DLUT lut5003 = toLogInterpolation1DLUT(photo5003);

//        luxToVoltagePlot("5549", lut5549);
        Plot2D plot5549 = luxToVoltagePlot2("5549", maxVoltage, lut5549);
        Plot2D plot5539 = luxToVoltagePlot2("5539", maxVoltage, lut5539);
        Plot2D plot9006 = luxToVoltagePlot2("9006", maxVoltage, lut9006);
        Plot2D plot5003 = luxToVoltagePlot2("5003", maxVoltage, lut5003);

        Interpolation1DLUT lut5549R = getReverseInterpolation1DLUT(lut5549);
        Interpolation1DLUT lut5539R = getReverseInterpolation1DLUT(lut5539);

        double maxPhotoresistor5549 = Math.pow(10, lut5549.getValue(Math.log10(maxLux)));
        double maxPhotoresistor5539 = Math.pow(10, lut5539.getValue(Math.log10(maxLux)));

        double pulldownResistor5539 = voltageToPulldownResistor(maxVoltage, 4.7687179276125296, maxPhotoresistor5539);
        System.out.println(pulldownResistor5539);

    }

    static void ADCVtoDeltaLightness(String title, double maxVoltage, Interpolation1DLUT lut) {
        Interpolation1DLUT reverseLut = getReverseInterpolation1DLUT(lut);
        Plot2D plot = Plot2D.getInstance(title);
        double maxLux = 163840;
        for (double pulldownResistor = minPulldown; pulldownResistor <= maxPulldown; pulldownResistor *= 2) {
            double outputvoltage = luxToVoltage(maxVoltage, maxLux, pulldownResistor, lut);
            double voltageRatio = outputvoltage / maxVoltage;
            int maxADCValue = (int) (voltageRatio * 1023);
            System.out.println(pulldownResistor + "R: " + voltageRatio + " " + maxADCValue);
            for (int ADCV = 10; ADCV <= maxADCValue; ADCV++) {
                double lux = luxToLightness(voltageToLux(maxVoltage, ADCV / 1023. * maxVoltage, pulldownResistor,
                        reverseLut));
                double lux2 = luxToLightness(voltageToLux(maxVoltage, (ADCV - 1) / 1023. * maxVoltage, pulldownResistor,
                        reverseLut));
                double deltaLux = lux - lux2;
                plot.addCacheScatterLinePlot(Double.toString(pulldownResistor), ADCV, deltaLux);
            }

        }
        plot.setVisible();
        plot.setFixedBounds(0, 0, 1023);
        plot.setFixedBounds(1, 0, 9);
    }

    static Interpolation1DLUT getReverseInterpolation1DLUT(Interpolation1DLUT lut) {
        double[] keyArray = reverse(lut.getKeyArray());
        double[] valueArray = reverse(lut.getValueArray());
//        DoubleArray.
        Interpolation1DLUT reverse = new Interpolation1DLUT(keyArray, valueArray, Interpolation1DLUT.Algo.LINEAR);
        return reverse;
    }

    static double[] reverse(double[] d) {
        int size = d.length;
        double[] reverse = new double[size];
        for (int x = 0; x < size; x++) {
            reverse[x] = d[size - x - 1];
        }
        return reverse;
    }

    static double luxToVoltage(double maxVoltage, double lux, double pulldownResistor, Interpolation1DLUT lut) {
        double lutoutput = lut.getValue(Math.log10(lux));
        double photoresistor = Math.pow(10, lutoutput);
//        if (lux == 163840) {
//            System.out.println(photoresistor);
//        }
        double outputvoltage = maxVoltage * (pulldownResistor / (photoresistor + pulldownResistor));
        return outputvoltage;
    }

    static double voltageToPulldownResistor(double maxVoltage, double voltage, double maxphotoresistor) {
        double ratio = voltage / maxVoltage;
//        double photoresistor = (maxVoltage * pulldownResistor) / voltage - pulldownResistor;
//        double log10photoresistor = Math.log10(photoresistor);
//        double log10lux = lut.getKey(log10photoresistor);
//        double lux = Math.pow(10, log10lux);
//        return lux;
        double pulldownResistor = ratio * maxphotoresistor / (1 - ratio);

        return pulldownResistor;
    }


    static double voltageToLux(double maxVoltage, double voltage, double pulldownResistor, Interpolation1DLUT lut) {
        double photoresistor = (maxVoltage * pulldownResistor) / voltage - pulldownResistor;
        double log10photoresistor = Math.log10(photoresistor);
        double log10lux = lut.getKey(log10photoresistor);
        double lux = Math.pow(10, log10lux);
        return lux;
    }

    static Interpolation1DLUT toLogInterpolation1DLUT(double[] photoresistorData) {
        int size = photoresistorData.length / 2;
        double[] luxlut = new double[size];
        double[] resistorlut = new double[size];
        double[] logluxlut = new double[size];
        double[] logresistorlut = new double[size];
        for (int x = 0; x < size; x++) {
            luxlut[x] = photoresistorData[x * 2];
            resistorlut[x] = photoresistorData[x * 2 + 1];
            logluxlut[x] = Math.log10(photoresistorData[x * 2]);
            logresistorlut[x] = Math.log10(photoresistorData[x * 2 + 1]);
        }

        Interpolation1DLUT loglut = new Interpolation1DLUT(logluxlut, logresistorlut, Interpolation1DLUT.Algo.LINEAR);
        return loglut;
    }

    static double luxToLightness(double lux) {
        double luminance = lux / 20.;
        double lightness = Maths.cubeRoot(luminance) * 25.29 - 18.38;
        return lightness;
    }

    public static void luxToVoltagePlot(String title, Interpolation1DLUT loglut) {

//        Interpolation1DLUT loglut = toLogInterpolation1DLUT(photoresistorData);

        double voltage = 5.0;
        boolean showDelta = false;

        Plot2D plot = Plot2D.getInstance(title);
        Plot2D plotDelta = showDelta ? Plot2D.getInstance("delta of " + title) : null;
        System.out.println(title + ": ");
        System.out.print("max photo resistor:");
        luxToVoltage(voltage, 163840, 0, loglut);
        for (double pulldownResistor = minPulldown; pulldownResistor <= maxPulldown; pulldownResistor *= 2) {

            for (int lux = 163840; lux >= 100; lux -= 100) {

//                double photoresistor = Math.pow(10, loglut.getValue(Math.log10(lux)));
//                double outputvoltage = voltage * (pulldownResistor / (photoresistor + pulldownResistor));
                double outputvoltage = luxToVoltage(voltage, lux, pulldownResistor, loglut);

                double deltaResistor = Math.pow(10, loglut.getValue(Math.log10(lux - 100)));
                double deltaVoltage = voltage * (pulldownResistor / (deltaResistor + pulldownResistor));

                double lightness = luxToLightness(lux);

                double xaxis = lightness;
//                double xaxis = lux;
                plot.addCacheScatterLinePlot(Double.toString(pulldownResistor), xaxis, outputvoltage);
                if (showDelta) {
                    plotDelta.addCacheScatterLinePlot(Double.toString(pulldownResistor), xaxis,
                            outputvoltage - deltaVoltage);
                }
                if (163800 == lux) {
                    System.out.println(pulldownResistor + "R " + outputvoltage + "V");
                }
            }

        }

        plot.setVisible();
        if (showDelta) {
            plotDelta.setVisible();
            plotDelta.setFixedBounds(1, 0, 0.04);
        }
    }

    public static Plot2D luxToVoltagePlot2(String title, double maxVoltage, Interpolation1DLUT loglut) {
        Plot2D plot = Plot2D.getInstance(title);
        System.out.println(title + ": ");
        System.out.print("max photo resistor:");
        luxToVoltage(maxVoltage, 163840, 0, loglut);
        for (double pulldownResistor = minPulldown; pulldownResistor <= maxPulldown; pulldownResistor *= 2) {
            luxToVoltagePlot(maxVoltage, pulldownResistor, loglut, plot);
        }
        plot.setChartTitle(title);
        plot.addLegend();
        plot.setVisible();
        plot.setFixedBounds(1, 0, 5);
        return plot;
    }

    static double[] toDoubleArray(ArrayList<Double> doubleList) {
        int size = doubleList.size();
        double[] result = new double[size];
        for (int x = 0; x < size; x++) {
            result[x] = doubleList.get(x);
        }
        return result;
    }

    public static void luxToVoltagePlot(double maxVoltage, double pulldownResistor, Interpolation1DLUT loglut,
                                        Plot2D plot) {
        ArrayList<Double> lightnesslist = new ArrayList<Double>();
        ArrayList<Double> voltagelist = new ArrayList<Double>();
        for (int lux = 200; lux <= 163840; lux += 100) {
            double outputvoltage = luxToVoltage(maxVoltage, lux, pulldownResistor, loglut);
            double lightness = luxToLightness(lux);
            double xaxis = lightness;
            plot.addCacheScatterLinePlot(Double.toString(pulldownResistor), xaxis, outputvoltage);

            lightnesslist.add(lightness);
            voltagelist.add(outputvoltage);
        }
        double[] lightnessArray = toDoubleArray(lightnesslist);
        double[] voltageArray = toDoubleArray(voltagelist);
        PolynomialRegression regress = new PolynomialRegression(lightnessArray, voltageArray, Polynomial.COEF_1.BY_1C);
        regress.regress();
        System.out.println(pulldownResistor + "R rmsd:" + regress.getRMSD());
        for (int lux = 200; lux <= 163840; lux += 100) {
            double lightness = luxToLightness(lux);
            double outputvoltage = regress.getPredict(new double[] {lightness})[0];
            plot.addCacheScatterLinePlot(Double.toString(pulldownResistor) + "-reg", Color.black, lightness,
                                         outputvoltage);
        }
        boolean show163840V = false;
        if (show163840V) {
            System.out.println(pulldownResistor + "R " + luxToVoltage(maxVoltage, 163840, pulldownResistor, loglut) +
                               "V @163840");
        }
    }


    public static void outDiagram(String[] args) {

        final int K = 1000;
        for (int newpos = 3; newpos <= 5; newpos++) {
            double down = Interpolation.linear(1, 2, Math.log10(45 * K), Math.log10(10 * K), newpos);
            double up = Interpolation.linear(1, 2, Math.log10(140 * K), Math.log10(20 * K), newpos);
            System.out.println(Math.pow(10, newpos) + ": " + Math.pow(10, down) + " " + Math.pow(10, up));
        }
    }

    public static void inDiagram(String[] args) {

        final int M = 1000000;
        final int K = 1000;
        double gamma = .85;
        Plot2D plot = Plot2D.getInstance(Double.toString(gamma));
        for (int pos = 10; pos <= 100; pos++) {
//            double loglux = Math.log10(lux);
//            double down = Interpolation.linear(Math.log(1), Math.log(10), 110 * K, 45 * K, loglux);
//            double up = Interpolation.linear(Math.log(1), Math.log(10), 1000 * K, 140 * K, loglux);
            double normpos = (pos - 10.) / (100 - 10);
            double gammapos = Math.pow(normpos, gamma);
            double newpos = 10 + (100 - 10) * gammapos;
            newpos = pos;

            double down = Interpolation.linear(10, 100, 45 * K, 10 * K, newpos);
            double up = Interpolation.linear(10, 100, 140 * K, 20 * K, newpos);
            double loglux = Math.log10(pos);
            double base1 = Math.pow(10, ((int) loglux));
            double base10 = Math.pow(10, ((int) loglux) + 1);
            double p = ((int) Math.log10(pos)) + (pos - base1) / (base10 - base1);
            double normp = p - ((int) Math.log10(pos));
//            Math.pow(10,loglux-1)
            double logvalue = Math.pow(10, p);

            plot.addCacheScatterLinePlot("dw", logvalue, down);
            plot.addCacheScatterLinePlot("up", logvalue, up);
            System.out.println(pos + " " + newpos + " " + up + " " + down + " " + normp + " " + p);
        }
//        for (int x = 1; x <= 10000; x++) {
//            plot.addCacheScatterLinePlot("", x, x);
//        }
//        plot.setAxisScale(1, Plot2D.Scale.Log);
        plot.setVisible();
        plot.setFixedBounds(0, 1, 100);
        plot.setFixedBounds(1, 1, 1000);
        plot.setAxisScale(0, Plot2D.Scale.Log);
        plot.setAxisScale(1, Plot2D.Scale.Log);

    }
}
