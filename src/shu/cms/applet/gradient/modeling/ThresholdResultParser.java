package shu.cms.applet.gradient.modeling;

import java.io.*;
import java.io.File;
import java.util.*;
import java.util.List;

import java.awt.*;

import jxl.*;
import jxl.read.biff.*;
import shu.cms.colorformat.file.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.regress.*;
import shu.util.log.*;
//import shu.plot.*;

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
 * @deprecated
 */
public class ThresholdResultParser {
  public ThresholdResultParser(String dirname) {
    parseDir(dirname);
  }

  public Plot2D[] plot(double[] probabilitys) {
    List<Plot2D> plotList = new LinkedList<Plot2D> ();
    int judgeTimes = fileCount * 2;

    int size = probabilitys.length;
    List[] clickListArray = new List[size];
//    Color[] colors = new Color[] {
//        Color.red, Color.green, Color.blue, Color.cyan};

    for (int key : map2.keySet()) {
      Plot2D plot = Plot2D.getInstance(Integer.toString(key));
      plotList.add(plot);

      Map<Double, Click> clickmap = map2.get(key);

      for (int x = 0; x < probabilitys.length; x++) {
        List<Click> clickList = new LinkedList<Click> ();
        double probability = probabilitys[x];
        Color c = Plot2D.getNewColor(x);
//        Color c = colors[x];
        for (Click click : clickmap.values()) {
          double p = ( (double) click.nonokCount) / judgeTimes;
          if (p >= probability && click.delta > 0.1) {
            plot.addCacheScatterPlot(Double.toString(probability), c,
                                     click.jndi,
                                     click.delta);
            clickList.add(click);
          }
        }
        clickListArray[x] = clickList;
      }

      plot.setAxeLabel(0, "JNDI");
      plot.setAxeLabel(1, "delta");
      plot.drawCachePlot();
      double[] bounds = plot.getFixedBounds(0);

      //========================================================================
      // fitting
      //========================================================================
      for (int x = 0; x < probabilitys.length; x++) {
        List<Click> clickList = clickListArray[x];
//        Color c = colors[x];
        Color c = Plot2D.getNewColor(x);
        double probability = probabilitys[x];
        PolynomialRegression regress = regress(clickList);
        for (double jndi = bounds[0]; jndi <= bounds[1]; jndi++) {
          double delta = regress.getPredict(new double[] {jndi})[0];
          plot.addCacheScatterLinePlot(Double.toString(probability) + "fit", c,
                                       jndi,
                                       delta);
        }
      }
      //========================================================================

      plot.drawCachePlot();
      plot.setFixedBounds(0, 0, 400);
      plot.setFixedBounds(1, 0, 2);
      plot.addLegend();
      plot.setVisible();
    }
    return plotList.toArray(new Plot2D[plotList.size()]);
  }

  protected PolynomialRegression regress(List<Click> clickList) {
    Collections.sort(clickList);
    int size = clickList.size();

    List<Double> inputList = new ArrayList<Double> ();
    List<Double> outputList = new ArrayList<Double> ();
    double preoutput = Double.MAX_VALUE;

    for (int x = size - 1; x >= 0; x--) {
      Click click = clickList.get(x);
      if (click.delta <= preoutput) {
        preoutput = click.delta;
        inputList.add(click.jndi);
        outputList.add(click.delta);
      }
    }

    int listSize = inputList.size();
    double[] input = new double[listSize];
    double[] output = new double[listSize];

    for (int x = 0; x < listSize; x++) {
//      Click click = clickList.get(x);
      input[x] = inputList.get(x);
      output[x] = outputList.get(x);
    }

    Polynomial.COEF_1 coef = Polynomial.COEF_1.BY_1C;
//    Polynomial.COEF_1 coef =     PolynomialRegression.findBestPolynomialCoefficient1(input,output);
    PolynomialRegression regress = new PolynomialRegression(input, output, coef);
    regress.regress();
    return regress;
  }

  private int fileCount = 0;

  protected void parseDir(String dirname) {
    File dir = new File(dirname);
    for (String filename : dir.list()) {
      if (filename.lastIndexOf(".xls") != -1) {
        String absfilename = dir.getPath() + "/" + filename;
        parseFile(absfilename);
        fileCount++;
      }
    }
  }

  public static void main(String[] args) {
    ThresholdResultParser parser = new ThresholdResultParser("ExperimentResult");
    double[] probabilitys = new double[] {
        0.5, 0.75, 1};
    Plot2D[] plots = parser.plot(probabilitys);

//    PlotUtils.arrange(plots, 4, true);
  }

  public static class Click
      implements Comparable {

    Click(double jndi, double delta, int okCount, int nonokCount) {
      this.jndi = jndi;
      this.delta = delta;
      this.okCount = okCount;
      this.nonokCount = nonokCount;
    }

    int okCount = 0;
    int nonokCount = 0;
    double delta;
    double jndi;

    public String toString() {
      return jndi + "/" + delta + "/" + okCount + "/" + nonokCount;
    }

    /**
     * Compares this object with the specified object for order.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *   is less than, equal to, or greater than the specified object.
     */
    public int compareTo(Object o) {
      return Double.compare(this.jndi, ( (Click) o).jndi);
    }
  }

//  protected Map<Integer, List<Click>> map = new HashMap<Integer, List<Click>> ();
  protected Map<Integer, Map<Double, Click>> map2 = new Hashtable<Integer,
      Map<Double, Click>> ();

  /**
   *
   * @param filename String
   * @deprecated
   */
  protected void parseFile(String filename) {
    try {
      ExcelFile excelFile = new ExcelFile(filename);
      int size = excelFile.getRows();
      //========================================================================
      // 一個row
      //========================================================================
      for (int x = 0; x < size; x += 2) {
        Cell[] cells = excelFile.getRow(x);
        int cellsize = cells.length;
        String condition = cells[0].getContents();
        boolean inverse = condition.lastIndexOf("invers") != -1;
        ContrastThresholdJudge.CPCodeObject obj = new ContrastThresholdJudge.
            CPCodeObject(null, null, condition, 0, inverse);
        boolean nonok = false;
        Map<Double, Click> clickMap = map2.get( (int) obj.maxCode);
        if (clickMap == null) {
          clickMap = new HashMap<Double, Click> ();
          map2.put( (int) obj.maxCode, clickMap);
        }
        double delta = obj.delta;

        TreeSet<Double> nonokSet = new TreeSet<Double> ();
        TreeSet<Double> okSet = new TreeSet<Double> ();

        //========================================================================
        // 一欄
        //========================================================================
        for (int y = 1; y < cellsize; y++) {
          Cell cell = cells[y];
          CellType type = cell.getType();
          if (type == CellType.LABEL && cell.getContents().equals("ok<>non")) {
            nonok = true;
          }
          else if (type == CellType.NUMBER) {
            double jndi = Double.parseDouble(cell.getContents());
            if (nonok) {
              nonokSet.add(jndi);
            }
            else {
              okSet.add(jndi);
            }
          }
        }

        //======================================================================
        // 整理到map
        //======================================================================
        for (double jndi : nonokSet) {
          Click click = clickMap.get(jndi);
          if (click == null) {
            click = new Click(jndi, delta, 0, 1);
            clickMap.put(jndi, click);
          }
          else {
            click.nonokCount++;
          }
        }
        for (double jndi : okSet) {
          Click click = clickMap.get(jndi);
          if (click == null) {
            click = new Click(jndi, delta, 1, 0);
            clickMap.put(jndi, click);
          }
          else {
            click.okCount++;
          }
        }
        //======================================================================
        //========================================================================
      }
      //========================================================================
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (BiffException ex) {
      Logger.log.error("", ex);
    }
  }
}
