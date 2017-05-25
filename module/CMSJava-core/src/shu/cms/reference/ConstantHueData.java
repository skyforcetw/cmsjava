package shu.cms.reference;

import java.io.*;
import java.util.*;

import jxl.read.biff.*;
import shu.cms.colorformat.file.*;
import shu.cms.colorspace.independ.*;
import shu.cms.plot.*;
import shu.util.*;
import shu.util.log.*;
import shu.io.files.ExcelFile;

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
public class ConstantHueData {
  protected ExcelFile xls;

  private ConstantHueData() {
    try {
      xls = new ExcelFile(ConstantHueData.class.getResourceAsStream(
          "Ebner_Constant_Hue_Data.xls"));
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (BiffException ex) {
      Logger.log.error("", ex);
    }
  }

  protected CIEXYZ getCIEXYZ(int y) {
    double X = xls.getCell(1, y);
    double Y = xls.getCell(2, y);
    double Z = xls.getCell(3, y);
    return new CIEXYZ(X, Y, Z);

  }

  public CIEXYZ getWhitePoint() {
    return getCIEXYZ(0);
  }

  protected int[] hueArray;
  protected int[] hueIndexArray;

  public CIEXYZ[] getData(int hue) {
    int index = Searcher.sequentialSearch(hueArray, hue);
    if (index == -1) {
      return null;
    }
    int first = hueIndexArray[index];
    int end = hueIndexArray[index + 1];
    int size = end - first;
    CIEXYZ[] XYZArray = new CIEXYZ[size];
    for (int x = 0; x < size; x++) {
      XYZArray[x] = getCIEXYZ(x + first);
    }
    return XYZArray;
  }

  public int[] getHueData() {
    if (hueIndexArray == null) {
      List<Integer> hueList = new ArrayList<Integer> ();
      List<Integer> hueIndexList = new ArrayList<Integer> ();

      int size = xls.getRows();
      for (int x = 1; x < size; x++) {
        String cell = xls.getCellAsString(0, x);
        if (cell.length() != 0) {
          int hue = Integer.parseInt(cell.substring(cell.indexOf("hue") + 4));
          hueList.add(hue);
          hueIndexList.add(x);
        }
      }
      hueIndexList.add(size);
      hueArray = Utils.list2IntArray(hueList);
      hueIndexArray = Utils.list2IntArray(hueIndexList);
    }

    return hueArray;
  }

  public static void main(String[] args) {
    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);

    ConstantHueData constanthuedata = new ConstantHueData();
    CIEXYZ white = constanthuedata.getWhitePoint();
    int[] hueArray = constanthuedata.getHueData();
    for (int hue : hueArray) {
      for (CIEXYZ XYZ : constanthuedata.getData(hue)) {
        CIELab Lab = new CIELab(XYZ, white);
        CIELuv Luv = CIELuv.fromXYZ(XYZ, white);
//        plot.addCacheScatterPlot(Integer.toString(hue), Lab.a, Lab.b);
        plot.addCacheScatterPlot(Integer.toString(hue), Luv.u, Luv.v);
      }
    }
    plot.drawCachePlot();
  }
}
