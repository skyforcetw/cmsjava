package shu.cms.applet.measure.auto;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
  File patchFile;
  File monitorFile;
  File dataDir;
  FileMonitor fileMonitor;
  boolean started = false;
  GretagMacbethAsciiFile.DataSet dataSet;
  int interval = 10;
  int fileSizeThreshold = -1;
  int samplingTimes = 1;

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
  JMenuItem jMenuItem_stop = new JMenuItem();
  JMenuItem jMenuItem_readme = new JMenuItem();
  JRadioButtonMenuItem jRadioButton_copydata = new JRadioButtonMenuItem();
//  JMenuItem jMenuItem_copydata = new JMenuItem();

  public PatchShower() {
    try {
      jbInit();
      makeDataDir();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new PatchShower();
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
    this.setTitle("色塊產生器");
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
//    jMenuItem_copydata.setText("copy data");
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

    jPopupMenu1.add(jMenu_size);
    jPopupMenu1.add(jMenu_setup);
    jPopupMenu1.add(jMenu_sample);

    jMenu_setup.add(jMenuItem_interval);
    jMenu_setup.add(jMenuItem_threshold);
    jMenu_setup.add(jRadioButton_copydata);
    jMenu_setup.setText("設定");
    jPopupMenu1.addSeparator();
    jPopupMenu1.add(jMenuItem_loading);
    jPopupMenu1.add(jMenuItem_monitor);
    jPopupMenu1.addSeparator();
    jPopupMenu1.add(jMenuItem_start);
    jPopupMenu1.add(jMenuItem_stop);

    this.setVisible(true);
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
    jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
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
    this.jFileChooser1.showDialog(this, "設定");
    monitorFile = jFileChooser1.getSelectedFile();
    if (monitorFile != null) {
      this.jMenuItem_monitor.setEnabled(false);
    }
    judgeOK();
  }

  protected void judgeOK() {
    if (patchFile != null && monitorFile != null) {
//      this.jMenuItem_start.setEnabled(true);
//      this.jMenuItem_stop.setEnabled(false);
      toggleControl(true);
    }
    else {
//      this.jMenuItem_start.setEnabled(false);
//      this.jMenuItem_stop.setEnabled(true);
//      toggleControl(false);
    }
  }

  public void jMenuItem_start_actionPerformed(ActionEvent e) {
    //設定取樣次數
    samplingTimes = buttonGroup1.getSelection().getMnemonic();

    //啟動檔案狀態監控執行緒
    fileMonitor = new FileMonitor(monitorFile, this);
    FileMonitor.INTERVAL = this.interval;
    FileMonitor.FILE_SIZE_THRESHOLD = this.fileSizeThreshold;
    fileMonitor.start();

    //初始第一個色塊
    int count = fileMonitor.count;
    if (count > dataSet.size()) {
      return;
    }
    String text = getSampleName(count);
    this.setTitle("色塊產生器 - " + text + " (總:" + dataSet.size() + ")"
                  + (samplingTimes != 1 ? " (多重取樣:" + samplingTimes + "x)" : ""));
    int[] rgb = getRGB(count);
    this.getContentPane().setBackground(new Color(rgb[0], rgb[1], rgb[2]));

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
//    this.jMenuItem_stop.setEnabled(!control);

    this.jMenuItem_loading.setEnabled(control);

  }

  protected boolean makeDataDir() {
    boolean check = true;
    if (dataDir == null) {
      File curDir = new File(System.getProperty("user.dir"));
      File measureDir = new File(curDir, "Measurement Files");
      if (!measureDir.exists()) {
        check = check && measureDir.mkdir();
      }

      dataDir = new File(curDir, "Measurement Files/AutoMeasurement");
      if (!dataDir.exists()) {
        check = check && dataDir.mkdir();
      }
    }
    return check;
  }

  protected int[] getRGB(int index) {
    double[] dRGB = dataSet.getTestChartData(index).RGB;
//    int[] iRGB = new int[] {
//        (int) Math.floor(dRGB[0]), (int) Math.floor(dRGB[1]),
//        (int) Math.floor(dRGB[2])};
//    return iRGB;
    return Maths.floor(dRGB);
  }

  protected String getSampleName(int index) {
    return dataSet.getTestChartData(index).sampleName;
  }

  public void callback() throws IOException {
    int recSampleIndex = fileMonitor.count;
    recSampleIndex = (int) ( (double) recSampleIndex / samplingTimes);

    int showSampleIndex = recSampleIndex +
        ( ( (fileMonitor.count + 1) % samplingTimes) == 0 ? 1 : 0);
//    System.out.println(recSampleIndex + " " + showSampleIndex + " " +
//                       fileMonitor.count);

    //==========================================================================
    //此為記錄區段
    //==========================================================================
    if (recSampleIndex >= dataSet.size()) {
      return;
    }

    String recText = getSampleName(recSampleIndex);

    if (samplingTimes != 1) {
      int suffix = (fileMonitor.count % samplingTimes) + 1;
      File newFile = new File(dataDir, recText + "-" + suffix + ".txt");
      Utils.copyFileByNIO(monitorFile, newFile);
    }
    else if (jRadioButton_copydata.isSelected()) {
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
    String str = JOptionPane.showInputDialog("interval", interval);
    if (str != null) {
      interval = Integer.parseInt(str);
    }
  }

  public void jMenuItem_threshold_actionPerformed(ActionEvent e) {
    String str = JOptionPane.showInputDialog("fileSizeThreshold",
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
    fileMonitor.pause();
    //重置
    fileMonitor.reset();
    started = false;
  }

  public void jMenuItem_readme_actionPerformed(ActionEvent e) {
    ReadmeFrame readmeframe = new ReadmeFrame();
    readmeframe.setLocationRelativeTo(this);
    readmeframe.setVisible(true);
    readmeframe = null;
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
