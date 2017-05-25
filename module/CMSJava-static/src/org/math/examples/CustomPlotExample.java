package org.math.examples;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;

import static org.math.array.StatisticSample.*;
import org.math.plot.*;
import org.math.plot.plotObjects.BaseLabel;

public class CustomPlotExample {
  public static void main(String[] args) {

    // define your data
    double[] x = randomNormal(1000, 0, 1); // 1000 random numbers from a normal (Gaussian) statistical law
    double[] y = randomUniform(1000, -3, 3); // 1000 random numbers from a uniform statistical law

    // create your PlotPanel (you can use it as a JPanel)
    Plot2DPanel plot = new Plot2DPanel();
//    Plot3DPanel plot = new Plot3DPanel();

    // legend at SOUTH
    plot.addLegend("SOUTH");

    // add the histogram (50 slices) of x to the PlotPanel
    plot.addHistogramPlot("Gaussian population", x, 50);

    // add the histogram (50 slices) of y to the PlotPanel in GREEN
    plot.addHistogramPlot("Uniform population", Color.RED, y, 50);

    // add a title
    BaseLabel title = new BaseLabel("...My nice plot...", Color.RED, 0.5, 1.1);
    title.setFont(new Font("Courier", Font.BOLD, 20));
    plot.addPlotable(title);

    // change name of axes
    plot.setAxisLabels("<X>", "frequency");

    // customize X axe
    // rotate light labels
    plot.getAxis(0).setLightLabelAngle( -PI / 4);
    // change axe title position relatively to the base of the plot
    plot.getAxis(0).setLabelPosition(0.5, -0.15);
//    plot.getAxis(0).setLabelAngle( -PI / 4);

    // customize Y axe
    // rotate light labels
    plot.getAxis(1).setLightLabelAngle( -PI / 4);
    // change axe title position relatively to the base of the plot
    plot.getAxis(1).setLabelPosition( -0.15, 0.5);
    // change axe title angle
    plot.getAxis(1).setLabelAngle( -PI / 2);

    // put the PlotPanel in a JFrame like a JPanel
    JFrame frame = new JFrame("a plot panel");
    frame.setSize(600, 600);
    frame.setContentPane(plot);
    frame.setVisible(true);

  }
}
