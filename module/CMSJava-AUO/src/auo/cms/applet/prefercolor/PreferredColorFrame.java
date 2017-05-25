package auo.cms.applet.prefercolor;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import auo.cms.prefercolor.model.*;
import com.borland.jbcl.layout.*;
import shu.cms.lcd.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class PreferredColorFrame
    extends JFrame {
  protected JPanel contentPane;
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JPanel jPanel1 = new JPanel();
  protected JPanel jPanel2 = new JPanel();
  protected TitledBorder titledBorder1 = new TitledBorder("");
  protected TitledBorder titledBorder2 = new TitledBorder("");
  protected JRadioButton jRadioButton_MeasureData = new JRadioButton();
  protected JRadioButton jRadioButton_PsychophysicsData = new JRadioButton();
  protected VerticalFlowLayout verticalFlowLayout2 = new VerticalFlowLayout();
  protected JTextField jTextField_InputData = new JTextField();
  protected JPanel jPanel_Input = new JPanel();
  protected FlowLayout flowLayout1 = new FlowLayout();
  protected JLabel jLabel1 = new JLabel();
  protected JButton jButton_InputDataBrowse = new JButton();
  protected JRadioButton jRadioButton_HSVLUT = new JRadioButton();
  protected JRadioButton jRadioButton_Image = new JRadioButton();
  protected VerticalFlowLayout verticalFlowLayout3 = new VerticalFlowLayout();
  protected JPanel jPanel4 = new JPanel();
  protected JButton jButton_OutputImageBrowse = new JButton();
  protected JLabel jLabel2 = new JLabel();
  protected JTextField jTextField_OutputImage = new JTextField();
  protected JPanel jPanel5 = new JPanel();
  protected JLabel jLabel3 = new JLabel();
  protected JTextField jTextField_InputImage = new JTextField();
  protected JButton jButton_InputImageBrowse = new JButton();
  protected JPanel jPanel6 = new JPanel();
  protected JLabel jLabel4 = new JLabel();
  protected JTextField jTextField_OutputHSV = new JTextField();
  protected JButton jButton_OutputHSVBrowse = new JButton();
  protected JPanel jPanel3 = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout4 = new VerticalFlowLayout();
  protected JPanel jPanel7 = new JPanel();
  protected JButton jButton1 = new JButton();
  protected FlowLayout flowLayout2 = new FlowLayout();
  protected JFileChooser jFileChooser1 = new JFileChooser();
  protected ButtonGroup buttonGroup1 = new ButtonGroup();
  protected ButtonGroup buttonGroup2 = new ButtonGroup();
  protected JPanel jPanel8 = new JPanel();
  protected TitledBorder titledBorder3 = new TitledBorder("");
  protected JCheckBox jCheckBox_MultiSpot = new JCheckBox();
  protected FlowLayout flowLayout3 = new FlowLayout();
  public PreferredColorFrame() {
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
    titledBorder3 = new TitledBorder("Parameter");
    titledBorder2 = new TitledBorder("Output");
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(verticalFlowLayout1);
    setSize(new Dimension(600, 600));
    setTitle("Preferred Color");
    jPanel1.setBackground(SystemColor.control);
    jPanel1.setBorder(titledBorder1);
    jPanel1.setLayout(verticalFlowLayout2);
    titledBorder1.setTitle("Input");
    jRadioButton_MeasureData.setSelected(true);
    jRadioButton_MeasureData.setText("Measure Data");
    jRadioButton_PsychophysicsData.setText("Psychophysics Data");
    jTextField_InputData.setMinimumSize(new Dimension(380, 20));
    jTextField_InputData.setPreferredSize(new Dimension(380, 20));
    jPanel_Input.setLayout(flowLayout1);
    jLabel1.setText("Filename");
    flowLayout1.setAlignment(FlowLayout.LEFT);
    jButton_InputDataBrowse.setText("Browse");
    jButton_InputDataBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_Browse_actionPerformed(e);
      }
    });
    jRadioButton_HSVLUT.setSelected(true);
    jRadioButton_HSVLUT.setText("HSV LUT");
    jRadioButton_Image.setText("Image");
    jPanel2.setLayout(verticalFlowLayout3);
    jButton_OutputImageBrowse.setText("Browse");
    jButton_OutputImageBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_OutputImageBrowse_actionPerformed(e);
      }
    });
    jLabel2.setText("Output Filename");
    jTextField_OutputImage.setMinimumSize(new Dimension(380, 20));
    jTextField_OutputImage.setPreferredSize(new Dimension(380, 20));
    jLabel3.setText("Input Filename");
    jTextField_InputImage.setMinimumSize(new Dimension(380, 20));
    jTextField_InputImage.setPreferredSize(new Dimension(380, 20));
    jButton_InputImageBrowse.setText("Browse");
    jButton_InputImageBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_InputImageBrowse_actionPerformed(e);
      };
    });
    jPanel2.setBorder(titledBorder2);
    jLabel4.setText("Output Filename");
    jTextField_OutputHSV.setMinimumSize(new Dimension(380, 20));
    jTextField_OutputHSV.setPreferredSize(new Dimension(380, 20));
    jButton_OutputHSVBrowse.setText("Browse");
    jButton_OutputHSVBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_OutputHSVBrowse_actionPerformed(e);
      }
    });
    jPanel3.setLayout(verticalFlowLayout4);
    jButton1.setText("Excute");
    jButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jPanel7.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.RIGHT);
    jPanel8.setBorder(titledBorder3);
    jPanel8.setLayout(flowLayout3);
    jCheckBox_MultiSpot.setText("MultiSpot");
    flowLayout3.setAlignment(FlowLayout.LEFT);
    contentPane.add(jPanel1);
    jPanel1.add(jRadioButton_MeasureData);
    jPanel1.add(jPanel_Input);
    jPanel1.add(jRadioButton_PsychophysicsData);
    contentPane.add(jPanel8);
    contentPane.add(jPanel2);
    jPanel6.add(jLabel4);
    jPanel6.add(jTextField_OutputHSV);
    jPanel6.add(jButton_OutputHSVBrowse);
    contentPane.add(jPanel7);
    jPanel7.add(jButton1);
    jPanel2.add(jRadioButton_Image);
    jPanel2.add(jPanel3);
    jPanel3.add(jPanel5);
    jPanel3.add(jPanel4);
    jPanel5.add(jLabel3);
    jPanel5.add(jTextField_InputImage);
    jPanel5.add(jButton_InputImageBrowse);
    jPanel4.add(jLabel2);
    jPanel4.add(jTextField_OutputImage);
    jPanel4.add(jButton_OutputImageBrowse);

    jPanel2.add(jRadioButton_HSVLUT);
    jPanel2.add(jPanel6);
    jPanel_Input.add(jLabel1);
    jPanel_Input.add(jTextField_InputData, null);
    jPanel_Input.add(jButton_InputDataBrowse);
    buttonGroup1.add(jRadioButton_MeasureData);
    buttonGroup1.add(jRadioButton_Image);
    buttonGroup1.add(jRadioButton_PsychophysicsData);

    buttonGroup2.add(jRadioButton_Image);
    buttonGroup2.add(jRadioButton_HSVLUT);
    jPanel8.add(jCheckBox_MultiSpot, null);
  }

  public void jButton_Browse_actionPerformed(ActionEvent e) {
//    jFileChooser1.set
    jFileChooser1.showOpenDialog(this);
    File file = jFileChooser1.getSelectedFile();
    if (file != null) {
      jTextField_InputData.setText(file.getAbsolutePath());
    }
  }

  public void jButton_InputImageBrowse_actionPerformed(ActionEvent e) {
    jFileChooser1.showOpenDialog(this);
    File file = jFileChooser1.getSelectedFile();
    if (file != null) {
      jTextField_InputImage.setText(file.getAbsolutePath());
    }

  }

  public void jButton_OutputImageBrowse_actionPerformed(ActionEvent e) {
    jFileChooser1.showSaveDialog(this);
    File file = jFileChooser1.getSelectedFile();
    if (file != null) {
      jTextField_OutputImage.setText(file.getAbsolutePath());
    }

  }

  public void jButton_OutputHSVBrowse_actionPerformed(ActionEvent e) {
    jFileChooser1.showSaveDialog(this);
    File file = jFileChooser1.getSelectedFile();
    if (file != null) {
      jTextField_OutputHSV.setText(file.getAbsolutePath());
    }

  }

  public void jButton1_actionPerformed(ActionEvent e) {
    //=========================================================================
    // input
    //=========================================================================
    if (jRadioButton_MeasureData.isSelected()) {
      //measure
      String inputFilename = jTextField_InputData.getText();
    }
    else {
      //psychophysic
      LCDTarget eizoTarget = LCDTarget.Instance.getFromAUORampXLS(
          "data/auo psychophysics/eizo ramp.xls");
      LCDTarget.Operator.gradationReverseFix(eizoTarget);
      ExperimentAnalyzer analyzer = new ExperimentAnalyzer(eizoTarget,
          "data/auo psychophysics");
      analyzer.analyze();
    }
    //=========================================================================
    //=========================================================================
    // output
    //=========================================================================
    if (jRadioButton_Image.isSelected()) {
      //image
    }
    else {
      //hsv lut
    }
    //=========================================================================
  }
}
