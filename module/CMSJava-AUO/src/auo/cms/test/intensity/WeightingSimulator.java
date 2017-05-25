package auo.cms.test.intensity;

import shu.cms.colorspace.depend.RGB;
import shu.cms.colorspace.independ.*;
import shu.math.array.*;
import shu.cms.plot.*;
import java.awt.Color;
import shu.math.Interpolation;
import shu.cms.colorformat.adapter.xls.AUOCPTableXLSAdapter;
import jxl.read.biff.BiffException;
import java.io.IOException;
import java.util.List;

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
public class WeightingSimulator {

  public static void main(String[] args) throws BiffException, IOException,
      InterruptedException {
    AUOCPTableXLSAdapter xls = new AUOCPTableXLSAdapter("debug.xls");
    List<CIEXYZ> XYZList = xls.getXYZList();
    CIEXYZ wXYZ = XYZList.get(0);

//    RGB.ColorSpace sRGB = RGB.ColorSpace.sRGB;
//    CIEXYZ wXYZ = RGB.White.toXYZ(sRGB);
    CIExyY rxyY = new CIExyY(0.6343119087827233, 0.3354135057725156,
                             80.00490570068359);
    CIExyY gxyY = new CIExyY(0.323630843266368, 0.6426228919431067,
                             382.2363586425781);
    CIExyY bxyY = new CIExyY(0.1451981782340157, 0.03935926846646249,
                             34.54988098144531);
    CIEXYZ rXYZ = rxyY.toXYZ();
    CIEXYZ gXYZ = gxyY.toXYZ();
    CIEXYZ bXYZ = bxyY.toXYZ();
    double[][] rgbMatrix = new double[][] {
        rXYZ.getValues(), gXYZ.getValues(), bXYZ.getValues()};
    rgbMatrix = DoubleArray.transpose(rgbMatrix);
    double[][] invMatrix = DoubleArray.inverse(rgbMatrix);
    double gamma = 2.2;
    Plot2D plot = Plot2D.getInstance();
    Plot2D xyplot = Plot2D.getInstance();

    double[][] targetxy = new double[256][2];
    double[] wxyValues = wXYZ.getxyValues();
//    double tol = 0.005;
    double tol = 0.012;
//    double tol = 0.009;
    for (int x = 0; x < 256; x++) {
      if (x >= 240 && x <= 247) {
        targetxy[x][0] = Interpolation.linear(240, 247, wxyValues[0],
                                              wxyValues[0] - tol, x);
        targetxy[x][1] = Interpolation.linear(240, 247, wxyValues[1],
                                              wxyValues[1] - tol, x);
      }
      else if (x >= 248 && x <= 254) {
        targetxy[x][0] = Interpolation.linear(248, 254, wxyValues[0] - tol,
                                              wxyValues[0], x);
        targetxy[x][1] = Interpolation.linear(248, 254, wxyValues[1] - tol,
                                              wxyValues[1], x);
      }
      else {
        targetxy[x][0] = wxyValues[0];
        targetxy[x][1] = wxyValues[1];
      }
      xyplot.addCacheScatterLinePlot("x", x, targetxy[x][0]);
      xyplot.addCacheScatterLinePlot("y", x, targetxy[x][1]);
    }
    xyplot.setVisible();

    for (int x = 0; x < 256; x++) {
      double normal = (x / 255.);
      double normalGamma = Math.pow(normal, gamma);
      CIExyY xyY = new CIExyY(targetxy[x][0], targetxy[x][1],
                              normalGamma * wXYZ.Y);

      double[] weighting = DoubleArray.times(invMatrix, xyY.toXYZ().getValues());
      plot.addCacheScatterLinePlot("r", Color.red, x, weighting[0]);
      plot.addCacheScatterLinePlot("g", Color.green, x, weighting[1]);
      plot.addCacheScatterLinePlot("b", Color.blue, x, weighting[2]);
    }
    plot.setVisible();
    plot.setFixedBounds(1, 0, 1);

    for (int x = 240; x <= 255; x++) {
      Plot2D p = Plot2D.getInstance(Integer.toString(x));

      double normal = (x / 255.);
      double normalGamma = Math.pow(normal, gamma);
      CIExyY xyY = new CIExyY(targetxy[x][0], targetxy[x][1], normalGamma);
      xyY.Y = normalGamma * wXYZ.Y;
      double[] targetWeighting = DoubleArray.times(invMatrix,
          xyY.toXYZ().getValues());
      for (int y = 0; y < 256; y++) {
        CIEXYZ XYZ = XYZList.get(y);
        double[] weighting = DoubleArray.times(invMatrix, XYZ.getValues());
        double[] intensity = DoubleArray.divide(weighting, targetWeighting);
        p.addCacheScatterLinePlot(y, intensity);
      }
      p.setVisible();

      p.addLinePlot("100%", Color.black, 0, 1, 255, 1);
      p.setFixedBounds(1, 0, 1);
      Thread.sleep(0);
    }
//    for(CIEXYZ XYZ:XYZList) {
//
//    }
  }
}
