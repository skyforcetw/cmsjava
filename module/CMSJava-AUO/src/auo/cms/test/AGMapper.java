package auo.cms.test;

import shu.cms.plot.*;
import shu.math.*;
import shu.math.lut.*;
///import shu.plot.*;

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
public class AGMapper {
  public static void main(String[] args) {
    double[] voltage = new double[] {
        6.13, 5.86, 4.12, 3.462, 2.242, 0.293, 0.253};
    double[] voltage2 = new double[] {
        6.83, 7.1, 8.82, 9.43, 10.61, 12.3, 12.31};
    double[] digitcount = new double[] {
        0, 1, 31, 127, 223, 254, 255};
    Interpolation1DLUT lut = new Interpolation1DLUT(digitcount, voltage2,
        Interpolation1DLUT.Algo.LINEAR);
    String filename = "Cal_Table_dot.xls";

    Plot2D p3 = Plot2D.getInstance(filename);
    double[] v3 = Maths.firstOrderDerivatives(voltage2);
    p3.addLinePlot("v'", 0, 254, v3);
    v3 = Maths.firstOrderDerivatives(v3);
    p3.addLinePlot("v\"", 0, 253, v3);
    //p3.addLinePlot("", new double[][] {digitcount, voltage2});
    //p3.addLinePlot("", new double[][] {digitcount, v3});
//    p3.addLinePlot("", 0, 254, v3);
    p3.addLegend();
    p3.setVisible();

    /*
      Plot2D p = Plot2D.getInstance(filename);
         Plot2D p2 = Plot2D.getInstance(filename);
     try {
      AUOCPTableXLSAdapter cp = new AUOCPTableXLSAdapter(filename,
          RGB.MaxValue.Int12Bit);
      List<RGB> rgbList = cp.getRGBList();
      int size = rgbList.size();
      for (int x = 0; x < size; x++) {
        RGB rgb = rgbList.get(x);
        double r = rgb.getValue(RGB.Channel.R, RGB.MaxValue.Double255);
        double g = rgb.getValue(RGB.Channel.G, RGB.MaxValue.Double255);
        double b = rgb.getValue(RGB.Channel.B, RGB.MaxValue.Double255);
        r = lut.getValue(r);
        g = lut.getValue(g);
        b = lut.getValue(b);
        System.out.println(x + " g: " + (r - g) + " b: " + (r - b));
        p.addCacheScatterLinePlot("g", Color.green, x, r - g);
        p.addCacheScatterLinePlot("b", Color.blue, x, r - b);
        p2.addCacheScatterLinePlot("r", Color.red, x, r);
        p2.addCacheScatterLinePlot("g", Color.green, x, g);
        p2.addCacheScatterLinePlot("b", Color.blue, x, b);

      }
         }
         catch (BiffException ex) {
      ex.printStackTrace();
         }
         catch (IOException ex) {
      ex.printStackTrace();
         }
         p.addLegend();
         p.setVisible();
         p2.addLegend();
         p2.setVisible();*/
  }
}
