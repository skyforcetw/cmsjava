package shu.math.regress;

import java.util.*;

//import shu.cms.*;
//import shu.cms.colorspace.independ.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 提供迴歸運算
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class Regression {
  /**
   * 經過多項式係數處理後的input值
   * 處在右側,也就是會與回歸係數相乘
   */
  protected double[][] inputCoefs;
  protected double[][] output;
  protected double[][] coefs;

  /**
   * 直接輸入已經過多項式處理的變數
   * @param input double[][]
   * @param output double[][]
   */
  public Regression(double[][] input, double[][] output) {
    this.inputCoefs = input;
    this.output = output;
  }

  /**
   *
   * @param val double[]
   * @return double[][]
   * @deprecated
   */
  private static double[][] transpose(double[] val) {
    int size = val.length;
    double[][] result = new double[size][];
    for (int x = 0; x < size; x++) {
      result[x] = new double[] {
          val[x]};
    }
    return result;
  }

  /**
   * 1-1回歸
   * @param input double[]
   * @param output double[]
   */
  public Regression(double[] input, double[] output) {
    this.inputCoefs = DoubleArray.transpose(input);
    this.output = DoubleArray.transpose(output);
  }

  public double[][] getCoefs() {
    return coefs;
  }

  public void setCoefs(double[][] coefs) {
    this.coefs = coefs;
  }

  /**
   * 進行回歸運算
   */
  public void regress() {
    SVDLib svdLib = new SVDLib(inputCoefs);
//    double[][] U = svdLib.getU();
//    double[][] S = DoubleArray.diagonal(svdLib.getSingularValues());
//    double[][] V = svdLib.getV();
//    double[][] org = DoubleArray.times(DoubleArray.times(U, S), V);
//    System.out.println(DoubleArray.toString(org));

    int items = output.length;
    int outSize = output[0].length;
    coefs = new double[outSize][];

    for (int x = 0; x < outSize; x++) {
      double[] singleOutput = new double[items];
      for (int y = 0; y < items; y++) {
        singleOutput[y] = output[y][x];
      }
      double[] singleCoefs = svdLib.getCoefficients(singleOutput);
      //System.out.println(Arrays.toString(singleCoefs));
      coefs[x] = singleCoefs;
    }
  }

  public final double[][] getPredict() {
    return getPredict(inputCoefs, coefs);
  }

  public final static double[][] getPredict(double[][] input, double[][] coefs) {
    int size = coefs.length;
    double[][] predict = new double[size][];
    for (int x = 0; x < size; x++) {
      predict[x] = SVDLib.getPredict(input, coefs[x]);
    }
    return DoubleArray.transpose(predict);
  }

  public double getRMSD() {
    double[][] predict = getPredict();
    return Maths.RMSD(predict, output);
  }

  public double getrSquare() {
    double[][] predict = getPredict();
    return Maths.rSquare(output, predict);
  }

//  public DeltaEReport[] getDeltaEReport(CIEXYZ white) {
//    double[][] predict = getPredict();
//    List<Patch> predictPatchList = producePatchList(predict, white);
//    List<Patch> outputPatchList = producePatchList(output, white);
//    return DeltaEReport.Instance.patchReport(predictPatchList, outputPatchList, true);
//  }
//
//  protected List<Patch> producePatchList(double[][] data, CIEXYZ white) {
//    int size = data.length;
//    List<Patch> patchList = new ArrayList<Patch> (size);
//    for (int x = 0; x < size; x++) {
//      CIEXYZ XYZ = new CIEXYZ(data[x], white);
//      CIELab Lab = CIELab.fromXYZ(XYZ, white);
//      Patch p = new Patch(null, null, Lab, null);
//      patchList.add(p);
//    }
//    return patchList;
//  }

  public static void main(String[] args) {
    //double[][] sv={{1,2,3},{4,5,6},{7,8,9}};
    //SVDLib svd=new SVDLib(sv);


    int count = 10;
    double[][] RGB = new double[count][3];
    double[][] XYZ = new double[count][3];

    for (int x = 0; x < count; x++) {
      for (int y = 0; y < 3; y++) {
        RGB[x][y] = x + 100 + y;
        XYZ[x][y] = RGB[x][y] * 10;
      }
    }
    //System.out.println(DoubleArray.toString(RGB));
    //System.out.println(DoubleArray.toString(XYZ));
    Polynomial.COEF_3[] coefsArray = new Polynomial.COEF_3[] {
        Polynomial.COEF_3.BY_3,
        Polynomial.COEF_3.BY_3C, Polynomial.COEF_3.BY_6,
        Polynomial.COEF_3.BY_6C};
//    Polynomial.COEF_3[] coefsArray = Polynomial.COEF_3.values();

    for (Polynomial.COEF_3 coefs : coefsArray) {
      /*double[][] polyCoefs = new double[RGB.length][];
             for (int y = 0; y < polyCoefs.length; y++) {
        polyCoefs[y] = Polynomial.getCoef(RGB[y], coefs);
             }
             Regression regression = new Regression(polyCoefs, XYZ);*/
      PolynomialRegression regression = new PolynomialRegression(RGB, XYZ,
          coefs);

      regression.regress();

      double[][] d = regression.getPredict(new double[][] { {105, 106, 107}
      }, regression.getCoefs());
      System.out.println(Arrays.deepToString(d));
      System.out.println(Arrays.deepToString(regression.getCoefs()));
    }

  }
}
