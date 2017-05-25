package auo.cms.test.gamma;

import shu.cms.colorformat.adapter.xls.AUORampXLSAdapter;
import java.io.*;
import shu.cms.lcd.LCDTarget;
import shu.cms.Patch;
import java.util.*;
import shu.cms.colorspace.depend.*;
import shu.math.lut.Interpolation1DLUT;

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
public class GammaCorrectTester {

  public static void main(String[] args) {
    AUORampXLSAdapter xls = null;
    try {
      xls = new AUORampXLSAdapter("ramp/Measurement00.xls");
    }
    catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }

    LCDTarget target = LCDTarget.Instance.get(xls);
    List<Patch> patchList = target.getPatchList();
    List<Patch> grayList = target.filter.grayPatch(true);
    int size = grayList.size();
    double[] key = new double[size];
    double[] value = new double[size];
    for (int x = 0; x < size; x++) {
      Patch p = grayList.get(x);
      double w = p.getRGB().getValue(RGB.Channel.W);
      double Y = p.getXYZ().Y;
      key[x] = w;
      value[x] = Y;
    }
    Interpolation1DLUT lut = new Interpolation1DLUT(key, value,
        Interpolation1DLUT.Algo.LINEAR);
    double maxY = value[size - 1];
    double gamma = 2.2;
    for (int x = 254; x > 0; x--) {
      double normal = x / 255.;
      double g = Math.pow(normal, gamma);
      double luminance = maxY * g;
      luminance = lut.correctValueInRange(luminance);
      double dg = lut.getKey(luminance);
      int realdg = (int) dg * 8;
//      double realdg = dg * 8;
      double realLuminance = lut.getValue(realdg / 8.);
//      double realLuminance2 = lut.getValue(dg);
      double realgamma = Math.log(realLuminance / maxY) / Math.log(x / 255.);
      System.out.println(realgamma);
    }
  }
}
