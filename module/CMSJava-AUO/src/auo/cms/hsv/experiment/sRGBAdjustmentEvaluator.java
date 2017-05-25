package auo.cms.hsv.experiment;

import shu.cms.lcd.LCDTarget;
import shu.cms.devicemodel.lcd.MultiMatrixModel;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import java.util.*;
import shu.cms.plot.*;
import java.awt.Color;
import shu.math.array.DoubleArray;

//import shu.plot.*;

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
public class sRGBAdjustmentEvaluator {
//ด๚ธี

  public static void main(String[] args) {
    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(
        "sRGB Adjust Evaluation/45% DG On.xls");
//                "sRGB Adjust Evaluation/60% DG On.xls", LCDTarget.Number.Ramp256_6Bit);

//    LCDTarget target = LCDTarget.Instance.getFromAUORampXLS(
//        "dell/Measurement01(DG On).xls", LCDTarget.Number.Ramp256_6Bit);

    LCDTarget.Operator.gradationReverseFix(target);
//    for (Patch p : target.getPatchList()) {
//      System.out.println(p.getRGB() + " " + p.getXYZ());
//    }
    MultiMatrixModel model = new MultiMatrixModel(target);
    model.produceFactor();
    double luminance = model.getWhiteXYZ(false).Y;
    model.setAutoRGBChangeMaxValue(true);
//    Plot3D plot = Plot3D.getInstance();

    List<RGB> rgblist = LCDTarget.Instance.getRGBList(LCDTarget.Number.WHQL);
    List<double[]> adjustList = new ArrayList<double[]> ();

    for (int x = 7; x < 46; x++) {
//        for (int x = 0; x < rgblist.size(); x++) {
      RGB rgb = rgblist.get(x);
//            CIEXYZ panelXYZ = model.getXYZ(rgb, false);
      CIEXYZ sRGBXYZ = rgb.toXYZ(RGB.ColorSpace.sRGB);
      sRGBXYZ.times(luminance);
      RGB rgb2 = model.getRGB(sRGBXYZ, false);
      HSV hsv = new HSV(rgb);
      HSV hsv2 = new HSV(rgb2);
      if (hsv.S == 0 || hsv.V == 100) {
        continue;
      }
      double deltav = hsv2.V - hsv.V;
      deltav = deltav / 100 * 255;
      System.out.println(rgb + " " + hsv + " " + hsv2 + " " + hsv2.S / hsv.S
                         + " " + (hsv2.V - hsv.V));
//      plot.addScatterPlot("", rgb.getColor(), hsv.S, hsv.V, deltav);
      double[] adjust = new double[] {
          hsv.H, hsv.S, hsv.V, deltav};
      adjustList.add(adjust);
    }

    Collections.sort(adjustList, DoubleArray.getDoubleArrayComparatorInstance());

    HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {0, 100, 100});
    Plot3D plot2 = Plot3D.getInstance();
    plot2.setTitle(target.getFilename());
    int piece = 3;
    for (int x = 0; x < 6; x++) {
      double[] firstadjust = adjustList.get(x * piece);
      hsv.H = firstadjust[0];
      Color c = hsv.toRGB().getColor();
      for (int y = 0; y < piece; y++) {
        double[] adjust = adjustList.get(x * piece + y);
        System.out.println(Arrays.toString(adjust));
        plot2.addCacheScatterLinePlot(Double.toString(adjust[0]), c,
                                      adjust[1], adjust[2], adjust[3]);
      }
      double[] adjust1 = adjustList.get(x * piece + 1);
      double[] adjust2 = adjustList.get(x * piece + 2);
      System.out.println(adjust1[3] / adjust2[3]);
    }

    plot2.setVisible();
    plot2.setAxeLabel(0, "S");
    plot2.setAxeLabel(1, "V");
    plot2.setAxeLabel(2, "dV");
    plot2.setFixedBounds(0, 0, 100);
    plot2.setFixedBounds(1, 0, 100);
    plot2.setFixedBounds(2, 0, 50);

//    plot2.addLinePlot("", Color.black, new double[] {1, 2, 3}, new double[] {1,
//                      2, 15});

  }

//    static class DoubleArrayComparator
//            implements Comparator {
//
//        /**
//         * Compares its two arguments for order.
//         *
//         * @param o1 the first object to be compared.
//         * @param o2 the second object to be compared.
//         * @return a negative integer, zero, or a positive integer as the first
//         *   argument is less than, equal to, or greater than the second.
//         */
//        public int compare(Object o1, Object o2) {
//            double[] array1 = (double[]) o1;
//            double[] array2 = (double[]) o2;
//            return Double.compare(array1[0], array2[0]) * 10
//                    + Double.compare(array1[1], array2[1]);
//        }
//
//        /**
//         * Indicates whether some other object is &quot;equal to&quot; this
//         * comparator.
//         *
//         * @param obj the reference object with which to compare.
//         * @return <code>true</code> only if the specified object is also a
//         *   comparator and it imposes the same ordering as this comparator.
//         * @todo Implement this java.util.Comparator method
//         */
//        public boolean equals(Object obj) {
//            return false;
//        }
//    }
}
