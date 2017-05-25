package shu.cms.plot;

import java.util.*;
import javax.vecmath.*;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.math.*;
import shu.math.geometry.*;

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
public class LocusPlot {
  public LocusPlot() {
    this("LocusPlot", 600, 600);
  }

  public LocusPlot(Plot2D plot2D) {
    this.plot2D = plot2D;
  }

  public LocusPlot(String title, int width, int height) {
    plot2D = Plot2D.getInstance(title, width, height);
  }

  public Plot2D getPlot2D() {
    return plot2D;
  }

  public final static xyCoordinateTransfer LabTrasnfer = new
      xyCoordinateTransfer() {
    public double[] getxyCoordinate(CIEXYZ XYZ) {
      XYZ = (CIEXYZ) XYZ.clone();
      XYZ.normalizeY();
      CIELab Lab = new CIELab(XYZ, Illuminant.D50WhitePoint);
      return new double[] {
          Lab.a, Lab.b};
    }

    public double[] getxyCoordinate(Spectra spectra) {
      return getxyCoordinate(spectra.getXYZ());
    }

    public String[] getxyCoordinateNames() {
      return new String[] {
          "x", "y"};
    }

    public boolean isSpectraOnly() {
      return false;
    }
  };

  public final static xyCoordinateTransfer xyTrasnfer = new
      xyCoordinateTransfer() {
    public double[] getxyCoordinate(CIEXYZ XYZ) {
      return XYZ.getxyValues();
    }

    public double[] getxyCoordinate(Spectra spectra) {
      return getxyCoordinate(spectra.getXYZ());
    }

    public String[] getxyCoordinateNames() {
      return new String[] {
          "x", "y"};
    }

    public boolean isSpectraOnly() {
      return false;
    }
  };

  public final static xyCoordinateTransfer uvpTrasnfer = new
      xyCoordinateTransfer() {
    public double[] getxyCoordinate(CIEXYZ XYZ) {
      return XYZ.getuvPrimeValues();
    }

    public double[] getxyCoordinate(Spectra spectra) {
      return getxyCoordinate(spectra.getXYZ());
    }

    public String[] getxyCoordinateNames() {
      return new String[] {
          "u'", "v'"};
    }

    public boolean isSpectraOnly() {
      return false;
    }

  };

  public final static xyCoordinateTransfer CIErgTrasnfer = new
      xyCoordinateTransfer() {
    public double[] getxyCoordinate(CIEXYZ XYZ) {
      throw new UnsupportedOperationException();
    }

    public double[] getxyCoordinate(Spectra spectra) {
      return spectra.getLMS(ConeFundamental.CIE_2007_10DEG_LMS).
          getChromaticityCoordinates();
    }

    public String[] getxyCoordinateNames() {
      return new String[] {
          "r", "g"};
    }

    public boolean isSpectraOnly() {
      return true;
    }

  };

  /**
   * 畫出xy色度圖的人眼色域邊界(馬蹄形圖)
   * @param color boolean
   */
  public void drawCIExyLocus(boolean color) {
    drawLocus(color, xyTrasnfer);
  }

  /**
   * 畫出xy色度圖的人眼色域邊界(馬蹄形圖)
   */
  public void drawCIExyLocus() {
    drawLocus(true, xyTrasnfer);
  }

  public void drawCIELabLocus() {
    drawLocus(true, LabTrasnfer);
  }

  public void drawCIEuvPrimeLocus(boolean color) {
    drawLocus(color, uvpTrasnfer);
  }

  private final static double[] _data = new double[471];
  public final static Spectra getSpectra(int waveLength) {
    Arrays.fill(_data, 0);
    _data[waveLength - 360] = 1;
    Spectra s = new Spectra("", Spectra.SpectrumType.EMISSION, 360, 830,
                            1, _data);
    return s;
  }

  public void drawLocus(boolean color, xyCoordinateTransfer transfer) {
    double[] lastxy = null;

    RGB.ColorSpace colorSpace = RGB.ColorSpace.WideGamutRGB;
    double[] start = null, end = null;
//      double[] end = getSpectra(830).getXYZ().getxyValues();

    for (int x = 360; x <= 830; x += 1) {
//      Arrays.fill(data, 0);
//      data[x - 360] = 1;
//      Spectra s = new Spectra("", Spectra.SpectrumType.EMISSION, 360, 830,
//                              1, data);
      Spectra s = getSpectra(x);
      CIEXYZ XYZ = s.getXYZ();
      if (x == 360) {
        start = XYZ.getxyValues();
      }
      if (x == 830) {
        end = XYZ.getxyValues();
      }

      double[] xy = transfer.getxyCoordinate(s);
      if (Double.isNaN(xy[0]) || Double.isNaN(xy[1])) {
        continue;
      }
      if (lastxy == null) {
        lastxy = xy;
      }
      else {

        if (color) {
          CIExyY xyY = CIExyY.fromXYZ(XYZ);
          xyY.Y = 1;
          RGB rgb = RGB.fromXYZ(xyY.toXYZ(), colorSpace, true);
          rgb.rationalize();
          Color c = rgb.getColor();
          plot2D.addLinePlot(null, c, lastxy[0],
                             xy[0], new double[] {lastxy[1], xy[1]});
        }
        else {
          plot2D.addCacheScatterLinePlot("Locus", Color.black, lastxy[0],
                                         lastxy[1]);
        }

        lastxy = xy;
      }
    }

    //==========================================================================
    // Magenta 2
    //==========================================================================
//    double[] data = new double[471];
    if (!transfer.isSpectraOnly()) {
//      Arrays.fill(data, 0);
//      data[0] = 1;
//      Spectra bSpectra = new Spectra("", Spectra.SpectrumType.EMISSION, 360,
//                                     830,
//                                     1, DoubleArray.copy(data));
//      Arrays.fill(data, 0);
//      data[data.length - 1] = 1;
//      Spectra rSpectra = new Spectra("", Spectra.SpectrumType.EMISSION, 360,
//                                     830,
//                                     1, DoubleArray.copy(data));
//
//      double[] start = bSpectra.getXYZ().getxyValues();
//      double[] end = rSpectra.getXYZ().getxyValues();

//      double[] start = getSpectra(360).getXYZ().getxyValues();
//      double[] end = getSpectra(830).getXYZ().getxyValues();

      for (double rate = 0.1; rate <= 1; rate += 0.1) {
        double startx = (end[0] - start[0]) * (rate - 0.1) + start[0];
        double starty = (end[1] - start[1]) * (rate - 0.1) + start[1];
        double endx = (end[0] - start[0]) * rate + start[0];
        double endy = (end[1] - start[1]) * rate + start[1];
//        int d = (int) (510 * rate);
//        int b = d > 255 ? 510 - d : 255;
        CIExyY startxyY = new CIExyY(startx, starty, 1);
        CIExyY endxyY = new CIExyY(endx, endy, 1);
        double[] startxy = transfer.getxyCoordinate(startxyY.toXYZ());
        double[] endxy = transfer.getxyCoordinate(endxyY.toXYZ());

        if (color) {
          RGB rgb = RGB.fromXYZ(startxyY.toXYZ(), colorSpace, true);
          Color c = rgb.getColor();

          plot2D.addLinePlot(null, c, startxy[0],
                             endxy[0], new double[] {startxy[1], endxy[1]});
        }
        else {
          plot2D.addCacheScatterLinePlot("Locus", Color.black, startxy[0],
                                         startxy[1]);
        }
      }
    }
    //==========================================================================
    plot2D.drawCachePlot();

    setAxeLabel(transfer);
  }

  protected void setAxeLabel(xyCoordinateTransfer transfer) {
    String[] names = transfer.getxyCoordinateNames();
    int size = names.length;
    for (int x = 0; x < size; x++) {
      this.plot2D.setAxeLabel(x, names[x]);
    }
  }

  protected Plot2D plot2D;

  public void setVisible(boolean visible) {
    plot2D.setVisible(visible);
//    plot2D.setFixedBounds(0, 0, 0.9);
//    plot2D.setFixedBounds(1, 0, 0.9);
  }

  public void setVisible() {
    setVisible(true);
  }

  public static void main(String[] args) {
    LocusPlot p = new LocusPlot();
    p.drawCIExyLocus();
    p.setVisible();

//    LinearFunction lf = LinearFunction.getInstance(new double[] {
//        0.1754825277104071, 0.005286339105915228},
//        new double[] {0.33331438077735165, 0.3332877057993168});
//    lf.getY(0.1754825277104071);
//
//    Plot2D pp = Plot2D.getInstance();
//    pp.addScatterPlot("", 0.1754825277104071,
//                      0.005286339105915228);
//    pp.addScatterPlot("", 0.33331438077735165,
//                      0.3332877057993168);
//    pp.setVisible();

//    p.drawCIELabLocus();
//    p.drawLocus(true, new XYZColorSpaceTransfer() {
//      public double[] getxyCoordinate(CIEXYZ XYZ) {
//        return XYZ.getuvValues();
////        return XYZ.getxyValues();
//      }
//
//      public String[] getxyCoordinateNames() {
//        return new String[] {
//            "x", "y"};
//      }
//    }
//    );
//    p.setVisible();
  }

  public void drawGamutTriangle(String name, LCDTarget lcdTarget,
                                Color color, xyCoordinateTransfer transfer) {
    CIEXYZ rXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.R).getXYZ();
    CIEXYZ gXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.G).getXYZ();
    CIEXYZ bXYZ = lcdTarget.getSaturatedChannelPatch(RGB.Channel.B).getXYZ();
    drawGamutTriangle(name, new CIExyY(rXYZ), new CIExyY(gXYZ), new CIExyY(bXYZ),
                      color, transfer);
  }

  public void drawGamutTriangle(String name, RGB.ColorSpace rgbColorSpace,
                                Color color, xyCoordinateTransfer transfer) {
    CIExyY rxyY = new CIExyY(rgbColorSpace.rx, rgbColorSpace.ry);
    CIExyY gxyY = new CIExyY(rgbColorSpace.gx, rgbColorSpace.gy);
    CIExyY bxyY = new CIExyY(rgbColorSpace.bx, rgbColorSpace.by);
    drawGamutTriangle(name, rxyY, gxyY, bxyY, color, transfer);
  }

  private final static double[] interpolate(double[] xyValues1,
                                            double[] xyValues2, int xn, int x) {
    double resultX = Interpolation.linear(0, xn, xyValues1[0], xyValues2[0], x);
    double resultY = Interpolation.linear(0, xn, xyValues1[1], xyValues2[1], x);
    double[] result = new double[] {
        resultX, resultY};
    return result;
  }

  /**
   *
   * @param xyValues1 double[]
   * @param xyValues2 double[]
   * @param step int
   * @param color Color
   * @param plot Plot2D
   * @todo
   */
  private final static void drawLine(double[] xyValues1, double[] xyValues2,
                                     int step, Color color, Plot2D plot) {
    for (int x = 0; x < step; x++) {
      double[] result0 = interpolate(xyValues1, xyValues2, step, x);
      double[] result1 = interpolate(xyValues1, xyValues2, step, x + 1);
//      plot.addCacheScatterLinePlot();
//      System.out.println(Arrays.toString(result1));
    }
  }

  public void drawGamutTriangle(String name, CIExyY rxyY, CIExyY gxyY,
                                CIExyY bxyY, Color color,
                                xyCoordinateTransfer transfer) {
    double[] rxyValues = transfer.getxyCoordinate(rxyY.toXYZ());
    double[] gxyValues = transfer.getxyCoordinate(gxyY.toXYZ());
    double[] bxyValues = transfer.getxyCoordinate(bxyY.toXYZ());

    Point2d r = new Point2d(rxyValues);
    Point2d g = new Point2d(gxyValues);
    Point2d b = new Point2d(bxyValues);
    double rg = Geometry.getDistance(r, g);
    double gb = Geometry.getDistance(g, b);
    double rb = Geometry.getDistance(r, b);

    int rgStep = (int) (rg / 0.001);
    int gbStep = (int) (gb / 0.001);
    int rbStep = (int) (rb / 0.001);

//    drawLine(rxyValues, gxyValues, rgStep, color, plot2D);

//    for (int x = 0; x < rgStep; x++) {
//      interpolate(rxyValues, gxyValues, rgStep - 1, x);
//      interpolate(rxyValues, gxyValues, rgStep - 1, x);
//      interpolate(rxyValues, gxyValues, rgStep - 1, x);
//      Interpolation2D.bilinear()
//      Interpolation.interpolate(new double[]{0,rgStep-1},)

//    }

    plot2D.addLinePlot(name, color, new double[][] { {rxyValues[0], rxyValues[1]},
                       {gxyValues[0], gxyValues[1]}, {bxyValues[0], bxyValues[1]},
                       {rxyValues[0], rxyValues[1]}
    });

//    plot2D.addLinePlot(name, color, rxyValues[0], rxyValues[1], gxyValues[0],
//                       gxyValues[1]);
//    plot2D.addLinePlot("a", color, gxyValues[0], gxyValues[1], bxyValues[0],
//                       bxyValues[1]);
//    plot2D.addLinePlot("", color, rxyValues[0], rxyValues[1], bxyValues[0],
//                       bxyValues[1]);
  }
}
