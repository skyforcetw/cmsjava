package auo.cms.cm;

import flanagan.math.MinimisationFunction;
import shu.math.array.DoubleArray;
import shu.cms.colorspace.depend.*;
import flanagan.math.Minimisation;
import shu.math.*;

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
public class ColorMatrixCalculator2 {
  static class MiniFunction
      implements MinimisationFunction {
    private double r, g, b;
    public MiniFunction(double r, double g, double b) {
      this.r = r;
      this.g = g;
      this.b = b;
    }

    private double[][] getCM(double[] param) {
      double[][] cm = new double[3][3];
      cm[0][0] = r;
      cm[1][1] = g;
      cm[2][2] = b;

      double combine = 1;
      double distribute0 = param[1];
      double distribute1 = param[2];
      double distribute2 = param[3];
      double diff0 = r - combine;
      double diff1 = g - combine;
      double diff2 = b - combine;
      double g1 = diff0 * distribute0;
      double b1 = diff0 * (1 - distribute0);
      double r2 = diff1 * distribute1;
      double b2 = diff1 * (1 - distribute1);
      double r3 = diff2 * distribute2;
      double g3 = diff2 * (1 - distribute2);

      cm[0][1] = -g1;
      cm[0][2] = -b1;
      cm[1][0] = -r2;
      cm[1][2] = -b2;
      cm[2][0] = -r3;
      cm[2][1] = -g3;

      return cm;
    }

    double test(double[][] cm, RGB.Channel ch) {
      RGB.Channel[] channels = RGB.Channel.C.getPrimaryColorChannel(ch);
      double[] color = new double[3];
      for (RGB.Channel c : channels) {
        int index = c.getArrayIndex();
        color[index] = 0.5;
      }

      double[] result = DoubleArray.times(cm, color);
      double v0 = result[channels[0].getArrayIndex()];
      double v1 = result[channels[1].getArrayIndex()];
      return (v0 > v1) ? v0 / v1 : v1 / v0;
    }

    public double function(double[] param) {
      double[][] cm = getCM(param);
      double t0 = test(cm, RGB.Channel.Y);
      double t1 = test(cm, RGB.Channel.C);
      double t2 = test(cm, RGB.Channel.M);
      double result = t0 + t1 + t2;
      return result;
    }

  }

  static class MiniFunctionWithCMY
      extends MiniFunction {
    private double[] ymcgain = new double[3];
    public MiniFunctionWithCMY(double r, double g, double b, double cgain,
                               double mgain, double ygain) {
      super(r, g, b);
      ymcgain[0] = ygain;
      ymcgain[1] = mgain;
      ymcgain[2] = cgain;
    }

    double test(double[][] cm, RGB.Channel ch) {
      RGB.Channel[] channels = RGB.Channel.C.getPrimaryColorChannel(ch);
      double[] color = new double[3];
      for (RGB.Channel c : channels) {
        int index = c.getArrayIndex();
        color[index] = 0.5;
      }

      double[] result = DoubleArray.times(cm, color);
      double gain = ymcgain[ch.getArrayIndex() - 3];

      double v0 = result[channels[0].getArrayIndex()];
      double v1 = result[channels[1].getArrayIndex()];
      double v = v0 / v1;
      return Math.abs(v - gain);
    }

  }

  public static double[][] getOptimizedColorMatrix(double rgain, double ggain,
      double bgain) {
    MiniFunction mfunc = new MiniFunction(rgain, ggain, bgain);
    Minimisation min = new Minimisation();
    min.addConstraint(1, -1, 0);
    min.addConstraint(1, 1, 1);
    min.addConstraint(2, -1, 0);
    min.addConstraint(2, 1, 1);
    min.addConstraint(3, -1, 0);
    min.addConstraint(3, 1, 1);

    min.addConstraint(0, 0, 1);
    min.nelderMead(mfunc, new double[] {1, 0.5, 0.5, 0.5}, new double[] {0.1,
                   0.1, 0.1, 0.1});
    double[][] cm = mfunc.getCM(min.getParamValues());
    return cm;
  }

  public static double[][] getOptimizedColorMatrixWithCMY(double rgain,
      double ggain,
      double bgain, double cgain, double mgain, double ygain) {
    MiniFunction mfunc = new MiniFunctionWithCMY(rgain, ggain, bgain, cgain,
                                                 mgain, ygain);
    Minimisation min = new Minimisation();
    min.addConstraint(1, -1, 0);
    min.addConstraint(1, 1, 1);
    min.addConstraint(2, -1, 0);
    min.addConstraint(2, 1, 1);
    min.addConstraint(3, -1, 0);
    min.addConstraint(3, 1, 1);

    min.addConstraint(0, 0, 1);
    min.nelderMead(mfunc, new double[] {1, 0.5, 0.5, 0.5}, new double[] {0.1,
                   0.1, 0.1, 0.1});
    double[][] cm = mfunc.getCM(min.getParamValues());
    return cm;
  }

  public static void main(String[] args) {
    MiniFunction mfunc = new MiniFunction(1.12, 1.2661, 1.1987);
//    MiniFunction mfunc = new MiniFunction(1.465745623575,
//                                          1.434523452353,
//                                          1.3235376347569856);
    System.out.println(mfunc.function(new double[] {1, 0.5, 0.5, 0.5}));

    Minimisation min = new Minimisation();

    min.addConstraint(1, -1, 0);
    min.addConstraint(1, 1, 1);
    min.addConstraint(2, -1, 0);
    min.addConstraint(2, 1, 1);
    min.addConstraint(3, -1, 0);
    min.addConstraint(3, 1, 1);

    min.addConstraint(0, 0, 1);

//    min.nelderMead(mfunc, new double[] {1, 0.5, 0.5, 0.5});
    min.nelderMead(mfunc, new double[] {1, 0.5, 0.5, 0.5}, new double[] {0.1,
                   0.1, 0.1, 0.1});
    System.out.println("min: " + min.getMinimum());
    System.out.println(DoubleArray.toString(min.getParamValues()));
    double[][] cm = mfunc.getCM(min.getParamValues());
    System.out.println(DoubleArray.toString(cm));
    System.out.println(DoubleArray.toString(getOptimizedColorMatrix(1.12,
        1.2661, 1.1987)));
//    System.out.println(Maths.sum(cm[0]));
//    System.out.println(Maths.sum(cm[1]));
//    System.out.println(Maths.sum(cm[2]));
//
//    System.out.println(mfunc.function(min.getParamValues()));
//    mfunc.function(min.getParamValues());
    System.out.println(String.format("%d", 3));
  }
}
