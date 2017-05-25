package shu.plot;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Image;

/**
 * <p>Title: Colour Management System - static</p>
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
public interface PlotInterface {
  public abstract void setChartTitle(String title);

  public abstract void addLegend();

  public abstract void addLegend(String o);

  public abstract void setFixedBounds(int axe, double min, double max);

  public abstract double[] getFixedBounds(int axe);

  public abstract JPanel getPlotPanel();

  public abstract void removeAllPlots();

  public abstract void removePlot(int index);

  public abstract void setPlotVisible(boolean b);

  public abstract void setPlotVisible(int num, boolean b);

  public abstract int getPlotSize();

  public abstract int addLinePlot(String name, Color c, double[] ...XY);

  public abstract void addImage(Image img, float alpha, double[] xySW,
                                double[] xySE, double[] xyNW);

  public abstract int addScatterPlot(String name, Color c, double[] ...XY);

  public abstract void addVectortoPlot(int numPlot, double[][] v);

  public abstract void setAxisVisible(boolean v);

  public abstract void setAxisVisible(int axe, boolean v);

  public abstract void setGridVisible(boolean v);

  public abstract void setGridVisible(int axe, boolean v);

  public abstract void setAxeLabel(int axe, String label);

  public abstract void setAxisScale(int axe, Scale scale);

  public abstract void setLinePlotDrawDot(boolean drawDot);

  public abstract void setLinePlotWidth(int lineWidth);

  public abstract void setLineType(LineType lineType);

  public abstract boolean setScatterPlotPattern(int index, Pattern pattern);

  public abstract boolean setDotRadius(int index, int radius);

  public abstract boolean setDotFill(int index, DotFill dotFill);

  public abstract int getAxisCount();

  public static enum Pattern {
    Round, Cross, Square, X;

    Pattern() {

    }

  }

  public static enum DotFill {
    Whole, Inside, None, Gradation;
  }

  public static enum LineType {
    Continous(1), Dotted(2);

    public int index;
    private LineType(int index) {
      this.index = index;
    }

  }

  public static enum Scale {
    Linear("LIN"), Log("LOG");

    Scale(String label) {
      this.label = label;
    }

    public String label;
  }

}
