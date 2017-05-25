package auo.cms.applet.hue;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.XYLayout;
import com.borland.jbcl.layout.*;
import java.awt.Dimension;

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
public class AdjustPanel
    extends JPanel {
  protected GridBagLayout gridBagLayout1 = new GridBagLayout();
  protected JLabel jLabel1 = new JLabel();
  protected JSlider jSlider_Saturation = new JSlider();
  protected JLabel jLabel2 = new JLabel();
  protected JSlider jSlider_Value = new JSlider();
  protected JSlider jSlider_Hue = new JSlider();
  protected JLabel jLabel3 = new JLabel();
  protected JLabel jLabel4 = new JLabel();
  protected JLabel jLabel5 = new JLabel();
  protected JLabel jLabel6 = new JLabel();
  public AdjustPanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(gridBagLayout1);
    this.setPreferredSize(new Dimension(300, 55));
    jLabel1.setText("0");
    jLabel2.setText("H");
    jLabel3.setText("S");
    jLabel4.setText("V");
    jLabel5.setText("0.00");
    jLabel6.setText("1.00");
    this.add(jSlider_Saturation, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                              , GridBagConstraints.CENTER,
                                              GridBagConstraints.NONE,
                                              new Insets(0, 0, 0, 0), 0, 0));
    this.add(jSlider_Value, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                                              , GridBagConstraints.CENTER,
                                              GridBagConstraints.NONE,
                                              new Insets(0, 0, 0, 0), 0, 0));
    this.add(jSlider_Hue, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                              , GridBagConstraints.CENTER,
                                              GridBagConstraints.NONE,
                                              new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.NONE,
                                             new Insets(0, 0, 0, 0), 10, 0));
    this.add(jLabel4, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.NONE,
                                             new Insets(0, 0, 0, 0), 10, 0));
    this.add(jLabel5, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.NONE,
                                             new Insets(0, 0, 0, 0), 10, 0));
    this.add(jLabel6, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.NONE,
                                             new Insets(0, 0, 0, 0), 10, 0));
    this.add(jLabel1, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.NONE,
                                             new Insets(0, 0, 0, 0), 10, 0));
    this.add(jLabel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.NONE,
                                             new Insets(0, 0, 0, 0), 10, 0));
  }
}
