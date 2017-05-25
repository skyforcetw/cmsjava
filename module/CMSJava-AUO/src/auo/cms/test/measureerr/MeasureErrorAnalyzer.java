package auo.cms.test.measureerr;

import shu.cms.colorformat.adapter.xls.AUORampXLSAdapter;
import java.io.FileNotFoundException;
import java.util.*;
import shu.cms.colorspace.independ.*;
import shu.cms.colorspace.depend.*;
import shu.math.*;
import shu.math.array.*;

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
public class MeasureErrorAnalyzer {

  static List<CIEXYZ> filterXYZ(String filename, int start, int end) throws
      FileNotFoundException {
    AUORampXLSAdapter adapter = new AUORampXLSAdapter(filename);
    List<CIEXYZ> XYZList = adapter.getXYZList();
    List<CIEXYZ> XYZList0 = new ArrayList<CIEXYZ> ();
    for (int x = start; x < end; x++) {
      XYZList0.add(XYZList.get(x));
    }
    return XYZList0;
  }

  public static void main(String[] args) throws FileNotFoundException {
    List<CIEXYZ> XYZList0 = filterXYZ("measure error/Measurement02.xls", 0, 256);
    List<CIEXYZ> XYZList1 = filterXYZ("measure error/Measurement03.xls", 0, 256);
    List<CIEXYZ> XYZList2 = filterXYZ("measure error/Measurement04.xls", 0, 256);
    List<CIEXYZ>
        XYZListB = filterXYZ("measure error/Measurement02.xls", 257, 513);
    int size = XYZList0.size();

    double maxY = XYZList0.get(size - 1).Y;
    double targetGamma = 2.2;

    for (int x = 0; x < size - 1; x++) {
      CIEXYZ XYZ0 = XYZList0.get(x);
      CIEXYZ XYZ1 = XYZList1.get(x);
      CIEXYZ XYZ2 = XYZList2.get(x);
      double mean = (XYZ0.Y + XYZ1.Y + XYZ2.Y) / 3;
      double[] delta = new double[] {
          XYZ0.Y - mean, XYZ1.Y - mean, XYZ2.Y - mean};
      DoubleArray.abs(delta);
      double meanDelta = Maths.max(delta);
//      System.out.println(x + " " + meanDelta);
//      mean/maxY
      double normal = GammaFinder.findNormalInput(mean / maxY, targetGamma);
//      double normal = x / 255.;

      double targetY = Math.pow(normal, targetGamma) * maxY;
      double error1 = targetY + meanDelta;
      double error0 = targetY - meanDelta;
      double gammaErr1 = Math.log(error1 / maxY) / Math.log(normal);
      double gammaErr0 = Math.log(error0 / maxY) / Math.log(normal);
      System.out.println(x + " " + gammaErr1 + " " + gammaErr0);
    }
  }
}
