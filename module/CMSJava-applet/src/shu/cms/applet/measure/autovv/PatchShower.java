package shu.cms.applet.measure.autovv;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import shu.cms.*;
import shu.cms.applet.measure.auto.*;
import shu.cms.colorformat.legend.*;
import shu.math.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * PatchShower視窗類別.<br>
 * 可以進行多重取樣的功能(2x或3x取樣),經多重取樣的資料,可由 進行運算回復.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class PatchShower
    extends JFrame implements CallBack {

  protected FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(
      "GMB file", "txt");
  protected File patchFile;
  protected File monitorFile;
  protected File dataDir;
  protected FileMonitor fileMonitor;
  protected FileMonitor.MonitorType monitorType = FileMonitor.MonitorType.
      Normal;
  protected GretagMacbethAsciiFile.DataSet dataSet;
  protected int interval = 10;
  protected int fileSizeThreshold = -1;
  protected int samplingTimes = 1;
  protected double averageTime = 0;

  BorderLayout borderLayout1 = new BorderLayout();
  JPopupMenu jPopupMenu1 = new JPopupMenu();
  JMenuItem jMenuItem_800x600 = new JMenuItem();
  JMenuItem jMenuItem_1024x768 = new JMenuItem();
  JMenuItem jMenuItem_1280x1024 = new JMenuItem();
  JMenuItem jMenuItem_loading = new JMenuItem();
  JFileChooser jFileChooser1;
  JMenuItem jMenuItem_monitor = new JMenuItem();
  JMenuItem jMenuItem_start = new JMenuItem();

  JMenu jMenu_size = new JMenu();
  JMenu jMenu_setup = new JMenu();

  JMenuItem jMenuItem_interval = new JMenuItem();
  JMenuItem jMenuItem_threshold = new JMenuItem();

  JMenu jMenu_sample = new JMenu();
  JRadioButtonMenuItem jRBMenuItem_1x = new JRadioButtonMenuItem("1x");
  JRadioButtonMenuItem jRBMenuItem_2x = new JRadioButtonMenuItem("2x");
  JRadioButtonMenuItem jRBMenuItem_3x = new JRadioButtonMenuItem("3x");
  ButtonGroup buttonGroup1 = new ButtonGroup();
  ButtonGroup buttonGroup2 = new ButtonGroup();
  JMenuItem jMenuItem_stop = new JMenuItem();
  JMenuItem jMenuItem_readme = new JMenuItem();
  JRadioButtonMenuItem jRadioButton_copydata = new JRadioButtonMenuItem();
  JRadioButtonMenuItem jRadioButton_twinmode = new JRadioButtonMenuItem();
  JMenu jMenu_twinmode = new JMenu();
  JRadioButtonMenuItem jRadioButton_training = new JRadioButtonMenuItem();
  JRadioButtonMenuItem jRadioButton_measuring = new JRadioButtonMenuItem();
  JRadioButtonMenuItem jRadioButton_doubleCheck = new JRadioButtonMenuItem();
  JCheckBox jCheckBox_SkipFirst = new JCheckBox();
  public PatchShower() {
    try {
      jbInit();
      makeDataDir();
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
    this.getContentPane().setBackground(Color.ORANGE);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setTitle("色塊產生器 (VastView version)");
    this.addMouseListener(new PatchShower_this_mouseAdapter(this));
    this.setSize(1024, 768);
    jMenuItem_800x600.setText("800*600");
    jMenuItem_800x600.addActionListener(new
                                        PatchShower_jMenuItem_800x600_actionAdapter(this));
    jMenuItem_1024x768.setText("1024*768");
    jMenuItem_1024x768.addActionListener(new
                                         PatchShower_jMenuItem_1024x768_actionAdapter(this));
    jMenuItem_1280x1024.setText("1280*1024");
    jMenuItem_1280x1024.addActionListener(new
                                          PatchShower_jMenuItem_1280x1024_actionAdapter(this));
    jMenuItem_loading.setText("色塊檔");
    jMenuItem_loading.addActionListener(new
                                        PatchShower_jMenuItem_loading_actionAdapter(this));
    jMenuItem_monitor.setText("監控檔案");
    jMenuItem_monitor.addActionListener(new
                                        PatchShower_jMenuItem_monitor_actionAdapter(this));
    jMenuItem_start.setEnabled(false);
    jMenuItem_start.setText("開始");
    jMenuItem_start.addActionListener(new
                                      PatchShower_jMenuItem_start_actionAdapter(this));
    jMenu_size.setText("視窗大小");
    jMenuItem_interval.setText("interval");
    jMenuItem_interval.addActionListener(new
                                         PatchShower_jMenuItem_interval_actionAdapter(this));
    jMenuItem_threshold.setText("file size threshold");
    jMenuItem_threshold.addActionListener(new
                                          PatchShower_jMenuItem_threshold_actionAdapter(this));
    jMenu_sample.setText("多重取樣");
    jRBMenuItem_1x.setSelected(true);
    jRBMenuItem_1x.setMnemonic(1);
    jRBMenuItem_2x.setMnemonic(2);
    jRBMenuItem_3x.setMnemonic(3);
    jMenuItem_stop.setEnabled(false);
    jMenuItem_stop.setText("停止");
    jMenuItem_stop.addActionListener(new
                                     PatchShower_jMenuItem_stop_actionAdapter(this));
    jMenuItem_readme.setText("使用說明");
    jMenuItem_readme.addActionListener(new
                                       PatchShower_jMenuItem_readme_actionAdapter(this));
    jRadioButton_copydata.setText("copy data");
    jRadioButton_copydata.setSelected(false);
    jRadioButton_twinmode.setText("twin mode");
    jRadioButton_twinmode.addActionListener(new
                                            PatchShower_jRadioButton_twinmode_actionAdapter(this));
    jRadioButton_twinmode.setEnabled(false);
    jRadioButton_twinmode.setSelected(false);
    jMenu_twinmode.setEnabled(false);
    jMenu_twinmode.setText("twin mode");
    jRadioButton_training.setText("training");
    jRadioButton_training.addActionListener(new
                                            PatchShower_jRadioButton_training_actionAdapter(this));
    jRadioButton_measuring.setText("measuring");
    jRadioButton_measuring.addActionListener(new
                                             PatchShower_jRadioButton_measuring_actionAdapter(this));
    jRadioButton_doubleCheck.setEnabled(false);
    jRadioButton_doubleCheck.setText("double-check");
    jCheckBox_SkipFirst.setText("jCheckBox1");
    buttonGroup1.add(jRBMenuItem_1x);
    buttonGroup1.add(jRBMenuItem_2x);
    buttonGroup1.add(jRBMenuItem_3x);

    jMenu_sample.add(jRBMenuItem_1x);
    jMenu_sample.add(jRBMenuItem_2x);
    jMenu_sample.add(jRBMenuItem_3x);

    jMenu_size.add(jMenuItem_800x600);
    jMenu_size.add(jMenuItem_1024x768);
    jMenu_size.add(jMenuItem_1280x1024);
    jPopupMenu1.add(jMenuItem_readme);

    jCheckBox_SkipFirst.setText("CS-1000 Mode");

    jPopupMenu1.add(jMenu_size);
    jPopupMenu1.add(jMenu_setup);
    jPopupMenu1.add(jMenu_sample);
    jPopupMenu1.add(jMenu_twinmode);
    jMenu_setup.add(jMenuItem_interval);
    jMenu_setup.add(jMenuItem_threshold);
    jMenu_setup.add(jRadioButton_copydata);
    jMenu_setup.add(jRadioButton_twinmode);
    jMenu_setup.add(jRadioButton_doubleCheck);
    jMenu_setup.add(jCheckBox_SkipFirst);
    jMenu_setup.setText("設定");
    jPopupMenu1.addSeparator();
    jPopupMenu1.add(jMenuItem_loading);
    jPopupMenu1.add(jMenuItem_monitor);
    jPopupMenu1.addSeparator();
    jPopupMenu1.add(jMenuItem_start);
    jPopupMenu1.add(jMenuItem_stop);
    jMenu_twinmode.add(jRadioButton_training);
    jMenu_twinmode.add(jRadioButton_measuring);
    buttonGroup2.add(jRadioButton_training);
    buttonGroup2.add(jRadioButton_measuring);

    this.setVisible(true);
  }

  public static void main(String[] args) {
    PatchShower patchshower = new PatchShower();
//    System.exit(0);
  }

  public void this_mouseReleased(MouseEvent e) {
    checkPopup(e);
  }

  private void checkPopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
      jPopupMenu1.show(this, e.getX(), e.getY());
    }
  }

  public void jMenuItem_800x600_actionPerformed(ActionEvent e) {
    this.setSize(800, 600);

  }

  public void jMenuItem_1024x768_actionPerformed(ActionEvent e) {
    this.setSize(1024, 768);
  }

  public void jMenuItem_1280x1024_actionPerformed(ActionEvent e) {
    this.setSize(1280, 1024);
  }

  public void jMenuItem_loading_actionPerformed(ActionEvent e) {
    if (patchFile != null) {
      //如果已經選過色塊,就跳到色塊所在的目錄
      jFileChooser1.setCurrentDirectory(patchFile.getParentFile());
    }
    else {
      jFileChooser1.setCurrentDirectory(new File(CMSDir.Reference.Monitor));
    }

    jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
    jFileChooser1.addChoosableFileFilter(fileFilter);
    this.jFileChooser1.showOpenDialog(this);
    patchFile = jFileChooser1.getSelectedFile();
    if (patchFile != null) {
      GretagMacbethAsciiParser parser = new GretagMacbethAsciiParser(patchFile.
          getAbsolutePath());
      GretagMacbethAsciiFile file = parser.getGretagMacbethAsciiFile();
      parser.close();
      dataSet = file.getDataSet();
    }
    judgeOK();
  }

  public void jMenuItem_monitor_actionPerformed(ActionEvent e) {
    jFileChooser1.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    jFileChooser1.removeChoosableFileFilter(fileFilter);
    this.jFileChooser1.showDialog(this, "設定");
    monitorFile = jFileChooser1.getSelectedFile();
    if (monitorFile != null) {
      this.jMenuItem_monitor.setEnabled(false);
    }
    judgeOK();
  }

  protected void judgeOK() {
    if (this.monitorType == FileMonitor.MonitorType.Training && monitorFile != null) {
      toggleControl(true);
    }
    else if (patchFile != null && monitorFile != null) {
      toggleControl(true);
    }

  }

  public void jMenuItem_start_actionPerformed(ActionEvent e) {
    //設定取樣次數
    samplingTimes = buttonGroup1.getSelection().getMnemonic();

    //啟動檔案狀態監控執行緒
    fileMonitor = new FileMonitor(monitorFile, this, this.monitorType);
    FileMonitor.INTERVAL = this.interval;
    FileMonitor.FILE_SIZE_THRESHOLD = this.fileSizeThreshold;
    fileMonitor.start();

    if (this.monitorType == FileMonitor.MonitorType.Training) {

    }
    else {
      //初始第一個色塊
      int count = fileMonitor.count;
      if (count > dataSet.size()) {
        return;
      }
      String text = getSampleName(count);
      this.setTitle("色塊產生器 - " + text + " (總:" + dataSet.size() + ")"
                    +
                    (samplingTimes != 1 ? " (多重取樣:" + samplingTimes + "x)" : ""));
      int[] rgb = getRGB(count);
      this.getContentPane().setBackground(new Color(rgb[0], rgb[1], rgb[2]));

    }

    toggleControl(false);
  }

  /**
   * 設定是否還可以做操作
   * @param control boolean
   */
  protected void toggleControl(boolean control) {
    this.jMenu_setup.setEnabled(control);
    this.jMenu_sample.setEnabled(control);
    this.jMenuItem_start.setEnabled(control);

    //取消暫停的功能,因為懶的除錯
    this.jMenuItem_stop.setEnabled(!control);

    this.jMenuItem_loading.setEnabled(control);
    this.jRadioButton_training.setEnabled(control);

  }

  protected boolean makeDataDir() {
    if (dataDir == null) {
      boolean check = true;
      File curDir = new File(System.getProperty("user.dir"));
      File measureDir = new File(curDir, "Measurement Files");
      if (!measureDir.exists()) {
        check = check & measureDir.mkdir();
      }

      dataDir = new File(curDir, "Measurement Files/AutoMeasurement");
      if (!dataDir.exists()) {
        check = check & dataDir.mkdir();
      }
      return check;
    }
    return false;
  }

  protected int[] getRGB(int index) {
    double[] dRGB = dataSet.getTestChartData(index).RGB;
    return Maths.floor(dRGB);
  }

  protected String getSampleName(int index) {
    GretagMacbethAsciiFile.TestChartData data = dataSet.getTestChartData(index);
    return data.sampleName;
  }

  protected boolean firstSkip = false;

  public void callback() throws IOException {
    System.out.println(fileMonitor.count);
    if (jCheckBox_SkipFirst.isSelected() && !firstSkip) {
      firstSkip = true;
      fileMonitor.count--;
      return;
    }

    int recSampleIndex = fileMonitor.count;
    recSampleIndex = (int) ( (double) recSampleIndex / samplingTimes);

    int showSampleIndex = recSampleIndex +
        ( ( (fileMonitor.count + 1) % samplingTimes) == 0 ? 1 : 0);

    //==========================================================================
    //此為記錄區段
    //==========================================================================
    if (recSampleIndex >= dataSet.size()) {
      return;
    }
//    if (jCheckBox_SkipFirst.isSelected() && showSampleIndex == 2 && !secondSkip) {
//      secondSkip = true;
//      return;
//    }

    String recText = getSampleName(recSampleIndex);

    if (samplingTimes != 1) {
      int suffix = (fileMonitor.count % samplingTimes) + 1;
      File newFile = new File(dataDir, recText + "-" + suffix + ".txt");
      Utils.copyFileByNIO(monitorFile, newFile);
    }
    else if (jRadioButton_copydata.isSelected()) {
      //複製測量資料
      File newFile = new File(dataDir, recText + ".txt");
      Utils.copyFileByNIO(monitorFile, newFile);
    }
    //==========================================================================

    //==========================================================================
    //此為更新色塊區段
    //==========================================================================
    if (showSampleIndex >= dataSet.size()) {
      return;
    }

    String showText = getSampleName(showSampleIndex);
    this.setTitle("色塊產生器 - " + showText +
                  (fileMonitor.failed != 0 ? " (錯:" + fileMonitor.failed + ")" :
                   "") +
                  " (總:" + dataSet.size() + ")"
                  + (samplingTimes != 1 ? " (多重取樣:" + samplingTimes + "x)" : ""));

    int[] rgb = getRGB(showSampleIndex);
    this.getContentPane().setBackground(new Color(rgb[0], rgb[1], rgb[2]));
    //==========================================================================
  }

  public void jMenuItem_interval_actionPerformed(ActionEvent e) {
    String str = JOptionPane.showInputDialog("interval (ms)\n(輪詢檔案狀況的時間間隔)",
                                             interval);
    if (str != null) {
      interval = Integer.parseInt(str);
    }
  }

  public void jMenuItem_threshold_actionPerformed(ActionEvent e) {
    String str = JOptionPane.showInputDialog(
        "fileSizeThreshold (byte)\n(-1代表不去考慮檔案大小)",
        fileSizeThreshold);
    if (str != null) {
      fileSizeThreshold = Integer.parseInt(str);
    }
  }

  /**
   * Releases all of the native screen resources used by this
   * <code>Window</code>, its subcomponents, and all of its owned children.
   */
  public synchronized void dispose() {
    if (fileMonitor != null) {
//      System.out.println("!=/null");
//      fileMonitor.stop();
      fileMonitor.stop = true;
    }
    super.dispose();
  }

  public void jMenuItem_stop_actionPerformed(ActionEvent e) {

    toggleControl(true);
    if (this.monitorType == FileMonitor.MonitorType.Training) {
      fileMonitor.trainingStop();
      averageTime = fileMonitor.averageTime;
      this.setTitle(this.getTitle() + " ave:" + averageTime);
    }
    else {
      fileMonitor.pause();
//重置
      fileMonitor.reset();
    }
    firstSkip = false;
//    secondSkip = false;
  }

  public void jMenuItem_readme_actionPerformed(ActionEvent e) {
    ReadmeFrame readmeframe = new ReadmeFrame();
    readmeframe.setLocationRelativeTo(this);
    readmeframe.setVisible(true);
    readmeframe = null;
  }

  public void jRadioButton_twinmode_actionPerformed(ActionEvent e) {
    this.jMenu_twinmode.setEnabled(jRadioButton_twinmode.isSelected());

    if (!jRadioButton_twinmode.isSelected()) {
      this.monitorType = FileMonitor.MonitorType.Normal;
      this.jMenuItem_loading.setEnabled(true);
    }
  }

  public void jRadioButton_training_actionPerformed(ActionEvent e) {
    this.monitorType = FileMonitor.MonitorType.Training;
    this.jMenuItem_loading.setEnabled(!this.jRadioButton_training.isSelected());

  }

  public void jRadioButton_measuring_actionPerformed(ActionEvent e) {
    this.monitorType = FileMonitor.MonitorType.Measuring;
    this.jMenuItem_loading.setEnabled(!this.jRadioButton_training.isSelected());
  }
}

class PatchShower_jRadioButton_measuring_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jRadioButton_measuring_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jRadioButton_measuring_actionPerformed(e);
  }
}

class PatchShower_jRadioButton_training_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jRadioButton_training_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jRadioButton_training_actionPerformed(e);
  }
}

class PatchShower_jRadioButton_twinmode_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jRadioButton_twinmode_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jRadioButton_twinmode_actionPerformed(e);
  }
}

class PatchShower_jMenuItem_stop_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jMenuItem_stop_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_stop_actionPerformed(e);
  }
}

class PatchShower_jMenuItem_threshold_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jMenuItem_threshold_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_threshold_actionPerformed(e);
  }
}

class PatchShower_jMenuItem_interval_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jMenuItem_interval_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_interval_actionPerformed(e);
  }
}

class PatchShower_jMenuItem_monitor_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jMenuItem_monitor_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_monitor_actionPerformed(e);
  }
}

class PatchShower_jMenuItem_loading_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jMenuItem_loading_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_loading_actionPerformed(e);
  }
}

class PatchShower_jMenuItem_readme_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jMenuItem_readme_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_readme_actionPerformed(e);
  }
}

class PatchShower_jMenuItem_1280x1024_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jMenuItem_1280x1024_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_1280x1024_actionPerformed(e);
  }
}

class PatchShower_jMenuItem_1024x768_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jMenuItem_1024x768_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_1024x768_actionPerformed(e);
  }
}

class PatchShower_this_mouseAdapter
    extends MouseAdapter {
  private PatchShower adaptee;
  PatchShower_this_mouseAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseReleased(MouseEvent e) {
    adaptee.this_mouseReleased(e);
  }
}

class PatchShower_jMenuItem_800x600_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jMenuItem_800x600_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_800x600_actionPerformed(e);
  }
}

class PatchShower_jMenuItem_start_actionAdapter
    implements ActionListener {
  private PatchShower adaptee;
  PatchShower_jMenuItem_start_actionAdapter(PatchShower adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem_start_actionPerformed(e);
  }

}
