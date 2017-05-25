package shu.math;

import shu.math.array.*;

//import shu.cms.plot.*;

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
public class Convolution {
  private Convolution() {

  }

//  public static void main(String[] args) {
//    double[] source = {
//        1, 4, 2, 6, 4, 8, 3, 6, 3, 6, 1, 7, 1, };
//    Plot2D plot = Plot2D.getInstance();
//    plot.setVisible(true);
//    plot.addLinePlot(null, 0, 1, source);
//    double[] result = convole(source, new double[] {1, 2, 3, 2, 1});
//    plot.addLinePlot(null, 0, 1, result);
//  }

  public final static double[] convole(final double[] source,
                                       final double[] kernel, int start,
                                       int end) {
    int size = source.length;
    double[] result = new double[size];
    double[] filter = new double[kernel.length];
    int kernelSize = kernel.length;
    int halfKernalSize = kernel.length / 2;
    double kernelTotal = Maths.sum(kernel);

    if (start != 0) {
      for (int x = 0; x < start; x++) {
        result[x] = source[x];
      }
    }

    for (int x = start; x < end; x++) {
      int filterStartIndex = x - halfKernalSize;
      if (filterStartIndex < 0) {
        System.arraycopy(source, x, filter, -filterStartIndex,
                         kernelSize + filterStartIndex);
        for (int y = 0; y < -filterStartIndex; y++) {
          filter[y] = filter[ -filterStartIndex];
        }
      }
      else if (filterStartIndex + kernelSize > size) {
        System.arraycopy(source, filterStartIndex, filter, 0,
                         size - filterStartIndex);
      }
      else {
        System.arraycopy(source, filterStartIndex, filter, 0, kernelSize);
      }
      result[x] = DoubleArray.times(filter, kernel) / kernelTotal;
    }

    if (end != source.length) {
      for (int x = end; x < source.length; x++) {
        result[x] = source[x];
      }
    }

    return result;

  }

  public final static double[] convole(final double[] source,
                                       final double[] kernel) {
    return convole(source, kernel, 0, source.length);
//    int size = source.length;
//    double[] result = new double[size];
//    double[] filter = new double[kernel.length];
//    int kernelSize = kernel.length;
//    int halfKernalSize = kernel.length / 2;
//    double kernelTotal = Maths.sum(kernel);
//
//    for (int x = 0; x < size; x++) {
//      int filterStartIndex = x - halfKernalSize;
//      if (filterStartIndex < 0) {
//        System.arraycopy(source, x, filter, -filterStartIndex,
//                         kernelSize + filterStartIndex);
//        for (int y = 0; y < -filterStartIndex; y++) {
//          filter[y] = filter[ -filterStartIndex];
//        }
//      }
//      else if (filterStartIndex + kernelSize > size) {
//        System.arraycopy(source, filterStartIndex, filter, 0,
//                         size - filterStartIndex);
//      }
//      else {
//        System.arraycopy(source, filterStartIndex, filter, 0, kernelSize);
//      }
//      result[x] = DoubleArray.times(filter, kernel) / kernelTotal;
//    }
//
//    return result;
  }
}
