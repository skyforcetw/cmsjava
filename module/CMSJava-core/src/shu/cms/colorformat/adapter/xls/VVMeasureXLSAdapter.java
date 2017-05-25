package shu.cms.colorformat.adapter.xls;

import java.io.*;
import java.io.File;
import java.util.*;

import jxl.read.biff.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorformat.adapter.TargetAdapter.Style;
import shu.cms.colorformat.file.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
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
public class VVMeasureXLSAdapter
    extends TargetAdapter {

  protected File file;

  public VVMeasureXLSAdapter(String filename) {
    file = new File(filename);
  }

  /**
   * estimateLCDTargetNumber
   *
   * @return Number
   */
  public LCDTargetBase.Number estimateLCDTargetNumber() {
    LCDTargetBase.Number number = LCDTargetBase.Number.getNumber(getRGBList().
        size());
    return number;
  }

  /**
   * getAbsolutePath
   *
   * @return String
   */
  public String getAbsolutePath() {
    return file.getAbsolutePath();
  }

  /**
   * getFileDescription
   *
   * @return String
   */
  public String getFileDescription() {
    return "XLS Measure Excel File";
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
    return file.getName();
  }

  /**
   * getPatchNameList
   *
   * @return List
   */
  public List<String> getPatchNameList() {
    List<String> list = new LinkedList<String> ();
    try {
      ExcelFile xls = new ExcelFile(file, false);
      int rows = xls.getRows();

      for (int x = 3; x < rows; x++) {
        String name = xls.getCellAsString(0, x);
        list.add(name);
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

    return list;
  }

  /**
   * getRGBList
   *
   * @return List
   */
  public List<RGB> getRGBList() {
    if (rgbList == null) {
      rgbList = new LinkedList<RGB> ();
      try {
        double[] RGBValues = new double[3];
        ExcelFile xls = new ExcelFile(file, false);
        int rows = xls.getRows();

        for (int x = 3; x < rows; x++) {

          RGBValues[0] = xls.getCell(1, x);
          RGBValues[1] = xls.getCell(2, x);
          RGBValues[2] = xls.getCell(3, x);
          RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, RGBValues,
                            RGB.MaxValue.Double255);
          rgbList.add(rgb);
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
    }
    return rgbList;
  }

  /**
   * getReflectSpectraList
   *
   * @return List
   */
  public List getReflectSpectraList() {
    throw new UnsupportedOperationException();
  }

  /**
   * getSpectraList
   *
   * @return List
   */
  public List getSpectraList() {
    throw new UnsupportedOperationException();
  }

  /**
   * getStyle
   *
   * @return Style
   */
  public Style getStyle() {
    return Style.RGBXYZ;
  }

  protected List<RGB> rgbList = null;
  protected List<CIEXYZ> XYZList = null;

  /**
   * getXYZList
   *
   * @return List
   */
  public List getXYZList() {
    if (XYZList == null) {
      XYZList = new LinkedList<CIEXYZ> ();
      try {
        double[] XYZValues = new double[3];
        ExcelFile xls = new ExcelFile(file, false);
        int rows = xls.getRows();

        for (int x = 3; x < rows; x++) {

          XYZValues[0] = xls.getCell(5, x);
          XYZValues[1] = xls.getCell(6, x);
          XYZValues[2] = xls.getCell(7, x);
          CIEXYZ XYZ = new CIEXYZ(XYZValues);
          XYZList.add(XYZ);
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
    }
    return XYZList;
  }

  /**
   * probeParsable
   *
   * @return boolean
   * @todo Implement this shu.cms.colorformat.adapter.TargetAdapter method
   */
  public boolean probeParsable() {
    return false;
  }

  public static void main(String[] args) {
    VVMeasureXLSAdapter ad = new VVMeasureXLSAdapter(
        "Measurement Files/Monitor/cpt_320WF01SC/k10/darkroom/native/1021+4096+4108_0909.xls");
//    List<RGB> list = ad.getRGBList();
//    for (RGB rgb : list) {
//      System.out.println(rgb);
//    }
//    List<CIEXYZ> XYZlist = ad.getXYZList();
//    for (CIEXYZ XYZ : XYZlist) {
//      System.out.println(XYZ);
//    }
    List<String> nameList = ad.getPatchNameList();
    for (String name : nameList) {
      System.out.println(name);
    }

  }

  public final boolean isInverseModeMeasure() {
    return false;
  }
}
