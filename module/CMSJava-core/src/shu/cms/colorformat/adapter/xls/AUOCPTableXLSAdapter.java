package shu.cms.colorformat.adapter.xls;

import java.io.*;
import java.io.File;
import java.util.*;

import jxl.read.biff.*;
import shu.cms.colorformat.adapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;

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
public class AUOCPTableXLSAdapter
    extends AbstractXLSAdapter {
  public AUOCPTableXLSAdapter(String filename) throws
      IOException, BiffException {
    this(filename, RGB.MaxValue.Int10Bit);
  }

  public AUOCPTableXLSAdapter(String filename, RGB.MaxValue maxValue) throws
      IOException, BiffException {
    super(filename, -1, new int[] {1, 2, 3}, new int[] {1, 2, 3}, 1,
          maxValue);
  }

  protected File file;

  public List<String> getPatchNameList() {
    throw new UnsupportedOperationException();
  }

  /**
   * getName
   *
   * @return String
   */
  public String getFilename() {
    return file.getName();
  }

  public String getAbsolutePath() {
    return file.getAbsolutePath();
  }

  public List<RGB> getRGBList() {
    this.xls.selectSheet("Gamma_Table");
    return super.getRGBList();
  }

  /**
   * getXYZList
   *
   * @return List
   */
  public List<CIEXYZ> getXYZList() {
    this.xls.selectSheet("Raw_Data");
    rows = xls.getRows();
    size = xls.getRows() - startIndex;
    return super.getXYZList();
  }

  public Style getStyle() {
    return Style.RGB;
  }

  public String getFileDescription() {
    return "AUO CPTable Excel File";
  }

  /**
   *
   * @return Number
   */
  public LCDTargetBase.Number estimateLCDTargetNumber() {
    throw new UnsupportedOperationException();
  }

  protected CIEXYZ getCIEXYZ(double[] values) {
    CIExyY xyY = new CIExyY(values);
    return xyY.toXYZ();
  }

  public static void main(String[] args) throws BiffException, IOException {
    AUOCPTableXLSAdapter file = new AUOCPTableXLSAdapter("debug.xls");
    for (RGB rgb : file.getRGBList()) {
      System.out.println(rgb);
    }
    for (CIEXYZ XYZ : file.getXYZList()) {
      System.out.println(XYZ);
    }
  }
}
