package shu.cms.applet.legend;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import shu.cms.colorformat.*;
import shu.cms.colorformat.trans.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * SpectraWin�ɮ����ɵ{��
 * �i�NSpectraWin ASCII file�ন.CXF
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class SpectraWin2CxFFrame
    extends JFrame {
  BorderLayout borderLayout1 = new BorderLayout();
  JFileChooser jFileChooser1;

  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenuItem jMenuItem_opendir = new JMenuItem();

  ProgressMonitor progressMonitor;
  TransformTask task;
  Timer timer;
  final static int ONE_PIECE = 5;
  JMenu jMenu_sample = new JMenu();

  JRadioButtonMenuItem jRBMenuItem_1x = new JRadioButtonMenuItem("1x");
  JRadioButtonMenuItem jRBMenuItem_2x = new JRadioButtonMenuItem("2x");
  JRadioButtonMenuItem jRBMenuItem_3x = new JRadioButtonMenuItem("3x");
  ButtonGroup buttonGroup1 = new ButtonGroup();

  public SpectraWin2CxFFrame() {
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
    setSize(new Dimension(400, 300));
    setTitle("SpectraWin To CxF ���ɵ{��");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new
                                    SpectraWin2CxFFrame_jMenuFileExit_actionAdapter(this));
    jMenuItem_opendir.setText("Open Directory");
    jMenuItem_opendir.addActionListener(new
                                        SpectraWin2CxFFrame_jMenuItem_opendir_actionAdapter(this));
    jMenu_sample.setText("�h������");
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenu_sample);
    jMenuFile.add(jMenuItem_opendir);
    jMenuFile.add(jMenuFileExit);

    jRBMenuItem_1x.setSelected(true);
    jRBMenuItem_1x.setMnemonic(1);
    jRBMenuItem_2x.setMnemonic(2);
    jRBMenuItem_3x.setMnemonic(3);

    buttonGroup1.add(jRBMenuItem_1x);
    buttonGroup1.add(jRBMenuItem_2x);
    buttonGroup1.add(jRBMenuItem_3x);

    jMenu_sample.add(jRBMenuItem_1x);
    jMenu_sample.add(jRBMenuItem_2x);
    jMenu_sample.add(jRBMenuItem_3x);

    setJMenuBar(jMenuBar1);
  }

  public static void main(String[] args) {
    SpectraWin2CxFFrame spectrawin2cxfframe = new SpectraWin2CxFFrame();
    spectrawin2cxfframe.setVisible(true);
  }

  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  /**
   * �]�w���ɥؿ�
   * @param e ActionEvent
   */
  public void jMenuItem_opendir_actionPerformed(ActionEvent e) {
    jFileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    jFileChooser1.setDialogTitle("�г]�wSpectracWin�ɮשҦb�ؿ�");
    jFileChooser1.showOpenDialog(this);
    final File dir = jFileChooser1.getSelectedFile();

    if (dir == null) {
      return;
    }

    //����W�@�h
    jFileChooser1.setCurrentDirectory(dir.getParentFile());

    jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
    jFileChooser1.setDialogTitle("�г]�w��X��CxF�ɦW");
    File defaultFile = new File(dir.getName() + ".cxf");
    jFileChooser1.setSelectedFile(defaultFile);
    jFileChooser1.showSaveDialog(this);
    final File file = jFileChooser1.getSelectedFile();

    if (file == null) {
      return;
    }

    int samplingTimes = buttonGroup1.getSelection().getMnemonic();
    if (samplingTimes != 1) {
      //�h�����˻ݭn�z�LMultiSampleReconstruction�٭�
      task = new MultiSampleReconstruction(samplingTimes);
    }
    else {
      task = new SpectraWinAsciiFile2CxF();
    }

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
            task.transforming(dir.getAbsolutePath(), file.getAbsolutePath());
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

class SpectraWin2CxFFrame_jMenuItem_opendir_actionAdapter
    implements ActionListener {
  private SpectraWin2CxFFrame adaptee;
  SpectraWin2CxFFrame_jMenuItem_opendir_actionAdapter(SpectraWin2CxFFrame
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_opendir_actionPerformed(e);
  }
}

class SpectraWin2CxFFrame_jMenuFileExit_actionAdapter
    implements ActionListener {
  private SpectraWin2CxFFrame adaptee;
  SpectraWin2CxFFrame_jMenuFileExit_actionAdapter(SpectraWin2CxFFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuFileExit_actionPerformed(e);
  }
}
