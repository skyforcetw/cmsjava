package auo.cms.hsv.value.experiment;

import java.awt.*;
import javax.swing.*;
import java.awt.BorderLayout;
import com.borland.jbcl.layout.OverlayLayout2;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import shu.ui.GUIUtils;
import shu.cms.plot.Plot2D;
import java.awt.Dimension;
//import shu.plot.*;

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
public class PolynomialPotterFrame
    extends JFrame {
  protected GridBagLayout gridBagLayout1 = new GridBagLayout();
  protected JSlider jSlider1 = new JSlider();
  protected JSlider jSlider2 = new JSlider();
  protected JSlider jSlider3 = new JSlider();
  protected JLabel jLabel1 = new JLabel();
  protected JLabel jLabel2 = new JLabel();
  protected JLabel jLabel3 = new JLabel();
  Plot2D plot = Plot2D.getInstance();
  public PolynomialPotterFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setSize(600, 150);
    getContentPane().setLayout(gridBagLayout1);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jSlider1.setMinimum( -1000);
    jSlider1.setValue(0);
    jSlider1.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider1_stateChanged(e);
      }
    });
    jSlider2.setMinimum( -1000);
    jSlider2.setValue(0);
    jSlider2.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider2_stateChanged(e);
      }
    });
    jSlider3.setMinimum( -1000);
    jSlider3.setValue(0);
    jSlider3.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider3_stateChanged(e);
      }
    });
    jLabel5.setText("jLabel5");
    jLabel6.setText("y+極值");
    jLabel8.setText("y-極值");
    jLabel1.setMinimumSize(new Dimension(50, 15));
    jLabel1.setPreferredSize(new Dimension(150, 15));
    jLabel3.setPreferredSize(new Dimension(150, 15));
    jLabel2.setPreferredSize(new Dimension(150, 15));
    jLabel3.setText("jLabel3");
    jLabel2.setText("jLabel2");
    jLabel1.setText("jLabel1");
    jSlider1.setMaximum(1000);
    jSlider2.setMaximum(1000);
    jSlider3.setMaximum(1000);
    plot.setVisible();
    jSlider4.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider4_stateChanged(e);
      }
    });
    this.getContentPane().add(jSlider1,
                              new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    jSlider4.setMaximum(5);
    jSlider4.setValue(0);
    this.getContentPane().add(jSlider2,
                              new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jSlider3,
                              new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jLabel1,
                              new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jLabel2,
                              new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jLabel3,
                              new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jLabel5,
                              new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jLabel6,
                              new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jLabel8,
                              new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jSlider4,
                              new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
  }

  private void udpatePlot() {
    int piece = 256;
    plot.removeAllPlots();
    for (int x = 0; x < piece; x++) {
      double v = vv1 * x + vv2 * x * x + vv3 * x * x * x;
      plot.addCacheScatterLinePlot("", Color.black, x, v);
    }
    plot.addLinePlot("0", Color.red, 0, 0, 255, 0);
    plot.drawCachePlot();

    double a = vv3, b = vv2, c = vv1;
//    double xp = ( -b + Math.sqrt(b * b - 3 * a * c)) / 3 * a;
//    double xn = ( -b - Math.sqrt(b * b - 3 * a * c)) / 3 * a;
    double yp = -c * (Math.sqrt(b * b - 3 * a * c) - b) / (8 * a);
    double yn = -yp;
//    null.setText(Double.toString(yp));
//    null.setText(Double.toString(yn));
  }

  public static void main(String[] args) {
    new PolynomialPotterFrame().setVisible(true);
  }

  private double vv1, vv2, vv3;
  protected JLabel jLabel5 = new JLabel();
  protected JLabel jLabel6 = new JLabel();
  protected JLabel jLabel8 = new JLabel();
  protected JSlider jSlider4 = new JSlider();
  public void jSlider1_stateChanged(ChangeEvent e) {
    int v1 = this.jSlider1.getValue();
    int v2 = this.jSlider2.getValue();
    int v3 = this.jSlider3.getValue();
    vv1 = ( (double) v1) / 1000.;
    vv2 = ( (double) v2) / 100000.;
    double c = Math.pow(10, jSlider4.getValue());
    this.jLabel5.setText(Double.toString(c));
    vv3 = ( (double) v3) / (1000000. * c);
    this.jLabel1.setText(Double.toString(vv1));
    this.jLabel2.setText(Double.toString(vv2));
    this.jLabel3.setText(Double.toString(vv3));
    udpatePlot();
  }

  public void jSlider2_stateChanged(ChangeEvent e) {
    jSlider1_stateChanged(e);
  }

  public void jSlider3_stateChanged(ChangeEvent e) {
    jSlider1_stateChanged(e);
  }

  public void jSlider4_stateChanged(ChangeEvent e) {
    jSlider1_stateChanged(e);
  }
}
