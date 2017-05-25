package auo.cms.hsv.autotune.ui;

import java.beans.*;

import javax.swing.*;
import javax.swing.event.*;

import com.borland.jbcl.layout.*;

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
public class HSVAdjustFrame
    extends JFrame {
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JPanel jPanel1 = new JPanel();
  protected JPanel jPanel2 = new JPanel();
  protected JPanel jPanel3 = new JPanel();
  protected JLabel jLabel1 = new JLabel();
  public JSlider jSlider1 = new JSlider();
  public JSlider jSlider2 = new JSlider();
  protected JLabel jLabel2 = new JLabel();
  public JSlider jSlider3 = new JSlider();
  protected JLabel jLabel3 = new JLabel();

  public HSVAdjustFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private HSVAdjustFrame frame = this;
  protected JPanel jPanel4 = new JPanel();
  public JSlider jSlider4 = new JSlider();
  protected JLabel jLabel4 = new JLabel();

  private void jbInit() throws Exception {
    getContentPane().setLayout(verticalFlowLayout1);
    jSlider1.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider1_stateChanged(e);
      }
    });
    jSlider2.setMinimum( -64);
    jSlider3.setMinimum( -64);
    jSlider3.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider3_stateChanged(e);
//        frame.firePropertyChange();
      }
    });
    this.getContentPane().add(jPanel4);
    jLabel4.setText("0");
    jSlider4.setMaximum(360);
    jSlider4.setValue(180);
    jSlider4.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider4_stateChanged(e);
      }
    });
    jPanel4.add(jLabel4);
    jPanel4.add(jSlider4);
    this.getContentPane().add(jPanel3);
    jLabel2.setText("0");
    jLabel3.setText("jLabel3");
    jSlider1.setValue(0);
    jPanel3.add(jLabel1);
    jPanel3.add(jSlider1);
    jLabel1.setText("jLabel1");
    this.getContentPane().add(jPanel2);
    jPanel2.add(jLabel2);
    jPanel2.add(jSlider2);
    this.getContentPane().add(jPanel1);
    jPanel1.add(jLabel3);
    jPanel1.add(jSlider3);
    jSlider1.setMaximum(768);
    jSlider2.setMaximum(64);
    jSlider3.setMaximum(64);
    jSlider3.setValue(0);
    jSlider2.setValue(0);
    jSlider2.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider2_stateChanged(e);

      }
    });
    this.setSize(600, 600);
  }

  static class Listener
      implements PropertyChangeListener {
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and
     *   the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
      System.out.println(evt);
    }

  }

  public static void main(String[] args) {
    HSVAdjustFrame frame1 = new HSVAdjustFrame();
    frame1.setVisible(true);
    frame1.addPropertyChangeListener(new Listener());
  }

  public void jSlider1_stateChanged(ChangeEvent e) {
    this.jLabel1.setText(Integer.toString(this.jSlider1.getValue()));
    this.firePropertyChange("", 0, 1);
  }

  public void jSlider2_stateChanged(ChangeEvent e) {
    this.jLabel2.setText(Integer.toString(this.jSlider2.getValue()));
    this.firePropertyChange("", 0, 1);
  }

  public void jSlider3_stateChanged(ChangeEvent e) {
    this.jLabel3.setText(Integer.toString(this.jSlider3.getValue()));
    this.firePropertyChange("", 0, 1);
  }

  public void jSlider4_stateChanged(ChangeEvent e) {
    this.jLabel4.setText(Integer.toString(this.jSlider4.getValue()));
    this.firePropertyChange("", 0, 1);
  }
}
