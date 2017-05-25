package shu.math;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class EigenvalueDecomposition {
  protected Jama.EigenvalueDecomposition eig;
  public EigenvalueDecomposition(Matrix matrix) {
    eig = new Jama.EigenvalueDecomposition(matrix.matrix);
  }

  public Matrix getV() {
    return new Matrix(eig.getV());
  }

  public double[] getRealEigenvalues() {
    return eig.getRealEigenvalues();
  }
}
