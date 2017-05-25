package auo.cms.hsv.value.experiment;

import shu.cms.plot.*;
import auo.cms.hsv.value.ValueFormula;
import auo.cms.hsv.value.backup.ValuePrecisionEvaluator;

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
public class ValueFormulaPlotter {

  public static void main(String[] args) {
    Plot3D plot = Plot3D.getInstance();
    Plot3D plot2 = Plot3D.getInstance();
    double[][][] planeData = new double[21][21][];
    double[][][] planeData2 = new double[21][21][];
    ValueFormula.setInterpolateOffset(false);
    ValueFormula valueFormula=new ValueFormula();

    for (int value = 0; value <= 100; value += 5) {
      for (int sat = 0; sat <= 100; sat += 5) {
        short v = (short) (value / 100. * 1020);
        short s = (short) (sat / 100. * 1020);
//
        int c = (int) (v * s / 1040400. * 1020);
        int min = - (c - v);
//        System.out.println(v + " " + min);
        short vprime = valueFormula.getV(v, (short) min, (short)   50);
        short deltav = (short) (vprime - v);
        planeData[value / 5][sat / 5] = new double[] {
            value, sat, deltav};
        planeData2[value / 5][sat / 5] = new double[] {
            value, sat, vprime};
      }
    }
    plot.addPlanePlot("", planeData);
    plot.setVisible();
    plot.setAxisLabels("S", "V", "dV");

    plot2.addPlanePlot("", planeData2);
    plot2.setVisible();
    plot2.setAxisLabels("S", "V", "V");
    plot2.setFixedBounds(2, 0, 1020);
  }
}
