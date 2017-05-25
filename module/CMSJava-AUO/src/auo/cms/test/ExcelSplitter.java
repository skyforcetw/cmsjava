package auo.cms.test;

import shu.io.files.ExcelFile;
import java.io.*;
import shu.cms.colorformat.adapter.xls.AUOMeasureXLSAdapter;
import shu.cms.lcd.LCDTarget;
import shu.cms.colorformat.file.AUORampXLSFile;
import shu.cms.Patch;
import java.util.List;

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
public class ExcelSplitter {
  public static void main(String[] args) throws Exception {
    File dir = new File("D:\\My Documents\\工作\\華山計畫\\Sharp LC-46LX1\\Modes");
    for (File f : dir.listFiles()) {
      if (f.isDirectory()) {
        for (File f2 : f.listFiles()) {
          if (f2.getName().equals("3247.xls")) {
            //System.out.println(f2.getName());

            ExcelFile xls = new ExcelFile(f2.getAbsolutePath(), false);
            ExcelFile xls871 = new ExcelFile(f2.getParent() + "\\871.xls", true);
            ExcelFile xlssurface = new ExcelFile(f2.getParent() +
                                                 "\\surface.xls", true);
            setHeader(xls871);
            setHeader(xlssurface);
            setCell(xls, xls871, 1, 1, 871);
            setCell(xls, xlssurface, 872, 1, 1352);

            xls.close();
            xls871.close();
            xlssurface.close();

            AUOMeasureXLSAdapter adapter = new AUOMeasureXLSAdapter(
                f2.getAbsolutePath());
            LCDTarget target = LCDTarget.Instance.get(adapter);
            List<Patch> patchList = target.filter.getRange(2223, 3247);
            LCDTarget rampTarget = LCDTarget.Instance.get(patchList,
                LCDTarget.Number.Ramp1024, false);

            String rampFilename = f2.getParent() + "\\ramp.xls";
            AUORampXLSFile rampfile = new AUORampXLSFile(rampFilename, rampTarget);
            rampfile.save();
          }
        }
      }

    }
  }

  static void setCell(ExcelFile from, ExcelFile to, int fromStart, int toStart,
                      int size) throws Exception {
    int limit = fromStart + size;
    int diff = toStart - fromStart;
    for (int x = fromStart; x < limit; x++) {
      for (int y = 0; y < 7; y++) {
        to.setCell(y, x + diff, from.getCell(y, x));
      }
    }
  }

  static void setHeader(ExcelFile xls) throws Exception {
    xls.setCell(0, 0, "Num");
    xls.setCell(1, 0, "R");
    xls.setCell(2, 0, "G");
    xls.setCell(3, 0, "B");
    xls.setCell(4, 0, "x");
    xls.setCell(5, 0, "y");
    xls.setCell(6, 0, "Y");
  }
}
