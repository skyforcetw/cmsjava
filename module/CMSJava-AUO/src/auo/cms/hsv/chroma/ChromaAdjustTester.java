package auo.cms.hsv.chroma;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import auo.cms.colorspace.depend.AUOHSV;
import auo.cms.hsvinteger.IntegerHSVIP;
import auo.cms.hsv.autotune.SingleHueAdjustValue;
import auo.cms.hsv.saturation.IntegerSaturationFormula;

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
public class ChromaAdjustTester {

  private static CIELCh getLCh(HSV hsv, RGB.ColorSpace colorspace) {
    RGB rgb = hsv.toRGB();
    CIEXYZ XYZ = rgb.toXYZ();
    CIELab Lab = new CIELab(XYZ, colorspace.getReferenceWhiteXYZ());
    CIELCh LCh = new CIELCh(Lab);
    return LCh;
  }

  public static void main(String[] args) {
    chromaEnhanceTest(args);
  }

  public static void chromaEnhanceTest(String[] args) {
    ChromaEnhanceFrame frame = new ChromaEnhanceFrame();
    frame.addPropertyChangeListener(new Listener(frame));
    frame.setVisible(true);
  }
}

class Listener
    implements PropertyChangeListener {
  ChromaEnhanceFrame frame;
  Plot3D plot = Plot3D.getInstance();
  Plot3D plot2 = Plot3D.getInstance();
  int h = 0;
  SingleHueAdjustValue adjustValue;
  IntegerSaturationFormula integerSaturationFormula = new
      IntegerSaturationFormula( (byte) 7, 3);

  Listener(ChromaEnhanceFrame frame) {
    this.frame = frame;
    adjustValue = new SingleHueAdjustValue( (short) 0, (byte) 0, (byte) 0);
    plot();
    plot.setVisible();
    plot2.setVisible();
  }

  void plot() {
    double[][][] LChPlaneData = new double[11][11][];
    double[][][] hsvPlaneData = new double[11][11][];
    RGB.ColorSpace colorspace = RGB.ColorSpace.sRGB;
    CIEXYZ whiteXYZ = colorspace.getReferenceWhiteXYZ();
    int index = 0;
    double totalDeltaL = 0, totalDeltaC = 0;

    for (int s = 0; s <= 100; s += 10) {
      for (int v = 0; v <= 100; v += 10) {
        HSV hsv = new HSV(colorspace, new double[] {h, s, v});
        AUOHSV auohsv = new AUOHSV(hsv);
        short[] auohsvValues = IntegerHSVIP.getHSVValues(auohsv, adjustValue,
            integerSaturationFormula, false);
        AUOHSV hsv2 = AUOHSV.fromHSVValues3(auohsvValues);

        RGB rgb = hsv2.toRGB();
        CIEXYZ XYZ = rgb.toXYZ(colorspace);
        CIELab Lab = new CIELab(XYZ, whiteXYZ);
        CIELCh LCh = new CIELCh(Lab);

        double hue = (h > 315) ? (LCh.h < 150) ? LCh.h + 360 : LCh.h : LCh.h;
        hsvPlaneData[s / 10][v / 10] = new double[] {
            hsv2.getHueInDegree(), hsv2.saturation, hsv2.value};
        LChPlaneData[s / 10][v / 10] = new double[] {
            hue, LCh.C, LCh.L};

        RGB rgb0 = hsv.toRGB();
        CIEXYZ XYZ0 = rgb0.toXYZ(colorspace);
        CIELab Lab0 = new CIELab(XYZ0, whiteXYZ);
        CIELCh LCh0 = new CIELCh(Lab0);

        double deltaL = LCh.L - LCh0.L;
        double deltaC = LCh.C - LCh0.C;
        totalDeltaL += deltaL;
        totalDeltaC += deltaC;
        index++;
        if (s >= 10 && s <= 90 && v >= 30 && v <= 90) {
          double hue0 = (h > 315) ? (LCh0.h < 150) ? LCh0.h + 360 : LCh0.h :
              LCh0.h;
          plot.addCacheScatterPlot("org", HSV.getLineColor(h), hue0, LCh0.C,
                                   LCh0.L);
        }
      }
    }

    //==========================================================================
    // plane data re-arrange
    //==========================================================================
    for (int x = 0; x <= 100; x += 10) {
      LChPlaneData[0][x / 10][0] = LChPlaneData[1][x / 10][0];
    }
    for (int x = 0; x <= 100; x += 10) {
      LChPlaneData[x / 10][0][0] = LChPlaneData[x / 10][1][0];
    }
    //==========================================================================

    plot.removeAllPlots();
    plot.addPlanePlot("", HSV.getLineColor(h), LChPlaneData);
    plot.drawCachePlot();
    plot.setFixedBounds(0, 0, 360);
    plot.setFixedBounds(1, 0, 120);
    plot.setFixedBounds(2, 0, 100);
    plot.setAxisLabels("h*", "C*", "L*");


    plot2.removeAllPlots();
    plot2.addPlanePlot("", HSV.getLineColor(h), hsvPlaneData);
    plot2.setFixedBounds(0, 0, 360);
    plot2.setFixedBounds(1, 0, 1020);
    plot2.setFixedBounds(2, 0, 1020);
    plot2.setAxisLabels("H", "S", "V");
//    plot2.rotateToAxis(1);
  }

  /**
   * This method gets called when a bound property is changed.
   *
   * @param evt A PropertyChangeEvent object describing the event source and
   *   the property that has changed.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    h = frame.jSlider3.getValue();
    int s = frame.jSlider2.getValue();
    int v = frame.jSlider1.getValue();
//    System.out.println(h);
    adjustValue = new SingleHueAdjustValue( (short) (h / 360. * 768), (byte) s,
                                           (byte) v);
    plot();
//    System.out.println(evt);
//    System.out.println(evt.getPropertyName());
  }

}
