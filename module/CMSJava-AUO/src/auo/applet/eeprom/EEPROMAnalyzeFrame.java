package auo.applet.eeprom;

import java.awt.*;
import javax.swing.*;
import java.awt.BorderLayout;
import com.borland.jbcl.layout.VerticalFlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import shu.ui.GUIUtils;
import javax.swing.UIManager.*;
import shu.util.log.Logger;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EEPROMAnalyzeFrame
    extends JFrame {
  protected JButton jButton1 = new JButton();
  protected JTextField jTextField1 = new JTextField();
  protected JButton jButton2 = new JButton();
  protected JTextField jTextField2 = new JTextField();
  protected JPanel jPanel3 = new JPanel();
  protected GridBagLayout gridBagLayout1 = new GridBagLayout();
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JProgressBar jProgressBar1 = new JProgressBar();
  protected JFileChooser jFileChooser1 = new JFileChooser();

  public EEPROMAnalyzeFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setSize(550, 220);
    getContentPane().setLayout(borderLayout1);
    jButton2.setText("Save tcon.ini");
    jButton2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jPanel3.setLayout(gridBagLayout1);
    jButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    this.setTitle("EEPROM Excel Analyzer");
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setText("\"Setting Map\" Sheet Name");
    jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel2.setText("Top Left");
    jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel3.setText("Bottom Right");
    jTextField_SettingMapName.setText("Setting Map");
    jTextField_TopLeft.setText("E17");
    jTextField_BottomRight.setText("L3184");
    jTextField1.setEditable(false);
    jTextField2.setEditable(false);
    jPanel1.setLayout(gridBagLayout2);
    jButton3.setText("jButton3");
    jTextField3.setText("jTextField3");
    jButton_LoadGolden.setText("Load Golden");
    jTextField4.setText("jTextField4");
    jLabel4.setText("Start Byte");
    jLabel5.setText("End Byte");
    jTextField5.setText("jTextField5");
    jTextField6.setText("jTextField6");
    jLabel6.setText("Start Byte");
    jLabel7.setText("End Byte");
    jTextField7.setText("jTextField7");
    jTextField8.setText("jTextField8");
    this.getContentPane().add(jProgressBar1, java.awt.BorderLayout.SOUTH);

    jPanel3.add(jTextField2, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    jPanel3.add(jButton1, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.HORIZONTAL,
                                                 new Insets(0, 5, 5, 5), 0, 0));
    jPanel3.add(jButton2, new GridBagConstraints(0, 5, 1, 2, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(5, 5, 5, 5), 0, 0));
    jPanel3.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(jTextField_SettingMapName,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                       , GridBagConstraints.CENTER,
                                       GridBagConstraints.HORIZONTAL,
                                       new Insets(5, 5, 5, 5), 0, 0));
    jPanel3.add(jLabel2, new GridBagConstraints(0, 1, 1, 2, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(jTextField_TopLeft, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    jPanel3.add(jLabel3, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(jTextField1, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    jPanel3.add(jTextField_BottomRight,
                new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
                                       , GridBagConstraints.CENTER,
                                       GridBagConstraints.HORIZONTAL,
                                       new Insets(5, 5, 5, 5), 0, 0));
    jTabbedPane1.add(jPanel3, "Output");
    jTabbedPane1.add(jPanel1, "Check");
    jPanel1.add(jButton_LoadGolden, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jLabel4, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jTextField5, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jTextField6, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jLabel5, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jTextField7, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jButton3, new GridBagConstraints(0, 2, 1, 2, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jLabel6, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jTextField4, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jTextField3, new GridBagConstraints(1, 2, 1, 2, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jLabel7, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jTextField8, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

    jButton1.setText("Load EEPROM Excel");
  }

  public static void main(String[] args) {
//    try {
//      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//        if ("Nimbus".equals(info.getName())) {
//          UIManager.setLookAndFeel(info.getClassName());
//          break;
//        }
//      }
//    }
//    catch (Exception e) {
//      // If Nimbus is not available, you can set the GUI to another look and feel.
//    }
    try {
      UIManager.setLookAndFeel(
          UIManager.getSystemLookAndFeelClassName());
    }
    catch (UnsupportedLookAndFeelException ex) {
    }
    catch (IllegalAccessException ex) {
    }
    catch (InstantiationException ex) {
    }
    catch (ClassNotFoundException ex) {
    }

    EEPROMAnalyzeFrame eepromanalyzeframe = new EEPROMAnalyzeFrame();
    GUIUtils.runAsApplication(eepromanalyzeframe, false);
  }

  private File excelFile;
  protected JLabel jLabel1 = new JLabel();
  protected JTextField jTextField_SettingMapName = new JTextField();
  protected JLabel jLabel2 = new JLabel();
  protected JTextField jTextField_TopLeft = new JTextField();
  protected JLabel jLabel3 = new JLabel();
  protected JTextField jTextField_BottomRight = new JTextField();
  protected JTabbedPane jTabbedPane1 = new JTabbedPane();
  protected JPanel jPanel1 = new JPanel();
  protected GridBagLayout gridBagLayout2 = new GridBagLayout();
  protected JButton jButton3 = new JButton();
  protected JTextField jTextField3 = new JTextField();
  protected JButton jButton_LoadGolden = new JButton();
  protected JTextField jTextField4 = new JTextField();
  protected JLabel jLabel4 = new JLabel();
  protected JLabel jLabel5 = new JLabel();
  protected JTextField jTextField5 = new JTextField();
  protected JTextField jTextField6 = new JTextField();
  protected JLabel jLabel6 = new JLabel();
  protected JLabel jLabel7 = new JLabel();
  protected JTextField jTextField7 = new JTextField();
  protected JTextField jTextField8 = new JTextField();
  public void jButton1_actionPerformed(ActionEvent e) {
    File userdir = new File(System.getProperty("user.dir"));
    jFileChooser1.setCurrentDirectory(userdir);
    if (JFileChooser.APPROVE_OPTION == jFileChooser1.showOpenDialog(this)) {
      excelFile = jFileChooser1.getSelectedFile();
      jTextField1.setText(excelFile.getAbsolutePath());
    }
  }

  public void jButton2_actionPerformed(ActionEvent e) {
    if (null == excelFile) {
      JOptionPane.showMessageDialog(this, "EEPROM Excel is not loaded yet.");
      return;
    }
    if (this.jTextField_SettingMapName.getText().length() == 0 ||
        this.jTextField_TopLeft.getText().length() == 0 ||
        this.jTextField_BottomRight.getText().length() == 0) {
      JOptionPane.showMessageDialog(this,
                                    "Please fill the require infomation for \"Setting Map\".");
      return;
    }

    if (JFileChooser.APPROVE_OPTION == jFileChooser1.showSaveDialog(this)) {
      File tconFile = jFileChooser1.getSelectedFile();
      jTextField2.setText(tconFile.getAbsolutePath());
      String settingMapName = jTextField_SettingMapName.getText();
      String topLeft = jTextField_TopLeft.getText();
      String bottomRight = jTextField_BottomRight.getText();
      try {
        EEPROMAnalyzer analyzer = new EEPROMAnalyzer(excelFile.getAbsolutePath(),
            settingMapName, topLeft, bottomRight);
        analyzer.writeToTextFile(tconFile.getAbsolutePath());
      }
      catch (Exception ex) {
        Logger.log.error(ex);
      }
      JOptionPane.showMessageDialog(this, "Ok!");
    }
  }
}
