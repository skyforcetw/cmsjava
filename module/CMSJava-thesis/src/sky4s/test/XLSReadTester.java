package sky4s.test;

import java.io.*;
import java.io.File;

import jxl.*;
import jxl.read.biff.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class XLSReadTester {
  public XLSReadTester() {
  }

  public static void main(String[] args) {
    try {
      Workbook workbook = Workbook.getWorkbook(new File("CP_Table.xls"));
      Sheet sheet = workbook.getSheet(0);
      Cell a1 = sheet.getCell(0, 0);
      Cell b2 = sheet.getCell(1, 1);
      Cell c2 = sheet.getCell(2, 1);

      String stringa1 = a1.getContents();
      String stringb2 = b2.getContents();
      String stringc2 = c2.getContents();
      System.out.println(stringa1);
      System.out.println(stringb2);
      System.out.println(stringc2);
    }
    catch (BiffException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
