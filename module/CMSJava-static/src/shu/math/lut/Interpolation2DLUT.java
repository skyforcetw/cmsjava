package shu.math.lut;

import flanagan.interpolation.*;
import shu.math.*;
import shu.util.*;

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
public class Interpolation2DLUT
    implements LookUpTable {

  public static enum Algo {
    BILINEAR, BICUBIC
  }

  public Interpolation2DLUT(double[] xkeys, double[] ykeys, double[][] values,
                            Algo algo) {
    this.algo = algo;
    switch (algo) {
      case BILINEAR:
        bilinear = new BiLinear(xkeys, ykeys, values);
        break;
      case BICUBIC:
        bicubic = new BiCubicSpline(xkeys, ykeys, values);
        break;
    }
  }

  public double[] getValues(double[] keys) {
    double result = getValue(keys[0], keys[1]);
    return new double[] {
        result};
  }

  public double[] getKeys(double[] values) {
    throw new UnsupportedOperationException();
  }

  public double getValue(double keyX, double keyY) {
    switch (algo) {
      case BILINEAR:
        return bilinear.interpolate(keyX, keyY);
      case BICUBIC:
        return bicubic.interpolate(keyX, keyY);
      default:
        return -1;
    }
  }

  protected BiCubicSpline bicubic;
  protected BiLinear bilinear;
  protected Algo algo;

  public final static class BiLinear {
    // Constructors
    public BiLinear(double[] xkeys, double[] ykeys,
                    double[][] values) {
      initData(xkeys, ykeys, values);
    }

    // Fields
    private int nPoints;
    private int mPoints;
    private double[][] v;
    private double[] xkeys;
    private double[] ykeys;
    private double[] min;
    private double[] max;

    // Methods

    private void initData(double[] xkeys, double[] ykeys,
                          double[][] values) {
      this.xkeys = xkeys;
      this.ykeys = ykeys;
      v = values;

      min = new double[] {
          Maths.min(this.xkeys), Maths.min(this.ykeys)};
      max = new double[] {
          Maths.max(this.xkeys), Maths.max(this.ykeys)};
      mPoints = this.xkeys.length;
      nPoints = this.ykeys.length;
    }

    public double[] getXmin() {
      return min;
    }

    public double[] getXmax() {
      return max;
    }

    private double[] getQ(int xIndex, int yIndex) {
      double Q11 = v[xIndex][yIndex];
      double Q21 = v[xIndex][yIndex + 1];
      double Q12 = v[xIndex + 1][yIndex];
      double Q22 = v[xIndex + 1][yIndex + 1];
      return new double[] {
          Q11, Q21, Q12, Q22};
    }

    private double[][] getXY12(int xIndex, int yIndex) {
      double x1 = xkeys[xIndex];
      double x2 = xkeys[xIndex + 1];
      double y1 = ykeys[yIndex];
      double y2 = ykeys[yIndex + 1];
      return new double[][] {
          {
          x1, x2}, {
          y1, y2}
      };
    }

    public double interpolate(double x, double y) {
      //x的根
      int xIndex = Searcher.leftBinarySearch(this.xkeys, x);
      //y的根
      int yIndex = Searcher.leftBinarySearch(this.ykeys, y);
      if (xIndex < 0 || yIndex < 0) {
        throw new IllegalArgumentException();
      }
      //四個values
      double[] Q = getQ(xIndex, yIndex);
      //四個key
      double[][] xy12 = getXY12(xIndex, yIndex);
      double[] X = xy12[0];
      double[] Y = xy12[1];
      return Interpolation2D.bilinear(X, Y, Q, x, y);
    }

  }

  public static void main(String[] args) {
    double[] x = new double[] {
        1, 3, 5};
    double[] y = new double[] {
        7, 8, 9};
    double[][] vals = new double[][] {
        {
        5, 3, 1}, {
        6, 4, 2}, {
        7, 5, 3}
    };

    Interpolation2DLUT lut1 = new Interpolation2DLUT(x, y, vals, Algo.BILINEAR);
//    Interpolation2DLUT lut2 = new Interpolation2DLUT(x, y, vals, Algo.BICUBIC);
    System.out.println(lut1.getValue(3, 7));
//    System.out.println(lut2.getValue(3.1, 2.4));
  }
}
