package shu.cms.colorformat.adapter.xls;

import java.io.*;

import jxl.read.biff.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;

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
public class AUOMeasureXLSAdapter
    extends AbstractXLSAdapter {

  public AUOMeasureXLSAdapter(String filename) throws
      IOException, BiffException {
    super(filename, 0, new int[] {1, 2, 3}, new int[] {4, 5, 6}, 1);
  }

  protected CIEXYZ getCIEXYZ(double[] values) {
    return new CIExyY(values).toXYZ();
  }

  public static void main(String[] args) throws Exception {
    AUOMeasureXLSAdapter adapter = new AUOMeasureXLSAdapter("ColorList_all(871).xls");
//    for (String s : adapter.getPatchNameList()) {
//      System.out.println(s);
//    }
    for (RGB rgb : adapter.getRGBList()) {
      System.out.println(rgb);
    }
//    for (CIEXYZ XYZ : adapter.getXYZList()) {
//      System.out.println(XYZ);
//    }

  }

}
