package shu.cms.measure.test;

import java.util.*;

import java.awt.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.measure.*;
import shu.cms.measure.meter.*;
import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 測試色塊是否會受到量測順序的影響
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class DisplayStableTester {
  public static void main(String[] args) {
//    for (int x = 32; x < 254; x += 15) {
//      test(new RGB(x, x, x));
//    }
//    System.out.println("test end");

//    randomTest(3);

//    int i = 9;
//    RandomIndex ri = new RandomIndex(i);
//    for (int x = 0; x < i; x++) {
//      System.out.println(ri.nextIndex());
//    }

    randomTest(5, 3);
  }

  private static Patch measureStable(RGB code, int times) {
    Patch p = null;
    for (int x = 0; x < times; x++) {
      p = mm.measure(code, null);
    }
    return p;
  }

  private static Patch measureAndInsert(RGB code, RGB insert) {
//    mm.measure(insert, null);
//    return mm.measure(code, null);
    return measureAndStepInsert(code, insert, 0);
  }

  private static Patch measureAndStepInsert(RGB code, RGB insert, int step) {
//    mm.measure(insert, null);
//    mm.setBlank(insert.getColor());
    mm.setBlankAndBackground(insert.getColor(), mm.getBackgroundColor());

    for (int x = step; x > 0; x--) {
      RGB rgb = (RGB) code.clone();
      rgb.addValues( -x);
//      System.out.println(rgb);
      mm.measure(rgb, null);
    }

    return mm.measure(code, null);
  }

  private final static MeterMeasurement getMeterMeasurement() {
    if (mm == null) {
      RemoteMeter meter = RemoteMeter.getDefaultInstance();
      mm = new MeterMeasurement(meter, false);
      mm.setDoBlankInsert(true);
      mm.setBlankTimes(17);
    }
    return mm;
  }

  private static MeterMeasurement mm = null;

  private static double max = Double.MIN_VALUE;
  private static double min = Double.MAX_VALUE;
  private static void setMaxAndMin(Patch p) {
    double Y = p.getXYZ().Y;
    max = Y > max ? Y : max;
    min = Y < min ? Y : min;
  }

  private static class RandomIndex {
    int[] indexArray;
//    Random rand = new Random(0);
    Random rand = new Random();
    int finalIndex;
    RandomIndex(int n) {
      indexArray = new int[n];
      for (int x = 0; x < n; x++) {
        indexArray[x] = x;
      }
      finalIndex = n;
    }

    int nextIndex() {
      if (finalIndex < 1) {
        throw new IllegalStateException("finalIndex < 1");
      }
      int index = rand.nextInt(finalIndex);
      int result = indexArray[index];
      indexArray[index] = indexArray[finalIndex - 1];
      finalIndex--;
      return result;
    }
  }

  private final static RGB[] getRandomRGB(int n) {
    Random rand = new Random(0);
    RGB[] rgbArray = new RGB[n];
    for (int x = 0; x < n; x++) {
      int v = rand.nextInt(256);
      rgbArray[x] = new RGB(v, v, v);
    }
    return rgbArray;
  }

  public final static void randomTest(int n, int times) {
    RGB[] rgbArray = getRandomRGB(n);
    Arrays.sort(rgbArray);
    double[][] lumiArray = new double[n][times];
    getMeterMeasurement();
    RGB white = new RGB(254, 254, 254);

    mm.setDoBlankInsert(false);
//    mm.setDoBlankInsert(true);
//    mm.setDoSecondBlankInsert(true);
//    mm.setBlankTimes(9);
//    mm.setBlank(white.getColor());
//    mm.setBlank(Color.white);
    mm.setBlankAndBackground(Color.white, mm.getBackgroundColor());
//    mm.setBlank(white.getColor());
//    mm.setSecondBlank(Color.black);
//    mm.setBlank(Color.black);
//    mm.setSecondBlank(Color.white);
//    mm.setSecondBlank(white.getColor());
//    mm.setWaitTimes(900);

    for (int x = 0; x < times; x++) {
      RandomIndex ri = new RandomIndex(n);
//      mm.measure(RGB.White, null);
      for (int y = 0; y < n; y++) {
//        int index = ri.nextIndex(); //亂數
//        int index = n - y - 1; //反向
        int index = y; //正向
        Patch p = mm.measure(rgbArray[index], null);
        System.out.println("mea " + p);
        lumiArray[index][x] = p.getXYZ().Y;
      }
    }
    Record[] records = new Record[n];
    for (int x = 0; x < n; x++) {
      double std = Maths.std(lumiArray[x]);
      double min = Maths.min(lumiArray[x]);
      records[x] = new Record(rgbArray[x], std, std / min);
    }
    Arrays.sort(records);
    for (int x = 0; x < n; x++) {
      Record rec = records[x];
      System.out.println(rec);
    }
  }

  private static class Record
      implements Comparable {
    RGB rgb;
    double std;
    double stdmin;
    Record(RGB rgb, double std, double stdmin) {
      this.rgb = rgb;
      this.std = std;
      this.stdmin = stdmin;
    }

    /**
     * Compares this object with the specified object for order.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *   is less than, equal to, or greater than the specified object.
     */
    public int compareTo(Object o) {
      return rgb.compareTo( ( (Record) o).rgb);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
      return rgb + " std:" + std + " std/min:" + (stdmin);
    }

  }

  public final static void test(RGB rgb) {
//    double min = Double.MAX_VALUE;
//    double max = Double.MIN_VALUE;
    max = Double.MIN_VALUE;
    min = Double.MAX_VALUE;
    getMeterMeasurement();
    System.out.println("\ntest: " + rgb);
    mm.setDoBlankInsert(true);
//    Patch stable = measureStable(rgb, 10);
//    setMaxAndMin(stable);
//    System.out.println("stable: " + stable.getXYZ().Y);

    Patch insertBlack = measureAndInsert(rgb, RGB.Black);
    setMaxAndMin(insertBlack);

    Patch insertWhite = measureAndInsert(rgb, new RGB(254, 254, 254));
    setMaxAndMin(insertWhite);

    System.out.println("insert black: " + insertBlack.getXYZ().Y);
    System.out.println("insert white: " + insertWhite.getXYZ().Y);

    mm.setDoBlankInsert(false);
    for (int step = 1; step < 7; step++) {
      Patch stepPatch = measureAndStepInsert(rgb, RGB.Black, step);
      setMaxAndMin(stepPatch);
    }

    System.out.println("max: " + max + " min: " + min + " (" +
                       ( (max - min) / min) * 100 + "%)");

  }

}
