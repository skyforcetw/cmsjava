package shu.math.regress;

import java.util.*;

import shu.math.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 嘗試以減少資料量(去除雜訊)達到回歸精準度的提高
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class ReductionPolynomialRegression
    extends PolynomialRegression {

  public ReductionPolynomialRegression(double[][] input, double[][] output,
                                       Polynomial.COEF_3 polynomialCoef) {
    super(input, output, polynomialCoef);
  }

  public ReductionPolynomialRegression(double[] input, double[] output,
                                       Polynomial.COEF_1 polynomialCoef) {
    super(input, output, polynomialCoef);
//  super(processPolynomialInput(input, polynomialCoef),
//        processRegressionOutput(output));
//  this.polynomialCoef = polynomialCoef;
  }

  /**
   * 最多減少的資料量
   */
  protected final double MAX_REDUCE = .5;
  protected int maxReduce;
  protected int reduce;

  public void regressBestDataSet() {
    if (coefs == null) {
      this.regress();
    }

    if (maxReduce == 0) {
      int size = output.length;
      maxReduce = (int) (size * MAX_REDUCE);
    }

    double originalRMSD = this.getRMSD();
    double rmsd = originalRMSD;

    for (int x = 1; x < maxReduce; x++) {
      reduceDataSet(x);
      this.regress();
      double newRMS = this.getRMS(oiginalInput, oiginalOutput);
      if (newRMS > originalRMSD || newRMS > rmsd) {
        //如果rms比之前的還要差,就代表這一筆資料不應該被消除掉.
        //所以把重新產生資料
        reduceDataSet(x - 1);
        //並且重新回歸
        this.regress();

        this.inputCoefs = oiginalInput;
        this.output = oiginalOutput;
        reduce = x - 1;
        break;
      }
      else {
        rmsd = newRMS;
      }
    }
  }

  protected double[][][] oiginalDataSetSortByRMS = null;
  protected double[][] oiginalInput;
  protected double[][] oiginalOutput;

  protected void reduceDataSet(int reduceCount) {

    if (oiginalDataSetSortByRMS == null) {
      //先保留最原本的資料
      oiginalInput = inputCoefs;
      oiginalOutput = output;

      //依照rms排序原始資料
      int size = output.length;
      double[] rmsd = new double[size];
      double[][] outputPredict = this.getPredict(inputCoefs, this.coefs);
      for (int x = 0; x < size; x++) {
        rmsd[x] = Maths.RMSD(output[x], outputPredict[x]);
      }
      double[][][] data = combine(rmsd, inputCoefs, output);
      Arrays.sort(data, new ItemComparator());
      oiginalDataSetSortByRMS = data;
    }

    int size = oiginalDataSetSortByRMS.length;
    int newSize = size - reduceCount;
    double[][] newInput = new double[newSize][];
    double[][] newOutput = new double[newSize][];
    for (int x = 0; x < newSize; x++) {
      newInput[x] = oiginalDataSetSortByRMS[x][1];
      newOutput[x] = oiginalDataSetSortByRMS[x][2];
    }
    this.inputCoefs = newInput;
    this.output = newOutput;

  }

  protected double getRMS(double[][] input, double[][] output) {
//    input = processPolynomialInput(input, polynomialCoef);
    double[][] predict = Regression.getPredict(input, this.coefs);
    return Maths.RMSD(predict, output);
  }

  protected static double[][][] combine(double[] rmsd, double[][] input,
                                        double[][] output) {
    int size = rmsd.length;
    double[][][] result = new double[size][3][];
    for (int x = 0; x < size; x++) {
      result[x][0] = new double[] {
          rmsd[x]};
      result[x][1] = input[x];
      result[x][2] = output[x];
    }
    return result;
  }

  protected static class ItemComparator
      implements Comparator<Object> {
    /**
     * Compares its two arguments for order.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *   argument is less than, equal to, or greater than the second.
     */
    public int compare(Object o1, Object o2) {
      return (int) Math.round( ( ( (double[][]) o1)[0][0] -
                                ( (double[][]) o2)[0][0]) * 10000);
    }

    /**
     * Indicates whether some other object is &quot;equal to&quot; this
     * Comparator.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> only if the specified object is also a
     *   comparator and it imposes the same ordering as this comparator.
     */
    public boolean equals(Object obj) {
      return compare(this, obj) == 0;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
      return super.hashCode();
    }
  }

  /**
   * 減少的資料量數目
   * @return int
   */
  public int getReduce() {
    return reduce;
  }

}
