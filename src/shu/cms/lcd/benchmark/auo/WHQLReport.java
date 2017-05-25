package shu.cms.lcd.benchmark.auo;

import java.text.*;

import shu.cms.*;
import shu.cms.lcd.*;
import shu.cms.lcd.benchmark.verify.*;

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
public class WHQLReport
    extends Benchmark {

  public WHQLReport(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  /**
   *
   * @return String
   */
  public String report() {
    StringBuilder report = new StringBuilder();

    LCDTarget whqlTarget = lcdTarget.targetFilter.getWHQL();
    WHQLVerifier verifier = new WHQLVerifier(whqlTarget);

    WHQLVerifier.VerifierReport standAloneReport = verifier.standAloneLCDVerify();
    report.append("Stand alone\n" + getReport(verifier.getStandAloneLCDReport()));
    report.append(standAloneReport);

    WHQLVerifier.VerifierReport IEC61966_4Report = verifier.IEC61966_4Verify();
    report.append("IEC61966-4\n" + getReport(verifier.getIEC61966_4LCDReport()));
    report.append(IEC61966_4Report);

    return report.toString();
  }

  protected String getReport(DeltaEReport report) {
    DecimalFormat df = new DecimalFormat("##.##");
    StringBuilder result = new StringBuilder();
    result.append("deltaE94 Average: " +
                  df.format(report.meanDeltaE.getCIE94DeltaE()));
    result.append(" Max: " +
                  df.format(report.maxDeltaE.getCIE94DeltaE()) +
                  "\n");
    return result.toString();
  }

  public static void main(String[] args) throws Exception {
    String[] modes = new String[] {
         "Standard", "Movie", "Game", "PC", "USER","Photo"};

    for (String mode : modes) {
      String filename = "D:\\My Documents\\工作\\華山計畫\\Sharp LC-46LX1\\Modes\\" + mode +
          "\\871.xls";

      LCDTarget lcdTarget = LCDTarget.Instance.getFromAUOXLS(filename);
      WHQLReport report = new WHQLReport(lcdTarget);
//      LCDTarget target = LCDTarget.Instance.getFromAUOXLS(filename);
//      WHQLVerifier verifier = new WHQLVerifier(target);
      System.out.println(mode);
      System.out.println(report.report());
    }
  }
}
