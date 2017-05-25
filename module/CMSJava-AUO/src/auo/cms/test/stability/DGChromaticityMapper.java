package auo.cms.test.stability;

import shu.io.files.ExcelFile;
import java.io.IOException;
import jxl.read.biff.BiffException;
import shu.math.array.DoubleArray;
import shu.cms.plot.Plot2D;
import java.awt.Color;
import shu.cms.plot.PlotUtils;

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
public class DGChromaticityMapper {

  public static void main(String[] args) throws IOException, BiffException {
    ExcelFile xls = new ExcelFile(
        "D:\\ณnล้\\nobody zone\\exp data\\CCTv3\\2012\\120611/stability(255-0, step1).xls");
    xls.selectSheet("Measure");
    int size = xls.getRows();
    int datasize = size - 1;
    double[] xarray = new double[datasize];
    double[] yarray = new double[datasize];
    for (int i = 1; i < size; i++) {
      double x = xls.getCell(7, i);
      double y = xls.getCell(8, i);
      xarray[i - 1] = x;
      yarray[i - 1] = y;
    }
    Plot2D plotx = Plot2D.getInstance("dx");
    Plot2D ploty = Plot2D.getInstance("dy");
    for (int gl = 50; gl > 25; gl--) {
      int row = (255 - gl) * 4;
      double x0 = xarray[row];
      double xr = xarray[row + 1];
      double xg = xarray[row + 2];
      double xb = xarray[row + 3];
      double[] dx = new double[] {
          x0 - xr, x0 - xg, x0 - xb};

      double y0 = yarray[row];
      double yr = yarray[row + 1];
      double yg = yarray[row + 2];
      double yb = yarray[row + 3];
      double[] dy = new double[] {
          y0 - yr, y0 - yg, y0 - yb};
      DoubleArray.abs(dx);
      DoubleArray.abs(dy);

      double maxdx = DoubleArray.max(dx);
      double maxdy = DoubleArray.max(dy);
      System.out.println(gl + " " + maxdx + " " + maxdy);
//      plot.addCacheScatterLinePlot("dx",gl,max);
      plotx.addCacheScatterLinePlot("r", Color.red, gl, dx[0]);
      plotx.addCacheScatterLinePlot("g", Color.green, gl, dx[1]);
      plotx.addCacheScatterLinePlot("b", Color.blue, gl, dx[2]);

      ploty.addCacheScatterLinePlot("r", Color.red, gl, dy[0]);
      ploty.addCacheScatterLinePlot("g", Color.green, gl, dy[1]);
      ploty.addCacheScatterLinePlot("b", Color.blue, gl, dy[2]);

    }
    plotx.setVisible();
    ploty.setVisible();
    plotx.setFixedBounds(0, 25, 50);
    plotx.setAxisLabels("Gray Level", "Delta Chromaticity");
    ploty.setFixedBounds(0, 25, 50);
    ploty.setAxisLabels("Gray Level", "Delta Chromaticity");
    plotx.addLegend();
    ploty.addLegend();
    PlotUtils.setAUOFormat(plotx);
    PlotUtils.setAUOFormat(ploty);
  }
}
