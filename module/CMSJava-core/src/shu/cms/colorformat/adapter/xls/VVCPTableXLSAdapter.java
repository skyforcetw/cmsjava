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
 * <p>Description: </p>
 * excel®æ¦¡ªºcp table adapter
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class VVCPTableXLSAdapter
    extends AbstractXLSAdapter {
  public VVCPTableXLSAdapter(String filename) throws
      IOException, BiffException {
    super(filename, -1, new int[] {0, 1, 2}, null, 0);
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

  public static void main(String[] args) throws Exception {
    VVCPTableXLSAdapter ad = new VVCPTableXLSAdapter(
        "CPCodeLoader.xls");
    List<RGB> list = ad.getRGBList();
    for (RGB rgb : list) {
      System.out.println(rgb);
    }
  }

  public Style getStyle() {
    return Style.RGB;
  }

  public String getFileDescription() {
    return "VastView CPTable Excel File";
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
