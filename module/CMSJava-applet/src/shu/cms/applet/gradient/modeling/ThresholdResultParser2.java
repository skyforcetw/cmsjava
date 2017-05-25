package shu.cms.applet.gradient.modeling;

import java.io.*;
import java.io.File;
import java.util.*;
import java.util.List;

import java.awt.*;

import jxl.read.biff.*;
import shu.io.files.ExcelFile;
import shu.cms.colorspace.depend.*;
import shu.cms.hvs.*;
import shu.cms.lcd.*;
import shu.cms.lcd.experiment.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
import shu.math.regress.*;
import shu.util.log.*;
//import shu.plot.*;

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
public class ThresholdResultParser2 {
  public ThresholdResultParser2(String dirname) {
    this(dirname, null);
  }

  public ThresholdResultParser2(String dirname, LCDTarget lcdTarget) {
    parseDir(dirname);
//    this.lcdTarget = lcdTarget;
    if (lcdTarget != null) {
      calibrator = new DICOMCalibrator(lcdTarget);
      calibrator.produceJNDILUT(254);
    }
  }

  private DICOMCalibrator calibrator = null;
//  private LCDTarget lcdTarget;


  /**
   * 以GSDF的基準計算平均亮度
   * @param target LCDTarget
   * @param maxcode double
   * @param startJNDI double
   * @return double
   */
  public final static double getMeanLuminanceWithGSDF(LCDTarget target,
      double maxcode, double startJNDI) {
    double maxY = target.getPatch(RGBBase.Channel.W, maxcode,
                                  RGBBase.MaxValue.Double255).getXYZ().Y;
    double maxJNDI = GSDF.getDICOMInstance().getJNDIndex(maxY);
    double deltaJNDI = (maxJNDI - startJNDI) / 256.;
    double totalY = 0;
    int index = 0;
    for (double jndi = startJNDI; jndi <= maxJNDI; jndi += deltaJNDI) {
      double Y = GSDF.getDICOMInstance().getLuminance(jndi);
      totalY += Y;
      index++;
    }
    double meanY = totalY / index;

    return meanY;
  }

  private Type type = Type.All;

  public final static void multiRegress(List < double[][] > acceptCoefs,
                                        List < double[][] > unacceptCoefs,
                                        double[] meanArray) {
    //==========================================================================
    // printf 變數
    //==========================================================================
    int coefssize = acceptCoefs.size();
    int inputsize = coefssize * 4;
    double[][] inputaccept = new double[inputsize][3];
    double[][] outputaccept = new double[inputsize][3];
    double[][] inputunaccept = new double[inputsize][3];
    double[][] outputunaccept = new double[inputsize][3];
    int indexaccept = 0;
    int indexunaccept = 0;
    for (int x = 0; x < coefssize; x++) {
      double mean = meanArray[x];
      System.out.println(mean);
      double[][] accept = acceptCoefs.get(x);
      for (double[] a : accept) {
        System.out.println("o " + a[0] + "%= " + a[1] + "+" + a[2] + "x");
        inputaccept[indexaccept][0] = a[0];
//        inputaccept[indexaccept][1] = a[0];
        outputaccept[indexaccept][0] = a[1];
        outputaccept[indexaccept][1] = a[2];
        indexaccept++;
      }

      double[][] unaccept = unacceptCoefs.get(x);
      for (double[] a : unaccept) {
        System.out.println("x " + a[0] + "%= " + a[1] + "+" + a[2] + "x");
        inputunaccept[indexunaccept][0] = mean;
        inputunaccept[indexunaccept][1] = a[0];
        outputunaccept[indexunaccept][0] = a[1];
        outputunaccept[indexunaccept][1] = a[2];
        indexunaccept++;
      }
//      index++;
    }

    //==========================================================================
    // regress
    //==========================================================================
    Polynomial.COEF_3 coef = Polynomial.COEF_3.BY_3;
    PolynomialRegression regressaccept = new PolynomialRegression(inputaccept,
        outputaccept, coef);
    regressaccept.regress();
    System.out.println("accept: ");
    System.out.println("c=" + DoubleArray.toString(regressaccept.getCoefs()[0]));
    System.out.println("a=" + DoubleArray.toString(regressaccept.getCoefs()[1]));
    System.out.println("rmsd: " + regressaccept.getRMSD() + " r2: " +
                       regressaccept.getrSquare());

    PolynomialRegression regressunaccept = new PolynomialRegression(
        inputunaccept, outputunaccept, coef);
    regressunaccept.regress();
    System.out.println("unaccept: ");
    System.out.println("c=" + DoubleArray.toString(regressunaccept.getCoefs()[0]));
    System.out.println("a=" + DoubleArray.toString(regressunaccept.getCoefs()[1]));
    System.out.println("rmsd: " + regressunaccept.getRMSD() + " r2: " +
                       regressunaccept.getrSquare());
    //==========================================================================
  }

  public static void main(String[] args) {
    LCDTarget target = ContrastThresholdJudge2.getLCDTarget();
    String[] dirs = new String[] {
        "254", "192", "128", "64"};
    int size = dirs.length;
    double[] meanArray = new double[size];
    double[][] coefsArray = new double[size][];
    int index = 0;

    Plot2D p = Plot2D.getInstance();
    int colorindex = 0;
    List<double[][]> acceptCoefs = new ArrayList<double[][]> ();
    List<double[][]> unacceptCoefs = new ArrayList<double[][]> ();

    //==========================================================================
    //  解析
    //==========================================================================
    for (String dir : dirs) {
      ThresholdResultParser2 parser = new ThresholdResultParser2(
          "ExperimentResult/" + dir, target);
      parser.plotMultiResult(dir);
//      parser.printCoefs();
      Color c = Plot2D.getNewColor(colorindex++);
      parser.plot(p, dir, c, true);
      PolynomialRegression regress = parser.getRegressUnaccept();
      double mean = getMeanLuminanceWithGSDF(target, Double.parseDouble(dir),
                                             40);
      double[] coefs = regress.getCoefs()[0];
      meanArray[index] = mean;
      coefsArray[index++] = coefs;

      acceptCoefs.add(parser.getAcceptPercentAndCoefs());
      unacceptCoefs.add(parser.getUnacceptPercentAndCoefs());
    }
    //==========================================================================

    p.drawCachePlot();
    p.setVisible();

    //==========================================================================
    // printf 變數
    //==========================================================================
    multiRegress(acceptCoefs, unacceptCoefs, meanArray);
    //==========================================================================

    //==========================================================================
    // o ave fitting
    //==========================================================================
//    double[] JNDIArray = Utils.list2DoubleArray(jndiList);
//    double[] acceptAveArray = Utils.list2DoubleArray(acceptList);
//    PolynomialRegression regressAccept = new PolynomialRegression(JNDIArray,
//        acceptAveArray, Polynomial.COEF_1.BY_1C);
//    regressAccept.regress();
//    int arraysize = JNDIArray.length;
//    for (int x = 0; x < arraysize; x++) {
//      double jndi = JNDIArray[x];
//      if (x > 0 && jndi < JNDIArray[x - 1]) {
//        break;
//      }
//      double predict = regressAccept.getPredict(new double[] {jndi})[0];
//      p.addCachexyLinePlot("o ave", jndi, predict);
//    }
//    System.out.println("o ave: " +
//                       Polynomial.COEF_1.BY_1C.toString(
//                           regressAccept.getCoefs()[0]));
    //==========================================================================

    //==========================================================================
    // 平均亮度與 a b 係數的關係
    //==========================================================================
//    coefsArray = DoubleArray.transpose(coefsArray);
//    CompositePolynomialRegression regressUnaccept = new
//        CompositePolynomialRegression(meanArray, coefsArray[0], coefsArray[1],
//                                      Polynomial.COEF_1.BY_1C,
//                                      CompositePolynomialRegression.Type.
//                                      Parallel);
//    regressUnaccept.regress();
//
//    System.out.println(Polynomial.COEF_1.BY_1C.toString(regressUnaccept.
//        getCoefs1()[0]));
//    System.out.println(Polynomial.COEF_1.BY_1C.toString(regressUnaccept.
//        getCoefs2()[0]));
    //==========================================================================

    //==========================================================================
    // 將可接受線與亮度之間關係描繪出來
    //==========================================================================
//    Plot2D p2 = Plot2D.getInstance("mean vs ");
//    for (double meanL = 10; meanL <= 200; meanL += 20) {
//      double[][] predict = regressUnaccept.getMultiPredict(
//          new double[] {meanL});
//      for (double jndi = 40; jndi <= 700; jndi += 10) {
//        double deltajndi = predict[0][0] + predict[1][0] * jndi;
//        p2.addCachexyLinePlot("mean " + Double.toString(meanL) + " nits", jndi,
//                              deltajndi);
//      }
//    }
//    p2.setAxeLabel(0, "JNDI");
//    p2.setAxeLabel(1, "delta JNDI\"");
//    p2.addLegend();
//    p2.setVisible();
    //==========================================================================
  }

  private int fileCount = 0;
  private Map<String, double[][]> map = new HashMap<String, double[][]> ();
  private int rows = -1;
  private double jndiEnd = -1;

  protected static enum Type {
    All, NonInverse, Inverse
  }

  protected void parseFile(String filename) {
    try {
      ExcelFile excelFile = new ExcelFile(filename);
      if (jndiEnd == -1) {
        jndiEnd = excelFile.getCell(8, 2);
      }
      int size = excelFile.getRows();
      size = (size == 4) ? 3 : size;
      if (rows == -1) {
        rows = size;
      }
      else if (rows != size) {
        throw new java.lang.IllegalStateException("rows is not equal.");
      }
      double[][] result = new double[3][size];

      for (int x = 0; x < size; x++) {
        double JNDI = excelFile.getCell(0, x);
        double acceptDeltaJNDI = excelFile.getCell(1, x);
        double unacceptDeltaJNDI = excelFile.getCell(2, x);
        if (Double.isNaN(acceptDeltaJNDI) || Double.isNaN(unacceptDeltaJNDI)) {
          throw new IllegalStateException(
              "Double.isNaN(acceptDeltaJNDI) || Double.isNaN(unacceptDeltaJNDI): " +
              filename);
        }
        result[0][x] = JNDI;
        result[1][x] = acceptDeltaJNDI;
        result[2][x] = unacceptDeltaJNDI;
      }
      map.put(filename, result);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (BiffException ex) {
      Logger.log.error("", ex);
    }

  }

  protected void parseDir(String dirname) {
    File dir = new File(dirname);
    if (dir == null || !dir.exists()) {
      return;
    }
    for (String filename : dir.list()) {
      if (filename.lastIndexOf(".xls") != -1 && filename.charAt(0) != '_') {
        switch (type) {
          case NonInverse:
            if (filename.indexOf('i') != -1) {
              continue;
            }
            break;
          case Inverse:
            if (filename.indexOf('i') == -1) {
              continue;
            }
            break;
        }
        String absfilename = dir.getPath() + "/" + filename;
        parseFile(absfilename);
        fileCount++;
      }
    }
    if (fileCount == 0) {
      return;
    }
    int size = map.size();
    int judgeTimes = rows;
    acceptResultsArray = new double[judgeTimes][size];
    unacceptResultsArray = new double[judgeTimes][size];
    int index = 0;

    //==========================================================================
    // 將實驗數據匯總起來
    //==========================================================================
    for (double[][] result : map.values()) {
      if (JNDIArray == null) {
        JNDIArray = new double[judgeTimes];
        System.arraycopy(result[0], 0, JNDIArray, 0, judgeTimes);
      }
      for (int x = 0; x < judgeTimes; x++) {
        acceptResultsArray[x][index] = result[1][x];
        unacceptResultsArray[x][index] = result[2][x];
      }
      index++;
    }
    //==========================================================================
  }

  public Plot2D plot(Plot2D p, String name, Color c, boolean fitting) {
    if (fileCount == 0) {
      return null;
    }
    statistics();
    int size = JNDIArray.length;

    for (int x = 0; x < size; x++) {
      double JNDI = JNDIArray[x];
      if (fitting) {
        //========================================================================
        // 回歸
        //========================================================================
        double predict = regressAccept.getPredict(new double[] {JNDI})[0];
        p.addCacheScatterLinePlot(name + "o ave(fit)", JNDI, predict);
        double predict2 = regressUnaccept.getPredict(new double[] {JNDI})[0];
        p.addCacheScatterLinePlot(name + "x ave(fit)", JNDI, predict2);
        //========================================================================
      }
      else {
        p.addCacheScatterLinePlot(name + "o ave", c, JNDI, acceptAveArray[x]);
        p.addCacheScatterLinePlot(name + "x ave", c, JNDI, unacceptAveArray[x]);
      }
    }
    p.setAxeLabel(0, "JNDI");
    p.setAxeLabel(1, "delta JNDI\"");
    p.addLegend();
    return p;
  }

  public double[][] getAcceptPercentAndCoefs() {
    return getPercentAndCoefs(regressAcceptArray);
  }

  protected final static double[][] getPercentAndCoefs(PolynomialRegression[]
      regressArray) {
    double[][] result = new double[4][];
    for (int x = 0; x < 4; x++) {
      double[] coefs = regressArray[x].getCoefs()[0];
      int size = coefs.length;
      result[x] = new double[size + 1];
      int p = 25 * (x + 1);
      result[x][0] = p;
      System.arraycopy(coefs, 0, result[x], 1, size);
    }

    return result;
  }

  public double[][] getUnacceptPercentAndCoefs() {
    return getPercentAndCoefs(regressUnacceptArray);
  }

  public void printCoefs() {
    for (int x = 0; x < 4; x++) {
      int p = 25 * (x + 1);
      System.out.println("o " + p + "% " +
                         Polynomial.COEF_1.BY_1C.toString(regressAcceptArray[
          x].getCoefs()[0]));
    }
    for (int x = 0; x < 4; x++) {
      int p = 25 * (x + 1);
      System.out.println("x " + p + "% " +
                         Polynomial.COEF_1.BY_1C.toString(
                             regressUnacceptArray[x].getCoefs()[0]));
    }
  }

  public Plot2D plotMultiResult(String title) {
    if (fileCount == 0) {
      return null;
    }
    statistics();
    Plot2D p = Plot2D.getInstance(title);
    int size = JNDIArray.length;

    for (int x = 0; x < size; x++) {
      double JNDI = JNDIArray[x];

      double p25 = regressAcceptArray[0].getPredict(new double[] {JNDI})[0];
      p.addCacheScatterLinePlot("o 25%", Color.red, JNDI, p25);
      double p50 = regressAcceptArray[1].getPredict(new double[] {JNDI})[0];
      p.addCacheScatterLinePlot("o 50%", Color.red, JNDI, p50);
      double p75 = regressAcceptArray[2].getPredict(new double[] {JNDI})[0];
      p.addCacheScatterLinePlot("o 75%", Color.red, JNDI, p75);
      double p100 = regressAcceptArray[3].getPredict(new double[] {JNDI})[0];
      p.addCacheScatterLinePlot("o 100%", Color.red, JNDI, p100);

      double xp25 = regressUnacceptArray[0].getPredict(new double[] {JNDI})[0];
      p.addCacheScatterLinePlot("x 25%", Color.green, JNDI, xp25);
      double xp50 = regressUnacceptArray[1].getPredict(new double[] {JNDI})[0];
      p.addCacheScatterLinePlot("x 50%", Color.green, JNDI, xp50);
      double xp75 = regressUnacceptArray[2].getPredict(new double[] {JNDI})[0];
      p.addCacheScatterLinePlot("x 75%", Color.green, JNDI, xp75);
      double xp100 = regressUnacceptArray[3].getPredict(new double[] {JNDI})[0];
      p.addCacheScatterLinePlot("x 100%", Color.green, JNDI, xp100);
    }

    p.addLegend();
    p.drawCachePlot();
    p.setAxeLabel(0, "JNDI");
    p.setAxeLabel(1, "delta JNDI\"");
    p.setFixedBounds(0, 0, 500);
    p.setFixedBounds(1, 0, 3);
    p.setVisible();
    return p;

  }

  public Plot2D plotResult(String title, boolean plotDot) {
    if (fileCount == 0) {
      return null;
    }
    statistics();
    Plot2D p = Plot2D.getInstance(title);
    int size = JNDIArray.length;

    for (int x = 0; x < size; x++) {
      double JNDI = JNDIArray[x];

      p.addCacheScatterLinePlot("o ave", JNDI, acceptAveArray[x]);
      double predict = regressAccept.getPredict(new double[] {JNDI})[0];
      p.addCacheScatterLinePlot("o ave(fit)", JNDI, predict);
      p.addCacheBoxPlot("o std", JNDI, acceptAveArray[x], 2, acceptStdArray[x]);

      if (plotDot) {
        for (double val : this.acceptResultsArray[x]) {
          p.addCacheScatterPlot("o", JNDI, val);
        }

      }
      p.addCacheScatterLinePlot("x ave", JNDI, unacceptAveArray[x]);
      double predict2 = regressUnaccept.getPredict(new double[] {JNDI})[0];
      p.addCacheScatterLinePlot("x ave(fit)", JNDI, predict2);
      p.addCacheBoxPlot("x std", JNDI, unacceptAveArray[x], 2,
                        unacceptStdArray[x]);
      if (plotDot) {
        for (double val : this.unacceptResultsArray[x]) {
          p.addCacheScatterPlot("x", JNDI, val);
        }
      }
    }

    p.addLegend();
    p.drawCachePlot();
    p.setAxeLabel(0, "JNDI");
    p.setAxeLabel(1, "delta JNDI\"");
    p.setFixedBounds(0, 0, 500);
    p.setFixedBounds(1, 0, 3);
    p.setVisible();
    return p;
  }

  protected void multiStatistics(double[][] meanArray, double[][] stdArray,
                                 int size, double[] result, int index) {
    int p25Size = size / 4;
    int p50Size = p25Size * 2;
    int p75Size = p25Size * 3;

//    double[] p2 = Arrays.copyOfRange(result, 0, 2);
//    meanArray[0][index] = Maths.mean(p2);
    double[] p25 = Arrays.copyOfRange(result, 0, p25Size);
    meanArray[0][index] = result[p25Size];
    stdArray[0][index] = Maths.std(p25);

    double[] p50 = Arrays.copyOfRange(result, 0, p50Size);
//    meanArray[1][index] = Maths.mean(p50);
    meanArray[1][index] = result[p50Size];
    stdArray[1][index] = Maths.std(p50);

    double[] p75 = Arrays.copyOfRange(result, 0, p75Size);
//    meanArray[2][index] = Maths.mean(p75);
    meanArray[2][index] = result[p75Size];
    stdArray[2][index] = Maths.std(p75);

//    meanArray[3][index] = Maths.mean(result);
    meanArray[3][index] = result[size - 1];
    stdArray[3][index] = Maths.std(result);
  }

  /**
   * 對實驗數據進行統計
   */
  protected void statistics() {
    if (acceptAveArray == null) {
      int size = acceptResultsArray.length;
      acceptAveArray = new double[size];
      acceptStdArray = new double[size];
      unacceptAveArray = new double[size];
      unacceptStdArray = new double[size];
      multiAcceptAveArray = new double[4][size];
      multiAcceptStdArray = new double[4][size];
      multiUnacceptAveArray = new double[4][size];
      multiUnacceptStdArray = new double[4][size];
      int resultsize = acceptResultsArray[0].length;

      for (int x = 0; x < size; x++) {
        //======================================================================
        //排序
        Arrays.sort(acceptResultsArray[x]);
        double[] acceptResult = acceptResultsArray[x];

        acceptAveArray[x] = Maths.mean(acceptResult);
        acceptStdArray[x] = Maths.std(acceptResult);

        multiStatistics(multiAcceptAveArray, multiAcceptStdArray, resultsize,
                        acceptResult, x);

        //======================================================================
        //排序
        Arrays.sort(unacceptResultsArray[x]);
        double[] unacceptResult = unacceptResultsArray[x];
        unacceptAveArray[x] = Maths.mean(unacceptResult);
        unacceptStdArray[x] = Maths.std(unacceptResult);

        multiStatistics(multiUnacceptAveArray, multiUnacceptStdArray,
                        resultsize, unacceptResult, x);
        //======================================================================
      }

      //==========================================================================
      // fitting
      //==========================================================================

      //==========================================================================
      Polynomial.COEF_1 coef = Polynomial.COEF_1.BY_1C;
      regressAccept = new PolynomialRegression(JNDIArray,
                                               acceptAveArray, coef);
      regressAccept.regress();
//      System.out.println("o: " + coef.toString(regressAccept.getCoefs()[0]));
      //==========================================================================

      //==========================================================================
      Polynomial.COEF_1 coef2 = Polynomial.COEF_1.BY_1C;
      regressUnaccept = new PolynomialRegression(JNDIArray,
                                                 unacceptAveArray, coef2);
      regressUnaccept.regress();
//      System.out.println("x: " + coef.toString(regressUnaccept.getCoefs()[0]));
      //==========================================================================

      for (int x = 0; x < 4; x++) {
        regressAcceptArray[x] = new PolynomialRegression(JNDIArray,
            multiAcceptAveArray[x], coef);
        regressAcceptArray[x].regress();
        regressUnacceptArray[x] = new PolynomialRegression(JNDIArray,
            multiUnacceptAveArray[x], coef);
        regressUnacceptArray[x].regress();
      }

      //==========================================================================
    }
  }

  private PolynomialRegression[] regressAcceptArray = new PolynomialRegression[
      4];
  private PolynomialRegression[] regressUnacceptArray = new
      PolynomialRegression[4];

  private PolynomialRegression regressAccept;
  private PolynomialRegression regressUnaccept;

  public double[] getJNDIArray() {
    return JNDIArray;
  }

  public double[] getAcceptAveArray() {
    return acceptAveArray;
  }

  public PolynomialRegression getRegressUnaccept() {
    return regressUnaccept;
  }

  private double[] JNDIArray = null;
  private double[][] acceptResultsArray;
  private double[][] unacceptResultsArray;
  private double[] acceptAveArray;
  private double[] acceptStdArray;
  private double[][] multiAcceptAveArray;
  private double[][] multiAcceptStdArray;
  private double[] unacceptAveArray;
  private double[] unacceptStdArray;
  private double[][] multiUnacceptAveArray;
  private double[][] multiUnacceptStdArray;

}
