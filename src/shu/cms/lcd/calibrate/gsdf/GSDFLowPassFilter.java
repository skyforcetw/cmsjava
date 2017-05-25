package shu.cms.lcd.calibrate.gsdf;

import java.util.*;

import org.apache.commons.collections.primitives.*;
import shu.cms.hvs.*;
import shu.cms.hvs.gradient.*;
import shu.cms.lcd.calibrate.*;
import shu.cms.plot.*;
import shu.math.*;
import shu.math.array.*;
//import shu.plot.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 將JNDIArray作lowPass的filter處理, 且可以控制filter後在誤差內, 使filter之後既平順又
 * 不會與原始資料差太遠.
 *
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class GSDFLowPassFilter
    implements Plottable {

  public static void main(String[] args) {
    double[] input = DoubleArray.buildX(0, 255, 256);
    double[] output = GammaFinder.gammaCurve(input, input[input.length - 1], 2.2);
    output = DoubleArray.plus(output, 0.2);
    double[] jndi = GSDF.getJNDICurve(output, GSDF.DICOM);
    double[] jndi2 = DoubleArray.copy(jndi);
    Random random = new Random(1);
    for (int x = 0; x < jndi.length; x++) {
      jndi2[x] = jndi[x] + random.nextDouble() * 1;
    }

    GSDFLowPassFilter filter = new GSDFLowPassFilter();
//    filter.setDeltaThreshold(2);
//    double[] result = filter.lowPass(jndi2, 10);
    double[] result = filter.lowPass(jndi2, false, false, true);
    System.out.println("FilterTimes: " + filter.getFilterTimes());
    System.out.println("1FilterTimes: " + filter.getPart1FilterTimes());
    System.out.println("2FilterTimes: " + filter.getPart2FilterTimes());
    System.out.println("CoverRange: " + filter.getCoverRange());

    double[] jndip = Maths.firstOrderDerivatives(jndi);
    double[] jndi2p = Maths.firstOrderDerivatives(jndi2);
//    double[] lpp = Maths.firstOrderDerivatives(result);

//    FastCosineTransformer fct = new FastCosineTransformer();
//    double[] fctTransform = null;
//    double[] fctInverse = null;
//    double[] copyInverse = null;
//
//    try {
//      double[] source = new double[257];
//      System.arraycopy(jndi2p, 0, source, 0, jndi2p.length);
//      fctTransform = fct.transform2(source);
//      fctInverse = fct.inversetransform2(fctTransform);
//
//      double[] copy = DoubleArray.copy(fctTransform);
//      for (int x = 50; x < copy.length; x++) {
//        copy[x] = 0;
//      }
//      copyInverse = fct.inversetransform2(copy);
//
//      Plot2D plot = Plot2D.getInstance("dct");
//      plot.addLinePlot("fctTransform", 0, fctTransform.length - 1, fctTransform);
//      plot.addLinePlot("copy", 0, copy.length - 1, copy);
//      plot.setVisible();
//
//    }
//    catch (IllegalArgumentException ex) {
//      ex.printStackTrace();
//    }
//    catch (MathException ex) {
//      ex.printStackTrace();
//    }
//
//    Plot2D plot = Plot2D.getInstance();
//    plot.addLinePlot("jndi2'", 0, jndi2p.length - 1, jndi2p);
//    plot.addLinePlot("fctInverse", 0, fctInverse.length - 1, fctInverse);
//    plot.addLinePlot("copyInverse", 0, copyInverse.length - 1, copyInverse);
//    plot.setVisible();

  }

  /**
   * 驗證加速度的資料是否符合
   * @param originalData double[]
   * @return boolean
   */
  protected final static boolean checkPrimeData(double[] originalData) {
    return checkPrimeData(originalData, true, true);
  }

  /**
   * 驗證加速度的資料是否符合
   * @param originalData double[]
   * @param part1Check boolean 對part1做驗證
   * @param part2Check boolean 對part2做驗證
   * @return boolean
   */
  protected final static boolean checkPrimeData(double[] originalData,
                                                boolean part1Check,
                                                boolean part2Check) {
    //一次微分資訊
    double[] primeData = Maths.firstOrderDerivatives(originalData);
    int size = primeData.length;
    //找到加速度最大點, 以此分辨part1以及part2
    int maxIndex = Maths.maxIndex(primeData);

    if (maxIndex == 0) {
      return false;
    }

    if (part1Check && !checkPrimeData(originalData, 1, maxIndex, true)) {
      return false;
    }
    if (part2Check && !checkPrimeData(originalData, maxIndex, size, false)) {
      return false;
    }

    return true;
  }

  /**
   * 驗證prime時所能忍受的threshold大小
   */
  private static double PrimeCheckThreshold = 0.1;
  /**
   * 設定驗證prime時所能忍受的threshold大小
   * @param primeCheckThreshold double
   */
  public static void setPrimeCheckThreshold(double primeCheckThreshold) {
    PrimeCheckThreshold = primeCheckThreshold;
  }

  /**
   * 檢查加速度的資訊.
   * 在checkStart到checkEnd的區間, 檢查趨勢是否與rasing所指定為相符.
   *
   * @param originalData double[]
   * @param checkStart int
   * @param checkEnd int
   * @param rasing boolean 若為true則檢查是否為上升, 若為false則檢查是否為下降.
   * @return boolean
   */
  protected final static boolean checkPrimeData(double[] originalData,
                                                int checkStart, int checkEnd,
                                                boolean rasing) {
    return checkPrimeData(originalData, checkStart, checkEnd, rasing,
                          PrimeCheckThreshold);
  }

  /**
   * 檢查prime是否符合所需(rasing 或 faling), 並且在checkStart與checkEnd區間.
   * 如果檢查沒有rasing或faling時, 會再檢查其誤差是否超過threshold, 若沒超過還是認定符合所需.
   *
   * @param originalData double[]
   * @param checkStart int
   * @param checkEnd int
   * @param rasing boolean
   * @param threshold double
   * @return boolean
   */
  protected final static boolean checkPrimeData(double[] originalData,
                                                int checkStart, int checkEnd,
                                                boolean rasing,
                                                double threshold) {
    double[] primeData = Maths.firstOrderDerivatives(originalData);
    for (int x = checkStart; x < checkEnd; x++) {
      boolean verifyOK = rasing ? primeData[x] >= primeData[x - 1] :
          primeData[x] <= primeData[x - 1];
      if (!verifyOK) {
        if (threshold != -1 &&
            Math.abs(primeData[x] - primeData[x - 1]) < threshold) {
          continue;
        }
        else {
          return false;
        }
      }
    }
    return true;

  }

  /**
   * 要對jndiArray進行times的LP
   *
   * @param jndiArray double[] 要被LP的JNDI陣列
   * @param times int LP的次數
   * @return double[]
   */
  public double[] lowPass(double[] jndiArray, int times) {
    return lowPass0(jndiArray, times, times, false, CoverRange, false, false);
  }

  /**
   * 對jndiArray進行LP
   *
   * @param jndiArray double[] 要被LP的JNDI陣列
   * @param probeDelta boolean 探測delta
   * @param coverRange int 兩個part重疊的code
   * @return double[]
   */
  public double[] lowPass(double[] jndiArray, boolean probeDelta,
                          int coverRange) {
    return lowPass0(jndiArray, MaxFilterTimes, MaxFilterTimes, probeDelta,
                    coverRange, false, false);
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * 用來判斷是否符合資格的類別
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  protected static class Qualifier {
    /**
     * 要被用來判斷的個數
     */
    private int size;
    /**
     * 是否要針對prime作判斷
     */
    private boolean checkPrimeVerified = true;

    protected Qualifier(int size, boolean checkPrimeVerified) {
      this.size = size;
      this.checkPrimeVerified = checkPrimeVerified;
      deltaArray = new double[size];
      primeVerifiedArray = new boolean[size];
    }

    protected Qualifier(int size) {
      this(size, true);
    }

    /**
     * 誤差的陣列
     */
    private double[] deltaArray;
    /**
     * prime的陣列
     */
    private boolean[] primeVerifiedArray;

    /**
     * 取得誤差
     * @param index int
     * @return double
     */
    protected double getDelta(int index) {
      return deltaArray[index];
    }

    /**
     * 設定用來判斷的資訊
     * @param index int 索引值
     * @param delta double 誤差值
     * @param verified boolean 是否通過驗證
     */
    protected void setInfomation(int index, double delta, boolean verified) {
      deltaArray[index] = delta;
      primeVerifiedArray[index] = verified;
    }

    /**
     * 找到合格的index
     * 1.如果不檢查prime, 就是找delta最小的
     * 2.如果要檢查prime, 先驗證prime所有ok的, 再找這之中delta最小的
     * @return int
     */
    protected int getQualifiedIndex() {
      if (checkPrimeVerified) {
        int[] indexArray = getPrimeVerfiedOkIndexArray();
        if (indexArray.length == 0) {
          return Maths.minIndex(deltaArray);
        }
        double minDelta = Double.MAX_VALUE;
        int qualifiedIndex = -1;
        for (int index : indexArray) {
          double delta = deltaArray[index];
          if (delta < minDelta) {
            minDelta = delta;
            qualifiedIndex = index;
          }
        }
        return qualifiedIndex;
      }
      else {
        return Maths.minIndex(deltaArray);
      }
    }

    /**
     * 取得prime驗證ok的元素的索引值陣列
     * @return int[]
     */
    private int[] getPrimeVerfiedOkIndexArray() {
      IntList list = new ArrayIntList();
      for (int x = 0; x < size; x++) {
        if (primeVerifiedArray[x] == true) {
          list.add(x);
        }
      }
      return list.toArray();
    }
  }

  /**
   * 自動找到最佳的CoverRange下進行的LP
   * CoverRange的找法是以delta最小的.
   *
   * @param jndiArray double[] 要被LP的JNDI陣列
   * @param probeDelta boolean 探測delta
   * @param probeSmooth boolean 探測smooth
   * @param probePrime boolean 探測加速度
   * @return double[]
   */
  public double[] lowPass(double[] jndiArray, boolean probeDelta,
                          boolean probeSmooth, boolean probePrime) {
    return lowPass(jndiArray, probeDelta, probeSmooth, probePrime, true, true);
  }

  private void setFilterInfo(int index, LowPassInfo info) {
    int[] filterTimes = info.getFilterTimes(index);
    setFilterInfo(index, filterTimes[0], filterTimes[1], filterTimes[2]);
  }

  private void setFilterInfo(int coverRange, int filterTimes,
                             int part1FilterTimes, int part2FilterTimes) {
    this.coverRange = coverRange;
    this.filterTimes = filterTimes;
    this.part1FilterTimes = part1FilterTimes;
    this.part2FilterTimes = part2FilterTimes;
  }

  private static class LowPassInfo {
    double[][] resultArray;
    int[][] filterTimesArray;
    Qualifier qualifier;

    private int getQualifiedIndex() {
      return qualifier.getQualifiedIndex();
    }

    private LowPassInfo(int size, boolean probePrime) {
      resultArray = new double[size][];
      filterTimesArray = new int[size][];
      qualifier = new Qualifier(size, probePrime);
    }

    private double[] getResult(int index) {
      return resultArray[index];
    }

    private void setResult(int index, double[] result) {
      resultArray[index] = result;
    }

    private void setInfomation(int index, double delta, boolean verified) {
      qualifier.setInfomation(index, delta, verified);
    }

    private void setInfomation(int index, double delta, boolean verified,
                               int filterTimes,
                               int part1FilterTimes, int part2FilterTimes) {
      qualifier.setInfomation(index, delta, verified);
      setFilterTimes(index, filterTimes, part1FilterTimes, part2FilterTimes);
    }

    private int[] getFilterTimes(int index) {
      return filterTimesArray[index];
    }

    private void setFilterTimes(int index, int filterTimes,
                                int part1FilterTimes, int part2FilterTimes) {
      filterTimesArray[index] = new int[] {
          filterTimes, part1FilterTimes, part2FilterTimes};
    }

    private double getDelta(int index) {
      return qualifier.getDelta(index);
    }
  }

  /**
   * 自動找到最佳的CoverRange下進行的LP
   * CoverRange的找法是以delta最小的.
   *
   * @param jndiArray double[] 要被LP的JNDI陣列
   * @param probeDelta boolean 探測delta
   * @param probeSmooth boolean 探測smooth
   * @param probePrime boolean 探測加速度
   * @param part1LowPass boolean part1作LP
   * @param part2LowPass boolean part2作LP
   * @return double[]
   */
  public double[] lowPass(double[] jndiArray, boolean probeDelta,
                          boolean probeSmooth, boolean probePrime,
                          boolean part1LowPass, boolean part2LowPass) {
    //==========================================================================
    // 運算基本資訊
    //==========================================================================
    int maxIndex = getMaxAccelerationIndex(jndiArray);
    int maxCoverRange = maxIndex < MaxCoverRange ? maxIndex : MaxCoverRange;
    LowPassInfo info = new LowPassInfo(maxCoverRange, probePrime);
    int part1Times = part1LowPass ? MaxFilterTimes : 0;
    int part2Times = part2LowPass ? MaxFilterTimes : 0;
    //==========================================================================

    for (int range = 0; range < maxCoverRange; range++) {
      //lowPass動作
      double[] result = lowPass0(jndiArray, part1Times,
                                 part2Times, probeDelta, range, probeSmooth,
                                 probePrime);

      info.setResult(range, result);
      //計算原始以及lowpass之後的總誤差
      double delta = Maths.sum(getDeltaJNDI(jndiArray, result));
      boolean checkPrime = checkPrimeData(result);
      info.setInfomation(range, delta, checkPrime, this.filterTimes,
                         this.part1FilterTimes,
                         this.part2FilterTimes);
    }
    //找到delta最小的並決定之
    int index = info.getQualifiedIndex();
    setFilterInfo(index, info);

    double[] result = info.getResult(index);
    plot(jndiArray, result, info.getDelta(index));
    return result;
  }

  private void plot(double[] jndiArray, double[] result, double delta) {
    if (plotting) {
      Plot2D plot = Plot2D.getInstance("LowPass Filter");
      double[] orgp = Maths.firstOrderDerivatives(jndiArray);
      double[] resultp = Maths.firstOrderDerivatives(result);
      System.out.println("check: " + checkPrimeData(result));
      plot.addLinePlot("org'", 0, orgp.length - 1, orgp);
      plot.addLinePlot("LP'", 0, resultp.length - 1, resultp);
      plot.addLegend();
      plot.setVisible();
      GSDFGradientModel gm = new GSDFGradientModel(result, false);
      System.out.println("smooth: " + gm.isSmooth());
      System.out.println("FilterTimes: " + getFilterTimes());
      System.out.println("1FilterTimes: " + getPart1FilterTimes());
      System.out.println("2FilterTimes: " + getPart2FilterTimes());
      System.out.println("CoverRange: " + getCoverRange());
      System.out.println("TotalDelta: " + delta);
    }
  }

  private boolean plotting = true;

  /**
   * 找到最大加速度的index
   * @param jndiArray double[]
   * @return int
   */
  protected final static int getMaxAccelerationIndex(double[] jndiArray) {
    double[] gsdfp = Maths.firstOrderDerivatives(jndiArray);
    int maxIndex = Maths.maxIndex(gsdfp);
    return maxIndex;
  }

  /**
   * part1跟part2重疊的個數
   */
  private int coverRange = 0;
  private final static int MaxCoverRange = 20;
  /**
   * LP的總次數
   */
  private int filterTimes = 0;
  /**
   * part1 LP的次數
   */
  private int part1FilterTimes = 0;
  /**
   * part2 LP的次數
   */
  private int part2FilterTimes = 0;

  private double[] convole(double[] data, double[] kernel, int start, int end) {
    double[] result = Convolution.convole(data, kernel, start, end);
//    try {
//      Thread.sleep(16);
//    }
//    catch (InterruptedException ex) {
//    }
//    Plot2D p = Plot2D.getStaticInstance();
//    p.removeAllPlots();
//    p.addLinePlot("org", 0, 1, data);
//    p.addLinePlot("result", 0, 1, result);
//    p.setVisible();

    return result;
  }

  /**
   * low pass的核心程式
   *
   * @param jndiArray double[] 要被進行low pass的JNDI陣列
   * @param part1Times int part1要被LP的次數
   * @param part2Times int part2要被LP的次數
   * @param probeDelta boolean 探測delta
   * @param coverRange int part1跟part2重疊的個數
   * @param probeSmooth boolean 探測是否smooth
   * @param probePrime boolean 探測prime是否smooth
   * @return double[]
   */
  protected double[] lowPass0(final double[] jndiArray, final int part1Times,
                              final int part2Times, boolean probeDelta,
                              int coverRange, boolean probeSmooth,
                              boolean probePrime) {
    double[] gsdfp = Maths.firstOrderDerivatives(jndiArray);
    //找到加速度的轉折點
    int maxIndex = Maths.maxIndex(gsdfp);
    double[] lowPass = gsdfp;
    //filter次數歸零
    filterTimes = part1FilterTimes = part2FilterTimes = 0;

    //==========================================================================
    // 設定兩次low-pass的參數
    //==========================================================================
    int parameter0end = maxIndex + coverRange;
    int[][] parameters = new int[][] {
        new int[] {
        part1Times, 0, parameter0end <= 255 ? parameter0end : 255},
        new int[] {
        part2Times, maxIndex - coverRange, lowPass.length}
    };
    double[][] kernels = new double[][] {
        kernel1, kernel2};
    //==========================================================================

    int[] partFilterTimes = new int[2];
    //判斷加速度是rasing or faling
    boolean rasing = true;

    //==========================================================================
    // 對part1及part2作low-pass
    //==========================================================================
    for (int x = 0; x < 2; x++) {
      int[] parameter = parameters[x];
      double[] kernel = kernels[x];

      for (int y = 0; y < parameter[0]; y++) {
        int start = parameter[1];
        int end = parameter[2];
        double[] result = lowPass;
        result = convole(result, kernel, start, end);

        if (probeDelta && !probeDeltaThreshold(jndiArray, result)) {
          //如果delta超過threshold限制, 就停止
          break;
        }
        double[] lowPassGSDF = recoverGSDF(jndiArray, result);
        if (probeSmooth && probeSmooth(lowPassGSDF)) {
          //如果已經smooth, 就停止, 因為沒有繼續LP的必要了
          //可惜的是, 這邊的smooth是指區域的smooth, 整體的smooth還是無法判別
          break;
        }
        if (probePrime &&
            this.checkPrimeData(lowPassGSDF, start + 1, end, rasing)) {
          break;
        }

        filterTimes++;
        lowPass = result;
        partFilterTimes[x]++;
      }
      rasing = !rasing;
    }
    //==========================================================================

    part1FilterTimes = partFilterTimes[0];
    part2FilterTimes = partFilterTimes[1];

    double[] finalResult = recoverGSDF(jndiArray, lowPass);
    return finalResult;
  }

  /**
   * 探測delta是否超出threshold
   * @param jndiArray double[]
   * @param lowPassJNDIPrime double[]
   * @return boolean
   */
  protected boolean probeDeltaThreshold(double[] jndiArray,
                                        double[] lowPassJNDIPrime) {
    double[] lpJNDIArray = recoverGSDF(jndiArray, lowPassJNDIPrime);
    boolean under = isUnderDeltaThreshold(jndiArray, lpJNDIArray);
    return under;
  }

  /**
   * 探測是否smooth
   * @param lowPassJNDIPrime double[]
   * @return boolean
   */
  protected boolean probeSmooth(double[] lowPassJNDIPrime) {
    GSDFGradientModel gm = new GSDFGradientModel(lowPassJNDIPrime, false);
    return gm.isSmooth();
  }

  private final static int MaxFilterTimes = 1000;
  private final static int CoverRange = 1;

  /**
   * 計算jndiArray1與jndiArray2的delta是否超過threshold
   * @param jndiArray1 double[]
   * @param jndiArray2 double[]
   * @return boolean
   */
  protected boolean isUnderDeltaThreshold(double[] jndiArray1,
                                          double[] jndiArray2) {
    double[] delta = getDeltaJNDI(jndiArray1, jndiArray2);
    double maxDelta = Maths.max(delta);
    return maxDelta < deltaThreshold;
  }

  /**
   * LP時最大能接受的delta
   */
  private double deltaThreshold = 1;

  /**
   * 計算jndiArray1與jndiArray2之間的delta JNDI
   * @param jndiArray1 double[]
   * @param jndiArray2 double[]
   * @return double[]
   */
  protected final static double[] getDeltaJNDI(double[] jndiArray1,
                                               double[] jndiArray2) {
    double[] delta = DoubleArray.minus(jndiArray1, jndiArray2);
    DoubleArray.abs(delta);
    return delta;
  }

  /**
   * 從原始的GSDF以及GSDF的預測加速度, 還原出GSDF.
   * 借用GSDFPredicter.recoverGSDF.
   *
   * @param originalGSDF double[]
   * @param predcitGSDFPrime double[]
   * @return double[]
   */
  protected final static double[] recoverGSDF(double[] originalGSDF,
                                              double[] predcitGSDFPrime) {
    double[] rgsdf = GSDFPredicter.recoverGSDF(originalGSDF, predcitGSDFPrime);
    rgsdf = GSDFPredicter.adjustPredictDataAlign(originalGSDF, rgsdf);
    return rgsdf;
  }

  public void setKernel1(double[] kernel1) {
    this.kernel1 = kernel1;
  }

  public void setKernel2(double[] kernel2) {
    this.kernel2 = kernel2;
  }

  public void setDeltaThreshold(double deltaThreshold) {
    this.deltaThreshold = deltaThreshold;
  }

  public void setPlotting(boolean plotting) {
    this.plotting = plotting;
  }

  public int getFilterTimes() {
    return filterTimes;
  }

  public int getPart1FilterTimes() {
    return part1FilterTimes;
  }

  public int getPart2FilterTimes() {
    return part2FilterTimes;
  }

  public int getCoverRange() {
    return coverRange;
  }

  private double[] kernel1 = new double[] {
      1, 10, 1};
  private double[] kernel2 = new double[] {
      1, 2, 1};
}
