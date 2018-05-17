package shu.cms.plot;

import java.util.List;

import java.awt.*;
import java.awt.image.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.image.*;
import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.Axis;
import shu.plot.PlotBase;
import org.math.plot.PlotPanel;
import shu.plot.SkinInterface;

//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 提供一些常用的plot使用方式,減少重覆的code
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public final class PlotUtils {

  static Font FONT = new Font("Arial", Font.PLAIN, 20);
  static Font LIGHTFONT = new Font("Arial", Font.PLAIN, 14);

  public final static SkinInterface AUOSkin = new SkinInterface() {
    public void setSkin(PlotBase plotBase) {
      PlotPanel plotPanel = null;
      if (plotBase instanceof PlotWrapperInterface) {
        plotPanel = (PlotPanel) ( (PlotWrapperInterface) plotBase).
            getOriginalPlot().
            getPlotPanel();
      }
      else {
        plotPanel = (PlotPanel) plotBase.getPlotPanel();
      }
      int axisCount = plotPanel.getAxisCount();
      for (int x = 0; x < axisCount; x++) {
        Axis axis = plotPanel.getAxis(x);
        axis.setLabelFont(FONT);
        axis.setLightLabelFont(LIGHTFONT);
        axis.setColor(Color.black);

//        axis.setLightLabels();

      }

      plotBase.setLinePlotWidth(2);
//    plotPanel.plotLegend.setFont(LIGHTFONT);
      plotPanel.setFont(FONT);
    }
  };

  public final static void setAUOFormat(PlotBase plotBase) {
    PlotPanel plotPanel = null;
    if (plotBase instanceof PlotWrapperInterface) {
      plotPanel = (PlotPanel) ( (PlotWrapperInterface) plotBase).
          getOriginalPlot().
          getPlotPanel();
    }
    else {
      plotPanel = (PlotPanel) plotBase.getPlotPanel();
    }
    int axisCount = plotPanel.getAxisCount();
    for (int x = 0; x < axisCount; x++) {
      Axis axis = plotPanel.getAxis(x);
      axis.setLabelFont(FONT);
      axis.setLightLabelFont(LIGHTFONT);

    }

    plotBase.setLinePlotWidth(2);
//    plotPanel.plotLegend.setFont(LIGHTFONT);
    plotPanel.setFont(FONT);

//    plotBase.set
//    plotPanel.getAxis()
//    plotPanel.get
  }

  public final static Plot2D plotxy(Plot2D plot, String name, Color c,
                                    List<Patch> patchList) {
    return plotxy(plot, name, c, c, patchList);
  }

  public final static Plot2D plotxy(Plot2D plot, String name, Color xColor,
                                    Color yColor,
                                    List<Patch> patchList) {
    for (Patch p : patchList) {
      CIEXYZ XYZ = p.getXYZ();
      if (XYZ.isBlack()) {
        break;
      }
      double Y = XYZ.Y;
      double[] xyValues = XYZ.getxyValues();
      plot.addCacheScatterLinePlot(name != null ? name + "-x" : "x", xColor, Y,
                                   xyValues[0]);
      plot.addCacheScatterLinePlot(name != null ? name + "-y" : "y", yColor, Y,
                                   xyValues[1]);
    }
    plot.setAxeLabel(0, "Luminance");
    plot.setAxeLabel(1, "delta");
    plot.addLegend();
    plot.drawCachePlot();
    return plot;

  }

  public final static Plot2D plotxy(Plot2D plot, List<Patch> patchList) {
    return plotxy(plot, null, Color.red, Color.green, patchList);
  }

  public final static void setVisible(shu.plot.PlotWindow[] plots) {
    int size = plots.length;
    for (int x = 0; x < size; x++) {
      plots[x].setVisible();
    }
  }

  public final static void arrange(shu.plot.PlotWindow[] plots, int xCount,
                                   boolean keepHeight) {
    Dimension size = getSize(xCount, 1);
    int height = plots[0].getSize().height;

    for (int x = 0; x < xCount; x++) {
      int index = x;
      plots[index].setLocation(x * size.width, 0);
      if (keepHeight) {
        plots[index].setSize(size.width, height);
      }
      else {
        plots[index].setSize(size);
      }

    }
  }

  public final static void arrange(shu.plot.PlotWindow[] plots, int xCount,
                                   int yCount) {
    Dimension size = getSize(xCount, yCount);

    for (int x = 0; x < xCount; x++) {
      for (int y = 0; y < yCount; y++) {
        int index = y * xCount + x;
        plots[index].setSize(size);
        plots[index].setLocation(x * size.width, y * size.height);
      }
    }
  }

  public final static Dimension getSize(int xCount, int yCount) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) screenSize.getWidth() / xCount;
    int height = (int) (screenSize.getHeight() - 100) / yCount;
    return new Dimension(width, height);
  }

  public static void main(String[] args) {
//    PlotUtils plotutils = new PlotUtils();
//     RGBBase.ColorSpace colorspace = new ColorSpace()
    RGBBase.ColorSpace colorspace = new RGBBase.ColorSpace(Illuminant.D65,
        RGBBase.ColorSpace.GammaType.LStar,
        0.64, 0.33, 0.30, 0.60, 0.15, 0.06);

    int size = 600;
    Plot2D plot = Plot2D.getInstance("", size, size);

    Plot2DPanel panel = (Plot2DPanel) plot.getPlotPanel();
//    Axis axisx = panel.getAxis(0);

//    axisx.getba

    plot.setVisible();
    plot.setFixedBounds(0, 0, 1);
    plot.setFixedBounds(1, 0, 1);

    Image i = PlotUtils.getxyChromaticity(size, size, colorspace);
    plot.addImage(i, 1.f, new double[] {0, 0}, 1);

    PlotUtils.setAUOFormat(plot);

//    LocusPlot locus = new LocusPlot(plot);
//    locus.drawCIExyLocus(false);

//    int[] pcx = panel.getAxisPixelCount(0);
//    int[] pcy = panel.getAxisPixelCount(1);

//    try {
//      ImageUtils.storeJPEGImage("a.jpg", ImageUtils.toBufferedImage(i));
//    }
//    catch (Exception ex) {
//      ex.printStackTrace();
//    }
  }

  public final static Image getxyChromaticity(int width, int height,
                                              RGBBase.ColorSpace
                                              renderColorSpace) {
    int arr[][] = Diagram.compute(width, height, renderColorSpace);
    int pix[] = new int[width * height];
    int index = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        pix[index++] = arr[x][y];
      }
    }
    return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width,
        height, ColorModel.getRGBdefault(), pix, 0, width));

  }

  public final static Image getxyChromaticity(int width, int height) {
    return getxyChromaticity(width, height, RGBBase.ColorSpace.sRGB);
  }
}
