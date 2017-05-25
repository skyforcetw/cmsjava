package shu.cms.applet.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.borland.jbcl.layout.*;
import shu.cms.applet.gradient.*;
import shu.cms.applet.legend.*;
import shu.cms.applet.measure.auto.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 公用程式視窗類別
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class UtilFrame
    extends JFrame {
  JPanel contentPane;
  JButton jButton_patchShower = new JButton();
  JButton jButton_sw2cxf = new JButton();
  JButton jButton_heightCalculator = new JButton();
  JButton jButton_instrumentCalibrator = new JButton();
  JButton jButton_sampleMaker = new JButton();
  JButton jButton_MeasurementFileChecker = new JButton();
  JButton jButton_RGBICCProfile2CxF = new JButton();
  JButton jButton_patchShowerVVV = new JButton();
  JButton jButton_GradientShower = new JButton();
  VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  public UtilFrame() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
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
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(verticalFlowLayout1);
    setSize(new Dimension(400, 400));
    setTitle("公用程式");
    jButton_patchShower.setToolTipText("PR-650 自動量測工具(色塊自動產生器)");
    jButton_patchShower.setText("PatchShower");
    jButton_patchShower.addActionListener(new
                                          UtilFrame_jButton_patchShower_actionAdapter(this));
    jButton_sw2cxf.setBackground(Color.gray);
    jButton_sw2cxf.setToolTipText("轉檔程式");
    jButton_sw2cxf.setText("SpectraWin轉CxF");
    jButton_sw2cxf.addActionListener(new UtilFrame_jButton_sw2cxf_actionAdapter(this));
    jButton_heightCalculator.setBackground(Color.gray);
    jButton_heightCalculator.setToolTipText("拍攝時相機的架設高度計算");
    jButton_heightCalculator.setText("相機高度計算器");
//    jButton_heightCalculator.addActionListener(new
//                                               UtilFrame_jButton_heightCalculator_actionAdapter(this));
    jButton_instrumentCalibrator.setBackground(Color.gray);
    jButton_instrumentCalibrator.setEnabled(false);
    jButton_instrumentCalibrator.setText("儀器校正");
    jButton_instrumentCalibrator.addActionListener(new
        UtilFrame_jButton_instrumentCalibrator_actionAdapter(this));
    jButton_sampleMaker.setText("SampleMaker");
    jButton_sampleMaker.addActionListener(new
                                          UtilFrame_jButton_sampleMaker_actionAdapter(this));
    jButton_MeasurementFileChecker.setText("MeasurementFileChecker");
    jButton_MeasurementFileChecker.addActionListener(new
        UtilFrame_jButton_MeasurementFileChecker_actionAdapter(this));
    jButton_RGBICCProfile2CxF.setText("ICCProfile DevD tag轉CxF");
    jButton_RGBICCProfile2CxF.addActionListener(new
                                                UtilFrame_jButton_RGBICCProfile2CxF_actionAdapter(this));
    jButton_patchShowerVVV.setToolTipText("");
    jButton_patchShowerVVV.setText("PatchShower(VastView Version)");
    jButton_patchShowerVVV.addActionListener(new
                                             UtilFrame_jButton_patchShowerVVV_actionAdapter(this));
    jButton_GradientShower.setText("GradientShower");
    jButton_GradientShower.addActionListener(new
                                             UtilFrame_jButton_GradientShower_actionAdapter(this));
    contentPane.setMinimumSize(new Dimension(229, 350));
    contentPane.setPreferredSize(new Dimension(229, 350));
    contentPane.add(jButton_patchShower, null);
    contentPane.add(jButton_patchShowerVVV, null);
    contentPane.add(jButton_instrumentCalibrator, null);
    contentPane.add(jButton_GradientShower, null);
    contentPane.add(jButton_RGBICCProfile2CxF, null);
    contentPane.add(jButton_sw2cxf, null);
    contentPane.add(jButton_MeasurementFileChecker, null);
    contentPane.add(jButton_sampleMaker, null);
    contentPane.add(jButton_heightCalculator, null);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = this.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    this.setLocation( (screenSize.width - frameSize.width) / 2,
                     (screenSize.height - frameSize.height) / 2);
    this.setVisible(true);

  }

  public static void main(String[] args) {
    new UtilFrame();
  }

  public void jButton_patchShower_actionPerformed(ActionEvent e) {
    new PatchShower();
  }

  public void jButton_sw2cxf_actionPerformed(ActionEvent e) {
    SpectraWin2CxFFrame toCxF = new SpectraWin2CxFFrame();
    toCxF.setLocationRelativeTo(this);
    toCxF.setVisible(true);
  }

  /**
   *
   * @param e ActionEvent
   * @deprecated
   */
  public void jButton_heightCalculator_actionPerformed(ActionEvent e) {
//    HeightCalculatorFrame cal = new HeightCalculatorFrame();
//    cal.setLocationRelativeTo(this);
//    cal.setVisible(true);
  }

  public void jButton_instrumentCalibrator_actionPerformed(ActionEvent e) {
//    InstrumentCalibratorFrame cali = new InstrumentCalibratorFrame();
//    cali.setLocationRelativeTo(this);
//    cali.setVisible(true);
  }

  public void jButton_sampleMaker_actionPerformed(ActionEvent e) {
    SampleMakerFrame maker = new SampleMakerFrame();
    maker.setLocationRelativeTo(this);
    maker.setVisible(true);
  }

  public void jButton_MeasurementFileChecker_actionPerformed(ActionEvent e) {
    MeasurementFileCheckFrame frame = new MeasurementFileCheckFrame();
    frame.setLocationRelativeTo(this);
    frame.setVisible(true);
  }

  public void jButton_RGBICCProfile2CxF_actionPerformed(ActionEvent e) {
    RGBICCProfile2CxFFrame frame = new RGBICCProfile2CxFFrame();
    frame.setLocationRelativeTo(this);
    frame.setVisible(true);
  }

  public void jButton_patchShowerVVV_actionPerformed(ActionEvent e) {
    new shu.cms.applet.measure.autovv.PatchShower();
  }

  public void jButton_GradientShower_actionPerformed(ActionEvent e) {
    new GradientShowerFrame();
  }
}

class UtilFrame_jButton_GradientShower_actionAdapter
    implements ActionListener {
  private UtilFrame adaptee;
  UtilFrame_jButton_GradientShower_actionAdapter(UtilFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_GradientShower_actionPerformed(e);
  }
}

class UtilFrame_jButton_patchShowerVVV_actionAdapter
    implements ActionListener {
  private UtilFrame adaptee;
  UtilFrame_jButton_patchShowerVVV_actionAdapter(UtilFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_patchShowerVVV_actionPerformed(e);
  }
}

class UtilFrame_jButton_RGBICCProfile2CxF_actionAdapter
    implements ActionListener {
  private UtilFrame adaptee;
  UtilFrame_jButton_RGBICCProfile2CxF_actionAdapter(UtilFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_RGBICCProfile2CxF_actionPerformed(e);
  }
}

class UtilFrame_jButton_MeasurementFileChecker_actionAdapter
    implements ActionListener {
  private UtilFrame adaptee;
  UtilFrame_jButton_MeasurementFileChecker_actionAdapter(UtilFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_MeasurementFileChecker_actionPerformed(e);
  }
}

class UtilFrame_jButton_sampleMaker_actionAdapter
    implements ActionListener {
  private UtilFrame adaptee;
  UtilFrame_jButton_sampleMaker_actionAdapter(UtilFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_sampleMaker_actionPerformed(e);
  }
}

class UtilFrame_jButton_instrumentCalibrator_actionAdapter
    implements ActionListener {
  private UtilFrame adaptee;
  UtilFrame_jButton_instrumentCalibrator_actionAdapter(UtilFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_instrumentCalibrator_actionPerformed(e);
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
class UtilFrame_jButton_heightCalculator_actionAdapter
    implements ActionListener {
  private UtilFrame adaptee;
  UtilFrame_jButton_heightCalculator_actionAdapter(UtilFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *
   * @param e ActionEvent
   * @deprecated
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_heightCalculator_actionPerformed(e);
  }
}

class UtilFrame_jButton_sw2cxf_actionAdapter
    implements ActionListener {
  private UtilFrame adaptee;
  UtilFrame_jButton_sw2cxf_actionAdapter(UtilFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_sw2cxf_actionPerformed(e);
  }
}

class UtilFrame_jButton_patchShower_actionAdapter
    implements ActionListener {
  private UtilFrame adaptee;
  UtilFrame_jButton_patchShower_actionAdapter(UtilFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_patchShower_actionPerformed(e);
  }
}
