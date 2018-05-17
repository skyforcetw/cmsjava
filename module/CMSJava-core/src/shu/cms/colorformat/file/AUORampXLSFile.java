package shu.cms.colorformat.file;

import java.io.*;
import java.text.*;
import java.util.*;

import jxl.read.biff.*;
import jxl.write.*;
import shu.cms.*;
import shu.cms.colorformat.adapter.*;
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
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class AUORampXLSFile {
  protected String filename;
  protected LCDTarget lcdTarget;

  public AUORampXLSFile(String filename, LCDTarget lcdTarget) {
    this.filename = filename;
    this.lcdTarget = lcdTarget;
  }

  public void save() throws IOException {
    save(this.filename);
  }

  private DecimalFormat df = new DecimalFormat(".###############");

  public String toString() {

    StringBuilder buf = new StringBuilder();
    for (RGBBase.Channel ch : RGBBase.Channel.RGBWChannel) {
      for (int x = 0; x < 256; x++) {
        Patch p = lcdTarget.getPatch(ch, x, RGB.MaxValue.Int8Bit);
        if (p == null) {
          buf.append("0,0,0,0,0,0\n");
        }
        else {
          CIEXYZ XYZ = p.getXYZ();
          CIExyY xyY = new CIExyY(XYZ);
          buf.append(' ');
          buf.append(df.format(XYZ.X));
          buf.append(",");
          buf.append(df.format(XYZ.Y));
          buf.append(",");
          buf.append(df.format(XYZ.Z));
          buf.append(",");
          buf.append(df.format(xyY.x));
          buf.append(",");
          buf.append(df.format(xyY.y));
          buf.append(",");
          buf.append(df.format(xyY.Y));
          buf.append("\n\r");
        }
      }
    }
    String str = buf.toString();
    return str.trim();
  }

  /**
   *
   * @param excel ExcelFile
   * @param lcdTarget LCDTarget
   * @return int 寫入的灰階筆數
   * @throws WriteException
   */
  private int writeWhite(ExcelFile excel, LCDTarget lcdTarget) throws
      WriteException {

    excel.setCell(0, 0, "Gray Level");
    excel.setCell(1, 0, "W_x");
    excel.setCell(2, 0, "W_y");
    excel.setCell(3, 0, "W_Y (nit)");
    excel.setCell(4, 0, "W_C.T.");
    excel.setCell(5, 0, "ΔUV");
    excel.setCell(6, 0, "W_R");
    excel.setCell(7, 0, "W_G");
    excel.setCell(8, 0, "W_B");
    List<Patch> grayPatchList = lcdTarget.filter.grayPatch(true);
    int size = grayPatchList.size();
    int index = 1;

    for (int x = size - 1; x >= 0; x--) {
      Patch p = grayPatchList.get(x);
      CIEXYZ XYZ = p.getXYZ();
      CIExyY xyY = new CIExyY(XYZ);
      double gray = p.getRGB().getValue(RGB.Channel.W);
      excel.setCell(0, index, gray);
      excel.setCell(1, index, xyY.x);
      excel.setCell(2, index, xyY.y);
      excel.setCell(3, index, xyY.Y);
      excel.setCell(4, index, XYZ.getCCT());
      excel.setCell(5, index,
                    CorrelatedColorTemperature.getduvWithBlackbody(XYZ));
      excel.setCell(6, index, 0);
      excel.setCell(7, index, 0);
      excel.setCell(8, index, 0);
      index++;
    }
    return size;
  }

  private void writeColor(ExcelFile excel, LCDTarget lcdTarget, RGB.Channel ch,
                          int grayScale) throws
      WriteException {
    if (!ch.isPrimaryColorChannel()) {
      throw new IllegalArgumentException("!ch.isPrimaryColorChannel()");
    }
    int indexX = ch.getArrayIndex() * 3 + 9;
    String chname = ch.name();
    excel.setCell(indexX, 0, chname + "_x");
    excel.setCell(indexX + 1, 0, chname + "_y");
    excel.setCell(indexX + 2, 0, chname + "_Y (nit)");

    List<Patch> grayPatchList = lcdTarget.filter.grayScalePatch(ch, true);
    int size = grayPatchList.size();
    int index = (size != grayScale) ? 2 : 1;

    for (int x = size - 1; x >= 0; x--) {
      Patch p = grayPatchList.get(x);
      CIEXYZ XYZ = p.getXYZ();
      CIExyY xyY = new CIExyY(XYZ);
      excel.setCell(indexX, index, xyY.x);
      excel.setCell(indexX + 1, index, xyY.y);
      excel.setCell(indexX + 2, index, xyY.Y);
      index++;
    }
  }

  public void save(String filename) throws IOException {
    try {
      ExcelFile excel = new ExcelFile(filename, true);
      int grayScale = writeWhite(excel, lcdTarget);
      writeColor(excel, lcdTarget, RGB.Channel.R, grayScale);
      writeColor(excel, lcdTarget, RGB.Channel.G, grayScale);
      writeColor(excel, lcdTarget, RGB.Channel.B, grayScale);
      excel.close();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (BiffException ex) {
      Logger.log.error("", ex);
    }
    catch (WriteException ex) {
      Logger.log.error("", ex);
    }

  }

  public static void main(String[] args) throws Exception {
    LogoFileAdapter adapter = new LogoFileAdapter(
        "lcd.calibrate\\(0)(252.0 252.0 252.0)_uvpByDE00_50_2.2\\OriginalTarget\\ramp.logo");
    LCDTarget target = LCDTarget.Instance.get(adapter);
    AUORampXLSFile file = new AUORampXLSFile("abc.xls", target);
    file.save();
  }
}
