package shu.cms.applet.util;

import java.io.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import shu.cms.colorformat.cxf.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 檢查測色後的結果的合理性.
 * 目前只能檢查數值是會有反轉
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class MeasurementFileCheckFrame
    extends JFrame {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JButton jButton_openFile = new JButton();
  JTextField jTextField_filename = new JTextField();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextPane jTextPane_message = new JTextPane();
  JFileChooser jFileChooser1;

  public MeasurementFileCheckFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    new Thread() {
      public void run() {
        jFileChooser1 = new JFileChooser();
      }
    }.start();

    getContentPane().setLayout(borderLayout1);
    this.setSize(800, 600);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setTitle("Measurement File Checker");
    jTextField_filename.setPreferredSize(new Dimension(500, 26));
    jTextField_filename.setToolTipText("");
    jTextField_filename.setEditable(false);
    jButton_openFile.addActionListener(new
                                       MeasurementFileCheckFrame_jButton_openFile_actionAdapter(this));
    jButton_checking.setText("檢查");
    jButton_checking.addActionListener(new
                                       MeasurementFileCheckFrame_jButton_checking_actionAdapter(this));
    jPanel1.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    this.getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);
    jPanel1.add(jButton_openFile);
    jPanel1.add(jTextField_filename);
    jPanel1.add(jButton_checking);
    this.getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jTextPane_message);
    jButton_openFile.setText("開檔");
  }

  public static void main(String[] args) {
    MeasurementFileCheckFrame frame = new MeasurementFileCheckFrame();
    frame.setVisible(true);
  }

  File checkFile;
  JButton jButton_checking = new JButton();
  FlowLayout flowLayout1 = new FlowLayout();

  public void jButton_openFile_actionPerformed(ActionEvent e) {
    jFileChooser1.setFileFilter(new FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory() ||
            f.getName().toLowerCase().indexOf(".cxf") != -1;
      }

      public String getDescription() {
        return "Color eXchange File";
      }
    });
    jFileChooser1.showOpenDialog(this);
    File file = jFileChooser1.getSelectedFile();
    if (file != null) {
      checkFile = file;
      this.jTextField_filename.setText(checkFile.getAbsolutePath());
    }
  }

  public void jButton_checking_actionPerformed(ActionEvent e) {
    String filename = checkFile.getAbsolutePath();
    String msg = i1Display2Checking(filename);
    this.jTextPane_message.setText(msg);
  }

  protected static String i1Display2Checking(String filename) {
    CXFOperator cxf = new CXFOperator(filename);
    List<CIELab> LabList = cxf.getCIELabList();
    List<RGB> rgbList = cxf.getRGBList();

    int size = LabList.size();
    double lastRGBValue = -1.;
    double lastL = -1.;
    StringBuilder msg = new StringBuilder();

    for (int x = 0; x < size; x++) {
      RGB rgb = rgbList.get(x);
      CIELab lab = LabList.get(x);
      double tmpValue = rgb.R + rgb.G + rgb.B;

      if (lastRGBValue != -1 && tmpValue > lastRGBValue) {
        if (lab.L < lastL) {
          RGB lastRGB = rgbList.get(x - 1);
          CIELab lastLab = LabList.get(x - 1);
          msg.append("[" + (x) + "] RGB[" + lastRGB + "] Lab[" +
                     lastLab +
                     "]\n");

          msg.append("[" + (x + 1) + "] RGB[" + rgb + "] Lab[" + lab +
                     "]\n\n");
        }
      }
      lastRGBValue = tmpValue;
      lastL = lab.L;

    }
    return msg.toString();
  }

}

class MeasurementFileCheckFrame_jButton_checking_actionAdapter
    implements ActionListener {
  private MeasurementFileCheckFrame adaptee;
  MeasurementFileCheckFrame_jButton_checking_actionAdapter(
      MeasurementFileCheckFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_checking_actionPerformed(e);
  }
}

class MeasurementFileCheckFrame_jButton_openFile_actionAdapter
    implements ActionListener {
  private MeasurementFileCheckFrame adaptee;
  MeasurementFileCheckFrame_jButton_openFile_actionAdapter(
      MeasurementFileCheckFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_openFile_actionPerformed(e);
  }
}
