package shu.cms.applet.measure.tool;

import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.borland.jbcl.layout.*;
import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.cms.measure.*;
import shu.cms.ui.PatchCanvas;
import shu.ui.*;

/**
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
 */
public class LCDTargetFrame
    extends JInternalFrame {
//    extends JFrame {
  protected JLabel jLabel_Choose = new JLabel();
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JPanel jPanel1 = new JPanel();
  protected JLabel jLabel_TestChart = new JLabel();
  protected JComboBox jComboBox_TestChart = new JComboBox();
  protected JPanel jPanel2 = new JPanel();
  protected JButton jButton_Start = new JButton();
  protected PatchCanvas patchCanvas = new PatchCanvas();
  protected JPanel jPanel_TestChart = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout2 = new VerticalFlowLayout();
  protected BorderLayout borderLayout2 = new BorderLayout();
  protected JCheckBox jCheckBox_DICOMMode = new JCheckBox();
  protected JCheckBox jCheckBox_255InverseMode = new JCheckBox();
  protected JComboBox jComboBox_LCDSize = new JComboBox();
  protected JLabel jLabel_LCDSize = new JLabel();
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JPanel jPanel_Excute = new JPanel();
  protected JLabel jLabel_DelayTime = new JLabel();
  /**
   * 預設一個delay time
   */
  protected JTextField jTextField_DelayTime = new JTextField();
  protected long delayTime;
  protected JCheckBox jCheckBox_Calibration = new JCheckBox();
  protected JCheckBox jCheckBox_MeasureDisplay2 = new JCheckBox();
  protected FlowLayout flowLayout1 = new FlowLayout();
  protected JButton jButton_Batch = new JButton();

  public LCDTargetFrame() {
    this("Test Chart Measurement", 300);
  }

  public LCDTargetFrame(String title, long delayTime) {
    this(title, delayTime, Mode.Normal);
  }

  public static enum Mode {
    Argyll, Dummy, Normal, HiBits
  }

  private Mode mode;

  public LCDTargetFrame(String title, long delayTime, Mode mode) {
    super(title, true, true, true, true);
    this.delayTime = delayTime;
    this.mode = mode;

    try {
      jbInit();
      myInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void myInit() throws Exception {
    jCheckBox_HighBits.setSelected(mode == Mode.HiBits);
    this.jComboBox_HighBits.setEnabled(mode == Mode.HiBits);
    this.jComboBox_ICBits.setEnabled(mode == Mode.HiBits);
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    jLabel_Choose.setText("Choose the type of test chart to measure:");
    jLabel_TestChart.setText("Test Chart:");
    jPanel1.setLayout(borderLayout2);
    jButton_Start.setText("Start");
    jButton_Start.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_Start_actionPerformed(e);
      }
    });
    jPanel2.setLayout(verticalFlowLayout1);
    jPanel_TestChart.setLayout(verticalFlowLayout2);
    jComboBox_TestChart.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jComboBox_TestChart_actionPerformed(e);
      }
    });
    jComboBox_TestChart.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jComboBox_TestChart_mouseWheelMoved(e);
      }
    });
    jCheckBox_DICOMMode.setEnabled(false);
    jCheckBox_DICOMMode.setText("DICOM Mode");
    jCheckBox_255InverseMode.setText("255 Inverse Mode(Replace by 254)");

    jLabel_LCDSize.setText("LCD Size:");
    jComboBox_LCDSize.addItem("12\"");
    jComboBox_LCDSize.addItem("13\"");
    jComboBox_LCDSize.addItem("14\"");
    jComboBox_LCDSize.addItem("15\"");
    jComboBox_LCDSize.addItem("17\"");
    jComboBox_LCDSize.addItem("19\"");
    jComboBox_LCDSize.addItem("20\"");
    jComboBox_LCDSize.addItem("21\"");
    jComboBox_LCDSize.addItem("22\"");
    jComboBox_LCDSize.addItem("24\"");
    jComboBox_LCDSize.addItem("26\"");
    jComboBox_LCDSize.addItem("27\"");
    jComboBox_LCDSize.addItem("30\"");
    jComboBox_LCDSize.addItem("32\"");
    jComboBox_LCDSize.addItem("37\"");
    jComboBox_LCDSize.setSelectedItem("24\"");

    jComboBox_LCDSize.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jComboBox_LCDSize_mouseWheelMoved(e);
      }
    });
    jPanel_Excute.setLayout(flowLayout1);
    patchCanvas.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        patchCanvas_mouseWheelMoved(e);
      }
    });
    jLabel_DelayTime.setText("Delay Time (ms):");
    jTextField_DelayTime.setText(String.valueOf(delayTime));

    jTextField_DelayTime.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jTextField_DelayTime_mouseWheelMoved(e);
      }
    });
    jCheckBox_Calibration.setSelected(true);
    jCheckBox_Calibration.setText("Calibration");
    jCheckBox_MeasureDisplay2.setText("Measure Display 2");
    flowLayout1.setAlignment(FlowLayout.RIGHT);
    jButton_Batch.setText("Batch");
    jButton_Batch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_Batch_actionPerformed(e);
      }
    });
    jCheckBox_BlankInsert.setText("Blank Insert");
    jCheckBox_BlankInsert.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jCheckBox_BlankInsert_actionPerformed(e);
      }
    });
    jPanel_Blank.setLayout(flowLayout3);
    jComboBox_BlankSelect.setEnabled(false);
    jComboBox_BlankSelect.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jComboBox_BlankSelect_mouseWheelMoved(e);
      }
    });
    jPanel_HighBits.setLayout(flowLayout3);
    jCheckBox_HighBits.setEnabled(false);

    jCheckBox_HighBits.setText("High Bits");
    jCheckBox_HighBits.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jCheckBox_HighBits_actionPerformed(e);
      }
    });
    flowLayout3.setAlignment(FlowLayout.LEFT);
    flowLayout3.setHgap(0);
    flowLayout3.setVgap(0);
    jComboBox_HighBits.setEnabled(false);
    jComboBox_HighBits.setMinimumSize(new Dimension(75, 27));
    jComboBox_HighBits.setPreferredSize(new Dimension(75, 27));
    jComboBox_HighBits.setSelectedIndex(1);
    jComboBox_HighBits.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jComboBox_HighBits_mouseWheelMoved(e);
      }
    });
    jLabel_ICBits.setText("   IC");
    jComboBox_ICBits.setEnabled(false);
    jComboBox_ICBits.setMinimumSize(new Dimension(75, 27));
    jComboBox_ICBits.setPreferredSize(new Dimension(75, 27));
    jComboBox_ICBits.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jComboBox_ICBits_mouseWheelMoved(e);
      }
    });
    jLabel_DICOMMode.setText("DICOM Mode");
    jPanel3.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    flowLayout2.setHgap(0);
    flowLayout2.setVgap(0);
    jComboBox_DICOMMode.setPreferredSize(new Dimension(155, 23));
    jComboBox_DICOMMode.setSelectedIndex(2);
    jComboBox_DICOMMode.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jComboBox_DICOMMode_mouseWheelMoved(e);
      }
    });
    this.getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
    jPanel_TestChart.add(jLabel_TestChart);
    jPanel_TestChart.add(jComboBox_TestChart);
    this.getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);
    this.getContentPane().add(jLabel_Choose, java.awt.BorderLayout.NORTH);
    jPanel1.add(jPanel_TestChart, java.awt.BorderLayout.NORTH);
    jPanel1.add(patchCanvas, java.awt.BorderLayout.CENTER);
    jPanel2.add(jLabel_LCDSize, null);
    jPanel2.add(jComboBox_LCDSize, null);
    jPanel2.add(jLabel_DelayTime);
    jPanel2.add(jTextField_DelayTime);
    jPanel2.add(jCheckBox_DICOMMode, null);
    jPanel2.add(jPanel3);
    jPanel3.add(jLabel_DICOMMode);
    jPanel3.add(jComboBox_DICOMMode);
    jPanel2.add(this.jCheckBox_255InverseMode);
    jPanel2.add(jCheckBox_Calibration);

    jPanel2.add(jPanel_Blank);
    jPanel_Blank.add(jCheckBox_BlankInsert);
    jPanel_Blank.add(jComboBox_BlankSelect);
    jPanel2.add(jPanel_HighBits);
    jPanel_HighBits.add(jCheckBox_HighBits);
    jPanel_HighBits.add(jComboBox_HighBits);
    jPanel_HighBits.add(jLabel_ICBits);
    jPanel_HighBits.add(jComboBox_ICBits);

    if (mode == Mode.Argyll) {
      jPanel2.add(jCheckBox_MeasureDisplay2);
    }

    jPanel2.add(jPanel_Excute);
    jPanel_Excute.add(jButton_Start, null);
    jPanel_Excute.add(jButton_Batch);
    jPanel1.setBackground(Color.gray);

    initTestChart(jComboBox_TestChart);

    if (mode == Mode.Argyll) {
      jCheckBox_DICOMMode.setEnabled(false);
      jCheckBox_255InverseMode.setEnabled(false);
      jComboBox_LCDSize.setEnabled(false);
      jTextField_DelayTime.setEnabled(false);
      jCheckBox_Calibration.setSelected(false);
      jCheckBox_Calibration.setEnabled(false);
    }

    if (mode == Mode.Dummy) {
      jCheckBox_Calibration.setSelected(false);
      jCheckBox_Calibration.setEnabled(false);
    }

    this.pack();
  }

  private void initTestChart(JComboBox testChart) {
    if (mode == Mode.HiBits) {
      for (String str : MeasureToolFrame.RampTargetNumberList) {
        testChart.addItem(str);
      }
    }
    else {
      for (String str : MeasureToolFrame.TargetNumberList) {
        testChart.addItem(str);
      }
    }

  }

  public static void main(String[] args) {
    LCDTarget.setRGBNormalize(false);
    List<RGB>
        rgbList = LCDTarget.Instance.getRGBList(LCDTargetBase.Number.WHQL);

    LCDTargetFrame frame = new LCDTargetFrame();
    frame.patchCanvas.setReferenceRGBList(rgbList);
//    frame.pack();
    frame.setVisible(true);
  }

  public void jComboBox_TestChart_actionPerformed(ActionEvent e) {
    if (e.getSource() == jComboBox_TestChart) {
      String reminder = (String) jComboBox_TestChart.getSelectedItem();
//      System.out.println(reminder);
      LCDTargetBase.Number number = LCDTargetBase.Number.
          getNumberFromReferenceFilenameAndDescription(reminder);
      List<RGB> rgbList = LCDTargetBase.Instance.getRGBList(number);
      patchCanvas.setReferenceRGBList(rgbList);
      this.repaint();
    }
  }

  public void jButton_Start_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.batch = false;
  }

  public void jComboBox_TestChart_mouseWheelMoved(MouseWheelEvent e) {
    if (e.getSource() == this.jComboBox_TestChart &&
        this.jComboBox_TestChart.isEnabled()) {
      GUIUtils.mouseWheelMoved(e, jComboBox_TestChart);
    }
  }

  public void jComboBox_LCDSize_mouseWheelMoved(MouseWheelEvent e) {
    if (e.getSource() == jComboBox_LCDSize) {
      GUIUtils.mouseWheelMoved(e, jComboBox_LCDSize);
    }
  }

  public void patchCanvas_mouseWheelMoved(MouseWheelEvent e) {
    if (e.getSource() == patchCanvas &&
        this.jComboBox_TestChart.isEnabled()) {
      GUIUtils.mouseWheelMoved(e, jComboBox_TestChart);
    }
  }

  public void jTextField_DelayTime_mouseWheelMoved(MouseWheelEvent e) {
    if (e.getSource() == jTextField_DelayTime) {
      if (e.getWheelRotation() == 1) {
        //下
        int time = Integer.valueOf(jTextField_DelayTime.getText()) + 10;
        jTextField_DelayTime.setText(String.valueOf(time));
      }
      else if (e.getWheelRotation() == -1) {
        //上
        int time = Integer.valueOf(jTextField_DelayTime.getText()) - 10;
        time = time < 0 ? 0 : time;

        jTextField_DelayTime.setText(String.valueOf(time));
      }
    }
  }

  protected boolean batch = false;
  protected JPanel jPanel_Blank = new JPanel();
  protected JCheckBox jCheckBox_BlankInsert = new JCheckBox();
  protected JComboBox jComboBox_BlankSelect = new JComboBox(new String[] {
      "Black(VA/IPS)", "White(TN)"});
//  protected FlowLayout flowLayout2 = new FlowLayout();
  protected JPanel jPanel_HighBits = new JPanel();
  protected FlowLayout flowLayout3 = new FlowLayout();
  protected JCheckBox jCheckBox_HighBits = new JCheckBox();
  protected JComboBox jComboBox_HighBits = new JComboBox(new String[] {
      "9Bits", "10Bits"});
  protected JLabel jLabel_ICBits = new JLabel();
  protected JComboBox jComboBox_ICBits = new JComboBox(new String[] {
      "10Bits", "12Bits"});
  protected JPanel jPanel3 = new JPanel();
  protected JLabel jLabel_DICOMMode = new JLabel();
  protected JComboBox jComboBox_DICOMMode = new JComboBox(new String[] {
      "None", "DICOM", "DICOM Undecorated"});
  protected FlowLayout flowLayout2 = new FlowLayout();
  //  protected JCheckBox jCheckBox_WhiteOnly = new JCheckBox();
  public void jButton_Batch_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.batch = true;
  }

  public void jCheckBox_BlankInsert_actionPerformed(ActionEvent e) {
    boolean selected = this.jCheckBox_BlankInsert.isSelected();
    this.jComboBox_BlankSelect.setEnabled(selected);
  }

  public void jComboBox_BlankSelect_mouseWheelMoved(MouseWheelEvent e) {
    if (jCheckBox_BlankInsert.isSelected()) {
      GUIUtils.mouseWheelMoved(e, this.jComboBox_BlankSelect);
    }
  }

  protected DICOM getDICOM() {
    int index = this.jComboBox_DICOMMode.getSelectedIndex();
    switch (index) {
      case 0:
        return DICOM.None;
      case 1:
        return DICOM.Normal;
      case 2:
        return DICOM.Undecorated;
//      case 2:
//        return DICOM.Black;
      default:
        return DICOM.None;
    }
  }

  public AppMeasureParameter getAppMeasureParameter(AppMeasureParameter mp) {
    mp.dicomMode = getDICOM();
    mp.calibration = jCheckBox_Calibration.isSelected();
    mp.size = Integer.valueOf( ( (String) jComboBox_LCDSize.getSelectedItem()).
                              substring(0, 2));
    mp.targetNumber = LCDTargetBase.Number.
        getNumberFromReferenceFilenameAndDescription( (String)
        jComboBox_TestChart.getSelectedItem());
    mp.delayTimes = Integer.valueOf(jTextField_DelayTime.getText());
    mp.measureDisplay2 = jCheckBox_MeasureDisplay2.isSelected();
    mp.inverseMode = jCheckBox_255InverseMode.isSelected();
    mp.blankInsert = jCheckBox_BlankInsert.isSelected();
    mp.blank = jComboBox_BlankSelect.getSelectedIndex() == 0 ?
        Color.black : Color.white;
    mp.batch = batch;
    if (this.jCheckBox_HighBits.isSelected()) {
      String measureBits = (String)this.jComboBox_HighBits.getSelectedItem();
      if ("10Bits".equals(measureBits)) {
        mp.measureBits = MeasureBits.TenBits;
      }
      else if ("9Bits".equals(measureBits)) {
        mp.measureBits = MeasureBits.NineBits;
      }

      String icBits = (String)this.jComboBox_ICBits.getSelectedItem();
      if ("10Bits".equals(icBits)) {
        mp.icBits = MeasureBits.TenBits;
      }
      else if ("12Bits".equals(icBits)) {
        mp.icBits = MeasureBits.TwelveBits;
      }

    }
    else {
      mp.measureBits = MeasureBits.EightBits;
    }
    return mp;
  }

  public void jCheckBox_HighBits_actionPerformed(ActionEvent e) {
    boolean selected = this.jCheckBox_HighBits.isSelected();
    this.jComboBox_HighBits.setEnabled(selected);
    this.jComboBox_ICBits.setEnabled(selected);

    this.jComboBox_TestChart.setEnabled(!selected);
    this.jCheckBox_BlankInsert.setEnabled(!selected);
    this.jButton_Batch.setEnabled(!selected);
  }

  public void jComboBox_HighBits_mouseWheelMoved(MouseWheelEvent e) {
    if (this.jCheckBox_HighBits.isSelected()) {
      GUIUtils.mouseWheelMoved(e, this.jComboBox_HighBits);
    }
  }

  public void jComboBox_ICBits_mouseWheelMoved(MouseWheelEvent e) {
    if (this.jCheckBox_HighBits.isSelected()) {
      GUIUtils.mouseWheelMoved(e, this.jComboBox_ICBits);
    }
  }

  public void jComboBox_DICOMMode_mouseWheelMoved(MouseWheelEvent e) {
    GUIUtils.mouseWheelMoved(e, this.jComboBox_DICOMMode);
  }
}
