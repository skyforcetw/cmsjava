package shu.cms.applet.calibrate;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.borland.jbcl.layout.*;
import shu.cms.*;
import shu.cms.colorformat.logo.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.jnd.*;
import shu.cms.measure.*;
import shu.cms.measure.meter.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
import shu.ui.*;
//import shu.plot.*;

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
public class WPCalibratorFrame
    extends JFrame {
  protected JPanel contentPane;
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JPanel jPanel1 = new JPanel();
  protected JPanel jPanel2 = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JButton jButton1 = new JButton();
  protected JComboBox jComboBox1 = new JComboBox();
  protected JComboBox jComboBox2 = new JComboBox();
  protected Plot2D plot2D = Plot2D.getInstance();
  protected BorderLayout borderLayout2 = new BorderLayout();
  protected Meter meter = new DummyMeter();
  public WPCalibratorFrame() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    plot2D.setFixedBounds(0, 0, 1);
    plot2D.setFixedBounds(1, 0, 1);
    plot2D.addLegend();
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    setSize(new Dimension(800, 600));
    setTitle("White Point Calibrator");
    jPanel1.setLayout(verticalFlowLayout1);
    jButton1.setText("Measure");
    jButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jPanel2.setLayout(borderLayout2);
    jComboBox2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jComboBox2_actionPerformed(e);
      }
    });
    jComboBox1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jComboBox1_actionPerformed(e);
      }
    });
    jToggleButton1.setText("Continuous");
    jToggleButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jToggleButton1_actionPerformed(e);
      }
    });
    jTextField1.setText("1000");
    jCheckBox1.setSelected(true);
    jCheckBox1.setText("Dummy");
    jCheckBox1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jCheckBox1_actionPerformed(e);
      }
    });
    jButton2.setText("Calibrate");
    jButton2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jPanel3.setLayout(verticalFlowLayout2);
    jPanel3.setBorder(BorderFactory.createEtchedBorder());
    jPanel4.setLayout(verticalFlowLayout3);
    jPanel4.setBorder(BorderFactory.createEtchedBorder());
    jLabel1.setText("Luminance");
    jPanel5.setLayout(verticalFlowLayout4);
    jTextField2.setMinimumSize(new Dimension(11, 40));
    jTextField2.setPreferredSize(new Dimension(150, 24));
    jTextField2.setText("0.0");
    jLabel2.setText("x");
    jTextField3.setMinimumSize(new Dimension(40, 24));
    jTextField3.setPreferredSize(new Dimension(150, 24));
    jTextField3.setText("0.0");
    jPanel5.setToolTipText("");
    jLabel3.setText("y");
    jTextField4.setMinimumSize(new Dimension(40, 24));
    jTextField4.setPreferredSize(new Dimension(150, 24));
    jTextField4.setText("0.0");
    contentPane.setPreferredSize(new Dimension(885, 556));
    jTextField5.setMinimumSize(new Dimension(40, 24));
    jTextField5.setPreferredSize(new Dimension(150, 24));
    jTextField6.setMinimumSize(new Dimension(40, 24));
    jTextField6.setPreferredSize(new Dimension(150, 24));
    jTextField6.setText("0.0");
    jLabel4.setText("dx");
    jLabel5.setText("dy");
    jLabel6.setToolTipText("");
    jLabel6.setText("dxy");
    jTextField7.setMinimumSize(new Dimension(40, 24));
    jTextField7.setPreferredSize(new Dimension(150, 24));
    jTextField7.setText("0.0");
    jButton3.setText("store");
    jButton3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton3_actionPerformed(e);
      }
    });
    jLabel7.setText("Target:");
    contentPane.add(jPanel1, java.awt.BorderLayout.WEST);
    jPanel4.add(jComboBox2);
    jPanel4.add(jComboBox1);

    jPanel1.add(jButton1);

    jPanel1.add(jToggleButton1);
    jPanel1.add(jPanel3);
    jPanel3.add(jButton2);
    jPanel3.add(jCheckBox1);
    jPanel3.add(jComboBox3);
    jPanel3.add(jTextField1);
    jPanel1.add(jPanel4);
    jPanel2.add(plot2D.getPlotPanel(), java.awt.BorderLayout.CENTER);
    jPanel1.add(jButton3);

    contentPane.add(jPanel2, java.awt.BorderLayout.CENTER);
    contentPane.add(jPanel5, java.awt.BorderLayout.SOUTH);
    jPanel5.add(jPanel8);
    jPanel8.add(jLabel7);

    jPanel5.add(jPanel7);
    jPanel7.add(jLabel1);
    jPanel7.add(jTextField2);
    jPanel7.add(jLabel2);
    jPanel7.add(jTextField3);
    jPanel7.add(jLabel3);
    jPanel7.add(jTextField4);
    jPanel5.add(jPanel6);
    jPanel6.add(jLabel4);
    jPanel6.add(jTextField5);
    jPanel6.add(jLabel5);
    jPanel6.add(jTextField6);
    jPanel6.add(jLabel6);
    jPanel6.add(jTextField7);
    jComboBox1.addItem("D65");
    jComboBox1.addItem("D93");
    jComboBox2.addItem("CIExy 2deg");
    jComboBox2.addItem("CIExy 10deg");
    jComboBox2.addItem("CIElm 2deg");
    jComboBox2.addItem("CIElm 10deg");
    jComboBox2.addItem("CIExy 2deg(1978)");
    jComboBox3.addItem("LCD");
    jComboBox3.addItem("CRT");
  }

  public static void main(String[] args) {
    GUIUtils.runAsApplication(new WPCalibratorFrame(), false);
  }

  private double[] getChromaticityCoordinatesLuminance(int coordinateIndex,
      Spectra spectra) {
    double[] xyYValues = null;
    switch (coordinateIndex) {
      case 0:
        xyYValues = new CIExyY(spectra.getXYZ(ColorMatchingFunction.
                                              CIE_1931_2DEG_XYZ)).getValues();
        xlabel = "x";
        ylabel = "y";
        break;
      case 1:
        xyYValues = new CIExyY(spectra.getXYZ(ColorMatchingFunction.
                                              CIE_1964_10DEG_XYZ)).getValues();
        xlabel = "x";
        ylabel = "y";
        break;
      case 2:
        xyYValues = spectra.getLMS(ConeFundamental.
                                   CIE_2007_2DEG_LMS).getlsYValues();
        xlabel = "l";
        ylabel = "s";
        break;
      case 3:
        xyYValues = spectra.getLMS(ConeFundamental.
                                   CIE_2007_10DEG_LMS).getlsYValues();
        xlabel = "l";
        ylabel = "s";
        break;
      case 4:
        xyYValues = new CIExyY(spectra.getXYZ(ColorMatchingFunction.
                                              JUDD_VOS_1978_2DEG_XYZ)).
            getValues();
        xlabel = "x'";
        ylabel = "y'";
        break;
    }
    return xyYValues;
  }

  private String xlabel, ylabel;
  protected JToggleButton jToggleButton1 = new JToggleButton();
  private void refreshPlot() {

    if (measureSpectra != null) {
      int targetIndex = jComboBox1.getSelectedIndex();
      int coordinateIndex = jComboBox2.getSelectedIndex();
      if (targetIndex == -1 || coordinateIndex == -1) {
        return;
      }
      Illuminant target = null;
      switch (targetIndex) {
        case 0:
          target = Illuminant.D65;
          break;
        case 1:
          target = Illuminant.D93;
          break;
      }
      //========================================================================
      double[] targetxyYValues = getChromaticityCoordinatesLuminance(
          coordinateIndex, target.getSpectra());
      this.jLabel7.setText("Target: " + targetxyYValues[0] + " " +
                           targetxyYValues[1]);

      plot2D.removeAllPlots();
      int index = plot2D.addScatterPlot("Target", Color.gray, targetxyYValues[0],
                                        targetxyYValues[1]);

      plot2D.setScatterPlotPattern(index, Plot2D.Pattern.X);
      double[][] circle001 = JNDCalculator.getJNDCircle(targetxyYValues, 64,
          0.001);
      plot2D.addLinePlot("0.001", Color.green, circle001);
      double[][] circle005 = JNDCalculator.getJNDCircle(targetxyYValues, 64,
          0.005);
      plot2D.addLinePlot("0.005", Color.red, circle005);
      //========================================================================

      //========================================================================
      Spectra r = new Spectra("", Spectra.SpectrumType.EMISSION, 400, 700, 10,
                              new double[31]);
      r.setData(700, 1);
      Spectra g = new Spectra("", Spectra.SpectrumType.EMISSION, 400, 700, 10,
                              new double[31]);
      g.setData(550, 1);
      Spectra b = new Spectra("", Spectra.SpectrumType.EMISSION, 400, 700, 10,
                              new double[31]);
      b.setData(440, 1);
      double[] rxyValues = getChromaticityCoordinatesLuminance(
          coordinateIndex, r);
      double[] gxyValues = getChromaticityCoordinatesLuminance(
          coordinateIndex, g);
      double[] bxyValues = getChromaticityCoordinatesLuminance(
          coordinateIndex, b);
      plot2D.addVectortoPlot(index,
                             new double[][] {new double[] {rxyValues[0] -
                             targetxyYValues[0],
                             rxyValues[1] - targetxyYValues[1]}
      });
      plot2D.addVectortoPlot(index,
                             new double[][] {new double[] {gxyValues[0] -
                             targetxyYValues[0],
                             gxyValues[1] - targetxyYValues[1]}
      });
      plot2D.addVectortoPlot(index,
                             new double[][] {new double[] {bxyValues[0] -
                             targetxyYValues[0],
                             bxyValues[1] - targetxyYValues[1]}
      });

      //========================================================================


      plot2D.setAxeLabel(0, xlabel);
      plot2D.setAxeLabel(1, ylabel);

      double[] measurexyValues =
          getChromaticityCoordinatesLuminance(
              coordinateIndex, measureSpectra);
      this.jTextField2.setText(Double.toString(
          measurexyValues[2]));
      this.jTextField3.setText(Double.toString(
          measurexyValues[0]));
      this.jTextField4.setText(Double.toString(
          measurexyValues[1]));
      double[] deltaValues = DoubleArray.minus(measurexyValues, targetxyYValues);
      this.jTextField5.setText(Double.toString(deltaValues[0]));
      this.jTextField6.setText(Double.toString(deltaValues[1]));
      double dxy = Maths.sqrt(Maths.sqr(deltaValues[0]) +
                              Maths.sqr(deltaValues[1]));
      this.jTextField7.setText(Double.toString(dxy));
      int measureIndex = plot2D.addScatterPlot("Measure",
                                               Color.magenta,
                                               measurexyValues[0],
                                               measurexyValues[1]);
      plot2D.setScatterPlotPattern(measureIndex,
                                   Plot2D.Pattern.Round);
    }

  }

  public void jComboBox2_actionPerformed(ActionEvent e) {
    if (plot2D != null) {
      refreshPlot();
    }
  }

  public void jComboBox1_actionPerformed(ActionEvent e) {
    jComboBox2_actionPerformed(e);
  }

  public void jButton1_actionPerformed(ActionEvent e) {
    if (jCheckBox1.isSelected()) {
      ( (DummyMeter) meter).setRGB(new RGB(RGB.ColorSpace.unknowRGB,
                                           new int[] { (int) (Math.random() *
          100.), (int) (Math.random() * 100.), (int) (Math.random() * 100.)}));
    }
    measureSpectra = meter.triggerMeasurementInSpectra();
    refreshPlot();

  }

  private Spectra measureSpectra;
  private Thread thread;
  private boolean stop;
  private long waitTime;
  protected JTextField jTextField1 = new JTextField();
  protected JCheckBox jCheckBox1 = new JCheckBox();
  protected JButton jButton2 = new JButton();
  protected JComboBox jComboBox3 = new JComboBox();
  protected JPanel jPanel3 = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout2 = new VerticalFlowLayout();
  protected TitledBorder titledBorder1 = new TitledBorder("");
  protected JPanel jPanel4 = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout3 = new VerticalFlowLayout();
  protected JPanel jPanel5 = new JPanel();
  protected JLabel jLabel1 = new JLabel();
  protected JTextField jTextField2 = new JTextField();
  protected JLabel jLabel2 = new JLabel();
  protected JTextField jTextField3 = new JTextField();
  protected JLabel jLabel3 = new JLabel();
  protected JTextField jTextField4 = new JTextField();
  protected JTextField jTextField6 = new JTextField();
  protected JLabel jLabel4 = new JLabel();
  protected JTextField jTextField5 = new JTextField();
  protected JLabel jLabel5 = new JLabel();
  protected JPanel jPanel6 = new JPanel();
  protected JPanel jPanel7 = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout4 = new VerticalFlowLayout();
  protected JLabel jLabel6 = new JLabel();
  protected JTextField jTextField7 = new JTextField();
  protected JButton jButton3 = new JButton();
  protected JLabel jLabel7 = new JLabel();
  protected JPanel jPanel8 = new JPanel();
  public void jToggleButton1_actionPerformed(ActionEvent e) {
    boolean continuous = jToggleButton1.isSelected();
    if (continuous) {
      stop = false;
      waitTime = Long.valueOf(jTextField1.getText());
      thread = new Thread() {
        public void run() {
          try {
            while (!stop) {
              Thread.currentThread().sleep(waitTime);
              Thread.currentThread().yield();
              jButton1_actionPerformed(null);
            }
          }
          catch (InterruptedException ex) {
            ex.printStackTrace();
          }
        }
      };
      thread.start();
    }
    else {
      stop = true;
    }
  }

  public void jCheckBox1_actionPerformed(ActionEvent e) {
    boolean dummy = jCheckBox1.isSelected();
    if (dummy) {
      meter = new DummyMeter();
    }
    else {
      Meter.ScreenType type = this.jComboBox3.getSelectedIndex() == 0 ?
          Meter.ScreenType.LCD : Meter.ScreenType.CRT;
      meter = new EyeOnePro(type);
    }
  }

  public void jButton2_actionPerformed(ActionEvent e) {
    MeasureUtils.meterCalibrate(this, meter);
  }

  public void jButton3_actionPerformed(ActionEvent e) {
    CIEXYZ XYZ = measureSpectra.getXYZ();
    Patch p = new Patch("", XYZ, XYZ, null, null, measureSpectra);
    java.util.List<Patch> patchList = new ArrayList<Patch> ();
    patchList.add(p);
    try {
      LogoFile logoFile = new LogoFile("000.logo", patchList);
      logoFile.save();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
