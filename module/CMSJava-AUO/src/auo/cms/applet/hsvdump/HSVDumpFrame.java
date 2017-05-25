package auo.cms.applet.hsvdump;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;

import auo.cms.colorspace.depend.*;
import auo.cms.hsv.autotune.*;
import auo.cms.hsv.saturation.*;
import auo.cms.hsvinteger.*;
import com.borland.jbcl.layout.*;
import shu.cms.colorspace.depend.*;
import shu.image.*;
import shu.util.log.*;
import shu.ui.TinyDialog;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;

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
public class HSVDumpFrame
    extends JFrame implements ActionListener {
  protected JPanel contentPane;
  protected JMenuBar jMenuBar1 = new JMenuBar();
  protected JMenu jMenuFile = new JMenu();
  protected JMenuItem jMenuFileExit = new JMenuItem();
  protected JMenu jMenuHelp = new JMenu();
  protected JMenuItem jMenuHelpAbout = new JMenuItem();
  protected JLabel statusBar = new JLabel();
  protected JButton jButton2_LoadParameter = new JButton();
  protected JTextField jTextField2_Parameter = new JTextField();
  protected JCheckBox jCheckBox1_HSVClip = new JCheckBox();
  protected JButton jButton3_Dump = new JButton();
  protected JSlider jSlider1_TurnPoint = new JSlider();
  protected JLabel jLabel1_TurnPointValue = new JLabel();
  protected JFileChooser jFileChooser1 = new JFileChooser();
  public HSVDumpFrame() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    imageFile = new File(jTextField1_Image.getText());
    imageFile = imageFile.exists() ? imageFile : null;
    parameterFile = new File(jTextField2_Parameter.getText());
    parameterFile = parameterFile.exists() ? parameterFile : null;
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    setSize(new Dimension(400, 400));
    setTitle("HSV Dump");
    //statusBar.setText(" ");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        jMenuFileExit_actionPerformed(actionEvent);
      }
    });

    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        jMenuHelpAbout_actionPerformed(actionEvent);
      }
    });
    jButton2_LoadParameter.setText("Load Parameter");
    jButton2_LoadParameter.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jTextField2_Parameter.setText("hsv.lut");
    jCheckBox1_HSVClip.setSelected(true);
    jCheckBox1_HSVClip.setText("HSV Cliping");
    jButton3_Dump.setText("Dump");
    jButton3_Dump.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton3_actionPerformed(e);
      }
    });
    jLabel1_TurnPointValue.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1_TurnPointValue.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel1_TurnPointValue.setText("7");
    jSlider1_TurnPoint.setMaximum(15);
    jSlider1_TurnPoint.setValue(7);
    jSlider1_TurnPoint.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider1_stateChanged(e);
      }
    });
    jLabel2_TurnPoint.setText("Turn Point");
    jPanel1.setLayout(boxLayout21);
    boxLayout21.setAxis(BoxLayout.Y_AXIS);
    jTextField_Bin.setPreferredSize(new Dimension(50, 20));
    jTextField_Bin.setText("255");
    jTextField_Bin.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        jTextField_Rin_keyReleased(e);
      }
    });

    jTextField_Gin.setPreferredSize(new Dimension(50, 20));
    jTextField_Gin.setToolTipText("");
    jTextField_Gin.setText("255");
    jTextField_Gin.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        jTextField_Rin_keyReleased(e);
      }
    });
    jTextField_Rin.setPreferredSize(new Dimension(50, 20));
    jTextField_Rin.setText("255");
    jTextField_Rin.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        jTextField_Rin_keyReleased(e);
      }
    });
    jLabel3.setText("Input");
    jLabel4.setText("Output");
    jTextField_Bout.setPreferredSize(new Dimension(50, 20));
    jTextField_Bout.setEditable(false);
    jTextField_Bout.setText("255");
    jTextField_Gout.setPreferredSize(new Dimension(50, 20));
    jTextField_Gout.setEditable(false);
    jTextField_Gout.setText("255");
    jTextField_Rout.setPreferredSize(new Dimension(50, 20));
    jTextField_Rout.setEditable(false);
    jTextField_Rout.setText("255");
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel4.setLayout(borderLayout2);
    jPanel5.setLayout(verticalFlowLayout1);
    jButton1_LoadImage.setText("Load Image");
    jButton1_LoadImage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jTextField1_Image.setText("Hue Circle.bmp");
    jTextField1_Image.addKeyListener(new KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        jTextField1_Image_keyTyped(e);
      }
    });
    jPanel6.setLayout(verticalFlowLayout2);
    jCheckBox1_6bitClip.setSelected(true);
    jCheckBox1_6bitClip.setText("Clip to 6Bit");
    jCheckBox_OutputRGB10BitData.setText("Output RGB 10Bit Data");
    jMenuBar1.add(jMenuFile);
    jMenuFile.add(jMenuFileExit);
    jMenuBar1.add(jMenuHelp);
    jMenuHelp.add(jMenuHelpAbout);
    setJMenuBar(jMenuBar1);
    jPanel1.add(jPanel2);
    jPanel2.add(jLabel3);
    jPanel2.add(jTextField_Rin);
    jPanel2.add(jTextField_Gin);
    jPanel2.add(jTextField_Bin);
    jPanel1.add(jPanel3);
    jPanel3.add(jLabel4);
    jPanel3.add(jTextField_Rout);
    jPanel3.add(jTextField_Gout);
    jPanel3.add(jTextField_Bout);
    contentPane.add(jPanel4, java.awt.BorderLayout.CENTER);
    contentPane.add(statusBar, java.awt.BorderLayout.WEST);
    jPanel4.add(jPanel5, java.awt.BorderLayout.WEST);
    jPanel5.add(jButton1_LoadImage);
    jPanel5.add(jButton2_LoadParameter);
    jPanel5.add(jLabel2_TurnPoint);
    jPanel4.add(jPanel6, java.awt.BorderLayout.CENTER);
    jPanel6.add(jTextField1_Image);
    jPanel6.add(jTextField2_Parameter);
    jPanel6.add(jSlider1_TurnPoint);
    jPanel6.add(jLabel1_TurnPointValue);
    jPanel6.add(jCheckBox1_HSVClip);
    jPanel6.add(jCheckBox1_6bitClip);
    jPanel6.add(jCheckBox_OutputRGB10BitData);
    jPanel6.add(jButton3_Dump); //    d.add(jPanel1);
    if (inRGBMode) {
      jCheckBox1_HSVClip.setVisible(false);
      jLabel2_TurnPoint.setVisible(false);
      jSlider1_TurnPoint.setVisible(false);
      jLabel1_TurnPointValue.setVisible(false);
      jButton3_Dump.setVisible(false);
    }
    jFileChooser1.setCurrentDirectory(new File("."));
  }

  /**
   * File | Exit action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
    System.exit(0);
  }

  /**
   * Help | About action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
    HSVDumpFrame_AboutBox dlg = new HSVDumpFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                    (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }

  IntegerHSVIP getIntegerHSVIP() {
    if (null == parameterFile) {
      d = TinyDialog.getDialogInstance(this, "Load hsv.lut first.", this);
      d.setLocationRelativeTo(null);
      d.setVisible(true);

      return null;
    }
    TuneParameter tuneParameter = null;
    try {
      tuneParameter = TuneParameter.getInstanceFromFile(parameterFile.
          getAbsolutePath());
    }
    catch (IOException ex) {
      Logger.log.error(ex);
    }

    int turnPoint = this.jSlider1_TurnPoint.getValue();
//    IntegerSaturationFormula integerSaturationFormula = new
//        IntegerSaturationFormula( (byte) turnPoint, 4);
    FastIntegerSaturationFormula integerSaturationFormula = new
        FastIntegerSaturationFormula( (byte) turnPoint);
    IntegerHSVIP hsvIP = new IntegerHSVIP(integerSaturationFormula,
                                          tuneParameter);
    hsvIP.setHSVClip(this.jCheckBox1_HSVClip.isSelected());
    return hsvIP;
  }

  public void jButton3_actionPerformed(ActionEvent e) {
    if (null == imageFile) {
      File file = new File(jTextField1_Image.getText());
      if (file.exists()) {
        imageFile = file;
      }
    }
    if (null == parameterFile) {
      File file = new File(jTextField2_Parameter.getText());
      if (file.exists()) {
        parameterFile = file;
      }
    }
    if (imageFile != null && parameterFile != null) {
      /*TuneParameter tuneParameter = null;
             try {
        tuneParameter = TuneParameter.getInstanceFromFile(parameterFile.
            getAbsolutePath());
             }
             catch (IOException ex) {
        Logger.log.error(ex);
             }
             int turnPoint = this.jSlider1.getValue();
             IntegerSaturationFormula integerSaturationFormula = new
          IntegerSaturationFormula( (byte) turnPoint, 4);
             IntegerHSVIP hsvIP = new IntegerHSVIP(integerSaturationFormula,
                                            tuneParameter);
             hsvIP.setHSVClip(this.jCheckBox1.isSelected());*/
      IntegerHSVIP hsvIP = getIntegerHSVIP();
      //==========================================================================
      // load image
      //==========================================================================
      BufferedImage img = null;
      try {
        String filename = imageFile.getAbsolutePath();
        img = ImageUtils.loadImage(filename);
        if (null == img) {
          img = ImageUtils.loadImageByJAI(filename);
        }
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
      if (jCheckBox1_6bitClip.isSelected()) {
        To6BitImage.to6Bit(img);
      }

//      BufferedImage img2 = new BufferedImage(10,10,BufferedImage.im

      WritableRaster raster = img.getRaster();
      final int w = raster.getWidth();
      final int h = raster.getHeight();
      //==========================================================================
      boolean output10BitData = jCheckBox_OutputRGB10BitData.isSelected();
      int[] pixels = new int[3];
      RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, RGB.MaxValue.Double255);
      try {
        BufferedWriter r = output10BitData ?
            new BufferedWriter(new FileWriter("r.txt")) : null;
        BufferedWriter g = output10BitData ?
            new BufferedWriter(new FileWriter("g.txt")) : null;
        BufferedWriter b = output10BitData ?
            new BufferedWriter(new FileWriter("b.txt")) : null;

        for (int y = 0; y < h; y++) {
          for (int x = 0; x < w; x++) {
            raster.getPixel(x, y, pixels);
            rgb.R = pixels[0];
            rgb.G = pixels[1];
            rgb.B = pixels[2];

            AUOHSV auoHSV = new AUOHSV(rgb);
//            AUOHSV auoHSV2 = hsvIP.getHSV(auoHSV);
            short[] rgbValues = hsvIP.getRGBValues(auoHSV);
//            short[] rgbValues = auoHSV2.getRGBValues();

            if (output10BitData) {
              r.write(Integer.toHexString(rgbValues[0]) + "\n");
              g.write(Integer.toHexString(rgbValues[1]) + "\n");
              b.write(Integer.toHexString(rgbValues[2]) + "\n");
            }
            pixels[0] = rgbValues[0] >> 2;
            pixels[1] = rgbValues[1] >> 2;
            pixels[2] = rgbValues[2] >> 2;
            raster.setPixel(x, y, pixels);
          }
        }
        //Àx¦sbmp
        ImageUtils.storeBMPImage("dump.bmp", img);
        if (output10BitData) {
          r.flush();
          r.close();
          g.flush();
          g.close();
          b.flush();
          b.close();
        }
      }
      catch (IOException ex) {
//        ex.printStackTrace();
        Logger.log.error(ex);
      }
      d = TinyDialog.getDialogInstance(this, "Done", this);
      d.setLocationRelativeTo(null);
      d.setVisible(true);
      img = null;
    }

  }

  private Dialog d;

  public void jSlider1_stateChanged(ChangeEvent e) {
    int val = this.jSlider1_TurnPoint.getValue();
    this.jLabel1_TurnPointValue.setText(Integer.toString(val));
  }

  private File imageFile;
  private File parameterFile;
  protected JLabel jLabel2_TurnPoint = new JLabel();
  protected JPanel jPanel1 = new JPanel();
  protected BoxLayout2 boxLayout21 = new BoxLayout2();
  protected JPanel jPanel2 = new JPanel();
  protected JPanel jPanel3 = new JPanel();
  protected JTextField jTextField_Bin = new JTextField();
  protected JTextField jTextField_Gin = new JTextField();
  protected JTextField jTextField_Rin = new JTextField();
  protected JLabel jLabel3 = new JLabel();
  protected JLabel jLabel4 = new JLabel();
  protected JTextField jTextField_Bout = new JTextField();
  protected JTextField jTextField_Gout = new JTextField();
  protected JTextField jTextField_Rout = new JTextField();
  private boolean inRGBMode = false;
  public void jButton1_actionPerformed(ActionEvent e) {

//    System.getenv()
    int result = this.jFileChooser1.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      imageFile = jFileChooser1.getSelectedFile();
      String filename = imageFile.getAbsolutePath();
      jTextField1_Image.setText(filename);
    }
  }

  public void jButton2_actionPerformed(ActionEvent e) {
    int result = this.jFileChooser1.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      parameterFile = jFileChooser1.getSelectedFile();
      String filename = parameterFile.getAbsolutePath();
      this.jTextField2_Parameter.setText(filename);
      hsvIP = null;
    }
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e ActionEvent
   */
  public void actionPerformed(ActionEvent e) {
    d.setVisible(false);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
          exception.printStackTrace();
        }

        HSVDumpFrame frame = new HSVDumpFrame();

        frame.validate();

        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
          frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
          frameSize.width = screenSize.width;
        }
        frame.setLocation( (screenSize.width - frameSize.width) / 2,
                          (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);

      }
    });

  }

  private IntegerHSVIP hsvIP = null;
  protected JPanel jPanel4 = new JPanel();
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected BorderLayout borderLayout2 = new BorderLayout();
  protected JPanel jPanel5 = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JButton jButton1_LoadImage = new JButton();
  protected JPanel jPanel6 = new JPanel();
  protected JTextField jTextField1_Image = new JTextField();
  protected VerticalFlowLayout verticalFlowLayout2 = new VerticalFlowLayout();
  protected JCheckBox jCheckBox1_6bitClip = new JCheckBox();
  protected JCheckBox jCheckBox_OutputRGB10BitData = new JCheckBox();
  public void jTextField_Rin_keyReleased(KeyEvent e) {
    if (null == hsvIP) {
      hsvIP = this.getIntegerHSVIP();
      if (null == hsvIP) {
        return;
      }
    }
    int r = Integer.parseInt(this.jTextField_Rin.getText());
    int g = Integer.parseInt(this.jTextField_Gin.getText());
    int b = Integer.parseInt(this.jTextField_Bin.getText());
    RGB rgb = new RGB(r, g, b);
//    System.out.println(rgb);
    AUOHSV auoHSV = new AUOHSV(rgb);
    AUOHSV auoHSV2 = hsvIP.getHSV(auoHSV);
    RGB rgb2 = auoHSV2.toRGB();
//    rgb2.changeMaxValue(RGB.MaxValue.Int8Bit);
    rgb2.changeMaxValue(RGB.MaxValue.Double255);
    jTextField_Rout.setText(Double.toString(rgb2.R));
    jTextField_Gout.setText(Double.toString(rgb2.G));
    jTextField_Bout.setText(Double.toString(rgb2.B));

  }

  public void jTextField1_Image_keyTyped(KeyEvent e) {
    File file = new File( ( (JTextField) e.getSource()).getText());
    if (file.exists()) {
      imageFile = file;
    }
  }

}
