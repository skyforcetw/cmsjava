package vv.cms.test;

import java.text.*;
import java.util.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import vv.cms.lcd.calibrate.measured.algo.*;
import shu.math.*;
import shu.math.array.DoubleArray;

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
 */
public class ChromaticityComparator {
  private static DecimalFormat df5 = new DecimalFormat("###.#####");
  private static DecimalFormat df3 = new DecimalFormat("###.###");
  public static void main(String[] args) {
    ChromaticAroundAlgorithm algo = new ChromaticAroundAlgorithm();
    Random rand = new Random(System.currentTimeMillis());
    RGB whiteRGB = new RGB(RGB.ColorSpace.sRGB, new double[] {255, 255, 255},
                           RGB.MaxValue.Int8Bit);
    CIEXYZ whiteXYZ = whiteRGB.toXYZ();
    int errorCount = 0;

    for (int x = 0; x < 1000; x++) {
      int r = rand.nextInt(255);
      int g = rand.nextInt(255);
      int b = rand.nextInt(255);
      RGB rgb = new RGB(RGB.ColorSpace.sRGB, new double[] {r, g, b},
                        RGB.MaxValue.Int8Bit);
      RGB[] aroundRGB = algo.getAroundRGB(rgb, 0.25);
      int size = aroundRGB.length;
      CIEXYZ[] aroundXYZ = new CIEXYZ[size];
      for (int y = 0; y < size; y++) {
        aroundXYZ[y] = aroundRGB[y].toXYZ();
      }
      double[] deltauvp = new double[size];
      double[] deltauv = new double[size];
//      double[] deltaE = new double[size];
      double[] deltaab = new double[size];
      deltauvp[0] = Double.MAX_VALUE;
//      deltaE[0] = Double.MAX_VALUE;
      deltaab[0] = Double.MAX_VALUE;
      deltauv[0] = Double.MAX_VALUE;
      CIEXYZ centerXYZ = aroundXYZ[0];
      CIExyY centerxyY = new CIExyY(centerXYZ);

      for (int y = 1; y < size; y++) {
        CIEXYZ XYZ = aroundXYZ[y];
        CIExyY xyY = new CIExyY(XYZ);

        double[] dupdvp = centerxyY.getDeltauvPrime(xyY);
        double duvp = Math.sqrt(Maths.sqr(dupdvp[0]) + Maths.sqr(dupdvp[1]));
        double[] dudv = centerxyY.getDeltauv(xyY);
        double duv = Math.sqrt(Maths.sqr(dudv[0]) + Maths.sqr(dudv[1]));
        deltauvp[y] = duvp;
        deltauv[y] = duv;

        DeltaE de = new DeltaE(centerXYZ, XYZ, whiteXYZ, false);
//        deltaE[y] = de.getCIE2000DeltaE();
        deltaab[y] = de.getCIE2000Deltaab();
      }
      int duvpIndex = Maths.minIndex(deltauvp);
//      int dEIndex = Maths.minIndex(deltaE);
      int dabIndex = Maths.minIndex(deltaab);
      int duvIndex = Maths.minIndex(deltauv);

      if (duvpIndex != dabIndex) {
        System.out.print(rgb);
        System.out.println(" " + duvpIndex + " " + dabIndex);
        double[] array0 = DoubleArray.getRangeCopy(deltauvp, 1,
            deltauvp.length - 1);
        double[] array1 = DoubleArray.getRangeCopy(deltaab, 1,
            deltaab.length - 1);
        System.out.println(DoubleArray.toString(df5, array0));
        System.out.println(DoubleArray.toString(df3, array1));
        System.out.println("");
        errorCount++;
      }

//      if (duvpIndex != duvIndex) {
//        System.out.print(rgb);
//        System.out.println(" " + duvpIndex + " " + duvIndex);
//        double[] array0 = DoubleArray.getRangeCopy(deltauvp, 1,
//            deltauvp.length - 1);
//        double[] array1 = DoubleArray.getRangeCopy(deltauv, 1,
//            deltauv.length - 1);
//        System.out.println(DoubleArray.toString(df5, array0));
//        System.out.println(DoubleArray.toString(df5, array1));
//        System.out.println("");
//        errorCount++;
//      }
    }
    System.out.println(errorCount);
  }
}
