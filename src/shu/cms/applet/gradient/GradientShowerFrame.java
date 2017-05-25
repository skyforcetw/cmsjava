package shu.cms.applet.gradient;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import shu.cms.colorspace.ColorSpace;
import shu.cms.colorspace.depend.*;
import shu.cms.ui.*;
import shu.ui.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GradientShowerFrame
    extends JFrame {

  private PixelSelectedListener pixelSelectedListener;

  public void setPixelSelectedListener(PixelSelectedListener listener) {
    this.pixelSelectedListener = listener;
  }

  private JPanel contentPane;
  private BorderLayout borderLayout1 = new BorderLayout();
  private JToolBar jToolBar = new JToolBar();
  private JTextField jTextField_Start = new JTextField();
  private JLabel jLabel1 = new JLabel();
  private JTextField jTextField_End = new JTextField();
  private JToggleButton jButton_256 = new JToggleButton("256");
  private JToggleButton jButton_128 = new JToggleButton();
  private JToggleButton jButton_64 = new JToggleButton();
  private JToggleButton jButton_32 = new JToggleButton();
  private JToggleButton jButton_16 = new JToggleButton();
  private JToggleButton jButton_8 = new JToggleButton();
  private JToggleButton jButton_4 = new JToggleButton();
  private JToggleButton jButton_R = new JToggleButton();
  private JToggleButton jButton_G = new JToggleButton();
  private JToggleButton jButton_B = new JToggleButton();
  private JToggleButton jButton_Inv = new JToggleButton();
  private JPanel jPanel3 = new JPanel();
  private JToggleButton jButton_Vertical = new JToggleButton();
  private JButton jButton_Exit = new JButton();
  private DitherCanvas ditherCanvas1 = new DitherCanvas();
  private ButtonGroup stepButtonGroup = new ButtonGroup();
  private ButtonGroup hsbButtonGroup = new ButtonGroup();
  private JButton jButton_Reset = new JButton();
  private JTextField jTextField_CodeR = new JTextField();
  private JTextField jTextField_CodeG = new JTextField();
  private JTextField jTextField_CodeB = new JTextField();
  private short[] pixelsCode = null;
  private BufferedImage bufferedImage = null;
  private BufferedImage HSBImage = null;
  private BufferedImage HSB2Image = null;
  private BufferedImage HSB3Image = null;
  private JToggleButton jButton_Grid = new JToggleButton();
  private JToggleButton jToggleButton_HSB1 = new JToggleButton();
  private JToggleButton jToggleButton_HSB2 = new JToggleButton();
  private JToggleButton jToggleButton_HSB3 = new JToggleButton();
  private JToggleButton[] hsbButtons = new JToggleButton[] {
      jToggleButton_HSB1, jToggleButton_HSB2, jToggleButton_HSB3};
  private JButton jButton_About = new JButton();
  private final static GradientShowerFrame frame = new GradientShowerFrame(true, false);
  private JToggleButton jToggleButton_Fill = new JToggleButton();

  public final static BufferedImage getImage(Dimension size, int start, int end,
                                             boolean R, boolean G, boolean B,
                                             boolean inv, boolean vertical,
                                             boolean grid, int scale) {
    return frame.calculateImage(size, start, end, R, G, B, inv, vertical, grid,
                                scale, false);
  }

  public GradientShowerFrame() {
    this(false, true);
  }

  public GradientShowerFrame(boolean UIControl) {
    this(false, UIControl);
  }

  public void setUIEnable(boolean enable) {
    this.jToolBar.setEnabled(enable);
  }

  private boolean UIControl = true;

  private GradientShowerFrame(boolean getImageOnly, boolean UIControl) {
    this.UIControl = UIControl;
    if (!getImageOnly) {
      try {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        jbInit();
      }
      catch (Exception ex) {
//        Logger.log.error("", ex);
        ex.printStackTrace();
      }

    }

  }

  public static void main(String[] args) throws IOException {
    GradientShowerFrame frame = new GradientShowerFrame(true);
    frame.setVisible(true);

//    BufferedImage img = GradientShowerFrame.getImage(new Dimension(256, 100), 0,
//        255, true, true, true, false, false, false, 256);
//    ImageUtils.storeJPEGImage("gray.jpg", img);

//    int hStep = 10;
//    int sStep = 3;
//    BufferedImage img = frame.calculateHSB2Image(true, true, true, true);
//    WritableRaster raster = img.getRaster();
//    int w = raster.getWidth();
//    int h = raster.getHeight();
//    int[] pixel = new int[3];
//    for (int x = 0; x < w; x++) {
//      for (int y = 0; y < h; y++) {
//        raster.getPixel(x, y, pixel);
//        RGB rgb = new RGB(RGB.ColorSpace.sRGB, pixel);
//        HSV hsv = new HSV(rgb);
////        hsv.H = Math.round(hsv.H / hStep) * hStep;
////        hsv.S = Math.round(hsv.S / sStep) * sStep;
//        hsv.H = ( (int) (hsv.H / hStep)) * hStep;
//        hsv.S = ( (int) (hsv.S / sStep)) * sStep;
////        if (y == 600) {
////          System.out.println(hsv);
////        }
//        RGB rgb2 = hsv.toRGB();
//        rgb2.changeMaxValue(RGB.MaxValue.Int8Bit);
//        pixel[0] = (int) rgb2.R;
//        pixel[1] = (int) rgb2.G;
//        pixel[2] = (int) rgb2.B;
//        raster.setPixel(x, y, pixel);
//      }
//    }
//    ImageUtils.storeTIFFImage("hue.tif", img);
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    this.setExtendedState(JFrame.MAXIMIZED_BOTH); //最大化
    this.addKeyListener(new GradientShowerFrame_this_keyAdapter(this));
    this.setResizable(false); //不能改變大小
    this.setUndecorated(true); //不要邊框
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setSize(screenSize);
    setTitle("GradientShower");

    jTextField_Start.setMaximumSize(new Dimension(40, 22));
    jTextField_Start.setPreferredSize(new Dimension(30, 22));
    jTextField_Start.setToolTipText("起始階調");
    jTextField_Start.setText("0");
    jTextField_Start.addKeyListener(new
                                    GradientShowerFrame_jTextField_Start_keyAdapter(this));
    jLabel1.setText("-");
    jTextField_End.setMaximumSize(new Dimension(40, 22));
    jTextField_End.setPreferredSize(new Dimension(30, 22));
    jTextField_End.setToolTipText("結束階調");
    jTextField_End.setText("255");
    jTextField_End.addKeyListener(new
                                  GradientShowerFrame_jTextField_End_keyAdapter(this));
    jButton_256.setMaximumSize(new Dimension(33, 22));
    jButton_256.setMinimumSize(new Dimension(33, 22));
    jButton_256.setPreferredSize(new Dimension(33, 22));
    jButton_256.setToolTipText("256灰階展開");
    jButton_256.setMnemonic(8);
    jButton_256.setSelected(true);
    jButton_256.setText("256");
    jButton_256.addActionListener(new
                                  GradientShowerFrame_jButton4_actionAdapter(this));
    jButton_128.setMaximumSize(new Dimension(33, 22));
    jButton_128.setMinimumSize(new Dimension(33, 22));
    jButton_128.setToolTipText("128灰階展開");
    jButton_128.setMnemonic(7);
    jButton_128.setText("128");
    jButton_128.addActionListener(new
                                  GradientShowerFrame_jButton5_actionAdapter(this));
    jButton_64.setMaximumSize(new Dimension(26, 22));
    jButton_64.setMinimumSize(new Dimension(26, 22));
    jButton_64.setPreferredSize(new Dimension(26, 22));
    jButton_64.setToolTipText("64灰階展開");
    jButton_64.setMnemonic(6);
    jButton_64.setText("64");
    jButton_64.addActionListener(new GradientShowerFrame_jButton6_actionAdapter(this));
    jButton_32.setMaximumSize(new Dimension(26, 22));
    jButton_32.setMinimumSize(new Dimension(26, 22));
    jButton_32.setPreferredSize(new Dimension(26, 22));
    jButton_32.setToolTipText("32灰階展開");
    jButton_32.setMnemonic(5);
    jButton_32.setText("32");
    jButton_32.addActionListener(new GradientShowerFrame_jButton7_actionAdapter(this));
    jButton_16.setMaximumSize(new Dimension(26, 22));
    jButton_16.setMinimumSize(new Dimension(26, 22));
    jButton_16.setPreferredSize(new Dimension(26, 22));
    jButton_16.setToolTipText("16灰階展開");
    jButton_16.setMnemonic(4);
    jButton_16.setText("16");
    jButton_16.addActionListener(new GradientShowerFrame_jButton8_actionAdapter(this));
    jButton_8.setMaximumSize(new Dimension(19, 22));
    jButton_8.setMinimumSize(new Dimension(19, 22));
    jButton_8.setPreferredSize(new Dimension(19, 22));
    jButton_8.setToolTipText("8灰階展開");
    jButton_8.setMnemonic(3);
    jButton_8.setText("8");
    jButton_8.addActionListener(new GradientShowerFrame_jButton9_actionAdapter(this));
    jButton_4.setMaximumSize(new Dimension(19, 22));
    jButton_4.setMinimumSize(new Dimension(19, 22));
    jButton_4.setPreferredSize(new Dimension(19, 22));
    jButton_4.setToolTipText("4灰階展開");
    jButton_4.setMnemonic(2);
    jButton_4.setText("4");
    jButton_4.addActionListener(new GradientShowerFrame_jButton10_actionAdapter(this));
    jPanel3.addMouseListener(new GradientShowerFrame_jPanel3_mouseAdapter(this));
    jButton_Reset.setMaximumSize(new Dimension(45, 22));
    jButton_Reset.setMinimumSize(new Dimension(40, 22));
    jButton_Reset.setPreferredSize(new Dimension(40, 30));
    jButton_Reset.setToolTipText("重置顯示設定");
    jButton_Reset.setText("reset");
    jButton_Reset.addActionListener(new
                                    GradientShowerFrame_jButton_Reset_actionAdapter(this));
    jTextField_CodeR.setMaximumSize(new Dimension(40, 22));
    jTextField_CodeR.setPreferredSize(new Dimension(30, 22));
    jTextField_CodeR.setToolTipText("滑鼠點選到的階調");
    jTextField_CodeR.setEditable(false);
    jTextField_CodeG.setMaximumSize(new Dimension(40, 22));
    jTextField_CodeG.setPreferredSize(new Dimension(30, 22));
    jTextField_CodeG.setToolTipText("滑鼠點選到的階調");
    jTextField_CodeG.setEditable(false);
    jTextField_CodeB.setMaximumSize(new Dimension(40, 22));
    jTextField_CodeB.setPreferredSize(new Dimension(30, 22));
    jTextField_CodeB.setToolTipText("滑鼠點選到的階調");
    jTextField_CodeB.setEditable(false);

    jButton_Grid.setMaximumSize(new Dimension(35, 22));
    jButton_Grid.setPreferredSize(new Dimension(35, 22));
    jButton_Grid.setToolTipText("方格顯示");
    jButton_Grid.setText("grid");
    jButton_Grid.addActionListener(new
                                   GradientShowerFrame_jButton_Grid_actionAdapter(this));
    jButton_R.setToolTipText("R頻道顯示");
    jButton_G.setToolTipText("G頻道顯示");
    jButton_B.setToolTipText("B頻道顯示");
    jButton_Inv.setToolTipText("階調反轉");
    jButton_Vertical.setToolTipText("階調垂直顯示");
    jButton_Exit.setToolTipText("結束程式");
    jToolBar.setBorder(null);
    jToolBar.setDoubleBuffered(true);
    jToggleButton_HSB1.setMaximumSize(new Dimension(44, 22));
    jToggleButton_HSB1.setMinimumSize(new Dimension(44, 22));
    jToggleButton_HSB1.setPreferredSize(new Dimension(44, 22));
    jToggleButton_HSB1.setToolTipText("HSB顯示1");
    jToggleButton_HSB1.setText("HSB1");
    jToggleButton_HSB1.addActionListener(new
                                         GradientShowerFrame_jToggleButton_HSB_actionAdapter(this));
    jToggleButton_HSB2.setMaximumSize(new Dimension(44, 22));
    jToggleButton_HSB2.setMinimumSize(new Dimension(44, 22));
    jToggleButton_HSB2.setPreferredSize(new Dimension(44, 22));
    jToggleButton_HSB2.setToolTipText("HSB顯示2");
    jToggleButton_HSB2.setText("HSB2");
    jToggleButton_HSB2.addActionListener(new
                                         GradientShowerFrame_jToggleButton1_HSB2_actionAdapter(this));
    jToggleButton_HSB3.setMaximumSize(new Dimension(44, 22));
    jToggleButton_HSB3.setMinimumSize(new Dimension(44, 22));
    jToggleButton_HSB3.setPreferredSize(new Dimension(44, 22));
    jToggleButton_HSB3.setText("HSB3");
    jToggleButton_HSB3.addActionListener(new
                                         GradientShowerFrame_jToggleButton_HSB3_actionAdapter(this));
    jToggleButton_Fill.setMaximumSize(new Dimension(27, 22));
    jToggleButton_Fill.setMinimumSize(new Dimension(27, 22));
    jToggleButton_Fill.setPreferredSize(new Dimension(27, 22));
    jToggleButton_Fill.setToolTipText("填滿漸層");
    jToggleButton_Fill.setMnemonic('0');
    jToggleButton_Fill.setText("Fill");
    jToggleButton_Fill.addActionListener(new
                                         GradientShowerFrame_jToggleButton_Fill_actionAdapter(this));
    jToggleButton_HSB.setMaximumSize(new Dimension(44, 22));
    jToggleButton_HSB.setMinimumSize(new Dimension(44, 22));
    jToggleButton_HSB.setPreferredSize(new Dimension(44, 22));
    jToggleButton_HSB.setToolTipText("HSB顯示");
    jToggleButton_HSB.setText("HSB");
    jToggleButton_HSB.addActionListener(new
                                        GradientShowerFrame_jToggleButton1_actionAdapter(this));
    jButton_Mono.setMaximumSize(new Dimension(48, 22));
    jButton_Mono.setMinimumSize(new Dimension(48, 22));
    jButton_Mono.setPreferredSize(new Dimension(48, 22));
    jButton_Mono.setMnemonic('0');
    jButton_Mono.setText("mono");
    jButton_Mono.addActionListener(new
                                   GradientShowerFrame_jButton_Mono_actionAdapter(this));
    jButton_About.setMaximumSize(new Dimension(45, 22));
    jButton_About.setMinimumSize(new Dimension(45, 22));
    jButton_About.setPreferredSize(new Dimension(45, 22));
    jButton_About.addActionListener(new
                                    GradientShowerFrame_jButton_About_actionAdapter(this));
    stepButtonGroup.add(jButton_256);
    stepButtonGroup.add(jButton_128);
    stepButtonGroup.add(jButton_64);
    stepButtonGroup.add(jButton_32);
    stepButtonGroup.add(jButton_16);
    stepButtonGroup.add(jButton_8);
    stepButtonGroup.add(jButton_4);
    jButton_R.setMaximumSize(new Dimension(20, 22));
    jButton_R.setMinimumSize(new Dimension(20, 22));
    jButton_R.setPreferredSize(new Dimension(20, 22));
    jButton_R.setSelected(true);
    jButton_R.setText("R");
    jButton_R.addActionListener(new GradientShowerFrame_jButton_R_actionAdapter(this));
    jButton_G.setMaximumSize(new Dimension(20, 22));
    jButton_G.setMinimumSize(new Dimension(20, 22));
    jButton_G.setPreferredSize(new Dimension(20, 22));
    jButton_G.setSelected(true);
    jButton_G.setText("G");
    jButton_G.addActionListener(new GradientShowerFrame_jButton_G_actionAdapter(this));
    jButton_B.setMaximumSize(new Dimension(20, 22));
    jButton_B.setMinimumSize(new Dimension(20, 22));
    jButton_B.setPreferredSize(new Dimension(20, 22));
    jButton_B.setSelected(true);
    jButton_B.setText("B");
    jButton_B.addActionListener(new GradientShowerFrame_jButton_B_actionAdapter(this));
    contentPane.setPreferredSize(new Dimension(600, 400));
    jButton_Inv.setMaximumSize(new Dimension(28, 22));
    jButton_Inv.setMinimumSize(new Dimension(28, 22));
    jButton_Inv.setPreferredSize(new Dimension(28, 22));
    jButton_Inv.setText("inv");
    jButton_Inv.addActionListener(new
                                  GradientShowerFrame_jButton_Inv_actionAdapter(this));
    jPanel3.setBackground(Color.gray);
    jPanel3.setVisible(false);
    jButton_Vertical.setMaximumSize(new Dimension(58, 22));
    jButton_Vertical.setMinimumSize(new Dimension(58, 22));
    jButton_Vertical.setPreferredSize(new Dimension(58, 22));
    jButton_Vertical.setText("vertical");
    jButton_Vertical.addActionListener(new
                                       GradientShowerFrame_jButton_Vertical_actionAdapter(this));
    jButton_Exit.setMaximumSize(new Dimension(33, 22));
    jButton_Exit.setMinimumSize(new Dimension(33, 22));
    jButton_Exit.setPreferredSize(new Dimension(33, 22));
    jButton_Exit.setText("exit");
    jButton_Exit.addActionListener(new
                                   GradientShowerFrame_jButton13_actionAdapter(this));
    this.jButton_About.setText("about");

    ditherCanvas1.addMouseListener(new
                                   GradientShowerFrame_ditherCanvas1_mouseAdapter(this));
    jToolBar.setBackground(Color.black);
    jToolBar.add(jButton_About);
    jToolBar.add(jTextField_Start);
    jToolBar.add(jLabel1);
    jToolBar.add(jTextField_End);
    jToolBar.add(jButton_Reset);
    jToolBar.add(jTextField_CodeR);
    jToolBar.add(jTextField_CodeG);
    jToolBar.add(jTextField_CodeB);
    jToolBar.add(jButton_256);
    jToolBar.add(jButton_128);
    jToolBar.add(jButton_64);
    jToolBar.add(jButton_32);
    jToolBar.add(jButton_16);
    jToolBar.add(jButton_8);
    jToolBar.add(jButton_4);
    jToolBar.add(jButton_R);
    jToolBar.add(jButton_G);
    jToolBar.add(jButton_B);
    jToolBar.add(jButton_Mono);
    jToolBar.add(jButton_Inv);
    jToolBar.add(jButton_Vertical);
    jToolBar.add(jButton_Grid);
    jToolBar.add(jToggleButton_HSB);
//    jToolBar.add(jToggleButton_HSB1);
//    jToolBar.add(jToggleButton_HSB2);
//    jToolBar.add(jToggleButton_HSB3);
    hsbButtonGroup.add(jToggleButton_HSB1);
    hsbButtonGroup.add(jToggleButton_HSB2);
    hsbButtonGroup.add(jToggleButton_HSB3);
    jToolBar.add(jToggleButton_Fill);
    jToolBar.add(this.jButton_About);
    jToolBar.add(jButton_Exit);

    contentPane.add(ditherCanvas1, java.awt.BorderLayout.CENTER);
    if (this.UIControl) {
      contentPane.add(jPanel3, java.awt.BorderLayout.SOUTH);
      contentPane.add(jToolBar, java.awt.BorderLayout.NORTH);
    }
    else {
      TinyDialog.Dialog d = TinyDialog.getDialogInstance(this, "x",
          new ActionListener() {

        public void actionPerformed(ActionEvent e) {
          dispose();
        }
      });
      d.setLocation(this.getWidth() - d.getWidth(), 0);
      d.setVisible(true);

    }
    this.setVisible(true);

    bufferedImage = new BufferedImage(ditherCanvas1.getWidth(),
                                      ditherCanvas1.getHeight(),
                                      BufferedImage.TYPE_INT_RGB);
    ditherCanvas1.setBufferedImage(calculateImage());
    ditherCanvas1.setBackground(Color.black);
  }

  void setBorderColor(int r, int g, int b) {
    Color bg = new Color(r, g, b);
    jPanel3.setBackground(bg);
    jToolBar.setBackground(bg);
  }

  /**
   * Calculates and returns the image.  Halts the calculation and returns
   * null if the Applet is stopped during the calculation.
   * @return Image
   */
  BufferedImage calculateImage() {
    return calculateImage(0, 255, true, true, true, false, false, false, 256, true);
  }

  private boolean[] hsbRGBSelected = new boolean[3];

  BufferedImage calculateHSBImage(boolean R, boolean G, boolean B) {
    Dimension size = ditherCanvas1.getSize();
    int height = size.height;
    int width = size.width;

    if (HSBImage == null || HSBImage.getWidth() != width
        || HSBImage.getHeight() != height) {
      HSBImage = new BufferedImage(width,
                                   height, BufferedImage.TYPE_INT_RGB);
    }
    else if (R == hsbRGBSelected[0] && G == hsbRGBSelected[1]
             && B == hsbRGBSelected[2]) {
      return HSBImage;
    }
    hsbRGBSelected[0] = R;
    hsbRGBSelected[1] = G;
    hsbRGBSelected[2] = B;

    HSBImageUtil.fillCircleHSBImage(R, G, B, HSBImage);
    return HSBImage;
  }

  private final static double sqr(double v) {
    return v * v;
  }

  private static double[] PolarValues = new double[3];
  private static double t1, t2;
  private final static double PI180 = (180.0 / Math.PI);

//  public static final double fastCartesian2RadialValues(final double[]
//      cartesianValues) {
//
//    return Math.sqrt(sqr(cartesianValues[1]) + sqr(cartesianValues[2]));
//  }
//
//  public static final double fastCartesian2AngularValues(final double[]
//      cartesianValues) {
//    t1 = cartesianValues[1];
//    t2 = cartesianValues[2];
//    double angular = 0;
//    if (t1 == 0 && t2 == 0) {
//      angular = 0;
//    }
//    else {
//      angular = Math.atan2(t2, t1);
//    }
//
//    angular *= PI180;
//    while (PolarValues[2] >= 360.0) { // Not necessary, but included as a check.
//      angular -= 360.0;
//    }
//    while (PolarValues[2] < 0) {
//      angular += 360.0;
//    }
//    return angular;
//  }

  private boolean[] hsb2RGBSelected = new boolean[3];
  private boolean[] hsb3RGBSelected = new boolean[3];

  BufferedImage calculateHSB2Image(boolean saturationChange, boolean R,
                                   boolean G, boolean B) {
    Dimension size = ditherCanvas1.getSize();
    int height = size.height;
    int width = size.width;
    BufferedImage image = null;
    boolean[] hsbRGBSelected = saturationChange ? hsb2RGBSelected
        : hsb3RGBSelected;

    if (saturationChange) {
      if (HSB2Image == null) {
        HSB2Image = new BufferedImage(width,
                                      height, BufferedImage.TYPE_INT_RGB);
      }
      else if (R == hsbRGBSelected[0] && G == hsbRGBSelected[1]
               && B == hsbRGBSelected[2]) {
        return HSB2Image;
      }
      image = HSB2Image;
    }
    else {
      if (HSB3Image == null) {
        HSB3Image = new BufferedImage(width,
                                      height, BufferedImage.TYPE_INT_RGB);
      }
      else if (R == hsbRGBSelected[0] && G == hsbRGBSelected[1]
               && B == hsbRGBSelected[2]) {
        return HSB3Image;
      }
      image = HSB3Image;
    }

    hsbRGBSelected[0] = R;
    hsbRGBSelected[1] = G;
    hsbRGBSelected[2] = B;

    HSBImageUtil.fillRectHSBImage(R, G, B, image, saturationChange);

//    double[] hsbValues = new double[3];
//    int xEnd = width / 3;
//
//    double[] hueVariance = new double[xEnd];
//    double[] yVariance = new double[height];
//    for (int x = 0; x < xEnd; x++) {
//      hueVariance[x] = ( ( (double) x) / width) * 360;
//    }
//
//    for (int y = 0; y < height; y++) {
//      yVariance[y] = ( ( (double) y) / height);
//    }
//
//    int codeR, codeG, codeB;
//    for (int x = 0; x < xEnd; x++) {
//      double h = ( ( (double) x) / width) * 360;
//      for (int y = 0; y < height; y++) {
//        hsbValues[0] = h;
//        if (saturationChange) {
//          hsbValues[1] = yVariance[y];
//          hsbValues[2] = 1;
//        }
//        else {
//          hsbValues[1] = 1;
//          hsbValues[2] = yVariance[y];
//        }
//
//        HSV.Sandbox.fastToRGBValues(hsbValues);
//        codeR = R ? (int) (hsbValues[0]) : 0;
//        codeG = G ? (int) (hsbValues[1]) : 0;
//        codeB = B ? (int) (hsbValues[2]) : 0;
//
//        //0~120
//        image.setRGB(x, y, ( (codeR << 16) | (codeG << 8) | codeB));
//        //120~240
//        image.setRGB(xEnd + x, y, ( (codeB << 16) | (codeR << 8) | codeG));
//        //240~360
//        image.setRGB(xEnd * 2 + x, y, ( (codeG << 16) | (codeB << 8) | codeR));
//      }
//    }
    return image;
  }

  BufferedImage calculateImage(int start, int end, boolean R, boolean G,
                               boolean B,
                               boolean inv, boolean vertical, boolean grid,
                               int scale, boolean fill) {
//    return calculateImage2(ditherCanvas1.getSize(), start, end, R, G, B, inv,
//                           vertical, grid, scale, fill);
    return calculateImage3(ditherCanvas1.getSize(), start, end, R, G, B, inv,
                           vertical, scale, fill);
  }

  private final static int STUFF = 0;
  private final static boolean CONDITION = true;
  private JToggleButton jToggleButton_HSB = new JToggleButton();

  BufferedImage calculateImage(Dimension size, int start, int end, boolean R,
                               boolean G, boolean B, boolean inv,
                               boolean vertical, boolean grid, int scale,
                               boolean fill) {
    int width = size.width;
    int height = size.height;

    if (pixelsCode == null || pixelsCode.length != width * height) {
      pixelsCode = new short[width * height];
      bufferedImage = new BufferedImage(width,
                                        height, BufferedImage.TYPE_INT_RGB);
    }

    //==========================================================================
    // 參數初始化
    //==========================================================================
    //每加一次code,改變的量
    int codeScale = 256 / (scale - 1);
    //幾階,畫面被切成幾段
    int level = ( (end - start + 1) / codeScale) + STUFF;
    if (level == 0) {
      BufferedImage img = this.calculateImage();
      return img;
    }
    //每一階的pixel數
    int normalStep = grid ? ( (width + height - 1) / (level + 1))
        : (width / level);
    int gripStep = grid ? ( (width + height - 1) / (level + 1))
        : (height / level);
    //code開始(左邊或上面)的值
    int codeStart = inv ? end : start;
    //每一次code的增加量
    int additive = inv ? -codeScale : codeScale;
    //code數值的結束值
    int limit = inv ? start : end;
    //code的初始值
    int code = inv ? end : start;
    //幾個pixel變換一次code
    int step = vertical ? gripStep : normalStep;
    boolean condition = (step == 1) ? false : CONDITION;
    //==========================================================================

    //==========================================================================
    // fill參數初始化
    //==========================================================================
    int remainder = width - (normalStep * level);
    int fillStartLevel = level - remainder + 1;
    int fillStartCode = code + fillStartLevel * additive;
    //==========================================================================

    boolean gripAndGrid = false;
    int gripAndGridCode = 0;

    for (int j = 0; j < height; j++) {
      code = vertical ? code : codeStart;
      int s = step;
      boolean filled = false;

      //========================================================================
      // grid的處理(水平)
      //========================================================================
      if (!vertical && grid && j != 0 && j % s == 0) {
        int multiply = j / s;
        //垂直
        if (inv) {
          codeStart = end + additive * multiply;
          codeStart = codeStart <= limit ? limit : codeStart;
        }
        else {
          codeStart = start + additive * multiply;
          codeStart = codeStart >= limit ? limit : codeStart;
        }
      }
      //========================================================================

      for (int i = 0; i < width; i++) {
        pixelsCode[j * width + i] = (short) code;

        bufferedImage.setRGB(i, j, ( ( (R ? code : 0) << 16)
                                    | ( (G ? code : 0) << 8)
                                    | (B ? code : 0)));
        if (fill && !vertical && code == fillStartCode && !filled) {
          s++;
          filled = true;
        }

        if (!vertical && i % s == 0 && (condition ? i != 0 : true)) {
          //水平
          if (inv) {
            code = code <= limit ? code : code + additive;
            code = code <= limit ? limit : code;
          }
          else {
            code = code >= limit ? code : code + additive;
            code = code >= limit ? limit : code;
          }
        }

        //========================================================================
        // grid的處理(垂直)
        //========================================================================
        if (vertical && grid && i % s == 0 && (condition ? i != 0 : true)) {
          if (gripAndGrid == false) {
            gripAndGrid = true;
            gripAndGridCode = code;
          }
          //垂直
          if (inv) {
            code = code <= limit ? code : code + additive;
            code = code <= limit ? limit : code;
          }
          else {
            code = code >= limit ? code : code + additive;
            code = code >= limit ? limit : code;
          }
        }
        //========================================================================
      }

      //========================================================================
      // grid的處理(垂直):code的恢復
      //========================================================================
      if (gripAndGrid) {
        gripAndGrid = false;
        code = gripAndGridCode;
      }
      //========================================================================

      if (vertical && j != 0 && j % s == 0) {
        //垂直
        if (inv) {
          code = code <= limit ? code : code + additive;
          code = code <= limit ? limit : code;
        }
        else {
          code = code >= limit ? code : code + additive;
          code = code >= limit ? limit : code;
        }
      }
      // Poll once per row to see if we've been told to stop.
    }
    return bufferedImage;
  }

  BufferedImage calculateImage3(Dimension size, int start, int end, boolean R,
                                boolean G, boolean B, boolean inv,
                                boolean vertical, int scale, boolean fill) {
    int width = size.width;
    int height = size.height;
    if (bufferedImage == null || bufferedImage.getWidth() != width
        || bufferedImage.getHeight() != height) {
      bufferedImage = new BufferedImage(width,
                                        height, BufferedImage.TYPE_INT_RGB);
    }

    //==========================================================================
    // 參數初始化
    //==========================================================================
    //每加一次code,改變的量
    int codeScale = 255 / (scale - 1);
    //幾階,畫面被切成幾段
    int level = ( (end - start + 1) / codeScale) + STUFF;
    if (level == 0) {
      BufferedImage img = this.calculateImage();
      return img;
    }
    //每一階的pixel數
    int normalStep = (width / level);
    int gripStep = (height / level);
    //每一次code的增加量
    int additive = inv ? -codeScale : codeScale;
    //code數值的結束值
    int limit = inv ? start : end;
    //code的初始值
    int codeStart = inv ? end : start;
    //幾個pixel變換一次code
    int step = vertical ? gripStep : normalStep;
    boolean condition = (step == 1) ? false : CONDITION;
    //==========================================================================

    //==========================================================================
    // fill參數初始化
    //==========================================================================
    int remainder = width - (normalStep * level);
    int fillStartLevel = level - remainder + 1;
    int fillStartCode = codeStart + fillStartLevel * additive;
    //==========================================================================

    short[][] pixels = new short[level][3];
    short[][] coordinates = new short[level][2];
    short[][] sizes = new short[level][2];
    short code = (short) codeStart;
    for (int x = 0; x < level; x++) {
      pixels[x][0] = R ? code : 0;
      pixels[x][1] = G ? code : 0;
      pixels[x][2] = B ? code : 0;
      code += additive;
      coordinates[x][0] = (short) (vertical ? 0 : step * x);
      coordinates[x][1] = (short) (vertical ? step * x : 0);
      sizes[x][0] = (short) (vertical ? width : step);
      sizes[x][1] = (short) (vertical ? step : height);
    }

    Graphics g = bufferedImage.getGraphics();
    g.setColor(Color.black);
    g.fillRect(0, 0, width, height);

    for (int x = 0; x < level; x++) {
      short[] pixel = pixels[x];
      Color color = new Color(pixel[0], pixel[1], pixel[2]);
      g.setColor(color);
      short[] coordinate = coordinates[x];
      short[] xysize = sizes[x];
      g.fillRect(coordinate[0], coordinate[1], xysize[0], xysize[1]);
    }

    return bufferedImage;
  }

  BufferedImage calculateImage2(Dimension size, int start, int end, boolean R,
                                boolean G, boolean B, boolean inv,
                                boolean vertical, boolean grid, int scale,
                                boolean fill) {
    int width = size.width;
    int height = size.height;

    if (pixelsCode == null || pixelsCode.length != width * height) {
      pixelsCode = new short[width * height];
      bufferedImage = new BufferedImage(width,
                                        height, BufferedImage.TYPE_INT_RGB);
    }

    //==========================================================================
    // 參數初始化
    //==========================================================================
    //每加一次code,改變的量
    int codeScale = 255 / (scale - 1);
    //幾階,畫面被切成幾段
    int level = ( (end - start + 1) / codeScale) + STUFF + 1;
    int levelIndex = 0;
    if (level == 0) {
      BufferedImage img = this.calculateImage();
      return img;
    }
    //每一階的pixel數
    int normalStep = grid ? ( (width + height - 1) / (level + 1))
        : (width / level);
    int gripStep = grid ? ( (width + height - 1) / (level + 1))
        : (height / level);
    //code開始(左邊或上面)的值
    int codeStart = inv ? end : start;
    //每一次code的增加量
    int additive = inv ? -codeScale : codeScale;
    //code數值的結束值
    int limit = inv ? start : end;
    //code的初始值
    int code = inv ? end : start;
    //幾個pixel變換一次code
    int step = vertical ? gripStep : normalStep;
    boolean condition = (step == 1) ? false : CONDITION;
    //==========================================================================

    //==========================================================================
    // fill參數初始化
    //==========================================================================
    int remainder = width - (normalStep * level);
    int fillStartLevel = level - remainder + 1;
    int fillStartCode = code + fillStartLevel * additive;
    //==========================================================================

    boolean gripAndGrid = false;
    int gripAndGridCode = 0;

    for (int j = 0; j < height; j++) {
      code = vertical ? code : codeStart;
      int s = step;
      boolean filled = false;

      //========================================================================
      // grid的處理(水平)
      //========================================================================
      if (!vertical && grid && j != 0 && j % s == 0) {
        int multiply = j / s;
        //垂直
        if (inv) {
          codeStart = end + additive * multiply;
          codeStart = codeStart <= limit ? limit : codeStart;
        }
        else {
          codeStart = start + additive * multiply;
          codeStart = codeStart >= limit ? limit : codeStart;
        }
      }
      //========================================================================

      for (int i = 0; i < width; i++) {
        pixelsCode[j * width + i] = (short) code;

        bufferedImage.setRGB(i, j, ( ( (R ? code : 0) << 16)
                                    | ( (G ? code : 0) << 8)
                                    | (B ? code : 0)));
        if (fill && !vertical && code == fillStartCode && !filled) {
          s++;
          filled = true;
        }

        if (!vertical && i % s == 0 && (condition ? i != 0 : true)) {
          //水平
          if (inv) {
            code = code <= limit ? code : code + additive;
            code = code <= limit ? limit : code;
          }
          else {
            code = code >= limit ? code : code + additive;
            code = code >= limit ? limit : code;
          }
          levelIndex++;
        }

        //========================================================================
        // grid的處理(垂直)
        //========================================================================
        if (vertical && grid && i % s == 0 && (condition ? i != 0 : true)) {
          if (gripAndGrid == false) {
            gripAndGrid = true;
            gripAndGridCode = code;
          }
          //垂直
          if (inv) {
            code = code <= limit ? code : code + additive;
            code = code <= limit ? limit : code;
          }
          else {
            code = code >= limit ? code : code + additive;
            code = code >= limit ? limit : code;
          }
          levelIndex++;
        }
        //========================================================================
      }

      //========================================================================
      // grid的處理(垂直):code的恢復
      //========================================================================
      if (gripAndGrid) {
        gripAndGrid = false;
        code = gripAndGridCode;
      }
      //========================================================================

      if (vertical && j != 0 && j % s == 0) {
        //垂直
        if (inv) {
          code = code <= limit ? code : code + additive;
          code = code <= limit ? limit : code;
        }
        else {
          code = code >= limit ? code : code + additive;
          code = code >= limit ? limit : code;
        }
        levelIndex++;
      }
      // Poll once per row to see if we've been told to stop.
    }
    return bufferedImage;
  }

  public void jButton13_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  public void setToolBarVisible(boolean visible) {
    this.jToolBar.setVisible(visible);
  }

  public void ditherCanvas1_mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
      this.jToolBar.setVisible(!jToolBar.isVisible());
    }
  }

  public void jButton_R_actionPerformed(ActionEvent e) {
    updateImage();
  }

  public void jButton_G_actionPerformed(ActionEvent e) {
    updateImage();
  }

  public void setChannel(String name) {
    if (name.equals("R")) {
      setChannel(true, false, false);
    }
    else if (name.equals("G")) {
      setChannel(false, true, false);
    }
    else if (name.equals("B")) {
      setChannel(false, false, true);
    }
    else if (name.equals("Y")) {
      setChannel(true, true, false);
    }
    else if (name.equals("M")) {
      setChannel(true, false, true);
    }
    else if (name.equals("C")) {
      setChannel(false, true, true);
    }
    else if (name.equals("W")) {
      setChannel(true, true, true);
    }
  }

//  public void setChannel(RGBBase.Channel ch) {
//    switch (ch) {
//      case R:
//        setChannel(true, false, false);
//        break;
//      case G:
//        setChannel(false, true, false);
//        break;
//      case B:
//        setChannel(false, false, true);
//        break;
//      case Y:
//        setChannel(true, true, false);
//        break;
//      case M:
//        setChannel(true, false, true);
//        break;
//      case C:
//        setChannel(false, true, true);
//        break;
//      case W:
//        setChannel(true, true, true);
//        break;
//
//    }
//  }
  public void setChannel(boolean R, boolean G, boolean B) {
    this.jButton_R.setSelected(R);
    this.jButton_G.setSelected(G);
    this.jButton_B.setSelected(B);
    this.updateImage();
  }

  public void jButton_B_actionPerformed(ActionEvent e) {
    updateImage();
  }

  protected void setChannelButtons(boolean enable) {
    this.jButton_R.setEnabled(enable);
    this.jButton_G.setEnabled(enable);
    this.jButton_B.setEnabled(enable);
  }

  protected void setStepButtons(boolean enable) {
    this.jButton_256.setEnabled(enable);
    this.jButton_128.setEnabled(enable);
    this.jButton_64.setEnabled(enable);
    this.jButton_32.setEnabled(enable);
    this.jButton_16.setEnabled(enable);
    this.jButton_8.setEnabled(enable);
    this.jButton_4.setEnabled(enable);

  }

  protected void setDirectionButtons(boolean enable) {
    this.jButton_Grid.setEnabled(enable);
    this.jButton_Inv.setEnabled(enable);
    this.jButton_Vertical.setEnabled(enable);
    this.jToggleButton_Fill.setEnabled(enable);
  }

//  protected boolean stepButtonEnable = true;
  public void setupImage(boolean R, boolean G, boolean B) {
    this.jButton_R.setSelected(R);
    this.jButton_G.setSelected(G);
    this.jButton_B.setSelected(B);
    this.updateImage();
  }

  public void setupImage(boolean R, boolean G, boolean B, boolean inverse) {
    this.jButton_Inv.setSelected(inverse);
    setupImage(R, G, B);
  }

//  private BufferedImage hsb1, hsb2, hsb3;
  void updateImage() {
    BufferedImage img = null;
    if (this.jToggleButton_HSB1.isSelected()) {
      img = calculateHSBImage(this.jButton_R.isSelected(),
                              this.jButton_G.isSelected(),
                              this.jButton_B.isSelected());

    }
    else if (this.jToggleButton_HSB2.isSelected()) {
      img = calculateHSB2Image(false, this.jButton_R.isSelected(),
                               this.jButton_G.isSelected(),
                               this.jButton_B.isSelected());
    }
    else if (this.jToggleButton_HSB3.isSelected()) {
      img = calculateHSB2Image(true, this.jButton_R.isSelected(),
                               this.jButton_G.isSelected(),
                               this.jButton_B.isSelected());
    }
    else {
      String startStr = jTextField_Start.getText();
      String endStr = jTextField_End.getText();
      int start = 0;
      int end = 255;
      try {
        if (startStr.length() != 0) {
          start = Integer.parseInt(startStr);
          start = start < 0 ? 0 : start;
          start = start > 255 ? 255 : start;
        }
        if (endStr.length() != 0) {
          end = Integer.parseInt(endStr);
          end = end < 0 ? 0 : end;
          end = end > 255 ? 255 : end;
        }
      }
      catch (NumberFormatException ex) {
        return;
      }
      if (start > end) {
        return;
      }
      int m = this.stepButtonGroup.getSelection().getMnemonic();
      img = calculateImage(start, end, this.jButton_R.isSelected(),
                           this.jButton_G.isSelected(),
                           this.jButton_B.isSelected(),
                           this.jButton_Inv.isSelected(),
                           this.jButton_Vertical.isSelected(),
                           this.jButton_Grid.isSelected(),
                           (int) Math.pow(2, m),
                           this.jToggleButton_Fill.isSelected());
    }
    ditherCanvas1.setBufferedImage(img);
  }

  public void jTextField_Start_keyReleased(KeyEvent e) {
    this.updateImage();
  }

  public void jTextField_End_keyReleased(KeyEvent e) {
    this.updateImage();
  }

  public void jButton_Inv_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void setInverse(boolean inverse) {
    this.jButton_Inv.setSelected(inverse);
    this.updateImage();
  }

  public void jButton_Vertical_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void jButton4_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void jButton5_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void jButton6_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void jButton7_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void jButton8_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void jButton9_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void jButton10_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void jPanel3_mouseEntered(MouseEvent e) {
    this.jPanel3.setVisible(false);
  }

  public void jButton_Reset_actionPerformed(ActionEvent e) {
    this.jTextField_Start.setText("0");
    this.jTextField_End.setText("255");
    this.jButton_256.setSelected(true);
    this.jButton_R.setSelected(true);
    this.jButton_G.setSelected(true);
    this.jButton_B.setSelected(true);
    this.jButton_Vertical.setSelected(false);
    this.jButton_Grid.setSelected(false);
    this.jButton_Inv.setSelected(false);
    this.jButton_Mono.setText("mono");
//    hsbButtonGroup.clearSelection();
//    this.jToggleButton_HSB.setSelected(false);
//    this.jToggleButton_HSB2.setSelected(false);
//    this.stepButtonEnable=false;
//    switchStepButton();
    resetHSBButtons();
    this.setStepButtons(true);
    setDirectionButtons(true);
    setChannelButtons(true);
    this.updateImage();
  }

  public void jButton_Grid_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  protected void resetHSBButtons() {
    this.jToggleButton_HSB.setEnabled(true);
    this.jToggleButton_HSB.setSelected(false);
    this.jToggleButton_HSB.setText("HSB");
    this.jToggleButton_HSB1.setSelected(false);
    this.jToggleButton_HSB2.setSelected(false);
    this.jToggleButton_HSB3.setSelected(false);
    hsbButtonGroup.clearSelection();
  }

  public void jToggleButton_HSB1_actionPerformed(ActionEvent e) {
    if (this.jToggleButton_HSB1.isSelected()) {
      setStepButtons(false);
      setDirectionButtons(false);
      this.updateImage();
    }
    else {
      this.jButton_Reset.doClick();
    }

  }

  public void jToggleButton_HSB2_actionPerformed(ActionEvent e) {
    if (this.jToggleButton_HSB2.isSelected()) {
      setStepButtons(false);
      setDirectionButtons(false);
      this.updateImage();
    }
    else {
      this.jButton_Reset.doClick();
    }
  }

  public void jToggleButton_HSB3_actionPerformed(ActionEvent e) {
    if (this.jToggleButton_HSB3.isSelected()) {
      setStepButtons(false);
      setDirectionButtons(false);
      this.updateImage();
    }
    else {
      this.jButton_Reset.doClick();
    }
  }

  public void jToggleButton_Fill_actionPerformed(ActionEvent e) {
    this.updateImage();
  }

  public void ditherCanvas1_mousePressed(MouseEvent e) {
    if (e.getClickCount() == 1 && !this.jToggleButton_HSB1.isSelected()) {
      BufferedImage img = ditherCanvas1.getBufferedImage();
      if (e.getX() > img.getWidth() || e.getY() > img.getHeight()) {
        return;
      }
      int[] pixel = img.getRaster().getPixel(e.getX(), e.getY(), new int[3]);
      this.jTextField_CodeR.setText(Integer.toString(pixel[0]));
      this.jTextField_CodeG.setText(Integer.toString(pixel[1]));
      this.jTextField_CodeB.setText(Integer.toString(pixel[2]));
    }
  }

  private int hsbToogleIndex = 0;
  protected JButton jButton_Mono = new JButton();

  public void jToggleButton_HSB_actionPerformed(ActionEvent e) {

    JToggleButton button = hsbButtons[hsbToogleIndex++ % hsbButtons.length];
    button.setSelected(true);
    this.jToggleButton_HSB.setSelected(true);
    this.jToggleButton_HSB.setText(button.getText());

    setStepButtons(false);
    setDirectionButtons(false);
    this.updateImage();
  }

  private int monoColorIndex = 0;
  private String[] channelNames = new String[] {
      "R", "G", "B", "Y", "M", "C", "W", "K"};

  public void jButton_Mono_actionPerformed(ActionEvent e) {
    String name = channelNames[monoColorIndex++ % channelNames.length];
    this.jButton_Mono.setText(name);
    this.jButton_256.setSelected(true);
    if (name.equals("K")) {
      setChannel(false, false, false);
      this.jTextField_Start.setText("0");
      this.jTextField_End.setText("0");
    }
    else {
//      RGBBase.Channel ch = RGBBase.Channel.valueOf(name);
      setChannel(name);
      this.jTextField_Start.setText("255");
      this.jTextField_End.setText("255");
    }

    //==========================================================================
    // UI設定
    //==========================================================================
    setStepButtons(false);
    setDirectionButtons(false);
    setChannelButtons(false);
//    this.jToggleButton_HSB.setEnabled(false);
    //==========================================================================
    this.updateImage();
  }

  public void jButton_About_actionPerformed(ActionEvent e) {
    AboutBox dlg = new AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                    (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }

  public void this_keyTyped(KeyEvent e) {
    char key = e.getKeyChar();
    switch (key) {
      case 'R':
      case 'r':
        jButton_R.setSelected(!jButton_R.isSelected());
        break;
      case 'G':
      case 'g':
        jButton_G.setSelected(!jButton_G.isSelected());
        break;
      case 'B':
      case 'b':
        jButton_B.setSelected(!jButton_B.isSelected());
        break;
    }
  }
}

class GradientShowerFrame_this_keyAdapter
    extends KeyAdapter {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_this_keyAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void keyTyped(KeyEvent e) {
    adaptee.this_keyTyped(e);
  }
}

class GradientShowerFrame_jButton_About_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton_About_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_About_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton_Mono_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton_Mono_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_Mono_actionPerformed(e);
  }
}

class GradientShowerFrame_jToggleButton1_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jToggleButton1_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jToggleButton_HSB_actionPerformed(e);
  }
}

class GradientShowerFrame_jToggleButton_Fill_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jToggleButton_Fill_actionAdapter(GradientShowerFrame
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jToggleButton_Fill_actionPerformed(e);
  }
}

class GradientShowerFrame_jToggleButton_HSB3_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jToggleButton_HSB3_actionAdapter(GradientShowerFrame
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jToggleButton_HSB3_actionPerformed(e);
  }
}

class GradientShowerFrame_jToggleButton1_HSB2_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jToggleButton1_HSB2_actionAdapter(GradientShowerFrame
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jToggleButton_HSB2_actionPerformed(e);
  }
}

class GradientShowerFrame_jToggleButton_HSB_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jToggleButton_HSB_actionAdapter(GradientShowerFrame
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jToggleButton_HSB1_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton_Reset_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton_Reset_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_Reset_actionPerformed(e);
  }
}

class GradientShowerFrame_jPanel3_mouseAdapter
    extends MouseAdapter {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jPanel3_mouseAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.jPanel3_mouseEntered(e);
  }

  public void mouseExited(MouseEvent e) {
  }
}

class GradientShowerFrame_jButton10_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton10_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton10_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton9_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton9_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton9_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton8_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton8_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton8_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton7_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton7_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton7_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton6_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton6_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton6_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton5_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton5_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton5_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton4_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton4_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton4_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton_Vertical_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton_Vertical_actionAdapter(GradientShowerFrame
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_Vertical_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton_Grid_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton_Grid_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_Grid_actionPerformed(e);
  }
}

class GradientShowerFrame_jTextField_End_keyAdapter
    extends KeyAdapter {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jTextField_End_keyAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
    adaptee.jTextField_End_keyReleased(e);
  }
}

class GradientShowerFrame_jTextField_Start_keyAdapter
    extends KeyAdapter {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jTextField_Start_keyAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void keyReleased(KeyEvent e) {
    adaptee.jTextField_Start_keyReleased(e);
  }
}

class GradientShowerFrame_jButton_Inv_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton_Inv_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_Inv_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton_B_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton_B_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_B_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton_R_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton_R_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_R_actionPerformed(e);
  }
}

class GradientShowerFrame_jButton_G_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton_G_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_G_actionPerformed(e);
  }
}

class GradientShowerFrame_ditherCanvas1_mouseAdapter
    extends MouseAdapter {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_ditherCanvas1_mouseAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseClicked(MouseEvent e) {
    adaptee.ditherCanvas1_mouseClicked(e);
  }

  public void mousePressed(MouseEvent e) {
    adaptee.ditherCanvas1_mousePressed(e);
  }
}

class GradientShowerFrame_jButton13_actionAdapter
    implements ActionListener {

  private GradientShowerFrame adaptee;

  GradientShowerFrame_jButton13_actionAdapter(GradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton13_actionPerformed(e);
  }
}

class AboutBox
    extends JDialog implements ActionListener {

  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JPanel insetsPanel2 = new JPanel();
  JPanel insetsPanel3 = new JPanel();
  JButton button1 = new JButton();
  JLabel imageLabel = new JLabel();
  JLabel label1 = new JLabel();
  JLabel label2 = new JLabel();
  JLabel label3 = new JLabel();
  JLabel label4 = new JLabel();
  ImageIcon image1 = new ImageIcon();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  GridLayout gridLayout1 = new GridLayout();
  String product = "Gradient Shower";
  String version = "1.0 (20100903)";
  String copyright = "skyforce (c) 2010";
  String comments = "a Colour Management System by Java";

  public AboutBox(Frame parent) {
    super(parent);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public AboutBox() {
    this(null);
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    image1 = new ImageIcon(GradientShowerFrame.class.getResource("about.png"));
    imageLabel.setIcon(image1);
    setTitle("About");
    panel1.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    insetsPanel1.setLayout(flowLayout1);
    insetsPanel2.setLayout(flowLayout1);
    insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    gridLayout1.setRows(4);
    gridLayout1.setColumns(1);
    label1.setText(product);
    label2.setText(version);
    label3.setText(copyright);
    label4.setText(comments);
    insetsPanel3.setLayout(gridLayout1);
    insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
    button1.setText("OK");
    button1.addActionListener(this);
    insetsPanel2.add(imageLabel, null);
    panel2.add(insetsPanel2, BorderLayout.WEST);
    getContentPane().add(panel1, null);
    insetsPanel3.add(label1, null);
    insetsPanel3.add(label2, null);
    insetsPanel3.add(label3, null);
    insetsPanel3.add(label4, null);
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2, BorderLayout.NORTH);
    setResizable(true);
  }

  /**
   * Close the dialog on a button event.
   *
   * @param actionEvent ActionEvent
   */
  public void actionPerformed(ActionEvent actionEvent) {
    if (actionEvent.getSource() == button1) {
      dispose();
    }
  }
}
