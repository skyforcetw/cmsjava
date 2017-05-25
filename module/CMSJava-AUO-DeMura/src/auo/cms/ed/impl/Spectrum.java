package auo.cms.ed.impl;

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
public class Spectrum {
    public Spectrum(double[] radialFrequency, double[] power) {
        this.radialFrequency = radialFrequency;
        this.power = power;
    }

    private double[] radialFrequency;
    private double[] power;


    public double[] getPower() {
        return power;
    }

    public double[] getRadialFrequency() {
        return radialFrequency;
    }

    private double[] frequency;
    public double[] getFrequency() {
        if (null == frequency) {
            int size = radialFrequency.length;
            frequency = new double[size];
            for (int x = 0; x < size; x++) {
                frequency[x] = radialFrequency[x] * 2 * Math.PI;
            }
        }
        return frequency;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        int size = radialFrequency.length;
        StringBuilder b = new StringBuilder();
        for (int x = 0; x < size; x++) {
            b.append(radialFrequency[x]);
            b.append(' ');
            b.append(power[x]);
            b.append('\n');
        }
        return b.toString();
    }
}
