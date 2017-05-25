package org.math.examples;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.math.array.*;
import org.math.plot.*;
import shu.util.Utils;
import shu.ui.GUIUtils;

/**
 * BSD License
 * @author Yann RICHET
 */

public class AllPlots2DApplet
    extends Applet implements ActionListener {

  public static void main(String[] args) {
    GUIUtils.startAppletAsApplicaiton(new AllPlots2DApplet());
  }

  public double[][] datas;

  String[] plots = new String[] {
      "scatter plot", "bar plot", "line plot", "staircase plot", "box plot",
      "histogram"};

  JComboBox choice;

  Plot2DPanel plotpanel;

  JPanel panel;

  //Initialiser le composant
  public void init() {
    choice = new JComboBox(plots);
    plotpanel = new Plot2DPanel();
    plotpanel.addLegend("SOUTH");
    plotpanel.setSize(600, 600);
    plotpanel.setPreferredSize(new Dimension(600, 600));
//    this.setSize(600,600);

    choice.addActionListener(this);

    panel = new JPanel(new BorderLayout());
    panel.add(choice, BorderLayout.NORTH);
    panel.add(plotpanel, BorderLayout.CENTER);

    this.add(panel);

  }

  public void actionPerformed(ActionEvent actionEvent) {
    int N = 20;
    int I = choice.getSelectedIndex();
    plotpanel.removeAllPlots();
    if (I == 0) {
      double[][] data = new double[N][2];
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data[i].length; j++) {
          data[i][j] = Math.random();
        }
      }
      plotpanel.addScatterPlot("data", data);
    }
    else if (I == 1) {
      double[][] data = new double[N][2];
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data[i].length; j++) {
          data[i][j] = Math.random();
        }
      }
      plotpanel.addBarPlot("data", data);
    }
    else if (I == 2) {
      double[][] data = new double[N][2];
      for (int i = 0; i < data.length; i++) {
        data[i][0] = i + 1;
        data[i][1] = Math.sin( (double) i / N * Math.PI);
      }
      plotpanel.addLinePlot("data", data);
    }
    else if (I == 3) {
      double[][] data = new double[N][2];
      for (int i = 0; i < data.length; i++) {
        data[i][0] = i + 1;
        data[i][1] = Math.sin( (double) i / N * Math.PI);
      }
      plotpanel.addStaircasePlot("data", data);
    }
    else if (I == 4) {
      double[][] data = new double[N][4];
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < 2; j++) {
          data[i][j] = Math.random();
        }
        data[i][2] = 0.1;
        data[i][3] = 0.2;
      }
      plotpanel.addBoxPlot("data", data);
    }
    else if (I == 5) {
      double[] data = StatisticSample.randomLogNormal(50 * N, 5, 10);
      plotpanel.addHistogramPlot("data", data, N);
    }
  }

}
