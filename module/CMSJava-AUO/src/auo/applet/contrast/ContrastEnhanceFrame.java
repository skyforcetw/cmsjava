package auo.applet.contrast;

import java.io.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import auo.contrast.*;
import com.borland.jbcl.layout.*;
import shu.cms.image.*;
import shu.cms.plot.*;
import shu.cms.plot.Plot2D;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.plot.*;
import shu.math.array.*;
import shu.ui.GUIUtils;
import shu.image.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ContrastEnhanceFrame
    extends JFrame {
  protected JPanel contentPane;
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JButton jButton1 = new JButton();
  protected JPanel jPanel1 = new JPanel();
  protected JTextField jTextField2 = new JTextField();
  protected JPanel jPanel2 = new JPanel();
  protected JPanel jPanel3 = new JPanel();
  protected XYLayout xYLayout1 = new XYLayout();
  protected JCheckBox jCheckBox1 = new JCheckBox();
  protected JLabel jLabel1 = new JLabel();
  protected JComboBox jComboBox1 = new JComboBox();
  protected JLabel jLabel2 = new JLabel();
  protected JLabel jLabel3 = new JLabel();
  protected JComboBox jComboBox2 = new JComboBox();
  protected JSlider jSlider_lumiMedian = new JSlider();
  protected JLabel jLabel4 = new JLabel();
  protected JSlider jSlider_contrastStr = new JSlider();
  protected JLabel jLabel5 = new JLabel();
  protected JLabel jLabel6 = new JLabel();
  protected JLabel jLabel7 = new JLabel();
  protected JLabel jLabel8 = new JLabel();
  protected JLabel jLabel9 = new JLabel();
  protected JLabel jLabel10 = new JLabel();
  protected JSlider jSlider_darkMaxAdj = new JSlider();
  protected JSlider jSlider_brightMaxAdj = new JSlider();
  protected JSlider jSlider_darkOfs = new JSlider();
  protected JSlider jSlider_brightOfs = new JSlider();
  protected JSlider jSlider_darkDR = new JSlider();
  protected JSlider jSlider_brightDR = new JSlider();
  protected JLabel jLabel11 = new JLabel();
  protected JLabel jLabel12 = new JLabel();
  protected JLabel jLabel13 = new JLabel();
  protected JLabel jLabel14 = new JLabel();
  protected JLabel jLabel15 = new JLabel();
  protected JLabel jLabel16 = new JLabel();
  protected JLabel jLabel17 = new JLabel();
  protected JLabel jLabel18 = new JLabel();
  protected JPanel jPanel5 = new JPanel();
  protected BorderLayout borderLayout3 = new BorderLayout();
  protected JPanel jPanel6 = new JPanel();
  protected XYLayout xYLayout2 = new XYLayout();
  protected TitledBorder titledBorder1 = new TitledBorder("");
  protected JPanel jPanel7 = new JPanel();
  protected JPanel jPanel8 = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected TitledBorder titledBorder2 = new TitledBorder("");
  protected XYLayout xYLayout3 = new XYLayout();
  protected JPanel jPanel9 = new JPanel();
  protected XYLayout xYLayout4 = new XYLayout();
  protected TitledBorder titledBorder3 = new TitledBorder("");
  protected TitledBorder titledBorder4 = new TitledBorder("");
  protected JPanel jPanel10 = new JPanel();
  protected JScrollPane jScrollPane1 = new JScrollPane();
  protected JTable jTable1; // = new JTable();
  protected BorderLayout borderLayout4 = new BorderLayout();
  protected JPanel jPanel11 = new JPanel();
  protected JPanel jPanel12 = new JPanel();
  protected BorderLayout borderLayout2 = new BorderLayout();
  protected BorderLayout borderLayout5 = new BorderLayout();
  protected JFileChooser jFileChooser1 = new JFileChooser();
  protected FlowLayout flowLayout1 = new FlowLayout();
  protected JLabel jLabel19 = new JLabel();
  protected JButton jButton2 = new JButton();
  protected JButton jButton3 = new JButton();
  protected JCheckBox jCheckBox_to6Bit = new JCheckBox();

  Plot2D plot = Plot2D.getInstance();
  Plot2D plot2 = Plot2D.getInstance();

  public ContrastEnhanceFrame() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);

      jTable1 = new JTable(new String[3][2], new String[] {"Register", "Value"});
      jTable1.setValueAt("Pixel Average", 0, 0);
      jTable1.setValueAt("Frame Average", 1, 0);
      jTable1.setValueAt("avg_div", 2, 0);

      jbInit();

      plot.setFixedBounds(0, 0, 255);
      plot.setFixedBounds(1, 0, 255);
      plot.setAxisLabels("In", "Out");
      PlotBase pbase = ( (PlotWrapperInterface) plot).getOriginalPlot();
      this.jPanel11.add(pbase.getPlotPanel());

      plot2.setFixedBounds(0, 0, 255);
      plot2.setFixedBounds(1, 0, 255);
      plot2.setAxisLabels("In", "Out");
      PlotBase pbase2 = ( (PlotWrapperInterface) plot2).getOriginalPlot();
      this.jPanel12.add(pbase2.getPlotPanel());

      this.jComboBox1.addItem("0: Y32");
      this.jComboBox1.addItem("1: Y64");
      this.jComboBox2.addItem("0: Y224");
      this.jComboBox2.addItem("1: Y192");
      jComboBox1.setSelectedIndex(0);
      jComboBox2.setSelectedIndex(1);
      jSlider_lumiMedian.setValue(118);
      jSlider_contrastStr.setValue(4);
      jSlider_darkMaxAdj.setValue(15);
      jSlider_brightMaxAdj.setValue(20);
      jSlider_darkOfs.setValue(10);
      jSlider_brightOfs.setValue(15);
      jSlider_darkDR.setValue(2);
      jSlider_brightDR.setValue(2);

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
    titledBorder4 = new TitledBorder("Global");
    titledBorder3 = new TitledBorder("Turn Point");
    titledBorder2 = new TitledBorder("Bright Scene");
    titledBorder1 = new TitledBorder("Dark Scene");
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    setSize(new Dimension(1236, 646));
    setTitle("Contrast Enhance");
    jButton1.setText("Load Image");
    jButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jTextField2.setMinimumSize(new Dimension(200, 20));
    jTextField2.setPreferredSize(new Dimension(200, 20));
    jTextField2.setEditable(false);
    jPanel2.setLayout(flowLayout1);
    jPanel3.setLayout(xYLayout1);
    jCheckBox1.setText("PIX_FILTER");
    jCheckBox1.setSelected(true);
    jCheckBox1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jCheckBox1_actionPerformed(e);
      }
    });
    jLabel1.setText("DARK_TP");
    jLabel2.setText("BRIGHT_TP");
    jLabel3.setText("LUM_MEDIAN");
    jLabel4.setText("CONTRAST_STR");
    jLabel5.setText("BRIGHT_DR");
    jLabel6.setText("DARK_MAX_ADJ");
    jLabel7.setText("BRIGHT_MAX_ADJ");
    jLabel8.setText("DARK_OFS");
    jLabel9.setText("BRIGHT_OFS");
    jLabel10.setText("DARK_DR");
    jLabel11.setText("jLabel11");
    jLabel12.setText("jLabel12");
    jLabel13.setText("jLabel13");
    jLabel14.setText("jLabel14");
    jLabel15.setText("jLabel15");
    jLabel16.setText("jLabel16");
    jLabel17.setText("jLabel17");
    jLabel18.setText("jLabel18");
    jSlider_lumiMedian.setMaximum(255);
    jSlider_lumiMedian.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jSlider_darkOfs_mouseWheelMoved(e);
      }
    });
    jSlider_lumiMedian.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider_lumiMedian_stateChanged(e);
      }
    });
    jSlider_contrastStr.setMaximum(7);
    jSlider_contrastStr.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jSlider_darkOfs_mouseWheelMoved(e);
      }
    });
    jSlider_contrastStr.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider_contrastStr_stateChanged(e);
      }
    });
    jSlider_darkMaxAdj.setMaximum(63);
    jSlider_darkMaxAdj.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jSlider_darkOfs_mouseWheelMoved(e);
      }
    });
    jSlider_darkMaxAdj.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider_darkMaxAdj_stateChanged(e);
      }
    });
    jSlider_brightMaxAdj.setMaximum(63);
    jSlider_brightMaxAdj.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jSlider_darkOfs_mouseWheelMoved(e);
      }
    });
    jSlider_brightMaxAdj.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider_brightMaxAdj_stateChanged(e);
      }
    });
    jSlider_darkOfs.setMaximum(63);
    jSlider_darkOfs.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jSlider_darkOfs_mouseWheelMoved(e);
      }
    });
    jSlider_darkOfs.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider_darkOfs_stateChanged(e);
      }
    });
    jSlider_brightOfs.setMaximum(63);
    jSlider_brightOfs.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jSlider_darkOfs_mouseWheelMoved(e);
      }
    });
    jSlider_brightOfs.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider_brightOfs_stateChanged(e);
      }
    });
    jSlider_darkDR.setMaximum(15);
    jSlider_darkDR.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jSlider_darkOfs_mouseWheelMoved(e);
      }
    });
    jSlider_darkDR.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider_darkDR_stateChanged(e);
      }
    });
    jSlider_brightDR.setMaximum(15);
    jSlider_brightDR.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jSlider_darkOfs_mouseWheelMoved(e);
      }
    });
    jSlider_brightDR.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider_brightDR_stateChanged(e);
      }
    });
    jComboBox1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jComboBox1_actionPerformed(e);
      }
    });
    jComboBox2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jComboBox2_actionPerformed(e);
      }
    });
    contentPane.setMinimumSize(new Dimension(800, 800));
    jPanel3.setBorder(titledBorder4);
    jPanel3.setMinimumSize(new Dimension(380, 120));
    jPanel3.setPreferredSize(new Dimension(380, 120));
    jLabel19.setMinimumSize(new Dimension(200, 15));
    jLabel19.setPreferredSize(new Dimension(200, 15));
    jButton2.setText("Clipboard");
    jButton2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jButton3.setText("Save");
    jButton3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton3_actionPerformed(e);
      }
    });
    jCheckBox_to6Bit.setSelected(true);
    jCheckBox_to6Bit.setText("to6Bit");
    jPanel5.setLayout(borderLayout3);
    jPanel6.setLayout(xYLayout2);
    jPanel6.setBorder(titledBorder1);
    jPanel7.setLayout(verticalFlowLayout1);
    jPanel8.setBorder(titledBorder2);
    jPanel8.setLayout(xYLayout3);
    jPanel9.setLayout(xYLayout4);
    jPanel9.setBorder(titledBorder3);
    jPanel2.setMinimumSize(new Dimension(805, 400));
    jPanel2.setPreferredSize(new Dimension(805, 400));
    jScrollPane1.setPreferredSize(new Dimension(452, 102));
    jPanel10.setLayout(borderLayout4);
    jPanel10.setPreferredSize(new Dimension(452, 102));
    jPanel11.setLayout(borderLayout2);
    jPanel12.setLayout(borderLayout5);
    jPanel11.setMinimumSize(new Dimension(400, 400));
    jPanel11.setPreferredSize(new Dimension(400, 400));
    jPanel12.setPreferredSize(new Dimension(400, 400));
    jComboBox1.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jComboBox1_mouseWheelMoved(e);
      }
    });
    jComboBox2.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        jComboBox1_mouseWheelMoved(e);
      }
    });
    jPanel1.add(jCheckBox_to6Bit);
    jPanel1.add(jButton2);
    jPanel1.add(jButton1);
    jPanel1.add(jButton3);
    jPanel1.add(jTextField2);
    jPanel1.add(jLabel19);
    jPanel2.add(jPanel5);
    jPanel5.add(jPanel7, java.awt.BorderLayout.CENTER);
    jPanel2.add(jPanel11);
    jPanel2.add(jPanel12);
    jPanel3.add(jLabel3, new XYConstraints(0, 30, -1, -1));
    jPanel3.add(jCheckBox1, new XYConstraints(0, 0, -1, -1));
    jPanel3.add(jLabel4, new XYConstraints(0, 60, -1, -1));
    jPanel3.add(jSlider_lumiMedian, new XYConstraints(110, 30, -1, -1));
    jPanel3.add(jSlider_contrastStr, new XYConstraints(110, 60, -1, -1));
    jPanel3.add(jLabel11, new XYConstraints(315, 30, -1, -1));
    jPanel3.add(jLabel12, new XYConstraints(315, 60, -1, -1));

    jPanel7.add(jPanel3);
    jPanel7.add(jPanel9);

    jPanel6.add(jLabel6, new XYConstraints(0, 0, -1, -1));
    jPanel6.add(jSlider_darkMaxAdj, new XYConstraints(120, 0, -1, -1));
    jPanel6.add(jLabel13, new XYConstraints(330, 0, -1, -1));
    jPanel6.add(jSlider_darkDR, new XYConstraints(120, 30, -1, -1));
    jPanel6.add(jLabel10, new XYConstraints(0, 30, -1, -1));
    jPanel6.add(jLabel17, new XYConstraints(330, 30, -1, -1));
    jPanel8.add(jLabel7, new XYConstraints(0, 0, -1, -1));
    jPanel8.add(jSlider_brightMaxAdj, new XYConstraints(120, 0, -1, -1));
    jPanel8.add(jLabel14, new XYConstraints(330, 0, -1, -1));
    jPanel8.add(jLabel5, new XYConstraints(0, 30, -1, -1));
    jPanel8.add(jSlider_brightDR, new XYConstraints(120, 30, -1, -1));
    jPanel8.add(jLabel18, new XYConstraints(330, 30, -1, -1));
    jPanel7.add(jPanel6);
    jPanel9.add(jLabel1, new XYConstraints(0, 0, -1, -1));
    jPanel9.add(jLabel8, new XYConstraints(0, 30, -1, -1));
    jPanel9.add(jLabel15, new XYConstraints(330, 30, -1, -1));
    jPanel9.add(jSlider_darkOfs, new XYConstraints(120, 30, -1, -1));
    jPanel9.add(jComboBox1, new XYConstraints(120, 0, -1, -1));
    jPanel9.add(jLabel2, new XYConstraints(0, 60, -1, -1));
    jPanel9.add(jComboBox2, new XYConstraints(120, 60, -1, -1));
    jPanel9.add(jLabel9, new XYConstraints(0, 90, -1, -1));
    jPanel9.add(jSlider_brightOfs, new XYConstraints(120, 90, -1, -1));
    jPanel9.add(jLabel16, new XYConstraints(330, 90, -1, -1));
    jPanel7.add(jPanel8);
    jScrollPane1.getViewport().add(jTable1);
    jPanel10.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    contentPane.add(jPanel2, java.awt.BorderLayout.CENTER);
    contentPane.add(jPanel10, java.awt.BorderLayout.SOUTH);
    contentPane.add(jPanel1, java.awt.BorderLayout.NORTH);
  }

  public void jSlider_lumiMedian_stateChanged(ChangeEvent e) {
    int v = ( (JSlider) e.getSource()).getValue();
    this.jLabel11.setText(String.valueOf(v));
    ce.setLumiMedian( (short) v);
    if (ce.isDarkScene()) {
      jLabel19.setText("Dark Scene");
    }
    else {
      jLabel19.setText("Bright Scene");
    }

    refreshGUI();
  }

  public void jSlider_contrastStr_stateChanged(ChangeEvent e) {
    int v = ( (JSlider) e.getSource()).getValue();
    this.jLabel12.setText(String.valueOf(v));
    ce.setContrastStr(v);
    refreshGUI();
  }

  public void jSlider_darkMaxAdj_stateChanged(ChangeEvent e) {
    int v = ( (JSlider) e.getSource()).getValue();
    this.jLabel13.setText(String.valueOf(v));
    ce.setDarkMaxAdjust(v);
    refreshGUI();
  }

  public void jSlider_brightMaxAdj_stateChanged(ChangeEvent e) {
    int v = ( (JSlider) e.getSource()).getValue();
    this.jLabel14.setText(String.valueOf(v));
    ce.setBrightMaxAdjust(v);
    refreshGUI();
  }

  public void jSlider_darkOfs_stateChanged(ChangeEvent e) {
    int v = ( (JSlider) e.getSource()).getValue();
    this.jLabel15.setText(String.valueOf(v));
    ce.setDarkOffset(v);
    refreshGUI();
  }

  public void jSlider_brightOfs_stateChanged(ChangeEvent e) {
    int v = ( (JSlider) e.getSource()).getValue();
    this.jLabel16.setText(String.valueOf(v));
    ce.setBrightOffset(v);
    refreshGUI();
  }

  public void jSlider_darkDR_stateChanged(ChangeEvent e) {
    int v = ( (JSlider) e.getSource()).getValue();
    this.jLabel17.setText(String.valueOf(v));
    ce.setDarkDR(v);
    refreshGUI();
  }

  public void jSlider_brightDR_stateChanged(ChangeEvent e) {
    int v = ( (JSlider) e.getSource()).getValue();
    this.jLabel18.setText(String.valueOf(v));
    ce.setBrightDR(v);
    refreshGUI();
  }

  private ContrastEnhancer ce = new ContrastEnhancer();

  public void jButton1_actionPerformed(ActionEvent e) {
    this.jFileChooser1.showOpenDialog(this);
    File f = jFileChooser1.getSelectedFile();
    if (f != null) {
      String filename = f.getAbsolutePath();
      jTextField2.setText(filename);

      BufferedImage image = null;
      try {
        image = ImageUtils.loadImage(filename);
        if (this.jCheckBox_to6Bit.isSelected()) {
          image = ImageUtils.cloneBufferedImage(image);
        }

      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
      if (null == image) {
        return;
      }
      setBufferedImage(image);
    }
  }

  private double[] getHistogram(short[][] YImage) {
    int h = YImage.length;
    int w = YImage[0].length;
    double[] histogram2 = new double[h * w];
    int p;
    int index = 0;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        short v = YImage[y][x];
        p = v / 4;
        if (p >= 0 && p <= 255) {
          histogram2[index++] = p;
        }
      }
    }

    return histogram2;
  }

  private double[] histogram = null;
  private BufferedImage bufferedImage;
  private void setBufferedImage(BufferedImage image) {
    this.bufferedImage = image;
    short[][] YImage = ContrastEnhancer.getYImage(image);
//    histogram = getHistogram(YImage);
    ce.setYImage(YImage);
    if (ce.isDarkScene()) {
      jLabel19.setText("Dark Scene");
    }
    else {
      jLabel19.setText("Bright Scene");
    }
    refreshGUI();
  }

  public void jCheckBox1_actionPerformed(ActionEvent e) {
    ce.setPixelFilter( ( (JCheckBox) e.getSource()).isSelected());
    refreshGUI();
  }

  public void jComboBox1_actionPerformed(ActionEvent e) {
    ce.setDarkTurnPoint( ( (JComboBox) e.getSource()).getSelectedIndex());
    refreshGUI();
  }

  public void jComboBox2_actionPerformed(ActionEvent e) {
    ce.setBrightTurnPoint( ( (JComboBox) e.getSource()).getSelectedIndex());
    refreshGUI();
  }

  private double[] getLightnessCurve(int[] mappingCurve) {
    int size = mappingCurve.length;
    double[] lightnessCurve = new double[size];
    RGB.ColorSpace colorspace = RGB.ColorSpace.sRGB;
    CIEXYZ refWhite = colorspace.getReferenceWhiteXYZ();
    RGB rgb = new RGB(RGB.ColorSpace.sRGB_gamma22, RGB.MaxValue.Int8Bit);
    for (int x = 0; x < size; x++) {
      int v = mappingCurve[x];
      //v=.25r+.5g+.25b
      rgb.R = rgb.G = rgb.B = v;
      CIEXYZ XYZ = rgb.toXYZ();
      CIELab Lab = new CIELab(XYZ, refWhite);
      lightnessCurve[x] = Lab.L;
    }
    return lightnessCurve;
  }

  private double[] originalLightnessCurve = getLightnessCurve(IntArray.buildX(0,
      255, 256));
  private void refreshGUI() {
    if (ce.isYImageEmpty()) {
      return;
    }

    if (null != histogram) {
//      plot.addHistogramPlot("hist", Color.blue, histogram, 255);
    }

    int[] curve = ce.getMappingCurve();

    //==========================================================================
    plot.removeAllPlots();
    for (int x = 0; x < curve.length; x++) {
      int v = curve[x];
      plot.addCacheScatterLinePlot("Mapping Curve", Color.blue, x, v);
    }
    plot.addLinePlot("Original", Color.red, 0, 0, 255, 255);

    plot.drawCachePlot();
    plot.setFixedBounds(0, 0, 255);
    plot.setFixedBounds(1, 0, 255);
    //==========================================================================
    double[] lightnessCurve = getLightnessCurve(curve);
//    if(null ==originalLightnessCurve) {
//    originalLightnessCurve = getLightnessCurve(originalCurve);
//    }
    plot2.removeAllPlots();
    for (int x = 0; x < lightnessCurve.length; x++) {
      double v = lightnessCurve[x];
      plot2.addCacheScatterLinePlot("Lightness Curve", Color.blue, x, v);
      double v2 = originalLightnessCurve[x];
      plot2.addCacheScatterLinePlot("Original Lightness Curve", Color.red, x,
                                    v2);
    }
//    plot2.addLinePlot("Original", Color.red, 0, 0, 255, 100);

    plot2.drawCachePlot();
    plot2.setFixedBounds(0, 0, 255);
    plot2.setFixedBounds(1, 0, 100);

    //==========================================================================
    jTable1.setValueAt(String.valueOf(ce.getPixelAverage()), 0, 1);
    jTable1.setValueAt(String.valueOf(ce.getFrameAverage()), 1, 1);
    jTable1.setValueAt(String.valueOf(ce.getAverageDiv()), 2, 1);
    //==========================================================================
  }

  public void jButton2_actionPerformed(ActionEvent e) {
    Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().
        getSystemClipboard();
    Transferable clipData = clipboard.getContents(clipboard);
    Image img = null;
    if (clipData != null) {
      if (clipData.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.
                                         imageFlavor)) {
        // 從 clipboard 來的應該是 BufferedImage，所以也可 cast 成 BufferedImage
        try {
          img = (Image) clipData.getTransferData(java.awt.
                                                 datatransfer.DataFlavor.
                                                 imageFlavor);
          if (null != img) {
            BufferedImage bi = ImageUtils.cloneBufferedImage( (BufferedImage)
                img);
            if (this.jCheckBox_to6Bit.isSelected()) {
              bi = ImageUtils.cloneBufferedImage(bi);
            }
            setBufferedImage(bi);
          }
        }
        catch (IOException ex) {
          ex.printStackTrace();
        }
        catch (UnsupportedFlavorException ex) {
          ex.printStackTrace();
        }
      }

    }

  }

  public void jComboBox1_mouseWheelMoved(MouseWheelEvent e) {
    JComboBox combobox = (JComboBox) e.getSource();
    int count = combobox.getItemCount();
    int i = combobox.getSelectedIndex() + e.getWheelRotation();
    i = i < 0 ? 0 : i;
    i = i >= count ? count - 1 : i;
    combobox.setSelectedIndex(i);
  }

  public void jSlider_darkOfs_mouseWheelMoved(MouseWheelEvent e) {

    JSlider slider = (JSlider) e.getSource();
    int count = slider.getMaximum();
    int i = slider.getValue() + e.getWheelRotation();
    i = i < 0 ? 0 : i;
    i = i > count ? count : i;
    slider.setValue(i);
  }

  public static void main(String[] args) {
    GUIUtils.runAsApplication(new ContrastEnhanceFrame(), false);
  }

  public void jButton3_actionPerformed(ActionEvent e) {

  }
}
