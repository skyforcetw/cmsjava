package auo.cms.plot;

import shu.cms.plot.Plot2D;
import shu.plot.plots.GridPlot2D;
import shu.math.array.DoubleArray;
import java.awt.Color;

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
public class PlotUtils {
  public final static void addScatterPlotAndVectortoPlot(Plot2D plot2D,
      String name, Color c, GridPlot2D fromGridPlot, GridPlot2D toGridPlot) {

    double[][] dataTo = toGridPlot.getData();
    double[][] dataFrom = fromGridPlot.getData();
    if (dataTo.length != dataFrom.length) {
      throw new IllegalArgumentException("dataTo.length != dataFrom.length");
    }

    int size = dataTo.length;
    double[][] vecData = new double[size][];
    for (int x = 0; x < dataTo.length; x++) {
      vecData[x] = DoubleArray.minus(dataTo[x], dataFrom[x]);
    }
    int num = plot2D.addScatterPlot(name, c, dataFrom);
    plot2D.addVectortoPlot(num, vecData);

  }
}
