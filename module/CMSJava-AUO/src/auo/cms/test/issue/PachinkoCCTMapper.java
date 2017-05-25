package auo.cms.test.issue;

import shu.cms.colorformat.adapter.xls.AUORampXLSAdapter;
import java.io.FileNotFoundException;
import shu.cms.lcd.LCDTarget;
import shu.cms.colorspace.depend.*;
import shu.cms.Patch;
import java.util.List;
import java.awt.Color;
import shu.cms.plot.Plot2D;
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
public class PachinkoCCTMapper {
  public PachinkoCCTMapper() {
    super();
  }

  static double[][] getLuminanceCurve(AUORampXLSAdapter adapter) {
    LCDTarget customerTarget = LCDTarget.Instance.get(adapter);
    List<Patch>
        rPatchListC = customerTarget.filter.grayScalePatch(RGB.Channel.R, true);
    List<Patch>
        gPatchListC = customerTarget.filter.grayScalePatch(RGB.Channel.G, true);
    List<Patch>
        bPatchListC = customerTarget.filter.grayScalePatch(RGB.Channel.B, true);
    double[][] luminanceCurve = new double[3][256];
    for (int x = 0; x < 256; x++) {
      Patch rPatch = rPatchListC.get(x);
      Patch gPatch = gPatchListC.get(x);
      Patch bPatch = bPatchListC.get(x);
      luminanceCurve[0][x] = rPatch.getXYZ().Y;
      luminanceCurve[1][x] = gPatch.getXYZ().Y;
      luminanceCurve[2][x] = bPatch.getXYZ().Y;
//    System.out.println(luminanceCurve[0][x] + " " + luminanceCurve[1][x] +
//                       " " + luminanceCurve[2][x]);
    }
    return luminanceCurve;
  }

  static double[] alterLuminanceRange(double[] curve, double targetBlack,
                                      double targetWhite) {
    double black = curve[0];
    double white = curve[255];
    for (int x = 0; x < 256; x++) {
      curve[x] -= black;
      curve[x] /= (white - black);
      curve[x] *= (targetWhite - targetBlack);
      curve[x] += targetBlack;
    }
    return curve;
  }

  public static void main(String[] args) throws FileNotFoundException {
    String dir = "D:/軟體/nobody zone/exp data/CCTv3/2012/120703/新資料夾";
    AUORampXLSAdapter customer = new AUORampXLSAdapter(dir +
        "/Measurement01_customer.xls");
    AUORampXLSAdapter original = new AUORampXLSAdapter(dir +
        "/Measurement01_original.xls");
//    AUORampXLSAdapter original = new AUORampXLSAdapter(
//        "D:/軟體/nobody zone/exp data/CCTv3/2012/120703/Measurement00_original.xls");

    double[][] customerCurve = getLuminanceCurve(customer);
    double[][] originalCurve = getLuminanceCurve(original);
    Plot2D plot = Plot2D.getInstance();
    //240 235 255
    double rTargetLuminance = originalCurve[0][242];
    double gTargetLuminance = originalCurve[1][235];
    double bTargetLuminance = originalCurve[2][255];
//    Interpolation1DLUT r
//    double kTargetLuminance = originalCurve[0][0];
    alterLuminanceRange(customerCurve[0], originalCurve[0][0], rTargetLuminance);
    alterLuminanceRange(customerCurve[1], originalCurve[1][0], gTargetLuminance);
    alterLuminanceRange(customerCurve[2], originalCurve[2][0], bTargetLuminance);
    double[] keys = new double[256];
    for (int x = 0; x < 256; x++) {
      plot.addCacheScatterLinePlot("r-cus", Color.red, x, customerCurve[0][x]);
      plot.addCacheScatterLinePlot("r-org", Color.red, x, originalCurve[0][x]);
      plot.addCacheScatterLinePlot("g-cus", Color.green, x, customerCurve[1][x]);
      plot.addCacheScatterLinePlot("g-org", Color.green, x, originalCurve[1][x]);
      plot.addCacheScatterLinePlot("b-cus", Color.blue, x, customerCurve[2][x]);
      plot.addCacheScatterLinePlot("b-org", Color.blue, x, originalCurve[2][x]);
      keys[x] = x;
    }
    plot.setVisible();

    Interpolation1DLUT rLut = new Interpolation1DLUT(keys, originalCurve[0]);
    Interpolation1DLUT gLut = new Interpolation1DLUT(keys, originalCurve[1]);
    Interpolation1DLUT bLut = new Interpolation1DLUT(keys, originalCurve[2]);
    for (int x = 0; x < 256; x++) {
      double targetRLuminance = customerCurve[0][x];
      double targetGLuminance = customerCurve[1][x];
      double targetBLuminance = customerCurve[2][x];
      targetRLuminance = rLut.correctValueInRange(targetRLuminance);
      targetGLuminance = gLut.correctValueInRange(targetGLuminance);
      targetBLuminance = bLut.correctValueInRange(targetBLuminance);
      double r = rLut.getKey(targetRLuminance);
      double g = gLut.getKey(targetGLuminance);
      double b = bLut.getKey(targetBLuminance);
//      System.out.println(r + " " + g + " " + b);
      System.out.println( ( (int) (r * 4 + 0.5)) + " " + ( (int) (g * 4 + 0.5)) +
                         " " + ( (int) (b * 4 + 0.5)));
    }
  }
}
