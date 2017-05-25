package auo.cms.applet.ccttest;

import java.awt.*;
import javax.swing.*;
import shu.ui.GUIUtils;
import java.awt.BorderLayout;
import com.sun.media.jai.widget.DisplayJAI;
import shu.image.GradientImage;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
//import shu.cms.image.ImageUtils;
import shu.math.Interpolation;
import shu.math.array.*;
import shu.cms.CorrelatedColorTemperature;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.*;
import java.awt.image.WritableRaster;
import com.borland.jbcl.layout.VerticalFlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
public class CCTTestFrame
    extends JFrame {
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JScrollPane jScrollPane1 = new JScrollPane();
  protected JSlider jSlider1 = new JSlider();
  protected JPanel jPanel1 = new JPanel();
  protected JTextField jTextField1 = new JTextField();
  protected JLabel jLabel1 = new JLabel();
  BufferedImage originalImage = GradientImage.getImage(new Dimension(1920, 950),
      0, 255, true, true, true, false, false,
      256, true, null);

  protected JPanel jPanel2 = new JPanel();
  protected JPanel jPanel3 = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JSlider jSlider2 = new JSlider();
  protected JLabel jLabel2 = new JLabel();
  protected JLabel jLabel3 = new JLabel();
  public CCTTestFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    BufferedImage image = ImageUtils.cloneBufferedImage(originalImage);
    DisplayJAI dj = new DisplayJAI(image);
    jScrollPane1.setViewportView(dj);
//      jScrollPane1.se
  }

  private void jbInit() throws Exception {
    this.setSize(1920, 1080);
    getContentPane().setLayout(borderLayout1);
    jSlider1.setMaximum(255);
    jSlider1.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider1_stateChanged(e);
      }
    });
    jTextField1.setPreferredSize(new Dimension(55, 20));
    jTextField1.setText("7500");
    jLabel1.setText("jLabel1");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        this_componentResized(e);
      }
    });
    this.addWindowStateListener(new WindowStateListener() {
      public void windowStateChanged(WindowEvent e) {
        this_windowStateChanged(e);
      }
    });
    this.addWindowListener(new WindowAdapter() {
      public void windowActivated(WindowEvent e) {
        this_windowActivated(e);
      }
    }); jSlider2.setMaximum(400);
    this.getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

    jPanel1.setLayout(verticalFlowLayout1);
    jLabel2.setMaximumSize(new Dimension(55, 15));
    jLabel2.setMinimumSize(new Dimension(55, 15));
    jLabel2.setPreferredSize(new Dimension(55, 15));
    jLabel2.setText("1");
    jSlider2.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider2_stateChanged(e);
      }
    });

    jLabel3.setText("O");

    jPanel1.add(jPanel3);
    jPanel3.add(jLabel2);
    jPanel3.add(jSlider2);
    jPanel3.add(jLabel3);
    jPanel1.add(jPanel2);
    jPanel2.add(jTextField1);
    jPanel2.add(jLabel1);
    jPanel2.add(jSlider1);
    this.getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

    jSlider2.setValue(0);
  }

  public static void main(String[] args) {
    CCTTestFrame ccttestframe = new CCTTestFrame();
    GUIUtils.runAsApplication(ccttestframe, false);
  }

  BufferedImage filterImage(int cct, int turnPoint, double cctgamma,
                            BufferedImage image) {
//    double[] key = new double[] {
//        0, turnPoint, 255};
//    double[] value = new double[] {
//        6500, 6500, cct};
//    Interpolation lut = new Interpolation(key, value);
    CIExyY xyY = CorrelatedColorTemperature.CCT2DIlluminantxyY(cct);
    CIEXYZ XYZ = xyY.toXYZ();
    XYZ.normalizeY();
    RGB whiteRGB = RGB.fromXYZ(XYZ, RGB.ColorSpace.sRGB_gamma22, true);
    double maxY = whiteRGB.toXYZ().Y;
    whiteRGB.changeMaxValue(RGB.MaxValue.Int8Bit);
    double maxV = whiteRGB.getValue(RGB.Channel.W);
//    double scale = maxV / 255.;

    int h = image.getHeight();
    int w = image.getWidth();
    WritableRaster raster = image.getRaster();
    int[] rgbValues = new int[3];
    int[] preRGBValues = null;
    double turnRatio = turnPoint / 255.;
    CIEXYZ D65XYZ = CorrelatedColorTemperature.CCT2DIlluminantxyY(6500).toXYZ();
//    D65XYZ.normalizeY();
//    double cctgamma = 1.8;
    boolean isInverse = false;
    for (int x = 0; x < w; x++) {
      raster.getPixel(x, 0, rgbValues);
      double ratio = ( (double) x / w);
      double Y = Math.pow(ratio, 2.2) * maxY;
      if (Y == 0) {
        rgbValues[0] = rgbValues[1] = rgbValues[2];
      }
      else if (ratio < turnRatio) {
        D65XYZ.normalizeY();
        D65XYZ.times(Y);
        RGB rgb = RGB.fromXYZ(D65XYZ, RGB.ColorSpace.sRGB_gamma22);
        rgb.changeMaxValue(RGB.MaxValue.Int8Bit);
        rgbValues[0] = (int) rgb.R;
        rgbValues[1] = (int) rgb.G;
        rgbValues[2] = (int) rgb.B;

      }
      else {
        double cctratio = (ratio - turnRatio) / (1 - turnRatio);
        cctratio = Math.pow(cctratio, cctgamma);
        double nowcct = 6500 + (cct - 6500) * cctratio;

        CIEXYZ XYZ2 = CorrelatedColorTemperature.CCT2DIlluminantxyY(nowcct).
            toXYZ();
        XYZ2.normalizeY();
        XYZ2.times(Y);
        RGB rgb = RGB.fromXYZ(XYZ2, RGB.ColorSpace.sRGB_gamma22);
        rgb.changeMaxValue(RGB.MaxValue.Int8Bit);
        rgbValues[0] = (int) rgb.R;
        rgbValues[1] = (int) rgb.G;
        rgbValues[2] = (int) rgb.B;

      }
      System.out.println(IntArray.toString(rgbValues));
      for (int y = 0; y < h; y++) {
        raster.setPixel(x, y, rgbValues);
      }
      if (null != preRGBValues && (preRGBValues[0] > rgbValues[0] ||
                                   preRGBValues[1] > rgbValues[1] ||
                                   preRGBValues[2] > rgbValues[2])) {
        isInverse = true;
      }
      if (null == preRGBValues) {
        preRGBValues = new int[3];
      }
      preRGBValues[0] = rgbValues[0];
      preRGBValues[1] = rgbValues[1];
      preRGBValues[2] = rgbValues[2];

    }
    jLabel3.setText(isInverse ? "X" : "O");
    return image;
  }

  public void jSlider1_stateChanged(ChangeEvent e) {
    int v = jSlider1.getValue();
    jLabel1.setText(Integer.toString(v));

    BufferedImage image = ImageUtils.cloneBufferedImage(originalImage);
    int cct = Integer.parseInt(jTextField1.getText());
    double cctgamma = Double.parseDouble(jLabel2.getText());
    image = filterImage(cct, v, cctgamma, image);
    DisplayJAI dj = new DisplayJAI(image);
    jScrollPane1.setViewportView(dj);

  }

  public void jSlider2_stateChanged(ChangeEvent e) {
    int v = jSlider2.getValue();
    double gamma = v / 400. * 4 + 1;
    jLabel2.setText(Double.toString(gamma));
    jSlider1_stateChanged(e);
  }

  public void this_windowActivated(WindowEvent e) {
//    int w = jScrollPane1.getWidth();
//    int w2 = jScrollPane1.getViewport().getWidth();
//    originalImage = GradientImage.getImage(new Dimension(w2, 950),
//                                           0, 255, true, true, true, false, false,
//                                           256, true, null);
  }

  public void this_windowStateChanged(WindowEvent e) {
//    int w2 = jScrollPane1.getViewport().getWidth();
  }

  public void this_componentResized(ComponentEvent e) {

    int w2 = jScrollPane1.getViewport().getWidth();
    originalImage = GradientImage.getImage(new Dimension(w2, 950),
                                           0, 255, true, true, true, false, false,
                                           256, true, null);
    jSlider1_stateChanged(null);
  }
}
