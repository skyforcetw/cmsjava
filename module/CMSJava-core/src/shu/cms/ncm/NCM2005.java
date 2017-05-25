package shu.cms.ncm;

import static java.lang.Math.*;
import java.util.*;

import shu.cms.colorspace.depend.*;
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
public class NCM2005 {

  public NCM2005(double[][][] EArray, double[][][] FArray, double[][] aqpArray) {
    this.EArray = EArray;
    this.FArray = FArray;
    this.aqpArray = aqpArray;
  }

  public static void main(String[] args) {
    NCM2005 ncm = new NCM2005();
    for (int x = 0; x < 360; x += 7) {
      HSV hsv = new HSV(RGB.ColorSpace.sRGB, new double[] {x, 100, 100});
      RGB rgb = hsv.toRGB();
      double[] v = ncm.getRGBYMCValues(rgb);
      double[] hsvValues = NCM2005.getHSVValues(v);
      double rgbValues[] = {
           (v[3] + v[4]), (v[3] + v[5]), (v[4] + v[5])};
      rgbValues = DoubleArray.minus(rgbValues, DoubleArray.min(rgbValues));
      System.out.println(v[0] + " " + v[1] + " " + v[2] + " " +
                         Arrays.toString(rgbValues));
//      System.out.println(rgb + " " + hsv + " " + Arrays.toString(hsvValues) +
//                         " " + Arrays.toString(rgbymcValues));
    }

  }

  public NCM2005() {
    double[][] I = DoubleArray.diagonal(1, 1, 1);
    EArray = new double[][][] {
        I, I, I, I, I, I};
    double[][] F = new double[3][5];
    this.FArray = new double[][][] {
        F, F, F, F, F, F};
    double[] aqp = new double[] {
        1, 1};
    this.aqpArray = new double[][] {
        aqp, aqp, aqp, aqp, aqp, aqp};
  }

  private double[][][] EArray;
  private double[][][] FArray;
  private double[][] aqpArray;

  public static class ColorMatrix {
    private double[][][] EArray = new double[6][][];
    private double[][][] FArray = new double[6][][];
    private double[][] aqpArray = new double[6][];

    public void setE(int index, double[][] E) {
      EArray[index] = E;
    }

    public void setF(int index, double[][] F) {
      FArray[index] = F;
    }

    public void setaqp(int index, double aq, double ap) {
      aqpArray[index] = new double[] {
          aq, ap};
    }
  }

  public RGB getRGB(RGB in) {
    this.rgb = in;
    init();
    double[] rgbp = getRGBp();
    RGB out = (RGB)this.rgb.clone();
    out.setValues(rgbp);
    return out;
  }

  public double r;
  public double g;
  public double b;
  public double y;
  public double m;
  public double c;
  private RGBBase.Channel alpha;
  private RGBBase.Channel beta;
  private int S1;

  private RGB rgb;

  /**
   * function 11
   * @return double[]
   */
  protected final double[] getaqp() {
    return aqpArray[S1];
  }

  /**
   * part1 of function5
   * @return double[][]
   */
  protected double[][] getE() {
    return EArray[S1];
  }

  protected double[] getRGBo() {
    double[] rgb1 = getRGB1();
    double alphaValue = rgb.getValue(alpha);
    double[] rgbo = DoubleArray.plus(rgb1, alphaValue);
    return rgbo;
  }

  protected double[] getRGBp() {
    double[] rgbo = getRGBo();
    return rgbo;
  }

  protected double[] getRGB1() {
    double[][] E = getE();
    double[] rgbterm = DoubleArray.times(E, new double[] {r, g, b});

    double[][] F = getF();
    double[] T = getT();
    double[][] TtermResult = DoubleArray.times(F, DoubleArray.transpose(T));
    double[] Tterm = DoubleArray.transpose(TtermResult)[0];
    double[] rgb1 = DoubleArray.plus(rgbterm, Tterm);
    return rgb1;
  }

  /**
   * part2 of funtion5
   * @return double[][]
   */
  protected double[][] getF() {
    return FArray[S1];
  }

  /**
   * part2 of Block3
   * @return double[]
   */
  protected final double[] getT() {

    double[] QP = getQP();
    double Q1 = QP[0];
    double Q2 = QP[1];
    double P1 = QP[2];
    double P2 = QP[3];
    double[] T = new double[5];
    T[0] = P1 * P2;
    T[1] = min(P1, P2);
    T[2] = Q1 * Q2;
    T[3] = min(Q1, Q2);
    double[] aqp = getaqp();
    T[4] = min(aqp[1] * T[1], aqp[0] * T[3]);
    return T;
  }

  /**
   * part1 of Block3(function 7)
   * @return double[]
   */
  protected final double[] getQP() {

    switch (S1) {
      case 0:
        return new double[] {
            r, b, m, y};
      case 1:
        return new double[] {
            r, g, y, m};
      case 2:
        return new double[] {
            g, b, c, y};
      case 3:
        return new double[] {
            g, r, y, c};
      case 4:
        return new double[] {
            b, g, c, m};
      case 5:
        return new double[] {
            b, r, m, c};
      default:
        throw new IllegalStateException();
    }
  }

  protected void init() {
    //==========================================================================
    // block1
    //==========================================================================
    beta = rgb.getMaxChannel();
    alpha = rgb.getMinChannel();
    double betaValue = rgb.getValue(beta);
    double alphaValue = rgb.getValue(alpha);
    this.S1 = getS1(beta, alpha);
    //==========================================================================

    //==========================================================================
    // block2
    //==========================================================================
    this.r = rgb.R - alphaValue;
    this.g = rgb.G - alphaValue;
    this.b = rgb.B - alphaValue;
    this.y = betaValue - rgb.B;
    this.m = betaValue - rgb.G;
    this.c = betaValue - rgb.R;
    //==========================================================================
  }

  public double[] getRGBYMCValues(RGB rgb) {
    this.rgb = rgb;
    init();
    double[] rgbymcValues = new double[] {
        r, g, b, y, m, c};
    return rgbymcValues;
  }

  public final static double[] getHSVValues(double[] rgbymcValues) {
    double[] hsvValues = new double[3];
    hsvValues[2] = Maths.sum(rgbymcValues);
    hsvValues[1] = Maths.max(rgbymcValues) - Maths.min(rgbymcValues);
    double v[] = rgbymcValues;
    if (v[0] == 0) {
      //r==0,c
      hsvValues[0] = 180 + ( -60) * v[1] + 60 * v[2];
    }
    else if (v[1] == 0) {
      //g==0,m
      hsvValues[0] = 300 + ( -60) * v[2] + 60 * v[0];
    }
    else {
      //b==0,y
      hsvValues[0] = 60 + ( -60) * v[0] + 60 * v[1];
    }
    hsvValues[0] = hsvValues[0] % 360;

    return hsvValues;

  }

  protected final static int getS1(RGBBase.Channel beta, RGBBase.Channel alpha) {
    if (beta.equals(RGBBase.Channel.R)) {
      return alpha.index - 2;
    }
    else if (beta.equals(RGBBase.Channel.G)) {
      return alpha.equals(RGBBase.Channel.R) ? 2 : 3;
    }
    else {
      return alpha.equals(RGBBase.Channel.R) ? 4 : 5;
    }

  }

}
