package shu.io.files;

import java.io.*;

import shu.math.*;
import shu.math.array.*;

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
public class BinaryFile
    extends org.math.io.files.BinaryFile {
  public BinaryFile(File f, String endian) {
    super(f, endian);
  }

  public static void main(String[] args) {
  }

  public void writeDoubleArray(double[][] array) {
    this.writeDoubleArray(DoubleArray.to1DDoubleArray(array), true);
//    int size = array.length;
//    for (int x = 0; x < size; x++) {
//      this.writeDoubleArray(array[x], true);
//    }
  }

  public double[][] readDoubleArray(int columns) {
    double[] data = readDoubleArray();
    return DoubleArray.to2DDoubleArray(data, columns);
  }

  public static void writeDoubleArray(File f, double[][] array, String endian) {
    BinaryFile bf = new BinaryFile(f, endian);
    bf.writeDoubleArray(array);
  }

  public static void writeDoubleArray(String filename, double[][] array) {
    writeDoubleArray(new File(filename), array, LITTLE_ENDIAN);
  }

  public static double[][] readDoubleArray(File f, String endian, int columns) {
    BinaryFile bf = new BinaryFile(f, endian);
    return bf.readDoubleArray(columns);
  }

  public static void writeDoubleArray(String filename, double[] array) {
    writeDoubleArray(new File(filename), array, LITTLE_ENDIAN);
  }

  public static double[][] readDoubleArray(String filename, int columns) {
    return readDoubleArray(new File(filename), LITTLE_ENDIAN, columns);
  }

  public static double[] readDoubleArray(String filename) {
    return readDoubleArray(new File(filename), LITTLE_ENDIAN);
  }

}
