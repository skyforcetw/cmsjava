package sky4s.test.frc;

import java.awt.*;
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
public class FRCFrame
    extends JFrame {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JSlider jSlider1 = new JSlider();

  public FRCFrame() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    this.setSize(640, 480);
    this.getContentPane().setBackground(Color.black);
//    this.setTitle("Measure");

    this.setAlwaysOnTop(true);

    // Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    setLocation( (screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);

    jPanel3.setPreferredSize(new Dimension(50, 10));
    jPanel2.setPreferredSize(new Dimension(10, 50));
    jPanel1.setDoubleBuffered(true); //    jPanel1.setBackground(Color.white);
    jSlider1.addChangeListener(new FRCFrame_jSlider1_changeAdapter(this));
    jSlider1.setMinimum( -80000);
    jSlider1.setMaximum( +80000);
    jPanel4.setPreferredSize(new Dimension(50, 10));
    this.getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
    this.getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);
    this.getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);
    this.getContentPane().add(jSlider1, java.awt.BorderLayout.NORTH);
    this.getContentPane().add(jPanel4, java.awt.BorderLayout.WEST);
    setVisible(true);
  }

  public void setColorC(Color color) {
    this.jPanel1.setBackground(color);
    this.jPanel1.setToolTipText(color.toString());
  }

  public void setColorD(Color color) {
    this.jPanel2.setBackground(color);
    this.jPanel2.setToolTipText(color.toString());
  }

  public void setColorR(Color color) {
    this.jPanel3.setBackground(color);
    this.jPanel3.setToolTipText(color.toString());
  }

  public void setColorL(Color color) {
    this.jPanel4.setBackground(color);
    this.jPanel4.setToolTipText(color.toString());
  }

  public static void main(String[] args) {
    FRCFrame frcframe = new FRCFrame();
    Color w0 = new Color(255, 0, 0);
    Color w1 = new Color(255, 255, 0);
    Color w2 = new Color(0, 255, 0);

    frcframe.setColorR(w0);
    frcframe.setColorD(w2);
    frcframe.setColorL(w1);
    long interval = 16666666;

    while (true) {
      frcframe.setColorC(w0);
      sleep(interval);
      frcframe.setColorC(w2);
      sleep(interval);
    }

  }

  private static long offset = 0;
  private static long selfOffset = 0;
  private static long start = 0;
  private static long wait = 0;
  private static long ms = 0;
  private static long realSleep = 0;
  JPanel jPanel4 = new JPanel();

  protected static void sleep(long nanosecond) {
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

  public void jSlider1_stateChanged(ChangeEvent e) {
    int val = jSlider1.getValue();
    offset = val;
//    System.out.println(val);
  }

  Image createImage(int w, int h, int[] pix, boolean frameTwo) {
    int[] realPix = new int[w * h];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int rgb = pix[y * w + x];
        int r = (rgb >> 16) & 256;
        int g = (rgb >> 8) & 256;
        int b = (rgb >> 16) & 256;

        r = frameTwo ? ( (r % 2 == 1) ? r / 2 + 1 : r / 2) : (r / 2);
        g = frameTwo ? ( (g % 2 == 1) ? g / 2 + 1 : g / 2) : (g / 2);
        b = frameTwo ? ( (b % 2 == 1) ? b / 2 + 1 : b / 2) : (b / 2);
        realPix[y * w + x] = (r << 16) | (g << 8) | b;
      }
    }
    return createImage(new MemoryImageSource(w, h,
                                             ColorModel.getRGBdefault(),
                                             realPix,
                                             0, w));
  }

}

class FRCFrame_jSlider1_changeAdapter
    implements ChangeListener {
  private FRCFrame adaptee;
  FRCFrame_jSlider1_changeAdapter(FRCFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void stateChanged(ChangeEvent e) {
    adaptee.jSlider1_stateChanged(e);
  }
}
