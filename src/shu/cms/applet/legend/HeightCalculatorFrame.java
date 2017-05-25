package shu.cms.applet.legend;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import shu.cms.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 拍攝導表時,用來計算相機的高度角度等等設置的參數
 * 但是實際測試的結果是...不太實用.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public class HeightCalculatorFrame
    extends JFrame {
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel_chart = new JLabel();
  JLabel jLabel_chartH = new JLabel();
  JTextField jTextField_chartH = new JTextField();
  JLabel jLabel_chartW = new JLabel();
  JTextField jTextField_chartW = new JTextField();
  JLabel jLabel_flatTopH = new JLabel();
  JTextField jTextField_flatTopH = new JTextField();
  JLabel jLabel_viewAngle = new JLabel();
  JTextField jTextField_viewAngle = new JTextField();
  JLabel jLabel_fit = new JLabel();
  JTextField jTextField_fit = new JTextField();
  JButton jButton_cal = new JButton();
  JLabel jLabel_cameraH = new JLabel();
  JTextField jTextField_cameraH = new JTextField();
  JLabel jLabel_cameraD = new JLabel();
  JTextField jTextField_cameraD = new JTextField();
  JComboBox jComboBox_chart = new JComboBox(new Object[] {"CC24", "CCDC",
                                            "CCSG", "Munsell"});
  JComboBox jComboBox_lens = new JComboBox(new Object[] {"PE 40/4", "PE 75/2.8",
                                           "17mm(APS)", "18mm(APS)",
                                           "35mm(APS)", "55mm(APS)",
                                           "70mm(APS)"});
  static double[] viewAngleOfLens = new double[] {
      82.1, 49.7, 79, 76, 46, 28.83, 22.83};
  JLabel jLabel_focus = new JLabel();
  JTextField jTextField_focus = new JTextField();

  public HeightCalculatorFrame() {
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
    setSize(new Dimension(400, 300));
    setTitle("相機高度計算器");
    getContentPane().setLayout(gridBagLayout1);
    jLabel_chart.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel_chart.setText("導表(cm)");
    jLabel_flatTopH.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel_flatTopH.setText("平台高度(cm)");
    jLabel_viewAngle.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel_viewAngle.setText("鏡頭視角(角度)");
    jTextField_viewAngle.setMinimumSize(new Dimension(60, 23));
    jTextField_viewAngle.setPreferredSize(new Dimension(60, 23));
    jTextField_viewAngle.setToolTipText("");
    jLabel_fit.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel_fit.setText("導表比率(%)");
    jButton_cal.setText("計算");
    jButton_cal.addActionListener(new
                                  HeightCalculatorFrame_jButton_cal_actionAdapter(this));
    jLabel_cameraH.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel_cameraH.setText("相機高度(cm)");
    jLabel_cameraD.setToolTipText("");
    jLabel_cameraD.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel_cameraD.setText("距離導表底部水平距離");
    jLabel_chartW.setToolTipText("");
    jLabel_chartW.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel_chartW.setText("寬");
    jTextField_chartH.setMinimumSize(new Dimension(60, 23));
    jTextField_chartH.setPreferredSize(new Dimension(60, 23));
    jTextField_chartW.setMinimumSize(new Dimension(60, 23));
    jTextField_chartW.setPreferredSize(new Dimension(60, 23));
    jTextField_flatTopH.setMinimumSize(new Dimension(60, 23));
    jTextField_flatTopH.setPreferredSize(new Dimension(60, 23));
    jTextField_fit.setMinimumSize(new Dimension(60, 23));
    jTextField_fit.setPreferredSize(new Dimension(60, 23));
    jTextField_fit.setText("70");
    jTextField_cameraH.setMinimumSize(new Dimension(160, 23));
    jTextField_cameraH.setPreferredSize(new Dimension(160, 23));
    jTextField_cameraH.setEditable(false);
    jTextField_cameraD.setMinimumSize(new Dimension(160, 23));
    jTextField_cameraD.setPreferredSize(new Dimension(160, 23));
    jTextField_cameraD.setEditable(false);
    jLabel_chartH.setHorizontalAlignment(SwingConstants.CENTER);
    jComboBox_chart.setMinimumSize(new Dimension(60, 23));
    jComboBox_chart.setPreferredSize(new Dimension(60, 23));
    jComboBox_chart.addActionListener(new
                                      HeightCalculatorFrame_jComboBox_chart_actionAdapter(this));
    jLabel_chartH.setText("高");
    jComboBox_lens.setMinimumSize(new Dimension(120, 23));
    jComboBox_lens.setPreferredSize(new Dimension(120, 23));
    jComboBox_lens.addActionListener(new
                                     HeightCalculatorFrame_jComboBox_lens_actionAdapter(this));
    jLabel_focus.setText("對焦距離(cm)");
    jTextField_focus.setMinimumSize(new Dimension(160, 23));
    jTextField_focus.setPreferredSize(new Dimension(160, 23));
    jTextField_focus.setEditable(false);
    this.getContentPane().add(jLabel_chart,
                              new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(3, 0, 3, 0), 20, 4));
    this.getContentPane().add(jLabel_flatTopH,
                              new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(3, 0, 3, 0), 20, 4));
    this.getContentPane().add(jLabel_viewAngle,
                              new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(3, 0, 3, 0), 20, 4));
    this.getContentPane().add(jLabel_fit,
                              new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(3, 0, 3, 0), 20, 4));
    this.getContentPane().add(jButton_cal,
                              new GridBagConstraints(1, 4, 4, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(3, 0, 3, 0), 0, 0));
    this.getContentPane().add(jTextField_chartW,
                              new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jLabel_chartW,
                              new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 20, 4));
    this.getContentPane().add(jLabel_chartH,
                              new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 20, 4));
    this.getContentPane().add(jComboBox_chart,
                              new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jTextField_flatTopH,
                              new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jTextField_fit,
                              new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jLabel_cameraH,
                              new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(3, 0, 3, 0), 30, 4));
    this.getContentPane().add(jLabel_cameraD,
                              new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(3, 0, 3, 0), 0, 4));
    this.getContentPane().add(jTextField_chartH,
                              new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jTextField_viewAngle,
                              new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jComboBox_lens,
                              new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jLabel_focus,
                              new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jTextField_focus,
                              new GridBagConstraints(1, 7, 5, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(0, 10, 0, 0), 0, 0));
    this.getContentPane().add(jTextField_cameraH,
                              new GridBagConstraints(1, 5, 5, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(0, 10, 0, 0), 0, 0));
    this.getContentPane().add(jTextField_cameraD,
                              new GridBagConstraints(1, 6, 5, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(0, 10, 0, 0), 0, 0));

  }

  public static void main(String[] args) {
    HeightCalculatorFrame heightcalculatorframe = new HeightCalculatorFrame();
    heightcalculatorframe.setVisible(true);
  }

  protected static double toDouble(JTextField textField) {
    String text = textField.getText();
    if (text.length() == 0) {
      return 0;
    }
    else {
      return Double.parseDouble(textField.getText());
    }
  }

  /**
   *
   * @param e ActionEvent
   * @deprecated
   */
  public void jButton_cal_actionPerformed(ActionEvent e) {
    double chartH = toDouble(this.jTextField_chartH);
    double chartW = toDouble(this.jTextField_chartW);
    double flatTopH = toDouble(this.jTextField_flatTopH);
    double viewAngle = toDouble(this.jTextField_viewAngle);
    double fit = toDouble(this.jTextField_fit);

    if (chartH != 0 && chartW != 0 && flatTopH != 0 && viewAngle != 0 &&
        fit != 0) {
      HeightCalculator.Chart chart = new HeightCalculator.Chart(chartW, chartH);
      HeightCalculator cal = new HeightCalculator(chart, flatTopH, viewAngle,
                                                  fit);

      this.jTextField_cameraD.setText(String.valueOf(cal.
          getDistanceOfCamera2ChartBottom()));
      this.jTextField_cameraH.setText(String.valueOf(cal.getCameraHeight()));
      this.jTextField_focus.setText(String.valueOf(cal.getFocusDistance()));
    }
  }

  public void jComboBox_chart_actionPerformed(ActionEvent e) {
    switch (jComboBox_chart.getSelectedIndex()) {
      case 0: //24
        this.jTextField_chartH.setText(String.valueOf(21.59));
        this.jTextField_chartW.setText(String.valueOf(27.94));
        break;
      case 1: //DC
        this.jTextField_chartH.setText(String.valueOf(21.59));
        this.jTextField_chartW.setText(String.valueOf(35.56));
        break;
      case 2: //SG
        this.jTextField_chartH.setText(String.valueOf(21.59));
        this.jTextField_chartW.setText(String.valueOf(27.94));
        break;
      case 3: //MunsellBook
        this.jTextField_chartH.setText(String.valueOf(21.59));
        this.jTextField_chartW.setText(String.valueOf(27.94));
        break;
      default:
        this.jTextField_chartH.setText(String.valueOf(0));
        this.jTextField_chartW.setText(String.valueOf(0));
    }

  }

  public void jComboBox_lens_actionPerformed(ActionEvent e) {
    this.jTextField_viewAngle.setText(String.valueOf(viewAngleOfLens[
        jComboBox_lens.getSelectedIndex()]));
    /*switch (this.jComboBox_lens.getSelectedIndex()) {
      case 0: //PE 40/4
        this.jTextField_viewAngle.setText(String.valueOf(82.1));
        break;
      case 1: //PE 75/2.8
        this.jTextField_viewAngle.setText(String.valueOf(49.7));
        break;
      case 2: //17
        this.jTextField_viewAngle.setText(String.valueOf(79));
        break;
      case 3: //18
        this.jTextField_viewAngle.setText(String.valueOf(76));
        break;
      case 4: //35
        this.jTextField_viewAngle.setText(String.valueOf(46));
        break;
      case 5: //55
        this.jTextField_viewAngle.setText(String.valueOf(28.83));
        break;
      case 6: //70
        this.jTextField_viewAngle.setText(String.valueOf(22.83));
        break;
      default:
         }*/
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
class HeightCalculatorFrame_jComboBox_lens_actionAdapter
    implements ActionListener {
  private HeightCalculatorFrame adaptee;
  /**
   *
   * @param adaptee HeightCalculatorFrame
   * @deprecated
   */
  HeightCalculatorFrame_jComboBox_lens_actionAdapter(HeightCalculatorFrame
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jComboBox_lens_actionPerformed(e);
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
class HeightCalculatorFrame_jComboBox_chart_actionAdapter
    implements ActionListener {
  /**
   * @deprecated
   */
  private HeightCalculatorFrame adaptee;
  /**
   *
   * @param adaptee HeightCalculatorFrame
   * @deprecated
   */
  HeightCalculatorFrame_jComboBox_chart_actionAdapter(HeightCalculatorFrame
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jComboBox_chart_actionPerformed(e);
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
class HeightCalculatorFrame_jButton_cal_actionAdapter
    implements ActionListener {
  /**
   * @deprecated
   */
  private HeightCalculatorFrame adaptee;
  /**
   *
   * @param adaptee HeightCalculatorFrame
   * @deprecated
   */
  HeightCalculatorFrame_jButton_cal_actionAdapter(HeightCalculatorFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *
   * @param e ActionEvent
   * @deprecated
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_cal_actionPerformed(e);
  }
}
