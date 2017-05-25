package shu.cms.lcd.benchmark.verify;

import java.util.List;
import javax.vecmath.*;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.hvs.cam.ciecam02.*;
import shu.cms.lcd.*;
import shu.cms.lcd.benchmark.verify.Verifier.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.geometry.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 螢幕的飽和度曲線驗證.
 * 如果螢幕受到漏光的影響嚴重的話, 飽和度的上升將會很緩慢, 反之, 很容易就到達飽和.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class SaturationVerifier
    extends Verifier {

  public SaturationVerifier(LCDTarget lcdTarget) {
    super(lcdTarget);
  }

  public static void main(String[] args) {

    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(
        "D:\\My Documents\\工作\\華山計畫\\LG 42SL90QD\\Cinema\\ramp.xls");
//    LCDTarget target = LCDTarget.Instance.getFromSpectroPhotometerCxF(
//        "Measurement Files/Monitor/dell_2407wfp_hc/i1pro/darkroom/D65/1021.cxf");
//    LCDTarget target = LCDTarget.Instance.getFromSpectroPhotometerCxF(
//        "Measurement Files/Monitor/eizo_cg241w/i1display2/darkroom/D65/1021.cxf");

    SaturationVerifier verifier = new SaturationVerifier(target);
//    VerifierReport r1 = verifier.CIExySaturationVerify(RGBBase.Channel.R);
//    VerifierReport r2 = verifier.CIExySaturationVerify(RGBBase.Channel.G);
//    VerifierReport r3 = verifier.CIExySaturationVerify(RGBBase.Channel.B);
//    VerifierReport r1 = verifier.CIELChSaturationVerify(RGBBase.Channel.R);
//    VerifierReport r2 = verifier.CIELChSaturationVerify(RGBBase.Channel.G);
//    VerifierReport r3 = verifier.CIELChSaturationVerify(RGBBase.Channel.B);
    VerifierReport r1 = verifier.CIECAM02SaturationVerify(RGBBase.Channel.R);
    VerifierReport r2 = verifier.CIECAM02SaturationVerify(RGBBase.Channel.G);
    VerifierReport r3 = verifier.CIECAM02SaturationVerify(RGBBase.Channel.B);

    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);
//    plot.addScatterPlot("R",Color.RED,
    plot.addLinePlot("R", Color.red, 0, 255, (double[]) r1.result);
    plot.addLinePlot("G", Color.green, 0, 255, (double[]) r2.result);
    plot.addLinePlot("B", Color.blue, 0, 255, (double[]) r3.result);
  }

  protected double getSaturation(double[] xyValues) {
    if (whitexyPoint == null) {
      double[] whitexyValues = lcdTarget.getWhitePatch().getXYZ().
          getxyValues();
      whitexyPoint = new Point2d(whitexyValues);
    }

    Point2d xyPoint = new Point2d(xyValues);
    return Geometry.getDistance(xyPoint, whitexyPoint);
  }

  protected Point2d whitexyPoint = null;

  public VerifierReport CIECAM02SaturationVerify(RGBBase.Channel ch) {
    List<Patch> patchList = lcdTarget.filter.oneValueChannel(ch);
    int size = patchList.size();
    double[] saturationArray = new double[size];
    double[] code = new double[size];
    CIECAM02 cam = new CIECAM02(ViewingConditions.DimViewingConditions);

    for (int x = 0; x < size; x++) {
      Patch p = patchList.get(x);
      CIECAM02Color color = cam.forward(p.getNormalizedXYZ());
      double saturation = color.s;
      saturationArray[x] = saturation;
      code[x] = p.getRGB().getValue(ch, RGB.MaxValue.Int8Bit);
    }

//    double[] smooth = new double[size];
//    System.arraycopy(saturationArray, 0, smooth, 0, size);
//    for (int x = 1; x < size - 1; x++) {
//      smooth[x] = (saturationArray[x - 1] + saturationArray[x + 1]) / 2;
//    }
    double[] smooth = Convolution.convole(saturationArray,
                                          new double[] {1, 3, 5, 7, 5, 3, 1});

    return new VerifierReport(smooth);
//    return new VerifierReport(saturationArray);
  }

  public VerifierReport CIELChSaturationVerify(RGBBase.Channel ch) {
    List<Patch> patchList = lcdTarget.filter.oneValueChannel(ch);
    int size = patchList.size();
    double[] saturationArray = new double[size];
    int index = 0;

    for (int x = size - 1; x >= 0; x--) {
      Patch p = patchList.get(x);
      CIELCh LCh = new CIELCh(p.getLab());
      double saturation = LCh.C;
      saturationArray[index++] = saturation;
    }

    return new VerifierReport(saturationArray);
  }

  public VerifierReport CIExySaturationVerify(RGBBase.Channel ch) {
    List<Patch> patchList = lcdTarget.filter.oneValueChannel(ch);
    int size = patchList.size();
    double[] maxxyValues = patchList.get(size - 1).getXYZ().getxyValues();
    double maxSaturation = getSaturation(maxxyValues);
    double[] saturationArray = new double[size];
    int index = 0;

    for (int x = size - 1; x >= 0; x--) {
      Patch p = patchList.get(x);

      double saturation = getSaturation(p.getXYZ().getxyValues());
      double percent = saturation / maxSaturation;
      saturationArray[index++] = percent;
    }

    return new VerifierReport(saturationArray);
  }

  /**
   * checkLCDTarget
   *
   * @param lcdTarget LCDTarget
   * @return boolean
   */
  protected boolean checkLCDTarget(LCDTarget lcdTarget) {
    return lcdTarget.getNumber() == LCDTargetBase.Number.Ramp1021 ||
        lcdTarget.getNumber() == LCDTargetBase.Number.Ramp1024;
  }
}
