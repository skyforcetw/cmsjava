package shu.cms.applet.measure;

import java.text.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.*;
import shu.cms.measure.*;
import shu.cms.measure.meter.*;
import shu.ui.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class UniformityMeasureFrame
    extends JFrame implements ActionListener {
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected static Font m_bFont = new Font("Arial", Font.BOLD, 28);
  protected PatchCanvas patchCanvas = new PatchCanvas();

  protected class PatchCanvas
      extends JComponent {

    protected FontMetrics fm;

    public void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (fm == null) {
        g.setFont(m_bFont);
        fm = g.getFontMetrics();
      }

      if (p != null) {
        g.setColor(c);
        g.fillRect(p.x, p.y, measureSize.width, measureSize.height);
        g.setColor(Color.red);
        g.drawRect(p.x, p.y, measureSize.width, measureSize.height);
      }
      else {
        g.clearRect(0, 0, screenSize.width, screenSize.height);
      }

      if (measureXYZ != null) {
        for (int x = 0; x < measureXYZ.length; x++) {
          CIEXYZ XYZ = measureXYZ[x];
          if (XYZ == null) {
            break;
          }
          else {
            CIELab Lab = CIELab.fromXYZ(XYZ, whitePoint);
            Point original = calculateOriginal(x);
            g.setColor(Color.white);
            String Y = "Y:  " + df.format(XYZ.Y) + " nits";
            String L = "CIE L:  " + df.format(Lab.L);
            double jndIndex = GSDF.DICOM.
                getJNDIndex(XYZ.Z);
            String jnd = "JNDIndex:  " + df.format(jndIndex);

            int w = fm.stringWidth(Y);
            int h = fm.getAscent();
            g.setFont(m_bFont);
            g.drawString(Y, original.x + measureSize.width / 2 - (w / 2),
                         original.y + measureSize.height / 2 + (h / 4));
            w = fm.stringWidth(L);
            g.drawString(L, original.x + measureSize.width / 2 - (w / 2),
                         original.y + h + measureSize.height / 2 + (h / 4));
            w = fm.stringWidth(jnd);
            g.drawString(jnd, original.x + measureSize.width / 2 - (w / 2),
                         original.y + h * 2 + measureSize.height / 2 + (h / 4));

          }
        }
      }
    }

    protected Point p;
    protected Color c;
  }

  protected static Dimension screenSize;
  static {
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  }

//  protected Dimension meterSize;
  protected Dimension measureSize;
  protected int w;
  protected int h;
  protected Meter meter;
  protected final static DecimalFormat df = new DecimalFormat("##.###");
  protected TinyDialog.Dialog exitDialog;

  public void actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }

  /**
   * 依照螢幕的大小計算測量的大小
   * @param meter Meter
   * @param LCDSize double
   */
  protected void calculateMeasureSize(Meter meter, double LCDSize) {
    Dimension meterSize = MeterMeasurement.getSize(meter.getType(), LCDSize);
    calculateMeasureSize(screenSize.width / meterSize.width,
                         screenSize.height / meterSize.height);
  }

  protected void calculateMeasureSize(int width, int height) {
    w = width;
    h = height;
    w = (w % 2 != 1) ? w - 1 : w;
    h = (h % 2 != 1) ? h - 1 : h;
    measureSize = new Dimension(screenSize.width / w, screenSize.height / h);

  }

  protected JOptionPane pane;
  protected JDialog dialog;

  protected void initDialog() {
    if (dialog == null) {
      pane = new JOptionPane("請將" + meter.getType().name() +
                             "放置在紅框裡,並按下 <確定> 進行量測.",
                             JOptionPane.INFORMATION_MESSAGE,
                             JOptionPane.OK_CANCEL_OPTION);
      dialog = pane.createDialog(this, "量測確認");
//      dialog.setUndecorated(true);
    }
  }

  protected CIEXYZ[] measureXYZ;
  protected CIEXYZ whitePoint;

  protected void measureReferenceWhite() {
    this.setBackground(Color.white);

    JOptionPane.showMessageDialog(this, "請將" + meter.getType().name() +
                                  "放置在螢幕上,並按下 <確定> 進行量測.",
                                  "參考白量測確認",
                                  JOptionPane.INFORMATION_MESSAGE);

    double[] XYZValues = meter.triggerMeasurementInXYZ();
    whitePoint = new CIEXYZ(XYZValues);
    this.setBackground(Color.black);
  }

  public java.util.List<Patch> measure(int code) {
    measureReferenceWhite();
    initDialog();
    int totalMeasure = w * h;
    measureXYZ = new CIEXYZ[totalMeasure];
    Color c = new Color(code, code, code);
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, c);
    java.util.List<Patch> patchList = new ArrayList<Patch> (totalMeasure);

    for (int x = 0; x < totalMeasure; x++) {
      drawRect(x, c);

      if (x >= ( (w / 2) * h)) {
        dialog.setLocation(screenSize.width / 5, dialog.getLocation().y);
      }
      else {
        dialog.setLocation( (screenSize.width / 3) * 2, dialog.getLocation().y);
      }
      dialog.setVisible(true);
      if (pane.getValue() == null ||
          ( (Integer) pane.getValue()).intValue() == 2) {
        exitDialog.setVisible(true);
        return patchList;
      }

      double[] XYZValues = meter.triggerMeasurementInXYZ();
      CIEXYZ XYZ = new CIEXYZ(XYZValues, this.whitePoint);
      measureXYZ[x] = XYZ;
      String patchName = Character.toString( (char) ('A' + (x / h))) +
          Integer.toString(x % h);
      Patch p = new Patch(patchName, XYZ, null, rgb);
      patchList.add(p);
    }
    patchCanvas.p = null;
    patchCanvas.repaint();
    exitDialog.setVisible(true);
    return patchList;

  }

  protected void drawRect(int index, Color c) {
    patchCanvas.p = calculateOriginal(index);
    patchCanvas.c = c;
    patchCanvas.repaint();
  }

  protected Point calculateOriginal(int index) {
    if (index >= w * h) {
      throw new IllegalArgumentException("index >= w * h");
    }
    int x = index / h;
    int y = index % h;

    return new Point(x * measureSize.width, y * measureSize.height);
  }

  public UniformityMeasureFrame(Meter meter, int width, int height) {
    this.meter = meter;
    calculateMeasureSize(width, height);
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    this.setVisible(true);
    MeasureUtils.meterCalibrate(this, meter);
  }

  public UniformityMeasureFrame(Meter meter, double LCDSize) {
    this.meter = meter;
    calculateMeasureSize(meter, LCDSize);
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    this.setVisible(true);
    MeasureUtils.meterCalibrate(this, meter);
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    getContentPane().add(patchCanvas, java.awt.BorderLayout.CENTER);
    this.setUndecorated(true); //不要邊框
    this.setSize(screenSize);
    this.getContentPane().setBackground(Color.black);
    this.setTitle("Uniformity Measure");
    this.setBackground(Color.white);
    exitDialog = TinyDialog.getExitDialogInstance(this, this);

  }

  public static void main(String[] args) {
//    System.out.println("xxxxx");
//    JOptionPane.showMessageDialog(null, "msg",
//                                  "title",
//                                  JOptionPane.INFORMATION_MESSAGE);
    DummyMeter meter = new DummyMeter();
//    EyeOneDisplay2 meter = new EyeOneDisplay2(Meter.ScreenType.LCD);
    UniformityMeasureFrame uniformitymeasurewindow = new
        UniformityMeasureFrame(meter, 24);
//    uniformitymeasurewindow.setVisible(true);
    uniformitymeasurewindow.measure(128);
//    uniformitymeasurewindow.setVisible(false);
//    uniformitymeasurewindow.dispose();
  }
}
