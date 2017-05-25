package auo.cms.hsv.value.applet;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import auo.cms.hsv.value.backup.ValuePrecisionEvaluator;
import shu.cms.plot.Plot3D;
import auo.cms.hsv.value.*;

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


public class Applet1
    extends Applet {
  protected boolean isStandalone = false;
  protected BorderLayout borderLayout1 = new BorderLayout();

  //Get a parameter value
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
        (getParameter(key) != null ? getParameter(key) : def);
  }

  //Construct the applet
  public Applet1() {
  }

  //Initialize the applet
  public void init() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  //Component initialization
  private void jbInit() throws Exception {

    Plot3D plot = Plot3D.getInstance();
    Plot3D plot2 = Plot3D.getInstance();
    double[][][] planeData = new double[21][21][];
    double[][][] planeData2 = new double[21][21][];
    ValueFormula.setInterpolateOffset(false);
    ValueFormulaIF valueFormula = new ValueFormula();
//    ValueFormulaIF valueFormula = new OriginalValueFormula();

    for (int value = 0; value <= 100; value += 5) {
      for (int sat = 0; sat <= 100; sat += 5) {
        short v = (short) (value / 100. * 1020);
        short s = (short) (sat / 100. * 1020);
//
        int c = (int) (v * s / 1040400. * 1020);
        int min = - (c - v);
//        System.out.println(v + " " + min);
        short vprime = valueFormula.getV(v, (short) min, (short) 50);
        short deltav = (short) (vprime - v);
//        deltav=(sat==100)?50:deltav;
        planeData[value / 5][sat / 5] = new double[] {
            value, sat, deltav};
        planeData2[value / 5][sat / 5] = new double[] {
            value, sat, vprime};
      }
    }
    plot.addPlanePlot("", planeData);
    plot.setVisible();
    plot.setAxisLabels("V", "S", "dV");

    plot2.addPlanePlot("", planeData2);
    plot2.setVisible();
    plot2.setAxisLabels("S", "Vin", "Vout");
    plot2.setFixedBounds(2, 0, 1020);

  }

  //Get Applet information
  public String getAppletInfo() {
    return "Applet Information";
  }

  //Get parameter info
  public String[][] getParameterInfo() {
    return null;
  }

  //Main method
  public static void main(String[] args) {
    Applet1 applet = new Applet1();
    applet.isStandalone = true;

    Frame frame;
    frame = new Frame() {
      protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
          System.exit(0);
        }
      }

      public synchronized void setTitle(String title) {
        super.setTitle(title);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      }
    };
    ;
    frame.setTitle("Applet Frame");

    frame.add(applet, BorderLayout.CENTER);

    applet.init();
    applet.start();
    frame.setSize(600, 620);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation( (d.width - frame.getSize().width) / 2,
                      (d.height - frame.getSize().height) / 2);
    frame.setVisible(true);
  }
}
