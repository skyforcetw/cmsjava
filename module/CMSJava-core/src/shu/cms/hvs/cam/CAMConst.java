package shu.cms.hvs.cam;

import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 各種色適應,色外貿模式的常數
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public abstract class CAMConst {
  public enum CATType {
    vonKries, Bradford, CAT02, Hunt, IPT, CIECAM97s2
  }

  public final static double[][] getXYZ2LMSMatrix(CATType type) {
    switch (type) {
      case vonKries:
        return vonKries.M;
      case Bradford:
        return Bradford.M;
      case CAT02:
        return CIECAM02.M;
      case Hunt:
        return CIECAM02.MH;
      case IPT:
        return IPT.M;
      case CIECAM97s2:
        return CIECAM97s2.M;
      default:
        return null;
    }
  }

  public final static double[][] getLMS2XYZMatrix(CATType type) {
    switch (type) {
      case vonKries:
        return vonKries.M_inv;
      case Bradford:
        return Bradford.M_inv;
      case CAT02:
        return CIECAM02.M_inv;
      case Hunt:
        return CIECAM02.MH_inv;
      case IPT:
        return IPT.M_inv;
      case CIECAM97s2:
        return CIECAM97s2.M_inv;
      default:
        return null;
    }
  }

  public static void main(String[] args) {
    System.out.println(DoubleArray.toString(CIECAM97s2.M_inv));
  }

  public interface vonKries {
    /**
     * base on D65
     */
    double[][] M = new double[][] {
        {
        0.40024, 0.70760, -0.08081}, {
        -0.22630, 1.16532, 0.04570}, {
        0.0000, 0.0000, 0.91822}
    };

    double[][] M_inv = DoubleArray.inverse(M);

  }

  public interface IPT {
    /**
     * base on D65
     */
    double[][] M = new double[][] {
        {
        0.4002, 0.7075, -0.0807}, {
        -0.2280, 1.1500, 0.0612}, {
        0.0000, 0.0000, 0.9184}
    };
    double[][] M_inv = DoubleArray.inverse(M);
  }

  public interface Bradford {
    /**
     * base on E
     */
    double[][] M = new double[][] {
        {
        0.8951, 0.2664, -0.1614}, {
        -0.7502, 1.7135, 0.0367}, {
        0.0389, -0.0685, 1.0296}
    };

    double[][] M_inv = DoubleArray.inverse(M);

  }

  public interface CIECAM97s2 {
    final double[][] M = new double[][] {
        {
        0.8562, 0.3372, -0.1934}, {
        -0.8360, 1.8327, 0.0033}, {
        0.0357, -0.0469, 1.0112}
    };

    final double[][] M_inv = DoubleArray.inverse(M);

  }

  public interface CIECAM02 {
    /**
     * base on E (CAT02)
     */
    //(7)
    final double[][] M = new double[][] {
        {
        0.7328, 0.4296, -0.1624}, {
        -0.7036, 1.6975, 0.0061}, {
        0.0030, 0.0136, 0.9834}
    };

    final double[][] M_inv = DoubleArray.inverse(M);

    /**
     * HPE (Hunt-Pointer-Esteves) transform
     */
    final double[][] MH = new double[][] {
        {
        0.38971, 0.68898, -0.07868}, {
        -0.22981, 1.18340, 0.04641}, {
        0.00000, 0.00000, 1.00000}
    };

    final double[][] MH_inv = DoubleArray.inverse(MH);

    final double[][] MHmod = new double[][] {
        {
        0.39175, 0.68734, -0.07909}, {
        -0.23272, 1.18573, 0.04700}, {
        0.00300, 0.01360, 0.98340}
    };
    final double[][] MHmod_inv = DoubleArray.inverse(MHmod);

  }

  public interface Hunt {
    /**
     * base on E
     */
    double[][] M = new double[][] {
        //Hunt94 (12.4)
        {
        0.38971, 0.68898, -0.07868}, {
        -0.22981, 1.18340, 0.04641}, {
        0.0, 0.0, 1.0}
    };

    double[][] M_inv = DoubleArray.inverse(M);

  }
}
