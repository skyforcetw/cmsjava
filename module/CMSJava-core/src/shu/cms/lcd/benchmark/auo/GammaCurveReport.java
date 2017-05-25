package shu.cms.lcd.benchmark.auo;

import shu.cms.colorformat.adapter.xls.*;
import shu.cms.lcd.*;
import java.io.*;

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
public class GammaCurveReport extends Benchmark {

    public GammaCurveReport(LCDTarget lcdTarget) {
        super(lcdTarget);
    }

    public static void main(String[] args) {
//    GammaCurve
    }

    public String report() {
        StringBuilder report = new StringBuilder();

        AUORampXLSAdapter adapter = null;
        try {
            adapter = new AUORampXLSAdapter(filename);
        } catch (FileNotFoundException ex) {
            return null;
        }

        LCDTarget target = LCDTarget.Instance.get(adapter);
        int size = target.size();
        if (size != 1024) {
            throw new IllegalArgumentException(
                    "Unsupported measurement count: " +
                    size);
        }
        target.targetFilter.getRamp256W();
        return report.toString();
    }
}
