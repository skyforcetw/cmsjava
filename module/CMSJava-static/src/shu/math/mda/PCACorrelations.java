package shu.math.mda;

import java.io.*;
import java.text.*;

import shu.math.*;

//import Jama.*;

/**
 * PCAcorr - Principal Components Analysis on correlations <p>
 * Jama Matrix class package, "JAMA: A Java Matrix Package" is used.<br>
 * See: http://math.nist.gov/javanumerics/jama <p>
 * Example of use: <p>
 * <tt> javac PCAcorr.java </tt> <br>
 * <tt> java PCAcorr <a href="../iris.dat">iris.dat</a> >
 * <a href="pcaoutput.txt">pcaoutput.txt</a></tt> <p>
 * Format of input data set:
 * <ul>
 * <li> integer row and column dimensions,
 * <li> followed by floating values
 * <li> which are read row-wise.
 * </ul>
 * Outputs produced:
 * <ul>
 * <li> Echo of input file name, input dimensions, sample of data
 * <li> Variable means and standard deviations
 * <li> Correlation matrix to be diagonalized
 * <li> Eigenvectors
 * <li> Eigenvalues and as cumulative percentages
 * <li> Row projections in new principal component space
 * <li> Column projections in new principal component space
 * </ul>
 * Version: 2002 Aug. 15 <br>
 * Author: F. Murtagh, f.murtagh@qub.ac.uk
 * @version 2002 Aug 15
 * @author F. Murtagh, f.murtagh@qub.ac.uk
 */
public class PCACorrelations {

  public static void main(String argv[]) {
    PrintStream out = System.out;

    try {

      //-------------------------------------------------------------------
      if (argv.length == 0) {
        System.out.println(" Syntax: java PCAcorr infile.dat ");
        System.out.println(" Input file format: ");
        System.out.println(" Line 1: integer no. rows, no. cols.");
        System.out.println(" Successive lines: matrix values, floating");
        System.out.println(" Read in row-wise");
        System.exit(1);
      }
      String filname = argv[0];
      System.out.println(" Input file name: " + filname);

      // Open the matrix file
      FileInputStream is = new FileInputStream(filname);
      BufferedReader bis = new BufferedReader(new InputStreamReader(is));
      StreamTokenizer st = new StreamTokenizer(bis);

      // Row and column sizes, read in first
      st.nextToken();
      int n = (int) st.nval;
      st.nextToken();
      int m = (int) st.nval;

      System.out.println(" No. of rows, n = " + n);
      System.out.println(" No. of cols, m = " + m);

      // Input array, values to be read in successively, float
      double[][] indat = new double[n][m];
      double inval;

      // New read in input array values, successively
//      System.out.println
//          (" Input data sample follows as a check, first 4 values.");
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
          st.nextToken();
          inval = (double) st.nval;
          indat[i][j] = inval;
//          if (i < 2 && j < 2) {
//            System.out.println(" value = " + inval);
//          }
        }
      }
//      System.out.println();

      //-------------------------------------------------------------------
      // Data preprocessing - standardization and determining correlations
      double[][] indatstd = standardize(n, m, indat);
      // use Jama matrix class
      Matrix x = new Matrix(indatstd);

      // Sums of squares and cross-products matrix
      Matrix xPrime = x.transpose();
      Matrix sscp = xPrime.times(x);
      // Note the following:
      // - with no preprocessing of the input data, we have an SSCP matrix
      // - with centering of columns (i.e. each col. has col. mean
      //   [vector in row-space] subtracted) we have variances/covariances
      // - with centering and reduction to unit variance [i.e. centered
      //   cols. are divided by std. dev.] we have correlations
      // Note: the current version supports correlations only

      // Print out SSCP matrix
      // Parameters: floating value width, and no. of decimal places
      // Comment out this line for large data sets, and/or diff. precision
      System.out.println();
      System.out.println
          (" SSCP or sums-of-squares and cross-products matrix:");
      System.out.println(" (Note: correlations in this implementation)");
      sscp.print(6, 2);

      //-------------------------------------------------------------------
      // Eigen decomposition
      EigenvalueDecomposition evaldec = sscp.eig();
      Matrix evecs = evaldec.getV();
      double[] evals = evaldec.getRealEigenvalues();

      // print out eigenvectors
      System.out.println(" Eigenvectors (leftmost col <--> largest eval):");
      // evecs contains the cols. ordered right to left
      // Evecs is the more natural order with cols. ordered left to right
      // So to repeat: leftmost col. of Evecs is assoc with largest Evals
      // Evals and Evecs ordered from left to right

      double tot = 0.0;
      for (int j = 0; j < evals.length; j++) {
        tot += evals[j];
      }

      // reverse order of evals into Evals
      double[] Evals = new double[m];
      for (int j = 0; j < m; j++) {
        Evals[j] = evals[m - j - 1];
      }
      // reverse order of Matrix evecs into Matrix Evecs
      double[][] tempold = evecs.getArray();
      double[][] tempnew = new double[m][m];
      for (int j1 = 0; j1 < m; j1++) {
        for (int j2 = 0; j2 < m; j2++) {
          tempnew[j1][j2] = tempold[j1][m - j2 - 1];
        }
      }
      Matrix Evecs = new Matrix(tempnew);
      Evecs.print(10, 4);

      System.out.println();
      System.out.println(" Eigenvalues and as cumulative percentages:");
      double runningtotal = 0.0;
      double[] percentevals = new double[m];
      for (int j = 0; j < Evals.length; j++) {
        percentevals[j] = runningtotal + 100.0 * Evals[j] / tot;
        runningtotal = percentevals[j];
      }
      printVect(Evals, 4, 10);
      printVect(percentevals, 4, 10);

      //-------------------------------------------------------------------
      // Projections - row, and col
      // Row projections in new space, X U  Dims: (n x m) x (m x m)
      System.out.println();
      System.out.println(" Row projections in new principal component space:");
      Matrix rowproj = x.times(Evecs);
      rowproj.print(10, 4);

      // Col projections (X'X) U    (4x4) x4  And col-wise div. by sqrt(evals)
      Matrix colproj = sscp.times(Evecs);

      // We need to leave colproj Matrix class and instead use double array
      double[][] ynew = colproj.getArray();
      for (int j1 = 0; j1 < m; j1++) {
        for (int j2 = 0; j2 < m; j2++) {
          if (Evals[j2] > 0.00005) {
            ynew[j1][j2] = ynew[j1][j2] / Math.sqrt(Evals[j2]);
          }
          if (Evals[j2] <= 0.00005) {
            ynew[j1][j2] = 0.0;
          }
        }
      }
      System.out.println();
      System.out.println(" Column projections in new princ. comp. space.");
      printMatrix(m, m, ynew, 4, 8);

      //-------------------------------------------------------------------
      // That's it.

    }

    catch (IOException e) {
      out.println("error: " + e);
      System.exit(1);
    }

  } // end of main

  //-------------------------------------------------------------------
  // Little method for helping in output formating
  public static String getSpaces(int n) {

    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) {
      sb.append(' ');
    }
    return sb.toString();
  } // getSpaces

  //-------------------------------------------------------------------
  /**
   * Method for printing a matrix  <br>
   * Based on ER Harold, "Java I/O", O'Reilly, around p. 473.
   * @param n1 row dimension of matrix
   * @param n2 column dimension of matrix
   * @param m input matrix values
   * @param d display precision, number of decimal places
   * @param w display precision, total width of floating value
   */
  public static void printMatrix(int n1, int n2, double[][] m, int d, int w) {
    // Some definitions for handling output formating
    NumberFormat myFormat = NumberFormat.getNumberInstance();
    FieldPosition fp = new FieldPosition(NumberFormat.INTEGER_FIELD);
    myFormat.setMaximumIntegerDigits(d);
    myFormat.setMaximumFractionDigits(d);
    myFormat.setMinimumFractionDigits(d);
    for (int i = 0; i < n1; i++) {
      // Print each row, elements separated by spaces
      for (int j = 0; j < n2; j++)
      // Following unfortunately doesn't format at all
      //                  System.out.print(m[i][j] + "  ");
      {
        String valString = myFormat.format(
            m[i][j], new StringBuffer(), fp).toString();
        valString = getSpaces(w - fp.getEndIndex()) + valString;
        System.out.print(valString);
      }
      // Start a new line at the end of a row
      System.out.println();
    }
    // Leave a gap after the entire matrix
    System.out.println();
  } // printMatrix

  //-------------------------------------------------------------------
  /**
   * Method printVect for printing a vector <br>
   * Based on ER Harold, "Java I/O", O'Reilly, around p. 473.
   * @param m input vector of length m.length
   * @param d display precision, number of decimal places
   * @param w display precision, total width of floating value
   */
  public static void printVect(double[] m, int d, int w) {
    // Some definitions for handling output formating
    NumberFormat myFormat = NumberFormat.getNumberInstance();
    FieldPosition fp = new FieldPosition(NumberFormat.INTEGER_FIELD);
    myFormat.setMaximumIntegerDigits(d);
    myFormat.setMaximumFractionDigits(d);
    myFormat.setMinimumFractionDigits(d);
    int len = m.length;
    for (int i = 0; i < len; i++) {
      // Following would be nice, but doesn't format adequately
      //                  System.out.print(m[i] + "  ");
      String valString = myFormat.format(
          m[i], new StringBuffer(), fp).toString();
      valString = getSpaces(w - fp.getEndIndex()) + valString;
      System.out.print(valString);
    }
    // Start a new line at the row end
    System.out.println();
    // Leave a gap after the entire vector
    System.out.println();
  } // printVect

  //-------------------------------------------------------------------
  /**
   * Method for standardizing the input data <p>
   * Note the formalas used (since these vary between implementations):<br>
   * reduction: (vect - meanvect)/sqrt(nrow)*colstdev <br>
   * colstdev:  sum_cols ((vect - meanvect)^2/nrow) <br>
   * if colstdev is close to 0, then set it to 1.
   * @param nrow number of rows in input matrix
   * @param ncol number of columns in input matrix
   * @param A input matrix values
   */
  public static double[][] standardize(int nrow, int ncol, double[][] A) {
    double[] colmeans = new double[ncol];
    double[] colstdevs = new double[ncol];
    // Adat will contain the standardized data and will be returned
    double[][] Adat = new double[nrow][ncol];
    double[] tempcol = new double[nrow];
    double tot;

    // Determine means and standard deviations of variables/columns
    for (int j = 0; j < ncol; j++) {
      tot = 0.0;
      for (int i = 0; i < nrow; i++) {
        tempcol[i] = A[i][j];
        tot += tempcol[i];
      }

      // For this col, det mean
      colmeans[j] = tot / (double) nrow;
      for (int i = 0; i < nrow; i++) {
        colstdevs[j] += Math.pow(tempcol[i] - colmeans[j], 2.0);
      }
      colstdevs[j] = Math.sqrt(colstdevs[j] / ( (double) nrow));
      if (colstdevs[j] < 0.0001) {
        colstdevs[j] = 1.0;
      }
    }

//    System.out.println(" Variable means:");
//    printVect(colmeans, 4, 8);
//    System.out.println(" Variable standard deviations:");
//    printVect(colstdevs, 4, 8);

    // Now ceter to zero mean, and reduce to unit standard deviation
    for (int j = 0; j < ncol; j++) {
      for (int i = 0; i < nrow; i++) {
        Adat[i][j] = (A[i][j] - colmeans[j]) /
            (Math.sqrt( (double) nrow) * colstdevs[j]);
      }
    }
    return Adat;
  } // Standardize
  //-------------------------------------------------------------------




} // PCAcorr
