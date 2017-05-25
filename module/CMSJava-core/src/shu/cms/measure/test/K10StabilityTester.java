package shu.cms.measure.test;

import shu.cms.hvs.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.math.array.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 * @deprecated
 */
public class K10StabilityTester {

  public static void main(String[] args) {
    LCDTarget target1 = LCDTarget.Instance.get("cpt_320WF01SC",
                                               LCDTarget.Source.CA210,
                                               LCDTargetBase.Number.Ramp1024,
                                               LCDTarget.FileType.VastView,
                                               null, "org_0709_1");

    LCDTarget target2 = LCDTarget.Instance.get("cpt_320WF01SC",
                                               LCDTarget.Source.CA210,
                                               LCDTargetBase.Number.Ramp1024,
                                               LCDTarget.FileType.VastView,
                                               null, "org_0709_2");
    int size = target1.size();
    double[] gsdf1 = new double[size];
    double[] gsdf2 = new double[size];
    for (int x = 0; x < size; x++) {
      double Y = target1.getPatch(x).getXYZ().Y;
      double jndIndex = GSDF.DICOM.getJNDIndex(Y);
      gsdf1[x] = jndIndex;

      double Y2 = target2.getPatch(x).getXYZ().Y;
      double jndIndex2 = GSDF.DICOM.getJNDIndex(Y2);
      gsdf2[x] = jndIndex2;
    }
    double[] diff = DoubleArray.minus(gsdf1, gsdf2);
//    System.out.println(DoubleArray.toString(diff));
    for (int x = 0; x < 64; x++) {
//      if (diff[x] > 1) {
      System.out.println(x + ": " + diff[x]);
//      }
    }

    Plot2D plot2 = Plot2D.getInstance();
    plot2.setVisible(true);
    plot2.addLinePlot("", 0, 1, diff);

//    plot2.addLinePlot("", 0, 1, Maths.firstOrderDerivatives(gsdf1));
  }
}
