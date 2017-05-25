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
import shu.util.log.*;
import shu.math.array.*;
import shu.cms.CorrelatedColorTemperature;

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
public class AUORampXLSAdapter
    extends TargetAdapter {
  private File file;
  private LCDTargetBase.Number number = LCDTargetBase.Number.Ramp1024;

  public static void main(String[] args) {
    AUORampXLSAdapter adapter = new AUORampXLSAdapter(
        "C:\\Temp\\measure.xls");
    List<RGB> rgblist = adapter.getRGBList();
    List<CIEXYZ> XYZlist = adapter.getXYZList();
    for (int x = 255; x >= 0; x--) {
      CIEXYZ XYZ = XYZlist.get(x);
      double CCT = CorrelatedColorTemperature.XYZ2CCTByRobertson(XYZ);
//      CorrelatedColorTemperature.getduvWithBlackbody()
      System.out.println(CCT);
    }
//    for (CIEXYZ XYZ : XYZlist) {
//      System.out.println(XYZ);
//    }
  }

  public AUORampXLSAdapter(String filename) {
    this(filename, LCDTargetBase.Number.Ramp1024);
  }

  public AUORampXLSAdapter(String filename, LCDTargetBase.Number number) {
    file = new File(filename);
    this.number = number;
  }

  /**
   * estimateLCDTargetNumber
   *
   * @return Number
   */
  public Number estimateLCDTargetNumber() {
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
    return "AUO Ramp Excel File";
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
  public List getPatchNameList() {
    LCDTarget target = LCDTargetBase.Instance.get(number);
    if (target != null) {
      return target.filter.nameList();
    }
    else {
      return null;
    }
  }

  /**
   * getRGBList
   *
   * @return List
   */
  public List<RGB> getRGBList() {
    return LCDTargetBase.Instance.getRGBList(number);
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
      XYZList = new ArrayList<CIEXYZ> (number.getPatchCount());
      int[][] indexArray = new int[][] {
          {
          9, 10, 11}, {
          12, 13, 14}, {
          15, 16, 17}, {
          1, 2, 3}
      };
      try {
        double[] xyYValues = new double[3];
        ExcelFile xls = new ExcelFile(file, false);
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
        for (RGB.Channel ch : RGB.Channel.RGBChannel) {
          //r g b
          int[] index = indexArray[ch.getArrayIndex()];

          for (int x = rows - 1; x > 0; x--) {
            CIEXYZ XYZ = null;
            if (!xls.isEmpty(index[0], x)) {
              xyYValues[0] = xls.getCell(index[0], x);
              xyYValues[1] = xls.getCell(index[1], x);
              xyYValues[2] = xls.getCell(index[2], x);
              XYZ = new CIExyY(xyYValues).toXYZ();
            }
            else {
              XYZ = new CIEXYZ();
            }
            XYZList.add(XYZ);
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

    }
    return XYZList;
  }

  private List<CIEXYZ> XYZList = null;

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
