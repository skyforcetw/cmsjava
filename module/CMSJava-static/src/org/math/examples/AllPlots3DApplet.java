package org.math.examples;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.math.plot.*;
import shu.util.Utils;
import shu.ui.GUIUtils;

/**
 * BSD License
 * @author Yann RICHET
 */

public class AllPlots3DApplet
    extends Applet implements ActionListener {

  public static void main(String[] args) {
//    JFrame frame = new JFrame();
//    AllPlots3DApplet p = new AllPlots3DApplet();
//    frame.add(p);
//    p.init();
//    frame.setSize(300, 300);
//    frame.setVisible(true);
    GUIUtils.startAppletAsApplicaiton(new AllPlots3DApplet());
  }

  public double[][] datas;

  String[] plots = new String[] {
      "scatter plot", "bar plot", "box plot", "3d histogram", "grid"};

  JComboBox choice;

  Plot3DPanel plotpanel;

  JPanel panel;

  //Initialiser le composant
  public void init() {
    choice = new JComboBox(plots);
    plotpanel = new Plot3DPanel();
    plotpanel.addLegend("SOUTH");
    plotpanel.setSize(600, 600);
    plotpanel.setPreferredSize(new Dimension(600, 600));

    choice.addActionListener(this);

    panel = new JPanel(new BorderLayout());
    panel.add(choice, BorderLayout.NORTH);
    panel.add(plotpanel, BorderLayout.CENTER);

    this.add(panel);

  }

  public void actionPerformed(ActionEvent actionEvent) {
    int N = 10;
    int I = choice.getSelectedIndex();
    plotpanel.removeAllPlots();
    if (I == 0) {
      double[][] data = new double[N][3];
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data[i].length; j++) {
          data[i][j] = Math.random();
        }
      }
      plotpanel.addScatterPlot("data", data);
    }
    else if (I == 1) {
      double[][] data = new double[N][3];
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data[i].length; j++) {
          data[i][j] = Math.random();
        }
      }
      plotpanel.addBarPlot("data", data);
    }
    else if (I == 2) {
      double[][] data = new double[N][6];
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < 3; j++) {
          data[i][j] = Math.random();
        }
        data[i][3] = 0.1;
        data[i][4] = 0.2;
        data[i][5] = 0.3;
      }
      plotpanel.addBoxPlot("data", data);
    }
    else if (I == 3) {
      double[][] data = new double[N * N][5];
      for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
          data[j + N * i][0] = i + 1;
          data[j + N * i][1] = j + 1;
          data[j + N * i][2] = Math.exp( - (i + 1) * (j + 1));
          data[j + N * i][3] = 1;
          data[j + N * i][4] = 1;
        }
      }
      plotpanel.addHistogramPlot("data", data);
    }
    else if (I == 4) {
      double[][] data = new double[N][N];
      double[] Xdata = new double[N];
      for (int i = 0; i < N; i++) {
        Xdata[i] = i - N / 2;
        for (int j = 0; j < N; j++) {
          data[i][j] = Math.sin( (double) i / N * Math.PI) *
              Math.cos( (double) j / N * Math.PI);
        }
      }
      plotpanel.addGridPlot("data", Xdata, Xdata, data);
    }
  }

}
