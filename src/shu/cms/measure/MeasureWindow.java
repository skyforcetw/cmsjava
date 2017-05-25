package shu.cms.measure;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.borland.jbcl.layout.*;
import shu.ui.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 顯示一個視窗,可以改變顏色,提供給測色儀器測量用
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class MeasureWindow
    extends JFrame {
  private final static int[][] OFFSET_ARRAY = new int[][] {
      {
      0, 0}, {
      1, 0}, {
      0, 1}, {
      1, 1}
  };

  protected PatchCanvas patchCanvas = new PatchCanvas();

  protected class PatchCanvas
      extends JComponent {

    private int[] getXY() {
      int x, y;
      if (this.isShowing()) {
        Point location = this.getLocationOnScreen();
        x = dicomTargetLocationX - location.x;
        y = dicomTargetLocationY - location.y;
      }
      else {
        Dimension size = this.getSize();
        x = (size.width - dicomTargetLength) / 2;
        y = (size.height - dicomTargetLength) / 2;
      }
      return new int[] {
          x, y};
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (isDICOMMode()) {
        int[] xy = getXY();
        final int x = xy[0], y = xy[1];

        if (multiColor) {
          int size = OFFSET_ARRAY.length;
          for (int i = 0; i < size; i++) {
            int[] offset = OFFSET_ARRAY[i];
            Color c = getColor(i);
            g.setColor(c);
            int startx = x + offset[0];
            int starty = y + offset[1];

            for (int x1 = startx; x1 < startx + dicomTargetLength; x1 += 2) {
              for (int y1 = starty; y1 < starty + dicomTargetLength; y1 += 2) {
                g.fillRect(x1, y1, 1, 1);
              }
            }
          }
        }
        else {
          g.setColor(color);
          g.fillRect(x, y, dicomTargetLength, dicomTargetLength);
        }
      }
    }

  }

  /**
   *
   * @param screenSize Dimension 視窗的寬及高
   */
  public MeasureWindow(Dimension screenSize) {
    this(screenSize.width, screenSize.height);
  }

  /**
   *
   * @param width int 視窗的寬
   * @param height int 視窗的高
   */
  public MeasureWindow(int width, int height) {
    this.windowWidth = width;
    this.windowHeight = height;
    this.mode = Mode.Normal;
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   *
   * @param dicom DICOM DICOM參數
   */
  public MeasureWindow(DICOM dicom) {
    this(Mode.getMode(dicom));
  }

  protected MeasureWindow(Mode mode) {
    this.mode = mode;
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }

  }

  public MeasureWindow() {
    this(Mode.DICOMUndecorated);
  }

  public static enum Mode {
    Normal(false, false, false, false),
    DICOM(true, false, false, false),
    DICOMUndecorated(true, true, true, false),
    FullScreen(false, false, true, true);

    Mode(boolean dicom, boolean black, boolean undecorate, boolean fullscreen) {
      this.dicom = dicom;
      this.undecorate = undecorate;
      this.fullscreen = fullscreen;
    }

    private boolean undecorate = false;
    private boolean dicom = false;
    private boolean fullscreen = false;
    public static Mode getMode(DICOM dicom) {
      switch (dicom) {
        case Normal:
          return Mode.DICOM;
        case Undecorated:
          return Mode.DICOMUndecorated;
        case None:
        default:
          return Mode.Normal;
      }
    }
  }

  /**
   * 是否處於全視窗模式
   * @return boolean
   */
  protected boolean isFullScreenMode() {
    return mode.fullscreen;
  }

  /**
   * 是否處於DICOM模式下(色塊非佔滿全視窗)
   * @return boolean
   */
  protected boolean isDICOMMode() {
    return mode.dicom;
  }

  private Mode mode = Mode.DICOM;

  private BorderLayout borderLayout1 = new BorderLayout();
  private int windowWidth = 433; //default for i1 display2 on 24" LCD
  private int windowHeight = 622; //default for i1 display2 on 24" LCD
  private static int dicomTargetLength;
  private static int dicomTargetLocationX;
  private static int dicomTargetLocationY;
  private static Dimension screenSize;
  private Color color = Color.black;
  private Color color1 = Color.red;
  private Color color2 = Color.green;
  private Color color3 = Color.blue;

  private Color getColor(int index) {
    switch (index) {
      case 0:
        return color;
      case 1:
        return color1;
      case 2:
        return color2;
      case 3:
        return color3;
      default:
        return null;
    }
  }

  protected boolean multiColor = false;
  protected JLabel southLabel = new JLabel(" ");
  protected JLabel northLabel1 = new JLabel(" ");
  private final static double DICOM_RATIO = 0.1;
  static {
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dicomTargetLength = (int) Math.pow(screenSize.width * screenSize.height *
                                       DICOM_RATIO, .5);
    dicomTargetLocationX = (screenSize.width - dicomTargetLength) / 2;
    dicomTargetLocationY = (screenSize.height - dicomTargetLength) / 2;
  }

  public void setColor(Color ul, Color ur, Color dl, Color dr) {
    if (isDICOMMode()) {
      this.color = ul;
      this.color1 = ur;
      this.color2 = dl;
      this.color3 = dr;
      this.patchCanvas.repaint();
    }
  }

  private Color getForegroundColor(Color pattern) {
    if (isDICOMMode()) {
      return Color.WHITE;
    }
    else {
      Color.RGBtoHSB(pattern.getRed(), pattern.getGreen(), pattern.getBlue(),
                     hsbValues);
      Color foreground = hsbValues[2] > 0.5 ? Color.black : Color.white;
      return foreground;
    }
  }

  public void setColor(Color c) {
    if (isDICOMMode()) {
      //非全螢幕, 字的顏色只要跟背景色顏色不同即可.
      this.color = c;
      this.patchCanvas.repaint();
    }
    else {
      //全螢幕, 所以要跟著pattern改變字的顏色
      this.getContentPane().setBackground(c);
    }

    Color foreground = getForegroundColor(c);
    southLabel.setForeground(foreground);
    northLabel1.setForeground(foreground);
    northLabel2.setForeground(foreground);
    northLabel3.setForeground(foreground);
    northLabel4.setForeground(foreground);
  }

  public void setMultiColor(boolean multiColor) {
    this.multiColor = multiColor;
  }

  protected float[] hsbValues = new float[3];

  public void setNorthLabel1(String text) {
    this.northLabel1.setText(text);
  }

  public void setNorthLabel2(String text) {
    this.northLabel2.setText(text);
  }

  public void setNorthLabel3(String text) {
    this.northLabel3.setText(text);
  }

  public void setNorthLabel4(String text) {
    this.northLabel4.setText(text);
  }

  public void setSouthLabel(String text) {
    this.southLabel.setText(text);
  }

  protected void setDICOMBackground() {
    this.getContentPane().setBackground(background);
    this.jPanel1.setBackground(background);
  }

  private Color background = Color.black;
  protected void setMeasureBackground(Color color) {
    this.background = color;
  }

  protected JPanel jPanel1 = new JPanel();
  protected JLabel northLabel2 = new JLabel(" ");
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JLabel northLabel3 = new JLabel(" ");
  protected JLabel northLabel4 = new JLabel(" ");
  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    this.setAlwaysOnTop(true);

    this.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        this_keyReleased(e);
      }
    });

    if (mode.undecorate) {
      GUIUtils.setUndecorated(this);
    }

    if (isDICOMMode()) {
      this.setTitle("Measure Window (DICOM mode)");
      this.setSize(screenSize);
      setDICOMBackground();
    }
    else {
      this.setTitle("Measure Window");
      this.getContentPane().setBackground(Color.black);

      if (mode.fullscreen) {
        this.setSize(screenSize);
      }
      else {
        this.setSize(windowWidth, windowHeight);
        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        setLocation( (screenSize.width - frameSize.width) / 2,
                    (screenSize.height - frameSize.height) / 2);

      }
      this.jPanel1.setVisible(false);
    }
    patchCanvas.setDoubleBuffered(true);
    jPanel1.setLayout(verticalFlowLayout1);

    jPanel1.add(northLabel1);
    jPanel1.add(northLabel2);
    jPanel1.add(northLabel3);
    jPanel1.add(northLabel4);

    getContentPane().add(patchCanvas, java.awt.BorderLayout.CENTER);
//    if (!mode.undecorate) {
    getContentPane().add(southLabel, java.awt.BorderLayout.SOUTH);
    getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);
//    }
  }

  public static void main(String[] args) {
//    TestMeasureWindow measurewindow = new TestMeasureWindow(Meter.Type.
//        i1Display2, 24);
//    measurewindow.setVisible(true);

//    JOptionPane.showMessageDialog(null,
//                                  "校正完畢", "校正完畢",
//                                  JOptionPane.INFORMATION_MESSAGE);

//    MeasureWindow measurewindow = new MeasureWindow(MeterMeasurement.getSize(
//        Meter.Instr.i1Display2, 24));
    MeasureWindow measurewindow = new MeasureWindow(Mode.DICOMUndecorated);
//    measurewindow.setMultiColor(true);
    measurewindow.setVisible(true);

    try {
      long start = System.nanoTime();
      for (int x = 0; x < 256; x += 1) {
        measurewindow.setColor(new Color(x, x, x));
        Thread.currentThread().sleep(30);
      }
//      measurewindow.setVisible(false);
      System.out.println( (System.nanoTime() - start) / 1000000.);
    }
    catch (InterruptedException ex) {
    }

  }

  private void this_keyReleased(KeyEvent e) {
    if (e.getKeyCode() == 27) {
      this.setVisible(false);
      if (listener != null) {
        listener.windowsInvisible();
      }
    }
  }

  private WindowsInvisibleListener listener;
  public void setWindowsInvisibleListener(WindowsInvisibleListener listener) {
    this.listener = listener;
  }

  public interface WindowsInvisibleListener {
    public void windowsInvisible();
  }
}
