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
    super(filename, -1, new int[] {1, 2, 3}, null, 1, maxValue);
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

  /**
   * getXYZList
   *
   * @return List
   */
  public List getXYZList() {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }
}
