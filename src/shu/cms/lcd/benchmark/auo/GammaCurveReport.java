package shu.cms.lcd.benchmark.auo;

import shu.cms.colorformat.adapter.xls.*;
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
 * @author skyforce
 * @version 1.0
 */
public class GammaCurveReport
    extends Benchmark {

  public GammaCurveReport(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  public static void main(String[] args) {
//    GammaCurve
  }

  public String report() {
    StringBuilder report = new StringBuilder();

    AUORampXLSAdapter adapter = new AUORampXLSAdapter(filename);
    LCDTarget target = LCDTarget.Instance.get(adapter);
    int size = target.size();
//    List<CIEXYZ> XYZList = adapter.getXYZList();
//    int size = XYZList.size();
    if (size != 1024) {
      throw new IllegalArgumentException("Unsupported measurement count: " +
                                         size);
    }
    target.targetFilter.getRamp256W();
//    for (CIEXYZ XYZ : XYZList) {
//      System.out.println(XYZ);
//    }
    return report.toString();
  }
}
