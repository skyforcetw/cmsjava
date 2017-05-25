package auo.cms.test;

import java.io.*;
import shu.cms.lcd.LCDTarget;
import shu.cms.colorformat.adapter.TargetAdapter;
import shu.cms.lcd.LCDTargetBase.Number;
import java.util.List;
import shu.cms.colorformat.adapter.TargetAdapter.Style;
import shu.util.log.Logger;
import shu.cms.colorspace.independ.CIExyY;
import java.util.ArrayList;
import shu.cms.colorspace.independ.CIEXYZ;
import jxl.read.biff.BiffException;
import shu.cms.colorspace.depend.RGB;
import shu.io.files.ExcelFile;
import shu.cms.*;
import shu.math.array.*;
import shu.math.*;
import shu.plot.*;
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
public class MeasureErrorAnalyzer {

  static class Adapter
      extends TargetAdapter {

    public Adapter(String filename) {
      this.filename = filename;
    }

    private String filename;

    /**
     * estimateLCDTargetNumber
     *
     * @return Number
     */
    public Number estimateLCDTargetNumber() {
      return null;
    }

    /**
     * getAbsolutePath
     *
     * @return String
     */
    public String getAbsolutePath() {
      return "";
    }

    /**
     * getFileDescription
     *
     * @return String
     */
    public String getFileDescription() {
      return "";
    }

    /**
     * getFileNameExtension
     *
     * @return String
     */
    public String getFileNameExtension() {
      return "xls";
    }

    /**
     * getFilename
     *
     * @return String
     */
    public String getFilename() {
      return "";
    }

    /**
     * getPatchNameList
     *
     * @return List
     * @todo Implement this shu.cms.colorformat.adapter.TargetAdapter method
     */
    public List getPatchNameList() {
      return null;
    }

    /**
     * getRGBList
     *
     * @return List
     */
    public List getRGBList() {
      List<RGB> rgbList = new ArrayList<RGB> ();
      for (int x = 0; x <= 50; x++) {
        RGB rgb = new RGB(x, x, x);
        rgbList.add(rgb);
      }
      return rgbList;
    }

    /**
     * getReflectSpectraList
     *
     * @return List
     */
    public List getReflectSpectraList() {
      return null;
    }

    /**
     * getSpectraList
     *
     * @return List
     */
    public List getSpectraList() {
      return null;
    }

    /**
     * getStyle
     *
     * @return Style
     */
    public Style getStyle() {
      return Style.RGBXYZ;
    }

    /**
     * getXYZList
     *
     * @return List
     */
    public List getXYZList() {
      List<CIEXYZ> XYZList = new ArrayList<CIEXYZ> (51);
      int[][] indexArray = new int[][] {
          {
          9, 10, 11}, {
          12, 13, 14}, {
          15, 16, 17}, {
          1, 2, 3}
      };
      try {
        double[] xyYValues = new double[3];
        ExcelFile xls = new ExcelFile(filename);
        int rows = xls.getRows();

        {
          //white
          int[] index = indexArray[3];
          for (int x = rows - 1; x > 0; x--) {
            xyYValues[0] = xls.getCell(index[0], x);
            xyYValues[1] = xls.getCell(index[1], x);
            xyYValues[2] = xls.getCell(index[2], x);
            CIExyY xyY = new CIExyY(xyYValues);
            XYZList.add(xyY.toXYZ());
          }
        }

      }
      catch (FileNotFoundException ex) {
        Logger.log.error("", ex);
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }
      catch (BiffException ex) {
        Logger.log.error("", ex);
      }

      return XYZList;
    }

    /**
     * isInverseModeMeasure
     *
     * @return boolean
     */
    public boolean isInverseModeMeasure() {
      return false;
    }

    /**
     * probeParsable
     *
     * @return boolean
     */
    public boolean probeParsable() {
      return false;
    }

  }

  public static void main(String[] args) {
    File dir = new File(
        "D:\\ณnล้\\nobody zone\\exp data\\CCTv3\\111107\\model noise\\");
    String[] filenames = dir.list();

    int size = filenames.length;
    LCDTarget[] targets = new LCDTarget[size];
    for (int x = 0; x < size; x++) {
      String filename = dir.getAbsolutePath() + "/" + filenames[x];
//      LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(filename);
      Adapter adapter = new Adapter(filename);
      LCDTarget target = LCDTarget.Instance.get(adapter);
      targets[x] = target;
    }

    LCDTarget aveTarget = LCDTarget.Operator.average(targets);
    List<Patch> avePatchList = aveTarget.getPatchList();
    Plot2D plot = Plot2D.getInstance("ave 10");
    int patchcnt = avePatchList.size();
    for (int x = 1; x < patchcnt; x++) {
      double[] xy0 = avePatchList.get(x - 1).getXYZ().getxyValues();
      double[] xy1 = avePatchList.get(x).getXYZ().getxyValues();
      double[] dxy = DoubleArray.minus(xy1, xy0);
      DoubleArray.abs(dxy);
      plot.addCacheScatterLinePlot("dx", x, dxy[0]);
      plot.addCacheScatterLinePlot("dy", x, dxy[1]);
    }
    plot.setVisible();

    for (int ave = 2; ave <= 5; ave++) {
      Plot2D p = Plot2D.getInstance(Integer.toString(ave));
      int targetCount = size - ave + 1;
      LCDTarget[] aveTargets = new LCDTarget[targetCount];
      for (int x = 0; x < targetCount; x++) {
        LCDTarget[] prepareAve = new LCDTarget[ave];
        for (int n = 0; n < ave; n++) {
          int index = x + n;
          prepareAve[n] = targets[index];
        }
        aveTargets[x] = LCDTarget.Operator.average(prepareAve);
      }

      double[][] dx = new double[targetCount][];
      double[][] dy = new double[targetCount][];
      for (int x = 0; x < targetCount; x++) {
        LCDTarget t = aveTargets[x];
        List<Patch> patchList = t.getPatchList();
        dx[x] = new double[patchcnt];
        dy[x] = new double[patchcnt];
        for (int y = 0; y < patchcnt; y++) {
          double[] xy1 = patchList.get(y).getXYZ().getxyValues();
          double[] xy2 = avePatchList.get(y).getXYZ().getxyValues();
          double[] dxy = DoubleArray.minus(xy1, xy2);
          dx[x][y] = Math.abs(dxy[0]);
          dy[x][y] = Math.abs(dxy[1]);
        }
      }

      dx = DoubleArray.transpose(dx);
      dy = DoubleArray.transpose(dy);
      double[] avedx = new double[patchcnt];
      double[] avedy = new double[patchcnt];
      for (int x = 0; x < patchcnt; x++) {
//        avedx[x] = DoubleArray.mean
        avedx[x] = Maths.mean(dx[x]);
        avedy[x] = Maths.mean(dy[x]);
      }
      p.addLinePlot("dx", Color.red, 0, 50, avedx);
      p.addLinePlot("dy", Color.blue, 0, 50, avedy);
      p.setVisible();
      p.addLegend();
      p.setAxisLabels("Gray Level", "Delta Chromaticity Coordinator");
      p.setChartTitle("Average from " + ave + " Measurement");
      p.setFixedBounds(1, 0, 0.001);
      PlotUtils.setAUOFormat(p);
    }
  }
}
