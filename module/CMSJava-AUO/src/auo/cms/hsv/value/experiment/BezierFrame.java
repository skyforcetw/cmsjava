package auo.cms.hsv.value.experiment;

import javax.swing.*;
import javax.swing.event.*;

import com.borland.jbcl.layout.*;
import shu.cms.plot.*;
///import shu.plot.*;

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
public class BezierFrame
    extends JFrame {
  protected XYLayout xYLayout1 = new XYLayout();
  protected JSlider jSlider1 = new JSlider();
  protected JSlider jSlider2 = new JSlider();
  public BezierFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(xYLayout1);
    this.setSize(400, 300);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    xYLayout1.setWidth(400);
//    xYLayout1.setHeight(200);
    jSlider1.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider1_stateChanged(e);
      }
    });
    jSlider2.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider2_stateChanged(e);
      }
    });
    jLabel1.setText("jLabel1");
    jLabel2.setText("jLabel1");
    jLabel3.setText("100");
    jLabel4.setText("100");
    jSlider3.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider3_stateChanged(e);
      }
    });
    jSlider4.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider4_stateChanged(e);
      }
    });
//    xYLayout1.setWidth(400);
//    xYLayout1.setHeight(200);
    jCheckBox1.setText("link");
    xYLayout1.setWidth(400);
    xYLayout1.setHeight(300);
    jCheckBox2.setText("Bezier2");
    jCheckBox3.setText("mod2");
    jCheckBox4.setText("Bezier3");
    jCheckBox5.setText("mod3");
    jSlider1.setMaximum(150);
    jSlider1.setMinimum( -50);
    jSlider2.setMinimum( -50);
    this.getContentPane().add(jSlider1, new XYConstraints(21, 21, 313, 24));
    this.getContentPane().add(jSlider2, new XYConstraints(21, 57, 313, -1));
    this.getContentPane().add(jLabel1, new XYConstraints(342, 21, -1, -1));
    this.getContentPane().add(jLabel2, new XYConstraints(342, 57, -1, -1));
    this.getContentPane().add(jSlider4, new XYConstraints(21, 135, 313, -1));
    this.getContentPane().add(jSlider3, new XYConstraints(21, 98, 313, 22));
    this.getContentPane().add(jLabel3, new XYConstraints(342, 98, -1, -1));
    this.getContentPane().add(jLabel4, new XYConstraints(342, 135, -1, -1));
    this.getContentPane().add(jCheckBox1, new XYConstraints(21, 165, -1, -1));
    this.getContentPane().add(jCheckBox4, new XYConstraints(21, 231, -1, -1));
    this.getContentPane().add(jCheckBox5, new XYConstraints(21, 253, -1, -1));
    this.getContentPane().add(jCheckBox2, new XYConstraints(21, 187, -1, -1));
    this.getContentPane().add(jCheckBox3, new XYConstraints(21, 209, -1, -1));
    jSlider1.setValue(50);
    jSlider2.setValue(100);
    jSlider3.setValue(87);
    jSlider4.setValue(25);

    plot2d.setVisible();
  }

  void updatePlot() {
    plot2d.removeAllPlots();
    for (int index = 0; index < piece; index++) {
      double t = ( (double) (index) / (piece - 1));
      double x = 2 * t * (1 - t) * p1x + t * t;
//        double x = t * t;
      double y = 2 * t * (1 - t) * p1y;

      double x2 = (1 - t) * (t * (2 * p1x - 1) + 1);
      double y2 = 2 * x2 * (1 - x2) * p1y;

      double x3 = 3 * p1x * t * Math.pow(1 - t, 2) + 3 * p2x * t * t * (1 - t) +
          Math.pow(t, 3);
      double y3 = 3 * p1y * t * Math.pow(1 - t, 2) + 3 * p2y * t * t * (1 - t);

      double x4 = 3 * p1x * t * Math.pow(1 - t, 2) + 3 * p2x * t * t * (1 - t) +
          Math.pow(t, 3);
      double y4 = 3 * p1y * x4 * Math.pow(1 - x4, 2) +
          3 * p2y * x4 * x4 * (1 - x4);
//      double y4 = 2 * p1y * x /* Math.pow(1 - x, 2)*/ +
//          2 * p2y * x * x * (1 - x);
//      double y4 = 2 * x * (p1y + p2y * x * (1 - x));

      if (this.jCheckBox2.isSelected()) {
        plot2d.addCacheScatterLinePlot("Bezier2", x, y);
      }
      if (this.jCheckBox3.isSelected()) {
        plot2d.addCacheScatterLinePlot("mod2", t, y2);
      }
      if (this.jCheckBox4.isSelected()) {
        plot2d.addCacheScatterLinePlot("Bezier3", x3, y3);
      }
//      if (this.jCheckBox5.isSelected()) {
//        plot.addCacheScatterLinePlot("mod3", x4, y4);
//      }
//        plot.addCacheScatterLinePlot("mod3", x , y4);
    }

    plot2d.drawCachePlot();
    plot2d.addLegend();
    plot2d.setFixedBounds(1, 0, 0.5);
    plot2d.setFixedBounds(0, 0, 1);
  }

  Plot2D plot2d = Plot2D.getInstance();
  Plot3D plot3d = Plot3D.getInstance();
  int piece = 30;

  public static void main(String[] args) {
    final BezierFrame frame = new BezierFrame();
    java.awt.EventQueue.invokeLater(new Runnable() {

      public void run() {
//                HueSimulationFrame frame = new HueSimulationFrame();
        frame.setVisible(true);
      }
    });

  }

  double p1x, p1y, p2x, p2y;
  protected JLabel jLabel1 = new JLabel();
  protected JLabel jLabel2 = new JLabel();
  protected JSlider jSlider3 = new JSlider();
  protected JSlider jSlider4 = new JSlider();
  protected JLabel jLabel3 = new JLabel();
  protected JLabel jLabel4 = new JLabel();
  protected JCheckBox jCheckBox1 = new JCheckBox();
  protected JCheckBox jCheckBox2 = new JCheckBox();
  protected JCheckBox jCheckBox3 = new JCheckBox();
  protected JCheckBox jCheckBox4 = new JCheckBox();
  protected JCheckBox jCheckBox5 = new JCheckBox();
  public void jSlider1_stateChanged(ChangeEvent e) {

    int v1 = this.jSlider1.getValue();

    if (e.getSource() == jSlider1 && this.jCheckBox1.isSelected()) {
      int newv3 = (int) (v1 * 0.25) + 75;
      jSlider3.setValue(newv3);
    }
    int v2 = this.jSlider2.getValue();
    int v3 = this.jSlider3.getValue();
    int v4 = this.jSlider4.getValue();

    this.jLabel1.setText(Integer.toString(v1));
    this.jLabel2.setText(Integer.toString(v2));
    this.jLabel3.setText(Integer.toString(v3));
    this.jLabel4.setText(Integer.toString(v4));
    p1x = v1 / 100.;
    p1y = v2 / 100.;
    p2x = v3 / 100.;
    p2y = v4 / 100.;
    updatePlot();
  }

  public void jSlider2_stateChanged(ChangeEvent e) {
    jSlider1_stateChanged(e);
    if (this.jCheckBox1.isSelected()) {
      int v2 = this.jSlider2.getValue();
      jSlider4.setValue(v2 / 4);
    }
  }

  public void jSlider3_stateChanged(ChangeEvent e) {
    jSlider1_stateChanged(e);

  }

  public void jSlider4_stateChanged(ChangeEvent e) {
    jSlider1_stateChanged(e);
  }
}
