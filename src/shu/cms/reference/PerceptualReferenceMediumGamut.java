package shu.cms.reference;

import shu.util.log.Logger;
import java.io.IOException;
import shu.cms.colorformat.file.ExcelFile;
import jxl.read.biff.BiffException;

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
public class PerceptualReferenceMediumGamut {
  protected ExcelFile xls;

  private PerceptualReferenceMediumGamut() {
    try {
      xls = new ExcelFile(PerceptualReferenceMediumGamut.class.
                          getResourceAsStream(
                              "PerceptualReferenceMediumGamut.xls"));
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (BiffException ex) {
      Logger.log.error("", ex);
    }
  }

  public static void main(String[] args) {
    new PerceptualReferenceMediumGamut();
  }
}
