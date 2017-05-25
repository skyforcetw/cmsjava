package auo.mura;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import com.borland.jbcl.layout.VerticalFlowLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.JRadioButton;
import java.awt.Dimension;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;
import javax.swing.JFileChooser;
import java.io.File;
import jxl.read.biff.*;
import java.io.*;
import shu.cms.ui.DitherCanvas;

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
public class DeMuraFrame
    extends JFrame {
  protected JPanel contentPane;
  protected JMenuBar jMenuBar1 = new JMenuBar();
  protected JMenu jMenuFile = new JMenu();
  protected JMenuItem jMenuFileExit = new JMenuItem();
  protected JMenu jMenuHelp = new JMenu();
  protected JMenuItem jMenuHelpAbout = new JMenuItem();
  protected JLabel statusBar = new JLabel();
  protected JPanel jPanel1 = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JButton jButton_GrayLevelOk = new JButton();
  protected JButton jButton_GrayLevelOff = new JButton();
  protected JSpinner jSpinner_GrayLevel = new JSpinner();
  protected SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(100,
      0, 100, 5);
  protected JPanel jPanel2 = new JPanel();
  protected JPanel jPanel3 = new JPanel();
  protected JButton jButton_UniOff = new JButton();
  protected JButton jButton_UniOn = new JButton();
  protected JPanel jPanel4 = new JPanel();
  protected JPanel jPanel5 = new JPanel();
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JTextField jTextField_BitmapFile = new JTextField();
  protected JButton jButton_BitmapFileLoad = new JButton();
  protected JButton jButton_BitmapFileOff = new JButton();
  protected JPanel jPanel6 = new JPanel();
  protected BorderLayout borderLayout2 = new BorderLayout();
  protected JTextField jTextField_CorrectionData = new JTextField();
  protected JButton jButton_CorrectionDataLoad = new JButton();
  protected JPanel jPanel7 = new JPanel();
  protected JRadioButton jRadioButton_2ndMonitor = new JRadioButton();
  protected JRadioButton jRadioButton_1stMonitor = new JRadioButton();
  protected VerticalFlowLayout verticalFlowLayout2 = new VerticalFlowLayout();
  protected Border border2 = new TitledBorder(BorderFactory.createEtchedBorder(
      EtchedBorder.
      RAISED, Color.white, new Color(158, 158, 158)), "Signal Level (%)");
  protected Border border4 = new TitledBorder(BorderFactory.createEtchedBorder(
      EtchedBorder.
      RAISED, Color.white, new Color(158, 158, 158)), "Uniformity");
  protected Border border6 = new TitledBorder(BorderFactory.createEmptyBorder(),
                                              "Display Info.");
  protected Border border8 = new TitledBorder(BorderFactory.createEmptyBorder(),
                                              "Bitmap File");
  protected Border border10 = new TitledBorder(BorderFactory.createEmptyBorder(),
                                               "Correction Data");
  protected ButtonGroup buttonGroup1 = new ButtonGroup();
  protected JPanel jPanel8 = new JPanel();
  protected JPanel jPanel9 = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout3 = new VerticalFlowLayout();
  protected JCheckBox jCheckBox_B = new JCheckBox();
  protected JCheckBox jCheckBox_G = new JCheckBox();
  protected JCheckBox jCheckBox_R = new JCheckBox();
  protected JProgressBar jProgressBar1 = new JProgressBar();
  protected JFileChooser jFileChooser1 = new JFileChooser();
  public DeMuraFrame() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
//      this.jSpinner1.setEditor(new JSpinner.NumberEditor(jSpinner1, ""));

    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    border10 = new TitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.
        RAISED, Color.white, new Color(158, 158, 158)), "Correction Data");
    border8 = new TitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.
        RAISED, Color.white, new Color(158, 158, 158)), "Bitmap File");
    border6 = new TitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.
        RAISED, Color.white, new Color(158, 158, 158)), "Display Info.");
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(verticalFlowLayout1);
    setSize(new Dimension(480, 380));
    setTitle("De Mura");
    statusBar.setText(" ");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        jMenuFileExit_actionPerformed(actionEvent);
      }
    });

    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        jMenuHelpAbout_actionPerformed(actionEvent);
      }
    });
    jButton_GrayLevelOk.setText("Ok");
    jButton_GrayLevelOk.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_GrayLevelOk_actionPerformed(e);
      }
    });
    jButton_GrayLevelOff.setText("Off");
    jSpinner_GrayLevel.setToolTipText("");
    jSpinner_GrayLevel.setModel(spinnerNumberModel);
    jButton_UniOff.setEnabled(false);
    jButton_UniOff.setText("Off");
    jButton_UniOn.setText("On");
    jPanel4.setLayout(borderLayout1);
    jPanel5.setLayout(borderLayout2);
    jButton_BitmapFileLoad.setText("Load");
    jButton_BitmapFileOff.setText("Off");
    jButton_CorrectionDataLoad.setText("Load");
    jButton_CorrectionDataLoad.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_CorrectionDataLoad_actionPerformed(e);
      }
    });
    jRadioButton_2ndMonitor.setSelected(false);
    jRadioButton_2ndMonitor.setText("2nd Monitor");
    jRadioButton_1stMonitor.setSelected(true);
    jRadioButton_1stMonitor.setText("1st Monitor");
    jPanel7.setLayout(verticalFlowLayout2);
    verticalFlowLayout2.setHgap(0);
    verticalFlowLayout2.setVgap(0);
//    jPanel3.setBorder(null);
//    jPanel1.setBorder(null);
//    jPanel7.setBorder(null);
//    jPanel5.setBorder(null);
//    jPanel4.setBorder(null);
    jTextField_BitmapFile.setMaximumSize(new Dimension(72, 22));
    jTextField_BitmapFile.setPreferredSize(new Dimension(72, 22));
    jPanel1.setBorder(border2);
    jPanel1.setLayout(verticalFlowLayout3);
    jPanel3.setBorder(border4);
    jPanel7.setBorder(border6);
    jPanel4.setBorder(border8);
    jPanel5.setBorder(border10);
    jTextField_CorrectionData.setText(
        "correctiondata 121x271 No2 20130227--0010(1)_data(final).csv");
    jCheckBox_B.setSelected(true);
    jCheckBox_B.setText("B");
    jCheckBox_G.setSelected(true);
    jCheckBox_G.setText("G");
    jCheckBox_R.setSelected(true);
    jCheckBox_R.setText("R");
    jMenuBar1.add(jMenuFile);
    jMenuFile.add(jMenuFileExit);
    jMenuBar1.add(jMenuHelp);
    jMenuHelp.add(jMenuHelpAbout);
    setJMenuBar(jMenuBar1);
    contentPane.add(jPanel2);
    contentPane.add(jPanel4);
    contentPane.add(jPanel5);
    jPanel3.add(jButton_UniOn);
    jPanel3.add(jButton_UniOff);
    jPanel7.add(jRadioButton_1stMonitor);
    jPanel7.add(jRadioButton_2ndMonitor);
    jPanel2.add(jPanel1);
    jPanel2.add(jPanel3);
    jPanel2.add(jPanel7);
    jPanel1.add(jPanel8);
    jPanel1.add(jPanel9);
//    jPanel9.add(jCheckBox3);
    jPanel9.add(jCheckBox_R);
    jPanel9.add(jCheckBox_G);
    jPanel9.add(jCheckBox_B);
    jPanel8.add(jSpinner_GrayLevel);
    jPanel8.add(jButton_GrayLevelOk);
    jPanel8.add(jButton_GrayLevelOff);
    contentPane.add(statusBar, null);
    jPanel4.add(jTextField_BitmapFile, java.awt.BorderLayout.CENTER);
    jPanel6.add(jButton_BitmapFileLoad);
    jPanel6.add(jButton_BitmapFileOff);
    jPanel4.add(jPanel6, java.awt.BorderLayout.EAST);
    jPanel5.add(jTextField_CorrectionData, java.awt.BorderLayout.CENTER);
    jPanel5.add(jButton_CorrectionDataLoad, java.awt.BorderLayout.EAST);
    contentPane.add(jProgressBar1);
    buttonGroup1.add(jRadioButton_1stMonitor);
    buttonGroup1.add(jRadioButton_2ndMonitor);
  }

  /**
   * File | Exit action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
    System.exit(0);
  }

  /**
   * Help | About action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
    DeMuraFrame_AboutBox dlg = new DeMuraFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                    (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }

  protected CorrectionData correctiondata;
  protected MuraCompensationProducer muraCompensation;
  protected DitherCanvas ditherCanvas;
  public void jButton_GrayLevelOk_actionPerformed(ActionEvent e) {
    String text = jTextField_CorrectionData.getText();
    if (null != correctionDataFile && null != text && text.length() != 0) {
      String path = correctionDataFile.getPath();
      try {
        correctiondata = new CorrectionData(path);
      }
      catch (BiffException ex) {
        ex.printStackTrace();
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }

      muraCompensation = new
          MuraCompensationProducer(correctiondata);

    }
  }

  protected File correctionDataFile;
  public void jButton_CorrectionDataLoad_actionPerformed(ActionEvent e) {
    jFileChooser1.showOpenDialog(this);
    correctionDataFile = this.jFileChooser1.getSelectedFile();
    if (null != correctionDataFile) {
      String name = correctionDataFile.getName();
      jTextField_CorrectionData.setText(name);
    }
    else {
      jTextField_CorrectionData.setText("");
    }
  }
}
