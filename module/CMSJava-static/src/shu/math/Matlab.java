package shu.math;

import java.util.*;
import javax.media.jai.*;

import java.awt.image.*;

import shu.jai.*;
import shu.jai.jaistuff.*;
import shu.math.array.*;
import shu.util.*;

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
public final class Matlab {

  protected final static boolean CONVOLVE_BY_JAI = true;

  public static double[] flip(double[] A) {
    int size = A.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = A[size - 1 - x];
    }
    return result;
  }

  public static double[][] fliplr(double[][] A) {
    int mSize = A.length;
    double[][] result = new double[mSize][];
    for (int x = 0; x < mSize; x++) {
      result[x] = flip(A[x]);
    }
    return result;
  }

  public static double[][] flipud(double[][] A) {
    int mSize = A.length;
    int nSize = A[0].length;
    double[][] result = new double[mSize][nSize];
    for (int x = 0; x < mSize; x++) {
      System.arraycopy(A[mSize - 1 - x], 0, result[x], 0, nSize);
    }
    return result;
  }

  public static float[] toKernelMatrix(double[][] kernel) {
    int m = kernel.length;
    int n = kernel[0].length;
    float[] result = new float[m * n];
    int index = 0;

    for (int x = 0; x < m; x++) {
      for (int y = 0; y < n; y++) {
        result[index++] = (float) kernel[x][y];
      }
//      System.arraycopy(kernel[x], 0, result, x * n, n);
    }

    return result;
  }

  protected static double[][] padZero(double[][] array, int firstPad,
                                      int endPad) {

    return pad(array, firstPad, firstPad, endPad, endPad);
//    int width = array[0].length;
//    int height = array.length;
//    int pad = firstPad + endPad;
//    double[][] result = new double[height + pad][width + pad];
//    for (int x = 0; x < height; x++) {
//      System.arraycopy(array[x], 0, result[x + firstPad], firstPad, width);
//    }
//    return result;

  }

  protected static double[][] pad(double[][] array, int top, int topLeft,
                                  int bottom, int bottomRight) {
    int width = array[0].length;
    int height = array.length;
    double[][] result = new double[height + top + bottom][width + topLeft +
        bottomRight];
    for (int x = top; x < top + height; x++) {
      System.arraycopy(array[x - top], 0, result[x], topLeft, width);
    }
    return result;
  }

  protected static double[][] unpad(double[][] array, int top, int topLeft,
                                    int bottom, int bottomRight) {
    int width = array[0].length - topLeft - bottomRight;
    int height = array.length - top - bottom;
    double[][] result = new double[height][width];
    for (int x = top; x < top + height; x++) {
      System.arraycopy(array[x], topLeft, result[x - top], 0, width);
    }
    return result;
  }

  protected static double[][] insertRows(double[][] x, int J, double[] ...y) {
    return org.math.array.DoubleArray.insertRows(x, J, y);
  }

  protected static double[][] deleteRowsRange(double[][] x, int I1, int I2) {
    return org.math.array.DoubleArray.deleteRowsRange(x, I1, I2);
  }

  protected static float[] padZero(float[] array, int width, int height) {
    int size = (width + 1) * (height + 1);
    float[] result = new float[size];
    int origIndex = 0;
    for (int x = 0; x < size; x++) {
      if (origIndex < array.length && ( (x + 1) % (width + 1) != 0)) {
        result[x] = array[origIndex++];
      }
      else {
        result[x] = 0;
      }
    }
    return result;
  }

  /**
   *
   * @param array double[][]
   * @return double[][]
   * @deprecated
   */
  protected static double[][] unpad(double[][] array) {
    return unpadEnd(array, 1);
  }

  /**
   *
   * @param array double[][]
   * @param pad int
   * @return double[][]
   * @deprecated
   */
  protected static double[][] unpadEnd(double[][] array, int pad) {
    int width = array[0].length - pad;
    int height = array.length - pad;

    double[][] result = new double[height][width];
    for (int x = 0; x < height; x++) {
      System.arraycopy(array[x], 0, result[x], 0, width);
    }

    return result;
  }

  protected static float[] unpad(float[] array, int originalWidth,
                                 int originalHeight) {
    int origSize = originalWidth * originalHeight;
    int size = array.length;
    float[] result = new float[origSize];
    int origIndex = 0;

    for (int x = 0; x < size; x++) {
      if (origIndex < origSize && ( (x + 1) % (originalWidth + 1) != 0)) {
        result[origIndex++] = array[x];
      }
    }

    return result;
  }

  protected static double[][] conv2Same(double[][] A, double[][] B) {
    return conv2Same(A, B, 0, 0, 0, 0);
  }

  protected static double[][] conv2Same(double[][] A, double[][] B, int topPad,
                                        int topLeftPad, int bottomPad,
                                        int bottomRightPad) {
    int sizeB = Math.max(B.length, B[0].length);
    int pad = sizeB / 2;

    double[][] padA = pad(A, topPad + pad, topLeftPad + pad, bottomPad + pad,
                          bottomRightPad + pad);
    double[][] result = null;
    if (CONVOLVE_BY_JAI) {
      result = convolve2JAI(padA, B);
    }
    else {
      result = convolve2(padA, B);
    }
    result = unpad(result, topPad + pad, topLeftPad + pad, bottomPad + pad,
                   bottomRightPad + pad);
    return result;
  }

  public enum Conv2Type {
    Same, Full, Mathematica
  }

  public static double[][] conv2(double[][] A, double[][] B, Conv2Type shape) {
    double[][] result = null;

    switch (shape) {
      case Same:
        result = conv2Same(A, B);
        break;
      case Full: {
        int top = B.length / 2;
        int topLeft = B[0].length / 2;
        int bottom = (B.length - 1) / 2;
        int bottomRight = (B[0].length - 1) / 2;
        result = A;
        result = conv2Same(result, B, top, topLeft, bottom, bottomRight);
        break;
      }
      case Mathematica: {
        int sizeB = Math.max(B.length, B[0].length);
        int pad = sizeB / 2;

        int top = B.length / 2 + pad;
        int topLeft = B[0].length / 2 + pad;
        int bottom = (B.length - 1) / 2 + pad;
        int bottomRight = (B[0].length - 1) / 2 + pad;
        result = A;
        result = pad(result, top, topLeft, bottom, bottomRight);
//        result = convolve2(result, B);
        if (CONVOLVE_BY_JAI) {
          result = convolve2JAI(result, B);
        }
        else {
          result = convolve2(result, B);
        }
        result = unpad(result, top, topLeft, bottom, bottomRight);
        break;

      }
      default:
        break;
    }

    return result;
  }

  public static double[][] convolve2JAI(
      double[][] threeDPix, double[][] filter) {
    float[] floatA = toKernelMatrix(threeDPix);
    int width = threeDPix[0].length;
    int height = threeDPix.length;

    floatA = padZero(floatA, width, height);

    TiledImage image = ImageCreator.createGrayImage(floatA, width + 1,
        height + 1);
    PlanarImage result = RegionOperators.smooth(image, toKernelMatrix(filter),
                                                filter[0].length, filter.length);
    Raster raster = result.getData();
    floatA = raster.getPixels(0, 0, width + 1, height + 1, floatA);

    floatA = unpad(floatA, width, height);
    return FloatArray.toDoubleArray(floatA, width, height);
  }

  public static double[][] convolve2(
      double[][] threeDPix, double[][] filter) {

    //Get the dimensions of the image and filter arrays.
    int numImgRows = threeDPix.length;
    int numImgCols = threeDPix[0].length;
    int numFilRows = filter.length;
    int numFilCols = filter[0].length;

    //Make a working copy of the incoming 3D pixel array to
    // avoid making permanent changes to the original image
    // data. Convert the pixel data to type double in the
    // process.  Will convert back to type int when
    // returning from this method.
    double[][] work3D = threeDPix;

    //Create an empty output array of the same size as the
    // incoming array of pixels.
    double[][] output =
        new double[numImgRows][numImgCols];

    //Copy the alpha values directly to the output array.
    // They will not be processed during the convolution
    // process.
    for (int row = 0; row < numImgRows; row++) {
//      for (int col = 0; col < numImgCols; col++) {
//        output[row][col] = 0;
      Arrays.fill(output[row], 0);
//      } //end inner loop
    } //end outer loop

//Because of the length of the following statements, and
// the width of this publication format, this format
// sacrifices indentation style for clarity. Otherwise,it
// would be necessary to break the statements into so many
// short lines that it would be very difficult to read
// them.

//Use nested for loops to perform a 2D convolution of each
// color plane with the 2D convolution filter.
//    System.out.println(filter.length+" "+filter[0].length);
    for (int yReg = numFilRows - 1; yReg < numImgRows; yReg++) {
      for (int xReg = numFilCols - 1; xReg < numImgCols; xReg++) {
//        System.out.println("xx");
        for (int filRow = 0; filRow < numFilRows; filRow++) {
          for (int filCol = 0; filCol < numFilCols; filCol++) {

            output[yReg - numFilRows / 2][xReg - numFilCols / 2] +=
                work3D[yReg - filRow][xReg - filCol] *
                filter[filRow][filCol];
//            System.out.println(filter[filRow][filCol]);
//            System.out.println("x");

          } //End loop on filCol
        } //End loop on filRow
      } //End loop on xReg
    } //End loop on yReg

    return output;
  } //end convolve method

//-----------------------------------------------------//




  public static double[][] getSubMatrixRangeCopy(double[][] M, int i1, int i2,
                                                 int j1, int j2) {
    return org.math.array.DoubleArray.getSubMatrixRangeCopy(M, i1, i2, j1, j2);
  }

  public static double[][] abs(double[][] array) {
    int height = array.length;
    int width = array[0].length;
    double[][] result = new double[height][width];

    for (int x = 0; x < height; x++) {
      for (int y = 0; y < width; y++) {
        result[x][y] = Math.abs(array[x][y]);
      }
    }
    return result;
  }

  public static double[][] assign(double[][] array1, int mStart1, int mEnd1,
                                  int nStart1, int nEnd1, double[][] array2,
                                  int mStart2, int mEnd2, int nStart2,
                                  int nEnd2) {
    int mSize = mEnd1 - mStart1 + 1;
    int nSize = nEnd1 - nStart1 + 1;
    for (int m = 0; m < mSize; m++) {
      for (int n = 0; n < nSize; n++) {
        array1[m + mStart1 - 1][n + nStart1 - 1] = array2[m + mStart2 - 1][n +
            nStart2 - 1];
      }
    }
    return array1;
  }

  public static double[][] ones(int m, int n) {
    double[][] ones = new double[m][n];
//    System.out.println(ones.length+" "+ones[0].length);
    for (int x = 0; x < ones.length; x++) {
      Arrays.fill(ones[x], 1);
    }
//    System.out.println(ones.length+" "+ones[0].length);
    return ones;
  }

  public static double[][] concatArrayAtNextColumn(double[][] original,
      double[][] concated) {
    if (original.length != concated.length) {
      throw new IllegalArgumentException(
          "original.length != concated .length");
    }

    int origN = original[0].length;
    int conN = concated[0].length;
    int m = original.length;
    int resultN = origN + conN;
    double[][] result = new double[m][resultN];

    for (int x = 0; x < m; x++) {
      System.arraycopy(original[x], 0, result[x], 0, origN);
      System.arraycopy(concated[x], 0, result[x], origN, conN);
    }

    return result;
  }

  public static double[][] concatArrayAtNextRow(double[][] original,
                                                double[][] concated) {
    if (original[0].length != concated[0].length) {
      throw new IllegalArgumentException(
          "original[0].length != concated[0].length");
    }

    int origM = original.length;
    int conM = concated.length;
    int n = original[0].length;
    int resultM = origM + conM;
    double[][] result = new double[resultM][n];

    for (int x = 0; x < origM; x++) {
      System.arraycopy(original[x], 0, result[x], 0, n);
    }
    for (int x = origM; x < resultM; x++) {
      System.arraycopy(concated[x - origM], 0, result[x], 0, n);
    }

    return result;
  }

  public static double[] concatArray(double[] array1, double[] array2) {
    return Utils.concatArray(array1, array2);
  }

  public static int[] concatArray(int[] array1, int[] array2) {
    return Utils.concatArray(array1, array2);
  }

  public static double[][] makeArrayByColumnIndex(double[][] array, int[] index) {
    int mSize = array.length;
    int nSize = index.length;
    double[][] result = new double[mSize][nSize];
    for (int x = 0; x < nSize; x++) {
      for (int y = 0; y < mSize; y++) {
        result[y][x] = array[y][index[x]];
      }
    }
    return result;
  }

  public static double[] makeArrayByRange(int start, int interval,
                                          int end) {
    if (interval > 0 && end < start) {
      return new double[0];
    }
    int size = (end - start) / interval + 1;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = start + x * interval;
    }
    return result;
  }

  public static long ceil(double value) {
    return Math.round(value);
  }

  public static double[] dotTimes(double[] a, double[] b) {
    if (a.length != b.length) {
      throw new IllegalArgumentException("a.length != b.length");
    }
    int size = a.length;
    double[] result = new double[size];
    for (int x = 0; x < size; x++) {
      result[x] = a[x] * b[x];
    }
    return result;
  }

  public static int[] less(double[] array, double lessValue) {
    int size = array.length;
    int[] result = new int[size];
    for (int x = 0; x < size; x++) {
      result[x] = array[x] < lessValue ? 1 : 0;
    }
    return result;
  }

  public static int[] less(int[] array, int lessValue) {
    int size = array.length;
    int[] result = new int[size];
    for (int x = 0; x < size; x++) {
      result[x] = array[x] < lessValue ? 1 : 0;
    }
    return result;
  }

  public static void main(String[] args) {
    double[][] m = DoubleArray.fill(4000, 3000, 3);
    for (int x = 0; x < 10; x++) {
      long start = System.currentTimeMillis();
      pad(m, 1, 1, 1, 1);
      System.out.println(System.currentTimeMillis() - start);

    }

  }

  public static int[] find(int[] array) {
    int size = array.length;
    ArrayList<Integer> list = new ArrayList<Integer> (size);
    for (int x = 0; x < size; x++) {
      if (array[x] != 0) {
        list.add(x);
      }
    }
    int indexSize = list.size();
    int[] result = new int[indexSize];
    for (int x = 0; x < indexSize; x++) {
      result[x] = list.get(x).intValue();
    }
    return result;
  }
}
