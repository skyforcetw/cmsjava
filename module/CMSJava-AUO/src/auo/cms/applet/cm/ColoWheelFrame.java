package auo.cms.applet.cm;

import java.text.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import auo.cms.cm.*;
import com.borland.jbcl.layout.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.ui.*;
import shu.math.array.*;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import shu.image.ImageUtils;
import java.awt.Dimension;

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
public class ColoWheelFrame
    extends JFrame implements TableModelListener {
  public ColoWheelFrame() throws HeadlessException {
    super();
    setColorMatrixTable(new double[][] { {1, 0, 0}, {0, 1, 0}, {0, 0, 1}
    });
    this.jPanel2.setVisible(false);
    this.jPanel6.setVisible(false);
    this.jPanel9.setVisible(false);
    this.jPanel7.setVisible(false);
    jPanel4.setVisible(false);
    this.jRadioButton_Gradient.setVisible(false);
    this.jRadioButton_WithoutSaturation.setVisible(false);
    this.jRadioButton_WithSaturation.setVisible(false);
    jRadioButton_ColoWheel.setVisible(false);
//    jToggleButton1.setVisible(false);
    try {

      jbInit();
      jButton_LoadImage_actionPerformed(null);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  public ColoWheelFrame(GraphicsConfiguration gc) {
    super(gc);
  }

  public ColoWheelFrame(String title) throws HeadlessException {
    super(title);
  }

  public ColoWheelFrame(String title, GraphicsConfiguration gc) {
    super(title, gc);
  }

  private void jbInit() throws Exception {
    titledBorder1 = new TitledBorder("Pattern");
    jPanel1.setLayout(verticalFlowLayout1);
    jLabel1.setMaximumSize(new Dimension(10, 15));
    jLabel1.setMinimumSize(new Dimension(10, 15));
    jLabel1.setPreferredSize(new Dimension(10, 15));
    jLabel1.setText("1.0");
    jLabel2.setText("1.0");
    jLabel3.setText("1.0");
    jSlider1.setPreferredSize(new Dimension(125, 25));
    jSlider1.setValue(25);
    jSlider2.setPreferredSize(new Dimension(125, 25));
    jSlider2.setValue(25);
    jSlider3.setPreferredSize(new Dimension(125, 25));
    jSlider3.setValue(25);

    jSlider1.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider1_stateChanged(e);
      }
    });
    jSlider2.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider2_stateChanged(e);
      }
    });
    jSlider3.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider3_stateChanged(e);
      }
    });
    jButton1.setText("Reset");
    jButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jToggleButton1.setText("Switch");
    jToggleButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jToggleButton1_actionPerformed(e);
      }
    });
    jRadioButton1.setSelected(true);
    jRadioButton1.setText("None");
    jRadioButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButton1_actionPerformed(e);
      }
    });
    jRadioButton2.setText("Half");
    jRadioButton2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButton2_actionPerformed(e);
      }
    });
    jRadioButton3.setText("Formula");
    jRadioButton3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButton3_actionPerformed(e);
      }
    });
    jRadioButton4.setText("Optimize");
    jRadioButton4.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButton4_actionPerformed(e);
      }
    });
    jPanel2.setLayout(verticalFlowLayout2);
    ditherCanvas1.setDoubleBuffered(true);
    ditherCanvas1.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        ditherCanvas1_mouseMoved(e);
      }
    });
    jTable1.setMaximumSize(new Dimension(225, 48));
    jTable1.setMinimumSize(new Dimension(225, 48));
    jTable1.setRowSelectionAllowed(false);
    this.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        this_componentResized(e);
      }
    }); jToggleButton2.setText("Draw CMY Black");
    jToggleButton2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jToggleButton2_actionPerformed(e);
      }
    });
    jLabel4.setText("B");
    jLabel5.setText("G");
    jLabel6.setText("R");
    jPanel4.setLayout(flowLayout1);
    jRadioButton_WithoutSaturation.setText("w/o Saturation");
    jRadioButton_WithoutSaturation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButton_WithoutSaturation_actionPerformed(e);
      }
    });
    jRadioButton_WithSaturation.setText("w/Saturation");
    jRadioButton_WithSaturation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButton_WithSaturation_actionPerformed(e);
      }
    });
    jRadioButton_ColoWheel.setSelected(false);
    jRadioButton_ColoWheel.setText("ColorWheel");
    jRadioButton_ColoWheel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButton_ColoWheel_actionPerformed(e);
      }
    });
    jPanel2.setBorder(BorderFactory.createEtchedBorder());
    jPanel5.setBorder(border2);
    jPanel5.setLayout(verticalFlowLayout3);
    jPanel6.setLayout(gridBagLayout1);
    jRadioButton5.setText("Optimize+Hue");
    jRadioButton5.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButton5_actionPerformed(e);
      }
    });
    jLabel7.setText("R Gain");
    jLabel8.setText("G Gain");
    jLabel9.setText("B Gain");
    jPanel6.setBorder(BorderFactory.createEtchedBorder());
    jPanel9.setLayout(gridBagLayout2);
    jLabel10.setText("1.0");
    jLabel11.setText("C Gain");
    jLabel12.setText("M Gain");
    jLabel13.setText("1.0");
    jLabel14.setText("Y Gain");
    jLabel15.setText("1.0");
    jPanel9.setBorder(BorderFactory.createEtchedBorder());
    jSlider4.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider4_stateChanged(e);
      }
    });
    jSlider5.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider5_stateChanged(e);
      }
    });
    jSlider6.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider6_stateChanged(e);
      }
    });
    jRadioButton_Gradient.setText("Gradient");
    jRadioButton_Gradient.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButton6_actionPerformed(e);
      }
    });
    jButton2.setText("Reset");
    jButton2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jCheckBox1.setEnabled(false);
    jCheckBox1.setSelected(true);
    jCheckBox1.setText("Manual");
    jCheckBox1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jCheckBox1_actionPerformed(e);
      }
    });
    jPanel3.setLayout(verticalFlowLayout4);
    jRadioButton_Custom.setSelected(true);
    jRadioButton_Custom.setText("Custom");
    jRadioButton_Custom.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButton_Custom_actionPerformed(e);
      }
    });
    jButton_LoadImage.setText("Load Image");
    jButton_LoadImage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_LoadImage_actionPerformed(e);
      }
    });
    jTextField_BOffset.setMinimumSize(new Dimension(40, 20));
    jTextField_BOffset.setPreferredSize(new Dimension(40, 20));
    jTextField_BOffset.setText("0");
    jTextField_BOffset.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTextField_BOffset_actionPerformed(e);
      }
    });
    jTextField_GOffset.setMinimumSize(new Dimension(40, 20));
    jTextField_GOffset.setPreferredSize(new Dimension(40, 20));
    jTextField_GOffset.setText("0");
    jTextField_GOffset.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTextField_GOffset_actionPerformed(e);
      }
    });
    jTextField_ROffset.setMinimumSize(new Dimension(40, 20));
    jTextField_ROffset.setPreferredSize(new Dimension(40, 20));
    jTextField_ROffset.setText("0");
    jTextField_ROffset.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTextField_ROffset_actionPerformed(e);
      }
    });
    jLabel16.setText("R");
    jLabel17.setText("G");
    jLabel18.setText("B");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.getContentPane().add(jPanel1, java.awt.BorderLayout.EAST);
    jPanel7.add(jButton1);
    jPanel7.add(jToggleButton1);
    jPanel7.add(jToggleButton2);

    jPanel1.add(jPanel7);
    jPanel1.add(jPanel4);
    jPanel1.add(jPanel6);
    jPanel1.add(jPanel9);
    jPanel4.add(jLabel6);
    jPanel4.add(jLabel5);
    jPanel4.add(jLabel4);

    this.getContentPane().add(ditherCanvas1, java.awt.BorderLayout.CENTER);
    jPanel1.add(jPanel2);
    jPanel2.add(jRadioButton1);
    jPanel2.add(jRadioButton2);
    jPanel2.add(jRadioButton3);
    jPanel2.add(jRadioButton4);
    jPanel2.add(jRadioButton5);
    jPanel1.add(jPanel5);
    jPanel5.add(jRadioButton_ColoWheel);
    jPanel5.add(jRadioButton_WithSaturation);
    jPanel5.add(jRadioButton_WithoutSaturation);
    jPanel5.add(jRadioButton_Gradient);
    jPanel5.add(jRadioButton_Custom);
    jPanel5.add(jButton_LoadImage);
    jPanel1.add(jPanel3);
    buttonGroup1.add(jRadioButton1);
    buttonGroup1.add(jRadioButton2);
    buttonGroup1.add(jRadioButton3);
    buttonGroup1.add(jRadioButton4);
    buttonGroup1.add(jRadioButton_WithSaturation);
    buttonGroup1.add(jRadioButton5);
    buttonGroup1.add(jRadioButton_Gradient);
    jPanel3.add(jCheckBox1);
    jPanel3.add(jTable1);
    jPanel3.add(jPanel10);
    jPanel10.add(jLabel16);
    jPanel10.add(jTextField_ROffset);
    jPanel10.add(jLabel17);
    jPanel10.add(jTextField_GOffset);
    jPanel10.add(jLabel18);
    jPanel10.add(jTextField_BOffset);

    buttonGroup2.add(jRadioButton_ColoWheel);
    buttonGroup2.add(jRadioButton_WithSaturation);
    buttonGroup2.add(jRadioButton_WithoutSaturation);
    buttonGroup2.add(jRadioButton_Gradient);
    buttonGroup2.add(jRadioButton_Custom);
    jPanel6.add(jSlider1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.BOTH,
                                                 new Insets(0, 5, 0, 5), 47, 0));
    jPanel6.add(jSlider2, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.BOTH,
                                                 new Insets(0, 5, 0, 5), 47, 0));
    jPanel6.add(jSlider3, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.BOTH,
                                                 new Insets(0, 5, 5, 5), 47, 0));
    jPanel6.add(jLabel7, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel6.add(jLabel8, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel6.add(jLabel9, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    jPanel6.add(jLabel1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 5, 0, 5), 50, 0));
    jPanel6.add(jLabel2, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 5, 0, 5), 50, 0));
    jPanel6.add(jLabel3, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 5, 0, 5), 50, 0));
    jPanel1.add(jPanel8);
    jPanel9.add(jLabel11, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel9.add(jLabel12, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel9.add(jLabel10, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel9.add(jSlider4, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel9.add(jLabel13, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel9.add(jSlider5, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel9.add(jLabel14, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel9.add(jLabel15, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel9.add(jSlider6, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    jPanel9.add(jButton2, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    TableModel model = jTable1.getModel();
    model.addTableModelListener(this);

    this.setSize(1200, 900);
    this.setVisible(true);
    updateImage();

  }

  protected JPanel jPanel1 = new JPanel();
  protected DitherCanvas ditherCanvas1 = new DitherCanvas();
  protected JSlider jSlider1 = new JSlider();
  protected JSlider jSlider2 = new JSlider();
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JSlider jSlider3 = new JSlider();
  protected JLabel jLabel1 = new JLabel();
  protected JLabel jLabel2 = new JLabel();
  protected JLabel jLabel3 = new JLabel();
  public void jSlider1_stateChanged(ChangeEvent e) {
    JSlider source = (JSlider) e.getSource();
    if (!source.getValueIsAdjusting()) {
      int v = (int) source.getValue();
      rgain = v <= 25 ? v / 25. : (v - 25) / 75. + 1;
      jLabel1.setText(df.format(rgain));
      updateImage();
    }
  }

  double rgain = 1, ggain = 1, bgain = 1;
  double cgain = 1, mgain = 1, ygain = 1;
  public void jSlider2_stateChanged(ChangeEvent e) {
    JSlider source = (JSlider) e.getSource();
    if (!source.getValueIsAdjusting()) {
      int v = (int) source.getValue();
      ggain = v <= 25 ? v / 25. : (v - 25) / 75. + 1;
      jLabel2.setText(df.format(ggain));
      updateImage();
    }
  }

  public void jSlider3_stateChanged(ChangeEvent e) {
    JSlider source = (JSlider) e.getSource();
    if (!source.getValueIsAdjusting()) {
      int v = (int) source.getValue();
      bgain = v <= 25 ? v / 25. : (v - 25) / 75. + 1;
      jLabel3.setText(df.format(bgain));
      updateImage();
    }
  }

  void updateImage() {
    BufferedImage img = null;

    if (false == hasOriginal) {
      double[][] originalcm = getColorMatrix(1, 1, 1, 1, 1, 1, true);
      originalImage = getBufferedImage(originalImage, originalcm);
      hasOriginal = true;
    }

    if (toggle) {
      img = originalImage;
    }
    else {
//        HSBImage = ImageUtils.cloneBufferedImage(originalImage);
      img = ImageUtils.cloneBufferedImage(originalImage);
      if (!toggled) {
        double[][] cm = getColorMatrix(rgain, ggain, bgain, cgain, mgain, ygain, false);
        setColorMatrixTable(cm);
        img = getBufferedImage(img, cm);
        HSBImage = img;

      }

    }

    ditherCanvas1.setBufferedImage(img);
  }

  BufferedImage getBufferedImage(BufferedImage img, double[][] cm) {
    switch (pattern) {
      case ColorWheel:
        img = calculateHSBImage(img, cm);
        break;
      case Saturation:
        img = calculateHSB2Image(true, img, cm);
        break;
      case WithoutSaturation:
        img = calculateHSB2Image(false, img, cm);
        break;
      case Gradient:
        img = calculateGradientImage(img, cm);
        break;
      case Custom:
        img = calculateImage(img, cm);
    }
    return img;
  }

  private boolean hasOriginal = false;
  private BufferedImage HSBImage = null;
  private BufferedImage originalImage = null;
  protected javax.swing.JButton jButton1 = new JButton();

  BufferedImage calculateImage(
      BufferedImage HSBImage, double[][] cm) {
    int width = HSBImage.getWidth();
    int height = HSBImage.getHeight();
    WritableRaster raster = HSBImage.getRaster();
    int[] rgbValues = new int[3];
    RGB color = new RGB();
    double roffset = Double.valueOf(this.jTextField_ROffset.getText());
    double goffset = Double.valueOf(this.jTextField_GOffset.getText());
    double boffset = Double.valueOf(this.jTextField_BOffset.getText());

    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        raster.getPixel(w, h, rgbValues);
//        doubleRGBValues[0]=rgbValues/255.;
        color.setValues(IntArray.toDoubleArray(rgbValues), RGB.MaxValue.Int8Bit);
        double[] doubleRGBValues = color.getValues(new double[3],
            RGB.MaxValue.Double1);
        int[] intRGBValues = getRGBValues(doubleRGBValues, cm);
        intRGBValues[0] += roffset;
        intRGBValues[1] += goffset;
        intRGBValues[2] += boffset;
        for (int x = 0; x < 3; x++) {
          intRGBValues[x] = (intRGBValues[x] < 0) ? 0 : intRGBValues[x];
          intRGBValues[x] = (intRGBValues[x] > 255) ? 255 : intRGBValues[x];
        }
        raster.setPixel(w, h, intRGBValues);
      }
    }
    return HSBImage;
  }

  BufferedImage calculateGradientImage(
      BufferedImage HSBImage, double[][] cm) {
    Dimension size = ditherCanvas1.getSize();
    int height = size.height;
    int width = size.width;
    int singleHeight = height / 7;
    int singleWidth = width / 256;
    RGB.Channel[] channels = RGB.Channel.RGBYMCWChannel;
    Graphics g = HSBImage.getGraphics();
    RGB color = new RGB();
    g.setColor(Color.black);
    g.fillRect(0, 0, width, height);

    for (int x = 0; x < 7; x++) {
      int x0 = x * singleHeight;
      for (int y = 0; y < 256; y++) {
        color.setColorBlack();
        RGB.Channel ch = channels[x];
        color.setValue(ch, y, RGB.MaxValue.Int8Bit);
        double[] rgbValues = color.getValues(new double[3],
                                             RGB.MaxValue.Double1);
        int[] intRGBValues = getRGBValues(rgbValues, cm);
        rgbValues = IntArray.toDoubleArray(intRGBValues);
        color.setValues(rgbValues, RGB.MaxValue.Int8Bit);

        g.setColor(color.getColor());
        int y0 = singleWidth * y;
        g.fillRect(y0, x0, singleWidth, singleHeight);
      }
    }

    return HSBImage;
  }

  BufferedImage calculateHSBImage(
      BufferedImage HSBImage, double[][] cm) {
    Dimension size = ditherCanvas1.getSize();
    int height = size.height;
    int width = size.width;

    if (HSBImage == null) {
      HSBImage = new BufferedImage(width,
                                   height, BufferedImage.TYPE_INT_RGB);
    }

    int half = (height / 2) - 1;

    double[] hsbValues = new double[3];
    hsbValues[2] = 100;
    double[] LabValues = new double[3];
    LabValues[1] = half;
    double maxC = CIELCh.fromLabValues(LabValues)[1];
    int xOriginal = (width - height) / 2;
    int xEnd = height + xOriginal;
    int xHalf = half + xOriginal;

    Graphics g = HSBImage.getGraphics();
    g.setColor(Color.black);
    g.fillRect(0, 0, width, height);

    for (int x = xOriginal; x < xEnd; x++) {
      for (int y = 0; y < height; y++) {
        int a = x - xHalf;
        int b = - (y - half);
        LabValues[1] = a;
        LabValues[2] = b;
        double[] LChValues = CIELCh.fromLabValues(LabValues);
        if (LChValues[1] <= maxC) {

          double s = (LChValues[1] / maxC) * 100;

          hsbValues[0] = LChValues[2];
          hsbValues[1] = s;

          double[] rgbValues = HSV.toRGBValues(hsbValues);
          int[] codeRGBValues = getRGBValues(rgbValues, cm);
          int codeR = codeRGBValues[0];
          int codeG = codeRGBValues[1];
          int codeB = codeRGBValues[2];

          HSBImage.setRGB(x, y, ( (codeR << 16) | (codeG << 8) | codeB));

        }
      }
    }
    return HSBImage;
  }

  BufferedImage calculateHSB2Image(boolean saturationChange,
                                   BufferedImage HSB2Image, double[][] cm) {
    Dimension size = ditherCanvas1.getSize();
    int height = size.height;
    int width = size.width;

    if (HSB2Image == null) {
      HSB2Image = new BufferedImage(width,
                                    height, BufferedImage.TYPE_INT_RGB);
    }

    double[] hsbValues = new double[3];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {

        hsbValues[0] = ( ( (double) x) / width) * 360;
        if (saturationChange) {
          hsbValues[1] = ( ( (double) y) / height) * 100;
          hsbValues[2] = 100;
        }
        else {
          hsbValues[2] = ( ( (double) y) / height) * 100;
          hsbValues[1] = 100;
        }

        double[] rgbValues = HSV.toRGBValues(hsbValues);
        int[] codeRGBValues = getRGBValues(rgbValues, cm);
        int codeR = codeRGBValues[0];
        int codeG = codeRGBValues[1];
        int codeB = codeRGBValues[2];
        HSB2Image.setRGB(x, y, ( (codeR << 16) | (codeG << 8) | codeB));
      }
    }

    return HSB2Image;
  }

  int[] getRGBValues(double[] rgbValues, double[][] cm) {
    if (!toggle) {
      rgbValues = DoubleArray.times(cm, rgbValues);
      rgbValues = RGB.rationalize(rgbValues, RGB.MaxValue.Double1);
    }

    int codeR = (int) (rgbValues[0] * 255);
    int codeG = (int) (rgbValues[1] * 255);
    int codeB = (int) (rgbValues[2] * 255);
    if (drawCMYBlack &&
        ( (codeR == codeG && codeR != 0 /*&& codeB == 0*/) ||
         (codeR == codeB && codeR != 0 /*&& codeG == 0*/) ||
         (codeG == codeB && codeG != 0 /*&& codeR == 0*/))) {
      codeR = codeG = codeB = 0;
    }
    return new int[] {
        codeR, codeG, codeB};
  }

  boolean drawCMYBlack = false;
  DecimalFormat df = new DecimalFormat("##.###");
  boolean autoUpdate = false;
  void setColorMatrixTable(double[][] colorMatrix) {

    autoUpdate = true;
    for (int x = 0; x < colorMatrix.length; x++) {
      for (int y = 0; y < colorMatrix[x].length; y++) {
        double v = colorMatrix[x][y];
        jTable1.setValueAt(df.format(v), x, y);
      }
    }
    autoUpdate = false;

  }

  double[][] getColorMatrix(double rgain, double ggain, double bgain,
                            double cgain, double mgain, double ygain,
                            boolean original) {
    double[][] cm = new double[3][3];

    if (this.jCheckBox1.isSelected() && original == false) {
      for (int x = 0; x < 3; x++) {
        for (int y = 0; y < 3; y++) {
          cm[x][y] = Double.valueOf( (String)this.jTable1.getValueAt(x, y));
        }
      }
      return cm;
    }

    cm[0][0] = rgain;
    cm[1][1] = ggain;
    cm[2][2] = bgain;
    switch (negative) {
      case None:
        break;
      case Half:
        double rneg = (1 - rgain) / 2.;
        double gneg = (1 - ggain) / 2.;
        double bneg = (1 - bgain) / 2.;
        cm[0][1] = rneg;
        cm[0][2] = rneg;
        cm[1][0] = gneg;
        cm[1][2] = gneg;
        cm[2][0] = bneg;
        cm[2][1] = bneg;
        break;
      case Formula:
        double[][] matrix = new double[][] {
            {
            0, 1, 1}, {
            1, 0, 1}, {
            1, 1, 0}
        };
        double[] ans = new double[] {
            1 - rgain, 1 - ggain, 1 - bgain};
        double[] negative = DoubleArray.times(DoubleArray.inverse(matrix), ans);
        cm[1][0] = negative[0];
        cm[2][0] = negative[0];
        cm[0][1] = negative[1];
        cm[2][1] = negative[1];
        cm[0][2] = negative[2];
        cm[1][2] = negative[2];
        break;
      case Optimize:
        cm = ColorMatrixCalculator2.getOptimizedColorMatrix(rgain, ggain, bgain);
        break;
      case OptimizeHue:
        cm = ColorMatrixCalculator2.getOptimizedColorMatrixWithCMY(rgain, ggain,
            bgain, cgain, mgain, ygain);
        break;
    }

    return cm;
  }

  public static void main(String[] args) {
    ColoWheelFrame frame = new ColoWheelFrame();
    frame.setVisible(true);
  }

  public void jButton1_actionPerformed(ActionEvent e) {
    jSlider1.setValue(25);
    jSlider2.setValue(25);
    jSlider3.setValue(25);
    jSlider4.setValue(50);
    jSlider5.setValue(50);
    jSlider6.setValue(50);
    this.jCheckBox1.setSelected(false);
  }

  int oldRValue, oldGValue, oldBValue;
  protected javax.swing.JToggleButton jToggleButton1 = new JToggleButton();
  protected javax.swing.ButtonGroup buttonGroup1 = new ButtonGroup();
  protected javax.swing.JRadioButton jRadioButton1 = new JRadioButton();
  protected javax.swing.JRadioButton jRadioButton2 = new JRadioButton();
  protected javax.swing.JRadioButton jRadioButton3 = new JRadioButton();
  protected javax.swing.JPanel jPanel2 = new JPanel();
  boolean toggle = false;
  boolean toggled = false;
  public void jToggleButton1_actionPerformed(ActionEvent e) {
    toggle = jToggleButton1.isSelected();
    if (toggle) {
      toggled = true;
    }
    updateImage();
    if (!toggle) {
      toggled = false;
    }

  }

  enum Negative {
    None, Half, Optimize, Formula, OptimizeHue
  }

  enum Pattern {
    ColorWheel, Saturation, WithoutSaturation, Gradient, Custom
  }

  Negative negative = Negative.None;
  Pattern pattern = Pattern.ColorWheel;
  protected javax.swing.JRadioButton jRadioButton4 = new JRadioButton();
  protected VerticalFlowLayout verticalFlowLayout2 = new VerticalFlowLayout();
  protected javax.swing.JPanel jPanel3 = new JPanel();
  protected javax.swing.JTable jTable1 = new JTable(3, 3);
  protected javax.swing.JToggleButton jToggleButton2 = new JToggleButton();
  protected javax.swing.JPanel jPanel4 = new JPanel();
  protected javax.swing.JLabel jLabel4 = new JLabel();
  protected javax.swing.JLabel jLabel5 = new JLabel();
  protected javax.swing.JLabel jLabel6 = new JLabel();
  protected java.awt.FlowLayout flowLayout1 = new FlowLayout();
  protected javax.swing.JPanel jPanel5 = new JPanel();
  protected javax.swing.ButtonGroup buttonGroup2 = new ButtonGroup();
  protected javax.swing.JRadioButton jRadioButton_WithoutSaturation = new
      JRadioButton();
  protected javax.swing.JRadioButton jRadioButton_WithSaturation = new
      JRadioButton();
  protected javax.swing.JRadioButton jRadioButton_ColoWheel = new JRadioButton();
  protected VerticalFlowLayout verticalFlowLayout3 = new VerticalFlowLayout();
  protected javax.swing.JPanel jPanel6 = new JPanel();
  protected javax.swing.JPanel jPanel7 = new JPanel();
  protected javax.swing.JRadioButton jRadioButton5 = new JRadioButton();
  protected java.awt.GridBagLayout gridBagLayout1 = new GridBagLayout();
  protected javax.swing.JLabel jLabel7 = new JLabel();
  protected javax.swing.JLabel jLabel8 = new JLabel();
  protected javax.swing.JLabel jLabel9 = new JLabel();
  protected javax.swing.JPanel jPanel8 = new JPanel();
  protected javax.swing.JPanel jPanel9 = new JPanel();
  protected java.awt.GridBagLayout gridBagLayout2 = new GridBagLayout();
  protected javax.swing.JLabel jLabel10 = new JLabel();
  protected javax.swing.JLabel jLabel11 = new JLabel();
  protected javax.swing.JSlider jSlider4 = new JSlider();
  protected javax.swing.JLabel jLabel12 = new JLabel();
  protected javax.swing.JLabel jLabel13 = new JLabel();
  protected javax.swing.JSlider jSlider5 = new JSlider();
  protected javax.swing.JLabel jLabel14 = new JLabel();
  protected javax.swing.JLabel jLabel15 = new JLabel();
  protected javax.swing.JSlider jSlider6 = new JSlider();
  protected JRadioButton jRadioButton_Gradient = new JRadioButton();
  protected JButton jButton2 = new JButton();
  protected JCheckBox jCheckBox1 = new JCheckBox();
  protected VerticalFlowLayout verticalFlowLayout4 = new VerticalFlowLayout();
  protected TitledBorder titledBorder1 = new TitledBorder("");
  protected Border border1 = BorderFactory.createEtchedBorder(Color.white,
      new Color(158, 158, 158));
  protected Border border2 = new TitledBorder(border1, "Pattern");
  protected JRadioButton jRadioButton_Custom = new JRadioButton();
  protected JButton jButton_LoadImage = new JButton();
  protected JFileChooser jFileChooser1 = new JFileChooser();
  protected JPanel jPanel10 = new JPanel();
  protected JTextField jTextField_BOffset = new JTextField();
  protected JTextField jTextField_GOffset = new JTextField();
  protected JTextField jTextField_ROffset = new JTextField();
  protected JLabel jLabel16 = new JLabel();
  protected JLabel jLabel17 = new JLabel();
  protected JLabel jLabel18 = new JLabel();
  public void jRadioButton1_actionPerformed(ActionEvent e) {
    negative = Negative.None;
    updateImage();
  }

  public void jRadioButton2_actionPerformed(ActionEvent e) {
    negative = Negative.Half;
    updateImage();
  }

  public void jRadioButton3_actionPerformed(ActionEvent e) {
    negative = Negative.Formula;
    updateImage();
  }

  public void jRadioButton4_actionPerformed(ActionEvent e) {
    negative = Negative.Optimize;
    updateImage();
  }

  public void this_componentResized(ComponentEvent e) {
    this.updateImage();
  }

  public void jToggleButton2_actionPerformed(ActionEvent e) {
    boolean selected = this.jToggleButton2.isSelected();
    drawCMYBlack = selected;
    updateImage();
  }

  public void ditherCanvas1_mouseMoved(MouseEvent e) {
    if (pattern == Pattern.Custom) {
      return;
    }
    int x = e.getX();
    int y = e.getY();
    BufferedImage img = ditherCanvas1.getBufferedImage();
    int[] rgbValues = img.getRaster().getPixel(x, y, new int[3]);
    this.jLabel6.setText(String.valueOf(rgbValues[0]));
    this.jLabel5.setText(String.valueOf(rgbValues[1]));
    this.jLabel4.setText(String.valueOf(rgbValues[2]));
  }

  public void jRadioButton_ColoWheel_actionPerformed(ActionEvent e) {
    pattern = Pattern.ColorWheel;
    hasOriginal = false;
    this.jToggleButton1.setSelected(false);
    updateImage();
  }

  public void jRadioButton_WithSaturation_actionPerformed(ActionEvent e) {
    pattern = Pattern.Saturation;
    hasOriginal = false;
    this.jToggleButton1.setSelected(false);
    updateImage();
  }

  public void jRadioButton_WithoutSaturation_actionPerformed(ActionEvent e) {
    pattern = Pattern.WithoutSaturation;
    hasOriginal = false;
    this.jToggleButton1.setSelected(false);
    updateImage();
  }

  public void jRadioButton5_actionPerformed(ActionEvent e) {
    negative = Negative.OptimizeHue;
    updateImage();
  }

  public void jSlider4_stateChanged(ChangeEvent e) {
    JSlider source = (JSlider) e.getSource();
    if (!source.getValueIsAdjusting()) {
      int v = (int) source.getValue();
      cgain = v <= 50 ? v / 50. : (v - 50) / 50. + 1;
      jLabel10.setText(df.format(cgain));
      updateImage();
    }
  }

  public void jSlider5_stateChanged(ChangeEvent e) {
    JSlider source = (JSlider) e.getSource();
    if (!source.getValueIsAdjusting()) {
      int v = (int) source.getValue();
      mgain = v <= 50 ? v / 50. : (v - 50) / 50. + 1;
      jLabel13.setText(df.format(mgain));
      updateImage();
    }
  }

  public void jSlider6_stateChanged(ChangeEvent e) {
    JSlider source = (JSlider) e.getSource();
    if (!source.getValueIsAdjusting()) {
      int v = (int) source.getValue();
      ygain = v <= 50 ? v / 50. : (v - 50) / 50. + 1;
      jLabel15.setText(df.format(ygain));
      updateImage();
    }
  }

  public void jRadioButton6_actionPerformed(ActionEvent e) {
    pattern = Pattern.Gradient;
    hasOriginal = false;
    this.jToggleButton1.setSelected(false);
    updateImage();
  }

  public void jButton2_actionPerformed(ActionEvent e) {
    jSlider4.setValue(50);
    jSlider5.setValue(50);
    jSlider6.setValue(50);
  }

  /**
   * This fine grain notification tells listeners the exact range of cells,
   * rows, or columns that changed.
   *
   * @param e TableModelEvent
   */
  public void tableChanged(TableModelEvent e) {
    if (!autoUpdate) {
      this.updateImage();
    }
  }

  public void jCheckBox1_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void jRadioButton_Custom_actionPerformed(ActionEvent e) {
    this.jButton_LoadImage.setEnabled(true);
  }

  public void jButton_LoadImage_actionPerformed(ActionEvent e) {
    this.jFileChooser1.showOpenDialog(this);
    File file = this.jFileChooser1.getSelectedFile();
    if (file != null) {
      try {
        originalImage = ImageUtils.loadImage(file.getAbsolutePath());
        HSBImage = ImageUtils.cloneBufferedImage(originalImage);
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
      hasOriginal = false;

      pattern = Pattern.Custom;
      hasOriginal = false;
      this.jToggleButton1.setSelected(false);
      updateImage();

    }
  }

  public void jTextField_ROffset_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void jTextField_GOffset_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void jTextField_BOffset_actionPerformed(ActionEvent e) {
    this.updateImage();
  }
}
