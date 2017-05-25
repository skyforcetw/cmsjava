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
import shu.cms.lcd.LCDTargetBase.Number;
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
 * @author not attributable
 * @version 1.0
 */
public abstract class AbstractXLSAdapter
    extends TargetAdapter {

  private File file;
  protected ExcelFile xls;
  private List<RGB> rgbList = null;
  private List<CIEXYZ> XYZList = null;
  protected abstract CIEXYZ getCIEXYZ(double[] values);

  private int[] rgbIndexArray;
  private int nameIndex;
  private int[] valuesIndexArray;
  protected int startIndex;
  protected int size;
  protected int rows;
  private RGB.MaxValue maxValue;

  protected AbstractXLSAdapter(String xlsFilename, int nameIndex,
                               int[] rgbIndexArray, int[] valuesIndexArray,
                               int startIndex) throws
      IOException, BiffException {
    this(xlsFilename, nameIndex, rgbIndexArray, valuesIndexArray, startIndex,
         RGB.MaxValue.Double255);
  }

  protected AbstractXLSAdapter(String xlsFilename, int nameIndex,
                               int[] rgbIndexArray, int[] valuesIndexArray,
                               int startIndex, RGB.MaxValue maxValue) throws
      IOException, BiffException {
    file = new File(xlsFilename);
    xls = new ExcelFile(file, false);
    this.nameIndex = nameIndex;
    this.rgbIndexArray = rgbIndexArray;
    this.valuesIndexArray = valuesIndexArray;
    this.startIndex = startIndex;
    rows = xls.getRows();
    size = xls.getRows() - startIndex;
    this.maxValue = maxValue;
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
    return "Excel File";
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
    List<String> list = new ArrayList<String> (size);

    for (int x = startIndex; x < rows; x++) {
      String name = xls.getCellAsString(nameIndex, x);
      list.add(name);
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
      double[] RGBValues = new double[3];
      rgbList = new ArrayList<RGB> (size);

      for (int x = startIndex; x < rows; x++) {

        RGBValues[0] = xls.getCell(rgbIndexArray[0], x);
        RGBValues[1] = xls.getCell(rgbIndexArray[1], x);
        RGBValues[2] = xls.getCell(rgbIndexArray[2], x);
        RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, RGBValues, maxValue);
        rgbList.add(rgb);
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

  /**
   * getXYZList
   *
   * @return List
   */
  public List<CIEXYZ> getXYZList() {
    if (XYZList == null) {
      double[] values = new double[3];
      XYZList = new ArrayList<CIEXYZ> (size);

      for (int x = startIndex; x < rows; x++) {
        values[0] = xls.getCell(valuesIndexArray[0], x);
        values[1] = xls.getCell(valuesIndexArray[1], x);
        values[2] = xls.getCell(valuesIndexArray[2], x);
        CIEXYZ XYZ = getCIEXYZ(values);
        XYZList.add(XYZ);
      }

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
