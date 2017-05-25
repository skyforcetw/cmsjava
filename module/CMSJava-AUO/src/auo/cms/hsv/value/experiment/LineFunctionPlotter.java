/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auo.cms.hsv.value.experiment;

import java.awt.Color;
import shu.cms.plot.Plot2D;
//import shu.plot.*;

/**
 *
 * @author SkyforceShen
 */
public class LineFunctionPlotter {

    public static void main(String[] args) {


        double c1 = 1;
        double c2 = 1;
        double c3 = 1;
        double c = 1;
//        double p0 = 0;
        double p1x = .55;
        double p1y = .7;
//        double p2 = 0;

        Plot2D plot = Plot2D.getInstance(Double.toString(p1x) + " " + Double.toString(p1y));

        for (double d = 0; d <= 100; d += 10) {
            double y = c3 * d * d * d + c2 * d * d + c1 * d + c;
//            plot.addCacheScatterLinePlot("3", d, y);

            double t = d / 100;
//            double b = Math.pow(1 - t, 2) * p0 + 2 * t * (1 - t) * p1 + t * t * p2;
            double px = 2 * t * (1 - t) * p1x + t * t * 1;
            double py = 2 * t * (1 - t) * p1y;
            plot.addCacheScatterLinePlot("be", px, py);
        }

        plot.setVisible();
    }
}
