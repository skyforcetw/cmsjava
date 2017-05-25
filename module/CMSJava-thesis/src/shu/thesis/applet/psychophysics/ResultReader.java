package shu.thesis.applet.psychophysics;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.commons.math.*;
import org.apache.commons.math.distribution.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * Z-Scoreªº­pºâ
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class ResultReader {

  public final static int[][][] resultFileParsing(String filename, int pics,
                                                  int methods) {
    File file = new File(filename);
    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      int[][][] accumulated = new int[pics][methods][methods];

      while (reader.ready()) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        StringTokenizer token = new StringTokenizer(line, "/");
        String img0 = token.nextToken();
        String img1 = token.nextToken();
        int judge = Integer.parseInt(token.nextToken());

        token = new StringTokenizer(img0, "_");
        int img = Integer.parseInt(token.nextToken());
        int method0 = Integer.parseInt(token.nextToken());

        token = new StringTokenizer(img1, "_");
        token.nextToken();
        int method1 = Integer.parseInt(token.nextToken());
        if (judge != 0) {
          accumulated[img][method0][method1]++;
        }
        else {
          accumulated[img][method1][method0]++;
        }
      }
      reader.close();
      return accumulated;
    }
    catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

    return null;
  }

  public final static void main(String[] args) throws IOException {
    DecimalFormat df = new DecimalFormat("##.###");

//    String dirName = "Psychophysics/D50";
    String dirName = "H:/Data/Psychophysics/F12";
//    String dirName = "H:/Data/Psychophysics/D50";

//    int[][][][] result = resultImageDirParsing(dirName, 7, 3);
//    System.out.println("");
    int[][][] result = resultDirParsing(dirName, 7, 3);
    System.out.println("select:\n" + replace(deepToString(result)) + "\n");

    double[][][] frequency = frequency(result);
    System.out.println("freq:\n" + replace(deepToString(df, frequency)) + "\n");

    double[][][] ZScore = ZScore(frequency);
    System.out.println("ZScore:\n" + replace(deepToString(df, ZScore)) + "\n");

    double[][] ZScoreTotal = ZTotalScore(ZScore);
    System.out.println("total:\n" + replace(toString(df, ZScoreTotal)));

  }

  protected final static String replace(String str) {
    str = str.replaceAll(" 0,", "¡V ");
    str = str.replaceAll("0\\]\\]", "¡V \n");
    str = str.replaceAll("\\[\\[0,", "¡V ");

    str = str.replaceAll(",", " ");
    str = str.replaceAll("\\[\\[", "");
    str = str.replaceAll("\\]  \\[", "\n");
    str = str.replaceAll("\\]\\]", "\n");
//    str.replaceAll("]","\n");
//    str.replaceAll(", [","");
    return str;
  }

  protected final static double[][] ZTotalScore(double[][][] ZScore) {
    int m = ZScore.length;
    int n = ZScore[0].length;
    double[][] total = new double[m][n];

    for (int p = 0; p < m; p++) {
      double[][] t = DoubleArray.transpose(ZScore[p]);
//      double[][] t = ZScore[p];
      for (int x = 0; x < n; x++) {
        total[p][x] = Maths.sum(t[x]);
      }
    }

    return total;
  }

  protected final static double[][][] frequency(int[][][] accumulated) {
    int m = accumulated.length;
    int n = accumulated[0].length;
    double[][][] result = new double[m][n][n];

    for (int p = 0; p < m; p++) {
      for (int y = 0; y < n; y++) {
        for (int x = 0; x < n; x++) {
          if (x != y) {
            result[p][y][x] = ( (double) accumulated[p][y][x]) /
                (accumulated[p][y][x] + accumulated[p][x][y]);
            /*if (result[p][y][x] == 1.) {
              result[p][y][x] = 1. - Double.MIN_VALUE;
                         }
                         else if (result[p][y][x] == 0.) {
              result[p][y][x] = Double.MIN_VALUE;
                         }*/
          }
        }
      }
    }

    return result;
  }

  protected final static double[][][] ZScore(double[][][] frequency) {
    int m = frequency.length;
    int n = frequency[0].length;
    double[][][] result = new double[m][n][n];
    NormalDistribution normDist = new NormalDistributionImpl();

    try {
      for (int p = 0; p < m; p++) {
        for (int y = 0; y < n; y++) {
          for (int x = 0; x < n; x++) {
            if (x != y) {
              result[p][y][x] = normDist.inverseCumulativeProbability(frequency[
                  p][y][x]);
            }
          }
        }
      }

    }
    catch (MathException ex) {
      ex.printStackTrace();
    }

    return result;
  }

  protected final static int[][][] plus(int[][][] a, int[][][] b) {
    if (a.length != b.length || a[0].length != b[0].length ||
        a[0][0].length != b[0][0].length || a[0].length != b[0][0].length) {
      throw new IllegalArgumentException(
          "Matrix dimension is not equal.");
    }

    int m = a.length;
    int n = a[0].length;
    int[][][] result = new int[m][n][n];

    for (int p = 0; p < m; p++) {
      for (int y = 0; y < n; y++) {
        for (int x = 0; x < n; x++) {
          result[p][y][x] = a[p][y][x] + b[p][y][x];
        }
      }
    }
    return result;
  }

  /**
   *
   * @param dirname String
   * @param pics int
   * @param methods int
   * @return int[][][]
   */
  public final static int[][][] resultDirParsing(String dirname, int pics,
                                                 int methods) {
    int[][][] totalResult = new int[pics][methods][methods];

    File dir = new File(dirname);
    for (File file : dir.listFiles(new ResultFilter())) {
      int[][][] result = resultFileParsing(file.getPath(), pics, methods);
      totalResult = plus(result, totalResult);
    }
    return totalResult;
  }

  /**
   *
   * @param dirname String
   * @param pics int
   * @param methods int
   * @return int[][][][]
   * @deprecated
   */
  public final static int[][][][] resultImageDirParsing(String dirname,
      int pics,
      int methods) {
    File dir = new File(dirname);
    File[] files = dir.listFiles(new ResultFilter());
    int[][][][] totalResult = new int[files.length][pics][methods][methods];
    int index = 0;

    for (File file : dir.listFiles(new ResultFilter())) {
      int[][][] result = resultFileParsing(file.getPath(), pics, methods);
      totalResult[index++] = result;
    }
    return totalResult;
  }

  protected static class ResultFilter
      implements FilenameFilter {
    public boolean accept(File dir, String name) {
      return name.endsWith(".txt");
    }
  }

  protected final static String deepToString(int[][][] intArray) {
    StringBuilder buf = new StringBuilder();
    for (int[][] array : intArray) {
      buf.append(Arrays.deepToString(array));
      buf.append('\n');
    }
    return buf.toString();
  }

  protected final static String deepToString(double[][][] doubleArray) {
    StringBuilder buf = new StringBuilder();
    for (double[][] array : doubleArray) {
      buf.append(Arrays.deepToString(array));
      buf.append('\n');
    }
    return buf.toString();
  }

  /*protected final static String deepToString(DecimalFormat df,
                                             double[][][] doubleArray) {
    StringBuilder buf = new StringBuilder();
    for (double[][] array : doubleArray) {
      buf.append(Arrays.deepToString(array));
      buf.append('\n');
    }
    return buf.toString();
     }*/

  protected final static String deepToString(DecimalFormat df,
                                             double[][][] doubleArray) {
    StringBuilder buf = new StringBuilder('[');
    for (double[][] d : doubleArray) {
      buf.append(toString(df, d) + "\n");
    }
//    buf.append(']');
    return buf.toString();
  }

  protected final static String toString(DecimalFormat df,
                                         double[][] doubleArray) {
    StringBuilder buf = new StringBuilder("[");
    for (double[] d : doubleArray) {
      buf.append(toString(df, d) + ", ");
    }
    buf.delete(buf.length() - 2, buf.length());
    buf.append(']');
    return buf.toString();
  }

  protected final static String toString(DecimalFormat df, double[] doubleArray) {
    StringBuilder buf = new StringBuilder("[");
    for (double d : doubleArray) {
      buf.append(df.format(d) + ", ");
    }
//    buf.delete(buf.length()-3,2);
    buf.delete(buf.length() - 2, buf.length());
    buf.append(']');
    return buf.toString();
  }
}
