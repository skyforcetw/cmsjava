package auo.cms.test.colormatrix;

import java.io.*;
import javax.media.jai.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;

import com.sun.media.jai.widget.*;
import shu.math.array.*;
import shu.ui.*;

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
public class ColorMatrixFrame
    extends JFrame {
  protected JPanel contentPane;
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JPanel jPanel1 = new JPanel();
  protected JPanel jPanel2 = new JPanel();
  protected JTextField jTextField1 = new JTextField();
  protected JTextField jTextField2 = new JTextField();
  protected DisplayJAI dj = new DisplayJAI();
  protected JSlider jSlider1 = new JSlider();
  protected JSlider jSlider2 = new JSlider();
  protected JButton jButton1 = new JButton();
  protected JFileChooser jFileChooser1 = new JFileChooser();
  protected ColorMatrix cm;
  protected PlanarImage image;

  public ColorMatrixFrame(PlanarImage image) {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    setImage(image);
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    setSize(new Dimension(634, 367));
    setTitle("Frame Title");
    jTextField1.setPreferredSize(new Dimension(50, 24));
    jTextField1.setEditable(false);
    jTextField1.setText("192");
    jTextField2.setPreferredSize(new Dimension(50, 24));
    jTextField2.setEditable(false);
    jTextField2.setText("240");
    jSlider1.setMaximum(255);
    jSlider1.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider1_stateChanged(e);
      }
    });
    jSlider2.setMaximum(255);
    jSlider2.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider2_stateChanged(e);
      }
    });
    jButton1.setText("Load");
    jButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    contentPane.add(jPanel1, java.awt.BorderLayout.CENTER);
    contentPane.add(jPanel2, java.awt.BorderLayout.SOUTH);
    jPanel1.add(dj);
    jPanel2.add(jTextField1);
    jPanel2.add(jSlider1);
    jPanel2.add(jTextField2);
    jPanel2.add(jSlider2);
    jPanel2.add(jButton1);
    int in = Integer.parseInt(jTextField1.getText());
    int out = Integer.parseInt(jTextField2.getText());
    jSlider1.setValue(in);
    jSlider2.setValue(out);
    cm = new ColorMatrix(in, out);
  }

  public static void main(String[] args) {
    ColorMatrix cm = new ColorMatrix(192, 240);
    for (int x = 192; x <= 255; x++) {
      int[] r = cm.getRGB(new int[] {x, 0, 0});
      System.out.println(x + " " + r[0]);

    }
    args = new String[] {
        "d200.jpg"};
    PlanarImage image = JAI.create("fileload", args[0]);
    ColorMatrixFrame cmframe = new ColorMatrixFrame(image);
    GUIUtils.runAsApplication(cmframe, false);
  }

  private void setImage(PlanarImage image) {
    this.image = image;
    int w = image.getWidth();
    int h = image.getHeight();
    this.setSize(w, h);
    dj.set(image);
  }

  private void processImage() {
    if (image != null) {
      WritableRaster raster = image.copyData();

      int w = raster.getWidth();
      int h = raster.getHeight();
      int[] rgb = new int[3];
      for (int x = 0; x < w; x++) {
        for (int y = 0; y < h; y++) {
          raster.getPixel(x, y, rgb);
          int[] result = cm.getRGB(rgb);
          raster.setPixel(x, y, result);
        }
      }

      ColorModel colorModel = image.getColorModel();
      BufferedImage bi = new BufferedImage(colorModel,
                                           raster,
                                           colorModel.isAlphaPremultiplied(),
                                           null);
      dj.set(PlanarImage.wrapRenderedImage(bi));
    }

  }

  public void jSlider1_stateChanged(ChangeEvent e) {
    int value = jSlider1.getValue();
    this.jTextField1.setText(Integer.toString(value));
    int in = Integer.parseInt(jTextField1.getText());
    int out = Integer.parseInt(jTextField2.getText());
    cm = new ColorMatrix(in, out);
    processImage();
  }

  public void jSlider2_stateChanged(ChangeEvent e) {
    int value = jSlider2.getValue();
    this.jTextField2.setText(Integer.toString(value));
    int in = Integer.parseInt(jTextField1.getText());
    int out = Integer.parseInt(jTextField2.getText());
    cm = new ColorMatrix(in, out);
    processImage();
  }

  public void jButton1_actionPerformed(ActionEvent e) {
    jFileChooser1.showOpenDialog(this);
    File file = this.jFileChooser1.getSelectedFile();
    if (file != null) {
      String filename = file.getAbsolutePath();
      PlanarImage image = JAI.create("fileload", filename);
      this.setImage(image);
    }
  }
}

class ColorMatrix {
  private int in, out;
  private double[][] matrix1, matrix2;
  private double offset1, offset2;
  public ColorMatrix(int in, int out) {
    this.in = in;
    this.out = out;
    double r1 = ( (double) out) / in;
    double r2 = - (r1 - 1) / 2;
    //matrix1 = new double[][]({ratio1,ratio2,ratio2},{},{}};
    matrix1 = new double[][] {
        {
        r1, r2, r2}, {
        r2, r1, r2}, {
        r2, r2, r1}
    };
    double r4 = (255. - out) / (255. - in);
    double r5 = 0;//-(1 - r4) / 2;
    matrix2 = new double[][] {
        {
        r4, r5, r5}, {
        r5, r4, r5}, {
        r5, r5, r4}
    };

    offset1 = -in;
    offset2 = out;
  }

  public int[] getRGB(int[] rgb) {
    //double[] rgbValues = new double[]{
    double[] rgbValues = IntArray.toDoubleArray(rgb);
    //double lumi = (DoubleArray.max(rgbValues) + DoubleArray.min(rgbValues)) / 2;
    double lumi = DoubleArray.max(rgbValues);
    //rgbValues = DoubleArray.times(matrix1, rgbValues);
    if (lumi <= in || out == 255) {
      rgbValues = DoubleArray.times(matrix1, rgbValues);
    }
    else {
      rgbValues = DoubleArray.plus(rgbValues, offset1);
//      rgbValues = rationalize(rgbValues);
      rgbValues = DoubleArray.times(matrix2, rgbValues);
//      rgbValues = rationalize(rgbValues);
      rgbValues = DoubleArray.plus(rgbValues, offset2);
    }
    rgbValues = rationalize(rgbValues);
    return DoubleArray.toIntArray(rgbValues);
  }

  private double[] rationalize(double[] rgbValues) {
    for (int x = 0; x < 3; x++) {
      rgbValues[x] = rgbValues[x] < 0 ? 0 : rgbValues[x];
      rgbValues[x] = rgbValues[x] > 255 ? 255 : rgbValues[x];
    }
    return rgbValues;
  }

}
