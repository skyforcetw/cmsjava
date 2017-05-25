package auo.cms.prefercolor.model;

import java.io.*;

import java.awt.*;

import org.math.io.files.*;
import org.math.plot.*;
import org.math.plot.plots.*;
import org.math.plot.render.*;
import auo.cms.prefercolor.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.devicemodel.lcd.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;

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
public class ExperimentAnalyzer {
  private LCDTarget lcdTarget;
  private MultiMatrixModel lcdModel;
  private String experimentDir;
  public ExperimentAnalyzer(LCDTarget lcdTarget, String experimentDir) {
    this.lcdTarget = lcdTarget;
    lcdModel = new MultiMatrixModel(lcdTarget);
    lcdModel.produceFactor();
    this.experimentDir = experimentDir;
  }

  private double[][][] memoryColorHSVData;
  private double[][][] memoryColorLabData;
  private double[][] averagMemoryColorLabData;

  public void analyze() {
    File[] files = new File(experimentDir).listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.indexOf(".txt") != -1;
      }
    });
    int size = files.length;
    memoryColorHSVData = new double[MemoryColorCount][size * ExperimentCount][];
    memoryColorLabData = new double[MemoryColorCount][size * ExperimentCount][];
    int index = 0;

    for (File f : files) {
//      if (!f.isFile()) {
//        continue;
//      }
      double[][] data = ASCIIFile.readDoubleArray(f);
      int dataSize = data.length;
      for (int x = 0; x < dataSize; x++) {
        HSV hsv = new HSV(RGB.ColorSpace.unknowRGB, data[x]);
        RGB rgb = hsv.toRGB();
        CIELab Lab = lcdModel.getLab(rgb, true);
        memoryColorHSVData[x % MemoryColorCount][x / MemoryColorCount +
            index * ExperimentCount] = hsv.getValues();
        memoryColorLabData[x % MemoryColorCount][x / MemoryColorCount +
            index * ExperimentCount] = Lab.getValues();
      }
      index++;
    }

    averagMemoryColorLabData = new double[MemoryColorCount][];
    for (int x = 0; x < MemoryColorCount; x++) {
      double[][] LabData = memoryColorLabData[x];
      double[][] LabDatap = DoubleArray.transpose(LabData);

      //========================================================================
      // plot ave data
      //========================================================================
      double averageL = Maths.mean(LabDatap[0]);
      double averagea = Maths.mean(LabDatap[1]);
      double averageb = Maths.mean(LabDatap[2]);
      averagMemoryColorLabData[x] = new double[] {
          averageL, averagea, averageb};
      //========================================================================
    }
  }

  public MemoryColorInterface getMemoryColorInterface() {
    MemoryColorInterface result = new MemoryColorInterface() {

      public CIEXYZ getReferenceWhiteXYZ() {
        return lcdModel.getWhiteXYZ();
      }

      public CIELab getSkin() {
        return new CIELab(averagMemoryColorLabData[0], lcdModel.getWhiteXYZ());
      }

      public CIELab getSky() {
        return new CIELab(averagMemoryColorLabData[1], lcdModel.getWhiteXYZ());
      }

      public CIELab getGrass() {
        return new CIELab(averagMemoryColorLabData[2], lcdModel.getWhiteXYZ());
      }

      public CIELab getFoliage() {
        return new CIELab(averagMemoryColorLabData[3], lcdModel.getWhiteXYZ());
      }

      public CIELab getOrange() {
        return new CIELab(averagMemoryColorLabData[5], lcdModel.getWhiteXYZ());
      }

      public CIELab getBanana() {
        return new CIELab(averagMemoryColorLabData[4], lcdModel.getWhiteXYZ());
      }

      /**
       * Returns a string representation of the object.
       *
       * @return a string representation of the object.
       * @todo Implement this java.lang.Object method
       */
      public String toString() {
        String string = "ReferenceWhiteXYZ: " + getReferenceWhiteXYZ()
            + "\nSkin: " + getSkin()
            + "\nSky: " + getSky()
            + "\nGrass: " + getGrass()
            + "\nFoliag: " + getFoliage()
            + "\nOrange: " + getOrange()
            + "\nBanana: " + getBanana();
        return string;
      }

    };
    return result;
  }

  private void plot(Plot2D plot, int memoryColorIndex) {
    Plot2DPanel plotPanel = (Plot2DPanel) plot.getPlotPanel();
    //========================================================================
    // plot exp data
    //========================================================================
    int LabDataSize = memoryColorLabData[0].length;
    for (int y = 0; y < LabDataSize; y++) {
      double[] hsvValues = memoryColorHSVData[memoryColorIndex][y];
      double[] LabValues = memoryColorLabData[memoryColorIndex][y];
      HSV hsv = new HSV(RGB.ColorSpace.unknowRGB, hsvValues);
      RGB rgb = hsv.toRGB();
      plot.addCacheScatterPlot(Integer.toString(memoryColorIndex), rgb.getColor(),
                               LabValues[1], LabValues[2]);
    }
    plot.drawCachePlot();
    //========================================================================

    //========================================================================
    // plot ave data
    //========================================================================
    double averagea = averagMemoryColorLabData[memoryColorIndex][1];
    double averageb = averagMemoryColorLabData[memoryColorIndex][2];
    int n = plot.addScatterPlot("mean" + Integer.toString(memoryColorIndex),
                                Color.black, averagea, averageb);
    ScatterPlot scatterPlot = (ScatterPlot) plotPanel.getPlot(n);
    scatterPlot.setDotPattern(AbstractDrawer.CROSS_DOT);
    scatterPlot.setRadius(CrossDotRadius);
    scatterPlot.setColor(Color.black);
    //=======================================================================
  }

  private void plotCIEMemoryColor(Plot2D plot, CIELab Lab) {
    Plot2DPanel plotPanel = (Plot2DPanel) plot.getPlotPanel();
    int n = plot.addScatterPlot(Lab.toString(), Color.red, Lab.a, Lab.b);
    ScatterPlot scatterPlot = (ScatterPlot) plotPanel.getPlot(n);
    scatterPlot.setDotPattern(AbstractDrawer.ROUND_DOT);
    scatterPlot.setRadius(CrossDotRadius);
    scatterPlot.setColor(Color.red);
  }

  private final static CIELab[] getLabArray(MemoryColorInterface
                                            memoryColorInterface) {
    CIELab[] LabArray = new CIELab[6];
    LabArray[5] = memoryColorInterface.getBanana();
    LabArray[3] = memoryColorInterface.getFoliage();
    LabArray[2] = memoryColorInterface.getGrass();
    LabArray[4] = memoryColorInterface.getOrange();
    LabArray[0] = memoryColorInterface.getSkin();
    LabArray[1] = memoryColorInterface.getSky();
    return LabArray;
  }

  public Plot2D plotLightness() {
    Plot2D plot = Plot2D.getInstance("Lightness");
//    MemoryColorPatches.get
    CIELab[] cieMemoryColor = getLabArray(
        MemoryColorPatches.getOrientalInstance());

    for (int x = 0; x < MemoryColorCount; x++) {
      double input = cieMemoryColor[x].L;
      double output = averagMemoryColorLabData[x][0];
      plot.addCacheScatterPlot("Lightness", input, output);
    }
    plot.setVisible();
    plot.setFixedBounds(0, 0, 100);
    plot.setFixedBounds(1, 0, 100);
    plot.setAxisLabels("input Lightness", "output Lightness");

    return plot;
  }

  public Plot2D plot() {
    Plot2D plot = Plot2D.getInstance("Memory Color");
    Plot2DPanel plotPanel = (Plot2DPanel) plot.getPlotPanel();
    for (int x = 0; x < MemoryColorCount; x++) {
      if (x == 3) {
        continue;
      }
      plot(plot, x);
    }

    //==========================================================================
    // axis
    //==========================================================================
    int n = plot.addLinePlot("x axis", Color.black, -80, 0, 60, 0);
    LinePlot linePlot = (LinePlot) plotPanel.getPlot(n);
    linePlot.line_width = 3;
    n = plot.addLinePlot("y axis", Color.black, 0, -80, 0, 100);
    linePlot = (LinePlot) plotPanel.getPlot(n);
    linePlot.line_width = 3;
    //==========================================================================


    MemoryColorInterface cie = MemoryColorPatches.getOrientalInstance();

    plotCIEMemoryColor(plot, cie.getBanana());
    plotCIEMemoryColor(plot, cie.getGrass());
    plotCIEMemoryColor(plot, cie.getOrange());
    plotCIEMemoryColor(plot, cie.getSkin());
    plotCIEMemoryColor(plot, cie.getSky());
    plot.setVisible();

    plot.setFixedBounds(0, -80, 60);
    plot.setFixedBounds(1, -80, 100);
    plot.setAxisLabels("a*", "b*");

    return plot;
  }

  public Plot2D plotFoliage() {
    Plot2D plot = Plot2D.getInstance("Foliage", 300, 300);
    Plot2DPanel plotPanel = (Plot2DPanel) plot.getPlotPanel();
    plot(plot, 3);

    MemoryColorInterface cie = MemoryColorPatches.getOrientalInstance();
    plotCIEMemoryColor(plot, cie.getFoliage());

    plot.setVisible();

    int n = plot.addLinePlot("x axis", Color.black, -80, 0, 60, 0);
    LinePlot linePlot = (LinePlot) plotPanel.getPlot(n);
    linePlot.line_width = 3;
    n = plot.addLinePlot("y axis", Color.black, 0, -80, 0, 100);
    linePlot = (LinePlot) plotPanel.getPlot(n);
    linePlot.line_width = 3;
    plot.setFixedBounds(0, -60, 0);
    plot.setFixedBounds(1, -20, 60);
    plot.setAxisLabels("a*", "b*");

    return plot;
  }

  public final static int CrossDotRadius = 5;
  public final static int MemoryColorCount = 6;
  public final static int ExperimentCount = 10;
  public static void main(String[] args) {
    LCDTarget eizoTarget = LCDTarget.Instance.getFromAUORampXLS(
        "psychophysics/eizo ramp.xls");
    LCDTarget.Operator.gradationReverseFix(eizoTarget);
    ExperimentAnalyzer analyzer = new ExperimentAnalyzer(eizoTarget,
        "psychophysics/data");
    analyzer.analyze();
    analyzer.plot();
    analyzer.plotFoliage();
    analyzer.plotLightness();
    MemoryColorInterface mci = analyzer.getMemoryColorInterface();
    System.out.println(mci);
  }
}
