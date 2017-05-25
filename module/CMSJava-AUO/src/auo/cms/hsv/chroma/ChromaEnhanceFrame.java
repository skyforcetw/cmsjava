package auo.cms.hsv.chroma;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.VerticalFlowLayout;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import auo.cms.hsv.autotune.SingleHueAdjustValue;
import shu.cms.colorspace.independ.CIELCh;
import shu.cms.colorspace.depend.HSV;
import auo.cms.hsvinteger.IntegerHSVIP;
import shu.cms.colorspace.independ.CIELab;
import shu.cms.colorspace.independ.CIEXYZ;
import auo.cms.colorspace.depend.AUOHSV;
import shu.cms.colorspace.depend.RGB;
import auo.cms.hsv.saturation.IntegerSaturationFormula;
import flanagan.math.MinimizationFunction;
import flanagan.math.Minimization;

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
public class ChromaEnhanceFrame
    extends JFrame {
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JPanel jPanel1 = new JPanel();
  protected JPanel jPanel2 = new JPanel();
  protected JSlider jSlider1 = new JSlider();
  protected JPanel jPanel3 = new JPanel();
  protected JSlider jSlider2 = new JSlider();
  protected JSlider jSlider3 = new JSlider();
  protected JLabel jLabel1 = new JLabel();
  protected JLabel jLabel2 = new JLabel();
  protected JLabel jLabel3 = new JLabel();
  protected JLabel jLabel4 = new JLabel();
  protected JLabel jLabel5 = new JLabel();
  protected JLabel jLabel6 = new JLabel();
  protected JPanel jPanel4 = new JPanel();
  protected JLabel jLabel7 = new JLabel();
  protected JSlider jSlider4 = new JSlider();
  protected JLabel jLabel8 = new JLabel();
  public ChromaEnhanceFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(verticalFlowLayout1);
    jSlider2.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider2_stateChanged(e);
      }
    });
    jSlider1.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider1_stateChanged(e);
      }
    });
    jSlider2.setMaximum(64);
    jSlider2.setMinimum( -64);
    jSlider2.setValue(0);
    jSlider1.setMinimum( -64);
    jSlider1.setValue(0);
    jSlider3.setValue(0);
    jLabel8.setText("C");
    jSlider4.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider4_stateChanged(e);
      }
    });
    jSlider4.setMaximum(64);
    jSlider4.setMinimum( -64);
    jPanel4.add(jLabel8);
    jPanel4.add(jSlider4);
    jPanel4.add(jLabel7);
    jLabel7.setText("0");
    this.getContentPane().add(jPanel3);
    jLabel1.setText("H");
    jLabel2.setText("S");
    jLabel3.setText("V");
    jLabel4.setText("0");
    jLabel5.setText("0");
    jLabel6.setText("jLabel6");
    jSlider3.setMaximum(359);
    jSlider3.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        jSlider3_stateChanged(e);
      }
    });
    jPanel3.add(jLabel1);
    jPanel3.add(jSlider3);
    jPanel3.add(jLabel4);
    this.getContentPane().add(jPanel2);
    jPanel2.add(jLabel2);
    jPanel2.add(jSlider2);
    jPanel2.add(jLabel5);
    this.getContentPane().add(jPanel1);
    jPanel1.add(jLabel3);
    jPanel1.add(jSlider1);
    jPanel1.add(jLabel6);
    this.getContentPane().add(jPanel4);
    jSlider1.setMaximum(64);
    this.setSize(600, 600);
    jSlider4.setValue(0);
  }

  public static void main(String[] args) {
    SingleHueAdjustValue shav = new SingleHueAdjustValue( (short) 0, (byte) 14,
        (byte) 3);
    System.out.println(ChromaEnhanceFrame.calculateDeltaL(shav));
//    ChromaEnhanceFrame chromaenhanceframe = new ChromaEnhanceFrame();
//    chromaenhanceframe.setVisible(true);
  }

  public void jSlider3_stateChanged(ChangeEvent e) {
    this.jLabel4.setText(String.valueOf(this.jSlider3.getValue()));
    this.firePropertyChange("HSV", 0, 1);
  }

  public void jSlider2_stateChanged(ChangeEvent e) {
    this.jLabel5.setText(String.valueOf(this.jSlider2.getValue()));
    this.firePropertyChange("HSV", 0, 1);
  }

  public void jSlider1_stateChanged(ChangeEvent e) {
    this.jLabel6.setText(String.valueOf(this.jSlider1.getValue()));
    this.firePropertyChange("HSV", 0, 1);
  }

  public void jSlider4_stateChanged(ChangeEvent e) {
    int val = this.jSlider4.getValue();
    this.jLabel7.setText(String.valueOf(val));
    this.jSlider2.setValue(val);
    this.firePropertyChange("C", 0, 1);
    chromaAdjust();
  }

  static IntegerSaturationFormula integerSaturationFormula = new
      IntegerSaturationFormula( (byte) 7, 3);

  private void chromaAdjust() {
    int h = this.jSlider3.getValue();
    int hAdjust = (int) (h / 360. * 768);
    int sAdjust = this.jSlider2.getValue();
    Minimization minimization = new Minimization();
    minimization.addConstraint(0, 1, 64);
    minimization.addConstraint(0, -1, -64);
    minimization.nelderMead(new OptimizeFunction(hAdjust, sAdjust), new double[] {
      0
    }, new double[] {
        1});
    double minimum = minimization.getMinimum();
    double[] params = minimization.getParamValues();
    int vAdjust = (int) params[0];
    this.jSlider1.setValue(vAdjust);
    System.out.println(minimum + " " + params[0]);
  }

  class OptimizeFunction
      implements MinimizationFunction {
    OptimizeFunction(int h, int s) {
      this.h = h;
      this.s = s;
    }

    int h, s;
    /**
     * function
     *
     * @param param double[]
     * @return double
     */
    public double function(double[] param) {
      int vAdjust = (int) param[0];
      SingleHueAdjustValue adjustValue = new SingleHueAdjustValue( (short) h,
          (byte) s, (byte) vAdjust);

      return calculateDeltaL(adjustValue);
    }

  }

  private static double calculateDeltaL(SingleHueAdjustValue adjustValue) {

    RGB.ColorSpace colorspace = RGB.ColorSpace.sRGB_gamma22;
    CIEXYZ whiteXYZ = colorspace.getReferenceWhiteXYZ();
    int index = 0;
    double totalDeltaL = 0;
    double h = adjustValue.getDoubleHueAdjustValue();

    for (int s = 10; s <= 90; s += 10) {
      for (int v = 30; v <= 90; v += 10) {
        HSV hsv = new HSV(colorspace, new double[] {h, s, v});
        AUOHSV auohsv = new AUOHSV(hsv);
        short[] auohsvValues = IntegerHSVIP.getHSVValues(auohsv, adjustValue,
            integerSaturationFormula, false);
        AUOHSV hsv2 = AUOHSV.fromHSVValues3(auohsvValues);
        RGB rgb = hsv2.toRGB();
        CIEXYZ XYZ = rgb.toXYZ(colorspace);
        CIELab Lab = new CIELab(XYZ, whiteXYZ);

        RGB rgb0 = hsv.toRGB();
        CIEXYZ XYZ0 = rgb0.toXYZ(colorspace);
        CIELab Lab0 = new CIELab(XYZ0, whiteXYZ);

        double deltaL = Math.abs(Lab.L - Lab0.L);
        totalDeltaL += deltaL;
        index++;
      }
    }

    double averageDeltaL = totalDeltaL / index;
    return Math.abs(averageDeltaL);
  }

}
