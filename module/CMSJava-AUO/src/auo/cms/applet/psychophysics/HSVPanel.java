package auo.cms.applet.psychophysics;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.VerticalFlowLayout;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Dimension;
import java.beans.PropertyChangeSupport;
import java.util.*;

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
public class HSVPanel
    extends JPanel {
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JPanel jPanel1 = new JPanel();
  protected JSlider jSlider_V = new JSlider();
  protected JLabel jLabel_V = new JLabel();
  protected JPanel jPanel2 = new JPanel();
  protected JLabel jLabel_S = new JLabel();
  protected JSlider jSlider_S = new JSlider();
  protected JPanel jPanel3 = new JPanel();
  protected JLabel jLabel_H = new JLabel();
  protected JSlider jSlider_H = new JSlider();
  protected JLabel jLabel_HValue = new JLabel();
  protected JLabel jLabel_SValue = new JLabel();
  protected JLabel jLabel_VValue = new JLabel();
//  PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  public HSVPanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  void reset() {
    this.jSlider_H.setValue(0);
    this.jSlider_S.setValue(0);
    this.jSlider_V.setValue(100);
  }

  void setColor(int h, int s, int v) {
    this.jSlider_H.setValue(h);
    this.jSlider_S.setValue(s);
    this.jSlider_V.setValue(v);
  }

  private void jbInit() throws Exception {
    this.setLayout(verticalFlowLayout1);
    jLabel_V.setText("V");
    jLabel_S.setText("S");
    jLabel_H.setText("H");
    jLabel_HValue.setPreferredSize(new Dimension(30, 15));
    jLabel_HValue.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel_HValue.setText("0");
    jLabel_SValue.setPreferredSize(new Dimension(30, 15));
    jLabel_SValue.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel_SValue.setText("0");
    jLabel_VValue.setPreferredSize(new Dimension(30, 15));
    jLabel_VValue.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel_VValue.setText("0");
    jSlider_H.setMaximum(360);
    jSlider_H.setValue(0);
    jSlider_H.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider_H_stateChanged(e);
      }
    });
    jSlider_S.setValue(0);
    jSlider_S.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider_H_stateChanged(e);
      }
    });
    jSlider_V.setValue(100);
    jSlider_V.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider_H_stateChanged(e);
      }
    });
    verticalFlowLayout1.setVgap(0);
    this.add(jPanel3);
    jPanel3.add(jLabel_H);
    jPanel3.add(jSlider_H);
    jPanel3.add(jLabel_HValue);
    this.add(jPanel2);
    jPanel2.add(jLabel_S);
    jPanel2.add(jSlider_S);
    jPanel2.add(jLabel_SValue);
    this.add(jPanel1);
    jPanel1.add(jLabel_V);
    jPanel1.add(jSlider_V);
    jPanel1.add(jLabel_VValue);
  }

  public void jSlider_H_stateChanged(ChangeEvent e) {
    this.jLabel_HValue.setText(Integer.toString(this.jSlider_H.getValue()));
    this.jLabel_SValue.setText(Integer.toString(this.jSlider_S.getValue()));
    this.jLabel_VValue.setText(Integer.toString(this.jSlider_V.getValue()));
    this.firePropertyChange("HSV", 0, 1);
  }

  public int[] getHSVValue() {
    return new int[] {
        jSlider_H.getValue(), jSlider_S.getValue(), jSlider_V.getValue()};
  }

}
