package shu.math;

import java.io.*;
import java.util.*;

//import Jama.Matrix;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 提供基本矩陣運算功能,目前為Jama.Matrix的Wrapper.
 * (Matrix Toolkits for Java  http://rs.cipr.uib.no/mtj/)
 *
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @todo L MTJ搭配NNI (NNI為原生程式,有較佳的效能)
 */
public class Matrix
    implements Serializable {
  protected Jama.Matrix matrix;
  public Matrix(double[][] A) {
    matrix = new Jama.Matrix(A);
  }

  protected Matrix(Matrix m) {
    this.matrix = m.matrix.copy();
  }

  protected Matrix(Jama.Matrix m) {
    this.matrix = m.copy();
  }

  public double[][] getArray() {
    return matrix.getArray();
  }

  /** Linear algebraic matrix multiplication, A * B
      @param B    another matrix
      @return     Matrix product, A * B
      @exception  IllegalArgumentException Matrix inner dimensions must agree.
   */
  public Matrix times(Matrix B) {
    return new Matrix(matrix.times(B.matrix));
  }

  /** Matrix inverse or pseudoinverse
      @return     inverse(A) if A is square, pseudoinverse otherwise.
   */

  public Matrix inverse() {
    return new Matrix(matrix.inverse());
  }

  public double det() {
    return matrix.det();
  }

  public Matrix plus(Matrix B) {
    return new Matrix(matrix.plus(B.matrix));
  }

  public Matrix minus(Matrix B) {
    return new Matrix(matrix.minus(B.matrix));
  }

  public Matrix transpose() {
    return new Matrix(matrix.transpose());
  }

  public String toString() {
    return matrix.toString();
  }

  /** Solve A*X = B
    @param B    right hand side
    @return     solution if A is square, least squares solution otherwise
   */
  public Matrix solve(Matrix B) {
    return new Matrix(matrix.solve(B.matrix));
  }

  /** Solve X*A = ,B which is also A'*X' = B'
    @param B    right hand side
    @return     solution if A is square, least squares solution otherwise.
   */
  public Matrix solveTranspose(Matrix B) {
    return new Matrix(matrix.solveTranspose(B.matrix));
  }

  public Matrix arrayLeftDivide(Matrix B) {
    return new Matrix(matrix.arrayLeftDivide(B.matrix));
  }

  public Matrix arrayRightDivide(Matrix B) {
    return new Matrix(matrix.arrayRightDivide(B.matrix));
  }

  /** Get a single element.
      @param i    Row index.
      @param j    Column index.
      @return     A(i,j)
      @exception  ArrayIndexOutOfBoundsException
   */
  public double get(int i, int j) {
    return matrix.get(i, j);
  }

  /** Is the matrix nonsingular?
    @return     true if U, and hence A, is nonsingular.
   */

  public boolean isNonsingular() {
    double[][] LU = matrix.getArray();
    int n = matrix.getColumnDimension();
    for (int j = 0; j < n; j++) {
      if (LU[j][j] == 0) {
        return false;
      }
    }
    return true;
  }

  public static void main(String[] args) {

    double[][] a = new double[][] {
        {
        3, 5}, {
        5, -1}
    };
    double[][] b = new double[][] {
        {
        10}, {
        8}

    };
    Matrix ma = new Matrix(a);
    Matrix mb = new Matrix(b);
    double[][] ans = ma.solve(mb).getArray();
    System.out.println(Arrays.deepToString(ans));
  }

  public void print(int w, int d) {
    matrix.print(w, d);
  }

  public EigenvalueDecomposition eig() {
    return new EigenvalueDecomposition(this);
  }

}
