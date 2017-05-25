package shu.cms.applet.lcd;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.lcd.*;
import shu.cms.plot.*;
import shu.math.array.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來檢查LCD的混色(混合)能力 ( R+G+B=W !? )
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class LCDMixVerifier {
  public static void main(String[] args) {
    String device = "Dell_2407WFP_HC_normal";

    LCDTarget lcdTarget = LCDTarget.Instance.get(device,
                                                 LCDTarget.Source.i1pro,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.D65,
                                                 LCDTargetBase.Number.Ramp1021, null, null);
//    RGB r = new RGB(RGB.RGBColorSpace.unknowRGB,new int[3],RGB.MaxValue.Int8Bit);
//    RGB g = new RGB(RGB.RGBColorSpace.unknowRGB);
//    RGB b = new RGB(RGB.RGBColorSpace.unknowRGB);
//    RGB w = new RGB(RGB.RGBColorSpace.unknowRGB);
    CIEXYZ white = lcdTarget.getWhitePatch().getXYZ();
    double[] whiteValues = white.getValues();

    Plot2D plot = Plot2D.getInstance();
    plot.setVisible(true);
    double[][] data = new double[4][255];

    for (int x = 1; x < 256; x += 1) {
//      if(x==254) {
//        x++;
//      }
      RGB w = new RGB(RGB.ColorSpace.unknowRGB, new int[] {x, x, x});
      RGB r = new RGB(RGB.ColorSpace.unknowRGB, new int[] {x, 0, 0});
      RGB g = new RGB(RGB.ColorSpace.unknowRGB, new int[] {0, x, 0});
      RGB b = new RGB(RGB.ColorSpace.unknowRGB, new int[] {0, 0, x});
      w.changeMaxValue(RGB.MaxValue.Double1);
      r.changeMaxValue(RGB.MaxValue.Double1);
      g.changeMaxValue(RGB.MaxValue.Double1);
      b.changeMaxValue(RGB.MaxValue.Double1);

      Patch pw = lcdTarget.getPatch(w);
      Patch pr = lcdTarget.getPatch(r);
      Patch pg = lcdTarget.getPatch(g);
      Patch pb = lcdTarget.getPatch(b);

      double[] rXYZValues = pr.getXYZ().getValues();
      double[] gXYZValues = pg.getXYZ().getValues();
      double[] bXYZValues = pb.getXYZ().getValues();
      double[] rgbXYZValues = DoubleArray.plus(DoubleArray.plus(rXYZValues,
          gXYZValues), bXYZValues);

      CIELab wLab = CIELab.fromXYZ(pw.getXYZ(), white);
      CIELab rgbLab = new CIELab(CIELab.fromXYZValues(rgbXYZValues, whiteValues));
      DeltaE de = new DeltaE(wLab, rgbLab);
      double[] deLCh = de.getCIE2000DeltaLCh();
      System.out.println(DoubleArray.toString(deLCh) + " " +
                         de.getCIE2000DeltaE());

      data[0][x - 1] = deLCh[0];
      data[1][x - 1] = deLCh[1];
      data[2][x - 1] = deLCh[2];
      data[3][x - 1] = de.getCIE2000DeltaE();
//      double rgbY = pr.getXYZ().Y + pg.getXYZ().Y + pb.getXYZ().Y;
//      double Y = pw.getXYZ().Y / rgbY;
//      System.out.println(Y);

    }

    plot.addLinePlot(null, data[0]);
    plot.addLinePlot(null, data[1]);
    plot.addLinePlot(null, data[2]);
    plot.addLinePlot(null, data[3]);
  }
}
