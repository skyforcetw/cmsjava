package sky4s.test.frc;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;

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
public class FRCGradientShowerFrame
    extends JFrame {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new Shower();
  int[] pixels = null;
//  Image img = null;
  Sleep sleep = new Sleep();
  FRCGradientShowerFrame self;

  public FRCGradientShowerFrame() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    self = this;
  }

  class Shower
      extends JPanel {
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (img1 != null) {
        g.drawImage(img1, 0, 0, this);
      }
    }
  }

  private void jbInit() throws Exception {

    getContentPane().setLayout(borderLayout1);

    // Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    this.setSize(screenSize);
    Dimension frameSize = getSize();
    setLocation( (screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
    jToggleButton1.setText("FRC");
    jToggleButton1.addActionListener(new
                                     FRCGradientShowerFrame_jToggleButton1_actionAdapter(this));
    jSlider1.setPreferredSize(new Dimension(1000, 16));
    jSlider1.addChangeListener(new
                               FRCGradientShowerFrame_jSlider1_changeAdapter(this));
//    jSlider1.setMinimum( -80000);
//    jSlider1.setMaximum( +80000);
    jSlider1.setMinimum( -15500000);
    jSlider1.setMaximum( +100000);
    jPanel2.setLayout(flowLayout1);
    this.getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
    this.getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);
    jPanel2.add(jSlider1);
    jPanel2.add(jToggleButton1);
    jPanel1.setBackground(Color.black);
    jPanel1.setDoubleBuffered(true);

    this.setVisible(true);
    img1 = this.getFrameOne();
    img2 = this.getFrameTwo();
    jPanel1.repaint();

//    jPanel1.getGraphics().drawImage(img1, 0, 0, this);
//    jPanel1.getGraphics().drawImage(img1, 0, 0, self);

  }

  Image img1 = null;
  Image img2 = null;
  boolean frc = false;
  long interval = 16666666;

  Image calculateImage() {
    return calculateImage(0, 512, true, true, true, false, false, false, 512);
  }

  Image calculateImage(int start, int end, boolean R, boolean G, boolean B,
                       boolean inv, boolean vertical, boolean grid, int scale) {
    Dimension size = jPanel1.getSize();
    int width = size.width;
    int height = size.height;
    pixels = new int[width * height];
    int c[] = new int[3]; //temporarily holds R,G,B,A information
    int index = 0;

    //每加一次code,改變的量
    int codeScale = 512 / (scale - 1);
    //幾階,畫面被切成幾段
    int level = ( (end - start + 1) / codeScale) + 1;
    if (level == 0) {
      Image img = this.calculateImage();
      return img;
    }
    //每一階的pixel數
    int normalStep = grid ? ( (width + height - 1) / (level + 1)) :
        (width / level) + 1;
    int gripStep = grid ? ( (width + height - 1) / (level + 1)) :
        (height / level) + 1;
    //code開始
    int codeStart = inv ? end : start;
    //每一次code的增加量
    int additive = inv ? -codeScale : codeScale;
    int limit = inv ? start : end;

    int code = inv ? end : start;
    //幾個pixel變換一次code
    int step = vertical ? gripStep : normalStep;

    boolean gripAndGrid = false;
    int gripAndGridCode = 0;

    for (int j = 0; j < height; j++) {
      code = vertical ? code : codeStart;

      //========================================================================
      // grid的處理(水平)
      //========================================================================
      if (!vertical && grid && j != 0 && j % step == 0) {
        int multiply = j / step;
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
        c[0] = R ? code : 0;
        c[1] = G ? code : 0;
        c[2] = B ? code : 0;
//        c[3] = 255;
//        pixelsCode[j * width + i] = (short) code;
        int pixel = ( (c[0] << 18) | (c[1] << 9) | c[2]);
        pixels[index++] = pixel;
        if (!vertical && i != 0 && i % step == 0) {
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
        if (vertical && grid && i != 0 && i % step == 0) {
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

      if (vertical && j != 0 && j % step == 0) {
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

    return createImage(width, height, pixels, frameTwo);
  }

  boolean frameTwo = false;
  JPanel jPanel2 = new JPanel();
  JSlider jSlider1 = new JSlider();
  JToggleButton jToggleButton1 = new JToggleButton();
  FlowLayout flowLayout1 = new FlowLayout();

  Image getFrameOne() {
    frameTwo = false;
    return calculateImage();
  }

  Image getFrameTwo() {
    frameTwo = true;
    return calculateImage();
  }

  Image createImage(int w, int h, int[] pix, boolean frameTwo) {
    int[] realPix = new int[w * h];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int rgb = pix[y * w + x];
        int r = (rgb >> 18) & 511;
        int g = (rgb >> 9) & 511;
        int b = rgb & 511;

        r = frameTwo ? ( (r % 2 == 1) ? r / 2 + 1 : r / 2) : (r / 2);
        g = frameTwo ? ( (g % 2 == 1) ? g / 2 + 1 : g / 2) : (g / 2);
        b = frameTwo ? ( (b % 2 == 1) ? b / 2 + 1 : b / 2) : (b / 2);
//        b = 0;
//        r = frameTwo ? ( (r % 2 == 1) ? r / 2 + 1 : r / 2) : 0;
//        g = frameTwo ? 0 : (g / 2);

        realPix[y * w + x] = 255 << 24 | (r << 16) | (g << 8) | b;
      }
    }
    return createImage(new MemoryImageSource(w, h,
                                             ColorModel.getRGBdefault(),
                                             realPix,
                                             0, w));
  }

  public static void main(String[] args) {
    FRCGradientShowerFrame frcgradientshowerframe = new FRCGradientShowerFrame();
//    System.out.println(200&255);
  }

  static class Sleep {
    long offset = 0;
    long selfOffset = 0;
    long start = 0;
    long wait = 0;
    long ms = 0;
    long realSleep = 0;

    protected void sleep(long nanosecond) {
      start = System.nanoTime();
      wait = nanosecond + offset - selfOffset;
      ms = wait / 1000000;

      try {
        Thread.sleep(ms - 1);
      }
      catch (InterruptedException ex) {
        ex.printStackTrace();
      }
      while (true) {
        Thread.yield();
        if ( (realSleep = (System.nanoTime() - start)) >= wait) {
          Thread.yield();
//        selfOffset = realSleep - wait;
//        System.out.println(selfOffset);
          break;
        }
        Thread.yield();
      }

    }

  }

  public void jSlider1_stateChanged(ChangeEvent e) {
    int val = jSlider1.getValue();
    sleep.offset = val;
  }

  public void jToggleButton1_actionPerformed(ActionEvent e) {
    if (jToggleButton1.isSelected()) {
      frc = true;

      new Thread() {
        public void run() {
          while (frc) {
            jPanel1.getGraphics().drawImage(img1, 0, 0, self);
            sleep.sleep(interval);
            jPanel1.getGraphics().drawImage(img2, 0, 0, self);
            sleep.sleep(interval);
          }
        }
      }.start();
    }
    else {
//      img1 = this.getFrameOne();
//      img2 = this.getFrameOne();
      frc = false;
    }
  }
}

class FRCGradientShowerFrame_jSlider1_changeAdapter
    implements ChangeListener {
  private FRCGradientShowerFrame adaptee;
  FRCGradientShowerFrame_jSlider1_changeAdapter(FRCGradientShowerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void stateChanged(ChangeEvent e) {
    adaptee.jSlider1_stateChanged(e);
  }
}

class FRCGradientShowerFrame_jToggleButton1_actionAdapter
    implements ActionListener {
  private FRCGradientShowerFrame adaptee;
  FRCGradientShowerFrame_jToggleButton1_actionAdapter(FRCGradientShowerFrame
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jToggleButton1_actionPerformed(e);
  }
}
