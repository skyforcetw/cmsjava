package shu.cms.applet.legend;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 可以進行儀器校正係數的產生,以及儀器的校正.
 * 但是儀器校正係數產生的畫面還沒開發,尚無法使用
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
@SuppressWarnings(value = {
                  "deprecation"})
public class InstrumentCalibratorFrame
    extends JFrame {
//  BorderLayout borderLayout1 = new BorderLayout();
  JFileChooser jFileChooser1;
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton jButton_input = new JButton();
  JTextField jTextField_input = new JTextField();
  JButton jButton_coefs = new JButton();
  File coefsCef = new File(cefFile);
  File outputCxFDir = new File(outputDir);
  JTextField jTextField_coefs = new JTextField(coefsCef.getAbsolutePath());
  JButton jButton_output = new JButton();
  JTextField jTextField_output = new JTextField(outputCxFDir.getAbsolutePath());

  File inputCxF;

  JButton jButton_calibrate = new JButton();
  JLabel jLabel_order = new JLabel();
  JTextField jTextField_order = new JTextField();

  public InstrumentCalibratorFrame() {
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   *
   * @throws Exception
   * @deprecated
   */
  private void jbInit() throws Exception {
    new Thread() {
      public void run() {
        jFileChooser1 = new JFileChooser();
        jFileChooser1.setCurrentDirectory(new File(System.getProperty(
            "user.dir")));
      }
    }.start();

//    getContentPane().setLayout(borderLayout1);
    setSize(new Dimension(500, 400));
    setTitle("儀器校正");
    jPanel2.setLayout(gridBagLayout1);
    jButton_input.setText("校正儀器 CxF");
    jButton_input.addActionListener(new
                                    InstrumentCalibratorFrame_jButton_input_actionAdapter(this));
    jButton_coefs.setText("係數 Cef");
    jButton_coefs.addActionListener(new
                                    InstrumentCalibratorFrame_jButton_coefs_actionAdapter(this));
    jButton_output.setText("輸出 CxF");
    jButton_output.addActionListener(new
                                     InstrumentCalibratorFrame_jButton_output_actionAdapter(this));
    jButton_calibrate.setText("校正");
    jButton_calibrate.addActionListener(new
                                        InstrumentCalibratorFrame_jButton_calibrate_actionAdapter(this));
    jLabel_order.setText("項次");
    jTextField_order.setText("2");
    jTextField_input.setEditable(false);
    jTextField_coefs.setEditable(false);
    jTextField_output.setEditable(false);
//    jTextField_output.setText(outputCxFDir.getAbsolutePath());
    this.getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    jTabbedPane1.add("係數產生", jPanel1);
    jTabbedPane1.add("儀器校正", jPanel2);
    jPanel2.add(jButton_input, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER,
        GridBagConstraints.NONE,
        new Insets(3, 0, 3, 0), 0, 0));
    jPanel2.add(jButton_coefs, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER,
        GridBagConstraints.NONE,
        new Insets(3, 0, 3, 0), 0, 0));
    jPanel2.add(jTextField_input, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(3, 10, 3, 0), 250, 0));
    jPanel2.add(jTextField_coefs, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(3, 10, 3, 0), 250, 0));
    jPanel2.add(jTextField_output, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(6, 10, 3, 0), 250, 0));
    jPanel2.add(jButton_output, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(3, 0, 3, 0), 0, 0));
    jPanel2.add(jLabel_order, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER,
        GridBagConstraints.NONE,
        new Insets(3, 0, 3, 0), 0, 0));
    jPanel2.add(jTextField_order, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 50, 0));
    jPanel2.add(jButton_calibrate, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(10, 0, 12, 0), 0, 0));
  }

  public static void main(String[] args) {
    InstrumentCalibratorFrame frame = new InstrumentCalibratorFrame();
    frame.setVisible(true);
    /*
     Properties p = System.getProperties();
         Enumeration e = p.propertyNames();
         while (e.hasMoreElements()) {
      System.out.println(e.nextElement());
         }
         System.out.println(System.getProperty("user.dir"));
     */
  }

  public void jButton_input_actionPerformed(ActionEvent e) {
    if (inputCxF != null) {
      jFileChooser1.setCurrentDirectory(inputCxF.getParentFile());
    }
    jFileChooser1.showOpenDialog(this);
    inputCxF = jFileChooser1.getSelectedFile();
    if (inputCxF != null) {
      this.jTextField_input.setText(inputCxF.getAbsolutePath());
    }
  }

  public final static String cefFile =
      "Measurement Files/Calibration/Training_29_380-730.cef";

  public final static String outputDir =
      "Measurement Files/";

  public void jButton_coefs_actionPerformed(ActionEvent e) {
    if (coefsCef != null) {
      jFileChooser1.setCurrentDirectory(coefsCef.getParentFile());
    }
    jFileChooser1.showOpenDialog(this);
    coefsCef = jFileChooser1.getSelectedFile();
    if (coefsCef != null) {
      this.jTextField_coefs.setText(coefsCef.getAbsolutePath());
    }
  }

  public void jButton_output_actionPerformed(ActionEvent e) {
    if (coefsCef != null) {
      jFileChooser1.setCurrentDirectory(coefsCef.getParentFile());
    }
    jFileChooser1.setFileSelectionMode(jFileChooser1.DIRECTORIES_ONLY);
    jFileChooser1.showOpenDialog(this);
    outputCxFDir = jFileChooser1.getSelectedFile();
    jFileChooser1.setFileSelectionMode(jFileChooser1.FILES_AND_DIRECTORIES);
    if (outputCxFDir != null) {
      this.jTextField_output.setText(outputCxFDir.getAbsolutePath());
    }
  }

  /**
   *
   * @param e ActionEvent
   * @deprecated
   */
  public void jButton_calibrate_actionPerformed(ActionEvent e) {
//    if (inputCxF != null && coefsCef != null && outputCxFDir != null) {
//      InstrumentCalibrator calibrator = new InstrumentCalibrator(
//          inputCxF.getAbsolutePath(),
//          coefsCef.getAbsolutePath());
//      int order = Integer.parseInt(this.jTextField_order.getText());
//      String outputCxFFilename = outputCxFDir.getAbsolutePath() + "/" +
//          inputCxF.getName();
//      calibrator.calibrate(order, outputCxFFilename);
//    }
  }

}

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 * @deprecated
 */
class InstrumentCalibratorFrame_jButton_calibrate_actionAdapter
    implements ActionListener {
  /**
   * @deprecated
   */
  private InstrumentCalibratorFrame adaptee;
  /**
   *
   * @param adaptee InstrumentCalibratorFrame
   * @deprecated
   */
  InstrumentCalibratorFrame_jButton_calibrate_actionAdapter(
      InstrumentCalibratorFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *
   * @param e ActionEvent
   * @deprecated
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_calibrate_actionPerformed(e);
  }
}

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 * @deprecated
 */
class InstrumentCalibratorFrame_jButton_output_actionAdapter
    implements ActionListener {
  /**
   * @deprecated
   */
  private InstrumentCalibratorFrame adaptee;
  /**
   *
   * @param adaptee InstrumentCalibratorFrame
   * @deprecated
   */
  InstrumentCalibratorFrame_jButton_output_actionAdapter(
      InstrumentCalibratorFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_output_actionPerformed(e);
  }
}

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 * @deprecated
 */
class InstrumentCalibratorFrame_jButton_coefs_actionAdapter
    implements ActionListener {
  /**
   * @deprecated
   */
  private InstrumentCalibratorFrame adaptee;
  /**
   *
   * @param adaptee InstrumentCalibratorFrame
   * @deprecated
   */
  InstrumentCalibratorFrame_jButton_coefs_actionAdapter(
      InstrumentCalibratorFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_coefs_actionPerformed(e);
  }
}

/**
 *
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 * @deprecated
 */
@SuppressWarnings(value = {
                  "deprecation"})
class InstrumentCalibratorFrame_jButton_input_actionAdapter
    implements ActionListener {
  /**
   * @deprecated
   */
  private InstrumentCalibratorFrame adaptee;
  /**
   *
   * @param adaptee InstrumentCalibratorFrame
   * @deprecated
   */
  InstrumentCalibratorFrame_jButton_input_actionAdapter(
      InstrumentCalibratorFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_input_actionPerformed(e);
  }
}
