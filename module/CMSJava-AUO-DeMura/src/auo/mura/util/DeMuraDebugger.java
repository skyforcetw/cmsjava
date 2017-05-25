package auo.mura.util;

import java.io.*;

import jxl.read.biff.*;
import jxl.write.*;
import shu.io.files.*;

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
public class DeMuraDebugger {
  public DeMuraDebugger(String filename) {
    try {
      excel = new ExcelFile(filename, true);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
  }

  public void close() {
    try {
      excel.close();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (WriteException ex) {
      ex.printStackTrace();
    }
  }

  private ExcelFile excel;
  public int storeDeMuraData(int row, short[][] deMuraData) {
//    if (!storeDeMuraData) {
//      return -1;
//    }
    int height = deMuraData.length;
    int width = deMuraData[0].length;
    try {
      for (int h = 0; h < height; h++) {
        int rowIndex = row + h;
        for (int w = 0; w < width; w++) {
          short v = deMuraData[h][w];

          excel.setCell(w, rowIndex, v);

        }
      }
    }
    catch (WriteException ex) {
      ex.printStackTrace();
    }

    return row + height + 1;
  }
}
