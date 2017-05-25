package shu.cms.applet.util;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import shu.cms.colorformat.trans.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �NICCProfile�̭���DevD tag���X��,�åB��s��CxF��
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class RGBICCProfile2CxFFrame
    extends JFrame {
  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JFileChooser jFileChooser1;

  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenuItem jMenuItem_opendir = new JMenuItem();

  RGBICCProfile2CxF task;
  Timer timer;
  ProgressMonitor progressMonitor;
  final static int ONE_PIECE = 5;

  public RGBICCProfile2CxFFrame() {
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static void main(String[] args) {
    RGBICCProfile2CxFFrame frame = new RGBICCProfile2CxFFrame();
    frame.setVisible(true);
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    new Thread() {
      public void run() {
        jFileChooser1 = new JFileChooser();
      }
    }.start();

    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    setSize(new Dimension(400, 300));
    setTitle("ICCProfile��CxF");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new
                                    RGBICCProfile2CxFFrame_jMenuFileExit_ActionAdapter(this));
    jMenuItem_opendir.addActionListener(new
                                        RGBICCProfile2CxFFrame_jMenuItem_opendir_actionAdapter(this));
    jMenuItem_opendir.setText("Open Directory");
    jMenuBar1.add(jMenuFile);
    jMenuFile.add(jMenuItem_opendir);
    jMenuFile.add(jMenuFileExit);
    setJMenuBar(jMenuBar1);
  }

  /**
   * File | Exit action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
    System.exit(0);
  }

  public void jMenuItem_opendir_actionPerformed(ActionEvent e) {
    jFileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    jFileChooser1.setDialogTitle("�г]�wICCProfile�ɮשҦb�ؿ�");
    jFileChooser1.showOpenDialog(this);
    final File dir = jFileChooser1.getSelectedFile();

    if (dir == null) {
      return;
    }

    task = new RGBICCProfile2CxF();

    try {

      //timer�|�w���ˬdtask�u�@���p
      timer = new Timer(ONE_PIECE, new TimerListener());
      //progressMonitor�|���SpectraWinASCIIFile2CxF���u�@�i��
      progressMonitor = new ProgressMonitor(this,
                                            "���ɤ�",
                                            "", 0, task.getLengthOfTask());
      progressMonitor.setProgress(0);
      progressMonitor.setMillisToDecideToPopup(0);
      timer.start();

      //�ѥt�~�@�Ӱ�����i�����ɤu�@,�קK��C�D�{���į�
      new Thread() {
        public void run() {
          try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//            task.transforming(dir.getAbsolutePath(), file.getAbsolutePath());
            task.transform(dir.getPath());
            setCursor(null);
          }
          catch (Exception ex) {
            Logger.log.error("", ex);
          }
        }
      }.start();

    }
    catch (Exception ex) {
      Logger.log.error("", ex);
    }
  }

  class TimerListener
      implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      int cur = task.getCurrent();
      progressMonitor.setProgress(cur);
      if (task.isDone()) {
        progressMonitor.close();
        timer.stop();
      }
    }
  }

}

class RGBICCProfile2CxFFrame_jMenuFileExit_ActionAdapter
    implements ActionListener {
  RGBICCProfile2CxFFrame adaptee;

  RGBICCProfile2CxFFrame_jMenuFileExit_ActionAdapter(RGBICCProfile2CxFFrame
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.jMenuFileExit_actionPerformed(actionEvent);
  }
}

class RGBICCProfile2CxFFrame_jMenuItem_opendir_actionAdapter
    implements ActionListener {
  private RGBICCProfile2CxFFrame adaptee;
  RGBICCProfile2CxFFrame_jMenuItem_opendir_actionAdapter(RGBICCProfile2CxFFrame
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_opendir_actionPerformed(e);
  }
}
